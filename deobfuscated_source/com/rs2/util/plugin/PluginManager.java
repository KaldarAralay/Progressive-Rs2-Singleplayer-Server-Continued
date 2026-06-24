/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.plugin;

import com.rs2.model.player.Player;
import com.rs2.util.ProfilerRegistry;
import com.rs2.util.ProfilerTimer;
import com.rs2.util.plugin.GlobalPlugin;
import com.rs2.util.plugin.PlayerPlugin;
import com.rs2.util.plugin.Plugin;
import com.rs2.util.plugin.PluginType;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PluginManager {
    private static List playerPluginClasses = new LinkedList();
    private static List globalPlugins = new LinkedList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadPlugins() {
        try {
            String packageName = "com.rs2.util.plugin.impl";
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert (classLoader != null);
            String packagePath = packageName.replace('.', '/');
            java.util.Enumeration resources = classLoader.getResources(packagePath);
            ArrayList pluginDirectories = new ArrayList();
            while (resources.hasMoreElements()) {
                URL url = (URL)resources.nextElement();
                pluginDirectories.add(new File(url.getFile()));
            }
            ArrayList pluginClassList = new ArrayList();
            Iterator directoryIterator = pluginDirectories.iterator();
            while (directoryIterator.hasNext()) {
                File pluginDirectory = (File)directoryIterator.next();
                pluginClassList.addAll(PluginManager.findPluginClasses(pluginDirectory, packageName));
            }
            Class[] pluginClasses = (Class[])pluginClassList.toArray(new Class[pluginClassList.size()]);
            int n = pluginClasses.length;
            int n2 = 0;
            while (n2 < n) {
                Class pluginClass = pluginClasses[n2];
                Plugin plugin = (Plugin)pluginClass.newInstance();
                if (plugin.getPluginType() == PluginType.GLOBAL) {
                    GlobalPlugin globalPlugin = (GlobalPlugin)plugin;
                    List list = playerPluginClasses;
                    synchronized (list) {
                        System.out.println("Loaded global plugin: " + globalPlugin.getName() + " v" + globalPlugin.getVersion() + " by " + globalPlugin.getAuthor());
                        globalPlugins.add(globalPlugin);
                    }
                }
                if (plugin.getPluginType() == PluginType.PLAYER_LOCAL) {
                    List list = playerPluginClasses;
                    synchronized (list) {
                        System.out.println("Loaded local plugin: " + pluginClass.getSimpleName());
                        playerPluginClasses.add(pluginClass);
                    }
                }
                ++n2;
            }
            return;
        }
        catch (InstantiationException instantiationException) {
            InstantiationException instantiationException2 = instantiationException;
            instantiationException.printStackTrace();
            return;
        }
        catch (IllegalAccessException illegalAccessException) {
            IllegalAccessException illegalAccessException2 = illegalAccessException;
            illegalAccessException.printStackTrace();
            return;
        }
        catch (ClassNotFoundException classNotFoundException) {
            ClassNotFoundException classNotFoundException2 = classNotFoundException;
            classNotFoundException.printStackTrace();
            return;
        }
        catch (IOException iOException) {
            IOException iOException2 = iOException;
            iOException.printStackTrace();
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void attachPlayerPlugins(Player player) {
        List list = playerPluginClasses;
        synchronized (list) {
            Iterator iterator = playerPluginClasses.iterator();
            while (iterator.hasNext()) {
                Object object;
                try {
                    object = (PlayerPlugin)((Class)iterator.next()).newInstance();
                    player.addPlayerPlugin((PlayerPlugin)object);
                }
                catch (InstantiationException instantiationException) {
                    object = instantiationException;
                    instantiationException.printStackTrace();
                    System.out.println("Error instantiating local plugin for " + player.getUsername());
                }
                catch (IllegalAccessException illegalAccessException) {
                    object = illegalAccessException;
                    illegalAccessException.printStackTrace();
                    System.out.println("Error instantiating local plugin for " + player.getUsername());
                }
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void tickGlobalPlugins() {
        ProfilerTimer profilerTimer = ProfilerRegistry.getTimer("tickPlugins");
        profilerTimer.start();
        List list = globalPlugins;
        synchronized (list) {
            Iterator iterator = globalPlugins.iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
        }
        profilerTimer.stop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void handleMovementPacketPlugins() {
        List list = globalPlugins;
        synchronized (list) {
            Iterator iterator = globalPlugins.iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void shutdownGlobalPlugins() {
        List list = globalPlugins;
        synchronized (list) {
            Iterator iterator = globalPlugins.iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
            return;
        }
    }

    private static List findPluginClasses(File file, String string) throws ClassNotFoundException {
        ArrayList arrayList = new ArrayList();
        if (!file.exists()) {
            return arrayList;
        }
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return arrayList;
        }
        int n = fileArray.length;
        int n2 = 0;
        while (n2 < n) {
            File child = fileArray[n2];
            if (child.isDirectory()) {
                assert (!child.getName().contains("."));
                arrayList.addAll(PluginManager.findPluginClasses(child, String.valueOf(string) + "." + child.getName()));
            } else if (child.getName().endsWith(".class")) {
                arrayList.add(Class.forName(String.valueOf(string) + '.' + child.getName().substring(0, child.getName().length() - 6)));
            }
            ++n2;
        }
        return arrayList;
    }

}

