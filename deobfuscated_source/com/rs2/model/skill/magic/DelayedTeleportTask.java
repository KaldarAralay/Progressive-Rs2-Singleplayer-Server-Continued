/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.TeleportManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class DelayedTeleportTask
extends CycleEvent {
    private int ticksRemaining = 6;
    private /* synthetic */ TeleportManager teleportManager;
    private final /* synthetic */ int destinationX;
    private final /* synthetic */ int destinationY;
    private final /* synthetic */ int destinationPlane;
    private final /* synthetic */ String arrivalMessage;

    public DelayedTeleportTask(TeleportManager teleportManager, int n, int n2, int n3, String string) {
        this.teleportManager = teleportManager;
        this.destinationX = n;
        this.destinationY = n2;
        this.destinationPlane = n3;
        this.arrivalMessage = string;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        --this.ticksRemaining;
        if (!TeleportManager.getPlayer(this.teleportManager).isDead()) {
            if (this.ticksRemaining == 3) {
                TeleportManager.getPlayer(this.teleportManager).getUpdateState().setAnimation(715);
                TeleportManager.getPlayer(this.teleportManager).moveTo(new Position(this.destinationX, this.destinationY, this.destinationPlane));
                if (this.arrivalMessage != null) {
                    Player player = TeleportManager.getPlayer(this.teleportManager);
                    player.packetSender.sendGameMessage(this.arrivalMessage);
                }
            }
        } else {
            this.ticksRemaining = 0;
        }
        if (this.ticksRemaining <= 0) {
            cycleEventContainer.stop();
        }
    }

    @Override
    public final void onStop() {
        TeleportManager.getPlayer(this.teleportManager).setActionLocked(false);
        TeleportManager.getPlayer(this.teleportManager).getAttributes().put("canTakeDamage", Boolean.TRUE);
    }
}

