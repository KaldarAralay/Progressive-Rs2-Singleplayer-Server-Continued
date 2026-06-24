/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.partyroom;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.gameplay.partyroom.PartyRoomBalloonReward;
import com.rs2.model.gameplay.partyroom.PartyRoomManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class PartyRoomBalloonSpawnTask
extends TickTask {
    private final /* synthetic */ ArrayList balloonPositions;
    private final /* synthetic */ ArrayList rewardItems;

    public PartyRoomBalloonSpawnTask(int n, ArrayList arrayList, ArrayList arrayList2) {
        super(n);
        this.balloonPositions = arrayList;
        this.rewardItems = arrayList2;
    }

    @Override
    public final void execute() {
        int n = 0;
        for (Object balloonPositionObject : this.balloonPositions) {
            Position position = (Position)balloonPositionObject;
            ItemStack itemStack;
            PartyRoomManager.activeBalloonObjects.add(new DynamicObject(115 + GameUtil.randomInt(7), position.getX(), position.getY(), 0, 0 + GameUtil.randomInt(3), 10, ServerSettings.placeholderObjectId, 1000, false));
            if (n < this.rewardItems.size() && (itemStack = (ItemStack)this.rewardItems.get(n)) != null) {
                new PartyRoomBalloonReward(itemStack, position, 0);
            }
            ++n;
        }
        this.stop();
        PartyRoomManager.partyChestContainer.clear();
        PartyRoomManager.balloonDropPending = false;
        PartyRoomManager.partyChestValue = 0;
        PartyRoomManager.savePartyChest();
        PartyRoomManager.refreshOpenPartyChestInterfaces();
    }
}

