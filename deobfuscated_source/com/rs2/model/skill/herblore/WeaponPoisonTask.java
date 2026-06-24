/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.herblore;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.herblore.PoisonedWeaponDefinition;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class WeaponPoisonTask
extends CycleEvent {
    private int outputItemId = 0;
    private final /* synthetic */ int poisonTier;
    private final /* synthetic */ PoisonedWeaponDefinition weaponDefinition;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int weaponItemId;

    public WeaponPoisonTask(int n, PoisonedWeaponDefinition poisonedWeaponDefinition, Player player, int n2) {
        this.poisonTier = n;
        this.weaponDefinition = poisonedWeaponDefinition;
        this.player = player;
        this.weaponItemId = n2;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n;
        switch (this.poisonTier) {
            case 1: {
                this.outputItemId = this.weaponDefinition.getPoisonedItemId();
                this.player.getInventoryManager().removeItem(new ItemStack(187));
                this.player.getInventoryManager().addItem(new ItemStack(229));
                break;
            }
            case 2: {
                this.outputItemId = this.weaponDefinition.getPoisonPlusItemId();
                this.player.getInventoryManager().removeItem(new ItemStack(5937));
                this.player.getInventoryManager().addItem(new ItemStack(229));
                break;
            }
            case 3: {
                this.outputItemId = this.weaponDefinition.getPoisonPlusPlusItemId();
                this.player.getInventoryManager().removeItem(new ItemStack(5940));
                this.player.getInventoryManager().addItem(new ItemStack(229));
            }
        }
        Object object = new ItemStack(this.weaponItemId);
        int n2 = n = this.player.getInventoryManager().getItemAmount(this.weaponItemId) < 15 ? this.player.getInventoryManager().getItemAmount(this.weaponItemId) : 15;
        if (!((ItemStack)object).getDefinition().isStackable()) {
            n = 1;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(this.weaponItemId, n));
        this.player.getInventoryManager().addItem(new ItemStack(this.outputItemId, n));
        object = this.player;
        ((Player)object).packetSender.sendGameMessage("You imbue the " + new ItemStack(this.weaponItemId).getDefinition().getName().toLowerCase() + (this.player.getInventoryManager().getItemAmount(this.weaponItemId) > 1 && new ItemStack(this.weaponItemId).getDefinition().isStackable() ? "s" : "") + " with poison");
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

