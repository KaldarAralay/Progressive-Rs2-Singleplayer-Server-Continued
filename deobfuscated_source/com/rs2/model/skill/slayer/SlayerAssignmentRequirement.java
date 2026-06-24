/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.slayer;

public final class SlayerAssignmentRequirement {
    public static int SLAYER_LEVEL = 0;
    public static int COMBAT_LEVEL = 1;
    public static int QUEST_STATE = 2;
    public static int DEFENCE_LEVEL = 3;
    public static int AGILITY_LEVEL = 4;
    public static int FIREMAKING_LEVEL = 5;
    int requirementType;
    int requiredValue;

    public SlayerAssignmentRequirement(int n, int n2) {
        this.requirementType = n;
        this.requiredValue = n2;
    }
}

