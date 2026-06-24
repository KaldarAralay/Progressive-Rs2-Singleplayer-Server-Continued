/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects;

import java.util.logging.Logger;

public class ObjectDefinition {
    public static ObjectDefinition[] definitionsById;
    private int objectId;
    public int b;
    public String name;
    public String description;
    public int width;
    public int length;
    public boolean solid;
    public boolean h;
    public boolean interactive;
    public boolean blocksProjectiles;
    public boolean k;
    public boolean projectileCollisionIgnored;

    static {
        Logger.getLogger(ObjectDefinition.class.getName());
    }

    public static void a() {
    }

    public static ObjectDefinition forId(int n) {
        if (n < definitionsById.length) {
            if (definitionsById[n] == null) {
                int n2 = n;
                ObjectDefinition.definitionsById[n] = new ObjectDefinition(n2, "Object: #" + n2, "Its an object!", 1, 1, false, false, false, true, 2);
            }
            return definitionsById[n];
        }
        return null;
    }

    private ObjectDefinition(int n, String string, String string2, int n2, int n3, boolean bl, boolean bl2, boolean bl3, boolean bl4, int n4) {
        this.objectId = n;
        this.name = string;
        if (string == null) {
            this.name = "";
        }
        this.width = 1;
        this.length = 1;
        this.solid = false;
        this.interactive = false;
        this.blocksProjectiles = true;
        this.projectileCollisionIgnored = this.isProjectileCollisionIgnored();
    }

    public final int getObjectId() {
        return this.objectId;
    }

    public final String getName() {
        return this.name;
    }

    public final int getWidthForOrientation(int n) {
        if (n == 1 || n == 3) {
            return this.length;
        }
        return this.width;
    }

    public final int getLengthForOrientation(int n) {
        if (n == 1 || n == 3) {
            return this.width;
        }
        return this.length;
    }

    public final int getMaxDimension() {
        if (this.length > this.width) {
            return this.length;
        }
        return this.width;
    }

    public final boolean isProjectileCollisionIgnored() {
        if (this.objectId == 1116 || this.objectId == 1117) {
            return false;
        }
        if (this.objectId == 6771 || this.objectId == 6772 || this.objectId == 6773 || this.objectId == 6821 || this.objectId == 6822 || this.objectId == 6823) {
            return false;
        }
        int[] nArray = new int[]{2440, 2441, 2442, 2443, 2637, 9563, 9565, 14462, 14464, 14465, 14466, 14467, 14468, 14470, 14502, 11754, 3007, 980, 997, 4262, 14437, 14438, 4437, 4439, 3487, 3457};
        int n = 0;
        while (n < 26) {
            int n2 = nArray[n];
            if (n2 == this.objectId) {
                return true;
            }
            ++n;
        }
        if (this.name != null) {
            String objectName = this.name.toLowerCase();
            String[] stringArray = new String[]{"fungus", "mushroom", "sarcophagus", "counter", "plant", "altar", "pew", "log", "stump", "stool", "sign", "cart", "chest", "rock", "bush", "hedge", "chair", "table", "crate", "barrel", "box", "skeleton", "corpse", "vent", "stone", "rockslide"};
            int n3 = 0;
            while (n3 < 26) {
                String ignoredNameFragment = stringArray[n3];
                if (objectName.contains(ignoredNameFragment)) {
                    return true;
                }
                ++n3;
            }
        }
        return false;
    }
}

