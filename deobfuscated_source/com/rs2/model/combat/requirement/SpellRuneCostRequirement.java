/*
 * Source recovery overlay for CFR control-flow damage.
 */
package com.rs2.model.combat.requirement;

import com.rs2.model.Entity;
import com.rs2.model.gameplay.magetrainingarena.AlchemistPlaygroundController;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.SpellDefinition;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class SpellRuneCostRequirement
extends InventoryItemRequirement {
    private SpellDefinition spell;
    private ArrayList runeCosts = new ArrayList();
    private ArrayList combinationRuneCosts = new ArrayList();
    private ArrayList combinationRuneCredits = new ArrayList();

    public SpellRuneCostRequirement(SpellDefinition spellDefinition) {
        super(1, 1);
        this.spell = spellDefinition;
    }

    @Override
    public final void consume(Entity entity) {
        if (!entity.isPlayer()) {
            return;
        }
        super.consume(entity);
    }

    @Override
    final boolean isSatisfiedBy(Entity entity) {
        if (!entity.isPlayer()) {
            return true;
        }
        Player player = (Player)entity;
        ItemStack[] itemStackArray = (ItemStack[])this.spell.getRuneCosts().clone();
        if ((this.spell == SpellDefinition.LOW_LEVEL_ALCHEMY || this.spell == SpellDefinition.HIGH_LEVEL_ALCHEMY) && player.getAlchemistPlaygroundController().isInsidePlayground() && AlchemistPlaygroundController.currentFreeAlchemyItemId == player.temporaryActionValue) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new ItemStack(561, 0));
            this.runeCosts = arrayList;
        } else {
            this.runeCosts = this.buildRuneCosts(player, itemStackArray);
        }
        super.setRequiredItems(this.runeCosts);
        return super.isSatisfiedBy(entity);
    }

    private ArrayList buildRuneCosts(Player player, ItemStack[] itemStackArray) {
        ArrayList required = new ArrayList();
        ArrayList credits = new ArrayList();
        ArrayList removeIndexes = new ArrayList();
        ArrayList pendingIndexes = new ArrayList();
        int n = 0;
        while (n < itemStackArray.length) {
            ItemStack itemStack = itemStackArray[n];
            int runeId = itemStack.getId();
            int amount = itemStack.getAmount();
            if (!SpellRuneCostRequirement.hasStaffForRune(player, runeId)) {
                ItemStack flatItem = player.getInventoryManager().getContainer().findFlatItem(runeId);
                int flatAmount = 0;
                if (flatItem != null) {
                    flatAmount = flatItem.getAmount();
                }
                if (flatAmount >= amount) {
                    required.add(new ItemStack(runeId, amount));
                } else if (this.collectCombinationRuneCosts(player, runeId, amount)) {
                    if (required.size() != 0) {
                        int i = 0;
                        while (i < this.combinationRuneCosts.size()) {
                            ItemStack combinationCost = (ItemStack)this.combinationRuneCosts.get(i);
                            boolean merged = false;
                            Iterator iterator = required.iterator();
                            while (iterator.hasNext()) {
                                ItemStack requiredItem = (ItemStack)iterator.next();
                                if (requiredItem.getId() != combinationCost.getId() || requiredItem.getAmount() >= combinationCost.getAmount()) continue;
                                requiredItem.setAmount(combinationCost.getAmount());
                                merged = true;
                            }
                            if (!merged) {
                                pendingIndexes.add(Integer.valueOf(i));
                            }
                            ++i;
                        }
                        Iterator iterator = pendingIndexes.iterator();
                        while (iterator.hasNext()) {
                            int index = ((Integer)iterator.next()).intValue();
                            required.add((ItemStack)this.combinationRuneCosts.get(index));
                        }
                        pendingIndexes.clear();
                    } else {
                        required.addAll(this.combinationRuneCosts);
                    }
                    if (credits.size() != 0) {
                        int i = 0;
                        while (i < this.combinationRuneCredits.size()) {
                            ItemStack combinationCredit = (ItemStack)this.combinationRuneCredits.get(i);
                            boolean merged = false;
                            Iterator iterator = credits.iterator();
                            while (iterator.hasNext()) {
                                ItemStack credit = (ItemStack)iterator.next();
                                if (credit.getId() != combinationCredit.getId() || credit.getAmount() >= credit.getAmount()) continue;
                                credit.setAmount(credit.getAmount());
                                merged = true;
                            }
                            if (!merged) {
                                pendingIndexes.add(Integer.valueOf(i));
                            }
                            ++i;
                        }
                        Iterator iterator = pendingIndexes.iterator();
                        while (iterator.hasNext()) {
                            int index = ((Integer)iterator.next()).intValue();
                            credits.add((ItemStack)this.combinationRuneCredits.get(index));
                        }
                        pendingIndexes.clear();
                    } else {
                        credits.addAll(this.combinationRuneCredits);
                    }
                } else {
                    required.add(new ItemStack(runeId, amount));
                }
            }
            ++n;
        }
        int i = 0;
        while (i < required.size()) {
            ItemStack requiredItem = (ItemStack)required.get(i);
            Iterator iterator = credits.iterator();
            while (iterator.hasNext()) {
                ItemStack credit = (ItemStack)iterator.next();
                if (requiredItem.getId() != credit.getId()) continue;
                if (requiredItem.getAmount() <= credit.getAmount()) {
                    removeIndexes.add(Integer.valueOf(i));
                    continue;
                }
                requiredItem.setAmount(requiredItem.getAmount() - credit.getAmount());
            }
            ++i;
        }
        Iterator iterator = removeIndexes.iterator();
        while (iterator.hasNext()) {
            int index = ((Integer)iterator.next()).intValue();
            required.remove(index);
        }
        return required;
    }

    private static boolean hasStaffForRune(Player player, int n) {
        if (!player.isPlayer()) {
            return true;
        }
        ItemStack itemStack = player.getEquipmentManager().getContainer().getItemAt(3);
        if (itemStack == null) {
            return false;
        }
        String string = ItemDefinition.forId(itemStack.getId()).getName().toLowerCase();
        if (!string.contains("staff")) {
            return false;
        }
        switch (n) {
            case 554: {
                return string.contains("fire") || string.contains("lava") || string.contains("steam");
            }
            case 555: {
                return string.contains("water") || string.contains("mud") || string.contains("steam");
            }
            case 556: {
                return string.contains("air");
            }
            case 557: {
                return string.contains("earth") || string.contains("lava") || string.contains("mud");
            }
        }
        return false;
    }

    private static int getPairedCombinationRuneId(int n, int n2) {
        switch (n2) {
            case 554: {
                if (n == 4697) {
                    return 556;
                }
                if (n == 4694) {
                    return 555;
                }
                if (n != 4699) break;
                return 557;
            }
            case 555: {
                if (n == 4695) {
                    return 556;
                }
                if (n == 4694) {
                    return 554;
                }
                if (n != 4698) break;
                return 557;
            }
            case 556: {
                if (n == 4695) {
                    return 555;
                }
                if (n == 4696) {
                    return 557;
                }
                if (n != 4697) break;
                return 554;
            }
            case 557: {
                if (n == 4696) {
                    return 556;
                }
                if (n == 4698) {
                    return 555;
                }
                if (n != 4699) break;
                return 554;
            }
        }
        return -1;
    }

    private boolean collectCombinationRuneCosts(Player player, int n, int n2) {
        this.combinationRuneCosts.clear();
        this.combinationRuneCredits.clear();
        ArrayList matchingCombinationRunes = new ArrayList();
        ItemStack flatItem = player.getInventoryManager().getContainer().findFlatItem(n);
        int flatAmount = 0;
        if (flatItem != null) {
            flatAmount = flatItem.getAmount();
        }
        if (flatAmount >= n2) {
            return true;
        }
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int i = 0;
        while (i < itemStackArray.length) {
            ItemStack itemStack = itemStackArray[i];
            if (itemStack != null && SpellRuneCostRequirement.isCombinationRuneFor(itemStack.getId(), n)) {
                matchingCombinationRunes.add(itemStack);
            }
            ++i;
        }
        int combinationAmount = 0;
        Iterator iterator = matchingCombinationRunes.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = (ItemStack)iterator.next();
            combinationAmount += itemStack.getAmount();
        }
        if (matchingCombinationRunes.size() == 0) {
            return false;
        }
        if (flatAmount + combinationAmount < n2) {
            return false;
        }
        int remaining = n2;
        if (flatItem != null) {
            this.combinationRuneCosts.add(flatItem);
        }
        remaining -= flatAmount;
        int supplied = flatAmount;
        i = 0;
        while (i < matchingCombinationRunes.size() && remaining > 0) {
            ItemStack combinationRune = (ItemStack)matchingCombinationRunes.get(i);
            int usedAmount = combinationRune.getAmount();
            if (remaining >= usedAmount) {
                this.combinationRuneCosts.add(combinationRune);
            } else {
                usedAmount = remaining;
                this.combinationRuneCosts.add(new ItemStack(combinationRune.getId(), usedAmount));
            }
            this.combinationRuneCredits.add(new ItemStack(SpellRuneCostRequirement.getPairedCombinationRuneId(combinationRune.getId(), n), usedAmount));
            supplied += combinationRune.getAmount();
            remaining -= usedAmount;
            if (supplied >= n2) {
                return true;
            }
            ++i;
        }
        return supplied >= n2;
    }

    private static boolean isCombinationRuneFor(int n, int n2) {
        switch (n2) {
            case 554: {
                return n == 4697 || n == 4694 || n == 4699;
            }
            case 555: {
                return n == 4695 || n == 4694 || n == 4698;
            }
            case 556: {
                return n == 4695 || n == 4696 || n == 4697;
            }
            case 557: {
                return n == 4696 || n == 4698 || n == 4699;
            }
        }
        return false;
    }
}
