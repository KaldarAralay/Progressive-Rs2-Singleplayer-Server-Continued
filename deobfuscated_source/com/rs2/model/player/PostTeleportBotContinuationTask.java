/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.bot.ClanWarsBotManager;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.combat.BotCombatLoadoutManager;
import com.rs2.model.GameplayHelper;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class PostTeleportBotContinuationTask
extends CycleEvent {
    private /* synthetic */ Player player;
    private final /* synthetic */ boolean wasActionLocked;
    private final /* synthetic */ Player continuationPlayer;

    public PostTeleportBotContinuationTask(Player player, boolean bl, Player player2) {
        this.player = player;
        this.wasActionLocked = bl;
        this.continuationPlayer = player2;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.wasActionLocked) {
            this.player.setActionLocked(false);
        }
        this.player.getMovementQueue().clear();
        if (!this.continuationPlayer.botLumbridgeResetPending) {
            if (this.continuationPlayer.isBot && this.continuationPlayer.botCombatState != null && (this.continuationPlayer.botCombatState.startsWith("escape") || this.continuationPlayer.botCombatState.equals("tele") || this.continuationPlayer.botCombatState.equals("died"))) {
                if (!this.continuationPlayer.botCombatState.equals("died") && !this.continuationPlayer.clanWarsBot) {
                    BotCombatHelper.sellBotLootItems(this.continuationPlayer);
                }
                if (this.continuationPlayer.clanWarsBot) {
                    ClanWarsBotManager.hideClanWarsBot(this.continuationPlayer);
                } else if (this.continuationPlayer.botMode == 4) {
                    GameplayHelper.selectAndStartNextProgressiveBotTask(this.continuationPlayer);
                } else {
                    BotCombatLoadoutManager.startCombatLoadoutBot(this.continuationPlayer);
                }
            }
            if (this.continuationPlayer.currentBotTask != null && this.continuationPlayer.botCombatState != null && (this.continuationPlayer.botCombatState.equals("died") || this.continuationPlayer.botCombatState.startsWith("escape") || this.continuationPlayer.botCombatState.equals("tele"))) {
                if (this.continuationPlayer.botMode != 4) {
                    GameplayHelper.startNextBotTask(this.continuationPlayer);
                } else {
                    GameplayHelper.selectAndStartNextProgressiveBotTask(this.continuationPlayer);
                }
            }
            if (this.continuationPlayer.currentBotRoute != null) {
                this.continuationPlayer.botRouteActionPending = false;
                if (!this.continuationPlayer.botRouteTravelPending) {
                    this.continuationPlayer.continueBotRoute();
                }
            }
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

