/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import java.util.HashMap;
import java.util.Map;

public enum CookableFoodDefinition {
    a(2142, 2146, 2146, 1, 0.0, 0, 0, true, 255, 255),
    b(401, 1781, 1781, 1, 0.0, 0, 0, true, 255, 255),
    c(2132, 2142, 2146, 1, 30.0, 7, 7, true, 128, 512),
    d(2134, 2142, 2146, 1, 30.0, 7, 7, true, 128, 512),
    e(2136, 2142, 2146, 1, 30.0, 7, 7, true, 128, 512),
    f(2138, 2140, 2144, 1, 30.0, 7, 7, true, 128, 512),
    g(3226, 3228, 7222, 1, 30.0, 7, 7, true, 128, 512),
    h(1859, 1861, 323, 1, 40.0, 7, 7, true, 30, 253),
    i(3363, 3369, 3375, 12, 70.0, 17, 17, true, 93, 444),
    j(3365, 3371, 3375, 17, 80.0, 26, 26, true, 85, 428),
    k(6293, 6297, 6301, 16, 80.0, 25, 25, true, 91, 438),
    l(6295, 6299, 6303, 16, 80.0, 25, 25, true, 91, 438),
    m(3226, 7223, 7222, 16, 72.0, 25, 25, true, 160, 255),
    n(3367, 3373, 3375, 22, 95.0, 28, 28, true, 73, 402),
    o(2876, 2878, 2880, 30, 140.0, 38, 38, true, 200, 255),
    p(2321, 2325, 2329, 10, 78.0, 15, 15, false, 98, 452),
    q(2317, 2327, 2329, 20, 110.0, 25, 25, false, 78, 412),
    r(2319, 7170, 2329, 29, 128.0, 35, 35, false, 58, 372),
    s(7168, 2323, 2329, 30, 130.0, 35, 35, false, 58, 372),
    t(7176, 7178, 2329, 34, 138.0, 39, 39, false, 48, 352),
    u(7186, 7188, 2329, 47, 164.0, 52, 52, false, 38, 332),
    v(7196, 7198, 2329, 70, 210.0, 77, 77, false, 15, 270),
    w(7206, 7208, 2329, 85, 240.0, 90, 90, false, 1, 222),
    x(7216, 7218, 2329, 95, 260.0, 100, 100, false, 1, 212),
    y(2001, 2003, 2005, 25, 117.0, 30, 30, true, 68, 392),
    z(2009, 2011, 2013, 60, 280.0, 65, 65, true, 38, 332),
    A(2287, 2289, 2305, 35, 143.0, 38, 38, true, 48, 352),
    B(1889, 1891, 1903, 40, 180.0, 120, 120, false, 38, 332),
    C(2307, 2309, 2311, 1, 40.0, 5, 5, false, 118, 492),
    D(1863, 1865, 1867, 58, 40.0, 65, 65, true, 118, 492),
    E(7072, 7072, 2880, 9, 25.0, 38, 38, true, 128, 512),
    F(7076, 7078, 7090, 13, 50.0, 16, 16, true, 90, 438),
    G(1871, 7084, 7092, 42, 60.0, 45, 45, true, 36, 322),
    H(7080, 7082, 7094, 46, 60.0, 52, 52, true, 16, 282),
    I(1942, 6701, 6699, 9, 25.0, 38, 38, false, 108, 472),
    J(4237, 4239, 6699, 9, 25.0, 38, 38, true, 78, 412),
    K(6006, 6008, 6008, 1, 0.0, 1, 1, true, 128, 512),
    L(317, 315, 323, 1, 30.0, 34, 34, true, 128, 512),
    M(3150, 3151, 3148, 1, 10.0, 34, 34, true, 200, 400),
    N(327, 325, 369, 1, 40.0, 38, 38, true, 118, 492),
    O(321, 319, 323, 1, 30.0, 34, 34, true, 128, 512),
    P(345, 347, 357, 5, 50.0, 37, 37, true, 108, 472),
    Q(353, 355, 357, 10, 60.0, 35, 35, true, 98, 452),
    R(335, 333, 343, 15, 70.0, 50, 50, true, 88, 432),
    S(341, 339, 343, 17, 75.0, 39, 39, true, 88, 432),
    T(349, 351, 343, 20, 80.0, 52, 52, true, 78, 412),
    U(331, 329, 343, 25, 90.0, 58, 58, true, 68, 392),
    V(3379, 3381, 3383, 28, 95.0, 58, 58, true, 63, 382),
    W(359, 361, 367, 30, 100.0, 65, 65, true, 58, 372),
    X(3142, 3144, 3148, 30, 190.0, 100, 100, true, 70, 255),
    Y(5001, 5003, 5002, 38, 115.0, 40, 40, true, 38, 332),
    Z(377, 379, 381, 40, 120.0, 74, 66, true, 38, 332),
    aa(363, 365, 367, 43, 130.0, 80, 80, true, 33, 312),
    ab(371, 373, 375, 45, 140.0, 86, 81, true, 18, 292),
    ac(2148, 2149, 3383, 53, 60.0, 72, 72, true, 18, 292),
    ad(383, 385, 387, 80, 210.0, 104, 94, true, 1, 232),
    ae(395, 397, 399, 82, 212.0, 110, 110, true, 1, 222),
    af(389, 391, 393, 91, 216.0, 112, 112, true, 1, 222);

    private int rawItemId;
    private int cookedItemId;
    private int burntItemId;
    private int requiredLevel;
    private double experience;
    private boolean cookableOnFire;
    private int successChanceLow;
    private int successChanceHigh;
    private static Map definitionsByRawItemId;

    static {
        definitionsByRawItemId = new HashMap();
        CookableFoodDefinition[] cookableFoodDefinitionArray = CookableFoodDefinition.values();
        int n = cookableFoodDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            CookableFoodDefinition cookableFoodDefinition = cookableFoodDefinitionArray[n2];
            definitionsByRawItemId.put(cookableFoodDefinition.rawItemId, cookableFoodDefinition);
            ++n2;
        }
    }

    public static CookableFoodDefinition forRawItemId(int n) {
        return (CookableFoodDefinition)((Object)definitionsByRawItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private CookableFoodDefinition(int n2, int n3, int n4, int n5, double d, int n6, int n7, boolean bl, int n8, int n9) {
        this.rawItemId = n2;
        this.cookedItemId = n3;
        this.burntItemId = n4;
        this.requiredLevel = n5;
        this.experience = d;
        this.cookableOnFire = bl;
        this.successChanceLow = n8;
        this.successChanceHigh = n9;
    }

    public final int getCookedItemId() {
        return this.cookedItemId;
    }

    public final int getBurntItemId() {
        return this.burntItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }

    public final boolean canCookOnFire() {
        return this.cookableOnFire;
    }

    public final int getSuccessChanceLow() {
        return this.successChanceLow;
    }

    public final int getSuccessChanceHigh() {
        return this.successChanceHigh;
    }
}

