/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Entity;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.WitchsHouseGardenEjectEvent;
import com.rs2.model.quest.impl.WitchsHouseQuest;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class WitchsHouseGardenTrespassTask
extends TickTask {
    private /* synthetic */ WitchsHouseQuest a;
    private final /* synthetic */ Player player;

    public WitchsHouseGardenTrespassTask(WitchsHouseQuest witchsHouseQuest, int n, Player player) {
        super(1);
        this.a = witchsHouseQuest;
        this.player = player;
    }

    @Override
    public final void execute() {
        if (!this.player.isRegistered()) {
            this.stop();
            return;
        }
        if (this.a.gardenTrespassArea.contains(this.player.getPosition())) {
            Object object = Npc.findByDefinitionId(896);
            if (GameUtil.isNpcWaypointFacingPlayer(this.player, (Npc)object)) {
                ((Entity)object).getUpdateState().setForcedText("Get out of my property!");
                Player player = this.player;
                object = this.a;
                player.getUpdateState().setGraphicHeight100(110);
                CycleEventHandler.getInstance().schedule(player, new WitchsHouseGardenEjectEvent((WitchsHouseQuest)object, player), 1);
                return;
            }
        } else {
            this.player.eq = -1;
            this.stop();
        }
    }
}

