/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.herblore;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class FinishedPotionTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ ItemStack secondaryIngredient;
    private final /* synthetic */ double unfinishedPotionItemId;
    private final /* synthetic */ double finishedPotionItemId;
    private final /* synthetic */ double experience;

    public FinishedPotionTask(Player player, int n, ItemStack itemStack, double d, double d2, double d3) {
        this.player = player;
        this.actionSequence = n;
        this.secondaryIngredient = itemStack;
        this.unfinishedPotionItemId = d;
        this.finishedPotionItemId = d2;
        this.experience = d3;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        if (this.player.getInventoryManager().containsItemStack(this.secondaryIngredient) && this.player.getInventoryManager().containsItemStack(new ItemStack((int)this.unfinishedPotionItemId))) {
            this.player.getInventoryManager().removeItem(this.secondaryIngredient);
            this.player.getInventoryManager().removeItem(new ItemStack((int)this.unfinishedPotionItemId));
            this.player.getInventoryManager().addItem(new ItemStack((int)this.finishedPotionItemId));
        }
        this.player.getSkillManager().addExperience(15, this.experience);
        FinishedPotionTask finishedPotionTask = this;
        finishedPotionTask.player.rollActionReward();
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

