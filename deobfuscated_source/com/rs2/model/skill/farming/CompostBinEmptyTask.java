/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.item.ItemStack;
import com.rs2.model.skill.farming.CompostBinManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class CompostBinEmptyTask
extends CycleEvent {
    private /* synthetic */ CompostBinManager manager;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int binIndex;
    private final /* synthetic */ int compostItemId;

    public CompostBinEmptyTask(CompostBinManager compostBinManager, int n, int n2, int n3) {
        this.manager = compostBinManager;
        this.actionSequence = n;
        this.binIndex = n2;
        this.compostItemId = n3;
    }

    @Override
    public final void execute(CycleEventContainer object) {
        if (!CompostBinManager.getPlayer(this.manager).isCurrentActionSequence(this.actionSequence) || !CompostBinManager.getPlayer(this.manager).getInventoryManager().getContainer().containsItem(1925) && this.manager.itemIds[this.binIndex] != 2518 || this.manager.states[this.binIndex] < 16) {
            ((CycleEventContainer)object).stop();
            return;
        }
        CompostBinManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.compostItemId == 6032 ? 4.5 : 8.5);
        if (this.manager.itemIds[this.binIndex] != 2518) {
            CompostBinManager.getPlayer(this.manager).getInventoryManager().removeItem(new ItemStack(1925));
        }
        CompostBinManager.getPlayer(this.manager).getInventoryManager().addItem(new ItemStack(this.compostItemId));
        CompostBinManager.getPlayer(this.manager).getUpdateState().setAnimation(832, 0);
        int n = this.binIndex;
        this.manager.states[n] = this.manager.states[n] - 1;
        if (this.manager.states[this.binIndex] < 16) {
            int n2 = this.binIndex;
            this.manager.states[n2] = 0;
            this.manager.itemIds[n2] = 0;
            this.manager.lastUpdateTicks[n2] = 0L;
        }
        this.manager.refreshConfig();
    }

    @Override
    public final void onStop() {
        CompostBinManager.getPlayer(this.manager).resetAnimation();
    }
}

