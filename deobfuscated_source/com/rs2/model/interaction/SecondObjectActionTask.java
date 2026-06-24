/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.gameplay.godwars.GodWarsDungeonManager;
import com.rs2.model.gameplay.partyroom.PartyRoomManager;
import com.rs2.model.interaction.InteractionDispatcher;
import com.rs2.model.interaction.MiningShortcutTask;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.objects.functions.PickableObjectHandler;
import com.rs2.model.player.BankManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.crafting.CraftingHandler;
import com.rs2.model.skill.smithing.SmeltingHandler;
import com.rs2.model.skill.thieving.StallThievingHandler;
import com.rs2.model.skill.thieving.ThievingObjectHandler;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class SecondObjectActionTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectPlane;

    public SecondObjectActionTask(int n, boolean bl, Player player, int n2, int n3, int n4, int n5, int n6) {
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
        WorldObject worldObject = SkillActionHelper.findWorldObjectById(this.objectId, this.objectX, this.objectY, this.objectPlane);
        if (worldObject == null) {
            return;
        }
        Object object = ObjectDefinition.forId(this.player.getInteractionTargetId());
        Position position = GameUtil.findReachableInteractionPosition(worldObject.getPosition().getX(), worldObject.getPosition().getY(), this.player.getPosition().getX(), this.player.getPosition().getY(), ((ObjectDefinition)object).getWidthForOrientation(worldObject.getOrientation()), ((ObjectDefinition)object).getLengthForOrientation(worldObject.getOrientation()), this.objectPlane);
        if (position == null) {
            return;
        }
        if (!InteractionDispatcher.canReachObjectInteraction(this.player, position, worldObject)) {
            this.stop();
            return;
        }
        position = new Position(this.player.getInteractionTargetX(), this.player.getInteractionTargetY(), this.objectPlane);
        if (object != null) {
            this.player.getUpdateState().setFacePosition(position.centerForSize(((ObjectDefinition)object).getMaxDimension()));
        }
        if (this.player.getQuestManager().handleSecondObjectAction(this.objectId, this.objectX, this.objectY)) {
            this.stop();
            return;
        }
        if (DialogueManager.startContextDialogue(2, this.player, this.player.getInteractionTargetId(), this.objectX, this.objectY)) {
            this.stop();
            return;
        }
        if (ThievingObjectHandler.handleThievingObject(this.player, this.objectId, this.objectX, this.objectY)) {
            this.stop();
            return;
        }
        if (ServerSettings.content2007Enabled) {
            if (GodWarsDungeonManager.handleSecondObjectAction(this.player, this.objectId)) {
                this.stop();
                return;
            }
        }
        if (StallThievingHandler.handleStallThieving(this.player, this.objectId, this.objectX, this.objectY)) {
            this.stop();
            return;
        }
        int n = this.objectY;
        int n2 = this.objectX;
        object = this.player;
        if (((Player)object).getAllotmentPatchManager().inspectPatch(n2, n) ? true : (((Player)object).getFlowerPatchManager().inspectPatch(n2, n) ? true : (((Player)object).getHerbPatchManager().inspectPatch(n2, n) ? true : (((Player)object).getHopsPatchManager().inspectPatch(n2, n) ? true : (((Player)object).getBushPatchManager().inspectPatch(n2, n) ? true : (((Player)object).getTreePatchManager().inspectPatch(n2, n) ? true : (((Player)object).getFruitTreePatchManager().inspectPatch(n2, n) ? true : (((Player)object).getSpecialTreePatchManager().inspectPatch(n2, n) ? true : ((Player)object).getSpecialCropPatchManager().inspectPatch(n2, n))))))))) {
            this.stop();
            return;
        }
        if (this.player.getMiningManager().prospectRock(this.objectId)) {
            this.stop();
            return;
        }
        if (PickableObjectHandler.handlePickableObject(this.player, this.objectId, this.objectX, this.objectY)) {
            this.stop();
            return;
        }
        if (this.objectId == 2418 && this.objectX == 2729 && this.objectY == 3470) {
            PartyRoomManager.openPartyChest(this.player);
            this.stop();
            return;
        }
        switch (this.player.getInteractionTargetId()) {
            case 2634: {
                int n3 = this.objectY;
                n = this.objectX;
                n2 = this.objectId;
                object = this.player;
                GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.findUsableGatheringTool((Player)object, 14);
                if (gatheringToolDefinition == null) {
                    Object object2 = object;
                    ((Player)object2).packetSender.sendGameMessage("You do not have a pickaxe that you can use.");
                    break;
                }
                if (!SkillActionHelper.checkSkillRequirement((Player)object, 14, 50, "mine here")) break;
                int n4 = ((Entity)object).nextActionSequence();
                ((Player)object).resetAnimation();
                ((Player)object).temporaryActionValue = 0;
                ((Entity)object).getUpdateState().setAnimation(gatheringToolDefinition.getGatherAnimationId());
                Object object3 = object;
                ((Player)object3).packetSender.sendSoundEffect(432, 1, 0);
                ((Entity)object).setActiveCycleEvent(new MiningShortcutTask((Player)object, n4, n, n3, n2, gatheringToolDefinition));
                CycleEventHandler.getInstance().schedule((Entity)object, ((Entity)object).getActiveCycleEvent(), 3);
                break;
            }
            case 2114: {
                object = this.player;
                if (((Player)object).getCoalTruckCoalCount() == 0) {
                    Object object4 = object;
                    ((Player)object4).packetSender.sendGameMessage("There is no coal left in the truck.");
                    break;
                }
                Object object5 = object;
                ((Player)object5).packetSender.sendGameMessage("The truck contains " + ((Player)object).getCoalTruckCoalCount() + " pieces of coal.");
                break;
            }
            case 3194: {
                BankManager.openBank(this.player);
                break;
            }
            case 8930: {
                AttackStyleDefinition.startDelayedObjectMove(this.player, new Position(2545, 10143, 0));
                break;
            }
            case 10177: {
                AttackStyleDefinition.startDelayedObjectMove(this.player, new Position(2544, 3741, 0));
                break;
            }
            case 3433: {
                AttackStyleDefinition.toggleObjectAfterAnimation(this.player, this.objectId, 3432, worldObject);
                break;
            }
            case 1570: {
                AttackStyleDefinition.toggleObjectAfterAnimation(this.player, this.objectId, 1568, worldObject);
                break;
            }
            case 1739: 
            case 4569: 
            case 12537: {
                AttackStyleDefinition.climbOffsetLadder(this.player, "up");
                break;
            }
            case 1748: 
            case 2884: 
            case 8745: 
            case 12965: {
                AttackStyleDefinition.climbOneFloorAtCurrentTile(this.player, "up");
                break;
            }
            case 2781: 
            case 3044: 
            case 9390: 
            case 11666: 
            case 14921: {
                SmeltingHandler.openSmeltingInterface(this.player);
                break;
            }
            case 2644: {
                GameplayHelper.openProductionInterface(this.player, "spinning");
                if (!this.player.botEnabled) break;
                CraftingHandler.startBotSpinningTask(this.player);
                break;
            }
            case 2213: 
            case 5276: 
            case 6084: 
            case 10517: 
            case 11338: 
            case 11758: 
            case 12121: 
            case 14367: {
                BankManager.openBank(this.player);
                break;
            }
            case 8717: {
                GameplayHelper.openProductionInterface(this.player, "weaving");
                break;
            }
            default: {
                Player player = this.player;
                player.packetSender.sendGameMessage("Nothing interesting happens.");
            }
        }
        this.stop();
    }
}

