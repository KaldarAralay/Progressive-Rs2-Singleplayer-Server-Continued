/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.HerbPatch;
import com.rs2.model.skill.farming.HerbPatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class HerbClearingTask
extends CycleEvent {
    private /* synthetic */ HerbPatchManager manager;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ HerbPatch patch;

    public HerbClearingTask(HerbPatchManager herbPatchManager, int n, HerbPatch herbPatch) {
        this.manager = herbPatchManager;
        this.animationId = n;
        this.patch = herbPatch;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        HerbPatchManager.getPlayer(this.manager).getUpdateState().setAnimation(this.animationId);
        boolean bl = false;
        if (this.manager.growthStages[this.patch.getIndex()] <= 2) {
            int n = this.patch.getIndex();
            this.manager.growthStages[n] = this.manager.growthStages[n] + 1;
            HerbPatchManager.getPlayer(this.manager).getInventoryManager().addOrDropItem(new ItemStack(6055));
        } else {
            this.manager.growthStages[this.patch.getIndex()] = 3;
            bl = true;
            cycleEventContainer.stop();
        }
        HerbPatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, 4.0);
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        this.manager.refreshConfig();
        if (this.manager.growthStages[this.patch.getIndex()] == 3 && !bl) {
            cycleEventContainer.stop();
            return;
        }
    }

    @Override
    public final void onStop() {
        HerbPatchManager.resetPatch(this.manager, this.patch.getIndex());
        Player player = HerbPatchManager.getPlayer(this.manager);
        player.packetSender.sendGameMessage("You clear the patch.");
        HerbPatchManager.getPlayer(this.manager).setActionLocked(false);
        HerbPatchManager.getPlayer(this.manager).resetAnimation();
    }
}

