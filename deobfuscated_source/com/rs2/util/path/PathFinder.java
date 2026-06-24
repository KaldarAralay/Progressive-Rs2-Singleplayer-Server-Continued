/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.path;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.util.path.WalkingCollisionMap;
import java.util.LinkedList;

public final class PathFinder {
    private static final PathFinder instance = new PathFinder();

    public static PathFinder getInstance() {
        return instance;
    }

    public static boolean findGlobalPath(Player player, int n, int n2, boolean bl, int n3, int n4) {
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        if (n == player.getPosition().getLocalX()) {
            player.getPosition().getLocalY();
        }
        n -= 8 * player.getPosition().getRegionX();
        n2 -= 8 * player.getPosition().getRegionY();
        int[][] nArray = new int[2080][2080];
        int[][] nArray2 = new int[2080][2080];
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        int n10 = 0;
        while (n10 < 2080) {
            n9 = 0;
            while (n9 < 2080) {
                nArray2[n10][n9] = 99999999;
                ++n9;
            }
            ++n10;
        }
        n10 = player.getPosition().getLocalX();
        n9 = player.getPosition().getLocalY();
        nArray[n10][n9] = 99;
        nArray2[n10][n9] = 0;
        int n11 = 0;
        linkedList.add(n10);
        linkedList2.add(n9);
        int n12 = 0;
        while (n11 != linkedList.size() && linkedList.size() < 80000) {
            n10 = (Integer)linkedList.get(n11);
            n9 = (Integer)linkedList2.get(n11);
            n8 = (player.getPosition().getRegionX() << 3) + n10;
            n7 = (player.getPosition().getRegionY() << 3) + n9;
            if (n10 == n && n9 == n2) {
                n12 = 1;
                break;
            }
            n11 = (n11 + 1) % 80000;
            n6 = nArray2[n10][n9] + 1;
            if (n9 > 0 && nArray[n10][n9 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n10);
                linkedList2.add(n9 - 1);
                nArray[n10][n9 - 1] = 1;
                nArray2[n10][n9 - 1] = n6;
            }
            if (n10 > 0 && nArray[n10 - 1][n9] == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7, player.getPosition().getPlane()) & 0x1280108) == 0) {
                linkedList.add(n10 - 1);
                linkedList2.add(n9);
                nArray[n10 - 1][n9] = 2;
                nArray2[n10 - 1][n9] = n6;
            }
            if (n9 < 2079 && nArray[n10][n9 + 1] == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 + 1, player.getPosition().getPlane()) & 0x1280120) == 0) {
                linkedList.add(n10);
                linkedList2.add(n9 + 1);
                nArray[n10][n9 + 1] = 4;
                nArray2[n10][n9 + 1] = n6;
            }
            if (n10 < 2079 && nArray[n10 + 1][n9] == 0 && (WalkingCollisionMap.getTileFlags(n8 + 1, n7, player.getPosition().getPlane()) & 0x1280180) == 0) {
                linkedList.add(n10 + 1);
                linkedList2.add(n9);
                nArray[n10 + 1][n9] = 8;
                nArray2[n10 + 1][n9] = n6;
            }
            if (n10 > 0 && n9 > 0 && nArray[n10 - 1][n9 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7 - 1, player.getPosition().getPlane()) & 0x128010E) == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7, player.getPosition().getPlane()) & 0x1280108) == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n10 - 1);
                linkedList2.add(n9 - 1);
                nArray[n10 - 1][n9 - 1] = 3;
                nArray2[n10 - 1][n9 - 1] = n6;
            }
            if (n10 > 0 && n9 < 2079 && nArray[n10 - 1][n9 + 1] == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7 + 1, player.getPosition().getPlane()) & 0x1280138) == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7, player.getPosition().getPlane()) & 0x1280108) == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 + 1, player.getPosition().getPlane()) & 0x1280120) == 0) {
                linkedList.add(n10 - 1);
                linkedList2.add(n9 + 1);
                nArray[n10 - 1][n9 + 1] = 6;
                nArray2[n10 - 1][n9 + 1] = n6;
            }
            if (n10 < 2079 && n9 > 0 && nArray[n10 + 1][n9 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n8 + 1, n7 - 1, player.getPosition().getPlane()) & 0x1280183) == 0 && (WalkingCollisionMap.getTileFlags(n8 + 1, n7, player.getPosition().getPlane()) & 0x1280180) == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n10 + 1);
                linkedList2.add(n9 - 1);
                nArray[n10 + 1][n9 - 1] = 9;
                nArray2[n10 + 1][n9 - 1] = n6;
            }
            if (n10 >= 2079 || n9 >= 2079 || nArray[n10 + 1][n9 + 1] != 0 || (WalkingCollisionMap.getTileFlags(n8 + 1, n7 + 1, player.getPosition().getPlane()) & 0x12801E0) != 0 || (WalkingCollisionMap.getTileFlags(n8 + 1, n7, player.getPosition().getPlane()) & 0x1280180) != 0 || (WalkingCollisionMap.getTileFlags(n8, n7 + 1, player.getPosition().getPlane()) & 0x1280120) != 0) continue;
            linkedList.add(n10 + 1);
            linkedList2.add(n9 + 1);
            nArray[n10 + 1][n9 + 1] = 12;
            nArray2[n10 + 1][n9 + 1] = n6;
        }
        if (n12 == 0) {
            n8 = 1000;
            n7 = 100;
            n12 = n - 10;
            while (n12 <= n + 10) {
                n5 = n2 - 10;
                while (n5 <= n2 + 10) {
                    if (n12 >= 0 && n5 >= 0 && n12 < 2080 && n5 < 2080 && nArray2[n12][n5] < 100) {
                        n11 = 0;
                        if (n12 < n) {
                            n11 = n - n12;
                        } else if (n12 > n - 1) {
                            n11 = n12 - (n - 1);
                        }
                        n6 = 0;
                        if (n5 < n2) {
                            n6 = n2 - n5;
                        } else if (n5 > n2 - 1) {
                            n6 = n5 - (n2 - 1);
                        }
                        n11 = n11 * n11 + n6 * n6;
                        if (n11 < n8 || n11 == n8 && nArray2[n12][n5] < n7) {
                            n8 = n11;
                            n7 = nArray2[n12][n5];
                            n10 = n12;
                            n9 = n5;
                        }
                    }
                    ++n5;
                }
                ++n12;
            }
            if (n8 == 1000) {
                return false;
            }
        }
        n11 = 0;
        linkedList.set(0, n10);
        ++n11;
        linkedList2.set(0, n9);
        n7 = n8 = nArray[n10][n9];
        while (n10 != player.getPosition().getLocalX() || n9 != player.getPosition().getLocalY()) {
            if (n7 != n8) {
                n8 = n7;
                linkedList.set(n11, n10);
                linkedList2.set(n11++, n9);
            }
            if ((n7 & 2) != 0) {
                ++n10;
            } else if ((n7 & 8) != 0) {
                --n10;
            }
            if ((n7 & 1) != 0) {
                ++n9;
            } else if ((n7 & 4) != 0) {
                --n9;
            }
            n7 = nArray[n10][n9];
        }
        player.getMovementQueue().clear();
        n7 = n11--;
        n6 = (player.getPosition().getRegionX() << 3) + (Integer)linkedList.get(n11);
        n12 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);
        player.getMovementQueue().addStep(new Position(n6, n12));
        n5 = 1;
        while (n5 < n7) {
            n6 = (player.getPosition().getRegionX() << 3) + (Integer)linkedList.get(--n11);
            n12 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);
            player.getMovementQueue().addStep(new Position(n6, n12));
            ++n5;
        }
        player.getMovementQueue().removeFirstStep();
        return true;
    }

    public static boolean findPath(Player player, int n, int n2, boolean bl, int n4, int n5) {
        int n6;
        int n7;
        int n8;
        int n9;
        if (n == player.getPosition().getLocalX() && n2 == player.getPosition().getLocalY() && !bl) {
            player.packetSender.sendGameMessage("ERROR!");
            return false;
        }
        n -= 8 * player.getPosition().getRegionX();
        n2 -= 8 * player.getPosition().getRegionY();
        int[][] nArray = new int[104][104];
        int[][] nArray2 = new int[104][104];
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        int n10 = 0;
        while (n10 < 104) {
            n9 = 0;
            while (n9 < 104) {
                nArray2[n10][n9] = 99999999;
                ++n9;
            }
            ++n10;
        }
        n10 = player.getPosition().getLocalX();
        n9 = player.getPosition().getLocalY();
        nArray[n10][n9] = 99;
        nArray2[n10][n9] = 0;
        int n11 = 0;
        linkedList.add(n10);
        linkedList2.add(n9);
        int n12 = 0;
        while (n11 != linkedList.size() && linkedList.size() < 4000) {
            n10 = (Integer)linkedList.get(n11);
            n9 = (Integer)linkedList2.get(n11);
            n8 = (player.getPosition().getRegionX() << 3) + n10;
            n7 = (player.getPosition().getRegionY() << 3) + n9;
            if (n10 == n && n9 == n2) {
                n12 = 1;
                break;
            }
            n11 = (n11 + 1) % 4000;
            n6 = nArray2[n10][n9] + 1;
            if (n9 > 0 && nArray[n10][n9 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n10);
                linkedList2.add(n9 - 1);
                nArray[n10][n9 - 1] = 1;
                nArray2[n10][n9 - 1] = n6;
            }
            if (n10 > 0 && nArray[n10 - 1][n9] == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7, player.getPosition().getPlane()) & 0x1280108) == 0) {
                linkedList.add(n10 - 1);
                linkedList2.add(n9);
                nArray[n10 - 1][n9] = 2;
                nArray2[n10 - 1][n9] = n6;
            }
            if (n9 < 103 && nArray[n10][n9 + 1] == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 + 1, player.getPosition().getPlane()) & 0x1280120) == 0) {
                linkedList.add(n10);
                linkedList2.add(n9 + 1);
                nArray[n10][n9 + 1] = 4;
                nArray2[n10][n9 + 1] = n6;
            }
            if (n10 < 103 && nArray[n10 + 1][n9] == 0 && (WalkingCollisionMap.getTileFlags(n8 + 1, n7, player.getPosition().getPlane()) & 0x1280180) == 0) {
                linkedList.add(n10 + 1);
                linkedList2.add(n9);
                nArray[n10 + 1][n9] = 8;
                nArray2[n10 + 1][n9] = n6;
            }
            if (n10 > 0 && n9 > 0 && nArray[n10 - 1][n9 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7 - 1, player.getPosition().getPlane()) & 0x128010E) == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7, player.getPosition().getPlane()) & 0x1280108) == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n10 - 1);
                linkedList2.add(n9 - 1);
                nArray[n10 - 1][n9 - 1] = 3;
                nArray2[n10 - 1][n9 - 1] = n6;
            }
            if (n10 > 0 && n9 < 103 && nArray[n10 - 1][n9 + 1] == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7 + 1, player.getPosition().getPlane()) & 0x1280138) == 0 && (WalkingCollisionMap.getTileFlags(n8 - 1, n7, player.getPosition().getPlane()) & 0x1280108) == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 + 1, player.getPosition().getPlane()) & 0x1280120) == 0) {
                linkedList.add(n10 - 1);
                linkedList2.add(n9 + 1);
                nArray[n10 - 1][n9 + 1] = 6;
                nArray2[n10 - 1][n9 + 1] = n6;
            }
            if (n10 < 103 && n9 > 0 && nArray[n10 + 1][n9 - 1] == 0 && (WalkingCollisionMap.getTileFlags(n8 + 1, n7 - 1, player.getPosition().getPlane()) & 0x1280183) == 0 && (WalkingCollisionMap.getTileFlags(n8 + 1, n7, player.getPosition().getPlane()) & 0x1280180) == 0 && (WalkingCollisionMap.getTileFlags(n8, n7 - 1, player.getPosition().getPlane()) & 0x1280102) == 0) {
                linkedList.add(n10 + 1);
                linkedList2.add(n9 - 1);
                nArray[n10 + 1][n9 - 1] = 9;
                nArray2[n10 + 1][n9 - 1] = n6;
            }
            if (n10 >= 103 || n9 >= 103 || nArray[n10 + 1][n9 + 1] != 0 || (WalkingCollisionMap.getTileFlags(n8 + 1, n7 + 1, player.getPosition().getPlane()) & 0x12801E0) != 0 || (WalkingCollisionMap.getTileFlags(n8 + 1, n7, player.getPosition().getPlane()) & 0x1280180) != 0 || (WalkingCollisionMap.getTileFlags(n8, n7 + 1, player.getPosition().getPlane()) & 0x1280120) != 0) continue;
            linkedList.add(n10 + 1);
            linkedList2.add(n9 + 1);
            nArray[n10 + 1][n9 + 1] = 12;
            nArray2[n10 + 1][n9 + 1] = n6;
        }
        if (n12 == 0) {
            if (bl) {
                n8 = 1000;
                n7 = 100;
                int n13 = n - 10;
                while (n13 <= n + 10) {
                    n12 = n2 - 10;
                    while (n12 <= n2 + 10) {
                        if (n13 >= 0 && n12 >= 0 && n13 < 104 && n12 < 104 && nArray2[n13][n12] < 100) {
                            n11 = 0;
                            if (n13 < n) {
                                n11 = n - n13;
                            } else if (n13 > n + n4 - 1) {
                                n11 = n13 - (n + n4 - 1);
                            }
                            n6 = 0;
                            if (n12 < n2) {
                                n6 = n2 - n12;
                            } else if (n12 > n2 + n5 - 1) {
                                n6 = n12 - (n2 + n5 - 1);
                            }
                            n11 = n11 * n11 + n6 * n6;
                            if (n11 < n8 || n11 == n8 && nArray2[n13][n12] < n7) {
                                n8 = n11;
                                n7 = nArray2[n13][n12];
                                n10 = n13;
                                n9 = n12;
                            }
                        }
                        ++n12;
                    }
                    ++n13;
                }
                if (n8 == 1000) {
                    return false;
                }
            } else {
                return false;
            }
        }
        n11 = 0;
        linkedList.set(0, n10);
        ++n11;
        linkedList2.set(0, n9);
        n7 = n8 = nArray[n10][n9];
        while (n10 != player.getPosition().getLocalX() || n9 != player.getPosition().getLocalY()) {
            if (n7 != n8) {
                n8 = n7;
                linkedList.set(n11, n10);
                linkedList2.set(n11++, n9);
            }
            if ((n7 & 2) != 0) {
                ++n10;
            } else if ((n7 & 8) != 0) {
                --n10;
            }
            if ((n7 & 1) != 0) {
                ++n9;
            } else if ((n7 & 4) != 0) {
                --n9;
            }
            n7 = nArray[n10][n9];
        }
        player.getMovementQueue().clear();
        n7 = n11--;
        n6 = (player.getPosition().getRegionX() << 3) + (Integer)linkedList.get(n11);
        int n13 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);
        player.getMovementQueue().addStep(new Position(n6, n13));
        n12 = 1;
        while (n12 < n7) {
            n6 = (player.getPosition().getRegionX() << 3) + (Integer)linkedList.get(--n11);
            n13 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);
            player.getMovementQueue().addStep(new Position(n6, n13));
            ++n12;
        }
        player.getMovementQueue().removeFirstStep();
        return true;
    }
}

