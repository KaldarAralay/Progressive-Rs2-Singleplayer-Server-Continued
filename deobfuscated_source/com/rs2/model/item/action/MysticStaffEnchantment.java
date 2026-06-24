/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.action;

import java.util.HashMap;

public enum MysticStaffEnchantment {
    AIR(1734, 1397, 1405),
    WATER(1735, 1395, 1403),
    EARTH(1736, 1399, 1407),
    FIRE(1737, 1393, 1401),
    LAVA(1738, 3053, 3054),
    MUD(15348, 6562, 6563);

    private int buttonId;
    private int battlestaffItemId;
    private int mysticStaffItemId;
    private static HashMap byButtonId;

    static {
        byButtonId = new HashMap();
        MysticStaffEnchantment[] mysticStaffEnchantmentArray = MysticStaffEnchantment.values();
        int n = mysticStaffEnchantmentArray.length;
        int n2 = 0;
        while (n2 < n) {
            MysticStaffEnchantment mysticStaffEnchantment;
            MysticStaffEnchantment mysticStaffEnchantment2 = mysticStaffEnchantment = mysticStaffEnchantmentArray[n2];
            byButtonId.put(mysticStaffEnchantment2.buttonId, mysticStaffEnchantment);
            ++n2;
        }
    }

    public static MysticStaffEnchantment forButtonId(int n) {
        return (MysticStaffEnchantment)((Object)byButtonId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private MysticStaffEnchantment(int n2, int n3, int n4) {
        this.buttonId = n2;
        this.battlestaffItemId = n3;
        this.mysticStaffItemId = n4;
    }

    public final int getBattlestaffItemId() {
        return this.battlestaffItemId;
    }

    public final int getMysticStaffItemId() {
        return this.mysticStaffItemId;
    }
}

