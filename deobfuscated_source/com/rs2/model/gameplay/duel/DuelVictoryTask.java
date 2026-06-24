/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.gameplay.duel.DuelArenaLocationManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class DuelVictoryTask
extends CycleEvent {
    private final /* synthetic */ Player winner;
    private final /* synthetic */ String loserUsername;
    private final /* synthetic */ String loserCombatLevel;
    private final /* synthetic */ ItemStack[] rewardItems;

    public DuelVictoryTask(Player player, String string, String string2, ItemStack[] itemStackArray) {
        this.winner = player;
        this.loserUsername = string;
        this.loserCombatLevel = string2;
        this.rewardItems = itemStackArray;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (this.winner != null) {
            this.winner.getDuelArenaLocationManager();
            this.winner.moveTo(DuelArenaLocationManager.randomExitPosition());
            Object object = this.winner;
            ((Player)object).packetSender.sendInterfaceText(this.loserUsername, 6840);
            object = this.winner;
            ((Player)object).packetSender.sendInterfaceText(this.loserCombatLevel, 6839);
            object = this.winner;
            ((Player)object).packetSender.sendItemContainer(6822, this.rewardItems);
            object = this.winner;
            ((Player)object).packetSender.sendEntityHintIcon(10, -1);
            ItemStack[] itemStackArray = this.rewardItems;
            int n = this.rewardItems.length;
            int n2 = 0;
            while (n2 < n) {
                object = itemStackArray[n2];
                this.winner.getInventoryManager().addOrDropItem((ItemStack)object);
                ++n2;
            }
            this.winner.eq();
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        if (this.winner != null) {
            this.winner.setActionLocked(false);
            Player player = this.winner;
            player.packetSender.showInterface(6733);
            player = this.winner;
            player.packetSender.sendMusicJingle(221, 200);
        }
    }
}

