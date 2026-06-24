/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;

public final class DaeroBlindfoldHangarReturnTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int b;

    public DaeroBlindfoldHangarReturnTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player, int n2) {
        super(5);
        this.player = player;
        this.b = n2;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        if (this.b >= 8) {
            this.player.moveTo(new Position(2649, 4516, 0));
        } else {
            this.player.moveTo(new Position(2585, 4516, 0));
        }
        Player player = this.player;
        player.packetSender.closeInterfaces();
        this.stop();
    }
}

