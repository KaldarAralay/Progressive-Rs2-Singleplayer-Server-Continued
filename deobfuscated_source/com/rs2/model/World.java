/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.BotReloginTask;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.animation.GraphicEffect;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObjectRegionIndex;
import com.rs2.model.player.Player;
import com.rs2.model.player.PlayerConnectionState;
import com.rs2.model.player.PlayerUpdateTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.task.TaskScheduler;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.CharacterFileManager;
import com.rs2.util.GameUtil;
import com.rs2.util.ProfilerRegistry;
import com.rs2.util.ProfilerTimer;
import com.rs2.util.TextUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;

public final class World {
    public static final Player[] players = new Player[ServerSettings.maxPlayers];
    private static Npc[] npcs = new Npc[20000];
    private static TaskScheduler taskScheduler = new TaskScheduler();
    private static NpcDefinition[] npcDefinitions = new NpcDefinition[6433];
    public static int tickCount = 0;
    private static final World instance;
    private WorldObjectRegionIndex objectRegionIndex = new WorldObjectRegionIndex();

    static {
        int n = Runtime.getRuntime().availableProcessors();
        Executors.newFixedThreadPool(n);
        instance = new World();
    }

    public static void logoutBotAndScheduleRelogin(Player player) {
        if (!player.isBot) {
            return;
        }
        String username = player.getUsername();
        player.packetSender.sendLogout();
        player.disconnect();
        TickTask reloginTask = new BotReloginTask(30, username);
        taskScheduler.schedule(reloginTask);
    }

    public static synchronized void processTick() {
        Object object;
        Object object2;
        Object object3;
        ++tickCount;
        ProfilerTimer profilerTimer = ProfilerRegistry.getTimer("executeTicks");
        LinkedList taskSnapshot = new LinkedList();
        taskSnapshot.addAll(taskScheduler.getTasks());
        java.util.Iterator taskIterator = taskSnapshot.iterator();
        taskScheduler.getTasks().clear();
        profilerTimer.start();
        while (taskIterator.hasNext()) {
            object3 = (TickTask)taskIterator.next();
            try {
                ((TickTask)object3).tick();
                if (!((TickTask)object3).isActive()) continue;
                taskScheduler.getTasks().add(object3);
            }
            catch (Exception exception) {
                object2 = exception;
                exception.printStackTrace();
            }
        }
        profilerTimer.stop();
        try {
            profilerTimer = ProfilerRegistry.getTimer("groundItemsUpdate");
            profilerTimer.start();
            GroundItemManager.getInstance().run();
            profilerTimer.stop();
            profilerTimer = ProfilerRegistry.getTimer("groundObjectsUpdate");
            profilerTimer.start();
            ObjectManager.getInstance().processObjects();
            profilerTimer.stop();
        }
        catch (Exception exception) {
            object3 = exception;
            exception.printStackTrace();
        }
        profilerTimer = ProfilerRegistry.getTimer("processPlayerLogic");
        profilerTimer.start();
        object3 = new ArrayList<String>();
        Entity[] entityArray = players;
        int n = players.length;
        int n2 = 0;
        while (n2 < n) {
            object2 = entityArray[n2];
            if (object2 != null) {
                if (((ArrayList)object3).contains(((Player)object2).getUsername())) {
                    System.out.println("Disconnecting: " + ((Player)object2).getUsername() + " Reason: Multilog!");
                    object = object2;
                    ((Player)object).packetSender.sendLogout();
                    ((Player)object2).disconnect();
                } else {
                    ((ArrayList)object3).add(((Player)object2).getUsername());
                    try {
                        ((Player)object2).process();
                    }
                    catch (Exception exception) {
                        object = exception;
                        exception.printStackTrace();
                        ((Player)object2).disconnect();
                    }
                }
            }
            ++n2;
        }
        profilerTimer.stop();
        profilerTimer = ProfilerRegistry.getTimer("processNPCLogic");
        profilerTimer.start();
        entityArray = npcs;
        n = npcs.length;
        n2 = 0;
        while (n2 < n) {
            object2 = entityArray[n2];
            if (object2 != null) {
                try {
                    ((Npc)object2).process();
                }
                catch (Exception exception) {
                    object = exception;
                    exception.printStackTrace();
                    World.unregisterNpc((Npc)object2);
                }
            }
            ++n2;
        }
        profilerTimer.stop();
        CycleEventHandler.getInstance().process();
        profilerTimer = ProfilerRegistry.getTimer("processMovement");
        profilerTimer.start();
        entityArray = players;
        n = players.length;
        n2 = 0;
        while (n2 < n) {
            object2 = entityArray[n2];
            if (object2 != null) {
                ((Entity)object2).getMovementQueue().process();
            }
            ++n2;
        }
        entityArray = npcs;
        n = npcs.length;
        n2 = 0;
        while (n2 < n) {
            object2 = entityArray[n2];
            if (object2 != null) {
                ((Entity)object2).getMovementQueue().process();
            }
            ++n2;
        }
        profilerTimer.stop();
        profilerTimer = ProfilerRegistry.getTimer("updatePlayers");
        profilerTimer.start();
        entityArray = players;
        n = players.length;
        n2 = 0;
        while (n2 < n) {
            object2 = entityArray[n2];
            if (object2 != null) {
                try {
                    PlayerUpdateTask.a((Player)object2);
                    GameplayHelper.sendNpcUpdatePacket((Player)object2);
                }
                catch (Exception exception) {
                    object = exception;
                    exception.printStackTrace();
                    ((Player)object2).disconnect();
                }
            }
            ++n2;
        }
        profilerTimer.stop();
        profilerTimer = ProfilerRegistry.getTimer("resetPlayers");
        profilerTimer.start();
        entityArray = players;
        n = players.length;
        n2 = 0;
        while (n2 < n) {
            object2 = entityArray[n2];
            if (object2 != null) {
                try {
                    object3 = object2;
                    ((Entity)object3).getUpdateState().reset();
                    ((Entity)object3).setWalkDirection(-1);
                    ((Entity)object3).setRunDirection(-1);
                    ((Player)object3).setAppearanceUpdateRequired(false);
                    ((Player)object3).setTeleportPlacementUpdateRequired(false);
                    ((Player)object3).setTeleporting(false);
                    ((Player)object3).setPublicChatPayload(null);
                }
                catch (Exception exception) {
                    object = exception;
                    exception.printStackTrace();
                    ((Player)object2).disconnect();
                }
            }
            ++n2;
        }
        profilerTimer.stop();
        profilerTimer = ProfilerRegistry.getTimer("resetNPCs");
        profilerTimer.start();
        entityArray = npcs;
        n = npcs.length;
        n2 = 0;
        while (n2 < n) {
            object2 = entityArray[n2];
            if (object2 != null) {
                try {
                    ((Npc)object2).resetAfterUpdate();
                }
                catch (Exception exception) {
                    object = exception;
                    exception.printStackTrace();
                    World.unregisterNpc((Npc)object2);
                }
            }
            ++n2;
        }
        profilerTimer.stop();
    }

    public static synchronized void registerPlayer(Player player) {
        int n = 1;
        while (n < players.length) {
            if (players[n] == null) {
                World.players[n] = player;
                player.setIndex(n);
                player.setEncodedIndex(n + 32768);
                player.setSize(1);
                return;
            }
            ++n;
        }
        throw new IllegalStateException("Server is full!");
    }

    public static synchronized void registerNpc(Npc npc) {
        int n = 1;
        while (n < npcs.length) {
            if (npcs[n] == null) {
                World.npcs[n] = npc;
                npc.setIndex(n);
                npc.setEncodedIndex(n);
                npc.setSize(npcDefinitions[npc.getNpcId()].getSize());
                int[] nArray = Npc.combatTransformNpcIds;
                int n2 = Npc.combatTransformNpcIds.length;
                int n3 = 0;
                while (n3 < n2) {
                    n = nArray[n3];
                    if (n == npc.getNpcId()) {
                        npc.setCombatTransformNpcId(npc.getNpcId() - 1);
                        break;
                    }
                    ++n3;
                }
                nArray = Npc.scriptedMovementNpcIds;
                n2 = Npc.scriptedMovementNpcIds.length;
                n3 = 0;
                while (n3 < n2) {
                    n = nArray[n3];
                    if (n == npc.getNpcId()) {
                        npc.setScriptedMovementEnabled(true);
                        break;
                    }
                    ++n3;
                }
                nArray = Npc.targetMovementDisabledNpcIds;
                n2 = Npc.targetMovementDisabledNpcIds.length;
                n3 = 0;
                while (n3 < n2) {
                    n = nArray[n3];
                    if (n == npc.getNpcId()) {
                        npc.setTargetMovementDisabled(true);
                        break;
                    }
                    ++n3;
                }
                nArray = Npc.autoRetaliateDisabledNpcIds;
                n2 = Npc.autoRetaliateDisabledNpcIds.length;
                n3 = 0;
                while (n3 < n2) {
                    n = nArray[n3];
                    if (n == npc.getNpcId()) {
                        npc.setAutoRetaliateDisabled(true);
                        break;
                    }
                    ++n3;
                }
                nArray = Npc.faceEntityUpdateDisabledNpcIds;
                n2 = Npc.faceEntityUpdateDisabledNpcIds.length;
                n3 = 0;
                while (n3 < n2) {
                    n = nArray[n3];
                    if (n == npc.getNpcId()) {
                        npc.setFaceEntityUpdateDisabled(true);
                        return;
                    }
                    ++n3;
                }
                return;
            }
            ++n;
        }
        throw new IllegalStateException("Server is full!");
    }

    public static synchronized void unregisterPlayer(Player player) {
        try {
            CharacterFileManager.savePlayer(player);
            player.setRegistered(false);
            player.setConnectionState(PlayerConnectionState.DISCONNECTED);
            if (player.getIndex() == -1) {
                return;
            }
            player.clearCombatEffectTasks();
            World.players[player.getIndex()] = null;
            player.setIndex(-1);
            return;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    public static synchronized void unregisterNpc(Npc npc) {
        if (npc.getIndex() == -1) {
            return;
        }
        npc.clearCombatEffectTasks();
        World.npcs[npc.getIndex()] = null;
        npc.setIndex(-1);
    }

    public static int getPlayerCount() {
        int n = 0;
        Player[] playerArray = players;
        int n2 = players.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null) {
                ++n;
            }
            ++n3;
        }
        return n;
    }

    public static int getNonBotPlayerCount() {
        int n = 0;
        Player[] playerArray = players;
        int n2 = players.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null && !player.isBot) {
                ++n;
            }
            ++n3;
        }
        return n;
    }

    public static boolean hasNearbyNonBotPlayer(Npc npc) {
        Player[] playerArray = players;
        int n = players.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null && !player.isBot && GameUtil.getDistance(npc.getPosition(), player.getPosition()) <= 32) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public static int getAdminCount() {
        int n = 0;
        Player[] playerArray = players;
        int n2 = players.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null && player.getPlayerRights() >= 2) {
                ++n;
            }
            ++n3;
        }
        return n;
    }

    public static int getModeratorCount() {
        int n = 0;
        Player[] playerArray = players;
        int n2 = players.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null && player.getPlayerRights() == 1) {
                ++n;
            }
            ++n3;
        }
        return n;
    }

    public static Player findPlayerByUsername(String username) {
        long nameHash = TextUtil.encodeNameHash(username);
        Player[] playerArray = players;
        int length = players.length;
        int index = 0;
        while (index < length) {
            Player player = playerArray[index];
            if (player != null && player.getNameHash() == nameHash) {
                return player;
            }
            ++index;
        }
        return null;
    }

    public static void logoutBotsAndScheduleShutdown(boolean bl) {
        Player[] playerArray = players;
        int n = players.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null && player.isBot) {
                Player player2 = player;
                player2.packetSender.sendLogout();
                player.disconnect();
            }
            ++n2;
        }
        Server.scheduleShutdown(true);
    }

    public static void scheduleTickTask(TickTask tickTask) {
        taskScheduler.schedule(tickTask);
    }

    public static Player[] getPlayers() {
        return players;
    }

    public static Npc[] getNpcs() {
        return npcs;
    }

    public static TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public static NpcDefinition[] getNpcDefinitions() {
        return npcDefinitions;
    }

    public static World getInstance() {
        return instance;
    }

    public final WorldObjectRegionIndex getObjectRegionIndex() {
        return this.objectRegionIndex;
    }

    public static void sendStillGraphicToNearbyPlayers(GraphicEffect graphicEffect, Position position) {
        Player[] playerArray = players;
        int n = players.length;
        int n2 = 0;
        while (n2 < n) {
            Object object = playerArray[n2];
            if (object != null && position.isWithinViewport(((Entity)object).getPosition())) {
                Position position2 = position;
                GraphicEffect graphicEffect2 = graphicEffect;
                object = ((Player)object).packetSender;
                ((PacketSender)object).sendStillGraphic(graphicEffect2.getId(), position2, graphicEffect2.getPackedDelay());
            }
            ++n2;
        }
    }
}

