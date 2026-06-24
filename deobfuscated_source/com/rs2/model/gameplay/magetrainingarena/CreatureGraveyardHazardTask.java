/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.magetrainingarena;

import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class CreatureGraveyardHazardTask
extends TickTask {
    public CreatureGraveyardHazardTask(int n) {
        super(20);
    }

    @Override
    public final void execute() {
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null && player.getCreatureGraveyardController().isInsideGraveyard()) {
                int n3 = 0;
                while (n3 < 20) {
                    Player player2 = player;
                    player2.packetSender.sendStillGraphic(520, new Position(3350 + GameUtil.randomInclusive(40), 9610 + GameUtil.randomInclusive(50)), 0);
                    ++n3;
                }
                if (GameUtil.randomInclusive(2) == 0) {
                    player.applyDirectHit(2, HitType.NORMAL);
                }
            }
            ++n2;
        }
    }
}

