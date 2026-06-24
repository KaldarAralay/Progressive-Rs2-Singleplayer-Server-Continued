/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import com.rs2.util.ProfilerTimer;
import java.util.HashMap;
import java.util.Map;

public final class ProfilerRegistry {
    private static Map timers = new HashMap();

    public static ProfilerTimer getTimer(String string) {
        ProfilerTimer profilerTimer = (ProfilerTimer)timers.get(string);
        if (profilerTimer == null) {
            profilerTimer = new ProfilerTimer();
            timers.put(string, profilerTimer);
        }
        return profilerTimer;
    }

    public static void resetAll() {
        for (Object entryObject : timers.entrySet()) {
            Map.Entry entry = (Map.Entry)entryObject;
            ((ProfilerTimer)entry.getValue()).reset();
        }
    }

    public static void b() {
    }
}

