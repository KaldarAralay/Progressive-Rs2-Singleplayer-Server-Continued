/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

public final class Vector2f {
    private float x;
    private float y;

    public Vector2f(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

    public final Vector2f normalize() {
        Vector2f vector2f = this;
        float f = (float)Math.sqrt(vector2f.x * vector2f.x + vector2f.y * vector2f.y);
        this.x /= f;
        this.y /= f;
        return this;
    }
}

