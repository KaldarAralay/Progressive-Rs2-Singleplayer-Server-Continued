/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.FruitTreePatch;
import com.rs2.model.skill.farming.FruitTreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class FruitTreeClearingTask
extends CycleEvent {
    private /* synthetic */ FruitTreePatchManager manager;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ FruitTreePatch patch;

    public FruitTreeClearingTask(FruitTreePatchManager fruitTreePatchManager, int n, FruitTreePatch fruitTreePatch) {
        this.manager = fruitTreePatchManager;
        this.animationId = n;
        this.patch = fruitTreePatch;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        FruitTreePatchManager.getPlayer(this.manager).getUpdateState().setAnimation(this.animationId);
        boolean bl = false;
        if (this.manager.growthStages[this.patch.getIndex()] <= 2) {
            int n = this.patch.getIndex();
            this.manager.growthStages[n] = this.manager.growthStages[n] + 1;
            FruitTreePatchManager.getPlayer(this.manager).getInventoryManager().addOrDropItem(new ItemStack(6055));
        } else {
            this.manager.growthStages[this.patch.getIndex()] = 3;
            bl = true;
            cycleEventContainer.stop();
        }
        FruitTreePatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, 4.0);
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        this.manager.refreshConfig();
        if (this.manager.growthStages[this.patch.getIndex()] == 3 && !bl) {
            cycleEventContainer.stop();
            return;
        }
    }

    @Override
    public final void onStop() {
        FruitTreePatchManager.resetPatch(this.manager, this.patch.getIndex());
        Player player = FruitTreePatchManager.getPlayer(this.manager);
        player.packetSender.sendGameMessage("You clear the patch.");
        FruitTreePatchManager.getPlayer(this.manager).setActionLocked(false);
        FruitTreePatchManager.getPlayer(this.manager).resetAnimation();
    }
}

