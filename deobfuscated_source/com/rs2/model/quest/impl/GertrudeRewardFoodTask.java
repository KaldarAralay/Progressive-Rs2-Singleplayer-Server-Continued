/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.GertrudeQuestCompletionTask;
import com.rs2.model.quest.impl.GertrudesCatQuest;
import com.rs2.model.task.TickTask;

public final class GertrudeRewardFoodTask
extends TickTask {
    private TickTask b;
    final /* synthetic */ GertrudesCatQuest a;
    private final /* synthetic */ Player player;

    public GertrudeRewardFoodTask(GertrudesCatQuest gertrudesCatQuest, int n, Player player) {
        super(5);
        this.a = gertrudesCatQuest;
        this.player = player;
        this.b = new GertrudeQuestCompletionTask(this, 4, player);
    }

    @Override
    public final void execute() {
        Player player = this.player;
        player.packetSender.sendGameMessage("... and some food!");
        this.player.getInventoryManager().addOrDropItem(new ItemStack(1897, 1));
        this.player.getInventoryManager().addOrDropItem(new ItemStack(2003, 1));
        World.getTaskScheduler().schedule(this.b);
        this.stop();
    }
}

