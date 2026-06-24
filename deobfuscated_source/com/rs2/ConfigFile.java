/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.bot.BotPlayer;
import com.rs2.bot.BotTradeAdvertManager;
import com.rs2.bot.DropPartyBotManager;
import com.rs2.bot.combat.WildernessBotSettings;
import com.rs2.model.player.BankManager;
import com.rs2.model.skill.SkillManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public final class ConfigFile {
    private String key;
    private int parameterCount;
    private static ConfigFile[] definitions = new ConfigFile[]{new ConfigFile("LAN", 1), new ConfigFile("MYSQL_HISCORES", 1), new ConfigFile("P2P_WORLD", 1), new ConfigFile("CACHE_VERSION", 1), new ConfigFile("FIGHTCAVE_START_WAVE", 1), new ConfigFile("MEMBERS_REQ", 3), new ConfigFile("XP_RATE", 2), new ConfigFile("QUEST_XP_RATE", 1), new ConfigFile("BOT_XP_RATE", 2), new ConfigFile("BANK_SIZE", 2), new ConfigFile("IDLE_LOGOUT", 1), new ConfigFile("BACKUP_CHARACTERS", 1), new ConfigFile("WC_STYLE_MINING", 1), new ConfigFile("DYNAMIC_SHOP_PRICES", 1), new ConfigFile("SHOP_ITEM_MULTIPLIER", 1), new ConfigFile("SHOP_RESTOCK_TIME_MULTIPLIER", 1), new ConfigFile("ITEM_RESPAWN_DELAY_MULTIPLIER", 1), new ConfigFile("NPC_RESPAWN_DELAY_MULTIPLIER", 1), new ConfigFile("ITEM_DROP_RATES", 5), new ConfigFile("ITEMS_STAY_ON_GROUND_TIME", 1), new ConfigFile("HOLIDAY_ITEM_DROPS", 3), new ConfigFile("DISEASING", 1), new ConfigFile("BARROWS_REWARD_RATE", 1), new ConfigFile("NO_RANDOMS", 1), new ConfigFile("FORCE_RESET_BOTS", 1), new ConfigFile("BOT_ESCAPE_HIGH_LVLS", 1), new ConfigFile("HOTZONES_FOR_WILDYBOTS", 1), new ConfigFile("REMOVED_BOT_NAMES", 1), new ConfigFile("TRADE_BOT_COMMON_ITEM_CHANCE", 1), new ConfigFile("WILDY_BOTS", 6), new ConfigFile("SKILLING_BOTS", 2), new ConfigFile("PROGRESSIVE_BOTS", 3), new ConfigFile("TRADE_BOTS", 2), new ConfigFile("OTHER_BOTS", 1), new ConfigFile("CLANWAR_BOTS", 3), new ConfigFile("DROP_PARTY_CHANCE", 1), new ConfigFile("SCAMMER_CHANCE", 1), new ConfigFile("WALKING_BOTS", 1), new ConfigFile("BOT_LOGIN", 2), new ConfigFile("MOVEMENT_SYSTEM", 1), new ConfigFile("SKIP_REQS_FOR_MISSING_QUESTS", 1), new ConfigFile("RECOLOR_MISSING_QUESTS", 1), new ConfigFile("COMPLETE_MISSING_QUESTS", 1), new ConfigFile("RELOG_FROZEN_BOTS", 1), new ConfigFile("SHOW_SKILL_UNLOCKS", 1), new ConfigFile("MOD_2HS", 3), new ConfigFile("MAX_LVL", 2)};

    private ConfigFile(String string, int n) {
        this.key = string;
        this.parameterCount = n;
    }

    public static void applyConfigEntry(String string, String[] stringArray) {
        int n = 0;
        ConfigFile[] configFileArray = definitions;
        int n2 = definitions.length;
        int n3 = 0;
        while (n3 < n2) {
            ConfigFile configFile = configFileArray[n3];
            Object object = configFile;
            if (configFile.key.equals(string)) {
                object = configFile;
                if (stringArray.length == ((ConfigFile)object).parameterCount) {
                    object = string;
                    if (((String)object).equals("WILDY_BOTS")) {
                        ServerSettings.wildyBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                        WildernessBotSettings.wildyBotCount = Integer.parseInt(stringArray[1]);
                        ServerSettings.wildyBotsUseNewGeneration = Integer.parseInt(stringArray[2]) == 1;
                        ServerSettings.wildyBotsBaseCombatLevel = Integer.parseInt(stringArray[3]);
                        ServerSettings.wildyBotsCombatLevelSpread = Integer.parseInt(stringArray[4]);
                        ServerSettings.wildyBotsIgnoreCombatForDeepWilderness = Integer.parseInt(stringArray[5]) == 1;
                    } else if (((String)object).equals("CACHE_VERSION")) {
                        ServerSettings.cacheVersion = Integer.parseInt(stringArray[0]);
                        ServerSettings.clientBuild = ServerSettings.cacheVersion;
                        if (ServerSettings.cacheVersion <= 245) {
                            ServerSettings.placeholderObjectId = 2733;
                        }
                    } else if (((String)object).equals("MOVEMENT_SYSTEM")) {
                        n = Integer.parseInt(stringArray[0]);
                        n = n <= 0 ? 0 : 1;
                        ServerSettings.defaultMovementSystem = n;
                    } else if (((String)object).equals("DISEASING")) {
                        ServerSettings.diseasingEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("MAX_LVL")) {
                        n = Integer.parseInt(stringArray[0]);
                        if (n < 10) {
                            n = 10;
                        }
                        if (n > 135) {
                            n = 135;
                        }
                        ServerSettings.maxLevel = n;
                        ServerSettings.capXpAtMaxLevel = Integer.parseInt(stringArray[1]) == 1;
                        SkillManager.maxCombatLevel = SkillManager.getMaxCombatLevel();
                    } else if (((String)object).equals("BOT_LOGIN")) {
                        Server.botLoginBatchSize = Integer.parseInt(stringArray[0]);
                        Server.botLoginBatchIntervalTicks = Integer.parseInt(stringArray[1]);
                    } else if (((String)object).equals("SKILLING_BOTS")) {
                        ServerSettings.skillingBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.skillingBotCount = Integer.parseInt(stringArray[1]);
                    } else if (((String)object).equals("PROGRESSIVE_BOTS")) {
                        ServerSettings.progressiveBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.progressiveBotCount = Integer.parseInt(stringArray[1]);
                        ServerSettings.progressiveBotsPrioritizeExisting = Integer.parseInt(stringArray[2]) == 1;
                    } else if (((String)object).equals("TRADE_BOTS")) {
                        ServerSettings.tradeBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.tradeBotCount = Integer.parseInt(stringArray[1]);
                    } else if (((String)object).equals("OTHER_BOTS")) {
                        ServerSettings.otherBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("CLANWAR_BOTS")) {
                        ServerSettings.clanWarsBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.clanWarsTeamSize = Integer.parseInt(stringArray[1]);
                        ServerSettings.clanWarsEventChanceDivisor = Integer.parseInt(stringArray[2]);
                    } else if (((String)object).equals("CLUE_MERCHANT")) {
                        ServerSettings.clueMerchantEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.clueMerchantClueLevel = Integer.parseInt(stringArray[1]);
                        ServerSettings.clueMerchantPriceCoins = Integer.parseInt(stringArray[2]);
                    } else if (((String)object).equals("DROP_PARTY_CHANCE")) {
                        DropPartyBotManager.dropPartyChanceDivisor = Integer.parseInt(stringArray[0]);
                    } else if (((String)object).equals("SCAMMER_CHANCE")) {
                        BotTradeAdvertManager.scammerChanceDivisor = Integer.parseInt(stringArray[0]);
                    } else if (((String)object).equals("WALKING_BOTS")) {
                        ServerSettings.walkingBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("BOT_ESCAPE_HIGH_LVLS")) {
                        WildernessBotSettings.escapeHighLevelAttackersEnabled = Integer.parseInt(stringArray[0]) == 0;
                    } else if (((String)object).equals("NO_RANDOMS")) {
                        ServerSettings.randomEventsMode = Integer.parseInt(stringArray[0]);
                    } else if (((String)object).equals("P2P_WORLD")) {
                        ServerSettings.freeToPlayWorld = Integer.parseInt(stringArray[0]) == 0;
                    } else if (((String)object).equals("SHOW_SKILL_UNLOCKS")) {
                        ServerSettings.showSkillUnlocks = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("2007_CONTENT")) {
                        ServerSettings.content2007Enabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("FIGHTCAVE_START_WAVE")) {
                        n = Integer.parseInt(stringArray[0]);
                        if (n <= 0) {
                            n = 1;
                        }
                        if (n > 63) {
                            n = 63;
                        }
                        ServerSettings.fightCaveStartWaveZeroBased = n - 1;
                    } else if (((String)object).equals("MEMBERS_REQ")) {
                        ServerSettings.membershipRequirementMode = Integer.parseInt(stringArray[0]);
                        ServerSettings.membershipRequirementValue = Integer.parseInt(stringArray[1]);
                        ServerSettings.membershipDaysPerPurchase = Integer.parseInt(stringArray[2]);
                    } else if (((String)object).equals("HOLIDAY_ITEM_DROPS")) {
                        ServerSettings.holidayItemDropsEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.holidayDropSets = Integer.parseInt(stringArray[1]);
                        ServerSettings.holidayDropRespawnTicks = Integer.parseInt(stringArray[2]);
                    } else if (((String)object).equals("SHOP_ITEM_MULTIPLIER")) {
                        ServerSettings.shopItemMultiplier = Double.parseDouble(stringArray[0]);
                    } else if (((String)object).equals("SHOP_RESTOCK_TIME_MULTIPLIER")) {
                        ServerSettings.shopRestockTimeMultiplier = Double.parseDouble(stringArray[0]);
                    } else if (((String)object).equals("ITEM_RESPAWN_DELAY_MULTIPLIER")) {
                        ServerSettings.itemRespawnDelayMultiplier = Double.parseDouble(stringArray[0]);
                    } else if (((String)object).equals("NPC_RESPAWN_DELAY_MULTIPLIER")) {
                        ServerSettings.npcRespawnDelayMultiplier = Double.parseDouble(stringArray[0]);
                    } else if (((String)object).equals("ITEMS_STAY_ON_GROUND_TIME")) {
                        ServerSettings.groundItemLifetimeSeconds = Integer.parseInt(stringArray[0]);
                    } else if (((String)object).equals("ITEM_DROP_RATES")) {
                        ServerSettings.commonDropRateMultiplier = Double.parseDouble(stringArray[0]);
                        ServerSettings.uncommonDropRateMultiplier = Double.parseDouble(stringArray[1]);
                        ServerSettings.rareDropRateMultiplier = Double.parseDouble(stringArray[2]);
                        ServerSettings.veryRareDropRateMultiplier = Double.parseDouble(stringArray[3]);
                        ServerSettings.dropRateCap = Integer.parseInt(stringArray[4]);
                        if (ServerSettings.commonDropRateMultiplier != 1.0 || ServerSettings.uncommonDropRateMultiplier != 1.0 || ServerSettings.rareDropRateMultiplier != 1.0 || ServerSettings.veryRareDropRateMultiplier != 1.0 || ServerSettings.dropRateCap > 0) {
                            ServerSettings.customDropRatesEnabled = true;
                        }
                    } else if (((String)object).equals("XP_RATE")) {
                        ServerSettings.xpRate = Double.parseDouble(stringArray[0]);
                        ServerSettings.progressiveXpMode = Integer.parseInt(stringArray[1]);
                    } else if (((String)object).equals("QUEST_XP_RATE")) {
                        ServerSettings.questXpRate = Double.parseDouble(stringArray[0]);
                    } else if (((String)object).equals("BOT_XP_RATE")) {
                        ServerSettings.botXpRateMultiplier = Double.parseDouble(stringArray[0]);
                        ServerSettings.botXpRateMode = Integer.parseInt(stringArray[1]);
                    } else if (((String)object).equals("MOD_2HS")) {
                        ServerSettings.mod2hsEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.mod2hsAttackBonusRate = Double.parseDouble(stringArray[1]);
                        ServerSettings.mod2hsStrengthBonusRate = Double.parseDouble(stringArray[2]);
                    } else if (((String)object).equals("BARROWS_REWARD_RATE")) {
                        ServerSettings.barrowsRewardRate = Double.parseDouble(stringArray[0]);
                    } else if (((String)object).equals("IDLE_LOGOUT")) {
                        ServerSettings.idleLogoutEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("MYSQL_HISCORES")) {
                        ServerSettings.mysqlHiscoresEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("BACKUP_CHARACTERS")) {
                        ServerSettings.backupCharactersEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("WC_STYLE_MINING")) {
                        ServerSettings.wcStyleMiningEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("RELOG_FROZEN_BOTS")) {
                        ServerSettings.relogFrozenBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("WILDERNESS_SLAYER")) {
                        ServerSettings.wildernessSlayerEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("DYNAMIC_SHOP_PRICES")) {
                        ServerSettings.dynamicShopPricesEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("GRAND_EXCHANGE")) {
                        ServerSettings.grandExchangeEnabled = Integer.parseInt(stringArray[0]) == 1;
                        ServerSettings.grandExchangeServerOfferCount = Integer.parseInt(stringArray[1]);
                        ServerSettings.instantGrandExchangeEnabled = Integer.parseInt(stringArray[2]) == 1;
                        ServerSettings.instantGrandExchangePriceFluctuationEnabled = Integer.parseInt(stringArray[3]) == 1;
                    } else if (((String)object).equals("HOTZONES_FOR_WILDYBOTS")) {
                        ServerSettings.hotzonesForWildyBotsEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("REMOVED_BOT_NAMES")) {
                        String[] stringArray2 = stringArray[0].split(",");
                        if (stringArray2 != null) {
                            int n4 = 0;
                            while (n4 < stringArray2.length) {
                                if (stringArray2[n4] != null) {
                                    BotPlayer.removedBotNames.add(stringArray2[n4].toLowerCase());
                                }
                                ++n4;
                            }
                        }
                    } else if (((String)object).equals("FORCE_RESET_BOTS")) {
                        String[] stringArray3 = stringArray[0].split(",");
                        if (stringArray3 != null) {
                            int n5 = 0;
                            while (n5 < stringArray3.length) {
                                if (stringArray3[n5] != null) {
                                    BotPlayer.forceResetBotNames.add(stringArray3[n5].toLowerCase());
                                }
                                ++n5;
                            }
                        }
                    } else if (((String)object).equals("LAN")) {
                        ServerSettings.lanConnectionsEnabled = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("SKIP_REQS_FOR_MISSING_QUESTS")) {
                        ServerSettings.skipRequirementsForMissingQuests = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("RECOLOR_MISSING_QUESTS")) {
                        ServerSettings.recolorMissingQuests = Integer.parseInt(stringArray[0]) == 1;
                    } else if (((String)object).equals("COMPLETE_MISSING_QUESTS")) {
                        ServerSettings.completeMissingQuestsMode = Integer.parseInt(stringArray[0]);
                    } else if (((String)object).equals("TRADE_BOT_COMMON_ITEM_CHANCE")) {
                        n = Integer.parseInt(stringArray[0]);
                        if (n < 0) {
                            n = 0;
                        }
                        if (n > 100) {
                            n = 100;
                        }
                        BotTradeAdvertManager.commonItemChancePercent = n;
                    } else if (((String)object).equals("BANK_SIZE")) {
                        int n6;
                        n = Integer.parseInt(stringArray[0]);
                        if (n <= 0) {
                            n = 1;
                        }
                        if (n > 288) {
                            n = 288;
                        }
                        if ((n6 = Integer.parseInt(stringArray[1])) < n) {
                            n6 = n;
                        }
                        if (n6 <= 0) {
                            n6 = 1;
                        }
                        if (n6 > 288) {
                            n6 = 288;
                        }
                        BankManager.freeBankCapacity = n;
                        BankManager.memberBankCapacity = n6;
                    }
                    n = 1;
                    break;
                }
                System.out.println("Invalid params for config: " + string);
                n = 1;
                break;
            }
            ++n3;
        }
        if (n == 0) {
            System.out.println("Invalid config: " + string);
        }
    }

    private static void writeConfigLine(BufferedWriter bufferedWriter, String string) throws IOException {
        bufferedWriter.write(string);
        bufferedWriter.newLine();
    }

    public static void writeDefaultConfig() {
        block13: {
            BufferedWriter bufferedWriter = null;
            Serializable serializable = new File("./Config.cfg");
            ((File)serializable).delete();
            try {
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter("./Config.cfg", true));
                    ConfigFile.writeConfigLine(bufferedWriter, "//CACHE_VERSION - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = cache version");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[CACHE_VERSION];289");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//P2P_WORLD - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = f2p world, 1 = p2p world");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[P2P_WORLD];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//MEMBERS_REQ - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1st param:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = every1 f2p, 1 = every1 p2p (2nd param is ignored)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//2 = total lvl req, 3 = quest point req (2nd param is the amount)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//4 = Hans will sell membership  (2nd param is the price)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//3rd param:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = days of membership you get each time you buy, put -1 for permanent member status");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[MEMBERS_REQ];1;1000000;-1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//MYSQL_HISCORES - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Note: for this to work you need to first download and start the website before starting the server");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[MYSQL_HISCORES];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//WILDY_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = amount of bots");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = old generation system, 1 = new generation system (NOTE: new system mostly generates mains and not pures! + also used for clanwar bots)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = base combatlvl (new generation system)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = +- of base combatlvl (new generation system)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = deeper wildy wandering based on combatlvl, 1 = ignore combatlvl for deep wildy wandering (new generation system)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[WILDY_BOTS];1;30;0;50;5;0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//HOTZONES_FOR_WILDYBOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Note: can cause lots of bot fights in same time which can lead to lagging!");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[HOTZONES_FOR_WILDYBOTS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//SKILLING_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = amount of bots");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[SKILLING_BOTS];1;50");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//PROGRESSIVE_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = amount of bots");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = random bots, 1 = prioritize existing bots");
                    ConfigFile.writeConfigLine(bufferedWriter, "//NOTE: Enabling progressive bots disables skilling bots");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[PROGRESSIVE_BOTS];0;10;0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//TRADE_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = amount of bots");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[TRADE_BOTS];1;50");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//OTHER_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Other bots adds bots that simulate online experience including scammers and drop parties");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[OTHER_BOTS];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//CLANWAR_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = team size");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = Chance of event start 1/# (checked every 5mins)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Clanwars event area is in deepwild near greater demons");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[CLANWAR_BOTS];0;20;24");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//DROP_PARTY_CHANCE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = chance for a drop party to trigger");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Drop party event is checked every 5mins");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[DROP_PARTY_CHANCE];24");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//SCAMMER_CHANCE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = chance for a trade bot to be a scammer");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[SCAMMER_CHANCE];25");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//WALKING_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Makes skill bots walk between towns instead of teleporting");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[WALKING_BOTS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//BOT_ESCAPE_HIGH_LVLS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Makes pkbots escape if the attacker is much higher lvl");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[BOT_ESCAPE_HIGH_LVLS];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//XP_RATE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = XP rate for normal xp gain (progression speed on progressive xp rate)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = Progressive xp mode; 0 = fixed rate; 1 = slow first (2% increase at speed 1), higher later (20% increase at speed 1); 2 = 10% increase after every lvl at speed 1");
                    ConfigFile.writeConfigLine(bufferedWriter, "//in progressive xp mode, the xp rate increases after every lvl (each skill has its own xp rate)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[XP_RATE];1.0;0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//QUEST_XP_RATE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = XP rate for quest xp gain");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[QUEST_XP_RATE];1.0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//BOT_XP_RATE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1st param: # = XP rate multiplier, multiples the xp rate defined in XP_RATE config");
                    ConfigFile.writeConfigLine(bufferedWriter, "//2nd param: 1 = apply only to real bots, 2 = apply to real bots and players using ::bot");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[BOT_XP_RATE];1.0;1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//BANK_SIZE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = F2P Bank size, max: 288");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = P2P Bank size, max: 288");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[BANK_SIZE];64;288");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//IDLE_LOGOUT - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[IDLE_LOGOUT];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//TRADE_BOT_COMMON_ITEM_CHANCE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = (0-100) chance for a trade bot to pick item from most used items (also affects GE offers)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[TRADE_BOT_COMMON_ITEM_CHANCE];90");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//BACKUP_CHARACTERS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//creates hourly backups from playerfiles");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[BACKUP_CHARACTERS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//LAN - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//allows lan connections if enabled");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[LAN];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//WC_STYLE_MINING - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//makes it possible to mine more than 1 ore per rock");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[WC_STYLE_MINING];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//REMOVED_BOT_NAMES - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//list names you dont want bots to use (current ones are just example usage)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[REMOVED_BOT_NAMES];bot 1,bot 2");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//FORCE_RESET_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//list bots that are frozen and the autofixes are not working (current ones are just example usage)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//the listed bots will be teled to lumbridge and given new objectives on their next login");
                    ConfigFile.writeConfigLine(bufferedWriter, "//remember to remove the names afterwards so they wont reset every login");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[FORCE_RESET_BOTS];bot 1,bot 2");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//SHOP_RESTOCK_TIME_MULTIPLIER - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = multiplier for shop shop restock time (lower value is faster)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[SHOP_RESTOCK_TIME_MULTIPLIER];1.0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//SHOP_ITEM_MULTIPLIER - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = multiplier for shop items");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[SHOP_ITEM_MULTIPLIER];1.0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//ITEM_RESPAWN_DELAY_MULTIPLIER - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = multiplier for item respawn time");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[ITEM_RESPAWN_DELAY_MULTIPLIER];1.0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//NPC_RESPAWN_DELAY_MULTIPLIER - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = multiplier for npc respawn time");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[NPC_RESPAWN_DELAY_MULTIPLIER];1.0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//ITEMS_STAY_ON_GROUND_TIME - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = amount of seconds dropped items stay on ground");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[ITEMS_STAY_ON_GROUND_TIME];200");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//ITEM_DROP_RATES - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = rate of common items");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = rate of uncommon items");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = rate of rare items");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = rate of very rare items");
                    ConfigFile.writeConfigLine(bufferedWriter, "//Drop rate cap, 0 = disabled; cap drop rate to 1/#, for example setting this to 512, would change the drop rate for any drop rarer than that to 1/512");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[ITEM_DROP_RATES];1.0;1.0;1.0;1.0;0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//DYNAMIC_SHOP_PRICES - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//item price changes depending on item amount compared to full stock");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[DYNAMIC_SHOP_PRICES];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//FIGHTCAVE_START_WAVE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = starting wave for fightcave minigame (1 - 63)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[FIGHTCAVE_START_WAVE];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//HOLIDAY_ITEM_DROPS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = sets to drop");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = time before spawning new sets (100 = 1 min)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[HOLIDAY_ITEM_DROPS];1;50;500");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//DISEASING - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[DISEASING];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//BARROWS_REWARD_RATE - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = reward chance rate");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[BARROWS_REWARD_RATE];1.0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//NO_RANDOMS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = all random events enabled, 1 = skilling random events disabled, 2 = all random events disabled");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[NO_RANDOMS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//SKIP_REQS_FOR_MISSING_QUESTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[SKIP_REQS_FOR_MISSING_QUESTS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//RECOLOR_MISSING_QUESTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[RECOLOR_MISSING_QUESTS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//COMPLETE_MISSING_QUESTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = disable, 1 = always, 2 = after all available quests are completed");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[COMPLETE_MISSING_QUESTS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//BOT_LOGIN - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = amount of bots to login at once");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = ticks to wait before logging in the next group of bots");
                    ConfigFile.writeConfigLine(bufferedWriter, "//if u see bunch of bots stuck at lumby castle, you can try fixing it by slowing down the bot login process");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[BOT_LOGIN];150;5");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//RELOG_FROZEN_BOTS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//if skilling/progressive bot has been detected to being frozen for 5mins, attempt to fix by relogging");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[RELOG_FROZEN_BOTS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//SHOW_SKILL_UNLOCKS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//show new unlocked stuff after lvlup");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[SHOW_SKILL_UNLOCKS];0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//MOVEMENT_SYSTEM - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = default movement system that u get set to everytime u login (can be changed using ::ms)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//0 = can have slight nuisances at times, but no gamebreaking issues");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = works better overall, but can break stuff (Known issues: Dagannoth doors)");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[MOVEMENT_SYSTEM];1");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//MOD_2HS - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//1 = enable, 0 = disable");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = atk bonus rate");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = str bonus rate");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[MOD_2HS];0;1.2;1.2");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "//MAX_LVL - Parameters for customization:");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = max lvl (10-135)");
                    ConfigFile.writeConfigLine(bufferedWriter, "//# = Cap xp at max lvl [1 = enable, 0 = disable]");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    ConfigFile.writeConfigLine(bufferedWriter, "[MAX_LVL];99;0");
                    ConfigFile.writeConfigLine(bufferedWriter, "");
                    bufferedWriter.flush();
                }
                catch (IOException iOException) {
                    serializable = iOException;
                    iOException.printStackTrace();
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                            return;
                        }
                        catch (IOException iOException2) {
                            return;
                        }
                    }
                    break block13;
                }
            }
            catch (Throwable throwable) {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    }
                    catch (IOException iOException) {}
                }
                throw throwable;
            }
            try {
                bufferedWriter.close();
                return;
            }
            catch (IOException iOException) {}
        }
    }
}

