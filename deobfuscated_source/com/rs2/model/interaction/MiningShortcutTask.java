/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.ServerSettings;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class MiningShortcutTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectId;
    private final /* synthetic */ GatheringToolDefinition gatheringTool;

    public MiningShortcutTask(Player player, int n, int n2, int n3, int n4, GatheringToolDefinition gatheringToolDefinition) {
        this.player = player;
        this.actionSequence = n;
        this.objectX = n2;
        this.objectY = n3;
        this.objectId = n4;
        this.gatheringTool = gatheringToolDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        ObjectManager.getInstance();
        Object object = ObjectManager.findDynamicObjectAt(this.objectX, this.objectY, this.player.getPosition().getPlane());
        if (object != null && ((DynamicObject)object).getWorldObject().getObjectId() != this.objectId) {
            cycleEventContainer.stop();
            return;
        }
        cycleEventContainer.setTickDelay(3);
        this.player.getUpdateState().setAnimation(this.gatheringTool.getGatherAnimationId());
        object = this.player;
        ((Player)object).packetSender.sendSoundEffect(432, 1, 0);
        if (this.player.temporaryActionValue == 1) {
            int n = SkillActionHelper.getObjectOrientation(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane());
            int n2 = SkillActionHelper.getObjectType(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane());
            new DynamicObject(ServerSettings.placeholderObjectId, this.objectX, this.objectY, this.player.getPosition().getPlane(), n, n2 == 11 ? 11 : 10, this.objectId, 1);
            Player player = this.player;
            player.packetSender.queueRelativeMovementStep(this.player.getPosition().getX() < 2840 ? 3 : -3, 0, true);
            cycleEventContainer.stop();
        }
        ++this.player.temporaryActionValue;
    }

    @Override
    public final void onStop() {
        this.player.getUpdateState().setAnimation(-1);
    }
}

