/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.skill.farming.FruitTreeGrowthDefinition;
import com.rs2.model.skill.farming.FruitTreePatch;
import com.rs2.model.skill.farming.FruitTreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class FruitTreeInspectTask
extends CycleEvent {
    private /* synthetic */ FruitTreePatchManager manager;
    private final /* synthetic */ FruitTreePatch patch;
    private final /* synthetic */ FruitTreeGrowthDefinition growthDefinition;

    public FruitTreeInspectTask(FruitTreePatchManager fruitTreePatchManager, FruitTreePatch fruitTreePatch, FruitTreeGrowthDefinition fruitTreeGrowthDefinition) {
        this.manager = fruitTreePatchManager;
        this.patch = fruitTreePatch;
        this.growthDefinition = fruitTreeGrowthDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n = this.manager.growthStages[this.patch.getIndex()] - 4;
        if (this.growthDefinition.getGrowthMessages().length > n) {
            FruitTreePatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[n]);
        } else {
            FruitTreePatchManager.getPlayer(this.manager).getDialogueManager().showStatement(this.growthDefinition.getGrowthMessages()[this.growthDefinition.getGrowthMessages().length - 1]);
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        FruitTreePatchManager.getPlayer(this.manager).getUpdateState().setAnimation(1332);
        FruitTreePatchManager.getPlayer(this.manager).setActionLocked(false);
    }
}

