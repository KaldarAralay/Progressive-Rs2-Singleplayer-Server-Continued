/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay;

import com.rs2.model.World;
import com.rs2.model.gameplay.CaveInsectSwarmTask;
import com.rs2.model.gameplay.SwampGasExplosionTask;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.RectangularArea;

public final class CaveLightManager {
    private static RectangularArea[] swampGasAreas = new RectangularArea[]{new RectangularArea(3155, 9584, 3174, 9596), new RectangularArea(3203, 9551, 3213, 9560)};
    private static int[] caveInsectRegionIds = new int[]{12693, 12949};

    static boolean isInCaveInsectRegion(Player player) {
        int n = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());
        int[] nArray = caveInsectRegionIds;
        int n2 = caveInsectRegionIds.length;
        int n3 = 0;
        while (n3 < n2) {
            int n4 = nArray[n3];
            if (n4 == n) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    static boolean isInSwampGasArea(Player player) {
        Object object;
        RectangularArea[] rectangularAreaArray = swampGasAreas;
        int n = swampGasAreas.length;
        int n2 = 0;
        while (n2 < n) {
            object = rectangularAreaArray[n2];
            if (((RectangularArea)object).contains(player.getPosition())) {
                return true;
            }
            ++n2;
        }
        if (player.swampGasFlareState == 1 && (object = player.findLitCaveLightSource()) != null) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("Your " + ((ItemStack)object).getDefinition().getName().toLowerCase() + " stops flaring.");
        }
        player.swampGasFlareState = 0;
        return false;
    }

    public static boolean updateCaveLightHazards(Player player) {
        Object object = player.findLitCaveLightSource();
        if (CaveLightManager.isInSwampGasArea(player) && object != null && player.swampGasFlareState == 0) {
            player.swampGasFlareState = 1;
            Player player2 = player;
            player2.packetSender.sendGameMessage("Your " + ((ItemStack)object).getDefinition().getName().toLowerCase() + " flares brightly!");
            World.getTaskScheduler().schedule(new SwampGasExplosionTask(7, player));
        }
        if (player.activeEnvironmentalHazardId == 1657) {
            return true;
        }
        if (CaveLightManager.isInCaveInsectRegion(player)) {
            World.getTaskScheduler().schedule(new CaveInsectSwarmTask(15, player));
            player.activeEnvironmentalHazardId = 1657;
            return true;
        }
        return false;
    }
}

