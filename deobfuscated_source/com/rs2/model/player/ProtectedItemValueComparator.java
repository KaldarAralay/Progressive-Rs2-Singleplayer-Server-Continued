/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import java.util.Comparator;

public final class ProtectedItemValueComparator
implements Comparator {
    public ProtectedItemValueComparator(Player player) {
    }

    public final /* synthetic */ int compare(Object object, Object object2) {
        object2 = (ItemStack)object2;
        object = (ItemStack)object;
        return ItemDefinition.forId(((ItemStack)object2).getId()).getHighAlchemyValue() - ItemDefinition.forId(((ItemStack)object).getId()).getHighAlchemyValue();
    }
}

