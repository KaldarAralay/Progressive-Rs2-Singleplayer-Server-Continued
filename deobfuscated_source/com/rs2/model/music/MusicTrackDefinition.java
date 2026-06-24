/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.music;

import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;

public final class MusicTrackDefinition {
    private int trackId;
    private String name;
    private int buttonId;
    private int unlockConfigId;
    private int unlockBitMask;
    public static int trackCount = 0;
    private static MusicTrackDefinition[] definitionsByTrackId = new MusicTrackDefinition[0];

    public static void loadDefinitions() {
        Object object = FileUtil.readBytes("./data/Songs.dat");
        ByteArrayReader byteArrayReader = new ByteArrayReader((byte[])object);
        object = byteArrayReader;
        trackCount = byteArrayReader.readUnsignedShort();
        definitionsByTrackId = new MusicTrackDefinition[trackCount];
        int n = 0;
        while (n < trackCount) {
            int n2;
            int n3;
            int n4;
            String string;
            int n5 = ((ByteArrayReader)object).readUnsignedByte();
            if (n5 == 0) {
                MusicTrackDefinition.definitionsByTrackId[n] = new MusicTrackDefinition(n, " ", -1, -1, -1);
            }
            if (n5 == 1) {
                string = ((ByteArrayReader)object).readString();
                MusicTrackDefinition.definitionsByTrackId[n] = new MusicTrackDefinition(n, string, -1, -1, -1);
            }
            if (n5 == 2) {
                string = ((ByteArrayReader)object).readString();
                n4 = ((ByteArrayReader)object).readUnsignedShort();
                n3 = ((ByteArrayReader)object).readUnsignedByte();
                n2 = (int)Math.pow(2.0, n3 - 1);
                if (n3 == 32) {
                    n2 = Integer.MIN_VALUE;
                }
                n3 = ((ByteArrayReader)object).readUnsignedShort();
                MusicTrackDefinition.definitionsByTrackId[n] = new MusicTrackDefinition(n, string, n3, n4, n2);
            }
            if (n5 == 3) {
                string = ((ByteArrayReader)object).readString();
                n3 = ((ByteArrayReader)object).readUnsignedShort();
                MusicTrackDefinition.definitionsByTrackId[n] = new MusicTrackDefinition(n, string, n3, -1, -1);
            }
            if (n5 == 4) {
                string = ((ByteArrayReader)object).readString();
                n4 = ((ByteArrayReader)object).readUnsignedShort();
                n3 = ((ByteArrayReader)object).readUnsignedByte();
                n2 = (int)Math.pow(2.0, n3 - 1);
                if (n3 == 32) {
                    n2 = Integer.MIN_VALUE;
                }
                MusicTrackDefinition.definitionsByTrackId[n] = new MusicTrackDefinition(n, string, -1, n4, n2);
            }
            ++n;
        }
    }

    private MusicTrackDefinition(int n, String string, int n2, int n3, int n4) {
        this.trackId = n;
        this.name = string;
        this.buttonId = n2;
        this.unlockConfigId = n3;
        this.unlockBitMask = n4;
    }

    public final int getTrackId() {
        return this.trackId;
    }

    public final String getName() {
        return this.name;
    }

    public final int getButtonId() {
        return this.buttonId;
    }

    public final int getUnlockConfigId() {
        return this.unlockConfigId;
    }

    public final int getUnlockBitMask() {
        return this.unlockBitMask;
    }

    public static MusicTrackDefinition forTrackId(int n) {
        MusicTrackDefinition musicTrackDefinition;
        if (n < 0) {
            n = 1;
        }
        if ((musicTrackDefinition = definitionsByTrackId[n]) == null) {
            musicTrackDefinition = new MusicTrackDefinition(n, " ", -1, -1, -1);
        }
        return musicTrackDefinition;
    }
}

