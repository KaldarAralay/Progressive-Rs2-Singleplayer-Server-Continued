/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill;

import com.rs2.ServerSettings;
import com.rs2.bot.BotTaskPlanner;
import com.rs2.model.World;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillLevelRestoreTask;
import com.rs2.model.skill.SpecialEnergyRestoreTask;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class SkillManager {
    private Player player;
    private int[] currentLevels = new int[22];
    private double[] experience = new double[22];
    private TickTask[] levelRestoreTasks = new TickTask[22];
    private int[] restoreDelayTicks = new int[22];
    private TickTask specialEnergyRestoreTask;
    private static final int[] experienceForLevel = new int[ServerSettings.maxLevel + 1];
    public static final String[] SKILL_NAMES;
    private long actionDelayExpiresAtMillis = -10000L;
    private long drinkDelayExpiresAtMillis = -10000L;
    public static int maxCombatLevel;

    static {
        int n = 0;
        int n2 = 1;
        while (n2 <= ServerSettings.maxLevel) {
            int n3;
            n = (int)((double)n + Math.floor((double)n2 + 300.0 * Math.pow(2.0, (double)n2 / 7.0)));
            SkillManager.experienceForLevel[n2] = n3 = (int)Math.floor(n / 4);
            ++n2;
        }
        SKILL_NAMES = new String[]{"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting"};
        maxCombatLevel = 126;
    }

    private double getExperienceCap() {
        if (ServerSettings.capXpAtMaxLevel) {
            return SkillManager.getExperienceForLevel(ServerSettings.maxLevel - 1);
        }
        return 2.0E8;
    }

    public SkillManager(Player player) {
        int[][] nArrayArray = new int[21][];
        int[] nArray = new int[4];
        nArray[1] = 6247;
        nArrayArray[0] = nArray;
        int[] nArray2 = new int[4];
        nArray2[0] = 1;
        nArray2[1] = 6253;
        nArrayArray[1] = nArray2;
        int[] nArray3 = new int[4];
        nArray3[0] = 2;
        nArray3[1] = 6206;
        nArrayArray[2] = nArray3;
        int[] nArray4 = new int[4];
        nArray4[0] = 3;
        nArray4[1] = 6216;
        nArrayArray[3] = nArray4;
        nArrayArray[4] = new int[]{4, 4443, 5453, 6114};
        int[] nArray5 = new int[4];
        nArray5[0] = 5;
        nArray5[1] = 6242;
        nArrayArray[5] = nArray5;
        int[] nArray6 = new int[4];
        nArray6[0] = 6;
        nArray6[1] = 6211;
        nArrayArray[6] = nArray6;
        int[] nArray7 = new int[4];
        nArray7[0] = 7;
        nArray7[1] = 6226;
        nArrayArray[7] = nArray7;
        int[] nArray8 = new int[4];
        nArray8[0] = 8;
        nArray8[1] = 4272;
        nArrayArray[8] = nArray8;
        int[] nArray9 = new int[4];
        nArray9[0] = 9;
        nArray9[1] = 6231;
        nArrayArray[9] = nArray9;
        int[] nArray10 = new int[4];
        nArray10[0] = 10;
        nArray10[1] = 6258;
        nArrayArray[10] = nArray10;
        int[] nArray11 = new int[4];
        nArray11[0] = 11;
        nArray11[1] = 4282;
        nArrayArray[11] = nArray11;
        int[] nArray12 = new int[4];
        nArray12[0] = 12;
        nArray12[1] = 6263;
        nArrayArray[12] = nArray12;
        int[] nArray13 = new int[4];
        nArray13[0] = 13;
        nArray13[1] = 6221;
        nArrayArray[13] = nArray13;
        nArrayArray[14] = new int[]{14, 4416, 4417, 4438};
        int[] nArray14 = new int[4];
        nArray14[0] = 15;
        nArray14[1] = 6237;
        nArrayArray[15] = nArray14;
        int[] nArray15 = new int[4];
        nArray15[0] = 16;
        nArray15[1] = 4277;
        nArrayArray[16] = nArray15;
        nArrayArray[17] = new int[]{17, 4261, 4263, 4264};
        int[] nArray16 = new int[4];
        nArray16[0] = 18;
        nArray16[1] = 12122;
        nArrayArray[18] = nArray16;
        nArrayArray[19] = new int[]{19, 4887, 4889, 4890};
        int[] nArray17 = new int[4];
        nArray17[0] = 20;
        nArray17[1] = 4267;
        nArrayArray[20] = nArray17;
        this.player = player;
        int n = 0;
        while (n < this.currentLevels.length) {
            if (n == 3) {
                this.currentLevels[n] = 10;
                this.experience[n] = 1154.0;
            } else {
                this.currentLevels[n] = 1;
                this.experience[n] = 0.0;
            }
            this.setSkillRestoreDelay(n, 100);
            ++n;
        }
    }

    public final void startRestorationTasks() {
        Object object;
        int n = 0;
        while (n < 22) {
            if (n != 5) {
                int n2 = n;
                SkillManager skillManager = this;
                object = skillManager.levelRestoreTasks[n2];
                SkillManager skillManager2 = skillManager;
                if (skillManager.currentLevels[n2] != SkillManager.getLevelForExperience(skillManager2.experience[n2])) {
                    if (skillManager.levelRestoreTasks[n2] == null || !skillManager.levelRestoreTasks[n2].isActive()) {
                        int n3 = skillManager.restoreDelayTicks[n2];
                        skillManager.levelRestoreTasks[n2] = new SkillLevelRestoreTask(skillManager, n3, n2);
                        World.getTaskScheduler().schedule(skillManager.levelRestoreTasks[n2]);
                    }
                } else if (object != null) {
                    ((TickTask)object).stop();
                    int n4 = skillManager.restoreDelayTicks[n2];
                    ((TickTask)object).setIntervalTicks(n4);
                    ((TickTask)object).setRemainingTicks(n4);
                }
            }
            ++n;
        }
        SkillManager skillManager = this;
        TickTask tickTask = skillManager.specialEnergyRestoreTask;
        if (skillManager.player.getSpecialEnergy() < 100) {
            object = skillManager;
            if (((SkillManager)object).specialEnergyRestoreTask == null || !((SkillManager)object).specialEnergyRestoreTask.isActive()) {
                ((SkillManager)object).specialEnergyRestoreTask = new SpecialEnergyRestoreTask((SkillManager)object, 50);
                World.getTaskScheduler().schedule(((SkillManager)object).specialEnergyRestoreTask);
            }
            return;
        }
        if (tickTask != null) {
            tickTask.stop();
            tickTask.setIntervalTicks(50);
            tickTask.setRemainingTicks(50);
        }
    }

    public final void setRapidHealRestoreDelay(boolean bl) {
        this.setSkillRestoreDelay(3, bl ? 50 : 100);
    }

    public final void restoreNonPrayerLevels() {
        int n = 0;
        while (n < 22) {
            if (n != 3 && n != 5) {
                SkillManager skillManager = this;
                if (this.currentLevels[n] != SkillManager.getLevelForExperience(skillManager.experience[n])) {
                    skillManager = this;
                    if (this.currentLevels[n] > SkillManager.getLevelForExperience(skillManager.experience[n])) {
                        int n2 = n;
                        this.currentLevels[n2] = this.currentLevels[n2] - 1;
                    } else {
                        int n3 = n;
                        this.currentLevels[n3] = this.currentLevels[n3] + 1;
                    }
                    this.refreshSkill(n);
                }
            }
            ++n;
        }
    }

    private void setSkillRestoreDelay(int n, int n2) {
        this.restoreDelayTicks[n] = n2;
        TickTask tickTask = this.levelRestoreTasks[n];
        if (tickTask != null) {
            tickTask.setIntervalTicks(n2);
            tickTask.setRemainingTicks(n2);
        }
    }

    public final boolean isLevelModified(int n) {
        SkillManager skillManager = this;
        return this.currentLevels[n] != SkillManager.getLevelForExperience(skillManager.experience[n]);
    }

    public final boolean isSpecialEnergyBelowMaximum() {
        return this.player.getSpecialEnergy() < 100;
    }

    public final void d() {
        this.refreshAllSkills();
    }

    public final void refreshAllSkills() {
        int n = 0;
        while (n < this.currentLevels.length) {
            Player player = this.player;
            player.packetSender.sendSkillUpdate(n, this.currentLevels[n], this.experience[n]);
            ++n;
        }
        this.player.setCombatLevel(this.getCombatLevel());
        this.player.setAppearanceUpdateRequired(true);
    }

    public final void refreshSkill(int n) {
        Player player = this.player;
        player.packetSender.sendSkillUpdate(n, this.currentLevels[n], this.experience[n]);
        this.player.setCombatLevel(this.getCombatLevel());
        this.player.setAppearanceUpdateRequired(true);
    }

    public final int getBaseLevel(int n) {
        return SkillManager.getLevelForExperience(this.experience[n]);
    }

    public static int getLevelForExperience(double d) {
        int n = 1;
        while (n <= ServerSettings.maxLevel) {
            if ((double)experienceForLevel[n] > d) {
                return n;
            }
            ++n;
        }
        return ServerSettings.maxLevel;
    }

    public static int getExperienceForLevel(int n) {
        if (n >= experienceForLevel.length) {
            return Integer.MAX_VALUE;
        }
        return experienceForLevel[n];
    }

    /*
     * Enabled aggressive block sorting
     */
    public final int getTotalLevel() {
        int n = 0;
        int n2 = 0;
        while (n2 < 21) {
            if (!(n2 == 18 && ServerSettings.cacheVersion < 319 || n2 == 19 && ServerSettings.cacheVersion < 336)) {
                n += this.getBaseLevel(n2);
            }
            ++n2;
        }
        return n;
    }

    /*
     * Enabled aggressive block sorting
     */
    public final long getTotalExperience() {
        long l = 0L;
        int n = 0;
        while (n < 21) {
            SkillManager skillManager = this;
            l = (long)((double)l + skillManager.experience[n]);
            ++n;
        }
        return l;
    }

    public final int calculateExperienceGain(int n, double d) {
        d = ServerSettings.progressiveXpMode == 0 ? (d *= ServerSettings.xpRate) : (d *= this.getExperienceRateForSkill(n));
        if (this.player.isBot && ServerSettings.botXpRateMode == 1) {
            d *= ServerSettings.botXpRateMultiplier;
        } else if ((this.player.isBot || this.player.botEnabled) && ServerSettings.botXpRateMode == 2) {
            d *= ServerSettings.botXpRateMultiplier;
        }
        if (this.player.getEnchantmentChamberController().isInsideChamber() || this.player.getAlchemistPlaygroundController().isInsidePlayground() || this.player.getCreatureGraveyardController().isInsideGraveyard() || this.player.getTelekineticTheatreController().isInsideTheatre()) {
            d *= 0.75;
        }
        n = (int)d;
        return n;
    }

    private double getExperienceRateForSkill(int n) {
        double d = 1.0;
        n = SkillManager.getLevelForExperience(this.experience[n]);
        if (ServerSettings.progressiveXpMode == 1) {
            double d2 = Math.pow(1.02, n);
            double d3 = Math.pow(n, 2.0);
            double d4 = d3 / 3300.0;
            d = 1.0 + (d2 + d4 - 1.0) * ServerSettings.xpRate;
        } else if (ServerSettings.progressiveXpMode == 2) {
            d = 1.0 + (double)(n - 1) * (0.1 * ServerSettings.xpRate);
        }
        return d;
    }

    private double getExperienceRateForLevel(int n) {
        double d = 1.0;
        if (ServerSettings.progressiveXpMode == 1) {
            double d2 = Math.pow(1.02, n);
            double d3 = Math.pow(n, 2.0);
            double d4 = d3 / 3300.0;
            d = 1.0 + (d2 + d4 - 1.0) * ServerSettings.xpRate;
        } else if (ServerSettings.progressiveXpMode == 2) {
            d = 1.0 + (double)(n - 1) * (0.1 * ServerSettings.xpRate);
        }
        if (this.player.isBot && ServerSettings.botXpRateMode == 1) {
            d *= ServerSettings.botXpRateMultiplier;
        } else if ((this.player.isBot || this.player.botEnabled) && ServerSettings.botXpRateMode == 2) {
            d *= ServerSettings.botXpRateMultiplier;
        }
        return d;
    }

    public final boolean addExperience(int n, double d) {
        int n2;
        int n3;
        int n4 = SkillManager.getLevelForExperience(this.experience[n]);
        if (n4 >= 3 && this.player.getQuestState(0) != 1) {
            return false;
        }
        if (d <= 0.0) {
            return false;
        }
        boolean bl = false;
        boolean bl2 = this.player.isMember();
        this.player.temporaryActionValue = n4;
        d = ServerSettings.progressiveXpMode == 0 ? (d *= ServerSettings.xpRate) : (d *= this.getExperienceRateForSkill(n));
        if (this.player.isBot && ServerSettings.botXpRateMode == 1) {
            d *= ServerSettings.botXpRateMultiplier;
        } else if ((this.player.isBot || this.player.botEnabled) && ServerSettings.botXpRateMode == 2) {
            d *= ServerSettings.botXpRateMultiplier;
        }
        if (this.player.getEnchantmentChamberController().isInsideChamber() || this.player.getAlchemistPlaygroundController().isInsidePlayground() || this.player.getCreatureGraveyardController().isInsideGraveyard() || this.player.getTelekineticTheatreController().isInsideTheatre()) {
            int n5 = n;
            this.experience[n5] = this.experience[n5] + d * 0.75;
        } else {
            int n6 = n;
            this.experience[n6] = this.experience[n6] + d;
        }
        if (this.experience[n] > this.getExperienceCap()) {
            this.experience[n] = this.getExperienceCap();
        }
        if ((n3 = (n2 = SkillManager.getLevelForExperience(this.experience[n])) - n4) > 0) {
            bl = true;
            int n7 = n;
            this.currentLevels[n7] = this.currentLevels[n7] + n3;
            if (n > 0 && n <= 6) {
                this.player.setCombatLevel(this.getCombatLevel());
            }
            this.showLevelUpInterface(n);
            if (this.player.botEnabled) {
                if (GameUtil.randomInt(5) == 0 && (n2 % 2 == 0 || n2 % 5 == 0)) {
                    this.player.queuePublicChatMessage("Yay " + n2 + " " + SKILL_NAMES[n] + "!");
                }
                if (this.player.currentBotTask != null && this.player.currentBotTask.combatTask && n2 % 5 == 0) {
                    BotTaskPlanner.selectMeleeTrainingFightMode(this.player);
                }
            }
        }
        this.refreshSkill(n);
        if (ServerSettings.membershipRequirementMode == 2 && !bl2 && this.player.isMember()) {
            this.player.packetSender.sendGameMessage("You have reached " + ServerSettings.membershipRequirementValue + "+ total lvl and gained access to members content!");
        }
        return bl;
    }

    public final void addQuestExperience(int n, double d) {
        int n2;
        if (d <= 0.0) {
            return;
        }
        this.player.isMember();
        this.player.temporaryActionValue = n2 = SkillManager.getLevelForExperience(this.experience[n]);
        int n3 = n;
        this.experience[n3] = this.experience[n3] + (d *= ServerSettings.questXpRate);
        if (this.experience[n] > this.getExperienceCap()) {
            this.experience[n] = this.getExperienceCap();
        }
        int n4 = SkillManager.getLevelForExperience(this.experience[n]);
        if ((n4 -= n2) > 0) {
            int n5 = n;
            this.currentLevels[n5] = this.currentLevels[n5] + n4;
            if (n > 0 && n <= 6) {
                this.player.setCombatLevel(this.getCombatLevel());
            }
            this.showLevelUpInterface(n);
        }
        this.refreshSkill(n);
    }

    public final void showLevelUpInterface(int n) {
        Object object;
        if (this.player.deferLevelUpInterfaces) {
            if (!this.player.queuedLevelUpSkillIds.contains(n)) {
                this.player.queuedLevelUpSkillIds.add(n);
            }
            return;
        }
        int[][] nArrayArray = new int[23][];
        int[] nArray = new int[8];
        nArray[1] = 6248;
        nArray[2] = 6249;
        nArray[3] = 6247;
        nArray[4] = 219;
        nArray[5] = 233;
        nArray[6] = 200;
        nArray[7] = 400;
        nArrayArray[0] = nArray;
        nArrayArray[1] = new int[]{1, 6254, 6255, 6253, 216, 210, 256, 256};
        nArrayArray[2] = new int[]{2, 6207, 6208, 6206, 235, 222, 220, 256};
        nArrayArray[3] = new int[]{3, 6217, 6218, 6216, 212, 208, 200, 220};
        nArrayArray[4] = new int[]{4, 5453, 6114, 4443, 202, 224, 256, 256};
        nArrayArray[5] = new int[]{5, 6243, 6244, 6242, 211, 207, 256, 256};
        nArrayArray[6] = new int[]{6, 6212, 6213, 6211, 215, 230, 256, 256};
        nArrayArray[7] = new int[]{7, 6227, 6228, 6226, 196, 217, 256, 180};
        nArrayArray[8] = new int[]{8, 4273, 4274, 4272, 220, 209, 220, 256};
        nArrayArray[9] = new int[]{9, 6232, 6233, 6231, 195, 205, 256, 256};
        nArrayArray[10] = new int[]{10, 6259, 6260, 6258, 213, 226, 256, 256};
        nArrayArray[11] = new int[]{11, 4283, 4284, 4282, 199, 126, 256, 256};
        nArrayArray[12] = new int[]{12, 6264, 6265, 6263, 214, 228, 300, 220};
        nArrayArray[13] = new int[]{13, 6222, 6223, 6221, 229, 206, 256, 256};
        nArrayArray[14] = new int[]{14, 4417, 4438, 4416, 227, 223, 256, 256};
        nArrayArray[15] = new int[]{15, 6238, 6239, 6237, 236, 218, 200, 256};
        nArrayArray[16] = new int[]{16, 4278, 4279, 4277, 231, 231, 256, 256};
        nArrayArray[17] = new int[]{17, 4263, 4264, 4261, 197, 201, 200, 256};
        nArrayArray[18] = new int[]{18, 12123, 12124, 12122, 275, 276, 380, 380};
        nArrayArray[19] = new int[]{19, 313, 312, 310, 422, 424, 320, 175};
        nArrayArray[20] = new int[]{20, 4268, 4269, 4267, 194, 200, 320, 320};
        nArrayArray[21] = new int[]{21, -1, -1, -1, -1, -1, -1, -1};
        nArrayArray[22] = new int[]{22, 19567, 19568, 19566, 656, 657, 256, 256};
        int[][] nArrayArray2 = nArrayArray;
        if (n == nArrayArray2[n][0]) {
            this.player.currentLevelUpSkillId = n;
            Player player = this.player;
            player.packetSender.sendInterfaceText("@dbl@Congratulations, you just advanced a " + SKILL_NAMES[n] + " level!", nArrayArray2[n][1]);
            player = this.player;
            player.packetSender.sendInterfaceText("Your " + SKILL_NAMES[n] + " level is now " + this.getBaseLevel(n) + ".", nArrayArray2[n][2]);
            player = this.player;
            player.packetSender.sendGameMessage("You've just advanced a " + SKILL_NAMES[n] + " level! You have reached level " + this.getBaseLevel(n) + ".");
            if (ServerSettings.progressiveXpMode != 0) {
                int n2 = this.getBaseLevel(n);
                int n3 = this.player.temporaryActionValue;
                double d = this.getExperienceRateForLevel(n3);
                double d2 = this.getExperienceRateForLevel(n2);
                Object object2 = new DecimalFormat("#0.00");
                ((DecimalFormat)object2).setRoundingMode(RoundingMode.FLOOR);
                String string = ((NumberFormat)object2).format(d).replaceAll(",", ".");
                object2 = ((NumberFormat)object2).format(d2).replaceAll(",", ".");
                if (!string.equals(object2)) {
                    Player player2 = this.player;
                    player2.packetSender.sendGameMessage("XP rate increased to: " + (String)object2 + " (Previous XP rate was: " + string + ").");
                }
            }
            this.player.getUpdateState().setGraphic(199);
            this.player.packetSender.sendSoundEffect(323, 1, 0);
            int n4 = this.getBaseLevel(n) >= 50 ? 1 : 0;
            object = this.player;
            ((Player)object).packetSender.sendMusicJingle(nArrayArray2[n][n4 + 4], nArrayArray2[n][n4 + 6]);
            if (n == 19) {
                object = this.player;
                ((Player)object).packetSender.sendInterfacePosition(311, 0, 30);
                object = this.player;
                ((Player)object).packetSender.sendInterfaceModel(311, 200, 5340);
            }
            object = this;
            if (SkillManager.getLevelForExperience(((SkillManager)object).experience[n]) == ServerSettings.maxLevel) {
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("Well done! You've achieved the highest possible level in this skill!");
            }
            object = this.player;
            ((Player)object).packetSender.showChatboxInterface(nArrayArray2[n][3]);
            this.player.getDialogueManager().finishDialogue();
        }
        this.player.setAppearanceUpdateRequired(true);
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText("Total Lvl: " + this.getTotalLevel(), 3984);
    }

    public final int getCombatLevel() {
        double d;
        int n = SkillManager.getLevelForExperience(this.experience[0]);
        int n2 = SkillManager.getLevelForExperience(this.experience[1]);
        int n3 = SkillManager.getLevelForExperience(this.experience[2]);
        int n4 = SkillManager.getLevelForExperience(this.experience[3]);
        int n5 = SkillManager.getLevelForExperience(this.experience[5]);
        int n6 = SkillManager.getLevelForExperience(this.experience[4]);
        int n7 = SkillManager.getLevelForExperience(this.experience[6]);
        double d2 = n2 + n4 + n5 / 2;
        double d3 = (d2 + 1.3 * (1.5 * (double)n7)) / 4.0;
        double d4 = (d2 + 1.3 * (1.5 * (double)n6)) / 4.0;
        double d5 = (d2 + 1.3 * (double)(n + n3)) / 4.0;
        if (d5 >= d4 && d5 >= d3) {
            return (int)d5;
        }
        if (d4 >= d5 && d4 >= d3) {
            return (int)d4;
        }
        return (int)d3;
    }

    public static int getMaxCombatLevel() {
        double d;
        int n = ServerSettings.maxLevel;
        int n2 = ServerSettings.maxLevel;
        int n3 = ServerSettings.maxLevel;
        int n4 = ServerSettings.maxLevel;
        int n5 = ServerSettings.maxLevel;
        int n6 = ServerSettings.maxLevel;
        int n7 = ServerSettings.maxLevel;
        double d2 = n2 + n4 + n5 / 2;
        double d3 = (d2 + 1.3 * (1.5 * (double)n7)) / 4.0;
        double d4 = (d2 + 1.3 * (1.5 * (double)n6)) / 4.0;
        double d5 = (d2 + 1.3 * (double)(n + n3)) / 4.0;
        if (d5 >= d4 && d5 >= d3) {
            return (int)d5;
        }
        if (d4 >= d5 && d4 >= d3) {
            return (int)d4;
        }
        return (int)d3;
    }

    public final int[] getCurrentLevels() {
        return this.currentLevels;
    }

    public final double[] getExperience() {
        return this.experience;
    }

    public final void setCurrentLevel(int n, int n2) {
        this.currentLevels[n] = n2;
    }

    public final boolean tryStartActionDelay(int n) {
        if (System.currentTimeMillis() >= this.actionDelayExpiresAtMillis) {
            this.actionDelayExpiresAtMillis = System.currentTimeMillis() + (long)n;
            return true;
        }
        return false;
    }

    public final boolean tryStartDrinkDelay(int n) {
        if (System.currentTimeMillis() >= this.drinkDelayExpiresAtMillis) {
            this.drinkDelayExpiresAtMillis = System.currentTimeMillis() + 600L;
            return true;
        }
        return false;
    }

    static /* synthetic */ Player getPlayer(SkillManager skillManager) {
        return skillManager.player;
    }

    static /* synthetic */ int[] getCurrentLevels(SkillManager skillManager) {
        return skillManager.currentLevels;
    }
}

