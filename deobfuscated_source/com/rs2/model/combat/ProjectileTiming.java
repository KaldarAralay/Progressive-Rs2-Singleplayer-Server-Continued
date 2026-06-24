/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

public final class ProjectileTiming {
    public static ProjectileTiming a = new ProjectileTiming(44, 3, 43, 31, 15);
    public static ProjectileTiming b = new ProjectileTiming(33, 3, 45, 37, 5);
    public static ProjectileTiming c;
    public static ProjectileTiming d;
    public static ProjectileTiming e;
    public static ProjectileTiming f;
    private int startDelay;
    private int speed;
    private int startHeight;
    private int endHeight;
    private int slope;

    static {
        ProjectileTiming projectileTiming = b.copy();
        projectileTiming.startDelay = 40;
        projectileTiming.speed = 2;
        c = projectileTiming;
        d = new ProjectileTiming(50, 6, 45, 30, 15);
        e = new ProjectileTiming(50, 25, 100, 26, 15);
        f = new ProjectileTiming(50, 50, 60, 26, 15);
        new ProjectileTiming(50, 6, 45, 0, 15);
    }

    private ProjectileTiming(int n, int n2, int n3, int n4, int n5) {
        this.startDelay = n;
        this.speed = n2;
        this.startHeight = n3;
        this.endHeight = n4;
        this.slope = n5;
    }

    public final int getStartDelay() {
        return this.startDelay;
    }

    public final int getSpeed() {
        return this.speed;
    }

    public final int getStartHeight() {
        return this.startHeight;
    }

    public final int getEndHeight() {
        return this.endHeight;
    }

    public final int getSlope() {
        return this.slope;
    }

    public final ProjectileTiming setStartDelay(int n) {
        this.startDelay = n;
        return this;
    }

    public final ProjectileTiming setSpeed(int n) {
        this.speed = n;
        return this;
    }

    public final ProjectileTiming copy() {
        return new ProjectileTiming(this.startDelay, this.speed, this.startHeight, this.endHeight, this.slope);
    }

    public final /* synthetic */ Object clone() {
        return this.copy();
    }
}

