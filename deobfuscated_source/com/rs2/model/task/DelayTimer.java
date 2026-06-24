/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.task;

import com.rs2.model.World;

public class DelayTimer {
    private int startTick;
    private int delayTicks;

    public DelayTimer(int n) {
        int n2 = n;
        DelayTimer delayTimer = this;
        this.delayTicks = n2;
        delayTimer = this;
        this.startTick = World.tickCount;
    }

    public final int getDelayTicks() {
        return this.delayTicks;
    }

    public final void setDelayTicks(int n) {
        this.delayTicks = n;
    }

    public final void reset() {
        this.startTick = World.tickCount;
    }

    public final boolean hasElapsed() {
        return World.tickCount - this.startTick >= this.delayTicks;
    }

    public final int getRemainingTicks() {
        return this.delayTicks - (World.tickCount - this.startTick);
    }
}

