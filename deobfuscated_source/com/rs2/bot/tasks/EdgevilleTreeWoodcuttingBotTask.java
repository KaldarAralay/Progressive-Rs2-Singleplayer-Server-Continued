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
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.skill.woodcutting.TreeDefinition;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class EdgevilleTreeWoodcuttingBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3094, 3491, 0);
    private static BotRoute taskRoute = new BotRoute(new Position[]{new Position(3090, 3491, 0), new Position(3081, 3483, 0), new Position(3080, 3467, 0), new Position(3086, 3464, 0), new Position(3086, 3461, 0)});

    public EdgevilleTreeWoodcuttingBotTask(int n) {
        super(routeStartPosition, taskRoute, 0, false, 1);
        int n2 = 3470;
        EdgevilleTreeWoodcuttingBotTask edgevilleTreeWoodcuttingBotTask = this;
        this.targetMaxY = n2;
        n2 = 3449;
        edgevilleTreeWoodcuttingBotTask = this;
        this.targetMinY = n2;
        n2 = 3073;
        edgevilleTreeWoodcuttingBotTask = this;
        this.targetMinX = n2;
        n2 = 3092;
        edgevilleTreeWoodcuttingBotTask = this;
        this.targetMaxX = n2;
    }

    @Override
    public final ArrayList getRequiredItems(Player player) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        arrayList.add(new ItemStack(ItemCombinationHandler.getOwnedOrFallbackGatheringTool(player, 8).getToolItemId(), 1));
        return arrayList;
    }

    @Override
    public final boolean isWithinProgressionRange(Player player) {
        return player.getSkillManager().getCurrentLevels()[8] < 15;
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        int[] nArray = TreeDefinition.collectObjectIds(new TreeDefinition[]{TreeDefinition.TREE});
        int n = nArray.length;
        int n2 = 0;
        while (n2 < n) {
            int n3 = nArray[n2];
            player.botInteractionTargetIds.add(n3);
            ++n2;
        }
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        if (player.getSkillManager().getCurrentLevels()[8] >= 41 && player.getSkillManager().getCurrentLevels()[0] >= 40) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1359));
        } else if (player.getSkillManager().getCurrentLevels()[8] >= 31 && player.getSkillManager().getCurrentLevels()[0] >= 30) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1357));
        } else if (player.getSkillManager().getCurrentLevels()[8] >= 21 && player.getSkillManager().getCurrentLevels()[0] >= 20) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1355));
        } else if (player.getSkillManager().getCurrentLevels()[8] >= 11 && player.getSkillManager().getCurrentLevels()[0] >= 10) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1361));
        } else if (player.getSkillManager().getCurrentLevels()[8] >= 6 && player.getSkillManager().getCurrentLevels()[0] >= 5) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1353));
        } else {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(GameUtil.randomInt(2) == 0 ? 1349 : 1351));
        }
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
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        n = n - n / 5 + GameUtil.randomInt(n2);
        BotCombatHelper.setBotSkillLevel(player, 8, n);
        player.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player);
        int[] nArray = player.getSkillManager().getCurrentLevels();
        player.getSkillManager();
        nArray[3] = SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]);
        player.getSkillManager().refreshAllSkills();
    }
}

