/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.FremennikTrialsQuest;
import com.rs2.model.quest.impl.ManniDrinkingContestResultTask;
import com.rs2.model.task.TickTask;

public final class ManniDrinkingContestPlayerDrinkTask
extends TickTask {
    private TickTask a;
    private final /* synthetic */ Player player;

    public ManniDrinkingContestPlayerDrinkTask(FremennikTrialsQuest fremennikTrialsQuest, int n, Player player, int n2) {
        super(6);
        this.player = player;
        this.a = new ManniDrinkingContestResultTask(this, 7, player, n2);
    }

    @Override
    public final void execute() {
        if (this.player.pendingGameMode == 3711) {
            this.player.getInventoryManager().removeItem(new ItemStack(3711, 1));
            Player player = this.player;
            player.packetSender.sendGameMessage("You drink from your keg. You don't feel at all drunk.");
        } else {
            this.player.getInventoryManager().removeItem(new ItemStack(3801, 1));
            Player player = this.player;
            player.packetSender.sendGameMessage("You drink from your keg. You feel extremely drunk...");
        }
        this.player.getUpdateState().setAnimation(1330);
        World.getTaskScheduler().schedule(this.a);
        this.stop();
    }
}

