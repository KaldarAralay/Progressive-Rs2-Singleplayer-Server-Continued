/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.partyroom;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.gameplay.partyroom.PartyRoomBalloonReward;
import com.rs2.model.gameplay.partyroom.PartyRoomBalloonSpawnTask;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemContainerType;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.CountingDataOutputStream;
import com.rs2.util.GameUtil;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class PartyRoomManager {
    public static boolean balloonDropPending = false;
    public static String b = null;
    private static String h = null;
    private static int partyChestCapacity = 200;
    public static ItemContainer partyChestContainer = new ItemContainer(ItemContainerType.a, partyChestCapacity);
    public static ArrayList activeBalloonObjects = new ArrayList();
    public static ArrayList balloonRewards = new ArrayList();
    public static int partyChestValue = 0;
    public static TickTask balloonDropTask;

    public static boolean hasActiveDropParty() {
        if (balloonDropPending) {
            return true;
        }
        return activeBalloonObjects.size() > 0;
    }

    public static boolean startBalloonBonanza(Player player) {
        ArrayList<ItemStack> arrayList;
        int n;
        if (PartyRoomManager.hasActiveDropParty()) {
            return false;
        }
        if (player.getInventoryManager().containsItemAmount(995, 1000)) {
            player.getInventoryManager().removeItem(new ItemStack(995, 1000));
            balloonDropPending = true;
            balloonRewards.clear();
            ArrayList positions = new ArrayList();
            int n2 = 2730;
            while (n2 < 2745) {
                n = 3462;
                while (n < 3476) {
                    if (n != 3468 || n2 < 2735 || n2 > 2740) {
                        positions.add(new Position(n2, n));
                    }
                    ++n;
                }
                ++n2;
            }
            Collections.shuffle(positions);
            arrayList = new ArrayList<ItemStack>();
            n = 0;
            while (n < partyChestContainer.g()) {
                ItemStack itemStack;
                if (partyChestContainer.getItemAt(n) != null && (itemStack = partyChestContainer.getItemAt(n)) != null) {
                    arrayList.add(itemStack);
                }
                ++n;
            }
            Collections.shuffle(arrayList);
            n = 20;
            if (partyChestValue >= 50000 && partyChestValue < 150000) {
                n = 100;
            } else if (partyChestValue >= 150000 && partyChestValue < 1000000) {
                n = 500;
            } else if (partyChestValue >= 1000000) {
                n = 1000;
            }
            balloonDropTask = new PartyRoomBalloonSpawnTask(n, positions, arrayList);
            World.getTaskScheduler().schedule(balloonDropTask);
            return true;
        }
        return false;
    }

    public static boolean startNightlyDance(Player player) {
        if (Npc.findByDefinitionId(660) != null) {
            return false;
        }
        if (player.getInventoryManager().containsItemAmount(995, 500)) {
            player.getInventoryManager().removeItem(new ItemStack(995, 500));
            int n = 2735;
            while (n <= 2740) {
                Npc npc = new Npc(660);
                GameplayHelper.spawnNpcWithRemovalDelay(npc, n, 3468, 0, 1000);
                ++n;
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean handleBalloonObjectAction(Player object, int n, int n2, int n3) {
        if (n >= 115 && n <= 122) {
            if (((Player)object).gameMode != 0) {
                Player player = object;
                player.packetSender.sendGameMessage("You are not playing on normal gamemode and cant burst the balloons.");
                return true;
            }
            int n4 = 0;
            int n5 = 0;
            if (n2 < ((Entity)object).getPosition().getX()) {
                n4 = -1;
            }
            if (n2 > ((Entity)object).getPosition().getX()) {
                n4 = 1;
            }
            if (n3 < ((Entity)object).getPosition().getY()) {
                n5 = -1;
            }
            if (n3 > ((Entity)object).getPosition().getY()) {
                n5 = 1;
            }
            Object object2 = object;
            ((Player)object2).packetSender.queueRelativeMovementStep(n4, n5, true);
            ((Entity)object).getUpdateState().setAnimation(794);
            object2 = object;
            ((Player)object2).packetSender.sendSoundEffect(393, 1, 10);
            ObjectManager.getInstance();
            DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n2, n3, 0);
            int n6 = dynamicObject.orientation;
            ObjectManager.getInstance().removeDynamicObjectAt(n2, n3, 0, 10);
            new DynamicObject(n + 8, n2, n3, 0, n6, 10, ServerSettings.placeholderObjectId, 2, false);
            Iterator iterator = balloonRewards.iterator();
            while (iterator.hasNext()) {
                int n7;
                PartyRoomBalloonReward partyRoomBalloonReward = (PartyRoomBalloonReward)iterator.next();
                if (partyRoomBalloonReward == null) continue;
                object2 = partyRoomBalloonReward;
                if (((PartyRoomBalloonReward)object2).rewardItem == null) continue;
                object2 = partyRoomBalloonReward;
                if (((PartyRoomBalloonReward)object2).balloonPosition == null) continue;
                object2 = partyRoomBalloonReward;
                if (((PartyRoomBalloonReward)object2).balloonPosition.getX() != n2) continue;
                object2 = partyRoomBalloonReward;
                if (((PartyRoomBalloonReward)object2).balloonPosition.getY() != n3) continue;
                object2 = partyRoomBalloonReward;
                int n8 = ((PartyRoomBalloonReward)object2).rewardItem.getId();
                object2 = partyRoomBalloonReward;
                if (((PartyRoomBalloonReward)object2).rewardItem.getAmount() <= 0) {
                    n7 = 1;
                } else {
                    object2 = partyRoomBalloonReward;
                    n7 = ((PartyRoomBalloonReward)object2).rewardItem.getAmount();
                }
                object2 = partyRoomBalloonReward;
                GroundItem groundItem = new GroundItem(new ItemStack(n8, n7), (Entity)object, ((PartyRoomBalloonReward)object2).balloonPosition);
                GroundItemManager.getInstance().spawn(groundItem);
                iterator.remove();
                break;
            }
            return true;
        }
        return false;
    }

    public static void openPartyChest(Player player) {
        player.getPartyRoomContainer().clear();
        PartyRoomManager.refreshPartyChestInterface(player);
        player.packetSender.showInterfaceWithInventory(2156, 2005);
    }

    public static void refreshOpenPartyChestInterfaces() {
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null && player.getOpenInterfaceId() == 2156) {
                PartyRoomManager.refreshPartyChestInterface(player);
            }
            ++n2;
        }
    }

    public static void stageInventoryItemForChest(Player player, int n, int n2, int n3) {
        boolean bl;
        if (n2 == -1) {
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
        int n5 = player.getPartyRoomContainer().getItemAmount(n2);
        boolean bl2 = bl = n5 <= 0 || !itemStack.getDefinition().isStackable();
        if (balloonDropPending) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("Drop party is already starting, no more items are accepted!");
            return;
        }
        if (player.getPartyRoomContainer().getFreeSlots() <= 0 && bl) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("You can't deposit more items at once!");
            return;
        }
        if (new ItemStack(n2).getDefinition().isUntradeable()) {
            Player player4 = player;
            player4.packetSender.sendGameMessage("You cannot deposit that item.");
            return;
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
        if (!bl) {
            player.getPartyRoomContainer().setItem(player.getPartyRoomContainer().indexOfItem(itemStack.getId()), new ItemStack(n2, n5 + n4));
        } else {
            ItemStack itemStack2 = new ItemStack(itemStack.getId(), n4);
            ItemContainer itemContainer = player.getPartyRoomContainer();
            itemContainer.add(itemStack2, -1);
        }
        PartyRoomManager.refreshPartyChestInterface(player);
    }

    public static void withdrawStagedChestItem(Player player, int n, int n2, int n3) {
        if (n2 == -1) {
            return;
        }
        ItemStack itemStack = player.getPartyRoomContainer().getItemAt(n);
        int n4 = player.getPartyRoomContainer().getItemAmount(n2);
        if (itemStack == null || itemStack.getId() != n2 || n3 <= 0) {
            return;
        }
        if (n4 > n3) {
            n4 = n3;
        }
        n = player.getPartyRoomContainer().removeFromSlot(new ItemStack(n2, n4), n);
        player.getInventoryManager().addItem(new ItemStack(itemStack.getId(), n));
        PartyRoomManager.refreshPartyChestInterface(player);
    }

    private static void refreshPartyChestInterface(Player player) {
        Player player2 = player;
        player2.packetSender.sendItemContainer(2006, player.getInventoryManager().getContainer().getRawItems());
        player2 = player;
        player2.packetSender.sendItemContainer(2274, player.getPartyRoomContainer().getRawItems());
        player2 = player;
        player2.packetSender.sendItemContainer(2273, partyChestContainer.getRawItems());
        String string = "Party Drop Chest";
        if (partyChestValue >= 1000) {
            string = String.valueOf(string) + " - Value: " + GameUtil.formatCompactAmount(partyChestValue);
        }
        player2 = player;
        player2.packetSender.sendInterfaceText(string, 2248);
    }

    public static void returnStagedChestItems(Player player) {
        int n = 0;
        while (n < 8) {
            ItemStack itemStack;
            if (player.getPartyRoomContainer().getItemAt(n) != null && (itemStack = player.getPartyRoomContainer().getItemAt(n)) != null) {
                player.getPartyRoomContainer().remove(itemStack);
                player.getInventoryManager().addItem(itemStack);
            }
            ++n;
        }
        player.getPartyRoomContainer().clear();
    }

    public static void depositStagedItemsToChest(Player player) {
        int n = 8 - player.getPartyRoomContainer().getFreeSlots();
        int n2 = partyChestContainer.getFreeSlots();
        if (balloonDropPending) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("Drop party is already starting, no more items are accepted!");
            return;
        }
        if (n <= 0) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("Add some items first!");
            return;
        }
        if (n2 <= 0) {
            Player player4 = player;
            player4.packetSender.sendGameMessage("Chest is already full!");
            return;
        }
        if (n > n2) {
            Player player5 = player;
            player5.packetSender.sendGameMessage("You can only add: " + n2 + " items to the chest!");
            return;
        }
        n = 0;
        while (n < 8) {
            ItemStack itemStack;
            if (player.getPartyRoomContainer().getItemAt(n) != null && (itemStack = player.getPartyRoomContainer().getItemAt(n)) != null) {
                player.getPartyRoomContainer().remove(itemStack);
                ItemStack itemStack2 = itemStack;
                ItemContainer itemContainer = partyChestContainer;
                itemContainer.add(itemStack2, -1);
                int n3 = itemStack.getDefinition().getShopValue();
                if (n3 > 0) {
                    partyChestValue += itemStack.getAmount() * n3;
                }
            }
            ++n;
        }
        player.getPartyRoomContainer().clear();
        PartyRoomManager.savePartyChest();
        PartyRoomManager.refreshOpenPartyChestInterfaces();
    }

    public static void loadPartyChest() {
        Object object = new File("./data/partyChest.dat");
        try {
            object = new FileInputStream((File)object);
            DataInputStream dataInputStream = new DataInputStream((InputStream)object);
            try {
                int n = 0;
                while (n < partyChestCapacity) {
                    int n2 = dataInputStream.readInt();
                    if (n2 != 65535) {
                        int n3 = dataInputStream.readInt();
                        int n4 = dataInputStream.readInt();
                        ItemStack itemStack = new ItemStack(n2, n3, n4);
                        partyChestContainer.setItem(n, itemStack);
                        n3 = itemStack.getDefinition().getShopValue();
                        if (n3 > 0) {
                            partyChestValue += itemStack.getAmount() * n3;
                        }
                    }
                    ++n;
                }
            }
            catch (Exception exception) {}
            dataInputStream.close();
            ((FileInputStream)object).close();
            return;
        }
        catch (IOException iOException) {
            return;
        }
    }

    public static void savePartyChest() {
        Object object = new File("./data/partyChest.dat");
        ((File)object).delete();
        try {
            object = new CountingDataOutputStream(new FileOutputStream("./data/partyChest.dat"));
            int n = 0;
            while (n < partyChestCapacity) {
                ItemStack itemStack = partyChestContainer.getItemAt(n);
                if (itemStack == null) {
                    ((CountingDataOutputStream)object).writeInt(65535);
                } else {
                    ((CountingDataOutputStream)object).writeInt(itemStack.getId());
                    ((CountingDataOutputStream)object).writeInt(itemStack.getAmount());
                    ((CountingDataOutputStream)object).writeInt(itemStack.getMetadata());
                }
                ++n;
            }
            ((FilterOutputStream)object).close();
            return;
        }
        catch (Exception exception) {
            return;
        }
    }
}

