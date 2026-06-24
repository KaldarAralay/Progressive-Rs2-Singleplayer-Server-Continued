/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.path;

import com.rs2.cache.CacheArchive;
import com.rs2.cache.CacheStore;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.WorldObjectRegionIndex;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.path.MapDataReader;

public final class WalkingCollisionMap {
    private static WalkingCollisionMap[] regions;
    private int regionId;
    private int[][][] tileFlags = new int[4][][];
    private static int[] regionIds;
    private static int e;
    private static int f;

    static {
        e = 0;
        f = 0;
    }

    private WalkingCollisionMap(int n) {
        this.regionId = n;
    }

    private static void setTileFlag(int n, int n2, int n3, int n4) {
        int n5 = n >> 3;
        int n6 = n2 >> 3;
        n5 = (n5 / 8 << 8) + n6 / 8;
        WalkingCollisionMap[] walkingCollisionMapArray = regions;
        int n7 = regions.length;
        int n8 = 0;
        while (n8 < n7) {
            WalkingCollisionMap walkingCollisionMap;
            WalkingCollisionMap walkingCollisionMap2 = walkingCollisionMap = walkingCollisionMapArray[n8];
            if (walkingCollisionMap.regionId == n5) {
                n5 = n4;
                n4 = n3;
                n3 = n2;
                n2 = n;
                WalkingCollisionMap walkingCollisionMap3 = walkingCollisionMap;
                try {
                    int n9 = walkingCollisionMap3.regionId >> 8 << 6;
                    n8 = (walkingCollisionMap3.regionId & 0xFF) << 6;
                    if (walkingCollisionMap3.tileFlags[n4] == null) {
                        walkingCollisionMap3.tileFlags[n4] = new int[64][64];
                    }
                    int[] nArray = walkingCollisionMap3.tileFlags[n4][n2 - n9];
                    int n10 = n3 - n8;
                    nArray[n10] = nArray[n10] | n5;
                    break;
                }
                catch (Exception exception) {
                    return;
                }
            }
            ++n8;
        }
    }

    private static void clearTileFlag(int n, int n2, int n3, int n4) {
        int n5 = n >> 3;
        int n6 = n2 >> 3;
        n5 = (n5 / 8 << 8) + n6 / 8;
        WalkingCollisionMap[] walkingCollisionMapArray = regions;
        int n7 = regions.length;
        int n8 = 0;
        while (n8 < n7) {
            WalkingCollisionMap walkingCollisionMap;
            WalkingCollisionMap walkingCollisionMap2 = walkingCollisionMap = walkingCollisionMapArray[n8];
            if (walkingCollisionMap.regionId == n5) {
                n5 = n4;
                n4 = n3;
                n3 = n2;
                n2 = n;
                WalkingCollisionMap walkingCollisionMap3 = walkingCollisionMap;
                int n9 = walkingCollisionMap3.regionId >> 8 << 6;
                n8 = (walkingCollisionMap3.regionId & 0xFF) << 6;
                if (walkingCollisionMap3.tileFlags != null && walkingCollisionMap3.tileFlags[n4] != null) {
                    int[] nArray = walkingCollisionMap3.tileFlags[n4][n2 - n9];
                    int n10 = n3 - n8;
                    nArray[n10] = nArray[n10] & 0xFFFFFF - n5;
                }
                return;
            }
            ++n8;
        }
    }

    private static void removeWallCollision(int n, int n2, int n3, int n4, int n5, boolean bl) {
        if (n4 == 0) {
            if (n5 == 0) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 128);
                WalkingCollisionMap.clearTileFlag(n - 1, n2, n3, 8);
            } else if (n5 == 1) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 2);
                WalkingCollisionMap.clearTileFlag(n, n2 + 1, n3, 32);
            } else if (n5 == 2) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 8);
                WalkingCollisionMap.clearTileFlag(n + 1, n2, n3, 128);
            } else if (n5 == 3) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 32);
                WalkingCollisionMap.clearTileFlag(n, n2 - 1, n3, 2);
            }
        } else if (n4 == 1 || n4 == 3) {
            if (n5 == 0) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 1);
                WalkingCollisionMap.clearTileFlag(n - 1, n2, n3, 16);
            } else if (n5 == 1) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 4);
                WalkingCollisionMap.clearTileFlag(n + 1, n2 + 1, n3, 64);
            } else if (n5 == 2) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 16);
                WalkingCollisionMap.clearTileFlag(n + 1, n2 - 1, n3, 1);
            } else if (n5 == 3) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 64);
                WalkingCollisionMap.clearTileFlag(n - 1, n2 - 1, n3, 4);
            }
        } else if (n4 == 2) {
            if (n5 == 0) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 130);
                WalkingCollisionMap.clearTileFlag(n - 1, n2, n3, 8);
                WalkingCollisionMap.clearTileFlag(n, n2 + 1, n3, 32);
            } else if (n5 == 1) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 10);
                WalkingCollisionMap.clearTileFlag(n, n2 + 1, n3, 32);
                WalkingCollisionMap.clearTileFlag(n + 1, n2, n3, 128);
            } else if (n5 == 2) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 40);
                WalkingCollisionMap.clearTileFlag(n + 1, n2, n3, 128);
                WalkingCollisionMap.clearTileFlag(n, n2 - 1, n3, 2);
            } else if (n5 == 3) {
                WalkingCollisionMap.clearTileFlag(n, n2, n3, 160);
                WalkingCollisionMap.clearTileFlag(n, n2 - 1, n3, 2);
                WalkingCollisionMap.clearTileFlag(n - 1, n2, n3, 8);
            }
        }
        if (bl) {
            if (n4 == 0) {
                if (n5 == 0) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 65536);
                    WalkingCollisionMap.clearTileFlag(n - 1, n2, n3, 4096);
                } else if (n5 == 1) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 1024);
                    WalkingCollisionMap.clearTileFlag(n, n2 + 1, n3, 16384);
                } else if (n5 == 2) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 4096);
                    WalkingCollisionMap.clearTileFlag(n + 1, n2, n3, 65536);
                } else if (n5 == 3) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 16384);
                    WalkingCollisionMap.clearTileFlag(n, n2 - 1, n3, 1024);
                }
            }
            if (n4 == 1 || n4 == 3) {
                if (n5 == 0) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 512);
                    WalkingCollisionMap.clearTileFlag(n - 1, n2 + 1, n3, 8192);
                    return;
                }
                if (n5 == 1) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 2048);
                    WalkingCollisionMap.clearTileFlag(n + 1, n2 + 1, n3, 32768);
                    return;
                }
                if (n5 == 2) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 8192);
                    WalkingCollisionMap.clearTileFlag(n + 1, n2 + 1, n3, 512);
                    return;
                }
                if (n5 == 3) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 32768);
                    WalkingCollisionMap.clearTileFlag(n - 1, n2 - 1, n3, 2048);
                    return;
                }
            } else if (n4 == 2) {
                if (n5 == 0) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 66560);
                    WalkingCollisionMap.clearTileFlag(n - 1, n2, n3, 4096);
                    WalkingCollisionMap.clearTileFlag(n, n2 + 1, n3, 16384);
                    return;
                }
                if (n5 == 1) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 5120);
                    WalkingCollisionMap.clearTileFlag(n, n2 + 1, n3, 16384);
                    WalkingCollisionMap.clearTileFlag(n + 1, n2, n3, 65536);
                    return;
                }
                if (n5 == 2) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 20480);
                    WalkingCollisionMap.clearTileFlag(n + 1, n2, n3, 65536);
                    WalkingCollisionMap.clearTileFlag(n, n2 - 1, n3, 1024);
                    return;
                }
                if (n5 == 3) {
                    WalkingCollisionMap.clearTileFlag(n, n2, n3, 81920);
                    WalkingCollisionMap.clearTileFlag(n, n2 - 1, n3, 1024);
                    WalkingCollisionMap.clearTileFlag(n - 1, n2, n3, 4096);
                }
            }
        }
    }

    private static void removeAreaCollision(int n, int n2, int n3, int n4, int n5, boolean bl) {
        int n7 = 256;
        if (bl) {
            n7 = 131328;
        }
        int n6 = n;
        while (n6 < n + n4) {
            int n8 = n2;
            while (n8 < n2 + n5) {
                WalkingCollisionMap.clearTileFlag(n6, n8, n3, n7);
                ++n8;
            }
            ++n6;
        }
    }

    public static void removeStraightWallCollision(int n, int n2, int n3, int n4) {
        WalkingCollisionMap.removeWallCollision(n, n2, n3, 0, n4, true);
    }

    public static void addObjectCollision(int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        LoadedWorldObject loadedWorldObject = new LoadedWorldObject(ObjectDefinition.forId(n), new Position(n2, n3, n4), n6, n5);
        World.getInstance().getObjectRegionIndex();
        WorldObjectRegionIndex.getOrCreateRegionBucket(loadedWorldObject.getPosition()).getLoadedObjects().add(loadedWorldObject);
        if (n < 0) {
            if (!bl) {
                WalkingCollisionMap.removeAreaCollision(n2, n3, n4, 0, 0, true);
            }
            return;
        }
        if (n == 4439) {
            if (!bl) {
                WalkingCollisionMap.removeAreaCollision(n2, n3, n4, 2, 2, true);
            }
            return;
        }
        ObjectDefinition objectDefinition = ObjectDefinition.forId(n);
        if (objectDefinition == null) {
            return;
        }
        int n7 = objectDefinition.getWidthForOrientation(n5);
        int n8 = objectDefinition.getLengthForOrientation(n5);
        if (n6 == 22) {
            ObjectDefinition objectDefinition4 = objectDefinition;
            if (objectDefinition4.interactive) {
                objectDefinition4 = objectDefinition;
                if (objectDefinition4.solid) {
                    if (!bl) {
                        objectDefinition4 = objectDefinition;
                        WalkingCollisionMap.removeWallCollision(n2, n3, n4, n6, n5, objectDefinition4.blocksProjectiles);
                    }
                    WalkingCollisionMap.setTileFlag(n2, n3, n4, 0x200000);
                    return;
                }
            }
        } else if (n6 >= 9) {
            ObjectDefinition objectDefinition2 = objectDefinition;
            if (objectDefinition2.solid) {
                int n9;
                if (!bl) {
                    objectDefinition2 = objectDefinition;
                    WalkingCollisionMap.removeAreaCollision(n2, n3, n4, n7, n8, objectDefinition2.blocksProjectiles);
                }
                int n10 = n2;
                int n11 = n3;
                int n12 = n4;
                objectDefinition2 = objectDefinition;
                n6 = objectDefinition2.blocksProjectiles ? 1 : 0;
                n5 = n8;
                n4 = n7;
                n3 = n12;
                n2 = n11;
                int n122 = n10;
                n9 = 256;
                if (n6 != 0) {
                    n9 = 131328;
                }
                n6 = n122;
                while (n6 < n122 + n4) {
                    n7 = n2;
                    while (n7 < n2 + n5) {
                        WalkingCollisionMap.setTileFlag(n6, n7, n3, n9);
                        ++n7;
                    }
                    ++n6;
                }
                return;
            }
        } else if (n6 >= 0 && n6 <= 3) {
            ObjectDefinition objectDefinition3 = objectDefinition;
            if (objectDefinition3.solid) {
                if (!bl) {
                    objectDefinition3 = objectDefinition;
                    WalkingCollisionMap.removeAreaCollision(n2, n3, n4, n7, n8, objectDefinition3.blocksProjectiles);
                }
                int n14 = n2;
                int n15 = n3;
                int n16 = n4;
                int n17 = n6;
                objectDefinition3 = objectDefinition;
                n6 = objectDefinition3.blocksProjectiles ? 1 : 0;
                n4 = n17;
                n3 = n16;
                n2 = n15;
                int n172 = n14;
                if (n4 == 0) {
                    if (n5 == 0) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 128);
                        WalkingCollisionMap.setTileFlag(n172 - 1, n2, n3, 8);
                    } else if (n5 == 1) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 2);
                        WalkingCollisionMap.setTileFlag(n172, n2 + 1, n3, 32);
                    } else if (n5 == 2) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 8);
                        WalkingCollisionMap.setTileFlag(n172 + 1, n2, n3, 128);
                    } else if (n5 == 3) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 32);
                        WalkingCollisionMap.setTileFlag(n172, n2 - 1, n3, 2);
                    }
                } else if (n4 == 1 || n4 == 3) {
                    if (n5 == 0) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 1);
                        WalkingCollisionMap.setTileFlag(n172 - 1, n2, n3, 16);
                    } else if (n5 == 1) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 4);
                        WalkingCollisionMap.setTileFlag(n172 + 1, n2 + 1, n3, 64);
                    } else if (n5 == 2) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 16);
                        WalkingCollisionMap.setTileFlag(n172 + 1, n2 - 1, n3, 1);
                    } else if (n5 == 3) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 64);
                        WalkingCollisionMap.setTileFlag(n172 - 1, n2 - 1, n3, 4);
                    }
                } else if (n4 == 2) {
                    if (n5 == 0) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 130);
                        WalkingCollisionMap.setTileFlag(n172 - 1, n2, n3, 8);
                        WalkingCollisionMap.setTileFlag(n172, n2 + 1, n3, 32);
                    } else if (n5 == 1) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 10);
                        WalkingCollisionMap.setTileFlag(n172, n2 + 1, n3, 32);
                        WalkingCollisionMap.setTileFlag(n172 + 1, n2, n3, 128);
                    } else if (n5 == 2) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 40);
                        WalkingCollisionMap.setTileFlag(n172 + 1, n2, n3, 128);
                        WalkingCollisionMap.setTileFlag(n172, n2 - 1, n3, 2);
                    } else if (n5 == 3) {
                        WalkingCollisionMap.setTileFlag(n172, n2, n3, 160);
                        WalkingCollisionMap.setTileFlag(n172, n2 - 1, n3, 2);
                        WalkingCollisionMap.setTileFlag(n172 - 1, n2, n3, 8);
                    }
                }
                if (n6 != 0) {
                    if (n4 == 0) {
                        if (n5 == 0) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 65536);
                            WalkingCollisionMap.setTileFlag(n172 - 1, n2, n3, 4096);
                        } else if (n5 == 1) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 1024);
                            WalkingCollisionMap.setTileFlag(n172, n2 + 1, n3, 16384);
                        } else if (n5 == 2) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 4096);
                            WalkingCollisionMap.setTileFlag(n172 + 1, n2, n3, 65536);
                        } else if (n5 == 3) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 16384);
                            WalkingCollisionMap.setTileFlag(n172, n2 - 1, n3, 1024);
                        }
                    }
                    if (n4 == 1 || n4 == 3) {
                        if (n5 == 0) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 512);
                            WalkingCollisionMap.setTileFlag(n172 - 1, n2 + 1, n3, 8192);
                            return;
                        }
                        if (n5 == 1) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 2048);
                            WalkingCollisionMap.setTileFlag(n172 + 1, n2 + 1, n3, 32768);
                            return;
                        }
                        if (n5 == 2) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 8192);
                            WalkingCollisionMap.setTileFlag(n172 + 1, n2 + 1, n3, 512);
                            return;
                        }
                        if (n5 == 3) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 32768);
                            WalkingCollisionMap.setTileFlag(n172 - 1, n2 - 1, n3, 2048);
                            return;
                        }
                    } else if (n4 == 2) {
                        if (n5 == 0) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 66560);
                            WalkingCollisionMap.setTileFlag(n172 - 1, n2, n3, 4096);
                            WalkingCollisionMap.setTileFlag(n172, n2 + 1, n3, 16384);
                            return;
                        }
                        if (n5 == 1) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 5120);
                            WalkingCollisionMap.setTileFlag(n172, n2 + 1, n3, 16384);
                            WalkingCollisionMap.setTileFlag(n172 + 1, n2, n3, 65536);
                            return;
                        }
                        if (n5 == 2) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 20480);
                            WalkingCollisionMap.setTileFlag(n172 + 1, n2, n3, 65536);
                            WalkingCollisionMap.setTileFlag(n172, n2 - 1, n3, 1024);
                            return;
                        }
                        if (n5 == 3) {
                            WalkingCollisionMap.setTileFlag(n172, n2, n3, 81920);
                            WalkingCollisionMap.setTileFlag(n172, n2 - 1, n3, 1024);
                            WalkingCollisionMap.setTileFlag(n172 - 1, n2, n3, 4096);
                        }
                    }
                }
            }
        }
    }

    public static void removeObjectCollision(int n, int n2, int n3, int n4, int n5, int n6) {
        ObjectDefinition objectDefinition = ObjectDefinition.forId(n);
        if (objectDefinition == null) {
            return;
        }
        int n7 = objectDefinition.getWidthForOrientation(n5);
        int n8 = objectDefinition.getLengthForOrientation(n5);
        if (n6 == 22) {
            WalkingCollisionMap.clearTileFlag(n2, n3, n4, 0x200000);
            return;
        }
        if (n6 >= 9) {
            WalkingCollisionMap.removeAreaCollision(n2, n3, n4, n7, n8, objectDefinition.blocksProjectiles);
            return;
        }
        if (n6 >= 0 && n6 <= 3) {
            WalkingCollisionMap.removeWallCollision(n2, n3, n4, n6, n5, objectDefinition.blocksProjectiles);
        }
    }

    public static int getTileFlags(int n, int n2, int n3) {
        n3 %= 4;
        int n4 = n >> 3;
        int n5 = n2 >> 3;
        n4 = (n4 / 8 << 8) + n5 / 8;
        WalkingCollisionMap[] walkingCollisionMapArray = regions;
        int n6 = regions.length;
        int n7 = 0;
        while (n7 < n6) {
            WalkingCollisionMap walkingCollisionMap;
            WalkingCollisionMap walkingCollisionMap2 = walkingCollisionMap = walkingCollisionMapArray[n7];
            if (walkingCollisionMap.regionId == n4) {
                n4 = n3;
                n3 = n2;
                n2 = n;
                WalkingCollisionMap walkingCollisionMap3 = walkingCollisionMap;
                int n8 = walkingCollisionMap3.regionId >> 8 << 6;
                n7 = (walkingCollisionMap3.regionId & 0xFF) << 6;
                if (walkingCollisionMap3.tileFlags[n4] == null) {
                    return 0;
                }
                return walkingCollisionMap3.tileFlags[n4][n2 - n8][n3 - n7];
            }
            ++n7;
        }
        return 0x200000;
    }

    public static boolean canTravelBetween(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        n = n3 - n;
        n2 = n4 - n2;
        int n8 = Math.max(Math.abs(n), Math.abs(n2));
        int n9 = 0;
        while (n9 < n8) {
            int n10 = n3 - n;
            int n11 = n4 - n2;
            int n12 = 0;
            while (n12 < n6) {
                int n13 = 0;
                while (n13 < n7) {
                    if (n < 0 && n2 < 0 ? (WalkingCollisionMap.getTileFlags(n10 + n12 - 1, n11 + n13 - 1, n5) & 0x128010E) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12 - 1, n11 + n13, n5) & 0x1280108) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12, n11 + n13 - 1, n5) & 0x1280102) != 0 : (n > 0 && n2 > 0 ? (WalkingCollisionMap.getTileFlags(n10 + n12 + 1, n11 + n13 + 1, n5) & 0x12801E0) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12 + 1, n11 + n13, n5) & 0x1280180) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12, n11 + n13 + 1, n5) & 0x1280120) != 0 : (n < 0 && n2 > 0 ? (WalkingCollisionMap.getTileFlags(n10 + n12 - 1, n11 + n13 + 1, n5) & 0x1280138) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12 - 1, n11 + n13, n5) & 0x1280108) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12, n11 + n13 + 1, n5) & 0x1280120) != 0 : (n > 0 && n2 < 0 ? (WalkingCollisionMap.getTileFlags(n10 + n12 + 1, n11 + n13 - 1, n5) & 0x1280183) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12 + 1, n11 + n13, n5) & 0x1280180) != 0 || (WalkingCollisionMap.getTileFlags(n10 + n12, n11 + n13 - 1, n5) & 0x1280102) != 0 : (n > 0 && n2 == 0 ? (WalkingCollisionMap.getTileFlags(n10 + n12 + 1, n11 + n13, n5) & 0x1280180) != 0 : (n < 0 && n2 == 0 ? (WalkingCollisionMap.getTileFlags(n10 + n12 - 1, n11 + n13, n5) & 0x1280108) != 0 : (n == 0 && n2 > 0 ? (WalkingCollisionMap.getTileFlags(n10 + n12, n11 + n13 + 1, n5) & 0x1280120) != 0 : n == 0 && n2 < 0 && (WalkingCollisionMap.getTileFlags(n10 + n12, n11 + n13 - 1, n5) & 0x1280102) != 0))))))) {
                        return false;
                    }
                    ++n13;
                }
                ++n12;
            }
            if (n < 0) {
                ++n;
            } else if (n > 0) {
                --n;
            }
            if (n2 < 0) {
                ++n2;
            } else if (n2 > 0) {
                --n2;
            }
            ++n9;
        }
        return true;
    }

    public static void loadCollisionMaps() {
        try {
            CacheStore cacheStore = CacheStore.getInstance();
            byte[] byArray = new CacheArchive(cacheStore.readFile(0, 5)).getFileBytes("map_index");
            Object object = new ByteArrayReader(byArray);
            int n = byArray.length / 7;
            regions = new WalkingCollisionMap[n];
            regionIds = new int[n];
            int[] nArray = new int[n];
            int[] nArray2 = new int[n];
            int n2 = 0;
            while (n2 < n) {
                WalkingCollisionMap.regionIds[n2] = ((ByteArrayReader)object).readUnsignedShort();
                nArray[n2] = ((ByteArrayReader)object).readUnsignedShort();
                nArray2[n2] = ((ByteArrayReader)object).readUnsignedShort();
                ((ByteArrayReader)object).readUnsignedByte();
                ++n2;
            }
            n2 = 0;
            while (n2 < n) {
                WalkingCollisionMap.regions[n2] = new WalkingCollisionMap(regionIds[n2]);
                ++n2;
            }
            n2 = 0;
            while (n2 < n) {
                object = GameplayHelper.inflateGzipCacheFile(cacheStore.readFile(4, nArray2[n2]));
                Object object2 = GameplayHelper.inflateGzipCacheFile(cacheStore.readFile(4, nArray[n2]));
                if (object != null && object2 != null) {
                    try {
                        int n3;
                        int n4;
                        int n5;
                        MapDataReader mapDataReader = new MapDataReader((byte[])object2);
                        object2 = new MapDataReader((byte[])object);
                        int n6 = regionIds[n2];
                        int n7 = n6 >> 8 << 6;
                        n6 = (n6 & 0xFF) << 6;
                        if (n7 > e) {
                            e = n7;
                        }
                        if (n6 > f) {
                            f = n6;
                        }
                        int[][][] nArray3 = new int[4][64][64];
                        int n8 = 0;
                        while (n8 < 4) {
                            n5 = 0;
                            while (n5 < 64) {
                                n4 = 0;
                                while (n4 < 64) {
                                    while ((n3 = mapDataReader.readUnsignedByte()) != 0) {
                                        if (n3 == 1) {
                                            mapDataReader.skipByte(1);
                                            break;
                                        }
                                        if (n3 <= 49) {
                                            mapDataReader.skipByte(1);
                                            continue;
                                        }
                                        if (n3 > 81) continue;
                                        nArray3[n8][n5][n4] = n3 - 49;
                                    }
                                    ++n4;
                                }
                                ++n5;
                            }
                            ++n8;
                        }
                        n8 = 0;
                        while (n8 < 4) {
                            n5 = 0;
                            while (n5 < 64) {
                                n4 = 0;
                                while (n4 < 64) {
                                    if ((nArray3[n8][n5][n4] & 1) == 1) {
                                        n3 = n8;
                                        if ((nArray3[1][n5][n4] & 2) == 2) {
                                            --n3;
                                        }
                                        if (n3 >= 0 && n3 <= 3) {
                                            WalkingCollisionMap.setTileFlag(n7 + n5, n6 + n4, n3, 0x200000);
                                        }
                                    }
                                    ++n4;
                                }
                                ++n5;
                            }
                            ++n8;
                        }
                        n8 = -1;
                        while ((n5 = ((MapDataReader)object2).readUnsignedSmart()) != 0) {
                            n8 += n5;
                            n4 = 0;
                            while ((n3 = ((MapDataReader)object2).readUnsignedSmart()) != 0) {
                                int n9 = (n4 += n3 - 1) >> 6 & 0x3F;
                                n5 = n4 & 0x3F;
                                n3 = n4 >> 12;
                                int n10 = ((MapDataReader)object2).readUnsignedByte();
                                int n11 = n10 >> 2;
                                n10 &= 3;
                                if (n8 >= 137 && n8 <= 145) {
                                    n11 = 0;
                                }
                                if (n8 == 2646) {
                                    n11 = 0;
                                }
                                if (n9 < 0 || n9 >= 64 || n5 < 0 || n5 >= 64) continue;
                                if ((nArray3[1][n9][n5] & 2) == 2) {
                                    --n3;
                                }
                                if (n3 < 0 || n3 > 3 || !GameplayHelper.isObjectDefinitionIdValid(n8)) continue;
                                WalkingCollisionMap.addObjectCollision(n8, n7 + n9, n6 + n5, n3, n10, n11, true);
                            }
                        }
                    }
                    catch (Exception exception) {
                        System.out.println("Error loading map region: " + regionIds[n2]);
                    }
                }
                ++n2;
            }
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    public static boolean hasDungeonCoordinateShiftRegion(int n) {
        int[] nArray = regionIds;
        int n2 = regionIds.length;
        int n3 = 0;
        while (n3 < n2) {
            n = nArray[n3];
            if (n == 9797) {
                return true;
            }
            ++n3;
        }
        return false;
    }
}

