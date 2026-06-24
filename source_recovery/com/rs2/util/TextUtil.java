/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import java.nio.ByteBuffer;

public final class TextUtil {
    private static char[] NAME_HASH_CHAR_TABLE = new char[]{'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '\"', '[', ']', '|', '?', '/', '`'};

    public static String decodeNameHash(long l) {
        int n = 0;
        char[] cArray = new char[12];
        while (l != 0L) {
            long l2 = l;
            cArray[11 - n++] = NAME_HASH_CHAR_TABLE[(int)(l2 - (l /= 37L) * 37L)];
        }
        return new String(cArray, 12 - n, n);
    }

    public static String formatCombatLevel(int n, int n2) {
        if ((n -= n2) < 0 && n > -10) {
            return "@gr2@" + n2;
        }
        if (n == 0) {
            return "@yel@" + n2;
        }
        if (n > 0 && n < 10) {
            return "@ora@" + n2;
        }
        if (n > 0) {
            return "@red@" + n2;
        }
        return "@gre@" + n2;
    }

    public static long encodeNameHash(String string) {
        long l = 0L;
        int n = 0;
        while (n < string.length() && n < 12) {
            char c = string.charAt(n);
            l *= 37L;
            if (c >= 'A' && c <= 'Z') {
                l += c + '\u0001' - 65;
            } else if (c >= 'a' && c <= 'z') {
                l += c + '\u0001' - 97;
            } else if (c >= '0' && c <= '9') {
                l += c + 27 - 48;
            }
            ++n;
        }
        while (l % 37L == 0L && l != 0L) {
            l /= 37L;
        }
        return l;
    }

    public static String capitalizeFirst(String string) {
        if ((string = string.toLowerCase()).length() <= 1) {
            return string.toUpperCase();
        }
        string = String.valueOf(string.substring(0, 1).toUpperCase()) + string.substring(1);
        return string;
    }

    public static String formatDisplayName(String string) {
        string = string.replace(" ", "_");
        if (string.length() > 0) {
            char[] chars = string.toCharArray();
            int n = 0;
            while (n < chars.length) {
                if (chars[n] == '_') {
                    chars[n] = ' ';
                    if (n + 1 < chars.length && chars[n + 1] >= 'a' && chars[n + 1] <= 'z') {
                        chars[n + 1] = (char)(chars[n + 1] + 'A' - 'a');
                    }
                }
                ++n;
            }
            if (chars[0] >= 'a' && chars[0] <= 'z') {
                chars[0] = (char)(chars[0] + 'A' - 'a');
            }
            return new String(chars);
        }
        return string;
    }

    public static String prependIndefiniteArticle(String string) {
        String string2 = string.toUpperCase();
        String string3 = "a";
        char c = string2.charAt(0);
        if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
            string3 = "an";
        }
        string = String.valueOf(string3) + " " + string;
        return string;
    }

    public static String readLine(ByteBuffer byteBuffer) {
        byte by;
        StringBuilder stringBuilder = new StringBuilder();
        while (byteBuffer.hasRemaining() && (by = byteBuffer.get()) != 10) {
            stringBuilder.append((char)by);
        }
        return stringBuilder.toString();
    }
}
