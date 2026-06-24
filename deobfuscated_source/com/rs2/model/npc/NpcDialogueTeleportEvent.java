/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc;

import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class NpcDialogueTeleportEvent
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int destinationX;
    private final /* synthetic */ int destinationY;
    private final /* synthetic */ int destinationPlane;

    public NpcDialogueTeleportEvent(Npc npc, Player player, int n, int n2, int n3) {
        this.player = player;
        this.destinationX = n;
        this.destinationY = n2;
        this.destinationPlane = n3;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.player.getTeleportManager().startDelayedTeleport(this.destinationX, this.destinationY, this.destinationPlane);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

