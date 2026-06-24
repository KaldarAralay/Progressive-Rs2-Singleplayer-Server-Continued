/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import com.rs2.model.player.Player;
import com.rs2.util.db.DatabaseQuery;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class PlayerUidLookupQuery
extends DatabaseQuery {
    private final /* synthetic */ Player player;

    public PlayerUidLookupQuery(String string, Player player) {
        super(string);
        this.player = player;
    }

    @Override
    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {
        preparedStatement.setString(1, this.player.getUsername().toLowerCase());
        return preparedStatement.executeQuery();
    }
}

