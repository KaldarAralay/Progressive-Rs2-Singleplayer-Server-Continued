/*
 * Source recovery overlay: make remapped event accessible to recovered callers.
 */
package com.rs2.model.objects.functions;

import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;

public final class WebSlashEvent
extends CycleEvent {
    private final /* synthetic */ Boolean successful;
    private final /* synthetic */ Player player;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int orientation;

    public WebSlashEvent(Boolean bl, Player player, int n, int n2, int n3) {
        this.successful = bl;
        this.player = player;
        this.objectX = n;
        this.objectY = n2;
        this.orientation = n3;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (!this.successful.booleanValue()) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You fail to slash through the web.");
            player = this.player;
            player.packetSender.sendSoundEffect(237, 1, 0);
        } else {
            new DynamicObject(734, this.objectX, this.objectY, 0, this.orientation, 10, 733, 100);
            ObjectManager.getInstance();
            ObjectManager.removeObjectCollision(733, this.objectX, this.objectY, 0, 10, this.orientation);
            Player player = this.player;
            player.packetSender.sendGameMessage("You successfully slash open the web.");
            player = this.player;
            player.packetSender.sendSoundEffect(237, 1, 0);
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}
