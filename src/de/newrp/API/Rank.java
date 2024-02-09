package de.newrp.API;

import org.bukkit.entity.Player;

public enum Rank {

    OWNER(0, 250, "Administrator", "Administration"),
    ADMINISTRATOR(1, 200, "Administrator", "Administration"),
    MODERATOR(2, 150, "Moderator", "Moderation"),
    SUPPORTER(3, 100, "Supporter", "Support"),
    PLAYER(4, 1, "Spieler", "Spieler");

    private final int id;
    private final int weight;
    private final String name;
    private final String o_name;

    Rank(int id, int weight, String name, String o_name) {
        this.id = id;
        this.weight = weight;
        this.name = name;
        this.o_name = o_name;
    }

    public int getWeight() {
        return weight;
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

}
