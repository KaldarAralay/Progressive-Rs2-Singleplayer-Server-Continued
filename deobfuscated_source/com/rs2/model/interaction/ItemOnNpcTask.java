/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.ServerSettings;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.GameplayHelper;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.item.action.BarrowsRepairHandler;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class ItemOnNpcTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ Npc npc;
    private final /* synthetic */ int itemId;
    private final /* synthetic */ int itemSlot;

    public ItemOnNpcTask(int n, boolean bl, Player player, int n2, Npc npc, int n3, int n4) {
        super(1, true);
        this.player = player;
        this.actionSequence = n2;
        this.npc = npc;
        this.itemId = n3;
        this.itemSlot = n4;
    }

    @Override
    public final void execute() {
        if (this.player == null || !this.player.isCurrentActionSequence(this.actionSequence) || this.npc.isDead()) {
            this.stop();
            return;
        }
        if (!this.player.isWithinReach(this.npc, 1) || this.player.isOverlapping(this.npc)) {
            return;
        }
        if (!GameUtil.hasClearPath(this.player.getPosition(), this.npc.getPosition(), true) && this.npc.getNpcId() != 901) {
            return;
        }
        this.player.getSlayerManager().useFinishingItemOnMonster(this.npc, this.itemId);
        EntityTargetMovement.clearMovementTarget(this.player);
        if (this.player.getQuestManager().handleItemOnNpc(this.player.getInteractionTargetId(), this.itemId)) {
            this.stop();
            return;
        }
        if (this.player.getRunecraftingObjectHandler().handleToolOnNpc(this.player.getInteractionTargetId(), this.itemId)) {
            this.stop();
            return;
        }
        if (BarrowsRepairHandler.handleItemOnNpc(this.player, this.player.getInteractionTargetId(), this.player.getInventoryManager().getContainer().getItemAt(this.itemSlot))) {
            this.stop();
            return;
        }
        switch (this.player.getInteractionTargetId()) {
            case 3021: {
                if (!this.player.isMember()) {
                    this.player.packetSender.sendGameMessage("You need a members account to access members content.");
                    this.stop();
                    return;
                }
                if (ServerSettings.freeToPlayWorld) {
                    this.player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    this.stop();
                    return;
                }
                this.player.getFarmingToolStore().noteProduce(this.npc, this.itemId);
                this.stop();
                return;
            }
        }
        switch (this.itemId) {
            case 1735: {
                if (this.player.getInteractionTargetId() != 43 && this.player.getInteractionTargetId() != 1765) break;
                ProjectileDefinition.startSheepShearing(this.player);
                this.stop();
                return;
            }
            case 1925: {
                if (this.player.getInteractionTargetId() != 81 || GameplayHelper.isObjectDefinitionIdValid(8689)) break;
                GameplayHelper.milkCow(this.player);
                this.stop();
                return;
            }
        }
        this.stop();
    }
}

