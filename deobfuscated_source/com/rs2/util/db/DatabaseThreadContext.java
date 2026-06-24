/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db;

import com.rs2.util.db.DatabaseQuery;
import java.sql.Connection;
import java.util.Map;

public final class DatabaseThreadContext {
    private Connection connection;
    private Map preparedStatements;

    public DatabaseThreadContext(DatabaseQuery databaseQuery) {
    }

    static /* synthetic */ Connection getConnection(DatabaseThreadContext databaseThreadContext) {
        return databaseThreadContext.connection;
    }

    static /* synthetic */ void setConnection(DatabaseThreadContext databaseThreadContext, Connection connection) {
        databaseThreadContext.connection = connection;
    }

    static /* synthetic */ Map getPreparedStatements(DatabaseThreadContext databaseThreadContext) {
        return databaseThreadContext.preparedStatements;
    }

    static /* synthetic */ void setPreparedStatements(DatabaseThreadContext databaseThreadContext, Map map) {
        databaseThreadContext.preparedStatements = map;
    }
}

