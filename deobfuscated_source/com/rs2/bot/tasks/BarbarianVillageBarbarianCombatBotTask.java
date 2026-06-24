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

public final class BarbarianVillageBarbarianCombatBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3094, 3491, 0);
    private static BotRoute taskRoute = new BotRoute(new Position[]{new Position(3090, 3490, 0), new Position(3081, 3483, 0), new Position(3080, 3467, 0), new Position(3086, 3464, 0), new Position(3086, 3451, 0), new Position(3086, 3440, 0), new Position(3083, 3434, 0), new Position(3079, 3433, 0), new Position(3079, 3425, 0)});
    private static int[] ignoredLootItemIds = new int[]{956, 1379, 1592, 1917, 6814};

    public BarbarianVillageBarbarianCombatBotTask(int n) {
        super(routeStartPosition, taskRoute, 1, false, 2);
        boolean bl = true;
        BarbarianVillageBarbarianCombatBotTask barbarianVillageBarbarianCombatBotTask = this;
        this.combatTask = true;
        int[] nArray = ignoredLootItemIds;
        barbarianVillageBarbarianCombatBotTask = this;
        ((BotTaskDefinition)this).ignoredLootItemIds = nArray;
    }

    @Override
    public final boolean meetsUnlockRequirements(Player player) {
        return player.getCombatLevel() >= 12;
    }

    @Override
    public final boolean isWithinProgressionRange(Player player) {
        return player.getCombatLevel() < 26;
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
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(3246);
        player.botInteractionTargetIds.add(3247);
        player.botInteractionTargetIds.add(3249);
        player.botInteractionTargetIds.add(12);
        player.botInteractionTargetIds.add(17);
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        player.botFoodItemId = 333;
        player.getBankContainer().addToTab(new ItemStack(player.botFoodItemId, 1000), 0);
        Object object = new ItemStack[]{new ItemStack(player.botFoodItemId, 20)};
        player.botTaskRequiredItems = (ItemStack[])object;
        player.getInventoryManager().addItem(((ItemStack[])object)[0]);
        object = player;
        GameplayHelper.prepareBotCombatStyle((Player)object, -1);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        GameplayHelper.resetBotSkillsToBase(player);
        int n = 15 + GameUtil.randomInt(5);
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
}

