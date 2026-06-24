/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.firemaking;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.firemaking.FiremakingHandler;
import com.rs2.model.skill.firemaking.FiremakingLog;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameplayTrace;
import com.rs2.util.GameUtil;

public final class FiremakingTask
extends CycleEvent {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ GroundItem groundItem;
    private final /* synthetic */ int logItemId;
    private final /* synthetic */ FiremakingLog firemakingLog;
    private final /* synthetic */ int fireX;
    private final /* synthetic */ int fireY;
    private final /* synthetic */ int plane;

    public FiremakingTask(FiremakingHandler firemakingHandler, Player player, int n, GroundItem groundItem, int n2, FiremakingLog firemakingLog, int n3, int n4, int n5) {
        this.player = player;
        this.actionSequence = n;
        this.groundItem = groundItem;
        this.logItemId = n2;
        this.firemakingLog = firemakingLog;
        this.fireX = n3;
        this.fireY = n4;
        this.plane = n5;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        block12: {
            block11: {
                if (!this.player.isCurrentActionSequence(this.actionSequence)) break block11;
                if (this.groundItem == null) break block12;
                GroundItemManager.getInstance();
                if (GroundItemManager.isVisible(this.player, this.groundItem)) break block12;
            }
            cycleEventContainer.stop();
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("firemaking stopped-invalid-state player=" + GameplayTrace.describe(this.player) + " logItemId=" + this.logItemId + " x=" + this.fireX + " y=" + this.fireY + " plane=" + this.plane);
            }
            return;
        }
        boolean bl = GameUtil.rollLevelScaledChance(64, 512, this.player.getSkillManager().getCurrentLevels()[11]);
        if (this.logItemId == 7404 || this.logItemId == 7405 || this.logItemId == 7406) {
            bl = true;
        }
        if (bl) {
            Player player;
            if (this.player.canStepToOffset(-1, 0)) {
                player = this.player;
                player.packetSender.queueRelativeMovementStep(-1, 0, false);
            } else {
                player = this.player;
                player.packetSender.queueRelativeMovementStep(1, 0, false);
            }
            player = null;
            if (this.player.gameMode != 0) {
                player = this.player;
            }
            new DynamicObject(this.firemakingLog.getFireObjectId(), this.fireX, this.fireY, this.plane, -1, 10, ServerSettings.placeholderObjectId, GameUtil.randomBetweenInclusive(100, 200), player);
            player = this.player;
            player.packetSender.sendGameMessage("The fire catches and the logs begin to burn.");
            player = this.player;
            player.packetSender.sendSoundEffect(374, 1, 0);
            if (this.player.getQuestState(0) == 9) {
                this.player.ea();
            }
            if (this.player.getQuestState(0) != 1) {
                this.player.getQuestManager().refreshQuestJournal();
            }
            if (this.groundItem != null) {
                GroundItemManager.getInstance().removeForPickup(this.groundItem, this.player);
            }
            this.player.getUpdateState().setFacePosition(new Position(this.fireX, this.fireY));
            this.player.getSkillManager().addExperience(11, this.firemakingLog.getExperience());
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("firemaking success player=" + GameplayTrace.describe(this.player) + " logItemId=" + this.logItemId + " fireObjectId=" + this.firemakingLog.getFireObjectId() + " x=" + this.fireX + " y=" + this.fireY + " plane=" + this.plane + " xp=" + this.firemakingLog.getExperience());
            }
            cycleEventContainer.stop();
            return;
        }
        this.player.getUpdateState().setAnimation(733);
        Player player = this.player;
        player.packetSender.sendSoundEffect(375, 1, 0);
    }

    @Override
    public final void onStop() {
        this.player.resetAnimation();
    }
}

