/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.player.BankManager;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class DelayedBankOpenTask
extends TickTask {
    private final /* synthetic */ Player player;

    public DelayedBankOpenTask(int n, Player player) {
        super(n);
        this.player = player;
    }

    @Override
    public final void execute() {
        BankManager.openBank(this.player, this.player);
        this.stop();
    }
}

