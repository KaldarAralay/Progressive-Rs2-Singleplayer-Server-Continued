/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.model.Position;
import com.rs2.model.interaction.InteractionDispatcher;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class SpellOnObjectTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectPlane;
    private final /* synthetic */ SpellDefinition spell;

    public SpellOnObjectTask(int n, boolean bl, Player player, int n2, int n3, int n4, int n5, int n6, SpellDefinition spellDefinition) {
        super(1, true);
        this.player = player;
        this.actionSequence = n2;
        this.objectId = n3;
        this.objectX = n4;
        this.objectY = n5;
        this.objectPlane = n6;
        this.spell = spellDefinition;
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
        ObjectDefinition objectDefinition = ObjectDefinition.forId(this.player.getInteractionTargetId());
        Position position = GameUtil.findReachableInteractionPosition(((WorldObject)object).getPosition().getX(), ((WorldObject)object).getPosition().getY(), this.player.getPosition().getX(), this.player.getPosition().getY(), objectDefinition.getWidthForOrientation(((WorldObject)object).getOrientation()), objectDefinition.getLengthForOrientation(((WorldObject)object).getOrientation()), this.objectPlane);
        if (this.objectId != 2638) {
            if (position == null) {
                return;
            }
            if (!InteractionDispatcher.canReachObjectInteraction(this.player, position, (WorldObject)object)) {
                this.stop();
                return;
            }
        }
        object = new Position(this.player.getInteractionTargetX(), this.player.getInteractionTargetY(), this.objectPlane);
        if (objectDefinition != null) {
            this.player.getUpdateState().setFacePosition(((Position)object).centerForSize(objectDefinition.getMaxDimension()));
        }
        if (MagicSpellAction.castObjectSpell(this.player, this.spell, this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane())) {
            this.stop();
            return;
        }
        this.stop();
    }
}

