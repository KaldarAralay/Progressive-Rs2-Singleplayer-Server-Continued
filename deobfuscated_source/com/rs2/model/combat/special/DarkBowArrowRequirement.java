/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.special;

import com.rs2.model.combat.requirement.EquipmentItemRequirement;
import com.rs2.model.combat.special.DarkBowSpecialAttack;

public final class DarkBowArrowRequirement
extends EquipmentItemRequirement {
    public DarkBowArrowRequirement(DarkBowSpecialAttack darkBowSpecialAttack, int n, int n2, int n3, boolean bl) {
        super(n, n2, 2, true);
    }

    @Override
    public final String getFailureMessage() {
        return "You do not have enough arrows!";
    }
}

