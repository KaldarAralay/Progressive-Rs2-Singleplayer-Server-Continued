/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.item.ItemStack;
import com.rs2.model.skill.farming.CompostBinManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class CompostBinFillTask
extends CycleEvent {
    private /* synthetic */ CompostBinManager manager;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int itemId;
    private final /* synthetic */ int binIndex;
    private final /* synthetic */ int fillAmount;

    public CompostBinFillTask(CompostBinManager compostBinManager, int n, int n2, int n3, int n4) {
        this.manager = compostBinManager;
        this.actionSequence = n;
        this.itemId = n2;
        this.binIndex = n3;
        this.fillAmount = n4;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!CompostBinManager.getPlayer(this.manager).isCurrentActionSequence(this.actionSequence) || !CompostBinManager.getPlayer(this.manager).getInventoryManager().getContainer().containsItem(this.itemId) || this.manager.states[this.binIndex] == 15) {
            cycleEventContainer.stop();
            return;
        }
        CompostBinManager.getPlayer(this.manager).getUpdateState().setAnimation(832, 0);
        CompostBinManager.getPlayer(this.manager).getInventoryManager().removeItem(new ItemStack(this.itemId));
        int n = this.binIndex;
        this.manager.states[n] = this.manager.states[n] + this.fillAmount;
        this.manager.refreshConfig();
    }

    @Override
    public final void onStop() {
        CompostBinManager.getPlayer(this.manager).resetAnimation();
    }
}

