/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;

public final class ApeAtollGuardCaptureTask
extends TickTask {
    private final /* synthetic */ Player player;

    public ApeAtollGuardCaptureTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player) {
        super(3);
        this.player = player;
    }

    @Override
    public final void execute() {
        this.player.ai = false;
        this.player.setActionLocked(false);
        this.player.moveTo(new Position(2772, 2794, 0));
        this.player.resetAnimation();
        Player player = this.player;
        player.packetSender.showWalkableInterface(-1);
        this.stop();
    }
}

