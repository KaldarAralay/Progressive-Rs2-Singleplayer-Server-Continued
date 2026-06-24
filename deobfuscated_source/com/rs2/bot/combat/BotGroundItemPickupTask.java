/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.combat;

import com.rs2.model.Position;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.path.PathFinder;

public final class BotGroundItemPickupTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ Position itemPosition;
    private final /* synthetic */ int itemId;
    private final /* synthetic */ GroundItem groundItem;

    public BotGroundItemPickupTask(int n, Player player, Position position, int n2, GroundItem groundItem) {
        super(2);
        this.player = player;
        this.itemPosition = position;
        this.itemId = n2;
        this.groundItem = groundItem;
    }

    @Override
    public final void execute() {
        if (this.player.isDead() || !this.player.isRegistered()) {
            this.stop();
            return;
        }
        this.player.setInteractionTargetY(this.itemPosition.getY());
        this.player.setInteractionTargetId(this.itemId);
        this.player.setInteractionTargetX(this.itemPosition.getX());
        this.player.setInteractionTargetPlane(this.player.getPosition().getPlane());
        ItemStack itemStack = this.groundItem.getItem();
        PathFinder.getInstance();
        PathFinder.findPath(this.player, this.itemPosition.getX(), this.itemPosition.getY(), true, 0, 0);
        ItemService.getInstance().pickupItem(this.player, itemStack.getId(), this.groundItem.getPosition());
        this.stop();
    }
}

