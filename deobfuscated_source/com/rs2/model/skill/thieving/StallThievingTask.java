/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.thieving;

import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.thieving.StallDefinition;
import com.rs2.model.skill.thieving.StallThievingHandler;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class StallThievingTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ ItemStack reward;
    private final /* synthetic */ StallDefinition stallDefinition;

    public StallThievingTask(Player player, int n, int n2, int n3, int n4, ItemStack itemStack, StallDefinition stallDefinition) {
        this.player = player;
        this.actionSequence = n;
        this.objectId = n2;
        this.objectX = n3;
        this.objectY = n4;
        this.reward = itemStack;
        this.stallDefinition = stallDefinition;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        if (!SkillActionHelper.isObjectPresent(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane())) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Too late, the items are gone.");
            return;
        }
        this.player.getInventoryManager().addOrDropItem(this.reward);
        Player player = this.player;
        player.packetSender.sendGameMessage("You successfully stole a " + this.reward.getDefinition().getName().toLowerCase() + ".");
        this.player.getSkillManager().addExperience(17, this.stallDefinition.getExperience());
        player = this.player;
        player.packetSender.sendSoundEffect(358, 1, 0);
        int n = SkillActionHelper.getObjectOrientation(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane());
        new DynamicObject(StallThievingHandler.getEmptyStallObjectId(this.objectId), this.objectX, this.objectY, this.player.getPosition().getPlane(), n, 10, this.objectId, this.stallDefinition.getRespawnTicks());
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setHideHeldItemsInAppearance(false);
        this.player.setActionLocked(false);
    }
}

