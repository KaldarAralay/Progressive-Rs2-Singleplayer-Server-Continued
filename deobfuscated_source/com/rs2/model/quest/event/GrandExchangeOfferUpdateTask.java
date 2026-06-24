/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest.event;

import com.rs2.model.grandexchange.GrandExchangeOffer;
import com.rs2.model.quest.event.ServerMaintenanceEventHook;
import com.rs2.model.task.TickTask;

public final class GrandExchangeOfferUpdateTask
extends TickTask {
    public GrandExchangeOfferUpdateTask(ServerMaintenanceEventHook serverMaintenanceEventHook, int n) {
        super(100);
    }

    @Override
    public final void execute() {
        GrandExchangeOffer.processServerOffers();
    }
}

