/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.path;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.path.PathResult;

public interface PathStrategy {
    public PathResult buildPath(Entity var1, Position var2, boolean var3);
}

