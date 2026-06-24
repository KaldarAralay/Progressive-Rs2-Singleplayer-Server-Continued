/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.Position;
import com.rs2.model.skill.farming.FarmingPatchUtils;
import java.util.HashMap;
import java.util.Map;

public enum TreePatch {
    VARROCK(0, new Position[]{new Position(3228, 3458), new Position(3230, 3460)}, 2341),
    LUMBRIDGE(1, new Position[]{new Position(3192, 3230), new Position(3194, 3232)}, 2342),
    TAVERLEY(2, new Position[]{new Position(2935, 3437), new Position(2937, 3439)}, 2339),
    FALADOR(3, new Position[]{new Position(3003, 3372), new Position(3005, 3374)}, 2340);

    private int index;
    private Position[] bounds;
    private int objectId;
    private static Map patchByObjectId;

    static {
        patchByObjectId = new HashMap();
        TreePatch[] treePatchArray = TreePatch.values();
        int n = treePatchArray.length;
        int n2 = 0;
        while (n2 < n) {
            TreePatch treePatch = treePatchArray[n2];
            patchByObjectId.put(treePatch.objectId, treePatch);
            ++n2;
        }
    }

    public static TreePatch forObjectId(int n) {
        return (TreePatch)((Object)patchByObjectId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private TreePatch(int n2, Position[] positionArray, int n3) {
        this.index = n2;
        this.bounds = positionArray;
        this.objectId = n3;
    }

    public static TreePatch forPosition(Position position) {
        TreePatch[] treePatchArray = TreePatch.values();
        int n = treePatchArray.length;
        int n2 = 0;
        while (n2 < n) {
            TreePatch treePatch;
            TreePatch treePatch2 = treePatch = treePatchArray[n2];
            treePatch2 = treePatch;
            if (FarmingPatchUtils.containsPosition(treePatch.bounds[0], treePatch2.bounds[1], position)) {
                return treePatch;
            }
            ++n2;
        }
        return null;
    }

    public final int getIndex() {
        return this.index;
    }
}

