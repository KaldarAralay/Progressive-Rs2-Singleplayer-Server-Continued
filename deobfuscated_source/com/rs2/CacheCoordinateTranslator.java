/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import com.rs2.util.path.WalkingCollisionMap;

public final class CacheCoordinateTranslator {
    public static boolean dungeonCoordinateShiftActive = false;
    private static int[] dungeonCoordinateShiftSourceRegionIds = new int[]{9540, 9541, 9797};
    private static int[] dungeonCoordinateShiftTargetRegionIds = new int[]{12692, 12693, 12949};
    private static int dungeonCoordinateShiftXOffset = 768;
    private static int dungeonCoordinateShiftYOffset = 5120;

    public static boolean isDungeonCoordinateShiftSourceRegion(int n, int n2) {
        n = GameUtil.getRegionId(n, n2);
        int[] nArray = dungeonCoordinateShiftSourceRegionIds;
        int n3 = dungeonCoordinateShiftSourceRegionIds.length;
        int n4 = 0;
        while (n4 < n3) {
            n2 = nArray[n4];
            if (n == n2) {
                return true;
            }
            ++n4;
        }
        return false;
    }

    public static void detectDungeonCoordinateShift() {
        if (!WalkingCollisionMap.hasDungeonCoordinateShiftRegion(9797)) {
            dungeonCoordinateShiftActive = true;
        }
    }

    public static void translateSavedDungeonPosition(Player player) {
        int n = player.getPosition().getX();
        int n2 = player.getPosition().getY();
        if (player.savedCacheVersion < 319 && !dungeonCoordinateShiftActive) {
            boolean bl;
            block5: {
                int n3 = n2;
                int n4 = n;
                n4 = GameUtil.getRegionId(n4, n3);
                int[] nArray = dungeonCoordinateShiftTargetRegionIds;
                int n5 = dungeonCoordinateShiftTargetRegionIds.length;
                int n6 = 0;
                while (n6 < n5) {
                    n3 = nArray[n6];
                    if (n4 == n3) {
                        bl = true;
                        break block5;
                    }
                    ++n6;
                }
                bl = false;
            }
            if (bl) {
                player.getPosition().setX(n - dungeonCoordinateShiftXOffset);
                player.getPosition().setY(n2 - dungeonCoordinateShiftYOffset);
                player.getPosition().setPreviousX(player.getPosition().getX());
                player.getPosition().setPreviousY(player.getPosition().getY());
            }
        }
        if (player.savedCacheVersion > 289 && dungeonCoordinateShiftActive && CacheCoordinateTranslator.isDungeonCoordinateShiftSourceRegion(n, n2)) {
            player.getPosition().setX(n + dungeonCoordinateShiftXOffset);
            player.getPosition().setY(n2 + dungeonCoordinateShiftYOffset);
            player.getPosition().setPreviousX(player.getPosition().getX());
            player.getPosition().setPreviousY(player.getPosition().getY());
        }
    }
}

