/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.Position;
import java.util.HashMap;
import java.util.Map;

public enum CompostBin {
    FALADOR(0, new Position(3056, 3312, 0)),
    CATHERBY(1, new Position(2804, 3464, 0)),
    MORYTANIA(2, new Position(3610, 3522, 0)),
    ARDOUGNE(3, new Position(2661, 3375, 0));

    private int index;
    private Position position;
    private static Map binByIndex;

    static {
        binByIndex = new HashMap();
        CompostBin[] compostBinArray = CompostBin.values();
        int n = compostBinArray.length;
        int n2 = 0;
        while (n2 < n) {
            CompostBin compostBin = compostBinArray[n2];
            binByIndex.put(compostBin.index, compostBin);
            ++n2;
        }
    }

    /*
     * WARNING - void declaration
     */
    private CompostBin(int n2, Position position) {
        this.index = n2;
        this.position = position;
    }

    public static CompostBin forPosition(Position position) {
        CompostBin[] compostBinArray = CompostBin.values();
        int n = compostBinArray.length;
        int n2 = 0;
        while (n2 < n) {
            CompostBin compostBin = compostBinArray[n2];
            if (compostBin.position.equals(position)) {
                return compostBin;
            }
            ++n2;
        }
        return null;
    }

    public final int getIndex() {
        return this.index;
    }
}

