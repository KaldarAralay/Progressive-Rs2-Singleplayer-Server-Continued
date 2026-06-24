/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class CountingDataOutputStream
extends FilterOutputStream
implements DataOutput {
    private int bytesWritten;
    private byte[] scratch = new byte[8];

    public CountingDataOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    public final void flush() throws IOException {
        super.flush();
    }

    @Override
    public final void write(byte[] byArray, int n, int n2) throws IOException {
        this.out.write(byArray, n, n2);
        this.bytesWritten += n2;
    }

    @Override
    public final void write(int n) throws IOException {
        this.out.write(n);
        ++this.bytesWritten;
    }

    @Override
    public final void writeBoolean(boolean bl) throws IOException {
        this.out.write(bl ? 1 : 0);
        ++this.bytesWritten;
    }

    public final void writeUnsignedByte(int n) throws IOException {
        this.out.write(n & 0xFF);
        ++this.bytesWritten;
    }

    @Override
    public final void writeByte(int n) throws IOException {
        this.out.write(n);
        ++this.bytesWritten;
    }

    @Override
    public final void writeBytes(String string) throws IOException {
        if (string.length() == 0) {
            return;
        }
        byte[] byArray = new byte[string.length()];
        int n = 0;
        while (n < string.length()) {
            byArray[n] = (byte)string.charAt(n);
            ++n;
        }
        this.out.write(byArray);
        this.bytesWritten += byArray.length;
    }

    @Override
    public final void writeChar(int n) throws IOException {
        this.scratch[0] = (byte)(n >> 8);
        this.scratch[1] = (byte)n;
        this.out.write(this.scratch, 0, 2);
        this.bytesWritten += 2;
    }

    @Override
    public final void writeChars(String string) throws IOException {
        byte[] byArray = new byte[string.length() << 1];
        int n = 0;
        while (n < string.length()) {
            int n2 = n == 0 ? n : n << 1;
            byArray[n2] = (byte)(string.charAt(n) >> 8);
            byArray[n2 + 1] = (byte)string.charAt(n);
            ++n;
        }
        this.out.write(byArray);
        this.bytesWritten += byArray.length;
    }

    @Override
    public final void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    @Override
    public final void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    @Override
    public final void writeInt(int n) throws IOException {
        this.scratch[0] = (byte)(n >> 24);
        this.scratch[1] = (byte)(n >> 16);
        this.scratch[2] = (byte)(n >> 8);
        this.scratch[3] = (byte)n;
        this.out.write(this.scratch, 0, 4);
        this.bytesWritten += 4;
    }

    @Override
    public final void writeLong(long l) throws IOException {
        this.scratch[0] = (byte)(l >> 56);
        this.scratch[1] = (byte)(l >> 48);
        this.scratch[2] = (byte)(l >> 40);
        this.scratch[3] = (byte)(l >> 32);
        this.scratch[4] = (byte)(l >> 24);
        this.scratch[5] = (byte)(l >> 16);
        this.scratch[6] = (byte)(l >> 8);
        this.scratch[7] = (byte)l;
        this.out.write(this.scratch, 0, 8);
        this.bytesWritten += 8;
    }

    @Override
    public final void writeShort(int n) throws IOException {
        this.scratch[0] = (byte)(n >> 8);
        this.scratch[1] = (byte)n;
        this.out.write(this.scratch, 0, 2);
        this.bytesWritten += 2;
    }

    @Override
    public final void writeUTF(String string) throws IOException {
        int n;
        String string2 = string;
        int n2 = 0;
        int n3 = string2.length();
        int n4 = 0;
        while (n4 < n3) {
            n = string2.charAt(n4);
            n2 = n > 0 && n <= 127 ? ++n2 : (n <= 2047 ? (n2 += 2) : (n2 += 3));
            ++n4;
        }
        long l = n2;
        byte[] byArray = new byte[(int)l + 2];
        n2 = 0;
        byte[] byArray2 = byArray;
        int n5 = (int)l;
        byArray2[0] = (byte)(n5 >> 8);
        byArray2[1] = (byte)n5;
        n2 = n5 = 2;
        byArray2 = byArray;
        String string3 = string;
        int n6 = string3.length();
        n = 0;
        while (n < n6) {
            char c = string3.charAt(n);
            if (c > '\u0000' && c <= '\u007f') {
                byArray2[n2++] = (byte)c;
            } else if (c <= '\u07ff') {
                byArray2[n2++] = (byte)(0xC0 | 0x1F & c >> 6);
                byArray2[n2++] = (byte)(0x80 | 0x3F & c);
            } else {
                byArray2[n2++] = (byte)(0xE0 | 0xF & c >> 12);
                byArray2[n2++] = (byte)(0x80 | 0x3F & c >> 6);
                byArray2[n2++] = (byte)(0x80 | 0x3F & c);
            }
            ++n;
        }
        int n7 = n2;
        this.write(byArray, 0, n7);
    }
}

