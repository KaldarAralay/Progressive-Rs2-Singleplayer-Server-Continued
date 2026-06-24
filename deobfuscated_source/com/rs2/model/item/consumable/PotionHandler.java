/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.consumable;

import com.rs2.model.combat.effect.PoisonEffect;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.consumable.PotionDefinition;
import com.rs2.model.item.consumable.PotionEffectMode;
import com.rs2.model.player.Player;

public final class PotionHandler {
    private Player player;
    public static PotionDefinition[] definitions = new PotionDefinition[50];
    public static int definitionCount = 0;
    public int selectedDefinitionIndex = 0;
    private int selectedDoseIndex = 0;

    public PotionHandler(Player player) {
        this.player = player;
    }

    public final boolean selectPotionForItemId(int n) {
        int n2 = 0;
        while (n2 < definitionCount) {
            int n3 = 0;
            while (n3 < definitions[n2].getDoseItemIds().length) {
                if (definitions[n2].getDoseItemIds()[n3] == n) {
                    this.selectedDefinitionIndex = n2;
                    this.selectedDoseIndex = n3;
                    return true;
                }
                ++n3;
            }
            ++n2;
        }
        return false;
    }

    public final void drinkPotion(int n, int n2) {
        if (DuelRule.NO_POTIONS.isEnabledFor(this.player)) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Usage of drinks have been disabled during this fight!");
            return;
        }
        if (this.player.getSkillManager().tryStartDrinkDelay(600) && !this.player.isDead()) {
            int[] nArray = definitions[this.selectedDefinitionIndex].getDoseItemIds();
            int[] skillIds = definitions[this.selectedDefinitionIndex].getSkillIds();
            int[] nArray2 = definitions[this.selectedDefinitionIndex].getFlatBoosts();
            double[] dArray = definitions[this.selectedDefinitionIndex].getPercentBoosts();
            int n3 = 0;
            while (n3 < skillIds.length) {
                int n4;
                int n5;
                int n6;
                int n7;
                if (definitions[this.selectedDefinitionIndex].getEffectMode() == PotionEffectMode.BOOST) {
                    n7 = skillIds[n3];
                    n6 = this.player.getSkillManager().getCurrentLevels()[n7];
                    n4 = n5 = this.player.getSkillManager().getBaseLevel(n7);
                    n4 = (int)((double)n5 + (double)n5 * dArray[n3]);
                    n4 += nArray2[n3];
                    if (n6 < n5) {
                        int[] nArray3 = this.player.getSkillManager().getCurrentLevels();
                        int n8 = n7;
                        nArray3[n8] = nArray3[n8] + (n4 - n5);
                        this.player.getSkillManager().refreshSkill(n7);
                    } else if (n6 < n4) {
                        this.player.getSkillManager().getCurrentLevels()[n7] = n4;
                        this.player.getSkillManager().refreshSkill(n7);
                    }
                } else if (definitions[this.selectedDefinitionIndex].getEffectMode() == PotionEffectMode.RESTORE) {
                    n7 = skillIds[n3];
                    n6 = this.player.getSkillManager().getCurrentLevels()[n7];
                    n5 = this.player.getSkillManager().getBaseLevel(n7);
                    n4 = (int)((double)n6 + (double)n5 * dArray[n3]);
                    n4 += nArray2[n3];
                    if (n6 <= n5) {
                        if (n4 <= n5) {
                            this.player.getSkillManager().getCurrentLevels()[n7] = n4;
                            this.player.getSkillManager().refreshSkill(n7);
                        } else {
                            this.player.getSkillManager().getCurrentLevels()[n7] = this.player.getSkillManager().getBaseLevel(n7);
                            this.player.getSkillManager().refreshSkill(n7);
                        }
                    }
                }
                ++n3;
            }
            int n9 = n;
            switch (n9) {
                case 175: 
                case 177: 
                case 179: 
                case 2446: {
                    this.player.clearCombatEffectTasks(PoisonEffect.class);
                    this.player.getPoisonImmunityTimer().setDelayTicks(150);
                    this.player.getPoisonImmunityTimer().reset();
                    this.player.setPoisonDamage(0.0);
                    break;
                }
                case 3008: 
                case 3010: 
                case 3012: 
                case 3014: {
                    this.player.addRunEnergyPercent(10);
                    break;
                }
                case 181: 
                case 183: 
                case 185: 
                case 2448: {
                    this.player.clearCombatEffectTasks(PoisonEffect.class);
                    this.player.getPoisonImmunityTimer().setDelayTicks(600);
                    this.player.getPoisonImmunityTimer().reset();
                    this.player.setPoisonDamage(0.0);
                    break;
                }
                case 3016: 
                case 3018: 
                case 3020: 
                case 3022: {
                    this.player.addRunEnergyPercent(20);
                    break;
                }
                case 5943: 
                case 5945: 
                case 5947: 
                case 5949: {
                    this.player.clearCombatEffectTasks(PoisonEffect.class);
                    this.player.getPoisonImmunityTimer().setDelayTicks(900);
                    this.player.getPoisonImmunityTimer().reset();
                    this.player.setPoisonDamage(0.0);
                    break;
                }
                case 2452: 
                case 2454: 
                case 2456: 
                case 2458: {
                    this.player.getAntifireTimer().setDelayTicks(600);
                    this.player.getAntifireTimer().reset();
                    break;
                }
                case 5952: 
                case 5954: 
                case 5956: 
                case 5958: {
                    this.player.clearCombatEffectTasks(PoisonEffect.class);
                    this.player.getPoisonImmunityTimer().setDelayTicks(1200);
                    this.player.getPoisonImmunityTimer().reset();
                    this.player.setPoisonDamage(0.0);
                    break;
                }
                case 6685: 
                case 6687: 
                case 6689: 
                case 6691: {
                    this.player.heal(2);
                    Player player = this.player;
                    player.packetSender.modifySkillLevel(3, (int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(3) * 0.15), true);
                    player = this.player;
                    player.packetSender.modifySkillLevel(1, (int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(1) * 0.2), true);
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, (int)(-(2.0 + (double)this.player.getSkillManager().getBaseLevel(0) * 0.1)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(2, (int)(-(2.0 + (double)this.player.getSkillManager().getBaseLevel(2) * 0.1)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(6, (int)(-(2.0 + (double)this.player.getSkillManager().getBaseLevel(6) * 0.1)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(4, (int)(-(2.0 + (double)this.player.getSkillManager().getBaseLevel(4) * 0.1)), false);
                    break;
                }
                case 189: 
                case 191: 
                case 193: 
                case 2450: {
                    Player player = this.player;
                    player.packetSender.modifySkillLevel(0, (int)((double)this.player.getSkillManager().getBaseLevel(0) * 0.2) + 2, true);
                    player = this.player;
                    player.packetSender.modifySkillLevel(2, (int)((double)this.player.getSkillManager().getBaseLevel(2) * 0.12) + 2, true);
                    player = this.player;
                    player.packetSender.modifySkillLevel(5, (int)((double)this.player.getSkillManager().getBaseLevel(5) * 0.1), true);
                    player = this.player;
                    player.packetSender.modifySkillLevel(1, -((int)((double)this.player.getSkillManager().getBaseLevel(1) * 0.1) + 2), false);
                    this.player.applyDirectHit((int)((double)this.player.getSkillManager().getCurrentLevels()[3] * 0.12), HitType.NORMAL);
                }
            }
            this.player.getUpdateState().setAnimation(n == 3801 ? 1330 : 829, 0);
            Player player = this.player;
            player.packetSender.sendSoundEffect(334, 1, 0);
            this.player.nextActionSequence();
            this.player.getAttackDelayTimer().setDelayTicks(this.player.getAttackDelayTimer().getDelayTicks() + 2);
            switch (n) {
                case 7919: {
                    this.player.heal(14);
                    player = this.player;
                    player.packetSender.sendGameMessage("You drink the " + definitions[this.selectedDefinitionIndex].getName() + ".");
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, -3, false);
                    if (!this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().removeItem(new ItemStack(n, 1));
                    }
                    return;
                }
                case 3801: {
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, -((int)(5.0 + (double)this.player.getSkillManager().getBaseLevel(0) * 0.5)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(2, (int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(2) * 0.1), true);
                    player = this.player;
                    player.packetSender.sendGameMessage("You drink the " + definitions[this.selectedDefinitionIndex].getName() + "... that wasn't very smart!");
                    this.player.heal(15);
                    if (!this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().removeItem(new ItemStack(n, 1));
                    }
                    return;
                }
                case 1993: {
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, -2, false);
                    this.player.heal(11);
                    player = this.player;
                    player.packetSender.sendGameMessage("You drink the " + definitions[this.selectedDefinitionIndex].getName() + ".");
                    if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().setItemInSlot(new ItemStack(1935), n2);
                        return;
                    }
                    if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                        this.player.getInventoryManager().addItem(new ItemStack(1935));
                    }
                    return;
                }
                case 1978: {
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, (int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(0) * 0.02), true);
                    this.player.getUpdateState().setForcedTextAndMarkUpdated("Aaah, nothing like a nice cuppa tea!");
                    this.player.heal(3);
                    if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().setItemInSlot(new ItemStack(1980), n2);
                        return;
                    }
                    if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                        this.player.getInventoryManager().addItem(new ItemStack(1980));
                    }
                    return;
                }
                case 4627: {
                    player = this.player;
                    player.packetSender.modifySkillLevel(1, -((int)(3.0 + (double)this.player.getSkillManager().getBaseLevel(1) * 0.06)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(2, -((int)(3.0 + (double)this.player.getSkillManager().getBaseLevel(2) * 0.06)), false);
                    this.player.heal(1);
                    player = this.player;
                    player.packetSender.sendGameMessage("You drink the " + definitions[this.selectedDefinitionIndex].getName() + "... it tashtes delishush!");
                    this.player.getUpdateState().setForcedTextAndMarkUpdated("... hic!");
                    if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().setItemInSlot(new ItemStack(1919), n2);
                        return;
                    }
                    if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                        this.player.getInventoryManager().addItem(new ItemStack(1919));
                    }
                    return;
                }
                case 1913: {
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, -((int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(0) * 0.04)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(1, -((int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(1) * 0.04)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(2, -((int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(2) * 0.04)), false);
                    this.player.heal(1);
                    player = this.player;
                    player.packetSender.sendGameMessage("You drink the " + definitions[this.selectedDefinitionIndex].getName() + "... it tashtes delishush!");
                    this.player.getUpdateState().setForcedTextAndMarkUpdated("... hic!");
                    if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().setItemInSlot(new ItemStack(1919), n2);
                        return;
                    }
                    if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                        this.player.getInventoryManager().addItem(new ItemStack(1919));
                    }
                    return;
                }
                case 1907: {
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, -((int)(1.0 + (double)this.player.getSkillManager().getBaseLevel(0) * 0.05)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(1, -((int)(1.0 + (double)this.player.getSkillManager().getBaseLevel(1) * 0.05)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(2, -((int)(1.0 + (double)this.player.getSkillManager().getBaseLevel(2) * 0.05)), false);
                    player = this.player;
                    player.packetSender.modifySkillLevel(6, (int)(2.0 + (double)this.player.getSkillManager().getBaseLevel(6) * 0.02), true);
                    this.player.heal(1);
                    player = this.player;
                    player.packetSender.sendGameMessage("You drink the " + definitions[this.selectedDefinitionIndex].getName() + "... it tashtes delishush!");
                    this.player.getUpdateState().setForcedTextAndMarkUpdated("... hic!");
                    if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().setItemInSlot(new ItemStack(1919), n2);
                        return;
                    }
                    if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                        this.player.getInventoryManager().addItem(new ItemStack(1919));
                    }
                    return;
                }
                case 1917: {
                    player = this.player;
                    player.packetSender.modifySkillLevel(2, (int)(1.0 + (double)this.player.getSkillManager().getBaseLevel(2) * 0.02), true);
                    player = this.player;
                    player.packetSender.modifySkillLevel(0, -((int)(1.0 + (double)this.player.getSkillManager().getBaseLevel(0) * 0.06)), false);
                    this.player.heal(1);
                    player = this.player;
                    player.packetSender.sendGameMessage("You drink the " + definitions[this.selectedDefinitionIndex].getName() + "... it tashtes delishush!");
                    this.player.getUpdateState().setForcedTextAndMarkUpdated("... hic!");
                    if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                        this.player.getInventoryManager().setItemInSlot(new ItemStack(1919), n2);
                        return;
                    }
                    if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                        this.player.getInventoryManager().addItem(new ItemStack(1919));
                    }
                    return;
                }
            }
            if (this.selectedDoseIndex < 3) {
                if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                    this.player.getInventoryManager().setItemInSlot(new ItemStack(nArray[this.selectedDoseIndex + 1], 1), n2);
                } else if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                    this.player.getInventoryManager().addItem(new ItemStack(nArray[this.selectedDoseIndex + 1], 1));
                }
                player = this.player;
                int n10 = n;
                player.packetSender.sendGameMessage("You drink" + (n10 != 1993 && n10 != 1978 && n10 != 1917 && n10 != 1907 && n10 != 1913 && n10 != 4627 && n10 != 7919 && n10 != 3801 ? " a dose of" : "") + " your " + definitions[this.selectedDefinitionIndex].getName() + ".");
                return;
            }
            if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                this.player.getInventoryManager().setItemInSlot(new ItemStack(229), n2);
            } else if (this.player.getInventoryManager().removeItem(new ItemStack(n, 1))) {
                this.player.getInventoryManager().addItem(new ItemStack(229));
            }
            player = this.player;
            player.packetSender.sendGameMessage("You drink the last of your " + definitions[this.selectedDefinitionIndex].getName() + ".");
        }
    }

    public static PotionDefinition[] getDefinitions() {
        return definitions;
    }
}

