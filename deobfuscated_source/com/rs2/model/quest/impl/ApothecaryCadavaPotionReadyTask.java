/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.ApothecaryCadavaPotionMixTask;
import com.rs2.model.task.TickTask;

public final class ApothecaryCadavaPotionReadyTask
extends TickTask {
    private final /* synthetic */ Player player;

    public ApothecaryCadavaPotionReadyTask(ApothecaryCadavaPotionMixTask apothecaryCadavaPotionMixTask, int n, Player player) {
        super(2);
        this.player = player;
    }

    @Override
    public final void execute() {
        this.player.getDialogueManager().showNpcOneLineDialogue("Phew! Here is what you need.", 591);
        this.stop();
    }
}

