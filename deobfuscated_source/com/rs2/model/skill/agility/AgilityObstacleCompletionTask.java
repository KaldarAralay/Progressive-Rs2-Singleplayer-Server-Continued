/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.agility;

import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AgilityObstacleCompletionTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int deltaX;
    private final /* synthetic */ int deltaY;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ int delayedAnimationId;
    private final /* synthetic */ int duration;
    private final /* synthetic */ double experience;
    private final /* synthetic */ String completionMessage;

    public AgilityObstacleCompletionTask(Player player, int n, int n2, int n3, int n4, int n5, double d, String string) {
        this.player = player;
        this.deltaX = n;
        this.deltaY = n2;
        this.animationId = n3;
        this.delayedAnimationId = n4;
        this.duration = n5;
        this.experience = d;
        this.completionMessage = string;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.player.setActionLocked(false);
        Player player = this.player;
        player.packetSender.queueAgilityMovement(this.deltaX, this.deltaY, true, this.animationId, this.delayedAnimationId, this.duration, this.experience, this.player.getMovementQueue().isRunning(), this.completionMessage);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

