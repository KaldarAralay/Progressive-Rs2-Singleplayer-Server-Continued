/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

import com.rs2.model.combat.AmmunitionDefinition;
import com.rs2.model.combat.ProjectileTiming;

public enum AmmunitionProfile {
    ARROW(13, 90, ProjectileTiming.a, AmmunitionDefinition.ARROWS),
    OBBY_RING(3, 90, ProjectileTiming.a, new AmmunitionDefinition[]{AmmunitionDefinition.TOKTZ}),
    KNIFE(3, 90, ProjectileTiming.b, AmmunitionDefinition.KNIVES),
    JAVELIN(3, 90, ProjectileTiming.a, AmmunitionDefinition.JAVELINS),
    DART(3, 90, ProjectileTiming.c, AmmunitionDefinition.DARTS),
    OGRE(13, 90, ProjectileTiming.a, new AmmunitionDefinition[]{AmmunitionDefinition.OGRE_ARROW}),
    BRUTAL(13, 90, ProjectileTiming.a, AmmunitionDefinition.BRUTAL_ARROWS),
    THROWNAXE(3, 90, ProjectileTiming.a, AmmunitionDefinition.THROWNAXES),
    BOLTS(13, 90, ProjectileTiming.a, new AmmunitionDefinition[]{AmmunitionDefinition.BOLT, AmmunitionDefinition.BARBED_BOLTS, AmmunitionDefinition.RUNITE_BOLTS, AmmunitionDefinition.DRAGON_BOLTS_E}),
    KARILS_BOLT(13, 90, ProjectileTiming.a, new AmmunitionDefinition[]{AmmunitionDefinition.BOLT_RACK});

    private final ProjectileTiming projectileTiming;
    private final AmmunitionDefinition[] allowedAmmunition;
    private final int equipmentSlot;
    private final int graphicDelay;

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private AmmunitionProfile(int n2, int n3, ProjectileTiming projectileTiming, AmmunitionDefinition[] ammunitionDefinitionArray) {
        this.equipmentSlot = n2;
        this.graphicDelay = 90;
        this.projectileTiming = projectileTiming;
        this.allowedAmmunition = ammunitionDefinitionArray;
    }

    public final ProjectileTiming getProjectileTiming() {
        return this.projectileTiming;
    }

    public final AmmunitionDefinition[] getAllowedAmmunition() {
        return this.allowedAmmunition;
    }

    public final int getEquipmentSlot() {
        return this.equipmentSlot;
    }

    public final int getGraphicDelay() {
        return this.graphicDelay;
    }
}

