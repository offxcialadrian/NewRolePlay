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
    BUTZE("Junkie-Bude", new Location(Script.WORLD, 777, 70, 1153), Organisation.FALCONE, "Lager"),
    CASINO("Casino", new Location(Script.WORLD, 756, 109, 865), Organisation.CORLEONE, "Kasse"),
    CONTAINER("Container", new Location(Script.WORLD, 1056, 66, 1065), Organisation.CORLEONE, "Lager"),
    BANK("Bank", new Location(Script.WORLD, 974, 83, 966), Organisation.KARTELL, "Kasse"),
    LABOR("Labor", new Location(Script.WORLD, 383, 75, 1307), Organisation.KARTELL, "Lager"),
    YACHT("Yacht", new Location(Script.WORLD, 152, 64, 1082), Organisation.SINALOA, "Kasse"),
    WAGEN("HotDog-Wagen", new Location(Script.WORLD, 843, 66, 741), Organisation.SINALOA, "Lager"),
    SUPERMARKT("Supermarkt", new Location(Script.WORLD, 619, 66, 1277), Organisation.BRATERSTWO, "Kasse"),
    GARAGE("Garage", new Location(Script.WORLD, 681, 64, 1234), Organisation.BRATERSTWO, "Lager");

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
