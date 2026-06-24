/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.quest;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.quest.QuestHook;
import com.rs2.model.quest.event.ChristmasDropEventHook;
import com.rs2.model.quest.event.EasterEggDropEventHook;
import com.rs2.model.quest.event.HalloweenMaskDropEventHook;
import com.rs2.model.quest.event.NoopQuestEventHook;
import com.rs2.model.quest.event.ServerMaintenanceEventHook;

public final class QuestEventRegistry {
    public static int eventHookCount = 0;
    private static int SERVER_MAINTENANCE_EVENT_TYPE = 0;
    private static int HALLOWEEN_EVENT_TYPE = 1;
    private static int CHRISTMAS_EVENT_TYPE = 2;
    private static int EASTER_EVENT_TYPE = 3;
    private static QuestHook[] eventHooks = new QuestHook[]{new NoopQuestEventHook(-1), new ServerMaintenanceEventHook(-1, 0), new HalloweenMaskDropEventHook(-1, HALLOWEEN_EVENT_TYPE), new ChristmasDropEventHook(-1, CHRISTMAS_EVENT_TYPE), new EasterEggDropEventHook(-1, EASTER_EVENT_TYPE)};

    public static QuestHook getEventHook(int n) {
        int n2 = 0;
        while (n2 < eventHooks.length) {
            int n3 = eventHooks[n2].getQuestId();
            if (n == n3 && eventHooks[n2].isEnabled()) {
                return eventHooks[n2];
            }
            ++n2;
        }
        return eventHooks[0];
    }

    public static void initializeEventHooks() {
        eventHookCount = eventHooks.length;
        int n = 0;
        while (n < eventHooks.length) {
            if (eventHooks[n].getEventType() == 0) {
                eventHooks[n].initialize();
                eventHooks[n].setEnabled(true);
            }
            if (eventHooks[n].getEventType() == HALLOWEEN_EVENT_TYPE && Server.halloweenEventActive && ServerSettings.holidayItemDropsEnabled) {
                eventHooks[n].initialize();
                eventHooks[n].setEnabled(true);
            }
            if (eventHooks[n].getEventType() == CHRISTMAS_EVENT_TYPE && Server.christmasEventActive && ServerSettings.holidayItemDropsEnabled) {
                eventHooks[n].initialize();
                eventHooks[n].setEnabled(true);
            }
            if (eventHooks[n].getEventType() == EASTER_EVENT_TYPE && Server.easterEventActive && ServerSettings.holidayItemDropsEnabled) {
                eventHooks[n].initialize();
                eventHooks[n].setEnabled(true);
            }
            ++n;
        }
    }
}

