/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum SpecialTreeDefinition {
    SPIRIT_TREE(5375, -1, 1, 83, 3520, 0.15, 199.5, 0.0, 8, 20, 44, 19301.8, 12, 23),
    CALQUAT(5503, 5980, 1, 72, 1280, 0.15, 129.5, 48.5, 4, 18, 34, 12096.0, 14, 20);

    private int saplingId;
    private int produceItemId;
    private int requiredLevel;
    private int totalGrowthTicks;
    private double diseaseChance;
    private double plantingExperience;
    private double harvestExperience;
    private int configStartStage;
    private int configEndStage;
    private int healthCheckConfigStage;
    private double healthCheckExperience;
    private int diseasedConfigOffset;
    private int deadConfigOffset;
    private static Map definitionsBySaplingId;

    static {
        definitionsBySaplingId = new HashMap();
        SpecialTreeDefinition[] specialTreeDefinitionArray = SpecialTreeDefinition.values();
        int n = specialTreeDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            SpecialTreeDefinition specialTreeDefinition = specialTreeDefinitionArray[n2];
            definitionsBySaplingId.put(specialTreeDefinition.saplingId, specialTreeDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SpecialTreeDefinition(int n2, int n3, int n4, int n5, int n6, double d, double d2, double d3, int n7, int n8, int n9, double d4, int n10, int n11) {
        this.saplingId = n2;
        this.produceItemId = n3;
        this.requiredLevel = n5;
        this.totalGrowthTicks = n6;
        this.diseaseChance = 0.15;
        this.plantingExperience = d2;
        this.harvestExperience = d3;
        this.configStartStage = n7;
        this.configEndStage = n8;
        this.healthCheckConfigStage = n9;
        this.healthCheckExperience = d4;
        this.diseasedConfigOffset = n10;
        this.deadConfigOffset = n11;
    }

    public static SpecialTreeDefinition forSaplingId(int n) {
        return (SpecialTreeDefinition)((Object)definitionsBySaplingId.get(n));
    }

    public final int getProduceItemId() {
        return this.produceItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final int getTotalGrowthTicks() {
        return this.totalGrowthTicks;
    }

    public final double getDiseaseChance() {
        return this.diseaseChance;
    }

    public final double getPlantingExperience() {
        return this.plantingExperience;
    }

    public final double getHarvestExperience() {
        return this.harvestExperience;
    }

    public final int getConfigStartStage() {
        return this.configStartStage;
    }

    public final int getGrowthStageCount() {
        SpecialTreeDefinition specialTreeDefinition = this;
        SpecialTreeDefinition specialTreeDefinition2 = specialTreeDefinition;
        specialTreeDefinition2 = this;
        return specialTreeDefinition.configEndStage - specialTreeDefinition2.configStartStage;
    }

    public final int getGrowthCycleTicks() {
        SpecialTreeDefinition specialTreeDefinition = this;
        return specialTreeDefinition.totalGrowthTicks / this.getGrowthStageCount();
    }

    public final int getHealthCheckConfigStage() {
        return this.healthCheckConfigStage;
    }

    public final double getHealthCheckExperience() {
        return this.healthCheckExperience;
    }

    public final int getDiseasedConfigOffset() {
        return this.diseasedConfigOffset;
    }

    public final int getDeadConfigOffset() {
        return this.deadConfigOffset;
    }
}

