/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects;

public final class ObjectRegionKey {
    private int regionX;
    private int regionY;

    public ObjectRegionKey(int n, int n2) {
        this.regionX = n;
        this.regionY = n2;
    }

    public final int hashCode() {
        int n = 31 + this.regionX;
        n = n * 31 + this.regionY;
        return n;
    }

    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        object = (ObjectRegionKey)object;
        if (this.regionX != ((ObjectRegionKey)object).regionX) {
            return false;
        }
        return this.regionY == ((ObjectRegionKey)object).regionY;
    }
}

