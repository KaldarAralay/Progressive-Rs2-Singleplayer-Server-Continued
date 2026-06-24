/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.util.db.DatabaseCallback;
import com.rs2.util.db.player.PlayerLoadQueryFactory;
import java.sql.ResultSet;

public final class PlayerContactsLoadCallback
implements DatabaseCallback {
    private /* synthetic */ PlayerLoadQueryFactory factory;

    public PlayerContactsLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory) {
        this.factory = playerLoadQueryFactory;
    }

    @Override
    public final void onResult(ResultSet resultSet) throws java.sql.SQLException {
        while (resultSet.next()) {
            long l = resultSet.getLong("contact");
            int n = resultSet.getInt("slot");
            if (resultSet.getBoolean("ignore")) {
                PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getIgnoreList()[n] = l;
                continue;
            }
            PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getFriendsList()[n] = l;
        }
    }

    @Override
    public final void onException(Exception exception) {
        exception.printStackTrace();
    }
}

