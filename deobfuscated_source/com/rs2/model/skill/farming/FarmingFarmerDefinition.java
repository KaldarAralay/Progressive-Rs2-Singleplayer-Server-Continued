/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum FarmingFarmerDefinition {
    a(2324, 41, "allotment", new String[]{"Northern patch", "Southern patch"}),
    b(2323, 54, "allotment", new String[]{"North-west patch", "South-East patch"}),
    c(2326, 123, "allotment", new String[]{"North-west patch", "South-East patch"}),
    d(2325, 99, "allotment", new String[]{"Northern patch", "Southern patch"}),
    e(2337, 162, "bushes", null),
    f(2336, 139, "bushes", null),
    g(2335, 47, "bushes", null),
    h(2338, 144, "bushes", null),
    i(2334, 123, "hops", null),
    j(2332, 134, "hops", null),
    k(2333, 153, "hops", null),
    l(2327, 62, "hops", null),
    m(2343, 32, "fruitTree", null),
    n(2331, 53, "fruitTree", null),
    o(2344, 74, "fruitTree", null),
    p(2330, 70, "fruitTree", null),
    q(2340, 85, "tree", null),
    r(2339, 163, "tree", null),
    s(2341, 146, "tree", null),
    t(2342, 58, "tree", null);

    private int npcId;
    private String patchType;
    private String[] patchLabels;
    private static Map definitionsByNpcId;

    static {
        definitionsByNpcId = new HashMap();
        FarmingFarmerDefinition[] farmingFarmerDefinitionArray = FarmingFarmerDefinition.values();
        int n = farmingFarmerDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            FarmingFarmerDefinition farmingFarmerDefinition = farmingFarmerDefinitionArray[n2];
            definitionsByNpcId.put(farmingFarmerDefinition.npcId, farmingFarmerDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private FarmingFarmerDefinition(int n2, int n3, String string2, String[] stringArray) {
        this.npcId = n2;
        this.patchType = string2;
        this.patchLabels = stringArray;
    }

    public static FarmingFarmerDefinition forNpcId(int n) {
        return (FarmingFarmerDefinition)((Object)definitionsByNpcId.get(n));
    }

    public final String getPatchType() {
        return this.patchType;
    }

    public final String[] getPatchLabels() {
        return this.patchLabels;
    }
}

