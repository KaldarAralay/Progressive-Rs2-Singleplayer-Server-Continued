/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.launcher;

import com.rs2.launcher.ControlPanel;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public final class MapVerticalScrollListener
implements AdjustmentListener {
    private /* synthetic */ ControlPanel controlPanel;

    public MapVerticalScrollListener(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    @Override
    public final void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
        int n;
        this.controlPanel.mapScrollY = n = adjustmentEvent.getValue();
        int cfr_ignored_0 = this.controlPanel.mapScale;
        ControlPanel.worldMapPanel.repaint();
    }
}

