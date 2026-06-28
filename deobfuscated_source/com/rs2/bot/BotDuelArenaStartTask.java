/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.bot.BotPlayer;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.model.task.TickTask;

public final class BotDuelArenaStartTask
extends TickTask {
    private final /* synthetic */ BotPlayer bot;

    public BotDuelArenaStartTask(BotPlayer botPlayer, int n, BotPlayer botPlayer2) {
        super(2);
        this.bot = botPlayer2;
    }

    @Override
    public final void execute() {
        BotTaskDefinition botTaskDefinition = BotTaskDefinition.getDuelArenaCombatTask();
        if (botTaskDefinition == null) {
            this.stop();
            return;
        }
        if (this.bot.currentBotTask != null) {
            this.bot.currentBotTask.assignedBotPlayers.remove(this.bot);
        }
        botTaskDefinition.assignedBotPlayers.add(this.bot);
        this.bot.botMode = 7;
        this.bot.deferredBotTask = null;
        this.bot.botTaskReturnToBankRequested = false;
        this.bot.botEnabled = true;
        this.bot.currentBotTask = botTaskDefinition;
        this.bot.currentBotTaskTypeId = botTaskDefinition.getTaskTypeId();
        this.bot.currentBotTaskIndex = botTaskDefinition.getTaskIndexForType(this.bot.currentBotTaskTypeId);
        this.bot.botTaskStartTimeMillis = System.currentTimeMillis();
        this.bot.botTaskSavedElapsedMillis = 0L;
        this.bot.botTaskDurationMinutes = 120;
        this.bot.currentBotTask.startTask(this.bot);
        this.stop();
    }
}
