package de.newrp.API;

import lombok.Getter;
import org.bukkit.entity.Player;

public enum Rank {

    OWNER(0, 300, "Geschäftsführer", "Geschäftsführer"),
    ADMINISTRATOR(1, 250, "Administrator", "Administration"),
    FRAKTIONSMANAGER(6, 200, "Fraktionsmanager", "Fraktionsmanager"),
    MODERATOR(2, 150, "Moderator", "Moderation"),
    SUPPORTER(3, 100, "Supporter", "Support"),
    DEVELOPER(4, 50, "Developer", "Developer"),
    PLAYER(5, 1, "Spieler", "Spieler");

    private final int id;
    @Getter
    private final int weight;
    private final String name;
    private final String o_name;

    Rank(int id, int weight, String name, String o_name) {
        this.id = id;
        this.weight = weight;
        this.name = name;
        this.o_name = o_name;
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

    public static Rank getRankByWeight(int weight) {
        for(Rank rank : Rank.values()) {
            if(rank.getWeight() == weight) return rank;
        }
        return null;
    }

}
