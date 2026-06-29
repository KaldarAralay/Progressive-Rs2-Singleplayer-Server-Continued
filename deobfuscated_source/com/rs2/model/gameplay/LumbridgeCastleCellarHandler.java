/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay;

import com.rs2.model.Position;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;

public final class LumbridgeCastleCellarHandler {
    private static final int KITCHEN_CLOSED_TRAPDOOR = 1568;
    private static final int KITCHEN_CLOSED_TRAPDOOR_LEVEL_1 = 1569;
    private static final int KITCHEN_OPEN_TRAPDOOR = 1570;
    private static final int KITCHEN_OPEN_TRAPDOOR_LEVEL_1 = 1571;
    private static final int CELLAR_LADDER = 1755;
    private static final int KITCHEN_TRAPDOOR_TYPE = 22;
    private static final int KITCHEN_TRAPDOOR_ORIENTATION = 0;
    private static final Position KITCHEN_TRAPDOOR_POSITION = new Position(3209, 3216, 0);
    private static final int KITCHEN_MIN_X = 3205;
    private static final int KITCHEN_MAX_X = 3212;
    private static final int KITCHEN_MIN_Y = 3212;
    private static final int KITCHEN_MAX_Y = 3221;
    private static final String TRAPDOOR_NAME = "trapdoor";
    private static final String CLOSED_TRAPDOOR_DESCRIPTION = "I wonder what's under it?";
    private static final String OPEN_TRAPDOOR_DESCRIPTION = "I wonder what's down there?";
    private static final Position CELLAR_LADDER_POSITION = new Position(3209, 9616, 0);
    private static final Position CELLAR_ENTRY_POSITION = new Position(3209, 9616, 0);
    private static final Position KITCHEN_EXIT_POSITION = new Position(3209, 3216, 0);

    private LumbridgeCastleCellarHandler() {
    }

    public static boolean handleFirstObjectAction(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        if (LumbridgeCastleCellarHandler.isKitchenClosedTrapdoor(player, objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.openKitchenTrapdoor(player, objectId, objectX, objectY, objectPlane);
            return true;
        }
        if (LumbridgeCastleCellarHandler.isKitchenOpenTrapdoor(player, objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.climbDownKitchenLadder(player);
            return true;
        }
        if (LumbridgeCastleCellarHandler.isCellarLadder(objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.climbUpCellarLadder(player);
            return true;
        }
        return false;
    }

    public static boolean handleSecondObjectAction(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        if (LumbridgeCastleCellarHandler.isKitchenClosedTrapdoor(player, objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.openKitchenTrapdoor(player, objectId, objectX, objectY, objectPlane);
            return true;
        }
        if (LumbridgeCastleCellarHandler.isKitchenOpenTrapdoor(player, objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.closeKitchenTrapdoor(player, objectId, objectX, objectY, objectPlane);
            return true;
        }
        if (LumbridgeCastleCellarHandler.isCellarLadder(objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.climbUpCellarLadder(player);
            return true;
        }
        return false;
    }

    public static boolean handleThirdObjectAction(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        return LumbridgeCastleCellarHandler.handleClimbOrOpenAction(player, objectId, objectX, objectY, objectPlane);
    }

    public static boolean handleFourthObjectAction(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        return LumbridgeCastleCellarHandler.handleClimbOrOpenAction(player, objectId, objectX, objectY, objectPlane);
    }

    private static boolean handleClimbOrOpenAction(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        if (LumbridgeCastleCellarHandler.isKitchenClosedTrapdoor(player, objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.openKitchenTrapdoor(player, objectId, objectX, objectY, objectPlane);
            return true;
        }
        if (LumbridgeCastleCellarHandler.isKitchenOpenTrapdoor(player, objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.climbDownKitchenLadder(player);
            return true;
        }
        if (LumbridgeCastleCellarHandler.isCellarLadder(objectId, objectX, objectY, objectPlane)) {
            LumbridgeCastleCellarHandler.climbUpCellarLadder(player);
            return true;
        }
        return false;
    }

    private static void openKitchenTrapdoor(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        player.packetSender.sendGameMessage("You open the trapdoor.");
        int openedObjectId = objectId == KITCHEN_CLOSED_TRAPDOOR_LEVEL_1 ? KITCHEN_OPEN_TRAPDOOR_LEVEL_1 : KITCHEN_OPEN_TRAPDOOR;
        WorldObject worldObject = SkillActionHelper.findWorldObjectById(objectId, objectX, objectY, objectPlane);
        if (worldObject != null) {
            AttackStyleDefinition.toggleObjectAfterAnimation(player, objectId, openedObjectId, worldObject);
            return;
        }
        player.getUpdateState().setAnimation(827);
        new DynamicObject(openedObjectId, objectX, objectY, objectPlane, KITCHEN_TRAPDOOR_ORIENTATION, KITCHEN_TRAPDOOR_TYPE, objectId, 999999999, false);
    }

    private static void closeKitchenTrapdoor(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        player.packetSender.sendGameMessage("You close the trapdoor.");
        int closedObjectId = objectId == KITCHEN_OPEN_TRAPDOOR_LEVEL_1 ? KITCHEN_CLOSED_TRAPDOOR_LEVEL_1 : KITCHEN_CLOSED_TRAPDOOR;
        WorldObject worldObject = SkillActionHelper.findWorldObjectById(objectId, objectX, objectY, objectPlane);
        if (worldObject != null) {
            AttackStyleDefinition.toggleObjectAfterAnimation(player, objectId, closedObjectId, worldObject);
            return;
        }
        player.getUpdateState().setAnimation(827);
        new DynamicObject(closedObjectId, objectX, objectY, objectPlane, KITCHEN_TRAPDOOR_ORIENTATION, KITCHEN_TRAPDOOR_TYPE, objectId, 999999999, false);
    }

    private static void climbDownKitchenLadder(Player player) {
        player.packetSender.sendGameMessage("You climb down the ladder.");
        AttackStyleDefinition.startDelayedObjectMove(player, CELLAR_ENTRY_POSITION);
    }

    private static void climbUpCellarLadder(Player player) {
        player.packetSender.sendGameMessage("You climb up the ladder.");
        AttackStyleDefinition.startDelayedObjectMove(player, KITCHEN_EXIT_POSITION);
    }

    private static boolean isKitchenClosedTrapdoor(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        if (!LumbridgeCastleCellarHandler.isLumbridgeKitchenAction(player, objectX, objectY, objectPlane)) {
            return false;
        }
        if (objectId == KITCHEN_CLOSED_TRAPDOOR || objectId == KITCHEN_CLOSED_TRAPDOOR_LEVEL_1) {
            return true;
        }
        return LumbridgeCastleCellarHandler.isTrapdoorDefinition(objectId, CLOSED_TRAPDOOR_DESCRIPTION)
            || LumbridgeCastleCellarHandler.isClosedTrapdoorDefinitionFallback(objectId);
    }

    private static boolean isKitchenOpenTrapdoor(Player player, int objectId, int objectX, int objectY, int objectPlane) {
        if (!LumbridgeCastleCellarHandler.isLumbridgeKitchenAction(player, objectX, objectY, objectPlane)) {
            return false;
        }
        if (objectId == KITCHEN_OPEN_TRAPDOOR || objectId == KITCHEN_OPEN_TRAPDOOR_LEVEL_1) {
            return true;
        }
        return LumbridgeCastleCellarHandler.isTrapdoorDefinition(objectId, OPEN_TRAPDOOR_DESCRIPTION);
    }

    private static boolean isCellarLadder(int objectId, int objectX, int objectY, int objectPlane) {
        return objectId == CELLAR_LADDER && objectX == CELLAR_LADDER_POSITION.getX() && objectY == CELLAR_LADDER_POSITION.getY() && objectPlane == CELLAR_LADDER_POSITION.getPlane();
    }

    private static boolean isInLumbridgeKitchen(int objectX, int objectY, int objectPlane) {
        return objectPlane == KITCHEN_TRAPDOOR_POSITION.getPlane() && objectX >= KITCHEN_MIN_X && objectX <= KITCHEN_MAX_X && objectY >= KITCHEN_MIN_Y && objectY <= KITCHEN_MAX_Y;
    }

    private static boolean isLumbridgeKitchenAction(Player player, int objectX, int objectY, int objectPlane) {
        return LumbridgeCastleCellarHandler.isInLumbridgeKitchen(objectX, objectY, objectPlane) || LumbridgeCastleCellarHandler.isPlayerInLumbridgeKitchen(player);
    }

    private static boolean isPlayerInLumbridgeKitchen(Player player) {
        if (player == null || player.getPosition() == null) {
            return false;
        }
        Position position = player.getPosition();
        return position.getPlane() == KITCHEN_TRAPDOOR_POSITION.getPlane()
            && position.getX() >= KITCHEN_MIN_X - 2
            && position.getX() <= KITCHEN_MAX_X + 2
            && position.getY() >= KITCHEN_MIN_Y - 2
            && position.getY() <= KITCHEN_MAX_Y + 2;
    }

    private static boolean isTrapdoorDefinition(int objectId, String expectedDescription) {
        ObjectDefinition definition = ObjectDefinition.forId(objectId);
        if (definition == null || definition.name == null || definition.description == null) {
            return false;
        }
        return TRAPDOOR_NAME.equalsIgnoreCase(definition.name) && expectedDescription.equalsIgnoreCase(definition.description);
    }

    private static boolean isClosedTrapdoorDefinitionFallback(int objectId) {
        ObjectDefinition definition = ObjectDefinition.forId(objectId);
        if (definition == null || definition.name == null || !TRAPDOOR_NAME.equalsIgnoreCase(definition.name)) {
            return false;
        }
        if (definition.description == null) {
            return true;
        }
        return !OPEN_TRAPDOOR_DESCRIPTION.equalsIgnoreCase(definition.description);
    }
}
