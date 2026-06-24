/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet;

import com.rs2.net.packet.AccessMode;
import com.rs2.net.packet.PacketReader;
import com.rs2.net.packet.PacketWriter;
import java.nio.ByteBuffer;

public abstract class PacketBuffer {
    public static final int[] BIT_MASKS;
    private AccessMode accessMode = AccessMode.BYTE_ACCESS;
    private int bitPosition = 0;

    static {
        int[] nArray = new int[33];
        nArray[1] = 1;
        nArray[2] = 3;
        nArray[3] = 7;
        nArray[4] = 15;
        nArray[5] = 31;
        nArray[6] = 63;
        nArray[7] = 127;
        nArray[8] = 255;
        nArray[9] = 511;
        nArray[10] = 1023;
        nArray[11] = 2047;
        nArray[12] = 4095;
        nArray[13] = 8191;
        nArray[14] = 16383;
        nArray[15] = Short.MAX_VALUE;
        nArray[16] = 65535;
        nArray[17] = 131071;
        nArray[18] = 262143;
        nArray[19] = 524287;
        nArray[20] = 1048575;
        nArray[21] = 0x1FFFFF;
        nArray[22] = 0x3FFFFF;
        nArray[23] = 0x7FFFFF;
        nArray[24] = 0xFFFFFF;
        nArray[25] = 0x1FFFFFF;
        nArray[26] = 0x3FFFFFF;
        nArray[27] = 0x7FFFFFF;
        nArray[28] = 0xFFFFFFF;
        nArray[29] = 0x1FFFFFFF;
        nArray[30] = 0x3FFFFFFF;
        nArray[31] = Integer.MAX_VALUE;
        nArray[32] = -1;
        BIT_MASKS = nArray;
    }

    public static final PacketReader wrapReader(ByteBuffer byteBuffer) {
        return new PacketReader(byteBuffer, 0);
    }

    public static final PacketWriter allocateWriter(int n) {
        return new PacketWriter(n, 0);
    }

    abstract void onAccessModeChanged(AccessMode var1);

    public final void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
        this.onAccessModeChanged(accessMode);
    }

    public final AccessMode getAccessMode() {
        return this.accessMode;
    }

    public final void setBitPosition(int n) {
        this.bitPosition = n;
    }

    public final int getBitPosition() {
        return this.bitPosition;
    }
}

