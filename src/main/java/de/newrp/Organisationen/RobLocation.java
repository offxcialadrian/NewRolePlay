package de.newrp.Organisationen;

import de.newrp.API.Script;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum RobLocation {

    JUWELIER("Juwelier", new Location(Script.WORLD, 640, 68, 899), Organisation.FALCONE, "Kasse"),
    UHRENLADEN("Uhrenladen", new Location(Script.WORLD, 696, 69, 835), Organisation.FALCONE, "Kasse"),
    BUTZE("Banditenversteck", new Location(Script.WORLD, 777, 70, 1153), Organisation.FALCONE, "Lager"),
    KIRCHE("Kirche", new Location(Script.WORLD, 220, 79, 660), Organisation.FALCONE, "Lager"),
    CASINO("Casino", new Location(Script.WORLD, 756, 109, 865), Organisation.CORLEONE, "Kasse"),
    BURGERSTAND("Burger-Stand", new Location(Script.WORLD, 663, 64, 637), Organisation.CORLEONE, "Kasse"),
    CONTAINER("Container", new Location(Script.WORLD, 1056, 66, 1065), Organisation.CORLEONE, "Lager"),
    UBAHN("U-Bahn-Stand", new Location(Script.WORLD, 273, 52, 877), Organisation.CORLEONE, "Lager"),
    BANK("Bank", new Location(Script.WORLD, 974, 83, 966), Organisation.KARTELL, "Kasse"),
    GERICHT("Gerichtsgebäude", new Location(Script.WORLD, 777, 79, 966), Organisation.KARTELL, "Kasse"),
    LABOR("Labor", new Location(Script.WORLD, 383, 75, 1307), Organisation.KARTELL, "Lager"),
    GARAGE("Garage", new Location(Script.WORLD, 681, 64, 1234), Organisation.KARTELL, "Lager"),
    KINO("Kino", new Location(Script.WORLD, 817, 74, 926), Organisation.SINALOA, "Kasse"),
    TIERHEIM("Tierheim", new Location(Script.WORLD, 592, 69, 1118), Organisation.SINALOA, "Kasse"),
    WAGEN("HotDog-Wagen", new Location(Script.WORLD, 843, 66, 741), Organisation.SINALOA, "Lager"),
    FLUGZEUG("Flugzeug", new Location(Script.WORLD, 851, 69, 1271), Organisation.SINALOA, "Lager"),
    ARCADE("Arcade", new Location(Script.WORLD, 401, 67, 750), Organisation.BRATERSTWO, "Kasse"),
    GELDWAGEN("Geldwagen", new Location(Script.WORLD, 909, 76, 912), Organisation.BRATERSTWO, "Kasse"),
    BAR("Bar", new Location(Script.WORLD, 699, 69, 865), Organisation.BRATERSTWO, "Lager"),
    NOODLES("Noodles", new Location(Script.WORLD, 867, 83, 952), Organisation.BRATERSTWO, "Lager");

    private final String name;
    private final Location loc;
    private final Organisation orga;
    private final String type;

    RobLocation(String name, Location loc, Organisation orga, String type) {
        this.name = name;
        this.loc = loc;
        this.orga = orga;
        this.type = type;
    }

    public static List<RobLocation> getLocations(Organisation orga) {
        List<RobLocation> list = new ArrayList<>();
        for (RobLocation rob : RobLocation.values()) if (rob.orga == orga) list.add(rob);
        return list;
    }

    public static RobLocation getRob(Organisation orga, Location loc) {
        for (RobLocation rob : RobLocation.getLocations(orga)) {
            if (rob.getLoc().distance(loc) <= 5) {
                return rob;
            }
        }
        return null;
    }
}
