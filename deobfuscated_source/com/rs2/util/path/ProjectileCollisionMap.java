/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.path;

import com.rs2.cache.CacheArchive;
import com.rs2.cache.CacheStore;
import com.rs2.model.GameplayHelper;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.path.MapDataReader;

public final class ProjectileCollisionMap {
    private static ProjectileCollisionMap[] regions;
    private int regionId;
    private int[][][] tileFlags = new int[4][][];

    private ProjectileCollisionMap(int n) {
        this.regionId = n;
    }

    private static void setTileFlag(int n, int n2, int n3, int n4) {
        int n5 = n >> 3;
        int n6 = n2 >> 3;
        n5 = (n5 / 8 << 8) + n6 / 8;
        ProjectileCollisionMap[] projectileCollisionMapArray = regions;
        int n7 = regions.length;
        int n8 = 0;
        while (n8 < n7) {
            ProjectileCollisionMap projectileCollisionMap;
            ProjectileCollisionMap projectileCollisionMap2 = projectileCollisionMap = projectileCollisionMapArray[n8];
            if (projectileCollisionMap.regionId == n5) {
                n5 = n4;
                n4 = n3;
                n3 = n2;
                n2 = n;
                ProjectileCollisionMap projectileCollisionMap3 = projectileCollisionMap;
                try {
                    int n9 = projectileCollisionMap3.regionId >> 8 << 6;
                    n8 = (projectileCollisionMap3.regionId & 0xFF) << 6;
                    if (projectileCollisionMap3.tileFlags[n4] == null) {
                        projectileCollisionMap3.tileFlags[n4] = new int[64][64];
                    }
                    int[] nArray = projectileCollisionMap3.tileFlags[n4][n2 - n9];
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
        ProjectileCollisionMap[] projectileCollisionMapArray = regions;
        int n7 = regions.length;
        int n8 = 0;
        while (n8 < n7) {
            ProjectileCollisionMap projectileCollisionMap;
            ProjectileCollisionMap projectileCollisionMap2 = projectileCollisionMap = projectileCollisionMapArray[n8];
            if (projectileCollisionMap.regionId == n5) {
                n5 = n4;
                n4 = n3;
                n3 = n2;
                n2 = n;
                ProjectileCollisionMap projectileCollisionMap3 = projectileCollisionMap;
                int n9 = projectileCollisionMap3.regionId >> 8 << 6;
                n8 = (projectileCollisionMap3.regionId & 0xFF) << 6;
                if (projectileCollisionMap3.tileFlags != null && projectileCollisionMap3.tileFlags[n4] != null) {
                    int[] nArray = projectileCollisionMap3.tileFlags[n4][n2 - n9];
                    int n10 = n3 - n8;
                    nArray[n10] = nArray[n10] & 0xFFFFFF - n5;
                }
                return;
            }
            ++n8;
        }
    }

    public static void removeWallCollision(int n, int n2, int n3, int n4, int n5, boolean bl) {
        if (n4 == 0) {
            if (n5 == 0) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 128);
                ProjectileCollisionMap.clearTileFlag(n - 1, n2, n3, 8);
            } else if (n5 == 1) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 2);
                ProjectileCollisionMap.clearTileFlag(n, n2 + 1, n3, 32);
            } else if (n5 == 2) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 8);
                ProjectileCollisionMap.clearTileFlag(n + 1, n2, n3, 128);
            } else if (n5 == 3) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 32);
                ProjectileCollisionMap.clearTileFlag(n, n2 - 1, n3, 2);
            }
        } else if (n4 == 1 || n4 == 3) {
            if (n5 == 0) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 1);
                ProjectileCollisionMap.clearTileFlag(n - 1, n2, n3, 16);
            } else if (n5 == 1) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 4);
                ProjectileCollisionMap.clearTileFlag(n + 1, n2 + 1, n3, 64);
            } else if (n5 == 2) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 16);
                ProjectileCollisionMap.clearTileFlag(n + 1, n2 - 1, n3, 1);
            } else if (n5 == 3) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 64);
                ProjectileCollisionMap.clearTileFlag(n - 1, n2 - 1, n3, 4);
            }
        } else if (n4 == 2) {
            if (n5 == 0) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 130);
                ProjectileCollisionMap.clearTileFlag(n - 1, n2, n3, 8);
                ProjectileCollisionMap.clearTileFlag(n, n2 + 1, n3, 32);
            } else if (n5 == 1) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 10);
                ProjectileCollisionMap.clearTileFlag(n, n2 + 1, n3, 32);
                ProjectileCollisionMap.clearTileFlag(n + 1, n2, n3, 128);
            } else if (n5 == 2) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 40);
                ProjectileCollisionMap.clearTileFlag(n + 1, n2, n3, 128);
                ProjectileCollisionMap.clearTileFlag(n, n2 - 1, n3, 2);
            } else if (n5 == 3) {
                ProjectileCollisionMap.clearTileFlag(n, n2, n3, 160);
                ProjectileCollisionMap.clearTileFlag(n, n2 - 1, n3, 2);
                ProjectileCollisionMap.clearTileFlag(n - 1, n2, n3, 8);
            }
        }
        if (bl) {
            if (n4 == 0) {
                if (n5 == 0) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 65536);
                    ProjectileCollisionMap.clearTileFlag(n - 1, n2, n3, 4096);
                } else if (n5 == 1) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 1024);
                    ProjectileCollisionMap.clearTileFlag(n, n2 + 1, n3, 16384);
                } else if (n5 == 2) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 4096);
                    ProjectileCollisionMap.clearTileFlag(n + 1, n2, n3, 65536);
                } else if (n5 == 3) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 16384);
                    ProjectileCollisionMap.clearTileFlag(n, n2 - 1, n3, 1024);
                }
            }
            if (n4 == 1 || n4 == 3) {
                if (n5 == 0) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 512);
                    ProjectileCollisionMap.clearTileFlag(n - 1, n2 + 1, n3, 8192);
                    return;
                }
                if (n5 == 1) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 2048);
                    ProjectileCollisionMap.clearTileFlag(n + 1, n2 + 1, n3, 32768);
                    return;
                }
                if (n5 == 2) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 8192);
                    ProjectileCollisionMap.clearTileFlag(n + 1, n2 + 1, n3, 512);
                    return;
                }
                if (n5 == 3) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 32768);
                    ProjectileCollisionMap.clearTileFlag(n - 1, n2 - 1, n3, 2048);
                    return;
                }
            } else if (n4 == 2) {
                if (n5 == 0) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 66560);
                    ProjectileCollisionMap.clearTileFlag(n - 1, n2, n3, 4096);
                    ProjectileCollisionMap.clearTileFlag(n, n2 + 1, n3, 16384);
                    return;
                }
                if (n5 == 1) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 5120);
                    ProjectileCollisionMap.clearTileFlag(n, n2 + 1, n3, 16384);
                    ProjectileCollisionMap.clearTileFlag(n + 1, n2, n3, 65536);
                    return;
                }
                if (n5 == 2) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 20480);
                    ProjectileCollisionMap.clearTileFlag(n + 1, n2, n3, 65536);
                    ProjectileCollisionMap.clearTileFlag(n, n2 - 1, n3, 1024);
                    return;
                }
                if (n5 == 3) {
                    ProjectileCollisionMap.clearTileFlag(n, n2, n3, 81920);
                    ProjectileCollisionMap.clearTileFlag(n, n2 - 1, n3, 1024);
                    ProjectileCollisionMap.clearTileFlag(n - 1, n2, n3, 4096);
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
                ProjectileCollisionMap.clearTileFlag(n6, n8, n3, n7);
                ++n8;
            }
            ++n6;
        }
    }

    public static void addObjectCollision(int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        ObjectDefinition objectDefinition;
        block68: {
            block67: {
                if (n < 0) break block67;
                objectDefinition = ObjectDefinition.forId(n);
                if (!objectDefinition.projectileCollisionIgnored) break block68;
            }
            if (!bl) {
                ProjectileCollisionMap.removeAreaCollision(n2, n3, n4, 0, 0, true);
            }
            return;
        }
        ObjectDefinition objectDefinition2 = ObjectDefinition.forId(n);
        if (objectDefinition2 == null) {
            return;
        }
        int n7 = objectDefinition2.getWidthForOrientation(n5);
        int n8 = objectDefinition2.getLengthForOrientation(n5);
        if (n6 == 22) {
            objectDefinition = objectDefinition2;
            if (objectDefinition.interactive) {
                objectDefinition = objectDefinition2;
                if (objectDefinition.solid) {
                    if (!bl) {
                        objectDefinition = objectDefinition2;
                        ProjectileCollisionMap.removeWallCollision(n2, n3, n4, n6, n5, objectDefinition.blocksProjectiles);
                    }
                    ProjectileCollisionMap.setTileFlag(n2, n3, n4, 0x200000);
                    return;
                }
            }
        } else if (n6 >= 9) {
            objectDefinition = objectDefinition2;
            if (objectDefinition.solid) {
                int n9;
                if (!bl) {
                    objectDefinition = objectDefinition2;
                    ProjectileCollisionMap.removeAreaCollision(n2, n3, n4, n7, n8, objectDefinition.blocksProjectiles);
                }
                int n10 = n2;
                int n11 = n3;
                int n12 = n4;
                objectDefinition = objectDefinition2;
                n6 = objectDefinition.blocksProjectiles ? 1 : 0;
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
                        ProjectileCollisionMap.setTileFlag(n6, n7, n3, n9);
                        ++n7;
                    }
                    ++n6;
                }
                return;
            }
        } else if (n6 >= 0 && n6 <= 3) {
            objectDefinition = objectDefinition2;
            if (objectDefinition.solid) {
                if (!bl) {
                    objectDefinition = objectDefinition2;
                    ProjectileCollisionMap.removeAreaCollision(n2, n3, n4, n7, n8, objectDefinition.blocksProjectiles);
                }
                int n14 = n2;
                int n15 = n3;
                int n16 = n4;
                int n17 = n6;
                objectDefinition = objectDefinition2;
                n6 = objectDefinition.blocksProjectiles ? 1 : 0;
                n4 = n17;
                n3 = n16;
                n2 = n15;
                int n172 = n14;
                if (n4 == 0) {
                    if (n5 == 0) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 128);
                        ProjectileCollisionMap.setTileFlag(n172 - 1, n2, n3, 8);
                    } else if (n5 == 1) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 2);
                        ProjectileCollisionMap.setTileFlag(n172, n2 + 1, n3, 32);
                    } else if (n5 == 2) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 8);
                        ProjectileCollisionMap.setTileFlag(n172 + 1, n2, n3, 128);
                    } else if (n5 == 3) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 32);
                        ProjectileCollisionMap.setTileFlag(n172, n2 - 1, n3, 2);
                    }
                } else if (n4 == 1 || n4 == 3) {
                    if (n5 == 0) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 1);
                        ProjectileCollisionMap.setTileFlag(n172 - 1, n2, n3, 16);
                    } else if (n5 == 1) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 4);
                        ProjectileCollisionMap.setTileFlag(n172 + 1, n2 + 1, n3, 64);
                    } else if (n5 == 2) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 16);
                        ProjectileCollisionMap.setTileFlag(n172 + 1, n2 - 1, n3, 1);
                    } else if (n5 == 3) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 64);
                        ProjectileCollisionMap.setTileFlag(n172 - 1, n2 - 1, n3, 4);
                    }
                } else if (n4 == 2) {
                    if (n5 == 0) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 130);
                        ProjectileCollisionMap.setTileFlag(n172 - 1, n2, n3, 8);
                        ProjectileCollisionMap.setTileFlag(n172, n2 + 1, n3, 32);
                    } else if (n5 == 1) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 10);
                        ProjectileCollisionMap.setTileFlag(n172, n2 + 1, n3, 32);
                        ProjectileCollisionMap.setTileFlag(n172 + 1, n2, n3, 128);
                    } else if (n5 == 2) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 40);
                        ProjectileCollisionMap.setTileFlag(n172 + 1, n2, n3, 128);
                        ProjectileCollisionMap.setTileFlag(n172, n2 - 1, n3, 2);
                    } else if (n5 == 3) {
                        ProjectileCollisionMap.setTileFlag(n172, n2, n3, 160);
                        ProjectileCollisionMap.setTileFlag(n172, n2 - 1, n3, 2);
                        ProjectileCollisionMap.setTileFlag(n172 - 1, n2, n3, 8);
                    }
                }
                if (n6 != 0) {
                    if (n4 == 0) {
                        if (n5 == 0) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 65536);
                            ProjectileCollisionMap.setTileFlag(n172 - 1, n2, n3, 4096);
                        } else if (n5 == 1) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 1024);
                            ProjectileCollisionMap.setTileFlag(n172, n2 + 1, n3, 16384);
                        } else if (n5 == 2) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 4096);
                            ProjectileCollisionMap.setTileFlag(n172 + 1, n2, n3, 65536);
                        } else if (n5 == 3) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 16384);
                            ProjectileCollisionMap.setTileFlag(n172, n2 - 1, n3, 1024);
                        }
                    }
                    if (n4 == 1 || n4 == 3) {
                        if (n5 == 0) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 512);
                            ProjectileCollisionMap.setTileFlag(n172 - 1, n2 + 1, n3, 8192);
                            return;
                        }
                        if (n5 == 1) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 2048);
                            ProjectileCollisionMap.setTileFlag(n172 + 1, n2 + 1, n3, 32768);
                            return;
                        }
                        if (n5 == 2) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 8192);
                            ProjectileCollisionMap.setTileFlag(n172 + 1, n2 + 1, n3, 512);
                            return;
                        }
                        if (n5 == 3) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 32768);
                            ProjectileCollisionMap.setTileFlag(n172 - 1, n2 - 1, n3, 2048);
                            return;
                        }
                    } else if (n4 == 2) {
                        if (n5 == 0) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 66560);
                            ProjectileCollisionMap.setTileFlag(n172 - 1, n2, n3, 4096);
                            ProjectileCollisionMap.setTileFlag(n172, n2 + 1, n3, 16384);
                            return;
                        }
                        if (n5 == 1) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 5120);
                            ProjectileCollisionMap.setTileFlag(n172, n2 + 1, n3, 16384);
                            ProjectileCollisionMap.setTileFlag(n172 + 1, n2, n3, 65536);
                            return;
                        }
                        if (n5 == 2) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 20480);
                            ProjectileCollisionMap.setTileFlag(n172 + 1, n2, n3, 65536);
                            ProjectileCollisionMap.setTileFlag(n172, n2 - 1, n3, 1024);
                            return;
                        }
                        if (n5 == 3) {
                            ProjectileCollisionMap.setTileFlag(n172, n2, n3, 81920);
                            ProjectileCollisionMap.setTileFlag(n172, n2 - 1, n3, 1024);
                            ProjectileCollisionMap.setTileFlag(n172 - 1, n2, n3, 4096);
                        }
                    }
                }
            }
        }
    }

    public static void removeObjectCollisionForReachability(int n, int n2, int n3, int n4, int n5, int n6) {
        ObjectDefinition objectDefinition = ObjectDefinition.forId(n);
        if (objectDefinition == null) {
            System.out.println("ID: " + n + " HAS NO DEF");
            return;
        }
        n = objectDefinition.getWidthForOrientation(n5);
        int n7 = objectDefinition.getLengthForOrientation(n5);
        ObjectDefinition objectDefinition2 = objectDefinition;
        ProjectileCollisionMap.removeAreaCollision(n2, n3, n4, 1, 1, objectDefinition2.blocksProjectiles);
        if (n6 == 22) {
            ProjectileCollisionMap.clearTileFlag(n2, n3, n4, 0x200000);
            return;
        }
        if (n6 >= 9) {
            objectDefinition2 = objectDefinition;
            ProjectileCollisionMap.removeAreaCollision(n2, n3, n4, n, n7, objectDefinition2.blocksProjectiles);
            return;
        }
        if (n6 >= 0 && n6 <= 3) {
            objectDefinition2 = objectDefinition;
            ProjectileCollisionMap.removeWallCollision(n2, n3, n4, n6, n5, objectDefinition2.blocksProjectiles);
        }
    }

    public static void removeObjectCollision(int n, int n2, int n3, int n4, int n5, int n6) {
        ObjectDefinition objectDefinition = ObjectDefinition.forId(n);
        if (objectDefinition == null) {
            System.out.println("ID: " + n + " HAS NO DEF");
            return;
        }
        n = objectDefinition.getWidthForOrientation(n5);
        int n7 = objectDefinition.getLengthForOrientation(n5);
        if (n6 == 22) {
            ProjectileCollisionMap.clearTileFlag(n2, n3, n4, 0x200000);
            return;
        }
        if (n6 >= 9) {
            ObjectDefinition objectDefinition2 = objectDefinition;
            ProjectileCollisionMap.removeAreaCollision(n2, n3, n4, n, n7, objectDefinition2.blocksProjectiles);
            return;
        }
        if (n6 >= 0 && n6 <= 3) {
            ObjectDefinition objectDefinition3 = objectDefinition;
            ProjectileCollisionMap.removeWallCollision(n2, n3, n4, n6, n5, objectDefinition3.blocksProjectiles);
        }
    }

    public static int getTileFlags(int n, int n2, int n3) {
        if (n3 > 3) {
            n3 = 0;
        }
        int n4 = n >> 3;
        int n5 = n2 >> 3;
        n4 = (n4 / 8 << 8) + n5 / 8;
        ProjectileCollisionMap[] projectileCollisionMapArray = regions;
        int n6 = regions.length;
        int n7 = 0;
        while (n7 < n6) {
            ProjectileCollisionMap projectileCollisionMap;
            ProjectileCollisionMap projectileCollisionMap2 = projectileCollisionMap = projectileCollisionMapArray[n7];
            if (projectileCollisionMap.regionId == n4) {
                n4 = n3;
                n3 = n2;
                n2 = n;
                ProjectileCollisionMap projectileCollisionMap3 = projectileCollisionMap;
                int n8 = projectileCollisionMap3.regionId >> 8 << 6;
                n7 = (projectileCollisionMap3.regionId & 0xFF) << 6;
                if (projectileCollisionMap3.tileFlags[n4] == null) {
                    return 0;
                }
                return projectileCollisionMap3.tileFlags[n4][n2 - n8][n3 - n7];
            }
            ++n7;
        }
        return 0;
    }

    public static void loadCollisionMaps() {
        try {
            CacheStore cacheStore = CacheStore.getInstance();
            byte[] byArray = new CacheArchive(cacheStore.readFile(0, 5)).getFileBytes("map_index");
            Object object = new ByteArrayReader(byArray);
            int n = byArray.length / 7;
            regions = new ProjectileCollisionMap[n];
            int[] nArray = new int[n];
            int[] nArray2 = new int[n];
            int[] nArray3 = new int[n];
            int n2 = 0;
            while (n2 < n) {
                nArray[n2] = ((ByteArrayReader)object).readUnsignedShort();
                nArray2[n2] = ((ByteArrayReader)object).readUnsignedShort();
                nArray3[n2] = ((ByteArrayReader)object).readUnsignedShort();
                ((ByteArrayReader)object).readUnsignedByte();
                ++n2;
            }
            n2 = 0;
            while (n2 < n) {
                ProjectileCollisionMap.regions[n2] = new ProjectileCollisionMap(nArray[n2]);
                ++n2;
            }
            n2 = 0;
            while (n2 < n) {
                object = GameplayHelper.inflateGzipCacheFile(cacheStore.readFile(4, nArray3[n2]));
                Object object2 = GameplayHelper.inflateGzipCacheFile(cacheStore.readFile(4, nArray2[n2]));
                if (object != null && object2 != null) {
                    try {
                        int n3;
                        int n4;
                        int n5;
                        MapDataReader mapDataReader = new MapDataReader((byte[])object2);
                        object2 = new MapDataReader((byte[])object);
                        int n6 = nArray[n2];
                        int n7 = n6 >> 8 << 6;
                        n6 = (n6 & 0xFF) << 6;
                        int[][][] nArray4 = new int[4][64][64];
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
                                        nArray4[n8][n5][n4] = n3 - 49;
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
                                if (n9 < 0 || n9 >= 64 || n5 < 0 || n5 >= 64) continue;
                                if ((nArray4[1][n9][n5] & 2) == 2) {
                                    --n3;
                                }
                                if (!GameplayHelper.isObjectDefinitionIdValid(n8) || n3 < 0 || n3 > 3) continue;
                                ObjectDefinition objectDefinition = ObjectDefinition.forId(n8);
                                if (objectDefinition.projectileCollisionIgnored) continue;
                                ProjectileCollisionMap.addObjectCollision(n8, n7 + n9, n6 + n5, n3, n10, n11, true);
                            }
                        }
                    }
                    catch (Exception exception) {
                        System.out.println("Error loading map region: " + nArray[n2]);
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
}

