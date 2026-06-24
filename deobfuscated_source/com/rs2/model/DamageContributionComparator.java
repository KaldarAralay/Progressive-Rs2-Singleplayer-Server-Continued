/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.model.Entity;
import com.rs2.model.combat.hit.DamageContribution;
import java.util.Comparator;

public final class DamageContributionComparator
implements Comparator {
    public DamageContributionComparator(Entity entity) {
    }

    public final /* synthetic */ int compare(Object object, Object object2) {
        object2 = (DamageContribution)object2;
        object = (DamageContribution)object;
        return ((DamageContribution)object2).getDamage() - ((DamageContribution)object).getDamage();
    }
}

