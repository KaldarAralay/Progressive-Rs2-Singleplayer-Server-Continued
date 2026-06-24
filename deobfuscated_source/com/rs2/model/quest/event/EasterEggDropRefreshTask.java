/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.model.quest.event.EasterEggDropEventHook;
import com.rs2.model.task.TickTask;

public final class EasterEggDropRefreshTask
extends TickTask {
    private /* synthetic */ EasterEggDropEventHook a;

    public EasterEggDropRefreshTask(EasterEggDropEventHook easterEggDropEventHook, int n) {
        super(n);
        this.a = easterEggDropEventHook;
    }

    @Override
    public final void execute() {
        EasterEggDropEventHook.a(this.a);
    }
}

