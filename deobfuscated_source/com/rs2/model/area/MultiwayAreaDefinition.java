/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.area;

import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import com.rs2.util.RectangularArea;

public final class MultiwayAreaDefinition {
    private int regionCount;
    private RectangularArea areaBounds;
    private int[] regionIds;
    private static int definitionCount = 0;
    public static MultiwayAreaDefinition[] definitions = new MultiwayAreaDefinition[0];

    public static void loadDefinitions() {
        Object object = FileUtil.readBytes("./data/areas/Multiway.dat");
        ByteArrayReader byteArrayReader = new ByteArrayReader((byte[])object);
        object = byteArrayReader;
        definitionCount = byteArrayReader.readUnsignedShort();
        definitions = new MultiwayAreaDefinition[definitionCount];
        int n = 0;
        while (n < definitionCount) {
            int n2;
            int[] nArray = null;
            RectangularArea rectangularArea = null;
            int n3 = ((ByteArrayReader)object).readUnsignedByte();
            if (n3 == 0) {
                int n4 = ((ByteArrayReader)object).readUnsignedShort();
                n2 = ((ByteArrayReader)object).readUnsignedShort();
                int n5 = ((ByteArrayReader)object).readUnsignedShort();
                int n6 = ((ByteArrayReader)object).readUnsignedShort();
                rectangularArea = new RectangularArea(n4, n2, n5, n6, 0);
            } else {
                nArray = new int[n3];
                n2 = 0;
                while (n2 < n3) {
                    nArray[n2] = ((ByteArrayReader)object).readUnsignedShort();
                    ++n2;
                }
            }
            MultiwayAreaDefinition.definitions[n] = new MultiwayAreaDefinition(n, n3, rectangularArea, nArray);
            ++n;
        }
    }

    private MultiwayAreaDefinition(int n, int n2, RectangularArea rectangularArea, int[] nArray) {
        this.regionCount = n2;
        this.areaBounds = rectangularArea;
        this.regionIds = nArray;
    }

    public final RectangularArea getAreaBounds() {
        return this.areaBounds;
    }

    public final int getRegionCount() {
        return this.regionCount;
    }

    public final int[] getRegionIds() {
        return this.regionIds;
    }

    public static MultiwayAreaDefinition forDefinitionId(int n) {
        MultiwayAreaDefinition multiwayAreaDefinition;
        if (n < 0) {
            n = 1;
        }
        if ((multiwayAreaDefinition = definitions[n]) == null) {
            multiwayAreaDefinition = new MultiwayAreaDefinition(n, 0, null, null);
        }
        return multiwayAreaDefinition;
    }
}

