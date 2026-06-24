/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.combat.ProjectileTiming;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;

public abstract class QuestHook {
    private int questId;
    private int eventType = 0;
    private boolean enabled = false;

    public QuestHook(int n) {
        this.questId = n;
    }

    public QuestHook(int n, int n2) {
        this.questId = n;
        this.eventType = n2;
    }

    public final int getQuestId() {
        return this.questId;
    }

    public final int getEventType() {
        return this.eventType;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final void setEnabled(boolean bl) {
        this.enabled = true;
    }

    public static int calculateProjectileTravelTicks(Position position, Position targetPosition) {
        int n = GameUtil.getDistance(position, targetPosition);
        ProjectileTiming projectileTiming = ProjectileTiming.d;
        double d = (double)(projectileTiming.getStartDelay() + projectileTiming.getSpeed()) + (double)n * 5.0;
        d = Math.ceil(d * 12.0 / 600.0);
        if (n > 1) {
            d += 1.0;
        }
        n = 0 + (int)d;
        return n;
    }

    public boolean handleNpcDialogue(Player player, int n, int n2, int n3, int n4) {
        return false;
    }

    public boolean handleContextDialogue(int n, Player player, int n2, int n3, int n4, int n5, int n6, int n7) {
        return false;
    }

    public boolean handleNpcDeathDrop(Player player, int n, Position position, int n2) {
        return false;
    }

    public boolean handleFirstObjectAction(Player player, int n, int n2, int n3, int n4) {
        return false;
    }

    public boolean handleSecondObjectAction(Player player, int n, int n2, int n3, int n4) {
        return false;
    }

    public boolean handleCombatDeath(Entity entity, Entity entity2, int n) {
        return false;
    }

    public int getQuestDamageOverride(Entity entity, Entity entity2, int n) {
        return -1;
    }

    public boolean handleNpcKill(Player player, int n, int n2) {
        return false;
    }

    public boolean handleMovementStep(Player player, int n) {
        return false;
    }

    public boolean canAttackNpc(Player player, int n, int n2) {
        return true;
    }

    public boolean refreshQuestJournalStatus(Player player, int n) {
        return false;
    }

    public void refreshQuestJournal(Player player, int n) {
    }

    public boolean handleGroundItemInteraction(Player player, int n, int n2) {
        return false;
    }

    public boolean handleItemOnObject(Player player, int n, int n2, int n3) {
        return false;
    }

    public boolean handleItemOnItem(Player player, int n, int n2, int n3) {
        return false;
    }

    public boolean handleDropItem(Player player, int n, int n2) {
        return false;
    }

    public boolean handleInventoryItemFirstOption(Player player, int n, int n2, int n3) {
        return false;
    }

    public boolean handleButtonClick(Player player, int n, int n2) {
        return false;
    }

    public boolean handleFirstNpcAction(Player player, int n, int n2) {
        return false;
    }

    public boolean handleItemOnNpc(Player player, int n, int n2, int n3) {
        return false;
    }

    public void initialize() {
    }
}

