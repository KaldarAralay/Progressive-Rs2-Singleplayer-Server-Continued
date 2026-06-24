/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects.functions;

import com.rs2.ServerSettings;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.objects.functions.DoorResetEvent;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.task.CycleEventHandler;
import java.util.ArrayList;
import java.util.List;

public final class DoorHandler {
    private static List doorStates = new ArrayList();
    private boolean open;
    private long lastInteractionMillis;
    private int rapidToggleCount;
    private int currentObjectId;
    private int originalObjectId;
    private int currentX;
    private int currentY;
    private int originalX;
    private int originalY;
    private int plane;
    private int originalOrientation;
    private int currentOrientation;
    private int objectType;
    private boolean initiallyOpen;
    private static int[] initiallyOpenObjectIds = new int[]{1504, 1514, 1517, 1520, 1531, 1534, 2033, 2035, 2037, 2998, 3271, 4468, 4697, 6101, 6103, 6105, 6107, 6109, 6111, 6113, 6115, 6976, 6978, 8696, 8819, 10261, 10263, 10265, 11708, 11710, 11712, 11715, 11994, 12445, 13002};

    private DoorHandler(int n, int n2, int n3, int n4) {
        int n5;
        this.currentObjectId = n;
        this.originalObjectId = n;
        this.currentX = n2;
        this.currentY = n3;
        this.originalX = n2;
        this.originalY = n3;
        this.plane = n4;
        this.originalOrientation = n5 = SkillActionHelper.getObjectOrientation(n, n2, n3, n4);
        this.currentOrientation = n5;
        this.objectType = n2 = SkillActionHelper.getObjectType(n, n2, n3, n4);
        this.open = this.initiallyOpen = DoorHandler.isInitiallyOpenObjectId(n);
        this.lastInteractionMillis = 0L;
        this.rapidToggleCount = 0;
    }

    private static DoorHandler getOrCreateDoorState(int n, int n2, int n3, int n4) {
        for (Object doorStateObject : doorStates) {
            DoorHandler doorHandler2 = (DoorHandler)doorStateObject;
            if (doorHandler2.currentX != n2 || doorHandler2.currentY != n3 || doorHandler2.plane != n4) continue;
            return doorHandler2;
        }
        DoorHandler doorHandler2 = new DoorHandler(n, n2, n3, n4);
        doorStates.add(doorHandler2);
        return doorHandler2;
    }

    public static boolean hasDoorAt(int n, int n2, int n3, int n4) {
        return DoorHandler.getOrCreateDoorState(n, n2, n3, n4) != null;
    }

    public static boolean handleDoor(Player player, int n, int n2, int n3, int n4) {
        Object object;
        Object object2;
        Object object3;
        if (ObjectDefinition.forId(n) != null) {
            object3 = ObjectDefinition.forId(n);
            object2 = ((ObjectDefinition)object3).name.toLowerCase();
        } else {
            object2 = object = "";
        }
        if (!((String)object2).contains("fence") && !((String)object2).contains("gate") && !((String)object2).contains("door") || ((String)object2).contains("trapdoor") || ((String)object2).contains("tree")) {
            return false;
        }
        if (n == 9300 || n == 9299 || n == 883 || n == 1805 || n == 2882 || n == 2883 || n == 2623 || n == 2112 || n == 1804 || n == 2266 || n == 2406 || n == 2407 || n == 2631 || n == 2623 || n == 8958 || n == 8959 || n == 8960 || n == 1589 || n == 1590 || n == 2155 && n2 == 2592 && n3 == 9490 || n == 2154 && n2 == 2593 && n3 == 9490) {
            return false;
        }
        object = DoorHandler.getOrCreateDoorState(n, n2, n3, n4);
        if (object == null) {
            return false;
        }
        if (((DoorHandler)object).rapidToggleCount >= 5 && ((DoorHandler)object).open) {
            if (System.currentTimeMillis() - ((DoorHandler)object).lastInteractionMillis >= 10000L) {
                ((DoorHandler)object).rapidToggleCount = 0;
            } else {
                object3 = player;
                ((Player)object3).packetSender.sendGameMessage("The door is stuck open.");
                return true;
            }
        }
        ObjectManager.getInstance();
        object3 = ObjectManager.findDynamicObjectByIdAt(n, n2, n3, n4);
        if (object3 != null) {
            if (((DoorHandler)object).objectType == 9) {
                ObjectManager.getInstance();
                ObjectManager.removeTypeNineObjectCollision(n2, n3, n4, 9, ((DynamicObject)object3).getWorldObject().getOrientation());
            } else {
                ObjectManager.getInstance();
                ObjectManager.removeWallObjectCollision(n2, n3, n4, ((DynamicObject)object3).getWorldObject().getOrientation());
            }
        }
        if ((object3 = WorldObjectLookup.findObjectByIdAt(n, n2, n3, n4)) != null) {
            if (((DoorHandler)object).objectType == 9) {
                ObjectManager.getInstance();
                ObjectManager.removeTypeNineObjectCollision(n2, n3, n4, 9, ((LoadedWorldObject)object3).getOrientation());
            } else {
                ObjectManager.getInstance();
                ObjectManager.removeWallObjectCollision(n2, n3, n4, ((LoadedWorldObject)object3).getOrientation());
            }
        }
        n2 = 0;
        n3 = 0;
        if (((DoorHandler)object).objectType == 0) {
            if (!((DoorHandler)object).initiallyOpen) {
                if (((DoorHandler)object).originalOrientation == 0 && ((DoorHandler)object).currentOrientation == 0) {
                    n2 = -1;
                } else if (((DoorHandler)object).originalOrientation == 1 && ((DoorHandler)object).currentOrientation == 1) {
                    n3 = 1;
                } else if (((DoorHandler)object).originalOrientation == 2 && ((DoorHandler)object).currentOrientation == 2) {
                    n2 = 1;
                } else if (((DoorHandler)object).originalOrientation == 3 && ((DoorHandler)object).currentOrientation == 3) {
                    n3 = -1;
                }
            } else if (((DoorHandler)object).initiallyOpen) {
                if (((DoorHandler)object).originalOrientation == 0 && ((DoorHandler)object).currentOrientation == 0) {
                    n3 = 1;
                } else if (((DoorHandler)object).originalOrientation == 1 && ((DoorHandler)object).currentOrientation == 1) {
                    n2 = 1;
                } else if (((DoorHandler)object).originalOrientation == 2 && ((DoorHandler)object).currentOrientation == 2) {
                    n3 = -1;
                } else if (((DoorHandler)object).originalOrientation == 3 && ((DoorHandler)object).currentOrientation == 3) {
                    n2 = -1;
                }
            }
        } else if (((DoorHandler)object).objectType == 9) {
            if (!((DoorHandler)object).initiallyOpen) {
                if (((DoorHandler)object).originalOrientation == 0 && ((DoorHandler)object).currentOrientation == 0) {
                    n2 = 1;
                } else if (((DoorHandler)object).originalOrientation == 1 && ((DoorHandler)object).currentOrientation == 1) {
                    n2 = 1;
                } else if (((DoorHandler)object).originalOrientation == 2 && ((DoorHandler)object).currentOrientation == 2) {
                    n2 = -1;
                } else if (((DoorHandler)object).originalOrientation == 3 && ((DoorHandler)object).currentOrientation == 3) {
                    n2 = -1;
                }
            } else if (((DoorHandler)object).initiallyOpen) {
                if (((DoorHandler)object).originalOrientation == 0 && ((DoorHandler)object).currentOrientation == 0) {
                    n2 = 1;
                } else if (((DoorHandler)object).originalOrientation == 1 && ((DoorHandler)object).currentOrientation == 1) {
                    n2 = 1;
                } else if (((DoorHandler)object).originalOrientation == 2 && ((DoorHandler)object).currentOrientation == 2) {
                    n2 = -1;
                } else if (((DoorHandler)object).originalOrientation == 3 && ((DoorHandler)object).currentOrientation == 3) {
                    n2 = -1;
                }
            }
        }
        if (n2 != 0 || n3 != 0) {
            ObjectManager.getInstance().removeDynamicObjectAt(((DoorHandler)object).currentX, ((DoorHandler)object).currentY, ((DoorHandler)object).plane, 0);
            new DynamicObject(ServerSettings.placeholderObjectId, ((DoorHandler)object).currentX, ((DoorHandler)object).currentY, ((DoorHandler)object).plane, 0, ((DoorHandler)object).objectType, ServerSettings.placeholderObjectId, 999999999);
        }
        if (((DoorHandler)object).currentX == ((DoorHandler)object).originalX && ((DoorHandler)object).currentY == ((DoorHandler)object).originalY) {
            ((DoorHandler)object).currentX += n2;
            ((DoorHandler)object).currentY += n3;
        } else {
            ObjectManager.getInstance().removeDynamicObjectAt(((DoorHandler)object).currentX, ((DoorHandler)object).currentY, ((DoorHandler)object).plane, 0);
            new DynamicObject(ServerSettings.placeholderObjectId, ((DoorHandler)object).currentX, ((DoorHandler)object).currentY, ((DoorHandler)object).plane, 0, ((DoorHandler)object).objectType, ServerSettings.placeholderObjectId, 999999999);
            ((DoorHandler)object).currentX = ((DoorHandler)object).originalX;
            ((DoorHandler)object).currentY = ((DoorHandler)object).originalY;
        }
        if (n == 22 || n == 2550 || n == 2551 || n == 2555 || n == 2556 || n == 2558 || n == 2557 || n == 3014) {
            ((DoorHandler)object).currentObjectId = n;
        } else if (((DoorHandler)object).currentObjectId == ((DoorHandler)object).originalObjectId) {
            if (!((DoorHandler)object).initiallyOpen) {
                ++((DoorHandler)object).currentObjectId;
            } else if (((DoorHandler)object).initiallyOpen) {
                --((DoorHandler)object).currentObjectId;
            }
        } else if (((DoorHandler)object).currentObjectId != ((DoorHandler)object).originalObjectId) {
            if (!((DoorHandler)object).initiallyOpen) {
                --((DoorHandler)object).currentObjectId;
            } else if (((DoorHandler)object).initiallyOpen) {
                ++((DoorHandler)object).currentObjectId;
            }
        }
        Object object4 = object;
        n2 = ((DoorHandler)object4).originalOrientation;
        if (((DoorHandler)object4).objectType == 0) {
            if (!((DoorHandler)object4).initiallyOpen) {
                if (((DoorHandler)object4).originalOrientation == 0 && ((DoorHandler)object4).currentOrientation == 0) {
                    n2 = 1;
                } else if (((DoorHandler)object4).originalOrientation == 1 && ((DoorHandler)object4).currentOrientation == 1) {
                    n2 = 2;
                } else if (((DoorHandler)object4).originalOrientation == 2 && ((DoorHandler)object4).currentOrientation == 2) {
                    n2 = 3;
                } else if (((DoorHandler)object4).originalOrientation == 3 && ((DoorHandler)object4).currentOrientation == 3) {
                    n2 = 0;
                } else if (((DoorHandler)object4).originalOrientation != ((DoorHandler)object4).currentOrientation) {
                    n2 = ((DoorHandler)object4).originalOrientation;
                }
            } else if (((DoorHandler)object4).initiallyOpen) {
                if (((DoorHandler)object4).originalOrientation == 0 && ((DoorHandler)object4).currentOrientation == 0) {
                    n2 = 3;
                } else if (((DoorHandler)object4).originalOrientation == 1 && ((DoorHandler)object4).currentOrientation == 1) {
                    n2 = 0;
                } else if (((DoorHandler)object4).originalOrientation == 2 && ((DoorHandler)object4).currentOrientation == 2) {
                    n2 = 1;
                } else if (((DoorHandler)object4).originalOrientation == 3 && ((DoorHandler)object4).currentOrientation == 3) {
                    n2 = 2;
                } else if (((DoorHandler)object4).originalOrientation != ((DoorHandler)object4).currentOrientation) {
                    n2 = ((DoorHandler)object4).originalOrientation;
                }
            }
        } else if (((DoorHandler)object4).objectType == 9) {
            if (!((DoorHandler)object4).initiallyOpen) {
                if (((DoorHandler)object4).originalOrientation == 0 && ((DoorHandler)object4).currentOrientation == 0) {
                    n2 = 3;
                } else if (((DoorHandler)object4).originalOrientation == 1 && ((DoorHandler)object4).currentOrientation == 1) {
                    n2 = 2;
                } else if (((DoorHandler)object4).originalOrientation == 2 && ((DoorHandler)object4).currentOrientation == 2) {
                    n2 = 1;
                } else if (((DoorHandler)object4).originalOrientation == 3 && ((DoorHandler)object4).currentOrientation == 3) {
                    n2 = 0;
                } else if (((DoorHandler)object4).originalOrientation != ((DoorHandler)object4).currentOrientation) {
                    n2 = ((DoorHandler)object4).originalOrientation;
                }
            } else if (((DoorHandler)object4).initiallyOpen) {
                if (((DoorHandler)object4).originalOrientation == 0 && ((DoorHandler)object4).currentOrientation == 0) {
                    n2 = 3;
                } else if (((DoorHandler)object4).originalOrientation == 1 && ((DoorHandler)object4).currentOrientation == 1) {
                    n2 = 0;
                } else if (((DoorHandler)object4).originalOrientation == 2 && ((DoorHandler)object4).currentOrientation == 2) {
                    n2 = 1;
                } else if (((DoorHandler)object4).originalOrientation == 3 && ((DoorHandler)object4).currentOrientation == 3) {
                    n2 = 2;
                } else if (((DoorHandler)object4).originalOrientation != ((DoorHandler)object4).currentOrientation) {
                    n2 = ((DoorHandler)object4).originalOrientation;
                }
            }
        }
        ((DoorHandler)object4).currentOrientation = n2;
        int n5 = n2;
        ObjectManager.getInstance().removeDynamicObjectAt(((DoorHandler)object).currentX, ((DoorHandler)object).currentY, ((DoorHandler)object).plane, 0);
        new DynamicObject(((DoorHandler)object).currentObjectId, ((DoorHandler)object).currentX, ((DoorHandler)object).currentY, ((DoorHandler)object).plane, n5, ((DoorHandler)object).objectType, ServerSettings.placeholderObjectId, 999999);
        boolean bl = ((DoorHandler)object).open = !((DoorHandler)object).open;
        ((DoorHandler)object).rapidToggleCount = System.currentTimeMillis() - ((DoorHandler)object).lastInteractionMillis < 1000L ? ++((DoorHandler)object).rapidToggleCount : 1;
        object3 = player;
        ((Player)object3).packetSender.sendSoundEffect(318, 1, 0);
        ((DoorHandler)object).lastInteractionMillis = System.currentTimeMillis();
        return true;
    }

    public static void handleDoorMovement(Player player, int n, int n2, int n3, int n4, int n5, int n6) {
        DoorHandler doorHandler = DoorHandler.getOrCreateDoorState(n, n2, n3, n4);
        if (doorHandler == null) {
            player.setActionLocked(false);
            return;
        }
        DoorHandler.handleDoor(player, n, n2, n3, n4);
        DoorHandler doorHandler2 = doorHandler;
        Player player2 = player;
        player2.packetSender.queueRelativeMovementStep(n5, n6, false);
        CycleEventHandler.getInstance().schedule(player, new DoorResetEvent(player, doorHandler2), 2);
    }

    private static boolean isInitiallyOpenObjectId(int n) {
        int n2 = 0;
        while (n2 < initiallyOpenObjectIds.length) {
            if (initiallyOpenObjectIds[n2] == n) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    static /* synthetic */ int getCurrentObjectId(DoorHandler doorHandler) {
        return doorHandler.currentObjectId;
    }

    static /* synthetic */ int getCurrentX(DoorHandler doorHandler) {
        return doorHandler.currentX;
    }

    static /* synthetic */ int getCurrentY(DoorHandler doorHandler) {
        return doorHandler.currentY;
    }

    static /* synthetic */ int getPlane(DoorHandler doorHandler) {
        return doorHandler.plane;
    }
}

