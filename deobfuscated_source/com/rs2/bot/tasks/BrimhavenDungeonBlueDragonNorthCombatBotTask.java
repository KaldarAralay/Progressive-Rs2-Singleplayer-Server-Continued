/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.tasks;

import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class BrimhavenDungeonBlueDragonNorthCombatBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(2445, 5178, 0);
    private static int[] ignoredLootItemIds = new int[]{117, 1353, 2353};
    private static BotRoute[] taskRouteSegments = new BotRoute[]{new BotRoute(new Position[]{new Position(2445, 5172, 0), new Position(2460, 5169, 0), new Position(2474, 5168, 0)}), new BotRoute(new Position[]{new Position(2858, 9571, 0)}), new BotRoute(new Position[]{new Position(2850, 3164, 0), new Position(2834, 3159, 0), new Position(2827, 3170, 0), new Position(2816, 3182, 0)}), new BotRoute(new Position[]{new Position(2815, 3182, 0), new Position(2801, 3179, 0), new Position(2789, 3179, 0), new Position(2780, 3186, 0), new Position(2766, 3185, 0), new Position(2757, 3173, 0), new Position(2750, 3158, 0)}), new BotRoute(new Position[]{new Position(2706, 9564, 0), new Position(2691, 9564, 0)}), new BotRoute(new Position[]{new Position(2689, 9564, 0), new Position(2683, 9568, 0)}), new BotRoute(new Position[]{new Position(2683, 9570, 0), new Position(2681, 9577, 0), new Position(2676, 9583, 0), new Position(2676, 9588, 0), new Position(2667, 9587, 0), new Position(2652, 9589, 0)}), new BotRoute(new Position[]{new Position(2634, 9585, 2), new Position(2635, 9575, 2)})};

    public BrimhavenDungeonBlueDragonNorthCombatBotTask(int n) {
        super(routeStartPosition, taskRouteSegments, 1, true, 4);
        boolean bl = true;
        BrimhavenDungeonBlueDragonNorthCombatBotTask brimhavenDungeonBlueDragonNorthCombatBotTask = this;
        this.combatTask = true;
        bl = true;
        brimhavenDungeonBlueDragonNorthCombatBotTask = this;
        this.usesDepositBox = true;
        int[] nArray = ignoredLootItemIds;
        brimhavenDungeonBlueDragonNorthCombatBotTask = this;
        ((BotTaskDefinition)this).ignoredLootItemIds = nArray;
        super.addLootSellShopIds(new int[]{2});
        int n2 = 336;
        brimhavenDungeonBlueDragonNorthCombatBotTask = this;
        this.minimumServerRevision = 336;
    }

    @Override
    public final boolean meetsUnlockRequirements(Player player) {
        return player.getSkillManager().getCurrentLevels()[16] >= 40;
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(110);
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        player.botFoodItemId = 379;
        player.getBankContainer().addToTab(new ItemStack(995, 100000), 0);
        player.getBankContainer().addToTab(new ItemStack(player.botFoodItemId, 1000), 0);
        Object object = new ItemStack[]{new ItemStack(995, 875), new ItemStack(1359), new ItemStack(player.botFoodItemId, 20)};
        player.botTaskRequiredItems = (ItemStack[])object;
        player.getInventoryManager().addItem(((ItemStack[])object)[0]);
        player.getInventoryManager().addItem(((ItemStack[])object)[1]);
        player.getInventoryManager().addItem(((ItemStack[])object)[2]);
        object = player;
        GameplayHelper.prepareBotCombatStyle((Player)object, -1);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        GameplayHelper.resetBotSkillsToBase(player);
        int n = 55 + GameUtil.randomInt(40);
        int n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 0, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 2, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 1, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 4, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 6, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 5, n - (n / 5 << 1) + GameUtil.randomInt(n2));
        player.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player);
        int[] nArray = player.getSkillManager().getCurrentLevels();
        player.getSkillManager();
        nArray[3] = SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]);
        BotCombatHelper.setBotSkillLevel(player, 8, 60);
        BotCombatHelper.setBotSkillLevel(player, 16, 40);
        player.getSkillManager().refreshAllSkills();
    }

    @Override
    public final void startWalkToTask(Player player) {
        player.setAutoRetaliate(true);
        player.botTaskState = "walk towards task";
        player.botPathWaypointIndex = 0;
        player.botPathSegmentIndex = 0;
        player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex];
        player.continueBotRoute();
    }

    @Override
    public final void startWalkToBank(Player player) {
        player.setAutoRetaliate(false);
        player.botTaskState = "walk towards bank";
        player.botPathWaypointIndex = 0;
        player.botPathSegmentIndex = taskRouteSegments.length - 1;
        player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex].reversed();
        player.continueBotRoute();
    }

    @Override
    public final void continueWalkToTask(Player player, int n) {
        player.setAutoRetaliate(true);
        player.botPathWaypointIndex = n;
        player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex];
        this.advanceTaskRouteSegment(player, true);
    }

    @Override
    public final void continueWalkToBank(Player player, int n) {
        player.setAutoRetaliate(false);
        player.botPathWaypointIndex = n;
        player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex].reversed();
        this.advanceTaskRouteSegment(player, true);
    }

    @Override
    public final void advanceTaskRouteSegment(Player player, boolean bl) {
        if (player.botTaskState.equals("walk towards task") || player.botTaskState.equals("walk to task") && bl) {
            if (!bl) {
                ++player.botPathSegmentIndex;
            }
            player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex];
            if (!bl) {
                player.botPathWaypointIndex = 0;
            }
            if (player.botPathSegmentIndex == 1) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(9359);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 2) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(1764);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 3) {
                player.botTargetNpcId = 1596;
                return;
            }
            if (player.botPathSegmentIndex == 4) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(5083);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 5) {
                player.botTargetNpcId = 5103;
                return;
            }
            if (player.botPathSegmentIndex == 6) {
                player.botTargetNpcId = 5104;
                return;
            }
            if (player.botPathSegmentIndex == taskRouteSegments.length - 1) {
                player.botTaskState = "walk to task";
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(5094);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
        } else if (player.botTaskState.equals("walk towards bank") || player.botTaskState.equals("walk to bank") && bl) {
            if (!bl) {
                --player.botPathSegmentIndex;
            }
            player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex].reversed();
            if (!bl) {
                player.botPathWaypointIndex = 0;
            }
            if (player.botPathSegmentIndex == 0) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(9358);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                player.botTaskState = "walk to bank";
                return;
            }
            if (player.botPathSegmentIndex == 1) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(492);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 2) {
                player.botTargetNpcId = 1596;
                return;
            }
            if (player.botPathSegmentIndex == 3) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(5084);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 4) {
                player.botTargetNpcId = 5103;
                return;
            }
            if (player.botPathSegmentIndex == 5) {
                player.botTargetNpcId = 5104;
                return;
            }
            if (player.botPathSegmentIndex == 6) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(5096);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
            }
        }
    }
}

