/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;

public final class ShipyardGateLockpickTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ boolean b;

    public ShipyardGateLockpickTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player, boolean bl) {
        super(3);
        this.player = player;
        this.b = bl;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        if (this.b) {
            Player player = this.player;
            player.packetSender.openSingleDoor(4799, 2771, 2795, this.player.getPosition().getPlane());
            player = this.player;
            player.packetSender.queueRelativeMovementStep(0, 1, true);
            player = this.player;
            player.packetSender.sendGameMessage("You manage to pick the lock.");
        } else {
            Player player = this.player;
            player.packetSender.sendGameMessage("You fail to pick the lock.");
        }
        this.stop();
    }
}

