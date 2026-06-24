/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.herblore;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.skill.herblore.FinishedPotionTask;
import com.rs2.model.skill.herblore.UnfinishedPotionTask;
import com.rs2.model.task.CycleEventHandler;

public final class HerbloreHandler {
    private static final double[][] POTION_RECIPES = new double[][]{{249.0, 221.0, 91.0, 121.0, 25.0, 0.0}, {251.0, 235.0, 93.0, 175.0, 37.5, 5.0}, {253.0, 225.0, 95.0, 115.0, 50.0, 12.0}, {253.0, 592.0, 95.0, 3408.0, 50.0, 15.0}, {255.0, 223.0, 97.0, 127.0, 62.5, 22.0}, {255.0, 1581.0, 97.0, 1582.0, 80.0, 25.0}, {255.0, 1975.0, 97.0, 3010.0, 67.5, 26.0}, {257.0, 239.0, 99.0, 133.0, 75.0, 30.0}, {2998.0, 2152.0, 3002.0, 3034.0, 80.0, 34.0}, {257.0, 231.0, 99.0, 139.0, 87.5, 38.0}, {259.0, 221.0, 101.0, 145.0, 100.0, 45.0}, {259.0, 235.0, 101.0, 181.0, 106.3, 48.0}, {261.0, 231.0, 103.0, 151.0, 112.5, 50.0}, {261.0, 2970.0, 103.0, 3018.0, 117.5, 52.0}, {263.0, 225.0, 105.0, 157.0, 125.0, 55.0}, {263.0, 241.0, 105.0, 187.0, 137.5, 60.0}, {3000.0, 223.0, 3004.0, 3026.0, 142.5, 63.0}, {265.0, 239.0, 107.0, 163.0, 150.0, 66.0}, {2998.0, 6049.0, 5942.0, 5945.0, 175.0, 68.0}, {2481.0, 241.0, 2483.0, 2454.0, 157.5, 69.0}, {267.0, 245.0, 109.0, 169.0, 162.5, 72.0}, {6016.0, 223.0, 5936.0, 5937.0, 175.0, 73.0}, {2481.0, 3138.0, 2483.0, 3042.0, 172.5, 76.0}, {269.0, 247.0, 111.0, 189.0, 175.0, 78.0}, {259.0, 6051.0, 101.0, 5954.0, 175.0, 79.0}, {2998.0, 6693.0, 3002.0, 6687.0, 175.0, 81.0}, {2398.0, 6018.0, 5939.0, 5940.0, 175.0, 82.0}};

    public static boolean combinePotionDoses(Player player, int n, int n2, int n3, int n4) {
        block5: {
            int n5;
            int n6;
            String string;
            Object object;
            block7: {
                Object object2;
                block6: {
                    object = new ItemStack(n);
                    object2 = new ItemStack(n2);
                    string = ((ItemStack)object).getDefinition().getName().toLowerCase();
                    String string2 = ((ItemStack)object2).getDefinition().getName().toLowerCase();
                    if (string.contains("(4)") || string2.contains("(4)") || string.contains("watering") || string2.contains("watering")) {
                        return false;
                    }
                    try {
                        if (!string.substring(0, string.indexOf("(")).equalsIgnoreCase(string2.substring(0, string2.indexOf("(")))) break block5;
                        n6 = Integer.parseInt(string.substring(string.indexOf("(") + 1, string.indexOf("(") + 2));
                        n5 = Integer.parseInt(string2.substring(string2.indexOf("(") + 1, string2.indexOf("(") + 2));
                        n5 = n6 + n5;
                        if (player.getInventoryManager().containsItem(((ItemStack)object).getId()) && player.getInventoryManager().containsItem(((ItemStack)object2).getId())) break block6;
                        return false;
                    }
                    catch (Exception exception) {
                        return false;
                    }
                }
                if (n5 <= 4) break block7;
                object = String.valueOf(string.substring(0, string.indexOf("(") + 1)) + 4 + ")";
                object2 = String.valueOf(string.substring(0, string.indexOf("(") + 1)) + (n5 -= 4) + ")";
                player.getInventoryManager().removeItemAtSlot(n3);
                player.getInventoryManager().removeItemAtSlot(n4);
                player.getInventoryManager().setItemInSlot(new ItemStack(ItemDefinition.findIdByName((String)object)), n4);
                player.getInventoryManager().setItemInSlot(new ItemStack(ItemDefinition.findIdByName((String)object2)), n3);
                return true;
            }
            n6 = n5;
            object = String.valueOf(string.substring(0, string.indexOf("(") + 1)) + n6 + ")";
            player.getInventoryManager().removeItemAtSlot(n3);
            player.getInventoryManager().removeItemAtSlot(n4);
            player.getInventoryManager().setItemInSlot(new ItemStack(ItemDefinition.findIdByName((String)object)), n4);
            player.getInventoryManager().setItemInSlot(new ItemStack(229, 1), n3);
            return true;
        }
        return false;
    }

    public static boolean handlePotionMaking(Player player, ItemStack itemStack, ItemStack itemStack2, int n, int n2) {
        n = itemStack.getId();
        int n3 = itemStack2.getId();
        double[][] dArray = POTION_RECIPES;
        int n4 = 0;
        while (n4 < 27) {
            double[] recipe = dArray[n4];
            int n5 = (int)recipe[0];
            double ingredientId = recipe[1];
            double unfinishedPotionId = recipe[2];
            double finishedPotionId = recipe[3];
            double experience = recipe[4];
            double requiredLevel = recipe[5];
            int n6 = n5 == 2398 || ingredientId == 6018.0 || n5 == 6016 || ingredientId == 6049.0 ? 5935 : 227;
            if (n == n5 && n3 == n6 || n == n6 && n3 == n5) {
                if ((double)player.getSkillManager().getCurrentLevels()[15] < requiredLevel) {
                    player.getDialogueManager().showOneLineStatement("You need a Herblore level of " + (int)requiredLevel + " in order to make this potion.");
                    return true;
                }
                int vialItemId = n6;
                ItemStack itemStack3 = new ItemStack(n5, 1);
                ItemStack itemStack4 = new ItemStack(vialItemId, 1);
                if (!ServerSettings.herbloreEnabled) {
                    Player player2 = player;
                    player2.packetSender.sendGameMessage("This skill is currently disabled.");
                } else if (player.getQuestState(29) != 1) {
                    QuestDefinition questDefinition = QuestDefinition.forId(29);
                    String string = questDefinition.getName();
                    Player player3 = player;
                    player3.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
                } else {
                    player.getUpdateState().setAnimation(363);
                    Player player4 = player;
                    player4.packetSender.sendGameMessage("You put the " + itemStack3.getDefinition().getName().replace(" herb", "").toLowerCase() + " into the " + itemStack4.getDefinition().getName() + ".");
                    int n7 = player.nextActionSequence();
                    CycleEventHandler.getInstance().schedule(player, new UnfinishedPotionTask(player, n7, itemStack3, vialItemId, unfinishedPotionId), 1);
                }
                return true;
            }
            if ((double)n == unfinishedPotionId && (double)n3 == ingredientId || (double)n == ingredientId && (double)n3 == unfinishedPotionId) {
                if ((double)player.getSkillManager().getCurrentLevels()[15] < requiredLevel) {
                    player.getDialogueManager().showOneLineStatement("You need a Herblore level of " + (int)requiredLevel + " in order to make this potion.");
                    return true;
                }
                ItemStack ingredientStack = new ItemStack((int)ingredientId, 1);
                if (!ServerSettings.herbloreEnabled) {
                    Player player5 = player;
                    player5.packetSender.sendGameMessage("This skill is currently disabled.");
                } else if (player.getQuestState(29) != 1) {
                    QuestDefinition questDefinition = QuestDefinition.forId(29);
                    String string = questDefinition.getName();
                    Player player6 = player;
                    player6.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
                } else {
                    player.getUpdateState().setAnimation(363);
                    Player player7 = player;
                    player7.packetSender.sendGameMessage("You mix the " + ingredientStack.getDefinition().getName().toLowerCase() + " into your potion");
                    int n8 = player.nextActionSequence();
                    CycleEventHandler.getInstance().schedule(player, new FinishedPotionTask(player, n8, ingredientStack, unfinishedPotionId, finishedPotionId, experience), 1);
                }
                return true;
            }
            ++n4;
        }
        return false;
    }

    public static boolean emptyContainer(Player player, ItemStack itemStack, int n) {
        Object object = itemStack.getDefinition().getDescription().toLowerCase();
        String string = itemStack.getDefinition().getName().toLowerCase();
        if (((String)object).contains("bucket") || ((String)object).contains("potion") || ((String)object).contains("dose") || ((String)object).contains("jug") || string.contains("jug") || ((String)object).contains("bowl") || ((String)object).contains("vial") || string.contains("flour") || ((String)object).contains("bucket") || ((String)object).contains("cup")) {
            object = player;
            ((Player)object).packetSender.sendGameMessage("You empty your " + string + ".");
            if (player.getInventoryManager().removeItemFromSlot(itemStack, n)) {
                player.getInventoryManager().setItemInSlot(new ItemStack(HerbloreHandler.getEmptyContainerItemId(itemStack)), n);
            } else if (player.getInventoryManager().removeItem(itemStack)) {
                player.getInventoryManager().addItem(new ItemStack(HerbloreHandler.getEmptyContainerItemId(itemStack)));
            }
            return true;
        }
        return false;
    }

    private static int getEmptyContainerItemId(ItemStack itemStack) {
        String string = itemStack.getDefinition().getDescription().toLowerCase();
        String string2 = itemStack.getDefinition().getName().toLowerCase();
        if (string.contains("potion") || string.contains("vial") || string.contains("dose")) {
            return 229;
        }
        if (string.contains("bucket") || string.contains("compost")) {
            return 1925;
        }
        if (string.contains("bowl") || string.contains("curry")) {
            return 1923;
        }
        if (string2.contains("jug") || string.contains("jug")) {
            return 1935;
        }
        if (string2.contains("flour")) {
            return 1931;
        }
        if (string.contains("cup")) {
            return 1980;
        }
        return -1;
    }

}

