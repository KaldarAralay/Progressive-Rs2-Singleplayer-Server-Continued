/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.SpecialTreeClearingTask;
import com.rs2.model.skill.farming.SpecialTreeCompostTask;
import com.rs2.model.skill.farming.SpecialTreeCureTask;
import com.rs2.model.skill.farming.SpecialTreeDefinition;
import com.rs2.model.skill.farming.SpecialTreeGrowthDefinition;
import com.rs2.model.skill.farming.SpecialTreeHarvestTask;
import com.rs2.model.skill.farming.SpecialTreeInspectTask;
import com.rs2.model.skill.farming.SpecialTreePatch;
import com.rs2.model.skill.farming.SpecialTreePlantingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;

public final class SpecialTreePatchManager {
    private Player player;
    public int[] growthStages = new int[4];
    public int[] treeIds = new int[4];
    public int[] patchStates = new int[4];
    public long[] lastUpdateTicks = new long[4];
    public double[] diseaseChanceMultipliers = new double[]{1.0, 1.0, 1.0, 1.0};
    public boolean[] calquatRegrowthFlags = new boolean[4];

    public SpecialTreePatchManager(Player player) {
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
            object = SpecialTreeDefinition.forSaplingId(n4);
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
                    n2 = object == null ? -1 : (SpecialTreePatchManager.getConfigStageForPatchState(n3, (SpecialTreeDefinition)((Object)object), n5) == 3 ? ((SpecialTreeDefinition)((Object)object)).getHealthCheckConfigStage() : SpecialTreePatchManager.getConfigStageForPatchState(n3, (SpecialTreeDefinition)((Object)object), n5));
                }
            }
            nArray[n] = n2;
            ++n;
        }
        int n6 = (nArray[0] << 16) + (nArray[1] << 8 << 16) + nArray[2] + (nArray[3] << 8);
        Player player = this.player;
        player.packetSender.sendConfig(507, n6);
    }

    private static int getConfigStageForPatchState(int n, SpecialTreeDefinition specialTreeDefinition, int n2) {
        n2 -= 4;
        n2 = specialTreeDefinition.getConfigStartStage() + n2;
        switch (n) {
            case 0: {
                return n2;
            }
            case 1: {
                return n2 + specialTreeDefinition.getDiseasedConfigOffset();
            }
            case 2: {
                return n2 + specialTreeDefinition.getDeadConfigOffset();
            }
            case 3: {
                return specialTreeDefinition.getHealthCheckConfigStage();
            }
        }
        return -1;
    }

    public final void processGrowth() {
        int n = 0;
        while (n < this.treeIds.length) {
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
                    SpecialTreeDefinition specialTreeDefinition = SpecialTreeDefinition.forSaplingId(this.treeIds[n]);
                    if (specialTreeDefinition != null && !this.shouldStopGrowthCycle(n)) {
                        if (specialTreeDefinition == SpecialTreeDefinition.CALQUAT && this.calquatRegrowthFlags[n] && this.growthStages[n] < 18) {
                            int n5 = n;
                            this.growthStages[n5] = this.growthStages[n5] + 1;
                        } else {
                            int n6 = (int)(l / (long)specialTreeDefinition.getGrowthCycleTicks());
                            int n7 = this.growthStages[n] - 4;
                            if ((n6 -= n7) > 0) {
                                n7 = 0;
                                while (n7 < n6) {
                                    if (this.growthStages[n] == 4) {
                                        int n8 = n;
                                        this.growthStages[n8] = this.growthStages[n8] + 1;
                                    } else {
                                        int n9 = n;
                                        SpecialTreePatchManager specialTreePatchManager = this;
                                        if (specialTreePatchManager.patchStates[n9] == 1 && GameUtil.randomInt(2) == 0) {
                                            specialTreePatchManager.patchStates[n9] = 2;
                                        }
                                        if (specialTreePatchManager.patchStates[n9] != 1 && specialTreePatchManager.patchStates[n9] != 2) {
                                            SpecialTreeDefinition specialTreeDefinition2;
                                            if (specialTreePatchManager.patchStates[n9] == 5 && specialTreePatchManager.growthStages[n9] != 2) {
                                                specialTreePatchManager.patchStates[n9] = 0;
                                            }
                                            if (specialTreePatchManager.patchStates[n9] == 0 && specialTreePatchManager.growthStages[n9] >= 4 && !specialTreePatchManager.calquatRegrowthFlags[n9] && (specialTreeDefinition2 = SpecialTreeDefinition.forSaplingId(specialTreePatchManager.treeIds[n9])) != null) {
                                                double d = specialTreePatchManager.diseaseChanceMultipliers[n9] * specialTreeDefinition2.getDiseaseChance();
                                                double d2 = d * 100.0;
                                                int n10 = (int)d2;
                                                if (GameUtil.randomInclusive(100) < n10 && ServerSettings.diseasingEnabled) {
                                                    specialTreePatchManager.patchStates[n9] = 1;
                                                }
                                            }
                                        }
                                        if (this.patchStates[n] == 2) break;
                                        if (this.patchStates[n] != 1) {
                                            int n11 = n;
                                            this.growthStages[n11] = this.growthStages[n11] + 1;
                                        }
                                        if (this.shouldStopGrowthCycle(n)) break;
                                        if (this.growthStages[n] <= specialTreeDefinition.getGrowthStageCount() + (specialTreeDefinition == SpecialTreeDefinition.SPIRIT_TREE ? 3 : -2) && this.growthStages[n] == specialTreeDefinition.getGrowthStageCount() + (specialTreeDefinition == SpecialTreeDefinition.SPIRIT_TREE ? 3 : -2)) {
                                            this.growthStages[n] = specialTreeDefinition.getGrowthStageCount() + 7;
                                            this.patchStates[n] = 3;
                                            break;
                                        }
                                    }
                                    ++n7;
                                }
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
        SpecialTreeDefinition specialTreeDefinition = SpecialTreeDefinition.forSaplingId(this.treeIds[n]);
        if (specialTreeDefinition == null) {
            return;
        }
        long l = Server.getElapsedMinutes() - this.lastUpdateTicks[n];
        int n2 = (int)(l / (long)specialTreeDefinition.getGrowthCycleTicks());
        this.growthStages[n] = n2 + 4;
        this.refreshConfig();
    }

    public final boolean clearPatch(int n, int n2, int n3) {
        int n4;
        SpecialTreePatch specialTreePatch = SpecialTreePatch.forPosition(new Position(n, n2));
        if (specialTreePatch == null || n3 != 5341 && n3 != 952) {
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
        if (this.growthStages[specialTreePatch.getIndex()] == 3) {
            return true;
        }
        if (this.growthStages[specialTreePatch.getIndex()] <= 3) {
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
        CycleEventHandler.getInstance().schedule(this.player, new SpecialTreeClearingTask(this, n3, specialTreePatch), n4);
        return true;
    }

    public final boolean plantSapling(int n, int n2, int n3) {
        Object object = SpecialTreePatch.forPosition(new Position(n, n2));
        SpecialTreeDefinition specialTreeDefinition = SpecialTreeDefinition.forSaplingId(n3);
        if (object == null || specialTreeDefinition == null || ((SpecialTreePatch)((Object)object)).getObjectId() != n3) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if ((this.growthStages[0] > 3 || this.growthStages[2] > 3 || this.growthStages[3] > 3) && ((SpecialTreePatch)((Object)object)).getIndex() != 1) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You already have a spirit tree planted somewhere else.");
            return true;
        }
        if (this.growthStages[((SpecialTreePatch)((Object)object)).getIndex()] != 3) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You can't plant a sapling here.");
            return true;
        }
        if (specialTreeDefinition.getRequiredLevel() > this.player.getSkillManager().getCurrentLevels()[19]) {
            this.player.getDialogueManager().showOneLineStatement("You need a farming level of " + specialTreeDefinition.getRequiredLevel() + " to plant this sapling.");
            return true;
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5325)) {
            this.player.getDialogueManager().showOneLineStatement("You need a trowel to plant the sapling here.");
            return true;
        }
        this.player.getUpdateState().setAnimation(2272);
        this.growthStages[((SpecialTreePatch)((Object)object)).getIndex()] = 4;
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new SpecialTreePlantingTask(this, (SpecialTreePatch)((Object)object), n3, specialTreeDefinition), 3);
        return true;
    }

    public final boolean handleSpecialTreeObject(int n, int n2) {
        Object object = SpecialTreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        SpecialTreeDefinition specialTreeDefinition = SpecialTreeDefinition.forSaplingId(this.treeIds[((SpecialTreePatch)object).getIndex()]);
        if (specialTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (specialTreeDefinition == SpecialTreeDefinition.SPIRIT_TREE && this.patchStates[((SpecialTreePatch)object).getIndex()] != 3) {
            object = this;
            DialogueManager.startDialogue(((SpecialTreePatchManager)object).player, 3636);
            return true;
        }
        if (this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("Not enough space in your inventory.");
            return true;
        }
        if (specialTreeDefinition == SpecialTreeDefinition.CALQUAT && this.player.getPlayerRights() < 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This feature is currently disabled.");
            return true;
        }
        this.player.getUpdateState().setAnimation(832);
        this.player.setActionLocked(true);
        int n3 = this.player.nextActionSequence();
        this.player.setActiveCycleEvent(new SpecialTreeHarvestTask(this, n3, (SpecialTreePatch)((Object)object), specialTreeDefinition));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 2);
        return true;
    }

    public final boolean compostPatch(int n, int n2, int n3) {
        if (n3 != 6032 && n3 != 6034) {
            return false;
        }
        SpecialTreePatch specialTreePatch = SpecialTreePatch.forPosition(new Position(n, n2));
        if (specialTreePatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.growthStages[specialTreePatch.getIndex()] != 3 || this.patchStates[specialTreePatch.getIndex()] == 5) {
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
        CycleEventHandler.getInstance().schedule(this.player, new SpecialTreeCompostTask(this, specialTreePatch, n3), 7);
        return true;
    }

    public final boolean inspectPatch(int n, int n2) {
        SpecialTreePatch specialTreePatch = SpecialTreePatch.forPosition(new Position(n, n2));
        if (specialTreePatch == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        SpecialTreeGrowthDefinition specialTreeGrowthDefinition = SpecialTreeGrowthDefinition.forTreeId(this.treeIds[specialTreePatch.getIndex()]);
        Object object = SpecialTreeDefinition.forSaplingId(this.treeIds[specialTreePatch.getIndex()]);
        if (this.patchStates[specialTreePatch.getIndex()] == 1) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[specialTreePatch.getIndex()] == 2) {
            this.player.getDialogueManager().showTwoLineStatement("This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
            return true;
        }
        if (this.patchStates[specialTreePatch.getIndex()] == 3) {
            this.player.getDialogueManager().showTwoLineStatement("This plant has fully grown. You can check it's health", "to gain some farming experiences.");
            return true;
        }
        if (this.growthStages[specialTreePatch.getIndex()] == 0) {
            this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has not been treated.", "The patch needs weeding.");
        } else if (this.growthStages[specialTreePatch.getIndex()] == 3) {
            if (this.diseaseChanceMultipliers[specialTreePatch.getIndex()] == 0.1) {
                this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has been treated with supercompost.", "The patch is empty and weeded.");
                return true;
            }
            if (this.diseaseChanceMultipliers[specialTreePatch.getIndex()] == 0.35) {
                this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has been treated with compost.", "The patch is empty and weeded.");
                return true;
            }
            this.player.getDialogueManager().showTwoLineStatement("This is one of the special patches. The soil has not been treated.", "The patch is empty and weeded.");
        } else if (specialTreeGrowthDefinition != null && object != null) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You bend down and start to inspect the patch...");
            this.player.getUpdateState().setAnimation(1331);
            this.player.setActionLocked(true);
            CycleEventHandler.getInstance().schedule(this.player, new SpecialTreeInspectTask(this, specialTreePatch, specialTreeGrowthDefinition), 5);
        }
        return true;
    }

    public final boolean openSkillGuide(int n, int n2) {
        Object object = SpecialTreePatch.forPosition(new Position(n, n2));
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
        Object object = SpecialTreePatch.forPosition(new Position(n, n2));
        if (object == null) {
            return false;
        }
        SpecialTreeDefinition specialTreeDefinition = SpecialTreeDefinition.forSaplingId(this.treeIds[((SpecialTreePatch)object).getIndex()]);
        if (specialTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((SpecialTreePatch)object).getIndex()] != 2) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be resurrected.");
            return true;
        }
        this.player.setPendingCropResurrectionTarget("special1", ((SpecialTreePatch)object).getIndex());
        return true;
    }

    public final boolean finishResurrection(boolean bl) {
        if (bl) {
            Object object = SpecialTreeDefinition.forSaplingId(this.treeIds[this.player.pendingCropResurrectionPatchIndex]);
            this.patchStates[this.player.pendingCropResurrectionPatchIndex] = 0;
            int n = this.growthStages[this.player.pendingCropResurrectionPatchIndex] - 4;
            this.lastUpdateTicks[this.player.pendingCropResurrectionPatchIndex] = Server.getElapsedMinutes() - (long)(((SpecialTreeDefinition)object).getGrowthCycleTicks() * n);
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
        Object object = SpecialTreePatch.forPosition(new Position(n, n2));
        if (object == null || n3 != 6036) {
            return false;
        }
        SpecialTreeDefinition specialTreeDefinition = SpecialTreeDefinition.forSaplingId(this.treeIds[((SpecialTreePatch)object).getIndex()]);
        if (specialTreeDefinition == null) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (this.patchStates[((SpecialTreePatch)object).getIndex()] != 1) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This plant doesn't need to be cured.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n3));
        this.player.getInventoryManager().addItem(new ItemStack(229));
        this.player.getUpdateState().setAnimation(2288);
        this.player.setActionLocked(true);
        this.patchStates[((SpecialTreePatch)object).getIndex()] = 0;
        CycleEventHandler.getInstance().schedule(this.player, new SpecialTreeCureTask(this), 7);
        return true;
    }

    private void resetPatch(int n) {
        this.treeIds[n] = 0;
        this.patchStates[n] = 0;
        this.diseaseChanceMultipliers[n] = 1.0;
        this.calquatRegrowthFlags[n] = false;
    }

    static /* synthetic */ Player getPlayer(SpecialTreePatchManager specialTreePatchManager) {
        return specialTreePatchManager.player;
    }

    static /* synthetic */ void resetPatch(SpecialTreePatchManager specialTreePatchManager, int n) {
        specialTreePatchManager.resetPatch(n);
    }
}

