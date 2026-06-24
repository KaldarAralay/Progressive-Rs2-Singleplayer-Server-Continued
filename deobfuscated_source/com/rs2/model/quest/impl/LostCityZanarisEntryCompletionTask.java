/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.LostCityQuest;
import com.rs2.model.task.TickTask;

public final class LostCityZanarisEntryCompletionTask
extends TickTask {
    private /* synthetic */ LostCityQuest a;
    private final /* synthetic */ Player player;

    public LostCityZanarisEntryCompletionTask(LostCityQuest lostCityQuest, int n, Player player) {
        super(4);
        this.a = lostCityQuest;
        this.player = player;
    }

    @Override
    public final void execute() {
        this.a.awardCompletionRewards(this.player);
        this.stop();
    }
}

