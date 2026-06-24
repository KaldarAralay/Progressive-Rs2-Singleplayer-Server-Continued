/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;

public final class ObjectSpellAction
extends MagicSpellAction {
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectPlane;
    private final /* synthetic */ SpellDefinition objectSpell;
    private static /* synthetic */ int[] f;

    public ObjectSpellAction(Player player, SpellDefinition spellDefinition, int n, int n2, int n3, int n4, SpellDefinition spellDefinition2) {
        super(player, spellDefinition, (byte)0);
        this.objectId = n;
        this.objectX = n2;
        this.objectY = n3;
        this.objectPlane = n4;
        this.objectSpell = spellDefinition2;
    }

    @Override
    public final boolean prepareCast() {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(this.objectId, this.objectX, this.objectY, this.objectPlane);
        if (loadedWorldObject == null || loadedWorldObject.getWorldObject().getObjectId() != this.objectId) {
            return false;
        }
        ObjectSpellAction.e();
        this.objectSpell.ordinal();
        return this.objectSpell == SpellDefinition.RESURRECT_CROPS || this.objectSpell == SpellDefinition.CHARGE_WATER_ORB || this.objectSpell == SpellDefinition.CHARGE_EARTH_ORB || this.objectSpell == SpellDefinition.CHARGE_FIRE_ORB || this.objectSpell == SpellDefinition.CHARGE_AIR_ORB;
    }

    @Override
    public final void applyImpact(HitDefinition hitDefinition) {
    }

    private static /* synthetic */ int[] e() {
        if (f != null) {
            return f;
        }
        int[] nArray = new int[SpellDefinition.values().length];
        try {
            nArray[SpellDefinition.ABERRANT_SPECTER_MAGIC_ATTACK.ordinal()] = 120;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WIND_BLAST.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WIND_BOLT.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WIND_WAVE.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ANNAKARL_TELEPORT.ordinal()] = 39;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.APE_ATOLL_TELEPORT.ordinal()] = 114;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ARDOUGNE_TELEPORT.ordinal()] = 111;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ARMADYL_SPIRITUAL_MAGE_MAGIC_ATTACK.ordinal()] = 138;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BALFRUG_KREEYATH_MAGIC_ATTACK.ordinal()] = 135;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BIND.ordinal()] = 48;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BLOOD_BARRAGE.ordinal()] = 31;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BLOOD_BLITZ.ordinal()] = 27;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BLOOD_BURST.ordinal()] = 23;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BLOOD_RUSH.ordinal()] = 19;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BONES_TO_BANANAS.ordinal()] = 65;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.BONES_TO_PEACHES.ordinal()] = 67;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CAMELOT_TELEPORT.ordinal()] = 110;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CARRALLANGAR_TELEPORT.ordinal()] = 38;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHAOS_ELEMENTAL_DISARM.ordinal()] = 122;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHAOS_ELEMENTAL_RANDOM_TELEPORT.ordinal()] = 123;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHAOS_DRUID_CONFUSE.ordinal()] = 131;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHARGE.ordinal()] = 57;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHARGE_AIR_ORB.ordinal()] = 64;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHARGE_EARTH_ORB.ordinal()] = 62;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHARGE_FIRE_ORB.ordinal()] = 63;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CHARGE_WATER_ORB.ordinal()] = 61;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CLAWS_OF_GUTHIX.ordinal()] = 54;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CONFUSE.ordinal()] = 42;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CRUMBLE_UNDEAD.ordinal()] = 51;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.CURSE.ordinal()] = 44;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.DAREEYAK_TELEPORT.ordinal()] = 37;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.EARTH_BLAST.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.EARTH_BOLT.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.EARTH_STRIKE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.EARTH_WAVE.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LVL_1_ENCHANT.ordinal()] = 68;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LVL_2_ENCHANT.ordinal()] = 69;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LVL_3_ENCHANT.ordinal()] = 70;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LVL_4_ENCHANT.ordinal()] = 71;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LVL_5_ENCHANT.ordinal()] = 72;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LVL_6_ENCHANT.ordinal()] = 73;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ENFEEBLE.ordinal()] = 46;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ENTANGLE.ordinal()] = 50;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.FALADOR_TELEPORT.ordinal()] = 109;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.FIRE_BLAST.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.INFERNAL_MAGE_FIRE_BLAST.ordinal()] = 121;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.FIRE_BOLT.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.FIRE_STRIKE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.FIRE_WAVE.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.FIRE_WIZARD_FIRE_STRIKE.ordinal()] = 129;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.FLAMES_OF_ZAMORAK.ordinal()] = 55;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.GHORROCK_TELEPORT.ordinal()] = 40;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.GROWLER_MAGIC_ATTACK.ordinal()] = 132;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.HIGH_LEVEL_ALCHEMY.ordinal()] = 59;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.HOME_TELEPORT.ordinal()] = 106;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.IBAN_BLAST.ordinal()] = 41;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ICE_BARRAGE.ordinal()] = 32;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ICE_BLITZ.ordinal()] = 28;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ICE_BURST.ordinal()] = 24;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ICE_RUSH.ordinal()] = 20;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.JUNGLE_DEMON_WIND_WAVE.ordinal()] = 124;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.JUNGLE_DEMON_EARTH_WAVE.ordinal()] = 126;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.JUNGLE_DEMON_FIRE_WAVE.ordinal()] = 127;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.JUNGLE_DEMON_WATER_WAVE.ordinal()] = 125;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.KHARYRLL_TELEPORT.ordinal()] = 35;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.KREE_ARRA_MAGIC_ATTACK.ordinal()] = 136;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.KRIL_TSUTSAROTH_MAGIC_ATTACK.ordinal()] = 134;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LASSAR_TELEPORT.ordinal()] = 36;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LOW_LEVEL_ALCHEMY.ordinal()] = 58;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.LUMBRIDGE_TELEPORT.ordinal()] = 108;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.MAGIC_DART.ordinal()] = 52;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.MELZAR_CABBAGE_SPELL.ordinal()] = 119;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SUMMON_ZOMBIE.ordinal()] = 118;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aZ.ordinal()] = 104;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aY.ordinal()] = 103;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aX.ordinal()] = 102;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aS.ordinal()] = 97;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aV.ordinal()] = 100;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aR.ordinal()] = 96;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aT.ordinal()] = 98;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aU.ordinal()] = 99;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aW.ordinal()] = 101;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.PADDEWWA_TELEPORT.ordinal()] = 33;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.DAGANNOTH_PRIME_WATER_WAVE.ordinal()] = 117;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aP.ordinal()] = 94;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aC.ordinal()] = 81;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aM.ordinal()] = 91;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aF.ordinal()] = 84;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aL.ordinal()] = 90;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aO.ordinal()] = 93;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aE.ordinal()] = 83;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aQ.ordinal()] = 95;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aI.ordinal()] = 87;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aG.ordinal()] = 85;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ay.ordinal()] = 77;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aA.ordinal()] = 79;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aK.ordinal()] = 89;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.az.ordinal()] = 78;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aH.ordinal()] = 86;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aB.ordinal()] = 80;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aJ.ordinal()] = 88;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aN.ordinal()] = 92;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.aD.ordinal()] = 82;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.RESURRECT_CROPS.ordinal()] = 105;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SARADOMIN_STRIKE.ordinal()] = 53;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SENNTISTEN_TELEPORT.ordinal()] = 34;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SHADOW_BARRAGE.ordinal()] = 30;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SHADOW_BLITZ.ordinal()] = 26;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SHADOW_BURST.ordinal()] = 22;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SHADOW_RUSH.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SMOKE_BARRAGE.ordinal()] = 29;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SMOKE_BLITZ.ordinal()] = 25;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SMOKE_BURST.ordinal()] = 21;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SMOKE_RUSH.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SNARE.ordinal()] = 49;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SPINOLYP_WATER_STRIKE.ordinal()] = 115;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SERGEANT_STEELWILL_MAGIC_ATTACK.ordinal()] = 137;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.STUN.ordinal()] = 47;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.SUPERHEAT_ITEM.ordinal()] = 66;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.TELE_BLOCK.ordinal()] = 56;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.TELEKINETIC_GRAB.ordinal()] = 60;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.TELEOTHER_CAMELOT.ordinal()] = 76;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.TELEOTHER_FALADOR.ordinal()] = 75;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.TELEOTHER_LUMBRIDGE.ordinal()] = 74;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.TROLLHEIM_TELEPORT.ordinal()] = 113;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.VARROCK_TELEPORT.ordinal()] = 107;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.VULNERABILITY.ordinal()] = 45;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WALLASALKI_WATER_WAVE.ordinal()] = 116;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WATCHTOWER_TELEPORT.ordinal()] = 112;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WATER_BLAST.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WATER_BOLT.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WATER_STRIKE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WATER_WAVE.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WEAKEN.ordinal()] = 43;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WIND_STRIKE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WINGMAN_SKREE_MAGIC_ATTACK.ordinal()] = 139;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.WIZARD_FIRE_STRIKE.ordinal()] = 130;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.COMMANDER_ZILYANA_MAGIC_ATTACK.ordinal()] = 133;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            nArray[SpellDefinition.ZOOKNOCK_WATER_BLAST.ordinal()] = 128;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        f = nArray;
        return nArray;
    }
}

