/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.special;

import com.rs2.model.Entity;
import com.rs2.model.World;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.combat.AttackValidationResult;
import com.rs2.model.combat.CombatCycleEvent;
import com.rs2.model.combat.ProjectileTiming;
import com.rs2.model.combat.WeaponProfile;
import com.rs2.model.combat.attack.WeaponCombatAttack;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.combat.special.RuneThrownaxeSpecialDefinition;
import com.rs2.model.combat.special.RuneThrownaxeTargetDistanceComparator;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.Collections;

public final class RuneThrownaxeSpecialAttack
extends WeaponCombatAttack {
    private final /* synthetic */ Player player;
    private final /* synthetic */ Entity primaryTarget;
    private final /* synthetic */ WeaponProfile sourceWeaponProfile;

    public RuneThrownaxeSpecialAttack(RuneThrownaxeSpecialDefinition runeThrownaxeSpecialDefinition, Player player, Entity entity, WeaponProfile weaponProfile, Player player2, Entity entity2, WeaponProfile weaponProfile2) {
        super(player, entity, weaponProfile);
        this.player = player2;
        this.primaryTarget = entity2;
        this.sourceWeaponProfile = weaponProfile2;
    }

    @Override
    public final boolean prepareSpecialAttack() {
        if (!super.prepareSpecialAttack()) {
            return false;
        }
        ArrayList chainedTargets = new ArrayList();
        if (this.player.isInMultiCombatArea() && this.primaryTarget.isInMultiCombatArea()) {
            Player[] players = World.getPlayers();
            int n = 0;
            while (n < players.length) {
                Player nearbyPlayer = players[n];
                if (nearbyPlayer != null && nearbyPlayer != this.getAttacker() && nearbyPlayer != this.getTarget() && nearbyPlayer.isInMultiCombatArea() && GameUtil.isWithinDistance(this.getTarget().getPosition(), nearbyPlayer.getPosition(), 3)) {
                    chainedTargets.add(nearbyPlayer);
                }
                ++n;
            }
            Npc[] npcs = World.getNpcs();
            n = 0;
            while (n < npcs.length) {
                Npc nearbyNpc = npcs[n];
                if (nearbyNpc != null && nearbyNpc != this.getTarget() && nearbyNpc.isInMultiCombatArea() && GameUtil.isWithinDistance(this.getTarget().getPosition(), nearbyNpc.getPosition(), 3)) {
                    chainedTargets.add(nearbyNpc);
                }
                ++n;
            }
            Collections.sort(chainedTargets, new RuneThrownaxeTargetDistanceComparator(this));
        }
        ArrayList validTargets = new ArrayList();
        if (chainedTargets.size() > 5) {
            int n = 0;
            while (n < chainedTargets.size()) {
                Entity entity = (Entity)chainedTargets.get(n);
                AttackValidationResult attackValidationResult = CombatCycleEvent.validateAttack(this.player, entity);
                boolean doorSupportNpc = false;
                if (entity.isNpc()) {
                    Npc npc = (Npc)entity;
                    doorSupportNpc = npc.isDoorSupportNpc();
                }
                if ((attackValidationResult == AttackValidationResult.VALID || doorSupportNpc) && validTargets.size() < 5) {
                    validTargets.add(entity);
                    if (validTargets.size() == 5) break;
                }
                ++n;
            }
        } else {
            validTargets = chainedTargets;
        }
        double d = this.calculateMaxHit();
        ProjectileTiming projectileTiming = this.sourceWeaponProfile.getAmmunitionProfile().getProjectileTiming();
        ProjectileDefinition projectileDefinition = new ProjectileDefinition(258, projectileTiming.copy());
        this.setHitDefinitions(new HitDefinition[]{new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setProjectile(projectileDefinition).setAccuracyMultiplier(1.0).enableAccuracyCheck(true).setChainedTargets(validTargets).setChainedSource(this.player)});
        return true;
    }

}

