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
import com.rs2.model.quest.event.EasterEggDropRefreshTask;
import com.rs2.util.GameUtil;
import com.rs2.util.path.WalkingCollisionMap;

public final class EasterEggDropEventHook
extends QuestHook {
    private int a = 0;

    public EasterEggDropEventHook(int n, int n2) {
        super(-1, n2);
    }

    @Override
    public final void initialize() {
        this.a();
        EasterEggDropRefreshTask easterEggDropRefreshTask = new EasterEggDropRefreshTask(this, ServerSettings.holidayDropRespawnTicks);
        World.getTaskScheduler().schedule(easterEggDropRefreshTask);
    }

    private void a() {
        int n = 0;
        while (n < ServerSettings.holidayDropSets * 6) {
            int n2 = 2145 + GameUtil.randomInt(1695);
            int n3 = 2560 + GameUtil.randomInt(1410);
            boolean bl = GameUtil.randomInt(2) == 0;
            if (!bl) {
                n2 = 2300 + GameUtil.randomInt(1390);
                n3 = 9085 + GameUtil.randomInt(1280);
            }
            bl = false;
            if (WalkingCollisionMap.getTileFlags(n2, n3, 0) != 0) {
                bl = true;
            }
            while (bl) {
                n2 = 2145 + GameUtil.randomInt(1695);
                n3 = 2560 + GameUtil.randomInt(1410);
                bl = GameUtil.randomInt(2) == 0;
                if (!bl) {
                    n2 = 2300 + GameUtil.randomInt(1390);
                    n3 = 9085 + GameUtil.randomInt(1280);
                }
                bl = WalkingCollisionMap.getTileFlags(n2, n3, 0) != 0;
            }
            Object object = new Position(n2, n3, 0);
            object = new GroundItem(new ItemStack(1961, 1), (Position)object, false);
            GroundItemManager.getInstance().spawn((GroundItem)object);
            ++this.a;
            ++n;
        }
    }

    static /* synthetic */ void a(EasterEggDropEventHook easterEggDropEventHook) {
        easterEggDropEventHook.a();
    }
}

