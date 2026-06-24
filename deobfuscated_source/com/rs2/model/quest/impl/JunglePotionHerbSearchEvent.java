/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.impl;

import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.quest.impl.JunglePotionQuest;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class JunglePotionHerbSearchEvent
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int herbItemId;
    private final /* synthetic */ boolean restoreObjectAfterSearch;

    public JunglePotionHerbSearchEvent(JunglePotionQuest junglePotionQuest, Player player, int n, int n2, int n3, int n4, int n5, boolean bl) {
        this.player = player;
        this.actionSequence = n;
        this.objectX = n2;
        this.objectY = n3;
        this.objectId = n4;
        this.herbItemId = n5;
        this.restoreObjectAfterSearch = bl;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.player.isCurrentActionSequence(this.actionSequence)) {
            cycleEventContainer.stop();
            return;
        }
        int n = 0;
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(this.objectX, this.objectY, this.player.getPosition().getPlane());
        if (dynamicObject != null && dynamicObject.getWorldObject().getObjectId() != this.objectId) {
            cycleEventContainer.stop();
            return;
        }
        cycleEventContainer.setTickDelay(3);
        this.player.getUpdateState().setAnimation(832);
        if (GameUtil.randomInt(5) == 0) {
            n = this.herbItemId;
            this.player.getInventoryManager().addItem(new ItemStack(n, 1));
            this.player.getDialogueManager().showItemMessage("You find a herb.", new ItemStack(n, 1));
            if (this.restoreObjectAfterSearch) {
                try {
                    n = SkillActionHelper.getObjectOrientation(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane());
                    int n2 = SkillActionHelper.getObjectType(this.objectId, this.objectX, this.objectY, this.player.getPosition().getPlane());
                    new DynamicObject(2576, this.objectX, this.objectY, this.player.getPosition().getPlane(), n, n2, this.objectId, 10);
                }
                catch (Exception exception) {
                    Exception exception2 = exception;
                    exception.printStackTrace();
                }
            }
            cycleEventContainer.stop();
            n = 1;
        }
        if (n == 0 && !this.player.getInventoryManager().canAddItem(new ItemStack(this.herbItemId, 1))) {
            cycleEventContainer.stop();
            return;
        }
    }

    @Override
    public final void onStop() {
        this.player.getUpdateState().setAnimation(-1);
    }
}

