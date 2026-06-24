/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.abyss;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AbyssDelayedMoveEvent
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int destinationX;
    private final /* synthetic */ int destinationY;

    public AbyssDelayedMoveEvent(Player player, int n, int n2) {
        this.player = player;
        this.destinationX = n;
        this.destinationY = n2;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.player.moveTo(new Position(this.destinationX, this.destinationY, this.player.getPosition().getPlane()));
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

