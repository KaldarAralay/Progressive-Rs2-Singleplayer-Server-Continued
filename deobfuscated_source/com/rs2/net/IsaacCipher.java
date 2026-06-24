/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net;

public final class IsaacCipher {
    private int count = 0;
    private int[] results = new int[256];
    private int[] memory = new int[256];
    private int accumulator;
    private int lastResult;
    private int counter;

    public IsaacCipher(int[] nArray) {
        int n = 0;
        while (n < 4) {
            this.results[n] = nArray[n];
            ++n;
        }
        this.initialize(true);
    }

    public final int nextInt() {
        if (this.count-- == 0) {
            this.generateResults();
            this.count = 255;
        }
        return this.results[this.count];
    }

    private void generateResults() {
        int n;
        int n2;
        this.lastResult += ++this.counter;
        int n3 = 0;
        int n4 = 128;
        while (n3 < 128) {
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator << 13;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator >>> 6;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator << 2;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator >>> 16;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
        }
        n4 = 0;
        while (n4 < 128) {
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator << 13;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator >>> 6;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator << 2;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
            n2 = this.memory[n3];
            this.accumulator ^= this.accumulator >>> 16;
            this.accumulator += this.memory[n4++];
            this.memory[n3] = n = this.memory[(n2 & 0x3FC) >> 2] + this.accumulator + this.lastResult;
            this.results[n3++] = this.lastResult = this.memory[(n >> 8 & 0x3FC) >> 2] + n2;
        }
    }

    private void initialize(boolean seeded) {
        int n2 = -1640531527;
        int n3 = -1640531527;
        int n4 = -1640531527;
        int n5 = -1640531527;
        int n6 = -1640531527;
        int n7 = -1640531527;
        int n8 = -1640531527;
        int n9 = -1640531527;
        int n = 0;
        while (n < 4) {
            n6 += (n9 ^= n8 << 11);
            n8 += n7;
            n5 += (n8 ^= n7 >>> 2);
            n7 += n6;
            n4 += (n7 ^= n6 << 8);
            n6 += n5;
            n3 += (n6 ^= n5 >>> 16);
            n5 += n4;
            n2 += (n5 ^= n4 << 10);
            n4 += n3;
            n9 += (n4 ^= n3 >>> 4);
            n3 += n2;
            n8 += (n3 ^= n2 << 8);
            n2 += n9;
            n7 += (n2 ^= n9 >>> 9);
            n9 += n8;
            ++n;
        }
        n = 0;
        while (n < 256) {
            n9 += this.results[n];
            n8 += this.results[n + 1];
            n7 += this.results[n + 2];
            n6 += this.results[n + 3];
            n5 += this.results[n + 4];
            n4 += this.results[n + 5];
            n3 += this.results[n + 6];
            n2 += this.results[n + 7];
            n6 += (n9 ^= n8 << 11);
            n8 += n7;
            n5 += (n8 ^= n7 >>> 2);
            n7 += n6;
            n4 += (n7 ^= n6 << 8);
            n6 += n5;
            n3 += (n6 ^= n5 >>> 16);
            n5 += n4;
            n2 += (n5 ^= n4 << 10);
            n4 += n3;
            n9 += (n4 ^= n3 >>> 4);
            n3 += n2;
            n8 += (n3 ^= n2 << 8);
            n2 += n9;
            n7 += (n2 ^= n9 >>> 9);
            this.memory[n] = n9 += n8;
            this.memory[n + 1] = n8;
            this.memory[n + 2] = n7;
            this.memory[n + 3] = n6;
            this.memory[n + 4] = n5;
            this.memory[n + 5] = n4;
            this.memory[n + 6] = n3;
            this.memory[n + 7] = n2;
            n += 8;
        }
        n = 0;
        while (n < 256) {
            n9 += this.memory[n];
            n8 += this.memory[n + 1];
            n7 += this.memory[n + 2];
            n6 += this.memory[n + 3];
            n5 += this.memory[n + 4];
            n4 += this.memory[n + 5];
            n3 += this.memory[n + 6];
            n2 += this.memory[n + 7];
            n6 += (n9 ^= n8 << 11);
            n8 += n7;
            n5 += (n8 ^= n7 >>> 2);
            n7 += n6;
            n4 += (n7 ^= n6 << 8);
            n6 += n5;
            n3 += (n6 ^= n5 >>> 16);
            n5 += n4;
            n2 += (n5 ^= n4 << 10);
            n4 += n3;
            n9 += (n4 ^= n3 >>> 4);
            n3 += n2;
            n8 += (n3 ^= n2 << 8);
            n2 += n9;
            n7 += (n2 ^= n9 >>> 9);
            this.memory[n] = n9 += n8;
            this.memory[n + 1] = n8;
            this.memory[n + 2] = n7;
            this.memory[n + 3] = n6;
            this.memory[n + 4] = n5;
            this.memory[n + 5] = n4;
            this.memory[n + 6] = n3;
            this.memory[n + 7] = n2;
            n += 8;
        }
        this.generateResults();
        this.count = 256;
    }
}

