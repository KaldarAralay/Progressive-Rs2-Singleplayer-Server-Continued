/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet.handler;

import com.rs2.model.EntityTargetMovement;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.handler.PlayerInteractionPacketHandler;

public final class DuelRequestTask
extends TickTask {
    private final /* synthetic */ Player targetPlayer;
    private final /* synthetic */ Player requestingPlayer;
    private final /* synthetic */ int actionSequence;

    public DuelRequestTask(PlayerInteractionPacketHandler playerInteractionPacketHandler, int n, Player player, Player player2, int n2) {
        super(1);
        this.targetPlayer = player;
        this.requestingPlayer = player2;
        this.actionSequence = n2;
    }

    @Override
    public final void execute() {
        if (!this.isValidDuelRequestTask()) {
            this.clearRequestState();
            return;
        }
        if (this.requestingPlayer.isWithinReach(this.targetPlayer, 1) && !this.requestingPlayer.isOverlapping(this.targetPlayer) && !EntityTargetMovement.isDiagonalTo(this.requestingPlayer.getPosition(), this.targetPlayer.getPosition())) {
            if (!this.canCompleteDuelRequest()) {
                this.clearRequestState();
                return;
            }
            if (this.requestingPlayer.isInDuelArenaLobby()) {
                this.requestingPlayer.getDuelController().handleDuelRequest(this.targetPlayer);
                this.requestingPlayer.getUpdateState().setFaceEntity(-1);
            }
            EntityTargetMovement.clearMovementTarget(this.requestingPlayer);
            this.requestingPlayer.getUpdateState().setFacePosition(this.targetPlayer.getPosition());
            this.requestingPlayer.setInteractionTarget(null);
            this.requestingPlayer.getMovementQueue().clear();
            this.stop();
        }
    }

    private boolean isValidDuelRequestTask() {
        return this.requestingPlayer != null && this.targetPlayer != null && !this.requestingPlayer.isDead() && !this.targetPlayer.isDead() && this.requestingPlayer.isRegistered() && this.targetPlayer.isRegistered() && this.requestingPlayer.isCurrentActionSequence(this.actionSequence);
    }

    private boolean canCompleteDuelRequest() {
        if (!this.requestingPlayer.isInDuelArenaLobby() || !this.targetPlayer.isInDuelArenaLobby() || this.requestingPlayer.isInDuelArena() || this.targetPlayer.isInDuelArena()) {
            return false;
        }
        if (this.requestingPlayer.getOpenInterfaceId() > 0 || this.targetPlayer.getOpenInterfaceId() > 0) {
            return false;
        }
        if (this.requestingPlayer.getDuelSession().getOpponent() != null || this.targetPlayer.getDuelSession().getOpponent() != null) {
            return false;
        }
        return !this.requestingPlayer.getDuelSession().isStarted() && !this.targetPlayer.getDuelSession().isStarted();
    }

    private void clearRequestState() {
        if (this.requestingPlayer != null) {
            EntityTargetMovement.clearMovementTarget(this.requestingPlayer);
            this.requestingPlayer.setInteractionTarget(null);
            this.requestingPlayer.getMovementQueue().clear();
        }
        this.stop();
    }
}

