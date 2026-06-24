/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects;

import com.rs2.model.Position;

public final class WorldObject {
    private int objectId;
    private int type;
    private int orientation;
    private Position position;

    public WorldObject(int n, int n2, int n3, Position position) {
        this.objectId = n;
        this.type = n2;
        this.orientation = n3;
        this.position = position;
    }

    public final int getObjectId() {
        return this.objectId;
    }

    public final int getType() {
        return this.type;
    }

    public final int getOrientation() {
        return this.orientation;
    }

    public final Position getPosition() {
        return this.position;
    }
}

