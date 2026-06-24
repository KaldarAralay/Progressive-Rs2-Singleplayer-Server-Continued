/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.GemDefinition;

public final class GemCuttingHandler {
    public static int CHISEL_ITEM_ID = 1755;

    public static boolean handleGemCutting(Player player, int n, int n2) {
        if (n != CHISEL_ITEM_ID && n2 != CHISEL_ITEM_ID) {
            return false;
        }
        GemDefinition gemDefinition = GemDefinition.forUncutItemId(n = n != CHISEL_ITEM_ID ? n : n2);
        if (gemDefinition != null) {
            if (!ServerSettings.craftingEnabled) {
                Player player2 = player;
                player2.packetSender.sendGameMessage("This skill is currently disabled.");
                return true;
            }
            if (!player.getInventoryManager().getContainer().containsItem(CHISEL_ITEM_ID)) {
                return true;
            }
            if (player.getSkillManager().getCurrentLevels()[12] < gemDefinition.getRequiredLevel()) {
                player.getDialogueManager().showOneLineStatement("You need a crafting level of " + gemDefinition.getRequiredLevel() + " to cut this gem.");
                return true;
            }
            player.getUpdateState().setAnimation(gemDefinition.getAnimationId());
            Player player3 = player;
            player3.packetSender.sendSoundEffect(464, 1, 0);
            if (player.getInventoryManager().containsItem(CHISEL_ITEM_ID) && player.getInventoryManager().containsItem(n)) {
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                player.getInventoryManager().addItem(new ItemStack(gemDefinition.getCutItemId(), 1));
                player.getSkillManager().addExperience(12, gemDefinition.getExperience());
            }
            return true;
        }
        return false;
    }
}

