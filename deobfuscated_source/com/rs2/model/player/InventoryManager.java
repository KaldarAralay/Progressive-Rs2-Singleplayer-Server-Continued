/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.combat.CombatManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemContainerType;
import com.rs2.model.item.ItemStack;

public final class InventoryManager {
    private Player player;
    private ItemContainer container = new ItemContainer(ItemContainerType.a, 28);

    public InventoryManager(Player player) {
        this.player = player;
    }

    public final void refresh() {
        ItemStack[] itemStackArray = this.container.getRawItems();
        this.player.packetSender.sendItemContainer(3214, itemStackArray);
    }

    public final void sendToInterface(int n) {
        ItemStack[] itemStackArray = this.container.getRawItems();
        this.player.packetSender.sendItemContainer(n, itemStackArray);
    }

    public final int addItemPartial(ItemStack itemStack) {
        if (!this.hasSpaceFor(itemStack)) {
            this.sendNoSpaceMessage();
            return 0;
        }
        int n = itemStack.getAmount();
        if (!itemStack.getDefinition().isStackable() && n > this.container.getFreeSlots()) {
            n = this.container.getFreeSlots();
        }
        this.container.add(new ItemStack(itemStack.getId(), n, itemStack.getMetadata()), -1);
        this.refresh();
        this.player.getEquipmentManager().refreshCarriedValue();
        return n;
    }

    public final void addOrDropItem(ItemStack itemStack) {
        if (!this.addItem(itemStack)) {
            if (!itemStack.getDefinition().isStackable()) {
                int n = 0;
                while (n < itemStack.getAmount()) {
                    GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(itemStack.getId(), 1), this.player));
                    ++n;
                }
                return;
            }
            GroundItemManager.getInstance().spawn(new GroundItem(itemStack, this.player));
        }
    }

    public final boolean addItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.isValid()) {
            return false;
        }
        if (!this.hasSpaceFor(itemStack)) {
            this.sendNoSpaceMessage();
            if (this.player.botEnabled) {
                if (this.player.botCombatState != null && this.player.botCombatState.equals("loot arrows")) {
                    this.player.botCombatState = null;
                    if (this.player.botLootResumeTarget != null && !this.player.botLootResumeTarget.isDead()) {
                        CombatManager.startCombat(this.player, this.player.botLootResumeTarget);
                    } else if (this.player.currentBotTask != null) {
                        this.player.interactWithBotNpcTargets(this.player.botInteractionTargetIds);
                    }
                } else if (this.player.botCombatState != null && this.player.botCombatState.equals("loot items")) {
                    this.player.botCombatState = null;
                    this.player.botLootGroundItems.clear();
                    this.player.botLootPickupTargets.clear();
                }
            }
            return false;
        }
        int n = itemStack.getAmount();
        if (!itemStack.getDefinition().isStackable() && n > this.container.getFreeSlots()) {
            n = this.container.getFreeSlots();
            int n2 = itemStack.getAmount() - n;
            int n3 = 0;
            while (n3 < n2) {
                GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(itemStack.getId(), 1, itemStack.getMetadata()), this.player));
                ++n3;
            }
        }
        this.container.add(new ItemStack(itemStack.getId(), n, itemStack.getMetadata()), -1);
        this.refresh();
        this.player.getEquipmentManager().refreshCarriedValue();
        return true;
    }

    public final boolean addItemUpToFreeSlots(ItemStack itemStack) {
        if (!itemStack.isValid()) {
            return false;
        }
        if (!this.hasSpaceFor(itemStack)) {
            this.sendNoSpaceMessage();
            return false;
        }
        int n = itemStack.getAmount();
        if (n >= this.container.getFreeSlots()) {
            n = this.container.getFreeSlots();
        }
        this.container.add(new ItemStack(itemStack.getId(), n, itemStack.getMetadata()), -1);
        this.refresh();
        this.player.getEquipmentManager().refreshCarriedValue();
        return true;
    }

    public final boolean canAddItem(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (!this.hasSpaceFor(itemStack)) {
            this.sendNoSpaceMessage();
            return false;
        }
        return true;
    }

    public final boolean hasSpaceFor(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        boolean bl = true;
        if (itemStack.getDefinition().isStackable() && this.container.containsItem(itemStack.getId())) {
            bl = false;
        }
        return !(bl ? this.container.getFreeSlots() <= 0 && !this.container.canAdd(itemStack) : !this.container.canAdd(itemStack));
    }

    public final boolean containsItemInInventoryOrBank(int n) {
        ItemStack itemStack;
        ItemStack[] itemStackArray = this.container.getItems();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            itemStack = itemStackArray[n3];
            if (itemStack != null && itemStack.getId() == n) {
                return true;
            }
            ++n3;
        }
        itemStackArray = this.player.getBankContainer().getItems();
        n2 = itemStackArray.length;
        n3 = 0;
        while (n3 < n2) {
            itemStack = itemStackArray[n3];
            if (itemStack != null && itemStack.getId() == n) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public final void setItemInSlot(ItemStack itemStack, int n) {
        if (itemStack == null || !itemStack.isValid()) {
            return;
        }
        if (itemStack.getDefinition().isStackable() && this.container.indexOfItem(itemStack.getId()) >= 0) {
            n = this.container.indexOfItem(itemStack.getId());
            ItemStack itemStack2 = this.container.getItemAt(n);
            this.container.setItem(n, new ItemStack(itemStack.getId(), itemStack.getAmount() + itemStack2.getAmount(), itemStack.getMetadata()));
            this.refresh();
            return;
        }
        if (this.container.getFirstFreeSlot() == -1) {
            this.sendNoSpaceMessage();
            return;
        }
        this.container.setItem(n, itemStack);
        this.refresh();
    }

    public final boolean removeItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.isValid()) {
            return false;
        }
        if (!this.containsItemStack(itemStack)) {
            return false;
        }
        this.container.remove(itemStack);
        this.refresh();
        this.player.getEquipmentManager().refreshCarriedValue();
        return true;
    }

    public final void replaceItem(ItemStack itemStack, ItemStack itemStack2) {
        this.removeItem(itemStack);
        this.addItem(itemStack2);
    }

    public final boolean removeItemAtSlot(int n) {
        if (n == -1) {
            return false;
        }
        if (this.container.getItemAt(n) == null) {
            return false;
        }
        this.container.removeFromSlot(this.container.getItemAt(n), n);
        return true;
    }

    public final boolean removeItemFromSlot(ItemStack itemStack, int n) {
        if (itemStack == null || itemStack.getId() == -1) {
            return false;
        }
        if (n == -1) {
            return false;
        }
        if (this.container.getItemAt(n) == null) {
            return false;
        }
        if (!this.container.containsItem(itemStack.getId())) {
            return false;
        }
        int n2 = this.container.removeFromSlot(itemStack, n);
        this.refresh();
        return n2 > 0;
    }

    public final void swapSlots(int n, int n2) {
        this.container.swapSlots(n, n2);
        this.refresh();
    }

    public final ItemContainer getContainer() {
        return this.container;
    }

    public final boolean containsItemStack(ItemStack itemStack) {
        return this.containsItemAmount(itemStack.getId(), itemStack.getAmount());
    }

    public final boolean containsItem(int n) {
        return this.container.indexOfItem(n) >= 0;
    }

    public final boolean containsItemAmount(int n, int n2) {
        if (!this.containsItem(n)) {
            return false;
        }
        return this.container.getItemAmount(n) >= n2;
    }

    public final int getItemAmount(int n) {
        return this.player.getInventoryManager().container.getItemAmount(n);
    }

    private void sendNoSpaceMessage() {
        this.player.packetSender.sendGameMessage("Not enough space in your inventory.");
        this.player.packetSender.sendSoundEffect(1878, 1, 0);
    }
}
