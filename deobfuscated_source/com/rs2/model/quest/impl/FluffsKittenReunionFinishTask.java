/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.GameplayHelper;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.FluffsKittenReunionStartTask;
import com.rs2.model.task.TickTask;

public final class FluffsKittenReunionFinishTask
extends TickTask {
    private final /* synthetic */ Npc a;
    private final /* synthetic */ Player player;
    private final /* synthetic */ Npc c;

    public FluffsKittenReunionFinishTask(FluffsKittenReunionStartTask fluffsKittenReunionStartTask, int n, Npc npc, Player player, Npc npc2) {
        super(3);
        this.a = npc;
        this.player = player;
        this.c = npc2;
    }

    @Override
    public final void execute() {
        CombatManager.finishDeath(this.a, this.player, false);
        GameplayHelper.unregisterTemporaryNpc(this.c);
        this.player.setActionLocked(false);
        this.player.getDialogueManager().showOneLineStatement("Fluffs has run off home with her offspring.");
        this.stop();
    }
}

