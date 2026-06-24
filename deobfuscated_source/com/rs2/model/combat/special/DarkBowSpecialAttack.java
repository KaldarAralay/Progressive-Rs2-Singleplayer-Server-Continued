/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.special;

import com.rs2.model.Entity;
import com.rs2.model.animation.GraphicEffect;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.combat.AmmunitionDefinition;
import com.rs2.model.combat.AmmunitionProfile;
import com.rs2.model.combat.ProjectileTiming;
import com.rs2.model.combat.WeaponProfile;
import com.rs2.model.combat.attack.WeaponCombatAttack;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.combat.requirement.CombatRequirement;
import com.rs2.model.combat.special.DarkBowArrowRequirement;
import com.rs2.model.combat.special.DarkBowSpecialDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;

public final class DarkBowSpecialAttack
extends WeaponCombatAttack {
    private final /* synthetic */ WeaponProfile sourceWeaponProfile;
    private final /* synthetic */ Player player;

    public DarkBowSpecialAttack(DarkBowSpecialDefinition darkBowSpecialDefinition, Player player, Entity entity, WeaponProfile weaponProfile, WeaponProfile weaponProfile2, Player player2) {
        super(player, entity, weaponProfile);
        this.sourceWeaponProfile = weaponProfile2;
        this.player = player2;
    }

    @Override
    public final boolean prepareSpecialAttack() {
        int n;
        int n2;
        if (!super.prepareSpecialAttack()) {
            return false;
        }
        Object object = this.getAmmunition();
        Object object2 = this.sourceWeaponProfile.getAmmunitionProfile();
        if (object == null || object2 == null) {
            return false;
        }
        ItemStack itemStack = this.player.isPlayer() ? this.player.getEquipmentManager().getContainer().getItemAt(((AmmunitionProfile)((Object)object2)).getEquipmentSlot()) : null;
        if (itemStack == null) {
            return false;
        }
        this.setRequirements(new CombatRequirement[]{new DarkBowArrowRequirement(this, ((AmmunitionProfile)((Object)object2)).getEquipmentSlot(), itemStack.getId(), 2, true)});
        object2 = ((AmmunitionProfile)((Object)object2)).getProjectileTiming();
        double d = this.calculateMaxHit();
        if (object == AmmunitionDefinition.DRAGON_ARROW) {
            n2 = 1100;
            n = 8;
            object = new ProjectileDefinition(1099, ((ProjectileTiming)object2).copy().setStartDelay(40).setSpeed(3));
            object2 = new ProjectileDefinition(1099, ((ProjectileTiming)object2).copy().setStartDelay(41).setSpeed(2));
            d *= 1.5;
            if (d >= 48.0) {
                d = 48.0;
            }
        } else {
            n2 = 1103;
            n = 5;
            object = new ProjectileDefinition(1101, ((ProjectileTiming)object2).copy().setStartDelay(40).setSpeed(3));
            object2 = new ProjectileDefinition(1101, ((ProjectileTiming)object2).copy().setStartDelay(41).setSpeed(2));
            d *= 1.3;
        }
        GraphicEffect graphicEffect = new GraphicEffect(n2, 96);
        this.setAnimationId(426);
        this.setAttackSoundId(3731);
        this.setAttackerGraphic(new GraphicEffect(this.getAmmunition().getAlternateGraphicId(), 90));
        this.setHitDefinitions(new HitDefinition[]{new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setAccuracyMultiplier(1.1).setProjectile((ProjectileDefinition)object).enableAccuracyCheck(true).setMinimumDamage(n), new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setAccuracyMultiplier(1.1).setProjectile((ProjectileDefinition)object2).enableAccuracyCheck(true).setMinimumDamage(n).setGraphic(graphicEffect)});
        return true;
    }
}

