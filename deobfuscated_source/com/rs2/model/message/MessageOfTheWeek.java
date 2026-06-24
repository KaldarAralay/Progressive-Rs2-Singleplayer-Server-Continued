/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.message;

import com.rs2.Server;
import com.rs2.model.GameplayHelper;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.CountingDataOutputStream;
import com.rs2.util.FileUtil;
import com.rs2.util.GameUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import org.joda.time.DateTime;

public final class MessageOfTheWeek {
    private static int antivirusMessageInterfaceId = 5993;
    private static int holidayMessageInterfaceId = 15767;
    private static int passwordMessageInterfaceId = 15774;
    private static int christmasMessageInterfaceId = 15819;
    private int interfaceId;
    private String[] lines;
    private String title;
    private static MessageOfTheWeek[] normalMessages = new MessageOfTheWeek[]{new MessageOfTheWeek(passwordMessageInterfaceId, false, "Your password is only as safe as your computer.", "Install anti-virus software!"), new MessageOfTheWeek(antivirusMessageInterfaceId, false, "Out of date anti-virus software is useless.", "Update it often and run regular scans!")};
    private static MessageOfTheWeek halloweenMessage = new MessageOfTheWeek(holidayMessageInterfaceId, false, "Halloween has arrived to RuneScape!", "");
    private static MessageOfTheWeek christmasMessage = new MessageOfTheWeek(christmasMessageInterfaceId, false, "JaGeX wishes you a Merry Christmas", "and a Happy New Year!");
    private static MessageOfTheWeek easterMessage = new MessageOfTheWeek(holidayMessageInterfaceId, false, "Easter has arrived to RuneScape!", "");

    private MessageOfTheWeek(int n, boolean bl, String ... stringArray) {
        this.interfaceId = n;
        this.lines = stringArray;
        this.title = "Message of the week";
    }

    public static MessageOfTheWeek getMessageForIndex(int n) {
        if (Server.halloweenEventActive) {
            return halloweenMessage;
        }
        if (Server.christmasEventActive) {
            return christmasMessage;
        }
        if (Server.easterEventActive) {
            return easterMessage;
        }
        return normalMessages[n];
    }

    private static void saveCurrentMessageIndex() {
        Object object = new File("./data/messageOfTheWeek.dat");
        ((File)object).delete();
        try {
            object = new CountingDataOutputStream(new FileOutputStream("./data/messageOfTheWeek.dat"));
            ((CountingDataOutputStream)object).writeLong(System.currentTimeMillis());
            ((CountingDataOutputStream)object).writeUnsignedByte(Server.messageOfTheWeekIndex);
            ((FilterOutputStream)object).close();
            return;
        }
        catch (Exception exception) {
            return;
        }
    }

    public static void loadAndRotateMessage() {
        long l;
        DateTime dateTime;
        long l2 = 0L;
        long l3 = System.currentTimeMillis();
        if (FileUtil.exists("./data/messageOfTheWeek.dat")) {
            try {
                Object object = FileUtil.readBytes("./data/messageOfTheWeek.dat");
                ByteArrayReader byteArrayReader = new ByteArrayReader((byte[])object);
                object = byteArrayReader;
                l2 = byteArrayReader.readLong();
                Server.messageOfTheWeekIndex = ((ByteArrayReader)object).readUnsignedByte();
            }
            catch (Exception exception) {
                Exception exception2 = exception;
                exception.printStackTrace();
            }
        }
        if (Server.halloweenEventActive || Server.christmasEventActive || Server.easterEventActive) {
            Server.messageOfTheWeekIndex = 250;
            MessageOfTheWeek.saveCurrentMessageIndex();
            return;
        }
        if (GameplayHelper.getDaysBetweenMidnights(l2, l3) > 0 && (dateTime = new DateTime(l = l3)).dayOfWeek().get() == 1 || Server.messageOfTheWeekIndex == 250 || l2 == 0L) {
            Server.messageOfTheWeekIndex = GameUtil.randomInt(normalMessages.length);
            MessageOfTheWeek.saveCurrentMessageIndex();
        }
    }

    public final int getInterfaceId() {
        return this.interfaceId;
    }

    public final String getTitle() {
        return this.title;
    }

    public final String[] getLines() {
        return this.lines;
    }
}

