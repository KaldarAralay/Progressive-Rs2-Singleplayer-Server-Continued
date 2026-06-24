/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.dialogue;

import com.rs2.CacheCoordinateTranslator;
import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.cache.CacheDefinitionIndex;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.bankpin.BankPinEntryMode;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.dialogue.TenthSquadSigilTeleportTask;
import com.rs2.model.gameplay.abyss.AbyssManager;
import com.rs2.model.gameplay.barrows.BarrowsManager;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.gameplay.partyroom.PartyRoomManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.action.BarrowsRepairHandler;
import com.rs2.model.item.action.GodBookHandler;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.player.BankManager;
import com.rs2.model.player.GrandExchangeManager;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.randomevent.RandomEventManager;
import com.rs2.model.randomevent.sandwichlady.SandwichLadyFoodOffer;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.farming.FarmingFarmerDefinition;
import com.rs2.model.skill.farming.FarmingFarmerHandler;
import com.rs2.model.skill.magic.TeleportManager;
import com.rs2.model.skill.runecrafting.EssencePouchDefinition;
import com.rs2.model.skill.slayer.SlayerMasterDefinition;
import com.rs2.model.skill.slayer.SlayerMonsterGuide;
import com.rs2.model.skill.smithing.DragonSquareShieldSmithing;
import com.rs2.model.travel.HajedyCartRoute;
import com.rs2.model.travel.ShipRoute;
import com.rs2.model.travel.TravelManager;
import com.rs2.util.GameUtil;
import com.rs2.util.GameplayTrace;

public class DialogueManager {
    private Player player;
    private int dialogueId;
    private int dialogueType;
    private int dialogueContextX;
    private int dialogueContextY;
    private int dialogueStep;
    private int dialogueContextId;
    private int dialogueNpcId;

    public DialogueManager(Player player) {
        this.player = player;
    }

    private void traceDisplay(String kind, String text) {
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("dialogue display kind=" + kind + " player=" + GameplayTrace.describe(this.player) + " id=" + this.dialogueId + " step=" + this.dialogueStep + " type=" + this.dialogueType + " npcId=" + this.dialogueNpcId + " text=" + text);
        }
    }

    public final void setDialogueStep(int n) {
        this.dialogueStep = n;
    }

    public final void setNextDialogueStep(int n) {
        this.dialogueStep = n - 1;
    }

    public final int getDialogueStep() {
        return this.dialogueStep;
    }

    public final void finishDialogue() {
        this.dialogueStep = 9001;
    }

    public final void markDialogueInactive() {
        this.dialogueStep = -1;
    }

    public final boolean isDialogueInactive() {
        return this.dialogueStep > 9000 || this.dialogueStep < 0 || this.dialogueId < 0;
    }

    public final void setDialogueId(int n) {
        this.dialogueId = n;
    }

    public final int getDialogueId() {
        return this.dialogueId;
    }

    public final void setDialogueType(int n) {
        this.dialogueType = n;
    }

    public final int getDialogueType() {
        return this.dialogueType;
    }

    public final int getDialogueContextX() {
        return this.dialogueContextX;
    }

    public final int getDialogueContextY() {
        return this.dialogueContextY;
    }

    public final int getDialogueContextId() {
        return this.dialogueContextId;
    }

    public final void resetDialogueState() {
        int n = 0;
        DialogueManager dialogueManager = this;
        this.dialogueStep = n;
        n = -1;
        dialogueManager = this;
        this.dialogueId = n;
        n = 0;
        dialogueManager = this;
        this.dialogueType = n;
        n = -1;
        dialogueManager = this;
        this.dialogueContextX = n;
        n = -1;
        dialogueManager = this;
        this.dialogueContextY = n;
    }

    public final boolean handleOptionButton(int n) {
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("dialogue option-button player=" + GameplayTrace.describe(this.player) + " buttonId=" + n + " id=" + this.dialogueId + " step=" + this.dialogueStep + " type=" + this.dialogueType + " npcId=" + this.dialogueNpcId);
        }
        switch (n) {
            case 2461: 
            case 2471: 
            case 2482: 
            case 2494: {
                DialogueManager dialogueManager = this;
                if (dialogueManager.dialogueType == 0) {
                    DialogueManager dialogueManager2 = this;
                    dialogueManager = dialogueManager2;
                    dialogueManager = this;
                    DialogueManager.continueDialogue(this.player, dialogueManager2.dialogueId, dialogueManager.dialogueStep + 1, 1);
                }
                dialogueManager = this;
                if (dialogueManager.dialogueType == 1) {
                    DialogueManager dialogueManager3 = this;
                    dialogueManager = dialogueManager3;
                    DialogueManager dialogueManager4 = this;
                    dialogueManager = dialogueManager4;
                    DialogueManager dialogueManager5 = this;
                    dialogueManager = dialogueManager5;
                    DialogueManager dialogueManager6 = this;
                    dialogueManager = dialogueManager6;
                    dialogueManager = this;
                    DialogueManager.continueContextDialogue(dialogueManager3.dialogueContextId, this.player, dialogueManager4.dialogueId, dialogueManager5.dialogueStep + 1, 1, dialogueManager6.dialogueContextX, dialogueManager.dialogueContextY);
                }
                return true;
            }
            case 2462: 
            case 2472: 
            case 2483: 
            case 2495: {
                DialogueManager dialogueManager = this;
                if (dialogueManager.dialogueType == 0) {
                    DialogueManager dialogueManager7 = this;
                    dialogueManager = dialogueManager7;
                    dialogueManager = this;
                    DialogueManager.continueDialogue(this.player, dialogueManager7.dialogueId, dialogueManager.dialogueStep + 1, 2);
                }
                dialogueManager = this;
                if (dialogueManager.dialogueType == 1) {
                    DialogueManager dialogueManager8 = this;
                    dialogueManager = dialogueManager8;
                    DialogueManager dialogueManager9 = this;
                    dialogueManager = dialogueManager9;
                    DialogueManager dialogueManager10 = this;
                    dialogueManager = dialogueManager10;
                    DialogueManager dialogueManager11 = this;
                    dialogueManager = dialogueManager11;
                    dialogueManager = this;
                    DialogueManager.continueContextDialogue(dialogueManager8.dialogueContextId, this.player, dialogueManager9.dialogueId, dialogueManager10.dialogueStep + 1, 2, dialogueManager11.dialogueContextX, dialogueManager.dialogueContextY);
                }
                return true;
            }
            case 2473: 
            case 2484: 
            case 2496: {
                DialogueManager dialogueManager = this;
                if (dialogueManager.dialogueType == 0) {
                    DialogueManager dialogueManager12 = this;
                    dialogueManager = dialogueManager12;
                    dialogueManager = this;
                    DialogueManager.continueDialogue(this.player, dialogueManager12.dialogueId, dialogueManager.dialogueStep + 1, 3);
                }
                dialogueManager = this;
                if (dialogueManager.dialogueType == 1) {
                    DialogueManager dialogueManager13 = this;
                    dialogueManager = dialogueManager13;
                    DialogueManager dialogueManager14 = this;
                    dialogueManager = dialogueManager14;
                    DialogueManager dialogueManager15 = this;
                    dialogueManager = dialogueManager15;
                    DialogueManager dialogueManager16 = this;
                    dialogueManager = dialogueManager16;
                    dialogueManager = this;
                    DialogueManager.continueContextDialogue(dialogueManager13.dialogueContextId, this.player, dialogueManager14.dialogueId, dialogueManager15.dialogueStep + 1, 3, dialogueManager16.dialogueContextX, dialogueManager.dialogueContextY);
                }
                return true;
            }
            case 2485: 
            case 2497: {
                DialogueManager dialogueManager = this;
                if (dialogueManager.dialogueType == 0) {
                    DialogueManager dialogueManager17 = this;
                    dialogueManager = dialogueManager17;
                    dialogueManager = this;
                    DialogueManager.continueDialogue(this.player, dialogueManager17.dialogueId, dialogueManager.dialogueStep + 1, 4);
                }
                dialogueManager = this;
                if (dialogueManager.dialogueType == 1) {
                    DialogueManager dialogueManager18 = this;
                    dialogueManager = dialogueManager18;
                    DialogueManager dialogueManager19 = this;
                    dialogueManager = dialogueManager19;
                    DialogueManager dialogueManager20 = this;
                    dialogueManager = dialogueManager20;
                    DialogueManager dialogueManager21 = this;
                    dialogueManager = dialogueManager21;
                    dialogueManager = this;
                    DialogueManager.continueContextDialogue(dialogueManager18.dialogueContextId, this.player, dialogueManager19.dialogueId, dialogueManager20.dialogueStep + 1, 4, dialogueManager21.dialogueContextX, dialogueManager.dialogueContextY);
                }
                return true;
            }
            case 2498: {
                DialogueManager dialogueManager = this;
                if (dialogueManager.dialogueType == 0) {
                    DialogueManager dialogueManager22 = this;
                    dialogueManager = dialogueManager22;
                    dialogueManager = this;
                    DialogueManager.continueDialogue(this.player, dialogueManager22.dialogueId, dialogueManager.dialogueStep + 1, 5);
                }
                dialogueManager = this;
                if (dialogueManager.dialogueType == 1) {
                    DialogueManager dialogueManager23 = this;
                    dialogueManager = dialogueManager23;
                    DialogueManager dialogueManager24 = this;
                    dialogueManager = dialogueManager24;
                    DialogueManager dialogueManager25 = this;
                    dialogueManager = dialogueManager25;
                    DialogueManager dialogueManager26 = this;
                    dialogueManager = dialogueManager26;
                    dialogueManager = this;
                    DialogueManager.continueContextDialogue(dialogueManager23.dialogueContextId, this.player, dialogueManager24.dialogueId, dialogueManager25.dialogueStep + 1, 5, dialogueManager26.dialogueContextX, dialogueManager.dialogueContextY);
                }
                return true;
            }
        }
        return false;
    }

    public final void showTwoOptionsWithTitle(String string, String string2, String string3) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string2, 2461);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 2462);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 2460);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2465);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2468);
        player = this.player;
        player.packetSender.showChatboxInterface(2459);
    }

    public final void showThreeOptionsWithTitle(String string, String string2, String string3, String string4) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string2, 2471);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 2472);
        player = this.player;
        player.packetSender.sendInterfaceText(string4, 2473);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 2470);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2476);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2479);
        player = this.player;
        player.packetSender.showChatboxInterface(2469);
    }

    public final void showFourOptionsWithTitle(String string, String string2, String string3, String string4, String string5) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string2, 2482);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 2483);
        player = this.player;
        player.packetSender.sendInterfaceText(string4, 2484);
        player = this.player;
        player.packetSender.sendInterfaceText(string5, 2485);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 2481);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2488);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2489);
        player = this.player;
        player.packetSender.showChatboxInterface(2480);
    }

    public final void showFiveOptionsWithTitle(String string, String string2, String string3, String string4, String string5, String string6) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string2, 2494);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 2495);
        player = this.player;
        player.packetSender.sendInterfaceText(string4, 2496);
        player = this.player;
        player.packetSender.sendInterfaceText(string5, 2497);
        player = this.player;
        player.packetSender.sendInterfaceText(string6, 2498);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 2493);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2501);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2502);
        player = this.player;
        player.packetSender.showChatboxInterface(2492);
    }

    public final void showTwoOptions(String string, String string2) {
        this.traceDisplay("options2", string + " | " + string2);
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 2461);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 2462);
        player = this.player;
        player.packetSender.sendInterfaceText("Select an Option", 2460);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2465);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2468);
        player = this.player;
        player.packetSender.showChatboxInterface(2459);
    }

    public final void showThreeOptions(String string, String string2, String string3) {
        this.traceDisplay("options3", string + " | " + string2 + " | " + string3);
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 2471);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 2472);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 2473);
        player = this.player;
        player.packetSender.sendInterfaceText("Select an Option", 2470);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2476);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2479);
        player = this.player;
        player.packetSender.showChatboxInterface(2469);
    }

    public final void showFourOptions(String string, String string2, String string3, String string4) {
        this.traceDisplay("options4", string + " | " + string2 + " | " + string3 + " | " + string4);
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 2482);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 2483);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 2484);
        player = this.player;
        player.packetSender.sendInterfaceText(string4, 2485);
        player = this.player;
        player.packetSender.sendInterfaceText("Select an Option", 2481);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2488);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2489);
        player = this.player;
        player.packetSender.showChatboxInterface(2480);
    }

    public final void showFiveOptions(String string, String string2, String string3, String string4, String string5) {
        this.traceDisplay("options5", string + " | " + string2 + " | " + string3 + " | " + string4 + " | " + string5);
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 2494);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 2495);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 2496);
        player = this.player;
        player.packetSender.sendInterfaceText(string4, 2497);
        player = this.player;
        player.packetSender.sendInterfaceText(string5, 2498);
        player = this.player;
        player.packetSender.sendInterfaceText("Select an Option", 2493);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(0, 2501);
        player = this.player;
        player.packetSender.setInterfaceHiddenFlag(1, 2502);
        player = this.player;
        player.packetSender.showChatboxInterface(2492);
    }

    public final void showOptions(String[] stringArray) {
        switch (stringArray.length) {
            case 2: {
                this.showTwoOptions(stringArray[0], stringArray[1]);
                return;
            }
            case 3: {
                this.showThreeOptions(stringArray[0], stringArray[1], stringArray[2]);
                return;
            }
            case 4: {
                this.showFourOptions(stringArray[0], stringArray[1], stringArray[2], stringArray[3]);
                return;
            }
            case 5: {
                this.showFiveOptions(stringArray[0], stringArray[1], stringArray[2], stringArray[3], stringArray[4]);
            }
        }
    }

    public final void showStatement(String[] stringArray) {
        switch (stringArray.length) {
            case 1: {
                this.showOneLineStatement(stringArray[0]);
                return;
            }
            case 2: {
                this.showTwoLineStatement(stringArray[0], stringArray[1]);
                return;
            }
            case 3: {
                this.showThreeLineStatement(stringArray[0], stringArray[1], stringArray[2]);
                return;
            }
            case 4: {
                this.showFourLineStatement(stringArray[0], stringArray[1], stringArray[2], stringArray[3]);
                return;
            }
            case 5: {
                this.showFiveLineStatement(stringArray[0], stringArray[1], stringArray[2], stringArray[3], stringArray[4]);
            }
        }
    }

    public final void showOneLineStatement(String string) {
        this.traceDisplay("statement1", string);
        Object object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string, 357);
        object = this.player;
        ((Player)object).packetSender.showChatboxInterface(356);
        if (this.player.getQuestState(0) != 1) {
            object = this.player.getDialogueManager();
            this.player.getDialogueManager().dialogueStep = 9001;
        }
    }

    public final void showItemIdMessage(String string, int n) {
        Object object = this.player;
        ((Player)object).packetSender.sendInterfaceModel(307, 150, n);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string, 308);
        object = this.player;
        ((Player)object).packetSender.showChatboxInterface(306);
        if (this.player.getQuestState(0) != 1) {
            object = this.player.getDialogueManager();
            this.player.getDialogueManager().dialogueStep = 9001;
        }
    }

    public final void showTwoLineStatement(String string, String string2) {
        this.traceDisplay("statement2", string + " | " + string2);
        Object object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string, 360);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string2, 361);
        object = this.player;
        ((Player)object).packetSender.showChatboxInterface(359);
        if (this.player.getQuestState(0) != 1) {
            object = this.player.getDialogueManager();
            this.player.getDialogueManager().dialogueStep = 9001;
        }
    }

    public final void showThreeLineStatement(String string, String string2, String string3) {
        Object object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string, 364);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string2, 365);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string3, 366);
        object = this.player;
        ((Player)object).packetSender.showChatboxInterface(363);
        if (this.player.getQuestState(0) != 1) {
            object = this.player.getDialogueManager();
            this.player.getDialogueManager().dialogueStep = 9001;
        }
    }

    public final void showFourLineStatement(String string, String string2, String string3, String string4) {
        Object object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string, 369);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string2, 370);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string3, 371);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string4, 372);
        object = this.player;
        ((Player)object).packetSender.showChatboxInterface(368);
        if (this.player.getQuestState(0) != 1) {
            object = this.player.getDialogueManager();
            this.player.getDialogueManager().dialogueStep = 9001;
        }
    }

    public final void showFiveLineStatement(String string, String string2, String string3, String string4, String string5) {
        Object object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string, 375);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string2, 376);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string3, 377);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string4, 378);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(string5, 379);
        object = this.player;
        ((Player)object).packetSender.showChatboxInterface(374);
        if (this.player.getQuestState(0) != 1) {
            object = this.player.getDialogueManager();
            this.player.getDialogueManager().dialogueStep = 9001;
        }
    }

    public final void showOneLineChatboxMessage(String string) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 12789);
        player = this.player;
        player.packetSender.showChatboxInterface(12788);
    }

    public final void showNpcDialogue(String[] stringArray, int n) {
        switch (stringArray.length) {
            case 1: {
                this.showNpcOneLineDialogue(stringArray[0], 588);
                return;
            }
            case 2: {
                this.showNpcTwoLineDialogue(stringArray[0], stringArray[1], 588);
                return;
            }
            case 3: {
                this.showNpcThreeLineDialogue(stringArray[0], stringArray[1], stringArray[2], 588);
                return;
            }
            case 4: {
                this.showNpcFourLineDialogue(stringArray[0], stringArray[1], stringArray[2], stringArray[3], 588);
            }
        }
    }

    public final void showNpcOneLineDialogue(String line, int animationId) {
        this.traceDisplay("npc1", line);
        int npcId = this.dialogueNpcId < 0 || this.dialogueNpcId > 6433 ? 0 : this.dialogueNpcId;
        String npcName = World.getNpcDefinitions()[npcId].getName();
        this.player.packetSender.sendInterfaceAnimation(4883, animationId);
        this.player.packetSender.sendInterfaceText(npcName, 4884);
        this.player.packetSender.sendInterfaceText(line, 4885);
        this.player.packetSender.sendNpcHeadOnInterface(npcId, 4883);
        this.player.packetSender.showChatboxInterface(4882);
    }

    public final void showNpcTwoLineDialogue(String line1, String line2, int animationId) {
        this.traceDisplay("npc2", line1 + " | " + line2);
        int npcId = this.dialogueNpcId < 0 || this.dialogueNpcId > 6433 ? 0 : this.dialogueNpcId;
        String npcName = World.getNpcDefinitions()[npcId].getName();
        this.player.packetSender.sendInterfaceAnimation(4888, animationId);
        this.player.packetSender.sendInterfaceText(npcName, 4889);
        this.player.packetSender.sendInterfaceText(line1, 4890);
        this.player.packetSender.sendInterfaceText(line2, 4891);
        this.player.packetSender.sendNpcHeadOnInterface(npcId, 4888);
        this.player.packetSender.showChatboxInterface(4887);
    }

    public final void showNpcThreeLineDialogue(String line1, String line2, String line3, int animationId) {
        this.traceDisplay("npc3", line1 + " | " + line2 + " | " + line3);
        int npcId = this.dialogueNpcId < 0 || this.dialogueNpcId > 6433 ? 0 : this.dialogueNpcId;
        String npcName = World.getNpcDefinitions()[npcId].getName();
        this.player.packetSender.sendInterfaceAnimation(4894, animationId);
        this.player.packetSender.sendInterfaceText(npcName, 4895);
        this.player.packetSender.sendInterfaceText(line1, 4896);
        this.player.packetSender.sendInterfaceText(line2, 4897);
        this.player.packetSender.sendInterfaceText(line3, 4898);
        this.player.packetSender.sendNpcHeadOnInterface(npcId, 4894);
        this.player.packetSender.showChatboxInterface(4893);
    }

    public final void showNpcFourLineDialogue(String line1, String line2, String line3, String line4, int animationId) {
        this.traceDisplay("npc4", line1 + " | " + line2 + " | " + line3 + " | " + line4);
        int npcId = this.dialogueNpcId < 0 || this.dialogueNpcId > 6433 ? 0 : this.dialogueNpcId;
        String npcName = World.getNpcDefinitions()[npcId].getName();
        this.player.packetSender.sendInterfaceAnimation(4901, animationId);
        this.player.packetSender.sendInterfaceText(npcName, 4902);
        this.player.packetSender.sendInterfaceText(line1, 4903);
        this.player.packetSender.sendInterfaceText(line2, 4904);
        this.player.packetSender.sendInterfaceText(line3, 4905);
        this.player.packetSender.sendInterfaceText(line4, 4906);
        this.player.packetSender.sendNpcHeadOnInterface(npcId, 4901);
        this.player.packetSender.showChatboxInterface(4900);
    }

    public final void showAlternateNpcThreeLineDialogue(String line1, String line2, String line3, int animationId) {
        int npcId = this.dialogueNpcId < 0 || this.dialogueNpcId > 6433 ? 0 : this.dialogueNpcId;
        String npcName = World.getNpcDefinitions()[npcId].getName();
        this.player.packetSender.sendInterfaceAnimation(12384, 591);
        this.player.packetSender.sendInterfaceText(npcName, 12385);
        this.player.packetSender.sendInterfaceText(line1, 12386);
        this.player.packetSender.sendInterfaceText(line2, 12387);
        this.player.packetSender.sendInterfaceText(line3, 12388);
        this.player.packetSender.sendNpcHeadOnInterface(npcId, 12384);
        this.player.packetSender.showChatboxInterface(12383);
    }

    public final void showPlayerOneLineDialogue(String string, int n) {
        this.traceDisplay("player1", string);
        Player player = this.player;
        player.packetSender.sendInterfaceAnimation(969, n);
        player = this.player;
        player.packetSender.sendInterfaceText(GameUtil.formatDisplayName(this.player.getUsername()), 970);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 971);
        player = this.player;
        player.packetSender.sendPlayerHeadOnInterface(969);
        player = this.player;
        player.packetSender.showChatboxInterface(968);
    }

    public final void showPlayerTwoLineDialogue(String string, String string2, int n) {
        this.traceDisplay("player2", string + " | " + string2);
        Player player = this.player;
        player.packetSender.sendInterfaceAnimation(974, n);
        player = this.player;
        player.packetSender.sendInterfaceText(GameUtil.formatDisplayName(this.player.getUsername()), 975);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 976);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 977);
        player = this.player;
        player.packetSender.sendPlayerHeadOnInterface(974);
        player = this.player;
        player.packetSender.showChatboxInterface(973);
    }

    public final void showPlayerThreeLineDialogue(String string, String string2, String string3, int n) {
        this.traceDisplay("player3", string + " | " + string2 + " | " + string3);
        Player player = this.player;
        player.packetSender.sendInterfaceAnimation(980, 591);
        player = this.player;
        player.packetSender.sendInterfaceText(GameUtil.formatDisplayName(this.player.getUsername()), 981);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 982);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 983);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 984);
        player = this.player;
        player.packetSender.sendPlayerHeadOnInterface(980);
        player = this.player;
        player.packetSender.showChatboxInterface(979);
    }

    public final void showPlayerFourLineDialogue(String string, String string2, String string3, String string4, int n) {
        this.traceDisplay("player4", string + " | " + string2 + " | " + string3 + " | " + string4);
        Player player = this.player;
        player.packetSender.sendInterfaceAnimation(987, 591);
        player = this.player;
        player.packetSender.sendInterfaceText(GameUtil.formatDisplayName(this.player.getUsername()), 988);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 989);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 990);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 991);
        player = this.player;
        player.packetSender.sendInterfaceText(string4, 992);
        player = this.player;
        player.packetSender.sendPlayerHeadOnInterface(987);
        player = this.player;
        player.packetSender.showChatboxInterface(986);
    }

    public final void showTutorialInstructionOverlay(String string, String string2, String string3, String string4, String string5, boolean bl) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 6180);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 6181);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 6182);
        player = this.player;
        player.packetSender.sendInterfaceText(string4, 6183);
        player = this.player;
        player.packetSender.sendInterfaceText(string5, 6184);
        player = this.player;
        player.packetSender.closeInterface(6179);
    }

    public final void showTwoItemMessage(String string, String string2, ItemStack itemStack, ItemStack itemStack2) {
        Player player = this.player;
        player.packetSender.sendInterfaceText("", 4953);
        player = this.player;
        player.packetSender.sendInterfaceText(string, 4952);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 4955);
        player = this.player;
        player.packetSender.sendInterfaceText("", 4956);
        player = this.player;
        player.packetSender.sendInterfaceModel(4951, 170, itemStack.getId());
        player = this.player;
        player.packetSender.sendInterfaceModel(4957, 170, itemStack2.getId());
        player = this.player;
        player.packetSender.showChatboxInterface(4950);
    }

    public final void showItemMessage(String string, ItemStack itemStack) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 308);
        player = this.player;
        player.packetSender.sendInterfaceModel(307, 200, itemStack.getId());
        player = this.player;
        player.packetSender.showChatboxInterface(306);
    }

    public final void showThreeLineItemMessage(String string, String string2, String string3, ItemStack itemStack) {
        Player player = this.player;
        player.packetSender.sendInterfaceText(string, 318);
        player = this.player;
        player.packetSender.sendInterfaceText(string2, 317);
        player = this.player;
        player.packetSender.sendInterfaceText(string3, 320);
        player = this.player;
        player.packetSender.sendInterfaceModel(316, 200, itemStack.getId());
        player = this.player;
        player.packetSender.showChatboxInterface(315);
    }

    public final void setDialogueNpcId(int n) {
        this.dialogueNpcId = n;
    }

    public static boolean startDialogue(Player player, int n) {
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("dialogue start player=" + GameplayTrace.describe(player) + " id=" + n);
        }
        player.getDialogueManager().resetDialogueState();
        return DialogueManager.continueDialogue(player, n, 1, 0);
    }

    public static boolean continueDialogue(Player player, int n, int n2, int n3) {
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("dialogue continue player=" + GameplayTrace.describe(player) + " id=" + n + " step=" + n2 + " option=" + n3);
        }
        return DialogueManager.continueDialogueWithNpcId(player, n, n2, n3, n);
    }

    public static void a(Player player, int n, int n2) {
        int n3 = 10009;
        DialogueManager dialogueManager = player.getDialogueManager();
        player.getDialogueManager().dialogueId = n3;
        n3 = n2;
        dialogueManager = player.getDialogueManager();
        player.getDialogueManager().dialogueStep = n3 - 1;
    }

    public static boolean startContextDialogue(int n, Player player, int n2, int n3, int n4) {
        player.getDialogueManager().resetDialogueState();
        return DialogueManager.continueContextDialogue(n, player, n2, 1, 0, n3, n4);
    }

    /*
     * Enabled aggressive block sorting
     */
    public static boolean continueContextDialogue(int n, Player player, int n2, int n3, int n4, int n5, int n6) {
        int n7 = n6;
        n6 = n5;
        n5 = n2;
        int n8 = n3;
        Object object = player.getDialogueManager();
        player.getDialogueManager().dialogueStep = n8;
        n8 = n;
        object = player.getDialogueManager();
        player.getDialogueManager().dialogueContextId = n8;
        n8 = n2;
        object = player.getDialogueManager();
        player.getDialogueManager().dialogueId = n8;
        n8 = 1;
        object = player.getDialogueManager();
        player.getDialogueManager().dialogueType = n8;
        n8 = n6;
        object = player.getDialogueManager();
        player.getDialogueManager().dialogueContextX = n8;
        n8 = n7;
        object = player.getDialogueManager();
        player.getDialogueManager().dialogueContextY = n8;
        n8 = n5;
        object = player.getDialogueManager();
        player.getDialogueManager().dialogueNpcId = n8;
        if (player.getQuestManager().handleContextDialogue(n, n2, n3, n4, n5, n6, n7)) {
            return true;
        }
        block0 : switch (n2) {
            case 2411: 
            case 12045: 
            case 12047: {
                n = 3307;
                if (!NpcDefinition.isDefined(3307)) {
                    n = 33;
                }
                n8 = n;
                object = player.getDialogueManager();
                player.getDialogueManager().dialogueNpcId = n8;
                object = player.getDialogueManager();
                switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showNpcTwoLineDialogue("You may not pass through this door without paying the", "trading tax.", 591);
                        return true;
                    }
                    case 2: {
                        player.getDialogueManager().showPlayerOneLineDialogue("So how much is the tax?", 591);
                        return true;
                    }
                    case 3: {
                        player.getDialogueManager().showNpcOneLineDialogue("The cost is one diamond.", 591);
                        return true;
                    }
                    case 4: {
                        player.getDialogueManager().showFourOptions("Okay...", "A diamond? Are you crazy?", "I haven't brought my diamonds with me.", "What do you do with all the diamonds you get?");
                        return true;
                    }
                    case 5: {
                        switch (n4) {
                            case 1: {
                                player.getDialogueManager().showPlayerOneLineDialogue("Okay...", 591);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 6: {
                        if (!player.getInventoryManager().containsItemAmount(1601, 1)) {
                            player.getDialogueManager().showNpcOneLineDialogue("You don't have a diamond with you!", 591);
                            object = player.getDialogueManager();
                            player.getDialogueManager().dialogueStep = 9001;
                            return true;
                        }
                        player.getInventoryManager().removeItem(new ItemStack(1601, 1));
                        if (n2 == 2411) {
                            n = 0;
                            n2 = 0;
                            if (CacheCoordinateTranslator.dungeonCoordinateShiftActive) {
                                n = 768;
                                n2 = 5120;
                            }
                            if (n6 == n + 2470) {
                                object = player;
                                ((Player)object).packetSender.queueRelativeMovementStep(1, 0, true);
                                object = player;
                                ((Player)object).packetSender.openSingleDoor(2411, n + 2470, n2 + 4438, 0);
                                break block0;
                            }
                            object = player;
                            ((Player)object).packetSender.queueRelativeMovementStep(0, -1, true);
                            object = player;
                            ((Player)object).packetSender.openSingleDoor(2411, n + 2465, n2 + 4434, 0);
                            break block0;
                        }
                        if (n6 == 2469) {
                            object = player;
                            ((Player)object).packetSender.queueRelativeMovementStep(1, 0, true);
                            object = player;
                            ((Player)object).packetSender.openDoubleDoorPair(12045, 2469, 4438, 12047, 2469, 4437);
                            break block0;
                        }
                        object = player;
                        ((Player)object).packetSender.queueRelativeMovementStep(0, -1, true);
                        object = player;
                        ((Player)object).packetSender.openDoubleDoorPair(12045, 2466, 4434, 12047, 2465, 4434);
                        break block0;
                    }
                }
                break;
            }
            case 2416: {
                object = player.getDialogueManager();
                block20 : switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showThreeOptions("Balloon Bonanza.", "Nightly Dance.", "No action.");
                        return true;
                    }
                    case 2: {
                        switch (n4) {
                            case 1: {
                                if (!player.getInventoryManager().containsItemAmount(995, 1000)) {
                                    player.getDialogueManager().showOneLineStatement("Balloon Bonanza costs 1000 coins.");
                                    object = player.getDialogueManager();
                                    player.getDialogueManager().dialogueStep = 9001;
                                    return true;
                                }
                                if (PartyRoomManager.hasActiveDropParty()) {
                                    player.getDialogueManager().showOneLineStatement("Drop party already in progress!");
                                    object = player.getDialogueManager();
                                    player.getDialogueManager().dialogueStep = 9001;
                                    return true;
                                }
                                if (Server.serverStatus == 3) {
                                    player.getDialogueManager().showOneLineStatement("You can't start a party during a system update!");
                                    object = player.getDialogueManager();
                                    player.getDialogueManager().dialogueStep = 9001;
                                    return true;
                                }
                                if (PartyRoomManager.partyChestContainer.getFreeSlots() == PartyRoomManager.partyChestContainer.g()) {
                                    player.getDialogueManager().showOneLineStatement("There are no items to be dropped!");
                                    object = player.getDialogueManager();
                                    player.getDialogueManager().dialogueStep = 9001;
                                    return true;
                                }
                                player.getDialogueManager().showTwoOptionsWithTitle("This will cost you 1000 coins.", "Pay to continue.", "Never mind.");
                                n8 = 3;
                                object = player.getDialogueManager();
                                player.getDialogueManager().dialogueStep = n8 - 1;
                                return true;
                            }
                            case 2: {
                                if (!player.getInventoryManager().containsItemAmount(995, 500)) {
                                    player.getDialogueManager().showOneLineStatement("Nightly Dance costs 500 coins.");
                                    object = player.getDialogueManager();
                                    player.getDialogueManager().dialogueStep = 9001;
                                    return true;
                                }
                                if (PartyRoomManager.startNightlyDance(player)) break;
                                player.getDialogueManager().showOneLineStatement("Dance event already in progress!");
                                object = player.getDialogueManager();
                                player.getDialogueManager().dialogueStep = 9001;
                                return true;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (n4) {
                            case 1: {
                                PartyRoomManager.startBalloonBonanza(player);
                                break block20;
                            }
                        }
                    }
                }
                break;
            }
            case 2878: {
                object = player.getDialogueManager();
                switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        if (player.mageArenaProgressStage < 5) return false;
                        player.getDialogueManager().showTwoLineStatement("You step into the pool of sparkling water. You feel energy rush", "through your veins.");
                        return true;
                    }
                    case 2: {
                        player.getTeleportManager().startScriptedTeleport(2509, 4689, 0, null, 5, 804, -1, 68);
                    }
                }
                break;
            }
            case 2879: {
                object = player.getDialogueManager();
                switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showTwoLineStatement("You step into the pool of sparkling water. You feel energy rush", "through your veins.");
                        return true;
                    }
                    case 2: {
                        player.getTeleportManager().startScriptedTeleport(2542, 4718, 0, null, 5, 804, -1, 68);
                        break block0;
                    }
                }
                break;
            }
            case 2874: {
                if (player.hasMageArenaGodCape()) {
                    player.getDialogueManager().showOneLineStatement("You already have a God cape!");
                    object = player.getDialogueManager();
                    player.getDialogueManager().dialogueStep = 9001;
                    return true;
                }
                if (n3 == 1) {
                    object = player;
                    ((Player)object).packetSender.queueAbsoluteMovementStep(2516, 4719);
                }
                object = player.getDialogueManager();
                switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showOneLineStatement("You kneel and begin to chant to Zamorak...");
                        return true;
                    }
                    case 2: {
                        player.getUpdateState().setFacePosition(new Position(player.getPosition().getX(), player.getPosition().getY() + 1));
                        player.getUpdateState().setAnimation(645);
                        player.a(player, n2);
                        break block0;
                    }
                    case 10: {
                        player.getDialogueManager().showTwoLineStatement("You feel a rush of energy charge through your veins. Suddenly a", "cape appears before you.");
                        object = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                }
                break;
            }
            case 2875: {
                if (player.hasMageArenaGodCape()) {
                    player.getDialogueManager().showOneLineStatement("You already have a God cape!");
                    object = player.getDialogueManager();
                    player.getDialogueManager().dialogueStep = 9001;
                    return true;
                }
                if (n3 == 1) {
                    object = player;
                    ((Player)object).packetSender.queueAbsoluteMovementStep(2507, 4722);
                }
                object = player.getDialogueManager();
                switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showOneLineStatement("You kneel and begin to chant to Guthix...");
                        return true;
                    }
                    case 2: {
                        player.getUpdateState().setFacePosition(new Position(player.getPosition().getX(), player.getPosition().getY() + 1));
                        player.getUpdateState().setAnimation(645);
                        player.a(player, n2);
                        break block0;
                    }
                    case 10: {
                        player.getDialogueManager().showTwoLineStatement("You feel a rush of energy charge through your veins. Suddenly a", "cape appears before you.");
                        object = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                }
                break;
            }
            case 2873: {
                if (player.hasMageArenaGodCape()) {
                    player.getDialogueManager().showOneLineStatement("You already have a God cape!");
                    object = player.getDialogueManager();
                    player.getDialogueManager().dialogueStep = 9001;
                    return true;
                }
                if (n3 == 1) {
                    object = player;
                    ((Player)object).packetSender.queueAbsoluteMovementStep(2500, 4719);
                }
                object = player.getDialogueManager();
                switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showOneLineStatement("You kneel and begin to chant to Saradomin...");
                        return true;
                    }
                    case 2: {
                        player.getUpdateState().setFacePosition(new Position(player.getPosition().getX(), player.getPosition().getY() + 1));
                        player.getUpdateState().setAnimation(645);
                        player.a(player, n2);
                        break block0;
                    }
                    case 10: {
                        player.getDialogueManager().showTwoLineStatement("You feel a rush of energy charge through your veins. Suddenly a", "cape appears before you.");
                        object = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                }
            }
        }
        object = player.getDialogueManager();
        if (((DialogueManager)object).dialogueStep > 1) {
            object = player;
            ((Player)object).packetSender.closeInterfaces();
        }
        object = player.getDialogueManager();
        if (((DialogueManager)object).dialogueId < 0) return false;
        player.getDialogueManager().resetDialogueState();
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static boolean continueUtilityNpcDialogue(Player player, int n, int n2) {
        switch (n) {
            case 2260: {
                DialogueManager dialogueManager = player.getDialogueManager();
                switch (dialogueManager.dialogueStep) {
                    case 1: {
                        if (!player.isMember()) {
                            player.packetSender.sendGameMessage("You need a members account to access members content.");
                            return true;
                        }
                        if (ServerSettings.freeToPlayWorld) {
                            player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            return true;
                        }
                        if (player.enterTheAbyssMiniquestState == 4) {
                            player.getDialogueManager().showPlayerTwoLineDialogue("So... that's my end of the deal upheld.", "What do I get in return?", 591);
                            return true;
                        }
                        if (player.enterTheAbyssMiniquestState == 3) {
                            if (!player.ownsItem(5518) && !player.ownsItem(5519)) {
                                player.getDialogueManager().showPlayerOneLineDialogue("I lost the orb.", 591);
                                int n3 = 37;
                                dialogueManager = player.getDialogueManager();
                                player.getDialogueManager().dialogueStep = n3 - 1;
                                return true;
                            }
                            player.getDialogueManager().showNpcThreeLineDialogue("Well?", "Have you managed to use my scrying orb to obtain the", "information yet?", 591);
                            return true;
                        }
                        if (player.enterTheAbyssMiniquestState == 2) {
                            player.getDialogueManager().showNpcFourLineDialogue("Ah, you again.", "What was it you wanted?", "The wilderness is hardly the appropriate place for a", "conversation now, is it?", 591);
                            return true;
                        }
                        player.getDialogueManager().showNpcOneLineDialogue("I'm busy right now.", 591);
                        dialogueManager = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                    case 2: {
                        if (player.enterTheAbyssMiniquestState == 4) {
                            player.getDialogueManager().showNpcOneLineDialogue("Indeed, a deal is always a deal.", 591);
                            int n4 = 39;
                            dialogueManager = player.getDialogueManager();
                            player.getDialogueManager().dialogueStep = n4 - 1;
                            return true;
                        }
                        if (player.enterTheAbyssMiniquestState != 3) {
                            player.getDialogueManager().showFourOptions("I'd like to buy some runes!", "Where do you get your runes from?", "All hail Zamorak!", "Nothing, thanks.");
                            return true;
                        }
                        if (!player.ownsItem(5518)) {
                            player.getDialogueManager().showPlayerOneLineDialogue("No, not yet.", 591);
                            dialogueManager = player.getDialogueManager();
                            player.getDialogueManager().dialogueStep = 9001;
                            return true;
                        }
                        if (player.ownsItem(5518) && !player.getInventoryManager().containsItem(5518)) {
                            player.getDialogueManager().showPlayerOneLineDialogue("Yes, I just need to go and get it.", 591);
                            dialogueManager = player.getDialogueManager();
                            player.getDialogueManager().dialogueStep = 9001;
                            return true;
                        }
                        if (!player.getInventoryManager().containsItem(5518)) return true;
                        player.getDialogueManager().showPlayerTwoLineDialogue("Yes I have!", "I've got it right here!", 591);
                        int n5 = 38;
                        dialogueManager = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = n5 - 1;
                        return true;
                    }
                    case 3: {
                        switch (n2) {
                            case 2: {
                                player.getDialogueManager().showPlayerThreeLineDialogue("Where do you get your runes from?", "No offence, but people around here don't exactly like", "'your type'.", 591);
                                return true;
                            }
                        }
                        return false;
                    }
                    case 4: {
                        player.getDialogueManager().showNpcOneLineDialogue("My 'type'? Explain.", 591);
                        return true;
                    }
                    case 5: {
                        player.getDialogueManager().showPlayerThreeLineDialogue("You know...", "Scary bearded men in dark clothing with unhealthy", "obsessions with destruction and stuff.", 591);
                        return true;
                    }
                    case 6: {
                        player.getDialogueManager().showNpcFourLineDialogue("Hmmm.", "Well, you may be right, the foolish Saradominists that", "own this pathetic city don't appreciate loyal Zamorakians,", "it is true.", 591);
                        return true;
                    }
                    case 7: {
                        player.getDialogueManager().showPlayerTwoLineDialogue("So you can't be getting your runes anywhere around", "here...", 591);
                        return true;
                    }
                    case 8: {
                        player.getDialogueManager().showNpcThreeLineDialogue("That is correct stranger.", "The mysteries of manufacturing Runes is a closely", "guarded secret of the Zamorakian brotherhood.", 591);
                        return true;
                    }
                    case 9: {
                        player.getDialogueManager().showPlayerFourLineDialogue("Oh, you mean the whole teleporting to the Rune", "Essence mine, mining some essence, then using the", "talismans to locate the Rune Temples, then binding", "runes there?", 591);
                        return true;
                    }
                    case 10: {
                        player.getDialogueManager().showPlayerOneLineDialogue("I know all about it...", 591);
                        return true;
                    }
                    case 11: {
                        player.getDialogueManager().showNpcTwoLineDialogue("WHAT?", "I... but... you...", 591);
                        return true;
                    }
                    case 12: {
                        player.getDialogueManager().showPlayerFourLineDialogue("Well, I helped deliver some research notes to Sedridor", "at the Wizards Tower, and he teleported me to a huge", "mine he said was hidden off to the North somewhere", "where I could mine essence.", 591);
                        return true;
                    }
                    case 13: {
                        player.getDialogueManager().showNpcTwoLineDialogue("And there is an abundant supply of this 'essence' there", "you say?", 591);
                        return true;
                    }
                    case 14: {
                        player.getDialogueManager().showPlayerThreeLineDialogue("Yes, but I thought you said that you knew how to make", "runes?", "All this stuff is fairly basic knowledge I thought.", 591);
                        return true;
                    }
                    case 15: {
                        player.getDialogueManager().showNpcTwoLineDialogue("No.", "No, not at all.", 591);
                        return true;
                    }
                    case 16: {
                        player.getDialogueManager().showNpcFourLineDialogue("We occasionally manage to plunder small samples of this", "'essence' and we have recently discovered these temples", "you speak of, but I have never heard of these talismans", "before, and I was certainly not aware that this 'essence'", 591);
                        return true;
                    }
                    case 17: {
                        player.getDialogueManager().showNpcOneLineDialogue("This changes everything.", 591);
                        return true;
                    }
                    case 18: {
                        player.getDialogueManager().showPlayerOneLineDialogue("How do you mean?", 591);
                        return true;
                    }
                    case 19: {
                        player.getDialogueManager().showNpcFourLineDialogue("For many years there has been a struggle for power", "on this world.", "You may dispute the morality of each side as you wish,", "but the stalemate that exists between my Lord Zamorak", 591);
                        return true;
                    }
                    case 20: {
                        player.getDialogueManager().showNpcFourLineDialogue("and that pathetic meddling fool Saradomin has meant", "that our struggles have become more secretive.", "We exist in a 'cold war' if you will, each side fearful of", "letting the other gain too much power, and each side", 591);
                        return true;
                    }
                    case 21: {
                        player.getDialogueManager().showPlayerOneLineDialogue("You mean Guthix?", 591);
                        return true;
                    }
                    case 22: {
                        player.getDialogueManager().showNpcFourLineDialogue("Indeed.", "Amongst others.", "But you now tell me that the Saradominist Wizards", "have the capability to mass produce runes, I can only", 591);
                        return true;
                    }
                    case 23: {
                        player.getDialogueManager().showNpcTwoLineDialogue("conclude that they have been doing so secretly for some", "time now.", 591);
                        return true;
                    }
                    case 24: {
                        player.getDialogueManager().showNpcFourLineDialogue("Will you help me and my fellow Zamorakians to access", "this 'essence' mine?", "In return I will share with you the research we have", "gathered.", 591);
                        return true;
                    }
                    case 25: {
                        player.getDialogueManager().showTwoOptionsWithTitle("Help the Zamorakian Mage?", "Yes", "No");
                        return true;
                    }
                    case 26: {
                        switch (n2) {
                            case 1: {
                                player.getDialogueManager().showPlayerTwoLineDialogue("Okay, I'll help you.", "What can I do?", 591);
                                return true;
                            }
                        }
                        return false;
                    }
                    case 27: {
                        player.getDialogueManager().showNpcFourLineDialogue("All I need from you is the spell that will teleport me to", "this essence mine.", "That should be sufficient for the armies of Zamorak to", "once more begin stockpiling magic for war.", 591);
                        return true;
                    }
                    case 28: {
                        player.getDialogueManager().showPlayerThreeLineDialogue("Oh.", "Erm....", "I don't actually know that spell.", 591);
                        return true;
                    }
                    case 29: {
                        player.getDialogueManager().showNpcTwoLineDialogue("What?", "Then how do you access this location?", 591);
                        return true;
                    }
                    case 30: {
                        player.getDialogueManager().showPlayerFourLineDialogue("Oh, well, people who do know the spell teleport me there", "directly.", "Apparently they wouldn't teach it to me to try and keep", "the location secret.", 591);
                        return true;
                    }
                    case 31: {
                        player.getDialogueManager().showNpcFourLineDialogue("Hmmm.", "Yes, yes I see.", "Very well then, you may still assist us in finding this", "mysterious essence mine.", 591);
                        return true;
                    }
                    case 32: {
                        player.getDialogueManager().showPlayerOneLineDialogue("How would I do that?", 591);
                        return true;
                    }
                    case 33: {
                        player.getDialogueManager().showNpcThreeLineDialogue("I'll give you a scrying orb.", "I have cast a standard cypher spell upon it, so that it", "will absorb mystical energies that it is exposed to.", 591);
                        return true;
                    }
                    case 34: {
                        player.getDialogueManager().showNpcThreeLineDialogue("Bring it with you and teleport to the rune essence", "location, and it will absorb the mechanics of the spell and", "allow us to reverse-engineer the magic behind it.", 591);
                        return true;
                    }
                    case 35: {
                        player.getDialogueManager().showNpcThreeLineDialogue("More than three may be helpful to us, but we need a", "minimum of three in order to triangulate the position of", "this essence mine.", 591);
                        return true;
                    }
                    case 36: {
                        player.getInventoryManager().addOrDropItem(new ItemStack(5519));
                        player.enterTheAbyssMiniquestState = 3;
                        return false;
                    }
                    case 37: {
                        player.getDialogueManager().showNpcTwoLineDialogue("Heres a new one.", "Try to not lose it this time.", 591);
                        int n6 = 36;
                        dialogueManager = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = n6 - 1;
                        return true;
                    }
                    case 38: {
                        player.getDialogueManager().showNpcThreeLineDialogue("Excellent.", "Give it here, and I shall examine the findings.", "Speak to me in a small while.", 591);
                        if (player.getInventoryManager().removeItem(new ItemStack(5518))) {
                            player.enterTheAbyssMiniquestState = 4;
                        }
                        dialogueManager = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                    case 39: {
                        player.getDialogueManager().showNpcTwoLineDialogue("I offer you three things as reward for your efforts on", "behalf of my Lord Zamorak;", 591);
                        return true;
                    }
                    case 40: {
                        player.getDialogueManager().showNpcFourLineDialogue("The first is knowledge.", "I offer you my collected research on the abyss.", "I also offer you 1000 points of experience in", "RuneCrafting for your trouble.", 591);
                        return true;
                    }
                    case 41: {
                        player.getDialogueManager().showNpcThreeLineDialogue("Your final gift is that of movement.", "I will from now on offer you a teleport to the abyss", "whenever you should require it.", 591);
                        return true;
                    }
                    case 42: {
                        player.getDialogueManager().showPlayerFourLineDialogue("Huh?", "Abyss?", "What are you talking about?", "You told me that you would help me with", 591);
                        return true;
                    }
                    case 43: {
                        player.enterTheAbyssMiniquestState = 1;
                        player.bp();
                        player.getInventoryManager().addOrDropItem(new ItemStack(5520));
                        if (!player.ownsItem(5509)) {
                            player.getInventoryManager().addOrDropItem(new ItemStack(5509));
                        }
                        player.getSkillManager().addQuestExperience(20, 1000.0);
                        player.getDialogueManager().showNpcThreeLineDialogue("And so I have done.", "Read my research notes, they may enlighten you", "somewhat.", 591);
                        dialogueManager = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                }
                return false;
            }
            case 2262: {
                Object object = player.getDialogueManager();
                switch (((DialogueManager)object).dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showPlayerOneLineDialogue("Hello there.", 591);
                        return true;
                    }
                    case 2: {
                        boolean bl;
                        player.getDialogueManager().showNpcTwoLineDialogue("Quiet!", "You must not break my concentration!", 591);
                        object = player;
                        ItemStack[] itemStackArray = ((Player)object).getInventoryManager().getContainer().getItems();
                        int n7 = itemStackArray.length;
                        int n8 = 0;
                        while (true) {
                            EssencePouchDefinition essencePouchDefinition;
                            if (n8 >= n7) {
                                bl = false;
                                break;
                            }
                            ItemStack itemStack = itemStackArray[n8];
                            if (itemStack != null && (essencePouchDefinition = EssencePouchDefinition.forItemOrIndex(itemStack.getId())) != null && itemStack.getId() == essencePouchDefinition.getDegradedItemId()) {
                                bl = true;
                                break;
                            }
                            ++n8;
                        }
                        if (bl) return true;
                        object = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                    case 3: {
                        player.getDialogueManager().showFourOptions("Why not?", "What are you doing here?", "Ok, sorry", "I need your help with something...");
                        return true;
                    }
                    case 4: {
                        switch (n2) {
                            case 4: {
                                player.getDialogueManager().showPlayerTwoLineDialogue("Sorry to disturb you, I just needed your help with", "something quickly.", 591);
                                return true;
                            }
                        }
                        return false;
                    }
                    case 5: {
                        player.getDialogueManager().showNpcThreeLineDialogue("What?", "Oh...", "Very well. What did you want?", 591);
                        return true;
                    }
                    case 6: {
                        player.getDialogueManager().showPlayerTwoLineDialogue("I think my essence pouches might be degrading...", "Can you restore them for me?", 591);
                        return true;
                    }
                    case 7: {
                        player.getDialogueManager().showNpcThreeLineDialogue("A simple transfiguration spell should resolve that for", "you.", "Now leave me be!", 591);
                        object = player;
                        ItemStack[] itemStackArray = ((Player)object).getInventoryManager().getContainer().getItems();
                        int n9 = itemStackArray.length;
                        int n10 = 0;
                        while (true) {
                            EssencePouchDefinition essencePouchDefinition;
                            if (n10 >= n9) {
                                object = player.getDialogueManager();
                                player.getDialogueManager().dialogueStep = 9001;
                                return true;
                            }
                            ItemStack itemStack = itemStackArray[n10];
                            if (itemStack != null && (essencePouchDefinition = EssencePouchDefinition.forItemOrIndex(itemStack.getId())) != null && itemStack.getId() == essencePouchDefinition.getDegradedItemId()) {
                                ((Player)object).getInventoryManager().removeItem(new ItemStack(essencePouchDefinition.getDegradedItemId()));
                                ((Player)object).getInventoryManager().addItem(new ItemStack(essencePouchDefinition.getItemId()));
                            }
                            ++n10;
                        }
                    }
                }
                return false;
            }
            case 1834: {
                DialogueManager dialogueManager = player.getDialogueManager();
                switch (dialogueManager.dialogueStep) {
                    case 1: {
                        player.getDialogueManager().showNpcOneLineDialogue("Do you want a lit candle for 1000 gold?", 591);
                        return true;
                    }
                    case 2: {
                        player.getDialogueManager().showThreeOptions("Yes please.", "One thousand gold?!", "No thanks, I'd rather curse the darkness.");
                        return true;
                    }
                    case 3: {
                        switch (n2) {
                            case 1: {
                                player.getDialogueManager().showPlayerOneLineDialogue("Yes please.", 591);
                                return true;
                            }
                        }
                        return false;
                    }
                    case 4: {
                        if (player.getInventoryManager().removeItem(new ItemStack(995, 1000))) {
                            player.getDialogueManager().showNpcOneLineDialogue("Here you go.", 591);
                            player.getInventoryManager().addOrDropItem(new ItemStack(33));
                            return true;
                        }
                        player.getDialogueManager().showPlayerOneLineDialogue("Looks like I don't have enough coins.", 599);
                        dialogueManager = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                }
                return false;
            }
            case 367: {
                DialogueManager dialogueManager = player.getDialogueManager();
                switch (dialogueManager.dialogueStep) {
                    case 1: {
                        if (ServerSettings.cacheVersion < 303) {
                            player.getDialogueManager().showNpcOneLineDialogue("I'm busy right now.", 591);
                            dialogueManager = player.getDialogueManager();
                            player.getDialogueManager().dialogueStep = 9001;
                            return true;
                        }
                        player.getDialogueManager().showPlayerOneLineDialogue("Hi, I need fuel for a lamp.", 591);
                        return true;
                    }
                    case 2: {
                        player.getDialogueManager().showNpcTwoLineDialogue("Hello there, the fuel you need is lamp oil, do you need.", "help making it?", 591);
                        return true;
                    }
                    case 3: {
                        player.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 4: {
                        switch (n2) {
                            case 1: {
                                player.getDialogueManager().showPlayerOneLineDialogue("Yes please.", 591);
                                return true;
                            }
                        }
                        return false;
                    }
                    case 5: {
                        player.getDialogueManager().showNpcTwoLineDialogue("It's really quite simple. You use the small still in here.", "It's all set up, so there's no fiddling around with dials...", 591);
                        return true;
                    }
                    case 6: {
                        player.getDialogueManager().showNpcTwoLineDialogue("Just put ordinary swamp tar in, and then use a lantern", "or lamp to get the oil out.", 591);
                        return true;
                    }
                    case 7: {
                        player.getDialogueManager().showPlayerOneLineDialogue("Thanks.", 591);
                        dialogueManager = player.getDialogueManager();
                        player.getDialogueManager().dialogueStep = 9001;
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean continueDialogueWithNpcId(Player var0, int var1_1, int var2_17, int var3_18, int var4_19) {
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("dialogue continue-npc player=" + GameplayTrace.describe(var0) + " id=" + var1_1 + " step=" + var2_17 + " option=" + var3_18 + " npcId=" + var4_19);
        }
        Npc var1_2 = null;
        Npc var1_3 = null;
        Player var1_4 = null;
        int[] var1_5 = null;
        GatheringToolDefinition var1_6 = null;
        GatheringToolDefinition var1_7 = null;
        ItemStack var1_8 = null;
        ItemStack var1_9 = null;
        QuestDefinition var1_10 = null;
        QuestDefinition var1_11 = null;
        Npc var1_12 = null;
        ItemStack var1_13 = null;
        QuestDefinition var1_14 = null;
        Npc var1_15 = null;
        SandwichLadyFoodOffer var1_16 = null;
        String var6_20 = null;
        int var6_21 = 0;
        TenthSquadSigilTeleportTask var6_22 = null;
        String var6_23 = null;
        String var6_24 = null;
        String var6_25 = null;
        String var6_26 = null;
        long var6_27 = 0L;
        int var6_28 = 0;
        String var6_29 = null;
        boolean var6_30 = false;
        String var7_31 = null;
        int var7_32 = 0;
        int var7_33 = 0;
        int var7_34 = 0;
        int var7_35 = 0;
        int var7_36 = 0;
        int var7_37 = 0;
        FarmingFarmerDefinition var7_38 = null;
        Npc var8_39 = null;
        ItemStack[] var8_40 = null;
        SlayerMasterDefinition var8_41 = null;
        double var9_42 = 0.0;
        double var9_43 = 0.0;
        double var9_44 = 0.0;
        double var9_45 = 0.0;
        SlayerMonsterGuide var9_46 = null;
        SlayerMonsterGuide var9_47 = null;
        ItemStack[] var9_48 = null;
        ItemStack var9_49 = null;
        ItemStack var10_50 = null;
        var0.getDialogueManager().setDialogueStep(var2_17);
        var0.getDialogueManager().setDialogueId(var1_1);
        var0.getDialogueManager().setDialogueType(0);
        var0.getDialogueManager().setDialogueNpcId(var4_19);
        if (var0.getQuestManager().handleNpcDialogue(var1_1, var2_17, var3_18, var4_19)) {
            return true;
        }
        if (DialogueManager.continueUtilityNpcDialogue(var0, var1_1, var3_18)) {
            return true;
        }
        block0 : switch (var1_1) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 16: 
            case 24: 
            case 25: 
            case 351: 
            case 352: 
            case 353: 
            case 354: 
            case 359: 
            case 360: 
            case 361: 
            case 362: 
            case 363: 
            case 605: 
            case 663: 
            case 726: 
            case 727: 
            case 728: 
            case 729: 
            case 730: 
            case 1024: 
            case 1025: 
            case 1026: 
            case 1027: 
            case 1028: 
            case 1029: 
            case 1086: 
            case 2675: 
            case 2776: 
            case 3223: 
            case 3224: 
            case 3225: 
            case 3226: 
            case 3227: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hello, how's it going?", 588);
                        return true;
                    }
                    case 2: {
                        var1_1 = GameUtil.randomInclusive(12);
                        if (var1_1 == 0) {
                            var0.getDialogueManager().showNpcOneLineDialogue("How can I help you?", 591);
                        } else if (var1_1 == 1) {
                            var0.getDialogueManager().showNpcOneLineDialogue("I'm fine, how are you?", 588);
                            var0.getDialogueManager().setNextDialogueStep(5);
                        } else if (var1_1 == 2) {
                            var0.getDialogueManager().showNpcOneLineDialogue("I'm busy right now.", 591);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 3) {
                            var0.getDialogueManager().showNpcOneLineDialogue("No, I don't want to buy anything!", 614);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 4) {
                            var0.getDialogueManager().showNpcOneLineDialogue("No I don't have any spare change.", 595);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 5) {
                            var0.getDialogueManager().showNpcOneLineDialogue("I'm very well thank you.", 588);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 6) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Hello there! Nice weather we've been having.", 588);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 7) {
                            var0.getDialogueManager().showNpcOneLineDialogue("That is classified information.", 591);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 8) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Get out of my way, I'm in a hurry!", 614);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 9) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Hello.", 588);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 10) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Do I know you? I'm in a hurry!", 588);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 11) {
                            var0.getDialogueManager().showNpcOneLineDialogue("I'm sorry I can't help you there.", 595);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 12) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Not too bad thanks.", 588);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showThreeOptions("Do you wish to trade?", "I'm in search of a quest.", "I'm in search of enemies to kill.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcThreeLineDialogue("No, I have nothing I wish to get rid of.", "If you want to do some trading, there are", "plent of shops and market stalls around though.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("I'm sorry I can't help you there.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showNpcTwoLineDialogue("I've heard there are many fearsome creatures", "that dwell under the ground...", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 5: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Very well thank you.", 588);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 11286: {
                var0.getDialogueManager().setDialogueNpcId(747);
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Could you make me a dragonfire shield?", 588);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Of course I could.", "I will do it for 1,250,000 coins.", 589);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("Here you go.", "Maybe some other time.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Here you go.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Maybe some other time.", 601);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 5: {
                        if (var0.getInventoryManager().containsItemStack(new ItemStack(995, 1250000)) && var0.getInventoryManager().containsItemStack(new ItemStack(1540)) && var0.getInventoryManager().containsItemStack(new ItemStack(11286))) {
                            var0.getInventoryManager().removeItem(new ItemStack(1540, 1));
                            var0.getInventoryManager().removeItem(new ItemStack(11286, 1));
                            var0.getInventoryManager().removeItem(new ItemStack(995, 1250000));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(11284));
                            var0.getPacketSender().sendGameMessage("Oziach makes you a dragonfire shield.");
                            break block0;
                        }
                        var0.getDialogueManager().showPlayerOneLineDialogue("Oops, I forgot to bring the money with me.", 599);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 11287: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showTwoLineStatement("You set to work trying to fix the ancient shield. It's seen some", "heavy action and needs some serious work doing to it.");
                        return true;
                    }
                    case 2: {
                        DragonSquareShieldSmithing.forgeDragonSquareShield(var0);
                        var0.getDialogueManager().showThreeLineStatement("Even for an experienced armourer it is not an easy task, but", "eventually it is ready. You have restored the dragon square shield to", "its former glory.");
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 13001: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showFourOptionsWithTitle("Select a relevant passage", "Wedding Ceremony", "Last Rites", "Blessings", "Preach");
                        return true;
                    }
                    case 2: {
                        GodBookHandler.startRecitation(var0, var3_18);
                    }
                }
                break;
            }
            case 3097: {
                if (var0.ownsProgressHat() && var2_17 == 1) {
                    return false;
                }
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hi.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Greetings. What wisdom do you seek?", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("I'm new to this place. Where am I?", "None, I don't really care.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'm new to this place. Where am I?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("None, I don't really care.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Well young one, you have entered the Magic Training", "Arena. It was built at the start of the Fifth Age, when", "runestones were first discovered. It was made because", "of the many pointless accidents caused by inexperienced", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("mages.", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Who created it?", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Good question. It was originally made by the ancestors", "of the wizards in the Wizards Tower. However, it was", "destroyed by melee and ranged warriors who took", "offence at the use of this new 'Magic Art'. Recently,", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcFourLineDialogue("the current denizens of the Wizards Tower have", "resurrected the arena including various Guardians you", "will see as you look around. We are here to help and to", "ensure things run smoothly.", 591);
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Interesting. So what can I do here?", 591);
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcFourLineDialogue("You may train up your skill in the magic arts by", "travelling through one of the portals at the back of this", "entrance hall. By training up in one of these areas you", "will be awarded special Pizazz Points unique to each", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("room. With these points you may claim a variety of", "items from my fellow guardian up the stairs.", 591);
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("How do you record the points I have earned?", 591);
                        return true;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("You really are full of questions! You will need a special", "Pizazz Progress Hat! I can give you one if you so", "wish to train here.", 591);
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Yes Please!", 591);
                        return true;
                    }
                    case 16: {
                        var0.getInventoryManager().addOrDropItem(new ItemStack(6885, 1));
                        var0.getDialogueManager().showNpcTwoLineDialogue("Here you go. Talk to the hat to find out your current", "Pizazz Point totals.", 591);
                        return true;
                    }
                    case 17: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Talk to it?", 591);
                        return true;
                    }
                    case 18: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Well of course, it's a magic Pizazz Progress Hat! Mind", "your manners though, hats have feelings too!", 591);
                        return true;
                    }
                    case 19: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Er... if you insist.", 591);
                        return true;
                    }
                    case 20: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Oh, and a word of warning: should you decide to leave", "the rooms by any method other than the exit portals,", "you will be teleported to the entrance and have any", "items that you picked up in the room removed.", 591);
                        return true;
                    }
                    case 21: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Can you explain the different portals?", 591);
                        return true;
                    }
                    case 22: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("They lead to four areas to train your magic: The", "Telekinetic Theatre, The Alchemists' Playground, The", "Enchanting Chamber, and The Creature Graveyard.", 591);
                        return true;
                    }
                    case 23: {
                        var0.getDialogueManager().showFiveOptions("What's the Telekinetic Theatre?", "What's the Alchemists' Playground?", "What's the Enchanting Chamber?", "What's the Creature Graveyard?", "Thanks, Bye!");
                        return true;
                    }
                    case 24: {
                        switch (var3_18) {
                            case 1: 
                            case 2: 
                            case 3: 
                            case 4: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(23);
                                return true;
                            }
                            case 5: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Thanks, Bye!", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 1334: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello adventurer.", "What brings you this way?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Can I see your wares?", "Have you found any new prayerbooks?");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can I see your wares?", 591);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Have you found any new prayerbooks?", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Sure thing!", "I think you'll find my prices are remarkable!", 591);
                        return true;
                    }
                    case 5: {
                        ShopManager.openShop(var0, GameplayHelper.getNpcShopId(var1_1));
                        var0.getDialogueManager().markDialogueInactive();
                        break;
                    }
                    case 6: {
                        var0.temporaryActionValue = GameUtil.bitFlag(1) + GameUtil.bitFlag(2) + GameUtil.bitFlag(3);
                        var1_1 = 0;
                        if (!var0.ownsItem(GodBookHandler.damagedSaradominBookId) && !var0.ownsItem(GodBookHandler.holyBookId)) {
                            ++var1_1;
                            var0.temporaryActionValue -= GameUtil.bitFlag(1);
                        }
                        if (!var0.ownsItem(GodBookHandler.damagedZamorakBookId) && !var0.ownsItem(GodBookHandler.unholyBookId)) {
                            ++var1_1;
                            var0.temporaryActionValue -= GameUtil.bitFlag(2);
                        }
                        if (!var0.ownsItem(GodBookHandler.damagedGuthixBookId) && !var0.ownsItem(GodBookHandler.bookOfBalanceId)) {
                            ++var1_1;
                            var0.temporaryActionValue -= GameUtil.bitFlag(3);
                        }
                        if (var1_1 == 0) {
                            var0.getDialogueManager().showNpcOneLineDialogue("No haven't found anything.", 591);
                            var0.getDialogueManager().finishDialogue();
                        } else if (var1_1 == 1) {
                            var0.getDialogueManager().showNpcThreeLineDialogue("Funnily enough I have! I found this book in", "casket just the other day! I'll sell it to you for 5000", "coins; What do you say?", 591);
                        } else {
                            var6_20 = "two";
                            if (var1_1 > 2) {
                                var6_20 = "three";
                            }
                            var0.getDialogueManager().showNpcThreeLineDialogue("Funnily enough I have! I found these " + var6_20 + " books in", "caskets just the other day! I'll sell one to you for 5000", "coins; What do you say?", 591);
                        }
                        return true;
                    }
                    case 7: {
                        if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                            var0.getDialogueManager().showFourOptions("Buy a book of Saradomin", "Buy a book of Zamorak", "Buy a book of Guthix", "Don't buy anything");
                        } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0) {
                            var0.getDialogueManager().showThreeOptions("Buy a book of Saradomin", "Buy a book of Zamorak", "Don't buy anything");
                        } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                            var0.getDialogueManager().showThreeOptions("Buy a book of Saradomin", "Buy a book of Guthix", "Don't buy anything");
                        } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                            var0.getDialogueManager().showThreeOptions("Buy a book of Zamorak", "Buy a book of Guthix", "Don't buy anything");
                        } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0) {
                            var0.getDialogueManager().showTwoOptions("Buy a book of Saradomin", "Don't buy anything");
                        } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0) {
                            var0.getDialogueManager().showTwoOptions("Buy a book of Zamorak", "Don't buy anything");
                        } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                            var0.getDialogueManager().showTwoOptions("Buy a book of Guthix", "Don't buy anything");
                        }
                        return true;
                    }
                    case 8: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getInventoryManager().containsItemAmount(995, 5000)) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("Here you go!", 591);
                                    var0.getInventoryManager().removeItem(new ItemStack(995, 5000));
                                    if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                                        GodBookHandler.giveReplacementBook(var0, GodBookHandler.damagedSaradominBookId);
                                    } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                                        GodBookHandler.giveReplacementBook(var0, GodBookHandler.damagedZamorakBookId);
                                    } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                                        GodBookHandler.giveReplacementBook(var0, GodBookHandler.damagedGuthixBookId);
                                    }
                                } else {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, don't have enough money right now.", 591);
                                }
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Maybe next time.", 591);
                                } else if (var0.getInventoryManager().containsItemAmount(995, 5000)) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("Here you go!", 591);
                                    var0.getInventoryManager().removeItem(new ItemStack(995, 5000));
                                    if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0) {
                                        GodBookHandler.giveReplacementBook(var0, GodBookHandler.damagedZamorakBookId);
                                    } else if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                                        GodBookHandler.giveReplacementBook(var0, GodBookHandler.damagedGuthixBookId);
                                    }
                                } else {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, don't have enough money right now.", 591);
                                }
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 3: {
                                if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) != 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0 || (var0.temporaryActionValue & GameUtil.bitFlag(1)) != 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Maybe next time.", 591);
                                } else if (var0.getInventoryManager().containsItemAmount(995, 5000)) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("Here you go!", 591);
                                    var0.getInventoryManager().removeItem(new ItemStack(995, 5000));
                                    if ((var0.temporaryActionValue & GameUtil.bitFlag(1)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(2)) == 0 && (var0.temporaryActionValue & GameUtil.bitFlag(3)) == 0) {
                                        GodBookHandler.giveReplacementBook(var0, GodBookHandler.damagedGuthixBookId);
                                    }
                                } else {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, don't have enough money right now.", 591);
                                }
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Maybe next time.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 3886: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.ownsClueScroll()) {
                            return false;
                        }
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Hi!", 591);
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello adventurer.", "Let me show you something.", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("This scroll should lead to a treasure, but", "I'm not able to solve it.", 591);
                        return true;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Would you like to buy it for " + GameUtil.formatNumber((long)ServerSettings.clueMerchantPriceCoins) + " coins?", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showTwoOptions("Sure thing!", "No thanks.");
                        return true;
                    }
                    case 6: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Sure thing!", 591);
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 7: {
                        if (var0.getInventoryManager().containsItemAmount(995, ServerSettings.clueMerchantPriceCoins)) {
                            var0.getDialogueManager().showNpcOneLineDialogue("There you go! Good luck with that!", 591);
                            var0.getInventoryManager().removeItem(new ItemStack(995, ServerSettings.clueMerchantPriceCoins));
                            if (ServerSettings.clueMerchantClueLevel == 0) {
                                var1_1 = TreasureTrailManager.randomClueItemForLevel(GameUtil.randomInt(3) + 1);
                            } else {
                                var6_21 = ServerSettings.clueMerchantClueLevel;
                                if (var6_21 < 0) {
                                    var6_21 = 1;
                                } else if (var6_21 > 3) {
                                    var6_21 = 3;
                                }
                                var1_1 = TreasureTrailManager.randomClueItemForLevel(var6_21);
                            }
                            var0.getInventoryManager().addOrDropItem(new ItemStack(var1_1, 1));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("I said " + GameUtil.formatNumber((long)ServerSettings.clueMerchantPriceCoins) + " coins! You haven't got " + GameUtil.formatNumber((long)ServerSettings.clueMerchantPriceCoins) + " coins!", 614);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                }
                break;
            }
            case 659: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hi!", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hi! I'm Party Pete. Welcome to the Party Room!", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showFourOptions("So what's this room for?", "What's the big lever over there for?", "What's the gold chest for?", "I wanna party!");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("So what's this room for?", 591);
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What's the big lever over there for?", 591);
                                var0.getDialogueManager().setNextDialogueStep(10);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What's the gold chest for?", 591);
                                var0.getDialogueManager().setNextDialogueStep(15);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I wanna party!", 591);
                                var0.getDialogueManager().setNextDialogueStep(19);
                                return true;
                            }
                        }
                        break;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcOneLineDialogue("This room is for partying the night away!", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("How do you have a party in RuneScape?", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Get a few mates round, get the beers in and have fun!", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Some players organise parties so keep an eye open!", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Woop! Thanks Pete!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Simple. With the lever you can do some fun stuff.", 591);
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("What kind of stuff?", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showNpcFourLineDialogue("A balloon drop costs 1000 gold. For this you get 200", "balloons dropped across the whole of the party room.", "You can then have fun popping the balloons! If there", "are items in the Party Drop Chest they will be inside", 591);
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("the balloons! For 500 gold you can summon the Party", "Room Knights who will dance for your delight.", 591);
                        return true;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Their singing isn't a delight though!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Any items that are in the chest will be dropped inside", "the balloons when you pull the lever!", 591);
                        return true;
                    }
                    case 16: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Cool! Sounds like a fun way to do a drop party!", 591);
                        return true;
                    }
                    case 17: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Exactly!", 591);
                        return true;
                    }
                    case 18: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("A word of warning though. Any items that you put into", "the chest can't be taken out again and it costs 1000", "gold pieces for each balloon drop.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 19: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("I've won the Dance Trophy at the Kandarin Ball three", "years in a trot!", 591);
                        return true;
                    }
                    case 20: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Show me your moves Pete!", 591);
                        return true;
                    }
                    case 21: {
                        var1_2 = Npc.findByDefinitionId(659);
                        if (var1_2 == null) break;
                        var1_2.getUpdateState().setAnimation(866);
                    }
                }
                break;
            }
            case 3098: {
                if (!var0.getTelekineticTheatreController().mazeSolved) {
                    return false;
                }
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hi!", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Would you like to try another maze?", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("Yes please!", "No thanks.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes please!", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Very well, I shall teleport you.", 591);
                        return true;
                    }
                    case 6: {
                        var0.getTelekineticTheatreController().startNextMaze();
                        var0.getDialogueManager().finishDialogue();
                    }
                }
                break;
            }
            case 3307: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerTwoLineDialogue("What happened to the old man who used to be the", "doorman?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("You mean my father? He went into retirement. I've", "taken over the family business instead.", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Your father! But you don't look anything like him!", 591);
                        return true;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("No, fortunately for me I inherited my good looks from", "my mother.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 389: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.getQuestState(80) != 1) {
                            return false;
                        }
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello there, would you like me to enchant a battlestaff", "for 40k coins for you?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().finishDialogue();
                                var0.getDialogueManager().markDialogueInactive();
                                var0.getPacketSender().sendInterfaceModel(1734, 200, 1397);
                                var0.getPacketSender().sendInterfaceModel(1735, 200, 1395);
                                var0.getPacketSender().sendInterfaceModel(1736, 200, 1399);
                                var0.getPacketSender().sendInterfaceModel(1737, 200, 1393);
                                var0.getPacketSender().sendInterfaceModel(1738, 200, 3053);
                                var0.getPacketSender().sendInterfaceModel(15348, 200, 6562);
                                var0.getPacketSender().showInterface(205);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks.", 588);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 904: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hi.", 591);
                        if (var0.mageArenaProgressStage == 6 || !var0.hasMageArenaGodCape()) {
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hello adventurer, have you made your choice?", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("I have.", 591);
                        return true;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Good, good, I hope you have chosen well. I will now", "present you with a magic staff. This, along with the", "cape awarded to you by your chosen god, are all the", "weapons and armour you will need here.", 591);
                        return true;
                    }
                    case 5: {
                        var1_1 = 1;
                        if (var0.ownsItem(2412)) {
                            var1_1 = 2415;
                        }
                        if (var0.ownsItem(2413)) {
                            var1_1 = 2416;
                        }
                        if (var0.ownsItem(2414)) {
                            var1_1 = 2417;
                        }
                        var0.getDialogueManager().showItemMessage("The guardian hands you an ornate magic staff.", new ItemStack(var1_1, 1));
                        var0.getInventoryManager().addOrDropItem(new ItemStack(var1_1, 1));
                        var0.mageArenaProgressStage = 6;
                        return true;
                    }
                }
                break;
            }
            case 905: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.mageArenaProgressStage == 6) {
                            return false;
                        }
                        if (var0.mageArenaProgressStage == 0) {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Hello there. What is this place?", 591);
                            return true;
                        }
                        if (var0.mageArenaProgressStage <= 5) {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Hello, Kolodion.", 591);
                            if (var0.mageArenaProgressStage == 5) {
                                var0.getDialogueManager().setNextDialogueStep(52);
                            } else {
                                var0.getDialogueManager().setNextDialogueStep(10);
                            }
                            return true;
                        }
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("I am the great Kolodion, master of battle magic, and", "this is my battle arena. Top wizards travel from all over", "RuneScape to fight here.", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showThreeOptions("Can I fight here?", "What's the point of that?", "That's barbaric!");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can I fight here?", 591);
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                        }
                        var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                        var0.getDialogueManager().setNextDialogueStep(3);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("My arena is open to any high level wizard, but this is", "no game. Many wizards fall in this arena, never to rise", "again. The strongest mages have been destroyed.", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("If you're sure you want in?", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showTwoOptions("Yes indeedy.", "No I don't.");
                        return true;
                    }
                    case 8: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes indeedy.", 591);
                                var0.getDialogueManager().setNextDialogueStep(9);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No I don't.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Good, good. You have a healthy sense of competition.", 591);
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Remember, traveller - in my arena, hand-to-hand", "combat is useless. Your strength will diminish as you", "enter the arena, but the spells you can learn are", "amongst the most powerful in all of RuneScape.", 591);
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Before I can accept you in, we must duel.", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showTwoOptions("Okay, let's fight.", "No thanks.");
                        return true;
                    }
                    case 13: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Okay, let's fight.", 591);
                                var0.getDialogueManager().setNextDialogueStep(14);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcOneLineDialogue("I must first check that you are up to scratch.", 591);
                        return true;
                    }
                    case 15: {
                        if (var0.getSkillManager().getBaseLevel(6) >= 60) {
                            var0.getDialogueManager().showPlayerOneLineDialogue("You don't need to worry about that.", 591);
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("Sorry, you are not experienced enough to enter.", 591);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 16: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Not just any magician can enter - only the most", "powerful and most feared. Before you can use the", "power of this arena, you must prove yourself against", "me.", 591);
                        return true;
                    }
                    case 17: {
                        if (var0.getInteractionTarget().isNpc() && (var1_3 = (Npc)var0.getInteractionTarget()).getNpcId() == 905) {
                            var1_3.startMageArenaChallenge(var0, 3105, 3934, 0, null);
                        }
                        return false;
                    }
                    case 52: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hello, young mage. You're a tough one.", 591);
                        return true;
                    }
                    case 53: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("What now?", 591);
                        return true;
                    }
                    case 54: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Step into the magic pool. It will take you to a chamber.", "There, you must decide which god you will represent in", "the arena.", 591);
                        return true;
                    }
                    case 55: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Thanks, Kolodion.", 591);
                        return true;
                    }
                    case 56: {
                        var0.getDialogueManager().showNpcOneLineDialogue("That's what I'm here for.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 1263: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showNpcOneLineDialogue("Hello there, can I help you?", 591);
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showFourOptions("What do you do here?", "What's that you're wearing?", "Can you make me some armour please?", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you make me some armour please?", 591);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                            case 1: 
                            case 2: 
                            case 4: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Certainly, what would you like me to make?", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().finishDialogue();
                        var0.setInterfaceAction("splitbark");
        GameplayHelper.showSplitbarkProductionInterface(var0, 3385, 3387, 3389, 3391, 3393, "Helm", "Body", "Legs", "Gauntlets", "Boots", "Splitbark Armour");
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("There you go, enjoy your new armour!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 736: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Heya! What can I get you?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("What ales are you serving?", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Well, we've got Asgarnian Ale, Wizard's Mind Bomb", "and Dwarven Stout, all for only 3 coins.", 591);
                        return true;
                    }
                    case 4: {
                        var0.getDialogueManager().showFourOptions("One Asgarnian Ale, please.", "I'll try the Mind Bomb.", "Can I have a Dwarven Stout?", "I don't feel like any of those.");
                        return true;
                    }
                    case 5: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("One Asgarnian Ale, please.", 591);
                                var0.setTemporaryActionValue(1917);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'll try the Mind Bomb.", 591);
                                var0.setTemporaryActionValue(1907);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can I have a Dwarven Stout?", 591);
                                var0.setTemporaryActionValue(1913);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I don't feel like any of those.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 6: {
                        if (var0.getInventoryManager().containsItemAmount(995, 3)) {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Thanks, " + new Npc(var1_1).getDefinition().getName() + ".", 591);
                            var0.getInventoryManager().removeItem(new ItemStack(995, 3));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(var0.getTemporaryActionValue(), 1));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("I said 3 coins! You haven't got 3 coins!", 614);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                }
                break;
            }
            case 734: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showTwoOptions("Could I buy a beer please?", "Have you heard any rumours here?");
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Could I buy a beer please?", 591);
                                var0.getDialogueManager().setNextDialogueStep(3);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Have you heard any rumours here?", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 3: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sure, that will be 2 gold coins please.", 591);
                        return true;
                    }
                    case 4: {
                        if (var0.getInventoryManager().containsItemAmount(995, 2)) {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Ok, here you go.", 591);
                        } else {
                            var0.getDialogueManager().showPlayerOneLineDialogue("I don't have enough coins.", 591);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 5: {
                        var0.getInventoryManager().removeItem(new ItemStack(995, 2));
                        var0.getInventoryManager().addOrDropItem(new ItemStack(1917, 1));
                        var0.getDialogueManager().showTwoItemMessage("You buy a pint of beer!", "", new ItemStack(-1, 1), new ItemStack(1917, 1));
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("No, it hasn't been very busy lately.", 591);
                        return true;
                    }
                }
                break;
            }
            case 731: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Can I help you?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptions("I'll have a beer please.", "Any hints where I can go adventuring?", "Heard any good gossip?");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'll have a pint of beer please.", 591);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Any hints where I can go adventuring?", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Heard any good gossip?", 591);
                                var0.getDialogueManager().setNextDialogueStep(10);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Ok, that'll be two coins please.", 591);
                        return true;
                    }
                    case 5: {
                        if (var0.getInventoryManager().containsItemAmount(995, 2)) {
                            var0.getPacketSender().sendGameMessage("You buy a pint of beer.");
                            var0.getInventoryManager().removeItem(new ItemStack(995, 2));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(1917, 1));
                            break block0;
                        }
                        var0.getDialogueManager().showPlayerOneLineDialogue("Oh dear, I don't seem to have enough money!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Ooh, now. Let me see...", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Well there is the Varrock sewers. There are tales of", "untold horrors coming out at night and stealing babies", "from houses.", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Sounds perfect! Where's the entrance?", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcOneLineDialogue("It's just to the east of the palace.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showNpcFourLineDialogue("I'm not that well up on the gossip out here. I've heard", "that the bartender in the Blue Moon Inn has gone a", "little crazy, he keeps claiming he is a part of something", "called a computer game.", 591);
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("What that means, I don't know.", "That's probably old news by now though.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 733: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("What can I do yer for?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptions("A glass of your finest ale please.", "Can you recommend where an adventurer might make his fortune?", "Do you know where I can get some good equipment?");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("A glass of your finest ale please.", 591);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerTwoLineDialogue("Can you recommend where an adventurer", "might make his fortune?", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Do you know where I can get some good equipment?", 591);
                                var0.getDialogueManager().setNextDialogueStep(14);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("No problemo. That'll be 2 coins.", 591);
                        return true;
                    }
                    case 5: {
                        if (var0.getInventoryManager().containsItemAmount(995, 2)) {
                            var0.getPacketSender().sendGameMessage("You buy a pint of beer.");
                            var0.getInventoryManager().removeItem(new ItemStack(995, 2));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(1917, 1));
                            break block0;
                        }
                        var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, don't have that right now.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Ooh I don't know if I should be giving away information,", "makes the computer game too easy.", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showThreeOptions("Oh ah well...", "Computer game? What are you talking about?", "Just a small clue?");
                        return true;
                    }
                    case 8: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Oh ah well...", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Computer game? What are you talking about?", 591);
                                var0.getDialogueManager().setNextDialogueStep(9);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Just a small clue?", 591);
                                var0.getDialogueManager().setNextDialogueStep(13);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("This world around us... is a computer game...", "called RuneScape.", 591);
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showPlayerTwoLineDialogue("Nope, still don't understand what you are talking about.", "What's a computer?", 591);
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("It's a sort of magic box thing,", "which can do all sorts of stuff.", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("I give up. You're obviously completely mad!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Go and talk to bartender at the Jolly Boar Inn,", "he doesn't seem to mind giving away clues.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Well, there's the sword shop across the road, or ", "there's also all sort of shops up around the market.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 36: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("I'm the head gardener around here.", "If you're looking for woad leaves, or if you need help", "with owt, I'm yer man.", 589);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptionsWithTitle("What would you like to say?", "Yes please, I need woad leaves.", "How about ME helping YOU instead?", "Sorry, but I'm not interested.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes please, I need woad leaves.", 589);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("How much are you willing to pay?", 595);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showFourOptionsWithTitle("What would you like to say?", "How about 5 coins?", "How about 10 coins?", "How about 15 coins?", "How about 20 coins?");
                        return true;
                    }
                    case 6: {
                        switch (var3_18) {
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("How about 20 coins?", 589);
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                            case 1: 
                            case 2: 
                            case 3: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Ok that's more than fair.", 589);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Here, have two, you're a generous person.", 589);
                        return true;
                    }
                    case 9: {
                        if (var0.getInventoryManager().containsItemAmount(995, 20)) {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Thanks.", 589);
                            var0.getInventoryManager().removeItem(new ItemStack(995, 20));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(1793, 2));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("You don't have enough coins with you.", 589);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                }
                break;
            }
            case 922: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("What can I help you with?", 589);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showFourOptions("What could you make for me?", "Cool, do you turn people into frogs?", "You mad old witch, you can't help me.", "Can you make dyes for me please?");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What could you make for me?", 589);
                                var0.getDialogueManager().setNextDialogueStep(11);
                                return true;
                            }
                            case 2: 
                            case 3: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you make dyes for me please?", 589);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("What sort of dye would you like? Red, yellow or blue?", 589);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showThreeOptions("What do you need to make red dye?", "What do you need to make yellow dye?", "What do you need to make blue dye?");
                        return true;
                    }
                    case 6: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What do you need to make red dye?", 589);
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What do you need to make yellow dye?", 589);
                                var0.getDialogueManager().setNextDialogueStep(14);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What do you need to make blue dye?", 589);
                                var0.getDialogueManager().setNextDialogueStep(18);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcOneLineDialogue("3 lots of redberries and 5 coins to you.", 589);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showFiveOptions("Okay, make me some red dye please.", "I don't think I have all the ingredients yet.", "I can do without dye at that price.", "Where do I get redberries?", "What other colours can you make?");
                        return true;
                    }
                    case 9: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Ok make me some red dye please.", 589);
                                var0.getDialogueManager().setNextDialogueStep(10);
                                return true;
                            }
                            case 2: 
                            case 3: {
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(8);
                                return true;
                            }
                            case 5: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What other colours can you make?", 589);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 10: {
                        if (var0.getInventoryManager().containsItemAmount(1951, 3) && var0.getInventoryManager().containsItemAmount(995, 5)) {
                            var0.getDialogueManager().showTwoItemMessage("You hand the berries and payment to Aggie. Aggie", "produces a red bottle and hands it to you.", new ItemStack(-1, 1), new ItemStack(1763, 1));
                            var0.getInventoryManager().removeItem(new ItemStack(1951, 3));
                            var0.getInventoryManager().removeItem(new ItemStack(995, 5));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(1763, 1));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("You don't have the ingredients for me to do that.", 589);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcFourLineDialogue("I mostly just make what I find pretty. I sometimes", "make dye for the women's clothes to brighten the place", "up. I can make red, yellow and blue dyes. If you'd like", "some, just bring me the appropriate ingredients.", 589);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showFourOptions("What do you need to make red dye?", "What do you need to make yellow dye?", "What do you need to make blue dye?", "No thanks, I am happy the colour I am.");
                        return true;
                    }
                    case 13: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What do you need to make red dye?", 589);
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What do you need to make yellow dye?", 589);
                                var0.getDialogueManager().setNextDialogueStep(14);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What do you need to make blue dye?", 589);
                                var0.getDialogueManager().setNextDialogueStep(18);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks, I am happy the colour I am.", 589);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Yellow is a strange colour to get, comes from onion", "skins. I need 2 onions and 5 coins to make yellow dye.", 589);
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showFiveOptions("Okay, make me some yellow dye please.", "I don't think I have all the ingredients yet.", "I can do without dye at that price.", "Where do I get onions?", "What other colours can you make?");
                        return true;
                    }
                    case 16: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Ok make me some yellow dye please.", 589);
                                var0.getDialogueManager().setNextDialogueStep(17);
                                return true;
                            }
                            case 2: 
                            case 3: {
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(15);
                                return true;
                            }
                            case 5: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What other colours can you make?", 589);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 17: {
                        if (var0.getInventoryManager().containsItemAmount(1957, 2) && var0.getInventoryManager().containsItemAmount(995, 5)) {
                            var0.getDialogueManager().showTwoItemMessage("You hand the onions and payment to Aggie. Aggie", "produces a yellow bottle and hands it to you.", new ItemStack(-1, 1), new ItemStack(1765, 1));
                            var0.getInventoryManager().removeItem(new ItemStack(1957, 2));
                            var0.getInventoryManager().removeItem(new ItemStack(995, 5));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(1765, 1));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("You don't have the ingredients for me to do that.", 589);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 18: {
                        var0.getDialogueManager().showNpcOneLineDialogue("2 woad leaves and 5 coins to you.", 589);
                        return true;
                    }
                    case 19: {
                        var0.getDialogueManager().showFiveOptions("Okay, make me some blue dye please.", "I don't think I have all the ingredients yet.", "I can do without dye at that price.", "Where do I get woad leaves?", "What other colours can you make?");
                        return true;
                    }
                    case 20: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Ok make me some blue dye please.", 589);
                                var0.getDialogueManager().setNextDialogueStep(21);
                                return true;
                            }
                            case 2: 
                            case 3: {
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showOneLineStatement("This option is currently missing...");
                                var0.getDialogueManager().setNextDialogueStep(19);
                                return true;
                            }
                            case 5: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What other colours can you make?", 589);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 21: {
                        if (var0.getInventoryManager().containsItemAmount(1793, 2) && var0.getInventoryManager().containsItemAmount(995, 5)) {
                            var0.getDialogueManager().showTwoItemMessage("You hand the woad leaves and payment to Aggie. Aggie", "produces a blue bottle and hands it to you.", new ItemStack(-1, 1), new ItemStack(1767, 1));
                            var0.getInventoryManager().removeItem(new ItemStack(1793, 2));
                            var0.getInventoryManager().removeItem(new ItemStack(995, 5));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(1767, 1));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("You don't have the ingredients for me to do that.", 589);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                }
                break;
            }
            case 918: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Why, hello there, lad. Me friends call me Ned. I was a", "man of the sea, but it's past me now. Could I be", "making or selling you some rope?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes, I would like some rope.", "No thanks Ned, I don't need any.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes, I would like some rope.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks Ned, I don't need any.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Well, I can sell you some rope for 15 coins. Or I can", "be making you some if you gets me 4 balls of wool. I", "strands them together I does, makes em strong.", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("You make rope from wool?", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Of course you can!", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("I thought you needed hemp or jute.", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Do you want some rope or not?", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showThreeOptions("Okay, please sell me some rope.", "I have balls of wool. Could you make me some rope?", "That's a little more than I want to pay.");
                        return true;
                    }
                    case 10: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Okay, please sell me some rope.", 591);
                                var0.getDialogueManager().setNextDialogueStep(11);
                                if (!var0.getInventoryManager().containsItemAmount(995, 15)) {
                                    var0.getDialogueManager().finishDialogue();
                                }
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I have balls of wool. Could you make me some rope?", 591);
                                var0.getDialogueManager().setNextDialogueStep(14);
                                if (!var0.getInventoryManager().containsItemAmount(1759, 4)) {
                                    var0.getDialogueManager().finishDialogue();
                                }
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("That's a little more than I want to pay.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcOneLineDialogue("There you go, finest rope in RuneScape.", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showOneLineStatement("You hand Ned 15 coins. Ned gives you a coil of rope.");
                        return true;
                    }
                    case 13: {
                        if (!var0.getInventoryManager().containsItemAmount(995, 15)) break;
                        var0.getInventoryManager().removeItem(new ItemStack(995, 15));
                        var0.getInventoryManager().addOrDropItem(new ItemStack(954, 1));
                        var0.getDialogueManager().finishDialogue();
                        break;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcOneLineDialogue("There you go, finest rope in RuneScape.", 591);
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showOneLineStatement("You hand Ned 4 balls of wool. Ned gives you a coil of rope.");
                        return true;
                    }
                    case 16: {
                        if (!var0.getInventoryManager().containsItemAmount(1759, 4)) break;
                        var0.getInventoryManager().removeItem(new ItemStack(1759, 4));
                        var0.getInventoryManager().addOrDropItem(new ItemStack(954, 1));
                        var0.getDialogueManager().finishDialogue();
                    }
                }
                break;
            }
            case 379: {
                if (var0.bO != 10) {
                    switch (var0.getDialogueManager().getDialogueStep()) {
                        case 1: {
                            var0.getDialogueManager().showNpcOneLineDialogue("Hello I'm Luthas, I run the banana plantation here.", 591);
                            return true;
                        }
                        case 2: {
                            var0.getDialogueManager().showTwoOptions("Could you offer me employment on your plantation?", "That customs officer is annoying isn't she?");
                            return true;
                        }
                        case 3: {
                            switch (var3_18) {
                                case 1: {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Could you offer me employment on your plantation?", 591);
                                    return true;
                                }
                                case 2: {
                                    var0.getDialogueManager().finishDialogue();
                                }
                            }
                            break block0;
                        }
                        case 4: {
                            var0.getDialogueManager().showNpcTwoLineDialogue("Yes, I can sort something out. There's a crate ready to", "be loaded onto the ship.", 591);
                            return true;
                        }
                        case 5: {
                            var0.getDialogueManager().showNpcThreeLineDialogue("You wouldn't believe the demand for bananas from", "Wydin's shop over in Port Sarim. I think this is the", "third crate I've shipped him this month..", 591);
                            return true;
                        }
                        case 6: {
                            var0.getDialogueManager().showNpcTwoLineDialogue("If you could fill it up with bananas, I'll pay you 30", "gold.", 591);
                            var0.getDialogueManager().finishDialogue();
                            return true;
                        }
                    }
                    break;
                }
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("I've filled a crate with bananas.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Well done, here's your payment.", 591);
                        return true;
                    }
                    case 3: {
                        var0.bO = 0;
                        if (var0.getQuestState(10) == 3) {
                            var0.setQuestState(10, 4);
                        }
                        var0.getPacketSender().sendGameMessage("Luthas hands you 30 coins.");
                        var0.getInventoryManager().addOrDropItem(new ItemStack(995, 30));
                        var0.getDialogueManager().finishDialogue();
                    }
                }
                break;
            }
            case 488: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hi, are you busy?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("What would you like to talk about?", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("Talk about the Observatory Quest.", "Talk about Treasure Trails.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().finishDialogue();
                                var0.getPacketSender().closeInterfaces();
                                return true;
                            }
                            case 2: {
                                if (var0.ownsItem(2576)) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("I have already given you a chart!", 591);
                                    var0.getDialogueManager().finishDialogue();
                                    return true;
                                }
                                if (var0.getInventoryManager().containsItem(2574) && var0.getInventoryManager().containsItem(2575)) {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("I've got the sextant and watch!", 591);
                                    var0.getDialogueManager().setNextDialogueStep(14);
                                    return true;
                                }
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you teach me to solve Treasure Trail clues?", 591);
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Ah, I get asked about treasure trails all the time!", "Listen carefully and I shall tell you what I know...", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Lots of clues have degrees and minutes written on", "them. These are the coordinates of the place where the", "treasure is buried.", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("You have to walk to the correct spot, so that your", "coordinates are exactly the same as the values written", "on the clue scroll.", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("To do this, you must use a sextant, a watch and a", "chart to find the coordinates of where you are.", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Once you know the coordinates of your position, you", "know which way you have to walk to get to the", "treasure's coordinates!", 591);
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Riiight. So where do I get these items from?", 591);
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcFourLineDialogue("I think Murphy, the owner of the Fishing Trawler", "moored at Port Khazard, might be able to spare you a", "sextant. After that, the nearest clock tower is south of", "Ardougne - you could probably get a watch there. I've", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("got plenty of charts myself; just come back here when", "you've got the sextant and watch, and I'll give you one", "and teach you how to use them.", 591);
                        var0.D = true;
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Thanks, I'll see you later.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Well done!", 591);
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("You use the sextant to measure the angle that the sun", "is currently at. You need the watch so that you know", "what the time is back here at the observatory.", 591);
                        return true;
                    }
                    case 16: {
                        var0.getDialogueManager().showNpcFourLineDialogue("You then need this chart to work out your position.", "Your position is recorded in terms of latitude and", "longitude. Latitude is your position above the equator", "and longitude is your position relative to here.", 591);
                        return true;
                    }
                    case 17: {
                        var0.getInventoryManager().addOrDropItem(new ItemStack(2576, 1));
                        var0.getDialogueManager().showItemIdMessage("The professor has given you a navigation chart.", 2576);
                        return true;
                    }
                    case 18: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("So, if you have your sextant, watch and chart with you", "then you can work out exactly where you are!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 3863: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("What can I do for you?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("I'd like to set up trade offers please.", "I'm fine, thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'd like to set up trade offers please.", 591);
                                var0.getDialogueManager().setNextDialogueStep(4);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'm fine, thanks.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        GrandExchangeManager.openGrandExchange(var0);
                        var0.getDialogueManager().markDialogueInactive();
                    }
                }
                break;
            }
            case 223: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.ownsItem(2575) || !var0.D) {
                            return false;
                        }
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hello.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hello, traveller, how can I help?", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("I'm trying to learn how to be a navigator.", 591);
                        return true;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("I don't know if I can help you there.", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showPlayerTwoLineDialogue("The professor from the Observatory says that I need a", "watch.", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Ah, that I can help you with. I've been tinkering with", "this new idea of a watch and made a few. The problem", "is the villagers don't see the point as they have the Clock", "Tower!", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Can I have one?", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcOneLineDialogue("You can have this one! It's the display model.", 591);
                        return true;
                    }
                    case 9: {
                        var0.getInventoryManager().addOrDropItem(new ItemStack(2575, 1));
                        var0.getDialogueManager().showItemIdMessage("Brother Kojo has given you a watch.", 2575);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 463: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.ownsItem(2574) || !var0.D) {
                            return false;
                        }
                        var0.getDialogueManager().showPlayerOneLineDialogue("Ahoy there!", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Ahoy!", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("I'm trying to learn how to be a navigator.", 591);
                        return true;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Well, you've come to the right place, m'hearty! What do", "you need to know?", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showPlayerTwoLineDialogue("The professor said that I need to have a sextant. Do", "you know where I can get one?", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hmm. I used to use a sextant when I was a young", "fella.", 591);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Do you still have it?", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Aye.", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Could I have it?", 591);
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Aye.", 591);
                        return true;
                    }
                    case 11: {
                        var0.getInventoryManager().addOrDropItem(new ItemStack(2574, 1));
                        var0.getDialogueManager().showItemIdMessage("Murphy has given you his old sextant.", 2574);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Don't you still need it?", 591);
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("I can tell from the taste of the sea spray where I am,", "m'hearty!", 591);
                        return true;
                    }
                    case 14: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Wow!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 543: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Would you like to buy a nice kebab? Only one gold.", 589);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("I think I'll give it a miss.", "Yes please.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I think I'll give it a miss.", 601);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes please.", 591);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        if (var0.getInventoryManager().containsItemStack(new ItemStack(995))) {
                            var0.getInventoryManager().removeItem(new ItemStack(995));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(1971));
                            var0.getPacketSender().sendGameMessage("You buy a kebab.");
                            break block0;
                        }
                        var0.getDialogueManager().showPlayerOneLineDialogue("Oops, I forgot to bring any money with me.", 599);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 539: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Do you want to buy any fine silks?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("How much are they?", "No, silk doesn't suit me.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("How much are they?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, silk doesn't suit me.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("3gp.", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showTwoOptions("No, that's too much for me.", "Okay, that sounds good.");
                        return true;
                    }
                    case 6: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, that's too much for me.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Okay, that sounds good.", 591);
                                var0.getDialogueManager().setNextDialogueStep(12);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcOneLineDialogue("2gp and that's as low as I'll go.", 591);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("I'm not selling it for any less. You'll only", "go and sell it in Varrock for a profit.", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showTwoOptions("2gp sounds good.", "No, really, I don't want it.");
                        return true;
                    }
                    case 10: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("2gp sounds good.", 591);
                                var0.getDialogueManager().setNextDialogueStep(14);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, really, I don't want it.", 591);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Okay, but that's the best price your going to get.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 12: {
                        if (var0.getInventoryManager().containsItemStack(new ItemStack(995, 3))) {
                            var0.getInventoryManager().removeItem(new ItemStack(995, 3));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(950));
                            var0.getDialogueManager().showItemMessage("You buy some silk for 3gp.", new ItemStack(950));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Oh dear. I don't have enough money.", 599);
                        }
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Well, come back when you do have some money.", 614);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 14: {
                        if (var0.getInventoryManager().containsItemStack(new ItemStack(995, 2))) {
                            var0.getInventoryManager().removeItem(new ItemStack(995, 2));
                            var0.getInventoryManager().addOrDropItem(new ItemStack(950));
                            var0.getDialogueManager().showItemMessage("You buy some silk for 2gp.", new ItemStack(950));
                            var0.getDialogueManager().finishDialogue();
                        } else {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Oh dear. I don't have enough money.", 599);
                            var0.getDialogueManager().setNextDialogueStep(13);
                        }
                        return true;
                    }
                }
                break;
            }
            case 2238: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hello there, can I help you?", 588);
                        var1_1 = GameUtil.randomInclusive(2);
                        var0.getDialogueManager().setNextDialogueStep(var1_1 == 0 ? 2 : (var1_1 == 1 ? 6 : 4));
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showFourOptions("Where am I?", "How are you today?", "Are there any quests I can do here?", "Where can I get a haircut like yours?");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcOneLineDialogue("This is the town of Lumbridge my friend.", 588);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcTwoLineDialogue("Aye, not too bad thank you. Lovely weather", "in 06Scape this fine day.", 588);
                                var0.getDialogueManager().setNextDialogueStep(10);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showNpcOneLineDialogue("What kind of quest are you looking for?", 595);
                                var0.getDialogueManager().setNextDialogueStep(20);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showNpcOneLineDialogue("Yes, it does look like you need a hairdresser.", 588);
                                var0.getDialogueManager().setNextDialogueStep(15);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showThreeOptions("How are you today?", "Are there any quests I can do here?", "Your shoe lace is united.");
                        return true;
                    }
                    case 5: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcTwoLineDialogue("Aye, not too bad thank you. Lovely weather", "in 06Scape this fine day.", 588);
                                var0.getDialogueManager().setNextDialogueStep(10);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("What kind of quest are you looking for?", 595);
                                var0.getDialogueManager().setNextDialogueStep(20);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showNpcOneLineDialogue("No it's not!", 614);
                                var0.getDialogueManager().setNextDialogueStep(18);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 6: {
                        var0.getDialogueManager().showThreeOptions("Do you have anything of value which I can have?", "Are there any quests I can do here?", "Can I buy your stick?");
                        return true;
                    }
                    case 7: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcOneLineDialogue("Are you asking for free stuff?", 595);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("What kind of quest are you looking for?", 595);
                                var0.getDialogueManager().setNextDialogueStep(20);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showNpcTwoLineDialogue("It's not a stick! I'll have you know it's", "a very powerful staff!", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 8: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Well... er... yes.", 595);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("No I dont not have anything I can give you.", "If I did have anything of value I wouldn't", "want to give it away.", 614);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Weather?", 589);
                        return true;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Yes weather, you know.", 588);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showNpcFourLineDialogue("The state or condition of the atmosphere", "at a time and place, with respect to variables", "such as temperature, moisture, wind velocity,", "and barometric pressure.", 595);
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("...", 589);
                        return true;
                    }
                    case 14: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Not just a pretty face eh? Ha ha ha.", 605);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Oh thanks.", 614);
                        return true;
                    }
                    case 16: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("No problem. The hairdresser in Falador", "will probably be able to sort you out.", 605);
                        return true;
                    }
                    case 17: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("The Lumbridge general store sells useful maps", "if you don't know the way.", 605);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 18: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("No you're right. I have nothing to back that up.", 614);
                        return true;
                    }
                    case 19: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Fool! Leave me alone!", 614);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 20: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sorry, quests have not been added yet.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 801: {
                block623 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hello brother, welcome to our monastery.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptions("Can you heal me? I'm injured.", "Can I climb up those stairs?", "Good bye.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getSkillManager().getCurrentLevels()[3] < var0.getSkillManager().getBaseLevel(3)) {
                                    if (var0.getInteractionTarget() != null) {
                                        var0.getInteractionTarget().getUpdateState().setAnimation(717);
                                    }
                                    var0.getDialogueManager().showNpcOneLineDialogue("Sure, here you go.", 591);
                                    var0.heal((int)((double)var0.getSkillManager().getBaseLevel(3) * 0.3));
                                    var0.getUpdateState().setGraphic(84);
                                    break block623;
                                }
                                var0.getDialogueManager().showNpcOneLineDialogue("You already have full hp.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcFourLineDialogue("Up those stairs is the prayer guild. You need a", "level of 31 Prayer to enter. There you will find", "an altar that can boost your prayer by 2 points,", "as well as some monk robes.", 591);
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 9998: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showTwoOptionsWithTitle("Let the sigil teleport you when worn?", "Yes", "No.");
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                var0.getPacketSender().sendGameMessage("The 10th squad sigil begins to shake violently!");
                                var0.setActionLocked(true);
                                var0.getPacketSender().showInterface(8677);
                                var1_4 = var0;
                                var6_22 = new TenthSquadSigilTeleportTask(5, var1_4);
                                World.getTaskScheduler().schedule(var6_22);
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 9999: {
                var0.getDialogueManager().setDialogueNpcId(926);
                block639 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.botEnabled) {
                            var0.getPacketSender().queueRelativeMovementStep(var0.getPosition().getX() < 3268 ? 1 : -1, 0, true);
                            var0.getPacketSender().openDoubleDoorPair(2882, 2883, 3268, 3227, 3268, 3228, 0);
                            break;
                        }
                        var0.getDialogueManager().showPlayerOneLineDialogue("Can I come through this gate?", 591);
                        return true;
                    }
                    case 2: {
                        if (var0.getQuestState(11) == 1 || var0.getQuestState(11) == 9) {
                            var0.getDialogueManager().showNpcOneLineDialogue("You may pass for free, you are a friend of Al-Kharid.", 591);
                            var0.getDialogueManager().setNextDialogueStep(19);
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("You must pay a toll of 10 gold coins to pass.", 591);
                            if (!var0.getInventoryManager().containsItemAmount(995, 10)) {
                                var0.getDialogueManager().setNextDialogueStep(7);
                            }
                        }
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showThreeOptions("Ok, I'll pay.", "Who does the money go to?", "No thanks, I'll walk around.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                if (!var0.getInventoryManager().containsItemAmount(995, 10)) break block639;
                                var0.getInventoryManager().removeItem(new ItemStack(995, 10));
                                var0.getPacketSender().queueRelativeMovementStep(var0.getPosition().getX() < 3268 ? 1 : -1, 0, true);
                                var0.getPacketSender().openDoubleDoorPair(2882, 2883, 3268, 3227, 3268, 3228, 0);
                                break block639;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcTwoLineDialogue("The money goes to the city of Al-Kharid.", "Will you pay the toll?", 591);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showNpcOneLineDialogue("As you wish. Don't go too near the scorpions.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 5: {
                        var0.getDialogueManager().showTwoOptions("Ok, I'll pay.", "No thanks, I'll walk around.");
                        return true;
                    }
                    case 6: {
                        switch (var3_18) {
                            case 1: {
                                if (!var0.getInventoryManager().containsItemAmount(995, 10)) break block639;
                                var0.getInventoryManager().removeItem(new ItemStack(995, 10));
                                var0.getPacketSender().queueRelativeMovementStep(var0.getPosition().getX() < 3268 ? 1 : -1, 0, true);
                                var0.getPacketSender().openDoubleDoorPair(2882, 2883, 3268, 3227, 3268, 3228, 0);
                                break block639;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("As you wish. Don't go too near the scorpions.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 7: {
                        var0.getDialogueManager().showTwoOptions("Who does the money go to?", "I haven't got that much.");
                        return true;
                    }
                    case 19: {
                        var0.getPacketSender().queueRelativeMovementStep(var0.getPosition().getX() < 3268 ? 1 : -1, 0, true);
                        var0.getPacketSender().openDoubleDoorPair(2882, 2883, 3268, 3227, 3268, 3228, 0);
                        break;
                    }
                    case 8: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcOneLineDialogue("The money goes to the city of Al-Kharid.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 166: 
            case 494: 
            case 495: 
            case 496: 
            case 498: 
            case 902: 
            case 2619: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("What can I do for you?", 591);
                        return true;
                    }
                    case 2: {
                        if (ServerSettings.cacheVersion >= 336) {
                            if (PartyRoomManager.partyChestValue >= 1000000 && PartyRoomManager.balloonDropPending) {
                                var0.getDialogueManager().showFourOptions("I would like to access my bank account.", "I would like to edit my Bank Pin settings.", "Teleport me to Party Room.", "Nothing.");
                            } else {
                                var0.getDialogueManager().showThreeOptions("I would like to access my bank account.", "I would like to edit my Bank Pin settings.", "Nothing.");
                            }
                        } else if (PartyRoomManager.partyChestValue >= 1000000 && PartyRoomManager.balloonDropPending) {
                            var0.getDialogueManager().showThreeOptions("I would like to access my bank account.", "Teleport me to Party Room.", "Nothing.");
                        } else {
                            var0.getDialogueManager().showTwoOptions("I would like to access my bank account.", "Nothing.");
                        }
                        return true;
                    }
                    case 3: {
                        if (ServerSettings.cacheVersion >= 336) {
                            switch (var3_18) {
                                case 1: {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("I would like to access my bank account.", 591);
                                    return true;
                                }
                                case 2: {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("I would like to edit my Bank Pin settings.", 591);
                                    var0.getDialogueManager().setNextDialogueStep(6);
                                    return true;
                                }
                                case 3: {
                                    if (PartyRoomManager.partyChestValue >= 1000000 && PartyRoomManager.balloonDropPending) {
                                        var0.getDialogueManager().showPlayerOneLineDialogue("Teleport me to Party Room please.", 591);
                                        var0.getDialogueManager().setNextDialogueStep(30);
                                    } else {
                                        var0.getDialogueManager().showPlayerOneLineDialogue("Nothing.", 591);
                                        var0.getDialogueManager().setNextDialogueStep(5);
                                    }
                                    return true;
                                }
                                case 4: {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Nothing.", 591);
                                    var0.getDialogueManager().setNextDialogueStep(5);
                                    return true;
                                }
                            }
                            break;
                        }
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I would like to access my bank account.", 591);
                                return true;
                            }
                            case 2: {
                                if (PartyRoomManager.partyChestValue >= 1000000 && PartyRoomManager.balloonDropPending) {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Teleport me to Party Room please.", 591);
                                    var0.getDialogueManager().setNextDialogueStep(30);
                                } else {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Nothing.", 591);
                                    var0.getDialogueManager().setNextDialogueStep(5);
                                }
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Nothing.", 591);
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        BankManager.openBank(var0);
                        var0.getDialogueManager().markDialogueInactive();
                        break;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Well, just let me know when I can help.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("What would you like to do?", 591);
                        return true;
                    }
                    case 7: {
                        if (var0.getBankPinManager().hasPin() && !var0.getBankPinManager().hasPendingPinChange()) {
                            var0.getDialogueManager().showTwoOptions("I would like to change my bank pin.", "I would like to delete my bank pin.");
                        } else if (var0.getBankPinManager().hasPin() && var0.getBankPinManager().hasPendingPinChange()) {
                            var0.getDialogueManager().showTwoOptions("I would like to delete my pending bank pin request.", "No, nevermind.");
                            var0.getDialogueManager().setNextDialogueStep(22);
                        } else {
                            var0.getDialogueManager().showTwoOptions("I would like to set a bank pin.", "No, nevermind.");
                            var0.getDialogueManager().setNextDialogueStep(28);
                        }
                        return true;
                    }
                    case 8: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I would like to change my bank pin.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I would like to delete my bank pin.", 591);
                                var0.getDialogueManager().setNextDialogueStep(18);
                                return true;
                            }
                        }
                        break;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Please carefully select your bank pin.", 591);
                        return true;
                    }
                    case 10: {
                        var0.getBankPinManager().setEntryMode(BankPinEntryMode.b);
                        var0.getDialogueManager().markDialogueInactive();
                        break;
                    }
                    case 11: {
                        var1_5 = var0.getBankPinManager().getPendingPin();
                        var0.getDialogueManager().showNpcTwoLineDialogue("Your bank pin will be set to " + var1_5[0] + " " + var1_5[1] + " " + var1_5[2] + " " + var1_5[3] + ".", "Does that sound correct?", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showThreeOptions("Yes.", "No, may I try again?", "No, nevermind.");
                        return true;
                    }
                    case 13: {
                        switch (var3_18) {
                            case 1: {
                                var0.getBankPinManager().requestPinChange();
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, may I try again?", 591);
                                var0.getDialogueManager().setNextDialogueStep(16);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, nevermind.", 591);
                                var0.getDialogueManager().setNextDialogueStep(17);
                                return true;
                            }
                        }
                        break;
                    }
                    case 14: {
                        if (var0.getBankPinManager().hasPin()) {
                            var0.getDialogueManager().showNpcTwoLineDialogue("Changes will take affect in 7 days.", "Return to me to edit or delete this change.", 591);
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("Your bank pin will be set accordingly.", 591);
                        }
                        var0.getBankPinManager().processPendingPinChanges();
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Will do.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 16: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sure.", 591);
                        var0.getBankPinManager().clearPendingPinChange();
                        var0.getDialogueManager().setNextDialogueStep(10);
                        return true;
                    }
                    case 17: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Return to me if you change your mind.", 591);
                        var0.getBankPinManager().clearPendingPinChange();
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 18: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("I would like to delete my bank pin.", 591);
                        return true;
                    }
                    case 19: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Are you sure you would like to delete your bank pin?", 591);
                        return true;
                    }
                    case 20: {
                        var0.getDialogueManager().showTwoOptions("Yes.", "No, nevermind.");
                        return true;
                    }
                    case 21: {
                        switch (var3_18) {
                            case 1: {
                                var0.getBankPinManager().requestPinDeletion();
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes.", 591);
                                var0.getDialogueManager().setNextDialogueStep(14);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, nevermind.", 591);
                                var0.getDialogueManager().setNextDialogueStep(29);
                                return true;
                            }
                        }
                        break;
                    }
                    case 22: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I would like to delete my pending bank pin request.", 591);
                                return true;
                            }
                        }
                        break;
                    }
                    case 23: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Are you sure?", "This clears any deletion or change request.", 591);
                        return true;
                    }
                    case 24: {
                        var0.getDialogueManager().showTwoOptions("Yes.", "No, nevermind.");
                        return true;
                    }
                    case 25: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, nevermind.", 591);
                                var0.getDialogueManager().setNextDialogueStep(29);
                                return true;
                            }
                        }
                        break;
                    }
                    case 26: {
                        var0.getBankPinManager().clearPendingPinChange();
                        var0.getDialogueManager().showNpcOneLineDialogue("Your pending bank pin request has been deleted.", 591);
                        return true;
                    }
                    case 27: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Thanks.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 28: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I would like to set my bank pin.", 591);
                                var0.getDialogueManager().setNextDialogueStep(9);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, nevermind.", 591);
                                return true;
                            }
                        }
                        break;
                    }
                    case 29: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Return to me if you change your mind.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 30: {
                        var0.getTeleportManager().castItemTeleport(new Position(2736, 3477, 0));
                    }
                }
                break;
            }
            case 10001: {
                block729 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showOneLineStatement("You've found a hidden tunnel, do you want to enter?");
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yeah I'm fearless!", "No way, that looks scary!");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                BarrowsManager.generateTunnelRoute(var0, true);
                                break block729;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No way, that looks scary!", 596);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 960: 
            case 961: 
            case 962: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Hey, what's up? you can call me " + NpcDefinition.forId(var0.getInteractionTargetId()).getName(), "my mission here is to take care of those players", "who would need some help to cure their wounds", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("So, anything I can do for someone like you?", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("Yes, I would like you to heal me please.", "No, I am just playing around.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes, I would like you to heal me please.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, I am just playing around.", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                    }
                    case 5: {
                        var0.getDuelSession().restoreHitpoints();
                        break block0;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("So don't waste my time, seriously...", 614);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 741: {
                block750 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.getQuestState(5) == 1 && !var0.ownsItem(1540)) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Hello, welcome to my kingdom.", 591);
                            return true;
                        }
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Can I have an anti-fire shield?", "Nevermind.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcOneLineDialogue("Sure, it will only cost you 1k.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Nevermind.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showTwoOptions("Ok, here you go.", "Nevermind.");
                        return true;
                    }
                    case 5: {
                        switch (var3_18) {
                            case 1: {
                                if (!var0.getInventoryManager().containsItemStack(new ItemStack(995, 1000))) {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, I don't have enough coins.", 599);
                                } else if (var0.getInventoryManager().getContainer().getFreeSlots() <= 0 && var0.getInventoryManager().getContainer().getItemAmount(995) != 1000) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("Looks like you don't have enough inventory space.", 591);
                                } else {
                                    var0.getInventoryManager().removeItem(new ItemStack(995, 1000));
                                    var0.getInventoryManager().addItem(new ItemStack(1540, 1));
                                    break block750;
                                }
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Nevermind.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 656: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Be careful going in there! You are unarmed, and there", "is much evilness lurking down there! The evilness seems", "to block off our contact with our gods,", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("so our prayers seem to have less effect down there. Oh,", "also, you won't be able to come back this way - This", "ladder only goes one way!", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("The only exit from the caves below is a portal which", "leads only to the deepest wilderness!", 591);
                        return true;
                    }
                    case 4: {
                        var0.getDialogueManager().showTwoOptions("I don't think I'm strong enough to enter then.", "Well that is a risk I will have to take.");
                        return true;
                    }
                    case 5: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I don't think I'm strong enough to enter then.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Well that is a risk I will have to take.", 591);
                                return true;
                            }
                        }
                        break;
                    }
                    case 6: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2822, 9774, 0));
                        var0.getSkillManager().setCurrentLevel(5, 1);
                        var0.getSkillManager().refreshSkill(5);
                        var0.getDialogueManager().finishDialogue();
                    }
                }
                break;
            }
            case 658: 
            case 2728: 
            case 2729: {
                block777 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to sail to Entrana?", "I will need to check you for dangerous equipment.", 591);
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                TravelManager.handleShipRoute(var0, ShipRoute.PORT_SARIM_TO_ENTRANA);
                                var0.getDialogueManager().markDialogueInactive();
                                break block777;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 1304: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hello. Can I get a ride on your ship?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello again, brother Rilkal. If you're ready to jump", "aboard, we're all ready to set sail with the tide!", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("Let's go!", "Actually, no.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Let's go!", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Actually, no.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 5: {
                        TravelManager.handleShipRoute(var0, ShipRoute.RELLEKKA_TO_MISCELLANIA);
                        var0.getDialogueManager().markDialogueInactive();
                        var0.getPacketSender().sendGameMessage("You board the longship...");
                    }
                }
                break;
            }
            case 1385: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Hello. Can I get a ride on your ship?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello again, brother Rilkal. If you're ready to jump", "aboard, we're all ready to set sail with the tide!", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("Let's go!", "Actually, no.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Let's go!", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Actually, no.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 5: {
                        TravelManager.handleShipRoute(var0, ShipRoute.MISCELLANIA_TO_RELLEKKA);
                        var0.getDialogueManager().markDialogueInactive();
                        var0.getPacketSender().sendGameMessage("You board the longship...");
                    }
                }
                break;
            }
            case 657: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Would you like to sail back to Port Sarim?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                TravelManager.handleShipRoute(var0, ShipRoute.ENTRANA_TO_PORT_SARIM);
                                var0.getDialogueManager().markDialogueInactive();
                                break block0;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("How dare you try to take dangerous equipment?", "Come back when you have left it all behind.", 614);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 10088: {
                block818 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var1_6 = ItemCombinationHandler.forBrokenToolItemId(var0.temporaryActionValue);
                        if (var1_6 == null) break;
                        var0.getDialogueManager().setDialogueNpcId(var0.O);
                        var6_23 = "free";
                        if (var1_6.getRepairCostCoins() != 0) {
                            var6_23 = String.valueOf(var1_6.getRepairCostCoins()) + "gp";
                        }
                        var0.getDialogueManager().showNpcTwoLineDialogue("Quite badly damaged, but easy to repair. Would you", "like me to repair it for " + var6_23 + "?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes, please.", "No, thank you.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var1_7 = ItemCombinationHandler.forBrokenToolItemId(var0.temporaryActionValue);
                                if (var1_7 == null) break block818;
                                var7_31 = "axe";
                                if (var1_7.getSkillId() == 14) {
                                    var7_31 = "pickaxe";
                                }
                                var6_24 = "free";
                                if (var1_7.getRepairCostCoins() != 0) {
                                    var6_24 = String.valueOf(var1_7.getRepairCostCoins()) + "gp";
                                }
                                var8_39 = new Npc(var0.O);
                                if (ItemCombinationHandler.repairBrokenGatheringTool(var0, var0.temporaryActionValue)) {
                                    var0.getPacketSender().sendGameMessage(String.valueOf(var8_39.getDefinition().getName()) + " fixes your " + var7_31 + " for " + var6_24 + ".");
                                }
                                var0.temporaryActionValue = -1;
                                var0.O = -1;
                                var0.getDialogueManager().finishDialogue();
                                break block818;
                            }
                            case 2: {
                                var0.temporaryActionValue = -1;
                                var0.O = -1;
                                var0.getDialogueManager().finishDialogue();
                            }
                        }
                    }
                }
                break;
            }
            case 10089: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var1_8 = var0.pendingDialogueItem;
                        if (var1_8 == null) break block0;
                        var0.getDialogueManager().setDialogueNpcId(var0.O);
                        var0.getDialogueManager().showNpcOneLineDialogue("That'll cost you " + GameUtil.formatNumber((long)BarrowsRepairHandler.calculateRepairCost(var1_8)) + " gold coins to fix, are you sure?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes I'm sure!", "On second thoughts, no thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes I'm sure!", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("On second thoughts, no thanks.", 591);
                                var0.O = -1;
                                var0.pendingDialogueItem = null;
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var1_9 = var0.pendingDialogueItem;
                        if (var1_9 == null) break block0;
                        var0.getDialogueManager().setDialogueNpcId(var0.O);
                        if (BarrowsRepairHandler.repairItem(var0, var1_9)) {
                            var0.getDialogueManager().showNpcOneLineDialogue("There you go, happy doing business with you!", 591);
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("You do not have enough gold coins!", 614);
                        }
                        var0.O = -1;
                        var0.pendingDialogueItem = null;
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 10002: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showTwoOptions("Burthorpe", "Nowhere");
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                GameplayHelper.castSelectedItemTeleport(var0, TeleportManager.BURTHORPE_TELEPORT_POSITION);
                            }
                        }
                    }
                }
                break;
            }
            case 10003: {
                block844 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showFiveOptions("Edgeville", "Karamja", "Draynor Village", "Al Kharid", "Nowhere");
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                GameplayHelper.castSelectedItemTeleport(var0, TeleportManager.EDGEVILLE_TELEPORT_POSITION);
                                break block844;
                            }
                            case 2: {
                                GameplayHelper.castSelectedItemTeleport(var0, TeleportManager.KARAMJA_TELEPORT_POSITION);
                                break block844;
                            }
                            case 3: {
                                GameplayHelper.castSelectedItemTeleport(var0, TeleportManager.DRAYNOR_VILLAGE_TELEPORT_POSITION);
                                break block844;
                            }
                            case 4: {
                                GameplayHelper.castSelectedItemTeleport(var0, TeleportManager.AL_KHARID_TELEPORT_POSITION);
                            }
                        }
                    }
                }
                break;
            }
            case 10004: {
                block854 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showThreeOptions("Duel Arena", "Castle Wars", "Nowhere");
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                GameplayHelper.castSelectedItemTeleport(var0, TeleportManager.DUEL_ARENA_TELEPORT_POSITION);
                                break block854;
                            }
                            case 2: {
                                GameplayHelper.castSelectedItemTeleport(var0, TeleportManager.CASTLE_WARS_TELEPORT_POSITION);
                            }
                        }
                    }
                }
                break;
            }
            case 170: 
            case 1800: 
            case 3809: 
            case 3810: 
            case 3811: 
            case 3812: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                if (var0.getQuestState(46) != 1) {
                                    var1_10 = QuestDefinition.forId(46);
                                    var6_25 = var1_10.getName();
                                    var0.getPacketSender().sendGameMessage("You need to complete " + var6_25 + " to use the glider.");
                                    var0.getDialogueManager().finishDialogue();
                                    return true;
                                }
                                var1_1 = GameUtil.getRegionId(var0.getPosition().getX(), var0.getPosition().getY());
                                if (var1_1 == 13109) {
                                    return false;
                                }
                                var0.getDialogueManager().showNpcOneLineDialogue("Would you like to fly somewhere on the glider?", 591);
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Sure.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getPacketSender().showInterface(802);
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                    }
                }
                break;
            }
            case 510: {
                block870 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.getQuestState(85) != 1) {
                            var1_11 = QuestDefinition.forId(85);
                            var6_26 = var1_11.getName();
                            var0.getDialogueManager().showOneLineStatement("You need to complete " + var6_26 + " to do this.");
                            var0.getDialogueManager().finishDialogue();
                            return true;
                        }
                        var7_32 = var0.getInventoryManager().getItemAmount(995);
                        var9_42 = var7_32 * 0.05;
                        var1_1 = (int)var9_42;
                        if (var1_1 < 10) {
                            var1_1 = 10;
                        }
                        if (var1_1 > 200) {
                            var1_1 = 200;
                        }
                        var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to travel to Shilo village?", "It will only cost you " + var1_1 + "gp.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var7_33 = var0.getInventoryManager().getItemAmount(995);
                                var9_43 = var7_33 * 0.05;
                                var1_1 = (int)var9_43;
                                if (var1_1 < 10) {
                                    var1_1 = 10;
                                }
                                if (var1_1 > 200) {
                                    var1_1 = 200;
                                }
                                if (var0.getInventoryManager().removeItem(new ItemStack(995, var1_1))) {
                                    TravelManager.handleHajedyCartRoute(var0, HajedyCartRoute.BRIMHAVEN_TO_SHILO);
                                    var0.getDialogueManager().markDialogueInactive();
                                    break block870;
                                }
                                var0.getDialogueManager().showNpcOneLineDialogue("It looks like you don't have enough money!", 614);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 511: {
                block878 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var7_34 = var0.getInventoryManager().getItemAmount(995);
                        var9_44 = var7_34 * 0.05;
                        var1_1 = (int)var9_44;
                        if (var1_1 < 10) {
                            var1_1 = 10;
                        }
                        if (var1_1 > 200) {
                            var1_1 = 200;
                        }
                        var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to travel to Brimhaven?", "It will cost " + var1_1 + "gp.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var7_35 = var0.getInventoryManager().getItemAmount(995);
                                var9_45 = var7_35 * 0.05;
                                var1_1 = (int)var9_45;
                                if (var1_1 < 10) {
                                    var1_1 = 10;
                                }
                                if (var1_1 > 200) {
                                    var1_1 = 200;
                                }
                                if (var0.getInventoryManager().removeItem(new ItemStack(995, var1_1))) {
                                    TravelManager.handleHajedyCartRoute(var0, HajedyCartRoute.SHILO_TO_BRIMHAVEN);
                                    var0.getDialogueManager().markDialogueInactive();
                                    break block878;
                                }
                                var0.getDialogueManager().showNpcOneLineDialogue("It looks like you don't have enough money!", 614);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 0: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hello, what are you doing here?", 591);
                        return true;
                    }
                    case 2: {
                        if (ServerSettings.membershipRequirementMode == 4 && !var0.hasMemberFlag()) {
                            var0.getDialogueManager().showFourOptions("I'm looking for whoever is in charge of this place.", "I have come to kill everyone in this castle!", "I don't know. I'm lost. Where am I?", "I would like to buy membership.");
                        } else {
                            var0.getDialogueManager().showThreeOptions("I'm looking for whoever is in charge of this place.", "I have come to kill everyone in this castle!", "I don't know. I'm lost. Where am I?");
                        }
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcTwoLineDialogue("The person in charge here is Duke Horacio.", "You can usually find him upstairs in his castle.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I have come to kill everyone in this castle!", 614);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showNpcOneLineDialogue("You are at the Lumbridge Castle.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 4: {
                                if (ServerSettings.membershipDaysPerPurchase <= 0) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("That will cost you " + GameUtil.formatNumber((long)ServerSettings.membershipRequirementValue) + " coins.", 591);
                                } else {
                                    var0.getDialogueManager().showNpcTwoLineDialogue("That will cost you " + GameUtil.formatNumber((long)ServerSettings.membershipRequirementValue) + " coins", "for " + ServerSettings.membershipDaysPerPurchase + " days of membership.", 591);
                                }
                                var0.getDialogueManager().setNextDialogueStep(10);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var1_12 = World.getNpcs()[var0.getInteractionTargetIndex()];
                        var1_12.getUpdateState().setForcedTextAndMarkUpdated("Help! Help!");
                        var1_12.getTargetMovement().moveAwayFromOverlap();
                        break;
                    }
                    case 10: {
                        var0.getDialogueManager().showTwoOptions("Here you go.", "Maybe next time.");
                        return true;
                    }
                    case 11: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getInventoryManager().removeItem(new ItemStack(995, ServerSettings.membershipRequirementValue))) {
                                    if (ServerSettings.membershipDaysPerPurchase <= 0) {
                                        var0.setMemberFlag(true);
                                        var0.getPacketSender().sendGameMessage("You are now a member!");
                                        var0.getDialogueManager().showNpcOneLineDialogue("You have now been granted members access!", 588);
                                    } else {
                                        if (var0.isMember()) {
                                            var0.getPacketSender().sendGameMessage("Your membership was extended by " + ServerSettings.membershipDaysPerPurchase + " days!");
                                            var0.getDialogueManager().showNpcOneLineDialogue("Your membership has been extended by " + ServerSettings.membershipDaysPerPurchase + " days!", 588);
                                        } else {
                                            var0.getPacketSender().sendGameMessage("You are now a member!");
                                            var0.getDialogueManager().showNpcTwoLineDialogue("You have now been granted members access", "for " + ServerSettings.membershipDaysPerPurchase + " days!", 588);
                                        }
                                        var6_27 = System.currentTimeMillis();
                                        if (var0.isMember()) {
                                            var6_27 = var0.membershipExpiresMillis;
                                        }
                                        var0.membershipExpiresMillis = GameplayHelper.addDaysToTimestamp(var6_27, ServerSettings.membershipDaysPerPurchase);
                                    }
                                    var0.getDialogueManager().finishDialogue();
                                    return true;
                                }
                                var0.getDialogueManager().showNpcOneLineDialogue("It looks like you don't have enough money!", 614);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 10005: {
                block903 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showThreeOptions("Climb up the ladder.", "Climb down the ladder.", "Never mind");
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2544, 3741, 0));
                                break block903;
                            }
                            case 2: {
                                AttackStyleDefinition.startDelayedObjectMove(var0, new Position(1798, 4407, 3));
                            }
                        }
                    }
                }
                break;
            }
            case 2437: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.getQuestState(40) == 1) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Would you like to sail to Waterbirth Island?", 591);
                        } else {
                            var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to sail to Waterbirth Island?", "It will only cost you 1000 coins.", 591);
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("YES", "NO");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                TravelManager.handleShipRoute(var0, ShipRoute.RELLEKKA_TO_WATERBIRTH);
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("So do you have the 1000 coins for my service, and are", "you ready to leave now?", 591);
                        var0.getDialogueManager().setNextDialogueStep(3);
                    }
                }
                break;
            }
            case 2436: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Would you like to sail back to Rellekka?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("YES", "NO");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                TravelManager.handleShipRoute(var0, ShipRoute.WATERBIRTH_TO_RELLEKKA);
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                    }
                }
                break;
            }
            case 802: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello brother, would you like me to bless all", "of your unblessed symbols?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (!var0.getInventoryManager().containsItem(1716)) {
                                    var0.getDialogueManager().showNpcTwoLineDialogue("It look's like you dont have any symbols", "in your inventory. Come back when you do.", 591);
                                } else {
                                    var8_40 = var0.getInventoryManager().getContainer().getItems();
                                    var7_36 = var8_40.length;
                                    var6_28 = 0;
                                    while (var6_28 < var7_36) {
                                        var1_13 = var8_40[var6_28];
                                        if (var1_13 != null && var1_13.getId() == 1716) {
                                            var0.getInventoryManager().replaceItem(new ItemStack(1716), new ItemStack(1718));
                                        }
                                        ++var6_28;
                                    }
                                    var0.getDialogueManager().showNpcOneLineDialogue("There you go, your welcome.", 591);
                                }
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 10006: {
                block936 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showThreeOptions("Climb up the ladder.", "Climb down the ladder.", "Never mind");
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                AttackStyleDefinition.climbOneFloorAtCurrentTile(var0, "up");
                                break block936;
                            }
                            case 2: {
                                AttackStyleDefinition.climbOneFloorAtCurrentTile(var0, "down");
                            }
                        }
                    }
                }
                break;
            }
            case 10007: {
                block944 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showThreeOptions("Go up the stairs.", "Go down the stairs.", "Never mind.");
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                AttackStyleDefinition.climbOffsetLadder(var0, "up");
                                break block944;
                            }
                            case 2: {
                                AttackStyleDefinition.climbOffsetLadder(var0, "down");
                            }
                        }
                    }
                }
                break;
            }
            case 222: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Greetings traveller.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Can you heal me? I'm injured.", "Nevermind.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getSkillManager().getCurrentLevels()[3] >= var0.getSkillManager().getBaseLevel(3)) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("You already have full hp.", 591);
                                    var0.getDialogueManager().finishDialogue();
                                    return true;
                                }
                                if (var0.getInteractionTarget() == null || var0.getInteractionTarget().isDead()) {
                                    break block0;
                                }
                                var0.getInteractionTarget().getUpdateState().setAnimation(717);
                                var0.getDialogueManager().showNpcOneLineDialogue("Sure, here you go.", 591);
                                var0.heal((int)((double)var0.getSkillManager().getBaseLevel(3) * 0.3));
                                var0.getUpdateState().setGraphic(84);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 2257: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else if (var0.enterTheAbyssMiniquestState == 1) {
                                var0.getDialogueManager().showNpcOneLineDialogue("How can I help a fellow Zamorak follower?", 591);
                            } else if (var0.getQuestState(14) == 1 && (var0.enterTheAbyssMiniquestState == 0 || var0.enterTheAbyssMiniquestState == 2)) {
                                var0.getDialogueManager().showNpcTwoLineDialogue("Meet me in Varrock's Chaos Temple.", "Here is not the place to talk.", 591);
                                var0.enterTheAbyssMiniquestState = 2;
                                var0.bp();
                                var0.getDialogueManager().finishDialogue();
                            } else {
                                var0.getDialogueManager().showNpcOneLineDialogue("I'm busy right now.", 591);
                                var0.getDialogueManager().finishDialogue();
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptions("Can you teleport me to the abyss?", "Can I see your shop?", "I'm not a Zamorak follower!");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you teleport me to the abyss?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can I see your shop?", 591);
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'm not a Zamorak follower!", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        if (var0.getQuestState(14) != 1) {
                            var1_14 = QuestDefinition.forId(14);
                            var6_29 = var1_14.getName();
                            var0.getDialogueManager().showOneLineStatement("You need to complete " + var6_29 + " to do this.");
                            var0.getDialogueManager().finishDialogue();
                            return true;
                        }
                        var1_15 = World.getNpcs()[var0.getInteractionTargetIndex()];
                        if (var0.enterTheAbyssMiniquestState == 1) {
                            AbyssManager.startAbyssMageTeleport(var0, var1_15);
                            break block0;
                        }
                        var0.getDialogueManager().showOneLineStatement("You need to complete Enter the Abyss miniquest to do this.");
                        var0.getDialogueManager().finishDialogue();
                        break block0;
                    }
                    case 5: {
                        ShopManager.openShop(var0, GameplayHelper.getNpcShopId(var1_1));
                        var0.getDialogueManager().markDialogueInactive();
                        break block0;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Then get out of my sight!", 614);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 376: 
            case 377: 
            case 378: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (!var0.botEnabled) {
                            var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to sail to Karamja?", "It will only cost you 30gp.", 591);
                            return true;
                        }
                        TravelManager.handleShipRoute(var0, ShipRoute.PORT_SARIM_TO_KARAMJA);
                        var0.getDialogueManager().markDialogueInactive();
                        break;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                TravelManager.handleShipRoute(var0, ShipRoute.PORT_SARIM_TO_KARAMJA);
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                    }
                }
                break;
            }
            case 3852: {
                block981 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showTwoOptions("Access shop", var0.ee != 1 ? "Unlock skeleton skin for 700 donator points." : "Switch to skeleton skin.");
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                ShopManager.openShop(var0, GameplayHelper.getNpcShopId(var1_1));
                                var0.getDialogueManager().markDialogueInactive();
                                break block981;
                            }
                            case 2: {
                                if (var0.ee != 1) {
                                    var0.getDialogueManager().showTwoOptionsWithTitle("Are you sure you want to spend 700 donator poins?", "Yes", "No");
                                    return true;
                                }
                                var0.applyDefaultMaleAppearance();
                                var0.getDialogueManager().showNpcOneLineDialogue("Come back anytime.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getDonatorPoints() >= 700) {
                                    var0.applyDefaultMaleAppearance();
                                    var0.subtractDonatorPoints(700);
                                    var0.ee = 1;
                                }
                                var0.getDialogueManager().showNpcOneLineDialogue("Come back anytime.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("Come back anytime.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 380: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.getPosition().getX() <= 2815) {
                            var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to sail back to Ardougne?", "It will cost 30gp.", 591);
                            return true;
                        }
                        if (!var0.botEnabled) {
                            var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to sail back to Port Sarim?", "It will cost 30gp.", 591);
                            return true;
                        }
                        TravelManager.handleShipRoute(var0, ShipRoute.KARAMJA_TO_PORT_SARIM);
                        var0.getDialogueManager().markDialogueInactive();
                        break block0;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getPosition().getX() > 2815) {
                                    TravelManager.handleShipRoute(var0, ShipRoute.KARAMJA_TO_PORT_SARIM);
                                } else {
                                    TravelManager.handleShipRoute(var0, ShipRoute.BRIMHAVEN_TO_ARDOUGNE);
                                }
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                    }
                }
                break;
            }
            case 381: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Would you like to sail to Brimhaven?", "It will cost 30gp.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                TravelManager.handleShipRoute(var0, ShipRoute.ARDOUGNE_TO_BRIMHAVEN);
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                    }
                }
                break;
            }
            case 3117: {
                var1_16 = new SandwichLadyFoodOffer(true);
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("You look hungry to me. I tell you what - ", "have a " + var1_16.getOfferedFoodNames()[0] + " on me.", 588);
                        var0.getSandwichLadyManager().selectedOfferIndex = var1_16.getOfferIndex();
                        return true;
                    }
                    case 2: {
                        var0.getSandwichLadyManager().openSelectionInterface(3117);
                        var0.getDialogueManager().markDialogueInactive();
                    }
                }
                break;
            }
            case 1595: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.botEnabled) {
                            if (var0.getInventoryManager().removeItem(new ItemStack(995, 875))) {
                                var0.scheduleDelayedMove(new Position(2713, 9564));
                                var0.setBrimhavenOpen(false);
                            } else {
                                var0.currentBotTask.startWalkToBank(var0);
                            }
                            return true;
                        }
                        if (var0.isBrimhavenOpen()) {
                            var0.getDialogueManager().showNpcTwoLineDialogue("You have already paid the entrance fee.", "You may enter the dungeon whenever you wish.", 591);
                            var0.getDialogueManager().finishDialogue();
                            return true;
                        }
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hey, there is a 875 coin fee if you wish", "to enter this dungeon.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Ok, here's 875 coins.", "Nevermind.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.isBrimhavenOpen()) {
                                    var0.getDialogueManager().showNpcTwoLineDialogue("You have already paid the entrance fee.", "You may enter the dungeon whenever you wish.", 591);
                                    var0.getDialogueManager().finishDialogue();
                                    return true;
                                }
                                var0.getDialogueManager().showPlayerOneLineDialogue("Ok, here's 875 coins", 591);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        if (var0.getInventoryManager().removeItem(new ItemStack(995, 875))) {
                            var0.getDialogueManager().showOneLineStatement("You give Saniboch 875 coins");
                            var0.setBrimhavenOpen(true);
                        } else {
                            var0.getDialogueManager().showPlayerOneLineDialogue("Looks like I don't have enough coins.", 599);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Many thanks. You may now pass the door.", "May your death be a glorious one!", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 10008: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello " + var0.getUsername() + ",", "would you like to see my shop?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getInteractionTarget() != null && var0.getInteractionTarget().isNpc()) {
                                    GameplayHelper.openNpcShop(var0, ((Npc)var0.getInteractionTarget()).getNpcId());
                                }
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                    }
                }
                break;
            }
            case 513: {
                block1032 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Can I come through this door?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sure, you can enter for a small fee of 20 coins.", 591);
                        return true;
                    }
                    case 3: {
                        var0.getDialogueManager().showTwoOptions("Ok, I'll pay.", "No thanks.");
                        return true;
                    }
                    case 4: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getInventoryManager().containsItemAmount(995, 20)) {
                                    var0.getInventoryManager().removeItem(new ItemStack(995, 20));
                                    var0.getPacketSender().queueRelativeMovementStep(0, var0.getPosition().getY() == 2963 ? 1 : 2, true);
                                    var0.getPacketSender().openSingleDoor(2266, 2856, 2963, 0);
                                    break block1032;
                                }
                                var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, I don't have that many coins.", 599);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No thanks.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 798: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Thank you for rescuing me! It isn't very comfy", "in this cell!", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("So... do you know anywhere good to explore?", "Do I get a reward?");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("So... do you know anywhere good to explore?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Do I get a reward?", 591);
                                var0.getDialogueManager().setNextDialogueStep(10);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Well, this dungeon was quite good to explore ...until I", "got captured, anyway. I was given a key to an inner", "part of this dungeon by a mysterious cloaked", "stranger!", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("It's rather tough for me to get that far into the", "dungeon however... I just keep getting", "captured! Would you like to give it a go?", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showTwoOptions("Yes please!", "No, it's too dangerous for me too.");
                        return true;
                    }
                    case 7: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes please!", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("No, it's too dangerous for me too.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 8: {
                        var0.getDialogueManager().showOneLineStatement("Velrak reaches somewhere mysterious and passes you a key.");
                        return true;
                    }
                    case 9: {
                        if (var0.getInventoryManager().getContainer().getFreeSlots() <= 0) {
                            var0.getDialogueManager().showNpcTwoLineDialogue("Looks like you don't have enough room", "in your inventory.", 591);
                        } else if (var0.ownsItem(1590)) {
                            var0.getDialogueManager().showNpcOneLineDialogue("I already gave you the key!", 591);
                        } else {
                            var0.getInventoryManager().addItem(new ItemStack(1590));
                            break block0;
                        }
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showNpcOneLineDialogue("I don't have anything expensive to give, sorry.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 10009: {
                var6_30 = true;
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.clueRequiredItems != null) {
                            if (!var0.getInventoryManager().containsItemStack(var0.clueRequiredItems[0])) {
                                var6_30 = false;
                            }
                            if (var6_30) {
                                var7_37 = 0;
                                while (var7_37 < var0.clueRequiredItems.length) {
                                    var0.getInventoryManager().removeItem(var0.clueRequiredItems[var7_37]);
                                    ++var7_37;
                                }
                                var0.clueRequiredItems = null;
                            } else {
                                return false;
                            }
                        }
                        var0.en = false;
                        TreasureTrailManager.completeTreasureTrail(var0, var0.activeClueLevel);
                        var0.getDialogueManager().markDialogueInactive();
                        break;
                    }
                    case 2: {
                        if (var0.clueRequiredItems != null) {
                            if (!var0.getInventoryManager().containsItemStack(var0.clueRequiredItems[0])) {
                                var6_30 = false;
                            }
                            if (!var6_30) {
                                return false;
                            }
                        }
                        var0.en = false;
                        TreasureTrailManager.advanceOrCompleteTrail(var0, var0.activeClueLevel, "You recieve another clue!", true, "Here is your reward");
                        return true;
                    }
                    case 3: {
                        if (var0.clueRequiredItems != null) {
                            if (!var0.getInventoryManager().containsItemStack(var0.clueRequiredItems[0])) {
                                var6_30 = false;
                            }
                            if (!var6_30) {
                                return false;
                            }
                        }
                        var0.getPacketSender().closeInterfaces();
                        var0.getDialogueManager().resetDialogueState();
                        var0.getPacketSender().sendEnterInputPrompt(207);
                        var0.getDialogueManager().markDialogueInactive();
                    }
                }
                break;
            }
            case 804: 
            case 1041: 
            case 2824: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Greetings friend. I am a manufacturer of leather.", 591);
                        return true;
                    }
                    case 2: {
                        if (ServerSettings.cacheVersion < 270) {
                            var0.getDialogueManager().showThreeOptions("Can I buy some leather then?", "Leather is rather weak stuff.", "Tan my hides.");
                        } else {
                            var0.getDialogueManager().showTwoOptions("Can I buy some leather then?", "Leather is rather weak stuff.");
                        }
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can I buy some leather then?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Leather is rather weak stuff.", 591);
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                            case 3: {
                    GameplayHelper.openTanningInterface(var0);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("I make leather from animal hides. Bring me some", "cowhides and one gold per hide, and I'll tan them", "into soft leather for you.", 588);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Normal leather may be quite weak, but it's", "very cheap - I make it from cowhides for only 1 gp", "per hide - and it's so easy to craft that anyone", "can work with it.", 588);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Alternatively you could try hard leather. It's", "not so easy to craft, but I only charge 3 gp", "per cowhide to prepare it, and it makes much", "sturdier armour.", 588);
                        return true;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("I can also tan snake hides and dragonhides,", "suitable for crafting into the highest quality", "armour for rangers.", 588);
                        return true;
                    }
                    case 8: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Thanks, I'll bear it in mind.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 12345: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showTwoOptionsWithTitle("Drop your " + var0.pendingDialogueItem.getDefinition().getName() + " for " + var0.pendingItemDropTarget.getUsername() + " to take?", "Yes", "No");
                        return true;
                    }
                    case 2: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.pendingItemDropTarget != null && !var0.pendingItemDropTarget.isDead()) {
                                    GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(var0.pendingDialogueItem.getId(), 1), var0.pendingItemDropTarget));
                                    var0.getInventoryManager().removeItem(new ItemStack(var0.pendingDialogueItem.getId(), 1));
                                    var0.getPacketSender().sendGameMessage(String.valueOf(var0.pendingItemDropTarget.getUsername()) + " can now take the item from the ground.");
                                    var0.pendingItemDropTarget.getPacketSender().sendGameMessage(String.valueOf(var0.getUsername()) + " has dropped something for you to take.");
                                    var0.pendingDialogueItem = null;
                                    var0.pendingItemDropTarget = null;
                                }
                            }
                            case 2: {
                                var0.pendingDialogueItem = null;
                                var0.pendingItemDropTarget = null;
                            }
                        }
                    }
                }
                break;
            }
            case 10011: {
                var0.getDialogueManager().setDialogueNpcId(3637);
                if (var0.getDialogueManager().getDialogueStep() == 1 && !NpcDefinition.isDefined(3637)) {
                    var0.getDialogueManager().setDialogueStep(2);
                }
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello " + var0.getUsername(), "Would you like a free trip somewhere?", -1);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showFiveOptions("The Tree Gnome Village", "The Gnome Stronghold", "Battlefield", "Varrock", "Nowhere");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2542, 3169, 0));
                        break;
                    }
                    case 5: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2462, 3444, 0));
                        break;
                    }
                    case 6: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2556, 3259, 0));
                        break;
                    }
                    case 7: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(3179, 3506, 0));
                    }
                }
                break;
            }
            case 3636: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Hello " + var0.getUsername() + ", First of all -", "Thank you for giving me life !", "Let me thank you by offering you a trip", -1);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showFiveOptions("The Tree Gnome Village", "The Gnome Stronghold", "Battlefield", "Varrock", "Nowhere");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                var0.getDialogueManager().setNextDialogueStep(5);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showTwoLineStatement("You place your hands on the dry tough bark of the spirit tree, and", "feel a surge of energy run through your veins.");
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2542, 3169, 0));
                        break;
                    }
                    case 5: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2462, 3444, 0));
                        break;
                    }
                    case 6: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(2556, 3259, 0));
                        break;
                    }
                    case 7: {
                        AttackStyleDefinition.startDelayedObjectMove(var0, new Position(3179, 3506, 0));
                    }
                }
                break;
            }
            case 2323: 
            case 2324: 
            case 2325: 
            case 2326: 
            case 2327: 
            case 2330: 
            case 2331: 
            case 2332: 
            case 2333: 
            case 2334: 
            case 2335: 
            case 2336: 
            case 2337: 
            case 2338: 
            case 2339: 
            case 2340: 
            case 2341: 
            case 2342: 
            case 2343: 
            case 2344: {
                if (!ServerSettings.farmingEnabled) {
                    var0.getPacketSender().sendGameMessage("This skill is currently disabled.");
                    break;
                }
                var7_38 = FarmingFarmerDefinition.forNpcId(var0.getInteractionTargetId());
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showNpcThreeLineDialogue("Hey, I am one of the master farmers of this world", "but you can call me " + NpcDefinition.forId(var0.getInteractionTargetId()).getName(), "So, what do you need from me?", 591);
                                if (var7_38.getPatchType() != "tree") {
                                    var0.getDialogueManager().setNextDialogueStep(16);
                                }
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showFiveOptions("Would you chop my tree down for me?", "Could you take care of my crops for me?", "Can you give me any farming advice?", "Can you sell me something?", "That's all, thanks");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Would you chop my tree down for me?", 591);
                                var0.getDialogueManager().setNextDialogueStep(11);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Could you take care of my crops for me?", 591);
                                if (var7_38.getPatchType() != "allotment") {
                                    var0.getDialogueManager().setNextDialogueStep(7);
                                }
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you give me any farming advice?", 591);
                                var0.getDialogueManager().setNextDialogueStep(8);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you sell me something?", 591);
                                var0.getDialogueManager().setNextDialogueStep(9);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("I Might, Which one were you thinking of?", 588);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showOptions(var7_38.getPatchLabels());
                        return true;
                    }
                    case 6: {
                        switch (var3_18) {
                            case 1: {
                                FarmingFarmerHandler.handlePatchProtectionDialogue(var0, 0, "allotment", var0.getInteractionTargetId(), 1);
                                return true;
                            }
                            case 2: {
                                FarmingFarmerHandler.handlePatchProtectionDialogue(var0, 1, "allotment", var0.getInteractionTargetId(), 1);
                                return true;
                            }
                        }
                        break;
                    }
                    case 7: {
                        FarmingFarmerHandler.handlePatchProtectionDialogue(var0, -1, var7_38.getPatchType(), var0.getInteractionTargetId(), 1);
                        return true;
                    }
                    case 8: {
                        FarmingFarmerHandler.showRandomFarmingAdvice(var0);
                        var0.getDialogueManager().markDialogueInactive();
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sure, I have a bunch of tools for you to use.", 588);
                        return true;
                    }
                    case 10: {
                        ShopManager.openShop(var0, GameplayHelper.getNpcShopId(var1_1));
                        var0.getDialogueManager().markDialogueInactive();
                        break;
                    }
                    case 11: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sure, for only 200gp, I will chop it down for you", 591);
                        return true;
                    }
                    case 12: {
                        var0.getDialogueManager().showTwoOptions("Sure, here you go", "Sorry, I am a little broke.");
                        return true;
                    }
                    case 13: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Sure, Here you go", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, I am a litle broke", 595);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 14: {
                        FarmingFarmerHandler.chopTreeForFee(var0, var0.getInteractionTargetId());
                        var0.getDialogueManager().markDialogueInactive();
                        return true;
                    }
                    case 15: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sorry, but you have no tree growing in this patch.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 16: {
                        var0.getDialogueManager().showFourOptions("Could you take care of my crops for me?", "Can you give me any farming advice?", "Can you sell me something?", "That's all, thanks");
                        return true;
                    }
                    case 17: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Could you take care of my crops for me?", 591);
                                if (var7_38.getPatchType() != "allotment") {
                                    var0.getDialogueManager().setNextDialogueStep(7);
                                } else {
                                    var0.getDialogueManager().setNextDialogueStep(4);
                                }
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you give me any farming advice?", 591);
                                var0.getDialogueManager().setNextDialogueStep(8);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you sell me something?", 591);
                                var0.getDialogueManager().setNextDialogueStep(9);
                                return true;
                            }
                        }
                        break;
                    }
                    case 18: {
                        var0.getDialogueManager().showTwoOptions("Sure, here you go", "Sorry, I don't have those at the moment.");
                        return true;
                    }
                    case 19: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Sure, here you go", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, I don't have those at the moment.", 595);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 20: {
                        FarmingFarmerHandler.handlePatchProtectionDialogue(var0, var0.getSelectedSkillItemId(), var7_38.getPatchType(), var0.getInteractionTargetId(), 2);
                        var0.getDialogueManager().markDialogueInactive();
                    }
                }
                break;
            }
            case 953: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Good day, would you like to access your bank account?", 588);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                BankManager.openBank(var0);
                                var0.getDialogueManager().markDialogueInactive();
                            }
                        }
                    }
                }
                break;
            }
            case 3021: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showNpcThreeLineDialogue("Ah, 'tis a foine day to be sure! Were yez wantin' me to", "store yer tools, or maybe ye might be wantin' yer stuff", "back from me?", 591);
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptions("What tools can you store?", "Open your tool store, please.", "Actually, I'm fine.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What tools can you store?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Open your tool store, please.", 591);
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Actually, I'm fine.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("We'll hold onto yer rake, seed dibber, spade, secateurs,", "waterin' can and trowel - but mind it's not one of them", "fancy trowels only archaeologist use.", 588);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("We'll take a few buckets off yer hand if you want", "too, and even yer compost and supercompost. There's", "room in our shed for plenty of compost, so bring it on.", 588);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Also, if ye hands us yer Farming produce,", "we might be able to change it into banknotes.", 588);
                        var0.getDialogueManager().setNextDialogueStep(2);
                        return true;
                    }
                    case 7: {
                        var0.getFarmingToolStore().open();
                        var0.getDialogueManager().markDialogueInactive();
                    }
                }
                break;
            }
            case 2244: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Greetings adventurer. I am Phileas the Lumbridge", "Guide. I am here to give information and directions to", "new players. Do you require any help?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes please.", "No, I can find things myself thank you.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes please.", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("If you ever need help, you can talk to me again.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcFourLineDialogue("First I must warn you to take every precaution to", "keep your password and PIN secure. The", "most important thing to remember is to never give your", "password to, or share your account with, anyone.", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("I have much more information to impart; what would", "you like to know about?", 591);
                        return true;
                    }
                    case 6: {
                        var0.getDialogueManager().showFiveOptions("Where can I find a quest to go on?", "What monsters should I fight?", "Where can I make money?", "How can I heal myself?", "Where can I find a bank?");
                        return true;
                    }
                    case 7: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I find a quest to go on?", 591);
                                var0.getDialogueManager().setNextDialogueStep(16);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What monsters should I fight?", 591);
                                var0.getDialogueManager().setNextDialogueStep(34);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I make money?", 591);
                                var0.getDialogueManager().setNextDialogueStep(18);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("How can I heal myself?", 591);
                                var0.getDialogueManager().setNextDialogueStep(12);
                                return true;
                            }
                            case 5: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I find a bank?", 591);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 8: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("The nearest bank is in Draynor Village - go", "west from here.", 591);
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Is there anything else you need help with?", 591);
                        return true;
                    }
                    case 10: {
                        var0.getDialogueManager().showTwoOptions("No thank you.", "Yes please.");
                        return true;
                    }
                    case 11: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcOneLineDialogue("If you ever need help, you can talk to me again.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes please.", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 12: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("You will always heal slowly over time, but people", "normally choose to heal themselves faster by eating food.", 591);
                        return true;
                    }
                    case 13: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("There are many different foods in the game such as", "cabbage, fish, meat and many more. Which do you wish", "to hear about?", 591);
                        return true;
                    }
                    case 14: {
                        var0.getDialogueManager().showFourOptions("How do I get cabbages?", "How do I fish?", "Where can I find meat?", "Nevermind.");
                        return true;
                    }
                    case 15: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("How do I get cabbages?", 591);
                                var0.getDialogueManager().setNextDialogueStep(19);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("How do I fish?", 591);
                                var0.getDialogueManager().setNextDialogueStep(23);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I find meat?", 591);
                                var0.getDialogueManager().setNextDialogueStep(29);
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showNpcOneLineDialogue("If you ever need help, you can talk to me again.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 16: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Well, I heard my friend the cook was in need of a spot", "of help. He'll be in the kitchen of this here castle. Just", "talk to him and he'll set you off.", 591);
                        var0.getDialogueManager().setNextDialogueStep(9);
                        return true;
                    }
                    case 18: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("There are many ways to make money in the game. I", "would suggest either killing monsters or doing a trade", "skill such as Smithing or Fishing.", 591);
                        var0.getDialogueManager().setNextDialogueStep(31);
                        return true;
                    }
                    case 19: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("There is a field a little distance to the north of here", "packed full of cabbages which are there for the picking.", 591);
                        var0.getDialogueManager().setNextDialogueStep(20);
                        return true;
                    }
                    case 20: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Is there anything else you need help with?", 591);
                        return true;
                    }
                    case 21: {
                        var0.getDialogueManager().showThreeOptions("No thank you.", "I'd like to know about other food.", "Yes please.");
                        return true;
                    }
                    case 22: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcOneLineDialogue("If you ever need help, you can talk to me again.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showFourOptions("How do I get cabbages?", "How do I fish?", "Where can I find meat?", "Nevermind.");
                                var0.getDialogueManager().setNextDialogueStep(15);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Yes please.", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 23: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Fishing spots require different levels and equipment to", "use. To start Fishing, you'll want to talk to the Fishing", "tutor who can be found in the swamps south of here.", "He will also give you a small fishing net if you don't", 591);
                        return true;
                    }
                    case 24: {
                        var0.getDialogueManager().showNpcOneLineDialogue("own one already.", 591);
                        return true;
                    }
                    case 25: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("You will need some Fishing equipment. At the Fishing", "spots to the south you can only use a small fishing net.", 591);
                        return true;
                    }
                    case 26: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Where could I find one of those?", 591);
                        return true;
                    }
                    case 27: {
                        var0.getDialogueManager().showNpcFourLineDialogue("You can get them from a Fishing shop or our Fishing", "Tutor south of here in the swamp. There is a Fishing", "shop in Port Sarim; you can find it on the world map.", "Port Sarim is some way to the west of here, beyond", 591);
                        return true;
                    }
                    case 28: {
                        var0.getDialogueManager().showNpcOneLineDialogue("the village of Draynor.", 591);
                        var0.getDialogueManager().setNextDialogueStep(20);
                        return true;
                    }
                    case 29: {
                        var0.getDialogueManager().showNpcFourLineDialogue("I suggest you go and kill some chickens. The roads on", "either side if this river eventually go past a chicken", "farm. When you have killed some chickens, cook them.", "You cold either make a fire or use a range.", 591);
                        return true;
                    }
                    case 30: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("There is a range at the southern end in this town and", "a Cooking tutor in south Lumbridge near Bob's Brilliant", "Axes shop.", 591);
                        var0.getDialogueManager().setNextDialogueStep(20);
                        return true;
                    }
                    case 31: {
                        var0.getDialogueManager().showNpcFourLineDialogue("Please don't try to get money by begging of other", "players. It will make you unpopular. Nobody likes a", "beggar. It is very irritating to have other players asking", "for your hard-earned cash.", 591);
                        return true;
                    }
                    case 32: {
                        var0.getDialogueManager().showThreeOptions("Where can I smith?", "How do I fish?", "What monsters should I fight?");
                        return true;
                    }
                    case 33: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I smith?", 591);
                                var0.getDialogueManager().setNextDialogueStep(45);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("How do I fish?", 591);
                                var0.getDialogueManager().setNextDialogueStep(23);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("What monsters should I fight?", 591);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 34: {
                        var0.getDialogueManager().showNpcFourLineDialogue("There's lots of beasts to fight in the woods around here,", "especially to the west. There are certainly some goblins", "and spiders that are pests and could do with being", "cleared out. There's also a chicken farm or two up the", 591);
                        return true;
                    }
                    case 35: {
                        var0.getDialogueManager().showNpcFourLineDialogue("road for some fairly easy picking. Non-player", "characters usually appear as yellow dots on your mini-", "map, although there are some that you won't be able to", "fight, such as myself. A monster's combat level is shown", 591);
                        return true;
                    }
                    case 36: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("next to their 'Attack' option. If that level is coloured", "green it means the monster is weaker than you. If it is", "red, it means that the monster is tougher than you.", 591);
                        return true;
                    }
                    case 37: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Remember, you will do better if you have better", "armour and weapons and it's always worth carrying a", "bit of food to heal yourself.", 591);
                        return true;
                    }
                    case 38: {
                        var0.getDialogueManager().showFiveOptions("Where can I get food to heal myself?", "Where can I get better armour and weapons?", "Okay, thanks, I will go and kill things.", "Can I kill other players?", "I'd like to know about something else.");
                        return true;
                    }
                    case 39: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I get food to heal myself?", 591);
                                var0.getDialogueManager().setNextDialogueStep(13);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I get better armour and weapons?", 591);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Okay, thanks, I will go and kill things.", 591);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 4: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can I kill other players?", 591);
                                var0.getDialogueManager().setNextDialogueStep(49);
                                return true;
                            }
                            case 5: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'd like to know about something else.", 591);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 40: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Well, you can make them, you buy them or talk to the", "combat tutors just west of here.", 591);
                        return true;
                    }
                    case 41: {
                        var0.getDialogueManager().showThreeOptions("How do I make a weapon?", "Where can I buy a weapon?", "Could I get a staff like yours?");
                        return true;
                    }
                    case 42: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("How do I make a weapon?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Where can I buy a weapon?", 591);
                                var0.getDialogueManager().setNextDialogueStep(47);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Could I get a staff like yours?", 591);
                                var0.getDialogueManager().setNextDialogueStep(48);
                                return true;
                            }
                        }
                        return true;
                    }
                    case 43: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("The Smithing skill allows you to make armour and", "weapons. Talk to the boy who smelts metal in the", "furnace, I'm sure he can help", 591);
                        return true;
                    }
                    case 44: {
                        var0.getDialogueManager().showPlayerOneLineDialogue("Where can I smith?", 591);
                        return true;
                    }
                    case 45: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("You will find a helpful Smithing tutor in the west of", "Varrock - that's north of here. Follow the path across", "the river and head north", 591);
                        return true;
                    }
                    case 46: {
                        var0.getDialogueManager().showNpcFourLineDialogue("I suggest you go and mine some ore; find the Mining", "symbol - with the guide symbol near it - in the swamp", "south of here. The Mining guide there can teach you", "how to mine ore.", 591);
                        var0.getDialogueManager().setNextDialogueStep(9);
                        return true;
                    }
                    case 47: {
                        var0.getDialogueManager().showNpcFourLineDialogue("You can buy a sword from any sword shop, such as", "the one in Varrock - located north of here. Simply", "look for the sword icon on the mini-map, and you'll", "find the store.", 591);
                        var0.getDialogueManager().setNextDialogueStep(9);
                        return true;
                    }
                    case 48: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Sorry, my staff is not for sale. However, if your", "interested in buy a staff, visit Zeke's staff", "shop located in Varrock, abit north of here.", 591);
                        var0.getDialogueManager().setNextDialogueStep(9);
                        return true;
                    }
                    case 49: {
                        var0.getDialogueManager().showNpcFourLineDialogue("To fight other players, you need to visit the duel", "arena, where you can fight players for fun or", "for stakes. However, if you want a more dangerous", "challenge, you can visit the wilderness.", 591);
                        var0.getDialogueManager().setNextDialogueStep(9);
                        return true;
                    }
                }
                break;
            }
            case 599: {
                block1280 : switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("Greetings, " + GameUtil.formatDisplayName(var0.getUsername()) + ".", "How may I assist you?", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptions("Can you change my appearance?", "That's a nice necklace you have, can I buy one?", "I'm fine, thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Can you change my appearance?", 591);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("That's a nice necklace you have, can I buy one?", 591);
                                var0.getDialogueManager().setNextDialogueStep(8);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'm fine, thanks.", 591);
                                var0.getDialogueManager().setNextDialogueStep(7);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Sure. It will only cost you 1000 coins.", 591);
                        return true;
                    }
                    case 5: {
                        var0.getDialogueManager().showTwoOptions("Alright, here you go.", "Nevermind.");
                        return true;
                    }
                    case 6: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getInventoryManager().removeItem(new ItemStack(995, 1000))) {
                                    var0.getPacketSender().showInterface(3559);
                                    var0.getDialogueManager().markDialogueInactive();
                                    break block1280;
                                }
                                var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, looks like I don't have enough coins for that.", 599);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("I'm fine, thanks.", 591);
                                return true;
                            }
                        }
                        break;
                    }
                    case 7: {
                        var0.getDialogueManager().showNpcTwoLineDialogue("I'm a busy man.", "Come back when you need something.", 591);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                    case 8: {
                        if (ItemDefinition.isDefined(7803)) {
                            var0.getDialogueManager().showNpcOneLineDialogue("Sure, I can sell you a copy for 100 coins.", 591);
                        } else {
                            var0.getDialogueManager().showNpcOneLineDialogue("Sorry, I only have this one.", 591);
                            var0.getDialogueManager().finishDialogue();
                        }
                        return true;
                    }
                    case 9: {
                        var0.getDialogueManager().showTwoOptions("Alright, here you go.", "Nevermind.");
                        return true;
                    }
                    case 10: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getInventoryManager().removeItem(new ItemStack(995, 100))) {
                                    var0.getInventoryManager().addItem(new ItemStack(7803));
                                    var0.getDialogueManager().showNpcOneLineDialogue("Thanks, here's your amulet.", 591);
                                } else {
                                    var0.getDialogueManager().showPlayerOneLineDialogue("Sorry, looks like I don't have enough coins for that.", 599);
                                }
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 10010: {
                if (!var0.getDuelSession().isActiveDuelStarted()) break;
                if (DuelRule.NO_FORFEIT.isEnabledFor(var0)) {
                    var0.getPacketSender().sendGameMessage("Forfeiting is disabled in this match!");
                    break;
                }
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showOneLineStatement("Are you sure you want to forfeit?");
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes, I want to give up.", "No, I'll keep fighting!");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDuelSession().finishDuelLoss(true);
                                return true;
                            }
                        }
                    }
                }
                break;
            }
            case 10012: {
                if (var0.getSlayerManager().slayerMasterId <= 0) {
                    var0.getDialogueManager().showTwoLineStatement("You have currently no task assigned. Talk to any", "slayer master to recieve one.");
                    var0.getDialogueManager().finishDialogue();
                    return true;
                }
                var8_41 = SlayerMasterDefinition.forNpcId(var0.getSlayerManager().slayerMasterId);
                var0.getDialogueManager().setDialogueNpcId(var0.getSlayerManager().slayerMasterId);
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Hello there, " + var0.getUsername() + ", what can I help you with?", 588);
                        return true;
                    }
                    case 2: {
                        if (var0.getSlayerManager().slayerMasterId == 3887 && !var0.skulled) {
                            var0.getDialogueManager().showOptions(new String[]{"How am I doing so far?", "Who are you?", "Where are you?", "Got any tips for me?", "Activate skull"});
                        } else {
                            var0.getDialogueManager().showOptions(new String[]{"How am I doing so far?", "Who are you?", "Where are you?", "Got any tips for me?", "Nothing really."});
                        }
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.getDialogueManager().showNpcTwoLineDialogue("You're currently assigned to kill " + var0.getSlayerManager().slayerTaskName + "s;", "only " + var0.getSlayerManager().taskAmount + " more to go.", 588);
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("My name's " + new Npc(var0.getSlayerManager().slayerMasterId).getDefinition().getName() + "; I'm a Slayer Master.", 588);
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                            case 3: {
                                var0.getDialogueManager().showNpcOneLineDialogue("I'm in " + var8_41.getLocationName() + ". Only a fool would forget that.", 588);
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                            case 4: {
                                var9_46 = SlayerMonsterGuide.forMonsterName(var0.getSlayerManager().slayerTaskName);
                                if (var9_46 == null) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("There is no tips about this npc yet.", 588);
                                } else {
                                    var0.getDialogueManager().showNpcDialogue(var9_46.getGuideTextLines(), 588);
                                }
                                var0.getDialogueManager().setNextDialogueStep(2);
                                return true;
                            }
                            case 5: {
                                if (var0.getSlayerManager().slayerMasterId == 3887 && !var0.skulled) {
                                    var0.addPvpCombatReference(var0, 2000);
                                    var0.getPacketSender().sendGameMessage("You are now skulled!");
                                }
                                break block0;
                            }
                        }
                    }
                }
                break;
            }
            case 70: 
            case 1596: 
            case 1597: 
            case 1598: 
            case 1599: 
            case 3887: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.isMember()) {
                            if (ServerSettings.freeToPlayWorld) {
                                var0.packetSender.sendGameMessage("You need to be in members world to access members content.");
                            } else {
                                var0.getDialogueManager().showNpcOneLineDialogue("'Ello, and what are you after then?", 588);
                            }
                        } else {
                            var0.packetSender.sendGameMessage("You need a members account to access members content.");
                        }
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showThreeOptions("I need another assignment", "Do you have anything for trade?", "Er...nothing");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var1_1 != 70) {
                                    if (var0.getSlayerManager().slayerMasterId != 0 && !var0.getSlayerManager().slayerTaskName.equals("")) {
                                        var0.getDialogueManager().showNpcTwoLineDialogue("You're still hunting " + var0.getSlayerManager().slayerTaskName + "s; come back", "when you've finished your task.", 588);
                                        var0.getDialogueManager().finishDialogue();
                                    } else {
                                        var0.getSlayerManager().assignTaskFromMaster(var1_1);
                                    }
                                } else if (var0.getSlayerManager().slayerMasterId == 70 && !var0.getSlayerManager().slayerTaskName.equals("")) {
                                    var0.getDialogueManager().showNpcTwoLineDialogue("You're still hunting " + var0.getSlayerManager().slayerTaskName + "s; come back", "when you've finished your task.", 588);
                                    var0.getDialogueManager().finishDialogue();
                                } else {
                                    var0.getSlayerManager().assignTaskFromMaster(var1_1);
                                }
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showNpcOneLineDialogue("I have a wide selection of Slayer equipment; take a look!", 588);
                                var0.getDialogueManager().setNextDialogueStep(6);
                                return true;
                            }
                        }
                        break;
                    }
                    case 4: {
                        var0.getDialogueManager().showTwoOptions("Do you have any tips for me?", "Thanks, I'll be on my way.");
                        return true;
                    }
                    case 5: {
                        switch (var3_18) {
                            case 1: {
                                var9_47 = SlayerMonsterGuide.forMonsterName(var0.getSlayerManager().slayerTaskName);
                                if (var9_47 == null) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("There is no tips about this npc yet.", 588);
                                } else {
                                    var0.getDialogueManager().showNpcDialogue(var9_47.getGuideTextLines(), 588);
                                }
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                            case 2: {
                                var0.getDialogueManager().showPlayerOneLineDialogue("Thanks, I'll be on my way.", 588);
                                var0.getDialogueManager().finishDialogue();
                                return true;
                            }
                        }
                        break;
                    }
                    case 6: {
                        ShopManager.openShop(var0, GameplayHelper.getNpcShopId(var1_1));
                        var0.getDialogueManager().markDialogueInactive();
                    }
                }
                break;
            }
            case 956: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("'Ere, matey, 'ave some 'o the good stuff.", 588);
                        return true;
                    }
                    case 2: {
                        var0.getInventoryManager().addOrDropItem(new ItemStack(1971));
                        var0.getInventoryManager().addOrDropItem(new ItemStack(1917));
                        var0.getDialogueManager().showTwoItemMessage("The dwarf gives you beer and a kebab.", "", new ItemStack(1971), new ItemStack(1917));
                        CacheDefinitionIndex.dismissRandomEventNpc(var0);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 409: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcOneLineDialogue("Here you go " + var0.getUsername() + ".", 588);
                        return true;
                    }
                    case 2: {
                        var0.getInventoryManager().addOrDropItem(new ItemStack(2528));
                        var0.getDialogueManager().showItemMessage("The genie gives you a lamp.", new ItemStack(2528));
                        CacheDefinitionIndex.dismissRandomEventNpc(var0);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 2476: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Today is your lucky day, sirrah!", "I am  donating to the victims of crime to atone", "for my past actions!", 588);
                        return true;
                    }
                    case 2: {
                        var9_48 = new ItemStack[]{new ItemStack(995, 50), new ItemStack(1969), new ItemStack(985), new ItemStack(987), new ItemStack(1623), new ItemStack(1621), new ItemStack(1619), new ItemStack(1617)};
                        var10_50 = var9_48[GameUtil.randomExclusive(8)];
                        var0.getInventoryManager().addOrDropItem(var10_50);
                        var0.getDialogueManager().showItemMessage("Rick hands you " + var10_50.getDefinition().getName().toLowerCase() + ".", var10_50);
                        CacheDefinitionIndex.dismissRandomEventNpc(var0);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 2540: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        if (var0.getRandomEventRequestedItem() == null) {
                            var0.setRandomEventRequestedItem(RandomEventManager.selectJekyllRequestedHerb());
                        }
                        var0.getDialogueManager().showNpcTwoLineDialogue("Hello " + var0.getUsername() + ",", "would you happen to have a " + var0.getRandomEventRequestedItem().getDefinition().getName().toLowerCase() + "?", 588);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes I do, here you go.", "No I don't, sorry.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                if (var0.getInventoryManager().removeItem(var0.getRandomEventRequestedItem())) {
                                    var0.getDialogueManager().showNpcOneLineDialogue("Oh thank you so much, here have this potion.", 588);
                                } else {
                                    var0.getDialogueManager().showNpcTwoLineDialogue("Looks like you don't have it. Oh well,", "have this potion I don't need anyways.", 588);
                                    var0.setRandomEventRequestedItem(new ItemStack(1));
                                }
                                return true;
                            }
                            case 2: {
                                var0.setRandomEventRequestedItem(new ItemStack(1));
                                var0.getDialogueManager().showNpcTwoLineDialogue("Oh well, was worth a try.", "Here, have this potion I don't need.", 588);
                                return true;
                            }
                        }
                        break block0;
                    }
                    case 4: {
                        var9_49 = RandomEventManager.getJekyllPotionRewardForHerb(var0.getRandomEventRequestedItem().getId());
                        var0.getInventoryManager().addOrDropItem(var9_49);
                        var0.getDialogueManager().showTwoItemMessage("Jekyll hands you " + var9_49.getDefinition().getName().toLowerCase() + ".", "", new ItemStack(-1, 1), var9_49);
                        var0.setRandomEventRequestedItem(null);
                        CacheDefinitionIndex.dismissRandomEventNpc(var0);
                        var0.getDialogueManager().finishDialogue();
                        return true;
                    }
                }
                break;
            }
            case 500: {
                switch (var0.getDialogueManager().getDialogueStep()) {
                    case 1: {
                        var0.getDialogueManager().showNpcThreeLineDialogue("Would you like to enter this village?", "Note that once you enter, you cannot get out", "through this way.", 591);
                        return true;
                    }
                    case 2: {
                        var0.getDialogueManager().showTwoOptions("Yes let me in please.", "No thanks.");
                        return true;
                    }
                    case 3: {
                        switch (var3_18) {
                            case 1: {
                                var0.scheduleDelayedMove(new Position(2876, 2952));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        if (var0.getDialogueManager().getDialogueStep() > 1) {
            var0.getPacketSender().closeInterfaces();
        }
        if (var0.getDialogueManager().getDialogueId() >= 0) {
            var0.getDialogueManager().resetDialogueState();
        }
        return false;
    }
}
