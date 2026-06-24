/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;

public enum CropStorageDefinition {
    POTATO(5420, 1942, true),
    ONION(5440, 1957, true),
    CABBAGE(5460, 1965, true),
    APPLE(5378, 1955, false),
    ORANGE(5388, 2108, false),
    STRAWBERRY(5398, 5504, false),
    BANANA(5408, 1963, false),
    TOMATO(5960, 1982, false);

    private int baseContainerItemId;
    private int produceItemId;
    private boolean sack;
    private static HashMap definitionsByProduceItemId;

    static {
        definitionsByProduceItemId = new HashMap();
        CropStorageDefinition[] cropStorageDefinitionArray = CropStorageDefinition.values();
        int n = cropStorageDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            CropStorageDefinition cropStorageDefinition;
            CropStorageDefinition cropStorageDefinition2 = cropStorageDefinition = cropStorageDefinitionArray[n2];
            definitionsByProduceItemId.put(cropStorageDefinition2.produceItemId, cropStorageDefinition);
            ++n2;
        }
    }

    public static CropStorageDefinition forProduceItemId(int n) {
        return (CropStorageDefinition)((Object)definitionsByProduceItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private CropStorageDefinition(int n2, int n3, boolean bl) {
        this.baseContainerItemId = n2;
        this.produceItemId = n3;
        this.sack = bl;
    }

    public final int getBaseContainerItemId() {
        return this.baseContainerItemId;
    }

    public final int getProduceItemId() {
        return this.produceItemId;
    }

    public final boolean isSack() {
        return this.sack;
    }

    static /* synthetic */ boolean isSack(CropStorageDefinition cropStorageDefinition) {
        return cropStorageDefinition.sack;
    }
}

