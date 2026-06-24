/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects.functions;

import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.functions.PickableObjectEvent;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.task.CycleEventHandler;

public final class PickableObjectHandler {
    private static int[][] pickableObjects = new int[][]{{5585, 1947}, {5584, 1947}, {5583, 1947}, {3366, 1957}, {1161, 1965}, {312, 1942}, {313, 1947}, {2646, 1779}};

    public static boolean handlePickableObject(Player player, int n, int n2, int n3) {
        Object object;
        if (!SkillActionHelper.isObjectPresent(n, n2, n3, player.getPosition().getPlane())) {
            return false;
        }
        int n4 = 0;
        int[][] nArray = pickableObjects;
        int n5 = 0;
        while (n5 < 8) {
            int[] pickableObject = nArray[n5];
            if (pickableObject[0] == n) {
                n4 = pickableObject[1];
                break;
            }
            ++n5;
        }
        if (n4 <= 0) {
            return false;
        }
        ItemStack itemStack = new ItemStack(n4);
        object = itemStack;
        String string = itemStack.getDefinition().getName().toLowerCase();
        if (!player.getInventoryManager().canAddItem((ItemStack)object)) {
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            return true;
        }
        player.nextActionSequence();
        player.getUpdateState().setAnimation(827);
        player.setActionLocked(true);
        player.setActiveCycleEvent(new PickableObjectEvent(n, n2, n3, player, (ItemStack)object, string));
        CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 2);
        return true;
    }
}

