/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.shop;

import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.shop.CatherbyGeneralStoreTradeTickTask;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class CatherbyGeneralStoreBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(2809, 3441, 0);
    private static BotRoute taskRoute = new BotRoute(new Position[]{new Position(2808, 3437, 0), new Position(2806, 3434, 0)});

    public CatherbyGeneralStoreBotTask(int n) {
        super(routeStartPosition, taskRoute, 1, true, 1);
        n = 2;
        CatherbyGeneralStoreBotTask catherbyGeneralStoreBotTask = this;
        this.interactionOption = 2;
        this.usesCustomTaskAction = true;
    }

    @Override
    public final int getShopId() {
        NpcDefinition npcDefinition = NpcDefinition.forId(563);
        return npcDefinition.getShopId();
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        player.botInteractionTargetIds.clear();
        player.botInteractionTargetIds.add(563);
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        player.getBankContainer().clear();
        player.botTaskItemId = 2347;
        player.botShopItemAmount = 1;
        player.botShopBuyMode = GameUtil.randomInt(2);
        ItemStack[] itemStackArray = player.botShopBuyMode == 1 ? new ItemStack[]{new ItemStack(995, 100)} : new ItemStack[]{new ItemStack(player.botTaskItemId, player.botShopItemAmount)};
        player.botTaskRequiredItems = itemStackArray;
        player.getInventoryManager().addItem(itemStackArray[0]);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void startCustomTaskAction(Player object) {
        World.getTaskScheduler().schedule(new CatherbyGeneralStoreTradeTickTask(this, 2, (Player)object));
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
}

