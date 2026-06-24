/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.GertrudesCatQuest;
import com.rs2.model.task.TickTask;

public final class FluffsInteractionHintTask
extends TickTask {
    private final /* synthetic */ int a;
    private final /* synthetic */ Player player;

    public FluffsInteractionHintTask(GertrudesCatQuest gertrudesCatQuest, int n, int n2, Player player) {
        super(2);
        this.a = n2;
        this.player = player;
    }

    @Override
    public final void execute() {
        if (this.a == 3) {
            this.player.getDialogueManager().showOneLineStatement("Maybe the cat is thirsty?");
        }
        if (this.a == 4) {
            this.player.getDialogueManager().showOneLineStatement("Maybe the cat is hungry?");
        }
        if (this.a == 5) {
            this.player.getDialogueManager().showTwoLineStatement("The cat seems afraid to leave.", "In the distance you can hear kittens mewing...");
        }
        this.player.setActionLocked(false);
        this.stop();
    }
}

