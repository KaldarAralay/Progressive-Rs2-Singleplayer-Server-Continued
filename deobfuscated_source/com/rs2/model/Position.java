/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;

public class Position {
    private int x;
    private int y;
    private int plane;
    private int previousX;
    private int previousY;

    public Position(int n, int n2) {
        this(n, n2, 0);
    }

    public Position(int n, int n2, int n3) {
        int n4 = n;
        Position position = this;
        this.x = n4;
        n4 = n2;
        position = this;
        this.y = n4;
        n4 = n3;
        position = this;
        this.plane = n4;
    }

    public String toString() {
        return "Position(" + this.x + ", " + this.y + ", " + this.plane + ")";
    }

    public boolean equals(Object object) {
        if (object instanceof Position) {
            object = (Position)object;
            return this.x == ((Position)object).x && this.y == ((Position)object).y && this.plane == ((Position)object).plane;
        }
        return false;
    }

    public final void set(Position position) {
        this.x = position.x;
        this.y = position.y;
        this.previousX = position.previousX;
        this.previousY = position.previousY;
        this.plane = position.plane;
    }

    public final void translate(int n, int n2) {
        Position position = this;
        int n3 = position.x;
        position = this;
        this.previousX = n3;
        position = this;
        n3 = position.y;
        position = this;
        this.previousY = n3;
        position = this;
        n3 = position.x + n;
        position = this;
        this.x = n3;
        position = this;
        n3 = position.y + n2;
        position = this;
        this.y = n3;
    }

    public final void setX(int n) {
        this.x = n;
    }

    public final int getX() {
        return this.x;
    }

    public final void setY(int n) {
        this.y = n;
    }

    public final int getY() {
        return this.y;
    }

    public final void setPlane(int n) {
        this.plane = n;
    }

    public final int getPlane() {
        return this.plane;
    }

    public final void setPreviousX(int n) {
        this.previousX = n;
    }

    public final int getPreviousX() {
        return this.previousX;
    }

    public final void setPreviousY(int n) {
        this.previousY = n;
    }

    public final int getPreviousY() {
        return this.previousY;
    }

    public final int getRegionX() {
        return (this.x >> 3) - 6;
    }

    public final int getRegionY() {
        return (this.y >> 3) - 6;
    }

    public final int getLocalX() {
        Position position = this;
        Position position2 = this;
        return position2.x - 8 * position.getRegionX();
    }

    public final int getLocalY() {
        Position position = this;
        Position position2 = this;
        return position2.y - 8 * position.getRegionY();
    }

    public static int updateLocalX(Player player) {
        int n = player.getLastKnownRegionPosition().getRegionX() << 3;
        Position position = player.getPosition();
        player.localX = position.x - n;
        return player.localX;
    }

    public static int updateLocalY(Player player) {
        int n = player.getLastKnownRegionPosition().getRegionY() << 3;
        Position position = player.getPosition();
        player.localY = position.y - n;
        return player.localY;
    }

    public final boolean isWithinViewport(Position position) {
        Position position2 = this;
        Position position3 = position2;
        position3 = position;
        if (position2.plane != position3.plane) {
            return false;
        }
        position3 = position = GameUtil.getDelta(this, position);
        if (position.x <= 14) {
            position3 = position;
            if (position3.x >= -15) {
                position3 = position;
                if (position3.y <= 14) {
                    position3 = position;
                    if (position3.y >= -15) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public final boolean isWithinDistance(Position position, int n) {
        Position position2 = this;
        Position position3 = position2;
        position3 = position;
        if (position2.plane != position3.plane) {
            return false;
        }
        return GameUtil.isWithinDistance(this, position, n);
    }

    public final Position copy() {
        return new Position(this.x, this.y, this.plane);
    }

    public final Position centerForSize(int n) {
        if (n == 1) {
            return this;
        }
        int n2 = n = (int)Math.floor(n / 2);
        int n3 = n;
        n = 0;
        int n4 = n3;
        int n5 = n2;
        Position position = this;
        return new Position(position.x + n5, position.y + n4, position.plane);
    }

    public final boolean isOrthogonallyAlignedWith(Position position) {
        block5: {
            Position position2;
            block4: {
                position2 = position = GameUtil.getDelta(this, position);
                position2 = position;
                if (position.x == position2.y) break block4;
                position2 = position;
                if (position2.x == 0) break block5;
            }
            position2 = position;
            if (position2.y != 0) {
                return false;
            }
        }
        return true;
    }

    public /* synthetic */ Object clone() {
        return this.copy();
    }
}

