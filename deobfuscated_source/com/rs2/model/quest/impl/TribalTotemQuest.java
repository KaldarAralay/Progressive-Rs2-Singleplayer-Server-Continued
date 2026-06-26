/*
 * Tribal Totem quest content, matched against the local 2004 base scripts.
 */
package com.rs2.model.quest.impl;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.objects.functions.DoorHandler;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestScript;
import java.awt.Color;

public final class TribalTotemQuest
extends QuestScript {
    public static final int QUEST_ID = 96;
    private static final int STATE_COMPLETE = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_CRATE_MARKED = 3;
    private static final int STATE_CRATE_DELIVERED = 4;
    private static final int STATE_TELEPORTED = 5;
    private static final int THIEVING = 17;
    private static final int GUIDE_BOOK = 1856;
    private static final int TRIBAL_TOTEM = 1857;
    private static final int ADDRESS_LABEL = 1858;
    private static final int SWORDFISH = 373;
    private static final int RPDT_EMPLOYEE = 843;
    private static final int WIZARD_CROMPERTY = 844;
    private static final int HORACIO = 845;
    private static final int KANGAI_MAU = 846;
    private static final int COMBO_DOOR = 2705;
    private static final int TRIBAL_TOTEM_DOOR = 2706;
    private static final int HANDELMORT_FRONT_DOOR_X = 2635;
    private static final int HANDELMORT_FRONT_DOOR_Y = 3321;
    private static final int HORN_CRATE = 2707;
    private static final int TELEPORT_CRATE = 2708;
    private static final int SHUT_CHEST = 2709;
    private static final int OPEN_CHEST = 2710;
    private static final int TRAP_STAIRS = 2711;
    private static final int LOCK_INTERFACE = 716;
    private static final int[] LOCK_TEXT_COMPONENTS = new int[]{760, 761, 762, 763};
    private static final int[] LOCK_DOWN_BUTTONS = new int[]{764, 766, 768, 770};
    private static final int[] LOCK_UP_BUTTONS = new int[]{765, 767, 769, 771};
    private static final int LOCK_ENTER_BUTTON = 774;
    private static final int FLAG_LOCK_SOLVED = 1;
    private static final int FLAG_STAIRS_TRAP_KNOWN = 1 << 21;
    private static final int LOCK_VALUE_MASK = 31;
    private static final int LOCK_VALUE_BITS = 5;
    private static final String[] LETTERS = new String[]{
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    private static final int[] CORRECT_COMBINATION = new int[]{10, 20, 17, 19};
    private static final Position RPDT_TELEPORT_POSITION = new Position(2649, 3271, 0);
    private static final Position MANSION_TELEPORT_POSITION = new Position(2638, 3321, 0);
    private static final Position STAIRS_SUCCESS_POSITION = new Position(2631, 3321, 1);
    private static final Position STAIRS_FAIL_POSITION = new Position(2640, 9719, 0);
    private static final int[][] NPC_SPAWNS = new int[][]{
        {KANGAI_MAU, 2791, 3182, 0, 2},
        {HORACIO, 2635, 3311, 0, 2},
        {RPDT_EMPLOYEE, 2644, 3274, 0, 0},
        {RPDT_EMPLOYEE, 2644, 3276, 0, 0},
        {RPDT_EMPLOYEE, 2647, 3271, 0, 0},
        {WIZARD_CROMPERTY, 2683, 3326, 0, 0}
    };

    public TribalTotemQuest(int n) {
        super(QUEST_ID);
        this.setQuestPointReward(1);
    }

    public static void spawnMissingContent() {
        int n = 0;
        while (n < NPC_SPAWNS.length) {
            int[] spawn = NPC_SPAWNS[n];
            TribalTotemQuest.spawnNpcIfMissingAt(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
            ++n;
        }
    }

    public static boolean isContentAvailable() {
        return NpcDefinition.isDefined(RPDT_EMPLOYEE)
            && NpcDefinition.isDefined(WIZARD_CROMPERTY)
            && NpcDefinition.isDefined(HORACIO)
            && NpcDefinition.isDefined(KANGAI_MAU)
            && ItemDefinition.forId(GUIDE_BOOK) != null
            && ItemDefinition.forId(TRIBAL_TOTEM) != null
            && ItemDefinition.forId(ADDRESS_LABEL) != null
            && ItemDefinition.forId(SWORDFISH) != null
            && GameplayHelper.isObjectDefinitionIdValid(COMBO_DOOR)
            && GameplayHelper.isObjectDefinitionIdValid(TRIBAL_TOTEM_DOOR)
            && GameplayHelper.isObjectDefinitionIdValid(HORN_CRATE)
            && GameplayHelper.isObjectDefinitionIdValid(TELEPORT_CRATE)
            && GameplayHelper.isObjectDefinitionIdValid(SHUT_CHEST)
            && GameplayHelper.isObjectDefinitionIdValid(OPEN_CHEST)
            && GameplayHelper.isObjectDefinitionIdValid(TRAP_STAIRS)
            && InterfaceDefinition.interfaceCount > LOCK_ENTER_BUTTON;
    }

    @Override
    public boolean refreshQuestJournalStatus(Player player, int n) {
        if (n != STATE_COMPLETE && !TribalTotemQuest.isContentAvailable()) {
            player.packetSender.sendInterfaceTextColor(QuestDefinition.forId(this.getQuestId()).getJournalButtonId(), new Color(102, 102, 102));
            return true;
        }
        return false;
    }

    @Override
    public String[] buildQuestJournal(Player player, int n) {
        if (n == STATE_COMPLETE) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "1,775 Thieving XP", "5 Swordfish"};
        }
        if (!TribalTotemQuest.isContentAvailable()) {
            return new String[]{"Tribal Totem is not available in this cache.", "", "The loaded cache is missing at least one required", "Tribal Totem item, NPC, object, or interface definition."};
        }
        if (n == 0) {
            return new String[]{"I can start this quest by speaking to Kangai Mau", "in Brimhaven."};
        }
        if (n == STATE_STARTED) {
            return new String[]{"Kangai Mau asked me to recover the Rantuki", "tribal totem from Lord Handelmort's mansion", "in Ardougne."};
        }
        if (n == STATE_CRATE_MARKED) {
            return new String[]{"I replaced Cromperty's crate label with", "Handelmort's address. I should ask the RPDT", "to deliver the crate."};
        }
        if (n == STATE_CRATE_DELIVERED) {
            return new String[]{"The RPDT has delivered the marked crate.", "I should ask Wizard Cromperty to teleport me", "to his other teleportation block."};
        }
        if (n == STATE_TELEPORTED) {
            if (!player.getInventoryManager().containsItemInInventoryOrBank(TRIBAL_TOTEM)) {
                return new String[]{"Cromperty teleported me into Lord Handelmort's", "mansion. I still need to find the Rantuki", "tribal totem and bring it back to Kangai Mau."};
            }
            return new String[]{"I recovered the tribal totem from", "Handelmort's mansion. I should return it to", "Kangai Mau in Brimhaven."};
        }
        return null;
    }

    @Override
    public void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        player.packetSender.sendInterfaceText("1 Quest Point", 12150);
        player.packetSender.sendInterfaceText("1,775 Thieving XP", 12151);
        player.packetSender.sendInterfaceText("5 Swordfish", 12152);
        player.packetSender.sendInterfaceText("", 12153);
        player.packetSender.sendInterfaceText("", 12154);
        player.packetSender.sendInterfaceText("", 12155);
        player.getSkillManager().addQuestExperience(THIEVING, 1775.0);
        player.getInventoryManager().addOrDropItem(new ItemStack(SWORDFISH, 5));
        player.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, TRIBAL_TOTEM);
        player.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public boolean handleFirstNpcAction(Player player, int n, int n2) {
        if (n == KANGAI_MAU || n == HORACIO || n == RPDT_EMPLOYEE || n == WIZARD_CROMPERTY) {
            DialogueManager.continueDialogue(player, n, 1, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcDialogue(Player player, int npcId, int step, int option, int state) {
        if (npcId == KANGAI_MAU) {
            return this.handleKangaiMauDialogue(player, step, option);
        }
        if (npcId == HORACIO) {
            return this.handleHoracioDialogue(player, step, option);
        }
        if (npcId == RPDT_EMPLOYEE) {
            return this.handleRpdtEmployeeDialogue(player, step, option);
        }
        if (npcId == WIZARD_CROMPERTY) {
            return this.handleWizardCrompertyDialogue(player, step, option);
        }
        return false;
    }

    @Override
    public boolean handleFirstObjectAction(Player player, int objectId, int x, int y, int state) {
        int plane = player.getPosition().getPlane();
        if (objectId == COMBO_DOOR) {
            if (!TribalTotemQuest.isLockSolved(player)) {
                TribalTotemQuest.openCombinationInterface(player);
                return true;
            }
            return DoorHandler.handleDoor(player, objectId, x, y, plane);
        }
        if (objectId == TRIBAL_TOTEM_DOOR) {
            if (!TribalTotemQuest.isHandelmortFrontDoor(x, y, plane) || player.getPosition().getY() > y) {
                return DoorHandler.handleDoor(player, objectId, x, y, plane);
            }
            player.packetSender.sendGameMessage("This door is securely locked.");
            return true;
        }
        if (objectId == TRAP_STAIRS) {
            TribalTotemQuest.handleTrapStairsClimb(player);
            return true;
        }
        if (objectId == SHUT_CHEST) {
            TribalTotemQuest.changeChestObject(SHUT_CHEST, OPEN_CHEST, x, y, plane, 300);
            player.packetSender.sendGameMessage("You open the chest.");
            return true;
        }
        if (objectId == OPEN_CHEST) {
            TribalTotemQuest.searchOpenChest(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleSecondObjectAction(Player player, int objectId, int x, int y, int state) {
        int questState = player.getQuestState(this.getQuestId());
        if (objectId == HORN_CRATE) {
            TribalTotemQuest.handleHornCrateSearch(player, questState);
            return true;
        }
        if (objectId == TELEPORT_CRATE) {
            TribalTotemQuest.readTeleportCrateLabel(player, questState);
            return true;
        }
        if (objectId == TRAP_STAIRS) {
            TribalTotemQuest.searchTrapStairs(player);
            return true;
        }
        if (objectId == OPEN_CHEST) {
            TribalTotemQuest.restoreDynamicObjectAt(x, y, player.getPosition().getPlane());
            player.packetSender.sendGameMessage("You close the chest.");
            return true;
        }
        return false;
    }

    @Override
    public boolean handleThirdObjectAction(Player player, int objectId, int x, int y, int state) {
        if (objectId == TRAP_STAIRS) {
            TribalTotemQuest.searchTrapStairs(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, int itemId, int objectId, int state) {
        if (itemId == ADDRESS_LABEL && objectId == TELEPORT_CRATE) {
            if (state >= STATE_CRATE_MARKED && state != STATE_COMPLETE) {
                player.packetSender.sendGameMessage("You have already replaced the delivery address label.");
                return true;
            }
            if (state == 0 || state == STATE_COMPLETE) {
                player.packetSender.sendGameMessage("Nothing interesting happens.");
                return true;
            }
            if (player.getInventoryManager().removeItem(new ItemStack(ADDRESS_LABEL, 1))) {
                player.packetSender.sendGameMessage("You carefully place the delivery label over the existing label, covering it completely.");
                player.setQuestState(this.getQuestId(), STATE_CRATE_MARKED);
                player.getDialogueManager().showPlayerOneLineDialogue("Now I just need someone to deliver it for me.", 591);
                player.getDialogueManager().finishDialogue();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleInventoryItemFirstOption(Player player, int interfaceId, int itemId, int state) {
        if (itemId == GUIDE_BOOK) {
            player.getDialogueManager().showFourLineStatement("The guide book lists many interesting sights", "around Ardougne. Lord Handelmort's mansion", "is described as a large guarded house in", "north-west East Ardougne.");
            return true;
        }
        return false;
    }

    @Override
    public boolean handleButtonClick(Player player, int buttonId, int state) {
        if (player.getOpenInterfaceId() != LOCK_INTERFACE) {
            return false;
        }
        int n = 0;
        while (n < LOCK_DOWN_BUTTONS.length) {
            if (buttonId == LOCK_DOWN_BUTTONS[n]) {
                TribalTotemQuest.adjustLockValue(player, n, -1);
                return true;
            }
            if (buttonId == LOCK_UP_BUTTONS[n]) {
                TribalTotemQuest.adjustLockValue(player, n, 1);
                return true;
            }
            ++n;
        }
        if (buttonId == LOCK_ENTER_BUTTON) {
            TribalTotemQuest.submitCombination(player);
            return true;
        }
        return false;
    }

    private boolean handleKangaiMauDialogue(Player player, int step, int option) {
        int state = player.getQuestState(this.getQuestId());
        if (!TribalTotemQuest.isContentAvailable()) {
            if (step == 1) {
                player.getDialogueManager().showNpcTwoLineDialogue("The spirits are quiet today.", "Perhaps this cache is missing what I need.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (state == 0) {
            return this.handleKangaiMauStartDialogue(player, step, option);
        }
        if (state == STATE_COMPLETE) {
            if (step == 1) {
                player.getDialogueManager().showNpcOneLineDialogue("Many greetings esteemed thief.", 591);
                return true;
            }
            if (step == 2) {
                player.getDialogueManager().showPlayerOneLineDialogue("Hey.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            return false;
        }
        if (step == 1) {
            player.getDialogueManager().showNpcOneLineDialogue("Have you got our totem back?", 591);
            return true;
        }
        if (step == 2) {
            if (state == STATE_TELEPORTED && player.getInventoryManager().containsItem(TRIBAL_TOTEM)) {
                player.getDialogueManager().showPlayerOneLineDialogue("Yes, I have it here.", 591);
                player.getDialogueManager().setNextDialogueStep(5);
                return true;
            }
            player.getDialogueManager().showPlayerOneLineDialogue("No, not yet.", 591);
            return true;
        }
        if (step == 3) {
            player.getDialogueManager().showNpcOneLineDialogue("Bah, you no good.", 591);
            player.getDialogueManager().finishDialogue();
            return true;
        }
        if (step == 5) {
            player.getDialogueManager().showItemMessage("You give Kangai Mau the tribal totem.", new ItemStack(TRIBAL_TOTEM, 1));
            player.getInventoryManager().removeItem(new ItemStack(TRIBAL_TOTEM, 1));
            return true;
        }
        if (step == 6) {
            player.getDialogueManager().showNpcTwoLineDialogue("Thank you. You have returned", "the Rantuki tribe's honour.", 591);
            player.getDialogueManager().finishDialogue();
            this.awardCompletionRewards(player);
            return true;
        }
        return false;
    }

    private boolean handleKangaiMauStartDialogue(Player player, int step, int option) {
        switch (step) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("Hello. I Kangai Mau of the Rantuki tribe.", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showThreeOptions("And what are you doing in Brimhaven?", "I'm in search of adventure!", "Who are the Rantuki tribe?");
                return true;
            }
            case 3: {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("And what are you doing in Brimhaven?", 591);
                    player.getDialogueManager().setNextDialogueStep(5);
                    return true;
                }
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("I'm in search of adventure!", 591);
                    player.getDialogueManager().setNextDialogueStep(5);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Who are the Rantuki tribe?", 591);
                player.getDialogueManager().setNextDialogueStep(13);
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcFourLineDialogue("I looking for someone brave", "to go on important mission for me.", "Someone skilled in thievery and sneaking about.", "I am told I can find such people in Brimhaven.", 591);
                return true;
            }
            case 6: {
                player.getDialogueManager().showPlayerOneLineDialogue("What sort of mission?", 591);
                return true;
            }
            case 7: {
                player.getDialogueManager().showNpcFourLineDialogue("I need someone to go on a mission", "to the city of Ardougne.", "There you will find the house of Lord Handelmort.", "In his house he has our tribal totem.", 591);
                return true;
            }
            case 8: {
                player.getDialogueManager().showNpcOneLineDialogue("We need it back.", 591);
                return true;
            }
            case 9: {
                player.getDialogueManager().showThreeOptions("Ok, I will get it back.", "Why does he have it?", "How can I find Handelmort's house?");
                return true;
            }
            case 10: {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Ok, I will get it back.", 591);
                    this.startQuest(player);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Why does he have it?", 591);
                    player.getDialogueManager().setNextDialogueStep(15);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("How can I find Handelmort's house?", 591);
                player.getDialogueManager().setNextDialogueStep(17);
                return true;
            }
            case 13: {
                player.getDialogueManager().showNpcTwoLineDialogue("We are proud people from far away.", "Lord Handelmort stole our sacred totem.", 591);
                player.getDialogueManager().setNextDialogueStep(5);
                return true;
            }
            case 15: {
                player.getDialogueManager().showNpcFourLineDialogue("Lord Handelmort is an Ardougnese explorer", "which means he think he have the right", "to come to my tribal home, steal our stuff", "and put in his private museum.", 591);
                player.getDialogueManager().setNextDialogueStep(9);
                return true;
            }
            case 17: {
                player.getDialogueManager().showPlayerOneLineDialogue("Ardougne IS a big place...", 591);
                return true;
            }
            case 18: {
                player.getDialogueManager().showNpcOneLineDialogue("I don't know Ardougne. You tell me.", 591);
                player.getDialogueManager().setNextDialogueStep(9);
                return true;
            }
        }
        return false;
    }

    private boolean handleHoracioDialogue(Player player, int step, int option) {
        int state = player.getQuestState(this.getQuestId());
        switch (step) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("It's a fine day to be out in the garden isn't it?", 591);
                return true;
            }
            case 2: {
                if (state >= STATE_STARTED && state != STATE_COMPLETE) {
                    player.getDialogueManager().showFourOptions("Yes, it's very nice.", "So... who are you?", "So... do you garden round the back too?", "Do you need any help?");
                } else {
                    player.getDialogueManager().showTwoOptions("Yes, it's very nice.", "So... who are you?");
                }
                return true;
            }
            case 3: {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Yes, it's very nice.", 591);
                    player.getDialogueManager().setNextDialogueStep(5);
                    return true;
                }
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("So... who are you?", 591);
                    player.getDialogueManager().setNextDialogueStep(7);
                    return true;
                }
                if (option == 3) {
                    player.getDialogueManager().showPlayerOneLineDialogue("So... do you garden round the back too?", 591);
                    player.getDialogueManager().setNextDialogueStep(10);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Do you need any help?", 591);
                player.getDialogueManager().setNextDialogueStep(15);
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcOneLineDialogue("Days like these make me glad to be alive!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 7: {
                player.getDialogueManager().showNpcFourLineDialogue("My name is Horacio Dobson.", "I'm the gardener to Lord Handelmort.", "Take a look around this beautiful garden,", "all of this is my handiwork.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 10: {
                player.getDialogueManager().showNpcOneLineDialogue("That I do!", 591);
                return true;
            }
            case 11: {
                player.getDialogueManager().showPlayerTwoLineDialogue("Doesn't all of the security around the house", "get in your way then?", 591);
                return true;
            }
            case 12: {
                player.getDialogueManager().showNpcFourLineDialogue("Ah, I'm used to all that. I have", "my keys, the guard dogs know me, and", "I know the combination to the door lock.", "It's rather easy, it's his middle name.", 591);
                return true;
            }
            case 13: {
                player.getDialogueManager().showPlayerOneLineDialogue("Whose middle name?", 591);
                return true;
            }
            case 14: {
                player.getDialogueManager().showNpcTwoLineDialogue("Hum. I probably shouldn't have said that.", "Forget I mentioned it.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 15: {
                player.getDialogueManager().showNpcTwoLineDialogue("Trying to muscle in on my job eh?", "I'm more than happy to do this all by myself!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleRpdtEmployeeDialogue(Player player, int step, int option) {
        int state = player.getQuestState(this.getQuestId());
        switch (step) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("Welcome to RPDT!", 591);
                return true;
            }
            case 2: {
                if (state == STATE_CRATE_MARKED) {
                    player.getDialogueManager().showTwoOptions("So, when are you going to deliver this crate?", "Thank you, it's interesting in here.");
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Thank you very much.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 3: {
                if (state == STATE_CRATE_MARKED && option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("So, when are you going to deliver this crate?", 591);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Thank you, it's interesting in here.", 591);
                player.getDialogueManager().setNextDialogueStep(6);
                return true;
            }
            case 4: {
                player.getDialogueManager().showNpcOneLineDialogue("Well... I guess we could do it now...", 591);
                player.setQuestState(this.getQuestId(), STATE_CRATE_DELIVERED);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 6: {
                player.getDialogueManager().showNpcOneLineDialogue("We're the premier delivery service in ALL of RuneScape!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleWizardCrompertyDialogue(Player player, int step, int option) {
        switch (step) {
            case 1: {
                player.getDialogueManager().showNpcThreeLineDialogue("Hello there.", "My name is Cromperty.", "I am a Wizard, and an inventor.", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showTwoOptions("Two jobs? That's got to be tough.", "So what have you invented?");
                return true;
            }
            case 3: {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Two jobs? That's got to be tough.", 591);
                    player.getDialogueManager().setNextDialogueStep(5);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("So what have you invented?", 591);
                player.getDialogueManager().setNextDialogueStep(8);
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcTwoLineDialogue("Not when you combine them it isn't!", "I invent MAGIC things!", 591);
                return true;
            }
            case 6: {
                player.getDialogueManager().showTwoOptions("So what have you invented?", "Well, I shall leave you to your inventing.");
                return true;
            }
            case 7: {
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Well, I shall leave you to your inventing.", 591);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("So what have you invented?", 591);
                return true;
            }
            case 8: {
                player.getDialogueManager().showNpcFourLineDialogue("Ah! My latest invention is my patent pending", "teleportation block! It emits a low level", "magical signal, that will allow me to locate it", "anywhere in the world, and teleport anything", 591);
                return true;
            }
            case 9: {
                player.getDialogueManager().showNpcFourLineDialogue("directly to it! I hope to revolutionise", "the entire teleportation system!", "Don't you think I'm great?", "Uh, I mean it's great?", 591);
                return true;
            }
            case 10: {
                player.getDialogueManager().showThreeOptions("So where is the other block?", "Can I be teleported please?", "Well done, that's very clever.");
                return true;
            }
            case 11: {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("So where is the other block?", 591);
                    player.getDialogueManager().setNextDialogueStep(13);
                    return true;
                }
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Can I be teleported please?", 591);
                    player.getDialogueManager().setNextDialogueStep(16);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Well done, that's very clever.", 591);
                player.getDialogueManager().setNextDialogueStep(20);
                return true;
            }
            case 13: {
                player.getDialogueManager().showNpcFourLineDialogue("It is somewhere between here", "and the Wizards' Tower by now.", "I sent it by RPDT and am waiting", "for their delivery to arrive.", 591);
                return true;
            }
            case 14: {
                player.getDialogueManager().showThreeOptions("Can I be teleported please?", "What is the RPDT?", "Well, I shall leave you to your inventing.");
                return true;
            }
            case 15: {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Can I be teleported please?", 591);
                    player.getDialogueManager().setNextDialogueStep(16);
                    return true;
                }
                if (option == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("What is the RPDT?", 591);
                    player.getDialogueManager().setNextDialogueStep(22);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Well, I shall leave you to your inventing.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 16: {
                player.getDialogueManager().showNpcFourLineDialogue("By all means! I'm afraid I can't give", "you any specifics as to where you will come", "out however. Presumably wherever the other", "block is located.", 591);
                return true;
            }
            case 17: {
                player.getDialogueManager().showTwoOptions("Yes, that sounds good. Teleport me!", "That sounds dangerous. Leave me here.");
                return true;
            }
            case 18: {
                if (option == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Yes, that sounds good. Teleport me!", 591);
                    player.getDialogueManager().setNextDialogueStep(24);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("That sounds dangerous. Leave me here.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 20: {
                player.getDialogueManager().showNpcOneLineDialogue("Yes, yes it is, isn't it?", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 22: {
                player.getDialogueManager().showNpcTwoLineDialogue("They're the Ranged Parcel Delivery Team.", "They deliver parcels around RuneScape.", 591);
                player.getDialogueManager().setNextDialogueStep(14);
                return true;
            }
            case 24: {
                player.getDialogueManager().showNpcOneLineDialogue("Okey dokey! Ready?", 591);
                return true;
            }
            case 25: {
                player.getDialogueManager().finishDialogue();
                TribalTotemQuest.performCrompertyTeleport(player, this.getQuestId());
                return true;
            }
        }
        return false;
    }

    private static void handleHornCrateSearch(Player player, int state) {
        if (state >= STATE_STARTED && state != STATE_COMPLETE && !player.getInventoryManager().containsItem(ADDRESS_LABEL)) {
            if (player.getInventoryManager().addItem(new ItemStack(ADDRESS_LABEL, 1))) {
                player.packetSender.sendGameMessage("There is a label on this crate. It says; To Lord Handelmort, Handelmort Mansion, Ardougne.");
                player.packetSender.sendGameMessage("You carefully peel it off and take it.");
            }
        } else if (player.getInventoryManager().containsItem(ADDRESS_LABEL)) {
            player.packetSender.sendGameMessage("You can see the gluey outline from where you peeled the address label off.");
        }
        player.packetSender.sendGameMessage("This crate is securely fastened shut.");
    }

    private static void readTeleportCrateLabel(Player player, int state) {
        player.packetSender.sendGameMessage("There is a label on this crate. It says;");
        if (state >= STATE_CRATE_MARKED && state != STATE_COMPLETE) {
            player.packetSender.sendGameMessage("To Lord Handelmort, Handelmort Mansion, Ardougne.");
        }
        player.packetSender.sendGameMessage("Senior Patents Clerk, Chamber of Invention, The Wizards' Tower, Misthalin.");
        player.packetSender.sendGameMessage("The crate is securely fastened shut and ready for delivery.");
    }

    private static void performCrompertyTeleport(Player player, int questId) {
        int state = player.getQuestState(questId);
        if (state == STATE_COMPLETE) {
            player.getDialogueManager().showNpcOneLineDialogue("Hmm.... that's odd... I can't seem to get a signal...", 591);
            player.getDialogueManager().finishDialogue();
            return;
        }
        player.packetSender.sendGameMessage("Dipsolum sententa sententi!");
        player.resetInteractionState();
        player.setActionLocked(false);
        player.npcTransformationId = -1;
        player.setAppearanceUpdateRequired(true);
        if (state >= STATE_CRATE_DELIVERED) {
            if (state == STATE_CRATE_DELIVERED) {
                player.setQuestState(questId, STATE_TELEPORTED);
            }
            player.moveTo(MANSION_TELEPORT_POSITION);
            player.setActionLocked(false);
            player.npcTransformationId = -1;
            player.setAppearanceUpdateRequired(true);
            return;
        }
        player.moveTo(RPDT_TELEPORT_POSITION);
        player.setActionLocked(false);
        player.npcTransformationId = -1;
        player.setAppearanceUpdateRequired(true);
    }

    private static void handleTrapStairsClimb(Player player) {
        if (TribalTotemQuest.isStairsTrapKnown(player)) {
            player.packetSender.sendGameMessage("You climb up the stairs.");
            player.getUpdateState().setAnimation(828);
            player.moveTo(STAIRS_SUCCESS_POSITION);
            return;
        }
        player.packetSender.sendGameMessage("As you climb the stairs you hear a click...");
        player.packetSender.sendGameMessage("You have fallen through a trap!");
        int currentHitpoints = Math.max(1, player.getCurrentHitpoints());
        int damage = currentHitpoints * 20 / 100 + 1;
        player.applyDirectHit(damage, HitType.NORMAL);
        player.moveTo(STAIRS_FAIL_POSITION);
    }

    private static void searchTrapStairs(Player player) {
        if (player.getSkillManager().getCurrentLevels()[THIEVING] < 21) {
            player.packetSender.sendGameMessage("You don't find anything interesting");
            return;
        }
        TribalTotemQuest.setStairsTrapKnown(player);
        player.getDialogueManager().showFourLineStatement("Your trained senses as a thief enable you", "to see that there is a trap in these stairs.", "You make a note of its location for", "future reference when using these stairs.");
        player.getDialogueManager().finishDialogue();
    }

    private static void searchOpenChest(Player player) {
        if (!player.getInventoryManager().containsItemInInventoryOrBank(TRIBAL_TOTEM)) {
            player.getDialogueManager().showItemMessage("Inside the chest you find the tribal totem.", new ItemStack(TRIBAL_TOTEM, 1));
            player.getInventoryManager().addOrDropItem(new ItemStack(TRIBAL_TOTEM, 1));
            return;
        }
        player.packetSender.sendGameMessage("The chest is empty.");
    }

    private static void openCombinationInterface(Player player) {
        TribalTotemQuest.clearLockValues(player);
        int n = 0;
        while (n < 4) {
            TribalTotemQuest.sendLockLetter(player, n);
            ++n;
        }
        player.packetSender.showInterface(LOCK_INTERFACE);
    }

    private static void adjustLockValue(Player player, int index, int delta) {
        int value = TribalTotemQuest.getLockValue(player, index);
        value += delta;
        if (value < 0) {
            value = LETTERS.length - 1;
        } else if (value >= LETTERS.length) {
            value = 0;
        }
        TribalTotemQuest.setLockValue(player, index, value);
        TribalTotemQuest.sendLockLetter(player, index);
    }

    private static void submitCombination(Player player) {
        player.packetSender.closeInterfaces();
        int n = 0;
        while (n < CORRECT_COMBINATION.length) {
            if (TribalTotemQuest.getLockValue(player, n) != CORRECT_COMBINATION[n]) {
                player.packetSender.sendGameMessage("This combination is incorrect.");
                return;
            }
            ++n;
        }
        TribalTotemQuest.setLockSolved(player);
        player.packetSender.sendGameMessage("The combination seems correct!");
    }

    private static void sendLockLetter(Player player, int index) {
        player.packetSender.sendInterfaceText(LETTERS[TribalTotemQuest.getLockValue(player, index)], LOCK_TEXT_COMPONENTS[index]);
    }

    private static int getLockValue(Player player, int index) {
        int shift = 1 + index * LOCK_VALUE_BITS;
        return player.questProgressFlags[QUEST_ID] >> shift & LOCK_VALUE_MASK;
    }

    private static void setLockValue(Player player, int index, int value) {
        int shift = 1 + index * LOCK_VALUE_BITS;
        int mask = LOCK_VALUE_MASK << shift;
        player.questProgressFlags[QUEST_ID] = player.questProgressFlags[QUEST_ID] & ~mask | (value & LOCK_VALUE_MASK) << shift;
    }

    private static void clearLockValues(Player player) {
        int mask = (1 << 21) - 1;
        player.questProgressFlags[QUEST_ID] &= ~mask;
    }

    private static boolean isLockSolved(Player player) {
        return (player.questProgressFlags[QUEST_ID] & FLAG_LOCK_SOLVED) != 0;
    }

    private static void setLockSolved(Player player) {
        player.questProgressFlags[QUEST_ID] |= FLAG_LOCK_SOLVED;
    }

    private static boolean isStairsTrapKnown(Player player) {
        return (player.questProgressFlags[QUEST_ID] & FLAG_STAIRS_TRAP_KNOWN) != 0;
    }

    private static void setStairsTrapKnown(Player player) {
        player.questProgressFlags[QUEST_ID] |= FLAG_STAIRS_TRAP_KNOWN;
    }

    private static boolean isHandelmortFrontDoor(int x, int y, int plane) {
        return plane == 0 && x == HANDELMORT_FRONT_DOOR_X && y == HANDELMORT_FRONT_DOOR_Y;
    }

    private static void changeChestObject(int currentId, int replacementId, int x, int y, int plane, int ticks) {
        int orientation = 0;
        int type = 10;
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(x, y, plane);
        if (dynamicObject != null) {
            WorldObject worldObject = dynamicObject.getWorldObject();
            orientation = worldObject.getOrientation();
            type = worldObject.getType();
        } else {
            LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(currentId, x, y, plane);
            if (loadedWorldObject != null) {
                orientation = loadedWorldObject.getOrientation();
                type = loadedWorldObject.getType();
            }
        }
        new DynamicObject(replacementId, x, y, plane, orientation, type, currentId, ticks, false);
    }

    private static void restoreDynamicObjectAt(int x, int y, int plane) {
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(x, y, plane);
        if (dynamicObject != null) {
            ObjectManager.getInstance().removeDynamicObjectAt(x, y, plane, dynamicObject.getWorldObject().getType());
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
    }
}
