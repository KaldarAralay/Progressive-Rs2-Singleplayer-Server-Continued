/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet.handler;

import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.net.packet.handler.ItemActionPacketHandler;

public final class TinderboxOnGroundItemTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;

    public TinderboxOnGroundItemTask(ItemActionPacketHandler itemActionPacketHandler, Player player, int n) {
        this.player = player;
        this.actionSequence = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        if (this.player.getPosition().getX() == this.player.getInteractionTargetX() && this.player.getPosition().getY() == this.player.getInteractionTargetY()) {
            this.player.getFiremakingHandler().startFiremaking(this.player.getInteractionTargetId(), 0, true, this.player.getInteractionTargetX(), this.player.getInteractionTargetY(), this.player.getPosition().getPlane());
            cycleEventContainer.stop();
        }
    }

    @Override
    public final void onStop() {
    }
}

