/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.slayer;

import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.npc.Npc;
import com.rs2.model.skill.slayer.SlayerManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class ZygomiteSpawnTask
extends CycleEvent {
    private /* synthetic */ SlayerManager slayerManager;
    private final /* synthetic */ Npc sourceNpc;
    private final /* synthetic */ int spawnX;
    private final /* synthetic */ int spawnY;

    public ZygomiteSpawnTask(SlayerManager slayerManager, Npc npc, int n, int n2) {
        this.slayerManager = slayerManager;
        this.sourceNpc = npc;
        this.spawnX = n;
        this.spawnY = n2;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.sourceNpc.setActive(false);
        GameplayHelper.spawnNpcTargetingEntityAtPosition(SlayerManager.getPlayer(this.slayerManager), this.sourceNpc.getDefinition().getId() == 3344 ? SlayerManager.ZYGOMITE_SPAWN_A : SlayerManager.ZYGOMITE_SPAWN_B, new Position(this.spawnX, this.spawnY), false, null);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

