/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.tasks;

import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class AirRuneRunecraftingBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3012, 3355, 0);
    private static BotRoute[] taskRouteSegments = new BotRoute[]{new BotRoute(new Position[]{new Position(3012, 3359, 0), new Position(3008, 3359, 0), new Position(3007, 3342, 0), new Position(3006, 3322, 0), new Position(2992, 3311, 0), new Position(2985, 3296, 0)}), new BotRoute(new Position[]{new Position(2841, 4829, 0)})};

    public AirRuneRunecraftingBotTask(int n) {
        super(routeStartPosition, taskRouteSegments, 0, false, 1);
    }

    @Override
    public final boolean isWithinProgressionRange(Player player) {
        return true;
    }

    @Override
    public final ArrayList getRequiredItems(Player player) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        if (player.ownsItem(5527) && ItemDefinition.isDefined(5527)) {
            arrayList.add(new ItemStack(5527, 1));
        } else {
            arrayList.add(new ItemStack(1438, 1));
        }
        if (!player.ownsItemAmount(1436, 500) && player.ownsItemAmount(7936, 500) && ItemDefinition.isDefined(7936)) {
            arrayList.add(new ItemStack(7936, 500));
        } else {
            arrayList.add(new ItemStack(1436, 500));
        }
        return arrayList;
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(2478);
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        player.getBankContainer().addToTab(new ItemStack(1436, 5000), 0);
        ItemStack[] itemStackArray = new ItemStack[]{new ItemStack(5527), new ItemStack(1436, 28)};
        if (GameUtil.randomInt(2) == 0 || !ItemDefinition.isDefined(5527)) {
            itemStackArray = new ItemStack[]{new ItemStack(1438), new ItemStack(1436, 27)};
        }
        player.botTaskRequiredItems = itemStackArray;
        if (itemStackArray[0].getId() == 5527) {
            player.getEquipmentManager().getContainer().setItem(0, itemStackArray[0]);
        } else {
            player.getInventoryManager().addItem(itemStackArray[0]);
        }
        player.getInventoryManager().addItem(itemStackArray[1]);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        GameplayHelper.resetBotSkillsToBase(player);
        int n = 1 + GameUtil.randomInt(99);
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
        player.botTaskState = "walk to bank";
        player.botPathWaypointIndex = 0;
        player.botPathSegmentIndex = taskRouteSegments.length - 2;
        player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex].reversed();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(2465);
        player.interactWithBotObjectTargets(arrayList);
        player.botRouteActionPending = true;
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
        n = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());
        if (player.botPathSegmentIndex == taskRouteSegments.length - 2 && player.botPathWaypointIndex == 0 && n == 11339) {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            arrayList.add(2465);
            player.interactWithBotObjectTargets(arrayList);
            player.botRouteActionPending = true;
        }
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
            if (player.botPathSegmentIndex == taskRouteSegments.length - 1) {
                player.botTaskState = "walk to task";
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(2452);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
            }
        }
    }
}

