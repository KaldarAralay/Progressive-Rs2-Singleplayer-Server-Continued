/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

public final class ChatTextCodec {
    private static char[] CHAR_TABLE = new char[]{' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\u00a3', '$', '%', '\"', '[', ']'};

    public static String decode(byte[] byArray, int n) {
        byte[] byArray2 = new byte[4096];
        int n2 = 0;
        int n3 = -1;
        int n4 = 0;
        while (n4 < n << 1) {
            int n5 = byArray[n4 / 2] >> 4 - 4 * (n4 % 2) & 0xF;
            if (n3 == -1) {
                if (n5 < 13) {
                    byArray2[n2++] = (byte)CHAR_TABLE[n5];
                } else {
                    n3 = n5;
                }
            } else {
                byArray2[n2++] = (byte)CHAR_TABLE[(n3 << 4) + n5 - 195];
                n3 = -1;
            }
            ++n4;
        }
        return new String(byArray2, 0, n2);
    }

    /*
     * Enabled aggressive block sorting
     */
    public static int encode(String string, byte[] byArray) {
        if (string.length() > 80) {
            string = string.substring(0, 80);
        }
        string = string.toLowerCase();
        int n = -1;
        int n2 = 0;
        int n3 = 0;
        while (n3 < string.length()) {
            char c = string.charAt(n3);
            int n4 = 0;
            int n5 = 0;
            while (n5 < 61) {
                if (c == CHAR_TABLE[n5]) {
                    n4 = n5;
                    break;
                }
                ++n5;
            }
            if (n4 > 12) {
                n4 += 195;
            }
            if (n == -1) {
                if (n4 < 13) {
                    n = n4;
                } else {
                    byArray[n2++] = (byte)n4;
                }
            } else if (n4 < 13) {
                byArray[n2++] = (byte)((n << 4) + n4);
                n = -1;
            } else {
                byArray[n2++] = (byte)((n << 4) + (n4 >> 4));
                n = n4 & 0xF;
            }
            ++n3;
        }
        if (n != -1) {
            byArray[n2++] = (byte)(n << 4);
        }
        return n2;
    }
}

