/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.GlassblowingRecipe;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class GlassblowingTask
extends CycleEvent {
    private int remainingActions;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ GlassblowingRecipe recipe;

    public GlassblowingTask(GlassblowingRecipe glassblowingRecipe, int n, Player player, int n2) {
        this.recipe = glassblowingRecipe;
        this.player = player;
        this.actionSequence = n2;
        this.remainingActions = glassblowingRecipe.getQuantity() != 0 ? glassblowingRecipe.getQuantity() : n;
    }

    @Override
    public final void execute(CycleEventContainer object) {
        if (!this.player.isCurrentActionSequence(this.actionSequence) || this.remainingActions == 0 || !this.player.getInventoryManager().getContainer().containsItem(1775)) {
            ((CycleEventContainer)object).stop();
            return;
        }
        ((CycleEventContainer)object).setTickDelay(3);
        this.player.getUpdateState().setAnimation(884);
        this.player.packetSender.sendGameMessage("You make the molten glass into a " + new ItemStack(this.recipe.getProductItemId()).getDefinition().getName() + ".");
        this.player.getInventoryManager().removeItem(new ItemStack(1775));
        this.player.getInventoryManager().addItem(new ItemStack(this.recipe.getProductItemId()));
        this.player.getSkillManager().addExperience(12, this.recipe.getExperience());
        --this.remainingActions;
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

