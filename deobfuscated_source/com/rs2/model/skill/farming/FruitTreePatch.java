/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.Position;
import com.rs2.model.skill.farming.FarmingPatchUtils;
import java.util.HashMap;
import java.util.Map;

public enum FruitTreePatch {
    BRIMHAVEN(0, new Position[]{new Position(2764, 3212), new Position(2765, 3213)}, 2330),
    CATHERBY(1, new Position[]{new Position(2860, 3433), new Position(2861, 3434)}, 2331),
    GNOME_STRONGHOLD(2, new Position[]{new Position(2475, 3445), new Position(2476, 3446)}, 2343),
    TREE_GNOME_VILLAGE(3, new Position[]{new Position(2489, 3179), new Position(2890, 3180)}, 2344);

    private int index;
    private Position[] bounds;
    private int objectId;
    private static Map patchByObjectId;

    static {
        patchByObjectId = new HashMap();
        FruitTreePatch[] fruitTreePatchArray = FruitTreePatch.values();
        int n = fruitTreePatchArray.length;
        int n2 = 0;
        while (n2 < n) {
            FruitTreePatch fruitTreePatch = fruitTreePatchArray[n2];
            patchByObjectId.put(fruitTreePatch.objectId, fruitTreePatch);
            ++n2;
        }
    }

    public static FruitTreePatch forObjectId(int n) {
        return (FruitTreePatch)((Object)patchByObjectId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private FruitTreePatch(int n2, Position[] positionArray, int n3) {
        this.index = n2;
        this.bounds = positionArray;
        this.objectId = n3;
    }

    public static FruitTreePatch forPosition(Position position) {
        FruitTreePatch[] fruitTreePatchArray = FruitTreePatch.values();
        int n = fruitTreePatchArray.length;
        int n2 = 0;
        while (n2 < n) {
            FruitTreePatch fruitTreePatch;
            FruitTreePatch fruitTreePatch2 = fruitTreePatch = fruitTreePatchArray[n2];
            fruitTreePatch2 = fruitTreePatch;
            if (FarmingPatchUtils.containsPosition(fruitTreePatch.bounds[0], fruitTreePatch2.bounds[1], position)) {
                return fruitTreePatch;
            }
            ++n2;
        }
        return null;
    }

    public final int getIndex() {
        return this.index;
    }
}

