/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.woodcutting;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.randomevent.SkillRandomEventNpc;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.woodcutting.TreeDefinition;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;
import com.rs2.util.GameplayTrace;

public final class WoodcuttingTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ TreeDefinition treeDefinition;
    private final /* synthetic */ int x;
    private final /* synthetic */ int y;
    private final /* synthetic */ GatheringToolDefinition gatheringTool;
    private final /* synthetic */ int treeObjectId;

    public WoodcuttingTask(Player player, int n, TreeDefinition treeDefinition, int n2, int n3, GatheringToolDefinition gatheringToolDefinition, int n4) {
        this.player = player;
        this.actionSequence = n;
        this.treeDefinition = treeDefinition;
        this.x = n2;
        this.y = n3;
        this.gatheringTool = gatheringToolDefinition;
        this.treeObjectId = n4;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n;
        boolean bl;
        Object object;
        block39: {
            if (!this.player.isCurrentActionSequence(this.actionSequence)) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("woodcutting stop invalid-sequence player=" + GameplayTrace.describe(this.player) + " seq=" + this.actionSequence + " tree=" + this.treeDefinition + " objectId=" + this.treeObjectId + " x=" + this.x + " y=" + this.y);
                }
                cycleEventContainer.stop();
                return;
            }
            object = new Position(this.x, this.y, this.player.getPosition().getPlane());
            TreeDefinition treeDefinition = this.treeDefinition;
            if (treeDefinition.getEntNpcIds() == null) {
                bl = false;
            } else {
                int[] nArray = treeDefinition.getEntNpcIds();
                int n2 = nArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    int n4 = nArray[n3];
                    Npc entNpc = Npc.findByDefinitionIdAtPosition(n4, (Position)object);
                    if (entNpc != null) {
                        bl = true;
                        break block39;
                    }
                    ++n3;
                }
                bl = false;
            }
        }
        if (bl) {
            if (this.player.gatheringHazardCounter >= 2) {
                ItemCombinationHandler.breakGatheringTool(this.player, 8);
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("Your axe has been broken by the tree ent!");
                object = this.player;
                ((Player)object).packetSender.sendSoundEffect(343, 1, 0);
                this.player.getUpdateState().setAnimation(-1);
                if (this.player.botEnabled) {
                    this.player.currentBotTask.startWalkToBank(this.player);
                }
                cycleEventContainer.stop();
                return;
            }
            this.player.getUpdateState().setAnimation(this.gatheringTool.getGatherAnimationId(), 0);
            ++this.player.gatheringHazardCounter;
            return;
        }
        ObjectManager.getInstance();
        Object object3 = ObjectManager.findDynamicObjectAt(this.x, this.y, this.player.getPosition().getPlane());
        if (object3 != null && ((DynamicObject)object3).getWorldObject().getObjectId() != this.treeObjectId) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("woodcutting stop depleted-before-roll player=" + GameplayTrace.describe(this.player) + " seq=" + this.actionSequence + " tree=" + this.treeDefinition + " objectId=" + this.treeObjectId + " dynamicId=" + ((DynamicObject)object3).getWorldObject().getObjectId() + " x=" + this.x + " y=" + this.y);
            }
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("The tree has run out of logs.");
            cycleEventContainer.stop();
            if (this.player.botEnabled) {
                if (this.treeDefinition == TreeDefinition.VINES) {
                    this.player.botRouteActionPending = false;
                    return;
                }
                this.player.interactWithBotObjectTargets(this.player.botInteractionTargetIds);
            }
            return;
        }
        object3 = new ItemStack(this.treeDefinition.getLogItemId(), 1);
        if (((ItemStack)object3).getId() > 0 && this.player.getInventoryManager().getContainer().getFirstFreeSlot() == -1) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("woodcutting stop full-inventory player=" + GameplayTrace.describe(this.player) + " seq=" + this.actionSequence + " tree=" + this.treeDefinition + " logItemId=" + ((ItemStack)object3).getId());
            }
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("Your inventory is too full to hold any more " + ((ItemStack)object3).getDefinition().getName().toLowerCase() + ".");
            object = this.player;
            ((Player)object).packetSender.sendSoundEffect(1878, 1, 0);
            cycleEventContainer.stop();
            if (this.player.botEnabled) {
                this.player.currentBotTask.startWalkToBank(this.player);
            }
            return;
        }
        if (SkillActionHelper.shouldTriggerRandomEvent(this.player) && !this.player.botEnabled && !this.player.isInTutorialIsland()) {
            GameplayHelper.spawnSkillRandomEventNpc(this.player, SkillRandomEventNpc.c);
        }
        if (this.player.isMember() && !ServerSettings.freeToPlayWorld && GameUtil.randomInt(256) == 0 && !this.player.botEnabled && !this.player.isInTutorialIsland() && ItemDefinition.isDefined(n = 5070 + GameUtil.randomInclusive(4))) {
            GroundItem groundItem = new GroundItem(new ItemStack(n), this.player);
            GroundItemManager.getInstance().spawn(groundItem);
        }
        if (GameUtil.rollLevelScaledChance(this.treeDefinition.getCutChanceLow(), this.treeDefinition.getCutChanceHigh(), this.player.getSkillManager().getCurrentLevels()[8], this.gatheringTool.getToolSpeed())) {
            this.player.getSkillManager().addExperience(8, this.treeDefinition.getExperience());
            if (((ItemStack)object3).getId() > 0) {
                this.player.getInventoryManager().addItem((ItemStack)object3);
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("woodcutting success player=" + GameplayTrace.describe(this.player) + " seq=" + this.actionSequence + " tree=" + this.treeDefinition + " logItemId=" + ((ItemStack)object3).getId() + " logName=" + ((ItemStack)object3).getDefinition().getName() + " xp=" + this.treeDefinition.getExperience() + " x=" + this.x + " y=" + this.y);
                }
                WoodcuttingTask woodcuttingTask = this;
                woodcuttingTask.player.rollActionReward();
                if (this.player.getQuestState(0) != 1) {
                    this.player.getDialogueManager().showItemMessage("You get some logs.", new ItemStack(1511));
                    this.player.getDialogueManager().finishDialogue();
                    if (this.player.getQuestState(0) == 8) {
                        object = this.player;
                        ((Player)object).packetSender.sendEntityHintIcon(1, -1);
                        this.player.ea();
                    }
                    this.player.getQuestManager().refreshQuestJournal();
                    this.player.setInteractionTargetId(0);
                } else if (this.treeDefinition != TreeDefinition.DRAMEN_TREE && this.treeDefinition != TreeDefinition.STRANGE_MUSICAL_TREE) {
                    object = this.player;
                    ((Player)object).packetSender.sendGameMessage("You get some " + ((ItemStack)object3).getDefinition().getName().toLowerCase() + ".");
                } else if (this.treeDefinition == TreeDefinition.DRAMEN_TREE) {
                    object = this.player;
                    ((Player)object).packetSender.sendGameMessage("You cut a branch from the Dramen tree.");
                } else if (this.treeDefinition == TreeDefinition.STRANGE_MUSICAL_TREE) {
                    object = this.player;
                    ((Player)object).packetSender.sendGameMessage("You cut a branch from the strangely musical tree.");
                }
            }
            if (this.treeDefinition != TreeDefinition.DRAMEN_TREE && this.treeDefinition != TreeDefinition.STRANGE_MUSICAL_TREE && GameUtil.rollChance(TreeDefinition.getDepletionChance(this.treeDefinition))) {
                if (this.treeDefinition != TreeDefinition.VINES) {
                    if (GameplayTrace.enabled()) {
                        GameplayTrace.log("woodcutting depleted player=" + GameplayTrace.describe(this.player) + " seq=" + this.actionSequence + " tree=" + this.treeDefinition + " stumpId=" + this.treeDefinition.getStumpObjectId() + " objectId=" + this.treeObjectId + " x=" + this.x + " y=" + this.y);
                    }
                    object = this.player;
                    ((Player)object).packetSender.sendGameMessage("The tree has run out of logs.");
                    object = this.player;
                    ((Player)object).packetSender.sendSoundEffect(1312, 1, 0);
                }
                int n5 = SkillActionHelper.getObjectOrientation(this.treeObjectId, this.x, this.y, this.player.getPosition().getPlane());
                int n6 = GameUtil.randomBetweenInclusive(this.treeDefinition.getRespawnTicksLow(), this.treeDefinition.getRespawnTicksHigh());
                new DynamicObject(this.treeDefinition.getStumpObjectId(), this.x, this.y, this.player.getPosition().getPlane(), n5, 10, this.treeObjectId, n6, this.treeDefinition != TreeDefinition.VINES);
                cycleEventContainer.stop();
                if (this.treeDefinition == TreeDefinition.VINES) {
                    int n7 = this.y;
                    n5 = this.x;
                    object3 = this.player;
                    if (n5 > ((Entity)object3).getPosition().getX() && n7 == ((Entity)object3).getPosition().getY()) {
                        Object object4 = object3;
                        ((Player)object4).packetSender.queueRelativeMovementStep(2, 0, true);
                    } else if (n5 < ((Entity)object3).getPosition().getX() && n7 == ((Entity)object3).getPosition().getY()) {
                        Object object5 = object3;
                        ((Player)object5).packetSender.queueRelativeMovementStep(-2, 0, true);
                    } else if (n5 == ((Entity)object3).getPosition().getX() && n7 < ((Entity)object3).getPosition().getY()) {
                        Object object6 = object3;
                        ((Player)object6).packetSender.queueRelativeMovementStep(0, -2, true);
                    } else {
                        Object object7 = object3;
                        ((Player)object7).packetSender.queueRelativeMovementStep(0, 2, true);
                    }
                    if (this.player.botEnabled) {
                        this.player.botRouteActionPending = false;
                        return;
                    }
                } else if (this.player.botEnabled) {
                    this.player.interactWithBotObjectTargets(this.player.botInteractionTargetIds);
                }
                return;
            }
        }
        if (((ItemStack)object3).getId() > 0 && this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("woodcutting stop no-space-after-roll player=" + GameplayTrace.describe(this.player) + " seq=" + this.actionSequence + " tree=" + this.treeDefinition + " logItemId=" + ((ItemStack)object3).getId());
            }
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("Not enough space in your inventory.");
            object = this.player;
            ((Player)object).packetSender.sendSoundEffect(1878, 1, 0);
            this.player.getUpdateState().setAnimation(-1);
            cycleEventContainer.stop();
            if (this.player.botEnabled) {
                this.player.currentBotTask.startWalkToBank(this.player);
            }
            return;
        }
        this.player.getUpdateState().setAnimation(this.gatheringTool.getGatherAnimationId(), 0);
    }

    @Override
    public final void onStop() {
        this.player.getMovementQueue().clear();
        this.player.getUpdateState().setAnimation(-1, 0);
    }
}

