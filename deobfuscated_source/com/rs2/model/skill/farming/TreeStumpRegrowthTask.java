/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.TreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class TreeStumpRegrowthTask
extends CycleEvent {
    private /* synthetic */ TreePatchManager manager;
    private final /* synthetic */ int patchIndex;

    public TreeStumpRegrowthTask(TreePatchManager treePatchManager, int n) {
        this.manager = treePatchManager;
        this.patchIndex = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (this.manager.patchStates[this.patchIndex] == 7) {
            this.manager.patchStates[this.patchIndex] = 6;
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

