/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.path;

import com.rs2.model.player.Player;
import com.rs2.util.path.WalkingCollisionMap;
import java.util.LinkedList;

public final class PathReachability {
    public static boolean isReachable(Player player, int n, int n2, boolean bl, int n3, int n4) {
        int n5;
        int n6;
        int n7;
        int n8;
        if (n == player.getPosition().getLocalX()) {
            player.getPosition().getLocalY();
        }
        n -= 8 * player.getPosition().getRegionX();
        n2 -= 8 * player.getPosition().getRegionY();
        int[][] nArray = new int[104][104];
        int[][] nArray2 = new int[104][104];
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        int n9 = 0;
        while (n9 < 104) {
            n8 = 0;
            while (n8 < 104) {
                nArray2[n9][n8] = 99999999;
                ++n8;
            }
            ++n9;
        }
        n9 = player.getPosition().getLocalX();
        n8 = player.getPosition().getLocalY();
        nArray[n9][n8] = 99;
        nArray2[n9][n8] = 0;
        int n10 = 0;
        linkedList.add(n9);
        linkedList2.add(n8);
        boolean bl2 = false;
        while (n10 != linkedList.size() && linkedList.size() < 4000) {
            n9 = (Integer)linkedList.get(n10);
            n8 = (Integer)linkedList2.get(n10);
            n7 = (player.getPosition().getRegionX() << 3) + n9;
            n6 = (player.getPosition().getRegionY() << 3) + n8;
            if (n9 == n && n8 == n2) {
                bl2 = true;
                break;
            }
            n10 = (n10 + 1) % 4000;
            n5 = nArray2[n9][n8] + 1;
            if (n8 > 0 && nArray[n9][n8 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n7, n6 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n9);
                linkedList2.add(n8 - 1);
                nArray[n9][n8 - 1] = 1;
                nArray2[n9][n8 - 1] = n5;
            }
            if (n9 > 0 && nArray[n9 - 1][n8] == 0 && (WalkingCollisionMap.getTileFlags(n7 - 1, n6, player.getPosition().getPlane()) & 0x1280108) == 0) {
                linkedList.add(n9 - 1);
                linkedList2.add(n8);
                nArray[n9 - 1][n8] = 2;
                nArray2[n9 - 1][n8] = n5;
            }
            if (n8 < 103 && nArray[n9][n8 + 1] == 0 && (WalkingCollisionMap.getTileFlags(n7, n6 + 1, player.getPosition().getPlane()) & 0x1280120) == 0) {
                linkedList.add(n9);
                linkedList2.add(n8 + 1);
                nArray[n9][n8 + 1] = 4;
                nArray2[n9][n8 + 1] = n5;
            }
            if (n9 < 103 && nArray[n9 + 1][n8] == 0 && (WalkingCollisionMap.getTileFlags(n7 + 1, n6, player.getPosition().getPlane()) & 0x1280180) == 0) {
                linkedList.add(n9 + 1);
                linkedList2.add(n8);
                nArray[n9 + 1][n8] = 8;
                nArray2[n9 + 1][n8] = n5;
            }
            if (n9 > 0 && n8 > 0 && nArray[n9 - 1][n8 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n7 - 1, n6 - 1, player.getPosition().getPlane()) & 0x128010E) == 0 && (WalkingCollisionMap.getTileFlags(n7 - 1, n6, player.getPosition().getPlane()) & 0x1280108) == 0 && (WalkingCollisionMap.getTileFlags(n7, n6 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n9 - 1);
                linkedList2.add(n8 - 1);
                nArray[n9 - 1][n8 - 1] = 3;
                nArray2[n9 - 1][n8 - 1] = n5;
            }
            if (n9 > 0 && n8 < 103 && nArray[n9 - 1][n8 + 1] == 0 && (WalkingCollisionMap.getTileFlags(n7 - 1, n6 + 1, player.getPosition().getPlane()) & 0x1280138) == 0 && (WalkingCollisionMap.getTileFlags(n7 - 1, n6, player.getPosition().getPlane()) & 0x1280108) == 0 && (WalkingCollisionMap.getTileFlags(n7, n6 + 1, player.getPosition().getPlane()) & 0x1280120) == 0) {
                linkedList.add(n9 - 1);
                linkedList2.add(n8 + 1);
                nArray[n9 - 1][n8 + 1] = 6;
                nArray2[n9 - 1][n8 + 1] = n5;
            }
            if (n9 < 103 && n8 > 0 && nArray[n9 + 1][n8 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n7 + 1, n6 - 1, player.getPosition().getPlane()) & 0x1280183) == 0 && (WalkingCollisionMap.getTileFlags(n7 + 1, n6, player.getPosition().getPlane()) & 0x1280180) == 0 && (WalkingCollisionMap.getTileFlags(n7, n6 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n9 + 1);
                linkedList2.add(n8 - 1);
                nArray[n9 + 1][n8 - 1] = 9;
                nArray2[n9 + 1][n8 - 1] = n5;
            }
            if (n9 >= 103 || n8 >= 103 || nArray[n9 + 1][n8 + 1] != 0 || (WalkingCollisionMap.getTileFlags(n7 + 1, n6 + 1, player.getPosition().getPlane()) & 0x12801E0) != 0 || (WalkingCollisionMap.getTileFlags(n7 + 1, n6, player.getPosition().getPlane()) & 0x1280180) != 0 || (WalkingCollisionMap.getTileFlags(n7, n6 + 1, player.getPosition().getPlane()) & 0x1280120) != 0) continue;
            linkedList.add(n9 + 1);
            linkedList2.add(n8 + 1);
            nArray[n9 + 1][n8 + 1] = 12;
            nArray2[n9 + 1][n8 + 1] = n5;
        }
        if (!bl2) {
            n7 = 1000;
            n6 = 100;
            n10 = n - 10;
            while (n10 <= n + 10) {
                n5 = n2 - 10;
                while (n5 <= n2 + 10) {
                    if (n10 >= 0 && n5 >= 0 && n10 < 104 && n5 < 104 && nArray2[n10][n5] < 100) {
                        int n11 = 0;
                        if (n10 < n) {
                            n11 = n - n10;
                        } else if (n10 > n - 1) {
                            n11 = n10 - (n - 1);
                        }
                        int n12 = 0;
                        if (n5 < n2) {
                            n12 = n2 - n5;
                        } else if (n5 > n2 - 1) {
                            n12 = n5 - (n2 - 1);
                        }
                        n11 = n11 * n11 + n12 * n12;
                        if (n11 < n7 || n11 == n7 && nArray2[n10][n5] < n6) {
                            n7 = n11;
                            n6 = nArray2[n10][n5];
                            n9 = n10;
                            n8 = n5;
                        }
                    }
                    ++n5;
                }
                ++n10;
            }
            if (n7 == 1000) {
                return false;
            }
        }
        n10 = 0;
        linkedList.set(0, n9);
        ++n10;
        linkedList2.set(0, n8);
        n6 = n7 = nArray[n9][n8];
        while (n9 != player.getPosition().getLocalX() || n8 != player.getPosition().getLocalY()) {
            if (n6 != n7) {
                n7 = n6;
                linkedList.set(n10, n9);
                linkedList2.set(n10++, n8);
            }
            if ((n6 & 2) != 0) {
                ++n9;
            } else if ((n6 & 8) != 0) {
                --n9;
            }
            if ((n6 & 1) != 0) {
                ++n8;
            } else if ((n6 & 4) != 0) {
                --n8;
            }
            n6 = nArray[n9][n8];
        }
        return bl2;
    }
}

