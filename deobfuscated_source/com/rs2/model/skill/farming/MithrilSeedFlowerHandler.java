/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;

public final class MithrilSeedFlowerHandler {
    private static int[] mithrilSeedFlowerObjectIds = new int[]{2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988};

    static {
        int[] nArray = new int[]{2460, 2462, 2464, 2466, 2468, 2470, 2472, 2474, 2476};
    }

    public static void plantMithrilSeedFlower(Player player) {
        int n = mithrilSeedFlowerObjectIds[GameUtil.randomExclusive(9)];
        int n2 = player.getPosition().getX();
        int n3 = player.getPosition().getY();
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n2, n3, player.getPosition().getPlane());
        if (dynamicObject != null) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("You can't plant a flower here.");
            return;
        }
        if (!player.getInventoryManager().getContainer().containsItem(299)) {
            return;
        }
        player.getInventoryManager().removeItem(new ItemStack(299));
        player.getUpdateState().setAnimation(827);
        new DynamicObject(n, n2, n3, player.getPosition().getPlane(), 0, 10, ServerSettings.placeholderObjectId, 500);
        if (player.canStepToOffset(-1, 0)) {
            Player player3 = player;
            player3.packetSender.queueRelativeMovementStep(-1, 0, false);
        } else {
            Player player4 = player;
            player4.packetSender.queueRelativeMovementStep(1, 0, false);
        }
        player.getUpdateState().setFacePosition(new Position(n2, n3));
    }

    public static final boolean isMithrilSeedFlowerObjectId(int n) {
        int[] nArray = mithrilSeedFlowerObjectIds;
        int n2 = 0;
        while (n2 < 9) {
            int n3 = nArray[n2];
            if (n3 == n) {
                return true;
            }
            ++n2;
        }
        return false;
    }
}

