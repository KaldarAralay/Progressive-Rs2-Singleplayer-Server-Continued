/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.AllotmentCropDefinition;
import com.rs2.model.skill.farming.AllotmentPatch;
import com.rs2.model.skill.farming.BushDefinition;
import com.rs2.model.skill.farming.BushPatch;
import com.rs2.model.skill.farming.FarmedTreeDefinition;
import com.rs2.model.skill.farming.FruitTreeDefinition;
import com.rs2.model.skill.farming.FruitTreePatch;
import com.rs2.model.skill.farming.HopsDefinition;
import com.rs2.model.skill.farming.HopsPatch;
import com.rs2.model.skill.farming.TreePatch;
import com.rs2.util.GameUtil;

public final class FarmingFarmerHandler {
    private static String[][] adviceMessages = new String[][]{{"You don't have to buy all your plantpots you know,", "you can make them yourself on a pottery wheel. If", "you are a good enough craftsman, that is."}, {"Don't just throw away your weeds after you've", "raked a patch - put them in a compost bin and", "make some compost."}, {"Tree seeds must be grown in a plantpot of soil", "into a tree sapling, and then transferred to a", "tree patch to continue growing to adulthood."}, {"You can put up to ten potatoes, cabbages, or", "onions in vegetable sacks, although you can't", "have a mix in the same sack."}, {"You can buy all the farming tools from farming", "shops which can be found close to the allotments"}, {"You can fill plantpots with soil from Farming", "patches, if you have a gardening trowel."}, {"If you want to make your own sacks and baskets", "you'll need to use the loom that's near the", "Farming shop in Falador."}, {"Bittercap mushrooms can only be grown in special", "patches in Morytania, near the Mort Myre swamp."}, {"Applying compost to a patch will not only reduce", "the chance that your crops will get diseased, but", "you will also grow more crops to harvest."}, {"Hops are good for brewing ales. I believe there", "is a brewery up in Keldagrim somewhere."}};

    public static void handlePatchProtectionDialogue(Player player, int n, String string, int n2, int n3) {
        int n4 = 0;
        int[] nArray = new int[2];
        if (n3 == 1) {
            if (string == "allotment") {
                n4 = (Integer)AllotmentPatch.getIndexesForObjectId(n2).get(n);
                if (AllotmentCropDefinition.forSeedId(player.getAllotmentPatchManager().cropIds[n4]) != null) {
                    nArray = AllotmentCropDefinition.forSeedId(player.getAllotmentPatchManager().cropIds[n4]).getProtectionPayment();
                }
                if (player.getAllotmentPatchManager().growthStages[n4] <= 3) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am sorry but you have no crops growing in this patch.", 595);
                    player.getDialogueManager().finishDialogue();
                } else if (player.getAllotmentPatchManager().protectionFlags[n4]) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am already watching over your plants.", 595);
                    player.getDialogueManager().finishDialogue();
                } else {
                    player.getDialogueManager().showNpcOneLineDialogue("If you like, but I want " + nArray[1] + " " + FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()) + (FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()).endsWith("s") ? "" : "s") + " for that.", 602);
                    player.getDialogueManager().setNextDialogueStep(18);
                    player.setSelectedSkillItemId(n);
                }
            }
            if (string == "bushes") {
                n4 = BushPatch.forObjectId(n2).getIndex();
                if (BushDefinition.forSeedId(player.getBushPatchManager().cropIds[n4]) != null) {
                    nArray = BushDefinition.forSeedId(player.getBushPatchManager().cropIds[n4]).getProtectionPayment();
                }
                if (player.getBushPatchManager().growthStages[n4] <= 3) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am sorry but you have no crops growing in this patch.", 595);
                    player.getDialogueManager().finishDialogue();
                } else if (player.getBushPatchManager().protectionFlags[n4]) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am already watching over your plants.", 595);
                    player.getDialogueManager().finishDialogue();
                } else {
                    player.getDialogueManager().showNpcOneLineDialogue("If you like, but I want " + nArray[1] + " " + FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()) + (FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()).endsWith("s") ? "" : "s") + " for that.", 602);
                    player.getDialogueManager().setNextDialogueStep(18);
                    player.setSelectedSkillItemId(n);
                }
            }
            if (string == "fruitTree") {
                n4 = FruitTreePatch.forObjectId(n2).getIndex();
                if (FruitTreeDefinition.forSaplingId(player.getFruitTreePatchManager().treeIds[n4]) != null) {
                    nArray = FruitTreeDefinition.forSaplingId(player.getFruitTreePatchManager().treeIds[n4]).getProtectionPayment();
                }
                if (player.getFruitTreePatchManager().growthStages[n4] <= 3) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am sorry but you have no crops growing in this patch.", 595);
                    player.getDialogueManager().finishDialogue();
                } else if (player.getFruitTreePatchManager().protectionFlags[n4]) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am already watching over your plants.", 595);
                    player.getDialogueManager().finishDialogue();
                } else {
                    player.getDialogueManager().showNpcOneLineDialogue("If you like, but I want " + nArray[1] + " " + FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()) + (FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()).endsWith("s") ? "" : "s") + " for that.", 602);
                    player.getDialogueManager().setNextDialogueStep(18);
                    player.setSelectedSkillItemId(n);
                }
            }
            if (string == "hops") {
                n4 = HopsPatch.forObjectId(n2).getIndex();
                if (HopsDefinition.forSeedId(player.getHopsPatchManager().cropIds[n4]) != null) {
                    nArray = HopsDefinition.forSeedId(player.getHopsPatchManager().cropIds[n4]).getProtectionPayment();
                }
                if (player.getHopsPatchManager().growthStages[n4] <= 3) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am sorry but you have no crops growing in this patch.", 595);
                    player.getDialogueManager().finishDialogue();
                } else if (player.getHopsPatchManager().protectionFlags[n4]) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am already watching over your plants.", 595);
                    player.getDialogueManager().finishDialogue();
                } else {
                    player.getDialogueManager().showNpcOneLineDialogue("If you like, but I want " + nArray[1] + " " + FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()) + (FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()).endsWith("s") ? "" : "s") + " for that.", 602);
                    player.getDialogueManager().setNextDialogueStep(18);
                    player.setSelectedSkillItemId(n);
                }
            }
            if (string == "tree") {
                n4 = TreePatch.forObjectId(n2).getIndex();
                if (FarmedTreeDefinition.forSaplingId(player.getTreePatchManager().treeIds[n4]) != null) {
                    nArray = FarmedTreeDefinition.forSaplingId(player.getTreePatchManager().treeIds[n4]).getProtectionPayment();
                }
                if (player.getTreePatchManager().growthStages[n4] <= 3) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am sorry but you have no crops growing in this patch.", 595);
                    player.getDialogueManager().finishDialogue();
                    return;
                }
                if (player.getTreePatchManager().protectionFlags[n4]) {
                    player.getDialogueManager().showNpcOneLineDialogue("I am already watching over your plants.", 595);
                    player.getDialogueManager().finishDialogue();
                    return;
                }
                player.getDialogueManager().showNpcOneLineDialogue("If you like, but I want " + nArray[1] + " " + FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()) + (FarmingFarmerHandler.formatPaymentItemName(ItemDefinition.forId(nArray[0]).getName().toLowerCase()).endsWith("s") ? "" : "s") + " for that.", 602);
                player.getDialogueManager().setNextDialogueStep(18);
                player.setSelectedSkillItemId(n);
                return;
            }
        } else if (n3 == 2) {
            if (string == "allotment") {
                n4 = (Integer)AllotmentPatch.getIndexesForObjectId(n2).get(n);
                nArray = AllotmentCropDefinition.forSeedId(player.getAllotmentPatchManager().cropIds[n4]).getProtectionPayment();
            }
            if (string == "bushes") {
                n4 = BushPatch.forObjectId(n2).getIndex();
                nArray = BushDefinition.forSeedId(player.getBushPatchManager().cropIds[n4]).getProtectionPayment();
            }
            if (string == "fruitTree") {
                n4 = FruitTreePatch.forObjectId(n2).getIndex();
                nArray = FruitTreeDefinition.forSaplingId(player.getFruitTreePatchManager().treeIds[n4]).getProtectionPayment();
            }
            if (string == "hops") {
                n4 = HopsPatch.forObjectId(n2).getIndex();
                nArray = HopsDefinition.forSeedId(player.getHopsPatchManager().cropIds[n4]).getProtectionPayment();
            }
            if (string == "tree") {
                n4 = TreePatch.forObjectId(n2).getIndex();
                nArray = FarmedTreeDefinition.forSaplingId(player.getTreePatchManager().treeIds[n4]).getProtectionPayment();
            }
            if (player.getInventoryManager().getItemAmount(nArray[0]) < nArray[1]) {
                player.getDialogueManager().showNpcTwoLineDialogue("Sorry, but you do not have the required items", "I need, for letting me take care of this patch", 595);
                player.getDialogueManager().finishDialogue();
                return;
            }
            if (string == "allotment") {
                player.getAllotmentPatchManager().protectionFlags[n4] = true;
            }
            if (string == "bushes") {
                player.getBushPatchManager().protectionFlags[n4] = true;
            }
            if (string == "fruitTree") {
                player.getFruitTreePatchManager().protectionFlags[n4] = true;
            }
            if (string == "hops") {
                player.getHopsPatchManager().protectionFlags[n4] = true;
            }
            if (string == "tree") {
                player.getTreePatchManager().protectionFlags[n4] = true;
            }
            player.getInventoryManager().removeItem(new ItemStack(nArray[0], nArray[1]));
            player.getDialogueManager().showNpcThreeLineDialogue("Here you go, I will be taking care of this patch", "as soon as it become diseased, I will cure it", "so you don't have to worry about it.", 588);
            player.getDialogueManager().finishDialogue();
        }
    }

    public static void chopTreeForFee(Player player, int n) {
        int n2 = TreePatch.forObjectId(n).getIndex();
        if (player.getTreePatchManager().growthStages[n2] < 3) {
            DialogueManager.continueDialogue(player, n, 15, 0);
            return;
        }
        if (player.getInventoryManager().getItemAmount(995) < 200) {
            player.getDialogueManager().showNpcTwoLineDialogue("I am sorry, but you do not have enough money", "to pay me to chop down this tree.", 595);
            player.getDialogueManager().finishDialogue();
            return;
        }
        player.getInventoryManager().removeItem(new ItemStack(995, 200));
        player.getDialogueManager().showNpcTwoLineDialogue("There you go, I have chopped down your tree but I am ", "keeping the logs and roots as compensation.", 595);
        player.getTreePatchManager().resetPatch(n2);
        player.getTreePatchManager().growthStages[n2] = 3;
        player.getTreePatchManager().lastUpdateTicks[n2] = Server.getElapsedMinutes();
        player.getTreePatchManager().refreshConfig();
        player.getDialogueManager().finishDialogue();
    }

    public static void showRandomFarmingAdvice(Player player) {
        player.getDialogueManager().showNpcDialogue(adviceMessages[GameUtil.randomInclusive(9)], 588);
        player.getDialogueManager().finishDialogue();
    }

    private static String formatPaymentItemName(String string) {
        if (string.contains("(5)")) {
            return "baskets of " + string.replace("(5)", "");
        }
        if (string.contains("(10)")) {
            return "sacks of " + string.replace("(10)", "");
        }
        return string;
    }
}

