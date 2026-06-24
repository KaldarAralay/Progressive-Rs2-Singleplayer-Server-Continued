/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.Position;
import com.rs2.model.skill.magic.TeleportManager;
import com.rs2.model.skill.runecrafting.RunecraftingHandler;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AbyssTeleportTask
extends CycleEvent {
    private int ticksRemaining = 3;
    private /* synthetic */ TeleportManager teleportManager;
    private final /* synthetic */ int destinationX;
    private final /* synthetic */ int destinationY;
    private final /* synthetic */ int destinationPlane;

    public AbyssTeleportTask(TeleportManager teleportManager, int n, int n2, int n3) {
        this.teleportManager = teleportManager;
        this.destinationX = n;
        this.destinationY = n2;
        this.destinationPlane = n3;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!TeleportManager.getPlayer(this.teleportManager).isDead()) {
            if (this.ticksRemaining == 3) {
                TeleportManager.getPlayer(this.teleportManager).moveTo(new Position(this.destinationX, this.destinationY, this.destinationPlane));
                if (TeleportManager.getPlayer(this.teleportManager).getInventoryManager().containsItem(5519)) {
                    RunecraftingHandler.recordScryingOrbTeleport(TeleportManager.getPlayer(this.teleportManager));
                }
                cycleEventContainer.stop();
            }
        } else {
            this.ticksRemaining = 0;
        }
        if (this.ticksRemaining <= 0) {
            cycleEventContainer.stop();
        }
        --this.ticksRemaining;
    }

    @Override
    public final void onStop() {
        TeleportManager.getPlayer(this.teleportManager).setActionLocked(false);
        TeleportManager.getPlayer(this.teleportManager).getAttributes().put("canTakeDamage", Boolean.TRUE);
    }
}

