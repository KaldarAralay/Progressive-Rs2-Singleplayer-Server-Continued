/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.Position;

public enum TeleotherDestination {
    CAMELOT(new Position(2757, 3479), "Camelot"),
    FALADOR(new Position(2964, 3378), "Falador"),
    LUMBRIDGE(new Position(3222, 3218), "Lumbridge");

    Position position;
    String displayName;

    /*
     * WARNING - void declaration
     */
    private TeleotherDestination(Position position, String string2) {
        this.position = position;
        this.displayName = string2;
    }
}

