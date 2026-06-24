#!/usr/bin/env python3
"""Apply first-pass class/package/method names to Server.jar.

This stays conservative: class and package names are remapped through jar entry
names and CONSTANT_Utf8 strings, while selected method names are remapped only
when the owner class, old name, and descriptor all match an evidence-backed rule.
"""

from __future__ import annotations

import argparse
import re
import struct
import zipfile
from pathlib import Path


EXACT_CLASS_MAP = {
    "com/rs2/a": "com/rs2/ConfigFile",
    "com/rs2/b": "com/rs2/ServerSettings",
    "com/rs2/c": "com/rs2/CacheCoordinateTranslator",
    "com/rs2/d": "com/rs2/CacheRevisionInfo",
    "com/rs2/d/a": "com/rs2/model/path/PathResult",
    "com/rs2/d/b": "com/rs2/model/path/PathStrategy",
    "com/rs2/d/c": "com/rs2/model/path/PathReachability",
    "com/rs2/d/d": "com/rs2/model/path/PathStep",
    "com/rs2/d/e": "com/rs2/model/path/DirectPathStrategy",
    "com/rs2/e": "com/rs2/ConnectionThrottleSettings",
    "com/rs2/f": "com/rs2/HiscoresDatabase",
    "com/rs2/g": "com/rs2/ConnectionThrottle",
    "com/rs2/h": "com/rs2/LanDiscoveryService",
    "com/rs2/i": "com/rs2/LanDiscoveryListener",
    "com/rs2/j": "com/rs2/StartingRareItemList",
    "com/rs2/k": "com/rs2/BotLoginBatchTask",
    "com/rs2/l": "com/rs2/MinuteMaintenanceTickTask",
    "com/rs2/launcher/Cpanel": "com/rs2/launcher/ControlPanel",
    "com/rs2/launcher/a": "com/rs2/launcher/ConsolePrintStream",
    "com/rs2/launcher/b": "com/rs2/launcher/ControlPanelWindowCloseListener",
    "com/rs2/launcher/c": "com/rs2/launcher/MapHorizontalScrollListener",
    "com/rs2/launcher/d": "com/rs2/launcher/MapVerticalScrollListener",
    "com/rs2/launcher/f": "com/rs2/launcher/TransparentColorFilter",
    "com/rs2/launcher/g": "com/rs2/launcher/WorldMapPanel",
    "com/rs2/b/a": "com/rs2/cache/CacheStore",
    "com/rs2/b/b": "com/rs2/cache/CacheStoreException",
    "com/rs2/launcher/e": "com/rs2/cache/CacheFile",
    "com/rs2/a/a/k": "com/rs2/cache/CacheArchive",
    "com/rs2/model/a/i/b": "com/rs2/cache/CacheDefinitionIndex",
    "com/rs2/b/a/a": "com/rs2/cache/CacheIndexEntry",
    "com/rs2/b/a/a/a": "com/rs2/cache/MapIndexEntry",
    "com/rs2/b/a/a/b": "com/rs2/cache/DefinitionIndexEntry",
    "com/rs2/b/b/a": "com/rs2/cache/InterfaceDefinition",
    "com/rs2/util/I": "com/rs2/util/ByteArrayReader",
    "com/rs2/util/m": "com/rs2/util/LinkedNode",
    "com/rs2/util/n": "com/rs2/util/LinkedNodeList",
    "com/rs2/util/o": "com/rs2/util/CacheableNode",
    "com/rs2/util/F": "com/rs2/util/LoginIpReservation",
    "com/rs2/util/g": "com/rs2/util/FileUtil",
    "com/rs2/util/G": "com/rs2/util/SaveAllPlayersShutdownHook",
    "com/rs2/util/H": "com/rs2/util/DelayedShutdownTask",
    "com/rs2/util/k": "com/rs2/util/TimestampedPrintStream",
    "com/rs2/util/h": "com/rs2/util/GameUtil",
    "com/rs2/util/K": "com/rs2/util/Vector2f",
    "com/rs2/util/f": "com/rs2/util/WeightedChanceEntry",
    "com/rs2/util/i": "com/rs2/util/WeightedChanceEntryThresholdComparator",
    "com/rs2/util/J": "com/rs2/util/ChatTextCodec",
    "com/rs2/util/l": "com/rs2/util/TextUtil",
    "com/rs2/util/p": "com/rs2/util/CountingDataOutputStream",
    "com/rs2/util/q": "com/rs2/util/CharacterFileManager",
    "com/rs2/util/d": "com/rs2/util/ConverterUidLookupQuery",
    "com/rs2/util/e": "com/rs2/util/ConverterUidLookupCallback",
    "com/rs2/util/b": "com/rs2/util/ProfilerTimer",
    "com/rs2/util/c": "com/rs2/util/ProfilerRegistry",
    "com/rs2/util/b/a": "com/rs2/util/plugin/Plugin",
    "com/rs2/util/b/b": "com/rs2/util/plugin/PluginType",
    "com/rs2/util/b/c": "com/rs2/util/plugin/GlobalPlugin",
    "com/rs2/util/b/d": "com/rs2/util/plugin/PlayerPlugin",
    "com/rs2/util/b/e": "com/rs2/util/plugin/PluginManager",
    "com/rs2/util/c/e": "com/rs2/model/combat/requirement/CombatRequirement",
    "com/rs2/util/c/b": "com/rs2/model/combat/requirement/CombatCostRequirement",
    "com/rs2/util/c/a": "com/rs2/model/combat/requirement/EquipmentItemRequirement",
    "com/rs2/util/c/c": "com/rs2/model/combat/requirement/InventoryItemRequirement",
    "com/rs2/util/c/d": "com/rs2/model/combat/requirement/MembersRequirement",
    "com/rs2/util/c/f": "com/rs2/model/combat/requirement/SpellRuneCostRequirement",
    "com/rs2/util/c/g": "com/rs2/model/combat/requirement/SkillLevelRequirement",
    "com/rs2/util/a": "com/rs2/util/RectangularArea",
    "com/rs2/util/a/a": "com/rs2/util/path/MapDataReader",
    "com/rs2/util/a/b": "com/rs2/util/path/PathFinder",
    "com/rs2/util/a/c": "com/rs2/util/path/ProjectileCollisionMap",
    "com/rs2/util/a/d": "com/rs2/util/path/WalkingCollisionMap",
    "com/rs2/util/j": "com/rs2/util/ElapsedTimer",
    "com/rs2/util/E": "com/rs2/util/BackupRestoreTimestampComparator",
    "com/rs2/util/s": "com/rs2/util/BackupRepairTimestampComparator",
    "com/rs2/model/a/e": "com/rs2/model/reward/ActionRewardDefinition",
    "com/rs2/model/d/f": "com/rs2/model/NoopStartupHook",
    "com/rs2/model/a/j/g": "com/rs2/model/skill/EquipmentKeywordBootstrap",
    "com/rs2/util/z": "com/rs2/util/PlayerLoginLoadCallback",
    "com/rs2/util/A": "com/rs2/util/PlayerProfileLoadQuery",
    "com/rs2/util/B": "com/rs2/util/PlayerContactsLoadQuery",
    "com/rs2/util/C": "com/rs2/util/PlayerSkillsLoadQuery",
    "com/rs2/util/D": "com/rs2/util/PlayerWorldUpdateQuery",
    "com/rs2/util/y": "com/rs2/util/PlayerUidLookupQuery",
    "com/rs2/util/d/a": "com/rs2/util/db/DatabaseCallback",
    "com/rs2/util/d/b": "com/rs2/util/db/DatabaseService",
    "com/rs2/util/d/c": "com/rs2/util/db/DatabaseQuery",
    "com/rs2/util/d/d": "com/rs2/util/db/DatabaseThreadContext",
    "com/rs2/util/d/a/a": "com/rs2/util/db/player/PlayerLoadQueryFactory",
    "com/rs2/util/d/a/b": "com/rs2/util/db/player/PlayerContainerLoadQuery",
    "com/rs2/util/d/a/c": "com/rs2/util/db/player/PlayerContactsLoadCallback",
    "com/rs2/util/d/a/d": "com/rs2/util/db/player/PlayerContainerLoadCallback",
    "com/rs2/util/d/a/e": "com/rs2/util/db/player/PlayerProfileLoadCallback",
    "com/rs2/util/d/a/f": "com/rs2/util/db/player/PlayerSkillsLoadCallback",
    "com/rs2/util/d/a/g": "com/rs2/util/db/player/PlayerSaveQueryFactory",
    "com/rs2/util/d/a/h": "com/rs2/util/db/player/PlayerContactsSaveQuery",
    "com/rs2/util/d/a/i": "com/rs2/util/db/player/PlayerContainerSaveQuery",
    "com/rs2/util/d/a/j": "com/rs2/util/db/player/PlayerProfileSaveQuery",
    "com/rs2/util/d/a/k": "com/rs2/util/db/player/PlayerSkillsSaveQuery",
    "com/rs2/util/r": "com/rs2/util/db/player/PlayerProfileSaveCallback",
    "com/rs2/util/t": "com/rs2/util/db/player/PlayerInventorySaveCallback",
    "com/rs2/util/u": "com/rs2/util/db/player/PlayerBankSaveCallback",
    "com/rs2/util/v": "com/rs2/util/db/player/PlayerEquipmentSaveCallback",
    "com/rs2/util/w": "com/rs2/util/db/player/PlayerContactsSaveCallback",
    "com/rs2/util/x": "com/rs2/util/db/player/PlayerSkillsSaveCallback",
    "com/rs2/a/a": "com/rs2/bot/BotPlayer",
    "com/rs2/a/i": "com/rs2/bot/BotTaskDefinition",
    "com/rs2/a/s": "com/rs2/bot/FishingBotTaskList",
    "com/rs2/a/t": "com/rs2/bot/CookingBotTaskList",
    "com/rs2/a/w": "com/rs2/bot/SmithingBotTaskList",
    "com/rs2/a/v": "com/rs2/bot/SmeltingBotTaskList",
    "com/rs2/a/u": "com/rs2/bot/MiningBotTaskList",
    "com/rs2/a/l": "com/rs2/bot/SheepShearingBotTaskList",
    "com/rs2/a/m": "com/rs2/bot/SpinningBotTaskList",
    "com/rs2/a/n": "com/rs2/bot/TanningBotTaskList",
    "com/rs2/a/o": "com/rs2/bot/LeatherCraftingBotTaskList",
    "com/rs2/a/y": "com/rs2/bot/RunecraftingBotTaskList",
    "com/rs2/a/x": "com/rs2/bot/WoodcuttingBotTaskList",
    "com/rs2/a/j": "com/rs2/bot/BrassKeyBotTaskList",
    "com/rs2/a/k": "com/rs2/bot/MoneyMakingBotTaskList",
    "com/rs2/a/p": "com/rs2/bot/CombatBotTaskList",
    "com/rs2/a/r": "com/rs2/bot/ShopBotTaskList",
    "com/rs2/a/q": "com/rs2/bot/BotCombatTickTask",
    "com/rs2/a/b": "com/rs2/bot/BotTradeAdvertStartTask",
    "com/rs2/a/c": "com/rs2/bot/BotTaskSelectionTask",
    "com/rs2/a/d": "com/rs2/bot/BotTaskPlanningTask",
    "com/rs2/a/e": "com/rs2/bot/BotCombatLoadoutTask",
    "com/rs2/a/f": "com/rs2/bot/DropPartyBotHideTask",
    "com/rs2/a/g": "com/rs2/bot/ClanWarsBotHideTask",
    "com/rs2/a/A": "com/rs2/bot/ClanWarsBotManagerTickTask",
    "com/rs2/a/B": "com/rs2/bot/ClanWarsBotManager",
    "com/rs2/a/C": "com/rs2/bot/ClanWarsBotCombatTickTask",
    "com/rs2/a/J": "com/rs2/bot/BotTradeAdvertManager",
    "com/rs2/a/D": "com/rs2/bot/DropPartyBotManager",
    "com/rs2/a/E": "com/rs2/bot/DropPartyLeaderTickTask",
    "com/rs2/a/F": "com/rs2/bot/DropPartyFollowerTickTask",
    "com/rs2/a/G": "com/rs2/bot/DropPartyGroundItemPickupTask",
    "com/rs2/a/H": "com/rs2/bot/DropPartyLeaderCleanupTask",
    "com/rs2/a/I": "com/rs2/bot/DropPartyCompletionTask",
    "com/rs2/a/h": "com/rs2/model/GameplayHelper",
    "com/rs2/a/z": "com/rs2/bot/DropPartyBotJoinTask",
    "com/rs2/a/a/a": "com/rs2/bot/combat/WildernessBotSettings",
    "com/rs2/a/a/b": "com/rs2/bot/combat/BotCombatLoadoutTables",
    "com/rs2/a/a/c": "com/rs2/bot/combat/BotCombatHelper",
    "com/rs2/a/a/d": "com/rs2/bot/combat/BotGroundItemPickupTask",
    "com/rs2/a/a/e": "com/rs2/bot/combat/BotCombatLoadoutManager",
    "com/rs2/a/a/f": "com/rs2/bot/combat/BotPvpCombatHandler",
    "com/rs2/a/a/g": "com/rs2/bot/combat/BotPvpSelfTargetCombatTickTask",
    "com/rs2/a/a/h": "com/rs2/bot/combat/BotPvpOpponentTargetCombatTickTask",
    "com/rs2/a/a/i": "com/rs2/bot/combat/BotCombatEscapeHandler",
    "com/rs2/a/a/j": "com/rs2/bot/combat/BotCombatEscapeLogoutTask",
    "com/rs2/a/a/l": "com/rs2/bot/combat/BotPvpTargetSearchTickTask",
    "com/rs2/a/a/m": "com/rs2/bot/combat/BotGroupCombatTickTask",
    "com/rs2/a/b/a": "com/rs2/bot/BotTaskPlanner",
    "com/rs2/a/b/b": "com/rs2/bot/route/BotWorldRouteChoice",
    "com/rs2/a/b/c": "com/rs2/bot/route/BotWorldRouteWalker",
    "com/rs2/a/b/d": "com/rs2/bot/route/BotWorldRoute",
    "com/rs2/a/e/a": "com/rs2/cache/CacheArchiveEntry",
    "com/rs2/a/e/b": "com/rs2/bot/trade/BotTradeOfferTickTask",
    "com/rs2/a/e/c": "com/rs2/bot/trade/BotTradeCoinShortageResetTask",
    "com/rs2/a/e/d": "com/rs2/bot/trade/BotTradeItemShortageResetTask",
    "com/rs2/a/c/c": "com/rs2/bot/shop/AlKharidGeneralStoreBotTask",
    "com/rs2/a/c/d": "com/rs2/bot/shop/AlKharidGeneralStoreTradeTickTask",
    "com/rs2/a/c/e": "com/rs2/bot/shop/AlKharidLegsShopBotTask",
    "com/rs2/a/c/f": "com/rs2/bot/shop/AlKharidLegsShopTradeTickTask",
    "com/rs2/a/c/g": "com/rs2/bot/shop/AlKharidScimitarShopBotTask",
    "com/rs2/a/c/h": "com/rs2/bot/shop/AlKharidScimitarShopTradeTickTask",
    "com/rs2/a/c/i": "com/rs2/bot/shop/AlKharidSkirtShopBotTask",
    "com/rs2/a/c/j": "com/rs2/bot/shop/AlKharidSkirtShopTradeTickTask",
    "com/rs2/a/c/a": "com/rs2/bot/shop/AlKharidCraftingShopBotTask",
    "com/rs2/a/c/b": "com/rs2/bot/shop/AlKharidCraftingShopTradeTickTask",
    "com/rs2/a/c/o": "com/rs2/bot/shop/BarbarianVillageHelmetShopBotTask",
    "com/rs2/a/c/p": "com/rs2/bot/shop/BarbarianVillageHelmetShopTradeTickTask",
    "com/rs2/a/c/w": "com/rs2/bot/shop/ChampionsGuildPlatebodyShopBotTask",
    "com/rs2/a/c/x": "com/rs2/bot/shop/ChampionsGuildPlatebodyShopTradeTickTask",
    "com/rs2/a/c/y": "com/rs2/bot/shop/ChampionsGuildRuneLongswordShopBotTask",
    "com/rs2/a/c/z": "com/rs2/bot/shop/ChampionsGuildRuneLongswordShopTradeTickTask",
    "com/rs2/a/c/A": "com/rs2/bot/shop/DwarvenMinePickaxeShopBotTask",
    "com/rs2/a/c/B": "com/rs2/bot/shop/DwarvenMinePickaxeShopTradeTickTask",
    "com/rs2/a/c/C": "com/rs2/bot/shop/EdgevilleGeneralStoreBotTask",
    "com/rs2/a/c/D": "com/rs2/bot/shop/EdgevilleGeneralStoreTradeTickTask",
    "com/rs2/a/c/G": "com/rs2/bot/shop/FaladorGeneralStoreBotTask",
    "com/rs2/a/c/H": "com/rs2/bot/shop/FaladorGeneralStoreTradeTickTask",
    "com/rs2/a/c/K": "com/rs2/bot/shop/FaladorShieldShopBotTask",
    "com/rs2/a/c/L": "com/rs2/bot/shop/FaladorShieldShopTradeTickTask",
    "com/rs2/a/c/M": "com/rs2/bot/shop/DraynorAxeShopBotTask",
    "com/rs2/a/c/N": "com/rs2/bot/shop/DraynorAxeShopTradeTickTask",
    "com/rs2/a/c/O": "com/rs2/bot/shop/DraynorGeneralStoreBotTask",
    "com/rs2/a/c/P": "com/rs2/bot/shop/DraynorGeneralStoreTradeTickTask",
    "com/rs2/a/c/Q": "com/rs2/bot/shop/EdgevilleOziachRunePlatebodyShopBotTask",
    "com/rs2/a/c/R": "com/rs2/bot/shop/EdgevilleOziachRunePlatebodyShopTradeTickTask",
    "com/rs2/a/c/E": "com/rs2/bot/shop/FaladorChainbodyShopBotTask",
    "com/rs2/a/c/F": "com/rs2/bot/shop/FaladorChainbodyShopTradeTickTask",
    "com/rs2/a/c/I": "com/rs2/bot/shop/FaladorMaceShopBotTask",
    "com/rs2/a/c/J": "com/rs2/bot/shop/FaladorMaceShopTradeTickTask",
    "com/rs2/a/c/S": "com/rs2/bot/shop/PortSarimBattleaxeShopBotTask",
    "com/rs2/a/c/T": "com/rs2/bot/shop/PortSarimBattleaxeShopTradeTickTask",
    "com/rs2/a/c/U": "com/rs2/bot/shop/PortSarimFishingShopBotTask",
    "com/rs2/a/c/V": "com/rs2/bot/shop/PortSarimFishingShopTradeTickTask",
    "com/rs2/a/c/W": "com/rs2/bot/shop/PortSarimMagicShopChaosRuneBotTask",
    "com/rs2/a/c/X": "com/rs2/bot/shop/PortSarimMagicShopChaosRuneTradeTickTask",
    "com/rs2/a/c/Y": "com/rs2/bot/shop/VarrockArcheryShopSteelArrowBotTask",
    "com/rs2/a/c/Z": "com/rs2/bot/shop/VarrockArcheryShopSteelArrowTradeTickTask",
    "com/rs2/a/c/aa": "com/rs2/bot/shop/VarrockArmourShopChainbodyBotTask",
    "com/rs2/a/c/ab": "com/rs2/bot/shop/VarrockArmourShopChainbodyTradeTickTask",
    "com/rs2/a/c/ac": "com/rs2/bot/shop/VarrockClothesShopCapeBotTask",
    "com/rs2/a/c/ad": "com/rs2/bot/shop/VarrockClothesShopCapeTradeTickTask",
    "com/rs2/a/c/ae": "com/rs2/bot/shop/VarrockGeneralStoreBotTask",
    "com/rs2/a/c/af": "com/rs2/bot/shop/VarrockGeneralStoreTradeTickTask",
    "com/rs2/a/c/ag": "com/rs2/bot/shop/VarrockRuneShopChaosRuneBotTask",
    "com/rs2/a/c/ah": "com/rs2/bot/shop/VarrockRuneShopChaosRuneTradeTickTask",
    "com/rs2/a/c/ai": "com/rs2/bot/shop/VarrockStaffShopFireStaffBotTask",
    "com/rs2/a/c/aj": "com/rs2/bot/shop/VarrockStaffShopFireStaffTradeTickTask",
    "com/rs2/a/c/ak": "com/rs2/bot/shop/VarrockSwordShopMithrilLongswordBotTask",
    "com/rs2/a/c/al": "com/rs2/bot/shop/VarrockSwordShopMithrilLongswordTradeTickTask",
    "com/rs2/a/c/k": "com/rs2/bot/shop/ArdougnePlatebodyShopBotTask",
    "com/rs2/a/c/l": "com/rs2/bot/shop/ArdougnePlatebodyShopTradeTickTask",
    "com/rs2/a/c/m": "com/rs2/bot/shop/ArdougneAxeShopBotTask",
    "com/rs2/a/c/n": "com/rs2/bot/shop/ArdougneAxeShopTradeTickTask",
    "com/rs2/a/c/q": "com/rs2/bot/shop/CatherbyArcheryShopShortbowBotTask",
    "com/rs2/a/c/r": "com/rs2/bot/shop/CatherbyArcheryShopShortbowTradeTickTask",
    "com/rs2/a/c/s": "com/rs2/bot/shop/CatherbyFishingShopBotTask",
    "com/rs2/a/c/t": "com/rs2/bot/shop/CatherbyFishingShopTradeTickTask",
    "com/rs2/a/c/u": "com/rs2/bot/shop/CatherbyGeneralStoreBotTask",
    "com/rs2/a/c/v": "com/rs2/bot/shop/CatherbyGeneralStoreTradeTickTask",
    "com/rs2/a/d/y": "com/rs2/bot/tasks/DraynorNetFishingBotTask",
    "com/rs2/a/d/E": "com/rs2/bot/tasks/BarbarianVillageFlyFishingBotTask",
    "com/rs2/a/d/ab": "com/rs2/bot/tasks/KaramjaFishingBotTask",
    "com/rs2/a/d/aj": "com/rs2/bot/tasks/AlKharidFlyFishingBotTask",
    "com/rs2/a/d/c": "com/rs2/bot/tasks/AlKharidNetBaitFishingBotTask",
    "com/rs2/a/d/v": "com/rs2/bot/tasks/CatherbyFishingBotTask",
    "com/rs2/a/d/as": "com/rs2/bot/tasks/VarrockLobsterCookingBotTask",
    "com/rs2/a/d/b": "com/rs2/bot/tasks/AlKharidLobsterCookingBotTask",
    "com/rs2/a/d/u": "com/rs2/bot/tasks/CatherbyLobsterCookingBotTask",
    "com/rs2/a/d/aG": "com/rs2/bot/tasks/VarrockSteelDaggerSmithingBotTask",
    "com/rs2/a/d/f": "com/rs2/bot/tasks/AlKharidSteelSmeltingBotTask",
    "com/rs2/a/d/S": "com/rs2/bot/tasks/FaladorSteelSmeltingBotTask",
    "com/rs2/a/d/d": "com/rs2/bot/tasks/AlKharidMineBotTask",
    "com/rs2/a/d/x": "com/rs2/bot/tasks/CraftingGuildMineBotTask",
    "com/rs2/a/d/R": "com/rs2/bot/tasks/DwarvenMineBotTask",
    "com/rs2/a/d/ap": "com/rs2/bot/tasks/MiningGuildMineBotTask",
    "com/rs2/a/d/au": "com/rs2/bot/tasks/VarrockEastMineBotTask",
    "com/rs2/a/d/aI": "com/rs2/bot/tasks/VarrockWestMineBotTask",
    "com/rs2/a/d/aN": "com/rs2/bot/tasks/WildernessRuniteMineBotTask",
    "com/rs2/a/d/aO": "com/rs2/bot/tasks/WildernessRuniteMineEscapeMonitorTickTask",
    "com/rs2/a/d/aq": "com/rs2/bot/tasks/RimmingtonMineBotTask",
    "com/rs2/a/d/g": "com/rs2/bot/tasks/AlKharidCowhideTanningBotTask",
    "com/rs2/a/d/h": "com/rs2/bot/tasks/TanningBotTickTask",
    "com/rs2/a/d/ae": "com/rs2/bot/tasks/LeatherCraftingBotTask",
    "com/rs2/a/d/al": "com/rs2/bot/tasks/DraynorSheepShearingBotTask",
    "com/rs2/a/d/am": "com/rs2/bot/tasks/LumbridgeWoolSpinningBotTask",
    "com/rs2/a/d/r": "com/rs2/bot/tasks/SeersFlaxSpinningBotTask",
    "com/rs2/a/d/ax": "com/rs2/bot/tasks/VarrockRuneEssenceMiningBotTask",
    "com/rs2/a/d/a": "com/rs2/bot/tasks/AirRuneRunecraftingBotTask",
    "com/rs2/a/d/ao": "com/rs2/bot/tasks/MindRuneRunecraftingBotTask",
    "com/rs2/a/d/aM": "com/rs2/bot/tasks/WaterRuneRunecraftingBotTask",
    "com/rs2/a/d/D": "com/rs2/bot/tasks/EarthRuneRunecraftingBotTask",
    "com/rs2/a/d/X": "com/rs2/bot/tasks/FireRuneRunecraftingBotTask",
    "com/rs2/a/d/j": "com/rs2/bot/tasks/BodyRuneRunecraftingBotTask",
    "com/rs2/a/d/A": "com/rs2/bot/tasks/DraynorTreeWoodcuttingBotTask",
    "com/rs2/a/d/z": "com/rs2/bot/tasks/DraynorOakWoodcuttingBotTask",
    "com/rs2/a/d/B": "com/rs2/bot/tasks/DraynorWillowWoodcuttingBotTask",
    "com/rs2/a/d/an": "com/rs2/bot/tasks/DraynorYewWoodcuttingBotTask",
    "com/rs2/a/d/aK": "com/rs2/bot/tasks/VarrockWestTreeWoodcuttingBotTask",
    "com/rs2/a/d/aJ": "com/rs2/bot/tasks/VarrockWestOakWoodcuttingBotTask",
    "com/rs2/a/d/aL": "com/rs2/bot/tasks/VarrockPalaceYewWoodcuttingBotTask",
    "com/rs2/a/d/aw": "com/rs2/bot/tasks/VarrockEastTreeWoodcuttingBotTask",
    "com/rs2/a/d/av": "com/rs2/bot/tasks/VarrockEastOakWoodcuttingBotTask",
    "com/rs2/a/d/K": "com/rs2/bot/tasks/EdgevilleTreeWoodcuttingBotTask",
    "com/rs2/a/d/L": "com/rs2/bot/tasks/EdgevilleYewWoodcuttingBotTask",
    "com/rs2/a/d/U": "com/rs2/bot/tasks/FaladorYewWoodcuttingBotTask",
    "com/rs2/a/d/q": "com/rs2/bot/tasks/SeersMapleWoodcuttingBotTask",
    "com/rs2/a/d/t": "com/rs2/bot/tasks/SeersYewWoodcuttingBotTask",
    "com/rs2/a/d/o": "com/rs2/bot/tasks/SeersMagicTreeWoodcuttingBotTask",
    "com/rs2/a/d/p": "com/rs2/bot/tasks/SorcerersTowerMagicTreeWoodcuttingBotTask",
    "com/rs2/a/d/aH": "com/rs2/bot/tasks/VarrockTradeAdvertBotTask",
    "com/rs2/a/d/T": "com/rs2/bot/tasks/FaladorTradeAdvertBotTask",
    "com/rs2/a/d/s": "com/rs2/bot/tasks/SeersTradeAdvertBotTask",
    "com/rs2/a/d/J": "com/rs2/bot/tasks/EdgevilleTradeAdvertBotTask",
    "com/rs2/a/d/at": "com/rs2/bot/tasks/VarrockDropPartyBotTask",
    "com/rs2/a/d/O": "com/rs2/bot/tasks/FaladorDropPartyBotTask",
    "com/rs2/a/d/V": "com/rs2/bot/tasks/FaladorWineOfZamorakTelegrabBotTask",
    "com/rs2/a/d/W": "com/rs2/bot/tasks/WineOfZamorakTelegrabTickTask",
    "com/rs2/a/d/aP": "com/rs2/bot/tasks/WizardsTowerLesserDemonMagicBotTask",
    "com/rs2/a/d/n": "com/rs2/bot/tasks/SeersFlaxPickingBotTask",
    "com/rs2/a/d/Y": "com/rs2/bot/tasks/EdgevilleDungeonBrassKeyBotTask",
    "com/rs2/a/d/Z": "com/rs2/bot/tasks/BrassKeyPickupTickTask",
    "com/rs2/a/d/aa": "com/rs2/bot/tasks/BrassKeyDungeonEntryTickTask",
    "com/rs2/a/d/M": "com/rs2/bot/tasks/DraynorChickenCombatBotTask",
    "com/rs2/a/d/N": "com/rs2/bot/tasks/FaladorCowCombatBotTask",
    "com/rs2/a/d/ag": "com/rs2/bot/tasks/LumbridgeEastChickenCombatBotTask",
    "com/rs2/a/d/ah": "com/rs2/bot/tasks/LumbridgeWestChickenCombatBotTask",
    "com/rs2/a/d/ai": "com/rs2/bot/tasks/LumbridgeCowCombatBotTask",
    "com/rs2/a/d/af": "com/rs2/bot/tasks/DraynorGoblinCombatBotTask",
    "com/rs2/a/d/ak": "com/rs2/bot/tasks/LumbridgeGoblinCombatBotTask",
    "com/rs2/a/d/e": "com/rs2/bot/tasks/AlKharidWarriorCombatBotTask",
    "com/rs2/a/d/Q": "com/rs2/bot/tasks/FaladorImpCombatBotTask",
    "com/rs2/a/d/aD": "com/rs2/bot/tasks/VarrockGuardCombatBotTask",
    "com/rs2/a/d/aE": "com/rs2/bot/tasks/VarrockSewerGiantRatCombatBotTask",
    "com/rs2/a/d/aF": "com/rs2/bot/tasks/VarrockSewerGiantRatDungeonEntryTickTask",
    "com/rs2/a/d/i": "com/rs2/bot/tasks/BarbarianVillageBarbarianCombatBotTask",
    "com/rs2/a/d/C": "com/rs2/bot/tasks/DwarvenMineDwarfCombatBotTask",
    "com/rs2/a/d/P": "com/rs2/bot/tasks/FaladorGuardCombatBotTask",
    "com/rs2/a/d/F": "com/rs2/bot/tasks/EdgevilleDungeonSpiderRatCombatBotTask",
    "com/rs2/a/d/G": "com/rs2/bot/tasks/SpiderRatDungeonEntryTickTask",
    "com/rs2/a/d/H": "com/rs2/bot/tasks/EdgevilleDungeonSkeletonCombatBotTask",
    "com/rs2/a/d/I": "com/rs2/bot/tasks/SkeletonDungeonEntryTickTask",
    "com/rs2/a/d/w": "com/rs2/bot/tasks/VarrockSouthChickenCombatBotTask",
    "com/rs2/a/d/ay": "com/rs2/bot/tasks/EdgevilleDungeonHillGiantCombatBotTask",
    "com/rs2/a/d/az": "com/rs2/bot/tasks/EdgevilleDungeonNorthMossGiantCombatBotTask",
    "com/rs2/a/d/aA": "com/rs2/bot/tasks/NorthMossGiantDungeonEntryTickTask",
    "com/rs2/a/d/aB": "com/rs2/bot/tasks/EdgevilleDungeonSouthMossGiantCombatBotTask",
    "com/rs2/a/d/aC": "com/rs2/bot/tasks/SouthMossGiantDungeonEntryTickTask",
    "com/rs2/a/d/ac": "com/rs2/bot/tasks/KaramjaVolcanoNorthLesserDemonCombatBotTask",
    "com/rs2/a/d/ad": "com/rs2/bot/tasks/KaramjaVolcanoSouthLesserDemonCombatBotTask",
    "com/rs2/a/d/k": "com/rs2/bot/tasks/BrimhavenDungeonBlueDragonNorthCombatBotTask",
    "com/rs2/a/d/l": "com/rs2/bot/tasks/BrimhavenDungeonBlueDragonSouthCombatBotTask",
    "com/rs2/a/d/m": "com/rs2/bot/tasks/BrimhavenDungeonRedDragonCombatBotTask",
    "com/rs2/a/d/ar": "com/rs2/bot/tasks/TaverleyDungeonHellhoundCombatBotTask",
    "com/rs2/model/d/i": "com/rs2/model/player/Player",
    "com/rs2/model/d/a": "com/rs2/model/player/BankManager",
    "com/rs2/model/d/b": "com/rs2/model/player/DelayedBankOpenTask",
    "com/rs2/model/d/y": "com/rs2/model/player/BankRearrangeMode",
    "com/rs2/model/d/b/a": "com/rs2/model/item/ItemStack",
    "com/rs2/model/a": "com/rs2/model/Entity",
    "com/rs2/model/d": "com/rs2/model/EntityReference",
    "com/rs2/model/g": "com/rs2/model/Position",
    "com/rs2/model/g/a": "com/rs2/model/travel/canoe/CanoeTravelManager",
    "com/rs2/model/g/b": "com/rs2/model/travel/canoe/CanoeBuildTask",
    "com/rs2/model/g/c": "com/rs2/model/travel/canoe/CanoeTreeCutTask",
    "com/rs2/model/g/d": "com/rs2/model/travel/canoe/CanoeTreeDefinition",
    "com/rs2/model/g/e": "com/rs2/model/travel/canoe/CanoeTypeDefinition",
    "com/rs2/model/g/f": "com/rs2/model/travel/canoe/CanoeStation",
    "com/rs2/model/g/g": "com/rs2/model/travel/TravelManager",
    "com/rs2/model/g/h": "com/rs2/model/travel/GnomeGliderTravelTask",
    "com/rs2/model/g/i": "com/rs2/model/travel/GnomeGliderLandingTask",
    "com/rs2/model/g/j": "com/rs2/model/travel/GnomeGliderDestination",
    "com/rs2/model/g/k": "com/rs2/model/travel/ChargedJewelryDefinition",
    "com/rs2/model/g/l": "com/rs2/model/travel/ShipTravelArrivalTask",
    "com/rs2/model/g/m": "com/rs2/model/travel/ShipRoute",
    "com/rs2/model/g/n": "com/rs2/model/travel/HajedyCartRoute",
    "com/rs2/model/h": "com/rs2/model/World",
    "com/rs2/model/b": "com/rs2/model/DamageContributionComparator",
    "com/rs2/model/e": "com/rs2/model/MovementQueue",
    "com/rs2/model/f": "com/rs2/model/MovementStep",
    "com/rs2/model/i": "com/rs2/model/BotReloginTask",
    "com/rs2/model/e/a": "com/rs2/model/area/MultiwayAreaDefinition",
    "com/rs2/model/e/a/a": "com/rs2/model/music/MusicTrackDefinition",
    "com/rs2/model/e/a/b": "com/rs2/model/music/MusicAreaDefinition",
    "com/rs2/model/e/a/c": "com/rs2/model/music/MusicManager",
    "com/rs2/model/b/a": "com/rs2/model/ground/GroundItem",
    "com/rs2/model/b/b": "com/rs2/model/ground/GroundItemVisibility",
    "com/rs2/model/b/c": "com/rs2/model/ground/GroundItemManager",
    "com/rs2/model/objects/a": "com/rs2/model/objects/DynamicObject",
    "com/rs2/model/objects/b": "com/rs2/model/objects/WorldObject",
    "com/rs2/b/c/a": "com/rs2/model/objects/LoadedWorldObject",
    "com/rs2/b/c/b": "com/rs2/model/objects/ObjectDefinition",
    "com/rs2/b/c/c": "com/rs2/model/objects/WorldObjectLookup",
    "com/rs2/b/d/a": "com/rs2/model/objects/ObjectRegionKey",
    "com/rs2/b/d/b": "com/rs2/model/objects/WorldObjectRegionIndex",
    "com/rs2/model/objects/functions/a": "com/rs2/model/objects/functions/DoorHandler",
    "com/rs2/model/objects/functions/b": "com/rs2/model/objects/functions/DoorResetEvent",
    "com/rs2/model/objects/functions/c": "com/rs2/model/objects/functions/DoubleDoorHandler",
    "com/rs2/model/objects/functions/d": "com/rs2/model/objects/functions/FlourMillHandler",
    "com/rs2/model/objects/functions/f": "com/rs2/model/objects/functions/DelayedObjectMoveEvent",
    "com/rs2/model/objects/functions/g": "com/rs2/model/objects/functions/WildernessObelisk",
    "com/rs2/model/objects/functions/h": "com/rs2/model/objects/functions/PickableObjectHandler",
    "com/rs2/model/objects/functions/i": "com/rs2/model/objects/functions/PickableObjectEvent",
    "com/rs2/model/objects/functions/j": "com/rs2/model/objects/functions/ObjectToggleEvent",
    "com/rs2/model/objects/functions/k": "com/rs2/model/objects/functions/WebSlashEvent",
    "com/rs2/model/c/a": "com/rs2/model/npc/Npc",
    "com/rs2/model/c/a/a": "com/rs2/model/npc/drop/NpcDropTable",
    "com/rs2/model/c/a/b": "com/rs2/model/npc/drop/NpcDropEntry",
    "com/rs2/model/c/a/c": "com/rs2/model/npc/drop/NpcDropManager",
    "com/rs2/model/c/aJ": "com/rs2/model/npc/NpcDefinition",
    "com/rs2/model/c/b": "com/rs2/model/npc/NpcStatRestoreTask",
    "com/rs2/model/c/c": "com/rs2/model/npc/NpcDialogueTeleportEvent",
    "com/rs2/model/c/d": "com/rs2/model/npc/MageArenaChallengeStartTask",
    "com/rs2/model/c/e": "com/rs2/model/npc/AbyssMageTeleportEvent",
    "com/rs2/model/c/f": "com/rs2/model/npc/NpcRelocationEvent",
    "com/rs2/model/c/g": "com/rs2/model/npc/NpcSequenceAdvanceTask",
    "com/rs2/model/c/h": "com/rs2/model/npc/NpcStageAdvanceTask",
    "com/rs2/model/c/i": "com/rs2/model/npc/NpcMovementMode",
    "com/rs2/model/c/j": "com/rs2/model/c/ProjectileDefinition",
    "com/rs2/model/c/k": "com/rs2/model/c/SheepShearingTask",
    "com/rs2/model/c/l": "com/rs2/model/npc/combat/NpcCombatDefinition",
    "com/rs2/model/c/m": "com/rs2/model/npc/combat/DefaultNpcCombatDefinition",
    "com/rs2/model/c/aK": "com/rs2/model/npc/combat/NpcDefinitionAttackStyleCombatDefinition",
    "com/rs2/model/c/aL": "com/rs2/model/npc/combat/NpcDefinitionMeleeCombatDefinition",
    "com/rs2/model/c/av": "com/rs2/model/c/KingBlackDragonCombatDefinition",
    "com/rs2/model/c/ap": "com/rs2/model/c/RedDragonCombatDefinition",
    "com/rs2/model/c/an": "com/rs2/model/c/BlackDragonCombatDefinition",
    "com/rs2/model/c/ao": "com/rs2/model/c/BlueDragonCombatDefinition",
    "com/rs2/model/c/ar": "com/rs2/model/c/GreenDragonCombatDefinition",
    "com/rs2/model/c/as": "com/rs2/model/c/BronzeDragonCombatDefinition",
    "com/rs2/model/c/at": "com/rs2/model/c/IronDragonCombatDefinition",
    "com/rs2/model/c/au": "com/rs2/model/c/SteelDragonCombatDefinition",
    "com/rs2/model/c/A": "com/rs2/model/c/CommanderZilyanaCombatDefinition",
    "com/rs2/model/c/B": "com/rs2/model/c/KrilTsutsarothCombatDefinition",
    "com/rs2/model/c/C": "com/rs2/model/c/KreeArraCombatDefinition",
    "com/rs2/model/c/D": "com/rs2/model/c/GeneralGraardorCombatDefinition",
    "com/rs2/model/c/am": "com/rs2/model/c/AhrimCombatDefinition",
    "com/rs2/model/c/aE": "com/rs2/model/c/KarilCombatDefinition",
    "com/rs2/model/c/aC": "com/rs2/model/c/KalphiteQueenFirstFormCombatDefinition",
    "com/rs2/model/c/aD": "com/rs2/model/c/KalphiteQueenSecondFormCombatDefinition",
    "com/rs2/model/c/N": "com/rs2/model/c/DagannothSupremeCombatDefinition",
    "com/rs2/model/c/L": "com/rs2/model/c/DagannothPrimeCombatDefinition",
    "com/rs2/model/c/ah": "com/rs2/model/c/TokXilCombatDefinition",
    "com/rs2/model/c/ai": "com/rs2/model/c/YtMejKotCombatDefinition",
    "com/rs2/model/c/aj": "com/rs2/model/c/KetZekCombatDefinition",
    "com/rs2/model/c/ak": "com/rs2/model/c/TzTokJadCombatDefinition",
    "com/rs2/model/c/al": "com/rs2/model/c/YtHurKotCombatDefinition",
    "com/rs2/model/c/H": "com/rs2/model/c/ZaklnGritchCombatDefinition",
    "com/rs2/model/c/F": "com/rs2/model/c/BalfrugKreeyathCombatDefinition",
    "com/rs2/model/c/z": "com/rs2/model/c/WingmanSkreeCombatDefinition",
    "com/rs2/model/c/G": "com/rs2/model/c/FlockleaderGeerinCombatDefinition",
    "com/rs2/model/c/U": "com/rs2/model/c/ZamorakSpiritualMageCombatDefinition",
    "com/rs2/model/c/w": "com/rs2/model/c/ArmadylSpiritualRangerCombatDefinition",
    "com/rs2/model/c/x": "com/rs2/model/c/ArmadylSpiritualMageCombatDefinition",
    "com/rs2/model/c/aH": "com/rs2/model/c/AviansieRanged1191Max8CombatDefinition",
    "com/rs2/model/c/r": "com/rs2/model/c/AviansieRanged1190Max9CombatDefinition",
    "com/rs2/model/c/s": "com/rs2/model/c/AviansieRanged1190Max10CombatDefinition",
    "com/rs2/model/c/o": "com/rs2/model/c/AviansieRanged1191Max11CombatDefinition",
    "com/rs2/model/c/v": "com/rs2/model/c/AviansieRanged1190Max15CombatDefinition",
    "com/rs2/model/c/q": "com/rs2/model/c/AviansieRanged1191Max16CombatDefinition",
    "com/rs2/model/c/aI": "com/rs2/model/c/AviansieRanged1191Max10CombatDefinition",
    "com/rs2/model/c/t": "com/rs2/model/c/AviansieRanged1190Max11CombatDefinition",
    "com/rs2/model/c/u": "com/rs2/model/c/AviansieRanged1190Max12CombatDefinition",
    "com/rs2/model/c/p": "com/rs2/model/c/AviansieRanged1191Max15CombatDefinition",
    "com/rs2/model/c/af": "com/rs2/model/c/GrowlerCombatDefinition",
    "com/rs2/model/c/aq": "com/rs2/model/c/BreeCombatDefinition",
    "com/rs2/model/c/aB": "com/rs2/model/c/SpiritualRangerCombatDefinition",
    "com/rs2/model/c/K": "com/rs2/model/c/BandosSpiritualRangerCombatDefinition",
    "com/rs2/model/c/J": "com/rs2/model/c/SaradominMagicFollowerCombatDefinition",
    "com/rs2/model/c/Y": "com/rs2/model/c/ZamorakWizardCombatDefinition",
    "com/rs2/model/c/y": "com/rs2/model/c/SaradominWizardCombatDefinition",
    "com/rs2/model/c/E": "com/rs2/model/c/SergeantSteelwillCombatDefinition",
    "com/rs2/model/c/I": "com/rs2/model/c/SergeantGrimspikeCombatDefinition",
    "com/rs2/model/c/R": "com/rs2/model/c/KolodionCombatDefinition",
    "com/rs2/model/c/S": "com/rs2/model/c/ZamorakBattleMageCombatDefinition",
    "com/rs2/model/c/T": "com/rs2/model/c/SaradominBattleMageCombatDefinition",
    "com/rs2/model/c/V": "com/rs2/model/c/GuthixBattleMageCombatDefinition",
    "com/rs2/model/c/aF": "com/rs2/model/c/ChaosElementalCombatDefinition",
    "com/rs2/model/c/aa": "com/rs2/model/c/DarkWizardLevel20CombatDefinition",
    "com/rs2/model/c/Z": "com/rs2/model/c/DarkWizardLevel7CombatDefinition",
    "com/rs2/model/c/n": "com/rs2/model/c/ChaosDruidCombatDefinition",
    "com/rs2/model/c/aw": "com/rs2/model/c/ElvargCombatDefinition",
    "com/rs2/model/c/ax": "com/rs2/model/c/MelzarTheMadCombatDefinition",
    "com/rs2/model/c/az": "com/rs2/model/c/CaradoCombatDefinition",
    "com/rs2/model/c/ay": "com/rs2/model/c/ZooknockCombatDefinition",
    "com/rs2/model/c/M": "com/rs2/model/c/MonkeyArcherCombatDefinition",
    "com/rs2/model/c/aA": "com/rs2/model/c/JungleDemonCombatDefinition",
    "com/rs2/model/c/W": "com/rs2/model/c/AberrantSpecterCombatDefinition",
    "com/rs2/model/c/X": "com/rs2/model/c/InfernalMageCombatDefinition",
    "com/rs2/model/c/Q": "com/rs2/model/c/DagannothCombatDefinition",
    "com/rs2/model/c/O": "com/rs2/model/c/WallasalkiCombatDefinition",
    "com/rs2/model/c/ab": "com/rs2/model/c/WizardCombatDefinition",
    "com/rs2/model/c/ac": "com/rs2/model/c/FireWizardCombatDefinition",
    "com/rs2/model/c/ad": "com/rs2/model/c/WaterWizardCombatDefinition",
    "com/rs2/model/c/ae": "com/rs2/model/c/EarthWizardCombatDefinition",
    "com/rs2/model/c/ag": "com/rs2/model/c/AirWizardCombatDefinition",
    "com/rs2/model/c/P": "com/rs2/model/c/SpinolypCombatDefinition",
    "com/rs2/model/c/aG": "com/rs2/model/c/SkeletalWyvernCombatDefinition",
    "com/rs2/model/a/f": "com/rs2/model/gameplay/CaveLightManager",
    "com/rs2/model/a/g": "com/rs2/model/gameplay/SwampGasExplosionTask",
    "com/rs2/model/a/g/b": "com/rs2/model/gameplay/PositionRange",
    "com/rs2/model/c": "com/rs2/model/LegacyCombatType",
    "com/rs2/model/a/g/a": "com/rs2/model/travel/TravelBootstrap",
    "com/rs2/model/a/g/d/a": "com/rs2/model/travel/DefaultTravelBootstrap",
    "com/rs2/model/a/h": "com/rs2/model/gameplay/CaveInsectSwarmTask",
    "com/rs2/model/a/i": "com/rs2/model/gameplay/CaveInsectDamageTask",
    "com/rs2/model/a/j": "com/rs2/model/gameplay/DesertHeatManager",
    "com/rs2/model/a/k": "com/rs2/model/gameplay/DesertThirstTask",
    "com/rs2/model/a/s": "com/rs2/model/message/MessageOfTheWeek",
    "com/rs2/model/a/t": "com/rs2/model/gameplay/partyroom/PartyRoomManager",
    "com/rs2/model/a/u": "com/rs2/model/gameplay/partyroom/PartyRoomBalloonSpawnTask",
    "com/rs2/model/a/v": "com/rs2/model/gameplay/partyroom/PartyRoomBalloonReward",
    "com/rs2/model/a/g/e/a": "com/rs2/model/gameplay/godwars/GodWarsDungeonManager",
    "com/rs2/model/a/g/e/b": "com/rs2/model/gameplay/godwars/BandosStrongholdDoorTask",
    "com/rs2/model/a/g/a/a": "com/rs2/model/gameplay/barrows/BarrowsManager",
    "com/rs2/model/a/g/a/b": "com/rs2/model/gameplay/barrows/BarrowsRewardTable",
    "com/rs2/model/a/g/a/c": "com/rs2/model/gameplay/barrows/BarrowsPrayerDrainTask",
    "com/rs2/model/a/g/a/d": "com/rs2/model/gameplay/barrows/BarrowsPrayerDrainResetTask",
    "com/rs2/model/a/g/a/e": "com/rs2/model/gameplay/barrows/BarrowsRewardEntry",
    "com/rs2/model/a/g/a/f": "com/rs2/model/gameplay/barrows/BarrowsDoorPuzzle",
    "com/rs2/model/a/g/a/g": "com/rs2/model/gameplay/barrows/BarrowsTunnelRoom",
    "com/rs2/model/a/g/b/a": "com/rs2/model/gameplay/duel/DuelArenaLocationManager",
    "com/rs2/model/a/g/b/b": "com/rs2/model/gameplay/duel/DuelInterfaceManager",
    "com/rs2/model/a/g/b/c": "com/rs2/model/gameplay/duel/DuelSession",
    "com/rs2/model/a/g/b/d": "com/rs2/model/gameplay/duel/DuelVictoryTask",
    "com/rs2/model/a/g/b/e": "com/rs2/model/gameplay/duel/DuelCountdownTask",
    "com/rs2/model/a/g/b/f": "com/rs2/model/gameplay/duel/DuelHistory",
    "com/rs2/model/a/g/b/g": "com/rs2/model/gameplay/duel/DuelController",
    "com/rs2/model/a/g/b/h": "com/rs2/model/gameplay/duel/DuelRule",
    "com/rs2/model/a/g/b/A": "com/rs2/model/gameplay/duel/NoForfeitDuelRule",
    "com/rs2/model/a/g/b/B": "com/rs2/model/gameplay/duel/NoPotionsDuelRule",
    "com/rs2/model/a/g/b/C": "com/rs2/model/gameplay/duel/NoFoodDuelRule",
    "com/rs2/model/a/g/b/D": "com/rs2/model/gameplay/duel/NoPrayerDuelRule",
    "com/rs2/model/a/g/b/i": "com/rs2/model/gameplay/duel/NoRangedDuelRule",
    "com/rs2/model/a/g/b/j": "com/rs2/model/gameplay/duel/NoMovementDuelRule",
    "com/rs2/model/a/g/b/k": "com/rs2/model/gameplay/duel/ObstaclesDuelRule",
    "com/rs2/model/a/g/b/l": "com/rs2/model/gameplay/duel/NoHelmetDuelRule",
    "com/rs2/model/a/g/b/m": "com/rs2/model/gameplay/duel/NoCapeDuelRule",
    "com/rs2/model/a/g/b/n": "com/rs2/model/gameplay/duel/NoAmuletDuelRule",
    "com/rs2/model/a/g/b/o": "com/rs2/model/gameplay/duel/NoAmmoDuelRule",
    "com/rs2/model/a/g/b/p": "com/rs2/model/gameplay/duel/NoWeaponDuelRule",
    "com/rs2/model/a/g/b/q": "com/rs2/model/gameplay/duel/NoBodyDuelRule",
    "com/rs2/model/a/g/b/r": "com/rs2/model/gameplay/duel/NoShieldDuelRule",
    "com/rs2/model/a/g/b/s": "com/rs2/model/gameplay/duel/NoLegsDuelRule",
    "com/rs2/model/a/g/b/t": "com/rs2/model/gameplay/duel/NoMeleeDuelRule",
    "com/rs2/model/a/g/b/u": "com/rs2/model/gameplay/duel/NoGlovesDuelRule",
    "com/rs2/model/a/g/b/v": "com/rs2/model/gameplay/duel/NoBootsDuelRule",
    "com/rs2/model/a/g/b/w": "com/rs2/model/gameplay/duel/NoRingDuelRule",
    "com/rs2/model/a/g/b/x": "com/rs2/model/gameplay/duel/NoMagicDuelRule",
    "com/rs2/model/a/g/b/y": "com/rs2/model/gameplay/duel/NoSpecialAttackDuelRule",
    "com/rs2/model/a/g/b/z": "com/rs2/model/gameplay/duel/FunWeaponsDuelRule",
    "com/rs2/model/a/g/c/a": "com/rs2/model/gameplay/fightcave/FightCaveSpawnTable",
    "com/rs2/model/a/g/c/b": "com/rs2/model/gameplay/fightcave/FightCaveController",
    "com/rs2/model/a/g/c/c": "com/rs2/model/gameplay/fightcave/FightCaveCompletionTask",
    "com/rs2/model/a/g/c/d": "com/rs2/model/gameplay/fightcave/FightCaveWaveSpawner",
    "com/rs2/model/a/g/c/e": "com/rs2/model/gameplay/fightcave/FightCaveNpcLevelComparator",
    "com/rs2/model/a/g/f/a": "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
    "com/rs2/model/a/g/f/b": "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundRotationTask",
    "com/rs2/model/a/g/f/c": "com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController",
    "com/rs2/model/a/g/f/d": "com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardHazardTask",
    "com/rs2/model/a/g/f/e": "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
    "com/rs2/model/a/g/f/f": "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberColorTask",
    "com/rs2/model/a/g/f/g": "com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaLobby",
    "com/rs2/model/a/g/f/h": "com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardShop",
    "com/rs2/model/a/g/f/i": "com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardDefinition",
    "com/rs2/model/a/g/f/j": "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
    "com/rs2/model/a/k/a": "com/rs2/model/clue/AnagramClue",
    "com/rs2/model/a/k/b": "com/rs2/model/clue/ChallengeQuestion",
    "com/rs2/model/a/k/c": "com/rs2/model/clue/TreasureTrailManager",
    "com/rs2/model/a/k/d": "com/rs2/model/clue/CoordinateClueHandler",
    "com/rs2/model/a/k/e": "com/rs2/model/clue/CoordinateClue",
    "com/rs2/model/a/k/f": "com/rs2/model/clue/CrypticDigClue",
    "com/rs2/model/a/k/g": "com/rs2/model/clue/ClueKeyHandler",
    "com/rs2/model/a/k/h": "com/rs2/model/clue/MapClue",
    "com/rs2/model/a/k/i": "com/rs2/model/clue/PuzzleBoxHandler",
    "com/rs2/model/a/k/j": "com/rs2/model/clue/SearchClue",
    "com/rs2/model/a/k/k": "com/rs2/model/clue/NpcClue",
    "com/rs2/model/a/a/a": "com/rs2/model/combat/attack/CombatAttack",
    "com/rs2/model/a/a/c": "com/rs2/model/combat/attack/CombatAttackState",
    "com/rs2/model/a/a/d": "com/rs2/model/combat/CombatCycleEvent",
    "com/rs2/model/a/a/e": "com/rs2/model/combat/AttackValidationResult",
    "com/rs2/model/a/a/f": "com/rs2/model/combat/CombatManager",
    "com/rs2/model/a/a/i": "com/rs2/model/combat/KalphiteQueenRespawnCombatEvent",
    "com/rs2/model/a/a/j": "com/rs2/model/combat/NpcRespawnCombatEvent",
    "com/rs2/model/a/a/m": "com/rs2/model/combat/CombatTargetDelayTimer",
    "com/rs2/model/a/a/n": "com/rs2/model/combat/PvpCombatReference",
    "com/rs2/model/a/a/f/a": "com/rs2/model/item/DegradableEquipmentHandler",
    "com/rs2/model/a/a/g": "com/rs2/model/combat/DeathAnimationTask",
    "com/rs2/model/a/a/h": "com/rs2/model/combat/DeathCleanupTask",
    "com/rs2/model/a/a/k": "com/rs2/model/combat/attack/CombatAttackProvider",
    "com/rs2/model/a/a/l": "com/rs2/model/combat/attack/DefaultCombatAttackProvider",
    "com/rs2/model/a/a/a/a": "com/rs2/model/combat/attack/BaseCombatAttack",
    "com/rs2/model/a/a/a/b": "com/rs2/model/combat/attack/AreaAttackTargetDistanceComparator",
    "com/rs2/model/a/a/a/c": "com/rs2/model/combat/attack/ForcedMeleeCombatAttack",
    "com/rs2/model/a/a/a/d": "com/rs2/model/combat/attack/MeleeCombatAttack",
    "com/rs2/model/a/a/a/e": "com/rs2/model/combat/attack/ProjectileCombatAttack",
    "com/rs2/model/a/a/a/f": "com/rs2/model/combat/attack/ForcedProjectileCombatAttack",
    "com/rs2/model/a/a/a/g": "com/rs2/model/combat/attack/FlaggedProjectileCombatAttack",
    "com/rs2/model/a/a/a/h": "com/rs2/model/combat/attack/MagicCombatAttack",
    "com/rs2/model/a/a/a/m": "com/rs2/model/combat/attack/WeaponCombatAttack",
    "com/rs2/model/a/a/a/i": "com/rs2/model/combat/requirement/MagicCombatMembersRequirement",
    "com/rs2/model/a/a/a/j": "com/rs2/model/combat/requirement/MagicCombatRuneRequirement",
    "com/rs2/model/a/a/a/k": "com/rs2/model/combat/requirement/MagicCombatLevelRequirement",
    "com/rs2/model/a/a/a/l": "com/rs2/model/combat/requirement/GodStaffRequirement",
    "com/rs2/model/a/a/a/n": "com/rs2/model/combat/requirement/AmmunitionRequirement",
    "com/rs2/model/a/a/e/a": "com/rs2/model/combat/special/SpecialAttackDefinition",
    "com/rs2/model/a/a/e/b": "com/rs2/model/combat/special/DragonDaggerSpecialDefinition",
    "com/rs2/model/a/a/e/c": "com/rs2/model/combat/special/DragonDaggerSpecialAttack",
    "com/rs2/model/a/a/e/d": "com/rs2/model/combat/special/DragonSpearSpecialDefinition",
    "com/rs2/model/a/a/e/e": "com/rs2/model/combat/special/DragonSpearSpecialAttack",
    "com/rs2/model/a/a/e/A": "com/rs2/model/combat/special/AbyssalWhipSpecialDefinition",
    "com/rs2/model/a/a/e/B": "com/rs2/model/combat/special/AbyssalWhipSpecialAttack",
    "com/rs2/model/a/a/e/C": "com/rs2/model/combat/special/ArmadylGodswordSpecialDefinition",
    "com/rs2/model/a/a/e/D": "com/rs2/model/combat/special/ArmadylGodswordSpecialAttack",
    "com/rs2/model/a/a/e/E": "com/rs2/model/combat/special/ZamorakGodswordSpecialDefinition",
    "com/rs2/model/a/a/e/F": "com/rs2/model/combat/special/ZamorakGodswordSpecialAttack",
    "com/rs2/model/a/a/e/M": "com/rs2/model/combat/special/RuneThrownaxeSpecialDefinition",
    "com/rs2/model/a/a/e/N": "com/rs2/model/combat/special/RuneThrownaxeSpecialAttack",
    "com/rs2/model/a/a/e/O": "com/rs2/model/combat/special/RuneThrownaxeTargetDistanceComparator",
    "com/rs2/model/a/a/e/P": "com/rs2/model/combat/special/DragonLongswordSpecialDefinition",
    "com/rs2/model/a/a/e/Q": "com/rs2/model/combat/special/DragonLongswordSpecialAttack",
    "com/rs2/model/a/a/e/R": "com/rs2/model/combat/special/DragonScimitarSpecialDefinition",
    "com/rs2/model/a/a/e/S": "com/rs2/model/combat/special/DragonScimitarSpecialAttack",
    "com/rs2/model/a/a/e/T": "com/rs2/model/combat/special/RuneClawsSpecialDefinition",
    "com/rs2/model/a/a/e/U": "com/rs2/model/combat/special/RuneClawsSpecialAttack",
    "com/rs2/model/a/a/e/V": "com/rs2/model/combat/special/DragonMaceSpecialDefinition",
    "com/rs2/model/a/a/e/W": "com/rs2/model/combat/special/DragonMaceSpecialAttack",
    "com/rs2/model/a/a/e/X": "com/rs2/model/combat/special/DragonAxeSpecialDefinition",
    "com/rs2/model/a/a/e/Y": "com/rs2/model/combat/special/DragonAxeSpecialAttack",
    "com/rs2/model/a/a/e/Z": "com/rs2/model/combat/special/DarklightSpecialDefinition",
    "com/rs2/model/a/a/e/aa": "com/rs2/model/combat/special/DarklightSpecialAttack",
    "com/rs2/model/a/a/e/f": "com/rs2/model/combat/special/DragonHalberdSpecialDefinition",
    "com/rs2/model/a/a/e/g": "com/rs2/model/combat/special/DragonHalberdSpecialAttack",
    "com/rs2/model/a/a/e/G": "com/rs2/model/combat/special/SaradominGodswordSpecialDefinition",
    "com/rs2/model/a/a/e/H": "com/rs2/model/combat/special/SaradominGodswordSpecialAttack",
    "com/rs2/model/a/a/e/h": "com/rs2/model/combat/special/Dragon2hSwordSpecialDefinition",
    "com/rs2/model/a/a/e/i": "com/rs2/model/combat/special/Dragon2hSwordSpecialAttack",
    "com/rs2/model/a/a/e/I": "com/rs2/model/combat/special/SaradominSwordSpecialDefinition",
    "com/rs2/model/a/a/e/J": "com/rs2/model/combat/special/SaradominSwordSpecialAttack",
    "com/rs2/model/a/a/e/j": "com/rs2/model/combat/special/SeercullSpecialDefinition",
    "com/rs2/model/a/a/e/k": "com/rs2/model/combat/special/SeercullSpecialAttack",
    "com/rs2/model/a/a/e/l": "com/rs2/model/combat/special/SeercullArrowRequirement",
    "com/rs2/model/a/a/e/K": "com/rs2/model/combat/special/GraniteMaulSpecialDefinition",
    "com/rs2/model/a/a/e/L": "com/rs2/model/combat/special/GraniteMaulSpecialAttack",
    "com/rs2/model/a/a/e/m": "com/rs2/model/combat/special/DarkBowSpecialDefinition",
    "com/rs2/model/a/a/e/n": "com/rs2/model/combat/special/DarkBowSpecialAttack",
    "com/rs2/model/a/a/e/o": "com/rs2/model/combat/special/DarkBowArrowRequirement",
    "com/rs2/model/a/a/e/p": "com/rs2/model/combat/special/MagicShortbowSpecialDefinition",
    "com/rs2/model/a/a/e/q": "com/rs2/model/combat/special/MagicShortbowSpecialAttack",
    "com/rs2/model/a/a/e/r": "com/rs2/model/combat/special/MagicShortbowArrowRequirement",
    "com/rs2/model/a/a/e/s": "com/rs2/model/combat/special/MagicShortbowDelayedGraphicTask",
    "com/rs2/model/a/a/e/t": "com/rs2/model/combat/special/MagicLongbowSpecialDefinition",
    "com/rs2/model/a/a/e/u": "com/rs2/model/combat/special/MagicLongbowSpecialAttack",
    "com/rs2/model/a/a/e/v": "com/rs2/model/combat/special/MagicLongbowArrowRequirement",
    "com/rs2/model/a/a/e/w": "com/rs2/model/combat/special/DragonBattleaxeSpecialDefinition",
    "com/rs2/model/a/a/e/x": "com/rs2/model/combat/special/ExcaliburSpecialDefinition",
    "com/rs2/model/a/a/e/y": "com/rs2/model/combat/special/BandosGodswordSpecialDefinition",
    "com/rs2/model/a/a/e/z": "com/rs2/model/combat/special/BandosGodswordSpecialAttack",
    "com/rs2/model/a/a/b": "com/rs2/model/combat/CombatType",
    "com/rs2/model/a/a/b/a": "com/rs2/model/combat/effect/CombatEffect",
    "com/rs2/model/a/a/b/b": "com/rs2/model/combat/effect/CombatEffectTask",
    "com/rs2/model/a/a/b/a/a": "com/rs2/model/combat/effect/MovementLockEffect",
    "com/rs2/model/a/a/b/a/b": "com/rs2/model/combat/effect/BloodSpellHealEffect",
    "com/rs2/model/a/a/b/a/c": "com/rs2/model/combat/effect/PoisonEffect",
    "com/rs2/model/a/a/b/a/d": "com/rs2/model/combat/effect/PoisonDamageTask",
    "com/rs2/model/a/a/b/a/e": "com/rs2/model/combat/effect/StatDrainEffect",
    "com/rs2/model/a/a/b/a/f": "com/rs2/model/combat/effect/WallBeastStunEffect",
    "com/rs2/model/a/a/b/a/g": "com/rs2/model/combat/effect/SummonNpcCombatEffect",
    "com/rs2/model/a/a/b/a/h": "com/rs2/model/combat/effect/TeleblockEffect",
    "com/rs2/model/a/a/b/a/i": "com/rs2/model/combat/effect/TargetRandomTeleportEffect",
    "com/rs2/model/a/a/b/a/j": "com/rs2/model/combat/effect/DisarmWeaponEffect",
    "com/rs2/model/a/a/g/a": "com/rs2/model/combat/AttackBonusType",
    "com/rs2/model/a/a/g/b": "com/rs2/model/combat/AttackXpMode",
    "com/rs2/model/a/a/g/c": "com/rs2/model/combat/AmmunitionDefinition",
    "com/rs2/model/a/a/g/d": "com/rs2/model/combat/AmmunitionProfile",
    "com/rs2/model/a/a/g/e": "com/rs2/model/combat/WeaponProfile",
    "com/rs2/model/a/a/g/f": "com/rs2/model/combat/WeaponInterfaceDefinition",
    "com/rs2/model/a/a/c/a": "com/rs2/model/combat/hit/HitDefinition",
    "com/rs2/model/a/a/c/b": "com/rs2/model/combat/hit/DamageContribution",
    "com/rs2/model/a/a/c/c": "com/rs2/model/combat/hit/HitType",
    "com/rs2/model/a/a/d/a": "com/rs2/model/combat/ProjectileTiming",
    "com/rs2/model/objects/functions/e": "com/rs2/model/combat/AttackStyleDefinition",
    "com/rs2/model/a/h/a": "com/rs2/model/quest/NullQuestScript",
    "com/rs2/model/a/h/b": "com/rs2/model/quest/QuestScript",
    "com/rs2/model/a/h/c": "com/rs2/model/quest/QuestConstants",
    "com/rs2/model/a/h/d": "com/rs2/model/quest/QuestDefinition",
    "com/rs2/model/a/h/e": "com/rs2/model/quest/QuestManager",
    "com/rs2/model/a/h/f": "com/rs2/model/quest/QuestArea",
    "com/rs2/model/a/h/g": "com/rs2/model/quest/QuestHook",
    "com/rs2/model/a/h/h": "com/rs2/model/quest/QuestEventRegistry",
    "com/rs2/model/a/h/a/a": "com/rs2/model/quest/event/EasterEggDropEventHook",
    "com/rs2/model/a/h/a/b": "com/rs2/model/quest/event/EasterEggDropRefreshTask",
    "com/rs2/model/a/h/a/c": "com/rs2/model/quest/event/HalloweenMaskDropEventHook",
    "com/rs2/model/a/h/a/d": "com/rs2/model/quest/event/HalloweenMaskDropRefreshTask",
    "com/rs2/model/a/h/a/e": "com/rs2/model/quest/event/NoopQuestEventHook",
    "com/rs2/model/a/h/a/f": "com/rs2/model/quest/event/ServerMaintenanceEventHook",
    "com/rs2/model/a/h/a/g": "com/rs2/model/quest/event/FrozenBotRelogScanTask",
    "com/rs2/model/a/h/a/h": "com/rs2/model/quest/event/FrozenBotReloginTask",
    "com/rs2/model/a/h/a/i": "com/rs2/model/quest/event/GrandExchangeOfferUpdateTask",
    "com/rs2/model/a/h/a/j": "com/rs2/model/quest/event/GrandExchangeManagerRefreshTask",
    "com/rs2/model/a/h/a/k": "com/rs2/model/quest/event/DropPartyEventStartTask",
    "com/rs2/model/a/h/a/l": "com/rs2/model/quest/event/ScheduledServerExitTask",
    "com/rs2/model/a/h/a/m": "com/rs2/model/quest/event/ClanWarsEventStartTask",
    "com/rs2/model/a/h/a/n": "com/rs2/model/quest/event/CreatorSupportBroadcastTask",
    "com/rs2/model/a/h/a/o": "com/rs2/model/quest/event/ClueMerchantSpawnTask",
    "com/rs2/model/a/h/a/p": "com/rs2/model/quest/event/ChristmasDropEventHook",
    "com/rs2/model/a/h/a/q": "com/rs2/model/quest/event/ChristmasDropRefreshTask",
    "com/rs2/model/a/h/b/bp": "com/rs2/model/quest/impl/TutorialQuest",
    "com/rs2/model/a/h/b/a": "com/rs2/model/quest/impl/BlackKnightsFortressQuest",
    "com/rs2/model/a/h/b/b": "com/rs2/model/quest/impl/CooksAssistantQuest",
    "com/rs2/model/a/h/b/c": "com/rs2/model/quest/impl/DemonSlayerQuest",
    "com/rs2/model/a/h/b/d": "com/rs2/model/quest/impl/DoricsQuest",
    "com/rs2/model/a/h/b/e": "com/rs2/model/quest/impl/DragonSlayerQuest",
    "com/rs2/model/a/h/b/f": "com/rs2/model/quest/impl/DragonSlayerShipHoleRepairTask",
    "com/rs2/model/a/h/b/j": "com/rs2/model/quest/impl/ErnestTheChickenQuest",
    "com/rs2/model/a/h/b/k": "com/rs2/model/quest/impl/PoisonedFishFoodPiranhaTask",
    "com/rs2/model/a/h/b/l": "com/rs2/model/quest/impl/PoisonedFishFoodPiranhaFinishTask",
    "com/rs2/model/a/h/b/m": "com/rs2/model/quest/impl/ErnestSecretDoorReturnTask",
    "com/rs2/model/a/h/b/n": "com/rs2/model/quest/impl/CompostHeapKeyFindTask",
    "com/rs2/model/a/h/b/o": "com/rs2/model/quest/impl/ErnestHumanDialogueTask",
    "com/rs2/model/a/h/b/p": "com/rs2/model/quest/impl/PouletmorphMachineStartTask",
    "com/rs2/model/a/h/b/q": "com/rs2/model/quest/impl/PouletmorphMachineTransformTask",
    "com/rs2/model/a/h/b/z": "com/rs2/model/quest/impl/GoblinDiplomacyQuest",
    "com/rs2/model/a/h/b/C": "com/rs2/model/quest/impl/ImpCatcherQuest",
    "com/rs2/model/a/h/b/bl": "com/rs2/model/quest/impl/KnightsSwordQuest",
    "com/rs2/model/a/h/b/at": "com/rs2/model/quest/impl/PiratesTreasureQuest",
    "com/rs2/model/a/h/b/au": "com/rs2/model/quest/impl/PiratesTreasureDigCompletionEvent",
    "com/rs2/model/a/h/b/av": "com/rs2/model/quest/impl/RumBananaCrateSearchTask",
    "com/rs2/model/a/h/b/aw": "com/rs2/model/quest/impl/RumBananaCratePromptTask",
    "com/rs2/model/a/h/b/ax": "com/rs2/model/quest/impl/BananaCratePromptTask",
    "com/rs2/model/a/h/b/ay": "com/rs2/model/quest/impl/InitialBananaCratePromptTask",
    "com/rs2/model/a/h/b/az": "com/rs2/model/quest/impl/HectorsChestMessageTask",
    "com/rs2/model/a/h/b/aB": "com/rs2/model/quest/impl/PrinceAliRescueQuest",
    "com/rs2/model/a/h/b/bm": "com/rs2/model/quest/impl/RestlessGhostQuest",
    "com/rs2/model/a/h/b/bn": "com/rs2/model/quest/impl/RestlessGhostCoffinGhostSpawnTask",
    "com/rs2/model/a/h/b/aC": "com/rs2/model/quest/impl/RomeoAndJulietQuest",
    "com/rs2/model/a/h/b/aD": "com/rs2/model/quest/impl/ApothecaryCadavaPotionMixTask",
    "com/rs2/model/a/h/b/aE": "com/rs2/model/quest/impl/ApothecaryCadavaPotionReadyTask",
    "com/rs2/model/a/h/b/aF": "com/rs2/model/quest/impl/RuneMysteriesQuest",
    "com/rs2/model/a/h/b/aL": "com/rs2/model/quest/impl/SheepShearerQuest",
    "com/rs2/model/a/h/b/aM": "com/rs2/model/quest/impl/ShieldOfArravQuest",
    "com/rs2/model/a/h/b/bq": "com/rs2/model/quest/impl/VampireSlayerQuest",
    "com/rs2/model/a/h/b/br": "com/rs2/model/quest/impl/DraynorManorCandlesBurnTask",
    "com/rs2/model/a/h/b/bs": "com/rs2/model/quest/impl/VampireCoffinRiseTask",
    "com/rs2/model/a/h/b/bt": "com/rs2/model/quest/impl/VampireAttackStartTask",
    "com/rs2/model/a/h/b/bx": "com/rs2/model/quest/impl/WitchsPotionQuest",
    "com/rs2/model/a/h/b/g": "com/rs2/model/quest/impl/DruidicRitualQuest",
    "com/rs2/model/a/h/b/h": "com/rs2/model/quest/impl/ElementalWorkshopQuest",
    "com/rs2/model/a/h/b/i": "com/rs2/model/quest/impl/ElementalShieldSmithingTask",
    "com/rs2/model/a/h/b/r": "com/rs2/model/quest/impl/FamilyCrestQuest",
    "com/rs2/model/a/h/b/aN": "com/rs2/model/quest/impl/FremennikTrialsQuest",
    "com/rs2/model/a/h/b/aO": "com/rs2/model/quest/impl/SwensenMazeObject4150ExitStepTask",
    "com/rs2/model/a/h/b/aP": "com/rs2/model/quest/impl/ManniDrinkingContestPlayerDrinkTask",
    "com/rs2/model/a/h/b/aQ": "com/rs2/model/quest/impl/ManniDrinkingContestResultTask",
    "com/rs2/model/a/h/b/aR": "com/rs2/model/quest/impl/SwensenMazeObject4151ExitStepTask",
    "com/rs2/model/a/h/b/aS": "com/rs2/model/quest/impl/SwensenMazeObject4152ExitStepTask",
    "com/rs2/model/a/h/b/aT": "com/rs2/model/quest/impl/SwensenMazeObject4153ExitStepTask",
    "com/rs2/model/a/h/b/aU": "com/rs2/model/quest/impl/SwensenMazeObject4154ExitStepTask",
    "com/rs2/model/a/h/b/aV": "com/rs2/model/quest/impl/SwensenMazeObject4155ExitStepTask",
    "com/rs2/model/a/h/b/aW": "com/rs2/model/quest/impl/SwensenMazeRandomObjectExitStepTask",
    "com/rs2/model/a/h/b/aX": "com/rs2/model/quest/impl/PeerTheSeerHouseGuardianTask",
    "com/rs2/model/a/h/b/aY": "com/rs2/model/quest/impl/LyrePerformanceStartTask",
    "com/rs2/model/a/h/b/aZ": "com/rs2/model/quest/impl/LyrePerformanceSecondLineTask",
    "com/rs2/model/a/h/b/ba": "com/rs2/model/quest/impl/LyrePerformanceThirdLineTask",
    "com/rs2/model/a/h/b/bb": "com/rs2/model/quest/impl/LyrePerformanceHeckleTask",
    "com/rs2/model/a/h/b/bc": "com/rs2/model/quest/impl/LyrePerformanceFinishDialogueTask",
    "com/rs2/model/a/h/b/s": "com/rs2/model/quest/impl/GertrudesCatQuest",
    "com/rs2/model/a/h/b/t": "com/rs2/model/quest/impl/FluffsInteractionHintTask",
    "com/rs2/model/a/h/b/u": "com/rs2/model/quest/impl/KittenCrateSearchTask",
    "com/rs2/model/a/h/b/v": "com/rs2/model/quest/impl/FluffsKittenReunionStartTask",
    "com/rs2/model/a/h/b/w": "com/rs2/model/quest/impl/FluffsKittenReunionFinishTask",
    "com/rs2/model/a/h/b/x": "com/rs2/model/quest/impl/GertrudeRewardFoodTask",
    "com/rs2/model/a/h/b/y": "com/rs2/model/quest/impl/GertrudeQuestCompletionTask",
    "com/rs2/model/a/h/b/bd": "com/rs2/model/quest/impl/GrandTreeQuest",
    "com/rs2/model/a/h/b/be": "com/rs2/model/quest/impl/GrandTreeGloughCaveEncounterTask",
    "com/rs2/model/a/h/b/bf": "com/rs2/model/quest/impl/GloughChestScrollFindTask",
    "com/rs2/model/a/h/b/bg": "com/rs2/model/quest/impl/GrandTreeGuardPassageReportTask",
    "com/rs2/model/a/h/b/bh": "com/rs2/model/quest/impl/GloughGuardArrestStartTask",
    "com/rs2/model/a/h/b/bi": "com/rs2/model/quest/impl/GloughDemonSpawnTask",
    "com/rs2/model/a/h/b/bj": "com/rs2/model/quest/impl/GrandTreeGuardPrisonEscortTask",
    "com/rs2/model/a/h/b/bk": "com/rs2/model/quest/impl/KingNarnodePrisonReleaseTask",
    "com/rs2/model/a/h/b/A": "com/rs2/model/quest/impl/HeroesQuest",
    "com/rs2/model/a/h/b/B": "com/rs2/model/quest/impl/HolyGrailQuest",
    "com/rs2/model/a/h/b/D": "com/rs2/model/quest/impl/JunglePotionQuest",
    "com/rs2/model/a/h/b/E": "com/rs2/model/quest/impl/JunglePotionHerbSearchEvent",
    "com/rs2/model/a/h/b/F": "com/rs2/model/quest/impl/LostCityQuest",
    "com/rs2/model/a/h/b/G": "com/rs2/model/quest/impl/LostCityZanarisEntryCompletionTask",
    "com/rs2/model/a/h/b/H": "com/rs2/model/quest/impl/MerlinsCrystalQuest",
    "com/rs2/model/a/h/b/I": "com/rs2/model/quest/impl/MerlinsCrystalThrantaxSummonTask",
    "com/rs2/model/a/h/b/J": "com/rs2/model/quest/impl/MonkeyMadnessQuest",
    "com/rs2/model/a/h/b/K": "com/rs2/model/quest/impl/ApeAtollDungeonHazardDamageTask",
    "com/rs2/model/a/h/b/L": "com/rs2/model/quest/impl/ApeAtollGuardCaptureDialogueTask",
    "com/rs2/model/a/h/b/M": "com/rs2/model/quest/impl/MonkeyMadnessChapterOneTitleTask",
    "com/rs2/model/a/h/b/N": "com/rs2/model/quest/impl/NarnodeOrdersWritingTask",
    "com/rs2/model/a/h/b/O": "com/rs2/model/quest/impl/MonkeyMadnessChapterThreeTitleTask",
    "com/rs2/model/a/h/b/P": "com/rs2/model/quest/impl/DaeroTrainingTimeSkipTask",
    "com/rs2/model/a/h/b/Q": "com/rs2/model/quest/impl/DaeroTrainingXpRewardTask",
    "com/rs2/model/a/h/b/R": "com/rs2/model/quest/impl/DaeroOrdersDecodeTask",
    "com/rs2/model/a/h/b/S": "com/rs2/model/quest/impl/DaeroBlindfoldHangarTravelTask",
    "com/rs2/model/a/h/b/T": "com/rs2/model/quest/impl/DaeroBlindfoldHangarReturnTask",
    "com/rs2/model/a/h/b/U": "com/rs2/model/quest/impl/WaydarInitialCrashIslandFlightTask",
    "com/rs2/model/a/h/b/V": "com/rs2/model/quest/impl/MonkeyAmuletSmithingTask",
    "com/rs2/model/a/h/b/W": "com/rs2/model/quest/impl/WaydarCrashIslandReturnFlightTask",
    "com/rs2/model/a/h/b/X": "com/rs2/model/quest/impl/LumdoApeAtollTravelTask",
    "com/rs2/model/a/h/b/Y": "com/rs2/model/quest/impl/LumdoCrashIslandReturnTask",
    "com/rs2/model/a/h/b/Z": "com/rs2/model/quest/impl/WaydarHangarReturnTask",
    "com/rs2/model/a/h/b/aa": "com/rs2/model/quest/impl/WaydarRepeatHangarReturnTask",
    "com/rs2/model/a/h/b/ab": "com/rs2/model/quest/impl/CaranockShipyardCutsceneStartTask",
    "com/rs2/model/a/h/b/ac": "com/rs2/model/quest/impl/MonkeyMadnessChapterTwoTitleTask",
    "com/rs2/model/a/h/b/ad": "com/rs2/model/quest/impl/AwowogeiAllianceCutsceneStartTask",
    "com/rs2/model/a/h/b/ae": "com/rs2/model/quest/impl/ZooknockFinalBattleExitTask",
    "com/rs2/model/a/h/b/af": "com/rs2/model/quest/impl/MonkeyMadnessChapterFourTitleTask",
    "com/rs2/model/a/h/b/ag": "com/rs2/model/quest/impl/ShipyardGateLockpickTask",
    "com/rs2/model/a/h/b/ah": "com/rs2/model/quest/impl/ZooknockAmuletEnchantSpellTask",
    "com/rs2/model/a/h/b/ai": "com/rs2/model/quest/impl/ZooknockGreegreeEnchantSpellTask",
    "com/rs2/model/a/h/b/aj": "com/rs2/model/quest/impl/CaranockWaydarCutsceneStartTask",
    "com/rs2/model/a/h/b/ak": "com/rs2/model/quest/impl/KrukAwowogeiEscortTask",
    "com/rs2/model/a/h/b/al": "com/rs2/model/quest/impl/ArdougneZooMonkeyRecaptureTask",
    "com/rs2/model/a/h/b/am": "com/rs2/model/quest/impl/ArdougneZooGnomeRescueTask",
    "com/rs2/model/a/h/b/an": "com/rs2/model/quest/impl/MonkeyDenturesCrateSearchTask",
    "com/rs2/model/a/h/b/ao": "com/rs2/model/quest/impl/ShipyardCrateHoleSearchTask",
    "com/rs2/model/a/h/b/ap": "com/rs2/model/quest/impl/ShipyardCrateTunnelDescentTask",
    "com/rs2/model/a/h/b/aq": "com/rs2/model/quest/impl/MonkeyAmuletMouldCrateSearchTask",
    "com/rs2/model/a/h/b/ar": "com/rs2/model/quest/impl/DaeroPuzzleCompletionTeleportTask",
    "com/rs2/model/a/h/b/as": "com/rs2/model/quest/impl/ApeAtollGuardCaptureTask",
    "com/rs2/model/a/h/b/aA": "com/rs2/model/quest/impl/PriestInPerilQuest",
    "com/rs2/model/a/h/b/aG": "com/rs2/model/quest/impl/ScorpionCatcherQuest",
    "com/rs2/model/a/h/b/aH": "com/rs2/model/quest/impl/ScorpionCatcherCageHandoffDialogueTask",
    "com/rs2/model/a/h/b/aI": "com/rs2/model/quest/impl/SeerMirrorGazeTask",
    "com/rs2/model/a/h/b/aJ": "com/rs2/model/quest/impl/SeerMirrorHairSmoothTask",
    "com/rs2/model/a/h/b/aK": "com/rs2/model/quest/impl/SeerMirrorResultDialogueTask",
    "com/rs2/model/a/h/b/bo": "com/rs2/model/quest/impl/TreeGnomeVillageQuest",
    "com/rs2/model/a/h/b/bu": "com/rs2/model/quest/impl/WitchsHouseQuest",
    "com/rs2/model/a/h/b/bv": "com/rs2/model/quest/impl/WitchsHouseGardenTrespassTask",
    "com/rs2/model/a/h/b/bw": "com/rs2/model/quest/impl/WitchsHouseGardenEjectEvent",
    "com/rs2/model/a/j/a": "com/rs2/model/skill/ItemCombinationHandler",
    "com/rs2/model/a/j/b": "com/rs2/model/skill/ItemCombinationRecipe",
    "com/rs2/model/a/j/c": "com/rs2/model/skill/SkillManager",
    "com/rs2/model/a/j/d": "com/rs2/model/skill/SkillLevelRestoreTask",
    "com/rs2/model/a/j/e": "com/rs2/model/skill/SpecialEnergyRestoreTask",
    "com/rs2/model/a/j/f": "com/rs2/model/skill/SkillActionHelper",
    "com/rs2/model/a/j/h": "com/rs2/model/skill/GatheringToolComparator",
    "com/rs2/model/a/j/i": "com/rs2/model/skill/GatheringToolDefinition",
    "com/rs2/model/a/x": "com/rs2/model/skill/guide/SkillGuideManager",
    "com/rs2/model/a/z": "com/rs2/model/skill/guide/SkillGuideCategory",
    "com/rs2/model/a/r": "com/rs2/model/skill/guide/SkillGuideEntry",
    "com/rs2/model/a/j/c/a": "com/rs2/model/skill/crafting/CraftingHandler",
    "com/rs2/model/a/j/c/b": "com/rs2/model/skill/crafting/DramenStaffCarvingTask",
    "com/rs2/model/a/j/c/c": "com/rs2/model/skill/crafting/DramenStaffRecipe",
    "com/rs2/model/a/j/c/d": "com/rs2/model/skill/crafting/JewelleryCraftingHandler",
    "com/rs2/model/a/j/c/e": "com/rs2/model/skill/crafting/JewelleryCraftingTask",
    "com/rs2/model/a/j/c/f": "com/rs2/model/skill/crafting/JewelleryDefinition",
    "com/rs2/model/a/j/c/g": "com/rs2/model/skill/crafting/GemCuttingHandler",
    "com/rs2/model/a/j/c/h": "com/rs2/model/skill/crafting/GemDefinition",
    "com/rs2/model/a/j/c/i": "com/rs2/model/skill/crafting/JewelleryCraftingData",
    "com/rs2/model/a/j/c/j": "com/rs2/model/skill/crafting/GlassblowingTask",
    "com/rs2/model/a/j/c/k": "com/rs2/model/skill/crafting/GlassblowingRecipe",
    "com/rs2/model/a/j/c/l": "com/rs2/model/skill/crafting/PotteryWheelTask",
    "com/rs2/model/a/j/c/m": "com/rs2/model/skill/crafting/PotteryOvenTask",
    "com/rs2/model/a/j/c/n": "com/rs2/model/skill/crafting/PotteryRecipe",
    "com/rs2/model/a/j/c/o": "com/rs2/model/skill/crafting/SilverCraftingTask",
    "com/rs2/model/a/j/c/p": "com/rs2/model/skill/crafting/SilverCraftingRecipe",
    "com/rs2/model/a/j/c/q": "com/rs2/model/skill/crafting/SpinningTask",
    "com/rs2/model/a/j/c/r": "com/rs2/model/skill/crafting/SpinningRecipe",
    "com/rs2/model/a/j/c/s": "com/rs2/model/skill/crafting/BattlestaffCraftingHandler",
    "com/rs2/model/a/j/c/t": "com/rs2/model/skill/crafting/BattlestaffRecipe",
    "com/rs2/model/a/j/c/u": "com/rs2/model/skill/crafting/WeavingTask",
    "com/rs2/model/a/j/c/v": "com/rs2/model/skill/crafting/WeavingRecipe",
    "com/rs2/model/a/j/c/a/a": "com/rs2/model/skill/crafting/armor/CraftedArmorAction",
    "com/rs2/model/a/j/c/a/b": "com/rs2/model/skill/crafting/armor/CraftedArmorTask",
    "com/rs2/model/a/j/c/a/c": "com/rs2/model/skill/crafting/armor/SplitbarkCraftingAction",
    "com/rs2/model/a/j/c/a/a/a": "com/rs2/model/skill/crafting/armor/BlackDragonhideCrafting",
    "com/rs2/model/a/j/c/a/a/b": "com/rs2/model/skill/crafting/armor/BlackDragonhideRecipe",
    "com/rs2/model/a/j/c/a/a/c": "com/rs2/model/skill/crafting/armor/BlueDragonhideCrafting",
    "com/rs2/model/a/j/c/a/a/d": "com/rs2/model/skill/crafting/armor/BlueDragonhideRecipe",
    "com/rs2/model/a/j/c/a/a/e": "com/rs2/model/skill/crafting/armor/GreenDragonhideCrafting",
    "com/rs2/model/a/j/c/a/a/f": "com/rs2/model/skill/crafting/armor/GreenDragonhideRecipe",
    "com/rs2/model/a/j/c/a/a/g": "com/rs2/model/skill/crafting/armor/HardLeatherCrafting",
    "com/rs2/model/a/j/c/a/a/h": "com/rs2/model/skill/crafting/armor/HardLeatherRecipe",
    "com/rs2/model/a/j/c/a/a/i": "com/rs2/model/skill/crafting/armor/LeatherCrafting",
    "com/rs2/model/a/j/c/a/a/j": "com/rs2/model/skill/crafting/armor/LeatherRecipe",
    "com/rs2/model/a/j/c/a/a/k": "com/rs2/model/skill/crafting/armor/RedDragonhideCrafting",
    "com/rs2/model/a/j/c/a/a/l": "com/rs2/model/skill/crafting/armor/RedDragonhideRecipe",
    "com/rs2/model/a/j/c/a/a/m": "com/rs2/model/skill/crafting/armor/SnakeskinAccessoryCrafting",
    "com/rs2/model/a/j/c/a/a/n": "com/rs2/model/skill/crafting/armor/SnakeskinAccessoryRecipe",
    "com/rs2/model/a/j/c/a/a/o": "com/rs2/model/skill/crafting/armor/SnakeskinArmorCrafting",
    "com/rs2/model/a/j/c/a/a/p": "com/rs2/model/skill/crafting/armor/SnakeskinArmorRecipe",
    "com/rs2/model/a/j/c/a/a/q": "com/rs2/model/skill/crafting/armor/SplitbarkCrafting",
    "com/rs2/model/a/j/c/a/a/r": "com/rs2/model/skill/crafting/armor/SplitbarkRecipe",
    "com/rs2/model/a/j/d/a": "com/rs2/model/skill/farming/AllotmentPatchManager",
    "com/rs2/model/a/j/d/b": "com/rs2/model/skill/farming/AllotmentWateringTask",
    "com/rs2/model/a/j/d/c": "com/rs2/model/skill/farming/AllotmentClearingTask",
    "com/rs2/model/a/j/d/d": "com/rs2/model/skill/farming/AllotmentPlantingTask",
    "com/rs2/model/a/j/d/e": "com/rs2/model/skill/farming/AllotmentHarvestTask",
    "com/rs2/model/a/j/d/f": "com/rs2/model/skill/farming/AllotmentCompostTask",
    "com/rs2/model/a/j/d/g": "com/rs2/model/skill/farming/AllotmentInspectTask",
    "com/rs2/model/a/j/d/h": "com/rs2/model/skill/farming/AllotmentCureTask",
    "com/rs2/model/a/j/d/i": "com/rs2/model/skill/farming/AllotmentCropDefinition",
    "com/rs2/model/a/j/d/j": "com/rs2/model/skill/farming/AllotmentPatch",
    "com/rs2/model/a/j/d/k": "com/rs2/model/skill/farming/AllotmentGrowthDefinition",
    "com/rs2/model/a/j/d/ab": "com/rs2/model/skill/farming/HerbPatchManager",
    "com/rs2/model/a/j/d/ac": "com/rs2/model/skill/farming/HerbClearingTask",
    "com/rs2/model/a/j/d/ad": "com/rs2/model/skill/farming/HerbPlantingTask",
    "com/rs2/model/a/j/d/ae": "com/rs2/model/skill/farming/HerbHarvestTask",
    "com/rs2/model/a/j/d/af": "com/rs2/model/skill/farming/HerbCompostTask",
    "com/rs2/model/a/j/d/ag": "com/rs2/model/skill/farming/HerbInspectTask",
    "com/rs2/model/a/j/d/ah": "com/rs2/model/skill/farming/HerbCureTask",
    "com/rs2/model/a/j/d/ai": "com/rs2/model/skill/farming/HerbDefinition",
    "com/rs2/model/a/j/d/aj": "com/rs2/model/skill/farming/HerbPatch",
    "com/rs2/model/a/j/d/ak": "com/rs2/model/skill/farming/HerbGrowthDefinition",
    "com/rs2/model/a/j/d/al": "com/rs2/model/skill/farming/HopsPatchManager",
    "com/rs2/model/a/j/d/am": "com/rs2/model/skill/farming/HopsWateringTask",
    "com/rs2/model/a/j/d/an": "com/rs2/model/skill/farming/HopsClearingTask",
    "com/rs2/model/a/j/d/ao": "com/rs2/model/skill/farming/HopsPlantingTask",
    "com/rs2/model/a/j/d/ap": "com/rs2/model/skill/farming/HopsHarvestTask",
    "com/rs2/model/a/j/d/aq": "com/rs2/model/skill/farming/HopsCompostTask",
    "com/rs2/model/a/j/d/ar": "com/rs2/model/skill/farming/HopsInspectTask",
    "com/rs2/model/a/j/d/as": "com/rs2/model/skill/farming/HopsCureTask",
    "com/rs2/model/a/j/d/at": "com/rs2/model/skill/farming/HopsDefinition",
    "com/rs2/model/a/j/d/au": "com/rs2/model/skill/farming/HopsPatch",
    "com/rs2/model/a/j/d/av": "com/rs2/model/skill/farming/HopsGrowthDefinition",
    "com/rs2/model/a/j/d/l": "com/rs2/model/skill/farming/BushPatchManager",
    "com/rs2/model/a/j/d/m": "com/rs2/model/skill/farming/BushClearingTask",
    "com/rs2/model/a/j/d/n": "com/rs2/model/skill/farming/BushPlantingTask",
    "com/rs2/model/a/j/d/o": "com/rs2/model/skill/farming/BushHarvestTask",
    "com/rs2/model/a/j/d/p": "com/rs2/model/skill/farming/BushCompostTask",
    "com/rs2/model/a/j/d/q": "com/rs2/model/skill/farming/BushInspectTask",
    "com/rs2/model/a/j/d/r": "com/rs2/model/skill/farming/BushCureTask",
    "com/rs2/model/a/j/d/s": "com/rs2/model/skill/farming/BushDefinition",
    "com/rs2/model/a/j/d/t": "com/rs2/model/skill/farming/BushPatch",
    "com/rs2/model/a/j/d/u": "com/rs2/model/skill/farming/BushGrowthDefinition",
    "com/rs2/model/a/j/d/E": "com/rs2/model/skill/farming/FlowerPatchManager",
    "com/rs2/model/a/j/d/F": "com/rs2/model/skill/farming/FlowerWateringTask",
    "com/rs2/model/a/j/d/G": "com/rs2/model/skill/farming/FlowerClearingTask",
    "com/rs2/model/a/j/d/H": "com/rs2/model/skill/farming/FlowerPlantingTask",
    "com/rs2/model/a/j/d/I": "com/rs2/model/skill/farming/FlowerHarvestTask",
    "com/rs2/model/a/j/d/J": "com/rs2/model/skill/farming/FlowerCompostTask",
    "com/rs2/model/a/j/d/K": "com/rs2/model/skill/farming/FlowerInspectTask",
    "com/rs2/model/a/j/d/L": "com/rs2/model/skill/farming/FlowerCureTask",
    "com/rs2/model/a/j/d/M": "com/rs2/model/skill/farming/ScarecrowPlantingTask",
    "com/rs2/model/a/j/d/N": "com/rs2/model/skill/farming/FlowerDefinition",
    "com/rs2/model/a/j/d/O": "com/rs2/model/skill/farming/FlowerPatch",
    "com/rs2/model/a/j/d/P": "com/rs2/model/skill/farming/FlowerGrowthDefinition",
    "com/rs2/model/a/j/d/aW": "com/rs2/model/skill/farming/TreePatchManager",
    "com/rs2/model/a/j/d/aX": "com/rs2/model/skill/farming/TreeClearingTask",
    "com/rs2/model/a/j/d/aY": "com/rs2/model/skill/farming/TreePlantingTask",
    "com/rs2/model/a/j/d/aZ": "com/rs2/model/skill/farming/TreeHealthCheckTask",
    "com/rs2/model/a/j/d/ba": "com/rs2/model/skill/farming/TreeStumpRegrowthTask",
    "com/rs2/model/a/j/d/bb": "com/rs2/model/skill/farming/TreeCompostTask",
    "com/rs2/model/a/j/d/bc": "com/rs2/model/skill/farming/TreeInspectTask",
    "com/rs2/model/a/j/d/bd": "com/rs2/model/skill/farming/TreePruneTask",
    "com/rs2/model/a/j/d/be": "com/rs2/model/skill/farming/FarmedTreeCuttingTask",
    "com/rs2/model/a/j/d/bf": "com/rs2/model/skill/farming/FarmedTreeGrowthDefinition",
    "com/rs2/model/a/j/d/bg": "com/rs2/model/skill/farming/FarmedTreeDefinition",
    "com/rs2/model/a/j/d/bh": "com/rs2/model/skill/farming/TreePatch",
    "com/rs2/model/a/j/d/Q": "com/rs2/model/skill/farming/FruitTreePatchManager",
    "com/rs2/model/a/j/d/R": "com/rs2/model/skill/farming/FruitTreeCuttingTask",
    "com/rs2/model/a/j/d/S": "com/rs2/model/skill/farming/FruitTreeClearingTask",
    "com/rs2/model/a/j/d/T": "com/rs2/model/skill/farming/FruitTreePlantingTask",
    "com/rs2/model/a/j/d/U": "com/rs2/model/skill/farming/FruitTreeHarvestTask",
    "com/rs2/model/a/j/d/V": "com/rs2/model/skill/farming/FruitTreeCompostTask",
    "com/rs2/model/a/j/d/W": "com/rs2/model/skill/farming/FruitTreeInspectTask",
    "com/rs2/model/a/j/d/X": "com/rs2/model/skill/farming/FruitTreePruneTask",
    "com/rs2/model/a/j/d/Y": "com/rs2/model/skill/farming/FruitTreeDefinition",
    "com/rs2/model/a/j/d/Z": "com/rs2/model/skill/farming/FruitTreePatch",
    "com/rs2/model/a/j/d/aa": "com/rs2/model/skill/farming/FruitTreeGrowthDefinition",
    "com/rs2/model/a/j/d/aA": "com/rs2/model/skill/farming/SpecialTreePatchManager",
    "com/rs2/model/a/j/d/aB": "com/rs2/model/skill/farming/SpecialTreeClearingTask",
    "com/rs2/model/a/j/d/aC": "com/rs2/model/skill/farming/SpecialTreePlantingTask",
    "com/rs2/model/a/j/d/aD": "com/rs2/model/skill/farming/SpecialTreeHarvestTask",
    "com/rs2/model/a/j/d/aE": "com/rs2/model/skill/farming/SpecialTreeCompostTask",
    "com/rs2/model/a/j/d/aF": "com/rs2/model/skill/farming/SpecialTreeInspectTask",
    "com/rs2/model/a/j/d/aG": "com/rs2/model/skill/farming/SpecialTreeCureTask",
    "com/rs2/model/a/j/d/aH": "com/rs2/model/skill/farming/SpecialTreeGrowthDefinition",
    "com/rs2/model/a/j/d/aI": "com/rs2/model/skill/farming/SpecialTreeDefinition",
    "com/rs2/model/a/j/d/aJ": "com/rs2/model/skill/farming/SpecialTreePatch",
    "com/rs2/model/a/j/d/aK": "com/rs2/model/skill/farming/SpecialCropPatchManager",
    "com/rs2/model/a/j/d/aL": "com/rs2/model/skill/farming/SpecialCropClearingTask",
    "com/rs2/model/a/j/d/aM": "com/rs2/model/skill/farming/SpecialCropPlantingTask",
    "com/rs2/model/a/j/d/aN": "com/rs2/model/skill/farming/SpecialCropHarvestTask",
    "com/rs2/model/a/j/d/aO": "com/rs2/model/skill/farming/SpecialCropCompostTask",
    "com/rs2/model/a/j/d/aP": "com/rs2/model/skill/farming/SpecialCropInspectTask",
    "com/rs2/model/a/j/d/aQ": "com/rs2/model/skill/farming/SpecialCropCureTask",
    "com/rs2/model/a/j/d/aR": "com/rs2/model/skill/farming/SpecialCropGrowthDefinition",
    "com/rs2/model/a/j/d/aS": "com/rs2/model/skill/farming/SpecialCropDefinition",
    "com/rs2/model/a/j/d/aT": "com/rs2/model/skill/farming/SpecialCropPatch",
    "com/rs2/model/a/j/d/A": "com/rs2/model/skill/farming/CompostBin",
    "com/rs2/model/a/j/d/B": "com/rs2/model/skill/farming/FarmingFarmerHandler",
    "com/rs2/model/a/j/d/C": "com/rs2/model/skill/farming/FarmingFarmerDefinition",
    "com/rs2/model/a/j/d/az": "com/rs2/model/skill/farming/SaplingDefinition",
    "com/rs2/model/a/j/d/v": "com/rs2/model/skill/farming/CompostBinManager",
    "com/rs2/model/a/j/d/w": "com/rs2/model/skill/farming/CompostBinCloseTask",
    "com/rs2/model/a/j/d/x": "com/rs2/model/skill/farming/CompostBinOpenTask",
    "com/rs2/model/a/j/d/y": "com/rs2/model/skill/farming/CompostBinFillTask",
    "com/rs2/model/a/j/d/z": "com/rs2/model/skill/farming/CompostBinEmptyTask",
    "com/rs2/model/a/j/d/D": "com/rs2/model/skill/farming/FarmingPatchUtils",
    "com/rs2/model/a/j/d/aU": "com/rs2/model/skill/farming/FarmingToolStore",
    "com/rs2/model/a/j/d/aV": "com/rs2/model/skill/farming/FarmingToolDefinition",
    "com/rs2/model/a/j/d/aw": "com/rs2/model/skill/farming/MithrilSeedFlowerHandler",
    "com/rs2/model/a/j/d/ax": "com/rs2/model/skill/farming/CropStorageDefinition",
    "com/rs2/model/a/j/d/ay": "com/rs2/model/skill/farming/PlantPotHandler",
    "com/rs2/model/a/j/k/a": "com/rs2/model/skill/prayer/BoneBuryingHandler",
    "com/rs2/model/a/j/k/b": "com/rs2/model/skill/prayer/BoneDefinition",
    "com/rs2/model/a/j/k/c": "com/rs2/model/skill/prayer/PrayerManager",
    "com/rs2/model/a/j/k/d": "com/rs2/model/skill/prayer/RapidRestoreTask",
    "com/rs2/model/a/j/k/e": "com/rs2/model/skill/prayer/RetributionPrayerTask",
    "com/rs2/model/a/j/l/a": "com/rs2/model/skill/runecrafting/CombinationRuneDefinition",
    "com/rs2/model/a/j/l/b": "com/rs2/model/skill/runecrafting/EssencePouchDefinition",
    "com/rs2/model/a/j/l/c": "com/rs2/model/skill/runecrafting/RunecraftingObjectHandler",
    "com/rs2/model/a/j/l/d": "com/rs2/model/skill/runecrafting/MysteriousRuinsTeleportTask",
    "com/rs2/model/a/j/l/e": "com/rs2/model/skill/runecrafting/RunecraftingAltarDefinition",
    "com/rs2/model/a/j/l/f": "com/rs2/model/skill/runecrafting/RunecraftingHandler",
    "com/rs2/model/a/j/l/g": "com/rs2/model/skill/runecrafting/RuneDefinition",
    "com/rs2/model/a/j/h/a": "com/rs2/model/skill/herblore/CleanHerbDefinition",
    "com/rs2/model/a/j/h/b": "com/rs2/model/skill/herblore/PestleAndMortarHandler",
    "com/rs2/model/a/j/h/c": "com/rs2/bot/BotRoute",
    "com/rs2/model/a/j/h/d": "com/rs2/model/skill/herblore/WeaponPoisonTask",
    "com/rs2/model/a/j/h/e": "com/rs2/model/skill/herblore/PoisonedWeaponDefinition",
    "com/rs2/model/a/j/h/f": "com/rs2/model/skill/herblore/HerbloreHandler",
    "com/rs2/model/a/j/h/g": "com/rs2/model/skill/herblore/UnfinishedPotionTask",
    "com/rs2/model/a/j/h/h": "com/rs2/model/skill/herblore/FinishedPotionTask",
    "com/rs2/model/a/j/a/a": "com/rs2/model/skill/agility/AgilityObstacleHandler",
    "com/rs2/model/a/j/a/b": "com/rs2/model/skill/agility/AgilityShortcutStartTask",
    "com/rs2/model/a/j/a/c": "com/rs2/model/skill/agility/AgilityMovementStepTask",
    "com/rs2/model/a/j/a/d": "com/rs2/model/skill/agility/AgilityMovementFinishTask",
    "com/rs2/model/a/j/a/e": "com/rs2/model/skill/agility/AgilityQueuedMovementTask",
    "com/rs2/model/a/j/a/f": "com/rs2/model/skill/agility/AgilityQueuedMovementStepTask",
    "com/rs2/model/a/j/a/g": "com/rs2/model/skill/agility/AgilityQueuedMovementFinishTask",
    "com/rs2/model/a/j/a/h": "com/rs2/model/skill/agility/AgilityObstacleCompletionTask",
    "com/rs2/model/a/j/a/i": "com/rs2/model/skill/agility/AgilityPositionOffsetTask",
    "com/rs2/model/a/j/j/a": "com/rs2/model/EntityTargetMovement",
    "com/rs2/model/a/j/j/b": "com/rs2/model/skill/mining/RuneEssenceMiningTask",
    "com/rs2/model/a/j/j/c": "com/rs2/model/skill/mining/MiningManager",
    "com/rs2/model/a/j/j/d": "com/rs2/model/skill/mining/MiningTask",
    "com/rs2/model/a/j/j/e": "com/rs2/model/skill/mining/ProspectingTask",
    "com/rs2/model/a/j/j/f": "com/rs2/model/skill/mining/MineableRockDefinition",
    "com/rs2/model/a/j/o/a": "com/rs2/model/combat/CombatAction",
    "com/rs2/model/a/j/o/b": "com/rs2/model/skill/thieving/PickpocketTask",
    "com/rs2/model/a/j/o/c": "com/rs2/model/skill/thieving/PickpocketDefinition",
    "com/rs2/model/a/j/o/d": "com/rs2/model/skill/thieving/ThievingObjectHandler",
    "com/rs2/model/a/j/o/e": "com/rs2/model/skill/thieving/LockpickTask",
    "com/rs2/model/a/j/o/f": "com/rs2/model/skill/thieving/TrapDisarmTask",
    "com/rs2/model/a/j/o/g": "com/rs2/model/skill/thieving/StallThievingHandler",
    "com/rs2/model/a/j/o/h": "com/rs2/model/skill/thieving/StallThievingTask",
    "com/rs2/model/a/j/o/i": "com/rs2/model/skill/thieving/StallDefinition",
    "com/rs2/model/a/j/g/a": "com/rs2/model/animation/GraphicEffect",
    "com/rs2/model/a/j/g/b": "com/rs2/model/skill/fletching/AmmunitionFletchingTask",
    "com/rs2/model/a/j/g/c": "com/rs2/model/skill/fletching/AmmunitionFletchingDefinition",
    "com/rs2/model/a/j/g/d": "com/rs2/model/skill/fletching/GemBoltTipDefinition",
    "com/rs2/model/a/j/g/e": "com/rs2/model/player/PlayerGroup",
    "com/rs2/model/a/j/g/f": "com/rs2/model/skill/fletching/BowStringingTask",
    "com/rs2/model/a/j/g/g": "com/rs2/model/skill/fletching/BowStringingDefinition",
    "com/rs2/model/a/j/g/a/a": "com/rs2/model/skill/fletching/logs/LogFletchingAction",
    "com/rs2/model/a/j/g/a/b": "com/rs2/model/skill/fletching/logs/LogFletchingTask",
    "com/rs2/model/a/j/g/a/a/a": "com/rs2/model/skill/fletching/logs/AcheyLogFletchingAction",
    "com/rs2/model/a/j/g/a/a/b": "com/rs2/model/skill/fletching/logs/AcheyLogFletchingRecipe",
    "com/rs2/model/a/j/g/a/a/c": "com/rs2/model/skill/fletching/logs/NormalLogFletchingAction",
    "com/rs2/model/a/j/g/a/a/d": "com/rs2/model/skill/fletching/logs/NormalLogFletchingRecipe",
    "com/rs2/model/a/j/g/a/a/e": "com/rs2/model/skill/fletching/logs/MagicLogFletchingAction",
    "com/rs2/model/a/j/g/a/a/f": "com/rs2/model/skill/fletching/logs/MagicLogFletchingRecipe",
    "com/rs2/model/a/j/g/a/a/g": "com/rs2/model/skill/fletching/logs/MapleLogFletchingAction",
    "com/rs2/model/a/j/g/a/a/h": "com/rs2/model/skill/fletching/logs/MapleLogFletchingRecipe",
    "com/rs2/model/a/j/g/a/a/i": "com/rs2/model/skill/fletching/logs/OakLogFletchingAction",
    "com/rs2/model/a/j/g/a/a/j": "com/rs2/model/skill/fletching/logs/OakLogFletchingRecipe",
    "com/rs2/model/a/j/g/a/a/k": "com/rs2/model/skill/fletching/logs/WillowLogFletchingAction",
    "com/rs2/model/a/j/g/a/a/l": "com/rs2/model/skill/fletching/logs/WillowLogFletchingRecipe",
    "com/rs2/model/a/j/g/a/a/m": "com/rs2/model/skill/fletching/logs/YewLogFletchingAction",
    "com/rs2/model/a/j/g/a/a/n": "com/rs2/model/skill/fletching/logs/YewLogFletchingRecipe",
    "com/rs2/model/a/j/i/a": "com/rs2/model/skill/magic/MagicSpellAction",
    "com/rs2/model/a/j/i/b": "com/rs2/model/skill/magic/MagicMembersAccountRequirement",
    "com/rs2/model/a/j/i/c": "com/rs2/model/skill/magic/TelekineticGrabSpellAction",
    "com/rs2/model/a/j/i/d": "com/rs2/model/skill/magic/NecromancyReanimateTask",
    "com/rs2/model/a/j/i/e": "com/rs2/model/skill/magic/RuneRequirement",
    "com/rs2/model/a/j/i/f": "com/rs2/model/skill/magic/MagicLevelRequirement",
    "com/rs2/model/a/j/i/g": "com/rs2/model/skill/magic/DelayedSpellImpactTask",
    "com/rs2/model/a/j/i/h": "com/rs2/model/skill/magic/ItemSpellAction",
    "com/rs2/model/a/j/i/i": "com/rs2/model/skill/magic/ObjectSpellAction",
    "com/rs2/model/a/j/i/j": "com/rs2/model/skill/magic/TeleotherSpellAction",
    "com/rs2/model/a/j/i/k": "com/rs2/model/skill/magic/SelfCastSpellAction",
    "com/rs2/model/a/j/i/l": "com/rs2/model/skill/magic/TelekineticGrabTask",
    "com/rs2/model/a/j/i/m": "com/rs2/model/skill/magic/TeleotherDestination",
    "com/rs2/model/a/j/i/n": "com/rs2/model/EntityUpdateState",
    "com/rs2/model/e/b/a": "com/rs2/model/update/EntityUpdateOverrideDefinition",
    "com/rs2/model/e/b/b": "com/rs2/model/update/EntityUpdateOverrideType",
    "com/rs2/model/a/j/i/o": "com/rs2/model/skill/magic/OrbChargeTask",
    "com/rs2/model/a/j/i/p": "com/rs2/model/skill/magic/SpellDefinition",
    "com/rs2/model/a/j/i/q": "com/rs2/model/skill/magic/Spellbook",
    "com/rs2/model/a/j/i/r": "com/rs2/model/skill/magic/TeleportManager",
    "com/rs2/model/a/j/i/s": "com/rs2/model/skill/magic/ScriptedTeleportTask",
    "com/rs2/model/a/j/i/t": "com/rs2/model/skill/magic/StandardTeleportTask",
    "com/rs2/model/a/j/i/u": "com/rs2/model/skill/magic/MagicTeleportTask",
    "com/rs2/model/a/j/i/v": "com/rs2/model/skill/magic/DelayedTeleportTask",
    "com/rs2/model/a/j/i/w": "com/rs2/model/skill/magic/AbyssTeleportTask",
    "com/rs2/model/a/j/b/a": "com/rs2/model/skill/cooking/DairyChurnHandler",
    "com/rs2/model/a/j/b/b": "com/rs2/model/skill/cooking/CookingManager",
    "com/rs2/model/a/j/b/c": "com/rs2/model/skill/cooking/CookingTask",
    "com/rs2/model/a/j/b/d": "com/rs2/model/skill/cooking/CookableFoodDefinition",
    "com/rs2/model/a/j/b/e": "com/rs2/model/skill/cooking/DairyChurnTask",
    "com/rs2/model/a/j/b/f": "com/rs2/model/skill/cooking/DairyChurnRecipe",
    "com/rs2/model/a/j/b/g": "com/rs2/model/skill/cooking/FoodPreparationRecipe",
    "com/rs2/model/a/j/b/h": "com/rs2/model/skill/cooking/PieRecipe",
    "com/rs2/model/a/j/b/i": "com/rs2/model/skill/cooking/MultiIngredientFoodRecipe",
    "com/rs2/model/a/j/b/j": "com/rs2/model/skill/cooking/WineFermentationHandler",
    "com/rs2/model/a/j/n/a": "com/rs2/model/skill/smithing/DragonfireShieldSmithing",
    "com/rs2/model/a/j/n/b": "com/rs2/model/skill/smithing/DragonSquareShieldSmithing",
    "com/rs2/model/a/j/n/c": "com/rs2/model/skill/smithing/SmeltingHandler",
    "com/rs2/model/a/j/n/d": "com/rs2/model/skill/smithing/SmeltingTask",
    "com/rs2/model/a/j/n/e": "com/rs2/model/skill/smithing/SmithingHandler",
    "com/rs2/model/a/j/n/f": "com/rs2/model/skill/smithing/SmithingTask",
    "com/rs2/model/a/j/n/g": "com/rs2/model/skill/smithing/SmithingBarDefinition",
    "com/rs2/model/a/j/n/h": "com/rs2/model/skill/smithing/BronzeBarDefinition",
    "com/rs2/model/a/j/n/i": "com/rs2/model/skill/smithing/IronBarDefinition",
    "com/rs2/model/a/j/n/j": "com/rs2/model/skill/smithing/SteelBarDefinition",
    "com/rs2/model/a/j/n/k": "com/rs2/model/skill/smithing/MithrilBarDefinition",
    "com/rs2/model/a/j/n/l": "com/rs2/model/skill/smithing/AdamantBarDefinition",
    "com/rs2/model/a/j/n/m": "com/rs2/model/skill/smithing/RuniteBarDefinition",
    "com/rs2/model/a/j/n/n": "com/rs2/model/skill/smithing/SmithableItemDefinition",
    "com/rs2/model/a/j/m/a": "com/rs2/model/skill/slayer/SlayerManager",
    "com/rs2/model/a/j/m/b": "com/rs2/model/skill/slayer/ZygomiteSpawnTask",
    "com/rs2/model/a/j/m/c": "com/rs2/model/skill/slayer/MogreSpawnTask",
    "com/rs2/model/a/j/m/d": "com/rs2/model/skill/slayer/SlayerMasterDefinition",
    "com/rs2/model/a/j/m/e": "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
    "com/rs2/model/a/j/m/f": "com/rs2/model/skill/slayer/SlayerMonsterGuide",
    "com/rs2/model/a/j/m/g": "com/rs2/model/skill/slayer/SlayerAssignmentDefinition",
    "com/rs2/model/a/j/m/h": "com/rs2/model/skill/slayer/SlayerAssignmentRequirement",
    "com/rs2/model/a/j/e/a": "com/rs2/model/skill/firemaking/FiremakingHandler",
    "com/rs2/model/a/j/e/b": "com/rs2/model/skill/firemaking/FiremakingTask",
    "com/rs2/model/a/j/e/c": "com/rs2/model/skill/firemaking/FiremakingLog",
    "com/rs2/model/a/j/f/a": "com/rs2/model/skill/fishing/FishingHandler",
    "com/rs2/model/a/j/f/b": "com/rs2/model/skill/fishing/FishingTask",
    "com/rs2/model/a/j/f/c": "com/rs2/model/skill/fishing/FishingSpotDefinition",
    "com/rs2/model/a/j/f/d": "com/rs2/model/skill/fishing/FishingWhirlpool",
    "com/rs2/model/a/j/f/e": "com/rs2/model/skill/fishing/FishingSpotManager",
    "com/rs2/model/a/j/p/a": "com/rs2/model/skill/woodcutting/WoodcuttingHandler",
    "com/rs2/model/a/j/p/b": "com/rs2/model/skill/woodcutting/WoodcuttingTask",
    "com/rs2/model/a/j/p/c": "com/rs2/model/skill/woodcutting/TreeDefinition",
    "com/rs2/model/f/a": "com/rs2/model/task/CycleEvent",
    "com/rs2/model/f/b": "com/rs2/model/task/CycleEventContainer",
    "com/rs2/model/f/c": "com/rs2/model/task/CycleEventHandler",
    "com/rs2/model/f/d": "com/rs2/model/task/TickTask",
    "com/rs2/model/f/e": "com/rs2/model/task/TaskScheduler",
    "com/rs2/model/f/f": "com/rs2/model/task/ElapsedTicks",
    "com/rs2/model/f/g": "com/rs2/model/task/DelayTimer",
    "com/rs2/model/d/d": "com/rs2/model/player/PlayerUpdateTask",
    "com/rs2/model/d/b/b": "com/rs2/model/item/ItemDefinition",
    "com/rs2/model/d/b/c": "com/rs2/model/item/ItemService",
    "com/rs2/model/d/b/d": "com/rs2/model/item/PickupItemTask",
    "com/rs2/model/d/b/e": "com/rs2/model/item/LootNextItemTask",
    "com/rs2/model/d/b/a/a": "com/rs2/model/item/action/CasketRewardHandler",
    "com/rs2/model/d/b/a/b": "com/rs2/model/item/action/GodBookHandler",
    "com/rs2/model/d/b/a/c": "com/rs2/model/item/action/GodBookRecitationEvent",
    "com/rs2/model/d/b/a/d": "com/rs2/model/item/action/CaveLightSourceDefinition",
    "com/rs2/model/d/b/a/e": "com/rs2/model/item/action/BirdNestSearchHandler",
    "com/rs2/model/d/b/a/f": "com/rs2/model/item/action/SpinningPlateHandler",
    "com/rs2/model/d/b/a/g": "com/rs2/model/item/action/SpinningPlateResultEvent",
    "com/rs2/model/d/b/a/h": "com/rs2/model/item/action/BrokenPlateDropEvent",
    "com/rs2/model/d/b/a/i": "com/rs2/model/item/action/ToyHorseyHandler",
    "com/rs2/model/d/b/a/j": "com/rs2/model/item/action/ToyHorseyUnlockEvent",
    "com/rs2/model/d/h": "com/rs2/model/objects/ObjectManager",
    "com/rs2/model/d/a/a": "com/rs2/model/item/ItemContainer",
    "com/rs2/model/d/a/b": "com/rs2/model/item/ItemContainerTab",
    "com/rs2/model/d/a/c": "com/rs2/model/item/ItemContainerType",
    "com/rs2/model/d/a/b/a": "com/rs2/model/player/InventoryManager",
    "com/rs2/model/d/a/a/a": "com/rs2/model/player/EquipmentManager",
    "com/rs2/model/d/a/a/b": "com/rs2/model/player/EquipmentContainer",
    "com/rs2/model/d/z": "com/rs2/model/player/PlayerConnectionState",
    "com/rs2/model/d/A": "com/rs2/model/player/TradeState",
    "com/rs2/model/d/B": "com/rs2/model/player/SocialManager",
    "com/rs2/model/d/C": "com/rs2/model/shop/ShopManager",
    "com/rs2/model/d/D": "com/rs2/model/shop/ShopRestockTask",
    "com/rs2/model/d/E": "com/rs2/model/shop/ShopDefinition",
    "com/rs2/model/d/F": "com/rs2/model/shop/ShopCurrency",
    "com/rs2/model/d/G": "com/rs2/model/interaction/InteractionDispatcher",
    "com/rs2/model/d/H": "com/rs2/model/interaction/FirstObjectActionTask",
    "com/rs2/model/d/I": "com/rs2/model/interaction/SearchHayTask",
    "com/rs2/model/d/J": "com/rs2/model/interaction/SearchCrateTask",
    "com/rs2/model/d/K": "com/rs2/model/interaction/SearchBookcaseTask",
    "com/rs2/model/d/L": "com/rs2/model/interaction/SarcophagusSneakTask",
    "com/rs2/model/d/M": "com/rs2/model/interaction/ItemOnObjectTask",
    "com/rs2/model/d/N": "com/rs2/model/interaction/ItemOnNpcTask",
    "com/rs2/model/d/O": "com/rs2/model/interaction/SecondObjectActionTask",
    "com/rs2/model/d/P": "com/rs2/model/interaction/ThirdObjectActionTask",
    "com/rs2/model/d/Q": "com/rs2/model/interaction/FourthObjectActionTask",
    "com/rs2/model/d/R": "com/rs2/model/interaction/FirstNpcActionTask",
    "com/rs2/model/d/S": "com/rs2/model/interaction/SecondNpcActionTask",
    "com/rs2/model/d/T": "com/rs2/model/interaction/ThirdNpcActionTask",
    "com/rs2/model/d/U": "com/rs2/model/interaction/FourthNpcActionTask",
    "com/rs2/model/d/V": "com/rs2/model/interaction/SpellOnObjectTask",
    "com/rs2/model/d/W": "com/rs2/model/interaction/InteractionType",
    "com/rs2/model/a/a": "com/rs2/model/bankpin/BankPinManager",
    "com/rs2/model/a/b": "com/rs2/model/bankpin/BankPinProtectedAction",
    "com/rs2/model/a/c": "com/rs2/model/bankpin/BankPinEntryMode",
    "com/rs2/model/a/b/a": "com/rs2/model/item/consumable/FoodHandler",
    "com/rs2/model/a/b/b": "com/rs2/model/item/consumable/FoodHealMessageEvent",
    "com/rs2/model/a/b/c": "com/rs2/model/item/consumable/FoodDefinition",
    "com/rs2/model/a/b/d": "com/rs2/model/item/consumable/PotionHandler",
    "com/rs2/model/a/b/e": "com/rs2/model/item/consumable/PotionDefinition",
    "com/rs2/model/a/b/f": "com/rs2/model/item/consumable/PotionEffectMode",
    "com/rs2/model/a/c/a": "com/rs2/model/cutscene/Cutscene",
    "com/rs2/model/a/c/b": "com/rs2/model/cutscene/CutsceneSceneSetupStep",
    "com/rs2/model/a/c/c": "com/rs2/model/cutscene/CutsceneDialogueStartStep",
    "com/rs2/model/a/c/d": "com/rs2/model/cutscene/CutsceneStepTask",
    "com/rs2/model/a/c/e": "com/rs2/model/cutscene/CutsceneEndTask",
    "com/rs2/model/a/c/f": "com/rs2/model/cutscene/CutsceneStep",
    "com/rs2/model/a/c/a/a": "com/rs2/model/cutscene/restlessghost/RestlessGhostCutscene",
    "com/rs2/model/a/c/a/b": "com/rs2/model/cutscene/restlessghost/RestlessGhostTextStep",
    "com/rs2/model/a/c/a/c": "com/rs2/model/cutscene/restlessghost/RestlessGhostAnimationStep",
    "com/rs2/model/a/c/a/d": "com/rs2/model/cutscene/restlessghost/RestlessGhostReleaseStep",
    "com/rs2/model/a/c/a/e": "com/rs2/model/cutscene/restlessghost/RestlessGhostCompletionStep",
    "com/rs2/model/a/d": "com/rs2/model/item/action/BarrowsRepairHandler",
    "com/rs2/model/a/d/a": "com/rs2/model/dialogue/DialogueManager",
    "com/rs2/model/a/d/b": "com/rs2/model/dialogue/TenthSquadSigilTeleportTask",
    "com/rs2/model/a/e/a": "com/rs2/model/gameplay/abyss/AbyssManager",
    "com/rs2/model/a/e/b": "com/rs2/model/gameplay/abyss/AbyssObstacleEvent",
    "com/rs2/model/a/e/c": "com/rs2/model/gameplay/abyss/AbyssDelayedMoveEvent",
    "com/rs2/model/a/i/a": "com/rs2/model/randomevent/RandomEventTeleportLocations",
    "com/rs2/model/a/i/c": "com/rs2/model/randomevent/RandomEventRollEvent",
    "com/rs2/model/a/i/d": "com/rs2/model/randomevent/SkillRandomEventNpc",
    "com/rs2/model/a/i/e": "com/rs2/model/randomevent/RandomEventManager",
    "com/rs2/model/a/i/f": "com/rs2/model/randomevent/RandomEventNpcReminderEvent",
    "com/rs2/model/a/i/g": "com/rs2/model/randomevent/RandomEventNpcDefinition",
    "com/rs2/model/a/i/a/a/a": "com/rs2/model/randomevent/sandwichlady/SandwichLadyRewardSet",
    "com/rs2/model/a/i/a/a/b": "com/rs2/model/randomevent/sandwichlady/SandwichLadyManager",
    "com/rs2/model/a/i/a/a/c": "com/rs2/model/randomevent/sandwichlady/SandwichLadyCleanupEvent",
    "com/rs2/model/a/i/a/a/d": "com/rs2/model/randomevent/sandwichlady/SandwichLadyFoodOffer",
    "com/rs2/model/a/l": "com/rs2/model/item/action/DyeMixingHandler",
    "com/rs2/model/a/n": "com/rs2/model/item/action/MysticStaffEnchantment",
    "com/rs2/model/a/m": "com/rs2/model/player/EmoteManager",
    "com/rs2/model/a/w": "com/rs2/model/player/PetManager",
    "com/rs2/model/a/y": "com/rs2/model/gameplay/SmokeDungeonDamageTask",
    "com/rs2/model/d/c": "com/rs2/model/player/BotBankContinuationTask",
    "com/rs2/model/d/g": "com/rs2/model/interaction/MiningShortcutTask",
    "com/rs2/model/d/j": "com/rs2/model/player/BotLumbridgeResetTask",
    "com/rs2/model/d/k": "com/rs2/model/player/RetryMissingObjectSearchTask",
    "com/rs2/model/d/l": "com/rs2/model/player/DelayedPositionMoveTask",
    "com/rs2/model/d/m": "com/rs2/model/player/BarrowsChestDamageTask",
    "com/rs2/model/d/n": "com/rs2/model/player/PostTeleportBotContinuationTask",
    "com/rs2/model/d/o": "com/rs2/model/player/DeathItemValueComparator",
    "com/rs2/model/d/p": "com/rs2/model/player/ProtectedItemValueComparator",
    "com/rs2/model/d/q": "com/rs2/model/player/DelayedBotTradeRequestTask",
    "com/rs2/model/d/r": "com/rs2/model/player/DelayedBotLevelReplyTask",
    "com/rs2/model/d/s": "com/rs2/model/player/DropGodCapeTask",
    "com/rs2/model/d/t": "com/rs2/model/player/PostLoginSyncTask",
    "com/rs2/model/d/u": "com/rs2/model/player/HiscoreEntryComparator",
    "com/rs2/model/d/v": "com/rs2/model/player/StartBotCommandTask",
    "com/rs2/model/d/w": "com/rs2/model/player/RetryMissingNpcSearchTask",
    "com/rs2/model/d/x": "com/rs2/model/player/RetryUnreachableObjectTask",
    "com/rs2/model/d/e": "com/rs2/model/player/GrandExchangeManager",
    "com/rs2/model/a/f/a": "com/rs2/model/player/CharacterFileRecord",
    "com/rs2/model/a/f/b": "com/rs2/model/player/CharacterFileBankTab",
    "com/rs2/model/a/o": "com/rs2/model/grandexchange/GrandExchangeOffer",
    "com/rs2/model/a/p": "com/rs2/model/grandexchange/GrandExchangePriceSample",
    "com/rs2/model/a/q": "com/rs2/model/grandexchange/GrandExchangePriceSampleTimestampComparator",
    "com/rs2/c/a": "com/rs2/net/packet/PacketSender",
    "com/rs2/c/b": "com/rs2/net/packet/DelayedUnlockEvent",
    "com/rs2/c/c": "com/rs2/net/packet/AgilityMovementCompletionEvent",
    "com/rs2/c/d": "com/rs2/net/packet/DelayedAnimationEvent",
    "com/rs2/c/e": "com/rs2/net/packet/QueuedPositionUnlockEvent",
    "com/rs2/c/f": "com/rs2/net/packet/RelativePositionUnlockEvent",
    "com/rs2/c/g": "com/rs2/net/packet/YAxisPositionUnlockEvent",
    "com/rs2/c/h": "com/rs2/net/DedicatedReactor",
    "com/rs2/c/i": "com/rs2/net/IsaacCipher",
    "com/rs2/c/j": "com/rs2/net/LoginProtocol",
    "com/rs2/c/k": "com/rs2/net/packet/PacketBuffer",
    "com/rs2/c/l": "com/rs2/net/packet/AccessMode",
    "com/rs2/c/m": "com/rs2/net/packet/ByteOrder",
    "com/rs2/c/n": "com/rs2/net/packet/PacketReader",
    "com/rs2/c/o": "com/rs2/net/packet/PacketWriter",
    "com/rs2/c/p": "com/rs2/net/packet/ByteTransform",
    "com/rs2/c/a/a": "com/rs2/net/packet/IncomingPacket",
    "com/rs2/c/a/b": "com/rs2/net/packet/PacketDispatcher",
    "com/rs2/c/a/c": "com/rs2/net/packet/PacketHandler",
    "com/rs2/c/a/a/A": "com/rs2/net/packet/handler/FollowPlayerTask",
    "com/rs2/c/a/a/D": "com/rs2/net/packet/handler/MovementPacketHandler",
    "com/rs2/c/a/a/u": "com/rs2/net/packet/handler/ObjectInteractionPacketHandler",
    "com/rs2/c/a/a/o": "com/rs2/net/packet/handler/ItemActionPacketHandler",
    "com/rs2/c/a/a/s": "com/rs2/net/packet/handler/RegionLoadPacketHandler",
    "com/rs2/c/a/a/a": "com/rs2/net/packet/handler/AppearancePacketHandler",
    "com/rs2/c/a/a/j": "com/rs2/net/packet/handler/CommandPacketHandler",
    "com/rs2/c/a/a/n": "com/rs2/net/packet/handler/IdlePacketHandler",
    "com/rs2/c/a/a/B": "com/rs2/net/packet/handler/SocialPacketHandler",
    "com/rs2/c/a/a/b": "com/rs2/net/packet/handler/ButtonClickPacketHandler",
    "com/rs2/c/a/a/c": "com/rs2/net/packet/handler/CameraPacketHandler",
    "com/rs2/c/a/a/h": "com/rs2/net/packet/handler/SkillMenuPacketHandler",
    "com/rs2/c/a/a/e": "com/rs2/net/packet/handler/PublicChatPacketHandler",
    "com/rs2/c/a/a/v": "com/rs2/net/packet/handler/PlayerInteractionPacketHandler",
    "com/rs2/c/a/a/i": "com/rs2/net/packet/handler/CloseInterfacePacketHandler",
    "com/rs2/c/a/a/t": "com/rs2/net/packet/handler/NpcInteractionPacketHandler",
    "com/rs2/c/a/a/C": "com/rs2/net/packet/handler/ReportAbusePacketHandler",
    "com/rs2/c/a/a/f": "com/rs2/net/packet/handler/ChatSettingsPacketHandler",
    "com/rs2/c/a/a/m": "com/rs2/net/packet/handler/ItemSpawnPacketHandler",
    "com/rs2/c/a/a/k": "com/rs2/net/packet/handler/NoOpPacketHandler",
    "com/rs2/c/a/a/d": "com/rs2/net/packet/handler/InterfaceInputPacketHandler",
    "com/rs2/c/a/a/w": "com/rs2/net/packet/handler/ItemOnPlayerTask",
    "com/rs2/c/a/a/l": "com/rs2/net/packet/handler/QuestJournalPacketHandler",
    "com/rs2/c/a/a/x": "com/rs2/net/packet/handler/TradeRequestTask",
    "com/rs2/c/a/a/y": "com/rs2/net/packet/handler/DeferredTradeRequestTask",
    "com/rs2/c/a/a/z": "com/rs2/net/packet/handler/DuelRequestTask",
    "com/rs2/c/a/a/g": "com/rs2/net/packet/handler/IgnoredBytePacketHandler",
    "com/rs2/c/a/a/p": "com/rs2/net/packet/handler/TinderboxOnGroundItemTask",
    "com/rs2/c/a/a/q": "com/rs2/net/packet/handler/GroundItemFiremakingTask",
    "com/rs2/c/a/a/r": "com/rs2/net/packet/handler/DigSearchTask",
}

PREFIX_CLASS_MAP = {
    "com/rs2/a/d/": "com/rs2/bot/tasks/",
    "com/rs2/a/c/": "com/rs2/bot/shop/",
    "com/rs2/c/a/a/": "com/rs2/net/packet/handler/",
    "com/rs2/model/a/h/a/": "com/rs2/model/quest/event/",
    "com/rs2/model/a/h/b/": "com/rs2/model/quest/impl/",
    "com/rs2/model/a/j/": "com/rs2/model/skill/",
    "com/rs2/model/a/j/a/": "com/rs2/model/skill/agility/",
    "com/rs2/model/a/j/b/": "com/rs2/model/skill/cooking/",
    "com/rs2/model/a/j/c/a/": "com/rs2/model/skill/crafting/armor/",
    "com/rs2/model/a/j/c/a/a/": "com/rs2/model/skill/crafting/armor/",
    "com/rs2/model/a/j/c/": "com/rs2/model/skill/crafting/",
    "com/rs2/model/a/j/d/": "com/rs2/model/skill/farming/",
    "com/rs2/model/a/j/e/": "com/rs2/model/skill/firemaking/",
    "com/rs2/model/a/j/f/": "com/rs2/model/skill/fishing/",
    "com/rs2/model/a/j/g/a/a/": "com/rs2/model/skill/fletching/logs/",
    "com/rs2/model/a/j/g/a/": "com/rs2/model/skill/fletching/logs/",
    "com/rs2/model/a/j/g/": "com/rs2/model/skill/fletching/",
    "com/rs2/model/a/j/h/": "com/rs2/model/skill/herblore/",
    "com/rs2/model/a/j/i/": "com/rs2/model/skill/magic/",
    "com/rs2/model/a/j/j/": "com/rs2/model/skill/mining/",
    "com/rs2/model/a/j/o/": "com/rs2/model/skill/thieving/",
    "com/rs2/model/a/j/k/": "com/rs2/model/skill/prayer/",
    "com/rs2/model/a/j/l/": "com/rs2/model/skill/runecrafting/",
    "com/rs2/model/a/j/m/": "com/rs2/model/skill/slayer/",
    "com/rs2/model/a/j/n/": "com/rs2/model/skill/smithing/",
    "com/rs2/model/a/j/p/": "com/rs2/model/skill/woodcutting/",
    "com/rs2/model/a/k/": "com/rs2/model/clue/",
    "com/rs2/model/b/": "com/rs2/model/ground/",
    "com/rs2/model/f/": "com/rs2/model/task/",
    "com/rs2/model/d/b/": "com/rs2/model/item/",
}


METHOD_NAME_MAP = {
    ("com/rs2/CacheCoordinateTranslator", "a", "(II)Z"): "isDungeonCoordinateShiftSourceRegion",
    ("com/rs2/CacheCoordinateTranslator", "a", "()V"): "detectDungeonCoordinateShift",
    (
        "com/rs2/CacheCoordinateTranslator",
        "a",
        "(Lcom/rs2/model/player/Player;)V",
    ): "translateSavedDungeonPosition",
    ("com/rs2/CacheRevisionInfo", "a", "(I)Lcom/rs2/CacheRevisionInfo;"): "forRevision",
    ("com/rs2/ConnectionThrottle", "a", "(Ljava/lang/String;)Z"): "tryAcquireConnectionSlot",
    ("com/rs2/ConnectionThrottle", "b", "(Ljava/lang/String;)V"): "releaseConnectionSlot",
    ("com/rs2/HiscoresDatabase", "a", "()V"): "connect",
    ("com/rs2/HiscoresDatabase", "a", "(Ljava/lang/String;)Ljava/sql/ResultSet;"): "executeSql",
    ("com/rs2/HiscoresDatabase", "b", "()V"): "disconnect",
    ("com/rs2/HiscoresDatabase", "a", "(Lcom/rs2/model/player/Player;)Z"): "savePlayer",
    ("com/rs2/LanDiscoveryService", "a", "()V"): "startListener",
    ("com/rs2/LanDiscoveryService", "b", "()Ljava/net/DatagramSocket;"): "getSocket",
    ("com/rs2/ConfigFile", "a", "(Ljava/lang/String;[Ljava/lang/String;)V"): "applyConfigEntry",
    (
        "com/rs2/ConfigFile",
        "a",
        "(Ljava/io/BufferedWriter;Ljava/lang/String;)V",
    ): "writeConfigLine",
    ("com/rs2/ConfigFile", "a", "()V"): "writeDefaultConfig",
    ("com/rs2/launcher/ControlPanel", "a", "()V"): "refreshStatusDisplay",
    ("com/rs2/launcher/ConsolePrintStream", "a", "(Ljava/lang/Object;)V"): "appendLine",
    ("com/rs2/launcher/ConsolePrintStream", "b", "(Ljava/lang/Object;)V"): "appendText",
    (
        "com/rs2/launcher/ControlPanel",
        "a",
        "(Lcom/rs2/launcher/ControlPanel;)Ljavax/swing/JTabbedPane;",
    ): "getTabbedPane",
    ("com/rs2/model/player/Player", "bv", "()Lcom/rs2/model/player/InventoryManager;"): "getInventoryManager",
    ("com/rs2/model/player/Player", "bR", "()Lcom/rs2/model/player/BankRearrangeMode;"): "getBankRearrangeMode",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/player/BankRearrangeMode;)V"): "setBankRearrangeMode",
    ("com/rs2/model/player/Player", "bT", "()Lcom/rs2/model/item/ItemContainer;"): "getBankContainer",
    ("com/rs2/model/player/Player", "bY", "()Lcom/rs2/model/player/EquipmentManager;"): "getEquipmentManager",
    ("com/rs2/model/player/Player", "bZ", "()Lcom/rs2/model/skill/SkillManager;"): "getSkillManager",
    ("com/rs2/model/player/Player", "ca", "()Lcom/rs2/model/quest/QuestManager;"): "getQuestManager",
    ("com/rs2/model/player/Player", "cb", "()Lcom/rs2/model/skill/runecrafting/RunecraftingObjectHandler;"): "getRunecraftingObjectHandler",
    ("com/rs2/model/player/Player", "cd", "()Lcom/rs2/net/packet/PacketSender;"): "getPacketSender",
    ("com/rs2/model/player/Player", "ce", "()Lcom/rs2/model/skill/slayer/SlayerManager;"): "getSlayerManager",
    ("com/rs2/model/player/Player", "cf", "()Lcom/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController;"): "getAlchemistPlaygroundController",
    ("com/rs2/model/player/Player", "cg", "()Lcom/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController;"): "getCreatureGraveyardController",
    ("com/rs2/model/player/Player", "ch", "()Lcom/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController;"): "getTelekineticTheatreController",
    ("com/rs2/model/player/Player", "ci", "()Lcom/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController;"): "getEnchantmentChamberController",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "h", "()Lcom/rs2/model/npc/Npc;"): "findAlchemistGuardianNpc",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "a", "()V"): "startCupboardRotation",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "c", "(I)I"): "nextCupboardObjectId",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "b", "()Z"): "isInsidePlayground",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "c", "()V"): "refreshPizazzInterface",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "d", "()V"): "refreshFreeAlchemyItemIndicator",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "a", "(III)Z"): "handleObjectAction",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "d", "(I)I"): "getAlchemyValueForItemId",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "a", "(I)Z"): "isAlchemistItem",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "b", "(I)V"): "alchemizeItem",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "e", "()V"): "rotateCupboardObjectIds",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "f", "()V"): "randomizeAlchemyItemValues",
    ("com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController", "g", "()[I"): "getAlchemyItemIds",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "a", "(I)I"): "getFruitYieldForBoneItemId",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "a", "()Z"): "isInsideGraveyard",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "b", "()V"): "handleGraveyardDeath",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "c", "()V"): "refreshPizazzInterface",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "d", "()V"): "startFallingBoneHazards",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "a", "(Z)Z"): "convertBonesToFruit",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "e", "()V"): "clearGraveyardItems",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "f", "()V"): "depositFruitChute",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "a", "(IIII)Z"): "handleObjectAction",
    ("com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController", "a", "(Lcom/rs2/model/objects/DynamicObject;)Z"): "advanceBonePileRespawnStage",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "a", "()V"): "refreshPizazzInterface",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "b", "()V"): "handleMazeItemPickupAttempt",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "c", "()V"): "spawnMazeItem",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "a", "(Lcom/rs2/model/Position;)Z"): "isMazeTargetPosition",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "d", "()V"): "completeMaze",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "e", "()V"): "startNextMaze",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "f", "()V"): "refreshCurrentMaze",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "h", "()V"): "startCurrentMaze",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "a", "(I)Ljava/lang/String;"): "getPlayerMazeSide",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "b", "(I)Z"): "handleObjectAction",
    ("com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController", "g", "()Z"): "isInsideTheatre",
    ("com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardShop", "a", "(Lcom/rs2/model/player/Player;)V"): "openRewardShop",
    ("com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardShop", "b", "(Lcom/rs2/model/player/Player;)V"): "refreshPizazzPointBalances",
    ("com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardShop", "a", "(Lcom/rs2/model/player/Player;I)V"): "sendRewardCostMessage",
    ("com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardShop", "b", "(Lcom/rs2/model/player/Player;I)V"): "buyReward",
    ("com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardShop", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardDefinition;)V"): "deductRewardCost",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "a", "()Lcom/rs2/model/npc/Npc;"): "findEnchantmentGuardianNpc",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "b", "()V"): "startBonusColorCycle",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "c", "()Z"): "isInsideChamber",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "d", "()V"): "refreshPizazzInterface",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "a", "(Ljava/lang/String;)V"): "refreshBonusColorIndicator",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "a", "(I)Z"): "handleObjectAction",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "c", "(I)I"): "getBasePizazzPointsForSpellTier",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "d", "(I)I"): "getBonusPizazzPointsForSpellTier",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "b", "(I)Z"): "isEnchantableChamberItem",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "a", "(II)V"): "enchantChamberItem",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "g", "()V"): "resetEnchantSpellTierCounts",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "e", "()[Ljava/lang/String;"): "getBonusColorNames",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "b", "(Ljava/lang/String;)V"): "setCurrentBonusColor",
    ("com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController", "f", "()Ljava/lang/String;"): "getCurrentBonusColor",
    ("com/rs2/model/player/Player", "cj", "()Lcom/rs2/model/gameplay/duel/DuelController;"): "getDuelController",
    ("com/rs2/model/player/Player", "ck", "()Lcom/rs2/model/gameplay/duel/DuelSession;"): "getDuelSession",
    ("com/rs2/model/player/Player", "cl", "()Lcom/rs2/model/gameplay/fightcave/FightCaveController;"): "getFightCaveController",
    ("com/rs2/model/player/Player", "cm", "()Lcom/rs2/model/gameplay/duel/DuelInterfaceManager;"): "getDuelInterfaceManager",
    ("com/rs2/model/player/Player", "cn", "()Lcom/rs2/model/gameplay/duel/DuelArenaLocationManager;"): "getDuelArenaLocationManager",
    ("com/rs2/model/player/Player", "cp", "()Lcom/rs2/model/skill/cooking/WineFermentationHandler;"): "getWineFermentationHandler",
    ("com/rs2/model/player/Player", "cq", "()Lcom/rs2/model/skill/cooking/CookingManager;"): "getCookingManager",
    ("com/rs2/model/player/Player", "cr", "()Lcom/rs2/model/skill/ItemCombinationHandler;"): "getItemCombinationHandler",
    ("com/rs2/model/player/Player", "cs", "()Lcom/rs2/model/skill/guide/SkillGuideManager;"): "getSkillGuideManager",
    ("com/rs2/model/player/Player", "ct", "()Lcom/rs2/model/item/consumable/FoodHandler;"): "getFoodHandler",
    ("com/rs2/model/player/Player", "cu", "()Lcom/rs2/model/item/consumable/PotionHandler;"): "getPotionHandler",
    ("com/rs2/model/player/Player", "cv", "()Lcom/rs2/model/bankpin/BankPinManager;"): "getBankPinManager",
    ("com/rs2/model/player/Player", "cw", "()Lcom/rs2/model/dialogue/DialogueManager;"): "getDialogueManager",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(I)V"): "setDialogueStep",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(I)V"): "setNextDialogueStep",
    ("com/rs2/model/dialogue/DialogueManager", "a", "()I"): "getDialogueStep",
    ("com/rs2/model/dialogue/DialogueManager", "b", "()V"): "finishDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "c", "()V"): "markDialogueInactive",
    ("com/rs2/model/dialogue/DialogueManager", "d", "()Z"): "isDialogueInactive",
    ("com/rs2/model/dialogue/DialogueManager", "c", "(I)V"): "setDialogueId",
    ("com/rs2/model/dialogue/DialogueManager", "e", "()I"): "getDialogueId",
    ("com/rs2/model/dialogue/DialogueManager", "d", "(I)V"): "setDialogueType",
    ("com/rs2/model/dialogue/DialogueManager", "f", "()I"): "getDialogueType",
    ("com/rs2/model/dialogue/DialogueManager", "f", "(I)V"): "setDialogueNpcId",
    ("com/rs2/model/dialogue/DialogueManager", "j", "()V"): "resetDialogueState",
    ("com/rs2/model/dialogue/DialogueManager", "e", "(I)Z"): "handleOptionButton",
    ("com/rs2/model/dialogue/DialogueManager", "g", "()I"): "getDialogueContextX",
    ("com/rs2/model/dialogue/DialogueManager", "h", "()I"): "getDialogueContextY",
    ("com/rs2/model/dialogue/DialogueManager", "i", "()I"): "getDialogueContextId",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Lcom/rs2/model/player/Player;I)Z"): "startDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Lcom/rs2/model/player/Player;III)Z"): "continueDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Lcom/rs2/model/player/Player;II)V"): "setTreasureTrailDialogueStep",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Lcom/rs2/model/player/Player;IIII)Z"): "continueDialogueWithNpcId",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Lcom/rs2/model/player/Player;II)Z"): "continueUtilityNpcDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(ILcom/rs2/model/player/Player;III)Z"): "startContextDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(ILcom/rs2/model/player/Player;IIIII)Z"): "continueContextDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showTwoOptionsWithTitle",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showThreeOptionsWithTitle",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showFourOptionsWithTitle",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showFiveOptionsWithTitle",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;)V"): "showTwoOptions",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showThreeOptions",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showFourOptions",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showFiveOptions",
    ("com/rs2/model/dialogue/DialogueManager", "a", "([Ljava/lang/String;)V"): "showOptions",
    ("com/rs2/model/dialogue/DialogueManager", "b", "([Ljava/lang/String;)V"): "showStatement",
    ("com/rs2/model/dialogue/DialogueManager", "a", "([Ljava/lang/String;I)V"): "showNpcDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;)V"): "showOneLineStatement",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;)V"): "showOneLineChatboxMessage",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;Ljava/lang/String;)V"): "showTwoLineStatement",
    ("com/rs2/model/dialogue/DialogueManager", "c", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showThreeLineStatement",
    ("com/rs2/model/dialogue/DialogueManager", "c", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showFourLineStatement",
    ("com/rs2/model/dialogue/DialogueManager", "c", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"): "showFiveLineStatement",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;I)V"): "showItemIdMessage",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;I)V"): "showNpcOneLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;I)V"): "showNpcTwoLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V"): "showNpcThreeLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V"): "showNpcFourLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V"): "showAlternateNpcThreeLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "c", "(Ljava/lang/String;I)V"): "showPlayerOneLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;Ljava/lang/String;I)V"): "showPlayerTwoLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "c", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V"): "showPlayerThreeLineDialogue",
    ("com/rs2/model/dialogue/DialogueManager", "b", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V"): "showPlayerFourLineDialogue",
    (
        "com/rs2/model/dialogue/DialogueManager",
        "a",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
    ): "showTutorialInstructionOverlay",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Lcom/rs2/model/item/ItemStack;)V"): "showItemMessage",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Lcom/rs2/model/item/ItemStack;Lcom/rs2/model/item/ItemStack;)V"): "showTwoItemMessage",
    ("com/rs2/model/dialogue/DialogueManager", "a", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/rs2/model/item/ItemStack;)V"): "showThreeLineItemMessage",
    ("com/rs2/model/player/Player", "cx", "()Lcom/rs2/model/skill/firemaking/FiremakingHandler;"): "getFiremakingHandler",
    ("com/rs2/model/player/Player", "cy", "()Lcom/rs2/model/skill/mining/MiningManager;"): "getMiningManager",
    ("com/rs2/model/player/Player", "cz", "()Lcom/rs2/model/skill/farming/CompostBinManager;"): "getCompostBinManager",
    ("com/rs2/model/player/Player", "cA", "()Lcom/rs2/model/skill/farming/AllotmentPatchManager;"): "getAllotmentPatchManager",
    ("com/rs2/model/player/Player", "cB", "()Lcom/rs2/model/skill/farming/FlowerPatchManager;"): "getFlowerPatchManager",
    ("com/rs2/model/player/Player", "cC", "()Lcom/rs2/model/skill/farming/HerbPatchManager;"): "getHerbPatchManager",
    ("com/rs2/model/player/Player", "cD", "()Lcom/rs2/model/skill/farming/HopsPatchManager;"): "getHopsPatchManager",
    ("com/rs2/model/player/Player", "cE", "()Lcom/rs2/model/skill/farming/BushPatchManager;"): "getBushPatchManager",
    ("com/rs2/model/player/Player", "cF", "()Lcom/rs2/model/skill/farming/PlantPotHandler;"): "getPlantPotHandler",
    ("com/rs2/model/player/Player", "cG", "()Lcom/rs2/model/skill/farming/TreePatchManager;"): "getTreePatchManager",
    ("com/rs2/model/player/Player", "cH", "()Lcom/rs2/model/skill/farming/FruitTreePatchManager;"): "getFruitTreePatchManager",
    ("com/rs2/model/player/Player", "cI", "()Lcom/rs2/model/skill/farming/SpecialTreePatchManager;"): "getSpecialTreePatchManager",
    ("com/rs2/model/player/Player", "cJ", "()Lcom/rs2/model/skill/farming/SpecialCropPatchManager;"): "getSpecialCropPatchManager",
    ("com/rs2/model/player/Player", "cK", "()Lcom/rs2/model/skill/farming/FarmingToolStore;"): "getFarmingToolStore",
    ("com/rs2/model/skill/farming/AllotmentPatchManager", "a", "(Lcom/rs2/model/skill/farming/AllotmentPatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/BushPatchManager", "a", "(Lcom/rs2/model/skill/farming/BushPatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/FlowerPatchManager", "a", "(Lcom/rs2/model/skill/farming/FlowerPatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/FruitTreePatchManager", "a", "(Lcom/rs2/model/skill/farming/FruitTreePatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/HerbPatchManager", "a", "(Lcom/rs2/model/skill/farming/HerbPatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/HopsPatchManager", "a", "(Lcom/rs2/model/skill/farming/HopsPatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/SpecialCropPatchManager", "a", "(Lcom/rs2/model/skill/farming/SpecialCropPatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/SpecialTreePatchManager", "a", "(Lcom/rs2/model/skill/farming/SpecialTreePatchManager;I)V"): "resetPatch",
    ("com/rs2/model/skill/farming/AllotmentPatchManager", "a", "(Lcom/rs2/model/player/Player;I)Z"): "emptyCropStorageContainer",
    ("com/rs2/model/skill/farming/BushPatchManager", "b", "(I)I"): "getPatchStateConfigValue",
    ("com/rs2/model/skill/farming/FruitTreePatchManager", "a", "(ILcom/rs2/model/skill/farming/FruitTreeDefinition;I)I"): "getConfigStageForPatchState",
    ("com/rs2/model/skill/farming/SpecialCropPatchManager", "a", "(ILcom/rs2/model/skill/farming/SpecialCropDefinition;I)I"): "getConfigStageForPatchState",
    ("com/rs2/model/skill/farming/SpecialTreePatchManager", "a", "(ILcom/rs2/model/skill/farming/SpecialTreeDefinition;I)I"): "getConfigStageForPatchState",
    ("com/rs2/model/skill/farming/HerbPatchManager", "a", "(I)Z"): "shouldStopGrowthCycle",
    ("com/rs2/model/skill/farming/HerbPatchManager", "b", "(I)Z"): "hasReachedFinalConfigStage",
    ("com/rs2/model/player/Player", "cL", "()Lcom/rs2/model/skill/prayer/BoneBuryingHandler;"): "getBoneBuryingHandler",
    ("com/rs2/model/player/Player", "cM", "()Lcom/rs2/model/skill/fishing/FishingHandler;"): "getFishingHandler",
    ("com/rs2/model/player/Player", "cN", "()Lcom/rs2/model/randomevent/sandwichlady/SandwichLadyManager;"): "getSandwichLadyManager",
    ("com/rs2/model/player/Player", "cO", "()Lcom/rs2/model/player/PetManager;"): "getPetManager",
    ("com/rs2/model/player/Player", "cP", "()Lcom/rs2/model/player/SocialManager;"): "getSocialManager",
    ("com/rs2/model/player/Player", "cQ", "()Lcom/rs2/model/player/TradeState;"): "getTradeState",
    ("com/rs2/model/player/Player", "cR", "()Lcom/rs2/model/item/ItemContainer;"): "getTradeOfferContainer",
    ("com/rs2/model/player/Player", "cS", "()Lcom/rs2/model/item/ItemContainer;"): "getPartyRoomContainer",
    ("com/rs2/model/player/Player", "cT", "()[I"): "getQueuedLoginItemIds",
    ("com/rs2/model/player/Player", "cU", "()[I"): "getQueuedLoginItemAmounts",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "a", "()Z"): "hasActiveDropParty",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "a", "(Lcom/rs2/model/player/Player;)Z"): "startBalloonBonanza",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "b", "(Lcom/rs2/model/player/Player;)Z"): "startNightlyDance",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "a", "(Lcom/rs2/model/player/Player;III)Z"): "handleBalloonObjectAction",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "c", "(Lcom/rs2/model/player/Player;)V"): "openPartyChest",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "b", "()V"): "refreshOpenPartyChestInterfaces",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "b", "(Lcom/rs2/model/player/Player;III)V"): "stageInventoryItemForChest",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "c", "(Lcom/rs2/model/player/Player;III)V"): "withdrawStagedChestItem",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "f", "(Lcom/rs2/model/player/Player;)V"): "refreshPartyChestInterface",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "d", "(Lcom/rs2/model/player/Player;)V"): "returnStagedChestItems",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "e", "(Lcom/rs2/model/player/Player;)V"): "depositStagedItemsToChest",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "c", "()V"): "loadPartyChest",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "d", "()V"): "savePartyChest",
    ("com/rs2/model/gameplay/CaveLightManager", "a", "(Lcom/rs2/model/player/Player;)Z"): "isInCaveInsectRegion",
    ("com/rs2/model/gameplay/CaveLightManager", "b", "(Lcom/rs2/model/player/Player;)Z"): "isInSwampGasArea",
    ("com/rs2/model/gameplay/CaveLightManager", "c", "(Lcom/rs2/model/player/Player;)Z"): "updateCaveLightHazards",
    ("com/rs2/model/gameplay/DesertHeatManager", "a", "(Lcom/rs2/model/player/Player;)Z"): "isInDesertHeatRegion",
    ("com/rs2/model/gameplay/DesertHeatManager", "b", "(Lcom/rs2/model/player/Player;)Z"): "updateDesertHeatHazard",
    ("com/rs2/model/player/Player", "eI", "()I"): "getActiveCaveLightLevel",
    ("com/rs2/model/player/Player", "eJ", "()Lcom/rs2/model/item/ItemStack;"): "findLitCaveLightSource",
    ("com/rs2/model/GameplayHelper", "f", "(I)I"): "getCaveLightLevelForItemId",
    ("com/rs2/model/GameplayHelper", "a", "(Lcom/rs2/model/player/Player;IZ)Z"): "extinguishCaveLightSource",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "a", "()I"): "getFiremakingLevelRequirement",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "b", "()I"): "getUnlitItemId",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "c", "()I"): "getLitItemId",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "d", "()I"): "getLightLevel",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "e", "()Z"): "canFlareInSwampGas",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "a", "(I)Lcom/rs2/model/item/action/CaveLightSourceDefinition;"): "forItemId",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "a", "(Lcom/rs2/model/player/Player;)I"): "getMissingPouchItemId",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)V"): "rollAbyssPouchDrop",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "a", "(Lcom/rs2/model/player/Player;III)Z"): "handleAbyssObjectAction",
    (
        "com/rs2/model/gameplay/abyss/AbyssManager",
        "a",
        "(Lcom/rs2/model/player/Player;IIIILjava/lang/String;IIIZ)V",
    ): "startObstacleAttempt",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "a", "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V"): "sendObstacleSuccessMessage",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "d", "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V"): "playObstacleAttemptAnimation",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "a", "(Lcom/rs2/model/player/Player;IIII)V"): "attemptMineRockObstacle",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "b", "(Lcom/rs2/model/player/Player;IIII)V"): "attemptChopTendrilsObstacle",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "c", "(Lcom/rs2/model/player/Player;IIII)V"): "attemptBurnBlockadeObstacle",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "d", "(Lcom/rs2/model/player/Player;IIII)V"): "attemptDistractEyesObstacle",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "e", "(Lcom/rs2/model/player/Player;IIII)V"): "attemptCrossGapObstacle",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "b", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)V"): "startAbyssMageTeleport",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "b", "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V"): "replayObstacleAttemptAnimation",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "c", "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V"): "sendObstacleFailureMessage",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "a", "(Lcom/rs2/model/player/Player;II)V"): "scheduleDelayedObstacleMove",
    ("com/rs2/model/player/Player", "dp", "()Lcom/rs2/model/skill/prayer/PrayerManager;"): "getPrayerManager",
    ("com/rs2/model/player/Player", "dq", "()Lcom/rs2/model/skill/magic/TeleportManager;"): "getTeleportManager",
    ("com/rs2/model/player/Player", "dr", "()Lcom/rs2/model/player/EmoteManager;"): "getEmoteManager",
    ("com/rs2/model/player/Player", "aP", "()Z"): "isMember",
    ("com/rs2/model/player/Player", "i", "(Z)V"): "setBankWithdrawNoteMode",
    ("com/rs2/model/player/Player", "bN", "()Z"): "isBankWithdrawNoteMode",
    ("com/rs2/model/player/PlayerUpdateTask", "a", "()I"): "getBankTabButtonId",
    ("com/rs2/model/player/Player", "H", "(I)V"): "setSelectedInterfaceItemId",
    ("com/rs2/model/player/Player", "bO", "()I"): "getSelectedInterfaceItemId",
    ("com/rs2/model/player/Player", "I", "(I)V"): "setSelectedInterfaceSlot",
    ("com/rs2/model/player/Player", "bP", "()I"): "getSelectedInterfaceSlot",
    ("com/rs2/model/player/Player", "J", "(I)V"): "setSelectedInterfaceId",
    ("com/rs2/model/player/Player", "bQ", "()I"): "getSelectedInterfaceId",
    ("com/rs2/model/player/Player", "at", "(I)V"): "setOpenInterfaceId",
    ("com/rs2/model/player/Player", "eR", "()I"): "getOpenInterfaceId",
    ("com/rs2/model/player/Player", "az", "(I)V"): "setInventoryOverlayInterfaceId",
    ("com/rs2/model/player/Player", "j", "(II)V"): "setSidebarInterfaceId",
    ("com/rs2/model/player/Player", "ax", "(I)V"): "setCombatLevel",
    ("com/rs2/model/player/Player", "eV", "()I"): "getCombatLevel",
    ("com/rs2/model/player/Player", "h", "(Ljava/lang/String;)V"): "setInterfaceAction",
    ("com/rs2/model/player/Player", "cc", "()Ljava/lang/String;"): "getInterfaceAction",
    (
        "com/rs2/model/player/Player",
        "a",
        "(Lcom/rs2/cache/InterfaceDefinition;)Z",
    ): "isInterfaceOpen",
    ("com/rs2/model/player/Player", "a", "(Ljava/nio/ByteBuffer;)V"): "writePacketBuffer",
    ("com/rs2/model/player/Player", "cY", "()Lcom/rs2/util/ElapsedTimer;"): "getPacketReadTimer",
    ("com/rs2/model/player/Player", "cZ", "()Ljava/nio/ByteBuffer;"): "getInboundBuffer",
    ("com/rs2/model/player/Player", "da", "()Ljava/nio/channels/SelectionKey;"): "getSelectionKey",
    ("com/rs2/model/player/Player", "db", "()Ljava/nio/channels/SocketChannel;"): "getSocketChannel",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/net/IsaacCipher;)V"): "setOutboundCipher",
    ("com/rs2/model/player/Player", "dc", "()Lcom/rs2/net/IsaacCipher;"): "getOutboundCipher",
    ("com/rs2/model/player/Player", "b", "(Lcom/rs2/net/IsaacCipher;)V"): "setInboundCipher",
    ("com/rs2/model/player/Player", "dd", "()Lcom/rs2/net/IsaacCipher;"): "getInboundCipher",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/player/PlayerConnectionState;)V"): "setConnectionState",
    ("com/rs2/model/player/Player", "de", "()Lcom/rs2/model/player/PlayerConnectionState;"): "getConnectionState",
    ("com/rs2/model/player/Player", "P", "(I)V"): "setCurrentPacketOpcode",
    ("com/rs2/model/player/Player", "dg", "()I"): "getCurrentPacketOpcode",
    ("com/rs2/model/player/Player", "Q", "(I)V"): "setCurrentPacketLength",
    ("com/rs2/model/player/Player", "dh", "()I"): "getCurrentPacketLength",
    ("com/rs2/model/player/Player", "d", "(Ljava/lang/String;)V"): "setUsername",
    ("com/rs2/model/player/Player", "di", "()Ljava/lang/String;"): "getUsername",
    ("com/rs2/model/player/Player", "e", "(Ljava/lang/String;)V"): "setSubmittedPassword",
    ("com/rs2/model/player/Player", "f", "(Ljava/lang/String;)V"): "setPassword",
    ("com/rs2/model/player/Player", "dj", "()Ljava/lang/String;"): "getPassword",
    ("com/rs2/model/player/Player", "a", "(Ljava/lang/String;[Ljava/lang/String;)V"): "executeCheatCommand",
    ("com/rs2/model/player/Player", "a", "(Ljava/lang/String;[Ljava/lang/String;Z)V"): "executeCheatCommand",
    ("com/rs2/model/player/Player", "a", "(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)V"): "handleCommand",
    ("com/rs2/model/player/Player", "b", "()V"): "clearStatsInterfaceText",
    ("com/rs2/model/player/Player", "a", "([Ljava/lang/String;)V"): "sendStatsInterfaceLines",
    ("com/rs2/model/player/Player", "eh", "()Ljava/nio/ByteBuffer;"): "getOutboundBuffer",
    ("com/rs2/net/packet/PacketSender", "a", "(Ljava/lang/String;)Lcom/rs2/net/packet/PacketSender;"): "sendGameMessage",
    ("com/rs2/net/packet/PacketSender", "a", "(Ljava/lang/String;I)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceText",
    ("com/rs2/net/packet/PacketSender", "a", "(ILjava/awt/Color;)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceTextColor",
    ("com/rs2/net/packet/PacketSender", "a", "(Ljava/lang/String;IZ)Lcom/rs2/net/packet/PacketSender;"): "sendPlayerOption",
    ("com/rs2/net/packet/PacketSender", "c", "(II)Lcom/rs2/net/packet/PacketSender;"): "setSidebarInterface",
    ("com/rs2/net/packet/PacketSender", "f", "()V"): "clearSidebarInterfaces",
    ("com/rs2/net/packet/PacketSender", "g", "(I)Lcom/rs2/net/packet/PacketSender;"): "showInterface",
    ("com/rs2/net/packet/PacketSender", "h", "(I)Lcom/rs2/net/packet/PacketSender;"): "showWalkableInterface",
    ("com/rs2/net/packet/PacketSender", "k", "(I)Lcom/rs2/net/packet/PacketSender;"): "showChatboxInterface",
    ("com/rs2/net/packet/PacketSender", "g", "(II)Lcom/rs2/net/packet/PacketSender;"): "showInterfaceWithInventory",
    ("com/rs2/net/packet/PacketSender", "i", "()Lcom/rs2/net/packet/PacketSender;"): "closeInterfaces",
    ("com/rs2/net/packet/PacketSender", "f", "(I)Lcom/rs2/net/packet/PacketSender;"): "closeInterface",
    ("com/rs2/net/packet/PacketSender", "m", "(II)Lcom/rs2/net/packet/PacketSender;"): "setInterfaceHiddenFlag",
    ("com/rs2/net/packet/PacketSender", "a", "(IZ)Lcom/rs2/net/packet/PacketSender;"): "setInterfaceVisible",
    ("com/rs2/net/packet/PacketSender", "j", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceAnimation",
    ("com/rs2/net/packet/PacketSender", "k", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendNpcHeadOnInterface",
    ("com/rs2/net/packet/PacketSender", "l", "(I)Lcom/rs2/net/packet/PacketSender;"): "sendPlayerHeadOnInterface",
    ("com/rs2/net/packet/PacketSender", "c", "(III)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceModel",
    ("com/rs2/net/packet/PacketSender", "d", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceItemModel",
    ("com/rs2/net/packet/PacketSender", "e", "(III)Lcom/rs2/net/packet/PacketSender;"): "sendInterfacePosition",
    ("com/rs2/net/packet/PacketSender", "a", "(III)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceOffset",
    ("com/rs2/net/packet/PacketSender", "e", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceProgress",
    ("com/rs2/net/packet/PacketSender", "a", "(IIII)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceModelRotation",
    ("com/rs2/net/packet/PacketSender", "f", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceScrollPosition",
    ("com/rs2/net/packet/PacketSender", "i", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceModelId",
    ("com/rs2/net/packet/PacketSender", "h", "()Lcom/rs2/net/packet/PacketSender;"): "sendLogout",
    ("com/rs2/net/packet/PacketSender", "c", "(I)Lcom/rs2/net/packet/PacketSender;"): "sendSystemUpdateTimer",
    ("com/rs2/net/packet/PacketSender", "b", "(I)V"): "lockPlayerForTicks",
    ("com/rs2/net/packet/PacketSender", "d", "(I)Lcom/rs2/net/packet/PacketSender;"): "sendEnterInputPrompt",
    ("com/rs2/net/packet/PacketSender", "a", "(IIZ)Lcom/rs2/net/packet/PacketSender;"): "modifySkillLevel",
    ("com/rs2/net/packet/PacketSender", "b", "(IIZ)I"): "modifySkillLevelReturningRemainder",
    ("com/rs2/net/packet/PacketSender", "a", "(IID)Lcom/rs2/net/packet/PacketSender;"): "sendSkillUpdate",
    ("com/rs2/net/packet/PacketSender", "h", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendConfig",
    ("com/rs2/net/packet/PacketSender", "d", "()Lcom/rs2/net/packet/PacketSender;"): "syncPlayerConfigs",
    ("com/rs2/net/packet/PacketSender", "m", "()V"): "refreshAutocastConfig",
    ("com/rs2/net/packet/PacketSender", "g", "()Lcom/rs2/net/packet/PacketSender;"): "sendMapRegion",
    ("com/rs2/net/packet/PacketSender", "a", "(I)Lcom/rs2/net/packet/PacketSender;"): "sendMinimapState",
    ("com/rs2/net/packet/PacketSender", "a", "(Lcom/rs2/model/music/MusicTrackDefinition;)V"): "sendMusicTrack",
    ("com/rs2/net/packet/PacketSender", "a", "(Z)Lcom/rs2/net/packet/PacketSender;"): "sendMultiwayAreaState",
    ("com/rs2/net/packet/PacketSender", "a", "(I[Lcom/rs2/model/item/ItemStack;)Lcom/rs2/net/packet/PacketSender;"): "sendItemContainer",
    ("com/rs2/net/packet/PacketSender", "b", "(III)Lcom/rs2/net/packet/PacketSender;"): "sendSingleItemContainer",
    ("com/rs2/net/packet/PacketSender", "a", "(Lcom/rs2/model/item/ItemStack;III)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceSlotItem",
    ("com/rs2/net/packet/PacketSender", "a", "(IILcom/rs2/model/item/ItemStack;)Lcom/rs2/net/packet/PacketSender;"): "sendInterfaceSlotItem",
    ("com/rs2/net/packet/PacketSender", "a", "(Lcom/rs2/model/ground/GroundItem;)Lcom/rs2/net/packet/PacketSender;"): "sendGroundItemCreate",
    ("com/rs2/net/packet/PacketSender", "b", "(Lcom/rs2/model/ground/GroundItem;)Lcom/rs2/net/packet/PacketSender;"): "sendGroundItemRemove",
    ("com/rs2/net/packet/PacketSender", "a", "(IIIIII)Lcom/rs2/net/packet/PacketSender;"): "sendObjectCreate",
    ("com/rs2/net/packet/PacketSender", "c", "(IIII)Lcom/rs2/net/packet/PacketSender;"): "sendObjectAnimation",
    ("com/rs2/net/packet/PacketSender", "a", "(Lcom/rs2/model/Position;)Lcom/rs2/net/packet/PacketSender;"): "sendLocalPosition",
    ("com/rs2/net/packet/PacketSender", "b", "(Lcom/rs2/model/Position;)Lcom/rs2/net/packet/PacketSender;"): "sendLocalScenePosition",
    ("com/rs2/net/packet/PacketSender", "a", "(ILcom/rs2/model/Position;I)Lcom/rs2/net/packet/PacketSender;"): "sendStillGraphic",
    ("com/rs2/net/packet/PacketSender", "a", "(IIIII)Lcom/rs2/net/packet/PacketSender;"): "sendStillGraphicToNearbyPlayers",
    ("com/rs2/net/packet/PacketSender", "d", "(III)Lcom/rs2/net/packet/PacketSender;"): "sendSoundEffect",
    ("com/rs2/net/packet/PacketSender", "l", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendMusicJingle",
    ("com/rs2/net/packet/PacketSender", "e", "(I)Lcom/rs2/net/packet/PacketSender;"): "selectMagicSidebarTab",
    ("com/rs2/net/packet/PacketSender", "i", "(I)Lcom/rs2/net/packet/PacketSender;"): "flashSidebarIcon",
    ("com/rs2/net/packet/PacketSender", "b", "(II)Lcom/rs2/net/packet/PacketSender;"): "sendEntityHintIcon",
    ("com/rs2/net/packet/PacketSender", "b", "(IIII)Lcom/rs2/net/packet/PacketSender;"): "sendPositionHintIcon",
    ("com/rs2/net/packet/PacketSender", "a", "(II)Lcom/rs2/net/packet/PacketSender;"): "queueAbsoluteMovementStep",
    ("com/rs2/net/packet/PacketSender", "c", "(IIZ)Lcom/rs2/net/packet/PacketSender;"): "queueRelativeMovementStep",
    ("com/rs2/net/packet/PacketSender", "a", "(IIIZ)Lcom/rs2/net/packet/PacketSender;"): "queueYAxisMovementStep",
    ("com/rs2/net/packet/PacketSender", "a", "([Lcom/rs2/model/Position;IZ)Lcom/rs2/net/packet/PacketSender;"): "queueTwoStepMovement",
    ("com/rs2/net/packet/PacketSender", "a", "(IIZIIIDZLjava/lang/String;)Lcom/rs2/net/packet/PacketSender;"): "queueAgilityMovement",
    ("com/rs2/net/packet/PacketSender", "a", "()V"): "refreshSidebarInterfaces",
    ("com/rs2/net/packet/PacketSender", "b", "()Z"): "updateMissingQuestCompletionStates",
    ("com/rs2/net/packet/PacketSender", "b", "(Ljava/lang/String;)I"): "packLastLoginAddress",
    ("com/rs2/net/packet/PacketSender", "j", "()Lcom/rs2/net/packet/PacketSender;"): "sendPlayerIndex",
    ("com/rs2/net/packet/PacketSender", "k", "()Lcom/rs2/net/packet/PacketSender;"): "sendRunEnergy",
    ("com/rs2/net/packet/PacketSender", "l", "()Lcom/rs2/net/packet/PacketSender;"): "sendWeight",
    ("com/rs2/net/packet/PacketSender", "c", "()Lcom/rs2/net/packet/PacketSender;"): "sendPostLoginState",
    ("com/rs2/net/packet/PacketSender", "n", "()V"): "refreshSpecialAttackConfig",
    ("com/rs2/net/packet/PacketSender", "m", "(I)V"): "refreshSpecialEnergyBar",
    ("com/rs2/net/packet/PacketSender", "b", "(IIIII)V"): "sendCameraPosition",
    ("com/rs2/net/packet/PacketSender", "c", "(IIIII)V"): "sendCameraLookAt",
    ("com/rs2/net/packet/PacketSender", "d", "(IIII)Lcom/rs2/net/packet/PacketSender;"): "sendCameraShake",
    ("com/rs2/net/packet/PacketSender", "o", "()V"): "resetCamera",
    ("com/rs2/net/packet/PacketSender", "e", "(IIII)V"): "openSingleDoor",
    ("com/rs2/net/packet/PacketSender", "f", "(IIII)V"): "openSouthShiftedSingleDoor",
    ("com/rs2/net/packet/PacketSender", "b", "(IIIIII)V"): "openDoubleDoorPair",
    ("com/rs2/net/packet/PacketSender", "b", "(IIIIIII)V"): "openDoubleDoorPair",
    ("com/rs2/net/packet/PacketSender", "a", "(IIIIIII)V"): "openWestShiftedDoubleDoorPair",
    ("com/rs2/net/packet/PacketSender", "c", "(IIIIIII)V"): "openNorthShiftedDoubleDoorPair",
    ("com/rs2/net/packet/PacketSender", "a", "(Lcom/rs2/net/packet/PacketSender;)Lcom/rs2/model/player/Player;"): "getPlayer",
    ("com/rs2/net/packet/handler/SkillMenuPacketHandler", "a", "(Lcom/rs2/model/player/Player;II)Z"): "handleSetLevelInput",
    ("com/rs2/model/skill/SkillManager", "l", "()D"): "getExperienceCap",
    ("com/rs2/model/skill/SkillManager", "a", "()V"): "startRestorationTasks",
    ("com/rs2/model/skill/SkillManager", "a", "(Z)V"): "setRapidHealRestoreDelay",
    ("com/rs2/model/skill/SkillManager", "b", "()V"): "restoreNonPrayerLevels",
    ("com/rs2/model/skill/SkillManager", "b", "(II)V"): "setSkillRestoreDelay",
    ("com/rs2/model/skill/SkillManager", "a", "(I)Z"): "isLevelModified",
    ("com/rs2/model/skill/SkillManager", "c", "()Z"): "isSpecialEnergyBelowMaximum",
    ("com/rs2/model/skill/SkillManager", "e", "()V"): "refreshAllSkills",
    ("com/rs2/model/skill/SkillManager", "b", "(I)V"): "refreshSkill",
    ("com/rs2/model/skill/SkillManager", "c", "(I)I"): "getBaseLevel",
    ("com/rs2/model/skill/SkillManager", "a", "(D)I"): "getLevelForExperience",
    ("com/rs2/model/skill/SkillManager", "d", "(I)I"): "getExperienceForLevel",
    ("com/rs2/model/skill/SkillManager", "f", "(I)Z"): "tryStartActionDelay",
    ("com/rs2/model/skill/SkillManager", "g", "(I)Z"): "tryStartDrinkDelay",
    ("com/rs2/model/skill/SkillManager", "f", "()I"): "getTotalLevel",
    ("com/rs2/model/skill/SkillManager", "g", "()J"): "getTotalExperience",
    ("com/rs2/model/skill/SkillManager", "a", "(ID)I"): "calculateExperienceGain",
    ("com/rs2/model/skill/SkillManager", "h", "(I)D"): "getExperienceRateForSkill",
    ("com/rs2/model/skill/SkillManager", "i", "(I)D"): "getExperienceRateForLevel",
    ("com/rs2/model/skill/SkillManager", "b", "(ID)Z"): "addExperience",
    ("com/rs2/model/skill/SkillManager", "c", "(ID)V"): "addQuestExperience",
    ("com/rs2/model/skill/SkillManager", "e", "(I)V"): "showLevelUpInterface",
    ("com/rs2/model/skill/SkillManager", "h", "()I"): "getCombatLevel",
    ("com/rs2/model/skill/SkillManager", "i", "()I"): "getMaxCombatLevel",
    ("com/rs2/model/skill/SkillManager", "j", "()[I"): "getCurrentLevels",
    ("com/rs2/model/skill/SkillManager", "k", "()[D"): "getExperience",
    ("com/rs2/model/skill/SkillManager", "a", "(II)V"): "setCurrentLevel",
    ("com/rs2/model/skill/SkillManager", "a", "(Lcom/rs2/model/skill/SkillManager;)Lcom/rs2/model/player/Player;"): "getPlayer",
    ("com/rs2/model/skill/SkillManager", "b", "(Lcom/rs2/model/skill/SkillManager;)[I"): "getCurrentLevels",
    ("com/rs2/model/player/InventoryManager", "a", "()V"): "refresh",
    ("com/rs2/model/player/InventoryManager", "a", "(I)V"): "sendToInterface",
    ("com/rs2/model/player/InventoryManager", "a", "(Lcom/rs2/model/item/ItemStack;)I"): "addItemPartial",
    ("com/rs2/model/player/InventoryManager", "d", "(Lcom/rs2/model/item/ItemStack;)Z"): "addItemUpToFreeSlots",
    ("com/rs2/model/player/InventoryManager", "b", "(Lcom/rs2/model/item/ItemStack;)V"): "addOrDropItem",
    ("com/rs2/model/player/InventoryManager", "f", "(Lcom/rs2/model/item/ItemStack;)Z"): "hasSpaceFor",
    ("com/rs2/model/player/InventoryManager", "e", "(Lcom/rs2/model/item/ItemStack;)Z"): "canAddItem",
    ("com/rs2/model/player/InventoryManager", "b", "()Lcom/rs2/model/item/ItemContainer;"): "getContainer",
    ("com/rs2/model/player/InventoryManager", "h", "(Lcom/rs2/model/item/ItemStack;)Z"): "containsItemStack",
    ("com/rs2/model/player/InventoryManager", "b", "(I)Z"): "containsItemInInventoryOrBank",
    ("com/rs2/model/player/InventoryManager", "d", "(I)Z"): "containsItem",
    ("com/rs2/model/player/InventoryManager", "b", "(II)Z"): "containsItemAmount",
    ("com/rs2/model/player/InventoryManager", "e", "(I)I"): "getItemAmount",
    ("com/rs2/model/player/InventoryManager", "a", "(Lcom/rs2/model/item/ItemStack;I)V"): "setItemInSlot",
    ("com/rs2/model/player/InventoryManager", "g", "(Lcom/rs2/model/item/ItemStack;)Z"): "removeItem",
    (
        "com/rs2/model/player/InventoryManager",
        "a",
        "(Lcom/rs2/model/item/ItemStack;Lcom/rs2/model/item/ItemStack;)V",
    ): "replaceItem",
    ("com/rs2/model/player/InventoryManager", "c", "(I)Z"): "removeItemAtSlot",
    ("com/rs2/model/player/InventoryManager", "b", "(Lcom/rs2/model/item/ItemStack;I)Z"): "removeItemFromSlot",
    ("com/rs2/model/player/InventoryManager", "a", "(II)V"): "swapSlots",
    ("com/rs2/model/player/EquipmentManager", "a", "()V"): "refresh",
    ("com/rs2/model/player/EquipmentManager", "a", "(Lcom/rs2/model/item/ItemStack;)Z"): "removeItemWithoutRefresh",
    ("com/rs2/model/player/EquipmentManager", "b", "(Lcom/rs2/model/item/ItemStack;)Z"): "removeItem",
    ("com/rs2/model/player/EquipmentManager", "a", "(I)Z"): "containsItem",
    ("com/rs2/model/player/EquipmentManager", "b", "()V"): "finishBulkEquipmentRemoval",
    ("com/rs2/model/player/EquipmentManager", "a", "(II)V"): "replaceSlotItem",
    ("com/rs2/model/player/EquipmentManager", "b", "(II)V"): "setSlotItem",
    ("com/rs2/model/player/EquipmentManager", "b", "(I)V"): "equipFromInventorySlot",
    ("com/rs2/model/player/EquipmentManager", "c", "(I)V"): "unequipSlot",
    ("com/rs2/model/player/EquipmentManager", "c", "()V"): "refreshCarriedValue",
    ("com/rs2/model/player/EquipmentManager", "c", "(II)V"): "consumeSlotItemAmount",
    ("com/rs2/model/player/EquipmentManager", "d", "()V"): "refreshWeaponInterface",
    ("com/rs2/model/player/EquipmentManager", "e", "(I)I"): "getItemIdAtSlot",
    ("com/rs2/model/player/EquipmentManager", "h", "()Lcom/rs2/model/item/ItemContainer;"): "getContainer",
    ("com/rs2/model/player/EquipmentManager", "f", "(I)Z"): "canEquipItem",
    ("com/rs2/model/player/EquipmentManager", "a", "(Lcom/rs2/model/player/Player;)V"): "refreshEquipmentBonuses",
    ("com/rs2/model/player/EquipmentManager", "i", "()V"): "refreshBarrowsSetEffects",
    ("com/rs2/model/player/EquipmentManager", "j", "()V"): "refreshWeaponAmmunitionState",
    ("com/rs2/model/player/EquipmentManager", "a", "(Lcom/rs2/model/player/EquipmentManager;)Lcom/rs2/model/player/Player;"): "getPlayer",
    ("com/rs2/model/item/ItemContainer", "a", "()I"): "getTabCount",
    ("com/rs2/model/item/ItemContainer", "d", "()I"): "getFirstFreeSlot",
    ("com/rs2/model/item/ItemContainer", "e", "()[Lcom/rs2/model/item/ItemStack;"): "getItems",
    ("com/rs2/model/item/ItemContainer", "f", "()I"): "getFreeSlots",
    ("com/rs2/model/item/ItemContainer", "b", "(I)Lcom/rs2/model/item/ItemStack;"): "getItemAt",
    ("com/rs2/model/item/ItemContainer", "a", "(II)Lcom/rs2/model/item/ItemStack;"): "getItemAtTabSlot",
    ("com/rs2/model/item/ItemContainer", "d", "(I)I"): "indexOfItem",
    ("com/rs2/model/item/ItemContainer", "b", "(II)I"): "indexOfItemInTab",
    ("com/rs2/model/item/ItemContainer", "a", "(ILcom/rs2/model/item/ItemStack;)V"): "setItem",
    ("com/rs2/model/item/ItemContainer", "a", "(ILcom/rs2/model/item/ItemStack;I)V"): "setTabItem",
    ("com/rs2/model/item/ItemContainer", "h", "()I"): "getUsedSlots",
    ("com/rs2/model/item/ItemContainer", "i", "()V"): "clear",
    ("com/rs2/model/item/ItemContainer", "j", "()[Lcom/rs2/model/item/ItemStack;"): "getRawItems",
    ("com/rs2/model/item/ItemContainer", "e", "(I)[Lcom/rs2/model/item/ItemStack;"): "getTabItems",
    ("com/rs2/model/item/ItemContainer", "d", "(II)V"): "swapSlots",
    ("com/rs2/model/item/ItemContainer", "a", "(IIII)V"): "swapTabSlots",
    ("com/rs2/model/item/ItemContainer", "g", "(I)I"): "getItemAmount",
    ("com/rs2/model/item/ItemContainer", "h", "(I)Lcom/rs2/model/item/ItemStack;"): "findItem",
    ("com/rs2/model/item/ItemContainer", "i", "(I)I"): "findTabContainingItem",
    ("com/rs2/model/item/ItemContainer", "j", "(I)Lcom/rs2/model/item/ItemContainerTab;"): "getTab",
    ("com/rs2/model/item/ItemContainer", "b", "(III)V"): "moveTabItem",
    ("com/rs2/model/item/ItemContainer", "k", "(I)Z"): "containsItem",
    ("com/rs2/model/item/ItemContainer", "c", "(Lcom/rs2/model/item/ItemStack;)Z"): "canAdd",
    ("com/rs2/model/item/ItemContainer", "a", "(Lcom/rs2/model/item/ItemStack;I)Z"): "addToTab",
    ("com/rs2/model/item/ItemContainer", "b", "(Lcom/rs2/model/item/ItemStack;I)Z"): "add",
    ("com/rs2/model/item/ItemContainer", "b", "()I"): "getTabLimit",
    ("com/rs2/model/item/ItemContainer", "a", "(I)V"): "compactTab",
    ("com/rs2/model/item/ItemContainer", "c", "()V"): "removeEmptyTabs",
    ("com/rs2/model/item/ItemContainer", "l", "(I)I"): "getFirstFreeTabSlot",
    ("com/rs2/model/item/ItemContainer", "b", "(Lcom/rs2/model/item/ItemStack;II)Z"): "addToTabInternal",
    ("com/rs2/model/item/ItemContainer", "c", "(I)Lcom/rs2/model/item/ItemStack;"): "findFlatItem",
    ("com/rs2/model/item/ItemContainer", "a", "(III)I"): "indexOfPlaceholderInTab",
    ("com/rs2/model/item/ItemContainer", "c", "(II)V"): "replaceItemId",
    ("com/rs2/model/item/ItemContainer", "m", "(I)V"): "notifySlotUpdated",
    ("com/rs2/model/item/ItemContainer", "l", "()V"): "notifyFullRefresh",
    ("com/rs2/model/item/ItemContainer", "a", "([I)V"): "notifySlotsUpdated",
    ("com/rs2/model/item/ItemContainer", "f", "(I)Z"): "hasItemAtSlot",
    ("com/rs2/model/item/ItemContainer", "a", "(Lcom/rs2/model/item/ItemStack;)I"): "remove",
    ("com/rs2/model/item/ItemContainer", "b", "(Lcom/rs2/model/item/ItemStack;)I"): "removeKeepingPlaceholder",
    ("com/rs2/model/item/ItemContainer", "c", "(Lcom/rs2/model/item/ItemStack;I)I"): "removeFromSlot",
    ("com/rs2/model/item/ItemContainer", "a", "(Lcom/rs2/model/item/ItemStack;II)I"): "removeFromTab",
    ("com/rs2/model/item/ItemContainer", "a", "(Lcom/rs2/model/item/ItemStack;IZ)I"): "removeInternal",
    ("com/rs2/model/item/ItemContainer", "e", "(II)I"): "findTabContainingPlaceholder",
    ("com/rs2/model/item/ItemContainerTab", "a", "()Ljava/util/ArrayList;"): "getItems",
    ("com/rs2/model/player/CharacterFileBankTab", "a", "()Ljava/util/ArrayList;"): "getItems",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;)V"): "openBank",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;I)V"): "selectTab",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V"): "openBank",
    ("com/rs2/model/player/BankManager", "b", "(Lcom/rs2/model/player/Player;)V"): "openDepositBox",
    ("com/rs2/model/player/BankManager", "c", "(Lcom/rs2/model/player/Player;)Z"): "depositEquipment",
    ("com/rs2/model/player/BankManager", "d", "(Lcom/rs2/model/player/Player;)Z"): "depositInventory",
    ("com/rs2/model/player/BankManager", "e", "(Lcom/rs2/model/player/Player;)Z"): "depositInventoryAndEquipment",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;I)Z"): "canDepositItem",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;)V"): "depositToBank",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;[Lcom/rs2/model/item/ItemStack;)Z"): "canDepositItems",
    ("com/rs2/model/player/BankManager", "f", "(Lcom/rs2/model/player/Player;)V"): "refreshBankAndInventory",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;III)V"): "depositInventoryItem",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;II)V"): "withdrawItem",
    ("com/rs2/model/player/BankManager", "a", "(Lcom/rs2/model/player/Player;IIII)V"): "withdrawItemFromTab",
    ("com/rs2/model/player/BankManager", "b", "(Lcom/rs2/model/player/Player;IIII)V"): "rearrangeBankItem",
    ("com/rs2/model/player/BankManager", "g", "(Lcom/rs2/model/player/Player;)V"): "refreshBankTabs",
    ("com/rs2/model/player/Player", "a", "(Ljava/util/ArrayList;)V"): "interactWithBotNpcTargets",
    ("com/rs2/model/player/Player", "b", "(Ljava/util/ArrayList;)Z"): "interactWithBotObjectTargets",
    ("com/rs2/model/player/Player", "a", "(Ljava/util/ArrayList;Z)Z"): "interactWithBotObjectTargetsNoRetry",
    ("com/rs2/model/player/Player", "a", "(Ljava/util/ArrayList;ZII)Z"): "interactWithBotObjectTargets",
    ("com/rs2/model/item/ItemStack", "a", "(I)V"): "setAmount",
    ("com/rs2/model/item/ItemStack", "b", "(I)V"): "setMetadata",
    ("com/rs2/model/item/ItemStack", "a", "()I"): "getId",
    ("com/rs2/model/item/ItemStack", "b", "()I"): "getAmount",
    ("com/rs2/model/item/ItemStack", "c", "()I"): "getMetadata",
    ("com/rs2/model/item/ItemStack", "d", "()Lcom/rs2/model/item/ItemDefinition;"): "getDefinition",
    ("com/rs2/model/item/ItemStack", "e", "()Z"): "isValid",
    ("com/rs2/model/item/ItemStack", "f", "()Z"): "isEquippable",
    ("com/rs2/model/player/Player", "aN", "()V"): "resetAnimation",
    (
        "com/rs2/model/skill/farming/FarmingPatchUtils",
        "a",
        "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;Lcom/rs2/model/Position;)Z",
    ): "containsPosition",
    ("com/rs2/model/skill/cooking/WineFermentationHandler", "a", "(I)V"): "finishInventoryWineFermentation",
    ("com/rs2/model/skill/cooking/WineFermentationHandler", "b", "(I)V"): "finishBankWineFermentation",
    ("com/rs2/model/item/ItemDefinition", "a", "(I)Lcom/rs2/model/item/ItemDefinition;"): "forId",
    ("com/rs2/model/item/ItemDefinition", "a", "()V"): "loadDefinitions",
    ("com/rs2/model/item/ItemDefinition", "b", "(I)Z"): "isDefined",
    ("com/rs2/model/item/ItemDefinition", "a", "(Ljava/lang/String;)I"): "findIdByName",
    ("com/rs2/model/item/ItemDefinition", "b", "()I"): "getId",
    ("com/rs2/model/item/ItemDefinition", "c", "()Ljava/lang/String;"): "getName",
    ("com/rs2/model/item/ItemDefinition", "d", "()Ljava/lang/String;"): "getShortName",
    ("com/rs2/model/item/ItemDefinition", "e", "()Ljava/lang/String;"): "getDisplayName",
    ("com/rs2/model/item/ItemDefinition", "f", "()Ljava/lang/String;"): "getDescription",
    ("com/rs2/model/item/ItemDefinition", "h", "()Z"): "isNote",
    ("com/rs2/model/item/ItemDefinition", "i", "()Z"): "hasNote",
    ("com/rs2/model/item/ItemDefinition", "j", "()Z"): "isStackable",
    ("com/rs2/model/item/ItemDefinition", "k", "()I"): "getUnnotedId",
    ("com/rs2/model/item/ItemDefinition", "l", "()I"): "getNotedId",
    ("com/rs2/model/item/ItemDefinition", "m", "()Z"): "isMembersOnly",
    ("com/rs2/model/item/ItemDefinition", "n", "()I"): "getValue",
    ("com/rs2/model/item/ItemDefinition", "o", "()I"): "getTokkulValue",
    ("com/rs2/model/item/ItemDefinition", "p", "()I"): "getShopValue",
    ("com/rs2/model/item/ItemDefinition", "q", "()I"): "getDonatorPointValue",
    ("com/rs2/model/item/ItemDefinition", "r", "()I"): "getLowAlchemyValue",
    ("com/rs2/model/item/ItemDefinition", "s", "()I"): "getHighAlchemyValue",
    ("com/rs2/model/item/ItemDefinition", "t", "()[I"): "getBonuses",
    ("com/rs2/model/item/ItemDefinition", "c", "(I)I"): "getBonus",
    ("com/rs2/model/item/ItemDefinition", "d", "(I)I"): "getRequiredLevel",
    ("com/rs2/model/item/ItemDefinition", "e", "(I)Z"): "requiresQuest",
    ("com/rs2/model/item/ItemDefinition", "u", "()D"): "getWeight",
    ("com/rs2/model/item/ItemDefinition", "v", "()I"): "getEquipmentSlot",
    ("com/rs2/model/item/ItemDefinition", "w", "()I"): "getEquipmentAppearanceType",
    ("com/rs2/model/item/ItemDefinition", "x", "()I"): "getRequiredQuestPoints",
    ("com/rs2/model/item/ItemDefinition", "y", "()Z"): "isTwoHanded",
    ("com/rs2/model/item/ItemDefinition", "z", "()Z"): "isUntradeable",
    ("com/rs2/model/item/ItemDefinition", "A", "()Z"): "hasDestroyOption",
    ("com/rs2/model/item/ItemService", "a", "()Lcom/rs2/model/item/ItemService;"): "getInstance",
    ("com/rs2/model/item/ItemService", "b", "(I)Ljava/lang/String;"): "getItemName",
    ("com/rs2/model/item/ItemService", "a", "(I)Z"): "isEssencePouch",
    ("com/rs2/model/item/ItemService", "a", "(ILjava/lang/String;I)I"): "getPrice",
    ("com/rs2/model/item/ItemService", "a", "(Lcom/rs2/model/player/Player;ILcom/rs2/model/Position;)V"): "pickupItem",
    (
        "com/rs2/model/item/ItemService",
        "a",
        "(Lcom/rs2/model/player/Player;ILcom/rs2/model/ground/GroundItem;Z)V",
    ): "handleBotGroundItemPickupResult",
    ("com/rs2/model/player/Player", "K", "(I)V"): "setCurrentShopId",
    ("com/rs2/model/player/Player", "bS", "()I"): "getCurrentShopId",
    ("com/rs2/model/shop/ShopDefinition", "a", "(I)V"): "setBuyPricePercent",
    ("com/rs2/model/shop/ShopDefinition", "a", "()I"): "getBuyPricePercent",
    ("com/rs2/model/shop/ShopDefinition", "b", "(I)V"): "setSellPricePercent",
    ("com/rs2/model/shop/ShopDefinition", "b", "()I"): "getSellPricePercent",
    ("com/rs2/model/shop/ShopDefinition", "c", "(I)V"): "setPriceChangeRateTenths",
    ("com/rs2/model/shop/ShopDefinition", "c", "()D"): "getPriceChangeRate",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Ljava/lang/String;)V"): "setName",
    ("com/rs2/model/shop/ShopDefinition", "d", "()Ljava/lang/String;"): "getName",
    ("com/rs2/model/shop/ShopDefinition", "e", "()Z"): "isMembersOnly",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Z)V"): "setMembersOnly",
    ("com/rs2/model/shop/ShopDefinition", "f", "()I"): "getShopId",
    ("com/rs2/model/shop/ShopDefinition", "b", "(Z)V"): "setGeneralStore",
    ("com/rs2/model/shop/ShopDefinition", "g", "()Z"): "isGeneralStore",
    ("com/rs2/model/shop/ShopDefinition", "d", "(I)V"): "setCurrencyItemId",
    ("com/rs2/model/shop/ShopDefinition", "h", "()I"): "getCurrencyItemId",
    ("com/rs2/model/shop/ShopDefinition", "i", "()Lcom/rs2/model/shop/ShopCurrency;"): "getCurrency",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Lcom/rs2/model/shop/ShopCurrency;)V"): "setCurrency",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Lcom/rs2/model/item/ItemContainer;)V"): "setOriginalStock",
    ("com/rs2/model/shop/ShopDefinition", "j", "()Lcom/rs2/model/item/ItemContainer;"): "getOriginalStock",
    ("com/rs2/model/shop/ShopDefinition", "b", "(Lcom/rs2/model/item/ItemContainer;)V"): "setStock",
    ("com/rs2/model/shop/ShopDefinition", "k", "()Lcom/rs2/model/item/ItemContainer;"): "getStock",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Lcom/rs2/model/shop/ShopDefinition;[I)V"): "setRestockDelayTicks",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Lcom/rs2/model/shop/ShopDefinition;[Lcom/rs2/model/task/TickTask;)V"): "setRestockTasks",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Lcom/rs2/model/shop/ShopDefinition;)[I"): "getRestockDelayTicks",
    ("com/rs2/model/shop/ShopDefinition", "a", "(Lcom/rs2/model/shop/ShopDefinition;I)V"): "setShopId",
    ("com/rs2/model/shop/ShopDefinition", "b", "(Lcom/rs2/model/shop/ShopDefinition;)[Lcom/rs2/model/task/TickTask;"): "getRestockTasks",
    ("com/rs2/model/shop/ShopManager", "a", "(I)V"): "refreshShopForPlayers",
    ("com/rs2/model/shop/ShopManager", "a", "(Lcom/rs2/model/player/Player;I)V"): "openShop",
    ("com/rs2/model/shop/ShopManager", "b", "(I)Z"): "isSkillcapeBundleItem",
    ("com/rs2/model/shop/ShopManager", "c", "(I)Z"): "requiresSecondaryCurrencyItem",
    ("com/rs2/model/shop/ShopManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;)V"): "buyItemStack",
    ("com/rs2/model/shop/ShopManager", "a", "(Lcom/rs2/model/player/Player;III)V"): "buyItem",
    ("com/rs2/model/shop/ShopManager", "b", "(Lcom/rs2/model/shop/ShopDefinition;II)I"): "calculateBuyPrice",
    ("com/rs2/model/shop/ShopManager", "c", "(Lcom/rs2/model/shop/ShopDefinition;II)I"): "calculateSellPrice",
    ("com/rs2/model/shop/ShopManager", "b", "(Lcom/rs2/model/player/Player;III)V"): "sellItem",
    ("com/rs2/model/shop/ShopManager", "b", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;)V"): "sellItemStack",
    ("com/rs2/model/shop/ShopManager", "b", "(Lcom/rs2/model/player/Player;I)V"): "sendBuyPrice",
    ("com/rs2/model/shop/ShopManager", "c", "(Lcom/rs2/model/player/Player;I)V"): "sendSellPrice",
    ("com/rs2/model/shop/ShopManager", "a", "(Lcom/rs2/model/shop/ShopDefinition;)Ljava/lang/String;"): "getCurrencyDisplayName",
    ("com/rs2/model/shop/ShopManager", "a", "()V"): "loadShops",
    ("com/rs2/model/shop/ShopManager", "d", "(Lcom/rs2/model/shop/ShopDefinition;II)V"): "updateRestockSchedule",
    ("com/rs2/model/shop/ShopManager", "a", "(Lcom/rs2/model/shop/ShopDefinition;I)I"): "indexOfStockItem",
    ("com/rs2/model/shop/ShopManager", "b", "(Lcom/rs2/model/shop/ShopDefinition;I)V"): "startRestockTask",
    ("com/rs2/model/shop/ShopManager", "a", "(Lcom/rs2/model/shop/ShopDefinition;II)Z"): "needsRestock",
    ("com/rs2/model/shop/ShopManager", "b", "()Ljava/util/List;"): "getShopDefinitions",
    ("com/rs2/model/shop/ShopManager", "c", "()[I"): "getShopCurrencySwitchMap",
    ("com/rs2/model/player/GrandExchangeManager", "a", "(Lcom/rs2/model/player/Player;)V"): "openGrandExchange",
    ("com/rs2/model/player/GrandExchangeManager", "b", "(Lcom/rs2/model/player/Player;)V"): "refreshSelectedOfferDetails",
    ("com/rs2/model/player/GrandExchangeManager", "a", "(Lcom/rs2/model/player/Player;III)V"): "collectOfferItem",
    ("com/rs2/model/player/GrandExchangeManager", "a", "(Lcom/rs2/model/player/Player;I)V"): "sendOfferCompletionMessage",
    ("com/rs2/model/player/GrandExchangeManager", "d", "(Lcom/rs2/model/player/Player;)V"): "clearSelectedOfferSlot",
    ("com/rs2/model/player/GrandExchangeManager", "c", "(Lcom/rs2/model/player/Player;)V"): "refreshOfferSlots",
    ("com/rs2/model/player/GrandExchangeManager", "a", "()V"): "rollInstantPriceFluctuation",
    ("com/rs2/model/player/GrandExchangeManager", "a", "(I)I"): "getGuidePrice",
    ("com/rs2/model/player/GrandExchangeManager", "c", "(Lcom/rs2/model/player/Player;I)V"): "setSelectedOfferQuantity",
    ("com/rs2/model/player/GrandExchangeManager", "d", "(Lcom/rs2/model/player/Player;I)V"): "setSelectedOfferUnitPrice",
    ("com/rs2/model/player/GrandExchangeManager", "e", "(Lcom/rs2/model/player/Player;I)V"): "adjustSelectedOfferQuantity",
    ("com/rs2/model/player/GrandExchangeManager", "f", "(Lcom/rs2/model/player/Player;I)V"): "adjustSelectedOfferUnitPrice",
    ("com/rs2/model/player/GrandExchangeManager", "a", "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V"): "applySelectedOfferPricePreset",
    ("com/rs2/model/player/GrandExchangeManager", "e", "(Lcom/rs2/model/player/Player;)V"): "refreshSelectedOfferTotals",
    ("com/rs2/model/player/GrandExchangeManager", "b", "(Lcom/rs2/model/player/Player;III)V"): "selectSellOfferItem",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "()V"): "initializeServerOffers",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "c", "()V"): "createServerOffer",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "b", "()V"): "processServerOffers",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/player/Player;I)V"): "cancelOffer",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/grandexchange/GrandExchangeOffer;Z)V"): "matchOffer",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/grandexchange/GrandExchangeOffer;Lcom/rs2/model/grandexchange/GrandExchangeOffer;)V"): "settleMatchedOffers",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/player/CharacterFileRecord;Lcom/rs2/model/grandexchange/GrandExchangeOffer;Lcom/rs2/model/player/CharacterFileRecord;Lcom/rs2/model/grandexchange/GrandExchangeOffer;)V"): "settleRecordVsRecordMatch",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/grandexchange/GrandExchangeOffer;Lcom/rs2/model/player/Player;Lcom/rs2/model/grandexchange/GrandExchangeOffer;)V"): "settlePlayerVsPlayerMatch",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/grandexchange/GrandExchangeOffer;Lcom/rs2/model/player/CharacterFileRecord;Lcom/rs2/model/grandexchange/GrandExchangeOffer;)V"): "settlePlayerVsRecordMatch",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/player/CharacterFileRecord;Lcom/rs2/model/grandexchange/GrandExchangeOffer;Lcom/rs2/model/player/Player;Lcom/rs2/model/grandexchange/GrandExchangeOffer;)V"): "settleRecordVsPlayerMatch",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/player/CharacterFileRecord;Lcom/rs2/model/grandexchange/GrandExchangeOffer;)Z"): "isRecordOfferCurrent",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/grandexchange/GrandExchangeOffer;)Z"): "isPlayerOfferCurrent",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "(III)V"): "recordPriceSample",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "a", "(I)I"): "getAveragePrice",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "a", "()V"): "loadPriceSamples",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "b", "()V"): "savePriceSamples",
    ("com/rs2/model/message/MessageOfTheWeek", "a", "(I)Lcom/rs2/model/message/MessageOfTheWeek;"): "getMessageForIndex",
    ("com/rs2/model/message/MessageOfTheWeek", "e", "()V"): "saveCurrentMessageIndex",
    ("com/rs2/model/message/MessageOfTheWeek", "a", "()V"): "loadAndRotateMessage",
    ("com/rs2/model/message/MessageOfTheWeek", "b", "()I"): "getInterfaceId",
    ("com/rs2/model/message/MessageOfTheWeek", "c", "()Ljava/lang/String;"): "getTitle",
    ("com/rs2/model/message/MessageOfTheWeek", "d", "()[Ljava/lang/String;"): "getLines",
    ("com/rs2/model/npc/drop/NpcDropEntry", "a", "(I)V"): "setChanceNumerator",
    ("com/rs2/model/npc/drop/NpcDropEntry", "b", "(I)V"): "setChanceDenominator",
    ("com/rs2/model/npc/drop/NpcDropEntry", "a", "()I"): "getChanceType",
    ("com/rs2/model/npc/drop/NpcDropEntry", "b", "()I"): "getChanceNumerator",
    ("com/rs2/model/npc/drop/NpcDropEntry", "c", "()I"): "getChanceDenominator",
    ("com/rs2/model/npc/drop/NpcDropEntry", "d", "()I"): "getItemId",
    ("com/rs2/model/npc/drop/NpcDropEntry", "e", "()I"): "getFixedAmount",
    ("com/rs2/model/npc/drop/NpcDropEntry", "f", "()I"): "getMinAmount",
    ("com/rs2/model/npc/drop/NpcDropEntry", "g", "()I"): "getMaxAmount",
    ("com/rs2/model/npc/drop/NpcDropEntry", "h", "()[I"): "getItemIds",
    ("com/rs2/model/npc/drop/NpcDropEntry", "i", "()[I"): "getAmountOptions",
    ("com/rs2/model/npc/drop/NpcDropEntry", "j", "()[I"): "getMinAmounts",
    ("com/rs2/model/npc/drop/NpcDropEntry", "k", "()[I"): "getMaxAmounts",
    ("com/rs2/model/npc/drop/NpcDropTable", "a", "()V"): "loadDropTables",
    ("com/rs2/model/npc/drop/NpcDropTable", "a", "(I)Lcom/rs2/model/npc/drop/NpcDropTable;"): "forNpcId",
    ("com/rs2/model/npc/drop/NpcDropTable", "a", "(Lcom/rs2/model/Entity;)[Lcom/rs2/model/npc/drop/NpcDropEntry;"): "getGuaranteedDrops",
    ("com/rs2/model/npc/drop/NpcDropTable", "b", "(Lcom/rs2/model/Entity;)[Lcom/rs2/model/npc/drop/NpcDropEntry;"): "getWeightedDrops",
    ("com/rs2/model/npc/drop/NpcDropTable", "c", "(Lcom/rs2/model/Entity;)[Lcom/rs2/model/npc/drop/NpcDropEntry;"): "getIndependentDrops",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(Lcom/rs2/model/Entity;IZ)[Lcom/rs2/model/item/ItemStack;"): "rollDrops",
    ("com/rs2/model/npc/drop/NpcDropManager", "b", "(I)Lcom/rs2/model/npc/drop/NpcDropTable;"): "getDropTableForNpcId",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(Lcom/rs2/model/Entity;I)I"): "resolveVirtualDropItemId",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(I)Z"): "isVirtualDropId",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(II)I"): "randomInclusive",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(Lcom/rs2/model/Entity;IZII)[Lcom/rs2/model/item/ItemStack;"): "rollIndependentDrops",
    ("com/rs2/model/npc/drop/NpcDropManager", "b", "(Lcom/rs2/model/Entity;IZII)[Lcom/rs2/model/item/ItemStack;"): "rollWeightedDrops",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(Lcom/rs2/model/Entity;IZIIZI)[Lcom/rs2/model/item/ItemStack;"): "rollDropEntrySelection",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "([Lcom/rs2/model/npc/drop/NpcDropEntry;)[Lcom/rs2/model/npc/drop/NpcDropEntry;"): "removeNoDropEntries",
    ("com/rs2/model/npc/drop/NpcDropManager", "c", "(I)Z"): "isVirtualDropTableId",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(Lcom/rs2/model/Entity;III)[Lcom/rs2/model/item/ItemStack;"): "resolveVirtualDropTable",
    ("com/rs2/model/npc/drop/NpcDropManager", "b", "([Lcom/rs2/model/npc/drop/NpcDropEntry;)V"): "assignMissingWeightedChances",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(JJ)J"): "greatestCommonDivisor",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "(DD)D"): "applyCustomDropRate",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "([Lcom/rs2/model/npc/drop/NpcDropEntry;Z)I"): "selectDropIndex",
    ("com/rs2/model/npc/drop/NpcDropManager", "b", "(II)Lcom/rs2/model/item/ItemStack;"): "createItemStack",
    ("com/rs2/model/ground/GroundItem", "a", "()Lcom/rs2/model/EntityReference;"): "getSource",
    ("com/rs2/model/ground/GroundItem", "b", "()Lcom/rs2/model/EntityReference;"): "getOwner",
    ("com/rs2/model/ground/GroundItem", "c", "()Lcom/rs2/model/item/ItemStack;"): "getItem",
    ("com/rs2/model/ground/GroundItem", "d", "()Lcom/rs2/model/task/ElapsedTicks;"): "getTimer",
    ("com/rs2/model/ground/GroundItem", "e", "()I"): "getRespawnDelayTicks",
    ("com/rs2/model/ground/GroundItem", "f", "()Lcom/rs2/model/ground/GroundItemVisibility;"): "getVisibility",
    ("com/rs2/model/ground/GroundItem", "a", "(Lcom/rs2/model/ground/GroundItemVisibility;)V"): "setVisibility",
    ("com/rs2/model/ground/GroundItem", "g", "()Lcom/rs2/model/Position;"): "getPosition",
    ("com/rs2/model/ground/GroundItem", "h", "()Z"): "isRespawning",
    ("com/rs2/model/ground/GroundItem", "i", "()Z"): "allowsRestrictedModePickup",
    ("com/rs2/model/ground/GroundItem", "a", "(Lcom/rs2/model/ground/GroundItem;)Z"): "canStackWith",
    ("com/rs2/model/ground/GroundItem", "a", "(Lcom/rs2/model/player/Player;)Z"): "isVisibleTo",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/ground/GroundItem;Lcom/rs2/model/ground/GroundItem;[Lcom/rs2/model/player/Player;)V"): "mergeStackedItems",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/ground/GroundItem;[Lcom/rs2/model/player/Player;)V"): "removeForPlayers",
    ("com/rs2/model/ground/GroundItemManager", "b", "(Lcom/rs2/model/ground/GroundItem;[Lcom/rs2/model/player/Player;)V"): "showToPlayers",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/ground/GroundItem;Lcom/rs2/model/player/Player;)Z"): "removeForPickup",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/ground/GroundItem;)V"): "spawn",
    ("com/rs2/model/ground/GroundItemManager", "a", "()Lcom/rs2/model/ground/GroundItemManager;"): "getInstance",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/ground/GroundItem;)Z"): "isVisible",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/player/Player;ILcom/rs2/model/Position;)Lcom/rs2/model/ground/GroundItem;"): "findVisibleItem",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;)Lcom/rs2/model/ground/GroundItem;"): "findVisibleItemAt",
    ("com/rs2/model/ground/GroundItemManager", "a", "(Lcom/rs2/model/player/Player;)V"): "clearVisibleItems",
    ("com/rs2/model/ground/GroundItemManager", "b", "(Lcom/rs2/model/player/Player;)V"): "refreshForPlayer",
    ("com/rs2/model/task/ElapsedTicks", "a", "()I"): "elapsed",
    ("com/rs2/model/task/ElapsedTicks", "b", "()V"): "reset",
    ("com/rs2/model/Position", "a", "(Lcom/rs2/model/Position;)V"): "set",
    ("com/rs2/model/Position", "a", "(II)V"): "translate",
    ("com/rs2/model/Position", "a", "(I)V"): "setX",
    ("com/rs2/model/Position", "b", "()I"): "getX",
    ("com/rs2/model/Position", "b", "(I)V"): "setY",
    ("com/rs2/model/Position", "c", "()I"): "getY",
    ("com/rs2/model/Position", "c", "(I)V"): "setPlane",
    ("com/rs2/model/Position", "d", "()I"): "getPlane",
    ("com/rs2/model/Position", "d", "(I)V"): "setPreviousX",
    ("com/rs2/model/Position", "e", "()I"): "getPreviousX",
    ("com/rs2/model/Position", "e", "(I)V"): "setPreviousY",
    ("com/rs2/model/Position", "f", "()I"): "getPreviousY",
    ("com/rs2/model/Position", "g", "()I"): "getRegionX",
    ("com/rs2/model/Position", "h", "()I"): "getRegionY",
    ("com/rs2/model/Position", "i", "()I"): "getLocalX",
    ("com/rs2/model/Position", "j", "()I"): "getLocalY",
    ("com/rs2/model/Position", "a", "(Lcom/rs2/model/player/Player;)I"): "updateLocalX",
    ("com/rs2/model/Position", "b", "(Lcom/rs2/model/player/Player;)I"): "updateLocalY",
    ("com/rs2/model/Position", "b", "(Lcom/rs2/model/Position;)Z"): "isWithinViewport",
    ("com/rs2/model/Position", "a", "(Lcom/rs2/model/Position;I)Z"): "isWithinDistance",
    ("com/rs2/model/Position", "c", "(Lcom/rs2/model/Position;)Z"): "isOrthogonallyAlignedWith",
    ("com/rs2/model/Position", "k", "()Lcom/rs2/model/Position;"): "copy",
    ("com/rs2/model/Position", "f", "(I)Lcom/rs2/model/Position;"): "centerForSize",
    ("com/rs2/model/gameplay/PositionRange", "a", "()Lcom/rs2/model/Position;"): "getMinPosition",
    ("com/rs2/model/gameplay/PositionRange", "b", "()Lcom/rs2/model/Position;"): "getMaxPosition",
    ("com/rs2/model/gameplay/PositionRange", "a", "(I)V"): "setPlane",
    (
        "com/rs2/model/GameplayHelper",
        "a",
        "(Lcom/rs2/model/gameplay/PositionRange;)Lcom/rs2/model/Position;",
    ): "randomUnblockedPositionInRange",
    ("com/rs2/model/GameplayHelper", "b", "()V"): "startClanWarsBotEvent",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/Position;)V"): "setPosition",
    ("com/rs2/model/Entity", "H", "()Lcom/rs2/model/Position;"): "getPosition",
    ("com/rs2/model/player/Player", "H", "()Lcom/rs2/model/Position;"): "getPosition",
    ("com/rs2/model/npc/Npc", "H", "()Lcom/rs2/model/Position;"): "getPosition",
    ("com/rs2/bot/BotPlayer", "H", "()Lcom/rs2/model/Position;"): "getPosition",
    ("com/rs2/model/Entity", "a", "(IIII)Z"): "isInArea",
    ("com/rs2/model/player/Player", "a", "(IIII)Z"): "isInArea",
    ("com/rs2/model/npc/Npc", "a", "(IIII)Z"): "isInArea",
    ("com/rs2/bot/BotPlayer", "a", "(IIII)Z"): "isInArea",
    ("com/rs2/model/Entity", "a", "(IIIIII)Z"): "isPointInArea",
    ("com/rs2/model/Entity", "o", "()Z"): "isInCastleWars",
    ("com/rs2/model/player/Player", "o", "()Z"): "isInCastleWars",
    ("com/rs2/model/npc/Npc", "o", "()Z"): "isInCastleWars",
    ("com/rs2/bot/BotPlayer", "o", "()Z"): "isInCastleWars",
    ("com/rs2/model/Entity", "p", "()Z"): "isInApeAtoll",
    ("com/rs2/model/player/Player", "p", "()Z"): "isInApeAtoll",
    ("com/rs2/model/npc/Npc", "p", "()Z"): "isInApeAtoll",
    ("com/rs2/bot/BotPlayer", "p", "()Z"): "isInApeAtoll",
    ("com/rs2/model/Entity", "a", "()Z"): "isInFightPits",
    ("com/rs2/model/Entity", "r", "()Z"): "isInTutorialIsland",
    ("com/rs2/model/player/Player", "r", "()Z"): "isInTutorialIsland",
    ("com/rs2/model/npc/Npc", "r", "()Z"): "isInTutorialIsland",
    ("com/rs2/bot/BotPlayer", "r", "()Z"): "isInTutorialIsland",
    ("com/rs2/model/Entity", "b", "()Z"): "isInWildernessDitchSafeZone",
    ("com/rs2/model/Entity", "s", "()Z"): "isInTenthSquadSigilInstance",
    ("com/rs2/model/player/Player", "s", "()Z"): "isInTenthSquadSigilInstance",
    ("com/rs2/model/npc/Npc", "s", "()Z"): "isInTenthSquadSigilInstance",
    ("com/rs2/bot/BotPlayer", "s", "()Z"): "isInTenthSquadSigilInstance",
    ("com/rs2/model/Entity", "t", "()Z"): "isInWilderness",
    ("com/rs2/model/player/Player", "t", "()Z"): "isInWilderness",
    ("com/rs2/model/npc/Npc", "t", "()Z"): "isInWilderness",
    ("com/rs2/bot/BotPlayer", "t", "()Z"): "isInWilderness",
    ("com/rs2/model/Entity", "a", "(II)Z"): "isWildernessCoordinate",
    ("com/rs2/model/player/Player", "a", "(II)Z"): "isWildernessCoordinate",
    ("com/rs2/model/npc/Npc", "a", "(II)Z"): "isWildernessCoordinate",
    ("com/rs2/bot/BotPlayer", "a", "(II)Z"): "isWildernessCoordinate",
    ("com/rs2/model/Entity", "u", "()I"): "getWildernessLevel",
    ("com/rs2/model/player/Player", "u", "()I"): "getWildernessLevel",
    ("com/rs2/model/npc/Npc", "u", "()I"): "getWildernessLevel",
    ("com/rs2/bot/BotPlayer", "u", "()I"): "getWildernessLevel",
    ("com/rs2/model/Entity", "w", "()Z"): "isInMultiCombatArea",
    ("com/rs2/model/player/Player", "w", "()Z"): "isInMultiCombatArea",
    ("com/rs2/model/npc/Npc", "w", "()Z"): "isInMultiCombatArea",
    ("com/rs2/bot/BotPlayer", "w", "()Z"): "isInMultiCombatArea",
    ("com/rs2/model/Entity", "b", "(II)Z"): "isInCastleWarsObstacleArea",
    ("com/rs2/model/player/Player", "b", "(II)Z"): "isInCastleWarsObstacleArea",
    ("com/rs2/model/npc/Npc", "b", "(II)Z"): "isInCastleWarsObstacleArea",
    ("com/rs2/bot/BotPlayer", "b", "(II)Z"): "isInCastleWarsObstacleArea",
    ("com/rs2/model/Entity", "x", "()Z"): "isInFiremakingRestrictedArea",
    ("com/rs2/model/player/Player", "x", "()Z"): "isInFiremakingRestrictedArea",
    ("com/rs2/model/npc/Npc", "x", "()Z"): "isInFiremakingRestrictedArea",
    ("com/rs2/bot/BotPlayer", "x", "()Z"): "isInFiremakingRestrictedArea",
    ("com/rs2/model/Entity", "q", "()Z"): "isInFightCave",
    ("com/rs2/model/player/Player", "q", "()Z"): "isInFightCave",
    ("com/rs2/model/npc/Npc", "q", "()Z"): "isInFightCave",
    ("com/rs2/bot/BotPlayer", "q", "()Z"): "isInFightCave",
    ("com/rs2/model/Entity", "z", "()Z"): "isInDuelArena",
    ("com/rs2/model/player/Player", "z", "()Z"): "isInDuelArena",
    ("com/rs2/model/npc/Npc", "z", "()Z"): "isInDuelArena",
    ("com/rs2/bot/BotPlayer", "z", "()Z"): "isInDuelArena",
    ("com/rs2/model/Entity", "B", "()Z"): "isInMageArena",
    ("com/rs2/model/player/Player", "B", "()Z"): "isInMageArena",
    ("com/rs2/model/npc/Npc", "B", "()Z"): "isInMageArena",
    ("com/rs2/bot/BotPlayer", "B", "()Z"): "isInMageArena",
    ("com/rs2/model/Entity", "A", "()Z"): "isInDuelArenaLobby",
    ("com/rs2/model/player/Player", "A", "()Z"): "isInDuelArenaLobby",
    ("com/rs2/model/npc/Npc", "A", "()Z"): "isInDuelArenaLobby",
    ("com/rs2/bot/BotPlayer", "A", "()Z"): "isInDuelArenaLobby",
    ("com/rs2/model/Entity", "y", "()Z"): "isInBarrows",
    ("com/rs2/model/player/Player", "y", "()Z"): "isInBarrows",
    ("com/rs2/model/npc/Npc", "y", "()Z"): "isInBarrows",
    ("com/rs2/bot/BotPlayer", "y", "()Z"): "isInBarrows",
    ("com/rs2/model/Entity", "v", "()Z"): "isInSmokeDungeon",
    ("com/rs2/model/player/Player", "v", "()Z"): "isInSmokeDungeon",
    ("com/rs2/model/npc/Npc", "v", "()Z"): "isInSmokeDungeon",
    ("com/rs2/bot/BotPlayer", "v", "()Z"): "isInSmokeDungeon",
    ("com/rs2/model/Entity", "az", "()Z"): "isInTeleportRestrictedArea",
    ("com/rs2/model/player/Player", "az", "()Z"): "isInTeleportRestrictedArea",
    ("com/rs2/model/npc/Npc", "az", "()Z"): "isInTeleportRestrictedArea",
    ("com/rs2/bot/BotPlayer", "az", "()Z"): "isInTeleportRestrictedArea",
    ("com/rs2/model/Entity", "a", "(IIIIIII)Z"): "canTravelBetween",
    ("com/rs2/model/player/Player", "a", "(IIIIIII)Z"): "canTravelBetween",
    ("com/rs2/model/npc/Npc", "a", "(IIIIIII)Z"): "canTravelBetween",
    ("com/rs2/bot/BotPlayer", "a", "(IIIIIII)Z"): "canTravelBetween",
    ("com/rs2/model/Entity", "aq", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockImmunityTimer",
    ("com/rs2/model/player/Player", "aq", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockImmunityTimer",
    ("com/rs2/model/npc/Npc", "aq", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockImmunityTimer",
    ("com/rs2/bot/BotPlayer", "aq", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockImmunityTimer",
    ("com/rs2/model/Entity", "ar", "()Lcom/rs2/model/task/DelayTimer;"): "getPoisonImmunityTimer",
    ("com/rs2/model/player/Player", "ar", "()Lcom/rs2/model/task/DelayTimer;"): "getPoisonImmunityTimer",
    ("com/rs2/model/npc/Npc", "ar", "()Lcom/rs2/model/task/DelayTimer;"): "getPoisonImmunityTimer",
    ("com/rs2/bot/BotPlayer", "ar", "()Lcom/rs2/model/task/DelayTimer;"): "getPoisonImmunityTimer",
    ("com/rs2/model/Entity", "as", "()Lcom/rs2/model/task/DelayTimer;"): "getAntifireTimer",
    ("com/rs2/model/player/Player", "as", "()Lcom/rs2/model/task/DelayTimer;"): "getAntifireTimer",
    ("com/rs2/model/npc/Npc", "as", "()Lcom/rs2/model/task/DelayTimer;"): "getAntifireTimer",
    ("com/rs2/bot/BotPlayer", "as", "()Lcom/rs2/model/task/DelayTimer;"): "getAntifireTimer",
    ("com/rs2/model/Entity", "at", "()Lcom/rs2/model/task/DelayTimer;"): "getTeleblockTimer",
    ("com/rs2/model/player/Player", "at", "()Lcom/rs2/model/task/DelayTimer;"): "getTeleblockTimer",
    ("com/rs2/model/npc/Npc", "at", "()Lcom/rs2/model/task/DelayTimer;"): "getTeleblockTimer",
    ("com/rs2/bot/BotPlayer", "at", "()Lcom/rs2/model/task/DelayTimer;"): "getTeleblockTimer",
    ("com/rs2/model/Entity", "au", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockTimer",
    ("com/rs2/model/player/Player", "au", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockTimer",
    ("com/rs2/model/npc/Npc", "au", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockTimer",
    ("com/rs2/bot/BotPlayer", "au", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementLockTimer",
    ("com/rs2/model/Entity", "av", "()Lcom/rs2/model/task/DelayTimer;"): "getStunTimer",
    ("com/rs2/model/player/Player", "av", "()Lcom/rs2/model/task/DelayTimer;"): "getStunTimer",
    ("com/rs2/model/npc/Npc", "av", "()Lcom/rs2/model/task/DelayTimer;"): "getStunTimer",
    ("com/rs2/bot/BotPlayer", "av", "()Lcom/rs2/model/task/DelayTimer;"): "getStunTimer",
    ("com/rs2/model/Entity", "aw", "()Z"): "isMovementLockImmune",
    ("com/rs2/model/player/Player", "aw", "()Z"): "isMovementLockImmune",
    ("com/rs2/model/npc/Npc", "aw", "()Z"): "isMovementLockImmune",
    ("com/rs2/bot/BotPlayer", "aw", "()Z"): "isMovementLockImmune",
    ("com/rs2/model/Entity", "ax", "()Z"): "isAntifireActive",
    ("com/rs2/model/player/Player", "ax", "()Z"): "isAntifireActive",
    ("com/rs2/model/npc/Npc", "ax", "()Z"): "isAntifireActive",
    ("com/rs2/bot/BotPlayer", "ax", "()Z"): "isAntifireActive",
    ("com/rs2/model/Entity", "ay", "()Z"): "isTeleblocked",
    ("com/rs2/model/player/Player", "ay", "()Z"): "isTeleblocked",
    ("com/rs2/model/npc/Npc", "ay", "()Z"): "isTeleblocked",
    ("com/rs2/bot/BotPlayer", "ay", "()Z"): "isTeleblocked",
    ("com/rs2/model/Entity", "aA", "()Z"): "isMovementLocked",
    ("com/rs2/model/player/Player", "aA", "()Z"): "isMovementLocked",
    ("com/rs2/model/npc/Npc", "aA", "()Z"): "isMovementLocked",
    ("com/rs2/bot/BotPlayer", "aA", "()Z"): "isMovementLocked",
    ("com/rs2/model/Entity", "aB", "()Z"): "isStunned",
    ("com/rs2/model/player/Player", "aB", "()Z"): "isStunned",
    ("com/rs2/model/npc/Npc", "aB", "()Z"): "isStunned",
    ("com/rs2/bot/BotPlayer", "aB", "()Z"): "isStunned",
    ("com/rs2/model/Entity", "Z", "()Lcom/rs2/model/task/DelayTimer;"): "getAttackDelayTimer",
    ("com/rs2/model/player/Player", "Z", "()Lcom/rs2/model/task/DelayTimer;"): "getAttackDelayTimer",
    ("com/rs2/model/npc/Npc", "Z", "()Lcom/rs2/model/task/DelayTimer;"): "getAttackDelayTimer",
    ("com/rs2/bot/BotPlayer", "Z", "()Lcom/rs2/model/task/DelayTimer;"): "getAttackDelayTimer",
    ("com/rs2/model/Entity", "k", "(I)V"): "setAttackDelayTicks",
    ("com/rs2/model/player/Player", "k", "(I)V"): "setAttackDelayTicks",
    ("com/rs2/model/npc/Npc", "k", "(I)V"): "setAttackDelayTicks",
    ("com/rs2/bot/BotPlayer", "k", "(I)V"): "setAttackDelayTicks",
    ("com/rs2/model/Entity", "X", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getRecentCombatTimer",
    ("com/rs2/model/player/Player", "X", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getRecentCombatTimer",
    ("com/rs2/model/npc/Npc", "X", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getRecentCombatTimer",
    ("com/rs2/bot/BotPlayer", "X", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getRecentCombatTimer",
    ("com/rs2/model/Entity", "Y", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getSingleCombatTimer",
    ("com/rs2/model/player/Player", "Y", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getSingleCombatTimer",
    ("com/rs2/model/npc/Npc", "Y", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getSingleCombatTimer",
    ("com/rs2/bot/BotPlayer", "Y", "()Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "getSingleCombatTimer",
    ("com/rs2/model/Entity", "ae", "()Z"): "isChargeSpellActive",
    ("com/rs2/model/player/Player", "ae", "()Z"): "isChargeSpellActive",
    ("com/rs2/model/npc/Npc", "ae", "()Z"): "isChargeSpellActive",
    ("com/rs2/bot/BotPlayer", "ae", "()Z"): "isChargeSpellActive",
    ("com/rs2/model/Entity", "af", "()V"): "activateChargeSpell",
    ("com/rs2/model/player/Player", "af", "()V"): "activateChargeSpell",
    ("com/rs2/model/npc/Npc", "af", "()V"): "activateChargeSpell",
    ("com/rs2/bot/BotPlayer", "af", "()V"): "activateChargeSpell",
    ("com/rs2/model/Entity", "ag", "()Lcom/rs2/model/task/DelayTimer;"): "getChargeCooldownTimer",
    ("com/rs2/model/player/Player", "ag", "()Lcom/rs2/model/task/DelayTimer;"): "getChargeCooldownTimer",
    ("com/rs2/model/npc/Npc", "ag", "()Lcom/rs2/model/task/DelayTimer;"): "getChargeCooldownTimer",
    ("com/rs2/bot/BotPlayer", "ag", "()Lcom/rs2/model/task/DelayTimer;"): "getChargeCooldownTimer",
    ("com/rs2/model/Entity", "a", "(D)V"): "setPoisonDamage",
    ("com/rs2/model/player/Player", "a", "(D)V"): "setPoisonDamage",
    ("com/rs2/model/npc/Npc", "a", "(D)V"): "setPoisonDamage",
    ("com/rs2/bot/BotPlayer", "a", "(D)V"): "setPoisonDamage",
    ("com/rs2/model/Entity", "aE", "()D"): "getPoisonDamage",
    ("com/rs2/model/player/Player", "aE", "()D"): "getPoisonDamage",
    ("com/rs2/model/npc/Npc", "aE", "()D"): "getPoisonDamage",
    ("com/rs2/bot/BotPlayer", "aE", "()D"): "getPoisonDamage",
    ("com/rs2/model/Entity", "n", "(I)V"): "setCombatTransformNpcId",
    ("com/rs2/model/player/Player", "n", "(I)V"): "setCombatTransformNpcId",
    ("com/rs2/model/npc/Npc", "n", "(I)V"): "setCombatTransformNpcId",
    ("com/rs2/bot/BotPlayer", "n", "(I)V"): "setCombatTransformNpcId",
    ("com/rs2/model/Entity", "aF", "()I"): "getCombatTransformNpcId",
    ("com/rs2/model/player/Player", "aF", "()I"): "getCombatTransformNpcId",
    ("com/rs2/model/npc/Npc", "aF", "()I"): "getCombatTransformNpcId",
    ("com/rs2/bot/BotPlayer", "aF", "()I"): "getCombatTransformNpcId",
    ("com/rs2/model/Entity", "b", "(Z)V"): "setScriptedMovementEnabled",
    ("com/rs2/model/player/Player", "b", "(Z)V"): "setScriptedMovementEnabled",
    ("com/rs2/model/npc/Npc", "b", "(Z)V"): "setScriptedMovementEnabled",
    ("com/rs2/bot/BotPlayer", "b", "(Z)V"): "setScriptedMovementEnabled",
    ("com/rs2/model/Entity", "aG", "()Z"): "isScriptedMovementEnabled",
    ("com/rs2/model/player/Player", "aG", "()Z"): "isScriptedMovementEnabled",
    ("com/rs2/model/npc/Npc", "aG", "()Z"): "isScriptedMovementEnabled",
    ("com/rs2/bot/BotPlayer", "aG", "()Z"): "isScriptedMovementEnabled",
    ("com/rs2/model/Entity", "c", "(Z)V"): "setTargetMovementDisabled",
    ("com/rs2/model/player/Player", "c", "(Z)V"): "setTargetMovementDisabled",
    ("com/rs2/model/npc/Npc", "c", "(Z)V"): "setTargetMovementDisabled",
    ("com/rs2/bot/BotPlayer", "c", "(Z)V"): "setTargetMovementDisabled",
    ("com/rs2/model/Entity", "aH", "()Z"): "isTargetMovementDisabled",
    ("com/rs2/model/player/Player", "aH", "()Z"): "isTargetMovementDisabled",
    ("com/rs2/model/npc/Npc", "aH", "()Z"): "isTargetMovementDisabled",
    ("com/rs2/bot/BotPlayer", "aH", "()Z"): "isTargetMovementDisabled",
    ("com/rs2/model/Entity", "aI", "()Z"): "isDoorSupportNpc",
    ("com/rs2/model/player/Player", "aI", "()Z"): "isDoorSupportNpc",
    ("com/rs2/model/npc/Npc", "aI", "()Z"): "isDoorSupportNpc",
    ("com/rs2/bot/BotPlayer", "aI", "()Z"): "isDoorSupportNpc",
    ("com/rs2/model/Entity", "d", "(Z)V"): "setAutoRetaliateDisabled",
    ("com/rs2/model/player/Player", "d", "(Z)V"): "setAutoRetaliateDisabled",
    ("com/rs2/model/npc/Npc", "d", "(Z)V"): "setAutoRetaliateDisabled",
    ("com/rs2/bot/BotPlayer", "d", "(Z)V"): "setAutoRetaliateDisabled",
    ("com/rs2/model/Entity", "aJ", "()Z"): "isAutoRetaliateDisabled",
    ("com/rs2/model/player/Player", "aJ", "()Z"): "isAutoRetaliateDisabled",
    ("com/rs2/model/npc/Npc", "aJ", "()Z"): "isAutoRetaliateDisabled",
    ("com/rs2/bot/BotPlayer", "aJ", "()Z"): "isAutoRetaliateDisabled",
    ("com/rs2/model/Entity", "ah", "()Z"): "hasCombatTarget",
    ("com/rs2/model/player/Player", "ah", "()Z"): "hasCombatTarget",
    ("com/rs2/model/npc/Npc", "ah", "()Z"): "hasCombatTarget",
    ("com/rs2/bot/BotPlayer", "ah", "()Z"): "hasCombatTarget",
    ("com/rs2/model/Entity", "aC", "()V"): "clearNegativeStatusTimers",
    ("com/rs2/model/player/Player", "aC", "()V"): "clearNegativeStatusTimers",
    ("com/rs2/model/npc/Npc", "aC", "()V"): "clearNegativeStatusTimers",
    ("com/rs2/bot/BotPlayer", "aC", "()V"): "clearNegativeStatusTimers",
    ("com/rs2/model/Entity", "aD", "()V"): "clearImmunityTimers",
    ("com/rs2/model/player/Player", "aD", "()V"): "clearImmunityTimers",
    ("com/rs2/model/npc/Npc", "aD", "()V"): "clearImmunityTimers",
    ("com/rs2/bot/BotPlayer", "aD", "()V"): "clearImmunityTimers",
    ("com/rs2/model/combat/CombatTargetDelayTimer", "a", "(Lcom/rs2/model/Entity;I)V"): "setTargetDelay",
    ("com/rs2/model/combat/CombatTargetDelayTimer", "a", "()Lcom/rs2/model/Entity;"): "getTarget",
    ("com/rs2/model/Entity", "b", "(Lcom/rs2/model/Entity;)V"): "setInteractionTarget",
    ("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/Entity;)V"): "setInteractionTarget",
    ("com/rs2/model/npc/Npc", "b", "(Lcom/rs2/model/Entity;)V"): "setInteractionTarget",
    ("com/rs2/bot/BotPlayer", "b", "(Lcom/rs2/model/Entity;)V"): "setInteractionTarget",
    ("com/rs2/model/Entity", "G", "()Lcom/rs2/model/Entity;"): "getInteractionTarget",
    ("com/rs2/model/player/Player", "G", "()Lcom/rs2/model/Entity;"): "getInteractionTarget",
    ("com/rs2/model/npc/Npc", "G", "()Lcom/rs2/model/Entity;"): "getInteractionTarget",
    ("com/rs2/bot/BotPlayer", "G", "()Lcom/rs2/model/Entity;"): "getInteractionTarget",
    ("com/rs2/model/Entity", "c", "(Lcom/rs2/model/Entity;)V"): "setCombatTarget",
    ("com/rs2/model/player/Player", "c", "(Lcom/rs2/model/Entity;)V"): "setCombatTarget",
    ("com/rs2/model/npc/Npc", "c", "(Lcom/rs2/model/Entity;)V"): "setCombatTarget",
    ("com/rs2/bot/BotPlayer", "c", "(Lcom/rs2/model/Entity;)V"): "setCombatTarget",
    ("com/rs2/model/Entity", "L", "()Lcom/rs2/model/Entity;"): "getCombatTarget",
    ("com/rs2/model/player/Player", "L", "()Lcom/rs2/model/Entity;"): "getCombatTarget",
    ("com/rs2/model/npc/Npc", "L", "()Lcom/rs2/model/Entity;"): "getCombatTarget",
    ("com/rs2/bot/BotPlayer", "L", "()Lcom/rs2/model/Entity;"): "getCombatTarget",
    ("com/rs2/model/Entity", "d", "(Lcom/rs2/model/Entity;)V"): "setMovementTarget",
    ("com/rs2/model/player/Player", "d", "(Lcom/rs2/model/Entity;)V"): "setMovementTarget",
    ("com/rs2/model/npc/Npc", "d", "(Lcom/rs2/model/Entity;)V"): "setMovementTarget",
    ("com/rs2/bot/BotPlayer", "d", "(Lcom/rs2/model/Entity;)V"): "setMovementTarget",
    ("com/rs2/model/Entity", "M", "()Lcom/rs2/model/Entity;"): "getMovementTarget",
    ("com/rs2/model/player/Player", "M", "()Lcom/rs2/model/Entity;"): "getMovementTarget",
    ("com/rs2/model/npc/Npc", "M", "()Lcom/rs2/model/Entity;"): "getMovementTarget",
    ("com/rs2/bot/BotPlayer", "M", "()Lcom/rs2/model/Entity;"): "getMovementTarget",
    ("com/rs2/model/Entity", "i", "(I)V"): "setAttackRange",
    ("com/rs2/model/player/Player", "i", "(I)V"): "setAttackRange",
    ("com/rs2/model/npc/Npc", "i", "(I)V"): "setAttackRange",
    ("com/rs2/bot/BotPlayer", "i", "(I)V"): "setAttackRange",
    ("com/rs2/model/Entity", "O", "()I"): "getAttackRange",
    ("com/rs2/model/player/Player", "O", "()I"): "getAttackRange",
    ("com/rs2/model/npc/Npc", "O", "()I"): "getAttackRange",
    ("com/rs2/bot/BotPlayer", "O", "()I"): "getAttackRange",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/task/CycleEvent;)V"): "setActiveCycleEvent",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/task/CycleEvent;)V"): "setActiveCycleEvent",
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/task/CycleEvent;)V"): "setActiveCycleEvent",
    ("com/rs2/bot/BotPlayer", "a", "(Lcom/rs2/model/task/CycleEvent;)V"): "setActiveCycleEvent",
    ("com/rs2/model/Entity", "ab", "()Lcom/rs2/model/task/CycleEvent;"): "getActiveCycleEvent",
    ("com/rs2/model/player/Player", "ab", "()Lcom/rs2/model/task/CycleEvent;"): "getActiveCycleEvent",
    ("com/rs2/model/npc/Npc", "ab", "()Lcom/rs2/model/task/CycleEvent;"): "getActiveCycleEvent",
    ("com/rs2/bot/BotPlayer", "ab", "()Lcom/rs2/model/task/CycleEvent;"): "getActiveCycleEvent",
    ("com/rs2/model/Entity", "ac", "()I"): "nextActionSequence",
    ("com/rs2/model/player/Player", "ac", "()I"): "nextActionSequence",
    ("com/rs2/model/npc/Npc", "ac", "()I"): "nextActionSequence",
    ("com/rs2/bot/BotPlayer", "ac", "()I"): "nextActionSequence",
    ("com/rs2/model/Entity", "l", "(I)Z"): "isCurrentActionSequence",
    ("com/rs2/model/player/Player", "l", "(I)Z"): "isCurrentActionSequence",
    ("com/rs2/model/npc/Npc", "l", "(I)Z"): "isCurrentActionSequence",
    ("com/rs2/bot/BotPlayer", "l", "(I)Z"): "isCurrentActionSequence",
    ("com/rs2/model/Entity", "U", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementDelayTimer",
    ("com/rs2/model/player/Player", "U", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementDelayTimer",
    ("com/rs2/model/npc/Npc", "U", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementDelayTimer",
    ("com/rs2/bot/BotPlayer", "U", "()Lcom/rs2/model/task/DelayTimer;"): "getMovementDelayTimer",
    ("com/rs2/model/Entity", "ak", "()V"): "beginInterruptibleAction",
    ("com/rs2/model/player/Player", "ak", "()V"): "beginInterruptibleAction",
    ("com/rs2/model/npc/Npc", "ak", "()V"): "beginInterruptibleAction",
    ("com/rs2/bot/BotPlayer", "ak", "()V"): "beginInterruptibleAction",
    ("com/rs2/model/Entity", "al", "()V"): "invalidateInterruptibleAction",
    ("com/rs2/model/player/Player", "al", "()V"): "invalidateInterruptibleAction",
    ("com/rs2/model/npc/Npc", "al", "()V"): "invalidateInterruptibleAction",
    ("com/rs2/bot/BotPlayer", "al", "()V"): "invalidateInterruptibleAction",
    ("com/rs2/model/Entity", "am", "()Z"): "isInterruptibleActionActive",
    ("com/rs2/model/player/Player", "am", "()Z"): "isInterruptibleActionActive",
    ("com/rs2/model/npc/Npc", "am", "()Z"): "isInterruptibleActionActive",
    ("com/rs2/bot/BotPlayer", "am", "()Z"): "isInterruptibleActionActive",
    ("com/rs2/model/Entity", "a", "(ILcom/rs2/model/combat/hit/HitType;)V"): "applyDirectHit",
    ("com/rs2/model/player/Player", "a", "(ILcom/rs2/model/combat/hit/HitType;)V"): "applyDirectHit",
    ("com/rs2/model/npc/Npc", "a", "(ILcom/rs2/model/combat/hit/HitType;)V"): "applyDirectHit",
    ("com/rs2/bot/BotPlayer", "a", "(ILcom/rs2/model/combat/hit/HitType;)V"): "applyDirectHit",
    ("com/rs2/model/Entity", "j", "()V"): "pruneExpiredDamageContributions",
    ("com/rs2/model/player/Player", "j", "()V"): "pruneExpiredDamageContributions",
    ("com/rs2/model/npc/Npc", "j", "()V"): "pruneExpiredDamageContributions",
    ("com/rs2/bot/BotPlayer", "j", "()V"): "pruneExpiredDamageContributions",
    ("com/rs2/model/Entity", "aa", "()Ljava/util/Queue;"): "getDamageContributions",
    ("com/rs2/model/player/Player", "aa", "()Ljava/util/Queue;"): "getDamageContributions",
    ("com/rs2/model/npc/Npc", "aa", "()Ljava/util/Queue;"): "getDamageContributions",
    ("com/rs2/bot/BotPlayer", "aa", "()Ljava/util/Queue;"): "getDamageContributions",
    ("com/rs2/model/Entity", "V", "()Lcom/rs2/model/Entity;"): "getTopDamageContributor",
    ("com/rs2/model/player/Player", "V", "()Lcom/rs2/model/Entity;"): "getTopDamageContributor",
    ("com/rs2/model/npc/Npc", "V", "()Lcom/rs2/model/Entity;"): "getTopDamageContributor",
    ("com/rs2/bot/BotPlayer", "V", "()Lcom/rs2/model/Entity;"): "getTopDamageContributor",
    ("com/rs2/model/Entity", "W", "()Ljava/util/ArrayList;"): "getDamageContributorList",
    ("com/rs2/model/player/Player", "W", "()Ljava/util/ArrayList;"): "getDamageContributorList",
    ("com/rs2/model/npc/Npc", "W", "()Ljava/util/ArrayList;"): "getDamageContributorList",
    ("com/rs2/bot/BotPlayer", "W", "()Ljava/util/ArrayList;"): "getDamageContributorList",
    ("com/rs2/model/Entity", "e", "(Lcom/rs2/model/Entity;)Lcom/rs2/model/combat/hit/DamageContribution;"): "getDamageContribution",
    ("com/rs2/model/player/Player", "e", "(Lcom/rs2/model/Entity;)Lcom/rs2/model/combat/hit/DamageContribution;"): "getDamageContribution",
    ("com/rs2/model/npc/Npc", "e", "(Lcom/rs2/model/Entity;)Lcom/rs2/model/combat/hit/DamageContribution;"): "getDamageContribution",
    ("com/rs2/bot/BotPlayer", "e", "(Lcom/rs2/model/Entity;)Lcom/rs2/model/combat/hit/DamageContribution;"): "getDamageContribution",
    ("com/rs2/model/Entity", "e", "(I)I"): "getCombatBonus",
    ("com/rs2/model/player/Player", "e", "(I)I"): "getCombatBonus",
    ("com/rs2/model/npc/Npc", "e", "(I)I"): "getCombatBonus",
    ("com/rs2/bot/BotPlayer", "e", "(I)I"): "getCombatBonus",
    ("com/rs2/model/Entity", "aj", "()Lcom/rs2/model/EntityTargetMovement;"): "getTargetMovement",
    ("com/rs2/model/player/Player", "aj", "()Lcom/rs2/model/EntityTargetMovement;"): "getTargetMovement",
    ("com/rs2/model/npc/Npc", "aj", "()Lcom/rs2/model/EntityTargetMovement;"): "getTargetMovement",
    ("com/rs2/bot/BotPlayer", "aj", "()Lcom/rs2/model/EntityTargetMovement;"): "getTargetMovement",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/Entity;)V"): "dropDeathItems",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/Entity;)V"): "dropDeathItems",
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/Entity;)V"): "dropDeathItems",
    ("com/rs2/bot/BotPlayer", "a", "(Lcom/rs2/model/Entity;)V"): "dropDeathItems",
    ("com/rs2/model/Entity", "b", "(I)V"): "heal",
    ("com/rs2/model/player/Player", "b", "(I)V"): "heal",
    ("com/rs2/model/npc/Npc", "b", "(I)V"): "heal",
    ("com/rs2/bot/BotPlayer", "b", "(I)V"): "heal",
    ("com/rs2/model/Entity", "b", "(Lcom/rs2/model/Position;)V"): "moveTo",
    ("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/Position;)V"): "moveTo",
    ("com/rs2/model/npc/Npc", "b", "(Lcom/rs2/model/Position;)V"): "moveTo",
    ("com/rs2/bot/BotPlayer", "b", "(Lcom/rs2/model/Position;)V"): "moveTo",
    ("com/rs2/model/player/Player", "a", "(III)V"): "moveToGroundPosition",
    ("com/rs2/model/player/Player", "d", "(Lcom/rs2/model/Position;)V"): "scheduleDelayedMove",
    ("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/Position;Z)V"): "moveToInstancedPosition",
    ("com/rs2/model/player/Player", "e", "(Lcom/rs2/model/Position;)V"): "moveToPreservingInteractionState",
    ("com/rs2/model/player/Player", "er", "()V"): "resetInteractionState",
    ("com/rs2/model/Entity", "m", "(I)V"): "setReferenceId",
    ("com/rs2/model/player/Player", "m", "(I)V"): "setReferenceId",
    ("com/rs2/model/npc/Npc", "m", "(I)V"): "setReferenceId",
    ("com/rs2/bot/BotPlayer", "m", "(I)V"): "setReferenceId",
    ("com/rs2/model/Entity", "an", "()I"): "getReferenceId",
    ("com/rs2/model/player/Player", "an", "()I"): "getReferenceId",
    ("com/rs2/model/npc/Npc", "an", "()I"): "getReferenceId",
    ("com/rs2/bot/BotPlayer", "an", "()I"): "getReferenceId",
    ("com/rs2/model/Entity", "c", "(Lcom/rs2/model/Position;)V"): "setDeathPosition",
    ("com/rs2/model/player/Player", "c", "(Lcom/rs2/model/Position;)V"): "setDeathPosition",
    ("com/rs2/model/npc/Npc", "c", "(Lcom/rs2/model/Position;)V"): "setDeathPosition",
    ("com/rs2/bot/BotPlayer", "c", "(Lcom/rs2/model/Position;)V"): "setDeathPosition",
    ("com/rs2/model/Entity", "ao", "()Lcom/rs2/model/Position;"): "getDeathPosition",
    ("com/rs2/model/player/Player", "ao", "()Lcom/rs2/model/Position;"): "getDeathPosition",
    ("com/rs2/model/npc/Npc", "ao", "()Lcom/rs2/model/Position;"): "getDeathPosition",
    ("com/rs2/bot/BotPlayer", "ao", "()Lcom/rs2/model/Position;"): "getDeathPosition",
    ("com/rs2/model/Entity", "g", "(Lcom/rs2/model/Entity;)V"): "setTradePartner",
    ("com/rs2/model/player/Player", "g", "(Lcom/rs2/model/Entity;)V"): "setTradePartner",
    ("com/rs2/model/npc/Npc", "g", "(Lcom/rs2/model/Entity;)V"): "setTradePartner",
    ("com/rs2/bot/BotPlayer", "g", "(Lcom/rs2/model/Entity;)V"): "setTradePartner",
    ("com/rs2/model/Entity", "ap", "()Lcom/rs2/model/Entity;"): "getTradePartner",
    ("com/rs2/model/player/Player", "ap", "()Lcom/rs2/model/Entity;"): "getTradePartner",
    ("com/rs2/model/npc/Npc", "ap", "()Lcom/rs2/model/Entity;"): "getTradePartner",
    ("com/rs2/bot/BotPlayer", "ap", "()Lcom/rs2/model/Entity;"): "getTradePartner",
    ("com/rs2/model/Entity", "f", "(I)V"): "setIndex",
    ("com/rs2/model/player/Player", "f", "(I)V"): "setIndex",
    ("com/rs2/model/npc/Npc", "f", "(I)V"): "setIndex",
    ("com/rs2/bot/BotPlayer", "f", "(I)V"): "setIndex",
    ("com/rs2/model/Entity", "C", "()I"): "getIndex",
    ("com/rs2/model/player/Player", "C", "()I"): "getIndex",
    ("com/rs2/model/npc/Npc", "C", "()I"): "getIndex",
    ("com/rs2/bot/BotPlayer", "C", "()I"): "getIndex",
    ("com/rs2/model/Entity", "g", "(I)V"): "setEncodedIndex",
    ("com/rs2/model/player/Player", "g", "(I)V"): "setEncodedIndex",
    ("com/rs2/model/npc/Npc", "g", "(I)V"): "setEncodedIndex",
    ("com/rs2/bot/BotPlayer", "g", "(I)V"): "setEncodedIndex",
    ("com/rs2/model/Entity", "D", "()I"): "getEncodedIndex",
    ("com/rs2/model/player/Player", "D", "()I"): "getEncodedIndex",
    ("com/rs2/model/npc/Npc", "D", "()I"): "getEncodedIndex",
    ("com/rs2/bot/BotPlayer", "D", "()I"): "getEncodedIndex",
    ("com/rs2/model/Entity", "E", "()Z"): "isPlayer",
    ("com/rs2/model/player/Player", "E", "()Z"): "isPlayer",
    ("com/rs2/model/npc/Npc", "E", "()Z"): "isPlayer",
    ("com/rs2/bot/BotPlayer", "E", "()Z"): "isPlayer",
    ("com/rs2/model/Entity", "F", "()Z"): "isNpc",
    ("com/rs2/model/player/Player", "F", "()Z"): "isNpc",
    ("com/rs2/model/npc/Npc", "F", "()Z"): "isNpc",
    ("com/rs2/bot/BotPlayer", "F", "()Z"): "isNpc",
    ("com/rs2/model/Entity", "I", "()Lcom/rs2/model/EntityUpdateState;"): "getUpdateState",
    ("com/rs2/model/player/Player", "I", "()Lcom/rs2/model/EntityUpdateState;"): "getUpdateState",
    ("com/rs2/model/npc/Npc", "I", "()Lcom/rs2/model/EntityUpdateState;"): "getUpdateState",
    ("com/rs2/bot/BotPlayer", "I", "()Lcom/rs2/model/EntityUpdateState;"): "getUpdateState",
    ("com/rs2/model/Entity", "a", "(Z)V"): "setDead",
    ("com/rs2/model/player/Player", "a", "(Z)V"): "setDead",
    ("com/rs2/model/npc/Npc", "a", "(Z)V"): "setDead",
    ("com/rs2/bot/BotPlayer", "a", "(Z)V"): "setDead",
    ("com/rs2/model/Entity", "J", "()Z"): "isDead",
    ("com/rs2/model/player/Player", "J", "()Z"): "isDead",
    ("com/rs2/model/npc/Npc", "J", "()Z"): "isDead",
    ("com/rs2/bot/BotPlayer", "J", "()Z"): "isDead",
    ("com/rs2/model/Entity", "K", "()Ljava/util/Map;"): "getAttributes",
    ("com/rs2/model/player/Player", "K", "()Ljava/util/Map;"): "getAttributes",
    ("com/rs2/model/npc/Npc", "K", "()Ljava/util/Map;"): "getAttributes",
    ("com/rs2/bot/BotPlayer", "K", "()Ljava/util/Map;"): "getAttributes",
    ("com/rs2/model/Entity", "h", "(I)V"): "setSize",
    ("com/rs2/model/player/Player", "h", "(I)V"): "setSize",
    ("com/rs2/model/npc/Npc", "h", "(I)V"): "setSize",
    ("com/rs2/bot/BotPlayer", "h", "(I)V"): "setSize",
    ("com/rs2/model/Entity", "N", "()I"): "getSize",
    ("com/rs2/model/player/Player", "N", "()I"): "getSize",
    ("com/rs2/model/npc/Npc", "N", "()I"): "getSize",
    ("com/rs2/bot/BotPlayer", "N", "()I"): "getSize",
    ("com/rs2/model/Entity", "c", "(I)V"): "setWalkDirection",
    ("com/rs2/model/player/Player", "c", "(I)V"): "setWalkDirection",
    ("com/rs2/model/npc/Npc", "c", "(I)V"): "setWalkDirection",
    ("com/rs2/bot/BotPlayer", "c", "(I)V"): "setWalkDirection",
    ("com/rs2/model/Entity", "k", "()I"): "getWalkDirection",
    ("com/rs2/model/player/Player", "k", "()I"): "getWalkDirection",
    ("com/rs2/model/npc/Npc", "k", "()I"): "getWalkDirection",
    ("com/rs2/bot/BotPlayer", "k", "()I"): "getWalkDirection",
    ("com/rs2/model/Entity", "d", "(I)V"): "setRunDirection",
    ("com/rs2/model/player/Player", "d", "(I)V"): "setRunDirection",
    ("com/rs2/model/npc/Npc", "d", "(I)V"): "setRunDirection",
    ("com/rs2/bot/BotPlayer", "d", "(I)V"): "setRunDirection",
    ("com/rs2/model/Entity", "l", "()I"): "getRunDirection",
    ("com/rs2/model/player/Player", "l", "()I"): "getRunDirection",
    ("com/rs2/model/npc/Npc", "l", "()I"): "getRunDirection",
    ("com/rs2/bot/BotPlayer", "l", "()I"): "getRunDirection",
    ("com/rs2/model/Entity", "P", "()I"): "getCurrentHitpoints",
    ("com/rs2/model/player/Player", "P", "()I"): "getCurrentHitpoints",
    ("com/rs2/model/npc/Npc", "P", "()I"): "getCurrentHitpoints",
    ("com/rs2/bot/BotPlayer", "P", "()I"): "getCurrentHitpoints",
    ("com/rs2/model/Entity", "j", "(I)V"): "setCurrentHitpoints",
    ("com/rs2/model/player/Player", "j", "(I)V"): "setCurrentHitpoints",
    ("com/rs2/model/npc/Npc", "j", "(I)V"): "setCurrentHitpoints",
    ("com/rs2/bot/BotPlayer", "j", "(I)V"): "setCurrentHitpoints",
    ("com/rs2/model/Entity", "Q", "()I"): "getMaxHitpoints",
    ("com/rs2/model/player/Player", "Q", "()I"): "getMaxHitpoints",
    ("com/rs2/model/npc/Npc", "Q", "()I"): "getMaxHitpoints",
    ("com/rs2/bot/BotPlayer", "Q", "()I"): "getMaxHitpoints",
    ("com/rs2/model/Entity", "R", "()I"): "getDeathAnimationId",
    ("com/rs2/model/player/Player", "R", "()I"): "getDeathAnimationId",
    ("com/rs2/model/npc/Npc", "R", "()I"): "getDeathAnimationId",
    ("com/rs2/bot/BotPlayer", "R", "()I"): "getDeathAnimationId",
    ("com/rs2/model/Entity", "S", "()I"): "getBlockAnimationId",
    ("com/rs2/model/player/Player", "S", "()I"): "getBlockAnimationId",
    ("com/rs2/model/npc/Npc", "S", "()I"): "getBlockAnimationId",
    ("com/rs2/bot/BotPlayer", "S", "()I"): "getBlockAnimationId",
    ("com/rs2/model/Entity", "T", "()I"): "getDeathDelayTicks",
    ("com/rs2/model/player/Player", "T", "()I"): "getDeathDelayTicks",
    ("com/rs2/model/npc/Npc", "T", "()I"): "getDeathDelayTicks",
    ("com/rs2/bot/BotPlayer", "T", "()I"): "getDeathDelayTicks",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/combat/CombatType;)I"): "getAttackLevelFor",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/combat/CombatType;)I"): "getAttackLevelFor",
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/combat/CombatType;)I"): "getAttackLevelFor",
    ("com/rs2/bot/BotPlayer", "a", "(Lcom/rs2/model/combat/CombatType;)I"): "getAttackLevelFor",
    ("com/rs2/model/Entity", "b", "(Lcom/rs2/model/combat/CombatType;)I"): "getDefenceLevelFor",
    ("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/combat/CombatType;)I"): "getDefenceLevelFor",
    ("com/rs2/model/npc/Npc", "b", "(Lcom/rs2/model/combat/CombatType;)I"): "getDefenceLevelFor",
    ("com/rs2/bot/BotPlayer", "b", "(Lcom/rs2/model/combat/CombatType;)I"): "getDefenceLevelFor",
    ("com/rs2/model/Entity", "c", "(Lcom/rs2/model/combat/CombatType;)Z"): "isProtectedFrom",
    ("com/rs2/model/player/Player", "c", "(Lcom/rs2/model/combat/CombatType;)Z"): "isProtectedFrom",
    ("com/rs2/model/npc/Npc", "c", "(Lcom/rs2/model/combat/CombatType;)Z"): "isProtectedFrom",
    ("com/rs2/bot/BotPlayer", "c", "(Lcom/rs2/model/combat/CombatType;)Z"): "isProtectedFrom",
    ("com/rs2/model/animation/GraphicEffect", "a", "(I)Lcom/rs2/model/animation/GraphicEffect;"): "createHeight100",
    ("com/rs2/model/animation/GraphicEffect", "b", "(I)Lcom/rs2/model/animation/GraphicEffect;"): "createHeight0",
    ("com/rs2/model/animation/GraphicEffect", "a", "()I"): "getId",
    ("com/rs2/model/animation/GraphicEffect", "b", "()I"): "getPackedDelay",
    ("com/rs2/model/animation/GraphicEffect", "c", "(I)Lcom/rs2/model/animation/GraphicEffect;"): "withDelay15",
    ("com/rs2/model/update/EntityUpdateOverrideDefinition", "a", "(ILcom/rs2/model/update/EntityUpdateOverrideType;)Lcom/rs2/model/update/EntityUpdateOverrideDefinition;"): "forOriginalIdAndType",
    ("com/rs2/model/update/EntityUpdateOverrideDefinition", "a", "()I"): "getReplacementId",
    ("com/rs2/model/c/ProjectileDefinition", "a", "(Lcom/rs2/model/player/Player;)V"): "startSheepShearing",
    ("com/rs2/model/c/ProjectileDefinition", "a", "()Lcom/rs2/model/combat/ProjectileTiming;"): "getTiming",
    ("com/rs2/model/c/ProjectileDefinition", "b", "()I"): "getProjectileId",
    ("com/rs2/model/c/ProjectileDefinition", "c", "()V"): "registerNpcCombatDefinitions",
    ("com/rs2/model/c/ProjectileDefinition", "d", "()Lcom/rs2/model/npc/combat/NpcCombatDefinition;"): "createSaradominWizardCombatDefinition",
    ("com/rs2/model/c/ProjectileDefinition", "e", "()Lcom/rs2/model/npc/combat/NpcCombatDefinition;"): "createZamorakWizardCombatDefinition",
    ("com/rs2/model/combat/ProjectileTiming", "a", "()I"): "getStartDelay",
    ("com/rs2/model/combat/ProjectileTiming", "b", "()I"): "getSpeed",
    ("com/rs2/model/combat/ProjectileTiming", "c", "()I"): "getStartHeight",
    ("com/rs2/model/combat/ProjectileTiming", "d", "()I"): "getEndHeight",
    ("com/rs2/model/combat/ProjectileTiming", "e", "()I"): "getSlope",
    ("com/rs2/model/combat/ProjectileTiming", "a", "(I)Lcom/rs2/model/combat/ProjectileTiming;"): "setStartDelay",
    ("com/rs2/model/combat/ProjectileTiming", "b", "(I)Lcom/rs2/model/combat/ProjectileTiming;"): "setSpeed",
    ("com/rs2/model/combat/ProjectileTiming", "f", "()Lcom/rs2/model/combat/ProjectileTiming;"): "copy",
    ("com/rs2/net/packet/PacketSender", "a", "(Lcom/rs2/model/Position;IIBBIIIIII)Lcom/rs2/net/packet/PacketSender;"): "sendProjectile",
    ("com/rs2/model/combat/AttackBonusType", "a", "()I"): "getIndex",
    ("com/rs2/model/combat/AttackXpMode", "a", "()[I"): "getSkillIds",
    ("com/rs2/model/combat/AttackStyleDefinition", "a", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/AttackStyleDefinition", "b", "()I"): "getButtonId",
    ("com/rs2/model/combat/AttackStyleDefinition", "c", "()Lcom/rs2/model/combat/AttackXpMode;"): "getXpMode",
    ("com/rs2/model/combat/AttackStyleDefinition", "d", "()Lcom/rs2/model/combat/AttackBonusType;"): "getAttackBonusType",
    ("com/rs2/model/combat/AttackStyleDefinition", "e", "()I"): "getAttackBonusIndex",
    ("com/rs2/model/combat/AttackStyleDefinition", "f", "()I"): "getDefenseBonusIndex",
    ("com/rs2/model/combat/WeaponProfile", "a", "()Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "getInterfaceDefinition",
    ("com/rs2/model/combat/WeaponProfile", "b", "()[I"): "getAttackAnimations",
    ("com/rs2/model/combat/WeaponProfile", "c", "()[I"): "getMovementAnimations",
    ("com/rs2/model/combat/WeaponProfile", "d", "()I"): "getAttackDelay",
    ("com/rs2/model/combat/WeaponProfile", "e", "()I"): "getBlockAnimationId",
    ("com/rs2/model/combat/WeaponProfile", "f", "()Lcom/rs2/model/combat/AmmunitionProfile;"): "getAmmunitionProfile",
    ("com/rs2/model/combat/WeaponProfile", "a", "(Lcom/rs2/model/item/ItemStack;)Lcom/rs2/model/combat/WeaponProfile;"): "forItem",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "a", "()I"): "getSpecialAttackButtonId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "b", "()I"): "getSidebarInterfaceId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "c", "()I"): "getSpecialBarWidgetId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "d", "()I"): "getWeaponNameTextId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "e", "()I"): "getWeaponModelWidgetId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "f", "()I"): "getSpecialEnergyWidgetId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "g", "()[Lcom/rs2/model/combat/AttackStyleDefinition;"): "getAttackStyles",
    ("com/rs2/model/combat/AmmunitionProfile", "a", "()Lcom/rs2/model/combat/ProjectileTiming;"): "getProjectileTiming",
    ("com/rs2/model/combat/AmmunitionProfile", "b", "()[Lcom/rs2/model/combat/AmmunitionDefinition;"): "getAllowedAmmunition",
    ("com/rs2/model/combat/AmmunitionProfile", "c", "()I"): "getEquipmentSlot",
    ("com/rs2/model/combat/AmmunitionProfile", "d", "()I"): "getGraphicDelay",
    ("com/rs2/model/combat/AmmunitionDefinition", "a", "()I"): "getRangedStrength",
    ("com/rs2/model/combat/AmmunitionDefinition", "b", "()I"): "getProjectileId",
    ("com/rs2/model/combat/AmmunitionDefinition", "c", "()I"): "getGraphicId",
    ("com/rs2/model/combat/AmmunitionDefinition", "d", "()I"): "getAlternateGraphicId",
    ("com/rs2/model/combat/AmmunitionDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;Lcom/rs2/model/item/ItemStack;)Z"): "isCompatible",
    ("com/rs2/model/combat/AmmunitionDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/combat/WeaponProfile;Z)Lcom/rs2/model/combat/AmmunitionDefinition;"): "findEquippedAmmunition",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/combat/WeaponProfile;)V"): "setWeaponProfile",
    ("com/rs2/model/player/Player", "dV", "()Lcom/rs2/model/combat/WeaponProfile;"): "getWeaponProfile",
    ("com/rs2/model/player/EquipmentManager", "e", "()I"): "getStandAnimation",
    ("com/rs2/model/player/EquipmentManager", "f", "()I"): "getWalkAnimation",
    ("com/rs2/model/player/EquipmentManager", "g", "()I"): "getRunAnimation",
    ("com/rs2/model/combat/attack/CombatAttack", "a", "()Lcom/rs2/model/Entity;"): "getAttacker",
    ("com/rs2/model/combat/attack/CombatAttack", "b", "()Lcom/rs2/model/Entity;"): "getTarget",
    ("com/rs2/model/combat/attack/CombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/MagicCombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "c", "()Lcom/rs2/model/combat/CombatType;"): "getCombatType",
    ("com/rs2/model/combat/attack/CombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/MagicCombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "d", "()I"): "getAttackRange",
    ("com/rs2/model/combat/attack/CombatAttack", "a", "(Lcom/rs2/model/task/CycleEventContainer;)I"): "execute",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "(Lcom/rs2/model/task/CycleEventContainer;)I"): "execute",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "a", "(Lcom/rs2/model/task/CycleEventContainer;)I"): "execute",
    ("com/rs2/model/combat/attack/MagicCombatAttack", "a", "(Lcom/rs2/model/task/CycleEventContainer;)I"): "execute",
    ("com/rs2/model/combat/special/MagicShortbowSpecialAttack", "a", "(Lcom/rs2/model/task/CycleEventContainer;)I"): "execute",
    ("com/rs2/model/combat/attack/CombatAttack", "e", "()Lcom/rs2/model/combat/attack/CombatAttackState;"): "getState",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "e", "()Lcom/rs2/model/combat/attack/CombatAttackState;"): "getState",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "e", "()Lcom/rs2/model/combat/attack/CombatAttackState;"): "getState",
    ("com/rs2/model/combat/attack/MagicCombatAttack", "e", "()Lcom/rs2/model/combat/attack/CombatAttackState;"): "getState",
    ("com/rs2/model/combat/attack/CombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/MagicCombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "f", "()V"): "prepare",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "g", "()V"): "onRequirementFailed",
    ("com/rs2/model/combat/attack/MagicCombatAttack", "g", "()V"): "onRequirementFailed",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "([Lcom/rs2/model/combat/requirement/CombatRequirement;)V"): "setRequirements",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "([Lcom/rs2/model/combat/hit/HitDefinition;)V"): "setHitDefinitions",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "h", "()[Lcom/rs2/model/combat/hit/HitDefinition;"): "getHitDefinitions",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "(I)V"): "setAttackDelay",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "(Lcom/rs2/model/animation/GraphicEffect;)V"): "setAttackerGraphic",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "b", "(I)V"): "setAnimationId",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "c", "(I)V"): "setAttackSoundId",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "(Lcom/rs2/model/combat/effect/CombatEffect;)Lcom/rs2/model/combat/attack/BaseCombatAttack;"): "addEffect",
    ("com/rs2/model/combat/attack/CombatAttackProvider", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)[Lcom/rs2/model/combat/attack/CombatAttack;"): "createAttacks",
    ("com/rs2/model/combat/attack/DefaultCombatAttackProvider", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)[Lcom/rs2/model/combat/attack/CombatAttack;"): "createAttacks",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "i", "()Lcom/rs2/model/combat/AmmunitionDefinition;"): "getAmmunition",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "j", "()D"): "calculateMaxHit",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "k", "()Lcom/rs2/model/combat/AttackStyleDefinition;"): "getAttackStyle",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/AbyssalWhipSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/ArmadylGodswordSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/BandosGodswordSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DarkBowSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DarklightSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/Dragon2hSwordSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DragonAxeSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DragonDaggerSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DragonHalberdSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DragonLongswordSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DragonMaceSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DragonScimitarSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/DragonSpearSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/GraniteMaulSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/MagicLongbowSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/MagicShortbowSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/RuneClawsSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/RuneThrownaxeSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/SaradominGodswordSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/SaradominSwordSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/SeercullSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/ZamorakGodswordSpecialAttack", "l", "()Z"): "prepareSpecialAttack",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "a", "()B"): "getEnergyCost",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "a", "(Lcom/rs2/model/item/ItemStack;)Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "forItem",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "a", "(Lcom/rs2/model/player/Player;)V"): "performDragonBattleaxeSpecial",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "b", "(Lcom/rs2/model/player/Player;)V"): "performExcaliburSpecial",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "c", "(Lcom/rs2/model/player/Player;)V"): "performGraniteMaulSpecial",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/hit/HitDefinition;I)V"): "applyHitSpecialEffect",
    ("com/rs2/model/combat/special/AbyssalWhipSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/ArmadylGodswordSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/BandosGodswordSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DarkBowSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DarklightSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/Dragon2hSwordSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonAxeSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonBattleaxeSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonDaggerSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonHalberdSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonLongswordSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonMaceSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonScimitarSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/DragonSpearSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/ExcaliburSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/GraniteMaulSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/MagicLongbowSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/MagicShortbowSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/RuneClawsSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/RuneThrownaxeSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/SaradominGodswordSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/SaradominSwordSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/SeercullSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/special/ZamorakGodswordSpecialDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/WeaponCombatAttack;"): "createAttack",
    ("com/rs2/model/combat/CombatCycleEvent", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)Lcom/rs2/model/combat/AttackValidationResult;"): "validateAttack",
    ("com/rs2/model/combat/CombatManager", "c", "()Lcom/rs2/model/combat/CombatManager;"): "getInstance",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/combat/CombatAction;)V"): "queueAction",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)V"): "startCombat",
    ("com/rs2/model/combat/CombatManager", "b", "(Lcom/rs2/model/Entity;)V"): "stopCombat",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/Entity;)V"): "handleDeath",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Z)V"): "finishDeath",
    ("com/rs2/model/combat/CombatManager", "b", "()V"): "initialize",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/combat/attack/WeaponCombatAttack;)D"): "calculateWeaponMaxHit",
    ("com/rs2/model/combat/CombatManager", "b", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/combat/attack/WeaponCombatAttack;)D"): "calculateMeleeMaxHit",
    ("com/rs2/model/combat/CombatManager", "a", "(DD)D"): "calculateHitChance",
    ("com/rs2/model/combat/CombatManager", "a", "(D)Z"): "rollAccuracy",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/combat/hit/HitDefinition;)D"): "calculateDefenceRoll",
    ("com/rs2/model/combat/CombatManager", "b", "(Lcom/rs2/model/Entity;Lcom/rs2/model/combat/hit/HitDefinition;)D"): "calculateAttackRoll",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/player/Player;I)D"): "getPrayerMultiplier",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/combat/attack/WeaponCombatAttack;)I"): "calculateMeleeMaxHit",
    ("com/rs2/model/combat/CombatManager", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/skill/magic/SpellDefinition;)I"): "calculateSpellMaxHit",
    ("com/rs2/model/combat/CombatAction", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)Z"): "handlePickpocketAttempt",
    ("com/rs2/model/combat/CombatAction", "a", "()V"): "tickDelay",
    ("com/rs2/model/combat/CombatAction", "b", "()V"): "queue",
    ("com/rs2/model/combat/CombatAction", "a", "(Z)V"): "queue",
    ("com/rs2/model/combat/CombatAction", "c", "()V"): "applyHitUpdate",
    ("com/rs2/model/combat/CombatAction", "d", "()V"): "applyHit",
    ("com/rs2/model/combat/CombatAction", "e", "()Z"): "isReady",
    ("com/rs2/model/combat/CombatAction", "f", "()Z"): "canTargetTakeDamage",
    ("com/rs2/model/combat/CombatAction", "g", "()Lcom/rs2/model/Entity;"): "getTarget",
    ("com/rs2/model/combat/CombatAction", "h", "()Lcom/rs2/model/Entity;"): "getAttacker",
    ("com/rs2/model/combat/CombatAction", "i", "()I"): "getDamage",
    ("com/rs2/model/World", "a", "()V"): "processTick",
    ("com/rs2/model/World", "a", "(Lcom/rs2/model/player/Player;)V"): "logoutBotAndScheduleRelogin",
    ("com/rs2/model/World", "b", "(Lcom/rs2/model/player/Player;)V"): "registerPlayer",
    ("com/rs2/model/World", "c", "(Lcom/rs2/model/player/Player;)V"): "unregisterPlayer",
    ("com/rs2/model/World", "a", "(Lcom/rs2/model/npc/Npc;)V"): "registerNpc",
    ("com/rs2/model/World", "b", "(Lcom/rs2/model/npc/Npc;)V"): "unregisterNpc",
    ("com/rs2/model/World", "b", "()I"): "getPlayerCount",
    ("com/rs2/model/World", "c", "()I"): "getNonBotPlayerCount",
    ("com/rs2/model/World", "c", "(Lcom/rs2/model/npc/Npc;)Z"): "hasNearbyNonBotPlayer",
    ("com/rs2/model/World", "d", "()I"): "getAdminCount",
    ("com/rs2/model/World", "e", "()I"): "getModeratorCount",
    ("com/rs2/model/World", "a", "(Ljava/lang/String;)Lcom/rs2/model/player/Player;"): "findPlayerByUsername",
    ("com/rs2/model/World", "a", "(Z)V"): "logoutBotsAndScheduleShutdown",
    ("com/rs2/model/World", "a", "(Lcom/rs2/model/task/TickTask;)V"): "scheduleTickTask",
    ("com/rs2/model/World", "f", "()[Lcom/rs2/model/player/Player;"): "getPlayers",
    ("com/rs2/model/World", "g", "()[Lcom/rs2/model/npc/Npc;"): "getNpcs",
    ("com/rs2/model/World", "h", "()Lcom/rs2/model/task/TaskScheduler;"): "getTaskScheduler",
    ("com/rs2/model/World", "i", "()[Lcom/rs2/model/npc/NpcDefinition;"): "getNpcDefinitions",
    ("com/rs2/model/World", "j", "()Lcom/rs2/model/World;"): "getInstance",
    ("com/rs2/model/World", "k", "()Lcom/rs2/model/objects/WorldObjectRegionIndex;"): "getObjectRegionIndex",
    (
        "com/rs2/model/World",
        "a",
        "(Lcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/Position;)V",
    ): "sendStillGraphicToNearbyPlayers",
    ("com/rs2/Server", "a", "()V"): "loadConfig",
    ("com/rs2/Server", "b", "()Ljava/util/Queue;"): "getDisconnectQueue",
    ("com/rs2/Server", "a", "(Lcom/rs2/model/player/Player;)V"): "queueLogin",
    ("com/rs2/Server", "j", "()V"): "loginAllConfiguredBots",
    ("com/rs2/Server", "c", "()I"): "selectNextBotType",
    ("com/rs2/Server", "k", "()V"): "refreshControlPanelStats",
    ("com/rs2/Server", "a", "(Ljava/lang/String;)V"): "broadcastServerMessage",
    ("com/rs2/Server", "a", "(Ljava/nio/channels/SelectionKey;)V"): "acceptConnection",
    ("com/rs2/Server", "l", "()V"): "processGameCycle",
    ("com/rs2/Server", "m", "()V"): "sleepUntilNextCycle",
    ("com/rs2/Server", "a", "(Z)V"): "scheduleShutdown",
    ("com/rs2/Server", "a", "(J)V"): "setElapsedMinutes",
    ("com/rs2/Server", "e", "()J"): "getElapsedMinutes",
    ("com/rs2/Server", "f", "()Lcom/rs2/Server;"): "getInstance",
    ("com/rs2/Server", "g", "()Ljava/nio/channels/Selector;"): "getSelector",
    ("com/rs2/net/DedicatedReactor", "b", "()Lcom/rs2/net/DedicatedReactor;"): "getInstance",
    ("com/rs2/net/DedicatedReactor", "a", "()Ljava/nio/channels/Selector;"): "getSelector",
    ("com/rs2/net/DedicatedReactor", "a", "(Lcom/rs2/net/DedicatedReactor;)V"): "setInstance",
    ("com/rs2/net/IsaacCipher", "a", "()I"): "nextInt",
    ("com/rs2/net/IsaacCipher", "b", "()V"): "generateResults",
    ("com/rs2/net/IsaacCipher", "a", "(Z)V"): "initialize",
    ("com/rs2/Server", "h", "()I"): "getBotLoginBatchIndex",
    ("com/rs2/Server", "i", "()I"): "getConfiguredBotCount",
    ("com/rs2/Server", "a", "(I)V"): "setBotLoginBatchIndex",
    ("com/rs2/model/task/TaskScheduler", "a", "()Ljava/util/List;"): "getTasks",
    ("com/rs2/model/task/TaskScheduler", "a", "(Lcom/rs2/model/task/TickTask;)V"): "schedule",
    ("com/rs2/model/task/CycleEventContainer", "a", "()V"): "execute",
    ("com/rs2/model/task/CycleEventContainer", "b", "()V"): "stop",
    ("com/rs2/model/task/CycleEventContainer", "c", "()Z"): "tick",
    ("com/rs2/model/task/CycleEventContainer", "d", "()Lcom/rs2/model/Entity;"): "getEntity",
    ("com/rs2/model/task/CycleEventContainer", "e", "()Z"): "isActive",
    ("com/rs2/model/task/CycleEventContainer", "a", "(I)V"): "setTickDelay",
    ("com/rs2/model/task/CycleEventHandler", "a", "()Lcom/rs2/model/task/CycleEventHandler;"): "getInstance",
    ("com/rs2/model/task/CycleEventHandler", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/task/CycleEvent;I)Lcom/rs2/model/task/CycleEventContainer;"): "schedule",
    ("com/rs2/model/task/CycleEventHandler", "b", "()V"): "process",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/combat/effect/CombatEffect;)Z"): "canApplyCombatEffect",
    ("com/rs2/model/Entity", "ad", "()V"): "clearCombatEffectTasks",
    ("com/rs2/model/Entity", "a", "(Ljava/lang/Class;)V"): "clearCombatEffectTasks",
    ("com/rs2/model/player/Player", "a", "(Ljava/lang/Class;)V"): "clearCombatEffectTasks",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/combat/effect/CombatEffectTask;)V"): "addCombatEffectTask",
    ("com/rs2/model/Entity", "b", "(Lcom/rs2/model/combat/effect/CombatEffect;)V"): "removeCombatEffect",
    ("com/rs2/model/EntityReference", "a", "()Lcom/rs2/model/Entity;"): "resolve",
    ("com/rs2/model/combat/PvpCombatReference", "a", "()Lcom/rs2/model/Entity;"): "resolve",
    ("com/rs2/model/combat/PvpCombatReference", "b", "()Z"): "hasExpired",
    ("com/rs2/model/combat/PvpCombatReference", "c", "()V"): "resetTimer",
    ("com/rs2/model/combat/PvpCombatReference", "d", "()I"): "getRemainingTicks",
    ("com/rs2/model/combat/effect/CombatEffect", "a", "(Lcom/rs2/model/combat/CombatAction;)V"): "apply",
    ("com/rs2/model/combat/effect/CombatEffect", "b", "(Lcom/rs2/model/combat/CombatAction;)V"): "afterApply",
    ("com/rs2/model/combat/effect/CombatEffectTask", "b", "()Lcom/rs2/model/combat/effect/CombatEffect;"): "getEffect",
    ("com/rs2/model/combat/effect/CombatEffectTask", "c", "()Lcom/rs2/model/EntityReference;"): "getSourceReference",
    ("com/rs2/model/combat/effect/CombatEffectTask", "d", "()Lcom/rs2/model/EntityReference;"): "getTargetReference",
    ("com/rs2/model/combat/effect/MovementLockEffect", "a", "()I"): "getDurationTicks",
    ("com/rs2/model/combat/effect/WallBeastStunEffect", "a", "()I"): "getDurationTicks",
    ("com/rs2/model/combat/effect/StatDrainEffect", "a", "()I"): "getSkillId",
    ("com/rs2/model/combat/effect/StatDrainEffect", "b", "(Lcom/rs2/model/Entity;)I"): "getBaseLevelForSkill",
    ("com/rs2/model/combat/effect/StatDrainEffect", "a", "(Lcom/rs2/model/Entity;I)I"): "getBaseLevel",
    ("com/rs2/model/combat/effect/StatDrainEffect", "a", "(I)I"): "calculateDrainAmount",
    ("com/rs2/model/combat/effect/PoisonEffect", "a", "(Lcom/rs2/model/combat/effect/PoisonEffect;)D"): "getInitialDamage",
    ("com/rs2/model/Entity", "a", "(Ljava/util/List;Lcom/rs2/model/Entity;I)I"): "collectCombatAttackOptions",
    ("com/rs2/model/skill/smithing/SmithingHandler", "a", "()Lcom/rs2/model/combat/attack/CombatAttack;"): "getAttack",
    ("com/rs2/model/skill/smithing/SmithingHandler", "b", "()Lcom/rs2/model/combat/attack/CombatAttackState;"): "getState",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/skill/magic/SpellDefinition;)Lcom/rs2/model/combat/attack/BaseCombatAttack;"): "createMagicAttack",
    ("com/rs2/model/skill/magic/SpellDefinition", "a", "()Z"): "isCombatSpell",
    ("com/rs2/model/skill/magic/SpellDefinition", "b", "()Z"): "isMembersOnly",
    ("com/rs2/model/skill/magic/SpellDefinition", "c", "()I"): "getRequiredLevel",
    ("com/rs2/model/skill/magic/SpellDefinition", "d", "()I"): "getAnimationId",
    ("com/rs2/model/skill/magic/SpellDefinition", "e", "()I"): "getCastSoundId",
    ("com/rs2/model/skill/magic/SpellDefinition", "f", "()D"): "getExperience",
    ("com/rs2/model/skill/magic/SpellDefinition", "g", "()[Lcom/rs2/model/item/ItemStack;"): "getRuneCosts",
    ("com/rs2/model/skill/magic/SpellDefinition", "h", "()[Lcom/rs2/model/item/ItemStack;"): "getProducedItems",
    ("com/rs2/model/skill/magic/SpellDefinition", "i", "()Lcom/rs2/model/combat/hit/HitDefinition;"): "getHitDefinition",
    ("com/rs2/model/skill/magic/SpellDefinition", "j", "()Lcom/rs2/model/animation/GraphicEffect;"): "getCastGraphic",
    ("com/rs2/model/skill/magic/SpellDefinition", "a", "(I)Lcom/rs2/model/c/ProjectileDefinition;"): "createProjectile",
    ("com/rs2/model/skill/magic/SpellDefinition", "a", "(IILcom/rs2/model/c/ProjectileDefinition;Lcom/rs2/model/animation/GraphicEffect;)Lcom/rs2/model/combat/hit/HitDefinition;"): "createHitDefinition",
    ("com/rs2/model/skill/magic/SpellDefinition", "k", "()Lcom/rs2/model/combat/effect/CombatEffect;"): "getPrimaryEffect",
    ("com/rs2/model/skill/magic/SpellDefinition", "l", "()Lcom/rs2/model/combat/effect/CombatEffect;"): "getSecondaryEffect",
    ("com/rs2/model/skill/magic/SpellDefinition", "m", "()Lcom/rs2/model/combat/effect/CombatEffect;"): "getPostHitEffect",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Ljava/util/ArrayList;)Lcom/rs2/model/combat/hit/HitDefinition;"): "setChainedTargets",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "()Ljava/util/ArrayList;"): "getChainedTargets",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/Entity;)Lcom/rs2/model/combat/hit/HitDefinition;"): "setChainedSource",
    ("com/rs2/model/combat/hit/HitDefinition", "b", "()Lcom/rs2/model/Entity;"): "getChainedSource",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/combat/AmmunitionDefinition;)V"): "setAmmunition",
    ("com/rs2/model/combat/hit/HitDefinition", "c", "()Lcom/rs2/model/combat/AmmunitionDefinition;"): "getAmmunition",
    ("com/rs2/model/combat/hit/HitDefinition", "d", "()Lcom/rs2/model/combat/hit/HitDefinition;"): "copy",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "([Lcom/rs2/model/combat/effect/CombatEffect;)Lcom/rs2/model/combat/hit/HitDefinition;"): "addEffects",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/combat/effect/CombatEffect;)Lcom/rs2/model/combat/hit/HitDefinition;"): "addEffect",
    ("com/rs2/model/combat/hit/HitDefinition", "f", "()Ljava/util/List;"): "getEffects",
    ("com/rs2/model/combat/hit/HitDefinition", "g", "()Lcom/rs2/model/combat/hit/HitDefinition;"): "clearEffects",
    ("com/rs2/model/combat/hit/HitDefinition", "h", "()Lcom/rs2/model/combat/hit/HitDefinition;"): "enableRandomDamage",
    ("com/rs2/model/combat/hit/HitDefinition", "w", "()Z"): "isRandomDamageEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(D)Lcom/rs2/model/combat/hit/HitDefinition;"): "setAccuracyMultiplier",
    ("com/rs2/model/combat/hit/HitDefinition", "i", "()Lcom/rs2/model/combat/hit/HitDefinition;"): "enableAccuracyCheck",
    ("com/rs2/model/combat/hit/HitDefinition", "b", "(Z)Lcom/rs2/model/combat/hit/HitDefinition;"): "enableAccuracyCheck",
    ("com/rs2/model/combat/hit/HitDefinition", "t", "()Z"): "isAccuracyCheckEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "v", "()D"): "getAccuracyMultiplier",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(I)Lcom/rs2/model/combat/hit/HitDefinition;"): "setSpecialEffectId",
    ("com/rs2/model/combat/hit/HitDefinition", "u", "()I"): "getSpecialEffectId",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/item/ItemStack;)Lcom/rs2/model/combat/hit/HitDefinition;"): "setDroppedAmmunition",
    ("com/rs2/model/combat/hit/HitDefinition", "o", "()Lcom/rs2/model/item/ItemStack;"): "getDroppedAmmunition",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Z)Lcom/rs2/model/combat/hit/HitDefinition;"): "setAlwaysHits",
    ("com/rs2/model/combat/hit/HitDefinition", "j", "()Z"): "isAlwaysHit",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/animation/GraphicEffect;)Lcom/rs2/model/combat/hit/HitDefinition;"): "setGraphic",
    ("com/rs2/model/combat/hit/HitDefinition", "m", "()Lcom/rs2/model/animation/GraphicEffect;"): "getGraphic",
    ("com/rs2/model/combat/hit/HitDefinition", "b", "(I)Lcom/rs2/model/combat/hit/HitDefinition;"): "setImpactSoundId",
    ("com/rs2/model/combat/hit/HitDefinition", "n", "()I"): "getImpactSoundId",
    ("com/rs2/model/combat/hit/HitDefinition", "c", "(I)Lcom/rs2/model/combat/hit/HitDefinition;"): "setDelay",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;)I"): "calculateDelay",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/c/ProjectileDefinition;)Lcom/rs2/model/combat/hit/HitDefinition;"): "setProjectile",
    ("com/rs2/model/combat/hit/HitDefinition", "l", "()Lcom/rs2/model/c/ProjectileDefinition;"): "getProjectile",
    ("com/rs2/model/combat/hit/HitDefinition", "k", "()Lcom/rs2/model/combat/AttackStyleDefinition;"): "getAttackStyle",
    ("com/rs2/model/combat/hit/HitDefinition", "q", "()I"): "getMaxDamage",
    ("com/rs2/model/combat/hit/HitDefinition", "e", "(I)V"): "setMaxDamage",
    ("com/rs2/model/combat/hit/HitDefinition", "d", "(I)Lcom/rs2/model/combat/hit/HitDefinition;"): "setMinimumDamage",
    ("com/rs2/model/combat/hit/HitDefinition", "r", "()I"): "getMinimumDamage",
    ("com/rs2/model/combat/hit/HitDefinition", "s", "()Lcom/rs2/model/combat/hit/HitType;"): "getHitType",
    ("com/rs2/model/combat/hit/HitDefinition", "a", "(Lcom/rs2/model/skill/magic/SpellDefinition;)Lcom/rs2/model/combat/hit/HitDefinition;"): "setSpell",
    ("com/rs2/model/combat/hit/HitDefinition", "x", "()Lcom/rs2/model/skill/magic/SpellDefinition;"): "getSpell",
    ("com/rs2/model/combat/hit/HitDefinition", "f", "(I)Lcom/rs2/model/combat/hit/HitDefinition;"): "setBlockAnimationId",
    ("com/rs2/model/combat/hit/HitDefinition", "y", "()I"): "getBlockAnimationId",
    ("com/rs2/model/combat/hit/HitDefinition", "c", "(Z)Lcom/rs2/model/combat/hit/HitDefinition;"): "setMultiTargetSpreadEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "z", "()Z"): "isMultiTargetSpreadEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "d", "(Z)Lcom/rs2/model/combat/hit/HitDefinition;"): "setBlockAnimationEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "A", "()Z"): "isBlockAnimationEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "e", "()Z"): "isProtectionPrayerReductionDeferred",
    ("com/rs2/model/combat/hit/HitType", "a", "()I"): "getClientId",
    ("com/rs2/model/combat/hit/DamageContribution", "b", "()Z"): "isExpired",
    ("com/rs2/model/combat/hit/DamageContribution", "a", "(I)V"): "addDamage",
    ("com/rs2/model/combat/hit/DamageContribution", "c", "()I"): "getDamage",
    ("com/rs2/model/EntityUpdateState", "a", "(Lcom/rs2/model/player/Player;II)Z"): "handleOrbChargeButton",
    ("com/rs2/model/EntityUpdateState", "a", "(Lcom/rs2/model/player/Player;IIIII)V"): "setForcedMovement",
    ("com/rs2/model/EntityUpdateState", "a", "()V"): "clearForcedMovement",
    ("com/rs2/model/EntityUpdateState", "a", "(Lcom/rs2/model/animation/GraphicEffect;)V"): "setGraphic",
    ("com/rs2/model/EntityUpdateState", "a", "(I)V"): "setGraphic",
    ("com/rs2/model/EntityUpdateState", "a", "(II)V"): "setGraphic",
    ("com/rs2/model/EntityUpdateState", "b", "(I)V"): "setGraphicHeight100",
    ("com/rs2/model/EntityUpdateState", "c", "(I)V"): "setAnimation",
    ("com/rs2/model/EntityUpdateState", "b", "(II)V"): "setAnimation",
    ("com/rs2/model/EntityUpdateState", "d", "(I)V"): "setFaceEntity",
    ("com/rs2/model/EntityUpdateState", "a", "(Lcom/rs2/model/Position;)V"): "setFacePosition",
    ("com/rs2/model/EntityUpdateState", "a", "(Ljava/lang/String;)V"): "setForcedTextAndMarkUpdated",
    ("com/rs2/model/EntityUpdateState", "b", "()V"): "reset",
    ("com/rs2/model/EntityUpdateState", "a", "(Z)V"): "setUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "c", "()Z"): "isUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "b", "(Z)V"): "setAppearanceUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "d", "()Z"): "isAppearanceUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "c", "(Z)V"): "setForcedTextUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "e", "()Z"): "isForcedTextUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "b", "(Ljava/lang/String;)V"): "setForcedText",
    ("com/rs2/model/EntityUpdateState", "f", "()Ljava/lang/String;"): "getForcedText",
    ("com/rs2/model/EntityUpdateState", "g", "()Z"): "isGraphicUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "h", "()I"): "getGraphicId",
    ("com/rs2/model/EntityUpdateState", "i", "()I"): "getGraphicDelay",
    ("com/rs2/model/EntityUpdateState", "j", "()Z"): "isAnimationUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "k", "()I"): "getAnimationId",
    ("com/rs2/model/EntityUpdateState", "l", "()I"): "getAnimationDelay",
    ("com/rs2/model/EntityUpdateState", "m", "()Z"): "isFaceEntityUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "e", "(I)V"): "setFaceEntityId",
    ("com/rs2/model/EntityUpdateState", "n", "()I"): "getFaceEntityId",
    ("com/rs2/model/EntityUpdateState", "o", "()Z"): "isFacePositionUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "b", "(Lcom/rs2/model/Position;)V"): "setFacePositionValue",
    ("com/rs2/model/EntityUpdateState", "p", "()Lcom/rs2/model/Position;"): "getFacePosition",
    ("com/rs2/model/EntityUpdateState", "d", "(Z)V"): "setPrimaryHitUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "q", "()Z"): "isPrimaryHitUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "e", "(Z)V"): "setSecondaryHitUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "r", "()Z"): "isSecondaryHitUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "f", "(I)V"): "setPrimaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "s", "()I"): "getPrimaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "g", "(I)V"): "setSecondaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "t", "()I"): "getSecondaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "h", "(I)V"): "setPrimaryHitType",
    ("com/rs2/model/EntityUpdateState", "u", "()I"): "getPrimaryHitType",
    ("com/rs2/model/EntityUpdateState", "i", "(I)V"): "setSecondaryHitType",
    ("com/rs2/model/EntityUpdateState", "v", "()I"): "getSecondaryHitType",
    ("com/rs2/model/EntityUpdateState", "w", "()Z"): "isForcedMovementUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "f", "(Z)V"): "setForcedMovementUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "a", "(Lcom/rs2/model/player/Player;)I"): "getLocalXForUpdate",
    ("com/rs2/model/EntityUpdateState", "b", "(Lcom/rs2/model/player/Player;)I"): "getLocalYForUpdate",
    ("com/rs2/model/EntityUpdateState", "x", "()I"): "getForcedMovementEndXOffset",
    ("com/rs2/model/EntityUpdateState", "y", "()I"): "getForcedMovementEndYOffset",
    ("com/rs2/model/EntityUpdateState", "z", "()I"): "getForcedMovementStartDelay",
    ("com/rs2/model/EntityUpdateState", "A", "()I"): "getForcedMovementEndDelay",
    ("com/rs2/model/EntityUpdateState", "B", "()I"): "getForcedMovementDirection",
    ("com/rs2/model/EntityUpdateState", "C", "()Z"): "isPrimaryHitDamageOverridden",
    ("com/rs2/model/EntityUpdateState", "D", "()Z"): "isSecondaryHitDamageOverridden",
    ("com/rs2/model/EntityUpdateState", "c", "(II)V"): "queueHit",
    ("com/rs2/model/Entity", "ai", "()Lcom/rs2/model/MovementQueue;"): "getMovementQueue",
    ("com/rs2/model/player/Player", "ai", "()Lcom/rs2/model/MovementQueue;"): "getMovementQueue",
    ("com/rs2/model/npc/Npc", "ai", "()Lcom/rs2/model/MovementQueue;"): "getMovementQueue",
    ("com/rs2/bot/BotPlayer", "ai", "()Lcom/rs2/model/MovementQueue;"): "getMovementQueue",
    ("com/rs2/model/Entity", "a", "(IIZ)V"): "queuePathTo",
    ("com/rs2/model/player/Player", "a", "(IIZ)V"): "queuePathTo",
    ("com/rs2/model/npc/Npc", "a", "(IIZ)V"): "queuePathTo",
    ("com/rs2/bot/BotPlayer", "a", "(IIZ)V"): "queuePathTo",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/Position;Z)V"): "queuePathTo",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/Position;Z)V"): "queuePathTo",
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/Position;Z)V"): "queuePathTo",
    ("com/rs2/bot/BotPlayer", "a", "(Lcom/rs2/model/Position;Z)V"): "queuePathTo",
    ("com/rs2/model/Entity", "a", "(Lcom/rs2/model/Entity;I)Z"): "isWithinReach",
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/Entity;I)Z"): "isWithinReach",
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/Entity;I)Z"): "isWithinReach",
    ("com/rs2/bot/BotPlayer", "a", "(Lcom/rs2/model/Entity;I)Z"): "isWithinReach",
    ("com/rs2/model/Entity", "f", "(Lcom/rs2/model/Entity;)Z"): "isOverlapping",
    ("com/rs2/model/player/Player", "f", "(Lcom/rs2/model/Entity;)Z"): "isOverlapping",
    ("com/rs2/model/npc/Npc", "f", "(Lcom/rs2/model/Entity;)Z"): "isOverlapping",
    ("com/rs2/bot/BotPlayer", "f", "(Lcom/rs2/model/Entity;)Z"): "isOverlapping",
    ("com/rs2/model/Entity", "c", "(II)Z"): "canStepToOffset",
    ("com/rs2/model/player/Player", "c", "(II)Z"): "canStepToOffset",
    ("com/rs2/model/npc/Npc", "c", "(II)Z"): "canStepToOffset",
    ("com/rs2/bot/BotPlayer", "c", "(II)Z"): "canStepToOffset",
    ("com/rs2/model/Entity", "m", "()Z"): "isMoving",
    ("com/rs2/model/player/Player", "m", "()Z"): "isMoving",
    ("com/rs2/model/npc/Npc", "m", "()Z"): "isMoving",
    ("com/rs2/bot/BotPlayer", "m", "()Z"): "isMoving",
    ("com/rs2/model/Entity", "n", "()Z"): "isRunningMovement",
    ("com/rs2/model/player/Player", "n", "()Z"): "isRunningMovement",
    ("com/rs2/model/npc/Npc", "n", "()Z"): "isRunningMovement",
    ("com/rs2/bot/BotPlayer", "n", "()Z"): "isRunningMovement",
    ("com/rs2/model/EntityTargetMovement", "a", "(Lcom/rs2/model/player/Player;)V"): "startRuneEssenceMining",
    ("com/rs2/model/EntityTargetMovement", "a", "()V"): "process",
    ("com/rs2/model/EntityTargetMovement", "b", "()V"): "processTargetMovement",
    ("com/rs2/model/EntityTargetMovement", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;)V"): "pathPlayerAdjacentToTarget",
    ("com/rs2/model/EntityTargetMovement", "a", "(Lcom/rs2/model/Entity;)V"): "clearMovementTarget",
    ("com/rs2/model/EntityTargetMovement", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)Z"): "canReachTarget",
    ("com/rs2/model/EntityTargetMovement", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;I)Z"): "canReachTarget",
    ("com/rs2/model/EntityTargetMovement", "c", "()V"): "moveAwayFromOverlap",
    ("com/rs2/model/EntityTargetMovement", "a", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;)Z"): "isDiagonalTo",
    ("com/rs2/model/MovementQueue", "a", "()Ljava/util/Deque;"): "getSteps",
    ("com/rs2/model/MovementQueue", "b", "()V"): "reset",
    ("com/rs2/model/MovementQueue", "c", "()V"): "process",
    ("com/rs2/model/MovementQueue", "d", "()V"): "clear",
    ("com/rs2/model/MovementQueue", "e", "()V"): "removeFirstStep",
    ("com/rs2/model/MovementQueue", "a", "(Lcom/rs2/model/Position;)V"): "addStep",
    ("com/rs2/model/MovementQueue", "a", "(Z)V"): "setRunning",
    ("com/rs2/model/MovementQueue", "f", "()Z"): "isRunning",
    ("com/rs2/model/MovementQueue", "b", "(Z)V"): "setRunPath",
    ("com/rs2/model/MovementQueue", "g", "()Z"): "isRunPath",
    ("com/rs2/model/MovementQueue", "h", "()V"): "clearMovementActions",
    ("com/rs2/model/MovementQueue", "a", "(II)Z"): "canStep",
    ("com/rs2/model/MovementStep", "a", "()I"): "getDirection",
    ("com/rs2/model/MovementStep", "b", "()I"): "getX",
    ("com/rs2/model/MovementStep", "c", "()I"): "getY",
    ("com/rs2/bot/BotPlayer", "a", "()V"): "startTradeAdvertBot",
    ("com/rs2/bot/BotPlayer", "b", "()V"): "startSkillingBot",
    ("com/rs2/bot/BotPlayer", "c", "()V"): "startProgressiveBot",
    ("com/rs2/bot/BotPlayer", "d", "()V"): "startCombatLoadoutBot",
    ("com/rs2/bot/BotPlayer", "e", "()V"): "startDropPartyBot",
    ("com/rs2/bot/BotPlayer", "a", "(I)V"): "startClanWarsBot",
    ("com/rs2/bot/BotPlayer", "a", "(ILjava/lang/String;I)V"): "createBotFromPool",
    ("com/rs2/bot/BotPlayer", "a", "(Ljava/lang/String;Ljava/lang/String;I)V"): "createNamedBot",
    ("com/rs2/bot/BotPlayer", "g", "()V"): "prepareBotNamePools",
    ("com/rs2/bot/BotPlayer", "h", "()V"): "removeConfiguredBotNames",
    ("com/rs2/bot/BotPlayer", "i", "()V"): "loadExistingProgressiveBotNames",
    ("com/rs2/bot/ClanWarsBotManager", "a", "()V"): "processClanWarsBotEvent",
    ("com/rs2/bot/ClanWarsBotManager", "a", "(Lcom/rs2/model/player/Player;)V"): "hideClanWarsBot",
    ("com/rs2/bot/ClanWarsBotManager", "b", "(Lcom/rs2/model/player/Player;)Lcom/rs2/model/player/Player;"): "findClanWarsOpponent",
    ("com/rs2/bot/ClanWarsBotManager", "b", "()V"): "startClanWarsCombatants",
    ("com/rs2/bot/ClanWarsBotManager", "c", "(Lcom/rs2/model/player/Player;)V"): "prepareClanWarsCombatant",
    ("com/rs2/bot/ClanWarsBotManager", "c", "()V"): "chooseClanWarsTeamCapes",
    ("com/rs2/bot/ClanWarsBotManager", "d", "()V"): "chooseClanWarsTeamTags",
    ("com/rs2/model/GameplayHelper", "c", "()I"): "getTradeAdvertItemId",
    ("com/rs2/model/GameplayHelper", "d", "()[I"): "getTradeAdvertQuantityOptions",
    ("com/rs2/model/GameplayHelper", "a", "(I)I"): "getPreviousTradeAdvertQuantityOption",
    ("com/rs2/model/GameplayHelper", "a", "(JJ)I"): "getDaysBetweenMidnights",
    ("com/rs2/model/GameplayHelper", "b", "(JJ)I"): "getHoursBetween",
    ("com/rs2/model/GameplayHelper", "a", "(JI)J"): "addDaysToTimestamp",
    ("com/rs2/model/GameplayHelper", "b", "(IIIII)J"): "buildTimestampMillis",
    ("com/rs2/model/GameplayHelper", "a", "(J)Ljava/lang/String;"): "formatDateDayMonthYear",
    ("com/rs2/model/GameplayHelper", "b", "(J)Ljava/lang/String;"): "formatDurationHoursMinutes",
    ("com/rs2/model/GameplayHelper", "d", "(Lcom/rs2/model/player/Player;)V"): "startNextBotTask",
    ("com/rs2/model/GameplayHelper", "e", "(Lcom/rs2/model/player/Player;)Z"): "shouldReturnToBankForBotTask",
    ("com/rs2/model/GameplayHelper", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V"): "refreshTradeOfferInterfaces",
    ("com/rs2/model/GameplayHelper", "b", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V"): "handleTradeRequest",
    ("com/rs2/model/GameplayHelper", "o", "(Lcom/rs2/model/player/Player;)V"): "declineTrade",
    ("com/rs2/model/GameplayHelper", "p", "(Lcom/rs2/model/player/Player;)V"): "returnTradeOfferItems",
    ("com/rs2/model/GameplayHelper", "b", "(Lcom/rs2/model/player/Player;III)V"): "addTradeOfferItem",
    ("com/rs2/model/GameplayHelper", "c", "(Lcom/rs2/model/player/Player;III)V"): "removeTradeOfferItem",
    ("com/rs2/model/GameplayHelper", "q", "(Lcom/rs2/model/player/Player;)V"): "acceptTradeFirstScreen",
    ("com/rs2/model/GameplayHelper", "r", "(Lcom/rs2/model/player/Player;)V"): "acceptTradeSecondScreen",
    ("com/rs2/model/GameplayHelper", "s", "(Lcom/rs2/model/player/Player;)V"): "refreshTradeConfirmationSummary",
    ("com/rs2/net/packet/handler/PlayerInteractionPacketHandler", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V"): "dispatchDeferredTradeRequest",
    ("com/rs2/bot/BotTradeAdvertManager", "a", "()V"): "initializeTradeAdvertOfferPools",
    ("com/rs2/bot/BotTradeAdvertManager", "a", "(ZII)I"): "roundAdvertUnitPrice",
    ("com/rs2/bot/BotTradeAdvertManager", "a", "(Lcom/rs2/model/player/Player;)V"): "prepareTradeAdvertOffer",
    ("com/rs2/bot/BotTradeAdvertManager", "b", "(Lcom/rs2/model/player/Player;)V"): "updateTradeAdvertMessage",
    ("com/rs2/bot/BotTradeAdvertManager", "c", "(Lcom/rs2/model/player/Player;)V"): "prepareTradeAdvertCombatLoadout",
    ("com/rs2/bot/BotTradeAdvertManager", "d", "(Lcom/rs2/model/player/Player;)V"): "prepareTradeAdvertInventory",
    ("com/rs2/bot/BotTradeAdvertManager", "e", "(Lcom/rs2/model/player/Player;)V"): "advanceTradeAdvertTradeFlow",
    ("com/rs2/bot/DropPartyBotManager", "a", "(Lcom/rs2/model/ground/GroundItem;)V"): "notifyDropPartyRewardDropped",
    ("com/rs2/bot/DropPartyBotManager", "a", "(Lcom/rs2/model/player/Player;)V"): "finishLeaderDrops",
    ("com/rs2/bot/DropPartyBotManager", "b", "(Lcom/rs2/model/player/Player;)V"): "finishDropPartyParticipant",
    ("com/rs2/bot/DropPartyBotManager", "c", "(Lcom/rs2/model/player/Player;)V"): "startDropPartyTick",
    ("com/rs2/bot/DropPartyBotManager", "d", "(Lcom/rs2/model/player/Player;)V"): "prepareDropPartyCombatLoadout",
    ("com/rs2/bot/DropPartyBotManager", "a", "()V"): "initializeDropPartyRewardPools",
    ("com/rs2/bot/DropPartyBotManager", "e", "(Lcom/rs2/model/player/Player;)V"): "prepareDropPartyInventory",
}

FIELD_NAME_MAP = {
    ("com/rs2/CacheCoordinateTranslator", "a", "Z"): "dungeonCoordinateShiftActive",
    ("com/rs2/CacheCoordinateTranslator", "b", "[I"): "dungeonCoordinateShiftSourceRegionIds",
    ("com/rs2/CacheCoordinateTranslator", "c", "[I"): "dungeonCoordinateShiftTargetRegionIds",
    ("com/rs2/CacheCoordinateTranslator", "d", "I"): "dungeonCoordinateShiftXOffset",
    ("com/rs2/CacheCoordinateTranslator", "e", "I"): "dungeonCoordinateShiftYOffset",
    ("com/rs2/CacheRevisionInfo", "c", "I"): "revision",
    ("com/rs2/CacheRevisionInfo", "a", "Ljava/lang/String;"): "releaseDate",
    ("com/rs2/CacheRevisionInfo", "b", "[Ljava/lang/String;"): "updateNotes",
    ("com/rs2/CacheRevisionInfo", "d", "[Lcom/rs2/CacheRevisionInfo;"): "revisions",
    ("com/rs2/ConnectionThrottleSettings", "a", "Z"): "connectionsEnabled",
    (
        "com/rs2/ConnectionThrottle",
        "a",
        "Ljava/util/concurrent/ConcurrentHashMap;",
    ): "connectionCountsByHost",
    ("com/rs2/HiscoresDatabase", "a", "Ljava/sql/Statement;"): "statement",
    ("com/rs2/HiscoresDatabase", "b", "Z"): "connected",
    ("com/rs2/HiscoresDatabase", "c", "Ljava/lang/String;"): "jdbcUrl",
    ("com/rs2/HiscoresDatabase", "d", "Ljava/lang/String;"): "username",
    ("com/rs2/HiscoresDatabase", "e", "Ljava/lang/String;"): "password",
    ("com/rs2/HiscoresDatabase", "f", "Ljava/lang/String;"): "driverClassName",
    ("com/rs2/LanDiscoveryService", "a", "Ljava/lang/String;"): "requestMessage",
    ("com/rs2/LanDiscoveryService", "b", "Ljava/lang/String;"): "responseMessage",
    ("com/rs2/LanDiscoveryService", "g", "Ljava/net/DatagramSocket;"): "socket",
    ("com/rs2/LanDiscoveryService", "f", "Z"): "running",
    ("com/rs2/LanDiscoveryListener", "a", "Ljava/net/DatagramPacket;"): "packet",
    ("com/rs2/ConfigFile", "a", "Ljava/lang/String;"): "key",
    ("com/rs2/ConfigFile", "b", "I"): "parameterCount",
    ("com/rs2/ConfigFile", "c", "[Lcom/rs2/ConfigFile;"): "definitions",
    ("com/rs2/model/quest/QuestConstants", "a", "[I"): "COMBAT_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "b", "[I"): "STATS_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "c", "[I"): "QUEST_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "d", "[I"): "INVENTORY_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "e", "[I"): "EQUIPMENT_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "f", "[I"): "PRAYER_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "g", "[I"): "MAGIC_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "h", "[I"): "FRIENDS_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "i", "[I"): "IGNORE_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "j", "[I"): "LOGOUT_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "k", "[I"): "OPTIONS_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "l", "[I"): "EMOTES_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "m", "[I"): "MUSIC_TAB_INTERFACE",
    ("com/rs2/model/quest/QuestConstants", "n", "I"): "RELDO_NPC_ID",
    ("com/rs2/model/ground/GroundItem", "a", "Lcom/rs2/model/item/ItemStack;"): "item",
    ("com/rs2/model/ground/GroundItem", "b", "Lcom/rs2/model/EntityReference;"): "source",
    ("com/rs2/model/ground/GroundItem", "c", "Lcom/rs2/model/EntityReference;"): "owner",
    ("com/rs2/model/ground/GroundItem", "d", "Lcom/rs2/model/task/ElapsedTicks;"): "timer",
    ("com/rs2/model/ground/GroundItem", "e", "Lcom/rs2/model/ground/GroundItemVisibility;"): "visibility",
    ("com/rs2/model/ground/GroundItem", "f", "Lcom/rs2/model/Position;"): "position",
    ("com/rs2/model/ground/GroundItem", "g", "Z"): "respawning",
    ("com/rs2/model/ground/GroundItem", "h", "I"): "respawnDelayTicks",
    ("com/rs2/model/ground/GroundItem", "i", "Z"): "restrictedModePickupAllowed",
    ("com/rs2/model/ground/GroundItem", "j", "[I"): "visibilitySwitchTable",
    ("com/rs2/model/ground/GroundItemManager", "a", "Ljava/util/LinkedList;"): "groundItems",
    ("com/rs2/model/ground/GroundItemManager", "b", "I"): "hiddenItemRevealDelayTicks",
    ("com/rs2/model/ground/GroundItemManager", "c", "I"): "privateItemRevealDelayTicks",
    ("com/rs2/model/ground/GroundItemManager", "d", "I"): "publicItemLifetimeTicks",
    ("com/rs2/model/ground/GroundItemManager", "e", "I"): "privateUntradeableLifetimeTicks",
    ("com/rs2/model/ground/GroundItemManager", "f", "Lcom/rs2/model/ground/GroundItemManager;"): "instance",
    ("com/rs2/model/ground/GroundItemManager", "g", "[I"): "visibilitySwitchTable",
    ("com/rs2/model/ground/GroundItemVisibility", "a", "Lcom/rs2/model/ground/GroundItemVisibility;"): "PRIVATE",
    ("com/rs2/model/ground/GroundItemVisibility", "b", "Lcom/rs2/model/ground/GroundItemVisibility;"): "PUBLIC",
    ("com/rs2/model/ground/GroundItemVisibility", "c", "Lcom/rs2/model/ground/GroundItemVisibility;"): "HIDDEN",
    ("com/rs2/model/ground/GroundItemVisibility", "d", "[Lcom/rs2/model/ground/GroundItemVisibility;"): "VALUES",
    ("com/rs2/model/skill/magic/TeleportManager", "i", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/skill/magic/TeleportManager", "a", "Lcom/rs2/model/Position;"): "RESPAWN_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/TeleportManager", "b", "Lcom/rs2/model/Position;"): "EDGEVILLE_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/TeleportManager", "c", "Lcom/rs2/model/Position;"): "KARAMJA_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/TeleportManager", "d", "Lcom/rs2/model/Position;"): "DRAYNOR_VILLAGE_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/TeleportManager", "e", "Lcom/rs2/model/Position;"): "AL_KHARID_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/TeleportManager", "f", "Lcom/rs2/model/Position;"): "CASTLE_WARS_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/TeleportManager", "g", "Lcom/rs2/model/Position;"): "DUEL_ARENA_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/TeleportManager", "h", "Lcom/rs2/model/Position;"): "BURTHORPE_TELEPORT_POSITION",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "a", "I"): "ticksRemaining",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "b", "Lcom/rs2/model/skill/magic/TeleportManager;"): "teleportManager",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "c", "I"): "departureAnimationId",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "d", "I"): "departureGraphicId",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "e", "I"): "arrivalAnimationId",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "f", "I"): "destinationX",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "g", "I"): "destinationY",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "h", "I"): "destinationPlane",
    ("com/rs2/model/skill/magic/ScriptedTeleportTask", "i", "Ljava/lang/String;"): "arrivalMessage",
    ("com/rs2/model/skill/magic/StandardTeleportTask", "a", "I"): "ticksRemaining",
    ("com/rs2/model/skill/magic/StandardTeleportTask", "b", "Lcom/rs2/model/skill/magic/TeleportManager;"): "teleportManager",
    ("com/rs2/model/skill/magic/StandardTeleportTask", "c", "I"): "destinationX",
    ("com/rs2/model/skill/magic/StandardTeleportTask", "d", "I"): "destinationY",
    ("com/rs2/model/skill/magic/StandardTeleportTask", "e", "I"): "destinationPlane",
    ("com/rs2/model/skill/magic/StandardTeleportTask", "f", "Ljava/lang/String;"): "arrivalMessage",
    ("com/rs2/model/skill/magic/MagicTeleportTask", "a", "I"): "ticksRemaining",
    ("com/rs2/model/skill/magic/MagicTeleportTask", "b", "Lcom/rs2/model/skill/magic/TeleportManager;"): "teleportManager",
    ("com/rs2/model/skill/magic/MagicTeleportTask", "c", "Z"): "ancientSpellbookTeleport",
    ("com/rs2/model/skill/magic/MagicTeleportTask", "d", "I"): "destinationX",
    ("com/rs2/model/skill/magic/MagicTeleportTask", "e", "I"): "destinationY",
    ("com/rs2/model/skill/magic/MagicTeleportTask", "f", "I"): "destinationPlane",
    ("com/rs2/model/skill/magic/DelayedTeleportTask", "a", "I"): "ticksRemaining",
    ("com/rs2/model/skill/magic/DelayedTeleportTask", "b", "Lcom/rs2/model/skill/magic/TeleportManager;"): "teleportManager",
    ("com/rs2/model/skill/magic/DelayedTeleportTask", "c", "I"): "destinationX",
    ("com/rs2/model/skill/magic/DelayedTeleportTask", "d", "I"): "destinationY",
    ("com/rs2/model/skill/magic/DelayedTeleportTask", "e", "I"): "destinationPlane",
    ("com/rs2/model/skill/magic/DelayedTeleportTask", "f", "Ljava/lang/String;"): "arrivalMessage",
    ("com/rs2/model/skill/magic/AbyssTeleportTask", "a", "I"): "ticksRemaining",
    ("com/rs2/model/skill/magic/AbyssTeleportTask", "b", "Lcom/rs2/model/skill/magic/TeleportManager;"): "teleportManager",
    ("com/rs2/model/skill/magic/AbyssTeleportTask", "c", "I"): "destinationX",
    ("com/rs2/model/skill/magic/AbyssTeleportTask", "d", "I"): "destinationY",
    ("com/rs2/model/skill/magic/AbyssTeleportTask", "e", "I"): "destinationPlane",
    ("com/rs2/launcher/ControlPanel", "f", "Ljavax/swing/JTabbedPane;"): "tabbedPane",
    ("com/rs2/launcher/ControlPanel", "g", "Ljavax/swing/JPanel;"): "mainPanel",
    ("com/rs2/launcher/ControlPanel", "h", "Ljavax/swing/JPanel;"): "serverSettingsPanel",
    ("com/rs2/launcher/ControlPanel", "i", "Ljavax/swing/JPanel;"): "connectionSettingsPanel",
    ("com/rs2/launcher/ControlPanel", "j", "Ljavax/swing/JPanel;"): "skillSettingsPanel",
    ("com/rs2/launcher/ControlPanel", "k", "Ljavax/swing/JPanel;"): "creditsPanel",
    ("com/rs2/launcher/ControlPanel", "l", "Ljavax/swing/JButton;"): "startServerButton",
    ("com/rs2/launcher/ControlPanel", "m", "Ljavax/swing/JButton;"): "restartServerButton",
    ("com/rs2/launcher/ControlPanel", "a", "Ljavax/swing/JButton;"): "shutdownServerButton",
    ("com/rs2/launcher/ControlPanel", "n", "Ljavax/swing/JButton;"): "sendServerMessageButton",
    ("com/rs2/launcher/ControlPanel", "o", "Ljavax/swing/JButton;"): "defaultsButton",
    ("com/rs2/launcher/ControlPanel", "p", "Ljavax/swing/JLabel;"): "serverStatusLabel",
    ("com/rs2/launcher/ControlPanel", "q", "Ljavax/swing/JLabel;"): "usersOnlineLabel",
    ("com/rs2/launcher/ControlPanel", "r", "Ljavax/swing/JLabel;"): "serverNameStatusLabel",
    ("com/rs2/launcher/ControlPanel", "s", "Ljavax/swing/JLabel;"): "runtimeLabel",
    ("com/rs2/launcher/ControlPanel", "t", "Ljavax/swing/JTextField;"): "serverMessageField",
    ("com/rs2/launcher/ControlPanel", "u", "Ljavax/swing/JTextField;"): "serverNameField",
    ("com/rs2/launcher/ControlPanel", "v", "Ljavax/swing/JTextField;"): "maxPlayersField",
    ("com/rs2/launcher/ControlPanel", "w", "Ljavax/swing/JTextField;"): "xpRateField",
    ("com/rs2/launcher/ControlPanel", "x", "Ljavax/swing/JTextField;"): "startPositionField",
    ("com/rs2/launcher/ControlPanel", "y", "Ljavax/swing/JTextField;"): "respawnPositionField",
    ("com/rs2/launcher/ControlPanel", "z", "Ljavax/swing/JCheckBox;"): "duelingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "A", "Ljavax/swing/JCheckBox;"): "adminInteractionsCheckbox",
    ("com/rs2/launcher/ControlPanel", "B", "Ljavax/swing/JCheckBox;"): "freeToPlayOnlyContentCheckbox",
    ("com/rs2/launcher/ControlPanel", "C", "Ljavax/swing/JCheckBox;"): "itemSpawningCheckbox",
    ("com/rs2/launcher/ControlPanel", "D", "Ljavax/swing/JCheckBox;"): "funPkCheckbox",
    ("com/rs2/launcher/ControlPanel", "E", "Ljavax/swing/JCheckBox;"): "pkWorldCheckbox",
    ("com/rs2/launcher/ControlPanel", "F", "Ljavax/swing/JRadioButton;"): "noLoginRestrictionButton",
    ("com/rs2/launcher/ControlPanel", "G", "Ljavax/swing/JRadioButton;"): "p2pLoginRestrictionButton",
    ("com/rs2/launcher/ControlPanel", "H", "Ljavax/swing/JRadioButton;"): "modLoginRestrictionButton",
    ("com/rs2/launcher/ControlPanel", "I", "Ljavax/swing/JRadioButton;"): "adminLoginRestrictionButton",
    ("com/rs2/launcher/ControlPanel", "J", "Ljavax/swing/JButton;"): "setSettingsButton",
    ("com/rs2/launcher/ControlPanel", "K", "Ljavax/swing/JTextField;"): "serverPortField",
    ("com/rs2/launcher/ControlPanel", "L", "Ljavax/swing/JTextField;"): "clientVersionField",
    ("com/rs2/launcher/ControlPanel", "M", "Ljavax/swing/JCheckBox;"): "mysqlEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "N", "Ljavax/swing/JTextField;"): "dbDriverField",
    ("com/rs2/launcher/ControlPanel", "O", "Ljavax/swing/JTextField;"): "dbUrlField",
    ("com/rs2/launcher/ControlPanel", "P", "Ljavax/swing/JTextField;"): "dbUserField",
    ("com/rs2/launcher/ControlPanel", "Q", "Ljavax/swing/JTextField;"): "dbPasswordField",
    ("com/rs2/launcher/ControlPanel", "R", "Ljavax/swing/JCheckBox;"): "rsaEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "S", "Ljavax/swing/JTextField;"): "rsaModulusField",
    ("com/rs2/launcher/ControlPanel", "T", "Ljavax/swing/JTextField;"): "rsaPrivateExponentField",
    ("com/rs2/launcher/ControlPanel", "U", "Ljavax/swing/JCheckBox;"): "debugModeCheckbox",
    ("com/rs2/launcher/ControlPanel", "V", "Ljavax/swing/JCheckBox;"): "hiscoresEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "W", "Ljavax/swing/JCheckBox;"): "developModeCheckbox",
    ("com/rs2/launcher/ControlPanel", "X", "Ljavax/swing/JCheckBox;"): "woodcuttingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "Y", "Ljavax/swing/JCheckBox;"): "thievingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "Z", "Ljavax/swing/JCheckBox;"): "smithingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "aa", "Ljavax/swing/JCheckBox;"): "slayerEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ab", "Ljavax/swing/JCheckBox;"): "runecraftingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ac", "Ljavax/swing/JCheckBox;"): "prayerEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ad", "Ljavax/swing/JCheckBox;"): "miningEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ae", "Ljavax/swing/JCheckBox;"): "herbloreEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "af", "Ljavax/swing/JCheckBox;"): "fletchingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ag", "Ljavax/swing/JCheckBox;"): "fishingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ah", "Ljavax/swing/JCheckBox;"): "firemakingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ai", "Ljavax/swing/JCheckBox;"): "farmingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "aj", "Ljavax/swing/JCheckBox;"): "craftingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "ak", "Ljavax/swing/JCheckBox;"): "cookingEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "al", "Ljavax/swing/JCheckBox;"): "agilityEnabledCheckbox",
    ("com/rs2/launcher/ControlPanel", "am", "Ljavax/swing/JCheckBox;"): "showPlayerNamesCheckbox",
    ("com/rs2/launcher/ControlPanel", "an", "Ljava/awt/Font;"): "controlFont",
    ("com/rs2/launcher/ControlPanel", "b", "Lcom/rs2/launcher/WorldMapPanel;"): "worldMapPanel",
    ("com/rs2/launcher/ControlPanel", "c", "I"): "mapScale",
    ("com/rs2/launcher/TransparentColorFilter", "a", "I"): "transparentRgb",
    ("com/rs2/launcher/ControlPanel", "ao", "Ljavax/swing/JFrame;"): "worldMapFrame",
    ("com/rs2/launcher/ControlPanel", "ap", "Ljavax/swing/JPanel;"): "worldMapContainerPanel",
    ("com/rs2/launcher/ControlPanel", "aq", "Ljavax/swing/JScrollPane;"): "worldMapScrollPane",
    ("com/rs2/launcher/ControlPanel", "d", "I"): "mapScrollX",
    ("com/rs2/launcher/ControlPanel", "e", "I"): "mapScrollY",
    ("com/rs2/launcher/ControlPanel", "ar", "Ljava/lang/String;"): "serverStatusHtml",
    ("com/rs2/launcher/ControlPanel", "as", "I"): "displayedMaxPlayers",
    ("com/rs2/launcher/ControlPanel", "at", "I"): "displayedOnlinePlayers",
    ("com/rs2/launcher/ControlPanel", "au", "I"): "displayedModeratorCount",
    ("com/rs2/launcher/ControlPanel", "av", "I"): "displayedAdminCount",
    ("com/rs2/launcher/ControlPanel", "aw", "Ljava/lang/String;"): "displayedServerName",
    ("com/rs2/launcher/ControlPanel", "ax", "I"): "displayedRuntimeMinutes",
    ("com/rs2/launcher/ControlPanel", "ay", "I"): "selectedLoginRestrictionMode",
    (
        "com/rs2/launcher/ControlPanelWindowCloseListener",
        "a",
        "Lcom/rs2/launcher/ControlPanel;",
    ): "controlPanel",
    (
        "com/rs2/launcher/MapHorizontalScrollListener",
        "a",
        "Lcom/rs2/launcher/ControlPanel;",
    ): "controlPanel",
    (
        "com/rs2/launcher/MapVerticalScrollListener",
        "a",
        "Lcom/rs2/launcher/ControlPanel;",
    ): "controlPanel",
    ("com/rs2/Server", "o", "Lcom/rs2/Server;"): "instance",
    ("com/rs2/Server", "p", "Ljava/lang/String;"): "bindHost",
    ("com/rs2/Server", "q", "I"): "port",
    ("com/rs2/Server", "r", "I"): "cycleMillis",
    ("com/rs2/Server", "s", "J"): "elapsedMinutes",
    ("com/rs2/Server", "t", "Ljava/util/ArrayList;"): "startingRareItems",
    ("com/rs2/Server", "a", "Ljava/util/ArrayList;"): "trackedRareItems",
    ("com/rs2/Server", "u", "Ljava/nio/channels/Selector;"): "selector",
    ("com/rs2/net/DedicatedReactor", "a", "Lcom/rs2/net/DedicatedReactor;"): "instance",
    ("com/rs2/net/DedicatedReactor", "b", "Ljava/nio/channels/Selector;"): "selector",
    ("com/rs2/net/IsaacCipher", "a", "I"): "count",
    ("com/rs2/net/IsaacCipher", "b", "[I"): "results",
    ("com/rs2/net/IsaacCipher", "c", "[I"): "memory",
    ("com/rs2/net/IsaacCipher", "d", "I"): "accumulator",
    ("com/rs2/net/IsaacCipher", "e", "I"): "lastResult",
    ("com/rs2/net/IsaacCipher", "f", "I"): "counter",
    ("com/rs2/Server", "v", "Ljava/net/InetSocketAddress;"): "bindAddress",
    ("com/rs2/Server", "w", "Ljava/nio/channels/ServerSocketChannel;"): "serverSocketChannel",
    ("com/rs2/Server", "x", "Lcom/rs2/util/ElapsedTimer;"): "cycleTimer",
    ("com/rs2/Server", "y", "Ljava/util/Queue;"): "loginQueue",
    ("com/rs2/Server", "z", "Ljava/util/Queue;"): "disconnectQueue",
    ("com/rs2/Server", "A", "Ljava/lang/Thread;"): "serverThread",
    ("com/rs2/Server", "B", "Ljava/lang/Thread;"): "engineThread",
    ("com/rs2/Server", "b", "I"): "serverStatus",
    ("com/rs2/Server", "c", "Z"): "shutdownRequested",
    ("com/rs2/Server", "d", "I"): "runtimeMinutes",
    ("com/rs2/Server", "e", "Z"): "backupPending",
    ("com/rs2/Server", "C", "Z"): "hourlyBackupArmed",
    ("com/rs2/Server", "f", "Z"): "halloweenEventActive",
    ("com/rs2/Server", "g", "Z"): "christmasEventActive",
    ("com/rs2/Server", "h", "Z"): "easterEventActive",
    ("com/rs2/Server", "i", "I"): "messageOfTheWeekIndex",
    ("com/rs2/Server", "D", "I"): "botLoginBatchIndex",
    ("com/rs2/BotLoginBatchTask", "a", "I"): "finalBatchIndex",
    ("com/rs2/model/skill/SkillManager", "a", "[Ljava/lang/String;"): "SKILL_NAMES",
    ("com/rs2/model/skill/SkillManager", "b", "I"): "maxCombatLevel",
    ("com/rs2/model/skill/SkillManager", "c", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/skill/SkillManager", "d", "[I"): "currentLevels",
    ("com/rs2/model/skill/SkillManager", "e", "[D"): "experience",
    ("com/rs2/model/skill/SkillManager", "f", "[Lcom/rs2/model/task/TickTask;"): "levelRestoreTasks",
    ("com/rs2/model/skill/SkillManager", "g", "[I"): "restoreDelayTicks",
    ("com/rs2/model/skill/SkillManager", "h", "Lcom/rs2/model/task/TickTask;"): "specialEnergyRestoreTask",
    ("com/rs2/model/skill/SkillManager", "i", "[I"): "experienceForLevel",
    ("com/rs2/model/skill/SkillManager", "j", "J"): "actionDelayExpiresAtMillis",
    ("com/rs2/model/skill/SkillManager", "k", "J"): "drinkDelayExpiresAtMillis",
    ("com/rs2/model/skill/SkillLevelRestoreTask", "a", "Lcom/rs2/model/skill/SkillManager;"): "skillManager",
    ("com/rs2/model/skill/SkillLevelRestoreTask", "b", "I"): "skillId",
    ("com/rs2/model/skill/SpecialEnergyRestoreTask", "a", "Lcom/rs2/model/skill/SkillManager;"): "skillManager",
    ("com/rs2/Server", "E", "I"): "configuredBotCount",
    ("com/rs2/Server", "j", "I"): "botLoginBatchSize",
    ("com/rs2/Server", "k", "I"): "botLoginBatchIntervalTicks",
    ("com/rs2/Server", "F", "I"): "wildernessBotLoginCount",
    ("com/rs2/Server", "G", "I"): "skillingBotLoginCount",
    ("com/rs2/Server", "H", "I"): "progressiveBotLoginCount",
    ("com/rs2/Server", "I", "I"): "tradeBotLoginCount",
    ("com/rs2/Server", "J", "I"): "clanWarsBotLoginCount",
    ("com/rs2/Server", "K", "I"): "otherBotLoginCount",
    ("com/rs2/Server", "l", "I"): "onlinePlayerCount",
    ("com/rs2/Server", "m", "I"): "adminPlayerCount",
    ("com/rs2/Server", "n", "I"): "moderatorPlayerCount",
    ("com/rs2/model/World", "a", "[Lcom/rs2/model/player/Player;"): "players",
    ("com/rs2/model/World", "b", "I"): "tickCount",
    ("com/rs2/model/World", "c", "[Lcom/rs2/model/npc/Npc;"): "npcs",
    ("com/rs2/model/World", "d", "Lcom/rs2/model/task/TaskScheduler;"): "taskScheduler",
    ("com/rs2/model/World", "e", "[Lcom/rs2/model/npc/NpcDefinition;"): "npcDefinitions",
    ("com/rs2/model/World", "f", "Lcom/rs2/model/World;"): "instance",
    ("com/rs2/model/World", "g", "Lcom/rs2/model/objects/WorldObjectRegionIndex;"): "objectRegionIndex",
    ("com/rs2/model/task/ElapsedTicks", "a", "I"): "lastResetTick",
    ("com/rs2/model/Position", "a", "I"): "x",
    ("com/rs2/model/Position", "b", "I"): "y",
    ("com/rs2/model/Position", "c", "I"): "plane",
    ("com/rs2/model/Position", "d", "I"): "previousX",
    ("com/rs2/model/Position", "e", "I"): "previousY",
    ("com/rs2/model/EntityReference", "a", "I"): "referenceId",
    ("com/rs2/model/EntityReference", "b", "Z"): "playerReference",
    ("com/rs2/model/EntityReference", "c", "Lcom/rs2/model/Entity;"): "cachedEntity",
    ("com/rs2/model/EntityReference", "d", "Ljava/lang/String;"): "playerUsername",
    ("com/rs2/model/gameplay/PositionRange", "a", "Lcom/rs2/model/Position;"): "minPosition",
    ("com/rs2/model/gameplay/PositionRange", "b", "Lcom/rs2/model/Position;"): "maxPosition",
    ("com/rs2/model/item/ItemContainer", "a", "I"): "capacity",
    ("com/rs2/model/item/ItemContainer", "b", "[Lcom/rs2/model/item/ItemStack;"): "items",
    ("com/rs2/model/item/ItemContainer", "c", "Ljava/util/List;"): "updateListeners",
    ("com/rs2/model/item/ItemContainer", "d", "Ljava/util/ArrayList;"): "tabs",
    ("com/rs2/model/item/ItemContainer", "e", "I"): "tabLimit",
    ("com/rs2/model/item/ItemContainer", "f", "Lcom/rs2/model/item/ItemContainerType;"): "containerType",
    ("com/rs2/model/item/ItemContainer", "g", "Z"): "updatesEnabled",
    ("com/rs2/model/item/ItemContainerTab", "a", "Ljava/util/ArrayList;"): "items",
    ("com/rs2/model/item/ItemContainerTab", "b", "Z"): "persistent",
    ("com/rs2/model/item/ItemStack", "a", "I"): "id",
    ("com/rs2/model/item/ItemStack", "b", "I"): "amount",
    ("com/rs2/model/item/ItemStack", "c", "I"): "metadata",
    ("com/rs2/model/player/InventoryManager", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/player/InventoryManager", "b", "Lcom/rs2/model/item/ItemContainer;"): "container",
    ("com/rs2/model/player/EquipmentManager", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/player/EquipmentManager", "b", "Lcom/rs2/model/item/ItemContainer;"): "container",
    ("com/rs2/model/player/EquipmentManager", "c", "[I"): "equipSoundIds",
    ("com/rs2/model/player/CharacterFileBankTab", "a", "Ljava/util/ArrayList;"): "items",
    ("com/rs2/model/player/BankManager", "a", "I"): "freeBankCapacity",
    ("com/rs2/model/player/BankManager", "b", "I"): "memberBankCapacity",
    ("com/rs2/model/player/BankManager", "d", "I"): "mainBankContainerInterfaceId",
    ("com/rs2/model/player/BankManager", "c", "[Lcom/rs2/model/player/PlayerUpdateTask;"): "bankTabUpdateTasks",
    ("com/rs2/model/player/PlayerUpdateTask", "a", "I"): "itemContainerInterfaceId",
    ("com/rs2/model/player/PlayerUpdateTask", "b", "I"): "bankTabButtonId",
    ("com/rs2/model/player/Player", "eW", "Ljava/util/List;"): "localPlayers",
    ("com/rs2/model/player/Player", "eX", "Ljava/util/List;"): "localNpcs",
    ("com/rs2/model/player/Player", "A", "Ljava/util/ArrayList;"): "temporaryCutsceneNpcs",
    ("com/rs2/model/player/Player", "ev", "Z"): "cutsceneActive",
    ("com/rs2/model/player/Player", "is", "Z"): "suppressTeleportCleanup",
    ("com/rs2/model/player/Player", "ew", "Z"): "planeChangeRefreshPending",
    ("com/rs2/model/player/Player", "dt", "Ljava/lang/String;"): "pendingCropResurrectionPatchType",
    ("com/rs2/model/player/Player", "ds", "I"): "pendingCropResurrectionPatchIndex",
    ("com/rs2/model/player/Player", "eA", "I"): "prayerDrainAccumulator",
    ("com/rs2/model/player/Player", "eB", "I"): "prayerDrainRate",
    ("com/rs2/model/player/Player", "eC", "Z"): "grandExchangeSettlementInProgress",
    ("com/rs2/model/player/Player", "gy", "I"): "loginResponseCode",
    ("com/rs2/model/player/Player", "gz", "Lcom/rs2/model/player/TradeState;"): "tradeState",
    ("com/rs2/model/player/Player", "gA", "[I"): "queuedLoginItemIds",
    ("com/rs2/model/player/Player", "gB", "[I"): "queuedLoginItemAmounts",
    ("com/rs2/model/player/Player", "gc", "Lcom/rs2/model/item/ItemContainer;"): "bankContainer",
    ("com/rs2/model/player/Player", "gd", "Lcom/rs2/model/item/ItemContainer;"): "tradeOfferContainer",
    ("com/rs2/model/player/Player", "ge", "Lcom/rs2/model/item/ItemContainer;"): "partyRoomContainer",
    ("com/rs2/model/player/Player", "eY", "Lcom/rs2/model/player/InventoryManager;"): "inventoryManager",
    ("com/rs2/model/player/Player", "eZ", "Lcom/rs2/model/player/EquipmentManager;"): "equipmentManager",
    ("com/rs2/model/player/Player", "fa", "Lcom/rs2/model/player/SocialManager;"): "socialManager",
    ("com/rs2/model/player/Player", "fb", "Lcom/rs2/model/skill/prayer/PrayerManager;"): "prayerManager",
    ("com/rs2/model/player/Player", "fc", "Lcom/rs2/model/skill/magic/TeleportManager;"): "teleportManager",
    ("com/rs2/model/player/Player", "fd", "Lcom/rs2/model/player/EmoteManager;"): "emoteManager",
    ("com/rs2/model/player/Player", "fe", "Lcom/rs2/model/skill/SkillManager;"): "skillManager",
    ("com/rs2/model/player/Player", "ff", "Lcom/rs2/model/quest/QuestManager;"): "questManager",
    ("com/rs2/model/player/Player", "fg", "Lcom/rs2/model/skill/runecrafting/RunecraftingObjectHandler;"): "runecraftingObjectHandler",
    ("com/rs2/model/player/Player", "fh", "Lcom/rs2/model/skill/slayer/SlayerManager;"): "slayerManager",
    ("com/rs2/model/player/Player", "fi", "Lcom/rs2/model/gameplay/duel/DuelController;"): "duelController",
    ("com/rs2/model/player/Player", "fj", "Lcom/rs2/model/gameplay/duel/DuelSession;"): "duelSession",
    ("com/rs2/model/player/Player", "fk", "Lcom/rs2/model/gameplay/fightcave/FightCaveController;"): "fightCaveController",
    ("com/rs2/model/player/Player", "fl", "Lcom/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController;"): "alchemistPlaygroundController",
    ("com/rs2/model/player/Player", "fm", "Lcom/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController;"): "creatureGraveyardController",
    ("com/rs2/model/player/Player", "fn", "Lcom/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController;"): "telekineticTheatreController",
    ("com/rs2/model/player/Player", "fo", "Lcom/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController;"): "enchantmentChamberController",
    ("com/rs2/model/player/Player", "fp", "Lcom/rs2/model/gameplay/duel/DuelInterfaceManager;"): "duelInterfaceManager",
    ("com/rs2/model/player/Player", "fq", "Lcom/rs2/model/gameplay/duel/DuelArenaLocationManager;"): "duelArenaLocationManager",
    ("com/rs2/model/player/Player", "fs", "Lcom/rs2/model/skill/fishing/FishingHandler;"): "fishingHandler",
    ("com/rs2/model/player/Player", "ft", "Lcom/rs2/model/skill/ItemCombinationHandler;"): "itemCombinationHandler",
    ("com/rs2/model/player/Player", "fu", "Lcom/rs2/model/skill/guide/SkillGuideManager;"): "skillGuideManager",
    ("com/rs2/model/player/Player", "fv", "Lcom/rs2/model/item/consumable/FoodHandler;"): "foodHandler",
    ("com/rs2/model/player/Player", "fw", "Lcom/rs2/model/item/consumable/PotionHandler;"): "potionHandler",
    ("com/rs2/model/player/Player", "fx", "Lcom/rs2/model/skill/mining/MiningManager;"): "miningManager",
    ("com/rs2/model/player/Player", "fy", "Lcom/rs2/model/skill/cooking/CookingManager;"): "cookingManager",
    ("com/rs2/model/player/Player", "fz", "Lcom/rs2/model/skill/farming/CompostBinManager;"): "compostBinManager",
    ("com/rs2/model/player/Player", "fA", "Lcom/rs2/model/skill/farming/AllotmentPatchManager;"): "allotmentPatchManager",
    ("com/rs2/model/player/Player", "fB", "Lcom/rs2/model/skill/farming/FlowerPatchManager;"): "flowerPatchManager",
    ("com/rs2/model/player/Player", "fC", "Lcom/rs2/model/skill/farming/HerbPatchManager;"): "herbPatchManager",
    ("com/rs2/model/player/Player", "fD", "Lcom/rs2/model/skill/farming/HopsPatchManager;"): "hopsPatchManager",
    ("com/rs2/model/player/Player", "fE", "Lcom/rs2/model/skill/farming/BushPatchManager;"): "bushPatchManager",
    ("com/rs2/model/player/Player", "fF", "Lcom/rs2/model/skill/farming/PlantPotHandler;"): "plantPotHandler",
    ("com/rs2/model/player/Player", "fG", "Lcom/rs2/model/skill/farming/TreePatchManager;"): "treePatchManager",
    ("com/rs2/model/player/Player", "fH", "Lcom/rs2/model/skill/farming/FruitTreePatchManager;"): "fruitTreePatchManager",
    ("com/rs2/model/player/Player", "fI", "Lcom/rs2/model/skill/farming/SpecialTreePatchManager;"): "specialTreePatchManager",
    ("com/rs2/model/player/Player", "fJ", "Lcom/rs2/model/skill/farming/SpecialCropPatchManager;"): "specialCropPatchManager",
    ("com/rs2/model/player/Player", "fK", "Lcom/rs2/model/skill/farming/FarmingToolStore;"): "farmingToolStore",
    ("com/rs2/model/player/Player", "fL", "Lcom/rs2/model/skill/firemaking/FiremakingHandler;"): "firemakingHandler",
    ("com/rs2/model/player/Player", "fM", "Lcom/rs2/model/skill/prayer/BoneBuryingHandler;"): "boneBuryingHandler",
    ("com/rs2/model/player/Player", "fN", "Lcom/rs2/model/player/PetManager;"): "petManager",
    ("com/rs2/model/player/PetManager", "a", "[[I"): "petItemNpcPairs",
    ("com/rs2/model/player/PetManager", "b", "Lcom/rs2/model/player/Player;"): "owner",
    ("com/rs2/model/player/PetManager", "c", "Lcom/rs2/model/npc/Npc;"): "activePetNpc",
    ("com/rs2/model/player/PetManager", "d", "I"): "activePetItemId",
    ("com/rs2/model/player/Player", "fO", "Lcom/rs2/model/randomevent/sandwichlady/SandwichLadyManager;"): "sandwichLadyManager",
    ("com/rs2/model/player/Player", "fP", "Lcom/rs2/model/dialogue/DialogueManager;"): "dialogueManager",
    ("com/rs2/model/player/Player", "fQ", "Lcom/rs2/model/bankpin/BankPinManager;"): "bankPinManager",
    ("com/rs2/model/player/Player", "fR", "Lcom/rs2/net/LoginProtocol;"): "loginProtocol",
    ("com/rs2/net/LoginProtocol", "a", "Ljava/util/ArrayList;"): "activeLoginUsernames",
    ("com/rs2/model/player/Player", "du", "I"): "currentBankTab",
    ("com/rs2/model/player/Player", "go", "Z"): "bankWithdrawNoteMode",
    ("com/rs2/model/player/Player", "gp", "I"): "selectedInterfaceItemId",
    ("com/rs2/model/player/Player", "gq", "I"): "selectedInterfaceSlot",
    ("com/rs2/model/player/Player", "gr", "I"): "selectedInterfaceId",
    ("com/rs2/model/player/Player", "eU", "I"): "openInterfaceId",
    ("com/rs2/model/player/Player", "hW", "I"): "inventoryOverlayInterfaceId",
    ("com/rs2/model/player/Player", "hP", "[I"): "sidebarInterfaceIds",
    ("com/rs2/model/player/Player", "a", "I"): "combatLevel",
    ("com/rs2/model/player/Player", "G", "Ljava/lang/String;"): "interfaceAction",
    ("com/rs2/model/player/Player", "gs", "Lcom/rs2/model/player/BankRearrangeMode;"): "bankRearrangeMode",
    ("com/rs2/model/player/BankRearrangeMode", "a", "Lcom/rs2/model/player/BankRearrangeMode;"): "SWAP",
    ("com/rs2/model/player/BankRearrangeMode", "b", "Lcom/rs2/model/player/BankRearrangeMode;"): "INSERT",
    ("com/rs2/model/player/PlayerConnectionState", "a", "Lcom/rs2/model/player/PlayerConnectionState;"): "HANDSHAKE",
    ("com/rs2/model/player/PlayerConnectionState", "b", "Lcom/rs2/model/player/PlayerConnectionState;"): "LOGIN_PAYLOAD",
    ("com/rs2/model/player/PlayerConnectionState", "c", "Lcom/rs2/model/player/PlayerConnectionState;"): "LOGIN_QUEUED",
    ("com/rs2/model/player/PlayerConnectionState", "d", "Lcom/rs2/model/player/PlayerConnectionState;"): "IN_GAME",
    ("com/rs2/model/player/PlayerConnectionState", "e", "Lcom/rs2/model/player/PlayerConnectionState;"): "DISCONNECTING",
    ("com/rs2/model/player/PlayerConnectionState", "f", "Lcom/rs2/model/player/PlayerConnectionState;"): "DISCONNECTED",
    ("com/rs2/launcher/ConsolePrintStream", "a", "Ljavax/swing/JTextArea;"): "outputTextArea",
    ("com/rs2/model/player/Player", "de", "Z"): "isBot",
    ("com/rs2/bot/BotPlayer", "de", "Z"): "isBot",
    ("com/rs2/model/player/Player", "az", "I"): "botMode",
    ("com/rs2/model/player/Player", "hV", "Ljava/lang/String;"): "hostAddress",
    ("com/rs2/model/player/Player", "ii", "Ljava/lang/String;"): "profileString1",
    ("com/rs2/model/player/Player", "ij", "Ljava/lang/String;"): "profileString2",
    ("com/rs2/model/player/Player", "cb", "Ljava/lang/String;"): "reservedVersion11String",
    ("com/rs2/model/player/Player", "ef", "Z"): "loginRestrictionExempt",
    ("com/rs2/model/player/Player", "fU", "Z"): "memberFlag",
    ("com/rs2/model/player/Player", "gI", "I"): "donatorPoints",
    ("com/rs2/model/player/Player", "dS", "I"): "legacyQuestPoints",
    ("com/rs2/model/player/Player", "bS", "I"): "barrowsRewardPotential",
    ("com/rs2/model/player/Player", "cm", "I"): "familyCrestGauntletItemId",
    ("com/rs2/model/player/Player", "df", "Z"): "bonesToPeachesUnlocked",
    ("com/rs2/model/player/Player", "dK", "I"): "mageArenaFlamesOfZamorakCastsRemaining",
    ("com/rs2/model/player/Player", "dL", "I"): "mageArenaClawsOfGuthixCastsRemaining",
    ("com/rs2/model/player/Player", "dM", "I"): "mageArenaSaradominStrikeCastsRemaining",
    ("com/rs2/model/player/Player", "dN", "I"): "mageArenaProgressStage",
    ("com/rs2/model/player/Player", "ed", "I"): "barrowsRunsCompleted",
    ("com/rs2/model/player/Player", "f", "I"): "movementSystemMode",
    ("com/rs2/model/player/Player", "i", "I"): "currentLevelUpSkillId",
    ("com/rs2/model/player/Player", "j", "Z"): "deferLevelUpInterfaces",
    ("com/rs2/model/player/Player", "k", "Ljava/util/ArrayList;"): "queuedLevelUpSkillIds",
    ("com/rs2/model/player/Player", "p", "J"): "dragonfireShieldLastOperateMillis",
    ("com/rs2/model/player/Player", "t", "Z"): "infiniteRunEnabled",
    ("com/rs2/model/player/Player", "P", "I"): "fremennikDoorRiddleFirstLetterIndex",
    ("com/rs2/model/player/Player", "Q", "I"): "fremennikDoorRiddleSecondLetterIndex",
    ("com/rs2/model/player/Player", "R", "I"): "fremennikDoorRiddleThirdLetterIndex",
    ("com/rs2/model/player/Player", "S", "I"): "fremennikDoorRiddleFourthLetterIndex",
    ("com/rs2/model/player/Player", "T", "I"): "canoeStationIndex",
    ("com/rs2/model/player/Player", "U", "I"): "builtCanoeTypeConfigValue",
    ("com/rs2/model/player/Player", "V", "[Lcom/rs2/model/item/ItemStack;"): "clueRequiredItems",
    ("com/rs2/model/player/Player", "W", "I"): "activeBookItemId",
    ("com/rs2/model/player/Player", "X", "I"): "activeBookPageIndex",
    ("com/rs2/model/player/Player", "ak", "I"): "npcTransformationId",
    ("com/rs2/model/player/Player", "al", "D"): "carriedWeight",
    ("com/rs2/model/player/Player", "am", "D"): "sextantSunAngleDegrees",
    ("com/rs2/model/player/Player", "an", "I"): "sextantSunVerticalOffset",
    ("com/rs2/model/player/Player", "ao", "I"): "sextantSunHorizontalOffset",
    ("com/rs2/model/player/Player", "ap", "I"): "sextantHorizonRotation",
    ("com/rs2/model/player/Player", "aq", "I"): "sextantHorizonVerticalOffset",
    ("com/rs2/model/player/Player", "ar", "I"): "treasureTrailStepCount",
    ("com/rs2/model/player/Player", "at", "I"): "activeClueLevel",
    ("com/rs2/model/player/Player", "au", "I"): "activeClueItemId",
    ("com/rs2/model/player/Player", "av", "[Lcom/rs2/model/item/ItemStack;"): "sliderPuzzlePieces",
    ("com/rs2/model/player/Player", "z", "Lcom/rs2/model/Position;"): "cutsceneReturnPosition",
    ("com/rs2/model/player/Player", "dP", "Z"): "godModeEnabled",
    ("com/rs2/model/player/Player", "dT", "I"): "npcKillCount",
    ("com/rs2/model/player/Player", "dU", "I"): "playerKillCount",
    ("com/rs2/model/player/Player", "dV", "I"): "deathCount",
    ("com/rs2/model/player/Player", "dW", "I"): "easyCluesCompleted",
    ("com/rs2/model/player/Player", "dX", "I"): "mediumCluesCompleted",
    ("com/rs2/model/player/Player", "dY", "I"): "hardCluesCompleted",
    ("com/rs2/model/player/Player", "dZ", "I"): "soldItemsValue",
    ("com/rs2/model/quest/impl/FremennikTrialsQuest", "i", "[Ljava/lang/String;"): "doorRiddleLetters",
    ("com/rs2/model/quest/impl/FremennikTrialsQuest", "j", "Ljava/util/List;"): "doorRiddleAnswers",
    ("com/rs2/model/player/Player", "ea", "I"): "boughtItemsValue",
    ("com/rs2/model/player/Player", "eb", "I"): "duelWins",
    ("com/rs2/model/player/Player", "ec", "I"): "duelLosses",
    ("com/rs2/model/player/Player", "o", "J"): "godWarsLastAltarBlessingMillis",
    ("com/rs2/model/player/Player", "m", "[I"): "godWarsKillCounts",
    ("com/rs2/model/player/Player", "n", "[I"): "lastDisplayedGodWarsKillCounts",
    ("com/rs2/model/player/Player", "q", "Lcom/rs2/model/player/PlayerGroup;"): "currentGroup",
    ("com/rs2/model/player/Player", "r", "Lcom/rs2/model/player/PlayerGroup;"): "pendingGroupCleanup",
    ("com/rs2/model/player/Player", "s", "Lcom/rs2/model/player/Player;"): "pendingGroupInviteTarget",
    ("com/rs2/model/player/Player", "v", "I"): "groupLootRoll",
    ("com/rs2/model/player/Player", "w", "I"): "craftingThreadUseCount",
    ("com/rs2/model/player/Player", "cc", "J"): "membershipExpiresMillis",
    ("com/rs2/model/player/Player", "eD", "I"): "savedCacheVersion",
    ("com/rs2/model/player/Player", "u", "I"): "godBookPageFlags",
    ("com/rs2/model/player/Player", "eF", "Z"): "swampCaveRopeAttached",
    ("com/rs2/model/player/Player", "eG", "Z"): "lampOilStillFilled",
    ("com/rs2/model/player/Player", "eE", "I"): "enterTheAbyssMiniquestState",
    ("com/rs2/model/player/Player", "fr", "Lcom/rs2/model/skill/cooking/WineFermentationHandler;"): "wineFermentationHandler",
    ("com/rs2/model/skill/cooking/WineFermentationHandler", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/skill/farming/FarmingPatchUtils", "a", "[I"): "wateredSeedlingItemIds",
    ("com/rs2/model/skill/farming/MithrilSeedFlowerHandler", "a", "[I"): "mithrilSeedFlowerObjectIds",
    ("com/rs2/model/player/Player", "dw", "[Z"): "grandExchangeSellOfferFlags",
    ("com/rs2/model/player/Player", "dx", "[I"): "grandExchangeItemIds",
    ("com/rs2/model/player/Player", "dy", "[I"): "grandExchangeQuantities",
    ("com/rs2/model/player/Player", "dz", "[I"): "grandExchangeUnitPrices",
    ("com/rs2/model/player/Player", "dA", "[Z"): "grandExchangeCancelledFlags",
    ("com/rs2/model/player/Player", "dB", "[I"): "grandExchangeCompletedQuantities",
    ("com/rs2/model/player/Player", "dC", "[I"): "grandExchangeTotalPrices",
    ("com/rs2/model/player/Player", "dD", "[I"): "grandExchangePrimaryCollectAmounts",
    ("com/rs2/model/player/Player", "dE", "[I"): "grandExchangeSecondaryCollectAmounts",
    ("com/rs2/model/player/Player", "dF", "[Z"): "grandExchangeFinishMessagePending",
    ("com/rs2/model/player/Player", "dG", "I"): "selectedGrandExchangeItemId",
    ("com/rs2/model/player/Player", "dH", "I"): "selectedGrandExchangeQuantity",
    ("com/rs2/model/player/Player", "dI", "I"): "selectedGrandExchangeUnitPrice",
    ("com/rs2/model/player/Player", "dJ", "I"): "selectedGrandExchangeSlot",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "a",
        "I",
    ): "pizazzPoints",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "b",
        "I",
    ): "mazeIndex",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "c",
        "Z",
    ): "mazeSolved",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "d",
        "I",
    ): "consecutiveMazesSolved",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "e",
        "[Lcom/rs2/model/Position;",
    ): "mazeEntryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "f",
        "[Lcom/rs2/model/Position;",
    ): "guardianSpawnPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "g",
        "[Lcom/rs2/model/Position;",
    ): "mazeItemSpawnPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "h",
        "[Lcom/rs2/model/Position;",
    ): "mazeTargetPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "i",
        "[Lcom/rs2/model/Position;",
    ): "upperLeftBoundaryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "j",
        "[Lcom/rs2/model/Position;",
    ): "lowerLeftBoundaryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "k",
        "[Lcom/rs2/model/Position;",
    ): "upperRightBoundaryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "l",
        "[Lcom/rs2/model/Position;",
    ): "lowerRightBoundaryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "m",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "n",
        "I",
    ): "mazeItemId",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "o",
        "I",
    ): "guardianNpcId",
    (
        "com/rs2/model/gameplay/magetrainingarena/TelekineticTheatreController",
        "p",
        "Ljava/util/Random;",
    ): "random",
    (
        "com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaLobby",
        "a",
        "Lcom/rs2/model/Position;",
    ): "LOBBY_POSITION",
    (
        "com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardShop",
        "a",
        "[Lcom/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardDefinition;",
    ): "rewardDefinitions",
    (
        "com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardDefinition",
        "a",
        "I",
    ): "itemId",
    (
        "com/rs2/model/gameplay/magetrainingarena/MageTrainingArenaRewardDefinition",
        "b",
        "[I",
    ): "pizazzPointCosts",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "a",
        "I",
    ): "pizazzPoints",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "b",
        "Ljava/util/Random;",
    ): "random",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "c",
        "[Lcom/rs2/model/Position;",
    ): "entryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "d",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "e",
        "[I",
    ): "bonusRuneRewardItemIds",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "f",
        "[I",
    ): "enchantmentOrbCountsBySpellTier",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "g",
        "[Ljava/lang/String;",
    ): "bonusColorNames",
    (
        "com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController",
        "h",
        "Ljava/lang/String;",
    ): "currentBonusColor",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "a",
        "I",
    ): "pizazzPoints",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "b",
        "Ljava/util/Random;",
    ): "random",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "c",
        "I",
    ): "currentFreeAlchemyItemId",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "d",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "e",
        "I",
    ): "rewardCoinsOnExit",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "f",
        "[Lcom/rs2/model/Position;",
    ): "entryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "g",
        "[I",
    ): "alchemyValuePool",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "h",
        "[I",
    ): "alchemyItemIds",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "i",
        "I",
    ): "item6897CupboardObjectId",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "j",
        "I",
    ): "item6896CupboardObjectId",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "k",
        "I",
    ): "item6895CupboardObjectId",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "l",
        "I",
    ): "item6894CupboardObjectId",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "m",
        "I",
    ): "item6893CupboardObjectId",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "n",
        "I",
    ): "item6897AlchemyValue",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "o",
        "I",
    ): "item6896AlchemyValue",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "p",
        "I",
    ): "item6895AlchemyValue",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "q",
        "I",
    ): "item6894AlchemyValue",
    (
        "com/rs2/model/gameplay/magetrainingarena/AlchemistPlaygroundController",
        "r",
        "I",
    ): "item6893AlchemyValue",
    (
        "com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController",
        "a",
        "I",
    ): "pizazzPoints",
    (
        "com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController",
        "b",
        "[Lcom/rs2/model/Position;",
    ): "entryPositions",
    (
        "com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController",
        "c",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController",
        "d",
        "Ljava/util/Random;",
    ): "random",
    (
        "com/rs2/model/gameplay/magetrainingarena/CreatureGraveyardController",
        "e",
        "[I",
    ): "fruitChuteRewardRuneItemIds",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "a", "Z"): "balloonDropPending",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomManager",
        "i",
        "I",
    ): "partyChestCapacity",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomManager",
        "c",
        "Lcom/rs2/model/item/ItemContainer;",
    ): "partyChestContainer",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomManager",
        "d",
        "Ljava/util/ArrayList;",
    ): "activeBalloonObjects",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomManager",
        "e",
        "Ljava/util/ArrayList;",
    ): "balloonRewards",
    ("com/rs2/model/gameplay/partyroom/PartyRoomManager", "f", "I"): "partyChestValue",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomManager",
        "g",
        "Lcom/rs2/model/task/TickTask;",
    ): "balloonDropTask",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomBalloonSpawnTask",
        "a",
        "Ljava/util/ArrayList;",
    ): "balloonPositions",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomBalloonSpawnTask",
        "b",
        "Ljava/util/ArrayList;",
    ): "rewardItems",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomBalloonReward",
        "a",
        "Lcom/rs2/model/item/ItemStack;",
    ): "rewardItem",
    (
        "com/rs2/model/gameplay/partyroom/PartyRoomBalloonReward",
        "b",
        "Lcom/rs2/model/Position;",
    ): "balloonPosition",
    (
        "com/rs2/model/gameplay/CaveLightManager",
        "a",
        "[Lcom/rs2/util/RectangularArea;",
    ): "swampGasAreas",
    ("com/rs2/model/gameplay/CaveLightManager", "b", "[I"): "caveInsectRegionIds",
    ("com/rs2/model/gameplay/DesertHeatManager", "a", "I"): "DESERT_SHIRT_ITEM_ID",
    ("com/rs2/model/gameplay/DesertHeatManager", "b", "I"): "DESERT_ROBE_ITEM_ID",
    ("com/rs2/model/gameplay/DesertHeatManager", "c", "I"): "DESERT_BOOTS_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "d",
        "I",
    ): "MENAPHITE_PURPLE_HAT_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "e",
        "I",
    ): "MENAPHITE_PURPLE_TOP_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "f",
        "I",
    ): "MENAPHITE_PURPLE_ROBE_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "g",
        "I",
    ): "MENAPHITE_PURPLE_KILT_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "h",
        "I",
    ): "MENAPHITE_RED_HAT_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "i",
        "I",
    ): "MENAPHITE_RED_TOP_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "j",
        "I",
    ): "MENAPHITE_RED_ROBE_ITEM_ID",
    (
        "com/rs2/model/gameplay/DesertHeatManager",
        "k",
        "I",
    ): "MENAPHITE_RED_KILT_ITEM_ID",
    ("com/rs2/model/gameplay/DesertHeatManager", "l", "[I"): "desertHeatRegionIds",
    ("com/rs2/model/player/Player", "er", "I"): "activeEnvironmentalHazardId",
    ("com/rs2/model/player/Player", "eH", "I"): "caveInsectSwarmStage",
    ("com/rs2/model/player/Player", "eI", "I"): "swampGasFlareState",
    (
        "com/rs2/model/gameplay/CaveInsectSwarmTask",
        "a",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/CaveInsectDamageTask",
        "a",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/SwampGasExplosionTask",
        "a",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/DesertThirstTask",
        "a",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/item/action/CaveLightSourceDefinition",
        "k",
        "I",
    ): "firemakingLevelRequirement",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "l", "I"): "unlitItemId",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "m", "I"): "litItemId",
    ("com/rs2/model/item/action/CaveLightSourceDefinition", "n", "I"): "lightLevel",
    (
        "com/rs2/model/item/action/CaveLightSourceDefinition",
        "o",
        "Z",
    ): "flaresInSwampGas",
    (
        "com/rs2/model/item/action/CaveLightSourceDefinition",
        "p",
        "Ljava/util/Map;",
    ): "definitionsByItemId",
    ("com/rs2/model/gameplay/abyss/AbyssManager", "a", "[I"): "POUCH_ITEM_IDS",
    ("com/rs2/model/gameplay/abyss/AbyssObstacleEvent", "a", "I"): "remainingObstaclePhase",
    (
        "com/rs2/model/gameplay/abyss/AbyssObstacleEvent",
        "b",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    (
        "com/rs2/model/gameplay/abyss/AbyssObstacleEvent",
        "c",
        "Ljava/lang/String;",
    ): "obstacleAction",
    ("com/rs2/model/gameplay/abyss/AbyssObstacleEvent", "d", "I"): "temporaryObjectId",
    ("com/rs2/model/gameplay/abyss/AbyssObstacleEvent", "e", "I"): "objectX",
    ("com/rs2/model/gameplay/abyss/AbyssObstacleEvent", "f", "I"): "objectY",
    (
        "com/rs2/model/gameplay/abyss/AbyssObstacleEvent",
        "g",
        "Lcom/rs2/model/objects/LoadedWorldObject;",
    ): "sourceObject",
    ("com/rs2/model/gameplay/abyss/AbyssObstacleEvent", "h", "I"): "finalObjectId",
    ("com/rs2/model/gameplay/abyss/AbyssObstacleEvent", "i", "I"): "destinationX",
    ("com/rs2/model/gameplay/abyss/AbyssObstacleEvent", "j", "I"): "destinationY",
    (
        "com/rs2/model/gameplay/abyss/AbyssDelayedMoveEvent",
        "a",
        "Lcom/rs2/model/player/Player;",
    ): "player",
    ("com/rs2/model/gameplay/abyss/AbyssDelayedMoveEvent", "b", "I"): "destinationX",
    ("com/rs2/model/gameplay/abyss/AbyssDelayedMoveEvent", "c", "I"): "destinationY",
    ("com/rs2/model/dialogue/DialogueManager", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/dialogue/DialogueManager", "b", "I"): "dialogueId",
    ("com/rs2/model/dialogue/DialogueManager", "c", "I"): "dialogueType",
    ("com/rs2/model/dialogue/DialogueManager", "f", "I"): "dialogueStep",
    ("com/rs2/model/dialogue/DialogueManager", "h", "I"): "dialogueNpcId",
    ("com/rs2/model/dialogue/DialogueManager", "g", "I"): "dialogueContextId",
    ("com/rs2/model/dialogue/DialogueManager", "d", "I"): "dialogueContextX",
    ("com/rs2/model/dialogue/DialogueManager", "e", "I"): "dialogueContextY",
    ("com/rs2/model/combat/WeaponProfile", "a", "Lcom/rs2/model/combat/WeaponProfile;"): "FISTS",
    ("com/rs2/model/combat/WeaponProfile", "j", "Lcom/rs2/model/combat/WeaponProfile;"): "SHORT_BOW",
    ("com/rs2/model/combat/WeaponProfile", "b", "Lcom/rs2/model/combat/WeaponProfile;"): "SPECIAL_BOW",
    ("com/rs2/model/combat/WeaponProfile", "c", "Lcom/rs2/model/combat/WeaponProfile;"): "CRYSTAL_BOW",
    ("com/rs2/model/combat/WeaponProfile", "k", "Lcom/rs2/model/combat/WeaponProfile;"): "OGRE_BOW",
    ("com/rs2/model/combat/WeaponProfile", "l", "Lcom/rs2/model/combat/WeaponProfile;"): "OGRE_COMP_BOW",
    ("com/rs2/model/combat/WeaponProfile", "d", "Lcom/rs2/model/combat/WeaponProfile;"): "LONG_BOW",
    ("com/rs2/model/combat/WeaponProfile", "m", "Lcom/rs2/model/combat/WeaponProfile;"): "WAND",
    ("com/rs2/model/combat/WeaponProfile", "n", "Lcom/rs2/model/combat/WeaponProfile;"): "STAFF",
    ("com/rs2/model/combat/WeaponProfile", "o", "Lcom/rs2/model/combat/WeaponProfile;"): "SPECSTAFF",
    ("com/rs2/model/combat/WeaponProfile", "p", "Lcom/rs2/model/combat/WeaponProfile;"): "THROWING_KNIFE",
    ("com/rs2/model/combat/WeaponProfile", "q", "Lcom/rs2/model/combat/WeaponProfile;"): "THROWING_DART",
    ("com/rs2/model/combat/WeaponProfile", "r", "Lcom/rs2/model/combat/WeaponProfile;"): "JAVELIN",
    ("com/rs2/model/combat/WeaponProfile", "s", "Lcom/rs2/model/combat/WeaponProfile;"): "THROWING_AXE",
    ("com/rs2/model/combat/WeaponProfile", "e", "Lcom/rs2/model/combat/WeaponProfile;"): "HALBERD",
    ("com/rs2/model/combat/WeaponProfile", "t", "Lcom/rs2/model/combat/WeaponProfile;"): "MACE",
    ("com/rs2/model/combat/WeaponProfile", "u", "Lcom/rs2/model/combat/WeaponProfile;"): "SPEAR",
    ("com/rs2/model/combat/WeaponProfile", "v", "Lcom/rs2/model/combat/WeaponProfile;"): "DRAGON_DAGGER",
    ("com/rs2/model/combat/WeaponProfile", "w", "Lcom/rs2/model/combat/WeaponProfile;"): "DAGGER",
    ("com/rs2/model/combat/WeaponProfile", "x", "Lcom/rs2/model/combat/WeaponProfile;"): "TWO_HANDED",
    ("com/rs2/model/combat/WeaponProfile", "y", "Lcom/rs2/model/combat/WeaponProfile;"): "PICKAXE",
    ("com/rs2/model/combat/WeaponProfile", "z", "Lcom/rs2/model/combat/WeaponProfile;"): "AXE",
    ("com/rs2/model/combat/WeaponProfile", "A", "Lcom/rs2/model/combat/WeaponProfile;"): "BATTLE_AXE",
    ("com/rs2/model/combat/WeaponProfile", "f", "Lcom/rs2/model/combat/WeaponProfile;"): "LONGSWORD",
    ("com/rs2/model/combat/WeaponProfile", "g", "Lcom/rs2/model/combat/WeaponProfile;"): "SCIMITAR",
    ("com/rs2/model/combat/WeaponProfile", "B", "Lcom/rs2/model/combat/WeaponProfile;"): "SWORD",
    ("com/rs2/model/combat/WeaponProfile", "C", "Lcom/rs2/model/combat/WeaponProfile;"): "KARILS_CROSSBOW",
    ("com/rs2/model/combat/WeaponProfile", "D", "Lcom/rs2/model/combat/WeaponProfile;"): "CROSSBOW",
    ("com/rs2/model/combat/WeaponProfile", "E", "Lcom/rs2/model/combat/WeaponProfile;"): "DHAROKS",
    ("com/rs2/model/combat/WeaponProfile", "F", "Lcom/rs2/model/combat/WeaponProfile;"): "TORAGS",
    ("com/rs2/model/combat/WeaponProfile", "G", "Lcom/rs2/model/combat/WeaponProfile;"): "AHRIMS",
    ("com/rs2/model/combat/WeaponProfile", "H", "Lcom/rs2/model/combat/WeaponProfile;"): "VERACS",
    ("com/rs2/model/combat/WeaponProfile", "I", "Lcom/rs2/model/combat/WeaponProfile;"): "GRANITE_MAUL",
    ("com/rs2/model/combat/WeaponProfile", "J", "Lcom/rs2/model/combat/WeaponProfile;"): "WHIP",
    ("com/rs2/model/combat/WeaponProfile", "K", "Lcom/rs2/model/combat/WeaponProfile;"): "OBBY_RING",
    ("com/rs2/model/combat/WeaponProfile", "L", "Lcom/rs2/model/combat/WeaponProfile;"): "OBBY_MAUL",
    ("com/rs2/model/combat/WeaponProfile", "M", "Lcom/rs2/model/combat/WeaponProfile;"): "OBBY_SWORD_AND_KNIFE",
    ("com/rs2/model/combat/WeaponProfile", "N", "Lcom/rs2/model/combat/WeaponProfile;"): "OBBY_MACE",
    ("com/rs2/model/combat/WeaponProfile", "O", "Lcom/rs2/model/combat/WeaponProfile;"): "OBBY_STAFF",
    ("com/rs2/model/combat/WeaponProfile", "P", "Lcom/rs2/model/combat/WeaponProfile;"): "WARHAMMER",
    ("com/rs2/model/combat/WeaponProfile", "Q", "Lcom/rs2/model/combat/WeaponProfile;"): "CLAWS",
    ("com/rs2/model/combat/WeaponProfile", "R", "Lcom/rs2/model/combat/WeaponProfile;"): "GODSWORD",
    ("com/rs2/model/combat/WeaponProfile", "h", "Lcom/rs2/model/combat/WeaponProfile;"): "METAL_CROSSBOW",
    ("com/rs2/model/combat/WeaponProfile", "i", "Lcom/rs2/model/combat/WeaponProfile;"): "DARK_BOW",
    ("com/rs2/model/combat/WeaponProfile", "S", "Lcom/rs2/model/combat/WeaponProfile;"): "SCYTHE",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "a", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "UNARMED",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "b", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "WHIP",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "c", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "HAMMER",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "d", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "TWO_HANDED_SWORD",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "e", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "GODSWORD",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "f", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "DAGGER",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "g", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "STAFF",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "h", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "PICKAXE",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "i", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "AXE",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "j", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "HALBERD",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "k", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "CLAWS",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "l", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "SPEAR",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "m", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "MACE",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "n", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "SCYTHE",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "o", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "BOW",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "p", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "LONG_BOW",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "q", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "THROWN",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "r", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "SLASH_SWORD",
    ("com/rs2/model/combat/WeaponProfile", "T", "Lcom/rs2/model/combat/WeaponInterfaceDefinition;"): "interfaceDefinition",
    ("com/rs2/model/combat/WeaponProfile", "U", "Lcom/rs2/model/combat/AmmunitionProfile;"): "ammunitionProfile",
    ("com/rs2/model/combat/WeaponProfile", "V", "[I"): "attackAnimations",
    ("com/rs2/model/combat/WeaponProfile", "W", "[I"): "movementAnimations",
    ("com/rs2/model/combat/WeaponProfile", "X", "I"): "blockAnimationId",
    ("com/rs2/model/combat/WeaponProfile", "Y", "I"): "attackDelay",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "t", "I"): "sidebarInterfaceId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "s", "I"): "attackSoundId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "u", "I"): "specialBarWidgetId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "v", "I"): "specialEnergyWidgetId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "w", "I"): "specialAttackButtonId",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "x", "[I"): "weaponTextWidgetIds",
    ("com/rs2/model/combat/WeaponInterfaceDefinition", "y", "[Lcom/rs2/model/combat/AttackStyleDefinition;"): "attackStyles",
    ("com/rs2/model/combat/AmmunitionProfile", "a", "Lcom/rs2/model/combat/AmmunitionProfile;"): "ARROW",
    ("com/rs2/model/combat/AmmunitionProfile", "b", "Lcom/rs2/model/combat/AmmunitionProfile;"): "OBBY_RING",
    ("com/rs2/model/combat/AmmunitionProfile", "c", "Lcom/rs2/model/combat/AmmunitionProfile;"): "KNIFE",
    ("com/rs2/model/combat/AmmunitionProfile", "d", "Lcom/rs2/model/combat/AmmunitionProfile;"): "JAVELIN",
    ("com/rs2/model/combat/AmmunitionProfile", "e", "Lcom/rs2/model/combat/AmmunitionProfile;"): "DART",
    ("com/rs2/model/combat/AmmunitionProfile", "f", "Lcom/rs2/model/combat/AmmunitionProfile;"): "OGRE",
    ("com/rs2/model/combat/AmmunitionProfile", "g", "Lcom/rs2/model/combat/AmmunitionProfile;"): "BRUTAL",
    ("com/rs2/model/combat/AmmunitionProfile", "h", "Lcom/rs2/model/combat/AmmunitionProfile;"): "THROWNAXE",
    ("com/rs2/model/combat/AmmunitionProfile", "i", "Lcom/rs2/model/combat/AmmunitionProfile;"): "BOLTS",
    ("com/rs2/model/combat/AmmunitionProfile", "j", "Lcom/rs2/model/combat/AmmunitionProfile;"): "KARILS_BOLT",
    ("com/rs2/model/combat/AmmunitionProfile", "k", "Lcom/rs2/model/combat/ProjectileTiming;"): "projectileTiming",
    ("com/rs2/model/combat/AmmunitionProfile", "l", "[Lcom/rs2/model/combat/AmmunitionDefinition;"): "allowedAmmunition",
    ("com/rs2/model/combat/AmmunitionProfile", "m", "I"): "equipmentSlot",
    ("com/rs2/model/combat/AmmunitionProfile", "n", "I"): "graphicDelay",
    ("com/rs2/model/combat/AmmunitionDefinition", "p", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRONZE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "q", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "r", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "s", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "t", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "u", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "a", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "DRAGON_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "b", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "CRYSTAL_BOW_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "v", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ICE_ARROWS",
    ("com/rs2/model/combat/AmmunitionDefinition", "w", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRONZE_BRUTAL",
    ("com/rs2/model/combat/AmmunitionDefinition", "x", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_BRUTAL",
    ("com/rs2/model/combat/AmmunitionDefinition", "y", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_BRUTAL",
    ("com/rs2/model/combat/AmmunitionDefinition", "z", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_BRUTAL",
    ("com/rs2/model/combat/AmmunitionDefinition", "A", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_BRUTAL",
    ("com/rs2/model/combat/AmmunitionDefinition", "B", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNE_BRUTAL",
    ("com/rs2/model/combat/AmmunitionDefinition", "C", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRONZE_FIRE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "D", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_FIRE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "E", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_FIRE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "F", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_FIRE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "G", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_FIRE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "H", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNE_FIRE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "c", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "OGRE_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "I", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BROAD_ARROW",
    ("com/rs2/model/combat/AmmunitionDefinition", "J", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRONZE_KNIFE",
    ("com/rs2/model/combat/AmmunitionDefinition", "K", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_KNIFE",
    ("com/rs2/model/combat/AmmunitionDefinition", "L", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_KNIFE",
    ("com/rs2/model/combat/AmmunitionDefinition", "M", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BLACK_KNIFE",
    ("com/rs2/model/combat/AmmunitionDefinition", "N", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_KNIFE",
    ("com/rs2/model/combat/AmmunitionDefinition", "O", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_KNIFE",
    ("com/rs2/model/combat/AmmunitionDefinition", "P", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNE_KNIFE",
    ("com/rs2/model/combat/AmmunitionDefinition", "Q", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRONZE_THROWNAXE",
    ("com/rs2/model/combat/AmmunitionDefinition", "R", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_THROWNAXE",
    ("com/rs2/model/combat/AmmunitionDefinition", "S", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_THROWNAXE",
    ("com/rs2/model/combat/AmmunitionDefinition", "T", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_THROWNAXE",
    ("com/rs2/model/combat/AmmunitionDefinition", "U", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_THROWNAXE",
    ("com/rs2/model/combat/AmmunitionDefinition", "V", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNE_THROWNAXE",
    ("com/rs2/model/combat/AmmunitionDefinition", "W", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRONZE_DART",
    ("com/rs2/model/combat/AmmunitionDefinition", "X", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_DART",
    ("com/rs2/model/combat/AmmunitionDefinition", "Y", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_DART",
    ("com/rs2/model/combat/AmmunitionDefinition", "Z", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BLACK_DART",
    ("com/rs2/model/combat/AmmunitionDefinition", "aa", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_DART",
    ("com/rs2/model/combat/AmmunitionDefinition", "ab", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_DART",
    ("com/rs2/model/combat/AmmunitionDefinition", "ac", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNE_DART",
    ("com/rs2/model/combat/AmmunitionDefinition", "ad", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRONZE_JAVELIN",
    ("com/rs2/model/combat/AmmunitionDefinition", "ae", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_JAVELIN",
    ("com/rs2/model/combat/AmmunitionDefinition", "af", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_JAVELIN",
    ("com/rs2/model/combat/AmmunitionDefinition", "ag", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_JAVELIN",
    ("com/rs2/model/combat/AmmunitionDefinition", "ah", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_JAVELIN",
    ("com/rs2/model/combat/AmmunitionDefinition", "ai", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNE_JAVELIN",
    ("com/rs2/model/combat/AmmunitionDefinition", "d", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BOLT_RACK",
    ("com/rs2/model/combat/AmmunitionDefinition", "e", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BOLT",
    ("com/rs2/model/combat/AmmunitionDefinition", "f", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "BARBED_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "aj", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "OPAL_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "ak", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "PEARL_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "al", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "IRON_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "am", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "STEEL_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "an", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "MITHRIL_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "ao", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ADAMANT_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "g", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "RUNITE_BOLTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "h", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "DRAGON_BOLTS_E",
    ("com/rs2/model/combat/AmmunitionDefinition", "i", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "TOKTZ",
    ("com/rs2/model/combat/AmmunitionDefinition", "j", "[Lcom/rs2/model/combat/AmmunitionDefinition;"): "ARROWS",
    ("com/rs2/model/combat/AmmunitionDefinition", "k", "[Lcom/rs2/model/combat/AmmunitionDefinition;"): "KNIVES",
    ("com/rs2/model/combat/AmmunitionDefinition", "l", "[Lcom/rs2/model/combat/AmmunitionDefinition;"): "DARTS",
    ("com/rs2/model/combat/AmmunitionDefinition", "m", "[Lcom/rs2/model/combat/AmmunitionDefinition;"): "BRUTAL_ARROWS",
    ("com/rs2/model/combat/AmmunitionDefinition", "n", "[Lcom/rs2/model/combat/AmmunitionDefinition;"): "THROWNAXES",
    ("com/rs2/model/combat/AmmunitionDefinition", "o", "[Lcom/rs2/model/combat/AmmunitionDefinition;"): "JAVELINS",
    ("com/rs2/model/combat/AmmunitionDefinition", "ap", "I"): "projectileId",
    ("com/rs2/model/combat/AmmunitionDefinition", "aq", "I"): "graphicId",
    ("com/rs2/model/combat/AmmunitionDefinition", "ar", "I"): "rangedStrength",
    ("com/rs2/model/combat/AmmunitionDefinition", "as", "I"): "alternateGraphicId",
    ("com/rs2/model/combat/AttackXpMode", "a", "Lcom/rs2/model/combat/AttackXpMode;"): "MELEE_ACCURATE",
    ("com/rs2/model/combat/AttackXpMode", "b", "Lcom/rs2/model/combat/AttackXpMode;"): "AGGRESSIVE",
    ("com/rs2/model/combat/AttackXpMode", "c", "Lcom/rs2/model/combat/AttackXpMode;"): "DEFENSIVE",
    ("com/rs2/model/combat/AttackXpMode", "d", "Lcom/rs2/model/combat/AttackXpMode;"): "CONTROLLED",
    ("com/rs2/model/combat/AttackXpMode", "e", "Lcom/rs2/model/combat/AttackXpMode;"): "RANGED_ACCURATE",
    ("com/rs2/model/combat/AttackXpMode", "f", "Lcom/rs2/model/combat/AttackXpMode;"): "LONGRANGE",
    ("com/rs2/model/combat/AttackXpMode", "g", "Lcom/rs2/model/combat/AttackXpMode;"): "RAPID",
    ("com/rs2/model/combat/AttackXpMode", "h", "Lcom/rs2/model/combat/AttackXpMode;"): "MAGIC",
    ("com/rs2/model/combat/AttackXpMode", "i", "Lcom/rs2/model/combat/AttackXpMode;"): "KQ_RANGED",
    ("com/rs2/model/combat/AttackXpMode", "j", "Lcom/rs2/model/combat/AttackXpMode;"): "KQ_MAGIC",
    ("com/rs2/model/combat/AttackXpMode", "k", "Lcom/rs2/model/combat/AttackXpMode;"): "KBD_SPECIAL",
    ("com/rs2/model/combat/AttackXpMode", "l", "Lcom/rs2/model/combat/AttackXpMode;"): "DRAGONFIRE_FAR",
    ("com/rs2/model/combat/AttackXpMode", "m", "Lcom/rs2/model/combat/AttackXpMode;"): "DRAGONFIRE",
    ("com/rs2/model/combat/AttackXpMode", "n", "Lcom/rs2/model/combat/AttackXpMode;"): "ICY_BREATH",
    ("com/rs2/model/combat/AttackXpMode", "o", "Lcom/rs2/model/combat/AttackXpMode;"): "SARADOMIN_SWORD",
    ("com/rs2/model/combat/AttackXpMode", "p", "Lcom/rs2/model/combat/AttackXpMode;"): "MELEE_FAR",
    ("com/rs2/model/combat/AttackXpMode", "q", "[I"): "skillIds",
    ("com/rs2/model/combat/AttackStyleDefinition", "a", "Lcom/rs2/model/combat/AttackXpMode;"): "xpMode",
    ("com/rs2/model/combat/AttackStyleDefinition", "b", "Lcom/rs2/model/combat/AttackBonusType;"): "attackBonusType",
    ("com/rs2/model/combat/AttackStyleDefinition", "c", "I"): "buttonId",
    ("com/rs2/model/combat/AttackStyleDefinition", "d", "Lcom/rs2/model/combat/CombatType;"): "combatType",
    ("com/rs2/model/combat/ProjectileTiming", "g", "I"): "startDelay",
    ("com/rs2/model/combat/ProjectileTiming", "h", "I"): "speed",
    ("com/rs2/model/combat/ProjectileTiming", "i", "I"): "startHeight",
    ("com/rs2/model/combat/ProjectileTiming", "j", "I"): "endHeight",
    ("com/rs2/model/combat/ProjectileTiming", "k", "I"): "slope",
    ("com/rs2/model/combat/hit/HitType", "a", "Lcom/rs2/model/combat/hit/HitType;"): "NORMAL",
    ("com/rs2/model/combat/hit/HitType", "b", "Lcom/rs2/model/combat/hit/HitType;"): "POISON",
    ("com/rs2/model/combat/hit/HitType", "c", "Lcom/rs2/model/combat/hit/HitType;"): "DISEASE",
    ("com/rs2/model/combat/hit/HitType", "d", "Lcom/rs2/model/combat/hit/HitType;"): "BLOCKED",
    ("com/rs2/model/combat/CombatType", "a", "Lcom/rs2/model/combat/CombatType;"): "MELEE",
    ("com/rs2/model/combat/CombatType", "b", "Lcom/rs2/model/combat/CombatType;"): "RANGED",
    ("com/rs2/model/combat/CombatType", "c", "Lcom/rs2/model/combat/CombatType;"): "MAGIC",
    ("com/rs2/model/LegacyCombatType", "a", "Lcom/rs2/model/LegacyCombatType;"): "MELEE",
    ("com/rs2/model/LegacyCombatType", "b", "Lcom/rs2/model/LegacyCombatType;"): "RANGED",
    ("com/rs2/model/LegacyCombatType", "c", "Lcom/rs2/model/LegacyCombatType;"): "MAGIC",
    ("com/rs2/model/combat/AttackBonusType", "a", "Lcom/rs2/model/combat/AttackBonusType;"): "STAB",
    ("com/rs2/model/combat/AttackBonusType", "b", "Lcom/rs2/model/combat/AttackBonusType;"): "SLASH",
    ("com/rs2/model/combat/AttackBonusType", "c", "Lcom/rs2/model/combat/AttackBonusType;"): "CRUSH",
    ("com/rs2/model/combat/AttackBonusType", "d", "Lcom/rs2/model/combat/AttackBonusType;"): "MAGIC",
    ("com/rs2/model/combat/AttackBonusType", "e", "Lcom/rs2/model/combat/AttackBonusType;"): "RANGED",
    ("com/rs2/model/combat/AttackValidationResult", "a", "Lcom/rs2/model/combat/AttackValidationResult;"): "INVALID_TARGET_LOCATION",
    ("com/rs2/model/combat/AttackValidationResult", "b", "Lcom/rs2/model/combat/AttackValidationResult;"): "WILDERNESS_LEVEL_MISMATCH",
    ("com/rs2/model/combat/AttackValidationResult", "c", "Lcom/rs2/model/combat/AttackValidationResult;"): "TARGET_NOT_IN_WILDERNESS",
    ("com/rs2/model/combat/AttackValidationResult", "d", "Lcom/rs2/model/combat/AttackValidationResult;"): "ALREADY_IN_COMBAT",
    ("com/rs2/model/combat/AttackValidationResult", "e", "Lcom/rs2/model/combat/AttackValidationResult;"): "NOT_DUEL_OPPONENT",
    ("com/rs2/model/combat/AttackValidationResult", "f", "Lcom/rs2/model/combat/AttackValidationResult;"): "INVALID_TARGET",
    ("com/rs2/model/combat/AttackValidationResult", "g", "Lcom/rs2/model/combat/AttackValidationResult;"): "VALID",
    ("com/rs2/model/combat/attack/CombatAttack", "a", "Lcom/rs2/model/Entity;"): "attacker",
    ("com/rs2/model/combat/attack/CombatAttack", "b", "Lcom/rs2/model/Entity;"): "target",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "a", "[Lcom/rs2/model/combat/requirement/CombatRequirement;"): "requirements",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "b", "[Lcom/rs2/model/combat/hit/HitDefinition;"): "hitDefinitions",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "c", "Lcom/rs2/model/animation/GraphicEffect;"): "attackerGraphic",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "d", "I"): "animationId",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "e", "I"): "attackDelay",
    ("com/rs2/model/combat/attack/BaseCombatAttack", "f", "I"): "attackSoundId",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "a", "Lcom/rs2/model/combat/WeaponProfile;"): "weaponProfile",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "b", "I"): "attackStyleIndex",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "c", "Lcom/rs2/model/combat/AttackStyleDefinition;"): "attackStyle",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "d", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ammunition",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "e", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "specialAttack",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "f", "Z"): "cancelled",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "g", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "poisonEffect",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "h", "I"): "droppedAmmunitionId",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "i", "I"): "droppedAmmunitionAmount",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "j", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "MELEE_POISON_EFFECT",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "k", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "RANGED_POISON_EFFECT",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "l", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "MELEE_POISON_PLUS_EFFECT",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "m", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "RANGED_POISON_PLUS_EFFECT",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "n", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "MELEE_POISON_PLUS_PLUS_EFFECT",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "o", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "RANGED_POISON_PLUS_PLUS_EFFECT",
    ("com/rs2/model/combat/attack/WeaponCombatAttack", "p", "Lcom/rs2/model/combat/effect/PoisonEffect;"): "KARAMBWAN_POISON_EFFECT",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "a", "Lcom/rs2/model/combat/AttackXpMode;"): "xpMode",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "b", "Lcom/rs2/model/combat/AttackBonusType;"): "attackBonusType",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "c", "I"): "maxHit",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "d", "I"): "animationId",
    ("com/rs2/model/combat/attack/MeleeCombatAttack", "e", "I"): "attackDelay",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "a", "Lcom/rs2/model/combat/AttackXpMode;"): "xpMode",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "b", "Lcom/rs2/model/combat/AttackBonusType;"): "attackBonusType",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "c", "I"): "maxHit",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "d", "Z"): "deferProtectionPrayerReduction",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "e", "I"): "animationId",
    ("com/rs2/model/combat/attack/ForcedMeleeCombatAttack", "f", "I"): "attackDelay",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "a", "Lcom/rs2/model/combat/AttackXpMode;"): "xpMode",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "b", "Lcom/rs2/model/combat/CombatType;"): "combatType",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "c", "Lcom/rs2/model/combat/ProjectileTiming;"): "projectileTiming",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "d", "I"): "projectileId",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "e", "I"): "maxHit",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "f", "I"): "hitDelay",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "g", "Lcom/rs2/model/animation/GraphicEffect;"): "hitGraphic",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "h", "Lcom/rs2/model/combat/effect/CombatEffect;"): "combatEffect",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "i", "I"): "animationId",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "j", "I"): "attackDelay",
    ("com/rs2/model/combat/attack/ProjectileCombatAttack", "k", "Lcom/rs2/model/animation/GraphicEffect;"): "attackerGraphic",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "a", "Lcom/rs2/model/combat/AttackXpMode;"): "xpMode",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "b", "Lcom/rs2/model/combat/CombatType;"): "combatType",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "c", "Lcom/rs2/model/combat/ProjectileTiming;"): "projectileTiming",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "d", "I"): "projectileId",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "e", "Z"): "alwaysHits",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "f", "I"): "maxHit",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "g", "I"): "hitDelay",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "h", "Lcom/rs2/model/animation/GraphicEffect;"): "hitGraphic",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "i", "Lcom/rs2/model/combat/effect/CombatEffect;"): "combatEffect",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "j", "I"): "animationId",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "k", "I"): "attackDelay",
    ("com/rs2/model/combat/attack/ForcedProjectileCombatAttack", "l", "Lcom/rs2/model/animation/GraphicEffect;"): "attackerGraphic",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "a", "Lcom/rs2/model/combat/AttackXpMode;"): "xpMode",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "b", "Lcom/rs2/model/combat/CombatType;"): "combatType",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "c", "Lcom/rs2/model/combat/ProjectileTiming;"): "projectileTiming",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "d", "I"): "projectileId",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "e", "Z"): "alwaysHits",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "f", "I"): "maxHit",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "g", "Z"): "deferProtectionPrayerReduction",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "h", "I"): "hitDelay",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "i", "Lcom/rs2/model/animation/GraphicEffect;"): "hitGraphic",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "j", "Lcom/rs2/model/combat/effect/CombatEffect;"): "combatEffect",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "k", "I"): "animationId",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "l", "I"): "attackDelay",
    ("com/rs2/model/combat/attack/FlaggedProjectileCombatAttack", "m", "Lcom/rs2/model/animation/GraphicEffect;"): "attackerGraphic",
    ("com/rs2/model/combat/attack/MagicCombatAttack", "a", "Lcom/rs2/model/skill/magic/SpellDefinition;"): "spell",
    ("com/rs2/model/skill/magic/SpellDefinition", "bJ", "I"): "requiredLevel",
    ("com/rs2/model/skill/magic/SpellDefinition", "bK", "I"): "animationId",
    ("com/rs2/model/skill/magic/SpellDefinition", "bL", "I"): "castSoundId",
    ("com/rs2/model/skill/magic/SpellDefinition", "bM", "Lcom/rs2/model/animation/GraphicEffect;"): "castGraphic",
    ("com/rs2/model/skill/magic/SpellDefinition", "bN", "D"): "experience",
    ("com/rs2/model/skill/magic/SpellDefinition", "bO", "[Lcom/rs2/model/item/ItemStack;"): "runeCosts",
    ("com/rs2/model/skill/magic/SpellDefinition", "bP", "[Lcom/rs2/model/item/ItemStack;"): "producedItems",
    ("com/rs2/model/skill/magic/SpellDefinition", "bQ", "Lcom/rs2/model/combat/hit/HitDefinition;"): "hitDefinition",
    ("com/rs2/model/skill/magic/SpellDefinition", "bR", "Z"): "combatSpell",
    ("com/rs2/model/skill/magic/SpellDefinition", "bS", "Z"): "membersOnly",
    ("com/rs2/model/skill/magic/SpellDefinition", "bT", "Lcom/rs2/model/combat/effect/CombatEffect;"): "primaryEffect",
    ("com/rs2/model/skill/magic/SpellDefinition", "bU", "Lcom/rs2/model/combat/effect/CombatEffect;"): "secondaryEffect",
    ("com/rs2/model/skill/magic/SpellDefinition", "bV", "Lcom/rs2/model/combat/effect/CombatEffect;"): "postHitEffect",
    ("com/rs2/model/combat/attack/AreaAttackTargetDistanceComparator", "a", "Lcom/rs2/model/combat/attack/BaseCombatAttack;"): "attack",
    ("com/rs2/model/combat/hit/HitDefinition", "b", "I"): "impactSoundId",
    ("com/rs2/model/combat/hit/HitDefinition", "c", "I"): "maxDamage",
    ("com/rs2/model/combat/hit/HitDefinition", "d", "I"): "minimumDamage",
    ("com/rs2/model/combat/hit/HitDefinition", "e", "I"): "blockAnimationId",
    ("com/rs2/model/combat/hit/HitDefinition", "f", "Lcom/rs2/model/combat/hit/HitType;"): "hitType",
    ("com/rs2/model/combat/hit/HitDefinition", "g", "Lcom/rs2/model/combat/AttackStyleDefinition;"): "attackStyle",
    ("com/rs2/model/combat/hit/HitDefinition", "h", "Lcom/rs2/model/animation/GraphicEffect;"): "graphic",
    ("com/rs2/model/combat/hit/HitDefinition", "i", "Lcom/rs2/model/c/ProjectileDefinition;"): "projectile",
    ("com/rs2/model/c/SheepShearingTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/c/SheepShearingTask", "b", "Lcom/rs2/model/npc/Npc;"): "sheep",
    ("com/rs2/model/combat/hit/HitDefinition", "j", "I"): "delay",
    ("com/rs2/model/combat/hit/HitDefinition", "k", "I"): "specialEffectId",
    ("com/rs2/model/combat/hit/HitDefinition", "l", "Z"): "accuracyCheckEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "m", "Z"): "randomDamageEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "n", "Z"): "alwaysHits",
    ("com/rs2/model/combat/hit/HitDefinition", "p", "Z"): "multiTargetSpreadEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "q", "Z"): "blockAnimationEnabled",
    ("com/rs2/model/combat/hit/HitDefinition", "r", "Z"): "protectionPrayerReductionDeferred",
    ("com/rs2/model/combat/hit/HitDefinition", "s", "Lcom/rs2/model/skill/magic/SpellDefinition;"): "spell",
    ("com/rs2/model/combat/hit/HitDefinition", "t", "Ljava/util/ArrayList;"): "chainedTargets",
    ("com/rs2/model/combat/hit/HitDefinition", "u", "Lcom/rs2/model/Entity;"): "chainedSource",
    ("com/rs2/model/combat/hit/HitDefinition", "v", "Lcom/rs2/model/combat/AmmunitionDefinition;"): "ammunition",
    ("com/rs2/model/combat/hit/HitDefinition", "w", "Ljava/util/List;"): "effects",
    ("com/rs2/model/combat/hit/HitDefinition", "x", "Lcom/rs2/model/item/ItemStack;"): "droppedAmmunition",
    ("com/rs2/model/combat/hit/HitDefinition", "z", "D"): "accuracyMultiplier",
    ("com/rs2/model/combat/hit/DamageContribution", "a", "I"): "damage",
    ("com/rs2/model/combat/hit/DamageContribution", "b", "Lcom/rs2/model/task/DelayTimer;"): "expiryTimer",
    ("com/rs2/model/Entity", "a", "I"): "index",
    ("com/rs2/model/Entity", "b", "I"): "encodedIndex",
    ("com/rs2/model/Entity", "c", "Lcom/rs2/model/Entity;"): "interactionTarget",
    ("com/rs2/model/Entity", "d", "Lcom/rs2/model/Entity;"): "tradePartner",
    ("com/rs2/model/Entity", "e", "Lcom/rs2/model/Entity;"): "combatTarget",
    ("com/rs2/model/Entity", "f", "Lcom/rs2/model/Entity;"): "movementTarget",
    ("com/rs2/model/Entity", "g", "Lcom/rs2/model/Position;"): "deathPosition",
    ("com/rs2/model/Entity", "h", "I"): "referenceId",
    ("com/rs2/model/Entity", "i", "Z"): "dead",
    ("com/rs2/model/Entity", "n", "I"): "attackRange",
    ("com/rs2/model/Entity", "o", "I"): "actionSequence",
    ("com/rs2/model/Entity", "p", "I"): "interruptibleActionCheckpoint",
    ("com/rs2/model/Entity", "q", "I"): "interruptibleActionCounter",
    ("com/rs2/model/Entity", "m", "I"): "combatTransformNpcId",
    ("com/rs2/model/Entity", "j", "Z"): "scriptedMovementEnabled",
    ("com/rs2/model/Entity", "k", "Z"): "targetMovementDisabled",
    ("com/rs2/model/Entity", "l", "Z"): "autoRetaliateDisabled",
    ("com/rs2/model/Entity", "r", "D"): "poisonDamage",
    ("com/rs2/model/Entity", "s", "Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "recentCombatTimer",
    ("com/rs2/model/Entity", "t", "Lcom/rs2/model/combat/CombatTargetDelayTimer;"): "singleCombatTimer",
    ("com/rs2/model/Entity", "v", "Lcom/rs2/model/combat/attack/CombatAttackProvider;"): "combatAttackProvider",
    ("com/rs2/model/Entity", "w", "Lcom/rs2/model/task/CycleEvent;"): "activeCycleEvent",
    ("com/rs2/model/Entity", "x", "Lcom/rs2/model/EntityTargetMovement;"): "targetMovement",
    ("com/rs2/model/Entity", "y", "Lcom/rs2/model/task/DelayTimer;"): "movementDelayTimer",
    ("com/rs2/model/EntityTargetMovement", "a", "Lcom/rs2/model/Entity;"): "entity",
    ("com/rs2/model/Entity", "F", "Ljava/util/List;"): "combatEffectTasks",
    ("com/rs2/model/Entity", "I", "Lcom/rs2/model/MovementQueue;"): "movementQueue",
    ("com/rs2/model/Entity", "J", "Ljava/util/Queue;"): "damageContributions",
    ("com/rs2/model/Entity", "K", "I"): "size",
    ("com/rs2/model/Entity", "L", "I"): "walkDirection",
    ("com/rs2/model/Entity", "M", "I"): "runDirection",
    ("com/rs2/model/Entity", "N", "Ljava/util/Map;"): "attributes",
    ("com/rs2/model/Entity", "O", "Lcom/rs2/model/Position;"): "position",
    ("com/rs2/model/Entity", "P", "Lcom/rs2/model/EntityUpdateState;"): "updateState",
    ("com/rs2/model/Entity", "u", "Lcom/rs2/model/task/DelayTimer;"): "attackDelayTimer",
    ("com/rs2/model/Entity", "z", "Lcom/rs2/model/task/DelayTimer;"): "teleblockTimer",
    ("com/rs2/model/Entity", "A", "Lcom/rs2/model/task/DelayTimer;"): "movementLockTimer",
    ("com/rs2/model/Entity", "B", "Lcom/rs2/model/task/DelayTimer;"): "stunTimer",
    ("com/rs2/model/Entity", "C", "Lcom/rs2/model/task/DelayTimer;"): "poisonImmunityTimer",
    ("com/rs2/model/Entity", "D", "Lcom/rs2/model/task/DelayTimer;"): "movementLockImmunityTimer",
    ("com/rs2/model/Entity", "E", "Lcom/rs2/model/task/DelayTimer;"): "antifireTimer",
    ("com/rs2/model/Entity", "G", "Lcom/rs2/model/task/DelayTimer;"): "chargeCooldownTimer",
    ("com/rs2/model/Entity", "H", "Lcom/rs2/model/task/DelayTimer;"): "chargeSpellTimer",
    ("com/rs2/model/EntityUpdateState", "a", "Z"): "updateRequired",
    ("com/rs2/model/EntityUpdateState", "b", "Z"): "appearanceUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "c", "Z"): "forcedTextUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "d", "Ljava/lang/String;"): "forcedText",
    ("com/rs2/model/EntityUpdateState", "e", "Z"): "graphicUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "f", "I"): "graphicId",
    ("com/rs2/model/EntityUpdateState", "g", "I"): "graphicDelay",
    ("com/rs2/model/EntityUpdateState", "h", "Z"): "animationUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "i", "I"): "animationId",
    ("com/rs2/model/EntityUpdateState", "j", "I"): "animationDelay",
    ("com/rs2/model/EntityUpdateState", "k", "Z"): "faceEntityUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "l", "I"): "faceEntityId",
    ("com/rs2/model/EntityUpdateState", "m", "Z"): "facePositionUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "n", "Lcom/rs2/model/Position;"): "facePosition",
    ("com/rs2/model/EntityUpdateState", "o", "Z"): "primaryHitUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "p", "Z"): "secondaryHitUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "q", "I"): "queuedPrimaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "r", "I"): "queuedPrimaryHitType",
    ("com/rs2/model/EntityUpdateState", "s", "I"): "queuedSecondaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "t", "I"): "queuedSecondaryHitType",
    ("com/rs2/model/EntityUpdateState", "u", "Z"): "forcedMovementUpdateRequired",
    ("com/rs2/model/EntityUpdateState", "v", "I"): "primaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "w", "I"): "secondaryHitDamage",
    ("com/rs2/model/EntityUpdateState", "x", "I"): "primaryHitType",
    ("com/rs2/model/EntityUpdateState", "y", "I"): "secondaryHitType",
    ("com/rs2/model/EntityUpdateState", "z", "I"): "forcedMovementStartDelay",
    ("com/rs2/model/EntityUpdateState", "A", "I"): "forcedMovementEndDelay",
    ("com/rs2/model/EntityUpdateState", "B", "I"): "forcedMovementDirection",
    ("com/rs2/model/EntityUpdateState", "C", "Z"): "primaryHitDamageOverridden",
    ("com/rs2/model/EntityUpdateState", "D", "Z"): "secondaryHitDamageOverridden",
    ("com/rs2/model/EntityUpdateState", "E", "Lcom/rs2/model/Entity;"): "entity",
    ("com/rs2/model/EntityUpdateState", "F", "I"): "forcedMovementEndXOffset",
    ("com/rs2/model/EntityUpdateState", "G", "I"): "forcedMovementEndYOffset",
    ("com/rs2/model/MovementQueue", "a", "Lcom/rs2/model/Entity;"): "entity",
    ("com/rs2/model/MovementQueue", "b", "Ljava/util/Deque;"): "steps",
    ("com/rs2/model/MovementQueue", "c", "Ljava/util/Deque;"): "stepHistory",
    ("com/rs2/model/MovementQueue", "d", "Z"): "running",
    ("com/rs2/model/MovementQueue", "e", "Z"): "runPath",
    ("com/rs2/model/MovementQueue", "f", "[B"): "directionDeltaX",
    ("com/rs2/model/MovementQueue", "g", "[B"): "directionDeltaY",
    ("com/rs2/model/MovementQueue", "h", "[[I"): "collisionBypassTiles",
    ("com/rs2/model/MovementStep", "a", "I"): "direction",
    ("com/rs2/model/item/ItemDefinition", "a", "Ljava/util/ArrayList;"): "grandExchangePriceSamples",
    ("com/rs2/model/item/ItemDefinition", "c", "[Lcom/rs2/model/item/ItemDefinition;"): "definitionsById",
    ("com/rs2/model/item/ItemDefinition", "d", "I"): "definitionCount",
    ("com/rs2/model/item/ItemDefinition", "i", "Z"): "destroyOption",
    ("com/rs2/model/item/ItemDefinition", "j", "I"): "id",
    ("com/rs2/model/item/ItemDefinition", "k", "Ljava/lang/String;"): "name",
    ("com/rs2/model/item/ItemDefinition", "l", "Ljava/lang/String;"): "description",
    ("com/rs2/model/item/ItemDefinition", "m", "Z"): "note",
    ("com/rs2/model/item/ItemDefinition", "n", "Z"): "hasNote",
    ("com/rs2/model/item/ItemDefinition", "o", "Z"): "stackable",
    ("com/rs2/model/item/ItemDefinition", "p", "I"): "unnotedId",
    ("com/rs2/model/item/ItemDefinition", "q", "I"): "notedId",
    ("com/rs2/model/item/ItemDefinition", "r", "Z"): "membersOnly",
    ("com/rs2/model/item/ItemDefinition", "s", "I"): "tokkulValue",
    ("com/rs2/model/item/ItemDefinition", "u", "I"): "highAlchemyValue",
    ("com/rs2/model/item/ItemDefinition", "v", "I"): "lowAlchemyValue",
    ("com/rs2/model/item/ItemDefinition", "w", "I"): "donatorPointValue",
    ("com/rs2/model/item/ItemDefinition", "x", "[I"): "bonuses",
    ("com/rs2/model/item/ItemDefinition", "y", "[I"): "requiredLevels",
    ("com/rs2/model/item/ItemDefinition", "z", "[Z"): "requiredQuests",
    ("com/rs2/model/item/ItemDefinition", "A", "I"): "equipmentSlot",
    ("com/rs2/model/item/ItemDefinition", "G", "Z"): "untradeable",
    ("com/rs2/model/item/ItemDefinition", "B", "I"): "equipmentAppearanceType",
    ("com/rs2/model/item/ItemDefinition", "C", "I"): "requiredQuestPoints",
    ("com/rs2/model/item/ItemDefinition", "D", "D"): "weight",
    ("com/rs2/model/item/ItemDefinition", "E", "Z"): "twoHanded",
    ("com/rs2/model/item/ItemDefinition", "F", "I"): "shopValue",
    ("com/rs2/model/item/ItemService", "a", "Lcom/rs2/model/path/PathReachability;"): "pathReachability",
    ("com/rs2/model/item/ItemService", "b", "Lcom/rs2/model/item/ItemService;"): "instance",
    ("com/rs2/model/item/PickupItemTask", "a", "Lcom/rs2/model/item/ItemService;"): "itemService",
    ("com/rs2/model/item/PickupItemTask", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/item/PickupItemTask", "c", "I"): "actionSequence",
    ("com/rs2/model/item/PickupItemTask", "d", "I"): "itemId",
    ("com/rs2/model/item/PickupItemTask", "e", "Lcom/rs2/model/Position;"): "groundItemPosition",
    ("com/rs2/model/item/LootNextItemTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/player/Player", "gt", "I"): "currentShopId",
    ("com/rs2/model/shop/ShopCurrency", "a", "Lcom/rs2/model/shop/ShopCurrency;"): "ITEM_CURRENCY",
    ("com/rs2/model/shop/ShopCurrency", "b", "Lcom/rs2/model/shop/ShopCurrency;"): "DONATOR_POINTS",
    ("com/rs2/model/shop/ShopDefinition", "a", "I"): "shopId",
    ("com/rs2/model/shop/ShopDefinition", "b", "Ljava/lang/String;"): "name",
    ("com/rs2/model/shop/ShopDefinition", "c", "Z"): "generalStore",
    ("com/rs2/model/shop/ShopDefinition", "d", "Z"): "membersOnly",
    ("com/rs2/model/shop/ShopDefinition", "e", "Lcom/rs2/model/shop/ShopCurrency;"): "currency",
    ("com/rs2/model/shop/ShopDefinition", "f", "I"): "currencyItemId",
    ("com/rs2/model/shop/ShopDefinition", "g", "Lcom/rs2/model/item/ItemContainer;"): "originalStock",
    ("com/rs2/model/shop/ShopDefinition", "h", "Lcom/rs2/model/item/ItemContainer;"): "stock",
    ("com/rs2/model/shop/ShopDefinition", "i", "I"): "buyPricePercent",
    ("com/rs2/model/shop/ShopDefinition", "j", "I"): "sellPricePercent",
    ("com/rs2/model/shop/ShopDefinition", "k", "I"): "priceChangeRateTenths",
    ("com/rs2/model/shop/ShopDefinition", "l", "[I"): "restockDelayTicks",
    ("com/rs2/model/shop/ShopDefinition", "m", "[Lcom/rs2/model/task/TickTask;"): "restockTasks",
    ("com/rs2/model/shop/ShopManager", "a", "Ljava/util/List;"): "shopDefinitions",
    ("com/rs2/model/shop/ShopManager", "b", "[I"): "shopCurrencySwitchMap",
    ("com/rs2/model/shop/ShopRestockTask", "a", "Lcom/rs2/model/shop/ShopDefinition;"): "shopDefinition",
    ("com/rs2/model/shop/ShopRestockTask", "b", "I"): "itemId",
    ("com/rs2/model/shop/ShopRestockTask", "c", "I"): "slotIndex",
    ("com/rs2/model/player/GrandExchangeManager", "a", "I"): "instantPriceFluctuationPercent",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "a", "J"): "timestampMillis",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "b", "I"): "itemId",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "c", "I"): "quantity",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "d", "I"): "unitPrice",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "e", "Ljava/util/ArrayList;"): "allSamples",
    ("com/rs2/model/grandexchange/GrandExchangePriceSample", "f", "Ljava/util/ArrayList;"): "sampledItemIds",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "d", "Ljava/lang/String;"): "ownerUsername",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "e", "Z"): "sellOffer",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "a", "I"): "itemId",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "f", "I"): "slotIndex",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "b", "I"): "quantity",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "g", "I"): "remainingQuantity",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "c", "I"): "unitPrice",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "h", "J"): "serverOfferCreatedAtMillis",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "i", "I"): "serverOfferLifetimeMinutes",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "j", "Ljava/util/ArrayList;"): "sellOffers",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "k", "Ljava/util/ArrayList;"): "buyOffers",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "l", "Ljava/util/ArrayList;"): "serverOffers",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "m", "Ljava/util/ArrayList;"): "pendingBuyOfferRemovals",
    ("com/rs2/model/grandexchange/GrandExchangeOffer", "n", "Ljava/util/ArrayList;"): "pendingSellOfferRemovals",
    ("com/rs2/model/message/MessageOfTheWeek", "a", "I"): "antivirusMessageInterfaceId",
    ("com/rs2/model/message/MessageOfTheWeek", "b", "I"): "holidayMessageInterfaceId",
    ("com/rs2/model/message/MessageOfTheWeek", "c", "I"): "passwordMessageInterfaceId",
    ("com/rs2/model/message/MessageOfTheWeek", "d", "I"): "christmasMessageInterfaceId",
    ("com/rs2/model/message/MessageOfTheWeek", "e", "I"): "interfaceId",
    ("com/rs2/model/message/MessageOfTheWeek", "f", "[Ljava/lang/String;"): "lines",
    ("com/rs2/model/message/MessageOfTheWeek", "g", "Ljava/lang/String;"): "title",
    ("com/rs2/model/message/MessageOfTheWeek", "h", "[Lcom/rs2/model/message/MessageOfTheWeek;"): "normalMessages",
    ("com/rs2/model/message/MessageOfTheWeek", "i", "Lcom/rs2/model/message/MessageOfTheWeek;"): "halloweenMessage",
    ("com/rs2/model/message/MessageOfTheWeek", "j", "Lcom/rs2/model/message/MessageOfTheWeek;"): "christmasMessage",
    ("com/rs2/model/message/MessageOfTheWeek", "k", "Lcom/rs2/model/message/MessageOfTheWeek;"): "easterMessage",
    ("com/rs2/model/npc/drop/NpcDropEntry", "a", "I"): "itemId",
    ("com/rs2/model/npc/drop/NpcDropEntry", "b", "I"): "fixedAmount",
    ("com/rs2/model/npc/drop/NpcDropEntry", "c", "I"): "minAmount",
    ("com/rs2/model/npc/drop/NpcDropEntry", "d", "I"): "maxAmount",
    ("com/rs2/model/npc/drop/NpcDropEntry", "e", "[I"): "itemIds",
    ("com/rs2/model/npc/drop/NpcDropEntry", "f", "[I"): "amountOptions",
    ("com/rs2/model/npc/drop/NpcDropEntry", "g", "I"): "chanceType",
    ("com/rs2/model/npc/drop/NpcDropEntry", "h", "I"): "chanceNumerator",
    ("com/rs2/model/npc/drop/NpcDropEntry", "i", "I"): "chanceDenominator",
    ("com/rs2/model/npc/drop/NpcDropEntry", "j", "[I"): "minAmounts",
    ("com/rs2/model/npc/drop/NpcDropEntry", "k", "[I"): "maxAmounts",
    ("com/rs2/model/npc/drop/NpcDropTable", "a", "[Lcom/rs2/model/npc/drop/NpcDropTable;"): "tablesByNpcId",
    ("com/rs2/model/npc/drop/NpcDropTable", "b", "[Lcom/rs2/model/npc/drop/NpcDropEntry;"): "guaranteedDrops",
    ("com/rs2/model/npc/drop/NpcDropTable", "c", "[Lcom/rs2/model/npc/drop/NpcDropEntry;"): "weightedDrops",
    ("com/rs2/model/npc/drop/NpcDropTable", "d", "[Lcom/rs2/model/npc/drop/NpcDropEntry;"): "independentDrops",
    ("com/rs2/model/npc/drop/NpcDropTable", "e", "[I"): "membersOnlyVirtualDropIds",
    ("com/rs2/model/npc/drop/NpcDropManager", "a", "Ljava/util/Random;"): "random",
    ("com/rs2/model/npc/drop/NpcDropManager", "b", "[I"): "HERB_DROP_ITEM_IDS",
    ("com/rs2/model/npc/drop/NpcDropManager", "c", "[I"): "SEED_DROP_ITEM_IDS",
    ("com/rs2/model/npc/drop/NpcDropManager", "d", "[I"): "GEM_DROP_ITEM_IDS",
    ("com/rs2/model/npc/drop/NpcDropManager", "e", "[I"): "HERB_DROP_WEIGHTS",
    ("com/rs2/model/npc/drop/NpcDropManager", "f", "[I"): "SEED_DROP_WEIGHTS",
    ("com/rs2/model/npc/drop/NpcDropManager", "g", "[I"): "GEM_DROP_WEIGHTS",
    ("com/rs2/model/npc/drop/NpcDropManager", "h", "Ljava/util/ArrayList;"): "weightedVirtualDropChoices",
    ("com/rs2/model/combat/CombatTargetDelayTimer", "a", "Lcom/rs2/model/Entity;"): "target",
    ("com/rs2/model/combat/hit/HitType", "e", "I"): "clientId",
    ("com/rs2/model/combat/CombatAction", "a", "Lcom/rs2/model/combat/hit/HitDefinition;"): "hitDefinition",
    ("com/rs2/model/combat/CombatAction", "b", "I"): "delay",
    ("com/rs2/model/combat/CombatAction", "c", "I"): "damage",
    ("com/rs2/model/combat/CombatAction", "d", "Lcom/rs2/model/Entity;"): "attacker",
    ("com/rs2/model/combat/CombatAction", "e", "Lcom/rs2/model/Entity;"): "target",
    ("com/rs2/model/combat/CombatAction", "f", "Z"): "hitSuccessful",
    ("com/rs2/model/combat/CombatAction", "g", "Lcom/rs2/model/Entity;"): "projectileSource",
    ("com/rs2/model/combat/CombatCycleEvent", "a", "Lcom/rs2/model/Entity;"): "attacker",
    ("com/rs2/model/combat/CombatCycleEvent", "b", "Lcom/rs2/model/Entity;"): "target",
    ("com/rs2/model/combat/CombatCycleEvent", "c", "I"): "actionSequence",
    ("com/rs2/model/combat/NpcRespawnCombatEvent", "a", "Lcom/rs2/model/npc/Npc;"): "respawningNpc",
    ("com/rs2/model/combat/NpcRespawnCombatEvent", "b", "Lcom/rs2/model/Entity;"): "killer",
    ("com/rs2/model/combat/DeathAnimationTask", "a", "I"): "deathAnimationId",
    ("com/rs2/model/combat/DeathAnimationTask", "b", "Lcom/rs2/model/Entity;"): "defeatedEntity",
    ("com/rs2/model/combat/DeathAnimationTask", "c", "Lcom/rs2/model/Entity;"): "killer",
    ("com/rs2/model/combat/DeathCleanupTask", "a", "Lcom/rs2/model/Entity;"): "defeatedEntity",
    ("com/rs2/model/combat/DeathCleanupTask", "b", "Lcom/rs2/model/Entity;"): "killer",
    ("com/rs2/model/combat/CombatManager", "a", "Lcom/rs2/model/npc/Npc;"): "kalphiteQueenFirstForm",
    ("com/rs2/model/combat/CombatManager", "b", "Lcom/rs2/model/combat/CombatManager;"): "instance",
    ("com/rs2/model/combat/CombatManager", "c", "Ljava/util/List;"): "pendingActions",
    ("com/rs2/model/combat/CombatManager", "d", "Ljava/util/Random;"): "random",
    ("com/rs2/model/combat/PvpCombatReference", "a", "Lcom/rs2/model/task/DelayTimer;"): "timer",
    ("com/rs2/model/combat/KalphiteQueenRespawnCombatEvent", "a", "Lcom/rs2/model/Entity;"): "killer",
    ("com/rs2/model/combat/effect/CombatEffect", "a", "Z"): "applied",
    ("com/rs2/model/combat/effect/CombatEffect", "b", "Lcom/rs2/model/combat/effect/CombatEffectTask;"): "activeTask",
    ("com/rs2/model/combat/effect/CombatEffectTask", "a", "Lcom/rs2/model/EntityReference;"): "sourceReference",
    ("com/rs2/model/combat/effect/CombatEffectTask", "b", "Lcom/rs2/model/EntityReference;"): "targetReference",
    ("com/rs2/model/combat/effect/CombatEffectTask", "c", "Lcom/rs2/model/combat/effect/CombatEffect;"): "effect",
    ("com/rs2/model/combat/effect/MovementLockEffect", "a", "I"): "durationTicks",
    ("com/rs2/model/combat/effect/BloodSpellHealEffect", "a", "D"): "healFraction",
    ("com/rs2/model/combat/effect/PoisonEffect", "a", "D"): "initialDamage",
    ("com/rs2/model/combat/effect/PoisonEffect", "b", "Z"): "notifyTarget",
    ("com/rs2/model/combat/effect/PoisonDamageTask", "a", "D"): "currentDamage",
    ("com/rs2/model/combat/effect/StatDrainEffect", "a", "I"): "skillId",
    ("com/rs2/model/combat/effect/StatDrainEffect", "b", "I"): "fixedDrainAmount",
    ("com/rs2/model/combat/effect/StatDrainEffect", "c", "D"): "drainFraction",
    ("com/rs2/model/combat/effect/WallBeastStunEffect", "a", "I"): "graphicId",
    ("com/rs2/model/combat/effect/SummonNpcCombatEffect", "a", "I"): "npcId",
    ("com/rs2/model/combat/effect/TeleblockEffect", "a", "I"): "durationTicks",
    ("com/rs2/model/combat/requirement/EquipmentItemRequirement", "a", "I"): "equipmentSlot",
    ("com/rs2/model/combat/requirement/EquipmentItemRequirement", "b", "I"): "itemId",
    ("com/rs2/model/combat/requirement/EquipmentItemRequirement", "c", "I"): "amount",
    ("com/rs2/model/combat/requirement/EquipmentItemRequirement", "d", "Z"): "consumeOnUse",
    ("com/rs2/model/combat/requirement/InventoryItemRequirement", "a", "I"): "itemId",
    ("com/rs2/model/combat/requirement/InventoryItemRequirement", "b", "I"): "amount",
    ("com/rs2/model/combat/requirement/InventoryItemRequirement", "c", "Ljava/util/ArrayList;"): "requiredItems",
    ("com/rs2/model/combat/requirement/SkillLevelRequirement", "a", "I"): "skillId",
    ("com/rs2/model/combat/requirement/SkillLevelRequirement", "b", "I"): "requiredLevel",
    ("com/rs2/model/combat/requirement/SpellRuneCostRequirement", "a", "Lcom/rs2/model/skill/magic/SpellDefinition;"): "spell",
    ("com/rs2/model/combat/requirement/SpellRuneCostRequirement", "b", "Ljava/util/ArrayList;"): "runeCosts",
    ("com/rs2/model/combat/requirement/SpellRuneCostRequirement", "c", "Ljava/util/ArrayList;"): "combinationRuneCosts",
    ("com/rs2/model/combat/requirement/SpellRuneCostRequirement", "d", "Ljava/util/ArrayList;"): "combinationRuneCredits",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "a", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_DAGGER",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "b", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "ABYSSAL_WHIP",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "c", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "RUNE_THROWNAXE",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "d", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_LONGSWORD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "e", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_SCIMITAR",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "f", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "RUNE_CLAWS",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "g", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_MACE",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "h", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_AXE",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "i", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DARKLIGHT",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "j", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_SPEAR",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "k", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_HALBERD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "l", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_2H_SWORD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "m", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "SEERCULL",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "n", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DARK_BOW",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "o", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "MAGIC_SHORTBOW",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "p", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "MAGIC_LONGBOW",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "q", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "DRAGON_BATTLEAXE",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "r", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "EXCALIBUR",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "s", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "BANDOS_GODSWORD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "t", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "ARMADYL_GODSWORD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "u", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "ZAMORAK_GODSWORD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "v", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "SARADOMIN_GODSWORD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "w", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "SARADOMIN_SWORD",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "x", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "GRANITE_MAUL",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "y", "B"): "energyCost",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "z", "[Ljava/lang/String;"): "weaponNamePatterns",
    ("com/rs2/model/combat/special/SpecialAttackDefinition", "A", "[Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "VALUES",
    ("com/rs2/model/combat/special/DarkBowSpecialAttack", "a", "Lcom/rs2/model/combat/WeaponProfile;"): "sourceWeaponProfile",
    ("com/rs2/model/combat/special/DarkBowSpecialAttack", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/combat/special/MagicLongbowSpecialAttack", "a", "Lcom/rs2/model/combat/WeaponProfile;"): "sourceWeaponProfile",
    ("com/rs2/model/combat/special/MagicLongbowSpecialAttack", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/combat/special/MagicShortbowSpecialAttack", "a", "Lcom/rs2/model/combat/WeaponProfile;"): "sourceWeaponProfile",
    ("com/rs2/model/combat/special/MagicShortbowSpecialAttack", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/combat/special/SeercullSpecialAttack", "a", "Lcom/rs2/model/combat/WeaponProfile;"): "sourceWeaponProfile",
    ("com/rs2/model/combat/special/SeercullSpecialAttack", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/combat/special/RuneThrownaxeSpecialAttack", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/combat/special/RuneThrownaxeSpecialAttack", "b", "Lcom/rs2/model/Entity;"): "primaryTarget",
    ("com/rs2/model/combat/special/RuneThrownaxeSpecialAttack", "c", "Lcom/rs2/model/combat/WeaponProfile;"): "sourceWeaponProfile",
    ("com/rs2/model/combat/special/RuneThrownaxeTargetDistanceComparator", "a", "Lcom/rs2/model/combat/special/RuneThrownaxeSpecialAttack;"): "attack",
    ("com/rs2/model/combat/special/DragonHalberdSpecialAttack", "a", "Lcom/rs2/model/Entity;"): "primaryTarget",
    ("com/rs2/model/combat/special/MagicShortbowDelayedGraphicTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/npc/Npc", "S", "Lcom/rs2/model/npc/combat/NpcCombatDefinition;"): "combatDefinition",
    ("com/rs2/model/npc/Npc", "t", "Lcom/rs2/model/npc/NpcDefinition;"): "definition",
    ("com/rs2/model/npc/Npc", "T", "I"): "nextReferenceId",
    ("com/rs2/model/npc/Npc", "r", "I"): "npcId",
    ("com/rs2/model/npc/Npc", "s", "I"): "originalNpcId",
    ("com/rs2/model/npc/Npc", "u", "Lcom/rs2/model/Position;"): "spawnMinPosition",
    ("com/rs2/model/npc/Npc", "v", "Lcom/rs2/model/Position;"): "spawnMaxPosition",
    ("com/rs2/model/npc/Npc", "w", "Lcom/rs2/model/Position;"): "spawnPosition",
    ("com/rs2/model/npc/Npc", "x", "Lcom/rs2/model/npc/NpcMovementMode;"): "movementMode",
    ("com/rs2/model/npc/Npc", "y", "I"): "spawnX",
    ("com/rs2/model/npc/Npc", "z", "I"): "spawnY",
    ("com/rs2/model/npc/Npc", "A", "I"): "currentHitpoints",
    ("com/rs2/model/npc/Npc", "B", "I"): "currentAttackLevel",
    ("com/rs2/model/npc/Npc", "C", "I"): "currentStrengthLevel",
    ("com/rs2/model/npc/Npc", "D", "I"): "currentDefenceLevel",
    ("com/rs2/model/npc/Npc", "E", "I"): "currentMagicLevel",
    ("com/rs2/model/npc/Npc", "F", "I"): "currentRangedLevel",
    ("com/rs2/model/npc/Npc", "G", "[Lcom/rs2/model/task/TickTask;"): "statRestoreTasks",
    ("com/rs2/model/npc/Npc", "H", "I"): "transformedNpcId",
    ("com/rs2/model/npc/Npc", "I", "I"): "transformTicksRemaining",
    ("com/rs2/model/npc/Npc", "J", "I"): "facingDirection",
    ("com/rs2/model/npc/Npc", "K", "Z"): "active",
    ("com/rs2/model/npc/Npc", "L", "Z"): "transformed",
    ("com/rs2/model/npc/Npc", "M", "Z"): "respawnEnabled",
    ("com/rs2/model/npc/Npc", "N", "Z"): "interactable",
    ("com/rs2/model/npc/Npc", "O", "I"): "ownerPlayerIndex",
    ("com/rs2/model/npc/Npc", "P", "Z"): "faceEntityUpdateDisabled",
    ("com/rs2/model/npc/Npc", "Q", "Lcom/rs2/model/Entity;"): "forcedCombatTarget",
    ("com/rs2/model/npc/Npc", "R", "I"): "removalDelayTicks",
    ("com/rs2/model/npc/Npc", "g", "[I"): "combatTransformNpcIds",
    ("com/rs2/model/npc/Npc", "h", "[I"): "scriptedMovementNpcIds",
    ("com/rs2/model/npc/Npc", "i", "[I"): "targetMovementDisabledNpcIds",
    ("com/rs2/model/npc/Npc", "j", "[I"): "autoRetaliateDisabledNpcIds",
    ("com/rs2/model/npc/Npc", "k", "[I"): "faceEntityUpdateDisabledNpcIds",
    ("com/rs2/model/npc/Npc", "l", "I"): "scriptedStageCursor",
    ("com/rs2/model/npc/Npc", "m", "Ljava/util/List;"): "scriptedStageNpcs",
    ("com/rs2/model/npc/Npc", "U", "I"): "scriptedPathTargetX",
    ("com/rs2/model/npc/Npc", "V", "I"): "scriptedPathTargetY",
    ("com/rs2/model/npc/Npc", "W", "[Lcom/rs2/model/Position;"): "waypointLoop",
    ("com/rs2/model/npc/Npc", "X", "I"): "waypointLoopIndex",
    ("com/rs2/model/npc/Npc", "n", "I"): "scriptedSequenceLoopCount",
    ("com/rs2/model/npc/Npc", "o", "I"): "scriptedPathStage",
    ("com/rs2/model/npc/Npc", "Y", "Z"): "skipNextPathAdvance",
    ("com/rs2/model/npc/Npc", "p", "I"): "lastStepDeltaX",
    ("com/rs2/model/npc/Npc", "q", "I"): "lastStepDeltaY",
    ("com/rs2/model/npc/NpcStageAdvanceTask", "a", "Lcom/rs2/model/npc/Npc;"): "npc",
    ("com/rs2/model/npc/NpcSequenceAdvanceTask", "a", "Lcom/rs2/model/npc/Npc;"): "npc",
    ("com/rs2/model/npc/NpcMovementMode", "a", "Lcom/rs2/model/npc/NpcMovementMode;"): "STATIONARY",
    ("com/rs2/model/npc/NpcMovementMode", "b", "Lcom/rs2/model/npc/NpcMovementMode;"): "ROAMING",
    ("com/rs2/model/npc/NpcDefinition", "a", "I"): "respawnDelaySeconds",
    ("com/rs2/model/npc/NpcDefinition", "b", "I"): "customDefinitionCount",
    ("com/rs2/model/npc/NpcDefinition", "c", "I"): "aggressionRange",
    ("com/rs2/model/npc/NpcDefinition", "d", "I"): "spawnRadius",
    ("com/rs2/model/npc/NpcDefinition", "e", "I"): "chaseRadius",
    ("com/rs2/model/npc/NpcDefinition", "f", "I"): "attackBonusTypeId",
    ("com/rs2/model/npc/NpcDefinition", "g", "I"): "respawnDelayTicks",
    ("com/rs2/model/npc/NpcDefinition", "h", "I"): "attackLevel",
    ("com/rs2/model/npc/NpcDefinition", "i", "I"): "strengthLevel",
    ("com/rs2/model/npc/NpcDefinition", "j", "I"): "defenceLevel",
    ("com/rs2/model/npc/NpcDefinition", "k", "I"): "magicLevel",
    ("com/rs2/model/npc/NpcDefinition", "l", "I"): "rangedLevel",
    ("com/rs2/model/npc/NpcDefinition", "m", "I"): "meleeAttackBonus",
    ("com/rs2/model/npc/NpcDefinition", "n", "I"): "meleeStrengthBonus",
    ("com/rs2/model/npc/NpcDefinition", "o", "I"): "magicAttackBonus",
    ("com/rs2/model/npc/NpcDefinition", "q", "I"): "rangedAttackBonus",
    ("com/rs2/model/npc/NpcDefinition", "u", "[I"): "defenceBonuses",
    ("com/rs2/model/npc/NpcDefinition", "v", "I"): "poisonDamage",
    ("com/rs2/model/npc/NpcDefinition", "w", "D"): "poisonChance",
    ("com/rs2/model/npc/NpcDefinition", "x", "I"): "cacheDefinitionCount",
    ("com/rs2/model/npc/NpcDefinition", "y", "I"): "shopId",
    ("com/rs2/model/npc/NpcDefinition", "z", "I"): "id",
    ("com/rs2/model/npc/NpcDefinition", "A", "I"): "dropTableNpcIdOverride",
    ("com/rs2/model/npc/NpcDefinition", "B", "Ljava/lang/String;"): "name",
    ("com/rs2/model/npc/NpcDefinition", "C", "I"): "legacyAttackBonus",
    ("com/rs2/model/npc/NpcDefinition", "D", "I"): "legacyMeleeDefenceBonus",
    ("com/rs2/model/npc/NpcDefinition", "E", "I"): "legacyRangedDefenceBonus",
    ("com/rs2/model/npc/NpcDefinition", "F", "I"): "legacyMagicDefenceBonus",
    ("com/rs2/model/npc/NpcDefinition", "G", "I"): "combatLevel",
    ("com/rs2/model/npc/NpcDefinition", "H", "I"): "hitpoints",
    ("com/rs2/model/npc/NpcDefinition", "I", "I"): "maxHit",
    ("com/rs2/model/npc/NpcDefinition", "J", "I"): "size",
    ("com/rs2/model/npc/NpcDefinition", "K", "I"): "attackDelay",
    ("com/rs2/model/npc/NpcDefinition", "L", "I"): "attackAnimationId",
    ("com/rs2/model/npc/NpcDefinition", "M", "I"): "blockAnimationId",
    ("com/rs2/model/npc/NpcDefinition", "N", "I"): "deathAnimationId",
    ("com/rs2/model/npc/NpcDefinition", "O", "I"): "hitSoundId",
    ("com/rs2/model/npc/NpcDefinition", "P", "I"): "attackSoundId",
    ("com/rs2/model/npc/NpcDefinition", "Q", "I"): "deathSoundId",
    ("com/rs2/model/npc/NpcDefinition", "R", "Z"): "attackable",
    ("com/rs2/model/npc/NpcDefinition", "S", "I"): "aggressionType",
    ("com/rs2/model/npc/NpcDefinition", "V", "Z"): "protectedFromMelee",
    ("com/rs2/model/npc/NpcDefinition", "W", "Z"): "protectedFromRanged",
    ("com/rs2/model/npc/NpcDefinition", "X", "Z"): "protectedFromMagic",
    ("com/rs2/model/c/ProjectileDefinition", "a", "Lcom/rs2/model/combat/ProjectileTiming;"): "timing",
    ("com/rs2/model/c/ProjectileDefinition", "b", "I"): "projectileId",
    ("com/rs2/model/npc/combat/NpcDefinitionAttackStyleCombatDefinition", "a", "Lcom/rs2/model/npc/NpcDefinition;"): "npcDefinition",
    ("com/rs2/model/npc/combat/NpcDefinitionMeleeCombatDefinition", "a", "Lcom/rs2/model/npc/NpcDefinition;"): "npcDefinition",
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "a", "Ljava/util/Map;"): "definitionsByNpcId",
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "b", "I"): "deathDelayTicks",
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "c", "I"): "respawnDelayTicks",
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "d", "Z"): "attackBonusesSet",
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "e", "Z"): "defenceBonusesSet",
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "f", "Ljava/util/Map;"): "combatBonuses",
    ("com/rs2/net/packet/IncomingPacket", "a", "I"): "opcode",
    ("com/rs2/net/packet/IncomingPacket", "b", "I"): "length",
    ("com/rs2/net/packet/IncomingPacket", "c", "Lcom/rs2/net/packet/PacketReader;"): "reader",
    ("com/rs2/net/packet/PacketDispatcher", "a", "[Lcom/rs2/net/packet/PacketHandler;"): "packetHandlers",
    ("com/rs2/net/packet/PacketDispatcher", "b", "[Lcom/rs2/util/ProfilerTimer;"): "packetTimers",
    ("com/rs2/net/packet/PacketDispatcher", "c", "Lcom/rs2/net/packet/handler/NoOpPacketHandler;"): "noOpHandler",
    ("com/rs2/net/packet/PacketDispatcher", "d", "Lcom/rs2/net/packet/handler/MovementPacketHandler;"): "movementHandler",
    ("com/rs2/net/packet/PacketDispatcher", "e", "Lcom/rs2/net/packet/handler/ObjectInteractionPacketHandler;"): "objectInteractionHandler",
    ("com/rs2/net/packet/PacketDispatcher", "f", "Lcom/rs2/net/packet/handler/ItemActionPacketHandler;"): "itemActionHandler",
    ("com/rs2/net/packet/PacketDispatcher", "g", "Lcom/rs2/net/packet/handler/ItemSpawnPacketHandler;"): "itemSpawnHandler",
    ("com/rs2/net/packet/PacketDispatcher", "h", "Lcom/rs2/net/packet/handler/InterfaceInputPacketHandler;"): "interfaceInputHandler",
    ("com/rs2/net/packet/PacketDispatcher", "i", "Lcom/rs2/net/packet/handler/SocialPacketHandler;"): "socialHandler",
    ("com/rs2/net/packet/PacketDispatcher", "j", "Lcom/rs2/net/packet/handler/NpcInteractionPacketHandler;"): "npcInteractionHandler",
    ("com/rs2/net/packet/PacketDispatcher", "k", "Lcom/rs2/net/packet/handler/PlayerInteractionPacketHandler;"): "playerInteractionHandler",
    ("com/rs2/ServerSettings", "bU", "[[[I"): "APPEARANCE_COLOR_RANGES",
    ("com/rs2/ServerSettings", "bV", "[[[I"): "APPEARANCE_BODY_PART_RANGES",
    ("com/rs2/model/player/Player", "C", "Lcom/rs2/net/packet/PacketSender;"): "packetSender",
    ("com/rs2/model/player/Player", "b", "Ljava/nio/channels/SelectionKey;"): "selectionKey",
    ("com/rs2/model/player/Player", "c", "Ljava/nio/ByteBuffer;"): "inboundBuffer",
    ("com/rs2/model/player/Player", "d", "Ljava/nio/ByteBuffer;"): "outboundBuffer",
    ("com/rs2/model/player/Player", "e", "Ljava/nio/channels/SocketChannel;"): "socketChannel",
    ("com/rs2/model/player/Player", "eJ", "Lcom/rs2/model/player/PlayerConnectionState;"): "connectionState",
    ("com/rs2/model/player/Player", "eK", "Lcom/rs2/net/IsaacCipher;"): "outboundCipher",
    ("com/rs2/model/player/Player", "eL", "Lcom/rs2/net/IsaacCipher;"): "inboundCipher",
    ("com/rs2/model/player/Player", "eM", "I"): "currentPacketOpcode",
    ("com/rs2/model/player/Player", "eN", "I"): "currentPacketLength",
    ("com/rs2/model/player/Player", "eO", "Ljava/lang/String;"): "username",
    ("com/rs2/model/player/Player", "eP", "Ljava/lang/String;"): "password",
    ("com/rs2/model/player/Player", "eQ", "Ljava/lang/String;"): "submittedPassword",
    ("com/rs2/model/player/Player", "ek", "Ljava/lang/String;"): "lastLoginHostAddress",
    ("com/rs2/model/player/Player", "eg", "J"): "createdAtMillis",
    ("com/rs2/model/player/Player", "eh", "J"): "lastSavedMillis",
    ("com/rs2/model/player/Player", "ei", "J"): "sessionStartMillis",
    ("com/rs2/model/player/Player", "ej", "J"): "totalPlaytimeMillis",
    ("com/rs2/model/player/Player", "eR", "I"): "clientBuild",
    ("com/rs2/model/player/Player", "eT", "I"): "loginMagicByte",
    ("com/rs2/model/player/Player", "eV", "Lcom/rs2/util/ElapsedTimer;"): "packetReadTimer",
    ("com/rs2/model/player/Player", "fT", "I"): "playerRights",
    ("com/rs2/model/player/Player", "x", "I"): "gameMode",
    ("com/rs2/model/player/Player", "M", "I"): "pendingGameMode",
    ("com/rs2/model/player/Player", "fZ", "I"): "gender",
    ("com/rs2/model/player/Player", "ga", "[I"): "appearanceParts",
    ("com/rs2/model/player/Player", "gb", "[I"): "appearanceColors",
    ("com/rs2/model/player/Player", "fV", "I"): "publicChatColor",
    ("com/rs2/model/player/Player", "fX", "[B"): "publicChatPayload",
    ("com/rs2/model/player/Player", "fY", "[I"): "essencePouchAmounts",
    ("com/rs2/model/player/Player", "gP", "I"): "publicChatEffects",
    ("com/rs2/model/player/Player", "gR", "I"): "privateChatMode",
    ("com/rs2/model/player/Player", "gS", "I"): "publicChatMode",
    ("com/rs2/model/player/Player", "gT", "I"): "tradeMode",
    ("com/rs2/model/player/Player", "N", "I"): "temporaryActionValue",
    ("com/rs2/model/player/Player", "gw", "[J"): "friendsList",
    ("com/rs2/model/player/Player", "gx", "[J"): "ignoreList",
    ("com/rs2/model/player/Player", "ht", "J"): "nameHash",
    ("com/rs2/model/player/Player", "hv", "Lcom/rs2/model/combat/special/SpecialAttackDefinition;"): "specialAttackDefinition",
    ("com/rs2/model/player/Player", "hx", "Lcom/rs2/model/skill/magic/SpellDefinition;"): "queuedCombatSpell",
    ("com/rs2/model/player/Player", "hy", "Lcom/rs2/model/skill/magic/SpellDefinition;"): "autocastSpell",
    ("com/rs2/model/player/Player", "hz", "Z"): "autocastEnabled",
    ("com/rs2/model/player/Player", "gG", "I"): "prayerHeadIcon",
    ("com/rs2/model/player/Player", "gH", "I"): "skullIcon",
    ("com/rs2/model/player/Player", "F", "Z"): "skulled",
    ("com/rs2/model/player/Player", "gJ", "[Z"): "activePrayers",
    ("com/rs2/model/player/Player", "gC", "I"): "runEnergyRaw",
    ("com/rs2/model/player/Player", "gL", "Z"): "autoRetaliate",
    ("com/rs2/model/player/Player", "gN", "I"): "brightness",
    ("com/rs2/model/player/Player", "gO", "I"): "mouseButtons",
    ("com/rs2/model/player/Player", "gQ", "I"): "splitPrivateChat",
    ("com/rs2/model/player/Player", "gU", "I"): "acceptAid",
    ("com/rs2/model/player/Player", "gV", "I"): "musicVolume",
    ("com/rs2/model/player/Player", "gW", "I"): "effectVolume",
    ("com/rs2/model/player/Player", "gK", "Lcom/rs2/model/skill/magic/Spellbook;"): "spellbook",
    ("com/rs2/model/player/Player", "gM", "Z"): "actionLocked",
    ("com/rs2/model/player/Player", "gY", "Z"): "specialAttackEnabled",
    ("com/rs2/model/player/Player", "gZ", "I"): "specialEnergy",
    ("com/rs2/model/player/Player", "ha", "I"): "ringOfRecoilLife",
    ("com/rs2/model/player/Player", "hb", "I"): "ringOfForgingLife",
    ("com/rs2/model/player/Player", "hc", "I"): "bindingNecklaceCharge",
    ("com/rs2/model/player/Player", "hd", "I"): "fightMode",
    ("com/rs2/model/player/Player", "he", "Z"): "crystalBowEquipped",
    ("com/rs2/model/player/Player", "hf", "Z"): "ammunitionDropsEnabled",
    ("com/rs2/model/player/Player", "hg", "Z"): "dharokSetEffectActive",
    ("com/rs2/model/player/Player", "hh", "Z"): "ahrimSetEffectActive",
    ("com/rs2/model/player/Player", "hi", "Z"): "karilSetEffectActive",
    ("com/rs2/model/player/Player", "hj", "Z"): "toragSetEffectActive",
    ("com/rs2/model/player/Player", "hk", "Z"): "guthanSetEffectActive",
    ("com/rs2/model/player/Player", "hl", "Z"): "veracSetEffectActive",
    ("com/rs2/model/player/Player", "ho", "I"): "currentWalkableInterfaceId",
    ("com/rs2/model/player/Player", "hn", "J"): "protectionPrayerDisabledUntil",
    ("com/rs2/model/player/Player", "hw", "Ljava/util/List;"): "pvpCombatReferences",
    ("com/rs2/model/player/Player", "hA", "Z"): "hideHeldItemsInAppearance",
    ("com/rs2/model/player/Player", "hD", "J"): "muteExpires",
    ("com/rs2/model/player/Player", "hE", "J"): "banExpires",
    ("com/rs2/model/player/Player", "hI", "Z"): "brimhavenOpen",
    ("com/rs2/model/player/Player", "hB", "Lcom/rs2/util/RectangularArea;"): "localViewArea",
    ("com/rs2/model/player/Player", "hC", "Ljava/util/List;"): "visibleGroundItems",
    ("com/rs2/model/player/Player", "hR", "I"): "selectedSkillItemId",
    ("com/rs2/model/player/Player", "aw", "Z"): "forcedMovementActive",
    ("com/rs2/model/player/Player", "hp", "I"): "selectedSmithingBarItemId",
    ("com/rs2/model/player/Player", "hq", "I"): "cookingObjectId",
    ("com/rs2/model/player/Player", "hr", "Lcom/rs2/model/skill/smithing/SmithingBarDefinition;"): "selectedSmithingBarDefinition",
    ("com/rs2/model/player/Player", "hs", "I"): "abyssMageNpcId",
    ("com/rs2/model/player/Player", "ia", "Lcom/rs2/model/player/Player;"): "tradeRequestTarget",
    ("com/rs2/model/player/Player", "hL", "Z"): "bankPinReminderShown",
    ("com/rs2/model/player/Player", "hX", "[I"): "bankPinEntryDigits",
    ("com/rs2/model/player/Player", "hM", "Lcom/rs2/model/npc/Npc;"): "activeRandomEventNpc",
    ("com/rs2/model/player/Player", "eS", "I"): "bossPetUnlockFlags",
    ("com/rs2/model/player/Player", "hK", "Lcom/rs2/model/item/ItemStack;"): "pendingDestroyItem",
    ("com/rs2/model/player/Player", "hN", "Lcom/rs2/model/item/ItemStack;"): "randomEventRequestedItem",
    ("com/rs2/model/player/Player", "hY", "J"): "disconnectGraceExpiresAtMillis",
    ("com/rs2/model/player/Player", "hZ", "I"): "coalTruckCoalCount",
    ("com/rs2/model/player/Player", "ib", "Lcom/rs2/model/player/Player;"): "duelRequestTarget",
    ("com/rs2/model/player/Player", "fW", "I"): "idlePacketCount",
    ("com/rs2/model/player/Player", "as", "Z"): "killedClueAttacker",
    ("com/rs2/model/bankpin/BankPinManager", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/bankpin/BankPinManager", "b", "[I"): "currentPin",
    ("com/rs2/model/bankpin/BankPinManager", "c", "[I"): "pendingPin",
    ("com/rs2/model/bankpin/BankPinManager", "d", "I"): "currentDigitIndex",
    ("com/rs2/model/bankpin/BankPinManager", "e", "I"): "pinAppendYear",
    ("com/rs2/model/bankpin/BankPinManager", "f", "I"): "pinAppendDate",
    ("com/rs2/model/bankpin/BankPinManager", "g", "Z"): "verified",
    ("com/rs2/model/bankpin/BankPinManager", "h", "Z"): "changingPin",
    ("com/rs2/model/bankpin/BankPinManager", "i", "Z"): "deletingPin",
    ("com/rs2/model/bankpin/BankPinManager", "j", "Lcom/rs2/model/bankpin/BankPinEntryMode;"): "entryMode",
    ("com/rs2/model/bankpin/BankPinManager", "k", "Lcom/rs2/model/bankpin/BankPinProtectedAction;"): "protectedAction",
    ("com/rs2/model/bankpin/BankPinManager", "l", "[I"): "digitButtonIds",
    ("com/rs2/model/skill/slayer/SlayerManager", "a", "Lcom/rs2/model/npc/Npc;"): "ZYGOMITE_SPAWN_A",
    ("com/rs2/model/skill/slayer/SlayerManager", "b", "Lcom/rs2/model/npc/Npc;"): "ZYGOMITE_SPAWN_B",
    ("com/rs2/model/skill/slayer/SlayerManager", "c", "Lcom/rs2/model/npc/Npc;"): "MOGRE_SPAWN",
    ("com/rs2/model/skill/slayer/SlayerManager", "n", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/skill/slayer/SlayerManager", "d", "I"): "slayerMasterId",
    ("com/rs2/model/skill/slayer/SlayerManager", "e", "Ljava/lang/String;"): "slayerTaskName",
    ("com/rs2/model/skill/slayer/SlayerManager", "f", "I"): "taskAmount",
    ("com/rs2/model/skill/slayer/SlayerManager", "g", "[I"): "FUNGICIDE_SPRAY_ITEM_IDS",
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "h",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ): "BURTHORPE_ASSIGNMENTS",
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "i",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ): "CANIFIS_ASSIGNMENTS",
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "j",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ): "EDGEVILLE_DUNGEON_ASSIGNMENTS",
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "k",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ): "ZANARIS_ASSIGNMENTS",
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "l",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ): "SHILO_VILLAGE_ASSIGNMENTS",
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "m",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ): "EDGEVILLE_ASSIGNMENTS",
    (
        "com/rs2/model/skill/slayer/ZygomiteSpawnTask",
        "a",
        "Lcom/rs2/model/skill/slayer/SlayerManager;",
    ): "slayerManager",
    ("com/rs2/model/skill/slayer/ZygomiteSpawnTask", "b", "Lcom/rs2/model/npc/Npc;"): "sourceNpc",
    ("com/rs2/model/skill/slayer/ZygomiteSpawnTask", "c", "I"): "spawnX",
    ("com/rs2/model/skill/slayer/ZygomiteSpawnTask", "d", "I"): "spawnY",
    (
        "com/rs2/model/skill/slayer/MogreSpawnTask",
        "a",
        "Lcom/rs2/model/skill/slayer/SlayerManager;",
    ): "slayerManager",
    ("com/rs2/model/clue/AnagramClue", "w", "I"): "clueItemId",
    ("com/rs2/model/clue/AnagramClue", "x", "I"): "npcId",
    ("com/rs2/model/clue/AnagramClue", "y", "Ljava/lang/String;"): "anagramText",
    ("com/rs2/model/clue/AnagramClue", "z", "Ljava/lang/String;"): "followupType",
    ("com/rs2/model/clue/AnagramClue", "A", "I"): "level",
    ("com/rs2/model/clue/AnagramClue", "B", "Ljava/util/Map;"): "cluesByNpcId",
    ("com/rs2/model/clue/AnagramClue", "C", "Ljava/util/Map;"): "cluesByItemId",
    ("com/rs2/model/clue/ChallengeQuestion", "o", "[Ljava/lang/String;"): "questionLines",
    ("com/rs2/model/clue/ChallengeQuestion", "p", "I"): "clueItemId",
    ("com/rs2/model/clue/ChallengeQuestion", "q", "I"): "answerItemId",
    ("com/rs2/model/clue/ChallengeQuestion", "r", "I"): "answerValue",
    ("com/rs2/model/clue/ChallengeQuestion", "s", "Ljava/util/Map;"): "questionsByClueItemId",
    ("com/rs2/model/clue/ChallengeQuestion", "t", "Ljava/util/Map;"): "questionsByAnswerItemId",
    ("com/rs2/model/clue/CoordinateClue", "Q", "I"): "clueItemId",
    ("com/rs2/model/clue/CoordinateClue", "R", "I"): "latitudeDegrees",
    ("com/rs2/model/clue/CoordinateClue", "S", "I"): "latitudeMinutes",
    ("com/rs2/model/clue/CoordinateClue", "T", "I"): "longitudeDegrees",
    ("com/rs2/model/clue/CoordinateClue", "U", "I"): "longitudeMinutes",
    ("com/rs2/model/clue/CoordinateClue", "V", "Ljava/lang/String;"): "latitudeDirection",
    ("com/rs2/model/clue/CoordinateClue", "W", "Ljava/lang/String;"): "longitudeDirection",
    ("com/rs2/model/clue/CoordinateClue", "X", "I"): "level",
    ("com/rs2/model/clue/CoordinateClue", "Y", "Lcom/rs2/model/Position;"): "position",
    ("com/rs2/model/clue/CoordinateClue", "Z", "Ljava/util/Map;"): "cluesByItemId",
    ("com/rs2/model/clue/CoordinateClue", "aa", "Ljava/util/Map;"): "cluesByPosition",
    ("com/rs2/model/clue/SearchClue", "as", "[Ljava/lang/String;"): "clueTextLines",
    ("com/rs2/model/clue/SearchClue", "at", "I"): "clueItemId",
    ("com/rs2/model/clue/SearchClue", "au", "Lcom/rs2/model/Position;"): "position",
    ("com/rs2/model/clue/SearchClue", "av", "I"): "replacementObjectId",
    ("com/rs2/model/clue/SearchClue", "aw", "I"): "animationId",
    ("com/rs2/model/clue/SearchClue", "ax", "I"): "level",
    ("com/rs2/model/clue/SearchClue", "ay", "Ljava/util/Map;"): "cluesByPosition",
    ("com/rs2/model/clue/SearchClue", "az", "Ljava/util/Map;"): "cluesByItemId",
    ("com/rs2/model/clue/NpcClue", "F", "[Ljava/lang/String;"): "clueTextLines",
    ("com/rs2/model/clue/NpcClue", "G", "I"): "clueItemId",
    ("com/rs2/model/clue/NpcClue", "H", "I"): "npcId",
    ("com/rs2/model/clue/NpcClue", "I", "Ljava/lang/String;"): "followupType",
    ("com/rs2/model/clue/NpcClue", "J", "I"): "level",
    ("com/rs2/model/clue/NpcClue", "K", "Ljava/util/Map;"): "cluesByNpcId",
    ("com/rs2/model/clue/NpcClue", "L", "Ljava/util/Map;"): "cluesByItemId",
    ("com/rs2/model/clue/MapClue", "x", "I"): "clueItemId",
    ("com/rs2/model/clue/MapClue", "y", "I"): "interfaceId",
    ("com/rs2/model/clue/MapClue", "z", "Lcom/rs2/model/Position;"): "position",
    ("com/rs2/model/clue/MapClue", "A", "Z"): "objectSearchClue",
    ("com/rs2/model/clue/MapClue", "B", "I"): "level",
    ("com/rs2/model/clue/MapClue", "C", "Ljava/util/Map;"): "cluesByPosition",
    ("com/rs2/model/clue/MapClue", "D", "Ljava/util/Map;"): "cluesByItemId",
    ("com/rs2/model/clue/CrypticDigClue", "i", "[Ljava/lang/String;"): "clueTextLines",
    ("com/rs2/model/clue/CrypticDigClue", "j", "I"): "clueItemId",
    ("com/rs2/model/clue/CrypticDigClue", "k", "Lcom/rs2/model/Position;"): "position",
    ("com/rs2/model/clue/CrypticDigClue", "l", "I"): "level",
    ("com/rs2/model/clue/CrypticDigClue", "m", "Ljava/util/Map;"): "cluesByItemId",
    ("com/rs2/model/clue/CrypticDigClue", "n", "Ljava/util/Map;"): "cluesByPosition",
    ("com/rs2/model/clue/CoordinateClueHandler", "a", "Lcom/rs2/model/Position;"): "COORDINATE_ORIGIN",
    ("com/rs2/model/clue/ClueKeyHandler", "a", "I"): "CLUE_7276_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "b", "I"): "CLUE_3615_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "c", "I"): "CLUE_3616_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "d", "I"): "CLUE_3617_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "e", "I"): "CLUE_7284_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "f", "I"): "CLUE_7274_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "g", "I"): "CLUE_3618_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "h", "I"): "CLUE_7300_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "i", "I"): "CLUE_7280_KEY_ITEM_ID",
    ("com/rs2/model/clue/ClueKeyHandler", "j", "I"): "CLUE_7282_KEY_ITEM_ID",
    ("com/rs2/model/clue/TreasureTrailManager", "a", "[I"): "DEFAULT_SLIDER_PUZZLE_PIECES",
    ("com/rs2/model/clue/TreasureTrailManager", "b", "[I"): "SLIDER_PUZZLE_ONE_PIECES",
    ("com/rs2/model/clue/TreasureTrailManager", "c", "[I"): "SLIDER_PUZZLE_TWO_PIECES",
    ("com/rs2/model/clue/TreasureTrailManager", "d", "[I"): "SLIDER_PUZZLE_THREE_PIECES",
    ("com/rs2/model/clue/TreasureTrailManager", "e", "[I"): "tierOneCommonRewardItems",
    ("com/rs2/model/clue/TreasureTrailManager", "f", "[I"): "tierTwoCommonRewardItems",
    ("com/rs2/model/clue/TreasureTrailManager", "g", "[I"): "tierThreeCommonRewardItems",
    ("com/rs2/model/clue/TreasureTrailManager", "h", "[I"): "tierFourCommonRewardItems",
    ("com/rs2/model/clue/TreasureTrailManager", "i", "[I"): "tierOneUniqueRewardItems",
    ("com/rs2/model/clue/TreasureTrailManager", "j", "[I"): "tierTwoUniqueRewardItems",
    ("com/rs2/model/clue/TreasureTrailManager", "k", "[I"): "tierThreeUniqueRewardItems",
    ("com/rs2/model/clue/TreasureTrailManager", "l", "[I"): "tierFourUniqueRewardItems",
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "Ljava/util/ArrayList;"): "pieceQueue",
    ("com/rs2/model/clue/PuzzleBoxHandler", "b", "I"): "activePuzzleType",
    ("com/rs2/cache/CacheStore", "a", "Z"): "cacheVerificationFailed",
    ("com/rs2/cache/CacheStore", "b", "Lcom/rs2/cache/CacheStore;"): "instance",
    ("com/rs2/cache/CacheStore", "c", "Ljava/io/RandomAccessFile;"): "dataFile",
    ("com/rs2/cache/CacheStore", "d", "[Ljava/io/RandomAccessFile;"): "indexFiles",
    ("com/rs2/cache/CacheStore", "e", "Lcom/rs2/cache/CacheDefinitionIndex;"): "definitionIndex",
    ("com/rs2/cache/CacheFile", "a", "Ljava/nio/ByteBuffer;"): "buffer",
    ("com/rs2/cache/CacheArchive", "a", "Z"): "wholeArchiveDecompressed",
    ("com/rs2/cache/CacheArchive", "b", "Ljava/nio/ByteBuffer;"): "archiveBuffer",
    ("com/rs2/cache/CacheArchive", "c", "Ljava/util/Map;"): "entriesByNameHash",
    ("com/rs2/cache/CacheArchiveEntry", "a", "I"): "nameHash",
    ("com/rs2/cache/CacheArchiveEntry", "b", "I"): "compressedSize",
    ("com/rs2/cache/CacheArchiveEntry", "c", "I"): "dataOffset",
    ("com/rs2/cache/DefinitionIndexEntry", "a", "I"): "dataOffset",
    ("com/rs2/cache/CacheDefinitionIndex", "a", "[Lcom/rs2/cache/MapIndexEntry;"): "mapIndexEntries",
    ("com/rs2/cache/CacheDefinitionIndex", "b", "[Lcom/rs2/cache/DefinitionIndexEntry;"): "objectDefinitionEntries",
    ("com/rs2/cache/CacheDefinitionIndex", "c", "[Lcom/rs2/cache/DefinitionIndexEntry;"): "itemDefinitionEntries",
    ("com/rs2/cache/CacheDefinitionIndex", "d", "[Lcom/rs2/cache/DefinitionIndexEntry;"): "npcDefinitionEntries",
    ("com/rs2/cache/InterfaceDefinition", "a", "I"): "interfaceCount",
    ("com/rs2/cache/InterfaceDefinition", "b", "[Lcom/rs2/cache/InterfaceDefinition;"): "definitionsById",
    ("com/rs2/cache/InterfaceDefinition", "c", "I"): "parentInterfaceId",
    ("com/rs2/bot/BotTaskDefinition", "bn", "[Lcom/rs2/bot/BotTaskDefinition;"): "progressiveTaskDefinitions",
    ("com/rs2/bot/BotTaskDefinition", "bo", "[Lcom/rs2/bot/BotTaskDefinition;"): "tradeAdvertTaskDefinitions",
    ("com/rs2/bot/BotTaskDefinition", "bp", "[Lcom/rs2/bot/BotTaskDefinition;"): "dropPartyTaskDefinitions",
    ("com/rs2/bot/BotTaskDefinition", "l", "Ljava/util/ArrayList;"): "brassKeyTasks",
    ("com/rs2/bot/BotTaskDefinition", "m", "Ljava/util/ArrayList;"): "shopTasks",
    ("com/rs2/bot/BotTaskDefinition", "n", "Ljava/util/ArrayList;"): "fishingTasks",
    ("com/rs2/bot/BotTaskDefinition", "o", "Ljava/util/ArrayList;"): "cookingTasks",
    ("com/rs2/bot/BotTaskDefinition", "p", "Ljava/util/ArrayList;"): "miningTasks",
    ("com/rs2/bot/BotTaskDefinition", "q", "Ljava/util/ArrayList;"): "smeltingTasks",
    ("com/rs2/bot/BotTaskDefinition", "r", "Ljava/util/ArrayList;"): "smithingTasks",
    ("com/rs2/bot/BotTaskDefinition", "s", "Ljava/util/ArrayList;"): "woodcuttingTasks",
    ("com/rs2/bot/BotTaskDefinition", "t", "Ljava/util/ArrayList;"): "runecraftingTasks",
    ("com/rs2/bot/BotTaskDefinition", "u", "Ljava/util/ArrayList;"): "moneyMakingTasks",
    ("com/rs2/bot/BotTaskDefinition", "v", "Ljava/util/ArrayList;"): "sheepShearingTasks",
    ("com/rs2/bot/BotTaskDefinition", "w", "Ljava/util/ArrayList;"): "spinningTasks",
    ("com/rs2/bot/BotTaskDefinition", "x", "Ljava/util/ArrayList;"): "tanningTasks",
    ("com/rs2/bot/BotTaskDefinition", "y", "Ljava/util/ArrayList;"): "leatherCraftingTasks",
    ("com/rs2/bot/BotTaskDefinition", "z", "Ljava/util/ArrayList;"): "combatTasks",
    ("com/rs2/bot/BotTaskDefinition", "A", "Ljava/util/ArrayList;"): "tradeAdvertTaskPool",
    ("com/rs2/bot/BotTaskDefinition", "B", "Ljava/util/ArrayList;"): "dropPartyTaskPool",
    ("com/rs2/bot/BotTaskDefinition", "C", "Ljava/util/ArrayList;"): "progressiveTaskPool",
    ("com/rs2/bot/BotTaskDefinition", "bq", "Ljava/util/ArrayList;"): "lootSellShopTasks",
    ("com/rs2/bot/BotTaskDefinition", "br", "I"): "totalTradeAdvertTaskWeight",
    ("com/rs2/bot/BotTaskDefinition", "bs", "I"): "totalProgressiveTaskWeight",
    ("com/rs2/bot/route/BotWorldRouteChoice", "a", "Lcom/rs2/bot/route/BotWorldRoute;"): "route",
    ("com/rs2/bot/route/BotWorldRouteChoice", "b", "Z"): "reversed",
    ("com/rs2/bot/BotTaskDefinition", "D", "I"): "minimumServerRevision",
    ("com/rs2/bot/BotTaskDefinition", "E", "Ljava/util/ArrayList;"): "lootSellShopIds",
    ("com/rs2/bot/BotTaskDefinition", "F", "Z"): "usesCustomTaskAction",
    ("com/rs2/bot/BotTaskDefinition", "G", "Z"): "usesEscapeMonitor",
    ("com/rs2/bot/BotTaskDefinition", "H", "Lcom/rs2/model/Position;"): "startPosition",
    ("com/rs2/bot/BotTaskDefinition", "bt", "[Lcom/rs2/util/RectangularArea;"): "taskAreas",
    ("com/rs2/bot/BotTaskDefinition", "bu", "Lcom/rs2/bot/BotRoute;"): "pretaskRoute",
    ("com/rs2/bot/BotTaskDefinition", "I", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/BotTaskDefinition", "J", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/BotTaskDefinition", "K", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/BotTaskDefinition", "L", "I"): "interactionTargetType",
    ("com/rs2/bot/BotTaskDefinition", "M", "Z"): "membersOnly",
    ("com/rs2/bot/BotTaskDefinition", "N", "I"): "interactionOption",
    ("com/rs2/bot/BotTaskDefinition", "O", "Z"): "combatTask",
    ("com/rs2/bot/BotTaskDefinition", "P", "Z"): "usesDepositBox",
    ("com/rs2/bot/BotTaskDefinition", "Q", "I"): "selectionWeight",
    ("com/rs2/bot/BotTaskDefinition", "R", "I"): "targetSearchRadius",
    ("com/rs2/bot/BotTaskDefinition", "S", "Ljava/util/ArrayList;"): "assignedBotPlayers",
    ("com/rs2/bot/BotTaskDefinition", "T", "I"): "targetMaxX",
    ("com/rs2/bot/BotTaskDefinition", "U", "I"): "targetMaxY",
    ("com/rs2/bot/BotTaskDefinition", "V", "I"): "targetMinX",
    ("com/rs2/bot/BotTaskDefinition", "W", "I"): "targetMinY",
    ("com/rs2/bot/BotTaskDefinition", "X", "I"): "forcedCombatStyle",
    ("com/rs2/bot/BotTaskDefinition", "Y", "Z"): "usesCombatTradeAdvertItems",
    ("com/rs2/bot/BotTaskDefinition", "Z", "I"): "dropPartyBotJoinIndex",
    ("com/rs2/bot/tasks/DraynorChickenCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorChickenCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/DraynorChickenCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/FaladorCowCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/FaladorCowCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/FaladorCowCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/LumbridgeGoblinCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/LumbridgeGoblinCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/LumbridgeGoblinCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/VarrockGuardCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockGuardCombatBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockGuardCombatBotTask", "ac", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/FaladorImpCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/FaladorImpCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/FaladorImpCombatBotTask", "ac", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/LumbridgeEastChickenCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/LumbridgeEastChickenCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/LumbridgeEastChickenCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/LumbridgeWestChickenCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/LumbridgeWestChickenCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/LumbridgeWestChickenCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/LumbridgeCowCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/LumbridgeCowCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/LumbridgeCowCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/DraynorGoblinCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorGoblinCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/DraynorGoblinCombatBotTask", "ac", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/AlKharidWarriorCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AlKharidWarriorCombatBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockSewerGiantRatCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockSewerGiantRatCombatBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/VarrockSouthChickenCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockSouthChickenCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/VarrockSouthChickenCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/FaladorGuardCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/FaladorGuardCombatBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/FaladorGuardCombatBotTask", "ac", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/DwarvenMineDwarfCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DwarvenMineDwarfCombatBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/BarbarianVillageBarbarianCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/BarbarianVillageBarbarianCombatBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/BarbarianVillageBarbarianCombatBotTask", "ac", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/BrimhavenDungeonBlueDragonNorthCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/BrimhavenDungeonBlueDragonNorthCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/BrimhavenDungeonBlueDragonNorthCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/BrimhavenDungeonBlueDragonSouthCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/BrimhavenDungeonBlueDragonSouthCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/BrimhavenDungeonBlueDragonSouthCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/BrimhavenDungeonRedDragonCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/BrimhavenDungeonRedDragonCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/BrimhavenDungeonRedDragonCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EdgevilleDungeonHillGiantCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleDungeonHillGiantCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/EdgevilleDungeonHillGiantCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EdgevilleDungeonNorthMossGiantCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleDungeonNorthMossGiantCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/EdgevilleDungeonNorthMossGiantCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EdgevilleDungeonSkeletonCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleDungeonSkeletonCombatBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EdgevilleDungeonSkeletonCombatBotTask", "ac", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/EdgevilleDungeonSouthMossGiantCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleDungeonSouthMossGiantCombatBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/EdgevilleDungeonSouthMossGiantCombatBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EdgevilleDungeonSpiderRatCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleDungeonSpiderRatCombatBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/KaramjaVolcanoNorthLesserDemonCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/KaramjaVolcanoNorthLesserDemonCombatBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/KaramjaVolcanoNorthLesserDemonCombatBotTask", "ac", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/KaramjaVolcanoSouthLesserDemonCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/KaramjaVolcanoSouthLesserDemonCombatBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/KaramjaVolcanoSouthLesserDemonCombatBotTask", "ac", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/TaverleyDungeonHellhoundCombatBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/TaverleyDungeonHellhoundCombatBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/AirRuneRunecraftingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AirRuneRunecraftingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/MindRuneRunecraftingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/MindRuneRunecraftingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/WaterRuneRunecraftingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/WaterRuneRunecraftingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EarthRuneRunecraftingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EarthRuneRunecraftingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/FireRuneRunecraftingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/FireRuneRunecraftingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/BodyRuneRunecraftingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/BodyRuneRunecraftingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/VarrockRuneEssenceMiningBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockRuneEssenceMiningBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/AlKharidMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AlKharidMineBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/CraftingGuildMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/CraftingGuildMineBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/DwarvenMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DwarvenMineBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/MiningGuildMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/MiningGuildMineBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/RimmingtonMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/RimmingtonMineBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockEastMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockEastMineBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockWestMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockWestMineBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/WildernessRuniteMineBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/WildernessRuniteMineBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/AlKharidFlyFishingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AlKharidFlyFishingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/AlKharidNetBaitFishingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AlKharidNetBaitFishingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/BarbarianVillageFlyFishingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/BarbarianVillageFlyFishingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/CatherbyFishingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/CatherbyFishingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/DraynorNetFishingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorNetFishingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/KaramjaFishingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/KaramjaFishingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/DraynorOakWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorOakWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/DraynorTreeWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorTreeWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/DraynorWillowWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorWillowWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/DraynorYewWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorYewWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/EdgevilleTreeWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleTreeWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/EdgevilleYewWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleYewWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/FaladorYewWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/FaladorYewWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/SeersMagicTreeWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/SeersMagicTreeWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/SeersMapleWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/SeersMapleWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/SeersYewWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/SeersYewWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/SorcerersTowerMagicTreeWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/SorcerersTowerMagicTreeWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockEastOakWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockEastOakWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockEastTreeWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockEastTreeWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockPalaceYewWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockPalaceYewWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockWestOakWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockWestOakWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/VarrockWestTreeWoodcuttingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockWestTreeWoodcuttingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/AlKharidCowhideTanningBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AlKharidCowhideTanningBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/AlKharidLobsterCookingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AlKharidLobsterCookingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/AlKharidSteelSmeltingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/AlKharidSteelSmeltingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/CatherbyLobsterCookingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/CatherbyLobsterCookingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/DraynorSheepShearingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/DraynorSheepShearingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/FaladorSteelSmeltingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/FaladorSteelSmeltingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/LumbridgeWoolSpinningBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/LumbridgeWoolSpinningBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/SeersFlaxPickingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/SeersFlaxPickingBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "taskRoute",
    ("com/rs2/bot/tasks/SeersFlaxSpinningBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/SeersFlaxSpinningBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/VarrockLobsterCookingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockLobsterCookingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/VarrockSteelDaggerSmithingBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/VarrockSteelDaggerSmithingBotTask", "ab", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EdgevilleDungeonBrassKeyBotTask", "aa", "Lcom/rs2/model/Position;"): "brassKeySpawnPosition",
    ("com/rs2/bot/tasks/EdgevilleDungeonBrassKeyBotTask", "ab", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/EdgevilleDungeonBrassKeyBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/BrassKeyPickupTickTask", "a", "Lcom/rs2/bot/tasks/EdgevilleDungeonBrassKeyBotTask;"): "task",
    ("com/rs2/bot/tasks/BrassKeyPickupTickTask", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/bot/tasks/BrassKeyDungeonEntryTickTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/bot/tasks/FaladorWineOfZamorakTelegrabBotTask", "aa", "Lcom/rs2/model/Position;"): "wineOfZamorakPosition",
    ("com/rs2/bot/tasks/FaladorWineOfZamorakTelegrabBotTask", "ab", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/FaladorWineOfZamorakTelegrabBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/WineOfZamorakTelegrabTickTask", "a", "Lcom/rs2/bot/tasks/FaladorWineOfZamorakTelegrabBotTask;"): "task",
    ("com/rs2/bot/tasks/WineOfZamorakTelegrabTickTask", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/bot/tasks/WizardsTowerLesserDemonMagicBotTask", "aa", "Lcom/rs2/model/Position;"): "routeStartPosition",
    ("com/rs2/bot/tasks/WizardsTowerLesserDemonMagicBotTask", "ab", "[I"): "ignoredLootItemIds",
    ("com/rs2/bot/tasks/WizardsTowerLesserDemonMagicBotTask", "ac", "[Lcom/rs2/bot/BotRoute;"): "taskRouteSegments",
    ("com/rs2/bot/tasks/EdgevilleTradeAdvertBotTask", "aa", "Lcom/rs2/model/Position;"): "tradeAdvertStartPosition",
    ("com/rs2/bot/tasks/EdgevilleTradeAdvertBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "unusedTradeAdvertRoute",
    ("com/rs2/bot/tasks/EdgevilleTradeAdvertBotTask", "ac", "[Lcom/rs2/util/RectangularArea;"): "tradeAdvertTaskAreas",
    ("com/rs2/bot/tasks/FaladorTradeAdvertBotTask", "aa", "Lcom/rs2/model/Position;"): "tradeAdvertStartPosition",
    ("com/rs2/bot/tasks/FaladorTradeAdvertBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "unusedTradeAdvertRoute",
    ("com/rs2/bot/tasks/FaladorTradeAdvertBotTask", "ac", "[Lcom/rs2/util/RectangularArea;"): "tradeAdvertTaskAreas",
    ("com/rs2/bot/tasks/SeersTradeAdvertBotTask", "aa", "Lcom/rs2/model/Position;"): "tradeAdvertStartPosition",
    ("com/rs2/bot/tasks/SeersTradeAdvertBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "unusedTradeAdvertRoute",
    ("com/rs2/bot/tasks/SeersTradeAdvertBotTask", "ac", "[Lcom/rs2/util/RectangularArea;"): "tradeAdvertTaskAreas",
    ("com/rs2/bot/tasks/VarrockTradeAdvertBotTask", "aa", "Lcom/rs2/model/Position;"): "tradeAdvertStartPosition",
    ("com/rs2/bot/tasks/VarrockTradeAdvertBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "unusedTradeAdvertRoute",
    ("com/rs2/bot/tasks/VarrockTradeAdvertBotTask", "ac", "[Lcom/rs2/util/RectangularArea;"): "tradeAdvertTaskAreas",
    ("com/rs2/bot/tasks/LeatherCraftingBotTask", "aa", "Lcom/rs2/model/Position;"): "leatherCraftingStartPosition",
    ("com/rs2/bot/combat/BotPvpCombatHandler", "a", "I"): "MELEE_COMBAT_STYLE",
    ("com/rs2/bot/combat/BotPvpCombatHandler", "b", "I"): "RANGED_COMBAT_STYLE",
    ("com/rs2/bot/combat/BotPvpCombatHandler", "c", "I"): "MAGIC_COMBAT_STYLE",
    ("com/rs2/bot/combat/BotCombatHelper", "a", "[Lcom/rs2/util/RectangularArea;"): "blockedPvpSearchAreas",
    ("com/rs2/bot/combat/BotCombatHelper", "b", "[Lcom/rs2/util/RectangularArea;"): "freeToPlayBlockedPvpSearchAreas",
    ("com/rs2/bot/combat/BotCombatHelper", "c", "[Lcom/rs2/util/RectangularArea;"): "hotzonePvpSearchAreas",
    ("com/rs2/ServerSettings", "a", "Z"): "mod2hsEnabled",
    ("com/rs2/ServerSettings", "b", "D"): "mod2hsAttackBonusRate",
    ("com/rs2/ServerSettings", "c", "D"): "mod2hsStrengthBonusRate",
    ("com/rs2/ServerSettings", "d", "Z"): "wildyBotsUseNewGeneration",
    ("com/rs2/ServerSettings", "e", "I"): "wildyBotsBaseCombatLevel",
    ("com/rs2/ServerSettings", "f", "I"): "wildyBotsCombatLevelSpread",
    ("com/rs2/ServerSettings", "g", "Z"): "wildyBotsIgnoreCombatForDeepWilderness",
    ("com/rs2/ServerSettings", "h", "Z"): "relogFrozenBotsEnabled",
    ("com/rs2/ServerSettings", "i", "Z"): "modernCombatSystemEnabled",
    ("com/rs2/ServerSettings", "j", "Z"): "mysqlHiscoresEnabled",
    ("com/rs2/ServerSettings", "m", "Z"): "broadcastCheatUsageEnabled",
    ("com/rs2/ServerSettings", "n", "I"): "defaultMovementSystem",
    ("com/rs2/ServerSettings", "o", "Ljava/lang/String;"): "serverVersion",
    ("com/rs2/ServerSettings", "p", "I"): "cacheVersion",
    ("com/rs2/ServerSettings", "q", "Z"): "skipUndefinedNpcSpawns",
    ("com/rs2/ServerSettings", "r", "I"): "maxLevel",
    ("com/rs2/ServerSettings", "s", "Z"): "capXpAtMaxLevel",
    ("com/rs2/ServerSettings", "t", "D"): "barrowsRewardRate",
    ("com/rs2/ServerSettings", "u", "I"): "randomEventsMode",
    ("com/rs2/ServerSettings", "v", "I"): "holidayDropSets",
    ("com/rs2/ServerSettings", "w", "I"): "holidayDropRespawnTicks",
    ("com/rs2/ServerSettings", "x", "Z"): "holidayItemDropsEnabled",
    ("com/rs2/ServerSettings", "y", "I"): "membershipDaysPerPurchase",
    ("com/rs2/ServerSettings", "z", "Z"): "content2007Enabled",
    ("com/rs2/ServerSettings", "A", "I"): "fightCaveStartWaveZeroBased",
    ("com/rs2/ServerSettings", "B", "I"): "rareDropChanceDivisor",
    ("com/rs2/ServerSettings", "C", "D"): "commonDropRateMultiplier",
    ("com/rs2/ServerSettings", "D", "D"): "uncommonDropRateMultiplier",
    ("com/rs2/ServerSettings", "E", "D"): "rareDropRateMultiplier",
    ("com/rs2/ServerSettings", "F", "D"): "veryRareDropRateMultiplier",
    ("com/rs2/ServerSettings", "G", "I"): "dropRateCap",
    ("com/rs2/ServerSettings", "H", "Z"): "customDropRatesEnabled",
    ("com/rs2/ServerSettings", "I", "Ljava/lang/String;"): "launcherJarPath",
    ("com/rs2/ServerSettings", "J", "Z"): "cacheVerificationShutdownPending",
    ("com/rs2/ServerSettings", "K", "I"): "progressiveXpMode",
    ("com/rs2/ServerSettings", "L", "I"): "groundItemLifetimeSeconds",
    ("com/rs2/ServerSettings", "M", "D"): "shopRestockTimeMultiplier",
    ("com/rs2/ServerSettings", "N", "D"): "shopItemMultiplier",
    ("com/rs2/ServerSettings", "O", "D"): "itemRespawnDelayMultiplier",
    ("com/rs2/ServerSettings", "P", "D"): "npcRespawnDelayMultiplier",
    ("com/rs2/ServerSettings", "R", "Z"): "showSkillUnlocks",
    ("com/rs2/ServerSettings", "S", "Z"): "dynamicShopPricesEnabled",
    ("com/rs2/ServerSettings", "U", "Z"): "skipRequirementsForMissingQuests",
    ("com/rs2/ServerSettings", "V", "Z"): "recolorMissingQuests",
    ("com/rs2/ServerSettings", "W", "I"): "completeMissingQuestsMode",
    ("com/rs2/ServerSettings", "X", "Z"): "clueMerchantEnabled",
    ("com/rs2/ServerSettings", "Y", "I"): "clueMerchantClueLevel",
    ("com/rs2/ServerSettings", "Z", "I"): "clueMerchantPriceCoins",
    ("com/rs2/ServerSettings", "ad", "Z"): "progressiveBotsPrioritizeExisting",
    ("com/rs2/ServerSettings", "ae", "Z"): "instantGrandExchangePriceFluctuationEnabled",
    ("com/rs2/ServerSettings", "af", "Z"): "instantGrandExchangeEnabled",
    ("com/rs2/ServerSettings", "ag", "Z"): "grandExchangeServerOffersEnabled",
    ("com/rs2/ServerSettings", "ah", "I"): "grandExchangeServerOfferCount",
    ("com/rs2/ServerSettings", "ai", "Z"): "hotzonesForWildyBotsEnabled",
    ("com/rs2/ServerSettings", "ak", "Z"): "backupCharactersEnabled",
    ("com/rs2/ServerSettings", "al", "Z"): "wildyBotsEnabled",
    ("com/rs2/bot/combat/WildernessBotSettings", "a", "I"): "wildyBotCount",
    (
        "com/rs2/bot/combat/WildernessBotSettings",
        "b",
        "Z",
    ): "escapeHighLevelAttackersEnabled",
    ("com/rs2/ServerSettings", "am", "Z"): "skillingBotsEnabled",
    ("com/rs2/ServerSettings", "an", "Z"): "tradeBotsEnabled",
    ("com/rs2/ServerSettings", "ao", "Z"): "otherBotsEnabled",
    ("com/rs2/ServerSettings", "ap", "Z"): "progressiveBotsEnabled",
    ("com/rs2/ServerSettings", "aq", "Z"): "wcStyleMiningEnabled",
    ("com/rs2/ServerSettings", "as", "Z"): "walkingBotsEnabled",
    ("com/rs2/ServerSettings", "at", "Z"): "lanConnectionsEnabled",
    ("com/rs2/ServerSettings", "au", "I"): "membershipRequirementMode",
    ("com/rs2/ServerSettings", "av", "I"): "membershipRequirementValue",
    ("com/rs2/ServerSettings", "aw", "Z"): "idleLogoutEnabled",
    ("com/rs2/ServerSettings", "ax", "I"): "otherBotCount",
    ("com/rs2/ServerSettings", "ay", "I"): "skillingBotCount",
    ("com/rs2/ServerSettings", "az", "I"): "tradeBotCount",
    ("com/rs2/ServerSettings", "aA", "I"): "progressiveBotCount",
    ("com/rs2/ServerSettings", "aB", "Z"): "freeToPlayWorld",
    ("com/rs2/ServerSettings", "aC", "Z"): "wildernessSlayerEnabled",
    ("com/rs2/ServerSettings", "aD", "I"): "botLoginIdLimit",
    ("com/rs2/ServerSettings", "aJ", "Z"): "grandExchangeEnabled",
    ("com/rs2/ServerSettings", "aK", "Z"): "tutorialSkipPromptEnabled",
    ("com/rs2/ServerSettings", "aM", "Ljava/lang/String;"): "serverName",
    ("com/rs2/ServerSettings", "aN", "Ljava/lang/String;"): "serverEmulatorName",
    ("com/rs2/ServerSettings", "aO", "Z"): "showServerEmulatorInWelcome",
    ("com/rs2/ServerSettings", "aP", "Ljava/lang/String;"): "mysqlDriverClass",
    ("com/rs2/ServerSettings", "aQ", "Ljava/lang/String;"): "mysqlJdbcUrl",
    ("com/rs2/ServerSettings", "aR", "Ljava/lang/String;"): "mysqlUsername",
    ("com/rs2/ServerSettings", "aS", "Ljava/lang/String;"): "mysqlPassword",
    ("com/rs2/ServerSettings", "aT", "Z"): "mysqlPlayerSaveEnabled",
    ("com/rs2/ServerSettings", "aU", "Z"): "debugModeEnabled",
    ("com/rs2/ServerSettings", "aV", "Z"): "developModeEnabled",
    ("com/rs2/ServerSettings", "aW", "Z"): "controlPanelHiscoresEnabled",
    ("com/rs2/ServerSettings", "aX", "Z"): "adminInteractionsAllowed",
    ("com/rs2/ServerSettings", "aZ", "Z"): "itemSpawningEnabled",
    ("com/rs2/ServerSettings", "ba", "Z"): "funPkEnabled",
    ("com/rs2/ServerSettings", "bb", "Z"): "pkWorldEnabled",
    ("com/rs2/ServerSettings", "bc", "I"): "loginRestrictionMode",
    ("com/rs2/ServerSettings", "bd", "Z"): "rsaEnabled",
    ("com/rs2/ServerSettings", "bf", "I"): "clientBuild",
    ("com/rs2/ServerSettings", "bg", "Ljava/lang/String;"): "rsaModulusString",
    ("com/rs2/ServerSettings", "bh", "Ljava/lang/String;"): "rsaPrivateExponentString",
    ("com/rs2/ServerSettings", "bi", "Ljava/math/BigInteger;"): "rsaModulus",
    ("com/rs2/ServerSettings", "bj", "Ljava/math/BigInteger;"): "rsaPrivateExponent",
    ("com/rs2/ServerSettings", "aY", "I"): "serverPort",
    ("com/rs2/ServerSettings", "bk", "D"): "xpRate",
    ("com/rs2/ServerSettings", "bl", "D"): "questXpRate",
    ("com/rs2/ServerSettings", "bm", "D"): "botXpRateMultiplier",
    ("com/rs2/ServerSettings", "bn", "I"): "botXpRateMode",
    ("com/rs2/ServerSettings", "bo", "I"): "startX",
    ("com/rs2/ServerSettings", "bp", "I"): "startY",
    ("com/rs2/ServerSettings", "bq", "I"): "startPlane",
    ("com/rs2/ServerSettings", "br", "I"): "respawnX",
    ("com/rs2/ServerSettings", "bs", "I"): "respawnY",
    ("com/rs2/ServerSettings", "bt", "I"): "respawnPlane",
    ("com/rs2/ServerSettings", "bu", "I"): "maxPlayers",
    ("com/rs2/ServerSettings", "bv", "[Ljava/lang/String;"): "bonusTypeNames",
    ("com/rs2/ServerSettings", "bw", "Z"): "diseasingEnabled",
    ("com/rs2/ServerSettings", "bx", "Z"): "agilityEnabled",
    ("com/rs2/ServerSettings", "by", "Z"): "cookingEnabled",
    ("com/rs2/ServerSettings", "bz", "Z"): "craftingEnabled",
    ("com/rs2/ServerSettings", "bA", "Z"): "farmingEnabled",
    ("com/rs2/ServerSettings", "bB", "Z"): "firemakingEnabled",
    ("com/rs2/ServerSettings", "bC", "Z"): "fishingEnabled",
    ("com/rs2/ServerSettings", "bD", "Z"): "fletchingEnabled",
    ("com/rs2/ServerSettings", "bE", "Z"): "herbloreEnabled",
    ("com/rs2/ServerSettings", "bF", "Z"): "miningEnabled",
    ("com/rs2/ServerSettings", "bG", "Z"): "prayerEnabled",
    ("com/rs2/ServerSettings", "bH", "Z"): "runecraftingEnabled",
    ("com/rs2/ServerSettings", "bI", "Z"): "slayerEnabled",
    ("com/rs2/ServerSettings", "bJ", "Z"): "smithingEnabled",
    ("com/rs2/ServerSettings", "bK", "Z"): "thievingEnabled",
    ("com/rs2/ServerSettings", "bL", "Z"): "woodcuttingEnabled",
    ("com/rs2/ServerSettings", "bM", "Z"): "duelingDisabled",
    ("com/rs2/ServerSettings", "bN", "[I"): "DEFAULT_WEAPON_MOVEMENT_ANIMATIONS",
    ("com/rs2/ServerSettings", "bO", "Lcom/rs2/model/combat/AttackStyleDefinition;"): "MAGIC_ATTACK_STYLE",
    ("com/rs2/ServerSettings", "bP", "Lcom/rs2/model/combat/AttackStyleDefinition;"): "DRAGONFIRE_ATTACK_STYLE",
    ("com/rs2/ServerSettings", "bQ", "Lcom/rs2/model/combat/AttackStyleDefinition;"): "SARADOMIN_SWORD_ATTACK_STYLE",
    ("com/rs2/ServerSettings", "bR", "[I"): "FUN_WEAPON_IDS",
    ("com/rs2/ServerSettings", "bS", "I"): "placeholderObjectId",
    ("com/rs2/ServerSettings", "bT", "[I"): "PACKET_LENGTHS",
    ("com/rs2/ServerSettings", "aa", "Z"): "clanWarsBotsEnabled",
    ("com/rs2/ServerSettings", "ab", "I"): "clanWarsTeamSize",
    ("com/rs2/ServerSettings", "ac", "I"): "clanWarsEventChanceDivisor",
    ("com/rs2/bot/ClanWarsBotManager", "a", "Z"): "clanWarsEventActive",
    ("com/rs2/bot/ClanWarsBotManager", "b", "Z"): "clanWarsRetreatActive",
    ("com/rs2/bot/ClanWarsBotManager", "c", "I"): "clanWarsBaseCombatLevel",
    ("com/rs2/bot/ClanWarsBotManager", "d", "I"): "clanWarsSupplyMultiplier",
    ("com/rs2/bot/ClanWarsBotManager", "e", "Ljava/lang/String;"): "clanWarsTeamOneTag",
    ("com/rs2/bot/ClanWarsBotManager", "f", "Ljava/lang/String;"): "clanWarsTeamTwoTag",
    ("com/rs2/bot/ClanWarsBotManager", "g", "Lcom/rs2/model/Position;"): "clanWarsRallyPosition",
    ("com/rs2/bot/ClanWarsBotManager", "h", "Ljava/util/ArrayList;"): "clanWarsTeamOneBots",
    ("com/rs2/bot/ClanWarsBotManager", "i", "Ljava/util/ArrayList;"): "clanWarsTeamTwoBots",
    ("com/rs2/bot/ClanWarsBotManager", "j", "I"): "clanWarsTeamOneCapeId",
    ("com/rs2/bot/ClanWarsBotManager", "k", "I"): "clanWarsTeamTwoCapeId",
    ("com/rs2/bot/ClanWarsBotManager", "l", "Ljava/util/List;"): "clanWarsTeamTagPool",
    ("com/rs2/bot/ClanWarsBotManager", "m", "[Lcom/rs2/util/RectangularArea;"): "clanWarsTeamSpawnAreas",
    ("com/rs2/bot/BotRoute", "a", "[Lcom/rs2/model/Position;"): "waypoints",
    ("com/rs2/bot/route/BotWorldRouteWalker", "b", "I"): "routeDistanceThreshold",
    ("com/rs2/model/player/Player", "aV", "I"): "savedWorldRouteIndex",
    ("com/rs2/model/player/Player", "aW", "Lcom/rs2/bot/route/BotWorldRouteChoice;"): "currentWorldRouteChoice",
    ("com/rs2/model/player/Player", "ic", "I"): "lastBotStallCheckX",
    ("com/rs2/model/player/Player", "id", "I"): "lastBotStallCheckY",
    ("com/rs2/model/player/Player", "ie", "I"): "lastBotStallCheckPlane",
    ("com/rs2/model/player/Player", "if", "J"): "lastBotStallCheckExperience",
    ("com/rs2/model/player/Player", "ig", "I"): "botStallSampleCount",
    ("com/rs2/model/player/Player", "iq", "I"): "botStallRecoveryAttempts",
    ("com/rs2/model/player/Player", "aX", "I"): "botTaskItemId",
    ("com/rs2/model/player/Player", "aY", "I"): "botSmithingProductItemId",
    ("com/rs2/model/player/Player", "aZ", "I"): "botShopItemAmount",
    ("com/rs2/model/player/Player", "ba", "Z"): "botUseTaskItemOnTarget",
    ("com/rs2/model/player/Player", "bb", "I"): "botShopBuyMode",
    ("com/rs2/model/player/Player", "bc", "I"): "botSkillTargetSkillId",
    ("com/rs2/model/player/Player", "bd", "I"): "botSkillTargetLevel",
    ("com/rs2/model/player/Player", "bi", "I"): "botCompletionItemId",
    ("com/rs2/model/player/Player", "bj", "I"): "botCompletionItemAmount",
    ("com/rs2/model/player/Player", "bo", "Z"): "savedWorldRouteReversed",
    ("com/rs2/model/player/Player", "bq", "I"): "currentBotTaskTypeId",
    ("com/rs2/model/player/Player", "br", "I"): "currentBotTaskIndex",
    ("com/rs2/model/player/Player", "bs", "I"): "deferredBotTaskTypeId",
    ("com/rs2/model/player/Player", "bt", "I"): "deferredBotTaskIndex",
    ("com/rs2/model/player/Player", "bu", "Lcom/rs2/bot/BotTaskDefinition;"): "deferredBotTask",
    ("com/rs2/model/player/Player", "bB", "Z"): "botLumbridgeResetPending",
    ("com/rs2/model/player/Player", "bv", "Ljava/util/ArrayList;"): "botCombatLoadoutItemIds",
    ("com/rs2/model/player/Player", "bw", "Ljava/util/ArrayList;"): "botMeleeLoadoutItemIds",
    ("com/rs2/model/player/Player", "bx", "Ljava/util/ArrayList;"): "botRangedLoadoutItemIds",
    ("com/rs2/model/player/Player", "by", "Ljava/util/ArrayList;"): "botMagicLoadoutItemIds",
    ("com/rs2/model/player/Player", "bz", "Ljava/util/ArrayList;"): "botShopSellItemIds",
    ("com/rs2/model/player/Player", "bA", "I"): "botCombatLoadoutSlotCursor",
    ("com/rs2/model/player/Player", "cz", "Z"): "botTaskReturnToBankRequested",
    ("com/rs2/model/player/Player", "cA", "[Lcom/rs2/model/item/ItemStack;"): "botTaskRequiredItems",
    ("com/rs2/model/player/Player", "cp", "Lcom/rs2/model/Entity;"): "botLootResumeTarget",
    ("com/rs2/model/player/Player", "cq", "Ljava/util/ArrayList;"): "botLootGroundItems",
    ("com/rs2/model/player/Player", "cr", "Ljava/util/ArrayList;"): "botLootPickupTargets",
    ("com/rs2/model/player/Player", "cs", "Ljava/util/ArrayList;"): "botLootSellGroundItems",
    ("com/rs2/model/player/Player", "ct", "Ljava/util/ArrayList;"): "botLootSellItems",
    ("com/rs2/model/player/Player", "cu", "Z"): "clanWarsBot",
    ("com/rs2/model/player/Player", "cv", "I"): "clanWarsTeamId",
    ("com/rs2/model/player/Player", "cw", "J"): "botTaskStartTimeMillis",
    ("com/rs2/model/player/Player", "cx", "I"): "botTaskDurationMinutes",
    ("com/rs2/model/player/Player", "cy", "J"): "botTaskSavedElapsedMillis",
    ("com/rs2/model/player/Player", "cB", "Lcom/rs2/model/Position;"): "botEscapeLastPosition",
    ("com/rs2/model/player/Player", "cC", "I"): "botEscapeStuckTicks",
    ("com/rs2/model/player/Player", "cD", "Z"): "botCombatEscapeActive",
    ("com/rs2/model/player/Player", "cE", "Z"): "botMagicPenaltyGearUnequipped",
    ("com/rs2/model/player/Player", "cF", "Z"): "botAntipoisonAvailable",
    ("com/rs2/model/player/Player", "cG", "I"): "botActiveCombatStyle",
    ("com/rs2/model/player/Player", "cH", "I"): "botPrimaryCombatStyle",
    ("com/rs2/model/player/Player", "cI", "I"): "botSpecialCombatStyle",
    ("com/rs2/model/player/Player", "cJ", "I"): "botOpponentCombatStyle",
    ("com/rs2/model/player/Player", "cK", "I"): "botCombatStyle",
    ("com/rs2/model/player/Player", "cL", "I"): "botSpecialAttackEnergyCost",
    ("com/rs2/model/player/Player", "cM", "Z"): "botStrengthPotionDepleted",
    ("com/rs2/model/player/Player", "cO", "Z"): "botFoodDepleted",
    ("com/rs2/model/player/Player", "cP", "I"): "botWeaponItemId",
    ("com/rs2/model/player/Player", "cQ", "I"): "botShieldItemId",
    ("com/rs2/model/player/Player", "cR", "I"): "botSpecialWeaponItemId",
    ("com/rs2/model/player/Player", "cS", "Lcom/rs2/model/skill/magic/SpellDefinition;"): "botCombatSpell",
    ("com/rs2/model/player/Player", "cT", "I"): "botFoodItemId",
    ("com/rs2/model/player/Player", "cU", "I"): "botWildernessMaxY",
    ("com/rs2/model/player/Player", "cV", "Ljava/lang/String;"): "botCombatState",
    ("com/rs2/model/player/Player", "cW", "Lcom/rs2/model/task/TickTask;"): "botEscapeLogoutTask",
    ("com/rs2/model/player/Player", "cX", "Lcom/rs2/model/task/TickTask;"): "botCombatTickTask",
    ("com/rs2/model/player/Player", "cY", "I"): "botMagicGearSwapDelayTicks",
    ("com/rs2/model/player/Player", "cZ", "I"): "botThreatEscapeDelayTicks",
    ("com/rs2/model/player/Player", "da", "I"): "botPrayerSwitchDelayTicks",
    ("com/rs2/model/player/Player", "db", "I"): "botQueuedPrayerId",
    ("com/rs2/model/player/Player", "dc", "I"): "botEatDelayTicks",
    ("com/rs2/model/player/Player", "dd", "I"): "botWeaponSwapDelayTicks",
    ("com/rs2/model/player/Player", "dg", "I"): "botPathSegmentIndex",
    ("com/rs2/model/player/Player", "bp", "I"): "botElementalSpellIndex",
    ("com/rs2/model/player/Player", "bC", "I"): "botPvpTeamInviteTicks",
    ("com/rs2/model/player/Player", "bD", "Lcom/rs2/model/player/Player;"): "botPvpPendingTeamTarget",
    ("com/rs2/model/player/Player", "bE", "Ljava/util/ArrayList;"): "botPvpRejectedTeamTargets",
    ("com/rs2/model/player/Player", "bF", "Ljava/util/ArrayList;"): "botPvpTeamRequesters",
    ("com/rs2/model/player/Player", "bG", "Lcom/rs2/model/player/Player;"): "botPvpChatSource",
    ("com/rs2/model/player/Player", "bH", "Ljava/lang/String;"): "botPvpChatMessage",
    ("com/rs2/model/player/Player", "dh", "Lcom/rs2/bot/BotTaskDefinition;"): "currentBotTask",
    ("com/rs2/model/player/Player", "di", "I"): "botTargetNpcId",
    ("com/rs2/model/player/Player", "dj", "I"): "botPathWaypointIndex",
    ("com/rs2/model/player/Player", "dk", "Ljava/lang/String;"): "botEscapeRouteName",
    ("com/rs2/model/player/Player", "dl", "Lcom/rs2/bot/BotRoute;"): "currentBotRoute",
    ("com/rs2/model/player/Player", "dm", "Z"): "botRouteActionPending",
    ("com/rs2/model/player/Player", "dn", "Z"): "botRouteTravelPending",
    ("com/rs2/model/player/Player", "do", "Ljava/util/ArrayList;"): "botInteractionTargetIds",
    ("com/rs2/model/player/Player", "dp", "Z"): "botEnabled",
    ("com/rs2/model/player/Player", "dq", "I"): "botInteractionOption",
    ("com/rs2/model/player/Player", "dr", "Ljava/lang/String;"): "botTaskState",
    ("com/rs2/model/player/Player", "aA", "Z"): "dropPartyLeader",
    ("com/rs2/model/player/Player", "aB", "Z"): "dropPartyPretaskComplete",
    ("com/rs2/model/player/Player", "aC", "Z"): "dropPartyFollower",
    ("com/rs2/model/player/Player", "aD", "Z"): "dropPartySentToAssignedDrop",
    ("com/rs2/model/player/Player", "aE", "Lcom/rs2/model/Position;"): "dropPartyAssignedDropPosition",
    ("com/rs2/model/player/Player", "aF", "I"): "dropPartyPretaskLoopCount",
    ("com/rs2/model/player/Player", "aG", "I"): "tradeAdvertAcceptedQuantity",
    ("com/rs2/model/player/Player", "aH", "I"): "tradeAdvertOfferPoolIndex",
    ("com/rs2/model/player/Player", "aI", "I"): "tradeAdvertQuantityOptionIndex",
    ("com/rs2/model/player/Player", "aJ", "Z"): "tradeAdvertInitialOfferPlaced",
    ("com/rs2/model/player/Player", "aK", "Z"): "tradeAdvertScam",
    ("com/rs2/model/player/Player", "aL", "Z"): "tradeAdvertVariableQuantity",
    ("com/rs2/model/player/Player", "aM", "I"): "tradeAdvertLastOfferAmount",
    ("com/rs2/model/player/Player", "aN", "I"): "tradeAdvertMode",
    ("com/rs2/model/player/Player", "aO", "I"): "botAdvertItemId",
    ("com/rs2/model/player/Player", "aP", "I"): "tradeAdvertQuantityRemaining",
    ("com/rs2/model/player/Player", "aQ", "I"): "tradeAdvertUnitPrice",
    ("com/rs2/model/player/Player", "aR", "Ljava/lang/String;"): "botPublicChatMessage",
    ("com/rs2/model/player/Player", "aS", "I"): "botPublicChatColor",
    ("com/rs2/model/player/Player", "aT", "I"): "botPublicChatEffect",
    ("com/rs2/model/player/Player", "aU", "Lcom/rs2/model/player/Player;"): "pendingTradeTarget",
    ("com/rs2/model/player/TradeState", "a", "Lcom/rs2/model/player/TradeState;"): "NONE",
    ("com/rs2/model/player/TradeState", "b", "Lcom/rs2/model/player/TradeState;"): "REQUEST_SENT",
    ("com/rs2/model/player/TradeState", "c", "Lcom/rs2/model/player/TradeState;"): "ACCEPTED",
    ("com/rs2/model/player/TradeState", "d", "Lcom/rs2/model/player/TradeState;"): "OFFER_SCREEN",
    ("com/rs2/model/player/TradeState", "e", "Lcom/rs2/model/player/TradeState;"): "CONFIRM_SCREEN",
    ("com/rs2/net/packet/handler/TradeRequestTask", "a", "Lcom/rs2/model/player/Player;"): "targetPlayer",
    ("com/rs2/net/packet/handler/TradeRequestTask", "b", "Lcom/rs2/model/player/Player;"): "requestingPlayer",
    ("com/rs2/net/packet/handler/TradeRequestTask", "c", "I"): "actionSequence",
    ("com/rs2/net/packet/handler/DeferredTradeRequestTask", "a", "Lcom/rs2/model/player/Player;"): "targetPlayer",
    ("com/rs2/net/packet/handler/DeferredTradeRequestTask", "b", "Lcom/rs2/model/player/Player;"): "requestingPlayer",
    ("com/rs2/net/packet/handler/DeferredTradeRequestTask", "c", "I"): "actionSequence",
    ("com/rs2/net/packet/handler/FollowPlayerTask", "a", "Lcom/rs2/model/player/Player;"): "targetPlayer",
    ("com/rs2/net/packet/handler/FollowPlayerTask", "b", "Lcom/rs2/model/player/Player;"): "requestingPlayer",
    ("com/rs2/net/packet/handler/FollowPlayerTask", "c", "I"): "actionSequence",
    ("com/rs2/net/packet/handler/DuelRequestTask", "a", "Lcom/rs2/model/player/Player;"): "targetPlayer",
    ("com/rs2/net/packet/handler/DuelRequestTask", "b", "Lcom/rs2/model/player/Player;"): "requestingPlayer",
    ("com/rs2/net/packet/handler/DuelRequestTask", "c", "I"): "actionSequence",
    ("com/rs2/net/packet/handler/ItemOnPlayerTask", "a", "Lcom/rs2/model/player/Player;"): "targetPlayer",
    ("com/rs2/net/packet/handler/ItemOnPlayerTask", "b", "Lcom/rs2/model/player/Player;"): "requestingPlayer",
    ("com/rs2/net/packet/handler/ItemOnPlayerTask", "c", "I"): "actionSequence",
    ("com/rs2/net/packet/handler/ItemOnPlayerTask", "d", "Lcom/rs2/model/item/ItemStack;"): "usedItem",
    ("com/rs2/net/packet/handler/ItemOnPlayerTask", "e", "I"): "inventorySlot",
    ("com/rs2/model/player/Player", "K", "Lcom/rs2/model/item/ItemStack;"): "pendingDialogueItem",
    ("com/rs2/model/player/Player", "L", "Lcom/rs2/model/player/Player;"): "pendingItemDropTarget",
    ("com/rs2/model/gameplay/duel/DuelController", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/gameplay/duel/DuelController", "b", "Z"): "accepted",
    ("com/rs2/model/gameplay/duel/DuelInterfaceManager", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/gameplay/duel/DuelInterfaceManager", "b", "I"): "ruleConfigValue",
    ("com/rs2/model/gameplay/duel/DuelInterfaceManager", "c", "[I"): "confirmRuleTextIds",
    ("com/rs2/model/gameplay/duel/DuelInterfaceManager", "d", "[I"): "ruleConfigMasks",
    ("com/rs2/model/gameplay/duel/DuelSession", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/gameplay/duel/DuelSession", "b", "Lcom/rs2/model/player/Player;"): "opponent",
    ("com/rs2/model/gameplay/duel/DuelSession", "c", "Z"): "started",
    ("com/rs2/model/gameplay/duel/DuelSession", "e", "Ljava/util/ArrayList;"): "equipmentToRemove",
    ("com/rs2/model/gameplay/duel/DuelSession", "f", "Ljava/util/ArrayList;"): "ruleDescriptions",
    ("com/rs2/model/gameplay/duel/DuelSession", "g", "[Z"): "enabledRules",
    ("com/rs2/model/gameplay/duel/DuelSession", "h", "Ljava/util/ArrayList;"): "stakedItems",
    ("com/rs2/model/gameplay/duel/DuelArenaLocationManager", "a", "[Lcom/rs2/model/gameplay/PositionRange;"): "obstacleArenaStartAreas",
    ("com/rs2/model/gameplay/duel/DuelArenaLocationManager", "b", "[Lcom/rs2/model/gameplay/PositionRange;"): "standardArenaStartAreas",
    ("com/rs2/model/gameplay/duel/DuelCountdownTask", "a", "I"): "countdownValue",
    ("com/rs2/model/gameplay/duel/DuelCountdownTask", "b", "Lcom/rs2/model/gameplay/duel/DuelSession;"): "session",
    ("com/rs2/model/gameplay/duel/DuelVictoryTask", "a", "Lcom/rs2/model/player/Player;"): "winner",
    ("com/rs2/model/gameplay/duel/DuelVictoryTask", "b", "Ljava/lang/String;"): "loserUsername",
    ("com/rs2/model/gameplay/duel/DuelVictoryTask", "c", "Ljava/lang/String;"): "loserCombatLevel",
    ("com/rs2/model/gameplay/duel/DuelVictoryTask", "d", "[Lcom/rs2/model/item/ItemStack;"): "rewardItems",
    ("com/rs2/model/gameplay/duel/DuelHistory", "a", "Ljava/util/ArrayList;"): "recentResults",
    ("com/rs2/model/gameplay/fightcave/FightCaveController", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/gameplay/fightcave/FightCaveController", "b", "Lcom/rs2/model/Position;"): "exitPosition",
    ("com/rs2/model/gameplay/fightcave/FightCaveCompletionTask", "a", "Lcom/rs2/model/gameplay/fightcave/FightCaveController;"): "controller",
    ("com/rs2/model/gameplay/fightcave/FightCaveWaveSpawner", "a", "[[I"): "waveNpcIds",
    ("com/rs2/model/gameplay/fightcave/FightCaveSpawnTable", "a", "[Lcom/rs2/model/gameplay/PositionRange;"): "spawnAreaRotation",
    ("com/rs2/model/gameplay/fightcave/FightCaveSpawnTable", "b", "Lcom/rs2/model/gameplay/PositionRange;"): "northWestSpawnArea",
    ("com/rs2/model/gameplay/fightcave/FightCaveSpawnTable", "c", "Lcom/rs2/model/gameplay/PositionRange;"): "centerSpawnArea",
    ("com/rs2/model/gameplay/fightcave/FightCaveSpawnTable", "d", "Lcom/rs2/model/gameplay/PositionRange;"): "eastSpawnArea",
    ("com/rs2/model/gameplay/fightcave/FightCaveSpawnTable", "e", "Lcom/rs2/model/gameplay/PositionRange;"): "southSpawnArea",
    ("com/rs2/model/gameplay/fightcave/FightCaveSpawnTable", "f", "Lcom/rs2/model/gameplay/PositionRange;"): "southWestSpawnArea",
    ("com/rs2/model/player/Player", "dO", "I"): "fightCaveWaveIndex",
    ("com/rs2/model/player/Player", "ez", "I"): "fightCaveSpawnRotation",
    ("com/rs2/model/player/Player", "in", "Ljava/util/ArrayList;"): "fightCaveNpcs",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "a", "I"): "ropeShortcutConfigId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "f", "I"): "armadylKillCountIndex",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "g", "I"): "bandosKillCountIndex",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "h", "I"): "saradominKillCountIndex",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "i", "I"): "zamorakKillCountIndex",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "j", "I"): "godswordShard1ItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "k", "I"): "godswordShard2ItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "l", "I"): "godswordShard3ItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "m", "I"): "godswordShard1And2ItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "n", "I"): "godswordShard1And3ItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "o", "I"): "godswordShard2And3ItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "p", "I"): "godswordBladeItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "q", "I"): "armadylGodswordItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "r", "I"): "bandosGodswordItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "s", "I"): "saradominGodswordItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "t", "I"): "zamorakGodswordItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "u", "I"): "armadylHiltItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "v", "I"): "bandosHiltItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "w", "I"): "saradominHiltItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "x", "I"): "zamorakHiltItemId",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "b", "Ljava/util/List;"): "armadylNpcIds",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "c", "Ljava/util/List;"): "bandosNpcIds",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "d", "Ljava/util/List;"): "saradominNpcIds",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "e", "Ljava/util/List;"): "zamorakNpcIds",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "y", "[Ljava/lang/String;"): "generalGraardorBattleCries",
    ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", "z", "[Ljava/lang/String;"): "commanderZilyanaBattleCries",
    ("com/rs2/model/gameplay/godwars/BandosStrongholdDoorTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/gameplay/duel/DuelRule", "a", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_RANGED",
    ("com/rs2/model/gameplay/duel/DuelRule", "b", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_MELEE",
    ("com/rs2/model/gameplay/duel/DuelRule", "c", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_MAGIC",
    ("com/rs2/model/gameplay/duel/DuelRule", "d", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_SPECIAL_ATTACK",
    ("com/rs2/model/gameplay/duel/DuelRule", "e", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "FUN_WEAPONS",
    ("com/rs2/model/gameplay/duel/DuelRule", "f", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_FORFEIT",
    ("com/rs2/model/gameplay/duel/DuelRule", "g", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_POTIONS",
    ("com/rs2/model/gameplay/duel/DuelRule", "h", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_FOOD",
    ("com/rs2/model/gameplay/duel/DuelRule", "i", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_PRAYER",
    ("com/rs2/model/gameplay/duel/DuelRule", "j", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_MOVEMENT",
    ("com/rs2/model/gameplay/duel/DuelRule", "k", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "OBSTACLES",
    ("com/rs2/model/gameplay/duel/DuelRule", "l", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_HELMET",
    ("com/rs2/model/gameplay/duel/DuelRule", "m", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_CAPE",
    ("com/rs2/model/gameplay/duel/DuelRule", "n", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_AMULET",
    ("com/rs2/model/gameplay/duel/DuelRule", "o", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_AMMO",
    ("com/rs2/model/gameplay/duel/DuelRule", "p", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_WEAPON",
    ("com/rs2/model/gameplay/duel/DuelRule", "q", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_BODY",
    ("com/rs2/model/gameplay/duel/DuelRule", "r", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_SHIELD",
    ("com/rs2/model/gameplay/duel/DuelRule", "s", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_LEGS",
    ("com/rs2/model/gameplay/duel/DuelRule", "t", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_GLOVES",
    ("com/rs2/model/gameplay/duel/DuelRule", "u", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_BOOTS",
    ("com/rs2/model/gameplay/duel/DuelRule", "v", "Lcom/rs2/model/gameplay/duel/DuelRule;"): "NO_RING",
    ("com/rs2/model/gameplay/duel/DuelRule", "w", "I"): "ruleIndex",
    ("com/rs2/model/gameplay/duel/DuelRule", "x", "I"): "primaryButtonId",
    ("com/rs2/model/gameplay/duel/DuelRule", "y", "I"): "secondaryButtonId",
    ("com/rs2/model/gameplay/duel/DuelRule", "z", "[Lcom/rs2/model/gameplay/duel/DuelRule;"): "VALUES",
    ("com/rs2/model/player/DelayedBotTradeRequestTask", "a", "Lcom/rs2/model/player/Player;"): "bot",
    ("com/rs2/model/player/DelayedBotTradeRequestTask", "b", "Ljava/lang/String;"): "triggerMessage",
    ("com/rs2/model/player/DelayedBotTradeRequestTask", "c", "Lcom/rs2/model/player/Player;"): "targetPlayer",
    ("com/rs2/model/player/DelayedBotLevelReplyTask", "a", "Lcom/rs2/model/player/Player;"): "bot",
    ("com/rs2/model/player/DelayedBotLevelReplyTask", "b", "Ljava/lang/String;"): "replyMessage",
    ("com/rs2/model/player/DelayedPositionMoveTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/player/DelayedPositionMoveTask", "b", "Lcom/rs2/model/Position;"): "destination",
    ("com/rs2/model/player/BotBankContinuationTask", "a", "Lcom/rs2/model/player/Player;"): "bot",
    ("com/rs2/model/player/BotLumbridgeResetTask", "a", "Lcom/rs2/model/player/Player;"): "resetPlayer",
    ("com/rs2/model/player/BotLumbridgeResetTask", "b", "Lcom/rs2/model/player/Player;"): "bot",
    ("com/rs2/model/player/PostTeleportBotContinuationTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/player/PostTeleportBotContinuationTask", "b", "Z"): "wasActionLocked",
    ("com/rs2/model/player/PostTeleportBotContinuationTask", "c", "Lcom/rs2/model/player/Player;"): "continuationPlayer",
    ("com/rs2/model/player/DropGodCapeTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/player/DropGodCapeTask", "b", "I"): "capeAnimationId",
    ("com/rs2/bot/BotPlayer", "eJ", "I"): "createdBotCount",
    ("com/rs2/bot/BotPlayer", "a", "Ljava/util/ArrayList;"): "dropPartyBots",
    ("com/rs2/bot/BotPlayer", "b", "Ljava/util/ArrayList;"): "defaultProgressiveBotNames",
    ("com/rs2/bot/BotPlayer", "eK", "Ljava/util/ArrayList;"): "shuffledBotNamePool",
    ("com/rs2/bot/BotPlayer", "c", "Ljava/util/ArrayList;"): "removedBotNames",
    ("com/rs2/bot/BotPlayer", "d", "Ljava/util/ArrayList;"): "forceResetBotNames",
    ("com/rs2/bot/BotPlayer", "eL", "Ljava/util/ArrayList;"): "progressiveBotNameQueue",
    ("com/rs2/bot/BotPlayer", "e", "Ljava/util/ArrayList;"): "botNamePool",
    ("com/rs2/bot/BotPlayer", "cu", "Z"): "clanWarsBot",
    ("com/rs2/bot/BotPlayer", "cv", "I"): "clanWarsTeamId",
    ("com/rs2/bot/BotPlayer", "dp", "Z"): "botEnabled",
    ("com/rs2/bot/BotTaskSelectionTask", "a", "Lcom/rs2/bot/BotPlayer;"): "bot",
    ("com/rs2/bot/BotTaskPlanningTask", "a", "Lcom/rs2/bot/BotPlayer;"): "bot",
    ("com/rs2/bot/BotCombatLoadoutTask", "a", "Lcom/rs2/bot/BotPlayer;"): "bot",
    ("com/rs2/bot/BotCombatTickTask", "a", "Lcom/rs2/bot/BotTaskDefinition;"): "taskDefinition",
    ("com/rs2/bot/BotCombatTickTask", "b", "Lcom/rs2/model/npc/Npc;"): "targetNpc",
    ("com/rs2/bot/BotCombatTickTask", "c", "Lcom/rs2/model/player/Player;"): "bot",
    ("com/rs2/model/GameplayHelper", "b", "I"): "tradeAdvertItemId",
    ("com/rs2/model/GameplayHelper", "c", "[I"): "tradeAdvertQuantityOptions",
    ("com/rs2/bot/BotTradeAdvertManager", "a", "Ljava/util/ArrayList;"): "tradeAdvertOfferPool",
    ("com/rs2/bot/BotTradeAdvertManager", "b", "Ljava/util/ArrayList;"): "commonTradeAdvertOfferPool",
    ("com/rs2/bot/BotTradeAdvertManager", "e", "Ljava/util/ArrayList;"): "scamTradeAdvertOfferPool",
    ("com/rs2/bot/BotTradeAdvertManager", "f", "Ljava/util/ArrayList;"): "combatTradeAdvertOfferPool",
    ("com/rs2/bot/BotTradeAdvertManager", "c", "I"): "scammerChanceDivisor",
    ("com/rs2/bot/BotTradeAdvertManager", "d", "I"): "commonItemChancePercent",
    ("com/rs2/bot/BotTradeAdvertManager", "g", "[Lcom/rs2/model/GameplayHelper;"): "configuredTradeAdvertOffers",
    ("com/rs2/bot/BotTradeAdvertStartTask", "a", "Lcom/rs2/bot/BotPlayer;"): "bot",
    ("com/rs2/bot/trade/BotTradeOfferTickTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/bot/trade/BotTradeCoinShortageResetTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/bot/trade/BotTradeItemShortageResetTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/bot/DropPartyBotHideTask", "a", "Lcom/rs2/bot/BotPlayer;"): "botToHide",
    ("com/rs2/bot/ClanWarsBotHideTask", "a", "Lcom/rs2/bot/BotPlayer;"): "botToHide",
    ("com/rs2/bot/ClanWarsBotCombatTickTask", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/bot/DropPartyBotJoinTask", "a", "Lcom/rs2/bot/BotTaskDefinition;"): "dropPartyTask",
    ("com/rs2/bot/DropPartyCompletionTask", "a", "Lcom/rs2/model/player/Player;"): "participant",
    ("com/rs2/bot/DropPartyLeaderCleanupTask", "a", "Lcom/rs2/model/player/Player;"): "leader",
    ("com/rs2/bot/DropPartyFollowerTickTask", "a", "Lcom/rs2/model/player/Player;"): "follower",
    ("com/rs2/bot/DropPartyFollowerTickTask", "b", "Lcom/rs2/model/player/Player;"): "leader",
    ("com/rs2/bot/DropPartyLeaderTickTask", "a", "Lcom/rs2/model/player/Player;"): "leader",
    ("com/rs2/bot/DropPartyGroundItemPickupTask", "a", "Lcom/rs2/model/player/Player;"): "participant",
    ("com/rs2/bot/DropPartyGroundItemPickupTask", "b", "Lcom/rs2/model/ground/GroundItem;"): "groundItem",
    ("com/rs2/bot/tasks/VarrockDropPartyBotTask", "aa", "Lcom/rs2/model/Position;"): "configuredStartPosition",
    ("com/rs2/bot/tasks/VarrockDropPartyBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "configuredPretaskRoute",
    ("com/rs2/bot/tasks/VarrockDropPartyBotTask", "ac", "Lcom/rs2/bot/BotRoute;"): "configuredTaskRoute",
    ("com/rs2/bot/tasks/VarrockDropPartyBotTask", "ad", "[Lcom/rs2/util/RectangularArea;"): "configuredTaskAreas",
    ("com/rs2/bot/tasks/FaladorDropPartyBotTask", "aa", "Lcom/rs2/model/Position;"): "configuredStartPosition",
    ("com/rs2/bot/tasks/FaladorDropPartyBotTask", "ab", "Lcom/rs2/bot/BotRoute;"): "configuredPretaskRoute",
    ("com/rs2/bot/tasks/FaladorDropPartyBotTask", "ac", "Lcom/rs2/bot/BotRoute;"): "configuredTaskRoute",
    ("com/rs2/bot/tasks/FaladorDropPartyBotTask", "ad", "[Lcom/rs2/util/RectangularArea;"): "configuredTaskAreas",
    ("com/rs2/model/player/RetryMissingNpcSearchTask", "c", "Ljava/util/ArrayList;"): "targetIds",
    ("com/rs2/model/player/RetryMissingObjectSearchTask", "c", "Ljava/util/ArrayList;"): "targetIds",
    ("com/rs2/model/player/RetryUnreachableObjectTask", "c", "Ljava/util/ArrayList;"): "targetIds",
    ("com/rs2/bot/DropPartyBotManager", "h", "I"): "leaderDropItemCount",
    ("com/rs2/bot/DropPartyBotManager", "a", "Z"): "dropPartyActive",
    ("com/rs2/bot/DropPartyBotManager", "b", "I"): "dropPartyChanceDivisor",
    ("com/rs2/bot/DropPartyBotManager", "i", "I"): "valuableDropMinValue",
    ("com/rs2/bot/DropPartyBotManager", "c", "Ljava/util/ArrayList;"): "pendingDropPartyGroundItems",
    ("com/rs2/bot/DropPartyBotManager", "d", "Ljava/util/ArrayList;"): "dropPartyParticipants",
    ("com/rs2/bot/DropPartyBotManager", "e", "I"): "targetDropPartySize",
    ("com/rs2/bot/DropPartyBotManager", "f", "I"): "baseDropPartySize",
    ("com/rs2/bot/DropPartyBotManager", "g", "I"): "dropPartyPretaskLoopLimit",
    ("com/rs2/bot/DropPartyBotManager", "j", "Ljava/util/ArrayList;"): "dropPartyRewardPool",
    ("com/rs2/bot/DropPartyBotManager", "k", "Ljava/util/ArrayList;"): "valuableDropPartyRewardPool",
    ("com/rs2/bot/route/BotWorldRoute", "a", "Lcom/rs2/bot/route/BotWorldRoute;"): "LUMBW_TO_DRAYNOR_CROSSROAD",
    ("com/rs2/bot/route/BotWorldRoute", "b", "Lcom/rs2/bot/route/BotWorldRoute;"): "DRAYNOR_CROSSROAD_TO_DRAYNOR",
    ("com/rs2/bot/route/BotWorldRoute", "c", "Lcom/rs2/bot/route/BotWorldRoute;"): "DRAYNOR_CROSSROAD_TO_BARB_VILLAGE",
    ("com/rs2/bot/route/BotWorldRoute", "d", "Lcom/rs2/bot/route/BotWorldRoute;"): "BARB_VILLAGE_TO_VARROCK_WBANK",
    ("com/rs2/bot/route/BotWorldRoute", "e", "Lcom/rs2/bot/route/BotWorldRoute;"): "BARB_VILLAGE_TO_EDGE",
    ("com/rs2/bot/route/BotWorldRoute", "f", "Lcom/rs2/bot/route/BotWorldRoute;"): "BARB_VILLAGE_TO_FALA_CENTER",
    ("com/rs2/bot/route/BotWorldRoute", "g", "Lcom/rs2/bot/route/BotWorldRoute;"): "FALA_CENTER_TO_WBANK",
    ("com/rs2/bot/route/BotWorldRoute", "h", "Lcom/rs2/bot/route/BotWorldRoute;"): "FALA_CENTER_TO_EBANK",
    ("com/rs2/bot/route/BotWorldRoute", "i", "Lcom/rs2/bot/route/BotWorldRoute;"): "DRAYNOR_CROSSROAD_TO_FALA_EBANK",
    ("com/rs2/bot/route/BotWorldRoute", "j", "Lcom/rs2/bot/route/BotWorldRoute;"): "ALKHARID_TO_VARROCK_EBANK",
    ("com/rs2/bot/route/BotWorldRoute", "k", "Lcom/rs2/bot/route/BotWorldRoute;"): "VARROCK_CENTER_TO_EBANK",
    ("com/rs2/bot/route/BotWorldRoute", "l", "Lcom/rs2/bot/route/BotWorldRoute;"): "VARROCK_CENTER_TO_WBANK",
    ("com/rs2/bot/route/BotWorldRoute", "m", "Lcom/rs2/bot/route/BotWorldRoute;"): "LUMBE_TO_VARROCK_CENTER",
    ("com/rs2/bot/route/BotWorldRoute", "n", "Lcom/rs2/bot/route/BotWorldRoute;"): "LUMBE_TO_ALKHARID",
    ("com/rs2/bot/route/BotWorldRoute", "o", "Lcom/rs2/bot/route/BotWorldRoute;"): "LUMBW_TO_LUMBE",
    ("com/rs2/bot/route/BotWorldRoute", "p", "Lcom/rs2/bot/route/BotWorldRoute;"): "FALA_CENTER_TO_TAVERLEY",
    ("com/rs2/bot/route/BotWorldRoute", "q", "Lcom/rs2/bot/route/BotWorldRoute;"): "TAVERLEY_TO_CATHERBY",
    ("com/rs2/bot/route/BotWorldRoute", "r", "Lcom/rs2/bot/route/BotWorldRoute;"): "CATHERBY_TO_CAMELOT",
    ("com/rs2/bot/route/BotWorldRoute", "s", "Lcom/rs2/bot/route/BotWorldRoute;"): "CAMELOT_TO_SEERS_VILLAGE",
    ("com/rs2/bot/route/BotWorldRoute", "t", "Lcom/rs2/bot/route/BotWorldRoute;"): "SEERS_VILLAGE_TO_ARDYN",
    ("com/rs2/bot/route/BotWorldRoute", "u", "Lcom/rs2/bot/route/BotWorldRoute;"): "ARDYN_TO_NBANK",
    ("com/rs2/bot/route/BotWorldRoute", "v", "Lcom/rs2/bot/route/BotWorldRoute;"): "ARDYN_TO_ARDY_CENTER",
    ("com/rs2/bot/route/BotWorldRoute", "w", "Lcom/rs2/bot/route/BotWorldRoute;"): "ARDY_CENTER_TO_SBANK",
    ("com/rs2/bot/route/BotWorldRoute", "x", "I"): "routeNpcId",
    ("com/rs2/bot/route/BotWorldRoute", "y", "Lcom/rs2/bot/BotRoute;"): "route",
    ("com/rs2/bot/route/BotWorldRoute", "z", "[Lcom/rs2/bot/BotRoute;"): "segments",
    ("com/rs2/bot/route/BotWorldRoute", "A", "[Lcom/rs2/bot/route/BotWorldRoute;"): "VALUES",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "a", "Lcom/rs2/model/skill/slayer/SlayerMasterDefinition;"): "BURTHORPE",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "b", "Lcom/rs2/model/skill/slayer/SlayerMasterDefinition;"): "CANIFIS",
    (
        "com/rs2/model/skill/slayer/SlayerMasterDefinition",
        "c",
        "Lcom/rs2/model/skill/slayer/SlayerMasterDefinition;",
    ): "EDGEVILLE_DUNGEON",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "d", "Lcom/rs2/model/skill/slayer/SlayerMasterDefinition;"): "ZANARIS",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "e", "Lcom/rs2/model/skill/slayer/SlayerMasterDefinition;"): "EDGEVILLE",
    (
        "com/rs2/model/skill/slayer/SlayerMasterDefinition",
        "f",
        "Lcom/rs2/model/skill/slayer/SlayerMasterDefinition;",
    ): "SHILO_VILLAGE",
    ("com/rs2/model/skill/slayer/SlayerAssignmentDefinition", "a", "Ljava/lang/String;"): "taskName",
    ("com/rs2/model/skill/slayer/SlayerAssignmentDefinition", "b", "I"): "minAmount",
    ("com/rs2/model/skill/slayer/SlayerAssignmentDefinition", "c", "I"): "maxAmount",
    ("com/rs2/model/skill/slayer/SlayerAssignmentDefinition", "d", "I"): "weight",
    (
        "com/rs2/model/skill/slayer/SlayerAssignmentDefinition",
        "e",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentRequirement;",
    ): "requirements",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "a", "I"): "SLAYER_LEVEL",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "b", "I"): "COMBAT_LEVEL",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "c", "I"): "QUEST_STATE",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "d", "I"): "DEFENCE_LEVEL",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "e", "I"): "AGILITY_LEVEL",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "f", "I"): "FIREMAKING_LEVEL",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "g", "I"): "requirementType",
    ("com/rs2/model/skill/slayer/SlayerAssignmentRequirement", "h", "I"): "requiredValue",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "g", "I"): "npcId",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "h", "I"): "requiredCombatLevel",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "i", "Ljava/lang/String;"): "locationName",
    (
        "com/rs2/model/skill/slayer/SlayerMasterDefinition",
        "j",
        "[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ): "assignments",
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "k", "Ljava/util/Map;"): "definitionsByNpcId",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "a", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "CRAWLING_HAND",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "b", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "CAVE_BUG",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "c", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "CAVE_CRAWLER",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "d", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "BANSHEE",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "e", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "CAVE_SLIME",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "f", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "ROCKSLUG",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "g", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "LIZARD",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "h", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "COCKATRICE",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "i", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "PYREFIEND",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "j", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "MOGRE",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "k",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ): "HARPIE_BUG_SWARM",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "l", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "WALL_BEAST",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "m", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "KILLERWATT",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "n", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "BASILISK",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "o", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "FEVER_SPIDER",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "p", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "INFERNAL_MAGE",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "q", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "BLOODVELD",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "r", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "JELLY",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "s", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "TUROTH",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "t", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "ZYGOMITE",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "u",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ): "ABERRANT_SPECTER",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "v", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "DUST_DEVIL",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "w", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "KURASK",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "x",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ): "SKELETAL_WYVERN",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "y", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "GARGOYLE",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "z", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "NECHRYAEL",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "A",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ): "ABYSSAL_DEMON",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "B",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ): "SPIRITUAL_RANGER",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "C",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ): "SPIRITUAL_WARRIOR",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "D",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ): "SPIRITUAL_MAGE",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "E", "Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;"): "DARK_BEAST",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "F", "Ljava/lang/String;"): "monsterName",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "G", "I"): "requiredSlayerLevel",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "H", "[I"): "requiredItemIds",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "I", "Ljava/lang/String;"): "requirementMode",
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "J", "Ljava/util/Map;"): "definitionsByName",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "a", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "BANSHEE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "b", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "BASILISK",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "c", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "BAT",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "d", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "BLOODVELD",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "e", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "BLUE_DRAGON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "f", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "BRONZE_DRAGON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "g", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "CAVE_BUG",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "h", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "CAVE_CRAWLER",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "i", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "CAVE_SLIME",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "j", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "COCKATRICE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "k", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "CRAWLING_HAND",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "l", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "DAGANNOTH",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "m", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "LIZARD",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "n", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "DOG",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "o", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "GHOUL",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "p", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "GREEN_DRAGON",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterGuide",
        "q",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;",
    ): "EARTH_WARRIOR",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "r", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "ELF",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "s", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "HELLHOUND",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "t", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "HOBGOBLIN",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "u", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "ICE_GIANT",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "v", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "INFERNAL_MAGE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "w", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "JELLY",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "x", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "KALPHITE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "y", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "LESSER_DEMON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "z", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "MOSS_GIANT",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "A", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "MOGRE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "B", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "OGRE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "C", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "PYREFIEND",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "D", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "ROCKSLUG",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterGuide",
        "E",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;",
    ): "SHADOW_WARRIOR",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "F", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "SKELETON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "G", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "TROLL",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "H", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "VAMPIRE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "I", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "WEREWOLF",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterGuide",
        "J",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;",
    ): "ABERRANT_SPECTER",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "K", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "FIRE_GIANT",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "L", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "CROCODILE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "M", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "DUST_DEVIL",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "N", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "GOBLIN",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "O", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "WALL_BEAST",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "P", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "TUROTH",
    (
        "com/rs2/model/skill/slayer/SlayerMonsterGuide",
        "Q",
        "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;",
    ): "ABYSSAL_DEMON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "R", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "KURASK",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "S", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "GREATER_DEMON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "T", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "IRON_DRAGON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "U", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "BLACK_DEMON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "V", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "STEEL_DRAGON",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "W", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "GARGOYLE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "X", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "SHADE",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "Y", "Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;"): "NECHRYAEL",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "Z", "Ljava/lang/String;"): "monsterName",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "aa", "[Ljava/lang/String;"): "guideTextLines",
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "ab", "Ljava/util/Map;"): "guidesByMonsterName",
    ("com/rs2/model/player/SocialManager", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/model/player/SocialManager", "b", "I"): "privateMessageCounter",
    ("com/rs2/util/ChatTextCodec", "a", "[C"): "CHAR_TABLE",
    ("com/rs2/util/TextUtil", "a", "[C"): "NAME_HASH_CHAR_TABLE",
    ("com/rs2/util/ByteArrayReader", "a", "I"): "position",
    ("com/rs2/util/ByteArrayReader", "b", "[B"): "buffer",
    ("com/rs2/util/path/MapDataReader", "a", "[B"): "data",
    ("com/rs2/util/path/MapDataReader", "b", "I"): "offset",
    ("com/rs2/util/LinkedNodeList", "a", "Lcom/rs2/util/LinkedNode;"): "sentinel",
    ("com/rs2/model/reward/ActionRewardDefinition", "a", "I"): "quantity",
    ("com/rs2/model/reward/ActionRewardDefinition", "b", "I"): "targetId",
    ("com/rs2/util/CountingDataOutputStream", "a", "I"): "bytesWritten",
    ("com/rs2/util/CountingDataOutputStream", "b", "[B"): "scratch",
    ("com/rs2/util/Vector2f", "a", "F"): "x",
    ("com/rs2/util/Vector2f", "b", "F"): "y",
    ("com/rs2/util/RectangularArea", "a", "I"): "minX",
    ("com/rs2/util/RectangularArea", "b", "I"): "minY",
    ("com/rs2/util/RectangularArea", "c", "I"): "maxX",
    ("com/rs2/util/RectangularArea", "d", "I"): "maxY",
    ("com/rs2/util/RectangularArea", "e", "B"): "plane",
    ("com/rs2/util/TimestampedPrintStream", "a", "Ljava/io/BufferedWriter;"): "logWriter",
    ("com/rs2/util/TimestampedPrintStream", "b", "Ljava/text/DateFormat;"): "dateFormat",
    ("com/rs2/util/DelayedShutdownTask", "a", "J"): "delayMillis",
    ("com/rs2/util/db/DatabaseQuery", "a", "Ljava/lang/String;"): "sql",
    ("com/rs2/util/db/DatabaseQuery", "b", "Ljava/lang/ThreadLocal;"): "threadContextLocal",
    ("com/rs2/util/db/DatabaseQuery", "c", "Lcom/rs2/util/db/DatabaseCallback;"): "callback",
    ("com/rs2/util/db/DatabaseQuery", "d", "Lcom/rs2/util/db/DatabaseService;"): "service",
    ("com/rs2/util/db/DatabaseService", "a", "Lcom/rs2/util/db/DatabaseService;"): "instance",
    ("com/rs2/util/db/DatabaseService", "b", "Ljava/util/concurrent/ExecutorService;"): "executor",
    ("com/rs2/util/db/DatabaseService", "c", "Ljava/lang/ThreadLocal;"): "threadContextLocal",
    ("com/rs2/util/db/DatabaseService", "d", "Ljava/lang/String;"): "jdbcUrl",
    ("com/rs2/util/db/DatabaseService", "e", "Ljava/lang/String;"): "username",
    ("com/rs2/util/db/DatabaseService", "f", "Ljava/lang/String;"): "password",
    ("com/rs2/util/db/DatabaseThreadContext", "a", "Ljava/sql/Connection;"): "connection",
    ("com/rs2/util/db/DatabaseThreadContext", "b", "Ljava/util/Map;"): "preparedStatements",
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "a", "Ljava/lang/String;"): "CONTACTS_SAVE_SQL",
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "b", "Ljava/util/Map;"): "containerSaveSqlByContainerId",
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "c", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/db/player/PlayerContainerLoadQuery", "a", "Lcom/rs2/util/db/player/PlayerLoadQueryFactory;"): "factory",
    ("com/rs2/util/db/player/PlayerContainerLoadQuery", "b", "I"): "containerId",
    ("com/rs2/util/db/player/PlayerContainerSaveQuery", "a", "Lcom/rs2/model/item/ItemContainer;"): "container",
    ("com/rs2/util/db/player/PlayerContainerSaveQuery", "b", "I"): "containerId",
    ("com/rs2/util/db/player/PlayerContainerSaveQuery", "c", "Lcom/rs2/util/db/player/PlayerSaveQueryFactory;"): "factory",
    ("com/rs2/util/db/player/PlayerContactsSaveQuery", "a", "Lcom/rs2/util/db/player/PlayerSaveQueryFactory;"): "factory",
    ("com/rs2/util/db/player/PlayerProfileSaveQuery", "a", "Lcom/rs2/util/db/player/PlayerSaveQueryFactory;"): "factory",
    ("com/rs2/util/db/player/PlayerSkillsSaveQuery", "a", "Lcom/rs2/util/db/player/PlayerSaveQueryFactory;"): "factory",
    ("com/rs2/util/db/player/PlayerContactsLoadCallback", "a", "Lcom/rs2/util/db/player/PlayerLoadQueryFactory;"): "factory",
    ("com/rs2/util/db/player/PlayerContainerLoadCallback", "a", "Lcom/rs2/model/item/ItemContainer;"): "container",
    ("com/rs2/util/db/player/PlayerProfileLoadCallback", "a", "Lcom/rs2/util/db/player/PlayerLoadQueryFactory;"): "factory",
    ("com/rs2/util/db/player/PlayerSkillsLoadCallback", "a", "Lcom/rs2/util/db/player/PlayerLoadQueryFactory;"): "factory",
    ("com/rs2/util/PlayerContactsLoadQuery", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/PlayerProfileLoadQuery", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/PlayerSkillsLoadQuery", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/PlayerWorldUpdateQuery", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/PlayerUidLookupQuery", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/PlayerLoginLoadCallback", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/ConverterUidLookupQuery", "a", "Ljava/lang/String;"): "username",
    ("com/rs2/util/ConverterUidLookupCallback", "a", "Ljava/io/File;"): "characterDirectory",
    ("com/rs2/util/ConverterUidLookupCallback", "b", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/util/ConverterUidLookupCallback", "c", "Ljava/lang/String;"): "username",
    ("com/rs2/util/Converter", "a", "I"): "convertedCount",
    ("com/rs2/util/Converter", "b", "Z"): "readyForNextFile",
    ("com/rs2/util/CharacterFileManager", "a", "[I"): "musicUnlockConfigIds",
    ("com/rs2/util/CharacterFileManager", "b", "Ljava/util/ArrayList;"): "deadHardcoreIronmanRecords",
    ("com/rs2/util/CharacterFileManager", "c", "Ljava/util/ArrayList;"): "liveHiscoreRecords",
    ("com/rs2/util/CharacterFileManager", "d", "Ljava/util/concurrent/ConcurrentHashMap;"): "loginIpReservations",
    ("com/rs2/util/LoginIpReservation", "a", "Lcom/rs2/util/ElapsedTimer;"): "timer",
    ("com/rs2/util/ElapsedTimer", "a", "J"): "startTimeMillis",
    ("com/rs2/util/ProfilerTimer", "a", "J"): "startTimeMillis",
    ("com/rs2/util/ProfilerTimer", "b", "J"): "accumulatedMillis",
    ("com/rs2/util/ProfilerRegistry", "a", "Ljava/util/Map;"): "timers",
    ("com/rs2/net/packet/handler/AppearancePacketHandler", "a", "[I"): "DEFAULT_MALE_BODY_PARTS",
    ("com/rs2/net/packet/AccessMode", "a", "Lcom/rs2/net/packet/AccessMode;"): "BYTE_ACCESS",
    ("com/rs2/net/packet/AccessMode", "b", "Lcom/rs2/net/packet/AccessMode;"): "BIT_ACCESS",
    ("com/rs2/net/packet/ByteOrder", "a", "Lcom/rs2/net/packet/ByteOrder;"): "LITTLE",
    ("com/rs2/net/packet/ByteOrder", "b", "Lcom/rs2/net/packet/ByteOrder;"): "BIG",
    ("com/rs2/net/packet/ByteOrder", "c", "Lcom/rs2/net/packet/ByteOrder;"): "MIDDLE",
    ("com/rs2/net/packet/ByteOrder", "d", "Lcom/rs2/net/packet/ByteOrder;"): "INVERSE_MIDDLE",
    ("com/rs2/net/packet/ByteTransform", "a", "Lcom/rs2/net/packet/ByteTransform;"): "NONE",
    ("com/rs2/net/packet/ByteTransform", "b", "Lcom/rs2/net/packet/ByteTransform;"): "ADD",
    ("com/rs2/net/packet/ByteTransform", "c", "Lcom/rs2/net/packet/ByteTransform;"): "NEGATE",
    ("com/rs2/net/packet/ByteTransform", "d", "Lcom/rs2/net/packet/ByteTransform;"): "SUBTRACT",
    ("com/rs2/net/packet/PacketBuffer", "a", "[I"): "BIT_MASKS",
    ("com/rs2/net/packet/PacketWriter", "a", "[I"): "BIT_MASKS",
    ("com/rs2/net/packet/PacketBuffer", "b", "Lcom/rs2/net/packet/AccessMode;"): "accessMode",
    ("com/rs2/net/packet/PacketBuffer", "c", "I"): "bitPosition",
    ("com/rs2/net/packet/PacketSender", "a", "Lcom/rs2/model/player/Player;"): "player",
    ("com/rs2/net/packet/handler/SkillMenuPacketHandler", "a", "[I"): "skillButtonIds",
    ("com/rs2/net/packet/handler/SkillMenuPacketHandler", "b", "[I"): "skillIdByButtonIndex",
    ("com/rs2/net/packet/PacketReader", "b", "Ljava/nio/ByteBuffer;"): "buffer",
    ("com/rs2/net/packet/PacketWriter", "b", "Ljava/nio/ByteBuffer;"): "buffer",
    ("com/rs2/net/packet/PacketWriter", "c", "I"): "lengthPlaceholderPosition",
    ("com/rs2/net/packet/DelayedUnlockEvent", "a", "Lcom/rs2/net/packet/PacketSender;"): "packetSender",
    ("com/rs2/net/packet/DelayedAnimationEvent", "a", "Lcom/rs2/net/packet/PacketSender;"): "packetSender",
    ("com/rs2/net/packet/DelayedAnimationEvent", "b", "I"): "animationId",
    ("com/rs2/net/packet/QueuedPositionUnlockEvent", "a", "Z"): "delayElapsed",
    ("com/rs2/net/packet/QueuedPositionUnlockEvent", "b", "Lcom/rs2/net/packet/PacketSender;"): "packetSender",
    ("com/rs2/net/packet/QueuedPositionUnlockEvent", "c", "Z"): "clearForcedMovementFlag",
    ("com/rs2/net/packet/RelativePositionUnlockEvent", "a", "Z"): "delayElapsed",
    ("com/rs2/net/packet/RelativePositionUnlockEvent", "b", "Lcom/rs2/net/packet/PacketSender;"): "packetSender",
    ("com/rs2/net/packet/RelativePositionUnlockEvent", "c", "Z"): "clearForcedMovementFlag",
    ("com/rs2/net/packet/YAxisPositionUnlockEvent", "a", "Z"): "delayElapsed",
    ("com/rs2/net/packet/YAxisPositionUnlockEvent", "b", "Lcom/rs2/net/packet/PacketSender;"): "packetSender",
    ("com/rs2/net/packet/YAxisPositionUnlockEvent", "c", "Z"): "clearForcedMovementFlag",
    ("com/rs2/net/packet/AgilityMovementCompletionEvent", "a", "Z"): "delayElapsed",
    ("com/rs2/net/packet/AgilityMovementCompletionEvent", "b", "Lcom/rs2/net/packet/PacketSender;"): "packetSender",
    ("com/rs2/net/packet/AgilityMovementCompletionEvent", "c", "Ljava/lang/String;"): "completionMessage",
    ("com/rs2/net/packet/AgilityMovementCompletionEvent", "d", "D"): "experience",
    ("com/rs2/net/packet/AgilityMovementCompletionEvent", "e", "Z"): "restoreRunning",
    ("com/rs2/net/packet/AgilityMovementCompletionEvent", "f", "Z"): "clearForcedMovementFlag",
}

_SINGLE_CAPTURED_PLAYER_FIELDS = {
    "com/rs2/bot/tasks/NorthMossGiantDungeonEntryTickTask": "a",
    "com/rs2/bot/tasks/SkeletonDungeonEntryTickTask": "a",
    "com/rs2/bot/tasks/SouthMossGiantDungeonEntryTickTask": "a",
    "com/rs2/bot/tasks/SpiderRatDungeonEntryTickTask": "a",
    "com/rs2/bot/tasks/TanningBotTickTask": "a",
    "com/rs2/bot/tasks/VarrockSewerGiantRatDungeonEntryTickTask": "a",
    "com/rs2/bot/tasks/WildernessRuniteMineEscapeMonitorTickTask": "a",
    "com/rs2/model/dialogue/TenthSquadSigilTeleportTask": "a",
    "com/rs2/model/gameplay/SmokeDungeonDamageTask": "a",
    "com/rs2/model/interaction/MiningShortcutTask": "a",
    "com/rs2/model/interaction/SarcophagusSneakTask": "a",
    "com/rs2/model/interaction/SearchBookcaseTask": "a",
    "com/rs2/model/interaction/SearchCrateTask": "a",
    "com/rs2/model/interaction/SearchHayTask": "a",
    "com/rs2/model/npc/AbyssMageTeleportEvent": "a",
    "com/rs2/model/npc/MageArenaChallengeStartTask": "a",
    "com/rs2/model/npc/NpcDialogueTeleportEvent": "a",
    "com/rs2/model/npc/NpcRelocationEvent": "a",
    "com/rs2/model/player/DelayedBankOpenTask": "a",
    "com/rs2/model/quest/impl/ApeAtollDungeonHazardDamageTask": "b",
    "com/rs2/model/quest/impl/ApeAtollGuardCaptureDialogueTask": "a",
    "com/rs2/model/quest/impl/ApeAtollGuardCaptureTask": "a",
    "com/rs2/model/quest/impl/ApothecaryCadavaPotionReadyTask": "a",
    "com/rs2/model/quest/impl/ArdougneZooGnomeRescueTask": "a",
    "com/rs2/model/quest/impl/ArdougneZooMonkeyRecaptureTask": "a",
    "com/rs2/model/quest/impl/AwowogeiAllianceCutsceneStartTask": "a",
    "com/rs2/model/quest/impl/BananaCratePromptTask": "a",
    "com/rs2/model/quest/impl/CaranockShipyardCutsceneStartTask": "a",
    "com/rs2/model/quest/impl/CaranockWaydarCutsceneStartTask": "a",
    "com/rs2/model/quest/impl/CompostHeapKeyFindTask": "a",
    "com/rs2/model/quest/impl/DaeroBlindfoldHangarReturnTask": "a",
    "com/rs2/model/quest/impl/DaeroBlindfoldHangarTravelTask": "a",
    "com/rs2/model/quest/impl/DaeroOrdersDecodeTask": "a",
    "com/rs2/model/quest/impl/DaeroPuzzleCompletionTeleportTask": "a",
    "com/rs2/model/quest/impl/DaeroTrainingTimeSkipTask": "b",
    "com/rs2/model/quest/impl/DaeroTrainingXpRewardTask": "b",
    "com/rs2/model/quest/impl/DragonSlayerShipHoleRepairTask": "b",
    "com/rs2/model/quest/impl/DraynorManorCandlesBurnTask": "b",
    "com/rs2/model/quest/impl/ErnestHumanDialogueTask": "a",
    "com/rs2/model/quest/impl/ErnestSecretDoorReturnTask": "a",
    "com/rs2/model/quest/impl/FluffsInteractionHintTask": "b",
    "com/rs2/model/quest/impl/FluffsKittenReunionFinishTask": "b",
    "com/rs2/model/quest/impl/GertrudeQuestCompletionTask": "b",
    "com/rs2/model/quest/impl/GertrudeRewardFoodTask": "c",
    "com/rs2/model/quest/impl/GloughChestScrollFindTask": "a",
    "com/rs2/model/quest/impl/GloughDemonSpawnTask": "a",
    "com/rs2/model/quest/impl/GloughGuardArrestStartTask": "a",
    "com/rs2/model/quest/impl/GrandTreeGloughCaveEncounterTask": "a",
    "com/rs2/model/quest/impl/GrandTreeGuardPassageReportTask": "a",
    "com/rs2/model/quest/impl/GrandTreeGuardPrisonEscortTask": "a",
    "com/rs2/model/quest/impl/HectorsChestMessageTask": "a",
    "com/rs2/model/quest/impl/InitialBananaCratePromptTask": "a",
    "com/rs2/model/quest/impl/JunglePotionHerbSearchEvent": "a",
    "com/rs2/model/quest/impl/KingNarnodePrisonReleaseTask": "a",
    "com/rs2/model/quest/impl/KittenCrateSearchTask": "a",
    "com/rs2/model/quest/impl/KrukAwowogeiEscortTask": "a",
    "com/rs2/model/quest/impl/LostCityZanarisEntryCompletionTask": "b",
    "com/rs2/model/quest/impl/LumdoApeAtollTravelTask": "a",
    "com/rs2/model/quest/impl/LumdoCrashIslandReturnTask": "a",
    "com/rs2/model/quest/impl/ManniDrinkingContestPlayerDrinkTask": "b",
    "com/rs2/model/quest/impl/ManniDrinkingContestResultTask": "a",
    "com/rs2/model/quest/impl/MerlinsCrystalThrantaxSummonTask": "a",
    "com/rs2/model/quest/impl/MonkeyAmuletMouldCrateSearchTask": "a",
    "com/rs2/model/quest/impl/MonkeyAmuletSmithingTask": "a",
    "com/rs2/model/quest/impl/MonkeyDenturesCrateSearchTask": "a",
    "com/rs2/model/quest/impl/MonkeyMadnessChapterFourTitleTask": "b",
    "com/rs2/model/quest/impl/MonkeyMadnessChapterOneTitleTask": "b",
    "com/rs2/model/quest/impl/MonkeyMadnessChapterThreeTitleTask": "b",
    "com/rs2/model/quest/impl/MonkeyMadnessChapterTwoTitleTask": "b",
    "com/rs2/model/quest/impl/NarnodeOrdersWritingTask": "a",
    "com/rs2/model/quest/impl/PiratesTreasureDigCompletionEvent": "b",
    "com/rs2/model/quest/impl/PoisonedFishFoodPiranhaFinishTask": "a",
    "com/rs2/model/quest/impl/PoisonedFishFoodPiranhaTask": "b",
    "com/rs2/model/quest/impl/PouletmorphMachineStartTask": "c",
    "com/rs2/model/quest/impl/PouletmorphMachineTransformTask": "b",
    "com/rs2/model/quest/impl/RestlessGhostCoffinGhostSpawnTask": "a",
    "com/rs2/model/quest/impl/RumBananaCratePromptTask": "a",
    "com/rs2/model/quest/impl/RumBananaCrateSearchTask": "b",
    "com/rs2/model/quest/impl/ScorpionCatcherCageHandoffDialogueTask": "a",
    "com/rs2/model/quest/impl/SeerMirrorGazeTask": "c",
    "com/rs2/model/quest/impl/SeerMirrorHairSmoothTask": "b",
    "com/rs2/model/quest/impl/SeerMirrorResultDialogueTask": "a",
    "com/rs2/model/quest/impl/ShipyardCrateHoleSearchTask": "a",
    "com/rs2/model/quest/impl/ShipyardCrateTunnelDescentTask": "a",
    "com/rs2/model/quest/impl/ShipyardGateLockpickTask": "a",
    "com/rs2/model/quest/impl/SwensenMazeObject4150ExitStepTask": "a",
    "com/rs2/model/quest/impl/SwensenMazeObject4151ExitStepTask": "a",
    "com/rs2/model/quest/impl/SwensenMazeObject4152ExitStepTask": "a",
    "com/rs2/model/quest/impl/SwensenMazeObject4153ExitStepTask": "a",
    "com/rs2/model/quest/impl/SwensenMazeObject4154ExitStepTask": "a",
    "com/rs2/model/quest/impl/SwensenMazeObject4155ExitStepTask": "a",
    "com/rs2/model/quest/impl/SwensenMazeRandomObjectExitStepTask": "b",
    "com/rs2/model/quest/impl/VampireAttackStartTask": "b",
    "com/rs2/model/quest/impl/VampireCoffinRiseTask": "c",
    "com/rs2/model/quest/impl/WaydarCrashIslandReturnFlightTask": "a",
    "com/rs2/model/quest/impl/WaydarHangarReturnTask": "a",
    "com/rs2/model/quest/impl/WaydarInitialCrashIslandFlightTask": "a",
    "com/rs2/model/quest/impl/WaydarRepeatHangarReturnTask": "a",
    "com/rs2/model/quest/impl/WitchsHouseGardenEjectEvent": "b",
    "com/rs2/model/quest/impl/WitchsHouseGardenTrespassTask": "b",
    "com/rs2/model/quest/impl/ZooknockAmuletEnchantSpellTask": "a",
    "com/rs2/model/quest/impl/ZooknockFinalBattleExitTask": "a",
    "com/rs2/model/quest/impl/ZooknockGreegreeEnchantSpellTask": "a",
    "com/rs2/model/skill/magic/OrbChargeTask": "b",
    "com/rs2/model/travel/GnomeGliderLandingTask": "a",
    "com/rs2/net/packet/handler/DigSearchTask": "a",
    "com/rs2/net/packet/handler/GroundItemFiremakingTask": "a",
    "com/rs2/net/packet/handler/TinderboxOnGroundItemTask": "a",
}

for _owner, _old_name in _SINGLE_CAPTURED_PLAYER_FIELDS.items():
    FIELD_NAME_MAP.setdefault((_owner, _old_name, "Lcom/rs2/model/player/Player;"), "player")

for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/npc/MageArenaChallengeStartTask", "b", "Lcom/rs2/model/npc/Npc;", "challengeNpc"),
    ("com/rs2/model/npc/NpcRelocationEvent", "b", "I", "destinationX"),
    ("com/rs2/model/npc/NpcRelocationEvent", "c", "I", "destinationY"),
    ("com/rs2/model/npc/NpcRelocationEvent", "d", "I", "destinationPlane"),
    ("com/rs2/model/npc/NpcRelocationEvent", "e", "Z", "unregisterSourceNpc"),
    ("com/rs2/model/npc/NpcRelocationEvent", "f", "Lcom/rs2/model/npc/Npc;", "sourceNpc"),
    ("com/rs2/model/quest/impl/GloughGuardArrestStartTask", "b", "Lcom/rs2/model/npc/Npc;", "guardNpc"),
    ("com/rs2/model/quest/impl/KingNarnodePrisonReleaseTask", "b", "Lcom/rs2/model/npc/Npc;", "kingNarnodeNpc"),
    ("com/rs2/model/quest/impl/VampireAttackStartTask", "a", "Lcom/rs2/model/npc/Npc;", "vampireNpc"),
    ("com/rs2/model/quest/impl/VampireCoffinRiseTask", "b", "Lcom/rs2/model/npc/Npc;", "vampireNpc"),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/task/TickTask", "a", "I", "remainingTicks"),
    ("com/rs2/model/task/TickTask", "b", "I", "intervalTicks"),
    ("com/rs2/model/task/TickTask", "c", "Z", "active"),
    ("com/rs2/model/task/TickTask", "d", "Z", "executeImmediately"),
    ("com/rs2/model/task/DelayTimer", "a", "I", "startTick"),
    ("com/rs2/model/task/DelayTimer", "b", "I", "delayTicks"),
    ("com/rs2/model/task/CycleEventContainer", "a", "Lcom/rs2/model/Entity;", "entity"),
    ("com/rs2/model/task/CycleEventContainer", "b", "Z", "active"),
    ("com/rs2/model/task/CycleEventContainer", "c", "I", "tickDelay"),
    ("com/rs2/model/task/CycleEventContainer", "d", "Lcom/rs2/model/task/CycleEvent;", "event"),
    ("com/rs2/model/task/CycleEventContainer", "e", "I", "elapsedTicks"),
    ("com/rs2/model/task/CycleEventHandler", "a", "Lcom/rs2/model/task/CycleEventHandler;", "instance"),
    ("com/rs2/model/task/CycleEventHandler", "b", "Ljava/util/Queue;", "activeEvents"),
    ("com/rs2/model/task/CycleEventHandler", "c", "Ljava/util/Queue;", "pendingEvents"),
    ("com/rs2/model/interaction/MiningShortcutTask", "b", "I", "actionSequence"),
    ("com/rs2/model/interaction/MiningShortcutTask", "c", "I", "objectX"),
    ("com/rs2/model/interaction/MiningShortcutTask", "d", "I", "objectY"),
    ("com/rs2/model/interaction/MiningShortcutTask", "e", "I", "objectId"),
    ("com/rs2/model/interaction/MiningShortcutTask", "f", "Lcom/rs2/model/skill/GatheringToolDefinition;", "gatheringTool"),
    ("com/rs2/model/npc/AbyssMageTeleportEvent", "b", "I", "destinationX"),
    ("com/rs2/model/npc/AbyssMageTeleportEvent", "c", "I", "destinationY"),
    ("com/rs2/model/npc/AbyssMageTeleportEvent", "d", "I", "destinationPlane"),
    ("com/rs2/model/npc/NpcDialogueTeleportEvent", "b", "I", "destinationX"),
    ("com/rs2/model/npc/NpcDialogueTeleportEvent", "c", "I", "destinationY"),
    ("com/rs2/model/npc/NpcDialogueTeleportEvent", "d", "I", "destinationPlane"),
    ("com/rs2/net/packet/handler/DigSearchTask", "b", "I", "actionSequence"),
    ("com/rs2/net/packet/handler/DigSearchTask", "c", "Z", "sendNothingFoundMessage"),
    ("com/rs2/net/packet/handler/GroundItemFiremakingTask", "b", "I", "actionSequence"),
    ("com/rs2/net/packet/handler/TinderboxOnGroundItemTask", "b", "I", "actionSequence"),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/BotReloginTask", "a", "Ljava/lang/String;", "username"),
    ("com/rs2/model/npc/NpcStatRestoreTask", "c", "I", "skillId"),
    ("com/rs2/model/skill/magic/DelayedSpellImpactTask", "a", "Lcom/rs2/model/skill/magic/MagicSpellAction;", "spellAction"),
    ("com/rs2/model/skill/magic/DelayedSpellImpactTask", "b", "Lcom/rs2/model/combat/hit/HitDefinition;", "hit"),
    ("com/rs2/model/skill/magic/DelayedSpellImpactTask", "c", "Lcom/rs2/model/Entity;", "target"),
    ("com/rs2/model/skill/magic/DelayedSpellImpactTask", "d", "Lcom/rs2/model/Position;", "targetPosition"),
    ("com/rs2/model/skill/magic/NecromancyReanimateTask", "a", "Lcom/rs2/model/skill/magic/MagicSpellAction;", "spellAction"),
    ("com/rs2/model/skill/magic/NecromancyReanimateTask", "b", "I", "reanimationIndex"),
    ("com/rs2/model/skill/magic/OrbChargeTask", "a", "I", "remainingCasts"),
    ("com/rs2/model/quest/impl/JunglePotionHerbSearchEvent", "b", "I", "actionSequence"),
    ("com/rs2/model/quest/impl/JunglePotionHerbSearchEvent", "c", "I", "objectX"),
    ("com/rs2/model/quest/impl/JunglePotionHerbSearchEvent", "d", "I", "objectY"),
    ("com/rs2/model/quest/impl/JunglePotionHerbSearchEvent", "e", "I", "objectId"),
    ("com/rs2/model/quest/impl/JunglePotionHerbSearchEvent", "f", "I", "herbItemId"),
    ("com/rs2/model/quest/impl/JunglePotionHerbSearchEvent", "g", "Z", "restoreObjectAfterSearch"),
    ("com/rs2/model/quest/impl/PiratesTreasureDigCompletionEvent", "a", "Lcom/rs2/model/quest/impl/PiratesTreasureQuest;", "quest"),
    ("com/rs2/model/quest/impl/PiratesTreasureDigCompletionEvent", "c", "I", "actionSequence"),
    ("com/rs2/model/quest/impl/DaeroTrainingXpRewardTask", "a", "Lcom/rs2/model/quest/impl/MonkeyMadnessQuest;", "quest"),
    ("com/rs2/model/quest/impl/DaeroTrainingXpRewardTask", "c", "I", "rewardChoice"),
    ("com/rs2/model/quest/impl/DaeroTrainingXpRewardTask", "d", "I", "questFlagIndex"),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_BOT_SHOP_TRADE_TICK_TASKS = [
    ("AlKharidCraftingShopTradeTickTask", True),
    ("AlKharidGeneralStoreTradeTickTask", False),
    ("AlKharidLegsShopTradeTickTask", True),
    ("AlKharidScimitarShopTradeTickTask", True),
    ("AlKharidSkirtShopTradeTickTask", True),
    ("ArdougneAxeShopTradeTickTask", False),
    ("ArdougnePlatebodyShopTradeTickTask", True),
    ("BarbarianVillageHelmetShopTradeTickTask", True),
    ("CatherbyArcheryShopShortbowTradeTickTask", True),
    ("CatherbyFishingShopTradeTickTask", True),
    ("CatherbyGeneralStoreTradeTickTask", False),
    ("ChampionsGuildPlatebodyShopTradeTickTask", True),
    ("ChampionsGuildRuneLongswordShopTradeTickTask", True),
    ("DraynorAxeShopTradeTickTask", True),
    ("DraynorGeneralStoreTradeTickTask", False),
    ("DwarvenMinePickaxeShopTradeTickTask", True),
    ("EdgevilleGeneralStoreTradeTickTask", False),
    ("EdgevilleOziachRunePlatebodyShopTradeTickTask", True),
    ("FaladorChainbodyShopTradeTickTask", True),
    ("FaladorGeneralStoreTradeTickTask", False),
    ("FaladorMaceShopTradeTickTask", True),
    ("FaladorShieldShopTradeTickTask", True),
    ("PortSarimBattleaxeShopTradeTickTask", True),
    ("PortSarimFishingShopTradeTickTask", True),
    ("PortSarimMagicShopChaosRuneTradeTickTask", True),
    ("VarrockArcheryShopSteelArrowTradeTickTask", True),
    ("VarrockArmourShopChainbodyTradeTickTask", True),
    ("VarrockClothesShopCapeTradeTickTask", True),
    ("VarrockGeneralStoreTradeTickTask", False),
    ("VarrockRuneShopChaosRuneTradeTickTask", True),
    ("VarrockStaffShopFireStaffTradeTickTask", True),
    ("VarrockSwordShopMithrilLongswordTradeTickTask", True),
]

_BOT_SHOP_ROUTE_TASKS = [
    ("AlKharidCraftingShopBotTask", True),
    ("AlKharidGeneralStoreBotTask", True),
    ("AlKharidLegsShopBotTask", True),
    ("AlKharidScimitarShopBotTask", True),
    ("AlKharidSkirtShopBotTask", True),
    ("ArdougneAxeShopBotTask", True),
    ("ArdougnePlatebodyShopBotTask", True),
    ("BarbarianVillageHelmetShopBotTask", True),
    ("CatherbyArcheryShopShortbowBotTask", True),
    ("CatherbyFishingShopBotTask", False),
    ("CatherbyGeneralStoreBotTask", False),
    ("ChampionsGuildPlatebodyShopBotTask", True),
    ("ChampionsGuildRuneLongswordShopBotTask", True),
    ("DraynorAxeShopBotTask", True),
    ("DraynorGeneralStoreBotTask", True),
    ("DwarvenMinePickaxeShopBotTask", True),
    ("EdgevilleGeneralStoreBotTask", True),
    ("EdgevilleOziachRunePlatebodyShopBotTask", True),
    ("FaladorChainbodyShopBotTask", True),
    ("FaladorGeneralStoreBotTask", True),
    ("FaladorMaceShopBotTask", True),
    ("FaladorShieldShopBotTask", True),
    ("PortSarimBattleaxeShopBotTask", True),
    ("PortSarimFishingShopBotTask", True),
    ("PortSarimMagicShopChaosRuneBotTask", True),
    ("VarrockArcheryShopSteelArrowBotTask", True),
    ("VarrockArmourShopChainbodyBotTask", True),
    ("VarrockClothesShopCapeBotTask", True),
    ("VarrockGeneralStoreBotTask", True),
    ("VarrockRuneShopChaosRuneBotTask", True),
    ("VarrockStaffShopFireStaffBotTask", True),
    ("VarrockSwordShopMithrilLongswordBotTask", True),
]

for _task_class, _uses_route_segments in _BOT_SHOP_ROUTE_TASKS:
    _owner = f"com/rs2/bot/shop/{_task_class}"
    FIELD_NAME_MAP.setdefault((_owner, "aa", "Lcom/rs2/model/Position;"), "routeStartPosition")
    if _uses_route_segments:
        FIELD_NAME_MAP.setdefault((_owner, "ab", "[Lcom/rs2/bot/BotRoute;"), "taskRouteSegments")
    else:
        FIELD_NAME_MAP.setdefault((_owner, "ab", "Lcom/rs2/bot/BotRoute;"), "taskRoute")

for _task_class, _has_shop_item in _BOT_SHOP_TRADE_TICK_TASKS:
    _owner = f"com/rs2/bot/shop/{_task_class}"
    FIELD_NAME_MAP.setdefault((_owner, "a", "Lcom/rs2/model/player/Player;"), "player")
    if _has_shop_item:
        FIELD_NAME_MAP.setdefault((_owner, "b", "Lcom/rs2/model/item/ItemStack;"), "shopItem")

_BOT_TASK_SINGLETON_FIELDS = [
    ("aa", "AlKharidMineBotTask", "alKharidMineTask"),
    ("b", "BrimhavenDungeonBlueDragonNorthCombatBotTask", "brimhavenDungeonBlueDragonNorthCombatTask"),
    ("ab", "BrimhavenDungeonBlueDragonSouthCombatBotTask", "brimhavenDungeonBlueDragonSouthCombatTask"),
    ("ac", "BrimhavenDungeonRedDragonCombatBotTask", "brimhavenDungeonRedDragonCombatTask"),
    ("ad", "CatherbyFishingBotTask", "catherbyFishingTask"),
    ("ae", "CraftingGuildMineBotTask", "craftingGuildMineTask"),
    ("af", "DraynorNetFishingBotTask", "draynorNetFishingTask"),
    ("c", "DraynorWillowWoodcuttingBotTask", "draynorWillowWoodcuttingTask"),
    ("ag", "BarbarianVillageFlyFishingBotTask", "barbarianVillageFlyFishingTask"),
    ("ah", "EdgevilleYewWoodcuttingBotTask", "edgevilleYewWoodcuttingTask"),
    ("ai", "DraynorChickenCombatBotTask", "draynorChickenCombatTask"),
    ("aj", "FaladorCowCombatBotTask", "faladorCowCombatTask"),
    ("d", "DwarvenMineBotTask", "dwarvenMineTask"),
    ("ak", "FaladorYewWoodcuttingBotTask", "faladorYewWoodcuttingTask"),
    ("al", "KaramjaFishingBotTask", "karamjaFishingTask"),
    ("am", "KaramjaVolcanoNorthLesserDemonCombatBotTask", "karamjaVolcanoNorthLesserDemonCombatTask"),
    ("an", "KaramjaVolcanoSouthLesserDemonCombatBotTask", "karamjaVolcanoSouthLesserDemonCombatTask"),
    ("ao", "LumbridgeEastChickenCombatBotTask", "lumbridgeEastChickenCombatTask"),
    ("ap", "LumbridgeWestChickenCombatBotTask", "lumbridgeWestChickenCombatTask"),
    ("aq", "LumbridgeCowCombatBotTask", "lumbridgeCowCombatTask"),
    ("ar", "MiningGuildMineBotTask", "miningGuildMineTask"),
    ("as", "TaverleyDungeonHellhoundCombatBotTask", "taverleyDungeonHellhoundCombatTask"),
    ("at", "VarrockEastMineBotTask", "varrockEastMineTask"),
    ("e", "RimmingtonMineBotTask", "rimmingtonMineTask"),
    ("au", "EdgevilleDungeonHillGiantCombatBotTask", "edgevilleDungeonHillGiantCombatTask"),
    ("av", "EdgevilleDungeonNorthMossGiantCombatBotTask", "edgevilleDungeonNorthMossGiantCombatTask"),
    ("aw", "EdgevilleDungeonSouthMossGiantCombatBotTask", "edgevilleDungeonSouthMossGiantCombatTask"),
    ("f", "VarrockWestMineBotTask", "varrockWestMineTask"),
    ("ax", "VarrockPalaceYewWoodcuttingBotTask", "varrockPalaceYewWoodcuttingTask"),
    ("ay", "SeersMapleWoodcuttingBotTask", "seersMapleWoodcuttingTask"),
    ("az", "SeersYewWoodcuttingBotTask", "seersYewWoodcuttingTask"),
    ("aA", "SeersMagicTreeWoodcuttingBotTask", "seersMagicTreeWoodcuttingTask"),
    ("aB", "SorcerersTowerMagicTreeWoodcuttingBotTask", "sorcerersTowerMagicTreeWoodcuttingTask"),
    ("g", "SeersFlaxPickingBotTask", "seersFlaxPickingTask"),
    ("aC", "FaladorWineOfZamorakTelegrabBotTask", "faladorWineOfZamorakTelegrabTask"),
    ("aD", "DraynorOakWoodcuttingBotTask", "draynorOakWoodcuttingTask"),
    ("aE", "DraynorTreeWoodcuttingBotTask", "draynorTreeWoodcuttingTask"),
    ("aF", "VarrockWestOakWoodcuttingBotTask", "varrockWestOakWoodcuttingTask"),
    ("aG", "VarrockWestTreeWoodcuttingBotTask", "varrockWestTreeWoodcuttingTask"),
    ("aH", "VarrockEastOakWoodcuttingBotTask", "varrockEastOakWoodcuttingTask"),
    ("aI", "VarrockEastTreeWoodcuttingBotTask", "varrockEastTreeWoodcuttingTask"),
    ("aJ", "EdgevilleTreeWoodcuttingBotTask", "edgevilleTreeWoodcuttingTask"),
    ("aK", "DraynorYewWoodcuttingBotTask", "draynorYewWoodcuttingTask"),
    ("aL", "DraynorGoblinCombatBotTask", "draynorGoblinCombatTask"),
    ("h", "LumbridgeGoblinCombatBotTask", "lumbridgeGoblinCombatTask"),
    ("aM", "AlKharidFlyFishingBotTask", "alKharidFlyFishingTask"),
    ("aN", "AlKharidNetBaitFishingBotTask", "alKharidNetBaitFishingTask"),
    ("aO", "AlKharidWarriorCombatBotTask", "alKharidWarriorCombatTask"),
    ("aP", "VarrockGuardCombatBotTask", "varrockGuardCombatTask"),
    ("aQ", "VarrockSewerGiantRatCombatBotTask", "varrockSewerGiantRatCombatTask"),
    ("aR", "BarbarianVillageBarbarianCombatBotTask", "barbarianVillageBarbarianCombatTask"),
    ("aS", "DwarvenMineDwarfCombatBotTask", "dwarvenMineDwarfCombatTask"),
    ("aT", "FaladorGuardCombatBotTask", "faladorGuardCombatTask"),
    ("aU", "EdgevilleDungeonSpiderRatCombatBotTask", "edgevilleDungeonSpiderRatCombatTask"),
    ("aV", "EdgevilleDungeonSkeletonCombatBotTask", "edgevilleDungeonSkeletonCombatTask"),
    ("aW", "VarrockRuneEssenceMiningBotTask", "varrockRuneEssenceMiningTask"),
    ("aX", "AirRuneRunecraftingBotTask", "airRuneRunecraftingTask"),
    ("aY", "MindRuneRunecraftingBotTask", "mindRuneRunecraftingTask"),
    ("aZ", "WaterRuneRunecraftingBotTask", "waterRuneRunecraftingTask"),
    ("ba", "EarthRuneRunecraftingBotTask", "earthRuneRunecraftingTask"),
    ("bb", "FireRuneRunecraftingBotTask", "fireRuneRunecraftingTask"),
    ("bc", "BodyRuneRunecraftingBotTask", "bodyRuneRunecraftingTask"),
    ("bd", "AlKharidLobsterCookingBotTask", "alKharidLobsterCookingTask"),
    ("be", "AlKharidSteelSmeltingBotTask", "alKharidSteelSmeltingTask"),
    ("bf", "FaladorSteelSmeltingBotTask", "faladorSteelSmeltingTask"),
    ("bg", "VarrockLobsterCookingBotTask", "varrockLobsterCookingTask"),
    ("i", "VarrockSteelDaggerSmithingBotTask", "varrockSteelDaggerSmithingTask"),
    ("bh", "WildernessRuniteMineBotTask", "wildernessRuniteMineTask"),
    ("bi", "VarrockSouthChickenCombatBotTask", "varrockSouthChickenCombatTask"),
    ("j", "WizardsTowerLesserDemonMagicBotTask", "wizardsTowerLesserDemonMagicTask"),
    ("k", "FaladorImpCombatBotTask", "faladorImpCombatTask"),
    ("bj", "DraynorSheepShearingBotTask", "draynorSheepShearingTask"),
    ("bk", "LumbridgeWoolSpinningBotTask", "lumbridgeWoolSpinningTask"),
    ("bl", "CatherbyLobsterCookingBotTask", "catherbyLobsterCookingTask"),
    ("bm", "SeersFlaxSpinningBotTask", "seersFlaxSpinningTask"),
]

for _old_name, _task_class, _mapped_name in _BOT_TASK_SINGLETON_FIELDS:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/bot/BotTaskDefinition", _old_name, f"Lcom/rs2/bot/tasks/{_task_class};"),
        _mapped_name,
    )

_BOT_TASK_SINGLETON_ACCESSORS = [
    ("k", "DraynorNetFishingBotTask", "getDraynorNetFishingTask"),
    ("l", "BarbarianVillageFlyFishingBotTask", "getBarbarianVillageFlyFishingTask"),
    ("m", "KaramjaFishingBotTask", "getKaramjaFishingTask"),
    ("n", "AlKharidFlyFishingBotTask", "getAlKharidFlyFishingTask"),
    ("o", "AlKharidNetBaitFishingBotTask", "getAlKharidNetBaitFishingTask"),
    ("p", "CatherbyFishingBotTask", "getCatherbyFishingTask"),
    ("q", "VarrockLobsterCookingBotTask", "getVarrockLobsterCookingTask"),
    ("r", "AlKharidLobsterCookingBotTask", "getAlKharidLobsterCookingTask"),
    ("s", "CatherbyLobsterCookingBotTask", "getCatherbyLobsterCookingTask"),
    ("t", "AlKharidMineBotTask", "getAlKharidMineTask"),
    ("u", "CraftingGuildMineBotTask", "getCraftingGuildMineTask"),
    ("v", "MiningGuildMineBotTask", "getMiningGuildMineTask"),
    ("w", "VarrockEastMineBotTask", "getVarrockEastMineTask"),
    ("x", "WildernessRuniteMineBotTask", "getWildernessRuniteMineTask"),
    ("y", "AlKharidSteelSmeltingBotTask", "getAlKharidSteelSmeltingTask"),
    ("z", "FaladorSteelSmeltingBotTask", "getFaladorSteelSmeltingTask"),
    ("A", "EdgevilleYewWoodcuttingBotTask", "getEdgevilleYewWoodcuttingTask"),
    ("B", "FaladorYewWoodcuttingBotTask", "getFaladorYewWoodcuttingTask"),
    ("C", "VarrockPalaceYewWoodcuttingBotTask", "getVarrockPalaceYewWoodcuttingTask"),
    ("D", "DraynorOakWoodcuttingBotTask", "getDraynorOakWoodcuttingTask"),
    ("E", "DraynorTreeWoodcuttingBotTask", "getDraynorTreeWoodcuttingTask"),
    ("F", "VarrockWestOakWoodcuttingBotTask", "getVarrockWestOakWoodcuttingTask"),
    ("G", "VarrockWestTreeWoodcuttingBotTask", "getVarrockWestTreeWoodcuttingTask"),
    ("H", "VarrockEastOakWoodcuttingBotTask", "getVarrockEastOakWoodcuttingTask"),
    ("I", "VarrockEastTreeWoodcuttingBotTask", "getVarrockEastTreeWoodcuttingTask"),
    ("J", "EdgevilleTreeWoodcuttingBotTask", "getEdgevilleTreeWoodcuttingTask"),
    ("K", "DraynorYewWoodcuttingBotTask", "getDraynorYewWoodcuttingTask"),
    ("L", "SeersMapleWoodcuttingBotTask", "getSeersMapleWoodcuttingTask"),
    ("M", "SeersYewWoodcuttingBotTask", "getSeersYewWoodcuttingTask"),
    ("N", "SeersMagicTreeWoodcuttingBotTask", "getSeersMagicTreeWoodcuttingTask"),
    ("O", "SorcerersTowerMagicTreeWoodcuttingBotTask", "getSorcerersTowerMagicTreeWoodcuttingTask"),
    ("P", "VarrockRuneEssenceMiningBotTask", "getVarrockRuneEssenceMiningTask"),
    ("Q", "AirRuneRunecraftingBotTask", "getAirRuneRunecraftingTask"),
    ("R", "MindRuneRunecraftingBotTask", "getMindRuneRunecraftingTask"),
    ("S", "WaterRuneRunecraftingBotTask", "getWaterRuneRunecraftingTask"),
    ("T", "EarthRuneRunecraftingBotTask", "getEarthRuneRunecraftingTask"),
    ("U", "FireRuneRunecraftingBotTask", "getFireRuneRunecraftingTask"),
    ("V", "BodyRuneRunecraftingBotTask", "getBodyRuneRunecraftingTask"),
    ("W", "FaladorWineOfZamorakTelegrabBotTask", "getFaladorWineOfZamorakTelegrabTask"),
    ("X", "DraynorSheepShearingBotTask", "getDraynorSheepShearingTask"),
    ("Y", "LumbridgeWoolSpinningBotTask", "getLumbridgeWoolSpinningTask"),
    ("Z", "SeersFlaxSpinningBotTask", "getSeersFlaxSpinningTask"),
    ("aa", "DraynorChickenCombatBotTask", "getDraynorChickenCombatTask"),
    ("ab", "FaladorCowCombatBotTask", "getFaladorCowCombatTask"),
    ("ac", "KaramjaVolcanoNorthLesserDemonCombatBotTask", "getKaramjaVolcanoNorthLesserDemonCombatTask"),
    ("ad", "KaramjaVolcanoSouthLesserDemonCombatBotTask", "getKaramjaVolcanoSouthLesserDemonCombatTask"),
    ("ae", "LumbridgeEastChickenCombatBotTask", "getLumbridgeEastChickenCombatTask"),
    ("af", "LumbridgeWestChickenCombatBotTask", "getLumbridgeWestChickenCombatTask"),
    ("ag", "LumbridgeCowCombatBotTask", "getLumbridgeCowCombatTask"),
    ("ah", "EdgevilleDungeonHillGiantCombatBotTask", "getEdgevilleDungeonHillGiantCombatTask"),
    ("ai", "EdgevilleDungeonNorthMossGiantCombatBotTask", "getEdgevilleDungeonNorthMossGiantCombatTask"),
    ("aj", "EdgevilleDungeonSouthMossGiantCombatBotTask", "getEdgevilleDungeonSouthMossGiantCombatTask"),
    ("ak", "DraynorGoblinCombatBotTask", "getDraynorGoblinCombatTask"),
    ("al", "AlKharidWarriorCombatBotTask", "getAlKharidWarriorCombatTask"),
    ("am", "VarrockGuardCombatBotTask", "getVarrockGuardCombatTask"),
    ("an", "VarrockSewerGiantRatCombatBotTask", "getVarrockSewerGiantRatCombatTask"),
    ("ao", "BarbarianVillageBarbarianCombatBotTask", "getBarbarianVillageBarbarianCombatTask"),
    ("ap", "DwarvenMineDwarfCombatBotTask", "getDwarvenMineDwarfCombatTask"),
    ("aq", "FaladorGuardCombatBotTask", "getFaladorGuardCombatTask"),
    ("ar", "EdgevilleDungeonSpiderRatCombatBotTask", "getEdgevilleDungeonSpiderRatCombatTask"),
    ("as", "EdgevilleDungeonSkeletonCombatBotTask", "getEdgevilleDungeonSkeletonCombatTask"),
    ("at", "VarrockSouthChickenCombatBotTask", "getVarrockSouthChickenCombatTask"),
    ("au", "BrimhavenDungeonBlueDragonSouthCombatBotTask", "getBrimhavenDungeonBlueDragonSouthCombatTask"),
    ("av", "BrimhavenDungeonRedDragonCombatBotTask", "getBrimhavenDungeonRedDragonCombatTask"),
    ("aw", "TaverleyDungeonHellhoundCombatBotTask", "getTaverleyDungeonHellhoundCombatTask"),
]

for _old_name, _task_class, _mapped_name in _BOT_TASK_SINGLETON_ACCESSORS:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/bot/BotTaskDefinition", _old_name, f"()Lcom/rs2/bot/tasks/{_task_class};"),
        _mapped_name,
    )

_SPELL_DEFINITION_ENUM_CONSTANTS = [
    ("a", "WIND_STRIKE"),
    ("b", "WATER_STRIKE"),
    ("c", "EARTH_STRIKE"),
    ("d", "FIRE_STRIKE"),
    ("e", "WIND_BOLT"),
    ("f", "WATER_BOLT"),
    ("g", "EARTH_BOLT"),
    ("h", "FIRE_BOLT"),
    ("i", "WIND_BLAST"),
    ("j", "WATER_BLAST"),
    ("k", "EARTH_BLAST"),
    ("l", "FIRE_BLAST"),
    ("m", "WIND_WAVE"),
    ("n", "WATER_WAVE"),
    ("o", "EARTH_WAVE"),
    ("p", "FIRE_WAVE"),
    ("q", "SMOKE_RUSH"),
    ("r", "SHADOW_RUSH"),
    ("s", "BLOOD_RUSH"),
    ("t", "ICE_RUSH"),
    ("u", "SMOKE_BURST"),
    ("v", "SHADOW_BURST"),
    ("w", "BLOOD_BURST"),
    ("x", "ICE_BURST"),
    ("y", "SMOKE_BLITZ"),
    ("z", "SHADOW_BLITZ"),
    ("A", "BLOOD_BLITZ"),
    ("B", "ICE_BLITZ"),
    ("C", "SMOKE_BARRAGE"),
    ("D", "SHADOW_BARRAGE"),
    ("E", "BLOOD_BARRAGE"),
    ("F", "ICE_BARRAGE"),
    ("G", "PADDEWWA_TELEPORT"),
    ("H", "SENNTISTEN_TELEPORT"),
    ("I", "KHARYRLL_TELEPORT"),
    ("J", "LASSAR_TELEPORT"),
    ("K", "DAREEYAK_TELEPORT"),
    ("L", "CARRALLANGAR_TELEPORT"),
    ("M", "ANNAKARL_TELEPORT"),
    ("N", "GHORROCK_TELEPORT"),
    ("O", "IBAN_BLAST"),
    ("P", "CONFUSE"),
    ("Q", "WEAKEN"),
    ("R", "CURSE"),
    ("S", "VULNERABILITY"),
    ("T", "ENFEEBLE"),
    ("U", "STUN"),
    ("V", "BIND"),
    ("W", "SNARE"),
    ("X", "ENTANGLE"),
    ("Y", "CRUMBLE_UNDEAD"),
    ("Z", "MAGIC_DART"),
    ("aa", "SARADOMIN_STRIKE"),
    ("ab", "CLAWS_OF_GUTHIX"),
    ("ac", "FLAMES_OF_ZAMORAK"),
    ("ad", "TELE_BLOCK"),
    ("ae", "CHARGE"),
    ("af", "LOW_LEVEL_ALCHEMY"),
    ("ag", "HIGH_LEVEL_ALCHEMY"),
    ("ah", "TELEKINETIC_GRAB"),
    ("ai", "CHARGE_WATER_ORB"),
    ("aj", "CHARGE_EARTH_ORB"),
    ("ak", "CHARGE_FIRE_ORB"),
    ("al", "CHARGE_AIR_ORB"),
    ("am", "BONES_TO_BANANAS"),
    ("an", "SUPERHEAT_ITEM"),
    ("ao", "BONES_TO_PEACHES"),
    ("ap", "LVL_1_ENCHANT"),
    ("aq", "LVL_2_ENCHANT"),
    ("ar", "LVL_3_ENCHANT"),
    ("as", "LVL_4_ENCHANT"),
    ("at", "LVL_5_ENCHANT"),
    ("au", "LVL_6_ENCHANT"),
    ("av", "TELEOTHER_LUMBRIDGE"),
    ("aw", "TELEOTHER_FALADOR"),
    ("ax", "TELEOTHER_CAMELOT"),
    ("ba", "RESURRECT_CROPS"),
    ("bb", "HOME_TELEPORT"),
    ("bc", "VARROCK_TELEPORT"),
    ("bd", "LUMBRIDGE_TELEPORT"),
    ("be", "FALADOR_TELEPORT"),
    ("bf", "CAMELOT_TELEPORT"),
    ("bg", "ARDOUGNE_TELEPORT"),
    ("bh", "WATCHTOWER_TELEPORT"),
    ("bi", "TROLLHEIM_TELEPORT"),
    ("bj", "APE_ATOLL_TELEPORT"),
    ("bk", "SPINOLYP_WATER_STRIKE"),
    ("bl", "WALLASALKI_WATER_WAVE"),
    ("bm", "DAGANNOTH_PRIME_WATER_WAVE"),
    ("bn", "SUMMON_ZOMBIE"),
    ("bo", "MELZAR_CABBAGE_SPELL"),
    ("bp", "ABERRANT_SPECTER_MAGIC_ATTACK"),
    ("bq", "INFERNAL_MAGE_FIRE_BLAST"),
    ("br", "CHAOS_ELEMENTAL_DISARM"),
    ("bs", "CHAOS_ELEMENTAL_RANDOM_TELEPORT"),
    ("bt", "JUNGLE_DEMON_WIND_WAVE"),
    ("bu", "JUNGLE_DEMON_WATER_WAVE"),
    ("bv", "JUNGLE_DEMON_EARTH_WAVE"),
    ("bw", "JUNGLE_DEMON_FIRE_WAVE"),
    ("bx", "ZOOKNOCK_WATER_BLAST"),
    ("by", "FIRE_WIZARD_FIRE_STRIKE"),
    ("bz", "WIZARD_FIRE_STRIKE"),
    ("bA", "CHAOS_DRUID_CONFUSE"),
    ("bB", "GROWLER_MAGIC_ATTACK"),
    ("bC", "COMMANDER_ZILYANA_MAGIC_ATTACK"),
    ("bD", "KRIL_TSUTSAROTH_MAGIC_ATTACK"),
    ("bE", "BALFRUG_KREEYATH_MAGIC_ATTACK"),
    ("bF", "KREE_ARRA_MAGIC_ATTACK"),
    ("bG", "SERGEANT_STEELWILL_MAGIC_ATTACK"),
    ("bH", "ARMADYL_SPIRITUAL_MAGE_MAGIC_ATTACK"),
    ("bI", "WINGMAN_SKREE_MAGIC_ATTACK"),
]

for _old_name, _mapped_name in _SPELL_DEFINITION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            "com/rs2/model/skill/magic/SpellDefinition",
            _old_name,
            "Lcom/rs2/model/skill/magic/SpellDefinition;",
        ),
        _mapped_name,
    )

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/ground/GroundItem", "j", "()[I"),
    "getVisibilitySwitchTable",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/ground/GroundItemManager", "b", "()[I"),
    "getVisibilitySwitchTable",
)

_SPELLBOOK_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/skill/magic/Spellbook;", "MODERN"),
    ("b", "Lcom/rs2/model/skill/magic/Spellbook;", "ANCIENT"),
    ("c", "Lcom/rs2/model/skill/magic/Spellbook;", "NECROMANCY"),
    ("d", "Ljava/util/Map;", "spellByButtonId"),
    ("e", "Ljava/util/Map;", "autocastSpellByButtonId"),
    ("f", "[Lcom/rs2/model/skill/magic/Spellbook;", "VALUES"),
]

for _old_name, _descriptor, _mapped_name in _SPELLBOOK_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/magic/Spellbook", _old_name, _descriptor),
        _mapped_name,
    )

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/magic/Spellbook", "a", "(Lcom/rs2/model/player/Player;I)Lcom/rs2/model/skill/magic/SpellDefinition;"),
    "getSpellForButtonId",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/magic/Spellbook", "b", "(Lcom/rs2/model/player/Player;I)Lcom/rs2/model/skill/magic/SpellDefinition;"),
    "getAutocastSpellForButtonId",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/magic/Spellbook", "a", "()Ljava/util/Map;"),
    "getSpellByButtonId",
)

_MAGIC_SPELL_ACTION_FIELD_NAMES = [
    ("a", "[Lcom/rs2/model/combat/requirement/CombatRequirement;", "requirements"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "Lcom/rs2/model/skill/magic/SpellDefinition;", "spell"),
    ("e", "[[I", "superheatItemTable"),
    ("f", "[[I", "enchantJewelryTable"),
    ("g", "[[I", "necromancyReanimationTable"),
]

for _old_name, _descriptor, _mapped_name in _MAGIC_SPELL_ACTION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/magic/MagicSpellAction", _old_name, _descriptor),
        _mapped_name,
    )

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/magic/MagicSpellAction", "a", "(Lcom/rs2/model/skill/magic/MagicSpellAction;)Lcom/rs2/model/player/Player;"),
    "getPlayer",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/magic/MagicSpellAction", "d", "()[[I"),
    "getNecromancyReanimationTable",
)

_MAGIC_SPELL_ACTION_METHOD_NAMES = [
    ("e", "()Z", "validateRequirements"),
    ("f", "()V", "executeImmediateCast"),
    ("b", "()Z", "tryStartCast"),
    ("a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Position;)V", "scheduleDelayedImpact"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;II)V", "castItemSpell"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;IIII)Z", "castObjectSpell"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;)V", "castTeleotherSpell"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;)V", "castSelfSpell"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;ILcom/rs2/model/Position;)V", "scheduleTelekineticGrab"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/ground/GroundItem;Z)V", "continueBotGroundItemLoot"),
    ("b", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;ILcom/rs2/model/Position;)V", "castTelekineticGrab"),
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "handleAutocastButton"),
    ("a", "(I)Z", "castSuperheatItem"),
    ("a", "(II)Z", "castNecromancyReanimation"),
    ("b", "(II)Z", "castEnchantJewelry"),
    ("a", "(Z)Z", "castBonesToFruit"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/TeleotherDestination;)Z", "sendTeleotherRequest"),
    ("a", "(Lcom/rs2/model/player/Player;IIIILcom/rs2/model/skill/magic/SpellDefinition;)Z", "castAlchemyItem"),
    ("a", "(Lcom/rs2/model/skill/magic/MagicSpellAction;Lcom/rs2/model/task/TickTask;Lcom/rs2/model/combat/hit/HitDefinition;)V", "finishDelayedImpact"),
]

for _old_name, _descriptor, _mapped_name in _MAGIC_SPELL_ACTION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/magic/MagicSpellAction", _old_name, _descriptor),
        _mapped_name,
    )

_MAGIC_SPELL_ACTION_OWNERS = [
    "com/rs2/model/skill/magic/MagicSpellAction",
    "com/rs2/model/skill/magic/ItemSpellAction",
    "com/rs2/model/skill/magic/ObjectSpellAction",
    "com/rs2/model/skill/magic/SelfCastSpellAction",
    "com/rs2/model/skill/magic/TelekineticGrabSpellAction",
    "com/rs2/model/skill/magic/TeleotherSpellAction",
]

for _owner in _MAGIC_SPELL_ACTION_OWNERS:
    METHOD_NAME_MAP.setdefault((_owner, "c", "()Z"), "prepareCast")
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Lcom/rs2/model/combat/hit/HitDefinition;)V"),
        "applyImpact",
    )

_MAGIC_SPELL_ACTION_INHERITED_METHOD_NAMES = [
    ("com/rs2/model/skill/magic/ItemSpellAction", "a", "(Lcom/rs2/model/player/Player;IIIILcom/rs2/model/skill/magic/SpellDefinition;)Z", "castAlchemyItem"),
    ("com/rs2/model/skill/magic/ItemSpellAction", "a", "(I)Z", "castSuperheatItem"),
    ("com/rs2/model/skill/magic/ItemSpellAction", "a", "(II)Z", "castNecromancyReanimation"),
    ("com/rs2/model/skill/magic/ItemSpellAction", "b", "(II)Z", "castEnchantJewelry"),
    ("com/rs2/model/skill/magic/SelfCastSpellAction", "a", "(Z)Z", "castBonesToFruit"),
    ("com/rs2/model/skill/magic/TelekineticGrabSpellAction", "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Position;)V", "scheduleDelayedImpact"),
    ("com/rs2/model/skill/magic/TelekineticGrabSpellAction", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/ground/GroundItem;Z)V", "continueBotGroundItemLoot"),
    ("com/rs2/model/skill/magic/TeleotherSpellAction", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/TeleotherDestination;)Z", "sendTeleotherRequest"),
]

for _owner, _old_name, _descriptor, _mapped_name in _MAGIC_SPELL_ACTION_INHERITED_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_MAGIC_SPELL_ACTION_SUBCLASS_FIELDS = [
    ("com/rs2/model/skill/magic/ItemSpellAction", "a", "Lcom/rs2/model/skill/magic/SpellDefinition;", "itemSpell"),
    ("com/rs2/model/skill/magic/ItemSpellAction", "b", "I", "itemId"),
    ("com/rs2/model/skill/magic/ItemSpellAction", "c", "Lcom/rs2/model/player/Player;", "caster"),
    ("com/rs2/model/skill/magic/ItemSpellAction", "d", "I", "inventorySlot"),
    ("com/rs2/model/skill/magic/ObjectSpellAction", "a", "I", "objectId"),
    ("com/rs2/model/skill/magic/ObjectSpellAction", "b", "I", "objectX"),
    ("com/rs2/model/skill/magic/ObjectSpellAction", "c", "I", "objectY"),
    ("com/rs2/model/skill/magic/ObjectSpellAction", "d", "I", "objectPlane"),
    ("com/rs2/model/skill/magic/ObjectSpellAction", "e", "Lcom/rs2/model/skill/magic/SpellDefinition;", "objectSpell"),
    ("com/rs2/model/skill/magic/SelfCastSpellAction", "a", "Lcom/rs2/model/skill/magic/SpellDefinition;", "selfSpell"),
    ("com/rs2/model/skill/magic/SelfCastSpellAction", "b", "Lcom/rs2/model/player/Player;", "caster"),
    ("com/rs2/model/skill/magic/TelekineticGrabSpellAction", "a", "Lcom/rs2/model/ground/GroundItem;", "groundItem"),
    ("com/rs2/model/skill/magic/TelekineticGrabSpellAction", "b", "Lcom/rs2/model/skill/magic/SpellDefinition;", "telegrabSpell"),
    ("com/rs2/model/skill/magic/TelekineticGrabSpellAction", "c", "Lcom/rs2/model/player/Player;", "caster"),
    ("com/rs2/model/skill/magic/TelekineticGrabSpellAction", "d", "Lcom/rs2/model/Position;", "targetPosition"),
    ("com/rs2/model/skill/magic/TelekineticGrabSpellAction", "f", "I", "itemId"),
    ("com/rs2/model/skill/magic/TelekineticGrabTask", "a", "Lcom/rs2/model/player/Player;", "caster"),
    ("com/rs2/model/skill/magic/TelekineticGrabTask", "b", "I", "actionSequence"),
    ("com/rs2/model/skill/magic/TelekineticGrabTask", "c", "I", "itemId"),
    ("com/rs2/model/skill/magic/TelekineticGrabTask", "d", "Lcom/rs2/model/Position;", "targetPosition"),
    ("com/rs2/model/skill/magic/TelekineticGrabTask", "e", "Lcom/rs2/model/skill/magic/SpellDefinition;", "telegrabSpell"),
    ("com/rs2/model/skill/magic/TeleotherSpellAction", "a", "Lcom/rs2/model/skill/magic/SpellDefinition;", "teleotherSpell"),
    ("com/rs2/model/skill/magic/TeleotherSpellAction", "b", "Lcom/rs2/model/player/Player;", "target"),
    ("com/rs2/model/skill/magic/TeleotherSpellAction", "c", "Lcom/rs2/model/player/Player;", "caster"),
]

for _owner, _old_name, _descriptor, _mapped_name in _MAGIC_SPELL_ACTION_SUBCLASS_FIELDS:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_TELEOTHER_DESTINATION_FIELDS = [
    ("a", "Lcom/rs2/model/skill/magic/TeleotherDestination;", "CAMELOT"),
    ("b", "Lcom/rs2/model/skill/magic/TeleotherDestination;", "FALADOR"),
    ("c", "Lcom/rs2/model/skill/magic/TeleotherDestination;", "LUMBRIDGE"),
    ("d", "Lcom/rs2/model/Position;", "position"),
    ("e", "Ljava/lang/String;", "displayName"),
    ("f", "[Lcom/rs2/model/skill/magic/TeleotherDestination;", "VALUES"),
]

for _old_name, _descriptor, _mapped_name in _TELEOTHER_DESTINATION_FIELDS:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/magic/TeleotherDestination", _old_name, _descriptor),
        _mapped_name,
    )

_BOT_COMBAT_LOADOUT_TABLE_FIELDS = [
    ("a", "[I", "enabledCombatLoadoutTypes"),
    ("b", "[I", "capeIds"),
    ("c", "[I", "teamCapeIds"),
    ("d", "[I", "castleWarsCapeIds"),
    ("e", "[I", "post306BowIds"),
    ("f", "[I", "post306ArrowIds"),
    ("g", "[I", "post306HighTierBowIds"),
    ("h", "[I", "post306HighTierArrowIds"),
    ("i", "[I", "legacyBowIds"),
    ("j", "[I", "legacyArrowIds"),
    ("k", "[I", "legacyHighTierBowIds"),
    ("l", "[I", "legacyHighTierArrowIds"),
    ("m", "[I", "daggerIds"),
    ("n", "[I", "specialMeleeWeaponIds"),
    ("o", "[I", "swordIds"),
    ("p", "[I", "longswordIds"),
    ("q", "[I", "dragonLongswordIds"),
    ("r", "[I", "scimitarIds"),
    ("s", "[I", "dragonScimitarIds"),
    ("t", "[I", "battleaxeIds"),
    ("u", "[I", "twoHandedSwordIds"),
    ("v", "[I", "warhammerIds"),
    ("w", "[I", "maceIds"),
    ("x", "[I", "basicRangedHeadIds"),
    ("y", "[I", "rareRangedHeadIds"),
    ("z", "[I", "basicRangedBodyIds"),
    ("A", "[I", "dragonhideBodyIds"),
    ("B", "[I", "basicRangedLegIds"),
    ("C", "[I", "dragonhideChapsIds"),
    ("D", "[I", "basicRangedVambraceIds"),
    ("E", "[I", "dragonhideVambraceIds"),
    ("F", "[I", "mageHeadIds"),
    ("G", "[I", "mageBodyIds"),
    ("H", "[I", "mageLegIds"),
    ("I", "[I", "mageGloveIds"),
    ("J", "[I", "mageBootIds"),
    ("K", "[I", "coloredMageHeadIds"),
    ("L", "[I", "coloredMageBodyIds"),
    ("M", "[I", "coloredMageLegIds"),
    ("N", "[I", "coloredMageGloveIds"),
    ("O", "[I", "coloredMageBootIds"),
    ("P", "[I", "rareMeleeHelmetIds"),
    ("Q", "[I", "mediumHelmetIds"),
    ("R", "[I", "fullHelmetIds"),
    ("S", "[I", "chainbodyIds"),
    ("T", "[I", "platebodyIds"),
    ("U", "[I", "platelegIds"),
    ("V", "[I", "plateskirtIds"),
    ("W", "[I", "squareShieldIds"),
    ("X", "[I", "kiteshieldIds"),
    ("Y", "[I", "goldTrimmedFullHelmetIds"),
    ("Z", "[I", "goldTrimmedPlatebodyIds"),
    ("aa", "[I", "goldTrimmedPlatelegIds"),
    ("ab", "[I", "goldTrimmedPlateskirtIds"),
    ("ac", "[I", "goldTrimmedKiteshieldIds"),
    ("ad", "[I", "trimmedFullHelmetIds"),
    ("ae", "[I", "trimmedPlatebodyIds"),
    ("af", "[I", "trimmedPlatelegIds"),
    ("ag", "[I", "trimmedPlateskirtIds"),
    ("ah", "[I", "trimmedKiteshieldIds"),
    ("ai", "[I", "godFullHelmetIds"),
    ("aj", "[I", "godPlatebodyIds"),
    ("ak", "[I", "godPlatelegIds"),
    ("al", "[I", "godPlateskirtIds"),
    ("am", "[I", "godKiteshieldIds"),
    ("an", "[Lcom/rs2/model/skill/magic/SpellDefinition;", "elementalStrikeSpells"),
    ("ao", "[Lcom/rs2/model/skill/magic/SpellDefinition;", "standardCombatSpellProgression"),
    ("ap", "[Lcom/rs2/model/skill/magic/SpellDefinition;", "standardWaveSpellProgression"),
    ("aq", "[Lcom/rs2/model/skill/magic/SpellDefinition;", "ancientCombatSpellProgression"),
]

for _old_name, _descriptor, _mapped_name in _BOT_COMBAT_LOADOUT_TABLE_FIELDS:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/bot/combat/BotCombatLoadoutTables", _old_name, _descriptor),
        _mapped_name,
    )

_WEAPON_SPECIAL_ATTACK_OWNERS = [
    "com/rs2/model/combat/special/AbyssalWhipSpecialAttack",
    "com/rs2/model/combat/special/ArmadylGodswordSpecialAttack",
    "com/rs2/model/combat/special/BandosGodswordSpecialAttack",
    "com/rs2/model/combat/special/DarkBowSpecialAttack",
    "com/rs2/model/combat/special/DarklightSpecialAttack",
    "com/rs2/model/combat/special/Dragon2hSwordSpecialAttack",
    "com/rs2/model/combat/special/DragonAxeSpecialAttack",
    "com/rs2/model/combat/special/DragonDaggerSpecialAttack",
    "com/rs2/model/combat/special/DragonHalberdSpecialAttack",
    "com/rs2/model/combat/special/DragonLongswordSpecialAttack",
    "com/rs2/model/combat/special/DragonMaceSpecialAttack",
    "com/rs2/model/combat/special/DragonScimitarSpecialAttack",
    "com/rs2/model/combat/special/DragonSpearSpecialAttack",
    "com/rs2/model/combat/special/GraniteMaulSpecialAttack",
    "com/rs2/model/combat/special/MagicLongbowSpecialAttack",
    "com/rs2/model/combat/special/MagicShortbowSpecialAttack",
    "com/rs2/model/combat/special/RuneClawsSpecialAttack",
    "com/rs2/model/combat/special/RuneThrownaxeSpecialAttack",
    "com/rs2/model/combat/special/SaradominGodswordSpecialAttack",
    "com/rs2/model/combat/special/SaradominSwordSpecialAttack",
    "com/rs2/model/combat/special/SeercullSpecialAttack",
    "com/rs2/model/combat/special/ZamorakGodswordSpecialAttack",
]

_COMBAT_ATTACK_INHERITED_REF_OWNERS = [
    "com/rs2/model/combat/attack/CombatAttack",
    "com/rs2/model/combat/attack/BaseCombatAttack",
    "com/rs2/model/combat/attack/WeaponCombatAttack",
    "com/rs2/model/combat/attack/MagicCombatAttack",
    "com/rs2/model/combat/attack/MeleeCombatAttack",
    "com/rs2/model/combat/attack/ForcedMeleeCombatAttack",
    "com/rs2/model/combat/attack/ProjectileCombatAttack",
    "com/rs2/model/combat/attack/ForcedProjectileCombatAttack",
    "com/rs2/model/combat/attack/FlaggedProjectileCombatAttack",
    *_WEAPON_SPECIAL_ATTACK_OWNERS,
]

for _owner in _COMBAT_ATTACK_INHERITED_REF_OWNERS:
    METHOD_NAME_MAP.setdefault((_owner, "a", "()Lcom/rs2/model/Entity;"), "getAttacker")
    METHOD_NAME_MAP.setdefault((_owner, "b", "()Lcom/rs2/model/Entity;"), "getTarget")

_BASE_COMBAT_ATTACK_INHERITED_REF_OWNERS = [
    "com/rs2/model/combat/attack/BaseCombatAttack",
    "com/rs2/model/combat/attack/WeaponCombatAttack",
    "com/rs2/model/combat/attack/MagicCombatAttack",
    "com/rs2/model/combat/attack/MeleeCombatAttack",
    "com/rs2/model/combat/attack/ForcedMeleeCombatAttack",
    "com/rs2/model/combat/attack/ProjectileCombatAttack",
    "com/rs2/model/combat/attack/ForcedProjectileCombatAttack",
    "com/rs2/model/combat/attack/FlaggedProjectileCombatAttack",
    *_WEAPON_SPECIAL_ATTACK_OWNERS,
]

for _owner in _BASE_COMBAT_ATTACK_INHERITED_REF_OWNERS:
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "([Lcom/rs2/model/combat/requirement/CombatRequirement;)V"),
        "setRequirements",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "([Lcom/rs2/model/combat/hit/HitDefinition;)V"),
        "setHitDefinitions",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "h", "()[Lcom/rs2/model/combat/hit/HitDefinition;"),
        "getHitDefinitions",
    )
    METHOD_NAME_MAP.setdefault((_owner, "a", "(I)V"), "setAttackDelay")
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Lcom/rs2/model/animation/GraphicEffect;)V"),
        "setAttackerGraphic",
    )
    METHOD_NAME_MAP.setdefault((_owner, "b", "(I)V"), "setAnimationId")
    METHOD_NAME_MAP.setdefault((_owner, "c", "(I)V"), "setAttackSoundId")
    METHOD_NAME_MAP.setdefault(
        (
            _owner,
            "a",
            "(Lcom/rs2/model/combat/effect/CombatEffect;)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        ),
        "addEffect",
    )

_BASE_COMBAT_ATTACK_FACTORY_METHOD_NAMES = [
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/AttackXpMode;Lcom/rs2/model/combat/AttackBonusType;IIIZ)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createForcedMeleeAttack",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/AttackXpMode;Lcom/rs2/model/combat/AttackBonusType;III)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createMeleeAttack",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/AttackXpMode;ILcom/rs2/model/combat/WeaponProfile;)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createWeaponProfileMeleeAttack",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/CombatType;Lcom/rs2/model/combat/AttackXpMode;IIILcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/animation/GraphicEffect;ILcom/rs2/model/combat/ProjectileTiming;ILcom/rs2/model/combat/effect/CombatEffect;)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createProjectileAttackInternal",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/CombatType;Lcom/rs2/model/combat/AttackXpMode;IIILcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/animation/GraphicEffect;ILcom/rs2/model/combat/ProjectileTiming;ILcom/rs2/model/combat/effect/CombatEffect;Z)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createForcedProjectileAttackInternal",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/CombatType;Lcom/rs2/model/combat/AttackXpMode;IIILcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/animation/GraphicEffect;ILcom/rs2/model/combat/ProjectileTiming;ZZ)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createFlaggedProjectileAttack",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/CombatType;Lcom/rs2/model/combat/AttackXpMode;IIILcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/animation/GraphicEffect;ILcom/rs2/model/combat/ProjectileTiming;)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createProjectileAttack",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/CombatType;Lcom/rs2/model/combat/AttackXpMode;IIILcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/animation/GraphicEffect;ILcom/rs2/model/combat/ProjectileTiming;Z)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createKalphiteQueenMagicAttack",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/CombatType;Lcom/rs2/model/combat/AttackXpMode;IIILcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/animation/GraphicEffect;ILcom/rs2/model/combat/ProjectileTiming;Lcom/rs2/model/combat/effect/CombatEffect;)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createProjectileAttackWithEffect",
    ),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;Lcom/rs2/model/combat/CombatType;Lcom/rs2/model/combat/AttackXpMode;IIILcom/rs2/model/animation/GraphicEffect;Lcom/rs2/model/animation/GraphicEffect;ILcom/rs2/model/combat/ProjectileTiming;Lcom/rs2/model/combat/effect/CombatEffect;Z)Lcom/rs2/model/combat/attack/BaseCombatAttack;",
        "createKalphiteQueenRangedAttackWithEffect",
    ),
]
for _old_name, _descriptor, _mapped_name in _BASE_COMBAT_ATTACK_FACTORY_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/combat/attack/BaseCombatAttack", _old_name, _descriptor),
        _mapped_name,
    )

for _owner in ["com/rs2/model/combat/attack/WeaponCombatAttack", *_WEAPON_SPECIAL_ATTACK_OWNERS]:
    METHOD_NAME_MAP.setdefault(
        (_owner, "i", "()Lcom/rs2/model/combat/AmmunitionDefinition;"),
        "getAmmunition",
    )
    METHOD_NAME_MAP.setdefault((_owner, "j", "()D"), "calculateMaxHit")
    METHOD_NAME_MAP.setdefault(
        (_owner, "k", "()Lcom/rs2/model/combat/AttackStyleDefinition;"),
        "getAttackStyle",
    )

_COMBAT_REQUIREMENT_MESSAGE_OWNERS = [
    "com/rs2/model/combat/requirement/CombatRequirement",
    "com/rs2/model/combat/requirement/MagicCombatMembersRequirement",
    "com/rs2/model/combat/requirement/MagicCombatRuneRequirement",
    "com/rs2/model/combat/requirement/MagicCombatLevelRequirement",
    "com/rs2/model/combat/requirement/GodStaffRequirement",
    "com/rs2/model/combat/requirement/AmmunitionRequirement",
    "com/rs2/model/skill/magic/MagicMembersAccountRequirement",
    "com/rs2/model/skill/magic/RuneRequirement",
    "com/rs2/model/skill/magic/MagicLevelRequirement",
    "com/rs2/model/combat/special/SeercullArrowRequirement",
    "com/rs2/model/combat/special/DarkBowArrowRequirement",
    "com/rs2/model/combat/special/MagicShortbowArrowRequirement",
    "com/rs2/model/combat/special/MagicLongbowArrowRequirement",
]

for _owner in _COMBAT_REQUIREMENT_MESSAGE_OWNERS:
    METHOD_NAME_MAP.setdefault((_owner, "a", "()Ljava/lang/String;"), "getFailureMessage")

_COMBAT_REQUIREMENT_CHECK_OWNERS = [
    "com/rs2/model/combat/requirement/CombatRequirement",
    "com/rs2/model/combat/requirement/MembersRequirement",
    "com/rs2/model/combat/requirement/SkillLevelRequirement",
    "com/rs2/model/combat/requirement/EquipmentItemRequirement",
    "com/rs2/model/combat/requirement/InventoryItemRequirement",
    "com/rs2/model/combat/requirement/SpellRuneCostRequirement",
]

for _owner in _COMBAT_REQUIREMENT_CHECK_OWNERS:
    METHOD_NAME_MAP.setdefault((_owner, "b", "(Lcom/rs2/model/Entity;)Z"), "isSatisfiedBy")

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/combat/requirement/CombatRequirement", "c", "(Lcom/rs2/model/Entity;)Z"),
    "validate",
)

for _owner in [
    "com/rs2/model/combat/requirement/CombatCostRequirement",
    "com/rs2/model/combat/requirement/EquipmentItemRequirement",
    "com/rs2/model/combat/requirement/InventoryItemRequirement",
    "com/rs2/model/combat/requirement/SpellRuneCostRequirement",
]:
    METHOD_NAME_MAP.setdefault((_owner, "a", "(Lcom/rs2/model/Entity;)V"), "consume")

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/combat/requirement/InventoryItemRequirement", "a", "(Ljava/util/ArrayList;)V"),
    "setRequiredItems",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/combat/requirement/SpellRuneCostRequirement",
        "a",
        "(Lcom/rs2/model/player/Player;[Lcom/rs2/model/item/ItemStack;)Ljava/util/ArrayList;",
    ),
    "buildRuneCosts",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/combat/requirement/SpellRuneCostRequirement", "a", "(II)I"),
    "getPairedCombinationRuneId",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/combat/requirement/SpellRuneCostRequirement", "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "collectCombinationRuneCosts",
)

METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/npc/combat/NpcCombatDefinition",
        "a",
        "(IIIII)Lcom/rs2/model/npc/combat/NpcCombatDefinition;",
    ),
    "addAttackBonuses",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/npc/combat/NpcCombatDefinition",
        "b",
        "(IIIII)Lcom/rs2/model/npc/combat/NpcCombatDefinition;",
    ),
    "addDefenceBonuses",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "a", "(I)Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
    "setRespawnDelayTicks",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "b", "(I)Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
    "setRespawnDelaySeconds",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/combat/NpcCombatDefinition", "a", "()I"), "getDeathDelayTicks")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/combat/NpcCombatDefinition", "b", "()I"), "getRespawnDelayTicks")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/combat/NpcCombatDefinition", "c", "()Ljava/util/Map;"), "getCombatBonuses")
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/npc/combat/NpcCombatDefinition",
        "a",
        "([ILcom/rs2/model/npc/combat/NpcCombatDefinition;)V",
    ),
    "register",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/combat/NpcCombatDefinition", "c", "(I)Z"), "isRegistered")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/combat/NpcCombatDefinition", "d", "(I)Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
    "forNpcId",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/combat/NpcCombatDefinition", "d", "()Z"), "hasAttackBonuses")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/combat/NpcCombatDefinition", "e", "()Z"), "hasDefenceBonuses")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/Npc", "bm", "()Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
    "getCombatDefinition",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/Npc", "bn", "()Lcom/rs2/model/npc/NpcDefinition;"),
    "getDefinition",
)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/npc/Npc", "c", "()V", "process"),
    ("com/rs2/model/npc/Npc", "h", "()I", "getNpcId"),
    ("com/rs2/model/npc/Npc", "i", "()I", "getOriginalNpcId"),
    ("com/rs2/model/npc/Npc", "j", "(Lcom/rs2/model/Entity;)V", "dropLootForKiller"),
    ("com/rs2/model/npc/Npc", "d", "()V", "resetAfterUpdate"),
    ("com/rs2/model/npc/Npc", "e", "(II)V", "transformToNpcId"),
    ("com/rs2/model/npc/Npc", "a", "(III)V", "transformToNpcIdWithAnimation"),
    ("com/rs2/model/npc/Npc", "aP", "()Z", "isTransformed"),
    ("com/rs2/model/npc/Npc", "aQ", "()I", "getTransformedNpcId"),
    ("com/rs2/model/npc/Npc", "bp", "()I", "getTransformTicksRemaining"),
    ("com/rs2/model/npc/Npc", "aR", "()I", "getRespawnDelayTicks"),
    ("com/rs2/model/npc/Npc", "g", "(Z)V", "setRespawnEnabled"),
    ("com/rs2/model/npc/Npc", "aS", "()Z", "isRespawnEnabled"),
    ("com/rs2/model/npc/Npc", "f", "(Z)V", "setActive"),
    ("com/rs2/model/npc/Npc", "aK", "()Z", "isActive"),
    ("com/rs2/model/npc/Npc", "t", "(I)V", "setOwnerPlayerIndex"),
    ("com/rs2/model/npc/Npc", "aL", "()Lcom/rs2/model/player/Player;", "getOwnerPlayer"),
    ("com/rs2/model/npc/Npc", "d", "(Lcom/rs2/model/Position;)V", "setSpawnMinPosition"),
    ("com/rs2/model/npc/Npc", "aM", "()Lcom/rs2/model/Position;", "getSpawnMinPosition"),
    ("com/rs2/model/npc/Npc", "e", "(Lcom/rs2/model/Position;)V", "setSpawnMaxPosition"),
    ("com/rs2/model/npc/Npc", "aN", "()Lcom/rs2/model/Position;", "getSpawnMaxPosition"),
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/npc/NpcMovementMode;)V", "setMovementMode"),
    ("com/rs2/model/npc/Npc", "aO", "()Lcom/rs2/model/npc/NpcMovementMode;", "getMovementMode"),
    ("com/rs2/model/npc/Npc", "f", "(Lcom/rs2/model/Position;)V", "setSpawnPosition"),
    ("com/rs2/model/npc/Npc", "aT", "()Lcom/rs2/model/Position;", "getSpawnPosition"),
    ("com/rs2/model/npc/Npc", "u", "(I)V", "setSpawnX"),
    ("com/rs2/model/npc/Npc", "aU", "()I", "getSpawnX"),
    ("com/rs2/model/npc/Npc", "v", "(I)V", "setSpawnY"),
    ("com/rs2/model/npc/Npc", "aV", "()I", "getSpawnY"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/npc/Npc", "bs", "()V", "processStatRestoration"),
    ("com/rs2/model/npc/Npc", "o", "(I)Z", "isStatModified"),
    ("com/rs2/model/npc/Npc", "F", "(I)I", "getStatRestoreTaskSlot"),
    ("com/rs2/model/npc/Npc", "d", "(II)V", "adjustCurrentLevel"),
    ("com/rs2/model/npc/Npc", "p", "(I)I", "getCurrentLevelForSkill"),
    ("com/rs2/model/npc/Npc", "q", "(I)I", "getBaseLevelForSkill"),
    ("com/rs2/model/npc/Npc", "e", "()V", "resetCombatLevels"),
    ("com/rs2/model/npc/Npc", "bc", "()I", "getCurrentAttackLevel"),
    ("com/rs2/model/npc/Npc", "bd", "()I", "getCurrentStrengthLevel"),
    ("com/rs2/model/npc/Npc", "aZ", "()I", "getCurrentDefenceLevel"),
    ("com/rs2/model/npc/Npc", "ba", "()I", "getCurrentMagicLevel"),
    ("com/rs2/model/npc/Npc", "bb", "()I", "getCurrentRangedLevel"),
    ("com/rs2/model/npc/Npc", "bh", "()I", "getBaseAttackLevel"),
    ("com/rs2/model/npc/Npc", "bi", "()I", "getBaseStrengthLevel"),
    ("com/rs2/model/npc/Npc", "be", "()I", "getBaseDefenceLevel"),
    ("com/rs2/model/npc/Npc", "bf", "()I", "getBaseMagicLevel"),
    ("com/rs2/model/npc/Npc", "bg", "()I", "getBaseRangedLevel"),
    ("com/rs2/model/npc/Npc", "x", "(I)V", "setCurrentAttackLevel"),
    ("com/rs2/model/npc/Npc", "y", "(I)V", "setCurrentStrengthLevel"),
    ("com/rs2/model/npc/Npc", "z", "(I)V", "setCurrentDefenceLevel"),
    ("com/rs2/model/npc/Npc", "A", "(I)V", "setCurrentMagicLevel"),
    ("com/rs2/model/npc/Npc", "B", "(I)V", "setCurrentRangedLevel"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/npc/Npc", "aW", "()I", "getAttackSoundId"),
    ("com/rs2/model/npc/Npc", "aX", "()I", "getHitSoundId"),
    ("com/rs2/model/npc/Npc", "aY", "()I", "getDeathSoundId"),
    ("com/rs2/model/npc/Npc", "w", "(I)I", "getDefenceBonus"),
    ("com/rs2/model/npc/Npc", "bj", "()I", "getMeleeAttackBonus"),
    ("com/rs2/model/npc/Npc", "bk", "()I", "getMagicAttackBonus"),
    ("com/rs2/model/npc/Npc", "bl", "()I", "getRangedAttackBonus"),
    ("com/rs2/model/npc/NpcDefinition", "f", "()I", "getHitSoundId"),
    ("com/rs2/model/npc/NpcDefinition", "g", "()I", "getAttackSoundId"),
    ("com/rs2/model/npc/NpcDefinition", "i", "()I", "getDeathSoundId"),
    ("com/rs2/model/npc/NpcDefinition", "d", "(I)I", "getDefenceBonus"),
    ("com/rs2/model/npc/NpcDefinition", "B", "()I", "getMeleeAttackBonus"),
    ("com/rs2/model/npc/NpcDefinition", "C", "()I", "getMeleeStrengthBonus"),
    ("com/rs2/model/npc/NpcDefinition", "D", "()I", "getMagicAttackBonus"),
    ("com/rs2/model/npc/NpcDefinition", "E", "()I", "getRangedAttackBonus"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/npc/Npc", "a", "(I)V", "setRemovalDelayTicks"),
    ("com/rs2/model/npc/Npc", "a", "()Z", "isFaceEntityUpdateDisabled"),
    ("com/rs2/model/npc/Npc", "e", "(Z)V", "setFaceEntityUpdateDisabled"),
    ("com/rs2/model/npc/Npc", "b", "()Lcom/rs2/model/Entity;", "getForcedCombatTarget"),
    ("com/rs2/model/npc/Npc", "h", "(Lcom/rs2/model/Entity;)V", "setForcedCombatTarget"),
    ("com/rs2/model/npc/Npc", "bo", "()I", "getFacingDirection"),
    ("com/rs2/model/npc/Npc", "C", "(I)V", "setFacingDirection"),
    ("com/rs2/model/npc/Npc", "bq", "()Z", "isInteractable"),
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/Position;I)Z", "isFacingInteractionPosition"),
    ("com/rs2/model/npc/Npc", "b", "(Lcom/rs2/model/Position;I)Z", "isWithinInteractionDistance"),
    ("com/rs2/model/npc/Npc", "E", "(I)Lcom/rs2/model/Position;", "getFacingInteractionPosition"),
    ("com/rs2/model/npc/Npc", "br", "()Z", "isBanker"),
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/player/Player;IIILjava/lang/String;)V", "startDialogueTeleport"),
    ("com/rs2/model/npc/Npc", "b", "(Lcom/rs2/model/player/Player;IIILjava/lang/String;)V", "startMageArenaChallenge"),
    ("com/rs2/model/npc/Npc", "c", "(Lcom/rs2/model/player/Player;IIILjava/lang/String;)V", "startAbyssMageTeleport"),
    (
        "com/rs2/model/npc/Npc",
        "a",
        "(Lcom/rs2/model/player/Player;IIIIILjava/lang/String;Z)V",
        "startNpcRelocation",
    ),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/npc/Npc", "b", "([Lcom/rs2/model/Position;)V", "startWaypointLoop"),
    ("com/rs2/model/npc/Npc", "a", "([Lcom/rs2/model/Position;)V", "queueScriptedPath"),
    ("com/rs2/model/npc/Npc", "r", "(I)V", "queueStageAdvancePath"),
    ("com/rs2/model/npc/Npc", "s", "(I)V", "queueSequenceAdvancePath"),
    ("com/rs2/model/npc/Npc", "f", "()I", "getLastStepFacingDirection"),
    ("com/rs2/model/npc/Npc", "g", "()I", "getWaypointFacingDirection"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/npc/Npc", "D", "(I)Lcom/rs2/model/npc/Npc;", "findByDefinitionId"),
    (
        "com/rs2/model/npc/Npc",
        "a",
        "(Lcom/rs2/util/RectangularArea;)[Lcom/rs2/model/npc/Npc;",
        "findActiveInArea",
    ),
    (
        "com/rs2/model/npc/Npc",
        "a",
        "(ILcom/rs2/model/Position;)Lcom/rs2/model/npc/Npc;",
        "findByDefinitionIdAtPosition",
    ),
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/player/Player;)V", "refreshNearbyTransformedNpcs"),
    ("com/rs2/model/npc/Npc", "f", "(II)Z", "canTraverseStep"),
    ("com/rs2/model/npc/Npc", "g", "(II)Z", "wouldCollideWithNpc"),
    ("com/rs2/model/npc/Npc", "a", "(Lcom/rs2/model/npc/Npc;Lcom/rs2/model/player/Player;II)Z", "wouldCollideWithPlayer"),
    ("com/rs2/model/npc/Npc", "i", "(Lcom/rs2/model/Entity;)Z", "isUndead"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "a", "()V"), "loadDefinitions")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "a", "(I)Z"), "isDefined")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/NpcDefinition", "b", "(I)Lcom/rs2/model/npc/NpcDefinition;"),
    "forId",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/NpcDefinition", "e", "(I)Lcom/rs2/model/npc/NpcDefinition;"),
    "createFallback",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "a", "(II)V"), "copyDefinition")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "J", "()V"), "initializeCombatDefinitions")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "b", "()Z"), "isProtectedFromMelee")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "c", "()Z"), "isProtectedFromRanged")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "d", "()Z"), "isProtectedFromMagic")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "e", "()I"), "getId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "m", "()Ljava/lang/String;"), "getName")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "q", "()I"), "getCombatLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "r", "()I"), "getSize")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "t", "()I"), "getHitpoints")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "u", "()I"), "getMaxHit")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "y", "()I"), "getAttackLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "z", "()I"), "getStrengthLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "v", "()I"), "getDefenceLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "w", "()I"), "getMagicLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "x", "()I"), "getRangedLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "p", "()I"), "getAttackAnimationId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "o", "()I"), "getBlockAnimationId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "n", "()I"), "getDeathAnimationId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "j", "()I"), "getPoisonDamage")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "k", "()D"), "getPoisonChance")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "l", "()I"), "getShopId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "G", "()I"), "getSpawnRadius")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "H", "()I"), "getChaseRadius")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "I", "()Z"), "isAttackable")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "s", "()I"), "getAggressionType")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "F", "()I"), "getAggressionRange")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "A", "()I"), "getRespawnDelayTicks")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "h", "()I"), "getDropTableNpcIdOverride")
METHOD_NAME_MAP.setdefault(("com/rs2/model/npc/NpcDefinition", "c", "(I)V"), "setDropTableNpcIdOverride")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/NpcDefinition", "a", "(Lcom/rs2/model/npc/NpcDefinition;)I"),
    "getAttackBonusTypeId",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/NpcDefinition", "b", "(Lcom/rs2/model/npc/NpcDefinition;)I"),
    "getDefaultMaxHit",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/NpcDefinition", "c", "(Lcom/rs2/model/npc/NpcDefinition;)I"),
    "getDefaultAttackDelay",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/npc/NpcDefinition", "d", "(Lcom/rs2/model/npc/NpcDefinition;)I"),
    "getDefaultAttackAnimationId",
)

METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/IncomingPacket", "a", "()I"), "getOpcode")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/IncomingPacket", "b", "()I"), "getLength")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/IncomingPacket", "c", "()Lcom/rs2/net/packet/PacketReader;"), "getReader")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketDispatcher", "a", "()V"), "registerHandlers")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/net/packet/PacketDispatcher", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/net/packet/IncomingPacket;)V"),
    "dispatchPacket",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/net/packet/PacketDispatcher", "a", "(Lcom/rs2/model/player/Player;)V"),
    "flushOutgoing",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/net/packet/PacketDispatcher", "b", "(Lcom/rs2/model/player/Player;)V"),
    "processIncoming",
)

_PACKET_HANDLER_OWNERS = [
    "com/rs2/net/packet/PacketHandler",
    "com/rs2/net/packet/handler/AppearancePacketHandler",
    "com/rs2/net/packet/handler/ButtonClickPacketHandler",
    "com/rs2/net/packet/handler/CameraPacketHandler",
    "com/rs2/net/packet/handler/ChatSettingsPacketHandler",
    "com/rs2/net/packet/handler/CloseInterfacePacketHandler",
    "com/rs2/net/packet/handler/CommandPacketHandler",
    "com/rs2/net/packet/handler/IdlePacketHandler",
    "com/rs2/net/packet/handler/IgnoredBytePacketHandler",
    "com/rs2/net/packet/handler/InterfaceInputPacketHandler",
    "com/rs2/net/packet/handler/ItemActionPacketHandler",
    "com/rs2/net/packet/handler/ItemSpawnPacketHandler",
    "com/rs2/net/packet/handler/MovementPacketHandler",
    "com/rs2/net/packet/handler/NoOpPacketHandler",
    "com/rs2/net/packet/handler/NpcInteractionPacketHandler",
    "com/rs2/net/packet/handler/ObjectInteractionPacketHandler",
    "com/rs2/net/packet/handler/PlayerInteractionPacketHandler",
    "com/rs2/net/packet/handler/PublicChatPacketHandler",
    "com/rs2/net/packet/handler/QuestJournalPacketHandler",
    "com/rs2/net/packet/handler/RegionLoadPacketHandler",
    "com/rs2/net/packet/handler/ReportAbusePacketHandler",
    "com/rs2/net/packet/handler/SkillMenuPacketHandler",
    "com/rs2/net/packet/handler/SocialPacketHandler",
]

for _owner in _PACKET_HANDLER_OWNERS:
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/net/packet/IncomingPacket;)V"),
        "handle",
    )

METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bd", "()V"), "resetAppearance")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "be", "()V"), "applyDefaultMaleAppearance")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bh", "()V"), "randomizeAppearance")
METHOD_NAME_MAP.setdefault(("com/rs2/bot/BotPlayer", "bh", "()V"), "randomizeAppearance")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "h", "(Z)V"), "flagAppearanceUpdate")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bC", "()[I"), "getAppearanceParts")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bD", "()[I"), "getAppearanceColors")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "z", "(I)V"), "setGender")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bE", "()I"), "getGender")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bF", "()Ljava/util/List;"), "getLocalPlayers")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bG", "()Ljava/util/List;"), "getLocalNpcs")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aK", "()V"), "clearTemporaryCutsceneNpcs")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "o", "(I)Lcom/rs2/model/npc/Npc;"), "findTemporaryCutsceneNpc")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "p", "(I)V"), "spawnTenthSquadSigilNpcs")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Ljava/lang/String;I)V"), "setPendingCropResurrectionTarget")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aS", "()V"), "clearPendingCropResurrectionTarget")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/net/packet/handler/AppearancePacketHandler", "a", "(Lcom/rs2/model/player/Player;)V"),
    "validateAppearance",
)

METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aW", "()I"), "getPrivateChatMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "s", "(I)V"), "setPrivateChatMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aV", "()I"), "getTemporaryActionValue")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "r", "(I)V"), "setTemporaryActionValue")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aX", "()I"), "getPublicChatMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "t", "(I)V"), "setPublicChatMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aY", "()I"), "getTradeMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "u", "(I)V"), "setTradeMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bV", "()[J"), "getFriendsList")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bX", "()[J"), "getIgnoreList")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dS", "()J"), "getNameHash")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "b", "(J)V"), "setNameHash")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "do", "()[Z"), "getActivePrayers")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bg", "()V"), "completeAllQuestStates")
METHOD_NAME_MAP.setdefault(("com/rs2/bot/BotPlayer", "bg", "()V"), "completeAllQuestStates")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bf", "()V"), "dispatchCurrentPacket")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bn", "()V"), "sendLoginResponse")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bq", "()V"), "processPostLogin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "br", "()Z"), "loadAndValidateLogin")
METHOD_NAME_MAP.setdefault(("com/rs2/bot/BotPlayer", "br", "()Z"), "loadAndValidateLogin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "g", "()Z"), "validateLocalLogin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "A", "(I)V"), "setLoginResponseCode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "an", "(I)V"), "setClientBuild")
METHOD_NAME_MAP.setdefault(("com/rs2/bot/BotPlayer", "an", "(I)V"), "setClientBuild")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ao", "(I)V"), "setLoginMagicByte")
METHOD_NAME_MAP.setdefault(("com/rs2/bot/BotPlayer", "ao", "(I)V"), "setLoginMagicByte")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bm", "()V"), "refreshRegionState")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "df", "()Lcom/rs2/net/LoginProtocol;"), "getLoginProtocol")
METHOD_NAME_MAP.setdefault(("com/rs2/net/LoginProtocol", "a", "(Lcom/rs2/model/player/Player;Ljava/nio/ByteBuffer;)V"), "processLoginBuffer")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "w", "(I)V"), "setPlayerRights")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bx", "()I"), "getPlayerRights")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bs", "()Ljava/lang/String;"), "getHostAddress")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bo", "()Ljava/lang/String;"), "getProfileString1")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "b", "(Ljava/lang/String;)V"), "setProfileString1")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dl", "()Ljava/lang/String;"), "getProfileString2")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "g", "(Ljava/lang/String;)V"), "setProfileString2")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "w", "(Z)V"), "setMemberFlag")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eg", "()Z"), "hasMemberFlag")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "S", "(I)V"), "subtractDonatorPoints")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "T", "(I)V"), "setDonatorPoints")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dm", "()I"), "getDonatorPoints")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aQ", "()I"), "getMembershipDaysRemaining")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dA", "()I"), "getQuestPoints")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bj", "()V"), "completeQuestJournal")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "d", "()V"), "resetQuestJournal")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aZ", "()I"), "getMaxedSkillCount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bc", "()V"), "process")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eB", "()Z"), "hasRestrictedCombatEquipment")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eC", "()Z"), "depositInventoryAndEquipment")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eD", "()Lcom/rs2/util/RectangularArea;"), "getLocalViewArea")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eE", "()V"), "refreshLocalViewArea")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eF", "()V"), "resetMageTrainingArenaPizazzPoints")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eG", "()Z"), "ownsProgressHat")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eH", "()Z"), "hasActiveProgressHat")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eK", "()Z"), "ownsClueScroll")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eL", "()Z"), "hasChargedAmuletOfGloryEquipped")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eM", "()Z"), "ownsCluePuzzleBox")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eN", "()Ljava/util/List;"), "getVisibleGroundItems")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "af", "(I)V"), "setCurrentWalkableInterfaceId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dT", "()I"), "getCurrentWalkableInterfaceId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/GameplayHelper", "c", "(Lcom/rs2/model/player/Player;I)Z"), "updateWalkableInterface")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "as", "(I)V"), "setCookingObjectId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eO", "()I"), "getCookingObjectId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ah", "(I)V"), "setSelectedSkillItemId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ei", "()I"), "getSelectedSkillItemId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ai", "(I)V"), "setSelectedSmithingBarItemId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ej", "()I"), "getSelectedSmithingBarItemId")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/skill/smithing/SmithingBarDefinition;)V"),
    "setSelectedSmithingBarDefinition",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/player/Player", "ek", "()Lcom/rs2/model/skill/smithing/SmithingBarDefinition;"),
    "getSelectedSmithingBarDefinition",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ap", "(I)V"), "setAbyssMageNpcId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eA", "()I"), "getAbyssMageNpcId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "am", "(I)I"), "getEssencePouchAmount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "h", "(II)V"), "setEssencePouchAmount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ev", "()Z"), "hasFullVoidMagicSet")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ew", "()Z"), "hasFullVoidRangedSet")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ex", "()Z"), "hasFullVoidMeleeSet")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ey", "()I"), "getDragonfireProtectionState")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "et", "()Z"), "shouldHideHeldItemsInAppearance")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "x", "(Z)V"), "setHideHeldItemsInAppearance")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eP", "()Z"), "isMuted")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eQ", "()Z"), "isBanned")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "z", "(Z)V"), "setBankPinReminderShown")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fd", "()Z"), "isBankPinReminderShown")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "k", "(II)V"), "setBankPinEntryDigit")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fe", "()V"), "resetBankPinEntryDigits")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ff", "()[I"), "getBankPinEntryDigits")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "c", "(Lcom/rs2/model/npc/Npc;)V"), "setActiveRandomEventNpc")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fg", "()Lcom/rs2/model/npc/Npc;"), "getActiveRandomEventNpc")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fh", "()I"), "getBossPetUnlockFlags")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aC", "(I)V"), "setBossPetUnlockFlags")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/PetManager", "a", "(II)V"), "summonPetFromItem")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/PetManager", "b", "(II)V"), "spawnQuestCatFollower")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/PetManager", "a", "()V"), "pickupPet")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/PetManager", "b", "()Lcom/rs2/model/npc/Npc;"), "getActivePetNpc")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/item/ItemStack;)V"), "setPendingDestroyItem")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fc", "()Lcom/rs2/model/item/ItemStack;"), "getPendingDestroyItem")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/item/ItemStack;)V"), "setRandomEventRequestedItem")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fi", "()Lcom/rs2/model/item/ItemStack;"), "getRandomEventRequestedItem")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fk", "()J"), "getDisconnectGraceExpiresAtMillis")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aE", "(I)V"), "setCoalTruckCoalCount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fl", "()I"), "getCoalTruckCoalCount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "c", "(Lcom/rs2/model/player/Player;)V"), "setDuelRequestTarget")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fm", "()Lcom/rs2/model/player/Player;"), "getDuelRequestTarget")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fn", "()I"), "getIdlePacketCount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aF", "(I)V"), "setIdlePacketCount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aq", "(I)Z"), "ownsItem")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "i", "(II)Z"), "ownsItemAmount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ar", "(I)I"), "getOwnedItemAmount")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "c", "()J"), "getSessionPlaytimeMillis")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ba", "()J"), "getTotalPlaytimeMillis")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bb", "()Z"), "hasMageArenaGodCape")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "v", "(I)V"), "showHiscoreInterface")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "L", "(I)V"), "setRunEnergyRaw")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "cV", "()I"), "getRunEnergyRaw")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "cW", "()I"), "getRunEnergyPercent")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "cX", "()I"), "getPrayerDrainThreshold")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aR", "()J"), "getBotTaskRuntimeMillis")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aL", "()Z"), "hasBotStalled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aM", "()V"), "resumeBotTaskState")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "()V"), "resetBotToLumbridge")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "A", "(Z)Z"), "recoverBotTaskStall")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "e", "()V"), "startCurrentBotTaskInteraction")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bk", "()V"), "continueBotRoute")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "M", "(I)V"), "addRunEnergyRaw")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "N", "(I)V"), "addRunEnergyPercent")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "O", "(I)V"), "setRunEnergyPercent")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ds", "()Z"), "isAutoRetaliate")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "k", "(Z)V"), "setAutoRetaliate")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dt", "()I"), "getBrightness")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "U", "(I)V"), "setBrightness")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "du", "()I"), "getMouseButtons")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "V", "(I)V"), "setMouseButtons")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dv", "()I"), "getSplitPrivateChat")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "W", "(I)V"), "setSplitPrivateChat")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dw", "()Z"), "isAcceptAidEnabled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dx", "()I"), "getAcceptAid")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "X", "(I)V"), "setAcceptAid")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dy", "()I"), "getMusicVolume")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "Y", "(I)V"), "setMusicVolume")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dz", "()I"), "getEffectVolume")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "Z", "(I)V"), "setEffectVolume")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/skill/magic/Spellbook;)V"), "setSpellbook")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dB", "()Lcom/rs2/model/skill/magic/Spellbook;"), "getSpellbook")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "R", "(I)V"), "setPrayerHeadIcon")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dk", "()I"), "getPrayerHeadIcon")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dn", "()I"), "getSkullIcon")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dY", "()V"), "clearPvpCombatReferences")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dZ", "()I"), "getSkullTimer")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/player/Player;I)V"), "addPvpCombatReference")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/player/Player;)V"), "recordPvpAttack")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "B", "(Z)V"), "setSkulled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "([Lcom/rs2/model/item/ItemStack;)Ljava/util/ArrayList;"), "getUnprotectedItems")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/player/TradeState;)V"), "setTradeState")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/player/Player;)V"), "setTradeRequestTarget")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eW", "()Lcom/rs2/model/player/Player;"), "getTradeRequestTarget")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Lcom/rs2/util/plugin/PlayerPlugin;)V"), "addPlayerPlugin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/combat/special/SpecialAttackDefinition;)V"), "setSpecialAttackDefinition")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dU", "()Lcom/rs2/model/combat/special/SpecialAttackDefinition;"), "getSpecialAttackDefinition")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "m", "(Z)V"), "setCrystalBowEquipped")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dI", "()Z"), "isCrystalBowEquipped")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "n", "(Z)V"), "setActionLocked")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dJ", "()Z"), "isActionLocked")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "o", "(Z)V"), "setAmmunitionDropsEnabled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dK", "()Z"), "isAmmunitionDropsEnabled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "p", "(Z)V"), "setDharokSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dL", "()Z"), "isDharokSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "q", "(Z)V"), "setAhrimSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dM", "()Z"), "isAhrimSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "r", "(Z)V"), "setKarilSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dN", "()Z"), "isKarilSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "s", "(Z)V"), "setToragSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dO", "()Z"), "isToragSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "t", "(Z)V"), "setGuthanSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dP", "()Z"), "isGuthanSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "u", "(Z)V"), "setVeracSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dQ", "()Z"), "isVeracSetEffectActive")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(J)V"), "setProtectionPrayerDisabledUntil")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dR", "()J"), "getProtectionPrayerDisabledUntil")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dW", "()I"), "getBlockSoundId")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "aa", "(I)V"), "setSpecialEnergy")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dC", "()I"), "getSpecialEnergy")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dD", "()Z"), "isSpecialAttackEnabled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "l", "(Z)V"), "setSpecialAttackEnabled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ab", "(I)V"), "setRingOfRecoilLife")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dE", "()I"), "getRingOfRecoilLife")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ac", "(I)V"), "setRingOfForgingLife")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dF", "()I"), "getRingOfForgingLife")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ad", "(I)V"), "setBindingNecklaceCharge")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dG", "()I"), "getBindingNecklaceCharge")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ae", "(I)V"), "setFightMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "dH", "()I"), "getFightMode")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ec", "()Lcom/rs2/model/skill/magic/SpellDefinition;"), "getQueuedCombatSpell")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "(Lcom/rs2/model/skill/magic/SpellDefinition;)V"), "setQueuedCombatSpell")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ed", "()Lcom/rs2/model/skill/magic/SpellDefinition;"), "getAutocastSpell")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "b", "(Lcom/rs2/model/skill/magic/SpellDefinition;)V"), "setAutocastSpell")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ee", "()Z"), "isAutocastEnabled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "v", "(Z)V"), "setAutocastEnabled")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "c", "(J)V"), "setMuteExpires")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "eo", "()J"), "getMuteExpires")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "d", "(J)V"), "setBanExpires")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ep", "()J"), "getBanExpires")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "y", "(Z)V"), "setBrimhavenOpen")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "fa", "()Z"), "isBrimhavenOpen")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "a", "(I)Z"), "handleButtonClick")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "d", "(I)I"), "decodeDigitButton")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "a", "([I)V"), "shuffleDigitButtons")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/bankpin/BankPinManager", "a", "(Lcom/rs2/model/bankpin/BankPinEntryMode;)V"),
    "setEntryMode",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "e", "(I)V"), "setCurrentDigitIndex")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "a", "()V"), "processPendingPinChanges")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "n", "()V"), "applyPendingPinChange")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "o", "()I"), "getPendingPinDaysElapsed")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "p", "()V"), "resetPinEntryInterface")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "b", "()V"), "requestPinChange")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "c", "()V"), "requestPinDeletion")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "d", "()V"), "clearPendingPinChange")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "e", "()Z"), "hasPin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "f", "()Z"), "hasPendingPinChange")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "g", "()Z"), "isVerified")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "h", "()[I"), "getPendingPin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "i", "()[I"), "getCurrentPin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "a", "(Lcom/rs2/model/player/Player;I)Z"), "showSkillUnlockMessage")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "a", "(Z)V"), "setChangingPin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "j", "()Z"), "isChangingPin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "b", "(Z)V"), "setDeletingPin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "k", "()Z"), "isDeletingPin")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "b", "(I)V"), "setPinAppendYear")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "l", "()I"), "getPinAppendYear")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "c", "(I)V"), "setPinAppendDate")
METHOD_NAME_MAP.setdefault(("com/rs2/model/bankpin/BankPinManager", "m", "()I"), "getPinAppendDate")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ChatTextCodec", "a", "([BI)Ljava/lang/String;"), "decode")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ChatTextCodec", "a", "(Ljava/lang/String;[B)I"), "encode")
METHOD_NAME_MAP.setdefault(("com/rs2/util/TextUtil", "a", "(J)Ljava/lang/String;"), "decodeNameHash")
METHOD_NAME_MAP.setdefault(("com/rs2/util/TextUtil", "a", "(Ljava/lang/String;)J"), "encodeNameHash")
METHOD_NAME_MAP.setdefault(("com/rs2/util/TextUtil", "a", "(II)Ljava/lang/String;"), "formatCombatLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/util/TextUtil", "b", "(Ljava/lang/String;)Ljava/lang/String;"), "capitalizeFirst")
METHOD_NAME_MAP.setdefault(("com/rs2/util/TextUtil", "c", "(Ljava/lang/String;)Ljava/lang/String;"), "formatDisplayName")
METHOD_NAME_MAP.setdefault(("com/rs2/util/TextUtil", "d", "(Ljava/lang/String;)Ljava/lang/String;"), "prependIndefiniteArticle")
METHOD_NAME_MAP.setdefault(("com/rs2/util/TextUtil", "a", "(Ljava/nio/ByteBuffer;)Ljava/lang/String;"), "readLine")
FIELD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "Ljava/util/Random;"), "random")
FIELD_NAME_MAP.setdefault(("com/rs2/util/WeightedChanceEntry", "a", "I"), "lowChance")
FIELD_NAME_MAP.setdefault(("com/rs2/util/WeightedChanceEntry", "b", "I"), "highChance")
FIELD_NAME_MAP.setdefault(("com/rs2/util/WeightedChanceEntry", "c", "I"), "requiredLevel")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(II)I"), "getRegionId")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(I)Lcom/rs2/model/Position;"), "getRegionBasePosition")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "b", "(I)I"), "bitFlag")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "b", "(II)I"), "randomBetweenInclusive")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(III)I"), "clampRunWeightForEnergyDrain")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "([I[I[II)I"), "rollLevelScaledChanceIndex")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(Ljava/util/ArrayList;I)[D"), "calculateLevelScaledProbabilities")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "b", "(III)Z"), "rollLevelScaledChance")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(IIID)Z"), "rollLevelScaledChance")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(D)Z"), "rollChance")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "c", "(III)D"), "calculateLevelScaledChance")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "([D)I"), "rollProbabilityIndex")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "([Ljava/lang/String;)I"), "rollFractionWeightIndex")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(Lcom/rs2/model/item/ItemStack;)V"), "addTrackedRareItemAmount")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(J)Ljava/lang/String;"), "formatNumber")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "b", "(D)Ljava/lang/String;"), "formatNumber")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "j", "(I)Ljava/lang/String;"), "formatNumber")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "c", "(I)Ljava/lang/String;"), "formatCompactAmountHighThreshold")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "d", "(I)Ljava/lang/String;"), "formatCompactAmount")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "e", "(I)Ljava/lang/String;"), "formatCompactAmountDetailed")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(Ljava/lang/String;)Ljava/lang/String;"), "capitalizeLowercaseFirst")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "b", "(Ljava/lang/String;)Ljava/lang/String;"), "capitalizeWords")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "c", "(Ljava/lang/String;)Ljava/lang/String;"), "formatDisplayName")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "f", "(I)I"), "randomExclusive")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "g", "(I)I"), "randomInclusive")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "h", "(I)I"), "randomInt")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "c", "(II)I"), "randomOneToInclusive")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "i", "(I)I"), "rollPriceFluctuationPercent")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "([I)[I"), "shuffleIntArray")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/GameUtil", "a", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;Z)Z"),
    "hasClearPath",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/GameUtil", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)Z"),
    "isNpcLastStepFacingPlayer",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/GameUtil", "b", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)Z"),
    "isNpcWaypointFacingPlayer",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(IIIIIZ)Z"), "hasClearPath")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/GameUtil", "a", "(IIIIIII)Lcom/rs2/model/Position;"),
    "findReachableInteractionPosition",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/GameUtil", "a", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;I)Z"),
    "isWithinDistance",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "(IIIII)Z"), "isWithinDistance")
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/GameUtil",
        "a",
        "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;)Lcom/rs2/model/Position;",
    ),
    "getDelta",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/GameUtil", "b", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;)I"),
    "getDistance",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "d", "(II)I"), "getDirectionForDelta")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "a", "()Ljava/util/Random;"), "getRandom")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "k", "(I)Ljava/lang/String;"), "getOrdinalWord")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "b", "()I"), "getDayOfYear")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "c", "()I"), "getCurrentYear")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "b", "(J)J"), "secondsToTicks")
METHOD_NAME_MAP.setdefault(("com/rs2/util/GameUtil", "l", "(I)I"), "minutesToMillis")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "a", "()I"), "readUnsignedByte")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "b", "()B"), "readByte")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "c", "()I"), "readUnsignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "d", "()I"), "readShort")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "e", "()I"), "readInt")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "f", "()J"), "readLong")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "g", "()Ljava/lang/String;"), "readString")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ByteArrayReader", "h", "()[B"), "readLineBytes")
METHOD_NAME_MAP.setdefault(("com/rs2/util/path/MapDataReader", "a", "(I)V"), "skipByte")
METHOD_NAME_MAP.setdefault(("com/rs2/util/path/MapDataReader", "a", "()I"), "readUnsignedByte")
METHOD_NAME_MAP.setdefault(("com/rs2/util/path/MapDataReader", "b", "()I"), "readUnsignedSmart")
METHOD_NAME_MAP.setdefault(("com/rs2/util/CountingDataOutputStream", "a", "(I)V"), "writeUnsignedByte")
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/RSAKeyGen",
        "a",
        "(Ljava/lang/String;Ljava/math/BigInteger;Ljava/math/BigInteger;)V",
    ),
    "writeRsaConstantsFile",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/player/Player",
        "aO",
        "()Lcom/rs2/model/reward/ActionRewardDefinition;",
    ),
    "rollActionReward",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/FileUtil", "a", "(Ljava/lang/String;)[B"), "readBytes")
METHOD_NAME_MAP.setdefault(("com/rs2/util/FileUtil", "a", "(Ljava/lang/String;Z)[B"), "readBytes")
METHOD_NAME_MAP.setdefault(("com/rs2/util/FileUtil", "a", "(Ljava/lang/String;[B)V"), "writeBytes")
METHOD_NAME_MAP.setdefault(("com/rs2/util/FileUtil", "b", "(Ljava/lang/String;)Z"), "exists")
METHOD_NAME_MAP.setdefault(("com/rs2/util/Vector2f", "a", "()F"), "getX")
METHOD_NAME_MAP.setdefault(("com/rs2/util/Vector2f", "b", "()F"), "getY")
METHOD_NAME_MAP.setdefault(("com/rs2/util/Vector2f", "c", "()Lcom/rs2/util/Vector2f;"), "normalize")
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "a", "()I"), "getMinX")
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "b", "()I"), "getMinY")
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "c", "()I"), "getMaxX")
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "d", "()I"), "getMaxY")
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "e", "()[Lcom/rs2/model/Position;"), "getPositions")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/RectangularArea", "a", "(Lcom/rs2/model/Position;II)Lcom/rs2/util/RectangularArea;"),
    "fromPositionOffset",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "a", "(Lcom/rs2/model/Position;)Z"), "containsExclusive")
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "b", "(Lcom/rs2/model/Position;)Z"), "contains")
METHOD_NAME_MAP.setdefault(("com/rs2/util/RectangularArea", "c", "(Lcom/rs2/model/Position;)Z"), "containsExclusiveOnPlane")
_DATABASE_QUERY_OWNERS = [
    "com/rs2/util/db/DatabaseQuery",
    "com/rs2/util/ConverterUidLookupQuery",
    "com/rs2/util/PlayerContactsLoadQuery",
    "com/rs2/util/PlayerProfileLoadQuery",
    "com/rs2/util/PlayerSkillsLoadQuery",
    "com/rs2/util/PlayerUidLookupQuery",
    "com/rs2/util/PlayerWorldUpdateQuery",
    "com/rs2/util/db/player/PlayerContactsSaveQuery",
    "com/rs2/util/db/player/PlayerContainerLoadQuery",
    "com/rs2/util/db/player/PlayerContainerSaveQuery",
    "com/rs2/util/db/player/PlayerProfileSaveQuery",
    "com/rs2/util/db/player/PlayerSkillsSaveQuery",
]

for _owner in _DATABASE_QUERY_OWNERS:
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Ljava/sql/PreparedStatement;)Ljava/sql/ResultSet;"),
        "executeStatement",
    )

_DATABASE_CALLBACK_OWNERS = [
    "com/rs2/util/db/DatabaseCallback",
    "com/rs2/util/ConverterUidLookupCallback",
    "com/rs2/util/PlayerLoginLoadCallback",
    "com/rs2/util/db/player/PlayerBankSaveCallback",
    "com/rs2/util/db/player/PlayerContactsLoadCallback",
    "com/rs2/util/db/player/PlayerContactsSaveCallback",
    "com/rs2/util/db/player/PlayerContainerLoadCallback",
    "com/rs2/util/db/player/PlayerEquipmentSaveCallback",
    "com/rs2/util/db/player/PlayerInventorySaveCallback",
    "com/rs2/util/db/player/PlayerProfileLoadCallback",
    "com/rs2/util/db/player/PlayerProfileSaveCallback",
    "com/rs2/util/db/player/PlayerSkillsLoadCallback",
    "com/rs2/util/db/player/PlayerSkillsSaveCallback",
]

for _owner in _DATABASE_CALLBACK_OWNERS:
    METHOD_NAME_MAP.setdefault((_owner, "a", "(Ljava/sql/ResultSet;)V"), "onResult")
    METHOD_NAME_MAP.setdefault((_owner, "a", "(Ljava/lang/Exception;)V"), "onException")

METHOD_NAME_MAP.setdefault(("com/rs2/util/db/DatabaseQuery", "a", "(Ljava/lang/ThreadLocal;)V"), "setThreadContextLocal")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/DatabaseQuery", "a", "(Lcom/rs2/util/db/DatabaseService;)V"),
    "setService",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/DatabaseQuery", "a", "(Lcom/rs2/util/db/DatabaseCallback;)V"),
    "setCallback",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/db/DatabaseService",
        "a",
        "(Lcom/rs2/util/db/DatabaseQuery;Lcom/rs2/util/db/DatabaseCallback;)V",
    ),
    "submit",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/db/DatabaseService", "a", "()Ljava/sql/Connection;"), "openConnection")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/DatabaseService", "b", "()Lcom/rs2/util/db/DatabaseService;"),
    "getInstance",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/DatabaseService", "a", "(Lcom/rs2/util/db/DatabaseService;)V"),
    "setInstance",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/db/DatabaseThreadContext",
        "a",
        "(Lcom/rs2/util/db/DatabaseThreadContext;)Ljava/sql/Connection;",
    ),
    "getConnection",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/db/DatabaseThreadContext",
        "a",
        "(Lcom/rs2/util/db/DatabaseThreadContext;Ljava/sql/Connection;)V",
    ),
    "setConnection",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/db/DatabaseThreadContext",
        "b",
        "(Lcom/rs2/util/db/DatabaseThreadContext;)Ljava/util/Map;",
    ),
    "getPreparedStatements",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/db/DatabaseThreadContext",
        "a",
        "(Lcom/rs2/util/db/DatabaseThreadContext;Ljava/util/Map;)V",
    ),
    "setPreparedStatements",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "a", "()Lcom/rs2/util/db/DatabaseCallback;"),
    "createProfileLoadCallback",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "b", "()Lcom/rs2/util/db/DatabaseCallback;"),
    "createBankLoadCallback",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "c", "()Lcom/rs2/util/db/DatabaseCallback;"),
    "createInventoryLoadCallback",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "d", "()Lcom/rs2/util/db/DatabaseCallback;"),
    "createEquipmentLoadCallback",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "e", "()Lcom/rs2/util/db/DatabaseCallback;"),
    "createContactsLoadCallback",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "f", "()Lcom/rs2/util/db/DatabaseCallback;"),
    "createSkillsLoadCallback",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerLoadQueryFactory", "a", "(I)Lcom/rs2/util/db/DatabaseQuery;"),
    "createContainerLoadQuery",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/db/player/PlayerLoadQueryFactory",
        "a",
        "(Lcom/rs2/util/db/player/PlayerLoadQueryFactory;)Lcom/rs2/model/player/Player;",
    ),
    "getPlayer",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "a", "()Lcom/rs2/util/db/DatabaseQuery;"),
    "createProfileSaveQuery",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "b", "()Lcom/rs2/util/db/DatabaseQuery;"),
    "createBankSaveQuery",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "c", "()Lcom/rs2/util/db/DatabaseQuery;"),
    "createInventorySaveQuery",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "d", "()Lcom/rs2/util/db/DatabaseQuery;"),
    "createEquipmentSaveQuery",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "e", "()Lcom/rs2/util/db/DatabaseQuery;"),
    "createContactsSaveQuery",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "f", "()Lcom/rs2/util/db/DatabaseQuery;"),
    "createSkillsSaveQuery",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/util/db/player/PlayerSaveQueryFactory",
        "a",
        "(Lcom/rs2/util/db/player/PlayerSaveQueryFactory;)Lcom/rs2/model/player/Player;",
    ),
    "getPlayer",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "a", "(II)Ljava/lang/String;"),
    "getContainerSaveSql",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/db/player/PlayerSaveQueryFactory", "g", "()Ljava/lang/String;"),
    "getContactsSaveSql",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/PlayerLoginLoadCallback", "b", "(Ljava/sql/ResultSet;)Z"),
    "processUidLookupResult",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/Converter", "a", "(Z)V"), "markReadyForNextFile")
METHOD_NAME_MAP.setdefault(("com/rs2/util/Converter", "a", "()I"), "getConvertedCount")
METHOD_NAME_MAP.setdefault(("com/rs2/util/Converter", "a", "(I)V"), "setConvertedCount")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerAssignmentDefinition", "a", "(Lcom/rs2/model/player/Player;)Z"),
    "isAvailableFor",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/slayer/SlayerManager", "a", "(I)V"), "assignTaskFromMaster")
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "a",
        "([Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;Lcom/rs2/model/player/Player;)Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ),
    "chooseWeightedAssignment",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/slayer/SlayerManager", "a", "()V"), "completeTask")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerManager", "a", "(Lcom/rs2/model/npc/Npc;)V"),
    "recordKill",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerManager", "b", "(Lcom/rs2/model/npc/Npc;)Z"),
    "canAttackSlayerMonster",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerManager", "a", "(Lcom/rs2/model/npc/Npc;Z)Z"),
    "requiresFinishingItem",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerManager", "c", "(Lcom/rs2/model/npc/Npc;)Z"),
    "handleZygomiteSpawn",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/slayer/SlayerManager", "a", "(III)Z"), "handleMogreLure")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerManager", "a", "(Lcom/rs2/model/npc/Npc;I)V"),
    "useFinishingItemOnMonster",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/slayer/SlayerManager", "a", "(II)Z"), "combineFungicideSpray")
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/slayer/SlayerManager",
        "a",
        "(Lcom/rs2/model/skill/slayer/SlayerManager;)Lcom/rs2/model/player/Player;",
    ),
    "getPlayer",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/slayer/SlayerMasterDefinition",
        "a",
        "(I)Lcom/rs2/model/skill/slayer/SlayerMasterDefinition;",
    ),
    "forNpcId",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/slayer/SlayerMasterDefinition", "a", "()I"), "getNpcId")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "b", "()I"),
    "getRequiredCombatLevel",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerMasterDefinition", "c", "()Ljava/lang/String;"),
    "getLocationName",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/slayer/SlayerMasterDefinition",
        "d",
        "()[Lcom/rs2/model/skill/slayer/SlayerAssignmentDefinition;",
    ),
    "getAssignments",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/slayer/SlayerMonsterDefinition",
        "a",
        "(Ljava/lang/String;)Lcom/rs2/model/skill/slayer/SlayerMonsterDefinition;",
    ),
    "forName",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "a", "()Ljava/lang/String;"),
    "getMonsterName",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "b", "()I"),
    "getRequiredSlayerLevel",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "c", "()[I"),
    "getRequiredItemIds",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerMonsterDefinition", "d", "()Ljava/lang/String;"),
    "getRequirementMode",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/slayer/SlayerMonsterGuide",
        "a",
        "(Ljava/lang/String;)Lcom/rs2/model/skill/slayer/SlayerMonsterGuide;",
    ),
    "forMonsterName",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/slayer/SlayerMonsterGuide", "a", "()[Ljava/lang/String;"),
    "getGuideTextLines",
)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/clue/AnagramClue", "a", "(I)Lcom/rs2/model/clue/AnagramClue;", "forNpcId"),
    ("com/rs2/model/clue/AnagramClue", "b", "(I)Lcom/rs2/model/clue/AnagramClue;", "forClueItemId"),
    ("com/rs2/model/clue/AnagramClue", "a", "()I", "getClueItemId"),
    ("com/rs2/model/clue/AnagramClue", "b", "()Ljava/lang/String;", "getAnagramText"),
    ("com/rs2/model/clue/AnagramClue", "c", "()Ljava/lang/String;", "getFollowupType"),
    ("com/rs2/model/clue/AnagramClue", "d", "()I", "getLevel"),
    ("com/rs2/model/clue/ChallengeQuestion", "a", "(I)Lcom/rs2/model/clue/ChallengeQuestion;", "forClueItemId"),
    ("com/rs2/model/clue/ChallengeQuestion", "b", "(I)Lcom/rs2/model/clue/ChallengeQuestion;", "forAnswerItemId"),
    ("com/rs2/model/clue/ChallengeQuestion", "a", "()[Ljava/lang/String;", "getQuestionLines"),
    ("com/rs2/model/clue/ChallengeQuestion", "b", "()I", "getClueItemId"),
    ("com/rs2/model/clue/ChallengeQuestion", "c", "()I", "getAnswerItemId"),
    ("com/rs2/model/clue/ChallengeQuestion", "d", "()I", "getAnswerValue"),
    ("com/rs2/model/clue/CoordinateClue", "a", "(Lcom/rs2/model/Position;)Lcom/rs2/model/clue/CoordinateClue;", "forPosition"),
    ("com/rs2/model/clue/CoordinateClue", "a", "(I)Lcom/rs2/model/clue/CoordinateClue;", "forClueItemId"),
    ("com/rs2/model/clue/CoordinateClue", "a", "()I", "getClueItemId"),
    ("com/rs2/model/clue/CoordinateClue", "b", "()I", "getLatitudeDegrees"),
    ("com/rs2/model/clue/CoordinateClue", "c", "()I", "getLatitudeMinutes"),
    ("com/rs2/model/clue/CoordinateClue", "d", "()I", "getLongitudeDegrees"),
    ("com/rs2/model/clue/CoordinateClue", "e", "()I", "getLongitudeMinutes"),
    ("com/rs2/model/clue/CoordinateClue", "f", "()Ljava/lang/String;", "getLatitudeDirection"),
    ("com/rs2/model/clue/CoordinateClue", "g", "()Ljava/lang/String;", "getLongitudeDirection"),
    ("com/rs2/model/clue/CoordinateClue", "h", "()I", "getLevel"),
    ("com/rs2/model/clue/SearchClue", "a", "(Lcom/rs2/model/Position;)Lcom/rs2/model/clue/SearchClue;", "forPosition"),
    ("com/rs2/model/clue/SearchClue", "a", "(I)Lcom/rs2/model/clue/SearchClue;", "forClueItemId"),
    ("com/rs2/model/clue/SearchClue", "a", "()[Ljava/lang/String;", "getClueTextLines"),
    ("com/rs2/model/clue/SearchClue", "b", "()I", "getClueItemId"),
    ("com/rs2/model/clue/SearchClue", "c", "()Lcom/rs2/model/Position;", "getPosition"),
    ("com/rs2/model/clue/SearchClue", "d", "()I", "getReplacementObjectId"),
    ("com/rs2/model/clue/SearchClue", "e", "()I", "getAnimationId"),
    ("com/rs2/model/clue/SearchClue", "f", "()I", "getLevel"),
    ("com/rs2/model/clue/NpcClue", "a", "(I)Lcom/rs2/model/clue/NpcClue;", "forNpcId"),
    ("com/rs2/model/clue/NpcClue", "b", "(I)Lcom/rs2/model/clue/NpcClue;", "forClueItemId"),
    ("com/rs2/model/clue/NpcClue", "a", "()[Ljava/lang/String;", "getClueTextLines"),
    ("com/rs2/model/clue/NpcClue", "b", "()I", "getClueItemId"),
    ("com/rs2/model/clue/NpcClue", "c", "()Ljava/lang/String;", "getFollowupType"),
    ("com/rs2/model/clue/NpcClue", "d", "()I", "getLevel"),
    ("com/rs2/model/clue/MapClue", "a", "(Lcom/rs2/model/Position;)Lcom/rs2/model/clue/MapClue;", "forPosition"),
    ("com/rs2/model/clue/MapClue", "a", "(I)Lcom/rs2/model/clue/MapClue;", "forClueItemId"),
    ("com/rs2/model/clue/MapClue", "a", "()I", "getClueItemId"),
    ("com/rs2/model/clue/MapClue", "b", "()I", "getInterfaceId"),
    ("com/rs2/model/clue/MapClue", "c", "()Z", "isObjectSearchClue"),
    ("com/rs2/model/clue/MapClue", "d", "()I", "getLevel"),
    ("com/rs2/model/clue/CrypticDigClue", "a", "(Lcom/rs2/model/Position;)Lcom/rs2/model/clue/CrypticDigClue;", "forPosition"),
    ("com/rs2/model/clue/CrypticDigClue", "a", "(I)Lcom/rs2/model/clue/CrypticDigClue;", "forClueItemId"),
    ("com/rs2/model/clue/CrypticDigClue", "a", "()[Ljava/lang/String;", "getClueTextLines"),
    ("com/rs2/model/clue/CrypticDigClue", "b", "()I", "getClueItemId"),
    ("com/rs2/model/clue/CrypticDigClue", "c", "()I", "getLevel"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/bot/route/BotWorldRouteChoice", "a", "(Lcom/rs2/model/player/Player;I)Z", "showCrypticDigClue"),
    ("com/rs2/bot/route/BotWorldRouteChoice", "a", "(I)I", "randomCrypticDigClueItemForLevel"),
    ("com/rs2/cache/CacheFile", "a", "(Lcom/rs2/model/player/Player;I)Z", "showSearchClue"),
    ("com/rs2/cache/CacheFile", "a", "(I)I", "randomSearchClueItemForLevel"),
    (
        "com/rs2/cache/CacheFile",
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/objects/WorldObject;)Z",
        "searchClueObject",
    ),
    (
        "com/rs2/cache/CacheArchive",
        "a",
        "(Lcom/rs2/model/player/Player;II)Z",
        "handleLogCuttingButton",
    ),
    ("com/rs2/cache/CacheArchive", "a", "(Lcom/rs2/model/player/Player;I)V", "giveChallengeQuestionAnswerItem"),
    ("com/rs2/cache/CacheArchive", "b", "(Lcom/rs2/model/player/Player;I)Z", "hasChallengeQuestionAnswerItem"),
    ("com/rs2/cache/CacheArchive", "a", "(I)[Ljava/lang/String;", "getChallengeQuestionLines"),
    ("com/rs2/cache/CacheArchive", "c", "(Lcom/rs2/model/player/Player;I)Z", "showChallengeQuestionForAnswerItem"),
    ("com/rs2/cache/CacheArchiveEntry", "a", "(I)I", "randomMapClueItemForLevel"),
    ("com/rs2/cache/CacheArchiveEntry", "a", "(Lcom/rs2/model/player/Player;II)Z", "searchMapClueObject"),
    ("com/rs2/cache/CacheArchiveEntry", "a", "(Lcom/rs2/model/player/Player;)V", "startTradeOfferTick"),
    ("com/rs2/cache/CacheArchiveEntry", "a", "(Lcom/rs2/model/player/Player;Z)V", "completeTradeAdvertOffer"),
    ("com/rs2/cache/CacheDefinitionIndex", "a", "(Lcom/rs2/model/player/Player;)V", "dismissRandomEventNpc"),
    ("com/rs2/cache/CacheDefinitionIndex", "b", "(Lcom/rs2/model/player/Player;)V", "scheduleRandomEventRoll"),
    ("com/rs2/cache/CacheDefinitionIndex", "a", "(Lcom/rs2/model/player/Player;I)Z", "showNpcClue"),
    ("com/rs2/cache/CacheDefinitionIndex", "d", "(I)I", "randomNpcClueItemForLevel"),
    ("com/rs2/cache/CacheDefinitionIndex", "b", "(Lcom/rs2/model/player/Player;I)Z", "handleNpcClueNpc"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/cache/CacheStore", "a", "()V", "initializeCacheStore"),
    ("com/rs2/cache/CacheStore", "b", "()Lcom/rs2/cache/CacheStore;", "getInstance"),
    ("com/rs2/cache/CacheStore", "c", "()Lcom/rs2/cache/CacheDefinitionIndex;", "getDefinitionIndex"),
    ("com/rs2/cache/CacheStore", "a", "(II)Lcom/rs2/cache/CacheFile;", "readFile"),
    ("com/rs2/cache/CacheStore", "d", "()V", "verifyLauncherJarIntegrity"),
    ("com/rs2/cache/CacheFile", "a", "()Ljava/nio/ByteBuffer;", "getBuffer"),
    ("com/rs2/cache/CacheArchive", "a", "(Ljava/lang/String;)[B", "getFileBytes"),
    ("com/rs2/cache/CacheArchive", "b", "(Ljava/lang/String;)Ljava/nio/ByteBuffer;", "getFileBuffer"),
    ("com/rs2/cache/CacheArchive", "a", "([B)[B", "decompressBzip2Payload"),
    ("com/rs2/cache/CacheArchiveEntry", "a", "()I", "getNameHash"),
    ("com/rs2/cache/CacheArchiveEntry", "b", "()I", "getCompressedSize"),
    ("com/rs2/cache/CacheArchiveEntry", "c", "()I", "getDataOffset"),
    ("com/rs2/cache/DefinitionIndexEntry", "a", "()I", "getDataOffset"),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "a",
        "(Lcom/rs2/cache/CacheArchive;)V",
        "loadObjectDefinitionIndex",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "b",
        "(Lcom/rs2/cache/CacheArchive;)V",
        "loadItemDefinitionIndex",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "c",
        "(Lcom/rs2/cache/CacheArchive;)V",
        "loadNpcDefinitionIndex",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "d",
        "(Lcom/rs2/cache/CacheArchive;)V",
        "loadMapIndex",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "a",
        "()[Lcom/rs2/cache/DefinitionIndexEntry;",
        "getObjectDefinitionEntries",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "b",
        "()[Lcom/rs2/cache/DefinitionIndexEntry;",
        "getItemDefinitionEntries",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "c",
        "()[Lcom/rs2/cache/DefinitionIndexEntry;",
        "getNpcDefinitionEntries",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "a",
        "(I)Lcom/rs2/cache/DefinitionIndexEntry;",
        "getObjectDefinitionEntry",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "b",
        "(I)Lcom/rs2/cache/DefinitionIndexEntry;",
        "getItemDefinitionEntry",
    ),
    (
        "com/rs2/cache/CacheDefinitionIndex",
        "c",
        "(I)Lcom/rs2/cache/DefinitionIndexEntry;",
        "getNpcDefinitionEntry",
    ),
    ("com/rs2/cache/InterfaceDefinition", "a", "()I", "getParentInterfaceId"),
    (
        "com/rs2/cache/InterfaceDefinition",
        "a",
        "(I)Lcom/rs2/cache/InterfaceDefinition;",
        "forId",
    ),
    ("com/rs2/cache/InterfaceDefinition", "b", "()V", "loadDefinitions"),
    ("com/rs2/bot/route/BotWorldRouteChoice", "a", "()Z", "isReversed"),
    ("com/rs2/bot/BotRoute", "a", "()Lcom/rs2/bot/BotRoute;", "reversed"),
    ("com/rs2/bot/BotRoute", "b", "()I", "getDistance"),
    ("com/rs2/bot/BotRoute", "c", "()Lcom/rs2/model/Position;", "getStartPosition"),
    ("com/rs2/bot/BotRoute", "d", "()Lcom/rs2/model/Position;", "getEndPosition"),
    ("com/rs2/bot/route/BotWorldRoute", "a", "()Lcom/rs2/bot/BotRoute;", "getRoute"),
    ("com/rs2/bot/route/BotWorldRoute", "b", "()[Lcom/rs2/bot/BotRoute;", "getSegments"),
    ("com/rs2/bot/route/BotWorldRoute", "c", "()I", "getRouteNpcId"),
    ("com/rs2/bot/route/BotWorldRoute", "d", "()I", "getDistance"),
    ("com/rs2/bot/route/BotWorldRoute", "e", "()Lcom/rs2/model/Position;", "getStartPosition"),
    ("com/rs2/bot/route/BotWorldRoute", "f", "()Lcom/rs2/model/Position;", "getEndPosition"),
    ("com/rs2/bot/route/BotWorldRouteWalker", "a", "(Lcom/rs2/model/player/Player;Ljava/util/ArrayList;)V", "findConnectingWorldRoute"),
    ("com/rs2/bot/route/BotWorldRouteWalker", "a", "(Lcom/rs2/model/player/Player;)Z", "findWorldRoute"),
    ("com/rs2/bot/route/BotWorldRouteWalker", "a", "(Lcom/rs2/bot/route/BotWorldRouteChoice;)I", "getRouteIndex"),
    ("com/rs2/bot/route/BotWorldRouteWalker", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/bot/route/BotWorldRouteChoice;)V", "continueWorldRoute"),
    ("com/rs2/bot/route/BotWorldRouteWalker", "b", "(Lcom/rs2/model/player/Player;Lcom/rs2/bot/route/BotWorldRouteChoice;)V", "startWorldRoute"),
    ("com/rs2/bot/route/BotWorldRouteWalker", "b", "(Lcom/rs2/model/player/Player;)V", "advanceRouteSegment"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/bot/BotTaskDefinition", "a", "()Ljava/util/ArrayList;", "getLootSellShopTasks"),
    ("com/rs2/bot/BotTaskDefinition", "a", "(II)Lcom/rs2/bot/BotTaskDefinition;", "getTaskByTypeAndIndex"),
    ("com/rs2/bot/BotTaskDefinition", "b", "()I", "getTaskTypeId"),
    ("com/rs2/bot/BotTaskDefinition", "a", "(I)I", "getTaskIndexForType"),
    ("com/rs2/bot/BotTaskDefinition", "c", "()V", "initializeTradeAdvertTaskPool"),
    ("com/rs2/bot/BotTaskDefinition", "d", "()V", "initializeDropPartyTaskPool"),
    ("com/rs2/bot/BotTaskDefinition", "e", "()V", "initializeProgressiveTaskPool"),
    ("com/rs2/bot/BotTaskDefinition", "a", "(Lcom/rs2/bot/BotRoute;)V", "setPretaskRoute"),
    ("com/rs2/bot/BotTaskDefinition", "a", "([Lcom/rs2/util/RectangularArea;)V", "setTaskAreas"),
    ("com/rs2/bot/BotTaskDefinition", "a", "([I)V", "addLootSellShopIds"),
    ("com/rs2/bot/BotTaskDefinition", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)V", "startNpcCombatTick"),
    ("com/rs2/bot/BotTaskDefinition", "b", "(Lcom/rs2/model/player/Player;Z)V", "completeTradeAdvertOffer"),
    ("com/rs2/bot/BotTaskDefinition", "a", "(Lcom/rs2/model/player/Player;)V", "startCustomTaskAction"),
    ("com/rs2/bot/BotTaskDefinition", "b", "(Lcom/rs2/model/player/Player;)V", "startEscapeMonitor"),
    ("com/rs2/bot/BotTaskDefinition", "c", "(Lcom/rs2/model/player/Player;)Z", "meetsUnlockRequirements"),
    ("com/rs2/bot/BotTaskDefinition", "d", "(Lcom/rs2/model/player/Player;)Z", "isWithinProgressionRange"),
    ("com/rs2/bot/BotTaskDefinition", "a", "(Lcom/rs2/model/player/Player;Z)Z", "isAvailableFor"),
    ("com/rs2/bot/BotTaskDefinition", "e", "(Lcom/rs2/model/player/Player;)Ljava/util/ArrayList;", "getRequiredItems"),
    ("com/rs2/bot/BotTaskDefinition", "f", "(Lcom/rs2/model/player/Player;)Ljava/util/ArrayList;", "getMissingRequiredItems"),
    ("com/rs2/bot/BotTaskDefinition", "b", "(I)V", "setForcedCombatStyle"),
    ("com/rs2/bot/BotTaskDefinition", "f", "()I", "getForcedCombatStyle"),
    ("com/rs2/bot/BotTaskDefinition", "g", "()I", "getShopId"),
    ("com/rs2/bot/BotTaskDefinition", "g", "(Lcom/rs2/model/player/Player;)I", "getInteractionOption"),
    ("com/rs2/bot/BotTaskDefinition", "h", "(Lcom/rs2/model/player/Player;)V", "startTask"),
    ("com/rs2/bot/BotTaskDefinition", "h", "()Lcom/rs2/model/Position;", "getStartPosition"),
    ("com/rs2/bot/BotTaskDefinition", "i", "()Lcom/rs2/model/Position;", "getRandomTaskAreaPosition"),
    ("com/rs2/bot/BotTaskDefinition", "j", "()Lcom/rs2/model/Position;", "getTaskPosition"),
    ("com/rs2/bot/BotTaskDefinition", "i", "(Lcom/rs2/model/player/Player;)V", "prepareTradeAdvertState"),
    ("com/rs2/bot/BotTaskDefinition", "j", "(Lcom/rs2/model/player/Player;)V", "prepareDropPartyState"),
    ("com/rs2/bot/BotTaskDefinition", "k", "(Lcom/rs2/model/player/Player;)V", "configureTaskInteractionTargets"),
    ("com/rs2/bot/BotTaskDefinition", "l", "(Lcom/rs2/model/player/Player;)V", "prepareTaskInventory"),
    ("com/rs2/bot/BotTaskDefinition", "m", "(Lcom/rs2/model/player/Player;)V", "prepareTaskCombatLoadout"),
    ("com/rs2/bot/BotTaskDefinition", "n", "(Lcom/rs2/model/player/Player;)V", "startWalkToTask"),
    ("com/rs2/bot/BotTaskDefinition", "a", "(Lcom/rs2/model/player/Player;I)V", "continueWalkToTask"),
    ("com/rs2/bot/BotTaskDefinition", "o", "(Lcom/rs2/model/player/Player;)V", "startPretaskPath"),
    ("com/rs2/bot/BotTaskDefinition", "p", "(Lcom/rs2/model/player/Player;)V", "returnPretaskPath"),
    ("com/rs2/bot/BotTaskDefinition", "q", "(Lcom/rs2/model/player/Player;)V", "startWalkToBank"),
    ("com/rs2/bot/BotTaskDefinition", "b", "(Lcom/rs2/model/player/Player;I)V", "continueWalkToBank"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/clue/ClueKeyHandler", "a", "(Lcom/rs2/model/player/Player;I)Z", "consumeRequiredKey"),
    ("com/rs2/model/clue/ClueKeyHandler", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)V", "dropRequiredKeyFromNpc"),
    ("com/rs2/model/clue/CoordinateClueHandler", "a", "(Lcom/rs2/model/player/Player;I)Z", "showCoordinateClue"),
    ("com/rs2/model/clue/CoordinateClueHandler", "a", "(Lcom/rs2/model/player/Player;)Z", "digAtCoordinateClue"),
    ("com/rs2/model/clue/CoordinateClueHandler", "b", "(I)Ljava/lang/String;", "formatTwoDigits"),
    (
        "com/rs2/model/clue/CoordinateClueHandler",
        "a",
        "(IIIILjava/lang/String;Ljava/lang/String;)Lcom/rs2/model/Position;",
        "resolvePosition",
    ),
    ("com/rs2/model/clue/CoordinateClueHandler", "a", "(II)[Ljava/lang/String;", "formatPositionAsCoordinate"),
    ("com/rs2/model/clue/CoordinateClueHandler", "a", "(I)I", "randomClueItemForLevel"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/clue/TreasureTrailManager", "a", "()V", "filterRewardItemPools"),
    ("com/rs2/model/clue/TreasureTrailManager", "a", "(Lcom/rs2/model/player/Player;)V", "clearClueInterfaceText"),
    (
        "com/rs2/model/clue/TreasureTrailManager",
        "a",
        "(Lcom/rs2/model/player/Player;ILjava/lang/String;ZLjava/lang/String;)V",
        "advanceOrCompleteTrail",
    ),
    ("com/rs2/model/clue/TreasureTrailManager", "c", "(Lcom/rs2/model/player/Player;I)V", "awardNextClueScroll"),
    ("com/rs2/model/clue/TreasureTrailManager", "a", "(Lcom/rs2/model/player/Player;I)V", "completeTreasureTrail"),
    (
        "com/rs2/model/clue/TreasureTrailManager",
        "d",
        "(Lcom/rs2/model/player/Player;I)[Lcom/rs2/model/item/ItemStack;",
        "rollRewardItems",
    ),
    ("com/rs2/model/clue/TreasureTrailManager", "a", "(I)I", "randomClueItemForLevel"),
    ("com/rs2/model/clue/TreasureTrailManager", "b", "(Lcom/rs2/model/player/Player;I)Z", "handleRewardContainerItem"),
    ("com/rs2/model/clue/TreasureTrailManager", "b", "(Lcom/rs2/model/player/Player;)V", "spawnClueWizard"),
    (
        "com/rs2/model/clue/TreasureTrailManager",
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)V",
        "recordClueWizardKill",
    ),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _owner, _name, _descriptor, _mapped_name in [
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(Lcom/rs2/model/player/Player;I)Z", "openCluePuzzleBox"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(Lcom/rs2/model/player/Player;)Z", "openQuestPuzzleBox"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "()Ljava/util/ArrayList;", "drainPieceQueue"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(I)I", "getPuzzleTypeForItem"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "b", "(I)[I", "getPiecesForPuzzleType"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "d", "(Lcom/rs2/model/player/Player;)V", "showCluePuzzleInterface"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "e", "(Lcom/rs2/model/player/Player;)V", "showQuestPuzzleInterface"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "c", "(Lcom/rs2/model/player/Player;I)Lcom/rs2/model/Position;", "getPiecePosition"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;)Z", "isAdjacentToBlankTile"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;Ljava/lang/String;)I", "getTileDistance"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;)I", "getTileDistance"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "b", "(Lcom/rs2/model/player/Player;I)Z", "movePuzzlePiece"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "b", "(Lcom/rs2/model/player/Player;)Z", "isCluePuzzleSolved"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "f", "(Lcom/rs2/model/player/Player;)Z", "isQuestPuzzleSolved"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;Z)V", "swapBlankWithPosition"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "g", "(Lcom/rs2/model/player/Player;)V", "scramblePuzzle"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "a", "(Ljava/util/ArrayList;)I", "maxValue"),
    ("com/rs2/model/clue/PuzzleBoxHandler", "c", "(Lcom/rs2/model/player/Player;)V", "giveRandomPuzzleBox"),
]:
    METHOD_NAME_MAP.setdefault((_owner, _name, _descriptor), _mapped_name)
for _name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;)Z", "savePlayer"),
    ("e", "(Lcom/rs2/model/player/Player;)V", "writePlayerFile"),
    ("a", "(Lcom/rs2/model/player/CharacterFileRecord;)V", "saveCharacterFileRecord"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "loadPlayer"),
    ("a", "()V", "saveAllPlayers"),
    ("f", "(Lcom/rs2/model/player/Player;)V", "restorePlayerFromBackup"),
    ("a", "(Ljava/lang/String;)Ljava/lang/String;", "stripFileExtension"),
    ("c", "()V", "repairBackupCharacterFiles"),
    ("b", "()V", "createTimestampedBackup"),
    ("a", "(Ljava/io/File;Ljava/io/File;)V", "copyFile"),
    ("a", "(Ljava/lang/String;Ljava/lang/String;)Z", "validateCharacterFile"),
    ("c", "(Lcom/rs2/model/player/Player;)V", "archiveDeadHardcoreIronman"),
    ("d", "(Lcom/rs2/model/player/Player;)V", "refreshLiveHiscoreRecord"),
    ("a", "(Ljava/lang/String;Ljava/lang/String;Z)Lcom/rs2/model/player/CharacterFileRecord;", "readCharacterFileRecord"),
    ("a", "(Ljava/lang/String;Lcom/rs2/model/player/Player;)V", "loadPlayerFromFile"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/util/CharacterFileManager", _name, _descriptor), _mapped_name)
for _name, _descriptor, _mapped_name in [
    ("a", "(ILcom/rs2/model/item/ItemStack;I)V", "setBankTabItem"),
    ("a", "()I", "getStoredItemValue"),
    ("a", "(I)J", "getSkillExperience"),
    ("b", "()I", "getTotalLevel"),
    ("a", "(D)I", "getLevelForExperience"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/player/CharacterFileRecord", _name, _descriptor), _mapped_name)
for _name, _descriptor, _mapped_name in [
    ("a", "Ljava/lang/String;", "username"),
    ("b", "Ljava/lang/String;", "password"),
    ("c", "Ljava/lang/String;", "hostAddress"),
    ("d", "Ljava/lang/String;", "reservedVersion11String"),
    ("e", "I", "playerRights"),
    ("f", "Ljava/lang/String;", "legacyProfileString"),
    ("g", "Ljava/lang/String;", "profileString1"),
    ("h", "Ljava/lang/String;", "profileString2"),
    ("i", "J", "lastSavedMillis"),
    ("j", "J", "totalPlayTimeMillis"),
    ("k", "J", "createdAtMillis"),
    ("l", "Z", "loginRestrictionExempt"),
    ("m", "Z", "memberFlag"),
    ("n", "I", "donatorPoints"),
    ("o", "I", "petUnlockFlags"),
    ("p", "I", "x"),
    ("q", "I", "y"),
    ("r", "I", "plane"),
    ("s", "I", "skeletonSkinUnlocked"),
    ("t", "I", "gender"),
    ("u", "I", "npcKillCount"),
    ("v", "I", "playerKillCount"),
    ("w", "I", "deathCount"),
    ("x", "I", "easyCluesCompleted"),
    ("y", "I", "mediumCluesCompleted"),
    ("z", "I", "hardCluesCompleted"),
    ("A", "I", "soldItemsValue"),
    ("B", "I", "boughtItemsValue"),
    ("C", "I", "duelWins"),
    ("D", "I", "duelLosses"),
    ("E", "I", "legacyQuestPoints"),
    ("F", "Z", "autoRetaliate"),
    ("G", "I", "fightMode"),
    ("H", "I", "brightness"),
    ("I", "I", "mouseButtons"),
    ("J", "I", "publicChatEffects"),
    ("K", "I", "splitPrivateChat"),
    ("L", "I", "acceptAid"),
    ("M", "I", "musicVolume"),
    ("N", "I", "effectVolume"),
    ("O", "I", "specialEnergy"),
    ("P", "Z", "changingBankPin"),
    ("Q", "Z", "deletingBankPin"),
    ("R", "I", "pinAppendYear"),
    ("S", "I", "pinAppendDate"),
    ("T", "I", "bindingNecklaceCharge"),
    ("U", "I", "ringOfForgingLife"),
    ("V", "I", "ringOfRecoilLife"),
    ("W", "I", "skullTimer"),
    ("X", "I", "runEnergyRaw"),
    ("Y", "Z", "running"),
    ("au", "[I", "currentPin"),
    ("av", "[I", "pendingPin"),
    ("aw", "[I", "essencePouchAmounts"),
    ("ax", "[I", "appearanceParts"),
    ("ay", "[I", "appearanceColors"),
    ("az", "[Lcom/rs2/model/item/ItemStack;", "inventoryItems"),
    ("aA", "[Lcom/rs2/model/item/ItemStack;", "equipmentItems"),
    ("aB", "[J", "friendsList"),
    ("aC", "[J", "ignoreList"),
    ("aD", "[I", "queuedLoginItemIds"),
    ("aE", "[I", "queuedLoginItemAmounts"),
    ("aF", "[Z", "barrowsKilledBrothers"),
    ("Z", "I", "abyssMageNpcId"),
    ("aa", "J", "muteExpires"),
    ("ab", "J", "banExpires"),
    ("ac", "I", "barrowsKillCount"),
    ("ad", "I", "barrowsTargetBrotherIndex"),
    ("ae", "I", "poisonImmunityTicks"),
    ("af", "I", "antifireTicks"),
    ("ag", "I", "teleblockTicks"),
    ("ah", "D", "poisonDamage"),
    ("aG", "[I", "allotmentGrowthStages"),
    ("aH", "[I", "allotmentCropIds"),
    ("aI", "[I", "allotmentHarvestAmounts"),
    ("aJ", "[I", "allotmentPatchStates"),
    ("aK", "[J", "allotmentLastUpdateTicks"),
    ("aL", "[D", "allotmentDiseaseChanceMultipliers"),
    ("aM", "[Z", "allotmentProtectionFlags"),
    ("aN", "[I", "bushGrowthStages"),
    ("aO", "[I", "bushCropIds"),
    ("aP", "[I", "bushPatchStates"),
    ("aQ", "[J", "bushLastUpdateTicks"),
    ("aR", "[D", "bushDiseaseChanceMultipliers"),
    ("aS", "[Z", "bushSavedFlags"),
    ("aT", "[I", "flowerGrowthStages"),
    ("aU", "[I", "flowerCropIds"),
    ("aV", "[I", "flowerPatchStates"),
    ("aW", "[J", "flowerLastUpdateTicks"),
    ("aX", "[D", "flowerDiseaseChanceMultipliers"),
    ("aY", "[I", "fruitTreeGrowthStages"),
    ("aZ", "[I", "fruitTreeIds"),
    ("ba", "[I", "fruitTreePatchStates"),
    ("bb", "[J", "fruitTreeLastUpdateTicks"),
    ("bc", "[D", "fruitTreeDiseaseChanceMultipliers"),
    ("bd", "[Z", "fruitTreeSavedFlags"),
    ("be", "[I", "herbGrowthStages"),
    ("bf", "[I", "herbCropIds"),
    ("bg", "[I", "herbHarvestAmounts"),
    ("bh", "[I", "herbPatchStates"),
    ("bi", "[J", "herbLastUpdateTicks"),
    ("bj", "[D", "herbDiseaseChanceMultipliers"),
    ("bk", "[I", "hopsGrowthStages"),
    ("bl", "[I", "hopsCropIds"),
    ("bm", "[I", "hopsHarvestAmounts"),
    ("bn", "[I", "hopsPatchStates"),
    ("bo", "[J", "hopsLastUpdateTicks"),
    ("bp", "[D", "hopsDiseaseChanceMultipliers"),
    ("bq", "[Z", "hopsProtectionFlags"),
    ("br", "[I", "specialTreeGrowthStages"),
    ("bs", "[I", "specialTreeIds"),
    ("bt", "[I", "specialTreePatchStates"),
    ("bu", "[J", "specialTreeLastUpdateTicks"),
    ("bv", "[D", "specialTreeDiseaseChanceMultipliers"),
    ("bw", "[I", "specialCropGrowthStages"),
    ("bx", "[I", "specialCropIds"),
    ("by", "[I", "specialCropPatchStates"),
    ("bz", "[J", "specialCropLastUpdateTicks"),
    ("bA", "[D", "specialCropDiseaseChanceMultipliers"),
    ("bB", "[I", "treeGrowthStages"),
    ("bC", "[I", "treeIds"),
    ("bD", "[I", "treePatchData"),
    ("bE", "[I", "treePatchStates"),
    ("bF", "[J", "treeLastUpdateTicks"),
    ("bG", "[D", "treeDiseaseChanceMultipliers"),
    ("bH", "[Z", "treeSavedFlags"),
    ("bI", "[I", "compostBinStates"),
    ("bJ", "[J", "compostBinLastUpdateTicks"),
    ("bK", "[I", "compostBinItemIds"),
    ("bL", "[I", "farmingToolStoreAmounts"),
    ("bM", "[I", "configStates"),
    ("bN", "[I", "questProgress"),
    ("bQ", "[I", "currentLevels"),
    ("bR", "[J", "skillExperience"),
    ("ai", "I", "slayerMasterId"),
    ("aj", "Ljava/lang/String;", "slayerTaskName"),
    ("ak", "I", "slayerTaskAmount"),
    ("al", "Z", "usingAncients"),
    ("am", "Z", "brimhavenOpen"),
    ("an", "Z", "killedClueAttacker"),
    ("ao", "I", "gangAffiliation"),
    ("ap", "I", "piratesTreasureBananaCrateCount"),
    ("aq", "Z", "treasureTrailNavigationTaught"),
    ("ar", "I", "coalTruckAmount"),
    ("as", "I", "treasureTrailStepCount"),
    ("at", "Z", "cluePuzzleSolved"),
    ("bO", "[I", "questBitFlags"),
    ("bP", "[I", "questHookStates"),
    ("cB", "Ljava/util/ArrayList;", "bankTabs"),
    ("bS", "Z", "barrowsDoorPuzzleSolved"),
    ("bT", "Z", "barrowsChestOpened"),
    ("bU", "I", "barrowsRewardPotential"),
    ("bV", "[Z", "grandExchangeSellOfferFlags"),
    ("bW", "[I", "grandExchangeItemIds"),
    ("bX", "[I", "grandExchangeQuantities"),
    ("bY", "[I", "grandExchangeUnitPrices"),
    ("bZ", "[Z", "grandExchangeCancelledFlags"),
    ("ca", "[I", "grandExchangeCompletedQuantities"),
    ("cb", "[I", "grandExchangeTotalPrices"),
    ("cc", "[I", "grandExchangePrimaryCollectAmounts"),
    ("cd", "[I", "grandExchangeSecondaryCollectAmounts"),
    ("ce", "[Z", "grandExchangeFinishMessagePending"),
    ("cf", "I", "flourMillHopperGrainCount"),
    ("cg", "I", "questRandomSeed"),
    ("ch", "I", "publicChatMode"),
    ("ci", "I", "privateChatMode"),
    ("cj", "I", "tradeMode"),
    ("co", "I", "familyCrestGauntletItemId"),
    ("cp", "I", "mageArenaFlamesOfZamorakCastsRemaining"),
    ("cq", "I", "mageArenaSaradominStrikeCastsRemaining"),
    ("cr", "I", "mageArenaClawsOfGuthixCastsRemaining"),
    ("cs", "I", "mageArenaProgressStage"),
    ("ct", "I", "telekineticPizazzPoints"),
    ("cu", "I", "enchantmentPizazzPoints"),
    ("cv", "I", "alchemistPizazzPoints"),
    ("cw", "I", "graveyardPizazzPoints"),
    ("cx", "Z", "bonesToPeachesUnlocked"),
    ("cy", "I", "telekineticMazeIndex"),
    ("cz", "Z", "telekineticMazeSolved"),
    ("cA", "I", "telekineticConsecutiveMazesSolved"),
    ("cC", "I", "gameMode"),
    ("cD", "I", "barrowsRunsCompleted"),
    ("du", "[I", "godWarsKillCounts"),
    ("dv", "J", "godWarsLastAltarBlessingMillis"),
    ("dw", "B", "craftingThreadUseCount"),
    ("dy", "J", "membershipExpiresMillis"),
    ("dz", "I", "savedCacheVersion"),
    ("dA", "I", "godBookPageFlags"),
    ("dB", "Z", "swampCaveRopeAttached"),
    ("dC", "Z", "lampOilStillFilled"),
    ("dD", "I", "enterTheAbyssMiniquestState"),
    ("dq", "B", "botMode"),
    ("dr", "Z", "botEnabled"),
    ("dp", "B", "currentBotTaskTypeId"),
    ("do", "I", "currentBotTaskIndex"),
    ("dn", "B", "deferredBotTaskTypeId"),
    ("dm", "I", "deferredBotTaskIndex"),
    ("dl", "Ljava/lang/String;", "botTaskState"),
    ("dk", "[Lcom/rs2/model/item/ItemStack;", "botTaskRequiredItems"),
    ("dj", "I", "botFoodItemId"),
    ("di", "B", "botPathSegmentIndex"),
    ("dh", "B", "botPathWaypointIndex"),
    ("dg", "Z", "savedWorldRouteReversed"),
    ("df", "J", "botTaskSavedElapsedMillis"),
    ("de", "B", "botTaskDurationMinutes"),
    ("dd", "B", "savedWorldRouteIndex"),
    ("dc", "B", "tradeAdvertMode"),
    ("db", "I", "botAdvertItemId"),
    ("da", "I", "tradeAdvertQuantityRemaining"),
    ("cZ", "I", "tradeAdvertUnitPrice"),
    ("cY", "Z", "tradeAdvertScam"),
    ("cX", "Z", "tradeAdvertVariableQuantity"),
    ("cW", "I", "tradeAdvertLastOfferAmount"),
    ("cV", "B", "botShopBuyMode"),
    ("cU", "I", "botTaskItemId"),
    ("cT", "I", "botShopItemAmount"),
    ("cS", "Ljava/util/ArrayList;", "botShopSellItemIds"),
    ("cR", "Ljava/util/ArrayList;", "botCombatLoadoutItemIds"),
    ("cQ", "B", "botCombatStyle"),
    ("cP", "B", "botSkillTargetSkillId"),
    ("cO", "B", "botSkillTargetLevel"),
    ("cJ", "I", "botCompletionItemId"),
    ("cI", "I", "botCompletionItemAmount"),
    ("ds", "Z", "botTaskReturnToBankRequested"),
    ("dt", "I", "botElementalSpellIndex"),
    ("dE", "I", "cachedItemValue"),
    ("dG", "[I", "experienceForLevel"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/player/CharacterFileRecord", _name, _descriptor), _mapped_name)
_FARMING_MANAGER_FIELD_NAMES = [
    (
        "com/rs2/model/skill/farming/AllotmentPatchManager",
        [
            ("i", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "harvestAmounts"),
            ("b", "[I", "growthStages"),
            ("c", "[I", "cropIds"),
            ("e", "[I", "patchStates"),
            ("f", "[J", "lastUpdateTicks"),
            ("g", "[D", "diseaseChanceMultipliers"),
            ("h", "[Z", "protectionFlags"),
            ("j", "[Z", "fullyGrownFlags"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HerbPatchManager",
        [
            ("h", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "harvestAmounts"),
            ("b", "[I", "growthStages"),
            ("c", "[I", "cropIds"),
            ("e", "[I", "patchStates"),
            ("f", "[J", "lastUpdateTicks"),
            ("g", "[D", "diseaseChanceMultipliers"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HopsPatchManager",
        [
            ("i", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "harvestAmounts"),
            ("b", "[I", "growthStages"),
            ("c", "[I", "cropIds"),
            ("e", "[I", "patchStates"),
            ("f", "[J", "lastUpdateTicks"),
            ("g", "[D", "diseaseChanceMultipliers"),
            ("h", "[Z", "protectionFlags"),
            ("j", "[Z", "fullyGrownFlags"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/BushPatchManager",
        [
            ("h", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "growthStages"),
            ("b", "[I", "cropIds"),
            ("c", "[I", "patchStates"),
            ("d", "[J", "lastUpdateTicks"),
            ("e", "[D", "diseaseChanceMultipliers"),
            ("g", "[Z", "protectionFlags"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FlowerPatchManager",
        [
            ("g", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "growthStages"),
            ("b", "[I", "cropIds"),
            ("c", "[I", "patchStates"),
            ("d", "[J", "lastUpdateTicks"),
            ("e", "[D", "diseaseChanceMultipliers"),
            ("f", "[Z", "fullyGrownFlags"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreePatchManager",
        [
            ("h", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "growthStages"),
            ("b", "[I", "treeIds"),
            ("c", "[I", "patchStates"),
            ("d", "[J", "lastUpdateTicks"),
            ("e", "[D", "diseaseChanceMultipliers"),
            ("g", "[Z", "protectionFlags"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreePatchManager",
        [
            ("g", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "growthStages"),
            ("b", "[I", "treeIds"),
            ("c", "[I", "patchStates"),
            ("d", "[J", "lastUpdateTicks"),
            ("e", "[D", "diseaseChanceMultipliers"),
            ("f", "[Z", "calquatRegrowthFlags"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropPatchManager",
        [
            ("g", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "growthStages"),
            ("b", "[I", "cropIds"),
            ("c", "[I", "patchStates"),
            ("d", "[J", "lastUpdateTicks"),
            ("e", "[D", "diseaseChanceMultipliers"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/TreePatchManager",
        [
            ("h", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "growthStages"),
            ("b", "[I", "treeIds"),
            ("c", "[I", "patchData"),
            ("d", "[I", "patchStates"),
            ("e", "[J", "lastUpdateTicks"),
            ("f", "[D", "diseaseChanceMultipliers"),
            ("g", "[Z", "protectionFlags"),
            ("i", "[Z", "fullyGrownFlags"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/CompostBinManager",
        [
            ("d", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "states"),
            ("b", "[I", "itemIds"),
            ("c", "[J", "lastUpdateTicks"),
            ("e", "[I", "compostableItemIds"),
            ("f", "[I", "supercompostableItemIds"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FarmingToolStore",
        [
            ("b", "Lcom/rs2/model/player/Player;", "player"),
            ("a", "[I", "storedAmounts"),
            ("c", "[Lcom/rs2/model/item/ItemStack;", "storedToolDisplayItems"),
            ("d", "[Lcom/rs2/model/item/ItemStack;", "storedCompostDisplayItems"),
            ("e", "[Lcom/rs2/model/item/ItemStack;", "inventoryToolDisplayItems"),
            ("f", "[Lcom/rs2/model/item/ItemStack;", "inventoryCompostDisplayItems"),
            ("g", "[I", "noteableProduceItemIds"),
        ],
    ),
]
for _owner, _fields in _FARMING_MANAGER_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_FARMING_PATCH_VALUE_FIELD_NAMES = [
    (
        "com/rs2/model/skill/farming/AllotmentPatch",
        [
            ("i", "I", "index"),
            ("j", "[Lcom/rs2/model/Position;", "bounds"),
            ("k", "I", "objectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/BushPatch",
        [
            ("e", "I", "index"),
            ("f", "[Lcom/rs2/model/Position;", "bounds"),
            ("g", "I", "objectId"),
            ("h", "Ljava/util/Map;", "patchByObjectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FlowerPatch",
        [
            ("e", "I", "index"),
            ("f", "[Lcom/rs2/model/Position;", "bounds"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreePatch",
        [
            ("e", "I", "index"),
            ("f", "[Lcom/rs2/model/Position;", "bounds"),
            ("g", "I", "objectId"),
            ("h", "Ljava/util/Map;", "patchByObjectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HerbPatch",
        [
            ("e", "I", "index"),
            ("f", "[Lcom/rs2/model/Position;", "bounds"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HopsPatch",
        [
            ("e", "I", "index"),
            ("f", "[Lcom/rs2/model/Position;", "bounds"),
            ("g", "I", "objectId"),
            ("h", "Ljava/util/Map;", "patchByObjectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropPatch",
        [
            ("d", "I", "index"),
            ("e", "[Lcom/rs2/model/Position;", "bounds"),
            ("f", "I", "objectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreePatch",
        [
            ("e", "I", "index"),
            ("f", "[Lcom/rs2/model/Position;", "bounds"),
            ("g", "I", "objectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/TreePatch",
        [
            ("e", "I", "index"),
            ("f", "[Lcom/rs2/model/Position;", "bounds"),
            ("g", "I", "objectId"),
            ("h", "Ljava/util/Map;", "patchByObjectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/CompostBin",
        [
            ("e", "I", "index"),
            ("f", "Lcom/rs2/model/Position;", "position"),
            ("g", "Ljava/util/Map;", "binByIndex"),
        ],
    ),
]
for _owner, _fields in _FARMING_PATCH_VALUE_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_FARMING_PATCH_ENUM_CONSTANTS = [
    (
        "com/rs2/model/skill/farming/AllotmentPatch",
        [
            ("a", "CATHERBY_NORTH"),
            ("b", "CATHERBY_SOUTH"),
            ("c", "FALADOR_WEST"),
            ("d", "FALADOR_EAST"),
            ("e", "MORYTANIA_WEST"),
            ("f", "MORYTANIA_EAST"),
            ("g", "ARDOUGNE_NORTH"),
            ("h", "ARDOUGNE_SOUTH"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HerbPatch",
        [
            ("a", "ARDOUGNE"),
            ("b", "MORYTANIA"),
            ("c", "FALADOR"),
            ("d", "CATHERBY"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FlowerPatch",
        [
            ("a", "ARDOUGNE"),
            ("b", "MORYTANIA"),
            ("c", "FALADOR"),
            ("d", "CATHERBY"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/TreePatch",
        [
            ("a", "VARROCK"),
            ("b", "LUMBRIDGE"),
            ("c", "TAVERLEY"),
            ("d", "FALADOR"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreePatch",
        [
            ("a", "BRIMHAVEN"),
            ("b", "CATHERBY"),
            ("c", "GNOME_STRONGHOLD"),
            ("d", "TREE_GNOME_VILLAGE"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HopsPatch",
        [
            ("a", "LUMBRIDGE"),
            ("b", "SEERS_VILLAGE"),
            ("c", "YANILLE"),
            ("d", "ENTRANA"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropPatch",
        [
            ("a", "BELLADONNA"),
            ("b", "CACTUS"),
            ("c", "MUSHROOM"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreePatch",
        [
            ("a", "BRIMHAVEN_SPIRIT_TREE"),
            ("b", "TAI_BWO_WANNAI_CALQUAT"),
            ("c", "PORT_SARIM_SPIRIT_TREE"),
            ("d", "ETCETERIA_SPIRIT_TREE"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/CompostBin",
        [
            ("a", "FALADOR"),
            ("b", "CATHERBY"),
            ("c", "MORYTANIA"),
            ("d", "ARDOUGNE"),
        ],
    ),
]
for _owner, _constants in _FARMING_PATCH_ENUM_CONSTANTS:
    _descriptor = f"L{_owner};"
    for _old_name, _mapped_name in _constants:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_FARMING_TOOL_DEFINITION_ENUM_CONSTANTS = [
    ("a", "RAKE"),
    ("b", "SEED_DIBBER"),
    ("c", "SPADE"),
    ("d", "SECATEURS"),
    ("e", "MAGIC_SECATEURS"),
    ("f", "WATERING_CAN_EMPTY"),
    ("g", "WATERING_CAN_1"),
    ("h", "WATERING_CAN_2"),
    ("i", "WATERING_CAN_3"),
    ("j", "WATERING_CAN_4"),
    ("k", "WATERING_CAN_5"),
    ("l", "WATERING_CAN_6"),
    ("m", "WATERING_CAN_7"),
    ("n", "WATERING_CAN_8"),
    ("o", "GARDENING_TROWEL"),
    ("p", "BUCKET"),
    ("q", "COMPOST"),
    ("r", "SUPER_COMPOST"),
]
for _old_name, _mapped_name in _FARMING_TOOL_DEFINITION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            "com/rs2/model/skill/farming/FarmingToolDefinition",
            _old_name,
            "Lcom/rs2/model/skill/farming/FarmingToolDefinition;",
        ),
        _mapped_name,
    )
_FARMING_TOOL_DEFINITION_FIELD_NAMES = [
    ("s", "I", "storageIndex"),
    ("t", "I", "itemId"),
    ("u", "I", "configValue"),
    ("v", "I", "maxStoredAmount"),
    ("w", "I", "nameTextInterfaceId"),
    ("x", "I", "amountTextInterfaceId"),
    ("y", "Ljava/lang/String;", "displayName"),
    ("z", "Ljava/util/Map;", "definitionsByItemId"),
    ("A", "Ljava/util/Map;", "definitionsByStorageIndex"),
]
for _old_name, _descriptor, _mapped_name in _FARMING_TOOL_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (
            "com/rs2/model/skill/farming/FarmingToolDefinition",
            _old_name,
            _descriptor,
        ),
        _mapped_name,
    )
_FARMING_DEFINITION_ENUM_CONSTANTS = [
    (
        "com/rs2/model/skill/farming/AllotmentCropDefinition",
        [
            ("a", "POTATO"),
            ("b", "ONION"),
            ("c", "CABBAGE"),
            ("d", "TOMATO"),
            ("e", "SWEETCORN"),
            ("f", "STRAWBERRY"),
            ("g", "WATERMELON"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/AllotmentGrowthDefinition",
        [
            ("a", "POTATO"),
            ("b", "ONION"),
            ("c", "CABBAGE"),
            ("d", "TOMATO"),
            ("e", "SWEETCORN"),
            ("f", "STRAWBERRY"),
            ("g", "WATERMELON"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/BushDefinition",
        [
            ("a", "REDBERRY"),
            ("b", "CADAVABERRY"),
            ("c", "DWELLBERRY"),
            ("d", "JANGERBERRY"),
            ("e", "WHITEBERRY"),
            ("f", "POISON_IVY"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/BushGrowthDefinition",
        [
            ("a", "REDBERRY"),
            ("b", "CADAVABERRY"),
            ("c", "DWELLBERRY"),
            ("d", "JANGERBERRY"),
            ("e", "WHITEBERRY"),
            ("f", "POISON_IVY"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FlowerDefinition",
        [
            ("a", "MARIGOLD"),
            ("b", "ROSEMARY"),
            ("c", "NASTURTIUM"),
            ("d", "WOAD"),
            ("e", "LIMPWURT"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FlowerGrowthDefinition",
        [
            ("a", "MARIGOLD"),
            ("b", "ROSEMARY"),
            ("c", "NASTURTIUM"),
            ("d", "WOAD"),
            ("e", "LIMPWURT"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HerbDefinition",
        [
            ("a", "GUAM"),
            ("b", "MARRENTILL"),
            ("c", "TARROMIN"),
            ("d", "HARRALANDER"),
            ("e", "GOUTWEED"),
            ("f", "RANARR"),
            ("g", "TOADFLAX"),
            ("h", "IRIT"),
            ("i", "AVANTOE"),
            ("j", "KWUARM"),
            ("k", "SNAPDRAGON"),
            ("l", "CADANTINE"),
            ("m", "LANTADYME"),
            ("n", "DWARF_WEED"),
            ("o", "TORSTOL"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HerbGrowthDefinition",
        [
            ("a", "GUAM"),
            ("b", "MARRENTILL"),
            ("c", "TARROMIN"),
            ("d", "HARRALANDER"),
            ("e", "GOUTWEED"),
            ("f", "RANARR"),
            ("g", "TOADFLAX"),
            ("h", "IRIT"),
            ("i", "AVANTOE"),
            ("j", "KWUARM"),
            ("k", "SNAPDRAGON"),
            ("l", "CADANTINE"),
            ("m", "LANTADYME"),
            ("n", "DWARF_WEED"),
            ("o", "TORSTOL"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HopsDefinition",
        [
            ("a", "BARLEY"),
            ("b", "HAMMERSTONE"),
            ("c", "ASGARNIAN"),
            ("d", "JUTE"),
            ("e", "YANILLIAN"),
            ("f", "KRANDORIAN"),
            ("g", "WILDBLOOD"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HopsGrowthDefinition",
        [
            ("a", "BARLEY"),
            ("b", "HAMMERSTONE"),
            ("c", "ASGARNIAN"),
            ("d", "JUTE"),
            ("e", "YANILLIAN"),
            ("f", "KRANDORIAN"),
            ("g", "WILDBLOOD"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SaplingDefinition",
        [
            ("a", "OAK"),
            ("b", "WILLOW"),
            ("c", "MAPLE"),
            ("d", "YEW"),
            ("e", "MAGIC"),
            ("f", "SPIRIT_TREE"),
            ("g", "APPLE"),
            ("h", "BANANA"),
            ("i", "ORANGE"),
            ("j", "CURRY"),
            ("k", "PINEAPPLE"),
            ("l", "PAPAYA"),
            ("m", "PALM"),
            ("n", "CALQUAT"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/CropStorageDefinition",
        [
            ("a", "POTATO"),
            ("b", "ONION"),
            ("c", "CABBAGE"),
            ("d", "APPLE"),
            ("e", "ORANGE"),
            ("f", "STRAWBERRY"),
            ("g", "BANANA"),
            ("h", "TOMATO"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FarmedTreeDefinition",
        [
            ("a", "OAK"),
            ("b", "WILLOW"),
            ("c", "MAPLE"),
            ("d", "YEW"),
            ("e", "MAGIC"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FarmedTreeGrowthDefinition",
        [
            ("a", "OAK"),
            ("b", "WILLOW"),
            ("c", "MAPLE"),
            ("d", "YEW"),
            ("e", "MAGIC"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreeDefinition",
        [
            ("a", "APPLE"),
            ("b", "BANANA"),
            ("c", "ORANGE"),
            ("d", "CURRY"),
            ("e", "PINEAPPLE"),
            ("f", "PAPAYA"),
            ("g", "PALM"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreeGrowthDefinition",
        [
            ("a", "APPLE"),
            ("b", "BANANA"),
            ("c", "ORANGE"),
            ("d", "CURRY"),
            ("e", "PINEAPPLE"),
            ("f", "PAPAYA"),
            ("g", "PALM"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropDefinition",
        [
            ("a", "BELLADONNA"),
            ("b", "CACTUS"),
            ("c", "MUSHROOM"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropGrowthDefinition",
        [
            ("a", "BELLADONNA"),
            ("b", "CACTUS"),
            ("c", "MUSHROOM"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreeDefinition",
        [
            ("a", "SPIRIT_TREE"),
            ("b", "CALQUAT"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreeGrowthDefinition",
        [
            ("a", "SPIRIT_TREE"),
            ("b", "CALQUAT"),
        ],
    ),
]
for _owner, _constants in _FARMING_DEFINITION_ENUM_CONSTANTS:
    for _old_name, _mapped_name in _constants:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, f"L{_owner};"), _mapped_name)
_FARMING_GROWTH_DEFINITION_FIELD_NAMES = [
    (
        "com/rs2/model/skill/farming/AllotmentGrowthDefinition",
        [("h", "I", "cropId"), ("i", "[[Ljava/lang/String;", "growthMessages"), ("j", "Ljava/util/Map;", "definitionsByCropId")],
    ),
    (
        "com/rs2/model/skill/farming/BushGrowthDefinition",
        [("g", "I", "cropId"), ("h", "[[Ljava/lang/String;", "growthMessages"), ("i", "Ljava/util/Map;", "definitionsByCropId")],
    ),
    (
        "com/rs2/model/skill/farming/FlowerGrowthDefinition",
        [("f", "I", "cropId"), ("g", "[[Ljava/lang/String;", "growthMessages"), ("h", "Ljava/util/Map;", "definitionsByCropId")],
    ),
    (
        "com/rs2/model/skill/farming/HerbGrowthDefinition",
        [("p", "I", "cropId"), ("q", "[[Ljava/lang/String;", "growthMessages"), ("r", "Ljava/util/Map;", "definitionsByCropId")],
    ),
    (
        "com/rs2/model/skill/farming/HopsGrowthDefinition",
        [("h", "I", "cropId"), ("i", "[[Ljava/lang/String;", "growthMessages"), ("j", "Ljava/util/Map;", "definitionsByCropId")],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropGrowthDefinition",
        [("d", "I", "cropId"), ("e", "[[Ljava/lang/String;", "growthMessages"), ("f", "Ljava/util/Map;", "definitionsByCropId")],
    ),
    (
        "com/rs2/model/skill/farming/FarmedTreeGrowthDefinition",
        [("f", "I", "treeId"), ("g", "[[Ljava/lang/String;", "growthMessages"), ("h", "Ljava/util/Map;", "definitionsByTreeId")],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreeGrowthDefinition",
        [("h", "I", "treeId"), ("i", "[[Ljava/lang/String;", "growthMessages"), ("j", "Ljava/util/Map;", "definitionsByTreeId")],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreeGrowthDefinition",
        [("c", "I", "treeId"), ("d", "[[Ljava/lang/String;", "growthMessages"), ("e", "Ljava/util/Map;", "definitionsByTreeId")],
    ),
]
for _owner, _fields in _FARMING_GROWTH_DEFINITION_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_SAPLING_DEFINITION_FIELD_NAMES = [
    ("o", "I", "seedId"),
    ("p", "I", "seedlingId"),
    ("q", "I", "wateredSeedlingId"),
    ("r", "I", "saplingId"),
    ("s", "Ljava/util/Map;", "definitionsBySeedId"),
    ("t", "Ljava/util/Map;", "definitionsBySeedlingId"),
    ("u", "Ljava/util/Map;", "definitionsByWateredSeedlingId"),
]
for _old_name, _descriptor, _mapped_name in _SAPLING_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (
            "com/rs2/model/skill/farming/SaplingDefinition",
            _old_name,
            _descriptor,
        ),
        _mapped_name,
    )
_CROP_STORAGE_DEFINITION_FIELD_NAMES = [
    ("i", "I", "baseContainerItemId"),
    ("j", "I", "produceItemId"),
    ("k", "Z", "sack"),
    ("l", "Ljava/util/HashMap;", "definitionsByProduceItemId"),
]
for _old_name, _descriptor, _mapped_name in _CROP_STORAGE_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (
            "com/rs2/model/skill/farming/CropStorageDefinition",
            _old_name,
            _descriptor,
        ),
        _mapped_name,
    )
_FARMING_FARMER_DEFINITION_FIELD_NAMES = [
    ("u", "I", "npcId"),
    ("v", "Ljava/lang/String;", "patchType"),
    ("w", "[Ljava/lang/String;", "patchLabels"),
    ("x", "Ljava/util/Map;", "definitionsByNpcId"),
]
for _old_name, _descriptor, _mapped_name in _FARMING_FARMER_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (
            "com/rs2/model/skill/farming/FarmingFarmerDefinition",
            _old_name,
            _descriptor,
        ),
        _mapped_name,
    )
FIELD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/farming/FarmingFarmerHandler",
        "a",
        "[[Ljava/lang/String;",
    ),
    "adviceMessages",
)
FIELD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/farming/PlantPotHandler",
        "a",
        "Lcom/rs2/model/player/Player;",
    ),
    "player",
)
_FARMING_PRODUCE_DEFINITION_FIELD_NAMES = [
    (
        "com/rs2/model/skill/farming/AllotmentCropDefinition",
        [
            ("h", "I", "seedId"),
            ("i", "I", "produceItemId"),
            ("j", "I", "protectionCropId"),
            ("k", "I", "seedAmount"),
            ("l", "I", "requiredLevel"),
            ("m", "[I", "protectionPayment"),
            ("n", "I", "totalGrowthTicks"),
            ("o", "D", "diseaseChance"),
            ("p", "D", "plantingExperience"),
            ("q", "D", "harvestExperience"),
            ("r", "I", "configStartStage"),
            ("s", "I", "configEndStage"),
            ("t", "I", "harvestChanceLow"),
            ("u", "I", "harvestChanceHigh"),
            ("v", "Ljava/util/Map;", "definitionsBySeedId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/BushDefinition",
        [
            ("g", "I", "seedId"),
            ("h", "I", "produceItemId"),
            ("i", "I", "seedAmount"),
            ("j", "I", "requiredLevel"),
            ("k", "[I", "protectionPayment"),
            ("l", "I", "totalGrowthTicks"),
            ("m", "D", "diseaseChance"),
            ("n", "D", "plantingExperience"),
            ("o", "D", "harvestExperience"),
            ("p", "I", "configStartStage"),
            ("q", "I", "configEndStage"),
            ("r", "I", "healthCheckConfigStage"),
            ("s", "D", "healthCheckExperience"),
            ("t", "Ljava/util/Map;", "definitionsBySeedId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FlowerDefinition",
        [
            ("f", "I", "seedId"),
            ("g", "I", "produceItemId"),
            ("h", "I", "requiredLevel"),
            ("i", "I", "totalGrowthTicks"),
            ("j", "D", "diseaseChance"),
            ("k", "D", "plantingExperience"),
            ("l", "D", "harvestExperience"),
            ("m", "I", "configStartStage"),
            ("n", "I", "configEndStage"),
            ("o", "Ljava/util/Map;", "definitionsBySeedId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HerbDefinition",
        [
            ("p", "I", "seedId"),
            ("q", "I", "produceItemId"),
            ("r", "I", "requiredLevel"),
            ("s", "I", "totalGrowthTicks"),
            ("t", "D", "diseaseChance"),
            ("u", "D", "plantingExperience"),
            ("v", "D", "harvestExperience"),
            ("w", "I", "configStartStage"),
            ("x", "I", "configEndStage"),
            ("y", "I", "harvestChanceLow"),
            ("z", "I", "harvestChanceHigh"),
            ("A", "Ljava/util/Map;", "definitionsBySeedId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HopsDefinition",
        [
            ("h", "I", "seedId"),
            ("i", "I", "produceItemId"),
            ("j", "I", "seedAmount"),
            ("k", "I", "requiredLevel"),
            ("l", "[I", "protectionPayment"),
            ("m", "I", "totalGrowthTicks"),
            ("n", "D", "diseaseChance"),
            ("o", "D", "plantingExperience"),
            ("p", "D", "harvestExperience"),
            ("q", "I", "configStartStage"),
            ("r", "I", "configEndStage"),
            ("s", "I", "harvestChanceLow"),
            ("t", "I", "harvestChanceHigh"),
            ("u", "Ljava/util/Map;", "definitionsBySeedId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FarmedTreeDefinition",
        [
            ("f", "I", "saplingId"),
            ("g", "I", "rootItemId"),
            ("h", "I", "requiredLevel"),
            ("i", "[I", "protectionPayment"),
            ("j", "I", "totalGrowthTicks"),
            ("k", "D", "diseaseChance"),
            ("l", "D", "plantingExperience"),
            ("m", "D", "healthCheckExperience"),
            ("n", "I", "configStartStage"),
            ("o", "I", "configEndStage"),
            ("p", "I", "checkedTreeConfigStage"),
            ("q", "I", "stumpConfigStage"),
            ("r", "I", "treeObjectId"),
            ("s", "Ljava/util/Map;", "definitionsBySaplingId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreeDefinition",
        [
            ("h", "I", "saplingId"),
            ("i", "I", "produceItemId"),
            ("j", "I", "requiredLevel"),
            ("k", "[I", "protectionPayment"),
            ("l", "I", "totalGrowthTicks"),
            ("m", "D", "diseaseChance"),
            ("n", "D", "plantingExperience"),
            ("o", "D", "harvestExperience"),
            ("p", "I", "configStartStage"),
            ("q", "I", "configEndStage"),
            ("r", "I", "matureConfigStage"),
            ("s", "I", "stumpConfigStage"),
            ("t", "I", "healthCheckConfigStage"),
            ("u", "D", "healthCheckExperience"),
            ("v", "I", "diseasedConfigOffset"),
            ("w", "I", "deadConfigOffset"),
            ("x", "Ljava/util/Map;", "definitionsBySaplingId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropDefinition",
        [
            ("d", "I", "seedId"),
            ("e", "I", "produceItemId"),
            ("f", "I", "seedAmount"),
            ("g", "I", "requiredLevel"),
            ("h", "I", "totalGrowthTicks"),
            ("i", "D", "diseaseChance"),
            ("j", "D", "plantingExperience"),
            ("k", "D", "harvestExperience"),
            ("l", "I", "configStartStage"),
            ("m", "I", "configEndStage"),
            ("n", "I", "healthCheckConfigStage"),
            ("o", "D", "healthCheckExperience"),
            ("p", "I", "diseasedConfigOffset"),
            ("q", "I", "deadConfigOffset"),
            ("r", "Ljava/util/Map;", "definitionsBySeedId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreeDefinition",
        [
            ("c", "I", "saplingId"),
            ("d", "I", "produceItemId"),
            ("e", "I", "requiredLevel"),
            ("f", "I", "totalGrowthTicks"),
            ("g", "D", "diseaseChance"),
            ("h", "D", "plantingExperience"),
            ("i", "D", "harvestExperience"),
            ("j", "I", "configStartStage"),
            ("k", "I", "configEndStage"),
            ("l", "I", "healthCheckConfigStage"),
            ("m", "D", "healthCheckExperience"),
            ("n", "I", "diseasedConfigOffset"),
            ("o", "I", "deadConfigOffset"),
            ("p", "Ljava/util/Map;", "definitionsBySaplingId"),
        ],
    ),
]
for _owner, _fields in _FARMING_PRODUCE_DEFINITION_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_FARMING_TASK_FIELD_NAMES = []


def _farming_desc(_class_name):
    return f"Lcom/rs2/model/skill/farming/{_class_name};"


def _add_farming_task_fields(_task_class, _fields):
    _FARMING_TASK_FIELD_NAMES.append(
        (f"com/rs2/model/skill/farming/{_task_class}", _fields)
    )


_FARMING_TASK_TYPES = [
    (
        "Allotment",
        "AllotmentPatchManager",
        "AllotmentPatch",
        "AllotmentCropDefinition",
        "AllotmentGrowthDefinition",
        "seedId",
    ),
    (
        "Bush",
        "BushPatchManager",
        "BushPatch",
        "BushDefinition",
        "BushGrowthDefinition",
        "seedId",
    ),
    (
        "Flower",
        "FlowerPatchManager",
        "FlowerPatch",
        "FlowerDefinition",
        "FlowerGrowthDefinition",
        "seedId",
    ),
    (
        "FruitTree",
        "FruitTreePatchManager",
        "FruitTreePatch",
        "FruitTreeDefinition",
        "FruitTreeGrowthDefinition",
        "saplingId",
    ),
    (
        "Herb",
        "HerbPatchManager",
        "HerbPatch",
        "HerbDefinition",
        "HerbGrowthDefinition",
        "seedId",
    ),
    (
        "Hops",
        "HopsPatchManager",
        "HopsPatch",
        "HopsDefinition",
        "HopsGrowthDefinition",
        "seedId",
    ),
    (
        "SpecialCrop",
        "SpecialCropPatchManager",
        "SpecialCropPatch",
        "SpecialCropDefinition",
        "SpecialCropGrowthDefinition",
        "seedId",
    ),
    (
        "SpecialTree",
        "SpecialTreePatchManager",
        "SpecialTreePatch",
        "SpecialTreeDefinition",
        "SpecialTreeGrowthDefinition",
        "saplingId",
    ),
    (
        "Tree",
        "TreePatchManager",
        "TreePatch",
        "FarmedTreeDefinition",
        "FarmedTreeGrowthDefinition",
        "saplingId",
    ),
]
for (
    _task_prefix,
    _manager_class,
    _patch_class,
    _definition_class,
    _growth_definition_class,
    _planting_item_name,
) in _FARMING_TASK_TYPES:
    _manager_descriptor = _farming_desc(_manager_class)
    _patch_descriptor = _farming_desc(_patch_class)
    _definition_descriptor = _farming_desc(_definition_class)
    _growth_definition_descriptor = _farming_desc(_growth_definition_class)
    _add_farming_task_fields(
        f"{_task_prefix}ClearingTask",
        [
            ("a", _manager_descriptor, "manager"),
            ("b", "I", "animationId"),
            ("c", _patch_descriptor, "patch"),
        ],
    )
    _add_farming_task_fields(
        f"{_task_prefix}CompostTask",
        [
            ("a", _manager_descriptor, "manager"),
            ("b", _patch_descriptor, "patch"),
            ("c", "I", "compostItemId"),
        ],
    )
    _add_farming_task_fields(
        f"{_task_prefix}InspectTask",
        [
            ("a", _manager_descriptor, "manager"),
            ("b", _patch_descriptor, "patch"),
            ("c", _growth_definition_descriptor, "growthDefinition"),
        ],
    )
    _add_farming_task_fields(
        f"{_task_prefix}PlantingTask",
        [
            ("a", _manager_descriptor, "manager"),
            ("b", _patch_descriptor, "patch"),
            ("c", "I", _planting_item_name),
            ("d", _definition_descriptor, "definition"),
        ],
    )

for _task_prefix, _manager_class in [
    ("Allotment", "AllotmentPatchManager"),
    ("Bush", "BushPatchManager"),
    ("Flower", "FlowerPatchManager"),
    ("Hops", "HopsPatchManager"),
    ("SpecialCrop", "SpecialCropPatchManager"),
    ("SpecialTree", "SpecialTreePatchManager"),
]:
    _add_farming_task_fields(
        f"{_task_prefix}CureTask",
        [("a", _farming_desc(_manager_class), "manager")],
    )

_add_farming_task_fields(
    "HerbCureTask",
    [
        ("a", _farming_desc("HerbPatchManager"), "manager"),
        ("b", _farming_desc("HerbPatch"), "patch"),
    ],
)

for _task_prefix, _manager_class, _patch_class in [
    ("Allotment", "AllotmentPatchManager", "AllotmentPatch"),
    ("Flower", "FlowerPatchManager", "FlowerPatch"),
    ("Hops", "HopsPatchManager", "HopsPatch"),
]:
    _add_farming_task_fields(
        f"{_task_prefix}WateringTask",
        [
            ("a", _farming_desc(_manager_class), "manager"),
            ("b", _farming_desc(_patch_class), "patch"),
        ],
    )

for _task_prefix, _manager_class, _patch_class, _definition_class in [
    ("Allotment", "AllotmentPatchManager", "AllotmentPatch", "AllotmentCropDefinition"),
    ("Herb", "HerbPatchManager", "HerbPatch", "HerbDefinition"),
    ("Hops", "HopsPatchManager", "HopsPatch", "HopsDefinition"),
]:
    _add_farming_task_fields(
        f"{_task_prefix}HarvestTask",
        [
            ("a", _farming_desc(_manager_class), "manager"),
            ("b", "I", "actionSequence"),
            ("c", _farming_desc(_definition_class), "definition"),
            ("d", _farming_desc(_patch_class), "patch"),
        ],
    )

for _task_prefix, _manager_class, _patch_class, _definition_class in [
    ("Bush", "BushPatchManager", "BushPatch", "BushDefinition"),
    ("Flower", "FlowerPatchManager", "FlowerPatch", "FlowerDefinition"),
    ("FruitTree", "FruitTreePatchManager", "FruitTreePatch", "FruitTreeDefinition"),
    (
        "SpecialCrop",
        "SpecialCropPatchManager",
        "SpecialCropPatch",
        "SpecialCropDefinition",
    ),
    (
        "SpecialTree",
        "SpecialTreePatchManager",
        "SpecialTreePatch",
        "SpecialTreeDefinition",
    ),
]:
    _add_farming_task_fields(
        f"{_task_prefix}HarvestTask",
        [
            ("a", _farming_desc(_manager_class), "manager"),
            ("b", "I", "actionSequence"),
            ("c", _farming_desc(_patch_class), "patch"),
            ("d", _farming_desc(_definition_class), "definition"),
        ],
    )

_add_farming_task_fields(
    "FruitTreeCuttingTask",
    [
        ("a", _farming_desc("FruitTreePatchManager"), "manager"),
        ("b", "I", "actionSequence"),
        ("c", _farming_desc("FruitTreePatch"), "patch"),
    ],
)
for _task_class, _manager_class in [
    ("FruitTreePruneTask", "FruitTreePatchManager"),
    ("TreePruneTask", "TreePatchManager"),
]:
    _add_farming_task_fields(
        _task_class,
        [("a", _farming_desc(_manager_class), "manager")],
    )
_add_farming_task_fields(
    "TreeHealthCheckTask",
    [
        ("a", _farming_desc("TreePatchManager"), "manager"),
        ("b", "I", "actionSequence"),
        ("c", _farming_desc("FarmedTreeDefinition"), "definition"),
        ("d", _farming_desc("TreePatch"), "patch"),
    ],
)
_add_farming_task_fields(
    "ScarecrowPlantingTask",
    [
        ("a", _farming_desc("FlowerPatchManager"), "manager"),
        ("b", _farming_desc("FlowerPatch"), "patch"),
    ],
)
_add_farming_task_fields(
    "TreeStumpRegrowthTask",
    [
        ("a", _farming_desc("TreePatchManager"), "manager"),
        ("b", "I", "patchIndex"),
    ],
)
_add_farming_task_fields(
    "FarmedTreeCuttingTask",
    [
        ("a", _farming_desc("TreePatchManager"), "manager"),
        ("b", "I", "actionSequence"),
        ("c", "I", "animationId"),
        ("d", "Lcom/rs2/model/skill/woodcutting/TreeDefinition;", "treeDefinition"),
        ("e", "Lcom/rs2/model/skill/GatheringToolDefinition;", "gatheringTool"),
        ("f", "I", "treeObjectId"),
        ("g", _farming_desc("TreePatch"), "patch"),
        ("h", "I", "x"),
        ("i", "I", "y"),
    ],
)
_add_farming_task_fields(
    "CompostBinCloseTask",
    [("a", _farming_desc("CompostBinManager"), "manager")],
)
_add_farming_task_fields(
    "CompostBinOpenTask",
    [("a", _farming_desc("CompostBinManager"), "manager")],
)
_add_farming_task_fields(
    "CompostBinFillTask",
    [
        ("a", _farming_desc("CompostBinManager"), "manager"),
        ("b", "I", "actionSequence"),
        ("c", "I", "itemId"),
        ("d", "I", "binIndex"),
        ("e", "I", "fillAmount"),
    ],
)
_add_farming_task_fields(
    "CompostBinEmptyTask",
    [
        ("a", _farming_desc("CompostBinManager"), "manager"),
        ("b", "I", "actionSequence"),
        ("c", "I", "binIndex"),
        ("d", "I", "compostItemId"),
    ],
)
for _owner, _fields in _FARMING_TASK_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_GATHERING_TASK_FIELD_NAMES = [
    (
        "com/rs2/model/skill/fishing/FishingTask",
        [
            ("a", "Lcom/rs2/model/skill/fishing/FishingHandler;", "handler"),
            ("b", "I", "actionSequence"),
            ("c", "Lcom/rs2/model/npc/Npc;", "spotNpc"),
            (
                "d",
                "Lcom/rs2/model/skill/fishing/FishingSpotDefinition;",
                "spotDefinition",
            ),
        ],
    ),
    (
        "com/rs2/model/skill/mining/MiningTask",
        [
            ("a", "Lcom/rs2/model/skill/mining/MiningManager;", "manager"),
            ("b", "I", "actionSequence"),
            ("c", "I", "rockObjectId"),
            ("d", "I", "x"),
            ("e", "I", "y"),
            ("f", "Lcom/rs2/model/skill/GatheringToolDefinition;", "gatheringTool"),
            ("g", "I", "mineChanceLow"),
            ("h", "I", "mineChanceHigh"),
            ("i", "I", "oreItemId"),
            ("j", "I", "baseExperience"),
            ("k", "D", "depletionChance"),
            ("l", "I", "respawnTicks"),
        ],
    ),
    (
        "com/rs2/model/skill/mining/RuneEssenceMiningTask",
        [
            ("a", "Lcom/rs2/model/player/Player;", "player"),
            ("b", "I", "actionSequence"),
            ("c", "I", "animationId"),
            ("d", "I", "essenceItemId"),
        ],
    ),
    (
        "com/rs2/model/skill/woodcutting/WoodcuttingTask",
        [
            ("a", "Lcom/rs2/model/player/Player;", "player"),
            ("b", "I", "actionSequence"),
            (
                "c",
                "Lcom/rs2/model/skill/woodcutting/TreeDefinition;",
                "treeDefinition",
            ),
            ("d", "I", "x"),
            ("e", "I", "y"),
            ("f", "Lcom/rs2/model/skill/GatheringToolDefinition;", "gatheringTool"),
            ("g", "I", "treeObjectId"),
        ],
    ),
]
for _owner, _fields in _GATHERING_TASK_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_MINING_MANAGER_OWNER = "com/rs2/model/skill/mining/MiningManager"
_MINING_MANAGER_METHOD_NAMES = [
    ("a", "(I)Z", "canMineRock"),
    ("a", "(III)V", "startMining"),
    ("b", "(I)I", "rotateDepletedRockOrientation"),
    ("a", "(II)I", "rollMinedItemId"),
    ("c", "(I)Z", "isMineableRockObjectId"),
    ("d", "(I)Z", "prospectRock"),
    ("a", "(ILcom/rs2/model/Position;)I", "getRandomEventRockObjectId"),
    ("e", "(I)I", "getRestoredRockObjectId"),
    (
        "a",
        "(Lcom/rs2/model/skill/mining/MiningManager;)Lcom/rs2/model/player/Player;",
        "getPlayer",
    ),
    ("b", "(II)D", "getExperienceForMinedItem"),
    ("f", "(I)I", "getDepletedRockObjectId"),
]
for _old_name, _descriptor, _mapped_name in _MINING_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault((_MINING_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)
_MINING_MANAGER_FIELD_NAMES = [
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "[I", "graniteItemIds"),
    ("d", "[I", "sandstoneItemIds"),
    ("e", "[I", "commonGemItemIds"),
    ("f", "[I", "semipreciousGemItemIds"),
    ("a", "Lcom/rs2/util/RectangularArea;", "perfectGoldOreArea"),
]
for _old_name, _descriptor, _mapped_name in _MINING_MANAGER_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_MINING_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)
_PROSPECTING_TASK_OWNER = "com/rs2/model/skill/mining/ProspectingTask"
_PROSPECTING_TASK_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/skill/mining/MiningManager;", "manager"),
    ("b", "I", "rockObjectId"),
    ("c", "Ljava/lang/String;", "oreName"),
]
for _old_name, _descriptor, _mapped_name in _PROSPECTING_TASK_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_PROSPECTING_TASK_OWNER, _old_name, _descriptor), _mapped_name)
_GATHERING_TOOL_DEFINITION_OWNER = "com/rs2/model/skill/GatheringToolDefinition"
_GATHERING_TOOL_DEFINITION_ENUM_CONSTANTS = [
    ("a", "DRAGON_AXE"),
    ("b", "RUNE_AXE"),
    ("c", "ADAMANT_AXE"),
    ("d", "MITHRIL_AXE"),
    ("e", "BLACK_AXE"),
    ("f", "STEEL_AXE"),
    ("g", "IRON_AXE"),
    ("h", "BRONZE_AXE"),
    ("i", "RUNE_PICKAXE"),
    ("j", "ADAMANT_PICKAXE"),
    ("k", "MITHRIL_PICKAXE"),
    ("l", "STEEL_PICKAXE"),
    ("m", "IRON_PICKAXE"),
    ("n", "BRONZE_PICKAXE"),
]
for _old_name, _mapped_name in _GATHERING_TOOL_DEFINITION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            _GATHERING_TOOL_DEFINITION_OWNER,
            _old_name,
            f"L{_GATHERING_TOOL_DEFINITION_OWNER};",
        ),
        _mapped_name,
    )
_GATHERING_TOOL_DEFINITION_FIELD_NAMES = [
    ("o", "I", "toolItemId"),
    ("p", "I", "requiredLevel"),
    ("q", "[I", "animationIds"),
    ("r", "D", "toolSpeed"),
    ("s", "I", "graphicId"),
    ("t", "I", "toolHeadItemId"),
    ("u", "I", "brokenToolItemId"),
    ("v", "I", "skillId"),
]
for _old_name, _descriptor, _mapped_name in _GATHERING_TOOL_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_GATHERING_TOOL_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_GATHERING_TOOL_DEFINITION_METHOD_NAMES = [
    ("a", "()I", "getToolItemId"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()I", "getToolHeadItemId"),
    ("d", "()I", "getGatherAnimationId"),
    ("e", "()I", "getCanoeAnimationId"),
    ("f", "()D", "getToolSpeed"),
    ("g", "()I", "getGraphicId"),
    ("h", "()I", "getBrokenToolItemId"),
    ("i", "()I", "getSkillId"),
    ("j", "()I", "getRepairCostCoins"),
]
for _old_name, _descriptor, _mapped_name in _GATHERING_TOOL_DEFINITION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_GATHERING_TOOL_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_ITEM_COMBINATION_HANDLER_OWNER = "com/rs2/model/skill/ItemCombinationHandler"
_ITEM_COMBINATION_HANDLER_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "Lcom/rs2/util/RectangularArea;", "caveLightShortcutArea"),
]
for _old_name, _descriptor, _mapped_name in _ITEM_COMBINATION_HANDLER_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_ITEM_COMBINATION_HANDLER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_ITEM_COMBINATION_HANDLER_METHOD_NAMES = [
    (
        "a",
        "(Lcom/rs2/model/item/ItemStack;Lcom/rs2/model/item/ItemStack;)Z",
        "handleItemCombination",
    ),
    ("a", "(I)Z", "isGatheringToolItemId"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;I)Lcom/rs2/model/skill/GatheringToolDefinition;",
        "findUsableGatheringTool",
    ),
    (
        "b",
        "(Lcom/rs2/model/player/Player;I)Lcom/rs2/model/skill/GatheringToolDefinition;",
        "findOwnedGatheringTool",
    ),
    ("b", "(I)Ljava/util/ArrayList;", "getGatheringToolsForSkill"),
    (
        "c",
        "(Lcom/rs2/model/player/Player;I)Lcom/rs2/model/skill/GatheringToolDefinition;",
        "getOwnedOrFallbackGatheringTool",
    ),
    (
        "c",
        "(I)Lcom/rs2/model/skill/GatheringToolDefinition;",
        "forBrokenToolItemId",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;II)Z",
        "handleToolHeadAttachment",
    ),
    ("d", "(Lcom/rs2/model/player/Player;I)V", "breakGatheringTool"),
    ("e", "(Lcom/rs2/model/player/Player;I)Z", "repairBrokenGatheringTool"),
]
for _old_name, _descriptor, _mapped_name in _ITEM_COMBINATION_HANDLER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_ITEM_COMBINATION_HANDLER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_ITEM_COMBINATION_RECIPE_OWNER = "com/rs2/model/skill/ItemCombinationRecipe"
_ITEM_COMBINATION_RECIPE_ENUM_CONSTANTS = [
    ("a", "UNFERMENTED_WINE"),
    ("b", "GNOMEBOWL"),
    ("c", "CHOCOLATE_BOMB_FROM_CHOCOLATE"),
    ("d", "CHOCOLATE_BOMB_FROM_LEAVES"),
    ("e", "PYRE_LOG_FROM_SACRED_OIL_4"),
    ("f", "PYRE_LOG_FROM_SACRED_OIL_3"),
    ("g", "PYRE_LOG_FROM_SACRED_OIL_2"),
    ("h", "OAK_PYRE_LOG_FROM_SACRED_OIL_4"),
    ("i", "OAK_PYRE_LOG_FROM_SACRED_OIL_3"),
    ("j", "OAK_PYRE_LOG_FROM_SACRED_OIL_2"),
    ("k", "WILLOW_PYRE_LOG_FROM_SACRED_OIL_4"),
    ("l", "WILLOW_PYRE_LOG_FROM_SACRED_OIL_3"),
    ("m", "MAPLE_PYRE_LOG_FROM_SACRED_OIL_4"),
    ("n", "MAPLE_PYRE_LOG_FROM_SACRED_OIL_3"),
    ("o", "YEW_PYRE_LOG_FROM_SACRED_OIL_4"),
    ("p", "MAGIC_PYRE_LOG_FROM_SACRED_OIL_4"),
    ("q", "RED_FIRELIGHTER_LOGS"),
    ("r", "GREEN_FIRELIGHTER_LOGS"),
    ("s", "BLUE_FIRELIGHTER_LOGS"),
    ("t", "CRYSTAL_KEY"),
    ("u", "SOFT_CLAY"),
    ("v", "CANDLE_LANTERN_WITH_CANDLE"),
    ("w", "CANDLE_LANTERN_WITH_LIT_CANDLE"),
    ("x", "OIL_LAMP_IN_LANTERN_FRAME"),
    ("y", "BULLSEYE_LANTERN_LENS"),
    ("z", "SAPPHIRE_BULLSEYE_LANTERN"),
]
for _old_name, _mapped_name in _ITEM_COMBINATION_RECIPE_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            _ITEM_COMBINATION_RECIPE_OWNER,
            _old_name,
            f"L{_ITEM_COMBINATION_RECIPE_OWNER};",
        ),
        _mapped_name,
    )
for _old_name, _descriptor, _mapped_name in [
    ("A", "I", "firstItemId"),
    ("B", "I", "secondItemId"),
    ("C", "[Lcom/rs2/model/item/ItemStack;", "requiredItems"),
    ("D", "[Lcom/rs2/model/item/ItemStack;", "productItems"),
    ("E", "I", "animationId"),
    ("F", "[I", "skillRequirement"),
    ("G", "D", "experience"),
    ("H", "Ljava/lang/String;", "message"),
    ("I", "[Lcom/rs2/model/skill/ItemCombinationRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault(
        (_ITEM_COMBINATION_RECIPE_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
for _old_name, _descriptor, _mapped_name in [
    ("a", "(II)Lcom/rs2/model/skill/ItemCombinationRecipe;", "forItemIds"),
    (
        "a",
        "(Lcom/rs2/model/skill/ItemCombinationRecipe;)[I",
        "getSkillRequirement",
    ),
    (
        "b",
        "(Lcom/rs2/model/skill/ItemCombinationRecipe;)[Lcom/rs2/model/item/ItemStack;",
        "getRequiredItems",
    ),
    (
        "c",
        "(Lcom/rs2/model/skill/ItemCombinationRecipe;)Ljava/lang/String;",
        "getMessage",
    ),
    (
        "d",
        "(Lcom/rs2/model/skill/ItemCombinationRecipe;)[Lcom/rs2/model/item/ItemStack;",
        "getProductItems",
    ),
    (
        "e",
        "(Lcom/rs2/model/skill/ItemCombinationRecipe;)I",
        "getAnimationId",
    ),
    (
        "f",
        "(Lcom/rs2/model/skill/ItemCombinationRecipe;)D",
        "getExperience",
    ),
]:
    METHOD_NAME_MAP.setdefault(
        (_ITEM_COMBINATION_RECIPE_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_MINEABLE_ROCK_DEFINITION_OWNER = "com/rs2/model/skill/mining/MineableRockDefinition"
_MINEABLE_ROCK_DEFINITION_ENUM_CONSTANTS = [
    ("a", "COPPER"),
    ("b", "TIN"),
    ("k", "BLURITE"),
    ("c", "IRON"),
    ("d", "COAL"),
    ("l", "GEM_ROCK"),
    ("m", "SANDSTONE"),
    ("n", "GRANITE"),
    ("e", "GOLD"),
    ("f", "SILVER"),
    ("g", "MITHRIL"),
    ("h", "ADAMANTITE"),
    ("i", "RUNITE"),
    ("j", "CLAY"),
    ("o", "DEPLETED_ROCK"),
]
for _old_name, _mapped_name in _MINEABLE_ROCK_DEFINITION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            _MINEABLE_ROCK_DEFINITION_OWNER,
            _old_name,
            f"L{_MINEABLE_ROCK_DEFINITION_OWNER};",
        ),
        _mapped_name,
    )
_MINEABLE_ROCK_DEFINITION_FIELD_NAMES = [
    ("p", "[I", "objectIds"),
    ("q", "I", "oreItemId"),
    ("r", "I", "requiredLevel"),
    ("s", "I", "baseExperience"),
    ("t", "I", "respawnTicks"),
    ("u", "D", "depletionChance"),
    ("v", "I", "mineChanceLow"),
    ("w", "I", "mineChanceHigh"),
]
for _old_name, _descriptor, _mapped_name in _MINEABLE_ROCK_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_MINEABLE_ROCK_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_MINEABLE_ROCK_DEFINITION_METHOD_NAMES = [
    ("a", "(I)Lcom/rs2/model/skill/mining/MineableRockDefinition;", "forObjectId"),
    (
        "a",
        "([Lcom/rs2/model/skill/mining/MineableRockDefinition;)[I",
        "collectObjectIds",
    ),
    (
        "a",
        "(Lcom/rs2/model/skill/mining/MineableRockDefinition;)I",
        "getOreItemId",
    ),
    (
        "b",
        "(Lcom/rs2/model/skill/mining/MineableRockDefinition;)I",
        "getMineChanceLow",
    ),
    (
        "c",
        "(Lcom/rs2/model/skill/mining/MineableRockDefinition;)I",
        "getMineChanceHigh",
    ),
    (
        "d",
        "(Lcom/rs2/model/skill/mining/MineableRockDefinition;)D",
        "getDepletionChance",
    ),
    (
        "e",
        "(Lcom/rs2/model/skill/mining/MineableRockDefinition;)I",
        "getBaseExperience",
    ),
    (
        "f",
        "(Lcom/rs2/model/skill/mining/MineableRockDefinition;)I",
        "getRespawnTicks",
    ),
    (
        "g",
        "(Lcom/rs2/model/skill/mining/MineableRockDefinition;)I",
        "getRequiredLevel",
    ),
]
for _old_name, _descriptor, _mapped_name in _MINEABLE_ROCK_DEFINITION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_MINEABLE_ROCK_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_WOODCUTTING_HANDLER_OWNER = "com/rs2/model/skill/woodcutting/WoodcuttingHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;IIIZ)V", "startWoodcutting"),
    ("a", "()V", "sendProjectileToNearbyPlayers"),
]:
    METHOD_NAME_MAP.setdefault((_WOODCUTTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

_WOODCUTTING_TREE_DEFINITION_OWNER = "com/rs2/model/skill/woodcutting/TreeDefinition"
_WOODCUTTING_TREE_DEFINITION_ENUM_CONSTANTS = [
    ("j", "ACHEY_TREE"),
    ("a", "TREE"),
    ("b", "OAK"),
    ("c", "WILLOW"),
    ("k", "TEAK"),
    ("d", "MAPLE"),
    ("l", "HOLLOW_TREE"),
    ("m", "MAHOGANY"),
    ("e", "YEW"),
    ("f", "MAGIC"),
    ("g", "DRAMEN_TREE"),
    ("h", "STRANGE_MUSICAL_TREE"),
    ("i", "VINES"),
]
for _old_name, _mapped_name in _WOODCUTTING_TREE_DEFINITION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            _WOODCUTTING_TREE_DEFINITION_OWNER,
            _old_name,
            f"L{_WOODCUTTING_TREE_DEFINITION_OWNER};",
        ),
        _mapped_name,
    )
_WOODCUTTING_TREE_DEFINITION_FIELD_NAMES = [
    ("n", "[I", "objectIds"),
    ("o", "[I", "entNpcIds"),
    ("p", "I", "requiredLevel"),
    ("q", "D", "experience"),
    ("r", "I", "logItemId"),
    ("s", "I", "stumpObjectId"),
    ("t", "I", "respawnTicksLow"),
    ("u", "I", "respawnTicksHigh"),
    ("v", "D", "depletionChance"),
    ("w", "I", "cutChanceLow"),
    ("x", "I", "cutChanceHigh"),
]
for _old_name, _descriptor, _mapped_name in _WOODCUTTING_TREE_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_WOODCUTTING_TREE_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_WOODCUTTING_TREE_DEFINITION_METHOD_NAMES = [
    ("a", "(I)Lcom/rs2/model/skill/woodcutting/TreeDefinition;", "forObjectId"),
    (
        "a",
        "(ILcom/rs2/model/skill/woodcutting/TreeDefinition;)I",
        "getObjectIdIndex",
    ),
    (
        "a",
        "([Lcom/rs2/model/skill/woodcutting/TreeDefinition;)[I",
        "collectObjectIds",
    ),
    ("b", "(I)Lcom/rs2/model/skill/woodcutting/TreeDefinition;", "forEntNpcId"),
    ("a", "(IZ)Lcom/rs2/model/skill/woodcutting/TreeDefinition;", "forTargetId"),
    ("a", "()[I", "getEntNpcIds"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()D", "getExperience"),
    ("d", "()I", "getLogItemId"),
    ("e", "()I", "getStumpObjectId"),
    ("f", "()I", "getRespawnTicksLow"),
    ("g", "()I", "getRespawnTicksHigh"),
    ("h", "()D", "getDepletionChance"),
    ("i", "()I", "getCutChanceLow"),
    ("j", "()I", "getCutChanceHigh"),
    (
        "a",
        "(Lcom/rs2/model/skill/woodcutting/TreeDefinition;)D",
        "getDepletionChance",
    ),
]
for _old_name, _descriptor, _mapped_name in _WOODCUTTING_TREE_DEFINITION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_WOODCUTTING_TREE_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_CANOE_TREE_DEFINITION_OWNER = "com/rs2/model/travel/canoe/CanoeTreeDefinition"
_CANOE_TREE_DEFINITION_ENUM_CONSTANTS = [
    ("a", "LUMBRIDGE_TREE"),
    ("b", "CHAMPIONS_GUILD_TREE"),
    ("c", "BARBARIAN_VILLAGE_TREE"),
    ("d", "EDGEVILLE_TREE"),
]
for _old_name, _mapped_name in _CANOE_TREE_DEFINITION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            _CANOE_TREE_DEFINITION_OWNER,
            _old_name,
            f"L{_CANOE_TREE_DEFINITION_OWNER};",
        ),
        _mapped_name,
    )
_CANOE_TREE_DEFINITION_FIELD_NAMES = [
    ("e", "[I", "objectIds"),
    ("f", "I", "requiredLevel"),
    ("g", "I", "configShift"),
]
for _old_name, _descriptor, _mapped_name in _CANOE_TREE_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_CANOE_TREE_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_CANOE_TREE_DEFINITION_METHOD_NAMES = [
    ("a", "(I)Lcom/rs2/model/travel/canoe/CanoeTreeDefinition;", "forObjectId"),
    ("a", "()I", "getRequiredLevel"),
    ("b", "()I", "getConfigShift"),
]
for _old_name, _descriptor, _mapped_name in _CANOE_TREE_DEFINITION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_CANOE_TREE_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_CANOE_STATION_OWNER = "com/rs2/model/travel/canoe/CanoeStation"
_CANOE_STATION_ENUM_CONSTANTS = [
    ("f", "LUMBRIDGE"),
    ("g", "CHAMPIONS_GUILD"),
    ("h", "BARBARIAN_VILLAGE"),
    ("i", "EDGEVILLE"),
    ("a", "WILDERNESS"),
]
for _old_name, _mapped_name in _CANOE_STATION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (_CANOE_STATION_OWNER, _old_name, f"L{_CANOE_STATION_OWNER};"),
        _mapped_name,
    )
_CANOE_STATION_FIELD_NAMES = [
    ("b", "I", "buttonId"),
    ("c", "[I", "destinationInterfaceIds"),
    ("d", "Ljava/lang/String;", "displayName"),
    ("e", "Lcom/rs2/model/Position;", "destination"),
]
for _old_name, _descriptor, _mapped_name in _CANOE_STATION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_CANOE_STATION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_CANOE_TYPE_DEFINITION_OWNER = "com/rs2/model/travel/canoe/CanoeTypeDefinition"
_CANOE_TYPE_DEFINITION_ENUM_CONSTANTS = [
    ("a", "LOG"),
    ("i", "DUGOUT"),
    ("j", "STABLE_DUGOUT"),
    ("k", "WAKA"),
]
for _old_name, _mapped_name in _CANOE_TYPE_DEFINITION_ENUM_CONSTANTS:
    FIELD_NAME_MAP.setdefault(
        (
            _CANOE_TYPE_DEFINITION_OWNER,
            _old_name,
            f"L{_CANOE_TYPE_DEFINITION_OWNER};",
        ),
        _mapped_name,
    )
_CANOE_TYPE_DEFINITION_FIELD_NAMES = [
    ("b", "I", "buttonId"),
    ("c", "I", "requiredLevel"),
    ("d", "I", "experience"),
    ("e", "I", "configValue"),
    ("f", "[I", "levelInterfaceIds"),
    ("g", "I", "travelRange"),
    ("h", "Z", "canEnterWilderness"),
]
for _old_name, _descriptor, _mapped_name in _CANOE_TYPE_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_CANOE_TYPE_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_CANOE_TRAVEL_MANAGER_OWNER = "com/rs2/model/travel/canoe/CanoeTravelManager"
FIELD_NAME_MAP.setdefault((_CANOE_TRAVEL_MANAGER_OWNER, "a", "I"), "CANOE_CONFIG_ID")
_CANOE_TRAVEL_MANAGER_METHOD_NAMES = [
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/travel/canoe/CanoeStation;I)Z",
        "canTravelToStation",
    ),
    ("a", "(Lcom/rs2/model/player/Player;III)V", "handleCanoeTreeClick"),
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "handleBuildButton"),
    ("b", "(Lcom/rs2/model/player/Player;I)Z", "handleDestinationButton"),
]
for _old_name, _descriptor, _mapped_name in _CANOE_TRAVEL_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_CANOE_TRAVEL_MANAGER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_CANOE_TASK_FIELD_NAMES = [
    (
        "com/rs2/model/travel/canoe/CanoeBuildTask",
        [
            ("a", "Lcom/rs2/model/player/Player;", "player"),
            ("b", "I", "actionSequence"),
            ("c", "Lcom/rs2/model/travel/canoe/CanoeTypeDefinition;", "canoeType"),
            ("d", "Lcom/rs2/model/skill/GatheringToolDefinition;", "gatheringTool"),
        ],
    ),
    (
        "com/rs2/model/travel/canoe/CanoeTreeCutTask",
        [
            ("a", "Lcom/rs2/model/player/Player;", "player"),
            ("b", "I", "actionSequence"),
            ("c", "Lcom/rs2/model/travel/canoe/CanoeTreeDefinition;", "treeDefinition"),
            ("d", "Lcom/rs2/model/skill/GatheringToolDefinition;", "gatheringTool"),
        ],
    ),
]
for _owner, _fields in _CANOE_TASK_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_CHARGED_JEWELRY_DEFINITION_OWNER = "com/rs2/model/travel/ChargedJewelryDefinition"
_CHARGED_JEWELRY_DEFINITION_FIELD_NAMES = [
    (
        "b",
        "Lcom/rs2/model/travel/ChargedJewelryDefinition;",
        "AMULET_OF_GLORY",
    ),
    (
        "c",
        "Lcom/rs2/model/travel/ChargedJewelryDefinition;",
        "RING_OF_DUELING",
    ),
    (
        "d",
        "Lcom/rs2/model/travel/ChargedJewelryDefinition;",
        "GAMES_NECKLACE",
    ),
    ("a", "[I", "itemIdsByDescendingCharge"),
    (
        "e",
        "[Lcom/rs2/model/travel/ChargedJewelryDefinition;",
        "VALUES",
    ),
]
for _old_name, _descriptor, _mapped_name in _CHARGED_JEWELRY_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_CHARGED_JEWELRY_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
METHOD_NAME_MAP.setdefault(
    (_CHARGED_JEWELRY_DEFINITION_OWNER, "a", "()[I"),
    "getItemIdsByDescendingCharge",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/GameplayHelper", "g", "(I)I"),
    "getNextDegradedJewelryItemId",
)
_GNOME_GLIDER_DESTINATION_OWNER = "com/rs2/model/travel/GnomeGliderDestination"
_GNOME_GLIDER_DESTINATION_FIELD_NAMES = [
    ("d", "Lcom/rs2/model/travel/GnomeGliderDestination;", "SINDARPOS"),
    ("e", "Lcom/rs2/model/travel/GnomeGliderDestination;", "TA_QUIR_PRIW"),
    ("f", "Lcom/rs2/model/travel/GnomeGliderDestination;", "LEMANTO_ANDRA"),
    ("g", "Lcom/rs2/model/travel/GnomeGliderDestination;", "KAR_HEWO"),
    ("h", "Lcom/rs2/model/travel/GnomeGliderDestination;", "GANDIUS"),
    ("i", "Lcom/rs2/model/travel/GnomeGliderDestination;", "LEMANTOLLY_UNDRI"),
    ("a", "I", "buttonId"),
    ("b", "Lcom/rs2/model/Position;", "destination"),
    ("c", "I", "routeIndex"),
    (
        "j",
        "[Lcom/rs2/model/travel/GnomeGliderDestination;",
        "VALUES",
    ),
]
for _old_name, _descriptor, _mapped_name in _GNOME_GLIDER_DESTINATION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_GNOME_GLIDER_DESTINATION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_TRAVEL_ROUTE_FIELD_NAMES = [
    (
        "com/rs2/model/travel/GnomeGliderTravelTask",
        [
            ("a", "Lcom/rs2/model/player/Player;", "player"),
            (
                "b",
                "Lcom/rs2/model/travel/GnomeGliderDestination;",
                "destination",
            ),
        ],
    ),
    (
        "com/rs2/model/travel/ShipTravelArrivalTask",
        [
            ("a", "Lcom/rs2/model/travel/ShipRoute;", "route"),
            ("b", "Lcom/rs2/model/player/Player;", "player"),
        ],
    ),
]
for _owner, _fields in _TRAVEL_ROUTE_FIELD_NAMES:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_SHIP_ROUTE_OWNER = "com/rs2/model/travel/ShipRoute"
_SHIP_ROUTE_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/travel/ShipRoute;", "PORT_SARIM_TO_ENTRANA"),
    ("b", "Lcom/rs2/model/travel/ShipRoute;", "ENTRANA_TO_PORT_SARIM"),
    ("c", "Lcom/rs2/model/travel/ShipRoute;", "PORT_SARIM_TO_CRANDOR"),
    ("r", "Lcom/rs2/model/travel/ShipRoute;", "CRANDOR_TO_PORT_SARIM"),
    ("d", "Lcom/rs2/model/travel/ShipRoute;", "PORT_SARIM_TO_KARAMJA"),
    ("e", "Lcom/rs2/model/travel/ShipRoute;", "KARAMJA_TO_PORT_SARIM"),
    ("f", "Lcom/rs2/model/travel/ShipRoute;", "ARDOUGNE_TO_BRIMHAVEN"),
    ("g", "Lcom/rs2/model/travel/ShipRoute;", "BRIMHAVEN_TO_ARDOUGNE"),
    ("s", "Lcom/rs2/model/travel/ShipRoute;", "UNUSED1"),
    ("t", "Lcom/rs2/model/travel/ShipRoute;", "UNUSED2"),
    ("u", "Lcom/rs2/model/travel/ShipRoute;", "PORT_KHAZARD_TO_SHIP_YARD"),
    ("v", "Lcom/rs2/model/travel/ShipRoute;", "SHIP_YARD_TO_PORT_KHAZARD"),
    ("w", "Lcom/rs2/model/travel/ShipRoute;", "CAIRN_ISLAND_TO_SHIP_YARD"),
    ("x", "Lcom/rs2/model/travel/ShipRoute;", "PORT_SARIM_TO_PEST_CONTROL"),
    ("y", "Lcom/rs2/model/travel/ShipRoute;", "PEST_CONTROL_TO_PORT_SARIM"),
    ("z", "Lcom/rs2/model/travel/ShipRoute;", "FELDIP_HILLS_TO_CAIRN_ISLAND"),
    ("h", "Lcom/rs2/model/travel/ShipRoute;", "RELLEKKA_TO_WATERBIRTH"),
    ("i", "Lcom/rs2/model/travel/ShipRoute;", "WATERBIRTH_TO_RELLEKKA"),
    ("j", "Lcom/rs2/model/travel/ShipRoute;", "RELLEKKA_TO_MISCELLANIA"),
    ("k", "Lcom/rs2/model/travel/ShipRoute;", "MISCELLANIA_TO_RELLEKKA"),
    ("l", "Ljava/lang/String;", "arrivalName"),
    ("m", "I", "fareCoins"),
    ("n", "Lcom/rs2/model/Position;", "destinationPosition"),
    ("o", "Lcom/rs2/model/Position;", "interfaceDestinationPosition"),
    ("p", "I", "arrivalDelayTicks"),
    ("q", "I", "jingleDelayTicks"),
    ("A", "[Lcom/rs2/model/travel/ShipRoute;", "VALUES"),
]
for _old_name, _descriptor, _mapped_name in _SHIP_ROUTE_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_SHIP_ROUTE_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_HAJEDY_CART_ROUTE_OWNER = "com/rs2/model/travel/HajedyCartRoute"
_HAJEDY_CART_ROUTE_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/travel/HajedyCartRoute;", "BRIMHAVEN_TO_SHILO"),
    ("b", "Lcom/rs2/model/travel/HajedyCartRoute;", "SHILO_TO_BRIMHAVEN"),
    ("c", "I", "fareCoins"),
    ("d", "Lcom/rs2/model/Position;", "destination"),
    ("e", "[Lcom/rs2/model/travel/HajedyCartRoute;", "VALUES"),
]
for _old_name, _descriptor, _mapped_name in _HAJEDY_CART_ROUTE_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_HAJEDY_CART_ROUTE_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/travel/TravelManager",
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/travel/ShipRoute;)Z",
    ),
    "handleShipRoute",
)
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/travel/TravelManager",
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/travel/HajedyCartRoute;)Z",
    ),
    "handleHajedyCartRoute",
)
_MUSIC_TRACK_DEFINITION_OWNER = "com/rs2/model/music/MusicTrackDefinition"
_MUSIC_TRACK_DEFINITION_FIELD_NAMES = [
    ("b", "I", "trackId"),
    ("c", "Ljava/lang/String;", "name"),
    ("d", "I", "buttonId"),
    ("e", "I", "unlockConfigId"),
    ("f", "I", "unlockBitMask"),
    ("a", "I", "trackCount"),
    (
        "g",
        "[Lcom/rs2/model/music/MusicTrackDefinition;",
        "definitionsByTrackId",
    ),
]
for _old_name, _descriptor, _mapped_name in _MUSIC_TRACK_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_MUSIC_TRACK_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_MUSIC_TRACK_DEFINITION_METHOD_NAMES = [
    ("a", "()V", "loadDefinitions"),
    ("b", "()I", "getTrackId"),
    ("c", "()Ljava/lang/String;", "getName"),
    ("d", "()I", "getButtonId"),
    ("e", "()I", "getUnlockConfigId"),
    ("f", "()I", "getUnlockBitMask"),
    (
        "a",
        "(I)Lcom/rs2/model/music/MusicTrackDefinition;",
        "forTrackId",
    ),
]
for _old_name, _descriptor, _mapped_name in _MUSIC_TRACK_DEFINITION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_MUSIC_TRACK_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_QUEST_AREA_OWNER = "com/rs2/model/quest/QuestArea"
for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "areaId"),
    ("b", "Lcom/rs2/model/Position;", "targetPosition"),
    ("c", "Lcom/rs2/util/RectangularArea;", "areaBounds"),
]:
    FIELD_NAME_MAP.setdefault((_QUEST_AREA_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getAreaId"),
    ("b", "()Lcom/rs2/model/Position;", "getTargetPosition"),
    ("c", "()Lcom/rs2/util/RectangularArea;", "getAreaBounds"),
]:
    METHOD_NAME_MAP.setdefault((_QUEST_AREA_OWNER, _old_name, _descriptor), _mapped_name)

_BARROWS_REWARD_ENTRY_OWNER = "com/rs2/model/gameplay/barrows/BarrowsRewardEntry"
for _old_name, _mapped_name in [
    ("a", "itemId"),
    ("b", "minAmount"),
    ("c", "maxAmount"),
    ("d", "rollThreshold"),
]:
    FIELD_NAME_MAP.setdefault((_BARROWS_REWARD_ENTRY_OWNER, _old_name, "I"), _mapped_name)

FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/gameplay/barrows/BarrowsManager", "f", "Ljava/util/ArrayList;"),
    "rewardTable",
)

_BARROWS_MANAGER_OWNER = "com/rs2/model/gameplay/barrows/BarrowsManager"
for _old_name, _descriptor, _mapped_name in [
    ("a", "[I", "prayerDrainModelItemIds"),
    ("b", "[I", "prayerDrainModelInterfaceIds"),
    ("c", "[[I", "brotherSarcophagusNpcIds"),
    ("d", "[Lcom/rs2/model/Position;", "targetBrotherCryptPositions"),
    ("e", "[[I", "brotherRewardItemIds"),
    ("g", "[I", "tunnelTargetRoomIds"),
    ("h", "[Lcom/rs2/model/Position;", "tunnelEntryPositions"),
    ("k", "[I", "cryptNpcIds"),
]:
    FIELD_NAME_MAP.setdefault((_BARROWS_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;III)Z", "handleTunnelDoorObject"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;)V", "spawnRandomCryptNpc"),
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "handleFirstObjectAction"),
    ("a", "(Lcom/rs2/model/player/Player;)Z", "digIntoCrypt"),
    ("e", "(Lcom/rs2/model/player/Player;)V", "awardChestRewards"),
    ("f", "(Lcom/rs2/model/player/Player;)I", "countKilledBrothers"),
    ("b", "(Lcom/rs2/model/player/Player;)Z", "ensurePrayerDrainTask"),
    ("g", "(Lcom/rs2/model/player/Player;)V", "openDoorPuzzle"),
    ("a", "(Lcom/rs2/model/player/Player;Z)V", "generateTunnelRoute"),
    ("c", "(Lcom/rs2/model/player/Player;)V", "resetBarrowsState"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)V", "recordNpcKill"),
    ("d", "(Lcom/rs2/model/player/Player;)I", "countKilledBrothers"),
]:
    METHOD_NAME_MAP.setdefault((_BARROWS_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)

_BARROWS_REPAIR_OWNER = "com/rs2/model/item/action/BarrowsRepairHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "[I", "repairNpcIds"),
    ("b", "I", "repairedItemId"),
    ("c", "I", "baseDegradedItemId"),
    ("d", "[[Lcom/rs2/model/item/action/BarrowsRepairHandler;", "repairDefinitions"),
]:
    FIELD_NAME_MAP.setdefault((_BARROWS_REPAIR_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;ILcom/rs2/model/item/ItemStack;)Z", "handleItemOnNpc"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;)Z", "repairItem"),
    ("a", "()I", "getFullyDegradedItemId"),
    ("a", "(Lcom/rs2/model/item/ItemStack;)I", "calculateRepairCost"),
    ("b", "(Lcom/rs2/model/item/ItemStack;)Lcom/rs2/model/item/action/BarrowsRepairHandler;", "forItem"),
    ("a", "(II)Lcom/rs2/model/item/action/BarrowsRepairHandler;", "findByDegradedItemIdAndPartIndex"),
]:
    METHOD_NAME_MAP.setdefault((_BARROWS_REPAIR_OWNER, _old_name, _descriptor), _mapped_name)

_GOD_BOOK_OWNER = "com/rs2/model/item/action/GodBookHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "damagedSaradominBookId"),
    ("b", "I", "holyBookId"),
    ("c", "I", "damagedZamorakBookId"),
    ("d", "I", "unholyBookId"),
    ("e", "I", "damagedGuthixBookId"),
    ("f", "I", "bookOfBalanceId"),
    ("g", "I", "saradominPage1Id"),
    ("h", "I", "saradominPage2Id"),
    ("i", "I", "saradominPage3Id"),
    ("j", "I", "saradominPage4Id"),
    ("k", "I", "zamorakPage1Id"),
    ("l", "I", "zamorakPage2Id"),
    ("m", "I", "zamorakPage3Id"),
    ("n", "I", "zamorakPage4Id"),
    ("o", "I", "guthixPage1Id"),
    ("p", "I", "guthixPage2Id"),
    ("q", "I", "guthixPage3Id"),
    ("r", "I", "guthixPage4Id"),
    ("s", "I", "saradominPage1Bit"),
    ("t", "I", "saradominPage2Bit"),
    ("u", "I", "saradominPage3Bit"),
    ("v", "I", "saradominPage4Bit"),
    ("w", "I", "zamorakPage1Bit"),
    ("x", "I", "zamorakPage2Bit"),
    ("y", "I", "zamorakPage3Bit"),
    ("z", "I", "zamorakPage4Bit"),
    ("A", "I", "guthixPage1Bit"),
    ("B", "I", "guthixPage2Bit"),
    ("C", "I", "guthixPage3Bit"),
    ("D", "I", "guthixPage4Bit"),
    ("E", "[I", "saradominPageBits"),
    ("F", "[I", "zamorakPageBits"),
    ("G", "[I", "guthixPageBits"),
    ("H", "I", "saradominRecitationAnimationId"),
    ("I", "I", "zamorakRecitationAnimationId"),
    ("J", "I", "guthixRecitationAnimationId"),
    ("K", "I", "weddingCeremonyOptionId"),
    ("L", "I", "lastRitesOptionId"),
    ("M", "I", "blessingsOptionId"),
    ("N", "I", "preachOptionId"),
    ("O", "[Ljava/lang/String;", "saradominWeddingCeremonyLines"),
    ("P", "[Ljava/lang/String;", "saradominLastRitesLines"),
    ("Q", "[Ljava/lang/String;", "saradominBlessingLines"),
    ("R", "[Ljava/lang/String;", "saradominPreachLines"),
    ("S", "[Ljava/lang/String;", "zamorakWeddingCeremonyLines"),
    ("T", "[Ljava/lang/String;", "zamorakLastRitesLines"),
    ("U", "[Ljava/lang/String;", "zamorakBlessingLines"),
    ("V", "[Ljava/lang/String;", "zamorakPreachLines"),
    ("W", "[Ljava/lang/String;", "guthixWeddingCeremonyLines"),
    ("X", "[Ljava/lang/String;", "guthixLastRitesLines"),
    ("Y", "[Ljava/lang/String;", "guthixBlessingLines"),
    ("Z", "[Ljava/lang/String;", "guthixPreachLines"),
]:
    FIELD_NAME_MAP.setdefault((_GOD_BOOK_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "showMissingPages"),
    ("e", "(Lcom/rs2/model/player/Player;I)Ljava/util/ArrayList;", "getMissingPageNumbers"),
    ("a", "(Lcom/rs2/model/player/Player;II)Z", "handlePageOnBook"),
    ("b", "(Lcom/rs2/model/player/Player;II)V", "completeBookIfFilled"),
    ("b", "(Lcom/rs2/model/player/Player;I)V", "giveReplacementBook"),
    ("c", "(Lcom/rs2/model/player/Player;I)Z", "openRecitationDialogue"),
    ("d", "(Lcom/rs2/model/player/Player;I)V", "startRecitation"),
]:
    METHOD_NAME_MAP.setdefault((_GOD_BOOK_OWNER, _old_name, _descriptor), _mapped_name)

_GOD_BOOK_RECITATION_OWNER = "com/rs2/model/item/action/GodBookRecitationEvent"
FIELD_NAME_MAP.setdefault(
    (_GOD_BOOK_RECITATION_OWNER, "a", "Lcom/rs2/model/player/Player;"),
    "player",
)
FIELD_NAME_MAP.setdefault(
    (_GOD_BOOK_RECITATION_OWNER, "b", "[Ljava/lang/String;"),
    "recitationLines",
)

_BIRD_NEST_OWNER = "com/rs2/model/item/action/BirdNestSearchHandler"
for _old_name, _mapped_name in [
    ("a", "commonSeedNestRewards"),
    ("b", "uncommonSeedNestRewards"),
    ("c", "rareSeedNestRewards"),
    ("d", "veryRareSeedNestRewards"),
    ("e", "commonRingNestRewards"),
    ("f", "uncommonRingNestRewards"),
    ("g", "rareRingNestRewards"),
    ("h", "veryRareRingNestRewards"),
]:
    FIELD_NAME_MAP.setdefault((_BIRD_NEST_OWNER, _old_name, "[I"), _mapped_name)
METHOD_NAME_MAP.setdefault(
    (_BIRD_NEST_OWNER, "a", "(Lcom/rs2/model/player/Player;I)Z"),
    "searchNest",
)

_CASKET_REWARD_OWNER = "com/rs2/model/item/action/CasketRewardHandler"
for _old_name, _mapped_name in [
    ("a", "commonRewards"),
    ("b", "uncommonRewards"),
    ("c", "rareRewards"),
]:
    FIELD_NAME_MAP.setdefault((_CASKET_REWARD_OWNER, _old_name, "[I"), _mapped_name)
METHOD_NAME_MAP.setdefault(
    (_CASKET_REWARD_OWNER, "a", "(Lcom/rs2/model/player/Player;)V"),
    "openCasket",
)

_DYE_MIXING_OWNER = "com/rs2/model/item/action/DyeMixingHandler"
for _old_name, _mapped_name in [
    ("a", "redDyeId"),
    ("b", "yellowDyeId"),
    ("c", "blueDyeId"),
    ("d", "orangeDyeId"),
    ("e", "greenDyeId"),
    ("f", "purpleDyeId"),
]:
    FIELD_NAME_MAP.setdefault((_DYE_MIXING_OWNER, _old_name, "I"), _mapped_name)
METHOD_NAME_MAP.setdefault(
    (_DYE_MIXING_OWNER, "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "mixDyes",
)

_MYSTIC_STAFF_OWNER = "com/rs2/model/item/action/MysticStaffEnchantment"
for _old_name, _mapped_name in [
    ("a", "AIR"),
    ("b", "WATER"),
    ("c", "EARTH"),
    ("d", "FIRE"),
    ("e", "LAVA"),
    ("f", "MUD"),
]:
    FIELD_NAME_MAP.setdefault(
        (_MYSTIC_STAFF_OWNER, _old_name, "Lcom/rs2/model/item/action/MysticStaffEnchantment;"),
        _mapped_name,
    )
for _old_name, _descriptor, _mapped_name in [
    ("g", "I", "buttonId"),
    ("h", "I", "battlestaffItemId"),
    ("i", "I", "mysticStaffItemId"),
    ("j", "Ljava/util/HashMap;", "byButtonId"),
]:
    FIELD_NAME_MAP.setdefault((_MYSTIC_STAFF_OWNER, _old_name, _descriptor), _mapped_name)
for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/item/action/MysticStaffEnchantment;", "forButtonId"),
    ("a", "()I", "getBattlestaffItemId"),
    ("b", "()I", "getMysticStaffItemId"),
]:
    METHOD_NAME_MAP.setdefault((_MYSTIC_STAFF_OWNER, _old_name, _descriptor), _mapped_name)

_SPINNING_PLATE_OWNER = "com/rs2/model/item/action/SpinningPlateHandler"
for _old_name, _mapped_name in [
    ("a", "spinningPlateItemId"),
    ("b", "brokenPlateItemId"),
    ("c", "startSpinAnimationId"),
    ("d", "catchPlateAnimationId"),
    ("e", "breakPlateAnimationId"),
]:
    FIELD_NAME_MAP.setdefault((_SPINNING_PLATE_OWNER, _old_name, "I"), _mapped_name)
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "spinPlate"),
    ("a", "()I", "getBreakPlateAnimationId"),
    ("b", "()I", "getSpinningPlateItemId"),
    ("c", "()I", "getBrokenPlateItemId"),
    ("d", "()I", "getCatchPlateAnimationId"),
]:
    METHOD_NAME_MAP.setdefault((_SPINNING_PLATE_OWNER, _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/item/action/SpinningPlateResultEvent", "a", "Lcom/rs2/model/player/Player;"),
    "player",
)
FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/item/action/BrokenPlateDropEvent", "a", "Lcom/rs2/model/player/Player;"),
    "player",
)

_TOY_HORSEY_OWNER = "com/rs2/model/item/action/ToyHorseyHandler"
FIELD_NAME_MAP.setdefault((_TOY_HORSEY_OWNER, "a", "[Ljava/lang/String;"), "forcedTextLines")
FIELD_NAME_MAP.setdefault((_TOY_HORSEY_OWNER, "b", "[[I"), "itemAnimationPairs")
METHOD_NAME_MAP.setdefault(
    (_TOY_HORSEY_OWNER, "a", "(Lcom/rs2/model/player/Player;I)Z"),
    "play",
)
FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/item/action/ToyHorseyUnlockEvent", "a", "Lcom/rs2/model/player/Player;"),
    "player",
)

_DEGRADABLE_EQUIPMENT_OWNER = "com/rs2/model/item/DegradableEquipmentHandler"
FIELD_NAME_MAP.setdefault(
    (_DEGRADABLE_EQUIPMENT_OWNER, "a", "[Ljava/lang/String;"),
    "barrowsSetNameTokens",
)
METHOD_NAME_MAP.setdefault(
    (_DEGRADABLE_EQUIPMENT_OWNER, "a", "(Lcom/rs2/model/player/Player;)V"),
    "degradeEquipmentAfterCombat",
)

_BARROWS_DOOR_PUZZLE_OWNER = "com/rs2/model/gameplay/barrows/BarrowsDoorPuzzle"
for _old_name, _mapped_name in [
    ("a", "displayOptionObjectIds"),
    ("b", "answerObjectIds"),
]:
    FIELD_NAME_MAP.setdefault((_BARROWS_DOOR_PUZZLE_OWNER, _old_name, "[I"), _mapped_name)

_BARROWS_TUNNEL_ROOM_OWNER = "com/rs2/model/gameplay/barrows/BarrowsTunnelRoom"
for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "roomId"),
    ("b", "[I", "connectedRoomIds"),
    ("c", "[I", "doorBitIndexes"),
    ("d", "Lcom/rs2/util/RectangularArea;", "roomBounds"),
]:
    FIELD_NAME_MAP.setdefault((_BARROWS_TUNNEL_ROOM_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("i", "[I", "puzzlePromptModelInterfaceIds"),
    ("j", "[I", "puzzleAnswerModelInterfaceIds"),
    ("l", "[Lcom/rs2/model/gameplay/barrows/BarrowsTunnelRoom;", "tunnelRooms"),
    ("m", "[Lcom/rs2/model/gameplay/barrows/BarrowsDoorPuzzle;", "doorPuzzles"),
]:
    FIELD_NAME_MAP.setdefault(
        (_BARROWS_MANAGER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )

for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/gameplay/barrows/BarrowsPrayerDrainTask", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/gameplay/barrows/BarrowsPrayerDrainResetTask", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/gameplay/barrows/BarrowsPrayerDrainResetTask", "b", "I", "interfaceId"),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("bQ", "Z", "barrowsDoorPuzzleSolved"),
    ("bR", "I", "activeBarrowsDoorPuzzleIndex"),
    ("bT", "[I", "activeBarrowsDoorPuzzleAnswerObjectIds"),
    ("bU", "Z", "barrowsChestOpened"),
    ("hF", "[Z", "barrowsKilledBrothers"),
    ("hG", "I", "barrowsKillCount"),
    ("hH", "I", "barrowsTargetBrotherIndex"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/player/BarrowsChestDamageTask", "a", "Lcom/rs2/model/player/Player;"),
    "player",
)

for _old_name, _descriptor, _mapped_name in [
    ("b", "(IZ)V", "setBarrowsBrotherKilled"),
    ("eS", "()[Z", "getBarrowsKilledBrothers"),
    ("au", "(I)Z", "isBarrowsBrotherKilled"),
    ("av", "(I)V", "setBarrowsKillCount"),
    ("eT", "()I", "getBarrowsKillCount"),
    ("aw", "(I)V", "setBarrowsTargetBrotherIndex"),
    ("eU", "()I", "getBarrowsTargetBrotherIndex"),
    ("bl", "()V", "startBarrowsChestDamage"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)

_CUTSCENE_OWNER = "com/rs2/model/cutscene/Cutscene"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/cutscene/CutsceneStep;)V", "addStep"),
    ("a", "(I)Lcom/rs2/model/npc/Npc;", "getNpc"),
    ("a", "()Lcom/rs2/model/player/Player;", "getPlayer"),
    ("b", "()V", "addCustomSteps"),
    ("c", "()V", "startDialogue"),
    ("d", "()V", "setupScene"),
    ("e", "()V", "finishCutscene"),
    ("f", "()V", "startCutscene"),
    ("g", "()I", "getMovementLockDurationMillis"),
    ("h", "()I", "getTotalDelayTicks"),
]:
    METHOD_NAME_MAP.setdefault((_CUTSCENE_OWNER, _old_name, _descriptor), _mapped_name)

_RESTLESS_GHOST_CUTSCENE_OWNER = "com/rs2/model/cutscene/restlessghost/RestlessGhostCutscene"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/cutscene/CutsceneStep;)V", "addStep"),
    ("a", "(I)Lcom/rs2/model/npc/Npc;", "getNpc"),
    ("a", "()Lcom/rs2/model/player/Player;", "getPlayer"),
    (
        "a",
        "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;I)V",
        "sendReleaseProjectile",
    ),
]:
    METHOD_NAME_MAP.setdefault((_RESTLESS_GHOST_CUTSCENE_OWNER, _old_name, _descriptor), _mapped_name)

for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/cutscene/Cutscene", "a", "Ljava/util/ArrayList;", "steps"),
    ("com/rs2/model/cutscene/Cutscene", "b", "Ljava/util/ArrayList;", "npcs"),
    ("com/rs2/model/cutscene/Cutscene", "c", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/cutscene/CutsceneStep", "a", "I", "delayTicks"),
    ("com/rs2/model/cutscene/CutsceneStepTask", "a", "Lcom/rs2/model/cutscene/CutsceneStep;", "step"),
    ("com/rs2/model/cutscene/CutsceneEndTask", "a", "Lcom/rs2/model/cutscene/Cutscene;", "cutscene"),
    ("com/rs2/model/cutscene/CutsceneSceneSetupStep", "a", "Lcom/rs2/model/cutscene/Cutscene;", "cutscene"),
    ("com/rs2/model/cutscene/CutsceneSceneSetupStep", "b", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/cutscene/CutsceneDialogueStartStep", "a", "Lcom/rs2/model/cutscene/Cutscene;", "cutscene"),
    ("com/rs2/model/cutscene/CutsceneDialogueStartStep", "b", "Lcom/rs2/model/player/Player;", "player"),
    (
        "com/rs2/model/cutscene/restlessghost/RestlessGhostCutscene",
        "a",
        "Lcom/rs2/model/quest/QuestScript;",
        "questScript",
    ),
    ("com/rs2/model/cutscene/restlessghost/RestlessGhostCutscene", "b", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/cutscene/restlessghost/RestlessGhostCutscene", "c", "Lcom/rs2/model/npc/Npc;", "ghost"),
    (
        "com/rs2/model/cutscene/restlessghost/RestlessGhostTextStep",
        "a",
        "Lcom/rs2/model/cutscene/restlessghost/RestlessGhostCutscene;",
        "cutscene",
    ),
    (
        "com/rs2/model/cutscene/restlessghost/RestlessGhostAnimationStep",
        "a",
        "Lcom/rs2/model/cutscene/restlessghost/RestlessGhostCutscene;",
        "cutscene",
    ),
    (
        "com/rs2/model/cutscene/restlessghost/RestlessGhostReleaseStep",
        "a",
        "Lcom/rs2/model/cutscene/restlessghost/RestlessGhostCutscene;",
        "cutscene",
    ),
    (
        "com/rs2/model/cutscene/restlessghost/RestlessGhostCompletionStep",
        "a",
        "Lcom/rs2/model/cutscene/restlessghost/RestlessGhostCutscene;",
        "cutscene",
    ),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _owner in [
    "com/rs2/model/cutscene/CutsceneStep",
    "com/rs2/model/cutscene/CutsceneSceneSetupStep",
    "com/rs2/model/cutscene/CutsceneDialogueStartStep",
    "com/rs2/model/cutscene/restlessghost/RestlessGhostTextStep",
    "com/rs2/model/cutscene/restlessghost/RestlessGhostAnimationStep",
    "com/rs2/model/cutscene/restlessghost/RestlessGhostReleaseStep",
    "com/rs2/model/cutscene/restlessghost/RestlessGhostCompletionStep",
]:
    METHOD_NAME_MAP.setdefault((_owner, "a", "()V"), "executeStep")

for _owner in [
    "com/rs2/model/cutscene/Cutscene",
    "com/rs2/model/cutscene/restlessghost/RestlessGhostCutscene",
]:
    METHOD_NAME_MAP.setdefault((_owner, "b", "()V"), "addCustomSteps")
    METHOD_NAME_MAP.setdefault((_owner, "c", "()V"), "startDialogue")
    METHOD_NAME_MAP.setdefault((_owner, "d", "()V"), "setupScene")

METHOD_NAME_MAP.setdefault(("com/rs2/model/cutscene/CutsceneStep", "b", "()I"), "getDelayTicks")

_MULTIWAY_AREA_DEFINITION_OWNER = "com/rs2/model/area/MultiwayAreaDefinition"
for _old_name, _descriptor, _mapped_name in [
    ("b", "I", "regionCount"),
    ("c", "Lcom/rs2/util/RectangularArea;", "areaBounds"),
    ("d", "[I", "regionIds"),
    ("e", "I", "definitionCount"),
    (
        "a",
        "[Lcom/rs2/model/area/MultiwayAreaDefinition;",
        "definitions",
    ),
]:
    FIELD_NAME_MAP.setdefault(
        (_MULTIWAY_AREA_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
for _old_name, _descriptor, _mapped_name in [
    ("a", "()V", "loadDefinitions"),
    ("b", "()Lcom/rs2/util/RectangularArea;", "getAreaBounds"),
    ("c", "()I", "getRegionCount"),
    ("d", "()[I", "getRegionIds"),
    (
        "a",
        "(I)Lcom/rs2/model/area/MultiwayAreaDefinition;",
        "forDefinitionId",
    ),
]:
    METHOD_NAME_MAP.setdefault(
        (_MULTIWAY_AREA_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_MUSIC_AREA_DEFINITION_OWNER = "com/rs2/model/music/MusicAreaDefinition"
_MUSIC_AREA_DEFINITION_FIELD_NAMES = [
    ("b", "I", "areaId"),
    ("c", "I", "regionCount"),
    ("d", "I", "priority"),
    ("e", "I", "trackId"),
    ("f", "Lcom/rs2/util/RectangularArea;", "areaBounds"),
    ("g", "[I", "regionIds"),
    ("a", "I", "areaCount"),
    (
        "h",
        "[Lcom/rs2/model/music/MusicAreaDefinition;",
        "definitionsByAreaId",
    ),
]
for _old_name, _descriptor, _mapped_name in _MUSIC_AREA_DEFINITION_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_MUSIC_AREA_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_MUSIC_AREA_DEFINITION_METHOD_NAMES = [
    ("a", "()V", "loadDefinitions"),
    ("b", "()I", "getAreaId"),
    ("c", "()I", "getRegionCount"),
    ("d", "()I", "getPriority"),
    ("e", "()I", "getTrackId"),
    ("f", "()Lcom/rs2/util/RectangularArea;", "getAreaBounds"),
    ("g", "()[I", "getRegionIds"),
    (
        "a",
        "(I)Lcom/rs2/model/music/MusicAreaDefinition;",
        "forAreaId",
    ),
]
for _old_name, _descriptor, _mapped_name in _MUSIC_AREA_DEFINITION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_MUSIC_AREA_DEFINITION_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_MUSIC_MANAGER_OWNER = "com/rs2/model/music/MusicManager"
for _old_name, _descriptor, _mapped_name in [
    ("a", "Ljava/util/List;", "buttonlessTrackIds"),
    ("b", "I", "currentAreaId"),
    ("c", "I", "currentTrackId"),
]:
    FIELD_NAME_MAP.setdefault((_MUSIC_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)
for _old_name, _descriptor, _mapped_name in [
    (
        "a",
        "(Lcom/rs2/model/player/Player;)V",
        "updateForPlayerPosition",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;I)V",
        "unlockTrack",
    ),
    (
        "b",
        "(Lcom/rs2/model/player/Player;)V",
        "unlockAllTracks",
    ),
    (
        "b",
        "(Lcom/rs2/model/player/Player;I)Z",
        "isTrackUnlocked",
    ),
]:
    METHOD_NAME_MAP.setdefault((_MUSIC_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)
_SKILL_GUIDE_ENTRY_OWNER = "com/rs2/model/skill/guide/SkillGuideEntry"
_SKILL_GUIDE_CATEGORY_OWNER = "com/rs2/model/skill/guide/SkillGuideCategory"
_SKILL_GUIDE_MANAGER_OWNER = "com/rs2/model/skill/guide/SkillGuideManager"
_SKILL_GUIDE_ENTRY_FIELD_NAMES = [
    ("a", "Ljava/lang/String;", "label"),
    ("b", "I", "itemId"),
    ("d", "I", "requiredLevel"),
    ("e", "Z", "membersOnly"),
    ("c", "Z", "suppressCategoryPrefix"),
]
for _old_name, _descriptor, _mapped_name in _SKILL_GUIDE_ENTRY_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_SKILL_GUIDE_ENTRY_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_SKILL_GUIDE_CATEGORY_FIELD_NAMES = [
    ("a", "Ljava/lang/String;", "name"),
    ("b", "Ljava/util/ArrayList;", "entries"),
    ("c", "Z", "prefixEntriesWithName"),
    ("d", "Z", "skipItemDefinitionLookup"),
]
for _old_name, _descriptor, _mapped_name in _SKILL_GUIDE_CATEGORY_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_SKILL_GUIDE_CATEGORY_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_SKILL_GUIDE_MANAGER_FIELD_NAMES = [
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("a", "I", "selectedSkillIndex"),
    ("c", "[I", "displayItemIds"),
    ("d", "Ljava/util/ArrayList;", "attackCategories"),
    ("e", "Ljava/util/ArrayList;", "hitpointsCategories"),
    ("f", "Ljava/util/ArrayList;", "miningCategories"),
    ("g", "Ljava/util/ArrayList;", "strengthCategories"),
    ("h", "Ljava/util/ArrayList;", "agilityCategories"),
    ("i", "Ljava/util/ArrayList;", "smithingCategories"),
    ("j", "Ljava/util/ArrayList;", "defenceCategories"),
    ("k", "Ljava/util/ArrayList;", "herbloreCategories"),
    ("l", "Ljava/util/ArrayList;", "fishingCategories"),
    ("m", "Ljava/util/ArrayList;", "rangedCategories"),
    ("n", "Ljava/util/ArrayList;", "thievingCategories"),
    ("o", "Ljava/util/ArrayList;", "cookingCategories"),
    ("p", "Ljava/util/ArrayList;", "prayerCategories"),
    ("q", "Ljava/util/ArrayList;", "craftingCategories"),
    ("r", "Ljava/util/ArrayList;", "firemakingCategories"),
    ("s", "Ljava/util/ArrayList;", "magicCategories"),
    ("t", "Ljava/util/ArrayList;", "fletchingCategories"),
    ("u", "Ljava/util/ArrayList;", "woodcuttingCategories"),
    ("v", "Ljava/util/ArrayList;", "runecraftingCategories"),
    ("w", "Ljava/util/ArrayList;", "slayerCategories"),
    ("x", "Ljava/util/ArrayList;", "farmingCategories"),
]
for _old_name, _descriptor, _mapped_name in _SKILL_GUIDE_MANAGER_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_SKILL_GUIDE_MANAGER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_SKILL_GUIDE_ENTRY_METHOD_NAMES = [
    ("a", "()Ljava/lang/String;", "getDisplayLabel"),
    ("b", "()Ljava/lang/String;", "getLevelText"),
]
for _old_name, _descriptor, _mapped_name in _SKILL_GUIDE_ENTRY_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_SKILL_GUIDE_ENTRY_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_SKILL_GUIDE_CATEGORY_METHOD_NAMES = [
    ("a", f"(L{_SKILL_GUIDE_ENTRY_OWNER};)V", "addEntry"),
    ("a", f"(L{_SKILL_GUIDE_ENTRY_OWNER};Z)V", "addEntry"),
    ("a", "()Ljava/lang/String;", "getLevelText"),
]
for _old_name, _descriptor, _mapped_name in _SKILL_GUIDE_CATEGORY_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_SKILL_GUIDE_CATEGORY_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_SKILL_GUIDE_FRAME_DESCRIPTOR = (
    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;"
    "Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;"
    "Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;"
    "Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
)
_SKILL_GUIDE_MANAGER_METHOD_NAMES = [
    ("a", "(I)Z", "handleButtonClick"),
    ("b", "(I)V", "showSelectedSkillCategory"),
    ("a", _SKILL_GUIDE_FRAME_DESCRIPTOR, "sendGuideFrame"),
    ("a", "(Ljava/lang/String;Ljava/lang/String;II)V", "sendEntryRow"),
    ("b", "()V", "clearEntryRows"),
    ("a", "([I)V", "sendItemContainer"),
    ("a", "(Ljava/lang/String;Ljava/util/ArrayList;I)V", "showSkillGuide"),
    ("c", "(I)V", "showAttackGuide"),
    ("d", "(I)V", "showHitpointsGuide"),
    ("e", "(I)V", "showMiningGuide"),
    ("f", "(I)V", "showStrengthGuide"),
    ("g", "(I)V", "showAgilityGuide"),
    ("h", "(I)V", "showSmithingGuide"),
    ("i", "(I)V", "showDefenceGuide"),
    ("j", "(I)V", "showHerbloreGuide"),
    ("k", "(I)V", "showFishingGuide"),
    ("l", "(I)V", "showRangedGuide"),
    ("m", "(I)V", "showThievingGuide"),
    ("n", "(I)V", "showCookingGuide"),
    ("o", "(I)V", "showPrayerGuide"),
    ("p", "(I)V", "showCraftingGuide"),
    ("q", "(I)V", "showFiremakingGuide"),
    ("r", "(I)V", "showMagicGuide"),
    ("s", "(I)V", "showFletchingGuide"),
    ("t", "(I)V", "showWoodcuttingGuide"),
    ("u", "(I)V", "showRunecraftingGuide"),
    ("v", "(I)V", "showSlayerGuide"),
    ("w", "(I)V", "showFarmingGuide"),
    ("x", "(I)Ljava/util/ArrayList;", "getCategoriesForSkillId"),
    (
        "a",
        f"(Ljava/util/ArrayList;L{_SKILL_GUIDE_CATEGORY_OWNER};)V",
        "addCategoryIfNotEmpty",
    ),
    ("a", "()V", "initialize"),
    ("c", "()V", "initializeFarmingCategories"),
    ("d", "()V", "initializeSlayerCategories"),
    ("e", "()V", "initializeRunecraftingCategories"),
    ("f", "()V", "initializeWoodcuttingCategories"),
    ("g", "()V", "initializeFletchingCategories"),
    ("h", "()V", "initializeMagicCategories"),
    ("i", "()V", "initializeFiremakingCategories"),
    ("j", "()V", "initializeCraftingCategories"),
    ("k", "()V", "initializePrayerCategories"),
    ("l", "()V", "initializeCookingCategories"),
    ("m", "()V", "initializeThievingCategories"),
    ("n", "()V", "initializeRangedCategories"),
    ("o", "()V", "initializeHerbloreCategories"),
]
for _old_name, _descriptor, _mapped_name in _SKILL_GUIDE_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_SKILL_GUIDE_MANAGER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_EMOTE_MANAGER_OWNER = "com/rs2/model/player/EmoteManager"
_EMOTE_MANAGER_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "[[I", "EMOTE_BUTTON_ANIMATION_PAIRS"),
]
for _old_name, _descriptor, _mapped_name in _EMOTE_MANAGER_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_EMOTE_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)
METHOD_NAME_MAP.setdefault((_EMOTE_MANAGER_OWNER, "a", "(I)Z"), "handleButtonClick")
_SANDWICH_LADY_MANAGER_OWNER = (
    "com/rs2/model/randomevent/sandwichlady/SandwichLadyManager"
)
_SANDWICH_LADY_REWARD_SET_OWNER = (
    "com/rs2/model/randomevent/sandwichlady/SandwichLadyRewardSet"
)
_SANDWICH_LADY_FOOD_OFFER_OWNER = (
    "com/rs2/model/randomevent/sandwichlady/SandwichLadyFoodOffer"
)
_SANDWICH_LADY_CLEANUP_EVENT_OWNER = (
    "com/rs2/model/randomevent/sandwichlady/SandwichLadyCleanupEvent"
)
_SANDWICH_LADY_MANAGER_FIELD_NAMES = [
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("a", "I", "selectedOfferIndex"),
    ("c", "I", "correctSelectionCount"),
    ("d", "Ljava/util/Map;", "rewardSetsByNpcId"),
]
for _old_name, _descriptor, _mapped_name in _SANDWICH_LADY_MANAGER_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_SANDWICH_LADY_MANAGER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
FIELD_NAME_MAP.setdefault((_SANDWICH_LADY_FOOD_OFFER_OWNER, "a", "I"), "offerIndex")
_SANDWICH_LADY_CLEANUP_EVENT_FIELD_NAMES = [
    ("a", f"L{_SANDWICH_LADY_MANAGER_OWNER};", "manager"),
    ("b", "Lcom/rs2/model/npc/Npc;", "npc"),
]
for _old_name, _descriptor, _mapped_name in _SANDWICH_LADY_CLEANUP_EVENT_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(
        (_SANDWICH_LADY_CLEANUP_EVENT_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_SANDWICH_LADY_REWARD_SET_METHOD_NAMES = [
    ("a", "()Ljava/util/ArrayList;", "getButtonIds"),
    ("b", "()[Lcom/rs2/model/item/ItemStack;", "getRewards"),
    ("c", "()[Ljava/lang/String;", "getMessages"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "punishWrongChoice"),
]
for _owner in [_SANDWICH_LADY_REWARD_SET_OWNER, _SANDWICH_LADY_FOOD_OFFER_OWNER]:
    for _old_name, _descriptor, _mapped_name in _SANDWICH_LADY_REWARD_SET_METHOD_NAMES:
        METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_SANDWICH_LADY_FOOD_OFFER_METHOD_NAMES = [
    ("d", "()[Ljava/lang/String;", "getOfferedFoodNames"),
    ("e", "()I", "getOfferIndex"),
]
for _old_name, _descriptor, _mapped_name in _SANDWICH_LADY_FOOD_OFFER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_SANDWICH_LADY_FOOD_OFFER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_SANDWICH_LADY_MANAGER_METHOD_NAMES = [
    (
        "c",
        f"(I)L{_SANDWICH_LADY_REWARD_SET_OWNER};",
        "getRewardSetForNpcId",
    ),
    ("a", "(I)V", "openSelectionInterface"),
    ("b", "(I)Z", "handleButtonClick"),
    (
        "a",
        f"(L{_SANDWICH_LADY_MANAGER_OWNER};)Lcom/rs2/model/player/Player;",
        "getPlayer",
    ),
]
for _old_name, _descriptor, _mapped_name in _SANDWICH_LADY_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        (_SANDWICH_LADY_MANAGER_OWNER, _old_name, _descriptor),
        _mapped_name,
    )
_BUTTON_FLOW_METHOD_NAMES = [
    ("com/rs2/model/gameplay/duel/DuelSession", "a", "(I)Z", "handleButtonClick"),
    ("com/rs2/model/quest/QuestManager", "f", "(I)Z", "handleButtonClick"),
    ("com/rs2/model/quest/QuestManager", "b", "()V", "refreshQuestJournal"),
    ("com/rs2/model/player/EquipmentManager", "d", "(I)Z", "handleAttackStyleButton"),
    ("com/rs2/model/skill/prayer/PrayerManager", "b", "(I)Z", "handleButtonClick"),
    ("com/rs2/model/skill/prayer/PrayerManager", "a", "(Ljava/lang/Integer;)V", "togglePrayer"),
    ("com/rs2/model/skill/prayer/PrayerManager", "a", "(I)V", "deactivatePrayer"),
    ("com/rs2/model/skill/prayer/PrayerManager", "b", "()V", "deactivateAll"),
    ("com/rs2/model/skill/prayer/PrayerManager", "c", "(I)V", "deactivateConflictingPrayers"),
    ("com/rs2/model/skill/prayer/PrayerManager", "d", "()V", "updatePrayerDrain"),
    ("com/rs2/model/skill/prayer/PrayerManager", "e", "()V", "updateRapidRestoreTask"),
    ("com/rs2/model/skill/prayer/PrayerManager", "c", "()Z", "isRapidRestoreActive"),
    ("com/rs2/model/skill/prayer/PrayerManager", "a", "()V", "drainPrayerPoints"),
    ("com/rs2/model/skill/prayer/PrayerManager", "a", "(Ljava/lang/Integer;I)I", "getPrayerConfigId"),
    ("com/rs2/model/skill/prayer/PrayerManager", "a", "(Lcom/rs2/model/npc/Npc;)V", "awardNpcPrayerExperience"),
    ("com/rs2/model/player/InventoryManager", "c", "(Lcom/rs2/model/item/ItemStack;)Z", "addItem"),
    ("com/rs2/model/skill/magic/TeleportManager", "a", "(Lcom/rs2/model/Position;)Z", "castSpellbookTeleport"),
    ("com/rs2/model/skill/magic/TeleportManager", "b", "(Lcom/rs2/model/Position;)Z", "castItemTeleport"),
    ("com/rs2/model/skill/magic/TeleportManager", "a", "(IIILjava/lang/String;IIII)V", "startScriptedTeleport"),
    ("com/rs2/model/skill/magic/TeleportManager", "a", "(IIILjava/lang/String;)V", "startStandardTeleport"),
    ("com/rs2/model/skill/magic/TeleportManager", "a", "(IIIZ)V", "startMagicTeleportTask"),
    ("com/rs2/model/skill/magic/TeleportManager", "a", "(III)V", "startDelayedTeleport"),
    ("com/rs2/model/skill/magic/TeleportManager", "a", "(IIIZLjava/lang/String;)V", "startDelayedTeleport"),
    ("com/rs2/model/skill/magic/TeleportManager", "b", "(III)V", "startAbyssTeleport"),
    (
        "com/rs2/model/skill/magic/TeleportManager",
        "a",
        "(Lcom/rs2/model/skill/magic/TeleportManager;)Lcom/rs2/model/player/Player;",
        "getPlayer",
    ),
    ("com/rs2/model/player/Player", "g", "(Lcom/rs2/model/Position;)V", "setTeleotherDestination"),
    ("com/rs2/model/player/Player", "fb", "()Lcom/rs2/model/Position;", "getTeleotherDestination"),
    ("com/rs2/model/task/DelayTimer", "b", "()I", "getDelayTicks"),
    ("com/rs2/model/task/DelayTimer", "a", "(I)V", "setDelayTicks"),
    ("com/rs2/model/task/DelayTimer", "c", "()V", "reset"),
    ("com/rs2/model/task/DelayTimer", "d", "()Z", "hasElapsed"),
    ("com/rs2/model/task/DelayTimer", "e", "()I", "getRemainingTicks"),
    ("com/rs2/model/combat/CombatTargetDelayTimer", "b", "()I", "getDelayTicks"),
    ("com/rs2/model/combat/CombatTargetDelayTimer", "a", "(I)V", "setDelayTicks"),
    ("com/rs2/model/combat/CombatTargetDelayTimer", "c", "()V", "reset"),
    ("com/rs2/model/combat/CombatTargetDelayTimer", "d", "()Z", "hasElapsed"),
    ("com/rs2/model/combat/CombatTargetDelayTimer", "e", "()I", "getRemainingTicks"),
    ("com/rs2/model/gameplay/barrows/BarrowsManager", "b", "(Lcom/rs2/model/player/Player;I)Z", "handlePuzzleButtonClick"),
    ("com/rs2/model/player/GrandExchangeManager", "b", "(Lcom/rs2/model/player/Player;I)Z", "handleButtonClick"),
    ("com/rs2/model/GameplayHelper", "j", "(Lcom/rs2/model/player/Player;)V", "openSextantInterface"),
    ("com/rs2/model/GameplayHelper", "h", "(Lcom/rs2/model/player/Player;I)Z", "handleSextantButtonClick"),
    ("com/rs2/model/GameplayHelper", "k", "(Lcom/rs2/model/player/Player;)V", "refreshSextantInterface"),
    ("com/rs2/model/GameplayHelper", "b", "(Lcom/rs2/model/player/Player;Z)V", "adjustSextantSun"),
    ("com/rs2/model/GameplayHelper", "c", "(Lcom/rs2/model/player/Player;Z)V", "adjustSextantHorizon"),
    ("com/rs2/model/travel/TravelManager", "a", "(Lcom/rs2/model/player/Player;I)Z", "handleGnomeGliderButton"),
    ("com/rs2/model/randomevent/RandomEventManager", "a", "(Lcom/rs2/model/player/Player;I)Z", "handleLampButtonClick"),
    ("com/rs2/model/player/Player", "aD", "(I)V", "setSelectedLampSkill"),
    ("com/rs2/model/player/Player", "fj", "()I", "getSelectedLampSkill"),
    ("com/rs2/model/player/Player", "ag", "(I)Z", "handleSpecialAttackButton"),
    ("com/rs2/model/player/Player", "dX", "()V", "refreshSpecialAttackWidgets"),
    ("com/rs2/model/player/Player", "q", "(I)I", "getQuestState"),
    ("com/rs2/model/player/Player", "a", "(IZ)I", "getQuestState"),
    ("com/rs2/model/player/Player", "e", "(II)V", "setQuestState"),
    ("com/rs2/model/player/Player", "f", "(II)V", "addQuestState"),
    ("com/rs2/model/player/Player", "bi", "()V", "disconnect"),
]
for _owner, _old_name, _descriptor, _mapped_name in _BUTTON_FLOW_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_RANDOM_EVENT_METHOD_NAMES = [
    (
        "com/rs2/model/randomevent/RandomEventManager",
        "a",
        "()Lcom/rs2/model/item/ItemStack;",
        "selectJekyllRequestedHerb",
    ),
    (
        "com/rs2/model/randomevent/RandomEventManager",
        "a",
        "(I)Lcom/rs2/model/item/ItemStack;",
        "getJekyllPotionRewardForHerb",
    ),
    (
        "com/rs2/model/randomevent/RandomEventManager",
        "b",
        "(I)Z",
        "isRandomEventCombatExcludedNpcId",
    ),
    (
        "com/rs2/model/randomevent/RandomEventManager",
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/randomevent/RandomEventNpcDefinition;)V",
        "spawnRandomEventNpc",
    ),
    (
        "com/rs2/model/randomevent/SkillRandomEventNpc",
        "a",
        "()I",
        "getBaseNpcId",
    ),
    (
        "com/rs2/model/GameplayHelper",
        "d",
        "(I)I",
        "getRandomEventCombatLevelOffset",
    ),
    (
        "com/rs2/model/GameplayHelper",
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/randomevent/SkillRandomEventNpc;)V",
        "spawnSkillRandomEventNpc",
    ),
]
for _owner, _old_name, _descriptor, _mapped_name in _RANDOM_EVENT_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_RANDOM_EVENT_FIELD_NAMES = [
    (
        "com/rs2/model/randomevent/RandomEventManager",
        "a",
        "[[I",
        "JEKYLL_HERB_POTION_PAIRS",
    ),
    (
        "com/rs2/model/randomevent/RandomEventNpcDefinition",
        "a",
        "Lcom/rs2/model/randomevent/RandomEventNpcDefinition;",
        "DRUNKEN_DWARF",
    ),
    (
        "com/rs2/model/randomevent/RandomEventNpcDefinition",
        "b",
        "Lcom/rs2/model/randomevent/RandomEventNpcDefinition;",
        "GENIE",
    ),
    (
        "com/rs2/model/randomevent/RandomEventNpcDefinition",
        "g",
        "Lcom/rs2/model/randomevent/RandomEventNpcDefinition;",
        "RICK_TURPENTINE",
    ),
    (
        "com/rs2/model/randomevent/RandomEventNpcDefinition",
        "c",
        "Lcom/rs2/model/randomevent/RandomEventNpcDefinition;",
        "DR_JEKYLL",
    ),
    ("com/rs2/model/randomevent/RandomEventNpcDefinition", "d", "I", "npcId"),
    (
        "com/rs2/model/randomevent/RandomEventNpcDefinition",
        "e",
        "I",
        "ignorePenaltyNpcId",
    ),
    (
        "com/rs2/model/randomevent/RandomEventNpcDefinition",
        "f",
        "[Ljava/lang/String;",
        "reminderLines",
    ),
    ("com/rs2/model/randomevent/RandomEventRollEvent", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/randomevent/RandomEventNpcReminderEvent", "a", "I", "reminderTicksRemaining"),
    ("com/rs2/model/randomevent/RandomEventNpcReminderEvent", "b", "I", "interactionGraceTicks"),
    (
        "com/rs2/model/randomevent/RandomEventNpcReminderEvent",
        "c",
        "Ljava/lang/String;",
        "playerDisplayName",
    ),
    ("com/rs2/model/randomevent/RandomEventNpcReminderEvent", "d", "Lcom/rs2/model/npc/Npc;", "npc"),
    (
        "com/rs2/model/randomevent/RandomEventNpcReminderEvent",
        "e",
        "[Ljava/lang/String;",
        "reminderLines",
    ),
    ("com/rs2/model/randomevent/RandomEventNpcReminderEvent", "f", "I", "ignorePenaltyNpcId"),
    (
        "com/rs2/model/randomevent/RandomEventNpcReminderEvent",
        "g",
        "Lcom/rs2/model/player/Player;",
        "player",
    ),
    ("com/rs2/model/randomevent/SkillRandomEventNpc", "f", "I", "baseNpcId"),
    (
        "com/rs2/model/randomevent/RandomEventTeleportLocations",
        "a",
        "[Lcom/rs2/model/Position;",
        "TELEPORT_DESTINATIONS",
    ),
]
for _owner, _old_name, _descriptor, _mapped_name in _RANDOM_EVENT_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_DUEL_CONTROLLER_METHOD_NAMES = [
    ("a", "(Lcom/rs2/model/player/Player;)V", "handleDuelRequest"),
    ("a", "(Z)V", "resetDuel"),
    ("a", "()V", "acceptCurrentDuelScreen"),
    ("b", "()Z", "isAccepted"),
    ("b", "(Z)V", "setAccepted"),
]
for _old_name, _descriptor, _mapped_name in _DUEL_CONTROLLER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/gameplay/duel/DuelController", _old_name, _descriptor),
        _mapped_name,
    )
_DUEL_INTERFACE_MANAGER_METHOD_NAMES = [
    ("a", "()V", "openDuelSetupInterface"),
    ("b", "()V", "openDuelConfirmInterface"),
    ("c", "()V", "refreshAcceptStatus"),
    ("d", "()Z", "hasInventorySpaceForDuel"),
    ("e", "()V", "refreshStakeContainers"),
    ("a", "(ILjava/lang/String;)V", "toggleRule"),
    ("f", "()V", "resetRules"),
    ("g", "()I", "countEquipmentItemsToRemove"),
]
for _old_name, _descriptor, _mapped_name in _DUEL_INTERFACE_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/gameplay/duel/DuelInterfaceManager", _old_name, _descriptor),
        _mapped_name,
    )
_DUEL_SESSION_METHOD_NAMES = [
    ("a", "()V", "clearDuelState"),
    ("a", "(Ljava/util/ArrayList;)[Lcom/rs2/model/item/ItemStack;", "toItemArray"),
    ("b", "()Lcom/rs2/model/combat/CombatType;", "getCurrentCombatType"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V", "finishDuelVictory"),
    ("a", "(Z)V", "finishDuelLoss"),
    ("c", "()V", "moveToDuelArenaExit"),
    ("d", "()V", "startDuel"),
    ("m", "()V", "startCountdown"),
    ("a", "(Lcom/rs2/model/item/ItemStack;I)V", "addStakeItem"),
    ("a", "(Lcom/rs2/model/item/ItemStack;)V", "removeStakeItem"),
    ("b", "(Lcom/rs2/model/item/ItemStack;)Z", "hasStakedItemAmount"),
    ("e", "()Z", "isStarted"),
    ("f", "()Z", "isActiveDuelStarted"),
    ("g", "()V", "restoreHitpoints"),
    ("h", "()Ljava/util/ArrayList;", "getStakedItems"),
    ("i", "()Lcom/rs2/model/player/Player;", "getOpponent"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "setOpponent"),
    ("j", "()Ljava/util/ArrayList;", "getEquipmentToRemove"),
    ("k", "()[Z", "getEnabledRules"),
    ("l", "()Ljava/util/ArrayList;", "getRuleDescriptions"),
    ("b", "(Z)V", "setStarted"),
    ("a", "(Lcom/rs2/model/gameplay/duel/DuelSession;)Lcom/rs2/model/player/Player;", "getPlayer"),
]
for _old_name, _descriptor, _mapped_name in _DUEL_SESSION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/gameplay/duel/DuelSession", _old_name, _descriptor),
        _mapped_name,
    )
_DUEL_LOCATION_MANAGER_METHOD_NAMES = [
    ("a", "(Lcom/rs2/model/Position;)Lcom/rs2/model/Position;", "findAdjacentOpenPosition"),
    ("a", "()Lcom/rs2/model/Position;", "randomExitPosition"),
    ("a", "(ZI)Lcom/rs2/model/Position;", "randomStartPosition"),
]
for _old_name, _descriptor, _mapped_name in _DUEL_LOCATION_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/gameplay/duel/DuelArenaLocationManager", _old_name, _descriptor),
        _mapped_name,
    )
_DUEL_HISTORY_METHOD_NAMES = [
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V", "recordDuelResult"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "openDuelHistoryInterface"),
]
for _old_name, _descriptor, _mapped_name in _DUEL_HISTORY_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/gameplay/duel/DuelHistory", _old_name, _descriptor),
        _mapped_name,
    )
_FIGHT_CAVE_CONTROLLER_METHOD_NAMES = [
    ("a", "()V", "handleDeath"),
    ("b", "()V", "cleanupIfInFightCave"),
    ("c", "()V", "leaveFightCave"),
    ("d", "()V", "startNextWave"),
    ("e", "()V", "completeFightCave"),
    ("f", "()V", "startFightCave"),
    ("a", "(Lcom/rs2/model/gameplay/fightcave/FightCaveController;)Lcom/rs2/model/player/Player;", "getPlayer"),
]
for _old_name, _descriptor, _mapped_name in _FIGHT_CAVE_CONTROLLER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/gameplay/fightcave/FightCaveController", _old_name, _descriptor),
        _mapped_name,
    )
_FIGHT_CAVE_PLAYER_METHOD_NAMES = [
    ("aT", "()Ljava/util/ArrayList;", "getFightCaveNpcs"),
    ("aU", "()V", "clearFightCaveNpcs"),
    ("a", "(Lcom/rs2/model/npc/Npc;)V", "addFightCaveNpc"),
    ("b", "(Lcom/rs2/model/npc/Npc;)V", "removeFightCaveNpc"),
]
for _old_name, _descriptor, _mapped_name in _FIGHT_CAVE_PLAYER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/player/Player", _old_name, _descriptor),
        _mapped_name,
    )
METHOD_NAME_MAP.setdefault(
    (
        "com/rs2/model/gameplay/fightcave/FightCaveWaveSpawner",
        "a",
        "(Lcom/rs2/model/player/Player;I)V",
    ),
    "spawnWave",
)
_GOD_WARS_MANAGER_METHOD_NAMES = [
    ("a", "()V", "spawnGodWarsNpcs"),
    ("a", "(Lcom/rs2/model/npc/Npc;)Z", "handleBossBattleCry"),
    ("a", "(Lcom/rs2/model/player/Player;I)V", "recordKillCount"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "refreshKillCountOverlay"),
    ("a", "(Lcom/rs2/model/player/Player;II)Z", "handleGodswordShardOnAnvil"),
    ("b", "(Lcom/rs2/model/player/Player;III)V", "smithGodswordShardCombination"),
    ("b", "(Lcom/rs2/model/player/Player;I)Z", "dismantleGodsword"),
    ("b", "(Lcom/rs2/model/player/Player;II)Z", "handleGodWarsItemCombination"),
    ("a", "(Lcom/rs2/model/player/Player;III)Z", "handleFirstObjectAction"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "handleUpperRopeRockShortcut"),
    ("c", "(Lcom/rs2/model/player/Player;)V", "handleLowerRopeRockShortcut"),
    ("c", "(Lcom/rs2/model/player/Player;I)Z", "handleSecondObjectAction"),
]
for _old_name, _descriptor, _mapped_name in _GOD_WARS_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/gameplay/godwars/GodWarsDungeonManager", _old_name, _descriptor),
        _mapped_name,
    )
_DUEL_RULE_OWNERS = [
    "com/rs2/model/gameplay/duel/DuelRule",
    "com/rs2/model/gameplay/duel/NoRangedDuelRule",
    "com/rs2/model/gameplay/duel/NoMeleeDuelRule",
    "com/rs2/model/gameplay/duel/NoMagicDuelRule",
    "com/rs2/model/gameplay/duel/NoSpecialAttackDuelRule",
    "com/rs2/model/gameplay/duel/FunWeaponsDuelRule",
    "com/rs2/model/gameplay/duel/NoForfeitDuelRule",
    "com/rs2/model/gameplay/duel/NoPotionsDuelRule",
    "com/rs2/model/gameplay/duel/NoFoodDuelRule",
    "com/rs2/model/gameplay/duel/NoPrayerDuelRule",
    "com/rs2/model/gameplay/duel/NoMovementDuelRule",
    "com/rs2/model/gameplay/duel/ObstaclesDuelRule",
    "com/rs2/model/gameplay/duel/NoHelmetDuelRule",
    "com/rs2/model/gameplay/duel/NoCapeDuelRule",
    "com/rs2/model/gameplay/duel/NoAmuletDuelRule",
    "com/rs2/model/gameplay/duel/NoAmmoDuelRule",
    "com/rs2/model/gameplay/duel/NoWeaponDuelRule",
    "com/rs2/model/gameplay/duel/NoBodyDuelRule",
    "com/rs2/model/gameplay/duel/NoShieldDuelRule",
    "com/rs2/model/gameplay/duel/NoLegsDuelRule",
    "com/rs2/model/gameplay/duel/NoGlovesDuelRule",
    "com/rs2/model/gameplay/duel/NoBootsDuelRule",
    "com/rs2/model/gameplay/duel/NoRingDuelRule",
]
_DUEL_RULE_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_RANGED"),
    ("b", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_MELEE"),
    ("c", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_MAGIC"),
    ("d", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_SPECIAL_ATTACK"),
    ("e", "Lcom/rs2/model/gameplay/duel/DuelRule;", "FUN_WEAPONS"),
    ("f", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_FORFEIT"),
    ("g", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_POTIONS"),
    ("h", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_FOOD"),
    ("i", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_PRAYER"),
    ("j", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_MOVEMENT"),
    ("k", "Lcom/rs2/model/gameplay/duel/DuelRule;", "OBSTACLES"),
    ("l", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_HELMET"),
    ("m", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_CAPE"),
    ("n", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_AMULET"),
    ("o", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_AMMO"),
    ("p", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_WEAPON"),
    ("q", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_BODY"),
    ("r", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_SHIELD"),
    ("s", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_LEGS"),
    ("t", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_GLOVES"),
    ("u", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_BOOTS"),
    ("v", "Lcom/rs2/model/gameplay/duel/DuelRule;", "NO_RING"),
    ("w", "I", "ruleIndex"),
]
for _owner in _DUEL_RULE_OWNERS:
    for _old_name, _descriptor, _mapped_name in _DUEL_RULE_FIELD_NAMES:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
for _owner in _DUEL_RULE_OWNERS:
    METHOD_NAME_MAP.setdefault((_owner, "a", "(Lcom/rs2/model/player/Player;Z)V"), "toggleForPlayer")
    METHOD_NAME_MAP.setdefault((_owner, "a", "(Lcom/rs2/model/player/Player;)Z"), "isEnabledFor")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/gameplay/duel/DuelRule", "a", "(I)Lcom/rs2/model/gameplay/duel/DuelRule;"),
    "forButtonId",
)
FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "hO", "I"), "selectedLampSkill")
FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "ih", "[I"), "questStates")
FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bI", "[I"), "questProgressFlags")
FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bJ", "[I"), "questHookStates")

_QUEST_FRAMEWORK_METHOD_NAMES = [
    ("com/rs2/model/quest/QuestDefinition", "a", "()I", "getTotalQuestPointReward"),
    ("com/rs2/model/quest/QuestDefinition", "a", "(I)Lcom/rs2/model/quest/QuestScript;", "getQuestScript"),
    ("com/rs2/model/quest/QuestDefinition", "b", "(I)Lcom/rs2/model/quest/QuestDefinition;", "forId"),
    ("com/rs2/model/quest/QuestDefinition", "b", "()V", "loadDefinitions"),
    ("com/rs2/model/quest/QuestDefinition", "c", "()Ljava/lang/String;", "getName"),
    ("com/rs2/model/quest/QuestDefinition", "d", "()I", "getJournalButtonId"),
    ("com/rs2/model/quest/QuestDefinition", "e", "()Z", "isMembersOnly"),
    ("com/rs2/model/quest/QuestEventRegistry", "a", "(I)Lcom/rs2/model/quest/QuestHook;", "getEventHook"),
    ("com/rs2/model/quest/QuestEventRegistry", "a", "()V", "initializeEventHooks"),
    ("com/rs2/model/quest/QuestHook", "b", "()I", "getQuestId"),
    ("com/rs2/model/quest/QuestHook", "c", "()I", "getEventType"),
    ("com/rs2/model/quest/QuestHook", "d", "()Z", "isEnabled"),
    ("com/rs2/model/quest/QuestHook", "a", "(Z)V", "setEnabled"),
    ("com/rs2/model/quest/QuestHook", "e", "()V", "initialize"),
    ("com/rs2/model/quest/QuestHook", "a", "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;)I", "calculateProjectileTravelTicks"),
    ("com/rs2/model/quest/QuestScript", "a", "(Lcom/rs2/model/player/Player;I)[Ljava/lang/String;", "buildQuestJournal"),
    ("com/rs2/model/quest/QuestScript", "a", "()I", "getQuestPointReward"),
    ("com/rs2/model/quest/QuestScript", "a", "(I)V", "setQuestPointReward"),
    ("com/rs2/model/quest/QuestScript", "a", "(Lcom/rs2/model/player/Player;)V", "markQuestComplete"),
    ("com/rs2/model/quest/QuestScript", "b", "(Lcom/rs2/model/player/Player;)V", "showQuestCompleteInterface"),
    ("com/rs2/model/quest/QuestScript", "c", "(Lcom/rs2/model/player/Player;)V", "awardCompletionRewards"),
    ("com/rs2/model/quest/QuestScript", "d", "(Lcom/rs2/model/player/Player;)V", "startQuest"),
    ("com/rs2/model/quest/QuestManager", "g", "(I)Z", "isQuestJournalButtonAvailable"),
    ("com/rs2/model/quest/QuestManager", "d", "()V", "refreshQuestPointText"),
]
for _owner, _old_name, _descriptor, _mapped_name in _QUEST_FRAMEWORK_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_QUEST_MANAGER_DISPATCH_METHOD_NAMES = [
    ("a", "(II)Z", "handleItemOnItem"),
    ("a", "(I)Z", "handleDropItem"),
    ("b", "(I)Z", "handleFirstNpcAction"),
    ("b", "(II)Z", "handleInventoryItemFirstOption"),
    ("a", "(III)Z", "handleFirstObjectAction"),
    ("b", "(III)Z", "handleSecondObjectAction"),
    ("c", "(I)Z", "handleGroundItemInteraction"),
    ("c", "(II)Z", "handleItemOnObject"),
    ("d", "(II)Z", "handleItemOnNpc"),
    ("d", "(I)Z", "canAttackNpc"),
    ("a", "()Z", "handleMovementStep"),
    ("a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)Z", "handleCombatDeath"),
    ("b", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)I", "getQuestDamageOverride"),
    ("e", "(I)Z", "handleNpcKill"),
    (
        "a",
        "(ILcom/rs2/model/player/Player;Lcom/rs2/model/Position;)Z",
        "handleNpcDeathDrop",
    ),
    ("a", "(IIIIIII)Z", "handleContextDialogue"),
    ("a", "(IIII)Z", "handleNpcDialogue"),
    ("c", "()Z", "refreshQuestJournalStatuses"),
]
for _old_name, _descriptor, _mapped_name in _QUEST_MANAGER_DISPATCH_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/quest/QuestManager", _old_name, _descriptor),
        _mapped_name,
    )

_QUEST_SCRIPT_METHOD_OWNERS = [
    "com/rs2/model/quest/QuestScript",
    "com/rs2/model/quest/NullQuestScript",
]
_QUEST_SCRIPT_METHOD_OWNERS.extend(
    sorted(
        owner
        for owner in EXACT_CLASS_MAP.values()
        if owner.startswith("com/rs2/model/quest/impl/")
    )
)
for _owner in _QUEST_SCRIPT_METHOD_OWNERS:
    METHOD_NAME_MAP.setdefault((_owner, "b", "()I"), "getQuestId")
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Lcom/rs2/model/player/Player;I)[Ljava/lang/String;"),
        "buildQuestJournal",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "c", "(Lcom/rs2/model/player/Player;)V"),
        "awardCompletionRewards",
    )

_QUEST_HOOK_METHOD_OWNERS = [
    "com/rs2/model/quest/QuestHook",
    "com/rs2/model/quest/QuestScript",
    "com/rs2/model/quest/NullQuestScript",
    "com/rs2/model/quest/event/ServerMaintenanceEventHook",
    "com/rs2/model/quest/event/HalloweenMaskDropEventHook",
    "com/rs2/model/quest/event/ChristmasDropEventHook",
    "com/rs2/model/quest/event/EasterEggDropEventHook",
    "com/rs2/model/quest/event/NoopQuestEventHook",
    "com/rs2/model/quest/impl/BlackKnightsFortressQuest",
    "com/rs2/model/quest/impl/CooksAssistantQuest",
    "com/rs2/model/quest/impl/DemonSlayerQuest",
    "com/rs2/model/quest/impl/DoricsQuest",
    "com/rs2/model/quest/impl/DragonSlayerQuest",
    "com/rs2/model/quest/impl/DruidicRitualQuest",
    "com/rs2/model/quest/impl/ElementalWorkshopQuest",
    "com/rs2/model/quest/impl/ErnestTheChickenQuest",
    "com/rs2/model/quest/impl/FamilyCrestQuest",
    "com/rs2/model/quest/impl/FremennikTrialsQuest",
    "com/rs2/model/quest/impl/GertrudesCatQuest",
    "com/rs2/model/quest/impl/GoblinDiplomacyQuest",
    "com/rs2/model/quest/impl/GrandTreeQuest",
    "com/rs2/model/quest/impl/HeroesQuest",
    "com/rs2/model/quest/impl/HolyGrailQuest",
    "com/rs2/model/quest/impl/ImpCatcherQuest",
    "com/rs2/model/quest/impl/JunglePotionQuest",
    "com/rs2/model/quest/impl/KnightsSwordQuest",
    "com/rs2/model/quest/impl/LostCityQuest",
    "com/rs2/model/quest/impl/MerlinsCrystalQuest",
    "com/rs2/model/quest/impl/MonkeyMadnessQuest",
    "com/rs2/model/quest/impl/PiratesTreasureQuest",
    "com/rs2/model/quest/impl/PriestInPerilQuest",
    "com/rs2/model/quest/impl/PrinceAliRescueQuest",
    "com/rs2/model/quest/impl/RestlessGhostQuest",
    "com/rs2/model/quest/impl/RomeoAndJulietQuest",
    "com/rs2/model/quest/impl/RuneMysteriesQuest",
    "com/rs2/model/quest/impl/ScorpionCatcherQuest",
    "com/rs2/model/quest/impl/SheepShearerQuest",
    "com/rs2/model/quest/impl/ShieldOfArravQuest",
    "com/rs2/model/quest/impl/TreeGnomeVillageQuest",
    "com/rs2/model/quest/impl/TutorialQuest",
    "com/rs2/model/quest/impl/VampireSlayerQuest",
    "com/rs2/model/quest/impl/WitchsHouseQuest",
    "com/rs2/model/quest/impl/WitchsPotionQuest",
]
_QUEST_HOOK_DISPATCH_METHOD_NAMES = [
    ("a", "(Lcom/rs2/model/player/Player;IIII)Z", "handleNpcDialogue"),
    ("a", "(ILcom/rs2/model/player/Player;IIIIII)Z", "handleContextDialogue"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;ILcom/rs2/model/Position;I)Z",
        "handleNpcDeathDrop",
    ),
    ("b", "(Lcom/rs2/model/player/Player;IIII)Z", "handleFirstObjectAction"),
    ("c", "(Lcom/rs2/model/player/Player;IIII)Z", "handleSecondObjectAction"),
    ("a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;I)Z", "handleCombatDeath"),
    ("b", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;I)I", "getQuestDamageOverride"),
    ("a", "(Lcom/rs2/model/player/Player;II)Z", "handleNpcKill"),
    ("b", "(Lcom/rs2/model/player/Player;I)Z", "handleMovementStep"),
    ("b", "(Lcom/rs2/model/player/Player;II)Z", "canAttackNpc"),
    ("c", "(Lcom/rs2/model/player/Player;I)Z", "refreshQuestJournalStatus"),
    ("d", "(Lcom/rs2/model/player/Player;I)V", "refreshQuestJournal"),
    ("c", "(Lcom/rs2/model/player/Player;II)Z", "handleGroundItemInteraction"),
    ("a", "(Lcom/rs2/model/player/Player;III)Z", "handleItemOnObject"),
    ("b", "(Lcom/rs2/model/player/Player;III)Z", "handleItemOnItem"),
    ("d", "(Lcom/rs2/model/player/Player;II)Z", "handleDropItem"),
    ("c", "(Lcom/rs2/model/player/Player;III)Z", "handleInventoryItemFirstOption"),
    ("e", "(Lcom/rs2/model/player/Player;II)Z", "handleButtonClick"),
    ("f", "(Lcom/rs2/model/player/Player;II)Z", "handleFirstNpcAction"),
    ("d", "(Lcom/rs2/model/player/Player;III)Z", "handleItemOnNpc"),
]
for _owner in _QUEST_HOOK_METHOD_OWNERS:
    for _old_name, _descriptor, _mapped_name in _QUEST_HOOK_DISPATCH_METHOD_NAMES:
        METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _owner in [
    "com/rs2/model/quest/event/ServerMaintenanceEventHook",
    "com/rs2/model/quest/event/HalloweenMaskDropEventHook",
    "com/rs2/model/quest/event/ChristmasDropEventHook",
    "com/rs2/model/quest/event/EasterEggDropEventHook",
    "com/rs2/model/quest/event/NoopQuestEventHook",
]:
    METHOD_NAME_MAP.setdefault((_owner, "e", "()V"), "initialize")

_QUEST_FRAMEWORK_FIELD_NAMES = [
    ("com/rs2/model/quest/QuestDefinition", "a", "I", "questCount"),
    ("com/rs2/model/quest/QuestDefinition", "b", "I", "questStateCapacity"),
    ("com/rs2/model/quest/QuestDefinition", "c", "Ljava/lang/String;", "name"),
    ("com/rs2/model/quest/QuestDefinition", "d", "I", "journalButtonId"),
    ("com/rs2/model/quest/QuestDefinition", "e", "Z", "membersOnly"),
    ("com/rs2/model/quest/QuestDefinition", "f", "[Lcom/rs2/model/quest/QuestDefinition;", "definitionsById"),
    ("com/rs2/model/quest/QuestDefinition", "g", "[Lcom/rs2/model/quest/QuestScript;", "questScripts"),
    ("com/rs2/model/quest/QuestEventRegistry", "a", "I", "eventHookCount"),
    ("com/rs2/model/quest/QuestEventRegistry", "b", "I", "SERVER_MAINTENANCE_EVENT_TYPE"),
    ("com/rs2/model/quest/QuestEventRegistry", "c", "I", "HALLOWEEN_EVENT_TYPE"),
    ("com/rs2/model/quest/QuestEventRegistry", "d", "I", "CHRISTMAS_EVENT_TYPE"),
    ("com/rs2/model/quest/QuestEventRegistry", "e", "I", "EASTER_EVENT_TYPE"),
    ("com/rs2/model/quest/QuestEventRegistry", "f", "[Lcom/rs2/model/quest/QuestHook;", "eventHooks"),
    ("com/rs2/model/quest/QuestHook", "a", "I", "questId"),
    ("com/rs2/model/quest/QuestHook", "b", "I", "eventType"),
    ("com/rs2/model/quest/QuestHook", "c", "Z", "enabled"),
    ("com/rs2/model/quest/QuestScript", "a", "I", "questPointReward"),
    ("com/rs2/model/quest/QuestManager", "a", "Lcom/rs2/model/player/Player;", "player"),
]
for _owner, _old_name, _descriptor, _mapped_name in _QUEST_FRAMEWORK_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_GRAND_TREE_QUEST_FIELD_NAMES = [
    ("a", "Ljava/util/List;", "daconiaRootSearchPositions"),
    ("b", "Lcom/rs2/model/Position;", "twig789PlacementPosition"),
    ("c", "Lcom/rs2/model/Position;", "twig790PlacementPosition"),
    ("d", "Lcom/rs2/model/Position;", "twig791PlacementPosition"),
    ("e", "Lcom/rs2/model/Position;", "twig792PlacementPosition"),
    ("f", "I", "gloughJournalLastPageIndex"),
    ("g", "I", "translationBookLastPageIndex"),
    ("h", "Lcom/rs2/util/RectangularArea;", "kingNarnodeIntroArea"),
    ("i", "Lcom/rs2/util/RectangularArea;", "grandTreeUndergroundArea"),
]
for _old_name, _descriptor, _mapped_name in _GRAND_TREE_QUEST_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/quest/impl/GrandTreeQuest", _old_name, _descriptor), _mapped_name)
_GRAND_TREE_QUEST_METHOD_NAMES = [
    ("e", "(Lcom/rs2/model/player/Player;)Z", "hasRequiredAgilityLevel"),
    ("b", "(I)Z", "isTwigItem"),
    ("f", "(Lcom/rs2/model/player/Player;)Z", "areAllTwigsPlaced"),
    ("g", "(Lcom/rs2/model/player/Player;)V", "clearBookInterfaceText"),
    ("e", "(Lcom/rs2/model/player/Player;I)V", "showGloughJournalPage"),
    ("f", "(Lcom/rs2/model/player/Player;I)V", "showTranslationBookPage"),
]
for _old_name, _descriptor, _mapped_name in _GRAND_TREE_QUEST_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/quest/impl/GrandTreeQuest", _old_name, _descriptor), _mapped_name)
_FREMENNIK_TRIALS_QUEST_FIELD_NAMES = [
    ("a", "[Lcom/rs2/util/RectangularArea;", "peerHouseGuardianAreas"),
    ("b", "[[Ljava/lang/String;", "lyrePerformanceLines"),
    ("c", "[Ljava/lang/String;", "lyreAudienceHeckleLines"),
    ("d", "[I", "lyreAudienceNpcIds"),
    ("e", "[Lcom/rs2/model/quest/QuestArea;", "swensenMazeAreas"),
    ("f", "[Lcom/rs2/model/Position;", "draugenTalismanTargetPositions"),
    ("g", "Lcom/rs2/util/RectangularArea;", "longhallDistractionArea"),
    ("h", "Lcom/rs2/util/RectangularArea;", "lyrePerformanceArea"),
    ("k", "Ljava/util/List;", "femaleFremennikNamePool"),
    ("l", "Ljava/util/List;", "maleFremennikNamePool"),
]
for _old_name, _descriptor, _mapped_name in _FREMENNIK_TRIALS_QUEST_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/quest/impl/FremennikTrialsQuest", _old_name, _descriptor), _mapped_name)
_FREMENNIK_LYRE_TASK_FIELD_NAMES = [
    ("com/rs2/model/quest/impl/PeerTheSeerHouseGuardianTask", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/quest/impl/LyrePerformanceStartTask", "e", "Lcom/rs2/model/task/TickTask;", "secondLineTask"),
    ("com/rs2/model/quest/impl/LyrePerformanceStartTask", "a", "Lcom/rs2/model/task/TickTask;", "thirdLineTask"),
    ("com/rs2/model/quest/impl/LyrePerformanceStartTask", "b", "Lcom/rs2/model/task/TickTask;", "heckleTask"),
    ("com/rs2/model/quest/impl/LyrePerformanceStartTask", "c", "Lcom/rs2/model/task/TickTask;", "finishDialogueTask"),
    ("com/rs2/model/quest/impl/LyrePerformanceStartTask", "d", "Lcom/rs2/model/quest/impl/FremennikTrialsQuest;", "quest"),
    ("com/rs2/model/quest/impl/LyrePerformanceStartTask", "f", "I", "performanceLineIndex"),
    ("com/rs2/model/quest/impl/LyrePerformanceStartTask", "g", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/quest/impl/LyrePerformanceSecondLineTask", "a", "Lcom/rs2/model/quest/impl/LyrePerformanceStartTask;", "startTask"),
    ("com/rs2/model/quest/impl/LyrePerformanceSecondLineTask", "b", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/quest/impl/LyrePerformanceSecondLineTask", "c", "I", "performanceLineIndex"),
    ("com/rs2/model/quest/impl/LyrePerformanceThirdLineTask", "a", "Lcom/rs2/model/quest/impl/LyrePerformanceStartTask;", "startTask"),
    ("com/rs2/model/quest/impl/LyrePerformanceThirdLineTask", "b", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/quest/impl/LyrePerformanceThirdLineTask", "c", "I", "performanceLineIndex"),
    ("com/rs2/model/quest/impl/LyrePerformanceHeckleTask", "a", "Lcom/rs2/model/quest/impl/LyrePerformanceStartTask;", "startTask"),
    ("com/rs2/model/quest/impl/LyrePerformanceHeckleTask", "b", "I", "hecklerNpcIndex"),
    ("com/rs2/model/quest/impl/LyrePerformanceHeckleTask", "c", "I", "heckleLineIndex"),
    ("com/rs2/model/quest/impl/LyrePerformanceHeckleTask", "d", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/quest/impl/LyrePerformanceHeckleTask", "e", "I", "performanceLineIndex"),
    ("com/rs2/model/quest/impl/LyrePerformanceFinishDialogueTask", "a", "Lcom/rs2/model/player/Player;", "player"),
]
for _owner, _old_name, _descriptor, _mapped_name in _FREMENNIK_LYRE_TASK_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_FREMENNIK_TRIALS_QUEST_METHOD_NAMES = [
    ("e", "(Lcom/rs2/model/player/Player;)Z", "hasRequiredSkillLevels"),
    ("f", "(Lcom/rs2/model/player/Player;)Z", "hasAllCouncilVotes"),
    ("a", "(Lcom/rs2/model/player/Player;Ljava/lang/String;I)V", "showDoorRiddleText"),
    ("g", "(Lcom/rs2/model/player/Player;)Z", "hasPreparedStoneSoup"),
]
for _old_name, _descriptor, _mapped_name in _FREMENNIK_TRIALS_QUEST_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/quest/impl/FremennikTrialsQuest", _old_name, _descriptor), _mapped_name)
_DRUIDIC_RITUAL_QUEST_METHOD_NAMES = [
    ("e", "(Lcom/rs2/model/player/Player;)Z", "hasAnyEnchantedMeat"),
    ("f", "(Lcom/rs2/model/player/Player;)Z", "hasAllEnchantedMeats"),
]
for _old_name, _descriptor, _mapped_name in _DRUIDIC_RITUAL_QUEST_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/quest/impl/DruidicRitualQuest", _old_name, _descriptor), _mapped_name)
FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/quest/impl/ElementalWorkshopQuest", "a", "I"),
    "elementalShieldBookLastPageIndex",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/quest/impl/ElementalWorkshopQuest", "e", "(Lcom/rs2/model/player/Player;I)V"),
    "showElementalShieldBookPage",
)
_ELEMENTAL_SHIELD_SMITHING_TASK_FIELD_NAMES = [
    ("a", "Lcom/rs2/model/quest/impl/ElementalWorkshopQuest;", "quest"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "questStateAtStart"),
]
for _old_name, _descriptor, _mapped_name in _ELEMENTAL_SHIELD_SMITHING_TASK_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/quest/impl/ElementalShieldSmithingTask", _old_name, _descriptor), _mapped_name)
_EARLY_QUEST_HELPER_METHOD_NAMES = [
    ("com/rs2/model/quest/impl/DemonSlayerQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasAllSilverlightKeys"),
    ("com/rs2/model/quest/impl/DoricsQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasAnyDoricMaterial"),
    ("com/rs2/model/quest/impl/DoricsQuest", "f", "(Lcom/rs2/model/player/Player;)Z", "hasAllDoricMaterials"),
    ("com/rs2/model/quest/impl/DragonSlayerQuest", "e", "(Lcom/rs2/model/player/Player;I)V", "sendMelzarDoorOpenMessageIfReady"),
    ("com/rs2/model/quest/impl/DragonSlayerQuest", "b", "(I)Z", "isCrandorMapPiece"),
    ("com/rs2/model/quest/impl/ErnestTheChickenQuest", "f", "(Lcom/rs2/model/player/Player;)Z", "hasAllMachineParts"),
    ("com/rs2/model/quest/impl/ErnestTheChickenQuest", "e", "(Lcom/rs2/model/player/Player;)V", "refreshBasementLeverDoorConfig"),
    ("com/rs2/model/quest/impl/FamilyCrestQuest", "b", "(I)Z", "isCrestPiece"),
    ("com/rs2/model/quest/impl/FamilyCrestQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasFamilyGauntlets"),
    ("com/rs2/model/quest/impl/FamilyCrestQuest", "f", "(Lcom/rs2/model/player/Player;)Z", "hasAnyCalebFishIngredient"),
    ("com/rs2/model/quest/impl/FamilyCrestQuest", "g", "(Lcom/rs2/model/player/Player;)Z", "hasAllCalebFishIngredients"),
    ("com/rs2/model/quest/impl/HeroesQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasAllHeroesGuildEntryItems"),
    ("com/rs2/model/quest/impl/ImpCatcherQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasAnyImpBead"),
    ("com/rs2/model/quest/impl/ImpCatcherQuest", "f", "(Lcom/rs2/model/player/Player;)Z", "hasAllImpBeads"),
    ("com/rs2/model/quest/impl/KnightsSwordQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasBluriteSwordMaterials"),
    ("com/rs2/model/quest/impl/PrinceAliRescueQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasAllRescueSupplies"),
    ("com/rs2/model/quest/impl/ScorpionCatcherQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasCaughtFirstScorpion"),
    ("com/rs2/model/quest/impl/ScorpionCatcherQuest", "f", "(Lcom/rs2/model/player/Player;)Z", "hasCaughtSecondScorpion"),
    ("com/rs2/model/quest/impl/ScorpionCatcherQuest", "g", "(Lcom/rs2/model/player/Player;)Z", "hasCaughtThirdScorpion"),
    ("com/rs2/model/quest/impl/WitchsPotionQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasAnyPotionIngredient"),
    ("com/rs2/model/quest/impl/WitchsPotionQuest", "f", "(Lcom/rs2/model/player/Player;)Z", "hasAllPotionIngredients"),
]
for _owner, _old_name, _descriptor, _mapped_name in _EARLY_QUEST_HELPER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_RESIDUAL_QUEST_HELPER_FIELD_NAMES = [
    ("com/rs2/model/quest/impl/ShieldOfArravQuest", "a", "I", "shieldOfArravBookLastPageIndex"),
    ("com/rs2/model/quest/impl/WitchsHouseQuest", "a", "Lcom/rs2/util/RectangularArea;", "gardenTrespassArea"),
    ("com/rs2/model/quest/impl/WitchsHouseQuest", "b", "I", "witchesDiaryLastPageIndex"),
]
for _owner, _old_name, _descriptor, _mapped_name in _RESIDUAL_QUEST_HELPER_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_RESIDUAL_QUEST_HELPER_METHOD_NAMES = [
    ("com/rs2/model/quest/impl/FremennikTrialsQuest", "h", "(Lcom/rs2/model/player/Player;)Z", "hasAnyMerchantTrialItem"),
    ("com/rs2/model/quest/impl/JunglePotionQuest", "a", "(Lcom/rs2/model/player/Player;IIIIZ)V", "startDelayedHerbSearch"),
    ("com/rs2/model/quest/impl/MonkeyMadnessQuest", "e", "(Lcom/rs2/model/player/Player;)Z", "hasCompletedGrandTreeAndTreeGnomeVillage"),
    ("com/rs2/model/quest/impl/MonkeyMadnessQuest", "f", "(Lcom/rs2/model/player/Player;)V", "showCaranockShipyardCutsceneTitle"),
    ("com/rs2/model/quest/impl/MonkeyMadnessQuest", "g", "(Lcom/rs2/model/player/Player;)V", "showCaranockWaydarCutsceneTitle"),
    ("com/rs2/model/quest/impl/MonkeyMadnessQuest", "h", "(Lcom/rs2/model/player/Player;)V", "showAwowogeiAllianceCutsceneTitle"),
    ("com/rs2/model/quest/impl/MonkeyMadnessQuest", "i", "(Lcom/rs2/model/player/Player;)V", "startApeAtollGuardCapture"),
    ("com/rs2/model/quest/impl/MonkeyMadnessQuest", "e", "(Lcom/rs2/model/player/Player;I)Z", "isProgressFlagSet"),
    ("com/rs2/model/quest/impl/ShieldOfArravQuest", "e", "(Lcom/rs2/model/player/Player;I)V", "showShieldOfArravBookPage"),
    ("com/rs2/model/quest/impl/WitchsHouseQuest", "e", "(Lcom/rs2/model/player/Player;I)V", "showWitchesDiaryPage"),
]
for _owner, _old_name, _descriptor, _mapped_name in _RESIDUAL_QUEST_HELPER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_PLUGIN_FIELD_NAMES = [
    ("com/rs2/util/plugin/Plugin", "a", "Lcom/rs2/util/plugin/PluginType;", "pluginType"),
    ("com/rs2/util/plugin/PluginManager", "a", "Ljava/util/List;", "playerPluginClasses"),
    ("com/rs2/util/plugin/PluginManager", "b", "Ljava/util/List;", "globalPlugins"),
    ("com/rs2/util/plugin/PluginType", "a", "Lcom/rs2/util/plugin/PluginType;", "UNSPECIFIED"),
    ("com/rs2/util/plugin/PluginType", "b", "Lcom/rs2/util/plugin/PluginType;", "GLOBAL"),
    ("com/rs2/util/plugin/PluginType", "c", "Lcom/rs2/util/plugin/PluginType;", "PLAYER_LOCAL"),
]
for _owner, _old_name, _descriptor, _mapped_name in _PLUGIN_FIELD_NAMES:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_PLUGIN_METADATA_METHOD_NAMES = [
    ("a", "()Ljava/lang/String;", "getName"),
    ("b", "()Ljava/lang/String;", "getAuthor"),
    ("c", "()D", "getVersion"),
    ("d", "()Lcom/rs2/util/plugin/PluginType;", "getPluginType"),
]
for _owner in [
    "com/rs2/util/plugin/Plugin",
    "com/rs2/util/plugin/GlobalPlugin",
    "com/rs2/util/plugin/PlayerPlugin",
]:
    for _old_name, _descriptor, _mapped_name in _PLUGIN_METADATA_METHOD_NAMES:
        METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_PLUGIN_MANAGER_METHOD_NAMES = [
    ("a", "()V", "loadPlugins"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "attachPlayerPlugins"),
    ("b", "()V", "tickGlobalPlugins"),
    ("c", "()V", "handleMovementPacketPlugins"),
    ("d", "()V", "shutdownGlobalPlugins"),
    ("a", "(Ljava/io/File;Ljava/lang/String;)Ljava/util/List;", "findPluginClasses"),
]
for _old_name, _descriptor, _mapped_name in _PLUGIN_MANAGER_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/util/plugin/PluginManager", _old_name, _descriptor), _mapped_name)
_PLAYER_ITEM_SELECTION_METHOD_NAMES = [
    ("F", "(I)V", "setSelectedItemId"),
    ("bL", "()I", "getSelectedItemId"),
    ("aA", "(I)V", "setSelectedItemInterfaceId"),
    ("eY", "()I", "getSelectedItemInterfaceId"),
    ("aB", "(I)V", "setSelectedItemSlot"),
    ("eZ", "()I", "getSelectedItemSlot"),
]
for _old_name, _descriptor, _mapped_name in _PLAYER_ITEM_SELECTION_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)
for _old_name, _mapped_name in [
    ("gk", "selectedItemId"),
    ("gl", "selectedItemInterfaceId"),
    ("gm", "selectedItemSlot"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, "I"), _mapped_name)
_PLAYER_INTERACTION_TARGET_METHOD_NAMES = [
    ("E", "(I)V", "setInteractionTargetId"),
    ("bK", "()I", "getInteractionTargetId"),
    ("B", "(I)V", "setInteractionTargetX"),
    ("bH", "()I", "getInteractionTargetX"),
    ("C", "(I)V", "setInteractionTargetY"),
    ("bI", "()I", "getInteractionTargetY"),
    ("ay", "(I)V", "setInteractionTargetPlane"),
    ("eX", "()I", "getInteractionTargetPlane"),
    ("G", "(I)V", "setInteractionTargetIndex"),
    ("bM", "()I", "getInteractionTargetIndex"),
    ("D", "(I)V", "setInteractionSpellButtonId"),
    ("bJ", "()I", "getInteractionSpellButtonId"),
]
for _old_name, _descriptor, _mapped_name in _PLAYER_INTERACTION_TARGET_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)
for _old_name, _mapped_name in [
    ("gj", "interactionTargetId"),
    ("gg", "interactionTargetX"),
    ("gh", "interactionTargetY"),
    ("gi", "interactionTargetPlane"),
    ("gn", "interactionTargetIndex"),
    ("gf", "interactionSpellButtonId"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, "I"), _mapped_name)

_PLAYER_UPDATE_STATE_METHOD_NAMES = [
    ("j", "(Z)V", "setRegistered"),
    ("bW", "()Z", "isRegistered"),
    ("f", "(Z)V", "setAppearanceUpdateRequired"),
    ("bw", "()Z", "isAppearanceUpdateRequired"),
    ("g", "(II)V", "setCombatBonus"),
    ("bU", "()Ljava/util/Map;", "getCombatBonuses"),
    ("al", "(I)V", "setStandAnimationOverride"),
    ("ak", "(I)V", "setWalkAnimationOverride"),
    ("aj", "(I)V", "setRunAnimationOverride"),
    ("el", "()I", "getStandAnimation"),
    ("em", "()I", "getWalkAnimation"),
    ("en", "()I", "getRunAnimation"),
]
for _old_name, _descriptor, _mapped_name in _PLAYER_UPDATE_STATE_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)
for _old_name, _descriptor, _mapped_name in [
    ("gu", "Z", "registered"),
    ("gF", "Z", "appearanceUpdateRequired"),
    ("gv", "Ljava/util/Map;", "combatBonuses"),
    ("hT", "I", "standAnimationOverride"),
    ("hU", "I", "walkAnimationOverride"),
    ("hS", "I", "runAnimationOverride"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)

_PLAYER_REGION_UPDATE_METHOD_NAMES = [
    ("bt", "()Lcom/rs2/model/Position;", "getLastKnownRegionPosition"),
    ("e", "(Z)V", "setTeleporting"),
    ("bu", "()Z", "isTeleporting"),
    ("g", "(Z)V", "setTeleportPlacementUpdateRequired"),
    ("by", "()Z", "isTeleportPlacementUpdateRequired"),
    ("f", "(Lcom/rs2/model/Position;)V", "applyTeleportPosition"),
]
for _old_name, _descriptor, _mapped_name in _PLAYER_REGION_UPDATE_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)
for _old_name, _descriptor, _mapped_name in [
    ("fS", "Lcom/rs2/model/Position;", "lastKnownRegionPosition"),
    ("g", "I", "localX"),
    ("h", "I", "localY"),
    ("gD", "Z", "teleporting"),
    ("gE", "Z", "teleportPlacementUpdateRequired"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/player/Player", _old_name, _descriptor), _mapped_name)

_INTERACTION_TYPE_OWNER = "com/rs2/model/interaction/InteractionType"
for _old_name, _mapped_name in [
    ("a", "FIRST_OBJECT"),
    ("b", "SECOND_OBJECT"),
    ("c", "THIRD_OBJECT"),
    ("d", "FOURTH_OBJECT"),
    ("e", "ITEM_ON_OBJECT"),
    ("f", "SPELL_ON_OBJECT"),
    ("g", "FIRST_NPC"),
    ("h", "ITEM_ON_NPC"),
    ("i", "SECOND_NPC"),
    ("j", "THIRD_NPC"),
    ("k", "FOURTH_NPC"),
]:
    FIELD_NAME_MAP.setdefault((_INTERACTION_TYPE_OWNER, _old_name, f"L{_INTERACTION_TYPE_OWNER};"), _mapped_name)
_INTERACTION_DISPATCHER_OWNER = "com/rs2/model/interaction/InteractionDispatcher"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;)V", "dispatchCurrentInteraction"),
    ("a", "(Lcom/rs2/model/interaction/InteractionType;)V", "setCurrentInteractionType"),
    (
        "a",
        "(Lcom/rs2/model/Position;Lcom/rs2/model/Position;Lcom/rs2/model/objects/WorldObject;)Z",
        "canReachObjectInteraction",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;Lcom/rs2/model/objects/WorldObject;)Z",
        "canReachObjectInteraction",
    ),
]:
    METHOD_NAME_MAP.setdefault((_INTERACTION_DISPATCHER_OWNER, _old_name, _descriptor), _mapped_name)
FIELD_NAME_MAP.setdefault(
    (_INTERACTION_DISPATCHER_OWNER, "a", "Lcom/rs2/model/interaction/InteractionType;"),
    "currentInteractionType",
)
_OBJECT_INTERACTION_TASK_OWNERS = [
    "FirstObjectActionTask",
    "SecondObjectActionTask",
    "ThirdObjectActionTask",
    "FourthObjectActionTask",
    "ItemOnObjectTask",
    "SpellOnObjectTask",
]
for _task_class in _OBJECT_INTERACTION_TASK_OWNERS:
    _owner = f"com/rs2/model/interaction/{_task_class}"
    for _old_name, _descriptor, _mapped_name in [
        ("a", "Lcom/rs2/model/player/Player;", "player"),
        ("b", "I", "actionSequence"),
        ("c", "I", "objectId"),
        ("d", "I", "objectX"),
        ("e", "I", "objectY"),
        ("f", "I", "objectPlane"),
    ]:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/interaction/FirstObjectActionTask", "g", "Ljava/lang/String;"),
    "objectName",
)
FIELD_NAME_MAP.setdefault(("com/rs2/model/interaction/ItemOnObjectTask", "g", "I"), "itemId")
FIELD_NAME_MAP.setdefault(
    (
        "com/rs2/model/interaction/SpellOnObjectTask",
        "g",
        "Lcom/rs2/model/skill/magic/SpellDefinition;",
    ),
    "spell",
)
for _task_class in [
    "FirstNpcActionTask",
    "SecondNpcActionTask",
    "ThirdNpcActionTask",
    "FourthNpcActionTask",
    "ItemOnNpcTask",
]:
    _owner = f"com/rs2/model/interaction/{_task_class}"
    for _old_name, _descriptor, _mapped_name in [
        ("a", "Lcom/rs2/model/player/Player;", "player"),
        ("b", "I", "actionSequence"),
        ("c", "Lcom/rs2/model/npc/Npc;", "npc"),
    ]:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
FIELD_NAME_MAP.setdefault(("com/rs2/model/interaction/ItemOnNpcTask", "d", "I"), "itemId")
FIELD_NAME_MAP.setdefault(("com/rs2/model/interaction/ItemOnNpcTask", "e", "I"), "itemSlot")
for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/objects/WorldObject", "a", "()I", "getObjectId"),
    ("com/rs2/model/objects/WorldObject", "b", "()I", "getType"),
    ("com/rs2/model/objects/WorldObject", "c", "()I", "getOrientation"),
    ("com/rs2/model/objects/WorldObject", "d", "()Lcom/rs2/model/Position;", "getPosition"),
    ("com/rs2/model/objects/LoadedWorldObject", "a", "()Lcom/rs2/model/Position;", "getPosition"),
    (
        "com/rs2/model/objects/LoadedWorldObject",
        "b",
        "()Lcom/rs2/model/objects/WorldObject;",
        "getWorldObject",
    ),
    ("com/rs2/model/objects/LoadedWorldObject", "c", "()I", "getType"),
    ("com/rs2/model/objects/LoadedWorldObject", "d", "()I", "getOrientation"),
    (
        "com/rs2/model/objects/DynamicObject",
        "a",
        "()Lcom/rs2/model/objects/WorldObject;",
        "getWorldObject",
    ),
    (
        "com/rs2/model/objects/ObjectDefinition",
        "a",
        "(I)Lcom/rs2/model/objects/ObjectDefinition;",
        "forId",
    ),
    ("com/rs2/model/objects/ObjectDefinition", "b", "()I", "getObjectId"),
    ("com/rs2/model/objects/ObjectDefinition", "c", "()Ljava/lang/String;", "getName"),
    ("com/rs2/model/objects/ObjectDefinition", "b", "(I)I", "getWidthForOrientation"),
    ("com/rs2/model/objects/ObjectDefinition", "c", "(I)I", "getLengthForOrientation"),
    ("com/rs2/model/objects/ObjectDefinition", "d", "()I", "getMaxDimension"),
    ("com/rs2/model/objects/ObjectDefinition", "e", "()Z", "isProjectileCollisionIgnored"),
    ("com/rs2/model/objects/WorldObjectLookup", "a", "()V", "loadWorldObjects"),
    (
        "com/rs2/model/objects/WorldObjectLookup",
        "a",
        "(III)Lcom/rs2/model/objects/LoadedWorldObject;",
        "findObjectAt",
    ),
    (
        "com/rs2/model/objects/WorldObjectLookup",
        "a",
        "(IIII)Lcom/rs2/model/objects/LoadedWorldObject;",
        "findObjectByIdAt",
    ),
    (
        "com/rs2/model/objects/WorldObjectLookup",
        "a",
        "(Ljava/lang/String;III)Lcom/rs2/model/objects/LoadedWorldObject;",
        "findObjectByNameAt",
    ),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "()Lcom/rs2/model/objects/ObjectManager;",
        "getInstance",
    ),
    ("com/rs2/model/objects/ObjectManager", "b", "()V", "processObjects"),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "(III)Lcom/rs2/model/objects/DynamicObject;",
        "findDynamicObjectAt",
    ),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "(IIII)Lcom/rs2/model/objects/DynamicObject;",
        "findDynamicObjectByIdAt",
    ),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "(Lcom/rs2/model/objects/DynamicObject;)V",
        "revertDynamicObject",
    ),
    ("com/rs2/model/objects/ObjectManager", "b", "(IIII)V", "removeDynamicObjectAt"),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "(Lcom/rs2/model/player/Player;)V",
        "refreshDynamicObjectsForPlayer",
    ),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "(Lcom/rs2/model/objects/DynamicObject;Lcom/rs2/model/player/Player;)Z",
        "isVisibleToPlayer",
    ),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "(Lcom/rs2/model/objects/DynamicObject;Z)V",
        "addDynamicObject",
    ),
    ("com/rs2/model/objects/ObjectManager", "c", "(IIII)V", "removeWallObjectCollision"),
    ("com/rs2/model/objects/ObjectManager", "b", "(IIIIII)V", "restoreObjectCollision"),
    ("com/rs2/model/objects/ObjectManager", "a", "(IIIII)V", "removeTypeNineObjectCollision"),
    ("com/rs2/model/objects/ObjectManager", "a", "(IIIIII)V", "removeObjectCollision"),
    (
        "com/rs2/model/objects/ObjectManager",
        "a",
        "(Lcom/rs2/model/player/Player;III)V",
        "prepareObjectInteractionMovement",
    ),
    (
        "com/rs2/model/skill/SkillActionHelper",
        "a",
        "(Lcom/rs2/model/player/Player;IILjava/lang/String;)Z",
        "checkSkillRequirement",
    ),
    ("com/rs2/model/skill/SkillActionHelper", "a", "(IIII)Z", "isObjectPresent"),
    (
        "com/rs2/model/skill/SkillActionHelper",
        "b",
        "(IIII)Lcom/rs2/model/objects/WorldObject;",
        "findWorldObjectById",
    ),
    (
        "com/rs2/model/skill/SkillActionHelper",
        "a",
        "(III)Lcom/rs2/model/objects/WorldObject;",
        "findWorldObjectAt",
    ),
    ("com/rs2/model/skill/SkillActionHelper", "c", "(IIII)I", "getObjectOrientation"),
    ("com/rs2/model/skill/SkillActionHelper", "d", "(IIII)I", "getObjectType"),
    (
        "com/rs2/model/skill/SkillActionHelper",
        "a",
        "(Lcom/rs2/model/player/Player;)Z",
        "shouldTriggerRandomEvent",
    ),
    (
        "com/rs2/model/objects/WorldObjectRegionIndex",
        "a",
        "(Lcom/rs2/model/Position;)Lcom/rs2/model/skill/agility/AgilityObstacleHandler;",
        "getOrCreateRegionBucket",
    ),
    (
        "com/rs2/model/skill/agility/AgilityObstacleHandler",
        "a",
        "()Ljava/util/Collection;",
        "getLoadedObjects",
    ),
    ("com/rs2/model/path/PathResult", "a", "()Ljava/util/Deque;", "getSteps"),
    ("com/rs2/model/path/PathStep", "a", "()I", "getX"),
    ("com/rs2/model/path/PathStep", "b", "()I", "getY"),
    (
        "com/rs2/model/path/PathStrategy",
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Position;Z)Lcom/rs2/model/path/PathResult;",
        "buildPath",
    ),
    (
        "com/rs2/model/path/DirectPathStrategy",
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/Position;Z)Lcom/rs2/model/path/PathResult;",
        "buildPath",
    ),
    ("com/rs2/model/path/PathReachability", "a", "(Lcom/rs2/model/player/Player;IIZII)Z", "isReachable"),
    ("com/rs2/util/path/PathFinder", "a", "()Lcom/rs2/util/path/PathFinder;", "getInstance"),
    ("com/rs2/util/path/PathFinder", "a", "(Lcom/rs2/model/player/Player;IIZII)Z", "findGlobalPath"),
    ("com/rs2/util/path/PathFinder", "b", "(Lcom/rs2/model/player/Player;IIZII)Z", "findPath"),
    ("com/rs2/util/path/WalkingCollisionMap", "b", "(IIII)V", "setTileFlag"),
    ("com/rs2/util/path/WalkingCollisionMap", "c", "(IIII)V", "clearTileFlag"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "(IIIIIZ)V", "removeWallCollision"),
    ("com/rs2/util/path/WalkingCollisionMap", "b", "(IIIIIZ)V", "removeAreaCollision"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "(IIII)V", "removeStraightWallCollision"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "(IIIIIIZ)V", "addObjectCollision"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "(IIIIII)V", "removeObjectCollision"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "(III)I", "getTileFlags"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "(IIIIIII)Z", "canTravelBetween"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "()V", "loadCollisionMaps"),
    ("com/rs2/util/path/WalkingCollisionMap", "a", "(I)Z", "hasDungeonCoordinateShiftRegion"),
    ("com/rs2/util/path/ProjectileCollisionMap", "a", "(IIII)V", "setTileFlag"),
    ("com/rs2/util/path/ProjectileCollisionMap", "b", "(IIII)V", "clearTileFlag"),
    ("com/rs2/util/path/ProjectileCollisionMap", "a", "(IIIIIZ)V", "removeWallCollision"),
    ("com/rs2/util/path/ProjectileCollisionMap", "b", "(IIIIIZ)V", "removeAreaCollision"),
    ("com/rs2/util/path/ProjectileCollisionMap", "a", "(IIIIIIZ)V", "addObjectCollision"),
    ("com/rs2/util/path/ProjectileCollisionMap", "a", "(IIIIII)V", "removeObjectCollisionForReachability"),
    ("com/rs2/util/path/ProjectileCollisionMap", "b", "(IIIIII)V", "removeObjectCollision"),
    ("com/rs2/util/path/ProjectileCollisionMap", "a", "(III)I", "getTileFlags"),
    ("com/rs2/util/path/ProjectileCollisionMap", "a", "()V", "loadCollisionMaps"),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "b",
        "(IIII)Lcom/rs2/model/objects/functions/DoorHandler;",
        "getOrCreateDoorState",
    ),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "a",
        "(IIII)Z",
        "hasDoorAt",
    ),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "a",
        "(Lcom/rs2/model/player/Player;IIII)Z",
        "handleDoor",
    ),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "a",
        "(Lcom/rs2/model/player/Player;IIIIII)V",
        "handleDoorMovement",
    ),
    ("com/rs2/model/objects/functions/DoorHandler", "a", "(I)Z", "isInitiallyOpenObjectId"),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "a",
        "(Lcom/rs2/model/objects/functions/DoorHandler;)I",
        "getCurrentObjectId",
    ),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "b",
        "(Lcom/rs2/model/objects/functions/DoorHandler;)I",
        "getCurrentX",
    ),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "c",
        "(Lcom/rs2/model/objects/functions/DoorHandler;)I",
        "getCurrentY",
    ),
    (
        "com/rs2/model/objects/functions/DoorHandler",
        "d",
        "(Lcom/rs2/model/objects/functions/DoorHandler;)I",
        "getPlane",
    ),
    (
        "com/rs2/model/objects/functions/DoubleDoorHandler",
        "b",
        "(III)Lcom/rs2/model/objects/functions/DoubleDoorHandler;",
        "getOrCreateDoubleDoorState",
    ),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "a", "(III)Z", "hasDoubleDoorAt"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "a", "(IIII)Z", "handleDoubleDoor"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "b", "(I)Z", "isInitiallyOpenObjectId"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "a", "(I)Z", "usesWideDoubleDoorOffset"),
    ("com/rs2/model/objects/functions/PickableObjectHandler", "a", "(Lcom/rs2/model/player/Player;III)Z", "handlePickableObject"),
    ("com/rs2/model/objects/functions/FlourMillHandler", "a", "(Lcom/rs2/model/player/Player;)V", "addGrainToHopper"),
    ("com/rs2/model/objects/functions/FlourMillHandler", "b", "(Lcom/rs2/model/player/Player;)V", "operateHopperControls"),
    ("com/rs2/model/objects/functions/FlourMillHandler", "c", "(Lcom/rs2/model/player/Player;)V", "collectFlourFromBin"),
    (
        "com/rs2/model/objects/functions/ObeliskTick",
        "b",
        "(Lcom/rs2/model/objects/functions/WildernessObelisk;)[Lcom/rs2/model/Position;",
        "buildCornerPositions",
    ),
    ("com/rs2/model/objects/functions/ObeliskTick", "a", "(Lcom/rs2/model/player/Player;I)Z", "activateForPlayer"),
    (
        "com/rs2/model/objects/functions/ObeliskTick",
        "a",
        "(Lcom/rs2/model/objects/functions/WildernessObelisk;)[Lcom/rs2/model/Position;",
        "getCornerPositions",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;)V",
        "startDelayedObjectMove",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "a",
        "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V",
        "climbOneFloorAtCurrentTile",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "a",
        "(Lcom/rs2/model/player/Player;Ljava/lang/String;I)V",
        "climbTwoFloorsAtCurrentTile",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "a",
        "(Lcom/rs2/model/player/Player;IILjava/lang/String;)V",
        "climbDirectionalStairs",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "b",
        "(Lcom/rs2/model/player/Player;IILjava/lang/String;)V",
        "climbFourTileDirectionalStairs",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "b",
        "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V",
        "climbOffsetLadder",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "c",
        "(Lcom/rs2/model/player/Player;IILjava/lang/String;)V",
        "climbDungeonLadder",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "a",
        "(Lcom/rs2/model/player/Player;IILcom/rs2/model/objects/WorldObject;)V",
        "toggleObjectAfterAnimation",
    ),
    (
        "com/rs2/model/combat/AttackStyleDefinition",
        "a",
        "(Lcom/rs2/model/player/Player;III)V",
        "slashWeb",
    ),
]:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/objects/WorldObject", "a", "I", "objectId"),
    ("com/rs2/model/objects/WorldObject", "b", "I", "type"),
    ("com/rs2/model/objects/WorldObject", "c", "I", "orientation"),
    ("com/rs2/model/objects/WorldObject", "d", "Lcom/rs2/model/Position;", "position"),
    ("com/rs2/model/objects/LoadedWorldObject", "a", "Lcom/rs2/model/Position;", "position"),
    (
        "com/rs2/model/objects/LoadedWorldObject",
        "b",
        "Lcom/rs2/model/objects/WorldObject;",
        "worldObject",
    ),
    ("com/rs2/model/objects/LoadedWorldObject", "c", "I", "type"),
    ("com/rs2/model/objects/LoadedWorldObject", "d", "I", "orientation"),
    (
        "com/rs2/model/objects/DynamicObject",
        "f",
        "Lcom/rs2/model/objects/WorldObject;",
        "worldObject",
    ),
    ("com/rs2/model/objects/DynamicObject", "a", "I", "orientation"),
    ("com/rs2/model/objects/DynamicObject", "b", "I", "restoreObjectId"),
    ("com/rs2/model/objects/DynamicObject", "c", "I", "remainingTicks"),
    ("com/rs2/model/objects/DynamicObject", "d", "Z", "updatesCollision"),
    ("com/rs2/model/objects/DynamicObject", "e", "Lcom/rs2/model/player/Player;", "owner"),
    (
        "com/rs2/model/objects/ObjectDefinition",
        "a",
        "[Lcom/rs2/model/objects/ObjectDefinition;",
        "definitionsById",
    ),
    ("com/rs2/model/objects/ObjectDefinition", "m", "I", "objectId"),
    ("com/rs2/model/objects/ObjectDefinition", "c", "Ljava/lang/String;", "name"),
    ("com/rs2/model/objects/ObjectDefinition", "d", "Ljava/lang/String;", "description"),
    ("com/rs2/model/objects/ObjectDefinition", "e", "I", "width"),
    ("com/rs2/model/objects/ObjectDefinition", "f", "I", "length"),
    ("com/rs2/model/objects/ObjectDefinition", "g", "Z", "solid"),
    ("com/rs2/model/objects/ObjectDefinition", "i", "Z", "interactive"),
    ("com/rs2/model/objects/ObjectDefinition", "j", "Z", "blocksProjectiles"),
    ("com/rs2/model/objects/ObjectDefinition", "l", "Z", "projectileCollisionIgnored"),
    ("com/rs2/model/objects/ObjectManager", "a", "Ljava/util/ArrayList;", "activeDynamicObjects"),
    ("com/rs2/model/objects/ObjectManager", "b", "Ljava/util/ArrayList;", "pendingRemovalObjects"),
    (
        "com/rs2/model/objects/ObjectManager",
        "c",
        "Lcom/rs2/model/objects/ObjectManager;",
        "instance",
    ),
    ("com/rs2/model/player/Player", "bL", "Ljava/util/ArrayList;", "visibleDynamicObjects"),
    ("com/rs2/model/player/Player", "bM", "Ljava/util/ArrayList;", "pendingDynamicObjectRemovals"),
    ("com/rs2/model/skill/SkillActionHelper", "a", "[Ljava/lang/String;", "skillNames"),
    ("com/rs2/model/objects/ObjectRegionKey", "a", "I", "regionX"),
    ("com/rs2/model/objects/ObjectRegionKey", "b", "I", "regionY"),
    ("com/rs2/model/objects/WorldObjectRegionIndex", "a", "Ljava/util/Map;", "bucketsByRegion"),
    ("com/rs2/model/skill/agility/AgilityObstacleHandler", "a", "Ljava/util/List;", "loadedObjects"),
    ("com/rs2/model/path/PathResult", "a", "Ljava/util/Deque;", "steps"),
    ("com/rs2/model/path/PathStep", "a", "I", "x"),
    ("com/rs2/model/path/PathStep", "b", "I", "y"),
    ("com/rs2/util/path/PathFinder", "a", "Lcom/rs2/util/path/PathFinder;", "instance"),
    (
        "com/rs2/util/path/WalkingCollisionMap",
        "a",
        "[Lcom/rs2/util/path/WalkingCollisionMap;",
        "regions",
    ),
    ("com/rs2/util/path/WalkingCollisionMap", "b", "I", "regionId"),
    ("com/rs2/util/path/WalkingCollisionMap", "c", "[[[I", "tileFlags"),
    ("com/rs2/util/path/WalkingCollisionMap", "d", "[I", "regionIds"),
    (
        "com/rs2/util/path/ProjectileCollisionMap",
        "a",
        "[Lcom/rs2/util/path/ProjectileCollisionMap;",
        "regions",
    ),
    ("com/rs2/util/path/ProjectileCollisionMap", "b", "I", "regionId"),
    ("com/rs2/util/path/ProjectileCollisionMap", "c", "[[[I", "tileFlags"),
    ("com/rs2/model/objects/functions/DoorHandler", "a", "Ljava/util/List;", "doorStates"),
    ("com/rs2/model/objects/functions/DoorHandler", "b", "Z", "open"),
    ("com/rs2/model/objects/functions/DoorHandler", "c", "J", "lastInteractionMillis"),
    ("com/rs2/model/objects/functions/DoorHandler", "d", "I", "rapidToggleCount"),
    ("com/rs2/model/objects/functions/DoorHandler", "e", "I", "currentObjectId"),
    ("com/rs2/model/objects/functions/DoorHandler", "f", "I", "originalObjectId"),
    ("com/rs2/model/objects/functions/DoorHandler", "g", "I", "currentX"),
    ("com/rs2/model/objects/functions/DoorHandler", "h", "I", "currentY"),
    ("com/rs2/model/objects/functions/DoorHandler", "i", "I", "originalX"),
    ("com/rs2/model/objects/functions/DoorHandler", "j", "I", "originalY"),
    ("com/rs2/model/objects/functions/DoorHandler", "k", "I", "plane"),
    ("com/rs2/model/objects/functions/DoorHandler", "l", "I", "originalOrientation"),
    ("com/rs2/model/objects/functions/DoorHandler", "m", "I", "currentOrientation"),
    ("com/rs2/model/objects/functions/DoorHandler", "n", "I", "objectType"),
    ("com/rs2/model/objects/functions/DoorHandler", "o", "Z", "initiallyOpen"),
    ("com/rs2/model/objects/functions/DoorHandler", "p", "[I", "initiallyOpenObjectIds"),
    ("com/rs2/model/objects/functions/DoorResetEvent", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/objects/functions/DoorResetEvent", "b", "Lcom/rs2/model/objects/functions/DoorHandler;", "door"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "a", "Ljava/util/List;", "doorStates"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "b", "I", "currentObjectId"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "c", "I", "originalObjectId"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "d", "Z", "initiallyOpen"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "e", "I", "currentX"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "f", "I", "currentY"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "g", "I", "plane"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "h", "I", "originalX"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "i", "I", "originalY"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "j", "I", "currentOrientation"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "k", "I", "originalOrientation"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "l", "[I", "initiallyOpenObjectIds"),
    ("com/rs2/model/objects/functions/DoubleDoorHandler", "m", "[I", "wideOffsetObjectIds"),
    ("com/rs2/model/objects/functions/DelayedObjectMoveEvent", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/objects/functions/DelayedObjectMoveEvent", "b", "Lcom/rs2/model/Position;", "destination"),
    ("com/rs2/model/objects/functions/ObjectToggleEvent", "a", "I", "restoreObjectId"),
    ("com/rs2/model/objects/functions/ObjectToggleEvent", "b", "Lcom/rs2/model/objects/WorldObject;", "worldObject"),
    ("com/rs2/model/objects/functions/ObjectToggleEvent", "c", "I", "objectType"),
    ("com/rs2/model/objects/functions/ObjectToggleEvent", "d", "I", "toggledObjectId"),
    ("com/rs2/model/objects/functions/ObjectToggleEvent", "e", "I", "toggledOrientation"),
    ("com/rs2/model/objects/functions/ObjectToggleEvent", "f", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/objects/functions/WebSlashEvent", "a", "Ljava/lang/Boolean;", "successful"),
    ("com/rs2/model/objects/functions/WebSlashEvent", "b", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/objects/functions/WebSlashEvent", "c", "I", "objectX"),
    ("com/rs2/model/objects/functions/WebSlashEvent", "d", "I", "objectY"),
    ("com/rs2/model/objects/functions/WebSlashEvent", "e", "I", "orientation"),
    ("com/rs2/model/objects/functions/PickableObjectHandler", "a", "[[I", "pickableObjects"),
    ("com/rs2/model/objects/functions/PickableObjectEvent", "a", "I", "objectId"),
    ("com/rs2/model/objects/functions/PickableObjectEvent", "b", "I", "objectX"),
    ("com/rs2/model/objects/functions/PickableObjectEvent", "c", "I", "objectY"),
    ("com/rs2/model/objects/functions/PickableObjectEvent", "d", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/objects/functions/PickableObjectEvent", "e", "Lcom/rs2/model/item/ItemStack;", "item"),
    ("com/rs2/model/objects/functions/PickableObjectEvent", "f", "Ljava/lang/String;", "itemName"),
    ("com/rs2/model/objects/functions/FlourMillHandler", "a", "I", "flourBinConfigId"),
    ("com/rs2/model/player/Player", "bW", "I", "flourMillHopperGrainCount"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "a", "Lcom/rs2/model/objects/functions/WildernessObelisk;", "LEVEL_13"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "h", "Lcom/rs2/model/objects/functions/WildernessObelisk;", "LEVEL_19"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "i", "Lcom/rs2/model/objects/functions/WildernessObelisk;", "LEVEL_27"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "j", "Lcom/rs2/model/objects/functions/WildernessObelisk;", "LEVEL_35"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "k", "Lcom/rs2/model/objects/functions/WildernessObelisk;", "LEVEL_44"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "l", "Lcom/rs2/model/objects/functions/WildernessObelisk;", "LEVEL_50"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "b", "Lcom/rs2/model/Position;", "basePosition"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "c", "Z", "active"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "d", "Ljava/awt/Rectangle;", "teleportBounds"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "e", "Lcom/rs2/util/RectangularArea;", "effectArea"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "f", "[Lcom/rs2/model/Position;", "cornerPositions"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "g", "I", "objectId"),
    ("com/rs2/model/objects/functions/WildernessObelisk", "m", "[Lcom/rs2/model/objects/functions/WildernessObelisk;", "VALUES"),
    ("com/rs2/model/objects/functions/ObeliskTick", "a", "I", "activationDelayTicks"),
    ("com/rs2/model/objects/functions/ObeliskTick", "b", "Lcom/rs2/model/objects/functions/WildernessObelisk;", "obelisk"),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
_ITEM_ACTION_PACKET_HANDLER_OWNER = "com/rs2/net/packet/handler/ItemActionPacketHandler"
for _old_name, _mapped_name in [
    ("b", "handleDropItem"),
    ("c", "handleInterfaceItemAmountFive"),
    ("d", "handleInterfaceItemAmountTenOrOperate"),
    ("e", "handleInterfaceItemAmountAll"),
    ("f", "handleInventoryItemFirstOption"),
    ("g", "handleInventoryItemSecondOption"),
    ("h", "handleInventoryItemThirdOption"),
    ("i", "handleEquipItem"),
    ("j", "handleMagicOnItem"),
    ("k", "handleMagicOnGroundItem"),
]:
    METHOD_NAME_MAP.setdefault(
        (
            _ITEM_ACTION_PACKET_HANDLER_OWNER,
            _old_name,
            "(Lcom/rs2/model/player/Player;Lcom/rs2/net/packet/IncomingPacket;)V",
        ),
        _mapped_name,
    )
_FARMING_MANAGER_PLAYER_ACCESSORS = [
    "AllotmentPatchManager",
    "BushPatchManager",
    "FlowerPatchManager",
    "FruitTreePatchManager",
    "HerbPatchManager",
    "HopsPatchManager",
    "SpecialCropPatchManager",
    "SpecialTreePatchManager",
    "TreePatchManager",
    "CompostBinManager",
]
for _manager_class in _FARMING_MANAGER_PLAYER_ACCESSORS:
    _owner = f"com/rs2/model/skill/farming/{_manager_class}"
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", f"(L{_owner};)Lcom/rs2/model/player/Player;"),
        "getPlayer",
    )
_FARMING_PATCH_VALUE_CLASSES = [
    "AllotmentPatch",
    "BushPatch",
    "FlowerPatch",
    "FruitTreePatch",
    "HerbPatch",
    "HopsPatch",
    "SpecialCropPatch",
    "SpecialTreePatch",
    "TreePatch",
    "CompostBin",
]
for _value_class in _FARMING_PATCH_VALUE_CLASSES:
    _owner = f"com/rs2/model/skill/farming/{_value_class}"
    METHOD_NAME_MAP.setdefault((_owner, "a", "()I"), "getIndex")
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", f"(Lcom/rs2/model/Position;)L{_owner};"),
        "forPosition",
    )
for _value_class in ["BushPatch", "FruitTreePatch", "HopsPatch", "TreePatch"]:
    _owner = f"com/rs2/model/skill/farming/{_value_class}"
    METHOD_NAME_MAP.setdefault((_owner, "a", f"(I)L{_owner};"), "forObjectId")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/farming/AllotmentPatch", "a", "(I)Ljava/util/ArrayList;"),
    "getIndexesForObjectId",
)
for _value_class in ["SpecialCropPatch", "SpecialTreePatch"]:
    METHOD_NAME_MAP.setdefault(
        (f"com/rs2/model/skill/farming/{_value_class}", "b", "()I"),
        "getObjectId",
    )
_FARMING_PATCH_MANAGER_LIFECYCLE_CLASSES = [
    "AllotmentPatchManager",
    "BushPatchManager",
    "FlowerPatchManager",
    "FruitTreePatchManager",
    "HerbPatchManager",
    "HopsPatchManager",
    "SpecialCropPatchManager",
    "SpecialTreePatchManager",
    "TreePatchManager",
]
for _manager_class in _FARMING_PATCH_MANAGER_LIFECYCLE_CLASSES:
    _owner = f"com/rs2/model/skill/farming/{_manager_class}"
    METHOD_NAME_MAP.setdefault((_owner, "a", "()V"), "refreshConfig")
    METHOD_NAME_MAP.setdefault((_owner, "b", "()V"), "processGrowth")
for _manager_class in ["AllotmentPatchManager", "FlowerPatchManager", "HopsPatchManager"]:
    METHOD_NAME_MAP.setdefault(
        (f"com/rs2/model/skill/farming/{_manager_class}", "a", "(I)Z"),
        "shouldStopGrowthCycle",
    )
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/farming/TreePatchManager", "c", "(I)Z"),
    "shouldStopGrowthCycle",
)
for _manager_class, _old_name in [
    ("BushPatchManager", "c"),
    ("FruitTreePatchManager", "b"),
    ("SpecialCropPatchManager", "b"),
    ("SpecialTreePatchManager", "b"),
]:
    METHOD_NAME_MAP.setdefault(
        (f"com/rs2/model/skill/farming/{_manager_class}", _old_name, "(I)Z"),
        "shouldStopGrowthCycle",
    )
_FARMING_PATCH_INTERACTION_METHOD_NAMES = [
    (
        "AllotmentPatchManager",
        [
            ("a", "(III)Z", "waterPatch"),
            ("b", "(III)Z", "clearPatch"),
            ("c", "(III)Z", "plantSeed"),
            ("a", "(II)Z", "harvestPatch"),
            ("d", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("e", "(III)Z", "curePatch"),
            ("b", "(I)V", "resetPatch"),
        ],
    ),
    (
        "BushPatchManager",
        [
            ("a", "(I)V", "recalculateRegrowthStage"),
            ("a", "(III)Z", "clearPatch"),
            ("b", "(III)Z", "plantSeed"),
            ("a", "(II)Z", "harvestPatch"),
            ("c", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("d", "(III)Z", "curePatch"),
            ("d", "(I)V", "resetPatch"),
        ],
    ),
    (
        "FlowerPatchManager",
        [
            ("a", "(III)Z", "waterPatch"),
            ("b", "(III)Z", "clearPatch"),
            ("c", "(III)Z", "plantSeed"),
            ("a", "(II)Z", "harvestPatch"),
            ("d", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("e", "(III)Z", "curePatch"),
            ("f", "(III)Z", "plantScarecrow"),
            ("b", "(I)V", "resetPatch"),
        ],
    ),
    (
        "FruitTreePatchManager",
        [
            ("a", "(I)V", "recalculateRegrowthStage"),
            ("a", "(III)Z", "clearPatch"),
            ("b", "(III)Z", "plantSapling"),
            ("a", "(II)Z", "harvestPatch"),
            ("c", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("d", "(III)Z", "prunePatch"),
            ("c", "(I)V", "resetPatch"),
        ],
    ),
    (
        "HerbPatchManager",
        [
            ("a", "(III)Z", "clearPatch"),
            ("b", "(III)Z", "plantSeed"),
            ("a", "(II)Z", "harvestPatch"),
            ("c", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("d", "(III)Z", "curePatch"),
            ("c", "(I)V", "resetPatch"),
        ],
    ),
    (
        "HopsPatchManager",
        [
            ("a", "(III)Z", "waterPatch"),
            ("b", "(III)Z", "clearPatch"),
            ("c", "(III)Z", "plantSeed"),
            ("a", "(II)Z", "harvestPatch"),
            ("d", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("e", "(III)Z", "curePatch"),
            ("b", "(I)V", "resetPatch"),
        ],
    ),
    (
        "SpecialCropPatchManager",
        [
            ("a", "(I)V", "recalculateRegrowthStage"),
            ("a", "(III)Z", "clearPatch"),
            ("b", "(III)Z", "plantSeed"),
            ("a", "(II)Z", "harvestPatch"),
            ("c", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("d", "(III)Z", "curePatch"),
            ("c", "(I)V", "resetPatch"),
        ],
    ),
    (
        "SpecialTreePatchManager",
        [
            ("a", "(I)V", "recalculateRegrowthStage"),
            ("a", "(III)Z", "clearPatch"),
            ("b", "(III)Z", "plantSapling"),
            ("a", "(II)Z", "handleSpecialTreeObject"),
            ("c", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("d", "(III)Z", "curePatch"),
            ("c", "(I)V", "resetPatch"),
        ],
    ),
    (
        "TreePatchManager",
        [
            ("a", "(III)Z", "clearPatch"),
            ("b", "(III)Z", "plantSapling"),
            ("a", "(II)Z", "checkHealth"),
            ("a", "(I)V", "scheduleStumpRegrowth"),
            ("c", "(III)Z", "compostPatch"),
            ("b", "(II)Z", "inspectPatch"),
            ("c", "(II)Z", "openSkillGuide"),
            ("d", "(II)Z", "startResurrection"),
            ("a", "(Z)Z", "finishResurrection"),
            ("d", "(III)Z", "prunePatch"),
            ("b", "(I)V", "resetPatch"),
            ("e", "(II)Z", "startCuttingTree"),
            ("f", "(II)Z", "canContinueCuttingTree"),
        ],
    ),
]
for _manager_class, _methods in _FARMING_PATCH_INTERACTION_METHOD_NAMES:
    _owner = f"com/rs2/model/skill/farming/{_manager_class}"
    for _old_name, _descriptor, _mapped_name in _methods:
        METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
for _name, _descriptor, _mapped_name in [
    ("a", "()V", "refreshConfig"),
    ("a", "(I)V", "startEmptyBin"),
    ("a", "(III)Z", "fillBin"),
    ("b", "()V", "processRotting"),
    ("a", "(II)Z", "handleBinObject"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/farming/CompostBinManager", _name, _descriptor), _mapped_name)
for _name, _descriptor, _mapped_name in [
    ("a", "()V", "open"),
    ("b", "()V", "refreshInventoryToolDisplayItems"),
    ("c", "()V", "refreshStoredToolDisplayItems"),
    ("d", "()V", "refreshInterface"),
    ("a", "(II)V", "depositItem"),
    ("b", "(II)V", "withdrawItem"),
    ("a", "(Lcom/rs2/model/npc/Npc;I)Z", "noteProduce"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/farming/FarmingToolStore", _name, _descriptor), _mapped_name)
for _name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;)V", "plantMithrilSeedFlower"),
    ("a", "(I)Z", "isMithrilSeedFlowerObjectId"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/farming/MithrilSeedFlowerHandler", _name, _descriptor), _mapped_name)
for _name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/farming/FarmingToolDefinition;", "forItemId"),
    ("b", "(I)Lcom/rs2/model/skill/farming/FarmingToolDefinition;", "forStorageIndex"),
    ("a", "()I", "getStorageIndex"),
    ("b", "()I", "getItemId"),
    ("c", "()I", "getConfigValue"),
    ("d", "()I", "getMaxStoredAmount"),
    ("e", "()I", "getNameTextInterfaceId"),
    ("f", "()I", "getAmountTextInterfaceId"),
    ("g", "()Ljava/lang/String;", "getDisplayName"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/farming/FarmingToolDefinition", _name, _descriptor),
        _mapped_name,
    )
for _growth_class in [
    "AllotmentGrowthDefinition",
    "BushGrowthDefinition",
    "FlowerGrowthDefinition",
    "HerbGrowthDefinition",
    "HopsGrowthDefinition",
    "SpecialCropGrowthDefinition",
]:
    _owner = f"com/rs2/model/skill/farming/{_growth_class}"
    METHOD_NAME_MAP.setdefault((_owner, "a", f"(I)L{_owner};"), "forCropId")
    METHOD_NAME_MAP.setdefault((_owner, "a", "()[[Ljava/lang/String;"), "getGrowthMessages")
for _growth_class in [
    "FarmedTreeGrowthDefinition",
    "FruitTreeGrowthDefinition",
    "SpecialTreeGrowthDefinition",
]:
    _owner = f"com/rs2/model/skill/farming/{_growth_class}"
    METHOD_NAME_MAP.setdefault((_owner, "a", f"(I)L{_owner};"), "forTreeId")
    METHOD_NAME_MAP.setdefault((_owner, "a", "()[[Ljava/lang/String;"), "getGrowthMessages")
for _name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/farming/SaplingDefinition;", "forSeedId"),
    ("b", "(I)Lcom/rs2/model/skill/farming/SaplingDefinition;", "forSeedlingId"),
    ("c", "(I)Lcom/rs2/model/skill/farming/SaplingDefinition;", "forWateredSeedlingId"),
    ("a", "()I", "getSeedId"),
    ("b", "()I", "getSeedlingId"),
    ("c", "()I", "getWateredSeedlingId"),
    ("d", "()I", "getSaplingId"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/farming/SaplingDefinition", _name, _descriptor),
        _mapped_name,
    )
for _name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/farming/CropStorageDefinition;", "forProduceItemId"),
    ("a", "()I", "getBaseContainerItemId"),
    ("b", "()I", "getProduceItemId"),
    ("c", "()Z", "isSack"),
    ("a", "(Lcom/rs2/model/skill/farming/CropStorageDefinition;)Z", "isSack"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/farming/CropStorageDefinition", _name, _descriptor),
        _mapped_name,
    )
for _name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/farming/FarmingFarmerDefinition;", "forNpcId"),
    ("a", "()Ljava/lang/String;", "getPatchType"),
    ("b", "()[Ljava/lang/String;", "getPatchLabels"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/farming/FarmingFarmerDefinition", _name, _descriptor),
        _mapped_name,
    )
for _name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;ILjava/lang/String;II)V", "handlePatchProtectionDialogue"),
    ("a", "(Lcom/rs2/model/player/Player;I)V", "chopTreeForFee"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "showRandomFarmingAdvice"),
    ("a", "(Ljava/lang/String;)Ljava/lang/String;", "formatPaymentItemName"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/farming/FarmingFarmerHandler", _name, _descriptor),
        _mapped_name,
    )
for _name, _descriptor, _mapped_name in [
    ("a", "(I)V", "finishInventorySeedlingGrowth"),
    ("a", "(III)V", "finishBankSeedlingGrowth"),
    ("a", "(II)Z", "waterSeedling"),
    ("a", "(IIII)Z", "plantSeedInPot"),
    ("b", "(III)Z", "fillPlantPotWithSoil"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/farming/PlantPotHandler", _name, _descriptor),
        _mapped_name,
    )
_FARMING_PRODUCE_DEFINITION_METHOD_NAMES = [
    (
        "com/rs2/model/skill/farming/AllotmentCropDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/AllotmentCropDefinition;", "forSeedId"),
            ("a", "()I", "getSeedId"),
            ("b", "()I", "getProduceItemId"),
            ("c", "()I", "getProtectionCropId"),
            ("d", "()I", "getSeedAmount"),
            ("e", "()I", "getRequiredLevel"),
            ("f", "()[I", "getProtectionPayment"),
            ("g", "()D", "getDiseaseChance"),
            ("h", "()D", "getPlantingExperience"),
            ("i", "()D", "getHarvestExperience"),
            ("j", "()I", "getConfigStartStage"),
            ("k", "()I", "getConfigEndStage"),
            ("l", "()I", "getGrowthStageCount"),
            ("m", "()I", "getGrowthCycleTicks"),
            ("n", "()I", "getHarvestChanceLow"),
            ("o", "()I", "getHarvestChanceHigh"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/BushDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/BushDefinition;", "forSeedId"),
            ("a", "()I", "getSeedId"),
            ("b", "()I", "getProduceItemId"),
            ("c", "()I", "getSeedAmount"),
            ("d", "()I", "getRequiredLevel"),
            ("e", "()[I", "getProtectionPayment"),
            ("f", "()I", "getTotalGrowthTicks"),
            ("g", "()D", "getDiseaseChance"),
            ("h", "()D", "getPlantingExperience"),
            ("i", "()D", "getHarvestExperience"),
            ("j", "()I", "getConfigStartStage"),
            ("k", "()I", "getGrowthStageCount"),
            ("l", "()I", "getGrowthCycleTicks"),
            ("m", "()I", "getHealthCheckConfigStage"),
            ("n", "()D", "getHealthCheckExperience"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FlowerDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/FlowerDefinition;", "forSeedId"),
            ("a", "()I", "getProduceItemId"),
            ("b", "()I", "getRequiredLevel"),
            ("c", "()D", "getDiseaseChance"),
            ("d", "()D", "getPlantingExperience"),
            ("e", "()D", "getHarvestExperience"),
            ("f", "()I", "getConfigStartStage"),
            ("g", "()I", "getConfigEndStage"),
            ("h", "()I", "getGrowthStageCount"),
            ("i", "()I", "getGrowthCycleTicks"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HerbDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/HerbDefinition;", "forSeedId"),
            ("a", "()I", "getProduceItemId"),
            ("b", "()I", "getRequiredLevel"),
            ("c", "()D", "getDiseaseChance"),
            ("d", "()D", "getPlantingExperience"),
            ("e", "()D", "getHarvestExperience"),
            ("f", "()I", "getConfigStartStage"),
            ("g", "()I", "getConfigEndStage"),
            ("h", "()I", "getGrowthCycleTicks"),
            ("i", "()I", "getHarvestChanceLow"),
            ("j", "()I", "getHarvestChanceHigh"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/HopsDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/HopsDefinition;", "forSeedId"),
            ("a", "()I", "getSeedId"),
            ("b", "()I", "getProduceItemId"),
            ("c", "()I", "getSeedAmount"),
            ("d", "()I", "getRequiredLevel"),
            ("e", "()[I", "getProtectionPayment"),
            ("f", "()D", "getDiseaseChance"),
            ("g", "()D", "getPlantingExperience"),
            ("h", "()D", "getHarvestExperience"),
            ("i", "()I", "getConfigStartStage"),
            ("j", "()I", "getConfigEndStage"),
            ("k", "()I", "getGrowthStageCount"),
            ("l", "()I", "getGrowthCycleTicks"),
            ("m", "()I", "getHarvestChanceLow"),
            ("n", "()I", "getHarvestChanceHigh"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FarmedTreeDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/FarmedTreeDefinition;", "forSaplingId"),
            ("a", "()I", "getRootItemId"),
            ("b", "()I", "getRequiredLevel"),
            ("c", "()[I", "getProtectionPayment"),
            ("d", "()D", "getDiseaseChance"),
            ("e", "()D", "getPlantingExperience"),
            ("f", "()D", "getHealthCheckExperience"),
            ("g", "()I", "getConfigStartStage"),
            ("h", "()I", "getConfigEndStage"),
            ("i", "()I", "getGrowthCycleTicks"),
            ("j", "()I", "getCheckedTreeConfigStage"),
            ("k", "()I", "getStumpConfigStage"),
            ("l", "()I", "getTreeObjectId"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/FruitTreeDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/FruitTreeDefinition;", "forSaplingId"),
            ("a", "()I", "getProduceItemId"),
            ("b", "()I", "getRequiredLevel"),
            ("c", "()[I", "getProtectionPayment"),
            ("d", "()I", "getTotalGrowthTicks"),
            ("e", "()D", "getDiseaseChance"),
            ("f", "()D", "getPlantingExperience"),
            ("g", "()D", "getHarvestExperience"),
            ("h", "()I", "getConfigStartStage"),
            ("i", "()I", "getGrowthStageCount"),
            ("j", "()I", "getGrowthCycleTicks"),
            ("k", "()I", "getMatureConfigStage"),
            ("l", "()I", "getStumpConfigStage"),
            ("m", "()I", "getHealthCheckConfigStage"),
            ("n", "()D", "getHealthCheckExperience"),
            ("o", "()I", "getDiseasedConfigOffset"),
            ("p", "()I", "getDeadConfigOffset"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialCropDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/SpecialCropDefinition;", "forSeedId"),
            ("a", "()I", "getSeedId"),
            ("b", "()I", "getProduceItemId"),
            ("c", "()I", "getSeedAmount"),
            ("d", "()I", "getRequiredLevel"),
            ("e", "()I", "getTotalGrowthTicks"),
            ("f", "()D", "getDiseaseChance"),
            ("g", "()D", "getPlantingExperience"),
            ("h", "()D", "getHarvestExperience"),
            ("i", "()I", "getConfigStartStage"),
            ("j", "()I", "getGrowthStageCount"),
            ("k", "()I", "getGrowthCycleTicks"),
            ("l", "()I", "getHealthCheckConfigStage"),
            ("m", "()D", "getHealthCheckExperience"),
            ("n", "()I", "getDiseasedConfigOffset"),
            ("o", "()I", "getDeadConfigOffset"),
        ],
    ),
    (
        "com/rs2/model/skill/farming/SpecialTreeDefinition",
        [
            ("a", "(I)Lcom/rs2/model/skill/farming/SpecialTreeDefinition;", "forSaplingId"),
            ("a", "()I", "getProduceItemId"),
            ("b", "()I", "getRequiredLevel"),
            ("c", "()I", "getTotalGrowthTicks"),
            ("d", "()D", "getDiseaseChance"),
            ("e", "()D", "getPlantingExperience"),
            ("f", "()D", "getHarvestExperience"),
            ("g", "()I", "getConfigStartStage"),
            ("h", "()I", "getGrowthStageCount"),
            ("i", "()I", "getGrowthCycleTicks"),
            ("j", "()I", "getHealthCheckConfigStage"),
            ("k", "()D", "getHealthCheckExperience"),
            ("l", "()I", "getDiseasedConfigOffset"),
            ("m", "()I", "getDeadConfigOffset"),
        ],
    ),
]
for _owner, _methods in _FARMING_PRODUCE_DEFINITION_METHOD_NAMES:
    for _old_name, _descriptor, _mapped_name in _methods:
        METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
METHOD_NAME_MAP.setdefault(("com/rs2/util/ElapsedTimer", "a", "()V"), "reset")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ElapsedTimer", "b", "()J"), "elapsedMillis")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ProfilerTimer", "a", "()V"), "start")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ProfilerTimer", "b", "()V"), "stop")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ProfilerTimer", "c", "()V"), "reset")
METHOD_NAME_MAP.setdefault(("com/rs2/util/ProfilerTimer", "d", "()J"), "getAccumulatedMillis")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/util/ProfilerRegistry", "a", "(Ljava/lang/String;)Lcom/rs2/util/ProfilerTimer;"),
    "getTimer",
)
METHOD_NAME_MAP.setdefault(("com/rs2/util/ProfilerRegistry", "a", "()V"), "resetAll")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "x", "(I)V"), "setPublicChatColor")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bz", "()I"), "getPublicChatColor")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "y", "(I)V"), "setPublicChatEffects")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bA", "()I"), "getPublicChatEffects")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "a", "([B)V"), "setPublicChatPayload")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/Player", "bB", "()[B"), "getPublicChatPayload")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/player/Player", "a", "(Ljava/lang/String;II)V"),
    "queuePublicChatMessage",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/player/Player", "a", "(Ljava/lang/String;)V"),
    "queuePublicChatMessage",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "a", "()V"), "initializePrivateMessaging")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "a", "(Z)V"), "refreshFriendStatuses")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "a", "(J)V"), "addFriend")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "b", "(J)V"), "addIgnore")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/player/SocialManager", "a", "(Lcom/rs2/model/player/Player;J[BI)V"),
    "sendPrivateMessage",
)
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "a", "([JJ)V"), "removeFromList")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "b", "([JJ)Z"), "containsNameHash")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "a", "([J)I"), "countEntries")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "b", "([J)I"), "findFreeSlot")
METHOD_NAME_MAP.setdefault(("com/rs2/model/player/SocialManager", "b", "()I"), "nextPrivateMessageId")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketSender", "a", "(JI)Lcom/rs2/net/packet/PacketSender;"), "sendFriendStatus")
METHOD_NAME_MAP.setdefault(
    ("com/rs2/net/packet/PacketSender", "a", "(JIII[BI)Lcom/rs2/net/packet/PacketSender;"),
    "sendPrivateMessage",
)
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketSender", "j", "(I)Lcom/rs2/net/packet/PacketSender;"), "sendPrivateMessagingStatus")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketSender", "e", "()Lcom/rs2/net/packet/PacketSender;"), "sendAccountStatus")

METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketBuffer", "a", "(Ljava/nio/ByteBuffer;)Lcom/rs2/net/packet/PacketReader;"), "wrapReader")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketBuffer", "a", "(I)Lcom/rs2/net/packet/PacketWriter;"), "allocateWriter")
for _owner in [
    "com/rs2/net/packet/PacketBuffer",
    "com/rs2/net/packet/PacketReader",
    "com/rs2/net/packet/PacketWriter",
]:
    METHOD_NAME_MAP.setdefault((_owner, "a", "(Lcom/rs2/net/packet/AccessMode;)V"), "onAccessModeChanged")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketBuffer", "b", "(Lcom/rs2/net/packet/AccessMode;)V"), "setAccessMode")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketBuffer", "a", "()Lcom/rs2/net/packet/AccessMode;"), "getAccessMode")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketBuffer", "b", "(I)V"), "setBitPosition")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketBuffer", "b", "()I"), "getBitPosition")
for _owner in ["com/rs2/net/packet/PacketReader", "com/rs2/net/packet/PacketWriter"]:
    METHOD_NAME_MAP.setdefault((_owner, "b", "(Lcom/rs2/net/packet/AccessMode;)V"), "setAccessMode")
    METHOD_NAME_MAP.setdefault((_owner, "a", "()Lcom/rs2/net/packet/AccessMode;"), "getAccessMode")
    METHOD_NAME_MAP.setdefault((_owner, "b", "(I)V"), "setBitPosition")
    METHOD_NAME_MAP.setdefault((_owner, "b", "()I"), "getBitPosition")

METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(ZLcom/rs2/net/packet/ByteTransform;)I"), "readByte")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "c", "()I"), "readSignedByte")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(Z)I"), "readUnsignedByte")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(Lcom/rs2/net/packet/ByteTransform;)I"), "readSignedByte")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(ZLcom/rs2/net/packet/ByteTransform;Lcom/rs2/net/packet/ByteOrder;)I"), "readShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "d", "()I"), "readSignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "b", "(Z)I"), "readSignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "b", "(Lcom/rs2/net/packet/ByteTransform;)I"), "readSignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "b", "(ZLcom/rs2/net/packet/ByteTransform;)I"), "readSignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(Lcom/rs2/net/packet/ByteOrder;)I"), "readSignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(ZLcom/rs2/net/packet/ByteOrder;)I"), "readSignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(Lcom/rs2/net/packet/ByteTransform;Lcom/rs2/net/packet/ByteOrder;)I"), "readSignedShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "e", "()I"), "readInt")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "f", "()J"), "readLong")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "g", "()Ljava/lang/String;"), "readString")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "c", "(I)[B"), "readBytes")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "a", "(ILcom/rs2/net/packet/ByteTransform;)[B"), "readBytesReverse")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketReader", "h", "()Ljava/nio/ByteBuffer;"), "getBuffer")

METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(Lcom/rs2/net/IsaacCipher;I)V"), "writeOpcode")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "b", "(Lcom/rs2/net/IsaacCipher;I)V"), "startVariableBytePacket")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "c", "(Lcom/rs2/net/IsaacCipher;I)V"), "startVariableShortPacket")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "c", "()V"), "finishVariableBytePacket")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "d", "()V"), "finishVariableShortPacket")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "b", "(Ljava/nio/ByteBuffer;)V"), "writeBuffer")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "([BI)V"), "writeBytes")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(II)V"), "writeBits")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(Z)V"), "writeBoolean")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(ILcom/rs2/net/packet/ByteTransform;)V"), "writeByte")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "c", "(I)V"), "writeByte")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(ILcom/rs2/net/packet/ByteTransform;Lcom/rs2/net/packet/ByteOrder;)V"), "writeShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "d", "(I)V"), "writeShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "b", "(ILcom/rs2/net/packet/ByteTransform;)V"), "writeShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(ILcom/rs2/net/packet/ByteOrder;)V"), "writeShort")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "b", "(ILcom/rs2/net/packet/ByteTransform;Lcom/rs2/net/packet/ByteOrder;)V"), "writeInt")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "e", "(I)V"), "writeInt")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "b", "(ILcom/rs2/net/packet/ByteOrder;)V"), "writeInt")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(J)V"), "writeLong")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "a", "(Ljava/lang/String;)V"), "writeString")
METHOD_NAME_MAP.setdefault(("com/rs2/net/packet/PacketWriter", "e", "()Ljava/nio/ByteBuffer;"), "getBuffer")

_COMBAT_EFFECT_OWNERS = [
    "com/rs2/model/combat/effect/CombatEffect",
    "com/rs2/model/combat/effect/MovementLockEffect",
    "com/rs2/model/combat/effect/BloodSpellHealEffect",
    "com/rs2/model/combat/effect/PoisonEffect",
    "com/rs2/model/combat/effect/StatDrainEffect",
    "com/rs2/model/combat/effect/WallBeastStunEffect",
    "com/rs2/model/combat/effect/SummonNpcCombatEffect",
    "com/rs2/model/combat/effect/TeleblockEffect",
    "com/rs2/model/combat/effect/TargetRandomTeleportEffect",
    "com/rs2/model/combat/effect/DisarmWeaponEffect",
]

for _owner in _COMBAT_EFFECT_OWNERS:
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Lcom/rs2/model/combat/CombatAction;Lcom/rs2/model/combat/effect/CombatEffectTask;)V"),
        "onAfterApply",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Lcom/rs2/model/Entity;)Z"),
        "canApplyTo",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "b", "(Lcom/rs2/model/combat/CombatAction;Lcom/rs2/model/combat/effect/CombatEffectTask;)V"),
        "onApply",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)Lcom/rs2/model/combat/effect/CombatEffectTask;"),
        "createTask",
    )

for _owner in [
    "com/rs2/model/combat/effect/CombatEffectTask",
    "com/rs2/model/combat/effect/PoisonDamageTask",
]:
    METHOD_NAME_MAP.setdefault(
        (_owner, "b", "()Lcom/rs2/model/combat/effect/CombatEffect;"),
        "getEffect",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "c", "()Lcom/rs2/model/EntityReference;"),
        "getSourceReference",
    )
    METHOD_NAME_MAP.setdefault(
        (_owner, "d", "()Lcom/rs2/model/EntityReference;"),
        "getTargetReference",
    )


def remap_internal_name(name: str) -> str:
    if name in EXACT_CLASS_MAP:
        return EXACT_CLASS_MAP[name]
    for old, new in sorted(PREFIX_CLASS_MAP.items(), key=lambda item: len(item[0]), reverse=True):
        if name.startswith(old):
            return new + name[len(old) :]
    return name


def remap_entry_name(name: str) -> str:
    if name.endswith(".class"):
        internal = name[:-6]
        return remap_internal_name(internal) + ".class"
    return name


_exact_patterns = [
    (re.compile(re.escape(old) + r"(?![A-Za-z0-9_$/])"), new)
    for old, new in sorted(EXACT_CLASS_MAP.items(), key=lambda item: len(item[0]), reverse=True)
]


def remap_text(text: str) -> str:
    for pattern, new in _exact_patterns:
        text = pattern.sub(new, text)
    for old, new in sorted(PREFIX_CLASS_MAP.items(), key=lambda item: len(item[0]), reverse=True):
        text = text.replace(old, new)
    return text


def _read_u2(data: bytes | bytearray, offset: int) -> int:
    return struct.unpack_from(">H", data, offset)[0]


def _write_u2(data: bytearray, offset: int, value: int) -> None:
    struct.pack_into(">H", data, offset, value)


def _skip_member_info(data: bytes | bytearray, offset: int) -> int:
    attributes_count = _read_u2(data, offset + 6)
    pos = offset + 8
    for _ in range(attributes_count):
        attribute_length = struct.unpack_from(">I", data, pos + 2)[0]
        pos += 6 + attribute_length
    return pos


def read_class_hierarchy_names(data: bytes) -> tuple[str, str | None]:
    if data[:4] != b"\xca\xfe\xba\xbe":
        raise ValueError("not a class file")

    cp_count = struct.unpack_from(">H", data, 8)[0]
    pos = 10
    index = 1
    cp: list[dict[str, object] | None] = [None] * cp_count

    while index < cp_count:
        tag = data[pos]
        pos += 1
        if tag == 1:
            length = struct.unpack_from(">H", data, pos)[0]
            pos += 2
            raw = data[pos : pos + length]
            pos += length
            cp[index] = {"tag": tag, "text": raw.decode("utf-8", errors="replace")}
        elif tag in (3, 4):
            pos += 4
        elif tag in (5, 6):
            pos += 8
            index += 1
        elif tag in (7, 8, 16, 19, 20):
            cp[index] = {"tag": tag, "index": struct.unpack_from(">H", data, pos)[0]}
            pos += 2
        elif tag in (9, 10, 11, 12, 17, 18):
            pos += 4
        elif tag == 15:
            pos += 3
        else:
            raise ValueError(f"unsupported constant-pool tag {tag} at index {index}")
        index += 1

    def utf8_text(idx: int) -> str:
        entry = cp[idx]
        if entry is None or entry["tag"] != 1:
            raise ValueError(f"constant-pool entry {idx} is not a UTF-8 text entry")
        return str(entry["text"])

    def class_name(idx: int) -> str:
        entry = cp[idx]
        if entry is None or entry["tag"] != 7:
            raise ValueError(f"constant-pool entry {idx} is not a class entry")
        return remap_internal_name(utf8_text(int(entry["index"])))

    this_class = class_name(_read_u2(data, pos + 2))
    super_index = _read_u2(data, pos + 4)
    super_class = class_name(super_index) if super_index else None
    return this_class, super_class


def populate_inherited_task_method_maps(class_to_super: dict[str, str | None]) -> None:
    def inherits(owner: str, target: str) -> bool:
        seen: set[str] = set()
        current = owner
        while current and current not in seen:
            if current == target:
                return True
            seen.add(current)
            current = class_to_super.get(current) or ""
        return False

    tick_owners = [
        owner
        for owner in class_to_super
        if inherits(owner, "com/rs2/model/task/TickTask")
    ]
    for owner in tick_owners:
        METHOD_NAME_MAP.setdefault((owner, "f", "()I"), "getIntervalTicks")
        METHOD_NAME_MAP.setdefault((owner, "g", "()I"), "getRemainingTicks")
        METHOD_NAME_MAP.setdefault((owner, "a", "(I)V"), "setIntervalTicks")
        METHOD_NAME_MAP.setdefault((owner, "b", "(I)V"), "setRemainingTicks")
        METHOD_NAME_MAP.setdefault((owner, "h", "()Z"), "isActive")
        METHOD_NAME_MAP.setdefault((owner, "e", "()V"), "stop")
        METHOD_NAME_MAP.setdefault((owner, "a", "()V"), "execute")
        METHOD_NAME_MAP.setdefault((owner, "i", "()V"), "tick")

    cycle_owners = [
        owner
        for owner in class_to_super
        if inherits(owner, "com/rs2/model/task/CycleEvent")
    ]
    for owner in cycle_owners:
        METHOD_NAME_MAP.setdefault(
            (owner, "a", "(Lcom/rs2/model/task/CycleEventContainer;)V"),
            "execute",
        )
        METHOD_NAME_MAP.setdefault((owner, "a", "()V"), "onStop")

    for target in [
        "com/rs2/model/skill/crafting/armor/CraftedArmorAction",
        "com/rs2/model/skill/crafting/armor/SplitbarkCraftingAction",
    ]:
        armor_action_owners = [
            owner
            for owner in class_to_super
            if inherits(owner, target)
        ]
        for owner in armor_action_owners:
            METHOD_NAME_MAP.setdefault((owner, "a", "()Z"), "startCrafting")

    npc_combat_definition_owners = [
        owner
        for owner in class_to_super
        if inherits(owner, "com/rs2/model/npc/combat/NpcCombatDefinition")
    ]
    for owner in npc_combat_definition_owners:
        METHOD_NAME_MAP.setdefault(
            (
                owner,
                "a",
                "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)[Lcom/rs2/model/combat/attack/CombatAttack;",
            ),
            "createAttacks",
        )
        METHOD_NAME_MAP.setdefault(
            (owner, "a", "(IIIII)Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
            "addAttackBonuses",
        )
        METHOD_NAME_MAP.setdefault(
            (owner, "b", "(IIIII)Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
            "addDefenceBonuses",
        )
        METHOD_NAME_MAP.setdefault(
            (owner, "a", "(I)Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
            "setRespawnDelayTicks",
        )
        METHOD_NAME_MAP.setdefault(
            (owner, "b", "(I)Lcom/rs2/model/npc/combat/NpcCombatDefinition;"),
            "setRespawnDelaySeconds",
        )
        METHOD_NAME_MAP.setdefault((owner, "a", "()I"), "getDeathDelayTicks")
        METHOD_NAME_MAP.setdefault((owner, "b", "()I"), "getRespawnDelayTicks")
        METHOD_NAME_MAP.setdefault((owner, "c", "()Ljava/util/Map;"), "getCombatBonuses")
        METHOD_NAME_MAP.setdefault((owner, "d", "()Z"), "hasAttackBonuses")
        METHOD_NAME_MAP.setdefault((owner, "e", "()Z"), "hasDefenceBonuses")

    bot_task_definition_owners = [
        owner
        for owner in class_to_super
        if inherits(owner, "com/rs2/bot/BotTaskDefinition")
    ]
    for owner in bot_task_definition_owners:
        METHOD_NAME_MAP.setdefault(
            (owner, "e", "(Lcom/rs2/model/player/Player;)Ljava/util/ArrayList;"),
            "getRequiredItems",
        )
        METHOD_NAME_MAP.setdefault((owner, "a", "(Lcom/rs2/model/player/Player;)V"), "startCustomTaskAction")
        METHOD_NAME_MAP.setdefault((owner, "b", "(Lcom/rs2/model/player/Player;)V"), "startEscapeMonitor")
        METHOD_NAME_MAP.setdefault((owner, "c", "(Lcom/rs2/model/player/Player;)Z"), "meetsUnlockRequirements")
        METHOD_NAME_MAP.setdefault((owner, "d", "(Lcom/rs2/model/player/Player;)Z"), "isWithinProgressionRange")
        METHOD_NAME_MAP.setdefault((owner, "b", "(I)V"), "setForcedCombatStyle")
        METHOD_NAME_MAP.setdefault((owner, "f", "()I"), "getForcedCombatStyle")
        METHOD_NAME_MAP.setdefault((owner, "g", "()I"), "getShopId")
        METHOD_NAME_MAP.setdefault((owner, "g", "(Lcom/rs2/model/player/Player;)I"), "getInteractionOption")
        METHOD_NAME_MAP.setdefault((owner, "i", "(Lcom/rs2/model/player/Player;)V"), "prepareTradeAdvertState")
        METHOD_NAME_MAP.setdefault((owner, "j", "(Lcom/rs2/model/player/Player;)V"), "prepareDropPartyState")
        METHOD_NAME_MAP.setdefault((owner, "k", "(Lcom/rs2/model/player/Player;)V"), "configureTaskInteractionTargets")
        METHOD_NAME_MAP.setdefault((owner, "l", "(Lcom/rs2/model/player/Player;)V"), "prepareTaskInventory")
        METHOD_NAME_MAP.setdefault((owner, "m", "(Lcom/rs2/model/player/Player;)V"), "prepareTaskCombatLoadout")
        METHOD_NAME_MAP.setdefault((owner, "n", "(Lcom/rs2/model/player/Player;)V"), "startWalkToTask")
        METHOD_NAME_MAP.setdefault((owner, "a", "(Lcom/rs2/model/player/Player;I)V"), "continueWalkToTask")
        METHOD_NAME_MAP.setdefault((owner, "q", "(Lcom/rs2/model/player/Player;)V"), "startWalkToBank")
        METHOD_NAME_MAP.setdefault((owner, "b", "(Lcom/rs2/model/player/Player;I)V"), "continueWalkToBank")
        METHOD_NAME_MAP.setdefault((owner, "c", "(Lcom/rs2/model/player/Player;Z)V"), "advanceTaskRouteSegment")
        for field_name, descriptor, mapped_name in [
            ("D", "I", "minimumServerRevision"),
            ("E", "Ljava/util/ArrayList;", "lootSellShopIds"),
            ("F", "Z", "usesCustomTaskAction"),
            ("G", "Z", "usesEscapeMonitor"),
            ("H", "Lcom/rs2/model/Position;", "startPosition"),
            ("bt", "[Lcom/rs2/util/RectangularArea;", "taskAreas"),
            ("bu", "Lcom/rs2/bot/BotRoute;", "pretaskRoute"),
            ("I", "Lcom/rs2/bot/BotRoute;", "taskRoute"),
            ("J", "[Lcom/rs2/bot/BotRoute;", "taskRouteSegments"),
            ("K", "[I", "ignoredLootItemIds"),
            ("M", "Z", "membersOnly"),
            ("N", "I", "interactionOption"),
            ("O", "Z", "combatTask"),
            ("P", "Z", "usesDepositBox"),
            ("Q", "I", "selectionWeight"),
            ("R", "I", "targetSearchRadius"),
            ("S", "Ljava/util/ArrayList;", "assignedBotPlayers"),
            ("T", "I", "targetMaxX"),
            ("U", "I", "targetMaxY"),
            ("V", "I", "targetMinX"),
            ("W", "I", "targetMinY"),
            ("X", "I", "forcedCombatStyle"),
            ("Y", "Z", "usesCombatTradeAdvertItems"),
        ]:
            FIELD_NAME_MAP.setdefault((owner, field_name, descriptor), mapped_name)

    for owner, old_name, descriptor, mapped_name in [
        (
            "com/rs2/bot/combat/BotPvpCombatHandler",
            "a",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V",
            "startBotPvpCombatTicks",
        ),
        (
            "com/rs2/bot/combat/BotPvpCombatHandler",
            "b",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V",
            "processBotPvpCombatTick",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;)V",
            "disableBotCombatPrayers",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;II)V",
            "setBotSkillLevel",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;[Lcom/rs2/util/RectangularArea;)Z",
            "isPlayerInAnyArea",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/Position;[Lcom/rs2/util/RectangularArea;)Z",
            "isPositionInAnyArea",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;[Lcom/rs2/model/Position;)V",
            "advanceBotEscapeWaypoints",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "c",
            "(Lcom/rs2/model/player/Player;)D",
            "calculateBotHitpointsExperience",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "d",
            "(Lcom/rs2/model/player/Player;)V",
            "sellBotLootItems",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "e",
            "(Lcom/rs2/model/player/Player;)Z",
            "processBotLootQueue",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;ILcom/rs2/model/Position;)Z",
            "pickupVisibleGroundItem",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "b",
            "(Lcom/rs2/model/player/Player;ILcom/rs2/model/Position;)Z",
            "pickupBotCombatGroundItem",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "b",
            "(Lcom/rs2/model/player/Player;)V",
            "restorePrimaryCombatGear",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "f",
            "(Lcom/rs2/model/player/Player;)V",
            "unequipMagicPenaltyGear",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "g",
            "(Lcom/rs2/model/player/Player;)V",
            "reequipMagicPenaltyGear",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;)Z",
            "hasRunesForSpell",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "h",
            "(Lcom/rs2/model/player/Player;)Z",
            "eatBotFood",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "i",
            "(Lcom/rs2/model/player/Player;)Z",
            "drinkStrengthPotion",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;I)Z",
            "hasPrayerLevelForCombatStyle",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "b",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;Z)V",
            "updateProtectionPrayerForStyle",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "j",
            "(Lcom/rs2/model/player/Player;)V",
            "toggleProtectionPrayerForOpponentStyle",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)Z",
            "updateBotDefensivePrayers",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "k",
            "(Lcom/rs2/model/player/Player;)V",
            "stopBotCombatTick",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "l",
            "(Lcom/rs2/model/player/Player;)V",
            "prepareBotPvpSearchPosition",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "b",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)Z",
            "isTargetLootWorthRisk",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;[I[IZ)I",
            "selectBotLoadoutItemId",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;[I[I)[I",
            "filterEquippableMemberLoadoutItems",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "b",
            "(Lcom/rs2/model/player/Player;[I[I)I",
            "selectBestBotLoadoutItemId",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "m",
            "(Lcom/rs2/model/player/Player;)V",
            "operateGloryTeleport",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "n",
            "(Lcom/rs2/model/player/Player;)I",
            "getEscapeCombatLevelMargin",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/magic/SpellDefinition;I)V",
            "grantBotSpellRunes",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;)V",
            "walkBotTowardPosition",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "o",
            "(Lcom/rs2/model/player/Player;)Z",
            "hasExternalCombatTarget",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;)V",
            "dropInventoryItem",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "c",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)Z",
            "tryHandleBotPvpTeamGrouping",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;Z)Z",
            "canTeamWithBotPvpPlayer",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "a",
            "()Z",
            "isFreeToPlayWorld",
        ),
        (
            "com/rs2/bot/combat/BotCombatHelper",
            "p",
            "(Lcom/rs2/model/player/Player;)Z",
            "drinkAntipoisonPotion",
        ),
    ]:
        METHOD_NAME_MAP.setdefault((owner, old_name, descriptor), mapped_name)

_BOT_COMBAT_LOADOUT_MANAGER_OWNER = "com/rs2/bot/combat/BotCombatLoadoutManager"
for _old_name, _descriptor, _mapped_name in [
    ("a", "()V", "initializeCombatLoadoutTypes"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "startCombatLoadoutBot"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "startGroupCombatBot"),
    ("a", "(Lcom/rs2/model/player/Player;Z)V", "selectCombatStyleFromStats"),
    ("c", "(Lcom/rs2/model/player/Player;)V", "equipRandomCape"),
    ("d", "(Lcom/rs2/model/player/Player;)V", "equipGlovesAndBoots"),
    ("h", "(Lcom/rs2/model/player/Player;)V", "addCombatSupplies"),
    ("i", "(Lcom/rs2/model/player/Player;)I", "selectSpecialAttackWeapon"),
    ("e", "(Lcom/rs2/model/player/Player;)V", "prepareMeleeLoadout"),
    ("b", "(Lcom/rs2/model/player/Player;Z)V", "prepareMeleeLoadout"),
    ("f", "(Lcom/rs2/model/player/Player;)V", "prepareRangedLoadout"),
    ("g", "(Lcom/rs2/model/player/Player;)V", "prepareMagicLoadout"),
    ("c", "(Lcom/rs2/model/player/Player;Z)V", "prepareCombatLoadout"),
]:
    METHOD_NAME_MAP.setdefault((_BOT_COMBAT_LOADOUT_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)

_BOT_COMBAT_ESCAPE_HANDLER_OWNER = "com/rs2/bot/combat/BotCombatEscapeHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;)Z", "tryStartBotCombatEscape"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "processBotCombatEscape"),
    ("c", "(Lcom/rs2/model/player/Player;)Z", "startBotCombatWalkingEscape"),
    ("d", "(Lcom/rs2/model/player/Player;)V", "walkFallbackEscapePath"),
]:
    METHOD_NAME_MAP.setdefault((_BOT_COMBAT_ESCAPE_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "[Lcom/rs2/util/RectangularArea;", "chaosTempleEscapeAreas"),
    ("b", "[Lcom/rs2/model/Position;", "chaosTempleEscapeWaypoints"),
    ("c", "[Lcom/rs2/util/RectangularArea;", "p2pGateSouthEscapeAreas"),
    ("d", "[Lcom/rs2/model/Position;", "p2pGateSouthEscapeWaypoints"),
    ("e", "[Lcom/rs2/util/RectangularArea;", "p2pGateSouthwestEscapeAreas"),
    ("f", "[Lcom/rs2/model/Position;", "p2pGateSouthwestEscapeWaypoints"),
    ("g", "[Lcom/rs2/util/RectangularArea;", "ruinsNorthEscapeAreas"),
    ("h", "[Lcom/rs2/model/Position;", "ruinsNorthEscapeWaypoints"),
    ("i", "[Lcom/rs2/util/RectangularArea;", "runeRockEscapeAreas"),
    ("j", "[Lcom/rs2/model/Position;", "runeRockEscapeWaypoints"),
]:
    FIELD_NAME_MAP.setdefault((_BOT_COMBAT_ESCAPE_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/bot/combat/BotCombatEscapeLogoutTask", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/bot/combat/BotGroundItemPickupTask", "a", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/bot/combat/BotGroundItemPickupTask", "b", "Lcom/rs2/model/Position;", "itemPosition"),
    ("com/rs2/bot/combat/BotGroundItemPickupTask", "c", "I", "itemId"),
    ("com/rs2/bot/combat/BotGroundItemPickupTask", "d", "Lcom/rs2/model/ground/GroundItem;", "groundItem"),
    ("com/rs2/bot/combat/BotPvpSelfTargetCombatTickTask", "a", "Lcom/rs2/model/player/Player;", "target"),
    ("com/rs2/bot/combat/BotPvpSelfTargetCombatTickTask", "b", "Lcom/rs2/model/player/Player;", "bot"),
    ("com/rs2/bot/combat/BotPvpOpponentTargetCombatTickTask", "a", "Lcom/rs2/model/player/Player;", "target"),
    ("com/rs2/bot/combat/BotPvpOpponentTargetCombatTickTask", "b", "Lcom/rs2/model/player/Player;", "bot"),
    ("com/rs2/bot/combat/BotPvpTargetSearchTickTask", "a", "Lcom/rs2/model/player/Player;", "bot"),
    ("com/rs2/bot/combat/BotPvpTargetSearchTickTask", "b", "Z", "ignoreLootRiskCheck"),
    ("com/rs2/bot/combat/BotGroupCombatTickTask", "a", "Lcom/rs2/model/player/Player;", "bot"),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_BOT_TASK_PLANNER_OWNER = "com/rs2/bot/BotTaskPlanner"
FIELD_NAME_MAP.setdefault(
    (_BOT_TASK_PLANNER_OWNER, "a", "[[I"),
    "skillTargetLevelMilestones",
)
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;)V", "startInitialProgressiveBotTask"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;II)Lcom/rs2/bot/BotTaskDefinition;",
        "selectShopPurchaseTask",
    ),
    ("b", "(Lcom/rs2/model/player/Player;)V", "resetBotTaskGoals"),
    ("c", "(Lcom/rs2/model/player/Player;)V", "configureCurrentBotTaskGoals"),
    ("d", "(Lcom/rs2/model/player/Player;)V", "populateBotShopSellItemIds"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;I[IIZ)Z",
        "tryDeferForUpgradePurchase",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;I[IIZZ)Z",
        "tryDeferForUpgradePurchase",
    ),
    ("e", "(Lcom/rs2/model/player/Player;)V", "prepareDeferredUpgradePurchase"),
    ("f", "(Lcom/rs2/model/player/Player;)Z", "prepareCookingTaskRequirements"),
    ("g", "(Lcom/rs2/model/player/Player;)Z", "prepareSpinningTaskRequirements"),
    ("a", "(Lcom/rs2/model/player/Player;Z)Z", "prepareTanningTaskRequirements"),
    ("h", "(Lcom/rs2/model/player/Player;)Z", "prepareLeatherCraftingTaskRequirements"),
    ("i", "(Lcom/rs2/model/player/Player;)Z", "hasSmeltingTaskMaterial"),
    ("j", "(Lcom/rs2/model/player/Player;)Z", "prepareSmithingTaskRequirements"),
    ("k", "(Lcom/rs2/model/player/Player;)V", "selectMeleeTrainingFightMode"),
    ("b", "(Lcom/rs2/model/player/Player;Z)V", "prepareBotCombatLoadoutLists"),
]:
    METHOD_NAME_MAP.setdefault((_BOT_TASK_PLANNER_OWNER, _old_name, _descriptor), _mapped_name)

_LOG_FLETCHING_ACTION_OWNERS = [
    "com/rs2/model/skill/fletching/logs/AcheyLogFletchingAction",
    "com/rs2/model/skill/fletching/logs/NormalLogFletchingAction",
    "com/rs2/model/skill/fletching/logs/MagicLogFletchingAction",
    "com/rs2/model/skill/fletching/logs/MapleLogFletchingAction",
    "com/rs2/model/skill/fletching/logs/OakLogFletchingAction",
    "com/rs2/model/skill/fletching/logs/WillowLogFletchingAction",
    "com/rs2/model/skill/fletching/logs/YewLogFletchingAction",
]

METHOD_NAME_MAP.setdefault(("com/rs2/model/skill/fletching/logs/LogFletchingAction", "a", "()V"), "start")
for _owner in _LOG_FLETCHING_ACTION_OWNERS:
    _action_class = _owner.rsplit("/", 1)[1]
    METHOD_NAME_MAP.setdefault(
        (_owner, "a", f"(Lcom/rs2/model/player/Player;II)Lcom/rs2/model/skill/fletching/logs/{_action_class};"),
        "create",
    )
    METHOD_NAME_MAP.setdefault((_owner, "a", "()V"), "start")

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "logItemId"),
    ("c", "I", "productItemId"),
    ("g", "I", "requiredLevel"),
    ("d", "D", "experience"),
    ("e", "I", "menuQuantity"),
    ("f", "I", "requestedQuantity"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/logs/LogFletchingAction", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/skill/fletching/logs/LogFletchingAction;", "action"),
    ("c", "I", "actionSequence"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/logs/LogFletchingTask", _old_name, _descriptor),
        _mapped_name,
    )

_LOG_FLETCHING_RECIPE_FIELD_LAYOUTS = [
    (
        "com/rs2/model/skill/fletching/logs/NormalLogFletchingRecipe",
        [
            ("m", "I", "buttonId"),
            ("n", "I", "logItemId"),
            ("o", "I", "productItemId"),
            ("p", "I", "menuQuantity"),
            ("q", "I", "requiredLevel"),
            ("r", "D", "experience"),
            ("s", "Ljava/util/HashMap;", "recipesByButtonId"),
            ("t", "[Lcom/rs2/model/skill/fletching/logs/NormalLogFletchingRecipe;", "VALUES"),
        ],
    ),
    (
        "com/rs2/model/skill/fletching/logs/AcheyLogFletchingRecipe",
        [
            ("e", "I", "buttonId"),
            ("f", "I", "logItemId"),
            ("g", "I", "productItemId"),
            ("h", "I", "menuQuantity"),
            ("i", "I", "requiredLevel"),
            ("j", "D", "experience"),
            ("k", "Ljava/util/HashMap;", "recipesByButtonId"),
            ("l", "[Lcom/rs2/model/skill/fletching/logs/AcheyLogFletchingRecipe;", "VALUES"),
        ],
    ),
]

for _recipe_class in [
    "MagicLogFletchingRecipe",
    "MapleLogFletchingRecipe",
    "OakLogFletchingRecipe",
    "WillowLogFletchingRecipe",
    "YewLogFletchingRecipe",
]:
    _owner = f"com/rs2/model/skill/fletching/logs/{_recipe_class}"
    _LOG_FLETCHING_RECIPE_FIELD_LAYOUTS.append(
        (
            _owner,
            [
                ("i", "I", "buttonId"),
                ("j", "I", "logItemId"),
                ("k", "I", "productItemId"),
                ("l", "I", "menuQuantity"),
                ("m", "I", "requiredLevel"),
                ("n", "D", "experience"),
                ("o", "Ljava/util/HashMap;", "recipesByButtonId"),
                ("p", f"[L{_owner};", "VALUES"),
            ],
        )
    )

for _owner, _fields in _LOG_FLETCHING_RECIPE_FIELD_LAYOUTS:
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _old_name, _mapped_name in [
    ("a", "id"),
    ("b", "height"),
    ("c", "delay"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/animation/GraphicEffect", _old_name, "I"), _mapped_name)

for _owner, _fields in _LOG_FLETCHING_RECIPE_FIELD_LAYOUTS:
    METHOD_NAME_MAP.setdefault((_owner, "a", f"(I)L{_owner};"), "forButtonId")
    for _old_name, _descriptor, _mapped_name in [
        ("a", "()I", "getLogItemId"),
        ("b", "()I", "getProductItemId"),
        ("c", "()I", "getMenuQuantity"),
        ("d", "()I", "getRequiredLevel"),
        ("e", "()D", "getExperience"),
    ]:
        METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/animation/GraphicEffect", "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleAmmunitionFletching",
)
METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/player/PlayerGroup", "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleBowStringing",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()Z", "isFull"),
    ("b", "()V", "refreshGroupFollowChain"),
    ("a", "(Lcom/rs2/model/player/Player;)Z", "containsMember"),
    ("a", "(Lcom/rs2/model/Entity;)V", "attackTarget"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "showMemberList"),
    ("c", "()I", "getHighestCombatLevel"),
    ("d", "()I", "getTotalCombatLevel"),
    ("c", "(Lcom/rs2/model/player/Player;)V", "addMember"),
    ("d", "(Lcom/rs2/model/player/Player;)V", "finishDeferredRemoval"),
    ("e", "(Lcom/rs2/model/player/Player;)V", "removeMember"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/player/Player;)V",
        "handleGroupInvite",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;)Lcom/rs2/model/Entity;",
        "selectLootRecipient",
    ),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/player/PlayerGroup", _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "leader"),
    ("b", "Ljava/util/concurrent/CopyOnWriteArrayList;", "members"),
    ("c", "Ljava/util/concurrent/CopyOnWriteArrayList;", "deferredRemovedMembers"),
    ("d", "Ljava/lang/Boolean;", "rollLootEnabled"),
]:
    FIELD_NAME_MAP.setdefault(("com/rs2/model/player/PlayerGroup", _old_name, _descriptor), _mapped_name)

for _owner, _old_name, _descriptor, _mapped_name in [
    (
        "com/rs2/model/item/consumable/FoodDefinition",
        "a",
        "(I)Lcom/rs2/model/item/consumable/FoodDefinition;",
        "forItemId",
    ),
    ("com/rs2/model/item/consumable/FoodDefinition", "a", "()I", "getHealAmount"),
    ("com/rs2/model/item/consumable/FoodDefinition", "b", "()I", "getReplacementItemId"),
    ("com/rs2/model/item/consumable/FoodDefinition", "c", "()[I", "getItemIds"),
    ("com/rs2/model/item/consumable/FoodHandler", "a", "(II)Z", "eatFood"),
    ("com/rs2/model/item/consumable/FoodHandler", "a", "()V", "loadPotionDefinitions"),
    ("com/rs2/model/item/consumable/PotionDefinition", "a", "()Z", "isAntipoison"),
    ("com/rs2/model/item/consumable/PotionDefinition", "b", "()Ljava/lang/String;", "getName"),
    ("com/rs2/model/item/consumable/PotionDefinition", "c", "()[I", "getDoseItemIds"),
    ("com/rs2/model/item/consumable/PotionDefinition", "d", "()[I", "getSkillIds"),
    ("com/rs2/model/item/consumable/PotionDefinition", "e", "()[I", "getFlatBoosts"),
    ("com/rs2/model/item/consumable/PotionDefinition", "f", "()[D", "getPercentBoosts"),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "g",
        "()Lcom/rs2/model/item/consumable/PotionEffectMode;",
        "getEffectMode",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "a",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;Lcom/rs2/model/item/consumable/PotionEffectMode;)V",
        "setEffectMode",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "a",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;[I)V",
        "setDoseItemIds",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "a",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;Z)V",
        "setAntipoison",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "a",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;)[I",
        "getMutableDoseItemIds",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "a",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;Ljava/lang/String;)V",
        "setName",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "b",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;[I)V",
        "setSkillIds",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "c",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;[I)V",
        "setFlatBoosts",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "a",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;[D)V",
        "setPercentBoosts",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "b",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;)[I",
        "getMutableSkillIds",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "c",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;)[I",
        "getMutableFlatBoosts",
    ),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "d",
        "(Lcom/rs2/model/item/consumable/PotionDefinition;)[D",
        "getMutablePercentBoosts",
    ),
    ("com/rs2/model/item/consumable/PotionHandler", "a", "(I)Z", "selectPotionForItemId"),
    ("com/rs2/model/item/consumable/PotionHandler", "a", "(II)V", "drinkPotion"),
    (
        "com/rs2/model/item/consumable/PotionHandler",
        "a",
        "()[Lcom/rs2/model/item/consumable/PotionDefinition;",
        "getDefinitions",
    ),
]:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _owner, _old_name, _descriptor, _mapped_name in [
    ("com/rs2/model/item/consumable/FoodDefinition", "aj", "Ljava/util/Map;", "definitionsByItemId"),
    ("com/rs2/model/item/consumable/FoodDefinition", "ak", "[I", "itemIds"),
    ("com/rs2/model/item/consumable/FoodDefinition", "al", "I", "healAmount"),
    ("com/rs2/model/item/consumable/FoodDefinition", "am", "I", "replacementItemId"),
    ("com/rs2/model/item/consumable/FoodHandler", "a", "Lcom/rs2/model/player/Player;", "player"),
    (
        "com/rs2/model/item/consumable/FoodHealMessageEvent",
        "a",
        "Lcom/rs2/model/item/consumable/FoodHandler;",
        "foodHandler",
    ),
    ("com/rs2/model/item/consumable/PotionDefinition", "a", "Z", "antipoison"),
    ("com/rs2/model/item/consumable/PotionDefinition", "b", "Ljava/lang/String;", "name"),
    (
        "com/rs2/model/item/consumable/PotionDefinition",
        "c",
        "Lcom/rs2/model/item/consumable/PotionEffectMode;",
        "effectMode",
    ),
    ("com/rs2/model/item/consumable/PotionDefinition", "d", "[I", "doseItemIds"),
    ("com/rs2/model/item/consumable/PotionDefinition", "e", "[I", "skillIds"),
    ("com/rs2/model/item/consumable/PotionDefinition", "f", "[I", "flatBoosts"),
    ("com/rs2/model/item/consumable/PotionDefinition", "g", "[D", "percentBoosts"),
    (
        "com/rs2/model/item/consumable/PotionEffectMode",
        "a",
        "Lcom/rs2/model/item/consumable/PotionEffectMode;",
        "BOOST",
    ),
    (
        "com/rs2/model/item/consumable/PotionEffectMode",
        "b",
        "Lcom/rs2/model/item/consumable/PotionEffectMode;",
        "RESTORE",
    ),
    (
        "com/rs2/model/item/consumable/PotionHandler",
        "a",
        "[Lcom/rs2/model/item/consumable/PotionDefinition;",
        "definitions",
    ),
    ("com/rs2/model/item/consumable/PotionHandler", "b", "I", "definitionCount"),
    ("com/rs2/model/item/consumable/PotionHandler", "c", "I", "selectedDefinitionIndex"),
    ("com/rs2/model/item/consumable/PotionHandler", "d", "Lcom/rs2/model/player/Player;", "player"),
    ("com/rs2/model/item/consumable/PotionHandler", "e", "I", "selectedDoseIndex"),
]:
    FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_FLETCHING_ITEM_COMBINATION_METHODS = [
    (
        "com/rs2/model/skill/fletching/AmmunitionFletchingDefinition",
        "a",
        "(II)Lcom/rs2/model/skill/fletching/AmmunitionFletchingDefinition;",
        "forComponents",
    ),
    (
        "com/rs2/model/skill/fletching/BowStringingDefinition",
        "a",
        "(II)Lcom/rs2/model/skill/fletching/BowStringingDefinition;",
        "forComponents",
    ),
    (
        "com/rs2/model/skill/fletching/GemBoltTipDefinition",
        "a",
        "(I)Lcom/rs2/model/skill/fletching/GemBoltTipDefinition;",
        "forGemItemId",
    ),
]

for _owner, _old_name, _descriptor, _mapped_name in _FLETCHING_ITEM_COMBINATION_METHODS:
    METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getComponentItemId"),
    ("b", "()I", "getBaseItemId"),
    ("c", "()I", "getProductItemId"),
    ("d", "()I", "getRequiredLevel"),
    ("e", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/AmmunitionFletchingDefinition", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getUnstrungBowItemId"),
    ("b", "()I", "getBowStringItemId"),
    ("c", "()I", "getStrungBowItemId"),
    ("d", "()I", "getRequiredLevel"),
    ("e", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/BowStringingDefinition", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getGemItemId"),
    ("b", "()I", "getBoltTipItemId"),
    ("c", "()I", "getBoltTipAmount"),
    ("d", "()I", "getRequiredLevel"),
    ("e", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/GemBoltTipDefinition", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "actionSequence"),
    ("c", "Lcom/rs2/model/skill/fletching/AmmunitionFletchingDefinition;", "definition"),
    ("d", "I", "consumedAmount"),
    ("e", "I", "productDivisor"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/AmmunitionFletchingTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "actionSequence"),
    ("c", "Lcom/rs2/model/skill/fletching/BowStringingDefinition;", "definition"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/BowStringingTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("w", "I", "componentItemId"),
    ("x", "I", "baseItemId"),
    ("y", "I", "productItemId"),
    ("z", "I", "requiredLevel"),
    ("A", "D", "experience"),
    ("B", "[Lcom/rs2/model/skill/fletching/AmmunitionFletchingDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/AmmunitionFletchingDefinition", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("n", "I", "unstrungBowItemId"),
    ("o", "I", "bowStringItemId"),
    ("p", "I", "strungBowItemId"),
    ("q", "I", "requiredLevel"),
    ("r", "D", "experience"),
    ("s", "[Lcom/rs2/model/skill/fletching/BowStringingDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/BowStringingDefinition", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("d", "I", "gemItemId"),
    ("e", "I", "boltTipItemId"),
    ("f", "I", "boltTipAmount"),
    ("g", "I", "requiredLevel"),
    ("h", "D", "experience"),
    ("i", "[Lcom/rs2/model/skill/fletching/GemBoltTipDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/fletching/GemBoltTipDefinition", _old_name, _descriptor),
        _mapped_name,
    )

_COOKABLE_FOOD_OWNER = "com/rs2/model/skill/cooking/CookableFoodDefinition"
METHOD_NAME_MAP.setdefault(
    (_COOKABLE_FOOD_OWNER, "a", "(I)Lcom/rs2/model/skill/cooking/CookableFoodDefinition;"),
    "forRawItemId",
)
for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getCookedItemId"),
    ("b", "()I", "getBurntItemId"),
    ("c", "()I", "getRequiredLevel"),
    ("d", "()D", "getExperience"),
    ("e", "()Z", "canCookOnFire"),
    ("f", "()I", "getSuccessChanceLow"),
    ("g", "()I", "getSuccessChanceHigh"),
]:
    METHOD_NAME_MAP.setdefault((_COOKABLE_FOOD_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("ag", "I", "rawItemId"),
    ("ah", "I", "cookedItemId"),
    ("ai", "I", "burntItemId"),
    ("aj", "I", "requiredLevel"),
    ("ak", "D", "experience"),
    ("al", "Z", "cookableOnFire"),
    ("am", "I", "successChanceLow"),
    ("an", "I", "successChanceHigh"),
    ("ao", "Ljava/util/Map;", "definitionsByRawItemId"),
    ("ap", "[Lcom/rs2/model/skill/cooking/CookableFoodDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_COOKABLE_FOOD_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(IIII)Z", "handleItemOnCookingObject"),
    ("a", "(Lcom/rs2/model/player/Player;I)V", "startCookingTask"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "cookSelectedItem"),
    ("a", "(Lcom/rs2/model/player/Player;IZ)V", "processCookingResult"),
    ("b", "(Lcom/rs2/model/player/Player;I)Z", "handleCookingButton"),
    ("a", "(II)Z", "handleWaterSourceItem"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/cooking/CookingManager", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/Position;", "firePosition"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "[I", "waterSourceObjectIds"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/cooking/CookingManager", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "actionSequence"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/cooking/CookingTask", _old_name, _descriptor),
        _mapped_name,
    )

_DAIRY_CHURN_RECIPE_OWNER = "com/rs2/model/skill/cooking/DairyChurnRecipe"
METHOD_NAME_MAP.setdefault(
    (_DAIRY_CHURN_RECIPE_OWNER, "a", "(I)Lcom/rs2/model/skill/cooking/DairyChurnRecipe;"),
    "forButtonId",
)
for _old_name, _descriptor, _mapped_name in [
    ("a", "()[I", "getIngredientItemIds"),
    ("b", "()I", "getProductItemId"),
    ("c", "()I", "getRequiredLevel"),
    ("d", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_DAIRY_CHURN_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/cooking/DairyChurnHandler", "a", "(Lcom/rs2/model/player/Player;I)Z"),
    "handleButtonClick",
)

for _old_name, _mapped_name in [
    ("a", "CREAM"),
    ("b", "BUTTER"),
    ("c", "CHEESE"),
]:
    FIELD_NAME_MAP.setdefault(
        (_DAIRY_CHURN_RECIPE_OWNER, _old_name, "Lcom/rs2/model/skill/cooking/DairyChurnRecipe;"),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("d", "I", "buttonId"),
    ("e", "[I", "ingredientItemIds"),
    ("f", "I", "productItemId"),
    ("g", "I", "requiredLevel"),
    ("h", "D", "experience"),
    ("i", "Ljava/util/HashMap;", "recipesByButtonId"),
    ("j", "[Lcom/rs2/model/skill/cooking/DairyChurnRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_DAIRY_CHURN_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "actionSequence"),
    ("c", "Lcom/rs2/model/skill/cooking/DairyChurnRecipe;", "recipe"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/cooking/DairyChurnTask", _old_name, _descriptor),
        _mapped_name,
    )

_FOOD_PREPARATION_RECIPE_OWNER = "com/rs2/model/skill/cooking/FoodPreparationRecipe"
METHOD_NAME_MAP.setdefault(
    (_FOOD_PREPARATION_RECIPE_OWNER, "a", "(II)Lcom/rs2/model/skill/cooking/FoodPreparationRecipe;"),
    "forIngredients",
)
for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getIngredientItemId"),
    ("b", "()I", "getIngredientAmount"),
    ("c", "()I", "getBaseItemId"),
    ("d", "()I", "getProductItemId"),
    ("e", "()I", "getRequiredLevel"),
    ("f", "()D", "getExperience"),
    ("g", "()Z", "usesPutIntoMessage"),
    ("h", "()I", "getReturnedItemId"),
]:
    METHOD_NAME_MAP.setdefault((_FOOD_PREPARATION_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("H", "I", "ingredientItemId"),
    ("I", "I", "ingredientAmount"),
    ("J", "I", "baseItemId"),
    ("K", "I", "productItemId"),
    ("L", "I", "requiredLevel"),
    ("M", "D", "experience"),
    ("N", "Z", "putIntoMessage"),
    ("O", "I", "returnedItemId"),
    ("P", "[Lcom/rs2/model/skill/cooking/FoodPreparationRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_FOOD_PREPARATION_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

_MULTI_INGREDIENT_FOOD_RECIPE_OWNER = "com/rs2/model/skill/cooking/MultiIngredientFoodRecipe"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/cooking/MultiIngredientFoodRecipe;", "forFirstIngredientItemId"),
    ("b", "(I)Lcom/rs2/model/skill/cooking/MultiIngredientFoodRecipe;", "forFirstStageProductItemId"),
    ("a", "()I", "getFirstIngredientItemId"),
    ("b", "()I", "getSecondIngredientItemId"),
    ("c", "()I", "getFirstStageProductItemId"),
    ("d", "()I", "getBaseItemId"),
    ("e", "()I", "getFinalProductItemId"),
    ("f", "()I", "getRequiredLevel"),
    ("g", "()D", "getExperience"),
    ("h", "()Z", "usesPutIntoMessage"),
    ("i", "()I", "getFirstStageReturnedItemId"),
    ("j", "()I", "getFinalStageReturnedItemId"),
]:
    METHOD_NAME_MAP.setdefault((_MULTI_INGREDIENT_FOOD_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("h", "I", "firstIngredientItemId"),
    ("i", "I", "secondIngredientItemId"),
    ("j", "I", "baseItemId"),
    ("k", "I", "firstStageProductItemId"),
    ("l", "I", "finalProductItemId"),
    ("m", "I", "requiredLevel"),
    ("n", "D", "experience"),
    ("o", "Z", "putIntoMessage"),
    ("p", "I", "firstStageReturnedItemId"),
    ("q", "I", "finalStageReturnedItemId"),
    ("r", "Ljava/util/HashMap;", "recipesByFirstIngredientItemId"),
    ("s", "Ljava/util/HashMap;", "recipesByFirstStageProductItemId"),
    ("t", "[Lcom/rs2/model/skill/cooking/MultiIngredientFoodRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_MULTI_INGREDIENT_FOOD_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

_PIE_RECIPE_OWNER = "com/rs2/model/skill/cooking/PieRecipe"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/cooking/PieRecipe;", "forFirstIngredientItemId"),
    ("b", "(I)Lcom/rs2/model/skill/cooking/PieRecipe;", "forFirstStagePieItemId"),
    ("c", "(I)Lcom/rs2/model/skill/cooking/PieRecipe;", "forSecondStagePieItemId"),
    ("a", "()I", "getFirstIngredientItemId"),
    ("b", "()I", "getSecondIngredientItemId"),
    ("c", "()I", "getThirdIngredientItemId"),
    ("d", "()I", "getFirstStagePieItemId"),
    ("e", "()I", "getSecondStagePieItemId"),
    ("f", "()I", "getPieShellItemId"),
    ("g", "()I", "getRawPieItemId"),
    ("h", "()I", "getRequiredLevel"),
    ("i", "()D", "getExperience"),
    ("j", "()Z", "usesPutIntoMessage"),
    ("k", "()I", "getFirstStageReturnedItemId"),
    ("l", "()I", "getSecondStageReturnedItemId"),
    ("m", "()I", "getThirdStageReturnedItemId"),
]:
    METHOD_NAME_MAP.setdefault((_PIE_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("g", "I", "firstIngredientItemId"),
    ("h", "I", "secondIngredientItemId"),
    ("i", "I", "thirdIngredientItemId"),
    ("j", "I", "pieShellItemId"),
    ("k", "I", "firstStagePieItemId"),
    ("l", "I", "secondStagePieItemId"),
    ("m", "I", "rawPieItemId"),
    ("n", "I", "requiredLevel"),
    ("o", "D", "experience"),
    ("p", "Z", "putIntoMessage"),
    ("q", "I", "firstStageReturnedItemId"),
    ("r", "I", "secondStageReturnedItemId"),
    ("s", "I", "thirdStageReturnedItemId"),
    ("t", "Ljava/util/HashMap;", "recipesByFirstIngredientItemId"),
    ("u", "Ljava/util/HashMap;", "recipesByFirstStagePieItemId"),
    ("v", "Ljava/util/HashMap;", "recipesBySecondStagePieItemId"),
    ("w", "[Lcom/rs2/model/skill/cooking/PieRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_PIE_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

_FIREMAKING_LOG_OWNER = "com/rs2/model/skill/firemaking/FiremakingLog"
for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getLogItemId"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()I", "getExperience"),
    ("d", "()I", "getFireObjectId"),
]:
    METHOD_NAME_MAP.setdefault((_FIREMAKING_LOG_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _mapped_name in [
    ("a", "NORMAL_LOGS"),
    ("b", "BLUE_LOGS"),
    ("c", "GREEN_LOGS"),
    ("d", "RED_LOGS"),
    ("e", "ACHEY_LOGS"),
    ("f", "OAK_LOGS"),
    ("g", "WILLOW_LOGS"),
    ("h", "TEAK_LOGS"),
    ("i", "MAPLE_LOGS"),
    ("j", "ARCTIC_PINE_LOGS"),
    ("k", "MAHOGANY_LOGS"),
    ("l", "YEW_LOGS"),
    ("m", "MAGIC_LOGS"),
    ("n", "PYRE_LOGS"),
    ("o", "OAK_PYRE_LOGS"),
    ("p", "WILLOW_PYRE_LOGS"),
    ("q", "MAPLE_PYRE_LOGS"),
    ("r", "YEW_PYRE_LOGS"),
    ("s", "MAGIC_PYRE_LOGS"),
]:
    FIELD_NAME_MAP.setdefault(
        (_FIREMAKING_LOG_OWNER, _old_name, "Lcom/rs2/model/skill/firemaking/FiremakingLog;"),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("t", "I", "logItemId"),
    ("u", "I", "requiredLevel"),
    ("v", "I", "experience"),
    ("w", "I", "fireObjectId"),
    ("x", "[Lcom/rs2/model/skill/firemaking/FiremakingLog;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_FIREMAKING_LOG_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(IIZIII)V", "startFiremaking"),
    ("a", "(I)Z", "isFireObjectId"),
]:
    METHOD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/firemaking/FiremakingHandler", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "[I", "fireObjectIds"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/firemaking/FiremakingHandler", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "actionSequence"),
    ("c", "Lcom/rs2/model/ground/GroundItem;", "groundItem"),
    ("d", "I", "logItemId"),
    ("e", "Lcom/rs2/model/skill/firemaking/FiremakingLog;", "firemakingLog"),
    ("f", "I", "fireX"),
    ("g", "I", "fireY"),
    ("h", "I", "plane"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/firemaking/FiremakingTask", _old_name, _descriptor),
        _mapped_name,
    )

_FISHING_SPOT_DEFINITION_OWNER = "com/rs2/model/skill/fishing/FishingSpotDefinition"
for _old_name, _mapped_name in [
    ("a", "SMALL_NET"),
    ("b", "BAIT"),
    ("c", "FLY_FISHING"),
    ("d", "RIVER_BAIT"),
    ("e", "LOBSTER_POT"),
    ("f", "HARPOON"),
    ("g", "BIG_NET"),
    ("h", "SHARK_HARPOON"),
    ("i", "LAVA_EEL"),
    ("j", "MONKFISH"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _FISHING_SPOT_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/fishing/FishingSpotDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("k", "[I", "spotNpcIds"),
    ("l", "Lcom/rs2/model/item/ItemStack;", "toolItem"),
    ("m", "[I", "requiredLevels"),
    ("n", "[I", "chanceLowValues"),
    ("o", "[I", "chanceHighValues"),
    ("p", "[Lcom/rs2/model/item/ItemStack;", "catchItems"),
    ("q", "[D", "experienceRewards"),
    ("r", "I", "animationId"),
    ("s", "Lcom/rs2/model/item/ItemStack;", "baitItem"),
    ("t", "[Lcom/rs2/model/skill/fishing/FishingSpotDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_FISHING_SPOT_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()Lcom/rs2/model/item/ItemStack;", "getBaitItem"),
    ("b", "()[Lcom/rs2/model/item/ItemStack;", "getCatchItems"),
    ("c", "()I", "getAnimationId"),
    ("d", "()[I", "getRequiredLevels"),
    ("e", "()[I", "getChanceLowValues"),
    ("f", "()[I", "getChanceHighValues"),
    ("g", "()[D", "getExperienceRewards"),
    ("h", "()Lcom/rs2/model/item/ItemStack;", "getToolItem"),
    ("i", "()[I", "getSpotNpcIds"),
    ("a", "(II)Lcom/rs2/model/skill/fishing/FishingSpotDefinition;", "forNpcIdAndOption"),
]:
    METHOD_NAME_MAP.setdefault((_FISHING_SPOT_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_FISHING_HANDLER_OWNER = "com/rs2/model/skill/fishing/FishingHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/npc/Npc;I)Z", "handleFishingSpot"),
    ("a", "(Lcom/rs2/model/skill/fishing/FishingHandler;)Lcom/rs2/model/player/Player;", "getPlayer"),
]:
    METHOD_NAME_MAP.setdefault((_FISHING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(
    (_FISHING_HANDLER_OWNER, "a", "Lcom/rs2/model/player/Player;"),
    "player",
)

_FISHING_WHIRLPOOL_OWNER = "com/rs2/model/skill/fishing/FishingWhirlpool"
for _old_name, _mapped_name in [
    ("a", "RIVER_WHIRLPOOL"),
    ("b", "NET_BAIT_WHIRLPOOL"),
    ("c", "CAGE_HARPOON_WHIRLPOOL"),
    ("d", "BIG_NET_HARPOON_WHIRLPOOL"),
    ("e", "MONKFISH_WHIRLPOOL"),
]:
    FIELD_NAME_MAP.setdefault(
        (_FISHING_WHIRLPOOL_OWNER, _old_name, "Lcom/rs2/model/skill/fishing/FishingWhirlpool;"),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("f", "I", "whirlpoolNpcId"),
    ("g", "[I", "sourceNpcIds"),
    ("h", "[Lcom/rs2/model/skill/fishing/FishingWhirlpool;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_FISHING_WHIRLPOOL_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()[I", "getSourceNpcIds"),
    ("b", "()I", "getWhirlpoolNpcId"),
    ("a", "(I)Lcom/rs2/model/skill/fishing/FishingWhirlpool;", "forSourceNpcId"),
    ("b", "(I)Lcom/rs2/model/skill/fishing/FishingWhirlpool;", "forWhirlpoolNpcId"),
]:
    METHOD_NAME_MAP.setdefault((_FISHING_WHIRLPOOL_OWNER, _old_name, _descriptor), _mapped_name)

_FISHING_SPOT_MANAGER_OWNER = "com/rs2/model/skill/fishing/FishingSpotManager"
for _old_name, _descriptor, _mapped_name in [
    ("a", "()V", "spawnFishingSpots"),
    (
        "a",
        "(Lcom/rs2/model/Position;Lcom/rs2/model/skill/fishing/FishingSpotDefinition;)Z",
        "isSpotAtPosition",
    ),
]:
    METHOD_NAME_MAP.setdefault((_FISHING_SPOT_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Ljava/util/Map;", "activeSpotsByPosition"),
    ("b", "[[Lcom/rs2/model/Position;", "spotPositions"),
    ("c", "[[I", "spotNpcIdsByLocation"),
]:
    FIELD_NAME_MAP.setdefault((_FISHING_SPOT_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/player/Player", "cj", "I"),
    "gatheringHazardCounter",
)

_GEM_DEFINITION_OWNER = "com/rs2/model/skill/crafting/GemDefinition"
for _old_name, _mapped_name in [
    ("a", "SAPPHIRE"),
    ("b", "EMERALD"),
    ("c", "RUBY"),
    ("d", "DIAMOND"),
    ("e", "DRAGONSTONE"),
    ("f", "ONYX"),
    ("g", "OPAL"),
    ("h", "JADE"),
    ("i", "RED_TOPAZ"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _GEM_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/GemDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("j", "S", "uncutItemId"),
    ("k", "S", "cutItemId"),
    ("l", "B", "requiredLevel"),
    ("m", "S", "animationId"),
    ("n", "D", "experience"),
    ("o", "Ljava/util/HashMap;", "definitionsByUncutItemId"),
    ("p", "[Lcom/rs2/model/skill/crafting/GemDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_GEM_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/GemDefinition;", "forUncutItemId"),
    ("a", "()I", "getCutItemId"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()I", "getAnimationId"),
    ("d", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_GEM_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_GEM_CUTTING_HANDLER_OWNER = "com/rs2/model/skill/crafting/GemCuttingHandler"
FIELD_NAME_MAP.setdefault((_GEM_CUTTING_HANDLER_OWNER, "a", "I"), "CHISEL_ITEM_ID")
METHOD_NAME_MAP.setdefault(
    (_GEM_CUTTING_HANDLER_OWNER, "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleGemCutting",
)

_BATTLESTAFF_RECIPE_OWNER = "com/rs2/model/skill/crafting/BattlestaffRecipe"
for _old_name, _mapped_name in [
    ("a", "AIR"),
    ("b", "WATER"),
    ("c", "EARTH"),
    ("d", "FIRE"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _BATTLESTAFF_RECIPE_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/BattlestaffRecipe;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("e", "I", "orbItemId"),
    ("f", "I", "battlestaffItemId"),
    ("g", "B", "requiredLevel"),
    ("h", "D", "experience"),
    ("i", "Ljava/util/HashMap;", "definitionsByOrbItemId"),
    ("j", "[Lcom/rs2/model/skill/crafting/BattlestaffRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_BATTLESTAFF_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/BattlestaffRecipe;", "forOrbItemId"),
    ("a", "()I", "getOrbItemId"),
    ("b", "()I", "getBattlestaffItemId"),
    ("c", "()I", "getRequiredLevel"),
    ("d", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_BATTLESTAFF_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

_BATTLESTAFF_CRAFTING_HANDLER_OWNER = "com/rs2/model/skill/crafting/BattlestaffCraftingHandler"
FIELD_NAME_MAP.setdefault((_BATTLESTAFF_CRAFTING_HANDLER_OWNER, "a", "I"), "BASE_BATTLESTAFF_ITEM_ID")
METHOD_NAME_MAP.setdefault(
    (_BATTLESTAFF_CRAFTING_HANDLER_OWNER, "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleBattlestaffCrafting",
)

_SPINNING_RECIPE_OWNER = "com/rs2/model/skill/crafting/SpinningRecipe"
for _old_name, _mapped_name in [
    ("a", "WOOL"),
    ("b", "WOOL5"),
    ("c", "WOOL10"),
    ("d", "WOOLX"),
    ("e", "FLAX"),
    ("f", "FLAX5"),
    ("g", "FLAX10"),
    ("h", "FLAXX"),
    ("i", "ROOT"),
    ("j", "ROOT5"),
    ("k", "ROOT10"),
    ("l", "ROOTX"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _SPINNING_RECIPE_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/SpinningRecipe;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("m", "I", "buttonId"),
    ("n", "I", "ingredientItemId"),
    ("o", "I", "productItemId"),
    ("p", "I", "quantity"),
    ("q", "I", "requiredLevel"),
    ("r", "D", "experience"),
    ("s", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("t", "[Lcom/rs2/model/skill/crafting/SpinningRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_SPINNING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/SpinningRecipe;", "forButtonId"),
    ("a", "()I", "getIngredientItemId"),
    ("b", "()I", "getProductItemId"),
    ("c", "()I", "getQuantity"),
    ("d", "()I", "getRequiredLevel"),
    ("e", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_SPINNING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

_CRAFTING_HANDLER_OWNER = "com/rs2/model/skill/crafting/CraftingHandler"
for _old_name, _descriptor, _mapped_name in [
    ("e", "I", "mapIndexCapacity"),
    ("a", "I", "mapIndexEntryCount"),
    ("b", "[I", "regionIds"),
    ("c", "[I", "mapArchiveIds"),
    ("d", "[I", "landscapeArchiveIds"),
]:
    FIELD_NAME_MAP.setdefault((_CRAFTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()V", "loadMapIndex"),
    ("a", "(Lcom/rs2/model/player/Player;)Z", "startBotSpinningTask"),
    ("b", "(Lcom/rs2/model/player/Player;I)Z", "startSpinningAtWheel"),
    ("e", "(Lcom/rs2/model/player/Player;II)Z", "handleSpinningButton"),
]:
    METHOD_NAME_MAP.setdefault((_CRAFTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

_WORLD_MAP_PANEL_OWNER = "com/rs2/launcher/WorldMapPanel"
for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "mapScale"),
    ("b", "I", "mapWidthPixels"),
    ("c", "I", "mapHeightPixels"),
    ("e", "Lcom/rs2/model/skill/crafting/CraftingHandler;", "mapIndex"),
    ("f", "Ljava/awt/Toolkit;", "toolkit"),
    ("g", "[[Ljava/awt/Image;", "regionImages"),
    ("h", "[I", "regionBaseXs"),
    ("i", "[I", "regionBaseYs"),
    ("k", "I", "mapBaseX"),
    ("d", "Z", "showPlayerNames"),
]:
    FIELD_NAME_MAP.setdefault((_WORLD_MAP_PANEL_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault((_WORLD_MAP_PANEL_OWNER, "a", "()V"), "loadMapRegionSprites")

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "actionSequence"),
    ("d", "Lcom/rs2/model/skill/crafting/SpinningRecipe;", "recipe"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/crafting/SpinningTask", _old_name, _descriptor),
        _mapped_name,
    )

_WEAVING_RECIPE_OWNER = "com/rs2/model/skill/crafting/WeavingRecipe"
for _old_name, _mapped_name in [
    ("a", "CLOTH"),
    ("b", "CLOTH5"),
    ("c", "CLOTH10"),
    ("d", "CLOTHX"),
    ("e", "SACKS"),
    ("f", "SACKS5"),
    ("g", "SACKS10"),
    ("h", "SACKSX"),
    ("i", "BASKET"),
    ("j", "BASKET5"),
    ("k", "BASKET10"),
    ("l", "BASKETX"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _WEAVING_RECIPE_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/WeavingRecipe;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("m", "I", "buttonId"),
    ("n", "I", "ingredientItemId"),
    ("o", "I", "productItemId"),
    ("p", "I", "quantity"),
    ("q", "I", "ingredientAmount"),
    ("r", "I", "requiredLevel"),
    ("s", "D", "experience"),
    ("t", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("u", "[Lcom/rs2/model/skill/crafting/WeavingRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_WEAVING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/WeavingRecipe;", "forButtonId"),
    ("a", "()I", "getIngredientItemId"),
    ("b", "()I", "getProductItemId"),
    ("c", "()I", "getQuantity"),
    ("d", "()I", "getIngredientAmount"),
    ("e", "()I", "getRequiredLevel"),
    ("f", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_WEAVING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (_CRAFTING_HANDLER_OWNER, "f", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleWeavingButton",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "actionSequence"),
    ("d", "Lcom/rs2/model/skill/crafting/WeavingRecipe;", "recipe"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/crafting/WeavingTask", _old_name, _descriptor),
        _mapped_name,
    )

_GLASSBLOWING_RECIPE_OWNER = "com/rs2/model/skill/crafting/GlassblowingRecipe"
for _old_name, _mapped_name in [
    ("a", "VIAL"),
    ("b", "VIAL5"),
    ("c", "VIAL10"),
    ("d", "VIALX"),
    ("e", "ORB"),
    ("f", "ORB5"),
    ("g", "ORB10"),
    ("h", "ORBX"),
    ("i", "BEER"),
    ("j", "BEER5"),
    ("k", "BEER10"),
    ("l", "BEERX"),
    ("m", "CANDLE"),
    ("n", "CANDLE5"),
    ("o", "CANDLE10"),
    ("p", "CANDLEX"),
    ("q", "OIL_LAMP"),
    ("r", "OIL_LAMP5"),
    ("s", "OIL_LAMP10"),
    ("t", "OIL_LAMPX"),
    ("u", "FISHBOWL"),
    ("v", "FISHBOWL5"),
    ("w", "FISHBOWL10"),
    ("x", "FISHBOWLX"),
    ("y", "LANTERN_LENS"),
    ("z", "LANTERN_LENS5"),
    ("A", "LANTERN_LENS10"),
    ("B", "LANTERN_LENSX"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _GLASSBLOWING_RECIPE_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/GlassblowingRecipe;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("C", "I", "buttonId"),
    ("D", "I", "productItemId"),
    ("E", "I", "quantity"),
    ("F", "I", "requiredLevel"),
    ("G", "D", "experience"),
    ("H", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("I", "[Lcom/rs2/model/skill/crafting/GlassblowingRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_GLASSBLOWING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/GlassblowingRecipe;", "forButtonId"),
    ("a", "()I", "getProductItemId"),
    ("b", "()I", "getQuantity"),
    ("c", "()I", "getRequiredLevel"),
    ("d", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_GLASSBLOWING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (_CRAFTING_HANDLER_OWNER, "b", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleGlassblowingButton",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "actionSequence"),
    ("d", "Lcom/rs2/model/skill/crafting/GlassblowingRecipe;", "recipe"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/crafting/GlassblowingTask", _old_name, _descriptor),
        _mapped_name,
    )

_SILVER_CRAFTING_RECIPE_OWNER = "com/rs2/model/skill/crafting/SilverCraftingRecipe"
for _old_name, _mapped_name in [
    ("a", "UNSTRUNG"),
    ("b", "UNSTRUNG5"),
    ("c", "UNSTRUNG10"),
    ("d", "UNSTRUNGX"),
    ("e", "SICKLE"),
    ("f", "SICKLE5"),
    ("g", "SICKLE10"),
    ("h", "SICKLEX"),
    ("i", "TIARA"),
    ("j", "TIARA5"),
    ("k", "TIARA10"),
    ("l", "TIARAX"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _SILVER_CRAFTING_RECIPE_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/SilverCraftingRecipe;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("m", "I", "buttonId"),
    ("n", "I", "barItemId"),
    ("o", "I", "productItemId"),
    ("p", "I", "quantity"),
    ("q", "I", "requiredLevel"),
    ("r", "D", "experience"),
    ("s", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("t", "[Lcom/rs2/model/skill/crafting/SilverCraftingRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_SILVER_CRAFTING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/SilverCraftingRecipe;", "forButtonId"),
    ("a", "()I", "getBarItemId"),
    ("b", "()I", "getProductItemId"),
    ("c", "()I", "getQuantity"),
    ("d", "()I", "getRequiredLevel"),
    ("e", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_SILVER_CRAFTING_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (_CRAFTING_HANDLER_OWNER, "d", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleSilverCraftingButton",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "actionSequence"),
    ("d", "Lcom/rs2/model/skill/crafting/SilverCraftingRecipe;", "recipe"),
    ("e", "Lcom/rs2/model/item/ItemStack;", "productItem"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/crafting/SilverCraftingTask", _old_name, _descriptor),
        _mapped_name,
    )

_POTTERY_RECIPE_OWNER = "com/rs2/model/skill/crafting/PotteryRecipe"
for _old_name, _mapped_name in [
    ("b", "POT"),
    ("c", "POT5"),
    ("d", "POT10"),
    ("e", "POTX"),
    ("f", "DISH"),
    ("g", "DISH5"),
    ("h", "DISH10"),
    ("i", "DISHX"),
    ("j", "BOWL"),
    ("k", "BOWL5"),
    ("l", "BOWL10"),
    ("m", "BOWLX"),
    ("n", "PLANT"),
    ("o", "PLANT5"),
    ("p", "PLANT10"),
    ("q", "PLANTX"),
    ("r", "LID"),
    ("s", "LID5"),
    ("t", "LID10"),
    ("u", "LIDX"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _POTTERY_RECIPE_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/PotteryRecipe;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("v", "I", "buttonId"),
    ("w", "I", "softClayItemId"),
    ("x", "I", "unfiredItemId"),
    ("y", "I", "firedItemId"),
    ("z", "I", "quantity"),
    ("A", "I", "requiredLevel"),
    ("B", "D", "wheelExperience"),
    ("C", "D", "firingExperience"),
    ("a", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("D", "[Lcom/rs2/model/skill/crafting/PotteryRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_POTTERY_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/PotteryRecipe;", "forButtonId"),
    ("a", "()I", "getButtonId"),
    ("b", "()I", "getSoftClayItemId"),
    ("c", "()I", "getUnfiredItemId"),
    ("d", "()I", "getFiredItemId"),
    ("e", "()I", "getQuantity"),
    ("f", "()I", "getRequiredLevel"),
    ("g", "()D", "getWheelExperience"),
    ("h", "()D", "getFiringExperience"),
]:
    METHOD_NAME_MAP.setdefault((_POTTERY_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "startPotteryFiring"),
    ("c", "(Lcom/rs2/model/player/Player;II)Z", "handlePotteryButton"),
]:
    METHOD_NAME_MAP.setdefault((_CRAFTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _owner in [
    "com/rs2/model/skill/crafting/PotteryWheelTask",
    "com/rs2/model/skill/crafting/PotteryOvenTask",
]:
    for _old_name, _descriptor, _mapped_name in [
        ("a", "I", "remainingActions"),
        ("b", "Lcom/rs2/model/player/Player;", "player"),
        ("c", "I", "actionSequence"),
        ("d", "Lcom/rs2/model/skill/crafting/PotteryRecipe;", "recipe"),
        ("e", "Lcom/rs2/model/item/ItemStack;", "productItem"),
    ]:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_DRAMEN_STAFF_RECIPE_OWNER = "com/rs2/model/skill/crafting/DramenStaffRecipe"
for _old_name, _mapped_name in [
    ("a", "STAFF"),
    ("b", "STAFF5"),
    ("c", "STAFF10"),
    ("d", "STAFFX"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _DRAMEN_STAFF_RECIPE_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/DramenStaffRecipe;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("e", "I", "buttonId"),
    ("f", "I", "ingredientItemId"),
    ("g", "I", "productItemId"),
    ("h", "I", "quantity"),
    ("i", "I", "ingredientAmount"),
    ("j", "I", "requiredLevel"),
    ("k", "D", "experience"),
    ("l", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("m", "[Lcom/rs2/model/skill/crafting/DramenStaffRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_DRAMEN_STAFF_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/DramenStaffRecipe;", "forButtonId"),
    ("a", "()I", "getIngredientItemId"),
    ("b", "()I", "getProductItemId"),
    ("c", "()I", "getQuantity"),
    ("d", "()I", "getIngredientAmount"),
    ("e", "()I", "getRequiredLevel"),
    ("f", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_DRAMEN_STAFF_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (_CRAFTING_HANDLER_OWNER, "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleDramenStaffButton",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "actionSequence"),
    ("d", "Lcom/rs2/model/skill/crafting/DramenStaffRecipe;", "recipe"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/crafting/DramenStaffCarvingTask", _old_name, _descriptor),
        _mapped_name,
    )

_JEWELLERY_DEFINITION_OWNER = "com/rs2/model/skill/crafting/JewelleryDefinition"
for _old_name, _mapped_name in [
    ("a", "GOLD"),
    ("b", "SAPPHIRE"),
    ("c", "EMERALD"),
    ("d", "RUBY"),
    ("e", "DIAMOND"),
    ("f", "DRAGONSTONE"),
    ("g", "ONYX"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _JEWELLERY_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/crafting/JewelleryDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("h", "[I", "recipeData"),
    ("i", "[Lcom/rs2/model/skill/crafting/JewelleryDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_JEWELLERY_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (_JEWELLERY_DEFINITION_OWNER, "a", "(Lcom/rs2/model/skill/crafting/JewelleryDefinition;)[I"),
    "getRecipeData",
)

_JEWELLERY_CRAFTING_DATA_OWNER = "com/rs2/model/skill/crafting/JewelleryCraftingData"
for _old_name, _descriptor, _mapped_name in [
    ("e", "[I", "materialItemIds"),
    ("a", "[Ljava/lang/String;", "missingMouldMessages"),
    ("b", "[[I", "interfaceIdsByJewelleryType"),
    ("c", "[[I", "amuletStringingRecipes"),
    ("d", "[[I", "productsByJewelleryType"),
]:
    FIELD_NAME_MAP.setdefault((_JEWELLERY_CRAFTING_DATA_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault((_JEWELLERY_CRAFTING_DATA_OWNER, "a", "()[I"), "getMaterialItemIds")

_JEWELLERY_CRAFTING_HANDLER_OWNER = "com/rs2/model/skill/crafting/JewelleryCraftingHandler"
FIELD_NAME_MAP.setdefault((_JEWELLERY_CRAFTING_HANDLER_OWNER, "e", "Ljava/util/HashMap;"), "definitionsByMaterialItemId")
for _old_name, _descriptor, _mapped_name in [
    ("a", "[Ljava/lang/String;", "missingMouldMessages"),
    ("b", "[[I", "interfaceIdsByJewelleryType"),
    ("c", "[[I", "amuletStringingRecipes"),
    ("d", "[[I", "productsByJewelleryType"),
]:
    FIELD_NAME_MAP.setdefault((_JEWELLERY_CRAFTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/JewelleryDefinition;", "forMaterialItemId"),
    ("c", "(Lcom/rs2/model/player/Player;I)V", "sendMouldOptions"),
    ("a", "(Lcom/rs2/model/player/Player;I)V", "openJewelleryCraftingInterface"),
    ("a", "(Lcom/rs2/model/player/Player;III)V", "startJewelleryCraftingTask"),
    ("b", "(Lcom/rs2/model/player/Player;I)V", "stringAmulet"),
]:
    METHOD_NAME_MAP.setdefault((_JEWELLERY_CRAFTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "materialItemId"),
    ("b", "I", "remainingActions"),
    ("c", "I", "jewelleryType"),
    ("d", "Lcom/rs2/model/player/Player;", "player"),
    ("e", "I", "actionSequence"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/crafting/JewelleryCraftingTask", _old_name, _descriptor),
        _mapped_name,
    )

_CRAFTED_ARMOR_ACTION_OWNER = "com/rs2/model/skill/crafting/armor/CraftedArmorAction"
for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "materialItemId"),
    ("c", "I", "materialAmount"),
    ("d", "I", "productItemId"),
    ("e", "I", "recipeQuantity"),
    ("f", "I", "requestedQuantity"),
    ("h", "I", "requiredLevel"),
    ("g", "D", "experience"),
]:
    FIELD_NAME_MAP.setdefault((_CRAFTED_ARMOR_ACTION_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault((_CRAFTED_ARMOR_ACTION_OWNER, "a", "()Z"), "startCrafting")

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/skill/crafting/armor/CraftedArmorAction;", "action"),
    ("c", "I", "actionSequence"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/crafting/armor/CraftedArmorTask", _old_name, _descriptor),
        _mapped_name,
    )

_SPLITBARK_CRAFTING_ACTION_OWNER = "com/rs2/model/skill/crafting/armor/SplitbarkCraftingAction"
for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "productItemId"),
    ("c", "I", "recipeQuantity"),
    ("g", "I", "requestedQuantity"),
    ("d", "I", "fineClothAmount"),
    ("e", "I", "barkAmount"),
    ("f", "I", "coinAmount"),
]:
    FIELD_NAME_MAP.setdefault((_SPLITBARK_CRAFTING_ACTION_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault((_SPLITBARK_CRAFTING_ACTION_OWNER, "a", "()Z"), "startCrafting")

_ARMOR_RECIPE_CONSTANTS = {
    "LeatherRecipe": [
        ("a", "ARMOUR"),
        ("b", "ARMOUR5"),
        ("c", "ARMOUR10"),
        ("d", "GLOVES"),
        ("e", "GLOVES5"),
        ("f", "GLOVES10"),
        ("g", "BOOTS"),
        ("h", "BOOTS5"),
        ("i", "BOOTS10"),
        ("j", "VAMB"),
        ("k", "VAMB5"),
        ("l", "VAMB10"),
        ("m", "CHAPS"),
        ("n", "CHAPS5"),
        ("o", "CHAPS10"),
        ("p", "COIF"),
        ("q", "COIF5"),
        ("r", "COIF10"),
        ("s", "COWL"),
        ("t", "COWL5"),
        ("u", "COWL10"),
    ],
    "HardLeatherRecipe": [
        ("a", "BODY"),
        ("b", "BODY5"),
        ("c", "BODY10"),
        ("d", "BODYX"),
    ],
    "GreenDragonhideRecipe": [
        ("a", "VAMB"),
        ("b", "VAMB5"),
        ("c", "VAMB10"),
        ("d", "VAMBX"),
        ("e", "CHAPS"),
        ("f", "CHAPS5"),
        ("g", "CHAPS10"),
        ("h", "CHAPSX"),
        ("i", "BODY"),
        ("j", "BODY5"),
        ("k", "BODY10"),
        ("l", "BODYX"),
    ],
    "BlueDragonhideRecipe": [
        ("a", "VAMB"),
        ("b", "VAMB5"),
        ("c", "VAMB10"),
        ("d", "VAMBX"),
        ("e", "CHAPS"),
        ("f", "CHAPS5"),
        ("g", "CHAPS10"),
        ("h", "CHAPSX"),
        ("i", "BODY"),
        ("j", "BODY5"),
        ("k", "BODY10"),
        ("l", "BODYX"),
    ],
    "RedDragonhideRecipe": [
        ("a", "VAMB"),
        ("b", "VAMB5"),
        ("c", "VAMB10"),
        ("d", "VAMBX"),
        ("e", "CHAPS"),
        ("f", "CHAPS5"),
        ("g", "CHAPS10"),
        ("h", "CHAPSX"),
        ("i", "BODY"),
        ("j", "BODY5"),
        ("k", "BODY10"),
        ("l", "BODYX"),
    ],
    "BlackDragonhideRecipe": [
        ("a", "VAMB"),
        ("b", "VAMB5"),
        ("c", "VAMB10"),
        ("d", "VAMBX"),
        ("e", "CHAPS"),
        ("f", "CHAPS5"),
        ("g", "CHAPS10"),
        ("h", "CHAPSX"),
        ("i", "BODY"),
        ("j", "BODY5"),
        ("k", "BODY10"),
        ("l", "BODYX"),
    ],
    "SnakeskinAccessoryRecipe": [
        ("a", "BANDANA"),
        ("b", "BANDANA5"),
        ("c", "BANDANA10"),
        ("d", "BANDANAX"),
        ("e", "BOOTS"),
        ("f", "BOOTS5"),
        ("g", "BOOTS10"),
        ("h", "BOOTSX"),
    ],
    "SnakeskinArmorRecipe": [
        ("a", "VAMB"),
        ("b", "VAMB5"),
        ("c", "VAMB10"),
        ("d", "VAMBX"),
        ("e", "CHAPS"),
        ("f", "CHAPS5"),
        ("g", "CHAPS10"),
        ("h", "CHAPSX"),
        ("i", "BODY"),
        ("j", "BODY5"),
        ("k", "BODY10"),
        ("l", "BODYX"),
    ],
    "SplitbarkRecipe": [
        ("a", "HELM"),
        ("b", "HELM5"),
        ("c", "HELM10"),
        ("d", "HELMX"),
        ("e", "BODY"),
        ("f", "BODY5"),
        ("g", "BODY10"),
        ("h", "BODYX"),
        ("i", "LEGS"),
        ("j", "LEGS5"),
        ("k", "LEGS10"),
        ("l", "LEGSX"),
        ("m", "GAUNTLETS"),
        ("n", "GAUNTLETS5"),
        ("o", "GAUNTLETS10"),
        ("p", "GAUNTLETSX"),
        ("q", "BOOTS"),
        ("r", "BOOTS5"),
        ("s", "BOOTS10"),
        ("t", "BOOTSX"),
    ],
}

for _recipe_class, _constants in _ARMOR_RECIPE_CONSTANTS.items():
    _owner = f"com/rs2/model/skill/crafting/armor/{_recipe_class}"
    for _old_name, _mapped_name in _constants:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, f"L{_owner};"), _mapped_name)

_ARMOR_STANDARD_RECIPE_LAYOUTS = {
    "LeatherRecipe": [
        ("v", "I", "buttonId"),
        ("w", "I", "materialItemId"),
        ("x", "I", "materialAmount"),
        ("y", "I", "productItemId"),
        ("z", "I", "quantity"),
        ("A", "I", "requiredLevel"),
        ("B", "D", "experience"),
        ("C", "Ljava/util/HashMap;", "definitionsByButtonId"),
        ("D", "[Lcom/rs2/model/skill/crafting/armor/LeatherRecipe;", "VALUES"),
    ],
    "HardLeatherRecipe": [
        ("e", "I", "buttonId"),
        ("f", "I", "materialItemId"),
        ("g", "I", "materialAmount"),
        ("h", "I", "productItemId"),
        ("i", "I", "quantity"),
        ("j", "I", "requiredLevel"),
        ("k", "D", "experience"),
        ("l", "Ljava/util/HashMap;", "definitionsByButtonId"),
        ("m", "[Lcom/rs2/model/skill/crafting/armor/HardLeatherRecipe;", "VALUES"),
    ],
}

for _recipe_class in [
    "GreenDragonhideRecipe",
    "BlueDragonhideRecipe",
    "RedDragonhideRecipe",
    "BlackDragonhideRecipe",
]:
    _ARMOR_STANDARD_RECIPE_LAYOUTS[_recipe_class] = [
        ("m", "I", "buttonId"),
        ("n", "I", "materialItemId"),
        ("o", "I", "materialAmount"),
        ("p", "I", "productItemId"),
        ("q", "I", "quantity"),
        ("r", "I", "requiredLevel"),
        ("s", "D", "experience"),
        ("t", "Ljava/util/HashMap;", "definitionsByButtonId"),
        ("u", f"[Lcom/rs2/model/skill/crafting/armor/{_recipe_class};", "VALUES"),
    ]

_ARMOR_STANDARD_RECIPE_METHODS = [
    ("a", "()I", "getButtonId"),
    ("b", "()I", "getMaterialItemId"),
    ("c", "()I", "getMaterialAmount"),
    ("d", "()I", "getProductItemId"),
    ("e", "()I", "getQuantity"),
    ("f", "()I", "getRequiredLevel"),
    ("g", "()D", "getExperience"),
]

for _recipe_class, _fields in _ARMOR_STANDARD_RECIPE_LAYOUTS.items():
    _owner = f"com/rs2/model/skill/crafting/armor/{_recipe_class}"
    for _old_name, _descriptor, _mapped_name in _fields:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)
    METHOD_NAME_MAP.setdefault((_owner, "a", f"(I)L{_owner};"), "forButtonId")
    for _old_name, _descriptor, _mapped_name in _ARMOR_STANDARD_RECIPE_METHODS:
        METHOD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

_SNAKESKIN_ACCESSORY_RECIPE_OWNER = "com/rs2/model/skill/crafting/armor/SnakeskinAccessoryRecipe"
for _old_name, _descriptor, _mapped_name in [
    ("i", "I", "buttonId"),
    ("j", "I", "materialItemId"),
    ("k", "I", "materialAmount"),
    ("l", "I", "productItemId"),
    ("m", "I", "quantity"),
    ("n", "I", "requiredLevel"),
    ("o", "D", "experience"),
    ("p", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("q", "[Lcom/rs2/model/skill/crafting/armor/SnakeskinAccessoryRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_SNAKESKIN_ACCESSORY_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (_SNAKESKIN_ACCESSORY_RECIPE_OWNER, "a", "(I)Lcom/rs2/model/skill/crafting/armor/SnakeskinAccessoryRecipe;"),
    "forButtonId",
)
for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getMaterialItemId"),
    ("b", "()I", "getMaterialAmount"),
    ("c", "()I", "getProductItemId"),
    ("d", "()I", "getQuantity"),
    ("e", "()I", "getRequiredLevel"),
    ("f", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_SNAKESKIN_ACCESSORY_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

_SNAKESKIN_ARMOR_RECIPE_OWNER = "com/rs2/model/skill/crafting/armor/SnakeskinArmorRecipe"
for _old_name, _descriptor, _mapped_name in [
    ("m", "I", "buttonId"),
    ("n", "I", "materialItemId"),
    ("o", "I", "materialAmount"),
    ("p", "I", "productItemId"),
    ("q", "I", "quantity"),
    ("r", "I", "requiredLevel"),
    ("s", "D", "experience"),
    ("t", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("u", "[Lcom/rs2/model/skill/crafting/armor/SnakeskinArmorRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_SNAKESKIN_ARMOR_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (_SNAKESKIN_ARMOR_RECIPE_OWNER, "a", "(I)Lcom/rs2/model/skill/crafting/armor/SnakeskinArmorRecipe;"),
    "forButtonId",
)
for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getMaterialItemId"),
    ("b", "()I", "getMaterialAmount"),
    ("c", "()I", "getProductItemId"),
    ("d", "()I", "getQuantity"),
    ("e", "()I", "getRequiredLevel"),
    ("f", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_SNAKESKIN_ARMOR_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

_SPLITBARK_RECIPE_OWNER = "com/rs2/model/skill/crafting/armor/SplitbarkRecipe"
for _old_name, _descriptor, _mapped_name in [
    ("u", "I", "buttonId"),
    ("v", "I", "productItemId"),
    ("w", "I", "quantity"),
    ("x", "I", "materialAmount"),
    ("y", "I", "coinAmount"),
    ("z", "Ljava/util/HashMap;", "definitionsByButtonId"),
    ("A", "[Lcom/rs2/model/skill/crafting/armor/SplitbarkRecipe;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_SPLITBARK_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/crafting/armor/SplitbarkRecipe;", "forButtonId"),
    ("a", "()I", "getProductItemId"),
    ("b", "()I", "getMaterialAmount"),
    ("c", "()I", "getCoinAmount"),
    ("d", "()I", "getQuantity"),
]:
    METHOD_NAME_MAP.setdefault((_SPLITBARK_RECIPE_OWNER, _old_name, _descriptor), _mapped_name)

for _action_class in [
    "BlackDragonhideCrafting",
    "BlueDragonhideCrafting",
    "GreenDragonhideCrafting",
    "HardLeatherCrafting",
    "LeatherCrafting",
    "RedDragonhideCrafting",
    "SnakeskinAccessoryCrafting",
    "SnakeskinArmorCrafting",
    "SplitbarkCrafting",
]:
    _owner = f"com/rs2/model/skill/crafting/armor/{_action_class}"
    METHOD_NAME_MAP.setdefault((_owner, "a", f"(Lcom/rs2/model/player/Player;II)L{_owner};"), "createForButton")

_AGILITY_OBSTACLE_HANDLER_OWNER = "com/rs2/model/skill/agility/AgilityObstacleHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;IIIIII)V", "startDelayedShortcutMovement"),
    ("a", "(Lcom/rs2/model/player/Player;IIIIIZII)V", "startForcedMovement"),
    ("a", "(Lcom/rs2/model/player/Player;IIIIIII)V", "startQueuedObstacleMovement"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;DIIIIIILjava/lang/String;Ljava/lang/String;)V",
        "startAgilityMovement",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;DIIIIILjava/lang/String;Ljava/lang/String;)V",
        "startPositionOffsetObstacle",
    ),
]:
    METHOD_NAME_MAP.setdefault((_AGILITY_OBSTACLE_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _owner in [
    "com/rs2/model/skill/agility/AgilityMovementStepTask",
    "com/rs2/model/skill/agility/AgilityQueuedMovementStepTask",
]:
    for _old_name, _descriptor, _mapped_name in [
        ("a", "Lcom/rs2/model/player/Player;", "player"),
        ("b", "I", "forcedMovementEndXOffset"),
        ("c", "I", "forcedMovementEndYOffset"),
        ("d", "I", "forcedMovementStartDelay"),
        ("e", "I", "forcedMovementEndDelay"),
        ("f", "I", "forcedMovementDirection"),
    ]:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _owner in [
    "com/rs2/model/skill/agility/AgilityMovementFinishTask",
    "com/rs2/model/skill/agility/AgilityQueuedMovementFinishTask",
]:
    for _old_name, _descriptor, _mapped_name in [
        ("a", "Lcom/rs2/model/player/Player;", "player"),
        ("b", "I", "experience"),
        ("c", "Z", "unlockPlayer"),
        ("d", "I", "destinationX"),
        ("e", "I", "destinationY"),
        ("f", "I", "destinationPlane"),
    ]:
        FIELD_NAME_MAP.setdefault((_owner, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "deltaX"),
    ("c", "I", "deltaY"),
    ("d", "I", "animationId"),
    ("e", "I", "delayedAnimationId"),
    ("f", "I", "duration"),
    ("g", "D", "experience"),
    ("h", "Ljava/lang/String;", "completionMessage"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/agility/AgilityObstacleCompletionTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "deltaX"),
    ("c", "I", "deltaY"),
    ("d", "I", "deltaPlane"),
    ("e", "D", "experience"),
    ("f", "Ljava/lang/String;", "completionMessage"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/agility/AgilityPositionOffsetTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "deltaX"),
    ("c", "I", "deltaY"),
    ("d", "I", "forcedMovementEndDelay"),
    ("e", "I", "completionDelay"),
    ("f", "I", "experience"),
    ("g", "I", "animationId"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/agility/AgilityQueuedMovementTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "deltaX"),
    ("c", "I", "deltaY"),
    ("d", "I", "forcedMovementEndDelay"),
    ("e", "I", "completionDelay"),
    ("f", "I", "experience"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/agility/AgilityShortcutStartTask", _old_name, _descriptor),
        _mapped_name,
    )

_CLEAN_HERB_DEFINITION_OWNER = "com/rs2/model/skill/herblore/CleanHerbDefinition"
for _old_name, _mapped_name in [
    ("a", "GUAM"),
    ("b", "MARRENTILL"),
    ("c", "TARROMIN"),
    ("d", "HARRALANDER"),
    ("e", "RANARR"),
    ("f", "TOADFLAX"),
    ("g", "IRIT"),
    ("h", "AVANTOE"),
    ("i", "KWUARM"),
    ("j", "SNAPDRAGON"),
    ("k", "CADANTINE"),
    ("l", "LANTADYME"),
    ("m", "DWARF_WEED"),
    ("n", "TORSTOL"),
    ("o", "SNAKE_WEED"),
    ("p", "ARDRIGAL"),
    ("q", "SITO_FOIL"),
    ("r", "VOLENCIA_MOSS"),
    ("s", "ROGUES_PURSE"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _CLEAN_HERB_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/herblore/CleanHerbDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("t", "I", "grimyItemId"),
    ("u", "I", "cleanItemId"),
    ("v", "I", "requiredLevel"),
    ("w", "D", "experience"),
    ("x", "Ljava/util/Map;", "definitionsByGrimyItemId"),
    ("y", "[Lcom/rs2/model/skill/herblore/CleanHerbDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_CLEAN_HERB_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/herblore/CleanHerbDefinition;", "forGrimyItemId"),
    ("a", "()I", "getCleanItemId"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_CLEAN_HERB_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_POISONED_WEAPON_DEFINITION_OWNER = "com/rs2/model/skill/herblore/PoisonedWeaponDefinition"
for _old_name, _mapped_name in [
    ("a", "BRONZE_ARROW"),
    ("b", "IRON_ARROW"),
    ("c", "STEEL_ARROW"),
    ("d", "MITHRIL_ARROW"),
    ("e", "ADAMANT_ARROW"),
    ("f", "RUNE_ARROW"),
    ("g", "IRON_KNIFE"),
    ("h", "BRONZE_KNIFE"),
    ("i", "STEEL_KNIFE"),
    ("j", "BLACK_KNIFE"),
    ("k", "MITHRIL_KNIFE"),
    ("l", "ADAMANT_KNIFE"),
    ("m", "RUNE_KNIFE"),
    ("n", "BRONZE_DART"),
    ("o", "IRON_DART"),
    ("p", "STEEL_DART"),
    ("q", "BLACK_DART"),
    ("r", "MITHRIL_DART"),
    ("s", "ADAMANT_DART"),
    ("t", "RUNE_DART"),
    ("u", "IRON_JAVELIN"),
    ("v", "BRONZE_JAVELIN"),
    ("w", "STEEL_JAVELIN"),
    ("x", "MITHRIL_JAVELIN"),
    ("y", "ADAMANT_JAVELIN"),
    ("z", "RUNE_JAVELIN"),
    ("A", "IRON_DAGGER"),
    ("B", "BRONZE_DAGGER"),
    ("C", "STEEL_DAGGER"),
    ("D", "BLACK_DAGGER"),
    ("E", "MITHRIL_DAGGER"),
    ("F", "ADAMANT_DAGGER"),
    ("G", "RUNE_DAGGER"),
    ("H", "DRAGON_DAGGER"),
    ("I", "BRONZE_SPEAR"),
    ("J", "IRON_SPEAR"),
    ("K", "STEEL_SPEAR"),
    ("L", "BLACK_SPEAR"),
    ("M", "MITHRIL_SPEAR"),
    ("N", "ADAMANT_SPEAR"),
    ("O", "RUNE_SPEAR"),
    ("P", "DRAGON_SPEAR"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _POISONED_WEAPON_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/herblore/PoisonedWeaponDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("Q", "I", "unpoisonedItemId"),
    ("R", "I", "poisonedItemId"),
    ("S", "I", "poisonPlusItemId"),
    ("T", "I", "poisonPlusPlusItemId"),
    ("U", "Ljava/util/Map;", "definitionsByUnpoisonedItemId"),
    ("V", "[Lcom/rs2/model/skill/herblore/PoisonedWeaponDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_POISONED_WEAPON_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/herblore/PoisonedWeaponDefinition;", "forUnpoisonedItemId"),
    ("a", "()I", "getPoisonedItemId"),
    ("b", "()I", "getPoisonPlusItemId"),
    ("c", "()I", "getPoisonPlusPlusItemId"),
]:
    METHOD_NAME_MAP.setdefault((_POISONED_WEAPON_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_HERBLORE_HANDLER_OWNER = "com/rs2/model/skill/herblore/HerbloreHandler"
FIELD_NAME_MAP.setdefault((_HERBLORE_HANDLER_OWNER, "a", "[[D"), "POTION_RECIPES")
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;IIII)Z", "combinePotionDoses"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;Lcom/rs2/model/item/ItemStack;II)Z",
        "handlePotionMaking",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;I)Z",
        "emptyContainer",
    ),
    ("a", "(Lcom/rs2/model/item/ItemStack;)I", "getEmptyContainerItemId"),
]:
    METHOD_NAME_MAP.setdefault((_HERBLORE_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

_PESTLE_AND_MORTAR_HANDLER_OWNER = "com/rs2/model/skill/herblore/PestleAndMortarHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "PESTLE_AND_MORTAR_ITEM_ID"),
    ("b", "[[I", "GRINDING_RECIPES"),
]:
    FIELD_NAME_MAP.setdefault((_PESTLE_AND_MORTAR_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    (
        _PESTLE_AND_MORTAR_HANDLER_OWNER,
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;Lcom/rs2/model/item/ItemStack;II)Z",
    ),
    "handlePestleAndMortar",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "actionSequence"),
    ("c", "Lcom/rs2/model/item/ItemStack;", "secondaryIngredient"),
    ("d", "D", "unfinishedPotionItemId"),
    ("e", "D", "finishedPotionItemId"),
    ("f", "D", "experience"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/herblore/FinishedPotionTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "actionSequence"),
    ("c", "Lcom/rs2/model/item/ItemStack;", "herbItem"),
    ("d", "I", "baseItemId"),
    ("e", "D", "unfinishedPotionItemId"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/herblore/UnfinishedPotionTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "outputItemId"),
    ("b", "I", "poisonTier"),
    ("c", "Lcom/rs2/model/skill/herblore/PoisonedWeaponDefinition;", "weaponDefinition"),
    ("d", "Lcom/rs2/model/player/Player;", "player"),
    ("e", "I", "weaponItemId"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/herblore/WeaponPoisonTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/item/ItemStack;Lcom/rs2/model/item/ItemStack;)Z",
        "handleWeaponPoisoning",
    ),
    ("a", "(Lcom/rs2/model/player/Player;II)V", "startWeaponPoisonTask"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/bot/BotRoute", _old_name, _descriptor), _mapped_name)

_PICKPOCKET_DEFINITION_OWNER = "com/rs2/model/skill/thieving/PickpocketDefinition"
for _old_name, _mapped_name in [
    ("a", "CITIZEN"),
    ("b", "FARMER"),
    ("c", "WARRIOR"),
    ("d", "ROGUE"),
    ("e", "MASTER_FARMER"),
    ("f", "GUARD"),
    ("g", "FREMENNIK_CITIZIN"),
    ("h", "BEARDED_POLLNIVIAN_BANDIT"),
    ("i", "DESERT_BANDIT"),
    ("j", "KNIGHT"),
    ("k", "POLLNIVIAN_BANDIT"),
    ("l", "WATCHMAN"),
    ("m", "MENAPHITE_THUG"),
    ("n", "PALADIN"),
    ("o", "GNOME"),
    ("p", "HERO"),
    ("q", "ELF"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _PICKPOCKET_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/thieving/PickpocketDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("r", "[Ljava/lang/String;", "npcNames"),
    ("s", "I", "requiredLevel"),
    ("t", "D", "experience"),
    ("u", "[Lcom/rs2/model/item/ItemStack;", "commonRewards"),
    ("v", "[Lcom/rs2/model/item/ItemStack;", "rareRewards"),
    ("w", "I", "stunTicks"),
    ("x", "I", "minDamage"),
    ("y", "I", "maxDamage"),
    ("z", "I", "successChanceLow"),
    ("A", "I", "successChanceHigh"),
    ("B", "[Lcom/rs2/model/skill/thieving/PickpocketDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_PICKPOCKET_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()[Ljava/lang/String;", "getNpcNames"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()D", "getExperience"),
    ("d", "()[Lcom/rs2/model/item/ItemStack;", "getCommonRewards"),
    ("e", "()[Lcom/rs2/model/item/ItemStack;", "getRareRewards"),
    ("f", "()I", "getStunTicks"),
    ("g", "()I", "getMinDamage"),
    ("h", "()I", "getMaxDamage"),
    ("i", "()I", "getSuccessChanceLow"),
    ("j", "()I", "getSuccessChanceHigh"),
]:
    METHOD_NAME_MAP.setdefault((_PICKPOCKET_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Z", "success"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "Lcom/rs2/model/npc/Npc;", "npc"),
    ("d", "Lcom/rs2/model/item/ItemStack;", "reward"),
    ("e", "Lcom/rs2/model/skill/thieving/PickpocketDefinition;", "definition"),
    ("f", "Ljava/lang/String;", "npcName"),
    ("g", "I", "damage"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/thieving/PickpocketTask", _old_name, _descriptor),
        _mapped_name,
    )

_STALL_DEFINITION_OWNER = "com/rs2/model/skill/thieving/StallDefinition"
for _old_name, _mapped_name in [
    ("a", "VEGETABLE_STALL"),
    ("b", "CAKE_STALL"),
    ("c", "CRAFTING_STALL"),
    ("d", "MONKEY_FOOD_STALL"),
    ("f", "TEA_STALL"),
    ("h", "SILK_STALL"),
    ("i", "WINE_STALL"),
    ("j", "SEED_STALL"),
    ("k", "FUR_STALL"),
    ("l", "FISH_STALL"),
    ("m", "SILVER_STALL"),
    ("n", "SPICE_STALL"),
    ("o", "MAGIC_STALL"),
    ("p", "SCIMITAR_STALL"),
    ("q", "GEM_STALL"),
]:
    FIELD_NAME_MAP.setdefault(
        (_STALL_DEFINITION_OWNER, _old_name, "Lcom/rs2/model/skill/thieving/StallDefinition;"),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("r", "[I", "objectIds"),
    ("s", "[Lcom/rs2/model/item/ItemStack;", "rewards"),
    ("t", "I", "requiredLevel"),
    ("u", "I", "experience"),
    ("v", "I", "respawnTicks"),
    ("w", "[Lcom/rs2/model/skill/thieving/StallDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_STALL_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()[I", "getObjectIds"),
    ("b", "()[Lcom/rs2/model/item/ItemStack;", "getRewards"),
    ("c", "()I", "getRequiredLevel"),
    ("d", "()I", "getExperience"),
    ("e", "()I", "getRespawnTicks"),
]:
    METHOD_NAME_MAP.setdefault((_STALL_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_STALL_THIEVING_HANDLER_OWNER = "com/rs2/model/skill/thieving/StallThievingHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)I", "getEmptyStallObjectId"),
    ("a", "(Lcom/rs2/model/player/Player;III)Z", "handleStallThieving"),
]:
    METHOD_NAME_MAP.setdefault((_STALL_THIEVING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(
    (_STALL_THIEVING_HANDLER_OWNER, "a", "Ljava/util/Random;"),
    "random",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "I", "actionSequence"),
    ("c", "I", "objectId"),
    ("d", "I", "objectX"),
    ("e", "I", "objectY"),
    ("f", "Lcom/rs2/model/item/ItemStack;", "reward"),
    ("g", "Lcom/rs2/model/skill/thieving/StallDefinition;", "stallDefinition"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/thieving/StallThievingTask", _old_name, _descriptor),
        _mapped_name,
    )

_THIEVING_OBJECT_HANDLER_OWNER = "com/rs2/model/skill/thieving/ThievingObjectHandler"
for _old_name, _descriptor, _mapped_name in [
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;IIDII)V",
        "startLockpickTask",
    ),
    ("a", "(Lcom/rs2/model/player/Player;III)Z", "handleThievingObject"),
    ("a", "()Ljava/util/Random;", "getRandom"),
]:
    METHOD_NAME_MAP.setdefault((_THIEVING_OBJECT_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(
    (_THIEVING_OBJECT_HANDLER_OWNER, "a", "Ljava/util/Random;"),
    "random",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "D", "experience"),
    ("c", "I", "objectId"),
    ("d", "Lcom/rs2/model/Position;", "objectPosition"),
    ("e", "I", "moveDeltaX"),
    ("f", "I", "moveDeltaY"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/thieving/LockpickTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "D", "experience"),
    ("c", "[Lcom/rs2/model/item/ItemStack;", "rewards"),
    ("d", "I", "objectX"),
    ("e", "I", "objectY"),
    ("f", "I", "objectId"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/thieving/TrapDisarmTask", _old_name, _descriptor),
        _mapped_name,
    )

_BONE_DEFINITION_OWNER = "com/rs2/model/skill/prayer/BoneDefinition"
for _old_name, _mapped_name in [
    ("c", "BONES"),
    ("d", "MONKEY_BONES"),
    ("e", "BAT_BONES"),
    ("f", "BIG_BONES"),
    ("g", "SHAIKAHAN_BONES"),
    ("h", "BABYDRAGON_BONES"),
    ("i", "DRAGON_BONES"),
    ("j", "ZOGRE_BONES"),
    ("k", "FAYRG_BONES"),
    ("l", "RAURG_BONES"),
    ("m", "DAGANNOTH_BONES"),
    ("n", "OURG_BONES"),
    ("o", "WYVERN_BONES"),
]:
    FIELD_NAME_MAP.setdefault(
        (_BONE_DEFINITION_OWNER, _old_name, "Lcom/rs2/model/skill/prayer/BoneDefinition;"),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "[I", "itemIds"),
    ("b", "D", "experience"),
    ("p", "[Lcom/rs2/model/skill/prayer/BoneDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_BONE_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

METHOD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/prayer/BoneBuryingHandler", "a", "(II)Z"),
    "handleBuryBone",
)
FIELD_NAME_MAP.setdefault(
    ("com/rs2/model/skill/prayer/BoneBuryingHandler", "a", "Lcom/rs2/model/player/Player;"),
    "player",
)

_PRAYER_MANAGER_OWNER = "com/rs2/model/skill/prayer/PrayerManager"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;)V", "rechargePrayerAtAltar"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "rechargePrayerWithBoost"),
    ("a", "(Lcom/rs2/model/Entity;Lcom/rs2/model/Entity;)V", "triggerRetribution"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Entity;I)V", "triggerRedemption"),
    ("a", "(Lcom/rs2/model/player/Player;I)V", "drainPrayerForSmite"),
    (
        "a",
        "(Lcom/rs2/model/skill/prayer/PrayerManager;)Lcom/rs2/model/player/Player;",
        "getPlayer",
    ),
]:
    METHOD_NAME_MAP.setdefault((_PRAYER_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "[[Ljava/lang/Object;", "PRAYER_DEFINITIONS"),
    ("c", "[[I", "NPC_PRAYER_EXPERIENCE_REWARDS"),
    ("d", "Lcom/rs2/model/task/TickTask;", "rapidRestoreTask"),
]:
    FIELD_NAME_MAP.setdefault((_PRAYER_MANAGER_OWNER, _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(
    (
        "com/rs2/model/skill/prayer/RapidRestoreTask",
        "a",
        "Lcom/rs2/model/skill/prayer/PrayerManager;",
    ),
    "prayerManager",
)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "Z", "multiCombat"),
    ("c", "Lcom/rs2/model/Entity;", "source"),
    ("d", "Lcom/rs2/model/Entity;", "primaryTarget"),
    ("e", "Lcom/rs2/model/combat/hit/HitDefinition;", "hitDefinition"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/prayer/RetributionPrayerTask", _old_name, _descriptor),
        _mapped_name,
    )

_RUNE_DEFINITION_OWNER = "com/rs2/model/skill/runecrafting/RuneDefinition"
for _old_name, _mapped_name in [
    ("a", "FIRE_RUNE"),
    ("b", "WATER_RUNE"),
    ("c", "AIR_RUNE"),
    ("d", "EARTH_RUNE"),
    ("e", "MIND_RUNE"),
    ("f", "BODY_RUNE"),
    ("g", "DEATH_RUNE"),
    ("h", "NATURE_RUNE"),
    ("i", "CHAOS_RUNE"),
    ("j", "LAW_RUNE"),
    ("k", "COSMIC_RUNE"),
    ("l", "BLOOD_RUNE"),
    ("m", "SOUL_RUNE"),
]:
    FIELD_NAME_MAP.setdefault(
        (_RUNE_DEFINITION_OWNER, _old_name, "Lcom/rs2/model/skill/runecrafting/RuneDefinition;"),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("n", "I", "runeItemId"),
    ("o", "I", "requiredLevel"),
    ("p", "D", "experience"),
    ("q", "I", "multipleRunesLevelInterval"),
    ("r", "[Lcom/rs2/model/skill/runecrafting/RuneDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_RUNE_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getRuneItemId"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()D", "getExperience"),
    ("d", "()I", "getMultipleRunesLevelInterval"),
]:
    METHOD_NAME_MAP.setdefault((_RUNE_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_COMBINATION_RUNE_DEFINITION_OWNER = "com/rs2/model/skill/runecrafting/CombinationRuneDefinition"
for _old_name, _mapped_name in [
    ("a", "MIST_WITH_AIR_TALISMAN"),
    ("b", "MIST_WITH_WATER_TALISMAN"),
    ("c", "DUST_WITH_AIR_TALISMAN"),
    ("d", "DUST_WITH_EARTH_TALISMAN"),
    ("e", "MUD_WITH_WATER_TALISMAN"),
    ("f", "MUD_WITH_EARTH_TALISMAN"),
    ("g", "SMOKE_WITH_AIR_TALISMAN"),
    ("h", "SMOKE_WITH_FIRE_TALISMAN"),
    ("i", "STEAM_WITH_WATER_TALISMAN"),
    ("j", "STEAM_WITH_FIRE_TALISMAN"),
    ("k", "LAVA_WITH_EARTH_TALISMAN"),
    ("l", "LAVA_WITH_FIRE_TALISMAN"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _COMBINATION_RUNE_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/runecrafting/CombinationRuneDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("m", "I", "talismanItemId"),
    ("n", "I", "runeItemId"),
    ("o", "I", "altarObjectId"),
    ("p", "I", "productRuneItemId"),
    ("q", "I", "requiredLevel"),
    ("r", "D", "experience"),
    ("s", "[Lcom/rs2/model/skill/runecrafting/CombinationRuneDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_COMBINATION_RUNE_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(II)Lcom/rs2/model/skill/runecrafting/CombinationRuneDefinition;", "forTalismanAndAltarObjectId"),
    ("a", "()I", "getTalismanItemId"),
    ("b", "()I", "getRuneItemId"),
    ("c", "()I", "getProductRuneItemId"),
    ("d", "()I", "getRequiredLevel"),
    ("e", "()D", "getExperience"),
]:
    METHOD_NAME_MAP.setdefault((_COMBINATION_RUNE_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_ESSENCE_POUCH_DEFINITION_OWNER = "com/rs2/model/skill/runecrafting/EssencePouchDefinition"
for _old_name, _mapped_name in [
    ("a", "SMALL_POUCH"),
    ("b", "MEDIUM_POUCH"),
    ("c", "LARGE_POUCH"),
    ("d", "GIANT_POUCH"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _ESSENCE_POUCH_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/runecrafting/EssencePouchDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("e", "I", "pouchIndex"),
    ("f", "I", "itemId"),
    ("g", "I", "degradedItemId"),
    ("h", "I", "capacity"),
    ("i", "I", "requiredLevel"),
    ("j", "I", "degradeAfterUses"),
    ("k", "I", "degradedCapacityPenalty"),
    ("l", "Ljava/util/Map;", "lookupById"),
    ("m", "[Lcom/rs2/model/skill/runecrafting/EssencePouchDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_ESSENCE_POUCH_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/runecrafting/EssencePouchDefinition;", "forItemOrIndex"),
    ("a", "()I", "getPouchIndex"),
    ("b", "()I", "getItemId"),
    ("c", "()I", "getDegradedItemId"),
    ("d", "()I", "getCapacity"),
    ("e", "()I", "getRequiredLevel"),
    ("f", "()I", "getDegradeAfterUses"),
    ("g", "()I", "getDegradedCapacityPenalty"),
]:
    METHOD_NAME_MAP.setdefault((_ESSENCE_POUCH_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_RUNECRAFTING_ALTAR_DEFINITION_OWNER = "com/rs2/model/skill/runecrafting/RunecraftingAltarDefinition"
for _old_name, _mapped_name in [
    ("j", "AIR_ALTAR"),
    ("k", "MIND_ALTAR"),
    ("l", "WATER_ALTAR"),
    ("m", "EARTH_ALTAR"),
    ("n", "FIRE_ALTAR"),
    ("o", "BODY_ALTAR"),
    ("p", "COSMIC_ALTAR"),
    ("q", "CHAOS_ALTAR"),
    ("r", "NATURE_ALTAR"),
    ("s", "LAW_ALTAR"),
    ("t", "DEATH_ALTAR"),
    ("u", "BLOOD_ALTAR"),
    ("v", "SOUL_ALTAR"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _RUNECRAFTING_ALTAR_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/runecrafting/RunecraftingAltarDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "talismanItemId"),
    ("b", "I", "tiaraItemId"),
    ("c", "I", "ruinsObjectId"),
    ("d", "I", "altarObjectId"),
    ("e", "I", "portalObjectId"),
    ("f", "Lcom/rs2/model/Position;", "altarPosition"),
    ("g", "Lcom/rs2/model/Position;", "ruinsPosition"),
    ("h", "D", "tiaraExperience"),
    ("i", "Z", "membersOnly"),
    ("w", "[Lcom/rs2/model/skill/runecrafting/RunecraftingAltarDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_RUNECRAFTING_ALTAR_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getTalismanItemId"),
    ("b", "()I", "getTiaraItemId"),
    ("c", "()D", "getTiaraExperience"),
    ("d", "()I", "getAltarObjectId"),
]:
    METHOD_NAME_MAP.setdefault((_RUNECRAFTING_ALTAR_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_RUNECRAFTING_HANDLER_OWNER = "com/rs2/model/skill/runecrafting/RunecraftingHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/skill/runecrafting/RuneDefinition;)V", "craftRunesAtAltar"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "recordScryingOrbTeleport"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;)V", "startAbyssMageTeleport"),
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "locateTalismanDirection"),
]:
    METHOD_NAME_MAP.setdefault((_RUNECRAFTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "PURE_ESSENCE_ITEM_ID"),
    ("b", "[I", "SCRYING_ORB_NPC_IDS"),
]:
    FIELD_NAME_MAP.setdefault((_RUNECRAFTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

_RUNECRAFTING_OBJECT_HANDLER_OWNER = "com/rs2/model/skill/runecrafting/RunecraftingObjectHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;I)Z", "handleAltarOrAbyssObject"),
    ("b", "(Lcom/rs2/model/player/Player;I)Z", "handleRuinsOrPortalObject"),
    ("a", "(Lcom/rs2/model/player/Player;II)Z", "handleTalismanOnMysteriousRuins"),
    ("a", "(II)Z", "handleToolOnNpc"),
]:
    METHOD_NAME_MAP.setdefault((_RUNECRAFTING_OBJECT_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    ("b", "[I", "toolInteractionNpcIds"),
]:
    FIELD_NAME_MAP.setdefault((_RUNECRAFTING_OBJECT_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/player/Player;", "player"),
    (
        "b",
        "Lcom/rs2/model/skill/runecrafting/RunecraftingAltarDefinition;",
        "altarDefinition",
    ),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/runecrafting/MysteriousRuinsTeleportTask", _old_name, _descriptor),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("c", "(Lcom/rs2/model/player/Player;II)Z", "handleCombinationRunecrafting"),
    ("d", "(Lcom/rs2/model/player/Player;II)Z", "handleTiaraCrafting"),
    ("e", "(Lcom/rs2/model/player/Player;I)Z", "fillEssencePouch"),
    ("a", "(Lcom/rs2/model/Entity;I)I", "selectNextAvailableEssencePouch"),
]:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/GameplayHelper", _old_name, _descriptor), _mapped_name)

_GAMEPLAY_HELPER_CLEANUP_METHOD_NAMES = [
    ("a", "(Lcom/rs2/model/player/Player;)V", "resetBotTaskState"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "resetBotSkillsToBase"),
    ("a", "(Lcom/rs2/model/player/Player;Z)V", "selectAndStartProgressiveBotTask"),
    ("c", "(Lcom/rs2/model/player/Player;)V", "selectAndStartNextProgressiveBotTask"),
    ("a", "()V", "startDropPartyBotEvent"),
    ("a", "(Lcom/rs2/model/player/Player;I)V", "prepareBotCombatStyle"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Ljava/util/ArrayList;Z)Lcom/rs2/bot/BotTaskDefinition;",
        "selectAvailableBotTask",
    ),
    ("f", "(Lcom/rs2/model/player/Player;)V", "startBotTaskRoute"),
    ("b", "(I)Z", "isObjectDefinitionIdValid"),
    ("e", "()V", "loadObjectDefinitions"),
    ("a", "(Lcom/rs2/cache/CacheFile;)[B", "inflateGzipCacheFile"),
    ("c", "(I)I", "getNpcShopId"),
    ("b", "(Lcom/rs2/model/player/Player;I)Z", "openNpcShop"),
    ("g", "(Lcom/rs2/model/player/Player;)V", "refreshPlayerAreaOverlay"),
    ("h", "(Lcom/rs2/model/player/Player;)V", "refreshRubberChickenPlayerOption"),
    ("a", "(Lcom/rs2/model/player/Player;Ljava/lang/String;)V", "openProductionInterface"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;IIIIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
        "showSplitbarkProductionInterface",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;IIIIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
        "showFiveOptionProductionInterface",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;IIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
        "showThreeOptionProductionInterface",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;IILjava/lang/String;Ljava/lang/String;)V",
        "showTwoOptionProductionInterface",
    ),
    ("a", "(Lcom/rs2/model/player/Player;ILjava/lang/String;)V", "showOneOptionProductionInterface"),
    ("a", "(Ljava/lang/String;)Ljava/lang/String;", "getIndefiniteArticle"),
    ("a", "(Lcom/rs2/model/player/Player;III)Z", "handleAgilityObjectAction"),
    ("d", "(Lcom/rs2/model/player/Player;I)Z", "handleFlourDoughButton"),
    ("a", "(Lcom/rs2/model/player/Player;IIII)Z", "handleLeatherCraftingItemUse"),
    ("a", "(Lcom/rs2/model/player/Player;II)Z", "handleCraftedArmorButton"),
    ("i", "(Lcom/rs2/model/player/Player;)V", "openTanningInterface"),
    ("b", "(Lcom/rs2/model/player/Player;IIII)V", "tanHide"),
    ("b", "(Lcom/rs2/model/player/Player;II)Z", "handleFarmingPatchObjectAction"),
    ("f", "(Lcom/rs2/model/player/Player;I)V", "refreshRunecraftingTiaraConfig"),
    ("g", "(Lcom/rs2/model/player/Player;I)Z", "handleAnagramClueNpc"),
    ("e", "(I)I", "randomAnagramClueItemForLevel"),
    ("f", "()V", "loadNpcSpawns"),
    ("a", "(IIIII)V", "spawnNpc"),
    ("a", "(Lcom/rs2/model/npc/Npc;IIII)V", "spawnNonRespawningNpc"),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;Lcom/rs2/model/npc/Npc;ZZ)V",
        "spawnOwnedNpcAtPosition",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;ZZ)V",
        "spawnOwnedNpcAdjacentToPlayer",
    ),
    (
        "a",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;IIIIZZ)V",
        "spawnRoamingNpcFacingPlayer",
    ),
    ("b", "(Lcom/rs2/model/npc/Npc;IIII)V", "spawnNpcWithRemovalDelay"),
    (
        "b",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;IIIIZZ)Z",
        "replaceOwnedRoamingNpcAtPosition",
    ),
    (
        "c",
        "(Lcom/rs2/model/player/Player;Lcom/rs2/model/npc/Npc;IIIIZZ)V",
        "spawnOwnedGroundPlaneNpcAtPosition",
    ),
    ("i", "(Lcom/rs2/model/player/Player;I)Z", "hasActiveTemporaryNpc"),
    (
        "a",
        "(Lcom/rs2/model/Entity;Lcom/rs2/model/npc/Npc;Lcom/rs2/model/Position;ZLjava/lang/String;)V",
        "spawnNpcTargetingEntityAtPosition",
    ),
    ("a", "(Lcom/rs2/model/npc/Npc;)V", "unregisterTemporaryNpc"),
    ("l", "(Lcom/rs2/model/player/Player;)V", "sendNpcUpdatePacket"),
    ("a", "(Lcom/rs2/net/packet/PacketWriter;Lcom/rs2/model/npc/Npc;)V", "writeNpcUpdateBlock"),
    ("a", "(III)I", "calculatePercent"),
    ("e", "(Lcom/rs2/model/player/Player;II)V", "takeAxeFromLog"),
    ("m", "(Lcom/rs2/model/player/Player;)V", "withdrawCoalTruckCoal"),
    ("n", "(Lcom/rs2/model/player/Player;)V", "milkCow"),
    ("g", "()V", "loadGroundItemSpawns"),
    ("a", "(Lcom/rs2/model/player/Player;Lcom/rs2/model/Position;)Z", "castSelectedItemTeleport"),
    ("a", "(Ljava/lang/String;Ljava/lang/String;)V", "appendLogLine"),
]

for _old_name, _descriptor, _mapped_name in _GAMEPLAY_HELPER_CLEANUP_METHOD_NAMES:
    METHOD_NAME_MAP.setdefault(("com/rs2/model/GameplayHelper", _old_name, _descriptor), _mapped_name)

FIELD_NAME_MAP.setdefault(("com/rs2/model/GameplayHelper", "a", "I"), "objectDefinitionCount")

_SMITHING_BAR_DEFINITION_OWNER = "com/rs2/model/skill/smithing/SmithingBarDefinition"
for _old_name, _mapped_name in [
    ("f", "BRONZE_BAR"),
    ("g", "IRON_BAR"),
    ("h", "STEEL_BAR"),
    ("i", "MITHRIL_BAR"),
    ("j", "ADAMANT_BAR"),
    ("k", "RUNITE_BAR"),
]:
    FIELD_NAME_MAP.setdefault(
        (
            _SMITHING_BAR_DEFINITION_OWNER,
            _old_name,
            "Lcom/rs2/model/skill/smithing/SmithingBarDefinition;",
        ),
        _mapped_name,
    )

for _old_name, _descriptor, _mapped_name in [
    ("a", "[Lcom/rs2/model/skill/smithing/SmithingBarDefinition;", "VALUES"),
    ("l", "[Lcom/rs2/model/skill/smithing/SmithableItemDefinition;", "smithableItems"),
    ("m", "I", "requiredLevel"),
    ("n", "D", "experiencePerBar"),
    ("o", "I", "barItemId"),
    ("b", "[I", "productNameTextIds"),
    ("c", "[I", "barRequirementTextIds"),
    ("d", "[I", "productItemInterfaceIds"),
    ("e", "[I", "productItemSlots"),
]:
    FIELD_NAME_MAP.setdefault((_SMITHING_BAR_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "(I)Lcom/rs2/model/skill/smithing/SmithingBarDefinition;", "forBarItemId"),
    ("a", "()[Lcom/rs2/model/skill/smithing/SmithableItemDefinition;", "getSmithableItems"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()D", "getExperiencePerBar"),
    ("d", "()I", "getBarItemId"),
]:
    METHOD_NAME_MAP.setdefault((_SMITHING_BAR_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_SMITHABLE_ITEM_DEFINITION_OWNER = "com/rs2/model/skill/smithing/SmithableItemDefinition"
for _old_name, _descriptor, _mapped_name in [
    ("bH", "I", "productItemId"),
    ("bI", "I", "requiredLevel"),
    ("bJ", "I", "barCount"),
    ("bK", "Ljava/lang/String;", "displayName"),
    ("bL", "I", "outputAmount"),
    ("bM", "[Lcom/rs2/model/skill/smithing/SmithableItemDefinition;", "VALUES"),
]:
    FIELD_NAME_MAP.setdefault((_SMITHABLE_ITEM_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "()I", "getProductItemId"),
    ("b", "()I", "getRequiredLevel"),
    ("c", "()I", "getBarCount"),
    ("d", "()I", "getOutputAmount"),
    ("e", "()Ljava/lang/String;", "getDisplayName"),
]:
    METHOD_NAME_MAP.setdefault((_SMITHABLE_ITEM_DEFINITION_OWNER, _old_name, _descriptor), _mapped_name)

_SMELTING_HANDLER_OWNER = "com/rs2/model/skill/smithing/SmeltingHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;I)V", "handleOreOnFurnace"),
    ("a", "(Lcom/rs2/model/player/Player;)I", "selectBestBotSmeltingBarItemId"),
    ("a", "(I)I", "getCoalRequiredForBar"),
    ("b", "(Lcom/rs2/model/player/Player;I)V", "prepareBotSmeltingRequirements"),
    ("b", "(Lcom/rs2/model/player/Player;)V", "openSmeltingInterface"),
    ("a", "(Lcom/rs2/model/player/Player;II)Z", "handleSmeltingButton"),
    ("c", "(Lcom/rs2/model/player/Player;I)Z", "startSmeltingTask"),
]:
    METHOD_NAME_MAP.setdefault((_SMELTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "[[[I", "SMELTING_DEFINITIONS"),
    ("b", "[[I", "SMELTING_BUTTONS"),
]:
    FIELD_NAME_MAP.setdefault((_SMELTING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "I", "ticksUntilSmelt"),
    ("c", "Lcom/rs2/model/player/Player;", "player"),
    ("d", "I", "actionSequence"),
    ("e", "Lcom/rs2/model/item/ItemStack;", "primaryOre"),
    ("f", "Z", "hasSecondaryOre"),
    ("g", "Lcom/rs2/model/item/ItemStack;", "secondaryOre"),
    ("h", "I", "barItemId"),
    ("i", "Lcom/rs2/model/item/ItemStack;", "outputBar"),
    ("j", "I", "experience"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/smithing/SmeltingTask", _old_name, _descriptor),
        _mapped_name,
    )

_SMITHING_HANDLER_OWNER = "com/rs2/model/skill/smithing/SmithingHandler"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;I)V", "openSmithingInterface"),
    ("a", "(Lcom/rs2/model/player/Player;II)V", "startSmithingTask"),
]:
    METHOD_NAME_MAP.setdefault((_SMITHING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "Lcom/rs2/model/combat/attack/CombatAttack;", "attack"),
    ("b", "Lcom/rs2/model/combat/attack/CombatAttackState;", "attackState"),
]:
    FIELD_NAME_MAP.setdefault((_SMITHING_HANDLER_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _descriptor, _mapped_name in [
    ("a", "I", "remainingActions"),
    ("b", "Lcom/rs2/model/player/Player;", "player"),
    ("c", "I", "actionSequence"),
    ("d", "Lcom/rs2/model/item/ItemStack;", "requiredBars"),
    ("e", "I", "productItemId"),
    ("f", "Lcom/rs2/model/skill/smithing/SmithableItemDefinition;", "smithableItem"),
    ("g", "Lcom/rs2/model/skill/smithing/SmithingBarDefinition;", "barDefinition"),
    ("h", "Lcom/rs2/model/item/ItemStack;", "productItem"),
]:
    FIELD_NAME_MAP.setdefault(
        ("com/rs2/model/skill/smithing/SmithingTask", _old_name, _descriptor),
        _mapped_name,
    )

_DRAGON_SQUARE_SHIELD_SMITHING_OWNER = "com/rs2/model/skill/smithing/DragonSquareShieldSmithing"
for _old_name, _descriptor, _mapped_name in [
    ("a", "(Lcom/rs2/model/player/Player;II)Z", "handleItemOnAnvil"),
    ("a", "(Lcom/rs2/model/player/Player;)V", "forgeDragonSquareShield"),
]:
    METHOD_NAME_MAP.setdefault((_DRAGON_SQUARE_SHIELD_SMITHING_OWNER, _old_name, _descriptor), _mapped_name)

for _old_name, _mapped_name in [
    ("a", "LEFT_SHIELD_HALF_ITEM_ID"),
    ("b", "RIGHT_SHIELD_HALF_ITEM_ID"),
    ("c", "DRAGON_SQUARE_SHIELD_ITEM_ID"),
]:
    FIELD_NAME_MAP.setdefault((_DRAGON_SQUARE_SHIELD_SMITHING_OWNER, _old_name, "I"), _mapped_name)

_DRAGONFIRE_SHIELD_SMITHING_OWNER = "com/rs2/model/skill/smithing/DragonfireShieldSmithing"
METHOD_NAME_MAP.setdefault(
    (_DRAGONFIRE_SHIELD_SMITHING_OWNER, "a", "(Lcom/rs2/model/player/Player;II)Z"),
    "handleItemOnAnvil",
)

for _old_name, _mapped_name in [
    ("a", "ANTI_DRAGON_SHIELD_ITEM_ID"),
    ("b", "DRAGONFIRE_SHIELD_ITEM_ID"),
    ("c", "DRACONIC_VISAGE_ITEM_ID"),
]:
    FIELD_NAME_MAP.setdefault((_DRAGONFIRE_SHIELD_SMITHING_OWNER, _old_name, "I"), _mapped_name)


def remap_class_bytes(data: bytes) -> tuple[bytes, int, int, int]:
    if data[:4] != b"\xca\xfe\xba\xbe":
        return data, 0, 0, 0

    cp_count = struct.unpack_from(">H", data, 8)[0]
    pos = 10
    index = 1
    changed_utf8 = 0
    method_changes = 0
    field_changes = 0
    cp: list[dict[str, object] | None] = [None] * cp_count

    while index < cp_count:
        tag = data[pos]
        pos += 1

        if tag == 1:  # CONSTANT_Utf8
            length = struct.unpack_from(">H", data, pos)[0]
            pos += 2
            raw = data[pos : pos + length]
            pos += length
            try:
                text = raw.decode("utf-8")
            except UnicodeDecodeError:
                cp[index] = {"tag": tag, "raw": raw}
            else:
                new_text = remap_text(text)
                if new_text != text:
                    changed_utf8 += 1
                cp[index] = {"tag": tag, "text": new_text}
        elif tag in (3, 4):  # int, float
            cp[index] = {"tag": tag, "bytes": data[pos : pos + 4]}
            pos += 4
        elif tag in (5, 6):  # long, double
            cp[index] = {"tag": tag, "bytes": data[pos : pos + 8]}
            pos += 8
            index += 1
            cp[index] = None
        elif tag in (7, 8, 16, 19, 20):  # class, string, method type, module, package
            cp[index] = {"tag": tag, "index": struct.unpack_from(">H", data, pos)[0]}
            pos += 2
        elif tag in (9, 10, 11, 12, 17, 18):  # refs, name/type, dynamic, invoke dynamic
            cp[index] = {
                "tag": tag,
                "index1": struct.unpack_from(">H", data, pos)[0],
                "index2": struct.unpack_from(">H", data, pos + 2)[0],
            }
            pos += 4
        elif tag == 15:  # method handle
            cp[index] = {
                "tag": tag,
                "reference_kind": data[pos],
                "reference_index": struct.unpack_from(">H", data, pos + 1)[0],
            }
            pos += 3
        else:
            raise ValueError(f"unsupported constant-pool tag {tag} at index {index}")

        index += 1

    rest = bytearray(data[pos:])

    def utf8_text(idx: int) -> str:
        entry = cp[idx]
        if entry is None or entry["tag"] != 1 or "text" not in entry:
            raise ValueError(f"constant-pool entry {idx} is not a UTF-8 text entry")
        return str(entry["text"])

    def class_name(idx: int) -> str:
        entry = cp[idx]
        if entry is None or entry["tag"] != 7:
            raise ValueError(f"constant-pool entry {idx} is not a class entry")
        return utf8_text(int(entry["index"]))

    utf8_cache: dict[str, int] = {
        str(entry["text"]): idx
        for idx, entry in enumerate(cp)
        if entry is not None and entry["tag"] == 1 and "text" in entry
    }

    def ensure_utf8(text: str) -> int:
        existing = utf8_cache.get(text)
        if existing is not None:
            return existing
        cp.append({"tag": 1, "text": text})
        idx = len(cp) - 1
        utf8_cache[text] = idx
        return idx

    name_type_cache: dict[tuple[int, int], int] = {}
    for idx, entry in enumerate(cp):
        if entry is not None and entry["tag"] == 12:
            name_type_cache[(int(entry["index1"]), int(entry["index2"]))] = idx

    def ensure_name_and_type(name_index: int, descriptor_index: int) -> int:
        key = (name_index, descriptor_index)
        existing = name_type_cache.get(key)
        if existing is not None:
            return existing
        cp.append({"tag": 12, "index1": name_index, "index2": descriptor_index})
        idx = len(cp) - 1
        name_type_cache[key] = idx
        return idx

    for entry in cp:
        if entry is None or entry["tag"] not in (9, 10, 11):
            continue
        owner = class_name(int(entry["index1"]))
        name_type = cp[int(entry["index2"])]
        if name_type is None or name_type["tag"] != 12:
            continue
        name = utf8_text(int(name_type["index1"]))
        descriptor = utf8_text(int(name_type["index2"]))
        if entry["tag"] == 9:
            new_name = FIELD_NAME_MAP.get((owner, name, descriptor))
        else:
            new_name = METHOD_NAME_MAP.get((owner, name, descriptor))
        if new_name is None:
            continue
        new_name_index = ensure_utf8(new_name)
        entry["index2"] = ensure_name_and_type(new_name_index, int(name_type["index2"]))
        if entry["tag"] == 9:
            field_changes += 1
        else:
            method_changes += 1

    this_class = class_name(_read_u2(rest, 2))
    rest_pos = 8
    interfaces_count = _read_u2(rest, rest_pos - 2)
    rest_pos += interfaces_count * 2
    fields_count = _read_u2(rest, rest_pos)
    rest_pos += 2
    for _ in range(fields_count):
        name_offset = rest_pos + 2
        descriptor_offset = rest_pos + 4
        name_index = _read_u2(rest, name_offset)
        descriptor_index = _read_u2(rest, descriptor_offset)
        name = utf8_text(name_index)
        descriptor = utf8_text(descriptor_index)
        new_name = FIELD_NAME_MAP.get((this_class, name, descriptor))
        if new_name is not None:
            new_name_index = ensure_utf8(new_name)
            if new_name_index != name_index:
                _write_u2(rest, name_offset, new_name_index)
                field_changes += 1
        rest_pos = _skip_member_info(rest, rest_pos)
    methods_count = _read_u2(rest, rest_pos)
    rest_pos += 2
    for _ in range(methods_count):
        name_offset = rest_pos + 2
        descriptor_offset = rest_pos + 4
        name_index = _read_u2(rest, name_offset)
        descriptor_index = _read_u2(rest, descriptor_offset)
        name = utf8_text(name_index)
        descriptor = utf8_text(descriptor_index)
        new_name = METHOD_NAME_MAP.get((this_class, name, descriptor))
        if new_name is not None:
            new_name_index = ensure_utf8(new_name)
            if new_name_index != name_index:
                _write_u2(rest, name_offset, new_name_index)
                method_changes += 1
        rest_pos = _skip_member_info(rest, rest_pos)

    out = bytearray()
    out.extend(data[:8])
    out.extend(struct.pack(">H", len(cp)))
    index = 1
    while index < len(cp):
        entry = cp[index]
        if entry is None:
            index += 1
            continue
        tag = int(entry["tag"])
        out.append(tag)
        if tag == 1:
            raw = bytes(entry["raw"]) if "raw" in entry else str(entry["text"]).encode("utf-8")
            out.extend(struct.pack(">H", len(raw)))
            out.extend(raw)
        elif tag in (3, 4, 5, 6):
            out.extend(bytes(entry["bytes"]))
        elif tag in (7, 8, 16, 19, 20):
            out.extend(struct.pack(">H", int(entry["index"])))
        elif tag in (9, 10, 11, 12, 17, 18):
            out.extend(struct.pack(">HH", int(entry["index1"]), int(entry["index2"])))
        elif tag == 15:
            out.append(int(entry["reference_kind"]))
            out.extend(struct.pack(">H", int(entry["reference_index"])))
        else:
            raise ValueError(f"unsupported constant-pool tag {tag} while writing")
        index += 2 if tag in (5, 6) else 1
    out.extend(rest)
    return bytes(out), changed_utf8, method_changes, field_changes


def remap_manifest(data: bytes) -> bytes:
    text = data.decode("utf-8")
    dotted_map = {old.replace("/", "."): new.replace("/", ".") for old, new in EXACT_CLASS_MAP.items()}
    for old, new in sorted(dotted_map.items(), key=lambda item: len(item[0]), reverse=True):
        text = re.sub(re.escape(old) + r"(?![A-Za-z0-9_$.])", new, text)
    return text.encode("utf-8")


def copy_info(info: zipfile.ZipInfo, filename: str) -> zipfile.ZipInfo:
    new_info = zipfile.ZipInfo(filename, info.date_time)
    new_info.comment = info.comment
    new_info.extra = info.extra
    new_info.internal_attr = info.internal_attr
    new_info.external_attr = info.external_attr
    new_info.create_system = info.create_system
    new_info.compress_type = zipfile.ZIP_DEFLATED
    return new_info


def remap_jar(input_jar: Path, output_jar: Path) -> None:
    changed_classes = 0
    changed_utf8 = 0
    changed_methods = 0
    changed_fields = 0
    renamed_entries = 0
    seen: set[str] = set()

    with zipfile.ZipFile(input_jar, "r") as zin, zipfile.ZipFile(output_jar, "w", allowZip64=True) as zout:
        class_to_super: dict[str, str | None] = {}
        for info in zin.infolist():
            if not info.filename.endswith(".class"):
                continue
            this_class, super_class = read_class_hierarchy_names(zin.read(info.filename))
            class_to_super[this_class] = super_class
        populate_inherited_task_method_maps(class_to_super)

        for info in zin.infolist():
            data = zin.read(info.filename)
            new_name = remap_entry_name(info.filename)
            if new_name != info.filename:
                renamed_entries += 1
            if new_name in seen:
                raise ValueError(f"duplicate output jar entry after remap: {new_name}")
            seen.add(new_name)

            if info.filename.endswith(".class"):
                data, changed, method_changes, field_changes = remap_class_bytes(data)
                if changed or method_changes or field_changes:
                    changed_classes += 1
                    changed_utf8 += changed
                    changed_methods += method_changes
                    changed_fields += field_changes
            elif info.filename.upper() == "META-INF/MANIFEST.MF":
                data = remap_manifest(data)

            zout.writestr(copy_info(info, new_name), data)

    print(f"wrote {output_jar}")
    print(f"renamed jar entries: {renamed_entries}")
    print(f"classes with remapped constants: {changed_classes}")
    print(f"constant-pool strings changed: {changed_utf8}")
    print(f"owner-aware method references/definitions changed: {changed_methods}")
    print(f"owner-aware field references/definitions changed: {changed_fields}")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("input_jar", type=Path)
    parser.add_argument("output_jar", type=Path)
    args = parser.parse_args()
    remap_jar(args.input_jar, args.output_jar)


if __name__ == "__main__":
    main()
