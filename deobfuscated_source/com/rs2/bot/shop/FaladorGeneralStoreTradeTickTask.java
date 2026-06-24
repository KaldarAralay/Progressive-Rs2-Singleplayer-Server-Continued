/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.shop;

import com.rs2.bot.shop.FaladorGeneralStoreBotTask;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class FaladorGeneralStoreTradeTickTask
extends TickTask {
    private final /* synthetic */ Player player;

    public FaladorGeneralStoreTradeTickTask(FaladorGeneralStoreBotTask faladorGeneralStoreBotTask, int n, Player player) {
        super(2);
        this.player = player;
    }

    @Override
    public final void execute() {
        if (this.player.isDead() || !this.player.isRegistered() || !this.player.currentBotTask.usesCustomTaskAction || !this.player.botTaskState.equals("do task")) {
            this.stop();
            return;
        }
        if (this.player.getOpenInterfaceId() == 3824) {
            if (this.player.temporaryActionValue <= 0) {
                if (this.player.botShopBuyMode == 1) {
                    ItemStack itemStack = new ItemStack(this.player.botTaskItemId, this.player.botShopItemAmount);
                    ShopManager.buyItemStack(this.player, itemStack);
                    return;
                }
                if (this.player.botShopSellItemIds.size() != 0) {
                    ItemStack[] itemStackArray = this.player.getInventoryManager().getContainer().getItems();
                    int n = itemStackArray.length;
                    int n2 = 0;
                    while (n2 < n) {
                        ItemStack itemStack = itemStackArray[n2];
                        if (itemStack != null && this.player.botShopSellItemIds.contains(itemStack.getDefinition().getUnnotedId())) {
                            ShopManager.sellItemStack(this.player, itemStack);
                        }
                        ++n2;
                    }
                    return;
                }
                ItemStack itemStack = new ItemStack(this.player.botTaskItemId, this.player.botShopItemAmount);
                ShopManager.sellItemStack(this.player, itemStack);
                return;
            }
            --this.player.temporaryActionValue;
            return;
        }
        this.player.temporaryActionValue = 3 + GameUtil.randomInt(3);
        this.player.botInteractionOption = 2;
        this.player.interactWithBotNpcTargets(this.player.botInteractionTargetIds);
    }
}

