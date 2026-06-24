/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.bot.BotPlayer;
import com.rs2.model.task.TickTask;

public final class BotLoginBatchTask
extends TickTask {
    private final /* synthetic */ int finalBatchIndex;

    public BotLoginBatchTask(int n, int n2) {
        super(n);
        this.finalBatchIndex = n2;
    }

    @Override
    public final void execute() {
        int n = Server.getBotLoginBatchIndex();
        int n2 = 1 + Server.botLoginBatchSize * n;
        n = Server.botLoginBatchSize + Server.botLoginBatchSize * n;
        if (Server.getConfiguredBotCount() < n) {
            n = Server.getConfiguredBotCount();
        }
        while (n2 <= n) {
            if (n2 > 0 && n2 < ServerSettings.botLoginIdLimit) {
                BotPlayer.createBotFromPool(n2, "zxcvbn", Server.selectNextBotType());
                if (n2 == Server.getConfiguredBotCount()) {
                    this.stop();
                    return;
                }
            }
            ++n2;
        }
        if (this.finalBatchIndex == Server.getBotLoginBatchIndex()) {
            this.stop();
            return;
        }
        Server.setBotLoginBatchIndex(Server.getBotLoginBatchIndex() + 1);
    }
}

