/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.shop;

import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemStack;
import com.rs2.model.shop.ShopDefinition;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.task.TickTask;

public final class ShopRestockTask
extends TickTask {
    private final /* synthetic */ ShopDefinition shopDefinition;
    private final /* synthetic */ int itemId;
    private final /* synthetic */ int slotIndex;

    public ShopRestockTask(int n, ShopDefinition shopDefinition, int n2, int n3) {
        super(n);
        this.shopDefinition = shopDefinition;
        this.itemId = n2;
        this.slotIndex = n3;
    }

    @Override
    public final void execute() {
        if (this.shopDefinition.getOriginalStock().containsItem(this.itemId)) {
            int n = this.shopDefinition.getOriginalStock().findFlatItem(this.itemId).getAmount();
            int n2 = this.shopDefinition.getStock().findFlatItem(this.itemId).getAmount();
            if (n2 < n) {
                ItemStack itemStack = new ItemStack(this.itemId);
                ItemContainer itemContainer = this.shopDefinition.getStock();
                itemContainer.add(itemStack, -1);
            } else if (n2 > n) {
                this.shopDefinition.getStock().removeKeepingPlaceholder(new ItemStack(this.itemId));
            }
        } else {
            int n = ShopManager.indexOfStockItem(this.shopDefinition, this.itemId);
            if (this.shopDefinition.getStock().containsItem(this.itemId) && n == this.slotIndex) {
                this.shopDefinition.getStock().remove(new ItemStack(this.itemId));
            }
        }
        ShopManager.refreshShopForPlayers(this.shopDefinition.getShopId());
        if (!ShopManager.needsRestock(this.shopDefinition, this.itemId, this.slotIndex)) {
            this.stop();
        }
    }
}

