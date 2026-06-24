/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.FlowerClearingTask;
import com.rs2.model.skill.farming.FlowerCompostTask;
import com.rs2.model.skill.farming.FlowerCureTask;
import com.rs2.model.skill.farming.FlowerDefinition;
import com.rs2.model.skill.farming.FlowerGrowthDefinition;
import com.rs2.model.skill.farming.FlowerHarvestTask;
import com.rs2.model.skill.farming.FlowerInspectTask;
import com.rs2.model.skill.farming.FlowerPatch;
import com.rs2.model.skill.farming.FlowerPlantingTask;
import com.rs2.model.skill.farming.FlowerWateringTask;
import com.rs2.model.skill.farming.ScarecrowPlantingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class FlowerPatchManager {
    private Player player;
    public int[] growthStages = new int[4];
    public int[] cropIds = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};
    public boolean[] fullyGrownFlags = new boolean[4];

    public FlowerPatchManager(Player player) {
        this.player = player;
    }

    public final void refreshConfig() {
        int[] nArray = new int[this.growthStages.length];
        int n = 0;
        while (n < this.growthStages.length) {
            int n2;
            int n3 = n;
            int n4 = this.patchStates[n];
            int n5 = this.cropIds[n];
            int n6 = this.growthStages[n];
            Object object = this;
            if (((FlowerPatchManager)object).cropIds[n3] >= 33 && ((FlowerPatchManager)object).cropIds[n3] <= 36 && ((FlowerPatchManager)object).growthStages[n3] > 3) {
                n2 = 0 + ((FlowerPatchManager)object).cropIds[n3];
            } else {
                object = FlowerDefinition.forSeedId(n5);
                switch (n6) {
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
                        int n7;
                        if (object == null) {
                            n2 = -1;
                            break;
                        }
                        n6 -= 4;
                        n5 = n4;
                        switch (n5) {
                            case 0: {
                                n7 = 0;
                                break;
                            }
                            case 1: {
                                n7 = 1;
                                break;
                            }
                            case 2: {
                                n7 = 2;
                                break;
                            }
                            case 3: {
                                n7 = 3;
                                break;
                            }
                            default: {
                                n7 = -1;
                            }
                        }
                        n2 = (n7 << 6) + ((FlowerDefinition)((Object)object)).getConfigStartStage() + n6;
                    }
                }
            }
            nArray[n] = n2;
            ++n;
        }
        int n8 = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        Player player = this.player;
        player.packetSender.sendConfig(508, n8);
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.cropIds.length) {
            long l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
            if (l >= 5L) {
                int n2;
                if (this.growthStages[n] > 0 && this.growthStages[n] <= 3) {
                    int n3 = (int)(l / 5L);
                    n2 = 0;
                    while (n2 < n3) {
                        if (this.growthStages[n] == 0) break;
                        int n4 = n;
                        this.growthStages[n4] = this.growthStages[n4] - 1;
                        this.lastUpdateTicks[n] = Server.getElapsedMinutes();
                        ++n2;
                    }
                }
                if (this.cropIds[n] > 33 && this.cropIds[n] <= 36) {
                    int n5 = n;
                    this.cropIds[n5] = this.cropIds[n5] - 1;
                } else {
                    Object object = FlowerDefinition.forSeedId(this.cropIds[n]);
                    if (object != null && this.cropIds[n] != 33 && !this.shouldStopGrowthCycle(n)) {
                        n2 = (int)(l / (long)((FlowerDefinition)object).getGrowthCycleTicks());
                        int n6 = this.growthStages[n] - 4;
                        if ((n6 = n2 - n6) > 0) {
                            int n7 = 0;
                            while (n7 < n6) {
                                if (this.growthStages[n] == 4) {
                                    if (this.patchStates[n] == 1) {
                                        this.patchStates[n] = 0;
                                    }
                                    int n8 = n;
                                    this.growthStages[n8] = this.growthStages[n8] + 1;
                                } else {
                                    n2 = n;
                                    object = this;
                                    if (((FlowerPatchManager)object).patchStates[n2] == 2 && GameUtil.randomInt(2) == 0) {
                                        ((FlowerPatchManager)object).patchStates[n2] = 3;
                                    }
                                    if (((FlowerPatchManager)object).patchStates[n2] != 2 && ((FlowerPatchManager)object).patchStates[n2] != 3) {
                                        FlowerDefinition flowerDefinition;
                                        if (((FlowerPatchManager)object).patchStates[n2] == 5 && ((FlowerPatchManager)object).growthStages[n2] != 3) {
                                            ((FlowerPatchManager)object).patchStates[n2] = 0;
                                        }
                                        if (!(((FlowerPatchManager)object).patchStates[n2] != 0 && ((FlowerPatchManager)object).patchStates[n2] != 1 || ((FlowerPatchManager)object).growthStages[n2] < 4 || ((FlowerPatchManager)object).fullyGrownFlags[n2] || (flowerDefinition = FlowerDefinition.forSeedId(((FlowerPatchManager)object).cropIds[n2])) == null)) {
                                            double d = ((FlowerPatchManager)object).diseaseChanceMultipliers[n2] * flowerDefinition.getDiseaseChance();
                                            double d2 = d * 100.0;
                                            int n9 = (int)d2;
                                            if (((FlowerPatchManager)object).patchStates[n2] == 1) {
                                                ((FlowerPatchManager)object).patchStates[n2] = 0;
                                            } else if (GameUtil.randomInclusive(100) <= n9 && ServerSettings.diseasingEnabled) {
                                                ((FlowerPatchManager)object).patchStates[n2] = 2;
                                            }
                                        }
                                    }
                                    if (this.patchStates[n] == 3) break;
                                    if (this.patchStates[n] != 2) {
                                        int n10 = n;
                                        this.growthStages[n10] = this.growthStages[n10] + 1;
                                    }
                                    if (this.shouldStopGrowthCycle(n)) break;
                                }
                                ++n7;
                            }
                        }
                    }
                }
            }
            ++n;
        }
        this.refreshConfig();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean shouldStopGrowthCycle(int n) {
        if (this.lastUpdateTicks[n] == 0L) return true;
        if (this.patchStates[n] == 3) return true;
        int n2 = n;
        FlowerPatchManager flowerPatchManager = this;
        FlowerDefinition flowerDefinition = FlowerDefinition.forSeedId(flowerPatchManager.cropIds[n2]);
        if (flowerDefinition == null) return false;
        int n3 = flowerPatchManager.growthStages[n2] - 4;
        if (flowerDefinition.getConfigEndStage() != flowerDefinition.getConfigStartStage() + n3) return false;
        flowerPatchManager.fullyGrownFlags[n2] = true;
        return true;
    }

    public final boolean waterPatch(int n, int n2, int n3) {
        FlowerPatch flowerPatch = FlowerPatch.forPosition(new Position(n, n2));
        if (flowerPatch == null) {
            return false;
        }
        Object object = FlowerDefinition.forSeedId(this.cropIds[flowerPatch.getIndex()]);
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[flowerPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[flowerPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[flowerPatch.getIndex()] == 1 || this.growthStages[flowerPatch.getIndex()] <= 1 || this.growthStages[flowerPatch.getIndex()] == ((FlowerDefinition)object).getGrowthStageCount() + 4) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This patch doesn't need watering.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(n3 == 5333 ? n3 - 2 : n3 - 1));
        object = this.player;
        ((Player)object).packetSender.sendGameMessage("You water the patch.");
        this.player.getUpdateState().setAnimation(2293);
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new FlowerWateringTask(this, flowerPatch), 5);
        return true;
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        FlowerPatch flowerPatch = FlowerPatch.forPosition(new Position(n, n2));
        if (flowerPatch == null || n3 != 5341 && n3 != 952) {
            return false;
        }
        if (this.growthStages[flowerPatch.getIndex()] == 3) {
            return true;
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
        if (this.growthStages[flowerPatch.getIndex()] <= 3) {
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
        CycleEventHandler.getInstance().schedule(this.player, new FlowerClearingTask(this, n3, flowerPatch), n4);
        return true;
    }

    public final boolean plantSeed(int n, int n2, int n3) {
        FlowerPatch flowerPatch = FlowerPatch.forPosition(new Position(n, n2));
        FlowerDefinition flowerDefinition = FlowerDefinition.forSeedId(n3);
        if (flowerPatch == null || flowerDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[flowerPatch.getIndex()] != 3) {
            this.player.getDialogueManager().showOneLineStatement("You can't plant a seed here.");
            return false;
        }
        if (flowerDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + flowerDefinition.getRequiredLevel() + " to plant this seed.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5343)) {
            this.player.getDialogueManager().showOneLineStatement("You need a seed dibber to plant seed here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2291);
        Player player = this.player;
        player.packetSender.sendSoundEffect(1321, 1, 0);
        this.growthStages[flowerPatch.getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new FlowerPlantingTask(this, flowerPatch, n3, flowerDefinition), 3);
        return true;
    }

    public final boolean harvestPatch(int n, int n2) {
        Object object = FlowerPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        FlowerDefinition flowerDefinition = FlowerDefinition.forSeedId(this.cropIds[((FlowerPatch)object).getIndex()]);
        if (flowerDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(952)) {
            this.player.getDialogueManager().showOneLineStatement("You need a spade to harvest here.");
            return true;
        }
        int n3 = this.player.nextActionSequence();
        this.player.getUpdateState().setAnimation(830);
        CycleEventHandler.getInstance().schedule(this.player, new FlowerHarvestTask(this, n3, (FlowerPatch)((Object)object), flowerDefinition), 2);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        FlowerPatch flowerPatch = FlowerPatch.forPosition(new Position(n, n2));
        if (flowerPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[flowerPatch.getIndex()] != 3 || this.patchStates[flowerPatch.getIndex()] == 5) {
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
        CycleEventHandler.getInstance().schedule(this.player, new FlowerCompostTask(this, flowerPatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        FlowerPatch flowerPatch = FlowerPatch.forPosition(new Position(n, n2));
        if (flowerPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        FlowerGrowthDefinition flowerGrowthDefinition = FlowerGrowthDefinition.forCropId(this.cropIds[flowerPatch.getIndex()]);
        Object object = FlowerDefinition.forSeedId(this.cropIds[flowerPatch.getIndex()]);
        if (this.patchStates[flowerPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[flowerPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.growthStages[flowerPatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is an flower patch. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[flowerPatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[flowerPatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is an flower patch. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[flowerPatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is an flower patch. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is an flower patch. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (flowerGrowthDefinition != null && object != null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new FlowerInspectTask(this, flowerPatch, flowerGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        Object object = FlowerPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(6);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        Object object = FlowerPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        FlowerDefinition flowerDefinition = FlowerDefinition.forSeedId(this.cropIds[((FlowerPatch)object).getIndex()]);
        if (flowerDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((FlowerPatch)object).getIndex()] != 3) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("flower", ((FlowerPatch)object).getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            Object object = FlowerDefinition.forSeedId(this.cropIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(((FlowerDefinition)object).getGrowthCycleTicks() * n);
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
        Object object = FlowerPatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 6036) {
            return false;
        }
        FlowerDefinition flowerDefinition = FlowerDefinition.forSeedId(this.cropIds[((FlowerPatch)object).getIndex()]);
        if (flowerDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((FlowerPatch)object).getIndex()] != 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be cured.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(229));
        this.player.getUpdateState().setAnimation(2288);
        this.patchStates[((FlowerPatch)object).getIndex()] = 0;
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new FlowerCureTask(this), 7);
        return true;
    }

    public final boolean plantScarecrow(int n, int n2, int n3) {
        Object object = FlowerPatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 6059) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[((FlowerPatch)object).getIndex()] != 3) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You need to clear the patch before planting a scarecrow");
            return false;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(6059));
        this.player.getUpdateState().setAnimation(832);
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new ScarecrowPlantingTask(this, (FlowerPatch)((Object)object)), 2);
        return true;
    }

    private void resetPatch(int n) {
        this.cropIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
    }

    static /* synthetic */ Player getPlayer(FlowerPatchManager flowerPatchManager) {
        return flowerPatchManager.player;
    }

    static /* synthetic */ void resetPatch(FlowerPatchManager flowerPatchManager, int n) {
        flowerPatchManager.resetPatch(n);
    }
}

