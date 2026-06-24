/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching;

public enum BowStringingDefinition {
    a(50, 1777, 841, 5, 5.0),
    b(48, 1777, 839, 10, 10.0),
    c(54, 1777, 843, 20, 16.5),
    d(56, 1777, 845, 25, 25.0),
    e(4825, 1777, 4827, 30, 45.0),
    f(60, 1777, 849, 35, 33.3),
    g(58, 1777, 847, 40, 41.5),
    h(64, 1777, 853, 50, 50.0),
    i(62, 1777, 851, 55, 58.3),
    j(68, 1777, 857, 65, 68.5),
    k(66, 1777, 855, 70, 75.0),
    l(72, 1777, 861, 80, 83.3),
    m(70, 1777, 859, 85, 91.5);

    private int unstrungBowItemId;
    private int bowStringItemId;
    private int strungBowItemId;
    private int requiredLevel;
    private double experience;

    public static BowStringingDefinition forComponents(int n, int n2) {
        BowStringingDefinition[] bowStringingDefinitionArray = BowStringingDefinition.values();
        int n3 = bowStringingDefinitionArray.length;
        int n4 = 0;
        while (n4 < n3) {
            BowStringingDefinition bowStringingDefinition = bowStringingDefinitionArray[n4];
            if (bowStringingDefinition.unstrungBowItemId == n && bowStringingDefinition.bowStringItemId == n2 || bowStringingDefinition.bowStringItemId == n && bowStringingDefinition.unstrungBowItemId == n2) {
                return bowStringingDefinition;
            }
            ++n4;
        }
        return null;
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private BowStringingDefinition(int n2, int n3, int n4, int n5, double d) {
        this.unstrungBowItemId = n2;
        this.bowStringItemId = 1777;
        this.strungBowItemId = n4;
        this.requiredLevel = n5;
        this.experience = d;
    }

    public final int getUnstrungBowItemId() {
        return this.unstrungBowItemId;
    }

    public final int getBowStringItemId() {
        return this.bowStringItemId;
    }

    public final int getStrungBowItemId() {
        return this.strungBowItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperience() {
        return this.experience;
    }
}

