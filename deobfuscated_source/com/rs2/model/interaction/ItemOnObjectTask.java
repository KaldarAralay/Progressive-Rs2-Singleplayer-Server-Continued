/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.gameplay.godwars.GodWarsDungeonManager;
import com.rs2.model.interaction.InteractionDispatcher;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.objects.functions.FlourMillHandler;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.crafting.CraftingHandler;
import com.rs2.model.skill.crafting.JewelleryCraftingHandler;
import com.rs2.model.skill.runecrafting.RunecraftingObjectHandler;
import com.rs2.model.skill.smithing.DragonSquareShieldSmithing;
import com.rs2.model.skill.smithing.DragonfireShieldSmithing;
import com.rs2.model.skill.smithing.SmeltingHandler;
import com.rs2.model.skill.smithing.SmithingHandler;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameplayTrace;
import com.rs2.util.GameUtil;

public final class ItemOnObjectTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int objectId;
    private final /* synthetic */ int objectX;
    private final /* synthetic */ int objectY;
    private final /* synthetic */ int objectPlane;
    private final /* synthetic */ int itemId;

    public ItemOnObjectTask(int n, boolean bl, Player player, int n2, int n3, int n4, int n5, int n6, int n7) {
        super(1, true);
        this.player = player;
        this.actionSequence = n2;
        this.objectId = n3;
        this.objectX = n4;
        this.objectY = n5;
        this.objectPlane = n6;
        this.itemId = n7;
    }

    @Override
    public final void execute() {
        boolean bl;
        Object object;
        if (this.player == null || !this.player.isCurrentActionSequence(this.actionSequence)) {
            if (GameplayTrace.enabled() && this.player != null) {
                GameplayTrace.log("item-on-object task stopped stale-sequence player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " objectId=" + this.objectId + " seq=" + this.actionSequence);
            }
            this.stop();
            return;
        }
        if (this.player.isMoving() || this.player.isStunned()) {
            return;
        }
        WorldObject worldObject = SkillActionHelper.findWorldObjectById(this.objectId, this.objectX, this.objectY, this.objectPlane);
        if (worldObject == null) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("item-on-object task missing-object player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " objectId=" + this.objectId + " x=" + this.objectX + " y=" + this.objectY + " plane=" + this.objectPlane);
            }
            return;
        }
        Object object2 = ObjectDefinition.forId(this.player.getInteractionTargetId());
        Object object3 = GameUtil.findReachableInteractionPosition(worldObject.getPosition().getX(), worldObject.getPosition().getY(), this.player.getPosition().getX(), this.player.getPosition().getY(), ((ObjectDefinition)object2).getWidthForOrientation(worldObject.getOrientation()), ((ObjectDefinition)object2).getLengthForOrientation(worldObject.getOrientation()), this.objectPlane);
        if (this.objectId != 2638) {
            if (object3 == null) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("item-on-object task no-reachable-position player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " objectId=" + this.objectId + " x=" + this.objectX + " y=" + this.objectY + " plane=" + this.objectPlane);
                }
                return;
            }
            if (!InteractionDispatcher.canReachObjectInteraction(this.player, (Position)object3, worldObject)) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("item-on-object task blocked-path player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " objectId=" + this.objectId + " objectX=" + this.objectX + " objectY=" + this.objectY + " reachX=" + ((Position)object3).getX() + " reachY=" + ((Position)object3).getY());
                }
                this.stop();
                return;
            }
        }
        object3 = new Position(this.player.getInteractionTargetX(), this.player.getInteractionTargetY(), this.objectPlane);
        if (object2 != null) {
            this.player.getUpdateState().setFacePosition(((Position)object3).centerForSize(((ObjectDefinition)object2).getMaxDimension()));
        }
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("item-on-object task reached player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " item=" + ItemDefinition.forId(this.itemId).getName() + " objectId=" + this.objectId + " object=" + ((ObjectDefinition)object2).name + " x=" + this.objectX + " y=" + this.objectY + " plane=" + this.objectPlane);
        }
        if (this.player.getQuestManager().handleItemOnObject(this.itemId, this.objectId)) {
            this.stop();
            return;
        }
        if (this.objectId == 2693 || this.objectId == 2995 || this.objectId == 4483 || this.objectId == 3194 || this.objectId == 12121 || this.objectId == 2213 || this.objectId == 6084 || this.objectId == 5276 || this.objectId == 11338 || this.objectId == 14367 || this.objectId == 10517 || this.objectId == 11758) {
            int n = this.player.getInventoryManager().getItemAmount(this.itemId);
            object3 = new ItemStack(this.itemId, n);
            object = ((ItemStack)object3).getDefinition();
            if (((ItemDefinition)object).hasNote()) {
                int n2 = ((ItemDefinition)object).getNotedId();
                this.player.getInventoryManager().removeItem((ItemStack)object3);
                this.player.getInventoryManager().addItem(new ItemStack(n2, n));
                this.stop();
                return;
            }
            if (((ItemDefinition)object).isNote()) {
                if (n > 1) {
                    this.player.temporaryActionValue = this.itemId;
                    object = this.player;
                    ((Player)object).packetSender.sendEnterInputPrompt(18902);
                } else if (n == 1) {
                    int n3 = ((ItemDefinition)object).getUnnotedId();
                    this.player.getInventoryManager().removeItem((ItemStack)object3);
                    this.player.getInventoryManager().addItem(new ItemStack(n3, n));
                }
                this.stop();
                return;
            }
        }
        if (ServerSettings.content2007Enabled) {
            if (GodWarsDungeonManager.handleGodswordShardOnAnvil(this.player, this.itemId, this.objectId)) {
                this.stop();
                return;
            }
            if (DragonfireShieldSmithing.handleItemOnAnvil(this.player, this.itemId, this.objectId)) {
                this.stop();
                return;
            }
        }
        if (this.objectId == 2644 && CraftingHandler.startSpinningAtWheel(this.player, this.itemId)) {
            this.stop();
            return;
        }
        if (DragonSquareShieldSmithing.handleItemOnAnvil(this.player, this.itemId, this.objectId)) {
            this.stop();
            return;
        }
        if (this.player.getCookingManager().handleItemOnCookingObject(this.itemId, this.objectId, this.objectX, this.objectY)) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("item-on-object cooking-handled player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " objectId=" + this.objectId + " x=" + this.objectX + " y=" + this.objectY + " plane=" + this.objectPlane);
            }
            this.stop();
            return;
        }
        if (this.player.getCookingManager().handleWaterSourceItem(this.itemId, this.objectId)) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("item-on-object water-source-handled player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " item=" + ItemDefinition.forId(this.itemId).getName() + " objectId=" + this.objectId + " x=" + this.objectX + " y=" + this.objectY + " plane=" + this.objectPlane);
            }
            this.player.getInventoryManager().refresh();
            this.stop();
            return;
        }
        if (this.objectId == 5947 && this.itemId == 954) {
            if (!this.player.swampCaveRopeAttached) {
                this.player.getInventoryManager().removeItem(new ItemStack(this.itemId));
                this.player.swampCaveRopeAttached = true;
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("You attach the rope.");
            } else {
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("You have already put a rope there!");
            }
            this.stop();
            return;
        }
        if (this.objectId == 5908 && this.itemId == 1939) {
            if (!this.player.lampOilStillFilled) {
                this.player.getInventoryManager().removeItem(new ItemStack(this.itemId));
                this.player.lampOilStillFilled = true;
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("You refine some swamp tar into lamp oil.");
            } else {
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("You need to take out the oil first before making new oil!");
            }
            this.stop();
            return;
        }
        if (this.objectId == 5908 && (this.itemId == 4525 || this.itemId == 4535 || this.itemId == 4546 || this.itemId == 4700)) {
            if (this.player.lampOilStillFilled) {
                this.player.getInventoryManager().removeItem(new ItemStack(this.itemId));
                this.player.lampOilStillFilled = false;
                int n = 4522;
                object3 = "lamp";
                if (this.itemId == 4535) {
                    object3 = "lantern";
                    n = 4537;
                } else if (this.itemId == 4546) {
                    object3 = "lantern";
                    n = 4548;
                } else if (this.itemId == 4700) {
                    object3 = "lantern";
                    n = 4701;
                }
                this.player.getInventoryManager().addItem(new ItemStack(n));
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("You put some oil in the " + (String)object3 + ".");
            } else {
                object = this.player;
                ((Player)object).packetSender.sendGameMessage("You need to make oil first!");
            }
            this.stop();
            return;
        }
        if (this.objectId == 1781 && this.itemId == 1931) {
            FlourMillHandler.collectFlourFromBin(this.player);
            this.stop();
            return;
        }
        if ((this.objectId == 14921 || this.objectId == 11666 || this.objectId == 9390 || this.objectId == 2781 || this.objectId == 3044) && this.itemId == 446) {
            SmeltingHandler.handleOreOnFurnace(this.player, this.itemId);
            this.stop();
            return;
        }
        if (this.objectId == 3044 && this.player.getQuestState(0) != 1 && (this.itemId == 438 || this.itemId == 436)) {
            SmeltingHandler.handleOreOnFurnace(this.player, this.itemId);
            this.stop();
            return;
        }
        if (this.objectId == 2783 && this.player.getQuestState(0) == 37 && this.itemId == 2349) {
            SmithingHandler.openSmithingInterface(this.player, 2349);
            object = this.player;
            ((Player)object).packetSender.sendEntityHintIcon(1, -1);
            this.player.ea();
            this.stop();
            return;
        }
        int n = this.objectY;
        int n4 = this.objectX;
        int n5 = this.objectId;
        int n6 = this.itemId;
        object2 = this.player;
        if (((Player)object2).getPlantPotHandler().fillPlantPotWithSoil(n6, n4, n)) {
            bl = true;
        } else if (((Player)object2).getAllotmentPatchManager().curePatch(n4, n, n6)) {
            bl = true;
        } else if (((Player)object2).getAllotmentPatchManager().compostPatch(n4, n, n6)) {
            bl = true;
        } else if (((Player)object2).getAllotmentPatchManager().clearPatch(n4, n, n6)) {
            bl = true;
        } else if (n6 >= 3422 && n6 <= 3428 && n5 == 4090) {
            ((Player)object2).getInventoryManager().removeItem(new ItemStack(n6));
            ((Player)object2).getInventoryManager().addItem(new ItemStack(n6 + 8));
            ((Entity)object2).getUpdateState().setAnimation(832);
            Object object4 = object2;
            ((Player)object4).packetSender.sendGameMessage("You put the olive oil on the fire, and turn it into sacred oil.");
            bl = true;
        } else {
            bl = n6 <= 5340 && n6 > 5332 && ((Player)object2).getAllotmentPatchManager().waterPatch(n4, n, n6) ? true : (((Player)object2).getAllotmentPatchManager().plantSeed(n4, n, n6) ? true : (((Player)object2).getFlowerPatchManager().plantScarecrow(n4, n, n6) ? true : (((Player)object2).getFlowerPatchManager().curePatch(n4, n, n6) ? true : (((Player)object2).getFlowerPatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getFlowerPatchManager().clearPatch(n4, n, n6) ? true : (n6 <= 5340 && n6 > 5332 && ((Player)object2).getFlowerPatchManager().waterPatch(n4, n, n6) ? true : (((Player)object2).getFlowerPatchManager().plantSeed(n4, n, n6) ? true : (((Player)object2).getCompostBinManager().fillBin(n6, n4, n) ? true : (((Player)object2).getHerbPatchManager().curePatch(n4, n, n6) ? true : (((Player)object2).getHerbPatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getHerbPatchManager().clearPatch(n4, n, n6) ? true : (((Player)object2).getHerbPatchManager().plantSeed(n4, n, n6) ? true : (((Player)object2).getHopsPatchManager().curePatch(n4, n, n6) ? true : (((Player)object2).getHopsPatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getHopsPatchManager().clearPatch(n4, n, n6) ? true : (n6 <= 5340 && n6 > 5332 && ((Player)object2).getHopsPatchManager().waterPatch(n4, n, n6) ? true : (((Player)object2).getHopsPatchManager().plantSeed(n4, n, n6) ? true : (((Player)object2).getBushPatchManager().curePatch(n4, n, n6) ? true : (((Player)object2).getBushPatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getBushPatchManager().clearPatch(n4, n, n6) ? true : (((Player)object2).getBushPatchManager().plantSeed(n4, n, n6) ? true : (((Player)object2).getTreePatchManager().prunePatch(n4, n, n6) ? true : (((Player)object2).getTreePatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getTreePatchManager().plantSapling(n4, n, n6) ? true : (((Player)object2).getTreePatchManager().clearPatch(n4, n, n6) ? true : (((Player)object2).getFruitTreePatchManager().prunePatch(n4, n, n6) ? true : (((Player)object2).getFruitTreePatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getFruitTreePatchManager().clearPatch(n4, n, n6) ? true : (((Player)object2).getFruitTreePatchManager().plantSapling(n4, n, n6) ? true : (((Player)object2).getSpecialTreePatchManager().curePatch(n4, n, n6) ? true : (((Player)object2).getSpecialTreePatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getSpecialTreePatchManager().clearPatch(n4, n, n6) ? true : (((Player)object2).getSpecialTreePatchManager().plantSapling(n4, n, n6) ? true : (((Player)object2).getSpecialCropPatchManager().curePatch(n4, n, n6) ? true : (((Player)object2).getSpecialCropPatchManager().compostPatch(n4, n, n6) ? true : (((Player)object2).getSpecialCropPatchManager().clearPatch(n4, n, n6) ? true : ((Player)object2).getSpecialCropPatchManager().plantSeed(n4, n, n6)))))))))))))))))))))))))))))))))))));
        }
        if (bl) {
            this.stop();
            return;
        }
        if (RunecraftingObjectHandler.handleTalismanOnMysteriousRuins(this.player, this.itemId, this.objectId)) {
            this.stop();
            return;
        }
        if (GameplayHelper.handleTiaraCrafting(this.player, this.itemId, this.objectId)) {
            this.stop();
            return;
        }
        if (GameplayHelper.handleCombinationRunecrafting(this.player, this.itemId, this.objectId)) {
            this.stop();
            return;
        }
        if (this.itemId >= 3422 && this.itemId <= 3428 && this.objectId == 4090) {
            this.player.getInventoryManager().removeItem(new ItemStack(this.itemId));
            this.player.getInventoryManager().addItem(new ItemStack(this.itemId + 8));
            this.player.getUpdateState().setAnimation(832);
            Player player = this.player;
            player.packetSender.sendGameMessage("You put the olive oil on the fire, and turn it into sacred oil.");
            this.stop();
            return;
        }
        switch (this.objectId) {
            case 2114: {
                if (this.itemId != 453) break;
                object2 = this.player;
                n6 = 120 - ((Player)object2).getCoalTruckCoalCount();
                if (n6 == 0) {
                    Object object5 = object2;
                    ((Player)object5).packetSender.sendGameMessage("The coal truck is already full.");
                    break;
                }
                int n7 = ((Player)object2).getInventoryManager().getItemAmount(453);
                if (n7 == 0) break;
                int n8 = n4 = n6 < n7 ? n6 : n7;
                if (!((Player)object2).getInventoryManager().removeItem(new ItemStack(453, n4))) break;
                ((Player)object2).setCoalTruckCoalCount(((Player)object2).getCoalTruckCoalCount() + n4);
                break;
            }
            case 172: {
                if (this.itemId != 989 || !((Player)(object2 = this.player)).getInventoryManager().removeItem(new ItemStack(989))) break;
                ((Entity)object2).getUpdateState().setAnimation(832);
                Object object6 = object2;
                ((Player)object6).packetSender.sendGameMessage("You unlock the chest with your key.");
                new DynamicObject(173, 2914, 3452, 0, 2, 10, 172, 2);
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(1631));
                String[] stringArray = new String[]{"1000/3765", "100/1067", "100/1067", "100/1067", "10/128", "10/128", "10/128", "1/16", "1/64", "1/128", "1000/7529"};
                int n9 = GameUtil.rollFractionWeightIndex(stringArray);
                if (n9 == 0) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(995, 2000));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(1969));
                    break;
                }
                if (n9 == 1) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(554, 50));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(555, 50));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(556, 50));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(557, 50));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(558, 50));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(559, 50));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(560, 10));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(561, 10));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(562, 10));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(563, 10));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(564, 10));
                    break;
                }
                if (n9 == 2) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(1617, 2));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(1619, 2));
                    break;
                }
                if (n9 == 3) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(2363, 3));
                    break;
                }
                if (n9 == 4) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(995, 750));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(GameUtil.randomInclusive(1) == 0 ? 985 : 987));
                    break;
                }
                if (n9 == 5) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(441, 150));
                    break;
                }
                if (n9 == 6) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(454, 100));
                    break;
                }
                if (n9 == 7) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(995, 1000));
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(371, 5));
                    break;
                }
                if (n9 == 8) {
                    ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(1183));
                    break;
                }
                if (n9 != 9) break;
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(((Player)object2).getGender() == 0 ? 1079 : 1093));
                break;
            }
            case 3827: {
                if (this.itemId != 954) break;
                this.player.getInventoryManager().removeItem(new ItemStack(954, 1));
                ObjectManager.getInstance().removeDynamicObjectAt(this.objectX, this.objectY, this.objectPlane, 0);
                new DynamicObject(worldObject.getObjectId() + 1, this.objectX, this.objectY, this.objectPlane, worldObject.getOrientation(), worldObject.getType(), worldObject.getObjectId(), 30);
                break;
            }
            case 3830: {
                if (this.itemId != 954) break;
                this.player.getInventoryManager().removeItem(new ItemStack(954, 1));
                ObjectManager.getInstance().removeDynamicObjectAt(this.objectX, this.objectY, this.objectPlane, 0);
                new DynamicObject(worldObject.getObjectId() + 1, this.objectX, this.objectY, this.objectPlane, worldObject.getOrientation(), worldObject.getType(), worldObject.getObjectId(), 30);
                break;
            }
            case 170: {
                if (this.itemId != 991 || !((Player)(object2 = this.player)).getInventoryManager().removeItem(new ItemStack(991))) break;
                ((Entity)object2).getUpdateState().setAnimation(832);
                Object object7 = object2;
                ((Player)object7).packetSender.sendGameMessage("You unlock the chest with your key.");
                new DynamicObject(171, 3089, 3859, 0, 1, 10, 170, 2);
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(1619, 1));
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(2359, 1));
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(1209, 1));
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(2297, 1));
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(563, 2));
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(560, 2));
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(562, 10));
                ((Player)object2).getInventoryManager().addOrDropItem(new ItemStack(995, 50));
                break;
            }
            case 733: {
                AttackStyleDefinition.slashWeb(this.player, this.objectX, this.objectY, this.itemId);
                break;
            }
            case 2782: 
            case 2783: {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("item-on-object smithing-interface-open player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " item=" + ItemDefinition.forId(this.itemId).getName() + " objectId=" + this.objectId + " x=" + this.objectX + " y=" + this.objectY + " plane=" + this.objectPlane);
                }
                SmithingHandler.openSmithingInterface(this.player, this.itemId);
                break;
            }
            case 2714: 
            case 2715: 
            case 2716: 
            case 2717: {
                FlourMillHandler.addGrainToHopper(this.player);
                break;
            }
            case 2638: {
                if (this.itemId != 1704 && this.itemId != 1706 && this.itemId != 1708 && this.itemId != 1710) break;
                Player player = this.player;
                player.packetSender.sendGameMessage("You dip your amulet into the fountain...");
                this.player.getUpdateState().setAnimation(827, 0);
                int n10 = 0;
                while (n10 < 28) {
                    int[] nArray = new int[]{1704, 1706, 1708, 1710};
                    nArray = nArray;
                    int n11 = 0;
                    while (n11 < 4) {
                        int n12 = nArray[n11];
                        if (this.player.getInventoryManager().getContainer().containsItem(n12)) {
                            this.player.getInventoryManager().setItemInSlot(new ItemStack(1712, 1), this.player.getInventoryManager().getContainer().indexOfItem(n12));
                        }
                        ++n11;
                    }
                    ++n10;
                }
                this.player.getDialogueManager().showThreeLineItemMessage("You feel a power emanating from the fountain as it", "recharges your amulet. You can now rub the amulet to", "teleport and wear it to get more gems whilst mining.", new ItemStack(this.itemId, 1));
                this.player.getDialogueManager().finishDialogue();
                break;
            }
            case 2645: 
            case 10814: {
                if (this.itemId != 1925) break;
                object2 = this.player;
                if (!ServerSettings.craftingEnabled) {
                    Object object8 = object2;
                    ((Player)object8).packetSender.sendGameMessage("This skill is currently disabled.");
                    break;
                }
                if (!((Player)object2).isMember()) {
                    ((Player)object2).packetSender.sendGameMessage("You need a members account to access members content.");
                    break;
                }
                if (ServerSettings.freeToPlayWorld) {
                    ((Player)object2).packetSender.sendGameMessage("You need to be in members world to access members content.");
                    break;
                }
                if (!((Player)object2).getInventoryManager().getContainer().containsItem(1925)) {
                    ((Player)object2).getDialogueManager().showOneLineStatement("You need a wooden bucket to do that.");
                    break;
                }
                ((Entity)object2).getUpdateState().setAnimation(895);
                Object object9 = object2;
                ((Player)object9).packetSender.sendGameMessage("You fill your bucket with sand.");
                ((Player)object2).getInventoryManager().removeItem(new ItemStack(1925));
                ((Player)object2).getInventoryManager().addItem(new ItemStack(1783));
                break;
            }
            case 2781: 
            case 2785: 
            case 2966: 
            case 3044: 
            case 3294: 
            case 4304: 
            case 4305: 
            case 6189: 
            case 6190: 
            case 9390: 
            case 11009: 
            case 11010: 
            case 11666: 
            case 12100: 
            case 12809: 
            case 14921: {
                if (this.itemId == 1783) {
                    object2 = this.player;
                    if (!ServerSettings.craftingEnabled) {
                        Object object10 = object2;
                        ((Player)object10).packetSender.sendGameMessage("This skill is currently disabled.");
                        break;
                    }
                    if (!((Player)object2).isMember()) {
                        ((Player)object2).packetSender.sendGameMessage("You need a members account to access members content.");
                        break;
                    }
                    if (ServerSettings.freeToPlayWorld) {
                        ((Player)object2).packetSender.sendGameMessage("You need to be in members world to access members content.");
                        break;
                    }
                    if (!((Player)object2).getInventoryManager().getContainer().containsItem(1783)) {
                        ((Player)object2).getDialogueManager().showOneLineStatement("You need a bucket of sand to do that.");
                        break;
                    }
                    if (!((Player)object2).getInventoryManager().getContainer().containsItem(1781)) {
                        ((Player)object2).getDialogueManager().showOneLineStatement("You need soda ash to do that.");
                        break;
                    }
                    ((Entity)object2).getUpdateState().setAnimation(899);
                    ((Player)object2).getSkillManager().addExperience(12, 20.0);
                    Object object11 = object2;
                    ((Player)object11).packetSender.sendGameMessage("You heat the sand and soda ash in the furnace to make glass.");
                    ((Player)object2).getInventoryManager().removeItem(new ItemStack(1783));
                    ((Player)object2).getInventoryManager().removeItem(new ItemStack(1781));
                    ((Player)object2).getInventoryManager().addItem(new ItemStack(1925));
                    ((Player)object2).getInventoryManager().addItem(new ItemStack(1775));
                    break;
                }
                if (this.itemId == 2357 || this.itemId == 2365) {
                    JewelleryCraftingHandler.openJewelleryCraftingInterface(this.player, this.itemId);
                    break;
                }
                if (this.itemId == 2355) {
                    GameplayHelper.openProductionInterface(this.player, "silverCrafting");
                    break;
                }
                ItemService.getInstance();
                if (!ItemService.getItemName(this.itemId).toLowerCase().endsWith("ore")) {
                    ItemService.getInstance();
                    if (!ItemService.getItemName(this.itemId).toLowerCase().equals("coal")) break;
                }
                SmeltingHandler.handleOreOnFurnace(this.player, this.itemId);
                break;
            }
            case 2642: {
                if (this.itemId != 1761) break;
                GameplayHelper.openProductionInterface(this.player, "potteryUnfired");
                break;
            }
            case 2643: 
            case 11601: {
                if (InterfaceDefinition.interfaceCount <= 8888) {
                    CraftingHandler.startPotteryFiring(this.player, this.itemId);
                    break;
                }
                GameplayHelper.openProductionInterface(this.player, "potteryFired");
                break;
            }
            default: {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("item-on-object unhandled player=" + GameplayTrace.describe(this.player) + " itemId=" + this.itemId + " objectId=" + this.objectId + " x=" + this.objectX + " y=" + this.objectY + " plane=" + this.objectPlane);
                }
                Player player = this.player;
                player.packetSender.sendGameMessage("Nothing interesting happens.");
            }
        }
        this.stop();
    }
}

