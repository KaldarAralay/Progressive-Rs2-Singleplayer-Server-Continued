/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.JewelleryCraftingData;
import com.rs2.model.skill.crafting.JewelleryCraftingTask;
import com.rs2.model.skill.crafting.JewelleryDefinition;
import com.rs2.model.task.CycleEventHandler;
import java.util.HashMap;

public final class JewelleryCraftingHandler
extends JewelleryCraftingData {
    private static HashMap definitionsByMaterialItemId = new HashMap();

    static {
        JewelleryDefinition[] jewelleryDefinitionArray = JewelleryDefinition.values();
        int n = jewelleryDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            JewelleryDefinition jewelleryDefinition = jewelleryDefinitionArray[n2];
            definitionsByMaterialItemId.put(JewelleryDefinition.getRecipeData(jewelleryDefinition)[0], jewelleryDefinition);
            ++n2;
        }
    }

    public static JewelleryDefinition forMaterialItemId(int n) {
        return (JewelleryDefinition)((Object)definitionsByMaterialItemId.get(n));
    }

    private static void sendMouldOptions(Player player, int n) {
        Player player2;
        int n2;
        if (!ServerSettings.craftingEnabled) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        n2 = n == 1592 ? 0 : (n == 1597 ? 1 : (n == 1595 ? 2 : -1));
        if (n2 < 0) {
            return;
        }
        if (player.getInventoryManager().getContainer().containsItem(n)) {
            n = 0;
            while (n < JewelleryCraftingData.productsByJewelleryType[n2].length) {
                player2 = player;
                player2.packetSender.sendInterfaceSlotItem(n, interfaceIdsByJewelleryType[n2][1], new ItemStack(JewelleryCraftingData.productsByJewelleryType[n2][n], 1));
                ++n;
            }
            player2 = player;
            player2.packetSender.sendInterfaceModel(interfaceIdsByJewelleryType[n2][0], 0, -1);
            player2 = player;
            player2.packetSender.sendInterfaceText("Choose an item to make.", interfaceIdsByJewelleryType[n2][1] - 3);
        } else {
            player2 = player;
            player2.packetSender.sendInterfaceModel(interfaceIdsByJewelleryType[n2][0], 120, 1595);
            player2 = player;
            player2.packetSender.sendInterfaceText(missingMouldMessages[n2], interfaceIdsByJewelleryType[n2][1] - 3);
            n = 0;
            while (n < JewelleryCraftingData.productsByJewelleryType[n2].length) {
                player2 = player;
                player2.packetSender.sendInterfaceSlotItem(n, interfaceIdsByJewelleryType[n2][1], new ItemStack(0));
                ++n;
            }
        }
        player2 = player;
        player2.packetSender.sendInterfaceText("What would you like to make?", 4226);
    }

    public static void openJewelleryCraftingInterface(Player player, int n) {
        if (!ServerSettings.craftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        JewelleryCraftingHandler.sendMouldOptions(player, 1592);
        JewelleryCraftingHandler.sendMouldOptions(player, 1595);
        JewelleryCraftingHandler.sendMouldOptions(player, 1597);
        player.setSelectedSkillItemId(n);
        player.packetSender.showInterface(4161);
    }

    public static void startJewelleryCraftingTask(Player player, int n, int n2, int n3) {
        if (JewelleryCraftingHandler.forMaterialItemId(n) == null || n2 <= 0 || n3 < 0) {
            return;
        }
        if (!player.getInventoryManager().getContainer().containsItem(JewelleryDefinition.getRecipeData(JewelleryCraftingHandler.forMaterialItemId(n))[0])) {
            player.getDialogueManager().showOneLineStatement("You do not have the required items to do that.");
            return;
        }
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        int n4 = player.nextActionSequence();
        player.setActiveCycleEvent(new JewelleryCraftingTask(n, n2, n3, player, n4));
        CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 1);
    }

    public static void stringAmulet(Player player, int n) {
        if (!player.getInventoryManager().getContainer().containsItem(1759) || !player.getInventoryManager().getContainer().containsItem(amuletStringingRecipes[n][0])) {
            return;
        }
        if (!ServerSettings.craftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        player.getInventoryManager().removeItem(new ItemStack(amuletStringingRecipes[n][0], 1));
        player.getInventoryManager().removeItem(new ItemStack(1759, 1));
        player.getSkillManager().addExperience(12, 4.0);
        player.getInventoryManager().addItem(new ItemStack(amuletStringingRecipes[n][1], 1));
        if (amuletStringingRecipes[n][1] == 4021) {
            player.packetSender.sendGameMessage("You put some string on your amulet. It makes a slight 'Ook' sound.");
            return;
        }
        player.packetSender.sendGameMessage("You attach a string to the " + new ItemStack(amuletStringingRecipes[n][0]).getDefinition().getName().toLowerCase() + ".");
    }
}

