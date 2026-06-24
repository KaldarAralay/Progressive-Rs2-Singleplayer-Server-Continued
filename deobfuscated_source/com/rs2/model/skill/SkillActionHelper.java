/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill;

import com.rs2.ServerSettings;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import java.util.Random;

public final class SkillActionHelper {
    private static String[] skillNames;

    static {
        new Random(System.currentTimeMillis());
        int[] nArray = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        skillNames = new String[]{"Attack", "Defence", "Strength", "Hitpoints", "Range", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting"};
    }

    public static boolean checkSkillRequirement(Player player, int n, int n2, String string) {
        if (player.getSkillManager().getBaseLevel(n) < n2) {
            player.getDialogueManager().showOneLineStatement("You need a " + skillNames[n] + " level of " + n2 + " to " + string + ".");
            return false;
        }
        return true;
    }

    public static boolean isObjectPresent(int n, int n2, int n3, int n4) {
        return SkillActionHelper.findWorldObjectById(n, n2, n3, n4) != null;
    }

    public static WorldObject findWorldObjectById(int n, int n2, int n3, int n4) {
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectByIdAt(n, n2, n3, n4);
        if (dynamicObject != null) {
            return dynamicObject.getWorldObject();
        }
        ObjectManager.getInstance();
        dynamicObject = ObjectManager.findDynamicObjectByIdAt(ServerSettings.placeholderObjectId, n2, n3, n4);
        if (dynamicObject != null) {
            return null;
        }
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n2, n3, n4);
        if (loadedWorldObject != null) {
            return loadedWorldObject.getWorldObject();
        }
        return null;
    }

    public static WorldObject findWorldObjectAt(int n, int n2, int n3) {
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n, n2, n3);
        if (dynamicObject != null) {
            return dynamicObject.getWorldObject();
        }
        ObjectManager.getInstance();
        dynamicObject = ObjectManager.findDynamicObjectByIdAt(ServerSettings.placeholderObjectId, n, n2, n3);
        if (dynamicObject != null) {
            return null;
        }
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectAt(n, n2, n3);
        if (loadedWorldObject != null) {
            return loadedWorldObject.getWorldObject();
        }
        return null;
    }

    public static int getObjectOrientation(int n, int n2, int n3, int n4) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n2, n3, n4);
        if (loadedWorldObject != null) {
            return loadedWorldObject.getOrientation();
        }
        return 0;
    }

    public static int getObjectType(int n, int n2, int n3, int n4) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n2, n3, n4);
        if (loadedWorldObject != null) {
            return loadedWorldObject.getType();
        }
        return 10;
    }

    public static boolean shouldTriggerRandomEvent(Player player) {
        if (player.H != null) {
            return false;
        }
        if (ServerSettings.randomEventsMode != 0) {
            return false;
        }
        return GameUtil.randomInclusive(2000) == 0;
    }
}

