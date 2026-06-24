/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum SpecialCropGrowthDefinition {
    BELLADONNA(5281, (String[][])new String[][]{{"The belladonna seed has only just been planted."}, {"The belladonna plant grows a little taller."}, {"The belladonna plant grows taller and leafier."}, {"The belladonna plant grows some flower buds."}, {"The belladonna plant is ready to harvest."}}),
    CACTUS(5280, (String[][])new String[][]{{"The cactus seed has only just been planted."}, {"The cactus grows taller."}, {"The cactus grows two small stumps."}, {"The cactus grows its stumps longer."}, {"The cactus grows larger."}, {"The cactus curves its arms upwards and grows another stump."}, {"The cactus grows all three of its arms upwards."}, {"The cactus is ready to be harvested."}}),
    MUSHROOM(5282, (String[][])new String[][]{{"The mushroom spore has only just been planted."}, {"The mushrooms grow a little taller."}, {"The mushrooms grow a little taller."}, {"The mushrooms grow a little larger."}, {"The mushrooms grow a little larger."}, {"The mushrooms tops grow a little wider."}, {"The mushrooms are ready to harvest."}});

    private int cropId;
    private String[][] growthMessages;
    private static Map definitionsByCropId;

    static {
        definitionsByCropId = new HashMap();
        SpecialCropGrowthDefinition[] specialCropGrowthDefinitionArray = SpecialCropGrowthDefinition.values();
        int n = specialCropGrowthDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            SpecialCropGrowthDefinition specialCropGrowthDefinition = specialCropGrowthDefinitionArray[n2];
            definitionsByCropId.put(specialCropGrowthDefinition.cropId, specialCropGrowthDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - void declaration
     */
    private SpecialCropGrowthDefinition(int n2, String[][] stringArray) {
        this.cropId = n2;
        this.growthMessages = stringArray;
    }

    public static SpecialCropGrowthDefinition forCropId(int n) {
        return (SpecialCropGrowthDefinition)((Object)definitionsByCropId.get(n));
    }

    public final String[][] getGrowthMessages() {
        return this.growthMessages;
    }
}

