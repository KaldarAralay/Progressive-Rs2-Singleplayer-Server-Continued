/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc;

import com.rs2.ServerSettings;
import com.rs2.cache.CacheArchive;
import com.rs2.cache.CacheStore;
import com.rs2.model.World;
import com.rs2.model.npc.combat.NpcCombatDefinition;
import com.rs2.model.npc.combat.NpcDefinitionAttackStyleCombatDefinition;
import com.rs2.model.npc.combat.NpcDefinitionMeleeCombatDefinition;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import java.io.IOException;

public final class NpcDefinition {
    private static int customDefinitionCount = 0;
    private int aggressionRange;
    private int spawnRadius = 5;
    private int chaseRadius;
    private int attackBonusTypeId = 0;
    private int respawnDelayTicks = 0;
    private int attackLevel = 0;
    private int strengthLevel = 0;
    private int defenceLevel = 0;
    private int magicLevel = 0;
    private int rangedLevel = 0;
    private int meleeAttackBonus = 0;
    private int meleeStrengthBonus = 0;
    private int magicAttackBonus = 0;
    private int p = 0;
    private int rangedAttackBonus = 0;
    private int r = 0;
    private boolean s = false;
    private boolean t = false;
    private int[] defenceBonuses;
    private int poisonDamage = 0;
    private double poisonChance = 0.0;
    private static int cacheDefinitionCount;
    private int shopId = -1;
    private int id;
    private int dropTableNpcIdOverride = -1;
    private String name;
    public int respawnDelaySeconds = 0;
    private int legacyAttackBonus = 20;
    private int legacyMeleeDefenceBonus = 20;
    private int legacyRangedDefenceBonus = 20;
    private int legacyMagicDefenceBonus = 20;
    private int combatLevel = 0;
    private int hitpoints = 1;
    private int maxHit = 0;
    private int size = 1;
    private int attackDelay = 4;
    private int attackAnimationId = 422;
    private int blockAnimationId = 404;
    private int deathAnimationId = 2304;
    private int hitSoundId = -1;
    private int attackSoundId = -1;
    private int deathSoundId = -1;
    private boolean attackable = false;
    private int aggressionType = 0;
    private boolean T = false;
    private boolean U = false;
    private boolean protectedFromMelee = false;
    private boolean protectedFromRanged = false;
    private boolean protectedFromMagic = false;

    public static void loadDefinitions() {
        int n;
        int n2;
        int n3;
        int n4;
        Object object;
        Object object2;
        try {
            object2 = FileUtil.readBytes("data/npcs/npcDefinitions.dat");
            ByteArrayReader byteArrayReader = new ByteArrayReader((byte[])object2);
            object2 = byteArrayReader;
            customDefinitionCount = byteArrayReader.readUnsignedShort();
            int n5 = 0;
            while (n5 < customDefinitionCount) {
                object = NpcDefinition.forId(n5);
                int n6 = ((ByteArrayReader)object2).readUnsignedByte();
                ((NpcDefinition)object).id = n5;
                ((NpcDefinition)object).hitpoints = 0;
                if (n6 == 1) {
                    ((NpcDefinition)object).respawnDelaySeconds = 60;
                    ((NpcDefinition)object).aggressionType = ((ByteArrayReader)object2).readUnsignedByte();
                    ((NpcDefinition)object).T = ((ByteArrayReader)object2).readUnsignedByte() == 1;
                    ((NpcDefinition)object).U = ((ByteArrayReader)object2).readUnsignedByte() == 1;
                    ((NpcDefinition)object).maxHit = ((ByteArrayReader)object2).readUnsignedByte() - 1;
                    ((NpcDefinition)object).hitpoints = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).attackDelay = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).attackAnimationId = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).blockAnimationId = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).deathAnimationId = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).attackSoundId = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).hitSoundId = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).deathSoundId = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).legacyAttackBonus = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).legacyMeleeDefenceBonus = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).legacyRangedDefenceBonus = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).legacyMagicDefenceBonus = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).attackBonusTypeId = ((ByteArrayReader)object2).readUnsignedByte() - 1;
                    if (((NpcDefinition)object).attackBonusTypeId >= 3) {
                        ((NpcDefinition)object).attackBonusTypeId = 0;
                    }
                    ((NpcDefinition)object).respawnDelayTicks = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).attackLevel = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).strengthLevel = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).defenceLevel = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).magicLevel = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).rangedLevel = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    ((NpcDefinition)object).meleeAttackBonus = ((ByteArrayReader)object2).readShort() - 1;
                    ((NpcDefinition)object).meleeStrengthBonus = ((ByteArrayReader)object2).readShort() - 1;
                    ((NpcDefinition)object).magicAttackBonus = ((ByteArrayReader)object2).readShort() - 1;
                    ((NpcDefinition)object).p = ((ByteArrayReader)object2).readShort() - 1;
                    ((NpcDefinition)object).rangedAttackBonus = ((ByteArrayReader)object2).readShort() - 1;
                    ((NpcDefinition)object).r = ((ByteArrayReader)object2).readShort() - 1;
                    int n7 = ((ByteArrayReader)object2).readShort() - 1;
                    n4 = ((ByteArrayReader)object2).readShort() - 1;
                    int n8 = ((ByteArrayReader)object2).readShort() - 1;
                    n3 = ((ByteArrayReader)object2).readShort() - 1;
                    n2 = ((ByteArrayReader)object2).readShort() - 1;
                    int[] nArray = new int[]{n7, n4, n8, n3, n2};
                    ((NpcDefinition)object).defenceBonuses = nArray;
                    ((NpcDefinition)object).s = ((ByteArrayReader)object2).readUnsignedByte() == 1;
                    ((NpcDefinition)object).t = ((ByteArrayReader)object2).readUnsignedByte() == 1;
                    ((NpcDefinition)object).poisonDamage = ((ByteArrayReader)object2).readUnsignedByte();
                    double d = ((ByteArrayReader)object2).readShort();
                    ((NpcDefinition)object).poisonChance = d / 1000.0;
                    ((NpcDefinition)object).spawnRadius = ((ByteArrayReader)object2).readUnsignedByte();
                    ((NpcDefinition)object).chaseRadius = ((ByteArrayReader)object2).readUnsignedByte();
                    if (((NpcDefinition)object).aggressionType > 0) {
                        ((NpcDefinition)object).aggressionRange = ((ByteArrayReader)object2).readUnsignedByte();
                    }
                }
                if (n6 == 2) {
                    ((NpcDefinition)object).shopId = ((ByteArrayReader)object2).readUnsignedShort() - 2;
                    ((NpcDefinition)object).spawnRadius = ((ByteArrayReader)object2).readUnsignedByte();
                }
                if (n6 == 3) {
                    int n9 = ((ByteArrayReader)object2).readUnsignedShort() - 1;
                    NpcDefinition npcDefinition = NpcDefinition.forId(n9);
                    ((NpcDefinition)object).respawnDelaySeconds = npcDefinition.respawnDelaySeconds;
                    ((NpcDefinition)object).aggressionType = npcDefinition.aggressionType;
                    ((NpcDefinition)object).T = npcDefinition.T;
                    ((NpcDefinition)object).U = npcDefinition.U;
                    ((NpcDefinition)object).maxHit = npcDefinition.maxHit;
                    ((NpcDefinition)object).hitpoints = npcDefinition.hitpoints;
                    ((NpcDefinition)object).attackDelay = npcDefinition.attackDelay;
                    ((NpcDefinition)object).attackAnimationId = npcDefinition.attackAnimationId;
                    ((NpcDefinition)object).blockAnimationId = npcDefinition.blockAnimationId;
                    ((NpcDefinition)object).deathAnimationId = npcDefinition.deathAnimationId;
                    ((NpcDefinition)object).attackSoundId = npcDefinition.attackSoundId;
                    ((NpcDefinition)object).hitSoundId = npcDefinition.hitSoundId;
                    ((NpcDefinition)object).deathSoundId = npcDefinition.deathSoundId;
                    ((NpcDefinition)object).legacyAttackBonus = npcDefinition.legacyAttackBonus;
                    ((NpcDefinition)object).legacyMeleeDefenceBonus = npcDefinition.legacyMeleeDefenceBonus;
                    ((NpcDefinition)object).legacyRangedDefenceBonus = npcDefinition.legacyRangedDefenceBonus;
                    ((NpcDefinition)object).legacyMagicDefenceBonus = npcDefinition.legacyMagicDefenceBonus;
                    ((NpcDefinition)object).shopId = npcDefinition.shopId;
                    ((NpcDefinition)object).attackBonusTypeId = npcDefinition.attackBonusTypeId;
                    ((NpcDefinition)object).respawnDelayTicks = npcDefinition.respawnDelayTicks;
                    ((NpcDefinition)object).attackLevel = npcDefinition.attackLevel;
                    ((NpcDefinition)object).strengthLevel = npcDefinition.strengthLevel;
                    ((NpcDefinition)object).defenceLevel = npcDefinition.defenceLevel;
                    ((NpcDefinition)object).magicLevel = npcDefinition.magicLevel;
                    ((NpcDefinition)object).rangedLevel = npcDefinition.rangedLevel;
                    ((NpcDefinition)object).meleeAttackBonus = npcDefinition.meleeAttackBonus;
                    ((NpcDefinition)object).meleeStrengthBonus = npcDefinition.meleeStrengthBonus;
                    ((NpcDefinition)object).magicAttackBonus = npcDefinition.magicAttackBonus;
                    ((NpcDefinition)object).p = npcDefinition.p;
                    ((NpcDefinition)object).rangedAttackBonus = npcDefinition.rangedAttackBonus;
                    ((NpcDefinition)object).r = npcDefinition.r;
                    ((NpcDefinition)object).defenceBonuses = npcDefinition.defenceBonuses;
                    ((NpcDefinition)object).s = npcDefinition.s;
                    ((NpcDefinition)object).t = npcDefinition.t;
                    ((NpcDefinition)object).poisonDamage = npcDefinition.poisonDamage;
                    ((NpcDefinition)object).poisonChance = npcDefinition.poisonChance;
                    ((NpcDefinition)object).aggressionRange = npcDefinition.aggressionRange;
                    ((NpcDefinition)object).spawnRadius = npcDefinition.spawnRadius;
                    ((NpcDefinition)object).chaseRadius = npcDefinition.chaseRadius;
                }
                if (ServerSettings.cacheVersion < 319 && ((NpcDefinition)object).blockAnimationId == 1834) {
                    ((NpcDefinition)object).blockAnimationId = 424;
                }
                if (ServerSettings.cacheVersion < 327 && ((NpcDefinition)object).deathAnimationId == 2304) {
                    ((NpcDefinition)object).deathAnimationId = 836;
                }
                if (ServerSettings.cacheVersion < 337 && n5 == 750) {
                    NpcDefinition npcDefinition = NpcDefinition.forId(92);
                    ((NpcDefinition)object).attackAnimationId = npcDefinition.attackAnimationId;
                    ((NpcDefinition)object).blockAnimationId = npcDefinition.blockAnimationId;
                    ((NpcDefinition)object).deathAnimationId = npcDefinition.deathAnimationId;
                }
                NpcDefinition npcDefinition2 = (NpcDefinition)object;
                World.getNpcDefinitions()[npcDefinition2.id] = npcDefinition2;
                ++n5;
            }
        }
        catch (Exception exception) {
            object2 = exception;
            exception.printStackTrace();
        }
        object2 = CacheStore.getInstance();
        ByteArrayReader byteArrayReader = null;
        try {
            byteArrayReader = new ByteArrayReader(new CacheArchive(((CacheStore)object2).readFile(0, 2)).getFileBytes("npc.dat"));
        }
        catch (Exception exception) {
            object = exception;
            exception.printStackTrace();
        }
        cacheDefinitionCount = n = ((CacheStore)object2).getDefinitionIndex().getNpcDefinitionEntries().length;
        byteArrayReader.position = 2;
        int n10 = 0;
        while (n10 < n) {
            byteArrayReader.position = ((CacheStore)object2).getDefinitionIndex().getNpcDefinitionEntry(n10).getDataOffset();
            n4 = n10;
            ByteArrayReader byteArrayReader2 = byteArrayReader;
            NpcDefinition npcDefinition = NpcDefinition.forId(n4);
            while ((n3 = byteArrayReader2.readUnsignedByte()) != 0) {
                if (n3 == 1) {
                    n3 = byteArrayReader2.readUnsignedByte();
                    n2 = 0;
                    while (n2 < n3) {
                        byteArrayReader2.readUnsignedShort();
                        ++n2;
                    }
                    continue;
                }
                if (n3 == 2) {
                    npcDefinition.name = byteArrayReader2.readString();
                    continue;
                }
                if (n3 == 3) {
                    new String(byteArrayReader2.readLineBytes());
                    continue;
                }
                if (n3 == 12) {
                    npcDefinition.size = byteArrayReader2.readByte();
                    if (n4 != 1431 && n4 != 1432) continue;
                    npcDefinition.size = 1;
                    continue;
                }
                if (n3 == 13) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 14) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 17) {
                    byteArrayReader2.readUnsignedShort();
                    byteArrayReader2.readUnsignedShort();
                    byteArrayReader2.readUnsignedShort();
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 >= 30 && n3 < 40) {
                    String string = byteArrayReader2.readString();
                    if (!string.toLowerCase().equals("attack") || npcDefinition.hitpoints <= 0) continue;
                    npcDefinition.attackable = true;
                    continue;
                }
                if (n3 == 40) {
                    n3 = byteArrayReader2.readUnsignedByte();
                    n2 = 0;
                    while (n2 < n3) {
                        byteArrayReader2.readUnsignedShort();
                        byteArrayReader2.readUnsignedShort();
                        ++n2;
                    }
                    continue;
                }
                if (n3 == 60) {
                    n3 = byteArrayReader2.readUnsignedByte();
                    n2 = 0;
                    while (n2 < n3) {
                        byteArrayReader2.readUnsignedShort();
                        ++n2;
                    }
                    continue;
                }
                if (n3 == 90) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 91) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 92) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 93) continue;
                if (n3 == 95) {
                    npcDefinition.combatLevel = byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 97) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 98) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 == 99) continue;
                if (n3 == 100) {
                    byteArrayReader2.readByte();
                    continue;
                }
                if (n3 == 101) {
                    byteArrayReader2.readByte();
                    continue;
                }
                if (n3 == 102) {
                    n3 = byteArrayReader2.readUnsignedShort();
                    if (n3 == 0) {
                        npcDefinition.protectedFromMelee = true;
                        continue;
                    }
                    if (n3 == 1) {
                        npcDefinition.protectedFromRanged = true;
                        continue;
                    }
                    if (n3 == 2) {
                        npcDefinition.protectedFromMagic = true;
                        continue;
                    }
                    if (n3 != 6) continue;
                    npcDefinition.protectedFromRanged = true;
                    npcDefinition.protectedFromMagic = true;
                    continue;
                }
                if (n3 == 103) {
                    byteArrayReader2.readUnsignedShort();
                    continue;
                }
                if (n3 != 106) continue;
                byteArrayReader2.readUnsignedShort();
                byteArrayReader2.readUnsignedShort();
                n3 = byteArrayReader2.readUnsignedByte();
                n2 = 0;
                while (n2 <= n3) {
                    byteArrayReader2.readUnsignedShort();
                    ++n2;
                }
            }
            ++n10;
        }
        NpcDefinition.initializeCombatDefinitions();
        NpcDefinition.copyDefinition(2257, 2258);
        NpcDefinition.copyDefinition(2260, 2261);
    }

    private static void copyDefinition(int n, int n2) {
        NpcDefinition npcDefinition = NpcDefinition.forId(n);
        NpcDefinition npcDefinition2 = NpcDefinition.forId(n2);
        npcDefinition.respawnDelaySeconds = npcDefinition2.respawnDelaySeconds;
        npcDefinition.aggressionType = npcDefinition2.aggressionType;
        npcDefinition.T = npcDefinition2.T;
        npcDefinition.U = npcDefinition2.U;
        npcDefinition.maxHit = npcDefinition2.maxHit;
        npcDefinition.hitpoints = npcDefinition2.hitpoints;
        npcDefinition.attackDelay = npcDefinition2.attackDelay;
        npcDefinition.attackAnimationId = npcDefinition2.attackAnimationId;
        npcDefinition.blockAnimationId = npcDefinition2.blockAnimationId;
        npcDefinition.deathAnimationId = npcDefinition2.deathAnimationId;
        npcDefinition.attackSoundId = npcDefinition2.attackSoundId;
        npcDefinition.hitSoundId = npcDefinition2.hitSoundId;
        npcDefinition.deathSoundId = npcDefinition2.deathSoundId;
        npcDefinition.legacyAttackBonus = npcDefinition2.legacyAttackBonus;
        npcDefinition.legacyMeleeDefenceBonus = npcDefinition2.legacyMeleeDefenceBonus;
        npcDefinition.legacyRangedDefenceBonus = npcDefinition2.legacyRangedDefenceBonus;
        npcDefinition.legacyMagicDefenceBonus = npcDefinition2.legacyMagicDefenceBonus;
        npcDefinition.shopId = npcDefinition2.shopId;
        npcDefinition.attackBonusTypeId = npcDefinition2.attackBonusTypeId;
        npcDefinition.respawnDelayTicks = npcDefinition2.respawnDelayTicks;
        npcDefinition.attackLevel = npcDefinition2.attackLevel;
        npcDefinition.strengthLevel = npcDefinition2.strengthLevel;
        npcDefinition.defenceLevel = npcDefinition2.defenceLevel;
        npcDefinition.magicLevel = npcDefinition2.magicLevel;
        npcDefinition.rangedLevel = npcDefinition2.rangedLevel;
        npcDefinition.meleeAttackBonus = npcDefinition2.meleeAttackBonus;
        npcDefinition.meleeStrengthBonus = npcDefinition2.meleeStrengthBonus;
        npcDefinition.magicAttackBonus = npcDefinition2.magicAttackBonus;
        npcDefinition.p = npcDefinition2.p;
        npcDefinition.rangedAttackBonus = npcDefinition2.rangedAttackBonus;
        npcDefinition.r = npcDefinition2.r;
        npcDefinition.defenceBonuses = npcDefinition2.defenceBonuses;
        npcDefinition.s = npcDefinition2.s;
        npcDefinition.t = npcDefinition2.t;
        npcDefinition.poisonDamage = npcDefinition2.poisonDamage;
        npcDefinition.poisonChance = npcDefinition2.poisonChance;
        npcDefinition.aggressionRange = npcDefinition2.aggressionRange;
        npcDefinition.spawnRadius = npcDefinition2.spawnRadius;
        npcDefinition.chaseRadius = npcDefinition2.chaseRadius;
        npcDefinition.name = npcDefinition2.name;
    }

    private static void initializeCombatDefinitions() {
        block22: {
            if (!ServerSettings.modernCombatSystemEnabled) break block22;
            int n = 0;
            while (n < customDefinitionCount) {
                NpcCombatDefinition npcCombatDefinition;
                NpcDefinition npcDefinition;
                NpcDefinition npcDefinition2;
                block23: {
                    block25: {
                        block24: {
                            npcDefinition = npcDefinition2 = NpcDefinition.forId(n);
                            boolean bl = NpcCombatDefinition.isRegistered(npcDefinition2.id);
                            npcDefinition = npcDefinition2;
                            npcCombatDefinition = NpcCombatDefinition.forNpcId(npcDefinition.id);
                            if (bl) break block23;
                            NpcDefinition npcDefinition3 = npcDefinition2;
                            npcDefinition = npcDefinition3;
                            NpcDefinition npcDefinition4 = npcDefinition2;
                            npcDefinition = npcDefinition4;
                            NpcDefinition npcDefinition5 = npcDefinition2;
                            npcDefinition = npcDefinition5;
                            NpcDefinition npcDefinition6 = npcDefinition2;
                            npcDefinition = npcDefinition6;
                            NpcDefinition npcDefinition7 = npcDefinition2;
                            npcDefinition = npcDefinition7;
                            npcDefinition = npcDefinition2;
                            npcCombatDefinition = new NpcDefinitionAttackStyleCombatDefinition(npcDefinition2).setRespawnDelayTicks(npcDefinition3.respawnDelayTicks).addAttackBonuses(npcDefinition4.meleeAttackBonus, npcDefinition5.meleeAttackBonus, npcDefinition6.meleeAttackBonus, npcDefinition7.magicAttackBonus, npcDefinition.rangedAttackBonus).addDefenceBonuses(npcDefinition2.getDefenceBonus(5), npcDefinition2.getDefenceBonus(6), npcDefinition2.getDefenceBonus(7), npcDefinition2.getDefenceBonus(8), npcDefinition2.getDefenceBonus(9));
                            npcDefinition = npcDefinition2;
                            if (npcDefinition.id == 919) break block24;
                            npcDefinition = npcDefinition2;
                            if (npcDefinition.id == 920) break block24;
                            npcDefinition = npcDefinition2;
                            if (npcDefinition.id == 385) break block24;
                            npcDefinition = npcDefinition2;
                            if (npcDefinition.id == 386) break block24;
                            npcDefinition = npcDefinition2;
                            if (npcDefinition.id == 387) break block24;
                            npcDefinition = npcDefinition2;
                            if (npcDefinition.id != 759) break block25;
                        }
                        npcCombatDefinition.setRespawnDelayTicks(150);
                    }
                    npcDefinition = npcDefinition2;
                    if (npcDefinition.name.toLowerCase().equals("fishing spot")) {
                        npcCombatDefinition.setRespawnDelayTicks(50);
                    }
                }
                int[] nArray = new int[1];
                npcDefinition = npcDefinition2;
                nArray[0] = npcDefinition.id;
                NpcCombatDefinition.register(nArray, npcCombatDefinition);
                ++n;
            }
            return;
        }
        int n = 0;
        while (n < customDefinitionCount) {
            NpcCombatDefinition npcCombatDefinition;
            NpcDefinition npcDefinition;
            NpcDefinition npcDefinition8;
            block27: {
                block29: {
                    block28: {
                        block26: {
                            npcDefinition8 = NpcDefinition.forId(n);
                            if ((n < 1290 || n > 1293) && npcDefinition8.hitpoints > 0) {
                                if (npcDefinition8.legacyAttackBonus != 0) {
                                    npcDefinition8.legacyAttackBonus = npcDefinition8.hitpoints;
                                }
                                if (npcDefinition8.legacyMeleeDefenceBonus != 0) {
                                    npcDefinition8.legacyMeleeDefenceBonus = npcDefinition8.hitpoints;
                                }
                                if (npcDefinition8.legacyMagicDefenceBonus != 0) {
                                    npcDefinition8.legacyMagicDefenceBonus = npcDefinition8.hitpoints;
                                }
                                if (npcDefinition8.legacyRangedDefenceBonus != 0) {
                                    npcDefinition8.legacyRangedDefenceBonus = npcDefinition8.hitpoints;
                                }
                            }
                            npcDefinition = npcDefinition8;
                            boolean bl = NpcCombatDefinition.isRegistered(npcDefinition.id);
                            npcDefinition = npcDefinition8;
                            npcCombatDefinition = NpcCombatDefinition.forNpcId(npcDefinition.id);
                            if (!bl) break block26;
                            if (!npcCombatDefinition.hasAttackBonuses() && !npcCombatDefinition.hasDefenceBonuses()) {
                                npcCombatDefinition = npcCombatDefinition.addAttackBonuses(npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus).addDefenceBonuses(npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMagicDefenceBonus, npcDefinition8.legacyRangedDefenceBonus);
                            } else if (!npcCombatDefinition.hasAttackBonuses()) {
                                npcCombatDefinition = npcCombatDefinition.addAttackBonuses(npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus);
                            } else if (!npcCombatDefinition.hasDefenceBonuses()) {
                                npcCombatDefinition = npcCombatDefinition.addDefenceBonuses(npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMagicDefenceBonus, npcDefinition8.legacyRangedDefenceBonus);
                            }
                            break block27;
                        }
                        npcCombatDefinition = new NpcDefinitionMeleeCombatDefinition(npcDefinition8).setRespawnDelaySeconds(npcDefinition8.respawnDelaySeconds).addAttackBonuses(npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus, npcDefinition8.legacyAttackBonus).addDefenceBonuses(npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMeleeDefenceBonus, npcDefinition8.legacyMagicDefenceBonus, npcDefinition8.legacyRangedDefenceBonus);
                        npcDefinition = npcDefinition8;
                        if (npcDefinition.id == 919) break block28;
                        npcDefinition = npcDefinition8;
                        if (npcDefinition.id == 920) break block28;
                        npcDefinition = npcDefinition8;
                        if (npcDefinition.id == 385) break block28;
                        npcDefinition = npcDefinition8;
                        if (npcDefinition.id == 386) break block28;
                        npcDefinition = npcDefinition8;
                        if (npcDefinition.id == 387) break block28;
                        npcDefinition = npcDefinition8;
                        if (npcDefinition.id != 759) break block29;
                    }
                    npcCombatDefinition.setRespawnDelayTicks(150);
                }
                npcDefinition = npcDefinition8;
                if (npcDefinition.name.toLowerCase().equals("fishing spot")) {
                    npcCombatDefinition.setRespawnDelayTicks(50);
                }
            }
            int[] nArray = new int[1];
            npcDefinition = npcDefinition8;
            nArray[0] = npcDefinition.id;
            NpcCombatDefinition.register(nArray, npcCombatDefinition);
            ++n;
        }
    }

    public static boolean isDefined(int n) {
        return n < cacheDefinitionCount;
    }

    public static NpcDefinition forId(int n) {
        NpcDefinition npcDefinition = World.getNpcDefinitions()[n];
        if (npcDefinition == null) {
            npcDefinition = NpcDefinition.createFallback(n);
        }
        return npcDefinition;
    }

    public final boolean isProtectedFromMelee() {
        return this.protectedFromMelee;
    }

    public final boolean isProtectedFromRanged() {
        return this.protectedFromRanged;
    }

    public final boolean isProtectedFromMagic() {
        return this.protectedFromMagic;
    }

    public final int getId() {
        return this.id;
    }

    public final int getHitSoundId() {
        if (this.hitSoundId == 0) {
            return -1;
        }
        return this.hitSoundId;
    }

    public final int getAttackSoundId() {
        if (this.attackSoundId == 0) {
            return -1;
        }
        return this.attackSoundId;
    }

    public final int getDropTableNpcIdOverride() {
        return this.dropTableNpcIdOverride;
    }

    public final void setDropTableNpcIdOverride(int n) {
        this.dropTableNpcIdOverride = n;
    }

    public final int getDeathSoundId() {
        if (this.deathSoundId == 0) {
            return -1;
        }
        return this.deathSoundId;
    }

    public final int getPoisonDamage() {
        return this.poisonDamage;
    }

    public final double getPoisonChance() {
        return this.poisonChance;
    }

    public final int getShopId() {
        return this.shopId;
    }

    public final String getName() {
        return this.name;
    }

    public final int getDeathAnimationId() {
        return this.deathAnimationId;
    }

    public final int getBlockAnimationId() {
        return this.blockAnimationId;
    }

    public final int getAttackAnimationId() {
        return this.attackAnimationId;
    }

    public final int getCombatLevel() {
        return this.combatLevel;
    }

    public final int getSize() {
        return this.size;
    }

    public final int getAggressionType() {
        return this.aggressionType;
    }

    public final int getHitpoints() {
        return this.hitpoints;
    }

    public final int getMaxHit() {
        return this.maxHit;
    }

    public final int getDefenceLevel() {
        return this.defenceLevel;
    }

    public final int getMagicLevel() {
        return this.magicLevel;
    }

    public final int getRangedLevel() {
        return this.rangedLevel;
    }

    public final int getAttackLevel() {
        return this.attackLevel;
    }

    public final int getStrengthLevel() {
        return this.strengthLevel;
    }

    public final int getRespawnDelayTicks() {
        return this.respawnDelayTicks;
    }

    public final int getDefenceBonus(int n) {
        if (this.defenceBonuses == null) {
            this.defenceBonuses = new int[5];
        }
        if (n >= 5) {
            n -= 5;
        }
        return this.defenceBonuses[n];
    }

    public final int getMeleeAttackBonus() {
        return this.meleeAttackBonus;
    }

    public final int getMeleeStrengthBonus() {
        return this.meleeStrengthBonus;
    }

    public final int getMagicAttackBonus() {
        return this.magicAttackBonus;
    }

    public final int getRangedAttackBonus() {
        return this.rangedAttackBonus;
    }

    public final int getAggressionRange() {
        return this.aggressionRange;
    }

    public final int getSpawnRadius() {
        return this.spawnRadius;
    }

    public final int getChaseRadius() {
        return this.chaseRadius;
    }

    public static NpcDefinition createFallback(int n) {
        NpcDefinition npcDefinition = new NpcDefinition();
        new NpcDefinition().id = n;
        npcDefinition.name = "NPC #" + npcDefinition.id;
        return npcDefinition;
    }

    public final boolean isAttackable() {
        return this.attackable;
    }

    public static /* synthetic */ int getAttackBonusTypeId(NpcDefinition npcDefinition) {
        return npcDefinition.attackBonusTypeId;
    }

    public static /* synthetic */ int getDefaultMaxHit(NpcDefinition npcDefinition) {
        return npcDefinition.maxHit;
    }

    public static /* synthetic */ int getDefaultAttackDelay(NpcDefinition npcDefinition) {
        return npcDefinition.attackDelay;
    }

    public static /* synthetic */ int getDefaultAttackAnimationId(NpcDefinition npcDefinition) {
        return npcDefinition.attackAnimationId;
    }
}

