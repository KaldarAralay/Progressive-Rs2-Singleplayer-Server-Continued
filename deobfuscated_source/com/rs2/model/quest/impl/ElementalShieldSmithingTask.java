/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.ElementalWorkshopQuest;
import com.rs2.model.task.TickTask;

public final class ElementalShieldSmithingTask
extends TickTask {
    private /* synthetic */ ElementalWorkshopQuest quest;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int questStateAtStart;

    public ElementalShieldSmithingTask(ElementalWorkshopQuest elementalWorkshopQuest, int n, Player player, int n2) {
        super(3);
        this.quest = elementalWorkshopQuest;
        this.player = player;
        this.questStateAtStart = n2;
    }

    @Override
    public final void execute() {
        this.player.getInventoryManager().replaceItem(new ItemStack(2893, 1), new ItemStack(2890, 1));
        if (this.questStateAtStart != 1) {
            this.quest.awardCompletionRewards(this.player);
        }
        this.stop();
    }
}

