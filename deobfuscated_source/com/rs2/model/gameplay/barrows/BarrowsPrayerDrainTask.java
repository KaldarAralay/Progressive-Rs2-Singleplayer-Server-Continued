/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.barrows;

import com.rs2.model.World;
import com.rs2.model.gameplay.barrows.BarrowsManager;
import com.rs2.model.gameplay.barrows.BarrowsPrayerDrainResetTask;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class BarrowsPrayerDrainTask
extends TickTask {
    private final /* synthetic */ Player player;

    public BarrowsPrayerDrainTask(int n, Player player) {
        super(30);
        this.player = player;
    }

    @Override
    public final void execute() {
        if (!this.player.isRegistered()) {
            this.stop();
            return;
        }
        if (this.player.isInBarrows()) {
            int n = BarrowsManager.prayerDrainModelItemIds[GameUtil.randomInt(12)];
            int n2 = BarrowsManager.prayerDrainModelInterfaceIds[GameUtil.randomInt(6)];
            Player player = this.player;
            player.packetSender.sendInterfaceModel(n2, 100, n);
            player = this.player;
            player.packetSender.sendInterfaceAnimation(n2, 2085);
            n = BarrowsManager.countKilledBrothers(this.player);
            int[] nArray = this.player.getSkillManager().getCurrentLevels();
            nArray[5] = nArray[5] - (n += 8);
            if (this.player.getSkillManager().getCurrentLevels()[5] < 0) {
                this.player.getSkillManager().getCurrentLevels()[5] = 0;
            }
            this.player.getSkillManager().refreshSkill(5);
            BarrowsPrayerDrainResetTask barrowsPrayerDrainResetTask = new BarrowsPrayerDrainResetTask(this, 3, this.player, n2);
            World.getTaskScheduler().schedule(barrowsPrayerDrainResetTask);
            return;
        }
        this.player.eq = -1;
        this.stop();
    }
}

