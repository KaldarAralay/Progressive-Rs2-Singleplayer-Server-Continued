/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.CompostBin;
import com.rs2.model.skill.farming.CompostBinCloseTask;
import com.rs2.model.skill.farming.CompostBinEmptyTask;
import com.rs2.model.skill.farming.CompostBinFillTask;
import com.rs2.model.skill.farming.CompostBinOpenTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class CompostBinManager {
    private Player player;
    public int[] states = new int[4];
    public int[] itemIds = new int[4];
    public long[] lastUpdateTicks = new long[4];
    private static int[] compostableItemIds = new int[]{239, 249, 251, 253, 255, 257, 259, 261, 263, 265, 267, 269, 753, 1942, 1951, 1957, 1965, 2126, 2481, 2998, 3000, 5504, 5986, 6018, 6055};
    private static int[] supercompostableItemIds = new int[]{247, 2114, 5978, 5980, 5982, 6004, 6469};

    public CompostBinManager(Player player) {
        this.player = player;
    }

    public final void refreshConfig() {
        int[] nArray = new int[this.states.length];
        int n = 0;
        while (n < this.states.length) {
            int n2;
            int n3 = this.itemIds[n];
            int n4 = n2 = this.states[n];
            if (n2 > 0 && n2 <= 30) {
                if (n3 == 6034) {
                    n4 += 32;
                }
                if (n3 == 2518) {
                    n4 += 128;
                }
            }
            nArray[n] = n4;
            ++n;
        }
        int n5 = nArray[0] | nArray[1] << 8 | nArray[2] << 16 | nArray[3] << 24;
        Player player = this.player;
        player.packetSender.sendConfig(511, n5);
    }

    private void startEmptyBin(int n) {
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        int n2 = this.itemIds[n];
        int n3 = this.player.nextActionSequence();
        this.player.getUpdateState().setAnimation(832, 0);
        this.player.setActiveCycleEvent(new CompostBinEmptyTask(this, n3, n, n2));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
    }

    public final boolean fillBin(int n, int n2, int n3) {
        CompostBin compostBin = CompostBin.forPosition(new Position(n2, n3));
        if (compostBin == null) {
            return false;
        }
        int n4 = compostBin.getIndex();
        if (this.states[n4] < 15) {
            Position position = new Position(n2, n3);
            n3 = n;
            Object object = position;
            Object object2 = this;
            object = CompostBin.forPosition((Position)object);
            n4 = ((CompostBin)((Object)object)).getIndex();
            if (object != null) {
                if (!ServerSettings.farmingEnabled) {
                    object2 = ((CompostBinManager)object2).player;
                    ((Player)object2).packetSender.sendGameMessage("This skill is currently disabled.");
                } else if (((CompostBinManager)object2).states[n4] < 15) {
                    int n5;
                    int n6 = 0;
                    int[] nArray = compostableItemIds;
                    int n7 = 0;
                    while (n7 < 25) {
                        n5 = nArray[n7];
                        if (n3 == n5) {
                            ((CompostBinManager)object2).itemIds[n4] = 6032;
                            n6 = 1;
                        }
                        ++n7;
                    }
                    nArray = supercompostableItemIds;
                    n7 = 0;
                    while (n7 < 7) {
                        n5 = nArray[n7];
                        if (n3 == n5) {
                            if (((CompostBinManager)object2).states[n4] == 0) {
                                ((CompostBinManager)object2).itemIds[n4] = 6034;
                            }
                            n6 = 1;
                        }
                        ++n7;
                    }
                    if (n3 == 1982) {
                        if (((CompostBinManager)object2).states[n4] == 0) {
                            ((CompostBinManager)object2).itemIds[n4] = 2518;
                        }
                        n6 = 1;
                    }
                    if (n6 == 0) {
                        object2 = ((CompostBinManager)object2).player;
                        ((Player)object2).packetSender.sendGameMessage("You need to put organic items into the compost bin in order to make compost.");
                    } else {
                        n5 = n6;
                        n7 = ((CompostBinManager)object2).player.nextActionSequence();
                        ((CompostBinManager)object2).player.setActiveCycleEvent(new CompostBinFillTask((CompostBinManager)object2, n7, n3, n4, n5));
                        CycleEventHandler.getInstance().schedule(((CompostBinManager)object2).player, ((CompostBinManager)object2).player.getActiveCycleEvent(), 2);
                    }
                }
            }
            return true;
        }
        if (this.states[n4] >= 16 && this.states[n4] <= 30) {
            if (n == 1925) {
                this.startEmptyBin(n4);
            } else {
                Player player = this.player;
                player.packetSender.sendGameMessage("You might need some buckets to gather the compost.");
            }
            return true;
        }
        return false;
    }

    public final void processRotting() {
        int n = 0;
        while (n < this.states.length) {
            long l;
            int n2 = n;
            CompostBinManager compostBinManager = this;
            if ((compostBinManager.itemIds[n2] == 6032 && compostBinManager.states[n2] >= 65 && compostBinManager.states[n2] <= 126 ? true : (compostBinManager.itemIds[n2] == 6034 && compostBinManager.states[n2] == 32 ? true : compostBinManager.itemIds[n2] == 2518 && compostBinManager.states[n2] >= 65 && compostBinManager.states[n2] <= 126)) && (l = Server.getElapsedMinutes() - this.lastUpdateTicks[n]) > 0L) {
                if (this.itemIds[n] == 6034) {
                    if (l >= 90L) {
                        this.states[n] = 31;
                    }
                } else {
                    int n3 = n;
                    this.states[n3] = (int)((long)this.states[n3] - l);
                    if (this.states[n] <= 64) {
                        this.states[n] = 64;
                    }
                    this.lastUpdateTicks[n] = Server.getElapsedMinutes();
                }
            }
            ++n;
        }
        this.refreshConfig();
    }

    public final boolean handleBinObject(int n, int n2) {
        CompostBin compostBin = CompostBin.forPosition(new Position(n, n2));
        if (compostBin == null) {
            return false;
        }
        int n3 = compostBin.getIndex();
        if (this.states[n3] == 15) {
            boolean bl;
            int n4 = n3;
            CompostBinManager compostBinManager = this;
            if (!ServerSettings.farmingEnabled) {
                Player player = compostBinManager.player;
                player.packetSender.sendGameMessage("This skill is currently disabled.");
                bl = true;
            } else if (compostBinManager.states[n4] != 15) {
                bl = true;
            } else {
                if (compostBinManager.itemIds[n4] == 6032) {
                    compostBinManager.states[n4] = 99 + GameUtil.randomInt(16);
                }
                if (compostBinManager.itemIds[n4] == 6034) {
                    compostBinManager.states[n4] = 32;
                }
                if (compostBinManager.itemIds[n4] == 2518) {
                    compostBinManager.states[n4] = 99 + GameUtil.randomInt(16);
                }
                compostBinManager.lastUpdateTicks[n4] = Server.getElapsedMinutes();
                compostBinManager.player.getUpdateState().setAnimation(835, 0);
                compostBinManager.player.setActionLocked(true);
                CycleEventHandler.getInstance().schedule(compostBinManager.player, new CompostBinCloseTask(compostBinManager), 2);
                bl = true;
            }
            return true;
        }
        if (this.states[n3] >= 16 && this.states[n3] <= 30) {
            this.startEmptyBin(n3);
            return true;
        }
        int n5 = n3;
        CompostBinManager compostBinManager = this;
        if (compostBinManager.itemIds[n5] == 6032 && compostBinManager.states[n5] >= 64 && compostBinManager.states[n5] <= 126 ? true : (compostBinManager.itemIds[n5] == 6034 && (compostBinManager.states[n5] == 31 || compostBinManager.states[n5] == 32) ? true : compostBinManager.itemIds[n5] == 2518 && compostBinManager.states[n5] >= 64 && compostBinManager.states[n5] <= 126)) {
            n5 = n3;
            compostBinManager = this;
            n3 = 0;
            if (compostBinManager.itemIds[n5] == 6032 && compostBinManager.states[n5] == 64) {
                n3 = 1;
            } else if (compostBinManager.itemIds[n5] == 6034 && compostBinManager.states[n5] == 31) {
                n3 = 1;
            } else if (compostBinManager.itemIds[n5] == 2518 && compostBinManager.states[n5] == 64) {
                n3 = 1;
            }
            if (n3 != 0) {
                compostBinManager.states[n5] = 30;
                compostBinManager.player.getUpdateState().setAnimation(834, 0);
                CycleEventHandler.getInstance().schedule(compostBinManager.player, new CompostBinOpenTask(compostBinManager), 2);
            } else {
                Player player = compostBinManager.player;
                player.packetSender.sendGameMessage("The compost bin is still rotting. I should wait until it is complete.");
            }
            return true;
        }
        return false;
    }

    static /* synthetic */ Player getPlayer(CompostBinManager compostBinManager) {
        return compostBinManager.player;
    }
}

