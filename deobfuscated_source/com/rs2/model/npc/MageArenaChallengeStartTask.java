/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc;

import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class MageArenaChallengeStartTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ Npc challengeNpc;

    public MageArenaChallengeStartTask(Npc npc, int n, Player player, Npc npc2) {
        super(12);
        this.player = player;
        this.challengeNpc = npc2;
    }

    @Override
    public final void execute() {
        GameplayHelper.spawnOwnedNpcAtPosition(this.player, new Position(3106, 3934, 0), this.challengeNpc, true, false);
        this.challengeNpc.getUpdateState().setGraphic(86, 0);
        if (this.player.mageArenaProgressStage == 0) {
            this.challengeNpc.getUpdateState().setForcedText("You must prove yourself... now!");
        } else {
            this.challengeNpc.getUpdateState().setForcedText("Let us continue with our battle.");
        }
        this.stop();
    }
}

