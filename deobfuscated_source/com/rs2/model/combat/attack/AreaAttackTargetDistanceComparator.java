/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.attack;

import com.rs2.model.Entity;
import com.rs2.model.combat.attack.BaseCombatAttack;
import com.rs2.util.GameUtil;
import java.util.Comparator;

public final class AreaAttackTargetDistanceComparator
implements Comparator {
    private /* synthetic */ BaseCombatAttack attack;

    public AreaAttackTargetDistanceComparator(BaseCombatAttack baseCombatAttack) {
        this.attack = baseCombatAttack;
    }

    public final /* synthetic */ int compare(Object object, Object object2) {
        Entity entity = (Entity)object2;
        object2 = (Entity)object;
        object = this;
        int n = GameUtil.getDistance(((AreaAttackTargetDistanceComparator)object).attack.getTarget().getPosition(), ((Entity)object2).getPosition());
        int n2 = GameUtil.getDistance(((AreaAttackTargetDistanceComparator)object).attack.getTarget().getPosition(), entity.getPosition());
        return Long.compare(n, n2);
    }
}

