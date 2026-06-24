/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.launcher;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

public final class TransparentColorFilter
extends RGBImageFilter {
    private int transparentRgb;

    public TransparentColorFilter(Color color) {
        this.transparentRgb = color.getRGB() | 0xFF000000;
    }

    @Override
    public final int filterRGB(int n, int n2, int n3) {
        if ((n3 | 0xFF000000) == this.transparentRgb) {
            return 0xFFFFFF & n3;
        }
        return n3;
    }
}

