/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.c;

import com.rs2.model.c.aN;

public abstract class aM {
    private static /* enum */ aM a = new aN();
    private final String name;
    private final int ordinal;
    private static final /* synthetic */ aM[] b;

    static {
        b = new aM[]{a};
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    protected aM(String string, int n) {
        this.name = string;
        this.ordinal = n;
    }

    public final String name() {
        return this.name;
    }

    public final int ordinal() {
        return this.ordinal;
    }

    public String toString() {
        return this.name;
    }

    public static aM[] values() {
        aM[] aMArray = new aM[1];
        System.arraycopy(b, 0, aMArray, 0, 1);
        return aMArray;
    }

    public static aM valueOf(String string) {
        if (string == null) {
            throw new NullPointerException("Name is null");
        }
        aM[] aMArray = aM.values();
        int n = aMArray.length;
        int n2 = 0;
        while (n2 < n) {
            aM aM2 = aMArray[n2];
            if (aM2.name().equals(string)) {
                return aM2;
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum constant com.rs2.model.c.aM." + string);
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    /* synthetic */ aM(String string, int n, byte by) {
        this(string, n);
    }
}

