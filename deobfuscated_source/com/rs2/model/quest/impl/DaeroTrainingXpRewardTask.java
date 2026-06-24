/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.MonkeyMadnessQuest;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class DaeroTrainingXpRewardTask
extends TickTask {
    private /* synthetic */ MonkeyMadnessQuest quest;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int rewardChoice;
    private final /* synthetic */ int questFlagIndex;

    public DaeroTrainingXpRewardTask(MonkeyMadnessQuest monkeyMadnessQuest, int n, Player player, int n2, int n3) {
        super(10);
        this.quest = monkeyMadnessQuest;
        this.player = player;
        this.rewardChoice = n2;
        this.questFlagIndex = n3;
    }

    @Override
    public final void execute() {
        Player player = this.player;
        player.packetSender.showWalkableInterface(-1);
        player = this.player;
        player.packetSender.closeInterfaces();
        this.player.setActionLocked(false);
        if (this.rewardChoice == 1) {
            player = this.player;
            player.packetSender.sendGameMessage("You gain 35,000 Strength XP.");
            player = this.player;
            player.packetSender.sendGameMessage("You gain 35,000 Hitpoints XP.");
            player = this.player;
            player.packetSender.sendGameMessage("You gain 20,000 Attack XP.");
            player = this.player;
            player.packetSender.sendGameMessage("You gain 20,000 Defence XP.");
            this.player.getSkillManager().addQuestExperience(2, 35000.0);
            this.player.getSkillManager().addQuestExperience(3, 35000.0);
            this.player.getSkillManager().addQuestExperience(0, 20000.0);
            this.player.getSkillManager().addQuestExperience(1, 20000.0);
        } else {
            player = this.player;
            player.packetSender.sendGameMessage("You gain 35,000 Attack XP.");
            player = this.player;
            player.packetSender.sendGameMessage("You gain 35,000 Defence XP.");
            player = this.player;
            player.packetSender.sendGameMessage("You gain 20,000 Strength XP.");
            player = this.player;
            player.packetSender.sendGameMessage("You gain 20,000 Hitpoints XP.");
            this.player.getSkillManager().addQuestExperience(0, 35000.0);
            this.player.getSkillManager().addQuestExperience(1, 35000.0);
            this.player.getSkillManager().addQuestExperience(2, 20000.0);
            this.player.getSkillManager().addQuestExperience(3, 20000.0);
        }
        if (!this.quest.isProgressFlagSet(this.player, 17)) {
            int n = this.questFlagIndex;
            this.player.questProgressFlags[n] = this.player.questProgressFlags[n] + GameUtil.bitFlag(17);
        }
        this.stop();
    }
}

