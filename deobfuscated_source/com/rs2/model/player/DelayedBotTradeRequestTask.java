/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.handler.PlayerInteractionPacketHandler;

public final class DelayedBotTradeRequestTask
extends TickTask {
    private final /* synthetic */ Player bot;
    private final /* synthetic */ String triggerMessage;
    private final /* synthetic */ Player targetPlayer;

    public DelayedBotTradeRequestTask(Player player, int n, Player player2, String string, Player player3) {
        super(n);
        this.bot = player2;
        this.triggerMessage = string;
        this.targetPlayer = player3;
    }

    @Override
    public final void execute() {
        if (this.bot.isDead() || !this.bot.isRegistered()) {
            this.stop();
            return;
        }
        if (this.triggerMessage.contains("buy") && this.bot.tradeAdvertMode == 0) {
            PlayerInteractionPacketHandler.dispatchDeferredTradeRequest(this.bot, this.targetPlayer);
        } else if (this.triggerMessage.contains("sell") && this.bot.tradeAdvertMode == 1) {
            PlayerInteractionPacketHandler.dispatchDeferredTradeRequest(this.bot, this.targetPlayer);
        }
        this.stop();
    }
}

