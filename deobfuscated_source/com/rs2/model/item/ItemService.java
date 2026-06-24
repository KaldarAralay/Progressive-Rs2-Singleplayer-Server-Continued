/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item;

import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.LootNextItemTask;
import com.rs2.model.item.PickupItemTask;
import com.rs2.model.path.PathReachability;
import com.rs2.model.player.Player;

public final class ItemService {
    private static ItemService instance = new ItemService();
    static PathReachability pathReachability = new PathReachability();

    public static boolean isEssencePouch(int n) {
        if (n >= 5510 && n <= 5515) {
            return true;
        }
        return n == 5519;
    }

    public final void pickupItem(Player player, int n, Position position) {
        int n2 = player.nextActionSequence();
        World.scheduleTickTask(new PickupItemTask(this, 1, true, player, n2, n, position));
    }

    public final void handleBotGroundItemPickupResult(Player player, int n, GroundItem groundItem, boolean bl) {
        int n2 = player.getEquipmentManager().getItemIdAtSlot(3);
        boolean bl2 = false;
        if (n2 > 0) {
            bl2 = ItemDefinition.forId(n2).isStackable();
        }
        if (n == player.getEquipmentManager().getItemIdAtSlot(13) || n == player.getEquipmentManager().getItemIdAtSlot(3) && bl2) {
            if (bl) {
                player.getEquipmentManager().equipFromInventorySlot(player.getInventoryManager().getContainer().indexOfItem(n));
            }
            if (player.botCombatState != null && player.botCombatState.equals("loot arrows")) {
                player.botCombatState = null;
                if (player.botLootResumeTarget != null && !player.botLootResumeTarget.isDead()) {
                    CombatManager.startCombat(player, player.botLootResumeTarget);
                } else if (player.currentBotTask != null) {
                    player.interactWithBotNpcTargets(player.botInteractionTargetIds);
                }
            }
        }
        if (player.botCombatState != null && player.botCombatState.equals("loot items")) {
            player.botLootPickupTargets.remove(0);
            if (player.currentBotTask != null) {
                if (groundItem.getItem().getDefinition().getName().toLowerCase().endsWith("bones")) {
                    if (bl) {
                        player.getBoneBuryingHandler().handleBuryBone(n, player.getInventoryManager().getContainer().indexOfItem(n));
                    }
                    if (player.botLootPickupTargets.size() > 0) {
                        LootNextItemTask lootNextItemTask = new LootNextItemTask(this, 1, player);
                        World.getTaskScheduler().schedule(lootNextItemTask);
                        return;
                    }
                } else if (player.currentBotTask.lootSellShopIds.size() > 0 && player.botLootSellGroundItems.contains(groundItem) && bl) {
                    player.botLootSellItems.add(groundItem.getItem());
                    player.botLootSellGroundItems.remove(groundItem);
                }
            }
            if (player.currentBotTask == null && !player.clanWarsBot && player.botLootSellGroundItems.contains(groundItem) && bl) {
                player.botLootSellItems.add(groundItem.getItem());
                player.botLootSellGroundItems.remove(groundItem);
            }
            if (player.botLootPickupTargets.size() > 0) {
                BotCombatHelper.pickupBotCombatGroundItem(player, ((GroundItem)player.botLootPickupTargets.get(0)).getItem().getId(), ((GroundItem)player.botLootPickupTargets.get(0)).getPosition());
                return;
            }
            player.botCombatState = null;
            player.botLootGroundItems.clear();
            player.botLootPickupTargets.clear();
            if (player.currentBotTask != null) {
                GameplayHelper.shouldReturnToBankForBotTask(player);
                int n3 = n = player.isInWilderness() ? 8 : 0;
                if (player.getInventoryManager().getItemAmount(player.botFoodItemId) <= n && player.currentBotTask.getForcedCombatStyle() != 2 || player.botTaskReturnToBankRequested) {
                    player.currentBotTask.startWalkToBank(player);
                    return;
                }
                player.interactWithBotNpcTargets(player.botInteractionTargetIds);
            }
        }
    }

    public static int getPrice(int n, String string, int n2) {
        double d = 0.0;
        if (string.equals("donator")) {
            d = new ItemStack(n).getDefinition().getDonatorPointValue();
        } else if (string.equals("buyfromshop")) {
            if (n2 == 995) {
                d = new ItemStack(n).getDefinition().getShopValue();
            }
            if (n2 == 6529) {
                d = new ItemStack(n).getDefinition().getTokkulValue();
            }
            d = d < 1.0 ? 1.0 : d;
        } else if (string.startsWith("selltoshop")) {
            if (n2 == 995) {
                d = string.contains("Specialty") ? (double)new ItemStack(n).getDefinition().getHighAlchemyValue() : (double)new ItemStack(n).getDefinition().getLowAlchemyValue();
            }
            if (n2 == 6529) {
                d = new ItemStack(n).getDefinition().getTokkulValue() * 15 / 100;
            }
        } else if (string.equals("lowalch") && n2 == 995) {
            d = new ItemStack(n).getDefinition().getLowAlchemyValue();
        } else if (string.equals("highalch") && n2 == 995) {
            d = new ItemStack(n).getDefinition().getHighAlchemyValue();
        }
        return (int)d;
    }

    public static String getItemName(int n) {
        return new ItemStack(n).getDefinition().getName();
    }

    public static ItemService getInstance() {
        return instance;
    }
}

