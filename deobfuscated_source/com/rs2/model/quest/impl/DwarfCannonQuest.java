/*
 * Dwarf Cannon quest content for the 2004-era multicannon.
 */
package com.rs2.model.quest.impl;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.gameplay.dwarfcannon.DwarfCannonManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestScript;
import com.rs2.model.shop.ShopManager;

public final class DwarfCannonQuest
extends QuestScript {
    public static final int QUEST_ID = 30;
    private static final int PROGRESS_MASK = 255;
    private static final int RAILING_BIT_START = 8;
    private static final int CANNON_REPAIR_BIT_START = 14;

    private static final int STATE_COMPLETE = 1;
    private static final int STATE_FIX_RAILINGS = 2;
    private static final int STATE_CHECK_TOWER = 3;
    private static final int STATE_FIND_CAVE = 4;
    private static final int STATE_FIND_CHILD = 5;
    private static final int STATE_RETURN_WITH_CHILD = 6;
    private static final int STATE_FIX_CANNON = 7;
    private static final int STATE_INSPECTED_CANNON = 8;
    private static final int STATE_REPAIRED_CANNON = 9;
    private static final int STATE_SPEAK_TO_NULODION = 10;
    private static final int STATE_RETURN_WITH_NOTES = 11;

    private static final int DWARF_REMAINS = 0;
    private static final int TOOLKIT = 1;
    private static final int CANNONBALL = 2;
    private static final int NULODIONS_NOTES = 3;
    private static final int AMMO_MOULD = 4;
    private static final int INSTRUCTION_MANUAL = 5;
    private static final int CANNON_BASE = 6;
    private static final int CANNON_STAND = 8;
    private static final int CANNON_BARRELS = 10;
    private static final int CANNON_FURNACE = 12;
    private static final int RAILING_ITEM = 14;
    private static final int COINS = 995;
    private static final int STEEL_BAR = 2353;

    private static final int DWARF_GUARD = 206;
    private static final int DWARF_CHILD = 207;
    private static final int LAWG0F = 208;
    private static final int NULODION = 209;

    private static final int EMPTY_CRATE = 0;
    private static final int CHILD_CRATE = 1;
    private static final int CAVE_ENTRANCE = 2;
    private static final int NULODION_DOOR = 3;
    private static final int LAWG0F_SHED_DOOR = 4;
    private static final int BROKEN_CANNON = 5;
    private static final int MUD_PILE = 13;
    private static final int PLAIN_RAILING = 14;
    private static final int FIRST_DAMAGED_RAILING = 15;
    private static final int LAST_DAMAGED_RAILING = 20;
    private static final int WATCH_TOWER_LADDER = 1747;

    private static final Position CAVE_ENTRY_POSITION = new Position(2620, 9797, 0);
    private static final Position CAVE_EXIT_POSITION = new Position(2623, 3391, 0);
    private static final Position DWARF_CHILD_POSITION = new Position(2571, 9851, 0);
    private static final Position WATCH_TOWER_LADDER_POSITION = new Position(2570, 3441, 0);

    private static final int[][] NPC_SPAWNS = new int[][]{
        {DWARF_GUARD, 2554, 3465, 0, 0},
        {DWARF_GUARD, 2554, 3469, 0, 0},
        {DWARF_GUARD, 2564, 3456, 0, 0},
        {LAWG0F, 2571, 3463, 0, 0},
        {DWARF_GUARD, 2572, 3456, 0, 0},
        {NULODION, 3011, 3453, 0, 0},
        {DWARF_GUARD, 3016, 3448, 0, 0},
        {DWARF_GUARD, 3016, 3450, 0, 0},
        {DWARF_GUARD, 3017, 3447, 0, 0},
        {DWARF_GUARD, 3021, 3447, 0, 0},
        {DWARF_GUARD, 3009, 3458, 0, 0},
        {DWARF_GUARD, 3015, 3459, 0, 0},
        {DWARF_GUARD, 3018, 3456, 0, 0},
        {DWARF_GUARD, 3027, 3457, 0, 0}
    };

    private static final int[][] OBJECT_SPAWNS = new int[][]{
        {WATCH_TOWER_LADDER, 2570, 3441, 0, 2, 10},
        {CAVE_ENTRANCE, 2622, 3392, 0, 0, 10},
        {MUD_PILE, 2621, 9796, 0, 2, 10},
        {CHILD_CRATE, 2571, 9850, 0, 3, 10},
        {NULODION_DOOR, 3015, 3453, 0, 0, 0},
        {LAWG0F_SHED_DOOR, 2576, 3461, 0, 0, 0},
        {BROKEN_CANNON, 2577, 3461, 0, 0, 10},
        {PLAIN_RAILING, 2553, 3477, 0, 3, 9},
        {PLAIN_RAILING, 2553, 3478, 0, 0, 0},
        {PLAIN_RAILING, 2553, 3479, 0, 0, 0},
        {PLAIN_RAILING, 2553, 3480, 0, 0, 9},
        {PLAIN_RAILING, 2554, 3477, 0, 3, 0},
        {PLAIN_RAILING, 2554, 3481, 0, 0, 9},
        {PLAIN_RAILING, 2555, 3465, 0, 3, 9},
        {PLAIN_RAILING, 2555, 3468, 0, 0, 9},
        {PLAIN_RAILING, 2555, 3476, 0, 3, 9},
        {PLAIN_RAILING, 2555, 3482, 0, 0, 0},
        {PLAIN_RAILING, 2556, 3464, 0, 3, 9},
        {PLAIN_RAILING, 2556, 3469, 0, 0, 9},
        {PLAIN_RAILING, 2556, 3474, 0, 3, 9},
        {FIRST_DAMAGED_RAILING, 2556, 3475, 0, 0, 0},
        {17, 2557, 3464, 0, 3, 0},
        {PLAIN_RAILING, 2557, 3470, 0, 0, 0},
        {PLAIN_RAILING, 2557, 3471, 0, 0, 9},
        {PLAIN_RAILING, 2557, 3473, 0, 3, 9},
        {PLAIN_RAILING, 2558, 3463, 0, 3, 9},
        {16, 2558, 3472, 0, 0, 0},
        {PLAIN_RAILING, 2559, 3460, 0, 3, 9},
        {PLAIN_RAILING, 2559, 3461, 0, 0, 0},
        {18, 2559, 3462, 0, 0, 0},
        {PLAIN_RAILING, 2560, 3460, 0, 3, 0},
        {PLAIN_RAILING, 2561, 3460, 0, 2, 9},
        {PLAIN_RAILING, 2562, 3461, 0, 3, 0},
        {PLAIN_RAILING, 2563, 3460, 0, 3, 9},
        {19, 2564, 3460, 0, 3, 0},
        {PLAIN_RAILING, 2565, 3458, 0, 3, 9},
        {PLAIN_RAILING, 2565, 3459, 0, 0, 0},
        {PLAIN_RAILING, 2566, 3458, 0, 3, 0},
        {PLAIN_RAILING, 2569, 3458, 0, 3, 0},
        {PLAIN_RAILING, 2570, 3458, 0, 2, 9},
        {PLAIN_RAILING, 2571, 3459, 0, 2, 9},
        {20, 2572, 3460, 0, 3, 0},
        {PLAIN_RAILING, 2573, 3460, 0, 3, 0},
        {PLAIN_RAILING, 2574, 3459, 0, 3, 9},
        {PLAIN_RAILING, 2575, 3456, 0, 3, 9},
        {PLAIN_RAILING, 2575, 3457, 0, 0, 0},
        {PLAIN_RAILING, 2575, 3458, 0, 0, 0},
        {PLAIN_RAILING, 2576, 3456, 0, 3, 0},
        {PLAIN_RAILING, 2577, 3456, 0, 3, 0},
        {PLAIN_RAILING, 2578, 3456, 0, 3, 0},
        {PLAIN_RAILING, 2579, 3456, 0, 3, 0},
        {PLAIN_RAILING, 2580, 3456, 0, 3, 0}
    };

    private static final int[] FURNACE_OBJECTS = new int[]{
        2781, 2785, 2966, 3044, 3294, 4304, 4305, 6189, 6190, 9390,
        11009, 11010, 11666, 12100, 12809, 14921
    };

    public DwarfCannonQuest(int n) {
        super(QUEST_ID);
        super.setQuestPointReward(1);
    }

    public static void spawnMissingContent() {
        int n = 0;
        while (n < NPC_SPAWNS.length) {
            int[] spawn = NPC_SPAWNS[n];
            DwarfCannonQuest.spawnNpcIfMissingAt(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
            ++n;
        }
        n = 0;
        while (n < OBJECT_SPAWNS.length) {
            int[] spawn = OBJECT_SPAWNS[n];
            DwarfCannonQuest.spawnObjectIfMissing(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], spawn[5]);
            ++n;
        }
    }

    public static boolean handleNulodionTrade(Player player) {
        if (player.getQuestState(QUEST_ID) != STATE_COMPLETE) {
            player.packetSender.sendGameMessage("The dwarf doesn't seem interested in trading.");
            return true;
        }
        ShopManager.openShop(player, 110);
        return true;
    }

    @Override
    public String[] buildQuestJournal(Player player, int n) {
        int progress = DwarfCannonQuest.getProgress(n);
        if (n == 0) {
            return new String[]{"I can start this quest by speaking to the Dwarf", "Commander at the coal truck mining site."};
        }
        if (progress == STATE_FIX_RAILINGS) {
            return new String[]{"I should replace the broken railings around the", "Black Guard camp south of the coal trucks."};
        }
        if (progress == STATE_CHECK_TOWER) {
            return new String[]{"The railings are fixed. I should check the watch", "tower south of the camp and look for the missing guard."};
        }
        if (progress == STATE_FIND_CAVE) {
            return new String[]{"I found dwarf remains in the tower. Lawgof asked me", "to find the goblin cave and rescue Gilob's son."};
        }
        if (progress == STATE_FIND_CHILD) {
            return new String[]{"I found the goblin cave. I should search it for", "Gilob's son."};
        }
        if (progress == STATE_RETURN_WITH_CHILD) {
            return new String[]{"Gilob's son is safe. I should return to Commander", "Lawgof."};
        }
        if (progress == STATE_FIX_CANNON || progress == STATE_INSPECTED_CANNON) {
            return new String[]{"Lawgof has asked me to repair the broken cannon in", "the shed near the Black Guard camp."};
        }
        if (progress == STATE_REPAIRED_CANNON) {
            return new String[]{"The cannon is repaired. I should tell Commander", "Lawgof."};
        }
        if (progress == STATE_SPEAK_TO_NULODION) {
            return new String[]{"Lawgof sent me to speak with Nulodion, the Cannon", "Engineer, south of Ice Mountain."};
        }
        if (progress == STATE_RETURN_WITH_NOTES) {
            return new String[]{"Nulodion gave me notes and an ammo mould. I should", "take them back to Commander Lawgof."};
        }
        if (n == STATE_COMPLETE) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "750 Crafting XP"};
        }
        return null;
    }

    @Override
    public void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        Player player2 = player;
        player2.packetSender.sendInterfaceText("1 Quest Point", 12150);
        player2.packetSender.sendInterfaceText("750 Crafting XP", 12151);
        player2.packetSender.sendInterfaceText("Permission to use dwarf multicannons", 12152);
        player2.packetSender.sendInterfaceText("", 12153);
        player2.packetSender.sendInterfaceText("", 12154);
        player2.packetSender.sendInterfaceText("", 12155);
        player.getSkillManager().addQuestExperience(12, 750.0);
        player2.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, CANNON_BASE);
        player2.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public boolean handleFirstNpcAction(Player player, int n, int n2) {
        if (n == LAWG0F || n == NULODION || n == DWARF_GUARD || n == DWARF_CHILD) {
            DialogueManager.continueDialogue(player, n, 1, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcDialogue(Player player, int npcId, int step, int option, int state) {
        if (npcId == LAWG0F) {
            return DwarfCannonQuest.handleLawgofDialogue(this, player, step, option, state);
        }
        if (npcId == NULODION) {
            return DwarfCannonQuest.handleNulodionDialogue(player, step, option, state);
        }
        if (npcId == DWARF_GUARD) {
            if (step == 1) {
                player.getDialogueManager().showNpcTwoLineDialogue("Keep your eyes open. The goblins have been", "troubling this post.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (npcId == DWARF_CHILD) {
            return DwarfCannonQuest.handleChildDialogue(player, step, state);
        }
        return false;
    }

    @Override
    public boolean handleFirstObjectAction(Player player, int objectId, int x, int y, int state) {
        boolean watchTowerLadder = DwarfCannonQuest.isWatchTowerLadder(objectId, x, y);
        if (!DwarfCannonQuest.isQuestObject(objectId) && !watchTowerLadder) {
            return false;
        }
        int progress = DwarfCannonQuest.getProgress(state);
        if (watchTowerLadder) {
            return DwarfCannonQuest.handleWatchTowerLadder(player, x, y, progress);
        }
        if (objectId >= FIRST_DAMAGED_RAILING && objectId <= LAST_DAMAGED_RAILING || objectId == PLAIN_RAILING) {
            return DwarfCannonQuest.handleRailing(player, objectId, x, y, progress);
        }
        if (objectId == CAVE_ENTRANCE) {
            player.packetSender.sendGameMessage("You cautiously enter the cave.");
            if (progress == STATE_FIND_CAVE) {
                DwarfCannonQuest.setProgress(player, STATE_FIND_CHILD);
            }
            player.moveTo(CAVE_ENTRY_POSITION);
            return true;
        }
        if (objectId == MUD_PILE) {
            player.getUpdateState().setAnimation(828);
            player.moveTo(CAVE_EXIT_POSITION);
            return true;
        }
        if (objectId == CHILD_CRATE || objectId == EMPTY_CRATE) {
            return DwarfCannonQuest.handleCrate(player, objectId, progress);
        }
        if (objectId == BROKEN_CANNON) {
            return DwarfCannonQuest.handleBrokenCannon(player, progress);
        }
        if (objectId == NULODION_DOOR) {
            if (progress < STATE_SPEAK_TO_NULODION && progress != STATE_COMPLETE) {
                player.packetSender.sendGameMessage("The door is locked.");
                return true;
            }
            player.packetSender.queueRelativeMovementStep(player.getPosition().getX() < x ? 1 : -1, 0, true);
            return true;
        }
        if (objectId == LAWG0F_SHED_DOOR) {
            if (progress < STATE_FIX_CANNON) {
                player.packetSender.sendGameMessage("The door is locked.");
                return true;
            }
            player.packetSender.queueRelativeMovementStep(player.getPosition().getX() < x ? 1 : -1, 0, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleGroundItemInteraction(Player player, int itemId, int state) {
        if (itemId != DWARF_REMAINS) {
            return false;
        }
        if (DwarfCannonQuest.getProgress(state) != STATE_CHECK_TOWER) {
            player.getDialogueManager().showPlayerOneLineDialogue("I'm not sure why I'd want to do that...", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (player.getInventoryManager().containsItem(DWARF_REMAINS)) {
            player.getDialogueManager().showPlayerOneLineDialogue("Carrying one 'dwarfs remains' is bad enough.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        player.getInventoryManager().addItem(new ItemStack(DWARF_REMAINS, 1));
        return true;
    }

    @Override
    public boolean handleItemOnObject(Player player, int itemId, int objectId, int state) {
        if (itemId == STEEL_BAR && DwarfCannonQuest.isFurnaceObject(objectId)) {
            return DwarfCannonQuest.makeCannonballs(player);
        }
        return false;
    }

    @Override
    public boolean handleInventoryItemFirstOption(Player player, int itemId, int interfaceId, int state) {
        if (itemId == NULODIONS_NOTES) {
            player.getDialogueManager().showFourLineStatement("The note reads...", "Ammo for the Dwarf Multi Cannon must be made from steel bars.", "The bars must be heated in a furnace and used", "with the ammo mould.");
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (itemId == INSTRUCTION_MANUAL) {
            player.getDialogueManager().showFourLineStatement("Dwarven Multi Cannon", "Set down the base, add the stand, add the barrels,", "then add the furnace. Make cannonballs by using", "steel bars on a furnace while carrying an ammo mould.");
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handleLawgofDialogue(DwarfCannonQuest quest, Player player, int step, int option, int state) {
        int progress = DwarfCannonQuest.getProgress(state);
        if (state == 0) {
            return DwarfCannonQuest.handleLawgofStart(quest, player, step, option);
        }
        if (progress == STATE_FIX_RAILINGS) {
            if (step == 1) {
                player.getDialogueManager().showNpcTwoLineDialogue("Hello again traveller.", "How are you doing with those railings?", 591);
                return true;
            }
            if (step == 2) {
                if (DwarfCannonQuest.allRailingsFixed(state)) {
                    player.getDialogueManager().showNpcThreeLineDialogue("The goblins seem to have stopped getting in.", "I think you've done the job. Could you check up on", "a guard in the Black Guard watch tower to the south?", 591);
                    DwarfCannonQuest.setProgress(player, STATE_CHECK_TOWER);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                player.getDialogueManager().showNpcTwoLineDialogue("The goblins are still getting in, so there must still", "be some broken railings.", 591);
                if (!player.getInventoryManager().containsItem(RAILING_ITEM)) {
                    player.getInventoryManager().addItem(new ItemStack(RAILING_ITEM, 1));
                    player.packetSender.sendGameMessage("The Dwarf Commander gives you another railing.");
                }
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (progress == STATE_CHECK_TOWER) {
            if (step == 1) {
                if (!player.getInventoryManager().containsItem(DWARF_REMAINS)) {
                    player.getDialogueManager().showNpcThreeLineDialogue("Have you been to the watch tower yet?", "Gilob never leaves his post. Can you return", "and look for clues?", 591);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                player.getDialogueManager().showNpcTwoLineDialogue("That's strange, Gilob never leaves his post.", "Do you have any news?", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showOneLineStatement("You show the Dwarf Commander the remains.");
                player.getInventoryManager().removeItem(new ItemStack(DWARF_REMAINS, 1));
                DwarfCannonQuest.setProgress(player, STATE_FIND_CAVE);
                return true;
            }
            if (step == 3) {
                player.getDialogueManager().showNpcThreeLineDialogue("Oh no, it can't be! Where is Gilob's son?", "The goblins must have taken him. Please seek out", "their base and return the lad to us.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (progress == STATE_FIND_CAVE || progress == STATE_FIND_CHILD) {
            player.getDialogueManager().showNpcTwoLineDialogue("Have you managed to find the goblin base", "and Gilob's son yet?", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (progress == STATE_RETURN_WITH_CHILD) {
            return DwarfCannonQuest.handleLawgofChildReturned(player, step, option);
        }
        if (progress == STATE_FIX_CANNON || progress == STATE_INSPECTED_CANNON) {
            if (step == 1) {
                player.getDialogueManager().showNpcThreeLineDialogue("How are you doing in there, adventurer?", "We've been trying our best with that thing, but", "I just haven't got the patience.", 591);
                if (!player.getInventoryManager().containsItem(TOOLKIT)) {
                    player.getInventoryManager().addItem(new ItemStack(TOOLKIT, 1));
                    player.packetSender.sendGameMessage("The Dwarf Commander gives you another toolkit.");
                }
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (progress == STATE_REPAIRED_CANNON) {
            return DwarfCannonQuest.handleLawgofCannonRepaired(player, step, option);
        }
        if (progress == STATE_SPEAK_TO_NULODION) {
            player.getDialogueManager().showNpcThreeLineDialogue("Any word from the Cannon Engineer?", "The Black Guard camp is south of Ice Mountain.", "Speak to Nulodion as soon as you can.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (progress == STATE_RETURN_WITH_NOTES) {
            return DwarfCannonQuest.handleLawgofFinalTurnIn(quest, player, step);
        }
        if (state == STATE_COMPLETE) {
            player.getDialogueManager().showNpcTwoLineDialogue("Well, hello there, how you doing?", "The goblins can't get close with this cannon blasting at them!", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handleLawgofStart(DwarfCannonQuest quest, Player player, int step, int option) {
        if (step == 1) {
            player.getDialogueManager().showPlayerOneLineDialogue("Hello.", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showNpcThreeLineDialogue("Hello traveller, I'm pleased to see you.", "We were hoping to find an extra pair of hands.", "That's if you don't mind helping?", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().showTwoOptions("I'm sorry, I'm too busy mining.", "Yeah, I'd love to help.");
            return true;
        }
        if (step == 4) {
            if (option == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("Yeah, I'd love to help.", 591);
                quest.startQuest(player);
                DwarfCannonQuest.setProgress(player, STATE_FIX_RAILINGS);
                player.getInventoryManager().addItem(new ItemStack(RAILING_ITEM, 6));
                player.packetSender.sendGameMessage("The Dwarf Commander gives you six railings.");
                return true;
            }
            player.getDialogueManager().showNpcOneLineDialogue("Ok then, we'll have to find someone else.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step == 5) {
            player.getDialogueManager().showNpcFourLineDialogue("Thank you, we have no time to waste.", "The goblins are attacking from the forests to the south.", "They managed to get through the broken railings.", "Could you please replace them with these new ones?", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handleLawgofChildReturned(Player player, int step, int option) {
        if (step == 1) {
            player.getDialogueManager().showNpcThreeLineDialogue("He has returned, and I thank you from the bottom", "of my heart. Without you he'd be goblin barbecue!", "I have one more favour to ask you.", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showNpcThreeLineDialogue("The Black Guard have sent us a cannon to help", "the situation, but we're having trouble fixing it.", "Could you fix it for us?", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().showTwoOptions("Ok, I'll see what I can do.", "Sorry, I've done enough for today.");
            return true;
        }
        if (step == 4) {
            if (option == 1) {
                player.getInventoryManager().addItem(new ItemStack(TOOLKIT, 1));
                DwarfCannonQuest.setProgress(player, STATE_FIX_CANNON);
                player.packetSender.sendGameMessage("The Dwarf Commander gives you a tool kit.");
                player.getDialogueManager().showNpcOneLineDialogue("Let me know how you get on.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showNpcOneLineDialogue("Fair enough, take care traveller.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handleLawgofCannonRepaired(Player player, int step, int option) {
        if (step == 1) {
            player.getDialogueManager().showNpcThreeLineDialogue("Well I don't believe it, it seems to be in working order.", "The Black Guard forgot to send instructions, though.", "Could you go to their base and ask Nulodion?", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showTwoOptions("Sorry, I've really done enough.", "Ok then, just for you!");
            return true;
        }
        if (step == 3) {
            if (option == 2) {
                DwarfCannonQuest.setProgress(player, STATE_SPEAK_TO_NULODION);
                player.getDialogueManager().showNpcThreeLineDialogue("You're a good adventurer.", "The base is located just south of Ice Mountain.", "Speak to Nulodion, the Dwarf Cannon Engineer.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showNpcOneLineDialogue("Fair enough.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handleLawgofFinalTurnIn(DwarfCannonQuest quest, Player player, int step) {
        if (step == 1) {
            if (!player.getInventoryManager().containsItem(NULODIONS_NOTES) || !player.getInventoryManager().containsItem(AMMO_MOULD)) {
                player.getDialogueManager().showNpcTwoLineDialogue("If you could go back and get another copy of", "the missing items, I'd appreciate it.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showOneLineStatement("You hand the Dwarf Commander the mould and the notes.");
            player.getInventoryManager().removeItem(new ItemStack(NULODIONS_NOTES, 1));
            player.getInventoryManager().removeItem(new ItemStack(AMMO_MOULD, 1));
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showNpcThreeLineDialogue("Aah, of course, we have to make the ammo!", "This is great. Now we will be able to defend ourselves.", "I don't know how to thank you.", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().finishDialogue();
            quest.awardCompletionRewards(player);
            return true;
        }
        return false;
    }

    private static boolean handleNulodionDialogue(Player player, int step, int option, int state) {
        int progress = DwarfCannonQuest.getProgress(state);
        if (progress == STATE_SPEAK_TO_NULODION) {
            if (step == 1) {
                player.getDialogueManager().showNpcTwoLineDialogue("Can I help you?", "The Dwarf Commander sent you? Of course.", 591);
                return true;
            }
            if (step == 2) {
                DwarfCannonQuest.giveMissingNulodionItems(player);
                DwarfCannonQuest.setProgress(player, STATE_RETURN_WITH_NOTES);
                player.packetSender.sendGameMessage("The Cannon Engineer gives you some notes and a mould.");
                player.getDialogueManager().showNpcTwoLineDialogue("Take these to him.", "The instructions explain everything.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (progress == STATE_RETURN_WITH_NOTES) {
            if (step == 1) {
                DwarfCannonQuest.giveMissingNulodionItems(player);
                player.getDialogueManager().showNpcTwoLineDialogue("If you can get those items to the commander", "it'll help him work out the cannon.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (state == STATE_COMPLETE) {
            return DwarfCannonQuest.handlePostQuestNulodion(player, step, option);
        }
        if (step == 1) {
            player.getDialogueManager().showNpcOneLineDialogue("The dwarf doesn't seem interested in talking right now.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handlePostQuestNulodion(Player player, int step, int option) {
        if (step == 1) {
            player.getDialogueManager().showNpcTwoLineDialogue("Hello traveller, how's things?", "I'm good, just working hard as usual...", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showFourOptions("I was hoping you might sell me a cannon?", "Can I see your spare parts?", "I want to know more about the cannon.", "I've lost my cannon.");
            return true;
        }
        if (step == 3) {
            if (option == 1) {
                player.getDialogueManager().showNpcFourLineDialogue("I shouldn't really, but as you helped us so much,", "I could sort something out. For the full set up:", "750,000 coins. Separate parts cost 200,000 each.", "That's not cheap!", 591);
                player.getDialogueManager().setNextDialogueStep(20);
                return true;
            }
            if (option == 2) {
                ShopManager.openShop(player, 110);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (option == 3) {
                player.getDialogueManager().showNpcThreeLineDialogue("The cannon automatically targets monsters close by.", "You just have to make the ammo and let it rip.", "It will fire up to 30 rounds before stopping.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            if (option == 4) {
                DwarfCannonQuest.reclaimLostCannon(player);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (step == 20) {
            player.getDialogueManager().showTwoOptions("Ok, I'll take a cannon please.", "Sorry, that's too much for me.");
            return true;
        }
        if (step == 21) {
            if (option == 1) {
                DwarfCannonQuest.buyFullCannon(player);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            player.getDialogueManager().showNpcOneLineDialogue("Fair enough, it's too much for most of us.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handleChildDialogue(Player player, int step, int state) {
        int progress = DwarfCannonQuest.getProgress(state);
        if (progress != STATE_FIND_CHILD && progress != STATE_RETURN_WITH_CHILD) {
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showNpcTwoLineDialogue("Thank the heavens, you saved me!", "I thought I'd be goblin lunch for sure!", 591);
            return true;
        }
        if (step == 2) {
            player.getDialogueManager().showPlayerOneLineDialogue("Are you okay?", 591);
            return true;
        }
        if (step == 3) {
            DwarfCannonQuest.setProgress(player, STATE_RETURN_WITH_CHILD);
            player.getDialogueManager().showNpcTwoLineDialogue("I think so, I'd better run off home.", "Thanks again, brave adventurer.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        return false;
    }

    private static boolean handleRailing(Player player, int objectId, int x, int y, int progress) {
        if (progress == 0) {
            player.packetSender.sendGameMessage("Nothing interesting happens.");
            return true;
        }
        if (progress != STATE_FIX_RAILINGS) {
            player.getDialogueManager().showPlayerOneLineDialogue("I've fixed all these railings now.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (objectId == PLAIN_RAILING) {
            player.packetSender.sendGameMessage("You search the railing...");
            player.packetSender.sendGameMessage("but find nothing of interest.");
            return true;
        }
        int railingIndex = objectId - FIRST_DAMAGED_RAILING;
        if (DwarfCannonQuest.hasFlag(player, RAILING_BIT_START + railingIndex)) {
            player.packetSender.sendGameMessage("You have already fixed this railing.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(RAILING_ITEM)) {
            player.packetSender.sendGameMessage("You need a railing to repair this.");
            return true;
        }
        player.packetSender.sendGameMessage("You attempt to replace the missing railing...");
        player.getUpdateState().setAnimation(827);
        player.getInventoryManager().removeItem(new ItemStack(RAILING_ITEM, 1));
        DwarfCannonQuest.setFlag(player, RAILING_BIT_START + railingIndex);
        int[] spawn = DwarfCannonQuest.findObjectSpawn(objectId, x, y, player.getPosition().getPlane());
        int orientation = spawn == null ? 0 : spawn[4];
        int type = spawn == null ? 10 : spawn[5];
        ObjectManager.getInstance().removeDynamicObjectAt(x, y, player.getPosition().getPlane(), type);
        new DynamicObject(PLAIN_RAILING, x, y, player.getPosition().getPlane(), orientation, type, ServerSettings.placeholderObjectId, 999999999, false);
        player.packetSender.sendGameMessage("You replace the railing with no problems.");
        if (DwarfCannonQuest.allRailingsFixed(player.getQuestState(QUEST_ID))) {
            player.packetSender.sendGameMessage("You should tell Commander Lawgof that the railings are fixed.");
        }
        return true;
    }

    private static boolean handleWatchTowerLadder(Player player, int x, int y, int progress) {
        player.packetSender.sendGameMessage("You climb up the ladder...");
        if (progress < STATE_CHECK_TOWER) {
            player.packetSender.sendGameMessage("but the trap door will not open.");
            return true;
        }
        player.moveTo(new Position(x, y + 1, player.getPosition().getPlane()));
        if (progress == STATE_CHECK_TOWER && !player.getInventoryManager().containsItem(DWARF_REMAINS)) {
            player.packetSender.sendGameMessage("You find dwarf remains in the tower.");
            player.getInventoryManager().addItem(new ItemStack(DWARF_REMAINS, 1));
        }
        return true;
    }

    private static boolean handleCrate(Player player, int objectId, int progress) {
        if (objectId == CHILD_CRATE && progress == STATE_FIND_CHILD) {
            player.packetSender.sendGameMessage("You search the crate...");
            player.packetSender.sendGameMessage("Inside you see a dwarf child, tied up!");
            player.packetSender.sendGameMessage("You untie the child.");
            if (NpcDefinition.isDefined(DWARF_CHILD) && Npc.findByDefinitionIdAtPosition(DWARF_CHILD, DWARF_CHILD_POSITION) == null) {
                GameplayHelper.spawnNonRespawningNpc(new Npc(DWARF_CHILD), DWARF_CHILD_POSITION.getX(), DWARF_CHILD_POSITION.getY(), DWARF_CHILD_POSITION.getPlane(), 0);
            }
            DialogueManager.continueDialogue(player, DWARF_CHILD, 1, 0);
            return true;
        }
        player.packetSender.sendGameMessage("You search the crate...");
        player.packetSender.sendGameMessage("but it's empty.");
        return true;
    }

    private static boolean handleBrokenCannon(Player player, int progress) {
        if (progress == STATE_FIX_CANNON) {
            player.getDialogueManager().showPlayerOneLineDialogue("I guess I'd better fix it with the toolkit I was given.", 591);
            DwarfCannonQuest.setProgress(player, STATE_INSPECTED_CANNON);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (progress == STATE_INSPECTED_CANNON) {
            if (!player.getInventoryManager().containsItem(TOOLKIT)) {
                player.packetSender.sendGameMessage("You need a tool kit to repair the cannon.");
                return true;
            }
            int part = DwarfCannonQuest.nextUnfixedCannonPart(player.getQuestState(QUEST_ID));
            if (part == -1) {
                DwarfCannonQuest.setProgress(player, STATE_REPAIRED_CANNON);
                player.getDialogueManager().showPlayerTwoLineDialogue("The cannon seems to be in working order.", "Lawgof will be pleased.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            String partName = DwarfCannonQuest.cannonPartName(part);
            player.packetSender.sendGameMessage("You use your tool kit and attempt to fix the " + partName + "...");
            player.getUpdateState().setAnimation(827);
            DwarfCannonQuest.setFlag(player, CANNON_REPAIR_BIT_START + part);
            player.getSkillManager().addExperience(12, 1.2);
            player.packetSender.sendGameMessage("After some tinkering you manage to fix it.");
            if (DwarfCannonQuest.allCannonPartsFixed(player.getQuestState(QUEST_ID))) {
                DwarfCannonQuest.setProgress(player, STATE_REPAIRED_CANNON);
                player.packetSender.sendGameMessage("The cannon seems to be in working order.");
            }
            return true;
        }
        player.packetSender.sendGameMessage("It's a strange dwarf contraption.");
        return true;
    }

    private static boolean makeCannonballs(Player player) {
        if (player.getQuestState(QUEST_ID) != STATE_COMPLETE) {
            player.packetSender.sendGameMessage("You need to complete the Dwarf Cannon quest to make cannonballs.");
            return true;
        }
        if (!player.isMember() || ServerSettings.freeToPlayWorld) {
            player.packetSender.sendGameMessage("You need to be on a members' world to make cannonballs.");
            return true;
        }
        if (player.getSkillManager().getCurrentLevels()[13] < 35) {
            player.packetSender.sendGameMessage("You need a Smithing level of 35 to make cannonballs.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(AMMO_MOULD)) {
            player.packetSender.sendGameMessage("You need a cannonball mould to make cannonballs.");
            return true;
        }
        if (!player.getInventoryManager().containsItem(STEEL_BAR)) {
            player.packetSender.sendGameMessage("You need a steel bar to make cannonballs.");
            return true;
        }
        player.packetSender.sendGameMessage("You heat the steel bar into a liquid state.");
        player.getUpdateState().setAnimation(899);
        player.packetSender.sendGameMessage("You pour the molten metal into your cannonball mould.");
        player.packetSender.sendGameMessage("The molten metal cools slowly to form 4 cannonballs.");
        player.getInventoryManager().removeItem(new ItemStack(STEEL_BAR, 1));
        player.getInventoryManager().addItem(new ItemStack(CANNONBALL, 4));
        player.getSkillManager().addExperience(13, 37.5);
        return true;
    }

    private static void giveMissingNulodionItems(Player player) {
        if (!player.getInventoryManager().containsItem(NULODIONS_NOTES)) {
            player.getInventoryManager().addItem(new ItemStack(NULODIONS_NOTES, 1));
        }
        if (!player.getInventoryManager().containsItem(AMMO_MOULD)) {
            player.getInventoryManager().addItem(new ItemStack(AMMO_MOULD, 1));
        }
    }

    private static void buyFullCannon(Player player) {
        if (!player.getInventoryManager().containsItemAmount(COINS, 750000)) {
            player.getDialogueManager().showNpcOneLineDialogue("Sorry, I can't go any lower than that.", 591);
            return;
        }
        if (player.getInventoryManager().getContainer().getFreeSlots() < 5) {
            player.packetSender.sendGameMessage("You need more inventory space to buy a cannon.");
            return;
        }
        player.getInventoryManager().removeItem(new ItemStack(COINS, 750000));
        player.getInventoryManager().addItem(new ItemStack(CANNON_BASE, 1));
        player.getInventoryManager().addItem(new ItemStack(CANNON_STAND, 1));
        player.getInventoryManager().addItem(new ItemStack(CANNON_BARRELS, 1));
        player.getInventoryManager().addItem(new ItemStack(CANNON_FURNACE, 1));
        player.getInventoryManager().addItem(new ItemStack(AMMO_MOULD, 1));
        player.getInventoryManager().addItem(new ItemStack(INSTRUCTION_MANUAL, 1));
        player.packetSender.sendGameMessage("You give the Cannon Engineer 750,000 coins...");
        player.packetSender.sendGameMessage("He gives you the four cannon parts, an ammo mould and an instruction manual.");
    }

    private static void reclaimLostCannon(Player player) {
        if (DwarfCannonManager.hasActiveCannon(player)) {
            player.getDialogueManager().showNpcTwoLineDialogue("I think you'll find your cannon still happily parked", "on the spot where you put it.", 591);
            return;
        }
        if (DwarfCannonManager.hasLostCannon(player)) {
            if (DwarfCannonManager.reclaimLostCannon(player)) {
                player.getDialogueManager().showNpcOneLineDialogue("Keep that quiet or I'll be in real trouble!", 591);
            }
            return;
        }
        if (DwarfCannonManager.reclaimLostCannon(player)) {
            player.getDialogueManager().showNpcOneLineDialogue("Keep that quiet or I'll be in real trouble!", 591);
            return;
        }
        player.getDialogueManager().showNpcTwoLineDialogue("I'm only allowed to replace cannons that were stolen", "in action. You'll have to buy a new set.", 591);
    }

    private static int getProgress(int state) {
        if (state == STATE_COMPLETE) {
            return STATE_COMPLETE;
        }
        return state & PROGRESS_MASK;
    }

    private static void setProgress(Player player, int progress) {
        if (progress == STATE_COMPLETE) {
            player.setQuestState(QUEST_ID, STATE_COMPLETE);
            return;
        }
        int state = player.getQuestState(QUEST_ID);
        if (state == STATE_COMPLETE) {
            state = 0;
        }
        player.setQuestState(QUEST_ID, state & ~PROGRESS_MASK | progress);
    }

    private static boolean hasFlag(Player player, int bit) {
        return (player.getQuestState(QUEST_ID) & 1 << bit) != 0;
    }

    private static void setFlag(Player player, int bit) {
        player.setQuestState(QUEST_ID, player.getQuestState(QUEST_ID) | 1 << bit);
    }

    private static boolean allRailingsFixed(int state) {
        int n = 0;
        while (n < 6) {
            if ((state & 1 << RAILING_BIT_START + n) == 0) {
                return false;
            }
            ++n;
        }
        return true;
    }

    private static boolean allCannonPartsFixed(int state) {
        return DwarfCannonQuest.nextUnfixedCannonPart(state) == -1;
    }

    private static int nextUnfixedCannonPart(int state) {
        int n = 0;
        while (n < 4) {
            if ((state & 1 << CANNON_REPAIR_BIT_START + n) == 0) {
                return n;
            }
            ++n;
        }
        return -1;
    }

    private static String cannonPartName(int n) {
        if (n == 0) {
            return "pipe";
        }
        if (n == 1) {
            return "barrel";
        }
        if (n == 2) {
            return "axle";
        }
        return "shaft";
    }

    private static boolean isQuestObject(int objectId) {
        return objectId == EMPTY_CRATE || objectId == CHILD_CRATE || objectId == CAVE_ENTRANCE || objectId == NULODION_DOOR || objectId == LAWG0F_SHED_DOOR || objectId == BROKEN_CANNON || objectId == MUD_PILE || objectId == PLAIN_RAILING || objectId >= FIRST_DAMAGED_RAILING && objectId <= LAST_DAMAGED_RAILING;
    }

    private static boolean isWatchTowerLadder(int objectId, int x, int y) {
        return objectId == WATCH_TOWER_LADDER
            && x == WATCH_TOWER_LADDER_POSITION.getX()
            && y == WATCH_TOWER_LADDER_POSITION.getY();
    }

    private static boolean isFurnaceObject(int objectId) {
        int n = 0;
        while (n < FURNACE_OBJECTS.length) {
            if (FURNACE_OBJECTS[n] == objectId) {
                return true;
            }
            ++n;
        }
        return false;
    }

    private static void spawnNpcIfMissingAt(int id, int x, int y, int plane, int walk) {
        if (!NpcDefinition.isDefined(id)) {
            return;
        }
        if (GameplayHelper.isNpcSpawnCoveredByNearbySpawn(id, x, y, plane, 5, NPC_SPAWNS)) {
            return;
        }
        GameplayHelper.spawnNpc(id, x, y, plane, walk);
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

    private static int[] findObjectSpawn(int id, int x, int y, int plane) {
        int n = 0;
        while (n < OBJECT_SPAWNS.length) {
            int[] spawn = OBJECT_SPAWNS[n];
            if (spawn[0] == id && spawn[1] == x && spawn[2] == y && spawn[3] == plane) {
                return spawn;
            }
            ++n;
        }
        return null;
    }
}
