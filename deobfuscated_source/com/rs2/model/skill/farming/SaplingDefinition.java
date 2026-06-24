/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum SaplingDefinition {
    OAK(5312, 5358, 5364, 5370),
    WILLOW(5313, 5359, 5365, 5371),
    MAPLE(5314, 5360, 5366, 5372),
    YEW(5315, 5361, 5367, 5373),
    MAGIC(5316, 5362, 5368, 5374),
    SPIRIT_TREE(5317, 5363, 5369, 5375),
    APPLE(5283, 5480, 5488, 5496),
    BANANA(5284, 5481, 5489, 5497),
    ORANGE(5285, 5482, 5490, 5498),
    CURRY(5286, 5483, 5491, 5499),
    PINEAPPLE(5287, 5484, 5492, 5500),
    PAPAYA(5288, 5485, 5493, 5501),
    PALM(5289, 5486, 5494, 5502),
    CALQUAT(5290, 5487, 5495, 5503);

    private int seedId;
    private int seedlingId;
    private int wateredSeedlingId;
    private int saplingId;
    private static Map definitionsBySeedId;
    private static Map definitionsBySeedlingId;
    private static Map definitionsByWateredSeedlingId;

    static {
        definitionsBySeedId = new HashMap();
        definitionsBySeedlingId = new HashMap();
        definitionsByWateredSeedlingId = new HashMap();
        SaplingDefinition[] saplingDefinitionArray = SaplingDefinition.values();
        int n = saplingDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            SaplingDefinition saplingDefinition = saplingDefinitionArray[n2];
            definitionsBySeedId.put(saplingDefinition.seedId, saplingDefinition);
            definitionsBySeedlingId.put(saplingDefinition.seedlingId, saplingDefinition);
            definitionsByWateredSeedlingId.put(saplingDefinition.wateredSeedlingId, saplingDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SaplingDefinition(int n2, int n3, int n4, int n5) {
        this.seedId = n2;
        this.seedlingId = n3;
        this.wateredSeedlingId = n4;
        this.saplingId = n5;
    }

    public static SaplingDefinition forSeedId(int n) {
        return (SaplingDefinition)((Object)definitionsBySeedId.get(n));
    }

    public static SaplingDefinition forSeedlingId(int n) {
        return (SaplingDefinition)((Object)definitionsBySeedlingId.get(n));
    }

    public static SaplingDefinition forWateredSeedlingId(int n) {
        return (SaplingDefinition)((Object)definitionsByWateredSeedlingId.get(n));
    }

    public final int getSeedId() {
        return this.seedId;
    }

    public final int getSeedlingId() {
        return this.seedlingId;
    }

    public final int getWateredSeedlingId() {
        return this.wateredSeedlingId;
    }

    public final int getSaplingId() {
        return this.saplingId;
    }
}

