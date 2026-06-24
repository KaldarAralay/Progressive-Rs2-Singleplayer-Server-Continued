/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.FluffsKittenReunionFinishTask;
import com.rs2.model.quest.impl.GertrudesCatQuest;
import com.rs2.model.task.TickTask;

public final class FluffsKittenReunionStartTask
extends TickTask {
    private TickTask a;
    private final /* synthetic */ Npc b;
    private final /* synthetic */ Npc c;

    public FluffsKittenReunionStartTask(GertrudesCatQuest gertrudesCatQuest, int n, Npc npc, Npc npc2, Player player) {
        super(2);
        this.b = npc;
        this.c = npc2;
        this.a = new FluffsKittenReunionFinishTask(this, 3, npc2, player, npc);
    }

    @Override
    public final void execute() {
        this.b.queueScriptedPath(new Position[]{new Position(3309, 3509, 1)});
        this.c.setAttackRange(1);
        this.c.setMovementTarget(this.c);
        World.getTaskScheduler().schedule(this.a);
        this.stop();
    }
}

