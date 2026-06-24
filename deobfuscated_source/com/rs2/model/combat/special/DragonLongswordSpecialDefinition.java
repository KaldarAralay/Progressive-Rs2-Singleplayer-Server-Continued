/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.special;

import com.rs2.model.Entity;
import com.rs2.model.combat.WeaponProfile;
import com.rs2.model.combat.attack.WeaponCombatAttack;
import com.rs2.model.combat.special.DragonLongswordSpecialAttack;
import com.rs2.model.combat.special.SpecialAttackDefinition;
import com.rs2.model.player.Player;

public final class DragonLongswordSpecialDefinition
extends SpecialAttackDefinition {
    public DragonLongswordSpecialDefinition(int n2, String ... stringArray) {
        super(n2, stringArray);
    }

    @Override
    public final WeaponCombatAttack createAttack(Player player, Entity entity, WeaponProfile weaponProfile) {
        return new DragonLongswordSpecialAttack(this, player, entity, weaponProfile);
    }
}

