/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.slayer;

import com.rs2.model.skill.slayer.SlayerManager;
import java.util.HashMap;
import java.util.Map;

public enum SlayerMonsterDefinition {
    CRAWLING_HAND("crawling hand", 5, null, "none"),
    CAVE_BUG("cave bug", 7, null, "none"),
    CAVE_CRAWLER("cave crawler", 10, null, "none"),
    BANSHEE("banshee", 15, new int[]{4166}, "equipment"),
    CAVE_SLIME("cave slime", 17, null, "none"),
    ROCKSLUG("rockslug", 20, new int[]{4161}, "use"),
    LIZARD("lizard", 22, new int[]{6696}, "use"),
    COCKATRICE("cockatrice", 25, new int[]{4156}, "equipment"),
    PYREFIEND("pyrefiend", 30, null, "none"),
    MOGRE("mogre", 32, null, "none"),
    HARPIE_BUG_SWARM("harpie bug swarm", 33, new int[]{7053}, "equipment"),
    WALL_BEAST("wall beast", 35, new int[]{4551}, "equipment"),
    KILLERWATT("killerwatt", 37, new int[]{7159}, "equipment"),
    BASILISK("basilisk", 40, new int[]{4156}, "equipment"),
    FEVER_SPIDER("fever spider", 42, new int[]{6720}, "equipment"),
    INFERNAL_MAGE("infernal mage", 45, null, "none"),
    BLOODVELD("bloodveld", 50, null, "none"),
    JELLY("jelly", 52, null, "none"),
    TUROTH("turoth", 55, new int[]{4158}, "equipment"),
    ZYGOMITE("zygomite", 57, SlayerManager.FUNGICIDE_SPRAY_ITEM_IDS, "use"),
    ABERRANT_SPECTER("aberrant specter", 60, new int[]{4168}, "equipment"),
    DUST_DEVIL("dust devil", 65, new int[]{4164}, "equipment"),
    KURASK("kurask", 70, new int[]{4158}, "equipment"),
    SKELETAL_WYVERN("skeletal wyvern", 72, new int[]{2890}, "equipment"),
    GARGOYLE("gargoyle", 75, new int[]{4162}, "use"),
    NECHRYAEL("nechryael", 80, null, "none"),
    ABYSSAL_DEMON("abyssal demon", 85, null, "none"),
    SPIRITUAL_RANGER("spiritual ranger", 63, null, "none"),
    SPIRITUAL_WARRIOR("spiritual warrior", 68, null, "none"),
    SPIRITUAL_MAGE("spiritual mage", 83, null, "none"),
    DARK_BEAST("dark beast", 90, null, "none");

    private String monsterName;
    private int requiredSlayerLevel;
    private int[] requiredItemIds;
    private String requirementMode;
    private static Map definitionsByName;

    static {
        definitionsByName = new HashMap();
        SlayerMonsterDefinition[] slayerMonsterDefinitionArray = SlayerMonsterDefinition.values();
        int n = slayerMonsterDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            SlayerMonsterDefinition slayerMonsterDefinition = slayerMonsterDefinitionArray[n2];
            definitionsByName.put(slayerMonsterDefinition.monsterName, slayerMonsterDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SlayerMonsterDefinition(String string2, int n2, int[] nArray, String string3) {
        this.monsterName = string2;
        this.requiredSlayerLevel = n2;
        this.requiredItemIds = nArray;
        this.requirementMode = string3;
    }

    public static SlayerMonsterDefinition forName(String string) {
        if (string.contains("lizard")) {
            string = "lizard";
        }
        if (string.equalsIgnoreCase("")) {
            return null;
        }
        return (SlayerMonsterDefinition)((Object)definitionsByName.get(string));
    }

    public final String getMonsterName() {
        return this.monsterName;
    }

    public final int getRequiredSlayerLevel() {
        return this.requiredSlayerLevel;
    }

    public final int[] getRequiredItemIds() {
        return this.requiredItemIds;
    }

    public final String getRequirementMode() {
        return this.requirementMode;
    }
}

