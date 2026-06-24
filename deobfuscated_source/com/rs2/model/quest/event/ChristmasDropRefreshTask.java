/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.model.quest.event.ChristmasDropEventHook;
import com.rs2.model.task.TickTask;

public final class ChristmasDropRefreshTask
extends TickTask {
    private /* synthetic */ ChristmasDropEventHook a;

    public ChristmasDropRefreshTask(ChristmasDropEventHook christmasDropEventHook, int n) {
        super(n);
        this.a = christmasDropEventHook;
    }

    @Override
    public final void execute() {
        ChristmasDropEventHook.a(this.a);
    }
}

