/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc.drop;

public final class NpcDropEntry {
    private int itemId;
    private int fixedAmount;
    private int minAmount;
    private int maxAmount;
    private int[] itemIds;
    private int[] amountOptions;
    private int chanceType = -1;
    private int chanceNumerator = -1;
    private int chanceDenominator = -1;
    private int[] minAmounts;
    private int[] maxAmounts;

    public NpcDropEntry(int n, int n2, int n3, int n4, int[] nArray, int[] nArray2, int n5, int n6, int n7, int[] nArray3, int[] nArray4) {
        this.itemId = n;
        this.fixedAmount = n2;
        this.minAmount = n3;
        this.maxAmount = n4;
        this.itemIds = nArray;
        this.amountOptions = nArray2;
        this.chanceType = n5;
        this.chanceNumerator = n6;
        this.chanceDenominator = n7;
        this.minAmounts = nArray3;
        this.maxAmounts = nArray4;
    }

    public final void setChanceNumerator(int n) {
        this.chanceNumerator = n;
    }

    public final void setChanceDenominator(int n) {
        this.chanceDenominator = n;
    }

    public final int getChanceType() {
        return this.chanceType;
    }

    public final int getChanceNumerator() {
        return this.chanceNumerator;
    }

    public final int getChanceDenominator() {
        return this.chanceDenominator;
    }

    public final int getItemId() {
        return this.itemId;
    }

    public final int getFixedAmount() {
        return this.fixedAmount;
    }

    public final int getMinAmount() {
        return this.minAmount;
    }

    public final int getMaxAmount() {
        return this.maxAmount;
    }

    public final int[] getItemIds() {
        if (this.itemIds == null) {
            return new int[0];
        }
        return this.itemIds;
    }

    public final int[] getAmountOptions() {
        if (this.amountOptions == null) {
            return new int[0];
        }
        return this.amountOptions;
    }

    public final int[] getMinAmounts() {
        if (this.minAmounts == null) {
            return new int[0];
        }
        return this.minAmounts;
    }

    public final int[] getMaxAmounts() {
        if (this.maxAmounts == null) {
            return new int[0];
        }
        return this.maxAmounts;
    }
}

