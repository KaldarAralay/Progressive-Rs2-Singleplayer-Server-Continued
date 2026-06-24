/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.model.EntityTargetMovement;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.BankManager;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class FourthNpcActionTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ Npc npc;

    public FourthNpcActionTask(int n, boolean bl, Player player, int n2, Npc npc) {
        super(1, true);
        this.player = player;
        this.actionSequence = n2;
        this.npc = npc;
    }

    @Override
    public final void execute() {
        if (this.player == null || !this.player.isCurrentActionSequence(this.actionSequence) || this.npc.isDead()) {
            this.stop();
            return;
        }
        if (!this.player.isWithinReach(this.npc, 1) || this.player.isOverlapping(this.npc)) {
            return;
        }
        if (!GameUtil.hasClearPath(this.player.getPosition(), this.npc.getPosition(), true)) {
            return;
        }
        EntityTargetMovement.clearMovementTarget(this.player);
        this.player.getUpdateState().setFaceEntity(this.npc.getEncodedIndex());
        this.npc.getUpdateState().setFaceEntity(this.player.getEncodedIndex());
        switch (this.player.getInteractionTargetId()) {
            case 494: {
                BankManager.openBank(this.player);
            }
        }
        this.stop();
    }
}

