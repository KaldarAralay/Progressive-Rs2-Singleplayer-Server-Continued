/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.ScorpionCatcherQuest;
import com.rs2.model.task.TickTask;

public final class ScorpionCatcherCageHandoffDialogueTask
extends TickTask {
    private final /* synthetic */ Player player;

    public ScorpionCatcherCageHandoffDialogueTask(ScorpionCatcherQuest scorpionCatcherQuest, int n, Player player) {
        super(4);
        this.player = player;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        DialogueManager.continueDialogue(this.player, 389, 10, 0);
        this.stop();
    }
}

