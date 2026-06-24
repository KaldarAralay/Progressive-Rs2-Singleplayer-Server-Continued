/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.grandexchange;

import java.util.Comparator;

public final class GrandExchangePriceSampleTimestampComparator
implements Comparator {
    public GrandExchangePriceSampleTimestampComparator() {
    }
    public final int compare(Object object, Object object2) {
        GrandExchangePriceSample sample = (GrandExchangePriceSample)object;
        GrandExchangePriceSample sample2 = (GrandExchangePriceSample)object2;
        return Long.compare(sample2.timestampMillis, sample.timestampMillis);
    }

}

