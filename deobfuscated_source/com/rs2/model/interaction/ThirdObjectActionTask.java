/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.model.Position;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.interaction.InteractionDispatcher;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class ThirdObjectActionTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectPlane;

    public ThirdObjectActionTask(int n, boolean bl, Player player, int n2, int n3, int n4, int n5, int n6) {
        super(1, true);
        this.player = player;
        this.actionSequence = n2;
        this.objectId = n3;
        this.objectX = n4;
        this.objectY = n5;
        this.objectPlane = n6;
    }

    @Override
    public final void execute() {
        if (this.player == null || !this.player.isCurrentActionSequence(this.actionSequence)) {
            this.stop();
            return;
        }
        if (this.player.isMoving() || this.player.isStunned()) {
            return;
        }
        Object object = SkillActionHelper.findWorldObjectById(this.objectId, this.objectX, this.objectY, this.objectPlane);
        if (object == null) {
            return;
        }
        Object object2 = ObjectDefinition.forId(this.player.getInteractionTargetId());
        Position position = GameUtil.findReachableInteractionPosition(((WorldObject)object).getPosition().getX(), ((WorldObject)object).getPosition().getY(), this.player.getPosition().getX(), this.player.getPosition().getY(), ((ObjectDefinition)object2).getWidthForOrientation(((WorldObject)object).getOrientation()), ((ObjectDefinition)object2).getLengthForOrientation(((WorldObject)object).getOrientation()), this.objectPlane);
        if (position == null) {
            return;
        }
        if (!InteractionDispatcher.canReachObjectInteraction(this.player, position, (WorldObject)object)) {
            this.stop();
            return;
        }
        position = new Position(this.player.getInteractionTargetX(), this.player.getInteractionTargetY(), this.objectPlane);
        if (object2 != null) {
            this.player.getUpdateState().setFacePosition(position.centerForSize(((ObjectDefinition)object2).getMaxDimension()));
        }
        switch (this.player.getInteractionTargetId()) {
            case 3194: {
                ObjectManager.getInstance();
                object2 = ObjectManager.findDynamicObjectByIdAt(this.objectId, this.objectX, this.objectY, this.objectPlane);
                if (object2 == null) break;
                this.player.getUpdateState().setAnimation(832);
                ObjectManager.getInstance().removeDynamicObjectAt(this.objectX, this.objectY, this.objectPlane, ((WorldObject)object).getType());
                break;
            }
            case 10177: {
                AttackStyleDefinition.startDelayedObjectMove(this.player, new Position(1798, 4407, 3));
                break;
            }
            case 1739: 
            case 4569: 
            case 12537: {
                AttackStyleDefinition.climbOffsetLadder(this.player, "down");
                break;
            }
            case 1748: 
            case 2884: 
            case 8745: 
            case 12965: {
                AttackStyleDefinition.climbOneFloorAtCurrentTile(this.player, "down");
                break;
            }
            case 4187: {
                AttackStyleDefinition.climbOneFloorAtCurrentTile(this.player, "up");
                break;
            }
            default: {
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("Nothing interesting happens.");
            }
        }
        this.stop();
    }
}

