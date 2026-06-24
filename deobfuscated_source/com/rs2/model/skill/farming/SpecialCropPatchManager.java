/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.SpecialCropClearingTask;
import com.rs2.model.skill.farming.SpecialCropCompostTask;
import com.rs2.model.skill.farming.SpecialCropCureTask;
import com.rs2.model.skill.farming.SpecialCropDefinition;
import com.rs2.model.skill.farming.SpecialCropGrowthDefinition;
import com.rs2.model.skill.farming.SpecialCropHarvestTask;
import com.rs2.model.skill.farming.SpecialCropInspectTask;
import com.rs2.model.skill.farming.SpecialCropPatch;
import com.rs2.model.skill.farming.SpecialCropPlantingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class SpecialCropPatchManager {
    private Player player;
    public int[] growthStages = new int[4];
    public int[] cropIds = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};
    public boolean[] f = new boolean[4];

    public SpecialCropPatchManager(Player player) {
        this.player = player;
    }

    public final void refreshConfig() {
        int[] nArray = new int[this.growthStages.length];
        int n = 0;
        while (n < this.growthStages.length) {
            int n2;
            int n3 = this.patchStates[n];
            int n4 = this.cropIds[n];
            int n5 = this.growthStages[n];
            Object object = this;
            object = SpecialCropDefinition.forSeedId(n4);
            switch (n5) {
                case 0: {
                    n2 = 0;
                    break;
                }
                case 1: {
                    n2 = 1;
                    break;
                }
                case 2: {
                    n2 = 2;
                    break;
                }
                case 3: {
                    n2 = 3;
                    break;
                }
                default: {
                    n2 = object == null ? -1 : (SpecialCropPatchManager.getConfigStageForPatchState(n3, (SpecialCropDefinition)((Object)object), n5) == 3 ? ((SpecialCropDefinition)((Object)object)).getHealthCheckConfigStage() : SpecialCropPatchManager.getConfigStageForPatchState(n3, (SpecialCropDefinition)((Object)object), n5));
                }
            }
            nArray[n] = n2;
            ++n;
        }
        int n6 = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        Player player = this.player;
        player.packetSender.sendConfig(512, n6);
    }

    private static int getConfigStageForPatchState(int n, SpecialCropDefinition specialCropDefinition, int n2) {
        n2 -= 4;
        n2 = specialCropDefinition.getConfigStartStage() + n2;
        switch (n) {
            case 0: {
                return n2;
            }
            case 1: {
                return n2 + specialCropDefinition.getDiseasedConfigOffset();
            }
            case 2: {
                return n2 + specialCropDefinition.getDeadConfigOffset();
            }
            case 3: {
                return specialCropDefinition.getHealthCheckConfigStage();
            }
        }
        return -1;
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.cropIds.length) {
            long l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
            if (l >= 5L) {
                if (this.growthStages[n] > 0 && this.growthStages[n] <= 3) {
                    int n2 = (int)(l / 5L);
                    int n3 = 0;
                    while (n3 < n2) {
                        if (this.growthStages[n] != 0) {
                            int n4 = n;
                            this.growthStages[n4] = this.growthStages[n4] - 1;
                            this.lastUpdateTicks[n] = Server.getElapsedMinutes();
                            ++n3;
                            continue;
                        }
                        break;
                    }
                } else {
                    SpecialCropDefinition specialCropDefinition = SpecialCropDefinition.forSeedId(this.cropIds[n]);
                    if (specialCropDefinition != null && !this.shouldStopGrowthCycle(n)) {
                        int n5 = (int)(l / (long)specialCropDefinition.getGrowthCycleTicks());
                        int n6 = this.growthStages[n] - 4;
                        if ((n5 -= n6) > 0) {
                            n6 = 0;
                            while (n6 < n5) {
                                if (this.growthStages[n] == 4) {
                                    int n7 = n;
                                    this.growthStages[n7] = this.growthStages[n7] + 1;
                                } else {
                                    int n8 = n;
                                    SpecialCropPatchManager specialCropPatchManager = this;
                                    if (specialCropPatchManager.patchStates[n8] == 1 && GameUtil.randomInt(2) == 0) {
                                        specialCropPatchManager.patchStates[n8] = 2;
                                    }
                                    if (specialCropPatchManager.patchStates[n8] != 1 && specialCropPatchManager.patchStates[n8] != 2) {
                                        SpecialCropDefinition specialCropDefinition2;
                                        if (specialCropPatchManager.patchStates[n8] == 5 && specialCropPatchManager.growthStages[n8] != 2) {
                                            specialCropPatchManager.patchStates[n8] = 0;
                                        }
                                        if (specialCropPatchManager.patchStates[n8] == 0 && specialCropPatchManager.growthStages[n8] >= 4 && !specialCropPatchManager.f[n8] && (specialCropDefinition2 = SpecialCropDefinition.forSeedId(specialCropPatchManager.cropIds[n8])) != null) {
                                            double d = specialCropPatchManager.diseaseChanceMultipliers[n8] * specialCropDefinition2.getDiseaseChance();
                                            double d2 = d * 100.0;
                                            int n9 = (int)d2;
                                            if (GameUtil.randomInclusive(100) <= n9 && ServerSettings.diseasingEnabled) {
                                                specialCropPatchManager.patchStates[n8] = 1;
                                            }
                                        }
                                    }
                                    if (this.patchStates[n] == 2) break;
                                    if (this.patchStates[n] != 1) {
                                        int n10 = n;
                                        this.growthStages[n10] = this.growthStages[n10] + 1;
                                    }
                                    if (this.shouldStopGrowthCycle(n)) break;
                                    if (this.growthStages[n] <= specialCropDefinition.getGrowthStageCount() + (specialCropDefinition == SpecialCropDefinition.BELLADONNA ? 3 : -2) && this.growthStages[n] == specialCropDefinition.getGrowthStageCount() - 2 && specialCropDefinition.getHealthCheckConfigStage() != -1) {
                                        this.growthStages[n] = specialCropDefinition.getGrowthStageCount() + 4;
                                        this.patchStates[n] = 3;
                                        break;
                                    }
                                }
                                ++n6;
                            }
                        }
                    }
                }
            }
            ++n;
        }
        this.refreshConfig();
    }

    private boolean shouldStopGrowthCycle(int n) {
        return this.lastUpdateTicks[n] == 0L || this.patchStates[n] == 2 || this.patchStates[n] == 3;
    }

    public final void recalculateRegrowthStage(int n) {
        SpecialCropDefinition specialCropDefinition = SpecialCropDefinition.forSeedId(this.cropIds[n]);
        if (specialCropDefinition == null) {
            return;
        }
        long l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
        int n2 = (int)(l / (long)specialCropDefinition.getGrowthCycleTicks());
        this.growthStages[n] = n2 + 4;
        this.refreshConfig();
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        SpecialCropPatch specialCropPatch = SpecialCropPatch.forPosition(new Position(n, n2));
        if (specialCropPatch == null || n3 != 5341 && n3 != 952) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!this.player.isMember()) {
            this.player.packetSender.sendGameMessage("You need a members account to access members content.");
            return true;
        }
        if (ServerSettings.freeToPlayWorld) {
            this.player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return true;
        }
        if (this.growthStages[specialCropPatch.getIndex()] == 3) {
            return true;
        }
        if (this.growthStages[specialCropPatch.getIndex()] <= 3) {
            if (!this.player.getInventoryManager().getContainer().containsItem(5341)) {
                this.player.getDialogueManager().showOneLineStatement("You need a rake to clear this path.");
                return true;
            }
            n3 = 2273;
            n2 = 1323;
            n4 = 5;
        } else {
            if (!this.player.getInventoryManager().getContainer().containsItem(952)) {
                this.player.getDialogueManager().showOneLineStatement("You need a spade to clear this path.");
                return true;
            }
            n3 = 830;
            n2 = 232;
            n4 = 3;
        }
        this.player.setActionLocked(true);
        Player player = this.player;
        player.packetSender.sendSoundEffect(n2, 1, 0);
        this.player.getUpdateState().setAnimation(n3);
        CycleEventHandler.getInstance().schedule(this.player, new SpecialCropClearingTask(this, n3, specialCropPatch), n4);
        return true;
    }

    public final boolean plantSeed(int n, int n2, int n3) {
        SpecialCropPatch specialCropPatch = SpecialCropPatch.forPosition(new Position(n, n2));
        SpecialCropDefinition specialCropDefinition = SpecialCropDefinition.forSeedId(n3);
        if (specialCropPatch == null || specialCropDefinition == null || specialCropPatch.getObjectId() != n3) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[specialCropPatch.getIndex()] != 3) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't plant a seed here.");
            return false;
        }
        if (specialCropDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + specialCropDefinition.getRequiredLevel() + " to plant this seed.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5343)) {
            this.player.getDialogueManager().showOneLineStatement("You need a seed dibber to plant seed here.");
            return true;
        }
        if (this.player.getInventoryManager().getItemAmount(specialCropDefinition.getSeedId()) < specialCropDefinition.getSeedAmount()) {
            this.player.getDialogueManager().showOneLineStatement("You need atleast " + specialCropDefinition.getSeedAmount() + " seeds to plant here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2291);
        Player player = this.player;
        player.packetSender.sendSoundEffect(1321, 1, 0);
        this.growthStages[specialCropPatch.getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3, specialCropDefinition.getSeedAmount()));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new SpecialCropPlantingTask(this, specialCropPatch, n3, specialCropDefinition), 3);
        return true;
    }

    public final boolean harvestPatch(int n, int n2) {
        Object object = SpecialCropPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        SpecialCropDefinition specialCropDefinition = SpecialCropDefinition.forSeedId(this.cropIds[((SpecialCropPatch)object).getIndex()]);
        if (specialCropDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("Not enough space in your inventory.");
            return true;
        }
        this.player.setActionLocked(true);
        this.player.getUpdateState().setAnimation(832);
        int n3 = this.player.nextActionSequence();
        this.player.setActiveCycleEvent(new SpecialCropHarvestTask(this, n3, (SpecialCropPatch)((Object)object), specialCropDefinition));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        SpecialCropPatch specialCropPatch = SpecialCropPatch.forPosition(new Position(n, n2));
        if (specialCropPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[specialCropPatch.getIndex()] != 3 || this.patchStates[specialCropPatch.getIndex()] == 5) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This patch doesn't need compost.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(1925));
        Player player = this.player;
        player.packetSender.sendGameMessage("You pour some " + (n3 == 6034 ? "super" : "") + "compost on the patch.");
        this.player.getUpdateState().setAnimation(2283);
        this.player.getSkillManager().addExperience(19, n3 == 6034 ? 26.0 : 18.0);
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new SpecialCropCompostTask(this, specialCropPatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        SpecialCropPatch specialCropPatch = SpecialCropPatch.forPosition(new Position(n, n2));
        if (specialCropPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        SpecialCropGrowthDefinition specialCropGrowthDefinition = SpecialCropGrowthDefinition.forCropId(this.cropIds[specialCropPatch.getIndex()]);
        Object object = SpecialCropDefinition.forSeedId(this.cropIds[specialCropPatch.getIndex()]);
        if (this.patchStates[specialCropPatch.getIndex()] == 1) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[specialCropPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[specialCropPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant has fully grown. You can check it's health", "to gain some farming experiences.");
            return true;
        }
        if (this.growthStages[specialCropPatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[specialCropPatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[specialCropPatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[specialCropPatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (specialCropGrowthDefinition != null && object != null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new SpecialCropInspectTask(this, specialCropPatch, specialCropGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        Object object = SpecialCropPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(8);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        Object object = SpecialCropPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        SpecialCropDefinition specialCropDefinition = SpecialCropDefinition.forSeedId(this.cropIds[((SpecialCropPatch)object).getIndex()]);
        if (specialCropDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((SpecialCropPatch)object).getIndex()] != 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("special2", ((SpecialCropPatch)object).getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            Object object = SpecialCropDefinition.forSeedId(this.cropIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(((SpecialCropDefinition)object).getGrowthCycleTicks() * n);
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You succesfully resurrected the crop.");
        } else {
            this.resetPatch(this.player.pendingCropResurrectionPatchIndex);
            this.growthStages[this.player.pendingCropResurrectionPatchIndex] = 3;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes();
            Player player = this.player;
            player.packetSender.sendGameMessage("You failed to resurrect the crop.");
        }
        this.refreshConfig();
        return true;
    }

    public final boolean curePatch(int n, int n2, int n3) {
        Object object = SpecialCropPatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 6036) {
            return false;
        }
        SpecialCropDefinition specialCropDefinition = SpecialCropDefinition.forSeedId(this.cropIds[((SpecialCropPatch)object).getIndex()]);
        if (specialCropDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((SpecialCropPatch)object).getIndex()] != 1) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be cured.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(229));
        this.player.getUpdateState().setAnimation(2288);
        this.player.setActionLocked(true);
        this.patchStates[((SpecialCropPatch)object).getIndex()] = 0;
        CycleEventHandler.getInstance().schedule(this.player, new SpecialCropCureTask(this), 7);
        return true;
    }

    private void resetPatch(int n) {
        this.cropIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.f[n] = false;
    }

    static /* synthetic */ Player getPlayer(SpecialCropPatchManager specialCropPatchManager) {
        return specialCropPatchManager.player;
    }

    static /* synthetic */ void resetPatch(SpecialCropPatchManager specialCropPatchManager, int n) {
        specialCropPatchManager.resetPatch(n);
    }
}

