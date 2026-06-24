/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.farming;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.farming.AllotmentPatch;
import com.rs2.model.skill.farming.AllotmentPatchManager;
import com.rs2.model.skill.farming.BushPatch;
import com.rs2.model.skill.farming.BushPatchManager;
import com.rs2.model.skill.farming.FlowerPatch;
import com.rs2.model.skill.farming.FlowerPatchManager;
import com.rs2.model.skill.farming.FruitTreePatch;
import com.rs2.model.skill.farming.FruitTreePatchManager;
import com.rs2.model.skill.farming.HerbPatch;
import com.rs2.model.skill.farming.HerbPatchManager;
import com.rs2.model.skill.farming.HopsPatch;
import com.rs2.model.skill.farming.HopsPatchManager;
import com.rs2.model.skill.farming.SaplingDefinition;
import com.rs2.model.skill.farming.SpecialCropPatch;
import com.rs2.model.skill.farming.SpecialCropPatchManager;
import com.rs2.model.skill.farming.SpecialTreePatch;
import com.rs2.model.skill.farming.SpecialTreePatchManager;
import com.rs2.model.skill.farming.TreePatch;
import com.rs2.model.skill.farming.TreePatchManager;

public final class PlantPotHandler {
    private Player player;

    public PlantPotHandler(Player player) {
        this.player = player;
    }

    public final void finishInventorySeedlingGrowth(int n) {
        SaplingDefinition saplingDefinition = SaplingDefinition.forWateredSeedlingId(n);
        if (saplingDefinition == null) {
            return;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n));
        this.player.getInventoryManager().addItem(new ItemStack(saplingDefinition.getSaplingId()));
    }

    public final void finishBankSeedlingGrowth(int n, int n2, int n3) {
        SaplingDefinition saplingDefinition = SaplingDefinition.forWateredSeedlingId(n);
        if (saplingDefinition == null) {
            return;
        }
        this.player.getBankContainer().removeFromTab(new ItemStack(n), n2, n3);
        this.player.getBankContainer().addToTab(new ItemStack(saplingDefinition.getSaplingId()), n3);
    }

    public final boolean waterSeedling(int n, int n2) {
        SaplingDefinition saplingDefinition = SaplingDefinition.forSeedlingId(n);
        if (saplingDefinition == null) {
            saplingDefinition = SaplingDefinition.forSeedlingId(n2);
        }
        if (saplingDefinition == null || !new ItemStack(n).getDefinition().getName().toLowerCase().contains("watering") && !new ItemStack(n2).getDefinition().getName().toLowerCase().contains("watering")) {
            return false;
        }
        if (n >= 5333 && n <= 5340 && this.player.getInventoryManager().removeItem(new ItemStack(n))) {
            this.player.getInventoryManager().addItem(new ItemStack(n == 5333 ? n - 2 : n - 1));
        }
        if (n2 >= 5333 && n2 <= 5340 && this.player.getInventoryManager().removeItem(new ItemStack(n2))) {
            this.player.getInventoryManager().addItem(new ItemStack(n == 5333 ? n - 2 : n - 1));
        }
        Player player = this.player;
        player.packetSender.sendGameMessage("You water the " + new ItemStack(saplingDefinition.getSeedId()).getDefinition().getName().toLowerCase() + ".");
        this.player.getInventoryManager().removeItem(new ItemStack(saplingDefinition.getSeedlingId()));
        this.player.getInventoryManager().addItem(new ItemStack(saplingDefinition.getWateredSeedlingId()));
        return true;
    }

    public final boolean plantSeedInPot(int n, int n2, int n3, int n4) {
        SaplingDefinition saplingDefinition = SaplingDefinition.forSeedId(n);
        if (saplingDefinition == null) {
            saplingDefinition = SaplingDefinition.forSeedlingId(n2);
        }
        if (saplingDefinition == null || n != 5354 && n2 != 5354) {
            return false;
        }
        if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(saplingDefinition.getSeedId()), n == 5354 ? n3 : n4)) {
            this.player.getInventoryManager().setItemInSlot(new ItemStack(saplingDefinition.getSeedlingId()), n == 5354 ? n3 : n4);
        } else if (this.player.getInventoryManager().removeItem(new ItemStack(saplingDefinition.getSeedId()))) {
            this.player.getInventoryManager().addItem(new ItemStack(saplingDefinition.getSeedlingId()));
        }
        Player player = this.player;
        player.packetSender.sendGameMessage("You sow some maple tree seeds in the plantpots.");
        player = this.player;
        player.packetSender.sendGameMessage("They need watering before they will grow.");
        return true;
    }

    public final boolean fillPlantPotWithSoil(int n, int n2, int n3) {
        if (n != 5350) {
            return false;
        }
        if (!ServerSettings.farmingEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        int n4 = n3;
        int n5 = n2;
        Object object = this.player.getAllotmentPatchManager();
        AllotmentPatch allotmentPatch = AllotmentPatch.forPosition(new Position(n5, n4));
        if (!(allotmentPatch != null && ((AllotmentPatchManager)object).growthStages[allotmentPatch.getIndex()] == 3)) {
            n4 = n3;
            int n6 = n2;
            object = this.player.getBushPatchManager();
            BushPatch bushPatch = BushPatch.forPosition(new Position(n6, n4));
            if (!(bushPatch != null && ((BushPatchManager)object).growthStages[bushPatch.getIndex()] == 3)) {
                n4 = n3;
                int n7 = n2;
                object = this.player.getFlowerPatchManager();
                FlowerPatch flowerPatch = FlowerPatch.forPosition(new Position(n7, n4));
                if (!(flowerPatch != null && ((FlowerPatchManager)object).growthStages[flowerPatch.getIndex()] == 3)) {
                    n4 = n3;
                    int n8 = n2;
                    object = this.player.getFruitTreePatchManager();
                    FruitTreePatch fruitTreePatch = FruitTreePatch.forPosition(new Position(n8, n4));
                    if (!(fruitTreePatch != null && ((FruitTreePatchManager)object).growthStages[fruitTreePatch.getIndex()] == 3)) {
                        n4 = n3;
                        int n9 = n2;
                        object = this.player.getHerbPatchManager();
                        HerbPatch herbPatch = HerbPatch.forPosition(new Position(n9, n4));
                        if (!(herbPatch != null && ((HerbPatchManager)object).growthStages[herbPatch.getIndex()] == 3)) {
                            n4 = n3;
                            int n10 = n2;
                            object = this.player.getHopsPatchManager();
                            HopsPatch hopsPatch = HopsPatch.forPosition(new Position(n10, n4));
                            if (!(hopsPatch != null && ((HopsPatchManager)object).growthStages[hopsPatch.getIndex()] == 3)) {
                                n4 = n3;
                                int n11 = n2;
                                object = this.player.getTreePatchManager();
                                TreePatch treePatch = TreePatch.forPosition(new Position(n11, n4));
                                if (!(treePatch != null && ((TreePatchManager)object).growthStages[treePatch.getIndex()] == 3)) {
                                    n4 = n3;
                                    int n12 = n2;
                                    object = this.player.getSpecialTreePatchManager();
                                    SpecialTreePatch specialTreePatch = SpecialTreePatch.forPosition(new Position(n12, n4));
                                    if (!(specialTreePatch != null && ((SpecialTreePatchManager)object).growthStages[specialTreePatch.getIndex()] == 3)) {
                                        n4 = n3;
                                        int n13 = n2;
                                        object = this.player.getSpecialCropPatchManager();
                                        SpecialCropPatch specialCropPatch = SpecialCropPatch.forPosition(new Position(n13, n4));
                                        if (!(specialCropPatch != null && ((SpecialCropPatchManager)object).growthStages[specialCropPatch.getIndex()] == 3)) {
                                            Player player = this.player;
                                            player.packetSender.sendGameMessage("You can only fill your pot on raked patches.");
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.player.getInventoryManager().getContainer().containsItem(5325)) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You need a gardening trowel to fill this pot with soil.");
            return true;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(n));
        this.player.getUpdateState().setAnimation(2287);
        Player player = this.player;
        player.packetSender.sendGameMessage("You fill the empty plant pot with soil.");
        this.player.getInventoryManager().addItem(new ItemStack(5354));
        return true;
    }
}

