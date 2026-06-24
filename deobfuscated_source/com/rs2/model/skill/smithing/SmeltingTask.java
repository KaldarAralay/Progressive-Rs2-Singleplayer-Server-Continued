/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.smithing;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class SmeltingTask
extends CycleEvent {
    private int remainingActions;
    private int ticksUntilSmelt;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ ItemStack primaryOre;
    private final /* synthetic */ boolean hasSecondaryOre;
    private final /* synthetic */ ItemStack secondaryOre;
    private final /* synthetic */ int barItemId;
    private final /* synthetic */ ItemStack outputBar;
    private final /* synthetic */ int experience;

    public SmeltingTask(int n, Player player, int n2, ItemStack itemStack, boolean bl, ItemStack itemStack2, int n3, ItemStack itemStack3, int n4) {
        this.player = player;
        this.actionSequence = n2;
        this.primaryOre = itemStack;
        this.hasSecondaryOre = bl;
        this.secondaryOre = itemStack2;
        this.barItemId = n3;
        this.outputBar = itemStack3;
        this.experience = n4;
        this.remainingActions = n;
        this.ticksUntilSmelt = 4;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        Player player;
        if (this.ticksUntilSmelt == 5) {
            if (!this.player.isCurrentActionSequence(this.actionSequence)) {
                cycleEventContainer.stop();
                if (this.player.botEnabled) {
                    this.player.currentBotTask.startWalkToBank(this.player);
                }
                return;
            }
            if (!this.player.getInventoryManager().containsItemStack(this.primaryOre) || this.hasSecondaryOre && !this.player.getInventoryManager().containsItemStack(this.secondaryOre)) {
                Player player2 = this.player;
                player2.packetSender.sendGameMessage("You have run out of ore to smith!");
                if (this.player.botEnabled) {
                    this.player.currentBotTask.startWalkToBank(this.player);
                }
                cycleEventContainer.stop();
                return;
            }
            player = this.player;
            player.packetSender.sendSoundEffect(469, 1, 0);
            this.player.getUpdateState().setAnimation(899);
            this.player.setActionLocked(true);
            player = this.player;
            player.packetSender.sendGameMessage("You smelt the " + this.primaryOre.getDefinition().getName().toLowerCase() + " " + (this.hasSecondaryOre ? "and " + this.secondaryOre.getDefinition().getName().toLowerCase() + " together " : "") + "in the furnace.");
            this.ticksUntilSmelt = 4;
        }
        if (this.ticksUntilSmelt == 0) {
            --this.remainingActions;
            this.player.setActionLocked(false);
            this.player.getInventoryManager().removeItem(this.primaryOre);
            if (this.hasSecondaryOre) {
                this.player.getInventoryManager().removeItem(this.secondaryOre);
            }
            if (this.barItemId == 2351 && GameUtil.randomInclusive(100) > 50 && this.player.getEquipmentManager().getItemIdAtSlot(12) != 2568) {
                player = this.player;
                player.packetSender.sendGameMessage("You unsuccessfuly smelt the iron ore.");
            } else {
                if (this.barItemId == 2351 && this.player.getEquipmentManager().getItemIdAtSlot(12) == 2568) {
                    this.player.setRingOfForgingLife(this.player.getRingOfForgingLife() - 1);
                    if (this.player.getRingOfForgingLife() <= 0) {
                        this.player.getEquipmentManager().consumeSlotItemAmount(12, 1);
                        player = this.player;
                        player.packetSender.sendGameMessage("Your ring of forging shatters!");
                        this.player.setRingOfForgingLife(140);
                    }
                }
                this.player.getInventoryManager().addItem(this.outputBar);
                SmeltingTask smeltingTask = this;
                smeltingTask.player.rollActionReward();
                if (this.outputBar.getId() == 2357 && this.player.getEquipmentManager().getItemIdAtSlot(9) == 776) {
                    this.player.getSkillManager().addExperience(13, 56.2);
                } else {
                    this.player.getSkillManager().addExperience(13, this.experience);
                }
                if (this.player.getQuestState(0) != 1) {
                    if (this.player.getQuestState(0) == 35) {
                        this.player.ea();
                    }
                    this.player.setInteractionTargetId(0);
                    this.player.getDialogueManager().showOneLineStatement("You retrieve a " + this.outputBar.getDefinition().getName().toLowerCase() + " from the furnace.");
                    this.player.getQuestManager().refreshQuestJournal();
                } else if (this.outputBar.getId() == 2365) {
                    player = this.player;
                    player.packetSender.sendGameMessage("You retrieve a bar of gold from the furnace.");
                } else {
                    player = this.player;
                    player.packetSender.sendGameMessage("You retrieve a " + this.outputBar.getDefinition().getName().toLowerCase() + " from the furnace.");
                }
            }
            if (this.remainingActions > 0) {
                this.ticksUntilSmelt = 5;
                if (!this.player.getInventoryManager().containsItemStack(this.primaryOre) || this.hasSecondaryOre && !this.player.getInventoryManager().containsItemStack(this.secondaryOre)) {
                    player = this.player;
                    player.packetSender.sendGameMessage("You have run out of ore to smith!");
                    if (this.player.botEnabled) {
                        this.player.currentBotTask.startWalkToBank(this.player);
                    }
                    cycleEventContainer.stop();
                }
                return;
            }
            if (this.remainingActions <= 0) {
                cycleEventContainer.stop();
                if (this.player.botEnabled) {
                    this.player.currentBotTask.startWalkToBank(this.player);
                }
                return;
            }
        }
        --this.ticksUntilSmelt;
    }

    @Override
    public final void onStop() {
        this.player.nextActionSequence();
        this.player.resetAnimation();
    }
}

