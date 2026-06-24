/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.action;

import com.rs2.model.item.action.SpinningPlateResultEvent;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEventHandler;

public final class SpinningPlateHandler {
    private static int spinningPlateItemId = 4613;
    private static int brokenPlateItemId = 4614;
    private static int startSpinAnimationId = 1902;
    private static int catchPlateAnimationId = 1904;
    private static int breakPlateAnimationId = 1906;

    public static boolean spinPlate(Player player, int n) {
        if (n == spinningPlateItemId) {
            player.setActionLocked(true);
            player.getUpdateState().setAnimation(startSpinAnimationId, 0);
            CycleEventHandler.getInstance().schedule(player, new SpinningPlateResultEvent(player), 5);
            return true;
        }
        return false;
    }

    static /* synthetic */ int getBreakPlateAnimationId() {
        return breakPlateAnimationId;
    }

    static /* synthetic */ int getSpinningPlateItemId() {
        return spinningPlateItemId;
    }

    static /* synthetic */ int getBrokenPlateItemId() {
        return brokenPlateItemId;
    }

    static /* synthetic */ int getCatchPlateAnimationId() {
        return catchPlateAnimationId;
    }
}

