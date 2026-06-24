/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;

public final class ZooknockAmuletEnchantSpellTask
extends TickTask {
    private final /* synthetic */ Player player;

    public ZooknockAmuletEnchantSpellTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player) {
        super(3);
        this.player = player;
    }

    @Override
    public final void execute() {
        this.player.setActionLocked(false);
        DialogueManager.continueDialogue(this.player, 1425, 110, 0);
        this.stop();
    }
}

