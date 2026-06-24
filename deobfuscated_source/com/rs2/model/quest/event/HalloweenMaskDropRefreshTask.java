/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.model.quest.event.HalloweenMaskDropEventHook;
import com.rs2.model.task.TickTask;

public final class HalloweenMaskDropRefreshTask
extends TickTask {
    private /* synthetic */ HalloweenMaskDropEventHook a;

    public HalloweenMaskDropRefreshTask(HalloweenMaskDropEventHook halloweenMaskDropEventHook, int n) {
        super(n);
        this.a = halloweenMaskDropEventHook;
    }

    @Override
    public final void execute() {
        HalloweenMaskDropEventHook.a(this.a);
    }
}

