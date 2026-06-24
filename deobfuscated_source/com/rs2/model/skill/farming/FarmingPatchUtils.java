/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.Position;

public final class FarmingPatchUtils {
    public static final int[] wateredSeedlingItemIds = new int[]{5364, 5365, 5366, 5367, 5368, 5369, 5488, 5489, 5490, 5491, 5492, 5493, 5494, 5495};

    public static boolean containsPosition(Position position, Position position2, Position position3) {
        int n = position.getX();
        int n2 = position.getY();
        int n3 = position2.getX();
        int n4 = position2.getY();
        return position3.getX() >= n && position3.getY() >= n2 && position3.getX() <= n3 && position3.getY() <= n4;
    }
}

