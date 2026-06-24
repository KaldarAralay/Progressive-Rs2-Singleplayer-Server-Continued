/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.abyss;

import com.rs2.model.gameplay.abyss.AbyssManager;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class AbyssObstacleEvent
extends CycleEvent {
    private int remainingObstaclePhase;
    private final /* synthetic */ Player player;
    private final /* synthetic */ String obstacleAction;
    private final /* synthetic */ int temporaryObjectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ LoadedWorldObject sourceObject;
    private final /* synthetic */ int finalObjectId;
    private final /* synthetic */ int destinationX;
    private final /* synthetic */ int destinationY;

    public AbyssObstacleEvent(int n, Player player, String string, int n2, int n3, int n4, LoadedWorldObject loadedWorldObject, int n5, int n6, int n7) {
        this.player = player;
        this.obstacleAction = string;
        this.temporaryObjectId = n2;
        this.objectX = n3;
        this.objectY = n4;
        this.sourceObject = loadedWorldObject;
        this.finalObjectId = n5;
        this.destinationX = n6;
        this.destinationY = n7;
        this.remainingObstaclePhase = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        AbyssManager.replayObstacleAttemptAnimation(this.player, this.obstacleAction);
        if (this.remainingObstaclePhase == 2) {
            if (GameUtil.randomInclusive(3) == 0) {
                --this.remainingObstaclePhase;
                new DynamicObject(this.temporaryObjectId, this.objectX, this.objectY, this.player.getPosition().getPlane(), this.sourceObject.getOrientation(), this.sourceObject.getType(), this.sourceObject.getWorldObject().getObjectId(), 1000);
                return;
            }
            AbyssManager.sendObstacleFailureMessage(this.player, this.obstacleAction);
            cycleEventContainer.stop();
            return;
        }
        if (this.remainingObstaclePhase == 1) {
            --this.remainingObstaclePhase;
            ObjectManager.getInstance().removeDynamicObjectAt(this.objectX, this.objectY, this.player.getPosition().getPlane(), 10);
            new DynamicObject(this.finalObjectId, this.objectX, this.objectY, this.player.getPosition().getPlane(), this.sourceObject.getOrientation(), this.sourceObject.getType(), this.sourceObject.getWorldObject().getObjectId(), 20);
            return;
        }
        if (this.remainingObstaclePhase == 0) {
            AbyssManager.sendObstacleSuccessMessage(this.player, this.obstacleAction);
            AbyssManager.scheduleDelayedObstacleMove(this.player, this.destinationX, this.destinationY);
            cycleEventContainer.stop();
        }
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

