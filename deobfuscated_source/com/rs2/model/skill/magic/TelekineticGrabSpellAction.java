/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.Position;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.path.PathResult;
import com.rs2.model.path.PathStep;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.util.GameUtil;

public final class TelekineticGrabSpellAction
extends MagicSpellAction {
    private final /* synthetic */ GroundItem groundItem;
    private final /* synthetic */ SpellDefinition telegrabSpell;
    private final /* synthetic */ Player caster;
    private final /* synthetic */ Position targetPosition;
    private final /* synthetic */ int itemId;

    public TelekineticGrabSpellAction(Player player, SpellDefinition spellDefinition, GroundItem groundItem, SpellDefinition spellDefinition2, Player player2, Position position, int n) {
        super(player, spellDefinition, (byte)0);
        this.groundItem = groundItem;
        this.telegrabSpell = spellDefinition2;
        this.caster = player2;
        this.targetPosition = position;
        this.itemId = n;
    }

    @Override
    public final boolean prepareCast() {
        if (this.groundItem == null) {
            return false;
        }
        switch (this.telegrabSpell) {
            case TELEKINETIC_GRAB: {
                this.caster.getUpdateState().setFacePosition(this.targetPosition);
                this.caster.getMovementQueue().clear();
                this.scheduleDelayedImpact(null, this.targetPosition);
            }
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public final void applyImpact(HitDefinition hitDefinition) {
        switch (this.telegrabSpell) {
            case TELEKINETIC_GRAB: {
                Position position;
                GroundItemManager.getInstance();
                if (!GroundItemManager.isVisible(this.caster, this.groundItem)) {
                    hitDefinition.setGraphic(null);
                    return;
                }
                if (!GroundItemManager.getInstance().removeForPickup(this.groundItem, this.caster)) {
                    this.caster.packetSender.sendGameMessage("That item does not seem to exist anymore.");
                    this.caster.packetSender.sendGroundItemRemove(this.groundItem);
                    if (!this.caster.botEnabled) return;
                    TelekineticGrabSpellAction.continueBotGroundItemLoot(this.caster, this.groundItem, false);
                    return;
                }
                if (!this.caster.getTelekineticTheatreController().isInsideTheatre()) {
                    this.caster.getInventoryManager().addItem(new ItemStack(this.groundItem.getItem().getId(), this.groundItem.getItem().getAmount(), this.groundItem.getItem().getMetadata()));
                    if (!this.caster.botEnabled) return;
                    TelekineticGrabSpellAction.continueBotGroundItemLoot(this.caster, this.groundItem, true);
                    return;
                }
                if (this.itemId != 6888) return;
                String mazeSide = this.caster.getTelekineticTheatreController().getPlayerMazeSide(this.caster.getTelekineticTheatreController().mazeIndex);
                int n = 0;
                int n2 = 0;
                if (mazeSide == "right") {
                    n += 20;
                } else if (mazeSide == "left") {
                    n -= 20;
                } else if (mazeSide == "bottom") {
                    n2 -= 20;
                } else if (mazeSide == "upper") {
                    n2 += 20;
                }
                boolean bl = true;
                Position position2 = new Position(this.targetPosition.getX() + n, this.targetPosition.getY() + n2, this.targetPosition.getPlane());
                Object object2 = this.targetPosition;
                PathResult pathResult = new PathResult();
                Position position3 = ((Position)object2).copy();
                Position position4 = position3.copy();
                int n3 = position2.getX() - position3.getX();
                int n4 = position2.getY() - position3.getY();
                int n5 = 0;
                int n6 = 0;
                int n7 = 0;
                int n8 = 0;
                if (n3 < 0) {
                    n5 = -1;
                } else if (n3 > 0) {
                    n5 = 1;
                }
                if (n4 < 0) {
                    n6 = -1;
                } else if (n4 > 0) {
                    n6 = 1;
                }
                if (n3 < 0) {
                    n7 = -1;
                } else if (n3 > 0) {
                    n7 = 1;
                }
                int n9 = Math.abs(n3);
                int n10 = Math.abs(n4);
                if (n9 <= n10) {
                    n9 = Math.abs(n4);
                    n10 = Math.abs(n3);
                    if (n4 < 0) {
                        n8 = -1;
                    } else if (n4 > 0) {
                        n8 = 1;
                    }
                    n7 = 0;
                }
                n4 = n9 >> 1;
                n3 = 0;
                while (true) {
                    if (n3 > n9) {
                        n3 = ((PathStep)pathResult.getSteps().getLast()).getX();
                        n4 = ((PathStep)pathResult.getSteps().getLast()).getY();
                        position = new Position(n3, n4, ((Position)object2).getPlane());
                        break;
                    }
                    n4 += n10;
                    if (!position4.equals(position3) && !GameUtil.hasClearPath(position3, position4, true)) {
                        n4 = ((PathStep)pathResult.getSteps().getLast()).getX();
                        int n11 = ((PathStep)pathResult.getSteps().getLast()).getY();
                        position = new Position(n4, n11, ((Position)object2).getPlane());
                        break;
                    }
                    position4 = position3.copy();
                    pathResult.getSteps().add(new PathStep(position3.getX(), position3.getY()));
                    if (n4 >= n9) {
                        n4 -= n9;
                        position3.setX(position3.getX() + n5);
                        position3.setY(position3.getY() + n6);
                    } else {
                        position3.setX(position3.getX() + n7);
                        position3.setY(position3.getY() + n8);
                    }
                    ++n3;
                }
                object2 = position;
                if (!this.caster.getTelekineticTheatreController().isMazeTargetPosition((Position)object2)) {
                    object2 = new GroundItem(new ItemStack(6888, 1), this.caster, (Position)object2);
                    GroundItemManager.getInstance().spawn((GroundItem)object2);
                    return;
                }
                this.caster.getTelekineticTheatreController().completeMaze();
                return;
            }
        }
    }
}

