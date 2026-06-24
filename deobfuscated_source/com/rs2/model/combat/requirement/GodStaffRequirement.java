/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.requirement;

import com.rs2.model.combat.attack.MagicCombatAttack;
import com.rs2.model.combat.requirement.EquipmentItemRequirement;

public final class GodStaffRequirement
extends EquipmentItemRequirement {
    public GodStaffRequirement(MagicCombatAttack magicCombatAttack, int n, int n2, int n3, boolean bl) {
        super(3, n2, 1, false);
    }

    @Override
    public final String getFailureMessage() {
        return "You must equip the proper god staff to use this spell!";
    }
}

