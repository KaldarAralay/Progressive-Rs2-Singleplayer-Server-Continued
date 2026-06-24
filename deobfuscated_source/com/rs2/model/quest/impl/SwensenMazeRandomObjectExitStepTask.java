/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.FremennikTrialsQuest;
import com.rs2.model.task.TickTask;

public final class SwensenMazeRandomObjectExitStepTask
extends TickTask {
    private final /* synthetic */ int a;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int c;

    public SwensenMazeRandomObjectExitStepTask(FremennikTrialsQuest fremennikTrialsQuest, int n, int n2, Player player, int n3) {
        super(1);
        this.a = n2;
        this.player = player;
        this.c = n3;
    }

    @Override
    public final void execute() {
        Player player;
        if (this.a != 0) {
            player = this.player;
            player.packetSender.queueRelativeMovementStep(0, this.a < 0 ? 1 : -1, true);
        }
        if (this.c != 0) {
            player = this.player;
            player.packetSender.queueRelativeMovementStep(this.c < 0 ? 1 : -1, 0, true);
        }
        this.stop();
    }
}

