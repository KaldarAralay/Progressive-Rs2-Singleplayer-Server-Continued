/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.DragonSlayerQuest;
import com.rs2.model.task.TickTask;

public final class DragonSlayerShipHoleRepairTask
extends TickTask {
    private final /* synthetic */ int a;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int c;

    public DragonSlayerShipHoleRepairTask(DragonSlayerQuest dragonSlayerQuest, int n, int n2, Player player, int n3) {
        super(2);
        this.a = n2;
        this.player = player;
        this.c = n3;
    }

    @Override
    public final void execute() {
        int n = 2;
        if ((this.a & 8) == 0) {
            n = 8;
        }
        if ((this.a & 4) == 0) {
            n = 4;
        }
        if ((this.a & 2) == 0) {
            n = 2;
        }
        this.player.setActionLocked(false);
        this.player.getInventoryManager().removeItem(new ItemStack(960, 1));
        this.player.getInventoryManager().removeItem(new ItemStack(1539, 30));
        if (n == 2) {
            this.player.getDialogueManager().showTwoLineStatement("You nail a plank over the hole, but you still need more planks to", "close the hole completely.");
            this.player.addQuestState(this.c, n);
        }
        if (n == 4) {
            this.player.getDialogueManager().showTwoLineStatement("You nail a plank over the hole, but you still need one more plank to", "close the hole completely.");
            this.player.addQuestState(this.c, n);
        }
        if (n == 8) {
            this.player.moveTo(new Position(this.player.getPosition().getX(), this.player.getPosition().getY(), 2));
            this.player.getDialogueManager().showTwoLineStatement("You nail a final plank over the hole. You have successfully patched", "the hole in the ship.");
            this.player.addQuestState(this.c, n);
        }
        this.stop();
    }
}

