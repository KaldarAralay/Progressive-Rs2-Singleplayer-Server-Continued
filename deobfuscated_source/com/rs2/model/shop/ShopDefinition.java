/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.shop;

import com.rs2.model.item.ItemContainer;
import com.rs2.model.shop.ShopCurrency;
import com.rs2.model.task.TickTask;

public final class ShopDefinition {
    private int shopId;
    private String name;
    private boolean generalStore;
    private boolean membersOnly = false;
    private ShopCurrency currency;
    private int currencyItemId;
    private ItemContainer originalStock;
    private ItemContainer stock;
    private int buyPricePercent;
    private int sellPricePercent;
    private int priceChangeRateTenths;
    private int[] restockDelayTicks;
    private TickTask[] restockTasks;

    public final void setBuyPricePercent(int n) {
        this.buyPricePercent = n;
    }

    public final int getBuyPricePercent() {
        return this.buyPricePercent;
    }

    public final void setSellPricePercent(int n) {
        this.sellPricePercent = n;
    }

    public final int getSellPricePercent() {
        return this.sellPricePercent;
    }

    public final void setPriceChangeRateTenths(int n) {
        this.priceChangeRateTenths = n;
    }

    public final double getPriceChangeRate() {
        double d = this.priceChangeRateTenths;
        return d /= 10.0;
    }

    public final void setName(String string) {
        this.name = string;
    }

    public final String getName() {
        return this.name;
    }

    public final boolean isMembersOnly() {
        return this.membersOnly;
    }

    public final void setMembersOnly(boolean bl) {
        this.membersOnly = bl;
    }

    public final int getShopId() {
        return this.shopId;
    }

    public final void setGeneralStore(boolean bl) {
        this.generalStore = bl;
    }

    public final boolean isGeneralStore() {
        return this.generalStore;
    }

    public final void setCurrencyItemId(int n) {
        this.currencyItemId = n;
    }

    public final int getCurrencyItemId() {
        return this.currencyItemId;
    }

    public final ShopCurrency getCurrency() {
        return this.currency;
    }

    public final void setCurrency(ShopCurrency shopCurrency) {
        this.currency = shopCurrency;
    }

    public final void setOriginalStock(ItemContainer itemContainer) {
        this.originalStock = itemContainer;
    }

    public final ItemContainer getOriginalStock() {
        return this.originalStock;
    }

    public final void setStock(ItemContainer itemContainer) {
        this.stock = itemContainer;
    }

    public final ItemContainer getStock() {
        return this.stock;
    }

    static /* synthetic */ void setRestockDelayTicks(ShopDefinition shopDefinition, int[] nArray) {
        shopDefinition.restockDelayTicks = nArray;
    }

    static /* synthetic */ void setRestockTasks(ShopDefinition shopDefinition, TickTask[] tickTaskArray) {
        shopDefinition.restockTasks = tickTaskArray;
    }

    static /* synthetic */ int[] getRestockDelayTicks(ShopDefinition shopDefinition) {
        return shopDefinition.restockDelayTicks;
    }

    static /* synthetic */ void setShopId(ShopDefinition shopDefinition, int n) {
        shopDefinition.shopId = n;
    }

    static /* synthetic */ TickTask[] getRestockTasks(ShopDefinition shopDefinition) {
        return shopDefinition.restockTasks;
    }
}

