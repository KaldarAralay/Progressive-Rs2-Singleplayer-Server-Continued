/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.magetrainingarena;

import com.rs2.model.World;
import com.rs2.model.gameplay.magetrainingarena.AlchemistPlaygroundController;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class AlchemistPlaygroundRotationTask
extends TickTask {
    public AlchemistPlaygroundRotationTask(int n) {
        super(70);
    }

    @Override
    public final void execute() {
        AlchemistPlaygroundController.rotateCupboardObjectIds();
        AlchemistPlaygroundController.randomizeAlchemyItemValues();
        int n = AlchemistPlaygroundController.getAlchemyItemIds()[AlchemistPlaygroundController.random.nextInt(AlchemistPlaygroundController.getAlchemyItemIds().length)];
        while (n == AlchemistPlaygroundController.currentFreeAlchemyItemId) {
            n = AlchemistPlaygroundController.getAlchemyItemIds()[AlchemistPlaygroundController.random.nextInt(AlchemistPlaygroundController.getAlchemyItemIds().length)];
        }
        AlchemistPlaygroundController.currentFreeAlchemyItemId = n;
        Player[] playerArray = World.getPlayers();
        int n2 = playerArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null && player.getAlchemistPlaygroundController().isInsidePlayground()) {
                player.getAlchemistPlaygroundController().refreshFreeAlchemyItemIndicator();
            }
            ++n3;
        }
    }
}

