/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.GrandTreeQuest;
import com.rs2.model.task.TickTask;

public final class KingNarnodePrisonReleaseTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ Npc kingNarnodeNpc;

    public KingNarnodePrisonReleaseTask(GrandTreeQuest grandTreeQuest, int n, Player player, Npc npc) {
        super(3);
        this.player = player;
        this.kingNarnodeNpc = npc;
    }

    @Override
    public final void execute() {
        DialogueManager.continueDialogue(this.player, 670, 100, 0);
        this.player.setInteractionTarget(this.kingNarnodeNpc);
        this.kingNarnodeNpc.setScriptedMovementEnabled(true);
        this.stop();
    }
}

