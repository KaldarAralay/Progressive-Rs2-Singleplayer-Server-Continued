/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.prayer;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.World;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.skill.prayer.RapidRestoreTask;
import com.rs2.model.skill.prayer.RetributionPrayerTask;
import com.rs2.model.task.TickTask;

public final class PrayerManager {
    private Player player;
    private static final Object[][] PRAYER_DEFINITIONS = new Object[][]{{0, 83, "Thick Skin", 1, 3, 446, false}, {1, 84, "Burst of Strength", 4, 3, 449, false}, {2, 85, "Clarity of Thought", 7, 3, 436, false}, {3, 86, "Rock Skin", 10, 6, 441, false}, {4, 87, "Superhuman Strength", 13, 6, 434, false}, {5, 88, "Improved Reflexes", 16, 6, 448, false}, {6, 89, "Rapid Restore", 19, 1, 451, false}, {7, 90, "Rapid Heal", 22, 2, 443, false}, {8, 91, "Protect Item", 25, 2, 337, false}, {9, 92, "Steel Skin", 28, 12, 439, false}, {10, 93, "Ultimate Strength", 31, 12, 450, false}, {11, 94, "Incredible Reflexes", 34, 12, 440, false}, {12, 95, "Protect from Magic", 37, 12, 438, false}, {13, 96, "Protect from Range", 40, 12, 444, false}, {14, 97, "Protect from Melee", 43, 12, 433, false}, {15, 98, "Retribution", 46, 3, 1703, true}, {16, 99, "Redemption", 49, 6, 1705, true}, {17, 100, "Smite", 52, 18, 1704, true}};
    private static final int[][] NPC_PRAYER_EXPERIENCE_REWARDS = new int[][]{{3867, 130}, {3868, 182}, {3869, 286}, {3870, 454}, {3871, 480}, {3872, 494}, {3873, 520}, {3874, 584}, {3875, 650}, {3876, 716}, {3877, 754}, {3878, 780}, {3879, 884}, {3880, 936}, {3881, 1040}, {3882, 1104}, {3883, 1170}, {3884, 1300}, {3885, 1560}};
    private TickTask rapidRestoreTask;

    public PrayerManager(Player player) {
        this.player = player;
    }

    /*
     * Enabled aggressive block sorting
     */
    public final void awardNpcPrayerExperience(Npc npc) {
        int n = -1;
        int n2 = 0;
        while (n2 < 19) {
            if (npc.getNpcId() == NPC_PRAYER_EXPERIENCE_REWARDS[n2][0]) {
                n = n2;
                break;
            }
            ++n2;
        }
        if (n != -1) {
            this.player.getSkillManager().addExperience(5, NPC_PRAYER_EXPERIENCE_REWARDS[n][1]);
        }
    }

    public final void drainPrayerPoints() {
        if (this.player.prayerDrainRate > 0) {
            this.player.prayerDrainAccumulator += this.player.prayerDrainRate;
            int n = 0;
            while (this.player.prayerDrainAccumulator > this.player.getPrayerDrainThreshold()) {
                this.player.prayerDrainAccumulator -= this.player.getPrayerDrainThreshold();
                ++n;
            }
            int n2 = n;
            PrayerManager prayerManager = this;
            int[] nArray = prayerManager.player.getSkillManager().getCurrentLevels();
            nArray[5] = nArray[5] - n2;
            if (prayerManager.player.getSkillManager().getCurrentLevels()[5] <= 0) {
                prayerManager.player.getSkillManager().getCurrentLevels()[5] = 0;
                prayerManager.player.getSkillManager().refreshSkill(5);
                prayerManager.deactivateAll();
                Player player = prayerManager.player;
                player.packetSender.sendGameMessage("You have ran out of prayer points; you must recharge at an altar.");
                player = prayerManager.player;
                player.packetSender.sendSoundEffect(437, 1, 0);
                return;
            }
            prayerManager.player.getSkillManager().refreshSkill(5);
        }
    }

    private static int getPrayerConfigId(Integer n, int n2) {
        n2 = 0;
        Object[][] objectArray = PRAYER_DEFINITIONS;
        int n3 = 0;
        while (n3 < 18) {
            Object[] objectArray2 = objectArray[n3];
            if (objectArray2[0] == n) {
                n2 = (Integer)objectArray2[1];
            }
            ++n3;
        }
        return n2;
    }

    public final void togglePrayer(Integer n) {
        if (this.player.isDead()) {
            return;
        }
        int n2 = 0;
        String string = null;
        int n3 = 0;
        int n4 = -1;
        boolean bl = false;
        Object[][] objectArray = PRAYER_DEFINITIONS;
        int n5 = 0;
        while (n5 < 18) {
            Object[] objectArray2 = objectArray[n5];
            if (objectArray2[0] == n) {
                n2 = (Integer)objectArray2[1];
                string = (String)objectArray2[2];
                n3 = (Integer)objectArray2[3];
                n4 = (Integer)objectArray2[5];
                bl = (Boolean)objectArray2[6];
            }
            ++n5;
        }
        if (bl) {
            if (this.player.isMember()) {
                if (ServerSettings.freeToPlayWorld) {
                    this.player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    Player player = this.player;
                    player.packetSender.sendConfig(n2, 0);
                    this.player.getDialogueManager().finishDialogue();
                    return;
                }
            } else {
                this.player.packetSender.sendGameMessage("You need a members account to access members content.");
                Player player = this.player;
                player.packetSender.sendConfig(n2, 0);
                this.player.getDialogueManager().finishDialogue();
                return;
            }
        }
        if (this.player.getSkillManager().getBaseLevel(5) < n3) {
            Player player = this.player;
            player.packetSender.sendConfig(n2, 0);
            this.player.getDialogueManager().finishDialogue();
            this.player.getDialogueManager().showOneLineStatement("You need a prayer level of at least " + n3 + " to use " + string + ".");
            player = this.player;
            player.packetSender.sendGameMessage("You need a prayer level of at least " + n3 + " to use " + string + ".");
            player = this.player;
            player.packetSender.sendSoundEffect(447, 1, 0);
            return;
        }
        if (DuelRule.NO_PRAYER.isEnabledFor(this.player)) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Usage of prayers have been disabled during this fight!");
            player = this.player;
            player.packetSender.sendConfig(n2, 0);
            return;
        }
        if (this.player.getSkillManager().getCurrentLevels()[5] <= 0) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You have run out of prayer points; recharge your prayer points at an altar");
            player = this.player;
            player.packetSender.sendSoundEffect(437, 1, 0);
            this.deactivateAll();
            return;
        }
        int n6 = -1;
        n5 = 0;
        if ((n == 12 || n == 13 || n == 14) && this.player.getProtectionPrayerDisabledUntil() > System.currentTimeMillis()) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Your protection prayers are temporarily disabled.");
            player = this.player;
            player.packetSender.sendConfig(PrayerManager.getPrayerConfigId(n, 1), 0);
            return;
        }
        switch (n) {
            case 12: {
                n6 = 2;
                n5 = 1;
                break;
            }
            case 13: {
                n6 = 1;
                n5 = 1;
                break;
            }
            case 14: {
                n6 = 0;
                n5 = 1;
                break;
            }
            case 15: {
                n6 = 3;
                n5 = 1;
                break;
            }
            case 16: {
                n6 = 5;
                n5 = 1;
                break;
            }
            case 17: {
                n6 = 4;
                n5 = 1;
            }
        }
        if (n5 != 0) {
            this.player.setPrayerHeadIcon(!this.player.getActivePrayers()[n] ? n6 : -1);
        }
        if (!this.player.getActivePrayers()[n] && n4 != -1) {
            Player player = this.player;
            player.packetSender.sendSoundEffect(n4, 1, 0);
        }
        if (this.player.getActivePrayers()[n]) {
            Player player = this.player;
            player.packetSender.sendSoundEffect(435, 1, 0);
        }
        this.player.getActivePrayers()[n.intValue()] = !this.player.getActivePrayers()[n];
        Player player = this.player;
        player.packetSender.sendConfig(n2, this.player.getActivePrayers()[n] ? 1 : 0);
        this.deactivateConflictingPrayers(n);
        this.updatePrayerDrain();
        n2 = this.player.getActivePrayers()[n] ? 0 : 1;
        Player player2 = this.player;
        this.player.setAppearanceUpdateRequired(true);
        if (n == 6) {
            this.updateRapidRestoreTask();
        }
        if (n == 7) {
            this.player.getSkillManager().setRapidHealRestoreDelay(this.player.getActivePrayers()[n]);
        }
    }

    private void updatePrayerDrain() {
        this.player.prayerDrainRate = 0;
        int n = 0;
        while (n < this.player.getActivePrayers().length) {
            if (this.player.getActivePrayers()[n]) {
                Object[] objectArray = PRAYER_DEFINITIONS[n];
                int n2 = (Integer)objectArray[4];
                this.player.prayerDrainRate += n2;
            }
            ++n;
        }
    }

    private void deactivateConflictingPrayers(int n) {
        int[] nArray = new int[]{};
        switch (n) {
            case 0: {
                nArray = new int[]{3, 9};
                break;
            }
            case 3: {
                int[] nArray2 = new int[2];
                nArray2[1] = 9;
                nArray = nArray2;
                break;
            }
            case 9: {
                int[] nArray3 = new int[2];
                nArray3[1] = 3;
                nArray = nArray3;
                break;
            }
            case 2: {
                nArray = new int[]{5, 11};
                break;
            }
            case 5: {
                nArray = new int[]{2, 11};
                break;
            }
            case 11: {
                nArray = new int[]{5, 2};
                break;
            }
            case 1: {
                nArray = new int[]{4, 10};
                break;
            }
            case 4: {
                nArray = new int[]{1, 10};
                break;
            }
            case 10: {
                nArray = new int[]{4, 1};
                break;
            }
            case 12: {
                nArray = new int[]{16, 17, 15, 13, 14};
                break;
            }
            case 13: {
                nArray = new int[]{16, 17, 15, 12, 14};
                break;
            }
            case 14: {
                nArray = new int[]{16, 17, 15, 13, 12};
                break;
            }
            case 15: {
                nArray = new int[]{16, 17, 14, 13, 12};
                break;
            }
            case 16: {
                nArray = new int[]{15, 17, 14, 13, 12};
                break;
            }
            case 17: {
                nArray = new int[]{16, 15, 14, 13, 12};
            }
        }
        int[] nArray4 = nArray;
        int n2 = nArray.length;
        int n3 = 0;
        while (n3 < n2) {
            int n4 = nArray4[n3];
            if (n4 != n) {
                this.player.getActivePrayers()[n4] = false;
                Player player = this.player;
                player.packetSender.sendConfig(PrayerManager.getPrayerConfigId(n4, 1), 0);
            }
            ++n3;
        }
    }

    public final void deactivatePrayer(int n) {
        if (this.player.getActivePrayers()[n]) {
            this.player.getActivePrayers()[n] = false;
            Player player = this.player;
            player.packetSender.sendConfig(PrayerManager.getPrayerConfigId(n, 1), 0);
            if (n == 12 || n == 13 || n == 14 || n == 15 || n == 16 || n == 17) {
                this.player.setPrayerHeadIcon(-1);
                this.player.setAppearanceUpdateRequired(true);
            }
        }
        this.updatePrayerDrain();
    }

    /*
     * Enabled aggressive block sorting
     */
    public final void deactivateAll() {
        if (this.player.getActivePrayers()[7]) {
            this.player.getSkillManager().setRapidHealRestoreDelay(false);
        }
        int n = 0;
        while (n < 18) {
            this.player.getActivePrayers()[n] = false;
            Player player = this.player;
            player.packetSender.sendConfig(PrayerManager.getPrayerConfigId(n, 1), 0);
            ++n;
        }
        this.updateRapidRestoreTask();
        this.player.setPrayerHeadIcon(-1);
        this.player.setAppearanceUpdateRequired(true);
        this.updatePrayerDrain();
    }

    private void updateRapidRestoreTask() {
        Object object = this.rapidRestoreTask;
        if (this.player.getActivePrayers()[6]) {
            object = this;
            if (((PrayerManager)object).rapidRestoreTask == null || !((PrayerManager)object).rapidRestoreTask.isActive()) {
                ((PrayerManager)object).rapidRestoreTask = new RapidRestoreTask((PrayerManager)object, 100);
                World.getTaskScheduler().schedule(((PrayerManager)object).rapidRestoreTask);
            }
            return;
        }
        if (object != null) {
            ((TickTask)object).stop();
            ((TickTask)object).setIntervalTicks(100);
            ((TickTask)object).setRemainingTicks(100);
        }
    }

    public final boolean isRapidRestoreActive() {
        return this.player.getSkillManager().getCurrentLevels()[5] > 0 && this.player.getActivePrayers()[6];
    }

    public final boolean handleButtonClick(int n) {
        switch (n) {
            case 5609: {
                this.togglePrayer(0);
                return true;
            }
            case 5610: {
                this.togglePrayer(1);
                return true;
            }
            case 5611: {
                this.togglePrayer(2);
                return true;
            }
            case 5612: {
                this.togglePrayer(3);
                return true;
            }
            case 5613: {
                this.togglePrayer(4);
                return true;
            }
            case 5614: {
                this.togglePrayer(5);
                return true;
            }
            case 5615: {
                this.togglePrayer(6);
                return true;
            }
            case 5616: {
                this.togglePrayer(7);
                return true;
            }
            case 5617: {
                if (this.player.gameMode == 2) {
                    Player player = this.player;
                    player.packetSender.sendGameMessage("In ultimate ironman mode you cannot use protect item.");
                } else {
                    this.togglePrayer(8);
                }
                return true;
            }
            case 5618: {
                this.togglePrayer(9);
                return true;
            }
            case 5619: {
                this.togglePrayer(10);
                return true;
            }
            case 5620: {
                this.togglePrayer(11);
                return true;
            }
            case 5621: {
                this.togglePrayer(12);
                return true;
            }
            case 5622: {
                this.togglePrayer(13);
                return true;
            }
            case 5623: {
                this.togglePrayer(14);
                return true;
            }
            case 683: {
                this.togglePrayer(15);
                return true;
            }
            case 684: {
                this.togglePrayer(16);
                Player player = this.player;
                if (player.getSkillManager().getCurrentLevels()[3] <= (int)((double)player.getSkillManager().getBaseLevel(3) * 0.1)) {
                    int[] nArray = player.getSkillManager().getCurrentLevels();
                    nArray[3] = nArray[3] + (int)((double)player.getSkillManager().getBaseLevel(5) * 0.25);
                    player.getUpdateState().setGraphic(436, 0);
                    player.getSkillManager().refreshSkill(5);
                    player.getSkillManager().setCurrentLevel(5, 0);
                    player.getSkillManager().refreshSkill(3);
                }
                return true;
            }
            case 685: {
                this.togglePrayer(17);
                return true;
            }
        }
        return false;
    }

    public static void rechargePrayerAtAltar(Player player) {
        if (player.getSkillManager().getCurrentLevels()[5] < player.getSkillManager().getBaseLevel(5)) {
            player.getUpdateState().setAnimation(645);
            player.getSkillManager().setCurrentLevel(5, player.getSkillManager().getBaseLevel(5));
            player.getSkillManager().refreshSkill(5);
            Player player2 = player;
            player2.packetSender.sendGameMessage("You recharge your prayer at the altar.");
            player2 = player;
            player2.packetSender.sendSoundEffect(442, 1, 0);
        } else {
            Player player3 = player;
            player3.packetSender.sendGameMessage("You already have full prayer!");
        }
        if (player.botEnabled) {
            player.botTaskReturnToBankRequested = true;
            player.currentBotTask.startWalkToBank(player);
        }
    }

    public static void rechargePrayerWithBoost(Player player) {
        if (player.getSkillManager().getCurrentLevels()[5] < player.getSkillManager().getBaseLevel(5) + 2) {
            player.getUpdateState().setAnimation(645);
            player.getSkillManager().setCurrentLevel(5, player.getSkillManager().getBaseLevel(5) + 2);
            player.getSkillManager().refreshSkill(5);
            Player player2 = player;
            player2.packetSender.sendGameMessage("You recharge your prayer at the altar.");
            player2 = player;
            player2.packetSender.sendGameMessage("You recieve a temporary prayer boost.");
            player2 = player;
            player2.packetSender.sendSoundEffect(442, 1, 0);
        } else {
            Player player3 = player;
            player3.packetSender.sendGameMessage("You already have full prayer!");
        }
        if (player.botEnabled) {
            player.botTaskReturnToBankRequested = true;
            player.currentBotTask.startWalkToBank(player);
        }
    }

    public static void triggerRetribution(Entity object, Entity entity) {
        Player player = (Player)object;
        boolean bl = ((Entity)object).isInMultiCombatArea();
        int n = player.getSkillManager().getBaseLevel(5) / 4;
        HitDefinition hitDefinition = new HitDefinition(null, HitType.NORMAL, n).enableRandomDamage().setAlwaysHits(true);
        World.getTaskScheduler().schedule(new RetributionPrayerTask(3, player, bl, (Entity)object, entity, hitDefinition));
    }

    public static void triggerRedemption(Player player, Entity entity, int n) {
        int n2 = (int)Math.floor((double)player.getSkillManager().getBaseLevel(5) / 4.0);
        if ((n += n2) > entity.getMaxHitpoints()) {
            entity.getMaxHitpoints();
        }
        player.getUpdateState().setGraphic(436, 0);
        player.getSkillManager().setCurrentLevel(5, 0);
        player.getPrayerManager().deactivateAll();
    }

    public static void drainPrayerForSmite(Player player, int n) {
        n = player.getSkillManager().getCurrentLevels()[5] - (int)Math.floor(n / 4);
        if (n < 0) {
            n = 0;
        }
        player.getSkillManager().setCurrentLevel(5, n);
        player.getSkillManager().refreshSkill(5);
    }

    static /* synthetic */ Player getPlayer(PrayerManager prayerManager) {
        return prayerManager.player;
    }
}

