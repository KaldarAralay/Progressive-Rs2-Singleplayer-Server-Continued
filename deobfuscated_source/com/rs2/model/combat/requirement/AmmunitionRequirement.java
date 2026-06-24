/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.requirement;

import com.rs2.model.combat.attack.WeaponCombatAttack;
import com.rs2.model.combat.requirement.EquipmentItemRequirement;

public final class AmmunitionRequirement
extends EquipmentItemRequirement {
    public AmmunitionRequirement(WeaponCombatAttack weaponCombatAttack, int n, int n2, int n3, boolean bl) {
        super(n, n2, n3, true);
    }

    @Override
    public final String getFailureMessage() {
        return "You have no ammo left!";
    }
}

