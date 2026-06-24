/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.consumable;

import java.util.HashMap;
import java.util.Map;

public enum FoodDefinition {
    a(1, -1, new int[]{1971, 319, 1965, 1967, 2128, 1957, 1942, 2130}),
    b(2, -1, new int[]{1963, 1985, 2126, 247, 2120, 2120, 2108, 7072, 1969, 1982, 2162}),
    c(3, -1, new int[]{7928, 7929, 7930, 7931, 7932, 7933, 2142, 4293, 2140, 4291, 1973, 3151, 315, 1861, 2152}),
    d(4, -1, new int[]{6701, 325, 1977, 403}),
    e(5, -1, new int[]{2309, 7062, 3228, 347, 7082, 7084, 7078}),
    f(6, -1, new int[]{355, 6961, 6962, 6965}),
    g(7, -1, new int[]{2213, 2209, 2239, 339, 7223, 333}),
    h(8, -1, new int[]{7064, 5972, 6883, 351, 2217, 2244, 2205, 2237}),
    i(9, -1, new int[]{329}),
    j(10, -1, new int[]{2878, 7228, 361}),
    k(11, -1, new int[]{2259, 2149, 2277, 7066, 2255, 2281, 2253}),
    l(12, -1, new int[]{379, 739, 2195, 2191}),
    m(13, -1, new int[]{365, 7068}),
    n(14, -1, new int[]{373, 2345, 6703, 7054, 1961}),
    o(15, -1, new int[]{2185, 2187}),
    p(16, -1, new int[]{7056, 6705}),
    q(18, -1, new int[]{3144, 3147}),
    r(19, -1, new int[]{1883, 1885}),
    s(20, -1, new int[]{7058, 385}),
    t(21, -1, new int[]{397}),
    u(22, -1, new int[]{391, 7060}),
    v(5 + (int)(Math.random() * 3.0), -1, new int[]{3369}),
    w(6 + (int)(Math.random() * 3.0), -1, new int[]{3371}),
    x(7 + (int)(Math.random() * 3.0), -1, new int[]{3373}),
    y(6 + (int)(Math.random() * 5.0), -1, new int[]{3381}),
    z(8 + (int)(Math.random() * 5.0), -1, new int[]{5003, 5007}),
    A(7 + (int)(Math.random() * 12.0), -1, new int[]{6293, 6295, 6297, 6299, 6303}),
    B(5, 2333, new int[]{2325}),
    C(5, 2313, new int[]{2333}),
    D(6, 2331, new int[]{2327}),
    E(5, 2313, new int[]{2331}),
    F(6, 7190, new int[]{7188}),
    G(6, 2313, new int[]{7190}),
    H(6, 7180, new int[]{7178}),
    I(6, 2313, new int[]{7180}),
    J(7, 2335, new int[]{2323}),
    K(7, 2313, new int[]{2335}),
    L(8, 7200, new int[]{7198}),
    M(8, 2313, new int[]{7200}),
    N(8, 7220, new int[]{7218}),
    O(8, 2313, new int[]{7220}),
    P(8, 7210, new int[]{7208}),
    Q(8, 2313, new int[]{7210}),
    R(7, 2291, new int[]{2289}),
    S(7, -1, new int[]{2291}),
    T(8, 2295, new int[]{2293}),
    U(8, -1, new int[]{2295}),
    V(9, 2299, new int[]{2297}),
    W(9, -1, new int[]{2299}),
    X(9, 2303, new int[]{2301}),
    Y(9, -1, new int[]{2303}),
    Z(4, 1925, new int[]{1977}, true),
    aa(11, 1935, new int[]{1993}, true),
    ab(5, 1899, new int[]{1897}),
    ac(5, 1901, new int[]{1899}),
    ad(5, -1, new int[]{1901}),
    ae(5, 1893, new int[]{1891}),
    af(5, 1895, new int[]{1893}),
    ag(5, -1, new int[]{1895}),
    ah(11, 1923, new int[]{2003}),
    ai(19, 1923, new int[]{2011});

    private static Map definitionsByItemId;
    private int[] itemIds;
    private int healAmount;
    private int replacementItemId;

    static {
        definitionsByItemId = new HashMap();
        FoodDefinition[] foodDefinitionArray = FoodDefinition.values();
        int n = foodDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            FoodDefinition foodDefinition = foodDefinitionArray[n2];
            int[] nArray = foodDefinition.itemIds;
            int n3 = foodDefinition.itemIds.length;
            int n4 = 0;
            while (n4 < n3) {
                int n5 = nArray[n4];
                definitionsByItemId.put(n5, foodDefinition);
                ++n4;
            }
            ++n2;
        }
    }

    public static FoodDefinition forItemId(int n) {
        return (FoodDefinition)((Object)definitionsByItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private FoodDefinition(int n2, int n3, int[] nArray, boolean bl) {
        this.healAmount = n2;
        this.replacementItemId = n3;
        this.itemIds = nArray;
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private FoodDefinition(int n2, int n3, int[] nArray) {
        this.healAmount = n2;
        this.replacementItemId = n3;
        this.itemIds = nArray;
    }

    public final int getHealAmount() {
        return this.healAmount;
    }

    public final int getReplacementItemId() {
        return this.replacementItemId;
    }

    public final int[] getItemIds() {
        return this.itemIds;
    }
}

