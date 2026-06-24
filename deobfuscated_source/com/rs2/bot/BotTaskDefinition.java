/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.ServerSettings;
import com.rs2.bot.BotCombatTickTask;
import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskPlanner;
import com.rs2.bot.BotTradeAdvertManager;
import com.rs2.bot.BrassKeyBotTaskList;
import com.rs2.bot.CombatBotTaskList;
import com.rs2.bot.CookingBotTaskList;
import com.rs2.bot.DropPartyBotManager;
import com.rs2.bot.FishingBotTaskList;
import com.rs2.bot.LeatherCraftingBotTaskList;
import com.rs2.bot.MiningBotTaskList;
import com.rs2.bot.MoneyMakingBotTaskList;
import com.rs2.bot.RunecraftingBotTaskList;
import com.rs2.bot.SheepShearingBotTaskList;
import com.rs2.bot.ShopBotTaskList;
import com.rs2.bot.SmeltingBotTaskList;
import com.rs2.bot.SmithingBotTaskList;
import com.rs2.bot.SpinningBotTaskList;
import com.rs2.bot.TanningBotTaskList;
import com.rs2.bot.WoodcuttingBotTaskList;
import com.rs2.bot.route.BotWorldRouteWalker;
import com.rs2.bot.tasks.AirRuneRunecraftingBotTask;
import com.rs2.bot.tasks.AlKharidFlyFishingBotTask;
import com.rs2.bot.tasks.AlKharidLobsterCookingBotTask;
import com.rs2.bot.tasks.AlKharidMineBotTask;
import com.rs2.bot.tasks.AlKharidNetBaitFishingBotTask;
import com.rs2.bot.tasks.AlKharidSteelSmeltingBotTask;
import com.rs2.bot.tasks.AlKharidWarriorCombatBotTask;
import com.rs2.bot.tasks.BarbarianVillageBarbarianCombatBotTask;
import com.rs2.bot.tasks.BarbarianVillageFlyFishingBotTask;
import com.rs2.bot.tasks.BodyRuneRunecraftingBotTask;
import com.rs2.bot.tasks.BrimhavenDungeonBlueDragonNorthCombatBotTask;
import com.rs2.bot.tasks.BrimhavenDungeonBlueDragonSouthCombatBotTask;
import com.rs2.bot.tasks.BrimhavenDungeonRedDragonCombatBotTask;
import com.rs2.bot.tasks.CatherbyFishingBotTask;
import com.rs2.bot.tasks.CatherbyLobsterCookingBotTask;
import com.rs2.bot.tasks.CraftingGuildMineBotTask;
import com.rs2.bot.tasks.DraynorChickenCombatBotTask;
import com.rs2.bot.tasks.DraynorGoblinCombatBotTask;
import com.rs2.bot.tasks.DraynorNetFishingBotTask;
import com.rs2.bot.tasks.DraynorOakWoodcuttingBotTask;
import com.rs2.bot.tasks.DraynorSheepShearingBotTask;
import com.rs2.bot.tasks.DraynorTreeWoodcuttingBotTask;
import com.rs2.bot.tasks.DraynorWillowWoodcuttingBotTask;
import com.rs2.bot.tasks.DraynorYewWoodcuttingBotTask;
import com.rs2.bot.tasks.DwarvenMineBotTask;
import com.rs2.bot.tasks.DwarvenMineDwarfCombatBotTask;
import com.rs2.bot.tasks.EarthRuneRunecraftingBotTask;
import com.rs2.bot.tasks.EdgevilleDungeonHillGiantCombatBotTask;
import com.rs2.bot.tasks.EdgevilleDungeonNorthMossGiantCombatBotTask;
import com.rs2.bot.tasks.EdgevilleDungeonSkeletonCombatBotTask;
import com.rs2.bot.tasks.EdgevilleDungeonSouthMossGiantCombatBotTask;
import com.rs2.bot.tasks.EdgevilleDungeonSpiderRatCombatBotTask;
import com.rs2.bot.tasks.EdgevilleTradeAdvertBotTask;
import com.rs2.bot.tasks.EdgevilleTreeWoodcuttingBotTask;
import com.rs2.bot.tasks.EdgevilleYewWoodcuttingBotTask;
import com.rs2.bot.tasks.FaladorCowCombatBotTask;
import com.rs2.bot.tasks.FaladorDropPartyBotTask;
import com.rs2.bot.tasks.FaladorGuardCombatBotTask;
import com.rs2.bot.tasks.FaladorImpCombatBotTask;
import com.rs2.bot.tasks.FaladorSteelSmeltingBotTask;
import com.rs2.bot.tasks.FaladorTradeAdvertBotTask;
import com.rs2.bot.tasks.FaladorWineOfZamorakTelegrabBotTask;
import com.rs2.bot.tasks.FaladorYewWoodcuttingBotTask;
import com.rs2.bot.tasks.FireRuneRunecraftingBotTask;
import com.rs2.bot.tasks.KaramjaFishingBotTask;
import com.rs2.bot.tasks.KaramjaVolcanoNorthLesserDemonCombatBotTask;
import com.rs2.bot.tasks.KaramjaVolcanoSouthLesserDemonCombatBotTask;
import com.rs2.bot.tasks.LumbridgeCowCombatBotTask;
import com.rs2.bot.tasks.LumbridgeEastChickenCombatBotTask;
import com.rs2.bot.tasks.LumbridgeGoblinCombatBotTask;
import com.rs2.bot.tasks.LumbridgeWestChickenCombatBotTask;
import com.rs2.bot.tasks.LumbridgeWoolSpinningBotTask;
import com.rs2.bot.tasks.MindRuneRunecraftingBotTask;
import com.rs2.bot.tasks.MiningGuildMineBotTask;
import com.rs2.bot.tasks.RimmingtonMineBotTask;
import com.rs2.bot.tasks.SeersFlaxPickingBotTask;
import com.rs2.bot.tasks.SeersFlaxSpinningBotTask;
import com.rs2.bot.tasks.SeersMagicTreeWoodcuttingBotTask;
import com.rs2.bot.tasks.SeersMapleWoodcuttingBotTask;
import com.rs2.bot.tasks.SeersTradeAdvertBotTask;
import com.rs2.bot.tasks.SeersYewWoodcuttingBotTask;
import com.rs2.bot.tasks.SorcerersTowerMagicTreeWoodcuttingBotTask;
import com.rs2.bot.tasks.TaverleyDungeonHellhoundCombatBotTask;
import com.rs2.bot.tasks.VarrockDropPartyBotTask;
import com.rs2.bot.tasks.VarrockEastMineBotTask;
import com.rs2.bot.tasks.VarrockEastOakWoodcuttingBotTask;
import com.rs2.bot.tasks.VarrockEastTreeWoodcuttingBotTask;
import com.rs2.bot.tasks.VarrockGuardCombatBotTask;
import com.rs2.bot.tasks.VarrockLobsterCookingBotTask;
import com.rs2.bot.tasks.VarrockPalaceYewWoodcuttingBotTask;
import com.rs2.bot.tasks.VarrockRuneEssenceMiningBotTask;
import com.rs2.bot.tasks.VarrockSewerGiantRatCombatBotTask;
import com.rs2.bot.tasks.VarrockSouthChickenCombatBotTask;
import com.rs2.bot.tasks.VarrockSteelDaggerSmithingBotTask;
import com.rs2.bot.tasks.VarrockTradeAdvertBotTask;
import com.rs2.bot.tasks.VarrockWestMineBotTask;
import com.rs2.bot.tasks.VarrockWestOakWoodcuttingBotTask;
import com.rs2.bot.tasks.VarrockWestTreeWoodcuttingBotTask;
import com.rs2.bot.tasks.WaterRuneRunecraftingBotTask;
import com.rs2.bot.tasks.WildernessRuniteMineBotTask;
import com.rs2.bot.tasks.WizardsTowerLesserDemonMagicBotTask;
import com.rs2.cache.CacheArchiveEntry;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.shop.ShopDefinition;
import com.rs2.model.shop.ShopManager;
import com.rs2.util.GameUtil;
import com.rs2.util.RectangularArea;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class BotTaskDefinition {
    public static boolean a = false;
    private static AlKharidMineBotTask alKharidMineTask = new AlKharidMineBotTask(6);
    static BrimhavenDungeonBlueDragonNorthCombatBotTask brimhavenDungeonBlueDragonNorthCombatTask = new BrimhavenDungeonBlueDragonNorthCombatBotTask(4);
    private static BrimhavenDungeonBlueDragonSouthCombatBotTask brimhavenDungeonBlueDragonSouthCombatTask = new BrimhavenDungeonBlueDragonSouthCombatBotTask(6);
    private static BrimhavenDungeonRedDragonCombatBotTask brimhavenDungeonRedDragonCombatTask = new BrimhavenDungeonRedDragonCombatBotTask(2);
    private static CatherbyFishingBotTask catherbyFishingTask = new CatherbyFishingBotTask(2);
    private static CraftingGuildMineBotTask craftingGuildMineTask = new CraftingGuildMineBotTask(1);
    private static DraynorNetFishingBotTask draynorNetFishingTask = new DraynorNetFishingBotTask(3);
    static DraynorWillowWoodcuttingBotTask draynorWillowWoodcuttingTask = new DraynorWillowWoodcuttingBotTask(4);
    private static BarbarianVillageFlyFishingBotTask barbarianVillageFlyFishingTask = new BarbarianVillageFlyFishingBotTask(3);
    private static EdgevilleYewWoodcuttingBotTask edgevilleYewWoodcuttingTask = new EdgevilleYewWoodcuttingBotTask(4);
    private static DraynorChickenCombatBotTask draynorChickenCombatTask = new DraynorChickenCombatBotTask(1);
    private static FaladorCowCombatBotTask faladorCowCombatTask = new FaladorCowCombatBotTask(4);
    static DwarvenMineBotTask dwarvenMineTask = new DwarvenMineBotTask(4);
    private static FaladorYewWoodcuttingBotTask faladorYewWoodcuttingTask = new FaladorYewWoodcuttingBotTask(4);
    private static KaramjaFishingBotTask karamjaFishingTask = new KaramjaFishingBotTask(4);
    private static KaramjaVolcanoNorthLesserDemonCombatBotTask karamjaVolcanoNorthLesserDemonCombatTask = new KaramjaVolcanoNorthLesserDemonCombatBotTask(4);
    private static KaramjaVolcanoSouthLesserDemonCombatBotTask karamjaVolcanoSouthLesserDemonCombatTask = new KaramjaVolcanoSouthLesserDemonCombatBotTask(2);
    private static LumbridgeEastChickenCombatBotTask lumbridgeEastChickenCombatTask = new LumbridgeEastChickenCombatBotTask(2);
    private static LumbridgeWestChickenCombatBotTask lumbridgeWestChickenCombatTask = new LumbridgeWestChickenCombatBotTask(1);
    private static LumbridgeCowCombatBotTask lumbridgeCowCombatTask = new LumbridgeCowCombatBotTask(10);
    private static MiningGuildMineBotTask miningGuildMineTask = new MiningGuildMineBotTask(6);
    private static TaverleyDungeonHellhoundCombatBotTask taverleyDungeonHellhoundCombatTask = new TaverleyDungeonHellhoundCombatBotTask(4);
    private static VarrockEastMineBotTask varrockEastMineTask = new VarrockEastMineBotTask(8);
    static RimmingtonMineBotTask rimmingtonMineTask = new RimmingtonMineBotTask(8);
    private static EdgevilleDungeonHillGiantCombatBotTask edgevilleDungeonHillGiantCombatTask = new EdgevilleDungeonHillGiantCombatBotTask(10);
    private static EdgevilleDungeonNorthMossGiantCombatBotTask edgevilleDungeonNorthMossGiantCombatTask = new EdgevilleDungeonNorthMossGiantCombatBotTask(2);
    private static EdgevilleDungeonSouthMossGiantCombatBotTask edgevilleDungeonSouthMossGiantCombatTask = new EdgevilleDungeonSouthMossGiantCombatBotTask(2);
    static VarrockWestMineBotTask varrockWestMineTask = new VarrockWestMineBotTask(3);
    private static VarrockPalaceYewWoodcuttingBotTask varrockPalaceYewWoodcuttingTask = new VarrockPalaceYewWoodcuttingBotTask(4);
    private static SeersMapleWoodcuttingBotTask seersMapleWoodcuttingTask = new SeersMapleWoodcuttingBotTask(2);
    private static SeersYewWoodcuttingBotTask seersYewWoodcuttingTask = new SeersYewWoodcuttingBotTask(2);
    private static SeersMagicTreeWoodcuttingBotTask seersMagicTreeWoodcuttingTask = new SeersMagicTreeWoodcuttingBotTask(2);
    private static SorcerersTowerMagicTreeWoodcuttingBotTask sorcerersTowerMagicTreeWoodcuttingTask = new SorcerersTowerMagicTreeWoodcuttingBotTask(2);
    static SeersFlaxPickingBotTask seersFlaxPickingTask = new SeersFlaxPickingBotTask(4);
    private static FaladorWineOfZamorakTelegrabBotTask faladorWineOfZamorakTelegrabTask = new FaladorWineOfZamorakTelegrabBotTask(1);
    private static DraynorOakWoodcuttingBotTask draynorOakWoodcuttingTask = new DraynorOakWoodcuttingBotTask(3);
    private static DraynorTreeWoodcuttingBotTask draynorTreeWoodcuttingTask = new DraynorTreeWoodcuttingBotTask(3);
    private static VarrockWestOakWoodcuttingBotTask varrockWestOakWoodcuttingTask = new VarrockWestOakWoodcuttingBotTask(3);
    private static VarrockWestTreeWoodcuttingBotTask varrockWestTreeWoodcuttingTask = new VarrockWestTreeWoodcuttingBotTask(3);
    private static VarrockEastOakWoodcuttingBotTask varrockEastOakWoodcuttingTask = new VarrockEastOakWoodcuttingBotTask(6);
    private static VarrockEastTreeWoodcuttingBotTask varrockEastTreeWoodcuttingTask = new VarrockEastTreeWoodcuttingBotTask(3);
    private static EdgevilleTreeWoodcuttingBotTask edgevilleTreeWoodcuttingTask = new EdgevilleTreeWoodcuttingBotTask(1);
    private static DraynorYewWoodcuttingBotTask draynorYewWoodcuttingTask = new DraynorYewWoodcuttingBotTask(4);
    private static DraynorGoblinCombatBotTask draynorGoblinCombatTask = new DraynorGoblinCombatBotTask(6);
    static LumbridgeGoblinCombatBotTask lumbridgeGoblinCombatTask = new LumbridgeGoblinCombatBotTask(4);
    private static AlKharidFlyFishingBotTask alKharidFlyFishingTask = new AlKharidFlyFishingBotTask(2);
    private static AlKharidNetBaitFishingBotTask alKharidNetBaitFishingTask = new AlKharidNetBaitFishingBotTask(4);
    private static AlKharidWarriorCombatBotTask alKharidWarriorCombatTask = new AlKharidWarriorCombatBotTask(3);
    private static VarrockGuardCombatBotTask varrockGuardCombatTask = new VarrockGuardCombatBotTask(3);
    private static VarrockSewerGiantRatCombatBotTask varrockSewerGiantRatCombatTask = new VarrockSewerGiantRatCombatBotTask(1);
    private static BarbarianVillageBarbarianCombatBotTask barbarianVillageBarbarianCombatTask = new BarbarianVillageBarbarianCombatBotTask(2);
    private static DwarvenMineDwarfCombatBotTask dwarvenMineDwarfCombatTask = new DwarvenMineDwarfCombatBotTask(1);
    private static FaladorGuardCombatBotTask faladorGuardCombatTask = new FaladorGuardCombatBotTask(3);
    private static EdgevilleDungeonSpiderRatCombatBotTask edgevilleDungeonSpiderRatCombatTask = new EdgevilleDungeonSpiderRatCombatBotTask(1);
    private static EdgevilleDungeonSkeletonCombatBotTask edgevilleDungeonSkeletonCombatTask = new EdgevilleDungeonSkeletonCombatBotTask(2);
    private static VarrockRuneEssenceMiningBotTask varrockRuneEssenceMiningTask = new VarrockRuneEssenceMiningBotTask(6);
    private static AirRuneRunecraftingBotTask airRuneRunecraftingTask = new AirRuneRunecraftingBotTask(1);
    private static MindRuneRunecraftingBotTask mindRuneRunecraftingTask = new MindRuneRunecraftingBotTask(1);
    private static WaterRuneRunecraftingBotTask waterRuneRunecraftingTask = new WaterRuneRunecraftingBotTask(1);
    private static EarthRuneRunecraftingBotTask earthRuneRunecraftingTask = new EarthRuneRunecraftingBotTask(1);
    private static FireRuneRunecraftingBotTask fireRuneRunecraftingTask = new FireRuneRunecraftingBotTask(1);
    private static BodyRuneRunecraftingBotTask bodyRuneRunecraftingTask = new BodyRuneRunecraftingBotTask(1);
    private static AlKharidLobsterCookingBotTask alKharidLobsterCookingTask = new AlKharidLobsterCookingBotTask(1);
    private static AlKharidSteelSmeltingBotTask alKharidSteelSmeltingTask = new AlKharidSteelSmeltingBotTask(1);
    private static FaladorSteelSmeltingBotTask faladorSteelSmeltingTask = new FaladorSteelSmeltingBotTask(1);
    private static VarrockLobsterCookingBotTask varrockLobsterCookingTask = new VarrockLobsterCookingBotTask(1);
    static VarrockSteelDaggerSmithingBotTask varrockSteelDaggerSmithingTask = new VarrockSteelDaggerSmithingBotTask(1);
    private static WildernessRuniteMineBotTask wildernessRuniteMineTask = new WildernessRuniteMineBotTask(1);
    private static VarrockSouthChickenCombatBotTask varrockSouthChickenCombatTask = new VarrockSouthChickenCombatBotTask(1);
    static WizardsTowerLesserDemonMagicBotTask wizardsTowerLesserDemonMagicTask = new WizardsTowerLesserDemonMagicBotTask(1);
    static FaladorImpCombatBotTask faladorImpCombatTask = new FaladorImpCombatBotTask(1);
    private static DraynorSheepShearingBotTask draynorSheepShearingTask = new DraynorSheepShearingBotTask(4);
    private static LumbridgeWoolSpinningBotTask lumbridgeWoolSpinningTask = new LumbridgeWoolSpinningBotTask(4);
    private static CatherbyLobsterCookingBotTask catherbyLobsterCookingTask = new CatherbyLobsterCookingBotTask(1);
    private static SeersFlaxSpinningBotTask seersFlaxSpinningTask = new SeersFlaxSpinningBotTask(4);
    private static BotTaskDefinition[] progressiveTaskDefinitions = new BotTaskDefinition[]{alKharidMineTask, brimhavenDungeonBlueDragonNorthCombatTask, brimhavenDungeonBlueDragonSouthCombatTask, brimhavenDungeonRedDragonCombatTask, catherbyFishingTask, craftingGuildMineTask, draynorNetFishingTask, draynorWillowWoodcuttingTask, barbarianVillageFlyFishingTask, edgevilleYewWoodcuttingTask, draynorChickenCombatTask, faladorCowCombatTask, dwarvenMineTask, faladorYewWoodcuttingTask, karamjaFishingTask, karamjaVolcanoNorthLesserDemonCombatTask, karamjaVolcanoSouthLesserDemonCombatTask, lumbridgeEastChickenCombatTask, lumbridgeWestChickenCombatTask, lumbridgeCowCombatTask, miningGuildMineTask, taverleyDungeonHellhoundCombatTask, varrockEastMineTask, edgevilleDungeonHillGiantCombatTask, edgevilleDungeonNorthMossGiantCombatTask, edgevilleDungeonSouthMossGiantCombatTask, varrockWestMineTask, varrockPalaceYewWoodcuttingTask, seersMapleWoodcuttingTask, seersYewWoodcuttingTask, seersMagicTreeWoodcuttingTask, sorcerersTowerMagicTreeWoodcuttingTask, seersFlaxPickingTask, faladorWineOfZamorakTelegrabTask, draynorOakWoodcuttingTask, draynorTreeWoodcuttingTask, varrockWestOakWoodcuttingTask, varrockWestTreeWoodcuttingTask, varrockEastOakWoodcuttingTask, varrockEastTreeWoodcuttingTask, edgevilleTreeWoodcuttingTask, draynorYewWoodcuttingTask, draynorGoblinCombatTask, lumbridgeGoblinCombatTask, alKharidFlyFishingTask, alKharidNetBaitFishingTask, alKharidWarriorCombatTask, varrockGuardCombatTask, varrockSewerGiantRatCombatTask, barbarianVillageBarbarianCombatTask, dwarvenMineDwarfCombatTask, faladorGuardCombatTask, edgevilleDungeonSpiderRatCombatTask, edgevilleDungeonSkeletonCombatTask, varrockRuneEssenceMiningTask, airRuneRunecraftingTask, mindRuneRunecraftingTask, waterRuneRunecraftingTask, earthRuneRunecraftingTask, fireRuneRunecraftingTask, bodyRuneRunecraftingTask, alKharidLobsterCookingTask, alKharidSteelSmeltingTask, faladorSteelSmeltingTask, varrockLobsterCookingTask, varrockSteelDaggerSmithingTask, wildernessRuniteMineTask, varrockSouthChickenCombatTask, wizardsTowerLesserDemonMagicTask, faladorImpCombatTask, draynorSheepShearingTask, lumbridgeWoolSpinningTask, catherbyLobsterCookingTask, seersFlaxSpinningTask, rimmingtonMineTask};
    private static BotTaskDefinition[] tradeAdvertTaskDefinitions = new BotTaskDefinition[]{new VarrockTradeAdvertBotTask(30), new FaladorTradeAdvertBotTask(30), new SeersTradeAdvertBotTask(10), new EdgevilleTradeAdvertBotTask(5)};
    private static BotTaskDefinition[] dropPartyTaskDefinitions = new BotTaskDefinition[]{new VarrockDropPartyBotTask(1), new FaladorDropPartyBotTask(1)};
    public static ArrayList brassKeyTasks = new BrassKeyBotTaskList();
    public static ArrayList shopTasks = new ShopBotTaskList();
    public static ArrayList fishingTasks = new FishingBotTaskList();
    public static ArrayList cookingTasks = new CookingBotTaskList();
    public static ArrayList miningTasks = new MiningBotTaskList();
    public static ArrayList smeltingTasks = new SmeltingBotTaskList();
    public static ArrayList smithingTasks = new SmithingBotTaskList();
    public static ArrayList woodcuttingTasks = new WoodcuttingBotTaskList();
    public static ArrayList runecraftingTasks = new RunecraftingBotTaskList();
    public static ArrayList moneyMakingTasks = new MoneyMakingBotTaskList();
    public static ArrayList sheepShearingTasks = new SheepShearingBotTaskList();
    public static ArrayList spinningTasks = new SpinningBotTaskList();
    public static ArrayList tanningTasks = new TanningBotTaskList();
    public static ArrayList leatherCraftingTasks = new LeatherCraftingBotTaskList();
    public static ArrayList combatTasks = new CombatBotTaskList();
    private static ArrayList lootSellShopTasks = new ArrayList();
    public static ArrayList tradeAdvertTaskPool = new ArrayList();
    public static ArrayList dropPartyTaskPool = new ArrayList();
    private static int totalTradeAdvertTaskWeight;
    public static ArrayList progressiveTaskPool;
    private static int totalProgressiveTaskWeight;
    public int minimumServerRevision = -1;
    public ArrayList lootSellShopIds = new ArrayList();
    public boolean usesCustomTaskAction = false;
    public boolean usesEscapeMonitor = false;
    private RectangularArea[] taskAreas;
    public Position startPosition;
    private BotRoute pretaskRoute;
    public BotRoute taskRoute;
    public BotRoute[] taskRouteSegments;
    public int[] ignoredLootItemIds;
    public int interactionTargetType;
    public boolean membersOnly;
    public int interactionOption = -1;
    public boolean combatTask = false;
    public boolean usesDepositBox = false;
    public int selectionWeight;
    public int targetSearchRadius = -1;
    public ArrayList assignedBotPlayers = new ArrayList();
    public int targetMaxX = -1;
    public int targetMaxY = -1;
    public int targetMinX = -1;
    public int targetMinY = -1;
    int forcedCombatStyle = -1;
    public boolean usesCombatTradeAdvertItems = false;
    public static int dropPartyBotJoinIndex;

    static {
        progressiveTaskPool = new ArrayList();
        dropPartyBotJoinIndex = 0;
    }

    public static ArrayList getLootSellShopTasks() {
        if (lootSellShopTasks.size() == 0) {
            for (Object taskObject : shopTasks) {
                BotTaskDefinition botTaskDefinition = (BotTaskDefinition)taskObject;
                int n = botTaskDefinition.getShopId();
                ShopDefinition shopDefinition = (ShopDefinition)ShopManager.getShopDefinitions().get(n);
                if (!shopDefinition.isGeneralStore()) continue;
                lootSellShopTasks.add(botTaskDefinition);
            }
        }
        return lootSellShopTasks;
    }

    public static BotTaskDefinition getTaskByTypeAndIndex(int n, int n2) {
        if (n == 0) {
            return (BotTaskDefinition)brassKeyTasks.get(n2);
        }
        if (n == 1) {
            return (BotTaskDefinition)shopTasks.get(n2);
        }
        if (n == 2) {
            return (BotTaskDefinition)fishingTasks.get(n2);
        }
        if (n == 3) {
            return (BotTaskDefinition)cookingTasks.get(n2);
        }
        if (n == 4) {
            return (BotTaskDefinition)miningTasks.get(n2);
        }
        if (n == 5) {
            return (BotTaskDefinition)smeltingTasks.get(n2);
        }
        if (n == 6) {
            return (BotTaskDefinition)smithingTasks.get(n2);
        }
        if (n == 7) {
            return (BotTaskDefinition)woodcuttingTasks.get(n2);
        }
        if (n == 8) {
            return (BotTaskDefinition)runecraftingTasks.get(n2);
        }
        if (n == 9) {
            return (BotTaskDefinition)moneyMakingTasks.get(n2);
        }
        if (n == 10) {
            return (BotTaskDefinition)combatTasks.get(n2);
        }
        if (n == 11) {
            return (BotTaskDefinition)sheepShearingTasks.get(n2);
        }
        if (n == 12) {
            return (BotTaskDefinition)spinningTasks.get(n2);
        }
        if (n == 13) {
            return (BotTaskDefinition)tanningTasks.get(n2);
        }
        if (n == 14) {
            return (BotTaskDefinition)leatherCraftingTasks.get(n2);
        }
        System.out.println("Error botTask (" + n + "-" + n2 + ") not found while loading!");
        return null;
    }

    public final int getTaskTypeId() {
        if (brassKeyTasks.contains(this)) {
            return 0;
        }
        if (shopTasks.contains(this)) {
            return 1;
        }
        if (fishingTasks.contains(this)) {
            return 2;
        }
        if (cookingTasks.contains(this)) {
            return 3;
        }
        if (miningTasks.contains(this)) {
            return 4;
        }
        if (smeltingTasks.contains(this)) {
            return 5;
        }
        if (smithingTasks.contains(this)) {
            return 6;
        }
        if (woodcuttingTasks.contains(this)) {
            return 7;
        }
        if (runecraftingTasks.contains(this)) {
            return 8;
        }
        if (moneyMakingTasks.contains(this)) {
            return 9;
        }
        if (combatTasks.contains(this)) {
            return 10;
        }
        if (sheepShearingTasks.contains(this)) {
            return 11;
        }
        if (spinningTasks.contains(this)) {
            return 12;
        }
        if (tanningTasks.contains(this)) {
            return 13;
        }
        if (leatherCraftingTasks.contains(this)) {
            return 14;
        }
        System.out.println("Error botTask (" + this + ") type not found while saving!");
        return -1;
    }

    public final int getTaskIndexForType(int n) {
        if (n == 0) {
            return brassKeyTasks.indexOf(this);
        }
        if (n == 1) {
            return shopTasks.indexOf(this);
        }
        if (n == 2) {
            return fishingTasks.indexOf(this);
        }
        if (n == 3) {
            return cookingTasks.indexOf(this);
        }
        if (n == 4) {
            return miningTasks.indexOf(this);
        }
        if (n == 5) {
            return smeltingTasks.indexOf(this);
        }
        if (n == 6) {
            return smithingTasks.indexOf(this);
        }
        if (n == 7) {
            return woodcuttingTasks.indexOf(this);
        }
        if (n == 8) {
            return runecraftingTasks.indexOf(this);
        }
        if (n == 9) {
            return moneyMakingTasks.indexOf(this);
        }
        if (n == 10) {
            return combatTasks.indexOf(this);
        }
        if (n == 11) {
            return sheepShearingTasks.indexOf(this);
        }
        if (n == 12) {
            return spinningTasks.indexOf(this);
        }
        if (n == 13) {
            return tanningTasks.indexOf(this);
        }
        if (n == 14) {
            return leatherCraftingTasks.indexOf(this);
        }
        System.out.println("Error botTask (" + this + ") index not found while saving!");
        return -1;
    }

    public static void initializeTradeAdvertTaskPool() {
        BotTradeAdvertManager.initializeTradeAdvertOfferPools();
        BotTaskDefinition[] taskDefinitions = tradeAdvertTaskDefinitions;
        int n = tradeAdvertTaskDefinitions.length;
        int n2 = 0;
        while (n2 < n) {
            BotTaskDefinition botTaskDefinition = taskDefinitions[n2];
            if (!ServerSettings.freeToPlayWorld || !botTaskDefinition.membersOnly) {
                tradeAdvertTaskPool.add(botTaskDefinition);
                totalTradeAdvertTaskWeight += botTaskDefinition.selectionWeight;
            }
            ++n2;
        }
        int n3 = ServerSettings.tradeBotCount;
        if (n3 < 0) {
            n3 = 0;
        } else if (n3 > 100) {
            n3 = 100;
        }
        if (n3 == 100) {
            for (Object taskObject : tradeAdvertTaskPool) {
                ++((BotTaskDefinition)taskObject).selectionWeight;
            }
        }
    }

    public static void initializeDropPartyTaskPool() {
        BotTaskDefinition[] botTaskDefinitionArray = dropPartyTaskDefinitions;
        int n = dropPartyTaskDefinitions.length;
        int n2 = 0;
        while (n2 < n) {
            BotTaskDefinition botTaskDefinition = botTaskDefinitionArray[n2];
            if (!ServerSettings.freeToPlayWorld || !botTaskDefinition.membersOnly) {
                dropPartyTaskPool.add(botTaskDefinition);
            }
            ++n2;
        }
        DropPartyBotManager.initializeDropPartyRewardPools();
    }

    public static void initializeProgressiveTaskPool() {
        BotTaskDefinition[] taskDefinitions = progressiveTaskDefinitions;
        int n = progressiveTaskDefinitions.length;
        int n2 = 0;
        while (n2 < n) {
            BotTaskDefinition botTaskDefinition = taskDefinitions[n2];
            if (!(ServerSettings.freeToPlayWorld && botTaskDefinition.membersOnly || botTaskDefinition.minimumServerRevision != -1 && ServerSettings.cacheVersion < botTaskDefinition.minimumServerRevision)) {
                progressiveTaskPool.add(botTaskDefinition);
                totalProgressiveTaskWeight += botTaskDefinition.selectionWeight;
            }
            ++n2;
        }
        int n3 = ServerSettings.skillingBotCount;
        if (n3 < 0) {
            n3 = 0;
        } else if (n3 > 100) {
            n3 = 100;
        }
        if (n3 == 100) {
            for (Object taskObject : progressiveTaskPool) {
                ++((BotTaskDefinition)taskObject).selectionWeight;
            }
        }
    }

    public final void setForcedCombatStyle(int n) {
        this.forcedCombatStyle = n;
    }

    public final int getForcedCombatStyle() {
        return this.forcedCombatStyle;
    }

    public int getShopId() {
        return -1;
    }

    public final void setPretaskRoute(BotRoute botRoute) {
        this.pretaskRoute = botRoute;
    }

    public void startCustomTaskAction(Player player) {
    }

    public void startEscapeMonitor(Player player) {
    }

    public boolean meetsUnlockRequirements(Player player) {
        return true;
    }

    public boolean isWithinProgressionRange(Player player) {
        return true;
    }

    public ArrayList getRequiredItems(Player player) {
        return new ArrayList();
    }

    public final ArrayList getMissingRequiredItems(Player player) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        ArrayList requiredItems = this.getRequiredItems(player);
        player.botTaskRequiredItems = new ItemStack[requiredItems.size()];
        int n = 0;
        Iterator iterator = requiredItems.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = (ItemStack)iterator.next();
            player.botTaskRequiredItems[n] = itemStack;
            if (!player.ownsItemAmount(itemStack.getId(), itemStack.getAmount())) {
                arrayList.add(itemStack);
            }
            ++n;
        }
        return arrayList;
    }

    public final boolean isAvailableFor(Player player, boolean bl) {
        if (this.membersOnly && ServerSettings.freeToPlayWorld) {
            return false;
        }
        if (this.membersOnly && !player.isMember()) {
            return false;
        }
        if (this.minimumServerRevision != -1 && ServerSettings.cacheVersion < this.minimumServerRevision) {
            return false;
        }
        if (!this.meetsUnlockRequirements(player)) {
            return false;
        }
        if (!this.isWithinProgressionRange(player)) {
            return false;
        }
        if (bl) {
            return true;
        }
        ArrayList arrayList = this.getMissingRequiredItems(player);
        if (arrayList.size() > 0) {
            if (((ItemStack)arrayList.get(0)).getId() == 983) {
                return true;
            }
            return BotTaskPlanner.selectShopPurchaseTask(player, ((ItemStack)arrayList.get(0)).getId(), ((ItemStack)arrayList.get(0)).getAmount()) != null;
        }
        return true;
    }

    public BotTaskDefinition(int n, boolean bl, int n2) {
        this.interactionTargetType = 0;
        this.membersOnly = false;
        this.selectionWeight = n2;
    }

    public BotTaskDefinition(Position position, BotRoute botRoute, int n, boolean bl, int n2) {
        this.startPosition = position;
        this.taskRoute = botRoute;
        this.interactionTargetType = n;
        this.membersOnly = bl;
        this.selectionWeight = n2;
    }

    public BotTaskDefinition(Position position, BotRoute[] botRouteArray, int n, boolean bl, int n2) {
        this.startPosition = position;
        this.taskRouteSegments = botRouteArray;
        this.interactionTargetType = n;
        this.membersOnly = bl;
        this.selectionWeight = n2;
    }

    public final Position getStartPosition() {
        return this.startPosition;
    }

    public final void setTaskAreas(RectangularArea[] rectangularAreaArray) {
        this.taskAreas = rectangularAreaArray;
    }

    public int getInteractionOption(Player player) {
        if (this.interactionOption != -1) {
            return this.interactionOption;
        }
        return 1;
    }

    public final void addLootSellShopIds(int[] nArray) {
        int[] nArray2 = nArray;
        int n = nArray.length;
        int n2 = 0;
        while (n2 < n) {
            int n3 = nArray2[n2];
            this.lootSellShopIds.add(n3);
            ++n2;
        }
    }

    public final void startTask(Player player) {
        GameplayHelper.resetBotTaskState(player);
        if (!(shopTasks.contains(this) || spinningTasks.contains(this) || leatherCraftingTasks.contains(this) || tanningTasks.contains(this) || cookingTasks.contains(this) || smeltingTasks.contains(this) || smithingTasks.contains(this))) {
            player.botTaskItemId = -1;
        }
        player.botSmithingProductItemId = -1;
        player.botUseTaskItemOnTarget = false;
        if (player.botMode != 4) {
            this.prepareTaskCombatLoadout(player);
            this.prepareTradeAdvertState(player);
            this.prepareDropPartyState(player);
            this.prepareTaskInventory(player);
        }
        this.configureTaskInteractionTargets(player);
        player.botEnabled = true;
        player.botInteractionOption = 1;
        if (player.tradeAdvertMode == -1) {
            boolean bl = true;
            if (player.botMode == 0 && ServerSettings.walkingBotsEnabled || player.botMode == 4) {
                bl = false;
            }
            if (bl && this.startPosition != null) {
                player.moveTo(this.startPosition);
            }
            if (this.pretaskRoute != null) {
                this.startPretaskPath(player);
            } else if (bl) {
                this.startWalkToTask(player);
            }
            if (!bl && this.startPosition != null) {
                BotWorldRouteWalker.findWorldRoute(player);
                return;
            }
        } else {
            player.moveTo(this.getRandomTaskAreaPosition());
            player.botTaskState = "do task";
        }
    }

    public final Position getRandomTaskAreaPosition() {
        int n = GameUtil.randomInt(this.taskAreas.length);
        int n2 = this.taskAreas[n].getMinX();
        int n3 = this.taskAreas[n].getMinY();
        int n4 = this.taskAreas[n].getMaxX();
        n = this.taskAreas[n].getMaxY();
        n4 -= n2;
        n -= n3;
        n = n3 + GameUtil.randomInt(n);
        return new Position(n2 += GameUtil.randomInt(n4), n);
    }

    public final void startNpcCombatTick(Player player, Npc npc) {
        boolean bl = player.botCombatTickTask != null && player.botCombatTickTask.isActive();
        if (!bl) {
            player.botCombatTickTask = new BotCombatTickTask(this, 1, npc, player);
            World.getTaskScheduler().schedule(player.botCombatTickTask);
        }
    }

    public static void completeTradeAdvertOffer(Player player, boolean bl) {
        CacheArchiveEntry.completeTradeAdvertOffer(player, bl);
    }

    public void prepareTradeAdvertState(Player player) {
        player.tradeAdvertMode = -1;
        player.botAdvertItemId = -1;
        player.tradeAdvertQuantityRemaining = -1;
        player.tradeAdvertUnitPrice = -1;
        player.botPublicChatMessage = "";
        player.botPublicChatColor = -1;
        player.botPublicChatEffect = -1;
        player.pendingTradeTarget = null;
        player.tradeAdvertVariableQuantity = false;
        player.tradeAdvertLastOfferAmount = -1;
        player.tradeAdvertOfferPoolIndex = -1;
        player.tradeAdvertQuantityOptionIndex = -1;
    }

    public void prepareDropPartyState(Player player) {
        player.dropPartyLeader = false;
        player.dropPartyPretaskComplete = false;
        player.dropPartyPretaskLoopCount = 0;
    }

    public void configureTaskInteractionTargets(Player player) {
    }

    public void prepareTaskInventory(Player player) {
    }

    public void prepareTaskCombatLoadout(Player player) {
    }

    public void startWalkToTask(Player player) {
        player.botTaskState = "walk to task";
        player.currentBotRoute = this.taskRoute;
        player.botPathWaypointIndex = 0;
        player.continueBotRoute();
    }

    public void continueWalkToTask(Player player, int n) {
        player.botTaskState = "walk to task";
        player.currentBotRoute = this.taskRoute;
        player.botPathWaypointIndex = n;
        player.continueBotRoute();
    }

    public final void startPretaskPath(Player player) {
        player.botTaskState = "walk pretask path1";
        player.currentBotRoute = this.pretaskRoute;
        player.botPathWaypointIndex = 0;
        player.continueBotRoute();
    }

    public final void returnPretaskPath(Player player) {
        player.botTaskState = "walk pretask path2";
        player.currentBotRoute = this.pretaskRoute.reversed();
        player.botPathWaypointIndex = 0;
        player.continueBotRoute();
    }

    public void startWalkToBank(Player player) {
        player.botTaskState = "walk to bank";
        player.currentBotRoute = this.taskRoute.reversed();
        player.botPathWaypointIndex = 0;
        player.continueBotRoute();
    }

    public void continueWalkToBank(Player player, int n) {
        player.botTaskState = "walk to bank";
        player.currentBotRoute = this.taskRoute.reversed();
        player.botPathWaypointIndex = n;
        player.continueBotRoute();
    }

    public final Position getTaskPosition() {
        if (this.taskRouteSegments != null) {
            return this.taskRouteSegments[this.taskRouteSegments.length - 1].reversed().getStartPosition();
        }
        return this.taskRoute.reversed().getStartPosition();
    }

    public void advanceTaskRouteSegment(Player player, boolean bl) {
    }

    static /* synthetic */ DraynorNetFishingBotTask getDraynorNetFishingTask() {
        return draynorNetFishingTask;
    }

    static /* synthetic */ BarbarianVillageFlyFishingBotTask getBarbarianVillageFlyFishingTask() {
        return barbarianVillageFlyFishingTask;
    }

    static /* synthetic */ KaramjaFishingBotTask getKaramjaFishingTask() {
        return karamjaFishingTask;
    }

    static /* synthetic */ AlKharidFlyFishingBotTask getAlKharidFlyFishingTask() {
        return alKharidFlyFishingTask;
    }

    static /* synthetic */ AlKharidNetBaitFishingBotTask getAlKharidNetBaitFishingTask() {
        return alKharidNetBaitFishingTask;
    }

    static /* synthetic */ CatherbyFishingBotTask getCatherbyFishingTask() {
        return catherbyFishingTask;
    }

    static /* synthetic */ VarrockLobsterCookingBotTask getVarrockLobsterCookingTask() {
        return varrockLobsterCookingTask;
    }

    static /* synthetic */ AlKharidLobsterCookingBotTask getAlKharidLobsterCookingTask() {
        return alKharidLobsterCookingTask;
    }

    static /* synthetic */ CatherbyLobsterCookingBotTask getCatherbyLobsterCookingTask() {
        return catherbyLobsterCookingTask;
    }

    static /* synthetic */ AlKharidMineBotTask getAlKharidMineTask() {
        return alKharidMineTask;
    }

    static /* synthetic */ CraftingGuildMineBotTask getCraftingGuildMineTask() {
        return craftingGuildMineTask;
    }

    static /* synthetic */ MiningGuildMineBotTask getMiningGuildMineTask() {
        return miningGuildMineTask;
    }

    static /* synthetic */ VarrockEastMineBotTask getVarrockEastMineTask() {
        return varrockEastMineTask;
    }

    static /* synthetic */ WildernessRuniteMineBotTask getWildernessRuniteMineTask() {
        return wildernessRuniteMineTask;
    }

    static /* synthetic */ AlKharidSteelSmeltingBotTask getAlKharidSteelSmeltingTask() {
        return alKharidSteelSmeltingTask;
    }

    static /* synthetic */ FaladorSteelSmeltingBotTask getFaladorSteelSmeltingTask() {
        return faladorSteelSmeltingTask;
    }

    static /* synthetic */ EdgevilleYewWoodcuttingBotTask getEdgevilleYewWoodcuttingTask() {
        return edgevilleYewWoodcuttingTask;
    }

    static /* synthetic */ FaladorYewWoodcuttingBotTask getFaladorYewWoodcuttingTask() {
        return faladorYewWoodcuttingTask;
    }

    static /* synthetic */ VarrockPalaceYewWoodcuttingBotTask getVarrockPalaceYewWoodcuttingTask() {
        return varrockPalaceYewWoodcuttingTask;
    }

    static /* synthetic */ DraynorOakWoodcuttingBotTask getDraynorOakWoodcuttingTask() {
        return draynorOakWoodcuttingTask;
    }

    static /* synthetic */ DraynorTreeWoodcuttingBotTask getDraynorTreeWoodcuttingTask() {
        return draynorTreeWoodcuttingTask;
    }

    static /* synthetic */ VarrockWestOakWoodcuttingBotTask getVarrockWestOakWoodcuttingTask() {
        return varrockWestOakWoodcuttingTask;
    }

    static /* synthetic */ VarrockWestTreeWoodcuttingBotTask getVarrockWestTreeWoodcuttingTask() {
        return varrockWestTreeWoodcuttingTask;
    }

    static /* synthetic */ VarrockEastOakWoodcuttingBotTask getVarrockEastOakWoodcuttingTask() {
        return varrockEastOakWoodcuttingTask;
    }

    static /* synthetic */ VarrockEastTreeWoodcuttingBotTask getVarrockEastTreeWoodcuttingTask() {
        return varrockEastTreeWoodcuttingTask;
    }

    static /* synthetic */ EdgevilleTreeWoodcuttingBotTask getEdgevilleTreeWoodcuttingTask() {
        return edgevilleTreeWoodcuttingTask;
    }

    static /* synthetic */ DraynorYewWoodcuttingBotTask getDraynorYewWoodcuttingTask() {
        return draynorYewWoodcuttingTask;
    }

    static /* synthetic */ SeersMapleWoodcuttingBotTask getSeersMapleWoodcuttingTask() {
        return seersMapleWoodcuttingTask;
    }

    static /* synthetic */ SeersYewWoodcuttingBotTask getSeersYewWoodcuttingTask() {
        return seersYewWoodcuttingTask;
    }

    static /* synthetic */ SeersMagicTreeWoodcuttingBotTask getSeersMagicTreeWoodcuttingTask() {
        return seersMagicTreeWoodcuttingTask;
    }

    static /* synthetic */ SorcerersTowerMagicTreeWoodcuttingBotTask getSorcerersTowerMagicTreeWoodcuttingTask() {
        return sorcerersTowerMagicTreeWoodcuttingTask;
    }

    static /* synthetic */ VarrockRuneEssenceMiningBotTask getVarrockRuneEssenceMiningTask() {
        return varrockRuneEssenceMiningTask;
    }

    static /* synthetic */ AirRuneRunecraftingBotTask getAirRuneRunecraftingTask() {
        return airRuneRunecraftingTask;
    }

    static /* synthetic */ MindRuneRunecraftingBotTask getMindRuneRunecraftingTask() {
        return mindRuneRunecraftingTask;
    }

    static /* synthetic */ WaterRuneRunecraftingBotTask getWaterRuneRunecraftingTask() {
        return waterRuneRunecraftingTask;
    }

    static /* synthetic */ EarthRuneRunecraftingBotTask getEarthRuneRunecraftingTask() {
        return earthRuneRunecraftingTask;
    }

    static /* synthetic */ FireRuneRunecraftingBotTask getFireRuneRunecraftingTask() {
        return fireRuneRunecraftingTask;
    }

    static /* synthetic */ BodyRuneRunecraftingBotTask getBodyRuneRunecraftingTask() {
        return bodyRuneRunecraftingTask;
    }

    static /* synthetic */ FaladorWineOfZamorakTelegrabBotTask getFaladorWineOfZamorakTelegrabTask() {
        return faladorWineOfZamorakTelegrabTask;
    }

    static /* synthetic */ DraynorSheepShearingBotTask getDraynorSheepShearingTask() {
        return draynorSheepShearingTask;
    }

    static /* synthetic */ LumbridgeWoolSpinningBotTask getLumbridgeWoolSpinningTask() {
        return lumbridgeWoolSpinningTask;
    }

    static /* synthetic */ SeersFlaxSpinningBotTask getSeersFlaxSpinningTask() {
        return seersFlaxSpinningTask;
    }

    static /* synthetic */ DraynorChickenCombatBotTask getDraynorChickenCombatTask() {
        return draynorChickenCombatTask;
    }

    static /* synthetic */ FaladorCowCombatBotTask getFaladorCowCombatTask() {
        return faladorCowCombatTask;
    }

    static /* synthetic */ KaramjaVolcanoNorthLesserDemonCombatBotTask getKaramjaVolcanoNorthLesserDemonCombatTask() {
        return karamjaVolcanoNorthLesserDemonCombatTask;
    }

    static /* synthetic */ KaramjaVolcanoSouthLesserDemonCombatBotTask getKaramjaVolcanoSouthLesserDemonCombatTask() {
        return karamjaVolcanoSouthLesserDemonCombatTask;
    }

    static /* synthetic */ LumbridgeEastChickenCombatBotTask getLumbridgeEastChickenCombatTask() {
        return lumbridgeEastChickenCombatTask;
    }

    static /* synthetic */ LumbridgeWestChickenCombatBotTask getLumbridgeWestChickenCombatTask() {
        return lumbridgeWestChickenCombatTask;
    }

    static /* synthetic */ LumbridgeCowCombatBotTask getLumbridgeCowCombatTask() {
        return lumbridgeCowCombatTask;
    }

    static /* synthetic */ EdgevilleDungeonHillGiantCombatBotTask getEdgevilleDungeonHillGiantCombatTask() {
        return edgevilleDungeonHillGiantCombatTask;
    }

    static /* synthetic */ EdgevilleDungeonNorthMossGiantCombatBotTask getEdgevilleDungeonNorthMossGiantCombatTask() {
        return edgevilleDungeonNorthMossGiantCombatTask;
    }

    static /* synthetic */ EdgevilleDungeonSouthMossGiantCombatBotTask getEdgevilleDungeonSouthMossGiantCombatTask() {
        return edgevilleDungeonSouthMossGiantCombatTask;
    }

    static /* synthetic */ DraynorGoblinCombatBotTask getDraynorGoblinCombatTask() {
        return draynorGoblinCombatTask;
    }

    static /* synthetic */ AlKharidWarriorCombatBotTask getAlKharidWarriorCombatTask() {
        return alKharidWarriorCombatTask;
    }

    static /* synthetic */ VarrockGuardCombatBotTask getVarrockGuardCombatTask() {
        return varrockGuardCombatTask;
    }

    static /* synthetic */ VarrockSewerGiantRatCombatBotTask getVarrockSewerGiantRatCombatTask() {
        return varrockSewerGiantRatCombatTask;
    }

    static /* synthetic */ BarbarianVillageBarbarianCombatBotTask getBarbarianVillageBarbarianCombatTask() {
        return barbarianVillageBarbarianCombatTask;
    }

    static /* synthetic */ DwarvenMineDwarfCombatBotTask getDwarvenMineDwarfCombatTask() {
        return dwarvenMineDwarfCombatTask;
    }

    static /* synthetic */ FaladorGuardCombatBotTask getFaladorGuardCombatTask() {
        return faladorGuardCombatTask;
    }

    static /* synthetic */ EdgevilleDungeonSpiderRatCombatBotTask getEdgevilleDungeonSpiderRatCombatTask() {
        return edgevilleDungeonSpiderRatCombatTask;
    }

    static /* synthetic */ EdgevilleDungeonSkeletonCombatBotTask getEdgevilleDungeonSkeletonCombatTask() {
        return edgevilleDungeonSkeletonCombatTask;
    }

    static /* synthetic */ VarrockSouthChickenCombatBotTask getVarrockSouthChickenCombatTask() {
        return varrockSouthChickenCombatTask;
    }

    static /* synthetic */ BrimhavenDungeonBlueDragonSouthCombatBotTask getBrimhavenDungeonBlueDragonSouthCombatTask() {
        return brimhavenDungeonBlueDragonSouthCombatTask;
    }

    static /* synthetic */ BrimhavenDungeonRedDragonCombatBotTask getBrimhavenDungeonRedDragonCombatTask() {
        return brimhavenDungeonRedDragonCombatTask;
    }

    static /* synthetic */ TaverleyDungeonHellhoundCombatBotTask getTaverleyDungeonHellhoundCombatTask() {
        return taverleyDungeonHellhoundCombatTask;
    }
}

