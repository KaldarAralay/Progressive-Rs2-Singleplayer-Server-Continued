/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.tasks;

import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.tasks.SouthMossGiantDungeonEntryTickTask;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.consumable.FoodDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class EdgevilleDungeonSouthMossGiantCombatBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3253, 3420, 0);
    private static int[] ignoredLootItemIds = new int[]{1389, 1969};
    private static BotRoute[] taskRouteSegments = new BotRoute[]{new BotRoute(new Position[]{new Position(3253, 3428, 0), new Position(3247, 3432, 0), new Position(3245, 3444, 0), new Position(3243, 3455, 0), new Position(3238, 3458, 0)}), new BotRoute(new Position[]{new Position(3243, 9870, 0), new Position(3244, 9890, 0), new Position(3246, 9892, 0)}), new BotRoute(new Position[]{new Position(3247, 9892, 0), new Position(3268, 9892, 0), new Position(3275, 9893, 0), new Position(3281, 9898, 0), new Position(3281, 9907, 0), new Position(3272, 9915, 0), new Position(3257, 9915, 0), new Position(3246, 9916, 0)}), new BotRoute(new Position[]{new Position(3245, 9916, 0), new Position(3241, 9911, 0)}), new BotRoute(new Position[]{new Position(3241, 9910, 0), new Position(3241, 9907, 0), new Position(3224, 9908, 0), new Position(3217, 9908, 0), new Position(3210, 9899, 0)}), new BotRoute(new Position[]{new Position(3210, 9897, 0), new Position(3208, 9890, 0), new Position(3188, 9890, 0), new Position(3180, 9895, 0), new Position(3172, 9892, 0), new Position(3169, 9884, 0)})};

    public EdgevilleDungeonSouthMossGiantCombatBotTask(int n) {
        super(routeStartPosition, taskRouteSegments, 1, false, 2);
        boolean bl = true;
        EdgevilleDungeonSouthMossGiantCombatBotTask edgevilleDungeonSouthMossGiantCombatBotTask = this;
        this.combatTask = true;
        super.setForcedCombatStyle(0);
        int[] nArray = ignoredLootItemIds;
        edgevilleDungeonSouthMossGiantCombatBotTask = this;
        ((BotTaskDefinition)this).ignoredLootItemIds = nArray;
        int n2 = 10;
        edgevilleDungeonSouthMossGiantCombatBotTask = this;
        this.targetSearchRadius = n2;
        super.addLootSellShopIds(new int[]{151});
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(112);
    }

    @Override
    public final boolean meetsUnlockRequirements(Player player) {
        return player.getCombatLevel() >= 36;
    }

    @Override
    public final ArrayList getRequiredItems(Player player) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        int n = -1;
        FoodDefinition[] foodDefinitionArray = FoodDefinition.values();
        int n2 = foodDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            FoodDefinition foodDefinition = foodDefinitionArray[n3];
            if (foodDefinition.getHealAmount() >= 7) {
                int[] nArray = foodDefinition.getItemIds();
                int n4 = nArray.length;
                int n5 = 0;
                while (n5 < n4) {
                    int n6 = nArray[n5];
                    if (player.ownsItemAmount(n6, 16)) {
                        n = n6;
                        break;
                    }
                    ++n5;
                }
                if (n != -1) break;
            }
            ++n3;
        }
        arrayList.add(new ItemStack(n, 16));
        player.botFoodItemId = n;
        return arrayList;
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        player.botFoodItemId = 379;
        player.getBankContainer().addToTab(new ItemStack(player.botFoodItemId, 1000), 0);
        ItemStack[] itemStackArray = new ItemStack[]{new ItemStack(player.botFoodItemId, 20)};
        player.botTaskRequiredItems = itemStackArray;
        player.getInventoryManager().addItem(itemStackArray[0]);
        GameplayHelper.prepareBotCombatStyle(player, 0);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        GameplayHelper.resetBotSkillsToBase(player);
        int n = 30 + GameUtil.randomInt(20);
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
    public final void advanceTaskRouteSegment(Player object, boolean bl) {
        if (((Player)object).botTaskState.equals("walk towards task") || ((Player)object).botTaskState.equals("walk to task") && bl) {
            if (!bl) {
                ++((Player)object).botPathSegmentIndex;
            }
            ((Player)object).currentBotRoute = taskRouteSegments[((Player)object).botPathSegmentIndex];
            if (!bl) {
                ((Player)object).botPathWaypointIndex = 0;
            }
            int n = GameUtil.getRegionId(((Entity)object).getPosition().getX(), ((Entity)object).getPosition().getY());
            if (((Player)object).botPathSegmentIndex == 1 && ((Player)object).botPathWaypointIndex == 0 && n == 12854) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(881);
                ((Player)object).botRouteActionPending = true;
                if (((Player)object).interactWithBotObjectTargetsNoRetry(arrayList, false)) {
                    World.getTaskScheduler().schedule(new SouthMossGiantDungeonEntryTickTask(this, 3, (Player)object));
                    return;
                }
                arrayList.clear();
                arrayList.add(882);
                ((Player)object).interactWithBotObjectTargetsNoRetry(arrayList, false);
                return;
            }
            if (((Player)object).botPathSegmentIndex == taskRouteSegments.length - 1) {
                ((Player)object).botTaskState = "walk to task";
                ((Player)object).botTargetNpcId = 733;
                return;
            }
            ((Player)object).botTargetNpcId = 1530;
            return;
        }
        if (((Player)object).botTaskState.equals("walk towards bank") || ((Player)object).botTaskState.equals("walk to bank") && bl) {
            if (!bl) {
                --((Player)object).botPathSegmentIndex;
            }
            int n = GameUtil.getRegionId(((Entity)object).getPosition().getX(), ((Entity)object).getPosition().getY());
            ((Player)object).currentBotRoute = taskRouteSegments[((Player)object).botPathSegmentIndex].reversed();
            if (!bl) {
                ((Player)object).botPathWaypointIndex = 0;
            }
            if (((Player)object).botPathSegmentIndex == taskRouteSegments.length - 2) {
                ((Player)object).botTargetNpcId = 733;
                return;
            }
            if (((Player)object).botPathSegmentIndex == 0 && n == 12954) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(1755);
                ((Player)object).interactWithBotObjectTargets(arrayList);
                ((Player)object).botRouteActionPending = true;
                ((Player)object).botTaskState = "walk to bank";
                return;
            }
            ((Player)object).botTargetNpcId = 1530;
        }
    }
}

