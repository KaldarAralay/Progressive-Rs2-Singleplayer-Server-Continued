/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.PiratesTreasureQuest;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class PiratesTreasureDigCompletionEvent
extends CycleEvent {
    private /* synthetic */ PiratesTreasureQuest quest;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;

    public PiratesTreasureDigCompletionEvent(PiratesTreasureQuest piratesTreasureQuest, Player player, int n) {
        this.quest = piratesTreasureQuest;
        this.player = player;
        this.actionSequence = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        Player player = this.player;
        player.packetSender.sendGameMessage("and find the treasure.");
        this.quest.awardCompletionRewards(this.player);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

