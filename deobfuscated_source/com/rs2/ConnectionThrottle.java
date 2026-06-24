/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.ConnectionThrottleSettings;
import java.util.concurrent.ConcurrentHashMap;

public final class ConnectionThrottle {
    private static ConcurrentHashMap connectionCountsByHost = new ConcurrentHashMap();

    public static boolean tryAcquireConnectionSlot(String string) {
        if (!ConnectionThrottleSettings.connectionsEnabled) {
            return false;
        }
        Integer n = (Integer)connectionCountsByHost.putIfAbsent(string, 1);
        if (n != null && n == 3) {
            return false;
        }
        n = n == null ? 0 : n;
        connectionCountsByHost.replace(string, n + 1);
        return true;
    }

    public static void releaseConnectionSlot(String string) {
        Integer n = (Integer)connectionCountsByHost.get(string);
        if (n == null) {
            return;
        }
        if (n == 1) {
            connectionCountsByHost.remove(string);
            return;
        }
        if (n != null) {
            connectionCountsByHost.replace(string, n - 1);
        }
    }
}

