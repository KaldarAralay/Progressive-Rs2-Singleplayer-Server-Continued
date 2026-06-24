/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.objects;

import com.rs2.cache.CacheStore;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.WorldObjectRegionIndex;
import com.rs2.model.skill.agility.AgilityObstacleHandler;
import java.util.logging.Logger;

public class WorldObjectLookup {
    static {
        Logger.getLogger(WorldObjectLookup.class.getName());
    }

    public static void loadWorldObjects() {
        Object object = CacheStore.getInstance();
        try {
            ((CacheStore)object).getDefinitionIndex().getObjectDefinitionEntries();
            GameplayHelper.loadObjectDefinitions();
            return;
        }
        catch (Exception exception) {
            object = exception;
            exception.printStackTrace();
            return;
        }
    }

    public static LoadedWorldObject findObjectAt(int n, int n2, int n3) {
        Position position = new Position(n, n2, n3);
        World.getInstance().getObjectRegionIndex();
        AgilityObstacleHandler bucket = WorldObjectRegionIndex.getOrCreateRegionBucket(position);
        for (Object loadedObject : bucket.getLoadedObjects()) {
            LoadedWorldObject loadedWorldObject = (LoadedWorldObject)loadedObject;
            if (!loadedWorldObject.getPosition().equals(position)) continue;
            ObjectDefinition objectDefinition = ObjectDefinition.forId(loadedWorldObject.getWorldObject().getObjectId());
            if (loadedWorldObject.getType() == 0 && !objectDefinition.interactive || loadedWorldObject.getType() == 3 && !objectDefinition.interactive || loadedWorldObject.getType() == 4 && !objectDefinition.interactive || loadedWorldObject.getType() == 22 && !objectDefinition.interactive) continue;
            return loadedWorldObject;
        }
        return null;
    }

    public static LoadedWorldObject findObjectByIdAt(int n, int n2, int n3, int n4) {
        Position position = new Position(n2, n3, n4);
        World.getInstance().getObjectRegionIndex();
        AgilityObstacleHandler bucket = WorldObjectRegionIndex.getOrCreateRegionBucket(position);
        for (Object loadedObject : bucket.getLoadedObjects()) {
            LoadedWorldObject loadedWorldObject = (LoadedWorldObject)loadedObject;
            if (loadedWorldObject.getWorldObject().getObjectId() != n || !loadedWorldObject.getPosition().equals(position)) continue;
            return loadedWorldObject;
        }
        return null;
    }

    public static LoadedWorldObject findObjectByNameAt(String string, int n, int n2, int n3) {
        Position position = new Position(n, n2, n3);
        World.getInstance().getObjectRegionIndex();
        AgilityObstacleHandler bucket = WorldObjectRegionIndex.getOrCreateRegionBucket(position);
        for (Object loadedObject : bucket.getLoadedObjects()) {
            LoadedWorldObject loadedWorldObject = (LoadedWorldObject)loadedObject;
            String objectName;
            if (ObjectDefinition.forId(loadedWorldObject.getWorldObject().getObjectId()) != null) {
                ObjectDefinition objectDefinition = ObjectDefinition.forId(loadedWorldObject.getWorldObject().getObjectId());
                objectName = objectDefinition.name.toLowerCase();
            } else {
                objectName = "";
            }
            if (!objectName.contains(string.toLowerCase()) || !loadedWorldObject.getPosition().equals(position)) continue;
            return loadedWorldObject;
        }
        return null;
    }

}

