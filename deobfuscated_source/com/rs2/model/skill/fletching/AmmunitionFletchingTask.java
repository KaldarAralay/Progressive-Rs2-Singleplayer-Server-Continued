/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.AmmunitionFletchingDefinition;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AmmunitionFletchingTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ AmmunitionFletchingDefinition definition;
    private final /* synthetic */ int consumedAmount;
    private final /* synthetic */ int productDivisor;

    public AmmunitionFletchingTask(Player player, int n, AmmunitionFletchingDefinition ammunitionFletchingDefinition, int n2, int n3) {
        this.player = player;
        this.actionSequence = n;
        this.definition = ammunitionFletchingDefinition;
        this.consumedAmount = n2;
        this.productDivisor = n3;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!(this.player.isCurrentActionSequence(this.actionSequence) && this.player.getInventoryManager().containsItemStack(new ItemStack(this.definition.getComponentItemId(), this.consumedAmount)) && this.player.getInventoryManager().containsItemStack(new ItemStack(this.definition.getBaseItemId(), this.consumedAmount)))) {
            cycleEventContainer.stop();
            return;
        }
        String string = new ItemStack(this.definition.getBaseItemId()).getDefinition().getName().toLowerCase();
        Player player = this.player;
        player.packetSender.sendGameMessage("You attach the " + new ItemStack(this.definition.getComponentItemId()).getDefinition().getName().toLowerCase() + " to " + this.consumedAmount / this.productDivisor + " " + string + (string.endsWith("s") ? "." : "s."));
        this.player.getInventoryManager().removeItem(new ItemStack(this.definition.getComponentItemId(), this.consumedAmount));
        this.player.getInventoryManager().removeItem(new ItemStack(this.definition.getBaseItemId(), this.consumedAmount));
        this.player.getInventoryManager().addItem(new ItemStack(this.definition.getProductItemId(), this.consumedAmount / this.productDivisor));
        this.player.getSkillManager().addExperience(9, (double)(this.consumedAmount / this.productDivisor) * this.definition.getExperience());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

