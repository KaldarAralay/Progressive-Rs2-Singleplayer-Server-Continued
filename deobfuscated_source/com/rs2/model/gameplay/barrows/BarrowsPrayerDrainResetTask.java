/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.barrows;

import com.rs2.model.gameplay.barrows.BarrowsPrayerDrainTask;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class BarrowsPrayerDrainResetTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int interfaceId;

    public BarrowsPrayerDrainResetTask(BarrowsPrayerDrainTask barrowsPrayerDrainTask, int n, Player player, int n2) {
        super(3);
        this.player = player;
        this.interfaceId = n2;
    }

    @Override
    public final void execute() {
        Player player = this.player;
        player.packetSender.sendInterfaceModel(this.interfaceId, 200, -1);
        this.stop();
    }
}

