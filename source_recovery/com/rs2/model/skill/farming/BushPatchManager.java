/*
 * Source recovery overlay for CFR control-flow damage.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class BushPatchManager {
    private Player player;
    public int[] growthStages = new int[4];
    public int[] cropIds = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};
    public boolean[] f = new boolean[4];
    public boolean[] protectionFlags = new boolean[4];

    public BushPatchManager(Player player) {
        this.player = player;
    }

    public final void refreshConfig() {
        int[] configStages = new int[this.growthStages.length];
        int index = 0;
        while (index < this.growthStages.length) {
            int patchState = this.patchStates[index];
            int cropId = this.cropIds[index];
            int growthStage = this.growthStages[index];
            BushDefinition bushDefinition = BushDefinition.forSeedId(cropId);
            int configStage;
            switch (growthStage) {
                case 0: {
                    configStage = 0;
                    break;
                }
                case 1: {
                    configStage = 1;
                    break;
                }
                case 2: {
                    configStage = 2;
                    break;
                }
                case 3: {
                    configStage = 3;
                    break;
                }
                default: {
                    if (bushDefinition == null) {
                        configStage = -1;
                        break;
                    }
                    int stateConfig = BushPatchManager.getPatchStateConfigValue(patchState);
                    if (stateConfig == 3) {
                        configStage = (stateConfig << 6) + bushDefinition.getHealthCheckConfigStage();
                    } else if (cropId == 5106 && stateConfig == 1) {
                        configStage = bushDefinition.getConfigStartStage() + growthStage - 4 + 12;
                    } else if (cropId == 5106 && stateConfig == 2) {
                        configStage = bushDefinition.getConfigStartStage() + growthStage - 4 + 20;
                    } else {
                        configStage = (stateConfig << 6) + bushDefinition.getConfigStartStage() + (growthStage - 4) + (stateConfig == 2 ? -1 : 0);
                    }
                }
            }
            configStages[index] = configStage;
            ++index;
        }
        int packedConfig = (configStages[0] << 16) + (configStages[1] << 8 << 16) + configStages[2] + (configStages[3] << 8);
        this.player.packetSender.sendConfig(509, packedConfig);
    }

    private static int getPatchStateConfigValue(int n) {
        switch (n) {
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 3;
            }
        }
        return -1;
    }

    public final void processGrowth() {
        int index = 0;
        while (index < this.cropIds.length) {
            long elapsedMinutes = Server.getElapsedMinutes() - this.lastUpdateTicks[index];
            if (elapsedMinutes >= 5L) {
                if (this.growthStages[index] > 0 && this.growthStages[index] <= 3) {
                    int cycles = (int)(elapsedMinutes / 5L);
                    int cycle = 0;
                    while (cycle < cycles) {
                        if (this.growthStages[index] == 0) {
                            break;
                        }
                        int n = index;
                        this.growthStages[n] = this.growthStages[n] - 1;
                        this.lastUpdateTicks[index] = Server.getElapsedMinutes();
                        ++cycle;
                    }
                } else {
                    BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[index]);
                    if (bushDefinition != null && !this.shouldStopGrowthCycle(index)) {
                        int cycles = (int)(elapsedMinutes / (long)bushDefinition.getGrowthCycleTicks());
                        int existingGrowthCycles = this.growthStages[index] - 4;
                        if ((cycles -= existingGrowthCycles) > 0) {
                            int cycle = 0;
                            while (cycle < cycles) {
                                if (this.growthStages[index] == 4) {
                                    int n = index;
                                    this.growthStages[n] = this.growthStages[n] + 1;
                                } else {
                                    this.processGrowthState(index);
                                    if (this.patchStates[index] == 2) {
                                        break;
                                    }
                                    if (this.patchStates[index] != 1) {
                                        int n = index;
                                        this.growthStages[n] = this.growthStages[n] + 1;
                                    }
                                    if (this.shouldStopGrowthCycle(index)) {
                                        break;
                                    }
                                    if (this.growthStages[index] == bushDefinition.getGrowthStageCount() - 1) {
                                        this.growthStages[index] = bushDefinition.getGrowthStageCount() + 4;
                                        this.patchStates[index] = 3;
                                        break;
                                    }
                                }
                                ++cycle;
                            }
                        }
                    }
                }
            }
            ++index;
        }
        this.refreshConfig();
    }

    private void processGrowthState(int index) {
        if (this.patchStates[index] == 1) {
            if (this.protectionFlags[index]) {
                this.patchStates[index] = 0;
                BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[index]);
                if (bushDefinition != null) {
                    int n = index;
                    this.lastUpdateTicks[n] = this.lastUpdateTicks[n] + (long)bushDefinition.getGrowthCycleTicks();
                }
            } else if (GameUtil.randomInt(2) == 0) {
                this.patchStates[index] = 2;
            }
        }
        if (this.patchStates[index] == 1 || this.patchStates[index] == 2) {
            return;
        }
        if (this.patchStates[index] == 5 && this.growthStages[index] != 2) {
            this.patchStates[index] = 0;
        }
        if (this.patchStates[index] == 0 && this.growthStages[index] >= 4 && !this.f[index]) {
            BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[index]);
            if (bushDefinition != null) {
                double diseaseChance = this.diseaseChanceMultipliers[index] * bushDefinition.getDiseaseChance();
                int percentChance = (int)(diseaseChance * 100.0);
                if (GameUtil.randomInclusive(100) <= percentChance && ServerSettings.diseasingEnabled) {
                    this.patchStates[index] = 1;
                }
            }
        }
    }

    private boolean shouldStopGrowthCycle(int n) {
        return this.lastUpdateTicks[n] == 0L || this.patchStates[n] == 2 || this.patchStates[n] == 3;
    }

    public final void recalculateRegrowthStage(int n) {
        BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[n]);
        if (bushDefinition == null) {
            return;
        }
        long elapsedMinutes = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
        int cycles = (int)(elapsedMinutes / (long)bushDefinition.getGrowthCycleTicks());
        this.growthStages[n] = cycles + 4;
        this.refreshConfig();
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int animationId;
        int soundId;
        int delayTicks;
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        if (bushPatch == null || n3 != 5341 && n3 != 952) {
            return false;
        }
        if (this.growthStages[bushPatch.getIndex()] == 3) {
            return true;
        }
        if (!ServerSettings.farmingEnabled) {
            this.player.packetSender.sendGameMessage("This skill is currently disabled.");
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
        if (this.growthStages[bushPatch.getIndex()] <= 3) {
            if (!this.player.getInventoryManager().getContainer().containsItem(5341)) {
                this.player.getDialogueManager().showOneLineStatement("You need a rake to clear this path.");
                return true;
            }
            animationId = 2273;
            soundId = 1323;
            delayTicks = 5;
        } else {
            if (!this.player.getInventoryManager().getContainer().containsItem(952)) {
                this.player.getDialogueManager().showOneLineStatement("You need a spade to clear this path.");
                return true;
            }
            animationId = 830;
            soundId = 232;
            delayTicks = 3;
        }
        this.player.setActionLocked(true);
        this.player.packetSender.sendSoundEffect(soundId, 1, 0);
        this.player.getUpdateState().setAnimation(animationId);
        CycleEventHandler.getInstance().schedule(this.player, new BushClearingTask(this, animationId, bushPatch), delayTicks);
        return true;
    }

    public final boolean plantSeed(int n, int n2, int n3) {
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        BushDefinition bushDefinition = BushDefinition.forSeedId(n3);
        if (bushPatch == null || bushDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            this.player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[bushPatch.getIndex()] != 3) {
            this.player.packetSender.sendGameMessage("You can't plant a seed here.");
            return false;
        }
        if (bushDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + bushDefinition.getRequiredLevel() + " to plant this seed.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5343)) {
            this.player.getDialogueManager().showOneLineStatement("You need a seed dibber to plant seed here.");
            return true;
        }
        if (this.player.getInventoryManager().getItemAmount(bushDefinition.getSeedId()) < bushDefinition.getSeedAmount()) {
            this.player.getDialogueManager().showOneLineStatement("You need atleast " + bushDefinition.getSeedAmount() + " seeds to plant here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2291);
        this.player.packetSender.sendSoundEffect(1321, 1, 0);
        this.growthStages[bushPatch.getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3, bushDefinition.getSeedAmount()));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new BushPlantingTask(this, bushPatch, n3, bushDefinition), 3);
        return true;
    }

    public final boolean harvestPatch(int n, int n2) {
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        if (bushPatch == null) {
            return false;
        }
        BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[bushPatch.getIndex()]);
        if (bushDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            this.player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            this.player.packetSender.sendGameMessage("Not enough space in your inventory.");
            return true;
        }
        this.player.getUpdateState().setAnimation(832);
        int actionSequence = this.player.nextActionSequence();
        this.player.setActiveCycleEvent(new BushHarvestTask(this, actionSequence, bushPatch, bushDefinition));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        if (bushPatch == null) {
            return false;
        }
        if (this.growthStages[bushPatch.getIndex()] != 3 || this.patchStates[bushPatch.getIndex()] == 5) {
            this.player.packetSender.sendGameMessage("This patch doesn't need compost.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(1925));
        this.player.packetSender.sendGameMessage("You pour some " + (n3 == 6034 ? "super" : "") + "compost on the patch.");
        this.player.getUpdateState().setAnimation(2283);
        this.player.getSkillManager().addExperience(19, n3 == 6034 ? 26.0 : 18.0);
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new BushCompostTask(this, bushPatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        if (bushPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            this.player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        BushGrowthDefinition bushGrowthDefinition = BushGrowthDefinition.forCropId(this.cropIds[bushPatch.getIndex()]);
        BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[bushPatch.getIndex()]);
        if (this.patchStates[bushPatch.getIndex()] == 1) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[bushPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[bushPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant has fully grown. You can check it's health", "to gain some farming experiences.");
            return true;
        }
        if (this.growthStages[bushPatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is a bush patch. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[bushPatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[bushPatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is a bush patch. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[bushPatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is a bush patch. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is a bush patch. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (bushGrowthDefinition != null && bushDefinition != null) {
            this.player.packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new BushInspectTask(this, bushPatch, bushGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        if (bushPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            this.player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(5);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        if (bushPatch == null) {
            return false;
        }
        BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[bushPatch.getIndex()]);
        if (bushDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            this.player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[bushPatch.getIndex()] != 2) {
            this.player.packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("bush", bushPatch.getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean success) {
        if (success) {
            BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int completedCycles = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(bushDefinition.getGrowthCycleTicks() * completedCycles);
            this.player.packetSender.sendGameMessage("You succesfully resurrected the crop.");
        } else {
            this.resetPatch(this.player.pendingCropResurrectionPatchIndex);
            this.growthStages[this.player.pendingCropResurrectionPatchIndex] = 3;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes();
            this.player.packetSender.sendGameMessage("You failed to resurrect the crop.");
        }
        this.refreshConfig();
        return true;
    }

    public final boolean curePatch(int n, int n2, int n3) {
        BushPatch bushPatch = BushPatch.forPosition(new Position(n, n2));
        if (bushPatch == null || n3 != 6036) {
            return false;
        }
        BushDefinition bushDefinition = BushDefinition.forSeedId(this.cropIds[bushPatch.getIndex()]);
        if (bushDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            this.player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[bushPatch.getIndex()] != 1) {
            this.player.packetSender.sendGameMessage("This plant doesn't need to be cured.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(229));
        this.player.getUpdateState().setAnimation(2288);
        this.player.setActionLocked(true);
        this.patchStates[bushPatch.getIndex()] = 0;
        CycleEventHandler.getInstance().schedule(this.player, new BushCureTask(this), 7);
        return true;
    }

    private void resetPatch(int n) {
        this.cropIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.f[n] = false;
        this.protectionFlags[n] = false;
    }

    static /* synthetic */ Player getPlayer(BushPatchManager bushPatchManager) {
        return bushPatchManager.player;
    }

    static /* synthetic */ void resetPatch(BushPatchManager bushPatchManager, int n) {
        bushPatchManager.resetPatch(n);
    }
}
