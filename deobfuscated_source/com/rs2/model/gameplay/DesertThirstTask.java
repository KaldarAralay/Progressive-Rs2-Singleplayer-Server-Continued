/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay;

import com.rs2.model.combat.hit.HitType;
import com.rs2.model.gameplay.DesertHeatManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class DesertThirstTask
extends TickTask {
    private final /* synthetic */ Player player;

    public DesertThirstTask(int n, Player player) {
        super(150);
        this.player = player;
    }

    @Override
    public final void execute() {
        if (!this.player.isRegistered()) {
            this.stop();
            return;
        }
        if (DesertHeatManager.isInDesertHeatRegion(this.player)) {
            int n = 0;
            if (this.player.getInventoryManager().containsItem(1829)) {
                n = 1829;
            } else if (this.player.getInventoryManager().containsItem(1827)) {
                n = 1827;
            } else if (this.player.getInventoryManager().containsItem(1825)) {
                n = 1825;
            } else if (this.player.getInventoryManager().containsItem(1823)) {
                n = 1823;
            }
            if (n != 0) {
                Player player = this.player;
                player.packetSender.sendGameMessage("You take a drink of water.");
                this.player.getUpdateState().setAnimation(829);
                this.player.getInventoryManager().removeItem(new ItemStack(n, 1));
                this.player.getInventoryManager().addOrDropItem(new ItemStack(n + 2, 1));
            } else {
                this.player.applyDirectHit(1 + GameUtil.randomInclusive(9), HitType.NORMAL);
                Player player = this.player;
                player.packetSender.sendGameMessage("You should get a waterskin for any travelling in the desert.");
                player = this.player;
                player.packetSender.sendGameMessage("You start dying of thirst while you're in the desert.");
            }
            this.setIntervalTicks(150);
            if (this.player.getEquipmentManager().getItemIdAtSlot(4) == DesertHeatManager.DESERT_SHIRT_ITEM_ID) {
                this.setIntervalTicks(this.getIntervalTicks() + 20);
            }
            if (this.player.getEquipmentManager().getItemIdAtSlot(7) == DesertHeatManager.DESERT_ROBE_ITEM_ID) {
                this.setIntervalTicks(this.getIntervalTicks() + 20);
            }
            if (this.player.getEquipmentManager().getItemIdAtSlot(10) == DesertHeatManager.DESERT_BOOTS_ITEM_ID) {
                this.setIntervalTicks(this.getIntervalTicks() + 10);
            }
            if (this.player.getEquipmentManager().getItemIdAtSlot(0) == DesertHeatManager.MENAPHITE_PURPLE_HAT_ITEM_ID || this.player.getEquipmentManager().getItemIdAtSlot(0) == DesertHeatManager.MENAPHITE_RED_HAT_ITEM_ID) {
                this.setIntervalTicks(this.getIntervalTicks() + 20);
            }
            if (this.player.getEquipmentManager().getItemIdAtSlot(4) == DesertHeatManager.MENAPHITE_PURPLE_TOP_ITEM_ID || this.player.getEquipmentManager().getItemIdAtSlot(4) == DesertHeatManager.MENAPHITE_RED_TOP_ITEM_ID) {
                this.setIntervalTicks(this.getIntervalTicks() + 20);
            }
            if (this.player.getEquipmentManager().getItemIdAtSlot(7) == DesertHeatManager.MENAPHITE_PURPLE_ROBE_ITEM_ID || this.player.getEquipmentManager().getItemIdAtSlot(7) == DesertHeatManager.MENAPHITE_RED_ROBE_ITEM_ID) {
                this.setIntervalTicks(this.getIntervalTicks() + 20);
            }
            if (this.player.getEquipmentManager().getItemIdAtSlot(7) == DesertHeatManager.MENAPHITE_PURPLE_KILT_ITEM_ID || this.player.getEquipmentManager().getItemIdAtSlot(7) == DesertHeatManager.MENAPHITE_RED_KILT_ITEM_ID) {
                this.setIntervalTicks(this.getIntervalTicks() + 20);
                return;
            }
        } else {
            this.player.activeEnvironmentalHazardId = -1;
            this.stop();
        }
    }
}

