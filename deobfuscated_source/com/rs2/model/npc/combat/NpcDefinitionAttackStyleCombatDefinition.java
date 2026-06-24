/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc.combat;

import com.rs2.model.Entity;
import com.rs2.model.combat.AttackBonusType;
import com.rs2.model.combat.AttackXpMode;
import com.rs2.model.combat.attack.BaseCombatAttack;
import com.rs2.model.combat.attack.CombatAttack;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.npc.combat.NpcCombatDefinition;

public final class NpcDefinitionAttackStyleCombatDefinition
extends NpcCombatDefinition {
    private final /* synthetic */ NpcDefinition npcDefinition;

    public NpcDefinitionAttackStyleCombatDefinition(NpcDefinition npcDefinition) {
        this.npcDefinition = npcDefinition;
    }

    @Override
    public final CombatAttack[] createAttacks(Entity entity, Entity entity2) {
        int n = NpcDefinition.getAttackBonusTypeId(this.npcDefinition);
        return new CombatAttack[]{BaseCombatAttack.createMeleeAttack(entity, entity2, AttackXpMode.MELEE_ACCURATE, n == 0 ? AttackBonusType.STAB : (n == 1 ? AttackBonusType.SLASH : (n == 2 ? AttackBonusType.CRUSH : (n == 3 ? AttackBonusType.MAGIC : (n == 4 ? AttackBonusType.RANGED : null)))), NpcDefinition.getDefaultMaxHit(this.npcDefinition), NpcDefinition.getDefaultAttackDelay(this.npcDefinition), NpcDefinition.getDefaultAttackAnimationId(this.npcDefinition))};
    }
}

