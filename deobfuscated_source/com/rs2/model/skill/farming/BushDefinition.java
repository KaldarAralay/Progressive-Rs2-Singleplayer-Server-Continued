/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum BushDefinition {
    REDBERRY(5101, 1951, 1, 10, new int[]{5478, 4}, 100, 0.2, 11.5, 4.5, 5, 14, 9, 58, 64.0),
    CADAVABERRY(5102, 753, 1, 22, new int[]{5968, 3}, 120, 0.2, 18.0, 7.0, 15, 25, 20, 59, 102.5),
    DWELLBERRY(5103, 2126, 1, 36, new int[]{5406, 3}, 140, 0.2, 31.5, 12.0, 26, 37, 32, 60, 177.5),
    JANGERBERRY(5104, 247, 1, 48, new int[]{5982, 6}, 160, 0.2, 50.5, 19.0, 38, 50, 45, 61, 284.5),
    WHITEBERRY(5105, 239, 1, 59, new int[]{6004, 8}, 160, 0.2, 78.0, 29.0, 51, 63, 58, 62, 437.5),
    POISON_IVY(5106, 6018, 1, 70, null, 160, 0.2, 120.0, 45.0, 197, 209, 204, 63, 674.0);

    private int seedId;
    private int produceItemId;
    private int seedAmount;
    private int requiredLevel;
    private int[] protectionPayment;
    private int totalGrowthTicks;
    private double diseaseChance;
    private double plantingExperience;
    private double harvestExperience;
    private int configStartStage;
    private int configEndStage;
    private int healthCheckConfigStage;
    private double healthCheckExperience;
    private static Map definitionsBySeedId;

    static {
        definitionsBySeedId = new HashMap();
        BushDefinition[] bushDefinitionArray = BushDefinition.values();
        int n = bushDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            BushDefinition bushDefinition = bushDefinitionArray[n2];
            definitionsBySeedId.put(bushDefinition.seedId, bushDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private BushDefinition(int n2, int n3, int n4, int n5, int[] nArray, int n6, double d, double d2, double d3, int n7, int n8, int n9, int n10, double d4) {
        this.seedId = n2;
        this.produceItemId = n3;
        this.seedAmount = 1;
        this.requiredLevel = n5;
        this.protectionPayment = nArray;
        this.totalGrowthTicks = n6;
        this.diseaseChance = 0.2;
        this.plantingExperience = d2;
        this.harvestExperience = d3;
        this.configStartStage = n7;
        this.configEndStage = n8;
        this.healthCheckConfigStage = n10;
        this.healthCheckExperience = d4;
    }

    public static BushDefinition forSeedId(int n) {
        return (BushDefinition)((Object)definitionsBySeedId.get(n));
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

    public final int[] getProtectionPayment() {
        return this.protectionPayment;
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
        BushDefinition bushDefinition = this;
        BushDefinition bushDefinition2 = bushDefinition;
        bushDefinition2 = this;
        return bushDefinition.configEndStage - bushDefinition2.configStartStage;
    }

    public final int getGrowthCycleTicks() {
        BushDefinition bushDefinition = this;
        return bushDefinition.totalGrowthTicks / this.getGrowthStageCount();
    }

    public final int getHealthCheckConfigStage() {
        return this.healthCheckConfigStage;
    }

    public final double getHealthCheckExperience() {
        return this.healthCheckExperience;
    }
}

