/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.HopsClearingTask;
import com.rs2.model.skill.farming.HopsCompostTask;
import com.rs2.model.skill.farming.HopsCureTask;
import com.rs2.model.skill.farming.HopsDefinition;
import com.rs2.model.skill.farming.HopsGrowthDefinition;
import com.rs2.model.skill.farming.HopsHarvestTask;
import com.rs2.model.skill.farming.HopsInspectTask;
import com.rs2.model.skill.farming.HopsPatch;
import com.rs2.model.skill.farming.HopsPlantingTask;
import com.rs2.model.skill.farming.HopsWateringTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class HopsPatchManager {
    private Player player;
    public int[] harvestAmounts = new int[4];
    public int[] growthStages = new int[4];
    public int[] cropIds = new int[4];
    public int[] d = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};
    private boolean[] fullyGrownFlags = new boolean[4];
    public boolean[] protectionFlags = new boolean[4];

    public HopsPatchManager(Player player) {
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
            object = HopsDefinition.forSeedId(n4);
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
                    n2 = (n6 << 6) + ((HopsDefinition)((Object)object)).getConfigStartStage() + n5;
                }
            }
            nArray[n] = n2;
            ++n;
        }
        int n7 = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        Player player = this.player;
        player.packetSender.sendConfig(506, n7);
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.cropIds.length) {
            block15: {
                long l;
                block16: {
                    l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
                    if (l < 5L) break block15;
                    if (this.growthStages[n] <= 0 || this.growthStages[n] > 3) break block16;
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
                        break block15;
                    }
                    break block15;
                }
                Object object = HopsDefinition.forSeedId(this.cropIds[n]);
                if (object == null || this.shouldStopGrowthCycle(n)) break block15;
                int n5 = (int)(l / (long)((HopsDefinition)object).getGrowthCycleTicks());
                int n6 = this.growthStages[n] - 4;
                if ((n5 -= n6) <= 0) break block15;
                n6 = 0;
                while (n6 < n5) {
                    block18: {
                        block21: {
                            HopsDefinition hopsDefinition;
                            int n7;
                            block19: {
                                block20: {
                                    block17: {
                                        if (this.growthStages[n] != 4) break block17;
                                        if (this.patchStates[n] == 1) {
                                            this.patchStates[n] = 0;
                                        }
                                        int n8 = n;
                                        this.growthStages[n8] = this.growthStages[n8] + 1;
                                        break block18;
                                    }
                                    n7 = n;
                                    object = this;
                                    if (((HopsPatchManager)object).patchStates[n7] != 2) break block19;
                                    if (!((HopsPatchManager)object).protectionFlags[n7]) break block20;
                                    ((HopsPatchManager)object).patchStates[n7] = 0;
                                    hopsDefinition = HopsDefinition.forSeedId(((HopsPatchManager)object).cropIds[n7]);
                                    if (hopsDefinition == null) break block21;
                                    int n9 = n7;
                                    ((HopsPatchManager)object).lastUpdateTicks[n9] = ((HopsPatchManager)object).lastUpdateTicks[n9] + (long)hopsDefinition.getGrowthCycleTicks();
                                    break block19;
                                }
                                if (GameUtil.randomInt(2) == 0) {
                                    ((HopsPatchManager)object).patchStates[n7] = 3;
                                }
                            }
                            if (((HopsPatchManager)object).patchStates[n7] != 2 && ((HopsPatchManager)object).patchStates[n7] != 3) {
                                if (((HopsPatchManager)object).patchStates[n7] == 5 && ((HopsPatchManager)object).growthStages[n7] != 3) {
                                    ((HopsPatchManager)object).patchStates[n7] = 0;
                                }
                                if (!(((HopsPatchManager)object).patchStates[n7] != 0 && ((HopsPatchManager)object).patchStates[n7] != 1 || ((HopsPatchManager)object).growthStages[n7] < 4 || ((HopsPatchManager)object).fullyGrownFlags[n7] || (hopsDefinition = HopsDefinition.forSeedId(((HopsPatchManager)object).cropIds[n7])) == null)) {
                                    double d = ((HopsPatchManager)object).diseaseChanceMultipliers[n7] * hopsDefinition.getDiseaseChance();
                                    double d2 = d * 100.0;
                                    int n10 = (int)d2;
                                    if (((HopsPatchManager)object).patchStates[n7] == 1) {
                                        ((HopsPatchManager)object).patchStates[n7] = 0;
                                    } else if (GameUtil.randomInclusive(100) <= n10 && ServerSettings.diseasingEnabled) {
                                        ((HopsPatchManager)object).patchStates[n7] = 2;
                                    }
                                }
                            }
                        }
                        if (this.patchStates[n] == 3) break block15;
                        if (this.patchStates[n] != 2) {
                            int n11 = n;
                            this.growthStages[n11] = this.growthStages[n11] + 1;
                        }
                        if (this.shouldStopGrowthCycle(n)) break block15;
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
        HopsPatchManager hopsPatchManager = this;
        HopsDefinition hopsDefinition = HopsDefinition.forSeedId(hopsPatchManager.cropIds[n2]);
        if (hopsDefinition == null) return false;
        int n3 = hopsPatchManager.growthStages[n2] - 4;
        if (hopsDefinition.getConfigStartStage() + n3 != hopsDefinition.getConfigEndStage()) return false;
        hopsPatchManager.fullyGrownFlags[n2] = true;
        return true;
    }

    public final boolean waterPatch(int n, int n2, int n3) {
        HopsPatch hopsPatch = HopsPatch.forPosition(new Position(n, n2));
        if (hopsPatch == null) {
            return false;
        }
        Object object = HopsDefinition.forSeedId(this.cropIds[hopsPatch.getIndex()]);
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[hopsPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[hopsPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[hopsPatch.getIndex()] == 1 || this.growthStages[hopsPatch.getIndex()] <= 1 || this.growthStages[hopsPatch.getIndex()] == ((HopsDefinition)object).getGrowthStageCount() + 4) {
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
        CycleEventHandler.getInstance().schedule(this.player, new HopsWateringTask(this, hopsPatch), 5);
        return true;
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        HopsPatch hopsPatch = HopsPatch.forPosition(new Position(n, n2));
        if (hopsPatch == null || n3 != 5341 && n3 != 952) {
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
        if (this.growthStages[hopsPatch.getIndex()] == 3) {
            return true;
        }
        if (this.growthStages[hopsPatch.getIndex()] <= 3) {
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
        CycleEventHandler.getInstance().schedule(this.player, new HopsClearingTask(this, n3, hopsPatch), n4);
        return true;
    }

    public final boolean plantSeed(int n, int n2, int n3) {
        HopsPatch hopsPatch = HopsPatch.forPosition(new Position(n, n2));
        HopsDefinition hopsDefinition = HopsDefinition.forSeedId(n3);
        if (hopsPatch == null || hopsDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[hopsPatch.getIndex()] != 3) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't plant a seed here.");
            return false;
        }
        if (hopsDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + hopsDefinition.getRequiredLevel() + " to plant this seed.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5343)) {
            this.player.getDialogueManager().showOneLineStatement("You need a seed dibber to plant seed here.");
            return true;
        }
        if (this.player.getInventoryManager().getItemAmount(hopsDefinition.getSeedId()) < hopsDefinition.getSeedAmount()) {
            this.player.getDialogueManager().showOneLineStatement("You need atleast " + hopsDefinition.getSeedAmount() + " seeds to plant here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2291);
        Player player = this.player;
        player.packetSender.sendSoundEffect(1321, 1, 0);
        this.growthStages[hopsPatch.getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3, hopsDefinition.getSeedAmount()));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new HopsPlantingTask(this, hopsPatch, n3, hopsDefinition), 3);
        return true;
    }

    public final boolean harvestPatch(int n, int n2) {
        Object object = HopsPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        HopsDefinition hopsDefinition = HopsDefinition.forSeedId(this.cropIds[((HopsPatch)object).getIndex()]);
        if (hopsDefinition == null) {
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
        this.player.setActiveCycleEvent(new HopsHarvestTask(this, n3, hopsDefinition, (HopsPatch)((Object)object)));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        HopsPatch hopsPatch = HopsPatch.forPosition(new Position(n, n2));
        if (hopsPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[hopsPatch.getIndex()] != 3 || this.patchStates[hopsPatch.getIndex()] == 5) {
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
        CycleEventHandler.getInstance().schedule(this.player, new HopsCompostTask(this, hopsPatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        HopsPatch hopsPatch = HopsPatch.forPosition(new Position(n, n2));
        if (hopsPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        HopsGrowthDefinition hopsGrowthDefinition = HopsGrowthDefinition.forCropId(this.cropIds[hopsPatch.getIndex()]);
        Object object = HopsDefinition.forSeedId(this.cropIds[hopsPatch.getIndex()]);
        if (this.patchStates[hopsPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[hopsPatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.growthStages[hopsPatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is a hops patch. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[hopsPatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[hopsPatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is a hops patch. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[hopsPatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is a hops patch. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is a hops patch. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (hopsGrowthDefinition != null && object != null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new HopsInspectTask(this, hopsPatch, hopsGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        Object object = HopsPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(2);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        Object object = HopsPatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        HopsDefinition hopsDefinition = HopsDefinition.forSeedId(this.cropIds[((HopsPatch)object).getIndex()]);
        if (hopsDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((HopsPatch)object).getIndex()] != 3) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("hops", ((HopsPatch)object).getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            Object object = HopsDefinition.forSeedId(this.cropIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(((HopsDefinition)object).getGrowthCycleTicks() * n);
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
        Object object = HopsPatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 6036) {
            return false;
        }
        HopsDefinition hopsDefinition = HopsDefinition.forSeedId(this.cropIds[((HopsPatch)object).getIndex()]);
        if (hopsDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((HopsPatch)object).getIndex()] != 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be cured.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(229));
        this.player.getUpdateState().setAnimation(2288);
        this.player.setActionLocked(true);
        this.patchStates[((HopsPatch)object).getIndex()] = 0;
        CycleEventHandler.getInstance().schedule(this.player, new HopsCureTask(this), 7);
        return true;
    }

    private void resetPatch(int n) {
        this.cropIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.d[n] = 0;
        this.harvestAmounts[n] = 3;
        this.fullyGrownFlags[n] = false;
        this.protectionFlags[n] = false;
    }

    static /* synthetic */ Player getPlayer(HopsPatchManager hopsPatchManager) {
        return hopsPatchManager.player;
    }

    static /* synthetic */ void resetPatch(HopsPatchManager hopsPatchManager, int n) {
        hopsPatchManager.resetPatch(n);
    }
}

