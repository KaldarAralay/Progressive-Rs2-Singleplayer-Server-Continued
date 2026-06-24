/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.Position;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class DropGodCapeTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int capeAnimationId;

    public DropGodCapeTask(Player player, int n, Player player2, int n2) {
        super(4);
        this.player = player2;
        this.capeAnimationId = n2;
    }

    @Override
    public final void execute() {
        int n = this.player.getPosition().getX();
        int n2 = this.player.getPosition().getY() + 1;
        int n3 = 0;
        if (this.capeAnimationId == 2873) {
            n3 = 2412;
        } else if (this.capeAnimationId == 2874) {
            n3 = 2414;
        } else if (this.capeAnimationId == 2875) {
            n3 = 2413;
        }
        GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(n3, 1), this.player, new Position(n, n2, 0)));
        Player player = this.player;
        player.packetSender.sendStillGraphicToNearbyPlayers(86, n, n2, 0, 0);
        this.player.setActionLocked(false);
        DialogueManager.continueContextDialogue(1, this.player, this.capeAnimationId, 10, 0, this.player.getPosition().getX(), this.player.getPosition().getY());
        this.stop();
    }
}

