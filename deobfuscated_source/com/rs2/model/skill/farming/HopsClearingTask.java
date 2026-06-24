/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.HopsPatch;
import com.rs2.model.skill.farming.HopsPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class HopsClearingTask
extends CycleEvent {
    private /* synthetic */ HopsPatchManager manager;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ HopsPatch patch;

    public HopsClearingTask(HopsPatchManager hopsPatchManager, int n, HopsPatch hopsPatch) {
        this.manager = hopsPatchManager;
        this.animationId = n;
        this.patch = hopsPatch;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        HopsPatchManager.getPlayer(this.manager).getUpdateState().setAnimation(this.animationId);
        boolean bl = false;
        if (this.manager.growthStages[this.patch.getIndex()] <= 2) {
            int n = this.patch.getIndex();
            this.manager.growthStages[n] = this.manager.growthStages[n] + 1;
            HopsPatchManager.getPlayer(this.manager).getInventoryManager().addOrDropItem(new ItemStack(6055));
        } else {
            this.manager.growthStages[this.patch.getIndex()] = 3;
            bl = true;
            cycleEventContainer.stop();
        }
        HopsPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, 4.0);
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        this.manager.refreshConfig();
        if (this.manager.growthStages[this.patch.getIndex()] == 3 && !bl) {
            cycleEventContainer.stop();
            return;
        }
    }

    @Override
    public final void onStop() {
        HopsPatchManager.resetPatch(this.manager, this.patch.getIndex());
        Player player = HopsPatchManager.getPlayer(this.manager);
        player.packetSender.sendGameMessage("You clear the patch.");
        HopsPatchManager.getPlayer(this.manager).setActionLocked(false);
        HopsPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

