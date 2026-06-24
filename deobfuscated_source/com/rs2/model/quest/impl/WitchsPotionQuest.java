/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestScript;

public final class WitchsPotionQuest
extends QuestScript {
    public WitchsPotionQuest(int n) {
        super(18);
        super.setQuestPointReward(1);
    }

    @Override
    public final String[] buildQuestJournal(Player player, int n) {
        if (n == 0) {
            return new String[]{"I can start this quest by speaking to Hetty in her house in", "Rimmington, West of Port Sarim"};
        }
        if (n == 2) {
            return new String[]{"I should bring these items to Hetty at her house in", "Rimmington, West of Port Sarim:", "Eye of newt", "Rat's tail", "Onion", "Burnt meat"};
        }
        if (n == 3) {
            return new String[]{"I should drink from the cauldron at Hetty's house to", "finish this quest."};
        }
        if (n == 1) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "325 Magic XP"};
        }
        return null;
    }

    @Override
    public final void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        Player player2 = player;
        player2.packetSender.sendInterfaceText("1 Quest Point", 12150);
        player2 = player;
        player2.packetSender.sendInterfaceText("325 Magic XP", 12151);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12152);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12153);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12154);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12155);
        player.getSkillManager().addQuestExperience(6, 325.0);
        player2 = player;
        player2.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, 221);
        player2 = player;
        player2.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player2 = player;
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public final boolean handleNpcDeathDrop(Player object, int n, Position position, int n2) {
        if (n == 47 && n2 == 2) {
            GroundItem groundItem = new GroundItem(new ItemStack(300, 1), (Entity)object, position);
            GroundItemManager.getInstance().spawn(groundItem);
            return true;
        }
        return false;
    }

    private static boolean hasAnyPotionIngredient(Player player) {
        return player.getInventoryManager().containsItem(221) || player.getInventoryManager().containsItem(300) || player.getInventoryManager().containsItem(1957) || player.getInventoryManager().containsItem(2146);
    }

    private static boolean hasAllPotionIngredients(Player player) {
        return player.getInventoryManager().containsItem(221) && player.getInventoryManager().containsItem(300) && player.getInventoryManager().containsItem(1957) && player.getInventoryManager().containsItem(2146);
    }

    @Override
    public final boolean handleContextDialogue(int n, Player player, int n2, int n3, int n4, int n5, int n6, int n7) {
        if (n2 == 2024 && n5 == 2967 && n6 == 3205 && n == 1 && n7 == 3) {
            if (n3 == 1) {
                player.getDialogueManager().showTwoLineStatement("You drink from the cauldron, it tastes horrible! You feel yourself", "imbued with power.");
                return true;
            }
            if (n3 == 2) {
                this.awardCompletionRewards(player);
                player.getDialogueManager().markDialogueInactive();
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean handleNpcDialogue(Player player, int n, int n2, int n3, int n4) {
        if (n == 307) {
            if (n4 == 0) {
                if (n2 == 1) {
                    player.getDialogueManager().showNpcOneLineDialogue("What could you want with an old woman like me?", 588);
                    return true;
                }
                if (n2 == 2) {
                    player.getDialogueManager().showTwoOptions("I am in search of a quest.", "I've heard that you are a witch.");
                    return true;
                }
                if (n2 == 3) {
                    if (n3 == 1) {
                        player.getDialogueManager().showPlayerOneLineDialogue("I am in search of a quest.", 591);
                        player.getDialogueManager().setNextDialogueStep(4);
                        return true;
                    }
                    player.getDialogueManager().showOneLineStatement("This option is currently missing...");
                    player.getDialogueManager().setNextDialogueStep(2);
                    return true;
                }
                if (n2 == 4) {
                    player.getDialogueManager().showNpcOneLineDialogue("Hmmm... Maybe I can think of something for you.", 588);
                    return true;
                }
                if (n2 == 5) {
                    player.getDialogueManager().showNpcTwoLineDialogue("Would you like to become more proficient in the dark", "arts?", 590);
                    return true;
                }
                if (n2 == 6) {
                    player.getDialogueManager().showThreeOptions("Yes help me become one with my darker side.", "No I have my principles and honour.", "What, you mean improve my magic?");
                    return true;
                }
                if (n2 == 7) {
                    if (n3 == 3) {
                        player.getDialogueManager().showPlayerOneLineDialogue("What, you mean improve my magic?", 591);
                        player.getDialogueManager().setNextDialogueStep(8);
                        return true;
                    }
                    player.getDialogueManager().showOneLineStatement("This option is currently missing...");
                    player.getDialogueManager().setNextDialogueStep(6);
                    return true;
                }
                if (n2 == 8) {
                    player.getDialogueManager().showOneLineStatement("The witch sighs.");
                    return true;
                }
                if (n2 == 9) {
                    player.getDialogueManager().showNpcOneLineDialogue("Yes, improve your magic...", 591);
                    return true;
                }
                if (n2 == 10) {
                    player.getDialogueManager().showNpcOneLineDialogue("Do you have no sense of drama?", 591);
                    return true;
                }
                if (n2 == 11) {
                    player.getDialogueManager().showThreeOptions("Yes I'd like to improve my magic.", "No I'm not interested.", "Show me the mysteries of the dark arts...");
                    return true;
                }
                if (n2 == 12) {
                    if (n3 == 1) {
                        player.getDialogueManager().showPlayerOneLineDialogue("Yes I'd like to improve my magic.", 591);
                        this.startQuest(player);
                        player.getDialogueManager().setNextDialogueStep(10);
                        return true;
                    }
                    if (n3 == 3) {
                        player.getDialogueManager().showOneLineStatement("This option is currently missing...");
                        player.getDialogueManager().setNextDialogueStep(11);
                        return true;
                    }
                    player.getDialogueManager().finishDialogue();
                    return false;
                }
            }
            if (n4 == 2) {
                if (n2 == 10) {
                    player.getDialogueManager().showOneLineStatement("The witch sighs.");
                    return true;
                }
                if (n2 == 11) {
                    player.getDialogueManager().showNpcTwoLineDialogue("Ok I'm going to make a potion to help bring out your", "darker self.", 591);
                    return true;
                }
                if (n2 == 12) {
                    player.getDialogueManager().showNpcOneLineDialogue("You will need certain ingredients.", 591);
                    return true;
                }
                if (n2 == 13) {
                    player.getDialogueManager().showPlayerOneLineDialogue("What do I need?", 591);
                    return true;
                }
                if (n2 == 14) {
                    player.getDialogueManager().showNpcTwoLineDialogue("You need an eye of newt, a rat's tail, an onion... Oh", "and a piece of burnt meat.", 591);
                    return true;
                }
                if (n2 == 15) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Great, I'll go and get them.", 591);
                    return true;
                }
                if (n2 == 1) {
                    player.getDialogueManager().showNpcOneLineDialogue("So have you found the things for the potion?", 591);
                    return true;
                }
                if (n2 == 2) {
                    if (!WitchsPotionQuest.hasAnyPotionIngredient(player)) {
                        player.getDialogueManager().showPlayerOneLineDialogue("No, I have none of them yet.", 591);
                        player.getDialogueManager().finishDialogue();
                    }
                    if (WitchsPotionQuest.hasAnyPotionIngredient(player) && !WitchsPotionQuest.hasAllPotionIngredients(player)) {
                        player.getDialogueManager().showPlayerOneLineDialogue("I have found some of the things you asked for.", 591);
                        player.getDialogueManager().setNextDialogueStep(3);
                    }
                    if (WitchsPotionQuest.hasAllPotionIngredients(player)) {
                        player.getDialogueManager().showPlayerOneLineDialogue("Yes I have everything!", 591);
                        player.getDialogueManager().setNextDialogueStep(4);
                    }
                    return true;
                }
                if (n2 == 3) {
                    player.getDialogueManager().showNpcOneLineDialogue("Great, but I'll need the other ingredients as well.", 591);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
                if (n2 == 4) {
                    player.getDialogueManager().showNpcOneLineDialogue("Excellent, can I have them then?", 591);
                    return true;
                }
                if (n2 == 5) {
                    player.getDialogueManager().showThreeLineStatement("You pass the ingredients to Hetty and she puts them all into her", "cauldron. Hetty closes her eyes and begins to chant. The cauldron", "bubbles mysteriously.");
                    return true;
                }
                if (n2 == 6) {
                    player.getDialogueManager().showPlayerOneLineDialogue("Well, is it ready?", 591);
                    return true;
                }
                if (n2 == 7 && WitchsPotionQuest.hasAllPotionIngredients(player)) {
                    player.getInventoryManager().removeItem(new ItemStack(221, 1));
                    player.getInventoryManager().removeItem(new ItemStack(300, 1));
                    player.getInventoryManager().removeItem(new ItemStack(1957, 1));
                    player.getInventoryManager().removeItem(new ItemStack(2146, 1));
                    player.getDialogueManager().showNpcOneLineDialogue("Ok, now drink from the cauldron.", 591);
                    player.setQuestState(this.getQuestId(), 3);
                    player.getDialogueManager().finishDialogue();
                    return true;
                }
            }
        }
        return false;
    }
}

