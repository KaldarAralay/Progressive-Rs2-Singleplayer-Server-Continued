/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.util.db.DatabaseQuery;
import com.rs2.util.db.player.PlayerSaveQueryFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class PlayerSkillsSaveQuery
extends DatabaseQuery {
    private /* synthetic */ PlayerSaveQueryFactory factory;

    public PlayerSkillsSaveQuery(PlayerSaveQueryFactory playerSaveQueryFactory) {
        super("INSERT INTO prs06_skills (id, player_id, cur_attack, cur_defence, cur_strength, cur_hitpoints, cur_ranged, cur_prayer, cur_magic, cur_cooking, cur_woodcutting, cur_fletching, cur_fishing, cur_firemaking, cur_crafting, cur_smithing, cur_mining, cur_herblore, cur_agility, cur_thieving, cur_slayer, cur_farming, cur_runecrafting, exp_attack, exp_defence, exp_strength, exp_hitpoints, exp_ranged, exp_prayer, exp_magic, exp_cooking, exp_woodcutting, exp_fletching, exp_fishing, exp_firemaking, exp_crafting, exp_smithing, exp_mining, exp_herblore, exp_agility, exp_thieving, exp_slayer, exp_farming, exp_runecrafting) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE cur_attack = VALUES(cur_attack), cur_defence = VALUES(cur_defence), cur_strength = VALUES(cur_strength), cur_hitpoints = VALUES(cur_hitpoints), cur_ranged = VALUES(cur_ranged), cur_prayer = VALUES(cur_prayer), cur_magic = VALUES(cur_magic), cur_cooking = VALUES(cur_cooking), cur_woodcutting = VALUES(cur_woodcutting), cur_fletching = VALUES(cur_fletching), cur_fishing = VALUES(cur_fishing), cur_firemaking = VALUES(cur_firemaking), cur_thieving = VALUES(cur_thieving), cur_slayer = VALUES(cur_slayer), cur_farming = VALUES(cur_farming), cur_runecrafting = VALUES(cur_runecrafting), exp_attack = VALUES(exp_attack), exp_defence = VALUES(exp_defence), exp_strength = VALUES(exp_strength), exp_hitpoints = VALUES(exp_hitpoints), exp_ranged = VALUES(exp_ranged), exp_prayer = VALUES(exp_prayer), exp_magic = VALUES(exp_magic), exp_cooking = VALUES(exp_cooking), exp_woodcutting = VALUES(exp_woodcutting), exp_fletching = VALUES(exp_fletching), exp_fishing = VALUES(exp_fishing), exp_firemaking = VALUES(exp_firemaking), exp_thieving = VALUES(exp_thieving), exp_slayer = VALUES(exp_slayer), exp_farming = VALUES(exp_farming), exp_runecrafting = VALUES(exp_runecrafting)");
        this.factory = playerSaveQueryFactory;
    }

    @Override
    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {
        preparedStatement.setInt(1, PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
        preparedStatement.setInt(2, PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
        int n = 0;
        while (n <= 20) {
            preparedStatement.setInt(n + 3, PlayerSaveQueryFactory.getPlayer(this.factory).getSkillManager().getCurrentLevels()[n]);
            preparedStatement.setInt(n + 24, (int)PlayerSaveQueryFactory.getPlayer(this.factory).getSkillManager().getExperience()[n]);
            ++n;
        }
        preparedStatement.executeUpdate();
        return null;
    }
}

