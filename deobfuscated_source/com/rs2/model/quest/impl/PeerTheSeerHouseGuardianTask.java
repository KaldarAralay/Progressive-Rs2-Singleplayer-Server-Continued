/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.GameplayHelper;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.FremennikTrialsQuest;
import com.rs2.model.task.TickTask;

public final class PeerTheSeerHouseGuardianTask
extends TickTask {
    private final /* synthetic */ Player player;

    public PeerTheSeerHouseGuardianTask(FremennikTrialsQuest fremennikTrialsQuest, int n, Player player) {
        super(10);
        this.player = player;
    }

    @Override
    public final void execute() {
        if (!this.player.isRegistered()) {
            this.stop();
            return;
        }
        boolean bl = false;
        int n = 0;
        while (n < FremennikTrialsQuest.peerHouseGuardianAreas.length) {
            if (FremennikTrialsQuest.peerHouseGuardianAreas[n].containsExclusive(this.player.getPosition())) {
                bl = true;
                if (!GameplayHelper.hasActiveTemporaryNpc(this.player, 1290)) {
                    Npc npc = new Npc(1290);
                    GameplayHelper.spawnOwnedNpcAdjacentToPlayer(this.player, npc, true, true);
                    npc.getUpdateState().setForcedText("Prepare to die, " + this.player.getUsername() + "!");
                }
                this.stop();
            }
            ++n;
        }
        if (!bl) {
            this.player.eq = -1;
            this.stop();
        }
    }
}

