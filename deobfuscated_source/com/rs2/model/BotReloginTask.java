/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.ServerSettings;
import com.rs2.bot.BotPlayer;
import com.rs2.model.task.TickTask;

public final class BotReloginTask
extends TickTask {
    private final /* synthetic */ String username;

    public BotReloginTask(int n, String string) {
        super(30);
        this.username = string;
    }

    @Override
    public final void execute() {
        int n = ServerSettings.skillingBotsEnabled ? 0 : 4;
        BotPlayer.createNamedBot(this.username, "zxcvbn", n);
        this.stop();
    }
}

