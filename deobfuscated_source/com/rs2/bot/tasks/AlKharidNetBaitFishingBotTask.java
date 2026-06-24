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

public final class AlKharidNetBaitFishingBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3269, 3166, 0);
    private static BotRoute taskRoute = new BotRoute(new Position[]{new Position(3273, 3166, 0), new Position(3273, 3155, 0), new Position(3272, 3145, 0)});

    public AlKharidNetBaitFishingBotTask(int n) {
        super(routeStartPosition, taskRoute, 1, false, 4);
        int n2 = 20;
        AlKharidNetBaitFishingBotTask alKharidNetBaitFishingBotTask = this;
        this.targetSearchRadius = n2;
    }

    @Override
    public final boolean isWithinProgressionRange(Player player) {
        return player.getSkillManager().getCurrentLevels()[10] < 20 || player.getSkillManager().getCurrentLevels()[7] < 15;
    }

    @Override
    public final ArrayList getRequiredItems(Player player) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        if (player.getSkillManager().getCurrentLevels()[10] >= 5) {
            arrayList.add(new ItemStack(307, 1));
            arrayList.add(new ItemStack(313, 200));
        } else {
            arrayList.add(new ItemStack(303, 1));
        }
        return arrayList;
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(316);
    }

    @Override
    public final int getInteractionOption(Player player) {
        if (player.botTaskRequiredItems[0].getId() == 307) {
            return 2;
        }
        return 1;
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        ItemStack[] itemStackArray;
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        if (player.getSkillManager().getCurrentLevels()[10] >= 5) {
            player.getBankContainer().addToTab(new ItemStack(313, 5000), 0);
            itemStackArray = new ItemStack[]{new ItemStack(307), new ItemStack(313, 30)};
            player.getInventoryManager().addItem(itemStackArray[0]);
            player.getInventoryManager().addItem(itemStackArray[1]);
        } else {
            itemStackArray = new ItemStack[]{new ItemStack(303)};
            player.getInventoryManager().addItem(itemStackArray[0]);
        }
        player.botTaskRequiredItems = itemStackArray;
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
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        n = n - n / 5 + GameUtil.randomInt(n2);
        BotCombatHelper.setBotSkillLevel(player, 10, n);
        player.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player);
        int[] nArray = player.getSkillManager().getCurrentLevels();
        player.getSkillManager();
        nArray[3] = SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]);
        player.getSkillManager().refreshAllSkills();
    }
}

