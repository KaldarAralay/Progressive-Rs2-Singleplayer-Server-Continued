/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.WeavingRecipe;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class WeavingTask
extends CycleEvent {
    private int remainingActions;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ WeavingRecipe recipe;

    public WeavingTask(WeavingRecipe weavingRecipe, int n, Player player, int n2) {
        this.recipe = weavingRecipe;
        this.player = player;
        this.actionSequence = n2;
        this.remainingActions = weavingRecipe.getQuantity() != 0 ? weavingRecipe.getQuantity() : n;
    }

    @Override
    public final void execute(CycleEventContainer object) {
        if (!this.player.isCurrentActionSequence(this.actionSequence) || this.remainingActions == 0 || this.player.getInventoryManager().getItemAmount(this.recipe.getIngredientItemId()) < this.recipe.getIngredientAmount()) {
            ((CycleEventContainer)object).stop();
            return;
        }
        this.player.getUpdateState().setAnimation(895);
        this.player.packetSender.sendGameMessage("You weave your materials into " + new ItemStack(this.recipe.getProductItemId()).getDefinition().getName().toLowerCase() + "s.");
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

