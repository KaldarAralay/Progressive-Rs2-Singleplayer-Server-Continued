/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestHook;
import com.rs2.util.GameplayTrace;
import java.awt.Color;

public abstract class QuestScript
extends QuestHook {
    private int questPointReward;

    public QuestScript(int n) {
        super(n);
    }

    public String[] buildQuestJournal(Player player, int n) {
        return new String[]{"Quest not added yet!"};
    }

    public final int getQuestPointReward() {
        return this.questPointReward;
    }

    public final void setQuestPointReward(int n) {
        this.questPointReward = n;
    }

    public final void markQuestComplete(Player player) {
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("quest complete player=" + GameplayTrace.describe(player) + " questId=" + this.getQuestId() + " questName=" + QuestDefinition.forId(this.getQuestId()).getName());
        }
        player.deferLevelUpInterfaces = true;
        boolean bl = player.isMember();
        Player player2 = player;
        Object object = player2;
        object = this;
        player2.packetSender.sendInterfaceTextColor(QuestDefinition.forId(((QuestHook)object).getQuestId()).getJournalButtonId(), Color.GREEN);
        player.setQuestState(this.getQuestId(), 1);
        object = player;
        ((Player)object).packetSender.sendGameMessage("Congratulations! Quest Complete!");
        player.getQuestManager().refreshQuestPointText();
        if (ServerSettings.membershipRequirementMode == 3 && !bl && player.isMember()) {
            player.packetSender.sendGameMessage("You have reached " + ServerSettings.membershipRequirementValue + " quest points and gained access to members content!");
        }
        if (ServerSettings.completeMissingQuestsMode == 2) {
            object = player;
            if (((Player)object).packetSender.updateMissingQuestCompletionStates()) {
                player.packetSender.sendGameMessage("You have completed all quests!");
            }
        }
    }

    public final void showQuestCompleteInterface(Player player) {
        player.packetSender.sendInterfacePosition(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 0, InterfaceDefinition.interfaceCount <= 12140 ? -40 : 0);
        player.packetSender.sendInterfaceText("You have completed " + QuestDefinition.forId(this.getQuestId()).getName() + "!", InterfaceDefinition.interfaceCount <= 12140 ? 6160 : 12144);
        player.packetSender.sendMusicJingle(238, 320);
        player.packetSender.sendInterfaceText("" + player.getQuestPoints(), InterfaceDefinition.interfaceCount <= 12140 ? 1696 : 12147);
        if (InterfaceDefinition.interfaceCount <= 12140) {
            player.packetSender.sendInterfaceText("" + this.questPointReward, 6164);
        }
    }

    public void awardCompletionRewards(Player player) {
    }

    public final void startQuest(Player player) {
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("quest start player=" + GameplayTrace.describe(player) + " questId=" + this.getQuestId() + " questName=" + QuestDefinition.forId(this.getQuestId()).getName());
        }
        Player player2 = player;
        Object object = player2;
        object = this;
        player2.packetSender.sendInterfaceTextColor(QuestDefinition.forId(((QuestHook)object).getQuestId()).getJournalButtonId(), Color.YELLOW);
        player.setQuestState(this.getQuestId(), 2);
    }
}

