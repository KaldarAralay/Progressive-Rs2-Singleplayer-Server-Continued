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

public final class LumbridgeWoolSpinningBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3092, 3245, 0);
    private static BotRoute[] taskRouteSegments = new BotRoute[]{new BotRoute(new Position[]{new Position(3093, 3247, 0), new Position(3098, 3247, 0), new Position(3098, 3239, 0), new Position(3104, 3234, 0), new Position(3110, 3232, 0), new Position(3115, 3228, 0), new Position(3135, 3228, 0), new Position(3152, 3234, 0), new Position(3161, 3239, 0), new Position(3173, 3240, 0), new Position(3180, 3247, 0), new Position(3190, 3245, 0), new Position(3198, 3254, 0), new Position(3213, 3253, 0), new Position(3220, 3243, 0), new Position(3231, 3231, 0), new Position(3232, 3219, 0), new Position(3217, 3218, 0)}), new BotRoute(new Position[]{new Position(3216, 3218, 0), new Position(3215, 3212, 0)}), new BotRoute(new Position[]{new Position(3215, 3211, 0), new Position(3205, 3209, 0)}), new BotRoute(new Position[]{new Position(3205, 3209, 1), new Position(3207, 3214, 1)}), new BotRoute(new Position[]{new Position(3208, 3214, 1), new Position(3209, 3213, 1)})};

    public LumbridgeWoolSpinningBotTask(int n) {
        super(routeStartPosition, taskRouteSegments, 0, false, 4);
        n = 2;
        LumbridgeWoolSpinningBotTask lumbridgeWoolSpinningBotTask = this;
        this.interactionOption = 2;
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(2644);
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        player.botTaskItemId = 1737;
        player.getBankContainer().addToTab(new ItemStack(player.botTaskItemId, 5000), 0);
        ItemStack[] itemStackArray = new ItemStack[]{new ItemStack(player.botTaskItemId, 28)};
        player.botTaskRequiredItems = itemStackArray;
        player.getInventoryManager().addItem(itemStackArray[0]);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        GameplayHelper.resetBotSkillsToBase(player);
        int n = 1 + GameUtil.randomInt(40);
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
            if (player.botPathSegmentIndex == 0 || player.botPathSegmentIndex == 1) {
                player.botTargetNpcId = 1516;
                return;
            }
            if (player.botPathSegmentIndex == 2) {
                player.botTargetNpcId = 1536;
                return;
            }
            if (player.botPathSegmentIndex == 3 && player.botPathWaypointIndex == 0) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(1738);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 4) {
                player.botTargetNpcId = 1536;
                player.botTaskState = "walk to task";
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
            if (player.botPathSegmentIndex == 3 || player.botPathSegmentIndex == 4) {
                player.botTargetNpcId = 1536;
                return;
            }
            if (player.botPathSegmentIndex == 2 && player.botPathWaypointIndex == 0) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(1739);
                player.botInteractionOption = 3;
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 1) {
                player.botInteractionOption = 1;
                player.botTargetNpcId = 1536;
                return;
            }
            if (player.botPathSegmentIndex == 0) {
                player.botTargetNpcId = 1516;
                player.botTaskState = "walk to bank";
            }
        }
    }
}

