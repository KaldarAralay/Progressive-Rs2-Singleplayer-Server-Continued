/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.path;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.path.PathResult;
import com.rs2.model.path.PathStep;
import com.rs2.model.path.PathStrategy;
import com.rs2.util.GameUtil;

public final class DirectPathStrategy
implements PathStrategy {
    @Override
    public final PathResult buildPath(Entity entity, Position position, boolean bl) {
        PathResult pathResult = new PathResult();
        Position currentPosition = entity.getPosition().copy();
        Position previousPosition = currentPosition.copy();
        int n = position.getX() - currentPosition.getX();
        int n2 = position.getY() - currentPosition.getY();
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        if (n < 0) {
            n3 = -1;
        } else if (n > 0) {
            n3 = 1;
        }
        if (n2 < 0) {
            n4 = -1;
        } else if (n2 > 0) {
            n4 = 1;
        }
        if (n < 0) {
            n5 = -1;
        } else if (n > 0) {
            n5 = 1;
        }
        int n7 = Math.abs(n);
        int n8 = Math.abs(n2);
        if (n7 <= n8) {
            n7 = Math.abs(n2);
            n8 = Math.abs(n);
            if (n2 < 0) {
                n6 = -1;
            } else if (n2 > 0) {
                n6 = 1;
            }
            n5 = 0;
        }
        n2 = n7 >> 1;
        n = 0;
        while (n <= n7) {
            n2 += n8;
            if (!previousPosition.equals(currentPosition) && bl && !GameUtil.hasClearPath(currentPosition, previousPosition, true)) {
                return pathResult;
            }
            previousPosition = currentPosition.copy();
            pathResult.getSteps().add(new PathStep(currentPosition.getX(), currentPosition.getY()));
            if (n2 >= n7) {
                n2 -= n7;
                currentPosition.setX(currentPosition.getX() + n3);
                currentPosition.setY(currentPosition.getY() + n4);
            } else {
                currentPosition.setX(currentPosition.getX() + n5);
                currentPosition.setY(currentPosition.getY() + n6);
            }
            ++n;
        }
        return pathResult;
    }

}

