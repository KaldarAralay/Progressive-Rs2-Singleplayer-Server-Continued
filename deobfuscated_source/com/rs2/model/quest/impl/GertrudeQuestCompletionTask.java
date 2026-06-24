/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.GertrudeRewardFoodTask;
import com.rs2.model.task.TickTask;

public final class GertrudeQuestCompletionTask
extends TickTask {
    private /* synthetic */ GertrudeRewardFoodTask a;
    private final /* synthetic */ Player player;

    public GertrudeQuestCompletionTask(GertrudeRewardFoodTask gertrudeRewardFoodTask, int n, Player player) {
        super(4);
        this.a = gertrudeRewardFoodTask;
        this.player = player;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        GertrudeRewardFoodTask gertrudeRewardFoodTask = this.a;
        gertrudeRewardFoodTask.a.awardCompletionRewards(this.player);
        this.stop();
    }
}

