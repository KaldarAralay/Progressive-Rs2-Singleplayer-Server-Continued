/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.path;

public class PathStep {
    private final int x;
    private final int y;

    public PathStep(int n, int n2) {
        this.x = n;
        this.y = n2;
    }

    public int hashCode() {
        int n = 31 + this.x;
        n = n * 31 + this.y;
        return n;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        object = (PathStep)object;
        if (this.x != ((PathStep)object).x) {
            return false;
        }
        return this.y == ((PathStep)object).y;
    }

    public String toString() {
        return String.valueOf(PathStep.class.getName()) + " [x=" + this.x + ", y=" + this.y + "]";
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }
}

