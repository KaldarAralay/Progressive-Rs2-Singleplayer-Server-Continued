/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.music;

import com.rs2.CacheCoordinateTranslator;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import com.rs2.util.GameUtil;
import com.rs2.util.RectangularArea;

public final class MusicAreaDefinition {
    private int areaId;
    private int regionCount;
    private int priority;
    private int trackId;
    private RectangularArea areaBounds;
    private int[] regionIds;
    public static int areaCount = 0;
    private static MusicAreaDefinition[] definitionsByAreaId = new MusicAreaDefinition[0];

    public static void loadDefinitions() {
        Object object = FileUtil.readBytes("./data/areas/Music.dat");
        ByteArrayReader byteArrayReader = new ByteArrayReader((byte[])object);
        object = byteArrayReader;
        areaCount = byteArrayReader.readUnsignedShort();
        definitionsByAreaId = new MusicAreaDefinition[areaCount];
        int n = 0;
        while (n < areaCount) {
            int n2;
            int n3;
            int n4;
            int[] nArray = null;
            RectangularArea rectangularArea = null;
            int n5 = ((ByteArrayReader)object).readUnsignedByte();
            int n6 = ((ByteArrayReader)object).readUnsignedByte();
            int n7 = ((ByteArrayReader)object).readUnsignedShort();
            if (n5 == 0) {
                int n8 = ((ByteArrayReader)object).readUnsignedShort();
                n4 = ((ByteArrayReader)object).readUnsignedShort();
                n3 = ((ByteArrayReader)object).readUnsignedShort();
                n2 = ((ByteArrayReader)object).readUnsignedShort();
                rectangularArea = new RectangularArea(n8, n4, n3, n2, 0);
            } else {
                nArray = new int[n5];
                n4 = 0;
                while (n4 < n5) {
                    nArray[n4] = ((ByteArrayReader)object).readUnsignedShort();
                    ++n4;
                }
            }
            if (ServerSettings.cacheVersion < 319 && n7 == 28) {
                n5 = 0;
                n6 = 1;
                rectangularArea = new RectangularArea(2706, 9801, 2731, 9830, 0);
            }
            if (CacheCoordinateTranslator.dungeonCoordinateShiftActive) {
                if (n7 == 181 || n7 == 118) {
                    int[] nArray2 = new int[nArray.length];
                    n3 = 0;
                    while (n3 < nArray.length) {
                        Position position = GameUtil.getRegionBasePosition(nArray[n3]);
                        int n9 = position.getX() + 768;
                        n2 = position.getY() + 5120;
                        nArray2[n3] = n2 = GameUtil.getRegionId(n9, n2);
                        ++n3;
                    }
                    nArray = nArray2;
                }
                if (n7 == 389) {
                    n5 = 0;
                    n6 = 0;
                    rectangularArea = new RectangularArea(0, 0, 0, 0, 0);
                }
            }
            MusicAreaDefinition.definitionsByAreaId[n] = new MusicAreaDefinition(n, n5, n6, n7, rectangularArea, nArray);
            ++n;
        }
    }

    private MusicAreaDefinition(int n, int n2, int n3, int n4, RectangularArea rectangularArea, int[] nArray) {
        this.areaId = n;
        this.regionCount = n2;
        this.priority = n3;
        this.trackId = n4;
        this.areaBounds = rectangularArea;
        this.regionIds = nArray;
    }

    public final int getAreaId() {
        return this.areaId;
    }

    public final int getRegionCount() {
        return this.regionCount;
    }

    public final int getPriority() {
        return this.priority;
    }

    public final int getTrackId() {
        return this.trackId;
    }

    public final RectangularArea getAreaBounds() {
        return this.areaBounds;
    }

    public final int[] getRegionIds() {
        return this.regionIds;
    }

    public static MusicAreaDefinition forAreaId(int n) {
        MusicAreaDefinition musicAreaDefinition;
        if (n < 0) {
            n = 1;
        }
        if ((musicAreaDefinition = definitionsByAreaId[n]) == null) {
            musicAreaDefinition = new MusicAreaDefinition(n, 0, 0, 0, null, null);
        }
        return musicAreaDefinition;
    }
}

