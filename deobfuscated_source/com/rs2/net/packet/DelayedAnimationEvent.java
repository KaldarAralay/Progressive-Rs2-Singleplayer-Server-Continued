/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet;

import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.net.packet.PacketSender;

public final class DelayedAnimationEvent
extends CycleEvent {
    private /* synthetic */ PacketSender packetSender;
    private final /* synthetic */ int animationId;

    public DelayedAnimationEvent(PacketSender packetSender, int n) {
        this.packetSender = packetSender;
        this.animationId = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        PacketSender.getPlayer(this.packetSender).getUpdateState().setAnimation(this.animationId);
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
    }
}

