/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.tasks;

import com.rs2.bot.tasks.AlKharidCowhideTanningBotTask;
import com.rs2.model.GameplayHelper;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;

public final class TanningBotTickTask
extends TickTask {
    private final /* synthetic */ Player player;

    public TanningBotTickTask(AlKharidCowhideTanningBotTask alKharidCowhideTanningBotTask, int n, Player player) {
        super(2);
        this.player = player;
    }

    @Override
    public final void execute() {
        if (this.player.isDead() || !this.player.isRegistered() || !this.player.currentBotTask.usesCustomTaskAction || !this.player.botTaskState.equals("do task")) {
            this.stop();
            return;
        }
        if (!this.player.getInventoryManager().containsItem(this.player.botTaskItemId)) {
            this.player.currentBotTask.startWalkToBank(this.player);
            this.stop();
            return;
        }
        if (this.player.getOpenInterfaceId() == 14670 || this.player.getOpenInterfaceId() == 679) {
            Player player = this.player;
            int n = player.botTaskItemId;
            int n2 = 1;
            int n3 = 1;
            if (n == 1739) {
                if (player.getSkillManager().getCurrentLevels()[12] < 28) {
                    n2 = 1;
                    n3 = 1741;
                } else {
                    n2 = 3;
                    n3 = 1743;
                }
            } else if (n == 1753) {
                n2 = 20;
                n3 = 1745;
            } else if (n == 1751) {
                n2 = 20;
                n3 = 2505;
            } else if (n == 1749) {
                n2 = 20;
                n3 = 2507;
            } else if (n == 1747) {
                n2 = 20;
                n3 = 2509;
            }
            GameplayHelper.tanHide(player, 27, n2, n, n3);
            return;
        }
        this.player.botInteractionOption = 2;
        this.player.interactWithBotNpcTargets(this.player.botInteractionTargetIds);
    }
}

