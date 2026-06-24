/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.gameplay.duel.DuelSession;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import com.rs2.util.TextUtil;

public final class DuelInterfaceManager {
    private Player player;
    private int ruleConfigValue = 0;
    private static int[] confirmRuleTextIds = new int[]{8242, 8243, 8244, 8245, 8246, 8247, 8248, 8249, 8251, 8252, 8253};
    private static final int[] ruleConfigMasks = new int[]{1, 2, 16, 32, 64, 128, 256, 512, 1024, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 0x200000, 0x800000, 0x1000000, 0x4000000, 0x8000000, 0x10000000};

    public DuelInterfaceManager(Player player) {
        this.player = player;
    }

    public final void openDuelSetupInterface() {
        this.resetRules();
        Player player = this.player;
        player.packetSender.showInterface(6575);
        String string = "duel";
        player = this.player;
        this.player.interfaceAction = string;
        player = this.player;
        player.packetSender.sendInterfaceText("Dueling with:  " + this.player.getDuelSession().getOpponent().getUsername() + "  Opponent's combat level: " + TextUtil.formatCombatLevel(this.player.getCombatLevel(), this.player.getDuelSession().getOpponent().getCombatLevel()), 6671);
        player = this.player;
        player.packetSender.sendItemContainer(13824, this.player.getEquipmentManager().getContainer().getRawItems());
        this.refreshStakeContainers();
        this.refreshAcceptStatus();
    }

    public final void openDuelConfirmInterface() {
        Object object;
        block10: {
            Player player = this.player;
            player.packetSender.showInterface(6412);
            object = "duel2";
            player = this.player;
            this.player.interfaceAction = (String)object;
            this.player.getDuelController().setAccepted(false);
            object = this;
            player = ((DuelInterfaceManager)object).player;
            player.packetSender.sendInterfaceText("Hitpoints will be restored.", 8250);
            player = ((DuelInterfaceManager)object).player;
            player.packetSender.sendInterfaceText("Boosted stats will be restored.", 8238);
            int n = 11;
            while (n < ((DuelInterfaceManager)object).player.getDuelSession().getEnabledRules().length) {
                if (((DuelInterfaceManager)object).player.getDuelSession().getEnabledRules()[n]) {
                    player = ((DuelInterfaceManager)object).player;
                    player.packetSender.sendInterfaceText("Some worn items will be taken off", 8239);
                    break block10;
                }
                ++n;
            }
            player = ((DuelInterfaceManager)object).player;
            player.packetSender.sendInterfaceText("", 8240);
            player = ((DuelInterfaceManager)object).player;
            player.packetSender.sendInterfaceText("", 8241);
            int[] nArray = confirmRuleTextIds;
            int n2 = 0;
            while (n2 < 11) {
                n = nArray[n2];
                player = ((DuelInterfaceManager)object).player;
                player.packetSender.sendInterfaceText("", n);
                ++n2;
            }
        }
        object = this;
        String string = ((DuelInterfaceManager)object).player.getDuelSession().getStakedItems().size() <= 0 ? "Absolutely nothing!" : "";
        ItemStack[] itemStackArray = DuelSession.toItemArray(((DuelInterfaceManager)object).player.getDuelSession().getStakedItems());
        int n = itemStackArray.length;
        int n3 = 0;
        while (n3 < n) {
            ItemStack itemStack = itemStackArray[n3];
            if (itemStack != null) {
                string = itemStack.getDefinition().isStackable() || itemStack.getDefinition().isNote() ? String.valueOf(string) + itemStack.getDefinition().getName() + " x @cya@" + GameUtil.formatNumber(itemStack.getAmount()) + "\\n" : String.valueOf(string) + itemStack.getDefinition().getName() + "\\n";
            }
            ++n3;
        }
        Player player = ((DuelInterfaceManager)object).player;
        player.packetSender.sendInterfaceText(string, 6516);
        string = ((DuelInterfaceManager)object).player.getDuelSession().getOpponent().getDuelSession().getStakedItems().size() <= 0 ? "Absolutely nothing!" : "";
        itemStackArray = DuelSession.toItemArray(((DuelInterfaceManager)object).player.getDuelSession().getOpponent().getDuelSession().getStakedItems());
        n = itemStackArray.length;
        int n4 = 0;
        while (n4 < n) {
            ItemStack itemStack = itemStackArray[n4];
            if (itemStack != null) {
                string = itemStack.getDefinition().isStackable() || itemStack.getDefinition().isNote() ? String.valueOf(string) + itemStack.getDefinition().getName() + " x @cya@" + GameUtil.formatNumber(itemStack.getAmount()) + "\\n" : String.valueOf(string) + itemStack.getDefinition().getName() + "\\n";
            }
            ++n4;
        }
        Player player2 = ((DuelInterfaceManager)object).player;
        player2.packetSender.sendInterfaceText(string, 6517);
        if (this.player.getDuelSession().getRuleDescriptions().size() == 0) {
            player2 = this.player;
            player2.packetSender.sendInterfaceText("Everything will be allowed!", confirmRuleTextIds[0]);
        } else {
            int n5 = 0;
            while (n5 < this.player.getDuelSession().getRuleDescriptions().size()) {
                player2 = this.player;
                player2.packetSender.sendInterfaceText((String)this.player.getDuelSession().getRuleDescriptions().get(n5), confirmRuleTextIds[n5]);
                ++n5;
            }
        }
        this.refreshAcceptStatus();
    }

    public final void refreshAcceptStatus() {
        if (this.player.getDuelSession().getOpponent() == null || !this.player.getDuelSession().getOpponent().isRegistered() || this.player.getDuelSession().getOpponent().getDuelController() == null) {
            this.player.getDuelController().resetDuel(true);
            return;
        }
        Player player = this.player;
        if (player.interfaceAction == "duel") {
            if (this.player.getDuelSession().getOpponent().getDuelController().isAccepted()) {
                player = this.player;
                player.packetSender.sendInterfaceText("Other player accepted.", 6684);
                return;
            }
            if (this.player.getDuelController().isAccepted()) {
                player = this.player;
                player.packetSender.sendInterfaceText("Waiting for other player...", 6684);
                return;
            }
            player = this.player;
            player.packetSender.sendInterfaceText("", 6684);
            return;
        }
        player = this.player;
        if (player.interfaceAction == "duel2") {
            if (this.player.getDuelSession().getOpponent().getDuelController().isAccepted()) {
                player = this.player;
                player.packetSender.sendInterfaceText("Other player accepted.", 6571);
                return;
            }
            if (this.player.getDuelController().isAccepted()) {
                player = this.player;
                player.packetSender.sendInterfaceText("Waiting for other player...", 6571);
                return;
            }
            player = this.player;
            player.packetSender.sendInterfaceText("", 6571);
        }
    }

    private int countEquipmentItemsToRemove() {
        int n = 0;
        for (Object itemObject : this.player.getDuelSession().getEquipmentToRemove()) {
            ItemStack itemStack = (ItemStack)itemObject;
            if (itemStack.getId() <= 0) continue;
            ++n;
        }
        return n;
    }

    public final boolean hasInventorySpaceForDuel() {
        Player player = this.player.getDuelSession().getOpponent();
        int n = this.player.getDuelSession().getStakedItems().size() + player.getDuelSession().getStakedItems().size() + this.player.getDuelInterfaceManager().countEquipmentItemsToRemove();
        if (n > this.player.getInventoryManager().getContainer().getFreeSlots()) {
            player = this.player;
            player.packetSender.sendGameMessage("You or your opponent doesn't have enough spaces for that.");
            return false;
        }
        n = player.getDuelSession().getStakedItems().size() + this.player.getDuelSession().getStakedItems().size() + player.getDuelInterfaceManager().countEquipmentItemsToRemove();
        if (n > player.getInventoryManager().getContainer().getFreeSlots()) {
            player = this.player;
            player.packetSender.sendGameMessage("You or your opponent doesn't have enough spaces for that.");
            return false;
        }
        return true;
    }

    public final void refreshStakeContainers() {
        if (this.player.getDuelSession().getOpponent() == null || !this.player.getDuelSession().getOpponent().isRegistered()) {
            this.player.getDuelController().resetDuel(true);
            return;
        }
        Player player = this.player;
        player.packetSender.showInterfaceWithInventory(6575, 3321);
        player = this.player;
        player.packetSender.sendItemContainer(3322, this.player.getInventoryManager().getContainer().getRawItems());
        player = this.player;
        player.packetSender.sendItemContainer(6669, DuelSession.toItemArray(this.player.getDuelSession().getStakedItems()));
        player = this.player;
        player.packetSender.sendItemContainer(6670, DuelSession.toItemArray(this.player.getDuelSession().getOpponent().getDuelSession().getStakedItems()));
    }

    public final void toggleRule(int n, String string) {
        this.player.getDuelController().setAccepted(false);
        if (this.player.getDuelSession().getEnabledRules()[n]) {
            this.ruleConfigValue -= ruleConfigMasks[n];
            this.player.getDuelSession().getEnabledRules()[n] = false;
            if (string != null) {
                this.player.getDuelSession().getRuleDescriptions().remove(string);
            }
        } else {
            this.ruleConfigValue += ruleConfigMasks[n];
            this.player.getDuelSession().getEnabledRules()[n] = true;
            if (string != null) {
                this.player.getDuelSession().getRuleDescriptions().add(string);
            }
        }
        Player player = this.player;
        player.packetSender.sendConfig(286, this.ruleConfigValue);
        this.refreshAcceptStatus();
    }

    public final void resetRules() {
        int n = 0;
        while (n < this.player.getDuelSession().getEnabledRules().length) {
            if (this.player.getDuelSession().getEnabledRules()[n]) {
                this.ruleConfigValue -= ruleConfigMasks[n];
                this.player.getDuelSession().getEnabledRules()[n] = false;
            }
            ++n;
        }
        Player player = this.player;
        player.packetSender.sendConfig(286, this.ruleConfigValue);
        int n2 = 0;
        while (n2 < this.player.getDuelSession().getEnabledRules().length) {
            this.player.getDuelSession().getEnabledRules()[n2] = false;
            ++n2;
        }
    }
}

