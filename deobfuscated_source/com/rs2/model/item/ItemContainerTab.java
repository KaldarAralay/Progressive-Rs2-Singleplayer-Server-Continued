/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item;

import java.util.ArrayList;

public final class ItemContainerTab {
    ArrayList items = new ArrayList();
    boolean persistent = false;

    private ItemContainerTab() {
    }

    private ItemContainerTab(byte by) {
        this.persistent = true;
    }

    public final ArrayList getItems() {
        return this.items;
    }

    /* synthetic */ ItemContainerTab(boolean bl) {
        this((byte)0);
    }

    /* synthetic */ ItemContainerTab(char c) {
        this();
    }
}

