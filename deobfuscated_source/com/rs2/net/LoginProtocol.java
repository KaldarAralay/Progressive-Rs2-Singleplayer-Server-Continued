/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.player.Player;
import com.rs2.model.player.PlayerConnectionState;
import com.rs2.net.DedicatedReactor;
import com.rs2.net.IsaacCipher;
import com.rs2.net.packet.PacketBuffer;
import com.rs2.net.packet.PacketReader;
import com.rs2.net.packet.PacketWriter;
import com.rs2.util.TextUtil;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;

public final class LoginProtocol {
    public static ArrayList activeLoginUsernames = new ArrayList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean processLoginBuffer(Player player, ByteBuffer object) {
        switch (player.getConnectionState()) {
            case HANDSHAKE: {
                if (((Buffer)object).remaining() < 2) {
                    ((ByteBuffer)object).compact();
                    return true;
                }
                int n = ((ByteBuffer)object).get() & 0xFF;
                ((ByteBuffer)object).get();
                if (n != 14) {
                    System.err.println("Invalid login request: " + n);
                    player.disconnect();
                    return false;
                }
                PacketWriter handshakeResponse = PacketBuffer.allocateWriter(17);
                handshakeResponse.writeLong(0L);
                handshakeResponse.writeByte(0);
                handshakeResponse.writeLong(new SecureRandom().nextLong());
                player.writePacketBuffer(handshakeResponse.getBuffer());
                player.setConnectionState(PlayerConnectionState.LOGIN_PAYLOAD);
                return false;
            }
            case LOGIN_PAYLOAD: {
                if (((Buffer)object).remaining() < 2) {
                    ((ByteBuffer)object).compact();
                    return true;
                }
                int loginPayloadStart = ((Buffer)object).position();
                int n = ((ByteBuffer)object).get();
                if (n != 16 && n != 18) {
                    System.err.println("Invalid login type: " + n);
                    player.disconnect();
                    return false;
                }
                n = ((ByteBuffer)object).get() & 0xFF;
                int n2 = n - 44;
                if (((Buffer)object).remaining() < n) {
                    ((Buffer)object).position(loginPayloadStart);
                    ((ByteBuffer)object).compact();
                    return true;
                }
                PacketReader packetReader = PacketBuffer.wrapReader((ByteBuffer)object);
                player.setLoginMagicByte(packetReader.readSignedByte());
                player.setClientBuild(packetReader.readSignedShort());
                packetReader.readSignedByte();
                int n3 = packetReader.readInt();
                if (-94395865 != n3) {
                    player.disconnect();
                    return false;
                }
                n3 = 0;
                while (n3 < 9) {
                    packetReader.readInt();
                    ++n3;
                }
                if (ServerSettings.rsaEnabled) {
                    n3 = ((ByteBuffer)object).get() & 0xFF;
                    if (n3 <= 0 || n3 > ((Buffer)object).remaining()) {
                        System.err.println("Encrypted packet size zero or negative : " + n3);
                        player.disconnect();
                        return false;
                    }
                    byte[] byArray = new byte[n3];
                    ((ByteBuffer)object).get(byArray);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(new BigInteger(byArray).modPow(ServerSettings.rsaPrivateExponent, ServerSettings.rsaModulus).toByteArray());
                    int n4 = byteBuffer.get() & 0xFF;
                    if (n4 != 10) {
                        System.err.println("Unable to decode RSA block properly!");
                        player.disconnect();
                        return false;
                    }
                    long l = byteBuffer.getLong();
                    long l2 = byteBuffer.getLong();
                    int[] isaacSeed = new int[]{(int)(l >> 32), (int)l, (int)(l2 >> 32), (int)l2};
                    player.setInboundCipher(new IsaacCipher(isaacSeed));
                    int n5 = 0;
                    while (n5 < 4) {
                        int n6 = n5++;
                        isaacSeed[n6] = isaacSeed[n6] + 50;
                    }
                    player.setOutboundCipher(new IsaacCipher(isaacSeed));
                    byteBuffer.getInt();
                    String string = TextUtil.readLine(byteBuffer).trim();
                    String string2 = TextUtil.readLine(byteBuffer).trim();
                    player.setSubmittedPassword(string2);
                    player.setUsername(TextUtil.capitalizeFirst(string));
                    player.sessionStartMillis = System.currentTimeMillis();
                } else {
                    packetReader.readSignedByte();
                    n3 = packetReader.readSignedByte();
                    if (n3 != 10) {
                        System.err.println("Unable to decode RSA block properly!");
                        player.disconnect();
                        return false;
                    }
                    long l = packetReader.readLong();
                    long l3 = packetReader.readLong();
                    int[] nArray = new int[]{(int)(l >> 32), (int)l, (int)(l3 >> 32), (int)l3};
                    player.setInboundCipher(new IsaacCipher(nArray));
                    int n7 = 0;
                    while (n7 < 4) {
                        int n8 = n7++;
                        nArray[n8] = nArray[n8] + 50;
                    }
                    player.setOutboundCipher(new IsaacCipher(nArray));
                    packetReader.readInt();
                    String string = packetReader.readString().trim();
                    String string2 = packetReader.readString().trim();
                    player.setSubmittedPassword(string2);
                    player.setUsername(TextUtil.capitalizeFirst(string));
                    player.sessionStartMillis = System.currentTimeMillis();
                }
                player.setNameHash(TextUtil.encodeNameHash(player.getUsername().toLowerCase()));
                player.setConnectionState(PlayerConnectionState.LOGIN_QUEUED);
                if (activeLoginUsernames.contains(player.getUsername())) {
                    System.out.println("Player was already logging in " + player.getUsername());
                    player.disconnect();
                    return false;
                }
                activeLoginUsernames.add(player.getUsername());
                if (!player.loadAndValidateLogin() || player.getConnectionState() != PlayerConnectionState.LOGIN_QUEUED) break;
                DedicatedReactor dedicatedReactor = DedicatedReactor.getInstance();
                synchronized (dedicatedReactor) {
                    DedicatedReactor.getInstance().getSelector().wakeup();
                    player.getSelectionKey().interestOps(player.getSelectionKey().interestOps() & 0xFFFFFFFE);
                    try {
                        player.getSocketChannel().register(Server.getInstance().getSelector(), 1, player);
                    }
                    catch (java.nio.channels.ClosedChannelException exception) {
                        player.disconnect();
                    }
                    return false;
                }
            }
        }
        return false;
    }
}

