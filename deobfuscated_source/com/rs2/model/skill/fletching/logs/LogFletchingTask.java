/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingAction;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class LogFletchingTask
extends CycleEvent {
    private int remainingActions;
    private /* synthetic */ LogFletchingAction action;
    private final /* synthetic */ int actionSequence;

    public LogFletchingTask(LogFletchingAction logFletchingAction, int n) {
        this.action = logFletchingAction;
        this.actionSequence = n;
        this.remainingActions = logFletchingAction.menuQuantity != 0 ? logFletchingAction.menuQuantity : logFletchingAction.requestedQuantity;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.action.player.isCurrentActionSequence(this.actionSequence) || this.remainingActions == 0 || !this.action.player.getInventoryManager().getContainer().containsItem(this.action.logItemId)) {
            String string = "";
            Player player = this.action.player;
            this.action.player.interfaceAction = string;
            this.action.player.resetAnimation();
            cycleEventContainer.stop();
            return;
        }
        this.action.player.getUpdateState().setAnimation(1248);
        Player player = this.action.player;
        player.packetSender.sendSoundEffect(811, 1, 0);
        String string = new ItemStack(this.action.productItemId).getDefinition().getName().toLowerCase();
        player = this.action.player;
        player.packetSender.sendGameMessage("You carefully cut the " + new ItemStack(this.action.logItemId).getDefinition().getName().toLowerCase() + " into " + (string.contains("shaft") ? "some arrow shafts" : (string.contains("longbow") ? "a longbow" : "a shortbow")) + ".");
        this.action.player.getInventoryManager().removeItem(new ItemStack(this.action.logItemId));
        this.action.player.getInventoryManager().addItem(new ItemStack(this.action.productItemId, this.action.productItemId == 52 ? 15 : 1));
        this.action.player.getSkillManager().addExperience(9, this.action.experience);
        --this.remainingActions;
        cycleEventContainer.setTickDelay(3);
    }

    @Override
    public final void onStop() {
        this.action.player.resetAnimation();
    }
}

