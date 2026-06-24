/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.attack;

import com.rs2.model.Entity;
import com.rs2.model.combat.attack.CombatAttack;
import com.rs2.model.combat.attack.DefaultCombatAttackProvider;

public interface CombatAttackProvider {
    public static final CombatAttackProvider a = new DefaultCombatAttackProvider();

    public CombatAttack[] createAttacks(Entity var1, Entity var2);
}

