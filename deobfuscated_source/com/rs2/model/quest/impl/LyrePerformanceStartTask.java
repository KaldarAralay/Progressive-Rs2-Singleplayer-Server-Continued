/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.World;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.FremennikTrialsQuest;
import com.rs2.model.quest.impl.LyrePerformanceFinishDialogueTask;
import com.rs2.model.quest.impl.LyrePerformanceHeckleTask;
import com.rs2.model.quest.impl.LyrePerformanceSecondLineTask;
import com.rs2.model.quest.impl.LyrePerformanceThirdLineTask;
import com.rs2.model.task.TickTask;

public final class LyrePerformanceStartTask
extends TickTask {
    private TickTask secondLineTask;
    final TickTask thirdLineTask;
    final TickTask heckleTask;
    final TickTask finishDialogueTask;
    final /* synthetic */ FremennikTrialsQuest quest;
    private final /* synthetic */ int performanceLineIndex;
    private final /* synthetic */ Player player;

    public LyrePerformanceStartTask(FremennikTrialsQuest fremennikTrialsQuest, int n, int n2, Player player, int n3, int n4) {
        super(2);
        this.quest = fremennikTrialsQuest;
        this.performanceLineIndex = n2;
        this.player = player;
        this.secondLineTask = new LyrePerformanceSecondLineTask(this, 4, player, n2);
        this.thirdLineTask = new LyrePerformanceThirdLineTask(this, 4, player, n2);
        this.heckleTask = new LyrePerformanceHeckleTask(this, 4, n3, n4, player, n2);
        this.finishDialogueTask = new LyrePerformanceFinishDialogueTask(this, 4, player);
    }

    @Override
    public final void execute() {
        String string = this.quest.lyrePerformanceLines[this.performanceLineIndex][0].replaceAll("PLAYERNAME", this.player.getUsername());
        this.player.getUpdateState().setForcedText(string);
        World.getTaskScheduler().schedule(this.secondLineTask);
        this.stop();
    }
}

