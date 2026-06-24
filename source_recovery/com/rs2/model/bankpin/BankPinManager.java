/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.bankpin;

import com.rs2.model.World;
import com.rs2.model.bankpin.BankPinEntryMode;
import com.rs2.model.bankpin.BankPinProtectedAction;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.gameplay.SmokeDungeonDamageTask;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.BankManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.guide.SkillGuideCategory;
import com.rs2.model.skill.guide.SkillGuideEntry;
import com.rs2.model.skill.guide.SkillGuideManager;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.Random;

public class BankPinManager {
    private Player player;
    private int[] currentPin = new int[]{-1, -1, -1, -1};
    private int[] pendingPin = new int[]{-1, -1, -1, -1};
    private int currentDigitIndex = -1;
    private int pinAppendYear = -1;
    private int pinAppendDate = -1;
    private boolean verified = false;
    private boolean changingPin = false;
    private boolean deletingPin = false;
    private BankPinEntryMode entryMode = BankPinEntryMode.a;
    private BankPinProtectedAction protectedAction = BankPinProtectedAction.BANK;
    private int[] digitButtonIds = new int[]{14883, 14884, 14885, 14886, 14887, 14888, 14889, 14890, 14891, 14892};
    private static /* synthetic */ int[] m;
    private static /* synthetic */ int[] n;

    public BankPinManager(Player player) {
        this.player = player;
    }

    public final boolean handleButtonClick(int buttonId) {
        switch (buttonId) {
            case 14922: {
                this.player.packetSender.closeInterfaces();
                return true;
            }
            case 14921: {
                DialogueManager.continueDialogue(this.player, 494, 19, 0);
                return true;
            }
        }

        if (this.entryMode == BankPinEntryMode.a) {
            for (int digitButton = 0; digitButton < 10; ++digitButton) {
                if (buttonId != digitButton + 14873) {
                    continue;
                }
                this.player.packetSender.sendSoundEffect(1827, 1, 0);
                this.player.setBankPinEntryDigit(this.decodeDigitButton(buttonId), this.currentDigitIndex);
                if (this.currentDigitIndex + 1 < 4) {
                    this.setCurrentDigitIndex(this.currentDigitIndex + 1);
                    return true;
                }

                boolean correctPin = true;
                for (int digitIndex = 0; digitIndex < 4; ++digitIndex) {
                    if (this.currentPin[digitIndex] < 0) {
                        break;
                    }
                    if (this.currentPin[digitIndex] != this.player.getBankPinEntryDigits()[digitIndex]) {
                        correctPin = false;
                        break;
                    }
                }

                if (!correctPin) {
                    this.player.packetSender.sendGameMessage("You've entered an incorrect pin, please try again.");
                    this.resetPinEntryInterface();
                    this.setCurrentDigitIndex(0);
                    this.player.resetBankPinEntryDigits();
                    this.player.packetSender.sendSoundEffect(1828, 1, 0);
                    return true;
                }

                this.player.packetSender.sendGameMessage("You have successfully verified your bank pin.");
                this.verified = true;
                if (this.protectedAction == BankPinProtectedAction.BANK) {
                    BankManager.openBank(this.player);
                }
                this.player.packetSender.sendSoundEffect(1257, 1, 0);
                return true;
            }
        } else if (this.entryMode == BankPinEntryMode.b) {
            for (int digitButton = 0; digitButton < 10; ++digitButton) {
                if (buttonId != digitButton + 14873) {
                    continue;
                }
                this.pendingPin[this.currentDigitIndex] = this.decodeDigitButton(buttonId);
                this.player.packetSender.sendSoundEffect(1827, 1, 0);
                if (this.currentDigitIndex + 1 < 4) {
                    this.setCurrentDigitIndex(this.currentDigitIndex + 1);
                } else {
                    DialogueManager.continueDialogue(this.player, 494, 11, 0);
                }
                return true;
            }
        }
        return false;
    }

    private int decodeDigitButton(int n) {
        int n2 = 0;
        while (n2 < this.digitButtonIds.length) {
            int n3 = this.digitButtonIds[n2];
            if (n + 10 == n3) {
                return n2;
            }
            ++n2;
        }
        return -1;
    }

    private static void shuffleDigitButtons(int[] nArray) {
        Random random = new Random();
        int n = nArray.length - 1;
        while (n > 0) {
            int n2 = random.nextInt(n + 1);
            int n3 = nArray[n2];
            nArray[n2] = nArray[n];
            nArray[n] = n3;
            --n;
        }
    }

    public final void setEntryMode(BankPinEntryMode bankPinEntryMode) {
        this.entryMode = bankPinEntryMode;
        this.resetPinEntryInterface();
        this.setCurrentDigitIndex(0);
    }

    private void setCurrentDigitIndex(int n) {
        Player player;
        this.currentDigitIndex = n;
        int n2 = 0;
        while (n2 < n) {
            player = this.player;
            player.packetSender.sendInterfaceText("*", n2 + 14913);
            player = this.player;
            player.packetSender.sendInterfaceText("Click the " + GameUtil.getOrdinalWord(n2 + 2) + " digit...", 15313);
            ++n2;
        }
        BankPinManager.shuffleDigitButtons(this.digitButtonIds);
        n2 = 0;
        while (n2 < this.digitButtonIds.length) {
            player = this.player;
            player.packetSender.sendInterfacePosition(this.digitButtonIds[n2], GameUtil.randomInclusive(47), -GameUtil.randomInclusive(42));
            player = this.player;
            player.packetSender.sendInterfaceText(String.valueOf(n2), this.digitButtonIds[n2]);
            ++n2;
        }
        player = this.player;
        player.packetSender.showInterface(7424);
    }

    public final void processPendingPinChanges() {
        int n;
        if (this.changingPin) {
            n = this.getPendingPinDaysElapsed();
            if (n >= 7) {
                this.applyPendingPinChange();
            } else if (!this.hasPin()) {
                this.applyPendingPinChange();
            }
        }
        if (this.deletingPin && (n = this.getPendingPinDaysElapsed()) >= 7) {
            Object object = this;
            int n2 = 0;
            while (n2 < ((BankPinManager)object).currentPin.length) {
                ((BankPinManager)object).currentPin[n2] = -1;
                ((BankPinManager)object).pendingPin[n2] = -1;
                ++n2;
            }
            ((BankPinManager)object).deletingPin = false;
            ((BankPinManager)object).pinAppendYear = -1;
            ((BankPinManager)object).pinAppendDate = -1;
            object = ((BankPinManager)object).player;
            ((Player)object).packetSender.sendGameMessage("Your bank pin has been successfully deleted!");
        }
    }

    private void applyPendingPinChange() {
        Player player = this.player;
        player.packetSender.sendGameMessage("Your bank pin has been successfully " + (this.hasPin() ? "changed" : "set") + "!");
        int n = 0;
        while (n < this.currentPin.length) {
            this.currentPin[n] = this.pendingPin[n];
            this.pendingPin[n] = -1;
            ++n;
        }
        this.changingPin = false;
        this.pinAppendYear = -1;
        this.pinAppendDate = -1;
    }

    private int getPendingPinDaysElapsed() {
        if (GameUtil.getCurrentYear() == this.pinAppendYear) {
            return GameUtil.getDayOfYear() - this.pinAppendDate;
        }
        return 365 - this.pinAppendDate + GameUtil.getDayOfYear();
    }

    private void resetPinEntryInterface() {
        this.currentDigitIndex = -1;
        Player player = this.player;
        BankPinManager bankPinManager = this;
        int n = bankPinManager.getPendingPinDaysElapsed();
        player.packetSender.sendInterfaceText(bankPinManager.changingPin ? "You bank pin will change in " + (7 - n) + " days." : (bankPinManager.deletingPin ? "You bank pin will be deleted in " + (7 - n) + " days." : (bankPinManager.hasPin() ? "Your bank pin is set and up to date." : "You do not have a bank pin.")), 14923);
        Player player2 = this.player;
        player2.packetSender.sendInterfaceText("Click the " + GameUtil.getOrdinalWord(1) + " digit...", 15313);
        int n2 = 0;
        while (n2 < 4) {
            player2 = this.player;
            player2.packetSender.sendInterfaceText("?", n2 + 14913);
            ++n2;
        }
        n2 = 0;
        while (n2 < 10) {
            player2 = this.player;
            player2.packetSender.sendInterfaceText(String.valueOf(n2), n2 + 14883);
            ++n2;
        }
    }

    public final void requestPinChange() {
        this.changingPin = true;
        this.pinAppendYear = GameUtil.getCurrentYear();
        this.pinAppendDate = GameUtil.getDayOfYear();
    }

    public final void requestPinDeletion() {
        this.clearPendingPinChange();
        this.deletingPin = true;
        this.pinAppendYear = GameUtil.getCurrentYear();
        this.pinAppendDate = GameUtil.getDayOfYear();
    }

    public final void clearPendingPinChange() {
        int n = 0;
        while (n < this.pendingPin.length) {
            this.pendingPin[n] = -1;
            ++n;
        }
        this.changingPin = false;
        this.deletingPin = false;
        this.pinAppendYear = -1;
        this.pinAppendDate = -1;
    }

    public final boolean hasPin() {
        return this.currentPin[0] != -1;
    }

    public final boolean hasPendingPinChange() {
        return this.changingPin || this.deletingPin;
    }

    public final boolean isVerified() {
        return this.verified;
    }

    public final int[] getPendingPin() {
        return this.pendingPin;
    }

    public final int[] getCurrentPin() {
        return this.currentPin;
    }

    public final void setChangingPin(boolean bl) {
        this.changingPin = bl;
    }

    public final boolean isChangingPin() {
        return this.changingPin;
    }

    public final void setDeletingPin(boolean bl) {
        this.deletingPin = bl;
    }

    public final boolean isDeletingPin() {
        return this.deletingPin;
    }

    public final void setPinAppendYear(int n) {
        this.pinAppendYear = n;
    }

    public final int getPinAppendYear() {
        return this.pinAppendYear;
    }

    public final void setPinAppendDate(int n) {
        this.pinAppendDate = n;
    }

    public final int getPinAppendDate() {
        return this.pinAppendDate;
    }

    private static /* synthetic */ int[] q() {
        if (m != null) {
            return m;
        }
        int[] nArray = new int[BankPinEntryMode.values().length];
        try {
            nArray[BankPinEntryMode.b.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[BankPinEntryMode.a.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        m = nArray;
        return nArray;
    }

    private static /* synthetic */ int[] r() {
        if (n != null) {
            return n;
        }
        int[] nArray = new int[BankPinProtectedAction.values().length];
        try {
            nArray[BankPinProtectedAction.BANK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        n = nArray;
        return nArray;
    }

    public static boolean showSkillUnlockMessage(Player player, int n) {
        if (n != 13 && n != 8 && n != 16 && n != 0 && n != 7 && n != 12 && n != 1 && n != 19 && n != 11 && n != 10 && n != 9 && n != 15 && n != 14 && n != 4 && n != 20 && n != 18 && n != 2 && n != 17) {
            return false;
        }
        Object object = SkillGuideManager.getCategoriesForSkillId(n);
        if (object == null) {
            return false;
        }
        int n2 = player.getSkillManager().getBaseLevel(n);
        int n3 = -1;
        boolean bl = false;
        int n4 = 0;
        while (n4 < ((ArrayList)object).size()) {
            Object object2;
            Object object3;
            SkillGuideCategory skillGuideCategory = (SkillGuideCategory)((ArrayList)object).get(n4);
            Object object4 = null;
            int n5 = 0;
            while (n5 < skillGuideCategory.entries.size()) {
                object3 = (SkillGuideEntry)skillGuideCategory.entries.get(n5);
                object2 = skillGuideCategory.getLevelText();
                if (((String)object2).equals("-1")) {
                    object2 = ((SkillGuideEntry)object3).getLevelText();
                    if (((String)object2).equals("-1")) {
                        object2 = "";
                    }
                    if (!((String)object2).equals("")) {
                        n3 = Integer.parseInt((String)object2);
                    }
                    if (n3 == n2) {
                        object4 = object3;
                        break;
                    }
                }
                ++n5;
            }
            if (object4 != null) {
                object2 = object4;
                n5 = ((SkillGuideEntry)object2).itemId;
                if (n5 >= 0) {
                    object2 = skillGuideCategory;
                    if (!((SkillGuideCategory)object2).skipItemDefinitionLookup) {
                        object2 = skillGuideCategory;
                        if (ItemDefinition.isDefined(n5)) {
                            object3 = new ItemStack(n5);
                            object2 = ((ItemStack)object3).getDefinition();
                            if (((ItemDefinition)object2).isMembersOnly()) {
                                bl = true;
                            }
                            object = ((ItemStack)object3).getDefinition().getName();
                            String string = (String)object;
                            String string2 = ".";
                            String string3 = "@bla@You ";
                            if (bl) {
                                string3 = "@bla@Members ";
                            }
                            String string4 = "can now ";
                            String string5 = "";
                            if (n == 13) {
                                string5 = ((String)object).endsWith("ore") ? String.valueOf(string5) + "smelt" : String.valueOf(string5) + "make";
                                string = String.valueOf(string) + "s";
                            } else if (n == 8) {
                                if (!((String)object).endsWith("axe")) {
                                    string5 = String.valueOf(string5) + "cut down";
                                    object2 = object4;
                                    string = ((SkillGuideEntry)object2).label;
                                } else {
                                    string5 = String.valueOf(string5) + "chop with a";
                                }
                            } else if (n == 16) {
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                                string5 = string.toLowerCase().endsWith("course") ? String.valueOf(string5) + "enter the" : String.valueOf(string5) + "use";
                            } else if (n == 0) {
                                string5 = String.valueOf(string5) + "wield";
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                                string = String.valueOf(string) + " weaponry";
                                string2 = "!";
                            } else if (n == 7) {
                                string5 = String.valueOf(string5) + "cook";
                            } else if (n == 12) {
                                object2 = skillGuideCategory;
                                if (((SkillGuideCategory)object2).name.toLowerCase().equals("milestones")) {
                                    object2 = object4;
                                    string = ((SkillGuideEntry)object2).label;
                                    string5 = String.valueOf(string5) + "enter the";
                                } else {
                                    string5 = String.valueOf(string5) + "craft";
                                }
                            } else if (n == 1) {
                                string5 = String.valueOf(string5) + "wear";
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                                string = String.valueOf(string) + " armour";
                                string2 = "!";
                            } else if (n == 19) {
                                string5 = String.valueOf(string5) + "grow";
                                string = String.valueOf(string) + "s";
                            } else if (n == 11) {
                                string5 = String.valueOf(string5) + "light";
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                            } else if (n == 10) {
                                string5 = String.valueOf(string5) + "try to catch";
                                if (!(string = string.replaceAll("Raw ", "")).endsWith("s")) {
                                    string = String.valueOf(string) + "s";
                                }
                            } else if (n == 9) {
                                string5 = String.valueOf(string5) + "craft";
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                            } else if (n == 15) {
                                object2 = skillGuideCategory;
                                string5 = ((SkillGuideCategory)object2).name.toLowerCase().equals("herbs") ? String.valueOf(string5) + "identify" : String.valueOf(string5) + "make";
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                            } else if (n == 14) {
                                object2 = skillGuideCategory;
                                if (((SkillGuideCategory)object2).name.toLowerCase().equals("milestones")) {
                                    object2 = object4;
                                    string = ((SkillGuideEntry)object2).label;
                                    string5 = String.valueOf(string5) + "enter the";
                                } else if (!((String)object).endsWith("axe")) {
                                    string5 = String.valueOf(string5) + "mine";
                                    object2 = object4;
                                    string = ((SkillGuideEntry)object2).label;
                                    string = string.replaceAll(" ore", "");
                                } else {
                                    string5 = String.valueOf(string5) + "mine with";
                                    string = String.valueOf(string) + "s";
                                }
                            } else if (n == 4) {
                                object2 = skillGuideCategory;
                                string5 = ((SkillGuideCategory)object2).name.toLowerCase().equals("armour") ? String.valueOf(string5) + "wear" : String.valueOf(string5) + "wield";
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                            } else if (n == 20) {
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                                object2 = skillGuideCategory;
                                if (((SkillGuideCategory)object2).name.toLowerCase().equals("runes")) {
                                    string5 = String.valueOf(string5) + "craft";
                                } else {
                                    object2 = skillGuideCategory;
                                    if (((SkillGuideCategory)object2).name.toLowerCase().equals("multiples")) {
                                        string5 = String.valueOf(string5) + "receive";
                                        string4 = "will now ";
                                    } else {
                                        object2 = skillGuideCategory;
                                        if (((SkillGuideCategory)object2).name.toLowerCase().equals("pouches")) {
                                            string5 = String.valueOf(string5) + "use ";
                                        }
                                    }
                                }
                            } else if (n == 18) {
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                                object2 = skillGuideCategory;
                                string5 = ((SkillGuideCategory)object2).name.toLowerCase().equals("monsters") ? String.valueOf(string5) + "kill" : String.valueOf(string5) + "use";
                            } else if (n == 2) {
                                string5 = String.valueOf(string5) + "wield";
                                string = String.valueOf(string) + "s";
                            } else if (n == 17) {
                                object2 = object4;
                                string = ((SkillGuideEntry)object2).label;
                                object2 = skillGuideCategory;
                                if (((SkillGuideCategory)object2).name.toLowerCase().equals("pickpocket")) {
                                    string5 = String.valueOf(string5) + "pickpocket";
                                    string = String.valueOf(string) + "s";
                                } else {
                                    string5 = String.valueOf(string5) + "steal from";
                                    string = String.valueOf(string) + "s";
                                }
                            }
                            string5 = String.valueOf(string5) + " @dbl@" + GameUtil.capitalizeWords(string) + "@bla@" + string2;
                            player.getDialogueManager().showItemMessage(String.valueOf(string3) + string4 + string5, (ItemStack)object3);
                            return true;
                        }
                    }
                }
            }
            ++n4;
        }
        return false;
    }

    public static boolean a(Player player) {
        if (player.eq == 1235) {
            return true;
        }
        if (player.isInSmokeDungeon()) {
            SmokeDungeonDamageTask smokeDungeonDamageTask = new SmokeDungeonDamageTask(20, player);
            World.getTaskScheduler().schedule(smokeDungeonDamageTask);
            player.eq = 1235;
            return true;
        }
        return false;
    }
}
