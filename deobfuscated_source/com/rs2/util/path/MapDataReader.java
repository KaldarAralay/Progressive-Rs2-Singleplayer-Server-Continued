/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.path;

public final class MapDataReader {
    private byte[] data;
    private int offset;

    public MapDataReader(byte[] byArray) {
        this.data = byArray;
        this.offset = 0;
    }

    public final void skipByte(int n) {
        ++this.offset;
    }

    public final int readUnsignedByte() {
        return this.data[this.offset++] & 0xFF;
    }

    public final int readUnsignedSmart() {
        int n = this.data[this.offset] & 0xFF;
        if (n < 128) {
            return this.readUnsignedByte();
        }
        MapDataReader mapDataReader = this;
        return (mapDataReader.readUnsignedByte() << 8) + mapDataReader.readUnsignedByte() - 32768;
    }
}

