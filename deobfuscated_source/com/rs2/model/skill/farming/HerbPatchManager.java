/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.HerbClearingTask;
import com.rs2.model.skill.farming.HerbCompostTask;
import com.rs2.model.skill.farming.HerbCureTask;
import com.rs2.model.skill.farming.HerbDefinition;
import com.rs2.model.skill.farming.HerbGrowthDefinition;
import com.rs2.model.skill.farming.HerbHarvestTask;
import com.rs2.model.skill.farming.HerbInspectTask;
import com.rs2.model.skill.farming.HerbPatch;
import com.rs2.model.skill.farming.HerbPlantingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class HerbPatchManager {
    private Player player;
    public int[] harvestAmounts = new int[4];
    public int[] growthStages = new int[4];
    public int[] cropIds = new int[4];
    public int[] d = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};

    public HerbPatchManager(Player player) {
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
            HerbPatchManager herbPatchManager = this;
            HerbDefinition herbDefinition = HerbDefinition.forSeedId(n5);
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
                    if (herbDefinition == null) {
                        n2 = -1;
                        break;
                    }
                    if (herbPatchManager.cropIds[n3] == 6311) {
                        if (n4 == 1) {
                            n2 = herbPatchManager.growthStages[n3] + 193;
                            break;
                        }
                        if (n4 == 2) {
                            n2 = herbPatchManager.growthStages[n3] + 196;
                            break;
                        }
                    }
                    if (n4 == 1) {
                        n2 = n6 + 123;
                        break;
                    }
                    if (n4 == 2) {
                        n2 = n6 + 165;
                        break;
                    }
                    int n8 = n6 - 4;
                    n6 = n4;
                    switch (n6) {
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
                        default: {
                            n7 = -1;
                        }
                    }
                    n2 = (n7 << 6) + herbDefinition.getConfigStartStage() + n8;
                }
            }
            nArray[n] = n2;
            ++n;
        }
        int n9 = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        Player player = this.player;
        player.packetSender.sendConfig(515, n9);
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.cropIds.length) {
            long l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
            if (l >= 5L) {
                Object object;
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
                if ((object = HerbDefinition.forSeedId(this.cropIds[n])) != null && !this.shouldStopGrowthCycle(n)) {
                    n2 = (int)(l / (long)((HerbDefinition)((Object)object)).getGrowthCycleTicks());
                    int n5 = this.growthStages[n] - 4;
                    if ((n5 = n2 - n5) > 0) {
                        int n6 = 0;
                        while (n6 < n5) {
                            if (this.growthStages[n] == 4) {
                                int n7 = n;
                                this.growthStages[n7] = this.growthStages[n7] + 1;
                            } else {
                                n2 = n;
                                object = this;
                                if (((HerbPatchManager)object).patchStates[n2] == 1 && GameUtil.randomInt(2) == 0) {
                                    ((HerbPatchManager)object).patchStates[n2] = 2;
                                }
                                if (((HerbPatchManager)object).patchStates[n2] != 1 && ((HerbPatchManager)object).patchStates[n2] != 2) {
                                    HerbDefinition herbDefinition;
                                    if (((HerbPatchManager)object).patchStates[n2] == 4 && ((HerbPatchManager)object).growthStages[n2] != 3) {
                                        ((HerbPatchManager)object).patchStates[n2] = 0;
                                    }
                                    boolean bl = ((HerbPatchManager)object).hasReachedFinalConfigStage(n2);
                                    if (((HerbPatchManager)object).patchStates[n2] == 0 && ((HerbPatchManager)object).growthStages[n2] >= 4 && ((HerbPatchManager)object).growthStages[n2] <= 7 && !bl && (herbDefinition = HerbDefinition.forSeedId(((HerbPatchManager)object).cropIds[n2])) != null) {
                                        double d = ((HerbPatchManager)object).diseaseChanceMultipliers[n2] * herbDefinition.getDiseaseChance();
                                        double d2 = d * 100.0;
                                        int n8 = (int)d2;
                                        if (GameUtil.randomInclusive(100) <= n8 && ServerSettings.diseasingEnabled) {
                                            ((HerbPatchManager)object).patchStates[n2] = 1;
                                        }
                                    }
                                }
                                if (this.patchStates[n] == 2) break;
                                if (this.patchStates[n] != 1) {
                                    int n9 = n;
                                    this.growthStages[n9] = this.growthStages[n9] + 1;
                                }
                                if (this.shouldStopGrowthCycle(n)) break;
                            }
                            ++n6;
                        }
                    }
                }
            }
            ++n;
        }
        this.refreshConfig();
    }

    private boolean shouldStopGrowthCycle(int n) {
        return this.lastUpdateTicks[n] == 0L || this.patchStates[n] == 2 || this.hasReachedFinalConfigStage(n);
    }

    private boolean hasReachedFinalConfigStage(int n) {
        int n2 = this.growthStages[n] - 4;
        HerbDefinition herbDefinition = HerbDefinition.forSeedId(this.cropIds[n]);
        if (herbDefinition.getConfigEndStage() == herbDefinition.getConfigStartStage() + n2) {
            this.patchStates[n] = 0;
            return true;
        }
        return false;
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        if (herbPatch == null || n3 != 5341 && n3 != 952) {
            return false;
        }
        if (this.growthStages[herbPatch.getIndex()] == 3) {
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
        if (this.growthStages[herbPatch.getIndex()] <= 3) {
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
        CycleEventHandler.getInstance().schedule(this.player, new HerbClearingTask(this, n3, herbPatch), n4);
        return true;
    }

    public final boolean plantSeed(int n, int n2, int n3) {
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        HerbDefinition herbDefinition = HerbDefinition.forSeedId(n3);
        if (herbPatch == null || herbDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[herbPatch.getIndex()] != 3) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't plant a seed here.");
            return false;
        }
        if (herbDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + herbDefinition.getRequiredLevel() + " to plant this seed.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5343)) {
            this.player.getDialogueManager().showOneLineStatement("You need a seed dibber to plant seed here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2291);
        Player player = this.player;
        player.packetSender.sendSoundEffect(1321, 1, 0);
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new HerbPlantingTask(this, herbPatch, n3, herbDefinition), 3);
        return true;
    }

    public final boolean harvestPatch(int n, int n2) {
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        if (herbPatch == null) {
            return false;
        }
        HerbDefinition herbDefinition = HerbDefinition.forSeedId(this.cropIds[herbPatch.getIndex()]);
        if (herbDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5329)) {
            this.player.getDialogueManager().showOneLineStatement("You need secateurs to harvest here.");
            return true;
        }
        int n3 = this.player.nextActionSequence();
        this.player.getUpdateState().setAnimation(2282);
        this.player.setActiveCycleEvent(new HerbHarvestTask(this, n3, herbDefinition, herbPatch));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 3);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        if (herbPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[herbPatch.getIndex()] != 3 || this.patchStates[herbPatch.getIndex()] == 4) {
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
        CycleEventHandler.getInstance().schedule(this.player, new HerbCompostTask(this, herbPatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        if (herbPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        HerbGrowthDefinition herbGrowthDefinition = HerbGrowthDefinition.forCropId(this.cropIds[herbPatch.getIndex()]);
        HerbDefinition herbDefinition = HerbDefinition.forSeedId(this.cropIds[herbPatch.getIndex()]);
        if (this.patchStates[herbPatch.getIndex()] == 1) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[herbPatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.growthStages[herbPatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is an herb patch. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[herbPatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[herbPatch.getIndex()] == 0.22) {
                this.player.getDialogueManager().showTwoLineStatement("This is an herb patch. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[herbPatch.getIndex()] == 0.52) {
                this.player.getDialogueManager().showTwoLineStatement("This is an herb patch. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is an herb patch. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (herbGrowthDefinition != null && herbDefinition != null) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new HerbInspectTask(this, herbPatch, herbGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        if (herbPatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(7);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        if (herbPatch == null) {
            return false;
        }
        HerbDefinition herbDefinition = HerbDefinition.forSeedId(this.cropIds[herbPatch.getIndex()]);
        if (herbDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[herbPatch.getIndex()] != 2) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("herb", herbPatch.getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            HerbDefinition herbDefinition = HerbDefinition.forSeedId(this.cropIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(herbDefinition.getGrowthCycleTicks() * n);
            Player player = this.player;
            player.packetSender.sendGameMessage("You succesfully resurrected the crop.");
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
        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n, n2));
        if (herbPatch == null || n3 != 6036) {
            return false;
        }
        HerbDefinition herbDefinition = HerbDefinition.forSeedId(this.cropIds[herbPatch.getIndex()]);
        if (herbDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[herbPatch.getIndex()] != 1) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This plant doesn't need to be cured.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(229));
        this.player.getUpdateState().setAnimation(2288);
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new HerbCureTask(this, herbPatch), 7);
        return true;
    }

    private void resetPatch(int n) {
        this.cropIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.d[n] = 0;
        this.harvestAmounts[n] = 3;
    }

    static /* synthetic */ Player getPlayer(HerbPatchManager herbPatchManager) {
        return herbPatchManager.player;
    }

    static /* synthetic */ void resetPatch(HerbPatchManager herbPatchManager, int n) {
        herbPatchManager.resetPatch(n);
    }
}
