/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.model.GameplayHelper;
import com.rs2.model.npc.Npc;
import com.rs2.model.quest.event.ServerMaintenanceEventHook;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.path.WalkingCollisionMap;

public final class ClueMerchantSpawnTask
extends TickTask {
    private /* synthetic */ ServerMaintenanceEventHook a;

    public ClueMerchantSpawnTask(ServerMaintenanceEventHook serverMaintenanceEventHook, int n) {
        super(1500);
        this.a = serverMaintenanceEventHook;
    }

    @Override
    public final void execute() {
        int n = 0;
        while (n < this.a.a) {
            int n2 = 2945 + GameUtil.randomInt(350);
            int n3 = 3145 + GameUtil.randomInt(370);
            boolean bl = false;
            if (WalkingCollisionMap.getTileFlags(n2, n3, 0) != 0) {
                bl = true;
            }
            while (bl) {
                n2 = 2945 + GameUtil.randomInt(350);
                bl = WalkingCollisionMap.getTileFlags(n2, n3 = 3145 + GameUtil.randomInt(370), 0) != 0;
            }
            Npc npc = new Npc(3886);
            GameplayHelper.spawnNpcWithRemovalDelay(npc, n2, n3, 0, 1500);
            ++n;
        }
    }
}

