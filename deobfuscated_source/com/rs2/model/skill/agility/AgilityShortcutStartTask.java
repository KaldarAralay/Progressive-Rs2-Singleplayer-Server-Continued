/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.agility;

import com.rs2.model.player.Player;
import com.rs2.model.skill.agility.AgilityObstacleHandler;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AgilityShortcutStartTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int deltaX;
    private final /* synthetic */ int deltaY;
    private final /* synthetic */ int forcedMovementEndDelay;
    private final /* synthetic */ int completionDelay;
    private final /* synthetic */ int experience;

    public AgilityShortcutStartTask(Player player, int n, int n2, int n3, int n4, int n5) {
        this.player = player;
        this.deltaX = n;
        this.deltaY = n2;
        this.forcedMovementEndDelay = n3;
        this.completionDelay = n4;
        this.experience = n5;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        Player player = this.player;
        player.packetSender.sendObjectAnimation(this.player.getInteractionTargetX(), this.player.getInteractionTargetY(), 3, 127);
        AgilityObstacleHandler.startForcedMovement(this.player, this.deltaX, this.deltaY, 1, this.forcedMovementEndDelay, this.completionDelay, true, this.experience, 0);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

