/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.barrows;

import com.rs2.util.RectangularArea;

public final class BarrowsTunnelRoom {
    int roomId;
    int[] connectedRoomIds;
    int[] doorBitIndexes;
    RectangularArea roomBounds;

    BarrowsTunnelRoom(int n, int[] nArray, int[] nArray2, RectangularArea rectangularArea) {
        this.roomId = n;
        this.connectedRoomIds = nArray;
        this.doorBitIndexes = nArray2;
        this.roomBounds = rectangularArea;
    }
}

