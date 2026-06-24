/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching;

import java.util.HashMap;

public enum AmmunitionFletchingDefinition {
    a(314, 52, 53, 1, 0.4),
    b(39, 53, 882, 1, 1.3),
    c(40, 53, 884, 15, 2.5),
    d(41, 53, 886, 30, 5.0),
    e(42, 53, 888, 45, 7.5),
    f(43, 53, 890, 60, 10.0),
    g(44, 53, 892, 75, 12.5),
    h(819, 314, 806, 1, 1.8),
    i(820, 314, 807, 22, 3.8),
    j(821, 314, 808, 37, 7.5),
    k(822, 314, 809, 52, 11.2),
    l(823, 314, 810, 67, 15.0),
    m(824, 314, 811, 81, 18.8),
    n(4819, 53, 4773, 7, 1.4),
    o(4820, 53, 4778, 18, 2.6),
    p(1539, 53, 4783, 33, 5.1),
    q(4821, 53, 4788, 38, 6.4),
    r(4822, 53, 4793, 49, 7.5),
    s(4823, 53, 4798, 62, 10.1),
    t(4824, 53, 4803, 77, 12.5),
    u(46, 877, 880, 41, 3.2),
    v(45, 877, 879, 11, 1.6);

    private int componentItemId;
    private int baseItemId;
    private int productItemId;
    private int requiredLevel;
    private double experience;

    static {
        new HashMap();
    }

    public static AmmunitionFletchingDefinition forComponents(int n, int n2) {
        AmmunitionFletchingDefinition[] ammunitionFletchingDefinitionArray = AmmunitionFletchingDefinition.values();
        int n3 = ammunitionFletchingDefinitionArray.length;
        int n4 = 0;
        while (n4 < n3) {
            AmmunitionFletchingDefinition ammunitionFletchingDefinition = ammunitionFletchingDefinitionArray[n4];
            if (ammunitionFletchingDefinition.componentItemId == n && ammunitionFletchingDefinition.baseItemId == n2 || ammunitionFletchingDefinition.baseItemId == n && ammunitionFletchingDefinition.componentItemId == n2) {
                return ammunitionFletchingDefinition;
            }
            ++n4;
        }
        return null;
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private AmmunitionFletchingDefinition(int n2, int n3, int n4, int n5, double d) {
        this.componentItemId = n2;
        this.baseItemId = n3;
        this.productItemId = n4;
        this.requiredLevel = n5;
        this.experience = d;
    }

    public final int getComponentItemId() {
        return this.componentItemId;
    }

    public final int getBaseItemId() {
        return this.baseItemId;
    }

    public final int getProductItemId() {
        return this.productItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

