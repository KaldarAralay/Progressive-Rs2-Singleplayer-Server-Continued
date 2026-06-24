/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.abyss;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.gameplay.abyss.AbyssDelayedMoveEvent;
import com.rs2.model.gameplay.abyss.AbyssObstacleEvent;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import java.util.Random;

public final class AbyssManager {
    private static int[] POUCH_ITEM_IDS = new int[]{5509, 5510, 5512, 5514};

    private static int getMissingPouchItemId(Player player) {
        int[] nArray = POUCH_ITEM_IDS;
        int n = POUCH_ITEM_IDS.length;
        int n2 = 0;
        while (n2 < n) {
            int n3 = nArray[n2];
            if (!player.getInventoryManager().containsItemInInventoryOrBank(n3)) {
                return n3;
            }
            ++n2;
        }
        return -1;
    }

    public static void rollAbyssPouchDrop(Player player, Npc npc) {
        int missingPouchItemId = AbyssManager.getMissingPouchItemId(player);
        if (missingPouchItemId == -1) {
            return;
        }
        if ((npc.getDefinition().getName().equalsIgnoreCase("leech") || npc.getDefinition().getName().equalsIgnoreCase("guardian") || npc.getDefinition().getName().equalsIgnoreCase("walker")) && GameUtil.randomInclusive(100) == 5) {
            GroundItem groundItem = new GroundItem(new ItemStack(missingPouchItemId), player, npc.getPosition());
            GroundItemManager.getInstance().spawn(groundItem);
        }
    }

    public static boolean handleAbyssObjectAction(Player player, int n, int n2, int n3) {
        switch (n) {
            case 7145: {
                AbyssManager.attemptBurnBlockadeObstacle(player, n2, n3, 3024, 4833);
                return true;
            }
            case 7151: {
                AbyssManager.attemptBurnBlockadeObstacle(player, n2, n3, 3053, 4830);
                return true;
            }
            case 7143: {
                AbyssManager.attemptMineRockObstacle(player, n2, n3, 3030, 4821);
                return true;
            }
            case 7153: {
                AbyssManager.attemptMineRockObstacle(player, n2, n3, 3048, 4822);
                return true;
            }
            case 7152: {
                AbyssManager.attemptChopTendrilsObstacle(player, n2, n3, 3050, 4824);
                return true;
            }
            case 7144: {
                AbyssManager.attemptChopTendrilsObstacle(player, n2, n3, 3028, 4824);
                return true;
            }
            case 7149: {
                AbyssManager.attemptCrossGapObstacle(player, n2, n3, 3048, 4842);
                return true;
            }
            case 7147: {
                AbyssManager.attemptCrossGapObstacle(player, n2, n3, 3031, 4842);
                return true;
            }
            case 7146: {
                AbyssManager.attemptDistractEyesObstacle(player, n2, n3, 3029, 4841);
                return true;
            }
            case 7150: {
                AbyssManager.attemptDistractEyesObstacle(player, n2, n3, 3051, 4838);
                return true;
            }
            case 7148: {
                player.moveTo(new Position(3040, 4844, player.getPosition().getPlane()));
                return true;
            }
        }
        return false;
    }

    private static void startObstacleAttempt(Player player, int n, int n2, int n3, int n4, String string, int n5, int n6, int n7, boolean bl) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectAt(n, n2, player.getPosition().getPlane());
        if (!bl) {
            return;
        }
        String string2 = string;
        Player player2 = player;
        if (string2 == "chopTendrils") {
            Player player3 = player2;
            player3.packetSender.sendGameMessage("You attempt to chop your way through...");
        } else if (string2 == "mineRock") {
            Player player4 = player2;
            player4.packetSender.sendGameMessage("You attempt to mine your way through...");
        } else if (string2 == "crossGap") {
            Player player5 = player2;
            player5.packetSender.sendGameMessage("You attempt to squeeze through the narrow gap...");
        } else if (string2 == "blockade") {
            Player player6 = player2;
            player6.packetSender.sendGameMessage("You attempt to set the blockade on fire...");
        } else if (string2 == "distractEye") {
            Player player7 = player2;
            player7.packetSender.sendGameMessage("You use your thieving skills to misdirect the eyes...");
        }
        AbyssManager.playObstacleAttemptAnimation(player2, string2);
        player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(player, new AbyssObstacleEvent(n7, player, string, n5, n, n2, loadedWorldObject, n6, n3, n4), 3);
    }

    public static void sendObstacleSuccessMessage(Player player, String object) {
        if (object == "chopTendrils") {
            player.packetSender.sendGameMessage("...and manage to chop down the tendrils.");
            return;
        }
        if (object == "mineRock") {
            player.packetSender.sendGameMessage("...and manage to break through the rock.");
            return;
        }
        if (object == "crossGap") {
            player.packetSender.sendGameMessage("...and you manage to crawl through.");
            player.getUpdateState().setAnimation(1332);
            return;
        }
        if (object == "blockade") {
            player.packetSender.sendGameMessage("...and manage to burn it down and get past.");
            return;
        }
        if (object == "distractEye") {
            player.packetSender.sendGameMessage("...and sneak past while they're not looking.");
        }
    }

    private static void playObstacleAttemptAnimation(Player player, String object) {
        if (object == "mineRock") {
            player.getUpdateState().setAnimation(ItemCombinationHandler.findUsableGatheringTool(player, 14).getGatherAnimationId());
            return;
        }
        if (object == "chopTendrils") {
            player.getUpdateState().setAnimation(ItemCombinationHandler.findUsableGatheringTool(player, 8).getGatherAnimationId());
            return;
        }
        if (object == "blockade") {
            player.getUpdateState().setAnimation(733);
            return;
        }
        if (object == "distractEye") {
            int[] animations = new int[]{855, 856, 857, 858, 859, 860, 861, 862, 863, 864, 865, 866, 2113, 2109, 2111, 2106, 2107, 2108, 1368, 2105, 2110, 2112, 2127, 2128, 1131, 1130, 1129, 1128, 1745, 3544, 3543, 2836};
            int n = new Random().nextInt(32);
            player.getUpdateState().setAnimation(animations[n]);
            return;
        }
        if (object == "crossGap") {
            player.getUpdateState().setAnimation(1331);
        }
    }

    private static void attemptMineRockObstacle(Player player, int n, int n2, int n3, int n4) {
        boolean bl = true;
        if (ItemCombinationHandler.findUsableGatheringTool(player, 14) == null) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("You need a pickaxe to mine here.");
            bl = false;
        }
        AbyssManager.startObstacleAttempt(player, n, n2, n3, n4, "mineRock", 7159, 7160, 2, bl);
    }

    private static void attemptChopTendrilsObstacle(Player player, int n, int n2, int n3, int n4) {
        n4 = 1;
        if (ItemCombinationHandler.findUsableGatheringTool(player, 8) == null) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("You need an axe to chop these.");
            n4 = 0;
        }
        AbyssManager.startObstacleAttempt(player, n, n2, n3, 4824, "chopTendrils", -1, 7163, 1, n4 != 0);
    }

    private static void attemptBurnBlockadeObstacle(Player player, int n, int n2, int n3, int n4) {
        boolean bl = true;
        if (!player.getInventoryManager().containsItem(590)) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("You don't have a tinderbox to burn it.");
            bl = false;
        }
        AbyssManager.startObstacleAttempt(player, n, n2, n3, n4, "blockade", -1, 7167, 1, bl);
    }

    private static void attemptDistractEyesObstacle(Player player, int n, int n2, int n3, int n4) {
        AbyssManager.startObstacleAttempt(player, n, n2, n3, n4, "distractEye", 7169, 7170, 2, true);
    }

    private static void attemptCrossGapObstacle(Player player, int n, int n2, int n3, int n4) {
        AbyssManager.startObstacleAttempt(player, n, n2, n3, 4842, "crossGap", -1, -1, 0, true);
    }

    public static void startAbyssMageTeleport(Player player, Npc npc) {
        int[] nArray = new int[]{3016, 3016, 3021, 3039, 3058, 3063, 3058, 3034};
        int[] nArray2 = new int[]{4848, 4831, 4815, 4806, 4812, 4827, 4848, 4854};
        int n = new Random().nextInt(8);
        player.setAbyssMageNpcId(npc.getNpcId());
        npc.startDialogueTeleport(player, nArray[n], nArray2[n], 0, "Veniens! Sallakar! Rinnesset!");
        player.addPvpCombatReference(player, 600);
        if (player.getSkillManager().getCurrentLevels()[5] > 0) {
            player.getSkillManager().getCurrentLevels()[5] = 0;
            player.getSkillManager().refreshSkill(5);
            player.packetSender.sendGameMessage("You feel your prayer drain.");
        }
    }

    static /* synthetic */ void replayObstacleAttemptAnimation(Player player, String string) {
        AbyssManager.playObstacleAttemptAnimation(player, string);
    }

    static /* synthetic */ void sendObstacleFailureMessage(Player player, String string) {
        Player player2;
        if (string == "mineRock") {
            player2 = player;
            player2.packetSender.sendGameMessage("...but fail to break-up the rock.");
        }
        if (string == "chopTendrils") {
            player2 = player;
            player2.packetSender.sendGameMessage("You fail to cut through the tendrils.");
        }
        if (string == "crossGap") {
            player2 = player;
            player2.packetSender.sendGameMessage("You are not agile enough to squeeze through the narrow gap.");
        }
        if (string == "blockade") {
            player2 = player;
            player2.packetSender.sendGameMessage("You fail to set it on fire.");
        }
        if (string == "distractEye") {
            player2 = player;
            player2.packetSender.sendGameMessage("You fail to distract the eyes.");
        }
    }

    static /* synthetic */ void scheduleDelayedObstacleMove(Player player, int n, int n2) {
        player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(player, new AbyssDelayedMoveEvent(player, n, n2), 3);
    }
}

