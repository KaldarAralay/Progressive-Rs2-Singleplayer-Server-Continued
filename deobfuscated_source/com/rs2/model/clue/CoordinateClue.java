/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import com.rs2.model.Position;
import com.rs2.model.clue.CoordinateClueHandler;
import java.util.HashMap;
import java.util.Map;

public enum CoordinateClue {
    a(2801, 0, 5, 1, 13, "south", "east", 2),
    b(2803, 0, 13, 13, 58, "south", "east", 2),
    c(2805, 0, 18, 9, 28, "south", "east", 2),
    d(2807, 0, 20, 23, 15, "south", "east", 2),
    e(2809, 0, 30, 24, 16, "north", "east", 2),
    f(2811, 0, 31, 17, 43, "south", "east", 2),
    g(2813, 2, 48, 22, 30, "north", "east", 2),
    h(2815, 2, 50, 6, 20, "north", "east", 2),
    i(2817, 3, 35, 13, 35, "south", "east", 2),
    j(2819, 4, 0, 12, 46, "south", "east", 2),
    k(2821, 4, 13, 12, 45, "north", "east", 2),
    l(2823, 5, 20, 4, 28, "south", "east", 2),
    m(2825, 5, 43, 23, 5, "north", "east", 2),
    n(3582, 6, 31, 1, 46, "north", "west", 2),
    o(3584, 7, 5, 30, 56, "north", "east", 2),
    p(3586, 7, 33, 15, 0, "north", "east", 2),
    q(3588, 8, 33, 1, 39, "north", "west", 2),
    r(3590, 9, 48, 17, 39, "north", "east", 2),
    s(3592, 11, 3, 31, 20, "north", "east", 2),
    t(3594, 11, 5, 0, 45, "north", "west", 2),
    u(7305, 11, 41, 14, 58, "north", "east", 2),
    v(7307, 14, 54, 9, 13, "north", "east", 2),
    w(2723, 3, 45, 22, 45, "south", "east", 3),
    x(2725, 6, 0, 21, 48, "south", "east", 3),
    y(2727, 6, 11, 15, 7, "south", "east", 3),
    z(2729, 8, 3, 31, 16, "north", "east", 3),
    A(2731, 13, 46, 21, 1, "north", "east", 3),
    B(2733, 16, 7, 22, 45, "north", "east", 3),
    C(2735, 16, 35, 27, 1, "north", "east", 3),
    D(2737, 16, 43, 19, 13, "north", "east", 3),
    E(2739, 16, 43, 26, 56, "north", "east", 3),
    F(2741, 17, 50, 8, 30, "north", "east", 3),
    G(2743, 18, 22, 16, 33, "north", "east", 3),
    H(2745, 19, 43, 25, 7, "north", "east", 3),
    I(2747, 20, 5, 21, 52, "north", "east", 3),
    J(3526, 21, 24, 17, 54, "north", "east", 3),
    K(3528, 22, 35, 19, 18, "north", "east", 3),
    L(3530, 22, 45, 26, 33, "north", "east", 3),
    M(3532, 24, 24, 26, 24, "north", "east", 3),
    N(3534, 24, 58, 18, 43, "north", "east", 3),
    O(3536, 25, 3, 17, 5, "north", "east", 3),
    P(3538, 25, 3, 23, 24, "north", "east", 3);

    private int clueItemId;
    private int latitudeDegrees;
    private int latitudeMinutes;
    private int longitudeDegrees;
    private int longitudeMinutes;
    private String latitudeDirection;
    private String longitudeDirection;
    private int level;
    private Position position;
    private static Map cluesByItemId;
    private static Map cluesByPosition;

    static {
        cluesByItemId = new HashMap();
        cluesByPosition = new HashMap();
        CoordinateClue[] coordinateClueArray = CoordinateClue.values();
        int n = coordinateClueArray.length;
        int n2 = 0;
        while (n2 < n) {
            CoordinateClue coordinateClue = coordinateClueArray[n2];
            coordinateClueArray[n2].position = CoordinateClueHandler.resolvePosition(coordinateClue.latitudeDegrees, coordinateClue.latitudeMinutes, coordinateClue.longitudeDegrees, coordinateClue.longitudeMinutes, coordinateClue.latitudeDirection, coordinateClue.longitudeDirection);
            cluesByItemId.put(coordinateClue.clueItemId, coordinateClue);
            cluesByPosition.put(coordinateClue.position, coordinateClue);
            ++n2;
        }
    }

    public static CoordinateClue forPosition(Position position) {
        int n = 0;
        while (n < CoordinateClue.values().length) {
            CoordinateClue coordinateClue = CoordinateClue.values()[n];
            if (coordinateClue.position.equals(position)) {
                return CoordinateClue.values()[n];
            }
            ++n;
        }
        return null;
    }

    public static CoordinateClue forClueItemId(int n) {
        return (CoordinateClue)((Object)cluesByItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private CoordinateClue(int n2, int n3, int n4, int n5, int n6, String string2, String string3, int n7) {
        this.clueItemId = n2;
        this.latitudeDegrees = n3;
        this.latitudeMinutes = n4;
        this.longitudeDegrees = n5;
        this.longitudeMinutes = n6;
        this.latitudeDirection = string2;
        this.longitudeDirection = string3;
        this.level = n7;
    }

    public final int getClueItemId() {
        return this.clueItemId;
    }

    public final int getLatitudeDegrees() {
        return this.latitudeDegrees;
    }

    public final int getLatitudeMinutes() {
        return this.latitudeMinutes;
    }

    public final int getLongitudeDegrees() {
        return this.longitudeDegrees;
    }

    public final int getLongitudeMinutes() {
        return this.longitudeMinutes;
    }

    public final String getLatitudeDirection() {
        return this.latitudeDirection;
    }

    public final String getLongitudeDirection() {
        return this.longitudeDirection;
    }

    public final int getLevel() {
        return this.level;
    }
}

