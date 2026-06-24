/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.combat;

import com.rs2.bot.combat.BotCombatEscapeHandler;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.combat.BotCombatLoadoutManager;
import com.rs2.model.Entity;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.consumable.FoodDefinition;
import com.rs2.model.path.PathReachability;
import com.rs2.model.player.Player;
import com.rs2.model.player.PlayerGroup;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.path.PathFinder;
import java.util.ArrayList;
import java.util.Collections;

public final class BotPvpTargetSearchTickTask
extends TickTask {
    private final /* synthetic */ Player bot;
    private final /* synthetic */ boolean ignoreLootRiskCheck;

    public BotPvpTargetSearchTickTask(int n, Player player, boolean bl) {
        super(10);
        this.bot = player;
        this.ignoreLootRiskCheck = bl;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public final void execute() {
        block74: {
            int var3_12 = 0;
            int n;
            int n2;
            Object object;
            if (this.bot.isDead() || !this.bot.isRegistered()) {
                this.stop();
                return;
            }
            if (this.bot.botCombatEscapeActive) {
                if (this.bot.getPosition().equals(this.bot.botEscapeLastPosition) && this.bot.getRecentCombatTimer().hasElapsed()) {
                    ++this.bot.botEscapeStuckTicks;
                } else {
                    this.bot.botEscapeLastPosition = this.bot.getPosition().copy();
                    this.bot.botEscapeStuckTicks = 0;
                }
                if (this.bot.botEscapeStuckTicks >= 10) {
                    BotCombatLoadoutManager.startCombatLoadoutBot(this.bot);
                }
            }
            if (BotCombatHelper.hasExternalCombatTarget(this.bot) || this.bot.botCombatState != null || this.bot.botCombatEscapeActive) break block74;
            if (!this.bot.isInWilderness()) {
                BotCombatHelper.prepareBotPvpSearchPosition(this.bot);
            }
            if (this.bot.getEquipmentManager().getItemIdAtSlot(3) != this.bot.botWeaponItemId) {
                this.bot.getEquipmentManager().equipFromInventorySlot(this.bot.getInventoryManager().getContainer().indexOfItem(this.bot.botWeaponItemId));
                if (this.bot.botShieldItemId != 0 && this.bot.getEquipmentManager().getItemIdAtSlot(5) != this.bot.botShieldItemId) {
                    this.bot.getEquipmentManager().equipFromInventorySlot(this.bot.getInventoryManager().getContainer().indexOfItem(this.bot.botShieldItemId));
                }
                this.bot.botActiveCombatStyle = this.bot.botPrimaryCombatStyle;
            }
            if (this.bot.botMagicPenaltyGearUnequipped) {
                BotCombatHelper.reequipMagicPenaltyGear(this.bot);
            }
            if (this.bot.getMovementQueue().isRunning()) {
                if (this.bot.currentGroup == null) {
                    this.bot.getMovementQueue().setRunning(false);
                } else if (this.bot.currentGroup.leader == this.bot) {
                    this.bot.getMovementQueue().setRunning(false);
                }
            }
            BotCombatHelper.disableBotCombatPrayers(this.bot);
            Object object2 = this.bot;
            if (((Player)object2).botPvpChatSource != null && ((Player)object2).botPvpChatMessage != null) {
                if (((Player)object2).currentGroup == null && ((Player)object2).botPvpPendingTeamTarget != null && ((Player)object2).botPvpPendingTeamTarget == ((Player)object2).botPvpChatSource) {
                    if (((Player)object2).botPvpChatMessage.toLowerCase().equals("sure") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("okay") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("ok") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("k")) {
                        ((Player)object2).botPvpPendingTeamTarget = null;
                        ((Player)object2).botPvpTeamInviteTicks = 0;
                        if (((Player)object2).botPvpChatSource.currentGroup == null) {
                            object = new PlayerGroup(((Player)object2).botPvpChatSource, (Player)object2);
                            ((PlayerGroup)object).refreshGroupFollowChain();
                        } else {
                            ((Player)object2).botPvpChatSource.currentGroup.addMember((Player)object2);
                            ((Player)object2).botPvpChatSource.currentGroup.refreshGroupFollowChain();
                        }
                    } else if (((Player)object2).botPvpChatMessage.toLowerCase().equals("nty") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("nah") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("no") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("nope") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("no thanks")) {
                        ((Player)object2).botPvpPendingTeamTarget.botPvpTeamRequesters.remove(object2);
                        EntityTargetMovement.clearMovementTarget((Entity)object2);
                        ((Player)object2).botPvpRejectedTeamTargets.add(((Player)object2).botPvpPendingTeamTarget);
                        ((Player)object2).botPvpPendingTeamTarget = null;
                        ((Player)object2).botPvpTeamInviteTicks = 0;
                    }
                }
                if (((Player)object2).botPvpChatMessage.toLowerCase().equals("team") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("team?")) {
                    if (((Player)object2).currentGroup == null && BotCombatHelper.canTeamWithBotPvpPlayer((Player)object2, ((Player)object2).botPvpChatSource, false)) {
                        String[] responses = new String[]{"sure", "okay", "ok", "k"};
                        ((Player)object2).queuePublicChatMessage(responses[GameUtil.randomInt(4)]);
                        ((Player)object2).botPvpChatSource.botPvpTeamRequesters.add(object2);
                        if (((Player)object2).botPvpChatSource.currentGroup == null) {
                            PlayerGroup playerGroup = new PlayerGroup(((Player)object2).botPvpChatSource, (Player)object2);
                            playerGroup.refreshGroupFollowChain();
                        } else {
                            ((Player)object2).botPvpChatSource.currentGroup.addMember((Player)object2);
                            ((Player)object2).botPvpChatSource.currentGroup.refreshGroupFollowChain();
                        }
                    } else {
                        ((Player)object2).queuePublicChatMessage("nty");
                    }
                } else if (((Player)object2).currentGroup != null && ((Player)object2).botPvpChatSource.currentGroup != null && ((Player)object2).currentGroup == ((Player)object2).botPvpChatSource.currentGroup) {
                    if (((Player)object2).botPvpChatMessage.toLowerCase().equals("food") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food?") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food left") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food left?")) {
                        object = ItemDefinition.forId(((Player)object2).botFoodItemId);
                        String string = ((ItemDefinition)object).getDisplayName();
                        n2 = ((Player)object2).getInventoryManager().getItemAmount(((Player)object2).botFoodItemId);
                        String foodName = string;
                        if (n2 > 1) {
                            foodName = String.valueOf(string) + "s";
                        }
                        ((Player)object2).queuePublicChatMessage("I have " + n2 + " " + foodName + " left");
                    } else if (((Player)object2).botPvpChatMessage.toLowerCase().equals("skills") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("skills?") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("lvls") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("lvls?") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("stats") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("stats?")) {
                        int n3;
                        int n4;
                        int n5;
                        int n6;
                        object = "";
                        int player = ((Player)object2).getSkillManager().getBaseLevel(0);
                        if (player > 1) {
                            object = String.valueOf(object) + "atk: " + player + " ";
                        }
                        if ((n2 = ((Player)object2).getSkillManager().getBaseLevel(1)) > 1) {
                            object = String.valueOf(object) + "def: " + n2 + " ";
                        }
                        if ((n6 = ((Player)object2).getSkillManager().getBaseLevel(2)) > 1) {
                            object = String.valueOf(object) + "str: " + n6 + " ";
                        }
                        if ((n = ((Player)object2).getSkillManager().getBaseLevel(6)) > 1) {
                            object = String.valueOf(object) + "mage: " + n + " ";
                        }
                        if ((n5 = ((Player)object2).getSkillManager().getBaseLevel(4)) > 1) {
                            object = String.valueOf(object) + "range: " + n5 + " ";
                        }
                        if ((n4 = ((Player)object2).getSkillManager().getBaseLevel(3)) > 10) {
                            object = String.valueOf(object) + "hp: " + n4 + " ";
                        }
                        if ((n3 = ((Player)object2).getSkillManager().getBaseLevel(5)) > 1) {
                            object = String.valueOf(object) + "pray: " + n3 + " ";
                        }
                        if (player == 1 && n2 == 1 && n6 == 1 && n == 1 && n5 == 1 && n4 == 10 && n3 == 1) {
                            object = "lol, i havent trained my skills at all";
                        }
                        ((Player)object2).queuePublicChatMessage((String)object);
                    } else if (((Player)object2).botPvpChatMessage.toLowerCase().equals("leave") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("g2g") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("bye") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("cya")) {
                        ((Player)object2).queuePublicChatMessage("cya");
                        EntityTargetMovement.clearMovementTarget((Entity)object2);
                        ((Player)object2).botPvpRejectedTeamTargets.add(((Player)object2).botPvpChatSource);
                        ((Player)object2).currentGroup.removeMember(((Player)object2).botPvpChatSource);
                    }
                }
                ((Player)object2).botPvpChatMessage = null;
                ((Player)object2).botPvpChatSource = null;
            }
            this.bot.botMagicGearSwapDelayTicks = 0;
            this.bot.botThreatEscapeDelayTicks = 0;
            this.bot.botPrayerSwitchDelayTicks = 0;
            this.bot.botQueuedPrayerId = -1;
            this.bot.botEatDelayTicks = 0;
            this.bot.botWeaponSwapDelayTicks = 0;
            if (!(this.bot.getCombatTarget() == null || this.bot.getCombatTarget().isDead() || this.bot.botCombatTickTask != null && this.bot.botCombatTickTask.isActive())) {
                this.bot.getMovementQueue().setRunning(true);
                CombatManager.startCombat(this.bot, this.bot.getCombatTarget());
            }
            object2 = FoodDefinition.forItemId(this.bot.botFoodItemId);
            int n8 = ((FoodDefinition)((Object)object2)).getHealAmount();
            int n7 = this.bot.getSkillManager().getCurrentLevels()[3] + n8;
            this.bot.getSkillManager();
            int n9 = n8 = n7 <= SkillManager.getLevelForExperience(this.bot.getSkillManager().getExperience()[3]) ? 1 : 0;
            if (!this.bot.botFoodDepleted && n8 != 0) {
                int n10 = n8 = this.bot.isTeleblocked() ? 6 : 4;
                if (!BotCombatHelper.eatBotFood(this.bot) || this.bot.getInventoryManager().getItemAmount(this.bot.botFoodItemId) <= n8) {
                    BotCombatEscapeHandler.tryStartBotCombatEscape(this.bot);
                }
            }
            if (this.bot.getPoisonDamage() > 0.0) {
                BotCombatHelper.drinkAntipoisonPotion(this.bot);
            }
            if (this.bot.botPvpTeamInviteTicks >= 3) {
                this.bot.botPvpPendingTeamTarget.botPvpTeamRequesters.remove(this.bot);
                EntityTargetMovement.clearMovementTarget(this.bot);
                this.bot.botPvpRejectedTeamTargets.add(this.bot.botPvpPendingTeamTarget);
                this.bot.botPvpPendingTeamTarget = null;
                this.bot.botPvpTeamInviteTicks = 0;
            }
            if (this.bot.botPvpTeamInviteTicks > 0) {
                ++this.bot.botPvpTeamInviteTicks;
                return;
            }
            if (this.bot.currentGroup != null && this.bot.currentGroup.leader != this.bot) {
                if (!this.bot.currentGroup.leader.isInWilderness()) {
                    this.bot.currentGroup.removeMember(this.bot.currentGroup.leader);
                } else {
                    if (this.bot.getMovementTarget() == null) {
                        this.bot.currentGroup.refreshGroupFollowChain();
                    } else if (this.bot.getMovementQueue().isRunning() != this.bot.currentGroup.leader.getMovementQueue().isRunning()) {
                        this.bot.getMovementQueue().setRunning(this.bot.currentGroup.leader.getMovementQueue().isRunning());
                    }
                    if (this.bot.currentGroup.leader.getCombatTarget() != null && !this.bot.currentGroup.leader.getCombatTarget().isDead()) {
                        this.bot.getMovementQueue().setRunning(true);
                        CombatManager.startCombat(this.bot, this.bot.currentGroup.leader.getCombatTarget());
                    }
                }
            }
            if (this.bot.currentGroup != null && this.bot.currentGroup.leader != this.bot) {
                return;
            }
            ArrayList<Player> arrayList = new ArrayList<Player>();
            Player[] playerArray = World.players;
            n2 = World.players.length;
            boolean n13 = false;
            while (var3_12 < n2) {
                block75: {
                    block76: {
                        block77: {
                            object = playerArray[var3_12];
                            if (object == null || object == this.bot || ((Player)object).clanWarsBot) break block75;
                            if (this.bot.currentGroup == null) break block76;
                            if (!this.bot.currentGroup.containsMember((Player)object)) break block77;
                            if (!((Entity)object).getPosition().isWithinViewport(this.bot.getPosition())) {
                                this.bot.currentGroup.removeMember((Player)object);
                            }
                            break block75;
                        }
                        if (this.bot.currentGroup.deferredRemovedMembers.contains(object)) break block75;
                    }
                    if (((Entity)object).getPosition().isWithinViewport(this.bot.getPosition())) {
                        n = BotCombatHelper.getEscapeCombatLevelMargin(this.bot);
                        if (((Player)object).getCombatLevel() <= this.bot.getCombatLevel() + n) {
                            arrayList.add((Player)object);
                        }
                    }
                }
                ++var3_12;
            }
            Collections.shuffle(arrayList);
            boolean bl = false;
            for (Player player : arrayList) {
                if (player.getWildernessLevel() < Math.abs(this.bot.getCombatLevel() - player.getCombatLevel()) || !player.isInMultiCombatArea() && !player.getSingleCombatTimer().hasElapsed()) continue;
                if (!PathReachability.isReachable(this.bot, player.getPosition().getX(), player.getPosition().getY(), true, 0, 0) || BotCombatHelper.tryHandleBotPvpTeamGrouping(this.bot, player)) continue;
                Player teamTarget = player;
                if (player.currentGroup != null) {
                    teamTarget = player.currentGroup.leader;
                }
                if (!teamTarget.isBot && BotCombatHelper.canTeamWithBotPvpPlayer(this.bot, teamTarget, true) && !this.bot.botPvpRejectedTeamTargets.contains(teamTarget)) {
                    this.bot.getMovementQueue().setRunning(true);
                    this.bot.getUpdateState().setFaceEntity(player.getEncodedIndex());
                    this.bot.setAttackRange(1);
                    this.bot.setMovementTarget(player);
                    this.bot.botPvpPendingTeamTarget = teamTarget;
                    if (GameUtil.getDistance(this.bot.getPosition(), player.getPosition()) > 5) continue;
                    this.bot.queuePublicChatMessage("team?");
                    teamTarget.botPvpTeamRequesters.add(this.bot);
                    this.bot.botPvpTeamInviteTicks = 1;
                    continue;
                }
                if (!this.ignoreLootRiskCheck && !BotCombatHelper.isTargetLootWorthRisk(this.bot, player)) continue;
                if (!this.bot.getMovementQueue().isRunning()) {
                    this.bot.getMovementQueue().setRunning(true);
                }
                if (this.bot.currentGroup == null && player.currentGroup != null && (this.bot.isInMultiCombatArea() || player.isInMultiCombatArea()) && ((double)player.currentGroup.getHighestCombatLevel() >= (double)this.bot.getCombatLevel() * 0.7 || (double)player.currentGroup.getTotalCombatLevel() >= (double)this.bot.getCombatLevel() * 1.2)) continue;
                if (player == null || player.isDead()) break;
                if (this.bot.currentGroup == null) {
                    CombatManager.startCombat(this.bot, player);
                } else {
                    this.bot.currentGroup.attackTarget(player);
                }
                bl = true;
                break;
            }
            if (!bl) {
                int n14 = this.bot.getPosition().getX() + GameUtil.randomInt(20) - 10;
                int n15 = this.bot.getPosition().getY() + GameUtil.randomInt(20) - 10;
                if (n14 < 2951) {
                    n14 = 2961;
                }
                if (n14 > 3376) {
                    n14 = 3366;
                }
                if (n15 < 3520) {
                    n15 = 3530;
                }
                if (n15 > this.bot.botWildernessMaxY) {
                    n15 = this.bot.botWildernessMaxY - 10;
                }
                PathFinder.getInstance();
                PathFinder.findPath(this.bot, n14, n15, true, 0, 0);
            }
        }
    }
}

