/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.combat;

import com.rs2.ServerSettings;
import com.rs2.bot.BotPlayer;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotGroundItemPickupTask;
import com.rs2.bot.combat.BotPvpCombatHandler;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.consumable.PotionHandler;
import com.rs2.model.player.GrandExchangeManager;
import com.rs2.model.player.InventoryManager;
import com.rs2.model.player.Player;
import com.rs2.model.player.PlayerGroup;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.skill.magic.TeleportManager;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.RectangularArea;
import com.rs2.util.Vector2f;
import com.rs2.util.path.PathFinder;
import com.rs2.util.path.WalkingCollisionMap;
import java.util.ArrayList;
import java.util.Iterator;

public final class BotCombatHelper {
    private static RectangularArea[] blockedPvpSearchAreas = new RectangularArea[]{new RectangularArea(3176, 3846, 3190, 3859), new RectangularArea(3226, 3817, 3236, 3828), new RectangularArea(3306, 3852, 3315, 3862)};
    private static RectangularArea[] freeToPlayBlockedPvpSearchAreas = new RectangularArea[]{new RectangularArea(3007, 3839, 3023, 3856), new RectangularArea(3170, 3798, 3240, 3863), new RectangularArea(3061, 3851, 3072, 3864), new RectangularArea(3078, 3560, 3099, 3581)};
    private static RectangularArea[] hotzonePvpSearchAreas = new RectangularArea[]{new RectangularArea(3225, 3521, 3275, 3551), new RectangularArea(3068, 3521, 3103, 3551)};

    public static void disableBotCombatPrayers(Player player) {
        if (player.getActivePrayers()[10]) {
            player.getPrayerManager().togglePrayer(10);
        }
        if (player.getActivePrayers()[8]) {
            player.getPrayerManager().togglePrayer(8);
        }
        if (player.getActivePrayers()[14]) {
            player.getPrayerManager().togglePrayer(14);
        }
        if (player.getActivePrayers()[13]) {
            player.getPrayerManager().togglePrayer(13);
        }
        if (player.getActivePrayers()[12]) {
            player.getPrayerManager().togglePrayer(12);
        }
        if (player.getActivePrayers()[17]) {
            player.getPrayerManager().togglePrayer(17);
        }
    }

    public static void setBotSkillLevel(Player player, int n, int n2) {
        if (n2 > 99) {
            n2 = 99;
        }
        if (n2 <= 0) {
            n2 = 1;
        }
        player.executeCheatCommand("setlevel", new String[]{String.valueOf(n), String.valueOf(n2)}, false);
    }

    public static boolean isPlayerInAnyArea(Player player, RectangularArea[] areas) {
        int n = areas.length;
        int n2 = 0;
        while (n2 < n) {
            RectangularArea area = areas[n2];
            if (area.contains(player.getPosition())) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    private static boolean isPositionInAnyArea(Position position, RectangularArea[] areas) {
        int n = areas.length;
        int n2 = 0;
        while (n2 < n) {
            RectangularArea area = areas[n2];
            if (area.contains(position)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public static void advanceBotEscapeWaypoints(Player player, Position[] positionArray) {
        Position position = positionArray[player.botPathWaypointIndex];
        if (GameUtil.isWithinDistance(player.getPosition(), position, 1)) {
            if (player.botPathWaypointIndex == positionArray.length - 1) {
                player.botEscapeRouteName = "";
                player.botPathWaypointIndex = -1;
                return;
            }
            ++player.botPathWaypointIndex;
            position = positionArray[player.botPathWaypointIndex];
        }
        BotCombatHelper.walkBotTowardPosition(player, position);
    }

    public static void restorePrimaryCombatGear(Player player) {
        player.getEquipmentManager().equipFromInventorySlot(player.getInventoryManager().getContainer().indexOfItem(player.botWeaponItemId));
        if (player.botShieldItemId != 0 && player.getEquipmentManager().getItemIdAtSlot(5) != player.botShieldItemId) {
            player.getEquipmentManager().equipFromInventorySlot(player.getInventoryManager().getContainer().indexOfItem(player.botShieldItemId));
        }
        player.botActiveCombatStyle = player.botPrimaryCombatStyle;
    }

    public static double calculateBotHitpointsExperience(Player player) {
        double d = player.getSkillManager().getExperience()[0] + player.getSkillManager().getExperience()[1] + player.getSkillManager().getExperience()[2] + player.getSkillManager().getExperience()[4] + player.getSkillManager().getExperience()[6];
        double d2 = d / 3.0 + 1154.0;
        return d2;
    }

    public static void sellBotLootItems(Player player) {
        if (player.botLootSellItems.size() > 0) {
            int n;
            if (player.currentBotTask == null) {
                n = GameUtil.randomInt(3) == 0 ? 151 : 50;
            } else {
                BotTaskDefinition botTaskDefinition = player.currentBotTask;
                int n2 = GameUtil.randomInt(botTaskDefinition.lootSellShopIds.size());
                n = (Integer)botTaskDefinition.lootSellShopIds.get(n2);
            }
            ShopManager.openShop(player, n);
            for (Object itemStackObject : player.botLootSellItems) {
                ItemStack itemStack = (ItemStack)itemStackObject;
                int n3 = GrandExchangeManager.getGuidePrice(itemStack.getId());
                if (n3 >= 10000) continue;
                ShopManager.sellItemStack(player, itemStack);
            }
            player.botLootSellItems.clear();
            player.botLootSellGroundItems.clear();
        }
    }

    public static boolean processBotLootQueue(Player player) {
        if (player.botLootGroundItems.size() <= 0) {
            player.botCombatState = null;
            if (player.currentBotTask != null) {
                if (player.getInventoryManager().getItemAmount(player.botFoodItemId) <= 2 && player.currentBotTask.getForcedCombatStyle() != 2 || player.botTaskReturnToBankRequested) {
                    player.currentBotTask.startWalkToBank(player);
                } else {
                    player.interactWithBotNpcTargets(player.botInteractionTargetIds);
                }
            }
            return false;
        }
        player.botLootPickupTargets.clear();
        for (Object groundItemObject : player.botLootGroundItems) {
            GroundItem groundItem = (GroundItem)groundItemObject;
            int n;
            int n2;
            ItemStack itemStack = groundItem.getItem();
            if (itemStack == null) continue;
            GroundItemManager.getInstance();
            GroundItem groundItem2 = GroundItemManager.findVisibleItem(player, groundItem.getItem().getId(), groundItem.getPosition());
            if (groundItem2 == null) continue;
            if (player.currentBotTask != null) {
                n2 = 0;
                if (player.currentBotTask.ignoredLootItemIds != null) {
                    int[] nArray = player.currentBotTask.ignoredLootItemIds;
                    int n3 = player.currentBotTask.ignoredLootItemIds.length;
                    int n4 = 0;
                    while (n4 < n3) {
                        n = nArray[n4];
                        if (groundItem2.getItem().getId() == n) {
                            n2 = 1;
                            break;
                        }
                        ++n4;
                    }
                    if (n2 != 0) continue;
                }
            }
            n2 = GrandExchangeManager.getGuidePrice(itemStack.getId());
            int n5 = n = player.currentBotTask == null ? 1000 : 0;
            if (groundItem2.getItem().getDefinition().isStackable() && player.getInventoryManager().containsItem(groundItem2.getItem().getId()) || groundItem2.getItem().getDefinition().isStackable() && player.getInventoryManager().containsItem(groundItem2.getItem().getId())) {
                player.botLootPickupTargets.add(groundItem);
                continue;
            }
            if (n2 * itemStack.getAmount() < n) continue;
            player.botLootPickupTargets.add(groundItem);
            player.botLootSellGroundItems.add(groundItem);
        }
        if (player.botLootPickupTargets.size() <= 0) {
            player.botCombatState = null;
            player.botLootGroundItems.clear();
            if (player.currentBotTask != null) {
                if (player.getInventoryManager().getItemAmount(player.botFoodItemId) <= 2 && player.currentBotTask.getForcedCombatStyle() != 2 || player.botTaskReturnToBankRequested) {
                    player.currentBotTask.startWalkToBank(player);
                } else {
                    player.interactWithBotNpcTargets(player.botInteractionTargetIds);
                }
            }
            return false;
        }
        if (player.currentBotTask == null) {
            player.getMovementQueue().setRunning(true);
        }
        BotCombatHelper.pickupBotCombatGroundItem(player, ((GroundItem)player.botLootPickupTargets.get(0)).getItem().getId(), ((GroundItem)player.botLootPickupTargets.get(0)).getPosition());
        return false;
    }

    public static boolean pickupVisibleGroundItem(Player player, int n, Position position) {
        if (((Boolean)player.getAttributes().get("canPickup")).booleanValue()) {
            GroundItemManager.getInstance();
            GroundItem groundItem = GroundItemManager.findVisibleItem(player, n, position);
            if (groundItem != null) {
                player.setInteractionTargetY(position.getY());
                player.setInteractionTargetId(n);
                player.setInteractionTargetX(position.getX());
                player.setInteractionTargetPlane(player.getPosition().getPlane());
                ItemStack itemStack = groundItem.getItem();
                PathFinder.getInstance();
                PathFinder.findPath(player, position.getX(), position.getY(), true, 0, 0);
                ItemService.getInstance().pickupItem(player, itemStack.getId(), groundItem.getPosition());
                return true;
            }
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static boolean pickupBotCombatGroundItem(Player object, int n, Position position) {
        if ((Boolean)((Entity)object).getAttributes().get("canPickup") == false) return false;
        GroundItemManager.getInstance();
        GroundItem groundItem = GroundItemManager.findVisibleItem((Player)object, n, position);
        if (groundItem != null) {
            boolean bl = false;
            if (((Player)object).botCombatState != null && ((Player)object).botCombatState.equals("loot arrows")) {
                bl = true;
            }
            if (((Player)object).currentBotTask != null && !bl) {
                PathFinder.getInstance();
                bl = PathFinder.findPath((Player)object, position.getX(), position.getY(), false, 0, 0);
                if (!bl && SpellDefinition.TELEKINETIC_GRAB.getRequiredLevel() <= ((Player)object).getSkillManager().getCurrentLevels()[6] && BotCombatHelper.hasRunesForSpell((Player)object, SpellDefinition.TELEKINETIC_GRAB)) {
                    MagicSpellAction.scheduleTelekineticGrab((Player)object, SpellDefinition.TELEKINETIC_GRAB, n, position);
                    return true;
                }
                if (((Player)object).getInventoryManager().getItemAmount(((Player)object).botFoodItemId) == 0 && ((Player)object).currentBotTask.combatTask && ((Player)object).currentBotTask.getForcedCombatStyle() != 2 || ((Player)object).getInventoryManager().getContainer().getFreeSlots() == 0) {
                    ((Player)object).botLootPickupTargets.clear();
                    ((Player)object).botLootGroundItems.clear();
                    ((Player)object).currentBotTask.startWalkToBank((Player)object);
                    return false;
                }
                ItemStack itemStack = ((GroundItem)((Player)object).botLootPickupTargets.get(0)).getItem();
                InventoryManager inventoryManager = ((Player)object).getInventoryManager();
                if (!(itemStack == null ? false : inventoryManager.hasSpaceFor(itemStack)) && ((Player)object).getInventoryManager().getItemAmount(((Player)object).botFoodItemId) > 0) {
                    BotCombatHelper.eatBotFood((Player)object);
                    World.getTaskScheduler().schedule(new BotGroundItemPickupTask(2, (Player)object, position, n, groundItem));
                    return true;
                }
            }
            ((Player)object).setInteractionTargetY(position.getY());
            ((Player)object).setInteractionTargetId(n);
            ((Player)object).setInteractionTargetX(position.getX());
            ((Player)object).setInteractionTargetPlane(((Entity)object).getPosition().getPlane());
            ItemStack itemStack = groundItem.getItem();
            PathFinder.getInstance();
            PathFinder.findPath((Player)object, position.getX(), position.getY(), true, 0, 0);
            ItemService.getInstance().pickupItem((Player)object, itemStack.getId(), groundItem.getPosition());
            return true;
        }
        if (((Player)object).botCombatState != null && ((Player)object).botCombatState.equals("loot arrows")) {
            ((Player)object).botCombatState = null;
            ((Player)object).setAutoRetaliate(true);
            if (((Player)object).botLootResumeTarget != null && !((Player)object).botLootResumeTarget.isDead()) {
                CombatManager.startCombat((Entity)object, ((Player)object).botLootResumeTarget);
                return true;
            }
            if (((Player)object).currentBotTask == null) return true;
        } else {
            ((Player)object).botCombatState = null;
            if (((Player)object).currentBotTask == null) return true;
            if (((Player)object).getInventoryManager().getItemAmount(((Player)object).botFoodItemId) <= 2 && ((Player)object).currentBotTask.getForcedCombatStyle() != 2 || ((Player)object).botTaskReturnToBankRequested) {
                ((Player)object).currentBotTask.startWalkToBank((Player)object);
                return true;
            }
        }
        ((Player)object).interactWithBotNpcTargets(((Player)object).botInteractionTargetIds);
        return true;
    }

    public static void unequipMagicPenaltyGear(Player player) {
        ItemStack[] itemStackArray = player.getEquipmentManager().getContainer().getItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null && itemStack.getDefinition().getBonus(8) < 0) {
                player.getEquipmentManager().unequipSlot(itemStack.getDefinition().getEquipmentSlot());
            }
            ++n2;
        }
        player.botMagicPenaltyGearUnequipped = true;
    }

    public static void reequipMagicPenaltyGear(Player player) {
        int n = 0;
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemStack = itemStackArray[n3];
            ++n;
            if (itemStack != null && itemStack.getDefinition().getBonus(8) < 0) {
                player.getEquipmentManager().equipFromInventorySlot(n - 1);
            }
            ++n3;
        }
        player.botMagicPenaltyGearUnequipped = false;
    }

    public static boolean hasRunesForSpell(Player player, SpellDefinition spellDefinition) {
        ItemStack[] itemStackArray = spellDefinition.getRuneCosts();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack runeCost = itemStackArray[n2];
            if (!(runeCost.getId() == 556 && player.getEquipmentManager().getItemIdAtSlot(3) == 1381 || runeCost.getId() == 555 && player.getEquipmentManager().getItemIdAtSlot(3) == 1383 || runeCost.getId() == 557 && player.getEquipmentManager().getItemIdAtSlot(3) == 1385 || runeCost.getId() == 554 && player.getEquipmentManager().getItemIdAtSlot(3) == 1387 || player.getInventoryManager().containsItemAmount(runeCost.getId(), runeCost.getAmount()))) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    public static boolean eatBotFood(Player player) {
        int n = 0;
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemStack = itemStackArray[n3];
            ++n;
            if (itemStack != null && itemStack.getId() == player.botFoodItemId) {
                player.getFoodHandler().eatFood(itemStack.getId(), n - 1);
                return true;
            }
            ++n3;
        }
        player.botFoodDepleted = true;
        return false;
    }

    public static boolean drinkStrengthPotion(Player player) {
        int n = 0;
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemStack = itemStackArray[n3];
            ++n;
            if (itemStack != null && player.getPotionHandler().selectPotionForItemId(itemStack.getId())) {
                while (0 < PotionHandler.definitions[player.getPotionHandler().selectedDefinitionIndex].getSkillIds().length) {
                    if (PotionHandler.definitions[player.getPotionHandler().selectedDefinitionIndex].getSkillIds()[0] == 2) {
                        player.getPotionHandler().drinkPotion(itemStack.getId(), n - 1);
                        return true;
                    }
                    ++n;
                }
            }
            ++n3;
        }
        player.botStrengthPotionDepleted = true;
        return false;
    }

    public static boolean hasPrayerLevelForCombatStyle(Player player, int n) {
        if (n == 0) {
            player.getSkillManager();
            if (SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[5]) >= 43) {
                return true;
            }
        }
        if (n == BotPvpCombatHandler.RANGED_COMBAT_STYLE) {
            player.getSkillManager();
            if (SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[5]) >= 40) {
                return true;
            }
        }
        if (n == BotPvpCombatHandler.MAGIC_COMBAT_STYLE) {
            player.getSkillManager();
            if (SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[5]) >= 37) {
                return true;
            }
        }
        return false;
    }

    private static void updateProtectionPrayerForStyle(Player player, Player player2, boolean bl) {
        int n = 0;
        if (player.botActiveCombatStyle == 0 && player2.getActivePrayers()[14]) {
            n = 1;
        } else if (player.botActiveCombatStyle == BotPvpCombatHandler.RANGED_COMBAT_STYLE && player2.getActivePrayers()[13]) {
            n = 1;
        } else if (player.botActiveCombatStyle == BotPvpCombatHandler.MAGIC_COMBAT_STYLE && player2.getActivePrayers()[12]) {
            n = 1;
        }
        boolean bl2 = true;
        if (bl && n == 0 && player2.getSkillManager().getCurrentLevels()[5] > 15) {
            bl2 = false;
        }
        if (bl2) {
            if (player.botOpponentCombatStyle == 0 && !player.getActivePrayers()[14]) {
                Player player3 = player2;
                player2 = player;
                n = 1;
                if (player3.isMoving() && !player3.isRunningMovement()) {
                    ++n;
                }
                if (player3.isMoving() && player3.isRunningMovement()) {
                    n += 2;
                }
                if (GameUtil.isWithinDistance(player2.getPosition(), player3.getPosition(), n)) {
                    if (player.botQueuedPrayerId != 14) {
                        player.botQueuedPrayerId = 14;
                        player.botPrayerSwitchDelayTicks = 0;
                        return;
                    }
                    if (player.botPrayerSwitchDelayTicks < 3) {
                        ++player.botPrayerSwitchDelayTicks;
                        return;
                    }
                    player.getPrayerManager().togglePrayer(14);
                    player.botPrayerSwitchDelayTicks = 0;
                    return;
                }
            }
            if (player.botOpponentCombatStyle == BotPvpCombatHandler.RANGED_COMBAT_STYLE && !player.getActivePrayers()[13]) {
                if (player.botQueuedPrayerId != 13) {
                    player.botQueuedPrayerId = 13;
                    player.botPrayerSwitchDelayTicks = 0;
                    return;
                }
                if (player.botPrayerSwitchDelayTicks < 3) {
                    ++player.botPrayerSwitchDelayTicks;
                    return;
                }
                player.getPrayerManager().togglePrayer(13);
                player.botPrayerSwitchDelayTicks = 0;
                return;
            }
            if (player.botOpponentCombatStyle == BotPvpCombatHandler.MAGIC_COMBAT_STYLE && !player.getActivePrayers()[12]) {
                if (player.botQueuedPrayerId != 12) {
                    player.botQueuedPrayerId = 12;
                    player.botPrayerSwitchDelayTicks = 0;
                    return;
                }
                if (player.botPrayerSwitchDelayTicks < 3) {
                    ++player.botPrayerSwitchDelayTicks;
                    return;
                }
                player.getPrayerManager().togglePrayer(12);
                player.botPrayerSwitchDelayTicks = 0;
                return;
            }
        } else {
            if (player.botOpponentCombatStyle == 0 && player.getActivePrayers()[14]) {
                if (player.botQueuedPrayerId != 14) {
                    player.botQueuedPrayerId = 14;
                    player.botPrayerSwitchDelayTicks = 0;
                    return;
                }
                if (player.botPrayerSwitchDelayTicks < 3) {
                    ++player.botPrayerSwitchDelayTicks;
                    return;
                }
                player.getPrayerManager().togglePrayer(14);
                player.botPrayerSwitchDelayTicks = 0;
                return;
            }
            if (player.botOpponentCombatStyle == BotPvpCombatHandler.RANGED_COMBAT_STYLE && player.getActivePrayers()[13]) {
                if (player.botQueuedPrayerId != 13) {
                    player.botQueuedPrayerId = 13;
                    player.botPrayerSwitchDelayTicks = 0;
                    return;
                }
                if (player.botPrayerSwitchDelayTicks < 3) {
                    ++player.botPrayerSwitchDelayTicks;
                    return;
                }
                player.getPrayerManager().togglePrayer(13);
                player.botPrayerSwitchDelayTicks = 0;
                return;
            }
            if (player.botOpponentCombatStyle == BotPvpCombatHandler.MAGIC_COMBAT_STYLE && player.getActivePrayers()[12]) {
                if (player.botQueuedPrayerId != 12) {
                    player.botQueuedPrayerId = 12;
                    player.botPrayerSwitchDelayTicks = 0;
                    return;
                }
                if (player.botPrayerSwitchDelayTicks < 3) {
                    ++player.botPrayerSwitchDelayTicks;
                    return;
                }
                player.getPrayerManager().togglePrayer(12);
                player.botPrayerSwitchDelayTicks = 0;
            }
        }
    }

    public static void toggleProtectionPrayerForOpponentStyle(Player player) {
        if (player.botOpponentCombatStyle == 0 && !player.getActivePrayers()[14]) {
            if (player.botQueuedPrayerId != 14) {
                player.botQueuedPrayerId = 14;
                player.botPrayerSwitchDelayTicks = 0;
                return;
            }
            if (player.botPrayerSwitchDelayTicks < 3) {
                ++player.botPrayerSwitchDelayTicks;
                return;
            }
            player.getPrayerManager().togglePrayer(14);
            player.botPrayerSwitchDelayTicks = 0;
            return;
        }
        if (player.botOpponentCombatStyle == BotPvpCombatHandler.RANGED_COMBAT_STYLE && !player.getActivePrayers()[13]) {
            if (player.botQueuedPrayerId != 13) {
                player.botQueuedPrayerId = 13;
                player.botPrayerSwitchDelayTicks = 0;
                return;
            }
            if (player.botPrayerSwitchDelayTicks < 3) {
                ++player.botPrayerSwitchDelayTicks;
                return;
            }
            player.getPrayerManager().togglePrayer(13);
            player.botPrayerSwitchDelayTicks = 0;
            return;
        }
        if (player.botOpponentCombatStyle == BotPvpCombatHandler.MAGIC_COMBAT_STYLE && !player.getActivePrayers()[12]) {
            if (player.botQueuedPrayerId != 12) {
                player.botQueuedPrayerId = 12;
                player.botPrayerSwitchDelayTicks = 0;
                return;
            }
            if (player.botPrayerSwitchDelayTicks < 3) {
                ++player.botPrayerSwitchDelayTicks;
                return;
            }
            player.getPrayerManager().togglePrayer(12);
            player.botPrayerSwitchDelayTicks = 0;
        }
    }

    public static boolean updateBotDefensivePrayers(Player player, Player player2) {
        boolean bl = true;
        if (player.getSkillManager().getCurrentLevels()[5] <= 10) {
            bl = false;
        }
        if (BotCombatHelper.hasPrayerLevelForCombatStyle(player, player.botOpponentCombatStyle) && bl && player2.getCombatTarget() != null && player2.getCombatTarget() == player) {
            if (!BotCombatHelper.hasPrayerLevelForCombatStyle(player2, player.botActiveCombatStyle)) {
                BotCombatHelper.updateProtectionPrayerForStyle(player, player2, false);
            } else if (!BotCombatHelper.isFreeToPlayWorld()) {
                player.getSkillManager();
                if (SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[5]) >= 52 && !player.botCombatEscapeActive && ServerSettings.cacheVersion >= 336) {
                    if (!player.getActivePrayers()[17]) {
                        player.getPrayerManager().togglePrayer(17);
                    }
                } else {
                    BotCombatHelper.updateProtectionPrayerForStyle(player, player2, true);
                }
            } else {
                BotCombatHelper.updateProtectionPrayerForStyle(player, player2, true);
            }
        }
        if (player.botActiveCombatStyle == 0 && bl) {
            player.getSkillManager();
            if (SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[5]) >= 31 && !player.getActivePrayers()[10]) {
                player.getPrayerManager().togglePrayer(10);
            }
        }
        if (!bl) {
            BotCombatHelper.disableBotCombatPrayers(player);
        }
        return true;
    }

    public static void stopBotCombatTick(Player player) {
        if (player.botCombatTickTask != null && player.botCombatTickTask.isActive()) {
            player.botCombatTickTask.stop();
        }
        player.getMovementQueue().setRunning(false);
        player.getPrayerManager().deactivateAll();
    }

    public static void prepareBotPvpSearchPosition(Player player) {
        int n;
        int n2 = 2951;
        int n3 = 3376;
        int n4 = 3520;
        double d = player.getCombatLevel();
        double d2 = d * 1.5;
        if (ServerSettings.wildyBotsUseNewGeneration && ServerSettings.wildyBotsIgnoreCombatForDeepWilderness) {
            d2 = 52.0;
        }
        if (d2 > 52.0) {
            d2 = 52.0;
        }
        int n5 = (int)d2;
        if ((n5 = (n5 << 3) + 3520 - 1) > 3965) {
            n5 = 3965;
        }
        if (ServerSettings.freeToPlayWorld && n5 > 3895) {
            n5 = 3895;
        }
        player.botWildernessMaxY = n5;
        if (ServerSettings.hotzonesForWildyBotsEnabled && (n = GameUtil.randomInt(hotzonePvpSearchAreas.length + 1)) < hotzonePvpSearchAreas.length) {
            RectangularArea rectangularArea = hotzonePvpSearchAreas[n];
            n2 = rectangularArea.getMinX();
            n4 = rectangularArea.getMinY();
            n3 = rectangularArea.getMaxX();
            n5 = rectangularArea.getMaxY();
        }
        n = n3 - n2;
        n3 = n2 + GameUtil.randomInt(n);
        int n6 = n4 + GameUtil.randomInt(n5 -= n4);
        boolean bl = true;
        while (bl) {
            n3 = n2 + GameUtil.randomInt(n);
            n6 = n4 + GameUtil.randomInt(n5);
            boolean bl2 = true;
            if (ServerSettings.freeToPlayWorld && BotCombatHelper.isPositionInAnyArea(new Position(n3, n6), freeToPlayBlockedPvpSearchAreas)) {
                bl2 = false;
            }
            if (BotCombatHelper.isPositionInAnyArea(new Position(n3, n6), blockedPvpSearchAreas)) {
                bl2 = false;
            }
            if (!bl2) continue;
            bl = WalkingCollisionMap.getTileFlags(n3, n6, 0) != 0;
        }
        player.moveTo(new Position(n3, n6, 0));
    }

    static boolean isTargetLootWorthRisk(Player player, Player player2) {
        if (player.skulled) {
            return true;
        }
        if (player.currentGroup != null && player2.currentGroup == null && player.isInMultiCombatArea() && player2.isInMultiCombatArea()) {
            return true;
        }
        double riskRatio = player2.getCombatLevel() > player.getCombatLevel() + 5 ? 0.3 : 0.2;
        int targetLootValue = 0;
        for (Object itemStackObject : player2.getUnprotectedItems(player2.getEquipmentManager().getContainer().getItems())) {
            ItemStack itemStack = (ItemStack)itemStackObject;
            if (itemStack == null) continue;
            int guidePrice = GrandExchangeManager.getGuidePrice(itemStack.getId());
            targetLootValue += guidePrice * itemStack.getAmount();
        }
        int playerRiskValue = 0;
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                int guidePrice = GrandExchangeManager.getGuidePrice(itemStack.getId());
                playerRiskValue += guidePrice * itemStack.getAmount();
            }
            ++n2;
        }
        itemStackArray = player.getEquipmentManager().getContainer().getItems();
        n = itemStackArray.length;
        n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                int guidePrice = GrandExchangeManager.getGuidePrice(itemStack.getId());
                playerRiskValue += guidePrice * itemStack.getAmount();
            }
            ++n2;
        }
        return !((double)targetLootValue < (double)playerRiskValue * riskRatio);
    }

    public static int selectBotLoadoutItemId(Player player, int[] freeItemIds, int[] memberItemIds, boolean randomize) {
        ArrayList<Integer> itemIds = new ArrayList<Integer>();
        if (freeItemIds != null) {
            int n = freeItemIds.length;
            int n2 = 0;
            while (n2 < n) {
                itemIds.add(freeItemIds[n2]);
                ++n2;
            }
        }
        if (!BotCombatHelper.isFreeToPlayWorld() && memberItemIds != null) {
            int n = memberItemIds.length;
            int n2 = 0;
            while (n2 < n) {
                itemIds.add(memberItemIds[n2]);
                ++n2;
            }
        }
        if (!randomize) {
            int selectedIndex = 0;
            int n = 0;
            while (n < itemIds.size()) {
                int itemId = (Integer)itemIds.get(n);
                if (ItemDefinition.isDefined(itemId)) {
                    if (!player.getEquipmentManager().canEquipItem(itemId)) {
                        selectedIndex = n - 1;
                        break;
                    }
                    selectedIndex = n;
                }
                ++n;
            }
            if (selectedIndex < 0 || itemIds.size() == 0) {
                return -1;
            }
            return (Integer)itemIds.get(selectedIndex);
        }
        ArrayList<Integer> equippableItemIds = new ArrayList<Integer>();
        Iterator iterator = itemIds.iterator();
        while (iterator.hasNext()) {
            int itemId = (Integer)iterator.next();
            if (!ItemDefinition.isDefined(itemId) || !player.getEquipmentManager().canEquipItem(itemId)) continue;
            equippableItemIds.add(itemId);
        }
        if (equippableItemIds.size() == 0) {
            return -1;
        }
        return (Integer)equippableItemIds.get(GameUtil.randomInt(equippableItemIds.size()));
    }

    public static int[] filterEquippableMemberLoadoutItems(Player player, int[] freeItemIds, int[] memberItemIds) {
        ArrayList<Integer> candidateItemIds = new ArrayList<Integer>();
        if (!BotCombatHelper.isFreeToPlayWorld() && memberItemIds != null) {
            int n = memberItemIds.length;
            int n2 = 0;
            while (n2 < n) {
                candidateItemIds.add(memberItemIds[n2]);
                ++n2;
            }
        }
        ArrayList<Integer> equippableItemIds = new ArrayList<Integer>();
        Iterator iterator = candidateItemIds.iterator();
        while (iterator.hasNext()) {
            int itemId = (Integer)iterator.next();
            if (!ItemDefinition.isDefined(itemId) || !player.getEquipmentManager().canEquipItem(itemId)) continue;
            equippableItemIds.add(itemId);
        }
        int[] itemIds = new int[equippableItemIds.size()];
        int n = 0;
        while (n < equippableItemIds.size()) {
            itemIds[n] = (Integer)equippableItemIds.get(n);
            ++n;
        }
        return itemIds;
    }

    static int selectBestBotLoadoutItemId(Player player, int[] nArray, int[] nArray2) {
        return BotCombatHelper.selectBotLoadoutItemId(player, nArray, nArray2, false);
    }

    public static void operateGloryTeleport(Player player) {
        player.setSelectedItemId(1712);
        player.setSelectedItemSlot(2);
        String string = "operate";
        Player player2 = player;
        player.interfaceAction = string;
        GameplayHelper.castSelectedItemTeleport(player, TeleportManager.EDGEVILLE_TELEPORT_POSITION);
    }

    public static void grantBotSpellRunes(Player player, SpellDefinition spellDefinition, int n) {
        if (player.botMode == 4 || BotPlayer.defaultProgressiveBotNames.contains(player.getUsername().toLowerCase())) {
            String string = "CRITICAL BUG, REPORT! BotUtil " + player.getUsername() + " " + player.botMode + " " + player.currentBotTaskIndex + " " + player.currentBotTaskTypeId + " " + player.currentBotTask;
            System.out.println(string);
            GameplayHelper.appendLogLine(string, "errors");
            return;
        }
        ArrayList<ItemStack> grantedRuneCosts = new ArrayList<ItemStack>();
        ItemStack[] itemStackArray = spellDefinition.getRuneCosts();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack runeCost = itemStackArray[n3];
            if (!(runeCost.getId() == 556 && player.getEquipmentManager().getItemIdAtSlot(3) == 1381 || runeCost.getId() == 555 && player.getEquipmentManager().getItemIdAtSlot(3) == 1383 || runeCost.getId() == 557 && player.getEquipmentManager().getItemIdAtSlot(3) == 1385 || runeCost.getId() == 554 && player.getEquipmentManager().getItemIdAtSlot(3) == 1387)) {
                ItemStack grantedRuneCost = new ItemStack(runeCost.getId(), runeCost.getAmount() * n);
                grantedRuneCosts.add(grantedRuneCost);
                player.getInventoryManager().addItem(grantedRuneCost);
            }
            ++n3;
        }
        if (player.currentBotTask != null) {
            ItemStack[] requiredItems;
            if (player.botTaskRequiredItems != null) {
                requiredItems = new ItemStack[player.botTaskRequiredItems.length + grantedRuneCosts.size()];
                n3 = 0;
                while (n3 < player.botTaskRequiredItems.length) {
                    requiredItems[n3] = player.botTaskRequiredItems[n3];
                    ++n3;
                }
                n3 = 0;
                while (n3 < grantedRuneCosts.size()) {
                    ItemStack grantedRuneCost = (ItemStack)grantedRuneCosts.get(n3);
                    requiredItems[player.botTaskRequiredItems.length + n3] = grantedRuneCost;
                    player.getBankContainer().addToTab(new ItemStack(grantedRuneCost.getId(), grantedRuneCost.getAmount() * 10), 0);
                    ++n3;
                }
            } else {
                requiredItems = new ItemStack[grantedRuneCosts.size()];
                n3 = 0;
                while (n3 < grantedRuneCosts.size()) {
                    ItemStack grantedRuneCost = (ItemStack)grantedRuneCosts.get(n3);
                    requiredItems[n3] = grantedRuneCost;
                    player.getBankContainer().addToTab(new ItemStack(grantedRuneCost.getId(), grantedRuneCost.getAmount() * 10), 0);
                    ++n3;
                }
            }
            player.botTaskRequiredItems = requiredItems;
        }
    }

    public static void walkBotTowardPosition(Player player, Position position) {
        Position position2 = player.getPosition();
        int n = position2.getX();
        int n2 = position2.getY();
        int n3 = position.getX();
        int n4 = position.getY();
        Vector2f vector2f = new Vector2f(n3 - n, n4 - n2);
        vector2f.normalize();
        int n5 = GameUtil.getDistance(position2, position);
        n5 = n5 > 20 ? 20 : n5;
        int n6 = (int)((float)n5 * vector2f.getX());
        n4 = (int)((float)n5 * vector2f.getY());
        n6 = n + n6 - 1 + GameUtil.randomInclusive(2);
        n4 = n2 + n4 - 1 + GameUtil.randomInclusive(2);
        boolean bl = false;
        if (WalkingCollisionMap.getTileFlags(n6, n4, 0) != 0) {
            bl = true;
        }
        while (bl && n5 > 1) {
            n6 = (int)((float)(--n5) * vector2f.getX());
            n4 = (int)((float)n5 * vector2f.getY());
            bl = WalkingCollisionMap.getTileFlags(n6 = n + n6 - 1 + GameUtil.randomInclusive(2), n4 = n2 + n4 - 1 + GameUtil.randomInclusive(2), 0) != 0;
        }
        if (!player.isMovementLocked()) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n6, n4, true, 0, 0);
            player.getMovementQueue().clearMovementActions();
        }
    }

    public static int getEscapeCombatLevelMargin(Player player) {
        boolean bl = player.botCombatStyle == 4 || player.botCombatStyle == 6 || player.botCombatStyle == 5;
        int n = 20;
        if (player.getCombatLevel() < 20 || !bl) {
            n = 5;
            if (player.getCombatLevel() >= 20) {
                n = 15;
            }
            if (player.currentGroup != null) {
                n *= player.currentGroup.members.size();
            }
        }
        return n;
    }

    public static boolean hasExternalCombatTarget(Player player) {
        Player player2;
        if (player.getCombatTarget() == null) {
            return false;
        }
        if (player.getMovementTarget() == null) {
            return false;
        }
        return player.currentGroup == null || !player.getMovementTarget().isPlayer() || !player.currentGroup.containsMember(player2 = (Player)player.getMovementTarget());
    }

    public static boolean drinkAntipoisonPotion(Player player) {
        if (!player.botAntipoisonAvailable) {
            return false;
        }
        int n = 0;
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemStack = itemStackArray[n3];
            ++n;
            if (itemStack != null && player.getPotionHandler().selectPotionForItemId(itemStack.getId()) && PotionHandler.definitions[player.getPotionHandler().selectedDefinitionIndex].isAntipoison()) {
                player.getPotionHandler().drinkPotion(itemStack.getId(), n - 1);
                return true;
            }
            ++n3;
        }
        player.botAntipoisonAvailable = false;
        return false;
    }

    public static void dropInventoryItem(Player player, ItemStack itemStack) {
        if (player.getInventoryManager().getContainer().containsItem(itemStack.getId())) {
            Player player2 = player;
            player2.packetSender.sendSoundEffect(376, 1, 0);
            if (!ServerSettings.adminInteractionsAllowed && player.getPlayerRights() >= 2) {
                player2 = player;
                player2.packetSender.sendGameMessage("Your item disappears because you're an administrator.");
            } else {
                GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(itemStack.getId(), itemStack.getAmount()), player));
            }
            if (!player.getInventoryManager().removeItemFromSlot(itemStack, player.getSelectedItemSlot())) {
                player.getInventoryManager().removeItem(itemStack);
            }
        }
        player.getEquipmentManager().refreshCarriedValue();
    }

    public static boolean tryHandleBotPvpTeamGrouping(Player player, Player player2) {
        if (!player.isInMultiCombatArea() || !player2.isInMultiCombatArea()) {
            return false;
        }
        if (player.getPlayerRights() >= 2 || player2.getPlayerRights() >= 2) {
            return false;
        }
        if (!player.botEnabled || !player2.botEnabled) {
            return false;
        }
        if (player.clanWarsBot || player2.clanWarsBot) {
            return false;
        }
        if (player.getCombatLevel() < 10 || player2.getCombatLevel() < 10) {
            return false;
        }
        if (!player.getSingleCombatTimer().hasElapsed()) {
            return false;
        }
        if (player.botCombatEscapeActive || player2.botCombatEscapeActive) {
            return false;
        }
        if (player.currentGroup != null) {
            if (player2.currentGroup != null) {
                return false;
            }
            if (!player.currentGroup.isFull()) {
                Player player3 = player.currentGroup.leader;
                if (!player3.botEnabled) {
                    return false;
                }
                if (Math.abs(player3.getCombatLevel() - player2.getCombatLevel()) > 2) {
                    return false;
                }
                player.currentGroup.addMember(player2);
                player.currentGroup.refreshGroupFollowChain();
            }
            return true;
        }
        if (player2.currentGroup != null) {
            if (player.currentGroup != null) {
                return false;
            }
            if (!player2.currentGroup.isFull()) {
                Player player4 = player2.currentGroup.leader;
                if (!player4.isBot) {
                    return false;
                }
                if (Math.abs(player4.getCombatLevel() - player.getCombatLevel()) > 2) {
                    return false;
                }
                player2.currentGroup.addMember(player);
                player2.currentGroup.refreshGroupFollowChain();
            }
            return true;
        }
        if (Math.abs(player.getCombatLevel() - player2.getCombatLevel()) <= 2) {
            PlayerGroup playerGroup = new PlayerGroup(player, player2);
            playerGroup.refreshGroupFollowChain();
            return true;
        }
        return true;
    }

    public static boolean canTeamWithBotPvpPlayer(Player player, Player player2, boolean bl) {
        int n = 2;
        if (!bl) {
            n = 6;
        }
        if (!player.getSingleCombatTimer().hasElapsed()) {
            return false;
        }
        if (player.botCombatEscapeActive) {
            return false;
        }
        if (player.currentGroup != null) {
            return false;
        }
        if (player2.currentGroup != null && !player2.currentGroup.isFull()) {
            player2 = player2.currentGroup.leader;
            return Math.abs(player2.getCombatLevel() - player.getCombatLevel()) <= n;
        }
        return Math.abs(player.getCombatLevel() - player2.getCombatLevel()) <= n;
    }

    public static boolean isFreeToPlayWorld() {
        return ServerSettings.freeToPlayWorld;
    }
}

