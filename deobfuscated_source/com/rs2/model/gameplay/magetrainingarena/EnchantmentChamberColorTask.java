/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.magetrainingarena;

import com.rs2.model.World;
import com.rs2.model.gameplay.magetrainingarena.EnchantmentChamberController;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class EnchantmentChamberColorTask
extends TickTask {
    public EnchantmentChamberColorTask(int n) {
        super(40);
    }

    @Override
    public final void execute() {
        EnchantmentChamberController.setCurrentBonusColor(EnchantmentChamberController.getBonusColorNames()[EnchantmentChamberController.random.nextInt(EnchantmentChamberController.getBonusColorNames().length)]);
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null && player.getEnchantmentChamberController().isInsideChamber()) {
                player.getEnchantmentChamberController().refreshBonusColorIndicator(EnchantmentChamberController.getCurrentBonusColor());
            }
            ++n2;
        }
        if (EnchantmentChamberController.findEnchantmentGuardianNpc() != null) {
            EnchantmentChamberController.findEnchantmentGuardianNpc().getUpdateState().setForcedTextAndMarkUpdated("The color shape is now " + EnchantmentChamberController.getCurrentBonusColor() + "!");
        }
    }
}

