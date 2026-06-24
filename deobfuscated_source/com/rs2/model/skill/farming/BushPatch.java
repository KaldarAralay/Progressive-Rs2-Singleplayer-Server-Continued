/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.Position;
import com.rs2.model.skill.farming.FarmingPatchUtils;
import java.util.HashMap;
import java.util.Map;

public enum BushPatch {
    a(0, new Position[]{new Position(2591, 3863), new Position(2592, 3864)}, 2337),
    b(1, new Position[]{new Position(2617, 3225), new Position(2618, 3226)}, 2338),
    c(2, new Position[]{new Position(3181, 3357), new Position(3182, 3358)}, 2335),
    d(3, new Position[]{new Position(2940, 3221), new Position(2941, 3222)}, 2336);

    private int index;
    private Position[] bounds;
    private int objectId;
    private static Map patchByObjectId;

    static {
        patchByObjectId = new HashMap();
        BushPatch[] bushPatchArray = BushPatch.values();
        int n = bushPatchArray.length;
        int n2 = 0;
        while (n2 < n) {
            BushPatch bushPatch = bushPatchArray[n2];
            patchByObjectId.put(bushPatch.objectId, bushPatch);
            ++n2;
        }
    }

    public static BushPatch forObjectId(int n) {
        return (BushPatch)((Object)patchByObjectId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private BushPatch(int n2, Position[] positionArray, int n3) {
        this.index = n2;
        this.bounds = positionArray;
        this.objectId = n3;
    }

    public static BushPatch forPosition(Position position) {
        BushPatch[] bushPatchArray = BushPatch.values();
        int n = bushPatchArray.length;
        int n2 = 0;
        while (n2 < n) {
            BushPatch bushPatch;
            BushPatch bushPatch2 = bushPatch = bushPatchArray[n2];
            bushPatch2 = bushPatch;
            if (FarmingPatchUtils.containsPosition(bushPatch.bounds[0], bushPatch2.bounds[1], position)) {
                return bushPatch;
            }
            ++n2;
        }
        return null;
    }

    public final int getIndex() {
        return this.index;
    }
}

