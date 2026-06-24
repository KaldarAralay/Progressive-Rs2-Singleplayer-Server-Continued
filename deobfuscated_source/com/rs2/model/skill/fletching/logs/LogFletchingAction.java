/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fletching.logs;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.logs.LogFletchingTask;
import com.rs2.model.task.CycleEventHandler;

public abstract class LogFletchingAction {
    protected Player player;
    protected int logItemId;
    protected int productItemId;
    private int requiredLevel;
    protected double experience;
    protected int menuQuantity;
    protected int requestedQuantity;

    public LogFletchingAction(Player player, int n, int n2, int n3, double d, int n4, int n5) {
        this.player = player;
        this.logItemId = n;
        this.productItemId = n2;
        this.requiredLevel = n3;
        this.experience = d;
        this.menuQuantity = n4;
        this.requestedQuantity = n5;
    }

    public final void start() {
        Player player = this.player;
        player.packetSender.closeInterfaces();
        if (!ServerSettings.fletchingEnabled) {
            player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(946)) {
            this.player.getDialogueManager().showOneLineStatement("You need a knife to do this.");
            this.player.getDialogueManager().finishDialogue();
            return;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(this.logItemId)) {
            this.player.getDialogueManager().showOneLineStatement("You need a " + new ItemStack(this.logItemId).getDefinition().getName().toLowerCase() + "s to do this.");
            this.player.getDialogueManager().finishDialogue();
            return;
        }
        if (this.player.getSkillManager().getCurrentLevels()[9] < this.requiredLevel) {
            this.player.getDialogueManager().showOneLineStatement("You need a fletching level of " + this.requiredLevel + " to make this.");
            this.player.getDialogueManager().finishDialogue();
            return;
        }
        int n = this.player.nextActionSequence();
        this.player.setActiveCycleEvent(new LogFletchingTask(this, n));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 1);
    }
}

