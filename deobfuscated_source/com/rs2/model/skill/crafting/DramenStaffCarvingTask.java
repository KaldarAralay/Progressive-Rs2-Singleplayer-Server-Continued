/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.DramenStaffRecipe;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class DramenStaffCarvingTask
extends CycleEvent {
    private int remainingActions;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ DramenStaffRecipe recipe;

    public DramenStaffCarvingTask(DramenStaffRecipe dramenStaffRecipe, int n, Player player, int n2) {
        this.recipe = dramenStaffRecipe;
        this.player = player;
        this.actionSequence = n2;
        this.remainingActions = dramenStaffRecipe.getQuantity() != 0 ? dramenStaffRecipe.getQuantity() : n;
    }

    @Override
    public final void execute(CycleEventContainer object) {
        if (!this.player.isCurrentActionSequence(this.actionSequence) || this.remainingActions == 0 || this.player.getInventoryManager().getItemAmount(this.recipe.getIngredientItemId()) < this.recipe.getIngredientAmount()) {
            ((CycleEventContainer)object).stop();
            return;
        }
        this.player.getUpdateState().setAnimation(1248);
        this.player.packetSender.sendGameMessage("You carve the branch into a staff.");
        this.player.getInventoryManager().removeItem(new ItemStack(this.recipe.getIngredientItemId(), this.recipe.getIngredientAmount()));
        this.player.getInventoryManager().addItem(new ItemStack(this.recipe.getProductItemId()));
        this.player.getSkillManager().addExperience(12, this.recipe.getExperience());
        --this.remainingActions;
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

