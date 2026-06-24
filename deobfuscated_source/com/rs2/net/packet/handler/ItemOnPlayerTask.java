/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet.handler;

import com.rs2.model.EntityTargetMovement;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.handler.PlayerInteractionPacketHandler;
import com.rs2.util.GameUtil;

public final class ItemOnPlayerTask
extends TickTask {
    private final /* synthetic */ Player targetPlayer;
    private final /* synthetic */ Player requestingPlayer;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ ItemStack usedItem;
    private final /* synthetic */ int inventorySlot;

    public ItemOnPlayerTask(PlayerInteractionPacketHandler playerInteractionPacketHandler, int n, Player player, Player player2, int n2, ItemStack itemStack, int n3) {
        super(1);
        this.targetPlayer = player;
        this.requestingPlayer = player2;
        this.actionSequence = n2;
        this.usedItem = itemStack;
        this.inventorySlot = n3;
    }

    @Override
    public final void execute() {
        if (this.targetPlayer == null || this.targetPlayer.isDead() || !this.requestingPlayer.isCurrentActionSequence(this.actionSequence)) {
            EntityTargetMovement.clearMovementTarget(this.requestingPlayer);
            this.requestingPlayer.setInteractionTarget(null);
            this.requestingPlayer.getMovementQueue().clear();
            this.stop();
            return;
        }
        if (this.requestingPlayer.isWithinReach(this.targetPlayer, 1) && !this.requestingPlayer.isOverlapping(this.targetPlayer) && !EntityTargetMovement.isDiagonalTo(this.requestingPlayer.getPosition(), this.targetPlayer.getPosition())) {
            if (this.usedItem.getDefinition().g() && (this.requestingPlayer.gameMode != 0 || this.targetPlayer.gameMode != 0)) {
                this.requestingPlayer.pendingDialogueItem = this.usedItem;
                this.requestingPlayer.pendingItemDropTarget = this.targetPlayer;
                DialogueManager.startDialogue(this.requestingPlayer, 12345);
                EntityTargetMovement.clearMovementTarget(this.requestingPlayer);
                this.requestingPlayer.getUpdateState().setFacePosition(this.targetPlayer.getPosition());
                this.requestingPlayer.setInteractionTarget(null);
                this.requestingPlayer.getMovementQueue().clear();
                this.stop();
            }
            switch (this.usedItem.getId()) {
                case 962: {
                    this.requestingPlayer.getInventoryManager().removeItem(this.usedItem);
                    Player player = this.requestingPlayer;
                    player.packetSender.sendGameMessage("You pull the cracker with " + this.targetPlayer.getUsername() + "...");
                    player = this.targetPlayer;
                    player.packetSender.sendGameMessage(String.valueOf(this.requestingPlayer.getUsername()) + " pulls a Christmas cracker with you...");
                    if (GameUtil.randomInclusive(1) == 1) {
                        player = this.requestingPlayer;
                        player.packetSender.sendGameMessage("  ... and get a partyhat! Merry Christmas!");
                        player = this.targetPlayer;
                        player.packetSender.sendGameMessage("  ... and they get a partyhat! But have some coins anyways, Merry Christmas!");
                        this.targetPlayer.getInventoryManager().addItem(new ItemStack(995, 5 + GameUtil.randomInclusive(100)));
                        this.requestingPlayer.getInventoryManager().setItemInSlot(new ItemStack(1038 + (GameUtil.randomInclusive(5) << 1)), this.inventorySlot);
                        break;
                    }
                    player = this.targetPlayer;
                    player.packetSender.sendGameMessage("  ... and you get a partyhat! Merry Christmas!");
                    player = this.requestingPlayer;
                    player.packetSender.sendGameMessage("  ... and they get a partyhat! But have some coins anyways, Merry Christmas!");
                    this.requestingPlayer.getInventoryManager().setItemInSlot(new ItemStack(995, 5 + GameUtil.randomInclusive(100)), this.inventorySlot);
                    this.targetPlayer.getInventoryManager().addItem(new ItemStack(1038 + (GameUtil.randomInclusive(5) << 1)));
                    break;
                }
                default: {
                    Player player = this.requestingPlayer;
                    player.packetSender.sendGameMessage("Nothing interesting happens.");
                }
            }
            EntityTargetMovement.clearMovementTarget(this.requestingPlayer);
            this.requestingPlayer.getUpdateState().setFacePosition(this.targetPlayer.getPosition());
            this.requestingPlayer.setInteractionTarget(null);
            this.requestingPlayer.getMovementQueue().clear();
            this.stop();
        }
    }
}

