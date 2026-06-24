/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.action;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;

public final class DyeMixingHandler {
    private static int redDyeId = 1763;
    private static int yellowDyeId = 1765;
    private static int blueDyeId = 1767;
    private static int orangeDyeId = 1769;
    private static int greenDyeId = 1771;
    private static int purpleDyeId = 1773;

    public static boolean mixDyes(Player player, int n, int n2) {
        boolean bl;
        if (n == redDyeId && n2 == yellowDyeId || n == yellowDyeId && n2 == redDyeId) {
            player.getInventoryManager().removeItem(new ItemStack(n, 1));
            player.getInventoryManager().removeItem(new ItemStack(n2, 1));
            player.getInventoryManager().addItem(new ItemStack(orangeDyeId, 1));
            player.packetSender.sendGameMessage("You mix the two dyes and make an orange one.");
            bl = true;
        } else if (n == blueDyeId && n2 == yellowDyeId || n == yellowDyeId && n2 == blueDyeId) {
            player.getInventoryManager().removeItem(new ItemStack(n, 1));
            player.getInventoryManager().removeItem(new ItemStack(n2, 1));
            player.getInventoryManager().addItem(new ItemStack(greenDyeId, 1));
            player.packetSender.sendGameMessage("You mix the two dyes and make a green one.");
            bl = true;
        } else if (n == blueDyeId && n2 == redDyeId || n == redDyeId && n2 == blueDyeId) {
            player.getInventoryManager().removeItem(new ItemStack(n, 1));
            player.getInventoryManager().removeItem(new ItemStack(n2, 1));
            player.getInventoryManager().addItem(new ItemStack(purpleDyeId, 1));
            player.packetSender.sendGameMessage("You mix the two dyes and make a purple one.");
            bl = true;
        } else {
            bl = false;
        }
        return bl;
    }
}

