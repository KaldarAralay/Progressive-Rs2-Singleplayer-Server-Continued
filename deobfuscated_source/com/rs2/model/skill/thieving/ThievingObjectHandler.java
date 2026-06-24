/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.thieving;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.thieving.LockpickTask;
import com.rs2.model.skill.thieving.TrapDisarmTask;
import com.rs2.model.task.CycleEventHandler;
import java.util.Random;

public final class ThievingObjectHandler {
    private static final Random random = new Random();

    private static void startLockpickTask(Player player, Position position, int n, int n2, double d, int n3, int n4) {
        if (!ServerSettings.thievingEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (player.getSkillManager().getCurrentLevels()[17] < n2) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("Your thieving level is not high enough to pick this lock.");
            return;
        }
        if (!player.getInventoryManager().getContainer().containsItem(1523)) {
            Player player4 = player;
            player4.packetSender.sendGameMessage("You need a lockpick to do that.");
            return;
        }
        player.getUpdateState().setAnimation(2246);
        Player player5 = player;
        player5.packetSender.sendGameMessage("You attempt to pick the lock...");
        player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(player, new LockpickTask(player, d, n, position, n3, n4), 4);
    }

    public static boolean handleThievingObject(Player player, int n, int n2, int n3) {
        int n4 = player.getPosition().getX();
        int n5 = player.getPosition().getY();
        switch (n) {
            case 2550: {
                if (n4 == 2674 && n5 == 3305) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 1, 3.5, 0, 1);
                } else {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 16, 15.0, 0, -1);
                }
                return true;
            }
            case 2556: {
                if (n4 == 2610 && n5 == 3316) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 14, 15.0, 1, 0);
                } else {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 13, 15.0, -1, 0);
                }
                return true;
            }
            case 2551: {
                if (n4 == 2674 && n5 == 3304) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 16, 15.0, 0, -1);
                } else {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 16, 15.0, 0, 1);
                }
                return true;
            }
            case 2555: {
                if (n4 == 2572 && n5 == 3288) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 61, 50.0, 0, -1);
                } else {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 61, 50.0, 0, 1);
                }
                return true;
            }
            case 2558: {
                if (n4 == 3037 && n5 == 3956) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 39, 35.0, 1, 0);
                } else if (n4 == 3041 && n5 == 3960) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 39, 35.0, 0, -1);
                } else if (n4 == 3041 && n5 == 3959) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 39, 35.0, 0, 1);
                } else if (n4 == 3045 && n5 == 3956) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 39, 35.0, -1, 0);
                } else if (n4 == 3044 && n5 == 3956) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 39, 35.0, 1, 0);
                } else {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 39, 35.0, -1, 0);
                }
                return true;
            }
            case 2557: {
                if (n4 == 3190 && n5 == 3957) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 42, 23.0, 0, 1);
                } else if (n4 == 3190 && n5 == 3958) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 42, 23.0, 0, -1);
                } else if (n4 == 3191 && n5 == 3962) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 42, 23.0, 0, 1);
                } else if (n4 == 3191 && n5 == 3963) {
                    ThievingObjectHandler.startLockpickTask(player, new Position(n2, n3), n, 42, 23.0, 0, -1);
                }
                return true;
            }
            case 2566: {
                if (n2 == 2673 && n3 == 3307) {
                    ItemStack[] itemStackArray = new ItemStack[]{new ItemStack(995, 10)};
                    double d = 7.0;
                    int n6 = 10;
                    Player player2 = player;
                    if (!ServerSettings.thievingEnabled) {
                        Player player3 = player2;
                        player3.packetSender.sendGameMessage("This skill is currently disabled.");
                    } else if (player2.getSkillManager().getCurrentLevels()[17] < 10) {
                        Player player4 = player2;
                        player4.packetSender.sendGameMessage("Your thieving level is not high enough to disarm traps.");
                    } else {
                        player2.setActionLocked(true);
                        player2.getUpdateState().setAnimation(2246);
                        Player player5 = player2;
                        player5.packetSender.sendGameMessage("You attempt to disarm the traps...");
                        CycleEventHandler.getInstance().schedule(player2, new TrapDisarmTask(player2, 7.0, itemStackArray, n2, n3, n), 3);
                    }
                }
                return true;
            }
        }
        return false;
    }

    static /* synthetic */ Random getRandom() {
        return random;
    }
}

