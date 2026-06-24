/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.special;

import com.rs2.model.Entity;
import com.rs2.model.combat.special.RuneThrownaxeSpecialAttack;
import com.rs2.util.GameUtil;
import java.util.Comparator;

public final class RuneThrownaxeTargetDistanceComparator
implements Comparator {
    private /* synthetic */ RuneThrownaxeSpecialAttack attack;

    public RuneThrownaxeTargetDistanceComparator(RuneThrownaxeSpecialAttack runeThrownaxeSpecialAttack) {
        this.attack = runeThrownaxeSpecialAttack;
    }

    public final /* synthetic */ int compare(Object object, Object object2) {
        Entity entity = (Entity)object2;
        object2 = (Entity)object;
        object = this;
        int n = GameUtil.getDistance(((RuneThrownaxeTargetDistanceComparator)object).attack.getTarget().getPosition(), ((Entity)object2).getPosition());
        int n2 = GameUtil.getDistance(((RuneThrownaxeTargetDistanceComparator)object).attack.getTarget().getPosition(), entity.getPosition());
        return Long.compare(n, n2);
    }
}

