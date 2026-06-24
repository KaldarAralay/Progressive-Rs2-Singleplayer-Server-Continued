/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestScript;
import java.awt.Color;

public final class WaterfallQuest
extends QuestScript {
    private static final int QUEST_ID = 102;
    private static final int PROGRESS_MASK = 15;
    private static final int RUNE_BIT_START = 4;
    private static final int ALL_RUNES_MASK = ((1 << 18) - 1) << RUNE_BIT_START;
    private static final int GOLRIE_GATE_UNLOCKED_BIT = 1 << 22;

    private static final int STATE_COMPLETE = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_SPOKE_TO_HUDON = 3;
    private static final int STATE_READ_BOOK = 4;
    private static final int STATE_ENTERED_TOMB = 5;
    private static final int STATE_ENTERED_WATERFALL = 6;
    private static final int STATE_PUZZLE_ROOM = 7;
    private static final int STATE_PLACED_AMULET = 8;

    private static final int BOOK_ON_BAXTORIAN = 292;
    private static final int GOLRIE_KEY = 293;
    private static final int GLARIALS_PEBBLE = 294;
    private static final int GLARIALS_AMULET = 295;
    private static final int GLARIALS_URN_FULL = 296;
    private static final int GLARIALS_URN_EMPTY = 297;
    private static final int BAXTORIAN_KEY = 298;
    private static final int MITHRIL_SEEDS = 299;
    private static final int WATER_RUNE = 555;
    private static final int AIR_RUNE = 556;
    private static final int EARTH_RUNE = 557;
    private static final int ROPE = 954;
    private static final int DIAMOND = 1601;
    private static final int GOLD_BAR = 2357;

    private static final int HADLEY = 302;
    private static final int GERALD = 303;
    private static final int ALMERA = 304;
    private static final int HUDON = 305;
    private static final int GOLRIE = 306;
    private static final int TREE_GNOME_DUNGEON_ZOMBIE = 75;
    private static final int TREE_GNOME_DUNGEON_GIANT_BAT = 78;

    private static final int LOG_RAFT = 1987;
    private static final int BOOKCASE = 1989;
    private static final int GOLRIE_CRATE = 1990;
    private static final int GOLRIE_GATE = 1991;
    private static final int GLARIALS_TOMBSTONE = 1992;
    private static final int GLARIALS_TOMB = 1993;
    private static final int CLOSED_CHEST = 1994;
    private static final int OPEN_CHEST = 1995;
    private static final int CROSSING_ROCK_1 = 1996;
    private static final int CROSSING_ROCK_2 = 1997;
    private static final int CROSSING_ROPE = 1998;
    private static final int BAXTORIAN_CRATE = 1999;
    private static final int WATERFALL_EXIT_DOOR = 2000;
    private static final int BAXTORIAN_DOOR_OPEN_ALT = 2001;
    private static final int BAXTORIAN_DOOR = 2002;
    private static final int BAXTORIAN_DOOR_OPEN = 2003;
    private static final int STONE_PILLAR = 2004;
    private static final int STATUE_OF_BAXTORIAN = 2005;
    private static final int STATUE_OF_GLARIAL = 2006;
    private static final int WATERFALL_LEDGE_LEFT = 2007;
    private static final int WATERFALL_LEDGE_MIDDLE = 2008;
    private static final int WATERFALL_LEDGE_RIGHT = 2009;
    private static final int WATERFALL_LEDGE_DOOR = 2010;
    private static final int WATERFALL_LEDGE_DOOR_LEFT = 2011;
    private static final int WATERFALL_LEDGE_DOOR_RIGHT = 2012;
    private static final int CHALICE_1 = 2014;
    private static final int CHALICE_2 = 2015;
    private static final int WHIRLPOOL = 2019;
    private static final int OVERHANGING_TREE_1 = 2020;
    private static final int OVERHANGING_TREE_2 = 2021;
    private static final int BARREL = 2022;
    private static final int WATERFALL_ROCKS_1 = 1797;
    private static final int WATERFALL_ROCKS_2 = 2225;
    private static final int GNOME_CAVE_ENTRANCE = 194;
    private static final int GNOME_CAVE_LADDER = 195;
    private static final int TREE_GNOME_VILLAGE_DUNGEON_ENTRANCE_LADDER = 1754;
    private static final int GLARIALS_TOMB_EXIT_LADDER = 1757;

    private static final Position RAFT_CRASH_POSITION = new Position(2512, 3481, 0);
    private static final Position FAILED_WATERFALL_POSITION = new Position(2527, 3413, 0);
    private static final Position WATERFALL_LEDGE_POSITION = new Position(2511, 3463, 0);
    private static final Position GLARIALS_TOMB_POSITION = new Position(2554, 9844, 0);
    private static final Position GLARIALS_TOMB_EXIT_POSITION = new Position(2556, 3444, 0);
    private static final Position GNOME_CAVE_POSITION = new Position(2409, 9819, 0);
    private static final Position GNOME_CAVE_EXIT_POSITION = new Position(2410, 3421, 0);
    private static final Position TREE_GNOME_VILLAGE_DUNGEON_POSITION = new Position(2533, 9554, 0);
    private static final Position TREE_GNOME_VILLAGE_DUNGEON_EXIT_POSITION = new Position(2532, 3155, 0);
    private static final Position WATERFALL_ENTRY_POSITION = new Position(2575, 9861, 0);
    private static final Position FIRE_GIANT_ROOM_DOOR_POSITION = new Position(2568, 9892, 0);
    private static final Position FIRE_GIANT_CORRIDOR_DOOR_POSITION = new Position(2568, 9894, 0);
    private static final Position ORIGINAL_ROOM_DOOR_POSITION = new Position(2566, 9901, 0);
    private static final Position ORIGINAL_ROOM_EXIT_POSITION = new Position(2566, 9900, 0);
    private static final Position PUZZLE_ROOM_POSITION = new Position(2566, 9902, 0);
    private static final Position RAISED_ROOM_POSITION = new Position(2604, 9901, 0);

    private static final int[][] PILLAR_COORDINATES = new int[][]{
        {2562, 9910},
        {2562, 9912},
        {2562, 9914},
        {2569, 9910},
        {2569, 9912},
        {2569, 9914}
    };
    private static final int[][] GLARIALS_TOMB_NPC_SPAWNS = new int[][]{
        {112, 2528, 9843},
        {92, 2529, 9841},
        {92, 2530, 9846},
        {73, 2533, 9842},
        {91, 2538, 9816},
        {92, 2540, 9813},
        {73, 2540, 9823},
        {73, 2540, 9843},
        {112, 2541, 9845},
        {112, 2542, 9819},
        {75, 2542, 9840},
        {153, 2543, 9813},
        {92, 2543, 9847},
        {92, 2545, 9822},
        {75, 2546, 9815},
        {75, 2546, 9842}
    };
    private static final int[][] WATERFALL_DUNGEON_NPC_SPAWNS = new int[][]{
        {110, 2562, 9886},
        {110, 2565, 9887},
        {47, 2566, 9877},
        {47, 2566, 9891},
        {110, 2568, 9889},
        {47, 2568, 9898},
        {78, 2569, 9886},
        {47, 2572, 9865},
        {58, 2572, 9875},
        {110, 2573, 9895},
        {58, 2574, 9876},
        {47, 2574, 9889},
        {110, 2575, 9891},
        {58, 2576, 9875},
        {58, 2576, 9877},
        {110, 2577, 9890},
        {110, 2577, 9897},
        {110, 2578, 9895},
        {58, 2580, 9876},
        {110, 2580, 9890},
        {110, 2581, 9895},
        {47, 2583, 9875},
        {47, 2584, 9896},
        {94, 2585, 9878},
        {93, 2586, 9884},
        {94, 2588, 9880},
        {93, 2588, 9883},
        {93, 2589, 9886},
        {94, 2590, 9882},
        {94, 2592, 9884},
        {47, 2604, 9890},
        {47, 2606, 9897}
    };

    public WaterfallQuest(int n) {
        super(QUEST_ID);
        super.setQuestPointReward(1);
    }

    public static void spawnMissingNpcs() {
        if (!WaterfallQuest.isContentAvailable()) {
            return;
        }
        WaterfallQuest.spawnNpcIfMissing(ALMERA, 2522, 3498, 0, 0);
        WaterfallQuest.spawnNpcIfMissing(HUDON, 2511, 3484, 0, 0);
        WaterfallQuest.spawnNpcIfMissing(HADLEY, 2516, 3428, 0, 0);
        WaterfallQuest.spawnNpcIfMissing(GERALD, 2528, 3414, 0, 0);
        WaterfallQuest.spawnNpcIfMissingAt(GOLRIE, 2515, 9581, 0, 0);
        WaterfallQuest.spawnNpcIfMissingAt(TREE_GNOME_DUNGEON_ZOMBIE, 2539, 9566, 0, 0);
        WaterfallQuest.spawnNpcIfMissingAt(TREE_GNOME_DUNGEON_GIANT_BAT, 2541, 9566, 0, 0);
        WaterfallQuest.spawnNpcIfMissingAt(TREE_GNOME_DUNGEON_GIANT_BAT, 2542, 9557, 0, 0);
        WaterfallQuest.spawnNpcIfMissingAt(TREE_GNOME_DUNGEON_GIANT_BAT, 2543, 9564, 0, 0);
        int n = 0;
        while (n < GLARIALS_TOMB_NPC_SPAWNS.length) {
            int[] nArray = GLARIALS_TOMB_NPC_SPAWNS[n];
            WaterfallQuest.spawnNpcIfMissingAt(nArray[0], nArray[1], nArray[2], 0, 0);
            ++n;
        }
        n = 0;
        while (n < WATERFALL_DUNGEON_NPC_SPAWNS.length) {
            int[] nArray = WATERFALL_DUNGEON_NPC_SPAWNS[n];
            WaterfallQuest.spawnNpcIfMissingAt(nArray[0], nArray[1], nArray[2], 0, 0);
            ++n;
        }
    }

    public static boolean handleWaterfallRiverAction(Player player, int n, String string, int n2, int n3) {
        if (!WaterfallQuest.isHudonCrossingInteraction(player, n2, n3) || !WaterfallQuest.isRiverSwimObject(n, string)) {
            return false;
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return WaterfallQuest.showUnavailable(player);
        }
        player.packetSender.sendGameMessage("You jump into the river and are swept downstream.");
        WaterfallQuest.failWaterfall(player);
        return true;
    }

    public static boolean handleWaterfallItemOnObject(Player player, int n, int n2, String string, int n3, int n4) {
        if (n == ROPE && WaterfallQuest.isHudonCrossingInteraction(player, n3, n4) && WaterfallQuest.isHudonIslandRopeObject(n2, string)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.useRopeCrossing(player, false);
        }
        if (n == ROPE && WaterfallQuest.isOverhangingTreeObject(n2, n3, n4) && WaterfallQuest.isPlayerNearObject(player, n3, n4, 8)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.useRopeCrossing(player, true);
        }
        if (n == GLARIALS_PEBBLE && n2 == GLARIALS_TOMBSTONE && WaterfallQuest.isGlarialsTombstoneCoord(n3, n4) && WaterfallQuest.isPlayerNearObject(player, n3, n4, 8)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.enterGlarialsTomb(player, WaterfallQuest.getProgress(player.getQuestState(QUEST_ID)));
        }
        if (n == BAXTORIAN_KEY && WaterfallQuest.isBaxtorianDoorObject(n2) && WaterfallQuest.isBaxtorianDoorCoord(n3, n4) && WaterfallQuest.isPlayerNearObject(player, n3, n4, 8)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.openBaxtorianDoor(player, WaterfallQuest.getProgress(player.getQuestState(QUEST_ID)), n3, n4, true);
        }
        if (WaterfallQuest.isPuzzleRune(n) && n2 == STONE_PILLAR && WaterfallQuest.isPuzzlePillarCoord(n3, n4) && WaterfallQuest.isPlayerNearObject(player, n3, n4, 8)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.placeRuneOnPillar(player, n, WaterfallQuest.getProgress(player.getQuestState(QUEST_ID)), n3, n4);
        }
        if (n == GLARIALS_AMULET && n2 == STATUE_OF_GLARIAL && WaterfallQuest.isOriginalGlarialStatueCoord(n3, n4) && WaterfallQuest.isPlayerNearObject(player, n3, n4, 8)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.placeAmuletOnStatue(player, player.getQuestState(QUEST_ID), WaterfallQuest.getProgress(player.getQuestState(QUEST_ID)), n3, n4);
        }
        if (n == GLARIALS_URN_FULL && WaterfallQuest.isChaliceObject(n2) && WaterfallQuest.isBaxtorianChaliceCoord(n3, n4) && WaterfallQuest.isPlayerNearObject(player, n3, n4, 8)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.completeAtChalice(player, WaterfallQuest.getProgress(player.getQuestState(QUEST_ID)));
        }
        return false;
    }

    public static boolean handleWaterfallRouteObject(Player player, int n, String string, int n2, int n3) {
        if (n == GOLRIE_GATE && WaterfallQuest.isGolrieGateCoord(n2, n3) && WaterfallQuest.isTreeGnomeVillageDungeonArea(player.getPosition().getX(), player.getPosition().getY()) && WaterfallQuest.isPlayerNearObject(player, n2, n3, 12)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.openGolrieGate(player);
        }
        if (WaterfallQuest.isTreeGnomeVillageDungeonSurfaceInteraction(player, n2, n3)) {
            player.packetSender.sendGameMessage("You climb down into the dungeon.");
            player.moveTo(TREE_GNOME_VILLAGE_DUNGEON_POSITION);
            return true;
        }
        if (WaterfallQuest.isTreeGnomeVillageDungeonUndergroundInteraction(player, n2, n3)) {
            player.packetSender.sendGameMessage("You climb up the ladder.");
            player.moveTo(TREE_GNOME_VILLAGE_DUNGEON_EXIT_POSITION);
            return true;
        }
        if (WaterfallQuest.isGnomeCaveEntranceObject(n, string, n2, n3) && WaterfallQuest.isPlayerNearObject(player, n2, n3, 8)) {
            player.packetSender.sendGameMessage("You climb down into the cave.");
            player.moveTo(GNOME_CAVE_POSITION);
            return true;
        }
        if (WaterfallQuest.isGnomeCaveLadderObject(n, string, n2, n3) && WaterfallQuest.isPlayerNearObject(player, n2, n3, 8)) {
            player.packetSender.sendGameMessage("You climb up the ladder.");
            player.moveTo(GNOME_CAVE_EXIT_POSITION);
            return true;
        }
        if (WaterfallQuest.isGlarialsTombExitLadder(n, n2, n3) && WaterfallQuest.isPlayerNearObject(player, n2, n3, 8)) {
            player.packetSender.sendGameMessage("You climb up the ladder.");
            player.moveTo(GLARIALS_TOMB_EXIT_POSITION);
            return true;
        }
        if (!WaterfallQuest.isPlayerNearObject(player, n2, n3, 8)) {
            return false;
        }
        if (WaterfallQuest.isWaterfallLedgeDoor(n, n2, n3)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.enterWaterfall(player, WaterfallQuest.getProgress(player.getQuestState(QUEST_ID)));
        }
        if (n == WATERFALL_EXIT_DOOR && WaterfallQuest.isWaterfallExitDoorCoord(n2, n3)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            player.moveTo(WATERFALL_LEDGE_POSITION);
            return true;
        }
        if (WaterfallQuest.isBaxtorianDoorObject(n) && WaterfallQuest.isBaxtorianDoorCoord(n2, n3)) {
            if (!WaterfallQuest.isContentAvailable()) {
                return WaterfallQuest.showUnavailable(player);
            }
            return WaterfallQuest.openBaxtorianDoor(player, WaterfallQuest.getProgress(player.getQuestState(QUEST_ID)), n2, n3, false);
        }
        return false;
    }

    public static boolean handleWaterfallNpcPreAction(Player player, Npc npc) {
        if (npc == null || npc.getNpcId() != GOLRIE) {
            return false;
        }
        int n = player.getPosition().getX();
        int n2 = player.getPosition().getY();
        int n3 = npc.getPosition().getX();
        int n4 = npc.getPosition().getY();
        if (!WaterfallQuest.isTreeGnomeVillageDungeonArea(n, n2) && !WaterfallQuest.isTreeGnomeVillageDungeonArea(n3, n4)) {
            return false;
        }
        if (!WaterfallQuest.isNear(n, n2, n3, n4, 12)) {
            return false;
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return WaterfallQuest.showUnavailable(player);
        }
        npc.getUpdateState().setFaceEntity(player.getEncodedIndex());
        player.setInteractionTarget(npc);
        player.getUpdateState().setFaceEntity(npc.getEncodedIndex());
        DialogueManager.startDialogue(player, GOLRIE);
        return true;
    }

    @Override
    public final boolean refreshQuestJournalStatus(Player player, int n) {
        if (n != STATE_COMPLETE && !WaterfallQuest.isContentAvailable()) {
            player.packetSender.sendInterfaceTextColor(QuestDefinition.forId(this.getQuestId()).getJournalButtonId(), new Color(102, 102, 102));
            return true;
        }
        return false;
    }

    @Override
    public final String[] buildQuestJournal(Player player, int n) {
        if (n == STATE_COMPLETE) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "13,750 Attack XP", "13,750 Strength XP", "2 diamonds, 2 gold bars", "40 mithril seeds"};
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return new String[]{"Waterfall Quest is not available in this cache.", "", "The loaded cache is missing at least one of the", "required Waterfall Quest item, NPC, or object definitions."};
        }
        int n2 = WaterfallQuest.getProgress(n);
        if (n2 == 0) {
            return new String[]{"I can start this quest by speaking to Almera near", "Baxtorian Falls.", "", "I will need rope, air runes, earth runes, water runes,", "and enough space for the treasure."};
        }
        if (n2 == STATE_STARTED) {
            return new String[]{"Almera has asked me to find her son Hudon.", "I should look for him near the waterfall."};
        }
        if (n2 == STATE_SPOKE_TO_HUDON) {
            return new String[]{"Hudon is safe, but there is more to the waterfall.", "I should ask Hadley about the old Baxtorian legends", "and search his house for a useful book."};
        }
        if (n2 == STATE_READ_BOOK) {
            return new String[]{"The book mentions Glarial and Baxtorian.", "I should find Golrie and obtain Glarial's pebble,", "then use it to enter Glarial's tomb."};
        }
        if (n2 == STATE_ENTERED_TOMB) {
            return new String[]{"I have entered Glarial's tomb.", "I should search the tomb for Glarial's urn and amulet.", "The amulet may let me enter the waterfall itself."};
        }
        if (n2 == STATE_ENTERED_WATERFALL) {
            return new String[]{"I am inside the waterfall.", "I should search for Baxtorian's key and use it on the", "locked door deeper inside."};
        }
        if (n2 == STATE_PUZZLE_ROOM) {
            return new String[]{"I have reached the waterfall puzzle room.", "I should place air, earth, and water runes on all six", "pillars, then use Glarial's amulet on her statue."};
        }
        if (n2 == STATE_PLACED_AMULET) {
            return new String[]{"Glarial's statue accepted the amulet and the room rose.", "I should use Glarial's urn on the Chalice of Eternity."};
        }
        return null;
    }

    @Override
    public final void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        player.packetSender.sendInterfaceText("1 Quest Point", 12150);
        player.packetSender.sendInterfaceText("13,750 Attack XP", 12151);
        player.packetSender.sendInterfaceText("13,750 Strength XP", 12152);
        player.packetSender.sendInterfaceText("2 diamonds, 2 gold bars", 12153);
        player.packetSender.sendInterfaceText("40 mithril seeds", 12154);
        player.packetSender.sendInterfaceText("", 12155);
        player.getInventoryManager().addItem(new ItemStack(GLARIALS_URN_EMPTY, 1));
        player.getInventoryManager().addItem(new ItemStack(DIAMOND, 2));
        player.getInventoryManager().addItem(new ItemStack(GOLD_BAR, 2));
        player.getInventoryManager().addItem(new ItemStack(MITHRIL_SEEDS, 40));
        player.getSkillManager().addQuestExperience(0, 13750.0);
        player.getSkillManager().addQuestExperience(2, 13750.0);
        player.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, MITHRIL_SEEDS);
        player.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public final boolean handleInventoryItemFirstOption(Player player, int n, int n2, int n3) {
        if (n2 != BOOK_ON_BAXTORIAN) {
            return false;
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return WaterfallQuest.showUnavailable(player);
        }
        if (WaterfallQuest.getProgress(n3) >= STATE_SPOKE_TO_HUDON && WaterfallQuest.getProgress(n3) < STATE_READ_BOOK) {
            WaterfallQuest.setProgress(player, STATE_READ_BOOK);
        }
        player.getDialogueManager().showFourLineStatement("The book tells of Baxtorian, Glarial, and the hidden", "treasure beneath the waterfall. It says Glarial's", "pebble opens her tomb, and her amulet is needed to", "enter the waterfall safely.");
        return true;
    }

    @Override
    public final boolean handleNpcDialogue(Player player, int n, int n2, int n3, int n4) {
        if (!WaterfallQuest.isWaterfallNpc(n)) {
            return false;
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return WaterfallQuest.showUnavailable(player);
        }
        int n5 = WaterfallQuest.getProgress(n4);
        if (n == ALMERA) {
            return this.handleAlmeraDialogue(player, n2, n3, n5);
        }
        if (n == HUDON) {
            return this.handleHudonDialogue(player, n2, n5);
        }
        if (n == HADLEY || n == GERALD) {
            return this.handleHadleyDialogue(player, n, n2, n5);
        }
        if (n == GOLRIE) {
            return this.handleGolrieDialogue(player, n2, n5);
        }
        return false;
    }

    @Override
    public final boolean handleFirstNpcAction(Player player, int n, int n2) {
        if (!WaterfallQuest.isWaterfallNpc(n)) {
            return false;
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return WaterfallQuest.showUnavailable(player);
        }
        DialogueManager.startDialogue(player, n);
        return true;
    }

    @Override
    public final boolean handleFirstObjectAction(Player player, int n, int n2, int n3, int n4) {
        if (!WaterfallQuest.isWaterfallObject(n)) {
            return false;
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return WaterfallQuest.showUnavailable(player);
        }
        int n5 = WaterfallQuest.getProgress(n4);
        if (n == GLARIALS_TOMBSTONE) {
            player.getDialogueManager().showThreeLineStatement("The tombstone reads:", "Here lies Glarial, wife of Baxtorian.", "Only the pure of heart may enter her resting place.");
            return true;
        }
        if (n == GLARIALS_TOMB) {
            if (n5 < STATE_ENTERED_TOMB) {
                player.packetSender.sendGameMessage("You do not know how to enter this tomb yet.");
                return true;
            }
            WaterfallQuest.giveItemIfMissing(player, GLARIALS_URN_FULL, "You find Glarial's urn.");
            return true;
        }
        if (n == CLOSED_CHEST || n == OPEN_CHEST) {
            if (n5 < STATE_ENTERED_TOMB) {
                player.packetSender.sendGameMessage("The chest is sealed.");
                return true;
            }
            WaterfallQuest.giveItemIfMissing(player, GLARIALS_AMULET, "You find Glarial's amulet.");
            return true;
        }
        if (n == LOG_RAFT) {
            if (n5 < STATE_STARTED) {
                player.packetSender.sendGameMessage("The raft looks too dangerous to use without a reason.");
                return true;
            }
            player.packetSender.sendGameMessage("You board the raft and are swept down the river.");
            player.moveTo(RAFT_CRASH_POSITION);
            if (n5 == STATE_STARTED) {
                WaterfallQuest.setProgress(player, STATE_SPOKE_TO_HUDON);
                player.packetSender.sendGameMessage("Hudon calls out that he is safe on the riverbank.");
            }
            return true;
        }
        if (n == CROSSING_ROCK_1 || n == CROSSING_ROCK_2 || n == WATERFALL_ROCKS_1 || n == WATERFALL_ROCKS_2) {
            player.packetSender.sendGameMessage("You lose your footing and the water sweeps you downstream.");
            WaterfallQuest.failWaterfall(player);
            return true;
        }
        if (n == OVERHANGING_TREE_1 || n == OVERHANGING_TREE_2 || n == BARREL) {
            player.packetSender.sendGameMessage("You slip and tumble over the waterfall.");
            WaterfallQuest.failWaterfall(player);
            return true;
        }
        if (n == WATERFALL_LEDGE_LEFT || n == WATERFALL_LEDGE_MIDDLE || n == WATERFALL_LEDGE_RIGHT || n == WHIRLPOOL) {
            player.packetSender.sendGameMessage("You need something to secure a safe crossing here.");
            return true;
        }
        if (n == BOOKCASE) {
            if (n5 < STATE_SPOKE_TO_HUDON) {
                player.packetSender.sendGameMessage("You see many tourist books about the waterfall.");
                return true;
            }
            WaterfallQuest.giveItemIfMissing(player, BOOK_ON_BAXTORIAN, "You find a book on Baxtorian.");
            return true;
        }
        if (n == GOLRIE_CRATE) {
            if (n5 < STATE_READ_BOOK) {
                player.packetSender.sendGameMessage("You search the crate but find nothing of interest.");
                return true;
            }
            WaterfallQuest.giveItemIfMissing(player, GOLRIE_KEY, "You find a small key.");
            return true;
        }
        if (n == GOLRIE_GATE) {
            return WaterfallQuest.openGolrieGate(player);
        }
        if (n == BAXTORIAN_CRATE) {
            if (n5 < STATE_ENTERED_WATERFALL) {
                player.packetSender.sendGameMessage("You search the crate but find nothing useful.");
                return true;
            }
            WaterfallQuest.giveItemIfMissing(player, BAXTORIAN_KEY, "You find Baxtorian's key.");
            return true;
        }
        if (n == WATERFALL_LEDGE_DOOR || n == WATERFALL_LEDGE_DOOR_LEFT || n == WATERFALL_LEDGE_DOOR_RIGHT) {
            return WaterfallQuest.enterWaterfall(player, n5);
        }
        if (n == WATERFALL_EXIT_DOOR) {
            player.moveTo(WATERFALL_LEDGE_POSITION);
            return true;
        }
        if (WaterfallQuest.isBaxtorianDoorObject(n)) {
            return WaterfallQuest.openBaxtorianDoor(player, n5, n2, n3, false);
        }
        if (n == STONE_PILLAR) {
            player.packetSender.sendGameMessage("The pillar has symbols for air, earth and water runes.");
            return true;
        }
        if (n == STATUE_OF_BAXTORIAN) {
            player.getDialogueManager().showTwoLineStatement("The statue of Baxtorian gazes over the chamber.", "It seems tied to the old waterfall legend.");
            return true;
        }
        if (n == STATUE_OF_GLARIAL) {
            player.packetSender.sendGameMessage("The statue seems to be waiting for an offering.");
            return true;
        }
        if (WaterfallQuest.isChaliceObject(n) && WaterfallQuest.isBaxtorianChaliceCoord(n2, n3)) {
            if (n5 == STATE_COMPLETE) {
                player.packetSender.sendGameMessage("The chalice is filled with ancient ashes.");
                return true;
            }
            player.packetSender.sendGameMessage("A torrent of water floods the room.");
            WaterfallQuest.failWaterfall(player);
            return true;
        }
        return true;
    }

    @Override
    public final boolean handleSecondObjectAction(Player player, int n, int n2, int n3, int n4) {
        if (!WaterfallQuest.isWaterfallObject(n)) {
            return false;
        }
        return this.handleFirstObjectAction(player, n, n2, n3, n4);
    }

    @Override
    public final boolean handleItemOnObject(Player player, int n, int n2, int n3) {
        if (!WaterfallQuest.isWaterfallObject(n2)) {
            return false;
        }
        if (!WaterfallQuest.isContentAvailable()) {
            return WaterfallQuest.showUnavailable(player);
        }
        int n4 = WaterfallQuest.getProgress(n3);
        if (n == GLARIALS_PEBBLE && n2 == GLARIALS_TOMBSTONE) {
            return WaterfallQuest.enterGlarialsTomb(player, n4);
        }
        if (n == ROPE && (n2 == CROSSING_ROCK_1 || n2 == CROSSING_ROCK_2 || n2 == WATERFALL_ROCKS_1 || n2 == WATERFALL_ROCKS_2)) {
            return WaterfallQuest.useRopeCrossing(player, false);
        }
        if (n == ROPE && (n2 == OVERHANGING_TREE_1 || n2 == OVERHANGING_TREE_2)) {
            return WaterfallQuest.useRopeCrossing(player, true);
        }
        if (n == GOLRIE_KEY && n2 == GOLRIE_GATE) {
            return WaterfallQuest.openGolrieGate(player);
        }
        if (n == BAXTORIAN_KEY && WaterfallQuest.isBaxtorianDoorObject(n2) && WaterfallQuest.isBaxtorianDoorCoord(player.getInteractionTargetX(), player.getInteractionTargetY())) {
            return WaterfallQuest.openBaxtorianDoor(player, n4, player.getInteractionTargetX(), player.getInteractionTargetY(), true);
        }
        if (WaterfallQuest.isPuzzleRune(n) && n2 == STONE_PILLAR && WaterfallQuest.isPuzzlePillarCoord(player.getInteractionTargetX(), player.getInteractionTargetY())) {
            return WaterfallQuest.placeRuneOnPillar(player, n, n4, player.getInteractionTargetX(), player.getInteractionTargetY());
        }
        if (n == GLARIALS_AMULET && n2 == STATUE_OF_GLARIAL && WaterfallQuest.isOriginalGlarialStatueCoord(player.getInteractionTargetX(), player.getInteractionTargetY())) {
            return WaterfallQuest.placeAmuletOnStatue(player, n3, n4, player.getInteractionTargetX(), player.getInteractionTargetY());
        }
        if (n == GLARIALS_URN_FULL && WaterfallQuest.isChaliceObject(n2) && WaterfallQuest.isBaxtorianChaliceCoord(player.getInteractionTargetX(), player.getInteractionTargetY())) {
            return WaterfallQuest.completeAtChalice(player, n4);
        }
        return false;
    }

    private boolean handleAlmeraDialogue(Player player, int n, int n2, int n3) {
        if (n == 4) {
            player.getDialogueManager().showNpcOneLineDialogue("Thank you. I last saw him by the river.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (n3 == 0) {
            if (n == 1) {
                player.getDialogueManager().showNpcTwoLineDialogue("Oh dear, my boy Hudon is missing near the", "waterfall. Please, can you help me find him?", 591);
                return true;
            }
            if (n == 2) {
                player.getDialogueManager().showTwoOptions("I'll help look for him.", "I'm a bit busy right now.");
                return true;
            }
            if (n == 3) {
                if (n2 == 1) {
                    this.startQuest(player);
                    player.getDialogueManager().showPlayerOneLineDialogue("Yes, I'll go and look for him.", 591);
                    player.getDialogueManager().setNextDialogueStep(4);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I'm a bit busy right now.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (n3 == STATE_COMPLETE) {
            if (n == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("Thank you again for helping with the waterfall.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (n == 1) {
            player.getDialogueManager().showNpcTwoLineDialogue("Please look for Hudon near the waterfall.", "That river is far too dangerous for him.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        player.getDialogueManager().finishDialogue();
        return true;
    }

    private boolean handleHudonDialogue(Player player, int n, int n2) {
        if (n2 < STATE_STARTED) {
            if (n == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("I'm watching the waterfall.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (n2 == STATE_STARTED) {
            if (n == 1) {
                player.getDialogueManager().showNpcTwoLineDialogue("I'm fine, but I can't get back across the river.", "There is something strange about this waterfall.", 591);
                return true;
            }
            if (n == 2) {
                WaterfallQuest.setProgress(player, STATE_SPOKE_TO_HUDON);
                player.getDialogueManager().showPlayerOneLineDialogue("I'll tell your mother and look into it.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (n == 1) {
            player.getDialogueManager().showNpcOneLineDialogue("Hadley knows a lot about this place.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        player.getDialogueManager().finishDialogue();
        return true;
    }

    private boolean handleHadleyDialogue(Player player, int n, int n2, int n3) {
        if (n2 == 1) {
            if (n == GERALD) {
                player.getDialogueManager().showNpcOneLineDialogue("Hadley is the man to ask about Baxtorian Falls.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (n3 < STATE_SPOKE_TO_HUDON) {
                player.getDialogueManager().showNpcTwoLineDialogue("Welcome to the tourist centre.", "The waterfall is our finest attraction.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (n3 < STATE_READ_BOOK) {
                player.getDialogueManager().showNpcTwoLineDialogue("There are old stories about Baxtorian and Glarial.", "You may find one of my books useful.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showNpcTwoLineDialogue("Glarial's pebble is said to open her tomb.", "Only unarmed visitors may enter that sacred place.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        player.getDialogueManager().finishDialogue();
        return true;
    }

    private boolean handleGolrieDialogue(Player player, int n, int n2) {
        if (n2 < STATE_READ_BOOK) {
            if (n == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("Please leave me alone.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (n == 1) {
            if (!player.ownsItem(GLARIALS_PEBBLE)) {
                if (!WaterfallQuest.isGolrieGateUnlocked(player)) {
                    if (player.getInventoryManager().containsItem(GOLRIE_KEY)) {
                        player.getDialogueManager().showNpcTwoLineDialogue("Thank you for finding the key.", "Please unlock the gate and let me out.", 591);
                    } else {
                        player.getDialogueManager().showNpcTwoLineDialogue("I'm locked in here.", "There should be a key somewhere nearby.", 591);
                    }
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                if (!player.getInventoryManager().addItem(new ItemStack(GLARIALS_PEBBLE, 1))) {
                    player.packetSender.sendGameMessage("You need more inventory space to take the pebble.");
                    return true;
                }
                player.getDialogueManager().showNpcTwoLineDialogue("Thank you for letting me out.", "Take this pebble. It belonged to Glarial.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showNpcOneLineDialogue("The pebble should help you find Glarial's tomb.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        player.getDialogueManager().finishDialogue();
        return true;
    }

    private static boolean completeAtChalice(Player player, int n) {
        if (n == STATE_COMPLETE) {
            player.packetSender.sendGameMessage("The chalice is filled with ancient ashes.");
            return true;
        }
        if (n < STATE_PLACED_AMULET) {
            player.packetSender.sendGameMessage("The urn does not belong here yet.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(GLARIALS_URN_FULL)) {
            player.packetSender.sendGameMessage("You need Glarial's urn.");
            return true;
        }
        if (player.getInventoryManager().getContainer().getFreeSlots() < 5) {
            player.packetSender.sendGameMessage("You need at least five free inventory spaces to take the treasure.");
            return true;
        }
        player.packetSender.sendGameMessage("You carefully pour the ashes into the chalice.");
        player.packetSender.sendGameMessage("Inside you find a mithril case containing treasure.");
        player.getInventoryManager().removeItem(new ItemStack(GLARIALS_URN_FULL, 1));
        QuestDefinition.getQuestScript(QUEST_ID).awardCompletionRewards(player);
        return true;
    }

    private static boolean placeAmuletOnStatue(Player player, int n, int n2, int n3, int n4) {
        if (!WaterfallQuest.isOriginalGlarialStatueCoord(n3, n4)) {
            return false;
        }
        if (n2 < STATE_PUZZLE_ROOM) {
            player.packetSender.sendGameMessage("The statue does not respond.");
            return true;
        }
        if ((n & ALL_RUNES_MASK) != ALL_RUNES_MASK) {
            player.packetSender.sendGameMessage("A boulder drops from above. The pillars are not prepared.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(GLARIALS_AMULET)) {
            player.packetSender.sendGameMessage("You need Glarial's amulet.");
            return true;
        }
        player.getInventoryManager().removeItem(new ItemStack(GLARIALS_AMULET, 1));
        WaterfallQuest.setProgress(player, STATE_PLACED_AMULET);
        player.packetSender.sendGameMessage("The statue accepts the amulet and the chamber rises.");
        player.moveTo(RAISED_ROOM_POSITION);
        return true;
    }

    private static boolean placeRuneOnPillar(Player player, int n, int n2, int n3, int n4) {
        int n5 = WaterfallQuest.getPillarIndex(n3, n4);
        if (n5 == -1) {
            return false;
        }
        if (n2 < STATE_PUZZLE_ROOM) {
            player.packetSender.sendGameMessage("You are not sure where this rune belongs.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(n)) {
            return true;
        }
        int n6 = WaterfallQuest.getRuneIndex(n);
        int n7 = RUNE_BIT_START + n5 * 3 + n6;
        int n8 = 1 << n7;
        if ((player.getQuestState(QUEST_ID) & n8) != 0) {
            player.packetSender.sendGameMessage("You remember putting that type of rune there.");
            return true;
        }
        player.getInventoryManager().removeItem(new ItemStack(n, 1));
        player.setQuestState(QUEST_ID, player.getQuestState(QUEST_ID) | n8);
        player.packetSender.sendGameMessage("You place the rune on the stand.");
        player.packetSender.sendGameMessage("The rune stone disappears in a puff of smoke.");
        return true;
    }

    private static boolean openBaxtorianDoor(Player player, int n, int n2, int n3, boolean bl) {
        if (!WaterfallQuest.isBaxtorianDoorCoord(n2, n3)) {
            return false;
        }
        if (!bl) {
            if (WaterfallQuest.isFireGiantCorridorDoorCoord(n2, n3)) {
                player.packetSender.sendGameMessage("The door is locked.");
                return true;
            }
            if (WaterfallQuest.isRaisedBaxtorianDoorCoord(n2, n3) && player.getPosition().getY() >= 9895) {
                player.packetSender.sendGameMessage("You open the door and walk through.");
                player.moveTo(ORIGINAL_ROOM_EXIT_POSITION);
                return true;
            }
            if (WaterfallQuest.isOriginalBaxtorianDoorCoord(n2, n3) && player.getPosition().getY() > n3) {
                player.packetSender.sendGameMessage("You open the door and walk through.");
                player.moveTo(ORIGINAL_ROOM_EXIT_POSITION);
                return true;
            }
            player.packetSender.sendGameMessage("The door is locked.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(BAXTORIAN_KEY)) {
            player.packetSender.sendGameMessage("You need Baxtorian's key to unlock this door.");
            return true;
        }
        player.packetSender.sendGameMessage("You open the door and walk through.");
        if (WaterfallQuest.isFireGiantCorridorDoorCoord(n2, n3)) {
            if (player.getPosition().getY() > 9893) {
                player.moveTo(FIRE_GIANT_ROOM_DOOR_POSITION);
            } else {
                player.moveTo(FIRE_GIANT_CORRIDOR_DOOR_POSITION);
            }
            return true;
        }
        if (WaterfallQuest.isOriginalBaxtorianDoorCoord(n2, n3)) {
            if (player.getPosition().getY() > n3) {
                player.moveTo(ORIGINAL_ROOM_EXIT_POSITION);
                return true;
            }
            if (n == STATE_ENTERED_WATERFALL) {
                WaterfallQuest.setProgress(player, STATE_PUZZLE_ROOM);
            }
            if (n >= STATE_PLACED_AMULET) {
                player.moveTo(RAISED_ROOM_POSITION);
                return true;
            }
            player.moveTo(PUZZLE_ROOM_POSITION);
            return true;
        }
        if (WaterfallQuest.isRaisedBaxtorianDoorCoord(n2, n3)) {
            player.moveTo(ORIGINAL_ROOM_EXIT_POSITION);
            return true;
        }
        if (n < STATE_PUZZLE_ROOM && n >= STATE_ENTERED_WATERFALL) {
            WaterfallQuest.setProgress(player, STATE_PUZZLE_ROOM);
        }
        player.moveTo(PUZZLE_ROOM_POSITION);
        return true;
    }

    private static boolean enterWaterfall(Player player, int n) {
        if (n < STATE_ENTERED_TOMB) {
            player.packetSender.sendGameMessage("You do not know how to enter the waterfall safely.");
            WaterfallQuest.failWaterfall(player);
            return true;
        }
        if (!player.getInventoryManager().containsItem(GLARIALS_AMULET) && !player.getEquipmentManager().containsItem(GLARIALS_AMULET)) {
            player.packetSender.sendGameMessage("A torrent of water pushes you away. You need Glarial's amulet.");
            WaterfallQuest.failWaterfall(player);
            return true;
        }
        if (n < STATE_ENTERED_WATERFALL) {
            WaterfallQuest.setProgress(player, STATE_ENTERED_WATERFALL);
        }
        player.packetSender.sendGameMessage("Glarial's amulet protects you as you enter the waterfall.");
        player.moveTo(WATERFALL_ENTRY_POSITION);
        return true;
    }

    private static boolean enterGlarialsTomb(Player player, int n) {
        if (n < STATE_READ_BOOK) {
            player.packetSender.sendGameMessage("You are not sure what to do with the pebble here.");
            return true;
        }
        if (!WaterfallQuest.canEnterGlarialsTomb(player)) {
            return true;
        }
        WaterfallQuest.setProgress(player, STATE_ENTERED_TOMB);
        player.packetSender.sendGameMessage("The pebble glows and the tomb opens.");
        player.moveTo(GLARIALS_TOMB_POSITION);
        return true;
    }

    private static boolean openGolrieGate(Player player) {
        if (!WaterfallQuest.isGolrieGateUnlocked(player) && !player.ownsItem(GLARIALS_PEBBLE)) {
            if (!player.getInventoryManager().containsItem(GOLRIE_KEY)) {
                player.packetSender.sendGameMessage("The gate is locked.");
                return true;
            }
            player.getInventoryManager().removeItem(new ItemStack(GOLRIE_KEY, 1));
            WaterfallQuest.unlockGolrieGate(player);
            player.packetSender.sendGameMessage("You unlock the gate.");
        }
        int n = player.getInteractionTargetX();
        int n2 = player.getInteractionTargetY();
        if (WaterfallQuest.isGolrieGateCoord(n, n2)) {
            boolean bl = player.getPosition().getY() <= 9575;
            player.moveTo(new Position(2515, bl ? 9576 : 9575, player.getPosition().getPlane()));
            player.packetSender.sendSoundEffect(318, 1, 0);
            if (bl) {
                player.packetSender.sendGameMessage("You open the gate and walk through.");
            }
            return true;
        }
        if (n <= 0) {
            n = player.getPosition().getX();
        }
        n2 = player.getPosition().getX() <= n ? 1 : -1;
        player.packetSender.queueRelativeMovementStep(n2, 0, true);
        player.packetSender.sendSoundEffect(318, 1, 0);
        return true;
    }

    private static boolean useRopeCrossing(Player player, boolean bl) {
        if (!player.getInventoryManager().containsItem(ROPE)) {
            return true;
        }
        player.getInventoryManager().removeItem(new ItemStack(ROPE, 1));
        if (bl) {
            player.packetSender.sendGameMessage("You secure the rope and swing across to the ledge.");
            player.moveTo(WATERFALL_LEDGE_POSITION);
            return true;
        }
        int n = player.getInteractionTargetX();
        int n2 = player.getInteractionTargetY();
        if (n <= 0 || n2 <= 0) {
            player.packetSender.sendGameMessage("You secure the rope and cross safely.");
            player.moveTo(WATERFALL_LEDGE_POSITION);
            return true;
        }
        int n3 = player.getPosition().getX() <= n ? n + 1 : n - 1;
        if (WaterfallQuest.isHudonCrossingObjectCoord(n, n2)) {
            WaterfallQuest.createTemporaryCrossingRope(player, n, n2, player.getPosition().getPlane());
        }
        player.packetSender.sendGameMessage("You secure the rope and cross safely.");
        player.moveTo(new Position(n3, n2, player.getPosition().getPlane()));
        return true;
    }

    private static void createTemporaryCrossingRope(Player player, int n, int n2, int n3) {
        int n4 = 20;
        new DynamicObject(CROSSING_ROCK_2, n, n2, n3, 2, 10, CROSSING_ROCK_1, n4, false);
        int n5 = 1;
        while (n5 < 8) {
            new DynamicObject(CROSSING_ROPE, n, n2 + n5, n3, 2, 10, 0, n4, false);
            ++n5;
        }
    }

    private static boolean canEnterGlarialsTomb(Player player) {
        ItemStack[] itemStackArray = player.getEquipmentManager().getContainer().getItems();
        int n = 0;
        while (n < itemStackArray.length) {
            ItemStack itemStack = itemStackArray[n];
            if (itemStack != null && itemStack.getId() != -1) {
                player.packetSender.sendGameMessage("You cannot take weapons or armour into Glarial's tomb.");
                return false;
            }
            ++n;
        }
        itemStackArray = player.getInventoryManager().getContainer().getItems();
        n = 0;
        while (n < itemStackArray.length) {
            ItemStack itemStack = itemStackArray[n];
            if (itemStack != null && itemStack.getId() != -1) {
                int n2 = itemStack.getId();
                if (WaterfallQuest.isRune(n2)) {
                    player.packetSender.sendGameMessage("You cannot take runes into Glarial's tomb.");
                    return false;
                }
                if (ItemDefinition.isDefined(n2) && ItemDefinition.forId(n2).getEquipmentSlot() != -1) {
                    player.packetSender.sendGameMessage("You cannot take weapons or armour into Glarial's tomb.");
                    return false;
                }
            }
            ++n;
        }
        return true;
    }

    private static void failWaterfall(Player player) {
        player.moveTo(FAILED_WATERFALL_POSITION);
    }

    private static void giveItemIfMissing(Player player, int n, String string) {
        if (player.ownsItem(n)) {
            player.packetSender.sendGameMessage("You already have this item.");
            return;
        }
        if (!player.getInventoryManager().addItem(new ItemStack(n, 1))) {
            player.packetSender.sendGameMessage("You need more inventory space.");
            return;
        }
        player.packetSender.sendGameMessage(string);
    }

    private static int getProgress(int n) {
        if (n == STATE_COMPLETE) {
            return STATE_COMPLETE;
        }
        return n & PROGRESS_MASK;
    }

    private static void setProgress(Player player, int n) {
        int n2 = player.getQuestState(QUEST_ID);
        player.setQuestState(QUEST_ID, n2 & ~PROGRESS_MASK | n);
    }

    private static boolean isGolrieGateUnlocked(Player player) {
        return (player.getQuestState(QUEST_ID) & GOLRIE_GATE_UNLOCKED_BIT) != 0;
    }

    private static void unlockGolrieGate(Player player) {
        player.setQuestState(QUEST_ID, player.getQuestState(QUEST_ID) | GOLRIE_GATE_UNLOCKED_BIT);
    }

    private static int getPillarIndex(int n, int n2) {
        int n3 = 0;
        while (n3 < PILLAR_COORDINATES.length) {
            if (PILLAR_COORDINATES[n3][0] == n && PILLAR_COORDINATES[n3][1] == n2) {
                return n3;
            }
            ++n3;
        }
        return -1;
    }

    private static int getFirstUnsetRuneBit(int n, int n2) {
        int n3 = 0;
        while (n3 < 6) {
            int n4 = RUNE_BIT_START + n3 * 3 + n2;
            if ((n & 1 << n4) == 0) {
                return n4;
            }
            ++n3;
        }
        return -1;
    }

    private static int getRuneIndex(int n) {
        if (n == AIR_RUNE) {
            return 0;
        }
        if (n == EARTH_RUNE) {
            return 1;
        }
        return 2;
    }

    private static boolean isPuzzleRune(int n) {
        return n == AIR_RUNE || n == EARTH_RUNE || n == WATER_RUNE;
    }

    private static boolean isRune(int n) {
        return n >= 554 && n <= 566;
    }

    private static boolean isWaterfallNpc(int n) {
        return n == HADLEY || n == GERALD || n == ALMERA || n == HUDON || n == GOLRIE;
    }

    private static boolean isWaterfallObject(int n) {
        return n == LOG_RAFT || n == BOOKCASE || n == GOLRIE_CRATE || n == GOLRIE_GATE || n == GLARIALS_TOMBSTONE || n == GLARIALS_TOMB || n == CLOSED_CHEST || n == OPEN_CHEST || n == CROSSING_ROCK_1 || n == CROSSING_ROCK_2 || n == CROSSING_ROPE || n == BAXTORIAN_CRATE || n == WATERFALL_EXIT_DOOR || WaterfallQuest.isBaxtorianDoorObject(n) || n == STONE_PILLAR || n == STATUE_OF_BAXTORIAN || n == STATUE_OF_GLARIAL || n == WATERFALL_LEDGE_LEFT || n == WATERFALL_LEDGE_MIDDLE || n == WATERFALL_LEDGE_RIGHT || n == WATERFALL_LEDGE_DOOR || n == WATERFALL_LEDGE_DOOR_LEFT || n == WATERFALL_LEDGE_DOOR_RIGHT || WaterfallQuest.isChaliceObject(n) || n == WHIRLPOOL || n == OVERHANGING_TREE_1 || n == OVERHANGING_TREE_2 || n == BARREL || n == WATERFALL_ROCKS_1 || n == WATERFALL_ROCKS_2;
    }

    private static boolean isBaxtorianDoorObject(int n) {
        return n == BAXTORIAN_DOOR || n == BAXTORIAN_DOOR_OPEN || n == BAXTORIAN_DOOR_OPEN_ALT;
    }

    private static boolean isChaliceObject(int n) {
        return n == CHALICE_1 || n == CHALICE_2;
    }

    private static boolean isRiverSwimObject(int n, String string) {
        if (n == CROSSING_ROCK_1 || n == CROSSING_ROCK_2 || n == WATERFALL_ROCKS_1 || n == WATERFALL_ROCKS_2) {
            return true;
        }
        String string2 = string == null ? "" : string.toLowerCase();
        return string2.contains("river") || string2.contains("rock");
    }

    private static boolean isHudonIslandRopeObject(int n, String string) {
        if (n == CROSSING_ROCK_1 || n == CROSSING_ROCK_2 || n == WATERFALL_ROCKS_1 || n == WATERFALL_ROCKS_2) {
            return true;
        }
        String string2 = string == null ? "" : string.toLowerCase();
        return string2.contains("rock") || string2.contains("river");
    }

    private static boolean isHudonCrossingInteraction(Player player, int n, int n2) {
        return WaterfallQuest.isHudonCrossingObjectCoord(n, n2) || WaterfallQuest.isHudonIslandPlayerCoord(player.getPosition().getX(), player.getPosition().getY());
    }

    private static boolean isHudonCrossingObjectCoord(int n, int n2) {
        return n >= 2509 && n <= 2517 && n2 >= 3466 && n2 <= 3484;
    }

    private static boolean isHudonIslandPlayerCoord(int n, int n2) {
        return n >= 2509 && n <= 2516 && n2 >= 3474 && n2 <= 3487;
    }

    private static boolean isGnomeCaveEntranceObject(int n, String string, int n2, int n3) {
        return n == GNOME_CAVE_ENTRANCE && WaterfallQuest.isNear(n2, n3, 2408, 3417, 6);
    }

    private static boolean isGnomeCaveLadderObject(int n, String string, int n2, int n3) {
        return n == GNOME_CAVE_LADDER && WaterfallQuest.isNear(n2, n3, 2409, 9818, 6);
    }

    private static boolean isTreeGnomeVillageDungeonSurfaceInteraction(Player player, int n, int n2) {
        return player.getPosition().getY() < 6400 && WaterfallQuest.isNear(n, n2, 2533, 3155, 4);
    }

    private static boolean isTreeGnomeVillageDungeonUndergroundInteraction(Player player, int n, int n2) {
        return player.getPosition().getY() >= 6400 && WaterfallQuest.isNear(n, n2, 2533, 9555, 4);
    }

    private static boolean isGolrieGateCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2515, 9575, 3);
    }

    private static boolean isTreeGnomeVillageDungeonArea(int n, int n2) {
        return n >= 2505 && n <= 2555 && n2 >= 9545 && n2 <= 9590;
    }

    private static boolean isGlarialsTombExitLadder(int n, int n2, int n3) {
        return n == GLARIALS_TOMB_EXIT_LADDER && WaterfallQuest.isNear(n2, n3, 2556, 9844, 6);
    }

    private static boolean isOverhangingTreeObject(int n, int n2, int n3) {
        return (n == OVERHANGING_TREE_1 || n == OVERHANGING_TREE_2) && WaterfallQuest.isNear(n2, n3, 2512, 3465, 4);
    }

    private static boolean isGlarialsTombstoneCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2558, 3444, 4);
    }

    private static boolean isWaterfallLedgeDoor(int n, int n2, int n3) {
        return (n == WATERFALL_LEDGE_DOOR || n == WATERFALL_LEDGE_DOOR_LEFT || n == WATERFALL_LEDGE_DOOR_RIGHT) && WaterfallQuest.isNear(n2, n3, 2511, 3464, 4);
    }

    private static boolean isWaterfallExitDoorCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2575, 9861, 4);
    }

    private static boolean isBaxtorianDoorCoord(int n, int n2) {
        return n >= 2560 && n <= 2610 && n2 >= 9888 && n2 <= 9905;
    }

    private static boolean isOriginalBaxtorianDoorCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2566, 9901, 1);
    }

    private static boolean isFireGiantCorridorDoorCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2568, 9893, 1);
    }

    private static boolean isRaisedBaxtorianDoorCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2604, 9901, 1) || WaterfallQuest.isNear(n, n2, 2606, 9892, 1);
    }

    private static boolean isPuzzlePillarCoord(int n, int n2) {
        return WaterfallQuest.getPillarIndex(n, n2) != -1;
    }

    private static boolean isOriginalGlarialStatueCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2565, 9916, 1);
    }

    private static boolean isBaxtorianChaliceCoord(int n, int n2) {
        return WaterfallQuest.isNear(n, n2, 2603, 9910, 2);
    }

    private static boolean isPlayerNearObject(Player player, int n, int n2, int n3) {
        return WaterfallQuest.isNear(player.getPosition().getX(), player.getPosition().getY(), n, n2, n3);
    }

    private static boolean isNear(int n, int n2, int n3, int n4, int n5) {
        return Math.abs(n - n3) <= n5 && Math.abs(n2 - n4) <= n5;
    }

    private static boolean showUnavailable(Player player) {
        player.getDialogueManager().showOneLineStatement("Waterfall Quest is not available in this cache.");
        return true;
    }

    private static void spawnNpcIfMissing(int n, int n2, int n3, int n4, int n5) {
        if (Npc.findByDefinitionId(n) != null) {
            return;
        }
        GameplayHelper.spawnNpc(n, n2, n3, n4, n5);
    }

    private static void spawnNpcIfMissingAt(int n, int n2, int n3, int n4, int n5) {
        Position position = new Position(n2, n3, n4);
        if (!NpcDefinition.isDefined(n) || Npc.findByDefinitionIdAtPosition(n, position) != null) {
            return;
        }
        GameplayHelper.spawnNpc(n, n2, n3, n4, n5);
    }

    private static boolean isContentAvailable() {
        return ItemDefinition.isDefined(BOOK_ON_BAXTORIAN) && ItemDefinition.isDefined(GLARIALS_URN_EMPTY) && ItemDefinition.isDefined(MITHRIL_SEEDS) && ItemDefinition.isDefined(DIAMOND) && ItemDefinition.isDefined(GOLD_BAR) && NpcDefinition.isDefined(ALMERA) && NpcDefinition.isDefined(GOLRIE) && GameplayHelper.isObjectDefinitionIdValid(LOG_RAFT) && GameplayHelper.isObjectDefinitionIdValid(GLARIALS_TOMBSTONE) && GameplayHelper.isObjectDefinitionIdValid(CHALICE_1);
    }
}
