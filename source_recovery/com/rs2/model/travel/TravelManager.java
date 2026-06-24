/*
 * Source recovered from CFR output plus javap bytecode for handleGnomeGliderButton.
 */
package com.rs2.model.travel;

import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.task.TickTask;
import com.rs2.model.travel.GnomeGliderDestination;
import com.rs2.model.travel.GnomeGliderLandingTask;
import com.rs2.model.travel.GnomeGliderTravelTask;
import com.rs2.model.travel.HajedyCartRoute;
import com.rs2.model.travel.ShipRoute;
import com.rs2.model.travel.ShipTravelArrivalTask;
import com.rs2.model.travel.TravelBootstrap;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public class TravelManager {
    private int a;
    private int b;

    public static boolean handleGnomeGliderButton(Player player, int buttonId) {
        for (GnomeGliderDestination destination : GnomeGliderDestination.values()) {
            if (buttonId != destination.buttonId) {
                continue;
            }
            if (!player.getInteractionTarget().isNpc()) {
                return false;
            }
            Npc gliderNpc = (Npc)player.getInteractionTarget();
            if (player.getInteractionTarget() != null) {
                player.packetSender.showInterface(802);
                int targetRouteIndex = destination.routeIndex;
                int currentRouteIndex = getCurrentGnomeGliderRouteIndex(gliderNpc);
                int routeConfig = getGnomeGliderRouteConfig(currentRouteIndex, targetRouteIndex);
                if (routeConfig <= 0) {
                    player.packetSender.sendGameMessage("You can't fly there from here.");
                } else if (routeConfig == 20) {
                    player.packetSender.sendGameMessage("You can't fly to the same place you are at.");
                } else if (routeConfig == 25) {
                    player.packetSender.sendGameMessage("You can't fly to that place yet.");
                } else {
                    player.packetSender.sendConfig(153, routeConfig);
                    player.setActionLocked(true);
                    CycleEventHandler.getInstance().schedule(player, new GnomeGliderTravelTask(player, destination), 3);
                    CycleEventHandler.getInstance().schedule(player, new GnomeGliderLandingTask(player), 4);
                }
            }
            return true;
        }
        return false;
    }

    private static int getCurrentGnomeGliderRouteIndex(Npc gliderNpc) {
        int npcId = gliderNpc.getNpcId();
        int regionId = GameUtil.getRegionId(gliderNpc.getPosition().getX(), gliderNpc.getPosition().getY());
        if (npcId == 3810 || regionId == 11318) {
            return 0;
        }
        if (npcId == 3811 || regionId == 9782) {
            return 1;
        }
        if (npcId == 3809 || regionId == 13106) {
            return 3;
        }
        if (npcId == 3812 || regionId == 11822) {
            return 4;
        }
        if (npcId == 1800 || regionId == 10030) {
            return 5;
        }
        return 0;
    }

    private static int getGnomeGliderRouteConfig(int currentRouteIndex, int targetRouteIndex) {
        if (currentRouteIndex == targetRouteIndex) {
            return 20;
        }
        switch (currentRouteIndex) {
            case 0:
                return targetRouteIndex == 1 ? 2 : 0;
            case 1:
                if (targetRouteIndex == 0) {
                    return 1;
                }
                if (targetRouteIndex == 2) {
                    return 3;
                }
                if (targetRouteIndex == 3) {
                    return 4;
                }
                if (targetRouteIndex == 4) {
                    return 7;
                }
                return targetRouteIndex == 5 ? 25 : 0;
            case 3:
                return targetRouteIndex == 1 ? 5 : 0;
            case 4:
                return targetRouteIndex == 1 ? 6 : 0;
            case 5:
                return targetRouteIndex == 1 ? 11 : 0;
            default:
                return 0;
        }
    }

    public TravelManager(TravelBootstrap travelBootstrap) {
        new ArrayList();
        this.a = 20;
        this.b = 5;
    }

    public static boolean handleShipRoute(Player object, ShipRoute shipRoute) {
        int n = shipRoute.fareCoins;
        if (shipRoute == ShipRoute.RELLEKKA_TO_WATERBIRTH && ((Player)object).getQuestState(40) == 1) {
            n = 0;
        }
        if (n > 0) {
            ItemStack itemStack = new ItemStack(995, n);
            if (!((Player)object).getInventoryManager().containsItemStack(itemStack)) {
                if (!((Player)object).botEnabled) {
                    ((Player)object).getDialogueManager().showPlayerOneLineDialogue("Sorry, I don't have enough coins for that.", 599);
                } else {
                    ((Player)object).currentBotTask.startWalkToBank((Player)object);
                }
                ((Player)object).getDialogueManager().finishDialogue();
                return false;
            }
            ((Player)object).getInventoryManager().removeItem(itemStack);
        }
        if (shipRoute == ShipRoute.PORT_SARIM_TO_ENTRANA && ((Player)object).hasRestrictedCombatEquipment()) {
            DialogueManager.continueDialogue((Player)object, 657, 4, 0);
            return false;
        }
        ((Player)object).setActionLocked(true);
        Object object2 = ((Player)object).botEnabled || shipRoute.interfaceDestinationPosition == null ? shipRoute.destinationPosition.copy() : shipRoute.interfaceDestinationPosition.copy();
        ((Player)object).moveTo((Position)object2);
        if (shipRoute.ordinal() < 16) {
            object2 = object;
            ((Player)object2).packetSender.sendConfig(75, shipRoute.ordinal() + 1);
            object2 = object;
            ((Player)object2).packetSender.showInterface(3281);
        } else {
            object2 = object;
            ((Player)object2).packetSender.showInterface(11092);
        }
        object2 = object;
        ((Player)object2).packetSender.sendMusicJingle(shipRoute.jingleDelayTicks > 250 ? 225 : 298, shipRoute.jingleDelayTicks);
        object2 = object;
        ((Player)object2).packetSender.sendMinimapState(2);
        TickTask arrivalTask = new ShipTravelArrivalTask(shipRoute.arrivalDelayTicks, shipRoute, (Player)object);
        World.getTaskScheduler().schedule(arrivalTask);
        return true;
    }

    public static boolean handleHajedyCartRoute(Player player, HajedyCartRoute hajedyCartRoute) {
        if (hajedyCartRoute.fareCoins > 0) {
            ItemStack itemStack = new ItemStack(995, hajedyCartRoute.fareCoins);
            if (!player.getInventoryManager().containsItemStack(itemStack)) {
                player.getDialogueManager().showPlayerOneLineDialogue("Sorry, I don't have enough coins for that.", 599);
                player.getDialogueManager().finishDialogue();
                return false;
            }
            player.getInventoryManager().removeItem(itemStack);
        }
        player.scheduleDelayedMove(hajedyCartRoute.destination.copy());
        return true;
    }
}
