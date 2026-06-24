/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.combat;

import com.rs2.bot.combat.BotCombatEscapeHandler;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.combat.BotPvpOpponentTargetCombatTickTask;
import com.rs2.bot.combat.BotPvpSelfTargetCombatTickTask;
import com.rs2.bot.combat.WildernessBotSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.combat.special.SpecialAttackDefinition;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.skill.magic.Spellbook;
import com.rs2.util.GameUtil;

public final class BotPvpCombatHandler {
    public static int MELEE_COMBAT_STYLE = 0;
    public static int RANGED_COMBAT_STYLE = 1;
    public static int MAGIC_COMBAT_STYLE = 2;

    public static void startBotPvpCombatTicks(Player player, Player player2) {
        boolean bl;
        boolean bl2;
        if (player.botEnabled) {
            bl2 = player.botCombatTickTask != null && player.botCombatTickTask.isActive();
            boolean bl3 = bl = player.botEscapeLogoutTask != null && player.botEscapeLogoutTask.isActive();
            if (!bl2 && !bl) {
                player.botCombatTickTask = new BotPvpSelfTargetCombatTickTask(1, player2, player);
                World.getTaskScheduler().schedule(player.botCombatTickTask);
            }
        }
        if (player2.botEnabled) {
            bl2 = player2.botCombatTickTask != null && player2.botCombatTickTask.isActive();
            boolean bl4 = bl = player2.botEscapeLogoutTask != null && player2.botEscapeLogoutTask.isActive();
            if (!bl2 && !bl) {
                player2.botCombatTickTask = new BotPvpOpponentTargetCombatTickTask(1, player, player2);
                World.getTaskScheduler().schedule(player2.botCombatTickTask);
            }
        }
    }

    static /* synthetic */ void processBotPvpCombatTick(Player player, Player player2) {
        int n;
        if (player.botCombatState != null && player.botEnabled && player.botCombatState.equals("escape")) {
            BotCombatEscapeHandler.tryStartBotCombatEscape(player);
            return;
        }
        if (!player.botEnabled || player.botCombatState != null) {
            return;
        }
        if (!player.getMovementQueue().isRunning()) {
            player.getMovementQueue().setRunning(true);
        }
        player.botOpponentCombatStyle = 0;
        if (player2.getAutocastSpell() != null) {
            player.botOpponentCombatStyle = MAGIC_COMBAT_STYLE;
        } else {
            String string;
            n = player2.getEquipmentManager().getItemIdAtSlot(3);
            if (n > 0 && ((string = ItemDefinition.forId(n).getName().toLowerCase()).contains("bow") || string.contains("knife") || string.contains("dart") || string.contains("javelin") || string.contains("thrownaxe"))) {
                player.botOpponentCombatStyle = RANGED_COMBAT_STYLE;
            }
        }
        if (player.botOpponentCombatStyle == MAGIC_COMBAT_STYLE && !player.botMagicPenaltyGearUnequipped) {
            if (player.botMagicGearSwapDelayTicks < 3) {
                ++player.botMagicGearSwapDelayTicks;
            } else {
                BotCombatHelper.unequipMagicPenaltyGear(player);
                player.botMagicGearSwapDelayTicks = 0;
            }
        } else if (player.botOpponentCombatStyle != MAGIC_COMBAT_STYLE && player.botMagicPenaltyGearUnequipped) {
            if (player.botMagicGearSwapDelayTicks < 3) {
                ++player.botMagicGearSwapDelayTicks;
            } else {
                BotCombatHelper.reequipMagicPenaltyGear(player);
                player.botMagicGearSwapDelayTicks = 0;
            }
        }
        int n2 = n = GameUtil.getDistance(player.getPosition(), player2.getPosition()) > 1 && GameUtil.isWithinDistance(player.getPosition(), player2.getPosition(), 15) ? 1 : 0;
        if (player.getAutocastSpell() != null && player.getQueuedCombatSpell() == null && player2 != null && !player2.isDead()) {
            player.setQueuedCombatSpell(player.getAutocastSpell());
            CombatManager.startCombat(player, player2);
        }
        if (player.botCombatSpell != null && player2 != null && !player2.isDead() && player.botCombatSpell.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6] && BotCombatHelper.hasRunesForSpell(player, player.botCombatSpell) && !player2.isMovementLocked() && !player2.isMovementLockImmune() && n != 0 && player2.isMoving()) {
            player.setQueuedCombatSpell(player.botCombatSpell);
            CombatManager.startCombat(player, player2);
        }
        if (12445 < InterfaceDefinition.interfaceCount && player2 != null && !player2.isDead() && player.botActiveCombatStyle == MAGIC_COMBAT_STYLE && player.getSpellbook() == Spellbook.MODERN && SpellDefinition.TELE_BLOCK.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6] && BotCombatHelper.hasRunesForSpell(player, SpellDefinition.TELE_BLOCK) && !player2.isTeleblocked()) {
            SpellDefinition spellDefinition = SpellDefinition.TELE_BLOCK;
            player.setQueuedCombatSpell(spellDefinition);
            CombatManager.startCombat(player, player2);
        }
        int n3 = BotCombatHelper.getEscapeCombatLevelMargin(player);
        if (WildernessBotSettings.escapeHighLevelAttackersEnabled && !player.clanWarsBot && player2.getCombatLevel() > player.getCombatLevel() + n3 && player.getCombatTarget() == player2) {
            if (player.botThreatEscapeDelayTicks < 3) {
                ++player.botThreatEscapeDelayTicks;
            } else {
                BotCombatEscapeHandler.tryStartBotCombatEscape(player);
                player.botThreatEscapeDelayTicks = 0;
            }
        }
        n3 = 0;
        player.getSkillManager();
        int n4 = SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]);
        int n5 = player.getSkillManager().getCurrentLevels()[3];
        int n6 = player2.getSkillManager().getCurrentLevels()[3];
        if (n4 < 40) {
            if ((double)n5 <= (double)n4 * 0.5 && n6 > n5) {
                n3 = 1;
            }
        } else if ((double)n5 <= (double)n4 * 0.3 && n6 > n5) {
            n3 = 1;
        }
        if (n3 != 0) {
            if (player.botEatDelayTicks < 3) {
                n3 = 0;
                ++player.botEatDelayTicks;
            } else {
                n3 = 1;
                player.botEatDelayTicks = 0;
            }
        }
        if (n3 != 0 && GameUtil.randomInt(100) < 10) {
            n3 = 0;
        }
        player2.getSkillManager();
        n4 = SkillManager.getLevelForExperience(player2.getSkillManager().getExperience()[3]);
        if (n3 == 0) {
            if ((double)n6 <= (double)n4 * 0.4) {
                if (player.botSpecialWeaponItemId == 0) {
                    SpecialAttackDefinition specialAttackDefinition = SpecialAttackDefinition.forItem(player.getEquipmentManager().getContainer().getItemAt(3));
                    if (specialAttackDefinition != null) {
                        player.botSpecialAttackEnergyCost = specialAttackDefinition.getEnergyCost();
                        if (player.getSpecialEnergy() >= player.botSpecialAttackEnergyCost && !player.isSpecialAttackEnabled() && player2 != null && !player2.isDead()) {
                            player.handleSpecialAttackButton(player.getWeaponProfile().getInterfaceDefinition().getSpecialAttackButtonId());
                            CombatManager.startCombat(player, player2);
                        }
                    }
                } else {
                    SpecialAttackDefinition specialAttackDefinition;
                    if (player.getEquipmentManager().getItemIdAtSlot(3) != player.botSpecialWeaponItemId && player2 != null && !player2.isDead() && (player.botSpecialAttackEnergyCost == 0 || player.botSpecialAttackEnergyCost > 0 && player.getSpecialEnergy() >= player.botSpecialAttackEnergyCost)) {
                        if (player.botWeaponSwapDelayTicks < 3) {
                            ++player.botWeaponSwapDelayTicks;
                        } else {
                            player.getEquipmentManager().equipFromInventorySlot(player.getInventoryManager().getContainer().indexOfItem(player.botSpecialWeaponItemId));
                            CombatManager.startCombat(player, player2);
                            player.botActiveCombatStyle = player.botSpecialCombatStyle;
                            player.botWeaponSwapDelayTicks = 0;
                        }
                    }
                    if (player.getEquipmentManager().getItemIdAtSlot(3) == player.botSpecialWeaponItemId && (specialAttackDefinition = SpecialAttackDefinition.forItem(player.getEquipmentManager().getContainer().getItemAt(3))) != null) {
                        player.botSpecialAttackEnergyCost = specialAttackDefinition.getEnergyCost();
                        if (player.getSpecialEnergy() >= player.botSpecialAttackEnergyCost) {
                            if (!player.isSpecialAttackEnabled() && player2 != null && !player2.isDead()) {
                                player.handleSpecialAttackButton(player.getWeaponProfile().getInterfaceDefinition().getSpecialAttackButtonId());
                                CombatManager.startCombat(player, player2);
                            }
                        } else if (player.getEquipmentManager().getItemIdAtSlot(3) != player.botWeaponItemId && player2 != null && !player2.isDead()) {
                            if (player.botWeaponSwapDelayTicks < 3) {
                                ++player.botWeaponSwapDelayTicks;
                            } else {
                                BotCombatHelper.restorePrimaryCombatGear(player);
                                CombatManager.startCombat(player, player2);
                                player.botWeaponSwapDelayTicks = 0;
                            }
                        }
                    }
                }
            } else if (player.getEquipmentManager().getItemIdAtSlot(3) != player.botWeaponItemId) {
                if (player.botWeaponSwapDelayTicks < 3) {
                    ++player.botWeaponSwapDelayTicks;
                } else {
                    BotCombatHelper.restorePrimaryCombatGear(player);
                    CombatManager.startCombat(player, player2);
                    player.botWeaponSwapDelayTicks = 0;
                }
            }
        }
        if (n3 != 0) {
            int n7;
            int n8 = n7 = player.isTeleblocked() ? 6 : 4;
            if (!player.isInWilderness()) {
                n7 = 0;
            }
            if (!BotCombatHelper.eatBotFood(player) || player.getInventoryManager().getItemAmount(player.botFoodItemId) <= n7) {
                BotCombatEscapeHandler.tryStartBotCombatEscape(player);
                return;
            }
            CombatManager.startCombat(player, player2);
            return;
        }
        if (player.getPoisonDamage() > 0.0 && GameUtil.randomInt(3) == 0) {
            BotCombatHelper.drinkAntipoisonPotion(player);
        }
        int n9 = player.getEquipmentManager().getItemIdAtSlot(3);
        n3 = 0;
        if (n9 > 0) {
            n3 = ItemDefinition.forId(n9).isStackable() ? 1 : 0;
        }
        if (n3 != 0 && player.getEquipmentManager().getContainer().getItemAmount(n9) <= 2) {
            BotCombatEscapeHandler.tryStartBotCombatEscape(player);
            return;
        }
        if (player.getEquipmentManager().getItemIdAtSlot(13) != 0 || n3 != 0) {
            n3 = n3 != 0 ? 3 : 13;
            GroundItemManager.getInstance();
            Object object = GroundItemManager.findVisibleItem(player, player.getEquipmentManager().getItemIdAtSlot(n3), player2.getPosition());
            if (object != null) {
                if (((ItemStack)(object = ((GroundItem)object).getItem())).getAmount() >= 6 && !player.isMovementLocked()) {
                    player.setAutoRetaliate(false);
                    CombatManager.stopCombat(player);
                    player.botCombatState = "loot arrows";
                    player.getMovementQueue().setRunning(true);
                    player.botLootResumeTarget = player2;
                    BotCombatHelper.pickupBotCombatGroundItem(player, player.getEquipmentManager().getItemIdAtSlot(n3), player2.getPosition());
                    return;
                }
                if (player.getEquipmentManager().getContainer().getItemAt(n3).getAmount() <= 3 && !player.isMovementLocked()) {
                    player.setAutoRetaliate(false);
                    CombatManager.stopCombat(player);
                    player.botCombatState = "loot arrows";
                    player.getMovementQueue().setRunning(true);
                    player.botLootResumeTarget = player2;
                    BotCombatHelper.pickupBotCombatGroundItem(player, player.getEquipmentManager().getItemIdAtSlot(n3), player2.getPosition());
                    return;
                }
            }
        }
        if (!player.botStrengthPotionDepleted && player.botActiveCombatStyle == 0) {
            int n10 = player.getSkillManager().getCurrentLevels()[2];
            player.getSkillManager();
            if (n10 == SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[2])) {
                BotCombatHelper.drinkStrengthPotion(player);
                CombatManager.startCombat(player, player2);
            }
        }
        if (player.getSkillManager().getCurrentLevels()[5] > 1) {
            BotCombatHelper.updateBotDefensivePrayers(player, player2);
        }
    }
}

