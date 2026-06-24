/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import com.rs2.ServerSettings;
import com.rs2.model.player.Player;
import com.rs2.util.ConverterUidLookupCallback;
import com.rs2.util.ConverterUidLookupQuery;
import com.rs2.util.db.DatabaseService;
import java.io.File;
import java.util.Arrays;

public class Converter {
    private static int convertedCount = 0;
    private static volatile boolean readyForNextFile = true;

    public static void main(String[] stringArray) {
        try {
            ServerSettings.mysqlPlayerSaveEnabled = true;
            File characterDirectory = new File("./data/characters/");
            DatabaseService.setInstance(new DatabaseService(8, ServerSettings.mysqlDriverClass, ServerSettings.mysqlJdbcUrl, ServerSettings.mysqlUsername, ServerSettings.mysqlPassword));
            File[] characterFiles = characterDirectory.listFiles();
            System.out.println("Preparing to convert " + characterFiles.length + " character files...");
            for (File characterFile : Arrays.asList(characterFiles)) {
                while (!readyForNextFile) {
                }
                readyForNextFile = false;
                String username = characterFile.getName().substring(0, characterFile.getName().indexOf(46)).toLowerCase();
                Player player = new Player(null);
                player.setUsername(username);
                ConverterUidLookupQuery converterUidLookupQuery = new ConverterUidLookupQuery("SELECT uid FROM `prs06_users` WHERE username = ?", username);
                DatabaseService.getInstance().submit(converterUidLookupQuery, new ConverterUidLookupCallback(characterDirectory, player, username));
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static /* synthetic */ void markReadyForNextFile(boolean bl) {
        readyForNextFile = true;
    }

    static /* synthetic */ int getConvertedCount() {
        return convertedCount;
    }

    static /* synthetic */ void setConvertedCount(int n) {
        convertedCount = n;
    }
}

