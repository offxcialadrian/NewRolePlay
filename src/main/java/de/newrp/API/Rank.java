package de.newrp.API;

import lombok.Getter;
import org.bukkit.entity.Player;

public enum Rank {

    OWNER(0, 300, "Geschäftsführer", "Geschäftsführer", "NRP", "0001ceo", 50),
    ADMINISTRATOR(1, 250, "Administrator", "Administration", "NRP", "0002admin", 50),
    FRAKTIONSMANAGER(6, 200, "Fraktionsmanager", "Fraktionsmanager", "FM", "0002fm", 50),
    MODERATOR(2, 150, "Moderator", "Moderation", "NRP", "0002mod", 50),
    SUPPORTER(3, 100, "Supporter", "Support", "NRP", "0003sup", 100),
    DEVELOPER(4, 50, "Developer", "Developer", "DEV", "0005dev", 100),
    PLAYER(5, 1, "Spieler", "Spieler", "", "", 0);

    private final int id;
    @Getter
    private final int weight;
    private final String name;
    private final String o_name;
    private final String prefix;
    private final String scoreboardName;
    private final int salary;

    Rank(int id, int weight, String name, String o_name, String prefix, final String scoreboardName, int salary) {
        this.id = id;
        this.weight = weight;
        this.name = name;
        this.o_name = o_name;
        this.prefix = prefix;
        this.scoreboardName = scoreboardName;
        this.salary = salary;
    }

    public String getName(Player p) {
        if (!Script.getGender(p).equals(Gender.MALE)) return name + "in";
        return name;
    }

    public String getName() {
        return o_name;
    }

    public int getID() {
        return id;
    }

    public static Rank getRankByID(int id) {
        for (Rank rank : Rank.values()) {
            if (rank.getID() == id) {
                return rank;
            }
        }
        return null;
    }

    public String getPrefix() {
        if(prefix.isEmpty()) return "";
        return prefix + " × ";
    }

    public static Rank getRankByWeight(int weight) {
        for(Rank rank : Rank.values()) {
            if(rank.getWeight() == weight) return rank;
        }
        return null;
    }

    public String getScoreboardName() {
        return scoreboardName;
    }

    public int getSalary() {
        return salary;
    }
}
