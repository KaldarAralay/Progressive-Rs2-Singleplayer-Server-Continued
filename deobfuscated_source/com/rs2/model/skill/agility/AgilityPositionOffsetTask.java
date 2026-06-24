/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.agility;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class AgilityPositionOffsetTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int deltaX;
    private final /* synthetic */ int deltaY;
    private final /* synthetic */ int deltaPlane;
    private final /* synthetic */ double experience;
    private final /* synthetic */ String completionMessage;

    public AgilityPositionOffsetTask(Player player, int n, int n2, int n3, double d, String string) {
        this.player = player;
        this.deltaX = n;
        this.deltaY = n2;
        this.deltaPlane = n3;
        this.experience = d;
        this.completionMessage = string;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        this.player.moveTo(new Position(this.player.getPosition().getX() + this.deltaX, this.player.getPosition().getY() + this.deltaY, this.player.getPosition().getPlane() + this.deltaPlane));
        if (this.deltaPlane == 0) {
            this.player.getUpdateState().setFacePosition(new Position(this.player.getPosition().getX() - this.deltaX, this.player.getPosition().getY() - this.deltaY, this.player.getPosition().getPlane() - this.deltaPlane));
        }
        this.player.getSkillManager().addExperience(16, this.experience);
        Player player = this.player;
        player.packetSender.sendGameMessage(this.completionMessage);
        this.player.setActionLocked(false);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

