/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.BattlestaffRecipe;

public final class BattlestaffCraftingHandler {
    public static int BASE_BATTLESTAFF_ITEM_ID = 1391;

    public static boolean handleBattlestaffCrafting(Player player, int n, int n2) {
        if (n != BASE_BATTLESTAFF_ITEM_ID && n2 != BASE_BATTLESTAFF_ITEM_ID) {
            return false;
        }
        BattlestaffRecipe battlestaffRecipe = BattlestaffRecipe.forOrbItemId(n = n != BASE_BATTLESTAFF_ITEM_ID ? n : n2);
        if (battlestaffRecipe != null) {
            if (!ServerSettings.craftingEnabled) {
                player.packetSender.sendGameMessage("This skill is currently disabled.");
                return true;
            }
            if (!player.getInventoryManager().getContainer().containsItem(BASE_BATTLESTAFF_ITEM_ID)) {
                return true;
            }
            if (player.getSkillManager().getCurrentLevels()[12] < battlestaffRecipe.getRequiredLevel()) {
                player.getDialogueManager().showOneLineStatement("You need a crafting level of " + battlestaffRecipe.getRequiredLevel() + " to craft this staff.");
                return true;
            }
            if (player.getInventoryManager().containsItem(BASE_BATTLESTAFF_ITEM_ID) && player.getInventoryManager().containsItem(battlestaffRecipe.getOrbItemId())) {
                player.getInventoryManager().removeItem(new ItemStack(BASE_BATTLESTAFF_ITEM_ID, 1));
                player.getInventoryManager().removeItem(new ItemStack(battlestaffRecipe.getOrbItemId(), 1));
                player.getInventoryManager().addItem(new ItemStack(battlestaffRecipe.getBattlestaffItemId(), 1));
                player.getSkillManager().addExperience(12, battlestaffRecipe.getExperience());
            }
            return true;
        }
        return false;
    }
}

