/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.hit;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.animation.GraphicEffect;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.combat.AmmunitionDefinition;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.combat.ProjectileTiming;
import com.rs2.model.combat.effect.CombatEffect;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.item.ItemStack;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class HitDefinition {
    private int a;
    private int impactSoundId;
    private int maxDamage;
    private int minimumDamage = -1;
    private int blockAnimationId;
    private HitType hitType;
    private AttackStyleDefinition attackStyle;
    private GraphicEffect graphic;
    private ProjectileDefinition projectile;
    private int delay;
    private int specialEffectId;
    private boolean accuracyCheckEnabled;
    private boolean randomDamageEnabled;
    private boolean alwaysHits;
    private byte o;
    private boolean multiTargetSpreadEnabled;
    private boolean blockAnimationEnabled;
    private boolean protectionPrayerReductionDeferred = false;
    private SpellDefinition spell;
    private ArrayList chainedTargets = new ArrayList();
    private Entity chainedSource;
    private AmmunitionDefinition ammunition;
    private List effects = new LinkedList();
    private ItemStack droppedAmmunition;
    private int y;
    private double accuracyMultiplier = 1.0;

    public final HitDefinition setChainedTargets(ArrayList arrayList) {
        this.chainedTargets = arrayList;
        return this;
    }

    public final ArrayList getChainedTargets() {
        return this.chainedTargets;
    }

    public final HitDefinition setChainedSource(Entity entity) {
        this.chainedSource = entity;
        return this;
    }

    public final Entity getChainedSource() {
        return this.chainedSource;
    }

    public final void setAmmunition(AmmunitionDefinition ammunitionDefinition) {
        this.ammunition = ammunitionDefinition;
    }

    public final AmmunitionDefinition getAmmunition() {
        return this.ammunition;
    }

    public HitDefinition(AttackStyleDefinition attackStyleDefinition, HitType hitType, int n) {
        this.attackStyle = attackStyleDefinition;
        this.hitType = hitType;
        this.maxDamage = n;
        this.a = -1;
        this.impactSoundId = -1;
        this.graphic = null;
        this.delay = 0;
        this.y = 0;
        this.alwaysHits = false;
        this.multiTargetSpreadEnabled = true;
        this.blockAnimationEnabled = true;
    }

    public HitDefinition(AttackStyleDefinition attackStyleDefinition, HitType hitType, int n, boolean bl) {
        this.attackStyle = attackStyleDefinition;
        this.hitType = hitType;
        this.maxDamage = n;
        this.a = -1;
        this.impactSoundId = -1;
        this.graphic = null;
        this.delay = 0;
        this.y = 0;
        this.alwaysHits = false;
        this.multiTargetSpreadEnabled = true;
        this.blockAnimationEnabled = true;
        this.protectionPrayerReductionDeferred = bl;
    }

    public final HitDefinition copy() {
        HitDefinition hitDefinition = new HitDefinition(this.attackStyle, this.hitType, this.maxDamage);
        hitDefinition.a = this.a;
        hitDefinition.graphic = this.graphic;
        hitDefinition.impactSoundId = this.impactSoundId;
        hitDefinition.projectile = this.projectile;
        hitDefinition.delay = this.delay;
        hitDefinition.droppedAmmunition = this.droppedAmmunition;
        hitDefinition.y = this.y;
        hitDefinition.o = this.o;
        hitDefinition.blockAnimationId = this.blockAnimationId;
        hitDefinition.alwaysHits = this.alwaysHits;
        hitDefinition.multiTargetSpreadEnabled = this.multiTargetSpreadEnabled;
        hitDefinition.blockAnimationEnabled = this.blockAnimationEnabled;
        hitDefinition.minimumDamage = this.minimumDamage;
        hitDefinition.protectionPrayerReductionDeferred = this.protectionPrayerReductionDeferred;
        hitDefinition.specialEffectId = this.specialEffectId;
        hitDefinition.spell = this.spell;
        hitDefinition.ammunition = this.ammunition;
        hitDefinition.chainedSource = this.chainedSource;
        hitDefinition.chainedTargets = new ArrayList(this.chainedTargets);
        HitDefinition hitDefinition2 = this;
        if (hitDefinition2.accuracyCheckEnabled) {
            hitDefinition2 = this;
            hitDefinition.setAccuracyMultiplier(hitDefinition2.accuracyMultiplier);
        }
        hitDefinition2 = this;
        if (hitDefinition2.randomDamageEnabled) {
            hitDefinition.enableRandomDamage();
        }
        hitDefinition2 = this;
        if (hitDefinition2.effects != null) {
            hitDefinition2 = this;
            if (hitDefinition2.effects.size() > 0) {
                CombatEffect[] effects = (CombatEffect[])this.effects.toArray(new CombatEffect[this.effects.size()]);
                hitDefinition.addEffects(effects);
            }
        }
        return hitDefinition;
    }

    public HitDefinition(AttackStyleDefinition attackStyleDefinition, HitType hitType, double d) {
        this(attackStyleDefinition, hitType, (int)Math.floor(d));
    }

    public final boolean isProtectionPrayerReductionDeferred() {
        return this.protectionPrayerReductionDeferred;
    }

    public final HitDefinition addEffects(CombatEffect[] object) {
        if (object != null) {
            CombatEffect[] combatEffectArray = object;
            int n = object.length;
            int n2 = 0;
            while (n2 < n) {
                CombatEffect combatEffect = combatEffectArray[n2];
                if (combatEffect != null) {
                    this.effects.add(combatEffect);
                }
                ++n2;
            }
        }
        return this;
    }

    public final HitDefinition addEffect(CombatEffect combatEffect) {
        this.effects.add(combatEffect);
        return this;
    }

    public final List getEffects() {
        return this.effects;
    }

    public final HitDefinition clearEffects() {
        this.effects = null;
        return this;
    }

    public final HitDefinition enableRandomDamage() {
        if (this.maxDamage == -1) {
            return this;
        }
        boolean bl = true;
        HitDefinition hitDefinition = this;
        this.randomDamageEnabled = true;
        return this;
    }

    public final HitDefinition setAccuracyMultiplier(double d) {
        if (this.attackStyle == null) {
            return this;
        }
        boolean bl = true;
        HitDefinition hitDefinition = this;
        this.accuracyCheckEnabled = true;
        this.accuracyCheckEnabled = true;
        double d2 = d;
        HitDefinition hitDefinition2 = this;
        this.accuracyMultiplier = d2;
        return this;
    }

    public final HitDefinition setSpecialEffectId(int n) {
        this.specialEffectId = n;
        return this;
    }

    public final HitDefinition setDroppedAmmunition(ItemStack itemStack) {
        this.droppedAmmunition = itemStack;
        return this;
    }

    public final HitDefinition enableAccuracyCheck() {
        return this.setAccuracyMultiplier(1.0);
    }

    public final HitDefinition setAlwaysHits(boolean bl) {
        this.alwaysHits = bl;
        return this;
    }

    public final boolean isAlwaysHit() {
        return this.alwaysHits;
    }

    public final HitDefinition setGraphic(GraphicEffect graphicEffect) {
        this.graphic = graphicEffect;
        return this;
    }

    public final HitDefinition setImpactSoundId(int n) {
        this.impactSoundId = n;
        return this;
    }

    public final HitDefinition setDelay(int n) {
        this.delay = n;
        return this;
    }

    public final HitDefinition setProjectile(ProjectileDefinition projectileDefinition) {
        this.projectile = projectileDefinition;
        return this;
    }

    public final AttackStyleDefinition getAttackStyle() {
        return this.attackStyle;
    }

    public final ProjectileDefinition getProjectile() {
        return this.projectile;
    }

    public final GraphicEffect getGraphic() {
        return this.graphic;
    }

    public final int getImpactSoundId() {
        return this.impactSoundId;
    }

    public final ItemStack getDroppedAmmunition() {
        return this.droppedAmmunition;
    }

    public final int p() {
        return this.a;
    }

    public final int getMaxDamage() {
        return this.maxDamage;
    }

    public final int getMinimumDamage() {
        return this.minimumDamage;
    }

    public final HitDefinition setMinimumDamage(int n) {
        this.minimumDamage = n;
        return this;
    }

    public final HitType getHitType() {
        return this.hitType;
    }

    public final boolean isAccuracyCheckEnabled() {
        return this.accuracyCheckEnabled;
    }

    public final int getSpecialEffectId() {
        return this.specialEffectId;
    }

    public final int calculateDelay(Position position, Position object) {
        int n = this.delay;
        if (position != null && this.projectile != null) {
            int n2 = GameUtil.getDistance(position, object);
            ProjectileTiming timing = this.projectile.getTiming();
            double d = (double)(timing.getStartDelay() + timing.getSpeed()) + (double)n2 * 5.0;
            d = Math.ceil(d * 12.0 / 600.0);
            if (n2 > 1) {
                d += 1.0;
            }
            n += (int)d;
        } else {
            ++n;
        }
        return n;
    }

    public final void setMaxDamage(int n) {
        this.maxDamage = n;
    }

    public final double getAccuracyMultiplier() {
        return this.accuracyMultiplier;
    }

    public final HitDefinition enableAccuracyCheck(boolean bl) {
        this.accuracyCheckEnabled = true;
        return this;
    }

    public final boolean isRandomDamageEnabled() {
        return this.randomDamageEnabled;
    }

    public final HitDefinition b(double d) {
        this.maxDamage = (int)((double)this.maxDamage * 0.75);
        return this;
    }

    public final HitDefinition setSpell(SpellDefinition spellDefinition) {
        this.spell = spellDefinition;
        return this;
    }

    public final SpellDefinition getSpell() {
        return this.spell;
    }

    public final HitDefinition setBlockAnimationId(int n) {
        this.blockAnimationId = 734;
        return this;
    }

    public final int getBlockAnimationId() {
        return this.blockAnimationId;
    }

    public final HitDefinition setMultiTargetSpreadEnabled(boolean bl) {
        this.multiTargetSpreadEnabled = false;
        return this;
    }

    public final boolean isMultiTargetSpreadEnabled() {
        return this.multiTargetSpreadEnabled;
    }

    public final HitDefinition setBlockAnimationEnabled(boolean bl) {
        this.blockAnimationEnabled = false;
        return this;
    }

    public final boolean isBlockAnimationEnabled() {
        return this.blockAnimationEnabled;
    }

    public final /* synthetic */ Object clone() {
        return this.copy();
    }
}

