/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.ServerSettings;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.GameplayHelper;
import com.rs2.model.combat.WeaponProfile;
import com.rs2.model.combat.special.SpecialAttackDefinition;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemContainerType;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.EquipmentContainer;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.skill.SkillManager;
import com.rs2.util.GameUtil;

public final class EquipmentManager {
    private Player player;
    private ItemContainer container = new EquipmentContainer(this, ItemContainerType.a, 14);
    private int[] equipSoundIds = new int[]{1342, 1343, 1344, 1346};

    public EquipmentManager(Player player) {
        this.player = player;
    }

    public final void refresh() {
        ItemStack[] rawItems = this.container.getRawItems();
        Player player = this.player;
        player.packetSender.sendItemContainer(1688, rawItems);
        ItemStack object = rawItems[3];
        if (object != null && ((ItemStack)object).getDefinition().isMembersOnly() && ServerSettings.freeToPlayWorld) {
            object = null;
        }
        this.player.setWeaponProfile(WeaponProfile.forItem((ItemStack)object));
        this.player.getEquipmentManager().refreshWeaponAmmunitionState();
        if (!ServerSettings.freeToPlayWorld) {
            this.player.getEquipmentManager().refreshBarrowsSetEffects();
        }
        this.refreshEquipmentBonuses(this.player);
        this.refreshWeaponInterface();
    }

    public final boolean removeItemWithoutRefresh(ItemStack itemStack) {
        if (itemStack == null || !itemStack.isValid()) {
            return false;
        }
        this.container.remove(itemStack);
        if (itemStack.getId() == 6583 || itemStack.getId() == 7927) {
            this.player.npcTransformationId = -1;
            Player player = this.player;
            player.packetSender.refreshSidebarInterfaces();
            this.player.setAppearanceUpdateRequired(true);
        }
        if (itemStack.getId() == 4024) {
            this.player.getUpdateState().setGraphic(160, 0);
            this.player.npcTransformationId = -1;
            this.player.setAppearanceUpdateRequired(true);
        }
        return true;
    }

    public final boolean removeItem(ItemStack itemStack) {
        if (!itemStack.isValid()) {
            return false;
        }
        EquipmentManager equipmentManager = this;
        ItemStack itemStack2 = itemStack;
        EquipmentManager equipmentManager2 = equipmentManager;
        int n = itemStack2.getAmount();
        int n2 = itemStack2.getId();
        equipmentManager2 = equipmentManager;
        if (!(equipmentManager.containsItem(n2) && equipmentManager2.container.getItemAmount(n2) >= n)) {
            return false;
        }
        this.container.remove(itemStack);
        this.refresh();
        this.player.getEquipmentManager().refreshCarriedValue();
        return true;
    }

    public final boolean containsItem(int n) {
        EquipmentManager equipmentManager = this;
        return equipmentManager.container.indexOfItem(n) >= 0;
    }

    public final void finishBulkEquipmentRemoval() {
        this.refresh();
        GameplayHelper.refreshRunecraftingTiaraConfig(this.player, -1);
        this.player.nextActionSequence();
        this.player.setWeaponProfile(null);
        this.player.setSpecialAttackDefinition(null);
        this.player.setAutocastSpell(null);
        this.refreshCarriedValue();
        GameplayHelper.refreshRubberChickenPlayerOption(this.player);
        this.player.setAppearanceUpdateRequired(true);
    }

    public final void replaceSlotItem(int n, int n2) {
        ItemStack itemStack = new ItemStack(this.player.getEquipmentManager().getItemIdAtSlot(n2));
        ItemStack itemStack2 = new ItemStack(n);
        this.container.removeFromSlot(itemStack, n2);
        this.container.setItem(n2, itemStack2);
        this.refresh();
        this.player.setSpecialAttackEnabled(false);
        this.refreshWeaponInterface();
        EntityTargetMovement.clearMovementTarget(this.player);
        this.player.getInventoryManager().refresh();
        this.player.setAppearanceUpdateRequired(true);
        this.player.getAttributes().put("usedGlory", Boolean.FALSE);
    }

    public final void setSlotItem(int n, int n2) {
        ItemStack itemStack = new ItemStack(n);
        this.container.setItem(n2, itemStack);
        this.refresh();
        this.player.setSpecialAttackEnabled(false);
        this.refreshWeaponInterface();
        EntityTargetMovement.clearMovementTarget(this.player);
        this.player.getInventoryManager().refresh();
        this.player.setAppearanceUpdateRequired(true);
    }

    public final void equipFromInventorySlot(int n) {
        int n2;
        int n3;
        ItemStack itemStack;
        block61: {
            block64: {
                block63: {
                    Object object;
                    block62: {
                        boolean bl;
                        block60: {
                            itemStack = this.player.getInventoryManager().getContainer().getItemAt(n);
                            if (itemStack == null) {
                                return;
                            }
                            n3 = itemStack.getDefinition().getEquipmentSlot();
                            if (!this.player.getInventoryManager().containsItemStack(itemStack)) {
                                return;
                            }
                            int n4 = n3;
                            n2 = itemStack.getId();
                            object = this;
                            if (new ItemStack(n2).getDefinition().isMembersOnly() && !((EquipmentManager)object).player.isMember()) {
                                ((EquipmentManager)object).player.packetSender.sendGameMessage("You need a members account to access members content.");
                                bl = false;
                            } else if (new ItemStack(n2).getDefinition().isMembersOnly() && ServerSettings.freeToPlayWorld) {
                                ((EquipmentManager)object).player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                                bl = false;
                            } else {
                                char c;
                                int n5;
                                int n6;
                                String string = "wear this item.";
                                if (n4 == 3) {
                                    string = "wield this weapon.";
                                } else if (n4 == 13) {
                                    string = "equip this ammo.";
                                }
                                n4 = 0;
                                while (n4 < 22) {
                                    n6 = ((EquipmentManager)object).player.getSkillManager().getBaseLevel(n4);
                                    if (n6 < (n5 = new ItemStack(n2).getDefinition().getRequiredLevel(n4))) {
                                        String string2 = "a";
                                        c = SkillManager.SKILL_NAMES[n4].charAt(0);
                                        if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
                                            string2 = "an";
                                        }
                                        object = ((EquipmentManager)object).player;
                                        ((Player)object).packetSender.sendGameMessage("You need " + string2 + " " + SkillManager.SKILL_NAMES[n4] + " level of " + n5 + " to " + string);
                                        bl = false;
                                        break block60;
                                    }
                                    ++n4;
                                }
                                n4 = ((EquipmentManager)object).player.getQuestPoints();
                                if (n4 < (n6 = new ItemStack(n2).getDefinition().getRequiredQuestPoints())) {
                                    object = ((EquipmentManager)object).player;
                                    ((Player)object).packetSender.sendGameMessage("You need " + n6 + " quest points to " + string);
                                    bl = false;
                                } else {
                                    n5 = 0;
                                    while (n5 < QuestDefinition.questCount) {
                                        n6 = new ItemStack(n2).getDefinition().requiresQuest(n5) ? 1 : 0;
                                        char c2 = c = ((EquipmentManager)object).player.getQuestState(n5) == 1 ? (char)'\u0001' : '\u0000';
                                        if (n6 != 0 && c == '\u0000') {
                                            Object object2 = QuestDefinition.forId(n5);
                                            object2 = ((QuestDefinition)object2).getName();
                                            object = ((EquipmentManager)object).player;
                                            ((Player)object).packetSender.sendGameMessage("You need to complete " + (String)object2 + " to " + string);
                                            bl = false;
                                            break block60;
                                        }
                                        ++n5;
                                    }
                                    bl = true;
                                }
                            }
                        }
                        if (!bl) {
                            return;
                        }
                        if (this.player.getQuestState(0) < 42 && this.player.getQuestState(0) != 1) {
                            this.player.getDialogueManager().showOneLineStatement("You haven't learned how to wield items yet!");
                            return;
                        }
                        if (this.player.getQuestState(0) != 44) break block61;
                        if (itemStack.getId() != 1171) break block62;
                        object = this.player.getEquipmentManager();
                        if (((EquipmentManager)object).container.containsItem(1277)) break block63;
                    }
                    if (itemStack.getId() != 1277) break block64;
                    object = this.player.getEquipmentManager();
                    if (!((EquipmentManager)object).container.containsItem(1171)) break block64;
                }
                this.player.ea();
            }
            this.player.getQuestManager().refreshQuestJournal();
        }
        if (itemStack.getId() == 1205 && this.player.getQuestState(0) == 42) {
            this.player.ea();
        }
        if (this.player.isInDuelArena()) {
            int[] nArray = ServerSettings.FUN_WEAPON_IDS;
            n2 = 0;
            while (n2 < 13) {
                int n7 = nArray[n2];
                if (!DuelRule.FUN_WEAPONS.isEnabledFor(this.player) && itemStack.getId() == n7) {
                    Player player = this.player;
                    player.packetSender.sendGameMessage("Usage of 'Fun weapons' haven't been enabled during this fight!");
                    return;
                }
                ++n2;
            }
        }
        boolean blockedByDuelRule = false;
        switch (n3) {
            case 0: {
                blockedByDuelRule = DuelRule.NO_HELMET.isEnabledFor(this.player);
                break;
            }
            case 1: {
                blockedByDuelRule = DuelRule.NO_CAPE.isEnabledFor(this.player);
                break;
            }
            case 2: {
                blockedByDuelRule = DuelRule.NO_AMULET.isEnabledFor(this.player);
                break;
            }
            case 13: {
                blockedByDuelRule = DuelRule.NO_AMMO.isEnabledFor(this.player);
                break;
            }
            case 3: {
                blockedByDuelRule = DuelRule.NO_WEAPON.isEnabledFor(this.player) || itemStack.getDefinition().isTwoHanded() && DuelRule.NO_SHIELD.isEnabledFor(this.player);
                break;
            }
            case 4: {
                blockedByDuelRule = DuelRule.NO_BODY.isEnabledFor(this.player);
                break;
            }
            case 5: {
                blockedByDuelRule = DuelRule.NO_SHIELD.isEnabledFor(this.player);
                break;
            }
            case 7: {
                blockedByDuelRule = DuelRule.NO_LEGS.isEnabledFor(this.player);
                break;
            }
            case 9: {
                blockedByDuelRule = DuelRule.NO_GLOVES.isEnabledFor(this.player);
                break;
            }
            case 10: {
                blockedByDuelRule = DuelRule.NO_BOOTS.isEnabledFor(this.player);
                break;
            }
            case 12: {
                blockedByDuelRule = DuelRule.NO_RING.isEnabledFor(this.player);
            }
        }
        if (blockedByDuelRule) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You cannot wear this during this fight!");
            return;
        }
        int n8;
        n2 = 0;
        if (itemStack.getDefinition().isStackable()) {
            n8 = n3;
            ItemStack itemStack2 = this.container.getItemAt(n8);
            this.player.getInventoryManager().removeItemFromSlot(itemStack, n);
            if (this.container.getItemAt(n8) != null) {
                if (itemStack.getId() == itemStack2.getId()) {
                    this.container.setItem(n8, new ItemStack(itemStack.getId(), itemStack.getAmount() + itemStack2.getAmount(), itemStack.getMetadata()));
                } else {
                    this.player.getInventoryManager().setItemInSlot(itemStack2, n);
                    this.container.setItem(n8, itemStack);
                }
            } else {
                this.container.setItem(n8, itemStack);
            }
        } else {
            n8 = n3;
            if (n8 == 3 && itemStack.getDefinition().isTwoHanded()) {
                if (this.container.getItemAt(3) != null && this.container.getItemAt(5) != null && this.player.getInventoryManager().getContainer().getFirstFreeSlot() == -1) {
                    Player player = this.player;
                    player.packetSender.sendGameMessage("Not enough space in your inventory.");
                    return;
                }
                this.player.getInventoryManager().removeItemFromSlot(itemStack, n);
                n2 = 1;
                this.unequipSlot(5);
                if (this.container.getItemAt(5) != null) {
                    return;
                }
            }
            if (n8 == 5 && this.container.getItemAt(3) != null && this.container.getItemAt(3).getDefinition().isTwoHanded()) {
                this.player.getInventoryManager().removeItemFromSlot(itemStack, n);
                n2 = 1;
                this.unequipSlot(3);
                if (this.container.getItemAt(3) != null) {
                    return;
                }
            }
            if (this.container.getItemAt(n8) != null) {
                ItemStack itemStack3 = this.container.getItemAt(n8);
                if (itemStack3.getId() == 4024) {
                    this.player.getUpdateState().setGraphic(160, 0);
                    this.player.npcTransformationId = -1;
                    this.player.setAppearanceUpdateRequired(true);
                }
                if (n2 == 0) {
                    this.player.getInventoryManager().removeItemFromSlot(itemStack, n);
                    this.player.getInventoryManager().setItemInSlot(itemStack3, n);
                } else {
                    this.player.getInventoryManager().addItem(itemStack3);
                }
            } else if (n2 == 0) {
                this.player.getInventoryManager().removeItemFromSlot(itemStack, n);
            }
            this.container.setItem(n8, new ItemStack(itemStack.getId(), itemStack.getAmount(), itemStack.getMetadata()));
        }
        this.player.nextActionSequence();
        this.player.setSpecialAttackEnabled(false);
        EntityTargetMovement.clearMovementTarget(this.player);
        this.player.getInventoryManager().refresh();
        if (n3 == 3) {
            this.player.setWeaponProfile(WeaponProfile.forItem(itemStack));
            this.player.setSpecialAttackDefinition(SpecialAttackDefinition.forItem(itemStack));
            this.player.setAutocastSpell(null);
        }
        this.refresh();
        this.refreshCarriedValue();
        this.player.getAttributes().put("usedGlory", Boolean.FALSE);
        if (itemStack.getId() == 6583 || itemStack.getId() == 7927) {
            this.player.npcTransformationId = itemStack.getId() == 6583 ? 2626 : 3689 + GameUtil.randomInclusive(5);
            this.player.setAppearanceUpdateRequired(true);
            Player player = this.player;
            player.packetSender.clearSidebarInterfaces();
            player = this.player;
            player.packetSender.setSidebarInterface(3, 6014);
        }
        if (itemStack.getId() == 4024) {
            this.player.getUpdateState().setGraphic(160, 0);
            this.player.npcTransformationId = 1463;
            this.player.setAppearanceUpdateRequired(true);
        }
        GameplayHelper.refreshRubberChickenPlayerOption(this.player);
        GameplayHelper.refreshRunecraftingTiaraConfig(this.player, itemStack.getId());
        Player player = this.player;
        player.packetSender.sendSoundEffect(this.equipSoundIds[GameUtil.randomInt(this.equipSoundIds.length)], 1, 0);
        this.player.setAppearanceUpdateRequired(true);
    }

    public final void unequipSlot(int n) {
        Object object;
        ItemStack itemStack = this.container.getItemAt(n);
        if (itemStack == null) {
            return;
        }
        if (this.player.getInventoryManager().getContainer().getFirstFreeSlot() == -1) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Not enough space in your inventory.");
            return;
        }
        if (itemStack.getId() == 6583 || itemStack.getId() == 7927) {
            this.player.npcTransformationId = -1;
            object = this.player;
            ((Player)object).packetSender.refreshSidebarInterfaces();
            this.player.setAppearanceUpdateRequired(true);
        }
        if (itemStack.getId() == 4024) {
            this.player.getUpdateState().setGraphic(160, 0);
            this.player.npcTransformationId = -1;
            this.player.setAppearanceUpdateRequired(true);
        }
        if (n == 0) {
            GameplayHelper.refreshRunecraftingTiaraConfig(this.player, -1);
        }
        object = this.player.getEquipmentManager();
        if (!((EquipmentManager)object).container.containsItem(itemStack.getId())) {
            return;
        }
        this.player.nextActionSequence();
        this.container.removeFromSlot(itemStack, n);
        this.player.getInventoryManager().addItem(new ItemStack(itemStack.getId(), itemStack.getAmount(), itemStack.getMetadata()));
        if (n == 3) {
            this.player.setWeaponProfile(null);
            this.player.setSpecialAttackDefinition(null);
            this.player.setAutocastSpell(null);
        }
        ItemStack itemStack2 = new ItemStack(-1, 0);
        int n2 = n;
        EquipmentManager equipmentManager = this;
        object = equipmentManager.player;
        ((Player)object).packetSender.sendInterfaceSlotItem(n2, 1688, itemStack2);
        equipmentManager.player.getEquipmentManager().refreshWeaponAmmunitionState();
        equipmentManager.player.getEquipmentManager().refreshBarrowsSetEffects();
        equipmentManager.refreshEquipmentBonuses(equipmentManager.player);
        equipmentManager.refreshWeaponInterface();
        this.refreshCarriedValue();
        object = this.player;
        ((Player)object).packetSender.sendSoundEffect(this.equipSoundIds[GameUtil.randomInt(this.equipSoundIds.length)], 1, 0);
        GameplayHelper.refreshRubberChickenPlayerOption(this.player);
        this.player.setAppearanceUpdateRequired(true);
    }

    public final void refreshCarriedValue() {
        Object object;
        double d = 0.0;
        int n = 0;
        while (n < 11) {
            object = this.player.getEquipmentManager();
            if (((EquipmentManager)object).container.getItemAt(n) != null) {
                object = this.player.getEquipmentManager();
                object = ((EquipmentManager)object).container.getItemAt(n);
                double d2 = ((ItemStack)object).getDefinition().getWeight();
                if (((ItemStack)object).getDefinition().isMembersOnly() && ServerSettings.freeToPlayWorld && d2 < 0.0) {
                    d2 = 0.0;
                }
                d += d2;
            }
            ++n;
        }
        n = 0;
        while (n < 28) {
            if (this.player.getInventoryManager().getContainer().getItemAt(n) != null) {
                d += this.player.getInventoryManager().getContainer().getItemAt(n).getDefinition().getWeight();
                if (this.player.getInventoryManager().getContainer().getItemAt(n).getId() == 88) {
                    d += 4.8;
                }
            }
            ++n;
        }
        this.player.carriedWeight = d;
        object = this.player;
        ((Player)object).packetSender.sendWeight();
    }

    public final void consumeSlotItemAmount(int n, int n2) {
        ItemStack itemStack = this.container.getItemAt(n);
        if (itemStack == null) {
            return;
        }
        n2 = this.container.removeFromSlot(itemStack, n) - n2;
        if (n2 > 0) {
            ItemStack itemStack2 = new ItemStack(itemStack.getId(), n2, itemStack.getMetadata());
            this.container.add(itemStack2, n);
        } else {
            this.container.add(new ItemStack(-1, 0), n);
        }
        this.refresh();
        this.player.setAppearanceUpdateRequired(true);
        this.refreshEquipmentBonuses(this.player);
    }

    private void refreshEquipmentBonuses(Player player) {
        Object object;
        int n = 0;
        int n2 = 0;
        while (n2 < 14) {
            player.setCombatBonus(n2, 0);
            ++n2;
        }
        Object object2 = player;
        n2 = 0;
        while (n2 < 14) {
            object = ((Player)object2).getEquipmentManager();
            if (!(((EquipmentManager)object).container.getItemAt(n2) == null || ((Player)object2).isCrystalBowEquipped() && n2 == 13)) {
                object = ((Player)object2).getEquipmentManager();
                object = ((EquipmentManager)object).container.getItemAt(n2);
                if (!((ItemStack)object).getDefinition().isMembersOnly() || !ServerSettings.freeToPlayWorld) {
                    int n3 = 0;
                    while (n3 < 14) {
                        int n4 = 0;
                        if (((ItemStack)object).getId() == 11283 && ((ItemStack)object).getMetadata() >= 0 && (n3 == 5 || n3 == 6 || n3 == 7 || n3 == 9)) {
                            n4 = ((ItemStack)object).getMetadata();
                        }
                        ((Player)object2).setCombatBonus(n3, ((ItemStack)object).getDefinition().getBonuses()[n3] + n4 + (Integer)((Player)object2).getCombatBonuses().get(n3));
                        ++n3;
                    }
                }
            }
            ++n2;
        }
        n2 = 0;
        while (n2 < 12) {
            object2 = (Integer)player.getCombatBonuses().get(n2) >= 0 ? String.valueOf(ServerSettings.bonusTypeNames[n2]) + ": +" + player.getCombatBonuses().get(n2) : String.valueOf(ServerSettings.bonusTypeNames[n2]) + ": -" + Math.abs((Integer)player.getCombatBonuses().get(n2));
            if (n2 == 10) {
                n = 1;
            }
            object = player;
            ((Player)object).packetSender.sendInterfaceText((String)object2, n2 + 1675 + n);
            ++n2;
        }
    }

    public final void refreshWeaponInterface() {
        Object object = this;
        ItemStack itemStack = ((EquipmentManager)object).container.getItemAt(3);
        if (itemStack != null && itemStack.getDefinition().isMembersOnly() && ServerSettings.freeToPlayWorld) {
            itemStack = null;
        }
        WeaponProfile weaponProfile = WeaponProfile.forItem(itemStack);
        if (this.player.getQuestState(0) >= 45 || this.player.getQuestState(0) == 1) {
            object = this.player;
            ((Player)object).packetSender.setSidebarInterface(0, weaponProfile.getInterfaceDefinition().getSidebarInterfaceId());
        }
        object = this.player;
        ((Player)object).packetSender.sendInterfaceText(itemStack == null ? "Unarmed" : itemStack.getDefinition().getName(), weaponProfile.getInterfaceDefinition().getWeaponNameTextId());
        WeaponProfile weaponProfile2 = weaponProfile;
        object = this;
        if (weaponProfile2.getAttackAnimations().length < 4 && ((EquipmentManager)object).player.getFightMode() == 3) {
            ((EquipmentManager)object).player.setFightMode(2);
        }
        object = this.player;
        ((Player)object).packetSender.sendConfig(43, this.player.getFightMode());
        if (weaponProfile.getInterfaceDefinition().getWeaponModelWidgetId() != -1) {
            object = this.player;
            ((Player)object).packetSender.sendInterfaceModel(weaponProfile.getInterfaceDefinition().getWeaponModelWidgetId(), 200, itemStack.getId());
        }
        if (weaponProfile.getInterfaceDefinition().getSpecialBarWidgetId() != -1) {
            if (SpecialAttackDefinition.forItem(itemStack) == null) {
                object = this.player;
                ((Player)object).packetSender.setInterfaceHiddenFlag(1, weaponProfile.getInterfaceDefinition().getSpecialBarWidgetId());
                return;
            }
            object = this.player;
            ((Player)object).packetSender.setInterfaceHiddenFlag(0, weaponProfile.getInterfaceDefinition().getSpecialBarWidgetId());
            this.player.refreshSpecialAttackWidgets();
        }
    }

    public final boolean handleAttackStyleButton(int n) {
        Object object = this;
        object = ((EquipmentManager)object).container.getItemAt(3);
        if (object != null && ((ItemStack)object).getDefinition().isMembersOnly() && ServerSettings.freeToPlayWorld) {
            object = null;
        }
        object = WeaponProfile.forItem((ItemStack)object);
        int n2 = 0;
        while (n2 < ((WeaponProfile)((Object)object)).getInterfaceDefinition().getAttackStyles().length) {
            if (n == ((WeaponProfile)((Object)object)).getInterfaceDefinition().getAttackStyles()[n2].getButtonId()) {
                this.player.ef();
                this.player.setFightMode(n2);
                return true;
            }
            ++n2;
        }
        return false;
    }

    public final int getItemIdAtSlot(int n) {
        EquipmentManager equipmentManager = this;
        ItemStack itemStack = equipmentManager.container.getItemAt(n);
        if (itemStack != null) {
            return itemStack.getId();
        }
        return 0;
    }

    public final int getStandAnimation() {
        return this.player.getWeaponProfile().getMovementAnimations()[0];
    }

    public final int getWalkAnimation() {
        return this.player.getWeaponProfile().getMovementAnimations()[1];
    }

    public final int getRunAnimation() {
        return this.player.getWeaponProfile().getMovementAnimations()[2];
    }

    public final ItemContainer getContainer() {
        return this.container;
    }

    public final boolean canEquipItem(int n) {
        int n2;
        int n3;
        int n4 = 0;
        while (n4 < 22) {
            n3 = this.player.getSkillManager().getBaseLevel(n4);
            if (n3 < (n2 = new ItemStack(n).getDefinition().getRequiredLevel(n4))) {
                return false;
            }
            ++n4;
        }
        n4 = this.player.getQuestPoints();
        if (n4 < (n3 = new ItemStack(n).getDefinition().getRequiredQuestPoints())) {
            return false;
        }
        n2 = 0;
        while (n2 < QuestDefinition.questCount) {
            n4 = new ItemStack(n).getDefinition().requiresQuest(n2) ? 1 : 0;
            int n5 = n3 = this.player.getQuestState(n2) == 1 ? 1 : 0;
            if (n4 != 0 && n3 == 0) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    public final void refreshBarrowsSetEffects() {
        this.player.setAhrimSetEffectActive(this.isEquippedSet("ahrims hood", "ahrims top", "ahrims robetop", "ahrims skirt", "ahrims robeskirt", "ahrims staff"));
        this.player.setKarilSetEffectActive(this.isEquippedSet("karils coif", "karils top", "karils leathertop", "karils skirt", "karils leatherskirt", "karils x-bow", "karils crossbow"));
        this.player.setDharokSetEffectActive(this.isEquippedSet("dharoks helm", "dharoks body", "dharoks platebody", "dharoks legs", "dharoks platelegs", "dharoks axe", "dharoks greataxe"));
        this.player.setVeracSetEffectActive(this.isEquippedSet("veracs helm", "veracs top", "veracs brassard", "veracs skirt", "veracs plateskirt", "veracs flail"));
        this.player.setToragSetEffectActive(this.isEquippedSet("torags helm", "torags body", "torags platebody", "torags legs", "torags platelegs", "torags hammer", "torags hammers"));
        this.player.setGuthanSetEffectActive(this.isEquippedSet("guthans helm", "guthans body", "guthans platebody", "guthans skirt", "guthans chainskirt", "guthans spear", "guthans warspear"));
    }

    private boolean isEquippedSet(String helmName, String bodyName, String alternateBodyName, String legsName, String alternateLegsName, String weaponName) {
        return this.hasEquippedName(0, helmName)
            && this.hasEquippedName(4, bodyName, alternateBodyName)
            && this.hasEquippedName(7, legsName, alternateLegsName)
            && this.hasEquippedName(3, weaponName);
    }

    private boolean isEquippedSet(String helmName, String bodyName, String alternateBodyName, String legsName, String alternateLegsName, String weaponName, String alternateWeaponName) {
        return this.hasEquippedName(0, helmName)
            && this.hasEquippedName(4, bodyName, alternateBodyName)
            && this.hasEquippedName(7, legsName, alternateLegsName)
            && this.hasEquippedName(3, weaponName, alternateWeaponName);
    }

    private boolean hasEquippedName(int slot, String nameFragment) {
        ItemStack itemStack = this.container.getItemAt(slot);
        return itemStack != null && itemStack.getDefinition().getName().toLowerCase().contains(nameFragment);
    }

    private boolean hasEquippedName(int slot, String nameFragment, String alternateNameFragment) {
        return this.hasEquippedName(slot, nameFragment) || this.hasEquippedName(slot, alternateNameFragment);
    }

    public final void refreshWeaponAmmunitionState() {
        String string = this.player.getWeaponProfile().name().toLowerCase();
        boolean bl = ((string = string.replaceAll("_", " ")).contains("seercull") || string.contains("bow")) && !string.contains("crossbow");
        Player player = this.player;
        boolean bl2 = string.contains("crossbow") || string.contains("x-bow");
        Player player2 = this.player;
        boolean bl3 = string.contains("knife") || string.contains("dart") || string.contains("javelin") || string.contains("thrownaxe") || string.contains("toktz-xil-ul") || string.contains("throwing axe");
        Player player3 = this.player;
        this.player.setCrystalBowEquipped(string.contains("crystal bow"));
        ItemService.getInstance();
        String string2 = ItemService.getItemName(this.player.getEquipmentManager().getItemIdAtSlot(13)).toLowerCase();
        boolean bl4 = string2.contains("arrow");
        Player player4 = this.player;
        boolean bl5 = string2.contains("bolt");
        Player player5 = this.player;
        this.player.setAmmunitionDropsEnabled(!string.contains("crystal") && !string.contains("karils"));
    }

    static /* synthetic */ Player getPlayer(EquipmentManager equipmentManager) {
        return equipmentManager.player;
    }
}
