/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.barrows;

public final class BarrowsRewardEntry {
    public int itemId;
    public int minAmount;
    public int maxAmount;
    public int rollThreshold;

    public BarrowsRewardEntry(int n, int n2, int n3, int n4) {
        this.itemId = n;
        this.minAmount = n2;
        this.maxAmount = n3;
        this.rollThreshold = n4;
    }
}

