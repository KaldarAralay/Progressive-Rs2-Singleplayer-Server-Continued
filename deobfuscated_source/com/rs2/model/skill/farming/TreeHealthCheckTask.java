/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.FarmedTreeDefinition;
import com.rs2.model.skill.farming.TreePatch;
import com.rs2.model.skill.farming.TreePatchManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class TreeHealthCheckTask
extends CycleEvent {
    private /* synthetic */ TreePatchManager manager;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ FarmedTreeDefinition definition;
    private final /* synthetic */ TreePatch patch;

    public TreeHealthCheckTask(TreePatchManager treePatchManager, int n, FarmedTreeDefinition farmedTreeDefinition, TreePatch treePatch) {
        this.manager = treePatchManager;
        this.actionSequence = n;
        this.definition = farmedTreeDefinition;
        this.patch = treePatch;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!TreePatchManager.getPlayer(this.manager).isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        Player player = TreePatchManager.getPlayer(this.manager);
        player.packetSender.sendGameMessage("You examine the tree for signs of disease and find that it is in perfect health");
        TreePatchManager.getPlayer(this.manager).getSkillManager().addExperience(19, this.definition.getHealthCheckExperience());
        this.manager.patchStates[this.patch.getIndex()] = 6;
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.manager.refreshConfig();
    }
}

