/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.cooking.DairyChurnRecipe;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class DairyChurnTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ DairyChurnRecipe recipe;

    public DairyChurnTask(Player player, int n, DairyChurnRecipe dairyChurnRecipe) {
        this.player = player;
        this.actionSequence = n;
        this.recipe = dairyChurnRecipe;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            this.player.setSelectedSkillItemId(0);
            cycleEventContainer.stop();
            return;
        }
        int n = -1;
        int n2 = 0;
        while (n2 <= this.recipe.getIngredientItemIds().length - 1) {
            int n3 = this.recipe.getIngredientItemIds()[n2];
            if (this.player.getInventoryManager().getContainer().containsItem(n3)) {
                n = n3;
                break;
            }
            ++n2;
        }
        cycleEventContainer.setTickDelay(5);
        if (n == -1) {
            this.player.getDialogueManager().showOneLineStatement("You don't have the required items to use the churn.");
            cycleEventContainer.stop();
            return;
        }
        this.player.getUpdateState().setAnimation(894);
        Player player = this.player;
        player.packetSender.sendGameMessage("You make a " + ItemDefinition.forId(this.recipe.getProductItemId()).getName().toLowerCase() + ".");
        this.player.getInventoryManager().removeItem(new ItemStack(n));
        this.player.getInventoryManager().addItem(new ItemStack(this.recipe.getProductItemId()));
        if (n == 1927) {
            this.player.getInventoryManager().addItem(new ItemStack(1925));
        }
        this.player.getSkillManager().addExperience(7, this.recipe.getExperience());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

