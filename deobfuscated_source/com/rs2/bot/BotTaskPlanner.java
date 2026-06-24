/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.ServerSettings;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatLoadoutTables;
import com.rs2.model.GameplayHelper;
import com.rs2.model.combat.AmmunitionDefinition;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.combat.WeaponProfile;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.shop.ShopDefinition;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.skill.cooking.CookableFoodDefinition;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.skill.smithing.SmeltingHandler;
import com.rs2.model.skill.smithing.SmithingBarDefinition;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class BotTaskPlanner {
    private static int[][] skillTargetLevelMilestones;

    static {
        int[][] nArrayArray = new int[21][];
        int[] nArray = new int[8];
        nArray[1] = -1;
        nArray[2] = -1;
        nArray[3] = -1;
        nArray[4] = -1;
        nArray[5] = -1;
        nArray[6] = -1;
        nArray[7] = -1;
        nArrayArray[0] = nArray;
        nArrayArray[1] = new int[]{1, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[2] = new int[]{2, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[3] = new int[]{3, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[4] = new int[]{4, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[5] = new int[]{5, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[6] = new int[]{6, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[7] = new int[]{7, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[8] = new int[]{8, 15, 30, 45, 60, 75, -1, -1};
        nArrayArray[9] = new int[]{9, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[10] = new int[]{10, 20, 30, 40, 50, 60, 70, 80};
        nArrayArray[11] = new int[]{11, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[12] = new int[]{12, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[13] = new int[]{13, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[14] = new int[]{14, 20, 35, 45, 60, 75, 90, -1};
        nArrayArray[15] = new int[]{15, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[16] = new int[]{16, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[17] = new int[]{17, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[18] = new int[]{18, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[19] = new int[]{19, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[20] = new int[]{20, -1, -1, -1, -1, -1, -1, -1};
        skillTargetLevelMilestones = nArrayArray;
    }

    public static void startInitialProgressiveBotTask(Player player) {
        if (player.currentBotTask == null && player.totalPlaytimeMillis == 0L) {
            int n;
            Object object = new ArrayList<BotTaskDefinition>();
            ((ArrayList)object).addAll(BotTaskDefinition.miningTasks);
            ((ArrayList)object).addAll(BotTaskDefinition.fishingTasks);
            ((ArrayList)object).addAll(BotTaskDefinition.woodcuttingTasks);
            ((ArrayList)object).add((BotTaskDefinition)BotTaskDefinition.runecraftingTasks.get(0));
            BotTaskDefinition botTaskDefinition = GameplayHelper.selectAvailableBotTask(player, (ArrayList)object, false);
            object = player;
            player.currentBotTask = botTaskDefinition;
            BotTaskPlanner.configureCurrentBotTaskGoals(player);
            BotTaskPlanner.prepareDeferredUpgradePurchase(player);
            player.botTaskReturnToBankRequested = false;
            player.botEnabled = true;
            player.currentBotTask.startTask(player);
            player.botTaskStartTimeMillis = System.currentTimeMillis();
            player.botTaskDurationMinutes = n = 30 + GameUtil.randomInt(60);
            player.botTaskSavedElapsedMillis = 0L;
            if (player.currentBotTask.usesEscapeMonitor) {
                player.currentBotTask.startEscapeMonitor(player);
            }
        }
    }

    public static BotTaskDefinition selectShopPurchaseTask(Player player, int n, int n2) {
        BotTaskDefinition botTaskDefinition = null;
        int n3 = 3000;
        int n4 = -1;
        for (Object botTaskDefinitionObject : BotTaskDefinition.shopTasks) {
            BotTaskDefinition botTaskDefinition2 = (BotTaskDefinition)botTaskDefinitionObject;
            double d;
            double d2;
            int n5;
            int n6;
            int n7;
            if (botTaskDefinition2.membersOnly && ServerSettings.freeToPlayWorld || botTaskDefinition2.membersOnly && !player.isMember() || (n7 = GameUtil.getDistance(player.getPosition(), botTaskDefinition2.getStartPosition())) > 3000 || (n6 = botTaskDefinition2.getShopId()) == -1) continue;
            ShopDefinition shopDefinition = (ShopDefinition)ShopManager.getShopDefinitions().get(n6);
            ItemDefinition itemDefinition = new ItemStack(n).getDefinition();
            n4 = itemDefinition.getShopValue();
            int n8 = shopDefinition.getBuyPricePercent();
            double d3 = (double)(n8 + (n5 = (int)((double)n8 + (d2 = (d = shopDefinition.getPriceChangeRate()) * (double)(n2 - 1))))) / 2.0;
            double d4 = d3 / 100.0;
            double d5 = (double)n4 * d4;
            double d6 = d5 * (double)n2 * 1.2;
            if (!player.ownsItemAmount(995, n4 = (int)d6)) continue;
            n8 = 0;
            if (shopDefinition.getOriginalStock().findFlatItem(n) != null) {
                n8 = shopDefinition.getOriginalStock().findFlatItem(n).getAmount();
            }
            if (n8 <= 0 || n7 >= n3) continue;
            botTaskDefinition = botTaskDefinition2;
            n3 = n7;
        }
        if (botTaskDefinition != null && n4 != -1) {
            player.botTaskItemId = n;
            player.botShopItemAmount = n2;
            player.botShopBuyMode = 1;
            player.botTaskRequiredItems = new ItemStack[]{new ItemStack(995, n4)};
        } else {
            player.botTaskItemId = -1;
            player.botShopItemAmount = -1;
            player.botShopBuyMode = -1;
        }
        return botTaskDefinition;
    }

    public static void resetBotTaskGoals(Player player) {
        player.botSkillTargetSkillId = -1;
        player.botSkillTargetLevel = -1;
        player.be = -1;
        player.bf = -1;
        player.bg = -1;
        player.bh = -1;
        player.botCompletionItemId = -1;
        player.botCompletionItemAmount = -1;
        player.bk = -1;
        player.bl = -1;
        player.bm = -1;
        player.bn = -1;
    }

    public static void configureCurrentBotTaskGoals(Player player) {
        boolean bl = false;
        Player player2 = player;
        BotTaskDefinition botTaskDefinition = player2.currentBotTask;
        BotTaskPlanner.resetBotTaskGoals(player2);
        if (!BotTaskDefinition.shopTasks.contains(botTaskDefinition)) {
            player2.botTaskRequiredItems = null;
            Player player3 = player2;
            ArrayList arrayList = player3.currentBotTask.getRequiredItems(player3);
            player3.botTaskRequiredItems = new ItemStack[arrayList.size()];
            int n = 0;
            Iterator iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                ItemStack itemStack;
                player3.botTaskRequiredItems[n] = itemStack = (ItemStack)iterator.next();
                ++n;
            }
            boolean bl2 = false;
            player3 = player2;
            if (BotTaskDefinition.miningTasks.contains(player3.currentBotTask)) {
                player3.botSkillTargetSkillId = 14;
            } else if (BotTaskDefinition.fishingTasks.contains(player3.currentBotTask)) {
                player3.botSkillTargetSkillId = 10;
            } else if (BotTaskDefinition.woodcuttingTasks.contains(player3.currentBotTask)) {
                player3.botSkillTargetSkillId = 8;
            }
            if (player3.botSkillTargetSkillId != -1) {
                n = -1;
                int n2 = 1;
                while (n2 < skillTargetLevelMilestones[player3.botSkillTargetSkillId].length) {
                    int n3 = skillTargetLevelMilestones[player3.botSkillTargetSkillId][n2];
                    if (n3 == -1) break;
                    if (player3.getSkillManager().getCurrentLevels()[player3.botSkillTargetSkillId] < n3) {
                        n = n3;
                        break;
                    }
                    ++n2;
                }
                if (n == -1) {
                    int[] nArray = player3.getSkillManager().getCurrentLevels();
                    int n4 = player3.botSkillTargetSkillId;
                    int n5 = nArray[n4] + 5;
                    nArray[n4] = n5;
                    n = n5;
                }
                player3.botSkillTargetLevel = n;
                if (!bl2) {
                    player3.queuePublicChatMessage("Im going to train " + SkillManager.SKILL_NAMES[player3.botSkillTargetSkillId] + ".");
                }
            }
            if (BotTaskDefinition.runecraftingTasks.get(0) == player3.currentBotTask) {
                player3.botCompletionItemId = 1436;
                player3.bk = 7936;
                player3.botCompletionItemAmount = 1000;
                if (!bl2) {
                    player3.queuePublicChatMessage("Im going to mine ess.");
                }
            } else if (BotTaskDefinition.smeltingTasks.contains(player3.currentBotTask)) {
                if (!bl2) {
                    player3.queuePublicChatMessage("Im going to make some bars.");
                }
            } else if (BotTaskDefinition.sheepShearingTasks.contains(player3.currentBotTask)) {
                player3.botCompletionItemId = 1737;
                player3.botCompletionItemAmount = 100;
                if (!bl2) {
                    player3.queuePublicChatMessage("Im going to get some wool.");
                }
            } else if (BotTaskDefinition.smithingTasks.contains(player3.currentBotTask)) {
                if (!bl2) {
                    player3.queuePublicChatMessage("Im going to smith some items.");
                }
            } else if (BotTaskDefinition.cookingTasks.contains(player3.currentBotTask)) {
                if (!bl2) {
                    player3.queuePublicChatMessage("Im going to cook some food.");
                }
            } else if (BotTaskDefinition.runecraftingTasks.contains(player3.currentBotTask) && !bl2) {
                player3.queuePublicChatMessage("Im going to do some runecrafting.");
            }
        }
        if (botTaskDefinition.combatTask) {
            BotTaskPlanner.prepareBotCombatLoadoutLists(player2, false);
        }
        if (BotTaskDefinition.cookingTasks.contains(botTaskDefinition)) {
            BotTaskPlanner.prepareCookingTaskRequirements(player2);
            return;
        }
        if (BotTaskDefinition.spinningTasks.contains(botTaskDefinition)) {
            BotTaskPlanner.prepareSpinningTaskRequirements(player2);
            return;
        }
        if (BotTaskDefinition.smeltingTasks.contains(botTaskDefinition)) {
            BotTaskPlanner.hasSmeltingTaskMaterial(player2);
            return;
        }
        if (BotTaskDefinition.smithingTasks.contains(botTaskDefinition)) {
            BotTaskPlanner.prepareSmithingTaskRequirements(player2);
            return;
        }
        if (BotTaskDefinition.tanningTasks.contains(botTaskDefinition)) {
            BotTaskPlanner.prepareTanningTaskRequirements(player2, false);
            return;
        }
        if (BotTaskDefinition.leatherCraftingTasks.contains(botTaskDefinition)) {
            BotTaskPlanner.prepareLeatherCraftingTaskRequirements(player2);
        }
    }

    public static void populateBotShopSellItemIds(Player player) {
        int n;
        player.botShopSellItemIds.clear();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        Object object = BotCombatLoadoutTables.post306BowIds;
        int n2 = 0;
        while (n2 < 4) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.swordIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.longswordIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.scimitarIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.battleaxeIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.warhammerIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.maceIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.daggerIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.twoHandedSwordIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.basicRangedHeadIds;
        n2 = 0;
        while (n2 < 2) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.basicRangedBodyIds;
        n2 = 0;
        while (n2 < 4) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.basicRangedLegIds;
        n2 = 0;
        while (n2 < 3) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.basicRangedVambraceIds;
        n2 = 0;
        while (n2 < 2) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.mediumHelmetIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.fullHelmetIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.chainbodyIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.platebodyIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.platelegIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.plateskirtIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.squareShieldIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        object = BotCombatLoadoutTables.kiteshieldIds;
        n2 = 0;
        while (n2 < 7) {
            n = ((int[])object)[n2];
            arrayList.add(new Integer(n));
            ++n2;
        }
        ArrayList<Integer> arrayList2 = new ArrayList<Integer>();
        arrayList2.add(1381);
        arrayList2.add(1383);
        arrayList2.add(1385);
        arrayList2.add(1387);
        BotTaskPlanner.prepareBotCombatLoadoutLists(player, true);
        n2 = -1;
        int n3 = -1;
        GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.findOwnedGatheringTool(player, 14);
        object = (Object)gatheringToolDefinition;
        if (gatheringToolDefinition != null) {
            n2 = ((GatheringToolDefinition)((Object)object)).getToolItemId();
        }
        if ((object = ItemCombinationHandler.findOwnedGatheringTool(player, 8)) != null) {
            n3 = ((GatheringToolDefinition)((Object)object)).getToolItemId();
        }
        ItemStack[] itemStackArray = player.getBankContainer().getItems();
        int n4 = itemStackArray.length;
        int n5 = 0;
        while (n5 < n4) {
            object = itemStackArray[n5];
            if (object != null) {
                if (player.botShopSellItemIds.size() > 20) break;
                if (ItemCombinationHandler.isGatheringToolItemId(((ItemStack)object).getId()) && ((ItemStack)object).getId() != n2 && ((ItemStack)object).getId() != n3) {
                    player.botShopSellItemIds.add(((ItemStack)object).getId());
                } else {
                    ItemDefinition itemDefinition = ((ItemStack)object).getDefinition();
                    if (itemDefinition.getName().toLowerCase().endsWith("logs") && !itemDefinition.getName().toLowerCase().equals("logs")) {
                        player.botShopSellItemIds.add(((ItemStack)object).getId());
                    } else if (!(!arrayList.contains(((ItemStack)object).getId()) || player.botMeleeLoadoutItemIds.contains(((ItemStack)object).getId()) || player.botMagicLoadoutItemIds.contains(((ItemStack)object).getId()) || player.botRangedLoadoutItemIds.contains(((ItemStack)object).getId()) || arrayList2.contains(((ItemStack)object).getId()))) {
                        player.botShopSellItemIds.add(((ItemStack)object).getId());
                    }
                }
            }
            ++n5;
        }
    }

    private static boolean tryDeferForUpgradePurchase(Player player, int n, int[] nArray, int n2, boolean bl) {
        return BotTaskPlanner.tryDeferForUpgradePurchase(player, n, nArray, n2, bl, false);
    }

    private static boolean tryDeferForUpgradePurchase(Player player, int currentLevel, int[] itemIds, int skillId, boolean requireUpgrade, boolean shuffle) {
        if (shuffle) {
            ArrayList<Integer> shuffledIds = new ArrayList<Integer>();
            int index = itemIds.length - 1;
            while (index >= 0) {
                shuffledIds.add(itemIds[index]);
                --index;
            }
            Collections.shuffle(shuffledIds);
            itemIds = new int[shuffledIds.size()];
            index = 0;
            while (index < shuffledIds.size()) {
                itemIds[index] = (Integer)shuffledIds.get(index);
                ++index;
            }
        }
        int index = itemIds.length - 1;
        while (index >= 0) {
            int itemId = itemIds[index];
            if (player.getEquipmentManager().canEquipItem(itemId)) {
                ItemDefinition itemDefinition = new ItemStack(itemId, 1).getDefinition();
                boolean shouldDefer = false;
                if (!requireUpgrade || currentLevel < itemDefinition.getRequiredLevel(skillId)) {
                    shouldDefer = true;
                }
                if (shouldDefer) {
                    BotTaskDefinition botTaskDefinition = BotTaskPlanner.selectShopPurchaseTask(player, itemId, 1);
                    if (botTaskDefinition != null) {
                        BotTaskPlanner.resetBotTaskGoals(player);
                        player.deferredBotTask = player.currentBotTask;
                        player.currentBotTask = botTaskDefinition;
                        return true;
                    }
                } else if (currentLevel >= itemDefinition.getRequiredLevel(skillId) && requireUpgrade) {
                    return false;
                }
            }
            --index;
        }
        return false;
    }

    public static void prepareDeferredUpgradePurchase(Player player) {
        block95: {
            if (!player.currentBotTask.combatTask) {
                player.botCombatLoadoutSlotCursor = -1;
            }
            if (BotTaskDefinition.miningTasks.contains(player.currentBotTask) || BotTaskDefinition.woodcuttingTasks.contains(player.currentBotTask) || BotTaskDefinition.runecraftingTasks.get(0) == player.currentBotTask) {
                int skillId = 14;
                if (BotTaskDefinition.woodcuttingTasks.contains(player.currentBotTask)) {
                    skillId = 8;
                }
                GatheringToolDefinition ownedTool = ItemCombinationHandler.findOwnedGatheringTool(player, skillId);
                ArrayList arrayList = ItemCombinationHandler.getGatheringToolsForSkill(skillId);
                int index = arrayList.size() - 1;
                while (index >= 0) {
                    GatheringToolDefinition toolDefinition = (GatheringToolDefinition)arrayList.get(index);
                    boolean shouldBuy = false;
                    if (ownedTool == null) {
                        shouldBuy = true;
                    } else if (skillId == 14) {
                        if (toolDefinition.getToolSpeed() < ownedTool.getToolSpeed()) {
                            shouldBuy = true;
                        }
                    } else if (toolDefinition.getToolSpeed() > ownedTool.getToolSpeed()) {
                        shouldBuy = true;
                    }
                    if (shouldBuy) {
                        BotTaskDefinition purchaseTask = BotTaskPlanner.selectShopPurchaseTask(player, toolDefinition.getToolItemId(), 1);
                        if (purchaseTask != null) {
                            BotTaskPlanner.resetBotTaskGoals(player);
                            player.deferredBotTask = player.currentBotTask;
                            player.currentBotTask = purchaseTask;
                            return;
                        }
                    }
                    --index;
                }
                return;
            }
            if (player.currentBotTask.combatTask) {
                int[] nArray = new int[8];
                nArray[0] = 3;
                nArray[1] = 5;
                nArray[3] = 7;
                nArray[4] = 4;
                nArray[5] = 1;
                nArray[6] = 9;
                nArray[7] = 10;
                int[] nArray2 = nArray;
                if (player.botCombatStyle == 0) {
                    int n = player.botCombatLoadoutSlotCursor + 1;
                    while (n < 8) {
                        Object object;
                        ItemDefinition itemDefinition = null;
                        player.botCombatLoadoutSlotCursor = n;
                        int n4 = nArray2[n];
                        Iterator iterator = player.botMeleeLoadoutItemIds.iterator();
                        while (iterator.hasNext()) {
                            int n5 = (Integer)iterator.next();
                            object = new ItemStack(n5, 1);
                            ItemDefinition itemDefinition2 = ((ItemStack)object).getDefinition();
                            if (itemDefinition2.getEquipmentSlot() != n4) continue;
                            itemDefinition = itemDefinition2;
                            break;
                        }
                        if (n4 == 3) {
                            int n6 = 0;
                            if (itemDefinition != null) {
                                n6 = itemDefinition.getRequiredLevel(0);
                            }
                            object = null;
                            int n7 = GameUtil.randomInt(4);
                            if (n7 == 0) {
                                object = BotCombatLoadoutTables.swordIds;
                            } else if (n7 == 1) {
                                object = BotCombatLoadoutTables.longswordIds;
                            } else if (n7 == 2) {
                                object = BotCombatLoadoutTables.scimitarIds;
                            } else if (n7 == 3) {
                                object = BotCombatLoadoutTables.battleaxeIds;
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n6, (int[])object, 0, itemDefinition != null)) {
                                return;
                            }
                        } else if (n4 == 5) {
                            int n8 = 0;
                            if (itemDefinition != null) {
                                n8 = itemDefinition.getRequiredLevel(1);
                            }
                            object = null;
                            int n9 = GameUtil.randomInt(2);
                            if (n9 == 0) {
                                object = BotCombatLoadoutTables.squareShieldIds;
                            } else if (n9 == 1) {
                                object = BotCombatLoadoutTables.kiteshieldIds;
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n8, (int[])object, 1, itemDefinition != null)) {
                                return;
                            }
                        } else if (n4 == 0) {
                            int n10 = 0;
                            if (itemDefinition != null) {
                                n10 = itemDefinition.getRequiredLevel(1);
                            }
                            object = null;
                            int n11 = GameUtil.randomInt(2);
                            if (n11 == 0) {
                                object = BotCombatLoadoutTables.mediumHelmetIds;
                            } else if (n11 == 1) {
                                object = BotCombatLoadoutTables.fullHelmetIds;
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n10, (int[])object, 1, itemDefinition != null)) {
                                return;
                            }
                        } else if (n4 == 7) {
                            int n12 = 0;
                            if (itemDefinition != null) {
                                n12 = itemDefinition.getRequiredLevel(1);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n12, (int[])(object = player.getGender() == 0 ? (Object)BotCombatLoadoutTables.platelegIds : (Object)BotCombatLoadoutTables.plateskirtIds), 1, itemDefinition != null)) {
                                return;
                            }
                        } else if (n4 == 4) {
                            int n13 = 0;
                            if (itemDefinition != null) {
                                n13 = itemDefinition.getRequiredLevel(1);
                            }
                            object = null;
                            int n14 = GameUtil.randomInt(2);
                            if (n14 == 0) {
                                object = BotCombatLoadoutTables.chainbodyIds;
                            } else if (n14 == 1) {
                                object = BotCombatLoadoutTables.platebodyIds;
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n13, (int[])object, 1, itemDefinition != null)) {
                                return;
                            }
                        } else if (n4 == 1) {
                            int n15 = 0;
                            if (itemDefinition != null) {
                                n15 = itemDefinition.getRequiredLevel(1);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n15, (int[])(object = (Object)BotCombatLoadoutTables.capeIds), 1, itemDefinition != null, true)) {
                                return;
                            }
                        } else if (n4 == 9) {
                            int n16 = 0;
                            if (itemDefinition != null) {
                                n16 = itemDefinition.getRequiredLevel(1);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n16, (int[])(object = (Object)new int[]{1059}), 1, itemDefinition != null)) {
                                return;
                            }
                        } else if (n4 == 10) {
                            int n17 = 0;
                            if (itemDefinition != null) {
                                n17 = itemDefinition.getRequiredLevel(1);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n17, (int[])(object = (Object)new int[]{1061}), 1, itemDefinition != null)) break block95;
                        }
                        ++n;
                    }
                    return;
                }
                if (player.botCombatStyle == 1) {
                    int n = player.botCombatLoadoutSlotCursor + 1;
                    while (n < 8) {
                        Object object;
                        ItemDefinition itemDefinition = null;
                        player.botCombatLoadoutSlotCursor = n;
                        int n18 = nArray2[n];
                        Iterator iterator = player.botRangedLoadoutItemIds.iterator();
                        while (iterator.hasNext()) {
                            int n19 = (Integer)iterator.next();
                            ItemStack itemStack = new ItemStack(n19, 1);
                            object = itemStack;
                            ItemDefinition itemDefinition3 = itemStack.getDefinition();
                            if (itemDefinition3.getEquipmentSlot() != n18) continue;
                            itemDefinition = itemDefinition3;
                            break;
                        }
                        if (n18 == 3) {
                            int n20 = 0;
                            if (itemDefinition != null) {
                                n20 = itemDefinition.getRequiredLevel(4);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n20, BotCombatLoadoutTables.post306BowIds, 4, itemDefinition != null)) {
                                return;
                            }
                        } else if (n18 == 9) {
                            int n21 = 0;
                            if (itemDefinition != null) {
                                n21 = itemDefinition.getRequiredLevel(4);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n21, BotCombatLoadoutTables.basicRangedVambraceIds, 4, itemDefinition != null)) {
                                return;
                            }
                        } else if (n18 == 0) {
                            int n22 = 0;
                            if (itemDefinition != null) {
                                n22 = itemDefinition.getRequiredLevel(4);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n22, BotCombatLoadoutTables.basicRangedHeadIds, 4, itemDefinition != null)) {
                                return;
                            }
                        } else if (n18 == 7) {
                            int n23 = 0;
                            if (itemDefinition != null) {
                                n23 = itemDefinition.getRequiredLevel(4);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n23, BotCombatLoadoutTables.basicRangedLegIds, 4, itemDefinition != null)) {
                                return;
                            }
                        } else if (n18 == 4) {
                            int n24 = 0;
                            if (itemDefinition != null) {
                                n24 = itemDefinition.getRequiredLevel(4);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n24, BotCombatLoadoutTables.basicRangedBodyIds, 4, itemDefinition != null)) {
                                return;
                            }
                        } else if (n18 == 1) {
                            int n25 = 0;
                            if (itemDefinition != null) {
                                n25 = itemDefinition.getRequiredLevel(1);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n25, BotCombatLoadoutTables.capeIds, 1, itemDefinition != null, true)) break block95;
                        }
                        ++n;
                    }
                    return;
                }
                if (player.botCombatStyle == 2) {
                    int n = player.botCombatLoadoutSlotCursor + 1;
                    while (n < 8) {
                        Object object;
                        ItemDefinition itemDefinition = null;
                        player.botCombatLoadoutSlotCursor = n;
                        int n26 = nArray2[n];
                        Iterator iterator = player.botMagicLoadoutItemIds.iterator();
                        while (iterator.hasNext()) {
                            int n27 = (Integer)iterator.next();
                            object = new ItemStack(n27, 1);
                            ItemDefinition itemDefinition4 = ((ItemStack)object).getDefinition();
                            if (itemDefinition4.getEquipmentSlot() != n26) continue;
                            itemDefinition = itemDefinition4;
                            break;
                        }
                        if (n26 == 0) {
                            int n28 = 0;
                            if (itemDefinition != null) {
                                n28 = itemDefinition.getRequiredLevel(6);
                            }
                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n28, (int[])(object = (Object)new int[]{579, 1017}), 6, itemDefinition != null, true)) break;
                        }
                        ++n;
                    }
                }
            }
        }
    }

    public static boolean prepareCookingTaskRequirements(Player player) {
        ArrayList<ItemStack> availableFood = new ArrayList<ItemStack>();
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                ItemDefinition itemDefinition = itemStack.getDefinition();
                CookableFoodDefinition foodDefinition = CookableFoodDefinition.forRawItemId(itemStack.getId());
                if (itemDefinition.getName().toLowerCase().startsWith("raw ") && foodDefinition != null && player.getSkillManager().getCurrentLevels()[7] >= foodDefinition.getRequiredLevel()) {
                    availableFood.add(itemStack);
                }
            }
            ++n2;
        }
        itemStackArray = player.getBankContainer().getItems();
        n = itemStackArray.length;
        n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                ItemDefinition itemDefinition = itemStack.getDefinition();
                CookableFoodDefinition foodDefinition = CookableFoodDefinition.forRawItemId(itemStack.getId());
                if (itemDefinition.getName().toLowerCase().startsWith("raw ") && foodDefinition != null && player.getSkillManager().getCurrentLevels()[7] >= foodDefinition.getRequiredLevel()) {
                    availableFood.add(itemStack);
                }
            }
            ++n2;
        }
        if (availableFood.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)availableFood.get(0)).getId();
        player.botTaskRequiredItems = new ItemStack[]{new ItemStack(player.botTaskItemId, 28)};
        return true;
    }

    public static boolean prepareSpinningTaskRequirements(Player player) {
        ArrayList<ItemStack> availableItems = new ArrayList<ItemStack>();
        if (player.ownsItemAmount(1737, 28)) {
            availableItems.add(new ItemStack(1737, 28));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 10 && player.ownsItemAmount(1779, 28)) {
            availableItems.add(new ItemStack(1779, 28));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 19 && player.ownsItemAmount(6051, 28)) {
            availableItems.add(new ItemStack(6051, 28));
        }
        if (availableItems.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)availableItems.get(0)).getId();
        player.botTaskRequiredItems = new ItemStack[]{new ItemStack(player.botTaskItemId, 28)};
        return true;
    }

    public static boolean prepareTanningTaskRequirements(Player player, boolean bl) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        if (player.ownsItemAmount(1739, 100) && player.ownsItemAmount(995, 500)) {
            arrayList.add(new ItemStack(1739, 100));
        }
        if (!ServerSettings.freeToPlayWorld && player.isMember()) {
            if (player.getSkillManager().getCurrentLevels()[12] >= 57 && player.ownsItemAmount(1753, 100) && player.ownsItemAmount(995, 2500)) {
                arrayList.add(new ItemStack(1753, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 66 && player.ownsItemAmount(1751, 100) && player.ownsItemAmount(995, 2500)) {
                arrayList.add(new ItemStack(1751, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 73 && player.ownsItemAmount(1749, 100) && player.ownsItemAmount(995, 2500)) {
                arrayList.add(new ItemStack(1749, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 79 && player.ownsItemAmount(1747, 100) && player.ownsItemAmount(995, 2500)) {
                arrayList.add(new ItemStack(1747, 100));
            }
        }
        if (arrayList.size() == 0) {
            return false;
        }
        if (!bl) {
            int n = 550;
            player.botTaskItemId = ((ItemStack)arrayList.get(0)).getId();
            if (player.botTaskItemId == 1739) {
                n = 100;
            }
            ItemStack[] itemStackArray2 = new ItemStack[]{new ItemStack(995, n), new ItemStack(player.botTaskItemId, 27)};
            player.botTaskRequiredItems = itemStackArray2;
        }
        return true;
    }

    public static boolean prepareLeatherCraftingTaskRequirements(Player player) {
        ArrayList<ItemStack> availableItems = new ArrayList<ItemStack>();
        if (player.ownsItemAmount(1741, 100)) {
            availableItems.add(new ItemStack(1741, 100));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 28 && player.ownsItemAmount(1743, 100)) {
            availableItems.add(new ItemStack(1743, 100));
        }
        if (!ServerSettings.freeToPlayWorld && player.isMember()) {
            if (player.getSkillManager().getCurrentLevels()[12] >= 57 && player.ownsItemAmount(1745, 100)) {
                availableItems.add(new ItemStack(1745, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 66 && player.ownsItemAmount(2505, 100)) {
                availableItems.add(new ItemStack(2505, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 73 && player.ownsItemAmount(2507, 100)) {
                availableItems.add(new ItemStack(2507, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 79 && player.ownsItemAmount(2509, 100)) {
                availableItems.add(new ItemStack(2509, 100));
            }
        }
        if (availableItems.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)availableItems.get(0)).getId();
        player.botTaskRequiredItems = new ItemStack[]{new ItemStack(1733, 1), new ItemStack(1734, 26), new ItemStack(player.botTaskItemId, 26)};
        return true;
    }

    public static boolean hasSmeltingTaskMaterial(Player player) {
        return SmeltingHandler.selectBestBotSmeltingBarItemId(player) != -1;
    }

    public static boolean prepareSmithingTaskRequirements(Player player) {
        int n = -1;
        int n2 = 0;
        while (n2 < SmithingBarDefinition.VALUES.length) {
            SmithingBarDefinition smithingBarDefinition = SmithingBarDefinition.VALUES[n2];
            int n3 = smithingBarDefinition.getRequiredLevel();
            if (player.getSkillManager().getCurrentLevels()[13] >= n3 && player.ownsItem(smithingBarDefinition.getBarItemId())) {
                player.botTaskItemId = n = smithingBarDefinition.getBarItemId();
                ItemStack[] itemStackArray = new ItemStack[]{new ItemStack(2347, 1), new ItemStack(n, 27)};
                player.botTaskRequiredItems = itemStackArray;
                break;
            }
            ++n2;
        }
        return n != -1;
    }

    public static void selectMeleeTrainingFightMode(Player player) {
        if (player.botCombatStyle != 0) {
            return;
        }
        Player player2 = player;
        int n = player2.getSkillManager().getCurrentLevels()[0] <= player2.getSkillManager().getCurrentLevels()[1] && player2.getSkillManager().getCurrentLevels()[0] <= player2.getSkillManager().getCurrentLevels()[2] ? 0 : (player2.getSkillManager().getCurrentLevels()[2] < player2.getSkillManager().getCurrentLevels()[0] && player2.getSkillManager().getCurrentLevels()[2] <= player2.getSkillManager().getCurrentLevels()[1] ? 2 : 1);
        WeaponProfile weaponProfile = WeaponProfile.forItem(player.getEquipmentManager().getContainer().getItemAt(3));
        AttackStyleDefinition[] attackStyleDefinitionArray = weaponProfile.getInterfaceDefinition().getAttackStyles();
        int n2 = -1;
        int n3 = 10;
        boolean bl = false;
        int n4 = 0;
        while (n4 < attackStyleDefinitionArray.length) {
            if (bl) break;
            int[] nArray = attackStyleDefinitionArray[n4].getXpMode().getSkillIds();
            int n5 = 0;
            while (n5 < nArray.length) {
                if (bl) break;
                if (nArray[n5] == n && nArray.length == 1) {
                    n2 = n4;
                    n3 = nArray.length;
                    bl = true;
                } else if (nArray[n5] == n && nArray.length < n3) {
                    n3 = nArray.length;
                    n2 = n4;
                }
                ++n5;
            }
            ++n4;
        }
        if (n2 == -1) {
            n2 = 0;
        }
        player.setFightMode(n2);
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    private static void prepareBotCombatLoadoutLists(Player player, boolean bl) {
        int var4_22 = 0;
        int n;
        int n2;
        Object object;
        ItemStack itemStack;
        player.botMeleeLoadoutItemIds.clear();
        player.botMagicLoadoutItemIds.clear();
        player.botRangedLoadoutItemIds.clear();
        player.botCombatLoadoutItemIds.clear();
        Object object2 = new ArrayList<ItemStack>();
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        ArrayList<ItemStack> arrayList2 = new ArrayList<ItemStack>();
        ItemStack[] itemStackArray = player.getEquipmentManager().getContainer().getItems();
        int n3 = itemStackArray.length;
        int n4 = 0;
        while (n4 < n3) {
            itemStack = itemStackArray[n4];
            if (itemStack != null && !ItemCombinationHandler.isGatheringToolItemId(itemStack.getId()) && ((ItemDefinition)(object = itemStack.getDefinition())).getEquipmentSlot() != -1 && player.getEquipmentManager().canEquipItem(itemStack.getId())) {
                if (((ItemDefinition)object).getEquipmentSlot() == 13 && ((ItemDefinition)object).getName().toLowerCase().endsWith("arrow")) {
                    if (itemStack.getAmount() >= 200) {
                        arrayList.add(itemStack);
                    }
                } else {
                    if (((ItemDefinition)object).getEquipmentSlot() == 3 && ((ItemDefinition)object).getName().toLowerCase().endsWith("bow")) {
                        arrayList2.add(itemStack);
                    }
                    ((ArrayList)object2).add(itemStack);
                }
            }
            ++n4;
        }
        ItemStack[] itemStackArray2 = player.getInventoryManager().getContainer().getItems();
        n3 = itemStackArray2.length;
        n4 = 0;
        while (n4 < n3) {
            itemStack = itemStackArray2[n4];
            if (itemStack != null && !ItemCombinationHandler.isGatheringToolItemId(itemStack.getId()) && ((ItemDefinition)(object = itemStack.getDefinition())).getEquipmentSlot() != -1 && player.getEquipmentManager().canEquipItem(itemStack.getId())) {
                if (((ItemDefinition)object).getEquipmentSlot() == 13 && ((ItemDefinition)object).getName().toLowerCase().endsWith("arrow")) {
                    if (itemStack.getAmount() >= 200) {
                        arrayList.add(itemStack);
                    }
                } else {
                    if (((ItemDefinition)object).getEquipmentSlot() == 3 && ((ItemDefinition)object).getName().toLowerCase().endsWith("bow")) {
                        arrayList2.add(itemStack);
                    }
                    ((ArrayList)object2).add(itemStack);
                }
            }
            ++n4;
        }
        ItemStack[] itemStackArray3 = player.getBankContainer().getItems();
        n3 = itemStackArray3.length;
        n4 = 0;
        while (n4 < n3) {
            itemStack = itemStackArray3[n4];
            if (itemStack != null && !ItemCombinationHandler.isGatheringToolItemId(itemStack.getId()) && ((ItemDefinition)(object = itemStack.getDefinition())).getEquipmentSlot() != -1 && player.getEquipmentManager().canEquipItem(itemStack.getId())) {
                if (((ItemDefinition)object).getEquipmentSlot() == 13 && ((ItemDefinition)object).getName().toLowerCase().endsWith("arrow")) {
                    if (itemStack.getAmount() >= 200) {
                        arrayList.add(itemStack);
                    }
                } else {
                    if (((ItemDefinition)object).getEquipmentSlot() == 3 && ((ItemDefinition)object).getName().toLowerCase().endsWith("bow")) {
                        arrayList2.add(itemStack);
                    }
                    ((ArrayList)object2).add(itemStack);
                }
            }
            ++n4;
        }
        boolean bl2 = false;
        n4 = 0;
        n3 = -1;
        int[] nArray = new int[4];
        int[] nArray2 = nArray;
        nArray[0] = 1381;
        nArray2[1] = 1383;
        nArray2[2] = 1385;
        nArray2[3] = 1387;
        object = new ArrayList();
        if (!bl) {
            int n5 = 0;
            BotTaskDefinition botTaskDefinition;
            BotTaskDefinition botTaskDefinition2;
            if (arrayList2.size() > 0 && arrayList.size() > 0) {
                block3: for (ItemStack itemStack2 : arrayList2) {
                    if (bl2) break;
                    for (ItemStack itemStack3 : arrayList) {
                        if (bl2) continue block3;
                        if (!AmmunitionDefinition.isCompatible(player, itemStack2, itemStack3)) continue;
                        bl2 = true;
                    }
                }
            }
            if (!bl2) {
                BotTaskDefinition botTaskDefinition3;
                if (arrayList2.size() == 0) {
                    Object object3 = BotCombatLoadoutTables.post306BowIds;
                    if (BotTaskPlanner.tryDeferForUpgradePurchase(player, 0, (int[])object3, 4, false)) {
                        return;
                    }
                } else if (arrayList.size() == 0 && (botTaskDefinition3 = BotTaskPlanner.selectShopPurchaseTask(player, 882, 400)) != null) {
                    BotTaskPlanner.resetBotTaskGoals(player);
                    object2 = player.currentBotTask;
                    Player player2 = player;
                    player.deferredBotTask = (BotTaskDefinition)object2;
                    object2 = botTaskDefinition3;
                    player2 = player;
                    player.currentBotTask = (BotTaskDefinition)object2;
                    return;
                }
            }
            if (!player.ownsItemAmount(556, 250) && !player.ownsItem(1438) && !player.ownsItem(5527) && player.ownsItemAmount(995, 375) && (botTaskDefinition2 = BotTaskPlanner.selectShopPurchaseTask(player, 556, 250)) != null) {
                BotTaskPlanner.resetBotTaskGoals(player);
                object2 = player.currentBotTask;
                Player player3 = player;
                player.deferredBotTask = (BotTaskDefinition)object2;
                object2 = botTaskDefinition2;
                player3 = player;
                player.currentBotTask = (BotTaskDefinition)object2;
                return;
            }
            if (!player.ownsItemAmount(558, 250) && player.ownsItemAmount(995, 375) && (botTaskDefinition = BotTaskPlanner.selectShopPurchaseTask(player, 558, 250)) != null) {
                BotTaskPlanner.resetBotTaskGoals(player);
                object2 = player.currentBotTask;
                Player player4 = player;
                player.deferredBotTask = (BotTaskDefinition)object2;
                object2 = botTaskDefinition;
                player4 = player;
                player.currentBotTask = (BotTaskDefinition)object2;
                return;
            }
            int[][] nArray3 = new int[4][2];
            int n6 = 0;
            Object object4 = BotCombatLoadoutTables.elementalStrikeSpells;
            int n7 = 0;
            while (n7 < 4) {
                SpellDefinition spellDefinition = ((SpellDefinition[])object4)[n7];
                ((ArrayList)object).add(spellDefinition);
                ++n7;
            }
            boolean bl3 = false;
            if (player.getSkillManager().getCurrentLevels()[6] >= 13) {
                n5 = 2;
            }
            n7 = n5;
            while (n7 < ((ArrayList)object).size()) {
                SpellDefinition spellDefinition = (SpellDefinition)((Object)((ArrayList)object).get(n7));
                if (spellDefinition.getRequiredLevel() > player.getSkillManager().getCurrentLevels()[6]) break;
                nArray3[n7][0] = 99999;
                nArray3[n7][1] = 99999;
                ItemStack[] itemStackArray4 = spellDefinition.getRuneCosts();
                n2 = itemStackArray4.length;
                n = 0;
                while (n < n2) {
                    int n8;
                    object4 = itemStackArray4[n];
                    if (!player.ownsItem(((ItemStack)object4).getId())) {
                        nArray3[n7][0] = 0;
                    } else {
                        n8 = player.getOwnedItemAmount(((ItemStack)object4).getId()) / ((ItemStack)object4).getAmount();
                        if (n8 < nArray3[n7][0]) {
                            nArray3[n7][0] = n8;
                        }
                    }
                    if ((n7 == 0 && ((ItemStack)object4).getId() != 556 || n7 == 1 && ((ItemStack)object4).getId() != 555 || n7 == 2 && ((ItemStack)object4).getId() != 557 || n7 == 3 && ((ItemStack)object4).getId() != 554) && (n8 = player.getOwnedItemAmount(((ItemStack)object4).getId()) / ((ItemStack)object4).getAmount()) < nArray3[n7][1]) {
                        nArray3[n7][1] = n8;
                    }
                    ++n;
                }
                ++n7;
            }
            n7 = n5;
            while (n7 < ((ArrayList)object).size()) {
                if (player.ownsItem(nArray2[n7]) || player.ownsItemAmount(995, 2000)) {
                    if (nArray3[n7][1] > n6) {
                        n6 = nArray3[n7][1];
                        n3 = n7;
                    }
                } else if (nArray3[n7][0] > n6) {
                    n6 = nArray3[n7][0];
                    n3 = n7;
                }
                ++n7;
            }
            if (n3 != -1) {
                BotTaskDefinition botTaskDefinition4;
                if (!player.ownsItem(nArray2[n3]) && (botTaskDefinition4 = BotTaskPlanner.selectShopPurchaseTask(player, nArray2[n3], 1)) != null) {
                    BotTaskPlanner.resetBotTaskGoals(player);
                    object2 = player.currentBotTask;
                    Player player5 = player;
                    player.deferredBotTask = (BotTaskDefinition)object2;
                    object2 = botTaskDefinition4;
                    player5 = player;
                    player.currentBotTask = (BotTaskDefinition)object2;
                    return;
                }
                if (n6 < 250) {
                    SpellDefinition spellDefinition = (SpellDefinition)((Object)((ArrayList)object).get(n3));
                    ItemStack[] itemStackArray5 = spellDefinition.getRuneCosts();
                    n = itemStackArray5.length;
                    int n9 = 0;
                    while (n9 < n) {
                        BotTaskDefinition botTaskDefinition5;
                        int n10;
                        ItemStack itemStack4 = itemStackArray5[n9];
                        if (!(player.ownsItem(nArray2[n3]) && (n3 == 0 && itemStack4.getId() == 556 || n3 == 1 && itemStack4.getId() == 555 || n3 == 2 && itemStack4.getId() == 557 || n3 == 3 && itemStack4.getId() == 554) || (n10 = player.getOwnedItemAmount(itemStack4.getId()) / itemStack4.getAmount()) >= 250 || (botTaskDefinition5 = BotTaskPlanner.selectShopPurchaseTask(player, itemStack4.getId(), itemStack4.getAmount() * 250)) == null)) {
                            BotTaskPlanner.resetBotTaskGoals(player);
                            object2 = player.currentBotTask;
                            Player player6 = player;
                            player.deferredBotTask = (BotTaskDefinition)object2;
                            object2 = botTaskDefinition5;
                            player6 = player;
                            player.currentBotTask = (BotTaskDefinition)object2;
                            return;
                        }
                        ++n9;
                    }
                }
                if (n6 >= 250) {
                    n4 = 1;
                }
            }
        }
        int[][] nArray4 = new int[3][14];
        int[][] nArray5 = new int[3][14];
        Object object5 = ((ArrayList)object2).iterator();
        while (((Iterator)object5).hasNext()) {
            ItemStack itemStack5 = (ItemStack)((Iterator)object5).next();
            ItemDefinition itemDefinition = itemStack5.getDefinition();
            int n11 = itemDefinition.getEquipmentSlot();
            n = 0;
            while (n < 3) {
                block87: {
                    block88: {
                        block91: {
                            block89: {
                                block90: {
                                    block85: {
                                        block86: {
                                            n2 = 0;
                                            if (n != 0) break block85;
                                            if (n11 != 3) break block86;
                                            if (itemDefinition.isTwoHanded()) break block87;
                                            n2 = 0 + itemDefinition.getBonus(0);
                                            n2 += itemDefinition.getBonus(1);
                                            n2 += itemDefinition.getBonus(2);
                                            n2 += itemDefinition.getBonus(10);
                                            break block88;
                                        }
                                        n2 = 0 + itemDefinition.getBonus(5);
                                        n2 += itemDefinition.getBonus(6);
                                        n2 += itemDefinition.getBonus(7);
                                        break block88;
                                    }
                                    if (n != 1) break block89;
                                    if (itemDefinition.getBonus(3) < 0) break block87;
                                    if (n11 != 3) break block90;
                                    if (!itemDefinition.getName().toLowerCase().contains("staff")) break block87;
                                    n2 = itemDefinition.getBonus(3);
                                    if (n3 != -1 && itemStack5.getId() == nArray2[n3]) {
                                        n2 = 5000;
                                    }
                                    break block88;
                                }
                                n2 = 0 + itemDefinition.getBonus(3) * 3;
                                n2 += itemDefinition.getBonus(5);
                                n2 += itemDefinition.getBonus(6);
                                n2 += itemDefinition.getBonus(7);
                                break block88;
                            }
                            if (n != 2) break block88;
                            if (itemDefinition.getBonus(4) < 0 || n11 == 5 || n11 == 13) break block87;
                            if (n11 != 3) break block91;
                            if (!itemDefinition.getName().toLowerCase().endsWith("bow")) break block87;
                            n2 = itemDefinition.getBonus(4);
                            break block88;
                        }
                        n2 = 0 + itemDefinition.getBonus(4) * 3;
                        n2 += itemDefinition.getBonus(5);
                        n2 += itemDefinition.getBonus(6);
                        n2 += itemDefinition.getBonus(7);
                    }
                    if (nArray5[n][n11] < n2) {
                        nArray4[n][n11] = itemStack5.getId();
                        nArray5[n][n11] = n2;
                    }
                }
                ++n;
            }
        }
        boolean bl4 = false;
        object5 = new ArrayList<Integer>();
        ((ArrayList)object5).add(0);
        if (bl2) {
            ((ArrayList)object5).add(1);
        }
        if (n4 != 0) {
            ((ArrayList)object5).add(2);
        }
        if (player.botCombatLoadoutSlotCursor == -1) {
            player.botCombatStyle = (Integer)((ArrayList)object5).get(GameUtil.randomInt(((ArrayList)object5).size()));
            if (player.currentBotTask.getForcedCombatStyle() != -1) {
                if (player.currentBotTask.getForcedCombatStyle() == 0) {
                    player.botCombatStyle = 0;
                } else if (player.currentBotTask.getForcedCombatStyle() == 1) {
                    if (!bl2) {
                        player.botTaskReturnToBankRequested = true;
                        player.currentBotTask.startWalkToBank(player);
                        return;
                    }
                    player.botCombatStyle = 1;
                } else if (player.currentBotTask.getForcedCombatStyle() == 2) {
                    if (n4 == 0) {
                        player.botTaskReturnToBankRequested = true;
                        player.currentBotTask.startWalkToBank(player);
                        return;
                    }
                    player.botCombatStyle = 2;
                }
            }
        }
        player.botElementalSpellIndex = -1;
        if (player.botCombatStyle == 1) {
            int n12 = 10000;
            int n13 = -1;
            n = nArray4[2][3];
            ItemStack itemStack6 = new ItemStack(n);
            for (ItemStack itemStack7 : arrayList) {
                if (!AmmunitionDefinition.isCompatible(player, itemStack6, itemStack7) || itemStack7.getDefinition().getValue() >= n12) continue;
                n12 = itemStack7.getDefinition().getValue();
                n13 = itemStack7.getId();
            }
            player.d(n13, 200);
        }
        if (player.botCombatStyle == 2 && n3 != -1) {
            SpellDefinition spellDefinition = (SpellDefinition)((Object)((ArrayList)object).get(n3));
            ItemStack[] itemStackArray6 = spellDefinition.getRuneCosts();
            int n15 = itemStackArray6.length;
            n = 0;
            while (n < n15) {
                ItemStack itemStack8 = itemStackArray6[n];
                if (!(player.ownsItem(nArray2[n3]) && (n3 == 0 && itemStack8.getId() == 556 || n3 == 1 && itemStack8.getId() == 555 || n3 == 2 && itemStack8.getId() == 557 || n3 == 3 && itemStack8.getId() == 554))) {
                    player.d(itemStack8.getId(), itemStack8.getAmount() * 200);
                }
                ++n;
            }
            player.botElementalSpellIndex = n3;
        }
        if (player.botCombatStyle == 2 && n3 == -1) {
            player.botCombatStyle = 0;
        }
        int selectedLoadoutIndex = 0;
        if (player.botCombatStyle == 2) {
            selectedLoadoutIndex = 1;
        } else if (player.botCombatStyle == 1) {
            selectedLoadoutIndex = 2;
        }
        if (!bl) {
            int selectedSlot = 0;
            while (selectedSlot < nArray4[selectedLoadoutIndex].length) {
                int n17 = nArray4[selectedLoadoutIndex][selectedSlot];
                if (n17 > 0) {
                    player.botCombatLoadoutItemIds.add(n17);
                }
                ++selectedSlot;
            }
        }
        while (var4_22 < 3) {
            int n18 = 0;
            while (n18 < nArray4[var4_22].length) {
                n = nArray4[var4_22][n18];
                if (n > 0) {
                    if (var4_22 == 0) {
                        player.botMeleeLoadoutItemIds.add(n);
                    } else if (var4_22 == 1) {
                        player.botMagicLoadoutItemIds.add(n);
                    } else if (var4_22 == 2) {
                        player.botRangedLoadoutItemIds.add(n);
                    }
                }
                ++n18;
            }
            ++var4_22;
        }
    }
}

