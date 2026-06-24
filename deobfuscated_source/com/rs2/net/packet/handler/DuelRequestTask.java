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
        if (this.targetPlayer == null || this.targetPlayer.isDead() || !this.requestingPlayer.isCurrentActionSequence(this.actionSequence)) {
            EntityTargetMovement.clearMovementTarget(this.requestingPlayer);
            this.requestingPlayer.setInteractionTarget(null);
            this.requestingPlayer.getMovementQueue().clear();
            this.stop();
            return;
        }
        if (this.requestingPlayer.isWithinReach(this.targetPlayer, 1) && !this.requestingPlayer.isOverlapping(this.targetPlayer) && !EntityTargetMovement.isDiagonalTo(this.requestingPlayer.getPosition(), this.targetPlayer.getPosition())) {
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
}

