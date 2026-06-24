/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import java.util.HashMap;
import java.util.Map;

public enum AnagramClue {
    a(2845, 1070, "A Bas", "", 2),
    b(2847, 962, "Aha Jar", "", 2),
    c(2848, 696, "Arc O Line", "Challenge", 2),
    d(2849, 746, "Are Col", "Challenge", 2),
    e(2851, 171, "Bail Trims", "", 2),
    f(2853, 1294, "Dt Run B", "Challenge", 2),
    g(2855, 28, "Eek Zero Op", "Challenge", 2),
    h(2856, 550, "El Ow", "", 2),
    i(2857, 469, "Goblin Kern", "", 2),
    j(2858, 2520, "Got A Boy", "Challenge", 2),
    k(2858, 2521, "Got A Boy", "Challenge", 2),
    l(3604, 379, "Halt Us", "", 2),
    m(3605, 1011, "Icy fe", "", 2),
    n(3607, 648, "Lark In Dog", "Challenge", 2),
    o(3609, 676, "Me If", "", 2),
    p(3610, 714, "Nod Med", "Challenge", 2),
    q(3611, 278, "Ok Co", "Challenge", 2),
    r(3612, 659, "Peaty Pert", "", 2),
    s(3613, 543, "R Ak Mi", "Challenge", 2),
    t(3566, 471, "By Look", "Challenge", 3),
    u(3568, 2802, "C On Game Hoc", "Challenge", 3),
    v(3570, 437, "O Birdz A Zany En Pc", "Challenge", 3);

    private int clueItemId;
    private int npcId;
    private String anagramText;
    private String followupType;
    private int level;
    private static Map cluesByNpcId;
    private static Map cluesByItemId;

    static {
        cluesByNpcId = new HashMap();
        cluesByItemId = new HashMap();
        AnagramClue[] anagramClueArray = AnagramClue.values();
        int n = anagramClueArray.length;
        int n2 = 0;
        while (n2 < n) {
            AnagramClue anagramClue = anagramClueArray[n2];
            cluesByNpcId.put(anagramClue.npcId, anagramClue);
            cluesByItemId.put(anagramClue.clueItemId, anagramClue);
            ++n2;
        }
    }

    public static AnagramClue forNpcId(int n) {
        return (AnagramClue)((Object)cluesByNpcId.get(n));
    }

    public static AnagramClue forClueItemId(int n) {
        return (AnagramClue)((Object)cluesByItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private AnagramClue(int n2, int n3, String string2, String string3, int n4) {
        this.clueItemId = n2;
        this.npcId = n3;
        this.anagramText = string2;
        this.followupType = string3;
        this.level = n4;
    }

    public final int getClueItemId() {
        return this.clueItemId;
    }

    public final String getAnagramText() {
        return this.anagramText;
    }

    public final String getFollowupType() {
        return this.followupType;
    }

    public final int getLevel() {
        return this.level;
    }
}

