/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting.armor;

import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;

public abstract class SplitbarkCraftingAction {
    private Player player;
    private int productItemId;
    private int recipeQuantity;
    private int fineClothAmount;
    private int barkAmount;
    private int coinAmount;
    private int requestedQuantity;

    protected SplitbarkCraftingAction(Player player, int n, int n2, int n3, int n4, int n5, int n6) {
        this.player = player;
        this.productItemId = n;
        this.requestedQuantity = n3;
        this.recipeQuantity = n2;
        this.fineClothAmount = n4;
        this.barkAmount = n5;
        this.coinAmount = n6;
    }

    public final boolean startCrafting() {
        int n;
        int n2 = n = this.recipeQuantity != 0 ? this.recipeQuantity : this.requestedQuantity;
        if (!this.player.getInventoryManager().containsItemStack(new ItemStack(3470, n * this.fineClothAmount))) {
            this.player.getDialogueManager().showOneLineStatement("You need " + n * this.fineClothAmount + " " + new ItemStack(3470).getDefinition().getName().toLowerCase() + " to do this.");
            return true;
        }
        if (!this.player.getInventoryManager().containsItemStack(new ItemStack(3239, n * this.barkAmount))) {
            this.player.getDialogueManager().showOneLineStatement("You need " + n * this.barkAmount + " " + new ItemStack(3239).getDefinition().getName().toLowerCase() + " to do this.");
            return true;
        }
        if (!this.player.getInventoryManager().containsItemStack(new ItemStack(995, n * this.coinAmount))) {
            this.player.getDialogueManager().showOneLineStatement("You need " + n * this.coinAmount + " " + new ItemStack(995).getDefinition().getName().toLowerCase() + " to do this.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(3470, n * this.fineClothAmount));
        this.player.getInventoryManager().removeItem(new ItemStack(3239, n * this.barkAmount));
        this.player.getInventoryManager().removeItem(new ItemStack(995, n * this.coinAmount));
        this.player.getInventoryManager().addItem(new ItemStack(this.productItemId, n));
        DialogueManager.continueDialogue(this.player, 1263, 6, 0);
        Player player = this.player;
        player.packetSender.sendInterfaceText("What would you like to make?", 8966);
        return true;
    }
}

