/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.GrandTreeQuest;
import com.rs2.model.task.TickTask;

public final class GrandTreeGuardPrisonEscortTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int b;

    public GrandTreeGuardPrisonEscortTask(GrandTreeQuest grandTreeQuest, int n, Player player, int n2) {
        super(3);
        this.player = player;
        this.b = n2;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        this.player.moveTo(new Position(2464, 3496, 3));
        this.player.setQuestState(this.b, 9);
        DialogueManager.continueDialogue(this.player, 673, 1, 0);
        this.stop();
    }
}

