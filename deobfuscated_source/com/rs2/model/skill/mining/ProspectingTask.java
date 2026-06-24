/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.mining;

import com.rs2.model.player.Player;
import com.rs2.model.skill.mining.MiningManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameplayTrace;

public final class ProspectingTask
extends CycleEvent {
    private /* synthetic */ MiningManager manager;
    private final /* synthetic */ int rockObjectId;
    private final /* synthetic */ String oreName;

    public ProspectingTask(MiningManager miningManager, int n, String string) {
        this.manager = miningManager;
        this.rockObjectId = n;
        this.oreName = string;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        Player player = MiningManager.getPlayer(this.manager);
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("mining prospect complete player=" + GameplayTrace.describe(player) + " rockObjectId=" + this.rockObjectId + " oreName=" + this.oreName);
        }
        player.packetSender.sendGameMessage("This rock contains " + (this.rockObjectId == 2111 ? "gems" : (this.rockObjectId == 2491 ? "unbound Rune Stone essence" : String.valueOf(this.oreName) + ".")));
        if (MiningManager.getPlayer(this.manager).getQuestState(0) != 1) {
            if (MiningManager.getPlayer(this.manager).getQuestState(0) == 30 && this.oreName.contains("tin")) {
                MiningManager.getPlayer(this.manager).ea();
            } else if (MiningManager.getPlayer(this.manager).getQuestState(0) == 31 && this.oreName.contains("copper")) {
                MiningManager.getPlayer(this.manager).ea();
            }
            MiningManager.getPlayer(this.manager).getDialogueManager().showOneLineStatement("This rock contains " + (this.rockObjectId == 2111 ? "gems" : (this.rockObjectId == 2491 ? "unbound Rune Stone essence" : String.valueOf(this.oreName) + ".")));
            MiningManager.getPlayer(this.manager).getQuestManager().refreshQuestJournal();
            MiningManager.getPlayer(this.manager).setInteractionTargetId(0);
        }
        player = MiningManager.getPlayer(this.manager);
        player.packetSender.sendSoundEffect(431, 1, 0);
        MiningManager.getPlayer(this.manager).setActionLocked(false);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

