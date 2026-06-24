/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.Position;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class TelekineticGrabTask
extends CycleEvent {
    private final /* synthetic */ Player caster;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int itemId;
    private final /* synthetic */ Position targetPosition;
    private final /* synthetic */ SpellDefinition telegrabSpell;

    public TelekineticGrabTask(Player player, int n, int n2, Position position, SpellDefinition spellDefinition) {
        this.caster = player;
        this.actionSequence = n;
        this.itemId = n2;
        this.targetPosition = position;
        this.telegrabSpell = spellDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.caster.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        GroundItemManager.getInstance();
        Object object = GroundItemManager.findVisibleItem(this.caster, this.itemId, this.targetPosition);
        if (object == null) {
            return;
        }
        if (this.caster.gameMode != 0) {
            if (!((GroundItem)object).isRespawning() && ((GroundItem)object).getOwner() == null) {
                object = this.caster;
                ((Player)object).packetSender.sendGameMessage("You are not playing on normal gamemode and cant pick that up.");
                cycleEventContainer.stop();
                return;
            }
            if (!((GroundItem)object).isRespawning() && ((GroundItem)object).getOwner().resolve() != this.caster) {
                object = this.caster;
                ((Player)object).packetSender.sendGameMessage("You are not playing on normal gamemode and cant pick that up.");
                cycleEventContainer.stop();
                return;
            }
        }
        switch (this.telegrabSpell) {
            case TELEKINETIC_GRAB: {
                if (this.itemId == 1583) {
                    object = this.caster;
                    ((Player)object).packetSender.sendGameMessage("I can't use Telekinetic Grab on this object.");
                    cycleEventContainer.stop();
                    return;
                }
                if (this.itemId == 1419) {
                    if (this.caster.ownsItem(this.itemId)) {
                        object = this.caster;
                        ((Player)object).packetSender.sendGameMessage("You already have a scythe, you don't need another one.");
                        cycleEventContainer.stop();
                        return;
                    }
                    this.caster.questHookStates[3] = 1;
                }
                if (this.itemId >= 5509 && this.itemId <= 5515 && this.caster.ownsItem(this.itemId)) {
                    object = this.caster;
                    ((Player)object).packetSender.sendGameMessage("I already have that pouch!");
                    cycleEventContainer.stop();
                    return;
                }
                if (!(this.caster.getTelekineticTheatreController().isInsideTheatre() || GameUtil.hasClearPath(this.caster.getPosition(), this.targetPosition, false) && GameUtil.isWithinDistance(this.caster.getPosition(), this.targetPosition, 10))) {
                    return;
                }
                if (!this.caster.getPosition().equals(this.targetPosition)) break;
                this.caster.getTargetMovement().moveAwayFromOverlap();
                return;
            }
            default: {
                return;
            }
        }
        MagicSpellAction.castTelekineticGrab(this.caster, this.telegrabSpell, this.itemId, this.targetPosition);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

