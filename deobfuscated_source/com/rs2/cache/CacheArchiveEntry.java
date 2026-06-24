/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.cache;

import com.rs2.bot.BotTradeAdvertManager;
import com.rs2.bot.trade.BotTradeCoinShortageResetTask;
import com.rs2.bot.trade.BotTradeItemShortageResetTask;
import com.rs2.bot.trade.BotTradeOfferTickTask;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.clue.MapClue;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.grandexchange.GrandExchangeOffer;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import java.util.Random;

public class CacheArchiveEntry {
    private int nameHash;
    private int compressedSize;
    private int dataOffset;

    public static void startTradeOfferTick(Player object) {
        World.getTaskScheduler().schedule(new BotTradeOfferTickTask(3, (Player)object));
    }

    public static void completeTradeAdvertOffer(Player object, boolean bl) {
        ((Player)object).queuePublicChatMessage("ty");
        boolean bl2 = true;
        if (((Player)object).tradeAdvertScam || ((Player)object).botAdvertItemId == 0 || ((Player)object).botAdvertItemId == 381) {
            bl2 = false;
        }
        if (!bl && bl2) {
            GrandExchangeOffer.recordPriceSample(((Player)object).botAdvertItemId, ((Player)object).tradeAdvertAcceptedQuantity, ((Player)object).tradeAdvertUnitPrice);
        }
        ((Player)object).tradeAdvertQuantityRemaining -= ((Player)object).tradeAdvertAcceptedQuantity;
        BotTradeAdvertManager.updateTradeAdvertMessage((Player)object);
        ((Player)object).tradeAdvertAcceptedQuantity = -1;
        if (((Player)object).tradeAdvertMode == 1) {
            if (!((Player)object).getInventoryManager().containsItem(995) || ((Player)object).tradeAdvertScam) {
                ((Player)object).botTaskState = "wait for new task";
                World.getTaskScheduler().schedule(new BotTradeCoinShortageResetTask(10, (Player)object));
                return;
            }
        } else if (!((Player)object).getInventoryManager().containsItem(((Player)object).botAdvertItemId) || ((Player)object).tradeAdvertScam) {
            ((Player)object).botTaskState = "wait for new task";
            World.getTaskScheduler().schedule(new BotTradeItemShortageResetTask(10, (Player)object));
        }
    }

    public CacheArchiveEntry(int n, int n2, int n3, int n4) {
        this.nameHash = n;
        this.compressedSize = n3;
        this.dataOffset = n4;
    }

    public int getNameHash() {
        return this.nameHash;
    }

    public int getCompressedSize() {
        return this.compressedSize;
    }

    public int getDataOffset() {
        return this.dataOffset;
    }

    public static int randomMapClueItemForLevel(int n) {
        int n2 = new Random().nextInt(MapClue.values().length);
        while (MapClue.values()[n2].getLevel() != n) {
            n2 = new Random().nextInt(MapClue.values().length);
        }
        return MapClue.values()[n2].getClueItemId();
    }

    public static boolean searchMapClueObject(Player player, int n, int n2) {
        MapClue mapClue = MapClue.forPosition(new Position(n, n2));
        if (mapClue == null) {
            return false;
        }
        if (!mapClue.isObjectSearchClue()) {
            return false;
        }
        if (!player.getInventoryManager().containsItem(mapClue.getClueItemId())) {
            return false;
        }
        player.getInventoryManager().removeItem(new ItemStack(mapClue.getClueItemId(), 1));
        player.getUpdateState().setAnimation(832);
        TreasureTrailManager.advanceOrCompleteTrail(player, mapClue.getLevel(), "You've found another clue!", false, "");
        return true;
    }
}

