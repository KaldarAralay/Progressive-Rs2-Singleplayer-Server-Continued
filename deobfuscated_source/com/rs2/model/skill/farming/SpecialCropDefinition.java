/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum SpecialCropDefinition {
    BELLADONNA(5281, 2398, 1, 63, 320, 0.15, 91.0, 512.0, 4, 8, -1, 0.0, 5, 8),
    CACTUS(5280, 6016, 1, 55, 560, 0.15, 66.5, 25.0, 8, 18, 31, 374.0, 11, 17),
    MUSHROOM(5282, 6004, 1, 53, 240, 0.15, 61.5, 57.7, 4, 15, -1, 0.0, 12, 17);

    private int seedId;
    private int produceItemId;
    private int seedAmount;
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
    private static Map definitionsBySeedId;

    static {
        definitionsBySeedId = new HashMap();
        SpecialCropDefinition[] specialCropDefinitionArray = SpecialCropDefinition.values();
        int n = specialCropDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            SpecialCropDefinition specialCropDefinition = specialCropDefinitionArray[n2];
            definitionsBySeedId.put(specialCropDefinition.seedId, specialCropDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SpecialCropDefinition(int n2, int n3, int n4, int n5, int n6, double d, double d2, double d3, int n7, int n8, int n9, double d4, int n10, int n11) {
        this.seedId = n2;
        this.produceItemId = n3;
        this.seedAmount = 1;
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

    public static SpecialCropDefinition forSeedId(int n) {
        return (SpecialCropDefinition)((Object)definitionsBySeedId.get(n));
    }

    public final int getSeedId() {
        return this.seedId;
    }

    public final int getProduceItemId() {
        return this.produceItemId;
    }

    public final int getSeedAmount() {
        return this.seedAmount;
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
        SpecialCropDefinition specialCropDefinition = this;
        SpecialCropDefinition specialCropDefinition2 = specialCropDefinition;
        specialCropDefinition2 = this;
        return specialCropDefinition.configEndStage - specialCropDefinition2.configStartStage;
    }

    public final int getGrowthCycleTicks() {
        SpecialCropDefinition specialCropDefinition = this;
        return specialCropDefinition.totalGrowthTicks / this.getGrowthStageCount();
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

