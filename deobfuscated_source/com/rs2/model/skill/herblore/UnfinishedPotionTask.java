/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.herblore;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class UnfinishedPotionTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ ItemStack herbItem;
    private final /* synthetic */ int baseItemId;
    private final /* synthetic */ double unfinishedPotionItemId;

    public UnfinishedPotionTask(Player player, int n, ItemStack itemStack, int n2, double d) {
        this.player = player;
        this.actionSequence = n;
        this.herbItem = itemStack;
        this.baseItemId = n2;
        this.unfinishedPotionItemId = d;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        if (this.player.getInventoryManager().containsItemStack(this.herbItem) && this.player.getInventoryManager().containsItem(this.baseItemId)) {
            this.player.getInventoryManager().removeItem(this.herbItem);
            this.player.getInventoryManager().removeItem(new ItemStack(this.baseItemId));
            this.player.getInventoryManager().addItem(new ItemStack((int)this.unfinishedPotionItemId));
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

