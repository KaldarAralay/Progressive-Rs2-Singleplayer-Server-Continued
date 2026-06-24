/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet;

import com.rs2.net.packet.AccessMode;
import com.rs2.net.packet.ByteOrder;
import com.rs2.net.packet.ByteTransform;
import com.rs2.net.packet.PacketBuffer;
import java.nio.ByteBuffer;

public final class PacketReader
extends PacketBuffer {
    private ByteBuffer buffer;

    private PacketReader(ByteBuffer byteBuffer) {
        this.buffer = byteBuffer;
    }

    @Override
    final void onAccessModeChanged(AccessMode accessMode) {
        if (accessMode == AccessMode.BIT_ACCESS) {
            throw new UnsupportedOperationException("Reading bits is not implemented!");
        }
    }

    public final int readByte(boolean bl, ByteTransform byteTransform) {
        int n = this.buffer.get();
        switch (byteTransform) {
            case ADD: {
                n -= 128;
                break;
            }
            case NEGATE: {
                n = -n;
                break;
            }
            case SUBTRACT: {
                n = 128 - n;
            }
        }
        if (bl) {
            return n;
        }
        return n & 0xFF;
    }

    public final int readSignedByte() {
        return this.readByte(true, ByteTransform.NONE);
    }

    public final int readUnsignedByte(boolean bl) {
        return this.readByte(false, ByteTransform.NONE);
    }

    public final int readSignedByte(ByteTransform byteTransform) {
        return this.readByte(true, byteTransform);
    }

    public final int readShort(boolean bl, ByteTransform byteTransform, ByteOrder byteOrder) {
        int n = 0;
        switch (byteOrder) {
            case BIG: {
                n = 0 | this.readUnsignedByte(false) << 8;
                n |= this.readByte(false, byteTransform);
                break;
            }
            case MIDDLE: {
                throw new UnsupportedOperationException("Middle-endian short is impossible!");
            }
            case INVERSE_MIDDLE: {
                throw new UnsupportedOperationException("Inverse-middle-endian short is impossible!");
            }
            case LITTLE: {
                n = 0 | this.readByte(false, byteTransform);
                n |= this.readUnsignedByte(false) << 8;
            }
        }
        if (bl) {
            return n;
        }
        return n & 0xFFFF;
    }

    public final int readSignedShort() {
        return this.readShort(true, ByteTransform.NONE, ByteOrder.BIG);
    }

    public final int readSignedShort(boolean bl) {
        return this.readShort(true, ByteTransform.NONE, ByteOrder.BIG);
    }

    public final int readSignedShort(ByteTransform byteTransform) {
        return this.readShort(true, byteTransform, ByteOrder.BIG);
    }

    public final int readSignedShort(boolean bl, ByteTransform byteTransform) {
        return this.readShort(true, byteTransform, ByteOrder.BIG);
    }

    public final int readSignedShort(ByteOrder byteOrder) {
        return this.readShort(true, ByteTransform.NONE, byteOrder);
    }

    public final int readSignedShort(boolean bl, ByteOrder byteOrder) {
        return this.readShort(true, ByteTransform.NONE, byteOrder);
    }

    public final int readSignedShort(ByteTransform byteTransform, ByteOrder byteOrder) {
        return this.readShort(true, byteTransform, byteOrder);
    }

    public final int readInt() {
        ByteOrder byteOrder = ByteOrder.BIG;
        ByteTransform byteTransform = ByteTransform.NONE;
        boolean bl = true;
        PacketReader packetReader = this;
        long l = 0L;
        switch (byteOrder) {
            case BIG: {
                l = 0L | (long)(packetReader.readUnsignedByte(false) << 24);
                l |= (long)(packetReader.readUnsignedByte(false) << 16);
                l |= (long)(packetReader.readUnsignedByte(false) << 8);
                l |= (long)packetReader.readByte(false, byteTransform);
                break;
            }
            case MIDDLE: {
                l = 0L | (long)(packetReader.readUnsignedByte(false) << 8);
                l |= (long)packetReader.readByte(false, byteTransform);
                l |= (long)(packetReader.readUnsignedByte(false) << 24);
                l |= (long)(packetReader.readUnsignedByte(false) << 16);
                break;
            }
            case INVERSE_MIDDLE: {
                l = 0L | (long)(packetReader.readUnsignedByte(false) << 16);
                l |= (long)(packetReader.readUnsignedByte(false) << 24);
                l |= (long)packetReader.readByte(false, byteTransform);
                l |= (long)(packetReader.readUnsignedByte(false) << 8);
                break;
            }
            case LITTLE: {
                l = 0L | (long)packetReader.readByte(false, byteTransform);
                l |= (long)(packetReader.readUnsignedByte(false) << 8);
                l |= (long)(packetReader.readUnsignedByte(false) << 16);
                l |= (long)(packetReader.readUnsignedByte(false) << 24);
            }
        }
        return (int)l;
    }

    public final long readLong() {
        ByteOrder byteOrder = ByteOrder.BIG;
        ByteTransform byteTransform = ByteTransform.NONE;
        PacketReader packetReader = this;
        long l = 0L;
        switch (byteOrder) {
            case BIG: {
                l = 0L | (long)packetReader.readUnsignedByte(false) << 56;
                l |= (long)packetReader.readUnsignedByte(false) << 48;
                l |= (long)packetReader.readUnsignedByte(false) << 40;
                l |= (long)packetReader.readUnsignedByte(false) << 32;
                l |= (long)packetReader.readUnsignedByte(false) << 24;
                l |= (long)packetReader.readUnsignedByte(false) << 16;
                l |= (long)packetReader.readUnsignedByte(false) << 8;
                l |= (long)packetReader.readByte(false, byteTransform);
                break;
            }
            case MIDDLE: {
                throw new UnsupportedOperationException("middle-endian long is not implemented!");
            }
            case INVERSE_MIDDLE: {
                throw new UnsupportedOperationException("inverse-middle-endian long is not implemented!");
            }
            case LITTLE: {
                l = 0L | (long)packetReader.readByte(false, byteTransform);
                l |= (long)packetReader.readUnsignedByte(false) << 8;
                l |= (long)packetReader.readUnsignedByte(false) << 16;
                l |= (long)packetReader.readUnsignedByte(false) << 24;
                l |= (long)packetReader.readUnsignedByte(false) << 32;
                l |= (long)packetReader.readUnsignedByte(false) << 40;
                l |= (long)packetReader.readUnsignedByte(false) << 48;
                l |= (long)packetReader.readUnsignedByte(false) << 56;
            }
        }
        return l;
    }

    public final String readString() {
        byte by;
        StringBuilder stringBuilder = new StringBuilder();
        while ((by = (byte)this.readSignedByte()) != 10) {
            stringBuilder.append((char)by);
        }
        return stringBuilder.toString();
    }

    public final byte[] readBytes(int n) {
        ByteTransform byteTransform = ByteTransform.NONE;
        int n2 = n;
        PacketReader packetReader = this;
        byte[] byArray = new byte[n2];
        int n3 = 0;
        while (n3 < n2) {
            ByteTransform byteTransform2 = byteTransform;
            PacketReader packetReader2 = packetReader;
            byArray[n3] = (byte)packetReader2.readByte(true, byteTransform2);
            ++n3;
        }
        return byArray;
    }

    public final byte[] readBytesReverse(int n, ByteTransform byteTransform) {
        byte[] byArray = new byte[n];
        int n2 = 0;
        n = this.buffer.position() + n - 1;
        while (n >= this.buffer.position()) {
            int n3 = this.buffer.get(n);
            switch (byteTransform) {
                case ADD: {
                    n3 -= 128;
                    break;
                }
                case NEGATE: {
                    n3 = -n3;
                    break;
                }
                case SUBTRACT: {
                    n3 = 128 - n3;
                }
            }
            byArray[n2++] = (byte)n3;
            --n;
        }
        return byArray;
    }

    public final ByteBuffer getBuffer() {
        return this.buffer;
    }

    /* synthetic */ PacketReader(ByteBuffer byteBuffer, byte by) {
        this(byteBuffer);
    }

    PacketReader(ByteBuffer byteBuffer, int n) {
        this(byteBuffer);
    }
}

