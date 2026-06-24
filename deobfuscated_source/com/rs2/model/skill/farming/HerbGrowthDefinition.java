/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum HerbGrowthDefinition {
    GUAM(5291, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    MARRENTILL(5292, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    TARROMIN(5293, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    HARRALANDER(5294, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    GOUTWEED(6311, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    RANARR(5295, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    TOADFLAX(5296, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    IRIT(5297, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    AVANTOE(5298, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    KWUARM(5299, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    SNAPDRAGON(5300, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    CADANTINE(5301, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    LANTADYME(5302, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    DWARF_WEED(5303, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}}),
    TORSTOL(5304, (String[][])new String[][]{{"The seed has only just been planted."}, {"The herb is now ankle height."}, {"The herb is now knee height."}, {"The herb is now mid-thigh height."}, {"The herb is fully grown and ready to harvest."}});

    private int cropId;
    private String[][] growthMessages;
    private static Map definitionsByCropId;

    static {
        definitionsByCropId = new HashMap();
        HerbGrowthDefinition[] herbGrowthDefinitionArray = HerbGrowthDefinition.values();
        int n = herbGrowthDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            HerbGrowthDefinition herbGrowthDefinition = herbGrowthDefinitionArray[n2];
            definitionsByCropId.put(herbGrowthDefinition.cropId, herbGrowthDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - void declaration
     */
    private HerbGrowthDefinition(int n2, String[][] stringArray) {
        this.cropId = n2;
        this.growthMessages = stringArray;
    }

    public static HerbGrowthDefinition forCropId(int n) {
        return (HerbGrowthDefinition)((Object)definitionsByCropId.get(n));
    }

    public final String[][] getGrowthMessages() {
        return this.growthMessages;
    }
}

