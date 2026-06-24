/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import com.rs2.util.CacheableNode;
import com.rs2.util.LinkedNodeList;

public final class ByteArrayReader
extends CacheableNode {
    private byte[] buffer;
    public int position;

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
        new LinkedNodeList();
    }

    private ByteArrayReader() {
    }

    public ByteArrayReader(byte[] byArray) {
        this.buffer = byArray;
        this.position = 0;
    }

    public final int readUnsignedByte() {
        return this.buffer[this.position++] & 0xFF;
    }

    public final byte readByte() {
        return this.buffer[this.position++];
    }

    public final int readUnsignedShort() {
        this.position += 2;
        return ((this.buffer[this.position - 2] & 0xFF) << 8) + (this.buffer[this.position - 1] & 0xFF);
    }

    public final int readShort() {
        this.position += 2;
        int n = ((this.buffer[this.position - 2] & 0xFF) << 8) + (this.buffer[this.position - 1] & 0xFF);
        if (n > Short.MAX_VALUE) {
            n -= 65536;
        }
        return n;
    }

    public final int readInt() {
        this.position += 4;
        return ((this.buffer[this.position - 4] & 0xFF) << 24) + ((this.buffer[this.position - 3] & 0xFF) << 16) + ((this.buffer[this.position - 2] & 0xFF) << 8) + (this.buffer[this.position - 1] & 0xFF);
    }

    public final long readLong() {
        long l = (long)this.readInt() & 0xFFFFFFFFL;
        long l2 = (long)this.readInt() & 0xFFFFFFFFL;
        return (l << 32) + l2;
    }

    public final String readString() {
        int n = this.position;
        while (this.buffer[this.position++] != 10) {
        }
        return new String(this.buffer, n, this.position - n - 1);
    }

    public final byte[] readLineBytes() {
        int n = this.position;
        while (this.buffer[this.position++] != 10) {
        }
        byte[] byArray = new byte[this.position - n - 1];
        System.arraycopy(this.buffer, n, byArray, n - n, this.position - 1 - n);
        return byArray;
    }
}

