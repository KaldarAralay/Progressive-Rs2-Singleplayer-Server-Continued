/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.runecrafting;

import com.rs2.ServerSettings;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemService;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.runecrafting.MysteriousRuinsTeleportTask;
import com.rs2.model.skill.runecrafting.RuneDefinition;
import com.rs2.model.skill.runecrafting.RunecraftingAltarDefinition;
import com.rs2.model.skill.runecrafting.RunecraftingHandler;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.net.packet.PacketSender;

public class RunecraftingObjectHandler {
    private Player player;
    private int[] toolInteractionNpcIds = new int[]{519, 594};

    public static boolean handleAltarOrAbyssObject(Player player, int n) {
        switch (n) {
            case 2489: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.SOUL_RUNE);
                return true;
            }
            case 2490: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.BLOOD_RUNE);
                return true;
            }
            case 2482: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.FIRE_RUNE);
                return true;
            }
            case 2480: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.WATER_RUNE);
                return true;
            }
            case 2478: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.AIR_RUNE);
                return true;
            }
            case 2481: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.EARTH_RUNE);
                return true;
            }
            case 2479: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.MIND_RUNE);
                return true;
            }
            case 2483: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.BODY_RUNE);
                return true;
            }
            case 2488: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.DEATH_RUNE);
                return true;
            }
            case 2486: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.NATURE_RUNE);
                return true;
            }
            case 2487: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.CHAOS_RUNE);
                return true;
            }
            case 2485: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.LAW_RUNE);
                return true;
            }
            case 2484: {
                RunecraftingHandler.craftRunesAtAltar(player, RuneDefinition.COSMIC_RUNE);
                return true;
            }
            case 7141: {
                player.packetSender.sendGameMessage("This has been disabled temporarily.");
                return true;
            }
            case 7138: {
                player.packetSender.sendGameMessage("This has been disabled temporarily.");
                return true;
            }
            case 7133: {
                player.getTeleportManager().startDelayedTeleport(2400, 4835, 0);
                return true;
            }
            case 7132: {
                n = player.getQuestState(58) == 1 ? 1 : 0;
                if (n == 0) {
                    Object object = QuestDefinition.forId(58);
                    object = ((QuestDefinition)object).getName();
                    player.getDialogueManager().showOneLineStatement("You need to complete " + (String)object + " first.");
                    player.getDialogueManager().finishDialogue();
                } else {
                    player.getTeleportManager().startDelayedTeleport(2142, 4813, 0);
                }
                return true;
            }
            case 7129: {
                player.getTeleportManager().startDelayedTeleport(2574, 4849, 0);
                return true;
            }
            case 7130: {
                player.getTeleportManager().startDelayedTeleport(2655, 4830, 0);
                return true;
            }
            case 7131: {
                player.getTeleportManager().startDelayedTeleport(2523, 4826, 0);
                return true;
            }
            case 7140: {
                player.getTeleportManager().startDelayedTeleport(2793, 4828, 0);
                return true;
            }
            case 7139: {
                player.getTeleportManager().startDelayedTeleport(2841, 4829, 0);
                return true;
            }
            case 7137: {
                player.getTeleportManager().startDelayedTeleport(2726, 4832, 0);
                return true;
            }
            case 7136: {
                if (player.getPlayerRights() < 2) {
                    player.packetSender.sendGameMessage("This has been disabled temporarily.");
                } else {
                    player.getTeleportManager().startDelayedTeleport(2208, 4830, 0);
                }
                return true;
            }
            case 7135: {
                if (player.hasRestrictedCombatEquipment()) {
                    player.getDialogueManager().showOneLineStatement("You cannot bring combat items to Entrana!");
                    player.getDialogueManager().finishDialogue();
                } else {
                    player.getTeleportManager().startDelayedTeleport(2464, 4818, 0);
                }
                return true;
            }
            case 7134: {
                player.getTeleportManager().startDelayedTeleport(2281, 4837, 0);
                return true;
            }
        }
        return false;
    }

    public static boolean handleRuinsOrPortalObject(Player player, int n) {
        RunecraftingAltarDefinition ruinsDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.ruinsObjectId) {
                ruinsDefinition = candidate;
                break;
            }
        }
        RunecraftingAltarDefinition portalDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.portalObjectId) {
                portalDefinition = candidate;
                break;
            }
        }
        if (ruinsDefinition == null && portalDefinition == null) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (ruinsDefinition != null) {
            if (ruinsDefinition.membersOnly && !player.isMember()) {
                player.packetSender.sendGameMessage("You need a members account to access members content.");
                return true;
            }
            if (ruinsDefinition.membersOnly && ServerSettings.freeToPlayWorld) {
                player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                return true;
            }
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return true;
        }
        if (ruinsDefinition != null) {
            player.moveTo(ruinsDefinition.altarPosition);
            player.packetSender.sendGameMessage("You feel a powerful force take hold of you...");
            return true;
        }
        if (portalDefinition != null) {
            player.moveTo(portalDefinition.ruinsPosition);
            player.packetSender.sendGameMessage("You step through the portal...");
            return true;
        }
        return false;
    }

    public static boolean handleTalismanOnMysteriousRuins(Player player, int n, int n2) {
        RunecraftingAltarDefinition altarDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.talismanItemId && n2 == candidate.ruinsObjectId) {
                altarDefinition = candidate;
                break;
            }
        }
        if (altarDefinition == null) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (altarDefinition.membersOnly && !player.isMember()) {
            player.packetSender.sendGameMessage("You need a members account to access members content.");
            return true;
        }
        if (altarDefinition.membersOnly && ServerSettings.freeToPlayWorld) {
            player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return true;
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return true;
        }
        PacketSender packetSender = player.packetSender;
        StringBuilder stringBuilder = new StringBuilder("You hold the ");
        ItemService.getInstance();
        packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(altarDefinition.talismanItemId)).append(" towards the mysterious ruins.").toString());
        player.getUpdateState().setAnimation(827);
        player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(player, new MysteriousRuinsTeleportTask(player, altarDefinition), 2);
        return true;
    }

    public RunecraftingObjectHandler(Player player) {
        this.player = player;
    }

    public boolean handleToolOnNpc(int n, int n2) {
        int n3 = -1;
        int[] nArray = this.toolInteractionNpcIds;
        int n4 = this.toolInteractionNpcIds.length;
        int n5 = 0;
        while (n5 < n4) {
            int n6 = nArray[n5];
            if (n == n6) {
                n3 = n6;
                break;
            }
            ++n5;
        }
        GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.forBrokenToolItemId(n2);
        if (n3 != -1 && gatheringToolDefinition != null) {
            this.player.temporaryActionValue = n2;
            this.player.O = n3;
            DialogueManager.startDialogue(this.player, 10088);
            return true;
        }
        return false;
    }
}

