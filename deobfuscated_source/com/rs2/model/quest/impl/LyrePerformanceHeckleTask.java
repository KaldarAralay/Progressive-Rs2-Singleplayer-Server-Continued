/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Entity;
import com.rs2.model.EntityUpdateState;
import com.rs2.model.World;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.LyrePerformanceStartTask;
import com.rs2.model.task.TickTask;

public final class LyrePerformanceHeckleTask
extends TickTask {
    private /* synthetic */ LyrePerformanceStartTask startTask;
    private final /* synthetic */ int hecklerNpcIndex;
    private final /* synthetic */ int heckleLineIndex;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int performanceLineIndex;

    public LyrePerformanceHeckleTask(LyrePerformanceStartTask lyrePerformanceStartTask, int n, int n2, int n3, Player player, int n4) {
        super(4);
        this.startTask = lyrePerformanceStartTask;
        this.hecklerNpcIndex = n2;
        this.heckleLineIndex = n3;
        this.player = player;
        this.performanceLineIndex = n4;
    }

    @Override
    public final void execute() {
        Object object = this.startTask;
        object = Npc.findByDefinitionId(((LyrePerformanceStartTask)object).quest.lyreAudienceNpcIds[this.hecklerNpcIndex]);
        if (object != null) {
            EntityUpdateState entityUpdateState = ((Entity)object).getUpdateState();
            object = this.startTask;
            entityUpdateState.setForcedText(((LyrePerformanceStartTask)object).quest.lyreAudienceHeckleLines[this.heckleLineIndex]);
        }
        object = this.startTask;
        this.player.getUpdateState().setForcedText(((LyrePerformanceStartTask)object).quest.lyrePerformanceLines[this.performanceLineIndex][3]);
        World.getTaskScheduler().schedule(this.startTask.finishDialogueTask);
        this.stop();
    }
}

