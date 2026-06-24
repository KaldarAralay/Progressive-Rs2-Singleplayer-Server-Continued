/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import com.rs2.model.Position;

public final class RectangularArea {
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private byte plane;

    public RectangularArea(int n, int n2, int n3, int n4, byte by) {
        this.minX = n;
        this.minY = n2;
        this.maxX = n3;
        this.maxY = n4;
        this.plane = by;
    }

    public RectangularArea(int n, int n2, int n3, int n4, int plane) {
        this(n, n2, n3, n4, (byte)plane);
    }

    public RectangularArea(int n, int n2, int n3, int n4) {
        this.minX = n <= n3 ? n : n3;
        this.minY = n2 <= n4 ? n2 : n4;
        this.maxX = n >= n3 ? n : n3;
        this.maxY = n2 >= n4 ? n2 : n4;
    }

    public final int getMinX() {
        return this.minX;
    }

    public final int getMinY() {
        return this.minY;
    }

    public final int getMaxX() {
        return this.maxX;
    }

    public final int getMaxY() {
        return this.maxY;
    }

    public final Position[] getPositions() {
        Position[] positionArray = new Position[(this.maxX - this.minX + 1) * (this.maxY - this.minY + 1)];
        int n = 0;
        int n2 = this.minX;
        while (n2 <= this.maxX) {
            int n3 = this.minY;
            while (n3 <= this.maxY) {
                positionArray[n++] = new Position(n2, n3, this.plane);
                ++n3;
            }
            ++n2;
        }
        return positionArray;
    }

    public static RectangularArea fromPositionOffset(Position position, int n, int n2) {
        int n3 = position.getX();
        int n4 = position.getY();
        n = position.getX() + n;
        n2 = position.getY() + n2;
        return new RectangularArea(n3, n4, n, n2, (byte)position.getPlane());
    }

    public final boolean containsExclusive(Position position) {
        int n = position.getX();
        int n2 = position.getY();
        return n > this.minX && n < this.maxX && n2 > this.minY && n2 < this.maxY;
    }

    public final boolean contains(Position position) {
        int n = position.getX();
        int n2 = position.getY();
        return n >= this.minX && n <= this.maxX && n2 >= this.minY && n2 <= this.maxY;
    }

    public final boolean containsExclusiveOnPlane(Position position) {
        if (this.plane != position.getPlane()) {
            return false;
        }
        int n = position.getX();
        int n2 = position.getY();
        return n > this.minX && n < this.maxX && n2 > this.minY && n2 < this.maxY;
    }
}

