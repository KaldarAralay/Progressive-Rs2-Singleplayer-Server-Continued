/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.combat.hit.HitType;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class ApeAtollDungeonHazardDamageTask
extends TickTask {
    private /* synthetic */ MonkeyMadnessQuest a;
    private final /* synthetic */ Player player;

    public ApeAtollDungeonHazardDamageTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player) {
        super(25);
        this.a = monkeyMadnessQuest;
        this.player = player;
    }

    @Override
    public final void execute() {
        if (!this.player.isRegistered()) {
            this.stop();
            return;
        }
        if (this.a.a.containsExclusive(this.player.getPosition()) && !this.a.b.containsExclusive(this.player.getPosition())) {
            this.player.getUpdateState().setGraphic(60, 0);
            this.player.applyDirectHit(GameUtil.randomInclusive(2), HitType.NORMAL);
            return;
        }
        this.player.activeEnvironmentalHazardId = -1;
        this.stop();
    }
}

