/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.World;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.LyrePerformanceStartTask;
import com.rs2.model.task.TickTask;

public final class LyrePerformanceThirdLineTask
extends TickTask {
    private /* synthetic */ LyrePerformanceStartTask startTask;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int performanceLineIndex;

    public LyrePerformanceThirdLineTask(LyrePerformanceStartTask lyrePerformanceStartTask, int n, Player player, int n2) {
        super(4);
        this.startTask = lyrePerformanceStartTask;
        this.player = player;
        this.performanceLineIndex = n2;
    }

    @Override
    public final void execute() {
        LyrePerformanceStartTask lyrePerformanceStartTask = this.startTask;
        this.player.getUpdateState().setForcedText(lyrePerformanceStartTask.quest.lyrePerformanceLines[this.performanceLineIndex][2]);
        World.getTaskScheduler().schedule(this.startTask.heckleTask);
        this.stop();
    }
}

