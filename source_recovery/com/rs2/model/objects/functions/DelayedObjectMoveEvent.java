/*
 * Source recovery overlay: make remapped event accessible to recovered callers.
 */
package com.rs2.model.objects.functions;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class DelayedObjectMoveEvent
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ Position destination;

    public DelayedObjectMoveEvent(Player player, Position position) {
        this.player = player;
        this.destination = position;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (this.player.getPosition() != this.destination) {
            this.player.moveTo(this.destination);
        }
        this.player.getUpdateState().setAnimation(65535);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}
