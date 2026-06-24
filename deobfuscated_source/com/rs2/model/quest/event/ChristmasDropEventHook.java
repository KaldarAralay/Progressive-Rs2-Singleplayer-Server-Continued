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
import com.rs2.model.quest.event.ChristmasDropRefreshTask;
import com.rs2.util.GameUtil;
import com.rs2.util.path.WalkingCollisionMap;

public final class ChristmasDropEventHook
extends QuestHook {
    private int a = 0;
    private int[] b = new int[]{962, 1050};

    public ChristmasDropEventHook(int n, int n2) {
        super(-1, n2);
    }

    @Override
    public final void initialize() {
        this.a();
        ChristmasDropRefreshTask christmasDropRefreshTask = new ChristmasDropRefreshTask(this, ServerSettings.holidayDropRespawnTicks);
        World.getTaskScheduler().schedule(christmasDropRefreshTask);
    }

    private void a() {
        int n = 0;
        while (n < ServerSettings.holidayDropSets * 3) {
            ++this.a;
            int n2 = 0;
            while (n2 < this.b.length) {
                int n3 = this.b[n2];
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

    static /* synthetic */ void a(ChristmasDropEventHook christmasDropEventHook) {
        christmasDropEventHook.a();
    }
}

