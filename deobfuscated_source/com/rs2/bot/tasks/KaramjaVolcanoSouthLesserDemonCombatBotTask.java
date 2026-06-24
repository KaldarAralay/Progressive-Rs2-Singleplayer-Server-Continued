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
import com.rs2.model.item.consumable.FoodDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class KaramjaVolcanoSouthLesserDemonCombatBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3012, 3355, 0);
    private static BotRoute[] taskRouteSegments = new BotRoute[]{new BotRoute(new Position[]{new Position(3012, 3359, 0), new Position(3008, 3359, 0), new Position(3008, 3342, 0), new Position(3008, 3323, 0), new Position(3007, 3305, 0), new Position(3007, 3289, 0), new Position(3011, 3272, 0), new Position(3016, 3262, 0), new Position(3020, 3249, 0), new Position(3028, 3241, 0), new Position(3028, 3224, 0)}), new BotRoute(new Position[]{new Position(2946, 3147, 0), new Position(2932, 3149, 0), new Position(2916, 3152, 0), new Position(2898, 3156, 0), new Position(2880, 3156, 0), new Position(2862, 3166, 0)}), new BotRoute(new Position[]{new Position(2851, 9576, 0), new Position(2840, 9581, 0), new Position(2836, 9566, 0)})};
    private static int[] ignoredLootItemIds = new int[]{592, 1141, 1157, 1295, 1325, 1353, 1993};

    public KaramjaVolcanoSouthLesserDemonCombatBotTask(int n) {
        super(routeStartPosition, taskRouteSegments, 1, false, 2);
        boolean bl = true;
        KaramjaVolcanoSouthLesserDemonCombatBotTask karamjaVolcanoSouthLesserDemonCombatBotTask = this;
        this.combatTask = true;
        int[] nArray = ignoredLootItemIds;
        karamjaVolcanoSouthLesserDemonCombatBotTask = this;
        ((BotTaskDefinition)this).ignoredLootItemIds = nArray;
        super.addLootSellShopIds(new int[]{56, 94});
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(82);
    }

    @Override
    public final boolean meetsUnlockRequirements(Player player) {
        return player.getCombatLevel() >= 70;
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
            if (foodDefinition.getHealAmount() >= 10) {
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
        arrayList.add(new ItemStack(995, 1000));
        return arrayList;
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        player.botFoodItemId = 379;
        player.getBankContainer().addToTab(new ItemStack(995, 10000), 0);
        player.getBankContainer().addToTab(new ItemStack(player.botFoodItemId, 1000), 0);
        Object object = new ItemStack[]{new ItemStack(995, 60), new ItemStack(player.botFoodItemId, 20)};
        player.botTaskRequiredItems = (ItemStack[])object;
        player.getInventoryManager().addItem(((ItemStack[])object)[0]);
        player.getInventoryManager().addItem(((ItemStack[])object)[1]);
        object = player;
        GameplayHelper.prepareBotCombatStyle((Player)object, -1);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        GameplayHelper.resetBotSkillsToBase(player);
        int n = 40 + GameUtil.randomInt(30);
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
    public final void advanceTaskRouteSegment(Player player, boolean continuing) {
        if (player.botTaskState.equals("walk towards task") || player.botTaskState.equals("walk to task") && continuing) {
            if (!continuing) {
                ++player.botPathSegmentIndex;
            }
            player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex];
            if (!continuing) {
                player.botPathWaypointIndex = 0;
            }
            int regionId = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());
            if (player.botPathSegmentIndex == 1 && regionId == 12082) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(377);
                player.botInteractionOption = 1;
                player.interactWithBotNpcTargets(arrayList);
                player.botRouteActionPending = true;
                player.botRouteTravelPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 2 && regionId == 11313) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(492);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
                player.botTaskState = "walk to task";
                return;
            }
        } else if (player.botTaskState.equals("walk towards bank") || player.botTaskState.equals("walk to bank") && continuing) {
            if (!continuing) {
                --player.botPathSegmentIndex;
            }
            player.currentBotRoute = taskRouteSegments[player.botPathSegmentIndex].reversed();
            if (!continuing) {
                player.botPathWaypointIndex = 0;
            }
            int regionId = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());
            if (player.botPathSegmentIndex == 0 && regionId == 11825) {
                player.botTaskState = "walk to bank";
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(380);
                player.botInteractionOption = 1;
                player.interactWithBotNpcTargets(arrayList);
                player.botRouteActionPending = true;
                player.botRouteTravelPending = true;
                return;
            }
            if (player.botPathSegmentIndex == 1 && regionId == 11413) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(1764);
                player.interactWithBotObjectTargets(arrayList);
                player.botRouteActionPending = true;
            }
        }
    }
}

