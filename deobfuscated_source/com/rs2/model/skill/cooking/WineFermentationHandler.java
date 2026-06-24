/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;

public final class WineFermentationHandler {
    private Player player;

    public WineFermentationHandler(Player player) {
        this.player = player;
    }

    public final void finishInventoryWineFermentation(int n) {
        if (!this.player.getInventoryManager().removeItemFromSlot(new ItemStack(1995), n)) {
            this.player.getInventoryManager().removeItem(new ItemStack(1995));
        }
        if (GameUtil.rollLevelScaledChance(48, 352, this.player.getSkillManager().getCurrentLevels()[7])) {
            this.player.getSkillManager().addExperience(7, 110.0);
            this.player.getInventoryManager().addItem(new ItemStack(1993));
            return;
        }
        this.player.getInventoryManager().addItem(new ItemStack(1991));
    }

    public final void finishBankWineFermentation(int n) {
        this.player.getBankContainer().removeFromSlot(new ItemStack(1995), n);
        if (GameUtil.rollLevelScaledChance(48, 352, this.player.getSkillManager().getCurrentLevels()[7])) {
            this.player.getSkillManager().addExperience(7, 110.0);
            this.player.getInventoryManager().addItem(new ItemStack(1993));
            return;
        }
        this.player.getInventoryManager().addItem(new ItemStack(1991));
    }
}

