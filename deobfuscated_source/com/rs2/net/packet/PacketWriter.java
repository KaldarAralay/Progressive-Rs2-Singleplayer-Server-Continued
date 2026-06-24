/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet;

import com.rs2.net.IsaacCipher;
import com.rs2.net.packet.AccessMode;
import com.rs2.net.packet.ByteOrder;
import com.rs2.net.packet.ByteTransform;
import com.rs2.net.packet.PacketBuffer;
import java.nio.ByteBuffer;

public final class PacketWriter
extends PacketBuffer {
    private ByteBuffer buffer;
    private int lengthPlaceholderPosition = 0;

    private PacketWriter(int n) {
        this.buffer = ByteBuffer.allocate(n);
    }

    @Override
    final void onAccessModeChanged(AccessMode accessMode) {
        switch (accessMode) {
            case BIT_ACCESS: {
                this.setBitPosition(this.buffer.position() << 3);
                return;
            }
            case BYTE_ACCESS: {
                this.buffer.position((this.getBitPosition() + 7) / 8);
            }
        }
    }

    public final void writeOpcode(IsaacCipher isaacCipher, int n) {
        this.writeByte(n + isaacCipher.nextInt());
    }

    public final void startVariableBytePacket(IsaacCipher isaacCipher, int n) {
        this.writeOpcode(isaacCipher, n);
        this.lengthPlaceholderPosition = this.buffer.position();
        this.writeByte(0);
    }

    public final void startVariableShortPacket(IsaacCipher isaacCipher, int n) {
        this.writeOpcode(isaacCipher, n);
        this.lengthPlaceholderPosition = this.buffer.position();
        this.writeShort(0);
    }

    public final void finishVariableBytePacket() {
        this.buffer.put(this.lengthPlaceholderPosition, (byte)(this.buffer.position() - this.lengthPlaceholderPosition - 1));
    }

    public final void finishVariableShortPacket() {
        this.buffer.putShort(this.lengthPlaceholderPosition, (short)(this.buffer.position() - this.lengthPlaceholderPosition - 2));
    }

    public final void writeBuffer(ByteBuffer byteBuffer) {
        int n = 0;
        while (n < byteBuffer.position()) {
            this.writeByte(byteBuffer.get(n));
            ++n;
        }
    }

    public final void writeBytes(byte[] byArray, int n) {
        this.buffer.put(byArray, 0, n);
    }

    public final void writeBits(int n, int n2) {
        if (this.getAccessMode() != AccessMode.BIT_ACCESS) {
            throw new IllegalStateException("Illegal access type.");
        }
        if (n < 0 || n > 32) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 32 inclusive.");
        }
        int n3 = this.getBitPosition() >> 3;
        int n4 = 8 - (this.getBitPosition() & 7);
        this.setBitPosition(this.getBitPosition() + n);
        int n5 = n3 - this.buffer.position() + 1;
        if (this.buffer.remaining() < (n5 += (n + 7) / 8)) {
            ByteBuffer byteBuffer = this.buffer;
            this.buffer = ByteBuffer.allocate(byteBuffer.capacity() + n5);
            byteBuffer.flip();
            this.buffer.put(byteBuffer);
        }
        while (n > n4) {
            byte by = this.buffer.get(n3);
            by = (byte)(by & ~BIT_MASKS[n4]);
            by = (byte)(by | n2 >> n - n4 & BIT_MASKS[n4]);
            this.buffer.put(n3++, by);
            n -= n4;
            n4 = 8;
        }
        if (n == n4) {
            byte by = this.buffer.get(n3);
            by = (byte)(by & ~BIT_MASKS[n4]);
            by = (byte)(by | n2 & BIT_MASKS[n4]);
            this.buffer.put(n3, by);
            return;
        }
        byte by = this.buffer.get(n3);
        by = (byte)(by & ~(BIT_MASKS[n] << n4 - n));
        by = (byte)(by | (n2 & BIT_MASKS[n]) << n4 - n);
        this.buffer.put(n3, by);
    }

    public final void writeBoolean(boolean bl) {
        this.writeBits(1, bl ? 1 : 0);
    }

    public final void writeByte(int n, ByteTransform byteTransform) {
        if (this.getAccessMode() != AccessMode.BYTE_ACCESS) {
            throw new IllegalStateException("Illegal access type.");
        }
        switch (byteTransform) {
            case ADD: {
                n += 128;
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
        this.buffer.put((byte)n);
    }

    public final void writeByte(int n) {
        this.writeByte(n, ByteTransform.NONE);
    }

    public final void writeShort(int n, ByteTransform byteTransform, ByteOrder byteOrder) {
        switch (byteOrder) {
            case BIG: {
                this.writeByte(n >> 8);
                this.writeByte(n, byteTransform);
                return;
            }
            case MIDDLE: {
                throw new IllegalArgumentException("Middle-endian short is impossible!");
            }
            case INVERSE_MIDDLE: {
                throw new IllegalArgumentException("Inverse-middle-endian short is impossible!");
            }
            case LITTLE: {
                this.writeByte(n, byteTransform);
                this.writeByte(n >> 8);
            }
        }
    }

    public final void writeShort(int n) {
        this.writeShort(n, ByteTransform.NONE, ByteOrder.BIG);
    }

    public final void writeShort(int n, ByteTransform byteTransform) {
        this.writeShort(n, byteTransform, ByteOrder.BIG);
    }

    public final void writeShort(int n, ByteOrder byteOrder) {
        this.writeShort(n, ByteTransform.NONE, byteOrder);
    }

    private void writeInt(int n, ByteTransform byteTransform, ByteOrder byteOrder) {
        switch (byteOrder) {
            case BIG: {
                this.writeByte(n >> 24);
                this.writeByte(n >> 16);
                this.writeByte(n >> 8);
                this.writeByte(n, byteTransform);
                return;
            }
            case MIDDLE: {
                this.writeByte(n >> 8);
                this.writeByte(n, byteTransform);
                this.writeByte(n >> 24);
                this.writeByte(n >> 16);
                return;
            }
            case INVERSE_MIDDLE: {
                this.writeByte(n >> 16);
                this.writeByte(n >> 24);
                this.writeByte(n, byteTransform);
                this.writeByte(n >> 8);
                return;
            }
            case LITTLE: {
                this.writeByte(n, byteTransform);
                this.writeByte(n >> 8);
                this.writeByte(n >> 16);
                this.writeByte(n >> 24);
            }
        }
    }

    public final void writeInt(int n) {
        this.writeInt(n, ByteTransform.NONE, ByteOrder.BIG);
    }

    public final void writeInt(int n, ByteOrder byteOrder) {
        this.writeInt(n, ByteTransform.NONE, byteOrder);
    }

    public final void writeLong(long l) {
        ByteOrder byteOrder = ByteOrder.BIG;
        ByteTransform byteTransform = ByteTransform.NONE;
        long l2 = l;
        PacketWriter packetWriter = this;
        switch (byteOrder) {
            case BIG: {
                packetWriter.writeByte((int)(l2 >> 56));
                packetWriter.writeByte((int)(l2 >> 48));
                packetWriter.writeByte((int)(l2 >> 40));
                packetWriter.writeByte((int)(l2 >> 32));
                packetWriter.writeByte((int)(l2 >> 24));
                packetWriter.writeByte((int)(l2 >> 16));
                packetWriter.writeByte((int)(l2 >> 8));
                packetWriter.writeByte((int)l2, byteTransform);
                return;
            }
            case MIDDLE: {
                throw new UnsupportedOperationException("Middle-endian long is not implemented!");
            }
            case INVERSE_MIDDLE: {
                throw new UnsupportedOperationException("Inverse-middle-endian long is not implemented!");
            }
            case LITTLE: {
                packetWriter.writeByte((int)l2, byteTransform);
                packetWriter.writeByte((int)(l2 >> 8));
                packetWriter.writeByte((int)(l2 >> 16));
                packetWriter.writeByte((int)(l2 >> 24));
                packetWriter.writeByte((int)(l2 >> 32));
                packetWriter.writeByte((int)(l2 >> 40));
                packetWriter.writeByte((int)(l2 >> 48));
                packetWriter.writeByte((int)(l2 >> 56));
            }
        }
    }

    public final void writeString(String string) {
        byte[] byArray = string.getBytes();
        int n = byArray.length;
        int n2 = 0;
        while (n2 < n) {
            byte by = byArray[n2];
            this.writeByte(by);
            ++n2;
        }
        this.writeByte(10);
    }

    public final ByteBuffer getBuffer() {
        return this.buffer;
    }

    /* synthetic */ PacketWriter(int n, byte by) {
        this(n);
    }

    PacketWriter(int n, int n2) {
        this(n);
    }
}

