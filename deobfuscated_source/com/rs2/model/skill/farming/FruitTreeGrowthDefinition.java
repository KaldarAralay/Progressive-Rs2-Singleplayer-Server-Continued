/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum FruitTreeGrowthDefinition {
    APPLE(5496, (String[][])new String[][]{{"The apple sapling has only just been planted."}, {"The apple sapling grows into a small stump."}, {"The apple stump grows a little larger."}, {"The apple tree grows a small canopy."}, {"The apple tree grows a second small canopy."}, {"The apple tree grows larger."}, {"The apple tree is ready to be harvested."}}),
    BANANA(5497, (String[][])new String[][]{{"The banana sapling has only just been planted."}, {"The banana sapling grows 3 segments high, with 2 leaves."}, {"The banana tree grows 2 more leaves."}, {"The banana tree grows 5 segments high, and has some small bananas."}, {"The banana tree grows a bit larger."}, {"The banana tree grows a bit larger."}, {"The banana tree is ready to be harvested."}}),
    ORANGE(5498, (String[][])new String[][]{{"The orange sapling has only just been planted."}, {"The orange sapling grows slightly taller."}, {"The orange sapling grows even taller."}, {"The orange tree grows a small canopy."}, {"The orange tree grows taller."}, {"The orange tree grows wider and taller."}, {"The oranges on the tree are ready to be harvested."}}),
    CURRY(5499, (String[][])new String[][]{{"The curry sapling has only just been planted."}, {"The curry trunk grows towards the north."}, {"The curry trunk grows towards the north."}, {"The curry tree grows upwards."}, {"The curry trunk grows towards the south."}, {"The curry trunk grows towards the south."}, {"The curry tree is ready to be harvested."}}),
    PINEAPPLE(5500, (String[][])new String[][]{{"The pineapple sapling has only just been planted."}, {"The pineapple plant grows larger."}, {"The pineapple plant base turns brown."}, {"The pineapple plant grows larger."}, {"The pineapple plant grows larger."}, {"The pineapple plant grows larger."}, {"The pineapple plant is ready to be harvested."}}),
    PAPAYA(5501, (String[][])new String[][]{{"The papaya sapling has only just been planted."}, {"The papaya sapling grows a little larger."}, {"The papaya tree grows a little larger."}, {"The papaya tree grows a bit larger."}, {"The papaya tree grows some small yellow fruit."}, {"The papaya tree grows larger."}, {"The papaya tree is ready to be harvested."}}),
    PALM(5502, (String[][])new String[][]{{"The palm sapling has only just been planted."}, {"The palm sapling grows a little larger."}, {"The palm stump grows a little larger."}, {"The palm tree grows a small canopy."}, {"The palm tree grows taller."}, {"The palm tree grows more leaves."}, {"The palm tree is ready to be harvested."}});

    private int treeId;
    private String[][] growthMessages;
    private static Map definitionsByTreeId;

    static {
        definitionsByTreeId = new HashMap();
        FruitTreeGrowthDefinition[] fruitTreeGrowthDefinitionArray = FruitTreeGrowthDefinition.values();
        int n = fruitTreeGrowthDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            FruitTreeGrowthDefinition fruitTreeGrowthDefinition = fruitTreeGrowthDefinitionArray[n2];
            definitionsByTreeId.put(fruitTreeGrowthDefinition.treeId, fruitTreeGrowthDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - void declaration
     */
    private FruitTreeGrowthDefinition(int n2, String[][] stringArray) {
        this.treeId = n2;
        this.growthMessages = stringArray;
    }

    public static FruitTreeGrowthDefinition forTreeId(int n) {
        return (FruitTreeGrowthDefinition)((Object)definitionsByTreeId.get(n));
    }

    public final String[][] getGrowthMessages() {
        return this.growthMessages;
    }
}

