/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.task;

public abstract class TickTask {
    private int remainingTicks;
    private int intervalTicks;
    private boolean active = true;
    private boolean executeImmediately = true;

    public TickTask(int n) {
        this(n, false);
    }

    public TickTask(int n, boolean bl) {
        this.remainingTicks = n;
        this.intervalTicks = n;
        this.executeImmediately = bl;
    }

    public final int getIntervalTicks() {
        return this.intervalTicks;
    }

    public final int getRemainingTicks() {
        return this.remainingTicks;
    }

    public final void setIntervalTicks(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Tick amount must be positive.");
        }
        this.intervalTicks = n;
    }

    public final void setRemainingTicks(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Tick amount must be positive.");
        }
        this.remainingTicks = n;
    }

    public final boolean isActive() {
        return this.active;
    }

    public void stop() {
        this.active = false;
    }

    public abstract void execute();

    public final void tick() {
        TickTask tickTask = this;
        if (tickTask.executeImmediately) {
            this.execute();
            this.executeImmediately = false;
            return;
        }
        if (this.remainingTicks-- <= 1) {
            tickTask = this;
            if (tickTask.active) {
                this.execute();
            }
            this.remainingTicks = this.intervalTicks;
        }
    }
}

