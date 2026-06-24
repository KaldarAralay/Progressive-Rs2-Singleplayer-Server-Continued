/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.WitchsHouseQuest;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class WitchsHouseGardenEjectEvent
extends CycleEvent {
    private int a = 6;
    private final /* synthetic */ Player player;

    public WitchsHouseGardenEjectEvent(WitchsHouseQuest witchsHouseQuest, Player player) {
        this.player = player;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        --this.a;
        if (!this.player.isDead()) {
            if (this.a == 3) {
                if (this.player.getInventoryManager().containsItem(2407)) {
                    this.player.getInventoryManager().removeItem(new ItemStack(2407, 1));
                }
                this.player.moveTo(new Position(2917, 3456, 0));
            }
        } else {
            this.a = 0;
        }
        if (this.a <= 0) {
            cycleEventContainer.stop();
        }
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
        this.player.getAttributes().put("canTakeDamage", Boolean.TRUE);
    }
}

