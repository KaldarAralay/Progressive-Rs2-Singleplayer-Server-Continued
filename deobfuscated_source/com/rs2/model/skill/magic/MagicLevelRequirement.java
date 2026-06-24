/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.combat.requirement.SkillLevelRequirement;
import com.rs2.model.skill.magic.MagicSpellAction;

public final class MagicLevelRequirement
extends SkillLevelRequirement {
    public MagicLevelRequirement(MagicSpellAction magicSpellAction, int n, int n2) {
        super(6, n2);
    }

    @Override
    public final String getFailureMessage() {
        return "Your level in magic is not high enough!";
    }
}

