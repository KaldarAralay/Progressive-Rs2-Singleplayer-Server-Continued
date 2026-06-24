/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.grandexchange;

import com.rs2.ServerSettings;
import com.rs2.bot.BotTradeAdvertManager;
import com.rs2.model.GameplayHelper;
import com.rs2.model.World;
import com.rs2.model.grandexchange.GrandExchangePriceSample;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.player.CharacterFileRecord;
import com.rs2.model.player.GrandExchangeManager;
import com.rs2.model.player.Player;
import com.rs2.util.CharacterFileManager;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.Iterator;

public final class GrandExchangeOffer {
    private String ownerUsername;
    private boolean sellOffer;
    int itemId;
    private int slotIndex;
    int quantity;
    private int remainingQuantity;
    int unitPrice;
    private long serverOfferCreatedAtMillis;
    private int serverOfferLifetimeMinutes;
    private static ArrayList sellOffers = new ArrayList();
    private static ArrayList buyOffers = new ArrayList();
    private static ArrayList serverOffers = new ArrayList();
    private static ArrayList pendingBuyOfferRemovals = new ArrayList();
    private static ArrayList pendingSellOfferRemovals = new ArrayList();

    public GrandExchangeOffer(String string, int n, boolean bl, int n2, int n3, int n4, int n5) {
        new GrandExchangeOffer(string, n, bl, n2, n3, n4, n5, 0L, 0);
    }

    private GrandExchangeOffer(String string, int n, boolean bl, int n2, int n3, int n4, int n5, long l, int n6) {
        this.ownerUsername = string;
        this.itemId = n2;
        this.sellOffer = bl;
        this.remainingQuantity = n3;
        this.unitPrice = n5;
        this.slotIndex = n;
        this.quantity = n4;
        this.serverOfferCreatedAtMillis = l;
        this.serverOfferLifetimeMinutes = n6;
        if (bl) {
            sellOffers.add(this);
        } else {
            buyOffers.add(this);
        }
        this.matchOffer(this, bl);
    }

    public static void initializeServerOffers() {
        int n = 0;
        while (n < ServerSettings.grandExchangeServerOfferCount) {
            GrandExchangeOffer.createServerOffer();
            ++n;
        }
    }

    private static void createServerOffer() {
        double d;
        boolean bl = GameUtil.randomInt(2) == 0;
        int n = GameUtil.randomInt(100);
        Object object = n >= BotTradeAdvertManager.commonItemChancePercent ? BotTradeAdvertManager.tradeAdvertOfferPool : BotTradeAdvertManager.commonTradeAdvertOfferPool;
        int n2 = GameUtil.randomInt(((ArrayList)object).size());
        object = (GameplayHelper)((ArrayList)object).get(n2);
        n2 = BotTradeAdvertManager.tradeAdvertOfferPool.indexOf(object);
        int n3 = ((GameplayHelper)BotTradeAdvertManager.tradeAdvertOfferPool.get(n2)).getTradeAdvertItemId();
        ItemDefinition.forId(n3);
        double d2 = GrandExchangeManager.getGuidePrice(n3);
        if (d2 < 1.0) {
            d2 = 1.0;
        }
        int n4 = GameUtil.rollPriceFluctuationPercent(11);
        double d3 = d2 / 100.0 * (double)n4;
        if (bl && GameUtil.randomInt(10) == 0) {
            d3 = -d3;
        }
        if (!bl && GameUtil.randomInt(10) == 0) {
            d3 = -d3;
        }
        double d4 = bl ? d2 + d3 : d2 - d3;
        n4 = (int)d4;
        int n5 = GameUtil.randomInt(((GameplayHelper)BotTradeAdvertManager.tradeAdvertOfferPool.get(n2)).getTradeAdvertQuantityOptions().length);
        n2 = GameUtil.randomInt(((GameplayHelper)BotTradeAdvertManager.tradeAdvertOfferPool.get(n2)).getTradeAdvertQuantityOptions()[n5]) + 1;
        n4 = n2 * n4;
        if ((n4 = BotTradeAdvertManager.roundAdvertUnitPrice(bl, n4, n2)) <= 0) {
            n4 = 1 + GameUtil.randomInt(5);
        }
        n5 = 2 + GameUtil.randomInt(4);
        GrandExchangeOffer grandExchangeOffer = new GrandExchangeOffer("[SERVER]", 0, bl, n3, n2, n2, n4, System.currentTimeMillis(), n5);
        serverOffers.add(grandExchangeOffer);
    }

    public static void processServerOffers() {
        int n = 0;
        ArrayList<GrandExchangeOffer> arrayList = new ArrayList<GrandExchangeOffer>();
        ArrayList<GrandExchangeOffer> arrayList2 = new ArrayList<GrandExchangeOffer>();
        ArrayList<GrandExchangeOffer> arrayList3 = new ArrayList<GrandExchangeOffer>();
        Iterator iterator = serverOffers.iterator();
        while (iterator.hasNext()) {
            GrandExchangeOffer grandExchangeOffer;
            GrandExchangeOffer grandExchangeOffer2 = grandExchangeOffer = (GrandExchangeOffer)iterator.next();
            if (!(System.currentTimeMillis() >= grandExchangeOffer2.serverOfferCreatedAtMillis + (long)GameUtil.minutesToMillis(grandExchangeOffer2.serverOfferLifetimeMinutes))) continue;
            if (grandExchangeOffer.sellOffer) {
                arrayList.add(grandExchangeOffer);
            } else {
                arrayList2.add(grandExchangeOffer);
            }
            arrayList3.add(grandExchangeOffer);
            ++n;
        }
        for (GrandExchangeOffer grandExchangeOffer : arrayList) {
            sellOffers.remove(grandExchangeOffer);
        }
        for (GrandExchangeOffer grandExchangeOffer : arrayList2) {
            buyOffers.remove(grandExchangeOffer);
        }
        for (GrandExchangeOffer grandExchangeOffer : arrayList3) {
            serverOffers.remove(grandExchangeOffer);
        }
        int n2 = 0;
        while (n2 < n) {
            GrandExchangeOffer.createServerOffer();
            ++n2;
        }
    }

    public static void cancelOffer(Player player, int n) {
        GrandExchangeOffer grandExchangeOffer;
        GrandExchangeOffer grandExchangeOffer22;
        Iterator iterator;
        String string = player.getUsername();
        if (player.grandExchangeSellOfferFlags[n]) {
            iterator = sellOffers.iterator();
            while (iterator.hasNext()) {
                grandExchangeOffer = grandExchangeOffer22 = (GrandExchangeOffer)iterator.next();
                if (!grandExchangeOffer22.ownerUsername.equals(string)) continue;
                grandExchangeOffer = grandExchangeOffer22;
                if (grandExchangeOffer.slotIndex != n || !GrandExchangeOffer.isPlayerOfferCurrent(player, grandExchangeOffer22)) continue;
                pendingSellOfferRemovals.add(grandExchangeOffer22);
                break;
            }
        } else {
            iterator = buyOffers.iterator();
            while (iterator.hasNext()) {
                grandExchangeOffer = grandExchangeOffer22 = (GrandExchangeOffer)iterator.next();
                if (!grandExchangeOffer22.ownerUsername.equals(string)) continue;
                grandExchangeOffer = grandExchangeOffer22;
                if (grandExchangeOffer.slotIndex != n || !GrandExchangeOffer.isPlayerOfferCurrent(player, grandExchangeOffer22)) continue;
                pendingBuyOfferRemovals.add(grandExchangeOffer22);
                break;
            }
        }
        for (Object pendingBuyOfferObject : pendingBuyOfferRemovals) {
            GrandExchangeOffer pendingBuyOffer = (GrandExchangeOffer)pendingBuyOfferObject;
            buyOffers.remove(pendingBuyOffer);
        }
        for (Object pendingSellOfferObject : pendingSellOfferRemovals) {
            GrandExchangeOffer pendingSellOffer = (GrandExchangeOffer)pendingSellOfferObject;
            sellOffers.remove(pendingSellOffer);
        }
    }

    private void matchOffer(GrandExchangeOffer grandExchangeOffer, boolean bl) {
        GrandExchangeOffer grandExchangeOffer2;
        Iterator iterator;
        pendingBuyOfferRemovals.clear();
        pendingSellOfferRemovals.clear();
        int n = 0;
        if (bl) {
            iterator = buyOffers.iterator();
            while (iterator.hasNext()) {
                GrandExchangeOffer grandExchangeOffer3;
                grandExchangeOffer2 = grandExchangeOffer3 = (GrandExchangeOffer)iterator.next();
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer3.ownerUsername.equals(grandExchangeOffer2.ownerUsername)) continue;
                GrandExchangeOffer grandExchangeOffer4 = grandExchangeOffer3;
                grandExchangeOffer2 = grandExchangeOffer4;
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer4.itemId != grandExchangeOffer2.itemId) continue;
                GrandExchangeOffer grandExchangeOffer5 = grandExchangeOffer3;
                grandExchangeOffer2 = grandExchangeOffer5;
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer5.unitPrice < grandExchangeOffer2.unitPrice) continue;
                this.settleMatchedOffers(grandExchangeOffer, grandExchangeOffer3);
                grandExchangeOffer2 = grandExchangeOffer3;
                if (grandExchangeOffer2.remainingQuantity == 0) {
                    pendingBuyOfferRemovals.add(grandExchangeOffer3);
                }
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer2.remainingQuantity != 0) continue;
                pendingSellOfferRemovals.add(grandExchangeOffer);
                break;
            }
        } else {
            iterator = sellOffers.iterator();
            while (iterator.hasNext()) {
                GrandExchangeOffer grandExchangeOffer6;
                grandExchangeOffer2 = grandExchangeOffer6 = (GrandExchangeOffer)iterator.next();
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer6.ownerUsername.equals(grandExchangeOffer2.ownerUsername)) continue;
                GrandExchangeOffer grandExchangeOffer7 = grandExchangeOffer6;
                grandExchangeOffer2 = grandExchangeOffer7;
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer7.itemId != grandExchangeOffer2.itemId) continue;
                GrandExchangeOffer grandExchangeOffer8 = grandExchangeOffer6;
                grandExchangeOffer2 = grandExchangeOffer8;
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer8.unitPrice > grandExchangeOffer2.unitPrice) continue;
                this.settleMatchedOffers(grandExchangeOffer6, grandExchangeOffer);
                grandExchangeOffer2 = grandExchangeOffer6;
                if (grandExchangeOffer2.remainingQuantity == 0) {
                    pendingSellOfferRemovals.add(grandExchangeOffer6);
                }
                grandExchangeOffer2 = grandExchangeOffer;
                if (grandExchangeOffer2.remainingQuantity != 0) continue;
                pendingBuyOfferRemovals.add(grandExchangeOffer);
                break;
            }
        }
        for (Object pendingBuyOfferObject : pendingBuyOfferRemovals) {
            GrandExchangeOffer pendingBuyOffer = (GrandExchangeOffer)pendingBuyOfferObject;
            buyOffers.remove(pendingBuyOffer);
            if (!serverOffers.contains(pendingBuyOffer)) continue;
            serverOffers.remove(pendingBuyOffer);
            ++n;
        }
        for (Object pendingSellOfferObject : pendingSellOfferRemovals) {
            GrandExchangeOffer pendingSellOffer = (GrandExchangeOffer)pendingSellOfferObject;
            sellOffers.remove(pendingSellOffer);
            if (!serverOffers.contains(pendingSellOffer)) continue;
            serverOffers.remove(pendingSellOffer);
            ++n;
        }
        int n2 = 0;
        while (n2 < n) {
            GrandExchangeOffer.createServerOffer();
            ++n2;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void settleMatchedOffers(GrandExchangeOffer grandExchangeOffer, GrandExchangeOffer grandExchangeOffer2) {
        boolean bl = false;
        boolean bl2 = false;
        GrandExchangeOffer grandExchangeOffer3 = grandExchangeOffer;
        String string = grandExchangeOffer3.ownerUsername;
        grandExchangeOffer3 = grandExchangeOffer2;
        String string2 = grandExchangeOffer3.ownerUsername;
        if (string.equals("[SERVER]")) {
            bl = true;
        }
        if (string2.equals("[SERVER]")) {
            bl2 = true;
        }
        Object object = null;
        Object object2 = null;
        Object object3 = null;
        Object object4 = null;
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Object object5 = playerArray[n2];
            if (object5 != null) {
                if (((Player)object5).getUsername().equals(string)) {
                    if (!GrandExchangeOffer.isPlayerOfferCurrent((Player)object5, grandExchangeOffer)) {
                        pendingSellOfferRemovals.add(grandExchangeOffer);
                        return;
                    }
                    object = object5;
                    bl = true;
                } else if (((Player)object5).getUsername().equals(string2)) {
                    if (!GrandExchangeOffer.isPlayerOfferCurrent((Player)object5, grandExchangeOffer2)) {
                        pendingBuyOfferRemovals.add(grandExchangeOffer2);
                        return;
                    }
                    object2 = object5;
                    bl2 = true;
                }
                if (bl && bl2) break;
            }
            ++n2;
        }
        if (!bl || !bl2) {
            for (Object object5 : CharacterFileManager.liveHiscoreRecords) {
                if (object5 == null) continue;
                if (!bl) {
                    Object object6 = object5;
                    if (((CharacterFileRecord)object6).username.equals(string)) {
                        if (!GrandExchangeOffer.isRecordOfferCurrent((CharacterFileRecord)object5, grandExchangeOffer)) {
                            pendingSellOfferRemovals.add(grandExchangeOffer);
                            return;
                        }
                        object3 = object5;
                        bl = true;
                    }
                }
                if (!bl2) {
                    Object object7 = object5;
                    if (((CharacterFileRecord)object7).username.equals(string2)) {
                        if (!GrandExchangeOffer.isRecordOfferCurrent((CharacterFileRecord)object5, grandExchangeOffer2)) {
                            pendingBuyOfferRemovals.add(grandExchangeOffer2);
                            return;
                        }
                        object4 = object5;
                        bl2 = true;
                    }
                }
                if (bl && bl2) break;
            }
        }
        if (object4 != null && object3 != null) {
            GrandExchangeOffer.settleRecordVsRecordMatch((CharacterFileRecord)object4, grandExchangeOffer2, (CharacterFileRecord)object3, grandExchangeOffer);
            return;
        }
        if (object2 != null && object != null) {
            ((Player)object2).grandExchangeSettlementInProgress = true;
            ((Player)object).grandExchangeSettlementInProgress = true;
            GrandExchangeOffer.settlePlayerVsPlayerMatch((Player)object2, grandExchangeOffer2, (Player)object, grandExchangeOffer);
            ((Player)object2).grandExchangeSettlementInProgress = false;
            ((Player)object).grandExchangeSettlementInProgress = false;
            return;
        }
        if (object4 != null && object != null) {
            ((Player)object).grandExchangeSettlementInProgress = true;
            GrandExchangeOffer.settleRecordVsPlayerMatch((CharacterFileRecord)object4, grandExchangeOffer2, (Player)object, grandExchangeOffer);
            ((Player)object).grandExchangeSettlementInProgress = false;
            return;
        }
        if (object2 != null && object3 != null) {
            ((Player)object2).grandExchangeSettlementInProgress = true;
            GrandExchangeOffer.settlePlayerVsRecordMatch((Player)object2, grandExchangeOffer2, (CharacterFileRecord)object3, grandExchangeOffer);
            ((Player)object2).grandExchangeSettlementInProgress = false;
            return;
        }
        if (object4 != null && string.equals("[SERVER]")) {
            GrandExchangeOffer.settleRecordVsRecordMatch((CharacterFileRecord)object4, grandExchangeOffer2, (CharacterFileRecord)object3, grandExchangeOffer);
            return;
        }
        if (object2 != null && string.equals("[SERVER]")) {
            ((Player)object2).grandExchangeSettlementInProgress = true;
            GrandExchangeOffer.settlePlayerVsPlayerMatch((Player)object2, grandExchangeOffer2, (Player)object, grandExchangeOffer);
            ((Player)object2).grandExchangeSettlementInProgress = false;
            return;
        }
        if (string2.equals("[SERVER]") && object != null) {
            ((Player)object).grandExchangeSettlementInProgress = true;
            GrandExchangeOffer.settleRecordVsPlayerMatch((CharacterFileRecord)object4, grandExchangeOffer2, (Player)object, grandExchangeOffer);
            ((Player)object).grandExchangeSettlementInProgress = false;
            return;
        }
        if (string2.equals("[SERVER]") && object3 != null) {
            GrandExchangeOffer.settlePlayerVsRecordMatch((Player)object2, grandExchangeOffer2, (CharacterFileRecord)object3, grandExchangeOffer);
        }
    }

    private static void settleRecordVsRecordMatch(CharacterFileRecord characterFileRecord, GrandExchangeOffer grandExchangeOffer, CharacterFileRecord characterFileRecord2, GrandExchangeOffer grandExchangeOffer2) {
        int n;
        GrandExchangeOffer grandExchangeOffer3 = grandExchangeOffer2;
        int n2 = grandExchangeOffer3.remainingQuantity;
        grandExchangeOffer3 = grandExchangeOffer;
        if (grandExchangeOffer3.remainingQuantity >= n2) {
            n = n2;
        } else {
            grandExchangeOffer3 = grandExchangeOffer;
            n = grandExchangeOffer3.remainingQuantity;
        }
        n2 = n;
        grandExchangeOffer3 = grandExchangeOffer2;
        int n3 = grandExchangeOffer3.unitPrice;
        grandExchangeOffer3 = grandExchangeOffer2;
        int n4 = grandExchangeOffer3.unitPrice * n2;
        grandExchangeOffer3 = grandExchangeOffer;
        int n5 = grandExchangeOffer3.remainingQuantity - n2;
        grandExchangeOffer3 = grandExchangeOffer;
        grandExchangeOffer.remainingQuantity = n5;
        grandExchangeOffer3 = grandExchangeOffer2;
        n5 = grandExchangeOffer3.remainingQuantity - n2;
        grandExchangeOffer3 = grandExchangeOffer2;
        grandExchangeOffer2.remainingQuantity = n5;
        grandExchangeOffer3 = grandExchangeOffer;
        int n6 = grandExchangeOffer3.slotIndex;
        characterFileRecord.grandExchangeCompletedQuantities[n6] = characterFileRecord.grandExchangeCompletedQuantities[n6] + n2;
        if (characterFileRecord2 != null) {
            grandExchangeOffer3 = grandExchangeOffer2;
            int n7 = grandExchangeOffer3.slotIndex;
            characterFileRecord2.grandExchangeCompletedQuantities[n7] = characterFileRecord2.grandExchangeCompletedQuantities[n7] + n2;
        }
        grandExchangeOffer3 = grandExchangeOffer;
        int n8 = grandExchangeOffer3.slotIndex;
        characterFileRecord.grandExchangeTotalPrices[n8] = characterFileRecord.grandExchangeTotalPrices[n8] + n4;
        if (characterFileRecord2 != null) {
            grandExchangeOffer3 = grandExchangeOffer2;
            int n9 = grandExchangeOffer3.slotIndex;
            characterFileRecord2.grandExchangeTotalPrices[n9] = characterFileRecord2.grandExchangeTotalPrices[n9] + n4;
        }
        grandExchangeOffer3 = grandExchangeOffer;
        if (n3 < grandExchangeOffer3.unitPrice) {
            grandExchangeOffer3 = grandExchangeOffer;
            n3 = grandExchangeOffer3.unitPrice - n3;
            grandExchangeOffer3 = grandExchangeOffer;
            int n10 = grandExchangeOffer3.slotIndex;
            characterFileRecord.grandExchangeSecondaryCollectAmounts[n10] = characterFileRecord.grandExchangeSecondaryCollectAmounts[n10] + (n3 *= n2);
        }
        grandExchangeOffer3 = grandExchangeOffer;
        int n11 = grandExchangeOffer3.slotIndex;
        characterFileRecord.grandExchangePrimaryCollectAmounts[n11] = characterFileRecord.grandExchangePrimaryCollectAmounts[n11] + n2;
        if (characterFileRecord2 != null) {
            grandExchangeOffer3 = grandExchangeOffer2;
            int n12 = grandExchangeOffer3.slotIndex;
            characterFileRecord2.grandExchangePrimaryCollectAmounts[n12] = characterFileRecord2.grandExchangePrimaryCollectAmounts[n12] + n4;
        }
        grandExchangeOffer3 = grandExchangeOffer;
        if (grandExchangeOffer3.remainingQuantity == 0) {
            grandExchangeOffer3 = grandExchangeOffer;
            characterFileRecord.grandExchangeFinishMessagePending[grandExchangeOffer3.slotIndex] = true;
        }
        grandExchangeOffer3 = grandExchangeOffer2;
        if (grandExchangeOffer3.remainingQuantity == 0) {
            if (characterFileRecord2 != null) {
                grandExchangeOffer3 = grandExchangeOffer2;
                characterFileRecord2.grandExchangeFinishMessagePending[grandExchangeOffer3.slotIndex] = true;
            }
            new GrandExchangePriceSample(grandExchangeOffer2);
        }
        CharacterFileManager.saveCharacterFileRecord(characterFileRecord);
        if (characterFileRecord2 != null) {
            CharacterFileManager.saveCharacterFileRecord(characterFileRecord2);
        }
    }

    private static void settlePlayerVsPlayerMatch(Player player, GrandExchangeOffer grandExchangeOffer, Player player2, GrandExchangeOffer grandExchangeOffer2) {
        int n;
        Object object = grandExchangeOffer2;
        int n2 = ((GrandExchangeOffer)object).remainingQuantity;
        object = grandExchangeOffer;
        if (((GrandExchangeOffer)object).remainingQuantity >= n2) {
            n = n2;
        } else {
            object = grandExchangeOffer;
            n = ((GrandExchangeOffer)object).remainingQuantity;
        }
        n2 = n;
        object = grandExchangeOffer2;
        int n3 = ((GrandExchangeOffer)object).unitPrice;
        object = grandExchangeOffer2;
        int n4 = ((GrandExchangeOffer)object).unitPrice * n2;
        object = grandExchangeOffer;
        int n5 = ((GrandExchangeOffer)object).remainingQuantity - n2;
        object = grandExchangeOffer;
        grandExchangeOffer.remainingQuantity = n5;
        object = grandExchangeOffer2;
        n5 = ((GrandExchangeOffer)object).remainingQuantity - n2;
        object = grandExchangeOffer2;
        grandExchangeOffer2.remainingQuantity = n5;
        object = grandExchangeOffer;
        int n6 = ((GrandExchangeOffer)object).slotIndex;
        player.grandExchangeCompletedQuantities[n6] = player.grandExchangeCompletedQuantities[n6] + n2;
        if (player2 != null) {
            object = grandExchangeOffer2;
            int n7 = ((GrandExchangeOffer)object).slotIndex;
            player2.grandExchangeCompletedQuantities[n7] = player2.grandExchangeCompletedQuantities[n7] + n2;
        }
        object = grandExchangeOffer;
        int n8 = ((GrandExchangeOffer)object).slotIndex;
        player.grandExchangeTotalPrices[n8] = player.grandExchangeTotalPrices[n8] + n4;
        if (player2 != null) {
            object = grandExchangeOffer2;
            int n9 = ((GrandExchangeOffer)object).slotIndex;
            player2.grandExchangeTotalPrices[n9] = player2.grandExchangeTotalPrices[n9] + n4;
        }
        object = grandExchangeOffer;
        if (n3 < ((GrandExchangeOffer)object).unitPrice) {
            object = grandExchangeOffer;
            n3 = ((GrandExchangeOffer)object).unitPrice - n3;
            object = grandExchangeOffer;
            int n10 = ((GrandExchangeOffer)object).slotIndex;
            player.grandExchangeSecondaryCollectAmounts[n10] = player.grandExchangeSecondaryCollectAmounts[n10] + (n3 *= n2);
        }
        object = grandExchangeOffer;
        int n11 = ((GrandExchangeOffer)object).slotIndex;
        player.grandExchangePrimaryCollectAmounts[n11] = player.grandExchangePrimaryCollectAmounts[n11] + n2;
        if (player2 != null) {
            object = grandExchangeOffer2;
            int n12 = ((GrandExchangeOffer)object).slotIndex;
            player2.grandExchangePrimaryCollectAmounts[n12] = player2.grandExchangePrimaryCollectAmounts[n12] + n4;
        }
        object = grandExchangeOffer;
        if (((GrandExchangeOffer)object).remainingQuantity == 0 && player != null) {
            object = grandExchangeOffer;
            GrandExchangeManager.sendOfferCompletionMessage(player, ((GrandExchangeOffer)object).slotIndex);
        }
        object = grandExchangeOffer2;
        if (((GrandExchangeOffer)object).remainingQuantity == 0) {
            if (player2 != null) {
                object = grandExchangeOffer2;
                GrandExchangeManager.sendOfferCompletionMessage(player2, ((GrandExchangeOffer)object).slotIndex);
            }
            new GrandExchangePriceSample(grandExchangeOffer2);
        }
        if (player.getOpenInterfaceId() == 19018) {
            GrandExchangeManager.refreshOfferSlots(player);
        }
        if (player.getOpenInterfaceId() == 18984) {
            object = grandExchangeOffer;
            if (player.selectedGrandExchangeSlot == ((GrandExchangeOffer)object).slotIndex) {
                GrandExchangeManager.refreshSelectedOfferDetails(player);
            }
        }
        if (player2 != null) {
            if (player2.getOpenInterfaceId() == 19018) {
                GrandExchangeManager.refreshOfferSlots(player2);
            }
            if (player2.getOpenInterfaceId() == 18984) {
                object = grandExchangeOffer2;
                if (player2.selectedGrandExchangeSlot == ((GrandExchangeOffer)object).slotIndex) {
                    GrandExchangeManager.refreshSelectedOfferDetails(player2);
                }
            }
        }
        object = player;
        CharacterFileManager.savePlayer((Player)object);
        if (player2 != null) {
            object = player2;
            CharacterFileManager.savePlayer((Player)object);
        }
    }

    private static void settlePlayerVsRecordMatch(Player player, GrandExchangeOffer grandExchangeOffer, CharacterFileRecord characterFileRecord, GrandExchangeOffer grandExchangeOffer2) {
        int n;
        Object object = grandExchangeOffer2;
        int n2 = ((GrandExchangeOffer)object).remainingQuantity;
        object = grandExchangeOffer;
        if (((GrandExchangeOffer)object).remainingQuantity >= n2) {
            n = n2;
        } else {
            object = grandExchangeOffer;
            n = ((GrandExchangeOffer)object).remainingQuantity;
        }
        n2 = n;
        object = grandExchangeOffer2;
        int n3 = ((GrandExchangeOffer)object).unitPrice;
        object = grandExchangeOffer2;
        int n4 = ((GrandExchangeOffer)object).unitPrice * n2;
        object = grandExchangeOffer;
        int n5 = ((GrandExchangeOffer)object).remainingQuantity - n2;
        object = grandExchangeOffer;
        grandExchangeOffer.remainingQuantity = n5;
        object = grandExchangeOffer2;
        n5 = ((GrandExchangeOffer)object).remainingQuantity - n2;
        object = grandExchangeOffer2;
        grandExchangeOffer2.remainingQuantity = n5;
        if (player != null) {
            object = grandExchangeOffer;
            int n6 = ((GrandExchangeOffer)object).slotIndex;
            player.grandExchangeCompletedQuantities[n6] = player.grandExchangeCompletedQuantities[n6] + n2;
        }
        object = grandExchangeOffer2;
        int n7 = ((GrandExchangeOffer)object).slotIndex;
        characterFileRecord.grandExchangeCompletedQuantities[n7] = characterFileRecord.grandExchangeCompletedQuantities[n7] + n2;
        if (player != null) {
            object = grandExchangeOffer;
            int n8 = ((GrandExchangeOffer)object).slotIndex;
            player.grandExchangeTotalPrices[n8] = player.grandExchangeTotalPrices[n8] + n4;
        }
        object = grandExchangeOffer2;
        int n9 = ((GrandExchangeOffer)object).slotIndex;
        characterFileRecord.grandExchangeTotalPrices[n9] = characterFileRecord.grandExchangeTotalPrices[n9] + n4;
        object = grandExchangeOffer;
        if (n3 < ((GrandExchangeOffer)object).unitPrice) {
            object = grandExchangeOffer;
            n3 = ((GrandExchangeOffer)object).unitPrice - n3;
            n3 *= n2;
            if (player != null) {
                object = grandExchangeOffer;
                int n10 = ((GrandExchangeOffer)object).slotIndex;
                player.grandExchangeSecondaryCollectAmounts[n10] = player.grandExchangeSecondaryCollectAmounts[n10] + n3;
            }
        }
        if (player != null) {
            object = grandExchangeOffer;
            int n11 = ((GrandExchangeOffer)object).slotIndex;
            player.grandExchangePrimaryCollectAmounts[n11] = player.grandExchangePrimaryCollectAmounts[n11] + n2;
        }
        object = grandExchangeOffer2;
        int n12 = ((GrandExchangeOffer)object).slotIndex;
        characterFileRecord.grandExchangePrimaryCollectAmounts[n12] = characterFileRecord.grandExchangePrimaryCollectAmounts[n12] + n4;
        object = grandExchangeOffer;
        if (((GrandExchangeOffer)object).remainingQuantity == 0 && player != null) {
            object = grandExchangeOffer;
            GrandExchangeManager.sendOfferCompletionMessage(player, ((GrandExchangeOffer)object).slotIndex);
        }
        object = grandExchangeOffer2;
        if (((GrandExchangeOffer)object).remainingQuantity == 0) {
            object = grandExchangeOffer2;
            characterFileRecord.grandExchangeFinishMessagePending[((GrandExchangeOffer)object).slotIndex] = true;
            new GrandExchangePriceSample(grandExchangeOffer2);
        }
        if (player != null) {
            if (player.getOpenInterfaceId() == 19018) {
                GrandExchangeManager.refreshOfferSlots(player);
            }
            if (player.getOpenInterfaceId() == 18984) {
                object = grandExchangeOffer;
                if (player.selectedGrandExchangeSlot == ((GrandExchangeOffer)object).slotIndex) {
                    GrandExchangeManager.refreshSelectedOfferDetails(player);
                }
            }
        }
        CharacterFileManager.saveCharacterFileRecord(characterFileRecord);
        if (player != null) {
            object = player;
            CharacterFileManager.savePlayer((Player)object);
        }
    }

    private static void settleRecordVsPlayerMatch(CharacterFileRecord characterFileRecord, GrandExchangeOffer grandExchangeOffer, Player player, GrandExchangeOffer grandExchangeOffer2) {
        int n;
        Object object = grandExchangeOffer2;
        int n2 = ((GrandExchangeOffer)object).remainingQuantity;
        object = grandExchangeOffer;
        if (((GrandExchangeOffer)object).remainingQuantity >= n2) {
            n = n2;
        } else {
            object = grandExchangeOffer;
            n = ((GrandExchangeOffer)object).remainingQuantity;
        }
        n2 = n;
        object = grandExchangeOffer2;
        int n3 = ((GrandExchangeOffer)object).unitPrice;
        object = grandExchangeOffer2;
        int n4 = ((GrandExchangeOffer)object).unitPrice * n2;
        object = grandExchangeOffer;
        int n5 = ((GrandExchangeOffer)object).remainingQuantity - n2;
        object = grandExchangeOffer;
        grandExchangeOffer.remainingQuantity = n5;
        object = grandExchangeOffer2;
        n5 = ((GrandExchangeOffer)object).remainingQuantity - n2;
        object = grandExchangeOffer2;
        grandExchangeOffer2.remainingQuantity = n5;
        if (characterFileRecord != null) {
            object = grandExchangeOffer;
            int n6 = ((GrandExchangeOffer)object).slotIndex;
            characterFileRecord.grandExchangeCompletedQuantities[n6] = characterFileRecord.grandExchangeCompletedQuantities[n6] + n2;
        }
        object = grandExchangeOffer2;
        int n7 = ((GrandExchangeOffer)object).slotIndex;
        player.grandExchangeCompletedQuantities[n7] = player.grandExchangeCompletedQuantities[n7] + n2;
        if (characterFileRecord != null) {
            object = grandExchangeOffer;
            int n8 = ((GrandExchangeOffer)object).slotIndex;
            characterFileRecord.grandExchangeTotalPrices[n8] = characterFileRecord.grandExchangeTotalPrices[n8] + n4;
        }
        object = grandExchangeOffer2;
        int n9 = ((GrandExchangeOffer)object).slotIndex;
        player.grandExchangeTotalPrices[n9] = player.grandExchangeTotalPrices[n9] + n4;
        object = grandExchangeOffer;
        if (n3 < ((GrandExchangeOffer)object).unitPrice) {
            object = grandExchangeOffer;
            n3 = ((GrandExchangeOffer)object).unitPrice - n3;
            n3 *= n2;
            if (characterFileRecord != null) {
                object = grandExchangeOffer;
                int n10 = ((GrandExchangeOffer)object).slotIndex;
                characterFileRecord.grandExchangeSecondaryCollectAmounts[n10] = characterFileRecord.grandExchangeSecondaryCollectAmounts[n10] + n3;
            }
        }
        if (characterFileRecord != null) {
            object = grandExchangeOffer;
            int n11 = ((GrandExchangeOffer)object).slotIndex;
            characterFileRecord.grandExchangePrimaryCollectAmounts[n11] = characterFileRecord.grandExchangePrimaryCollectAmounts[n11] + n2;
        }
        object = grandExchangeOffer2;
        int n12 = ((GrandExchangeOffer)object).slotIndex;
        player.grandExchangePrimaryCollectAmounts[n12] = player.grandExchangePrimaryCollectAmounts[n12] + n4;
        object = grandExchangeOffer2;
        if (((GrandExchangeOffer)object).remainingQuantity == 0) {
            object = grandExchangeOffer2;
            GrandExchangeManager.sendOfferCompletionMessage(player, ((GrandExchangeOffer)object).slotIndex);
            new GrandExchangePriceSample(grandExchangeOffer2);
        }
        object = grandExchangeOffer;
        if (((GrandExchangeOffer)object).remainingQuantity == 0 && characterFileRecord != null) {
            object = grandExchangeOffer;
            characterFileRecord.grandExchangeFinishMessagePending[((GrandExchangeOffer)object).slotIndex] = true;
        }
        if (player.getOpenInterfaceId() == 19018) {
            GrandExchangeManager.refreshOfferSlots(player);
        }
        if (player.getOpenInterfaceId() == 18984) {
            object = grandExchangeOffer2;
            if (player.selectedGrandExchangeSlot == ((GrandExchangeOffer)object).slotIndex) {
                GrandExchangeManager.refreshSelectedOfferDetails(player);
            }
        }
        if (characterFileRecord != null) {
            CharacterFileManager.saveCharacterFileRecord(characterFileRecord);
        }
        object = player;
        CharacterFileManager.savePlayer((Player)object);
    }

    private static boolean isRecordOfferCurrent(CharacterFileRecord characterFileRecord, GrandExchangeOffer grandExchangeOffer) {
        if (characterFileRecord.grandExchangeCancelledFlags[grandExchangeOffer.slotIndex]) {
            return false;
        }
        GrandExchangeOffer grandExchangeOffer2 = grandExchangeOffer;
        if (characterFileRecord.grandExchangeSellOfferFlags[grandExchangeOffer.slotIndex] == grandExchangeOffer2.sellOffer) {
            grandExchangeOffer2 = grandExchangeOffer;
            if (characterFileRecord.grandExchangeItemIds[grandExchangeOffer.slotIndex] == grandExchangeOffer2.itemId) {
                grandExchangeOffer2 = grandExchangeOffer;
                if (characterFileRecord.grandExchangeQuantities[grandExchangeOffer.slotIndex] == grandExchangeOffer2.quantity) {
                    grandExchangeOffer2 = grandExchangeOffer;
                    if (characterFileRecord.grandExchangeUnitPrices[grandExchangeOffer.slotIndex] == grandExchangeOffer2.unitPrice) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isPlayerOfferCurrent(Player player, GrandExchangeOffer grandExchangeOffer) {
        if (player.grandExchangeCancelledFlags[grandExchangeOffer.slotIndex]) {
            return false;
        }
        GrandExchangeOffer grandExchangeOffer2 = grandExchangeOffer;
        if (player.grandExchangeSellOfferFlags[grandExchangeOffer.slotIndex] == grandExchangeOffer2.sellOffer) {
            grandExchangeOffer2 = grandExchangeOffer;
            if (player.grandExchangeItemIds[grandExchangeOffer.slotIndex] == grandExchangeOffer2.itemId) {
                grandExchangeOffer2 = grandExchangeOffer;
                if (player.grandExchangeQuantities[grandExchangeOffer.slotIndex] == grandExchangeOffer2.quantity) {
                    grandExchangeOffer2 = grandExchangeOffer;
                    if (player.grandExchangeUnitPrices[grandExchangeOffer.slotIndex] == grandExchangeOffer2.unitPrice) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void recordPriceSample(int n, int n2, int n3) {
        ItemDefinition itemDefinition = ItemDefinition.forId(n);
        boolean bl = itemDefinition.isNote();
        int n4 = bl ? itemDefinition.getUnnotedId() : itemDefinition.getId();
        new GrandExchangePriceSample(n4, n2, n3);
    }
}

