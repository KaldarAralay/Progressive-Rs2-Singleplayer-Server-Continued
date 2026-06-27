/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.gameplay.PositionRange;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import com.rs2.util.path.WalkingCollisionMap;

public final class DuelArenaLocationManager {
    private static PositionRange duelLobbyRange = new PositionRange(new Position(3356, 3269, 0), new Position(3379, 3280, 0));
    private static Position[] duelLobbyAnchorPositions = new Position[]{new Position(3358, 3271, 0), new Position(3362, 3277, 0), new Position(3365, 3270, 0), new Position(3368, 3278, 0), new Position(3372, 3271, 0), new Position(3376, 3277, 0), new Position(3378, 3272, 0), new Position(3360, 3274, 0)};
    private static PositionRange[] obstacleArenaStartAreas = new PositionRange[]{new PositionRange(new Position(3367, 3246, 0), new Position(3385, 3256, 0)), new PositionRange(new Position(3336, 3227, 0), new Position(3355, 3237, 0)), new PositionRange(new Position(3367, 3208, 0), new Position(3386, 3218, 0))};
    private static PositionRange[] standardArenaStartAreas = new PositionRange[]{new PositionRange(new Position(3337, 3245, 0), new Position(3355, 3256, 0)), new PositionRange(new Position(3366, 3227, 0), new Position(3386, 3237, 0)), new PositionRange(new Position(3337, 3207, 0), new Position(3354, 3218, 0))};

    public DuelArenaLocationManager(Player player) {
    }

    public static Position findAdjacentOpenPosition(Position position) {
        int n = position.getX();
        int n2 = position.getY();
        Position position2 = new Position(n, n2 + 1);
        Position position3 = new Position(n, n2 - 1);
        Position position4 = new Position(n - 1, n2);
        Position position5 = new Position(n + 1, n2);
        if (WalkingCollisionMap.getTileFlags(position2.getX(), position2.getY(), 0) == 0) {
            return position2;
        }
        if (WalkingCollisionMap.getTileFlags(position3.getX(), position3.getY(), 0) == 0) {
            return position3;
        }
        if (WalkingCollisionMap.getTileFlags(position4.getX(), position4.getY(), 0) == 0) {
            return position4;
        }
        return position5;
    }

    public static Position randomExitPosition() {
        return GameplayHelper.randomUnblockedPositionInRange(duelLobbyRange);
    }

    public static Position lobbyAnchorPosition(Player player) {
        if (player == null) {
            return DuelArenaLocationManager.randomExitPosition();
        }
        int hash = player.getUsername() == null ? player.getIndex() : player.getUsername().hashCode();
        hash ^= player.getCombatLevel() * 131;
        if (hash == Integer.MIN_VALUE) {
            hash = 0;
        }
        if (hash < 0) {
            hash = -hash;
        }
        return duelLobbyAnchorPositions[hash % duelLobbyAnchorPositions.length];
    }

    public static Position randomLobbyPositionNear(Position position, int radius) {
        int attempts = 0;
        while (attempts < 16) {
            int x = position.getX() - radius + GameUtil.randomInclusive(radius * 2);
            int y = position.getY() - radius + GameUtil.randomInclusive(radius * 2);
            x = DuelArenaLocationManager.clamp(x, duelLobbyRange.getMinPosition().getX(), duelLobbyRange.getMaxPosition().getX());
            y = DuelArenaLocationManager.clamp(y, duelLobbyRange.getMinPosition().getY(), duelLobbyRange.getMaxPosition().getY());
            Position candidate = new Position(x, y, 0);
            if (DuelArenaLocationManager.isOpenLobbyPosition(candidate)) {
                return candidate;
            }
            ++attempts;
        }
        return DuelArenaLocationManager.randomExitPosition();
    }

    public static boolean isOpenLobbyPosition(Position position) {
        if (position == null || position.getPlane() != 0) {
            return false;
        }
        if (position.getX() < duelLobbyRange.getMinPosition().getX() || position.getX() > duelLobbyRange.getMaxPosition().getX()) {
            return false;
        }
        if (position.getY() < duelLobbyRange.getMinPosition().getY() || position.getY() > duelLobbyRange.getMaxPosition().getY()) {
            return false;
        }
        return WalkingCollisionMap.getTileFlags(position.getX(), position.getY(), position.getPlane()) == 0;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public final Position randomStartPosition(boolean bl, int n) {
        return GameplayHelper.randomUnblockedPositionInRange((bl ? obstacleArenaStartAreas : standardArenaStartAreas)[n]);
    }
}
