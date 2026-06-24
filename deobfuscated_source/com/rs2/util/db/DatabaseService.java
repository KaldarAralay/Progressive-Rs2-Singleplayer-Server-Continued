/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db;

import com.rs2.util.db.DatabaseCallback;
import com.rs2.util.db.DatabaseQuery;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DatabaseService {
    private static DatabaseService instance;
    private ExecutorService executor;
    private ThreadLocal threadContextLocal;
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseService(int n, String string, String string2, String string3, String string4) throws ClassNotFoundException {
        Class.forName(string);
        this.executor = Executors.newFixedThreadPool(8);
        this.threadContextLocal = new ThreadLocal();
        this.jdbcUrl = string2;
        this.username = string3;
        this.password = string4;
    }

    public final void submit(DatabaseQuery databaseQuery, DatabaseCallback databaseCallback) {
        databaseQuery.setThreadContextLocal(this.threadContextLocal);
        databaseQuery.setCallback(databaseCallback);
        databaseQuery.setService(this);
        this.executor.execute(databaseQuery);
    }

    protected final Connection openConnection() throws java.sql.SQLException {
        return DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
    }

    public static DatabaseService getInstance() {
        return instance;
    }

    public static void setInstance(DatabaseService databaseService) {
        instance = databaseService;
    }
}

