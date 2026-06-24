/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestScript;
import com.rs2.model.quest.impl.ElementalShieldSmithingTask;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.smithing.SmeltingHandler;
import com.rs2.model.task.TickTask;

public final class ElementalWorkshopQuest
extends QuestScript {
    private int elementalShieldBookLastPageIndex = 1;

    public ElementalWorkshopQuest(int n) {
        super(32);
        super.setQuestPointReward(1);
    }

    @Override
    public final String[] buildQuestJournal(Player player, int n) {
        int n2 = n - 3;
        if (n == 0) {
            n = player.getSkillManager().getBaseLevel(12);
            n2 = player.getSkillManager().getBaseLevel(14);
            int n3 = player.getSkillManager().getBaseLevel(13);
            String[] stringArray = new String[]{"I can start this quest by reading a", "book found in Seers village.", "", "Minimum requirements:", String.valueOf(n2 >= 20 ? "@str@" : "") + "Level 20 Mining", String.valueOf(n3 >= 20 ? "@str@" : "") + "Level 20 Smithing", String.valueOf(n >= 20 ? "@str@" : "") + "Level 20 Crafting"};
            return stringArray;
        }
        if (n == 2) {
            String[] stringArray = new String[]{"I should now search for the hidden workshop located", "in the village of the Seers."};
            return stringArray;
        }
        if (n >= 3 && n < 66) {
            String[] stringArray = new String[]{"I should do the following things now:", String.valueOf((n2 & 4) != 0 ? "@str@" : "") + "Get the waterwheel running", String.valueOf((n2 & 0x10) != 0 ? "@str@" : "") + "Start the bellows", String.valueOf((n2 & 0x20) != 0 ? "@str@" : "") + "Warm up the furnace"};
            return stringArray;
        }
        if (n == 66) {
            String[] stringArray = new String[]{"I have fixed everything, and should now be able to make", "the elemental shield."};
            return stringArray;
        }
        if (n == 1) {
            String[] stringArray = new String[]{"Quest Completed!", "", "You were awarded:", "1 Quest Point", "5000 Crafting  XP", "5000 Smithing XP", "The ability to make elemental shields."};
            return stringArray;
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
        player2.packetSender.sendInterfaceText("5000 Crafting  XP", 12151);
        player2 = player;
        player2.packetSender.sendInterfaceText("5000 Smithing XP", 12152);
        player2 = player;
        player2.packetSender.sendInterfaceText("The ability to make elemental shields.", 12153);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12154);
        player2 = player;
        player2.packetSender.sendInterfaceText("", 12155);
        player.getSkillManager().addQuestExperience(12, 5000.0);
        player.getSkillManager().addQuestExperience(13, 5000.0);
        player2 = player;
        player2.packetSender.sendInterfaceModel(InterfaceDefinition.interfaceCount <= 12140 ? 6161 : 12145, 250, 2890);
        player2 = player;
        player2.packetSender.showInterface(InterfaceDefinition.interfaceCount <= 12140 ? 1689 : 12140);
        player2 = player;
        player.deferLevelUpInterfaces = false;
    }

    @Override
    public final boolean handleFirstObjectAction(Player player, int n, int n2, int n3, int n4) {
        int n5 = n4 - 3;
        if (n == 3403) {
            if (player.H != null && !player.H.isDead() && player.H.getNpcId() == 1023) {
                return true;
            }
            GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.findUsableGatheringTool(player, 14);
            if (gatheringToolDefinition == null) {
                Player player2 = player;
                player2.packetSender.sendGameMessage("You do not have a pickaxe that you can use.");
                return true;
            }
            if (!SkillActionHelper.checkSkillRequirement(player, 14, 20, "mine here")) {
                return true;
            }
            new DynamicObject(ServerSettings.placeholderObjectId, n2, n3, player.getPosition().getPlane(), 0, 10, n, 100);
            Npc npc = new Npc(1023);
            GameplayHelper.spawnOwnedNpcAdjacentToPlayer(player, npc, true, false);
            npc.getUpdateState().setAnimation(1038);
            npc.getUpdateState().setForcedText("Grr... Ge'roff us!");
            return true;
        }
        if (n == 3389 && n2 == 2716 && n3 == 3481 && !player.ownsItem(2886)) {
            player.getInventoryManager().addOrDropItem(new ItemStack(2886, 1));
            player.getDialogueManager().showItemMessage("You find a book titled 'The Elemental Shield'.", new ItemStack(2886, 1));
            return true;
        }
        if (n == 3410 && n2 == 2734 && n3 == 9882) {
            if (n4 == 1) {
                return false;
            }
            if ((n5 & 8) != 0) {
                Player player3 = player;
                player3.packetSender.sendGameMessage("You have already fixed the bellows.");
                return true;
            }
            if (!SkillActionHelper.checkSkillRequirement(player, 12, 20, "fix the bellows")) {
                return true;
            }
            if (player.getInventoryManager().containsItemAmount(1741, 1) && player.getInventoryManager().containsItemAmount(1733, 1) && player.getInventoryManager().containsItemAmount(1734, 1)) {
                Player player4 = player;
                player4.packetSender.sendGameMessage("You stitch the leather over the hole in the bellows.");
                player.getInventoryManager().removeItem(new ItemStack(1741, 1));
                player.getInventoryManager().removeItem(new ItemStack(1734, 1));
                player.addQuestState(this.getQuestId(), 8);
                return true;
            }
        }
        if (n == 3397 && n2 == 2724 && n3 == 9894) {
            if (!player.ownsItem(2888)) {
                player.getInventoryManager().addOrDropItem(new ItemStack(2888, 1));
                Player player5 = player;
                player5.packetSender.sendGameMessage("You find a stone bowl.");
            } else {
                Player player6 = player;
                player6.packetSender.sendGameMessage("It's empty.");
            }
            return true;
        }
        if (n == 3390 && n2 == 2710 && n3 == 3495 || n == 3391 && n2 == 2709 && n3 == 3495) {
            if (player.getPosition().getY() < 3496) {
                if (player.getInventoryManager().containsItemAmount(2887, 1)) {
                    Player player7 = player;
                    player7.packetSender.queueRelativeMovementStep(0, 1, true);
                    player7 = player;
                    player7.packetSender.openNorthShiftedDoubleDoorPair(3391, 3390, 2709, 3495, 2710, 3495, 0);
                    player7 = player;
                    player7.packetSender.sendGameMessage("You use the battered key to open the doors.");
                    return true;
                }
            } else {
                Player player8 = player;
                player8.packetSender.queueRelativeMovementStep(0, -1, true);
                player8 = player;
                player8.packetSender.openNorthShiftedDoubleDoorPair(3391, 3390, 2709, 3495, 2710, 3495, 0);
                return true;
            }
        }
        if (n == 3415 && n2 == 2710 && n3 == 3497) {
            player.moveTo(new Position(2716, 9888, 0));
            if (n4 == 2) {
                player.getDialogueManager().showPlayerTwoLineDialogue("Now to explore this area thoroughly, to find what", "forgotten secrets it contains.", 591);
                player.getDialogueManager().finishDialogue();
                player.setQuestState(this.getQuestId(), 3);
            }
            return true;
        }
        if (n == 3416 && n2 == 2714 && n3 == 9887) {
            player.moveTo(new Position(2709, 3498, 0));
            return true;
        }
        if (n == 3404 && n2 == 2726 && n3 == 9908) {
            if (n4 == 1) {
                return false;
            }
            if ((n5 & 1) == 0) {
                Player player9 = player;
                player9.packetSender.sendGameMessage("You turn the handle.");
                player.addQuestState(this.getQuestId(), 1);
            } else {
                Player player10 = player;
                player10.packetSender.sendGameMessage("You have already turned this handle.");
            }
            return true;
        }
        if (n == 3405 && n2 == 2713 && n3 == 9908) {
            if (n4 == 1) {
                return false;
            }
            if ((n5 & 1) == 0) {
                Player player11 = player;
                player11.packetSender.sendGameMessage("It doesn't seem to work quite yet.");
            }
            if ((n5 & 2) == 0 && (n5 & 1) != 0) {
                Player player12 = player;
                player12.packetSender.sendGameMessage("You turn the handle.");
                player.addQuestState(this.getQuestId(), 2);
            }
            if ((n5 & 2) != 0) {
                Player player13 = player;
                player13.packetSender.sendGameMessage("You have already turned this handle.");
            }
            return true;
        }
        if (n == 3406 && n2 == 2722 && n3 == 9906) {
            if (n4 == 1) {
                return false;
            }
            if ((n5 & 1) == 0 || (n5 & 2) == 0) {
                Player player14 = player;
                player14.packetSender.sendGameMessage("It doesn't seem to work quite yet.");
            }
            if ((n5 & 4) == 0 && (n5 & 1) != 0 && (n5 & 2) != 0) {
                Player player15 = player;
                player15.packetSender.sendGameMessage("You pull the lever.");
                player15 = player;
                player15.packetSender.sendGameMessage("You hear the sound of a water wheel starting up.");
                player.addQuestState(this.getQuestId(), 4);
            }
            if ((n5 & 4) != 0) {
                Player player16 = player;
                player16.packetSender.sendGameMessage("You have already fixed the waterwheel.");
            }
            return true;
        }
        if (n == 3409 && n2 == 2734 && n3 == 9887) {
            if (n4 == 1) {
                return false;
            }
            if ((n5 & 8) == 0) {
                Player player17 = player;
                player17.packetSender.sendGameMessage("You should fix the bellows first before pulling the lever.");
            }
            if ((n5 & 0x10) == 0 && (n5 & 8) != 0) {
                Player player18 = player;
                player18.packetSender.sendGameMessage("You pull the lever.");
                player18 = player;
                player18.packetSender.sendGameMessage("The bellows pump air down the pipe.");
                player.addQuestState(this.getQuestId(), 16);
            }
            if ((n5 & 0x10) != 0) {
                Player player19 = player;
                player19.packetSender.sendGameMessage("You have already pulled the lever.");
            }
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleItemOnItem(Player player, int n, int n2, int n3) {
        if (n == 2886 && n2 == 946 || n == 946 && n2 == 2886) {
            if (n3 != 0) {
                if (!player.ownsItem(2887)) {
                    player.getInventoryManager().addItem(new ItemStack(2887, 1));
                    Player player2 = player;
                    player2.packetSender.sendGameMessage("You make a small cut in the spine of the book.");
                    player2 = player;
                    player2.packetSender.sendGameMessage("Inside you find a small, old, battered key.");
                    return true;
                }
            } else {
                Player player3 = player;
                player3.packetSender.sendGameMessage("You don't want to damage the book.");
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean handleItemOnObject(Player object, int n, int n2, int n3) {
        int n4 = n3 - 3;
        if (n == 2888 && n2 == 3414) {
            ((Player)object).getInventoryManager().replaceItem(new ItemStack(2888, 1), new ItemStack(2889, 1));
            Player player = object;
            player.packetSender.sendGameMessage("You fill the bowl with hot lava.");
            return true;
        }
        if (n == 2889 && n2 == 3413) {
            if (n3 == 1) {
                return false;
            }
            if ((n4 & 0x20) != 0) {
                Player player = object;
                player.packetSender.sendGameMessage("You have already added lava to the furnace.");
                return true;
            }
            ((Player)object).getInventoryManager().replaceItem(new ItemStack(2889, 1), new ItemStack(2888, 1));
            Player player = object;
            player.packetSender.sendGameMessage("You empty the lava into the furnace.");
            ((Player)object).addQuestState(this.getQuestId(), 32);
            return true;
        }
        if (n == 2892 && n2 == 3413 && (n3 == 66 || n3 == 1)) {
            SmeltingHandler.handleOreOnFurnace((Player)object, 2892);
            return true;
        }
        if (n == 2893 && n2 == 3402 && (n3 == 66 || n3 == 1) && ((Player)object).getInventoryManager().containsItemAmount(2347, 1)) {
            if (n3 != 1) {
                if (((Player)object).getInventoryManager().containsItemAmount(2886, 1)) {
                    Player player = object;
                    player.packetSender.sendGameMessage("Following the instructions in the book you make an elemental shield.");
                } else {
                    return true;
                }
            }
            Player player = object;
            player.packetSender.sendSoundEffect(468, 1, 0);
            ((Entity)object).getUpdateState().setAnimation(898);
            World.getTaskScheduler().schedule(new ElementalShieldSmithingTask(this, 3, (Player)object, n3));
            return true;
        }
        return false;
    }

    private void showElementalShieldBookPage(Player player, int n) {
        String[] stringArray;
        Player player2;
        Player player3 = player2 = player;
        player2.packetSender.sendInterfaceText("", 14165);
        player3 = player2;
        player3.packetSender.sendInterfaceText("", 14166);
        int n2 = 843;
        while (n2 <= 864) {
            player3 = player2;
            player3.packetSender.sendInterfaceText("", n2);
            ++n2;
        }
        int n3 = n;
        if (n3 == 0) {
            String[] stringArray2;
            stringArray = stringArray2 = new String[]{"Book of the elemental shield", "", "", "Within the pages of this", "book you will find the", "secret to working the", "very elements themselves.", "Early in the fifth age, a", "new ore was discovered.", "This ore has a unique", "property of absorbing,", "transforming or focusing", "elemental energy. A", "workshop was erected", "close by to work this new", "material. The workshop", "was set up for artisans", "and inventors to be able", "to come and create", "devices made from the", "unique ore, found only in", "the village of the Seers."};
        } else if (n3 == 1) {
            String[] stringArray3 = new String[]{"Book of the elemental shield", "", "", "After some time of", "successful industry the", "true power of this ore", "became apparent, as", "greater and more", "powerful weapons were", "created. Realising the", "threat this posed, the magi", "of the time closed down", "the workshop and bound", "it under lock and key,", "also trying to destroy all", "knowledge of", "manufacturing processes.", "Yet this book remains and", "you may still find a way", "to enter the workshop", "within this leather bound", "volume."};
            stringArray = stringArray3;
        } else {
            stringArray = null;
        }
        String[] stringArray4 = stringArray;
        player3 = player;
        player3.packetSender.sendInterfaceText((String)stringArray4[0], 903);
        player3 = player;
        player3.packetSender.sendInterfaceText((String)stringArray4[1], 14165);
        player3 = player;
        player3.packetSender.sendInterfaceText((String)stringArray4[2], 14166);
        n3 = 3;
        while (n3 < stringArray4.length) {
            player3 = player;
            player3.packetSender.sendInterfaceText((String)stringArray4[n3], n3 + 843 - 3);
            ++n3;
        }
        player3 = player;
        player3.packetSender.setInterfaceHiddenFlag(player.activeBookPageIndex == 0 ? 1 : 0, 840);
        player3 = player;
        player3.packetSender.setInterfaceHiddenFlag(player.activeBookPageIndex == this.elementalShieldBookLastPageIndex ? 1 : 0, 842);
    }

    @Override
    public final boolean handleButtonClick(Player player, int n, int n2) {
        if (player.activeBookItemId == 2886) {
            if (n == 841 && player.activeBookPageIndex < this.elementalShieldBookLastPageIndex) {
                ++player.activeBookPageIndex;
                this.showElementalShieldBookPage(player, player.activeBookPageIndex);
                return true;
            }
            if (n == 839 && player.activeBookPageIndex > 0) {
                --player.activeBookPageIndex;
                this.showElementalShieldBookPage(player, player.activeBookPageIndex);
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean handleInventoryItemFirstOption(Player player, int n, int n2, int n3) {
        if (n == 3214 && n2 == 2886) {
            this.showElementalShieldBookPage(player, 0);
            player.activeBookItemId = n2;
            player.activeBookPageIndex = 0;
            Player player2 = player;
            player2.packetSender.showInterface(837);
            if (n3 == 0) {
                player2 = player;
                player2.packetSender.sendGameMessage("The book has two parts: an introduction and an instruction section.");
                player2 = player;
                player2.packetSender.sendGameMessage("You flip the book open to the introduction and start reading.");
                this.startQuest(player);
            }
            return true;
        }
        return false;
    }
}

