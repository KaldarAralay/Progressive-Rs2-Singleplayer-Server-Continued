/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import java.util.Comparator;

public final class WeightedChanceEntryThresholdComparator
implements Comparator {
    public WeightedChanceEntryThresholdComparator() {
    }
    public final int compare(Object object, Object object2) {
        WeightedChanceEntry entry = (WeightedChanceEntry)object;
        WeightedChanceEntry entry2 = (WeightedChanceEntry)object2;
        return Integer.compare(entry2.requiredLevel, entry.requiredLevel);
    }

}

