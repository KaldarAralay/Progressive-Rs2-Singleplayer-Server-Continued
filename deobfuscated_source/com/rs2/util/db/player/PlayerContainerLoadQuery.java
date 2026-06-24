/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.util.db.DatabaseQuery;
import com.rs2.util.db.player.PlayerLoadQueryFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class PlayerContainerLoadQuery
extends DatabaseQuery {
    private /* synthetic */ PlayerLoadQueryFactory factory;
    private final /* synthetic */ int containerId;

    public PlayerContainerLoadQuery(PlayerLoadQueryFactory playerLoadQueryFactory, String string, int n) {
        super(string);
        this.factory = playerLoadQueryFactory;
        this.containerId = n;
    }

    @Override
    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {
        preparedStatement.setInt(1, PlayerLoadQueryFactory.getPlayer(this.factory).getReferenceId());
        preparedStatement.setInt(2, this.containerId);
        return preparedStatement.executeQuery();
    }
}

