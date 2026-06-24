/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.runecrafting;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.runecrafting.RuneDefinition;
import com.rs2.model.skill.runecrafting.RunecraftingAltarDefinition;
import com.rs2.util.GameUtil;

public final class RunecraftingHandler {
    public static int PURE_ESSENCE_ITEM_ID = 7936;
    private static int[] SCRYING_ORB_NPC_IDS = new int[]{171, 300, 462, 553, 844};

    public static void craftRunesAtAltar(Player player, RuneDefinition runeDefinition) {
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return;
        }
        if (!SkillActionHelper.checkSkillRequirement(player, 20, runeDefinition.getRequiredLevel(), "craft this rune")) {
            return;
        }
        int n = player.getInventoryManager().getItemAmount(1436);
        int n2 = player.getInventoryManager().getItemAmount(PURE_ESSENCE_ITEM_ID);
        double d = runeDefinition.getMultipleRunesLevelInterval() < 0 ? 0 : player.getSkillManager().getBaseLevel(20) / runeDefinition.getMultipleRunesLevelInterval();
        int n3 = (int)Math.floor(d) + 1;
        if (runeDefinition.getRuneItemId() >= 560) {
            if (n2 <= 0) {
                if (PURE_ESSENCE_ITEM_ID != 1436) {
                    player.packetSender.sendGameMessage("You need pure essence to make these kinds of runes.");
                } else {
                    player.packetSender.sendGameMessage("You need rune essence to make runes.");
                }
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return;
            }
            player.getInventoryManager().removeItem(new ItemStack(PURE_ESSENCE_ITEM_ID, n2));
            player.getInventoryManager().addItem(new ItemStack(runeDefinition.getRuneItemId(), n2 * n3));
            player.getSkillManager().addExperience(20, runeDefinition.getExperience() * (double)n2);
        } else {
            if (n2 <= 0 && n <= 0) {
                if (PURE_ESSENCE_ITEM_ID != 1436) {
                    player.packetSender.sendGameMessage("You need rune or pure essence to make these kinds of runes.");
                } else {
                    player.packetSender.sendGameMessage("You need rune essence to make runes.");
                }
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return;
            }
            int n4 = 0;
            while (n4 < 28) {
                player.getInventoryManager().removeItem(new ItemStack(1436, n));
                if (PURE_ESSENCE_ITEM_ID != 1436) {
                    player.getInventoryManager().removeItem(new ItemStack(PURE_ESSENCE_ITEM_ID, n2));
                }
                ++n4;
            }
            player.getInventoryManager().addItem(new ItemStack(runeDefinition.getRuneItemId(), n * n3));
            player.getSkillManager().addExperience(20, runeDefinition.getExperience() * (double)n);
            if (PURE_ESSENCE_ITEM_ID != 1436) {
                player.getInventoryManager().addItem(new ItemStack(runeDefinition.getRuneItemId(), n2 * n3));
                player.getSkillManager().addExperience(20, runeDefinition.getExperience() * (double)n2);
            }
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
        }
        player.packetSender.sendSoundEffect(481, 1, 0);
        player.getUpdateState().setAnimation(791);
        player.getUpdateState().setGraphicHeight100(186);
    }

    public static void recordScryingOrbTeleport(Player player) {
        int n;
        int n2;
        int n3;
        block6: {
            n3 = player.getAbyssMageNpcId();
            int n4 = 0;
            while (n4 < SCRYING_ORB_NPC_IDS.length) {
                n2 = SCRYING_ORB_NPC_IDS[n4];
                if (n3 == n2) {
                    n = n4;
                    break block6;
                }
                ++n4;
            }
            n = -1;
        }
        n3 = 1 + n;
        ItemStack itemStack = player.getInventoryManager().getContainer().findFlatItem(5519);
        n2 = itemStack.getMetadata();
        if ((n2 & GameUtil.bitFlag(n3)) == 0) {
            itemStack.setMetadata(n2 += GameUtil.bitFlag(n3));
        }
        Player player2 = player;
        n2 = 0;
        int n5 = itemStack.getMetadata();
        int n6 = 0;
        while (n6 < SCRYING_ORB_NPC_IDS.length) {
            int n7 = n6 + 1;
            if ((n5 & GameUtil.bitFlag(n7)) != 0) {
                ++n2;
            }
            ++n6;
        }
        if (n2 >= 3) {
            player2.getInventoryManager().removeItem(new ItemStack(5519));
            player2.getInventoryManager().addItem(new ItemStack(5518));
            Player player3 = player2;
            player3.packetSender.sendGameMessage("Your scrying orb has absorbed enough teleport information.");
        }
    }

    public static void startAbyssMageTeleport(Player player, Npc npc) {
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return;
        }
        player.setAbyssMageNpcId(npc.getNpcId());
        npc.startAbyssMageTeleport(player, 2911, 4832, 0, "Senventior disthine molenko!");
    }

    public static boolean locateTalismanDirection(Player player, int n) {
        RunecraftingAltarDefinition altarDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.talismanItemId) {
                altarDefinition = candidate;
                break;
            }
        }
        if (altarDefinition == null) {
            return false;
        }
        int n2 = altarDefinition.ruinsPosition.getY();
        int n4 = altarDefinition.ruinsPosition.getX();
        String string = "";
        String string2 = "";
        if (player.getPosition().getX() >= n4) {
            string = "west";
        }
        if (player.getPosition().getY() > n2) {
            string2 = "South";
        }
        if (player.getPosition().getX() < n4) {
            string = "east";
        }
        if (player.getPosition().getY() <= n2) {
            string2 = "North";
        }
        player.packetSender.sendGameMessage("You feel a slight pull towards " + string2 + "-" + string + "...");
        return true;
    }

}

