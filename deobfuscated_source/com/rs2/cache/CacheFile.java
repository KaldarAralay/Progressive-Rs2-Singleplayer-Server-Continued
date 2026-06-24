/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.cache;

import com.rs2.model.Position;
import com.rs2.model.clue.ClueKeyHandler;
import com.rs2.model.clue.SearchClue;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.player.Player;
import com.rs2.net.packet.PacketSender;
import java.nio.ByteBuffer;
import java.util.Random;

public class CacheFile {
    private ByteBuffer buffer;

    public CacheFile(int n, int n2, ByteBuffer byteBuffer) {
        this.buffer = byteBuffer;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

    public static boolean showSearchClue(Player player, int n) {
        SearchClue searchClue = SearchClue.forClueItemId(n);
        if (searchClue == null) {
            return false;
        }
        String[] stringArray2;
        player.packetSender.showInterface(6965);
        int n2 = 0;
        while (n2 < searchClue.getClueTextLines().length) {
            int[] nArray;
            PacketSender packetSender = player.packetSender;
            String string = searchClue.getClueTextLines()[n2];
            stringArray2 = searchClue.getClueTextLines();
            switch (stringArray2.length) {
                case 1: {
                    int[] nArray2 = new int[1];
                    nArray = nArray2;
                    nArray2[0] = 6971;
                    break;
                }
                case 2: {
                    int[] nArray3 = new int[2];
                    nArray3[0] = 6971;
                    nArray = nArray3;
                    nArray3[1] = 6972;
                    break;
                }
                case 3: {
                    int[] nArray4 = new int[3];
                    nArray4[0] = 6970;
                    nArray4[1] = 6971;
                    nArray = nArray4;
                    nArray4[2] = 6972;
                    break;
                }
                case 4: {
                    int[] nArray5 = new int[4];
                    nArray5[0] = 6970;
                    nArray5[1] = 6971;
                    nArray5[2] = 6972;
                    nArray = nArray5;
                    nArray5[3] = 6973;
                    break;
                }
                case 5: {
                    int[] nArray6 = new int[5];
                    nArray6[0] = 6969;
                    nArray6[1] = 6970;
                    nArray6[2] = 6971;
                    nArray6[3] = 6972;
                    nArray = nArray6;
                    nArray6[4] = 6973;
                    break;
                }
                case 6: {
                    int[] nArray7 = new int[6];
                    nArray7[0] = 6969;
                    nArray7[1] = 6970;
                    nArray7[2] = 6971;
                    nArray7[3] = 6972;
                    nArray7[4] = 6973;
                    nArray = nArray7;
                    nArray7[5] = 6974;
                    break;
                }
                case 7: {
                    int[] nArray8 = new int[7];
                    nArray8[0] = 6968;
                    nArray8[1] = 6969;
                    nArray8[2] = 6970;
                    nArray8[3] = 6971;
                    nArray8[4] = 6972;
                    nArray8[5] = 6973;
                    nArray = nArray8;
                    nArray8[6] = 6974;
                    break;
                }
                case 8: {
                    int[] nArray9 = new int[8];
                    nArray9[0] = 6968;
                    nArray9[1] = 6969;
                    nArray9[2] = 6970;
                    nArray9[3] = 6971;
                    nArray9[4] = 6972;
                    nArray9[5] = 6973;
                    nArray9[6] = 6974;
                    nArray = nArray9;
                    nArray9[7] = 6975;
                    break;
                }
                default: {
                    nArray = null;
                }
            }
            packetSender.sendInterfaceText(string, nArray[n2]);
            ++n2;
        }
        return true;
    }

    public static int randomSearchClueItemForLevel(int n) {
        int n2 = new Random().nextInt(SearchClue.values().length);
        while (SearchClue.values()[n2].getLevel() != n) {
            n2 = new Random().nextInt(SearchClue.values().length);
        }
        return SearchClue.values()[n2].getClueItemId();
    }

    public static boolean searchClueObject(Player player, WorldObject worldObject) {
        SearchClue searchClue = SearchClue.forPosition(new Position(worldObject.getPosition().getX(), worldObject.getPosition().getY(), player.getPosition().getPlane()));
        if (searchClue == null) {
            return false;
        }
        if (!player.getInventoryManager().containsItem(searchClue.getClueItemId()) || player.getPosition().getPlane() != searchClue.getPosition().getPlane()) {
            return false;
        }
        if (!ClueKeyHandler.consumeRequiredKey(player, searchClue.getClueItemId())) {
            return true;
        }
        if (searchClue.getReplacementObjectId() > 0) {
            new DynamicObject(searchClue.getReplacementObjectId(), worldObject.getPosition().getX(), worldObject.getPosition().getY(), player.getPosition().getPlane(), worldObject.getOrientation(), worldObject.getType(), worldObject.getObjectId(), 30);
        }
        player.getInventoryManager().removeItem(new ItemStack(searchClue.getClueItemId(), 1));
        player.getUpdateState().setAnimation(searchClue.getAnimationId());
        TreasureTrailManager.advanceOrCompleteTrail(player, searchClue.getLevel(), "You've found another clue!", false, "You've found a casket!");
        return true;
    }
}

