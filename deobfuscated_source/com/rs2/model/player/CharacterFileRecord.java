/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.CharacterFileBankTab;
import com.rs2.model.quest.QuestDefinition;
import java.util.ArrayList;

public final class CharacterFileRecord {
    public String username;
    public String password;
    public String hostAddress = "0.0.0.0";
    public String reservedVersion11String = "";
    public int playerRights;
    public String legacyProfileString = "";
    public String profileString1 = "";
    public String profileString2 = "";
    public long lastSavedMillis;
    public long totalPlayTimeMillis;
    public long createdAtMillis;
    public boolean loginRestrictionExempt;
    public boolean memberFlag;
    public int donatorPoints;
    public int petUnlockFlags;
    public int x = 3093;
    public int y = 3104;
    public int plane = 0;
    public int skeletonSkinUnlocked = 0;
    public int gender;
    public int npcKillCount;
    public int playerKillCount;
    public int deathCount;
    public int easyCluesCompleted;
    public int mediumCluesCompleted;
    public int hardCluesCompleted;
    public int soldItemsValue;
    public int boughtItemsValue;
    public int duelWins;
    public int duelLosses;
    public int legacyQuestPoints;
    public boolean autoRetaliate;
    public int fightMode;
    public int brightness = 2;
    public int mouseButtons;
    public int publicChatEffects = 1;
    public int splitPrivateChat;
    public int acceptAid;
    public int musicVolume;
    public int effectVolume;
    public int specialEnergy = 100;
    public boolean changingBankPin;
    public boolean deletingBankPin;
    public int pinAppendYear = -1;
    public int pinAppendDate = -1;
    public int bindingNecklaceCharge = 15;
    public int ringOfForgingLife = 140;
    public int ringOfRecoilLife = 40;
    public int skullTimer;
    public int runEnergyRaw = 10000;
    public boolean running;
    public int abyssMageNpcId;
    public long muteExpires;
    public long banExpires;
    public int barrowsKillCount;
    public int barrowsTargetBrotherIndex;
    public int poisonImmunityTicks;
    public int antifireTicks;
    public int teleblockTicks;
    public double poisonDamage;
    public int slayerMasterId;
    public String slayerTaskName = "";
    public int slayerTaskAmount;
    public boolean usingAncients;
    public boolean brimhavenOpen;
    public boolean killedClueAttacker;
    public int gangAffiliation;
    public int piratesTreasureBananaCrateCount;
    public boolean treasureTrailNavigationTaught;
    public int coalTruckAmount;
    public int treasureTrailStepCount;
    public boolean cluePuzzleSolved;
    public int[] currentPin = new int[]{-1, -1, -1, -1};
    public int[] pendingPin = new int[]{-1, -1, -1, -1};
    public int[] essencePouchAmounts = new int[4];
    public int[] appearanceParts = new int[7];
    public int[] appearanceColors = new int[5];
    public ItemStack[] inventoryItems = new ItemStack[28];
    public ItemStack[] equipmentItems = new ItemStack[14];
    public long[] friendsList = new long[200];
    public long[] ignoreList = new long[100];
    public int[] queuedLoginItemIds = new int[28];
    public int[] queuedLoginItemAmounts = new int[28];
    public boolean[] barrowsKilledBrothers = new boolean[6];
    public int[] allotmentGrowthStages = new int[8];
    public int[] allotmentCropIds = new int[8];
    public int[] allotmentHarvestAmounts = new int[8];
    public int[] allotmentPatchStates = new int[8];
    public long[] allotmentLastUpdateTicks = new long[8];
    public double[] allotmentDiseaseChanceMultipliers = new double[8];
    public boolean[] allotmentProtectionFlags = new boolean[8];
    public int[] bushGrowthStages = new int[4];
    public int[] bushCropIds = new int[4];
    public int[] bushPatchStates = new int[4];
    public long[] bushLastUpdateTicks = new long[4];
    public double[] bushDiseaseChanceMultipliers = new double[4];
    public boolean[] bushSavedFlags = new boolean[4];
    public int[] flowerGrowthStages = new int[4];
    public int[] flowerCropIds = new int[4];
    public int[] flowerPatchStates = new int[4];
    public long[] flowerLastUpdateTicks = new long[4];
    public double[] flowerDiseaseChanceMultipliers = new double[4];
    public int[] fruitTreeGrowthStages = new int[4];
    public int[] fruitTreeIds = new int[4];
    public int[] fruitTreePatchStates = new int[4];
    public long[] fruitTreeLastUpdateTicks = new long[4];
    public double[] fruitTreeDiseaseChanceMultipliers = new double[4];
    public boolean[] fruitTreeSavedFlags = new boolean[4];
    public int[] herbGrowthStages = new int[4];
    public int[] herbCropIds = new int[4];
    public int[] herbHarvestAmounts = new int[4];
    public int[] herbPatchStates = new int[4];
    public long[] herbLastUpdateTicks = new long[4];
    public double[] herbDiseaseChanceMultipliers = new double[4];
    public int[] hopsGrowthStages = new int[4];
    public int[] hopsCropIds = new int[4];
    public int[] hopsHarvestAmounts = new int[4];
    public int[] hopsPatchStates = new int[4];
    public long[] hopsLastUpdateTicks = new long[4];
    public double[] hopsDiseaseChanceMultipliers = new double[4];
    public boolean[] hopsProtectionFlags = new boolean[4];
    public int[] specialTreeGrowthStages = new int[4];
    public int[] specialTreeIds = new int[4];
    public int[] specialTreePatchStates = new int[4];
    public long[] specialTreeLastUpdateTicks = new long[4];
    public double[] specialTreeDiseaseChanceMultipliers = new double[4];
    public int[] specialCropGrowthStages = new int[4];
    public int[] specialCropIds = new int[4];
    public int[] specialCropPatchStates = new int[4];
    public long[] specialCropLastUpdateTicks = new long[4];
    public double[] specialCropDiseaseChanceMultipliers = new double[4];
    public int[] treeGrowthStages = new int[4];
    public int[] treeIds = new int[4];
    public int[] treePatchData = new int[4];
    public int[] treePatchStates = new int[4];
    public long[] treeLastUpdateTicks = new long[4];
    public double[] treeDiseaseChanceMultipliers = new double[4];
    public boolean[] treeSavedFlags = new boolean[4];
    public int[] compostBinStates = new int[4];
    public long[] compostBinLastUpdateTicks = new long[4];
    public int[] compostBinItemIds = new int[4];
    public int[] farmingToolStoreAmounts = new int[18];
    public int[] configStates = new int[2000];
    public int[] questProgress = new int[QuestDefinition.questStateCapacity];
    public int[] questBitFlags = new int[QuestDefinition.questStateCapacity];
    public int[] questHookStates = new int[100];
    public int[] currentLevels = new int[22];
    public long[] skillExperience = new long[22];
    public boolean barrowsDoorPuzzleSolved;
    public boolean barrowsChestOpened;
    private int cachedItemValue = 0;
    public int barrowsRewardPotential = 0;
    public boolean[] grandExchangeSellOfferFlags = new boolean[6];
    public int[] grandExchangeItemIds = new int[6];
    public int[] grandExchangeQuantities = new int[6];
    public int[] grandExchangeUnitPrices = new int[6];
    public boolean[] grandExchangeCancelledFlags = new boolean[6];
    public int[] grandExchangeCompletedQuantities = new int[6];
    public int[] grandExchangeTotalPrices = new int[6];
    public int[] grandExchangePrimaryCollectAmounts = new int[6];
    public int[] grandExchangeSecondaryCollectAmounts = new int[6];
    public boolean[] grandExchangeFinishMessagePending = new boolean[6];
    public int flourMillHopperGrainCount = 0;
    public int questRandomSeed;
    public int publicChatMode = 0;
    public int privateChatMode = 0;
    public int tradeMode = 0;
    public int ck = 0;
    public long cl = 0L;
    public int cm = 0;
    public int cn = 0;
    public int familyCrestGauntletItemId = 778;
    public int mageArenaFlamesOfZamorakCastsRemaining = 100;
    public int mageArenaSaradominStrikeCastsRemaining = 100;
    public int mageArenaClawsOfGuthixCastsRemaining = 100;
    public int mageArenaProgressStage = 0;
    public int telekineticPizazzPoints = 0;
    public int enchantmentPizazzPoints = 0;
    public int alchemistPizazzPoints = 0;
    public int graveyardPizazzPoints = 0;
    public boolean bonesToPeachesUnlocked = false;
    public int telekineticMazeIndex = 0;
    public boolean telekineticMazeSolved = false;
    public int telekineticConsecutiveMazesSolved = 0;
    public ArrayList bankTabs = new ArrayList();
    private int dF = 10;
    public int gameMode = 0;
    public int barrowsRunsCompleted = 0;
    public int cE = -1;
    public int cF = -1;
    public int cG = -1;
    public int cH = -1;
    public int botCompletionItemAmount = -1;
    public int botCompletionItemId = -1;
    public byte cK = (byte)-1;
    public byte cL = (byte)-1;
    public byte cM = (byte)-1;
    public byte cN = (byte)-1;
    public byte botSkillTargetLevel = (byte)-1;
    public byte botSkillTargetSkillId = (byte)-1;
    public byte botCombatStyle = (byte)-1;
    public ArrayList botCombatLoadoutItemIds = new ArrayList();
    public ArrayList botShopSellItemIds;
    public int botShopItemAmount;
    public int botTaskItemId;
    public byte botShopBuyMode;
    public int tradeAdvertLastOfferAmount;
    public boolean tradeAdvertVariableQuantity;
    public boolean tradeAdvertScam;
    public int tradeAdvertUnitPrice;
    public int tradeAdvertQuantityRemaining;
    public int botAdvertItemId;
    public byte tradeAdvertMode;
    public byte savedWorldRouteIndex;
    public byte botTaskDurationMinutes;
    public long botTaskSavedElapsedMillis;
    public boolean savedWorldRouteReversed;
    public byte botPathWaypointIndex;
    public byte botPathSegmentIndex;
    public int botFoodItemId;
    public ItemStack[] botTaskRequiredItems;
    public String botTaskState;
    public int deferredBotTaskIndex;
    public byte deferredBotTaskTypeId;
    public int currentBotTaskIndex;
    public byte currentBotTaskTypeId;
    public byte botMode;
    public boolean botEnabled;
    public boolean botTaskReturnToBankRequested;
    public int botElementalSpellIndex;
    public int[] godWarsKillCounts;
    public long godWarsLastAltarBlessingMillis;
    public byte craftingThreadUseCount;
    public int dx;
    public long membershipExpiresMillis;
    public int savedCacheVersion;
    public int godBookPageFlags;
    public boolean swampCaveRopeAttached;
    public boolean lampOilStillFilled;
    public int enterTheAbyssMiniquestState;
    private static final int[] experienceForLevel;

    static {
        int[][][] nArrayArray = new int[][][]{new int[][]{new int[5], {11, 15, 15, 5, 7}}, new int[][]{new int[5], {11, 15, 15, 5, 7}}};
        int[][][] nArrayArray2 = new int[2][][];
        int[][] nArrayArray3 = new int[2][];
        int[] nArray = new int[7];
        nArray[0] = 18;
        nArray[1] = 26;
        nArray[2] = 36;
        nArray[4] = 33;
        nArray[5] = 42;
        nArray[6] = 10;
        nArrayArray3[0] = nArray;
        nArrayArray3[1] = new int[]{25, 31, 40, 8, 34, 43, 17};
        nArrayArray2[0] = nArrayArray3;
        nArrayArray2[1] = new int[][]{{56, 61, 70, 45, 67, 79, -1}, {60, 65, 77, 54, 68, 80, -1}};
        experienceForLevel = new int[100];
        int n = 0;
        int n2 = 1;
        while (n2 <= 99) {
            int n3;
            n = (int)((double)n + Math.floor((double)n2 + 300.0 * Math.pow(2.0, (double)n2 / 7.0)));
            CharacterFileRecord.experienceForLevel[n2] = n3 = (int)Math.floor(n / 4);
            ++n2;
        }
    }

    public CharacterFileRecord() {
        new ArrayList();
        this.botShopSellItemIds = new ArrayList();
        this.botShopItemAmount = -1;
        this.botTaskItemId = -1;
        this.botShopBuyMode = (byte)-1;
        this.tradeAdvertLastOfferAmount = -1;
        this.tradeAdvertVariableQuantity = false;
        this.tradeAdvertScam = false;
        this.tradeAdvertUnitPrice = -1;
        this.tradeAdvertQuantityRemaining = -1;
        this.botAdvertItemId = -1;
        this.tradeAdvertMode = (byte)-1;
        this.savedWorldRouteIndex = (byte)-1;
        this.botTaskDurationMinutes = 0;
        this.botTaskSavedElapsedMillis = 0L;
        this.savedWorldRouteReversed = false;
        this.botPathWaypointIndex = (byte)-1;
        this.botPathSegmentIndex = (byte)-1;
        this.botFoodItemId = -1;
        this.deferredBotTaskIndex = -1;
        this.deferredBotTaskTypeId = (byte)-1;
        this.currentBotTaskIndex = -1;
        this.currentBotTaskTypeId = (byte)-1;
        this.botMode = (byte)-1;
        this.botEnabled = false;
        this.botTaskReturnToBankRequested = false;
        this.botElementalSpellIndex = -1;
        this.godWarsKillCounts = new int[4];
        this.craftingThreadUseCount = 0;
        this.dx = 0;
        this.swampCaveRopeAttached = false;
        this.lampOilStillFilled = false;
        this.enterTheAbyssMiniquestState = 0;
        this.bankTabs.add(new CharacterFileBankTab(0));
    }

    public final void setBankTabItem(int n, ItemStack itemStack, int n2) {
        try {
            if (n2 > this.dF - 1) {
                n2 = this.dF - 1;
            }
            if (n2 > this.bankTabs.size() - 1) {
                this.bankTabs.add(new CharacterFileBankTab(0));
            }
            CharacterFileBankTab characterFileBankTab = (CharacterFileBankTab)this.bankTabs.get(n2);
            if (n > characterFileBankTab.items.size() - 1) {
                characterFileBankTab = (CharacterFileBankTab)this.bankTabs.get(n2);
                characterFileBankTab.items.add(itemStack);
                return;
            }
            characterFileBankTab = (CharacterFileBankTab)this.bankTabs.get(n2);
            characterFileBankTab.items.set(n, itemStack);
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    public final int getStoredItemValue() {
        if (this.cachedItemValue == 0) {
            ItemDefinition itemDefinition;
            Object object;
            int n;
            int n2 = 0;
            while (n2 < this.bankTabs.size()) {
                Object object2 = (CharacterFileBankTab)this.bankTabs.get(n2);
                object2 = ((CharacterFileBankTab)object2).items;
                n = 0;
                while (n < ((ArrayList)object2).size()) {
                    object = (ItemStack)((ArrayList)object2).get(n);
                    if (object != null && ((ItemStack)object).getAmount() > 0 && !(itemDefinition = ((ItemStack)object).getDefinition()).isUntradeable()) {
                        this.cachedItemValue += itemDefinition.getValue() * ((ItemStack)object).getAmount();
                    }
                    ++n;
                }
                ++n2;
            }
            ItemStack[] inventoryItems = this.inventoryItems;
            n = this.inventoryItems.length;
            int n3 = 0;
            while (n3 < n) {
                ItemStack itemStack = inventoryItems[n3];
                if (itemStack != null && itemStack.getAmount() > 0 && !(itemDefinition = itemStack.getDefinition()).isUntradeable()) {
                    this.cachedItemValue += itemDefinition.getValue() * itemStack.getAmount();
                }
                ++n3;
            }
            ItemStack[] equipmentItems = this.equipmentItems;
            n = this.equipmentItems.length;
            n3 = 0;
            while (n3 < n) {
                ItemStack object3 = equipmentItems[n3];
                if (object3 != null && ((ItemStack)object3).getAmount() > 0 && !(itemDefinition = ((ItemStack)object3).getDefinition()).isUntradeable()) {
                    this.cachedItemValue += itemDefinition.getValue() * ((ItemStack)object3).getAmount();
                }
                ++n3;
            }
        }
        return this.cachedItemValue;
    }

    public final long getSkillExperience(int n) {
        if (n < 21) {
            return this.skillExperience[n];
        }
        long l = 0L;
        n = 0;
        while (n < 21) {
            l += this.skillExperience[n];
            ++n;
        }
        return l;
    }

    public final int getTotalLevel() {
        int n = 0;
        int n2 = 0;
        while (n2 < 21) {
            n += CharacterFileRecord.getLevelForExperience(this.getSkillExperience(n2));
            ++n2;
        }
        return n;
    }

    public static int getLevelForExperience(double d) {
        int n = 1;
        while (n <= 99) {
            if ((double)experienceForLevel[n] > d) {
                return n;
            }
            ++n;
        }
        return 99;
    }
}

