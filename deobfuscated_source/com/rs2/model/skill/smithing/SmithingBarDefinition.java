/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.smithing;

import com.rs2.model.skill.smithing.AdamantBarDefinition;
import com.rs2.model.skill.smithing.BronzeBarDefinition;
import com.rs2.model.skill.smithing.IronBarDefinition;
import com.rs2.model.skill.smithing.MithrilBarDefinition;
import com.rs2.model.skill.smithing.RuniteBarDefinition;
import com.rs2.model.skill.smithing.SmithableItemDefinition;
import com.rs2.model.skill.smithing.SteelBarDefinition;

public class SmithingBarDefinition {
    private static SmithingBarDefinition BRONZE_BAR;
    private static SmithingBarDefinition IRON_BAR;
    private static SmithingBarDefinition STEEL_BAR;
    private static SmithingBarDefinition MITHRIL_BAR;
    private static SmithingBarDefinition ADAMANT_BAR;
    private static SmithingBarDefinition RUNITE_BAR;
    public static SmithingBarDefinition[] VALUES;
    private SmithableItemDefinition[] smithableItems;
    private int requiredLevel;
    private double experiencePerBar;
    private int barItemId;
    public static int[] productNameTextIds;
    public static int[] barRequirementTextIds;
    public static int[] productItemInterfaceIds;
    public static int[] productItemSlots;

    static {
        SmithableItemDefinition[] smithableItemDefinitionArray = new SmithableItemDefinition[25];
        smithableItemDefinitionArray[0] = SmithableItemDefinition.a;
        smithableItemDefinitionArray[1] = SmithableItemDefinition.b;
        smithableItemDefinitionArray[2] = SmithableItemDefinition.q;
        smithableItemDefinitionArray[3] = SmithableItemDefinition.d;
        smithableItemDefinitionArray[4] = SmithableItemDefinition.f;
        smithableItemDefinitionArray[5] = SmithableItemDefinition.e;
        smithableItemDefinitionArray[6] = SmithableItemDefinition.c;
        smithableItemDefinitionArray[7] = SmithableItemDefinition.u;
        smithableItemDefinitionArray[8] = SmithableItemDefinition.l;
        smithableItemDefinitionArray[9] = SmithableItemDefinition.j;
        smithableItemDefinitionArray[10] = SmithableItemDefinition.i;
        smithableItemDefinitionArray[11] = SmithableItemDefinition.o;
        smithableItemDefinitionArray[12] = SmithableItemDefinition.v;
        smithableItemDefinitionArray[13] = SmithableItemDefinition.n;
        smithableItemDefinitionArray[14] = SmithableItemDefinition.m;
        smithableItemDefinitionArray[15] = SmithableItemDefinition.k;
        smithableItemDefinitionArray[16] = SmithableItemDefinition.p;
        smithableItemDefinitionArray[17] = SmithableItemDefinition.w;
        smithableItemDefinitionArray[18] = SmithableItemDefinition.r;
        smithableItemDefinitionArray[19] = SmithableItemDefinition.h;
        smithableItemDefinitionArray[20] = SmithableItemDefinition.t;
        smithableItemDefinitionArray[21] = SmithableItemDefinition.s;
        smithableItemDefinitionArray[24] = SmithableItemDefinition.g;
        BRONZE_BAR = new BronzeBarDefinition(1, 12.5, 2349, smithableItemDefinitionArray);
        SmithableItemDefinition[] smithableItemDefinitionArray2 = new SmithableItemDefinition[25];
        smithableItemDefinitionArray2[0] = SmithableItemDefinition.x;
        smithableItemDefinitionArray2[1] = SmithableItemDefinition.y;
        smithableItemDefinitionArray2[2] = SmithableItemDefinition.N;
        smithableItemDefinitionArray2[3] = SmithableItemDefinition.B;
        smithableItemDefinitionArray2[4] = SmithableItemDefinition.D;
        smithableItemDefinitionArray2[5] = SmithableItemDefinition.C;
        smithableItemDefinitionArray2[6] = SmithableItemDefinition.z;
        smithableItemDefinitionArray2[7] = SmithableItemDefinition.S;
        smithableItemDefinitionArray2[8] = SmithableItemDefinition.I;
        smithableItemDefinitionArray2[9] = SmithableItemDefinition.G;
        smithableItemDefinitionArray2[10] = SmithableItemDefinition.F;
        smithableItemDefinitionArray2[11] = SmithableItemDefinition.L;
        smithableItemDefinitionArray2[12] = SmithableItemDefinition.T;
        smithableItemDefinitionArray2[13] = SmithableItemDefinition.K;
        smithableItemDefinitionArray2[14] = SmithableItemDefinition.J;
        smithableItemDefinitionArray2[15] = SmithableItemDefinition.H;
        smithableItemDefinitionArray2[16] = SmithableItemDefinition.M;
        smithableItemDefinitionArray2[17] = SmithableItemDefinition.U;
        smithableItemDefinitionArray2[18] = SmithableItemDefinition.P;
        smithableItemDefinitionArray2[19] = SmithableItemDefinition.E;
        smithableItemDefinitionArray2[20] = SmithableItemDefinition.R;
        smithableItemDefinitionArray2[21] = SmithableItemDefinition.Q;
        smithableItemDefinitionArray2[22] = SmithableItemDefinition.O;
        smithableItemDefinitionArray2[24] = SmithableItemDefinition.A;
        IRON_BAR = new IronBarDefinition(15, 25.0, 2351, smithableItemDefinitionArray2);
        SmithableItemDefinition[] smithableItemDefinitionArray3 = new SmithableItemDefinition[25];
        smithableItemDefinitionArray3[0] = SmithableItemDefinition.V;
        smithableItemDefinitionArray3[1] = SmithableItemDefinition.W;
        smithableItemDefinitionArray3[2] = SmithableItemDefinition.al;
        smithableItemDefinitionArray3[3] = SmithableItemDefinition.Y;
        smithableItemDefinitionArray3[4] = SmithableItemDefinition.aa;
        smithableItemDefinitionArray3[5] = SmithableItemDefinition.Z;
        smithableItemDefinitionArray3[6] = SmithableItemDefinition.X;
        smithableItemDefinitionArray3[7] = SmithableItemDefinition.ap;
        smithableItemDefinitionArray3[8] = SmithableItemDefinition.ag;
        smithableItemDefinitionArray3[9] = SmithableItemDefinition.ad;
        smithableItemDefinitionArray3[10] = SmithableItemDefinition.ac;
        smithableItemDefinitionArray3[11] = SmithableItemDefinition.aj;
        smithableItemDefinitionArray3[12] = SmithableItemDefinition.aq;
        smithableItemDefinitionArray3[13] = SmithableItemDefinition.ai;
        smithableItemDefinitionArray3[14] = SmithableItemDefinition.ah;
        smithableItemDefinitionArray3[15] = SmithableItemDefinition.ae;
        smithableItemDefinitionArray3[16] = SmithableItemDefinition.ak;
        smithableItemDefinitionArray3[17] = SmithableItemDefinition.ar;
        smithableItemDefinitionArray3[18] = SmithableItemDefinition.am;
        smithableItemDefinitionArray3[19] = SmithableItemDefinition.ab;
        smithableItemDefinitionArray3[20] = SmithableItemDefinition.ao;
        smithableItemDefinitionArray3[21] = SmithableItemDefinition.an;
        smithableItemDefinitionArray3[22] = SmithableItemDefinition.as;
        smithableItemDefinitionArray3[24] = SmithableItemDefinition.af;
        STEEL_BAR = new SteelBarDefinition(30, 37.5, 2353, smithableItemDefinitionArray3);
        SmithableItemDefinition[] smithableItemDefinitionArray4 = new SmithableItemDefinition[25];
        smithableItemDefinitionArray4[0] = SmithableItemDefinition.at;
        smithableItemDefinitionArray4[1] = SmithableItemDefinition.au;
        smithableItemDefinitionArray4[2] = SmithableItemDefinition.aI;
        smithableItemDefinitionArray4[3] = SmithableItemDefinition.aw;
        smithableItemDefinitionArray4[4] = SmithableItemDefinition.ay;
        smithableItemDefinitionArray4[5] = SmithableItemDefinition.ax;
        smithableItemDefinitionArray4[6] = SmithableItemDefinition.av;
        smithableItemDefinitionArray4[7] = SmithableItemDefinition.aM;
        smithableItemDefinitionArray4[8] = SmithableItemDefinition.aD;
        smithableItemDefinitionArray4[9] = SmithableItemDefinition.aB;
        smithableItemDefinitionArray4[10] = SmithableItemDefinition.aA;
        smithableItemDefinitionArray4[11] = SmithableItemDefinition.aG;
        smithableItemDefinitionArray4[12] = SmithableItemDefinition.aN;
        smithableItemDefinitionArray4[13] = SmithableItemDefinition.aF;
        smithableItemDefinitionArray4[14] = SmithableItemDefinition.aE;
        smithableItemDefinitionArray4[15] = SmithableItemDefinition.aC;
        smithableItemDefinitionArray4[16] = SmithableItemDefinition.aH;
        smithableItemDefinitionArray4[17] = SmithableItemDefinition.aO;
        smithableItemDefinitionArray4[18] = SmithableItemDefinition.aJ;
        smithableItemDefinitionArray4[19] = SmithableItemDefinition.az;
        smithableItemDefinitionArray4[20] = SmithableItemDefinition.aL;
        smithableItemDefinitionArray4[21] = SmithableItemDefinition.aK;
        MITHRIL_BAR = new MithrilBarDefinition(50, 50.0, 2359, smithableItemDefinitionArray4);
        SmithableItemDefinition[] smithableItemDefinitionArray5 = new SmithableItemDefinition[25];
        smithableItemDefinitionArray5[0] = SmithableItemDefinition.aP;
        smithableItemDefinitionArray5[1] = SmithableItemDefinition.aQ;
        smithableItemDefinitionArray5[2] = SmithableItemDefinition.be;
        smithableItemDefinitionArray5[3] = SmithableItemDefinition.aS;
        smithableItemDefinitionArray5[4] = SmithableItemDefinition.aU;
        smithableItemDefinitionArray5[5] = SmithableItemDefinition.aT;
        smithableItemDefinitionArray5[6] = SmithableItemDefinition.aR;
        smithableItemDefinitionArray5[7] = SmithableItemDefinition.bi;
        smithableItemDefinitionArray5[8] = SmithableItemDefinition.aZ;
        smithableItemDefinitionArray5[9] = SmithableItemDefinition.aX;
        smithableItemDefinitionArray5[10] = SmithableItemDefinition.aW;
        smithableItemDefinitionArray5[11] = SmithableItemDefinition.bc;
        smithableItemDefinitionArray5[12] = SmithableItemDefinition.bj;
        smithableItemDefinitionArray5[13] = SmithableItemDefinition.bb;
        smithableItemDefinitionArray5[14] = SmithableItemDefinition.ba;
        smithableItemDefinitionArray5[15] = SmithableItemDefinition.aY;
        smithableItemDefinitionArray5[16] = SmithableItemDefinition.bd;
        smithableItemDefinitionArray5[17] = SmithableItemDefinition.bk;
        smithableItemDefinitionArray5[18] = SmithableItemDefinition.bf;
        smithableItemDefinitionArray5[19] = SmithableItemDefinition.aV;
        smithableItemDefinitionArray5[20] = SmithableItemDefinition.bh;
        smithableItemDefinitionArray5[21] = SmithableItemDefinition.bg;
        ADAMANT_BAR = new AdamantBarDefinition(70, 62.5, 2361, smithableItemDefinitionArray5);
        SmithableItemDefinition[] smithableItemDefinitionArray6 = new SmithableItemDefinition[25];
        smithableItemDefinitionArray6[0] = SmithableItemDefinition.bl;
        smithableItemDefinitionArray6[1] = SmithableItemDefinition.bm;
        smithableItemDefinitionArray6[2] = SmithableItemDefinition.bA;
        smithableItemDefinitionArray6[3] = SmithableItemDefinition.bo;
        smithableItemDefinitionArray6[4] = SmithableItemDefinition.bq;
        smithableItemDefinitionArray6[5] = SmithableItemDefinition.bp;
        smithableItemDefinitionArray6[6] = SmithableItemDefinition.bn;
        smithableItemDefinitionArray6[7] = SmithableItemDefinition.bE;
        smithableItemDefinitionArray6[8] = SmithableItemDefinition.bv;
        smithableItemDefinitionArray6[9] = SmithableItemDefinition.bt;
        smithableItemDefinitionArray6[10] = SmithableItemDefinition.bs;
        smithableItemDefinitionArray6[11] = SmithableItemDefinition.by;
        smithableItemDefinitionArray6[12] = SmithableItemDefinition.bF;
        smithableItemDefinitionArray6[13] = SmithableItemDefinition.bx;
        smithableItemDefinitionArray6[14] = SmithableItemDefinition.bw;
        smithableItemDefinitionArray6[15] = SmithableItemDefinition.bu;
        smithableItemDefinitionArray6[16] = SmithableItemDefinition.bz;
        smithableItemDefinitionArray6[17] = SmithableItemDefinition.bG;
        smithableItemDefinitionArray6[18] = SmithableItemDefinition.bB;
        smithableItemDefinitionArray6[19] = SmithableItemDefinition.br;
        smithableItemDefinitionArray6[20] = SmithableItemDefinition.bD;
        smithableItemDefinitionArray6[21] = SmithableItemDefinition.bC;
        RUNITE_BAR = new RuniteBarDefinition(85, 75.0, 2363, smithableItemDefinitionArray6);
        VALUES = new SmithingBarDefinition[]{BRONZE_BAR, IRON_BAR, STEEL_BAR, MITHRIL_BAR, ADAMANT_BAR, RUNITE_BAR};
        productNameTextIds = new int[]{1094, 1091, 1098, 1102, 1107, 1085, 1093, 1099, 1103, 1108, 1087, 1083, 1100, 1104, 1106, 1086, 1092, 1101, 1105, 1096, 1088, 8429, 11461, 13358, 1134};
        barRequirementTextIds = new int[]{1125, 1126, 1109, 1127, 1128, 1124, 1129, 1110, 1113, 1130, 1116, 1118, 1111, 1114, 1131, 1089, 1095, 1112, 1115, 1132, 1090, 8428, 11459, 13357, 1135};
        productItemInterfaceIds = new int[]{1119, 1120, 1121, 1122, 1123, 1119, 1120, 1121, 1122, 1123, 1119, 1120, 1121, 1122, 1123, 1119, 1120, 1121, 1122, 1123, 1119, 1120, 1121, 1122, 1123};
        int[] nArray = new int[25];
        nArray[5] = 1;
        nArray[6] = 1;
        nArray[7] = 1;
        nArray[8] = 1;
        nArray[9] = 1;
        nArray[10] = 2;
        nArray[11] = 2;
        nArray[12] = 2;
        nArray[13] = 2;
        nArray[14] = 2;
        nArray[15] = 3;
        nArray[16] = 3;
        nArray[17] = 3;
        nArray[18] = 3;
        nArray[19] = 3;
        nArray[20] = 4;
        nArray[21] = 4;
        nArray[22] = 4;
        nArray[23] = 4;
        nArray[24] = 4;
        productItemSlots = nArray;
    }

    public static SmithingBarDefinition forBarItemId(int n) {
        SmithingBarDefinition[] smithingBarDefinitionArray = VALUES;
        int n2 = VALUES.length;
        int n3 = 0;
        while (n3 < n2) {
            SmithingBarDefinition smithingBarDefinition;
            SmithingBarDefinition smithingBarDefinition2 = smithingBarDefinition = smithingBarDefinitionArray[n3];
            if (smithingBarDefinition.barItemId == n) {
                return smithingBarDefinition;
            }
            ++n3;
        }
        return null;
    }

    public final SmithableItemDefinition[] getSmithableItems() {
        return this.smithableItems;
    }

    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    public final double getExperiencePerBar() {
        return this.experiencePerBar;
    }

    public final int getBarItemId() {
        return this.barItemId;
    }

    public SmithingBarDefinition(int n, double d, int n2, SmithableItemDefinition[] smithableItemDefinitionArray) {
        this.smithableItems = smithableItemDefinitionArray;
        this.requiredLevel = n;
        this.experiencePerBar = d;
        this.barItemId = n2;
    }
}

