/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.Position;
import com.rs2.model.skill.farming.FarmingPatchUtils;

public enum SpecialTreePatch {
    BRIMHAVEN_SPIRIT_TREE(0, new Position[]{new Position(2801, 3202), new Position(2803, 3204)}, 5375),
    TAI_BWO_WANNAI_CALQUAT(1, new Position[]{new Position(2795, 3100), new Position(2797, 3102)}, 5503),
    PORT_SARIM_SPIRIT_TREE(2, new Position[]{new Position(3059, 3257), new Position(3061, 3259)}, 5375),
    ETCETERIA_SPIRIT_TREE(3, new Position[]{new Position(2612, 3857), new Position(2614, 3859)}, 5375);

    private int index;
    private Position[] bounds;
    private int objectId;

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SpecialTreePatch(int n2, Position[] positionArray, int n3) {
        this.index = n2;
        this.bounds = positionArray;
        this.objectId = n3;
    }

    public static SpecialTreePatch forPosition(Position position) {
        SpecialTreePatch[] specialTreePatchArray = SpecialTreePatch.values();
        int n = specialTreePatchArray.length;
        int n2 = 0;
        while (n2 < n) {
            SpecialTreePatch specialTreePatch;
            SpecialTreePatch specialTreePatch2 = specialTreePatch = specialTreePatchArray[n2];
            specialTreePatch2 = specialTreePatch;
            if (FarmingPatchUtils.containsPosition(specialTreePatch.bounds[0], specialTreePatch2.bounds[1], position)) {
                return specialTreePatch;
            }
            ++n2;
        }
        return null;
    }

    public final int getIndex() {
        return this.index;
    }

    public final int getObjectId() {
        return this.objectId;
    }
}

