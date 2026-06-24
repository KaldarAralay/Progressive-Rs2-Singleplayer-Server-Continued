/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import com.rs2.model.Position;
import java.util.HashMap;
import java.util.Map;

public enum SearchClue {
    a(new String[]{"A crate found in the", "tower of a church is", "your next location."}, 2691, new Position(2612, 3307, 2), -1, 832, 1),
    b(new String[]{"Search a bookcase in the Wizards", "tower."}, 2692, new Position(3113, 3158, 0), -1, 832, 1),
    c(new String[]{"Search chests found in the", "upstairs of shops in Port Sarim."}, 2693, new Position(3016, 3205, 1), 378, 832, 1),
    d(new String[]{"Search for a crate in a ", "building in Hemenster"}, 2694, new Position(2636, 3453, 0), -1, 832, 1),
    e(new String[]{"Search for a crate in Varrock", "Castle."}, 2695, new Position(3224, 3492, 0), -1, 832, 1),
    f(new String[]{"Search for a crate on the ", "ground floor of a house in", "Seers' Village"}, 2696, new Position(2699, 3470, 0), -1, 832, 1),
    g(new String[]{"Search the boxes in a shop ", "in Taverley"}, 2697, new Position(2886, 3449, 0), -1, 832, 1),
    h(new String[]{"Search the boxes in one of ", "the tents in Al Kharid"}, 2698, new Position(3308, 3206, 0), -1, 832, 1),
    i(new String[]{"Search the boxes in the ", "Goblin house near Lumbridge"}, 2699, new Position(3245, 3245, 0), -1, 832, 1),
    j(new String[]{"Search the boxes in the ", "house near the South ", "entrance of Varrock"}, 2700, new Position(3203, 3384, 0), -1, 832, 1),
    k(new String[]{"Search the boxes just ", "outside the Armour shop in ", "East Ardougne"}, 2701, new Position(2654, 3299, 0), -1, 832, 1),
    l(new String[]{"Search the chest in the Duke ", "of Lumbridge's bedroom"}, 2702, new Position(3209, 3218, 1), 378, 832, 1),
    m(new String[]{"Search the chest in the left-", "hand tower of Camelot castle"}, 2703, new Position(2748, 3495, 2), 378, 832, 1),
    n(new String[]{"Search the chests in the", "Dwarven Mine"}, 2704, new Position(3000, 9798, 0), 378, 832, 1),
    o(new String[]{"Search the chests upstairs in", "Al Kharid palace."}, 2705, new Position(3301, 3169, 1), 378, 832, 1),
    p(new String[]{"Search the Coffin in Edgeville."}, 2706, new Position(3090, 3476, 0), 0, 832, 1),
    q(new String[]{"Search the crate in the left-", "hand tower of Lumbridge", "castle"}, 2707, new Position(3228, 3212, 1), -1, 832, 1),
    r(new String[]{"Search the crate near a cart ", "in Port Khazard."}, 2708, new Position(2660, 3149, 0), -1, 832, 1),
    s(new String[]{"Search the crates in a house ", "in Yanille that has a piano"}, 2709, new Position(2598, 3105, 0), -1, 832, 1),
    t(new String[]{"Search the crates in Canifis"}, 2710, new Position(3509, 3497, 0), -1, 832, 1),
    u(new String[]{"Search the crates in Draynor ", "Manor"}, 2711, new Position(3106, 3369, 2), -1, 832, 1),
    v(new String[]{"Search the crates in East", "Ardougne's general store."}, 2712, new Position(2615, 3291, 0), -1, 832, 1),
    w(new String[]{"Search the crates of", "Falador's general store"}, 3490, new Position(2955, 3390, 0), -1, 832, 1),
    x(new String[]{"Search the crates in Horvik's ", "armoury"}, 3491, new Position(3228, 3433, 0), -1, 832, 1),
    y(new String[]{"Search the crates in", "Barbarian Village", "helmet shop"}, 3492, new Position(3073, 3430, 0), -1, 832, 1),
    z(new String[]{"Search the crates in the ", "guard house of the northern ", "gate of East Ardougne."}, 3493, new Position(2645, 3338, 0), -1, 832, 1),
    A(new String[]{"Search the crates in the ", "most north-western house in Al Kharid"}, 3494, new Position(3289, 3202, 0), -1, 832, 1),
    B(new String[]{"Search the crates in the Port ", "Sarim fishing shop"}, 3495, new Position(3012, 3222, 0), -1, 832, 1),
    C(new String[]{"Search the crates in the shed ", "just north of east Ardougne"}, 3496, new Position(2617, 3347, 0), -1, 832, 1),
    D(new String[]{"Search the crates near a cart ", "in Varrock."}, 3497, new Position(3226, 3452, 0), -1, 832, 1),
    E(new String[]{"Search the drawers above", "Varrock's shops."}, 3498, new Position(3206, 3419, 1), 351, 832, 1),
    F(new String[]{"Search the drawers in a ", "house in Draynor Village."}, 3499, new Position(3097, 3277, 0), 351, 832, 1),
    G(new String[]{"Search the drawers in", "Falador's chainmail shop"}, 3500, new Position(2969, 3311, 0), 349, 832, 1),
    H(new String[]{"Search the drawers in one of", "Gertrude's bedrooms"}, 3501, new Position(3156, 3406, 0), 349, 832, 1),
    I(new String[]{"Search the drawers in the ", "upstairs of a house in ", "Catherby."}, 3502, new Position(2809, 3451, 1), 351, 832, 1),
    J(new String[]{"Search the drawers of houses ", "in Burthorpe"}, 3503, new Position(2929, 3570, 0), 349, 832, 1),
    K(new String[]{"Search the drawers upstairs ", "in Falador's shield shop."}, 3504, new Position(2971, 3386, 1), 349, 832, 1),
    L(new String[]{"Search the drawers in the ", "upstairs of the bank to the ", "east of Varrock"}, 3505, new Position(3250, 3420, 1), 349, 832, 1),
    M(new String[]{"Search the tents in the ", "imperial guard camp in", "Burthorpe for some boxes"}, 3506, new Position(2885, 3540, 0), -1, 832, 1),
    N(new String[]{"Search through chests found ", "in the upstairs of houses in ", "eastern Falador."}, 3507, new Position(3041, 3364, 1), 378, 832, 1),
    O(new String[]{"Search through some drawers", "in the upstairs of a", "house in Rimmington."}, 3508, new Position(2970, 3214, 1), 353, 832, 1),
    P(new String[]{"Search through some drawers ", "found in Taverley's houses."}, 3509, new Position(2894, 3418, 0), 351, 832, 1),
    Q(new String[]{"Search upstairs in the ", "houses of Seers' Village for ", "some drawers."}, 3510, new Position(2716, 3471, 1), 348, 832, 1),
    R(new String[]{"A town with a different sort of", "night-life is your destination. ", "Search for some crates in one", "of the houses."}, 3614, new Position(3498, 3507, 0), -1, 832, 2),
    S(new String[]{"Go to the village being", "attacked by trolls, search the", "drawers in one of the houses."}, 3615, new Position(2921, 3577, 0), 351, 832, 2),
    T(new String[]{"Go to this ", "building to be ", "illuminated, and ", "search the ", "drawers while ", "you're there."}, 3616, new Position(2512, 3641, 1), 351, 832, 2),
    U(new String[]{"In a town where everyone has", "perfect vision, seek some", "locked drawers in a house that", "sits opposite a workshop."}, 3617, new Position(2709, 3478, 0), 351, 832, 2),
    V(new String[]{"In a town where the ", "guards are armed with ", "maces, search the ", "upstairs room of the ", "Public House."}, 3618, new Position(2574, 3326, 1), 349, 832, 2),
    W(new String[]{"In a town where thieves ", "steal from stalls, search ", "for some drawers ", "upstairs of a house near ", "the bank."}, 7274, new Position(2611, 3324, 1), 349, 832, 2),
    X(new String[]{"In a town where wizards are", "known to gather, search", "upstairs in a large", "house to the north."}, 7276, new Position(2593, 3108, 1), 378, 832, 2),
    Y(new String[]{"In a village made of ", "bamboo look for some ", "crates under one of the ", "houses."}, 7278, new Position(2800, 3074, 0), 0, 832, 2),
    Z(new String[]{"Probably filled ", "with wizards' ", "socks"}, 7280, new Position(3116, 9562, 0), 351, 832, 2),
    aa(new String[]{"Search the upstairs drawers ", "of a house in a village were ", "pirates are known to have a ", "good time."}, 7282, new Position(2809, 3165, 1), 349, 832, 2),
    ab(new String[]{"The dead, red dragon watches", "over this chest.", "He must really dig the view."}, 7284, new Position(3353, 3332, 0), 378, 832, 2),
    ac(new String[]{"This crate holds a better ", "reward than a broken ", "arrow"}, 7296, new Position(2671, 3437, 0), 0, 832, 2),
    ad(new String[]{"This crate is mine, all ", "mine, even if it is in the ", "middle of the desert."}, 7298, new Position(3289, 3022, 0), 0, 832, 2),
    ae(new String[]{"You'll need to look for ", "a town with a central ", "fountain. Look for a locked", "chest in the town's chapel."}, 7300, new Position(3256, 3487, 0), 378, 832, 2),
    af(new String[]{"Four blades I have, yet draw no", "blood;", "Still I turn my prey to powder.", "If you are brave, come search my", "roof;", "It is there my blades are louder."}, 3572, new Position(3166, 3309, 2), 0, 832, 3),
    ag(new String[]{"A great view: watch", "the rapidly drying", "hides get splashed.", "Check the box you are sitting on."}, 3573, new Position(2523, 3493, 1), 0, 832, 3),
    ah(new String[]{"His head might be hollow, but", "the crates nearby are filled with", "surprises."}, 3574, new Position(3478, 3091, 0), 0, 832, 3),
    ai(new String[]{"If you look closely enough,", "it seems that the archers have lost", "more than their needles."}, 3575, new Position(2671, 3415, 0), 0, 832, 3),
    aj(new String[]{"It seems to have reached the ", "end of the line, and it's still", "empty."}, 3577, new Position(3041, 9820, 0), 0, 832, 3),
    ak(new String[]{"My home is grey, and made of", "stone;", "A castle with a search for a meal.", "Hidden in some drawers I am,", "Across from a wooden wheel."}, 3579, new Position(3213, 3216, 1), 5619, 832, 3),
    al(new String[]{"Probably filled with books", "on magic."}, 3580, new Position(3096, 9571, 0), 0, 832, 3),
    am(new String[]{"Read 'How to breed scorpions'", "By O.W. Thathurt."}, 7243, new Position(2702, 3409, 1), 0, 832, 3),
    an(new String[]{"The cheapest water for miles around,", "but they react badly to", "religious icons."}, 7245, new Position(3178, 2987, 0), 0, 832, 3),
    ao(new String[]{"This village has a ", "problem with cartloads ", "of the undead. Try ", "checking the bookcase ", "to find the answer."}, 7247, new Position(2833, 2991, 0), 0, 832, 3),
    ap(new String[]{"When no weapons are at hand,", "then is the time to reflect.", "In Saradomin's name,", "redemption draws closer..."}, 7248, new Position(2818, 3351, 0), 351, 832, 3),
    aq(new String[]{"You have all of the elements", "available to solve this clue.", "Fortunately you do not have to", "go so far as to stand in a", "draft."}, 7249, new Position(2723, 9891, 0), 0, 832, 3),
    ar(new String[]{"Try not to let yourself be ", "dazzled when you ", "search these drawers."}, 7250, new Position(2561, 3323, 0), 351, 832, 3);

    private String[] clueTextLines;
    private int clueItemId;
    private Position position;
    private int replacementObjectId;
    private int animationId;
    private int level;
    private static Map cluesByPosition;
    private static Map cluesByItemId;

    static {
        cluesByPosition = new HashMap();
        cluesByItemId = new HashMap();
        SearchClue[] searchClueArray = SearchClue.values();
        int n = searchClueArray.length;
        int n2 = 0;
        while (n2 < n) {
            SearchClue searchClue = searchClueArray[n2];
            cluesByPosition.put(searchClue.position, searchClue);
            cluesByItemId.put(searchClue.clueItemId, searchClue);
            ++n2;
        }
    }

    public static SearchClue forPosition(Position position) {
        int n = 0;
        while (n < SearchClue.values().length) {
            SearchClue searchClue = SearchClue.values()[n];
            if (searchClue.position.equals(position)) {
                return SearchClue.values()[n];
            }
            ++n;
        }
        return null;
    }

    public static SearchClue forClueItemId(int n) {
        return (SearchClue)((Object)cluesByItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private SearchClue(String[] stringArray, int n2, Position position, int n3, int n4, int n5) {
        this.clueTextLines = stringArray;
        this.clueItemId = n2;
        this.position = position;
        this.replacementObjectId = n3;
        this.animationId = 832;
        this.level = n5;
    }

    public final String[] getClueTextLines() {
        return this.clueTextLines;
    }

    public final int getClueItemId() {
        return this.clueItemId;
    }

    public final Position getPosition() {
        return this.position;
    }

    public final int getReplacementObjectId() {
        return this.replacementObjectId;
    }

    public final int getAnimationId() {
        return this.animationId;
    }

    public final int getLevel() {
        return this.level;
    }
}

