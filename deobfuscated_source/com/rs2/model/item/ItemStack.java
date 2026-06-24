/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item;

import com.rs2.model.item.ItemDefinition;
import com.rs2.model.skill.farming.FarmingPatchUtils;

public class ItemStack {
    private int id;
    private int amount;
    private int metadata;

    public ItemStack(int n) {
        this(n, 1);
    }

    public final void setAmount(int n) {
        this.amount = n;
    }

    public final void setMetadata(int n) {
        this.metadata = n;
    }

    public ItemStack(int n, int n2) {
        if (n2 < 0) {
            throw new IllegalArgumentException("Count cannot be negative.");
        }
        this.id = n;
        this.amount = n2;
        this.metadata = -1;
        int[] nArray = FarmingPatchUtils.wateredSeedlingItemIds;
        int n3 = 0;
        while (n3 < 14) {
            n2 = nArray[n3];
            if (n2 == n) {
                this.metadata = 5;
            }
            ++n3;
        }
        if (n == 1995) {
            this.metadata = 1;
        }
        if (n >= 5510 && n <= 5515 || n == 5519) {
            this.metadata = 0;
        }
    }

    public ItemStack(int n, int n2, int n3) {
        if (n2 < 0) {
            throw new IllegalArgumentException("Count cannot be negative.");
        }
        this.id = n;
        this.amount = n2;
        this.metadata = n3;
    }

    public final int getId() {
        return this.id;
    }

    public final int getAmount() {
        return this.amount;
    }

    public final int getMetadata() {
        return this.metadata;
    }

    public final ItemDefinition getDefinition() {
        return ItemDefinition.forId(this.id);
    }

    public String toString() {
        return String.valueOf(ItemStack.class.getName()) + " [id=" + this.id + ", count=" + this.amount + "]";
    }

    public final boolean isValid() {
        ItemStack itemStack = this;
        if (itemStack.id >= 0) {
            itemStack = this;
            if (itemStack.id <= 11883) {
                itemStack = this;
                if (itemStack.amount > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isEquippable() {
        if (this.isValid()) {
            ItemStack itemStack = this;
            if (ItemDefinition.forId(itemStack.id).getEquipmentSlot() != -1) {
                return true;
            }
        }
        return false;
    }
}

