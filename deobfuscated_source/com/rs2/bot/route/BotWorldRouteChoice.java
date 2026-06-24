/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.route;

import com.rs2.bot.route.BotWorldRoute;
import com.rs2.model.clue.CrypticDigClue;
import com.rs2.model.player.Player;
import com.rs2.net.packet.PacketSender;
import java.util.Random;

public class BotWorldRouteChoice {
    BotWorldRoute route;
    boolean reversed;

    public BotWorldRouteChoice(BotWorldRoute botWorldRoute, boolean bl) {
        this.route = botWorldRoute;
        this.reversed = bl;
    }

    public final boolean isReversed() {
        return this.reversed;
    }

    public static boolean showCrypticDigClue(Player player, int n) {
        CrypticDigClue crypticDigClue = CrypticDigClue.forClueItemId(n);
        if (crypticDigClue == null) {
            return false;
        }
        String[] stringArray2;
        player.packetSender.showInterface(6965);
        int n2 = 0;
        while (n2 < crypticDigClue.getClueTextLines().length) {
            int[] nArray;
            PacketSender packetSender = player.packetSender;
            String string = crypticDigClue.getClueTextLines()[n2];
            stringArray2 = crypticDigClue.getClueTextLines();
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

    public static int randomCrypticDigClueItemForLevel(int n) {
        int n2 = new Random().nextInt(CrypticDigClue.values().length);
        while (CrypticDigClue.values()[n2].getLevel() != n) {
            n2 = new Random().nextInt(CrypticDigClue.values().length);
        }
        return CrypticDigClue.values()[n2].getClueItemId();
    }
}

