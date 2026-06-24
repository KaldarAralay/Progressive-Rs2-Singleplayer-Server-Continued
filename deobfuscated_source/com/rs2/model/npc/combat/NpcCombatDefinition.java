/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc.combat;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.c.aM;
import com.rs2.model.combat.AttackBonusType;
import com.rs2.model.combat.attack.CombatAttack;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.npc.combat.DefaultNpcCombatDefinition;
import java.util.HashMap;
import java.util.Map;

public abstract class NpcCombatDefinition {
    private static final Map definitionsByNpcId = new HashMap();
    private int deathDelayTicks = 6;
    private int respawnDelayTicks = 30;
    private boolean attackBonusesSet;
    private boolean defenceBonusesSet;
    private Map combatBonuses = new HashMap();
    private aM g = null;

    public abstract CombatAttack[] createAttacks(Entity var1, Entity var2);

    public NpcCombatDefinition() {
        AttackBonusType[] attackBonusTypeArray = AttackBonusType.values();
        int n = attackBonusTypeArray.length;
        int n2 = 0;
        while (n2 < n) {
            AttackBonusType attackBonusType = attackBonusTypeArray[n2];
            this.combatBonuses.put(attackBonusType.getIndex(), 0);
            this.combatBonuses.put(attackBonusType.getIndex() + AttackBonusType.values().length, 0);
            ++n2;
        }
    }

    public final NpcCombatDefinition addAttackBonuses(int n, int n2, int n3, int n4, int n5) {
        this.combatBonuses.put(0, (Integer)this.combatBonuses.get(0) + n);
        this.combatBonuses.put(1, (Integer)this.combatBonuses.get(1) + n2);
        this.combatBonuses.put(2, (Integer)this.combatBonuses.get(2) + n3);
        this.combatBonuses.put(3, (Integer)this.combatBonuses.get(3) + n4);
        this.combatBonuses.put(4, (Integer)this.combatBonuses.get(4) + n5);
        n = 1;
        NpcCombatDefinition npcCombatDefinition = this;
        this.attackBonusesSet = true;
        return this;
    }

    public final NpcCombatDefinition addDefenceBonuses(int n, int n2, int n3, int n4, int n5) {
        this.combatBonuses.put(5, (Integer)this.combatBonuses.get(5) + n);
        this.combatBonuses.put(6, (Integer)this.combatBonuses.get(6) + n2);
        this.combatBonuses.put(7, (Integer)this.combatBonuses.get(7) + n3);
        this.combatBonuses.put(8, (Integer)this.combatBonuses.get(8) + n4);
        this.combatBonuses.put(9, (Integer)this.combatBonuses.get(9) + n5);
        n = 1;
        NpcCombatDefinition npcCombatDefinition = this;
        this.defenceBonusesSet = true;
        return this;
    }

    public final NpcCombatDefinition setRespawnDelayTicks(int n) {
        this.respawnDelayTicks = n > 0 ? n : 30;
        return this;
    }

    public final NpcCombatDefinition setRespawnDelaySeconds(int n) {
        return this.setRespawnDelayTicks((int)Math.ceil((double)n * 1000.0 / 600.0));
    }

    public final int getDeathDelayTicks() {
        return this.deathDelayTicks;
    }

    public final int getRespawnDelayTicks() {
        return this.respawnDelayTicks;
    }

    public final Map getCombatBonuses() {
        return this.combatBonuses;
    }

    public static void register(int[] nArray, NpcCombatDefinition npcCombatDefinition) {
        int[] nArray2 = nArray;
        int n = nArray.length;
        int n2 = 0;
        while (n2 < n) {
            int n3 = nArray2[n2];
            NpcDefinition npcDefinition = NpcDefinition.forId(n3);
            if (npcDefinition != null && npcDefinition.respawnDelaySeconds > 0) {
                double d;
                double d2 = npcDefinition.getCombatLevel();
                if (d2 <= 0.0) {
                    npcCombatDefinition.respawnDelayTicks = npcDefinition.respawnDelaySeconds;
                } else {
                    double d3;
                    if (d2 > 443.0) {
                        d2 = 443.0;
                    }
                    if (ServerSettings.modernCombatSystemEnabled) {
                        d3 = npcDefinition.getRespawnDelayTicks();
                    } else {
                        double d4 = Math.pow(d2, 2.0);
                        double d5 = d4 * -4.0E-4;
                        double d6 = d2 * 0.3547;
                        d3 = d5 + d6 + 24.646;
                    }
                    npcCombatDefinition.respawnDelayTicks = (int)(d3 *= ServerSettings.npcRespawnDelayMultiplier);
                }
            }
            definitionsByNpcId.put(n3, npcCombatDefinition);
            ++n2;
        }
    }

    public static boolean isRegistered(int n) {
        NpcCombatDefinition npcCombatDefinition = (NpcCombatDefinition)definitionsByNpcId.get(n);
        return npcCombatDefinition != null;
    }

    public static NpcCombatDefinition forNpcId(int n) {
        NpcCombatDefinition npcCombatDefinition = (NpcCombatDefinition)definitionsByNpcId.get(n);
        if (npcCombatDefinition == null) {
            npcCombatDefinition = new DefaultNpcCombatDefinition();
            definitionsByNpcId.put(n, npcCombatDefinition);
        }
        return npcCombatDefinition;
    }

    public final boolean hasAttackBonuses() {
        return this.attackBonusesSet;
    }

    public final boolean hasDefenceBonuses() {
        return this.defenceBonusesSet;
    }
}

