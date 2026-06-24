/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.util.db.DatabaseQuery;
import com.rs2.util.db.player.PlayerSaveQueryFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class PlayerContactsSaveQuery
extends DatabaseQuery {
    private /* synthetic */ PlayerSaveQueryFactory factory;

    public PlayerContactsSaveQuery(PlayerSaveQueryFactory playerSaveQueryFactory) {
        super(PlayerSaveQueryFactory.getContactsSaveSql());
        this.factory = playerSaveQueryFactory;
    }

    @Override
    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {
        int n = 1;
        long[] lArray = PlayerSaveQueryFactory.getPlayer(this.factory).getIgnoreList();
        int n2 = 0;
        while (n2 < lArray.length) {
            preparedStatement.setInt(n++, 0x40000000 | n2 << 24 | PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
            preparedStatement.setInt(n++, PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
            preparedStatement.setInt(n++, n2);
            preparedStatement.setLong(n++, lArray[n2]);
            preparedStatement.setBoolean(n++, true);
            ++n2;
        }
        long[] lArray2 = PlayerSaveQueryFactory.getPlayer(this.factory).getFriendsList();
        int n3 = 0;
        while (n3 < lArray2.length) {
            preparedStatement.setInt(n++, n3 << 24 | PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
            preparedStatement.setInt(n++, PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
            preparedStatement.setInt(n++, n3);
            preparedStatement.setLong(n++, lArray2[n3]);
            preparedStatement.setBoolean(n++, false);
            ++n3;
        }
        preparedStatement.executeUpdate();
        return null;
    }
}

