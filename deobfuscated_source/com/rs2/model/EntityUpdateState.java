/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.animation.GraphicEffect;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.OrbChargeTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.update.EntityUpdateOverrideDefinition;
import com.rs2.model.update.EntityUpdateOverrideType;

public class EntityUpdateState {
    private boolean updateRequired;
    private boolean appearanceUpdateRequired;
    private boolean forcedTextUpdateRequired;
    private String forcedText;
    private boolean graphicUpdateRequired;
    private int graphicId;
    private int graphicDelay;
    private boolean animationUpdateRequired;
    private int animationId;
    private int animationDelay;
    private boolean faceEntityUpdateRequired;
    private int faceEntityId = -1;
    private boolean facePositionUpdateRequired;
    private Position facePosition;
    private boolean primaryHitUpdateRequired;
    private boolean secondaryHitUpdateRequired;
    private int queuedPrimaryHitDamage = -1;
    private int queuedPrimaryHitType;
    private int queuedSecondaryHitDamage = -1;
    private int queuedSecondaryHitType;
    private boolean forcedMovementUpdateRequired;
    private int primaryHitDamage;
    private int secondaryHitDamage;
    private int primaryHitType;
    private int secondaryHitType;
    private int forcedMovementStartDelay;
    private int forcedMovementEndDelay;
    private int forcedMovementDirection;
    private boolean primaryHitDamageOverridden;
    private boolean secondaryHitDamageOverridden;
    private Entity entity;
    private int forcedMovementEndXOffset;
    private int forcedMovementEndYOffset;

    public static boolean handleOrbChargeButton(Player player, int n, int n2) {
        if (n == 2799) {
            n2 = 1;
        }
        if (n == 2798) {
            n2 = 5;
        }
        if (n == 1747) {
            n2 = player.getInventoryManager().getItemAmount(567);
        }
        if (n2 <= 0) {
            return false;
        }
        if (n2 >= 28) {
            n2 = 28;
        }
        n = n2 - 1;
        Player player2 = player;
        if (player2.interfaceAction == "orb charge") {
            player2 = player;
            player2.packetSender.closeInterfaces();
            player.B.tryStartCast();
            player.setActiveCycleEvent(new OrbChargeTask(n, player));
            CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 6);
            return true;
        }
        return false;
    }

    public EntityUpdateState(Entity entity) {
        this.entity = entity;
    }

    public void setForcedMovement(Player player, int n, int n2, int n3, int n4, int n5) {
        player.getPosition();
        Position.updateLocalX(player);
        player.getPosition();
        Position.updateLocalY(player);
        player.getPosition();
        Position.updateLocalX(player);
        player.getPosition();
        Position.updateLocalY(player);
        this.forcedMovementEndXOffset = n;
        this.forcedMovementEndYOffset = n2;
        this.forcedMovementStartDelay = n3;
        this.forcedMovementEndDelay = n4;
        this.forcedMovementDirection = n5;
        this.forcedMovementUpdateRequired = true;
        this.updateRequired = true;
    }

    public void clearForcedMovement() {
        this.forcedMovementDirection = 0;
        this.forcedMovementEndYOffset = 0;
        this.forcedMovementEndXOffset = 0;
        this.forcedMovementEndDelay = 0;
        this.forcedMovementStartDelay = 0;
    }

    public void setGraphic(GraphicEffect graphicEffect) {
        this.setGraphic(graphicEffect.getId(), graphicEffect.getPackedDelay());
    }

    public void setGraphic(int n) {
        this.graphicId = n;
        this.graphicDelay = 0;
        this.graphicUpdateRequired = true;
        this.updateRequired = true;
    }

    public void setGraphic(int n, int n2) {
        EntityUpdateOverrideDefinition entityUpdateOverrideDefinition;
        if (this.entity.isPlayer() && (entityUpdateOverrideDefinition = EntityUpdateOverrideDefinition.forOriginalIdAndType(n, EntityUpdateOverrideType.b)) != null) {
            Player player = (Player)this.entity;
            player.packetSender.sendSoundEffect(entityUpdateOverrideDefinition.getReplacementId(), 1, 0);
        }
        this.graphicId = n;
        this.graphicDelay = n2;
        this.graphicUpdateRequired = true;
        this.updateRequired = true;
    }

    public void setGraphicHeight100(int n) {
        EntityUpdateOverrideDefinition entityUpdateOverrideDefinition;
        if (this.entity.isPlayer() && (entityUpdateOverrideDefinition = EntityUpdateOverrideDefinition.forOriginalIdAndType(n, EntityUpdateOverrideType.b)) != null) {
            Player player = (Player)this.entity;
            player.packetSender.sendSoundEffect(entityUpdateOverrideDefinition.getReplacementId(), 1, 0);
        }
        this.graphicId = n;
        this.graphicDelay = 0x640000;
        this.graphicUpdateRequired = true;
        this.updateRequired = true;
    }

    public void setAnimation(int n) {
        this.animationId = n;
        this.animationDelay = 0;
        this.animationUpdateRequired = true;
        this.updateRequired = true;
    }

    public void setAnimation(int n, int n2) {
        this.animationId = n;
        this.animationDelay = 0;
        this.animationUpdateRequired = true;
        this.updateRequired = true;
    }

    public void setFaceEntity(int n) {
        this.faceEntityId = n;
        this.faceEntityUpdateRequired = true;
        this.updateRequired = true;
    }

    public void setFacePosition(Position position) {
        this.facePosition = position;
        this.facePositionUpdateRequired = true;
        this.updateRequired = true;
    }

    public void setForcedTextAndMarkUpdated(String string) {
        this.forcedText = string;
        this.forcedTextUpdateRequired = true;
        this.updateRequired = true;
    }

    public void reset() {
        this.forcedTextUpdateRequired = false;
        this.appearanceUpdateRequired = false;
        this.graphicUpdateRequired = false;
        this.animationUpdateRequired = false;
        this.faceEntityUpdateRequired = false;
        this.facePositionUpdateRequired = false;
        this.primaryHitDamage = this.queuedPrimaryHitDamage;
        this.secondaryHitDamage = this.queuedSecondaryHitDamage;
        this.primaryHitType = this.queuedPrimaryHitType;
        this.secondaryHitType = this.queuedSecondaryHitType;
        this.queuedPrimaryHitDamage = -1;
        this.queuedSecondaryHitDamage = -1;
        this.primaryHitUpdateRequired = this.primaryHitDamage != -1;
        this.secondaryHitUpdateRequired = this.secondaryHitDamage != -1;
        this.primaryHitDamageOverridden = false;
        this.secondaryHitDamageOverridden = false;
        this.forcedMovementUpdateRequired = false;
    }

    public void setUpdateRequired(boolean bl) {
        this.updateRequired = true;
    }

    public boolean isUpdateRequired() {
        return this.updateRequired;
    }

    public void setAppearanceUpdateRequired(boolean bl) {
        this.appearanceUpdateRequired = bl;
    }

    public boolean isAppearanceUpdateRequired() {
        return this.appearanceUpdateRequired;
    }

    public void setForcedTextUpdateRequired(boolean bl) {
        this.forcedTextUpdateRequired = true;
    }

    public boolean isForcedTextUpdateRequired() {
        return this.forcedTextUpdateRequired;
    }

    public void setForcedText(String string) {
        this.forcedText = string;
        this.setForcedTextUpdateRequired(true);
    }

    public String getForcedText() {
        return this.forcedText;
    }

    public boolean isGraphicUpdateRequired() {
        return this.graphicUpdateRequired;
    }

    public int getGraphicId() {
        return this.graphicId;
    }

    public int getGraphicDelay() {
        return this.graphicDelay;
    }

    public boolean isAnimationUpdateRequired() {
        return this.animationUpdateRequired;
    }

    public int getAnimationId() {
        return this.animationId;
    }

    public int getAnimationDelay() {
        return this.animationDelay;
    }

    public boolean isFaceEntityUpdateRequired() {
        return this.faceEntityUpdateRequired;
    }

    public void setFaceEntityId(int n) {
        this.faceEntityId = n;
    }

    public int getFaceEntityId() {
        return this.faceEntityId;
    }

    public boolean isFacePositionUpdateRequired() {
        return this.facePositionUpdateRequired;
    }

    public void setFacePositionValue(Position position) {
        this.facePosition = position;
    }

    public Position getFacePosition() {
        return this.facePosition;
    }

    public void setPrimaryHitUpdateRequired(boolean bl) {
        this.primaryHitUpdateRequired = true;
    }

    public boolean isPrimaryHitUpdateRequired() {
        return this.primaryHitUpdateRequired;
    }

    public void setSecondaryHitUpdateRequired(boolean bl) {
        this.secondaryHitUpdateRequired = true;
    }

    public boolean isSecondaryHitUpdateRequired() {
        return this.secondaryHitUpdateRequired;
    }

    public void setPrimaryHitDamage(int n) {
        this.primaryHitDamage = n;
        this.primaryHitDamageOverridden = true;
    }

    public int getPrimaryHitDamage() {
        return this.primaryHitDamage;
    }

    public void setSecondaryHitDamage(int n) {
        this.secondaryHitDamage = n;
        this.secondaryHitDamageOverridden = true;
    }

    public int getSecondaryHitDamage() {
        return this.secondaryHitDamage;
    }

    public void setPrimaryHitType(int n) {
        this.primaryHitType = n;
    }

    public int getPrimaryHitType() {
        return this.primaryHitType;
    }

    public void setSecondaryHitType(int n) {
        this.secondaryHitType = n;
    }

    public int getSecondaryHitType() {
        return this.secondaryHitType;
    }

    public boolean isForcedMovementUpdateRequired() {
        return this.forcedMovementUpdateRequired;
    }

    public void setForcedMovementUpdateRequired(boolean bl) {
        this.forcedMovementUpdateRequired = false;
    }

    public static int getLocalXForUpdate(Player player) {
        player.getPosition();
        return Position.updateLocalX(player);
    }

    public static int getLocalYForUpdate(Player player) {
        player.getPosition();
        return Position.updateLocalY(player);
    }

    public int getForcedMovementEndXOffset() {
        return this.forcedMovementEndXOffset;
    }

    public int getForcedMovementEndYOffset() {
        return this.forcedMovementEndYOffset;
    }

    public int getForcedMovementStartDelay() {
        return this.forcedMovementStartDelay;
    }

    public int getForcedMovementEndDelay() {
        return this.forcedMovementEndDelay;
    }

    public int getForcedMovementDirection() {
        return this.forcedMovementDirection;
    }

    public boolean isPrimaryHitDamageOverridden() {
        return this.primaryHitDamageOverridden;
    }

    public boolean isSecondaryHitDamageOverridden() {
        return this.secondaryHitDamageOverridden;
    }

    public void queueHit(int n, int n2) {
        if (this.queuedPrimaryHitDamage == -1) {
            this.queuedPrimaryHitDamage = n;
            this.queuedPrimaryHitType = n2;
            return;
        }
        if (this.queuedSecondaryHitDamage == -1) {
            this.queuedSecondaryHitDamage = n;
            this.queuedSecondaryHitType = n2;
        }
    }
}

