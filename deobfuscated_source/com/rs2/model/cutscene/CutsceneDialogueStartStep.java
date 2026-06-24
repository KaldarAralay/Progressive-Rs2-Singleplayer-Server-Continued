/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.cutscene;

import com.rs2.model.cutscene.Cutscene;
import com.rs2.model.cutscene.CutsceneStep;
import com.rs2.model.player.Player;

public final class CutsceneDialogueStartStep
extends CutsceneStep {
    private /* synthetic */ Cutscene cutscene;
    private final /* synthetic */ Player player;

    public CutsceneDialogueStartStep(Cutscene cutscene, Cutscene cutscene2, int n, Player player) {
        super(cutscene, 1);
        this.cutscene = cutscene2;
        this.player = player;
    }

    @Override
    public final void executeStep() {
        Player player = this.player;
        player.packetSender.closeInterfaces();
        this.cutscene.startDialogue();
    }
}

