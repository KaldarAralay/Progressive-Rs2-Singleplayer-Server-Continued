/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.World;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.ApothecaryCadavaPotionReadyTask;
import com.rs2.model.quest.impl.RomeoAndJulietQuest;
import com.rs2.model.task.TickTask;

public final class ApothecaryCadavaPotionMixTask
extends TickTask {
    private TickTask a;

    public ApothecaryCadavaPotionMixTask(RomeoAndJulietQuest romeoAndJulietQuest, int n, Player player) {
        super(2);
        this.a = new ApothecaryCadavaPotionReadyTask(this, 2, player);
    }

    @Override
    public final void execute() {
        Npc npc = Npc.findByDefinitionId(638);
        npc.getUpdateState().setAnimation(363);
        npc.getUpdateState().setGraphic(189, 25);
        World.getTaskScheduler().schedule(this.a);
        this.stop();
    }
}

