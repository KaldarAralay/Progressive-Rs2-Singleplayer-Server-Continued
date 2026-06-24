/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;

public final class DaeroBlindfoldHangarTravelTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int b;

    public DaeroBlindfoldHangarTravelTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player, int n2) {
        super(5);
        this.player = player;
        this.b = n2;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        this.player.setQuestState(this.b, 6);
        this.player.moveTo(new Position(2585, 4516, 0));
        Player player = this.player;
        player.packetSender.closeInterfaces();
        this.stop();
    }
}

