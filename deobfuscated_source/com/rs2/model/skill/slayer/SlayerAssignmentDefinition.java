/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.slayer;

import com.rs2.model.player.Player;
import com.rs2.model.skill.slayer.SlayerAssignmentRequirement;

public final class SlayerAssignmentDefinition {
    String taskName;
    int minAmount;
    int maxAmount;
    int weight;
    private SlayerAssignmentRequirement[] requirements;

    public SlayerAssignmentDefinition(String string, int n, int n2, int n3) {
        this.taskName = string;
        this.minAmount = n;
        this.maxAmount = n2;
        this.weight = n3;
    }

    public SlayerAssignmentDefinition(String string, int n, int n2, int n3, SlayerAssignmentRequirement[] slayerAssignmentRequirementArray) {
        this.taskName = string;
        this.minAmount = n;
        this.maxAmount = n2;
        this.weight = n3;
        this.requirements = slayerAssignmentRequirementArray;
    }

    public final boolean isAvailableFor(Player player) {
        if (this.requirements == null) {
            return true;
        }
        SlayerAssignmentRequirement[] slayerAssignmentRequirementArray = this.requirements;
        int n = this.requirements.length;
        int n2 = 0;
        while (n2 < n) {
            SlayerAssignmentRequirement slayerAssignmentRequirement;
            SlayerAssignmentRequirement slayerAssignmentRequirement2 = slayerAssignmentRequirement = slayerAssignmentRequirementArray[n2];
            if (slayerAssignmentRequirement.requirementType == 0) {
                slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                if (player.getSkillManager().getBaseLevel(18) < slayerAssignmentRequirement2.requiredValue) {
                    return false;
                }
            } else {
                slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                if (slayerAssignmentRequirement2.requirementType == SlayerAssignmentRequirement.COMBAT_LEVEL) {
                    slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                    if (player.getCombatLevel() < slayerAssignmentRequirement2.requiredValue) {
                        return false;
                    }
                } else {
                    slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                    if (slayerAssignmentRequirement2.requirementType == SlayerAssignmentRequirement.QUEST_STATE) {
                        slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                        if (player.getQuestState(slayerAssignmentRequirement2.requiredValue) != 1) {
                            return false;
                        }
                    } else {
                        slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                        if (slayerAssignmentRequirement2.requirementType == SlayerAssignmentRequirement.DEFENCE_LEVEL) {
                            slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                            if (player.getSkillManager().getBaseLevel(1) < slayerAssignmentRequirement2.requiredValue) {
                                return false;
                            }
                        } else {
                            slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                            if (slayerAssignmentRequirement2.requirementType == SlayerAssignmentRequirement.AGILITY_LEVEL) {
                                slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                                if (player.getSkillManager().getBaseLevel(16) < slayerAssignmentRequirement2.requiredValue) {
                                    return false;
                                }
                            } else {
                                slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                                if (slayerAssignmentRequirement2.requirementType == SlayerAssignmentRequirement.FIREMAKING_LEVEL) {
                                    slayerAssignmentRequirement2 = slayerAssignmentRequirement;
                                    if (player.getSkillManager().getBaseLevel(11) < slayerAssignmentRequirement2.requiredValue) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ++n2;
        }
        return true;
    }
}

