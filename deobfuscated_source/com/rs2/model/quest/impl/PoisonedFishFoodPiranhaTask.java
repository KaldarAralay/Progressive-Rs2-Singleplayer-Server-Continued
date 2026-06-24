/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.World;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.ErnestTheChickenQuest;
import com.rs2.model.quest.impl.PoisonedFishFoodPiranhaFinishTask;
import com.rs2.model.task.TickTask;

public final class PoisonedFishFoodPiranhaTask
extends TickTask {
    private TickTask a;
    private final /* synthetic */ Player player;

    public PoisonedFishFoodPiranhaTask(ErnestTheChickenQuest ernestTheChickenQuest, int n, Player player) {
        super(1);
        this.player = player;
        this.a = new PoisonedFishFoodPiranhaFinishTask(this, 2, player);
    }

    @Override
    public final void execute() {
        Player player = this.player;
        player.packetSender.sendGameMessage("The piranhas start eating the food...");
        World.getTaskScheduler().schedule(this.a);
        this.stop();
    }
}

