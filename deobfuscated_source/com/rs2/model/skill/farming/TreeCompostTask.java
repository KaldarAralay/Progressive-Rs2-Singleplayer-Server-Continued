/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.TreePatch;
import com.rs2.model.skill.farming.TreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class TreeCompostTask
extends CycleEvent {
    private /* synthetic */ TreePatchManager manager;
    private final /* synthetic */ TreePatch patch;
    private final /* synthetic */ int compostItemId;

    public TreeCompostTask(TreePatchManager treePatchManager, TreePatch treePatch, int n) {
        this.manager = treePatchManager;
        this.patch = treePatch;
        this.compostItemId = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.patch.getIndex();
        this.manager.diseaseChanceMultipliers[n] = this.manager.diseaseChanceMultipliers[n] * (this.compostItemId == 6032 ? 0.35 : 0.1);
        this.manager.patchStates[this.patch.getIndex()] = 5;
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        TreePatchManager.getPlayer(this.manager).setActionLocked(false);
        TreePatchManager.getPlayer(this.manager).resetAnimation();
    }
}

