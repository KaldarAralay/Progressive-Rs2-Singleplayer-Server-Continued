/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.CacheCoordinateTranslator;
import com.rs2.ServerSettings;
import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.BotTaskPlanner;
import com.rs2.bot.ClanWarsBotManager;
import com.rs2.bot.ClanWarsBotManagerTickTask;
import com.rs2.bot.DropPartyBotJoinTask;
import com.rs2.bot.DropPartyBotManager;
import com.rs2.bot.combat.BotCombatEscapeHandler;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.combat.BotCombatLoadoutManager;
import com.rs2.bot.combat.BotPvpCombatHandler;
import com.rs2.cache.CacheArchive;
import com.rs2.cache.CacheFile;
import com.rs2.cache.CacheStore;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.Entity;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.animation.GraphicEffect;
import com.rs2.model.clue.AnagramClue;
import com.rs2.model.clue.CoordinateClueHandler;
import com.rs2.model.clue.PuzzleBoxHandler;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.gameplay.PositionRange;
import com.rs2.model.gameplay.barrows.BarrowsManager;
import com.rs2.model.gameplay.godwars.GodWarsDungeonManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.action.CaveLightSourceDefinition;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.npc.NpcMovementMode;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.player.TradeState;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.randomevent.SkillRandomEventNpc;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.agility.AgilityObstacleHandler;
import com.rs2.model.skill.crafting.armor.BlackDragonhideCrafting;
import com.rs2.model.skill.crafting.armor.BlackDragonhideRecipe;
import com.rs2.model.skill.crafting.armor.BlueDragonhideCrafting;
import com.rs2.model.skill.crafting.armor.BlueDragonhideRecipe;
import com.rs2.model.skill.crafting.armor.GreenDragonhideCrafting;
import com.rs2.model.skill.crafting.armor.GreenDragonhideRecipe;
import com.rs2.model.skill.crafting.armor.HardLeatherCrafting;
import com.rs2.model.skill.crafting.armor.HardLeatherRecipe;
import com.rs2.model.skill.crafting.armor.LeatherCrafting;
import com.rs2.model.skill.crafting.armor.LeatherRecipe;
import com.rs2.model.skill.crafting.armor.RedDragonhideCrafting;
import com.rs2.model.skill.crafting.armor.RedDragonhideRecipe;
import com.rs2.model.skill.crafting.armor.SnakeskinAccessoryCrafting;
import com.rs2.model.skill.crafting.armor.SnakeskinArmorCrafting;
import com.rs2.model.skill.crafting.armor.SplitbarkCrafting;
import com.rs2.model.skill.magic.Spellbook;
import com.rs2.model.skill.runecrafting.CombinationRuneDefinition;
import com.rs2.model.skill.runecrafting.EssencePouchDefinition;
import com.rs2.model.skill.runecrafting.RunecraftingAltarDefinition;
import com.rs2.model.skill.runecrafting.RunecraftingHandler;
import com.rs2.model.task.TickTask;
import com.rs2.model.travel.ChargedJewelryDefinition;
import com.rs2.net.packet.AccessMode;
import com.rs2.net.packet.ByteOrder;
import com.rs2.net.packet.ByteTransform;
import com.rs2.net.packet.PacketBuffer;
import com.rs2.net.packet.PacketSender;
import com.rs2.net.packet.PacketWriter;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.CharacterFileManager;
import com.rs2.util.FileUtil;
import com.rs2.util.GameUtil;
import com.rs2.util.TextUtil;
import com.rs2.util.path.WalkingCollisionMap;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.base.AbstractDateTime;

public class GameplayHelper {
    private int tradeAdvertItemId;
    private int[] tradeAdvertQuantityOptions;
    public static int objectDefinitionCount;

    public static void resetBotTaskState(Player player) {
        if (player.botMode == 4) {
            if (player.botEscapeLogoutTask != null && player.botEscapeLogoutTask.isActive()) {
                player.botEscapeLogoutTask.stop();
            }
            if (player.botCombatTickTask != null && player.botCombatTickTask.isActive()) {
                player.botCombatTickTask.stop();
            }
            if (player.pendingGroupCleanup != null) {
                player.pendingGroupCleanup.finishDeferredRemoval(player);
            }
            player.botLootSellItems.clear();
            player.botLootSellGroundItems.clear();
            player.botEscapeStuckTicks = 0;
            player.botCombatEscapeActive = false;
            player.botAntipoisonAvailable = false;
            player.botWeaponItemId = 0;
            player.botPrimaryCombatStyle = 0;
            player.botActiveCombatStyle = 0;
            player.botSpecialCombatStyle = 0;
            player.botSpecialWeaponItemId = 0;
            player.botShieldItemId = 0;
            player.botSpecialAttackEnergyCost = 0;
            player.botCombatSpell = null;
            player.botMagicPenaltyGearUnequipped = false;
            player.setAutoRetaliate(true);
            player.botFoodDepleted = false;
            player.botStrengthPotionDepleted = false;
            player.setAutocastSpell(null);
            player.getPrayerManager().deactivateAll();
            player.botCombatState = null;
            return;
        }
        if (player.botEscapeLogoutTask != null && player.botEscapeLogoutTask.isActive()) {
            player.botEscapeLogoutTask.stop();
        }
        if (player.botCombatTickTask != null && player.botCombatTickTask.isActive()) {
            player.botCombatTickTask.stop();
        }
        if (player.pendingGroupCleanup != null) {
            player.pendingGroupCleanup.finishDeferredRemoval(player);
        }
        player.botEatDelayTicks = 0;
        player.botWeaponSwapDelayTicks = 0;
        player.botMagicGearSwapDelayTicks = 0;
        player.botThreatEscapeDelayTicks = 0;
        player.botPrayerSwitchDelayTicks = 0;
        player.botQueuedPrayerId = -1;
        player.botLootSellItems.clear();
        player.botLootSellGroundItems.clear();
        player.botEscapeStuckTicks = 0;
        player.botCombatEscapeActive = false;
        player.botAntipoisonAvailable = false;
        player.botWeaponItemId = 0;
        player.botPrimaryCombatStyle = 0;
        player.botActiveCombatStyle = 0;
        player.botSpecialCombatStyle = 0;
        player.botSpecialWeaponItemId = 0;
        player.botShieldItemId = 0;
        player.botSpecialAttackEnergyCost = 0;
        Player player2 = player;
        player2.packetSender.setSidebarInterface(6, 1151);
        player.setSpellbook(Spellbook.MODERN);
        player.botCombatSpell = null;
        player.botMagicPenaltyGearUnequipped = false;
        player.setAutoRetaliate(true);
        player.clearPvpCombatReferences();
        player.getRecentCombatTimer().setDelayTicks(0);
        player.getRecentCombatTimer().reset();
        player.getSingleCombatTimer().setDelayTicks(0);
        player.getSingleCombatTimer().reset();
        player.botFoodDepleted = false;
        player.botStrengthPotionDepleted = false;
        player.setAutocastSpell(null);
        player.getMovementQueue().setRunning(false);
        player.setRunEnergyPercent(100);
        player.setSpecialEnergy(100);
        player.setPoisonDamage(0.0);
        player.getPrayerManager().deactivateAll();
        player.botCombatState = null;
        player.clearCombatEffectTasks();
        player.eq();
        player.getDamageContributions().clear();
    }

    public static void resetBotSkillsToBase(Player player) {
        int n = 0;
        while (n < player.getSkillManager().getCurrentLevels().length) {
            if (n == 3) {
                player.getSkillManager().getCurrentLevels()[n] = 10;
                player.getSkillManager().getExperience()[n] = 1154.0;
            } else {
                player.getSkillManager().getCurrentLevels()[n] = 1;
                player.getSkillManager().getExperience()[n] = 0.0;
            }
            ++n;
        }
    }

    public static void selectAndStartProgressiveBotTask(Player player, boolean bl) {
        int n;
        boolean bl2;
        Object object = null;
        boolean bl3 = false;
        if (bl) {
            if (BotTaskDefinition.cookingTasks.contains(player.currentBotTask)) {
                if (!BotTaskPlanner.prepareCookingTaskRequirements(player)) {
                    bl3 = false;
                } else {
                    object = player.currentBotTask;
                    bl3 = true;
                }
            } else if (BotTaskDefinition.spinningTasks.contains(player.currentBotTask)) {
                if (!BotTaskPlanner.prepareSpinningTaskRequirements(player)) {
                    bl3 = false;
                } else {
                    object = player.currentBotTask;
                    bl3 = true;
                }
            } else if (BotTaskDefinition.tanningTasks.contains(player.currentBotTask)) {
                if (!BotTaskPlanner.prepareTanningTaskRequirements(player, false)) {
                    bl3 = false;
                } else {
                    object = player.currentBotTask;
                    bl3 = true;
                }
            } else if (BotTaskDefinition.leatherCraftingTasks.contains(player.currentBotTask)) {
                if (!BotTaskPlanner.prepareLeatherCraftingTaskRequirements(player)) {
                    bl3 = false;
                } else {
                    object = player.currentBotTask;
                    bl3 = true;
                    Object object2 = ((BotTaskDefinition)object).getMissingRequiredItems(player);
                    bl2 = true;
                    if (((ArrayList)object2).size() > 0) {
                        if (((ItemStack)((ArrayList)object2).get(0)).getId() == 983) {
                            player.deferredBotTask = (BotTaskDefinition)object;
                            object = (BotTaskDefinition)BotTaskDefinition.brassKeyTasks.get(0);
                            bl2 = true;
                        } else if ((object2 = BotTaskPlanner.selectShopPurchaseTask(player, ((ItemStack)((ArrayList)object2).get(0)).getId(), ((ItemStack)((ArrayList)object2).get(0)).getAmount())) != null) {
                            BotTaskPlanner.resetBotTaskGoals(player);
                            player.deferredBotTask = (BotTaskDefinition)object;
                            object = object2;
                            bl2 = true;
                        } else {
                            bl2 = false;
                        }
                    }
                    if (!bl2) {
                        bl3 = false;
                    }
                }
            } else if (BotTaskDefinition.smeltingTasks.contains(player.currentBotTask)) {
                if (!BotTaskPlanner.hasSmeltingTaskMaterial(player)) {
                    bl3 = false;
                } else {
                    object = player.currentBotTask;
                    bl3 = true;
                }
            } else if (BotTaskDefinition.smithingTasks.contains(player.currentBotTask)) {
                if (!BotTaskPlanner.prepareSmithingTaskRequirements(player)) {
                    bl3 = false;
                } else {
                    object = player.currentBotTask;
                    bl3 = true;
                }
            }
        }
        while (!bl3) {
            if (player.deferredBotTask != null) {
                object = player.deferredBotTask;
                player.deferredBotTask = null;
            } else {
                BotTaskDefinition botTaskDefinition;
                Player player2 = player;
                object = new ArrayList();
                bl2 = false;
                boolean bl4 = false;
                if (BotTaskDefinition.woodcuttingTasks.contains(player2.currentBotTask) || BotTaskDefinition.smithingTasks.contains(player2.currentBotTask) || BotTaskDefinition.combatTasks.contains(player2.currentBotTask) || player2.currentBotTask == BotTaskDefinition.moneyMakingTasks.get(1) || BotTaskDefinition.leatherCraftingTasks.contains(player2.currentBotTask)) {
                    BotTaskPlanner.populateBotShopSellItemIds(player2);
                    if (player2.botShopSellItemIds.size() != 0) {
                        bl4 = true;
                    }
                }
                if (BotTaskDefinition.fishingTasks.contains(player2.currentBotTask)) {
                    ((ArrayList)object).addAll(BotTaskDefinition.cookingTasks);
                    if (player2.currentBotTask != BotTaskDefinition.fishingTasks.get(5)) {
                        ((ArrayList)object).remove(BotTaskDefinition.cookingTasks.get(2));
                    }
                    bl2 = true;
                } else if (BotTaskDefinition.miningTasks.contains(player2.currentBotTask)) {
                    ((ArrayList)object).addAll(BotTaskDefinition.smeltingTasks);
                    bl2 = true;
                } else if (BotTaskDefinition.tanningTasks.contains(player2.currentBotTask)) {
                    ((ArrayList)object).addAll(BotTaskDefinition.leatherCraftingTasks);
                } else if (BotTaskDefinition.smeltingTasks.contains(player2.currentBotTask)) {
                    ((ArrayList)object).addAll(BotTaskDefinition.smithingTasks);
                    bl2 = true;
                } else if (BotTaskDefinition.sheepShearingTasks.contains(player2.currentBotTask) || player2.currentBotTask == BotTaskDefinition.moneyMakingTasks.get(2)) {
                    ((ArrayList)object).addAll(BotTaskDefinition.spinningTasks);
                    bl2 = true;
                } else if (bl4) {
                    ((ArrayList)object).addAll(BotTaskDefinition.getLootSellShopTasks());
                    player2.botShopBuyMode = 0;
                    bl2 = true;
                } else if (BotTaskDefinition.runecraftingTasks.get(0) == player2.currentBotTask) {
                    ((ArrayList)object).addAll(BotTaskDefinition.runecraftingTasks);
                    ((ArrayList)object).remove(BotTaskDefinition.runecraftingTasks.get(0));
                } else {
                    ((ArrayList)object).addAll(BotTaskDefinition.miningTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.fishingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.woodcuttingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.combatTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.moneyMakingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.runecraftingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.sheepShearingTasks);
                    if (BotTaskPlanner.prepareTanningTaskRequirements(player2, true)) {
                        ((ArrayList)object).addAll(BotTaskDefinition.tanningTasks);
                    }
                }
                BotTaskDefinition botTaskDefinition2 = GameplayHelper.selectAvailableBotTask(player2, (ArrayList)object, bl2);
                if (botTaskDefinition2 != null) {
                    botTaskDefinition = botTaskDefinition2;
                } else {
                    ((ArrayList)object).clear();
                    ((ArrayList)object).addAll(BotTaskDefinition.miningTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.fishingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.woodcuttingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.combatTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.moneyMakingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.runecraftingTasks);
                    ((ArrayList)object).addAll(BotTaskDefinition.sheepShearingTasks);
                    if (BotTaskPlanner.prepareTanningTaskRequirements(player2, true)) {
                        ((ArrayList)object).addAll(BotTaskDefinition.tanningTasks);
                    }
                    botTaskDefinition = GameplayHelper.selectAvailableBotTask(player2, (ArrayList)object, false);
                }
                object = botTaskDefinition;
            }
            Object object3 = ((BotTaskDefinition)object).getMissingRequiredItems(player);
            bl2 = true;
            if (((ArrayList)object3).size() > 0) {
                if (((ItemStack)((ArrayList)object3).get(0)).getId() == 983) {
                    player.deferredBotTask = (BotTaskDefinition)object;
                    object = (BotTaskDefinition)BotTaskDefinition.brassKeyTasks.get(0);
                    bl2 = true;
                } else if ((object3 = BotTaskPlanner.selectShopPurchaseTask(player, ((ItemStack)((ArrayList)object3).get(0)).getId(), ((ItemStack)((ArrayList)object3).get(0)).getAmount())) != null) {
                    BotTaskPlanner.resetBotTaskGoals(player);
                    player.deferredBotTask = (BotTaskDefinition)object;
                    object = object3;
                    bl2 = true;
                } else {
                    bl2 = false;
                }
            }
            if (BotTaskDefinition.cookingTasks.contains(object)) {
                if (!BotTaskPlanner.prepareCookingTaskRequirements(player)) {
                    Object var4_10 = null;
                    object3 = player;
                    player.currentBotTask = null;
                    bl2 = false;
                }
            } else if (BotTaskDefinition.spinningTasks.contains(object)) {
                if (!BotTaskPlanner.prepareSpinningTaskRequirements(player)) {
                    Object var4_11 = null;
                    object3 = player;
                    player.currentBotTask = null;
                    bl2 = false;
                }
            } else if (BotTaskDefinition.tanningTasks.contains(object)) {
                if (!BotTaskPlanner.prepareTanningTaskRequirements(player, false)) {
                    Object var4_12 = null;
                    object3 = player;
                    player.currentBotTask = null;
                    bl2 = false;
                }
            } else if (BotTaskDefinition.leatherCraftingTasks.contains(object)) {
                if (!BotTaskPlanner.prepareLeatherCraftingTaskRequirements(player)) {
                    Object var4_13 = null;
                    object3 = player;
                    player.currentBotTask = null;
                    bl2 = false;
                }
            } else if (BotTaskDefinition.smeltingTasks.contains(object)) {
                if (!BotTaskPlanner.hasSmeltingTaskMaterial(player)) {
                    Object var4_14 = null;
                    object3 = player;
                    player.currentBotTask = null;
                    bl2 = false;
                }
            } else if (BotTaskDefinition.smithingTasks.contains(object) && !BotTaskPlanner.prepareSmithingTaskRequirements(player)) {
                Object var4_15 = null;
                object3 = player;
                player.currentBotTask = null;
                bl2 = false;
            }
            if (!bl2) continue;
            bl3 = true;
        }
        player.botTaskReturnToBankRequested = false;
        BotTaskDefinition botTaskDefinition = (BotTaskDefinition)object;
        Player player3 = player;
        player.currentBotTask = botTaskDefinition;
        BotTaskPlanner.configureCurrentBotTaskGoals(player);
        if (player.botShopSellItemIds.size() == 0) {
            BotTaskPlanner.prepareDeferredUpgradePurchase(player);
        }
        player.botEnabled = true;
        player.currentBotTask.startTask(player);
        player.botTaskStartTimeMillis = System.currentTimeMillis();
        player.botTaskSavedElapsedMillis = 0L;
        player.botTaskDurationMinutes = n = 30 + GameUtil.randomInt(60);
        if (player.currentBotTask.usesEscapeMonitor) {
            player.currentBotTask.startEscapeMonitor(player);
        }
    }

    public static void selectAndStartNextProgressiveBotTask(Player player) {
        GameplayHelper.selectAndStartProgressiveBotTask(player, false);
    }

    public static void startNextBotTask(Player player) {
        BotTaskDefinition currentTask = player.currentBotTask;
        double totalWeight = 0.0;
        ArrayList<BotTaskDefinition> availableTasks = new ArrayList<BotTaskDefinition>();
        ArrayList taskPool = player.botMode == 2 ? BotTaskDefinition.tradeAdvertTaskPool : BotTaskDefinition.progressiveTaskPool;
        Iterator taskIterator = taskPool.iterator();
        while (taskIterator.hasNext()) {
            BotTaskDefinition botTaskDefinition = (BotTaskDefinition)taskIterator.next();
            if (currentTask != null && botTaskDefinition == currentTask) continue;
            availableTasks.add(botTaskDefinition);
            totalWeight += (double)botTaskDefinition.selectionWeight;
        }
        double roll = Math.random() * totalWeight;
        double cumulativeWeight = 0.0;
        BotTaskDefinition selectedTask = null;
        Iterator availableIterator = availableTasks.iterator();
        while (availableIterator.hasNext()) {
            BotTaskDefinition botTaskDefinition = (BotTaskDefinition)availableIterator.next();
            cumulativeWeight += (double)botTaskDefinition.selectionWeight;
            if (cumulativeWeight < roll) continue;
            selectedTask = botTaskDefinition;
            break;
        }
        if (selectedTask == null) {
            selectedTask = availableTasks.size() > 0 ? (BotTaskDefinition)availableTasks.get(0) : currentTask;
        }
        if (player.currentBotTask != null) {
            player.currentBotTask.assignedBotPlayers.remove(player);
        }
        player.botTaskReturnToBankRequested = false;
        player.botEnabled = true;
        selectedTask.assignedBotPlayers.add(player);
        player.currentBotTask = selectedTask;
        if (!BotTaskDefinition.shopTasks.contains(selectedTask)) {
            player.botTaskRequiredItems = null;
        }
        selectedTask.startTask(player);
        player.botTaskStartTimeMillis = System.currentTimeMillis();
        int durationRange = player.botMode == 2 ? 16 : 106;
        player.botTaskDurationMinutes = 15 + GameUtil.randomInt(durationRange);
        if (player.currentBotTask.usesEscapeMonitor) {
            player.currentBotTask.startEscapeMonitor(player);
        }
    }

    public static void startDropPartyBotEvent() {
        if (DropPartyBotManager.dropPartyActive) {
            return;
        }
        if (ServerSettings.otherBotCount < DropPartyBotManager.baseDropPartySize) {
            DropPartyBotManager.baseDropPartySize = ServerSettings.otherBotCount;
        }
        DropPartyBotManager.dropPartyParticipants.clear();
        BotTaskDefinition.dropPartyBotJoinIndex = 0;
        DropPartyBotManager.dropPartyActive = true;
        int n = GameUtil.randomInt(BotTaskDefinition.dropPartyTaskPool.size());
        Object object = (BotTaskDefinition)BotTaskDefinition.dropPartyTaskPool.get(n);
        World.getTaskScheduler().schedule(new DropPartyBotJoinTask(1, (BotTaskDefinition)object));
    }

    public static void startClanWarsBotEvent() {
        if (ClanWarsBotManager.clanWarsEventActive) {
            return;
        }
        ClanWarsBotManager.clanWarsEventActive = true;
        ClanWarsBotManager.clanWarsBaseCombatLevel = 35 + GameUtil.randomInt(45);
        ClanWarsBotManager.chooseClanWarsTeamTags();
        ClanWarsBotManager.chooseClanWarsTeamCapes();
        ClanWarsBotManager.startClanWarsCombatants();
        ClanWarsBotManagerTickTask clanWarsBotManagerTickTask = new ClanWarsBotManagerTickTask(10);
        World.getTaskScheduler().schedule(clanWarsBotManagerTickTask);
    }

    public static boolean shouldReturnToBankForBotTask(Player player) {
        if (player.botMode != 4) {
            if (System.currentTimeMillis() >= player.botTaskStartTimeMillis + (long)GameUtil.minutesToMillis(player.botTaskDurationMinutes)) {
                player.botTaskReturnToBankRequested = true;
                return true;
            }
        } else {
            if (player.botTaskSavedElapsedMillis + player.getBotTaskRuntimeMillis() >= (long)GameUtil.minutesToMillis(player.botTaskDurationMinutes)) {
                player.botTaskReturnToBankRequested = true;
                return true;
            }
            if (player.botSkillTargetSkillId != -1 && player.getSkillManager().getCurrentLevels()[player.botSkillTargetSkillId] >= player.botSkillTargetLevel) {
                player.botTaskReturnToBankRequested = true;
                return true;
            }
            if (player.botCompletionItemId != -1 && player.ownsItemAmount(player.botCompletionItemId, player.botCompletionItemAmount)) {
                player.botTaskReturnToBankRequested = true;
                return true;
            }
        }
        return false;
    }

    public static void prepareBotCombatStyle(Player player, int n) {
        player.botCombatStyle = n;
        if (n == -1) {
            BotCombatLoadoutManager.selectCombatStyleFromStats(player, true);
            n = player.botCombatStyle;
        }
        if (n == 0) {
            player.botActiveCombatStyle = player.botPrimaryCombatStyle = 0;
            BotCombatLoadoutManager.prepareMeleeLoadout(player);
            BotCombatLoadoutManager.equipGlovesAndBoots(player);
        } else if (n == 2) {
            player.botActiveCombatStyle = player.botPrimaryCombatStyle = BotPvpCombatHandler.MAGIC_COMBAT_STYLE;
            BotCombatLoadoutManager.prepareMagicLoadout(player);
        } else if (n == 1) {
            player.botActiveCombatStyle = player.botPrimaryCombatStyle = BotPvpCombatHandler.RANGED_COMBAT_STYLE;
            BotCombatLoadoutManager.prepareRangedLoadout(player);
            int n2 = player.getSkillManager().getCurrentLevels()[4] >= 40 ? 1731 : (n = GameUtil.randomInt(3) == 0 ? 1478 : 1729);
            if (!BotCombatHelper.isFreeToPlayWorld() && player.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
                n = 1712;
            }
            player.getEquipmentManager().getContainer().setItem(2, new ItemStack(n));
        }
        BotCombatLoadoutManager.equipRandomCape(player);
    }

    public static BotTaskDefinition selectAvailableBotTask(Player player, ArrayList tasks, boolean nearestStart) {
        if (nearestStart) {
            int closestDistance = 1000;
            BotTaskDefinition closestTask = null;
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                BotTaskDefinition task = (BotTaskDefinition)iterator.next();
                if (!task.isAvailableFor(player, false)) continue;
                int distance = GameUtil.getDistance(player.getPosition(), task.startPosition);
                if (distance >= closestDistance) continue;
                closestDistance = distance;
                closestTask = task;
            }
            return closestTask;
        }
        double totalWeight = 0.0;
        BotTaskDefinition currentTask = player.currentBotTask;
        ArrayList<BotTaskDefinition> availableTasks = new ArrayList<BotTaskDefinition>();
        Iterator iterator = tasks.iterator();
        while (iterator.hasNext()) {
            BotTaskDefinition task = (BotTaskDefinition)iterator.next();
            if (currentTask != null && task == currentTask || !task.isAvailableFor(player, false)) continue;
            availableTasks.add(task);
            totalWeight += (double)task.selectionWeight;
        }
        double roll = Math.random() * totalWeight;
        double cumulativeWeight = 0.0;
        for (BotTaskDefinition task : availableTasks) {
            cumulativeWeight += (double)task.selectionWeight;
            if (cumulativeWeight < roll) continue;
            return task;
        }
        return null;
    }

    public static void startBotTaskRoute(Player player) {
        BotTaskDefinition botTaskDefinition = player.currentBotTask;
        player.botTaskState = "worldwalk to bank";
        player.botTaskStartTimeMillis = System.currentTimeMillis();
        player.botTaskDurationMinutes = 15;
        player.botTaskSavedElapsedMillis = 0L;
        BotRoute botRoute = botTaskDefinition.taskRoute != null ? new BotRoute(new Position[]{botTaskDefinition.taskRoute.waypoints[0], botTaskDefinition.startPosition}) : new BotRoute(new Position[]{botTaskDefinition.taskRouteSegments[0].waypoints[0], botTaskDefinition.startPosition});
        int n = GameUtil.getDistance(player.getPosition(), botRoute.getStartPosition());
        int n2 = GameUtil.getDistance(player.getPosition(), botTaskDefinition.startPosition);
        if (n2 <= n) {
            botRoute = new BotRoute(new Position[]{botTaskDefinition.startPosition});
        }
        player.currentBotRoute = botRoute;
        player.botPathWaypointIndex = 0;
        player.continueBotRoute();
    }

    public GameplayHelper(int n, int[] nArray) {
        this.tradeAdvertItemId = n;
        this.tradeAdvertQuantityOptions = nArray;
    }

    public int getTradeAdvertItemId() {
        return this.tradeAdvertItemId;
    }

    public int[] getTradeAdvertQuantityOptions() {
        return this.tradeAdvertQuantityOptions;
    }

    public int getPreviousTradeAdvertQuantityOption(int n) {
        if (n > 0) {
            return this.tradeAdvertQuantityOptions[n - 1];
        }
        return -1;
    }

    public static boolean isObjectDefinitionIdValid(int n) {
        return n < objectDefinitionCount;
    }

    public static void loadObjectDefinitions() {
        CacheStore cacheStore = CacheStore.getInstance();
        ByteArrayReader reader = null;
        try {
            reader = new ByteArrayReader(new CacheArchive(cacheStore.readFile(0, 2)).getFileBytes("loc.dat"));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        GameplayHelper.objectDefinitionCount = cacheStore.getDefinitionIndex().getObjectDefinitionEntries().length;
        ObjectDefinition.definitionsById = new ObjectDefinition[objectDefinitionCount];
        reader.position = 2;
        int objectId = 0;
        while (objectId < objectDefinitionCount) {
            reader.position = cacheStore.getDefinitionIndex().getObjectDefinitionEntry(objectId).getDataOffset();
            ObjectDefinition definition = ObjectDefinition.forId(objectId);
            int collisionType = 2;
            definition.name = null;
            definition.description = null;
            definition.length = 1;
            definition.width = 1;
            definition.solid = true;
            definition.h = true;
            definition.interactive = false;
            definition.blocksProjectiles = true;
            definition.b = 0;
            while (true) {
                int opcode = reader.readUnsignedByte();
                if (opcode == 0) {
                    if (definition.name == null) {
                        definition.name = "";
                    }
                    break;
                }
                if (opcode == 1) {
                    int count = reader.readUnsignedByte();
                    int i = 0;
                    while (i < count) {
                        reader.readUnsignedShort();
                        reader.readUnsignedByte();
                        ++i;
                    }
                    continue;
                }
                if (opcode == 2) {
                    definition.name = reader.readString();
                    continue;
                }
                if (opcode == 3) {
                    definition.description = new String(reader.readLineBytes());
                    continue;
                }
                if (opcode == 5) {
                    int count = reader.readUnsignedByte();
                    int i = 0;
                    while (i < count) {
                        reader.readUnsignedShort();
                        ++i;
                    }
                    continue;
                }
                if (opcode == 14) {
                    definition.width = reader.readUnsignedByte();
                    continue;
                }
                if (opcode == 15) {
                    definition.length = reader.readUnsignedByte();
                    continue;
                }
                if (opcode == 17) {
                    definition.solid = false;
                    collisionType = 0;
                    continue;
                }
                if (opcode == 18) {
                    definition.h = false;
                    continue;
                }
                if (opcode == 19) {
                    definition.interactive = reader.readUnsignedByte() == 1;
                    continue;
                }
                if (opcode == 21 || opcode == 22 || opcode == 23) continue;
                if (opcode == 24) {
                    reader.readUnsignedShort();
                    continue;
                }
                if (opcode == 28) {
                    reader.readUnsignedByte();
                    continue;
                }
                if (opcode == 29) {
                    reader.readByte();
                    continue;
                }
                if (opcode == 39) {
                    reader.readByte();
                    continue;
                }
                if (opcode >= 30 && opcode < 39) {
                    reader.readString();
                    definition.interactive = true;
                    continue;
                }
                if (opcode == 40) {
                    int count = reader.readUnsignedByte();
                    int i = 0;
                    while (i < count) {
                        reader.readUnsignedShort();
                        reader.readUnsignedShort();
                        ++i;
                    }
                    continue;
                }
                if (opcode == 60) {
                    reader.readUnsignedShort();
                    continue;
                }
                if (opcode == 62) continue;
                if (opcode == 64) {
                    definition.blocksProjectiles = false;
                    continue;
                }
                if (opcode == 65) {
                    reader.readUnsignedShort();
                    continue;
                }
                if (opcode == 66) {
                    reader.readUnsignedShort();
                    continue;
                }
                if (opcode == 67) {
                    reader.readUnsignedShort();
                    continue;
                }
                if (opcode == 68) {
                    reader.readUnsignedShort();
                    continue;
                }
                if (opcode == 69) {
                    definition.b = reader.readUnsignedByte();
                    continue;
                }
                if (opcode == 70) {
                    reader.readShort();
                    continue;
                }
                if (opcode == 71) {
                    reader.readShort();
                    continue;
                }
                if (opcode == 72) {
                    reader.readShort();
                    continue;
                }
                if (opcode == 73 || opcode == 74) continue;
                if (opcode == 75) {
                    reader.readUnsignedByte();
                    continue;
                }
                if (opcode == 77) {
                    reader.readUnsignedShort();
                    reader.readUnsignedShort();
                    int count = reader.readUnsignedByte();
                    int i = 0;
                    while (i <= count) {
                        reader.readUnsignedShort();
                        ++i;
                    }
                }
            }
            definition.k = collisionType <= 1 || collisionType == 2 && !definition.solid;
            definition.projectileCollisionIgnored = definition.isProjectileCollisionIgnored();
            ++objectId;
        }
    }

    public static byte[] inflateGzipCacheFile(CacheFile cacheFile) {
        byte[] compressedBytes = new byte[cacheFile.getBuffer().remaining()];
        cacheFile.getBuffer().get(compressedBytes);
        try {
            byte[] inflatedBuffer = new byte[999999];
            int length = 0;
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedBytes));
            while (true) {
                if (length == 999999) {
                    System.out.println("Error inflating data.\nGZIP buffer overflow.");
                    break;
                }
                int readCount = gzipInputStream.read(inflatedBuffer, length, 999999 - length);
                if (readCount == -1) break;
                length += readCount;
            }
            byte[] inflatedBytes = new byte[length];
            System.arraycopy(inflatedBuffer, 0, inflatedBytes, 0, length);
            if (inflatedBytes.length < 10) {
                return null;
            }
            return inflatedBytes;
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static int getNpcShopId(int n) {
        NpcDefinition npcDefinition = NpcDefinition.forId(n);
        int n2 = npcDefinition.getShopId();
        if (n2 >= 0) {
            return n2;
        }
        return -1;
    }

    public static boolean openNpcShop(Player player, int n) {
        if ((n = GameplayHelper.getNpcShopId(n)) >= 0) {
            ShopManager.openShop(player, n);
            return true;
        }
        return false;
    }

    public static void refreshPlayerAreaOverlay(Player player) {
        Player player2;
        if (player.isInWilderness()) {
            player.aj = true;
        }
        if ((player2 = player).isInWilderness()) {
            Player player3;
            if (GameplayHelper.updateWalkableInterface(player2, 197)) {
                player3 = player2;
                player3.packetSender.sendPlayerOption("Attack", 1, false);
            }
            if (player2.ch != player2.getWildernessLevel()) {
                player3 = player2;
                player3.packetSender.sendInterfaceText("@yel@Level: " + player2.getWildernessLevel(), 199);
                player2.ch = player2.getWildernessLevel();
            }
        } else if (player2.isInDuelArena()) {
            Player player4 = player2;
            player4.packetSender.sendPlayerOption("Attack", 1, false);
            GameplayHelper.updateWalkableInterface(player2, 201);
        } else if (player2.isInDuelArenaLobby()) {
            Player player5 = player2;
            player5.packetSender.sendPlayerOption("Challenge", 1, false);
            GameplayHelper.updateWalkableInterface(player2, 201);
        } else if (player2.isInBarrows()) {
            Player player6;
            if (player2.ci != player2.getBarrowsKillCount()) {
                player6 = player2;
                player6.packetSender.sendInterfaceText("Kill count: " + player2.getBarrowsKillCount(), 4536);
                player2.ci = player2.getBarrowsKillCount();
            }
            BarrowsManager.ensurePrayerDrainTask(player2);
            if (GameplayHelper.updateWalkableInterface(player2, 4535)) {
                player6 = player2;
                player6.packetSender.sendPlayerOption("null", 1, false);
                player6 = player2;
                player6.packetSender.sendMinimapState(2);
            }
        } else {
            Object object = player2;
            if (((Entity)object).isInArea(2816, 2943, 5248, 5375)) {
                GodWarsDungeonManager.refreshKillCountOverlay(player2);
                if (GameplayHelper.updateWalkableInterface(player2, 19556)) {
                    object = player2;
                    ((Player)object).packetSender.sendPlayerOption("null", 1, false);
                }
            } else {
                object = player2;
                if (((Entity)object).isInArea(2494, 2569, 3701, 3785)) {
                    if (GameplayHelper.updateWalkableInterface(player2, 11877)) {
                        object = player2;
                        ((Player)object).packetSender.sendPlayerOption("null", 1, false);
                    }
                } else {
                    object = player2;
                    if (((Entity)object).isInArea(3136, 3263, 9536, 9599)) {
                        int n = -1;
                        if (player2.getActiveCaveLightLevel() == 0) {
                            n = 12414;
                        } else if (player2.getActiveCaveLightLevel() == 1) {
                            n = 12418;
                        } else if (player2.getActiveCaveLightLevel() == 2) {
                            n = 12416;
                        }
                        if (n != -1 && GameplayHelper.updateWalkableInterface(player2, n)) {
                            Player player7 = player2;
                            player7.packetSender.sendPlayerOption("null", 1, false);
                        }
                    } else if (player2.isInSmokeDungeon()) {
                        if (GameplayHelper.updateWalkableInterface(player2, 13103)) {
                            object = player2;
                            ((Player)object).packetSender.sendPlayerOption("null", 1, false);
                        }
                    } else if (GameplayHelper.updateWalkableInterface(player2, -1)) {
                        object = player2;
                        ((Player)object).packetSender.sendPlayerOption("null", 1, false);
                        object = InterfaceDefinition.forId(11092);
                        if (!player2.isInterfaceOpen((InterfaceDefinition)object)) {
                            object = player2;
                            ((Player)object).packetSender.sendMinimapState(0);
                        }
                    }
                }
            }
        }
        if ((player2 = player).isInMultiCombatArea()) {
            Player player8 = player2;
            player8.packetSender.sendMultiwayAreaState(true);
        } else {
            Player player9 = player2;
            player9.packetSender.sendMultiwayAreaState(false);
        }
        if (player.getAlchemistPlaygroundController().isInsidePlayground()) {
            player.getAlchemistPlaygroundController().refreshPizazzInterface();
        }
        if (player.getEnchantmentChamberController().isInsideChamber()) {
            player.getEnchantmentChamberController().refreshPizazzInterface();
        }
        if (player.getTelekineticTheatreController().isInsideTheatre()) {
            player.getTelekineticTheatreController().refreshPizazzInterface();
        }
        if (player.getCreatureGraveyardController().isInsideGraveyard()) {
            player.getCreatureGraveyardController().refreshPizazzInterface();
        }
    }

    public static void refreshRubberChickenPlayerOption(Player player) {
        if (player.getEquipmentManager().getItemIdAtSlot(3) == 4566) {
            player.packetSender.sendPlayerOption("Whack", 5, false);
            return;
        }
        player.packetSender.sendPlayerOption("null", 5, false);
    }

    public static boolean updateWalkableInterface(Player player, int n) {
        if (player.getCurrentWalkableInterfaceId() == n) {
            return false;
        }
        if (player.barrowsChestOpened && n == 4535) {
            return true;
        }
        player.setCurrentWalkableInterfaceId(n);
        player.packetSender.showWalkableInterface(n);
        return true;
    }

    public static Position randomUnblockedPositionInRange(PositionRange positionRange) {
        Position position = new Position(positionRange.getMinPosition().getX() + GameUtil.randomInclusive(positionRange.getMaxPosition().getX() - positionRange.getMinPosition().getX()), positionRange.getMinPosition().getY() + GameUtil.randomInclusive(positionRange.getMaxPosition().getY() - positionRange.getMinPosition().getY()), positionRange.getMinPosition().getPlane());
        while (WalkingCollisionMap.getTileFlags(position.getX(), position.getY(), position.getPlane()) != 0) {
            position = new Position(positionRange.getMinPosition().getX() + GameUtil.randomInclusive(positionRange.getMaxPosition().getX() - positionRange.getMinPosition().getX()), positionRange.getMinPosition().getY() + GameUtil.randomInclusive(positionRange.getMaxPosition().getY() - positionRange.getMinPosition().getY()), positionRange.getMinPosition().getPlane());
        }
        return position;
    }

    public static int getRandomEventCombatLevelOffset(int n) {
        if (n < 10) {
            return 0;
        }
        if (n < 20) {
            return 1;
        }
        if (n < 40) {
            return 2;
        }
        if (n < 70) {
            return 3;
        }
        if (n < 110) {
            return 4;
        }
        return 5;
    }

    public static void spawnSkillRandomEventNpc(Player player, SkillRandomEventNpc skillRandomEventNpc) {
        Player player2 = player;
        if (player2.H != null) {
            player2 = player;
            if (!player2.H.isDead()) {
                return;
            }
        }
        GameplayHelper.spawnOwnedNpcAdjacentToPlayer(player, new Npc(skillRandomEventNpc.getBaseNpcId() + GameplayHelper.getRandomEventCombatLevelOffset(player.getCombatLevel())), true, true);
    }

    public static void openProductionInterface(Player object, String string) {
        Object object2;
        if (string == "potteryUnfired") {
            if (!ItemDefinition.isDefined(4438)) {
                GameplayHelper.showThreeOptionProductionInterface((Player)object, 1787, 1789, 1791, "Pot", "Pie Dish", "Bowl");
            } else {
                GameplayHelper.showFiveOptionProductionInterface((Player)object, 1787, 1789, 1791, 5352, 4438, "Pot", "Pie Dish", "Bowl", "Plant pot", "Pot lid");
            }
        } else if (string == "potteryFired") {
            if (!ItemDefinition.isDefined(4438)) {
                GameplayHelper.showThreeOptionProductionInterface((Player)object, 1931, 2313, 1923, "Pot", "Pie Dish", "Bowl");
            } else {
                GameplayHelper.showFiveOptionProductionInterface((Player)object, 1931, 2313, 1923, 5350, 4440, "Pot", "Pie Dish", "Bowl", "Plant pot", "Pot lid");
            }
        } else if (string == "silverCrafting") {
            if (!ItemDefinition.isDefined(5525)) {
                GameplayHelper.showTwoOptionProductionInterface((Player)object, 1714, 2961, "Unstrung symbol", "Silver sickle");
            } else {
                GameplayHelper.showThreeOptionProductionInterface((Player)object, 1714, 2961, 5525, "Unstrung symbol", "Silver sickle", "Tiara");
            }
        } else if (string == "spinning") {
            if (!ItemDefinition.isDefined(6051)) {
                GameplayHelper.showTwoOptionProductionInterface((Player)object, 1737, 1779, "Wool", "Flax");
            } else {
                GameplayHelper.showThreeOptionProductionInterface((Player)object, 1737, 1779, 6051, "Wool", "Flax", "Magic tree");
            }
        } else if (string == "glassMaking") {
            if (!ItemDefinition.isDefined(4529)) {
                GameplayHelper.showThreeOptionProductionInterface((Player)object, 229, 567, 1919, "Vial", "Orb", "Beer Glass");
            } else if (!ItemDefinition.isDefined(6667)) {
                object2 = object;
                ((Player)object2).packetSender.showChatboxInterface(11462);
            } else {
                object2 = object;
                ((Player)object2).packetSender.showInterface(11462);
            }
        } else if (string == "normalLeather") {
            object2 = object;
            ((Player)object2).packetSender.showInterface(2311);
        } else if (string == "hardLeather") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 1131, "Hard leather body");
        } else if (string == "dramenBranch") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 772, "Dramen staff");
        } else if (string == "greenLeather") {
            GameplayHelper.showThreeOptionProductionInterface((Player)object, 1065, 1099, 1135, "Vamb", "Chaps", "Body");
        } else if (string == "blueLeather") {
            GameplayHelper.showThreeOptionProductionInterface((Player)object, 2487, 2493, 2499, "Vamb", "Chaps", "Body");
        } else if (string == "redLeather") {
            GameplayHelper.showThreeOptionProductionInterface((Player)object, 2489, 2495, 2501, "Vamb", "Chaps", "Body");
        } else if (string == "blackLeather") {
            GameplayHelper.showThreeOptionProductionInterface((Player)object, 2491, 2497, 2503, "Vamb", "Chaps", "Body");
        } else if (string == "snakeskin1") {
            GameplayHelper.showTwoOptionProductionInterface((Player)object, 6326, 6328, "Bandana", "Boots");
        } else if (string == "snakeskin2") {
            GameplayHelper.showThreeOptionProductionInterface((Player)object, 6330, 6324, 6322, "Vamb", "Chaps", "Body");
        } else if (string == "weaving") {
            Object object3;
            String string2 = "Basket";
            String string3 = "Empty sack";
            String string4 = "Cloth";
            int n = 5376;
            n = 5418;
            n = 3224;
            object2 = object3 = object;
            ((Player)object3).packetSender.sendInterfaceText(string4, 8889);
            object2 = object3;
            ((Player)object2).packetSender.sendInterfaceText(string3, 8893);
            object2 = object3;
            ((Player)object2).packetSender.sendInterfaceText(string2, 8897);
            object2 = object3;
            ((Player)object2).packetSender.sendInterfaceModel(8883, 150, 3224);
            object2 = object3;
            ((Player)object2).packetSender.sendInterfaceModel(8884, 100, 5418);
            object2 = object3;
            ((Player)object2).packetSender.sendInterfaceModel(8885, 100, 5376);
            object2 = object3;
            ((Player)object2).packetSender.showChatboxInterface(8880);
        } else if (string == "shaft") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 53, "Headless arrows");
        } else if (string == "bronzeArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 882, "Bronze arrows");
        } else if (string == "ironArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 884, "Iron arrows");
        } else if (string == "steelArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 886, "Steel arrows");
        } else if (string == "mithrilArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 888, "Mithril arrows");
        } else if (string == "adamantArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 890, "Adamant arrows");
        } else if (string == "runeArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 892, "Rune arrows");
        } else if (string == "bronzeDart") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 806, "Bronze darts");
        } else if (string == "ironDart") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 807, "Iron darts");
        } else if (string == "steelDart") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 808, "Steel darts");
        } else if (string == "mithrilDart") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 809, "Mithril darts");
        } else if (string == "adamantDart") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 810, "Adamant darts");
        } else if (string == "runeDart") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 811, "Rune darts");
        } else if (string == "bronzeBrutalArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4773, "Bronze brutal arrows");
        } else if (string == "ironBrutalArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4778, "Iron brutal arrows");
        } else if (string == "steelBrutalArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4783, "Black brutal arrows");
        } else if (string == "blackBrutalArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4788, "Steel brutal arrows");
        } else if (string == "mithrilBrutalArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4793, "Mithril brutal arrows");
        } else if (string == "adamantBrutalArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4798, "Adamant brutal arrows");
        } else if (string == "runeBrutalArrow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4803, "Rune brutal arrows");
        } else if (string == "shortBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 841, "Shortbow");
        } else if (string == "longBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 839, "Longbow");
        } else if (string == "oakShortBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 843, "Oak shortbow");
        } else if (string == "oakLongBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 845, "Oak longbow");
        } else if (string == "compositeOgre") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4827, "Composite Ogre bow");
        } else if (string == "willowShortBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 849, "Willow shortbow");
        } else if (string == "willowLongBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 849, "Willow longbow");
        } else if (string == "mapleShortBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 853, "Maple shortbow");
        } else if (string == "mapleLongBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 851, "Maple longbow");
        } else if (string == "yewShortBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 857, "Yew shortbow");
        } else if (string == "yewLongBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 855, "Yew longbow");
        } else if (string == "magicShortBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 861, "Magic shortbow");
        } else if (string == "magicLongBow") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 859, "Magic longbow");
        } else if (string == "normalCutting") {
            GameplayHelper.showThreeOptionProductionInterface((Player)object, 52, 50, 48, "Arrow shafts", "Shortbow", "Longbow");
        } else if (string == "oakCutting") {
            GameplayHelper.showTwoOptionProductionInterface((Player)object, 54, 56, "Oak shortbow", "Oak longbow");
        } else if (string == "acheyCutting") {
            GameplayHelper.showOneOptionProductionInterface((Player)object, 4825, "Unstrung comp bow");
        } else if (string == "willowCutting") {
            GameplayHelper.showTwoOptionProductionInterface((Player)object, 60, 58, "Willow shortbow", "Willow longbow");
        } else if (string == "mapleCutting") {
            GameplayHelper.showTwoOptionProductionInterface((Player)object, 64, 62, "Maple shortbow", "Maple longbow");
        } else if (string == "yewCutting") {
            GameplayHelper.showTwoOptionProductionInterface((Player)object, 68, 66, "Yew shortbow", "Yew longbow");
        } else if (string == "magicCutting") {
            GameplayHelper.showTwoOptionProductionInterface((Player)object, 72, 70, "Magic shortbow", "Magic longbow");
        } else if (string == "dairyChurn") {
            object2 = object;
            ((Player)object2).packetSender.showChatboxInterface(15336);
        }
        Player player = object;
        player.interfaceAction = string;
    }

    public static void showSplitbarkProductionInterface(Player player, int n, int n2, int n3, int n4, int n5, String string, String string2, String string3, String string4, String string5, String string6) {
        Player player2 = player;
        player2.packetSender.sendInterfaceModel(8941, 120, 3385);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8942, 150, 3387);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8943, 150, 3389);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8944, 120, 3391);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8945, 150, 3393);
        player2 = player;
        player2.packetSender.sendInterfaceText(string, 8949);
        player2 = player;
        player2.packetSender.sendInterfaceText(string2, 8953);
        player2 = player;
        player2.packetSender.sendInterfaceText(string3, 8957);
        player2 = player;
        player2.packetSender.sendInterfaceText(string4, 8961);
        player2 = player;
        player2.packetSender.sendInterfaceText(string5, 8965);
        player2 = player;
        player2.packetSender.sendInterfaceText(string6, 8966);
        player2 = player;
        player2.packetSender.showChatboxInterface(8938);
    }

    public static void showFiveOptionProductionInterface(Player player, int n, int n2, int n3, int n4, int n5, String string, String string2, String string3, String string4, String string5) {
        Player player2 = player;
        player2.packetSender.sendInterfaceModel(8941, 120, n);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8942, 150, n2);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8943, 150, n3);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8944, 120, n4);
        player2 = player;
        player2.packetSender.sendInterfaceModel(8945, 150, n5);
        player2 = player;
        player2.packetSender.sendInterfaceText(string, 8949);
        player2 = player;
        player2.packetSender.sendInterfaceText(string2, 8953);
        player2 = player;
        player2.packetSender.sendInterfaceText(string3, 8957);
        player2 = player;
        player2.packetSender.sendInterfaceText(string4, 8961);
        player2 = player;
        player2.packetSender.sendInterfaceText(string5, 8965);
        player2 = player;
        player2.packetSender.showChatboxInterface(8938);
    }

    public static void showThreeOptionProductionInterface(Player player, int n, int n2, int n3, String string, String string2, String string3) {
        if (ServerSettings.cacheVersion < 274) {
            Player player2 = player;
            player2.packetSender.setInterfaceHiddenFlag(1, 2476);
            player2 = player;
            player2.packetSender.setInterfaceHiddenFlag(0, 2479);
            player2 = player;
            player2.packetSender.sendInterfaceText("What would you like to make?", 2470);
            player2 = player;
            player2.packetSender.sendInterfaceText(string, 2471);
            player2 = player;
            player2.packetSender.sendInterfaceText(string2, 2472);
            player2 = player;
            player2.packetSender.sendInterfaceText(string3, 2473);
            player2 = player;
            player2.packetSender.showChatboxInterface(2469);
            return;
        }
        Player player3 = player;
        player3.packetSender.sendInterfaceText(string, 8889);
        player3 = player;
        player3.packetSender.sendInterfaceText(string2, 8893);
        player3 = player;
        player3.packetSender.sendInterfaceText(string3, 8897);
        player3 = player;
        player3.packetSender.sendInterfaceModel(8883, 180, n);
        player3 = player;
        player3.packetSender.sendInterfaceModel(8884, 180, n2);
        player3 = player;
        player3.packetSender.sendInterfaceModel(8885, 180, n3);
        player3 = player;
        player3.packetSender.showChatboxInterface(8880);
    }

    public static void showTwoOptionProductionInterface(Player player, int n, int n2, String string, String string2) {
        if (ServerSettings.cacheVersion < 274) {
            Player player2 = player;
            player2.packetSender.setInterfaceHiddenFlag(1, 2465);
            player2 = player;
            player2.packetSender.setInterfaceHiddenFlag(0, 2468);
            player2 = player;
            player2.packetSender.sendInterfaceText("What would you like to make?", 2460);
            player2 = player;
            player2.packetSender.sendInterfaceText(string, 2461);
            player2 = player;
            player2.packetSender.sendInterfaceText(string2, 2462);
            player2 = player;
            player2.packetSender.showChatboxInterface(2459);
            return;
        }
        Player player3 = player;
        player3.packetSender.sendInterfaceText(string, 8874);
        player3 = player;
        player3.packetSender.sendInterfaceText(string2, 8878);
        player3 = player;
        player3.packetSender.sendInterfaceModel(8869, 180, n);
        player3 = player;
        player3.packetSender.sendInterfaceModel(8870, 180, n2);
        player3 = player;
        player3.packetSender.showChatboxInterface(8866);
    }

    public static void showOneOptionProductionInterface(Player player, int n, String string) {
        if (ServerSettings.cacheVersion < 334) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This action does not currently work on this build!");
            return;
        }
        Player player3 = player;
        player3.packetSender.sendInterfaceText(string, 2799);
        player3 = player;
        player3.packetSender.sendInterfaceModel(1746, 200, n);
        player3 = player;
        player3.packetSender.showChatboxInterface(4429);
    }

    public static String getIndefiniteArticle(String string) {
        String[] stringArray = new String[]{"a", "e", "i", "o", "u", "y"};
        int n = 0;
        while (n < 6) {
            String vowel = stringArray[n];
            if (string.toLowerCase().startsWith(vowel)) {
                return "an";
            }
            ++n;
        }
        return "a";
    }

    /*
     * Enabled aggressive block sorting
     */
    public static boolean handleAgilityObjectAction(Player player, int n, int n2, int n3) {
        int n4;
        int n5 = n3;
        int n6 = n2;
        int n7 = n;
        Player player2 = player;
        switch (n7) {
            case 2295: {
                if (n6 != 2474 || n5 != 3435) break;
                AgilityObstacleHandler.startAgilityMovement(player2, 7.5, 0, -7, -1, 762, -1, 4, "You walk carefully across the slippery log...", "...You make it safely to the other side.");
                player2.bN = 1;
                return true;
            }
            case 2285: {
                if (!(n6 == 2475 && n5 == 3425 || n6 == 2473 && n5 == 3425) && (n6 != 2471 || n5 != 3425) || player2.getPosition().getY() < 3426) break;
                AgilityObstacleHandler.startPositionOffsetObstacle(player2, 7.5, 0, -2, 1, 828, 2, "You climb the netting...", null);
                if (player2.bN != 1) return true;
                player2.bN = 2;
                return true;
            }
            case 2313: {
                if (n6 != 2473 || n5 != 3422) break;
                n4 = 3421 - player2.getPosition().getY();
                AgilityObstacleHandler.startPositionOffsetObstacle(player2, 5.0, 0, n4, 1, 828, 2, "You climb the tree...", "...To the platform above.");
                if (player2.bN != 2) return true;
                player2.bN = 3;
                return true;
            }
            case 2312: {
                if (n6 != 2478 || n5 != 3420) break;
                AgilityObstacleHandler.startAgilityMovement(player2, 7.5, 6, 0, -1, 762, -1, 3, "You carefully cross the tightrope.", null);
                if (player2.bN != 3) return true;
                player2.bN = 4;
                return true;
            }
            case 2314: {
                if (n6 != 2486 || n5 != 3419) break;
                AgilityObstacleHandler.startPositionOffsetObstacle(player2, 5.0, 0, 0, -2, 828, 2, "You climb down the tree...", "You land on the ground.");
                if (player2.bN != 4) return true;
                player2.bN = 5;
                return true;
            }
            case 2286: {
                if (!(n6 == 2483 && n5 == 3426 || n6 == 2485 && n5 == 3426) && (n6 != 2487 || n5 != 3426) || player2.getPosition().getY() > 3425) break;
                AgilityObstacleHandler.startPositionOffsetObstacle(player2, 7.5, 0, 2, 0, 828, 2, "You climb the netting...", null);
                if (player2.bN != 5) return true;
                player2.bN = 6;
                return true;
            }
            case 154: 
            case 4058: {
                if ((n6 != 2484 || n5 != 3431) && (n6 != 2487 || n5 != 3431)) break;
                double d = player2.bN == 6 ? 46.5 : 7.5;
                AgilityObstacleHandler.startAgilityMovement(player2, d, 0, 7, 746, 844, 748, 7, null, null);
                player2.bN = 0;
                return true;
            }
        }
        boolean bl = false;
        if (bl) {
            return true;
        }
        n5 = n3;
        n6 = n2;
        n7 = n;
        player2 = player;
        switch (n7) {
            case 2287: {
                if (n6 != 2552 || n5 != 3559) break;
                if (!SkillActionHelper.checkSkillRequirement(player2, 16, 35, "enter")) {
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 10.0, 0, player2.getPosition().getY() >= 3560 ? -3 : 3, 746, 844, 748, 3, null, null);
                return true;
            }
            case 2282: {
                if (n6 != 2551 || n5 != 3550) break;
                AgilityObstacleHandler.startQueuedObstacleMovement(player2, 22, 0, -5, 1, 60, -1, 751);
                Player player3 = player2;
                player3.packetSender.sendObjectAnimation(2551, 3550, 0, 127);
                player2.bN = 1;
                return true;
            }
            case 2294: {
                if (n6 != 2550 || n5 != 3546) break;
                AgilityObstacleHandler.startAgilityMovement(player2, 13.0, -10, 0, -1, 762, -1, 5, "You walk carefully across the slippery log...", "...You make it safely to the other side.");
                if (player2.bN != 1) return true;
                player2.bN = 2;
                return true;
            }
            case 2284: {
                if (n6 != 2538 || n5 != 3545) break;
                AgilityObstacleHandler.startPositionOffsetObstacle(player2, 8.0, -1, 0, 1, 828, 2, "You climb the netting...", null);
                if (player2.bN != 2) return true;
                player2.bN = 3;
                return true;
            }
            case 2302: {
                if (n6 != 2535 || n5 != 3547) break;
                AgilityObstacleHandler.startAgilityMovement(player2, 22.0, -4, 0, -1, 756, -1, 3, null, null);
                if (player2.bN != 3) return true;
                player2.bN = 4;
                return true;
            }
            case 1948: {
                if (n6 == 2536 && n5 == 3553) {
                    AgilityObstacleHandler.startAgilityMovement(player2, 13.0, 2, 0, -1, 839, -1, 1, null, null);
                    if (player2.bN != 4) return true;
                    player2.bN = 5;
                    return true;
                }
                if (n6 == 2539 && n5 == 3553) {
                    AgilityObstacleHandler.startAgilityMovement(player2, 13.0, 2, 0, -1, 839, -1, 1, null, null);
                    if (player2.bN != 5) return true;
                    player2.bN = 6;
                    return true;
                }
                if (n6 != 2542 || n5 != 3553) break;
                n4 = player2.bN == 6 ? 60 : 13;
                AgilityObstacleHandler.startAgilityMovement(player2, n4, 2, 0, -1, 839, -1, 1, null, null);
                player2.bN = 0;
                return true;
            }
        }
        boolean bl2 = false;
        if (bl2) {
            return true;
        }
        n5 = n3;
        n6 = n2;
        n7 = n;
        player2 = player;
        switch (n7) {
            case 2309: {
                if (n6 != 2998 || n5 != 3917) break;
                if (!SkillActionHelper.checkSkillRequirement(player2, 16, 52, "enter")) {
                    return true;
                }
                Player player4 = player2;
                player4.packetSender.openSingleDoor(n7, n6, n5, 0);
                AgilityObstacleHandler.startAgilityMovement(player2, 13.0, 0, 15, -1, 762, -1, 8, "You go through the gate and try to edge over the ridge...", "...You skillfully balance across the ridge...");
                return true;
            }
            case 2307: 
            case 2308: {
                if ((n6 != 2998 || n5 != 3931) && (n6 != 2997 || n5 != 3931)) break;
                AgilityObstacleHandler.startAgilityMovement(player2, 13.0, 0, -15, -1, 762, -1, 8, "You go through the gate and try to edge over the ridge...", "...You skillfully balance across the ridge...");
                return true;
            }
            case 2288: {
                if (n6 != 3004 || n5 != 3938) break;
                if (!SkillActionHelper.checkSkillRequirement(player2, 16, 49, "to go through.")) {
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 12.5, 0, 13, 746, 844, 748, 9, null, null);
                player2.bN = 1;
                return true;
            }
            case 2283: {
                if (n6 != 3005 || n5 != 3952) break;
                AgilityObstacleHandler.startQueuedObstacleMovement(player2, 20, 0, 7, 1, 60, -1, 751);
                Player player5 = player2;
                player5.packetSender.sendObjectAnimation(2551, 3550, 0, 127);
                player5 = player2;
                player5.packetSender.sendGameMessage("You skillfully swing across.");
                if (player2.bN != 1) return true;
                player2.bN = 2;
                return true;
            }
            case 2311: {
                if (n6 != 3001 || n5 != 3960) break;
                player2.moveTo(new Position(2996, 3960, 0));
                player2.getSkillManager().addExperience(16, 20.0);
                if (player2.bN != 2) return true;
                player2.bN = 3;
                return true;
            }
            case 2297: {
                if (n6 != 3001 || n5 != 3945) break;
                AgilityObstacleHandler.startAgilityMovement(player2, 20.0, -8, 0, -1, 762, -1, 4, "You walk carefully across the slippery log...", "You skillfully edge across the gap.");
                if (player2.bN != 3) return true;
                player2.bN = 4;
                return true;
            }
            case 2328: {
                n4 = player2.bN == 4 ? 499 : 0;
                AgilityObstacleHandler.startPositionOffsetObstacle(player2, n4, 0, -4, 0, 828, 1, null, null);
                player2.bN = 0;
                return true;
            }
        }
        boolean bl3 = false;
        if (bl3) {
            return true;
        }
        n5 = n3;
        n6 = n2;
        n7 = n;
        player2 = player;
        switch (n7) {
            case 2296: {
                if (n6 != 2599 || n5 != 3477) {
                    if (n6 != 2602) return false;
                    if (n5 != 3477) return false;
                }
                if (player2.getSkillManager().getCurrentLevels()[16] < 20) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 20 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, GameUtil.randomInclusive(5) == 0 ? 7 : 0, n6 < 2600 ? 5 : -5, 0, -1, 762, -1, 3, "You walk carefully across the slippery log...", "...You make it safely to the other side.");
                return true;
            }
            case 9330: {
                if (n6 != 2601) return false;
                if (n5 != 3336) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 32) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 32 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 4.0, -4, 0, -1, 762, -1, 2, "You walk carefully across the slippery log...", "...You make it safely to the other side.");
                return true;
            }
            case 9328: {
                if (n6 != 2599) return false;
                if (n5 != 3336) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 32) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 32 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 4.0, 4, 0, -1, 762, -1, 2, "You walk carefully across the slippery log...", "...You make it safely to the other side.");
                return true;
            }
            case 12127: {
                if (n6 == 2400 && n5 == 4403) {
                    if (player2.getSkillManager().getCurrentLevels()[16] < 44) {
                        player2.getDialogueManager().showOneLineStatement("You need a agility level of 44 to do that.");
                        return true;
                    }
                    player2.getUpdateState().setAnimation(754);
                    player2.getSkillManager().addExperience(16, 10.0);
                    AgilityObstacleHandler.startForcedMovement(player2, 0, player2.getPosition().getY() < 4404 ? 2 : -2, 1, 80, 2, true, 0, 0);
                    return true;
                }
                if (n6 == 2408 && n5 == 4395) {
                    if (player2.getSkillManager().getCurrentLevels()[16] < 66) {
                        player2.getDialogueManager().showOneLineStatement("You need a agility level of 66 to do that.");
                        return true;
                    }
                    player2.getUpdateState().setAnimation(754);
                    player2.getSkillManager().addExperience(16, 10.0);
                    AgilityObstacleHandler.startForcedMovement(player2, 0, player2.getPosition().getY() < 4396 ? 2 : -2, 1, 80, 2, true, 0, 0);
                    return true;
                }
                if (n6 != 2415) return false;
                if (n5 != 4402) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 66) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 66 to do that.");
                    return true;
                }
                player2.getUpdateState().setAnimation(754);
                player2.getSkillManager().addExperience(16, 10.0);
                AgilityObstacleHandler.startForcedMovement(player2, 0, player2.getPosition().getY() < 4403 ? 2 : -2, 1, 80, 2, true, 0, 0);
                return true;
            }
            case 9302: {
                if (n6 != 2575) return false;
                if (n5 != 3111) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 16) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 16 to do that.");
                    return true;
                }
                player2.moveTo(new Position(2575, 3107, 0));
                return true;
            }
            case 9301: {
                if (n6 != 2575) return false;
                if (n5 != 3108) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 16) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 16 to do that.");
                    return true;
                }
                player2.moveTo(new Position(2575, 3112, 0));
                return true;
            }
            case 9294: {
                if (n6 != 2879) return false;
                if (n5 != 9813) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 80) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 80 to do that.");
                    return true;
                }
                if (player2.getPosition().getX() < 2880) {
                    player2.moveTo(new Position(2880, 9813, 0));
                    return true;
                } else {
                    player2.moveTo(new Position(2878, 9813, 0));
                }
                return true;
            }
            case 9326: {
                if (n6 != 2769 || n5 != 10002) {
                    if (n6 != 2774) return false;
                    if (n5 != 10003) return false;
                }
                if (player2.getSkillManager().getCurrentLevels()[16] < 81) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 81 to do that.");
                    return true;
                }
                if (n6 == 2774 && n5 == 10003) {
                    player2.moveTo(new Position(2768, 10002, 0));
                    return true;
                } else {
                    player2.moveTo(new Position(2775, 10003, 0));
                }
                return true;
            }
            case 9321: {
                if (n6 != 2734 || n5 != 10008) {
                    if (n6 != 2731) return false;
                    if (n5 != 10008) return false;
                }
                if (player2.getSkillManager().getCurrentLevels()[16] < 61) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 61 to do that.");
                    return true;
                }
                if (n6 == 2731 && n5 == 10008) {
                    player2.moveTo(new Position(2735, 10008, 0));
                    return true;
                } else {
                    player2.moveTo(new Position(2730, 10008, 0));
                }
                return true;
            }
            case 9324: {
                if (n6 != 2722) return false;
                if (n5 != 3593) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 47) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 47 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 0.0, 0, 4, -1, 762, -1, 2, "You walk carefully across the slippery log...", "...You make it safely to the other side.");
                return true;
            }
            case 9322: {
                if (n6 != 2722) return false;
                if (n5 != 3595) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 47) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 47 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 0.0, 0, -4, -1, 762, -1, 2, "You walk carefully across the slippery log...", "...You make it safely to the other side.");
                return true;
            }
            case 1947: 
            case 11844: {
                if (n6 != 2935) return false;
                if (n5 != 3355) return false;
                if (!player2.isMember()) {
                    player2.packetSender.sendGameMessage("You need a members account to access members content.");
                    return true;
                }
                if (ServerSettings.freeToPlayWorld) {
                    player2.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    return true;
                }
                if (player2.getSkillManager().getCurrentLevels()[16] < 5) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 5 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 0.5, player2.getPosition().getX() < 2936 ? 2 : -2, 0, -1, 839, -1, 1, null, null);
                return true;
            }
            case 9295: {
                if (n6 == 3150 && n5 == 9906) {
                    if (player2.getSkillManager().getCurrentLevels()[16] < 51) {
                        player2.getDialogueManager().showOneLineStatement("You need a agility level of 51 to do that.");
                        return true;
                    }
                    AgilityObstacleHandler.startAgilityMovement(player2, 0.0, 6, 0, 746, 844, 748, 6, null, null);
                    return true;
                }
                if (n6 != 3153) return false;
                if (n5 != 9906) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 51) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 51 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 0.0, -6, 0, 746, 844, 748, 6, null, null);
                return true;
            }
            case 9293: {
                if (n6 == 2887 && n5 == 9799) {
                    if (player2.getSkillManager().getCurrentLevels()[16] < 70) {
                        player2.getDialogueManager().showOneLineStatement("You need a agility level of 70 to do that.");
                        return true;
                    }
                    AgilityObstacleHandler.startAgilityMovement(player2, 0.0, 6, 0, 746, 844, 748, 6, null, null);
                    return true;
                }
                if (n6 != 2890) return false;
                if (n5 != 9799) return false;
                if (player2.getSkillManager().getCurrentLevels()[16] < 70) {
                    player2.getDialogueManager().showOneLineStatement("You need a agility level of 70 to do that.");
                    return true;
                }
                AgilityObstacleHandler.startAgilityMovement(player2, 0.0, -6, 0, 746, 844, 748, 6, null, null);
                return true;
            }
        }
        return false;
    }

    public static boolean handleFlourDoughButton(Player player, int n) {
        Player player2 = player;
        if (player2.interfaceAction != "flour") {
            return false;
        }
        switch (n) {
            case 2482: 
            case 8209: {
                player.getInventoryManager().removeItem(new ItemStack(1929));
                player.getInventoryManager().removeItem(new ItemStack(1933));
                player.getInventoryManager().addItem(new ItemStack(1925));
                player.getInventoryManager().addItem(new ItemStack(1931));
                player2 = player;
                player2.packetSender.sendGameMessage("You put the water on the flour and make it into a bread dough");
                player.getInventoryManager().addOrDropItem(new ItemStack(2307));
                player2 = player;
                player2.packetSender.closeInterfaces();
                return true;
            }
            case 2483: 
            case 8210: {
                if (!player.getInventoryManager().getContainer().containsItem(1929) || !player.getInventoryManager().getContainer().containsItem(1933)) {
                    return true;
                }
                player.getInventoryManager().removeItem(new ItemStack(1929));
                player.getInventoryManager().removeItem(new ItemStack(1933));
                player.getInventoryManager().addItem(new ItemStack(1925));
                player.getInventoryManager().addItem(new ItemStack(1931));
                player2 = player;
                player2.packetSender.sendGameMessage("You put the water on the flour and make it into a pastry dough");
                player.getInventoryManager().addOrDropItem(new ItemStack(1953));
                player2 = player;
                player2.packetSender.closeInterfaces();
                return true;
            }
            case 2484: 
            case 8211: {
                if (!player.getInventoryManager().getContainer().containsItem(1929) || !player.getInventoryManager().getContainer().containsItem(1933)) {
                    return true;
                }
                player.getInventoryManager().removeItem(new ItemStack(1929));
                player.getInventoryManager().removeItem(new ItemStack(1933));
                player.getInventoryManager().addItem(new ItemStack(1925));
                player.getInventoryManager().addItem(new ItemStack(1931));
                player2 = player;
                player2.packetSender.sendGameMessage("You put the water on the flour and make it into a pizza base");
                player.getInventoryManager().addOrDropItem(new ItemStack(2283));
                player2 = player;
                player2.packetSender.closeInterfaces();
                return true;
            }
            case 2485: 
            case 8212: {
                if (!player.getInventoryManager().getContainer().containsItem(1929) || !player.getInventoryManager().getContainer().containsItem(1933)) {
                    return true;
                }
                player.getInventoryManager().removeItem(new ItemStack(1929));
                player.getInventoryManager().removeItem(new ItemStack(1933));
                player.getInventoryManager().addItem(new ItemStack(1925));
                player.getInventoryManager().addItem(new ItemStack(1931));
                player2 = player;
                player2.packetSender.sendGameMessage("You put the water on the flour and make it into a pitta dough");
                player.getInventoryManager().addOrDropItem(new ItemStack(1863));
                player2 = player;
                player2.packetSender.closeInterfaces();
                return true;
            }
        }
        return false;
    }

    public static boolean handleLeatherCraftingItemUse(Player player, int n, int n2, int n3, int n4) {
        int n5 = n3 = n == 2370 ? n4 : n3;
        if (n == 2370 || n2 == 2370) {
            if (!ServerSettings.craftingEnabled) {
                Player player2 = player;
                player2.packetSender.sendGameMessage("This skill is currently disabled.");
                return true;
            }
            if (!player.isMember()) {
                player.packetSender.sendGameMessage("You need a members account to access members content.");
                return true;
            }
            if (ServerSettings.freeToPlayWorld) {
                player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                return true;
            }
            if (n == 1131 || n2 == 1131) {
                Player player3 = player;
                player3.packetSender.sendGameMessage("You attach the steel studs to the hard leather body");
                if (player.getInventoryManager().removeItemFromSlot(new ItemStack(2370), n3)) {
                    player.getInventoryManager().setItemInSlot(new ItemStack(1133), n3);
                } else if (player.getInventoryManager().removeItem(new ItemStack(2370))) {
                    player.getInventoryManager().addItem(new ItemStack(1133));
                }
                return true;
            }
            if (n == 1095 || n2 == 1095) {
                Player player4 = player;
                player4.packetSender.sendGameMessage("You attach the steel studs to the leather chaps");
                if (player.getInventoryManager().removeItemFromSlot(new ItemStack(2370), n3)) {
                    player.getInventoryManager().setItemInSlot(new ItemStack(1097), n3);
                } else if (player.getInventoryManager().removeItem(new ItemStack(2370))) {
                    player.getInventoryManager().addItem(new ItemStack(1097));
                }
                return true;
            }
        }
        if (n == 1733 || n2 == 1733) {
            if (!ServerSettings.craftingEnabled) {
                Player player5 = player;
                player5.packetSender.sendGameMessage("This skill is currently disabled.");
                return true;
            }
            if (n == 1741 || n2 == 1741) {
                GameplayHelper.openProductionInterface(player, "normalLeather");
                return true;
            }
            if (n == 1743 || n2 == 1743) {
                Object object = "hardLeather";
                if (ServerSettings.cacheVersion < 334) {
                    String string = (String)object;
                    object = player;
                    player.interfaceAction = string;
                    GameplayHelper.handleCraftedArmorButton(player, 2799, 1);
                } else {
                    GameplayHelper.openProductionInterface(player, (String)object);
                }
                return true;
            }
            if (n == 1745 || n2 == 1745) {
                GameplayHelper.openProductionInterface(player, "greenLeather");
                return true;
            }
            if (n == 2505 || n2 == 2505) {
                GameplayHelper.openProductionInterface(player, "blueLeather");
                return true;
            }
            if (n == 2507 || n2 == 2507) {
                GameplayHelper.openProductionInterface(player, "redLeather");
                return true;
            }
            if (n == 2509 || n2 == 2509) {
                GameplayHelper.openProductionInterface(player, "blackLeather");
                return true;
            }
            if (n == 6287 || n2 == 6287) {
                GameplayHelper.openProductionInterface(player, "snakeskin1");
                return true;
            }
            if (n == 6289 || n2 == 6289) {
                GameplayHelper.openProductionInterface(player, "snakeskin2");
                return true;
            }
        }
        return false;
    }

    public static boolean handleCraftedArmorButton(Player player, int n, int n2) {
        Enum enum_;
        int n3;
        int n4;
        Enum[] enumArray;
        int n5;
        Object object = player;
        if (((Player)object).interfaceAction == "normalLeather") {
            if (player.botEnabled) {
                Player player2 = player;
                object = null;
                n5 = 0;
                enumArray = LeatherRecipe.values();
                n4 = enumArray.length;
                n3 = 0;
                while (n3 < n4) {
                    enum_ = enumArray[n3];
                    if (((LeatherRecipe)enum_).getRequiredLevel() <= player2.getSkillManager().getCurrentLevels()[12] && ((LeatherRecipe)enum_).getMaterialAmount() <= player2.getInventoryManager().getItemAmount(((LeatherRecipe)enum_).getMaterialItemId()) && ((LeatherRecipe)enum_).getRequiredLevel() > n5) {
                        n5 = ((LeatherRecipe)enum_).getRequiredLevel();
                        object = enum_;
                    }
                    ++n3;
                }
                int n6 = n = object != null ? ((LeatherRecipe)((Object)object)).getButtonId() : -1;
            }
            if (LeatherCrafting.createForButton(player, n, n2) != null) {
                LeatherCrafting.createForButton(player, n, n2).startCrafting();
                return true;
            }
        }
        object = player;
        if (((Player)object).interfaceAction == "greenLeather") {
            if (player.botEnabled) {
                Player player3 = player;
                object = null;
                n5 = 0;
                enumArray = GreenDragonhideRecipe.values();
                n4 = enumArray.length;
                n3 = 0;
                while (n3 < n4) {
                    enum_ = enumArray[n3];
                    if (((GreenDragonhideRecipe)enum_).getRequiredLevel() <= player3.getSkillManager().getCurrentLevels()[12] && ((GreenDragonhideRecipe)enum_).getMaterialAmount() <= player3.getInventoryManager().getItemAmount(((GreenDragonhideRecipe)enum_).getMaterialItemId()) && ((GreenDragonhideRecipe)enum_).getRequiredLevel() > n5) {
                        n5 = ((GreenDragonhideRecipe)enum_).getRequiredLevel();
                        object = enum_;
                    }
                    ++n3;
                }
                int n7 = n = object != null ? ((GreenDragonhideRecipe)((Object)object)).getButtonId() : -1;
            }
            if (GreenDragonhideCrafting.createForButton(player, n, n2) != null) {
                GreenDragonhideCrafting.createForButton(player, n, n2).startCrafting();
                return true;
            }
        }
        object = player;
        if (((Player)object).interfaceAction == "blueLeather") {
            if (player.botEnabled) {
                Player player4 = player;
                object = null;
                n5 = 0;
                enumArray = BlueDragonhideRecipe.values();
                n4 = enumArray.length;
                n3 = 0;
                while (n3 < n4) {
                    enum_ = enumArray[n3];
                    if (((BlueDragonhideRecipe)enum_).getRequiredLevel() <= player4.getSkillManager().getCurrentLevels()[12] && ((BlueDragonhideRecipe)enum_).getMaterialAmount() <= player4.getInventoryManager().getItemAmount(((BlueDragonhideRecipe)enum_).getMaterialItemId()) && ((BlueDragonhideRecipe)enum_).getRequiredLevel() > n5) {
                        n5 = ((BlueDragonhideRecipe)enum_).getRequiredLevel();
                        object = enum_;
                    }
                    ++n3;
                }
                int n8 = n = object != null ? ((BlueDragonhideRecipe)((Object)object)).getButtonId() : -1;
            }
            if (BlueDragonhideCrafting.createForButton(player, n, n2) != null) {
                BlueDragonhideCrafting.createForButton(player, n, n2).startCrafting();
                return true;
            }
        }
        object = player;
        if (((Player)object).interfaceAction == "redLeather") {
            if (player.botEnabled) {
                Player player5 = player;
                object = null;
                n5 = 0;
                enumArray = RedDragonhideRecipe.values();
                n4 = enumArray.length;
                n3 = 0;
                while (n3 < n4) {
                    enum_ = enumArray[n3];
                    if (((RedDragonhideRecipe)enum_).getRequiredLevel() <= player5.getSkillManager().getCurrentLevels()[12] && ((RedDragonhideRecipe)enum_).getMaterialAmount() <= player5.getInventoryManager().getItemAmount(((RedDragonhideRecipe)enum_).getMaterialItemId()) && ((RedDragonhideRecipe)enum_).getRequiredLevel() > n5) {
                        n5 = ((RedDragonhideRecipe)enum_).getRequiredLevel();
                        object = enum_;
                    }
                    ++n3;
                }
                int n9 = n = object != null ? ((RedDragonhideRecipe)((Object)object)).getButtonId() : -1;
            }
            if (RedDragonhideCrafting.createForButton(player, n, n2) != null) {
                RedDragonhideCrafting.createForButton(player, n, n2).startCrafting();
                return true;
            }
        }
        object = player;
        if (((Player)object).interfaceAction == "blackLeather") {
            if (player.botEnabled) {
                Player player6 = player;
                object = null;
                n5 = 0;
                enumArray = BlackDragonhideRecipe.values();
                n4 = enumArray.length;
                n3 = 0;
                while (n3 < n4) {
                    enum_ = enumArray[n3];
                    if (((BlackDragonhideRecipe)enum_).getRequiredLevel() <= player6.getSkillManager().getCurrentLevels()[12] && ((BlackDragonhideRecipe)enum_).getMaterialAmount() <= player6.getInventoryManager().getItemAmount(((BlackDragonhideRecipe)enum_).getMaterialItemId()) && ((BlackDragonhideRecipe)enum_).getRequiredLevel() > n5) {
                        n5 = ((BlackDragonhideRecipe)enum_).getRequiredLevel();
                        object = enum_;
                    }
                    ++n3;
                }
                int n10 = n = object != null ? ((BlackDragonhideRecipe)((Object)object)).getButtonId() : -1;
            }
            if (BlackDragonhideCrafting.createForButton(player, n, n2) != null) {
                BlackDragonhideCrafting.createForButton(player, n, n2).startCrafting();
                return true;
            }
        }
        object = player;
        if (((Player)object).interfaceAction == "hardLeather") {
            if (player.botEnabled) {
                Player player7 = player;
                object = null;
                n5 = 0;
                enumArray = HardLeatherRecipe.values();
                n4 = enumArray.length;
                n3 = 0;
                while (n3 < n4) {
                    enum_ = enumArray[n3];
                    if (((HardLeatherRecipe)enum_).getRequiredLevel() <= player7.getSkillManager().getCurrentLevels()[12] && ((HardLeatherRecipe)enum_).getMaterialAmount() <= player7.getInventoryManager().getItemAmount(((HardLeatherRecipe)enum_).getMaterialItemId()) && ((HardLeatherRecipe)enum_).getRequiredLevel() > n5) {
                        n5 = ((HardLeatherRecipe)enum_).getRequiredLevel();
                        object = enum_;
                    }
                    ++n3;
                }
                int n11 = n = object != null ? ((HardLeatherRecipe)((Object)object)).getButtonId() : -1;
            }
            if (HardLeatherCrafting.createForButton(player, n, n2) != null) {
                HardLeatherCrafting.createForButton(player, n, n2).startCrafting();
                return true;
            }
        }
        object = player;
        if (((Player)object).interfaceAction == "snakeskin1" && SnakeskinAccessoryCrafting.createForButton(player, n, n2) != null) {
            SnakeskinAccessoryCrafting.createForButton(player, n, n2).startCrafting();
            return true;
        }
        object = player;
        if (((Player)object).interfaceAction == "snakeskin2" && SnakeskinArmorCrafting.createForButton(player, n, n2) != null) {
            SnakeskinArmorCrafting.createForButton(player, n, n2).startCrafting();
            return true;
        }
        object = player;
        if (((Player)object).interfaceAction == "splitbark" && SplitbarkCrafting.createForButton(player, n, n2) != null) {
            SplitbarkCrafting.createForButton(player, n, n2).startCrafting();
            return true;
        }
        return false;
    }

    public static void openTanningInterface(Player player) {
        if (!ServerSettings.craftingEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        int n = player.getInteractionTargetId() == 1041 ? 2 : 1;
        Object object = "tanning";
        Player player3 = player;
        player.interfaceAction = (String)object;
        player3 = player;
        player3.packetSender.showInterface(14670 >= InterfaceDefinition.interfaceCount ? 679 : 14670);
        player3 = player;
        player3.packetSender.sendInterfaceText("Soft Leather", 14777);
        if (player.getInventoryManager().getItemAmount(995) > 0) {
            player3 = player;
            player3.packetSender.sendInterfaceText(String.valueOf(1 * n) + " coins", 14785);
        } else {
            player3 = player;
            player3.packetSender.sendInterfaceText(String.valueOf(1 * n) + " coins", 14785);
        }
        player3 = player;
        player3.packetSender.sendInterfaceText("Hard Leather", 14778);
        if (player.getInventoryManager().getItemAmount(995) >= 3) {
            player3 = player;
            player3.packetSender.sendInterfaceText(String.valueOf(3 * n) + " coins", 14786);
        } else {
            player3 = player;
            player3.packetSender.sendInterfaceText(String.valueOf(3 * n) + " coins", 14786);
        }
        player3 = player;
        player3.packetSender.sendInterfaceModel(14769 >= InterfaceDefinition.interfaceCount ? 7445 : 14769, 250, 1741);
        player3 = player;
        player3.packetSender.sendInterfaceModel(14770 >= InterfaceDefinition.interfaceCount ? 7436 : 14770, 250, 1743);
        player3 = player;
        player3.packetSender.sendInterfaceModel(14773 >= InterfaceDefinition.interfaceCount ? 8694 : 14773, 250, 1753);
        player3 = player;
        player3.packetSender.sendInterfaceModel(14774 >= InterfaceDefinition.interfaceCount ? 8699 : 14774, 250, 1751);
        player3 = player;
        player3.packetSender.sendInterfaceModel(14771, 250, 6287);
        player3 = player;
        player3.packetSender.sendInterfaceModel(14775 >= InterfaceDefinition.interfaceCount ? 8704 : 14775, 250, 1749);
        player3 = player;
        player3.packetSender.sendInterfaceModel(14776 >= InterfaceDefinition.interfaceCount ? 8709 : 14776, 250, 1747);
        player3 = player;
        player3.packetSender.sendInterfaceText("Snakeskin", 14779);
        if (player.getInventoryManager().getItemAmount(995) >= 15) {
            player3 = player;
            player3.packetSender.sendInterfaceText(String.valueOf(n * 15) + " coins", 14787);
        } else {
            player3 = player;
            player3.packetSender.sendInterfaceText(String.valueOf(n * 15) + " coins", 14787);
        }
        player3 = player;
        player3.packetSender.sendInterfaceText("", 14780);
        player3 = player;
        player3.packetSender.sendInterfaceText("", 14788);
        object = new int[]{14781, 14789, 14783, 14791, 14782, 14790, 14784, 14792};
        String[] stringArray = new String[]{"Green d'hide", "Red d'hide", "Blue d'hide", "Black d'hide"};
        String[] stringArray2 = new String[]{String.valueOf(n * 20) + " coins", String.valueOf(n * 20) + " coins", String.valueOf(n * 20) + " coins", String.valueOf(n * 20) + " coins"};
        boolean bl = false;
        int n2 = 0;
        while (n2 < 8) {
            if (!bl) {
                Player player4 = player;
                player4.packetSender.sendInterfaceText(stringArray[n2 / 2], ((int[])object)[n2]);
                bl = true;
            } else {
                Player player5;
                if (player.getInventoryManager().getItemAmount(995) > 0) {
                    player5 = player;
                    player5.packetSender.sendInterfaceText(stringArray2[n2 / 2], ((int[])object)[n2]);
                } else {
                    player5 = player;
                    player5.packetSender.sendInterfaceText(stringArray2[n2 / 2], ((int[])object)[n2]);
                }
                bl = false;
            }
            ++n2;
        }
    }

    public static void tanHide(Player object, int n, int n2, int n3, int n4) {
        int n5;
        int n6 = n5 = ((Player)object).getInteractionTargetId() == 1041 ? 2 : 1;
        if (n > ((Player)object).getInventoryManager().getContainer().getItemAmount(n3)) {
            n = ((Player)object).getInventoryManager().getContainer().getItemAmount(n3);
        }
        ItemStack itemStack = new ItemStack(995, n2 * n * n5);
        Player player = object;
        player.packetSender.closeInterfaces();
        if (!ServerSettings.craftingEnabled) {
            player = object;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        player = object;
        if (player.interfaceAction != "tanning") {
            return;
        }
        if (!((Player)object).getInventoryManager().getContainer().containsItem(n3)) {
            ((Player)object).getDialogueManager().showOneLineStatement("You don't have enough rough hides in your inventory.");
            if (((Player)object).botEnabled) {
                ((Player)object).currentBotTask.startWalkToBank((Player)object);
            }
            return;
        }
        if (!((Player)object).getInventoryManager().getContainer().containsItem(995)) {
            ((Player)object).getDialogueManager().showOneLineStatement("You do not have enough coins.");
            if (((Player)object).botEnabled) {
                ((Player)object).currentBotTask.startWalkToBank((Player)object);
            }
            return;
        }
        ((Player)object).getInventoryManager().removeItem(itemStack);
        ((Player)object).getInventoryManager().removeItem(new ItemStack(n3, n));
        ((Player)object).getInventoryManager().addItem(new ItemStack(n4, n));
        Player player2 = object;
        player = player2;
        player2.interfaceAction = "";
    }

    public static boolean handleFarmingPatchObjectAction(Player player, int n, int n2) {
        if (player.getAllotmentPatchManager().harvestPatch(n, n2)) {
            return true;
        }
        if (player.getFlowerPatchManager().harvestPatch(n, n2)) {
            return true;
        }
        if (player.getHerbPatchManager().harvestPatch(n, n2)) {
            return true;
        }
        if (player.getHopsPatchManager().harvestPatch(n, n2)) {
            return true;
        }
        if (player.getBushPatchManager().harvestPatch(n, n2)) {
            return true;
        }
        if (player.getTreePatchManager().checkHealth(n, n2)) {
            return true;
        }
        if (player.getTreePatchManager().startCuttingTree(n, n2)) {
            return true;
        }
        if (player.getFruitTreePatchManager().harvestPatch(n, n2)) {
            return true;
        }
        if (player.getSpecialTreePatchManager().handleSpecialTreeObject(n, n2)) {
            return true;
        }
        return player.getSpecialCropPatchManager().harvestPatch(n, n2);
    }

    public static boolean handleCombinationRunecrafting(Player player, int n, int n2) {
        CombinationRuneDefinition combinationRuneDefinition = CombinationRuneDefinition.forTalismanAndAltarObjectId(n, n2);
        if (combinationRuneDefinition == null) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!player.isMember()) {
            player.packetSender.sendGameMessage("You need a members account to access members content.");
            return true;
        }
        if (ServerSettings.freeToPlayWorld) {
            player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return true;
        }
        if (player.getQuestState(14) != 1) {
            Object object = QuestDefinition.forId(14);
            String string = ((QuestDefinition)object).getName();
            object = player;
            ((Player)object).packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(combinationRuneDefinition.getTalismanItemId())) {
            Player player3 = player;
            PacketSender packetSender = player3.packetSender;
            StringBuilder stringBuilder = new StringBuilder("You need a ");
            ItemService.getInstance();
            packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(combinationRuneDefinition.getTalismanItemId()).toLowerCase()).append(" to do this.").toString());
            return true;
        }
        if (!player.getInventoryManager().containsItem(combinationRuneDefinition.getRuneItemId()) || !player.getInventoryManager().containsItem(RunecraftingHandler.PURE_ESSENCE_ITEM_ID)) {
            Player player4 = player;
            player4.packetSender.sendGameMessage("You need pure essences to do this.");
            return true;
        }
        if (player.getSkillManager().getBaseLevel(20) < combinationRuneDefinition.getRequiredLevel()) {
            Player player5 = player;
            player5.packetSender.sendGameMessage("You need a runecrafting level of " + combinationRuneDefinition.getRequiredLevel() + " to do this.");
            return true;
        }
        player.getInventoryManager().removeItem(new ItemStack(combinationRuneDefinition.getTalismanItemId(), 1));
        Player player6 = player;
        player6.packetSender.sendGameMessage("You attempt to bind the Runes.");
        player6 = player;
        player6.packetSender.sendSoundEffect(481, 1, 0);
        player.getUpdateState().setAnimation(791);
        player.getUpdateState().setGraphicHeight100(186);
        int n3 = player.getInventoryManager().getItemAmount(RunecraftingHandler.PURE_ESSENCE_ITEM_ID);
        int n4 = player.getInventoryManager().getItemAmount(combinationRuneDefinition.getRuneItemId());
        n3 = n3 > n4 ? n4 : n3;
        n4 = 0;
        if (player.getEquipmentManager().getItemIdAtSlot(2) == 5521) {
            n4 = n3;
        } else {
            int n5 = 0;
            while (n5 < n3) {
                if (GameUtil.randomInclusive(1) == 0) {
                    ++n4;
                }
                ++n5;
            }
        }
        player.getInventoryManager().removeItem(new ItemStack(combinationRuneDefinition.getRuneItemId(), n3));
        player.getInventoryManager().removeItem(new ItemStack(RunecraftingHandler.PURE_ESSENCE_ITEM_ID, n3));
        player.getInventoryManager().addItem(new ItemStack(combinationRuneDefinition.getProductRuneItemId(), n4));
        player.getSkillManager().addExperience(20, combinationRuneDefinition.getExperience() * (double)n4);
        if (player.getEquipmentManager().getItemIdAtSlot(2) == 5521) {
            player.setBindingNecklaceCharge(player.getBindingNecklaceCharge() - 1);
        }
        if (player.getBindingNecklaceCharge() <= 0) {
            player.setBindingNecklaceCharge(15);
            player.getEquipmentManager().replaceSlotItem(5521, 2);
            Player player7 = player;
            player7.packetSender.sendGameMessage("Your binding necklace crumble into dust.");
        }
        return true;
    }

    public static boolean fillEssencePouch(Player player, int n) {
        EssencePouchDefinition essencePouchDefinition = EssencePouchDefinition.forItemOrIndex(n);
        if (essencePouchDefinition == null) {
            return false;
        }
        if (n != essencePouchDefinition.getItemId() && n != essencePouchDefinition.getDegradedItemId()) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (player.getQuestState(14) != 1) {
            Object object = QuestDefinition.forId(14);
            object = ((QuestDefinition)object).getName();
            Player player3 = player;
            player3.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
            return true;
        }
        int n2 = player.getSkillManager().getBaseLevel(20);
        if (n2 < essencePouchDefinition.getRequiredLevel()) {
            Player player4 = player;
            player4.packetSender.sendGameMessage("You need " + essencePouchDefinition.getRequiredLevel() + " Runecrafting to use this pouch.");
            return true;
        }
        ItemStack itemStack = player.getInventoryManager().getContainer().findFlatItem(n);
        int n3 = player.getInventoryManager().getContainer().indexOfItem(n);
        while (true) {
            int n4;
            boolean bl = false;
            if (n == essencePouchDefinition.getDegradedItemId()) {
                bl = true;
            }
            if ((n4 = player.getInventoryManager().getContainer().getItemAmount(7936)) <= 0) {
                Player player5 = player;
                player5.packetSender.sendGameMessage("You don't have any more Pure essence.");
                break;
            }
            n4 = essencePouchDefinition.getCapacity();
            if (bl) {
                EssencePouchDefinition essencePouchDefinition2 = essencePouchDefinition;
                n4 = essencePouchDefinition2.getCapacity() - essencePouchDefinition2.getDegradedCapacityPenalty();
            }
            if ((n4 -= player.getEssencePouchAmount(essencePouchDefinition.getPouchIndex())) <= 0) {
                Player player6 = player;
                PacketSender packetSender = player6.packetSender;
                StringBuilder stringBuilder = new StringBuilder("Your ");
                ItemService.getInstance();
                packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n)).append(" is full.").toString());
                break;
            }
            player.setEssencePouchAmount(essencePouchDefinition.getPouchIndex(), player.getEssencePouchAmount(essencePouchDefinition.getPouchIndex()) + 1);
            if (essencePouchDefinition.getDegradedItemId() != -1) {
                itemStack.setMetadata(itemStack.getMetadata() + 1);
            }
            if (!bl && itemStack.getMetadata() >= essencePouchDefinition.getDegradeAfterUses() && essencePouchDefinition.getDegradedItemId() != -1) {
                player.getInventoryManager().getContainer().setItem(n3, new ItemStack(essencePouchDefinition.getDegradedItemId()));
                n = essencePouchDefinition.getDegradedItemId();
                Player player7 = player;
                PacketSender packetSender = player7.packetSender;
                StringBuilder stringBuilder = new StringBuilder("Your ");
                ItemService.getInstance();
                packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n)).append(" degraded.").toString());
            }
            player.getInventoryManager().removeItem(new ItemStack(7936, 1));
        }
        return true;
    }

    public static int selectNextAvailableEssencePouch(Entity entity, int n) {
        if (!entity.isPlayer()) {
            return -1;
        }
        entity = (Player)entity;
        int n2 = n;
        boolean bl = true;
        if (((Player)entity).ownsItem(n)) {
            bl = false;
        }
        while (!bl) {
            EssencePouchDefinition essencePouchDefinition;
            n = n2;
            EssencePouchDefinition essencePouchDefinition2 = EssencePouchDefinition.forItemOrIndex(n);
            int n3 = n = essencePouchDefinition2 != null && (n == essencePouchDefinition2.getItemId() || n == essencePouchDefinition2.getDegradedItemId()) && essencePouchDefinition2.getPouchIndex() != 3 && (essencePouchDefinition = EssencePouchDefinition.forItemOrIndex(essencePouchDefinition2.getPouchIndex() + 1)) != null && essencePouchDefinition.getPouchIndex() == essencePouchDefinition2.getPouchIndex() + 1 ? essencePouchDefinition.getItemId() : -1;
            if (!((Player)entity).ownsItem(n) || n == -1) {
                bl = true;
                continue;
            }
            bl = false;
            n2 = n;
        }
        return n;
    }

    public static boolean handleTiaraCrafting(Player player, int n, int n2) {
        RunecraftingAltarDefinition altarDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.getTalismanItemId() && n2 == candidate.getAltarObjectId()) {
                altarDefinition = candidate;
                break;
            }
        }
        if (altarDefinition == null) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return true;
        }
        if (player.getInventoryManager().containsItem(5525)) {
            player.getInventoryManager().removeItem(new ItemStack(5525, 1));
            player.getInventoryManager().removeItem(new ItemStack(altarDefinition.getTalismanItemId(), 1));
            player.getInventoryManager().addItem(new ItemStack(altarDefinition.getTiaraItemId(), 1));
            player.getSkillManager().addExperience(20, altarDefinition.getTiaraExperience());
            player.packetSender.sendGameMessage("You bind the power of the talisman into the tiara.");
        }
        return true;
    }

    public static void refreshRunecraftingTiaraConfig(Player player, int n) {
        Object object = new int[][]{{5527, 1}, {5529, 2}, {5531, 4}, {5535, 8}, {5537, 16}, {5533, 32}, {5539, 64}, {5543, 512}, {5541, 256}, {5545, 128}, {5547, 1024}, {5551, 2048}, {5549, 4096}};
        int[][] nArrayArray = (int[][])object;
        int n2 = 0;
        while (n2 < 13) {
            int[] nArray = nArrayArray[n2];
            object = nArray;
            if (nArray[0] == n) {
                player.packetSender.sendConfig(491, nArray[1]);
                return;
            }
            ++n2;
        }
        player.packetSender.sendConfig(491, 0);
    }

    public static boolean handleAnagramClueNpc(Player player, int n) {
        AnagramClue anagramClue = AnagramClue.forNpcId(n);
        if (anagramClue == null) {
            return false;
        }
        if (!player.getInventoryManager().getContainer().containsItem(anagramClue.getClueItemId())) {
            return false;
        }
        player.getDialogueManager().setDialogueNpcId(n);
        if (anagramClue.getFollowupType() == "Challenge") {
            if (CacheArchive.hasChallengeQuestionAnswerItem(player, anagramClue.getClueItemId())) {
                player.activeClueLevel = anagramClue.getLevel();
                player.activeClueItemId = anagramClue.getClueItemId();
                DialogueManager.a(player, 10009, 3);
                if (CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId()).length == 1) {
                    player.getDialogueManager().showNpcOneLineDialogue(CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId())[0], 588);
                } else if (CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId()).length == 2) {
                    player.getDialogueManager().showNpcTwoLineDialogue(CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId())[0], CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId())[1], 588);
                } else if (CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId()).length == 3) {
                    player.getDialogueManager().showNpcThreeLineDialogue(CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId())[0], CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId())[1], CacheArchive.getChallengeQuestionLines(anagramClue.getClueItemId())[2], 588);
                }
            } else {
                player.clueRequiredItems = new ItemStack[1];
                player.clueRequiredItems[0] = new ItemStack(anagramClue.getClueItemId(), 1);
                player.getDialogueManager().showNpcOneLineDialogue("Here's a challenge for you.", 588);
                CacheArchive.giveChallengeQuestionAnswerItem(player, anagramClue.getClueItemId());
            }
        } else if (anagramClue.getFollowupType() == "Puzzle") {
            if (PuzzleBoxHandler.isCluePuzzleSolved(player) && player.ownsCluePuzzleBox()) {
                DialogueManager.a(player, 10009, 2);
                player.clueRequiredItems = new ItemStack[4];
                player.clueRequiredItems[0] = new ItemStack(anagramClue.getClueItemId(), 1);
                player.clueRequiredItems[1] = new ItemStack(2800, 1);
                player.clueRequiredItems[2] = new ItemStack(3571, 1);
                player.clueRequiredItems[3] = new ItemStack(3565, 1);
                player.activeClueLevel = anagramClue.getLevel();
            } else if (player.ownsCluePuzzleBox()) {
                player.getDialogueManager().showNpcOneLineDialogue("The puzzle doesn't seem to be complete yet.", 588);
            } else {
                player.getDialogueManager().showNpcOneLineDialogue("Hello, Solve this puzzle for me please.", 588);
                PuzzleBoxHandler.giveRandomPuzzleBox(player);
            }
        } else {
            DialogueManager.a(player, 10009, 2);
            player.getDialogueManager().showNpcOneLineDialogue("Thank you very much.", 588);
            player.clueRequiredItems = new ItemStack[1];
            player.clueRequiredItems[0] = new ItemStack(anagramClue.getClueItemId(), 1);
            player.activeClueLevel = anagramClue.getLevel();
        }
        return true;
    }

    public static int randomAnagramClueItemForLevel(int n) {
        int n2 = new Random().nextInt(AnagramClue.values().length);
        while (AnagramClue.values()[n2].getLevel() != n) {
            n2 = new Random().nextInt(AnagramClue.values().length);
        }
        return AnagramClue.values()[n2].getClueItemId();
    }

    public static void openSextantInterface(Player player) {
        Player player2 = player;
        player.sextantSunAngleDegrees = 0.0;
        player2.sextantSunVerticalOffset = 0;
        player2.sextantSunHorizontalOffset = 0;
        player2.sextantHorizonRotation = 0;
        player2.sextantHorizonVerticalOffset = 0;
        player2 = player;
        double d = -32.0 + (double)player2.sextantSunHorizontalOffset / 5.7 + (double)GameUtil.randomInclusive((int)(32.0 + (double)player2.sextantSunHorizontalOffset / 5.7 + 28.0 + (double)player2.sextantSunHorizontalOffset / 5.7));
        int n = (int)(28.0 + (double)player2.sextantSunHorizontalOffset / 5.7 - d);
        int n2 = Math.abs((int)(d + 32.0 + (double)player2.sextantSunHorizontalOffset / 5.7));
        n = GameUtil.randomInclusive(1) == 0 ? GameUtil.randomInclusive(n2) << 1 : -GameUtil.randomInclusive(n) << 1;
        double d2 = -10.175438596491228 + (double)GameUtil.randomInclusive(17);
        n2 = (int)(7.017543859649122 - d2);
        int n3 = Math.abs((int)(d2 + 10.175438596491228));
        n2 = GameUtil.randomInclusive(1) == 0 ? GameUtil.randomInclusive(n3) : -GameUtil.randomInclusive(n2);
        player2.sextantHorizonVerticalOffset = (int)((double)player2.sextantHorizonVerticalOffset + (double)n2 * 5.7);
        player2.sextantHorizonRotation = (int)((double)player2.sextantHorizonRotation + d2 * 5.7);
        player2.sextantSunVerticalOffset += n;
        player2.sextantSunAngleDegrees = d;
        player2 = player;
        player2.packetSender.showInterface(6946);
        GameplayHelper.refreshSextantInterface(player);
    }

    public static void refreshSextantInterface(Player player) {
        double d = player.sextantSunAngleDegrees;
        Player player2 = player;
        double d2 = d < -32.0 + (double)player2.sextantSunHorizontalOffset / 5.7 ? -32.0 + (double)player2.sextantSunHorizontalOffset / 5.7 : (d > 28.0 + (double)player2.sextantSunHorizontalOffset / 5.7 ? 28.0 + (double)player2.sextantSunHorizontalOffset / 5.7 : d);
        int n = d2 > 0.0 ? (int)(d2 * 5.7) : 2047 - (int)(-d2 * 5.7);
        int n2 = (int)Math.round(Math.abs(140.0 * Math.sin(d2 * Math.PI / 180.0)));
        int n3 = (int)Math.floor(Math.abs(140.0 * (1.0 - Math.cos(d2 * Math.PI / 180.0))));
        Player player3 = player2;
        player3.packetSender.sendInterfaceModelRotation(6957, 513, n, 580);
        player3 = player2;
        player3.packetSender.sendInterfaceOffset(d2 > 0.0 ? -(n2 - player2.sextantSunHorizontalOffset / 2) : n2 + player2.sextantSunHorizontalOffset / 2, -n3, 6957);
        player3 = player2 = player;
        player2.packetSender.sendInterfaceOffset(0, player2.sextantSunVerticalOffset, 6949);
        player2 = player;
        n = player2.sextantHorizonRotation < -58 ? -58 : (player2.sextantHorizonRotation > 40 ? 40 : player2.sextantHorizonRotation);
        n2 = n > 0 ? n : 2047 - -n;
        player3 = player2;
        player3.packetSender.sendInterfaceModelRotation(6958, 513, n2, 555);
        player3 = player2;
        player3.packetSender.sendInterfaceModelRotation(6956, 513, n2, 555);
        player3 = player2 = player;
        player2.packetSender.sendInterfaceOffset(0, player2.sextantHorizonVerticalOffset, 6948);
    }

    public static void adjustSextantSun(Player player, boolean bl) {
        boolean bl2;
        player.sextantSunAngleDegrees = bl ? (player.sextantSunAngleDegrees += 1.0) : (player.sextantSunAngleDegrees -= 1.0);
        boolean bl3 = player.sextantSunAngleDegrees > 28.0 + (double)player.sextantSunHorizontalOffset / 5.7;
        boolean bl4 = bl2 = player.sextantSunAngleDegrees < -32.0 + (double)player.sextantSunHorizontalOffset / 5.7;
        if (!bl3 && !bl2) {
            boolean bl5 = bl;
            Player player2 = player;
            player2.sextantSunVerticalOffset = bl5 ? (player2.sextantSunVerticalOffset += 2) : (player2.sextantSunVerticalOffset -= 2);
            GameplayHelper.refreshSextantInterface(player2);
        }
        if (bl3) {
            player.sextantSunAngleDegrees = 28.0 + (double)player.sextantSunHorizontalOffset / 5.7;
        }
        if (bl2) {
            player.sextantSunAngleDegrees = -32.0 + (double)player.sextantSunHorizontalOffset / 5.7;
        }
        GameplayHelper.refreshSextantInterface(player);
    }

    public static void adjustSextantHorizon(Player player, boolean bl) {
        boolean bl2;
        if (bl) {
            player.sextantHorizonRotation += 7;
            if (player.sextantHorizonRotation <= 40 && player.sextantHorizonRotation >= -58) {
                player.sextantSunAngleDegrees += 1.2280701754385965;
                player.sextantSunHorizontalOffset += 7;
            }
        } else {
            player.sextantHorizonRotation -= 7;
            if (player.sextantHorizonRotation <= 40 && player.sextantHorizonRotation >= -58) {
                player.sextantSunAngleDegrees -= 1.2280701754385965;
                player.sextantSunHorizontalOffset -= 7;
            }
        }
        boolean bl3 = player.sextantHorizonRotation > 40;
        boolean bl4 = bl2 = player.sextantHorizonRotation < -58;
        if (!bl3 && !bl2) {
            boolean bl5;
            boolean bl6 = bl;
            Player player2 = player;
            player2.sextantHorizonVerticalOffset = bl6 ? (player2.sextantHorizonVerticalOffset += 7) : (player2.sextantHorizonVerticalOffset -= 7);
            GameplayHelper.refreshSextantInterface(player2);
            bl6 = player2.sextantHorizonVerticalOffset > 70;
            boolean bl7 = bl5 = player2.sextantHorizonVerticalOffset < -70;
            if (bl6) {
                player2.sextantHorizonVerticalOffset = 70;
            }
            if (bl5) {
                player2.sextantHorizonVerticalOffset = -70;
            }
        }
        if (bl3) {
            player.sextantHorizonRotation = 40;
        }
        if (bl2) {
            player.sextantHorizonRotation = -58;
        }
        GameplayHelper.refreshSextantInterface(player);
    }

    public static boolean handleSextantButtonClick(Player object, int n) {
        switch (n) {
            case 6955: {
                GameplayHelper.adjustSextantSun((Player)object, true);
                return true;
            }
            case 6954: {
                GameplayHelper.adjustSextantSun((Player)object, false);
                return true;
            }
            case 6953: {
                GameplayHelper.adjustSextantHorizon((Player)object, true);
                return true;
            }
            case 6952: {
                GameplayHelper.adjustSextantHorizon((Player)object, false);
                return true;
            }
            case 6959: {
                boolean bl;
                Player player;
                Player player2 = object;
                if (Math.abs(player2.sextantHorizonVerticalOffset) > 7) {
                    player = player2;
                    player.packetSender.sendGameMessage("You need to get the horizon in the middle of the eye piece.");
                    bl = false;
                } else if (player2.sextantSunVerticalOffset != 0) {
                    player = player2;
                    player.packetSender.sendGameMessage("You need to get the sun in the middle of the eye piece.");
                    bl = false;
                } else if (!(player2.getInventoryManager().getContainer().containsItem(2574) && player2.getInventoryManager().getContainer().containsItem(2576) && player2.getInventoryManager().getContainer().containsItem(2575))) {
                    player2.getDialogueManager().showOneLineStatement("You need a watch and navigator's chart to work out your Position.");
                    bl = false;
                } else {
                    bl = true;
                }
                if (bl) {
                    player2 = object;
                    String[] coordinateLines = CoordinateClueHandler.formatPositionAsCoordinate(player2.getPosition().getX(), player2.getPosition().getY());
                    String coordinateLine = coordinateLines[0];
                    String string = coordinateLines[1];
                    player2.getDialogueManager().showTwoLineStatement(coordinateLine, string);
                    player = player2;
                    player.packetSender.sendGameMessage("the sextant displays:");
                    player = player2;
                    player.packetSender.sendGameMessage(coordinateLine);
                    player = player2;
                    player.packetSender.sendGameMessage(string);
                }
                return true;
            }
        }
        return false;
    }

    public static void loadNpcSpawns() {
        int n = 0;
        new StringBuilder("0");
        try {
            int n2;
            byte[] byArray = FileUtil.readBytes("./data/npcs/Npc spawn.dat");
            ByteArrayReader byteArrayReader = new ByteArrayReader(byArray);
            byteArrayReader.readUnsignedByte();
            int n3 = n2 = byteArrayReader.readUnsignedShort();
            int n4 = 0;
            while (n4 < n2) {
                block9: {
                    int n5;
                    int n6;
                    int n7;
                    int n8;
                    int n9;
                    block10: {
                        block30: {
                            block29: {
                                block28: {
                                    block27: {
                                        block26: {
                                            block25: {
                                                block24: {
                                                    block23: {
                                                        block22: {
                                                            block21: {
                                                                block20: {
                                                                    block19: {
                                                                        block18: {
                                                                            block17: {
                                                                                block16: {
                                                                                    block15: {
                                                                                        block14: {
                                                                                            block13: {
                                                                                                block12: {
                                                                                                    block11: {
                                                                                                        n9 = byteArrayReader.readUnsignedShort() - n3 - n4;
                                                                                                        int n10 = byteArrayReader.readUnsignedByte();
                                                                                                        n8 = byteArrayReader.readUnsignedByte();
                                                                                                        n7 = byteArrayReader.readUnsignedShort();
                                                                                                        n6 = byteArrayReader.readUnsignedShort();
                                                                                                        n5 = byteArrayReader.readUnsignedByte();
                                                                                                        if (n9 > 3851 || ServerSettings.freeToPlayWorld && n10 == 1 || n9 == 3852 || n9 == 3853 && ServerSettings.freeToPlayWorld) break block9;
                                                                                                        if (ServerSettings.cacheVersion < 319 && n9 == 50) {
                                                                                                            n7 = 2718;
                                                                                                            n6 = 9823;
                                                                                                            n5 = 0;
                                                                                                        }
                                                                                                        if (n9 == 1800 || n9 == 3809 || n9 == 3810 || n9 == 3811 || n9 == 3812) {
                                                                                                            n9 = 170;
                                                                                                        }
                                                                                                        if (NpcDefinition.isDefined(n9)) break block10;
                                                                                                        if (n9 != 1757) break block11;
                                                                                                        n9 = 7;
                                                                                                        break block10;
                                                                                                    }
                                                                                                    if (n9 < 1762 || n9 > 1765) break block12;
                                                                                                    n9 = 43;
                                                                                                    break block10;
                                                                                                }
                                                                                                if (n9 < 1769 || n9 > 1776) break block13;
                                                                                                n9 = 100;
                                                                                                break block10;
                                                                                            }
                                                                                            if (n9 == 1862 || n9 == 2239 || n9 == 2240 || n9 == 2242 || n9 == 2243 || n9 == 2234 || n9 == 2236 || n9 == 2237 || n9 == 2238 || n9 == 2244 || n9 == 2253 || n9 == 2290 || n9 == 2304 || n9 == 2311 || n9 >= 2316 && n9 <= 2317 || n9 == 2323 || n9 == 2333 || n9 == 2335 || n9 == 2340 || n9 == 2341 || n9 == 2342) break block9;
                                                                                            if (n9 != 2661) break block14;
                                                                                            n9 = 647;
                                                                                            break block10;
                                                                                        }
                                                                                        if (n9 == 2693 || n9 == 2709 || n9 == 2710 || n9 == 2711 || n9 == 2712) break block9;
                                                                                        if (n9 != 2809) break block15;
                                                                                        n9 = 80;
                                                                                        break block10;
                                                                                    }
                                                                                    if (n9 != 2810) break block16;
                                                                                    n9 = 80;
                                                                                    break block10;
                                                                                }
                                                                                if (n9 != 2811) break block17;
                                                                                n9 = 80;
                                                                                break block10;
                                                                            }
                                                                            if (n9 != 2812) break block18;
                                                                            n9 = 80;
                                                                            break block10;
                                                                        }
                                                                        if (n9 != 2824) break block19;
                                                                        n9 = 804;
                                                                        break block10;
                                                                    }
                                                                    if (n9 == 3021) break block9;
                                                                    if (n9 != 3219) break block20;
                                                                    n9 = 118;
                                                                    break block10;
                                                                }
                                                                if (n9 != 3220) break block21;
                                                                n9 = 118;
                                                                break block10;
                                                            }
                                                            if (n9 != 3221) break block22;
                                                            n9 = 118;
                                                            break block10;
                                                        }
                                                        if (n9 != 3223) break block23;
                                                        n9 = 1;
                                                        break block10;
                                                    }
                                                    if (n9 != 3226) break block24;
                                                    n9 = 4;
                                                    break block10;
                                                }
                                                if (n9 != 3228) break block25;
                                                n9 = 9;
                                                break block10;
                                            }
                                            if (n9 != 3230) break block26;
                                            n9 = 9;
                                            break block10;
                                        }
                                        if (n9 != 3246 && n9 != 3249) break block27;
                                        n9 = 17;
                                        break block10;
                                    }
                                    if (n9 != 3247) break block28;
                                    n9 = 12;
                                    break block10;
                                }
                                if (n9 == 3295 || n9 == 3296) break block9;
                                if (n9 != 3307) break block29;
                                n9 = 33;
                                if (n != 0) break block9;
                                ++n;
                                break block10;
                            }
                            if (n9 != 3348) break block30;
                            n9 = 19;
                            break block10;
                        }
                        if (n9 == 3806 || n9 == 3809 || ServerSettings.skipUndefinedNpcSpawns) break block9;
                    }
                    if (CacheCoordinateTranslator.dungeonCoordinateShiftActive && CacheCoordinateTranslator.isDungeonCoordinateShiftSourceRegion(n7, n6)) {
                        n7 += 768;
                        n6 += 5120;
                    }
                    GameplayHelper.spawnNpc(n9, n7, n6, n5, n8);
                    NpcDefinition npcDefinition = NpcDefinition.forId(n9);
                    npcDefinition.getDropTableNpcIdOverride();
                }
                ++n4;
            }
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    public static void spawnNpc(int n, int n2, int n3, int n4, int n5) {
        if (n == 767 || n == 1826) {
            WalkingCollisionMap.addObjectCollision(2620, n2, n3, n4, 0, 10, true);
        }
        Npc npc = new Npc(n);
        npc.moveTo(new Position(n2, n3, n4));
        npc.setSpawnPosition(new Position(n2, n3, n4));
        npc.setRespawnEnabled(true);
        npc.setSpawnMinPosition(new Position(n2 - npc.getDefinition().getSpawnRadius(), n3 - npc.getDefinition().getSpawnRadius()));
        npc.setSpawnMaxPosition(new Position(n2 + npc.getDefinition().getSpawnRadius(), n3 + npc.getDefinition().getSpawnRadius()));
        npc.setMovementMode(n5 == 1 || n5 > 5 ? NpcMovementMode.ROAMING : NpcMovementMode.STATIONARY);
        npc.setFacingDirection(n5);
        npc.setSpawnX(n2);
        npc.setSpawnY(n3);
        npc.setRespawnEnabled(true);
        if (n == 1431 || n == 1432) {
            if (n == 1431) {
                Npc.scriptedStageCursor = 0;
                npc.scriptedPathStage = 0;
                npc.queueStageAdvancePath(npc.scriptedPathStage);
            }
            npc.setScriptedMovementEnabled(true);
            Npc.scriptedStageNpcs.add(npc);
        }
        if (n == 1454) {
            npc.scriptedPathStage = 0;
            npc.queueSequenceAdvancePath(npc.scriptedPathStage);
            npc.setScriptedMovementEnabled(true);
        }
        World.registerNpc(npc);
    }

    public static void spawnNonRespawningNpc(Npc npc, int n, int n2, int n3, int n4) {
        npc.moveTo(new Position(n, n2, n3));
        npc.setSpawnPosition(new Position(n, n2, n3));
        npc.setRespawnEnabled(false);
        npc.setSpawnMinPosition(new Position(n - npc.getDefinition().getSpawnRadius(), n2 - npc.getDefinition().getSpawnRadius()));
        npc.setSpawnMaxPosition(new Position(n + npc.getDefinition().getSpawnRadius(), n2 + npc.getDefinition().getSpawnRadius()));
        npc.setMovementMode(n4 == 1 || n4 > 5 ? NpcMovementMode.ROAMING : NpcMovementMode.STATIONARY);
        npc.setFacingDirection(n4);
        npc.setSpawnX(n);
        npc.setSpawnY(n2);
        World.registerNpc(npc);
    }

    public static void spawnOwnedNpcAtPosition(Player player, Position object, Npc npc, boolean bl, boolean bl2) {
        npc.moveTo((Position)object);
        npc.setSpawnPosition((Position)object);
        npc.setMovementMode(NpcMovementMode.STATIONARY);
        npc.setSpawnX(((Position)object).getX());
        npc.setSpawnY(((Position)object).getY());
        World.registerNpc(npc);
        player.H = npc;
        npc.setOwnerPlayerIndex(player.getIndex());
        npc.getUpdateState().setFacePosition(player.getPosition());
        if (bl) {
            CombatManager.startCombat(npc, player);
        }
    }

    public static void spawnOwnedNpcAdjacentToPlayer(Player player, Npc npc, boolean bl, boolean bl2) {
        int n = 0;
        int n2 = 0;
        if (player.canStepToOffset(1, 0)) {
            n = 1;
            n2 = 0;
        } else if (player.canStepToOffset(-1, 0)) {
            n = -1;
            n2 = 0;
        } else if (player.canStepToOffset(0, 1)) {
            n = 0;
            n2 = 1;
        } else if (player.canStepToOffset(0, -1)) {
            n = 0;
            n2 = -1;
        }
        n = player.getPosition().getX() + n;
        n2 = player.getPosition().getY() + n2;
        npc.moveTo(new Position(n, n2, player.getPosition().getPlane()));
        npc.setSpawnPosition(new Position(n, n2, player.getPosition().getPlane()));
        npc.setMovementMode(NpcMovementMode.STATIONARY);
        npc.setSpawnX(n);
        npc.setSpawnY(n2);
        World.registerNpc(npc);
        Npc npc2 = npc;
        Player player2 = player;
        player.H = npc2;
        npc.setOwnerPlayerIndex(player.getIndex());
        npc.getUpdateState().setFacePosition(player.getPosition());
        if (bl) {
            CombatManager.startCombat(npc, player);
        } else {
            npc.setAttackRange(1);
            npc.setMovementTarget(player);
        }
        if (bl2) {
            player2 = player;
            player2.packetSender.sendEntityHintIcon(1, npc.getIndex());
        }
        if (npc.getNpcId() == 77) {
            npc.getUpdateState().setGraphic(GraphicEffect.createHeight0(78));
        }
    }

    public static void spawnRoamingNpcFacingPlayer(Player player, Npc npc, int n, int n2, int n3, int n4, boolean bl, boolean bl2) {
        npc.moveTo(new Position(n, n2, n3));
        npc.setSpawnPosition(new Position(n, n2, n3));
        npc.setMovementMode(NpcMovementMode.ROAMING);
        npc.setSpawnX(n);
        npc.setSpawnY(n2);
        npc.setSpawnMinPosition(new Position(n - npc.getDefinition().getSpawnRadius(), n2 - npc.getDefinition().getSpawnRadius()));
        npc.setSpawnMaxPosition(new Position(n + npc.getDefinition().getSpawnRadius(), n2 + npc.getDefinition().getSpawnRadius()));
        World.registerNpc(npc);
        npc.getUpdateState().setAnimation(n4);
        npc.getUpdateState().setFacePosition(player.getPosition());
        if (npc.getNpcId() == 77) {
            npc.getUpdateState().setGraphic(GraphicEffect.createHeight0(78));
        }
    }

    public static void spawnNpcWithRemovalDelay(Npc npc, int n, int n2, int n3, int n4) {
        npc.moveTo(new Position(n, n2, n3));
        npc.setSpawnPosition(new Position(n, n2, n3));
        npc.setSpawnX(n);
        npc.setSpawnY(n2);
        npc.setRemovalDelayTicks(n4);
        npc.setFaceEntityUpdateDisabled(true);
        World.registerNpc(npc);
    }

    public static boolean replaceOwnedRoamingNpcAtPosition(Player player, Npc npc, int n, int n2, int n3, int n4, boolean bl, boolean bl2) {
        Player player2 = player;
        if (player2.H != null) {
            player2 = player;
            if (!player2.H.isDead()) {
                player2 = player;
                player2.H.setActive(false);
                player2 = player;
                World.unregisterNpc(player2.H);
            }
        }
        npc.moveTo(new Position(n, n2, n3));
        npc.setSpawnPosition(new Position(n, n2, n3));
        npc.setMovementMode(NpcMovementMode.ROAMING);
        npc.setSpawnX(n);
        npc.setSpawnY(n2);
        npc.setSpawnMinPosition(new Position(n - npc.getDefinition().getSpawnRadius(), n2 - npc.getDefinition().getSpawnRadius()));
        npc.setSpawnMaxPosition(new Position(n + npc.getDefinition().getSpawnRadius(), n2 + npc.getDefinition().getSpawnRadius()));
        World.registerNpc(npc);
        Npc npc2 = npc;
        player2 = player;
        player.H = npc2;
        npc.setOwnerPlayerIndex(player.getIndex());
        if (n4 != -1) {
            npc.getUpdateState().setAnimation(n4);
        }
        npc.getUpdateState().setFacePosition(player.getPosition());
        if (bl) {
            CombatManager.startCombat(npc, player);
        }
        if (bl2) {
            player2 = player;
            player2.packetSender.sendEntityHintIcon(1, npc.getIndex());
        }
        if (npc.getNpcId() == 77) {
            npc.getUpdateState().setGraphic(GraphicEffect.createHeight0(78));
        }
        return true;
    }

    public static void spawnOwnedGroundPlaneNpcAtPosition(Player player, Npc npc, int n, int n2, int n3, int n4, boolean bl, boolean bl2) {
        npc.moveTo(new Position(n, n2, 0));
        npc.setSpawnPosition(new Position(n, n2, 0));
        npc.setMovementMode(NpcMovementMode.ROAMING);
        npc.setSpawnX(n);
        npc.setSpawnY(n2);
        npc.setSpawnMinPosition(new Position(n - npc.getDefinition().getSpawnRadius(), n2 - npc.getDefinition().getSpawnRadius()));
        npc.setSpawnMaxPosition(new Position(n + npc.getDefinition().getSpawnRadius(), n2 + npc.getDefinition().getSpawnRadius()));
        World.registerNpc(npc);
        Npc npc2 = npc;
        Player player2 = player;
        player.H = npc2;
        npc.setOwnerPlayerIndex(player.getIndex());
        npc.getUpdateState().setAnimation(-1);
        npc.getUpdateState().setFacePosition(player.getPosition());
        if (bl) {
            CombatManager.startCombat(npc, player);
        }
        if (npc.getNpcId() == 77) {
            npc.getUpdateState().setGraphic(GraphicEffect.createHeight0(78));
        }
    }

    public static boolean hasActiveTemporaryNpc(Player player, int n) {
        Player player2 = player;
        if (player2.H != null) {
            player2 = player;
            if (!player2.H.isDead()) {
                player2 = player;
                if (player2.H.getNpcId() == n) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void spawnNpcTargetingEntityAtPosition(Entity entity, Npc npc, Position position, boolean bl, String string) {
        npc.moveTo(position);
        npc.setMovementMode(NpcMovementMode.STATIONARY);
        npc.setSpawnX(position.getX());
        npc.setSpawnY(position.getY());
        npc.setRespawnEnabled(false);
        World.registerNpc(npc);
        if (entity != null) {
            npc.setMovementTarget(entity);
            CombatManager.startCombat(npc, entity);
            npc.getUpdateState().setFacePosition(entity.getPosition());
        }
        entity.isPlayer();
    }

    public static void unregisterTemporaryNpc(Npc npc) {
        if (npc.getOwnerPlayer() != null) {
            npc.getOwnerPlayer().H = null;
        }
        npc.setActive(false);
        EntityTargetMovement.clearMovementTarget(npc);
        World.unregisterNpc(npc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void sendNpcUpdatePacket(Player player) {
        PacketWriter packetWriter;
        Object object;
        Npc npc;
        if (player.isBot) {
            return;
        }
        PacketWriter packetWriter2 = PacketBuffer.allocateWriter(8192);
        PacketWriter packetWriter3 = PacketBuffer.allocateWriter(4096);
        packetWriter2.startVariableShortPacket(player.getOutboundCipher(), 65);
        packetWriter2.setAccessMode(AccessMode.BIT_ACCESS);
        List list = player.getLocalNpcs();
        synchronized (list) {
            packetWriter2.writeBits(8, player.getLocalNpcs().size());
            Iterator iterator = player.getLocalNpcs().iterator();
            while (iterator.hasNext()) {
                npc = (Npc)iterator.next();
                if (npc.isActive() && npc.getPosition().isWithinViewport(player.getPosition()) && !npc.b) {
                    object = npc;
                    packetWriter = packetWriter2;
                    if (((Entity)object).getWalkDirection() == -1) {
                        if (((Entity)object).getUpdateState().isUpdateRequired()) {
                            packetWriter.writeBits(1, 1);
                            packetWriter.writeBits(2, 0);
                        } else {
                            packetWriter.writeBits(1, 0);
                        }
                    } else {
                        packetWriter.writeBits(1, 1);
                        packetWriter.writeBits(2, 1);
                        packetWriter.writeBits(3, ((Entity)object).getWalkDirection());
                        packetWriter.writeBoolean(true);
                    }
                    if (!npc.getUpdateState().isUpdateRequired()) continue;
                    GameplayHelper.writeNpcUpdateBlock(packetWriter3, npc);
                    continue;
                }
                if (npc.b) {
                    npc.b = false;
                }
                packetWriter2.writeBoolean(true);
                packetWriter2.writeBits(2, 3);
                iterator.remove();
            }
        }
        int n = 0;
        int n2 = 0;
        while (n2 < World.getNpcs().length) {
            if (n > 15) break;
            npc = World.getNpcs()[n2];
            if (npc != null && npc.isActive()) {
                List list2 = player.getLocalNpcs();
                synchronized (list2) {
                    if (!player.getLocalNpcs().contains(npc) && npc.getPosition().isWithinViewport(player.getPosition())) {
                        ++n;
                        player.getLocalNpcs().add(npc);
                        Npc npc2 = npc;
                        object = player;
                        packetWriter = packetWriter2;
                        packetWriter.writeBits(14, npc2.getIndex());
                        object = GameUtil.getDelta(((Entity)object).getPosition(), npc2.getPosition());
                        packetWriter.writeBits(5, ((Position)object).getY());
                        packetWriter.writeBits(5, ((Position)object).getX());
                        packetWriter.writeBits(1, 0);
                        packetWriter.writeBits(12, npc2.getNpcId());
                        packetWriter.writeBoolean(true);
                        if (npc.getUpdateState().isUpdateRequired()) {
                            GameplayHelper.writeNpcUpdateBlock(packetWriter3, npc);
                        }
                    }
                }
            }
            ++n2;
        }
        if (packetWriter3.getBuffer().position() > 0) {
            packetWriter2.writeBits(14, 16383);
            packetWriter2.setAccessMode(AccessMode.BYTE_ACCESS);
            packetWriter2.writeBuffer(packetWriter3.getBuffer());
        } else {
            packetWriter2.setAccessMode(AccessMode.BYTE_ACCESS);
        }
        packetWriter2.finishVariableShortPacket();
        player.writePacketBuffer(packetWriter2.getBuffer());
    }

    public static void writeNpcUpdateBlock(PacketWriter packetWriter, Npc npc) {
        int n = 0;
        if (npc.getUpdateState().isAnimationUpdateRequired()) {
            n = 16;
        }
        if (npc.getUpdateState().isPrimaryHitUpdateRequired()) {
            n |= 8;
        }
        if (npc.getUpdateState().isGraphicUpdateRequired()) {
            n |= 0x80;
        }
        if (npc.getUpdateState().isFaceEntityUpdateRequired()) {
            n |= 0x20;
        }
        if (npc.getUpdateState().isForcedTextUpdateRequired()) {
            n |= 1;
        }
        if (npc.getUpdateState().isSecondaryHitUpdateRequired()) {
            n |= 0x40;
        }
        if (npc.isTransformed()) {
            n |= 2;
        }
        if (npc.getUpdateState().isFacePositionUpdateRequired()) {
            n |= 4;
        }
        packetWriter.writeByte(n);
        if (npc.getUpdateState().isAnimationUpdateRequired()) {
            packetWriter.writeShort(npc.getUpdateState().getAnimationId(), ByteOrder.LITTLE);
            packetWriter.writeByte(npc.getUpdateState().getAnimationDelay());
        }
        if (npc.getUpdateState().isPrimaryHitUpdateRequired()) {
            n = npc.getUpdateState().getPrimaryHitDamage();
            packetWriter.writeByte(n, ByteTransform.ADD);
            packetWriter.writeByte(npc.getUpdateState().getPrimaryHitType(), ByteTransform.NEGATE);
            packetWriter.writeByte(GameplayHelper.calculatePercent(npc.getCurrentHitpoints(), npc.getMaxHitpoints(), 100), ByteTransform.ADD);
            packetWriter.writeByte(100);
        }
        if (npc.getUpdateState().isGraphicUpdateRequired()) {
            packetWriter.writeShort(npc.getUpdateState().getGraphicId());
            packetWriter.writeInt(npc.getUpdateState().getGraphicDelay());
        }
        if (npc.getUpdateState().isFaceEntityUpdateRequired()) {
            packetWriter.writeShort(npc.getUpdateState().getFaceEntityId());
        }
        if (npc.getUpdateState().isForcedTextUpdateRequired()) {
            packetWriter.writeString(npc.getUpdateState().getForcedText());
        }
        if (npc.getUpdateState().isSecondaryHitUpdateRequired()) {
            n = npc.getUpdateState().getSecondaryHitDamage();
            packetWriter.writeByte(n, ByteTransform.NEGATE);
            packetWriter.writeByte(npc.getUpdateState().getSecondaryHitType(), ByteTransform.SUBTRACT);
            packetWriter.writeByte(GameplayHelper.calculatePercent(npc.getCurrentHitpoints(), npc.getMaxHitpoints(), 100), ByteTransform.SUBTRACT);
            packetWriter.writeByte(100, ByteTransform.NEGATE);
        }
        if (npc.isTransformed() && npc.getTransformedNpcId() != -1) {
            packetWriter.writeShort(npc.getTransformedNpcId(), ByteTransform.ADD, ByteOrder.LITTLE);
        }
        if (npc.getUpdateState().isFacePositionUpdateRequired()) {
            Position position = npc.getUpdateState().getFacePosition();
            if (position == null) {
                packetWriter.writeShort(0, ByteOrder.LITTLE);
                packetWriter.writeShort(0, ByteOrder.LITTLE);
                return;
            }
            packetWriter.writeShort((position.getX() << 1) + 1, ByteOrder.LITTLE);
            packetWriter.writeShort((position.getY() << 1) + 1, ByteOrder.LITTLE);
        }
    }

    public static int calculatePercent(int n, int n2, int n3) {
        double d = (double)n / (double)n2;
        return (int)Math.round(d * 100.0);
    }

    public static void takeAxeFromLog(Player player, int n, int n2) {
        if (player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("Not enough space in your inventory.");
            return;
        }
        player.getInventoryManager().addItem(new ItemStack(1351));
        player.getUpdateState().setAnimation(832);
        Player player3 = player;
        player3.packetSender.sendGameMessage("You take the axe from the log.");
        new DynamicObject(5582, n, n2, player.getPosition().getPlane(), 2, 10, 5581, 100);
    }

    public static void withdrawCoalTruckCoal(Player player) {
        if (player.getCoalTruckCoalCount() == 0) {
            player.packetSender.sendGameMessage("There is no coal left in the truck.");
            return;
        }
        int n = player.getInventoryManager().getContainer().getFreeSlots();
        if (n == 0) {
            player.packetSender.sendGameMessage("Not enough space in your inventory.");
            return;
        }
        n = player.getCoalTruckCoalCount() < n ? player.getCoalTruckCoalCount() : n;
        player.getInventoryManager().addItem(new ItemStack(453, n));
        player.setCoalTruckCoalCount(player.getCoalTruckCoalCount() - n);
    }

    public static void milkCow(Player player) {
        if (!player.getInventoryManager().containsItemAmount(1925, 1)) {
            player.packetSender.sendGameMessage("You need a bucket in order to milk this cow.");
            return;
        }
        player.getUpdateState().setAnimation(2305);
        player.getInventoryManager().removeItem(new ItemStack(1925));
        player.getInventoryManager().addItem(new ItemStack(1927));
        player.packetSender.sendGameMessage("You milk the cow.");
    }

    public static void loadGroundItemSpawns() {
        try {
            int n;
            byte[] byArray = FileUtil.readBytes("./data/world/Item spawn.dat");
            ByteArrayReader byteArrayReader = new ByteArrayReader(byArray);
            byteArrayReader.readUnsignedByte();
            int n2 = n = byteArrayReader.readUnsignedShort();
            int n3 = 0;
            while (n3 < n) {
                int n4 = byteArrayReader.readUnsignedShort() - n2 - n3;
                int n5 = byteArrayReader.readUnsignedShort();
                int n6 = byteArrayReader.readUnsignedByte();
                int n7 = byteArrayReader.readUnsignedShort();
                int n8 = byteArrayReader.readUnsignedShort();
                int n9 = byteArrayReader.readUnsignedByte();
                int n10 = 100;
                if (n4 == 245) {
                    n10 = 20;
                }
                if (ServerSettings.cacheVersion < 298 && n4 == 1982 && n7 == 3085 && n8 == 3261) {
                    n7 = 3087;
                    n8 = 3261;
                }
                if (ServerSettings.cacheVersion < 298 && n4 == 1985 && n7 == 3083 && n8 == 3260) {
                    n7 = 3084;
                    n8 = 3261;
                }
                if (ItemDefinition.isDefined(n4) && (!ServerSettings.freeToPlayWorld || n6 != 1)) {
                    Object object = new ItemStack(n4, n5);
                    if (!ServerSettings.freeToPlayWorld || !((ItemStack)object).getDefinition().isMembersOnly()) {
                        n10 = (int)((double)n10 * ServerSettings.itemRespawnDelayMultiplier);
                        if (CacheCoordinateTranslator.dungeonCoordinateShiftActive && CacheCoordinateTranslator.isDungeonCoordinateShiftSourceRegion(n7, n8)) {
                            n7 += 768;
                            n8 += 5120;
                        }
                        object = new GroundItem((ItemStack)object, new Position(n7, n8, n9), (int)GameUtil.secondsToTicks(n10), true);
                        GroundItemManager.getInstance().spawn((GroundItem)object);
                    }
                }
                ++n3;
            }
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    public static void refreshTradeOfferInterfaces(Player player, Player player2) {
        Player player3 = player;
        player3.packetSender.sendItemContainer(3322, player.getInventoryManager().getContainer().getRawItems());
        player3 = player2;
        player3.packetSender.sendItemContainer(3322, player2.getInventoryManager().getContainer().getRawItems());
        player3 = player;
        player3.packetSender.sendItemContainer(3415, player.getTradeOfferContainer().getRawItems());
        player3 = player;
        player3.packetSender.sendItemContainer(3416, player2.getTradeOfferContainer().getRawItems());
        player3 = player2;
        player3.packetSender.sendItemContainer(3415, player2.getTradeOfferContainer().getRawItems());
        player3 = player2;
        player3.packetSender.sendItemContainer(3416, player.getTradeOfferContainer().getRawItems());
        player3 = player;
        player3.packetSender.sendInterfaceText("Trading With: " + player2.getUsername(), 3417);
        player3 = player2;
        player3.packetSender.sendInterfaceText("Trading With: " + player.getUsername(), 3417);
    }

    public static void handleTradeRequest(Player player, Player player2) {
        if (player.getQuestState(0) != 1) {
            player.pendingTradeTarget = null;
            return;
        }
        if (player.gameMode != 0) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("You are not playing on normal gamemode and cannot trade.");
            player.pendingTradeTarget = null;
            return;
        }
        if (player2.gameMode != 0) {
            Player player4 = player;
            player4.packetSender.sendGameMessage(String.valueOf(player2.getUsername()) + " is not playing on normal gamemode and cannot trade.");
            player.pendingTradeTarget = null;
            return;
        }
        if (!(ServerSettings.adminInteractionsAllowed || player.getPlayerRights() < 2 && player2.getPlayerRights() < 2)) {
            Player player5 = player;
            player5.packetSender.sendGameMessage("You are not allowed to trade this player.");
            player.pendingTradeTarget = null;
            return;
        }
        if (player2.getTradeRequestTarget() != player) {
            Player player6 = player;
            player6.packetSender.sendGameMessage("Sending trade offer...");
            player6 = player2;
            player6.packetSender.sendGameMessage(TextUtil.capitalizeFirst(player.getUsername()) + ":tradereq:");
            player.setTradeState(TradeState.REQUEST_SENT);
            player.setTradeRequestTarget(player2);
            if (player2.isBot) {
                player2.pendingTradeTarget = player;
                return;
            }
        } else {
            player.setTradeState(TradeState.OFFER_SCREEN);
            player2.setTradeState(TradeState.OFFER_SCREEN);
            Player player7 = player2;
            Player player8 = player;
            if (player8.botEnabled) {
                player8.tradeAdvertInitialOfferPlaced = false;
            }
            if (player7.botEnabled) {
                player7.tradeAdvertInitialOfferPlaced = false;
            }
            player8.getTradeOfferContainer().clear();
            player7.getTradeOfferContainer().clear();
            Player player9 = player8;
            player9.packetSender.sendInterfaceText("Trading With: " + player7.getUsername() + " has " + player7.getInventoryManager().getContainer().getFreeSlots() + " free inventory slots.", 3417);
            player9 = player7;
            player9.packetSender.sendInterfaceText("Trading With: " + player8.getUsername() + " has " + player8.getInventoryManager().getContainer().getFreeSlots() + " free inventory slots.", 3417);
            player9 = player8;
            player9.packetSender.sendInterfaceText("", 3431);
            player9 = player7;
            player9.packetSender.sendInterfaceText("", 3431);
            GameplayHelper.refreshTradeOfferInterfaces(player8, player7);
            player8.setTradePartner(player7);
            player7.setTradePartner(player8);
            player9 = player8;
            player9.packetSender.showInterfaceWithInventory(3323, 3321);
            player9 = player7;
            player9.packetSender.showInterfaceWithInventory(3323, 3321);
            player.setTradeRequestTarget(null);
            player2.setTradeRequestTarget(null);
        }
    }

    public static void declineTrade(Player player) {
        if (player.getTradePartner() == null) {
            player.pendingTradeTarget = null;
            return;
        }
        Player player2 = (Player)player.getTradePartner();
        ((Player)player.getTradePartner()).pendingTradeTarget = null;
        player.pendingTradeTarget = null;
        Player player3 = player2;
        player3.packetSender.sendGameMessage("Other player has declined the trade.");
        player3 = player;
        player3.packetSender.closeInterfaces();
        player3 = player2;
        player3.packetSender.closeInterfaces();
        player.setTradeState(TradeState.NONE);
        player2.setTradeState(TradeState.NONE);
        GameplayHelper.returnTradeOfferItems(player2);
        GameplayHelper.returnTradeOfferItems(player);
        player.setTradePartner(null);
        player2.setTradePartner(null);
        if (player.botEnabled) {
            player.tradeAdvertLastOfferAmount = -1;
        }
        if (player2.botEnabled) {
            player2.tradeAdvertLastOfferAmount = -1;
        }
        player3 = player;
        CharacterFileManager.savePlayer(player3);
        player3 = player2;
        CharacterFileManager.savePlayer(player3);
    }

    public static void returnTradeOfferItems(Player player) {
        int n = 0;
        while (n < 28) {
            ItemStack itemStack;
            if (player.getTradeOfferContainer().getItemAt(n) != null && (itemStack = player.getTradeOfferContainer().getItemAt(n)) != null) {
                player.getTradeOfferContainer().remove(itemStack);
                player.getInventoryManager().addItem(itemStack);
            }
            ++n;
        }
        player.getTradeOfferContainer().clear();
    }

    public static void addTradeOfferItem(Player player, int n, int n2, int n3) {
        Object object;
        Player player2 = (Player)player.getTradePartner();
        if (player.getTradeState().equals((Object)TradeState.CONFIRM_SCREEN)) {
            return;
        }
        if (n2 == -1 || player2 == null) {
            return;
        }
        ItemStack itemStack = player.getInventoryManager().getContainer().getItemAt(n);
        int n4 = player.getInventoryManager().getContainer().getItemAmount(n2);
        if (itemStack == null || itemStack.getId() != n2 || !itemStack.isValid()) {
            return;
        }
        if (itemStack.getId() <= 0 || !itemStack.isValid() || n3 <= 0) {
            return;
        }
        ItemStack itemStack2 = new ItemStack(n2);
        if (itemStack2.getDefinition().isUntradeable()) {
            if (player.getPlayerRights() < 2) {
                Player player3 = player;
                player3.packetSender.sendGameMessage("You cannot trade that item.");
                return;
            }
            object = player;
            ((Player)object).packetSender.sendGameMessage("Note: " + itemStack2.getDefinition().getName() + " is untradeable item!");
        }
        if (n4 > n3) {
            n4 = n3;
        }
        if (itemStack.getDefinition().isStackable()) {
            if (!player.getInventoryManager().removeItemFromSlot(new ItemStack(n2, n4), n)) {
                player.getInventoryManager().removeItem(new ItemStack(n2, n4));
            }
        } else {
            n = 0;
            while (n < n4) {
                player.getInventoryManager().removeItem(new ItemStack(n2, 1));
                ++n;
            }
        }
        if ((n = player.getTradeOfferContainer().getItemAmount(n2)) > 0 && itemStack.getDefinition().isStackable()) {
            player.getTradeOfferContainer().setItem(player.getTradeOfferContainer().indexOfItem(itemStack.getId()), new ItemStack(n2, n + n4));
        } else {
            ItemStack itemStack3 = new ItemStack(itemStack.getId(), n4);
            object = player.getTradeOfferContainer();
            ((ItemContainer)object).add(itemStack3, -1);
        }
        GameplayHelper.refreshTradeOfferInterfaces(player, player2);
        player.setTradeState(TradeState.OFFER_SCREEN);
        player2.setTradeState(TradeState.OFFER_SCREEN);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("", 3431);
        object = player2;
        ((Player)object).packetSender.sendInterfaceText("", 3431);
    }

    public static void removeTradeOfferItem(Player player, int n, int n2, int n3) {
        Player player2 = (Player)player.getTradePartner();
        if (player.getTradeState().equals((Object)TradeState.CONFIRM_SCREEN)) {
            return;
        }
        if (n2 == -1 || player2 == null) {
            return;
        }
        ItemStack itemStack = player.getTradeOfferContainer().getItemAt(n);
        int n4 = player.getTradeOfferContainer().getItemAmount(n2);
        if (itemStack == null || itemStack.getId() != n2 || n3 <= 0) {
            return;
        }
        if (n4 > n3) {
            n4 = n3;
        }
        n = player.getTradeOfferContainer().removeFromSlot(new ItemStack(n2, n4), n);
        player.getInventoryManager().addItem(new ItemStack(itemStack.getId(), n));
        GameplayHelper.refreshTradeOfferInterfaces(player, player2);
        player.setTradeState(TradeState.OFFER_SCREEN);
        player2.setTradeState(TradeState.OFFER_SCREEN);
        player.packetSender.sendInterfaceText("", 3431);
        player = player2;
        player.packetSender.sendInterfaceText("", 3431);
    }

    public static void acceptTradeFirstScreen(Player player) {
        ItemStack itemStack;
        Player player2 = (Player)player.getTradePartner();
        player.setTradeState(TradeState.ACCEPTED);
        if (!player2.getTradeState().equals((Object)TradeState.ACCEPTED)) {
            Player player3 = player;
            player3.packetSender.sendInterfaceText("Waiting for other player...", 3431);
            player3 = player2;
            player3.packetSender.sendInterfaceText("Other player accepted.", 3431);
            return;
        }
        int n = 0;
        ItemStack[] itemStackArray = player.getTradeOfferContainer().getItems();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            itemStack = itemStackArray[n3];
            if (!(itemStack == null || itemStack.getDefinition().isStackable() && player2.getInventoryManager().containsItem(itemStack.getId()))) {
                ++n;
            }
            ++n3;
        }
        if (player2.getInventoryManager().getContainer().getFreeSlots() < n) {
            Player player4 = player;
            player4.packetSender.sendInterfaceText("Other player doesn't have enough inventory space for this trade.", 3431);
            player4 = player2;
            player4.packetSender.sendInterfaceText("You don't have enough inventory space for this trade.", 3431);
            return;
        }
        n = 0;
        itemStackArray = player2.getTradeOfferContainer().getItems();
        n2 = itemStackArray.length;
        n3 = 0;
        while (n3 < n2) {
            itemStack = itemStackArray[n3];
            if (!(itemStack == null || itemStack.getDefinition().isStackable() && player.getInventoryManager().containsItem(itemStack.getId()))) {
                ++n;
            }
            ++n3;
        }
        if (player.getInventoryManager().getContainer().getFreeSlots() < n) {
            Player player5 = player2;
            player5.packetSender.sendInterfaceText("Other player doesn't have enough inventory space for this trade.", 3431);
            player5 = player;
            player5.packetSender.sendInterfaceText("You don't have enough inventory space for this trade.", 3431);
            return;
        }
        GameplayHelper.refreshTradeOfferInterfaces(player, player2);
        Player player6 = player;
        player6.packetSender.showInterfaceWithInventory(3443, 3213);
        player6 = player2;
        player6.packetSender.showInterfaceWithInventory(3443, 3213);
        player.setTradeState(TradeState.CONFIRM_SCREEN);
        player2.setTradeState(TradeState.CONFIRM_SCREEN);
        player6 = player;
        player6.packetSender.sendInterfaceText("Are you sure you want to accept this trade?", 3535);
        player6 = player2;
        player6.packetSender.sendInterfaceText("Are you sure you want to accept this trade?", 3535);
        GameplayHelper.refreshTradeConfirmationSummary(player);
        GameplayHelper.refreshTradeConfirmationSummary(player2);
    }

    public static void acceptTradeSecondScreen(Player player) {
        if (!player.getTradeState().equals((Object)TradeState.CONFIRM_SCREEN)) {
            return;
        }
        Player player2 = (Player)player.getTradePartner();
        player.setTradeState(TradeState.ACCEPTED);
        if (!player2.getTradeState().equals((Object)TradeState.ACCEPTED)) {
            Player player3 = player;
            player3.packetSender.sendInterfaceText("Waiting for other player...", 3535);
            player3 = player2;
            player3.packetSender.sendInterfaceText("Other player accepted.", 3535);
            return;
        }
        ItemStack[] playerTradeItems = new ItemStack[28];
        int n = 0;
        while (n < 28) {
            ItemStack itemStack = player.getTradeOfferContainer().getItemAt(n);
            if (itemStack != null) {
                playerTradeItems[n] = itemStack;
                player.getTradeOfferContainer().remove(itemStack);
                player2.getInventoryManager().addItem(itemStack);
            }
            ++n;
        }
        ItemStack[] partnerTradeItems = new ItemStack[28];
        n = 0;
        while (n < 28) {
            ItemStack itemStack = player2.getTradeOfferContainer().getItemAt(n);
            if (itemStack != null) {
                partnerTradeItems[n] = itemStack;
                player2.getTradeOfferContainer().remove(itemStack);
                player.getInventoryManager().addItem(itemStack);
            }
            ++n;
        }
        player.setTradeState(TradeState.NONE);
        player2.setTradeState(TradeState.NONE);
        player.packetSender.sendGameMessage("You accept the trade.");
        player2.packetSender.sendGameMessage("You accept the trade.");
        player.packetSender.closeInterfaces();
        player2.packetSender.closeInterfaces();
        player.setTradePartner(null);
        player2.setTradePartner(null);
        player2.pendingTradeTarget = null;
        player.pendingTradeTarget = null;
        CharacterFileManager.savePlayer(player);
        CharacterFileManager.savePlayer(player2);
        boolean bothPlayersAreBots = false;
        if (player.botEnabled && player2.botEnabled) {
            bothPlayersAreBots = true;
        }
        if (player.tradeAdvertMode != -1) {
            BotTaskDefinition cfr_ignored_0 = player.currentBotTask;
            BotTaskDefinition.completeTradeAdvertOffer(player, bothPlayersAreBots);
        }
        if (player2.tradeAdvertMode != -1) {
            BotTaskDefinition cfr_ignored_1 = player2.currentBotTask;
            BotTaskDefinition.completeTradeAdvertOffer(player2, bothPlayersAreBots);
        }
    }

    private static void refreshTradeConfirmationSummary(Player player) {
        int n;
        Player player2 = (Player)player.getTradePartner();
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = true;
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        int n2 = 0;
        while (n2 < 28) {
            ItemStack itemStack = player.getTradeOfferContainer().getItemAt(n2);
            if (itemStack != null) {
                int n3 = -1;
                n = 0;
                while (n < arrayList.size()) {
                    if (((ItemStack)arrayList.get(n)).getId() == itemStack.getId()) {
                        n3 = n;
                        break;
                    }
                    ++n;
                }
                if (n3 == -1) {
                    arrayList.add(new ItemStack(itemStack.getId(), itemStack.getAmount()));
                } else {
                    n = ((ItemStack)arrayList.get(n3)).getAmount() + itemStack.getAmount();
                    arrayList.set(n3, new ItemStack(itemStack.getId(), n));
                }
            }
            ++n2;
        }
        for (ItemStack itemStack : arrayList) {
            if (itemStack == null) continue;
            bl = false;
            String string = itemStack.getAmount() >= 1000 && itemStack.getAmount() < 1000000 ? "@cya@" + itemStack.getAmount() / 1000 + "K @whi@(" + GameUtil.formatNumber(itemStack.getAmount()) + ")" : (itemStack.getAmount() >= 1000000 ? "@gre@" + itemStack.getAmount() / 1000000 + " million @whi@(" + GameUtil.formatNumber(itemStack.getAmount()) + ")" : "" + itemStack.getAmount());
            stringBuilder.append(itemStack.getDefinition().getName());
            stringBuilder.append(" x ");
            stringBuilder.append(string);
            stringBuilder.append("\\n");
        }
        if (bl) {
            stringBuilder.append("Absolutely nothing!");
        }
        Player player3 = player;
        player3.packetSender.sendInterfaceText(stringBuilder.toString(), 3557);
        stringBuilder = new StringBuilder();
        boolean bl2 = true;
        ArrayList<ItemStack> arrayList2 = new ArrayList<ItemStack>();
        int n4 = 0;
        while (n4 < 28) {
            ItemStack itemStack = player2.getTradeOfferContainer().getItemAt(n4);
            if (itemStack != null) {
                n = -1;
                int n5 = 0;
                while (n5 < arrayList2.size()) {
                    if (((ItemStack)arrayList2.get(n5)).getId() == itemStack.getId()) {
                        n = n5;
                        break;
                    }
                    ++n5;
                }
                if (n == -1) {
                    arrayList2.add(new ItemStack(itemStack.getId(), itemStack.getAmount()));
                } else {
                    n5 = ((ItemStack)arrayList2.get(n)).getAmount() + itemStack.getAmount();
                    arrayList2.set(n, new ItemStack(itemStack.getId(), n5));
                }
            }
            ++n4;
        }
        if (player2 == null) {
            return;
        }
        for (ItemStack itemStack : arrayList2) {
            if (itemStack == null) continue;
            bl2 = false;
            String string = itemStack.getAmount() >= 1000 && itemStack.getAmount() < 1000000 ? "@cya@" + itemStack.getAmount() / 1000 + "K @whi@(" + GameUtil.formatNumber(itemStack.getAmount()) + ")" : (itemStack.getAmount() >= 1000000 ? "@gre@" + itemStack.getAmount() / 1000000 + " million @whi@(" + GameUtil.formatNumber(itemStack.getAmount()) + ")" : "" + itemStack.getAmount());
            stringBuilder.append(itemStack.getDefinition().getName());
            stringBuilder.append(" x ");
            stringBuilder.append(string);
            stringBuilder.append("\\n");
        }
        if (bl2) {
            stringBuilder.append("Absolutely nothing!");
        }
        Player player4 = player;
        player4.packetSender.sendInterfaceText(stringBuilder.toString(), 3558);
    }

    public static int getCaveLightLevelForItemId(int n) {
        CaveLightSourceDefinition caveLightSourceDefinition = CaveLightSourceDefinition.forItemId(n);
        if (caveLightSourceDefinition == null) {
            return 0;
        }
        if (caveLightSourceDefinition.getUnlitItemId() == n) {
            return 0;
        }
        return caveLightSourceDefinition.getLightLevel();
    }

    public static boolean extinguishCaveLightSource(Player player, int n, boolean bl) {
        CaveLightSourceDefinition caveLightSourceDefinition = CaveLightSourceDefinition.forItemId(n);
        if (caveLightSourceDefinition == null || !player.getInventoryManager().containsItem(n)) {
            return false;
        }
        if (caveLightSourceDefinition.getLitItemId() != n) {
            return false;
        }
        player.getInventoryManager().removeItem(new ItemStack(n, 1));
        player.getInventoryManager().addItem(new ItemStack(caveLightSourceDefinition.getUnlitItemId(), 1));
        ItemDefinition itemDefinition = ItemDefinition.forId(n);
        if (bl) {
            player.packetSender.sendGameMessage("You extinguish the " + itemDefinition.getName().toLowerCase() + ".");
        }
        return true;
    }

    public static boolean castSelectedItemTeleport(Player player, Position position) {
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        player2 = player;
        if (player2.interfaceAction.equals("operate") ? player.getEquipmentManager().getItemIdAtSlot(player.getSelectedItemSlot()) != player.getSelectedItemId() : !player.getInventoryManager().containsItem(player.getSelectedItemId())) {
            return false;
        }
        boolean teleportStarted = player.getTeleportManager().castItemTeleport(position);
        if (player.botEnabled && !teleportStarted && player.botCombatState.startsWith("escape")) {
            player.botCombatState = "tele";
            BotCombatEscapeHandler.startBotCombatWalkingEscape(player);
        } else if (player.botEnabled && !teleportStarted && player.botCombatState.equals("tele")) {
            player.botCombatState = "run";
        }
        if (!teleportStarted) {
            return false;
        }
        player2 = player;
        if (player.interfaceAction.equals("operate")) {
            int nextItemId;
            if (player.getEquipmentManager().removeItem(new ItemStack(player.getSelectedItemId())) && (nextItemId = GameplayHelper.getNextDegradedJewelryItemId(player.getSelectedItemId())) > 0) {
                player.getEquipmentManager().setSlotItem(nextItemId, player.getSelectedItemSlot());
            }
        } else {
            int nextItemId;
            if (player.getInventoryManager().removeItem(new ItemStack(player.getSelectedItemId())) && (nextItemId = GameplayHelper.getNextDegradedJewelryItemId(player.getSelectedItemId())) > 0) {
                player.getInventoryManager().addItem(new ItemStack(nextItemId));
            }
        }
        return true;
    }

    public static int getNextDegradedJewelryItemId(int n) {
        boolean bl = false;
        ChargedJewelryDefinition[] chargedJewelryDefinitionArray = ChargedJewelryDefinition.values();
        int n2 = chargedJewelryDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ChargedJewelryDefinition chargedJewelryDefinition = chargedJewelryDefinitionArray[n3];
            int[] nArray = chargedJewelryDefinition.getItemIdsByDescendingCharge();
            int n4 = nArray.length;
            int n5 = 0;
            while (n5 < n4) {
                int n6 = nArray[n5];
                if (bl) {
                    return n6;
                }
                if (n == n6) {
                    bl = true;
                }
                ++n5;
            }
            ++n3;
        }
        return 0;
    }

    public static void appendLogLine(String string, String logName) {
        String path = "./data/logs/" + logName + ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            try {
                writer.write(string);
                writer.newLine();
            }
            finally {
                writer.close();
            }
            return;
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
    }

    public static int getDaysBetweenMidnights(long l, long l2) {
        DateTime dateTime = new DateTime(l);
        DateTime dateTime2 = new DateTime(l2);
        int n = Days.daysBetween(dateTime.toDateMidnight(), dateTime2.toDateMidnight()).getDays();
        return n;
    }

    public static int getHoursBetween(long l, long l2) {
        DateTime dateTime = new DateTime(l);
        DateTime dateTime2 = new DateTime(l2);
        int n = Hours.hoursBetween(dateTime, dateTime2).getHours();
        return n;
    }

    public static long addDaysToTimestamp(long l, int n) {
        DateTime dateTime = new DateTime(l);
        dateTime = dateTime.plusDays(n);
        return dateTime.getMillis();
    }

    public static long buildTimestampMillis(int n, int n2, int n3, int n4, int n5) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(n3, n2 - 1, n, n4, n5, 0);
        return gregorianCalendar.getTimeInMillis();
    }

    public static String formatDateDayMonthYear(long l) {
        DateTime dateTime = new DateTime(l);
        return String.valueOf(dateTime.getDayOfMonth()) + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getYear();
    }

    public static String formatDurationHoursMinutes(long l) {
        String string = String.valueOf(l / 1000L / 60L / 60L) + "h " + l / 1000L / 60L % 60L + "min";
        return string;
    }
}
