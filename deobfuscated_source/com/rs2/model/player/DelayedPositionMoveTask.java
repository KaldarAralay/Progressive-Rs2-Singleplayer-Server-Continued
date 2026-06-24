/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class DelayedPositionMoveTask
extends CycleEvent {
    private /* synthetic */ Player player;
    private final /* synthetic */ Position destination;

    public DelayedPositionMoveTask(Player player, Position position) {
        this.player = player;
        this.destination = position;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.player.moveTo(this.destination);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

