/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.attack;

import com.rs2.model.Entity;
import com.rs2.model.animation.GraphicEffect;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.combat.AttackBonusType;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.combat.AttackXpMode;
import com.rs2.model.combat.CombatType;
import com.rs2.model.combat.ProjectileTiming;
import com.rs2.model.combat.attack.BaseCombatAttack;
import com.rs2.model.combat.effect.CombatEffect;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.hit.HitType;

public final class ForcedProjectileCombatAttack
extends BaseCombatAttack {
    private final /* synthetic */ AttackXpMode xpMode;
    private final /* synthetic */ CombatType combatType;
    private final /* synthetic */ ProjectileTiming projectileTiming;
    private final /* synthetic */ int projectileId;
    private final /* synthetic */ boolean alwaysHits;
    private final /* synthetic */ int maxHit;
    private final /* synthetic */ int hitDelay;
    private final /* synthetic */ GraphicEffect hitGraphic;
    private final /* synthetic */ CombatEffect combatEffect;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ int attackDelay;
    private final /* synthetic */ GraphicEffect attackerGraphic;

    public ForcedProjectileCombatAttack(Entity entity, Entity entity2, AttackXpMode attackXpMode, CombatType combatType, ProjectileTiming projectileTiming, int n, boolean bl, int n2, int n3, GraphicEffect graphicEffect, CombatEffect combatEffect, int n4, int n5, GraphicEffect graphicEffect2) {
        super(entity, entity2);
        this.xpMode = attackXpMode;
        this.combatType = combatType;
        this.projectileTiming = projectileTiming;
        this.projectileId = n;
        this.alwaysHits = bl;
        this.maxHit = n2;
        this.hitDelay = n3;
        this.hitGraphic = graphicEffect;
        this.combatEffect = combatEffect;
        this.animationId = n4;
        this.attackDelay = n5;
        this.attackerGraphic = graphicEffect2;
    }

    @Override
    public final int getAttackRange() {
        if (this.xpMode == AttackXpMode.DRAGONFIRE) {
            return 1;
        }
        if (this.xpMode == AttackXpMode.ICY_BREATH) {
            return 4;
        }
        if (this.xpMode == AttackXpMode.DRAGONFIRE_FAR) {
            return 8;
        }
        if (this.xpMode == AttackXpMode.KBD_SPECIAL) {
            return 8;
        }
        if (this.combatType == CombatType.MAGIC || this.xpMode == AttackXpMode.MELEE_FAR) {
            return 10;
        }
        if (this.xpMode == AttackXpMode.LONGRANGE) {
            return 10;
        }
        return 8;
    }

    @Override
    public final CombatType getCombatType() {
        return this.combatType;
    }

    @Override
    public final void prepare() {
        ProjectileDefinition projectileDefinition;
        ProjectileDefinition projectileDefinition2 = projectileDefinition = this.projectileTiming != null && this.projectileId != -1 ? new ProjectileDefinition(this.projectileId, this.projectileTiming) : null;
        if (this.alwaysHits) {
            this.setHitDefinitions(new HitDefinition[]{new HitDefinition(new AttackStyleDefinition(this.combatType, this.xpMode, this.combatType == CombatType.RANGED ? AttackBonusType.RANGED : AttackBonusType.MAGIC), HitType.NORMAL, this.maxHit).setProjectile(projectileDefinition).setDelay(this.hitDelay).setGraphic(this.hitGraphic).enableRandomDamage().setAlwaysHits(this.alwaysHits)});
        } else {
            this.setHitDefinitions(new HitDefinition[]{new HitDefinition(new AttackStyleDefinition(this.combatType, this.xpMode, this.combatType == CombatType.RANGED ? AttackBonusType.RANGED : AttackBonusType.MAGIC), HitType.NORMAL, this.maxHit).setProjectile(projectileDefinition).setDelay(this.hitDelay).setGraphic(this.hitGraphic).enableAccuracyCheck().enableRandomDamage().setAlwaysHits(this.alwaysHits)});
        }
        if (this.combatEffect != null) {
            this.addEffect(this.combatEffect);
        }
        this.setAnimationId(this.animationId);
        this.setAttackDelay(this.attackDelay);
        this.setAttackerGraphic(this.attackerGraphic);
    }
}

