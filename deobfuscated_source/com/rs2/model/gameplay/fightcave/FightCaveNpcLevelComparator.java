/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.fightcave;

import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import java.util.Comparator;

public final class FightCaveNpcLevelComparator
implements Comparator {
    public FightCaveNpcLevelComparator() {
    }

    public final /* synthetic */ int compare(Object object, Object object2) {
        object2 = (Npc)object2;
        object = (Npc)object;
        object = NpcDefinition.forId(((Npc)object).getNpcId());
        object2 = NpcDefinition.forId(((Npc)object2).getNpcId());
        int n = ((NpcDefinition)object).getCombatLevel();
        int n2 = ((NpcDefinition)object2).getCombatLevel();
        return Integer.compare(n2, n);
    }
}

