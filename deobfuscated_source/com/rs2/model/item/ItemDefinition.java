/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item;

import com.rs2.ServerSettings;
import com.rs2.cache.CacheArchive;
import com.rs2.cache.CacheStore;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.skill.runecrafting.RunecraftingHandler;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ItemDefinition {
    public ArrayList grandExchangePriceSamples = new ArrayList();
    private static ItemDefinition[] definitionsById;
    private static int definitionCount;
    private static boolean[] e;
    private static boolean[] f;
    private int[] g;
    private int[] h;
    private boolean destroyOption;
    private int id;
    private String name;
    private String description;
    private boolean note;
    private boolean hasNote;
    private boolean stackable;
    private int unnotedId;
    private int notedId;
    private boolean membersOnly;
    private int tokkulValue;
    private int t;
    private int highAlchemyValue;
    private int lowAlchemyValue;
    private int donatorPointValue;
    private final int[] bonuses;
    private final int[] requiredLevels;
    private final boolean[] requiredQuests;
    private int equipmentSlot;
    private int equipmentAppearanceType;
    private int requiredQuestPoints;
    public int b;
    private double weight;
    private boolean twoHanded;
    private int shopValue;
    private boolean untradeable;

    static {
        Logger.getLogger(ItemDefinition.class.getName());
        definitionsById = new ItemDefinition[11884];
        definitionCount = 0;
        e = new boolean[11884];
        f = new boolean[11884];
    }

    public static ItemDefinition forId(int n) {
        ItemDefinition itemDefinition;
        if (n < 0) {
            n = 1;
        }
        if ((itemDefinition = definitionsById[n]) == null) {
            itemDefinition = new ItemDefinition(n, "# + id", "It's an item!", "NONE", false, false, false, -1, -1, true, 0, 0, 0, 0, new int[14], 0, new int[25], 0, new boolean[QuestDefinition.questCount], 0.0, 0, 0, false);
        }
        return itemDefinition;
    }

    public static void loadDefinitions() {
        int n;
        Object object;
        ByteArrayReader byteArrayReader;
        try {
            byte[] byArray = FileUtil.readBytes("./data/content/itemDefinitions.dat");
            byteArrayReader = new ByteArrayReader(byArray);
            int n2 = byteArrayReader.readUnsignedShort();
            int n3 = 0;
            while (n3 < n2) {
                object = ItemDefinition.forId(n3);
                ItemDefinition.definitionsById[n3] = (ItemDefinition)object;
                n = byteArrayReader.readUnsignedByte();
                if (n != 200) {
                    int n4;
                    int n5;
                    if (n > 200) {
                        n5 = byteArrayReader.readShort();
                        ItemDefinition itemDefinition = ItemDefinition.forId(n5);
                        ((ItemDefinition)object).b = n5;
                        ((ItemDefinition)object).equipmentSlot = itemDefinition.equipmentSlot;
                        ((ItemDefinition)object).untradeable = itemDefinition.untradeable;
                        ItemDefinition.e[n3] = itemDefinition.untradeable;
                        ((ItemDefinition)object).twoHanded = itemDefinition.twoHanded;
                        ((ItemDefinition)object).equipmentAppearanceType = itemDefinition.equipmentAppearanceType;
                        n4 = 0;
                        while (n4 < 25) {
                            ((ItemDefinition)object).requiredLevels[n4] = itemDefinition.requiredLevels[n4];
                            ++n4;
                        }
                        ((ItemDefinition)object).requiredQuestPoints = itemDefinition.requiredQuestPoints;
                        n4 = 0;
                        while (n4 < 104) {
                            ((ItemDefinition)object).requiredQuests[n4] = itemDefinition.requiredQuests[n4];
                            ++n4;
                        }
                        ((ItemDefinition)object).shopValue = itemDefinition.shopValue;
                        ((ItemDefinition)object).highAlchemyValue = itemDefinition.highAlchemyValue;
                        ((ItemDefinition)object).lowAlchemyValue = itemDefinition.lowAlchemyValue;
                        ((ItemDefinition)object).tokkulValue = itemDefinition.tokkulValue;
                        ((ItemDefinition)object).t = itemDefinition.t;
                        ((ItemDefinition)object).weight = itemDefinition.weight;
                        n4 = 0;
                        while (n4 < 14) {
                            ((ItemDefinition)object).bonuses[n4] = itemDefinition.bonuses[n4];
                            ++n4;
                        }
                        if (n == 202) {
                            ((ItemDefinition)object).donatorPointValue = byteArrayReader.readShort();
                            if (n3 == 7999) {
                                if (ServerSettings.membershipRequirementMode == 4) {
                                    ((ItemDefinition)object).shopValue = ServerSettings.membershipRequirementValue;
                                } else if (ServerSettings.membershipRequirementMode == 5) {
                                    ((ItemDefinition)object).shopValue = 1000;
                                }
                            }
                        }
                    } else {
                        ((ItemDefinition)object).equipmentSlot = n - 1;
                        ItemDefinition.e[n3] = ((ItemDefinition)object).untradeable = byteArrayReader.readUnsignedByte() == 1;
                        if (n != 0) {
                            ((ItemDefinition)object).twoHanded = byteArrayReader.readUnsignedByte() == 1;
                            ((ItemDefinition)object).equipmentAppearanceType = byteArrayReader.readUnsignedByte();
                            while ((n5 = byteArrayReader.readUnsignedByte()) != 0) {
                                int n6;
                                if (n5 == 1) {
                                    n6 = byteArrayReader.readUnsignedByte();
                                    ((ItemDefinition)object).requiredLevels[n6] = n4 = byteArrayReader.readUnsignedByte();
                                }
                                if (n5 == 2) {
                                    n6 = byteArrayReader.readUnsignedByte();
                                    if (n6 == 250) {
                                        n6 = QuestDefinition.getTotalQuestPointReward();
                                    }
                                    ((ItemDefinition)object).requiredQuestPoints = n6;
                                }
                                if (n5 != 3) continue;
                                n6 = byteArrayReader.readUnsignedByte();
                                ((ItemDefinition)object).requiredQuests[n6] = true;
                            }
                            n5 = byteArrayReader.readUnsignedByte();
                            if ((n5 & 1) != 0) {
                                ((ItemDefinition)object).shopValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 2) != 0) {
                                ((ItemDefinition)object).highAlchemyValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 4) != 0) {
                                ((ItemDefinition)object).lowAlchemyValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 8) != 0) {
                                ((ItemDefinition)object).tokkulValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 0x10) != 0) {
                                ((ItemDefinition)object).t = byteArrayReader.readInt();
                            }
                            double d = byteArrayReader.readShort();
                            ((ItemDefinition)object).weight = d / 1000.0;
                            n = 0;
                            while (n < 14) {
                                double d2 = byteArrayReader.readShort();
                                if (ServerSettings.mod2hsEnabled && (n3 == 1307 || n3 == 1309 || n3 == 1311 || n3 == 1313 || n3 == 1315 || n3 == 1317 || n3 == 1319 || n3 == 7158)) {
                                    if (n >= 0 && n <= 4) {
                                        d2 *= ServerSettings.mod2hsAttackBonusRate;
                                    } else if (n == 10) {
                                        d2 *= ServerSettings.mod2hsStrengthBonusRate;
                                    }
                                }
                                ((ItemDefinition)object).bonuses[n] = (int)d2;
                                ++n;
                            }
                        } else {
                            n5 = byteArrayReader.readUnsignedByte();
                            if ((n5 & 1) != 0) {
                                ((ItemDefinition)object).shopValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 2) != 0) {
                                ((ItemDefinition)object).highAlchemyValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 4) != 0) {
                                ((ItemDefinition)object).lowAlchemyValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 8) != 0) {
                                ((ItemDefinition)object).tokkulValue = byteArrayReader.readInt();
                            }
                            if ((n5 & 0x10) != 0) {
                                ((ItemDefinition)object).t = byteArrayReader.readInt();
                            }
                            double d = byteArrayReader.readShort();
                            ((ItemDefinition)object).weight = d / 1000.0;
                        }
                    }
                }
                ++n3;
            }
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
        }
        CacheStore cacheStore = CacheStore.getInstance();
        byteArrayReader = null;
        try {
            byteArrayReader = new ByteArrayReader(new CacheArchive(cacheStore.readFile(0, 2)).getFileBytes("obj.dat"));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        definitionCount = cacheStore.getDefinitionIndex().getItemDefinitionEntries().length;
        int n7 = 0;
        while (n7 < definitionCount) {
            byteArrayReader.position = cacheStore.getDefinitionIndex().getItemDefinitionEntry(n7).getDataOffset();
            n = n7;
            object = byteArrayReader;
            ItemDefinition itemDefinition = definitionsById[n];
            definitionsById[n].stackable = false;
            itemDefinition.hasNote = false;
            itemDefinition.note = false;
            itemDefinition.membersOnly = false;
            itemDefinition.destroyOption = false;
            block11: while (true) {
                int n8;
                if ((n8 = ((ByteArrayReader)object).readUnsignedByte()) == 0) {
                    if (n != 4561) break;
                    itemDefinition.stackable = true;
                    break;
                }
                if (n8 == 1) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 2) {
                    itemDefinition.name = ((ByteArrayReader)object).readString();
                    continue;
                }
                if (n8 == 3) {
                    String string;
                    itemDefinition.description = string = new String(((ByteArrayReader)object).readLineBytes());
                    continue;
                }
                if (n8 == 4) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 5) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 6) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 7) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 8) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 10) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 11) {
                    itemDefinition.stackable = true;
                    continue;
                }
                if (n8 == 12) {
                    ((ByteArrayReader)object).readInt();
                    continue;
                }
                if (n8 == 16) {
                    itemDefinition.membersOnly = true;
                    continue;
                }
                if (n8 == 23) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    ((ByteArrayReader)object).readByte();
                    continue;
                }
                if (n8 == 24) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 25) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    ((ByteArrayReader)object).readByte();
                    continue;
                }
                if (n8 == 26) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 >= 30 && n8 < 35) {
                    ((ByteArrayReader)object).readString();
                    continue;
                }
                if (n8 >= 35 && n8 < 40) {
                    String string = ((ByteArrayReader)object).readString();
                    if (!string.toLowerCase().equals("destroy")) continue;
                    itemDefinition.destroyOption = true;
                    continue;
                }
                if (n8 == 40) {
                    n8 = ((ByteArrayReader)object).readUnsignedByte();
                    int n9 = 0;
                    while (true) {
                        if (n9 >= n8) continue block11;
                        ((ByteArrayReader)object).readUnsignedShort();
                        ((ByteArrayReader)object).readUnsignedShort();
                        ++n9;
                    }
                }
                if (n8 == 78) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 79) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 90) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 91) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 92) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 93) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 95) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 97) {
                    n8 = ((ByteArrayReader)object).readUnsignedShort();
                    itemDefinition.note = true;
                    itemDefinition.unnotedId = n8;
                    ItemDefinition itemDefinition2 = definitionsById[n8];
                    definitionsById[n8].hasNote = true;
                    itemDefinition2.notedId = n;
                    continue;
                }
                if (n8 == 98) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 >= 100 && n8 < 110) {
                    if (itemDefinition.g == null) {
                        itemDefinition.g = new int[10];
                        itemDefinition.h = new int[10];
                    }
                    itemDefinition.g[n8 - 100] = ((ByteArrayReader)object).readUnsignedShort();
                    itemDefinition.h[n8 - 100] = ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 110) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 111) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 112) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 113) {
                    ((ByteArrayReader)object).readByte();
                    continue;
                }
                if (n8 == 114) {
                    ((ByteArrayReader)object).readByte();
                    continue;
                }
                if (n8 == 115) {
                    ((ByteArrayReader)object).readUnsignedByte();
                    continue;
                }
                if (n8 == 121) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 122) {
                    ((ByteArrayReader)object).readUnsignedShort();
                    continue;
                }
                if (n8 == 140) {
                    n8 = ((ByteArrayReader)object).readUnsignedByte();
                    int n10 = 0;
                    while (true) {
                        if (n10 >= n8) continue block11;
                        ((ByteArrayReader)object).readUnsignedShort();
                        ((ByteArrayReader)object).readUnsignedShort();
                        ++n10;
                    }
                }
                if (n8 != 177) continue;
                ItemDefinition.f[n] = true;
            }
            ++n7;
        }
        if (!ItemDefinition.isDefined(7936)) {
            RunecraftingHandler.PURE_ESSENCE_ITEM_ID = 1436;
        }
    }

    public static boolean isDefined(int n) {
        return n < definitionCount;
    }

    private ItemDefinition(int n, String string, String string2, String string3, boolean bl, boolean bl2, boolean bl3, int n2, int n3, boolean bl4, int n4, int n5, int n6, int n7, int[] nArray, int n8, int[] nArray2, int n9, boolean[] blArray, double d, int n10, int n11, boolean bl5) {
        this.id = n;
        this.name = string;
        this.description = string2;
        this.note = false;
        this.hasNote = false;
        this.stackable = false;
        this.unnotedId = -1;
        this.notedId = -1;
        this.membersOnly = true;
        this.tokkulValue = 0;
        this.t = 0;
        this.highAlchemyValue = 0;
        this.lowAlchemyValue = 0;
        this.bonuses = nArray;
        String string4 = string3;
        this.equipmentSlot = string4.equals("HAT") ? 0 : (string4.equals("CAPE") ? 1 : (string4.equals("AMULET") ? 2 : (string4.equals("WEAPON") ? 3 : (string4.equals("BODY") ? 4 : (string4.equals("SHIELD") ? 5 : (string4.equals("LEGS") ? 7 : (string4.equals("GLOVES") ? 9 : (string4.equals("BOOTS") ? 10 : (string4.equals("RING") ? 12 : (string4.equals("ARROWS") ? 13 : -1))))))))));
        this.equipmentAppearanceType = 0;
        this.requiredLevels = nArray2;
        this.requiredQuestPoints = 0;
        this.requiredQuests = blArray;
        this.weight = 0.0;
        this.donatorPointValue = 0;
        this.destroyOption = false;
    }

    public final int getId() {
        return this.id;
    }

    public final String getName() {
        if (this.note && this.unnotedId != -1 && this.unnotedId != this.id) {
            this.name = ItemDefinition.forId(this.unnotedId).getName();
        }
        return this.name;
    }

    public final String getShortName() {
        if (this.id == 249) {
            return "guam";
        }
        if (this.id == 377) {
            return "raw lob";
        }
        if (this.id == 379) {
            return "lob";
        }
        if (this.id == 381) {
            return "rare black lobster";
        }
        if (this.id == 536) {
            return "d bone";
        }
        if (this.id == 556) {
            return "air";
        }
        if (this.id == 558) {
            return "mind";
        }
        if (this.id == 560) {
            return "death";
        }
        if (this.id == 561) {
            return "nat";
        }
        if (this.id == 563) {
            return "law";
        }
        if (this.id == 564) {
            return "cosmic";
        }
        if (this.id == 565) {
            return "blood";
        }
        if (this.id == 1305) {
            return "dlong";
        }
        if (this.id == 1319) {
            return "r2h";
        }
        if (this.id == 1333) {
            return "rune scim";
        }
        if (this.id == 1373) {
            return "rune baxe";
        }
        if (this.id == 1377) {
            return "dbaxe";
        }
        if (this.id == 1436) {
            return "rune ess";
        }
        if (this.id == 1725) {
            return "str ammy";
        }
        if (this.id == 1727) {
            return "mage ammy";
        }
        if (this.id == 1729) {
            return "def ammy";
        }
        if (this.id == 1731) {
            return "power ammy";
        }
        if (this.id == 4099) {
            return "dark mystic hat";
        }
        if (this.id == 4101) {
            return "dark mystic top";
        }
        if (this.id == 4103) {
            return "dark mystic bottom";
        }
        if (this.id == 4105) {
            return "dark mystic gloves";
        }
        if (this.id == 4107) {
            return "dark mystic boots";
        }
        if (this.id == 4109) {
            return "light mystic hat";
        }
        if (this.id == 4111) {
            return "light mystic top";
        }
        if (this.id == 4113) {
            return "light mystic bottom";
        }
        if (this.id == 4115) {
            return "light mystic gloves";
        }
        if (this.id == 4117) {
            return "light mystic boots";
        }
        if (this.id == 4151) {
            return "whip";
        }
        if (this.id == 4153) {
            return "gmaul";
        }
        if (this.id == 6522) {
            return "obby ring";
        }
        if (this.id == 6523) {
            return "obby sword";
        }
        if (this.id == 6524) {
            return "obby shield";
        }
        if (this.id == 6525) {
            return "obby knife";
        }
        if (this.id == 6526) {
            return "obby staff";
        }
        if (this.id == 6527) {
            return "obby mace";
        }
        if (this.id == 6528) {
            return "obby maul";
        }
        if (this.id == 6568) {
            return "obby cape";
        }
        if (this.id == 7936) {
            return "pure ess";
        }
        return null;
    }

    public final String getDisplayName() {
        String string = this.getShortName();
        if (string == null) {
            return this.getName();
        }
        return string;
    }

    public final String getDescription() {
        if (this.note && this.unnotedId != -1 && this.unnotedId != this.id) {
            this.description = "Swap this note at any bank for the equivalent item.";
        }
        return this.description;
    }

    public final boolean g() {
        return this.id == 759 || this.id == 763 || this.id == 765 || this.id == 769 || this.id == 1586 || this.id == 1577 || this.id == 7999;
    }

    public final boolean isNote() {
        return this.note;
    }

    public final boolean hasNote() {
        return this.hasNote;
    }

    public final boolean isStackable() {
        return this.stackable || this.note;
    }

    public final int getUnnotedId() {
        return this.unnotedId;
    }

    public final int getNotedId() {
        return this.notedId;
    }

    public final boolean isMembersOnly() {
        return this.membersOnly;
    }

    public final int getValue() {
        return this.getShopValue();
    }

    public final int getTokkulValue() {
        int n = this.tokkulValue;
        ItemDefinition itemDefinition = this;
        if (itemDefinition.note && this.unnotedId != -1 && this.unnotedId != this.id && n < ItemDefinition.forId(this.unnotedId).getTokkulValue()) {
            n = ItemDefinition.forId(this.unnotedId).getTokkulValue();
        }
        if (n == 0) {
            return 1;
        }
        return n;
    }

    public final int getShopValue() {
        if (this.note && this.unnotedId != -1 && this.unnotedId != this.id && this.shopValue < ItemDefinition.forId(this.unnotedId).getShopValue()) {
            this.shopValue = ItemDefinition.forId(this.unnotedId).getShopValue();
        }
        return this.shopValue;
    }

    public final int getDonatorPointValue() {
        if (this.note && this.unnotedId != -1 && this.unnotedId != this.id && this.donatorPointValue < ItemDefinition.forId(this.unnotedId).getDonatorPointValue()) {
            this.donatorPointValue = ItemDefinition.forId(this.unnotedId).getDonatorPointValue();
        }
        return this.donatorPointValue;
    }

    public final int getLowAlchemyValue() {
        int n = this.lowAlchemyValue;
        if (n == 0) {
            if (this.note && this.unnotedId != -1 && this.unnotedId != this.id) {
                n = ItemDefinition.forId(this.unnotedId).getLowAlchemyValue();
            }
            if (n == 0) {
                n = (this.getShopValue() << 1) / 5;
            }
        }
        return n;
    }

    public final int getHighAlchemyValue() {
        int n = this.highAlchemyValue;
        if (n == 0) {
            if (this.note && this.unnotedId != -1 && this.unnotedId != this.id) {
                n = ItemDefinition.forId(this.unnotedId).getHighAlchemyValue();
            }
            if (n == 0) {
                n = this.getShopValue() * 3 / 5;
            }
        }
        return n;
    }

    public final int[] getBonuses() {
        return this.bonuses;
    }

    public final int getBonus(int n) {
        return this.bonuses[n];
    }

    public final int getRequiredLevel(int n) {
        return this.requiredLevels[n];
    }

    public final boolean requiresQuest(int n) {
        return this.requiredQuests[n];
    }

    public final double getWeight() {
        return this.weight;
    }

    public final int getEquipmentSlot() {
        return this.equipmentSlot;
    }

    public final int getEquipmentAppearanceType() {
        return this.equipmentAppearanceType;
    }

    public final int getRequiredQuestPoints() {
        return this.requiredQuestPoints;
    }

    public final boolean isTwoHanded() {
        return this.twoHanded;
    }

    public final boolean isUntradeable() {
        boolean bl = this.untradeable;
        if (this.note && this.unnotedId != -1 && this.unnotedId != this.id) {
            bl = ItemDefinition.forId(this.unnotedId).isUntradeable();
        }
        return bl;
    }

    public final boolean hasDestroyOption() {
        boolean bl = this.destroyOption;
        if (this.note && this.unnotedId != -1 && this.unnotedId != this.id) {
            bl = ItemDefinition.forId(this.unnotedId).hasDestroyOption();
        }
        return bl;
    }

    public static int findIdByName(String name) {
        String normalizedName = name.toLowerCase();
        ItemDefinition[] itemDefinitionArray = definitionsById;
        int n = definitionsById.length;
        int n2 = 0;
        while (n2 < n) {
            ItemDefinition itemDefinition = itemDefinitionArray[n2];
            if (itemDefinition != null && itemDefinition.getName() != null && itemDefinition.getName().toLowerCase().equalsIgnoreCase(normalizedName)) {
                return itemDefinition.id;
            }
            ++n2;
        }
        return 0;
    }

}

