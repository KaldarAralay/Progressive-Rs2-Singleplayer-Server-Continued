/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.guide;

import com.rs2.model.item.ItemDefinition;
import com.rs2.model.skill.guide.SkillGuideEntry;
import java.util.ArrayList;

public final class SkillGuideCategory {
    public String name;
    private int e;
    public ArrayList entries = new ArrayList();
    public boolean prefixEntriesWithName;
    public boolean skipItemDefinitionLookup;
    private boolean f;

    public SkillGuideCategory(String string) {
        this.name = string;
        this.e = -1;
        this.prefixEntriesWithName = false;
        this.skipItemDefinitionLookup = false;
        this.f = false;
    }

    public SkillGuideCategory(String string, boolean bl) {
        this.name = string;
        this.e = -1;
        this.prefixEntriesWithName = false;
        this.skipItemDefinitionLookup = true;
        this.f = false;
    }

    public SkillGuideCategory(String string, int n, boolean bl) {
        this.name = string;
        this.e = -1;
        this.prefixEntriesWithName = true;
        this.skipItemDefinitionLookup = false;
        this.f = false;
    }

    public final void addEntry(SkillGuideEntry skillGuideEntry) {
        this.addEntry(skillGuideEntry, false);
    }

    public final void addEntry(SkillGuideEntry skillGuideEntry, boolean bl) {
        SkillGuideEntry skillGuideEntry2;
        if (!bl) {
            skillGuideEntry2 = skillGuideEntry;
            if (skillGuideEntry2.itemId >= 0) {
                skillGuideEntry2 = skillGuideEntry;
                if (!ItemDefinition.isDefined(skillGuideEntry2.itemId)) {
                    return;
                }
            }
        }
        if (bl) {
            skillGuideEntry2 = skillGuideEntry;
            if (!ItemDefinition.isDefined(skillGuideEntry2.itemId)) {
                skillGuideEntry.itemId = -1;
            }
        }
        this.entries.add(skillGuideEntry);
    }

    public final String getLevelText() {
        return "" + this.e;
    }
}
