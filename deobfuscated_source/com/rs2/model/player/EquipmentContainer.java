/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemContainerType;
import com.rs2.model.player.EquipmentManager;

public final class EquipmentContainer
extends ItemContainer {
    private /* synthetic */ EquipmentManager a;

    public EquipmentContainer(EquipmentManager equipmentManager, ItemContainerType itemContainerType, int n) {
        super(itemContainerType, 14);
        this.a = equipmentManager;
    }

    public final void i() {
        super.clear();
        EquipmentManager.getPlayer(this.a).getEquipmentManager().refreshCarriedValue();
    }
}

