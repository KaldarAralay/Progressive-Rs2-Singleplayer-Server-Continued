/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import java.util.HashMap;
import java.util.Map;

public enum NpcClue {
    a(new String[]{"One of the sailors in Port Sarim ", " is your next destination."}, 2678, 376, "", 1),
    b(new String[]{"Someone watching the fights ", "in the duel arena is your next ", "destination"}, 2679, 969, "", 1),
    c(new String[]{"Speak to Arhein in Catherby"}, 2680, 563, "", 1),
    d(new String[]{"Speak to Doric who lives ", "north of Falador"}, 2681, 284, "", 1),
    e(new String[]{"Speak to Ellis in Al Kharid."}, 2682, 2824, "", 1),
    f(new String[]{"Speak to Gaius in Taverley."}, 2683, 586, "", 1),
    g(new String[]{"Speak to Jatix in Taverley."}, 2684, 587, "", 1),
    h(new String[]{"Speak to Ned in Draynor "}, 2685, 918, "", 1),
    i(new String[]{"Speak to Sir Kay in Camelot", "Castle"}, 2686, 241, "", 1),
    j(new String[]{"Speak to the bartender of the", "Blue Moon Inn in Varrock"}, 2687, 733, "", 1),
    k(new String[]{"Speak to the staff of Sinclair ", "Mansion"}, 2688, 809, "", 1),
    l(new String[]{"Talk to the bartender of the Rusty ", "Anchor in Port Sarim."}, 2689, 734, "", 1),
    m(new String[]{"Talk to the Squire in the White ", "Knights' castle in Falador."}, 2690, 606, "", 1),
    n(new String[]{"Speak to a referee."}, 2831, 635, "Challenge", 2),
    o(new String[]{"Speak to Donovan the Family ", "Handyman"}, 2833, 806, "", 2),
    p(new String[]{"Speak to Hajedy"}, 2835, 510, "", 2),
    q(new String[]{"Speak to Hazelmere"}, 2837, 669, "Challenge", 2),
    r(new String[]{"Speak to Kangai Mau"}, 2839, 846, "", 2),
    s(new String[]{"Speak to Roavar"}, 2841, 1042, "", 2),
    t(new String[]{"Speak to Ulizius"}, 2843, 1054, "", 2),
    u(new String[]{"'A bag belt only?' he asked ", "his balding brothers"}, 2785, 801, "", 3),
    v(new String[]{"Citric Cellar", ""}, 2786, 603, "Puzzle", 3),
    w(new String[]{"Generally ", "speaking, his ", "nose was very ", "bent"}, 2788, 296, "Puzzle", 3),
    x(new String[]{"Identify the back of this ", "over-acting brother. ", "(He's a long way from ", "home.)"}, 2790, 1008, "Puzzle", 3),
    y(new String[]{"If a man carried my ", "burden, he would break ", "his back. I am not rich, ", "but leave silver in my ", "track. Speak to the ", "keeper of my trail."}, 2792, 558, "", 3),
    z(new String[]{"My name is like a tree, ", "yet it is spelt with a \"g\"", "come see the fur, which ", "is right near me"}, 2793, 783, "Puzzle", 3),
    A(new String[]{"Often sought out by scholars of", "histories past, find me where", "words of wisdom speak volumes."}, 2794, 618, "Puzzle", 3),
    B(new String[]{"'Small Shoe.' Often found with ", "rod on mushroom."}, 2796, 162, "Puzzle", 3),
    C(new String[]{"Snah? I feel all confused, like ", "one of those cakes."}, 2797, 0, "", 3),
    D(new String[]{"Surprising? I bet he is..."}, 2799, 883, "", 3),
    E(new String[]{"Surviving."}, 3564, 605, "Puzzle", 3);

    private String[] clueTextLines;
    private int clueItemId;
    private int npcId;
    private String followupType;
    private int level;
    private static Map cluesByNpcId;
    private static Map cluesByItemId;

    static {
        cluesByNpcId = new HashMap();
        cluesByItemId = new HashMap();
        NpcClue[] npcClueArray = NpcClue.values();
        int n = npcClueArray.length;
        int n2 = 0;
        while (n2 < n) {
            NpcClue npcClue = npcClueArray[n2];
            cluesByNpcId.put(npcClue.npcId, npcClue);
            cluesByItemId.put(npcClue.clueItemId, npcClue);
            ++n2;
        }
    }

    public static NpcClue forNpcId(int n) {
        return (NpcClue)((Object)cluesByNpcId.get(n));
    }

    public static NpcClue forClueItemId(int n) {
        return (NpcClue)((Object)cluesByItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private NpcClue(String[] stringArray, int n2, int n3, String string2, int n4) {
        this.clueTextLines = stringArray;
        this.clueItemId = n2;
        this.npcId = n3;
        this.followupType = string2;
        this.level = n4;
    }

    public final String[] getClueTextLines() {
        return this.clueTextLines;
    }

    public final int getClueItemId() {
        return this.clueItemId;
    }

    public final String getFollowupType() {
        return this.followupType;
    }

    public final int getLevel() {
        return this.level;
    }
}

