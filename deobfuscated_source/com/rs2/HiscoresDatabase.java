/*
 * Decompiled with CFR 0.152.
 */
package com.rs2;

import com.rs2.model.player.Player;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public final class HiscoresDatabase {
    private static Statement statement;
    private static boolean connected;
    private static String jdbcUrl;
    private static String username;
    private static String password;
    private static String driverClassName;

    static {
        jdbcUrl = "jdbc:mysql://localhost:3306/rs2psp";
        username = "root";
        password = "root";
        driverClassName = "com.mysql.cj.jdbc.Driver";
    }

    public static void connect() {
        try {
            Class.forName(driverClassName).newInstance();
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            statement = connection.createStatement();
            connected = true;
            System.out.println("Succesfully connected to MySQL Database.");
            return;
        }
        catch (Exception exception) {
            connected = false;
            exception.printStackTrace();
            return;
        }
    }

    private static ResultSet executeSql(String sql) {
        try {
            if (sql.toLowerCase().startsWith("select")) {
                return statement.executeQuery(sql);
            }
            statement.executeUpdate(sql);
            return null;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            HiscoresDatabase.disconnect();
            return null;
        }
    }

    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            connected = false;
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    public static boolean savePlayer(Player object) {
        if (!connected) {
            return false;
        }
        if (((Player)object).isBot && ((Player)object).botMode != 4) {
            return false;
        }
        try {
            HiscoresDatabase.executeSql("DELETE FROM `skills` WHERE playerName = '" + ((Player)object).getUsername() + "';");
            HiscoresDatabase.executeSql("DELETE FROM `skillsoverall` WHERE playerName = '" + ((Player)object).getUsername() + "';");
            HiscoresDatabase.executeSql("INSERT INTO `skills` (`playerName`,`Attacklvl`,`Attackxp`,`Defencelvl`,`Defencexp`,`Strengthlvl`,`Strengthxp`,`Hitpointslvl`,`Hitpointsxp`,`Rangelvl`,`Rangexp`,`Prayerlvl`,`Prayerxp`,`Magiclvl`,`Magicxp`,`Cookinglvl`,`Cookingxp`,`Woodcuttinglvl`,`Woodcuttingxp`,`Fletchinglvl`,`Fletchingxp`,`Fishinglvl`,`Fishingxp`,`Firemakinglvl`,`Firemakingxp`,`Craftinglvl`,`Craftingxp`,`Smithinglvl`,`Smithingxp`,`Mininglvl`,`Miningxp`,`Herblorelvl`,`Herblorexp`,`Agilitylvl`,`Agilityxp`,`Thievinglvl`,`Thievingxp`,`Slayerlvl`,`Slayerxp`,`Farminglvl`,`Farmingxp`,`Runecraftlvl`,`Runecraftxp`) VALUES ('" + ((Player)object).getUsername() + "'," + ((Player)object).getSkillManager().getBaseLevel(0) + "," + ((Player)object).getSkillManager().getExperience()[0] + "," + ((Player)object).getSkillManager().getBaseLevel(1) + "," + ((Player)object).getSkillManager().getExperience()[1] + "," + ((Player)object).getSkillManager().getBaseLevel(2) + "," + ((Player)object).getSkillManager().getExperience()[2] + "," + ((Player)object).getSkillManager().getBaseLevel(3) + "," + ((Player)object).getSkillManager().getExperience()[3] + "," + ((Player)object).getSkillManager().getBaseLevel(4) + "," + ((Player)object).getSkillManager().getExperience()[4] + "," + ((Player)object).getSkillManager().getBaseLevel(5) + "," + ((Player)object).getSkillManager().getExperience()[5] + "," + ((Player)object).getSkillManager().getBaseLevel(6) + "," + ((Player)object).getSkillManager().getExperience()[6] + "," + ((Player)object).getSkillManager().getBaseLevel(7) + "," + ((Player)object).getSkillManager().getExperience()[7] + "," + ((Player)object).getSkillManager().getBaseLevel(8) + "," + ((Player)object).getSkillManager().getExperience()[8] + "," + ((Player)object).getSkillManager().getBaseLevel(9) + "," + ((Player)object).getSkillManager().getExperience()[9] + "," + ((Player)object).getSkillManager().getBaseLevel(10) + "," + ((Player)object).getSkillManager().getExperience()[10] + "," + ((Player)object).getSkillManager().getBaseLevel(11) + "," + ((Player)object).getSkillManager().getExperience()[11] + "," + ((Player)object).getSkillManager().getBaseLevel(12) + "," + ((Player)object).getSkillManager().getExperience()[12] + "," + ((Player)object).getSkillManager().getBaseLevel(13) + "," + ((Player)object).getSkillManager().getExperience()[13] + "," + ((Player)object).getSkillManager().getBaseLevel(14) + "," + ((Player)object).getSkillManager().getExperience()[14] + "," + ((Player)object).getSkillManager().getBaseLevel(15) + "," + ((Player)object).getSkillManager().getExperience()[15] + "," + ((Player)object).getSkillManager().getBaseLevel(16) + "," + ((Player)object).getSkillManager().getExperience()[16] + "," + ((Player)object).getSkillManager().getBaseLevel(17) + "," + ((Player)object).getSkillManager().getExperience()[17] + "," + ((Player)object).getSkillManager().getBaseLevel(18) + "," + ((Player)object).getSkillManager().getExperience()[18] + "," + ((Player)object).getSkillManager().getBaseLevel(19) + "," + ((Player)object).getSkillManager().getExperience()[19] + "," + ((Player)object).getSkillManager().getBaseLevel(20) + "," + ((Player)object).getSkillManager().getExperience()[20] + ");");
            HiscoresDatabase.executeSql("INSERT INTO `skillsoverall` (`playerName`,`level`,`xp`) VALUES ('" + ((Player)object).getUsername() + "'," + (((Player)object).getSkillManager().getBaseLevel(0) + ((Player)object).getSkillManager().getBaseLevel(1) + ((Player)object).getSkillManager().getBaseLevel(2) + ((Player)object).getSkillManager().getBaseLevel(3) + ((Player)object).getSkillManager().getBaseLevel(4) + ((Player)object).getSkillManager().getBaseLevel(5) + ((Player)object).getSkillManager().getBaseLevel(6) + ((Player)object).getSkillManager().getBaseLevel(7) + ((Player)object).getSkillManager().getBaseLevel(8) + ((Player)object).getSkillManager().getBaseLevel(9) + ((Player)object).getSkillManager().getBaseLevel(10) + ((Player)object).getSkillManager().getBaseLevel(11) + ((Player)object).getSkillManager().getBaseLevel(12) + ((Player)object).getSkillManager().getBaseLevel(13) + ((Player)object).getSkillManager().getBaseLevel(14) + ((Player)object).getSkillManager().getBaseLevel(15) + ((Player)object).getSkillManager().getBaseLevel(16) + ((Player)object).getSkillManager().getBaseLevel(17) + ((Player)object).getSkillManager().getBaseLevel(18) + ((Player)object).getSkillManager().getBaseLevel(19) + ((Player)object).getSkillManager().getBaseLevel(20)) + "," + (((Player)object).getSkillManager().getExperience()[0] + ((Player)object).getSkillManager().getExperience()[1] + ((Player)object).getSkillManager().getExperience()[2] + ((Player)object).getSkillManager().getExperience()[3] + ((Player)object).getSkillManager().getExperience()[4] + ((Player)object).getSkillManager().getExperience()[5] + ((Player)object).getSkillManager().getExperience()[6] + ((Player)object).getSkillManager().getExperience()[7] + ((Player)object).getSkillManager().getExperience()[8] + ((Player)object).getSkillManager().getExperience()[9] + ((Player)object).getSkillManager().getExperience()[10] + ((Player)object).getSkillManager().getExperience()[11] + ((Player)object).getSkillManager().getExperience()[12] + ((Player)object).getSkillManager().getExperience()[13] + ((Player)object).getSkillManager().getExperience()[14] + ((Player)object).getSkillManager().getExperience()[15] + ((Player)object).getSkillManager().getExperience()[16] + ((Player)object).getSkillManager().getExperience()[17] + ((Player)object).getSkillManager().getExperience()[18] + ((Player)object).getSkillManager().getExperience()[19] + ((Player)object).getSkillManager().getExperience()[20]) + ");");
            System.out.println("Hiscores saved for: " + ((Player)object).getUsername());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }
}

