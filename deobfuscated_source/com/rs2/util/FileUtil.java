/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public final class FileUtil {
    private static int a = 0;
    private static int b = 0;
    private static int c = 0;

    public static final byte[] readBytes(String string) {
        return FileUtil.readBytes(string, true);
    }

    public static final byte[] readBytes(String string, boolean bl) {
        try {
            File file = new File(string);
            int n = (int)file.length();
            byte[] byArray = new byte[n];
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(string)));
            dataInputStream.readFully(byArray, 0, n);
            dataInputStream.close();
            ++a;
            return byArray;
        }
        catch (Exception exception) {
            if (bl) {
                System.out.println("Read Error: " + string);
            }
            return null;
        }
    }

    public static final void writeBytes(String string, byte[] byArray) {
        try {
            new File(new File(string).getParent()).mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(string);
            fileOutputStream.write(byArray, 0, byArray.length);
            fileOutputStream.close();
            ++b;
            ++c;
            return;
        }
        catch (Throwable throwable) {
            System.out.println("Write Error: " + string);
            return;
        }
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }
}

