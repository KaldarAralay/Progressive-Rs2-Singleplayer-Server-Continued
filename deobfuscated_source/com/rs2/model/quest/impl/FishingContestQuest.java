/*
 * Fishing Contest quest content, matched against the local 2004 base scripts.
 */
package com.rs2.model.quest.impl;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.npc.NpcMovementMode;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestScript;
import java.awt.Color;

public final class FishingContestQuest
extends QuestScript {
    public static final int QUEST_ID = 38;
    private static final int STATE_COMPLETE = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_PIPE_STASHED = 3;
    private static final int STATE_IN_COMP = 4;
    private static final int STATE_GARLIC_COMP = 5;
    private static final int STATE_WILLOW_FISH_ONE = 6;
    private static final int STATE_WILLOW_FISH_TWO = 7;
    private static final int STATE_GARLIC_FISH_ONE = 8;
    private static final int STATE_GARLIC_FISH_TWO = 9;
    private static final int STATE_WON_COMP = 10;
    private static final int STATE_WILLOW_FISH_THREE = 11;
    private static final int STATE_GARLIC_FISH_THREE = 12;

    private static final int FISHING = 10;
    private static final int COINS = 995;
    private static final int RED_VINE_WORM = 25;
    private static final int FISHING_TROPHY = 26;
    private static final int FISHING_PASS = 27;
    private static final int RAW_SHRIMPS = 317;
    private static final int RAW_SARDINE = 327;
    private static final int RAW_GIANT_CARP = 338;
    private static final int FISHING_ROD = 307;
    private static final int FISHING_ROD_ALT = 308;
    private static final int FISHING_BAIT = 313;
    private static final int SPADE = 952;
    private static final int SPADE_ALT = 953;
    private static final int GARLIC = 1550;
    private static final int GARLIC_ALT = 1551;

    private static final int BONZO = 225;
    private static final int MORRIS = 227;
    private static final int BIG_DAVE = 228;
    private static final int JOSHUA = 229;
    private static final int GRANDPA_JACK = 230;
    private static final int AUSTRI = 232;
    private static final int VESTRI = 3679;
    private static final int COMP_FISH_SPOT = 233;
    private static final int SINISTER_FISH_SPOT = 234;
    private static final int BIG_DAVE_FISH_SPOT = 235;
    private static final int JOSHUA_FISH_SPOT = 236;
    private static final int SINISTER_STRANGER = 3677;
    private static final int SINISTER_STRANGER_ALT = 3678;
    private static final int TUNNEL_DWARF_STAIRS_DIALOGUE = 100;

    private static final int WALL_PIPE = 41;
    private static final int HEMENSTER_GATE_LEFT = 47;
    private static final int HEMENSTER_GATE_RIGHT = 48;
    private static final int LOOSE_RAILING = 51;
    private static final int MCGRUBOR_GATE_LEFT = 52;
    private static final int MCGRUBOR_GATE_RIGHT = 53;
    private static final int TUNNEL_CAVE_WEST_STAIRS = 54;
    private static final int TUNNEL_TOP_WEST_STAIRS = 55;
    private static final int TUNNEL_CAVE_EAST_STAIRS = 56;
    private static final int TUNNEL_TOP_EAST_STAIRS = 57;
    private static final int[] RED_VINE_OBJECTS = new int[]{58, 2013, 2989, 2990, 2991, 2992, 2993, 2994};

    private static final Position TUNNEL_TOP_WEST = new Position(2820, 3486, 0);
    private static final Position TUNNEL_TOP_EAST = new Position(2877, 3482, 0);
    private static final Position TUNNEL_CAVE_WEST = new Position(2820, 9882, 0);
    private static final Position TUNNEL_CAVE_EAST = new Position(2876, 9878, 0);
    private static final Position SINISTER_PIPE_POSITION = new Position(2637, 3440, 0);
    private static final Position SINISTER_MOVED_POSITION = new Position(2631, 3435, 0);

    private static final int[][] NPC_SPAWNS = new int[][]{
        {VESTRI, 2820, 3487, 0, 0},
        {AUSTRI, 2877, 3483, 0, 0},
        {MORRIS, 2643, 3440, 0, 0},
        {BONZO, 2641, 3439, 0, 0},
        {GRANDPA_JACK, 2650, 3452, 0, 2},
        {SINISTER_STRANGER, 2637, 3440, 0, 0},
        {BIG_DAVE, 2634, 3425, 0, 1},
        {JOSHUA, 2629, 3415, 0, 1},
        {COMP_FISH_SPOT, 2630, 3435, 0, 0},
        {SINISTER_FISH_SPOT, 2637, 3444, 0, 0},
        {BIG_DAVE_FISH_SPOT, 2632, 3425, 0, 0},
        {JOSHUA_FISH_SPOT, 2627, 3415, 0, 0}
    };

    public FishingContestQuest(int n) {
        super(QUEST_ID);
        this.setQuestPointReward(1);
    }

    public static void spawnMissingContent() {
        int n = 0;
        while (n < NPC_SPAWNS.length) {
            int[] spawn = NPC_SPAWNS[n];
            FishingContestQuest.spawnNpcIfMissingAt(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
            ++n;
        }
    }

    public static boolean isContentAvailable() {
        return NpcDefinition.isDefined(BONZO)
            && NpcDefinition.isDefined(MORRIS)
            && NpcDefinition.isDefined(BIG_DAVE)
            && NpcDefinition.isDefined(JOSHUA)
            && NpcDefinition.isDefined(GRANDPA_JACK)
            && NpcDefinition.isDefined(AUSTRI)
            && NpcDefinition.isDefined(VESTRI)
            && NpcDefinition.isDefined(SINISTER_STRANGER)
            && NpcDefinition.isDefined(COMP_FISH_SPOT)
            && NpcDefinition.isDefined(SINISTER_FISH_SPOT)
            && NpcDefinition.isDefined(BIG_DAVE_FISH_SPOT)
            && NpcDefinition.isDefined(JOSHUA_FISH_SPOT)
            && ItemDefinition.isDefined(RED_VINE_WORM)
            && ItemDefinition.isDefined(FISHING_TROPHY)
            && ItemDefinition.isDefined(FISHING_PASS)
            && ItemDefinition.isDefined(RAW_GIANT_CARP)
            && ItemDefinition.isDefined(FISHING_ROD)
            && ItemDefinition.isDefined(FISHING_BAIT)
            && ItemDefinition.isDefined(SPADE)
            && ItemDefinition.isDefined(GARLIC)
            && FishingContestQuest.isObjectDefinitionAvailable(WALL_PIPE)
            && FishingContestQuest.isObjectDefinitionAvailable(HEMENSTER_GATE_LEFT)
            && FishingContestQuest.isObjectDefinitionAvailable(HEMENSTER_GATE_RIGHT)
            && FishingContestQuest.isObjectDefinitionAvailable(LOOSE_RAILING)
            && FishingContestQuest.isObjectDefinitionAvailable(TUNNEL_TOP_WEST_STAIRS)
            && FishingContestQuest.isObjectDefinitionAvailable(TUNNEL_TOP_EAST_STAIRS);
    }

    public static boolean isContestFishingSpot(int npcId) {
        return npcId == COMP_FISH_SPOT || npcId == SINISTER_FISH_SPOT
            || npcId == BIG_DAVE_FISH_SPOT || npcId == JOSHUA_FISH_SPOT;
    }

    public static boolean handleContestFishingSpot(Player player, Npc npc) {
        if (npc == null || !FishingContestQuest.isContestFishingSpot(npc.getNpcId())) {
            return false;
        }
        QuestScript script = QuestDefinition.getQuestScript(QUEST_ID);
        if (script instanceof FishingContestQuest) {
            return ((FishingContestQuest)script).attemptFishHemenster(player, npc);
        }
        return false;
    }

    @Override
    public boolean refreshQuestJournalStatus(Player player, int n) {
        if (n != STATE_COMPLETE && !FishingContestQuest.isContentAvailable()) {
            player.packetSender.sendInterfaceTextColor(QuestDefinition.forId(this.getQuestId()).getJournalButtonId(), new Color(102, 102, 102));
            return true;
        }
        return false;
    }

    @Override
    public String[] buildQuestJournal(Player player, int n) {
        if (n == STATE_COMPLETE) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "2,437 Fishing XP", "Use of the dwarven tunnel"};
        }
        if (!FishingContestQuest.isContentAvailable()) {
            return new String[]{"Fishing Contest is not available in this cache.", "", "The loaded cache is missing at least one required", "Fishing Contest item, NPC, or object definition."};
        }
        if (n == 0) {
            return new String[]{"I can start this quest by speaking to the", "Mountain Dwarf near White Wolf Mountain."};
        }
        if (n == STATE_STARTED) {
            return new String[]{"The Mountain Dwarf gave me a pass for", "the Hemenster fishing competition.", "I should go to Hemenster and enter the contest."};
        }
        if (n == STATE_PIPE_STASHED) {
            return new String[]{"I have hidden garlic in the wall pipe by", "the Hemenster fishing contest. I should enter", "the competition and see if it helps."};
        }
        if (n == STATE_IN_COMP) {
            return new String[]{"Bonzo assigned me the fishing spot by the", "willow tree. Grandpa Jack may know how", "to catch a winning fish."};
        }
        if (n == STATE_GARLIC_COMP) {
            return new String[]{"The Sinister Stranger has moved away from", "the pipes. I should fish by the pipes with", "red vine worms from McGrubor's Wood."};
        }
        if (FishingContestQuest.hasCaughtContestFish(n)) {
            return new String[]{"I have caught fish for the Hemenster contest.", "I should hand my catch to Bonzo."};
        }
        if (n == STATE_WON_COMP) {
            return new String[]{"I won the Hemenster fishing trophy.", "I should take it back to the Mountain Dwarf", "near White Wolf Mountain."};
        }
        return null;
    }

    @Override
    public void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        player.packetSender.sendInterfaceText("1 Quest Point", 12150);
        player.packetSender.sendInterfaceText("2,437 Fishing XP", 12151);
        player.packetSender.sendInterfaceText("Use of the dwarven tunnel", 12152);
        player.packetSender.sendInterfaceText("", 12153);
        player.packetSender.sendInterfaceText("", 12154);
        player.packetSender.sendInterfaceText("", 12155);
        player.getSkillManager().addQuestExperience(FISHING, 2437.0);
        player.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 180, FISHING_TROPHY);
        player.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public boolean handleFirstNpcAction(Player player, int npcId, int state) {
        if (FishingContestQuest.isTunnelDwarf(npcId) || npcId == BONZO || npcId == MORRIS
            || npcId == GRANDPA_JACK || FishingContestQuest.isSinisterStranger(npcId)
            || npcId == BIG_DAVE || npcId == JOSHUA) {
            DialogueManager.continueDialogue(player, npcId, 1, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcDialogue(Player player, int npcId, int step, int option, int state) {
        if (FishingContestQuest.isTunnelDwarf(npcId)) {
            return this.handleTunnelDwarfDialogue(player, step, option);
        }
        if (npcId == BONZO) {
            return this.handleBonzoDialogue(player, step, option);
        }
        if (npcId == MORRIS) {
            player.packetSender.sendGameMessage("Morris does not appear interested in talking.");
            return true;
        }
        if (npcId == GRANDPA_JACK) {
            return this.handleGrandpaJackDialogue(player, step, option);
        }
        if (FishingContestQuest.isSinisterStranger(npcId)) {
            return this.handleSinisterStrangerDialogue(player, step, option);
        }
        if (npcId == BIG_DAVE) {
            player.getDialogueManager().showNpcTwoLineDialogue("Oi whaddya think ya doin'? I'm fishin' here.", "Now beat it!", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (npcId == JOSHUA) {
            player.getDialogueManager().showNpcTwoLineDialogue("This is my fishing spot. Ya don't wanna be", "fishing 'ere mate. Cos I'll break your knuckles!", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleFirstObjectAction(Player player, int objectId, int x, int y, int state) {
        if (FishingContestQuest.isRedVine(objectId, x, y)) {
            FishingContestQuest.digRedVine(player);
            return true;
        }
        if (objectId == LOOSE_RAILING && x == 2662 && y == 3500) {
            FishingContestQuest.squeezeLooseRailing(player, x, y);
            return true;
        }
        if (objectId == MCGRUBOR_GATE_LEFT || objectId == MCGRUBOR_GATE_RIGHT) {
            FishingContestQuest.handleMcGruborGate(player, y);
            return true;
        }
        if ((objectId == HEMENSTER_GATE_LEFT || objectId == HEMENSTER_GATE_RIGHT) && x == 2642 && (y == 3441 || y == 3442)) {
            FishingContestQuest.handleHemensterGate(player, x, y);
            return true;
        }
        if (objectId == WALL_PIPE && FishingContestQuest.isHemensterPipe(x, y)) {
            player.packetSender.sendGameMessage("Nothing interesting happens.");
            return true;
        }
        if (objectId == TUNNEL_CAVE_WEST_STAIRS) {
            player.moveTo(TUNNEL_TOP_WEST);
            return true;
        }
        if (objectId == TUNNEL_CAVE_EAST_STAIRS) {
            player.moveTo(TUNNEL_TOP_EAST);
            return true;
        }
        if (objectId == TUNNEL_TOP_WEST_STAIRS) {
            return FishingContestQuest.handleTunnelTopStairs(player, TUNNEL_CAVE_WEST, VESTRI);
        }
        if (objectId == TUNNEL_TOP_EAST_STAIRS) {
            return FishingContestQuest.handleTunnelTopStairs(player, TUNNEL_CAVE_EAST, AUSTRI);
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, int itemId, int objectId, int state) {
        if (FishingContestQuest.isSpade(itemId) && FishingContestQuest.isRedVineObjectId(objectId)) {
            FishingContestQuest.digRedVine(player);
            return true;
        }
        if (FishingContestQuest.isGarlic(itemId) && objectId == WALL_PIPE) {
            FishingContestQuest.stashGarlic(player);
            return true;
        }
        return false;
    }

    private boolean handleTunnelDwarfDialogue(Player player, int step, int option) {
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_COMPLETE) {
            player.getDialogueManager().showNpcTwoLineDialogue("Welcome, oh great Fishing Champion!", "Feel free to pop by any time.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (state == STATE_WON_COMP) {
            if (step == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("Have you won yet?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("Yes I have!", 591);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showNpcOneLineDialogue("Well done! So where is the trophy?", 591);
                return true;
            }
            if (step == 4) {
                if (!player.getInventoryManager().containsItem(FISHING_TROPHY)) {
                    player.getDialogueManager().showPlayerOneLineDialogue("I don't have it with me.", 591);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I have it right here!", 591);
                return true;
            }
            if (step == 5) {
                if (player.getInventoryManager().containsItem(FISHING_TROPHY)) {
                    player.packetSender.sendGameMessage("You give the trophy to the dwarf.");
                    player.getInventoryManager().removeItem(new ItemStack(FISHING_TROPHY, 1));
                    this.awardCompletionRewards(player);
                } else {
                    player.getDialogueManager().showNpcOneLineDialogue("You'd better go get it then hadn't you?", 591);
                    player.getDialogueManager().finishDialogue();
                }
                return true;
            }
            return false;
        }
        if (state >= STATE_STARTED) {
            if (!player.getInventoryManager().containsItemInInventoryOrBank(FISHING_PASS)
                && !player.getInventoryManager().containsItemInInventoryOrBank(FISHING_TROPHY)) {
                player.getDialogueManager().showNpcTwoLineDialogue("Hmmm. It's a good job they sent us spares.", "There you go.", 591);
                player.getInventoryManager().addOrDropItem(new ItemStack(FISHING_PASS, 1));
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showNpcOneLineDialogue("Have you won yet?", 591);
            player.getDialogueManager().showPlayerOneLineDialogue("No, not yet.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step == TUNNEL_DWARF_STAIRS_DIALOGUE) {
            player.getDialogueManager().showNpcOneLineDialogue("Hoi there! Halt!", 591);
            return true;
        }
        if (step == TUNNEL_DWARF_STAIRS_DIALOGUE + 1) {
            player.getDialogueManager().showNpcOneLineDialogue("You can't come in here!", 591);
            return true;
        }
        if (step == TUNNEL_DWARF_STAIRS_DIALOGUE + 2) {
            player.getDialogueManager().showThreeOptions("Why not?", "Oh, sorry, I hadn't realised it was private.", "I'm bigger than you. Let me by!");
            return true;
        }
        if (step == TUNNEL_DWARF_STAIRS_DIALOGUE + 3) {
            if (option == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("Oh, sorry, I hadn't realised it was private.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (option == 3) {
                player.getDialogueManager().showPlayerTwoLineDialogue("I'm bigger than you.", "Let me by!", 591);
                player.getDialogueManager().setNextDialogueStep(TUNNEL_DWARF_STAIRS_DIALOGUE + 6);
                return true;
            }
            player.getDialogueManager().showPlayerOneLineDialogue("Why not?", 591);
            return true;
        }
        if (step == TUNNEL_DWARF_STAIRS_DIALOGUE + 4) {
            player.getDialogueManager().showNpcTwoLineDialogue("This is the home of the Mountain Dwarves.", "How would you like it if I wanted to take a shortcut through your home?", 591);
            return true;
        }
        if (step == TUNNEL_DWARF_STAIRS_DIALOGUE + 5) {
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step == TUNNEL_DWARF_STAIRS_DIALOGUE + 6) {
            player.getDialogueManager().showNpcTwoLineDialogue("Go away!", "You're not going to bully your way in here!", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step == 1) {
            player.getDialogueManager().showNpcOneLineDialogue("Hmmph. What do you want?", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showTwoOptions("Well, let's be friends!", "I was just stopping to say hello!");
            return true;
        }
        if (step == 3) {
            if (option == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("I was just stopping to say hello!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showPlayerOneLineDialogue("Well, let's be friends!", 591);
            return true;
        }
        if (step == 4) {
            player.getDialogueManager().showNpcTwoLineDialogue("I don't make friends easily.", "People need to earn my trust first.", 591);
            return true;
        }
        if (step == 5) {
            player.getDialogueManager().showPlayerOneLineDialogue("And how am I meant to do that?", 591);
            return true;
        }
        if (step == 6) {
            player.getDialogueManager().showNpcThreeLineDialogue("There's a certain gold artefact we're after.", "This artefact is the first prize at the", "Hemenster fishing competition.", 591);
            return true;
        }
        if (step == 7) {
            player.getDialogueManager().showNpcThreeLineDialogue("Fortunately we have acquired a pass to enter", "that competition. Unfortunately Dwarves don't", "make good fishermen.", 591);
            return true;
        }
        if (step == 8) {
            player.getDialogueManager().showTwoOptions("Fortunately I'm alright at fishing!", "I'm not much of a fisherman either.");
            return true;
        }
        if (step == 9) {
            if (option == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("I'm not much of a fisherman either.", 591);
                player.getDialogueManager().showNpcOneLineDialogue("What good are you?", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showPlayerOneLineDialogue("Fortunately I'm alright at fishing!", 591);
            return true;
        }
        if (step == 10) {
            player.getDialogueManager().showNpcTwoLineDialogue("Okay, I entrust you with our competition pass.", "Go to Hemenster and do us proud!", 591);
            if (player.getQuestState(QUEST_ID) == 0) {
                this.startQuest(player);
                player.getInventoryManager().addOrDropItem(new ItemStack(FISHING_PASS, 1));
            }
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleBonzoDialogue(Player player, int step, int option) {
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_WON_COMP || state == STATE_COMPLETE) {
            player.getDialogueManager().showNpcOneLineDialogue("Hello champ!", 591);
            if (state == STATE_WON_COMP && !player.getInventoryManager().containsItem(FISHING_TROPHY)) {
                player.getDialogueManager().showNpcOneLineDialogue("Don't worry, I have a spare!", 591);
                player.getInventoryManager().addOrDropItem(new ItemStack(FISHING_TROPHY, 1));
            }
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (FishingContestQuest.hasCaughtContestFish(state)) {
            if (step == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("So how are you doing so far?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("I caught some fish! Here...", 591);
                return true;
            }
            if (step == 3) {
                FishingContestQuest.handoverCatch(player);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == STATE_IN_COMP || state == STATE_GARLIC_COMP) {
            player.getDialogueManager().showNpcOneLineDialogue("So how are you doing so far?", 591);
            player.getDialogueManager().showPlayerOneLineDialogue("I think I might still be able to find a bigger fish.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (state != STATE_STARTED && state != STATE_PIPE_STASHED) {
            player.getDialogueManager().showNpcOneLineDialogue("Hello champ!", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step == 1) {
            player.getDialogueManager().showNpcOneLineDialogue("Roll up, roll up!", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showNpcOneLineDialogue("Enter the great Hemenster fishing competition!", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().showNpcOneLineDialogue("Only 5gp entrance fee!", 591);
            return true;
        }
        if (step == 4) {
            player.getDialogueManager().showTwoOptions("I'll enter the competition please.", "No thanks, I'll just watch the fun.");
            return true;
        }
        if (step == 5) {
            if (option == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("No thanks, I'll just watch the fun.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showPlayerOneLineDialogue("I'll enter the competition please.", 591);
            return true;
        }
        if (step == 6) {
            if (!player.getInventoryManager().containsItemAmount(COINS, 5)) {
                player.getDialogueManager().showPlayerOneLineDialogue("I don't have the 5gp though...", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getInventoryManager().removeItem(new ItemStack(COINS, 5));
            player.packetSender.sendGameMessage("You pay Bonzo 5 coins.");
            player.getDialogueManager().showNpcOneLineDialogue("Marvelous!", 591);
            return true;
        }
        if (step == 7) {
            player.getDialogueManager().showNpcTwoLineDialogue("Ok, we've got all the fishermen!", "It's time to roll!", 591);
            return true;
        }
        if (step == 8) {
            FishingContestQuest.assignContestSpot(player);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleGrandpaJackDialogue(Player player, int step, int option) {
        int state = player.getQuestState(QUEST_ID);
        if (step == 1) {
            player.getDialogueManager().showNpcThreeLineDialogue("Hello young one! Come to visit old Grandpa Jack?", "I can tell ye stories for sure.", "I used to be the best fisherman these parts have seen!", 591);
            return true;
        }
        if (step == 2) {
            if (state >= STATE_STARTED && state < STATE_COMPLETE) {
                player.getDialogueManager().showThreeOptions("Tell me a story then.", "Sorry, I don't have time now.", "Are you entering the fishing competition?");
            } else {
                player.getDialogueManager().showTwoOptions("Tell me a story then.", "Sorry, I don't have time now.");
            }
            return true;
        }
        if (step == 3) {
            if (option == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("Sorry, I don't have time now.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (option == 3) {
                player.getDialogueManager().showPlayerOneLineDialogue("Are you entering the fishing competition?", 591);
                return true;
            }
            player.getDialogueManager().showPlayerOneLineDialogue("Tell me a story then.", 591);
            return true;
        }
        if (step == 4 && option == 3) {
            player.getDialogueManager().showNpcTwoLineDialogue("Ah... the Hemenster fishing competition...", "I know all about that.", 591);
            return true;
        }
        if (step == 5 && option == 3) {
            player.getDialogueManager().showTwoOptions("I don't suppose you could give me any hints?", "That's less competition for me then.");
            return true;
        }
        if (step == 6 && option == 1) {
            player.getDialogueManager().showNpcThreeLineDialogue("Well, you sometimes get these really big fish", "in the water just by the outflow pipes.", "The best sort of bait for them is red vine worms.", 591);
            return true;
        }
        if (step == 7 && option == 1) {
            player.getDialogueManager().showNpcTwoLineDialogue("I used to get those from McGrubor's wood, north of here.", "Just dig around in the red vines up there.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step >= 4) {
            player.getDialogueManager().showNpcTwoLineDialogue("Well, when I were a young man we used to take", "fishing trips over to Catherby.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean handleSinisterStrangerDialogue(Player player, int step, int option) {
        if (step == 1) {
            player.getDialogueManager().showNpcOneLineDialogue("...", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showThreeOptions("...?", "Who are you?", "So... you like fishing?");
            return true;
        }
        if (step == 3) {
            if (option == 2) {
                player.getDialogueManager().showNpcTwoLineDialogue("My name iz Vlad. I come from far avay,", "vere the sun iz not so bright.", 591);
            } else if (option == 3) {
                player.getDialogueManager().showNpcThreeLineDialogue("My doctor told me to take up a velaxing hobby.", "Vhen I am stressed I tend to get a little..", "..thirsty.", 591);
            } else {
                player.getDialogueManager().showNpcOneLineDialogue("...", 591);
            }
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private boolean attemptFishHemenster(Player player, Npc spot) {
        int state = player.getQuestState(QUEST_ID);
        int npcId = spot.getNpcId();
        if (state == STATE_COMPLETE || state == STATE_WON_COMP) {
            player.getDialogueManager().showOneLineStatement("You have already won the fishing competition! You don't need to catch any more fish here.");
            return true;
        }
        if (!FishingContestQuest.isInCompetition(state)) {
            player.getDialogueManager().showNpcTwoLineDialogue("Hey, you need to pay to enter the competition first!", "Only 5 gp entrance fee!", 591);
            player.packetSender.sendGameMessage("Talk to Bonzo to pay the entrance fee.");
            return true;
        }
        if (npcId == BIG_DAVE_FISH_SPOT) {
            player.getDialogueManager().showNpcTwoLineDialogue("Oi whaddya think ya doin'? I'm fishin' here.", "Now beat it!", 591);
            return true;
        }
        if (npcId == JOSHUA_FISH_SPOT) {
            player.getDialogueManager().showNpcTwoLineDialogue("This is my fishing spot.", "Ya don't wanna be fishing 'ere mate.", 591);
            return true;
        }
        if ((npcId == SINISTER_FISH_SPOT && !FishingContestQuest.isGarlicCompetitionState(state))
            || (npcId == COMP_FISH_SPOT && FishingContestQuest.isGarlicCompetitionState(state))) {
            FishingContestQuest.sinisterMySpot(player);
            return true;
        }
        if (player.getSkillManager().getCurrentLevels()[FISHING] < 10) {
            player.getDialogueManager().showOneLineStatement("You need at least 10 Fishing to lure these fish.");
            return true;
        }
        int bait = player.getInventoryManager().containsItem(RED_VINE_WORM) ? RED_VINE_WORM : FISHING_BAIT;
        boolean hasRod = player.getInventoryManager().containsItem(FISHING_ROD) || player.getInventoryManager().containsItem(FISHING_ROD_ALT);
        boolean hasBait = player.getInventoryManager().containsItem(bait);
        if (!hasRod && !hasBait) {
            player.packetSender.sendGameMessage("You need a fishing rod and some bait to catch these fish.");
            return true;
        }
        if (!hasRod) {
            player.packetSender.sendGameMessage("You need a fishing rod to catch these fish.");
            return true;
        }
        if (!hasBait) {
            player.packetSender.sendGameMessage("You need some bait to catch these fish.");
            return true;
        }
        if (!FishingContestQuest.hasSpaceAfterConsumingBait(player, bait)) {
            player.getDialogueManager().showOneLineStatement("You can't carry any more fish.");
            return true;
        }
        player.getInventoryManager().removeItem(new ItemStack(bait, 1));
        if (npcId == SINISTER_FISH_SPOT && bait == RED_VINE_WORM) {
            player.getInventoryManager().addOrDropItem(new ItemStack(RAW_GIANT_CARP, 1));
            player.packetSender.sendGameMessage("You catch a giant carp.");
        } else if (bait == RED_VINE_WORM) {
            player.getInventoryManager().addOrDropItem(new ItemStack(RAW_SARDINE, 1));
            player.packetSender.sendGameMessage("You catch a sardine.");
        } else if (npcId == SINISTER_FISH_SPOT) {
            player.getInventoryManager().addOrDropItem(new ItemStack(RAW_SARDINE, 1));
            player.packetSender.sendGameMessage("You catch a sardine.");
        } else {
            player.getInventoryManager().addOrDropItem(new ItemStack(RAW_SHRIMPS, 1));
            player.packetSender.sendGameMessage("You catch some shrimps.");
        }
        FishingContestQuest.advanceCatchState(player);
        if (FishingContestQuest.getCatchCount(player.getQuestState(QUEST_ID)) >= 3) {
            player.getDialogueManager().showNpcTwoLineDialogue("Okay folks, time's up!", "Let's see who caught the biggest fish!", 591);
            FishingContestQuest.handoverCatch(player);
        }
        return true;
    }

    private static void assignContestSpot(Player player) {
        player.getDialogueManager().showNpcThreeLineDialogue("Ok, nearly everyone is in their place already.", "You fish in the spot by the willow tree,", "and the Sinister Stranger, you fish by the pipes.", 591);
        if (player.getQuestState(QUEST_ID) == STATE_PIPE_STASHED) {
            FishingContestQuest.moveSinisterForGarlic(player);
            return;
        }
        player.setQuestState(QUEST_ID, STATE_IN_COMP);
        player.getDialogueManager().showOneLineStatement("Your fishing competition spot is beside the willow tree.");
    }

    private static void stashGarlic(Player player) {
        if (!player.getInventoryManager().containsItem(GARLIC) && !player.getInventoryManager().containsItem(GARLIC_ALT)) {
            return;
        }
        player.packetSender.sendGameMessage("You stash the garlic in the pipe.");
        if (player.getInventoryManager().containsItem(GARLIC)) {
            player.getInventoryManager().removeItem(new ItemStack(GARLIC, 1));
        } else {
            player.getInventoryManager().removeItem(new ItemStack(GARLIC_ALT, 1));
        }
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_IN_COMP || state == STATE_WILLOW_FISH_ONE || state == STATE_WILLOW_FISH_TWO) {
            FishingContestQuest.moveSinisterForGarlic(player);
        } else if (state == STATE_STARTED) {
            player.setQuestState(QUEST_ID, STATE_PIPE_STASHED);
        }
    }

    private static void moveSinisterForGarlic(Player player) {
        Npc sinister = Npc.findByDefinitionIdAtPosition(SINISTER_STRANGER, SINISTER_PIPE_POSITION);
        if (sinister == null) {
            sinister = Npc.findByDefinitionIdAtPosition(SINISTER_STRANGER_ALT, SINISTER_PIPE_POSITION);
        }
        if (sinister != null) {
            sinister.moveTo(SINISTER_MOVED_POSITION);
            sinister.setSpawnPosition(SINISTER_MOVED_POSITION);
        }
        player.getDialogueManager().showNpcTwoLineDialogue("Arrgh! WHAT is that GHASTLY smell???", "I think I will move over here instead...", 591);
        player.setQuestState(QUEST_ID, STATE_GARLIC_COMP);
    }

    private static void handoverCatch(Player player) {
        player.packetSender.sendGameMessage("You hand over your catch.");
        boolean hasGiant = player.getInventoryManager().containsItem(RAW_GIANT_CARP);
        FishingContestQuest.removeAll(player, RAW_SHRIMPS);
        FishingContestQuest.removeAll(player, RAW_SARDINE);
        FishingContestQuest.removeAll(player, RAW_GIANT_CARP);
        if (hasGiant) {
            player.setQuestState(QUEST_ID, STATE_WON_COMP);
            player.getInventoryManager().addOrDropItem(new ItemStack(FISHING_TROPHY, 1));
            player.getDialogueManager().showNpcFourLineDialogue("We have a new winner!", "The heroic-looking person who was fishing by the pipes", "has caught the biggest carp I've seen", "since Grandpa Jack used to compete!", 591);
            player.packetSender.sendGameMessage("You are given the Hemenster fishing trophy!");
            return;
        }
        player.setQuestState(QUEST_ID, STATE_STARTED);
        player.getDialogueManager().showNpcOneLineDialogue("And the winner is... The stranger in black!", 591);
    }

    private static void digRedVine(Player player) {
        if (!FishingContestQuest.hasSpade(player)) {
            player.packetSender.sendGameMessage("You need a spade to dig up this vine.");
            return;
        }
        player.getUpdateState().setAnimation(830);
        player.packetSender.sendGameMessage("You use your spade with the vine.");
        player.packetSender.sendGameMessage("You dig in amongst the vines.");
        if (player.getInventoryManager().getContainer().getFreeSlots() == 0 && !player.getInventoryManager().containsItem(RED_VINE_WORM)) {
            player.packetSender.sendGameMessage("You do not have enough space to carry this.");
            return;
        }
        player.packetSender.sendGameMessage("You find a red vine worm.");
        player.getInventoryManager().addOrDropItem(new ItemStack(RED_VINE_WORM, 1));
    }

    private static void handleHemensterGate(Player player, int x, int y) {
        int state = player.getQuestState(QUEST_ID);
        if (player.getPosition().getX() <= x) {
            if (FishingContestQuest.isInCompetition(state) || FishingContestQuest.hasCaughtContestFish(state)) {
                player.getDialogueManager().showNpcOneLineDialogue("So you're calling it quits here for now?", 591);
                player.setQuestState(QUEST_ID, STATE_STARTED);
            }
            player.moveTo(new Position(x + 1, y, player.getPosition().getPlane()));
            return;
        }
        player.getDialogueManager().showNpcOneLineDialogue("Competition pass please.", 591);
        if (!player.getInventoryManager().containsItem(FISHING_PASS)) {
            player.getDialogueManager().showNpcTwoLineDialogue("This is the entrance to the Hemenster fishing", "competition. It's invitation only.", 591);
            return;
        }
        player.packetSender.sendGameMessage("You show Morris your pass.");
        player.getDialogueManager().showNpcOneLineDialogue("Move on through.", 591);
        player.moveTo(new Position(x - 1, y, player.getPosition().getPlane()));
    }

    private static void handleMcGruborGate(Player player, int y) {
        if (player.getPosition().getY() > y) {
            player.packetSender.sendGameMessage("The gate is locked.");
            return;
        }
        player.getDialogueManager().showNpcTwoLineDialogue("Hey! You can't come through here!", "This is private land!", 591);
        player.getDialogueManager().showOneLineStatement("You will need to find another way in.");
    }

    private static boolean handleTunnelTopStairs(Player player, Position destination, int dwarfId) {
        if (player.getQuestState(QUEST_ID) == STATE_COMPLETE) {
            player.moveTo(destination);
            return true;
        }
        DialogueManager.continueDialogue(player, dwarfId, TUNNEL_DWARF_STAIRS_DIALOGUE, 0);
        return true;
    }

    private static void squeezeLooseRailing(Player player, int x, int y) {
        player.packetSender.sendGameMessage("You squeeze through the loose railing.");
        int targetX = player.getPosition().getX() < x ? x + 1 : x - 1;
        player.moveTo(new Position(targetX, y, player.getPosition().getPlane()));
    }

    private static void sinisterMySpot(Player player) {
        player.getDialogueManager().showNpcOneLineDialogue("I think you will find that is my spot.", 591);
        if (!FishingContestQuest.isGarlicCompetitionState(player.getQuestState(QUEST_ID))) {
            player.getDialogueManager().showPlayerOneLineDialogue("Can't you go to another spot?", 591);
            player.getDialogueManager().showNpcTwoLineDialogue("I like this place.", "I like to savour the aroma coming from these pipes.", 591);
        }
    }

    private static void advanceCatchState(Player player) {
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_IN_COMP) {
            player.setQuestState(QUEST_ID, STATE_WILLOW_FISH_ONE);
        } else if (state == STATE_WILLOW_FISH_ONE) {
            player.setQuestState(QUEST_ID, STATE_WILLOW_FISH_TWO);
        } else if (state == STATE_WILLOW_FISH_TWO) {
            player.setQuestState(QUEST_ID, STATE_WILLOW_FISH_THREE);
        } else if (state == STATE_GARLIC_COMP) {
            player.setQuestState(QUEST_ID, STATE_GARLIC_FISH_ONE);
        } else if (state == STATE_GARLIC_FISH_ONE) {
            player.setQuestState(QUEST_ID, STATE_GARLIC_FISH_TWO);
        } else if (state == STATE_GARLIC_FISH_TWO) {
            player.setQuestState(QUEST_ID, STATE_GARLIC_FISH_THREE);
        }
    }

    private static int getCatchCount(int state) {
        if (state == STATE_WILLOW_FISH_ONE || state == STATE_GARLIC_FISH_ONE) {
            return 1;
        }
        if (state == STATE_WILLOW_FISH_TWO || state == STATE_GARLIC_FISH_TWO) {
            return 2;
        }
        if (state == STATE_WILLOW_FISH_THREE || state == STATE_GARLIC_FISH_THREE) {
            return 3;
        }
        return 0;
    }

    private static boolean hasCaughtContestFish(int state) {
        return state == STATE_WILLOW_FISH_ONE || state == STATE_WILLOW_FISH_TWO
            || state == STATE_WILLOW_FISH_THREE || state == STATE_GARLIC_FISH_ONE
            || state == STATE_GARLIC_FISH_TWO || state == STATE_GARLIC_FISH_THREE;
    }

    private static boolean isInCompetition(int state) {
        return state == STATE_IN_COMP || state == STATE_GARLIC_COMP || FishingContestQuest.hasCaughtContestFish(state);
    }

    private static boolean isGarlicCompetitionState(int state) {
        return state == STATE_GARLIC_COMP || state == STATE_GARLIC_FISH_ONE
            || state == STATE_GARLIC_FISH_TWO || state == STATE_GARLIC_FISH_THREE;
    }

    private static boolean hasSpaceAfterConsumingBait(Player player, int bait) {
        if (player.getInventoryManager().getContainer().getFreeSlots() > 0) {
            return true;
        }
        return player.getInventoryManager().getItemAmount(bait) <= 1;
    }

    private static boolean isTunnelDwarf(int npcId) {
        return npcId == AUSTRI || npcId == VESTRI;
    }

    private static boolean isSinisterStranger(int npcId) {
        return npcId == SINISTER_STRANGER || npcId == SINISTER_STRANGER_ALT;
    }

    private static boolean isSpade(int itemId) {
        return itemId == SPADE || itemId == SPADE_ALT;
    }

    private static boolean hasSpade(Player player) {
        return player.getInventoryManager().containsItem(SPADE) || player.getInventoryManager().containsItem(SPADE_ALT);
    }

    private static boolean isGarlic(int itemId) {
        return itemId == GARLIC || itemId == GARLIC_ALT;
    }

    private static boolean isRedVine(int objectId, int x, int y) {
        return FishingContestQuest.isRedVineObjectId(objectId)
            && x >= 2630 && x <= 2633 && y >= 3493 && y <= 3499;
    }

    private static boolean isRedVineObjectId(int objectId) {
        int n = 0;
        while (n < RED_VINE_OBJECTS.length) {
            if (RED_VINE_OBJECTS[n] == objectId) {
                return true;
            }
            ++n;
        }
        return false;
    }

    private static boolean isHemensterPipe(int x, int y) {
        return x >= 2636 && x <= 2638 && y == 3446;
    }

    private static boolean isObjectDefinitionAvailable(int objectId) {
        return ObjectDefinition.definitionsById != null
            && objectId >= 0
            && objectId < ObjectDefinition.definitionsById.length
            && ObjectDefinition.forId(objectId) != null;
    }

    private static void removeAll(Player player, int itemId) {
        while (player.getInventoryManager().containsItem(itemId)) {
            player.getInventoryManager().removeItem(new ItemStack(itemId, 1));
        }
    }

    private static void spawnNpcIfMissingAt(int id, int x, int y, int plane, int walk) {
        if (!NpcDefinition.isDefined(id)) {
            return;
        }
        if (GameplayHelper.isNpcSpawnCoveredByNearbySpawn(id, x, y, plane, 5, NPC_SPAWNS)) {
            return;
        }
        GameplayHelper.spawnNpc(id, x, y, plane, walk);
        Npc npc = Npc.findByDefinitionIdAtPosition(id, new Position(x, y, plane));
        if (FishingContestQuest.isContestFishingSpot(id) && npc != null) {
            npc.setMovementMode(NpcMovementMode.STATIONARY);
        }
    }

    private static void spawnObjectIfMissing(int id, int x, int y, int plane, int orientation, int type) {
        if (!GameplayHelper.isObjectDefinitionIdValid(id)) {
            return;
        }
        if (ObjectManager.findDynamicObjectAt(x, y, plane) != null) {
            return;
        }
        if (WorldObjectLookup.findObjectByIdAt(id, x, y, plane) != null) {
            return;
        }
        new DynamicObject(id, x, y, plane, orientation, type, ServerSettings.placeholderObjectId, 999999999, false);
    }
}
