/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.agility;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AgilityQueuedMovementFinishTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int experience;
    private final /* synthetic */ boolean unlockPlayer;
    private final /* synthetic */ int destinationX;
    private final /* synthetic */ int destinationY;
    private final /* synthetic */ int destinationPlane;

    public AgilityQueuedMovementFinishTask(Player player, int n, boolean bl, int n2, int n3, int n4) {
        this.player = player;
        this.experience = n;
        this.unlockPlayer = bl;
        this.destinationX = n2;
        this.destinationY = n3;
        this.destinationPlane = n4;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.player.getSkillManager().addExperience(16, this.experience);
        Player player = this.player;
        player.packetSender.sendGameMessage("...You make it safely to the other side.");
        this.player.getUpdateState().setForcedMovementUpdateRequired(false);
        if (this.unlockPlayer) {
            this.player.setActionLocked(false);
        }
        this.player.forcedMovementActive = false;
        this.player.moveTo(new Position(this.destinationX, this.destinationY, this.destinationPlane));
        this.player.getUpdateState().clearForcedMovement();
        this.player.setStandAnimationOverride(-1);
        this.player.setRunAnimationOverride(-1);
        this.player.setWalkAnimationOverride(-1);
        this.player.setAppearanceUpdateRequired(true);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

