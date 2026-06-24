/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.CacheCoordinateTranslator;
import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestScript;
import com.rs2.model.quest.impl.LostCityZanarisEntryCompletionTask;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.task.TickTask;

public final class LostCityQuest
extends QuestScript {
    public LostCityQuest(int n) {
        super(58);
        super.setQuestPointReward(3);
    }

    @Override
    public final String[] buildQuestJournal(Player player, int n) {
        if (n == 0) {
            n = player.getSkillManager().getBaseLevel(12);
            int n2 = player.getSkillManager().getBaseLevel(8);
            String[] stringArray = new String[]{"I can start this quest by speaking to the Adventurers in", "the Swamp just south of Lumbridge.", "To complete this quest I need:", String.valueOf(n >= 31 ? "@str@" : "") + "Level 31 Crafting", String.valueOf(n2 >= 36 ? "@str@" : "") + "Level 36 Woodcutting", "and be able to defeat a Level 101 Spirit without weapons"};
            return stringArray;
        }
        if (n == 2) {
            String[] stringArray = new String[]{"I should search for leprechaun hiding in the", "trees next to the swamp."};
            return stringArray;
        }
        if (n == 3) {
            String[] stringArray = new String[]{"I should go to cave located in Entrana and", "look for a dramen tree, to make a dramen staff."};
            return stringArray;
        }
        if (n == 4) {
            String[] stringArray = new String[]{"I should now try entering the shed in Lumbridge", "swamp while wearing the dramen staff."};
            return stringArray;
        }
        if (n == 1) {
            String[] stringArray = new String[]{"Quest Completed!", "", "You were awarded:", "3 Quest Points", "Access to Zanaris"};
            return stringArray;
        }
        return null;
    }

    @Override
    public final void awardCompletionRewards(Player player) {
        super.markQuestComplete(player);
        super.showQuestCompleteInterface(player);
        Player player2 = player;
        player2.packetSender.sendInterfaceText("3 Quest Points", 12150);
        player2 = player;
        player2.packetSender.sendInterfaceText("Access to Zanaris", 12151);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12152);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12153);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12154);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12155);
        player2 = player;
        player2.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, 772);
        player2 = player;
        player2.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player2 = player;
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public final boolean handleFirstObjectAction(Player object, int n, int n2, int n3, int n4) {
        if (n == 2409 && n2 == 3138 && n3 == 3212 && n4 == 2) {
            if (Npc.findByDefinitionId(654) == null) {
                GameplayHelper.spawnRoamingNpcFacingPlayer((Player)object, new Npc(654), 3138, 3211, 0, -1, false, false);
            }
            return true;
        }
        if (n == 1292 && n2 == 2860 && n3 == 9734 && n4 == 3) {
            Object object2 = ItemCombinationHandler.findUsableGatheringTool((Player)object, 8);
            if (object2 == null) {
                object2 = object;
                ((Player)object2).packetSender.sendGameMessage("You do not have an axe which you have the woodcutting level to use.");
                return true;
            }
            if (((Player)object).getSkillManager().getCurrentLevels()[8] < 36) {
                object2 = object;
                ((Player)object2).packetSender.sendGameMessage("You need a Woodcutting level of 36 to cut this tree.");
                return true;
            }
            object2 = "You must defeat me before touching the tree!";
            if (!GameplayHelper.hasActiveTemporaryNpc((Player)object, 655)) {
                Npc npc = new Npc(655);
                GameplayHelper.spawnOwnedGroundPlaneNpcAtPosition((Player)object, npc, 2860, 9737, 0, -1, true, false);
                npc.getUpdateState().setForcedText((String)object2);
            }
            return true;
        }
        if (n == 2406 && n2 == 3202 && n3 == 3169) {
            if (!(n4 != 4 && n4 != 1 || ((Player)object).getEquipmentManager().getItemIdAtSlot(3) != 772 || ServerSettings.freeToPlayWorld)) {
                Object object3 = !CacheCoordinateTranslator.dungeonCoordinateShiftActive ? new Position(2452, 4473, 0) : new Position(3220, 9593, 0);
                if (((Player)object).getTeleportManager().castItemTeleport((Position)object3) && n4 == 4) {
                    object3 = object;
                    ((Player)object3).packetSender.sendGameMessage("The world starts to shimmer...");
                    World.getTaskScheduler().schedule(new LostCityZanarisEntryCompletionTask(this, 4, (Player)object));
                }
            } else {
                Player player = object;
                player.packetSender.sendGameMessage("The door seems to be locked.");
            }
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleNpcKill(Player player, int n, int n2) {
        if (n == 655 && n2 == 3) {
            player.getDialogueManager().showOneLineStatement("With the Tree Spirit defeated you can now chop the tree.");
            player.setQuestState(this.getQuestId(), 4);
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleNpcDialogue(Player entity, int n, int n2, int n3, int n4) {
        if (n == 650 && n4 == 0) {
            if (n2 == 1) {
                ((Player)entity).getDialogueManager().showNpcOneLineDialogue("Hello there traveller.", 591);
                return true;
            }
            if (n2 == 2) {
                ((Player)entity).getDialogueManager().showTwoOptions("What are you camped out here for?", "Do you know any good adventures I can go on?");
                return true;
            }
            if (n2 == 3) {
                if (n3 == 1) {
                    ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("What are you camped here for?", 591);
                    ((Player)entity).getDialogueManager().setNextDialogueStep(4);
                    return true;
                }
                ((Player)entity).getDialogueManager().finishDialogue();
                return false;
            }
            if (n2 == 4) {
                ((Player)entity).getDialogueManager().showNpcTwoLineDialogue("We're looking for Zanaris...GAH! I mean we're not", "here for any particular reason at all.", 591);
                return true;
            }
            if (n2 == 5) {
                ((Player)entity).getDialogueManager().showThreeOptions("Who's Zanaris?", "What's Zanaris?", "What makes you think it's out here?");
                return true;
            }
            if (n2 == 6) {
                if (n3 == 1) {
                    ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("Who's Zanaris?", 591);
                    ((Player)entity).getDialogueManager().setNextDialogueStep(7);
                    return true;
                }
                ((Player)entity).getDialogueManager().finishDialogue();
                return false;
            }
            if (n2 == 7) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Ahahahaha! Zanaris isn't a person! It's a magical hidden", "city filled with treasures and rich.. uh, nothing. It's", "nothing.", 591);
                return true;
            }
            if (n2 == 8) {
                ((Player)entity).getDialogueManager().showTwoOptions("If it's hidden how are you planning to find it?", "There's no such thing.");
                return true;
            }
            if (n2 == 9) {
                if (n3 == 1) {
                    ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("If it's hidden how are you planning to find it?", 591);
                    ((Player)entity).getDialogueManager().setNextDialogueStep(10);
                    return true;
                }
                ((Player)entity).getDialogueManager().finishDialogue();
                return false;
            }
            if (n2 == 10) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Well, we don't want to tell anyone else about that,", "because we don't want anyone else sharing in all that", "glory and treasure.", 591);
                return true;
            }
            if (n2 == 11) {
                ((Player)entity).getDialogueManager().showTwoOptions("Please tell me.", "Looks like you don't know either.");
                return true;
            }
            if (n2 == 12) {
                if (n3 == 2) {
                    ((Player)entity).getDialogueManager().showPlayerTwoLineDialogue("Well, it looks to me like YOU don't know EITHER", "seeing as you're all just sat around here.", 591);
                    ((Player)entity).getDialogueManager().setNextDialogueStep(13);
                    return true;
                }
                ((Player)entity).getDialogueManager().finishDialogue();
                return false;
            }
            if (n2 == 13) {
                ((Player)entity).getDialogueManager().showNpcTwoLineDialogue("Of course we know! We just haven't found which tree", "the stupid leprechaun's hiding in yet!", 591);
                return true;
            }
            if (n2 == 14) {
                ((Player)entity).getDialogueManager().showNpcTwoLineDialogue("GAH! I didn't mean to tell you that! Look, just forget I", "said anything okay?", 591);
                return true;
            }
            if (n2 == 15) {
                ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("So a leprechaun knows where Zanaris is eh?", 591);
                return true;
            }
            if (n2 == 16) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Ye.. uh, no. No, not at all. And even if he did - which", "he doesn't - he DEFINITELY ISN'T hiding in some", "tree around here. Nope, definitely not. Honestly.", 591);
                return true;
            }
            if (n2 == 17) {
                ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("Thanks for the help!", 591);
                return true;
            }
            if (n2 == 18) {
                ((Player)entity).getDialogueManager().showNpcTwoLineDialogue("Help? What help? I didn't help! Please don't say I did,", "I'll get in trouble!", 591);
                this.startQuest((Player)entity);
                ((Player)entity).getDialogueManager().finishDialogue();
                return true;
            }
        }
        if (n == 654 && n4 == 2) {
            if (n2 == 1) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Ay yer elephant! Yer've caught me, to be sure!", "What would an elephant like yer be wanting wid ol'", "Shamus then?", 591);
                return true;
            }
            if (n2 == 2) {
                ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("I want to find Zanaris.", 591);
                return true;
            }
            if (n2 == 3) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Zanaris is it now? Well well well... Yer'll be needing to", "be going to that funny little shed out there in the", "swamp, so you will.", 591);
                return true;
            }
            if (n2 == 4) {
                ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("...but... I thought... Zanaris was a city...?", 591);
                return true;
            }
            if (n2 == 5) {
                ((Player)entity).getDialogueManager().showNpcOneLineDialogue("Aye that it is!", 591);
                return true;
            }
            if (n2 == 6) {
                ((Player)entity).getDialogueManager().showTwoOptions("How does it fit in a shed then?", "I've been in that shed, I didn't see a city.");
                return true;
            }
            if (n2 == 7) {
                if (n3 == 1) {
                    ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("...How does it fit in a shed then?", 591);
                    ((Player)entity).getDialogueManager().setNextDialogueStep(8);
                    return true;
                }
                ((Player)entity).getDialogueManager().finishDialogue();
                return false;
            }
            if (n2 == 8) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Ah yer stupid elephant! The city isn't IN the shed! The", "doorway to the shed is being a portal to Zanaris, so it", "is.", 591);
                return true;
            }
            if (n2 == 9) {
                ((Player)entity).getDialogueManager().showPlayerTwoLineDialogue("So I just walk into the shed and end up in Zanaris", "then?", 591);
                return true;
            }
            if (n2 == 10) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Oh, was I fergetting to say? Yer need to be carrying a", "Dramenwood staff to be getting there! Otherwise Yer'll", "just be ending up in the shed.", 591);
                return true;
            }
            if (n2 == 11) {
                ((Player)entity).getDialogueManager().showPlayerOneLineDialogue("So where would I get a staff?", 591);
                return true;
            }
            if (n2 == 12) {
                ((Player)entity).getDialogueManager().showNpcThreeLineDialogue("Dramenwood staffs are crafted from branches of the", "Dramen tree, so they are. I hear there's a Dramen", "tree over on the island of Entrana in a cave", 591);
                return true;
            }
            if (n2 == 13) {
                ((Player)entity).getDialogueManager().showNpcTwoLineDialogue("or some such. There would probably be a good place", "for an elephant like yer to be starting looking I reckon.", 591);
                return true;
            }
            if (n2 == 14) {
                ((Player)entity).getDialogueManager().showNpcTwoLineDialogue("The monks are running a ship from Port Sarim to", "Entrana, I hear too. Now leave me alone yer elephant!", 591);
                return true;
            }
            if (n2 == 15) {
                ((Player)entity).getDialogueManager().showOneLineStatement("The leprechaun magically disappears.");
                ((Player)entity).setQuestState(this.getQuestId(), 3);
                Npc npc = Npc.findByDefinitionId(654);
                npc.setActive(false);
                World.unregisterNpc(npc);
                return true;
            }
        }
        return false;
    }
}

