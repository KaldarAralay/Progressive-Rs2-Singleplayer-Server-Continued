/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.FruitTreeDefinition;
import com.rs2.model.skill.farming.FruitTreePatch;
import com.rs2.model.skill.farming.FruitTreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class FruitTreeHarvestTask
extends CycleEvent {
    private /* synthetic */ FruitTreePatchManager manager;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ FruitTreePatch patch;
    private final /* synthetic */ FruitTreeDefinition definition;

    public FruitTreeHarvestTask(FruitTreePatchManager fruitTreePatchManager, int n, FruitTreePatch fruitTreePatch, FruitTreeDefinition fruitTreeDefinition) {
        this.manager = fruitTreePatchManager;
        this.actionSequence = n;
        this.patch = fruitTreePatch;
        this.definition = fruitTreeDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!FruitTreePatchManager.getPlayer(this.manager).isCurrentActionSequence(this.actionSequence) || FruitTreePatchManager.getPlayer(this.manager).getInventoryManager().getContainer().getFreeSlots() <= 0) {
            cycleEventContainer.stop();
            return;
        }
        if (this.manager.patchStates[this.patch.getIndex()] == 3) {
            Player player = FruitTreePatchManager.getPlayer(this.manager);
            player.packetSender.sendGameMessage("You examine the tree for signs of disease and find that it's in perfect health.");
            FruitTreePatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getHealthCheckExperience());
            this.manager.patchStates[this.patch.getIndex()] = 0;
            this.manager.f[this.patch.getIndex()] = false;
            this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes() - (long)this.definition.getTotalGrowthTicks();
            this.manager.recalculateRegrowthStage(this.patch.getIndex());
            cycleEventContainer.stop();
            return;
        }
        Object object = FruitTreePatchManager.getPlayer(this.manager);
        ((Player)object).packetSender.sendGameMessage("You harvest the crop, and pick a fruit.");
        FruitTreePatchManager.getPlayer(this.manager).getInventoryManager().addItem(new ItemStack(this.definition.getProduceItemId()));
        FruitTreePatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getHarvestExperience());
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        int n = this.definition.getTotalGrowthTicks() - this.definition.getGrowthCycleTicks() * (this.definition.getGrowthStageCount() + 5 - this.manager.growthStages[this.patch.getIndex()]);
        int n2 = this.patch.getIndex();
        object = this.manager;
        ((FruitTreePatchManager)object).f[n2] = false;
        int n3 = n2;
        ((FruitTreePatchManager)object).lastUpdateTicks[n3] = ((FruitTreePatchManager)object).lastUpdateTicks[n3] - (long)n;
        this.manager.recalculateRegrowthStage(this.patch.getIndex());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

