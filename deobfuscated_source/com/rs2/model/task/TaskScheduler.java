/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.task;

import com.rs2.model.task.TickTask;
import java.util.LinkedList;
import java.util.List;

public final class TaskScheduler {
    private List a = new LinkedList();

    public final List getTasks() {
        return this.a;
    }

    public final void schedule(TickTask tickTask) {
        this.a.add(tickTask);
    }
}

