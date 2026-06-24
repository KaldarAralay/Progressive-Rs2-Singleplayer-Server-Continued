/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.cooking.CookableFoodDefinition;
import com.rs2.model.skill.cooking.CookingManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class CookingTask
extends CycleEvent {
    private int remainingActions;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;

    public CookingTask(int n, Player player, int n2) {
        this.player = player;
        this.actionSequence = n2;
        this.remainingActions = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!(this.player.isInterruptibleActionActive() && this.player.isCurrentActionSequence(this.actionSequence) && this.player.getInventoryManager().getContainer().containsItem(this.player.getSelectedSkillItemId()) && this.remainingActions != 0)) {
            if (this.player.botEnabled) {
                CookableFoodDefinition cookableFoodDefinition = CookableFoodDefinition.forRawItemId(this.player.getSelectedSkillItemId());
                if (cookableFoodDefinition != null && this.player.getInventoryManager().getContainer().containsItem(cookableFoodDefinition.getBurntItemId())) {
                    ItemStack[] itemStackArray = this.player.getInventoryManager().getContainer().getItems();
                    int n = itemStackArray.length;
                    int n2 = 0;
                    while (n2 < n) {
                        ItemStack itemStack = itemStackArray[n2];
                        if (itemStack != null && itemStack.getId() == cookableFoodDefinition.getBurntItemId()) {
                            BotCombatHelper.dropInventoryItem(this.player, itemStack);
                        }
                        ++n2;
                    }
                }
                this.player.setSelectedSkillItemId(0);
                this.player.currentBotTask.startWalkToBank(this.player);
            }
            cycleEventContainer.stop();
            return;
        }
        Object object = this.player;
        if (((Player)object).interfaceAction.equals("cookFire") && !SkillActionHelper.isObjectPresent(this.player.getCookingObjectId(), this.player.getCookingManager().firePosition.getX(), this.player.getCookingManager().firePosition.getY(), this.player.getCookingManager().firePosition.getPlane())) {
            if (this.player.botEnabled) {
                object = CookableFoodDefinition.forRawItemId(this.player.getSelectedSkillItemId());
                if (object != null && this.player.getInventoryManager().getContainer().containsItem(((CookableFoodDefinition)((Object)object)).getBurntItemId())) {
                    ItemStack[] itemStackArray = this.player.getInventoryManager().getContainer().getItems();
                    int n = itemStackArray.length;
                    int n3 = 0;
                    while (n3 < n) {
                        ItemStack itemStack = itemStackArray[n3];
                        if (itemStack != null && itemStack.getId() == ((CookableFoodDefinition)((Object)object)).getBurntItemId()) {
                            BotCombatHelper.dropInventoryItem(this.player, itemStack);
                        }
                        ++n3;
                    }
                }
                this.player.currentBotTask.startWalkToBank(this.player);
            }
            cycleEventContainer.stop();
            return;
        }
        CookingManager.cookSelectedItem(this.player);
        --this.remainingActions;
        cycleEventContainer.setTickDelay(4);
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

