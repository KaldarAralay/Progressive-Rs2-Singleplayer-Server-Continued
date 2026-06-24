/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

public final class WeightedChanceEntry {
    public final int lowChance;
    public final int highChance;
    public final int requiredLevel;

    public WeightedChanceEntry(int n, int n2, int n3) {
        this.lowChance = n;
        this.highChance = n2;
        this.requiredLevel = n3;
    }
}

