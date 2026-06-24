/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.FarmingToolDefinition;

public final class FarmingToolStore {
    private Player player;
    public int[] storedAmounts = new int[18];
    private ItemStack[] storedToolDisplayItems = new ItemStack[]{new ItemStack(5341), new ItemStack(5343), new ItemStack(952), new ItemStack(5329), new ItemStack(5331), new ItemStack(5325)};
    private ItemStack[] storedCompostDisplayItems = new ItemStack[]{new ItemStack(1925), new ItemStack(6032), new ItemStack(6034)};
    private ItemStack[] inventoryToolDisplayItems = new ItemStack[]{new ItemStack(5341), new ItemStack(5343), new ItemStack(952), new ItemStack(5329), new ItemStack(5331), new ItemStack(5325)};
    private ItemStack[] inventoryCompostDisplayItems = new ItemStack[]{new ItemStack(1925), new ItemStack(6032), new ItemStack(6034)};
    private static int[] noteableProduceItemIds = new int[]{199, 201, 203, 205, 207, 209, 211, 213, 215, 217, 219, 225, 239, 247, 249, 251, 253, 255, 257, 259, 261, 263, 265, 267, 269, 2485, 2998, 3000, 3049, 3051, 3261, 2481, 592, 1965, 1967, 6004, 5980, 5976, 1955, 1963, 2108, 5972, 2114, 754, 2126, 248, 1951, 240, 2367, 1942, 1957, 1965, 1982, 5986, 5504, 5982, 6006, 5994, 5996, 5931, 5998, 6000, 6002, 6016, 6055};

    public FarmingToolStore(Player player) {
        this.player = player;
    }

    public final void open() {
        Player player = this.player;
        player.packetSender.showInterface(15614);
        player = this.player;
        player.packetSender.sendItemContainer(15682, this.storedToolDisplayItems);
        player = this.player;
        player.packetSender.sendItemContainer(15683, this.storedCompostDisplayItems);
        player = this.player;
        player.packetSender.setSidebarInterface(3, 15593);
        player = this.player;
        player.packetSender.sendItemContainer(15594, this.inventoryToolDisplayItems);
        player = this.player;
        player.packetSender.sendItemContainer(15595, this.inventoryCompostDisplayItems);
        this.refreshInterface();
    }

    private void refreshInventoryToolDisplayItems() {
        int n = 5340;
        while (!this.player.getInventoryManager().getContainer().containsItem(n) && n >= 5330) {
            --n;
        }
        if (n == 5330) {
            return;
        }
        this.inventoryToolDisplayItems[4] = new ItemStack(n);
        if (this.player.getInventoryManager().getContainer().containsItem(7409)) {
            this.inventoryToolDisplayItems[3] = new ItemStack(7409);
            return;
        }
        this.inventoryToolDisplayItems[3] = new ItemStack(5329);
    }

    private void refreshStoredToolDisplayItems() {
        int n = 0;
        int n2 = 0;
        int n3 = 5;
        while (n3 <= 13) {
            FarmingToolDefinition farmingToolDefinition = FarmingToolDefinition.forStorageIndex(n3);
            if (this.player.getInventoryManager().getContainer().containsItem(farmingToolDefinition.getItemId())) {
                ++n2;
            }
            if (this.storedAmounts[n3] == 1) {
                ++n;
            }
            ++n3;
        }
        if (n == 0) {
            this.storedToolDisplayItems[4] = new ItemStack(5331);
        }
        if (n2 == 0) {
            this.inventoryToolDisplayItems[4] = new ItemStack(5331);
        }
    }

    private void refreshInterface() {
        int n = 0;
        this.player.ai = false;
        int n2 = 0;
        while (n2 < this.storedAmounts.length) {
            FarmingToolDefinition farmingToolDefinition = FarmingToolDefinition.forStorageIndex(n2);
            if (farmingToolDefinition == null) {
                return;
            }
            n += farmingToolDefinition.getConfigValue() * this.storedAmounts[n2];
            int n3 = n2;
            int n4 = this.player.getInventoryManager().getItemAmount(farmingToolDefinition.getItemId());
            FarmingToolDefinition farmingToolDefinition2 = farmingToolDefinition;
            FarmingToolStore farmingToolStore = this;
            if (n4 > 0) {
                if (n3 >= 5 && n3 <= 13) {
                    farmingToolStore.player.ai = true;
                }
                Player player = farmingToolStore.player;
                player.packetSender.sendInterfaceText("@gre@" + farmingToolDefinition2.getDisplayName(), farmingToolDefinition2.getNameTextInterfaceId());
                player = farmingToolStore.player;
                player.packetSender.sendInterfaceText("@gre@" + n4, farmingToolDefinition2.getAmountTextInterfaceId());
            } else if (!(n3 >= 5 && n3 <= 13 && farmingToolStore.player.ai || (n3 == 3 || n3 == 4) && (farmingToolStore.player.getInventoryManager().getContainer().containsItem(7409) || farmingToolStore.player.getInventoryManager().getContainer().containsItem(5329)))) {
                Player player = farmingToolStore.player;
                player.packetSender.sendInterfaceText(farmingToolDefinition2.getDisplayName(), farmingToolDefinition2.getNameTextInterfaceId());
                player = farmingToolStore.player;
                player.packetSender.sendInterfaceText(String.valueOf(n4), farmingToolDefinition2.getAmountTextInterfaceId());
            }
            if (farmingToolDefinition.getItemId() != 5332 && farmingToolDefinition.getItemId() >= 5331 && farmingToolDefinition.getItemId() <= 5340 && this.storedAmounts[n2] == 1) {
                this.storedToolDisplayItems[4] = new ItemStack(farmingToolDefinition.getItemId());
            }
            if (farmingToolDefinition.getItemId() == 7409 && this.storedAmounts[n2] == 1) {
                this.storedToolDisplayItems[3] = new ItemStack(7409);
            }
            ++n2;
        }
        this.refreshInventoryToolDisplayItems();
        this.refreshStoredToolDisplayItems();
        Player player = this.player;
        player.packetSender.sendConfig(615, n);
        player = this.player;
        player.packetSender.sendItemContainer(15682, this.storedToolDisplayItems);
        player = this.player;
        player.packetSender.sendItemContainer(15594, this.inventoryToolDisplayItems);
    }

    public final void depositItem(int n, int n2) {
        int n3;
        FarmingToolDefinition farmingToolDefinition;
        block11: {
            block10: {
                farmingToolDefinition = FarmingToolDefinition.forItemId(n);
                if (farmingToolDefinition == null) {
                    return;
                }
                int n4 = this.storedAmounts[farmingToolDefinition.getStorageIndex()];
                n3 = n2;
                if (!this.player.getInventoryManager().getContainer().containsItem(n)) {
                    return;
                }
                if (farmingToolDefinition.getMaxStoredAmount() == n4 || (n == 7409 || n == 5329) && (this.storedAmounts[3] == 1 || this.storedAmounts[4] == 1)) break block10;
                FarmingToolStore farmingToolStore = this;
                int n5 = 0;
                int n6 = 5;
                while (n6 <= 13) {
                    if (farmingToolStore.storedAmounts[n6] == 1) {
                        ++n5;
                    }
                    ++n6;
                }
                if (!(n5 != 0) || farmingToolDefinition.getItemId() == 5332 || n > 5340 || n < 5331) break block11;
            }
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't store any more of those.");
            return;
        }
        if (this.player.getInventoryManager().getContainer().getItemAmount(n) <= 0) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You aren't carrying any of those.");
            return;
        }
        if (this.player.getInventoryManager().getContainer().getItemAmount(n) < n2) {
            n3 = this.player.getInventoryManager().getContainer().getItemAmount(n);
        }
        n2 = n3;
        if (this.storedAmounts[farmingToolDefinition.getStorageIndex()] + n2 > farmingToolDefinition.getMaxStoredAmount()) {
            n3 -= this.storedAmounts[farmingToolDefinition.getStorageIndex()] + n2 - farmingToolDefinition.getMaxStoredAmount();
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n, n3));
        int n7 = farmingToolDefinition.getStorageIndex();
        this.storedAmounts[n7] = this.storedAmounts[n7] + n3;
        this.refreshInterface();
    }

    public final void withdrawItem(int n, int n2) {
        FarmingToolDefinition farmingToolDefinition = FarmingToolDefinition.forItemId(n);
        if (farmingToolDefinition == null) {
            return;
        }
        if (this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Not enough space in your inventory.");
            return;
        }
        if (this.storedAmounts[farmingToolDefinition.getStorageIndex()] <= 0) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You haven't got any of those stored in here.");
            return;
        }
        if (n2 > this.storedAmounts[farmingToolDefinition.getStorageIndex()]) {
            n2 = this.storedAmounts[farmingToolDefinition.getStorageIndex()];
        }
        if (n2 > this.player.getInventoryManager().getContainer().getFreeSlots()) {
            n2 = this.player.getInventoryManager().getContainer().getFreeSlots();
        }
        int n3 = farmingToolDefinition.getStorageIndex();
        this.storedAmounts[n3] = this.storedAmounts[n3] - n2;
        this.player.getInventoryManager().addItem(new ItemStack(n, n2));
        this.refreshInterface();
    }

    public final boolean noteProduce(Npc npc, int n) {
        this.player.getDialogueManager().setDialogueNpcId(3021);
        if (new ItemStack(n).getDefinition().isNote()) {
            this.player.getDialogueManager().showNpcOneLineDialogue("That IS a banknote!", 600);
            this.player.getDialogueManager().finishDialogue();
            return true;
        }
        int[] nArray = noteableProduceItemIds;
        int n2 = 0;
        while (n2 < 65) {
            int n3 = nArray[n2];
            if (n == n3 && (npc.getPosition().getX() < 3044 || npc.getPosition().getX() > 3064 || npc.getPosition().getY() < 3300 || npc.getPosition().getY() > 3313 || n != 1965)) {
                int n4 = this.player.getInventoryManager().getItemAmount(n);
                this.player.getInventoryManager().removeItem(new ItemStack(n, n4));
                this.player.getInventoryManager().addItem(new ItemStack(n + 1, n4));
                this.player.getDialogueManager().showOneLineStatement("The tool leprechaun notes those items for you.");
                this.player.getDialogueManager().finishDialogue();
                return true;
            }
            ++n2;
        }
        this.player.getDialogueManager().showNpcOneLineDialogue("Nay, I've got no banknotes to exchange for that item.", 588);
        this.player.getDialogueManager().finishDialogue();
        return true;
    }
}

