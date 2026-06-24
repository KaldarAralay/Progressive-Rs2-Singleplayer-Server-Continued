/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill;

import com.rs2.model.skill.SkillManager;
import com.rs2.model.task.TickTask;

public final class SkillLevelRestoreTask
extends TickTask {
    private /* synthetic */ SkillManager skillManager;
    private final /* synthetic */ int skillId;

    public SkillLevelRestoreTask(SkillManager skillManager, int n, int n2) {
        super(n);
        this.skillManager = skillManager;
        this.skillId = n2;
    }

    @Override
    public final void execute() {
        if (SkillManager.getPlayer(this.skillManager) == null) {
            this.stop();
            return;
        }
        if (!SkillManager.getPlayer((SkillManager)this.skillManager).el || SkillManager.getPlayer(this.skillManager).isDead()) {
            this.stop();
            return;
        }
        if (SkillManager.getCurrentLevels(this.skillManager)[this.skillId] != SkillManager.getLevelForExperience(this.skillManager.getExperience()[this.skillId])) {
            if (SkillManager.getCurrentLevels(this.skillManager)[this.skillId] > SkillManager.getLevelForExperience(this.skillManager.getExperience()[this.skillId])) {
                int[] nArray = SkillManager.getCurrentLevels(this.skillManager);
                int n = this.skillId;
                nArray[n] = nArray[n] - 1;
            } else {
                int[] nArray = SkillManager.getCurrentLevels(this.skillManager);
                int n = this.skillId;
                nArray[n] = nArray[n] + 1;
            }
            this.skillManager.refreshSkill(this.skillId);
        }
        if (!this.skillManager.isLevelModified(this.skillId)) {
            this.stop();
        }
    }
}

