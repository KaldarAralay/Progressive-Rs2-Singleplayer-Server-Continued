/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import com.rs2.model.Position;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class PuzzleBoxHandler {
    private static ArrayList pieceQueue = new ArrayList(25);
    private static int activePuzzleType;

    public static boolean openCluePuzzleBox(Player player, int n) {
        if (PuzzleBoxHandler.getPuzzleTypeForItem(n) == 0) {
            return false;
        }
        int n2 = PuzzleBoxHandler.getPuzzleTypeForItem(n);
        int n3 = 0;
        while (n3 < 25) {
            pieceQueue.add(PuzzleBoxHandler.getPiecesForPuzzleType(n2)[n3]);
            ++n3;
        }
        activePuzzleType = PuzzleBoxHandler.getPuzzleTypeForItem(n);
        Player player2 = player;
        ArrayList arrayList = PuzzleBoxHandler.drainPieceQueue();
        n = 0;
        int n4 = 0;
        while (n4 < 25) {
            if (player2.sliderPuzzlePieces[n4] != null && player2.sliderPuzzlePieces[n4].getId() != -1 && arrayList.contains(player2.sliderPuzzlePieces[n4].getId())) {
                n = 1;
            }
            ++n4;
        }
        if (n == 0 && !PuzzleBoxHandler.isCluePuzzleSolved(player2)) {
            n4 = 0;
            while (n4 < 25) {
                player2.sliderPuzzlePieces[n4] = new ItemStack((Integer)arrayList.get(n4));
                ++n4;
            }
            if (!player2.en) {
                PuzzleBoxHandler.scramblePuzzle(player2);
            }
        }
        PuzzleBoxHandler.showCluePuzzleInterface(player);
        return true;
    }

    public static boolean openQuestPuzzleBox(Player player) {
        int n = 0;
        while (n < 25) {
            pieceQueue.add(TreasureTrailManager.DEFAULT_SLIDER_PUZZLE_PIECES[n]);
            ++n;
        }
        Player player2 = player;
        ArrayList arrayList = PuzzleBoxHandler.drainPieceQueue();
        boolean bl = false;
        int n2 = 0;
        while (n2 < 25) {
            if (player2.sliderPuzzlePieces[n2] != null && player2.sliderPuzzlePieces[n2].getId() != -1 && arrayList.contains(player2.sliderPuzzlePieces[n2].getId())) {
                bl = true;
            }
            ++n2;
        }
        if (!bl && !PuzzleBoxHandler.isQuestPuzzleSolved(player2)) {
            n2 = 0;
            while (n2 < 25) {
                player2.sliderPuzzlePieces[n2] = new ItemStack((Integer)arrayList.get(n2));
                ++n2;
            }
            if (!player2.en) {
                PuzzleBoxHandler.scramblePuzzle(player2);
            }
        }
        PuzzleBoxHandler.showQuestPuzzleInterface(player);
        return true;
    }

    private static ArrayList drainPieceQueue() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>(25);
        while (pieceQueue.size() > 0) {
            arrayList.add((Integer)pieceQueue.get(0));
            pieceQueue.remove(0);
        }
        return arrayList;
    }

    private static int getPuzzleTypeForItem(int n) {
        switch (n) {
            case 2800: {
                return 1;
            }
            case 3565: {
                return 2;
            }
            case 3571: {
                return 3;
            }
        }
        return 0;
    }

    private static int[] getPiecesForPuzzleType(int n) {
        switch (n) {
            case 1: {
                return TreasureTrailManager.SLIDER_PUZZLE_ONE_PIECES;
            }
            case 2: {
                return TreasureTrailManager.SLIDER_PUZZLE_TWO_PIECES;
            }
            case 3: {
                return TreasureTrailManager.SLIDER_PUZZLE_THREE_PIECES;
            }
        }
        return null;
    }

    private static void showCluePuzzleInterface(Player player) {
        player.packetSender.showInterface(6976);
        player.packetSender.sendItemContainer(6980, player.sliderPuzzlePieces);
        PacketSender packetSender = player.packetSender;
        ItemStack[] itemStackArray = new ItemStack[25];
        int n = 0;
        while (n < 25) {
            itemStackArray[n] = new ItemStack(PuzzleBoxHandler.getPiecesForPuzzleType(activePuzzleType)[n]);
            ++n;
        }
        packetSender.sendItemContainer(6985, itemStackArray);
        if (!PuzzleBoxHandler.isCluePuzzleSolved(player)) {
            player.en = false;
        }
    }

    private static void showQuestPuzzleInterface(Player player) {
        Player player2 = player;
        player2.packetSender.showInterface(11126);
        player2 = player;
        player2.packetSender.sendItemContainer(11130, player.sliderPuzzlePieces);
        if (!PuzzleBoxHandler.isQuestPuzzleSolved(player)) {
            player.en = false;
            return;
        }
        DialogueManager.continueContextDialogue(1, player, 4871, 100, 0, 2650, 4507);
    }

    private static Position getPiecePosition(Player player, int n) {
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (n4 < player.sliderPuzzlePieces.length) {
            if (player.sliderPuzzlePieces[n4] != null && player.sliderPuzzlePieces[n4].getId() == n) {
                n2 = n4 - 5 * (n4 / 5) + 1;
                n3 = n4 / 5 + 1;
            }
            ++n4;
        }
        return new Position(n2, n3);
    }

    private static boolean isAdjacentToBlankTile(Player player, Position position) {
        Position position2 = new Position(position.getX() - 1, position.getY(), 0);
        Position position3 = new Position(position.getX() + 1, position.getY(), 0);
        Position position4 = new Position(position.getX(), position.getY() - 1, 0);
        position = new Position(position.getX(), position.getY() + 1, 0);
        Player player2 = player;
        return PuzzleBoxHandler.getPiecePosition(player2, -1).equals(position2) || PuzzleBoxHandler.getPiecePosition(player2 = player, -1).equals(position3) || PuzzleBoxHandler.getPiecePosition(player2 = player, -1).equals(position4) || PuzzleBoxHandler.getPiecePosition(player2 = player, -1).equals(position);
    }

    private static int getTileDistance(Position position, Position position2, String string) {
        int n = position.getX();
        int n2 = position.getY();
        int n3 = position2.getX();
        int n4 = position2.getY();
        Position position3 = new Position(n, n2, 0);
        Position position4 = new Position(n3, n4, 0);
        int n5 = 0;
        int n6 = 0;
        while (position3.getX() != position4.getX()) {
            if (n3 < n) {
                ++n3;
                ++n5;
            }
            if (n3 > n) {
                --n3;
                ++n5;
            }
            position4.setX(n3);
        }
        while (position3.getY() != position4.getY()) {
            if (n4 < n2) {
                ++n4;
                ++n6;
            }
            if (n4 > n2) {
                --n4;
                ++n6;
            }
            position4.setY(n4);
        }
        if (string == "x") {
            return n5;
        }
        if (string == "y") {
            return n6;
        }
        return n5 + n6;
    }

    private static int getTileDistance(Position position, Position position2) {
        return PuzzleBoxHandler.getTileDistance(position, position2, "");
    }

    public static boolean movePuzzlePiece(Player player, int n) {
        Object object;
        if (PuzzleBoxHandler.getPiecePosition(player, n).equals(new Position(0, 0, 0))) {
            return false;
        }
        PuzzleBoxHandler.getPiecePosition(player, n);
        Position position = PuzzleBoxHandler.getPiecePosition(player, n);
        Object object2 = player;
        object2 = PuzzleBoxHandler.getPiecePosition((Player)object2, -1);
        if (PuzzleBoxHandler.isAdjacentToBlankTile(player, PuzzleBoxHandler.getPiecePosition(player, n))) {
            PuzzleBoxHandler.swapBlankWithPosition(player, PuzzleBoxHandler.getPiecePosition(player, n), true);
            return true;
        }
        ArrayList<Object> arrayList = new ArrayList<Object>(2);
        int n2 = 0;
        while (n2 < player.sliderPuzzlePieces.length) {
            object = PuzzleBoxHandler.getPiecePosition(player, player.sliderPuzzlePieces[n2].getId());
            if (PuzzleBoxHandler.isAdjacentToBlankTile(player, (Position)object) && PuzzleBoxHandler.getTileDistance((Position)object2, position) >= PuzzleBoxHandler.getTileDistance(position, (Position)object)) {
                arrayList.add(object);
            }
            ++n2;
        }
        n2 = 0;
        while (n2 < player.sliderPuzzlePieces.length) {
            object = new ArrayList(4);
            Position position2 = PuzzleBoxHandler.getPiecePosition(player, player.sliderPuzzlePieces[n2].getId());
            if (!position2.equals(object2) && PuzzleBoxHandler.getTileDistance((Position)object2, position) >= PuzzleBoxHandler.getTileDistance(position, position2)) {
                int n3 = 0;
                while (n3 < arrayList.size()) {
                    ((ArrayList)object).add(PuzzleBoxHandler.getTileDistance(position, (Position)arrayList.get(n3), "x"));
                    ((ArrayList)object).add(PuzzleBoxHandler.getTileDistance(position, (Position)arrayList.get(n3), "y"));
                    ++n3;
                }
                if (PuzzleBoxHandler.isAdjacentToBlankTile(player, position2) && (PuzzleBoxHandler.maxValue((ArrayList)object) == PuzzleBoxHandler.getTileDistance(position, position2, "x") || PuzzleBoxHandler.maxValue((ArrayList)object) == PuzzleBoxHandler.getTileDistance(position, position2, "y"))) {
                    PuzzleBoxHandler.swapBlankWithPosition(player, position2, true);
                    return true;
                }
            }
            ++n2;
        }
        return true;
    }

    public static boolean isCluePuzzleSolved(Player player) {
        int n = 0;
        if (PuzzleBoxHandler.getPiecesForPuzzleType(activePuzzleType) == null) {
            return false;
        }
        int n2 = 0;
        while (n2 < player.sliderPuzzlePieces.length) {
            if (player.sliderPuzzlePieces[n2] != null && player.sliderPuzzlePieces[n2].getId() == PuzzleBoxHandler.getPiecesForPuzzleType(activePuzzleType)[n2]) {
                ++n;
            }
            ++n2;
        }
        return n == player.sliderPuzzlePieces.length;
    }

    private static boolean isQuestPuzzleSolved(Player player) {
        int n = 0;
        int n2 = 0;
        while (n2 < player.sliderPuzzlePieces.length) {
            if (player.sliderPuzzlePieces[n2] != null && player.sliderPuzzlePieces[n2].getId() == TreasureTrailManager.DEFAULT_SLIDER_PUZZLE_PIECES[n2]) {
                ++n;
            }
            ++n2;
        }
        return n == player.sliderPuzzlePieces.length;
    }

    private static void swapBlankWithPosition(Player player, Position position, boolean bl) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (n3 < player.sliderPuzzlePieces.length) {
            if (player.sliderPuzzlePieces[n3].getId() == -1) {
                n = n3;
            }
            if (PuzzleBoxHandler.getPiecePosition(player, player.sliderPuzzlePieces[n3].getId()).equals(position)) {
                n2 = n3;
            }
            ++n3;
        }
        ItemStack itemStack = player.sliderPuzzlePieces[n];
        player.sliderPuzzlePieces[n] = player.sliderPuzzlePieces[n2];
        player.sliderPuzzlePieces[n2] = itemStack;
        if (bl) {
            if (player.getOpenInterfaceId() == 6976) {
                PuzzleBoxHandler.showCluePuzzleInterface(player);
                return;
            }
            if (player.getOpenInterfaceId() == 11126) {
                PuzzleBoxHandler.showQuestPuzzleInterface(player);
            }
        }
    }

    private static void scramblePuzzle(Player player) {
        int n = 0;
        int n2 = -1;
        while (n < 125) {
            int n3 = 0;
            int[] nArray = new int[4];
            Object object = player;
            if (((Position)(object = PuzzleBoxHandler.getPiecePosition((Player)object, -1))).getY() > 1 && n2 != 1) {
                nArray[0] = 0;
                ++n3;
            }
            if (((Position)object).getY() < 5 && n2 != 0) {
                nArray[n3] = 1;
                ++n3;
            }
            if (((Position)object).getX() > 1 && n2 != 3) {
                nArray[n3] = 2;
                ++n3;
            }
            if (((Position)object).getX() < 5 && n2 != 2) {
                nArray[n3] = 3;
                ++n3;
            }
            n3 = nArray[GameUtil.randomInt(n3)];
            switch (n3) {
                case 0: {
                    Player player2 = player;
                    object = player2;
                    Position position = PuzzleBoxHandler.getPiecePosition(player2, -1);
                    position = new Position(position.getX(), position.getY() - 1, 0);
                    PuzzleBoxHandler.swapBlankWithPosition(player2, position, false);
                    n2 = 0;
                    break;
                }
                case 1: {
                    Player player3 = player;
                    object = player3;
                    Position position = PuzzleBoxHandler.getPiecePosition(player3, -1);
                    position = new Position(position.getX(), position.getY() + 1, 0);
                    PuzzleBoxHandler.swapBlankWithPosition(player3, position, false);
                    n2 = 1;
                    break;
                }
                case 2: {
                    Player player4 = player;
                    object = player4;
                    Position position = PuzzleBoxHandler.getPiecePosition(player4, -1);
                    position = new Position(position.getX() - 1, position.getY(), 0);
                    PuzzleBoxHandler.swapBlankWithPosition(player4, position, false);
                    n2 = 2;
                    break;
                }
                case 3: {
                    Player player5 = player;
                    object = player5;
                    Position position = PuzzleBoxHandler.getPiecePosition(player5, -1);
                    position = new Position(position.getX() + 1, position.getY(), 0);
                    PuzzleBoxHandler.swapBlankWithPosition(player5, position, false);
                    n2 = 3;
                }
            }
            ++n;
        }
    }

    private static int maxValue(ArrayList arrayList) {
        int n = (Integer)arrayList.get(0);
        int n2 = 0;
        while (n2 < arrayList.size()) {
            if ((Integer)arrayList.get(n2) >= n) {
                n = (Integer)arrayList.get(n2);
            }
            ++n2;
        }
        return n;
    }

    public static void giveRandomPuzzleBox(Player player) {
        int[] nArray = new int[]{2800, 3565, 3571};
        player.getInventoryManager().addItem(new ItemStack(nArray[GameUtil.randomExclusive(3)]));
    }
}

