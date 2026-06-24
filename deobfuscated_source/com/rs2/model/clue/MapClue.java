/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import com.rs2.model.Position;
import java.util.HashMap;
import java.util.Map;

public enum MapClue {
    a(2713, 6994, new Position(3166, 3360), false, 1),
    b(2716, 9275, new Position(3109, 3153), false, 1),
    c(2719, 9108, new Position(2612, 3482), false, 1),
    d(3516, 7271, new Position(3043, 3399), false, 1),
    e(3518, 7045, new Position(3290, 3373), false, 1),
    f(7236, 17537, new Position(2970, 3414), false, 1),
    g(2827, 9720, new Position(2565, 3248), true, 2),
    h(2829, 9196, new Position(2658, 3488), true, 2),
    i(7286, 17907, new Position(2579, 3597), false, 2),
    j(7288, 17888, new Position(2454, 3230), false, 2),
    k(7290, 17774, new Position(3434, 3265), false, 2),
    l(3596, 9632, new Position(2650, 3230), false, 2),
    m(3602, 17687, new Position(2535, 3865), false, 2),
    n(3598, 9839, new Position(2924, 3209), false, 2),
    o(3599, 4305, new Position(2906, 3294), false, 2),
    p(7292, 18055, new Position(2666, 3562), false, 2),
    q(3601, 7113, new Position(3092, 3226), false, 2),
    r(2722, 7221, new Position(3309, 3503), true, 3),
    s(3520, 9454, new Position(2459, 3179), true, 3),
    t(3522, 9507, new Position(3026, 3628), true, 3),
    u(3524, 9043, new Position(2616, 3077), false, 3),
    v(7239, 17620, new Position(3021, 3912), false, 3),
    w(7241, 17634, new Position(2722, 3338), false, 3);

    private int clueItemId;
    private int interfaceId;
    private Position position;
    private boolean objectSearchClue;
    private int level;
    private static Map cluesByPosition;
    private static Map cluesByItemId;

    static {
        cluesByPosition = new HashMap();
        cluesByItemId = new HashMap();
        MapClue[] mapClueArray = MapClue.values();
        int n = mapClueArray.length;
        int n2 = 0;
        while (n2 < n) {
            MapClue mapClue = mapClueArray[n2];
            cluesByPosition.put(mapClue.position, mapClue);
            cluesByItemId.put(mapClue.clueItemId, mapClue);
            ++n2;
        }
    }

    public static MapClue forPosition(Position position) {
        int n = 0;
        while (n < MapClue.values().length) {
            MapClue mapClue = MapClue.values()[n];
            if (mapClue.position.equals(position)) {
                return MapClue.values()[n];
            }
            ++n;
        }
        return null;
    }

    public static MapClue forClueItemId(int n) {
        return (MapClue)((Object)cluesByItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private MapClue(int n2, int n3, Position position, boolean bl, int n4) {
        this.clueItemId = n2;
        this.interfaceId = n3;
        this.position = position;
        this.objectSearchClue = bl;
        this.level = n4;
    }

    public final int getClueItemId() {
        return this.clueItemId;
    }

    public final int getInterfaceId() {
        return this.interfaceId;
    }

    public final boolean isObjectSearchClue() {
        return this.objectSearchClue;
    }

    public final int getLevel() {
        return this.level;
    }
}

