/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc.drop;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.player.Player;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import java.util.ArrayList;

public final class NpcDropTable {
    private static NpcDropTable[] tablesByNpcId = new NpcDropTable[0];
    private NpcDropEntry[] guaranteedDrops;
    private NpcDropEntry[] weightedDrops;
    private NpcDropEntry[] independentDrops;
    private static int[] membersOnlyVirtualDropIds = new int[]{65001, 65002, 65003, 65004, 65005, 65007, 65009, 65011, 65012, 65013, 65014, 65015, 65018, 65020, 65021, 65022, 65023, 65024, 65025, 65026, 65027, 65028, 65029, 65030};

    public static void loadDropTables() {
        if (!FileUtil.exists("./data/npcs/Npc drops.dat")) {
            return;
        }
        ByteArrayReader byteArrayReader = new ByteArrayReader(FileUtil.readBytes("./data/npcs/Npc drops.dat"));
        int n = byteArrayReader.readUnsignedByte();
        int n2 = byteArrayReader.readUnsignedShort();
        tablesByNpcId = new NpcDropTable[n2];
        int n3 = 0;
        while (n3 < n2) {
            int n4 = byteArrayReader.readUnsignedByte();
            if (n4 == 2) {
                int n5 = byteArrayReader.readUnsignedShort();
                NpcDefinition.forId(n3).setDropTableNpcIdOverride(n5);
            }
            if (n4 == 1) {
                NpcDropEntry[] npcDropEntryArray2 = null;
                NpcDropEntry[] npcDropEntryArray = null;
                NpcDropEntry[] npcDropEntryArray3 = null;
                NpcDropEntry[] npcDropEntryArray4 = null;
                NpcDropEntry[] npcDropEntryArray5 = null;
                NpcDropEntry[] npcDropEntryArray6 = null;
                NpcDropEntry[] npcDropEntryArray7 = null;
                int n6 = 0;
                while (n6 < 7) {
                    int n7 = byteArrayReader.readUnsignedByte();
                    if (n6 == 0) {
                        npcDropEntryArray2 = new NpcDropEntry[n7];
                    }
                    if (n6 == 1) {
                        npcDropEntryArray = new NpcDropEntry[n7];
                    }
                    if (n6 == 2) {
                        npcDropEntryArray3 = new NpcDropEntry[n7];
                    }
                    if (n6 == 3) {
                        npcDropEntryArray4 = new NpcDropEntry[n7];
                    }
                    if (n6 == 4) {
                        npcDropEntryArray5 = new NpcDropEntry[n7];
                    }
                    if (n6 == 5) {
                        npcDropEntryArray6 = new NpcDropEntry[n7];
                    }
                    if (n6 == 6) {
                        npcDropEntryArray7 = new NpcDropEntry[n7];
                    }
                    int n8 = 0;
                    while (n8 < n7) {
                        int n13 = -1;
                        int n14 = -1;
                        int n15 = -1;
                        if (n6 == 5 || n6 == 6) {
                            n13 = byteArrayReader.readUnsignedByte();
                            if (n == 1) {
                                n14 = byteArrayReader.readUnsignedByte();
                            }
                            if (n == 2) {
                                n14 = byteArrayReader.readUnsignedShort();
                            }
                            n15 = byteArrayReader.readInt();
                        }
                        int n12 = byteArrayReader.readUnsignedByte();
                        int n11 = -1;
                        int n16 = -1;
                        int n17 = -1;
                        int n18 = -1;
                        int[] nArray = null;
                        int[] nArray2 = null;
                        int[] nArray3 = null;
                        int[] nArray4 = null;
                        if (n12 != 3 && n12 != 4) {
                            n11 = byteArrayReader.readUnsignedShort();
                        }
                        if (n12 == 0) {
                            n16 = byteArrayReader.readUnsignedShort();
                        }
                        if (n12 == 1) {
                            n17 = byteArrayReader.readUnsignedShort();
                            n18 = byteArrayReader.readUnsignedShort();
                        }
                        if (n12 == 2) {
                            int n10 = byteArrayReader.readUnsignedByte();
                            nArray = new int[n10];
                            int n9 = 0;
                            while (n9 < n10) {
                                nArray[n9] = byteArrayReader.readUnsignedShort();
                                ++n9;
                            }
                        }
                        if (n12 == 3) {
                            int n10 = byteArrayReader.readUnsignedByte();
                            nArray = new int[n10];
                            nArray2 = new int[n10];
                            int n9 = 0;
                            while (n9 < n10) {
                                nArray2[n9] = byteArrayReader.readUnsignedShort();
                                nArray[n9] = byteArrayReader.readUnsignedShort();
                                ++n9;
                            }
                        }
                        if (n12 == 4) {
                            int n10 = byteArrayReader.readUnsignedByte();
                            nArray2 = new int[n10];
                            nArray3 = new int[n10];
                            nArray4 = new int[n10];
                            int n9 = 0;
                            while (n9 < n10) {
                                nArray2[n9] = byteArrayReader.readUnsignedShort();
                                nArray3[n9] = byteArrayReader.readUnsignedShort();
                                nArray4[n9] = byteArrayReader.readUnsignedShort();
                                ++n9;
                            }
                        }
                        NpcDropEntry npcDropEntry = new NpcDropEntry(n11, n16, n17, n18, nArray2, nArray, n13, n14, n15, nArray3, nArray4);
                        if (n6 == 0) {
                            npcDropEntryArray2[n8] = npcDropEntry;
                        }
                        if (n6 == 1) {
                            npcDropEntryArray[n8] = npcDropEntry;
                        }
                        if (n6 == 2) {
                            npcDropEntryArray3[n8] = npcDropEntry;
                        }
                        if (n6 == 3) {
                            npcDropEntryArray4[n8] = npcDropEntry;
                        }
                        if (n6 == 4) {
                            npcDropEntryArray5[n8] = npcDropEntry;
                        }
                        if (n6 == 5) {
                            npcDropEntryArray6[n8] = npcDropEntry;
                        }
                        if (n6 == 6) {
                            npcDropEntryArray7[n8] = npcDropEntry;
                        }
                        ++n8;
                    }
                    ++n6;
                }
                NpcDropTable.tablesByNpcId[n3] = new NpcDropTable(npcDropEntryArray2, npcDropEntryArray, npcDropEntryArray3, npcDropEntryArray4, npcDropEntryArray5, npcDropEntryArray6, npcDropEntryArray7);
            }
            ++n3;
        }
    }

    private NpcDropTable(NpcDropEntry[] npcDropEntryArray, NpcDropEntry[] npcDropEntryArray2, NpcDropEntry[] npcDropEntryArray3, NpcDropEntry[] npcDropEntryArray4, NpcDropEntry[] npcDropEntryArray5, NpcDropEntry[] npcDropEntryArray6, NpcDropEntry[] npcDropEntryArray7) {
        this.guaranteedDrops = npcDropEntryArray;
        this.weightedDrops = npcDropEntryArray6;
        this.independentDrops = npcDropEntryArray7;
    }

    public static NpcDropTable forNpcId(int n) {
        NpcDropTable npcDropTable;
        if (n < 0) {
            n = 1;
        }
        if ((npcDropTable = tablesByNpcId[n]) == null) {
            npcDropTable = new NpcDropTable(null, null, null, null, null, null, null);
        }
        return npcDropTable;
    }

    public final NpcDropEntry[] getGuaranteedDrops(Entity entity) {
        if (this.guaranteedDrops == null) {
            return null;
        }
        return NpcDropTable.filterDrops(this.guaranteedDrops, entity, false);
    }

    public final NpcDropEntry[] getWeightedDrops(Entity entity) {
        if (this.weightedDrops == null) {
            return null;
        }
        return NpcDropTable.filterDrops(this.weightedDrops, entity, true);
    }

    public final NpcDropEntry[] getIndependentDrops(Entity entity) {
        if (this.independentDrops == null) {
            return null;
        }
        return NpcDropTable.filterDrops(this.independentDrops, entity, true);
    }

    private static NpcDropEntry[] filterDrops(NpcDropEntry[] entries, Entity entity, boolean rejectUnavailableSeedTable) {
        Player player = null;
        if (entity.isPlayer()) {
            player = (Player)entity;
        }
        ArrayList<NpcDropEntry> arrayList = new ArrayList<NpcDropEntry>();
        int n = entries.length;
        int n2 = 0;
        while (n2 < n) {
            NpcDropEntry npcDropEntry = entries[n2];
            if (NpcDropTable.isAllowedDrop(npcDropEntry, player, rejectUnavailableSeedTable)) {
                arrayList.add(npcDropEntry);
            }
            ++n2;
        }
        return arrayList.toArray(new NpcDropEntry[arrayList.size()]);
    }

    private static boolean isAllowedDrop(NpcDropEntry npcDropEntry, Player player, boolean rejectUnavailableSeedTable) {
        if (npcDropEntry == null) {
            return false;
        }
        int itemId = npcDropEntry.getItemId();
        if (itemId < 65000) {
            return (itemId < 7956 || itemId > 8118) && ItemDefinition.isDefined(itemId) && (!ServerSettings.freeToPlayWorld && (player == null || player.isMember()) || !ItemDefinition.forId(itemId).isMembersOnly());
        }
        boolean membersOnly = false;
        int n3 = 0;
        while (n3 < membersOnlyVirtualDropIds.length) {
            int n4 = membersOnlyVirtualDropIds[n3];
            if (n4 == itemId) {
                membersOnly = true;
            }
            ++n3;
        }
        if ((ServerSettings.freeToPlayWorld || player != null && !player.isMember()) && membersOnly) {
            return false;
        }
        return !rejectUnavailableSeedTable || itemId - 65000 != 7 || ItemDefinition.isDefined(5300);
    }
}
