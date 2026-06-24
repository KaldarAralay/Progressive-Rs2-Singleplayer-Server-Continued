/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.cache;

import com.rs2.cache.CacheIndexEntry;

public final class DefinitionIndexEntry
extends CacheIndexEntry {
    private int dataOffset;

    public DefinitionIndexEntry(int n, int n2) {
        super(n);
        this.dataOffset = n2;
    }

    public final int getDataOffset() {
        return this.dataOffset;
    }
}

