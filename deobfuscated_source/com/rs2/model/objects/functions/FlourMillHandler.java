/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects.functions;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;

public final class FlourMillHandler {
    public static int flourBinConfigId = 695;

    public static void addGrainToHopper(Player player) {
        Player player2 = player;
        int n = player.ep[flourBinConfigId] + player2.flourMillHopperGrainCount;
        if (n > 30) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("The hopper or bin is already full.");
            return;
        }
        if (!player.getInventoryManager().containsItem(1947)) {
            return;
        }
        player.getInventoryManager().removeItem(new ItemStack(1947, 1));
        player.getUpdateState().setAnimation(832);
        Player player4 = player;
        int n2 = player4.flourMillHopperGrainCount + 1;
        player4 = player;
        player.flourMillHopperGrainCount = n2;
        player4 = player;
        player4.packetSender.sendGameMessage("You place the grain into the hopper.");
        player4 = player;
        if (player4.flourMillHopperGrainCount < 30) {
            Player player5 = player;
            player4 = player5;
            player4 = player;
            player5.packetSender.sendGameMessage("The hopper is now holding " + player4.flourMillHopperGrainCount + " pieces of grain.");
            return;
        }
        player4 = player;
        player4.packetSender.sendGameMessage("The hopper is now full with 30 pieces of grain.");
    }

    public static void operateHopperControls(Player player) {
        Player player2 = player;
        if (player2.flourMillHopperGrainCount <= 0) {
            player2 = player;
            player2.packetSender.sendGameMessage("There is no grain in the hopper.");
            return;
        }
        if (player.ep[flourBinConfigId] > 29) {
            player2 = player;
            player2.packetSender.sendGameMessage("The grain bin is already full.");
            return;
        }
        player2 = player;
        int n = player.ep[flourBinConfigId] + player2.flourMillHopperGrainCount;
        player2 = player;
        player2.packetSender.sendConfig(flourBinConfigId, n);
        player.ep[FlourMillHandler.flourBinConfigId] = n;
        n = 0;
        player2 = player;
        player.flourMillHopperGrainCount = n;
        player.getUpdateState().setAnimation(832);
        player2 = player;
        player2.packetSender.sendGameMessage("The grain in the hopper slides down the chute.");
    }

    public static void collectFlourFromBin(Player player) {
        if (player.ep[flourBinConfigId] <= 0) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("There is no grain in the bin.");
            return;
        }
        if (!player.getInventoryManager().containsItem(1931)) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("You need an empty pot in order to take flour from here.");
            return;
        }
        if (player.ep[flourBinConfigId] > 30) {
            return;
        }
        player.getUpdateState().setAnimation(832);
        player.getInventoryManager().removeItem(new ItemStack(1931, 1));
        player.getInventoryManager().addItem(new ItemStack(1933, 1));
        int n = flourBinConfigId;
        player.ep[n] = player.ep[n] - 1;
        Player player4 = player;
        player4.packetSender.sendConfig(flourBinConfigId, player.ep[flourBinConfigId]);
        player4 = player;
        player4.packetSender.sendGameMessage("You take some flour from the bin.");
        if (player.ep[flourBinConfigId] > 0) {
            player4 = player;
            player4.packetSender.sendGameMessage("There is enough grain left in the bin to fill " + player.ep[flourBinConfigId] + " pot" + (player.ep[flourBinConfigId] > 1 ? "s" : "") + ".");
            return;
        }
        player4 = player;
        player4.packetSender.sendGameMessage("The grain bin is now empty.");
    }
}

