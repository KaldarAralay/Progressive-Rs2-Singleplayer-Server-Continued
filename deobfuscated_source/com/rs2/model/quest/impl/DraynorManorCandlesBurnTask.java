/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.VampireSlayerQuest;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class DraynorManorCandlesBurnTask
extends TickTask {
    private /* synthetic */ VampireSlayerQuest a;
    private final /* synthetic */ Player player;

    public DraynorManorCandlesBurnTask(VampireSlayerQuest vampireSlayerQuest, int n, Player player) {
        super(5);
        this.a = vampireSlayerQuest;
        this.player = player;
    }

    @Override
    public final void execute() {
        if (!this.player.isRegistered()) {
            this.stop();
            return;
        }
        if (this.a.b.containsExclusive(this.player.getPosition())) {
            Player player = this.player;
            player.packetSender.sendGameMessage("The candles burn your feet!");
            this.player.getUpdateState().setForcedText(this.a.a[GameUtil.randomInclusive(this.a.a.length - 1)]);
            return;
        }
        this.player.eq = -1;
        this.stop();
    }
}

