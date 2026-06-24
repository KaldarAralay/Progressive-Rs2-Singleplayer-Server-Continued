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
import com.rs2.model.skill.SkillActionHelper;
import java.util.ArrayList;
import java.util.List;

public final class DoubleDoorHandler {
    private static List doorStates = new ArrayList();
    private int currentObjectId;
    private int originalObjectId;
    private boolean initiallyOpen;
    private int currentX;
    private int currentY;
    private int plane;
    private int originalX;
    private int originalY;
    private int currentOrientation;
    private int originalOrientation;
    private static int[] initiallyOpenObjectIds = new int[]{1520, 1517, 11625, 11624};
    private static int[] wideOffsetObjectIds = new int[]{1551, 1553, 1598, 1599, 2261, 2262, 2438, 2439, 2050, 2051, 3015, 3016, 3198, 3197, 3725, 3726, 7049, 7050, 8810, 8811, 12816, 12817, 12986, 12987};

    private static DoubleDoorHandler getOrCreateDoubleDoorState(int n, int n2, int n3) {
        Object object2;
        for (Object doorStateObject : doorStates) {
            DoubleDoorHandler doubleDoorHandler = (DoubleDoorHandler)doorStateObject;
            if (doubleDoorHandler.currentX != n || doubleDoorHandler.currentY != n2 || doubleDoorHandler.plane != n3) continue;
            return doubleDoorHandler;
        }
        object2 = WorldObjectLookup.findObjectByNameAt("door", n, n2, n3);
        if (object2 == null && (object2 = WorldObjectLookup.findObjectByNameAt("gate", n, n2, n3)) == null && (object2 = WorldObjectLookup.findObjectByNameAt("fence", n, n2, n3)) == null) {
            return null;
        }
        DoubleDoorHandler doubleDoorHandler = new DoubleDoorHandler(((LoadedWorldObject)object2).getWorldObject().getObjectId(), n, n2, n3);
        doorStates.add(doubleDoorHandler);
        return doubleDoorHandler;
    }

    public static boolean hasDoubleDoorAt(int n, int n2, int n3) {
        return DoubleDoorHandler.getOrCreateDoubleDoorState(n, n2, n3) != null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean handleDoubleDoor(int n, int n2, int n3, int n4) {
        Object object2;
        ObjectDefinition objectDefinition = ObjectDefinition.forId(n);
        String string = objectDefinition != null && objectDefinition.name != null ? objectDefinition.name.toLowerCase() : "";
        if (!string.contains("fence") && !string.contains("gate") && !string.contains("door") || string.contains("trapdoor") || string.contains("tree")) {
            return false;
        }
        if (n == 2882 || n == 2883 || n == 1589 || n == 1590 || n == 2155 && n2 == 2592 && n3 == 9490 || n == 2154 && n2 == 2593 && n3 == 9490) {
            return false;
        }
        object2 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3, n4);
        if (object2 == null) {
            return false;
        }
        if (!((DoubleDoorHandler)object2).initiallyOpen) {
            if (((DoubleDoorHandler)object2).originalOrientation == 0) {
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 - 1, n4);
                DoubleDoorHandler doubleDoorHandler2 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 + 1, n4);
                DoubleDoorHandler doubleDoorHandler3 = null;
                DoubleDoorHandler doubleDoorHandler4 = null;
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(n)) {
                    doubleDoorHandler3 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 - 1, n3, n4);
                    doubleDoorHandler4 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 + 1, n3, n4);
                }
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else if (doubleDoorHandler2 != null) {
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler2, false);
                    return true;
                } else if (doubleDoorHandler3 != null) {
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler3, false);
                    return true;
                } else {
                    if (doubleDoorHandler4 == null) return false;
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler4, false);
                }
                return true;
            } else if (((DoubleDoorHandler)object2).originalOrientation == 1) {
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 - 1, n3, n4);
                DoubleDoorHandler doubleDoorHandler5 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 + 1, n3, n4);
                DoubleDoorHandler doubleDoorHandler6 = null;
                DoubleDoorHandler doubleDoorHandler7 = null;
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(n)) {
                    doubleDoorHandler6 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 - 1, n4);
                    doubleDoorHandler7 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 + 1, n4);
                }
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else if (doubleDoorHandler5 != null) {
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler5, false);
                    return true;
                } else if (doubleDoorHandler7 != null) {
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler7, false);
                    return true;
                } else {
                    if (doubleDoorHandler6 == null) return false;
                    DoubleDoorHandler.a(doubleDoorHandler6, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                }
                return true;
            } else if (((DoubleDoorHandler)object2).originalOrientation == 2) {
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 + 1, n4);
                DoubleDoorHandler doubleDoorHandler8 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 - 1, n4);
                DoubleDoorHandler doubleDoorHandler9 = null;
                DoubleDoorHandler doubleDoorHandler10 = null;
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(n)) {
                    doubleDoorHandler9 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 - 1, n3, n4);
                    doubleDoorHandler10 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 + 1, n3, n4);
                }
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else if (doubleDoorHandler8 != null) {
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler8, false);
                    return true;
                } else if (doubleDoorHandler10 != null) {
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler10, false);
                    return true;
                } else {
                    if (doubleDoorHandler9 == null) return false;
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler9, false);
                }
                return true;
            } else {
                if (((DoubleDoorHandler)object2).originalOrientation != 3) return true;
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 - 1, n3, n4);
                DoubleDoorHandler doubleDoorHandler11 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 + 1, n3, n4);
                DoubleDoorHandler doubleDoorHandler12 = null;
                DoubleDoorHandler doubleDoorHandler13 = null;
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(n)) {
                    doubleDoorHandler12 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 - 1, n4);
                    doubleDoorHandler13 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 + 1, n4);
                }
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else if (doubleDoorHandler11 != null) {
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler11, false);
                    return true;
                } else if (doubleDoorHandler12 != null) {
                    DoubleDoorHandler.a(doubleDoorHandler12, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else {
                    if (doubleDoorHandler13 == null) return false;
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler13, false);
                }
            }
            return true;
        } else {
            if (!((DoubleDoorHandler)object2).initiallyOpen) return true;
            if (((DoubleDoorHandler)object2).originalOrientation == 0) {
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 - 1, n3, n4);
                DoubleDoorHandler doubleDoorHandler14 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 + 1, n3, n4);
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else {
                    if (doubleDoorHandler14 == null) return false;
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler14, false);
                }
                return true;
            } else if (((DoubleDoorHandler)object2).originalOrientation == 1) {
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 + 1, n4);
                DoubleDoorHandler doubleDoorHandler15 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 - 1, n4);
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else {
                    if (doubleDoorHandler15 == null) return false;
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler15, false);
                }
                return true;
            } else if (((DoubleDoorHandler)object2).originalOrientation == 2) {
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2 - 1, n3, n4);
                DoubleDoorHandler doubleDoorHandler16 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 - 1, n4);
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else {
                    if (doubleDoorHandler16 == null) return false;
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler16, false);
                }
                return true;
            } else {
                if (((DoubleDoorHandler)object2).originalOrientation != 3) return true;
                DoubleDoorHandler doubleDoorHandler = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 + 1, n4);
                DoubleDoorHandler doubleDoorHandler17 = DoubleDoorHandler.getOrCreateDoubleDoorState(n2, n3 - 1, n4);
                if (doubleDoorHandler != null) {
                    DoubleDoorHandler.a(doubleDoorHandler, false);
                    DoubleDoorHandler.b((DoubleDoorHandler)object2, false);
                    return true;
                } else {
                    if (doubleDoorHandler17 == null) return false;
                    DoubleDoorHandler.a((DoubleDoorHandler)object2, false);
                    DoubleDoorHandler.b(doubleDoorHandler17, false);
                }
            }
        }
        return true;
    }

    /*
     * WARNING - void declaration
     */
    private static void a(DoubleDoorHandler doubleDoorHandler, boolean bl) {
        int var1_20 = doubleDoorHandler.currentOrientation;
        int var1_8 = 0;
        int n = 0;
        ObjectManager.getInstance();
        Object object = ObjectManager.findDynamicObjectByIdAt(doubleDoorHandler.currentObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane);
        if (object != null) {
            ObjectManager.getInstance();
            ObjectManager.removeWallObjectCollision(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, ((DynamicObject)object).getWorldObject().getOrientation());
        }
        if ((object = WorldObjectLookup.findObjectByIdAt(doubleDoorHandler.currentObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane)) != null) {
            ObjectManager.getInstance();
            ObjectManager.removeWallObjectCollision(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, ((LoadedWorldObject)object).getOrientation());
        }
        if (!doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                var1_8 = -1;
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                n = 1;
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                var1_8 = 1;
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                n = -1;
            }
        } else if (doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                n = -1;
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                var1_8 = -1;
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                var1_8 = -1;
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                n = -1;
            }
        }
        if (var1_8 != 0 || n != 0) {
            ObjectManager.getInstance().removeDynamicObjectAt(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0);
            new DynamicObject(ServerSettings.placeholderObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0, 0, ServerSettings.placeholderObjectId, 999999999);
        }
        if (doubleDoorHandler.currentX == doubleDoorHandler.originalX && doubleDoorHandler.currentY == doubleDoorHandler.originalY) {
            doubleDoorHandler.currentX += var1_8;
            doubleDoorHandler.currentY += n;
        } else {
            ObjectManager.getInstance().removeDynamicObjectAt(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0);
            new DynamicObject(ServerSettings.placeholderObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0, 0, ServerSettings.placeholderObjectId, 999999999);
            doubleDoorHandler.currentX = doubleDoorHandler.originalX;
            doubleDoorHandler.currentY = doubleDoorHandler.originalY;
        }
        if (doubleDoorHandler.currentObjectId != doubleDoorHandler.originalObjectId) {
            if (!doubleDoorHandler.initiallyOpen) {
                doubleDoorHandler.currentObjectId = doubleDoorHandler.originalObjectId;
            } else if (doubleDoorHandler.initiallyOpen) {
                doubleDoorHandler.currentObjectId = doubleDoorHandler.originalObjectId;
            }
        }
        ObjectManager.getInstance().removeDynamicObjectAt(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0);
        int n6 = doubleDoorHandler.currentObjectId;
        int n7 = doubleDoorHandler.currentX;
        int n8 = doubleDoorHandler.currentY;
        int n9 = doubleDoorHandler.plane;
        int n10 = doubleDoorHandler.originalOrientation;
        if (!doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                var1_20 = 3;
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                var1_20 = 0;
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                var1_20 = 1;
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                var1_20 = 0;
            } else if (doubleDoorHandler.originalOrientation != doubleDoorHandler.currentOrientation) {
                var1_20 = doubleDoorHandler.originalOrientation;
            }
        } else if (doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                var1_20 = 1;
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                var1_20 = 2;
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                var1_20 = 1;
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                var1_20 = 2;
            } else if (doubleDoorHandler.originalOrientation != doubleDoorHandler.currentOrientation) {
                var1_20 = doubleDoorHandler.originalOrientation;
            }
        }
        doubleDoorHandler.currentOrientation = var1_20;
        new DynamicObject(n6, n7, n8, n9, (int)var1_20, 0, ServerSettings.placeholderObjectId, 999999999);
    }

    private static void b(DoubleDoorHandler doubleDoorHandler, boolean bl) {
        int n = 0;
        int n2 = 0;
        ObjectManager.getInstance();
        Object object = ObjectManager.findDynamicObjectByIdAt(doubleDoorHandler.currentObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane);
        if (object != null) {
            ObjectManager.getInstance();
            ObjectManager.removeWallObjectCollision(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, ((DynamicObject)object).getWorldObject().getOrientation());
        }
        if ((object = WorldObjectLookup.findObjectByIdAt(doubleDoorHandler.currentObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane)) != null) {
            ObjectManager.getInstance();
            ObjectManager.removeWallObjectCollision(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, ((LoadedWorldObject)object).getOrientation());
        }
        if (!doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId)) {
                    n = -2;
                    n2 = -1;
                } else {
                    n = -1;
                }
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId)) {
                    n = -1;
                    n2 = 2;
                } else {
                    n2 = 1;
                }
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId)) {
                    n = 2;
                    n2 = 1;
                } else {
                    n = 1;
                }
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                if (DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId)) {
                    n = -1;
                    n2 = -2;
                } else {
                    n2 = -1;
                }
            }
        } else if (doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                n = 1;
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                n = -1;
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                n2 = -1;
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                n = -1;
            }
        }
        if (n != 0 || n2 != 0) {
            ObjectManager.getInstance().removeDynamicObjectAt(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0);
            new DynamicObject(ServerSettings.placeholderObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0, 0, ServerSettings.placeholderObjectId, 999999999);
        }
        if (doubleDoorHandler.currentX == doubleDoorHandler.originalX && doubleDoorHandler.currentY == doubleDoorHandler.originalY) {
            doubleDoorHandler.currentX += n;
            doubleDoorHandler.currentY += n2;
        } else {
            ObjectManager.getInstance().removeDynamicObjectAt(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0);
            new DynamicObject(ServerSettings.placeholderObjectId, doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0, 0, ServerSettings.placeholderObjectId, 999999999);
            doubleDoorHandler.currentX = doubleDoorHandler.originalX;
            doubleDoorHandler.currentY = doubleDoorHandler.originalY;
        }
        ObjectManager.getInstance().removeDynamicObjectAt(doubleDoorHandler.currentX, doubleDoorHandler.currentY, doubleDoorHandler.plane, 0);
        int n3 = doubleDoorHandler.currentObjectId;
        int n4 = doubleDoorHandler.currentX;
        int n5 = doubleDoorHandler.currentY;
        int n6 = doubleDoorHandler.plane;
        n = doubleDoorHandler.originalOrientation;
        if (!doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                n = DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId) ? 3 : 1;
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                n = DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId) ? 0 : 2;
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                n = DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId) ? 1 : 3;
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                n = DoubleDoorHandler.usesWideDoubleDoorOffset(doubleDoorHandler.currentObjectId) ? 0 : 2;
            } else if (doubleDoorHandler.originalOrientation != doubleDoorHandler.currentOrientation) {
                n = doubleDoorHandler.originalOrientation;
            }
        } else if (doubleDoorHandler.initiallyOpen) {
            if (doubleDoorHandler.originalOrientation == 0 && doubleDoorHandler.currentOrientation == 0) {
                n = 3;
            } else if (doubleDoorHandler.originalOrientation == 1 && doubleDoorHandler.currentOrientation == 1) {
                n = 0;
            } else if (doubleDoorHandler.originalOrientation == 2 && doubleDoorHandler.currentOrientation == 2) {
                n = 1;
            } else if (doubleDoorHandler.originalOrientation == 3 && doubleDoorHandler.currentOrientation == 3) {
                n = 2;
            } else if (doubleDoorHandler.originalOrientation != doubleDoorHandler.currentOrientation) {
                n = doubleDoorHandler.originalOrientation;
            }
        }
        doubleDoorHandler.currentOrientation = n;
        new DynamicObject(n3, n4, n5, n6, n, 0, ServerSettings.placeholderObjectId, 999999999);
    }

    private static boolean isInitiallyOpenObjectId(int n) {
        int n2 = 0;
        while (n2 < initiallyOpenObjectIds.length) {
            if (n == initiallyOpenObjectIds[n2] || n + 3 == initiallyOpenObjectIds[n2]) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public static boolean usesWideDoubleDoorOffset(int n) {
        int n2 = 0;
        while (n2 < wideOffsetObjectIds.length) {
            if (n == wideOffsetObjectIds[n2]) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    private DoubleDoorHandler(int n, int n2, int n3, int n4) {
        this.currentObjectId = n;
        this.originalObjectId = n;
        this.initiallyOpen = DoubleDoorHandler.isInitiallyOpenObjectId(n);
        this.currentX = n2;
        this.originalX = n2;
        this.currentY = n3;
        this.plane = n4;
        this.originalY = n3;
        this.originalOrientation = this.currentOrientation = SkillActionHelper.getObjectOrientation(n, n2, n3, n4);
    }
}

