/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill;

import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.GatheringToolComparator;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationRecipe;
import com.rs2.model.skill.SkillManager;
import com.rs2.util.RectangularArea;
import java.util.ArrayList;
import java.util.Collections;

public class ItemCombinationHandler {
    private Player player;
    private RectangularArea caveLightShortcutArea = new RectangularArea(2638, 9736, 2655, 9745, 0);

    public ItemCombinationHandler(Player player) {
        this.player = player;
    }

    public final boolean handleItemCombination(ItemStack firstItem, ItemStack secondItem) {
        ItemCombinationRecipe recipe = ItemCombinationRecipe.forItemIds(firstItem.getId(), secondItem.getId());
        if (recipe == null) {
            return false;
        }
        int[] skillRequirement = ItemCombinationRecipe.getSkillRequirement(recipe);
        if (skillRequirement != null && this.player.getSkillManager().getCurrentLevels()[skillRequirement[0]] < skillRequirement[1]) {
            this.player.packetSender.sendGameMessage("Your " + SkillManager.SKILL_NAMES[skillRequirement[0]].toLowerCase() + " level is not high enough to do this.");
            return true;
        }
        ItemStack[] requiredItems = ItemCombinationRecipe.getRequiredItems(recipe);
        if (requiredItems != null) {
            int n = 0;
            while (n < requiredItems.length) {
                ItemStack itemStack = requiredItems[n];
                if (this.player.getInventoryManager().getItemAmount(itemStack.getId()) < itemStack.getAmount()) {
                    return true;
                }
                ++n;
            }
            n = 0;
            while (n < requiredItems.length) {
                this.player.getInventoryManager().removeItem(requiredItems[n]);
                ++n;
            }
        }
        if (ItemCombinationRecipe.getMessage(recipe) != null) {
            this.player.packetSender.sendGameMessage(ItemCombinationRecipe.getMessage(recipe));
        }
        ItemStack[] productItems = ItemCombinationRecipe.getProductItems(recipe);
        if (productItems != null) {
            if (productItems.length == 2 && requiredItems != null && requiredItems.length == 2) {
                this.player.getInventoryManager().addItem(productItems[0]);
                this.player.getInventoryManager().addItem(productItems[1]);
            } else {
                int n = 0;
                while (n < productItems.length) {
                    this.player.getInventoryManager().addItem(productItems[n]);
                    ++n;
                }
                if (this.player.getActiveCaveLightLevel() > 0 && this.caveLightShortcutArea.containsExclusive(this.player.getPosition())) {
                    this.player.packetSender.sendGameMessage("The light lets you see further into the room.");
                    this.player.moveTo(new Position(this.player.getPosition().getX(), this.player.getPosition().getY() + 23, 0));
                }
            }
        }
        if (ItemCombinationRecipe.getAnimationId(recipe) > 0) {
            this.player.getUpdateState().setAnimation(ItemCombinationRecipe.getAnimationId(recipe));
        }
        if (skillRequirement != null) {
            this.player.getSkillManager().addExperience(skillRequirement[0], ItemCombinationRecipe.getExperience(recipe));
        }
        return true;
    }

    public static boolean isGatheringToolItemId(int n) {
        GatheringToolDefinition[] gatheringToolDefinitionArray = GatheringToolDefinition.values();
        int n2 = gatheringToolDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            GatheringToolDefinition gatheringToolDefinition = gatheringToolDefinitionArray[n3];
            if (gatheringToolDefinition.getToolItemId() == n) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public static GatheringToolDefinition findUsableGatheringTool(Player player, int n) {
        GatheringToolDefinition[] gatheringToolDefinitionArray = GatheringToolDefinition.values();
        int n2 = gatheringToolDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            GatheringToolDefinition gatheringToolDefinition = gatheringToolDefinitionArray[n3];
            if (gatheringToolDefinition.getSkillId() == n && player.getSkillManager().getCurrentLevels()[n] >= gatheringToolDefinition.getRequiredLevel() && (player.getEquipmentManager().getItemIdAtSlot(3) == gatheringToolDefinition.getToolItemId() || player.getInventoryManager().containsItem(gatheringToolDefinition.getToolItemId()))) {
                return gatheringToolDefinition;
            }
            ++n3;
        }
        return null;
    }

    public static GatheringToolDefinition findOwnedGatheringTool(Player player, int n) {
        GatheringToolDefinition[] gatheringToolDefinitionArray = GatheringToolDefinition.values();
        int n2 = gatheringToolDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            GatheringToolDefinition gatheringToolDefinition = gatheringToolDefinitionArray[n3];
            if (gatheringToolDefinition.getSkillId() == n && player.getSkillManager().getCurrentLevels()[n] >= gatheringToolDefinition.getRequiredLevel() && player.ownsItem(gatheringToolDefinition.getToolItemId())) {
                return gatheringToolDefinition;
            }
            ++n3;
        }
        return null;
    }

    public static ArrayList getGatheringToolsForSkill(int n) {
        ArrayList<GatheringToolDefinition> arrayList = new ArrayList<GatheringToolDefinition>();
        GatheringToolDefinition[] gatheringToolDefinitionArray = GatheringToolDefinition.values();
        int n2 = gatheringToolDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            GatheringToolDefinition gatheringToolDefinition = gatheringToolDefinitionArray[n3];
            if (gatheringToolDefinition.getSkillId() == n) {
                arrayList.add(gatheringToolDefinition);
            }
            ++n3;
        }
        Collections.sort(arrayList, new GatheringToolComparator(n));
        return arrayList;
    }

    public static GatheringToolDefinition getOwnedOrFallbackGatheringTool(Player player, int n) {
        GatheringToolDefinition ownedTool = ItemCombinationHandler.findOwnedGatheringTool(player, n);
        if (ownedTool != null) {
            return ownedTool;
        }
        ArrayList tools = ItemCombinationHandler.getGatheringToolsForSkill(n);
        return (GatheringToolDefinition)tools.get(0);
    }

    public static GatheringToolDefinition forBrokenToolItemId(int n) {
        GatheringToolDefinition[] gatheringToolDefinitionArray = GatheringToolDefinition.values();
        int n2 = gatheringToolDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            GatheringToolDefinition gatheringToolDefinition = gatheringToolDefinitionArray[n3];
            if (gatheringToolDefinition.getBrokenToolItemId() == n) {
                return gatheringToolDefinition;
            }
            ++n3;
        }
        return null;
    }

    public static boolean handleToolHeadAttachment(Player player, int n, int n2) {
        GatheringToolDefinition[] gatheringToolDefinitionArray = GatheringToolDefinition.values();
        int n3 = gatheringToolDefinitionArray.length;
        int n4 = 0;
        while (n4 < n3) {
            block3: {
                GatheringToolDefinition gatheringToolDefinition;
                block4: {
                    gatheringToolDefinition = gatheringToolDefinitionArray[n4];
                    if (gatheringToolDefinition.getToolHeadItemId() != n && gatheringToolDefinition.getToolHeadItemId() != n2) break block3;
                    GatheringToolDefinition gatheringToolDefinition2 = gatheringToolDefinition;
                    if (n == 0) break block4;
                    gatheringToolDefinition2 = gatheringToolDefinition;
                    if (n2 != 0) break block3;
                }
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                player.getInventoryManager().removeItem(new ItemStack(n2, 1));
                player.getInventoryManager().addItem(new ItemStack(gatheringToolDefinition.getToolItemId(), 1));
                return true;
            }
            ++n4;
        }
        return false;
    }

    public static void breakGatheringTool(Player player, int n) {
        GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.findUsableGatheringTool(player, n);
        if (player.getEquipmentManager().getItemIdAtSlot(3) == gatheringToolDefinition.getToolItemId()) {
            player.getEquipmentManager().replaceSlotItem(gatheringToolDefinition.getBrokenToolItemId(), 3);
            return;
        }
        if (player.getInventoryManager().containsItemAmount(gatheringToolDefinition.getToolItemId(), 1)) {
            player.getInventoryManager().removeItem(new ItemStack(gatheringToolDefinition.getToolItemId(), 1));
            player.getInventoryManager().addItem(new ItemStack(gatheringToolDefinition.getBrokenToolItemId(), 1));
        }
    }

    public static boolean repairBrokenGatheringTool(Player player, int n) {
        GatheringToolDefinition[] gatheringToolDefinitionArray = GatheringToolDefinition.values();
        int n2 = gatheringToolDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            GatheringToolDefinition gatheringToolDefinition = gatheringToolDefinitionArray[n3];
            if (gatheringToolDefinition.getBrokenToolItemId() == n) {
                if (!player.getInventoryManager().containsItemStack(new ItemStack(995, gatheringToolDefinition.getRepairCostCoins()))) {
                    player.packetSender.sendGameMessage("You don't have enough coins to fix that.");
                    return false;
                }
                player.getInventoryManager().removeItem(new ItemStack(n, 1));
                player.getInventoryManager().removeItem(new ItemStack(995, gatheringToolDefinition.getRepairCostCoins()));
                player.getInventoryManager().addItem(new ItemStack(gatheringToolDefinition.getToolItemId(), 1));
                return true;
            }
            ++n3;
        }
        return false;
    }
}

