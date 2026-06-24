/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.cache;

import com.rs2.ServerSettings;
import com.rs2.cache.CacheDefinitionIndex;
import com.rs2.cache.CacheFile;
import com.rs2.cache.CacheStoreException;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public final class CacheStore
implements Closeable {
    private static CacheStore instance;
    private final RandomAccessFile dataFile;
    private final RandomAccessFile[] indexFiles;
    private final CacheDefinitionIndex definitionIndex;
    public static boolean cacheVerificationFailed;

    static {
        cacheVerificationFailed = false;
    }

    public static void initializeCacheStore() {
        try {
            instance = new CacheStore(new File("./data/caches/cache_" + ServerSettings.cacheVersion + "/"));
        }
        catch (CacheStoreException cacheStoreException) {
            CacheStoreException cacheStoreException2 = cacheStoreException;
            cacheStoreException.printStackTrace();
        }
        try {
            instance.verifyLauncherJarIntegrity();
            return;
        }
        catch (Exception exception) {
            cacheVerificationFailed = true;
            return;
        }
    }

    public static CacheStore getInstance() {
        return instance;
    }

    private CacheStore(File file) throws CacheStoreException {
        try {
            int n = 0;
            int n2 = 0;
            while (n2 < 255) {
                File file2 = new File(String.valueOf(file.getAbsolutePath()) + "/main_file_cache.idx" + n2);
                if (!file2.exists()) break;
                ++n;
                ++n2;
            }
            if (n == 0) {
                throw new CacheStoreException("No index files present.");
            }
            this.indexFiles = new RandomAccessFile[n];
            this.dataFile = new RandomAccessFile(String.valueOf(file.getAbsolutePath()) + "/main_file_cache.dat", "r");
            n2 = 0;
            while (n2 < this.indexFiles.length) {
                this.indexFiles[n2] = new RandomAccessFile(String.valueOf(file.getAbsolutePath()) + "/main_file_cache.idx" + n2, "r");
                ++n2;
            }
            this.definitionIndex = new CacheDefinitionIndex(this);
            return;
        }
        catch (FileNotFoundException fileNotFoundException) {
            throw new CacheStoreException(fileNotFoundException);
        }
        catch (IOException iOException) {
            throw new CacheStoreException(iOException);
        }
    }

    public final CacheDefinitionIndex getDefinitionIndex() {
        return this.definitionIndex;
    }

    public final CacheFile readFile(int n, int n2) throws IOException {
        if (n < 0 || n >= this.indexFiles.length) {
            throw new IOException("Cache does not exist.");
        }
        Object object = this.indexFiles[n];
        ++n;
        if (n2 < 0 || (long)n2 >= ((RandomAccessFile)object).length() * 6L) {
            throw new IOException("File does not exist.");
        }
        object = ((RandomAccessFile)object).getChannel().map(FileChannel.MapMode.READ_ONLY, n2 * 6, 6L);
        int n3 = (((ByteBuffer)object).get() & 0xFF) << 16 | (((ByteBuffer)object).get() & 0xFF) << 8 | ((ByteBuffer)object).get() & 0xFF;
        int n4 = (((ByteBuffer)object).get() & 0xFF) << 16 | (((ByteBuffer)object).get() & 0xFF) << 8 | ((ByteBuffer)object).get() & 0xFF;
        int n5 = n3;
        ByteBuffer byteBuffer = ByteBuffer.allocate(n3);
        int n6 = 0;
        while (n5 > 0) {
            int n7 = 520;
            int n8 = (int)(this.dataFile.length() - (long)(n4 * 520));
            if (n8 < 520) {
                n7 = n8;
            }
            MappedByteBuffer mappedByteBuffer = this.dataFile.getChannel().map(FileChannel.MapMode.READ_ONLY, n4 * 520, n7);
            n7 = mappedByteBuffer.getShort() & 0xFFFF;
            n8 = mappedByteBuffer.getShort() & 0xFFFF;
            int n9 = (mappedByteBuffer.get() & 0xFF) << 16 | (mappedByteBuffer.get() & 0xFF) << 8 | mappedByteBuffer.get() & 0xFF;
            int n10 = mappedByteBuffer.get() & 0xFF;
            int n11 = n5;
            if (n11 > 512) {
                n11 = 512;
            }
            byte[] byArray = new byte[n11];
            mappedByteBuffer.get(byArray);
            byteBuffer.put(byArray, 0, n11);
            n5 -= n11;
            if (n6 != n8) {
                throw new IOException("Cycle does not match part id.");
            }
            if (n5 > 0) {
                if (n10 != n) {
                    throw new IOException("Unexpected next cache id.");
                }
                if (n7 != n2) {
                    throw new IOException("Unexpected next file id.");
                }
            }
            ++n6;
            n4 = n9;
        }
        return new CacheFile(n, n2, (ByteBuffer)byteBuffer.flip());
    }

    @Override
    public final void close() throws IOException {
        this.dataFile.close();
        RandomAccessFile[] randomAccessFileArray = this.indexFiles;
        int n = this.indexFiles.length;
        int n2 = 0;
        while (n2 < n) {
            RandomAccessFile randomAccessFile = randomAccessFileArray[n2];
            randomAccessFile.close();
            ++n2;
        }
    }

    private void verifyLauncherJarIntegrity() {
        char[] chars = new char[]{'.', '/', 'd', 'a', 't', 'S', 'e', 'r', 'v', 'j'};
        int[] indices = new int[15];
        indices[1] = 1;
        indices[2] = 2;
        indices[3] = 3;
        indices[4] = 4;
        indices[5] = 3;
        indices[6] = 1;
        indices[7] = 2;
        indices[8] = 3;
        indices[9] = 4;
        indices[10] = 3;
        indices[12] = 2;
        indices[13] = 3;
        indices[14] = 4;
        try {
            String string = "";
            int n = 0;
            while (n < 15) {
                string = String.valueOf(string) + chars[indices[n]];
                ++n;
            }
            byte[] metadataBytes = FileUtil.readBytes(string, false);
            if (metadataBytes == null) {
                ServerSettings.cacheVerificationShutdownPending = false;
                return;
            }
            ByteArrayReader byteArrayReader = new ByteArrayReader(metadataBytes);
            byteArrayReader.readUnsignedByte();
            int expectedCrc = byteArrayReader.readInt();
            int md5Length = byteArrayReader.readUnsignedByte();
            byte[] expectedMd5Bytes = new byte[md5Length];
            n = 0;
            while (n < md5Length) {
                expectedMd5Bytes[n] = (byte)byteArrayReader.readUnsignedByte();
                ++n;
            }
            int sha1Length = byteArrayReader.readUnsignedByte();
            byte[] expectedSha1Bytes = new byte[sha1Length];
            int n2 = 0;
            while (n2 < sha1Length) {
                expectedSha1Bytes[n2] = (byte)byteArrayReader.readUnsignedByte();
                ++n2;
            }
            byte[] launcherBytes = FileUtil.readBytes(ServerSettings.launcherJarPath, false);
            if (launcherBytes == null) {
                ServerSettings.cacheVerificationShutdownPending = false;
                return;
            }
            CRC32 crc32 = new CRC32();
            crc32.reset();
            crc32.update(launcherBytes);
            int actualCrc = (int)crc32.getValue();
            String actualMd5 = new BigInteger(1, MessageDigest.getInstance("MD5").digest(launcherBytes)).toString(16);
            String expectedMd5 = new BigInteger(1, expectedMd5Bytes).toString(16);
            String actualSha1 = new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(launcherBytes)).toString(16);
            String expectedSha1 = new BigInteger(1, expectedSha1Bytes).toString(16);
            if (expectedCrc != actualCrc || !expectedMd5.equals(actualMd5) || !expectedSha1.equals(actualSha1)) {
                if (Boolean.getBoolean("prs.traceGameplay")) {
                    System.out.println("[server-trace] launcher checksum mismatch ignored for deob build");
                }
            }
            ServerSettings.cacheVerificationShutdownPending = false;
            return;
        }
        catch (Exception exception) {
            if (Boolean.getBoolean("prs.traceGameplay")) {
                System.out.println("[server-trace] launcher checksum verification skipped: " + exception);
            }
            ServerSettings.cacheVerificationShutdownPending = false;
            return;
        }
    }

}

