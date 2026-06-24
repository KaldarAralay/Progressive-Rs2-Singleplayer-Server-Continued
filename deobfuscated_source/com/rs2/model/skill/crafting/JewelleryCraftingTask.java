/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.JewelleryCraftingHandler;
import com.rs2.model.skill.crafting.JewelleryDefinition;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class JewelleryCraftingTask
extends CycleEvent {
    private int materialItemId;
    private int remainingActions;
    private int jewelleryType;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;

    public JewelleryCraftingTask(int n, int n2, int n3, Player player, int n4) {
        this.player = player;
        this.actionSequence = n4;
        this.materialItemId = n;
        this.remainingActions = n2;
        this.jewelleryType = n3;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence) || this.remainingActions-- <= 0) {
            cycleEventContainer.stop();
            return;
        }
        if (JewelleryCraftingHandler.forMaterialItemId(this.materialItemId) == null || !this.player.getInventoryManager().getContainer().containsItem(JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[0]) || !this.player.getInventoryManager().getContainer().containsItem(this.player.getSelectedSkillItemId())) {
            cycleEventContainer.stop();
            return;
        }
        cycleEventContainer.setTickDelay(4);
        this.player.getUpdateState().setAnimation(899);
        Player player = this.player;
        player.packetSender.sendSoundEffect(469, 1, 0);
        switch (this.jewelleryType) {
            case 0: {
                if (this.player.getSkillManager().getCurrentLevels()[12] < JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[3]) {
                    this.player.getDialogueManager().showOneLineStatement("You need a crafting level of " + JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[3] + " to craft this.");
                    cycleEventContainer.stop();
                    return;
                }
                if (JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[0] != this.player.getSelectedSkillItemId()) {
                    this.player.getInventoryManager().removeItem(new ItemStack(JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[0], 1));
                }
                this.player.getInventoryManager().removeItem(new ItemStack(this.player.getSelectedSkillItemId(), 1));
                int n = JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[5];
                if (this.player.getSelectedSkillItemId() == 2365 && n == 1641) {
                    n = 773;
                }
                this.player.getInventoryManager().addItem(new ItemStack(n, 1));
                player = this.player;
                player.packetSender.sendGameMessage("You craft a ring.");
                this.player.getSkillManager().addExperience(12, JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[4]);
                return;
            }
            case 1: {
                if (this.player.getSkillManager().getCurrentLevels()[12] < JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[6]) {
                    this.player.getDialogueManager().showOneLineStatement("You need a Crafting level of " + JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[6] + " to craft this.");
                    cycleEventContainer.stop();
                    return;
                }
                if (JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[0] != this.player.getSelectedSkillItemId()) {
                    this.player.getInventoryManager().removeItem(new ItemStack(JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[0], 1));
                }
                this.player.getInventoryManager().removeItem(new ItemStack(this.player.getSelectedSkillItemId(), 1));
                int n = JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[8];
                if (this.player.getSelectedSkillItemId() == 2365 && n == 1660) {
                    n = 774;
                }
                this.player.getInventoryManager().addItem(new ItemStack(n, 1));
                player = this.player;
                player.packetSender.sendGameMessage("You craft a necklace.");
                this.player.getSkillManager().addExperience(12, JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[7]);
                return;
            }
            case 2: {
                if (this.player.getSkillManager().getCurrentLevels()[12] < JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[9]) {
                    this.player.getDialogueManager().showOneLineStatement("You need a Crafting level of " + JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[9] + " to craft this.");
                    cycleEventContainer.stop();
                    return;
                }
                if (JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[0] != this.player.getSelectedSkillItemId()) {
                    this.player.getInventoryManager().removeItem(new ItemStack(JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[0], 1));
                }
                this.player.getInventoryManager().removeItem(new ItemStack(this.player.getSelectedSkillItemId(), 1));
                if (this.player.getInventoryManager().getContainer().containsItem(1759)) {
                    this.player.getInventoryManager().removeItem(new ItemStack(1759, 1));
                    this.player.getInventoryManager().addItem(new ItemStack(JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[12], 1));
                    player = this.player;
                    player.packetSender.sendGameMessage("You craft an amulet and attach a string to it.");
                } else {
                    this.player.getInventoryManager().addItem(new ItemStack(JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[11], 1));
                    player = this.player;
                    player.packetSender.sendGameMessage("You craft an amulet.");
                }
                this.player.getSkillManager().addExperience(12, JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(this.materialItemId))[10]);
                return;
            }
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

