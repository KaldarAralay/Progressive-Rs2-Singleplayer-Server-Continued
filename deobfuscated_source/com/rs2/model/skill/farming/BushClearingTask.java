/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.BushPatch;
import com.rs2.model.skill.farming.BushPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class BushClearingTask
extends CycleEvent {
    private /* synthetic */ BushPatchManager manager;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ BushPatch patch;

    public BushClearingTask(BushPatchManager bushPatchManager, int n, BushPatch bushPatch) {
        this.manager = bushPatchManager;
        this.animationId = n;
        this.patch = bushPatch;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        BushPatchManager.getPlayer(this.manager).getUpdateState().setAnimation(this.animationId);
        boolean bl = false;
        if (this.manager.growthStages[this.patch.getIndex()] <= 2) {
            int n = this.patch.getIndex();
            this.manager.growthStages[n] = this.manager.growthStages[n] + 1;
            BushPatchManager.getPlayer(this.manager).getInventoryManager().addOrDropItem(new ItemStack(6055));
        } else {
            this.manager.growthStages[this.patch.getIndex()] = 3;
            bl = true;
            cycleEventContainer.stop();
        }
        BushPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, 4.0);
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        this.manager.refreshConfig();
        if (this.manager.growthStages[this.patch.getIndex()] == 3 && !bl) {
            cycleEventContainer.stop();
            return;
        }
    }

    @Override
    public final void onStop() {
        BushPatchManager.resetPatch(this.manager, this.patch.getIndex());
        Player player = BushPatchManager.getPlayer(this.manager);
        player.packetSender.sendGameMessage("You clear the patch.");
        BushPatchManager.getPlayer(this.manager).setActionLocked(false);
        BushPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

