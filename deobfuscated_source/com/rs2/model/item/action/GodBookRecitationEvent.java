/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.action;

import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class GodBookRecitationEvent
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ String[] recitationLines;

    public GodBookRecitationEvent(Player player, String[] stringArray) {
        this.player = player;
        this.recitationLines = stringArray;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (this.player.O == this.recitationLines.length) {
            cycleEventContainer.stop();
            return;
        }
        this.player.getUpdateState().setForcedText(this.recitationLines[this.player.O]);
        ++this.player.O;
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

