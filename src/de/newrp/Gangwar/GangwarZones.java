package de.newrp.Gangwar;

import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import de.newrp.main;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.Statement;

public enum GangwarZones {

    PARK_KRANKENHAUS(1, "Park am Krankenhaus", new Location[] { new Location(Script.WORLD, 371, 77, 1141, 317.19263f, 4.649651f), new Location(Script.WORLD, 458, 76, 1094, 316.1435f, 7.0496526f), new Location(Script.WORLD, 585, 69, 1103, 73.91614f, 5.0996685f), new Location(Script.WORLD, 564, 65, 1188, 135.55707f, -3.3003342f)},
                                                    new Location(Script.WORLD, 467, 93, 1198, 221.80402f, 6.791289f), new Location(Script.WORLD, 564, 69, 1111, 75.2457f, 9.449617f),
                                                    new Location[] {new Location(Script.WORLD, 542, 69, 1128, 61.892517f, 9.449564f), new Location(Script.WORLD, 471, 69, 1193, 161.78973f, 6.4495254f), new Location(Script.WORLD, 470, 73, 1134, 322.32104f, 3.5996263f)}),
    SKATEPARK(2, "Skatepark", new Location[] { new Location(Script.WORLD, 706, 71, 1017, 22.146423f, 5.8498826f), new Location(Script.WORLD, 728, 65, 1102, 134.03522f, 6.5998526f), new Location(Script.WORLD, 632, 68, 1129, 201.70453f, 1.4995866f), new Location(Script.WORLD, 613, 69, 1039, 298.4734f, 3.4496026f)},
                                                    new Location(Script.WORLD, 706, 69, 1052, 26.229736f, 9.449713f), new Location(Script.WORLD, 652, 92, 1108, 248.3476f, 68.550026f),
                                                    new Location[]{new Location(Script.WORLD, 657, 70, 1091, 93.39734f, 44.849953f), new Location(Script.WORLD, 697, 73, 1090, 103.586334f, 12.299564f), new Location(Script.WORLD, 702, 70, 1070, 170.78705f, -3.3003447f)}),
    HAFEN(3, "Hafen", new Location[] { new Location(Script.WORLD, 969, 67, 1232, -161.0336f, 0.13279422f), new Location(Script.WORLD, 888, 66, 1024, -0.23248291f, 1.0327944f), new Location(Script.WORLD, 837, 66, 1289, -145.12738f, -2.2672079f), new Location(Script.WORLD, 960, 66, 1308, -234.53351f, 4.3327937f)},
                                                new Location(Script.WORLD, 706, 69, 1052, 26.229736f, 9.449713f), new Location(Script.WORLD, 652, 92, 1108, 248.3476f, 68.550026f),
                                                new Location[]{new Location(Script.WORLD, 657, 70, 1091, 93.39734f, 44.849953f), new Location(Script.WORLD, 697, 73, 190, 103.586334f, 12.299564f), new Location(Script.WORLD, 702, 70, 1070, 170.78705f, -3.3003447f)});

    private final int id;
    private final String name;
    private final Location[] spawns;
    private final Location pos1;
    private final Location pos2;
    private final Location[] capturePoints;

    GangwarZones(int id, String name, Location[] spawns, Location pos1, Location pos2, Location[] capturePoints) {
        this.id = id;
        this.name = name;
        this.spawns = spawns;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.capturePoints = capturePoints;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location[] getSpawn() {
        return spawns;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location[] getCapturePoints() {
        return capturePoints;
    }

    public static GangwarZones getZoneByID(int id) {
        for(GangwarZones zone : GangwarZones.values()) {
            if(zone.getID() == id) {
                return zone;
            }
        }
        return null;
    }

    public static GangwarZones getZoneByName(String name) {
        for(GangwarZones zone : GangwarZones.values()) {
            if(zone.getName().equalsIgnoreCase(name)) {
                return zone;
            }
        }
        return null;
    }

    public static GangwarZones getZoneByLocation(Location loc) {
        for(GangwarZones zone : GangwarZones.values()) {
            if(Script.isInRegion(loc, zone.getPos1(), zone.getPos2())) {
                return zone;
            }
        }
        return null;
    }

    public Organisation getOwner() {
        try(Statement stmt = main.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM gangwarzone WHERE zoneID='" + this.id + "'")) {
            if(rs.next()) {
                return Organisation.getOrganisation(rs.getInt("ownerID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }






}
