/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util.db.player;

import com.rs2.model.player.Player;
import com.rs2.util.db.DatabaseQuery;
import com.rs2.util.db.player.PlayerContactsSaveQuery;
import com.rs2.util.db.player.PlayerContainerSaveQuery;
import com.rs2.util.db.player.PlayerProfileSaveQuery;
import com.rs2.util.db.player.PlayerSkillsSaveQuery;
import java.util.HashMap;
import java.util.Map;

public final class PlayerSaveQueryFactory {
    private static final String CONTACTS_SAVE_SQL;
    private static final Map containerSaveSqlByContainerId;
    private final Player player;

    static {
        String string = "INSERT INTO prs06_contacts (`id`, `player_id`, `slot`, `contact`, `ignore`) VALUES ";
        String string2 = " ON DUPLICATE KEY UPDATE contact = VALUES(contact)";
        int n = 1;
        while (n <= 300) {
            string = String.valueOf(string) + "(?, ?, ?, ?, ?)";
            if (n != 300) {
                string = String.valueOf(string) + ", ";
            }
            ++n;
        }
        CONTACTS_SAVE_SQL = String.valueOf(string) + string2;
        containerSaveSqlByContainerId = new HashMap();
    }

    public PlayerSaveQueryFactory(Player player) {
        this.player = player;
    }

    public final DatabaseQuery createProfileSaveQuery() {
        return new PlayerProfileSaveQuery(this);
    }

    public final DatabaseQuery createBankSaveQuery() {
        return new PlayerContainerSaveQuery(this, this.player.getBankContainer(), 0);
    }

    public final DatabaseQuery createInventorySaveQuery() {
        return new PlayerContainerSaveQuery(this, this.player.getInventoryManager().getContainer(), 1);
    }

    public final DatabaseQuery createEquipmentSaveQuery() {
        return new PlayerContainerSaveQuery(this, this.player.getEquipmentManager().getContainer(), 2);
    }

    public final DatabaseQuery createContactsSaveQuery() {
        return new PlayerContactsSaveQuery(this);
    }

    public final DatabaseQuery createSkillsSaveQuery() {
        return new PlayerSkillsSaveQuery(this);
    }

    static /* synthetic */ Player getPlayer(PlayerSaveQueryFactory playerSaveQueryFactory) {
        return playerSaveQueryFactory.player;
    }

    static /* synthetic */ String getContainerSaveSql(int n, int n2) {
        if (!containerSaveSqlByContainerId.containsKey(n)) {
            String string = "INSERT INTO prs06_containers (id, container_id, user_id, item_id, amount, slot, timer) VALUES ";
            String string2 = " ON DUPLICATE KEY UPDATE item_id = VALUES(item_id), amount = VALUES(amount), slot = VALUES(slot), timer = VALUES(timer)";
            int n3 = 1;
            while (n3 <= n2) {
                string = String.valueOf(string) + "(?, ?, ?, ?, ?, ?, ?)";
                if (n3 != n2) {
                    string = String.valueOf(string) + ", ";
                }
                ++n3;
            }
            String string3 = String.valueOf(string) + string2;
            containerSaveSqlByContainerId.put(n, string3);
            return string3;
        }
        return (String)containerSaveSqlByContainerId.get(n);
    }

    static /* synthetic */ String getContactsSaveSql() {
        return CONTACTS_SAVE_SQL;
    }
}

