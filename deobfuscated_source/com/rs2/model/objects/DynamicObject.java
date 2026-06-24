/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects;

import com.rs2.ServerSettings;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.player.Player;

public final class DynamicObject {
    private WorldObject worldObject;
    public int orientation;
    public int restoreObjectId;
    public int remainingTicks;
    public boolean updatesCollision;
    public Player owner;

    public DynamicObject(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        if (!GameplayHelper.isObjectDefinitionIdValid(n)) {
            n = ServerSettings.placeholderObjectId;
        }
        if (!GameplayHelper.isObjectDefinitionIdValid(n7)) {
            n7 = ServerSettings.placeholderObjectId;
        }
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n2, n3, n4);
        if (dynamicObject != null && n == dynamicObject.worldObject.getObjectId()) {
            return;
        }
        this.worldObject = new WorldObject(n, n6, n5, new Position(n2, n3, n4));
        this.orientation = this.worldObject.getOrientation();
        this.restoreObjectId = n7;
        this.remainingTicks = n8;
        this.updatesCollision = true;
        ObjectManager.getInstance().addDynamicObject(this, this.updatesCollision);
    }

    public DynamicObject(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, Player player) {
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n2, n3, n4);
        if (dynamicObject != null && n == dynamicObject.worldObject.getObjectId()) {
            return;
        }
        this.worldObject = new WorldObject(n, 10, -1, new Position(n2, n3, n4));
        this.orientation = this.worldObject.getOrientation();
        this.restoreObjectId = n7;
        this.remainingTicks = n8;
        this.updatesCollision = true;
        this.owner = player;
        ObjectManager.getInstance().addDynamicObject(this, this.updatesCollision);
    }

    public DynamicObject(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, boolean bl) {
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n2, n3, n4);
        if (dynamicObject != null && n == dynamicObject.worldObject.getObjectId()) {
            return;
        }
        this.worldObject = new WorldObject(n, n6, n5, new Position(n2, n3, n4));
        this.orientation = this.worldObject.getOrientation();
        this.restoreObjectId = n7;
        this.remainingTicks = n8;
        this.updatesCollision = bl;
        ObjectManager.getInstance().addDynamicObject(this, this.updatesCollision);
    }

    public DynamicObject(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, boolean bl) {
        ObjectManager.getInstance();
        DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(n2, n3, n4);
        if (dynamicObject != null && n == dynamicObject.worldObject.getObjectId()) {
            return;
        }
        this.worldObject = new WorldObject(n, n6, n5, new Position(n2, n3, n4));
        this.orientation = n9;
        this.restoreObjectId = n7;
        this.remainingTicks = n8;
        this.updatesCollision = false;
        ObjectManager.getInstance().addDynamicObject(this, this.updatesCollision);
    }

    public final WorldObject getWorldObject() {
        return this.worldObject;
    }
}

