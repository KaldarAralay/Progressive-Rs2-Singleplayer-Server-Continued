/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.SpinningRecipe;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class SpinningTask
extends CycleEvent {
    private int remainingActions;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ SpinningRecipe recipe;

    public SpinningTask(SpinningRecipe spinningRecipe, int n, Player player, int n2) {
        this.recipe = spinningRecipe;
        this.player = player;
        this.actionSequence = n2;
        this.remainingActions = spinningRecipe.getQuantity() != 0 ? spinningRecipe.getQuantity() : n;
    }

    @Override
    public final void execute(CycleEventContainer object) {
        if (!this.player.isCurrentActionSequence(this.actionSequence) || this.remainingActions == 0 || !this.player.getInventoryManager().getContainer().containsItem(this.recipe.getIngredientItemId())) {
            if (this.player.botEnabled) {
                this.player.currentBotTask.startWalkToBank(this.player);
            }
            ((CycleEventContainer)object).stop();
            return;
        }
        this.player.getUpdateState().setAnimation(896);
        this.player.packetSender.sendGameMessage("You make the " + new ItemStack(this.recipe.getIngredientItemId()).getDefinition().getName() + " into a " + new ItemStack(this.recipe.getProductItemId()).getDefinition().getName() + ".");
        this.player.getInventoryManager().removeItem(new ItemStack(this.recipe.getIngredientItemId()));
        this.player.getInventoryManager().addItem(new ItemStack(this.recipe.getProductItemId()));
        this.player.getSkillManager().addExperience(12, this.recipe.getExperience());
        --this.remainingActions;
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

