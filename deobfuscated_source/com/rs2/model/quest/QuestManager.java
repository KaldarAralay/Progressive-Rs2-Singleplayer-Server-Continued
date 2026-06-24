/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestEventRegistry;
import com.rs2.model.quest.QuestHook;
import com.rs2.model.quest.QuestScript;

public final class QuestManager {
    private Player player;

    public QuestManager(Player player) {
        this.player = player;
    }

    private static boolean isQuestJournalButtonAvailable(int n) {
        QuestDefinition questDefinition = QuestDefinition.forId(n);
        int n2 = questDefinition.getJournalButtonId();
        return n2 < InterfaceDefinition.interfaceCount;
    }

    public final boolean handleItemOnItem(int n, int n2) {
        QuestHook questHook;
        int n3 = 0;
        while (n3 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n3);
            if (questHook.getQuestId() != -1 && questHook.handleItemOnItem(this.player, n, n2, this.player.questHookStates[n3])) {
                return true;
            }
            ++n3;
        }
        n3 = 1;
        while (n3 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n3);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleItemOnItem(this.player, n, n2, this.player.getQuestState(n3)))) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public final boolean handleDropItem(int n) {
        QuestHook questHook;
        int n2 = 0;
        while (n2 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n2);
            if (questHook.getQuestId() != -1 && questHook.handleDropItem(this.player, n, this.player.questHookStates[n2])) {
                return true;
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n2);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleDropItem(this.player, n, this.player.getQuestState(n2)))) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public final boolean handleFirstNpcAction(int n) {
        QuestHook questHook;
        int n2 = 0;
        while (n2 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n2);
            if (questHook.getQuestId() != -1 && questHook.handleFirstNpcAction(this.player, n, this.player.questHookStates[n2])) {
                return true;
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n2);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleFirstNpcAction(this.player, n, this.player.getQuestState(n2)))) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public final boolean handleInventoryItemFirstOption(int n, int n2) {
        QuestHook questHook;
        int n3 = 0;
        while (n3 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n3);
            if (questHook.getQuestId() != -1 && questHook.handleInventoryItemFirstOption(this.player, n, n2, this.player.questHookStates[n3])) {
                return true;
            }
            ++n3;
        }
        n3 = 1;
        while (n3 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n3);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleInventoryItemFirstOption(this.player, n, n2, this.player.getQuestState(n3)))) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public final boolean handleFirstObjectAction(int n, int n2, int n3) {
        QuestHook questHook;
        int n4 = 0;
        while (n4 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n4);
            if (questHook.getQuestId() != -1 && questHook.handleFirstObjectAction(this.player, n, n2, n3, this.player.questHookStates[n4])) {
                return true;
            }
            ++n4;
        }
        n4 = 0;
        while (n4 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n4);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleFirstObjectAction(this.player, n, n2, n3, this.player.getQuestState(n4)))) {
                return true;
            }
            ++n4;
        }
        return false;
    }

    public final boolean handleSecondObjectAction(int n, int n2, int n3) {
        QuestHook questHook;
        int n4 = 0;
        while (n4 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n4);
            if (questHook.getQuestId() != -1 && questHook.handleSecondObjectAction(this.player, n, n2, n3, this.player.questHookStates[n4])) {
                return true;
            }
            ++n4;
        }
        n4 = 0;
        while (n4 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n4);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleSecondObjectAction(this.player, n, n2, n3, this.player.getQuestState(n4)))) {
                return true;
            }
            ++n4;
        }
        return false;
    }

    public final boolean handleGroundItemInteraction(int n) {
        QuestHook questHook;
        int n2 = 0;
        while (n2 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n2);
            if (questHook.getQuestId() != -1 && questHook.handleGroundItemInteraction(this.player, n, this.player.questHookStates[n2])) {
                return true;
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n2);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleGroundItemInteraction(this.player, n, this.player.getQuestState(n2)))) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public final boolean handleItemOnObject(int n, int n2) {
        QuestHook questHook;
        int n3 = 0;
        while (n3 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n3);
            if (questHook.getQuestId() != -1 && questHook.handleItemOnObject(this.player, n, n2, this.player.questHookStates[n3])) {
                return true;
            }
            ++n3;
        }
        n3 = 1;
        while (n3 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n3);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleItemOnObject(this.player, n, n2, this.player.getQuestState(n3)))) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public final boolean handleItemOnNpc(int n, int n2) {
        QuestHook questHook;
        int n3 = 0;
        while (n3 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n3);
            if (questHook.getQuestId() != -1 && questHook.handleItemOnNpc(this.player, n, n2, this.player.questHookStates[n3])) {
                return true;
            }
            ++n3;
        }
        n3 = 1;
        while (n3 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n3);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleItemOnNpc(this.player, n, n2, this.player.getQuestState(n3)))) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public final boolean canAttackNpc(int n) {
        QuestHook questHook;
        int n2 = 0;
        while (n2 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n2);
            if (questHook.getQuestId() != -1 && !questHook.canAttackNpc(this.player, n, this.player.questHookStates[n2])) {
                return false;
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n2);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || questHook.canAttackNpc(this.player, n, this.player.getQuestState(n2)))) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    public final boolean handleMovementStep() {
        QuestHook questHook;
        int n = 0;
        while (n < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n);
            if (questHook.getQuestId() != -1 && questHook.handleMovementStep(this.player, this.player.questHookStates[n])) {
                return true;
            }
            ++n;
        }
        n = 1;
        while (n < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleMovementStep(this.player, this.player.getQuestState(n)))) {
                return true;
            }
            ++n;
        }
        return false;
    }

    public final boolean handleCombatDeath(Entity entity, Entity entity2) {
        QuestHook questHook;
        int n = 0;
        while (n < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n);
            if (questHook.getQuestId() != -1 && questHook.handleCombatDeath(entity, entity2, this.player.questHookStates[n])) {
                return true;
            }
            ++n;
        }
        n = 1;
        while (n < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleCombatDeath(entity, entity2, this.player.getQuestState(n)))) {
                return true;
            }
            ++n;
        }
        return false;
    }

    public final int getQuestDamageOverride(Entity entity, Entity entity2) {
        QuestHook questHook;
        int n = 0;
        while (n < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n);
            if (questHook.getQuestId() != -1 && questHook.getQuestDamageOverride(entity, entity2, this.player.questHookStates[n]) != -1) {
                return questHook.getQuestDamageOverride(entity, entity2, this.player.questHookStates[n]);
            }
            ++n;
        }
        n = 1;
        while (n < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || questHook.getQuestDamageOverride(entity, entity2, this.player.getQuestState(n)) == -1)) {
                return questHook.getQuestDamageOverride(entity, entity2, this.player.getQuestState(n));
            }
            ++n;
        }
        return -1;
    }

    public final boolean handleNpcKill(int n) {
        QuestHook questHook;
        int n2 = 0;
        while (n2 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n2);
            if (questHook.getQuestId() != -1 && questHook.handleNpcKill(this.player, n, this.player.questHookStates[n2])) {
                return true;
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n2);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleNpcKill(this.player, n, this.player.getQuestState(n2)))) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public final void refreshQuestJournal() {
        QuestScript questScript = QuestDefinition.getQuestScript(0);
        questScript.refreshQuestJournal(this.player, this.player.getQuestState(0));
    }

    public final boolean handleNpcDeathDrop(int n, Player player, Position position) {
        QuestHook questHook;
        int n2 = 0;
        while (n2 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n2);
            if (questHook.getQuestId() != -1 && questHook.handleNpcDeathDrop(player, n, position, this.player.questHookStates[n2])) {
                return true;
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n2);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleNpcDeathDrop(player, n, position, this.player.getQuestState(n2)))) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public final boolean handleContextDialogue(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        QuestHook questHook;
        n5 = 0;
        while (n5 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n5);
            if (questHook.getQuestId() != -1 && questHook.handleContextDialogue(n, this.player, n2, n3, n4, n6, n7, this.player.questHookStates[n5])) {
                return true;
            }
            ++n5;
        }
        n5 = 0;
        while (n5 < QuestDefinition.questCount) {
            QuestHook questHook2;
            questHook = QuestDefinition.getQuestScript(n5);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questHook2 = questHook).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleContextDialogue(n, this.player, n2, n3, n4, n6, n7, this.player.getQuestState(n5)))) {
                return true;
            }
            ++n5;
        }
        return false;
    }

    public final boolean handleNpcDialogue(int n, int n2, int n3, int n4) {
        QuestHook questHook;
        n4 = 0;
        while (n4 < QuestEventRegistry.eventHookCount) {
            questHook = QuestEventRegistry.getEventHook(n4);
            if (questHook.getQuestId() != -1 && questHook.handleNpcDialogue(this.player, n, n2, n3, this.player.questHookStates[n4])) {
                return true;
            }
            ++n4;
        }
        n4 = 0;
        while (n4 < QuestDefinition.questCount) {
            Object object;
            questHook = QuestDefinition.getQuestScript(n4);
            if (!(questHook.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questHook.getQuestId()) || QuestDefinition.forId(((QuestHook)(object = questHook)).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId(((QuestHook)(object = questHook)).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questHook.handleNpcDialogue(this.player, n, n2, n3, this.player.getQuestState(n4)))) {
                if (this.player.getQuestState(questHook.getQuestId()) == 0 && questHook.getQuestId() >= 105 && !this.player.hasMemberFlag()) {
                    object = this.player;
                    ((Player)object).packetSender.closeInterfaces();
                    Player player = this.player;
                    object = player;
                    object = questHook;
                    player.packetSender.sendGameMessage("You need a donator rank to start this quest: " + QuestDefinition.forId(((QuestHook)object).getQuestId()).getName());
                    return false;
                }
                return true;
            }
            ++n4;
        }
        return false;
    }

    public final boolean refreshQuestJournalStatuses() {
        int n = 0;
        while (n < QuestDefinition.questCount) {
            QuestScript questScript;
            QuestScript questScript2 = QuestDefinition.getQuestScript(n);
            if (!(questScript2.getQuestId() == -1 || !QuestManager.isQuestJournalButtonAvailable(questScript2.getQuestId()) || QuestDefinition.forId((questScript = questScript2).getQuestId()).isMembersOnly() && !this.player.isMember() || QuestDefinition.forId((questScript = questScript2).getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld)) {
                questScript2.refreshQuestJournalStatus(this.player, this.player.getQuestState(n));
            }
            ++n;
        }
        return false;
    }

    public final void refreshQuestPointText() {
        Player player = this.player;
        player.packetSender.sendInterfaceText("QP: " + this.player.getQuestPoints(), 3985);
    }

    public final boolean handleButtonClick(int n) {
        boolean bl;
        block10: {
            int n2;
            int n3;
            if (ServerSettings.cacheVersion > 245) {
                n3 = 1;
                while (n3 < QuestDefinition.questCount) {
                    QuestDefinition questDefinition = QuestDefinition.forId(n3);
                    String string = questDefinition.getName();
                    n2 = questDefinition.getJournalButtonId();
                    if (n == n2) {
                        QuestManager questManager = this;
                        Player player = questManager.player;
                        player.packetSender.sendInterfaceText("", 8145);
                        n2 = 8147;
                        while (n2 <= 8195) {
                            player = questManager.player;
                            player.packetSender.sendInterfaceText("", n2);
                            ++n2;
                        }
                        n2 = 12174;
                        while (n2 <= 12223) {
                            player = questManager.player;
                            player.packetSender.sendInterfaceText("", n2);
                            ++n2;
                        }
                        player = this.player;
                        player.packetSender.sendInterfaceText(string, 8144);
                        this.player.getQuestPoints();
                        n2 = n3;
                        questManager = this;
                        QuestScript questScript = QuestDefinition.getQuestScript(n2);
                        String[] stringArray = questScript.buildQuestJournal(questManager.player, questManager.player.getQuestState(n2));
                        player = questManager.player;
                        player.packetSender.sendInterfaceText(stringArray[0], 8145);
                        int n4 = 1;
                        while (n4 < stringArray.length) {
                            player = questManager.player;
                            player.packetSender.sendInterfaceText(stringArray[n4], n4 + 8146);
                            ++n4;
                        }
                        player = this.player;
                        player.packetSender.showInterface(8134);
                        return true;
                    }
                    ++n3;
                }
            }
            n2 = n;
            QuestManager questManager = this;
            n3 = 1;
            while (n3 < QuestDefinition.questCount) {
                QuestScript questScript = QuestDefinition.getQuestScript(n3);
                if (questScript.getQuestId() != -1 && QuestManager.isQuestJournalButtonAvailable(questScript.getQuestId())) {
                    if (!QuestDefinition.forId(questScript.getQuestId()).isMembersOnly() || questManager.player.isMember()) {
                        if (!(QuestDefinition.forId(questScript.getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questScript.handleButtonClick(questManager.player, n2, questManager.player.getQuestState(n3)))) {
                            bl = true;
                            break block10;
                        }
                    }
                }
                ++n3;
            }
            bl = false;
        }
        return bl;
    }

}

