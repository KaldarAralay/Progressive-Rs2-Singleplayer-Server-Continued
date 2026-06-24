/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import java.util.HashMap;
import java.util.Map;

public enum FlowerDefinition {
    MARIGOLD(5096, 6010, 2, 20, 0.35, 8.5, 47.0, 8, 12),
    ROSEMARY(5097, 6014, 11, 20, 0.32, 12.0, 66.5, 13, 17),
    NASTURTIUM(5098, 6012, 24, 20, 0.3, 19.5, 111.0, 18, 22),
    WOAD(5099, 1793, 25, 20, 0.27, 20.5, 115.5, 23, 27),
    LIMPWURT(5100, 225, 26, 20, 0.215, 8.5, 120.0, 28, 32);

    private int seedId;
    private int produceItemId;
    private int requiredLevel;
    private int totalGrowthTicks;
    private double diseaseChance;
    private double plantingExperience;
    private double harvestExperience;
    private int configStartStage;
    private int configEndStage;
    private static Map definitionsBySeedId;

    static {
        definitionsBySeedId = new HashMap();
        FlowerDefinition[] flowerDefinitionArray = FlowerDefinition.values();
        int n = flowerDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            FlowerDefinition flowerDefinition = flowerDefinitionArray[n2];
            definitionsBySeedId.put(flowerDefinition.seedId, flowerDefinition);
            ++n2;
        }
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private FlowerDefinition(int n2, int n3, int n4, int n5, double d, double d2, double d3, int n6, int n7) {
        this.seedId = n2;
        this.produceItemId = n3;
        this.requiredLevel = n4;
        this.totalGrowthTicks = 20;
        this.diseaseChance = d;
        this.plantingExperience = d2;
        this.harvestExperience = d3;
        this.configStartStage = n6;
        this.configEndStage = n7;
    }

    public static FlowerDefinition forSeedId(int n) {
        return (FlowerDefinition)((Object)definitionsBySeedId.get(n));
    }

    public final int getProduceItemId() {
        return this.produceItemId;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
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

    public final int getConfigEndStage() {
        return this.configEndStage;
    }

    public final int getGrowthStageCount() {
        FlowerDefinition flowerDefinition = this;
        FlowerDefinition flowerDefinition2 = flowerDefinition;
        flowerDefinition2 = this;
        return flowerDefinition.configEndStage - flowerDefinition2.configStartStage;
    }

    public final int getGrowthCycleTicks() {
        FlowerDefinition flowerDefinition = this;
        return flowerDefinition.totalGrowthTicks / this.getGrowthStageCount();
    }
}

