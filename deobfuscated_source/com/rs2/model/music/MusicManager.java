/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.music;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.music.MusicAreaDefinition;
import com.rs2.model.music.MusicTrackDefinition;
import com.rs2.model.player.Player;
import com.rs2.util.CharacterFileManager;
import com.rs2.util.GameUtil;
import java.util.Arrays;
import java.util.List;

public final class MusicManager {
    private static List buttonlessTrackIds = Arrays.asList(648, 649, 650, 651, 652);
    private int currentAreaId = -1;
    private int currentTrackId = -1;

    public final void updateForPlayerPosition(Player player) {
        int playerX = player.getPosition().getX();
        int playerY = player.getPosition().getY();
        int[] areaIds = new int[5];
        int areaIdCount = 0;
        int areaId = 0;
        while (areaId < MusicAreaDefinition.areaCount) {
            boolean inArea = false;
            MusicAreaDefinition musicAreaDefinition = MusicAreaDefinition.forAreaId(areaId);
            if (musicAreaDefinition.getRegionCount() == 0) {
                inArea = musicAreaDefinition.getAreaBounds().contains(new Position(playerX, playerY, 0));
            } else {
                int regionId = GameUtil.getRegionId(playerX, playerY);
                int[] regionIds = musicAreaDefinition.getRegionIds();
                int n = 0;
                while (n < musicAreaDefinition.getRegionCount()) {
                    if (regionId == regionIds[n]) {
                        inArea = true;
                        break;
                    }
                    ++n;
                }
            }
            if (inArea) {
                areaIds[areaIdCount] = areaId;
                ++areaIdCount;
            }
            ++areaId;
        }
        int bestPriority = 0;
        int bestAreaId = -1;
        int n = 0;
        while (n < areaIdCount) {
            MusicAreaDefinition musicAreaDefinition = MusicAreaDefinition.forAreaId(areaIds[n]);
            if (n == 0) {
                bestPriority = musicAreaDefinition.getPriority();
                bestAreaId = musicAreaDefinition.getAreaId();
            } else if (bestPriority < musicAreaDefinition.getPriority()) {
                bestPriority = musicAreaDefinition.getPriority();
                bestAreaId = musicAreaDefinition.getAreaId();
            }
            ++n;
        }
        this.currentAreaId = bestAreaId;
        int trackId;
        if (this.currentAreaId != -1) {
            MusicAreaDefinition musicAreaDefinition = MusicAreaDefinition.forAreaId(this.currentAreaId);
            int areaTrackId = musicAreaDefinition.getTrackId();
            if (player.eo) {
                this.currentTrackId = areaTrackId;
            }
            MusicTrackDefinition musicTrackDefinition = MusicTrackDefinition.forTrackId(areaTrackId);
            int unlockConfigId = musicTrackDefinition.getUnlockConfigId();
            int unlockBitMask = musicTrackDefinition.getUnlockBitMask();
            if (unlockConfigId != -1 && (player.ep[unlockConfigId] & unlockBitMask) == 0) {
                player.eo = true;
                player.ep[unlockConfigId] = player.ep[unlockConfigId] + unlockBitMask;
                player.packetSender.sendConfig(unlockConfigId, player.ep[unlockConfigId]);
                player.packetSender.sendGameMessage("@red@You have unlocked a new music track: " + musicTrackDefinition.getName());
                this.currentTrackId = areaTrackId;
            }
            trackId = this.currentTrackId;
        } else {
            trackId = -1;
        }
        if (trackId != -1) {
            if (trackId == player.cg) {
                return;
            }
            player.cg = trackId;
            MusicTrackDefinition musicTrackDefinition = MusicTrackDefinition.forTrackId(trackId);
            if (musicTrackDefinition.getButtonId() != -1 || buttonlessTrackIds.contains(trackId)) {
                player.packetSender.sendMusicTrack(musicTrackDefinition);
            }
        }
    }

    public static void unlockTrack(Player player, int n) {
        int n2;
        MusicTrackDefinition musicTrackDefinition = MusicTrackDefinition.forTrackId(n);
        int n3 = player.ep[musicTrackDefinition.getUnlockConfigId()];
        if ((n3 & (n2 = musicTrackDefinition.getUnlockBitMask())) == 0) {
            int n4;
            int n5 = n4 = musicTrackDefinition.getUnlockConfigId();
            player.ep[n5] = player.ep[n5] + n2;
            Player player2 = player;
            player2.packetSender.sendConfig(n4, player.ep[n4]);
        }
    }

    public static void unlockAllTracks(Player player) {
        int n = 0;
        while (n < MusicTrackDefinition.trackCount) {
            int n2;
            int n3;
            MusicTrackDefinition musicTrackDefinition = MusicTrackDefinition.forTrackId(n);
            if (musicTrackDefinition.getUnlockConfigId() != -1 && ((n3 = player.ep[musicTrackDefinition.getUnlockConfigId()]) & (n2 = musicTrackDefinition.getUnlockBitMask())) == 0) {
                int n4;
                int n5 = n4 = musicTrackDefinition.getUnlockConfigId();
                player.ep[n5] = player.ep[n5] + n2;
            }
            ++n;
        }
        n = 0;
        while (n < CharacterFileManager.musicUnlockConfigIds.length) {
            int n6 = CharacterFileManager.musicUnlockConfigIds[n];
            Player player2 = player;
            player2.packetSender.sendConfig(n6, player.ep[n6]);
            ++n;
        }
    }

    public static boolean isTrackUnlocked(Player player, int n) {
        int n2;
        MusicTrackDefinition musicTrackDefinition = MusicTrackDefinition.forTrackId(n);
        int n3 = player.ep[musicTrackDefinition.getUnlockConfigId()];
        return (n3 & (n2 = musicTrackDefinition.getUnlockBitMask())) != 0 && n2 != -1;
    }
}

