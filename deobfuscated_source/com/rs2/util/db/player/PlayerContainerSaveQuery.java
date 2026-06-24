/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemStack;
import com.rs2.util.db.DatabaseQuery;
import com.rs2.util.db.player.PlayerSaveQueryFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class PlayerContainerSaveQuery
extends DatabaseQuery {
    private ItemContainer container;
    private int containerId;
    private /* synthetic */ PlayerSaveQueryFactory factory;

    public PlayerContainerSaveQuery(PlayerSaveQueryFactory playerSaveQueryFactory, ItemContainer itemContainer, int n) {
        super(PlayerSaveQueryFactory.getContainerSaveSql(n, itemContainer.g()));
        this.factory = playerSaveQueryFactory;
        this.container = itemContainer;
        this.containerId = n;
    }

    @Override
    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {
        int n = 1;
        int n2 = 0;
        while (n2 < this.container.g()) {
            ItemStack itemStack = this.container.getItemAt(n2);
            preparedStatement.setInt(n++, this.containerId << 28 | n2 << 18 | PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
            preparedStatement.setInt(n++, this.containerId);
            preparedStatement.setInt(n++, PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
            if (itemStack == null) {
                preparedStatement.setInt(n++, -1);
                preparedStatement.setInt(n++, 0);
                preparedStatement.setInt(n++, n2);
                preparedStatement.setInt(n++, 0);
            } else {
                preparedStatement.setInt(n++, itemStack.getId());
                preparedStatement.setInt(n++, itemStack.getAmount());
                preparedStatement.setInt(n++, n2);
                preparedStatement.setInt(n++, itemStack.getMetadata());
            }
            ++n2;
        }
        preparedStatement.executeUpdate();
        return null;
    }
}

