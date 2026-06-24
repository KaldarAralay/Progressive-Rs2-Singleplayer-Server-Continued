/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.PiratesTreasureQuest;
import com.rs2.model.quest.impl.RumBananaCratePromptTask;
import com.rs2.model.task.TickTask;

public final class RumBananaCrateSearchTask
extends TickTask {
    private TickTask a;
    private final /* synthetic */ Player player;

    public RumBananaCrateSearchTask(PiratesTreasureQuest piratesTreasureQuest, int n, Player player) {
        super(3);
        this.player = player;
        this.a = new RumBananaCratePromptTask(this, 2, player);
    }

    @Override
    public final void execute() {
        this.player.getInventoryManager().addOrDropItem(new ItemStack(431, 1));
        this.player.getUpdateState().setAnimation(832);
        Player player = this.player;
        player.packetSender.sendGameMessage("You find your bottle of rum in amongst the bananas.");
        World.getTaskScheduler().schedule(this.a);
        this.stop();
    }
}

