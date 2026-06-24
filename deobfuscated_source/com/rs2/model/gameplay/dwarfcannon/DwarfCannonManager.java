/*
 * Dwarf multicannon setup and operation.
 */
package com.rs2.model.gameplay.dwarfcannon;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.combat.AttackBonusType;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.combat.AttackXpMode;
import com.rs2.model.combat.CombatAction;
import com.rs2.model.combat.CombatType;
import com.rs2.model.combat.ProjectileTiming;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.woodcutting.WoodcuttingHandler;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.path.WalkingCollisionMap;
import java.util.HashMap;
import java.util.Map;

public final class DwarfCannonManager {
    public static final int CANNONBALL = 2;
    public static final int BASE_ITEM = 6;
    public static final int STAND_ITEM = 8;
    public static final int BARRELS_ITEM = 10;
    public static final int FURNACE_ITEM = 12;
    public static final int FULL_CANNON_OBJECT = 6;
    public static final int BASE_OBJECT = 7;
    public static final int STAND_OBJECT = 8;
    public static final int BARRELS_OBJECT = 9;
    private static final int DWARF_CANNON_QUEST_ID = 30;
    private static final int CANNON_OBJECT_TYPE = 10;
    private static final int STAGE_NONE = 0;
    private static final int STAGE_BASE = 1;
    private static final int STAGE_STAND = 2;
    private static final int STAGE_BARRELS = 3;
    private static final int STAGE_FULL = 4;
    private static final int DECAY_TICKS = 2500;
    private static final int MAX_AMMO = 30;
    private static final int[] TURN_ANIMATIONS = new int[]{515, 516, 517, 518, 519, 520, 521, 514};
    private static final int[] TURN_ORIENTATIONS = new int[]{0, 0, 1, 1, 2, 2, 3, 3};
    private static final AttackStyleDefinition CANNON_ATTACK_STYLE = new AttackStyleDefinition(CombatType.RANGED, AttackXpMode.RANGED_ACCURATE, AttackBonusType.RANGED);
    private static final ProjectileDefinition CANNONBALL_PROJECTILE = new ProjectileDefinition(53, ProjectileTiming.a.copy().setStartDelay(0).setSpeed(5));
    private static final Map cannonsByOwner = new HashMap();
    private static final Map cannonsByCoord = new HashMap();
    private static final Map lostCannonsByOwner = new HashMap();
    private static final int[][] DIRECTION_TARGETS = new int[][]{
        {0, 3, 0, 7, 0, 14},
        {2, 2, 5, 5, 12, 12},
        {3, 0, 7, 0, 14, 0},
        {2, -2, 5, -5, 12, -12},
        {0, -3, 0, -7, 0, -14},
        {-2, -2, -5, -5, -12, -12},
        {-3, 0, -7, 0, -14, 0},
        {-2, 2, -5, 5, -12, 12}
    };

    private DwarfCannonManager() {
    }

    public static boolean handleInventoryItemFirstOption(Player player, int itemId) {
        if (itemId != BASE_ITEM) {
            return false;
        }
        setupBase(player);
        return true;
    }

    public static boolean handleFirstObjectAction(Player player, int objectId, int x, int y, int plane) {
        if (!isCannonObject(objectId)) {
            return false;
        }
        if (objectId == FULL_CANNON_OBJECT) {
            fire(player, x, y, plane);
            return true;
        }
        pickupPartialCannon(player, objectId, x, y, plane);
        return true;
    }

    public static boolean handleSecondObjectAction(Player player, int objectId, int x, int y, int plane) {
        if (objectId != FULL_CANNON_OBJECT) {
            return false;
        }
        pickupFullCannon(player, x, y, plane);
        return true;
    }

    public static boolean handleItemOnObject(Player player, int itemId, int objectId, int x, int y, int plane) {
        if (!isCannonObject(objectId)) {
            return false;
        }
        CannonState state = getStateForObject(x, y, plane);
        if (objectId == FULL_CANNON_OBJECT && itemId == CANNONBALL) {
            loadCannon(player, state);
            return true;
        }
        if (state == null || !state.isOwnedBy(player)) {
            player.packetSender.sendGameMessage(objectId == FULL_CANNON_OBJECT ? "This is not your cannon." : "That isn't your cannon!");
            return true;
        }
        if (objectId == BASE_OBJECT) {
            if (itemId == STAND_ITEM) {
                addPart(player, state, STAND_ITEM, STAND_OBJECT, STAGE_STAND, "You add the stand.");
            } else if (isCannonPart(itemId) && itemId != BASE_ITEM) {
                player.packetSender.sendGameMessage("This cannon needs its stand.");
            } else {
                player.packetSender.sendGameMessage("Nothing interesting happens.");
            }
            return true;
        }
        if (objectId == STAND_OBJECT) {
            if (itemId == BARRELS_ITEM) {
                addPart(player, state, BARRELS_ITEM, BARRELS_OBJECT, STAGE_BARRELS, "You add the barrels.");
            } else if (isCannonPart(itemId) && itemId != STAND_ITEM) {
                player.packetSender.sendGameMessage("This cannon needs its barrels.");
            } else {
                player.packetSender.sendGameMessage("Nothing interesting happens.");
            }
            return true;
        }
        if (objectId == BARRELS_OBJECT) {
            if (itemId == FURNACE_ITEM) {
                addPart(player, state, FURNACE_ITEM, FULL_CANNON_OBJECT, STAGE_FULL, "You add the furnace.");
            } else if (isCannonPart(itemId) && itemId != BARRELS_ITEM) {
                player.packetSender.sendGameMessage("This cannon needs its furnace.");
            } else {
                player.packetSender.sendGameMessage("Nothing interesting happens.");
            }
            return true;
        }
        player.packetSender.sendGameMessage("Nothing interesting happens.");
        return true;
    }

    private static void setupBase(Player player) {
        if (player.getQuestState(DWARF_CANNON_QUEST_ID) != 1) {
            player.packetSender.sendGameMessage("You can't set up this cannon.");
            player.packetSender.sendGameMessage("You need to complete the Dwarf Cannon quest.");
            return;
        }
        CannonState existingState = getStateForOwner(player);
        if (existingState != null) {
            player.packetSender.sendGameMessage("You cannot construct more than one Cannon at a time.");
            player.packetSender.sendGameMessage("If you have lost your Cannon, go and see the Dwarf Cannon engineer.");
            return;
        }
        int centerX = player.getPosition().getX();
        int centerY = player.getPosition().getY();
        int plane = player.getPosition().getPlane();
        int originX = centerX - 1;
        int originY = centerY - 1;
        if (!hasSetupSpace(originX, originY, plane)) {
            player.packetSender.sendGameMessage("There isn't enough space to set up here.");
            return;
        }
        if (!removeSelectedItem(player, BASE_ITEM)) {
            return;
        }
        CannonState state = new CannonState(ownerKey(player), originX, originY, plane);
        state.stage = STAGE_BASE;
        registerState(state);
        player.getUpdateState().setAnimation(827);
        placeCannonObject(state, BASE_OBJECT);
        player.packetSender.sendGameMessage("You place the cannon base on the ground.");
        scheduleDecay(state);
    }

    private static void addPart(Player player, CannonState state, int itemId, int objectId, int stage, String message) {
        if (!player.getInventoryManager().removeItem(new ItemStack(itemId, 1))) {
            return;
        }
        player.getUpdateState().setAnimation(827);
        state.stage = stage;
        placeCannonObject(state, objectId);
        player.packetSender.sendGameMessage(message);
        scheduleDecay(state);
    }

    private static void loadCannon(Player player, CannonState state) {
        if (state == null || !state.isOwnedBy(player)) {
            player.packetSender.sendGameMessage("This is not your cannon.");
            return;
        }
        if (state.stage != STAGE_FULL) {
            player.packetSender.sendGameMessage("Nothing interesting happens.");
            return;
        }
        if (state.ammo >= MAX_AMMO) {
            player.packetSender.sendGameMessage("The cannon is already full of ammo.");
            return;
        }
        int amount = Math.min(MAX_AMMO - state.ammo, player.getInventoryManager().getItemAmount(CANNONBALL));
        if (amount <= 0) {
            return;
        }
        if (!player.getInventoryManager().removeItem(new ItemStack(CANNONBALL, amount))) {
            return;
        }
        state.ammo += amount;
        player.packetSender.sendGameMessage("You load the cannon with " + amount + " cannonballs.");
    }

    private static void fire(Player player, int x, int y, int plane) {
        CannonState state = getStateForObject(x, y, plane);
        if (state == null || !state.isOwnedBy(player)) {
            player.packetSender.sendGameMessage("That isn't your cannon!");
            return;
        }
        if (state.rotating) {
            player.packetSender.sendGameMessage("Your cannon is already firing.");
            return;
        }
        if (state.ammo < 1) {
            player.packetSender.sendGameMessage("Your cannon is out of ammo!");
            return;
        }
        state.rotating = true;
        state.direction = 0;
        World.getTaskScheduler().schedule(new CannonFireTask(player, state.ownerKey, state.decaySerial));
    }

    private static void pickupPartialCannon(Player player, int objectId, int x, int y, int plane) {
        CannonState state = getStateForObject(x, y, plane);
        if (state == null || !state.isOwnedBy(player)) {
            player.packetSender.sendGameMessage("That isn't your cannon!");
            return;
        }
        int[] items = getStageItems(objectId);
        if (items == null) {
            return;
        }
        if (!hasFreeSpacesForPickup(player, items.length, objectId)) {
            return;
        }
        sendPickupMessages(player);
        removeState(state, true);
        lostCannonsByOwner.remove(state.ownerKey);
        int index = 0;
        while (index < items.length) {
            player.getInventoryManager().addItem(new ItemStack(items[index], 1));
            ++index;
        }
    }

    private static void pickupFullCannon(Player player, int x, int y, int plane) {
        CannonState state = getStateForObject(x, y, plane);
        if (state == null || !state.isOwnedBy(player)) {
            player.packetSender.sendGameMessage("This is not your cannon.");
            return;
        }
        if (!hasFreeSpacesForPickup(player, 4, FULL_CANNON_OBJECT)) {
            return;
        }
        sendPickupMessages(player);
        int ammo = state.ammo;
        removeState(state, true);
        lostCannonsByOwner.remove(state.ownerKey);
        player.getInventoryManager().addItem(new ItemStack(BASE_ITEM, 1));
        player.getInventoryManager().addItem(new ItemStack(STAND_ITEM, 1));
        player.getInventoryManager().addItem(new ItemStack(BARRELS_ITEM, 1));
        player.getInventoryManager().addItem(new ItemStack(FURNACE_ITEM, 1));
        if (ammo > 0) {
            player.getInventoryManager().addOrDropItem(new ItemStack(CANNONBALL, Math.min(MAX_AMMO, ammo)));
        }
    }

    private static boolean hasFreeSpacesForPickup(Player player, int needed, int objectId) {
        if (player.getInventoryManager().getContainer().getFreeSlots() >= needed) {
            return true;
        }
        if (objectId == FULL_CANNON_OBJECT) {
            player.packetSender.sendGameMessage("You need 4 free inventory spaces to pick that up.");
        } else if (needed == 1) {
            player.packetSender.sendGameMessage("You need one free inventory spaces to pick that up.");
        } else if (needed == 2) {
            player.packetSender.sendGameMessage("You need two free inventory spaces to pick that up.");
        } else {
            player.packetSender.sendGameMessage("You need three free inventory spaces to pick that up.");
        }
        return false;
    }

    private static void sendPickupMessages(Player player) {
        player.packetSender.sendGameMessage("You pick up the cannon,");
        player.packetSender.sendGameMessage("It's really heavy.");
        player.packetSender.sendSoundEffect(258, 1, 0);
    }

    private static boolean hasSetupSpace(int originX, int originY, int plane) {
        if (SkillActionHelper.findWorldObjectAt(originX, originY, plane) != null) {
            return false;
        }
        int x = originX;
        while (x <= originX + 2) {
            int y = originY;
            while (y <= originY + 2) {
                if (WalkingCollisionMap.getTileFlags(x, y, plane) != 0) {
                    return false;
                }
                if (ObjectManager.findDynamicObjectAt(x, y, plane) != null) {
                    return false;
                }
                ++y;
            }
            ++x;
        }
        Position north = new Position(originX + 1, originY + 2, plane);
        Position south = new Position(originX + 1, originY, plane);
        Position east = new Position(originX + 2, originY + 1, plane);
        Position west = new Position(originX, originY + 1, plane);
        Position northeast = new Position(originX + 2, originY + 2, plane);
        Position southwest = new Position(originX, originY, plane);
        Position northwest = new Position(originX, originY + 2, plane);
        Position southeast = new Position(originX + 2, originY, plane);
        return GameUtil.hasClearPath(north, south, true)
            && GameUtil.hasClearPath(east, west, true)
            && GameUtil.hasClearPath(northeast, southwest, true)
            && GameUtil.hasClearPath(northwest, southeast, true);
    }

    private static void placeCannonObject(CannonState state, int objectId) {
        ObjectManager.getInstance().removeDynamicObjectAt(state.x, state.y, state.plane, 10);
        new DynamicObject(objectId, state.x, state.y, state.plane, 0, CANNON_OBJECT_TYPE, ServerSettings.placeholderObjectId, DECAY_TICKS, false);
    }

    private static CannonState getStateForOwner(Player player) {
        CannonState state = (CannonState)cannonsByOwner.get(ownerKey(player));
        if (state != null && !objectStillExists(state)) {
            removeState(state, false, true);
            return null;
        }
        return state;
    }

    private static CannonState getStateForObject(int x, int y, int plane) {
        CannonState state = (CannonState)cannonsByCoord.get(coordKey(x, y, plane));
        if (state != null && !objectStillExists(state)) {
            removeState(state, false, true);
            return null;
        }
        return state;
    }

    private static boolean objectStillExists(CannonState state) {
        WorldObject object = SkillActionHelper.findWorldObjectAt(state.x, state.y, state.plane);
        return object != null && object.getObjectId() == objectIdForStage(state.stage);
    }

    private static void registerState(CannonState state) {
        cannonsByOwner.put(state.ownerKey, state);
        cannonsByCoord.put(coordKey(state.x, state.y, state.plane), state);
    }

    private static void removeState(CannonState state, boolean removeObject) {
        removeState(state, removeObject, false);
    }

    private static void removeState(CannonState state, boolean removeObject, boolean reclaimable) {
        if (reclaimable && state.stage > STAGE_NONE) {
            lostCannonsByOwner.put(state.ownerKey, new LostCannonState(state.stage));
        }
        state.rotating = false;
        state.stage = STAGE_NONE;
        cannonsByOwner.remove(state.ownerKey);
        cannonsByCoord.remove(coordKey(state.x, state.y, state.plane));
        if (removeObject) {
            ObjectManager.getInstance().removeDynamicObjectAt(state.x, state.y, state.plane, 10);
        }
    }

    private static void scheduleDecay(CannonState state) {
        ++state.decaySerial;
        World.getTaskScheduler().schedule(new CannonDecayTask(state.ownerKey, state.decaySerial));
    }

    public static boolean hasActiveCannon(Player player) {
        return getStateForOwner(player) != null;
    }

    public static boolean hasLostCannon(Player player) {
        return lostCannonsByOwner.containsKey(ownerKey(player));
    }

    public static boolean reclaimLostCannon(Player player) {
        if (hasActiveCannon(player)) {
            return false;
        }
        String ownerKey = ownerKey(player);
        LostCannonState lostState = (LostCannonState)lostCannonsByOwner.get(ownerKey);
        if (lostState == null || lostState.stage <= STAGE_NONE) {
            return false;
        }
        int[] items = getItemsForStage(lostState.stage);
        if (items == null || player.getInventoryManager().getContainer().getFreeSlots() < items.length) {
            player.packetSender.sendGameMessage("You don't have enough inventory space for the cannon parts.");
            return false;
        }
        int index = 0;
        while (index < items.length) {
            player.getInventoryManager().addItem(new ItemStack(items[index], 1));
            ++index;
        }
        lostCannonsByOwner.remove(ownerKey);
        if (items.length == 1) {
            player.packetSender.sendGameMessage("The dwarf gives you a new cannon part.");
        } else if (items.length == 4) {
            player.packetSender.sendGameMessage("The dwarf gives you a new cannon.");
        } else {
            player.packetSender.sendGameMessage("The dwarf gives you new cannon parts.");
        }
        return true;
    }

    public static void handleLogout(Player player) {
        CannonState state = getStateForOwner(player);
        if (state != null) {
            removeState(state, true, true);
        }
    }

    private static Npc findTarget(Player player, CannonState state, Position center) {
        int dir = state.direction % DIRECTION_TARGETS.length;
        int[] targets = DIRECTION_TARGETS[dir];
        int[] radii = new int[]{1, 2, 5};
        int band = 0;
        while (band < 3) {
            int targetX = center.getX() + targets[band * 2];
            int targetY = center.getY() + targets[band * 2 + 1];
            Npc npc = findNpcNear(player, center, state.plane, targetX, targetY, radii[band]);
            if (npc != null) {
                return npc;
            }
            ++band;
        }
        return null;
    }

    private static Npc findNpcNear(Player player, Position center, int plane, int targetX, int targetY, int radius) {
        Npc best = null;
        int bestDistance = Integer.MAX_VALUE;
        Npc[] npcs = World.getNpcs();
        int index = 0;
        while (index < npcs.length) {
            Npc npc = npcs[index];
            if (isValidCannonTarget(player, center, npc, plane, targetX, targetY, radius)) {
                int distance = GameUtil.getDistance(center, npc.getPosition());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = npc;
                }
            }
            ++index;
        }
        return best;
    }

    private static boolean isValidCannonTarget(Player player, Position center, Npc npc, int plane, int targetX, int targetY, int radius) {
        if (npc == null || !npc.isActive() || npc.isDead() || npc.getPosition().getPlane() != plane) {
            return false;
        }
        if (npc.getMaxHitpoints() <= 0 || npc.getNpcId() == 411) {
            return false;
        }
        if (!player.getQuestManager().canAttackNpc(npc.getNpcId())) {
            return false;
        }
        if (!passesCannonCombatRules(player, npc)) {
            return false;
        }
        int npcX = npc.getPosition().getX();
        int npcY = npc.getPosition().getY();
        if (Math.abs(npcX - targetX) > radius || Math.abs(npcY - targetY) > radius) {
            return false;
        }
        return GameUtil.hasClearPath(center, npc.getPosition(), false);
    }

    private static boolean passesCannonCombatRules(Player player, Npc npc) {
        if (npc.isInMultiCombatArea()) {
            return true;
        }
        return isFreeForSingleCombatPair(player, npc) && isFreeForSingleCombatPair(npc, player);
    }

    private static boolean isFreeForSingleCombatPair(Entity entity, Entity target) {
        Entity currentTarget = entity.getSingleCombatTimer().getTarget();
        return currentTarget == null || entity.getSingleCombatTimer().hasElapsed() || currentTarget == target;
    }

    private static void shoot(Player player, CannonState state, Npc target) {
        Position center = state.center();
        state.ammo -= 1;
        new WoodcuttingHandler(center, 1, target.getPosition(), target.getIndex() + 1, CANNONBALL_PROJECTILE).sendProjectileToNearbyPlayers();
        HitDefinition hit = new HitDefinition(CANNON_ATTACK_STYLE, HitType.NORMAL, 30).enableRandomDamage().enableAccuracyCheck().setDelay(1).setBlockAnimationEnabled(false);
        target.getRecentCombatTimer().setTargetDelay(player, 17);
        target.getSingleCombatTimer().setTargetDelay(player, 8);
        new CombatAction(player, target, hit).queue(false);
    }

    private static void animateTurn(CannonState state) {
        int direction = state.direction % TURN_ANIMATIONS.length;
        int animationId = TURN_ANIMATIONS[direction];
        int orientation = TURN_ORIENTATIONS[direction];
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(state.x, state.y, state.plane);
        if (dynamicObject != null) {
            dynamicObject.orientation = orientation;
        }
        Position center = state.center();
        Player[] players = World.getPlayers();
        int index = 0;
        while (index < players.length) {
            Player player = players[index];
            if (player != null && !player.isBot && center.isWithinViewport(player.getPosition())) {
                player.packetSender.sendObjectAnimation(state.x, state.y, state.plane, animationId);
            }
            ++index;
        }
    }

    private static Player findOnlinePlayer(String key) {
        Player[] players = World.getPlayers();
        int index = 0;
        while (index < players.length) {
            Player player = players[index];
            if (player != null && ownerKey(player).equals(key)) {
                return player;
            }
            ++index;
        }
        return null;
    }

    private static boolean removeSelectedItem(Player player, int itemId) {
        int slot = player.getSelectedItemSlot();
        if (player.getInventoryManager().removeItemFromSlot(new ItemStack(itemId, 1), slot)) {
            return true;
        }
        return player.getInventoryManager().removeItem(new ItemStack(itemId, 1));
    }

    private static int[] getStageItems(int objectId) {
        if (objectId == BASE_OBJECT) {
            return new int[]{BASE_ITEM};
        }
        if (objectId == STAND_OBJECT) {
            return new int[]{BASE_ITEM, STAND_ITEM};
        }
        if (objectId == BARRELS_OBJECT) {
            return new int[]{BASE_ITEM, STAND_ITEM, BARRELS_ITEM};
        }
        return null;
    }

    private static int[] getItemsForStage(int stage) {
        if (stage == STAGE_BASE) {
            return new int[]{BASE_ITEM};
        }
        if (stage == STAGE_STAND) {
            return new int[]{BASE_ITEM, STAND_ITEM};
        }
        if (stage == STAGE_BARRELS) {
            return new int[]{BASE_ITEM, STAND_ITEM, BARRELS_ITEM};
        }
        if (stage == STAGE_FULL) {
            return new int[]{BASE_ITEM, STAND_ITEM, BARRELS_ITEM, FURNACE_ITEM};
        }
        return null;
    }

    private static boolean isCannonObject(int objectId) {
        return objectId == FULL_CANNON_OBJECT || objectId == BASE_OBJECT || objectId == STAND_OBJECT || objectId == BARRELS_OBJECT;
    }

    private static boolean isCannonPart(int itemId) {
        return itemId == BASE_ITEM || itemId == STAND_ITEM || itemId == BARRELS_ITEM || itemId == FURNACE_ITEM;
    }

    private static int objectIdForStage(int stage) {
        if (stage == STAGE_BASE) {
            return BASE_OBJECT;
        }
        if (stage == STAGE_STAND) {
            return STAND_OBJECT;
        }
        if (stage == STAGE_BARRELS) {
            return BARRELS_OBJECT;
        }
        if (stage == STAGE_FULL) {
            return FULL_CANNON_OBJECT;
        }
        return ServerSettings.placeholderObjectId;
    }

    private static String ownerKey(Player player) {
        return player.getUsername().toLowerCase();
    }

    private static String coordKey(int x, int y, int plane) {
        return x + "," + y + "," + plane;
    }

    private static final class CannonState {
        private final String ownerKey;
        private final int x;
        private final int y;
        private final int plane;
        private int stage;
        private int ammo;
        private boolean rotating;
        private int direction;
        private int decaySerial;

        private CannonState(String ownerKey, int x, int y, int plane) {
            this.ownerKey = ownerKey;
            this.x = x;
            this.y = y;
            this.plane = plane;
        }

        private boolean isOwnedBy(Player player) {
            return this.ownerKey.equals(DwarfCannonManager.ownerKey(player));
        }

        private Position center() {
            return new Position(this.x + 1, this.y + 1, this.plane);
        }
    }

    private static final class LostCannonState {
        private final int stage;

        private LostCannonState(int stage) {
            this.stage = stage;
        }
    }

    private static final class CannonDecayTask
    extends TickTask {
        private final String ownerKey;
        private final int decaySerial;

        private CannonDecayTask(String ownerKey, int decaySerial) {
            super(DECAY_TICKS);
            this.ownerKey = ownerKey;
            this.decaySerial = decaySerial;
        }

        @Override
        public void execute() {
            CannonState state = (CannonState)cannonsByOwner.get(this.ownerKey);
            if (state != null && state.decaySerial == this.decaySerial) {
                Player player = findOnlinePlayer(this.ownerKey);
                if (player != null) {
                    player.packetSender.sendGameMessage("Your cannon has decayed.");
                }
                removeState(state, true, true);
            }
            this.stop();
        }
    }

    private static final class CannonFireTask
    extends TickTask {
        private final Player player;
        private final String ownerKey;
        private final int decaySerial;

        private CannonFireTask(Player player, String ownerKey, int decaySerial) {
            super(1, true);
            this.player = player;
            this.ownerKey = ownerKey;
            this.decaySerial = decaySerial;
        }

        @Override
        public void execute() {
            CannonState state = (CannonState)cannonsByOwner.get(this.ownerKey);
            if (state == null || state.decaySerial != this.decaySerial || !state.rotating || state.stage != STAGE_FULL || !objectStillExists(state)) {
                this.stop();
                return;
            }
            if (state.ammo < 1 && state.direction == 0) {
                this.player.packetSender.sendGameMessage("Your cannon is out of ammo!");
                state.rotating = false;
                this.stop();
                return;
            }
            if (state.ammo < 1) {
                animateTurn(state);
                state.direction = (state.direction + 1) % DIRECTION_TARGETS.length;
                return;
            }
            Position center = state.center();
            Npc target = findTarget(this.player, state, center);
            if (target != null) {
                shoot(this.player, state, target);
            }
            animateTurn(state);
            state.direction = (state.direction + 1) % DIRECTION_TARGETS.length;
        }
    }
}
