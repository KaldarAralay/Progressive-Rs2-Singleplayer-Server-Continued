/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.grandexchange;

import com.rs2.model.grandexchange.GrandExchangeOffer;
import com.rs2.model.grandexchange.GrandExchangePriceSampleTimestampComparator;
import com.rs2.model.item.ItemDefinition;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.CountingDataOutputStream;
import com.rs2.util.FileUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class GrandExchangePriceSample {
    long timestampMillis;
    private int itemId;
    private int quantity;
    private int unitPrice;
    private static ArrayList allSamples = new ArrayList();
    private static ArrayList sampledItemIds = new ArrayList();

    GrandExchangePriceSample(int n, int n2, int n3) {
        this.timestampMillis = System.currentTimeMillis();
        this.itemId = n;
        this.quantity = n2;
        this.unitPrice = n3;
        ItemDefinition itemDefinition = ItemDefinition.forId(n);
        if (!sampledItemIds.contains(n)) {
            sampledItemIds.add(n);
        }
        itemDefinition.grandExchangePriceSamples.add(this);
        allSamples.add(this);
        try {
            GrandExchangePriceSample.savePriceSamples();
            return;
        }
        catch (IOException iOException) {
            IOException iOException2 = iOException;
            iOException.printStackTrace();
            return;
        }
    }

    GrandExchangePriceSample(GrandExchangeOffer grandExchangeOffer) {
        this.timestampMillis = System.currentTimeMillis();
        this.itemId = grandExchangeOffer.itemId;
        this.quantity = grandExchangeOffer.quantity;
        this.unitPrice = grandExchangeOffer.unitPrice;
        ItemDefinition itemDefinition = ItemDefinition.forId(this.itemId);
        if (!sampledItemIds.contains(this.itemId)) {
            sampledItemIds.add(this.itemId);
        }
        itemDefinition.grandExchangePriceSamples.add(this);
        allSamples.add(this);
        try {
            GrandExchangePriceSample.savePriceSamples();
            return;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }

    private GrandExchangePriceSample(long l, int n, int n2, int n3) {
        this.timestampMillis = l;
        this.itemId = n;
        this.quantity = n2;
        this.unitPrice = n3;
        ItemDefinition itemDefinition = ItemDefinition.forId(n);
        if (!sampledItemIds.contains(n)) {
            sampledItemIds.add(n);
        }
        itemDefinition.grandExchangePriceSamples.add(this);
        allSamples.add(this);
    }

    public static int getAveragePrice(int n) {
        long l = 0L;
        long l2 = 0L;
        Object object = ItemDefinition.forId(n);
        Iterator iterator = ((ItemDefinition)object).grandExchangePriceSamples.iterator();
        while (iterator.hasNext()) {
            Object object2 = object = (GrandExchangePriceSample)iterator.next();
            long l3 = ((GrandExchangePriceSample)object).quantity;
            object2 = object;
            long l4 = ((GrandExchangePriceSample)object2).unitPrice;
            l += l3 * l4;
            l2 += l3;
        }
        if (l == 0L || l2 == 0L) {
            return -1;
        }
        return (int)(l / l2);
    }

    public static void loadPriceSamples() {
        allSamples.clear();
        if (!FileUtil.exists("./data/geOfferData.dat")) {
            return;
        }
        Object object = FileUtil.readBytes("./data/geOfferData.dat");
        ByteArrayReader byteArrayReader = new ByteArrayReader((byte[])object);
        object = byteArrayReader;
        int n = byteArrayReader.readInt();
        int n2 = 0;
        while (n2 < n) {
            long l = ((ByteArrayReader)object).readLong();
            int n3 = ((ByteArrayReader)object).readUnsignedShort();
            int n4 = ((ByteArrayReader)object).readInt();
            int n5 = ((ByteArrayReader)object).readInt();
            new GrandExchangePriceSample(l, n3, n4, n5);
            ++n2;
        }
        Iterator iterator = sampledItemIds.iterator();
        while (iterator.hasNext()) {
            int n6 = (Integer)iterator.next();
            object = ItemDefinition.forId(n6);
            Collections.sort(((ItemDefinition)object).grandExchangePriceSamples, new GrandExchangePriceSampleTimestampComparator());
            double d = 0.0;
            Iterator iterator2 = ((ItemDefinition)object).grandExchangePriceSamples.iterator();
            while (iterator2.hasNext()) {
                GrandExchangePriceSample grandExchangePriceSample;
                GrandExchangePriceSample grandExchangePriceSample2 = grandExchangePriceSample = (GrandExchangePriceSample)iterator2.next();
                d += (double)grandExchangePriceSample2.quantity;
            }
            double d2 = d * 0.75;
            n2 = (int)d2;
            ArrayList<GrandExchangePriceSample> arrayList = new ArrayList<GrandExchangePriceSample>();
            int n7 = 0;
            for (Object grandExchangePriceSampleObject : ((ItemDefinition)object).grandExchangePriceSamples) {
                GrandExchangePriceSample grandExchangePriceSample = (GrandExchangePriceSample)grandExchangePriceSampleObject;
                if (n7 >= n2) {
                    arrayList.add(grandExchangePriceSample);
                    continue;
                }
                GrandExchangePriceSample grandExchangePriceSample3 = grandExchangePriceSample;
                n7 += grandExchangePriceSample3.quantity;
            }
            for (GrandExchangePriceSample grandExchangePriceSample : arrayList) {
                ((ItemDefinition)object).grandExchangePriceSamples.remove(grandExchangePriceSample);
                allSamples.remove(grandExchangePriceSample);
            }
        }
    }

    private static void savePriceSamples() throws IOException {
        CountingDataOutputStream countingDataOutputStream = new CountingDataOutputStream(new FileOutputStream("./data/geOfferData.dat"));
        countingDataOutputStream.writeInt(allSamples.size());
        Iterator iterator = allSamples.iterator();
        while (iterator.hasNext()) {
            GrandExchangePriceSample grandExchangePriceSample;
            GrandExchangePriceSample grandExchangePriceSample2 = grandExchangePriceSample = (GrandExchangePriceSample)iterator.next();
            countingDataOutputStream.writeLong(grandExchangePriceSample2.timestampMillis);
            grandExchangePriceSample2 = grandExchangePriceSample;
            countingDataOutputStream.writeShort(grandExchangePriceSample2.itemId);
            grandExchangePriceSample2 = grandExchangePriceSample;
            countingDataOutputStream.writeInt(grandExchangePriceSample2.quantity);
            grandExchangePriceSample2 = grandExchangePriceSample;
            countingDataOutputStream.writeInt(grandExchangePriceSample2.unitPrice);
        }
        countingDataOutputStream.close();
    }
}

