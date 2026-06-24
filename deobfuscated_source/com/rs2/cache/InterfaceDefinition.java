/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.cache;

import com.rs2.ServerSettings;
import com.rs2.cache.CacheArchive;
import com.rs2.cache.CacheStore;
import com.rs2.net.packet.PacketBuffer;
import com.rs2.net.packet.PacketReader;

public final class InterfaceDefinition {
    public static int interfaceCount = 0;
    private static InterfaceDefinition[] definitionsById;
    private int parentInterfaceId;

    private InterfaceDefinition(int n, byte by, int n2, boolean bl) {
        this.parentInterfaceId = n2;
    }

    public final int getParentInterfaceId() {
        return this.parentInterfaceId;
    }

    public static InterfaceDefinition forId(int n) {
        if (n < 0 || n >= interfaceCount) {
            return null;
        }
        return definitionsById[n];
    }

    public static void loadDefinitions() {
        try {
            Object object = new CacheArchive(CacheStore.getInstance().readFile(0, 3));
            object = PacketBuffer.wrapReader(((CacheArchive)object).getFileBuffer("data"));
            interfaceCount = ((PacketReader)object).readSignedShort();
            definitionsById = new InterfaceDefinition[interfaceCount];
            int n = -1;
            while (((PacketReader)object).getBuffer().hasRemaining()) {
                int n2 = ((PacketReader)object).readSignedShort();
                if (n2 == 65535) {
                    n = ((PacketReader)object).readSignedShort();
                    n2 = ((PacketReader)object).readSignedShort();
                }
                int n3 = ((PacketReader)object).readSignedByte();
                int n4 = ((PacketReader)object).readSignedByte();
                ((PacketReader)object).readSignedShort();
                int n5 = ((PacketReader)object).readSignedShort();
                int n6 = ((PacketReader)object).readSignedShort();
                if (ServerSettings.cacheVersion > 237) {
                    ((PacketReader)object).readSignedByte();
                }
                int n7 = ((PacketReader)object).readSignedByte();
                int n8 = n5 = n5 == 512 && n6 == 334 ? 1 : 0;
                if (n2 < interfaceCount && n2 > 0) {
                    InterfaceDefinition.definitionsById[n2] = new InterfaceDefinition(n2, (byte)n3, n, n5 != 0);
                }
                if (n7 != 0) {
                    ((PacketReader)object).readSignedByte();
                }
                if ((n2 = ((PacketReader)object).readSignedByte()) > 0) {
                    ((PacketReader)object).readBytes(n2 * 3);
                }
                if ((n2 = ((PacketReader)object).readSignedByte()) > 0) {
                    n5 = 0;
                    while (n5 < n2) {
                        n6 = ((PacketReader)object).readSignedShort();
                        ((PacketReader)object).readBytes(n6 << 1);
                        ++n5;
                    }
                }
                if (n3 == 0) {
                    ((PacketReader)object).readSignedShort();
                    ((PacketReader)object).readSignedByte();
                    n2 = ((PacketReader)object).readSignedShort();
                    ((PacketReader)object).readBytes(n2 * 6);
                }
                if (n3 == 1) {
                    ((PacketReader)object).readBytes(3);
                }
                if (n3 == 2) {
                    n5 = 6;
                    if (ServerSettings.cacheVersion < 245) {
                        n5 = 5;
                    }
                    ((PacketReader)object).readBytes(n5);
                    n6 = 0;
                    while (n6 < 20) {
                        n2 = ((PacketReader)object).readSignedByte();
                        if (n2 == 1) {
                            ((PacketReader)object).readBytes(4);
                            ((PacketReader)object).readString();
                        }
                        ++n6;
                    }
                    n6 = 0;
                    while (n6 < 5) {
                        ((PacketReader)object).readString();
                        ++n6;
                    }
                }
                if (n3 == 3) {
                    ((PacketReader)object).readSignedByte();
                }
                if (n3 == 4 || n3 == 1) {
                    ((PacketReader)object).readBytes(3);
                }
                if (n3 == 4) {
                    ((PacketReader)object).readString();
                    ((PacketReader)object).readString();
                }
                if (n3 == 1 || n3 == 3 || n3 == 4) {
                    ((PacketReader)object).readInt();
                    if (n3 != 1) {
                        n5 = 12;
                        if (ServerSettings.cacheVersion < 245) {
                            n5 = 8;
                        }
                        ((PacketReader)object).readBytes(n5);
                    }
                }
                if (n3 == 5 || n3 == 17 || n3 == 18 || n3 == 19) {
                    ((PacketReader)object).readString();
                    ((PacketReader)object).readString();
                    if (n3 == 17) {
                        ((PacketReader)object).readString();
                        ((PacketReader)object).readString();
                    }
                }
                if (n3 == 6) {
                    n5 = ((PacketReader)object).readSignedByte();
                    if (n5 != 0) {
                        ((PacketReader)object).readSignedByte();
                    }
                    if ((n5 = ((PacketReader)object).readSignedByte()) != 0) {
                        ((PacketReader)object).readSignedByte();
                    }
                    if ((n5 = ((PacketReader)object).readSignedByte()) != 0) {
                        ((PacketReader)object).readSignedByte();
                    }
                    if ((n5 = ((PacketReader)object).readSignedByte()) != 0) {
                        ((PacketReader)object).readSignedByte();
                    }
                    ((PacketReader)object).readBytes(6);
                }
                if (n3 == 7) {
                    ((PacketReader)object).readBytes(12);
                    n5 = 0;
                    while (n5 < 5) {
                        ((PacketReader)object).readString();
                        ++n5;
                    }
                }
                if (n4 == 2 || n3 == 2) {
                    ((PacketReader)object).readString();
                    ((PacketReader)object).readString();
                    ((PacketReader)object).readSignedShort();
                }
                if (n3 == 8) {
                    ((PacketReader)object).readString();
                }
                if (n4 != 1 && n4 != 4 && n4 != 5 && n4 != 6) continue;
                ((PacketReader)object).readString();
            }
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }
}

