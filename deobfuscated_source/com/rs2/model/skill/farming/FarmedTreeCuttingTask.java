/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.farming.TreePatch;
import com.rs2.model.skill.farming.TreePatchManager;
import com.rs2.model.skill.woodcutting.TreeDefinition;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.GameUtil;

public final class FarmedTreeCuttingTask
extends CycleEvent {
    private /* synthetic */ TreePatchManager manager;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ TreeDefinition treeDefinition;
    private final /* synthetic */ GatheringToolDefinition gatheringTool;
    private final /* synthetic */ int treeObjectId;
    private final /* synthetic */ TreePatch patch;
    private final /* synthetic */ int x;
    private final /* synthetic */ int y;

    public FarmedTreeCuttingTask(TreePatchManager treePatchManager, int n, int n2, TreeDefinition treeDefinition, GatheringToolDefinition gatheringToolDefinition, int n3, TreePatch treePatch, int n4, int n5) {
        this.manager = treePatchManager;
        this.actionSequence = n;
        this.animationId = n2;
        this.treeDefinition = treeDefinition;
        this.gatheringTool = gatheringToolDefinition;
        this.treeObjectId = n3;
        this.patch = treePatch;
        this.x = n4;
        this.y = n5;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        Object object;
        if (!TreePatchManager.getPlayer(this.manager).isCurrentActionSequence(this.actionSequence) || TreePatchManager.getPlayer(this.manager).getInventoryManager().getContainer().getFreeSlots() <= 0) {
            cycleEventContainer.stop();
            return;
        }
        ++TreePatchManager.getPlayer((TreePatchManager)this.manager).temporaryActionValue;
        if (TreePatchManager.getPlayer((TreePatchManager)this.manager).temporaryActionValue % 4 != 0) {
            return;
        }
        if (GameUtil.randomInclusive(256) == 0) {
            object = new GroundItem(new ItemStack(5070 + GameUtil.randomInclusive(4)), TreePatchManager.getPlayer(this.manager));
            GroundItemManager.getInstance().spawn((GroundItem)object);
        }
        TreePatchManager.getPlayer(this.manager).getUpdateState().setAnimation(this.animationId);
        if (GameUtil.rollLevelScaledChance(this.treeDefinition.getCutChanceLow(), this.treeDefinition.getCutChanceHigh(), TreePatchManager.getPlayer(this.manager).getSkillManager().getCurrentLevels()[8], this.gatheringTool.getToolSpeed())) {
            TreePatchManager.getPlayer(this.manager).getInventoryManager().addItem(new ItemStack(this.treeDefinition.getLogItemId()));
            object = TreePatchManager.getPlayer(this.manager);
            PacketSender packetSender = ((Player)object).packetSender;
            StringBuilder stringBuilder = new StringBuilder("You get some ");
            ItemService.getInstance();
            packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(TreeDefinition.forObjectId(this.treeObjectId).getLogItemId()).toLowerCase()).append(".").toString());
            TreePatchManager.getPlayer(this.manager).getSkillManager().addExperience(8, this.treeDefinition.getExperience());
            if (GameUtil.rollChance(this.treeDefinition.getDepletionChance())) {
                TreePatchManager.getPlayer(this.manager).getTreePatchManager().scheduleStumpRegrowth(this.patch.getIndex());
                this.manager.patchStates[this.patch.getIndex()] = 7;
                this.manager.refreshConfig();
                object = TreePatchManager.getPlayer(this.manager);
                ((Player)object).packetSender.sendSoundEffect(1312, 1, 0);
                cycleEventContainer.stop();
                TreePatchManager.getPlayer(this.manager).getUpdateState().setAnimation(-1, 0);
            }
        }
        if (!this.manager.canContinueCuttingTree(this.x, this.y)) {
            TreePatchManager.getPlayer(this.manager).getUpdateState().setAnimation(-1, 0);
            cycleEventContainer.stop();
        }
    }

    @Override
    public final void onStop() {
    }
}

