/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.bot.BotPlayer;
import com.rs2.model.Position;
import com.rs2.model.task.TickTask;

public final class DropPartyBotHideTask
extends TickTask {
    private final /* synthetic */ BotPlayer botToHide;

    public DropPartyBotHideTask(BotPlayer botPlayer, int n, BotPlayer botPlayer2) {
        super(2);
        this.botToHide = botPlayer2;
    }

    @Override
    public final void execute() {
        this.botToHide.moveTo(new Position(9999, 9999, 0));
        this.stop();
    }
}

