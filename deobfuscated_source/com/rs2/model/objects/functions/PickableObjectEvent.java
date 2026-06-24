/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects.functions;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class PickableObjectEvent
extends CycleEvent {
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ Player player;
    private final /* synthetic */ ItemStack item;
    private final /* synthetic */ String itemName;

    public PickableObjectEvent(int n, int n2, int n3, Player player, ItemStack itemStack, String string) {
        this.objectId = n;
        this.objectX = n2;
        this.objectY = n3;
        this.player = player;
        this.item = itemStack;
        this.itemName = string;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!SkillActionHelper.isObjectPresent(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane())) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Too late, the plant is gone.");
            if (this.player.botEnabled) {
                this.player.interactWithBotObjectTargets(this.player.botInteractionTargetIds);
            }
            cycleEventContainer.stop();
            return;
        }
        Player player = this.player;
        player.packetSender.sendSoundEffect(356, 1, 0);
        this.player.getInventoryManager().addItem(this.item);
        if (this.item.getId() != 1779 || GameUtil.randomInclusive(3) == 0) {
            int n = SkillActionHelper.getObjectOrientation(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane());
            new DynamicObject(ServerSettings.placeholderObjectId, this.objectX, this.objectY, this.player.getPosition().getPlane(), n, 10, this.objectId, 20);
        }
        Player player2 = this.player;
        player2.packetSender.sendGameMessage("You pick a " + this.itemName + ".");
        if (this.player.botEnabled) {
            this.player.interactWithBotObjectTargets(this.player.botInteractionTargetIds);
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

