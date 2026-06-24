/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.shop;

import com.rs2.ServerSettings;
import com.rs2.model.World;
import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemContainerType;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.InventoryManager;
import com.rs2.model.player.Player;
import com.rs2.model.shop.ShopCurrency;
import com.rs2.model.shop.ShopDefinition;
import com.rs2.model.shop.ShopRestockTask;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import com.rs2.util.GameUtil;
import com.rs2.util.GameplayTrace;
import java.util.ArrayList;
import java.util.List;

public final class ShopManager {
    private static List shopDefinitions = new ArrayList(40);

    public static void refreshShopForPlayers(int n) {
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(n);
        Player[] playerArray = World.getPlayers();
        int n2 = playerArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null && player.getCurrentShopId() == n) {
                ItemStack[] itemStackArray = shopDefinition.getStock().getRawItems();
                player.packetSender.sendItemContainer(3900, itemStackArray);
            }
            ++n3;
        }
    }

    public static void openShop(Player player, int n) {
        Player player2;
        if (n >= shopDefinitions.toArray().length) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop open invalid-id player=" + GameplayTrace.describe(player) + " shopId=" + n + " loaded=" + shopDefinitions.size());
            }
            return;
        }
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(n);
        if (shopDefinition.isMembersOnly()) {
            if (player.isMember()) {
                if (ServerSettings.freeToPlayWorld) {
                    if (GameplayTrace.enabled()) {
                        GameplayTrace.log("shop open blocked-free-world player=" + GameplayTrace.describe(player) + " shopId=" + n + " name=" + shopDefinition.getName());
                    }
                    player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    return;
                }
            } else {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("shop open blocked-nonmember player=" + GameplayTrace.describe(player) + " shopId=" + n + " name=" + shopDefinition.getName());
                }
                player.packetSender.sendGameMessage("You need a members account to access members content.");
                return;
            }
        }
        String string = shopDefinition.getName();
        if (shopDefinition.getCurrency() == ShopCurrency.DONATOR_POINTS) {
            string = "<img=2>" + string + "<img=2>";
            player2 = player;
            player2.packetSender.sendGameMessage("You have " + player.getDonatorPoints() + " Donator points.");
        }
        ItemStack[] itemStackArray = shopDefinition.getStock().getRawItems();
        player.getInventoryManager().sendToInterface(3823);
        player2 = player;
        player2.packetSender.sendItemContainer(3900, itemStackArray);
        player2 = player;
        player2.packetSender.sendInterfaceText(string, 3901);
        player2 = player;
        player2.packetSender.showInterfaceWithInventory(3824, 3822);
        player.setCurrentShopId(n);
        player.getAttributes().put("isShopping", Boolean.TRUE);
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("shop open success player=" + GameplayTrace.describe(player) + " shopId=" + n + " name=" + string + " stockSlots=" + itemStackArray.length + " currency=" + shopDefinition.getCurrency());
        }
    }

    private static boolean isSkillcapeBundleItem(int n) {
        return n >= 7992 && n <= 7997 || n >= 8001 && n <= 8012 || n >= 8016 && n <= 8065;
    }

    private static boolean requiresSecondaryCurrencyItem(int n) {
        return n == 7958 || n == 7972 || n == 7974 || n == 7976 || n == 7982 || n == 7984 || n == 7986 || n == 8068 || n == 8076 || n == 8078;
    }

    public static void buyItemStack(Player player, ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(player.getCurrentShopId());
        if (shopDefinition == null) {
            return;
        }
        int n = shopDefinition.getStock().indexOfItem(itemStack.getId());
        if (n == -1) {
            return;
        }
        ShopManager.buyItem(player, n, itemStack.getId(), itemStack.getAmount());
    }

    public static void buyItem(Player player, int n, int n2, int n3) {
        int n4;
        int n5;
        int n6;
        Object object;
        int n7;
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(player.getCurrentShopId());
        ItemContainer itemContainer = player.getInventoryManager().getContainer();
        ItemStack itemStack = shopDefinition.getStock().getItemAt(n);
        int traceRequestedAmount = n3;
        int traceStockItemId = itemStack != null ? itemStack.getId() : -1;
        int traceStockSlotAmountBefore = itemStack != null ? itemStack.getAmount() : -1;
        if (shopDefinition.getCurrency() == ShopCurrency.ITEM_CURRENCY) {
            n6 = shopDefinition.getCurrencyItemId();
        } else {
            ShopDefinition shopDefinition2 = shopDefinition;
            object = player;
            switch (shopDefinition2.getCurrency()) {
                case DONATOR_POINTS: {
                    n6 = ((Player)object).getDonatorPoints();
                    break;
                }
                default: {
                    n6 = -1;
                }
            }
        }
        int traceCurrencyBefore = shopDefinition.getCurrency() == ShopCurrency.ITEM_CURRENCY ? itemContainer.getItemAmount(n6) : -1;
        int traceInventoryBefore = itemContainer.getItemAmount(n2);
        int traceStockBefore = itemStack != null ? shopDefinition.getStock().getItemAmount(n2) : -1;
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("shop buy request player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " slot=" + n + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " stockSlotItemId=" + traceStockItemId + " stockSlotAmountBefore=" + traceStockSlotAmountBefore + " stockItemAmountBefore=" + traceStockBefore + " currency=" + shopDefinition.getCurrency() + " currencyRef=" + n6 + " currencyBefore=" + traceCurrencyBefore + " inventoryItemBefore=" + traceInventoryBefore);
        }
        if (n3 <= 0 || n2 < 0 || itemStack == null || !itemStack.isValid()) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop buy rejected-invalid player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " slot=" + n + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " stockSlotItemId=" + traceStockItemId);
            }
            if (player.botEnabled) {
                player.deferredBotTask = null;
                player.botTaskReturnToBankRequested = true;
                player.currentBotTask.startWalkToBank(player);
            }
            return;
        }
        if (n2 != itemStack.getId()) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop buy rejected-item-mismatch player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " slot=" + n + " requestedItemId=" + n2 + " stockItemId=" + itemStack.getId());
            }
            return;
        }
        boolean bl = false;
        if (ShopManager.requiresSecondaryCurrencyItem(itemStack.getId())) {
            bl = true;
        }
        if (shopDefinition.getCurrency() == ShopCurrency.ITEM_CURRENCY) {
            ItemService.getInstance();
            n5 = ItemService.getPrice(n2, "buyfromshop", n6);
        } else {
            ItemService.getInstance();
            n5 = ItemService.getPrice(n2, "donator", n6);
            if (n6 < n5 * n3) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("shop buy rejected-donator-points player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " points=" + n6 + " unitPrice=" + n5);
                }
                object = player;
                ((Player)object).packetSender.sendGameMessage("You do not have enough donator points to buy this item.");
                return;
            }
            if (bl) {
                ItemDefinition itemDefinition = ItemDefinition.forId(itemStack.getId());
                n4 = player.getInventoryManager().getItemAmount(itemDefinition.b);
                if (n4 < n3) {
                    if (GameplayTrace.enabled()) {
                        GameplayTrace.log("shop buy rejected-secondary-currency player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " secondaryItemId=" + itemDefinition.b + " secondaryAmount=" + n4);
                    }
                    object = player;
                    PacketSender packetSender = ((Player)object).packetSender;
                    StringBuilder stringBuilder = new StringBuilder("You do not have enough ");
                    ItemService.getInstance();
                    packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(itemDefinition.b)).append("(s) to buy this item.").toString());
                    return;
                }
            }
        }
        if (shopDefinition.getStock().getItemAt(n).getAmount() < n3) {
            n3 = shopDefinition.getStock().getItemAt(n).getAmount();
        }
        int n8 = itemContainer.getFreeSlots();
        int n9 = n5;
        int n10 = n6;
        object = player.getInventoryManager();
        if (((InventoryManager)object).containsItem(n10) && ((InventoryManager)object).getContainer().getItemAmount(n10) == n9) {
            ++n8;
        }
        int n11 = n4 = !itemStack.getDefinition().isStackable() ? n3 : 1;
        if (ShopManager.isSkillcapeBundleItem(itemStack.getId())) {
            n4 = 2 * n3;
        }
        if (!(itemStack.getDefinition().isStackable() && player.getInventoryManager().containsItem(n2) || n8 >= n4)) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop buy adjusted-no-space player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " adjustedAmount=" + n8 + " freeSlots=" + itemContainer.getFreeSlots() + " requiredSlots=" + n4);
            }
            n3 = n8;
            object = player;
            ((Player)object).packetSender.sendGameMessage("Not enough space in your inventory.");
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            if (ShopManager.isSkillcapeBundleItem(itemStack.getId())) {
                return;
            }
        }
        if (shopDefinition.getStock().getItemAt(n).getAmount() < n3) {
            n3 = shopDefinition.getStock().getItemAt(n).getAmount();
        }
        if (shopDefinition.isGeneralStore() && shopDefinition.getStock().getItemAt(n).getAmount() == 0) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop buy rejected-out-of-stock player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount);
            }
            object = player;
            ((Player)object).packetSender.sendGameMessage("This item is out of stock.");
            return;
        }
        n = 0;
        if (shopDefinition.getOriginalStock().findFlatItem(n2) != null) {
            n = shopDefinition.getOriginalStock().findFlatItem(n2).getAmount();
        }
        n8 = shopDefinition.getStock().getItemAmount(n2);
        n4 = 0;
        if (player.gameMode != 0 && n8 > n) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop buy rejected-overstock-gamemode player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " stockAmount=" + n8 + " originalAmount=" + n + " gameMode=" + player.gameMode);
            }
            object = player;
            ((Player)object).packetSender.sendGameMessage("You are not playing on normal gamemode and cannot buy overstocked items.");
            return;
        }
        n8 = 0;
        while (n8 < n3) {
            if (shopDefinition.getCurrency() == ShopCurrency.ITEM_CURRENCY) {
                int n12 = itemContainer.getItemAmount(n6);
                n = ShopManager.calculateBuyPrice(shopDefinition, n2, n8);
                if (n > n12) {
                    if (GameplayTrace.enabled()) {
                        GameplayTrace.log("shop buy rejected-currency player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " purchasedBeforeFailure=" + n4 + " unitPrice=" + n + " currencyHeld=" + n12 + " currencyItemId=" + n6);
                    }
                    Player player2 = player;
                    PacketSender packetSender = player2.packetSender;
                    StringBuilder stringBuilder = new StringBuilder("You do not have enough ");
                    ItemService.getInstance();
                    packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n6)).append(" to buy this item.").toString());
                    n3 = n4;
                    if (!player.botEnabled) break;
                    player.currentBotTask.startWalkToBank(player);
                    break;
                }
                player.getInventoryManager().removeItem(new ItemStack(n6, n));
                n4 = n8 + 1;
                if (n6 == 995) {
                    player.boughtItemsValue += n5;
                }
            } else {
                if (bl) {
                    ItemDefinition itemDefinition = ItemDefinition.forId(itemStack.getId());
                    player.getInventoryManager().removeItem(new ItemStack(itemDefinition.b, 1));
                }
                n9 = n5;
                ShopDefinition shopDefinition3 = shopDefinition;
                Player player3 = player;
                switch (shopDefinition3.getCurrency()) {
                    case DONATOR_POINTS: {
                        player3.subtractDonatorPoints(n9);
                    }
                }
                if (shopDefinition.getCurrency() == ShopCurrency.DONATOR_POINTS) {
                    player3 = player;
                    player3.packetSender.sendGameMessage("You have " + player.getDonatorPoints() + " Donator points remaining.");
                }
            }
            ++n8;
        }
        n8 = ShopManager.indexOfStockItem(shopDefinition, itemStack.getId());
        if (shopDefinition.getOriginalStock().containsItem(itemStack.getId())) {
            shopDefinition.getStock().removeKeepingPlaceholder(new ItemStack(itemStack.getId(), n3));
        } else {
            shopDefinition.getStock().remove(new ItemStack(itemStack.getId(), n3));
        }
        ShopManager.updateRestockSchedule(shopDefinition, itemStack.getId(), n8);
        if (!ShopManager.isSkillcapeBundleItem(n2)) {
            ItemStack itemStack2 = new ItemStack(n2, n3);
            player.getInventoryManager().addItem(itemStack2);
            if (player.botEnabled) {
                player.botTaskReturnToBankRequested = true;
                player.currentBotTask.startWalkToBank(player);
            }
            if (shopDefinition.getCurrency() == ShopCurrency.DONATOR_POINTS) {
                GameUtil.addTrackedRareItemAmount(itemStack2);
            }
        } else {
            player.getInventoryManager().addItem(new ItemStack(player.getMaxedSkillCount() >= 2 && n2 != 8058 ? n2 + 1 : n2, n3));
            player.getInventoryManager().addItem(new ItemStack(n2 != 8058 ? n2 + 2 : n2 + 1, n3));
        }
        player.getInventoryManager().sendToInterface(3823);
        ShopManager.refreshShopForPlayers(player.getCurrentShopId());
        if (GameplayTrace.enabled()) {
            int traceCurrencyAfter = shopDefinition.getCurrency() == ShopCurrency.ITEM_CURRENCY ? itemContainer.getItemAmount(n6) : -1;
            String traceResult = n3 > 0 ? "shop buy success" : "shop buy completed-none";
            GameplayTrace.log(traceResult + " player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " stockSlot=" + n8 + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount + " purchasedAmount=" + n3 + " currency=" + shopDefinition.getCurrency() + " currencyRef=" + n6 + " currencyBefore=" + traceCurrencyBefore + " currencyAfter=" + traceCurrencyAfter + " inventoryItemBefore=" + traceInventoryBefore + " inventoryItemAfter=" + itemContainer.getItemAmount(n2) + " stockItemAmountBefore=" + traceStockBefore + " stockItemAmountAfter=" + shopDefinition.getStock().getItemAmount(n2));
        }
    }

    private static int calculateBuyPrice(ShopDefinition shopDefinition, int n, int n2) {
        int n3 = new ItemStack(n).getDefinition().getShopValue();
        int n4 = 0;
        if (shopDefinition.getOriginalStock().findFlatItem(n) != null) {
            n4 = shopDefinition.getOriginalStock().findFlatItem(n).getAmount();
        }
        n = shopDefinition.getStock().getItemAmount(n) - n2;
        n -= n4;
        if (!ServerSettings.dynamicShopPricesEnabled) {
            n = 0;
        }
        n2 = n3 * 30;
        int n5 = (int)((double)n3 * ((double)shopDefinition.getBuyPricePercent() - shopDefinition.getPriceChangeRate() * (double)n));
        n5 /= 100;
        n5 = Math.max(n5, n2 /= 100);
        return n5;
    }

    private static int calculateSellPrice(ShopDefinition shopDefinition, int n, int n2) {
        int n3 = new ItemStack(n).getDefinition().getShopValue();
        int n4 = 0;
        if (shopDefinition.getOriginalStock().findFlatItem(n) != null) {
            n4 = shopDefinition.getOriginalStock().findFlatItem(n).getAmount();
        }
        n = shopDefinition.getStock().getItemAmount(n) + n2;
        n -= n4;
        if (!ServerSettings.dynamicShopPricesEnabled) {
            n = 0;
        }
        int n5 = (int)(((double)shopDefinition.getSellPricePercent() - shopDefinition.getPriceChangeRate() * (double)Math.min(n, 10)) * (double)n3);
        n = n3 / 10;
        n5 = (n5 /= 100) < n ? n : n5;
        return n5;
    }

    public static void sellItem(Player player, int n, int n2, int n3) {
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(player.getCurrentShopId());
        ItemContainer itemContainer = player.getInventoryManager().getContainer();
        ItemStack itemStack = itemContainer.getItemAt(n);
        int n4 = shopDefinition.getCurrencyItemId();
        int traceRequestedAmount = n3;
        int traceCurrencyBefore = itemContainer.getItemAmount(n4);
        int traceInventoryBefore = itemContainer.getItemAmount(n2);
        if (itemStack == null) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-empty-slot player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " slot=" + n + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount);
            }
            return;
        }
        int n5 = n2;
        if (itemStack.getDefinition().isNote()) {
            n5 = itemStack.getDefinition().getUnnotedId();
        }
        int n6 = shopDefinition.getStock().getItemAmount(n5);
        int traceStockBefore = n6;
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("shop sell request player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " slot=" + n + " itemId=" + n2 + " stockItemId=" + n5 + " requestedAmount=" + traceRequestedAmount + " inventoryItemBefore=" + traceInventoryBefore + " stockItemAmountBefore=" + traceStockBefore + " currencyItemId=" + n4 + " currencyBefore=" + traceCurrencyBefore);
        }
        if (!ServerSettings.adminInteractionsAllowed && player.getPlayerRights() >= 2) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-admin player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2);
            }
            Player player2 = player;
            player2.packetSender.sendGameMessage("This action is not allowed.");
            return;
        }
        if (itemStack.getId() != n2 || !itemStack.isValid()) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-item-mismatch player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " slot=" + n + " requestedItemId=" + n2 + " slotItemId=" + itemStack.getId());
            }
            return;
        }
        if (shopDefinition.getCurrency() != ShopCurrency.ITEM_CURRENCY) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-non-item-currency player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " currency=" + shopDefinition.getCurrency());
            }
            Player player3 = player;
            player3.packetSender.sendGameMessage("This shop can't buy anything.");
            return;
        }
        if (shopDefinition.getStock().getFreeSlots() <= 0 && n6 <= 0) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-shop-full player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " stockItemId=" + n5);
            }
            Player player4 = player;
            player4.packetSender.sendGameMessage("The shop is currently full!");
            return;
        }
        if (n2 == 995) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-currency-item player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2);
            }
            Player player5 = player;
            player5.packetSender.sendGameMessage("You cannot sell coins to the shop.");
            return;
        }
        if (!shopDefinition.isGeneralStore() && !shopDefinition.getStock().containsItem(n5) || itemStack.getDefinition().isUntradeable()) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-unsellable player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " stockItemId=" + n5 + " generalStore=" + shopDefinition.isGeneralStore() + " untradeable=" + itemStack.getDefinition().isUntradeable());
            }
            Player player6 = player;
            player6.packetSender.sendGameMessage("You cannot sell this item in this shop.");
            return;
        }
        int n7 = itemContainer.getItemAmount(n2);
        if (n3 <= 0 || n2 < 0) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-invalid player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " requestedAmount=" + traceRequestedAmount);
            }
            return;
        }
        if (n2 > 11883) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-unsupported-item player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2);
            }
            Player player7 = player;
            player7.packetSender.sendGameMessage("This item is not supported yet.");
            return;
        }
        if (!itemContainer.containsItem(n2)) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-not-in-inventory player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2);
            }
            return;
        }
        if (n3 >= n7) {
            n3 = n7;
        }
        n7 = 0;
        if (shopDefinition.getOriginalStock().findFlatItem(n5) != null) {
            n7 = shopDefinition.getOriginalStock().findFlatItem(n5).getAmount();
        }
        if (player.gameMode != 0 && n6 < n7) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("shop sell rejected-understock-gamemode player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n2 + " stockItemId=" + n5 + " stockAmount=" + n6 + " originalAmount=" + n7 + " gameMode=" + player.gameMode);
            }
            Player player8 = player;
            player8.packetSender.sendGameMessage("You are not playing on normal gamemode and cannot sell understocked items.");
            return;
        }
        player.getInventoryManager().removeItem(new ItemStack(n2, n3));
        double d = 0.0;
        n7 = 0;
        while (n7 < n3) {
            d += (double)ShopManager.calculateSellPrice(shopDefinition, n2, n7);
            ++n7;
        }
        if (!player.botEnabled || player.currentBotTask != null) {
            player.getInventoryManager().addItem(new ItemStack(n4, (int)d));
        }
        if (n4 == 995) {
            player.soldItemsValue = (int)((double)player.soldItemsValue + d);
        }
        n7 = ShopManager.indexOfStockItem(shopDefinition, n5);
        if (shopDefinition.isGeneralStore() && n6 <= 0) {
            ItemStack itemStack2 = new ItemStack(n5, n3);
            ItemContainer itemContainer2 = shopDefinition.getStock();
            itemContainer2.add(itemStack2, -1);
        } else {
            shopDefinition.getStock().setItem(shopDefinition.getStock().indexOfItem(n5), new ItemStack(n5, n6 + n3));
        }
        ShopManager.updateRestockSchedule(shopDefinition, n5, n7);
        if (player.botShopSellItemIds.contains(n5)) {
            n7 = player.botShopSellItemIds.indexOf(n5);
            player.botShopSellItemIds.remove(n7);
        }
        if (player.botEnabled && player.currentBotTask != null && player.botShopSellItemIds.size() == 0) {
            player.botTaskReturnToBankRequested = true;
            player.currentBotTask.startWalkToBank(player);
        }
        player.getInventoryManager().sendToInterface(3823);
        ShopManager.refreshShopForPlayers(player.getCurrentShopId());
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("shop sell success player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " stockSlot=" + n7 + " itemId=" + n2 + " stockItemId=" + n5 + " requestedAmount=" + traceRequestedAmount + " soldAmount=" + n3 + " currencyItemId=" + n4 + " currencyBefore=" + traceCurrencyBefore + " currencyAfter=" + itemContainer.getItemAmount(n4) + " currencyAdded=" + (int)d + " inventoryItemBefore=" + traceInventoryBefore + " inventoryItemAfter=" + itemContainer.getItemAmount(n2) + " stockItemAmountBefore=" + traceStockBefore + " stockItemAmountAfter=" + shopDefinition.getStock().getItemAmount(n5));
        }
    }

    public static void sellItemStack(Player player, ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }
        ShopManager.sellItem(player, player.getInventoryManager().getContainer().indexOfItem(itemStack.getId()), itemStack.getId(), itemStack.getAmount());
    }

    public static void sendBuyPrice(Player player, int n) {
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(player.getCurrentShopId());
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("shop buy-price request player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n + " currency=" + shopDefinition.getCurrency() + " currencyItemId=" + shopDefinition.getCurrencyItemId());
        }
        if (shopDefinition.getCurrency() == ShopCurrency.ITEM_CURRENCY) {
            int n2 = n;
            ItemService.getInstance();
            String string = ItemService.getItemName(shopDefinition.getCurrencyItemId());
            int n3 = ShopManager.calculateBuyPrice(shopDefinition, n2, 0);
            PacketSender packetSender = player.packetSender;
            StringBuilder stringBuilder = new StringBuilder();
            ItemService.getInstance();
            packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n)).append(": currently costs ").append(GameUtil.formatNumber((double)n3)).append(" ").append(string).append(".").toString());
            return;
        }
        ItemService.getInstance();
        int n4 = ItemService.getPrice(n, "donator", shopDefinition.getCurrencyItemId());
        boolean bl = false;
        if (ShopManager.requiresSecondaryCurrencyItem(n)) {
            bl = true;
        }
        if (!bl) {
            PacketSender packetSender = player.packetSender;
            StringBuilder stringBuilder = new StringBuilder();
            ItemService.getInstance();
            packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n)).append(": currently costs ").append(n4).append(" ").append(ShopManager.getCurrencyDisplayName(shopDefinition)).append(".").toString());
            return;
        }
        ItemDefinition itemDefinition = ItemDefinition.forId(n);
        itemDefinition = ItemDefinition.forId(itemDefinition.b);
        PacketSender packetSender = player.packetSender;
        StringBuilder stringBuilder = new StringBuilder();
        ItemService.getInstance();
        packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n)).append(": currently costs ").append(n4).append(" ").append(ShopManager.getCurrencyDisplayName(shopDefinition)).append(" + ").append(itemDefinition.getName()).append(".").toString());
    }

    public static void sendSellPrice(Player player, int n) {
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(player.getCurrentShopId());
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("shop sell-price request player=" + GameplayTrace.describe(player) + " shopId=" + player.getCurrentShopId() + " itemId=" + n + " currency=" + shopDefinition.getCurrency() + " currencyItemId=" + shopDefinition.getCurrencyItemId());
        }
        Object object = ItemDefinition.forId(n);
        shopDefinition.getCurrencyItemId();
        int n2 = n;
        if (((ItemDefinition)object).isNote()) {
            n2 = ((ItemDefinition)object).getUnnotedId();
        }
        if (shopDefinition.getCurrency() != ShopCurrency.ITEM_CURRENCY) {
            player.packetSender.sendGameMessage("This shop can't buy anything.");
            return;
        }
        if (n == 995) {
            player.packetSender.sendGameMessage("You cannot sell coins to the shop.");
            return;
        }
        if (!shopDefinition.isGeneralStore() && !shopDefinition.getStock().containsItem(n2) || ((ItemDefinition)object).isUntradeable()) {
            player.packetSender.sendGameMessage("You cannot sell this item in this shop.");
            return;
        }
        if (shopDefinition.getCurrency() == ShopCurrency.ITEM_CURRENCY || shopDefinition.isGeneralStore()) {
            object = shopDefinition;
            int n3 = ShopManager.calculateSellPrice((ShopDefinition)object, n2, 0);
            PacketSender packetSender = player.packetSender;
            StringBuilder stringBuilder = new StringBuilder();
            ItemService.getInstance();
            packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n)).append(": shop will buy for ").append(GameUtil.formatNumber((double)n3)).append(" ").append(ShopManager.getCurrencyDisplayName(shopDefinition)).append(".").toString());
            return;
        }
        player.packetSender.sendGameMessage("You cannot sell this item to this shop.");
    }

    private static String getCurrencyDisplayName(ShopDefinition shopDefinition) {
        switch (shopDefinition.getCurrency()) {
            case DONATOR_POINTS: {
                return "Donator Points";
            }
        }
        ItemService.getInstance();
        return ItemService.getItemName(shopDefinition.getCurrencyItemId());
    }

    public static void loadShops() {
        try {
            byte[] byArray = FileUtil.readBytes("./data/content/Shops.dat");
            ByteArrayReader byteArrayReader = new ByteArrayReader(byArray);
            int n = byteArrayReader.readUnsignedShort();
            int n2 = 0;
            while (n2 < n) {
                ShopDefinition shopDefinition = new ShopDefinition();
                String string = byteArrayReader.readString();
                boolean bl = byteArrayReader.readUnsignedByte() == 1;
                int n3 = byteArrayReader.readUnsignedByte();
                int n4 = byteArrayReader.readUnsignedByte();
                int n5 = byteArrayReader.readUnsignedByte();
                int n6 = byteArrayReader.readUnsignedByte();
                int n7 = byteArrayReader.readUnsignedByte();
                ItemContainer itemContainer = new ItemContainer(ItemContainerType.b, 40);
                ItemContainer itemContainer2 = new ItemContainer(ItemContainerType.b, 40);
                ShopDefinition.setRestockDelayTicks(shopDefinition, new int[40]);
                ShopDefinition.setRestockTasks(shopDefinition, new TickTask[40]);
                int n8 = 0;
                while (n8 < 40) {
                    ShopDefinition.getRestockDelayTicks((ShopDefinition)shopDefinition)[n8] = 100;
                    ++n8;
                }
                n8 = 0;
                while (n8 < n7) {
                    int n9 = byteArrayReader.readUnsignedShort();
                    int n10 = byteArrayReader.readUnsignedShort();
                    int n11 = byteArrayReader.readUnsignedShort();
                    n10 = (int)((double)n10 * ServerSettings.shopItemMultiplier);
                    if (!(ServerSettings.freeToPlayWorld && n2 != 169 && new ItemStack(n9).getDefinition().isMembersOnly() || !ItemDefinition.isDefined(n9))) {
                        ShopDefinition.getRestockDelayTicks((ShopDefinition)shopDefinition)[n8] = n11;
                        ItemStack itemStack = new ItemStack(n9, n10);
                        ItemContainer itemContainer3 = itemContainer;
                        itemContainer3.add(itemStack, -1);
                        itemStack = new ItemStack(n9, n10);
                        itemContainer3 = itemContainer2;
                        itemContainer3.add(itemStack, -1);
                    }
                    ++n8;
                }
                shopDefinition.setGeneralStore(n3 == 0);
                shopDefinition.setOriginalStock(itemContainer);
                shopDefinition.setStock(itemContainer2);
                n8 = byteArrayReader.readUnsignedShort();
                shopDefinition.setName(string);
                shopDefinition.setMembersOnly(bl);
                shopDefinition.setCurrencyItemId(n8);
                shopDefinition.setCurrency(ShopCurrency.values()[n3 == 2 ? 1 : 0]);
                shopDefinition.setBuyPricePercent(n4);
                shopDefinition.setSellPricePercent(n5);
                shopDefinition.setPriceChangeRateTenths(n6);
                ShopDefinition.setShopId(shopDefinition, n2);
                shopDefinitions.add(shopDefinition);
                ++n2;
            }
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    private static void updateRestockSchedule(ShopDefinition shopDefinition, int n, int n2) {
        int n3;
        TickTask tickTask = null;
        if (n2 != -1) {
            tickTask = ShopDefinition.getRestockTasks(shopDefinition)[n2];
        }
        if (shopDefinition.getOriginalStock().containsItem(n)) {
            n3 = shopDefinition.getOriginalStock().findFlatItem(n).getAmount();
            int n4 = shopDefinition.getStock().findFlatItem(n).getAmount();
            if (n4 < n3) {
                ShopManager.startRestockTask(shopDefinition, n);
                return;
            }
            if (n4 > n3) {
                ShopManager.startRestockTask(shopDefinition, n);
                return;
            }
        } else if (shopDefinition.getStock().containsItem(n)) {
            ShopManager.startRestockTask(shopDefinition, n);
            return;
        }
        if (tickTask != null) {
            tickTask.stop();
            n3 = ShopDefinition.getRestockDelayTicks(shopDefinition)[n2];
            tickTask.setIntervalTicks(n3);
            tickTask.setRemainingTicks(n3);
        }
    }

    public static int indexOfStockItem(ShopDefinition shopDefinition, int n) {
        if (shopDefinition.getStock().containsItem(n)) {
            return shopDefinition.getStock().indexOfItem(n);
        }
        return -1;
    }

    private static void startRestockTask(ShopDefinition shopDefinition, int n) {
        int n2 = ShopManager.indexOfStockItem(shopDefinition, n);
        int n3 = ShopDefinition.getRestockDelayTicks(shopDefinition)[n2];
        double d = (double)n3 * ServerSettings.shopRestockTimeMultiplier;
        n3 = (int)d;
        if (ShopDefinition.getRestockTasks(shopDefinition)[n2] != null && ShopDefinition.getRestockTasks(shopDefinition)[n2].isActive()) {
            return;
        }
        ShopDefinition.getRestockTasks((ShopDefinition)shopDefinition)[n2] = new ShopRestockTask(n3, shopDefinition, n, n2);
        World.getTaskScheduler().schedule(ShopDefinition.getRestockTasks(shopDefinition)[n2]);
    }

    public static boolean needsRestock(ShopDefinition shopDefinition, int n, int n2) {
        if (shopDefinition.getOriginalStock().containsItem(n)) {
            int n3 = shopDefinition.getOriginalStock().findFlatItem(n).getAmount();
            int n4 = shopDefinition.getStock().findFlatItem(n).getAmount();
            if (n4 < n3) {
                return true;
            }
            if (n4 > n3) {
                return true;
            }
        } else {
            int n5 = ShopManager.indexOfStockItem(shopDefinition, n);
            if (n5 != n2) {
                return false;
            }
            if (shopDefinition.getStock().containsItem(n)) {
                return true;
            }
        }
        return false;
    }

    public static List getShopDefinitions() {
        return shopDefinitions;
    }
}

