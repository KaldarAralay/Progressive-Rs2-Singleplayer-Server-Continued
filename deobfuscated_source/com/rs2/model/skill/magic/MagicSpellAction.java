/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.ServerSettings;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.Entity;
import com.rs2.model.EntityUpdateState;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.requirement.CombatCostRequirement;
import com.rs2.model.combat.requirement.CombatRequirement;
import com.rs2.model.gameplay.magetrainingarena.AlchemistPlaygroundController;
import com.rs2.model.gameplay.magetrainingarena.EnchantmentChamberController;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.DelayedSpellImpactTask;
import com.rs2.model.skill.magic.ItemSpellAction;
import com.rs2.model.skill.magic.MagicLevelRequirement;
import com.rs2.model.skill.magic.MagicMembersAccountRequirement;
import com.rs2.model.skill.magic.NecromancyReanimateTask;
import com.rs2.model.skill.magic.ObjectSpellAction;
import com.rs2.model.skill.magic.RuneRequirement;
import com.rs2.model.skill.magic.SelfCastSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.skill.magic.Spellbook;
import com.rs2.model.skill.magic.TelekineticGrabSpellAction;
import com.rs2.model.skill.magic.TelekineticGrabTask;
import com.rs2.model.skill.magic.TeleotherDestination;
import com.rs2.model.skill.magic.TeleotherSpellAction;
import com.rs2.model.skill.woodcutting.WoodcuttingHandler;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.GameUtil;

public abstract class MagicSpellAction
extends CycleEvent {
    private CombatRequirement[] requirements;
    private Player player;
    private SpellDefinition spell;
    private final int d;
    private static final int[][] superheatItemTable = new int[][]{{436, 1, 438, 1, 2349, 6, 1}, {438, 1, 436, 1, 2349, 6, 1}, {440, 1, 453, 2, 2353, 18, 30}, {440, 1, -1, -1, 2351, 13, 15}, {442, 1, -1, -1, 2355, 14, 20}, {444, 1, -1, -1, 2357, 23, 40}, {447, 1, 453, 4, 2359, 30, 50}, {449, 1, 453, 6, 2361, 38, 70}, {451, 1, 453, 8, 2363, 50, 85}};
    private static final int[][] enchantJewelryTable;
    private static final int[][] necromancyReanimationTable;
    private static int h;
    private static int i;
    private static int j;
    private static int k;
    private static /* synthetic */ int[] l;

    static {
        int[][] nArrayArray = new int[6][];
        int[] nArray = new int[11];
        nArray[0] = 1637;
        nArray[1] = 1694;
        nArray[2] = 1656;
        nArray[3] = 2550;
        nArray[4] = 1727;
        nArray[5] = 3853;
        nArray[6] = 555;
        nArray[7] = 1;
        nArray[10] = 7;
        nArrayArray[0] = nArray;
        int[] nArray2 = new int[11];
        nArray2[0] = 1639;
        nArray2[1] = 1696;
        nArray2[2] = 1658;
        nArray2[3] = 2552;
        nArray2[4] = 1729;
        nArray2[5] = 5521;
        nArray2[6] = 556;
        nArray2[7] = 3;
        nArray2[10] = 27;
        nArrayArray[1] = nArray2;
        int[] nArray3 = new int[12];
        nArray3[0] = 1641;
        nArray3[1] = 1698;
        nArray3[2] = -1;
        nArray3[3] = 2568;
        nArray3[4] = 1725;
        nArray3[5] = -1;
        nArray3[6] = 554;
        nArray3[7] = 5;
        nArray3[10] = 49;
        nArray3[11] = 59;
        nArrayArray[2] = nArray3;
        int[] nArray4 = new int[12];
        nArray4[0] = 1643;
        nArray4[1] = 1700;
        nArray4[2] = -1;
        nArray4[3] = 2570;
        nArray4[4] = 1731;
        nArray4[5] = -1;
        nArray4[6] = 557;
        nArray4[7] = 10;
        nArray4[10] = 57;
        nArray4[11] = 67;
        nArrayArray[3] = nArray4;
        nArrayArray[4] = new int[]{1645, 1702, -1, 2572, 1712, -1, 557, 15, 555, 15, 68};
        nArrayArray[5] = new int[]{6575, 6581, 6577, 6583, 6585, 11128, 557, 20, 554, 20, 87};
        enchantJewelryTable = nArrayArray;
        necromancyReanimationTable = new int[][]{{8080, 3867}, {8082, 3868}, {8084, 3869}, {8086, 3870}, {8088, 3871}, {8090, 3872}, {8092, 3873}, {8094, 3874}, {8096, 3875}, {8098, 3876}, {8100, 3877}, {8102, 3878}, {8104, 3879}, {8106, 3880}, {8108, 3881}, {8110, 3882}, {8112, 3883}, {8114, 3884}, {8116, 3885}};
        h = 2150;
        i = 2151;
        j = 2152;
        k = 2153;
    }

    private MagicSpellAction(Player player, SpellDefinition spellDefinition) {
        this.player = player;
        this.spell = spellDefinition;
        this.requirements = new CombatRequirement[2];
        this.d = player.nextActionSequence();
        int n = 0;
        if (spellDefinition.isMembersOnly()) {
            this.requirements = new CombatRequirement[3];
            this.requirements[n++] = new MagicMembersAccountRequirement(this);
        }
        this.requirements[n++] = new RuneRequirement(this, spellDefinition);
        this.requirements[n] = new MagicLevelRequirement(this, 6, spellDefinition.getRequiredLevel());
        if (this.spell == SpellDefinition.HOME_TELEPORT) {
            this.requirements = null;
        }
    }

    @Override
    public final void onStop() {
        if (this.player.getActiveCycleEvent() == this) {
            this.player.setActiveCycleEvent(null);
        }
    }

    private boolean validateRequirements() {
        if (ServerSettings.freeToPlayWorld && this.spell.isMembersOnly()) {
            this.player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return false;
        }
        if (this.requirements != null) {
            CombatRequirement[] combatRequirementArray = this.requirements;
            int n = this.requirements.length;
            int n2 = 0;
            while (n2 < n) {
                CombatRequirement combatRequirement = combatRequirementArray[n2];
                if (!combatRequirement.validate(this.player)) {
                    return false;
                }
                ++n2;
            }
        }
        return true;
    }

    private void executeImmediateCast() {
        this.player.setActiveCycleEvent(this);
        this.execute(null);
    }

    public final boolean tryStartCast() {
        if (!(this.player.isCurrentActionSequence(this.d) && this.validateRequirements() && this.prepareCast())) {
            return false;
        }
        this.player.setActiveCycleEvent(this);
        this.execute(null);
        return true;
    }

    @Override
    public final void execute(CycleEventContainer container) {
        if (!(this.player.isCurrentActionSequence(this.d) && this.validateRequirements() && this.prepareCast())) {
            this.player.clearPendingCropResurrectionTarget();
            this.onStop();
            return;
        }
        if (this.requirements != null) {
            for (CombatRequirement requirement : this.requirements) {
                if (requirement instanceof CombatCostRequirement) {
                    ((CombatCostRequirement)requirement).consume(this.player);
                }
            }
        }
        if (this.spell.getAnimationId() != -1) {
            this.player.getUpdateState().setAnimation(this.spell.getAnimationId());
        }
        if (this.spell.getCastGraphic() != null) {
            this.player.getUpdateState().setGraphic(this.spell.getCastGraphic());
        }
        if (this.spell.getCastSoundId() != -1) {
            this.player.packetSender.sendSoundEffect(this.spell.getCastSoundId(), 1, 0);
        }
        this.player.getSkillManager().addExperience(6, this.spell.getExperience());
        if (this.spell.getProducedItems() != null) {
            for (ItemStack producedItem : this.spell.getProducedItems()) {
                this.player.getInventoryManager().addOrDropItem(producedItem);
            }
        }
        if (this.spell == SpellDefinition.RESURRECT_CROPS) {
            double d = this.player.getSkillManager().getCurrentLevels()[6];
            double d2 = d - 78.0;
            double d3 = 50.0 + 25.0 * (d2 / 21.0);
            boolean success = (double)GameUtil.randomInt(100) <= d3;
            if (this.player.pendingCropResurrectionPatchType.equals("allotment")) {
                this.player.getAllotmentPatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("herb")) {
                this.player.getHerbPatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("flower")) {
                this.player.getFlowerPatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("bush")) {
                this.player.getBushPatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("fruittree")) {
                this.player.getFruitTreePatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("hops")) {
                this.player.getHopsPatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("special1")) {
                this.player.getSpecialTreePatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("special2")) {
                this.player.getSpecialCropPatchManager().finishResurrection(success);
            } else if (this.player.pendingCropResurrectionPatchType.equals("tree")) {
                this.player.getTreePatchManager().finishResurrection(success);
            }
            this.player.clearPendingCropResurrectionTarget();
        }
    }

    public final void scheduleDelayedImpact(Entity target, Position position) {
        HitDefinition hitDefinition = this.spell.getHitDefinition();
        if (position != null && hitDefinition != null && (hitDefinition.getProjectile() != null || hitDefinition.getGraphic() != null)) {
            int delayTicks = hitDefinition.calculateDelay(this.player.getPosition(), position);
            ProjectileDefinition projectile = hitDefinition.getProjectile();
            if (projectile != null) {
                new WoodcuttingHandler(this.player.getPosition(), this.player.getSize(), position, 0, projectile).sendProjectileToNearbyPlayers();
            }
            World.getTaskScheduler().schedule(new DelayedSpellImpactTask(this, delayTicks, hitDefinition, null, position));
        }
    }

    public abstract boolean prepareCast();

    public abstract void applyImpact(HitDefinition var1);

    public static void castItemSpell(Player player, SpellDefinition spellDefinition, int itemId, int inventorySlot) {
        MagicSpellAction action = new ItemSpellAction(player, spellDefinition, spellDefinition, itemId, player, inventorySlot);
        action.executeImmediateCast();
    }

    public static boolean castObjectSpell(Player player, SpellDefinition spellDefinition, int objectId, int objectX, int objectY, int objectPlane) {
        player.ai = false;
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(objectId, objectX, objectY, objectPlane);
        if (loadedWorldObject == null || loadedWorldObject.getWorldObject().getObjectId() != objectId) {
            return false;
        }
        MagicSpellAction objectSpellAction = new ObjectSpellAction(player, spellDefinition, objectId, objectX, objectY, objectPlane, spellDefinition);
        player.B = objectSpellAction;
        switch (MagicSpellAction.g()[spellDefinition.ordinal()]) {
            case 105: {
                boolean started = player.getAllotmentPatchManager().startResurrection(objectX, objectY)
                    || player.getFlowerPatchManager().startResurrection(objectX, objectY)
                    || player.getHerbPatchManager().startResurrection(objectX, objectY)
                    || player.getHopsPatchManager().startResurrection(objectX, objectY)
                    || player.getBushPatchManager().startResurrection(objectX, objectY)
                    || player.getTreePatchManager().startResurrection(objectX, objectY)
                    || player.getFruitTreePatchManager().startResurrection(objectX, objectY)
                    || player.getSpecialTreePatchManager().startResurrection(objectX, objectY)
                    || player.getSpecialCropPatchManager().startResurrection(objectX, objectY);
                if (started && player.pendingCropResurrectionPatchIndex != -1) {
                    objectSpellAction.executeImmediateCast();
                    return true;
                }
                return false;
            }
            case 61: {
                if (objectId == i) {
                    String string = "orb charge";
                    player.interfaceAction = string;
                    if (ServerSettings.cacheVersion < 334) {
                        EntityUpdateState.handleOrbChargeButton(player, 2799, 0);
                    } else {
                        GameplayHelper.showOneOptionProductionInterface(player, 571, "Water orb");
                    }
                    return true;
                }
                player.packetSender.sendGameMessage("Use this spell on Obelisk of Water!");
                return false;
            }
            case 62: {
                if (objectId == h) {
                    String string = "orb charge";
                    player.interfaceAction = string;
                    if (ServerSettings.cacheVersion < 334) {
                        EntityUpdateState.handleOrbChargeButton(player, 2799, 0);
                    } else {
                        GameplayHelper.showOneOptionProductionInterface(player, 575, "Earth orb");
                    }
                    return true;
                }
                player.packetSender.sendGameMessage("Use this spell on Obelisk of Earth!");
                return false;
            }
            case 63: {
                if (objectId == k) {
                    String string = "orb charge";
                    player.interfaceAction = string;
                    if (ServerSettings.cacheVersion < 334) {
                        EntityUpdateState.handleOrbChargeButton(player, 2799, 0);
                    } else {
                        GameplayHelper.showOneOptionProductionInterface(player, 569, "Fire orb");
                    }
                    return true;
                }
                player.packetSender.sendGameMessage("Use this spell on Obelisk of Fire!");
                return false;
            }
            case 64: {
                if (objectId == j) {
                    String string = "orb charge";
                    player.interfaceAction = string;
                    if (ServerSettings.cacheVersion < 334) {
                        EntityUpdateState.handleOrbChargeButton(player, 2799, 0);
                    } else {
                        GameplayHelper.showOneOptionProductionInterface(player, 573, "Air orb");
                    }
                    return true;
                }
                player.packetSender.sendGameMessage("Use this spell on Obelisk of Air!");
                return false;
            }
        }
        objectSpellAction.executeImmediateCast();
        return player.ai;
    }

    public static void castTeleotherSpell(Player player, Player target, SpellDefinition spellDefinition) {
        if (target == null) {
            return;
        }
        player.getUpdateState().setFacePosition(target.getPosition());
        player.getMovementQueue().clear();
        MagicSpellAction action = new TeleotherSpellAction(player, spellDefinition, spellDefinition, target, player);
        action.executeImmediateCast();
    }

    public static void castSelfSpell(Player player, SpellDefinition spellDefinition) {
        MagicSpellAction action = new SelfCastSpellAction(player, spellDefinition, spellDefinition, player);
        action.executeImmediateCast();
    }

    public static void scheduleTelekineticGrab(Player player, SpellDefinition spellDefinition, int n, Position position) {
        int n2 = player.nextActionSequence();
        CycleEventHandler.getInstance().schedule(player, new TelekineticGrabTask(player, n2, n, position, spellDefinition), 1);
    }

    public static void continueBotGroundItemLoot(Player player, GroundItem groundItem, boolean bl) {
        if (player.botCombatState != null && player.botCombatState.equals("loot items")) {
            player.botLootPickupTargets.remove(0);
            if (player.currentBotTask != null && player.currentBotTask.lootSellShopIds.size() > 0 && bl && player.botLootSellGroundItems.contains(groundItem)) {
                player.botLootSellItems.add(groundItem.getItem());
                player.botLootSellGroundItems.remove(groundItem);
            }
            if (player.currentBotTask == null && player.botLootSellGroundItems.contains(groundItem) && bl) {
                player.botLootSellItems.add(groundItem.getItem());
                player.botLootSellGroundItems.remove(groundItem);
            }
            if (player.botLootPickupTargets.size() > 0) {
                BotCombatHelper.pickupBotCombatGroundItem(player, ((GroundItem)player.botLootPickupTargets.get(0)).getItem().getId(), ((GroundItem)player.botLootPickupTargets.get(0)).getPosition());
                return;
            }
            player.botCombatState = null;
            player.botLootGroundItems.clear();
            player.botLootPickupTargets.clear();
            if (player.currentBotTask != null) {
                if (player.getInventoryManager().getItemAmount(563) == 0 || player.botTaskReturnToBankRequested) {
                    player.currentBotTask.startWalkToBank(player);
                    return;
                }
                player.interactWithBotNpcTargets(player.botInteractionTargetIds);
            }
        }
    }

    public static void castTelekineticGrab(Player player, SpellDefinition spellDefinition, int n, Position position) {
        GroundItemManager.getInstance();
        GroundItem groundItem = GroundItemManager.findVisibleItem(player, n, position);
        MagicSpellAction action = new TelekineticGrabSpellAction(player, spellDefinition, groundItem, spellDefinition, player, position, n);
        action.executeImmediateCast();
    }

    public static boolean handleAutocastButton(Player player, int n) {
        if (player.getSpellbook() == Spellbook.NECROMANCY) {
            return false;
        }
        if (Spellbook.getAutocastSpellForButtonId(player, n) != null && n != 349) {
            player.setAutocastSpell(Spellbook.getAutocastSpellForButtonId(player, n));
            return true;
        }
        switch (n) {
            case 349: {
                if (player.isAutocastEnabled()) {
                    return true;
                }
                if (player.getAutocastSpell() != null) {
                    player.setAutocastEnabled(!player.isAutocastEnabled());
                } else {
                    player.packetSender.sendGameMessage("You haven't selected a spell to autocast!");
                }
                return true;
            }
            case 353: {
                ItemStack itemStack = player.getEquipmentManager().getContainer().getItemAt(3);
                if (player.getSpellbook() == Spellbook.ANCIENT) {
                    if (itemStack.getId() == 4675) {
                        player.packetSender.setSidebarInterface(0, 1689);
                    } else {
                        player.packetSender.sendGameMessage("You can't autocast ancient magic without an ancient staff!");
                    }
                } else if (itemStack.getId() == 4170) {
                    player.packetSender.setSidebarInterface(0, 12050);
                } else {
                    player.packetSender.setSidebarInterface(0, 1829);
                }
                return true;
            }
            case 2004: 
            case 6161: 
            case 12101: {
                player.packetSender.setSidebarInterface(0, 328);
                return true;
            }
        }
        return false;
    }

    public final boolean castSuperheatItem(int itemId) {
        if (!this.player.getSkillManager().tryStartActionDelay(1200)) {
            return false;
        }
        for (int[] superheatItem : MagicSpellAction.superheatItemTable) {
            if (itemId != superheatItem[0]) {
                continue;
            }
            int requiredItemId = superheatItem[2];
            int requiredAmount = superheatItem[3];
            boolean missingRequiredItem = !this.player.getInventoryManager().containsItemAmount(requiredItemId, requiredAmount);
            if (missingRequiredItem && (itemId != 440 || requiredItemId != 453) && requiredItemId > 0) {
                ItemService.getInstance();
                this.player.packetSender.sendGameMessage("You haven't got enough " + ItemService.getItemName(requiredItemId).toLowerCase() + " to cast this spell!");
                this.player.packetSender.sendSoundEffect(218, 1, 0);
                return false;
            }
            if (this.player.getSkillManager().getBaseLevel(13) < superheatItem[6]) {
                this.player.packetSender.sendGameMessage("You need a smithing level of " + superheatItem[6] + " to superheat this ore.");
                this.player.packetSender.sendSoundEffect(218, 1, 0);
                return false;
            }
            this.player.getInventoryManager().removeItem(new ItemStack(itemId, 1));
            if (requiredItemId > 0) {
                this.player.getInventoryManager().removeItem(new ItemStack(requiredItemId, requiredAmount));
            }
            this.player.getInventoryManager().addItem(new ItemStack(superheatItem[4], 1));
            if (superheatItem[4] == 2357 && this.player.getEquipmentManager().getItemIdAtSlot(9) == 776) {
                this.player.getSkillManager().addExperience(13, 56.2);
            } else {
                this.player.getSkillManager().addExperience(13, superheatItem[5]);
            }
            this.player.packetSender.selectMagicSidebarTab(6);
            return true;
        }
        this.player.packetSender.sendGameMessage("You can only cast superheat item on ores!");
        this.player.packetSender.sendSoundEffect(218, 1, 0);
        return false;
    }

    public final boolean castNecromancyReanimation(int n, int n2) {
        if (!this.player.getSkillManager().tryStartActionDelay(1200)) {
            return false;
        }
        if (n != necromancyReanimationTable[n2][0]) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You cannot use this spell with this item.");
            return false;
        }
        Object object = this.player;
        if (((Player)object).H != null) {
            object = this.player;
            if (!((Player)object).H.isDead()) {
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("You cannot do that right now!");
                return false;
            }
        }
        this.player.setActionLocked(true);
        World.getTaskScheduler().schedule(new NecromancyReanimateTask(this, 2, n2));
        return true;
    }

    public final boolean castEnchantJewelry(int n, int n2) {
        if (!this.player.getSkillManager().tryStartActionDelay(1200)) {
            return false;
        }
        if (!this.player.getEnchantmentChamberController().isInsideChamber()) {
            int n3;
            if (n == enchantJewelryTable[n2][0]) {
                n3 = 0;
            } else if (n == enchantJewelryTable[n2][1]) {
                n3 = 1;
            } else if (n == enchantJewelryTable[n2][2]) {
                n3 = 2;
            } else {
                Player player = this.player;
                player.packetSender.sendGameMessage("You cannot enchant this item with this spell.");
                return false;
            }
            if (!ServerSettings.content2007Enabled && n == 6577) {
                Player player = this.player;
                player.packetSender.sendGameMessage("This item can only be enchanted when 2007 content is enabled.");
                return false;
            }
            Object object = new ItemStack(enchantJewelryTable[n2][n3 + 3], 1);
            if (((ItemStack)object).getDefinition().isMembersOnly()) {
                if (!this.player.isMember()) {
                    this.player.packetSender.sendGameMessage("You need a members account to access members content.");
                    return false;
                }
                if (ServerSettings.freeToPlayWorld) {
                    this.player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    return false;
                }
            }
            this.player.getInventoryManager().removeItem(new ItemStack(enchantJewelryTable[n2][n3], 1));
            this.player.getInventoryManager().addItem((ItemStack)object);
            object = this.player;
            PacketSender packetSender = ((Player)object).packetSender;
            StringBuilder stringBuilder = new StringBuilder("You enchant the ");
            ItemService.getInstance();
            packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(enchantJewelryTable[n2][n3])).append(".").toString());
        } else {
            this.player.getEnchantmentChamberController();
            if (!EnchantmentChamberController.isEnchantableChamberItem(n)) {
                Player player = this.player;
                player.packetSender.sendGameMessage("You cannot enchant this item with this spell.");
                return false;
            }
            this.player.getEnchantmentChamberController().enchantChamberItem(n2, n);
        }
        Player player = this.player;
        player.packetSender.selectMagicSidebarTab(6);
        return true;
    }

    public final boolean castBonesToFruit(boolean bl) {
        if (!this.player.getSkillManager().tryStartActionDelay(1200)) {
            return false;
        }
        if (bl && !this.player.bonesToPeachesUnlocked) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You need to unlock this spell at Mage Training Arena!");
            return false;
        }
        if (!this.player.getCreatureGraveyardController().isInsideGraveyard()) {
            int player = bl ? 6883 : 1963;
            boolean bl2 = false;
            ItemStack[] itemStackArray = this.player.getInventoryManager().getContainer().getItems();
            int n = itemStackArray.length;
            int n2 = 0;
            while (n2 < n) {
                ItemStack itemStack = itemStackArray[n2];
                if (itemStack != null && (itemStack.getId() == 526 || itemStack.getId() == 528 || itemStack.getId() == 530 || itemStack.getId() == 532 || itemStack.getId() == 2859 || itemStack.getId() == 3179)) {
                    this.player.getInventoryManager().removeItem(itemStack);
                    this.player.getInventoryManager().addItem(new ItemStack(player));
                    bl2 = true;
                }
                ++n2;
            }
            if (!bl2) {
                Player player2 = this.player;
                player2.packetSender.sendGameMessage("You don't have any bones to convert into fruits.");
                return false;
            }
        } else if (!this.player.getCreatureGraveyardController().convertBonesToFruit(bl)) {
            return false;
        }
        return true;
    }

    public static boolean sendTeleotherRequest(Player player, Player player2, TeleotherDestination teleotherDestination) {
        if (!player.getSkillManager().tryStartActionDelay(1200)) {
            return false;
        }
        if (player.gameMode != 0) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("You are not playing on normal gamemode and cannot trade.");
            return false;
        }
        if (player2.gameMode != 0) {
            Player player4 = player;
            player4.packetSender.sendGameMessage(String.valueOf(player2.getUsername()) + " is not playing on normal gamemode and cannot trade.");
            return false;
        }
        if (player2.getOpenInterfaceId() > 0) {
            Player player5 = player;
            player5.packetSender.sendGameMessage("This player is busy.");
            return false;
        }
        if (!player2.isAcceptAidEnabled()) {
            Player player6 = player;
            player6.packetSender.sendGameMessage("This player is not accepting aid.");
            return false;
        }
        if (player2.isInTeleportRestrictedArea()) {
            Player player7 = player;
            player7.packetSender.sendGameMessage("You cannot use this spell here.");
            return false;
        }
        if (player2.isInWilderness() && player2.getWildernessLevel() > 20) {
            Player player8 = player;
            player8.packetSender.sendGameMessage("You cannot use this spell here.");
            return false;
        }
        player2.setTeleotherDestination(teleotherDestination.position);
        player2.packetSender.sendInterfaceText(player.getUsername(), 12558);
        player2.packetSender.sendInterfaceText(teleotherDestination.displayName, 12560);
        player2.packetSender.showInterface(12468);
        return true;
    }

    public static boolean castAlchemyItem(Player player, int n, int n2, int n3, int n4, SpellDefinition spellDefinition) {
        block15: {
            block14: {
                block13: {
                    if (!player.getSkillManager().tryStartActionDelay(n4)) {
                        return false;
                    }
                    if (player.getAlchemistPlaygroundController().isInsidePlayground()) {
                        player.getAlchemistPlaygroundController();
                        if (!AlchemistPlaygroundController.isAlchemistItem(n)) {
                            Player player2 = player;
                            player2.packetSender.sendGameMessage("You can't alch this item here.");
                            return false;
                        }
                    }
                    if (!new ItemStack(n).getDefinition().isUntradeable()) break block13;
                    player.getAlchemistPlaygroundController();
                    if (!AlchemistPlaygroundController.isAlchemistItem(n)) break block14;
                }
                if (n != 995) break block15;
            }
            Player player3 = player;
            player3.packetSender.sendGameMessage("You cannot alch this item.");
            return false;
        }
        if (player.getInventoryManager().getContainer().getItemAt(n2).getAmount() > 1 && !player.getInventoryManager().getContainer().canAdd(new ItemStack(995))) {
            Player player4 = player;
            player4.packetSender.sendGameMessage("Not enough space in your inventory.");
            return false;
        }
        ItemStack[] itemStackArray = spellDefinition.getRuneCosts();
        int n5 = itemStackArray.length;
        n4 = 0;
        while (n4 < n5) {
            int n6;
            Object object = itemStackArray[n4];
            if (((ItemStack)object).getId() == n && (n6 = player.getInventoryManager().getItemAmount(n)) - 1 < ((ItemStack)object).getAmount()) {
                object = player;
                ((Player)object).packetSender.sendGameMessage("You don't have enough runes to do this.");
                return false;
            }
            ++n4;
        }
        Player player5 = player;
        player5.packetSender.lockPlayerForTicks(2);
        player5 = player;
        player5.packetSender.selectMagicSidebarTab(6);
        if (!player.getAlchemistPlaygroundController().isInsidePlayground()) {
            player.getInventoryManager().removeItem(new ItemStack(n, 1));
            if (n3 > 0) {
                player.getInventoryManager().addItem(new ItemStack(995, n3));
            }
        } else {
            player.getAlchemistPlaygroundController().alchemizeItem(n);
        }
        return true;
    }

    static /* synthetic */ void finishDelayedImpact(MagicSpellAction magicSpellAction, TickTask tickTask, HitDefinition hitDefinition) {
        tickTask.stop();
        magicSpellAction.applyImpact(hitDefinition);
    }

    static /* synthetic */ Player getPlayer(MagicSpellAction magicSpellAction) {
        return magicSpellAction.player;
    }

    /* synthetic */ MagicSpellAction(Player player, SpellDefinition spellDefinition, byte by) {
        this(player, spellDefinition);
    }

    private static /* synthetic */ int[] g() {
        if (l != null) {
            return l;
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
        l = nArray;
        return nArray;
    }

    static /* synthetic */ int[][] getNecromancyReanimationTable() {
        return necromancyReanimationTable;
    }
}
