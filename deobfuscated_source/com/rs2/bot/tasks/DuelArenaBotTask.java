/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.tasks;

import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.duel.DuelArenaBotTickTask;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.gameplay.duel.DuelArenaLocationManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.util.GameUtil;
import com.rs2.util.RectangularArea;
import java.util.ArrayList;

public final class DuelArenaBotTask
extends BotTaskDefinition {
    public static final int STAKE_ITEM_ID = 995;
    public static final int FOOD_ITEM_ID = 379;
    public static final int BUILD_BALANCED_MAIN = 0;
    public static final int BUILD_STRENGTH_PURE = 1;
    public static final int BUILD_DEFENCE_TANK = 2;
    public static final int BUILD_LOW_LEVEL_PURE = 3;
    public static final int RISK_CAUTIOUS = 0;
    public static final int RISK_STANDARD = 1;
    public static final int RISK_GAMBLER = 2;
    public static final int BASE_MATCH_LEVEL_BAND = 6;
    public static final int MAX_UNDERDOG_EXTRA_LEVELS = 6;
    private static final Position duelArenaStartPosition = new Position(3366, 3267, 0);
    private static final BotRoute duelArenaTaskRoute = new BotRoute(new Position[]{duelArenaStartPosition});
    private static final RectangularArea[] duelArenaTaskAreas = new RectangularArea[]{new RectangularArea(3358, 3267, 3376, 3282), new RectangularArea(3360, 3258, 3380, 3266)};

    public DuelArenaBotTask(int n) {
        super(duelArenaStartPosition, duelArenaTaskRoute, 1, false, n);
        this.combatTask = true;
        this.usesCustomTaskAction = true;
        super.setTaskAreas(duelArenaTaskAreas);
    }

    @Override
    public final boolean meetsUnlockRequirements(Player player) {
        return player.getCombatLevel() >= 12;
    }

    @Override
    public final boolean isWithinProgressionRange(Player player) {
        return player.botMode == 7;
    }

    @Override
    public final ArrayList getRequiredItems(Player player) {
        return new ArrayList();
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        int buildType = DuelArenaBotTask.getBuildType(player);
        int attackLevel = 40;
        int defenceLevel = 40;
        int strengthLevel = 40;
        GameplayHelper.resetBotSkillsToBase(player);
        if (buildType == BUILD_STRENGTH_PURE) {
            int attackRoll = DuelArenaBotTask.getIdentityRoll(player, 3, 11);
            attackLevel = attackRoll == 0 ? 40 : (attackRoll == 1 ? 60 : 70);
            defenceLevel = DuelArenaBotTask.getIdentityRoll(player, 4, 12) == 0 ? 5 : 1;
            strengthLevel = 58 + DuelArenaBotTask.getIdentityRoll(player, 38, 13);
        } else if (buildType == BUILD_DEFENCE_TANK) {
            attackLevel = 35 + DuelArenaBotTask.getIdentityRoll(player, 24, 14);
            defenceLevel = 50 + DuelArenaBotTask.getIdentityRoll(player, 36, 15);
            strengthLevel = 35 + DuelArenaBotTask.getIdentityRoll(player, 30, 16);
        } else if (buildType == BUILD_LOW_LEVEL_PURE) {
            attackLevel = DuelArenaBotTask.getIdentityRoll(player, 2, 17) == 0 ? 30 : 40;
            defenceLevel = 1;
            strengthLevel = 48 + DuelArenaBotTask.getIdentityRoll(player, 28, 18);
        } else {
            int baseLevel = 35 + DuelArenaBotTask.getIdentityRoll(player, 36, 19);
            attackLevel = baseLevel + DuelArenaBotTask.getIdentityRoll(player, 8, 20) - 2;
            defenceLevel = Math.max(30, baseLevel - 4 + DuelArenaBotTask.getIdentityRoll(player, 12, 21));
            strengthLevel = baseLevel + DuelArenaBotTask.getIdentityRoll(player, 10, 22) - 1;
        }
        DuelArenaBotTask.setDuelSkillLevel(player, 0, attackLevel);
        DuelArenaBotTask.setDuelSkillLevel(player, 1, defenceLevel);
        DuelArenaBotTask.setDuelSkillLevel(player, 2, strengthLevel);
        DuelArenaBotTask.setDuelSkillLevel(player, 4, 1);
        DuelArenaBotTask.setDuelSkillLevel(player, 5, 1);
        DuelArenaBotTask.setDuelSkillLevel(player, 6, 1);
        DuelArenaBotTask.refreshDuelHitpoints(player);
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        DuelArenaBotTask.resetDuelCombatRuntimeState(player);
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        GameplayHelper.prepareBotCombatStyle(player, 0);
        int stakeAmount = DuelArenaBotTask.getStakeAmount(player);
        int reserveMultiplier = DuelArenaBotTask.getStakeReserveMultiplier(player);
        player.getInventoryManager().addItem(new ItemStack(STAKE_ITEM_ID, stakeAmount * reserveMultiplier));
        player.getInventoryManager().addItem(new ItemStack(FOOD_ITEM_ID, 10));
        player.botFoodItemId = FOOD_ITEM_ID;
        player.getBankContainer().addToTab(new ItemStack(STAKE_ITEM_ID, stakeAmount * (reserveMultiplier + 4)), 0);
        DuelArenaBotTask.resetDuelCombatRuntimeState(player);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void startCustomTaskAction(Player player) {
        if (player.isInDuelArenaLobby() && player.getPosition().getX() == duelArenaStartPosition.getX() && player.getPosition().getY() == duelArenaStartPosition.getY() && player.getPosition().getPlane() == duelArenaStartPosition.getPlane()) {
            player.moveTo(DuelArenaBotTask.getPreferredLobbyPosition(player));
        }
        boolean active = player.botDuelArenaTask != null && player.botDuelArenaTask.isActive();
        if (!active) {
            player.botDuelArenaTask = new DuelArenaBotTickTask(2, this, player);
            World.getTaskScheduler().schedule(player.botDuelArenaTask);
        }
    }

    public static Position getRandomLobbyPosition() {
        return DuelArenaLocationManager.randomExitPosition();
    }

    public static Position getPreferredLobbyPosition(Player player) {
        return DuelArenaLocationManager.randomLobbyPositionNear(DuelArenaLocationManager.lobbyAnchorPosition(player), 2);
    }

    public static Position getLobbyAnchorPosition(Player player) {
        return DuelArenaLocationManager.lobbyAnchorPosition(player);
    }

    public static int getStakeAmount(Player player) {
        int combatLevel = Math.max(12, player.getCombatLevel());
        int stakeTier = DuelArenaBotTask.getStakeTier(player);
        int riskProfile = DuelArenaBotTask.getRiskProfile(player);
        int buildType = DuelArenaBotTask.getBuildType(player);
        int amount = 2000 + combatLevel * (170 + stakeTier * 45);
        amount += DuelArenaBotTask.getIdentityRoll(player, 4000 + stakeTier * 2500, 61);
        if (buildType == BUILD_STRENGTH_PURE || buildType == BUILD_LOW_LEVEL_PURE) {
            amount += combatLevel * 90;
        } else if (buildType == BUILD_DEFENCE_TANK) {
            amount += combatLevel * 50;
        }
        if (stakeTier == 4) {
            amount += 10000 + combatLevel * 250;
        }
        if (riskProfile == RISK_CAUTIOUS) {
            amount = amount * 85 / 100;
        } else if (riskProfile == RISK_GAMBLER) {
            amount = amount * 125 / 100;
        }
        int maximum = stakeTier == 4 ? 150000 : (stakeTier == 3 ? 100000 : 75000);
        if (riskProfile == RISK_CAUTIOUS) {
            maximum = Math.min(maximum, 75000);
        }
        return DuelArenaBotTask.clamp(amount, 5000, maximum);
    }

    public static int getBuildType(Player player) {
        int roll = DuelArenaBotTask.getIdentityRoll(player, 100, 71);
        if (roll < 50) {
            return BUILD_BALANCED_MAIN;
        }
        if (roll < 75) {
            return BUILD_STRENGTH_PURE;
        }
        if (roll < 90) {
            return BUILD_DEFENCE_TANK;
        }
        return BUILD_LOW_LEVEL_PURE;
    }

    public static int getRiskProfile(Player player) {
        return DuelArenaBotTask.getIdentityRoll(player, 3, 72);
    }

    public static int getStakeTier(Player player) {
        return DuelArenaBotTask.getIdentityRoll(player, 5, 73);
    }

    public static int getStakeReserveMultiplier(Player player) {
        return 3 + DuelArenaBotTask.getIdentityRoll(player, 4, 74);
    }

    public static int getCombatLevelBand(Player player) {
        int band = BASE_MATCH_LEVEL_BAND + DuelArenaBotTask.getRiskProfile(player) * 2;
        int stakeTier = DuelArenaBotTask.getStakeTier(player);
        int buildType = DuelArenaBotTask.getBuildType(player);
        if (stakeTier >= 3) {
            --band;
        }
        if (buildType == BUILD_STRENGTH_PURE || buildType == BUILD_LOW_LEVEL_PURE) {
            ++band;
        } else if (buildType == BUILD_DEFENCE_TANK) {
            --band;
        }
        return DuelArenaBotTask.clamp(band, 4, 12);
    }

    public static boolean isCombatLevelMatchAcceptable(Player bot, Player opponent) {
        if (bot == null || opponent == null) {
            return false;
        }
        int levelDifference = opponent.getCombatLevel() - bot.getCombatLevel();
        int band = DuelArenaBotTask.getCombatLevelBand(bot);
        if (levelDifference > band) {
            return DuelArenaBotTask.willTakeUnderdogFight(bot, levelDifference);
        }
        return -levelDifference <= band;
    }

    public static boolean willTakeUnderdogFight(Player player, int levelDifference) {
        int band = DuelArenaBotTask.getCombatLevelBand(player);
        if (levelDifference <= band) {
            return true;
        }
        int riskProfile = DuelArenaBotTask.getRiskProfile(player);
        int extraLevels = riskProfile == RISK_GAMBLER ? MAX_UNDERDOG_EXTRA_LEVELS : (riskProfile == RISK_STANDARD ? 2 : 0);
        if (DuelArenaBotTask.getBuildType(player) == BUILD_STRENGTH_PURE) {
            ++extraLevels;
        }
        if (levelDifference > band + extraLevels) {
            return false;
        }
        int acceptChance = riskProfile == RISK_GAMBLER ? 65 : (riskProfile == RISK_STANDARD ? 20 : 0);
        if (DuelArenaBotTask.getStakeTier(player) >= 3) {
            acceptChance -= 10;
        }
        return DuelArenaBotTask.getIdentityRoll(player, 100, 90 + levelDifference) < acceptChance;
    }

    public static int getPersonalityRoll(Player player, int range) {
        return DuelArenaBotTask.getPersonalityRoll(player, range, 0);
    }

    public static int getPersonalityRoll(Player player, int range, int salt) {
        if (range <= 0) {
            return 0;
        }
        int hash = player.getUsername() == null ? player.getIndex() : player.getUsername().hashCode();
        hash ^= player.getCombatLevel() * 131;
        hash ^= salt * 1009;
        return (hash & 0x7FFFFFFF) % range;
    }

    private static int getIdentityRoll(Player player, int range, int salt) {
        if (range <= 0) {
            return 0;
        }
        int hash = player.getUsername() == null ? player.getIndex() : player.getUsername().hashCode();
        hash ^= salt * 1009;
        return (hash & 0x7FFFFFFF) % range;
    }

    private static void setDuelSkillLevel(Player player, int skillId, int level) {
        BotCombatHelper.setBotSkillLevel(player, skillId, DuelArenaBotTask.clamp(level, 1, 99));
    }

    private static void refreshDuelHitpoints(Player player) {
        player.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player);
        int[] currentLevels = player.getSkillManager().getCurrentLevels();
        player.getSkillManager();
        currentLevels[3] = SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]);
        player.getSkillManager().refreshAllSkills();
    }

    private static int clamp(int value, int minimum, int maximum) {
        if (value < minimum) {
            return minimum;
        }
        if (value > maximum) {
            return maximum;
        }
        return value;
    }

    private static void resetDuelCombatRuntimeState(Player player) {
        if (player.botCombatTickTask != null && player.botCombatTickTask.isActive()) {
            player.botCombatTickTask.stop();
        }
        if (player.botEscapeLogoutTask != null && player.botEscapeLogoutTask.isActive()) {
            player.botEscapeLogoutTask.stop();
        }
        player.botCombatTickTarget = null;
        player.botCombatState = null;
        player.botCombatEscapeActive = false;
        player.botTaskReturnToBankRequested = false;
        player.botFoodDepleted = false;
        player.botEatDelayTicks = 0;
        player.botThreatEscapeDelayTicks = 0;
        player.botWeaponSwapDelayTicks = 0;
        player.botMagicGearSwapDelayTicks = 0;
        player.setAutoRetaliate(true);
        CombatManager.stopCombat(player);
    }
}
