/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.armor.CraftedArmorTask;
import com.rs2.model.task.CycleEventHandler;

public abstract class CraftedArmorAction {
    protected Player player;
    protected int materialItemId;
    protected int materialAmount;
    protected int productItemId;
    protected int recipeQuantity;
    protected int requestedQuantity;
    private int requiredLevel;
    protected double experience;

    protected CraftedArmorAction(Player player, int n, int n2, int n3, int n4, int n5, int n6, double d) {
        this.player = player;
        this.materialItemId = n;
        this.materialAmount = n2;
        this.productItemId = n3;
        this.requestedQuantity = n5;
        this.recipeQuantity = n4;
        this.requiredLevel = n6;
        this.experience = d;
    }

    public final boolean startCrafting() {
        Object object = this.player;
        ((Player)object).packetSender.closeInterfaces();
        if (!ServerSettings.craftingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(1733)) {
            this.player.getDialogueManager().showOneLineStatement("You need a needle to do this.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(1734)) {
            this.player.getDialogueManager().showOneLineStatement("You need thread to do this.");
            return true;
        }
        if (!this.player.getInventoryManager().containsItemStack(new ItemStack(this.materialItemId, this.materialAmount))) {
            this.player.getDialogueManager().showOneLineStatement("You need " + this.materialAmount + " " + new ItemStack(this.materialItemId).getDefinition().getName().toLowerCase() + " to do this.");
            return true;
        }
        if (this.player.getSkillManager().getCurrentLevels()[12] < this.requiredLevel) {
            this.player.getDialogueManager().showOneLineStatement("You need a crafting level of " + this.requiredLevel + " to make this.");
            return true;
        }
        object = new ItemStack(this.productItemId, 1);
        if (((ItemStack)object).getDefinition().isMembersOnly()) {
            if (!this.player.isMember()) {
                this.player.packetSender.sendGameMessage("You need a members account to access members content.");
                return true;
            }
            if (ServerSettings.freeToPlayWorld) {
                this.player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                return true;
            }
        }
        int n = this.player.nextActionSequence();
        this.player.setActiveCycleEvent(new CraftedArmorTask(this, n));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 1);
        return true;
    }
}

