/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.BushDefinition;
import com.rs2.model.skill.farming.BushPatch;
import com.rs2.model.skill.farming.BushPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class BushHarvestTask
extends CycleEvent {
    private /* synthetic */ BushPatchManager manager;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ BushPatch patch;
    private final /* synthetic */ BushDefinition definition;

    public BushHarvestTask(BushPatchManager bushPatchManager, int n, BushPatch bushPatch, BushDefinition bushDefinition) {
        this.manager = bushPatchManager;
        this.actionSequence = n;
        this.patch = bushPatch;
        this.definition = bushDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!BushPatchManager.getPlayer(this.manager).isCurrentActionSequence(this.actionSequence) || BushPatchManager.getPlayer(this.manager).getInventoryManager().getContainer().getFreeSlots() <= 0) {
            cycleEventContainer.stop();
            return;
        }
        if (this.manager.patchStates[this.patch.getIndex()] == 3) {
            Player player = BushPatchManager.getPlayer(this.manager);
            player.packetSender.sendGameMessage("You examine the bush for signs of disease and find that it's in perfect health.");
            BushPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getHealthCheckExperience());
            this.manager.patchStates[this.patch.getIndex()] = 0;
            this.manager.f[this.patch.getIndex()] = false;
            this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes() - (long)this.definition.getTotalGrowthTicks();
            this.manager.recalculateRegrowthStage(this.patch.getIndex());
            cycleEventContainer.stop();
            return;
        }
        Object object = BushPatchManager.getPlayer(this.manager);
        ((Player)object).packetSender.sendGameMessage("You harvest the crop, and pick some berries.");
        BushPatchManager.getPlayer(this.manager).getInventoryManager().addItem(new ItemStack(this.definition.getProduceItemId()));
        BushPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getHarvestExperience());
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        int n = this.definition.getTotalGrowthTicks() - this.definition.getGrowthCycleTicks() * (this.definition.getGrowthStageCount() + 5 - this.manager.growthStages[this.patch.getIndex()]);
        int n2 = this.patch.getIndex();
        object = this.manager;
        ((BushPatchManager)object).f[n2] = false;
        int n3 = n2;
        ((BushPatchManager)object).lastUpdateTicks[n3] = ((BushPatchManager)object).lastUpdateTicks[n3] - (long)n;
        this.manager.recalculateRegrowthStage(this.patch.getIndex());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        BushPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

