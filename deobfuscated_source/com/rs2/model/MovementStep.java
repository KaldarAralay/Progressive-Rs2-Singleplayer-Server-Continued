/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.model.MovementQueue;
import com.rs2.model.Position;

public final class MovementStep
extends Position {
    private int direction;

    public MovementStep(MovementQueue object, int n, int n2, int n3) {
        super(n, n2);
        n = n3;
        this.direction = n;
    }

    public final int getDirection() {
        return this.direction;
    }
}

