/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.farming.FruitTreeClearingTask;
import com.rs2.model.skill.farming.FruitTreeCompostTask;
import com.rs2.model.skill.farming.FruitTreeCuttingTask;
import com.rs2.model.skill.farming.FruitTreeDefinition;
import com.rs2.model.skill.farming.FruitTreeGrowthDefinition;
import com.rs2.model.skill.farming.FruitTreeHarvestTask;
import com.rs2.model.skill.farming.FruitTreeInspectTask;
import com.rs2.model.skill.farming.FruitTreePatch;
import com.rs2.model.skill.farming.FruitTreePlantingTask;
import com.rs2.model.skill.farming.FruitTreePruneTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class FruitTreePatchManager {
    private Player player;
    public int[] growthStages = new int[4];
    public int[] treeIds = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};
    public boolean[] f = new boolean[4];
    public boolean[] protectionFlags = new boolean[4];

    public FruitTreePatchManager(Player player) {
        this.player = player;
    }

    public final void refreshConfig() {
        int[] nArray = new int[this.growthStages.length];
        int n = 0;
        while (n < this.growthStages.length) {
            int n2;
            int n3 = this.patchStates[n];
            int n4 = this.treeIds[n];
            int n5 = this.growthStages[n];
            Object object = this;
            object = FruitTreeDefinition.forSaplingId(n4);
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
                    n2 = object == null ? -1 : (n3 == 6 ? ((FruitTreeDefinition)((Object)object)).getStumpConfigStage() : (FruitTreePatchManager.getConfigStageForPatchState(n3, (FruitTreeDefinition)((Object)object), n5) == 3 ? ((FruitTreeDefinition)((Object)object)).getHealthCheckConfigStage() : FruitTreePatchManager.getConfigStageForPatchState(n3, (FruitTreeDefinition)((Object)object), n5)));
                }
            }
            nArray[n] = n2;
            ++n;
        }
        int n6 = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        Player player = this.player;
        player.packetSender.sendConfig(503, n6);
    }

    private static int getConfigStageForPatchState(int n, FruitTreeDefinition fruitTreeDefinition, int n2) {
        n2 -= 4;
        n2 = fruitTreeDefinition.getConfigStartStage() + n2;
        switch (n) {
            case 0: {
                return n2;
            }
            case 1: {
                return n2 + fruitTreeDefinition.getDiseasedConfigOffset();
            }
            case 2: {
                return n2 + fruitTreeDefinition.getDeadConfigOffset();
            }
            case 3: {
                return fruitTreeDefinition.getHealthCheckConfigStage();
            }
        }
        return -1;
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.treeIds.length) {
            block11: {
                long l;
                block12: {
                    l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
                    if (l < 5L) break block11;
                    if (this.growthStages[n] <= 0 || this.growthStages[n] > 3) break block12;
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
                        break block11;
                    }
                    break block11;
                }
                FruitTreeDefinition fruitTreeDefinition = FruitTreeDefinition.forSaplingId(this.treeIds[n]);
                if (fruitTreeDefinition == null || this.shouldStopGrowthCycle(n)) break block11;
                int n5 = (int)(l / (long)fruitTreeDefinition.getGrowthCycleTicks());
                int n6 = this.growthStages[n] - 4;
                if ((n5 -= n6) <= 0) break block11;
                n6 = 0;
                while (n6 < n5) {
                    block14: {
                        block17: {
                            FruitTreeDefinition fruitTreeDefinition2;
                            FruitTreePatchManager fruitTreePatchManager;
                            int n7;
                            block15: {
                                block16: {
                                    block13: {
                                        if (this.growthStages[n] != 4) break block13;
                                        int n8 = n;
                                        this.growthStages[n8] = this.growthStages[n8] + 1;
                                        break block14;
                                    }
                                    n7 = n;
                                    fruitTreePatchManager = this;
                                    if (fruitTreePatchManager.patchStates[n7] != 1) break block15;
                                    if (!fruitTreePatchManager.protectionFlags[n7]) break block16;
                                    fruitTreePatchManager.patchStates[n7] = 0;
                                    fruitTreeDefinition2 = FruitTreeDefinition.forSaplingId(fruitTreePatchManager.treeIds[n7]);
                                    if (fruitTreeDefinition2 == null) break block17;
                                    int n9 = n7;
                                    fruitTreePatchManager.lastUpdateTicks[n9] = fruitTreePatchManager.lastUpdateTicks[n9] + (long)fruitTreeDefinition2.getGrowthCycleTicks();
                                    break block15;
                                }
                                if (GameUtil.randomInt(2) == 0) {
                                    fruitTreePatchManager.patchStates[n7] = 2;
                                }
                            }
                            if (fruitTreePatchManager.patchStates[n7] != 1 && fruitTreePatchManager.patchStates[n7] != 2) {
                                if (fruitTreePatchManager.patchStates[n7] == 5 && fruitTreePatchManager.growthStages[n7] != 2) {
                                    fruitTreePatchManager.patchStates[n7] = 0;
                                }
                                if (fruitTreePatchManager.patchStates[n7] == 0 && fruitTreePatchManager.growthStages[n7] >= 4 && !fruitTreePatchManager.f[n7] && (fruitTreeDefinition2 = FruitTreeDefinition.forSaplingId(fruitTreePatchManager.treeIds[n7])) != null) {
                                    double d = fruitTreePatchManager.diseaseChanceMultipliers[n7] * fruitTreeDefinition2.getDiseaseChance();
                                    double d2 = d * 100.0;
                                    int n10 = (int)d2;
                                    if (GameUtil.randomInclusive(100) <= n10 && ServerSettings.diseasingEnabled) {
                                        fruitTreePatchManager.patchStates[n7] = 1;
                                    }
                                }
                            }
                        }
                        if (this.patchStates[n] == 2) break block11;
                        if (this.patchStates[n] != 1) {
                            int n11 = n;
                            this.growthStages[n11] = this.growthStages[n11] + 1;
                        }
                        if (this.shouldStopGrowthCycle(n)) break block11;
                        if (this.growthStages[n] + fruitTreeDefinition.getConfigStartStage() == fruitTreeDefinition.getMatureConfigStage() + 3) {
                            this.growthStages[n] = fruitTreeDefinition.getGrowthStageCount() + 7;
                            this.patchStates[n] = 3;
                            break;
                        }
                    }
                    ++n6;
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
        FruitTreeDefinition fruitTreeDefinition = FruitTreeDefinition.forSaplingId(this.treeIds[n]);
        if (fruitTreeDefinition == null) {
            return;
        }
        long l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
        int n2 = (int)(l / (long)fruitTreeDefinition.getGrowthCycleTicks());
        this.growthStages[n] = n2 + 4;
        this.refreshConfig();
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        FruitTreePatch fruitTreePatch = FruitTreePatch.forPosition(new Position(n, n2));
        if (fruitTreePatch == null || n3 != 5341 && n3 != 952) {
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
        if (this.growthStages[fruitTreePatch.getIndex()] == 3) {
            return true;
        }
        if (this.growthStages[fruitTreePatch.getIndex()] <= 3) {
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
        CycleEventHandler.getInstance().schedule(this.player, new FruitTreeClearingTask(this, n3, fruitTreePatch), n4);
        return true;
    }

    public final boolean plantSapling(int n, int n2, int n3) {
        Object object = FruitTreePatch.forPosition(new Position(n, n2));
        FruitTreeDefinition fruitTreeDefinition = FruitTreeDefinition.forSaplingId(n3);
        if (object == null || fruitTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[((FruitTreePatch)((Object)object)).getIndex()] != 3) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You can't plant a sapling here.");
            return true;
        }
        if (fruitTreeDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + fruitTreeDefinition.getRequiredLevel() + " to plant this sapling.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5325)) {
            this.player.getDialogueManager().showOneLineStatement("You need a trowel to plant the sapling here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2272);
        this.growthStages[((FruitTreePatch)((Object)object)).getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new FruitTreePlantingTask(this, (FruitTreePatch)((Object)object), n3, fruitTreeDefinition), 3);
        return true;
    }

    public final boolean harvestPatch(int n, int n2) {
        FruitTreePatch fruitTreePatch = FruitTreePatch.forPosition(new Position(n, n2));
        if (fruitTreePatch == null) {
            return false;
        }
        Enum enum_ = FruitTreeDefinition.forSaplingId(this.treeIds[fruitTreePatch.getIndex()]);
        if (enum_ == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[fruitTreePatch.getIndex()] + ((FruitTreeDefinition)enum_).getConfigStartStage() == ((FruitTreeDefinition)enum_).getMatureConfigStage() + 4) {
            boolean bl;
            int n3 = n2;
            n2 = n;
            FruitTreePatchManager fruitTreePatchManager = this;
            enum_ = FruitTreePatch.forPosition(new Position(n2, n3));
            if (enum_ == null) {
                bl = false;
            } else {
                Object object = FruitTreeDefinition.forSaplingId(fruitTreePatchManager.treeIds[((FruitTreePatch)enum_).getIndex()]);
                if (object == null) {
                    bl = false;
                } else if (fruitTreePatchManager.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
                    object = fruitTreePatchManager.player;
                    ((Player)object).packetSender.sendGameMessage("Not enough space in your inventory.");
                    bl = true;
                } else if (ItemCombinationHandler.findUsableGatheringTool(fruitTreePatchManager.player, 8) == null) {
                    object = fruitTreePatchManager.player;
                    ((Player)object).packetSender.sendGameMessage("You do not have an axe which you have the woodcutting level to use.");
                    bl = true;
                } else {
                    object = fruitTreePatchManager.player;
                    ((Player)object).packetSender.sendGameMessage("You swing your axe at the tree.");
                    int n4 = ItemCombinationHandler.findUsableGatheringTool(fruitTreePatchManager.player, 8).getGatherAnimationId();
                    fruitTreePatchManager.player.getUpdateState().setAnimation(n4);
                    fruitTreePatchManager.player.ah = new Position(n2, n3);
                    n2 = fruitTreePatchManager.player.nextActionSequence();
                    fruitTreePatchManager.player.setActiveCycleEvent(new FruitTreeCuttingTask(fruitTreePatchManager, n2, (FruitTreePatch)enum_));
                    CycleEventHandler.getInstance().schedule(fruitTreePatchManager.player, fruitTreePatchManager.player.getActiveCycleEvent(), 5);
                    bl = true;
                }
            }
            return true;
        }
        if (this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Not enough space in your inventory.");
            return true;
        }
        this.player.getUpdateState().setAnimation(832);
        n = this.player.nextActionSequence();
        this.player.setActiveCycleEvent(new FruitTreeHarvestTask(this, n, fruitTreePatch, (FruitTreeDefinition)enum_));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        FruitTreePatch fruitTreePatch = FruitTreePatch.forPosition(new Position(n, n2));
        if (fruitTreePatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[fruitTreePatch.getIndex()] != 3 || this.patchStates[fruitTreePatch.getIndex()] == 5) {
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
        CycleEventHandler.getInstance().schedule(this.player, new FruitTreeCompostTask(this, fruitTreePatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        FruitTreePatch fruitTreePatch = FruitTreePatch.forPosition(new Position(n, n2));
        if (fruitTreePatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        FruitTreeGrowthDefinition fruitTreeGrowthDefinition = FruitTreeGrowthDefinition.forTreeId(this.treeIds[fruitTreePatch.getIndex()]);
        Object object = FruitTreeDefinition.forSaplingId(this.treeIds[fruitTreePatch.getIndex()]);
        if (this.patchStates[fruitTreePatch.getIndex()] == 1) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[fruitTreePatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[fruitTreePatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant has fully grown. You can check it's health", "to gain some farming experiences.");
            return true;
        }
        if (this.patchStates[fruitTreePatch.getIndex()] == 6) {
            this.player.getDialogueManager().showTwoLineStatement("This is a fruit tree stump, to remove it, use a ", "spade on it to clear the patch");
            return true;
        }
        if (this.growthStages[fruitTreePatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is a fruit tree patch. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[fruitTreePatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[fruitTreePatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is a fruit tree patch. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[fruitTreePatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is a fruit tree patch. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is a fruit tree patch. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (fruitTreeGrowthDefinition != null && object != null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new FruitTreeInspectTask(this, fruitTreePatch, fruitTreeGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        Object object = FruitTreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(4);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        Object object = FruitTreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        FruitTreeDefinition fruitTreeDefinition = FruitTreeDefinition.forSaplingId(this.treeIds[((FruitTreePatch)object).getIndex()]);
        if (fruitTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((FruitTreePatch)object).getIndex()] != 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("fruittree", ((FruitTreePatch)object).getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            Object object = FruitTreeDefinition.forSaplingId(this.treeIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(((FruitTreeDefinition)object).getGrowthCycleTicks() * n);
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

    public final boolean prunePatch(int n, int n2, int n3) {
        Object object = FruitTreePatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 5329 && n3 != 7409) {
            return false;
        }
        FruitTreeDefinition fruitTreeDefinition = FruitTreeDefinition.forSaplingId(this.treeIds[((FruitTreePatch)object).getIndex()]);
        if (fruitTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((FruitTreePatch)object).getIndex()] != 1) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This area doesn't need to be pruned.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2275);
        this.player.setActionLocked(true);
        this.patchStates[((FruitTreePatch)object).getIndex()] = 0;
        CycleEventHandler.getInstance().schedule(this.player, new FruitTreePruneTask(this), 15);
        return true;
    }

    private void resetPatch(int n) {
        this.treeIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.f[n] = false;
        this.protectionFlags[n] = false;
    }

    static /* synthetic */ Player getPlayer(FruitTreePatchManager fruitTreePatchManager) {
        return fruitTreePatchManager.player;
    }

    static /* synthetic */ void resetPatch(FruitTreePatchManager fruitTreePatchManager, int n) {
        fruitTreePatchManager.resetPatch(n);
    }
}

