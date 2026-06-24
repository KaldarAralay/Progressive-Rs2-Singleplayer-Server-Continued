/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.farming.FarmedTreeCuttingTask;
import com.rs2.model.skill.farming.FarmedTreeDefinition;
import com.rs2.model.skill.farming.FarmedTreeGrowthDefinition;
import com.rs2.model.skill.farming.TreeClearingTask;
import com.rs2.model.skill.farming.TreeCompostTask;
import com.rs2.model.skill.farming.TreeHealthCheckTask;
import com.rs2.model.skill.farming.TreeInspectTask;
import com.rs2.model.skill.farming.TreePatch;
import com.rs2.model.skill.farming.TreePlantingTask;
import com.rs2.model.skill.farming.TreePruneTask;
import com.rs2.model.skill.farming.TreeStumpRegrowthTask;
import com.rs2.model.skill.woodcutting.TreeDefinition;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class TreePatchManager {
    private Player player;
    public int[] growthStages = new int[4];
    public int[] treeIds = new int[4];
    public int[] patchData = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};
    private boolean[] fullyGrownFlags = new boolean[4];
    public boolean[] protectionFlags = new boolean[4];

    public TreePatchManager(Player player) {
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
            object = FarmedTreeDefinition.forSaplingId(n4);
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
                    if (n3 == 6) {
                        n2 = ((FarmedTreeDefinition)((Object)object)).getCheckedTreeConfigStage();
                        break;
                    }
                    if (n3 == 7) {
                        n2 = ((FarmedTreeDefinition)((Object)object)).getStumpConfigStage();
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
                        default: {
                            n6 = -1;
                        }
                    }
                    n2 = (n6 << 6) + ((FarmedTreeDefinition)((Object)object)).getConfigStartStage() + n5;
                }
            }
            nArray[n] = n2;
            ++n;
        }
        int n7 = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        Player player = this.player;
        player.packetSender.sendConfig(502, n7);
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.treeIds.length) {
            block12: {
                long l;
                block13: {
                    l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
                    if (l < 5L) break block12;
                    if (this.growthStages[n] <= 0 || this.growthStages[n] > 3) break block13;
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
                        break block12;
                    }
                    break block12;
                }
                Object object = FarmedTreeDefinition.forSaplingId(this.treeIds[n]);
                if (object == null || this.shouldStopGrowthCycle(n)) break block12;
                int n5 = (int)(l / (long)((FarmedTreeDefinition)object).getGrowthCycleTicks());
                int n6 = this.growthStages[n] - 4;
                if ((n5 -= n6) <= 0) break block12;
                n6 = 0;
                while (n6 < n5) {
                    block15: {
                        block18: {
                            FarmedTreeDefinition farmedTreeDefinition;
                            int n7;
                            block16: {
                                block17: {
                                    block14: {
                                        if (this.growthStages[n] != 4) break block14;
                                        int n8 = n;
                                        this.growthStages[n8] = this.growthStages[n8] + 1;
                                        break block15;
                                    }
                                    n7 = n;
                                    object = this;
                                    if (((TreePatchManager)object).patchStates[n7] != 1) break block16;
                                    if (!((TreePatchManager)object).protectionFlags[n7]) break block17;
                                    ((TreePatchManager)object).patchStates[n7] = 0;
                                    farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(((TreePatchManager)object).treeIds[n7]);
                                    if (farmedTreeDefinition == null) break block18;
                                    int n9 = n7;
                                    ((TreePatchManager)object).lastUpdateTicks[n9] = ((TreePatchManager)object).lastUpdateTicks[n9] + (long)farmedTreeDefinition.getGrowthCycleTicks();
                                    break block16;
                                }
                                if (GameUtil.randomInt(2) == 0) {
                                    ((TreePatchManager)object).patchStates[n7] = 2;
                                }
                            }
                            if (((TreePatchManager)object).patchStates[n7] != 1 && ((TreePatchManager)object).patchStates[n7] != 2) {
                                if (((TreePatchManager)object).patchStates[n7] == 5 && ((TreePatchManager)object).growthStages[n7] != 3) {
                                    ((TreePatchManager)object).patchStates[n7] = 0;
                                }
                                if (((TreePatchManager)object).patchStates[n7] == 0 && ((TreePatchManager)object).growthStages[n7] >= 4 && !((TreePatchManager)object).fullyGrownFlags[n7] && (farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(((TreePatchManager)object).treeIds[n7])) != null) {
                                    double d = ((TreePatchManager)object).diseaseChanceMultipliers[n7] * farmedTreeDefinition.getDiseaseChance();
                                    double d2 = d * 100.0;
                                    int n10 = (int)d2;
                                    if (GameUtil.randomInclusive(100) <= n10 && ServerSettings.diseasingEnabled) {
                                        ((TreePatchManager)object).patchStates[n7] = 1;
                                    }
                                }
                            }
                        }
                        if (this.patchStates[n] == 2) break block12;
                        if (this.patchStates[n] != 1) {
                            int n11 = n;
                            this.growthStages[n11] = this.growthStages[n11] + 1;
                        }
                        if (this.shouldStopGrowthCycle(n)) break block12;
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
        if (this.patchStates[n] == 2) return true;
        int n2 = n;
        TreePatchManager treePatchManager = this;
        FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(treePatchManager.treeIds[n2]);
        if (farmedTreeDefinition == null) return false;
        int n3 = treePatchManager.growthStages[n2] - 4;
        if (farmedTreeDefinition.getConfigStartStage() + n3 != farmedTreeDefinition.getConfigEndStage()) return false;
        treePatchManager.fullyGrownFlags[n2] = true;
        return true;
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        TreePatch treePatch = TreePatch.forPosition(new Position(n, n2));
        if (treePatch == null || n3 != 5341 && n3 != 952) {
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
        if (this.growthStages[treePatch.getIndex()] == 3) {
            return true;
        }
        if (this.growthStages[treePatch.getIndex()] <= 3) {
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
        CycleEventHandler.getInstance().schedule(this.player, new TreeClearingTask(this, n3, treePatch), n4);
        return true;
    }

    public final boolean plantSapling(int n, int n2, int n3) {
        Object object = TreePatch.forPosition(new Position(n, n2));
        FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(n3);
        if (object == null || farmedTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[((TreePatch)((Object)object)).getIndex()] != 3) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You can't plant a sapling here.");
            return true;
        }
        if (farmedTreeDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + farmedTreeDefinition.getRequiredLevel() + " to plant this sapling.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5325)) {
            this.player.getDialogueManager().showOneLineStatement("You need a trowel to plant the sapling here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2272);
        this.growthStages[((TreePatch)((Object)object)).getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new TreePlantingTask(this, (TreePatch)((Object)object), n3, farmedTreeDefinition), 3);
        return true;
    }

    public final boolean checkHealth(int n, int n2) {
        Object object = TreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(this.treeIds[((TreePatch)object).getIndex()]);
        if (farmedTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((TreePatch)object).getIndex()] != 0) {
            return false;
        }
        this.player.getUpdateState().setAnimation(832);
        int n3 = this.player.nextActionSequence();
        this.player.setActiveCycleEvent(new TreeHealthCheckTask(this, n3, farmedTreeDefinition, (TreePatch)((Object)object)));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
        return true;
    }

    public final void scheduleStumpRegrowth(int n) {
        CycleEventHandler.getInstance().schedule(this.player, new TreeStumpRegrowthTask(this, n), 500);
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        TreePatch treePatch = TreePatch.forPosition(new Position(n, n2));
        if (treePatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[treePatch.getIndex()] != 3 || this.patchStates[treePatch.getIndex()] == 5) {
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
        CycleEventHandler.getInstance().schedule(this.player, new TreeCompostTask(this, treePatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        TreePatch treePatch = TreePatch.forPosition(new Position(n, n2));
        if (treePatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        FarmedTreeGrowthDefinition farmedTreeGrowthDefinition = FarmedTreeGrowthDefinition.forTreeId(this.treeIds[treePatch.getIndex()]);
        Object object = FarmedTreeDefinition.forSaplingId(this.treeIds[treePatch.getIndex()]);
        if (this.patchStates[treePatch.getIndex()] == 1) {
            this.player.getDialogueManager().showTwoLineStatement("This tree is diseased. Use secateurs to prune the area, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[treePatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This tree is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[treePatch.getIndex()] == 7) {
            this.player.getDialogueManager().showTwoLineStatement("This is a tree stump, to remove it, use a spade on it", "to recieve some roots and clear the patch.");
            return true;
        }
        if (this.growthStages[treePatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is a tree patch. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[treePatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[treePatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is a tree patch. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[treePatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is a tree patch. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is a tree patch. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (farmedTreeGrowthDefinition != null && object != null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new TreeInspectTask(this, treePatch, farmedTreeGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        Object object = TreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.getSkillGuideManager().showFarmingGuide(3);
        this.player.getSkillGuideManager().selectedSkillIndex = 20;
        return true;
    }

    public final boolean startResurrection(int n, int n2) {
        Object object = TreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(this.treeIds[((TreePatch)object).getIndex()]);
        if (farmedTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((TreePatch)object).getIndex()] != 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("tree", ((TreePatch)object).getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            Object object = FarmedTreeDefinition.forSaplingId(this.treeIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(((FarmedTreeDefinition)object).getGrowthCycleTicks() * n);
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
        Object object = TreePatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 5329 && n3 != 7409) {
            return false;
        }
        FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(this.treeIds[((TreePatch)object).getIndex()]);
        if (farmedTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((TreePatch)object).getIndex()] != 1) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This area doesn't need to be pruned.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2275);
        this.player.setActionLocked(true);
        this.patchStates[((TreePatch)object).getIndex()] = 0;
        CycleEventHandler.getInstance().schedule(this.player, new TreePruneTask(this), 15);
        return true;
    }

    public final void resetPatch(int n) {
        this.treeIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.patchData[n] = 0;
        this.fullyGrownFlags[n] = false;
        this.protectionFlags[n] = false;
    }

    public final boolean startCuttingTree(int n, int n2) {
        TreePatch treePatch = TreePatch.forPosition(new Position(n, n2));
        if (treePatch == null) {
            return false;
        }
        FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(this.treeIds[treePatch.getIndex()]);
        if (farmedTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Not enough space in your inventory.");
            return true;
        }
        GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.findUsableGatheringTool(this.player, 8);
        if (gatheringToolDefinition == null) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You do not have an axe which you have the woodcutting level to use.");
            return true;
        }
        int n3 = farmedTreeDefinition.getTreeObjectId();
        TreeDefinition treeDefinition = TreeDefinition.forObjectId(n3);
        if (treeDefinition == null) {
            return true;
        }
        Player player = this.player;
        player.packetSender.sendGameMessage("You swing your axe at the tree.");
        int n4 = gatheringToolDefinition.getGatherAnimationId();
        gatheringToolDefinition.getToolSpeed();
        treeDefinition.getRequiredLevel();
        this.player.getUpdateState().setAnimation(n4);
        this.player.ah = new Position(n, n2);
        int n5 = this.player.nextActionSequence();
        this.player.temporaryActionValue = 0;
        this.player.O = 0;
        this.player.setActiveCycleEvent(new FarmedTreeCuttingTask(this, n5, n4, treeDefinition, gatheringToolDefinition, n3, treePatch, n, n2));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 1);
        return true;
    }

    public final boolean canContinueCuttingTree(int n, int n2) {
        Object object = TreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        FarmedTreeDefinition farmedTreeDefinition = FarmedTreeDefinition.forSaplingId(this.treeIds[((TreePatch)object).getIndex()]);
        if (farmedTreeDefinition == null) {
            return false;
        }
        int n3 = farmedTreeDefinition.getTreeObjectId();
        if (!this.fullyGrownFlags[((TreePatch)object).getIndex()]) {
            return false;
        }
        if (ItemCombinationHandler.findUsableGatheringTool(this.player, 8) == null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You do not have an axe which you have the woodcutting level to use.");
            return false;
        }
        if (!this.player.getInventoryManager().canAddItem(new ItemStack(1511))) {
            return false;
        }
        return SkillActionHelper.checkSkillRequirement(this.player, 8, TreeDefinition.forObjectId(n3).getRequiredLevel(), "chop this tree");
    }

    static /* synthetic */ Player getPlayer(TreePatchManager treePatchManager) {
        return treePatchManager.player;
    }
}

