/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.BotLoginBatchTask;
import com.rs2.CacheCoordinateTranslator;
import com.rs2.ConfigFile;
import com.rs2.ConnectionThrottle;
import com.rs2.HiscoresDatabase;
import com.rs2.LanDiscoveryService;
import com.rs2.MinuteMaintenanceTickTask;
import com.rs2.ServerSettings;
import com.rs2.StartingRareItemList;
import com.rs2.bot.BotPlayer;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.ClanWarsBotManager;
import com.rs2.bot.combat.BotCombatLoadoutManager;
import com.rs2.bot.combat.WildernessBotSettings;
import com.rs2.cache.CacheStore;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.launcher.ControlPanel;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.GameplayHelper;
import com.rs2.model.NoopStartupHook;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.area.MultiwayAreaDefinition;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.gameplay.duel.DuelSession;
import com.rs2.model.gameplay.godwars.GodWarsDungeonManager;
import com.rs2.model.gameplay.magetrainingarena.AlchemistPlaygroundController;
import com.rs2.model.gameplay.magetrainingarena.CreatureGraveyardController;
import com.rs2.model.gameplay.magetrainingarena.EnchantmentChamberController;
import com.rs2.model.gameplay.partyroom.PartyRoomManager;
import com.rs2.model.grandexchange.GrandExchangePriceSample;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.consumable.FoodHandler;
import com.rs2.model.message.MessageOfTheWeek;
import com.rs2.model.music.MusicAreaDefinition;
import com.rs2.model.music.MusicTrackDefinition;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.npc.drop.NpcDropTable;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.player.PlayerConnectionState;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestEventRegistry;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.skill.fishing.FishingSpotManager;
import com.rs2.model.skill.guide.SkillGuideManager;
import com.rs2.model.task.TickTask;
import com.rs2.model.travel.DefaultTravelBootstrap;
import com.rs2.model.travel.TravelManager;
import com.rs2.net.DedicatedReactor;
import com.rs2.net.packet.PacketDispatcher;
import com.rs2.util.CharacterFileManager;
import com.rs2.util.DelayedShutdownTask;
import com.rs2.util.ElapsedTimer;
import com.rs2.util.ProfilerRegistry;
import com.rs2.util.ProfilerTimer;
import com.rs2.util.SaveAllPlayersShutdownHook;
import com.rs2.util.TimestampedPrintStream;
import com.rs2.util.path.ProjectileCollisionMap;
import com.rs2.util.path.WalkingCollisionMap;
import com.rs2.util.plugin.PluginManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server
implements Runnable {
    private static Server instance;
    private final String bindHost;
    private final int port;
    private final int cycleMillis;
    private static long elapsedMinutes;
    private static ArrayList startingRareItems;
    public static ArrayList trackedRareItems;
    private Selector selector;
    private InetSocketAddress bindAddress;
    private ServerSocketChannel serverSocketChannel;
    private ElapsedTimer cycleTimer;
    private final Queue loginQueue = new ConcurrentLinkedQueue();
    private static final Queue disconnectQueue;
    private static Thread serverThread;
    private static Thread engineThread;
    public static int serverStatus;
    public static boolean shutdownRequested;
    public static int runtimeMinutes;
    public boolean backupPending = false;
    private boolean hourlyBackupArmed = false;
    public static boolean halloweenEventActive;
    public static boolean christmasEventActive;
    public static boolean easterEventActive;
    public static int messageOfTheWeekIndex;
    private static int botLoginBatchIndex;
    private static int configuredBotCount;
    public static int botLoginBatchSize;
    public static int botLoginBatchIntervalTicks;
    private static int wildernessBotLoginCount;
    private static int skillingBotLoginCount;
    private static int progressiveBotLoginCount;
    private static int tradeBotLoginCount;
    private static int clanWarsBotLoginCount;
    private static int otherBotLoginCount;
    public static int onlinePlayerCount;
    public static int adminPlayerCount;
    public static int moderatorPlayerCount;

    static {
        new TravelManager(new DefaultTravelBootstrap());
        startingRareItems = new StartingRareItemList();
        trackedRareItems = new ArrayList();
        disconnectQueue = new LinkedList();
        serverStatus = 0;
        shutdownRequested = false;
        runtimeMinutes = 0;
        halloweenEventActive = false;
        christmasEventActive = false;
        easterEventActive = false;
        messageOfTheWeekIndex = 0;
        botLoginBatchIndex = 0;
        configuredBotCount = 0;
        botLoginBatchSize = 150;
        botLoginBatchIntervalTicks = 5;
        wildernessBotLoginCount = 0;
        skillingBotLoginCount = 0;
        progressiveBotLoginCount = 0;
        tradeBotLoginCount = 0;
        clanWarsBotLoginCount = 0;
        otherBotLoginCount = 0;
        onlinePlayerCount = 0;
        adminPlayerCount = 0;
        moderatorPlayerCount = 0;
    }

    private Server(String string, int n, int n2) {
        this.bindHost = string;
        this.port = n;
        this.cycleMillis = 600;
    }

    public static void loadConfig() {
        String configPath = "./Config.cfg";
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            ConfigFile.writeDefaultConfig();
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(configPath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("[")) continue;
                String[] parts = line.split(";");
                String key = parts[0].replace("[", "").replace("]", "");
                String[] values = new String[parts.length - 1];
                int n = 0;
                while (n < parts.length - 1) {
                    values[n] = parts[n + 1];
                    ++n;
                }
                ConfigFile.applyConfigEntry(key, values);
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            System.out.println(String.valueOf(configPath) + ": file not found.");
        }
        catch (IOException iOException) {
            System.out.println(String.valueOf(configPath) + ": error loading file.");
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String bindHost = "0.0.0.0";
        runtimeMinutes = 0;
        int n = ServerSettings.serverPort;
        Server server = new Server(bindHost, n, 600);
        if (instance != null) {
            throw new IllegalStateException("Singleton already set!");
        }
        instance = server;
        serverThread = new Thread(instance);
        serverThread.start();
    }

    public static Queue getDisconnectQueue() {
        return disconnectQueue;
    }

    public final void queueLogin(Player player) {
        long l = player.getNameHash();
        if (this.loginQueue.contains(l)) {
            System.out.println(String.valueOf(player.getUsername()) + " was already on login queue!");
            player.disconnect();
            return;
        }
        this.loginQueue.add(player);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            Object object;
            engineThread = Thread.currentThread();
            engineThread.setName("ServerEngine");
            System.setOut(new TimestampedPrintStream(System.out));
            System.setErr(new TimestampedPrintStream((OutputStream)System.err, "./data/err.log"));
            this.bindAddress = new InetSocketAddress(this.bindHost, this.port);
            System.out.println("Starting " + ServerSettings.serverName + " on " + this.bindAddress + "...");
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(2, 9);
            calendar2.set(5, 31);
            long l = calendar.getTimeInMillis();
            long l2 = calendar2.getTimeInMillis();
            long l3 = l2 - l;
            int n = (int)Math.abs(l3 / 24L / 60L / 60L / 1000L);
            if (n <= 7) {
                halloweenEventActive = true;
            }
            calendar = Calendar.getInstance();
            calendar2 = Calendar.getInstance();
            calendar2.set(2, 11);
            calendar2.set(5, 24);
            l = calendar.getTimeInMillis();
            l2 = calendar2.getTimeInMillis();
            l3 = l2 - l;
            n = (int)Math.abs(l3 / 24L / 60L / 60L / 1000L);
            if (n <= 7) {
                christmasEventActive = true;
            }
            calendar = Calendar.getInstance();
            int n2 = calendar.get(1);
            Calendar calendar3 = Calendar.getInstance();
            boolean bl = false;
            int n3 = (int)((((double)n2 / 38.0 - (double)(n2 / 38)) * 1440.0 % 60.0 / 2.0 + 56.0) % 30.0) + 1;
            if ((n2 - 5) % 19 == 0) {
                n3 += 28;
                bl = true;
            }
            calendar3.set(n2, 4, n3);
            if (bl && calendar3.get(7) == 7) {
                calendar3.add(5, 7);
            }
            calendar3.add(5, -calendar3.get(7) - 34);
            long l4 = calendar.getTimeInMillis();
            long l5 = calendar3.getTimeInMillis();
            long l6 = l5 - l4;
            int n4 = (int)Math.abs(l6 / 24L / 60L / 60L / 1000L);
            if (n4 <= 7) {
                easterEventActive = true;
            }
            serverStatus = 1;
            Server.refreshControlPanelStats();
            MessageOfTheWeek.loadAndRotateMessage();
            Object object2 = new SaveAllPlayersShutdownHook();
            Runtime.getRuntime().addShutdownHook((Thread)object2);
            PacketDispatcher.registerHandlers();
            CacheStore.initializeCacheStore();
            ProjectileDefinition.registerNpcCombatDefinitions();
            QuestDefinition.loadDefinitions();
            ItemDefinition.loadDefinitions();
            NpcDefinition.loadDefinitions();
            FoodHandler.loadPotionDefinitions();
            ShopManager.loadShops();
            InterfaceDefinition.loadDefinitions();
            PluginManager.loadPlugins();
            new WorldObjectLookup();
            WorldObjectLookup.loadWorldObjects();
            ObjectDefinition.a();
            WalkingCollisionMap.loadCollisionMaps();
            ProjectileCollisionMap.loadCollisionMaps();
            CacheCoordinateTranslator.detectDungeonCoordinateShift();
            CombatManager.initialize();
            object2 = this;
            try {
                object = new BufferedReader(new FileReader("./data/minutes.log"));
                elapsedMinutes = Integer.parseInt(((BufferedReader)object).readLine());
            }
            catch (Exception exception) {
                object = exception;
                exception.printStackTrace();
            }
            World.scheduleTickTask(new MinuteMaintenanceTickTask((Server)object2, 100));
            GameplayHelper.loadGroundItemSpawns();
            FishingSpotManager.spawnFishingSpots();
            MusicTrackDefinition.loadDefinitions();
            MusicAreaDefinition.loadDefinitions();
            MultiwayAreaDefinition.loadDefinitions();
            PartyRoomManager.loadPartyChest();
            NpcDropTable.loadDropTables();
            GameplayHelper.loadNpcSpawns();
            TreasureTrailManager.filterRewardItemPools();
            BotCombatLoadoutManager.initializeCombatLoadoutTypes();
            SkillGuideManager.initialize();
            GrandExchangePriceSample.loadPriceSamples();
            new NoopStartupHook();
            object2 = CacheStore.getInstance();
            ((CacheStore)object2).close();
            AlchemistPlaygroundController.startCupboardRotation();
            EnchantmentChamberController.startBonusColorCycle();
            CreatureGraveyardController.startFallingBoneHazards();
            object2 = this;
            this.selector = Selector.open();
            ((Server)object2).serverSocketChannel = ServerSocketChannel.open();
            DedicatedReactor.setInstance(new DedicatedReactor(Selector.open()));
            DedicatedReactor.getInstance().start();
            ((Server)object2).serverSocketChannel.configureBlocking(false);
            ((Server)object2).serverSocketChannel.socket().bind(((Server)object2).bindAddress);
            object = DedicatedReactor.getInstance();
            synchronized (object) {
                DedicatedReactor.getInstance().getSelector().wakeup();
                ((Server)object2).serverSocketChannel.register(DedicatedReactor.getInstance().getSelector(), 16);
            }
            ((Server)object2).cycleTimer = new ElapsedTimer();
            serverStatus = 2;
            long l7 = System.currentTimeMillis();
            shutdownRequested = false;
            for (Object itemStackObject : startingRareItems) {
                ItemStack itemStack = (ItemStack)itemStackObject;
                trackedRareItems.add(itemStack);
            }
            if (ServerSettings.content2007Enabled) {
                GodWarsDungeonManager.spawnGodWarsNpcs();
            }
            System.out.println("Online!");
            BotTaskDefinition.initializeProgressiveTaskPool();
            BotTaskDefinition.initializeTradeAdvertTaskPool();
            BotTaskDefinition.initializeDropPartyTaskPool();
            QuestEventRegistry.initializeEventHooks();
            BotPlayer.removeConfiguredBotNames();
            if (ServerSettings.progressiveBotsPrioritizeExisting && ServerSettings.progressiveBotsEnabled && ServerSettings.progressiveBotCount > 0) {
                BotPlayer.loadExistingProgressiveBotNames();
            }
            BotPlayer.prepareBotNamePools();
            if (ServerSettings.progressiveBotsEnabled) {
                ServerSettings.skillingBotsEnabled = false;
                ServerSettings.walkingBotsEnabled = true;
            }
            if (WildernessBotSettings.wildyBotCount <= 0 && ServerSettings.wildyBotsEnabled) {
                ServerSettings.wildyBotsEnabled = false;
            }
            if (ServerSettings.skillingBotCount <= 0 && ServerSettings.skillingBotsEnabled) {
                ServerSettings.skillingBotsEnabled = false;
            }
            if (ServerSettings.progressiveBotCount <= 0 && ServerSettings.progressiveBotsEnabled) {
                ServerSettings.progressiveBotsEnabled = false;
            }
            if (ServerSettings.tradeBotCount <= 0 && ServerSettings.tradeBotsEnabled) {
                ServerSettings.tradeBotsEnabled = false;
            }
            if (ServerSettings.clanWarsTeamSize <= 0 && ServerSettings.clanWarsBotsEnabled) {
                ServerSettings.clanWarsBotsEnabled = false;
            }
            configuredBotCount = 0;
            if (ServerSettings.wildyBotsEnabled) {
                configuredBotCount += WildernessBotSettings.wildyBotCount;
            }
            if (ServerSettings.progressiveBotsEnabled || ServerSettings.skillingBotsEnabled) {
                configuredBotCount = !ServerSettings.progressiveBotsEnabled ? (configuredBotCount += ServerSettings.skillingBotCount) : (configuredBotCount += ServerSettings.progressiveBotCount);
            }
            if (ServerSettings.tradeBotsEnabled) {
                configuredBotCount += ServerSettings.tradeBotCount;
            }
            if (ServerSettings.clanWarsBotsEnabled) {
                configuredBotCount += ServerSettings.clanWarsTeamSize << 1;
            }
            if (ServerSettings.otherBotsEnabled) {
                configuredBotCount += ServerSettings.otherBotCount;
            }
            botLoginBatchIndex = 0;
            int n5 = configuredBotCount / botLoginBatchSize;
            if (n5 == 0) {
                Server.loginAllConfiguredBots();
            } else {
                object = new BotLoginBatchTask(botLoginBatchIntervalTicks, n5);
                World.getTaskScheduler().schedule((TickTask)object);
            }
            if (ServerSettings.lanConnectionsEnabled) {
                LanDiscoveryService.startListener();
            }
            if (ServerSettings.mysqlHiscoresEnabled) {
                HiscoresDatabase.connect();
            }
            while (!Thread.interrupted() && !shutdownRequested) {
                try {
                    long l8 = System.currentTimeMillis() - l7;
                    runtimeMinutes = (int)(l8 / 1000L / 60L);
                    if (ServerSettings.backupCharactersEnabled) {
                        if (runtimeMinutes % 60 == 0 && this.hourlyBackupArmed) {
                            if (!this.backupPending && World.getPlayerCount() > 0) {
                                this.backupPending = true;
                            }
                            if (this.backupPending) {
                                CharacterFileManager.saveAllPlayers();
                                CharacterFileManager.createTimestampedBackup();
                                System.out.println("Creating hourly backups of character files.");
                            } else {
                                System.out.println("No backup files created, reason: No players have been online");
                            }
                            this.backupPending = false;
                            this.hourlyBackupArmed = false;
                        }
                        if (runtimeMinutes % 60 != 0) {
                            this.hourlyBackupArmed = true;
                        }
                    }
                    if (!(ServerSettings.wildyBotsEnabled || ServerSettings.skillingBotsEnabled || ServerSettings.tradeBotsEnabled)) {
                    }
                    Server.refreshControlPanelStats();
                    this.processGameCycle();
                    this.sleepUntilNextCycle();
                }
                catch (Exception exception) {
                    CharacterFileManager.saveAllPlayers();
                    if (ServerSettings.mysqlHiscoresEnabled) {
                        HiscoresDatabase.disconnect();
                    }
                    exception.printStackTrace();
                }
            }
            CharacterFileManager.saveAllPlayers();
            if (ServerSettings.mysqlHiscoresEnabled) {
                HiscoresDatabase.disconnect();
            }
            serverStatus = 0;
            Server.refreshControlPanelStats();
            instance = null;
            serverThread.stop();
            serverThread = null;
            engineThread = null;
            ((Thread)null).stop();
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
        }
        PluginManager.shutdownGlobalPlugins();
    }

    private static void loginAllConfiguredBots() {
        int n = 1;
        while (n <= configuredBotCount) {
            if (n > 0 && n < ServerSettings.botLoginIdLimit) {
                BotPlayer.createBotFromPool(n, "zxcvbn", Server.selectNextBotType());
            }
            ++n;
        }
    }

    public static int selectNextBotType() {
        if (ServerSettings.wildyBotsEnabled && wildernessBotLoginCount < WildernessBotSettings.wildyBotCount) {
            ++wildernessBotLoginCount;
            return 1;
        }
        if (ServerSettings.progressiveBotsEnabled || ServerSettings.skillingBotsEnabled) {
            if (!ServerSettings.progressiveBotsEnabled) {
                if (skillingBotLoginCount < ServerSettings.skillingBotCount) {
                    ++skillingBotLoginCount;
                    return 0;
                }
            } else if (progressiveBotLoginCount < ServerSettings.progressiveBotCount) {
                ++progressiveBotLoginCount;
                return 4;
            }
        }
        if (ServerSettings.tradeBotsEnabled && tradeBotLoginCount < ServerSettings.tradeBotCount) {
            ++tradeBotLoginCount;
            return 2;
        }
        if (ServerSettings.clanWarsBotsEnabled && clanWarsBotLoginCount < ServerSettings.clanWarsTeamSize << 1) {
            int n = ClanWarsBotManager.clanWarsTeamOneBots.size() == ServerSettings.clanWarsTeamSize ? 6 : 5;
            ++clanWarsBotLoginCount;
            return n;
        }
        if (ServerSettings.otherBotsEnabled && otherBotLoginCount < ServerSettings.otherBotCount) {
            ++otherBotLoginCount;
            return 3;
        }
        System.out.println("ERROR at getBotType, did not find type for bot!");
        return -1;
    }

    public static void d() {
    }

    private static void refreshControlPanelStats() {
        onlinePlayerCount = World.getPlayerCount();
        adminPlayerCount = World.getAdminCount();
        moderatorPlayerCount = World.getModeratorCount();
        ControlPanel.refreshStatusDisplay();
    }

    public static void broadcastServerMessage(String string) {
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null && player.getIndex() != -1) {
                player.packetSender.sendGameMessage("[SERVER]: " + string);
            }
            ++n2;
        }
    }

    public static void acceptConnection(SelectionKey selectionKey) {
        try {
            Object object = (ServerSocketChannel)selectionKey.channel();
            if ((object = ((ServerSocketChannel)object).accept()) == null) {
                return;
            }
            if (!ConnectionThrottle.tryAcquireConnectionSlot(((SocketChannel)object).socket().getInetAddress().getHostAddress())) {
                ((AbstractInterruptibleChannel)object).close();
                return;
            }
            ((AbstractSelectableChannel)object).configureBlocking(false);
            selectionKey = ((SelectableChannel)object).register(selectionKey.selector(), 1);
            object = new Player(selectionKey);
            selectionKey.attach(object);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    private void processGameCycle() {
        ProfilerTimer elapsedTimer = ProfilerRegistry.getTimer("loginQueue");
        elapsedTimer.start();
        Player player;
        while ((player = (Player)this.loginQueue.poll()) != null) {
            try {
                player.processPostLogin();
                player.setConnectionState(PlayerConnectionState.IN_GAME);
            }
            catch (Exception exception) {
                exception.printStackTrace();
                player.disconnect();
            }
        }
        elapsedTimer.stop();
        elapsedTimer = ProfilerRegistry.getTimer("handleNetworkPackets");
        elapsedTimer.start();
        try {
            this.selector.selectNow();
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        for (Object selectedKeyObject : this.selector.selectedKeys()) {
            SelectionKey selectionKey = (SelectionKey)selectedKeyObject;
            if (!selectionKey.isValid() || !selectionKey.isReadable()) continue;
            PacketDispatcher.processIncoming((Player)selectionKey.attachment());
        }
        elapsedTimer.stop();
        PluginManager.tickGlobalPlugins();
        World.processTick();
        elapsedTimer = ProfilerRegistry.getTimer("disconnectingPlayers");
        elapsedTimer.start();
        synchronized (Server.disconnectQueue) {
            Iterator iterator = Server.disconnectQueue.iterator();
            while (iterator.hasNext()) {
                Player disconnectingPlayer = (Player)iterator.next();
                boolean keepQueued = disconnectingPlayer.grandExchangeSettlementInProgress || !disconnectingPlayer.cn && (disconnectingPlayer.getConnectionState() != PlayerConnectionState.DISCONNECTING || disconnectingPlayer.getDisconnectGraceExpiresAtMillis() >= System.currentTimeMillis()) && !disconnectingPlayer.getRecentCombatTimer().hasElapsed();
                if (keepQueued) continue;
                try {
                    try {
                        ProfilerTimer cleanupTimer = ProfilerRegistry.getTimer("tradeDecline");
                        cleanupTimer.start();
                        if (disconnectingPlayer.getTradePartner() != null) {
                            GameplayHelper.declineTrade(disconnectingPlayer);
                        }
                        PartyRoomManager.returnStagedChestItems(disconnectingPlayer);
                        cleanupTimer.stop();
                        cleanupTimer = ProfilerRegistry.getTimer("duelDecline");
                        cleanupTimer.start();
                        if (disconnectingPlayer.getDuelSession().getOpponent() != null) {
                            if (disconnectingPlayer.getDuelSession().isStarted()) {
                                DuelSession.finishDuelVictory(disconnectingPlayer.getDuelSession().getOpponent(), disconnectingPlayer);
                            } else {
                                disconnectingPlayer.getDuelSession().getOpponent().getDuelController().resetDuel(true);
                                disconnectingPlayer.getDuelController().resetDuel(true);
                            }
                        }
                        disconnectingPlayer.clearTemporaryCutsceneNpcs();
                        cleanupTimer.stop();
                        cleanupTimer = ProfilerRegistry.getTimer("petUnregister");
                        cleanupTimer.start();
                        if (disconnectingPlayer.getPetManager().getActivePetNpc() != null) {
                            disconnectingPlayer.getPetManager().pickupPet();
                        }
                        cleanupTimer.stop();
                        cleanupTimer = ProfilerRegistry.getTimer("endFightCave");
                        cleanupTimer.start();
                        disconnectingPlayer.getFightCaveController().cleanupIfInFightCave();
                        cleanupTimer.stop();
                        cleanupTimer = ProfilerRegistry.getTimer("unlockMovement");
                        cleanupTimer.start();
                        disconnectingPlayer.getMovementQueue();
                        cleanupTimer.stop();
                        cleanupTimer = ProfilerRegistry.getTimer("resetFollowing");
                        cleanupTimer.start();
                        if (disconnectingPlayer.getMovementTarget() != null) {
                            EntityTargetMovement.clearMovementTarget(disconnectingPlayer);
                        }
                        cleanupTimer.stop();
                        cleanupTimer = ProfilerRegistry.getTimer("logoutPrivatemessage");
                        cleanupTimer.start();
                        disconnectingPlayer.getSocialManager().refreshFriendStatuses(true);
                        cleanupTimer.stop();
                        if (disconnectingPlayer.isInArea(1790, 2045, 4800, 4915)) {
                            disconnectingPlayer.moveTo(new Position(3053, 3246, 0));
                        }
                        CharacterFileManager.savePlayer(disconnectingPlayer);
                        CharacterFileManager.refreshLiveHiscoreRecord(disconnectingPlayer);
                        if (ServerSettings.mysqlHiscoresEnabled) {
                            HiscoresDatabase.savePlayer(disconnectingPlayer);
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                finally {
                    ProfilerTimer unregisterTimer = ProfilerRegistry.getTimer("unregisterPlayer");
                    unregisterTimer.start();
                    World.unregisterPlayer(disconnectingPlayer);
                    unregisterTimer.stop();
                }
                iterator.remove();
            }
        }
        elapsedTimer.stop();
    }

    private void sleepUntilNextCycle() {
        try {
            long elapsedMillis = this.cycleTimer.elapsedMillis();
            long remainingMillis = (long)this.cycleMillis - elapsedMillis;
            if (remainingMillis > 0L && remainingMillis <= 600L) {
                Thread.sleep(remainingMillis);
            } else {
                long loadPercent = 100L + (Math.abs(remainingMillis) - (long)this.cycleMillis) / 6L;
                System.out.println("[WARNING] Server Load is at " + loadPercent + "%");
                ProfilerRegistry.b();
                ProfilerRegistry.resetAll();
                System.out.println();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            this.cycleTimer.reset();
            int packetId = 0;
            while (packetId < 256) {
                ProfilerTimer packetTimer = PacketDispatcher.packetTimers[packetId];
                packetTimer.getAccumulatedMillis();
                packetTimer.reset();
                ++packetId;
            }
        }
    }

    public static void scheduleShutdown(boolean bl) {
        int n = 300;
        if (World.getPlayerCount() == 0 || bl) {
            n = 1;
        }
        Player[] playerArray = World.getPlayers();
        int n2 = playerArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null && player.getIndex() != -1) {
                player.packetSender.sendSystemUpdateTimer(n / 30 * 50 + 1);
            }
            ++n3;
        }
        Runnable runnable = new DelayedShutdownTask(n + 3);
        runnable = new Thread(runnable);
        ((Thread)runnable).start();
        serverStatus = 3;
    }

    public static void setElapsedMinutes(long l) {
        elapsedMinutes = l;
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./data/minutes.log"));
            bufferedWriter.write(Long.toString(elapsedMinutes));
            bufferedWriter.close();
            return;
        }
        catch (IOException iOException) {
            IOException iOException2 = iOException;
            iOException.printStackTrace();
            return;
        }
    }

    public static long getElapsedMinutes() {
        return elapsedMinutes;
    }

    public static Server getInstance() {
        return instance;
    }

    public final Selector getSelector() {
        return this.selector;
    }

    static /* synthetic */ int getBotLoginBatchIndex() {
        return botLoginBatchIndex;
    }

    static /* synthetic */ int getConfiguredBotCount() {
        return configuredBotCount;
    }

    static /* synthetic */ void setBotLoginBatchIndex(int n) {
        botLoginBatchIndex = n;
    }
}
