/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import com.rs2.model.Position;
import com.rs2.model.clue.CoordinateClue;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import java.util.Random;

public final class CoordinateClueHandler {
    private static Position COORDINATE_ORIGIN = new Position(2440, 3161, 0);

    public static boolean showCoordinateClue(Player player, int n) {
        CoordinateClue coordinateClue = CoordinateClue.forClueItemId(n);
        if (coordinateClue == null) {
            return false;
        }
        Player player2 = player;
        player2.packetSender.showInterface(6965);
        player2 = player;
        player2.packetSender.sendInterfaceText(String.valueOf(CoordinateClueHandler.formatTwoDigits(coordinateClue.getLatitudeDegrees())) + " degrees " + CoordinateClueHandler.formatTwoDigits(coordinateClue.getLatitudeMinutes()) + " minutes " + coordinateClue.getLatitudeDirection(), 6971);
        player2 = player;
        player2.packetSender.sendInterfaceText(String.valueOf(CoordinateClueHandler.formatTwoDigits(coordinateClue.getLongitudeDegrees())) + " degrees " + CoordinateClueHandler.formatTwoDigits(coordinateClue.getLongitudeMinutes()) + " minutes " + coordinateClue.getLongitudeDirection(), 6972);
        return true;
    }

    public static boolean digAtCoordinateClue(Player player) {
        CoordinateClue coordinateClue = CoordinateClue.forPosition(new Position(player.getPosition().getX(), player.getPosition().getY()));
        if (coordinateClue == null) {
            return false;
        }
        if (!player.getInventoryManager().containsItem(coordinateClue.getClueItemId())) {
            return false;
        }
        if (!(player.getInventoryManager().getContainer().containsItem(2576) && player.getInventoryManager().getContainer().containsItem(2574) && player.getInventoryManager().getContainer().containsItem(2575))) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("You need a chart, sextant, and watch in order to find the clue.");
            return true;
        }
        Player player3 = player;
        if (!player3.killedClueAttacker && coordinateClue.getLevel() == 3) {
            TreasureTrailManager.spawnClueWizard(player);
            player3 = player;
            player3.packetSender.sendGameMessage("You must kill the wizard before continuing the search!");
            return true;
        }
        boolean bl = false;
        player3 = player;
        player.killedClueAttacker = bl;
        player.getInventoryManager().removeItem(new ItemStack(coordinateClue.getClueItemId(), 1));
        switch (coordinateClue.getLevel()) {
            case 1: {
                player.getInventoryManager().addOrDropItem(new ItemStack(2724, 1));
                break;
            }
            case 2: {
                player.getInventoryManager().addOrDropItem(new ItemStack(2726, 1));
                break;
            }
            case 3: {
                player.getInventoryManager().addOrDropItem(new ItemStack(2728, 1));
            }
        }
        player.getDialogueManager().showItemIdMessage("You've found a casket!", 2724);
        return true;
    }

    private static String formatTwoDigits(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return String.valueOf(n);
    }

    public static Position resolvePosition(int n, int n2, int n3, int n4, String string, String string2) {
        int n5 = COORDINATE_ORIGIN.getX();
        int n6 = COORDINATE_ORIGIN.getY();
        if (string == "north") {
            n6 += (int)Math.ceil((double)(n * 60 + n2) / 1.875);
        }
        if (string == "south") {
            n6 -= (int)Math.ceil((double)(n * 60 + n2) / 1.875);
        }
        if (string == "east") {
            n5 += (int)Math.ceil((double)(n * 60 + n2) / 1.875);
        }
        if (string == "west") {
            n5 -= (int)Math.ceil((double)(n * 60 + n2) / 1.875);
        }
        if (string2 == "north") {
            n6 += (int)Math.ceil((double)(n3 * 60 + n4) / 1.875);
        }
        if (string2 == "south") {
            n6 -= (int)Math.ceil((double)(n3 * 60 + n4) / 1.875);
        }
        if (string2 == "east") {
            n5 += (int)Math.ceil((double)(n3 * 60 + n4) / 1.875);
        }
        if (string2 == "west") {
            n5 -= (int)Math.ceil((double)(n3 * 60 + n4) / 1.875);
        }
        return new Position(n5, n6);
    }

    public static String[] formatPositionAsCoordinate(int n, int n2) {
        int n3 = COORDINATE_ORIGIN.getX();
        int n4 = COORDINATE_ORIGIN.getY();
        double d = (double)Math.abs(n -= n3) * 1.875;
        double d2 = (double)Math.abs(n2 -= n4) * 1.875;
        n3 = (int)d % 60;
        n4 = (int)d2 % 60;
        int n5 = (int)(d / 60.0);
        int n6 = (int)(d2 / 60.0);
        String string = n < 0 ? "west" : "east";
        String string2 = n2 < 0 ? "south" : "north";
        return new String[]{String.valueOf(n6) + " degrees, " + n4 + " minutes " + string2, String.valueOf(n5) + " degrees, " + n3 + " minutes " + string};
    }

    public static int randomClueItemForLevel(int n) {
        int n2 = new Random().nextInt(CoordinateClue.values().length);
        while (CoordinateClue.values()[n2].getLevel() != n) {
            n2 = new Random().nextInt(CoordinateClue.values().length);
        }
        return CoordinateClue.values()[n2].getClueItemId();
    }
}

