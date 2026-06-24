/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.agility;

import com.rs2.ServerSettings;
import com.rs2.model.objects.ObjectRegionKey;
import com.rs2.model.player.Player;
import com.rs2.model.skill.agility.AgilityMovementFinishTask;
import com.rs2.model.skill.agility.AgilityMovementStepTask;
import com.rs2.model.skill.agility.AgilityObstacleCompletionTask;
import com.rs2.model.skill.agility.AgilityPositionOffsetTask;
import com.rs2.model.skill.agility.AgilityQueuedMovementTask;
import com.rs2.model.skill.agility.AgilityShortcutStartTask;
import com.rs2.model.task.CycleEventHandler;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AgilityObstacleHandler {
    private List loadedObjects = new LinkedList();

    public static void startDelayedShortcutMovement(Player player, int n, int n2, int n3, int n4, int n5, int n6) {
        if (!ServerSettings.agilityEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        player.getUpdateState().setAnimation(751);
        CycleEventHandler.getInstance().schedule(player, new AgilityShortcutStartTask(player, n2, 0, 30, 2, 50), 2);
    }

    /*
     * WARNING - void declaration
     */
    public static void startForcedMovement(Player player, int n, int n2, int n3, int n4, int n5, boolean bl, int n6, int n7) {
        int n8 = n7;
        player.setActionLocked(true);
        player.getMovementQueue().clear();
        if (n8 > 0) {
            player.setRunAnimationOverride(n8);
            player.setWalkAnimationOverride(n8);
            player.setAppearanceUpdateRequired(true);
        }
        n3 = 2;
        if (n > 0) {
            n3 = 1;
        } else if (n < 0) {
            n3 = 3;
        } else if (n2 > 0) {
            n3 = 0;
        }
        int n9 = player.getPosition().getX() + n;
        n8 = player.getPosition().getY() + n2;
        int n10 = player.getPosition().getPlane();
        player.forcedMovementActive = true;
        CycleEventHandler.getInstance().schedule(player, new AgilityMovementStepTask(player, n, n2, 1, n4, n3), 1);
        CycleEventHandler.getInstance().schedule(player, new AgilityMovementFinishTask(player, n6, true, n9, n8, n10), n5 + 1);
    }

    public static void startQueuedObstacleMovement(Player player, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        if (!ServerSettings.agilityEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        CycleEventHandler.getInstance().schedule(player, new AgilityQueuedMovementTask(player, 0, n3, 60, 1, n, 751), 0);
    }

    public static void startAgilityMovement(Player player, double d, int n, int n2, int n3, int n4, int n5, int n6, String string, String string2) {
        if (!ServerSettings.agilityEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (n3 > 0) {
            player.getUpdateState().setAnimation(n3);
        }
        player.setActionLocked(true);
        Player player3 = player;
        player3.packetSender.sendGameMessage(string);
        CycleEventHandler.getInstance().schedule(player, new AgilityObstacleCompletionTask(player, n, n2, n4, n5, n6, d, string2), n3 > 0 ? 2 : 0);
    }

    public static void startPositionOffsetObstacle(Player player, double d, int n, int n2, int n3, int n4, int n5, String string, String string2) {
        if (!ServerSettings.agilityEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        player.getUpdateState().setAnimation(828);
        player.setActionLocked(true);
        Player player3 = player;
        player3.packetSender.sendGameMessage(string);
        CycleEventHandler.getInstance().schedule(player, new AgilityPositionOffsetTask(player, n, n2, n3, d, string2), n5);
    }

    public AgilityObstacleHandler(ObjectRegionKey objectRegionKey) {
    }

    public Collection getLoadedObjects() {
        return this.loadedObjects;
    }
}

