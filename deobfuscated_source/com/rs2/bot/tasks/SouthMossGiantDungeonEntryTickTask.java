/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.tasks;

import com.rs2.bot.tasks.EdgevilleDungeonSouthMossGiantCombatBotTask;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import java.util.ArrayList;

public final class SouthMossGiantDungeonEntryTickTask
extends TickTask {
    private final /* synthetic */ Player player;

    public SouthMossGiantDungeonEntryTickTask(EdgevilleDungeonSouthMossGiantCombatBotTask edgevilleDungeonSouthMossGiantCombatBotTask, int n, Player player) {
        super(3);
        this.player = player;
    }

    @Override
    public final void execute() {
        if (this.player.isDead() || !this.player.isRegistered()) {
            this.stop();
            return;
        }
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(882);
        this.player.interactWithBotObjectTargetsNoRetry(arrayList, false);
        this.stop();
    }
}

