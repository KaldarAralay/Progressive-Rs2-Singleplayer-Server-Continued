#!/usr/bin/env python3
"""Repair known non-enum CFR `void` local artifacts in generated source."""

from __future__ import annotations

import sys
from pathlib import Path


def replace_exact(text: str, old: str, new: str, path: Path) -> tuple[str, int]:
    count = text.count(old)
    if count == 0:
        preview = old.strip().splitlines()[0][:120] if old.strip() else "<empty>"
        raise SystemExit(f"{path}: expected snippet not found: {preview!r}")
    return text.replace(old, new), count


def repair_bot_task_planner(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    repairs = 0
    text, count = replace_exact(text, "        void var4_22;\n", "        int var4_22 = 0;\n", path)
    repairs += count
    old = """        if (player.botCombatStyle == 0) {
            boolean bl5 = false;
        } else if (player.botCombatStyle == 2) {
            boolean bl6 = true;
        } else if (player.botCombatStyle == 1) {
            int n16 = 2;
        }
        if (!bl) {
            void var12_54;
            void var4_20;
            boolean bl7 = false;
            while (var4_20 < nArray4[var12_54].length) {
                int n17 = nArray4[var12_54][var4_20];
                if (n17 > 0) {
                    player.botCombatLoadoutItemIds.add(n17);
                }
                ++var4_20;
            }
        }
        boolean bl8 = false;
        while (var4_22 < 3) {
"""
    new = """        int selectedLoadoutIndex = 0;
        if (player.botCombatStyle == 2) {
            selectedLoadoutIndex = 1;
        } else if (player.botCombatStyle == 1) {
            selectedLoadoutIndex = 2;
        }
        if (!bl) {
            int selectedSlot = 0;
            while (selectedSlot < nArray4[selectedLoadoutIndex].length) {
                int n17 = nArray4[selectedLoadoutIndex][selectedSlot];
                if (n17 > 0) {
                    player.botCombatLoadoutItemIds.add(n17);
                }
                ++selectedSlot;
            }
        }
        while (var4_22 < 3) {
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count
    text = text.replace("if (var4_22 == false)", "if (var4_22 == 0)")
    text = text.replace("else if (var4_22 == true)", "else if (var4_22 == 1)")

    required_replacements = [
        (
            "            BotTaskDefinition botTaskDefinition = GameplayHelper.selectAvailableBotTask(player, object, false);\n",
            "            BotTaskDefinition botTaskDefinition = GameplayHelper.selectAvailableBotTask(player, (ArrayList)object, false);\n",
        ),
        (
            "        for (BotTaskDefinition botTaskDefinition2 : BotTaskDefinition.shopTasks) {\n",
            "        for (Object botTaskDefinitionObject : BotTaskDefinition.shopTasks) {\n"
            "            BotTaskDefinition botTaskDefinition2 = (BotTaskDefinition)botTaskDefinitionObject;\n",
        ),
        ("            n = object[n2];\n", "            n = ((int[])object)[n2];\n"),
        (
            "        AttackStyleDefinition[] attackStyleDefinitionArray = WeaponProfile.forItem(player.getEquipmentManager().getContainer().getItemAt(3));\n"
            "        attackStyleDefinitionArray = attackStyleDefinitionArray.getInterfaceDefinition().getAttackStyles();\n",
            "        WeaponProfile weaponProfile = WeaponProfile.forItem(player.getEquipmentManager().getContainer().getItemAt(3));\n"
            "        AttackStyleDefinition[] attackStyleDefinitionArray = weaponProfile.getInterfaceDefinition().getAttackStyles();\n",
        ),
        (
            "            int n;\n"
            "            player.botTaskItemId = ((ItemStack)arrayList.get(0)).getId();\n"
            "            int itemStackArray = 550;\n"
            "            if (player.botTaskItemId == 1739) {\n"
            "                n = 100;\n"
            "            }\n",
            "            int n = 550;\n"
            "            player.botTaskItemId = ((ItemStack)arrayList.get(0)).getId();\n"
            "            if (player.botTaskItemId == 1739) {\n"
            "                n = 100;\n"
            "            }\n",
        ),
        (
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n20, object = BotCombatLoadoutTables.post306BowIds, 4, itemDefinition != null)) {\n",
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n20, BotCombatLoadoutTables.post306BowIds, 4, itemDefinition != null)) {\n",
        ),
        (
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n21, object = BotCombatLoadoutTables.basicRangedVambraceIds, 4, itemDefinition != null)) {\n",
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n21, BotCombatLoadoutTables.basicRangedVambraceIds, 4, itemDefinition != null)) {\n",
        ),
        (
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n22, object = BotCombatLoadoutTables.basicRangedHeadIds, 4, itemDefinition != null)) {\n",
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n22, BotCombatLoadoutTables.basicRangedHeadIds, 4, itemDefinition != null)) {\n",
        ),
        (
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n23, object = BotCombatLoadoutTables.basicRangedLegIds, 4, itemDefinition != null)) {\n",
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n23, BotCombatLoadoutTables.basicRangedLegIds, 4, itemDefinition != null)) {\n",
        ),
        (
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n24, object = BotCombatLoadoutTables.basicRangedBodyIds, 4, itemDefinition != null)) {\n",
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n24, BotCombatLoadoutTables.basicRangedBodyIds, 4, itemDefinition != null)) {\n",
        ),
        (
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n25, object = BotCombatLoadoutTables.capeIds, 1, itemDefinition != null, true)) break block95;\n",
            "                            if (BotTaskPlanner.tryDeferForUpgradePurchase(player, n25, BotCombatLoadoutTables.capeIds, 1, itemDefinition != null, true)) break block95;\n",
        ),
        (
            "                SpellDefinition spellDefinition = object4[n7];\n",
            "                SpellDefinition spellDefinition = ((SpellDefinition[])object4)[n7];\n",
        ),
        ("        while (object5.hasNext()) {\n", "        while (((Iterator)object5).hasNext()) {\n"),
        (
            "            ItemStack itemStack5 = (ItemStack)object5.next();\n",
            "            ItemStack itemStack5 = (ItemStack)((Iterator)object5).next();\n",
        ),
        ("                        player.deferredBotTask = object;\n", "                        player.deferredBotTask = (BotTaskDefinition)object;\n"),
        ("                        player.currentBotTask = object;\n", "                        player.currentBotTask = (BotTaskDefinition)object;\n"),
        ("                    player.deferredBotTask = object2;\n", "                    player.deferredBotTask = (BotTaskDefinition)object2;\n"),
        ("                    player.currentBotTask = object2;\n", "                    player.currentBotTask = (BotTaskDefinition)object2;\n"),
        ("                player.deferredBotTask = object2;\n", "                player.deferredBotTask = (BotTaskDefinition)object2;\n"),
        ("                player.currentBotTask = object2;\n", "                player.currentBotTask = (BotTaskDefinition)object2;\n"),
        ("            int n5;\n            BotTaskDefinition botTaskDefinition;\n", "            int n5 = 0;\n            BotTaskDefinition botTaskDefinition;\n"),
        (
            "            for (ItemStack itemStack7 : arrayList) {\n"
            "                int n14;\n"
            "                if (!AmmunitionDefinition.isCompatible(player, itemStack6, itemStack7) || itemStack7.getDefinition().getValue() >= n14) continue;\n"
            "                n14 = itemStack7.getDefinition().getValue();\n"
            "                n13 = itemStack7.getId();\n"
            "            }\n",
            "            for (ItemStack itemStack7 : arrayList) {\n"
            "                if (!AmmunitionDefinition.isCompatible(player, itemStack6, itemStack7) || itemStack7.getDefinition().getValue() >= n12) continue;\n"
            "                n12 = itemStack7.getDefinition().getValue();\n"
            "                n13 = itemStack7.getId();\n"
            "            }\n",
        ),
    ]
    for old, new in required_replacements:
        count = text.count(old)
        if count == 0:
            preview = old.strip().splitlines()[0][:120] if old.strip() else "<empty>"
            raise SystemExit(f"{path}: expected snippet not found: {preview!r}")
        text = text.replace(old, new)
        repairs += count

    old = """    private static boolean tryDeferForUpgradePurchase(Player player, int n, int[] object, int n2, boolean bl, boolean n3) {
        if (n3 != 0) {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            int n4 = ((int[])object).length - 1;
            while (n4 >= 0) {
                arrayList.add(object[n4]);
                --n4;
            }
            Collections.shuffle(arrayList);
            object = new int[arrayList.size()];
            n4 = 0;
            while (n4 < arrayList.size()) {
                object[n4] = (Integer)arrayList.get(n4);
                ++n4;
            }
        }
        n3 = ((int[])object).length - 1;
        while (n3 >= 0) {
            int n5 = object[n3];
            if (player.getEquipmentManager().canEquipItem(n5)) {
                Object object2 = new ItemStack(n5, 1);
                object2 = ((ItemStack)object2).getDefinition();
                boolean bl2 = false;
                if (!bl || n < ((ItemDefinition)object2).getRequiredLevel(n2)) {
                    bl2 = true;
                }
                if (bl2) {
                    BotTaskDefinition botTaskDefinition = BotTaskPlanner.selectShopPurchaseTask(player, n5, 1);
                    if (botTaskDefinition != null) {
                        BotTaskPlanner.resetBotTaskGoals(player);
                        object = player.currentBotTask;
                        Player player2 = player;
                        player.deferredBotTask = (BotTaskDefinition)object;
                        object = botTaskDefinition;
                        player2 = player;
                        player.currentBotTask = (BotTaskDefinition)object;
                        return true;
                    }
                } else if (n >= ((ItemDefinition)object2).getRequiredLevel(n2) && bl) {
                    return false;
                }
            }
            --n3;
        }
        return false;
    }
"""
    new = """    private static boolean tryDeferForUpgradePurchase(Player player, int currentLevel, int[] itemIds, int skillId, boolean requireUpgrade, boolean shuffle) {
        if (shuffle) {
            ArrayList<Integer> shuffledIds = new ArrayList<Integer>();
            int index = itemIds.length - 1;
            while (index >= 0) {
                shuffledIds.add(itemIds[index]);
                --index;
            }
            Collections.shuffle(shuffledIds);
            itemIds = new int[shuffledIds.size()];
            index = 0;
            while (index < shuffledIds.size()) {
                itemIds[index] = (Integer)shuffledIds.get(index);
                ++index;
            }
        }
        int index = itemIds.length - 1;
        while (index >= 0) {
            int itemId = itemIds[index];
            if (player.getEquipmentManager().canEquipItem(itemId)) {
                ItemDefinition itemDefinition = new ItemStack(itemId, 1).getDefinition();
                boolean shouldDefer = false;
                if (!requireUpgrade || currentLevel < itemDefinition.getRequiredLevel(skillId)) {
                    shouldDefer = true;
                }
                if (shouldDefer) {
                    BotTaskDefinition botTaskDefinition = BotTaskPlanner.selectShopPurchaseTask(player, itemId, 1);
                    if (botTaskDefinition != null) {
                        BotTaskPlanner.resetBotTaskGoals(player);
                        player.deferredBotTask = player.currentBotTask;
                        player.currentBotTask = botTaskDefinition;
                        return true;
                    }
                } else if (currentLevel >= itemDefinition.getRequiredLevel(skillId) && requireUpgrade) {
                    return false;
                }
            }
            --index;
        }
        return false;
    }
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count

    old = """            if (BotTaskDefinition.miningTasks.contains(player.currentBotTask) || BotTaskDefinition.woodcuttingTasks.contains(player.currentBotTask) || BotTaskDefinition.runecraftingTasks.get(0) == player.currentBotTask) {
                int n = 14;
                if (BotTaskDefinition.woodcuttingTasks.contains(player.currentBotTask)) {
                    n = 8;
                }
                Object object = ItemCombinationHandler.findOwnedGatheringTool(player, n);
                int n2 = n;
                ArrayList arrayList = ItemCombinationHandler.getGatheringToolsForSkill(n2);
                int n3 = arrayList.size() - 1;
                while (n3 >= 0) {
                    Object object2 = (GatheringToolDefinition)((Object)arrayList.get(n3));
                    boolean bl = false;
                    if (object == null) {
                        bl = true;
                    } else if (n == 14) {
                        if (object2.getToolSpeed() < object.getToolSpeed()) {
                            bl = true;
                        }
                    } else if (object2.getToolSpeed() > object.getToolSpeed()) {
                        bl = true;
                    }
                    if (bl && (object2 = BotTaskPlanner.selectShopPurchaseTask(player, object2.getToolItemId(), 1)) != null) {
                        BotTaskPlanner.resetBotTaskGoals(player);
                        object = player.currentBotTask;
                        Player player2 = player;
                        player.deferredBotTask = (BotTaskDefinition)object;
                        object = object2;
                        player2 = player;
                        player.currentBotTask = (BotTaskDefinition)object;
                        return;
                    }
                    --n3;
                }
                return;
            }
"""
    new = """            if (BotTaskDefinition.miningTasks.contains(player.currentBotTask) || BotTaskDefinition.woodcuttingTasks.contains(player.currentBotTask) || BotTaskDefinition.runecraftingTasks.get(0) == player.currentBotTask) {
                int skillId = 14;
                if (BotTaskDefinition.woodcuttingTasks.contains(player.currentBotTask)) {
                    skillId = 8;
                }
                GatheringToolDefinition ownedTool = ItemCombinationHandler.findOwnedGatheringTool(player, skillId);
                ArrayList arrayList = ItemCombinationHandler.getGatheringToolsForSkill(skillId);
                int index = arrayList.size() - 1;
                while (index >= 0) {
                    GatheringToolDefinition toolDefinition = (GatheringToolDefinition)arrayList.get(index);
                    boolean shouldBuy = false;
                    if (ownedTool == null) {
                        shouldBuy = true;
                    } else if (skillId == 14) {
                        if (toolDefinition.getToolSpeed() < ownedTool.getToolSpeed()) {
                            shouldBuy = true;
                        }
                    } else if (toolDefinition.getToolSpeed() > ownedTool.getToolSpeed()) {
                        shouldBuy = true;
                    }
                    if (shouldBuy) {
                        BotTaskDefinition purchaseTask = BotTaskPlanner.selectShopPurchaseTask(player, toolDefinition.getToolItemId(), 1);
                        if (purchaseTask != null) {
                            BotTaskPlanner.resetBotTaskGoals(player);
                            player.deferredBotTask = player.currentBotTask;
                            player.currentBotTask = purchaseTask;
                            return;
                        }
                    }
                    --index;
                }
                return;
            }
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count

    old = """    public static boolean prepareCookingTaskRequirements(Player player) {
        Object object;
        Object object2;
        ArrayList<ItemStack[]> arrayList = new ArrayList<ItemStack[]>();
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            object2 = itemStackArray[n2];
            if (object2 != null && ((ItemDefinition)(object = object2.getDefinition())).getName().toLowerCase().startsWith("raw ") && (object = CookableFoodDefinition.forRawItemId(object2.getId())) != null && player.getSkillManager().getCurrentLevels()[7] >= ((CookableFoodDefinition)((Object)object)).getRequiredLevel()) {
                arrayList.add((ItemStack[])object2);
            }
            ++n2;
        }
        itemStackArray = player.getBankContainer().getItems();
        n = itemStackArray.length;
        n2 = 0;
        while (n2 < n) {
            object2 = itemStackArray[n2];
            if (object2 != null && ((ItemDefinition)(object = object2.getDefinition())).getName().toLowerCase().startsWith("raw ") && (object = CookableFoodDefinition.forRawItemId(object2.getId())) != null && player.getSkillManager().getCurrentLevels()[7] >= ((CookableFoodDefinition)((Object)object)).getRequiredLevel()) {
                arrayList.add((ItemStack[])object2);
            }
            ++n2;
        }
        if (arrayList.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)arrayList.get(0)).getId();
        object2 = new ItemStack[]{new ItemStack(player.botTaskItemId, 28)};
        player.botTaskRequiredItems = object2;
        return true;
    }
"""
    new = """    public static boolean prepareCookingTaskRequirements(Player player) {
        ArrayList<ItemStack> availableFood = new ArrayList<ItemStack>();
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                ItemDefinition itemDefinition = itemStack.getDefinition();
                CookableFoodDefinition foodDefinition = CookableFoodDefinition.forRawItemId(itemStack.getId());
                if (itemDefinition.getName().toLowerCase().startsWith("raw ") && foodDefinition != null && player.getSkillManager().getCurrentLevels()[7] >= foodDefinition.getRequiredLevel()) {
                    availableFood.add(itemStack);
                }
            }
            ++n2;
        }
        itemStackArray = player.getBankContainer().getItems();
        n = itemStackArray.length;
        n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                ItemDefinition itemDefinition = itemStack.getDefinition();
                CookableFoodDefinition foodDefinition = CookableFoodDefinition.forRawItemId(itemStack.getId());
                if (itemDefinition.getName().toLowerCase().startsWith("raw ") && foodDefinition != null && player.getSkillManager().getCurrentLevels()[7] >= foodDefinition.getRequiredLevel()) {
                    availableFood.add(itemStack);
                }
            }
            ++n2;
        }
        if (availableFood.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)availableFood.get(0)).getId();
        player.botTaskRequiredItems = new ItemStack[]{new ItemStack(player.botTaskItemId, 28)};
        return true;
    }
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count

    old = """    public static boolean prepareSpinningTaskRequirements(Player player) {
        ItemStack[] itemStackArray = new ArrayList();
        if (player.ownsItemAmount(1737, 28)) {
            itemStackArray.add(new ItemStack(1737, 28));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 10 && player.ownsItemAmount(1779, 28)) {
            itemStackArray.add(new ItemStack(1779, 28));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 19 && player.ownsItemAmount(6051, 28)) {
            itemStackArray.add(new ItemStack(6051, 28));
        }
        if (itemStackArray.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)itemStackArray.get(0)).getId();
        itemStackArray = new ItemStack[]{new ItemStack(player.botTaskItemId, 28)};
        player.botTaskRequiredItems = itemStackArray;
        return true;
    }
"""
    new = """    public static boolean prepareSpinningTaskRequirements(Player player) {
        ArrayList<ItemStack> availableItems = new ArrayList<ItemStack>();
        if (player.ownsItemAmount(1737, 28)) {
            availableItems.add(new ItemStack(1737, 28));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 10 && player.ownsItemAmount(1779, 28)) {
            availableItems.add(new ItemStack(1779, 28));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 19 && player.ownsItemAmount(6051, 28)) {
            availableItems.add(new ItemStack(6051, 28));
        }
        if (availableItems.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)availableItems.get(0)).getId();
        player.botTaskRequiredItems = new ItemStack[]{new ItemStack(player.botTaskItemId, 28)};
        return true;
    }
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count

    old = """    public static boolean prepareLeatherCraftingTaskRequirements(Player player) {
        ItemStack[] itemStackArray = new ArrayList();
        if (player.ownsItemAmount(1741, 100)) {
            itemStackArray.add(new ItemStack(1741, 100));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 28 && player.ownsItemAmount(1743, 100)) {
            itemStackArray.add(new ItemStack(1743, 100));
        }
        if (!ServerSettings.freeToPlayWorld && player.isMember()) {
            if (player.getSkillManager().getCurrentLevels()[12] >= 57 && player.ownsItemAmount(1745, 100)) {
                itemStackArray.add(new ItemStack(1745, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 66 && player.ownsItemAmount(2505, 100)) {
                itemStackArray.add(new ItemStack(2505, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 73 && player.ownsItemAmount(2507, 100)) {
                itemStackArray.add(new ItemStack(2507, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 79 && player.ownsItemAmount(2509, 100)) {
                itemStackArray.add(new ItemStack(2509, 100));
            }
        }
        if (itemStackArray.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)itemStackArray.get(0)).getId();
        itemStackArray = new ItemStack[]{new ItemStack(1733, 1), new ItemStack(1734, 26), new ItemStack(player.botTaskItemId, 26)};
        player.botTaskRequiredItems = itemStackArray;
        return true;
    }
"""
    new = """    public static boolean prepareLeatherCraftingTaskRequirements(Player player) {
        ArrayList<ItemStack> availableItems = new ArrayList<ItemStack>();
        if (player.ownsItemAmount(1741, 100)) {
            availableItems.add(new ItemStack(1741, 100));
        }
        if (player.getSkillManager().getCurrentLevels()[12] >= 28 && player.ownsItemAmount(1743, 100)) {
            availableItems.add(new ItemStack(1743, 100));
        }
        if (!ServerSettings.freeToPlayWorld && player.isMember()) {
            if (player.getSkillManager().getCurrentLevels()[12] >= 57 && player.ownsItemAmount(1745, 100)) {
                availableItems.add(new ItemStack(1745, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 66 && player.ownsItemAmount(2505, 100)) {
                availableItems.add(new ItemStack(2505, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 73 && player.ownsItemAmount(2507, 100)) {
                availableItems.add(new ItemStack(2507, 100));
            }
            if (player.getSkillManager().getCurrentLevels()[12] >= 79 && player.ownsItemAmount(2509, 100)) {
                availableItems.add(new ItemStack(2509, 100));
            }
        }
        if (availableItems.size() == 0) {
            return false;
        }
        player.botTaskItemId = ((ItemStack)availableItems.get(0)).getId();
        player.botTaskRequiredItems = new ItemStack[]{new ItemStack(1733, 1), new ItemStack(1734, 26), new ItemStack(player.botTaskItemId, 26)};
        return true;
    }
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_bot_pvp_target_search(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    repairs = 0
    text, count = replace_exact(text, "            void var3_12;\n", "            int var3_12 = 0;\n", path)
    repairs += count
    old = """                    if (((Player)object2).botPvpChatMessage.toLowerCase().equals("food") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food?") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food left") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food left?")) {
                        void n12;
                        object = ItemDefinition.forId(((Player)object2).botFoodItemId);
                        String string = ((ItemDefinition)object).getDisplayName();
                        n2 = ((Player)object2).getInventoryManager().getItemAmount(((Player)object2).botFoodItemId);
                        if (n2 > 1) {
                            String n7 = String.valueOf(string) + "s";
                        }
                        ((Player)object2).queuePublicChatMessage("I have " + n2 + " " + (String)n12 + " left");
"""
    new = """                    if (((Player)object2).botPvpChatMessage.toLowerCase().equals("food") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food?") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food left") || ((Player)object2).botPvpChatMessage.toLowerCase().equals("food left?")) {
                        object = ItemDefinition.forId(((Player)object2).botFoodItemId);
                        String string = ((ItemDefinition)object).getDisplayName();
                        n2 = ((Player)object2).getInventoryManager().getItemAmount(((Player)object2).botFoodItemId);
                        String foodName = string;
                        if (n2 > 1) {
                            foodName = String.valueOf(string) + "s";
                        }
                        ((Player)object2).queuePublicChatMessage("I have " + n2 + " " + foodName + " left");
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count
    old = """            if (!bl) {
                int n11;
                int n12;
                int n14 = this.bot.getPosition().getX() + GameUtil.randomInt(20) - 10;
                int n15 = this.bot.getPosition().getY() + GameUtil.randomInt(20) - 10;
                if (n14 < 2951) {
                    n12 = 2961;
                }
                if (n12 > 3376) {
                    n11 = 3366;
                }
                if (n15 < 3520) {
                    n15 = 3530;
                }
                if (n15 > this.bot.botWildernessMaxY) {
                    n15 = this.bot.botWildernessMaxY - 10;
                }
                PathFinder.getInstance();
                PathFinder.findPath(this.bot, n11, n15, true, 0, 0);
            }
"""
    new = """            if (!bl) {
                int n14 = this.bot.getPosition().getX() + GameUtil.randomInt(20) - 10;
                int n15 = this.bot.getPosition().getY() + GameUtil.randomInt(20) - 10;
                if (n14 < 2951) {
                    n14 = 2961;
                }
                if (n14 > 3376) {
                    n14 = 3366;
                }
                if (n15 < 3520) {
                    n15 = 3530;
                }
                if (n15 > this.bot.botWildernessMaxY) {
                    n15 = this.bot.botWildernessMaxY - 10;
                }
                PathFinder.getInstance();
                PathFinder.findPath(this.bot, n14, n15, true, 0, 0);
            }
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_bot_world_route_walker(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    old = """            if (arrayList.size() == 1) {
                void var12_17;
                object4 = (BotWorldRouteChoice)arrayList.get(0);
                object5 = object4;
                Position position2 = object4.route.getEndPosition();
                object5 = object4;
                if (((BotWorldRouteChoice)object5).reversed) {
                    object5 = object4;
                    Position position3 = ((BotWorldRouteChoice)object5).route.getStartPosition();
                }
                GameUtil.getDistance((Position)var12_17, (Position)object2);
            } else {
"""
    new = """            if (arrayList.size() == 1) {
                object4 = (BotWorldRouteChoice)arrayList.get(0);
                object5 = object4;
                Position position2 = object4.route.getEndPosition();
                object5 = object4;
                if (((BotWorldRouteChoice)object5).reversed) {
                    object5 = object4;
                    position2 = ((BotWorldRouteChoice)object5).route.getStartPosition();
                }
                if (GameUtil.getDistance(position2, (Position)object2) < n) {
                    arrayList2.add(object4);
                }
            } else {
"""
    count = text.count(old)
    if count:
        text = text.replace(old, new)
    path.write_text(text, encoding="utf-8")
    return count


def repair_agility(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    repairs = 0
    text, count = replace_exact(
        text,
        "        void var7_8;\n        int n8;\n",
        "        int n8 = n7;\n",
        path,
    )
    repairs += count
    text, count = replace_exact(text, "new AgilityMovementFinishTask(player, (int)var7_8, true, n9, n8, n10)", "new AgilityMovementFinishTask(player, n6, true, n9, n8, n10)", path)
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_double_door(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    old = """    private static void a(DoubleDoorHandler doubleDoorHandler, boolean bl) {
        void var1_20;
        void var1_8;
        boolean bl2 = false;
        int n = 0;
"""
    new = """    private static void a(DoubleDoorHandler doubleDoorHandler, boolean bl) {
        int var1_20 = doubleDoorHandler.currentOrientation;
        int var1_8 = 0;
        int n = 0;
"""
    text, repairs = replace_exact(text, old, new, path)
    replacements = [
        ("                int n2 = -1;", "                var1_8 = -1;"),
        ("                boolean bl3 = true;", "                var1_8 = 1;"),
        ("                int n3 = -1;", "                var1_8 = -1;"),
        ("                int n4 = -1;", "                var1_8 = -1;"),
        ("                int n5 = -1;", "                n = -1;"),
        ("                int n11 = 3;", "                var1_20 = 3;"),
        ("                boolean bl4 = false;", "                var1_20 = 0;"),
        ("                boolean bl5 = true;", "                var1_20 = 1;"),
        ("                boolean bl6 = false;", "                var1_20 = 0;"),
        ("                int n12 = doubleDoorHandler.originalOrientation;", "                var1_20 = doubleDoorHandler.originalOrientation;"),
        ("                boolean bl7 = true;", "                var1_20 = 1;"),
        ("                int n13 = 2;", "                var1_20 = 2;"),
        ("                boolean bl8 = true;", "                var1_20 = 1;"),
        ("                int n14 = 2;", "                var1_20 = 2;"),
        ("                int n15 = doubleDoorHandler.originalOrientation;", "                var1_20 = doubleDoorHandler.originalOrientation;"),
    ]
    for old_line, new_line in replacements:
        if old_line in text:
            text = text.replace(old_line, new_line, 1)
            repairs += 1
    simple_replacements = [
        ("        if (var1_8 != false || n != 0) {\n", "        if (var1_8 != 0 || n != 0) {\n"),
        ("    private static void b(DoubleDoorHandler doubleDoorHandler, boolean n) {\n        n = 0;\n", "    private static void b(DoubleDoorHandler doubleDoorHandler, boolean bl) {\n        int n = 0;\n"),
        ("        if (n || n2 != 0) {\n", "        if (n != 0 || n2 != 0) {\n"),
    ]
    for old_text, new_text in simple_replacements:
        count = text.count(old_text)
        if count:
            text = text.replace(old_text, new_text)
            repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_player(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    repairs = 0
    text, count = replace_exact(text, "                    void npc;\n", "                    int npc = 0;\n", path)
    repairs += count
    text, count = replace_exact(text, "                    Object var8_26 = null;\n", "                    var8_30 = null;\n", path)
    repairs += count
    text, count = replace_exact(
        text,
        """                        if (!bl && (position = this.d((Npc)object)) == null) {
                            continue;
                        }
                        break block40;
""",
        """                        if (!bl) {
                            position = this.d((Npc)object);
                            if (position == null) {
                                continue;
                            }
                            var8_30 = position;
                        }
                        break block40;
""",
        path,
    )
    repairs += count
    text, count = replace_exact(
        text,
        """                if (!bl) {
                    Position position = this.d((Npc)object);
                }
""",
        """                if (!bl) {
                    var8_30 = this.d((Npc)object);
                }
""",
        path,
    )
    repairs += count
    text, count = replace_exact(text, "            void var8_30;\n", "", path)
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_firemaking(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    old = """            void var9_19;
            void var3_7;
            int n6;
"""
    new = """            int var9_19 = n4;
            int var3_7 = n5;
            int n6 = n4;
"""
    text, repairs = replace_exact(text, old, new, path)
    text = text.replace("                    void player;\n                    boolean groundItem2;\n", "                    int player = n5;\n                    int groundItem2 = n3;\n")
    text = text.replace("ObjectManager.findDynamicObjectAt(groundItem2 ? 1 : 0, n6, (int)player)", "ObjectManager.findDynamicObjectAt(groundItem2, n6, player)")
    count = text.count("((FiremakingLog)((Object)object2)).getRequiredLevel()")
    text = text.replace("((FiremakingLog)((Object)object2)).getRequiredLevel()", "((FiremakingLog)object3).getRequiredLevel()")
    repairs += count
    text = text.replace("                    void v1 = groundItem2;\n                    groundItem2 = bl;\n                    var3_7 = player;\n                    var9_19 = n6;\n                    n7 = v1;\n                    firemakingLog = object2;\n", "                    boolean useGroundItem = bl;\n                    var3_7 = player;\n                    var9_19 = n6;\n                    n7 = groundItem2;\n                    firemakingLog = (FiremakingLog)object3;\n")
    text = text.replace("                    if (groundItem2) break block15;", "                    if (useGroundItem) break block15;")
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_isaac_cipher(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    repairs = 0
    text, count = replace_exact(
        text,
        "    private void initialize(boolean n) {\n",
        "    private void initialize(boolean seeded) {\n",
        path,
    )
    repairs += count
    text, count = replace_exact(
        text,
        "        int n9 = -1640531527;\n        n = 0;\n",
        "        int n9 = -1640531527;\n        int n = 0;\n",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_bot_login_batch_task(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    old = """    BotLoginBatchTask(int n, int n2) {
        this.finalBatchIndex = n2;
        super(n);
    }
"""
    new = """    BotLoginBatchTask(int n, int n2) {
        super(n);
        this.finalBatchIndex = n2;
    }
"""
    text, repairs = replace_exact(text, old, new, path)
    path.write_text(text, encoding="utf-8")
    return repairs


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_known_cfr_void_locals.py <source_root>", file=sys.stderr)
        return 2
    root = Path(sys.argv[1]).resolve()
    repairs = 0
    repairs += repair_bot_task_planner(root / "com/rs2/bot/BotTaskPlanner.java")
    repairs += repair_bot_pvp_target_search(root / "com/rs2/bot/combat/BotPvpTargetSearchTickTask.java")
    repairs += repair_bot_world_route_walker(root / "com/rs2/bot/route/BotWorldRouteWalker.java")
    repairs += repair_double_door(root / "com/rs2/model/objects/functions/DoubleDoorHandler.java")
    repairs += repair_agility(root / "com/rs2/model/skill/agility/AgilityObstacleHandler.java")
    repairs += repair_player(root / "com/rs2/model/player/Player.java")
    repairs += repair_firemaking(root / "com/rs2/model/skill/firemaking/FiremakingHandler.java")
    repairs += repair_isaac_cipher(root / "com/rs2/net/IsaacCipher.java")
    repairs += repair_bot_login_batch_task(root / "com/rs2/BotLoginBatchTask.java")
    print(f"repaired known CFR void locals: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
