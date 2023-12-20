package de.newrp.API;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.newrp.main;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

public enum Achievement {
    FIRST_JOIN(1, "Willkommen!", "Ein neuer Bewohner!", 50),
    SERVERTEAM(2, "Serverteam", "Du bist ein Teil des Serverteams!", 100),
    TEAMJOIN(3, "Teammitglied", "Du bist einem Team beigetreten!", 30),
    TEAMLEADER(4, "Teamleiter", "Du bist Teamleiter geworden!", 50);

    private final int id;
    private final String text;
    private final String name;
    private final int exp;

    Achievement(int id, String name, String text, int exp) {
        this.id = id;
        this.text = text;
        this.name = name;
        this.exp = exp;
    }

    public static Achievement getAchievementByID(int id) {
        for (Achievement a : values()) {
            if (a.getID() == id) return a;
        }
        return null;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        return this.text;
    }

    public int getExp() {
        return this.exp;
    }

    public void grant(Player p) {
        int id = Script.getNRPID(p);
        LinkedHashMap<Achievement, Boolean> cache = getAchievements(id);
        boolean done = false;
        if (cache != null) {
            done = cache.get(this);
        }
        if (!done) {
            Title.sendTitle(p, 20, 100, 20, "§aAchievement freigeschaltet!");
            p.sendMessage("§8[§aAchievement§8]§6 Du hast das Achievement \"§6§l" + this.getText() + "§r§6\" freigeschaltet!");
            Script.addEXP(p, this.getExp());
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            Script.executeAsyncUpdate("INSERT INTO achievements (userID, achievementID, time, done) VALUES (" + id + ", " + this.getID() + ", " + System.currentTimeMillis() + ", TRUE);");
        }
    }

    public void grant(OfflinePlayer p) {
        int id = Script.getNRPID(p);
        LinkedHashMap<Achievement, Boolean> cache = getAchievements(id);
        boolean done = false;
        if (cache != null) {
            done = cache.get(this);
        }
        if (!done) {
            Script.executeAsyncUpdate("INSERT INTO achievements (userID, achievementID, time, done) VALUES (" + id + ", " + this.getID() + ", " + System.currentTimeMillis() + ", TRUE);");
            if(p.isOnline()) {
                Player player = p.getPlayer();
                Title.sendTitle(player, 20, 100, 20, "§aAchievement freigeschaltet!");
                player.sendMessage("§8[§aAchievement§8]§6 Du hast das Achievement \"§6§l" + this.getText() + "§r§6\" freigeschaltet!");
                Script.addEXP(player, this.getExp());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            } else {
                Script.addOfflineMessage(p, "§8[§aAchievement§8]§6 Du hast das Achievement \"§6§l" + this.getText() + "§r§6\" freigeschaltet!");
                Script.addEXP(Script.getNRPID(p), this.getExp());
            }
        }
    }

    public static LinkedHashMap<Achievement, Boolean> getAchievements(int id) {
        LinkedHashMap<Achievement, Boolean> map = new LinkedHashMap<>();
        Achievement[] all = Achievement.values();

        for (Achievement a : all) {
            map.put(a, false);
        }
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT achievementID FROM achievements WHERE userID=" + id + " AND done=TRUE")) {
            while (rs.next()) {
                Achievement a = Achievement.getAchievementByID(rs.getInt("achievementID"));
                if (a != null) {
                    map.put(a, true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
