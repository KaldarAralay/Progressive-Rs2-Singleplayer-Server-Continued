/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.item.ItemService;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;

public final class ItemSpellAction
extends MagicSpellAction {
    private final /* synthetic */ SpellDefinition itemSpell;
    private final /* synthetic */ int itemId;
    private final /* synthetic */ Player caster;
    private final /* synthetic */ int inventorySlot;

    public ItemSpellAction(Player player, SpellDefinition spellDefinition, SpellDefinition spellDefinition2, int n, Player player2, int n2) {
        super(player, spellDefinition, (byte)0);
        this.itemSpell = spellDefinition2;
        this.itemId = n;
        this.caster = player2;
        this.inventorySlot = n2;
    }

    @Override
    public final boolean prepareCast() {
        switch (this.itemSpell) {
            case LVL_1_ENCHANT: {
                return this.castEnchantJewelry(this.itemId, 0);
            }
            case LVL_2_ENCHANT: {
                return this.castEnchantJewelry(this.itemId, 1);
            }
            case LVL_3_ENCHANT: {
                return this.castEnchantJewelry(this.itemId, 2);
            }
            case LVL_4_ENCHANT: {
                return this.castEnchantJewelry(this.itemId, 3);
            }
            case LVL_5_ENCHANT: {
                return this.castEnchantJewelry(this.itemId, 4);
            }
            case LVL_6_ENCHANT: {
                return this.castEnchantJewelry(this.itemId, 5);
            }
            case LOW_LEVEL_ALCHEMY: {
                ItemService.getInstance();
                int n = ItemService.getPrice(this.itemId, "lowalch", 995);
                return ItemSpellAction.castAlchemyItem(this.caster, this.itemId, this.inventorySlot, n, 1200, this.itemSpell);
            }
            case HIGH_LEVEL_ALCHEMY: {
                ItemService.getInstance();
                int n = ItemService.getPrice(this.itemId, "highalch", 995);
                return ItemSpellAction.castAlchemyItem(this.caster, this.itemId, this.inventorySlot, n, 3000, this.itemSpell);
            }
            case SUPERHEAT_ITEM: {
                return this.castSuperheatItem(this.itemId);
            }
            case ay: {
                return this.castNecromancyReanimation(this.itemId, 0);
            }
            case az: {
                return this.castNecromancyReanimation(this.itemId, 1);
            }
            case aA: {
                return this.castNecromancyReanimation(this.itemId, 2);
            }
            case aB: {
                return this.castNecromancyReanimation(this.itemId, 3);
            }
            case aC: {
                return this.castNecromancyReanimation(this.itemId, 4);
            }
            case aD: {
                return this.castNecromancyReanimation(this.itemId, 5);
            }
            case aE: {
                return this.castNecromancyReanimation(this.itemId, 6);
            }
            case aF: {
                return this.castNecromancyReanimation(this.itemId, 7);
            }
            case aG: {
                return this.castNecromancyReanimation(this.itemId, 8);
            }
            case aH: {
                return this.castNecromancyReanimation(this.itemId, 9);
            }
            case aI: {
                return this.castNecromancyReanimation(this.itemId, 10);
            }
            case aJ: {
                return this.castNecromancyReanimation(this.itemId, 11);
            }
            case aK: {
                return this.castNecromancyReanimation(this.itemId, 12);
            }
            case aL: {
                return this.castNecromancyReanimation(this.itemId, 13);
            }
            case aM: {
                return this.castNecromancyReanimation(this.itemId, 14);
            }
            case aN: {
                return this.castNecromancyReanimation(this.itemId, 15);
            }
            case aO: {
                return this.castNecromancyReanimation(this.itemId, 16);
            }
            case aP: {
                return this.castNecromancyReanimation(this.itemId, 17);
            }
            case aQ: {
                return this.castNecromancyReanimation(this.itemId, 18);
            }
        }
        return false;
    }

    @Override
    public final void applyImpact(HitDefinition hitDefinition) {
    }
}

