/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestHook;
import com.rs2.model.quest.QuestScript;
import com.rs2.model.quest.impl.FluffsInteractionHintTask;
import com.rs2.model.quest.impl.FluffsKittenReunionStartTask;
import com.rs2.model.quest.impl.GertrudeRewardFoodTask;
import com.rs2.model.quest.impl.KittenCrateSearchTask;
import com.rs2.model.task.TickTask;

public final class GertrudesCatQuest
extends QuestScript {
    public GertrudesCatQuest(int n) {
        super(42);
        super.setQuestPointReward(1);
    }

    @Override
    public final String[] buildQuestJournal(Player player, int n) {
        if (n == 0) {
            return new String[]{"I can start this quest by speaking to Gertrude.", "She can be found to the west of Varrock."};
        }
        if (n == 2) {
            return new String[]{"I should go talk to the boys who can be found", "in the Varrock marketplace."};
        }
        if (n == 3) {
            return new String[]{"The boys told me to go to the abandoned lumber", "mill just beyond Jolly Boar Inn."};
        }
        if (n == 4) {
            return new String[]{"Maybe I should bring Fluffs something to eat."};
        }
        if (n == 5) {
            return new String[]{"I should go look for a kitten."};
        }
        if (n == 6) {
            return new String[]{"I should now go talk to Gertrude."};
        }
        if (n == 1) {
            return new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "A kitten!", "1525 Cooking XP", "A chocolate cake", "A bowl of stew.", "Raise cats."};
        }
        return null;
    }

    @Override
    public final void awardCompletionRewards(Player player) {
        Object object = player;
        ((Player)object).packetSender.sendInterfacePosition(12145, 60, 130);
        super.markQuestComplete(player);
        Player player2 = player;
        object = player2;
        object = this;
        player2.packetSender.sendInterfaceText("You have completed " + QuestDefinition.forId(((QuestHook)object).getQuestId()).getName() + "!", 12144);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("1 Quest Point", 12150);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("A kitten!", 12151);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("1525 Cooking XP", 12152);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("A chocolate cake", 12153);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("A bowl of stew.", 12154);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("Raise cats.", 12155);
        player.getSkillManager().addQuestExperience(7, 1525.0);
        object = player;
        ((Player)object).packetSender.sendMusicJingle(238, 320);
        object = player;
        ((Player)object).packetSender.sendInterfaceText("" + player.getQuestPoints(), 12147);
        object = player;
        ((Player)object).packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, 1561);
        object = player;
        ((Player)object).packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        object = player;
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public final boolean handleFirstObjectAction(Player player, int n, int n2, int n3, int n4) {
        if (n == 2618 && n2 == 3305 && n3 == 3493) {
            Player player2 = player;
            player2.packetSender.queueRelativeMovementStep(0, player.getPosition().getY() < 3493 ? 2 : -2, true);
            player.getUpdateState().setAnimation(882);
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleFirstNpcAction(Player object, int n, int n2) {
        Object object2;
        if (n == 759) {
            object2 = Npc.findByDefinitionId(759);
            if (n2 >= 3) {
                ((Entity)object2).getUpdateState().setForcedText("Hisss!");
                ((Entity)object).getUpdateState().setForcedText("Ouch!");
                ((Player)object).setActionLocked(true);
                World.getTaskScheduler().schedule(new FluffsInteractionHintTask(this, 2, n2, (Player)object));
                return true;
            }
        }
        if (n == 767 && n2 == 5 && !((Player)object).ownsItem(1554)) {
            Player player = object;
            player.packetSender.sendGameMessage("You search the crate.");
            ((Player)object).setActionLocked(true);
            World.getTaskScheduler().schedule(new KittenCrateSearchTask(this, 3, (Player)object));
        }
        return false;
    }

    @Override
    public final boolean handleItemOnNpc(Player object, int n, int n2, int n3) {
        Npc npc;
        if (n2 == 1927 && n == 759) {
            npc = Npc.findByDefinitionId(759);
            if (n3 == 3) {
                ((Player)object).getInventoryManager().replaceItem(new ItemStack(1927, 1), new ItemStack(1925, 1));
                npc.getUpdateState().setForcedText("Mew!");
                ((Player)object).setQuestState(this.getQuestId(), 4);
                return true;
            }
        }
        if (n2 == 1552 && n == 759) {
            npc = Npc.findByDefinitionId(759);
            if (n3 == 4) {
                ((Player)object).getInventoryManager().removeItem(new ItemStack(1552, 1));
                npc.getUpdateState().setForcedText("Mew!");
                ((Player)object).setQuestState(this.getQuestId(), 5);
                return true;
            }
        }
        if (n2 == 1554 && n == 759) {
            npc = Npc.findByDefinitionId(759);
            if (n3 == 5) {
                ((Player)object).getInventoryManager().removeItem(new ItemStack(1554, 1));
                ((Entity)object).getUpdateState().setAnimation(827);
                Npc npc2 = new Npc(760);
                GameplayHelper.spawnOwnedNpcAtPosition((Player)object, ((Entity)object).getPosition(), npc2, false, false);
                npc.getUpdateState().setForcedText("Purr...");
                npc2.getUpdateState().setForcedText("Purr...");
                ((Player)object).setQuestState(this.getQuestId(), 6);
                ((Player)object).setActionLocked(true);
                World.getTaskScheduler().schedule(new FluffsKittenReunionStartTask(this, 2, npc2, npc, (Player)object));
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean handleItemOnItem(Player player, int n, int n2, int n3) {
        if (n3 != 0 && (n == 1573 && n2 == 327 || n == 327 && n2 == 1573)) {
            player.getInventoryManager().removeItem(new ItemStack(n, 1));
            player.getInventoryManager().removeItem(new ItemStack(n2, 1));
            player.getInventoryManager().addItem(new ItemStack(1552, 1));
            player.getDialogueManager().showOneLineStatement("You rub the doogle leaves over the sardine.");
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleNpcDialogue(Player object, int n, int n2, int n3, int n4) {
        if (n == 780) {
            if (n4 == 0) {
                if (n2 == 1) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Hello, are you ok?", 591);
                    return true;
                }
                if (n2 == 2) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("Do I look ok? Those kids drive me crazy.", 591);
                    return true;
                }
                if (n2 == 3) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("I'm sorry. It's just that I've lost her.", 591);
                    return true;
                }
                if (n2 == 4) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Lost who?", 591);
                    return true;
                }
                if (n2 == 5) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("Fluffs, poor Fluffs. She never hurt anyone.", 591);
                    return true;
                }
                if (n2 == 6) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Who's Fluffs?", 591);
                    return true;
                }
                if (n2 == 7) {
                    ((Player)object).getDialogueManager().showNpcThreeLineDialogue("My beloved feline friend Fluffs. She's been purring by", "my side for almost a decade. Please, could you go", "search for her while I look over the kids?", 591);
                    return true;
                }
                if (n2 == 8) {
                    ((Player)object).getDialogueManager().showThreeOptions("Well, I suppose I could.", "What's in it for me?", "Sorry, I'm too busy to play pet rescue.");
                    return true;
                }
                if (n2 == 9) {
                    if (n3 == 1) {
                        ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Well, I suppose I could.", 591);
                        ((Player)object).getDialogueManager().setNextDialogueStep(10);
                        return true;
                    }
                    ((Player)object).getDialogueManager().finishDialogue();
                    return false;
                }
                if (n2 == 10) {
                    ((Player)object).getDialogueManager().showNpcTwoLineDialogue("Really? Thank you so much! I really have no idea", "where she could be!", 591);
                    return true;
                }
                if (n2 == 11) {
                    ((Player)object).getDialogueManager().showNpcTwoLineDialogue("I think my sons, Shilop and Wilough, saw the cat last.", "They'll be out in the market place.", 591);
                    return true;
                }
                if (n2 == 12) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Alright then, I'll see what I can do.", 591);
                    this.startQuest((Player)object);
                    ((Player)object).getDialogueManager().finishDialogue();
                    return true;
                }
            }
            if (n4 == 6) {
                if (n2 == 1) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Hello Gertrude. Fluffs ran off with her kitten.", 591);
                    return true;
                }
                if (n2 == 2) {
                    ((Player)object).getDialogueManager().showNpcThreeLineDialogue("You're back! Thank you! Thank you! Fluffs just came", "back! I think she was just upset as she couldn't find her", "kitten.", 591);
                    return true;
                }
                if (n2 == 3) {
                    ((Player)object).getDialogueManager().showOneLineStatement("Gertrude gives you a hug.");
                    return true;
                }
                if (n2 == 4) {
                    ((Player)object).getDialogueManager().showNpcTwoLineDialogue("If you hadn't found her kitten it would have died out", "there!", 591);
                    return true;
                }
                if (n2 == 5) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("That's ok, I like to do my bit.", 591);
                    return true;
                }
                if (n2 == 6) {
                    ((Player)object).getDialogueManager().showNpcThreeLineDialogue("I don't know how to thank you. I have no real material", "possessions. I do have kittens! I can only really look", "after one.", 591);
                    return true;
                }
                if (n2 == 7) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Well, if it needs a home.", 591);
                    return true;
                }
                if (n2 == 8) {
                    ((Player)object).getDialogueManager().showNpcTwoLineDialogue("I would sell it to my cousin in West Ardougne. I hear", "there's a rat epidemic there. But it's too far.", 591);
                    return true;
                }
                if (n2 == 9) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("Here you go, look after her and thank you again!", 591);
                    return true;
                }
                if (n2 == 10) {
                    ((Player)object).getDialogueManager().showNpcThreeLineDialogue("Oh by the way, the kitten can live in your backpack,", "but to make it grow you must take it out and feed and", "stroke it often.", 591);
                    return true;
                }
                if (n2 == 11) {
                    ((Player)object).getDialogueManager().showOneLineStatement("Gertrude gives you a kitten.");
                    return true;
                }
                if (n2 == 12) {
                    ((Player)object).getDialogueManager().finishDialogue();
                    ((Player)object).getPetManager().spawnQuestCatFollower(1561, 768);
                    ((Player)object).setActionLocked(true);
                    World.getTaskScheduler().schedule(new GertrudeRewardFoodTask(this, 5, (Player)object));
                    return false;
                }
            }
        }
        if (n == 783) {
            if (n4 == 2) {
                if (n2 == 1) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Hello there, I've been looking for you.", 591);
                    return true;
                }
                if (n2 == 2) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("I didn't mean to take it! I just forgot to pay.", 591);
                    return true;
                }
                if (n2 == 3) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("What? I'm trying to help your mum find Fluffs.", 591);
                    return true;
                }
                if (n2 == 4) {
                    ((Player)object).getDialogueManager().showNpcThreeLineDialogue("Ohh...well, in that case I might be able to help. Fluffs", "followed me to my secret play area, I haven't seen her", "since.", 591);
                    return true;
                }
                if (n2 == 5) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Where is this play area?", 591);
                    return true;
                }
                if (n2 == 6) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("If I told you that, it wouldn't be a secret.", 591);
                    return true;
                }
                if (n2 == 7) {
                    ((Player)object).getDialogueManager().showThreeOptions("Tell me sonny, or I will hurt you.", "What will make you tell me?", "Well never mind, it's Fluffs' loss.");
                    return true;
                }
                if (n2 == 8) {
                    if (n3 == 2) {
                        ((Player)object).getDialogueManager().showPlayerOneLineDialogue("What will make you tell me?", 591);
                        ((Player)object).getDialogueManager().setNextDialogueStep(9);
                        return true;
                    }
                    ((Player)object).getDialogueManager().finishDialogue();
                    return false;
                }
                if (n2 == 9) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("Well...now you ask, I am a bit short on cash.", 591);
                    return true;
                }
                if (n2 == 10) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("How much?", 591);
                    return true;
                }
                if (n2 == 11) {
                    ((Player)object).getDialogueManager().showNpcOneLineDialogue("100 coins should cover it.", 591);
                    return true;
                }
                if (n2 == 12) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("100 coins! Why should I pay you?", 591);
                    return true;
                }
                if (n2 == 13) {
                    ((Player)object).getDialogueManager().showNpcTwoLineDialogue("You shouldn't, but I won't help otherwise. I never liked", "that cat anyway, so what do you say?", 591);
                    return true;
                }
                if (n2 == 14) {
                    ((Player)object).getDialogueManager().showTwoOptions("I'm not paying you a penny.", "Okay then, I'll pay.");
                    return true;
                }
                if (n2 == 15) {
                    if (n3 == 2) {
                        ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Okay then, I'll pay.", 591);
                        ((Player)object).getDialogueManager().setNextDialogueStep(16);
                        return true;
                    }
                    ((Player)object).getDialogueManager().finishDialogue();
                    return false;
                }
                if (n2 == 16 && ((Player)object).getInventoryManager().containsItemAmount(995, 100)) {
                    ((Player)object).getInventoryManager().removeItem(new ItemStack(995, 100));
                    ((Player)object).getDialogueManager().showItemMessage("You give the lad 100 coins.", new ItemStack(995, 100));
                    ((Player)object).setQuestState(this.getQuestId(), 3);
                    return true;
                }
            }
            if (n4 == 3) {
                if (n2 == 17) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("There you go, now where did you see Fluffs?", 591);
                    ((Player)object).getDialogueManager().setNextDialogueStep(1);
                    return true;
                }
                if (n2 == 1) {
                    ((Player)object).getDialogueManager().showNpcThreeLineDialogue("I play at an abandoned lumber mill to the north east.", "Just beyond the Jolly Boar Inn. I saw Fluffs running", "around in there.", 591);
                    return true;
                }
                if (n2 == 2) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Anything else?", 591);
                    return true;
                }
                if (n2 == 3) {
                    ((Player)object).getDialogueManager().showNpcTwoLineDialogue("Well, you'll have to find the broken fence to get in. I'm", "sure you can manage that.", 591);
                    ((Player)object).getDialogueManager().finishDialogue();
                    return true;
                }
            }
        }
        return false;
    }
}

