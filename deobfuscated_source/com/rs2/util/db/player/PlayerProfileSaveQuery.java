/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.model.Position;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.Spellbook;
import com.rs2.util.db.DatabaseQuery;
import com.rs2.util.db.player.PlayerSaveQueryFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class PlayerProfileSaveQuery
extends DatabaseQuery {
    private /* synthetic */ PlayerSaveQueryFactory factory;

    public PlayerProfileSaveQuery(PlayerSaveQueryFactory playerSaveQueryFactory) {
        super("INSERT INTO prs06_players (id, username, x, y, z, appearance_0, appearance_1, appearance_2, appearance_3, appearance_4, appearance_5, appearance_6, color_0, color_1, color_2, color_3, color_4, pin, tutorial_stage, tutorial_progress, is_male, ban_expires, mute_expires, changing_bankpin, deleting_bankpin, pin_append_year, pin_append_date, binding_neck_charge, ring_of_forging_life, ring_of_recoil_life, slayer_master, slayer_task, task_amount, using_ancients, brimhaven_open, killed_clue_attacker) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE x = VALUES(x), y = VALUES(y), z = VALUES(z), appearance_0 = VALUES(appearance_0), appearance_1 = VALUES(appearance_1), appearance_2 = VALUES(appearance_2), appearance_3 = VALUES(appearance_3), appearance_4 = VALUES(appearance_4), appearance_5 = VALUES(appearance_5), appearance_6 = VALUES(appearance_6), color_0 = VALUES(color_0), color_1 = VALUES(color_1), color_2 = VALUES(color_2), color_3 = VALUES(color_3), color_4 = VALUES(color_4), pin = VALUES(pin), tutorial_stage = VALUES(tutorial_stage), tutorial_progress = VALUES(tutorial_progress), is_male = VALUES(is_male), ban_expires = VALUES(ban_expires), mute_expires = VALUES(mute_expires), changing_bankpin = VALUES(changing_bankpin), deleting_bankpin = VALUES(deleting_bankpin),pin_append_year = VALUES(pin_append_year), pin_append_date = VALUES(pin_append_date), binding_neck_charge = VALUES(binding_neck_charge), ring_of_forging_life = VALUES(ring_of_forging_life), ring_of_recoil_life = VALUES(ring_of_recoil_life), slayer_master = VALUES(slayer_master), slayer_task = VALUES(slayer_task), task_amount = VALUES(task_amount), using_ancients = VALUES(using_ancients), brimhaven_open = VALUES(brimhaven_open), killed_clue_attacker = VALUES(killed_clue_attacker)");
        this.factory = playerSaveQueryFactory;
    }

    @Override
    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {
        preparedStatement.setInt(1, PlayerSaveQueryFactory.getPlayer(this.factory).getReferenceId());
        preparedStatement.setString(2, PlayerSaveQueryFactory.getPlayer(this.factory).getUsername());
        Position position = PlayerSaveQueryFactory.getPlayer(this.factory).getPosition();
        preparedStatement.setInt(3, position.getX());
        preparedStatement.setInt(4, position.getY());
        preparedStatement.setInt(5, position.getPlane());
        int n = 0;
        while (n < PlayerSaveQueryFactory.getPlayer(this.factory).getAppearanceParts().length) {
            preparedStatement.setInt(n + 6, PlayerSaveQueryFactory.getPlayer(this.factory).getAppearanceParts()[n]);
            ++n;
        }
        n = 0;
        while (n < PlayerSaveQueryFactory.getPlayer(this.factory).getAppearanceColors().length) {
            preparedStatement.setInt(n + 13, PlayerSaveQueryFactory.getPlayer(this.factory).getAppearanceColors()[n]);
            ++n;
        }
        Object object = "";
        int n2 = 0;
        while (n2 < PlayerSaveQueryFactory.getPlayer(this.factory).getBankPinManager().getCurrentPin().length) {
            int n3 = PlayerSaveQueryFactory.getPlayer(this.factory).getBankPinManager().getCurrentPin()[n2];
            if (n3 == -1) {
                object = "na";
                break;
            }
            object = String.valueOf(object) + n3;
            ++n2;
        }
        preparedStatement.setString(18, (String)object);
        preparedStatement.setInt(21, PlayerSaveQueryFactory.getPlayer(this.factory).getGender());
        preparedStatement.setLong(22, PlayerSaveQueryFactory.getPlayer(this.factory).getBanExpires());
        preparedStatement.setLong(23, PlayerSaveQueryFactory.getPlayer(this.factory).getMuteExpires());
        preparedStatement.setBoolean(24, PlayerSaveQueryFactory.getPlayer(this.factory).getBankPinManager().isChangingPin());
        preparedStatement.setBoolean(25, PlayerSaveQueryFactory.getPlayer(this.factory).getBankPinManager().isDeletingPin());
        preparedStatement.setInt(26, PlayerSaveQueryFactory.getPlayer(this.factory).getBankPinManager().getPinAppendYear());
        preparedStatement.setInt(27, PlayerSaveQueryFactory.getPlayer(this.factory).getBankPinManager().getPinAppendDate());
        preparedStatement.setInt(28, PlayerSaveQueryFactory.getPlayer(this.factory).getBindingNecklaceCharge());
        preparedStatement.setInt(29, PlayerSaveQueryFactory.getPlayer(this.factory).getRingOfForgingLife());
        preparedStatement.setInt(30, PlayerSaveQueryFactory.getPlayer(this.factory).getRingOfRecoilLife());
        preparedStatement.setInt(31, PlayerSaveQueryFactory.getPlayer((PlayerSaveQueryFactory)this.factory).getSlayerManager().slayerMasterId);
        preparedStatement.setString(32, PlayerSaveQueryFactory.getPlayer((PlayerSaveQueryFactory)this.factory).getSlayerManager().slayerTaskName);
        preparedStatement.setInt(33, PlayerSaveQueryFactory.getPlayer((PlayerSaveQueryFactory)this.factory).getSlayerManager().taskAmount);
        preparedStatement.setBoolean(34, PlayerSaveQueryFactory.getPlayer(this.factory).getSpellbook() == Spellbook.ANCIENT);
        preparedStatement.setBoolean(35, PlayerSaveQueryFactory.getPlayer(this.factory).isBrimhavenOpen());
        object = PlayerSaveQueryFactory.getPlayer(this.factory);
        preparedStatement.setBoolean(36, ((Player)object).killedClueAttacker);
        preparedStatement.executeUpdate();
        return null;
    }
}

