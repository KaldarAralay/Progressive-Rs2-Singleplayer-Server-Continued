/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.skill.farming.FruitTreeDefinition;
import com.rs2.model.skill.farming.FruitTreePatch;
import com.rs2.model.skill.farming.FruitTreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class FruitTreePlantingTask
extends CycleEvent {
    private /* synthetic */ FruitTreePatchManager manager;
    private final /* synthetic */ FruitTreePatch patch;
    private final /* synthetic */ int saplingId;
    private final /* synthetic */ FruitTreeDefinition definition;

    public FruitTreePlantingTask(FruitTreePatchManager fruitTreePatchManager, FruitTreePatch fruitTreePatch, int n, FruitTreeDefinition fruitTreeDefinition) {
        this.manager = fruitTreePatchManager;
        this.patch = fruitTreePatch;
        this.saplingId = n;
        this.definition = fruitTreeDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.manager.patchStates[this.patch.getIndex()] = 0;
        this.manager.treeIds[this.patch.getIndex()] = this.saplingId;
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        FruitTreePatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getPlantingExperience());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.manager.refreshConfig();
        FruitTreePatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

