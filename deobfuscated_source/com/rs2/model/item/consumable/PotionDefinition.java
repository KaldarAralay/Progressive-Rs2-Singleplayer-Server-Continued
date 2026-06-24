/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.consumable;

import com.rs2.model.item.consumable.PotionEffectMode;

public final class PotionDefinition {
    private boolean antipoison;
    private String name;
    private PotionEffectMode effectMode;
    private int[] doseItemIds;
    private int[] skillIds;
    private int[] flatBoosts;
    private double[] percentBoosts;

    public final boolean isAntipoison() {
        return this.antipoison;
    }

    public final String getName() {
        return this.name;
    }

    public final int[] getDoseItemIds() {
        return this.doseItemIds;
    }

    public final int[] getSkillIds() {
        return this.skillIds;
    }

    public final int[] getFlatBoosts() {
        return this.flatBoosts;
    }

    public final double[] getPercentBoosts() {
        return this.percentBoosts;
    }

    public final PotionEffectMode getEffectMode() {
        return this.effectMode;
    }

    static /* synthetic */ void setEffectMode(PotionDefinition potionDefinition, PotionEffectMode potionEffectMode) {
        potionDefinition.effectMode = potionEffectMode;
    }

    static /* synthetic */ void setDoseItemIds(PotionDefinition potionDefinition, int[] nArray) {
        potionDefinition.doseItemIds = nArray;
    }

    static /* synthetic */ void setAntipoison(PotionDefinition potionDefinition, boolean bl) {
        potionDefinition.antipoison = bl;
    }

    static /* synthetic */ int[] getMutableDoseItemIds(PotionDefinition potionDefinition) {
        return potionDefinition.doseItemIds;
    }

    static /* synthetic */ void setName(PotionDefinition potionDefinition, String string) {
        potionDefinition.name = string;
    }

    static /* synthetic */ void setSkillIds(PotionDefinition potionDefinition, int[] nArray) {
        potionDefinition.skillIds = nArray;
    }

    static /* synthetic */ void setFlatBoosts(PotionDefinition potionDefinition, int[] nArray) {
        potionDefinition.flatBoosts = nArray;
    }

    static /* synthetic */ void setPercentBoosts(PotionDefinition potionDefinition, double[] dArray) {
        potionDefinition.percentBoosts = dArray;
    }

    static /* synthetic */ int[] getMutableSkillIds(PotionDefinition potionDefinition) {
        return potionDefinition.skillIds;
    }

    static /* synthetic */ int[] getMutableFlatBoosts(PotionDefinition potionDefinition) {
        return potionDefinition.flatBoosts;
    }

    static /* synthetic */ double[] getMutablePercentBoosts(PotionDefinition potionDefinition) {
        return potionDefinition.percentBoosts;
    }
}

