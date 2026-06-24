/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.smithing;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.smithing.SmeltingTask;
import com.rs2.model.task.CycleEventHandler;

public final class SmeltingHandler {
    private static final int[][][] SMELTING_DEFINITIONS = new int[][][]{new int[][]{{2349, 1, 6}, {436, 1}, {438, 1}}, new int[][]{{2351, 15, 13}, {440, 1}}, new int[][]{{2355, 20, 14}, {442, 1}}, new int[][]{{2353, 30, 18}, {440, 1}, {453, 2}}, new int[][]{{2893, 20, 8}, {2892, 1}, {453, 4}}, new int[][]{{2357, 40, 23}, {444, 1}}, new int[][]{{2365, 40, 23}, {446, 1}}, new int[][]{{2359, 50, 30}, {447, 1}, {453, 4}}, new int[][]{{2361, 70, 38}, {449, 1}, {453, 6}}, new int[][]{{2363, 85, 50}, {451, 1}, {453, 8}}};
    private static int[][] SMELTING_BUTTONS = new int[][]{{3987, 2349, 1}, {3986, 2349, 5}, {2807, 2349, 10}, {2414, 2349, 28}, {3991, 2351, 1}, {3990, 2351, 5}, {3989, 2351, 10}, {3988, 2351, 28}, {3995, 2355, 1}, {3994, 2355, 5}, {3993, 2355, 10}, {3992, 2355, 28}, {3999, 2353, 1}, {3998, 2353, 5}, {3997, 2353, 10}, {3996, 2353, 28}, {4003, 2357, 1}, {4002, 2357, 5}, {4001, 2357, 10}, {4000, 2357, 28}, {7441, 2359, 1}, {7440, 2359, 5}, {6397, 2359, 10}, {4158, 2359, 28}, {7446, 2361, 1}, {7444, 2361, 5}, {7443, 2361, 10}, {7442, 2361, 28}, {7450, 2363, 1}, {7449, 2363, 5}, {7448, 2363, 10}, {7447, 2363, 28}};

    public static void handleOreOnFurnace(Player player, int n) {
        int[][][] nArray = SMELTING_DEFINITIONS;
        int n2 = 0;
        while (n2 < 10) {
            int[][] nArray2 = nArray[n2];
            if (n == nArray2[1][0] || nArray2.length > 2 && n == nArray2[2][0]) {
                player.setSelectedSmithingBarItemId(nArray2[0][0]);
                SmeltingHandler.startSmeltingTask(player, 1);
                return;
            }
            ++n2;
        }
    }

    public static int selectBestBotSmeltingBarItemId(Player player) {
        int n = -1;
        int n2 = 9;
        while (n2 >= 0) {
            int n3 = SMELTING_DEFINITIONS[n2][0][0];
            int n4 = SMELTING_DEFINITIONS[n2][0][1];
            if (player.getSkillManager().getCurrentLevels()[13] >= n4) {
                boolean bl;
                n4 = SMELTING_DEFINITIONS[n2][1][0];
                int n5 = SMELTING_DEFINITIONS[n2][1][1];
                int n6 = 0;
                int n7 = 0;
                if (SMELTING_DEFINITIONS[n2].length > 2) {
                    n6 = SMELTING_DEFINITIONS[n2][2][0];
                    n7 = SMELTING_DEFINITIONS[n2][2][1];
                }
                ItemStack itemStack = new ItemStack(n4, n5);
                ItemStack itemStack2 = new ItemStack(n6, n7);
                boolean bl2 = bl = n6 > 0;
                if (player.ownsItemAmount(itemStack.getId(), itemStack.getAmount()) && (!bl || player.ownsItemAmount(itemStack2.getId(), itemStack2.getAmount()))) {
                    n = 0;
                    n2 = 0;
                    int n8 = 0;
                    while (n + n5 + n7 <= 28) {
                        n = (n2 += n5) + (n8 += n7);
                    }
                    ItemStack[] itemStackArray = bl ? new ItemStack[]{new ItemStack(n4, n2), new ItemStack(n6, n8)} : new ItemStack[]{new ItemStack(n4, n2)};
                    player.botTaskRequiredItems = itemStackArray;
                    player.botTaskItemId = n3;
                    n = n3;
                    break;
                }
            }
            --n2;
        }
        return n;
    }

    public static int getCoalRequiredForBar(int n) {
        int n2 = 0;
        int n3 = 9;
        while (n3 >= 0) {
            int n4 = SMELTING_DEFINITIONS[n3][0][0];
            if (n == n4) {
                n4 = 0;
                int n5 = 0;
                if (SMELTING_DEFINITIONS[n3].length > 2) {
                    n4 = SMELTING_DEFINITIONS[n3][2][0];
                    n5 = SMELTING_DEFINITIONS[n3][2][1];
                }
                if (n4 == 453) {
                    n2 = n5;
                }
            }
            --n3;
        }
        return n2;
    }

    public static void prepareBotSmeltingRequirements(Player player, int n) {
        int n2 = 9;
        while (n2 >= 0) {
            int n3 = SMELTING_DEFINITIONS[n2][0][0];
            if (n == n3) {
                n = SMELTING_DEFINITIONS[n2][1][0];
                int n4 = SMELTING_DEFINITIONS[n2][1][1];
                int n5 = 0;
                int n6 = 0;
                if (SMELTING_DEFINITIONS[n2].length > 2) {
                    n5 = SMELTING_DEFINITIONS[n2][2][0];
                    n6 = SMELTING_DEFINITIONS[n2][2][1];
                }
                ItemStack itemStack = new ItemStack(n, n4);
                ItemStack itemStack2 = new ItemStack(n5, n6);
                boolean bl = n5 > 0;
                int n7 = 0;
                int n8 = 0;
                int n9 = 0;
                while (n7 + n4 + n6 <= 28) {
                    n7 = (n8 += n4) + (n9 += n6);
                }
                ItemStack[] itemStackArray = bl ? new ItemStack[]{new ItemStack(n, n8), new ItemStack(n5, n9)} : new ItemStack[]{new ItemStack(n, n8)};
                player.botTaskRequiredItems = itemStackArray;
                player.botTaskItemId = n3;
                if (!player.getInventoryManager().containsItemAmount(itemStack.getId(), itemStack.getAmount()) || bl && !player.getInventoryManager().containsItemAmount(itemStack2.getId(), itemStack2.getAmount())) {
                    player.currentBotTask.startWalkToBank(player);
                    return;
                }
                return;
            }
            --n2;
        }
    }

    public static void openSmeltingInterface(Player player) {
        if (!ServerSettings.smithingEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (player.botEnabled) {
            player.setSelectedSmithingBarItemId(player.botTaskItemId);
            SmeltingHandler.startSmeltingTask(player, 28);
            return;
        }
        String string = "smelt";
        Player player3 = player;
        player.interfaceAction = string;
        player3 = player;
        player3.packetSender.sendInterfaceModel(2405, 150, 2349);
        player3 = player;
        player3.packetSender.sendInterfaceModel(2406, 150, 2351);
        player3 = player;
        player3.packetSender.sendInterfaceModel(2407, 150, 2355);
        player3 = player;
        player3.packetSender.sendInterfaceModel(2409, 150, 2353);
        player3 = player;
        player3.packetSender.sendInterfaceModel(2410, 150, 2357);
        player3 = player;
        player3.packetSender.sendInterfaceModel(2411, 150, 2359);
        player3 = player;
        player3.packetSender.sendInterfaceModel(2412, 150, 2361);
        player3 = player;
        player3.packetSender.sendInterfaceModel(2413, 150, 2363);
        player3 = player;
        player3.packetSender.showChatboxInterface(2400);
    }

    public static boolean handleSmeltingButton(Player player, int n, int n2) {
        if (player.interfaceAction != "smelt") {
            return false;
        }
        int[][] nArray = SMELTING_BUTTONS;
        int n3 = 0;
        while (n3 < 32) {
            int[] buttonDefinition = nArray[n3];
            if (n == buttonDefinition[0]) {
                player.setSelectedSmithingBarItemId(buttonDefinition[1]);
                if (buttonDefinition[2] == 28 && n2 <= 0) {
                    player.packetSender.sendEnterInputPrompt(n);
                    return true;
                }
                SmeltingHandler.startSmeltingTask(player, n2 <= 0 ? buttonDefinition[2] : n2);
                return true;
            }
            ++n3;
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    private static boolean startSmeltingTask(Player player, int n) {
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        String string = "";
        player2 = player;
        player.interfaceAction = string;
        int n2 = player.getSelectedSmithingBarItemId();
        int n3 = 0;
        while (n3 < 10) {
            if (n2 == SMELTING_DEFINITIONS[n3][0][0]) {
                if (!ServerSettings.smithingEnabled) {
                    player2 = player;
                    player2.packetSender.sendGameMessage("This skill is currently disabled.");
                    return false;
                }
                int n4 = SMELTING_DEFINITIONS[n3][0][1];
                int n5 = SMELTING_DEFINITIONS[n3][0][2];
                int n6 = SMELTING_DEFINITIONS[n3][1][0];
                int n7 = SMELTING_DEFINITIONS[n3][1][1];
                int secondaryOreId = 0;
                int secondaryOreAmount = 0;
                if (SMELTING_DEFINITIONS[n3].length > 2) {
                    secondaryOreId = SMELTING_DEFINITIONS[n3][2][0];
                    secondaryOreAmount = SMELTING_DEFINITIONS[n3][2][1];
                }
                if (!SkillActionHelper.checkSkillRequirement(player, 13, n4, "smelt this bar")) {
                    if (player.botEnabled) {
                        player.currentBotTask.startWalkToBank(player);
                    }
                    return true;
                }
                n3 = player.nextActionSequence();
                ItemStack itemStack = new ItemStack(n6, n7);
                ItemStack itemStack2 = new ItemStack(secondaryOreId, secondaryOreAmount);
                ItemStack itemStack3 = new ItemStack(n2);
                boolean bl = secondaryOreId > 0;
                if (!player.getInventoryManager().containsItemStack(itemStack) || bl && !player.getInventoryManager().containsItemStack(itemStack2)) {
                    Player player3 = player;
                    player3.packetSender.sendGameMessage("You have run out of ore to smith!");
                    if (player.botEnabled) {
                        player.currentBotTask.startWalkToBank(player);
                    }
                    return true;
                }
                if (player.getQuestState(0) != 1) {
                    player.getDialogueManager().showOneLineStatement("You smelt the " + itemStack.getDefinition().getName().toLowerCase() + " " + (bl ? "and " + itemStack2.getDefinition().getName().toLowerCase() + " together " : "") + "in the furnace.");
                    player.setInteractionTargetId(0);
                } else if (itemStack3.getId() == 2365) {
                    Player player4 = player;
                    player4.packetSender.sendGameMessage("You place a lump of gold in the furnace.");
                } else {
                    Player player5 = player;
                    player5.packetSender.sendGameMessage("You smelt the " + itemStack.getDefinition().getName().toLowerCase() + " " + (bl ? "and " + itemStack2.getDefinition().getName().toLowerCase() + " together " : "") + "in the furnace.");
                }
                player.getUpdateState().setAnimation(899);
                Player player6 = player;
                player6.packetSender.sendSoundEffect(469, 1, 0);
                player.setActionLocked(true);
                player.setActiveCycleEvent(new SmeltingTask(n, player, n3, itemStack, bl, itemStack2, n2, itemStack3, n5));
                CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 1);
                return true;
            }
            ++n3;
        }
        return false;
    }
}

