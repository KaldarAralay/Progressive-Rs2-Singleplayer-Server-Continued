/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.player.CharacterFileRecord;
import com.rs2.model.player.Player;
import java.util.Comparator;

public final class HiscoreEntryComparator
implements Comparator {
    private final /* synthetic */ int a;

    public HiscoreEntryComparator(Player player, int n) {
        this.a = n;
    }

    public final /* synthetic */ int compare(Object object, Object object2) {
        CharacterFileRecord characterFileRecord = (CharacterFileRecord)object2;
        object2 = (CharacterFileRecord)object;
        object = this;
        long l = ((CharacterFileRecord)object2).getSkillExperience(((HiscoreEntryComparator)object).a);
        long l2 = characterFileRecord.getSkillExperience(((HiscoreEntryComparator)object).a);
        if (((HiscoreEntryComparator)object).a == 21 && (l = (long)((CharacterFileRecord)object2).getTotalLevel()) == (l2 = (long)characterFileRecord.getTotalLevel())) {
            l = ((CharacterFileRecord)object2).getSkillExperience(((HiscoreEntryComparator)object).a);
            l2 = characterFileRecord.getSkillExperience(((HiscoreEntryComparator)object).a);
        }
        if (((HiscoreEntryComparator)object).a == 22) {
            l = ((CharacterFileRecord)object2).getStoredItemValue();
            l2 = characterFileRecord.getStoredItemValue();
        }
        return Long.compare(l2, l);
    }
}

