/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.trade;

import com.rs2.model.GameplayHelper;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class BotTradeItemShortageResetTask
extends TickTask {
    private final /* synthetic */ Player player;

    public BotTradeItemShortageResetTask(int n, Player player) {
        super(10);
        this.player = player;
    }

    @Override
    public final void execute() {
        GameplayHelper.startNextBotTask(this.player);
        this.stop();
    }
}

