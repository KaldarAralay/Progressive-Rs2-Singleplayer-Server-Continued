/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;

public final class ApeAtollGuardCaptureDialogueTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int b;

    public ApeAtollGuardCaptureDialogueTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player, int n2) {
        super(10);
        this.player = player;
        this.b = n2;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        this.player.moveTo(new Position(2772, 2794, 0));
        this.player.resetAnimation();
        this.player.ai = false;
        this.player.pendingGameMode = 0;
        Player player = this.player;
        player.packetSender.showWalkableInterface(-1);
        if (this.b == 11) {
            DialogueManager.continueDialogue(this.player, 1413, 100, 0);
        }
        this.stop();
    }
}

