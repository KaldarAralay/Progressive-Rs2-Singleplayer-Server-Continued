/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.attack;

import com.rs2.model.Entity;
import com.rs2.model.combat.AttackBonusType;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.combat.AttackXpMode;
import com.rs2.model.combat.CombatType;
import com.rs2.model.combat.attack.BaseCombatAttack;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.hit.HitType;

public final class MeleeCombatAttack
extends BaseCombatAttack {
    private final /* synthetic */ AttackXpMode xpMode;
    private final /* synthetic */ AttackBonusType attackBonusType;
    private final /* synthetic */ int maxHit;
    private final /* synthetic */ int animationId;
    private final /* synthetic */ int attackDelay;

    public MeleeCombatAttack(Entity entity, Entity entity2, AttackXpMode attackXpMode, AttackBonusType attackBonusType, int n, int n2, int n3) {
        super(entity, entity2);
        this.xpMode = attackXpMode;
        this.attackBonusType = attackBonusType;
        this.maxHit = n;
        this.animationId = n2;
        this.attackDelay = n3;
    }

    @Override
    public final int getAttackRange() {
        return 1;
    }

    @Override
    public final CombatType getCombatType() {
        return CombatType.MELEE;
    }

    @Override
    public final void prepare() {
        this.setHitDefinitions(new HitDefinition[]{new HitDefinition(new AttackStyleDefinition(CombatType.MELEE, this.xpMode, this.attackBonusType), HitType.NORMAL, this.maxHit).enableAccuracyCheck().enableRandomDamage()});
        this.setAnimationId(this.animationId);
        this.setAttackDelay(this.attackDelay);
    }
}

