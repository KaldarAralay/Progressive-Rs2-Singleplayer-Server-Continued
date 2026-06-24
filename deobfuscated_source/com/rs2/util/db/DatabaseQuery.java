/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db;

import com.rs2.util.db.DatabaseCallback;
import com.rs2.util.db.DatabaseService;
import com.rs2.util.db.DatabaseThreadContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public abstract class DatabaseQuery
implements Runnable {
    private final String sql;
    private ThreadLocal threadContextLocal;
    private DatabaseCallback callback;
    private DatabaseService service;

    public DatabaseQuery(String string) {
        this.sql = string;
    }

    public abstract ResultSet executeStatement(PreparedStatement var1) throws SQLException;

    @Override
    public void run() {
        block8: {
            try {
                PreparedStatement preparedStatement;
                Connection connection;
                Object object = (DatabaseThreadContext)this.threadContextLocal.get();
                if (object == null) {
                    object = new DatabaseThreadContext(this);
                    this.threadContextLocal.set(object);
                }
                if ((connection = DatabaseThreadContext.getConnection((DatabaseThreadContext)object)) == null || connection.isClosed()) {
                    connection = this.service.openConnection();
                    DatabaseThreadContext.setConnection((DatabaseThreadContext)object, connection);
                }
                if (DatabaseThreadContext.getPreparedStatements((DatabaseThreadContext)object) == null) {
                    DatabaseThreadContext.setPreparedStatements((DatabaseThreadContext)object, new HashMap());
                }
                if ((preparedStatement = (PreparedStatement)DatabaseThreadContext.getPreparedStatements((DatabaseThreadContext)object).get(this.sql)) == null) {
                    preparedStatement = connection.prepareStatement(this.sql);
                    DatabaseThreadContext.getPreparedStatements((DatabaseThreadContext)object).put(this.sql, preparedStatement);
                }
                preparedStatement.setQueryTimeout(5);
                object = this.executeStatement(preparedStatement);
                if (this.callback != null) {
                    this.callback.onResult((ResultSet)object);
                }
                if (object != null) {
                    ((ResultSet)object).close();
                    return;
                }
            }
            catch (SQLException sQLException) {
                if (this.callback == null) break block8;
                this.callback.onException(sQLException);
            }
        }
    }

    protected final void setThreadContextLocal(ThreadLocal threadLocal) {
        this.threadContextLocal = threadLocal;
    }

    protected final void setService(DatabaseService databaseService) {
        this.service = databaseService;
    }

    protected final void setCallback(DatabaseCallback databaseCallback) {
        this.callback = databaseCallback;
    }
}

