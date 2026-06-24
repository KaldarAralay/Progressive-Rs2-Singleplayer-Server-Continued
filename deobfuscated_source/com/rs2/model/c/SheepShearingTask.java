/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.c;

import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class SheepShearingTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ Npc sheep;

    public SheepShearingTask(Player player, Npc npc) {
        this.player = player;
        this.sheep = npc;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!GameUtil.rollChance(0.75)) {
            Player player = this.player;
            player.packetSender.sendGameMessage("The sheep manages to get away!");
            int n = this.sheep.getSpawnMinPosition().getX();
            int n2 = this.sheep.getSpawnMinPosition().getY();
            int n3 = this.sheep.getSpawnMaxPosition().getX() - this.sheep.getSpawnMinPosition().getX();
            int n4 = this.sheep.getSpawnMaxPosition().getY() - this.sheep.getSpawnMinPosition().getY();
            n3 = GameUtil.getRandom().nextInt(n3);
            n4 = GameUtil.getRandom().nextInt(n4);
            Position position = new Position(n + n3, n2 + n4, this.sheep.getPosition().getPlane());
            this.sheep.queuePathTo(position, true);
            if (this.player.botEnabled) {
                this.player.interactWithBotNpcTargets(this.player.botInteractionTargetIds);
            }
            cycleEventContainer.stop();
            return;
        }
        this.player.getInventoryManager().addItem(new ItemStack(1737));
        this.sheep.getUpdateState().setForcedText("Baa!");
        Player player = this.player;
        player.packetSender.sendSoundEffect(1314, 1, 0);
        player = this.player;
        player.packetSender.sendGameMessage("You manage to shear the sheep.");
        this.sheep.transformToNpcId(42, 10);
        if (this.player.botEnabled) {
            this.player.interactWithBotNpcTargets(this.player.botInteractionTargetIds);
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

