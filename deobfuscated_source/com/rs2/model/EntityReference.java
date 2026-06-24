/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.model.Entity;
import com.rs2.model.World;
import com.rs2.model.player.Player;

public class EntityReference {
    private int referenceId;
    private boolean playerReference;
    private Entity cachedEntity;
    private String playerUsername;

    public EntityReference(Entity entity) {
        this.cachedEntity = entity;
        this.referenceId = entity.getReferenceId();
        this.playerReference = entity.isPlayer();
        if (entity.isPlayer()) {
            entity = (Player)entity;
            this.playerUsername = ((Player)entity).getUsername();
        }
    }

    public final Entity resolve() {
        if (this.cachedEntity == null || this.cachedEntity.getIndex() == -1) {
            this.cachedEntity = null;
            Entity[] entities = this.playerReference ? World.getPlayers() : World.getNpcs();
            int n = entities.length;
            int n2 = 0;
            while (n2 < n) {
                Entity entity = entities[n2];
                if (entity != null) {
                    Player player;
                    if (entity.isPlayer() && this.playerReference && (player = (Player)entity).getUsername().equals(this.playerUsername)) {
                        return entity;
                    }
                    if (!this.playerReference && entity.getReferenceId() == this.referenceId) {
                        this.cachedEntity = entity;
                        break;
                    }
                }
                ++n2;
            }
        }
        return this.cachedEntity;
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof EntityReference) && !(object instanceof Entity)) {
            return false;
        }
        if (object instanceof Entity) {
            if (((Entity)(object = (Entity)object)).isPlayer() && this.playerReference) {
                return ((Player)(object = (Player)object)).getUsername().equals(this.playerUsername);
            }
            if (this.cachedEntity == object) {
                return true;
            }
            if (((Entity)object).isPlayer() == this.playerReference && ((Entity)object).getReferenceId() == this.referenceId) {
                this.cachedEntity = (Entity)object;
                return true;
            }
            return false;
        }
        object = (EntityReference)object;
        if (((EntityReference)object).playerReference && this.playerReference) {
            return ((EntityReference)object).playerUsername.equals(this.playerUsername);
        }
        return ((EntityReference)object).referenceId == this.referenceId && ((EntityReference)object).playerReference == this.playerReference;
    }
}

