/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.TeleportManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class ScriptedTeleportTask
extends CycleEvent {
    private int ticksRemaining;
    private /* synthetic */ TeleportManager teleportManager;
    private final /* synthetic */ int departureAnimationId;
    private final /* synthetic */ int departureGraphicId;
    private final /* synthetic */ int arrivalAnimationId;
    private final /* synthetic */ int destinationX;
    private final /* synthetic */ int destinationY;
    private final /* synthetic */ int destinationPlane;
    private final /* synthetic */ String arrivalMessage;

    public ScriptedTeleportTask(TeleportManager teleportManager, int n, int n2, int n3, int n4, int n5, int n6, int n7, String string) {
        this.teleportManager = teleportManager;
        this.departureAnimationId = n2;
        this.departureGraphicId = n3;
        this.arrivalAnimationId = n4;
        this.destinationX = n5;
        this.destinationY = n6;
        this.destinationPlane = n7;
        this.arrivalMessage = string;
        this.ticksRemaining = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        --this.ticksRemaining;
        if (!TeleportManager.getPlayer(this.teleportManager).isDead()) {
            if (this.ticksRemaining == 4) {
                TeleportManager.getPlayer(this.teleportManager).getUpdateState().setAnimation(this.departureAnimationId);
                TeleportManager.getPlayer(this.teleportManager).getUpdateState().setGraphicHeight100(this.departureGraphicId);
            }
            if (this.ticksRemaining == 2) {
                TeleportManager.getPlayer(this.teleportManager).setActionLocked(false);
                TeleportManager.getPlayer(this.teleportManager).getUpdateState().setAnimation(this.arrivalAnimationId);
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

