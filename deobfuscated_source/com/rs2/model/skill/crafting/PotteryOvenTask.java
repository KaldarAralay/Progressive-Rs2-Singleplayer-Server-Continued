/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.PotteryRecipe;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class PotteryOvenTask
extends CycleEvent {
    private int remainingActions;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ PotteryRecipe recipe;
    private final /* synthetic */ ItemStack productItem;

    public PotteryOvenTask(PotteryRecipe potteryRecipe, int n, Player player, int n2, ItemStack itemStack) {
        this.recipe = potteryRecipe;
        this.player = player;
        this.actionSequence = n2;
        this.productItem = itemStack;
        this.remainingActions = potteryRecipe.getQuantity() != 0 ? potteryRecipe.getQuantity() : n;
    }

    @Override
    public final void execute(CycleEventContainer object) {
        if (!this.player.isCurrentActionSequence(this.actionSequence) || this.remainingActions == 0 || !this.player.getInventoryManager().getContainer().containsItem(this.recipe.getUnfiredItemId())) {
            ((CycleEventContainer)object).stop();
            return;
        }
        this.player.getUpdateState().setAnimation(896);
        this.player.getInventoryManager().removeItem(new ItemStack(this.recipe.getUnfiredItemId()));
        this.player.packetSender.sendGameMessage("You put a " + new ItemStack(this.recipe.getUnfiredItemId()).getDefinition().getName() + " in the oven.");
        boolean bl = GameUtil.rollLevelScaledChance(180, 800, this.player.getSkillManager().getCurrentLevels()[12]);
        if (bl) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You retrieve the " + new ItemStack(this.recipe.getFiredItemId()).getDefinition().getName() + " from the oven.");
            this.player.getInventoryManager().addItem(this.productItem);
            this.player.getSkillManager().addExperience(12, this.recipe.getFiringExperience());
        } else {
            Player player = this.player;
            player.packetSender.sendGameMessage("The " + new ItemStack(this.recipe.getFiredItemId()).getDefinition().getName() + " cracks in the oven.");
        }
        --this.remainingActions;
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

