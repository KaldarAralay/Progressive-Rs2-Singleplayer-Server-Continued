/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db;

import java.sql.ResultSet;

public interface DatabaseCallback {
    public void onResult(ResultSet var1) throws java.sql.SQLException;

    public void onException(Exception var1);
}

