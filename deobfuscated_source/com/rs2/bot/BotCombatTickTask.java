/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.consumable.FoodDefinition;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.task.TickTask;

public final class BotCombatTickTask
extends TickTask {
    private /* synthetic */ BotTaskDefinition taskDefinition;
    private final /* synthetic */ Npc targetNpc;
    private final /* synthetic */ Player bot;

    public BotCombatTickTask(BotTaskDefinition botTaskDefinition, int n, Npc npc, Player player) {
        super(1);
        this.taskDefinition = botTaskDefinition;
        this.targetNpc = npc;
        this.bot = player;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public final void execute() {
        boolean bl;
        Player player;
        Npc npc;
        block14: {
            block16: {
                BotTaskDefinition botTaskDefinition;
                block15: {
                    if (this.targetNpc.isDead()) {
                        CombatManager.stopCombat(this.bot);
                        this.stop();
                        return;
                    }
                    if (this.bot.isDead() || !this.bot.isRegistered()) {
                        CombatManager.stopCombat(this.bot);
                        this.stop();
                        return;
                    }
                    if (this.bot.getCombatTarget() == null) {
                        CombatManager.stopCombat(this.bot);
                        this.stop();
                        return;
                    }
                    if (!this.bot.botTaskState.equals("do task")) {
                        CombatManager.stopCombat(this.bot);
                        this.stop();
                        return;
                    }
                    npc = this.targetNpc;
                    player = this.bot;
                    botTaskDefinition = this.taskDefinition;
                    if (player.botCombatState != null && player.botCombatState.equals("escape")) {
                        CombatManager.stopCombat(player);
                        botTaskDefinition.startWalkToBank(player);
                        return;
                    }
                    if (player.botCombatState != null) return;
                    bl = false;
                    player.getSkillManager();
                    int n = SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]);
                    int n2 = player.getSkillManager().getCurrentLevels()[3];
                    FoodDefinition foodDefinition = FoodDefinition.forItemId(player.botFoodItemId);
                    if (foodDefinition == null) {
                        CombatManager.stopCombat(player);
                        botTaskDefinition.startWalkToBank(player);
                        return;
                    }
                    int n3 = foodDefinition.getHealAmount();
                    if (n2 + n3 < n) {
                        bl = true;
                    }
                    if (!bl) break block14;
                    if (!BotCombatHelper.eatBotFood(player)) break block15;
                    if (player.getInventoryManager().getItemAmount(player.botFoodItemId) > 2) break block16;
                    BotTaskDefinition botTaskDefinition2 = botTaskDefinition;
                    if (botTaskDefinition2.forcedCombatStyle == 2) break block16;
                }
                CombatManager.stopCombat(player);
                botTaskDefinition.startWalkToBank(player);
                return;
            }
            if (npc != null && !npc.isDead()) {
                CombatManager.startCombat(player, npc);
                return;
            }
            player.interactWithBotNpcTargets(player.botInteractionTargetIds);
            return;
        }
        int n = player.getEquipmentManager().getItemIdAtSlot(3);
        bl = false;
        if (n > 0) {
            bl = ItemDefinition.forId(n).isStackable();
        }
        if (player.getEquipmentManager().getItemIdAtSlot(13) == 0) {
            if (!bl) return;
        }
        n = bl ? 3 : 13;
        GroundItemManager.getInstance();
        Object object = GroundItemManager.findVisibleItem(player, player.getEquipmentManager().getItemIdAtSlot(n), npc.getPosition());
        if (object == null) return;
        if (((ItemStack)(object = ((GroundItem)object).getItem())).getAmount() >= 6 && !player.isMovementLocked()) {
            player.setAutoRetaliate(false);
            CombatManager.stopCombat(player);
            player.botCombatState = "loot arrows";
            player.getMovementQueue().setRunning(true);
            player.botLootResumeTarget = npc;
            BotCombatHelper.pickupBotCombatGroundItem(player, player.getEquipmentManager().getItemIdAtSlot(n), npc.getPosition());
            return;
        }
        if (player.getEquipmentManager().getContainer().getItemAt(n).getAmount() > 3) return;
        if (player.isMovementLocked()) return;
        player.setAutoRetaliate(false);
        CombatManager.stopCombat(player);
        player.botCombatState = "loot arrows";
        player.getMovementQueue().setRunning(true);
        player.botLootResumeTarget = npc;
        BotCombatHelper.pickupBotCombatGroundItem(player, player.getEquipmentManager().getItemIdAtSlot(n), npc.getPosition());
    }
}

