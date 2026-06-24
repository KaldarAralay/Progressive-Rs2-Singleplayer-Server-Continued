/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.ServerSettings;
import com.rs2.model.grandexchange.GrandExchangeOffer;
import com.rs2.model.grandexchange.GrandExchangePriceSample;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;

public final class GrandExchangeManager {
    private static int instantPriceFluctuationPercent = 0;

    public static void openGrandExchange(Player player) {
        if (player.gameMode != 0) {
            player.packetSender.sendGameMessage("You are not playing on normal gamemode and cannot use grand exchange.");
            return;
        }
        Player player2 = player;
        player.selectedGrandExchangeItemId = -1;
        player2.selectedGrandExchangeQuantity = 0;
        player2.selectedGrandExchangeUnitPrice = 0;
        player2.selectedGrandExchangeSlot = 0;
        GrandExchangeManager.refreshOfferSlots(player);
        player.packetSender.showInterface(19018);
    }

    public static void refreshSelectedOfferDetails(Player player) {
        ItemStack secondaryCollectItem;
        ItemStack primaryCollectItem;
        String string;
        double d = player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot];
        double d2 = player.grandExchangeQuantities[player.selectedGrandExchangeSlot];
        double d3 = 100.0 * (d / d2);
        int n = (int)d3;
        boolean bl = n == 100;
        boolean bl2 = player.grandExchangeCancelledFlags[player.selectedGrandExchangeSlot];
        Player player2 = player;
        player2.packetSender.sendInterfaceText("for a total price of " + GameUtil.formatNumber(player.grandExchangeTotalPrices[player.selectedGrandExchangeSlot]) + " coins.", 19002);
        String string2 = bl || bl2 ? "" : "have";
        String string3 = string = bl || bl2 ? "" : " so far";
        if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {
            player2 = player;
            player2.packetSender.sendInterfaceText("You " + string2 + " sold a total of " + GameUtil.formatNumber(player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot]) + string, 19001);
        } else {
            player2 = player;
            player2.packetSender.sendInterfaceText("You " + string2 + " bought a total of " + GameUtil.formatNumber(player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot]) + string, 19001);
        }
        if (bl2) {
            n = 250;
        }
        if (bl2 || bl) {
            player2 = player;
            player2.packetSender.setInterfaceHiddenFlag(1, 19016);
        } else {
            player2 = player;
            player2.packetSender.setInterfaceHiddenFlag(0, 19016);
        }
        player2 = player;
        player2.packetSender.sendInterfaceProgress(19011, n);
        if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {
            primaryCollectItem = new ItemStack(995, player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
            secondaryCollectItem = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);
        } else {
            primaryCollectItem = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
            secondaryCollectItem = new ItemStack(995, player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);
        }
        if (primaryCollectItem.getAmount() <= 0) {
            primaryCollectItem = null;
        }
        if (secondaryCollectItem.getAmount() <= 0) {
            secondaryCollectItem = null;
        }
        ItemStack[] collectItems = new ItemStack[]{primaryCollectItem, secondaryCollectItem};
        player2 = player;
        player2.packetSender.sendItemContainer(19006, collectItems);
    }

    public static void collectOfferItem(Player player, int n, int n2, int n3) {
        ItemStack itemStack;
        int n4;
        int n5;
        double d = player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot];
        double d2 = player.grandExchangeQuantities[player.selectedGrandExchangeSlot];
        double d3 = 100.0 * (d / d2);
        n3 = (int)d3;
        ItemStack itemStack2 = new ItemStack(n2);
        boolean bl = itemStack2.getDefinition().hasNote();
        int n6 = itemStack2.getDefinition().getNotedId();
        n3 = n3 == 100 ? 1 : 0;
        boolean bl2 = player.grandExchangeCancelledFlags[player.selectedGrandExchangeSlot];
        if (n == 0) {
            n5 = player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot] ? 995 : player.grandExchangeItemIds[player.selectedGrandExchangeSlot];
            n4 = player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot];
        } else {
            n5 = player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot] ? player.grandExchangeItemIds[player.selectedGrandExchangeSlot] : 995;
            n4 = player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot];
        }
        if (n2 < 0 || n5 != itemStack2.getId() || !itemStack2.isValid()) {
            return;
        }
        if (n2 > 11883) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This item is not supported yet.");
            return;
        }
        n2 = bl ? n6 : n2;
        ItemStack itemStack3 = new ItemStack(n2, n4);
        if (!player.getInventoryManager().canAddItem(itemStack3)) {
            return;
        }
        if (n == 0) {
            player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot] = 0;
        } else {
            player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot] = 0;
        }
        if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {
            itemStack2 = new ItemStack(995, player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
            itemStack = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);
        } else {
            itemStack2 = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
            itemStack = new ItemStack(995, player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);
        }
        if (itemStack2.getAmount() <= 0) {
            itemStack2 = null;
        }
        if (itemStack.getAmount() <= 0) {
            itemStack = null;
        }
        ItemStack[] itemStackArray = new ItemStack[]{itemStack2, itemStack};
        Player player3 = player;
        player3.packetSender.sendItemContainer(19006, itemStackArray);
        player.getInventoryManager().addItem(itemStack3);
        if (itemStack2 == null && itemStack == null && (n3 != 0 || bl2)) {
            GrandExchangeManager.clearSelectedOfferSlot(player);
        }
    }

    public static void sendOfferCompletionMessage(Player player, int n) {
        String string = new ItemStack(player.grandExchangeItemIds[n], 1).getDefinition().getName();
        String string2 = "buying";
        if (player.grandExchangeSellOfferFlags[n]) {
            string2 = "selling";
        }
        Player player2 = player;
        player2.packetSender.sendGameMessage("Grand Exchange: Finished " + string2 + " " + GameUtil.formatNumber(player.grandExchangeQuantities[n]) + " x " + string + ".");
        player.grandExchangeFinishMessagePending[n] = false;
    }

    private static void clearSelectedOfferSlot(Player player) {
        player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot] = true;
        player.grandExchangeItemIds[player.selectedGrandExchangeSlot] = -1;
        player.grandExchangeQuantities[player.selectedGrandExchangeSlot] = 0;
        player.grandExchangeUnitPrices[player.selectedGrandExchangeSlot] = 0;
        player.grandExchangeCancelledFlags[player.selectedGrandExchangeSlot] = false;
        player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot] = 0;
        player.grandExchangeTotalPrices[player.selectedGrandExchangeSlot] = 0;
        player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot] = 0;
        player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot] = 0;
        GrandExchangeManager.openGrandExchange(player);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean handleButtonClick(Player player, int n) {
        switch (n) {
            case 19024: 
            case 19027: 
            case 19030: 
            case 19033: 
            case 19036: 
            case 19039: {
                player.selectedGrandExchangeSlot = (n - 19024) / 3;
                Player player2 = player;
                player2.packetSender.showInterface(18890);
                return true;
            }
            case 19025: 
            case 19028: 
            case 19031: 
            case 19034: 
            case 19037: 
            case 19040: {
                player.selectedGrandExchangeSlot = (n - 19025) / 3;
                ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getRawItems();
                Player player3 = player;
                player3.packetSender.sendItemContainer(19102, itemStackArray);
                player3 = player;
                player3.packetSender.showInterfaceWithInventory(18939, 19101);
                return true;
            }
            case 18907: 
            case 18956: 
            case 18990: {
                GrandExchangeManager.openGrandExchange(player);
                return true;
            }
            case 19042: 
            case 19051: 
            case 19060: 
            case 19069: 
            case 19078: 
            case 19087: {
                Object object;
                player.selectedGrandExchangeSlot = (n - 19042) / 9;
                double d = player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot];
                double d2 = player.grandExchangeQuantities[player.selectedGrandExchangeSlot];
                double d3 = 100.0 * (d / d2);
                n = (int)d3;
                boolean bl = n == 100;
                boolean bl2 = player.grandExchangeCancelledFlags[player.selectedGrandExchangeSlot];
                player.selectedGrandExchangeItemId = player.grandExchangeItemIds[player.selectedGrandExchangeSlot];
                player.selectedGrandExchangeQuantity = player.grandExchangeQuantities[player.selectedGrandExchangeSlot];
                player.selectedGrandExchangeUnitPrice = player.grandExchangeUnitPrices[player.selectedGrandExchangeSlot];
                Player player4 = player;
                player4.packetSender.sendInterfaceText(GameUtil.formatNumber(GrandExchangeManager.getGuidePrice(player.grandExchangeItemIds[player.selectedGrandExchangeSlot])), 18997);
                player4 = player;
                player4.packetSender.sendInterfaceItemModel(19008, player.grandExchangeItemIds[player.selectedGrandExchangeSlot]);
                player4 = player;
                player4.packetSender.sendInterfaceText(GameUtil.formatNumber(player.selectedGrandExchangeQuantity), 18998);
                player4 = player;
                player4.packetSender.sendInterfaceText(String.valueOf(GameUtil.formatNumber(player.selectedGrandExchangeUnitPrice)) + " coins", 18999);
                player4 = player;
                player4.packetSender.sendInterfaceText(String.valueOf(GameUtil.formatNumber(player.selectedGrandExchangeUnitPrice * player.selectedGrandExchangeQuantity)) + " coins", 19000);
                player4 = player;
                player4.packetSender.sendInterfaceText("for a total price of " + GameUtil.formatNumber(player.grandExchangeTotalPrices[player.selectedGrandExchangeSlot]) + " coins.", 19002);
                Object object2 = bl || bl2 ? "" : "have";
                Object object3 = object = bl || bl2 ? "" : " so far";
                if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {
                    player4 = player;
                    player4.packetSender.setInterfaceHiddenFlag(0, 19012);
                    player4 = player;
                    player4.packetSender.setInterfaceHiddenFlag(1, 19014);
                    player4 = player;
                    player4.packetSender.sendInterfaceText("Sell offer", 18992);
                    player4 = player;
                    player4.packetSender.sendInterfaceText("You " + (String)object2 + " sold a total of " + GameUtil.formatNumber(player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot]) + (String)object, 19001);
                } else {
                    player4 = player;
                    player4.packetSender.setInterfaceHiddenFlag(1, 19012);
                    player4 = player;
                    player4.packetSender.setInterfaceHiddenFlag(0, 19014);
                    player4 = player;
                    player4.packetSender.sendInterfaceText("Buy offer", 18992);
                    player4 = player;
                    player4.packetSender.sendInterfaceText("You " + (String)object2 + " bought a total of " + GameUtil.formatNumber(player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot]) + (String)object, 19001);
                }
                if (bl2) {
                    n = 250;
                }
                if (bl2 || bl) {
                    player4 = player;
                    player4.packetSender.setInterfaceHiddenFlag(1, 19016);
                } else {
                    player4 = player;
                    player4.packetSender.setInterfaceHiddenFlag(0, 19016);
                }
                player4 = player;
                player4.packetSender.sendInterfaceProgress(19011, n);
                if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {
                    object2 = new ItemStack(995, player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
                    object = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);
                } else {
                    object2 = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
                    object = new ItemStack(995, player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);
                }
                if (((ItemStack)object2).getAmount() <= 0) {
                    object2 = null;
                }
                if (((ItemStack)object).getAmount() <= 0) {
                    object = null;
                }
                ItemStack[] itemStackArray = new ItemStack[]{(ItemStack)object2, (ItemStack)object};
                player4 = player;
                player4.packetSender.sendItemContainer(19006, itemStackArray);
                player4 = player;
                player4.packetSender.showInterface(18984);
                if (object2 != null || object != null || !bl && !bl2) return true;
                GrandExchangeManager.clearSelectedOfferSlot(player);
                return true;
            }
            case 18908: 
            case 18957: {
                GrandExchangeManager.adjustSelectedOfferQuantity(player, -1);
                return true;
            }
            case 18898: 
            case 18909: 
            case 18947: 
            case 18958: {
                GrandExchangeManager.adjustSelectedOfferQuantity(player, 1);
                return true;
            }
            case 18899: 
            case 18948: {
                GrandExchangeManager.adjustSelectedOfferQuantity(player, 10);
                return true;
            }
            case 18900: 
            case 18949: {
                GrandExchangeManager.adjustSelectedOfferQuantity(player, 100);
                return true;
            }
            case 18901: {
                GrandExchangeManager.adjustSelectedOfferQuantity(player, 1000);
                return true;
            }
            case 18950: {
                String string = "all";
                if (string.equals("all")) {
                    player.selectedGrandExchangeQuantity = player.getInventoryManager().getContainer().getItemAmount(player.selectedGrandExchangeItemId);
                    ItemStack itemStack = new ItemStack(player.selectedGrandExchangeItemId, 1);
                    boolean bl = itemStack.getDefinition().hasNote();
                    if (bl) {
                        int n2 = itemStack.getDefinition().getNotedId();
                        player.selectedGrandExchangeQuantity += player.getInventoryManager().getContainer().getItemAmount(n2);
                    }
                }
                GrandExchangeManager.refreshSelectedOfferTotals(player);
                return true;
            }
            case 18902: 
            case 18951: {
                Player player5 = player;
                player5.packetSender.sendEnterInputPrompt(18900);
                return true;
            }
            case 18910: 
            case 18959: {
                GrandExchangeManager.adjustSelectedOfferUnitPrice(player, -1);
                return true;
            }
            case 18911: 
            case 18960: {
                GrandExchangeManager.adjustSelectedOfferUnitPrice(player, 1);
                return true;
            }
            case 18903: 
            case 18952: {
                GrandExchangeManager.applySelectedOfferPricePreset(player, "-5%");
                return true;
            }
            case 18906: 
            case 18955: {
                GrandExchangeManager.applySelectedOfferPricePreset(player, "+5%");
                return true;
            }
            case 18904: 
            case 18953: {
                GrandExchangeManager.applySelectedOfferPricePreset(player, "guide");
                return true;
            }
            case 18905: 
            case 18954: {
                if (ServerSettings.instantGrandExchangeEnabled) {
                    Player player6 = player;
                    player6.packetSender.sendGameMessage("Price can't be modified with instant Grand Exchange trades.");
                    return true;
                }
                Player player7 = player;
                player7.packetSender.sendEnterInputPrompt(18901);
                return true;
            }
            case 18896: 
            case 18945: {
                if (player.selectedGrandExchangeItemId < 0 || player.selectedGrandExchangeQuantity < 0 || player.selectedGrandExchangeSlot < 0 || player.selectedGrandExchangeSlot > 5 || player.selectedGrandExchangeUnitPrice * player.selectedGrandExchangeQuantity < 0) return true;
                int n3 = 1;
                if (player.getOpenInterfaceId() == 18890) {
                    n3 = 0;
                }
                if (n3 == 0) {
                    int n4 = player.selectedGrandExchangeUnitPrice * player.selectedGrandExchangeQuantity;
                    if (!player.getInventoryManager().containsItemAmount(995, n4)) {
                        Player player8 = player;
                        player8.packetSender.sendGameMessage("Not enough coins!");
                        return true;
                    }
                    player.getInventoryManager().removeItem(new ItemStack(995, n4));
                } else {
                    int n5 = player.selectedGrandExchangeQuantity;
                    ItemStack itemStack = new ItemStack(player.selectedGrandExchangeItemId, 1);
                    int n6 = 0;
                    int n7 = -1;
                    boolean bl = itemStack.getDefinition().hasNote();
                    if (bl) {
                        n7 = itemStack.getDefinition().getNotedId();
                        n6 = player.getInventoryManager().getContainer().getItemAmount(n7);
                    }
                    if (n6 >= n5) {
                        player.getInventoryManager().removeItem(new ItemStack(n7, n5));
                    } else {
                        if (n6 > 0) {
                            player.getInventoryManager().removeItem(new ItemStack(n7, n6));
                        }
                        player.getInventoryManager().removeItem(new ItemStack(player.selectedGrandExchangeItemId, n5 - n6));
                    }
                }
                player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot] = n3 != 0;
                player.grandExchangeItemIds[player.selectedGrandExchangeSlot] = player.selectedGrandExchangeItemId;
                player.grandExchangeQuantities[player.selectedGrandExchangeSlot] = player.selectedGrandExchangeQuantity;
                player.grandExchangeUnitPrices[player.selectedGrandExchangeSlot] = player.selectedGrandExchangeUnitPrice;
                if (!ServerSettings.instantGrandExchangeEnabled) {
                    new GrandExchangeOffer(player.getUsername(), player.selectedGrandExchangeSlot, n3 != 0, player.selectedGrandExchangeItemId, player.selectedGrandExchangeQuantity, player.selectedGrandExchangeQuantity, player.selectedGrandExchangeUnitPrice);
                } else {
                    int n8 = player.selectedGrandExchangeSlot;
                    Player player9 = player;
                    int n9 = player9.grandExchangeQuantities[n8];
                    n3 = player9.grandExchangeUnitPrices[n8] * player9.grandExchangeQuantities[n8];
                    player9.grandExchangeCompletedQuantities[n8] = n9;
                    player9.grandExchangeTotalPrices[n8] = n3;
                    player9.grandExchangePrimaryCollectAmounts[n8] = !player9.grandExchangeSellOfferFlags[n8] ? player9.grandExchangeQuantities[n8] : n3;
                }
                GrandExchangeManager.openGrandExchange(player);
                return true;
            }
            case 19017: {
                ItemStack[] itemStackArray;
                int n10 = 100 * (player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot] / player.grandExchangeQuantities[player.selectedGrandExchangeSlot]);
                boolean bl = n10 == 100;
                boolean bl3 = player.grandExchangeCancelledFlags[player.selectedGrandExchangeSlot];
                if (bl3 || bl) return true;
                GrandExchangeOffer.cancelOffer(player, player.selectedGrandExchangeSlot);
                player.grandExchangeCancelledFlags[player.selectedGrandExchangeSlot] = true;
                Player player10 = player;
                player10.packetSender.setInterfaceHiddenFlag(1, 19016);
                if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {
                    ItemStack itemStack = new ItemStack(995, player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
                    ItemStack itemStack2 = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangeQuantities[player.selectedGrandExchangeSlot] - player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot]);
                    player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot] = itemStack2.getAmount();
                    if (itemStack.getAmount() <= 0) {
                        itemStack = null;
                    }
                    if (itemStack2.getAmount() <= 0) {
                        itemStack2 = null;
                    }
                    itemStackArray = new ItemStack[]{itemStack, itemStack2};
                } else {
                    ItemStack itemStack = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);
                    ItemStack itemStack3 = new ItemStack(995, (player.grandExchangeQuantities[player.selectedGrandExchangeSlot] - player.grandExchangeCompletedQuantities[player.selectedGrandExchangeSlot]) * player.grandExchangeUnitPrices[player.selectedGrandExchangeSlot]);
                    int n11 = player.selectedGrandExchangeSlot;
                    player.grandExchangeSecondaryCollectAmounts[n11] = player.grandExchangeSecondaryCollectAmounts[n11] + itemStack3.getAmount();
                    if (itemStack.getAmount() <= 0) {
                        itemStack = null;
                    }
                    if (itemStack3.getAmount() <= 0) {
                        itemStack3 = null;
                    }
                    itemStackArray = new ItemStack[]{itemStack, itemStack3};
                }
                player10 = player;
                player10.packetSender.sendInterfaceProgress(19011, 250);
                player10 = player;
                player10.packetSender.sendItemContainer(19006, itemStackArray);
                GrandExchangeManager.refreshSelectedOfferDetails(player);
                return true;
            }
        }
        return false;
    }

    public static void refreshOfferSlots(Player player) {
        int n = 0;
        while (n < 6) {
            Player player2;
            if (player.grandExchangeQuantities[n] == 0) {
                player2 = player;
                player2.packetSender.sendInterfaceText("Empty", n + 19095);
                player2 = player;
                player2.packetSender.setInterfaceHiddenFlag(0, 19023 + n * 3);
                player2 = player;
                player2.packetSender.setInterfaceHiddenFlag(1, 19041 + n * 9);
                player2 = player;
                player2.packetSender.sendSingleItemContainer(19044 + n * 9, -1, 0);
                player2 = player;
                player2.packetSender.sendInterfaceText("Item name", 19045 + n * 9);
                player2 = player;
                player2.packetSender.sendInterfaceText("0 coins", 19046 + n * 9);
                player2 = player;
                player2.packetSender.sendInterfaceProgress(19049 + n * 9, 0);
            } else {
                player2 = player;
                player2.packetSender.sendInterfaceText(player.grandExchangeSellOfferFlags[n] ? "Sell" : "Buy", n + 19095);
                player2 = player;
                player2.packetSender.sendSingleItemContainer(19044 + n * 9, player.grandExchangeItemIds[n], player.grandExchangeQuantities[n]);
                player2 = player;
                player2.packetSender.sendInterfaceText(new ItemStack(player.grandExchangeItemIds[n], 1).getDefinition().getName(), 19045 + n * 9);
                player2 = player;
                player2.packetSender.sendInterfaceText(String.valueOf(GameUtil.formatNumber(player.grandExchangeUnitPrices[n])) + " coins", 19046 + n * 9);
                double d = player.grandExchangeCompletedQuantities[n];
                double d2 = player.grandExchangeQuantities[n];
                double d3 = 100.0 * (d / d2);
                int n2 = (int)d3;
                boolean bl = player.grandExchangeCancelledFlags[n];
                if (bl) {
                    n2 = 250;
                }
                player2 = player;
                player2.packetSender.sendInterfaceProgress(19049 + n * 9, n2);
                player2 = player;
                player2.packetSender.setInterfaceHiddenFlag(1, 19023 + n * 3);
                player2 = player;
                player2.packetSender.setInterfaceHiddenFlag(0, 19041 + n * 9);
            }
            ++n;
        }
    }

    public static void rollInstantPriceFluctuation() {
        int n = GameUtil.rollPriceFluctuationPercent(11);
        if (GameUtil.randomInt(2) == 0) {
            n = -n;
        }
        instantPriceFluctuationPercent = n;
    }

    public static int getGuidePrice(int n) {
        double d;
        double d2;
        if (n == 995) {
            return 1;
        }
        ItemDefinition itemDefinition = ItemDefinition.forId(n);
        int n2 = itemDefinition.isNote() ? itemDefinition.getUnnotedId() : itemDefinition.getId();
        if ((n2 = GrandExchangePriceSample.getAveragePrice(n2)) == -1) {
            n2 = itemDefinition.getValue();
        }
        if (ServerSettings.instantGrandExchangeEnabled && ServerSettings.instantGrandExchangePriceFluctuationEnabled && (n2 = (int)(d2 = (double)n2 + (d = (double)(n2 / 100 * instantPriceFluctuationPercent)))) <= 0) {
            n2 = 1;
        }
        return n2;
    }

    public static void setSelectedOfferQuantity(Player player, int n) {
        player.selectedGrandExchangeQuantity = n;
        if (player.selectedGrandExchangeQuantity <= 0) {
            player.selectedGrandExchangeQuantity = 1;
        }
        if (player.getOpenInterfaceId() == 18939) {
            n = player.getInventoryManager().getContainer().getItemAmount(player.selectedGrandExchangeItemId);
            ItemStack itemStack = new ItemStack(player.selectedGrandExchangeItemId, 1);
            boolean bl = itemStack.getDefinition().hasNote();
            if (bl) {
                int n2 = itemStack.getDefinition().getNotedId();
                n += player.getInventoryManager().getContainer().getItemAmount(n2);
            }
            if (player.selectedGrandExchangeQuantity >= n) {
                player.selectedGrandExchangeQuantity = n;
            }
        }
        GrandExchangeManager.refreshSelectedOfferTotals(player);
    }

    public static void setSelectedOfferUnitPrice(Player player, int n) {
        player.selectedGrandExchangeUnitPrice = n;
        if (player.selectedGrandExchangeUnitPrice < 0) {
            player.selectedGrandExchangeUnitPrice = 0;
        }
        GrandExchangeManager.refreshSelectedOfferTotals(player);
    }

    private static void adjustSelectedOfferQuantity(Player player, int n) {
        player.selectedGrandExchangeQuantity += n;
        if (player.selectedGrandExchangeQuantity <= 0) {
            player.selectedGrandExchangeQuantity = 1;
        }
        if (player.getOpenInterfaceId() == 18939) {
            n = player.getInventoryManager().getContainer().getItemAmount(player.selectedGrandExchangeItemId);
            ItemStack itemStack = new ItemStack(player.selectedGrandExchangeItemId, 1);
            boolean bl = itemStack.getDefinition().hasNote();
            if (bl) {
                int n2 = itemStack.getDefinition().getNotedId();
                n += player.getInventoryManager().getContainer().getItemAmount(n2);
            }
            if (player.selectedGrandExchangeQuantity >= n) {
                player.selectedGrandExchangeQuantity = n;
            }
        }
        GrandExchangeManager.refreshSelectedOfferTotals(player);
    }

    private static void adjustSelectedOfferUnitPrice(Player player, int n) {
        if (ServerSettings.instantGrandExchangeEnabled) {
            player.packetSender.sendGameMessage("Price can't be modified with instant Grand Exchange trades.");
            return;
        }
        player.selectedGrandExchangeUnitPrice += n;
        if (player.selectedGrandExchangeUnitPrice < 0) {
            player.selectedGrandExchangeUnitPrice = 0;
        }
        GrandExchangeManager.refreshSelectedOfferTotals(player);
    }

    private static void applySelectedOfferPricePreset(Player player, String string) {
        double d;
        if (ServerSettings.instantGrandExchangeEnabled) {
            player.packetSender.sendGameMessage("Price can't be modified with instant Grand Exchange trades.");
            return;
        }
        if (string.equals("guide")) {
            player.selectedGrandExchangeUnitPrice = GrandExchangeManager.getGuidePrice(player.selectedGrandExchangeItemId);
        }
        if (string.equals("+5%")) {
            int n;
            double d2 = player.selectedGrandExchangeUnitPrice;
            d = d2 * 1.05;
            player.selectedGrandExchangeUnitPrice = n = (int)d;
        }
        if (string.equals("-5%")) {
            int n;
            double d3 = player.selectedGrandExchangeUnitPrice;
            d = d3 * 0.95;
            player.selectedGrandExchangeUnitPrice = n = (int)d;
        }
        if (player.selectedGrandExchangeUnitPrice < 0) {
            player.selectedGrandExchangeUnitPrice = 0;
        }
        GrandExchangeManager.refreshSelectedOfferTotals(player);
    }

    private static void refreshSelectedOfferTotals(Player player) {
        int n = 18920;
        if (player.getOpenInterfaceId() == 18939) {
            n = 18969;
        }
        Player player2 = player;
        player2.packetSender.sendInterfaceText(GameUtil.formatNumber(player.selectedGrandExchangeQuantity), n);
        player2 = player;
        player2.packetSender.sendInterfaceText(String.valueOf(GameUtil.formatNumber(player.selectedGrandExchangeUnitPrice)) + " coins", n + 1);
        player2 = player;
        player2.packetSender.sendInterfaceText(String.valueOf(GameUtil.formatNumber(player.selectedGrandExchangeUnitPrice * player.selectedGrandExchangeQuantity)) + " coins", n + 2);
    }

    public static void selectSellOfferItem(Player player, int n, int n2, int n3) {
        Object object = player.getInventoryManager().getContainer().getItemAt(n);
        if (object == null || ((ItemStack)object).getId() != n2 || !((ItemStack)object).isValid()) {
            return;
        }
        if (((ItemStack)object).getDefinition().isUntradeable()) {
            return;
        }
        player.getInventoryManager().getContainer().getItemAmount(n2);
        n2 = ((ItemStack)object).getDefinition().isNote() ? 1 : 0;
        if (((ItemStack)object).getDefinition().getId() > 11883) {
            object = player;
            ((Player)object).packetSender.sendGameMessage("This item is not supported yet.");
            return;
        }
        if ((n2 = n2 != 0 ? ((ItemStack)object).getDefinition().getUnnotedId() : ((ItemStack)object).getDefinition().getId()) == 995) {
            return;
        }
        player.selectedGrandExchangeItemId = n2;
        player.selectedGrandExchangeQuantity = ((ItemStack)object).getAmount();
        player.selectedGrandExchangeUnitPrice = GrandExchangeManager.getGuidePrice(n2);
        object = player;
        ((Player)object).packetSender.sendInterfaceText(GameUtil.formatNumber(player.selectedGrandExchangeUnitPrice), 18968);
        object = player;
        ((Player)object).packetSender.sendInterfaceItemModel(18983, n2);
        GrandExchangeManager.refreshSelectedOfferTotals(player);
    }
}

