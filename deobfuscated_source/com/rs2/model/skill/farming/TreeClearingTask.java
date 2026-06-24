/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.FarmedTreeDefinition;
import com.rs2.model.skill.farming.TreePatch;
import com.rs2.model.skill.farming.TreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class TreeClearingTask
extends CycleEvent {
    private /* synthetic */ TreePatchManager manager;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ TreePatch patch;

    public TreeClearingTask(TreePatchManager treePatchManager, int n, TreePatch treePatch) {
        this.manager = treePatchManager;
        this.animationId = n;
        this.patch = treePatch;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        TreePatchManager.getPlayer(this.manager).getUpdateState().setAnimation(this.animationId);
        boolean bl = false;
        if (this.manager.growthStages[this.patch.getIndex()] <= 2) {
            int n = this.patch.getIndex();
            this.manager.growthStages[n] = this.manager.growthStages[n] + 1;
            TreePatchManager.getPlayer(this.manager).getInventoryManager().addOrDropItem(new ItemStack(6055));
        } else if (this.manager.patchStates[this.patch.getIndex()] != 7) {
            this.manager.growthStages[this.patch.getIndex()] = 3;
            bl = true;
            cycleEventContainer.stop();
        }
        if (this.manager.patchStates[this.patch.getIndex()] == 7) {
            FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(this.manager.treeIds[this.patch.getIndex()]);
            TreePatchManager.getPlayer(this.manager).getInventoryManager().addOrDropItem(new ItemStack(farmedTreeDefinition.getRootItemId()));
            this.manager.growthStages[this.patch.getIndex()] = 3;
        }
        TreePatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, 4.0);
        this.manager.lastUpdateTicks[this.patch.getIndex()] = Server.getElapsedMinutes();
        this.manager.refreshConfig();
        if (this.manager.growthStages[this.patch.getIndex()] == 3 && !bl) {
            cycleEventContainer.stop();
            return;
        }
    }

    @Override
    public final void onStop() {
        this.manager.resetPatch(this.patch.getIndex());
        Player player = TreePatchManager.getPlayer(this.manager);
        player.packetSender.sendGameMessage("You clear the patch.");
        TreePatchManager.getPlayer(this.manager).setActionLocked(false);
        TreePatchManager.getPlayer(this.manager).resetAnimation();
    }
}

