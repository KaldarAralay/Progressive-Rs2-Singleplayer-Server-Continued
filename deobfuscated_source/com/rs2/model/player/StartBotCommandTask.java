/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class StartBotCommandTask
extends TickTask {
    private /* synthetic */ Player a;
    private final /* synthetic */ Player b;

    public StartBotCommandTask(Player player, int n, Player player2) {
        super(2);
        this.a = player;
        this.b = player2;
    }

    @Override
    public final void execute() {
        this.a.completeQuestJournal();
        this.b.botMode = 4;
        this.a.resumeBotTaskState();
        this.stop();
    }
}

