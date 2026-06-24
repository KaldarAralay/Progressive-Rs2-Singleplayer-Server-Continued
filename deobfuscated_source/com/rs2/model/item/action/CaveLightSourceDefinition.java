/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.action;

import java.util.HashMap;
import java.util.Map;

public enum CaveLightSourceDefinition {
    a(1, 1, true, true, 596, 594),
    b(1, 1, true, true, 36, 33),
    c(1, 1, true, true, 38, 32),
    d(4, 1, false, true, 4529, 4531),
    e(12, 2, true, true, 4522, 4524),
    f(26, 2, false, true, 4537, 4539),
    g(49, 3, false, false, 4548, 4550),
    h(49, 2, false, false, 4701, 4702),
    i(49, 3, false, false, 9064, 9065),
    j(65, 2, false, false, 5014, 5013);

    private int firemakingLevelRequirement;
    private int unlitItemId;
    private int litItemId;
    private int lightLevel;
    private boolean flaresInSwampGas;
    private static Map definitionsByItemId;

    static {
        definitionsByItemId = new HashMap();
        CaveLightSourceDefinition[] caveLightSourceDefinitionArray = CaveLightSourceDefinition.values();
        int n = caveLightSourceDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            CaveLightSourceDefinition caveLightSourceDefinition = caveLightSourceDefinitionArray[n2];
            definitionsByItemId.put(caveLightSourceDefinition.unlitItemId, caveLightSourceDefinition);
            definitionsByItemId.put(caveLightSourceDefinition.litItemId, caveLightSourceDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private CaveLightSourceDefinition(int n2, int n3, boolean bl, boolean bl2, int n4, int n5) {
        this.firemakingLevelRequirement = n2;
        this.unlitItemId = n4;
        this.litItemId = n5;
        this.lightLevel = n3;
        this.flaresInSwampGas = bl;
    }

    public final int getFiremakingLevelRequirement() {
        return this.firemakingLevelRequirement;
    }

    public final int getUnlitItemId() {
        return this.unlitItemId;
    }

    public final int getLitItemId() {
        return this.litItemId;
    }

    public final int getLightLevel() {
        return this.lightLevel;
    }

    public final boolean canFlareInSwampGas() {
        return this.flaresInSwampGas;
    }

    public static CaveLightSourceDefinition forItemId(int n) {
        return (CaveLightSourceDefinition)((Object)definitionsByItemId.get(n));
    }
}

