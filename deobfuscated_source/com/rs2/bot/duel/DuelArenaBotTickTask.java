/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.duel;

import com.rs2.bot.combat.BotPvpCombatHandler;
import com.rs2.bot.tasks.DuelArenaBotTask;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.gameplay.duel.DuelHistory;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.handler.PlayerInteractionPacketHandler;
import com.rs2.util.GameUtil;
import com.rs2.util.path.PathFinder;
import java.util.ArrayList;

public final class DuelArenaBotTickTask
extends TickTask {
    private static final DuelRule[] MELEE_STAKE_RULES = new DuelRule[]{DuelRule.NO_RANGED, DuelRule.NO_MAGIC, DuelRule.NO_SPECIAL_ATTACK, DuelRule.NO_PRAYER};
    private static final int[] MELEE_STAKE_RULE_BUTTONS = new int[]{6725, 6727, 7816, 6730};
    private static final String[] GENERAL_ADVERTISEMENTS = new String[]{"staking {stake}", "{stake} stake", "duel me {stake}", "stake me {stake}", "{stake} duel?", "anyone stake {stake}?", "staking cash", "duel anyone?"};
    private static final String[] RULE_ADVERTISEMENTS = new String[]{"melee only {stake}", "no range no mage", "no pray stake", "nr nm np stake", "melee stake no pray", "no mage stake", "melee duel?", "rules on"};
    private static final String[] LEVEL_ADVERTISEMENTS = new String[]{"lvl {level} stake", "same lvl stake?", "close lvl {stake}", "lvl {level} melee?", "near my lvl stake", "my lvl stake me"};
    private static final String[] LOBBY_ADVERTISEMENTS = new String[]{"stake or walk", "stop hiding", "u scared?", "easy cash here", "dont run", "clean me?", "free win?", "no noobs", "stake noob", "come duel", "fast stake", "rematch anyone?"};
    private static final String[] CAUTIOUS_ADVERTISEMENTS = new String[]{"same lvl only", "close lvls only", "fair stake {stake}", "dont scam rules"};
    private static final String[] GAMBLER_ADVERTISEMENTS = new String[]{"risking {stake}", "ill stake {stake}", "try me {stake}", "all in?", "double or nothing"};
    private static final int RESTOCK_FOOD_THRESHOLD = 4;
    private static final int PROACTIVE_CHALLENGE_DISTANCE = 7;
    private final DuelArenaBotTask taskDefinition;
    private final Player bot;
    private int advertiseCooldownTicks = 3;
    private int requestCooldownTicks = 0;
    private int challengeScanCooldownTicks = 0;
    private int lobbyMoveCooldownTicks = 0;
    private int pendingRequestTicks = 0;
    private int setupTicks = 0;
    private int confirmTicks = 0;

    public DuelArenaBotTickTask(int n, DuelArenaBotTask duelArenaBotTask, Player player) {
        super(2);
        this.taskDefinition = duelArenaBotTask;
        this.bot = player;
        this.advertiseCooldownTicks = 3 + DuelArenaBotTask.getPersonalityRoll(player, 8);
        this.challengeScanCooldownTicks = 2 + DuelArenaBotTask.getPersonalityRoll(player, 6);
        this.lobbyMoveCooldownTicks = 4 + DuelArenaBotTask.getPersonalityRoll(player, 10);
    }

    @Override
    public final void execute() {
        if (this.bot == null || !this.bot.isRegistered() || this.bot.currentBotTask != this.taskDefinition) {
            this.stop();
            return;
        }
        if (this.bot.isDead()) {
            return;
        }
        if (!"do task".equals(this.bot.botTaskState)) {
            this.recoverDedicatedDuelTaskState();
        }
        if (this.requestCooldownTicks > 0) {
            --this.requestCooldownTicks;
        }
        if (this.challengeScanCooldownTicks > 0) {
            --this.challengeScanCooldownTicks;
        }
        if (this.lobbyMoveCooldownTicks > 0) {
            --this.lobbyMoveCooldownTicks;
        }
        this.expireStaleOutgoingDuelRequest();
        if (this.bot.getDuelSession().isStarted()) {
            this.startDuelCombat();
            return;
        }
        if (this.bot.getDuelSession().getOpponent() != null) {
            this.processDuelInterface();
            return;
        }
        this.resetIdleDuelCombatState();
        this.setupTicks = 0;
        this.confirmTicks = 0;
        if (this.bot.getOpenInterfaceId() != 0) {
            this.bot.packetSender.closeInterfaces();
            return;
        }
        if (!this.bot.isInDuelArenaLobby()) {
            if (this.bot.isInDuelArena()) {
                this.bot.getDuelSession().moveToDuelArenaExit();
            } else {
                this.bot.moveTo(DuelArenaBotTask.getPreferredLobbyPosition(this.bot));
            }
            return;
        }
        if (this.bot.getInventoryManager().getItemAmount(DuelArenaBotTask.STAKE_ITEM_ID) < DuelArenaBotTask.getStakeAmount(this.bot) || this.bot.getInventoryManager().getItemAmount(DuelArenaBotTask.FOOD_ITEM_ID) < RESTOCK_FOOD_THRESHOLD || this.bot.botFoodDepleted) {
            this.taskDefinition.prepareTaskInventory(this.bot);
            this.bot.moveTo(DuelArenaBotTask.getPreferredLobbyPosition(this.bot));
            return;
        }
        Player requester = this.findRequester();
        if (requester != null) {
            this.sendDuelRequest(requester);
            return;
        }
        if (this.bot.getDuelRequestTarget() != null) {
            this.manageLobbySpacing();
            this.advertiseStake();
            return;
        }
        this.manageLobbySpacing();
        this.advertiseStake();
        if (!this.bot.isMoving() && this.requestCooldownTicks == 0 && this.challengeScanCooldownTicks == 0 && GameUtil.randomInt(5) == 0) {
            Player target = this.findChallengeTarget();
            this.challengeScanCooldownTicks = this.nextChallengeScanCooldown();
            if (target != null) {
                this.sendDuelRequest(target);
            }
        }
    }

    private void processDuelInterface() {
        Player opponent = this.bot.getDuelSession().getOpponent();
        if (!this.isValidDuelTarget(opponent)) {
            this.declineDuel(opponent);
            return;
        }
        if ("duel".equals(this.bot.interfaceAction)) {
            this.processSetupInterface(opponent);
            return;
        }
        if ("duel2".equals(this.bot.interfaceAction)) {
            this.processConfirmInterface(opponent);
        }
    }

    private void processSetupInterface(Player opponent) {
        ++this.setupTicks;
        if (this.setupTicks > 150) {
            this.declineDuel(opponent);
            return;
        }
        if (!this.ensureSupportedRules()) {
            this.declineDuel(opponent);
            return;
        }
        if (!this.isOpponentMatchupAcceptable(opponent)) {
            this.bot.queuePublicChatMessage(this.getMatchupDeclineMessage(opponent));
            this.declineDuel(opponent);
            return;
        }
        if (!this.ensureOwnStakePlaced()) {
            this.declineDuel(opponent);
            return;
        }
        if (!this.isOpponentStakeAcceptable(opponent)) {
            if (this.setupTicks % 12 == 0) {
                this.bot.queuePublicChatMessage("offer stake");
            }
            return;
        }
        if (!this.bot.getDuelController().isAccepted()) {
            this.bot.getDuelSession().handleButtonClick(6674);
        }
    }

    private void processConfirmInterface(Player opponent) {
        ++this.confirmTicks;
        if (this.confirmTicks > 75) {
            this.declineDuel(opponent);
            return;
        }
        if (!this.hasRequiredRules() || !this.isOpponentMatchupAcceptable(opponent) || !this.isOpponentStakeAcceptable(opponent) || this.getOwnStakeValue() <= 0) {
            this.declineDuel(opponent);
            return;
        }
        if (!this.bot.getDuelController().isAccepted()) {
            this.bot.getDuelSession().handleButtonClick(6520);
        }
    }

    private boolean ensureOwnStakePlaced() {
        if (this.getOwnStakeValue() > 0) {
            return true;
        }
        int stakeAmount = DuelArenaBotTask.getStakeAmount(this.bot);
        int slot = this.bot.getInventoryManager().getContainer().indexOfItem(DuelArenaBotTask.STAKE_ITEM_ID);
        if (slot == -1 || this.bot.getInventoryManager().getItemAmount(DuelArenaBotTask.STAKE_ITEM_ID) < stakeAmount) {
            return false;
        }
        this.bot.getDuelSession().addStakeItem(new ItemStack(DuelArenaBotTask.STAKE_ITEM_ID, stakeAmount), slot);
        return this.getOwnStakeValue() > 0;
    }

    private boolean ensureSupportedRules() {
        if (DuelRule.NO_MELEE.isEnabledFor(this.bot) || DuelRule.NO_MOVEMENT.isEnabledFor(this.bot)) {
            return false;
        }
        int index = 0;
        while (index < MELEE_STAKE_RULES.length) {
            if (!MELEE_STAKE_RULES[index].isEnabledFor(this.bot)) {
                this.bot.getDuelSession().handleButtonClick(MELEE_STAKE_RULE_BUTTONS[index]);
            }
            ++index;
        }
        return this.hasRequiredRules();
    }

    private boolean hasRequiredRules() {
        if (DuelRule.NO_MELEE.isEnabledFor(this.bot) || DuelRule.NO_MOVEMENT.isEnabledFor(this.bot)) {
            return false;
        }
        DuelRule[] duelRuleArray = MELEE_STAKE_RULES;
        int n = duelRuleArray.length;
        int n2 = 0;
        while (n2 < n) {
            if (!duelRuleArray[n2].isEnabledFor(this.bot)) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    private boolean isOpponentStakeAcceptable(Player opponent) {
        long ownStakeValue = this.getOwnStakeValue();
        long opponentStakeValue = this.getStakeValue(opponent);
        if (ownStakeValue <= 0 || opponentStakeValue <= 0) {
            return false;
        }
        return opponentStakeValue * 100 >= ownStakeValue * this.getMinimumOpponentStakeValuePercent();
    }

    private int getMinimumOpponentStakeValuePercent() {
        int minimumPercent = 55 + DuelArenaBotTask.getStakeTier(this.bot) * 8;
        int riskProfile = DuelArenaBotTask.getRiskProfile(this.bot);
        if (riskProfile == DuelArenaBotTask.RISK_CAUTIOUS) {
            minimumPercent += 15;
        } else if (riskProfile == DuelArenaBotTask.RISK_GAMBLER) {
            minimumPercent -= 15;
        }
        if (this.getOwnStakeValue() > 75000L) {
            minimumPercent += 10;
        }
        return Math.max(35, Math.min(110, minimumPercent));
    }

    private long getOwnStakeValue() {
        return this.getStakeValue(this.bot);
    }

    private long getStakeValue(Player player) {
        if (player == null || player.getDuelSession() == null) {
            return 0;
        }
        long value = 0L;
        ArrayList stakedItems = player.getDuelSession().getStakedItems();
        int index = 0;
        while (index < stakedItems.size()) {
            ItemStack itemStack = (ItemStack)stakedItems.get(index);
            if (itemStack != null && itemStack.isValid()) {
                if (itemStack.getId() == DuelArenaBotTask.STAKE_ITEM_ID) {
                    value += itemStack.getAmount();
                } else {
                    int itemValue = ItemDefinition.forId(itemStack.getId()).getValue();
                    if (itemValue <= 0) {
                        itemValue = 1;
                    }
                    value += itemValue * itemStack.getAmount();
                }
            }
            ++index;
        }
        return value;
    }

    private void startDuelCombat() {
        Player opponent = this.bot.getDuelSession().getOpponent();
        if (opponent == null || opponent.isDead() || !opponent.isRegistered()) {
            return;
        }
        this.prepareDuelCombatDriver(this.bot, opponent);
        if (opponent.botEnabled) {
            this.prepareDuelCombatDriver(opponent, this.bot);
        }
        if (this.bot.getCombatTarget() != opponent) {
            CombatManager.startCombat(this.bot, opponent);
        }
        BotPvpCombatHandler.startBotPvpCombatTicks(this.bot, opponent);
    }

    private void recoverDedicatedDuelTaskState() {
        this.bot.botTaskState = "do task";
        this.bot.currentBotRoute = null;
        this.bot.botPathWaypointIndex = 0;
        this.bot.botTaskReturnToBankRequested = false;
    }

    private void resetIdleDuelCombatState() {
        if (this.bot.getDuelSession().isStarted() || this.bot.getDuelSession().getOpponent() != null || this.bot.isInDuelArena()) {
            return;
        }
        if (this.bot.botCombatTickTask != null && this.bot.botCombatTickTask.isActive()) {
            this.bot.botCombatTickTask.stop();
        }
        if (this.bot.botEscapeLogoutTask != null && this.bot.botEscapeLogoutTask.isActive()) {
            this.bot.botEscapeLogoutTask.stop();
        }
        this.bot.botCombatTickTarget = null;
        this.bot.botCombatState = null;
        this.bot.botCombatEscapeActive = false;
        this.bot.botTaskReturnToBankRequested = false;
        this.bot.botEatDelayTicks = 0;
        this.bot.botThreatEscapeDelayTicks = 0;
        this.bot.setAutoRetaliate(true);
        CombatManager.stopCombat(this.bot);
    }

    private void prepareDuelCombatDriver(Player player, Player opponent) {
        if (player == null || !player.botEnabled) {
            return;
        }
        if (player.botEscapeLogoutTask != null && player.botEscapeLogoutTask.isActive()) {
            player.botEscapeLogoutTask.stop();
        }
        if (player.botCombatTickTask != null && player.botCombatTickTask.isActive() && player.botCombatTickTarget != opponent) {
            player.botCombatTickTask.stop();
            player.botCombatTickTask = null;
            player.botCombatTickTarget = null;
        }
        player.botCombatState = null;
        player.botCombatEscapeActive = false;
        player.botTaskReturnToBankRequested = false;
        player.setAutoRetaliate(true);
    }

    private void manageLobbySpacing() {
        if (this.lobbyMoveCooldownTicks > 0 || this.bot.isMoving() || this.bot.getInteractionTarget() != null) {
            return;
        }
        Position anchor = DuelArenaBotTask.getLobbyAnchorPosition(this.bot);
        int anchorDistance = GameUtil.getDistance(this.bot.getPosition(), anchor);
        boolean crowded = this.countNearbyIdleDuelBots(1) > 0 || this.countNearbyIdleDuelBots(2) >= 3;
        if (!crowded && anchorDistance <= 4 && GameUtil.randomInt(8) != 0) {
            this.lobbyMoveCooldownTicks = this.nextLobbyMoveCooldown();
            return;
        }
        Position destination = DuelArenaBotTask.getPreferredLobbyPosition(this.bot);
        this.walkToLobbyPosition(destination);
        this.lobbyMoveCooldownTicks = this.nextLobbyMoveCooldown();
    }

    private void walkToLobbyPosition(Position destination) {
        if (destination == null || this.isAtPosition(destination) || this.bot.isMovementLocked()) {
            return;
        }
        this.bot.getMovementQueue().setRunning(false);
        PathFinder.getInstance();
        PathFinder.findPath(this.bot, destination.getX(), destination.getY(), true, 0, 0);
        this.bot.getMovementQueue().clearMovementActions();
    }

    private boolean isAtPosition(Position position) {
        return this.bot.getPosition().getX() == position.getX() && this.bot.getPosition().getY() == position.getY() && this.bot.getPosition().getPlane() == position.getPlane();
    }

    private int countNearbyIdleDuelBots(int distance) {
        int count = 0;
        Player[] players = World.getPlayers();
        int index = 0;
        while (index < players.length) {
            Player candidate = players[index];
            if (candidate != null && candidate != this.bot && candidate.botEnabled && candidate.currentBotTask instanceof DuelArenaBotTask && candidate.isInDuelArenaLobby() && candidate.getDuelSession().getOpponent() == null && candidate.getOpenInterfaceId() == 0 && GameUtil.getDistance(this.bot.getPosition(), candidate.getPosition()) <= distance) {
                ++count;
            }
            ++index;
        }
        return count;
    }

    private int nextLobbyMoveCooldown() {
        return 10 + GameUtil.randomInt(16) + DuelArenaBotTask.getPersonalityRoll(this.bot, 6);
    }

    private int nextChallengeScanCooldown() {
        return 4 + GameUtil.randomInt(8) + DuelArenaBotTask.getPersonalityRoll(this.bot, 5);
    }

    private void advertiseStake() {
        if (this.advertiseCooldownTicks > 0) {
            --this.advertiseCooldownTicks;
            return;
        }
        this.bot.queuePublicChatMessage(this.buildAdvertisement());
        this.advertiseCooldownTicks = 12 + GameUtil.randomInt(12);
    }

    private String buildAdvertisement() {
        int roll = GameUtil.randomInt(12);
        if (roll < 3) {
            return this.formatAdvertisement(GENERAL_ADVERTISEMENTS[GameUtil.randomInt(GENERAL_ADVERTISEMENTS.length)]);
        }
        if (roll < 5) {
            return this.formatAdvertisement(RULE_ADVERTISEMENTS[GameUtil.randomInt(RULE_ADVERTISEMENTS.length)]);
        }
        if (roll < 7) {
            return this.formatAdvertisement(LEVEL_ADVERTISEMENTS[GameUtil.randomInt(LEVEL_ADVERTISEMENTS.length)]);
        }
        if (roll == 7) {
            int riskProfile = DuelArenaBotTask.getRiskProfile(this.bot);
            if (riskProfile == DuelArenaBotTask.RISK_CAUTIOUS) {
                return this.formatAdvertisement(CAUTIOUS_ADVERTISEMENTS[GameUtil.randomInt(CAUTIOUS_ADVERTISEMENTS.length)]);
            }
            if (riskProfile == DuelArenaBotTask.RISK_GAMBLER) {
                return this.formatAdvertisement(GAMBLER_ADVERTISEMENTS[GameUtil.randomInt(GAMBLER_ADVERTISEMENTS.length)]);
            }
        }
        return this.formatAdvertisement(LOBBY_ADVERTISEMENTS[GameUtil.randomInt(LOBBY_ADVERTISEMENTS.length)]);
    }

    private String formatAdvertisement(String advertisement) {
        return advertisement.replace("{stake}", this.getFormattedStakeAmount()).replace("{level}", String.valueOf(this.bot.getCombatLevel()));
    }

    private String getFormattedStakeAmount() {
        int stakeAmount = DuelArenaBotTask.getStakeAmount(this.bot);
        if (stakeAmount >= 1000000) {
            return String.valueOf((stakeAmount + 500000) / 1000000) + "m";
        }
        if (stakeAmount >= 1000) {
            return String.valueOf(Math.max(1, (stakeAmount + 500) / 1000)) + "k";
        }
        return String.valueOf(stakeAmount);
    }

    private Player findRequester() {
        Player[] players = World.getPlayers();
        int index = 0;
        while (index < players.length) {
            Player candidate = players[index];
            if (candidate != null && candidate != this.bot && candidate.getDuelRequestTarget() == this.bot && this.isValidLobbyTarget(candidate) && this.isOpponentMatchupAcceptable(candidate)) {
                return candidate;
            }
            ++index;
        }
        return null;
    }

    private Player findChallengeTarget() {
        Player[] players = World.getPlayers();
        ArrayList botCandidates = new ArrayList();
        ArrayList playerCandidates = new ArrayList();
        int index = 0;
        while (index < players.length) {
            Player candidate = players[index];
            if (candidate != null && candidate != this.bot && this.isValidProactiveChallengeTarget(candidate)) {
                if (candidate.botEnabled && candidate.currentBotTask instanceof DuelArenaBotTask) {
                    botCandidates.add(candidate);
                } else {
                    playerCandidates.add(candidate);
                }
            }
            ++index;
        }
        if (botCandidates.size() > 0) {
            return (Player)botCandidates.get(GameUtil.randomInt(botCandidates.size()));
        }
        if (playerCandidates.size() > 0) {
            return (Player)playerCandidates.get(GameUtil.randomInt(playerCandidates.size()));
        }
        return null;
    }

    private boolean isValidProactiveChallengeTarget(Player player) {
        if (!this.isValidLobbyTarget(player)) {
            return false;
        }
        if (GameUtil.getDistance(this.bot.getPosition(), player.getPosition()) > PROACTIVE_CHALLENGE_DISTANCE) {
            return false;
        }
        if (!this.isOpponentMatchupAcceptable(player)) {
            return false;
        }
        if (!this.canSendDuelRequestTo(player)) {
            return false;
        }
        if (player.botEnabled && player.currentBotTask instanceof DuelArenaBotTask && (player.isMoving() || player.getInteractionTarget() != null || player.getDuelRequestTarget() != null)) {
            return false;
        }
        return true;
    }

    private boolean isOpponentMatchupAcceptable(Player opponent) {
        if (!this.isValidDuelTarget(opponent)) {
            return false;
        }
        if (DuelHistory.shouldAvoidRepeatFarm(this.bot, opponent)) {
            return false;
        }
        return DuelArenaBotTask.isCombatLevelMatchAcceptable(this.bot, opponent);
    }

    private String getMatchupDeclineMessage(Player opponent) {
        if (DuelHistory.shouldAvoidRepeatFarm(this.bot, opponent)) {
            return "nty rematch";
        }
        if (opponent != null && opponent.getCombatLevel() > this.bot.getCombatLevel()) {
            return "nty too high";
        }
        return "nty levels";
    }

    private boolean isValidLobbyTarget(Player player) {
        if (!this.isValidDuelTarget(player)) {
            return false;
        }
        if (!player.isInDuelArenaLobby() || player.isInDuelArena()) {
            return false;
        }
        if (player.getOpenInterfaceId() > 0 || player.getDuelSession().getOpponent() != null || player.getDuelSession().isStarted()) {
            return false;
        }
        return GameUtil.isWithinDistance(this.bot.getPosition(), player.getPosition(), 15);
    }

    private boolean isValidDuelTarget(Player player) {
        return player != null && player != this.bot && !player.isDead() && player.isRegistered();
    }

    private void sendDuelRequest(Player target) {
        if (this.requestCooldownTicks > 0) {
            return;
        }
        if (!this.canSendDuelRequestTo(target)) {
            this.requestCooldownTicks = Math.max(this.requestCooldownTicks, 3 + GameUtil.randomInt(4));
            return;
        }
        PlayerInteractionPacketHandler.dispatchDeferredDuelRequest(this.bot, target);
        this.requestCooldownTicks = 8 + GameUtil.randomInt(8);
        this.pendingRequestTicks = 0;
    }

    private boolean canSendDuelRequestTo(Player target) {
        if (!this.isValidLobbyTarget(target)) {
            return false;
        }
        if (this.bot.getOpenInterfaceId() > 0 || this.bot.getDuelSession().getOpponent() != null || this.bot.getDuelSession().isStarted()) {
            return false;
        }
        Player currentRequestTarget = this.bot.getDuelRequestTarget();
        Player targetRequestTarget = target.getDuelRequestTarget();
        if (currentRequestTarget != null && currentRequestTarget != target && targetRequestTarget != this.bot) {
            return false;
        }
        if (targetRequestTarget != null && targetRequestTarget != this.bot) {
            return false;
        }
        return true;
    }

    private void expireStaleOutgoingDuelRequest() {
        Player requestTarget = this.bot.getDuelRequestTarget();
        if (requestTarget == null || this.bot.getDuelSession().getOpponent() != null || this.bot.getDuelSession().isStarted()) {
            this.pendingRequestTicks = 0;
            return;
        }
        ++this.pendingRequestTicks;
        if (this.pendingRequestTicks <= 18 && this.isValidPendingRequestTarget(requestTarget)) {
            return;
        }
        this.bot.setDuelRequestTarget(null);
        this.bot.setInteractionTarget(null);
        this.bot.getMovementQueue().clearMovementActions();
        this.pendingRequestTicks = 0;
        this.requestCooldownTicks = Math.max(this.requestCooldownTicks, 3 + GameUtil.randomInt(4));
    }

    private boolean isValidPendingRequestTarget(Player requestTarget) {
        if (!this.isValidDuelTarget(requestTarget)) {
            return false;
        }
        if (!requestTarget.isInDuelArenaLobby() || requestTarget.isInDuelArena()) {
            return false;
        }
        if (requestTarget.getOpenInterfaceId() > 0 || requestTarget.getDuelSession().getOpponent() != null || requestTarget.getDuelSession().isStarted()) {
            return false;
        }
        return requestTarget.getDuelRequestTarget() == null || requestTarget.getDuelRequestTarget() == this.bot;
    }

    private void declineDuel(Player opponent) {
        if (opponent != null && opponent.getDuelSession() != null && opponent.getDuelSession().getOpponent() == this.bot) {
            opponent.getDuelController().resetDuel(true);
        }
        if (this.bot.getDuelSession().getOpponent() != null) {
            this.bot.getDuelController().resetDuel(true);
        }
        this.setupTicks = 0;
        this.confirmTicks = 0;
    }
}
