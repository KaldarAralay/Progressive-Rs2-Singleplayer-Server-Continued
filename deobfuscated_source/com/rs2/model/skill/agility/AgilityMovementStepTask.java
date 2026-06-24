/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.agility;

import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AgilityMovementStepTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int forcedMovementEndXOffset;
    private final /* synthetic */ int forcedMovementEndYOffset;
    private final /* synthetic */ int forcedMovementStartDelay;
    private final /* synthetic */ int forcedMovementEndDelay;
    private final /* synthetic */ int forcedMovementDirection;

    public AgilityMovementStepTask(Player player, int n, int n2, int n3, int n4, int n5) {
        this.player = player;
        this.forcedMovementEndXOffset = n;
        this.forcedMovementEndYOffset = n2;
        this.forcedMovementStartDelay = n3;
        this.forcedMovementEndDelay = n4;
        this.forcedMovementDirection = n5;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.player.getUpdateState().setForcedMovement(this.player, this.forcedMovementEndXOffset, this.forcedMovementEndYOffset, this.forcedMovementStartDelay, this.forcedMovementEndDelay, this.forcedMovementDirection);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

