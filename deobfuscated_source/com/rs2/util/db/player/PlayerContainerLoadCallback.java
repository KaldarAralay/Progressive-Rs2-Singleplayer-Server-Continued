/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemStack;
import com.rs2.util.db.DatabaseCallback;
import com.rs2.util.db.player.PlayerLoadQueryFactory;
import java.sql.ResultSet;

public final class PlayerContainerLoadCallback
implements DatabaseCallback {
    private ItemContainer container;

    public PlayerContainerLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory, ItemContainer itemContainer) {
        this.container = itemContainer;
    }

    @Override
    public final void onResult(ResultSet resultSet) throws java.sql.SQLException {
        while (resultSet.next()) {
            int n = resultSet.getInt("item_id");
            if (n == -1) continue;
            ItemStack itemStack = new ItemStack(n, resultSet.getInt("amount"), resultSet.getInt("timer"));
            this.container.add(itemStack, resultSet.getInt("slot"));
        }
    }

    @Override
    public final void onException(Exception exception) {
        exception.printStackTrace();
    }
}

