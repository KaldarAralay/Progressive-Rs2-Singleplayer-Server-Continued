/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.ServerSettings;
import com.rs2.bot.BotPlayer;
import com.rs2.model.quest.event.FrozenBotRelogScanTask;
import com.rs2.model.task.TickTask;
import java.util.ArrayList;

public final class FrozenBotReloginTask
extends TickTask {
    private final /* synthetic */ ArrayList a;

    public FrozenBotReloginTask(FrozenBotRelogScanTask frozenBotRelogScanTask, int n, ArrayList arrayList) {
        super(30);
        this.a = arrayList;
    }

    @Override
    public final void execute() {
        for (Object reloginNameObject : this.a) {
            String string = (String)reloginNameObject;
            int n = ServerSettings.skillingBotsEnabled ? 0 : 4;
            BotPlayer.createNamedBot(string, "zxcvbn", n);
        }
        this.stop();
    }
}

