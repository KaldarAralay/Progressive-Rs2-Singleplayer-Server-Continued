/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestScript;
import com.rs2.util.GameUtil;
import com.rs2.util.path.ProjectileCollisionMap;
import com.rs2.util.path.WalkingCollisionMap;

public final class MonksFriendQuest
extends QuestScript {
    public static final int QUEST_ID = 63;
    private static final int STATE_COMPLETE = 1;
    private static final int STATE_FIND_BLANKET = 2;
    private static final int STATE_RETRIEVED_BLANKET = 3;
    private static final int STATE_LOOKING_CEDRIC = 4;
    private static final int STATE_FINDING_WATER = 5;
    private static final int STATE_GIVEN_WATER = 6;
    private static final int STATE_FIXING_CART = 7;
    private static final int STATE_FIXED_CART = 8;
    private static final int BROTHER_OMAD = 279;
    private static final int BROTHER_CEDRIC = 280;
    private static final int ARDOUGNE_MONK = 281;
    private static final int BLANKET_THIEF = 282;
    private static final int HEAD_THIEF = 283;
    private static final int CHILDS_BLANKET = 90;
    private static final int LAW_RUNE = 563;
    private static final int LOGS = 1511;
    private static final int JUG_OF_WATER = 1937;
    private static final int SURFACE_LADDER = 1765;
    private static final int CAVE_UP_LADDER = 1755;
    private static final int MONASTERY_DOOR = 1530;
    private static final int DANCE_ANIMATION = 866;
    private static final Position BLANKET_LADDER_POSITION = new Position(2561, 3222, 0);
    private static final Position BLANKET_CAVE_ENTRY_POSITION = new Position(2561, 9621, 0);
    private static final Position BLANKET_CAVE_UP_LADDER_POSITION = new Position(2561, 9622, 0);
    private static final Position BLANKET_CAVE_EXIT_POSITION = new Position(2561, 3221, 0);
    private static final Position BLANKET_POSITION = new Position(2570, 9604, 0);
    private static final Position BROTHER_OMAD_POSITION = new Position(2604, 3209, 0);
    private static final Position MONASTERY_FRONT_DOOR_POSITION = new Position(2606, 3219, 0);
    private static final int[][] NPC_SPAWNS = new int[][]{
        new int[]{BROTHER_OMAD, 2604, 3209, 0, 3},
        new int[]{BROTHER_CEDRIC, 2614, 3259, 0, 0},
        new int[]{ARDOUGNE_MONK, 2606, 3217, 0, 0},
        new int[]{ARDOUGNE_MONK, 2608, 3209, 0, 0},
        new int[]{ARDOUGNE_MONK, 2618, 3209, 0, 0},
        new int[]{ARDOUGNE_MONK, 2595, 3210, 1, 0},
        new int[]{BLANKET_THIEF, 2564, 9609, 0, 3},
        new int[]{BLANKET_THIEF, 2566, 9605, 0, 3},
        new int[]{HEAD_THIEF, 2569, 9606, 0, 3}
    };
    private static final int[][] OBJECT_SPAWNS = new int[][]{
        new int[]{CAVE_UP_LADDER, 2561, 9622, 0, 0, 10}
    };
    private static final int[][] PARTY_BALLOON_POSITIONS = new int[][]{
        new int[]{2603, 3207},
        new int[]{2605, 3208},
        new int[]{2607, 3210},
        new int[]{2609, 3211},
        new int[]{2605, 3213},
        new int[]{2607, 3216}
    };

    public MonksFriendQuest(int n) {
        super(QUEST_ID);
        this.setQuestPointReward(1);
    }

    public static void spawnMissingContent() {
        int n = 0;
        while (n < NPC_SPAWNS.length) {
            int[] spawn = NPC_SPAWNS[n];
            MonksFriendQuest.spawnNpcIfMissingAt(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
            ++n;
        }
        n = 0;
        while (n < OBJECT_SPAWNS.length) {
            int[] spawn = OBJECT_SPAWNS[n];
            MonksFriendQuest.spawnObjectIfMissing(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], spawn[5]);
            ++n;
        }
        MonksFriendQuest.removeMonasteryFrontDoor();
        MonksFriendQuest.spawnBlanketIfMissing();
    }

    public static boolean isContentAvailable() {
        return NpcDefinition.isDefined(BROTHER_OMAD)
            && NpcDefinition.isDefined(BROTHER_CEDRIC)
            && NpcDefinition.isDefined(ARDOUGNE_MONK)
            && NpcDefinition.isDefined(BLANKET_THIEF)
            && NpcDefinition.isDefined(HEAD_THIEF)
            && GameplayHelper.isObjectDefinitionIdValid(SURFACE_LADDER)
            && GameplayHelper.isObjectDefinitionIdValid(CAVE_UP_LADDER)
            && GameplayHelper.isObjectDefinitionIdValid(MONASTERY_DOOR)
            && ItemDefinition.forId(CHILDS_BLANKET) != null
            && ItemDefinition.forId(JUG_OF_WATER) != null
            && ItemDefinition.forId(LOGS) != null
            && ItemDefinition.forId(LAW_RUNE) != null;
    }

    @Override
    public final boolean refreshQuestJournalStatus(Player player, int n) {
        if (n != STATE_COMPLETE && !MonksFriendQuest.isContentAvailable()) {
            player.packetSender.sendInterfaceTextColor(com.rs2.model.quest.QuestDefinition.forId(this.getQuestId()).getJournalButtonId(), new java.awt.Color(102, 102, 102));
            return true;
        }
        return false;
    }

    @Override
    public final String[] buildQuestJournal(Player player, int n) {
        if (n == STATE_COMPLETE) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "2,000 Woodcutting XP", "8 Law runes"};
        }
        if (!MonksFriendQuest.isContentAvailable()) {
            return new String[]{"Monk's Friend is not available in this cache.", "", "The loaded cache is missing at least one required", "Monk's Friend item, NPC, or object definition."};
        }
        if (n == 0) {
            return new String[]{"I can start this quest by speaking to Brother Omad", "at the monastery south of Ardougne."};
        }
        if (n == STATE_FIND_BLANKET) {
            return new String[]{"Brother Omad cannot sleep because a child is crying.", "I should find the child's blanket in the cave hidden", "under the ring of stones south-west of the monastery."};
        }
        if (n == STATE_RETRIEVED_BLANKET) {
            return new String[]{"I returned the child's blanket to Brother Omad.", "I should talk to him again about the party."};
        }
        if (n == STATE_LOOKING_CEDRIC) {
            return new String[]{"Brother Cedric has gone missing with the party wine.", "I should look for him in the forest nearby."};
        }
        if (n == STATE_FINDING_WATER) {
            return new String[]{"Brother Cedric has had too much wine.", "I should bring him a jug of water."};
        }
        if (n == STATE_GIVEN_WATER) {
            return new String[]{"Brother Cedric is feeling better, but his cart is broken.", "I should talk to him and help repair it."};
        }
        if (n == STATE_FIXING_CART) {
            return new String[]{"Brother Cedric needs wood to fix his broken cart.", "I should bring him some logs."};
        }
        if (n == STATE_FIXED_CART) {
            return new String[]{"Brother Cedric has fixed his cart.", "I should tell Brother Omad that the party wine is safe."};
        }
        return null;
    }

    @Override
    public final void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        Player player2 = player;
        player2.packetSender.sendInterfaceText("1 Quest Point", 12150);
        player2.packetSender.sendInterfaceText("2,000 Woodcutting XP", 12151);
        player2.packetSender.sendInterfaceText("8 Law runes", 12152);
        player2.packetSender.sendInterfaceText("", 12153);
        player2.packetSender.sendInterfaceText("", 12154);
        player2.packetSender.sendInterfaceText("", 12155);
        player.getInventoryManager().addOrDropItem(new ItemStack(LAW_RUNE, 8));
        player.getSkillManager().addQuestExperience(8, 2000.0);
        player2.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, LAW_RUNE);
        player2.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public final boolean handleFirstNpcAction(Player player, int n, int n2) {
        if (n == BROTHER_OMAD || n == BROTHER_CEDRIC || n == ARDOUGNE_MONK) {
            DialogueManager.continueDialogue(player, n, 1, 0);
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleNpcDialogue(Player player, int n, int n2, int n3, int n4) {
        if (n == BROTHER_OMAD) {
            return this.handleBrotherOmadDialogue(player, n2, n3);
        }
        if (n == BROTHER_CEDRIC) {
            return this.handleBrotherCedricDialogue(player, n2, n3);
        }
        if (n == ARDOUGNE_MONK) {
            return this.handleArdougneMonkDialogue(player, n2);
        }
        return false;
    }

    @Override
    public final boolean handleFirstObjectAction(Player player, int n, int n2, int n3, int n4) {
        if (n == SURFACE_LADDER && n2 == BLANKET_LADDER_POSITION.getX() && n3 == BLANKET_LADDER_POSITION.getY()) {
            if (player.getQuestState(this.getQuestId()) == 0) {
                player.packetSender.sendGameMessage("You do not know where this ladder leads.");
                return true;
            }
            player.packetSender.sendGameMessage("You climb down the ladder.");
            player.getUpdateState().setAnimation(828);
            player.moveTo(BLANKET_CAVE_ENTRY_POSITION);
            return true;
        }
        if (n == CAVE_UP_LADDER && n2 == BLANKET_CAVE_UP_LADDER_POSITION.getX() && n3 == BLANKET_CAVE_UP_LADDER_POSITION.getY()) {
            player.packetSender.sendGameMessage("You climb up the ladder.");
            player.getUpdateState().setAnimation(828);
            player.moveTo(BLANKET_CAVE_EXIT_POSITION);
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleMovementStep(Player player, int n) {
        if (player.getQuestState(this.getQuestId()) != 0 && player.getPosition().isWithinDistance(BLANKET_LADDER_POSITION, 2)) {
            MonksFriendQuest.revealBlanketLadder(player, true);
        }
        return false;
    }

    @Override
    public final boolean handleGroundItemInteraction(Player player, int n, int n2) {
        if (n != CHILDS_BLANKET) {
            return false;
        }
        if (!player.getPosition().isWithinDistance(BLANKET_POSITION, 8)) {
            return false;
        }
        if (n2 == STATE_FIND_BLANKET) {
            return false;
        }
        if (n2 == 0) {
            player.packetSender.sendGameMessage("You do not need this blanket.");
        } else {
            player.packetSender.sendGameMessage("You have already recovered the blanket.");
        }
        return true;
    }

    private boolean handleBrotherOmadDialogue(Player player, int n, int n2) {
        int n3 = player.getQuestState(this.getQuestId());
        if (n3 == 0) {
            return this.handleBrotherOmadStartDialogue(player, n, n2);
        }
        if (n3 == STATE_FIND_BLANKET) {
            return this.handleBrotherOmadBlanketDialogue(player, n);
        }
        if (n3 == STATE_RETRIEVED_BLANKET) {
            return this.handleBrotherOmadPartyDialogue(player, n, n2);
        }
        if (n3 == STATE_LOOKING_CEDRIC || n3 == STATE_FINDING_WATER) {
            return this.handleBrotherOmadCedricReminderDialogue(player, n, n3);
        }
        if (n3 == STATE_GIVEN_WATER || n3 == STATE_FIXING_CART) {
            return this.handleBrotherOmadCartReminderDialogue(player, n);
        }
        if (n3 == STATE_FIXED_CART) {
            return this.handleBrotherOmadCompletionDialogue(player, n);
        }
        return this.handleBrotherOmadCompleteDialogue(player, n);
    }

    private boolean handleBrotherOmadStartDialogue(Player player, int n, int n2) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showPlayerOneLineDialogue("Hello there, what's wrong?", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showNpcTwoLineDialogue("Oh, I am so very tired.", "I have not slept in days.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showTwoOptions("Why can't you sleep, what's wrong?", "Sorry, I can't help.");
                return true;
            }
            case 4: {
                if (n2 == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Why can't you sleep, what's wrong?", 591);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Sorry, I can't help.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcThreeLineDialogue("It is my little son.", "The poor child keeps crying, and none of us", "can get any rest.", 591);
                return true;
            }
            case 6: {
                player.getDialogueManager().showPlayerOneLineDialogue("Why is he crying?", 591);
                return true;
            }
            case 7: {
                player.getDialogueManager().showNpcFourLineDialogue("Some thieves stole his favourite blanket.", "They ran into a cave hidden under a ring of stones", "south-west of here.", "Without it, he just will not sleep.", 591);
                return true;
            }
            case 8: {
                player.getDialogueManager().showTwoOptions("Can I help at all?", "I'm sorry, I have no time.");
                return true;
            }
            case 9: {
                if (n2 == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Can I help at all?", 591);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I'm sorry, I have no time.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 10: {
                player.getDialogueManager().showNpcThreeLineDialogue("Please do.", "Find the blanket and bring it back to me.", "Then perhaps this monastery can rest again.", 591);
                this.startQuest(player);
                MonksFriendQuest.revealBlanketLadder(player, true);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherOmadBlanketDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("Have you found the child's blanket yet?", 591);
                return true;
            }
            case 2: {
                if (player.getInventoryManager().containsItem(CHILDS_BLANKET)) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Yes, I have recovered it.", 591);
                    player.getDialogueManager().setNextDialogueStep(4);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("I'm afraid not.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showNpcTwoLineDialogue("Please hurry.", "I need some sleep!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 4: {
                player.getDialogueManager().showItemMessage("You hand the monk the child's blanket.", new ItemStack(CHILDS_BLANKET, 1));
                player.getInventoryManager().removeItem(new ItemStack(CHILDS_BLANKET, 1));
                player.setQuestState(this.getQuestId(), STATE_RETRIEVED_BLANKET);
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcThreeLineDialogue("Thank you, thank you!", "At last we shall get some peace.", "Come back in a moment and I shall thank you properly.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherOmadPartyDialogue(Player player, int n, int n2) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcTwoLineDialogue("Ah, I feel much better now.", "We are arranging a small party to celebrate.", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showPlayerOneLineDialogue("A party?", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showNpcThreeLineDialogue("Yes. Unfortunately Brother Cedric has gone", "missing with the wine.", "This is rather inconvenient.", 591);
                return true;
            }
            case 4: {
                player.getDialogueManager().showTwoOptions("Who's Brother Cedric?", "Enjoy it! I'll see you soon!");
                return true;
            }
            case 5: {
                if (n2 == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Who's Brother Cedric?", 591);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Enjoy it! I'll see you soon!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 6: {
                player.getDialogueManager().showNpcThreeLineDialogue("He is another monk here.", "He was taking the wine from Ardougne, but he", "has not returned.", 591);
                return true;
            }
            case 7: {
                player.getDialogueManager().showNpcOneLineDialogue("Would you look for him?", 591);
                return true;
            }
            case 8: {
                player.getDialogueManager().showThreeOptions("I've no time for that, sorry.", "Where should I look?", "Can I come to the party?");
                return true;
            }
            case 9: {
                if (n2 == 1) {
                    player.getDialogueManager().showPlayerOneLineDialogue("I've no time for that, sorry.", 591);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                if (n2 == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Where should I look?", 591);
                    player.getDialogueManager().setNextDialogueStep(11);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Can I come to the party?", 591);
                player.getDialogueManager().setNextDialogueStep(13);
                return true;
            }
            case 11: {
                player.getDialogueManager().showNpcThreeLineDialogue("He should be somewhere in the forest nearby.", "Please find him and tell him we need that wine", "for the party.", 591);
                player.setQuestState(this.getQuestId(), STATE_LOOKING_CEDRIC);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 13: {
                player.getDialogueManager().showNpcTwoLineDialogue("Of course, but it will be a poor party", "without Brother Cedric and the wine.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherOmadCedricReminderDialogue(Player player, int n, int n2) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("Have you found Brother Cedric yet?", 591);
                return true;
            }
            case 2: {
                if (n2 == STATE_LOOKING_CEDRIC) {
                    player.getDialogueManager().showPlayerOneLineDialogue("No, not yet.", 591);
                    player.getDialogueManager().setNextDialogueStep(4);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("Yes, but he needs some water.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showNpcTwoLineDialogue("Then please help him.", "The party cannot start without him.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 4: {
                player.getDialogueManager().showNpcTwoLineDialogue("Please keep looking.", "He should be somewhere in the nearby forest.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherOmadCartReminderDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("How is Brother Cedric getting on?", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showPlayerOneLineDialogue("His cart is broken.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showNpcTwoLineDialogue("Please help him fix it.", "We need him back with the wine.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherOmadCompletionDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showPlayerOneLineDialogue("Brother Cedric is on his way back.", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showNpcTwoLineDialogue("Excellent!", "Now we can finally have our party.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showItemMessage("Brother Omad gives you 8 law runes.", new ItemStack(LAW_RUNE, 8));
                return true;
            }
            case 4: {
                player.getDialogueManager().showNpcOneLineDialogue("Let the party begin!", 591);
                return true;
            }
            case 5: {
                MonksFriendQuest.startParty(player);
                this.awardCompletionRewards(player);
                player.getDialogueManager().markDialogueInactive();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherOmadCompleteDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcTwoLineDialogue("Thanks again for your help.", "That was quite a party!", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherCedricDialogue(Player player, int n, int n2) {
        int n3 = player.getQuestState(this.getQuestId());
        if (n3 == STATE_LOOKING_CEDRIC) {
            return this.handleBrotherCedricWaterRequestDialogue(player, n);
        }
        if (n3 == STATE_FINDING_WATER) {
            return this.handleBrotherCedricWaterHandoffDialogue(player, n, n2);
        }
        if (n3 == STATE_GIVEN_WATER) {
            return this.handleBrotherCedricCartRequestDialogue(player, n, n2);
        }
        if (n3 == STATE_FIXING_CART) {
            return this.handleBrotherCedricLogsDialogue(player, n);
        }
        if (n3 == STATE_FIXED_CART) {
            return this.handleBrotherCedricFixedDialogue(player, n);
        }
        if (n3 == STATE_COMPLETE) {
            return this.handleBrotherCedricCompleteDialogue(player, n);
        }
        return this.handleBrotherCedricDefaultDialogue(player, n);
    }

    private boolean handleBrotherCedricDefaultDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("Honey, money, woman and wine!", 591);
                player.packetSender.sendGameMessage("The old monk has had too much to drink.");
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherCedricWaterRequestDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showPlayerOneLineDialogue("Brother Cedric, are you okay?", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showNpcTwoLineDialogue("Oh my head...", "I think I have had a little too much wine.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showPlayerOneLineDialogue("Brother Omad needs you back at the monastery.", 591);
                return true;
            }
            case 4: {
                player.getDialogueManager().showNpcThreeLineDialogue("I cannot go anywhere in this state.", "Could you bring me a jug of water?", "That should sober me up.", 591);
                player.setQuestState(this.getQuestId(), STATE_FINDING_WATER);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherCedricWaterHandoffDialogue(Player player, int n, int n2) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("Have you brought me a jug of water?", 591);
                return true;
            }
            case 2: {
                if (player.getInventoryManager().containsItem(JUG_OF_WATER)) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Here you go.", 591);
                    player.getDialogueManager().setNextDialogueStep(4);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("No, not yet.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showNpcTwoLineDialogue("Please hurry.", "I cannot face Brother Omad like this.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 4: {
                player.getDialogueManager().showItemMessage("You give Brother Cedric a jug of water.", new ItemStack(JUG_OF_WATER, 1));
                player.getInventoryManager().removeItem(new ItemStack(JUG_OF_WATER, 1));
                player.setQuestState(this.getQuestId(), STATE_GIVEN_WATER);
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcTwoLineDialogue("Aah! That's better.", "Now I just need to fix this cart.", 591);
                return true;
            }
            case 6: {
                player.getDialogueManager().showNpcOneLineDialogue("Could you help me with that?", 591);
                return true;
            }
            case 7: {
                player.getDialogueManager().showTwoOptions("No, sorry.", "Yes, what do you need?");
                return true;
            }
            case 8: {
                if (n2 == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Yes, what do you need?", 591);
                    player.getDialogueManager().setNextDialogueStep(10);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("No, sorry.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 10: {
                player.getDialogueManager().showNpcTwoLineDialogue("I need some wood to repair the cart.", "Please bring me some logs.", 591);
                player.setQuestState(this.getQuestId(), STATE_FIXING_CART);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherCedricCartRequestDialogue(Player player, int n, int n2) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcTwoLineDialogue("The water helped, but my cart is broken.", "Could you help me repair it?", 591);
                return true;
            }
            case 2: {
                player.getDialogueManager().showTwoOptions("No, sorry.", "Yes, what do you need?");
                return true;
            }
            case 3: {
                if (n2 == 2) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Yes, what do you need?", 591);
                    player.getDialogueManager().setNextDialogueStep(5);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("No, sorry.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcTwoLineDialogue("I need some wood to repair the cart.", "Please bring me some logs.", 591);
                player.setQuestState(this.getQuestId(), STATE_FIXING_CART);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherCedricLogsDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcOneLineDialogue("Have you brought some logs for the cart?", 591);
                return true;
            }
            case 2: {
                if (player.getInventoryManager().containsItem(LOGS)) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Yes, here are some logs.", 591);
                    player.getDialogueManager().setNextDialogueStep(4);
                    return true;
                }
                player.getDialogueManager().showPlayerOneLineDialogue("No, not yet.", 591);
                return true;
            }
            case 3: {
                player.getDialogueManager().showNpcOneLineDialogue("Please bring me some logs.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
            case 4: {
                player.getDialogueManager().showItemMessage("You give Brother Cedric some logs.", new ItemStack(LOGS, 1));
                player.getInventoryManager().removeItem(new ItemStack(LOGS, 1));
                player.setQuestState(this.getQuestId(), STATE_FIXED_CART);
                return true;
            }
            case 5: {
                player.getDialogueManager().showNpcTwoLineDialogue("Well done.", "I can fix the cart now.", 591);
                return true;
            }
            case 6: {
                player.getDialogueManager().showPlayerOneLineDialogue("I'll go and tell Brother Omad.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherCedricFixedDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcTwoLineDialogue("I am almost ready to return.", "Please tell Brother Omad the wine is safe.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleBrotherCedricCompleteDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                player.getDialogueManager().showNpcTwoLineDialogue("Brother Omad sends you his thanks!", "He will not be in a fit state to thank you in person.", 591);
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private boolean handleArdougneMonkDialogue(Player player, int n) {
        switch (n) {
            case 1: {
                int n2 = player.getQuestState(this.getQuestId());
                if (n2 == 0) {
                    player.getDialogueManager().showNpcOneLineDialogue("Peace brother.", 591);
                } else if (n2 == STATE_COMPLETE) {
                    player.getDialogueManager().showNpcOneLineDialogue("What a fine party!", 591);
                } else {
                    player.getDialogueManager().showNpcOneLineDialogue("*yawn*", 591);
                }
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    private static void revealBlanketLadder(Player player, boolean bl) {
        if (!player.getPosition().isWithinDistance(BLANKET_LADDER_POSITION, 2)) {
            return;
        }
        if (ObjectManager.findDynamicObjectByIdAt(SURFACE_LADDER, BLANKET_LADDER_POSITION.getX(), BLANKET_LADDER_POSITION.getY(), BLANKET_LADDER_POSITION.getPlane()) != null) {
            return;
        }
        new DynamicObject(SURFACE_LADDER, BLANKET_LADDER_POSITION.getX(), BLANKET_LADDER_POSITION.getY(), BLANKET_LADDER_POSITION.getPlane(), 1, 10, ServerSettings.placeholderObjectId, 200, false);
        if (bl) {
            player.packetSender.sendGameMessage("A ladder mysteriously appears.");
        }
    }

    private static void startParty(Player player) {
        int n = 0;
        while (n < PARTY_BALLOON_POSITIONS.length) {
            int[] pos = PARTY_BALLOON_POSITIONS[n];
            if (ObjectManager.findDynamicObjectAt(pos[0], pos[1], 0) == null) {
                new DynamicObject(115 + n % 8, pos[0], pos[1], 0, 0, 10, ServerSettings.placeholderObjectId, (int)GameUtil.secondsToTicks(120L), false);
            }
            ++n;
        }
        player.getUpdateState().setAnimation(DANCE_ANIMATION);
        player.getUpdateState().setForcedTextAndMarkUpdated("Woop! Party!");
        Npc npc = Npc.findByDefinitionIdAtPosition(BROTHER_OMAD, BROTHER_OMAD_POSITION);
        if (npc != null) {
            npc.getUpdateState().setAnimation(DANCE_ANIMATION);
            npc.getUpdateState().setForcedTextAndMarkUpdated("Woop! Party!");
        }
        Npc[] npcArray = World.getNpcs();
        n = 0;
        while (n < npcArray.length) {
            npc = npcArray[n];
            if (npc != null && npc.getDefinition().getId() == ARDOUGNE_MONK && npc.getPosition().isWithinDistance(BROTHER_OMAD_POSITION, 10)) {
                npc.getUpdateState().setAnimation(DANCE_ANIMATION);
                npc.getUpdateState().setForcedTextAndMarkUpdated("Party!");
            }
            ++n;
        }
    }

    private static void spawnBlanketIfMissing() {
        GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(CHILDS_BLANKET, 1), BLANKET_POSITION, (int)GameUtil.secondsToTicks(50L), true));
    }

    private static void removeMonasteryFrontDoor() {
        int n = MONASTERY_FRONT_DOOR_POSITION.getX();
        int n2 = MONASTERY_FRONT_DOOR_POSITION.getY();
        int n3 = MONASTERY_FRONT_DOOR_POSITION.getPlane();
        if (ObjectManager.findDynamicObjectAt(n, n2, n3) == null) {
            LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(MONASTERY_DOOR, n, n2, n3);
            if (loadedWorldObject != null) {
                new DynamicObject(ServerSettings.placeholderObjectId, n, n2, n3, loadedWorldObject.getOrientation(), loadedWorldObject.getType(), ServerSettings.placeholderObjectId, 999999999, false);
            }
        }
        WorldObjectLookup.removeObjectByIdAt(MONASTERY_DOOR, n, n2, n3);
        int n4 = n2 - 1;
        while (n4 <= n2 + 1) {
            WalkingCollisionMap.clearTileCollision(n, n4, n3);
            ProjectileCollisionMap.clearTileCollision(n, n4, n3);
            ++n4;
        }
    }

    private static void spawnNpcIfMissingAt(int n, int n2, int n3, int n4, int n5) {
        if (!NpcDefinition.isDefined(n)) {
            return;
        }
        if (GameplayHelper.isNpcSpawnCoveredByNearbySpawn(n, n2, n3, n4, 5, NPC_SPAWNS)) {
            return;
        }
        GameplayHelper.spawnNpc(n, n2, n3, n4, n5);
    }

    private static void spawnObjectIfMissing(int n, int n2, int n3, int n4, int n5, int n6) {
        if (!GameplayHelper.isObjectDefinitionIdValid(n)) {
            return;
        }
        if (ObjectManager.findDynamicObjectAt(n2, n3, n4) != null) {
            return;
        }
        if (WorldObjectLookup.findObjectByIdAt(n, n2, n3, n4) != null) {
            return;
        }
        new DynamicObject(n, n2, n3, n4, n5, n6, ServerSettings.placeholderObjectId, 999999999, false);
    }
}
