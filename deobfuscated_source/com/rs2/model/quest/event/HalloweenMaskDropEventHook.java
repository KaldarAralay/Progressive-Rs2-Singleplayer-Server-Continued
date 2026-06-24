/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.quest.QuestHook;
import com.rs2.model.quest.event.HalloweenMaskDropRefreshTask;
import com.rs2.util.GameUtil;
import com.rs2.util.path.WalkingCollisionMap;

public final class HalloweenMaskDropEventHook
extends QuestHook {
    private int a = 0;

    public HalloweenMaskDropEventHook(int n, int n2) {
        super(-1, n2);
    }

    @Override
    public final void initialize() {
        this.a();
        HalloweenMaskDropRefreshTask halloweenMaskDropRefreshTask = new HalloweenMaskDropRefreshTask(this, ServerSettings.holidayDropRespawnTicks);
        World.getTaskScheduler().schedule(halloweenMaskDropRefreshTask);
    }

    private void a() {
        int n = 0;
        while (n < ServerSettings.holidayDropSets << 1) {
            ++this.a;
            int n2 = 0;
            while (n2 < 3) {
                int n3 = 1053 + (n2 << 1);
                int n4 = 2145 + GameUtil.randomInt(1695);
                int n5 = 2560 + GameUtil.randomInt(1410);
                boolean bl = GameUtil.randomInt(2) == 0;
                if (!bl) {
                    n4 = 2300 + GameUtil.randomInt(1390);
                    n5 = 9085 + GameUtil.randomInt(1280);
                }
                bl = false;
                if (WalkingCollisionMap.getTileFlags(n4, n5, 0) != 0) {
                    bl = true;
                }
                while (bl) {
                    n4 = 2145 + GameUtil.randomInt(1695);
                    n5 = 2560 + GameUtil.randomInt(1410);
                    bl = GameUtil.randomInt(2) == 0;
                    if (!bl) {
                        n4 = 2300 + GameUtil.randomInt(1390);
                        n5 = 9085 + GameUtil.randomInt(1280);
                    }
                    bl = WalkingCollisionMap.getTileFlags(n4, n5, 0) != 0;
                }
                Position position = new Position(n4, n5, 0);
                GroundItem groundItem = new GroundItem(new ItemStack(n3, 1), position, false, true);
                GroundItemManager.getInstance().spawn(groundItem);
                ++n2;
            }
            ++n;
        }
    }

    static /* synthetic */ void a(HalloweenMaskDropEventHook halloweenMaskDropEventHook) {
        halloweenMaskDropEventHook.a();
    }
}

