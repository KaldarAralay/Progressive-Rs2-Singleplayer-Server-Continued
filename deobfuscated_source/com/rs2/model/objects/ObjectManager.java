/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.gameplay.magetrainingarena.CreatureGraveyardController;
import com.rs2.model.gameplay.partyroom.PartyRoomManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.functions.DoorHandler;
import com.rs2.model.objects.functions.DoubleDoorHandler;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.farming.MithrilSeedFlowerHandler;
import com.rs2.model.skill.firemaking.FiremakingHandler;
import com.rs2.util.GameUtil;
import com.rs2.util.ProfilerRegistry;
import com.rs2.util.ProfilerTimer;
import com.rs2.util.path.ProjectileCollisionMap;
import com.rs2.util.path.WalkingCollisionMap;
import java.util.ArrayList;

public final class ObjectManager {
    public static ArrayList activeDynamicObjects = new ArrayList();
    private static ArrayList pendingRemovalObjects = new ArrayList();
    private static ObjectManager instance;
    private static int[][] d;

    static {
        int[][] nArrayArray = new int[12][];
        nArrayArray[0] = new int[]{4415, 2425, 3074, 3, 2426, 3074, 2425, 3077, 2};
        nArrayArray[1] = new int[]{4417, 2425, 3074, 2, 2425, 3077, 2426, 3074, 3};
        nArrayArray[2] = new int[]{4415, 2430, 3081, 2, 2430, 3080, 2427, 3081, 1};
        nArrayArray[3] = new int[]{4417, 2428, 3081, 1, 2427, 3081, 2430, 3080, 2};
        int[] nArray = new int[9];
        nArray[0] = 4415;
        nArray[1] = 2419;
        nArray[2] = 3080;
        nArray[3] = 1;
        nArray[4] = 2420;
        nArray[5] = 3080;
        nArray[6] = 2419;
        nArray[7] = 3077;
        nArrayArray[4] = nArray;
        int[] nArray2 = new int[9];
        nArray2[0] = 4417;
        nArray2[1] = 2419;
        nArray2[2] = 3078;
        nArray2[4] = 2419;
        nArray2[5] = 3077;
        nArray2[6] = 2420;
        nArray2[7] = 3080;
        nArray2[8] = 1;
        nArrayArray[5] = nArray2;
        int[] nArray3 = new int[9];
        nArray3[0] = 4418;
        nArray3[1] = 2380;
        nArray3[2] = 3127;
        nArray3[4] = 2380;
        nArray3[5] = 3130;
        nArray3[6] = 2379;
        nArray3[7] = 3127;
        nArray3[8] = 1;
        nArrayArray[6] = nArray3;
        int[] nArray4 = new int[9];
        nArray4[0] = 4415;
        nArray4[1] = 2380;
        nArray4[2] = 3127;
        nArray4[3] = 1;
        nArray4[4] = 2379;
        nArray4[5] = 3127;
        nArray4[6] = 2380;
        nArray4[7] = 3130;
        nArrayArray[7] = nArray4;
        nArrayArray[8] = new int[]{4418, 2369, 3126, 1, 2372, 3126, 2369, 3127, 2};
        nArrayArray[9] = new int[]{4415, 2369, 3126, 2, 2369, 3127, 2372, 3126, 1};
        nArrayArray[10] = new int[]{4418, 2374, 3131, 2, 2374, 3130, 2373, 3133, 3};
        nArrayArray[11] = new int[]{4415, 2374, 3133, 3, 2373, 3133, 2374, 3130, 2};
        d = nArrayArray;
    }

    public ObjectManager() {
        int[] nArray = new int[]{14829, 14830, 14827, 14828, 14826, 14831};
        int[][] nArrayArray = new int[][]{{3154, 3618}, {3225, 3665}, {3033, 3730}, {3104, 3792}, {2978, 3864}, {3305, 3914}};
    }

    public static ObjectManager getInstance() {
        if (instance == null) {
            instance = new ObjectManager();
        }
        return instance;
    }

    public final void processObjects() {
        ArrayList<DynamicObject> arrayList = new ArrayList<DynamicObject>();
        ProfilerTimer profilerTimer = ProfilerRegistry.getTimer("tickObjects");
        profilerTimer.start();
        for (Object dynamicObjectObject : activeDynamicObjects) {
            DynamicObject dynamicObject = (DynamicObject)dynamicObjectObject;
            if (dynamicObject.remainingTicks > 0 && dynamicObject.remainingTicks < 99999) {
                --dynamicObject.remainingTicks;
                continue;
            }
            if (dynamicObject.remainingTicks >= 99999) continue;
            pendingRemovalObjects.add(dynamicObject);
            this.revertDynamicObject(dynamicObject);
            if (!(dynamicObject.getWorldObject().getObjectId() <= 2078 && dynamicObject.getWorldObject().getObjectId() > 2073 || dynamicObject.getWorldObject().getObjectId() <= 1413 && dynamicObject.getWorldObject().getObjectId() > 1408) && (dynamicObject.getWorldObject().getObjectId() < 10725 || dynamicObject.getWorldObject().getObjectId() > 10727)) continue;
            arrayList.add(dynamicObject);
        }
        for (Object dynamicObjectObject : pendingRemovalObjects) {
            DynamicObject dynamicObject = (DynamicObject)dynamicObjectObject;
            activeDynamicObjects.remove(dynamicObject);
            if (dynamicObject.getWorldObject().getObjectId() < 115 || dynamicObject.getWorldObject().getObjectId() > 122) continue;
            PartyRoomManager.activeBalloonObjects.remove(dynamicObject);
        }
        for (DynamicObject dynamicObject : arrayList) {
            if (CreatureGraveyardController.advanceBonePileRespawnStage(dynamicObject)) continue;
            int n = dynamicObject.getWorldObject().getPosition().getX();
            int n2 = dynamicObject.getWorldObject().getPosition().getY();
            int n3 = dynamicObject.getWorldObject().getPosition().getPlane();
            int n4 = dynamicObject.getWorldObject().getOrientation();
            int n5 = dynamicObject.getWorldObject().getType();
            new DynamicObject(dynamicObject.getWorldObject().getObjectId() - 1, n, n2, n3, n4, n5, dynamicObject.getWorldObject().getObjectId() - 1, 10);
        }
        pendingRemovalObjects.clear();
        arrayList.clear();
        profilerTimer.stop();
    }

    public final void revertDynamicObject(DynamicObject dynamicObject) {
        Object object;
        if (dynamicObject.getWorldObject().getObjectId() == 734) {
            ObjectManager.restoreObjectCollision(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
        } else if (!(!dynamicObject.updatesCollision || dynamicObject.getWorldObject().getObjectId() <= 0 || dynamicObject.getWorldObject().getObjectId() == ServerSettings.placeholderObjectId || FiremakingHandler.isFireObjectId(dynamicObject.getWorldObject().getObjectId()) || MithrilSeedFlowerHandler.isMithrilSeedFlowerObjectId(dynamicObject.getWorldObject().getObjectId()) || DoorHandler.hasDoorAt(dynamicObject.getWorldObject().getObjectId(), dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane()) || DoubleDoorHandler.hasDoubleDoorAt(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane()))) {
            ObjectManager.restoreObjectCollision(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
        }
        if (FiremakingHandler.isFireObjectId(dynamicObject.getWorldObject().getObjectId())) {
            object = new GroundItem(new ItemStack(592), new Position(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane()), false, dynamicObject.owner);
            GroundItemManager.getInstance().spawn((GroundItem)object);
        }
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            object = playerArray[n2];
            if (object != null && ((Entity)object).getPosition().getPlane() == dynamicObject.getWorldObject().getPosition().getPlane() && GameUtil.isWithinDistance(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), ((Entity)object).getPosition().getX(), ((Entity)object).getPosition().getY(), 60)) {
                ((Player)object).packetSender.sendObjectCreate(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.orientation, dynamicObject.getWorldObject().getType());
            }
            ++n2;
        }
    }

    public static DynamicObject findDynamicObjectAt(int n, int n2, int n3) {
        for (Object dynamicObjectObject : activeDynamicObjects) {
            DynamicObject dynamicObject = (DynamicObject)dynamicObjectObject;
            if (dynamicObject.getWorldObject().getPosition().getX() != n || dynamicObject.getWorldObject().getPosition().getY() != n2 || dynamicObject.getWorldObject().getPosition().getPlane() != n3) continue;
            return dynamicObject;
        }
        return null;
    }

    public static DynamicObject findDynamicObjectByIdAt(int n, int n2, int n3, int n4) {
        for (Object dynamicObjectObject : activeDynamicObjects) {
            DynamicObject dynamicObject = (DynamicObject)dynamicObjectObject;
            if (dynamicObject.getWorldObject().getObjectId() != n || dynamicObject.getWorldObject().getPosition().getX() != n2 || dynamicObject.getWorldObject().getPosition().getY() != n3 || dynamicObject.getWorldObject().getPosition().getPlane() != n4) continue;
            return dynamicObject;
        }
        return null;
    }

    public final void removeDynamicObjectAt(int n, int n2, int n3, int n4) {
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n, n2, n3);
        if (dynamicObject != null) {
            ObjectManager.removeObjectCollision(dynamicObject.getWorldObject().getObjectId(), n, n2, n3, n4, dynamicObject.getWorldObject().getOrientation());
            if (dynamicObject.getWorldObject().getObjectId() >= 115 && dynamicObject.getWorldObject().getObjectId() <= 122) {
                PartyRoomManager.activeBalloonObjects.remove(dynamicObject);
            }
            activeDynamicObjects.remove(dynamicObject);
            this.revertDynamicObject(dynamicObject);
        }
    }

    public final void refreshDynamicObjectsForPlayer(Player player) {
        if (player == null) {
            return;
        }
        for (Object visibleDynamicObjectObject : player.visibleDynamicObjects) {
            DynamicObject dynamicObject = (DynamicObject)visibleDynamicObjectObject;
            if (activeDynamicObjects.size() == 0) {
                if (!ObjectManager.isVisibleToPlayer(dynamicObject, player)) continue;
                player.pendingDynamicObjectRemovals.add(dynamicObject);
                Player player2 = player;
                player2.packetSender.sendObjectCreate(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.orientation, dynamicObject.getWorldObject().getType());
                continue;
            }
            boolean bl = false;
            for (Object dynamicObject2Object : activeDynamicObjects) {
                DynamicObject dynamicObject2 = (DynamicObject)dynamicObject2Object;
                if (dynamicObject != dynamicObject2) continue;
                bl = true;
                break;
            }
            if (bl || !ObjectManager.isVisibleToPlayer(dynamicObject, player)) continue;
            player.pendingDynamicObjectRemovals.add(dynamicObject);
            Player player3 = player;
            player3.packetSender.sendObjectCreate(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.orientation, dynamicObject.getWorldObject().getType());
        }
        for (Object pendingDynamicObjectObject : player.pendingDynamicObjectRemovals) {
            DynamicObject dynamicObject = (DynamicObject)pendingDynamicObjectObject;
            player.visibleDynamicObjects.remove(dynamicObject);
        }
        player.pendingDynamicObjectRemovals.clear();
        for (Object dynamicObjectObject : activeDynamicObjects) {
            DynamicObject dynamicObject = (DynamicObject)dynamicObjectObject;
            if (!ObjectManager.isVisibleToPlayer(dynamicObject, player)) continue;
            boolean bl = false;
            for (Object dynamicObject2Object : player.visibleDynamicObjects) {
                DynamicObject dynamicObject2 = (DynamicObject)dynamicObject2Object;
                if (dynamicObject2 != dynamicObject) continue;
                bl = true;
                break;
            }
            if (!bl) {
                player.visibleDynamicObjects.add(dynamicObject);
            }
            Player player4 = player;
            player4.packetSender.sendObjectCreate(dynamicObject.getWorldObject().getObjectId(), dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
        }
    }

    public static void removeWallObjectCollision(int n, int n2, int n3, int n4) {
        WalkingCollisionMap.removeStraightWallCollision(n, n2, n3, n4);
        ProjectileCollisionMap.removeWallCollision(n, n2, n3, 0, n4, true);
    }

    private static void restoreObjectCollision(int n, int n2, int n3, int n4, int n5, int n6) {
        WalkingCollisionMap.addObjectCollision(n, n2, n3, n4, n5, n6, false);
        ProjectileCollisionMap.addObjectCollision(n, n2, n3, n4, n5, n6, false);
    }

    public static void removeTypeNineObjectCollision(int n, int n2, int n3, int n4, int n5) {
        WalkingCollisionMap.removeObjectCollision(1, n, n2, n3, n5, 9);
        ProjectileCollisionMap.removeObjectCollision(1, n, n2, n3, n5, 9);
    }

    public static void removeObjectCollision(int n, int n2, int n3, int n4, int n5, int n6) {
        WalkingCollisionMap.removeObjectCollision(n, n2, n3, n4, n6, n5);
        ProjectileCollisionMap.removeObjectCollision(n, n2, n3, n4, n6, n5);
    }

    private static boolean isVisibleToPlayer(DynamicObject dynamicObject, Player player) {
        if (dynamicObject == null || player == null) {
            return false;
        }
        return player.getPosition().getPlane() == dynamicObject.getWorldObject().getPosition().getPlane() && GameUtil.isWithinDistance(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), player.getPosition().getX(), player.getPosition().getY(), 60);
    }

    public final void addDynamicObject(DynamicObject dynamicObject, boolean updateCollision) {
        if (ObjectManager.findDynamicObjectAt(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane()) == null) {
            activeDynamicObjects.add(dynamicObject);
            if (updateCollision) {
                if (dynamicObject.getWorldObject().getObjectId() > 0 && dynamicObject.getWorldObject().getObjectId() != ServerSettings.placeholderObjectId && !FiremakingHandler.isFireObjectId(dynamicObject.getWorldObject().getObjectId()) && !MithrilSeedFlowerHandler.isMithrilSeedFlowerObjectId(dynamicObject.getWorldObject().getObjectId())) {
                    ObjectManager.restoreObjectCollision(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
                } else {
                    ObjectManager.removeObjectCollision(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
                }
            }
            Player[] playerArray = World.getPlayers();
            int n3 = playerArray.length;
            int n2 = 0;
            while (n2 < n3) {
                Player player = playerArray[n2];
                if (player != null && player.getPosition().getPlane() == dynamicObject.getWorldObject().getPosition().getPlane() && GameUtil.isWithinDistance(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), player.getPosition().getX(), player.getPosition().getY(), 60)) {
                    player.packetSender.sendObjectCreate(dynamicObject.getWorldObject().getObjectId(), dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
                    player.visibleDynamicObjects.add(dynamicObject);
                }
                ++n2;
            }
        }
    }

    public static void prepareObjectInteractionMovement(Player player, int n, int n2, int n3) {
        player.ag = 0;
        player.af = 0;
        player.ac = 0;
        player.ab = 0;
        switch (n) {
            case 4493: 
            case 4494: 
            case 4495: 
            case 4496: {
                player.af = 2;
                player.ag = 4;
                return;
            }
            case 1722: 
            case 1723: {
                player.af = 2;
                player.ag = 3;
                return;
            }
            case 1747: 
            case 1750: 
            case 1755: 
            case 2148: 
            case 2609: 
            case 8744: 
            case 9319: {
                int n4 = SkillActionHelper.getObjectOrientation(n, n2, n3, 0);
                player.ab = n2 + (n4 == 1 ? 1 : (n4 == 3 ? -1 : 0));
                player.ac = n3 + (n4 == 0 ? 1 : (n4 == 2 ? -1 : 0));
                return;
            }
            case 3566: {
                player.ab = player.getPosition().getX() > 2768 ? 2769 : 2764;
                player.ac = player.getPosition().getX() > 2768 ? 9567 : 9569;
                return;
            }
            case 2640: {
                player.af = 1;
                player.ag = 2;
                return;
            }
            case 3044: {
                player.af = 5;
                player.ag = 5;
                return;
            }
            case 2309: {
                player.ab = 2998;
                player.ac = 3916;
                return;
            }
            case 2307: 
            case 2308: {
                player.ab = 2998;
                player.ac = 3931;
                return;
            }
            case 2288: {
                player.ab = 3004;
                player.ac = 3937;
                return;
            }
            case 2283: {
                player.ab = 3005;
                player.ac = 3953;
                player.ae = 2;
                return;
            }
            case 2311: {
                player.ab = 3002;
                player.ac = 3960;
                return;
            }
            case 2297: {
                player.ab = 3002;
                player.ac = 3945;
                return;
            }
            case 2295: {
                player.ab = 2474;
                player.ac = 3436;
                return;
            }
            case 154: {
                player.ab = 2484;
                player.ac = 3430;
                return;
            }
            case 4058: {
                player.ab = 2487;
                player.ac = 3430;
                return;
            }
            case 1815: 
            case 1816: 
            case 5959: 
            case 5960: {
                player.ag = 0;
                player.af = 0;
                break;
            }
            case 4467: 
            case 4468: 
            case 8959: {
                player.ae = 1;
                break;
            }
            case 4465: 
            case 4466: {
                player.ae = -1;
                break;
            }
            case 4381: {
                player.ae = 2;
                break;
            }
            case 4382: {
                player.ae = -2;
                break;
            }
            case 4419: {
                if (player.isInCastleWarsObstacleArea(player.getPosition().getX(), player.getPosition().getY())) {
                    player.ab = 2416;
                    player.ac = 3074;
                    player.Y = 2417;
                    player.Z = 3077;
                } else {
                    player.ab = 2417;
                    player.ac = 3077;
                    player.Y = 2416;
                    player.Z = 3074;
                }
                player.aa = 0;
                return;
            }
            case 4420: {
                if (player.isInCastleWarsObstacleArea(player.getPosition().getX(), player.getPosition().getY())) {
                    player.ab = 2383;
                    player.ac = 3133;
                    player.Y = 2382;
                    player.Z = 3130;
                } else {
                    player.ab = 2382;
                    player.ac = 3130;
                    player.Y = 2383;
                    player.Z = 3133;
                }
                player.aa = 0;
                return;
            }
            case 2558: {
                player.getPosition().getX();
                if (player.getPosition().getY() > 0) {
                    player.ae = 1;
                }
                player.getPosition().getX();
                player.ag = 0;
                player.af = 0;
                break;
            }
            case 4031: 
            case 6706: {
                player.ad = 2;
                break;
            }
            case 6707: {
                player.ae = 3;
                break;
            }
            case 6823: {
                player.ae = 1;
                break;
            }
            case 6772: {
                player.ae = 1;
                break;
            }
            case 6705: {
                player.ae = -1;
                break;
            }
            case 6822: {
                player.ae = 1;
                break;
            }
            case 6704: {
                player.ae = -1;
                break;
            }
            case 6773: {
                player.ad = 1;
                player.ae = 1;
                break;
            }
            case 6703: {
                player.ad = -1;
                break;
            }
            case 6771: {
                player.ad = 1;
                player.ae = 1;
                break;
            }
            case 6702: {
                player.ad = -1;
                break;
            }
            case 6821: {
                player.ad = 1;
                player.ae = 1;
            }
        }
        int[][] nArray = d;
        n2 = 0;
        while (n2 < 12) {
            int[] nArray2 = nArray[n2];
            if (n == nArray2[0] && 0 == nArray2[1] && 0 == nArray2[2] && player.getPosition().getPlane() == nArray2[3]) {
                player.ab = nArray2[4];
                player.ac = nArray2[5];
                player.Y = nArray2[6];
                player.Z = nArray2[7];
                player.aa = nArray2[8];
                return;
            }
            ++n2;
        }
    }
}

