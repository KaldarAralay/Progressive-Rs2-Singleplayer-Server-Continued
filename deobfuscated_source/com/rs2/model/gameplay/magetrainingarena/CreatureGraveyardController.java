/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.magetrainingarena;

import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.gameplay.magetrainingarena.CreatureGraveyardHazardTask;
import com.rs2.model.gameplay.magetrainingarena.MageTrainingArenaLobby;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.InventoryManager;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;
import com.rs2.util.TextUtil;
import java.util.Random;

public final class CreatureGraveyardController {
    private static Position[] entryPositions = new Position[]{new Position(3364, 9640, 1), new Position(3363, 9641, 1), new Position(3362, 9640, 1), new Position(3363, 9639, 1)};
    private Player player;
    private static Random random = new Random();
    private static int[] fruitChuteRewardRuneItemIds = new int[]{555, 557, 560, 561, 565};
    public int pizazzPoints;

    public CreatureGraveyardController(Player player) {
        this.player = player;
    }

    private static int getFruitYieldForBoneItemId(int n) {
        switch (n) {
            case 6904: {
                return 1;
            }
            case 6905: {
                return 2;
            }
            case 6906: {
                return 3;
            }
            case 6907: {
                return 4;
            }
        }
        return 0;
    }

    public final boolean isInsideGraveyard() {
        int n = this.player.getPosition().getX();
        int n2 = this.player.getPosition().getY();
        return this.player.getPosition().getPlane() == 1 && n >= 3340 && n <= 3390 && n2 >= 9610 && n2 <= 9670;
    }

    public final void handleGraveyardDeath() {
        this.pizazzPoints -= 10;
        if (this.pizazzPoints < 0) {
            this.pizazzPoints = 0;
        }
        this.player.moveTo(MageTrainingArenaLobby.LOBBY_POSITION);
        this.clearGraveyardItems();
    }

    public final void refreshPizazzInterface() {
        Player player = this.player;
        player.packetSender.showWalkableInterface(15931);
        player = this.player;
        player.packetSender.sendInterfaceText("" + this.pizazzPoints, 15935);
    }

    public static void startFallingBoneHazards() {
        World.scheduleTickTask(new CreatureGraveyardHazardTask(20));
    }

    public final boolean convertBonesToFruit(boolean bl) {
        int player = bl ? 6883 : 1963;
        int[] nArray = new int[]{this.player.getInventoryManager().getItemAmount(6904), this.player.getInventoryManager().getItemAmount(6905), this.player.getInventoryManager().getItemAmount(6906), this.player.getInventoryManager().getItemAmount(6907)};
        if (nArray[0] == 0 && nArray[1] == 0 && nArray[2] == 0 && nArray[3] == 0) {
            Player player2 = this.player;
            player2.packetSender.sendGameMessage("You don't have any bones to convert into fruits.");
            return false;
        }
        this.player.getInventoryManager().removeItem(new ItemStack(6904, nArray[0]));
        this.player.getInventoryManager().removeItem(new ItemStack(6905, nArray[1]));
        this.player.getInventoryManager().removeItem(new ItemStack(6906, nArray[2]));
        this.player.getInventoryManager().removeItem(new ItemStack(6907, nArray[3]));
        this.player.getInventoryManager().addItemUpToFreeSlots(new ItemStack(player, nArray[0] * CreatureGraveyardController.getFruitYieldForBoneItemId(6904)));
        this.player.getInventoryManager().addItemUpToFreeSlots(new ItemStack(player, nArray[1] * CreatureGraveyardController.getFruitYieldForBoneItemId(6905)));
        this.player.getInventoryManager().addItemUpToFreeSlots(new ItemStack(player, nArray[2] * CreatureGraveyardController.getFruitYieldForBoneItemId(6906)));
        this.player.getInventoryManager().addItemUpToFreeSlots(new ItemStack(player, nArray[3] * CreatureGraveyardController.getFruitYieldForBoneItemId(6907)));
        return true;
    }

    private void clearGraveyardItems() {
        this.player.getInventoryManager().removeItem(new ItemStack(6883, this.player.getInventoryManager().getItemAmount(6883)));
        this.player.getInventoryManager().removeItem(new ItemStack(1963, this.player.getInventoryManager().getItemAmount(1963)));
        this.player.getInventoryManager().removeItem(new ItemStack(6904, this.player.getInventoryManager().getItemAmount(6904)));
        this.player.getInventoryManager().removeItem(new ItemStack(6905, this.player.getInventoryManager().getItemAmount(6905)));
        this.player.getInventoryManager().removeItem(new ItemStack(6906, this.player.getInventoryManager().getItemAmount(6906)));
        this.player.getInventoryManager().removeItem(new ItemStack(6907, this.player.getInventoryManager().getItemAmount(6907)));
    }

    private void depositFruitChute() {
        int n;
        int n2 = this.player.getInventoryManager().getItemAmount(1963);
        if (n2 + (n = this.player.getInventoryManager().getItemAmount(6883)) == 0) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You have no fruit to put in the fruit chute.");
            return;
        }
        int n3 = (n2 + n) / 16;
        if (this.player.hasActiveProgressHat()) {
            this.pizazzPoints += n3;
        }
        if (this.pizazzPoints > 4000) {
            this.pizazzPoints = 4000;
        }
        Player player = this.player;
        player.packetSender.sendGameMessage("You've put " + (n2 + n) + " in the food chute and receive " + n3 + " points");
        this.player.getUpdateState().setAnimation(832);
        this.clearGraveyardItems();
        if (n3 >= 0) {
            this.player.getSkillManager().addExperience(6, 25.0);
            n2 = GameUtil.randomInt(2) + 1;
            n = fruitChuteRewardRuneItemIds[random.nextInt(fruitChuteRewardRuneItemIds.length)];
            ItemStack itemStack = new ItemStack(n, n2);
            this.player.getInventoryManager().addOrDropItem(itemStack);
            String string = "";
            string = n2 == 1 ? String.valueOf(string) + TextUtil.prependIndefiniteArticle(itemStack.getDefinition().getName()) : String.valueOf(string) + n2 + " " + itemStack.getDefinition().getName() + "s";
            this.player.getDialogueManager().showTwoLineStatement("Congratulations - you've been awarded " + string + " and extra", "magic XP.");
        }
    }

    public final boolean handleObjectAction(int n, int n2, int n3, int n4) {
        if (n >= 10725 && n <= 10728) {
            int n5;
            if (this.player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
                Player player = this.player;
                player.packetSender.sendGameMessage("Not enough space in your inventory.");
                return true;
            }
            if (GameUtil.randomInclusive(5) == 0) {
                int n6 = n4;
                n4 = n3;
                n3 = n2;
                n2 = n;
                LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n2, n3, n4, n6);
                if (n2 != 10725 && loadedWorldObject != null) {
                    ObjectManager.getInstance().removeDynamicObjectAt(n3, n4, n6, 10);
                    new DynamicObject(n2 - 1, n3, n4, n6, loadedWorldObject.getOrientation(), loadedWorldObject.getType(), n2, 20);
                }
            }
            if (GameUtil.randomInclusive(3) == 0) {
                this.player.applyDirectHit(2, HitType.NORMAL);
            }
            InventoryManager inventoryManager = this.player.getInventoryManager();
            n2 = n;
            switch (n2) {
                case 10725: {
                    n5 = 6904;
                    break;
                }
                case 10726: {
                    n5 = 6905;
                    break;
                }
                case 10727: {
                    n5 = 6906;
                    break;
                }
                case 10728: {
                    n5 = 6907;
                    break;
                }
                default: {
                    n5 = 0;
                }
            }
            inventoryManager.addItem(new ItemStack(n5));
            this.player.getUpdateState().setAnimation(832);
            return true;
        }
        if (n == 10782 && this.isInsideGraveyard()) {
            CreatureGraveyardController creatureGraveyardController = this;
            creatureGraveyardController.player.moveTo(MageTrainingArenaLobby.LOBBY_POSITION);
            Player player = creatureGraveyardController.player;
            player.packetSender.sendGameMessage("You've left the Creature Graveyard.");
            creatureGraveyardController.clearGraveyardItems();
            player = creatureGraveyardController.player;
            player.packetSender.showWalkableInterface(-1);
            return true;
        }
        if (n == 10781) {
            CreatureGraveyardController creatureGraveyardController = this;
            Random random = new Random();
            n4 = random.nextInt(4);
            if (creatureGraveyardController.player.getSkillManager().getCurrentLevels()[6] < 15) {
                Player player = creatureGraveyardController.player;
                player.packetSender.sendGameMessage("You need a magic level of 21 to enter here.");
            } else {
                creatureGraveyardController.player.moveTo(entryPositions[n4]);
                Player player = creatureGraveyardController.player;
                player.packetSender.sendGameMessage("You've entered the Creature Graveyard.");
            }
            return true;
        }
        if (n == 10735) {
            this.depositFruitChute();
            return true;
        }
        return false;
    }

    public static boolean advanceBonePileRespawnStage(DynamicObject dynamicObject) {
        if (dynamicObject == null) {
            return false;
        }
        DynamicObject dynamicObject2 = dynamicObject;
        switch (dynamicObject.getWorldObject().getObjectId()) {
            case 10725: {
                new DynamicObject(dynamicObject2.getWorldObject().getObjectId() + 1, dynamicObject2.getWorldObject().getPosition().getX(), dynamicObject2.getWorldObject().getPosition().getY(), dynamicObject2.getWorldObject().getPosition().getPlane(), dynamicObject2.getWorldObject().getOrientation(), dynamicObject2.getWorldObject().getType(), dynamicObject2.getWorldObject().getObjectId(), 20);
                return true;
            }
            case 10726: {
                new DynamicObject(dynamicObject2.getWorldObject().getObjectId() + 1, dynamicObject2.getWorldObject().getPosition().getX(), dynamicObject2.getWorldObject().getPosition().getY(), dynamicObject2.getWorldObject().getPosition().getPlane(), dynamicObject2.getWorldObject().getOrientation(), dynamicObject2.getWorldObject().getType(), dynamicObject2.getWorldObject().getObjectId(), 20);
                return true;
            }
            case 10727: {
                new DynamicObject(dynamicObject2.getWorldObject().getObjectId() + 1, dynamicObject2.getWorldObject().getPosition().getX(), dynamicObject2.getWorldObject().getPosition().getY(), dynamicObject2.getWorldObject().getPosition().getPlane(), dynamicObject2.getWorldObject().getOrientation(), dynamicObject2.getWorldObject().getType(), dynamicObject2.getWorldObject().getObjectId(), 500000);
                return true;
            }
        }
        return false;
    }
}

