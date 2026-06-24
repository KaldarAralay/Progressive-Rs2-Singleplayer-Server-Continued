/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

import com.rs2.ServerSettings;
import com.rs2.model.combat.AmmunitionProfile;
import com.rs2.model.combat.WeaponInterfaceDefinition;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;

public final class WeaponProfile {
    public static final /* enum */ WeaponProfile FISTS;
    private static /* enum */ WeaponProfile SHORT_BOW;
    public static final /* enum */ WeaponProfile SPECIAL_BOW;
    public static final /* enum */ WeaponProfile CRYSTAL_BOW;
    private static /* enum */ WeaponProfile OGRE_BOW;
    private static /* enum */ WeaponProfile OGRE_COMP_BOW;
    public static final /* enum */ WeaponProfile LONG_BOW;
    private static /* enum */ WeaponProfile WAND;
    private static /* enum */ WeaponProfile STAFF;
    private static /* enum */ WeaponProfile SPECSTAFF;
    private static /* enum */ WeaponProfile THROWING_KNIFE;
    private static /* enum */ WeaponProfile THROWING_DART;
    private static /* enum */ WeaponProfile JAVELIN;
    private static /* enum */ WeaponProfile THROWING_AXE;
    public static final /* enum */ WeaponProfile HALBERD;
    private static /* enum */ WeaponProfile MACE;
    private static /* enum */ WeaponProfile SPEAR;
    private static /* enum */ WeaponProfile DRAGON_DAGGER;
    private static /* enum */ WeaponProfile DAGGER;
    private static /* enum */ WeaponProfile TWO_HANDED;
    private static /* enum */ WeaponProfile PICKAXE;
    private static /* enum */ WeaponProfile AXE;
    private static /* enum */ WeaponProfile BATTLE_AXE;
    public static final /* enum */ WeaponProfile LONGSWORD;
    public static final /* enum */ WeaponProfile SCIMITAR;
    private static /* enum */ WeaponProfile SWORD;
    private static /* enum */ WeaponProfile KARILS_CROSSBOW;
    private static /* enum */ WeaponProfile CROSSBOW;
    private static /* enum */ WeaponProfile DHAROKS;
    private static /* enum */ WeaponProfile TORAGS;
    private static /* enum */ WeaponProfile AHRIMS;
    private static /* enum */ WeaponProfile VERACS;
    private static /* enum */ WeaponProfile GRANITE_MAUL;
    private static /* enum */ WeaponProfile WHIP;
    private static /* enum */ WeaponProfile OBBY_RING;
    private static /* enum */ WeaponProfile OBBY_MAUL;
    private static /* enum */ WeaponProfile OBBY_SWORD_AND_KNIFE;
    private static /* enum */ WeaponProfile OBBY_MACE;
    private static /* enum */ WeaponProfile OBBY_STAFF;
    private static /* enum */ WeaponProfile WARHAMMER;
    private static /* enum */ WeaponProfile CLAWS;
    private static /* enum */ WeaponProfile GODSWORD;
    public static final /* enum */ WeaponProfile METAL_CROSSBOW;
    public static final /* enum */ WeaponProfile DARK_BOW;
    private static /* enum */ WeaponProfile SCYTHE;
    private WeaponInterfaceDefinition interfaceDefinition;
    private AmmunitionProfile ammunitionProfile;
    private int[] attackAnimations;
    private int[] movementAnimations;
    private int blockAnimationId;
    private int attackDelay;
    private final String name;
    private final int ordinal;
    private static final /* synthetic */ WeaponProfile[] Z;

    static {
        int[] nArray;
        int[] nArray2;
        int[] nArray3;
        FISTS = new WeaponProfile("FISTS", 0, WeaponInterfaceDefinition.UNARMED, 4, new int[]{422, 423, 422}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        SHORT_BOW = new WeaponProfile("SHORT_BOW", 1, WeaponInterfaceDefinition.BOW, AmmunitionProfile.ARROW, 4, new int[]{426, 426, 426}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        SPECIAL_BOW = new WeaponProfile("SPECIAL_BOW", 2, WeaponInterfaceDefinition.BOW, AmmunitionProfile.ARROW, 5, new int[]{426, 426, 426}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        CRYSTAL_BOW = new WeaponProfile("CRYSTAL_BOW", 3, WeaponInterfaceDefinition.BOW, AmmunitionProfile.ARROW, 5, new int[]{426, 426, 426}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        OGRE_BOW = new WeaponProfile("OGRE_BOW", 4, WeaponInterfaceDefinition.BOW, AmmunitionProfile.OGRE, 8, new int[]{426, 426, 426}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        OGRE_COMP_BOW = new WeaponProfile("OGRE_COMP_BOW", 5, WeaponInterfaceDefinition.BOW, AmmunitionProfile.BRUTAL, 5, new int[]{426, 426, 426}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        LONG_BOW = new WeaponProfile("LONG_BOW", 6, WeaponInterfaceDefinition.LONG_BOW, AmmunitionProfile.ARROW, 6, new int[]{426, 426, 426}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        WAND = new WeaponProfile("WAND", 7, WeaponInterfaceDefinition.STAFF, 5, new int[]{406, 407, 408}, new int[]{809, 1146, 1210}, 435);
        int[] nArray4 = new int[]{406, 406, 406};
        if (ServerSettings.cacheVersion < 254) {
            nArray3 = ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS;
        } else {
            int[] nArray5 = new int[3];
            nArray5[0] = 809;
            nArray5[1] = 1146;
            nArray3 = nArray5;
            nArray5[2] = ServerSettings.cacheVersion < 274 ? 824 : 1210;
        }
        STAFF = new WeaponProfile("STAFF", 8, WeaponInterfaceDefinition.STAFF, 5, nArray4, nArray3, 435);
        SPECSTAFF = new WeaponProfile("SPECSTAFF", 9, WeaponInterfaceDefinition.STAFF, 4, new int[]{406, 407, 408}, new int[]{809, 1146, 1210}, 435);
        THROWING_KNIFE = new WeaponProfile("THROWING_KNIFE", 10, WeaponInterfaceDefinition.THROWN, AmmunitionProfile.KNIFE, 3, new int[]{806, 806, 806}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        THROWING_DART = new WeaponProfile("THROWING_DART", 11, WeaponInterfaceDefinition.THROWN, AmmunitionProfile.DART, 3, new int[]{806, 806, 806}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        JAVELIN = new WeaponProfile("JAVELIN", 12, WeaponInterfaceDefinition.THROWN, AmmunitionProfile.JAVELIN, 6, new int[]{806, 806, 806}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        THROWING_AXE = new WeaponProfile("THROWING_AXE", 13, WeaponInterfaceDefinition.THROWN, AmmunitionProfile.THROWNAXE, 5, new int[]{806, 806, 806}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        HALBERD = new WeaponProfile("HALBERD", 14, WeaponInterfaceDefinition.HALBERD, 7, new int[]{2080, 2078, 2082}, new int[]{809, 1146, 1210}, 435);
        MACE = new WeaponProfile("MACE", 15, WeaponInterfaceDefinition.MACE, 4, new int[]{401, 401, 400, 401}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        SPEAR = new WeaponProfile("SPEAR", 16, WeaponInterfaceDefinition.SPEAR, 4, new int[]{ServerSettings.cacheVersion < 319 ? 417 : 2080, ServerSettings.cacheVersion < 319 ? 414 : 2081, ServerSettings.cacheVersion < 319 ? 414 : 2082, ServerSettings.cacheVersion < 319 ? 417 : 2080}, new int[]{809, 1146, ServerSettings.cacheVersion < 274 ? 824 : 1210}, 435);
        DRAGON_DAGGER = new WeaponProfile("DRAGON_DAGGER", 17, WeaponInterfaceDefinition.DAGGER, null, 4, new int[]{402, 402, 451, 400}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 403);
        DAGGER = new WeaponProfile("DAGGER", 18, WeaponInterfaceDefinition.DAGGER, 4, new int[]{386, 386, 390, 386}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 404);
        int[] nArray6 = new int[]{406, 407, 406, 406};
        if (ServerSettings.cacheVersion < 254) {
            nArray2 = ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS;
        } else {
            int[] nArray7 = new int[3];
            nArray7[0] = ServerSettings.cacheVersion < 319 ? 809 : 2065;
            nArray7[1] = ServerSettings.cacheVersion < 319 ? 1146 : 2064;
            nArray2 = nArray7;
            nArray7[2] = ServerSettings.cacheVersion <= 319 ? 824 : 2563;
        }
        TWO_HANDED = new WeaponProfile("TWO_HANDED", 19, WeaponInterfaceDefinition.TWO_HANDED_SWORD, 7, nArray6, nArray2, 410);
        PICKAXE = new WeaponProfile("PICKAXE", 20, WeaponInterfaceDefinition.PICKAXE, 5, new int[]{395, 400, 401, 395}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 410);
        AXE = new WeaponProfile("AXE", 21, WeaponInterfaceDefinition.AXE, 5, new int[]{395, 395, 401, 395}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 404);
        BATTLE_AXE = new WeaponProfile("BATTLE_AXE", 22, WeaponInterfaceDefinition.AXE, 6, new int[]{395, 395, 401, 395}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 404);
        int[] nArray8 = new int[]{451, 451, 412, 451};
        if (ServerSettings.cacheVersion < 254) {
            nArray = ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS;
        } else {
            int[] nArray9 = new int[3];
            nArray9[0] = 809;
            nArray9[1] = 1146;
            nArray = nArray9;
            nArray9[2] = ServerSettings.cacheVersion < 274 ? 824 : 1210;
        }
        LONGSWORD = new WeaponProfile("LONGSWORD", 23, WeaponInterfaceDefinition.SLASH_SWORD, 5, nArray8, nArray, 404);
        SCIMITAR = new WeaponProfile("SCIMITAR", 24, WeaponInterfaceDefinition.SLASH_SWORD, 4, new int[]{390, 390, 412, 390}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 404);
        SWORD = new WeaponProfile("SWORD", 25, WeaponInterfaceDefinition.DAGGER, 4, new int[]{412, 412, 451, 412}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 404);
        KARILS_CROSSBOW = new WeaponProfile("KARILS_CROSSBOW", 26, WeaponInterfaceDefinition.BOW, AmmunitionProfile.KARILS_BOLT, 4, new int[]{2075, 2075, 2075}, new int[]{2074, 2076, 2077}, 1666);
        CROSSBOW = new WeaponProfile("CROSSBOW", 27, WeaponInterfaceDefinition.BOW, AmmunitionProfile.BOLTS, 6, new int[]{427, 427, 427}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 404);
        DHAROKS = new WeaponProfile("DHAROKS", 28, WeaponInterfaceDefinition.AXE, 7, new int[]{2067, 2067, 2066, 2067}, new int[]{2065, 1663, 1664}, 1666);
        TORAGS = new WeaponProfile("TORAGS", 29, WeaponInterfaceDefinition.HAMMER, 5, new int[]{2068, 2068, 2068}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        AHRIMS = new WeaponProfile("AHRIMS", 30, WeaponInterfaceDefinition.STAFF, 4, new int[]{406, 407, 408}, new int[]{809, 1146, 1210}, 435);
        VERACS = new WeaponProfile("VERACS", 31, WeaponInterfaceDefinition.MACE, 5, new int[]{2062, 2062, 2062, 2062}, new int[]{1832, 1830, 1831}, 2063);
        GRANITE_MAUL = new WeaponProfile("GRANITE_MAUL", 32, WeaponInterfaceDefinition.HAMMER, 7, new int[]{1665, 1665, 1665}, new int[]{1662, 1663, 1664}, 1666);
        WHIP = new WeaponProfile("WHIP", 33, WeaponInterfaceDefinition.WHIP, null, 4, new int[]{1658, 1658, 1658}, new int[]{1832, 1660, 1661}, 1659);
        OBBY_RING = new WeaponProfile("OBBY_RING", 34, WeaponInterfaceDefinition.THROWN, AmmunitionProfile.OBBY_RING, 4, new int[]{3353, 3353, 3353}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 2063);
        OBBY_MAUL = new WeaponProfile("OBBY_MAUL", 35, WeaponInterfaceDefinition.HAMMER, 7, new int[]{2661, 2661, 2661}, new int[]{2065, 2064, 1664}, 1666);
        OBBY_SWORD_AND_KNIFE = new WeaponProfile("OBBY_SWORD_AND_KNIFE", 36, WeaponInterfaceDefinition.SLASH_SWORD, 4, new int[]{412, 401, 451, 401}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        OBBY_MACE = new WeaponProfile("OBBY_MACE", 37, WeaponInterfaceDefinition.MACE, 5, new int[]{401, 401, 400, 401}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        OBBY_STAFF = new WeaponProfile("OBBY_STAFF", 38, WeaponInterfaceDefinition.STAFF, 6, new int[]{406, 407, 408}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 410);
        WARHAMMER = new WeaponProfile("WARHAMMER", 39, WeaponInterfaceDefinition.HAMMER, 6, new int[]{401, 401, 400}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        CLAWS = new WeaponProfile("CLAWS", 40, WeaponInterfaceDefinition.CLAWS, 4, new int[]{393, 393, 1067, 393}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        GODSWORD = new WeaponProfile("GODSWORD", 41, WeaponInterfaceDefinition.GODSWORD, 6, new int[]{7041, 7041, 7048, 7049}, new int[]{7047, 7046, 7039}, 7050);
        METAL_CROSSBOW = new WeaponProfile("METAL_CROSSBOW", 42, WeaponInterfaceDefinition.BOW, AmmunitionProfile.BOLTS, 6, new int[]{4230, 4230, 4230}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        DARK_BOW = new WeaponProfile("DARK_BOW", 43, WeaponInterfaceDefinition.LONG_BOW, AmmunitionProfile.ARROW, 9, new int[]{426, 426, 426}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 424);
        SCYTHE = new WeaponProfile("SCYTHE", 44, WeaponInterfaceDefinition.SCYTHE, 7, new int[]{440, 440, 440, 440}, ServerSettings.DEFAULT_WEAPON_MOVEMENT_ANIMATIONS, 435);
        Z = new WeaponProfile[]{FISTS, SHORT_BOW, SPECIAL_BOW, CRYSTAL_BOW, OGRE_BOW, OGRE_COMP_BOW, LONG_BOW, WAND, STAFF, SPECSTAFF, THROWING_KNIFE, THROWING_DART, JAVELIN, THROWING_AXE, HALBERD, MACE, SPEAR, DRAGON_DAGGER, DAGGER, TWO_HANDED, PICKAXE, AXE, BATTLE_AXE, LONGSWORD, SCIMITAR, SWORD, KARILS_CROSSBOW, CROSSBOW, DHAROKS, TORAGS, AHRIMS, VERACS, GRANITE_MAUL, WHIP, OBBY_RING, OBBY_MAUL, OBBY_SWORD_AND_KNIFE, OBBY_MACE, OBBY_STAFF, WARHAMMER, CLAWS, GODSWORD, METAL_CROSSBOW, DARK_BOW, SCYTHE};
    }

    private WeaponProfile(String string, int n, WeaponInterfaceDefinition weaponInterfaceDefinition, AmmunitionProfile ammunitionProfile, int n2, int[] nArray, int[] nArray2, int n3) {
        this.name = string;
        this.ordinal = n;
        this.interfaceDefinition = weaponInterfaceDefinition;
        this.ammunitionProfile = ammunitionProfile;
        this.attackDelay = n2;
        this.attackAnimations = nArray;
        this.movementAnimations = nArray2;
        if (this.movementAnimations.length != 3) {
            throw new IllegalArgumentException("There must be 3 elements in movementAnimations array. [stand, walk, run]");
        }
        this.blockAnimationId = n3;
        if (weaponInterfaceDefinition.getAttackStyles().length != nArray.length) {
            throw new IllegalArgumentException("There are not the same amount of animations as attack styles for weapon: " + this.name());
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private WeaponProfile(String string, int n, WeaponInterfaceDefinition weaponInterfaceDefinition, int n2, int[] nArray, int[] nArray2, int n3) {
        this(string, n, weaponInterfaceDefinition, null, n2, nArray, nArray2, n3);
    }

    public final String name() {
        return this.name;
    }

    public final int ordinal() {
        return this.ordinal;
    }

    public final String toString() {
        return this.name;
    }

    public final WeaponInterfaceDefinition getInterfaceDefinition() {
        return this.interfaceDefinition;
    }

    public final int[] getAttackAnimations() {
        return this.attackAnimations;
    }

    public final int[] getMovementAnimations() {
        return this.movementAnimations;
    }

    public final int getAttackDelay() {
        return this.attackDelay;
    }

    public final int getBlockAnimationId() {
        return this.blockAnimationId;
    }

    public final AmmunitionProfile getAmmunitionProfile() {
        return this.ammunitionProfile;
    }

    public static WeaponProfile forItem(ItemStack itemStack) {
        if (itemStack == null) {
            return FISTS;
        }
        String string = ItemDefinition.forId(itemStack.getId()).getName().toLowerCase();
        if (string.contains("dragon dagger") || string.contains("drag dagger")) {
            return DRAGON_DAGGER;
        }
        if (string.contains("dagger") || string.contains("wolfbane")) {
            return DAGGER;
        }
        if (string.contains("longsword") || string.contains("darklight") || string.contains("silverlight") || string.contains("excalibur") || string.contains("machete")) {
            return LONGSWORD;
        }
        if (string.endsWith("2h sword")) {
            return TWO_HANDED;
        }
        if (string.endsWith("godsword") || string.endsWith("saradomin sword")) {
            return GODSWORD;
        }
        if (string.endsWith("granite maul")) {
            return GRANITE_MAUL;
        }
        if (string.endsWith("sword")) {
            return SWORD;
        }
        if (string.endsWith("scimitar") || string.endsWith("machete")) {
            return SCIMITAR;
        }
        if (string.endsWith("mace") || string.endsWith("club")) {
            return MACE;
        }
        if (string.startsWith("dharoks")) {
            return DHAROKS;
        }
        if (string.endsWith("thrownaxe")) {
            return THROWING_AXE;
        }
        if (string.endsWith("axe") && !string.contains("pickaxe") && !string.contains("battleaxe")) {
            return AXE;
        }
        if (string.endsWith("battleaxe")) {
            return BATTLE_AXE;
        }
        if (string.endsWith("warhammer") || string.equalsIgnoreCase("flowers") || string.equalsIgnoreCase("chicken")) {
            return WARHAMMER;
        }
        if (string.contains("spear") || string.contains("mjolnir")) {
            return SPEAR;
        }
        if (string.contains("claw")) {
            return CLAWS;
        }
        if (string.endsWith("halberd")) {
            return HALBERD;
        }
        if (string.equalsIgnoreCase("toktz-xil-ak") || string.equalsIgnoreCase("toktz-xil-ek")) {
            return OBBY_SWORD_AND_KNIFE;
        }
        if (string.equalsIgnoreCase("tzhaar-ket-em")) {
            return OBBY_MACE;
        }
        if (string.equalsIgnoreCase("toktz-mej-tal")) {
            return OBBY_STAFF;
        }
        if (string.equalsIgnoreCase("tzhaar-ket-om")) {
            return OBBY_MAUL;
        }
        if (string.equalsIgnoreCase("toktz-xil-ul")) {
            return OBBY_RING;
        }
        if (string.equalsIgnoreCase("abyssal whip")) {
            return WHIP;
        }
        if (string.startsWith("torags")) {
            return TORAGS;
        }
        if (string.startsWith("veracs")) {
            return VERACS;
        }
        if (string.contains("wand")) {
            return WAND;
        }
        if (itemStack.getId() == 2415 || itemStack.getId() == 2416 || itemStack.getId() == 2417 || itemStack.getId() == 4170 || itemStack.getId() == 4675) {
            return SPECSTAFF;
        }
        if (string.contains("staff") && (itemStack.getId() <= 1410 || itemStack.getId() >= 3053 && itemStack.getId() <= 3056 || itemStack.getId() >= 6562 && itemStack.getId() <= 6727)) {
            return STAFF;
        }
        if (string.contains("crozier")) {
            return STAFF;
        }
        if (string.contains("ahrims")) {
            return AHRIMS;
        }
        if (string.contains("longbow")) {
            return LONG_BOW;
        }
        if (string.contains("dark bow")) {
            return DARK_BOW;
        }
        if (string.contains("pickaxe")) {
            return PICKAXE;
        }
        if (string.contains("shortbow")) {
            return SHORT_BOW;
        }
        if (string.contains("comp bow") || string.contains("seercull")) {
            return SPECIAL_BOW;
        }
        if (string.contains("crystal bow")) {
            return CRYSTAL_BOW;
        }
        if (string.contains("ogre")) {
            if (string.startsWith("comp")) {
                return OGRE_COMP_BOW;
            }
            return OGRE_BOW;
        }
        if (string.startsWith("karils")) {
            return KARILS_CROSSBOW;
        }
        if (string.contains("crossbow")) {
            return CROSSBOW;
        }
        if (string.contains("c'bow")) {
            return METAL_CROSSBOW;
        }
        if (string.contains("knife")) {
            return THROWING_KNIFE;
        }
        if (string.contains("dart")) {
            return THROWING_DART;
        }
        if (string.contains("javelin")) {
            return JAVELIN;
        }
        if (string.contains("scythe")) {
            return SCYTHE;
        }
        return FISTS;
    }

    public static WeaponProfile[] values() {
        WeaponProfile[] weaponProfileArray = new WeaponProfile[45];
        System.arraycopy(Z, 0, weaponProfileArray, 0, 45);
        return weaponProfileArray;
    }

    public static WeaponProfile valueOf(String string) {
        if (string == null) {
            throw new NullPointerException("Name is null");
        }
        WeaponProfile[] weaponProfileArray = WeaponProfile.values();
        int n = weaponProfileArray.length;
        int n2 = 0;
        while (n2 < n) {
            WeaponProfile weaponProfile = weaponProfileArray[n2];
            if (weaponProfile.name().equals(string)) {
                return weaponProfile;
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum constant com.rs2.model.combat.WeaponProfile." + string);
    }
}

