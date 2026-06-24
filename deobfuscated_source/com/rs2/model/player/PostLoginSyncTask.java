/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.GameplayHelper;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class PostLoginSyncTask
extends TickTask {
    private /* synthetic */ Player a;
    private final /* synthetic */ Player b;

    public PostLoginSyncTask(Player player, int n, Player player2) {
        super(3);
        this.a = player;
        this.b = player2;
    }

    @Override
    public final void execute() {
        this.stop();
        this.a.getAttributes().put("canPickup", Boolean.TRUE);
        this.a.getSocialManager().refreshFriendStatuses(false);
        GameplayHelper.refreshPlayerAreaOverlay(this.b);
        Player player = this.a;
        player.packetSender.sendRunEnergy();
    }
}

