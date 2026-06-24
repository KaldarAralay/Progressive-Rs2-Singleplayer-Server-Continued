/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.agility;

import com.rs2.model.player.Player;
import com.rs2.model.skill.agility.AgilityQueuedMovementFinishTask;
import com.rs2.model.skill.agility.AgilityQueuedMovementStepTask;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.model.task.CycleEventHandler;

public final class AgilityQueuedMovementTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int deltaX;
    private final /* synthetic */ int deltaY;
    private final /* synthetic */ int forcedMovementEndDelay;
    private final /* synthetic */ int completionDelay;
    private final /* synthetic */ int experience;
    private final /* synthetic */ int animationId;

    public AgilityQueuedMovementTask(Player player, int n, int n2, int n3, int n4, int n5, int n6) {
        this.player = player;
        this.deltaX = n;
        this.deltaY = n2;
        this.forcedMovementEndDelay = n3;
        this.completionDelay = n4;
        this.experience = n5;
        this.animationId = n6;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        Player player = this.player;
        player.packetSender.sendObjectAnimation(this.player.getInteractionTargetX(), this.player.getInteractionTargetY(), 3, 127);
        int n = this.animationId;
        int n2 = this.experience;
        boolean bl = true;
        int n3 = this.completionDelay;
        int n4 = this.forcedMovementEndDelay;
        bl = true;
        int n5 = this.deltaY;
        int n6 = this.deltaX;
        Player player2 = this.player;
        player2.setActionLocked(true);
        player2.getMovementQueue().clear();
        if (n > 0) {
            player2.setStandAnimationOverride(n);
            player2.setRunAnimationOverride(n);
            player2.setWalkAnimationOverride(n);
            player2.setAppearanceUpdateRequired(true);
        }
        n = 2;
        if (n6 > 0) {
            n = 1;
        } else if (n6 < 0) {
            n = 3;
        } else if (n5 > 0) {
            n = 0;
        }
        int n7 = player2.getPosition().getX() + n6;
        int n8 = player2.getPosition().getY() + n5;
        int n9 = player2.getPosition().getPlane();
        player2.forcedMovementActive = true;
        CycleEventHandler.getInstance().schedule(player2, new AgilityQueuedMovementStepTask(player2, n6, n5, 1, n4, n), 1);
        CycleEventHandler.getInstance().schedule(player2, new AgilityQueuedMovementFinishTask(player2, n2, true, n7, n8, n9), n3 + 1);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

