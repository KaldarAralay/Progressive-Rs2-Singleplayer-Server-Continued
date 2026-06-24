/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.Server;
import com.rs2.model.World;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.FarmingPatchUtils;
import com.rs2.model.task.TickTask;

public final class MinuteMaintenanceTickTask
extends TickTask {
    public MinuteMaintenanceTickTask(Server server, int n) {
        super(100);
    }

    @Override
    public final void execute() {
        Server.setElapsedMinutes(Server.getElapsedMinutes() + 1L);
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Object object = playerArray[n2];
            if (object != null) {
                int n3;
                ((Player)object).getCompostBinManager().processRotting();
                ((Player)object).getAllotmentPatchManager().processGrowth();
                ((Player)object).getFlowerPatchManager().processGrowth();
                ((Player)object).getHerbPatchManager().processGrowth();
                ((Player)object).getHopsPatchManager().processGrowth();
                ((Player)object).getBushPatchManager().processGrowth();
                ((Player)object).getTreePatchManager().processGrowth();
                ((Player)object).getFruitTreePatchManager().processGrowth();
                ((Player)object).getSpecialTreePatchManager().processGrowth();
                ((Player)object).getSpecialCropPatchManager().processGrowth();
                Player player = (Player)object;
                object = ItemService.getInstance();
                int n4 = 0;
                while (n4 < 28) {
                    ItemStack itemStack = player.getInventoryManager().getContainer().getItemAt(n4);
                    if (itemStack != null && itemStack.getMetadata() >= 0 && itemStack.getDefinition().getEquipmentSlot() == -1 && !ItemService.isEssencePouch(itemStack.getId())) {
                        itemStack.setMetadata(itemStack.getMetadata() - 1);
                        if (itemStack.getMetadata() == 0) {
                            int[] nArray = FarmingPatchUtils.wateredSeedlingItemIds;
                            n3 = 0;
                            while (n3 < 14) {
                                int n5 = nArray[n3];
                                if (n5 == itemStack.getId()) {
                                    player.getPlantPotHandler().finishInventorySeedlingGrowth(itemStack.getId());
                                }
                                ++n3;
                            }
                            if (itemStack.getId() == 1995) {
                                player.getWineFermentationHandler().finishInventoryWineFermentation(n4);
                            }
                        }
                    }
                    ++n4;
                }
                n4 = 0;
                while (n4 < player.getBankContainer().getTabCount()) {
                    int n6 = 0;
                    while (n6 < 288) {
                        ItemStack itemStack = player.getBankContainer().getItemAtTabSlot(n6, n4);
                        if (itemStack != null && itemStack.getMetadata() >= 0 && itemStack.getDefinition().getEquipmentSlot() == -1 && !ItemService.isEssencePouch(itemStack.getId())) {
                            itemStack.setMetadata(itemStack.getMetadata() - 1);
                            if (itemStack.getMetadata() == 0) {
                                int[] nArray = FarmingPatchUtils.wateredSeedlingItemIds;
                                int n7 = 0;
                                while (n7 < 14) {
                                    n3 = nArray[n7];
                                    if (n3 == itemStack.getId()) {
                                        player.getPlantPotHandler().finishBankSeedlingGrowth(itemStack.getId(), n6, n4);
                                    }
                                    ++n7;
                                }
                                if (itemStack.getId() == 1995) {
                                    player.getWineFermentationHandler().finishBankWineFermentation(n6);
                                }
                            }
                        }
                        ++n6;
                    }
                    ++n4;
                }
            }
            ++n2;
        }
    }
}

