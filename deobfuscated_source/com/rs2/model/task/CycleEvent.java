/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.task;

import com.rs2.model.task.CycleEventContainer;

public abstract class CycleEvent {
    public abstract void execute(CycleEventContainer var1);

    public abstract void onStop();
}

