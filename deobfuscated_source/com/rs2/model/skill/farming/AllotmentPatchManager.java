/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.AllotmentClearingTask;
import com.rs2.model.skill.farming.AllotmentCompostTask;
import com.rs2.model.skill.farming.AllotmentCropDefinition;
import com.rs2.model.skill.farming.AllotmentCureTask;
import com.rs2.model.skill.farming.AllotmentGrowthDefinition;
import com.rs2.model.skill.farming.AllotmentHarvestTask;
import com.rs2.model.skill.farming.AllotmentInspectTask;
import com.rs2.model.skill.farming.AllotmentPatch;
import com.rs2.model.skill.farming.AllotmentPlantingTask;
import com.rs2.model.skill.farming.AllotmentWateringTask;
import com.rs2.model.skill.farming.CropStorageDefinition;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public class AllotmentPatchManager {
    private Player player;
    public int[] harvestAmounts = new int[8];
    public int[] growthStages = new int[8];
    public int[] cropIds = new int[8];
    public int[] d = new int[8];
    public int[] patchStates = new int[8];
    public long[] lastUpdateTicks = new long[8];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    public boolean[] protectionFlags = new boolean[8];
    private boolean[] fullyGrownFlags = new boolean[8];

    public AllotmentPatchManager(Player player) {
        this.player = player;
    }

    public final void refreshConfig() {
        Object object;
        int[] nArray = new int[this.growthStages.length];
        int n = 0;
        while (n < this.growthStages.length) {
            int n2;
            int n3 = this.patchStates[n];
            int n4 = this.cropIds[n];
            int n5 = this.growthStages[n];
            object = this;
            object = AllotmentCropDefinition.forSeedId(n4);
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
                    int n6;
                    if (object == null) {
                        n2 = -1;
                        break;
                    }
                    n5 -= 4;
                    n4 = n3;
                    switch (n4) {
                        case 0: {
                            n6 = 0;
                            break;
                        }
                        case 1: {
                            n6 = 1;
                            break;
                        }
                        case 2: {
                            n6 = 2;
                            break;
                        }
                        case 3: {
                            n6 = 3;
                            break;
                        }
                        default: {
                            n6 = -1;
                        }
                    }
                    n2 = (n6 << 6) + ((AllotmentCropDefinition)((Object)object)).getConfigStartStage() + n5;
                }
            }
            nArray[n] = n2;
            ++n;
        }
        n = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        object = this.player;
        ((Player)object).packetSender.sendConfig(504, n);
        n = nArray[4] << 16 | nArray[5] << 8 << 16 | nArray[6] | nArray[7] << 8;
        object = this.player;
        ((Player)object).packetSender.sendConfig(505, n);
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.cropIds.length) {
            block23: {
                Object object;
                int n2;
                long l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
                if (l < 5L) break block23;
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
                if ((object = AllotmentCropDefinition.forSeedId(this.cropIds[n])) == null || this.shouldStopGrowthCycle(n)) break block23;
                n2 = (int)(l / (long)((AllotmentCropDefinition)object).getGrowthCycleTicks());
                int n5 = this.growthStages[n] - 4;
                if ((n5 = n2 - n5) <= 0) break block23;
                int n6 = 0;
                while (n6 < n5) {
                    block25: {
                        block28: {
                            AllotmentCropDefinition allotmentCropDefinition;
                            block26: {
                                block27: {
                                    block24: {
                                        if (this.growthStages[n] != 4) break block24;
                                        if (this.patchStates[n] == 1) {
                                            this.patchStates[n] = 0;
                                        }
                                        int n7 = n;
                                        this.growthStages[n7] = this.growthStages[n7] + 1;
                                        break block25;
                                    }
                                    n2 = n;
                                    object = this;
                                    if (((AllotmentPatchManager)object).patchStates[n2] != 2) break block26;
                                    if (!((AllotmentPatchManager)object).protectionFlags[n2]) break block27;
                                    ((AllotmentPatchManager)object).patchStates[n2] = 0;
                                    allotmentCropDefinition = AllotmentCropDefinition.forSeedId(((AllotmentPatchManager)object).cropIds[n2]);
                                    if (allotmentCropDefinition == null) break block28;
                                    int n8 = n2;
                                    ((AllotmentPatchManager)object).lastUpdateTicks[n8] = ((AllotmentPatchManager)object).lastUpdateTicks[n8] + (long)allotmentCropDefinition.getGrowthCycleTicks();
                                    break block26;
                                }
                                if (GameUtil.randomInt(2) == 0) {
                                    ((AllotmentPatchManager)object).patchStates[n2] = 3;
                                }
                            }
                            if (((AllotmentPatchManager)object).patchStates[n2] != 2 && ((AllotmentPatchManager)object).patchStates[n2] != 3) {
                                if (((AllotmentPatchManager)object).patchStates[n2] == 5 && ((AllotmentPatchManager)object).growthStages[n2] != 3) {
                                    ((AllotmentPatchManager)object).patchStates[n2] = 0;
                                }
                                if (!(((AllotmentPatchManager)object).patchStates[n2] != 0 && ((AllotmentPatchManager)object).patchStates[n2] != 1 || ((AllotmentPatchManager)object).growthStages[n2] < 4 || ((AllotmentPatchManager)object).fullyGrownFlags[n2] || (allotmentCropDefinition = AllotmentCropDefinition.forSeedId(((AllotmentPatchManager)object).cropIds[n2])) == null || (allotmentCropDefinition = AllotmentCropDefinition.forSeedId(((AllotmentPatchManager)object).cropIds[n2])) == null)) {
                                    double d = ((AllotmentPatchManager)object).diseaseChanceMultipliers[n2] * allotmentCropDefinition.getDiseaseChance();
                                    double d2 = d * 100.0;
                                    int n9 = (int)d2;
                                    int n10 = 0;
                                    Player cfr_ignored_0 = ((AllotmentPatchManager)object).player;
                                    if (((AllotmentPatchManager)object).patchStates[n2] == 1) {
                                        ((AllotmentPatchManager)object).patchStates[n2] = 0;
                                    } else if (!((AllotmentPatchManager)object).protectionFlags[n2] && GameUtil.randomInclusive(100) <= n9) {
                                        switch (n2) {
                                            case 0: 
                                            case 1: {
                                                n10 = 3;
                                                break;
                                            }
                                            case 2: 
                                            case 3: {
                                                n10 = 2;
                                                break;
                                            }
                                            case 4: 
                                            case 5: {
                                                n10 = 1;
                                                break;
                                            }
                                            case 6: 
                                            case 7: {
                                                n10 = 0;
                                            }
                                        }
                                        if (((AllotmentPatchManager)object).player.getFlowerPatchManager().cropIds[n10] < 33 || ((AllotmentPatchManager)object).player.getFlowerPatchManager().cropIds[n10] > 36 || allotmentCropDefinition.getProtectionCropId() != 6059) {
                                            if (((AllotmentPatchManager)object).player.getFlowerPatchManager().patchStates[n10] != 3 && ((AllotmentPatchManager)object).player.getFlowerPatchManager().fullyGrownFlags[n10] && ((AllotmentPatchManager)object).player.getFlowerPatchManager().cropIds[n10] == allotmentCropDefinition.getProtectionCropId()) {
                                                ((AllotmentPatchManager)object).player.getFlowerPatchManager().patchStates[n10] = 3;
                                                ((AllotmentPatchManager)object).player.getFlowerPatchManager().refreshConfig();
                                            } else if (ServerSettings.diseasingEnabled) {
                                                ((AllotmentPatchManager)object).patchStates[n2] = 2;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (this.patchStates[n] == 3) break;
                        if (this.patchStates[n] != 2) {
                            int n11 = n;
                            this.growthStages[n11] = this.growthStages[n11] + 1;
                        }
                        if (this.shouldStopGrowthCycle(n)) break;
                    }
                    ++n6;
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
        AllotmentPatchManager allotmentPatchManager = this;
        AllotmentCropDefinition allotmentCropDefinition = AllotmentCropDefinition.forSeedId(allotmentPatchManager.cropIds[n2]);
        if (allotmentCropDefinition == null) return false;
        int n3 = allotmentPatchManager.growthStages[n2] - 4;
        if (allotmentCropDefinition.getConfigStartStage() + n3 != allotmentCropDefinition.getConfigEndStage()) return false;
        allotmentPatchManager.fullyGrownFlags[n2] = true;
        return true;
    }

    public final boolean waterPatch(int n, int n2, int n3) {
        AllotmentPatch allotmentPatch = AllotmentPatch.forPosition(new Position(n, n2));
        if (allotmentPatch == null) {
            return false;
        }
        Object object = AllotmentCropDefinition.forSeedId(this.cropIds[allotmentPatch.getIndex()]);
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[allotmentPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[allotmentPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[allotmentPatch.getIndex()] == 1 || this.growthStages[allotmentPatch.getIndex()] <= 1 || this.growthStages[allotmentPatch.getIndex()] == ((AllotmentCropDefinition)object).getGrowthStageCount() + 4) {
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
        CycleEventHandler.getInstance().schedule(this.player, new AllotmentWateringTask(this, allotmentPatch), 5);
        return true;
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        AllotmentPatch allotmentPatch = AllotmentPatch.forPosition(new Position(n, n2));
        if (allotmentPatch == null || n3 != 5341 && n3 != 952) {
            return false;
        }
        if (this.growthStages[allotmentPatch.getIndex()] == 3) {
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
        if (this.growthStages[allotmentPatch.getIndex()] <= 3) {
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
        CycleEventHandler.getInstance().schedule(this.player, new AllotmentClearingTask(this, n3, allotmentPatch), n4);
        return true;
    }

    public final boolean plantSeed(int n, int n2, int n3) {
        AllotmentPatch allotmentPatch = AllotmentPatch.forPosition(new Position(n, n2));
        AllotmentCropDefinition allotmentCropDefinition = AllotmentCropDefinition.forSeedId(n3);
        if (allotmentPatch == null || allotmentCropDefinition == null) {
            return false;
        }
        if (this.growthStages[allotmentPatch.getIndex()] != 3) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't plant a seed here.");
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (allotmentCropDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + allotmentCropDefinition.getRequiredLevel() + " to plant this seed.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5343)) {
            this.player.getDialogueManager().showOneLineStatement("You need a seed dibber to plant seed here.");
            return true;
        }
        if (this.player.getInventoryManager().getItemAmount(allotmentCropDefinition.getSeedId()) < allotmentCropDefinition.getSeedAmount()) {
            this.player.getDialogueManager().showOneLineStatement("You need atleast " + allotmentCropDefinition.getSeedAmount() + " seeds to plant here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2291);
        Player player = this.player;
        player.packetSender.sendSoundEffect(1321, 1, 0);
        this.growthStages[allotmentPatch.getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3, allotmentCropDefinition.getSeedAmount()));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new AllotmentPlantingTask(this, allotmentPatch, n3, allotmentCropDefinition), 3);
        return true;
    }

    public final boolean harvestPatch(int n, int n2) {
        Object object = AllotmentPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        AllotmentCropDefinition allotmentCropDefinition = AllotmentCropDefinition.forSeedId(this.cropIds[((AllotmentPatch)object).getIndex()]);
        if (allotmentCropDefinition == null) {
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
        this.player.setActiveCycleEvent(new AllotmentHarvestTask(this, n3, allotmentCropDefinition, (AllotmentPatch)((Object)object)));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        AllotmentPatch allotmentPatch = AllotmentPatch.forPosition(new Position(n, n2));
        if (allotmentPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[allotmentPatch.getIndex()] != 3 || this.patchStates[allotmentPatch.getIndex()] == 5) {
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
        CycleEventHandler.getInstance().schedule(this.player, new AllotmentCompostTask(this, allotmentPatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        AllotmentPatch allotmentPatch = AllotmentPatch.forPosition(new Position(n, n2));
        if (allotmentPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        AllotmentGrowthDefinition allotmentGrowthDefinition = AllotmentGrowthDefinition.forCropId(this.cropIds[allotmentPatch.getIndex()]);
        Object object = AllotmentCropDefinition.forSeedId(this.cropIds[allotmentPatch.getIndex()]);
        if (this.patchStates[allotmentPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[allotmentPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.growthStages[allotmentPatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is an allotment patch. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[allotmentPatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[allotmentPatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is an allotment patch. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[allotmentPatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is an allotment patch. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is an allotment patch. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (allotmentGrowthDefinition != null && object != null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new AllotmentInspectTask(this, allotmentPatch, allotmentGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        Object object = AllotmentPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(1);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        Object object = AllotmentPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        AllotmentCropDefinition allotmentCropDefinition = AllotmentCropDefinition.forSeedId(this.cropIds[((AllotmentPatch)object).getIndex()]);
        if (allotmentCropDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((AllotmentPatch)object).getIndex()] != 3) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("allotment", ((AllotmentPatch)object).getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            Object object = AllotmentCropDefinition.forSeedId(this.cropIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(((AllotmentCropDefinition)object).getGrowthCycleTicks() * n);
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
        Object object = AllotmentPatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 6036) {
            return false;
        }
        AllotmentCropDefinition allotmentCropDefinition = AllotmentCropDefinition.forSeedId(this.cropIds[((AllotmentPatch)object).getIndex()]);
        if (allotmentCropDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((AllotmentPatch)object).getIndex()] != 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be cured.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(229));
        this.player.getUpdateState().setAnimation(2288);
        this.player.setActionLocked(true);
        this.patchStates[((AllotmentPatch)object).getIndex()] = 0;
        CycleEventHandler.getInstance().schedule(this.player, new AllotmentCureTask(this), 7);
        return true;
    }

    private void resetPatch(int n) {
        this.cropIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.d[n] = 0;
        this.harvestAmounts[n] = 3;
        this.protectionFlags[n] = false;
        this.fullyGrownFlags[n] = false;
    }

    static /* synthetic */ Player getPlayer(AllotmentPatchManager allotmentPatchManager) {
        return allotmentPatchManager.player;
    }

    static /* synthetic */ void resetPatch(AllotmentPatchManager allotmentPatchManager, int n) {
        allotmentPatchManager.resetPatch(n);
    }

    public static boolean emptyCropStorageContainer(Player player, int n) {
        CropStorageDefinition cropStorageDefinition;
        CropStorageDefinition cropStorageDefinition2;
        Object object;
        int n2;
        block8: {
            if (n == -1 || new ItemStack(n, 1).getDefinition().isNote()) {
                return false;
            }
            int n3 = n;
            CropStorageDefinition[] cropStorageDefinitionArray = CropStorageDefinition.values();
            int n4 = cropStorageDefinitionArray.length;
            n2 = 0;
            while (n2 < n4) {
                object = cropStorageDefinitionArray[n2];
                int n5 = ((CropStorageDefinition)object).getBaseContainerItemId();
                int n6 = ((CropStorageDefinition)object).getBaseContainerItemId() + (CropStorageDefinition.isSack((CropStorageDefinition)object) ? 18 : 8);
                if (n3 >= n5 && n3 <= n6) {
                    cropStorageDefinition2 = (CropStorageDefinition)object;
                    break block8;
                }
                ++n2;
            }
            cropStorageDefinition2 = cropStorageDefinition = null;
        }
        if (cropStorageDefinition2 != null) {
            object = new ItemStack(cropStorageDefinition2.getProduceItemId(), 1);
            n2 = 0;
            if (n == cropStorageDefinition2.getBaseContainerItemId()) {
                n2 = 1;
            }
            if (player.getInventoryManager().canAddItem((ItemStack)object)) {
                player.getInventoryManager().addItem((ItemStack)object);
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                if (n2 != 0) {
                    player.getInventoryManager().addItem(new ItemStack(cropStorageDefinition2.isSack() ? 5418 : 5376, 1));
                } else {
                    player.getInventoryManager().addItem(new ItemStack(n - 2, 1));
                }
                return true;
            }
        }
        return false;
    }
}

