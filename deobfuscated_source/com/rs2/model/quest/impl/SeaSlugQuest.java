/*
 * Sea Slug quest content, matched against the local 2004 base scripts.
 */
package com.rs2.model.quest.impl;

import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestScript;
import com.rs2.util.GameUtil;
import java.awt.Color;

public final class SeaSlugQuest
extends QuestScript {
    public static final int QUEST_ID = 81;
    private static final int STATE_COMPLETE = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_SPOKEN_HOLGART = 3;
    private static final int STATE_BOAT_REPAIRED = 4;
    private static final int STATE_SPOKEN_KENNITH = 5;
    private static final int STATE_SAILED_KENT = 6;
    private static final int STATE_SPOKEN_KENT = 7;
    private static final int STATE_LIT_TORCH = 8;
    private static final int STATE_KENNITH_NEED_ESCAPE = 9;
    private static final int STATE_PANEL_OPENED = 10;
    private static final int STATE_NEED_KENNITH_PATH = 11;
    private static final int STATE_SAVED_KENNITH = 12;

    private static final int COOKING = 7;
    private static final int FISHING = 10;
    private static final int FIREMAKING = 11;

    private static final int BIG_OYSTER_PEARLS = 413;
    private static final int TINDERBOX = 590;
    private static final int TORCH_LIT = 594;
    private static final int TORCH_UNLIT = 596;
    private static final int SEA_SLUG_ITEM = 1466;
    private static final int DAMP_STICKS = 1467;
    private static final int DRY_STICKS = 1468;
    private static final int GENERIC_BROKEN_GLASS = 690;
    private static final int BROKEN_GLASS = 1469;
    private static final int EMPTY_POT = 1931;
    private static final int POT_OF_FLOUR = 1933;
    private static final int SWAMP_TAR = 1939;
    private static final int RAW_SWAMP_PASTE = 1940;
    private static final int SWAMP_PASTE = 1941;

    private static final int FISHING_SPOT = 316;
    private static final int BAILEY = 695;
    private static final int CAROLINE = 696;
    private static final int KENNITH = 697;
    private static final int HOLGART_ISLAND = 698;
    private static final int HOLGART_PLATFORM = 699;
    private static final int HOLGART_SHORE = 700;
    private static final int KENT = 701;
    private static final int FISHERMAN_1 = 702;
    private static final int FISHERMAN_2 = 703;
    private static final int FISHERMAN_3 = 704;
    private static final int FISHERMAN_4 = 705;
    private static final int SEA_SLUG_NPC = 1006;

    private static final int ROW_BOAT = 2515;
    private static final int SUNK_ROW_BOAT = 2516;
    private static final int SLUG_LADDER = 2517;
    private static final int LOOSE_PANEL = 2518;
    private static final int KENNITH_WALL = 2519;
    private static final int FISHING_CRANE = 2520;
    private static final int FISHING_CRANE_NOSCRIPT = 2521;
    private static final int PLATFORM_UP_LADDER = 1746;

    private static final Position SHORE_POSITION = new Position(2722, 3305, 0);
    private static final Position PLATFORM_POSITION = new Position(2782, 3273, 0);
    private static final Position ISLAND_POSITION = new Position(2800, 3320, 0);
    private static final Position PLATFORM_LADDER_DOWN = new Position(2784, 3285, 0);
    private static final Position PLATFORM_LADDER_UP = new Position(2784, 3287, 1);
    private static final Position BROKEN_GLASS_POSITION = new Position(2766, 3277, 0);

    private static final int[][] NPC_SPAWNS = new int[][]{
        {CAROLINE, 2716, 3302, 0, 2},
        {HOLGART_SHORE, 2720, 3306, 0, 2},
        {HOLGART_PLATFORM, 2782, 3276, 0, 2},
        {BAILEY, 2763, 3276, 0, 3},
        {KENNITH, 2764, 3288, 1, 1},
        {HOLGART_ISLAND, 2799, 3320, 0, 1},
        {KENT, 2793, 3321, 0, 1},
        {FISHERMAN_1, 2768, 3276, 0, 4},
        {FISHERMAN_1, 2772, 3273, 0, 4},
        {FISHERMAN_1, 2775, 3285, 0, 4},
        {FISHERMAN_1, 2781, 3290, 1, 4},
        {FISHERMAN_1, 2785, 3284, 1, 4},
        {FISHERMAN_2, 2768, 3285, 0, 4},
        {FISHERMAN_2, 2794, 3279, 0, 4},
        {FISHERMAN_2, 2766, 3285, 1, 4},
        {FISHERMAN_2, 2784, 3277, 1, 4},
        {FISHERMAN_3, 2778, 3291, 0, 4},
        {FISHERMAN_3, 2771, 3282, 1, 4},
        {FISHERMAN_3, 2787, 3280, 1, 4},
        {FISHING_SPOT, 2789, 3273, 0, 0},
        {FISHING_SPOT, 2790, 3273, 0, 0},
        {FISHING_SPOT, 2794, 3283, 0, 0},
        {FISHING_SPOT, 2795, 3279, 0, 0},
        {SEA_SLUG_NPC, 2761, 3284, 0, 2},
        {SEA_SLUG_NPC, 2766, 3277, 0, 2},
        {SEA_SLUG_NPC, 2766, 3288, 0, 2},
        {SEA_SLUG_NPC, 2768, 3277, 0, 2},
        {SEA_SLUG_NPC, 2768, 3290, 0, 2},
        {SEA_SLUG_NPC, 2769, 3279, 0, 2},
        {SEA_SLUG_NPC, 2774, 3291, 0, 2},
        {SEA_SLUG_NPC, 2778, 3285, 0, 2},
        {SEA_SLUG_NPC, 2781, 3278, 0, 2},
        {SEA_SLUG_NPC, 2781, 3285, 0, 2},
        {SEA_SLUG_NPC, 2783, 3275, 0, 2},
        {SEA_SLUG_NPC, 2784, 3279, 0, 2},
        {SEA_SLUG_NPC, 2785, 3276, 0, 2},
        {SEA_SLUG_NPC, 2785, 3287, 0, 2},
        {SEA_SLUG_NPC, 2788, 3274, 0, 2},
        {SEA_SLUG_NPC, 2793, 3275, 0, 2},
        {SEA_SLUG_NPC, 2793, 3280, 0, 2},
        {SEA_SLUG_NPC, 2765, 3282, 1, 2},
        {SEA_SLUG_NPC, 2767, 3282, 1, 2},
        {SEA_SLUG_NPC, 2769, 3282, 1, 2},
        {SEA_SLUG_NPC, 2771, 3290, 1, 2},
        {SEA_SLUG_NPC, 2779, 3289, 1, 2},
        {SEA_SLUG_NPC, 2780, 3283, 1, 2},
        {SEA_SLUG_NPC, 2783, 3286, 1, 2},
        {SEA_SLUG_NPC, 2784, 3279, 1, 2},
        {SEA_SLUG_NPC, 2785, 3282, 1, 2}
    };

    public SeaSlugQuest(int n) {
        super(QUEST_ID);
        this.setQuestPointReward(1);
    }

    public static void spawnMissingContent() {
        int n = 0;
        while (n < NPC_SPAWNS.length) {
            int[] spawn = NPC_SPAWNS[n];
            SeaSlugQuest.spawnNpcIfMissingAt(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
            ++n;
        }
        if (ItemDefinition.isDefined(BROKEN_GLASS)) {
            GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(BROKEN_GLASS, 1), BROKEN_GLASS_POSITION, (int)GameUtil.secondsToTicks(50L), true));
        }
    }

    public static boolean isContentAvailable() {
        return NpcDefinition.isDefined(BAILEY)
            && NpcDefinition.isDefined(CAROLINE)
            && NpcDefinition.isDefined(KENNITH)
            && NpcDefinition.isDefined(HOLGART_ISLAND)
            && NpcDefinition.isDefined(HOLGART_PLATFORM)
            && NpcDefinition.isDefined(HOLGART_SHORE)
            && NpcDefinition.isDefined(KENT)
            && NpcDefinition.isDefined(FISHERMAN_1)
            && NpcDefinition.isDefined(FISHERMAN_2)
            && NpcDefinition.isDefined(FISHERMAN_3)
            && NpcDefinition.isDefined(FISHERMAN_4)
            && NpcDefinition.isDefined(SEA_SLUG_NPC)
            && ItemDefinition.isDefined(BIG_OYSTER_PEARLS)
            && ItemDefinition.isDefined(TORCH_LIT)
            && ItemDefinition.isDefined(TORCH_UNLIT)
            && ItemDefinition.isDefined(SEA_SLUG_ITEM)
            && ItemDefinition.isDefined(DAMP_STICKS)
            && ItemDefinition.isDefined(DRY_STICKS)
            && ItemDefinition.isDefined(BROKEN_GLASS)
            && ItemDefinition.isDefined(EMPTY_POT)
            && ItemDefinition.isDefined(POT_OF_FLOUR)
            && ItemDefinition.isDefined(SWAMP_TAR)
            && ItemDefinition.isDefined(RAW_SWAMP_PASTE)
            && ItemDefinition.isDefined(SWAMP_PASTE)
            && GameplayHelper.isObjectDefinitionIdValid(ROW_BOAT)
            && GameplayHelper.isObjectDefinitionIdValid(SUNK_ROW_BOAT)
            && GameplayHelper.isObjectDefinitionIdValid(SLUG_LADDER)
            && GameplayHelper.isObjectDefinitionIdValid(LOOSE_PANEL)
            && GameplayHelper.isObjectDefinitionIdValid(KENNITH_WALL)
            && GameplayHelper.isObjectDefinitionIdValid(FISHING_CRANE)
            && GameplayHelper.isObjectDefinitionIdValid(FISHING_CRANE_NOSCRIPT);
    }

    public static boolean handlePacketItemOnItem(Player player, int firstItemId, int secondItemId) {
        return QuestDefinition.getQuestScript(QUEST_ID).handleItemOnItem(player, firstItemId, secondItemId, player.getQuestState(QUEST_ID));
    }

    @Override
    public boolean refreshQuestJournalStatus(Player player, int n) {
        if (n != STATE_COMPLETE && !SeaSlugQuest.isContentAvailable()) {
            player.packetSender.sendInterfaceTextColor(QuestDefinition.forId(this.getQuestId()).getJournalButtonId(), new Color(102, 102, 102));
            return true;
        }
        return false;
    }

    @Override
    public String[] buildQuestJournal(Player player, int n) {
        if (n == STATE_COMPLETE) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "7,175 Fishing XP", "Oyster pearls"};
        }
        if (!SeaSlugQuest.isContentAvailable()) {
            return new String[]{"Sea Slug is not available in this cache.", "", "The loaded cache is missing at least one required", "Sea Slug item, NPC, or object definition."};
        }
        if (n == 0) {
            return new String[]{"I can start this quest by speaking to Caroline", "in Witchaven, east of Ardougne."};
        }
        if (n == STATE_STARTED) {
            return new String[]{"Caroline asked me to look for her husband Kent", "and son Kennith on the fishing platform.", "I should speak to Holgart by the shore."};
        }
        if (n == STATE_SPOKEN_HOLGART) {
            return new String[]{"Holgart's boat needs swamp paste before it can", "sail. I can make some with swamp tar and", "a pot of flour, then warm it over a fire."};
        }
        if (n == STATE_BOAT_REPAIRED) {
            return new String[]{"Holgart's boat is repaired. I should ask him", "to take me to the fishing platform."};
        }
        if (n == STATE_SPOKEN_KENNITH) {
            return new String[]{"I found Kennith on the fishing platform.", "He is frightened and wants his father."};
        }
        if (n == STATE_SAILED_KENT) {
            return new String[]{"Holgart took me to a small island where Kent", "is stranded. I should speak with him."};
        }
        if (n == STATE_SPOKEN_KENT) {
            return new String[]{"Kent warned me that sea slugs control the fishermen.", "I need a lit torch before climbing onto the", "upper platform."};
        }
        if (n == STATE_LIT_TORCH) {
            return new String[]{"My lit torch should keep the fishermen away.", "I should talk to Kennith through the wall."};
        }
        if (n == STATE_KENNITH_NEED_ESCAPE) {
            return new String[]{"Kennith will not come downstairs past the slugs.", "I should find another way for him to escape."};
        }
        if (n == STATE_PANEL_OPENED) {
            return new String[]{"I kicked open a loose panel near Kennith.", "I should tell him about the opening."};
        }
        if (n == STATE_NEED_KENNITH_PATH) {
            return new String[]{"Kennith needs a safe route down to Holgart's boat.", "I should use the fishing crane on the upper", "platform."};
        }
        if (n == STATE_SAVED_KENNITH) {
            return new String[]{"Kennith has escaped to the row boat.", "I should return to Caroline in Witchaven."};
        }
        return null;
    }

    @Override
    public void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        player.packetSender.sendInterfaceText("1 Quest Point", 12150);
        player.packetSender.sendInterfaceText("7,175 Fishing XP", 12151);
        player.packetSender.sendInterfaceText("Oyster pearls", 12152);
        player.packetSender.sendInterfaceText("", 12153);
        player.packetSender.sendInterfaceText("", 12154);
        player.packetSender.sendInterfaceText("", 12155);
        player.getInventoryManager().addOrDropItem(new ItemStack(BIG_OYSTER_PEARLS, 1));
        player.getSkillManager().addQuestExperience(FISHING, 7175.0);
        player.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, SEA_SLUG_ITEM);
        player.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public boolean handleFirstNpcAction(Player player, int n, int n2) {
        if (n == SEA_SLUG_NPC) {
            SeaSlugQuest.pickupSeaSlug(player);
            return true;
        }
        if (n == CAROLINE || n == HOLGART_SHORE || n == HOLGART_PLATFORM || n == HOLGART_ISLAND || n == KENT || n == KENNITH || n == BAILEY
            || n == FISHERMAN_1 || n == FISHERMAN_2 || n == FISHERMAN_3 || n == FISHERMAN_4) {
            DialogueManager.continueDialogue(player, n, 1, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcDialogue(Player player, int npcId, int step, int option, int state) {
        if (npcId == CAROLINE) {
            return this.handleCarolineDialogue(player, step, option);
        }
        if (npcId == HOLGART_SHORE) {
            return this.handleHolgartShoreDialogue(player, step, option);
        }
        if (npcId == HOLGART_PLATFORM) {
            return this.handleHolgartPlatformDialogue(player, step, option);
        }
        if (npcId == HOLGART_ISLAND) {
            return this.handleHolgartIslandDialogue(player, step);
        }
        if (npcId == KENT) {
            return this.handleKentDialogue(player, step);
        }
        if (npcId == KENNITH) {
            return this.handleKennithDialogue(player, step);
        }
        if (npcId == BAILEY) {
            return this.handleBaileyDialogue(player, step);
        }
        if (npcId == FISHERMAN_1 || npcId == FISHERMAN_2 || npcId == FISHERMAN_3 || npcId == FISHERMAN_4) {
            return this.handleFishermanDialogue(player, step);
        }
        return false;
    }

    @Override
    public boolean handleFirstObjectAction(Player player, int objectId, int x, int y, int state) {
        if (objectId == SLUG_LADDER && x == 2784 && y == 3286) {
            SeaSlugQuest.climbPlatformLadderUp(player);
            return true;
        }
        if (objectId == PLATFORM_UP_LADDER && x == 2784 && y == 3286) {
            player.packetSender.sendGameMessage("You climb down the ladder.");
            player.getUpdateState().setAnimation(828);
            player.moveTo(PLATFORM_LADDER_DOWN);
            return true;
        }
        if (SeaSlugQuest.isLoosePanel(objectId, x, y)) {
            SeaSlugQuest.kickLoosePanel(player);
            return true;
        }
        if (SeaSlugQuest.isKennithWall(objectId, x, y)) {
            return SeaSlugQuest.shoutAcrossKennithWall(player, x, y);
        }
        if (objectId == FISHING_CRANE || objectId == FISHING_CRANE_NOSCRIPT) {
            SeaSlugQuest.rotateCrane(player, x, y);
            return true;
        }
        if (objectId == ROW_BOAT || objectId == SUNK_ROW_BOAT) {
            return SeaSlugQuest.handleRowBoat(player, x, y);
        }
        return false;
    }

    @Override
    public boolean handleSecondObjectAction(Player player, int objectId, int x, int y, int state) {
        if (SeaSlugQuest.isLoosePanel(objectId, x, y)) {
            SeaSlugQuest.kickLoosePanel(player);
            return true;
        }
        if (SeaSlugQuest.isKennithWall(objectId, x, y)) {
            return SeaSlugQuest.shoutAcrossKennithWall(player, x, y);
        }
        return false;
    }

    @Override
    public boolean handleThirdObjectAction(Player player, int objectId, int x, int y, int state) {
        return this.handleFirstObjectAction(player, objectId, x, y, state);
    }

    @Override
    public boolean handleGroundItemInteraction(Player player, int itemId, int state) {
        if (itemId != SEA_SLUG_ITEM) {
            return false;
        }
        SeaSlugQuest.pickupSeaSlug(player);
        return true;
    }

    @Override
    public boolean handleItemOnItem(Player player, int firstItemId, int secondItemId, int state) {
        if (SeaSlugQuest.isPair(firstItemId, secondItemId, SWAMP_TAR, POT_OF_FLOUR)) {
            if (!player.getInventoryManager().containsItem(SWAMP_TAR) || !player.getInventoryManager().containsItem(POT_OF_FLOUR)) {
                return true;
            }
            player.packetSender.sendGameMessage("You mix the flour with the swamp tar.");
            player.packetSender.sendGameMessage("It mixes into a paste.");
            player.getInventoryManager().removeItem(new ItemStack(SWAMP_TAR, 1));
            player.getInventoryManager().removeItem(new ItemStack(POT_OF_FLOUR, 1));
            player.getInventoryManager().addOrDropItem(new ItemStack(EMPTY_POT, 1));
            player.getInventoryManager().addOrDropItem(new ItemStack(RAW_SWAMP_PASTE, 1));
            return true;
        }
        if (SeaSlugQuest.isDampSticksAndBrokenGlass(firstItemId, secondItemId)) {
            if (!player.getInventoryManager().containsItem(DAMP_STICKS) || !SeaSlugQuest.hasBrokenGlass(player)) {
                return true;
            }
            player.packetSender.sendGameMessage("You hold the glass to the sun above the damp sticks.");
            player.packetSender.sendGameMessage("The glass acts like a lens and drys the sticks out.");
            player.getInventoryManager().removeItem(new ItemStack(DAMP_STICKS, 1));
            player.getInventoryManager().addOrDropItem(new ItemStack(DRY_STICKS, 1));
            return true;
        }
        if (SeaSlugQuest.isPair(firstItemId, secondItemId, DRY_STICKS, TORCH_UNLIT)) {
            SeaSlugQuest.lightDrySticks(player);
            return true;
        }
        if (SeaSlugQuest.isPair(firstItemId, secondItemId, TINDERBOX, TORCH_UNLIT) && SeaSlugQuest.isSeaSlugTinderboxDampArea(player.getPosition())) {
            player.packetSender.sendGameMessage("Your tinderbox is damp from the sea crossing. It won't light here.");
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, int itemId, int objectId, int state) {
        if (itemId != RAW_SWAMP_PASTE) {
            return false;
        }
        ObjectDefinition definition = ObjectDefinition.forId(objectId);
        String name = definition == null || definition.name == null ? "" : definition.name.toLowerCase();
        if (name.contains("range") || name.contains("stove") || name.contains("oven")) {
            player.packetSender.sendGameMessage("You need to warm that over a fire.");
            return true;
        }
        if (!name.contains("fire")) {
            return false;
        }
        if (!player.getInventoryManager().containsItem(RAW_SWAMP_PASTE)) {
            return true;
        }
        player.packetSender.sendGameMessage("You warm the paste over the fire.");
        player.packetSender.sendGameMessage("It thickens into a sticky goo.");
        player.getInventoryManager().removeItem(new ItemStack(RAW_SWAMP_PASTE, 1));
        player.getInventoryManager().addOrDropItem(new ItemStack(SWAMP_PASTE, 1));
        player.getSkillManager().addExperience(COOKING, 20.0);
        return true;
    }

    @Override
    public boolean handleInventoryItemFirstOption(Player player, int interfaceId, int itemId, int state) {
        if (itemId != DRY_STICKS) {
            return false;
        }
        SeaSlugQuest.lightDrySticks(player);
        return true;
    }

    private boolean handleCarolineDialogue(Player player, int step, int option) {
        int state = player.getQuestState(QUEST_ID);
        if (!SeaSlugQuest.isContentAvailable()) {
            if (step == 1) {
                player.getDialogueManager().showNpcTwoLineDialogue("I am worried about my family, but something", "seems to be missing from this cache.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == 0) {
            switch (step) {
                case 1: {
                    player.getDialogueManager().showPlayerOneLineDialogue("Hello there.", 591);
                    return true;
                }
                case 2: {
                    player.getDialogueManager().showNpcOneLineDialogue("Is there any chance you could help me?", 591);
                    return true;
                }
                case 3: {
                    player.getDialogueManager().showPlayerOneLineDialogue("What's wrong?", 591);
                    return true;
                }
                case 4: {
                    player.getDialogueManager().showNpcTwoLineDialogue("It's my husband, he works on a fishing platform.", "Once a month he takes our son, Kennith, out with him.", 591);
                    return true;
                }
                case 5: {
                    player.getDialogueManager().showNpcTwoLineDialogue("They usually write to me regularly, but I've", "heard nothing all week. It's very strange.", 591);
                    return true;
                }
                case 6: {
                    player.getDialogueManager().showPlayerOneLineDialogue("Maybe the post was lost!", 591);
                    return true;
                }
                case 7: {
                    player.getDialogueManager().showNpcThreeLineDialogue("Maybe, but no-one's heard from the other", "fishermen on the platform. Their families are", "becoming quite concerned.", 591);
                    return true;
                }
                case 8: {
                    player.getDialogueManager().showNpcTwoLineDialogue("Is there any chance you could visit the platform", "and find out what's going on?", 591);
                    return true;
                }
                case 9: {
                    player.getDialogueManager().showTwoOptions("I suppose so, how do I get there?", "I'm sorry, I'm too busy.");
                    return true;
                }
                case 10: {
                    if (option == 1) {
                        player.getDialogueManager().showPlayerOneLineDialogue("I suppose so, how do I get there?", 591);
                        player.getDialogueManager().setNextDialogueStep(12);
                        return true;
                    }
                    player.getDialogueManager().showPlayerOneLineDialogue("I'm sorry, I'm too busy.", 591);
                    player.getDialogueManager().setNextDialogueStep(20);
                    return true;
                }
                case 12: {
                    player.getDialogueManager().showNpcTwoLineDialogue("That's very good of you traveller.", "My friend Holgart will take you there.", 591);
                    return true;
                }
                case 13: {
                    player.getDialogueManager().showPlayerOneLineDialogue("Ok, I'll go and see if they're ok.", 591);
                    return true;
                }
                case 14: {
                    this.startQuest(player);
                    player.getDialogueManager().showNpcThreeLineDialogue("I'll reward you for your time.", "It'll give me peace of mind to know Kennith", "and my husband, Kent, are safe.", 591);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                case 20: {
                    player.getDialogueManager().showNpcOneLineDialogue("That's a shame.", 591);
                    return true;
                }
                case 21: {
                    player.getDialogueManager().showPlayerOneLineDialogue("Bye.", 591);
                    return true;
                }
                case 22: {
                    player.getDialogueManager().showNpcOneLineDialogue("Bye.", 591);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
            }
            return false;
        }
        if (state == STATE_SAVED_KENNITH) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Brave traveller, you've returned!", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showNpcTwoLineDialogue("Kennith told me about the strange goings-on", "at the platform. I had no idea it was so serious.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcTwoLineDialogue("I could have lost my son and my husband", "if it wasn't for you.", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerOneLineDialogue("We found Kent stranded on an island.", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showNpcThreeLineDialogue("Yes, Holgart told me and sent a rescue party out.", "Kent's back home now, resting with Kennith.", "I don't think he'll be doing any fishing for a while.", 591);
                return true;
            }
            if (step == 7) {
                player.getDialogueManager().showNpcThreeLineDialogue("Here, take these Oyster pearls as a reward.", "They're worth quite a bit and can be used", "to make lethal crossbow bolts.", 591);
                return true;
            }
            if (step == 8) {
                player.getDialogueManager().finishDialogue();
                this.awardCompletionRewards(player);
                return true;
            }
            return false;
        }
        if (state == STATE_COMPLETE) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello again.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Hello traveller, how are you?", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("Not bad thanks, yourself?", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcTwoLineDialogue("I'm good. Busy as always looking after Kent", "and Kennith but no complaints.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showPlayerOneLineDialogue("Hello Caroline.", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showNpcTwoLineDialogue("Brave adventurer, have you any news", "about my son and his father?", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().showPlayerOneLineDialogue("I'm working on it now Caroline.", 591);
            return true;
        }
        if (step == 4) {
            player.getDialogueManager().showNpcOneLineDialogue("Please bring them back safe and sound.", 591);
            return true;
        }
        if (step == 5) {
            player.getDialogueManager().showPlayerOneLineDialogue("I'll do my best.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleHolgartShoreDialogue(Player player, int step, int option) {
        int state = player.getQuestState(QUEST_ID);
        if (state == 0) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello there.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Well hello m'laddy, beautiful day isn't it.", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("Not bad I suppose.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcOneLineDialogue("Just smell that sea air... beautiful.", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hmm... lovely...", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_STARTED) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Hello m'hearty.", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("I would like a ride on your boat to the fishing platform.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcTwoLineDialogue("I'm afraid it isn't sea worthy, it's full of holes.", "To fill the holes I'll need some swamp paste.", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerOneLineDialogue("Swamp paste?", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showNpcTwoLineDialogue("Yes, swamp tar mixed with flour", "and heated over a fire.", 591);
                return true;
            }
            if (step == 7) {
                player.getDialogueManager().showPlayerOneLineDialogue("Where can I find swamp tar?", 591);
                return true;
            }
            if (step == 8) {
                player.getDialogueManager().showNpcThreeLineDialogue("Unfortunately the only supply of swamp tar", "is in the swamps below Lumbridge. It's too far", "for an old man like me to travel.", 591);
                return true;
            }
            if (step == 9) {
                player.getDialogueManager().showNpcTwoLineDialogue("If you make me some swamp paste", "I'll give you a ride in my boat.", 591);
                return true;
            }
            if (step == 10) {
                player.getDialogueManager().showPlayerOneLineDialogue("I'll see what I can do.", 591);
                player.setQuestState(QUEST_ID, STATE_SPOKEN_HOLGART);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_SPOKEN_HOLGART || state == STATE_BOAT_REPAIRED && step >= 5 && step <= 9) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello Holgart.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Hello m'hearty. Did you manage to make some swamp paste?", 591);
                return true;
            }
            if (step == 3) {
                if (!player.getInventoryManager().containsItem(SWAMP_PASTE)) {
                    player.getDialogueManager().showPlayerOneLineDialogue("I'm afraid not.", 591);
                    player.getDialogueManager().setNextDialogueStep(20);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Yes, I have some here.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showItemMessage("You give Holgart the swamp paste.", new ItemStack(SWAMP_PASTE, 1));
                player.getInventoryManager().removeItem(new ItemStack(SWAMP_PASTE, 1));
                player.setQuestState(QUEST_ID, STATE_BOAT_REPAIRED);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showNpcOneLineDialogue("Superb, this looks great.", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showTwoLineStatement("Holgart smears the paste over the under", "side of his boat.");
                return true;
            }
            if (step == 7) {
                player.getDialogueManager().showNpcTwoLineDialogue("That's the job done, now we can go.", "Jump aboard!", 591);
                return true;
            }
            if (step == 8) {
                player.getDialogueManager().showTwoOptions("I'll come back later.", "Okay, lets do it.");
                return true;
            }
            if (step == 9) {
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Okay, lets do it.", 591);
                    player.getDialogueManager().finishDialogue();
                    SeaSlugQuest.travelToPlatform(player);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I'll come back later.", 591);
                player.getDialogueManager().setNextDialogueStep(30);
                return true;
            }
            if (step == 20) {
                player.getDialogueManager().showNpcThreeLineDialogue("It's simply swamp tar mixed with flour", "heated over a fire. Unfortunately the only supply", "of swamp tar is in the swamps below Lumbridge.", 591);
                return true;
            }
            if (step == 21) {
                player.getDialogueManager().showNpcOneLineDialogue("I can't fix my row boat without it.", 591);
                return true;
            }
            if (step == 22) {
                player.getDialogueManager().showPlayerOneLineDialogue("Ok, I'll try to find some.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (step == 30) {
                player.getDialogueManager().showNpcTwoLineDialogue("Okay then.", "I'll wait here for you.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_SAILED_KENT) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Holgart, can you take me back out to Kent?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Righty ho traveller. Let's go.", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().finishDialogue();
                SeaSlugQuest.travelToIsland(player);
                return true;
            }
            return false;
        }
        if (state >= STATE_BOAT_REPAIRED && state != STATE_SAVED_KENNITH) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello Holgart.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcThreeLineDialogue("Hello again land lover.", "There's some strange goings on,", "on that platform, I tell you.", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showTwoOptions("Will you take me there?", "I'm keeping away from there.");
                return true;
            }
            if (step == 4) {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Will you take me there?", 591);
                    player.getDialogueManager().finishDialogue();
                    SeaSlugQuest.travelToPlatform(player);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I'm keeping away from there.", 591);
                player.getDialogueManager().setNextDialogueStep(20);
                return true;
            }
            if (step == 20) {
                player.getDialogueManager().showNpcOneLineDialogue("Fair enough m'hearty.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_SAVED_KENNITH || state == STATE_COMPLETE) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello again Holgart.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcTwoLineDialogue("Well hello again m'hearty.", "Your land loving legs getting bored?", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("Pardon?", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcOneLineDialogue("Fancy going out to sea?", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showTwoOptions("I'll come back later.", "Okay, let's do it.");
                return true;
            }
            if (step == 6) {
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Okay, let's do it.", 591);
                    player.getDialogueManager().finishDialogue();
                    SeaSlugQuest.travelToPlatform(player);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I'll come back later.", 591);
                player.getDialogueManager().setNextDialogueStep(20);
                return true;
            }
            if (step == 20) {
                player.getDialogueManager().showNpcOneLineDialogue("Okay then. I'll wait here for you.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleHolgartPlatformDialogue(Player player, int step, int option) {
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_SPOKEN_KENNITH) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Holgart, something strange is going on here.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("You're telling me, none of the sailors seem to remember who I am.", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerTwoLineDialogue("Apparently Kennith's father left for help", "a couple of days ago.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcTwoLineDialogue("That's a worry, no-one's heard from him on shore.", "Come on, we'd better go look for him.", 591);
                player.getDialogueManager().finishDialogue();
                player.setQuestState(QUEST_ID, STATE_SAILED_KENT);
                SeaSlugQuest.travelToIsland(player);
                return true;
            }
            return false;
        }
        if (state == STATE_SAVED_KENNITH) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Did you get the kid back to shore?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcTwoLineDialogue("Yes, he's safe and sound with his parents.", "Your turn to return to land now adventurer.", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("Looking forward to it.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().finishDialogue();
                SeaSlugQuest.travelToShore(player);
                return true;
            }
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showPlayerOneLineDialogue("Hey, Holgart.", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showNpcTwoLineDialogue("Have you had enough of this place yet?", "It's really starting to scare me.", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().showTwoOptions("No, I'm going to stay a while.", "Okay, let's go back.");
            return true;
        }
        if (step == 4) {
            if (option == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("Okay, let's go back.", 591);
                player.getDialogueManager().finishDialogue();
                SeaSlugQuest.travelToShore(player);
                return true;
            }
            player.getDialogueManager().showPlayerOneLineDialogue("No, I'm going to stay a while.", 591);
            player.getDialogueManager().setNextDialogueStep(20);
            return true;
        }
        if (step == 20) {
            player.getDialogueManager().showNpcOneLineDialogue("Okay... you're the boss.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleHolgartIslandDialogue(Player player, int step) {
        int state = player.getQuestState(QUEST_ID);
        if (state != STATE_SAILED_KENT) {
            if (step == 1) {
                player.getDialogueManager().showPlayerTwoLineDialogue("We'd better get back to the platform", "so we can see what's going on.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("You're right. It all sounds pretty creepy.", 591);
                SeaSlugQuest.travelToPlatform(player);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showPlayerOneLineDialogue("Where are we?", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showNpcTwoLineDialogue("Someway off mainland still.", "You'd better see if me old matey's okay.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleKentDialogue(Player player, int step) {
        int state = player.getQuestState(QUEST_ID);
        if (state != STATE_SAILED_KENT) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Oh my, I must get back to shore.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showNpcTwoLineDialogue("Oh thank Saradomin!", "I thought I'd be left out here forever.", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showPlayerTwoLineDialogue("Your wife sent me out to find you and your boy.", "Kennith's fine by the way, he's on the platform.", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().showNpcTwoLineDialogue("I knew the row boat wasn't sea worthy.", "You must get him off that platform.", 591);
            return true;
        }
        if (step == 4) {
            player.getDialogueManager().showPlayerOneLineDialogue("What's going on here?", 591);
            return true;
        }
        if (step == 5) {
            player.getDialogueManager().showNpcThreeLineDialogue("Five days ago we pulled in a huge catch.", "As well as fish we caught small slug like", "creatures, hundreds of them.", 591);
            return true;
        }
        if (step == 6) {
            player.getDialogueManager().showNpcOneLineDialogue("That's when the fishermen began to act strange.", 591);
            return true;
        }
        if (step == 7) {
            player.getDialogueManager().showNpcThreeLineDialogue("It was the sea slugs, they attach themselves", "to your body and somehow take over", "the mind of the carrier.", 591);
            return true;
        }
        if (step == 8) {
            player.getDialogueManager().showNpcOneLineDialogue("I told Kennith to hide until I returned but I was washed up here.", 591);
            return true;
        }
        if (step == 9) {
            player.getDialogueManager().showNpcTwoLineDialogue("Please go back and get my boy,", "you can send help for me later.", 591);
            return true;
        }
        if (step == 10) {
            player.getDialogueManager().showNpcOneLineDialogue("Traveller wait!", 591);
            return true;
        }
        if (step == 11) {
            player.getDialogueManager().showOneLineStatement("Kent reaches behind your neck.");
            return true;
        }
        if (step == 12) {
            player.getDialogueManager().showOneLineStatement("*slooop*");
            return true;
        }
        if (step == 13) {
            player.getDialogueManager().showOneLineStatement("He pulls a sea slug from under your top.");
            SeaSlugQuest.spawnSeaSlugGroundItem(player);
            return true;
        }
        if (step == 14) {
            player.getDialogueManager().showNpcTwoLineDialogue("A few more minutes and that thing would have", "full control of your body.", 591);
            return true;
        }
        if (step == 15) {
            player.getDialogueManager().showPlayerOneLineDialogue("Yuck! Thanks Kent.", 591);
            player.setQuestState(QUEST_ID, STATE_SPOKEN_KENT);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleKennithDialogue(Player player, int step) {
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_BOAT_REPAIRED) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Are you okay young one?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("No, I want my daddy!", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("Where is your father?", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcOneLineDialogue("He went to get help days ago.", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showNpcThreeLineDialogue("The nasty fishermen tried to throw", "me and daddy into the sea.", "So he told me to hide here.", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showPlayerThreeLineDialogue("That's good advice,", "you stay here and I'll go", "try and find your father.", 591);
                return true;
            }
            if (step == 7) {
                player.setQuestState(QUEST_ID, STATE_SPOKEN_KENNITH);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_SPOKEN_KENNITH) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Are you okay?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("I want to see daddy!", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("I'm working on it.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_LIT_TORCH) {
            if (step == 1) {
                player.getDialogueManager().showPlayerTwoLineDialogue("Hello Kennith,", "are you okay?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("No, I want my daddy.", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerThreeLineDialogue("You'll be able to see him soon.", "First we need to get you back to land,", "come with me to the boat.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcOneLineDialogue("No!", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerOneLineDialogue("What, why not?", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showNpcTwoLineDialogue("I'm scared of those nasty sea slugs.", "I won't go near them.", 591);
                return true;
            }
            if (step == 7) {
                player.getDialogueManager().showPlayerTwoLineDialogue("Okay, you wait here and I'll go figure", "another way to get you out.", 591);
                player.setQuestState(QUEST_ID, STATE_KENNITH_NEED_ESCAPE);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_PANEL_OPENED) {
            if (step == 1) {
                player.getDialogueManager().showPlayerTwoLineDialogue("Kennith, I've made an opening in the wall.", "You can come out through there.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcOneLineDialogue("Are there any sea slugs on the other side?", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("Not one.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcOneLineDialogue("How will I get downstairs?", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerOneLineDialogue("I'll figure that out in a moment.", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showNpcOneLineDialogue("Ok, when you have I'll come out.", 591);
                player.setQuestState(QUEST_ID, STATE_NEED_KENNITH_PATH);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_NEED_KENNITH_PATH) {
            if (step == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("Please make the way down safe for me.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_SAVED_KENNITH || state == STATE_COMPLETE) {
            if (step == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("Thank you for saving me!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showOneLineStatement("He doesn't seem interested in talking.");
            return true;
        }
        return false;
    }

    private boolean handleBaileyDialogue(Player player, int step) {
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_BOAT_REPAIRED || state == STATE_SPOKEN_KENNITH || state == STATE_SAILED_KENT) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcTwoLineDialogue("Well hello there.", "What are you doing here?", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerTwoLineDialogue("I'm trying to find out what happened", "to a boy named Kennith.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcThreeLineDialogue("Oh you mean Kent's son.", "He's around somewhere, probably hiding", "if he knows what's good for him.", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hiding from what?", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showNpcOneLineDialogue("Haven't you seen all those things out there?", 591);
                return true;
            }
            if (step == 7) {
                player.getDialogueManager().showPlayerOneLineDialogue("The sea slugs?", 591);
                return true;
            }
            if (step == 8) {
                player.getDialogueManager().showNpcTwoLineDialogue("Ever since we pulled up that haul,", "something strange has been going on.", 591);
                return true;
            }
            if (step == 9) {
                player.getDialogueManager().showNpcTwoLineDialogue("The fishermen spend all day pulling in hauls of fish,", "only to throw back the fish and keep those nasty sea slugs.", 591);
                return true;
            }
            if (step == 10) {
                player.getDialogueManager().showNpcFourLineDialogue("What am I supposed to do with those?", "I haven't figured out how to kill one yet,", "if I put them near the stove they squirm", "and jump away.", 591);
                return true;
            }
            if (step == 11) {
                player.getDialogueManager().showPlayerOneLineDialogue("I doubt they would taste too good.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_SPOKEN_KENT) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showNpcFourLineDialogue("Oh, thank the gods it's you.", "They've all gone mad I tell you,", "one of the fishermen tried to", "throw me into the sea!", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("They're all being controlled by the sea slugs.", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcOneLineDialogue("I figured as much.", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerTwoLineDialogue("I need to get Kennith off this platform,", "but I can't get past the fishermen.", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showNpcTwoLineDialogue("The sea slugs are scared of heat,", "I figured that out when I tried to cook them.", 591);
                if (SeaSlugQuest.hasItem(player, TORCH_UNLIT)) {
                    player.getDialogueManager().setNextDialogueStep(30);
                }
                return true;
            }
            if (step == 7) {
                player.getDialogueManager().showNpcOneLineDialogue("Here.", 591);
                return true;
            }
            if (step == 8) {
                SeaSlugQuest.giveTorch(player);
                return true;
            }
            if (step == 9) {
                player.getDialogueManager().showNpcTwoLineDialogue("I doubt the fishermen will come near you", "if you can get this torch lit.", 591);
                return true;
            }
            if (step == 10) {
                player.getDialogueManager().showNpcTwoLineDialogue("The only problem is all the wood and flint are damp...", "I can't light a thing!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (step == 30) {
                player.getDialogueManager().showPlayerOneLineDialogue("I'd better find a way to light this torch, and soon.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_LIT_TORCH || state == STATE_KENNITH_NEED_ESCAPE || state == STATE_PANEL_OPENED || state == STATE_NEED_KENNITH_PATH) {
            if (step == 1) {
                if (SeaSlugQuest.hasItem(player, TORCH_LIT)) {
                    player.getDialogueManager().showPlayerOneLineDialogue("I've managed to light the torch.", 591);
                    return true;
                }
                if (SeaSlugQuest.hasItem(player, TORCH_UNLIT)) {
                    player.getDialogueManager().showPlayerOneLineDialogue("I'd better find a way to light this torch, and soon.", 591);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I've managed to lose my torch.", 591);
                return true;
            }
            if (step == 2) {
                if (SeaSlugQuest.hasItem(player, TORCH_LIT)) {
                    player.getDialogueManager().showNpcOneLineDialogue("Well done traveller, you'd better get Kennith out of here soon.", 591);
                    return true;
                }
                player.getDialogueManager().showNpcTwoLineDialogue("That was silly, fortunately I have another.", "Here, take it.", 591);
                return true;
            }
            if (step == 3) {
                if (!SeaSlugQuest.hasItem(player, TORCH_LIT)) {
                    SeaSlugQuest.giveTorch(player);
                    return true;
                }
                player.getDialogueManager().showNpcTwoLineDialogue("The fishermen are becoming stranger by the minute,", "and they keep pulling up those blasted sea slugs.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_SAVED_KENNITH || state == STATE_COMPLETE) {
            if (step == 1) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello Bailey.", 591);
                return true;
            }
            if (step == 2) {
                if (state == STATE_COMPLETE) {
                    player.getDialogueManager().showNpcTwoLineDialogue("Well hello again traveller.", "What brings you back out here?", 591);
                    return true;
                }
                player.getDialogueManager().showNpcTwoLineDialogue("Hello again. I saw you managed to get Kennith off", "the platform. He wasn't safe around those slugs.", 591);
                return true;
            }
            if (step == 3) {
                if (state == STATE_COMPLETE) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Just looking around.", 591);
                    player.getDialogueManager().setNextDialogueStep(20);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Are you going to come back with us?", 591);
                return true;
            }
            if (step == 4) {
                player.getDialogueManager().showNpcThreeLineDialogue("No, these fishermen are my friends,", "I'm sure they can be saved. I'm going to stay", "and try to get rid of all these slugs.", 591);
                return true;
            }
            if (step == 5) {
                player.getDialogueManager().showPlayerTwoLineDialogue("You're braver than most.", "Take care of yourself Bailey.", 591);
                return true;
            }
            if (step == 6) {
                player.getDialogueManager().showNpcOneLineDialogue("You too traveller.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (step == 20) {
                player.getDialogueManager().showNpcOneLineDialogue("Well don't go touching any of those blasted slugs.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showNpcOneLineDialogue("Hello there.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleFishermanDialogue(Player player, int step) {
        if (step == 1) {
            player.getDialogueManager().showPlayerOneLineDialogue("Hello there.", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showTwoLineStatement("His eyes are fixated", "staring at the sea..");
            return true;
        }
        if (step == 3) {
            if (GameUtil.randomInt(2) == 0) {
                player.getDialogueManager().showNpcTwoLineDialogue("Keep away human...", "Leave or face the deep blue...", 591);
                player.getDialogueManager().setNextDialogueStep(20);
            } else {
                player.getDialogueManager().showNpcOneLineDialogue("Must find family...", 591);
                player.getDialogueManager().setNextDialogueStep(30);
            }
            return true;
        }
        if (step == 20) {
            player.getDialogueManager().showPlayerOneLineDialogue("Pardon?", 591);
            return true;
        }
        if (step == 21) {
            player.getDialogueManager().showNpcTwoLineDialogue("You will all end up in the blue...", "Deep deep under the blue...", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step == 30) {
            player.getDialogueManager().showPlayerOneLineDialogue("What?", 591);
            return true;
        }
        if (step == 31) {
            player.getDialogueManager().showNpcOneLineDialogue("Soon we will all be together...", 591);
            return true;
        }
        if (step == 32) {
            player.getDialogueManager().showPlayerOneLineDialogue("Are you ok?", 591);
            return true;
        }
        if (step == 33) {
            player.getDialogueManager().showNpcThreeLineDialogue("Must find family...", "They are all under the blue...", "Deep deep under the blue...", 591);
            return true;
        }
        if (step == 34) {
            player.getDialogueManager().showPlayerOneLineDialogue("Ermm... I'll leave you to it then.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static void climbPlatformLadderUp(Player player) {
        int state = player.getQuestState(QUEST_ID);
        if (state >= STATE_SPOKEN_KENT && state <= STATE_SAVED_KENNITH) {
            if (!SeaSlugQuest.hasItem(player, TORCH_LIT)) {
                player.packetSender.sendGameMessage("You attempt to climb up the ladder.");
                player.packetSender.sendGameMessage("The fishermen approach you...");
                player.packetSender.sendGameMessage("and smack you on the head with a fishing rod!");
                player.applyDirectHit(4, HitType.NORMAL);
                player.getDialogueManager().showPlayerOneLineDialogue("Ouch!", 591);
                player.getDialogueManager().finishDialogue();
                return;
            }
            player.packetSender.sendGameMessage("The fishermen seem afraid of your torch.");
        }
        player.packetSender.sendGameMessage("You climb up the ladder.");
        player.getUpdateState().setAnimation(828);
        player.moveTo(PLATFORM_LADDER_UP);
    }

    private static void kickLoosePanel(Player player) {
        player.getUpdateState().setAnimation(422);
        player.packetSender.sendGameMessage("You kick the loose panel.");
        if (player.getQuestState(QUEST_ID) == STATE_KENNITH_NEED_ESCAPE) {
            player.packetSender.sendGameMessage("The wood is rotten and crumbles away...");
            player.packetSender.sendGameMessage("leaving an opening big enough for Kennith to climb through.");
            player.setQuestState(QUEST_ID, STATE_PANEL_OPENED);
            return;
        }
        player.packetSender.sendGameMessage("But nothing interesting happens.");
    }

    private static boolean shoutAcrossKennithWall(Player player, int x, int y) {
        if (!GameUtil.isWithinDistance(player.getPosition().getX(), player.getPosition().getY(), x, y, 8)) {
            return false;
        }
        DialogueManager.continueDialogue(player, KENNITH, 1, 0);
        return true;
    }

    private static void rotateCrane(Player player, int x, int y) {
        if (player.getPosition().getY() < y + 3) {
            player.packetSender.sendGameMessage("I need to get closer to use that.");
            return;
        }
        player.packetSender.sendGameMessage("You rotate the crane around.");
        if (player.getQuestState(QUEST_ID) != STATE_NEED_KENNITH_PATH) {
            return;
        }
        player.getDialogueManager().showPlayerOneLineDialogue("Jump on Kennith!", 591);
        player.packetSender.sendGameMessage("Kennith comes out through the broken panel.");
        player.packetSender.sendGameMessage("He climbs onto the fishing net.");
        player.packetSender.sendGameMessage("You rotate the crane back around...");
        player.packetSender.sendGameMessage("and lower Kennith to the row boat waiting below.");
        player.setQuestState(QUEST_ID, STATE_SAVED_KENNITH);
    }

    private static boolean handleRowBoat(Player player, int x, int y) {
        int state = player.getQuestState(QUEST_ID);
        Position position = new Position(x, y, player.getPosition().getPlane());
        if (position.isWithinDistance(SHORE_POSITION, 12)) {
            if (state == STATE_SAILED_KENT) {
                SeaSlugQuest.travelToIsland(player);
            } else if (state >= STATE_BOAT_REPAIRED || state == STATE_COMPLETE) {
                SeaSlugQuest.travelToPlatform(player);
            } else {
                player.packetSender.sendGameMessage("The boat needs repairing before it can be sailed.");
            }
            return true;
        }
        if (position.isWithinDistance(PLATFORM_POSITION, 18)) {
            if (state == STATE_SPOKEN_KENNITH) {
                player.setQuestState(QUEST_ID, STATE_SAILED_KENT);
                SeaSlugQuest.travelToIsland(player);
            } else {
                SeaSlugQuest.travelToShore(player);
            }
            return true;
        }
        if (position.isWithinDistance(ISLAND_POSITION, 12)) {
            SeaSlugQuest.travelToPlatform(player);
            return true;
        }
        return false;
    }

    private static void travelToPlatform(Player player) {
        player.packetSender.sendGameMessage("You board the small row boat.");
        SeaSlugQuest.extinguishLitTorches(player);
        player.moveTo(PLATFORM_POSITION);
        player.packetSender.sendGameMessage("You arrive at the fishing platform.");
    }

    private static void travelToIsland(Player player) {
        player.packetSender.sendGameMessage("You board the row boat.");
        player.moveTo(ISLAND_POSITION);
        player.packetSender.sendGameMessage("You arrive on a small island.");
    }

    private static void travelToShore(Player player) {
        player.packetSender.sendGameMessage("You board the small row boat.");
        player.moveTo(SHORE_POSITION);
        player.packetSender.sendGameMessage("You arrive back on shore.");
    }

    private static void extinguishLitTorches(Player player) {
        int count = 0;
        while (player.getInventoryManager().containsItem(TORCH_LIT) && player.getInventoryManager().removeItem(new ItemStack(TORCH_LIT, 1))) {
            player.getInventoryManager().addOrDropItem(new ItemStack(TORCH_UNLIT, 1));
            ++count;
        }
        if (count > 0) {
            if (count > 1) {
                player.packetSender.sendGameMessage("Your torches go out on the crossing.");
            } else {
                player.packetSender.sendGameMessage("Your torch goes out on the crossing.");
            }
        }
    }

    private static void lightDrySticks(Player player) {
        if (player.getSkillManager().getCurrentLevels()[FIREMAKING] < 30) {
            player.packetSender.sendGameMessage("You rub together the dry sticks but nothing happens.");
            player.packetSender.sendGameMessage("You need a Firemaking level of 30 or above.");
            return;
        }
        if (!GameUtil.rollLevelScaledChance(210, 250, player.getSkillManager().getCurrentLevels()[FIREMAKING])) {
            player.packetSender.sendGameMessage("You rub together the dry sticks and the sticks smoke momentarily then die out.");
            return;
        }
        player.packetSender.sendGameMessage("You rub together the dry sticks and the sticks catch alight.");
        if (!player.getInventoryManager().containsItem(TORCH_UNLIT)) {
            player.packetSender.sendGameMessage("The sticks smoke momentarily then die out");
            return;
        }
        player.packetSender.sendGameMessage("You place the smoulding twigs to your torch.");
        player.packetSender.sendGameMessage("Your torch lights.");
        player.getInventoryManager().removeItem(new ItemStack(TORCH_UNLIT, 1));
        player.getInventoryManager().addOrDropItem(new ItemStack(TORCH_LIT, 1));
        if (player.getQuestState(QUEST_ID) == STATE_SPOKEN_KENT) {
            player.setQuestState(QUEST_ID, STATE_LIT_TORCH);
        }
    }

    private static void giveTorch(Player player) {
        player.getDialogueManager().showItemMessage("Bailey gives you a torch.", new ItemStack(TORCH_UNLIT, 1));
        player.getInventoryManager().addOrDropItem(new ItemStack(TORCH_UNLIT, 1));
    }

    private static void pickupSeaSlug(Player player) {
        player.packetSender.sendGameMessage("You pick up the sea slug.");
        player.packetSender.sendGameMessage("It sinks its teeth deep into your hand.");
        player.packetSender.sendGameMessage("You drop the sea slug.");
        player.applyDirectHit(3, HitType.NORMAL);
        player.getDialogueManager().showPlayerOneLineDialogue("Ouch!", 591);
        player.getDialogueManager().finishDialogue();
    }

    private static void spawnSeaSlugGroundItem(Player player) {
        GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(SEA_SLUG_ITEM, 1), player.getPosition(), false, player));
    }

    private static boolean isPair(int firstItemId, int secondItemId, int firstExpected, int secondExpected) {
        return firstItemId == firstExpected && secondItemId == secondExpected || firstItemId == secondExpected && secondItemId == firstExpected;
    }

    private static boolean isBrokenGlass(int itemId) {
        return itemId == BROKEN_GLASS || itemId == GENERIC_BROKEN_GLASS;
    }

    private static boolean isDampSticksAndBrokenGlass(int firstItemId, int secondItemId) {
        return firstItemId == DAMP_STICKS && SeaSlugQuest.isBrokenGlass(secondItemId)
            || secondItemId == DAMP_STICKS && SeaSlugQuest.isBrokenGlass(firstItemId);
    }

    private static boolean hasBrokenGlass(Player player) {
        return player.getInventoryManager().containsItem(BROKEN_GLASS)
            || player.getInventoryManager().containsItem(GENERIC_BROKEN_GLASS);
    }

    private static boolean hasItem(Player player, int itemId) {
        return player.getInventoryManager().containsItem(itemId);
    }

    private static boolean isKennithWall(int objectId, int x, int y) {
        return objectId == KENNITH_WALL && (x == 2763 || x == 2764) && y == 3287;
    }

    private static boolean isLoosePanel(int objectId, int x, int y) {
        return objectId == LOOSE_PANEL && x == 2765 && y == 3288;
    }

    private static boolean isSeaSlugTinderboxDampArea(Position position) {
        return position.getPlane() >= 0
            && position.getPlane() <= 1
            && position.getX() >= 2760
            && position.getX() <= 2804
            && position.getY() >= 3271
            && position.getY() <= 3327;
    }

    private static void spawnNpcIfMissingAt(int id, int x, int y, int plane, int walk) {
        if (!NpcDefinition.isDefined(id)) {
            return;
        }
        Position position = new Position(x, y, plane);
        if (Npc.findByDefinitionIdAtPosition(id, position) != null) {
            return;
        }
        GameplayHelper.spawnNpc(id, x, y, plane, walk);
    }

}
