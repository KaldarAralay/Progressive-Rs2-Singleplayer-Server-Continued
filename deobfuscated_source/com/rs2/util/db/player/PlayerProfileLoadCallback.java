/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.model.Position;
import com.rs2.model.skill.magic.Spellbook;
import com.rs2.util.db.DatabaseCallback;
import com.rs2.util.db.player.PlayerLoadQueryFactory;
import java.sql.ResultSet;

public final class PlayerProfileLoadCallback
implements DatabaseCallback {
    private /* synthetic */ PlayerLoadQueryFactory factory;

    private PlayerProfileLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory) {
        this.factory = playerLoadQueryFactory;
    }

    @Override
    public final void onResult(ResultSet object) throws java.sql.SQLException {
        if (object.next()) {
            String string;
            ResultSet resultSet = object;
            Object object2 = this;
            object2 = PlayerLoadQueryFactory.getPlayer(((PlayerProfileLoadCallback)object2).factory).getPosition();
            ((Position)object2).setX(resultSet.getInt("x"));
            ((Position)object2).setPreviousX(resultSet.getInt("x"));
            ((Position)object2).setY(resultSet.getInt("y"));
            ((Position)object2).setPreviousY(resultSet.getInt("y"));
            ((Position)object2).setPlane(resultSet.getInt("z"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setGender(object.getInt("is_male"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setAutoRetaliate(object.getBoolean("is_auto_retaliate"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setFightMode(object.getInt("fight_mode"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setBrightness(object.getInt("brightness"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setMouseButtons(object.getInt("mouse_buttons"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setPublicChatEffects(object.getInt("chat_effects"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setSplitPrivateChat(object.getInt("split_private_chat"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setAcceptAid(object.getInt("accept_aid"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setMusicVolume(object.getInt("music_volume"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setEffectVolume(object.getInt("effect_volume"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setSpecialEnergy(object.getInt("special"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setRunEnergyPercent(object.getInt("energy"));
            PlayerLoadQueryFactory.getPlayer(this.factory).getMovementQueue().setRunning(object.getBoolean("is_running"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setMuteExpires(object.getLong("mute_expires"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setBanExpires(object.getLong("ban_expires"));
            PlayerLoadQueryFactory.getPlayer(this.factory).getBankPinManager().setChangingPin(object.getBoolean("changing_bankpin"));
            PlayerLoadQueryFactory.getPlayer(this.factory).getBankPinManager().setDeletingPin(object.getBoolean("deleting_bankpin"));
            PlayerLoadQueryFactory.getPlayer(this.factory).getBankPinManager().setPinAppendYear(object.getInt("pin_append_year"));
            PlayerLoadQueryFactory.getPlayer(this.factory).getBankPinManager().setPinAppendYear(object.getInt("pin_append_date"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setBindingNecklaceCharge(object.getInt("binding_neck_charge"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setRingOfForgingLife(object.getInt("ring_of_forging_life"));
            PlayerLoadQueryFactory.getPlayer(this.factory).setRingOfForgingLife(object.getInt("ring_of_recoil_life"));
            int n = object.getInt("skull_timer");
            if (n > 0) {
                PlayerLoadQueryFactory.getPlayer(this.factory).addPvpCombatReference(PlayerLoadQueryFactory.getPlayer(this.factory), n);
            }
            if (!(string = object.getString("pin")).equals("na")) {
                int n2 = 0;
                while (n2 < PlayerLoadQueryFactory.getPlayer(this.factory).getBankPinManager().getCurrentPin().length) {
                    PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getBankPinManager().getCurrentPin()[n2] = Integer.parseInt(string.substring(n2, n2 + 1));
                    ++n2;
                }
            }
            int n3 = 0;
            while (n3 < PlayerLoadQueryFactory.getPlayer(this.factory).getAppearanceParts().length) {
                PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getAppearanceParts()[n3] = object.getInt("appearance_" + n3);
                ++n3;
            }
            n3 = 0;
            while (n3 < PlayerLoadQueryFactory.getPlayer(this.factory).getAppearanceColors().length) {
                PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getAppearanceColors()[n3] = object.getInt("color_" + n3);
                ++n3;
            }
            PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getSlayerManager().slayerMasterId = object.getInt("slayer_master");
            PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getSlayerManager().slayerTaskName = object.getString("slayer_task");
            PlayerLoadQueryFactory.getPlayer((PlayerLoadQueryFactory)this.factory).getSlayerManager().taskAmount = object.getInt("task_amount");
            PlayerLoadQueryFactory.getPlayer(this.factory).setSpellbook(object.getBoolean("using_ancients") ? Spellbook.ANCIENT : Spellbook.MODERN);
            PlayerLoadQueryFactory.getPlayer(this.factory).setBrimhavenOpen(object.getBoolean("brimhaven_open"));
            boolean bl = object.getBoolean("killed_clue_attacker");
            PlayerLoadQueryFactory.getPlayer(this.factory).killedClueAttacker = bl;
        }
    }

    @Override
    public final void onException(Exception exception) {
        exception.printStackTrace();
    }

    public /* synthetic */ PlayerProfileLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory, byte by) {
        this(playerLoadQueryFactory);
    }
}

