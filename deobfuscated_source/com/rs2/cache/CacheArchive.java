/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.cache;

import com.rs2.model.clue.ChallengeQuestion;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.AcheyLogFletchingAction;
import com.rs2.model.skill.fletching.logs.MagicLogFletchingAction;
import com.rs2.model.skill.fletching.logs.MapleLogFletchingAction;
import com.rs2.model.skill.fletching.logs.NormalLogFletchingAction;
import com.rs2.model.skill.fletching.logs.OakLogFletchingAction;
import com.rs2.model.skill.fletching.logs.WillowLogFletchingAction;
import com.rs2.model.skill.fletching.logs.YewLogFletchingAction;
import com.rs2.net.packet.PacketSender;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class CacheArchive {
    private boolean wholeArchiveDecompressed = false;
    private ByteBuffer archiveBuffer;
    private Map entriesByNameHash = new HashMap();

    public CacheArchive(CacheFile cacheFile) {
        ByteBuffer byteBuffer = cacheFile.getBuffer();
        byteBuffer.position(0);
        int n = (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | byteBuffer.get() & 0xFF;
        int n2 = (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | byteBuffer.get() & 0xFF;
        if (n != n2) {
            byte[] byArray = new byte[n2];
            byteBuffer.get(byArray);
            byte[] byArray2 = CacheArchive.decompressBzip2Payload(byArray);
            byteBuffer = ByteBuffer.allocate(byArray2.length);
            byteBuffer.put(byArray2);
            byteBuffer.flip();
            this.wholeArchiveDecompressed = true;
        }
        n = byteBuffer.getShort() & 0xFFFF;
        n2 = byteBuffer.position() + n * 10;
        int n3 = 0;
        while (n3 < n) {
            int n4 = byteBuffer.getInt();
            int n5 = (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | byteBuffer.get() & 0xFF;
            int n6 = (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | byteBuffer.get() & 0xFF;
            CacheArchiveEntry cacheArchiveEntry = new CacheArchiveEntry(n4, n5, n6, n2);
            this.entriesByNameHash.put(cacheArchiveEntry.getNameHash(), cacheArchiveEntry);
            n2 += cacheArchiveEntry.getCompressedSize();
            ++n3;
        }
        this.archiveBuffer = byteBuffer;
    }

    public byte[] getFileBytes(String string) {
        int n = 0;
        string = string.toUpperCase();
        int n2 = 0;
        while (n2 < string.length()) {
            n = n * 61 + string.charAt(n2) - 32;
            ++n2;
        }
        CacheArchiveEntry cacheArchiveEntry = (CacheArchiveEntry)this.entriesByNameHash.get(n);
        if (cacheArchiveEntry == null) {
            return null;
        }
        byte[] byArray = new byte[cacheArchiveEntry.getCompressedSize()];
        this.archiveBuffer.position(cacheArchiveEntry.getDataOffset());
        this.archiveBuffer.get(byArray);
        if (this.wholeArchiveDecompressed) {
            return byArray;
        }
        return CacheArchive.decompressBzip2Payload(byArray);
    }

    public ByteBuffer getFileBuffer(String string) {
        byte[] byArray = this.getFileBytes(string);
        if (byArray == null) {
            return null;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(byArray.length);
        byteBuffer.put(byArray);
        byteBuffer.flip();
        return byteBuffer;
    }

    private static byte[] decompressBzip2Payload(byte[] byArray) {
        byte[] byArray2 = new byte[byArray.length + 4];
        System.arraycopy(byArray, 0, byArray2, 4, byArray.length);
        byArray2[0] = 66;
        byArray2[1] = 90;
        byArray2[2] = 104;
        byArray2[3] = 49;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();){
            BZip2CompressorInputStream bZip2CompressorInputStream = new BZip2CompressorInputStream(new ByteArrayInputStream(byArray2));
            try {
                int n;
                byte[] buffer = new byte[512];
                while ((n = bZip2CompressorInputStream.read(buffer, 0, buffer.length)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, n);
                }
            }
            finally {
                bZip2CompressorInputStream.close();
            }
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    public static boolean handleLogCuttingButton(Player player, int n, int n2) {
        if (player.interfaceAction == "normalCutting" && NormalLogFletchingAction.create(player, n, n2) != null) {
            NormalLogFletchingAction.create(player, n, n2).start();
            return true;
        }
        if (player.interfaceAction == "oakCutting" && OakLogFletchingAction.create(player, n, n2) != null) {
            OakLogFletchingAction.create(player, n, n2).start();
            return true;
        }
        if (player.interfaceAction == "acheyCutting" && AcheyLogFletchingAction.create(player, n, n2) != null) {
            AcheyLogFletchingAction.create(player, n, n2).start();
            return true;
        }
        if (player.interfaceAction == "willowCutting" && WillowLogFletchingAction.create(player, n, n2) != null) {
            WillowLogFletchingAction.create(player, n, n2).start();
            return true;
        }
        if (player.interfaceAction == "mapleCutting" && MapleLogFletchingAction.create(player, n, n2) != null) {
            MapleLogFletchingAction.create(player, n, n2).start();
            return true;
        }
        if (player.interfaceAction == "yewCutting" && YewLogFletchingAction.create(player, n, n2) != null) {
            YewLogFletchingAction.create(player, n, n2).start();
            return true;
        }
        if (player.interfaceAction == "magicCutting" && MagicLogFletchingAction.create(player, n, n2) != null) {
            MagicLogFletchingAction.create(player, n, n2).start();
            return true;
        }
        return false;
    }

    public static void giveChallengeQuestionAnswerItem(Player player, int n) {
        ChallengeQuestion challengeQuestion = ChallengeQuestion.forClueItemId(n);
        if (challengeQuestion == null) {
            return;
        }
        player.getInventoryManager().addOrDropItem(new ItemStack(challengeQuestion.getAnswerItemId(), 1));
    }

    public static boolean hasChallengeQuestionAnswerItem(Player player, int n) {
        ChallengeQuestion challengeQuestion = ChallengeQuestion.forClueItemId(n);
        if (challengeQuestion == null) {
            return false;
        }
        return player.getInventoryManager().containsItem(challengeQuestion.getAnswerItemId());
    }

    public static String[] getChallengeQuestionLines(int n) {
        ChallengeQuestion challengeQuestion = ChallengeQuestion.forClueItemId(n);
        if (challengeQuestion == null) {
            return null;
        }
        return challengeQuestion.getQuestionLines();
    }

    public static boolean showChallengeQuestionForAnswerItem(Player player, int n) {
        ChallengeQuestion challengeQuestion = ChallengeQuestion.forAnswerItemId(n);
        if (challengeQuestion == null) {
            return false;
        }
        player.packetSender.showInterface(6965);
        String[] lines = challengeQuestion.getQuestionLines();
        int[] textIds = CacheArchive.interfaceTextIdsForLineCount(lines.length);
        int n2 = 0;
        while (n2 < lines.length) {
            PacketSender packetSender = player.packetSender;
            packetSender.sendInterfaceText(lines[n2], textIds[n2]);
            ++n2;
        }
        return true;
    }

    private static int[] interfaceTextIdsForLineCount(int n) {
        switch (n) {
            case 1: {
                return new int[]{6971};
            }
            case 2: {
                return new int[]{6971, 6972};
            }
            case 3: {
                return new int[]{6971, 6972, 6973};
            }
        }
        return null;
    }
}
