/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.thieving;

import com.rs2.model.combat.hit.HitType;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.thieving.ThievingObjectHandler;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class TrapDisarmTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ double experience;
    private final /* synthetic */ ItemStack[] rewards;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectId;

    public TrapDisarmTask(Player player, double d, ItemStack[] itemStackArray, int n, int n2, int n3) {
        this.player = player;
        this.experience = d;
        this.rewards = itemStackArray;
        this.objectX = n;
        this.objectY = n2;
        this.objectId = n3;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (ThievingObjectHandler.getRandom().nextInt(30) < 5) {
            Player player = this.player;
            player.packetSender.sendGameMessage("But fail to disarm it, and get hit by the traps.");
            int n = ThievingObjectHandler.getRandom().nextInt(1);
            this.player.applyDirectHit(GameUtil.randomInclusive(10), n == 1 ? HitType.POISON : HitType.NORMAL);
            if (n == 1) {
                this.player.getUpdateState().setGraphic(184);
            }
            cycleEventContainer.stop();
            return;
        }
        Object object = this.player;
        ((Player)object).packetSender.sendGameMessage("And manage to disarm it.");
        object = this.player;
        ((Player)object).packetSender.sendSoundEffect(1502, 1, 0);
        this.player.getSkillManager().addExperience(17, this.experience);
        ItemStack[] itemStackArray = this.rewards;
        int n = this.rewards.length;
        int n2 = 0;
        while (n2 < n) {
            object = itemStackArray[n2];
            this.player.getInventoryManager().addOrDropItem((ItemStack)object);
            ++n2;
        }
        new DynamicObject(2588, this.objectX, this.objectY, this.player.getPosition().getPlane(), SkillActionHelper.getObjectOrientation(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane()), 10, this.objectId, 10);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
        this.player.resetAnimation();
    }
}

