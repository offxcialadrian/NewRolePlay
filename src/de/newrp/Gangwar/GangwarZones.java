package de.newrp.Gangwar;

import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import de.newrp.main;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.Statement;

public enum GangwarZones {

    HAFEN(1, "Hafen", new Location(Script.WORLD, 1, 1, 1), new Location(Script.WORLD, 1, 1, 1), new Location(Script.WORLD, 1, 1, 1), new Location[] {new Location(Script.WORLD, 1, 1, 1), new Location(Script.WORLD, 1, 1, 1)}),
    BAHNHOF(2, "Bahnhof", new Location(Script.WORLD, 1, 1, 1), new Location(Script.WORLD, 1, 1, 1), new Location(Script.WORLD, 1, 1, 1), new Location[] {new Location(Script.WORLD, 1, 1, 1), new Location(Script.WORLD, 1, 1, 1)});

    private final int id;
    private final String name;
    private final Location spawn;
    private final Location pos1;
    private final Location pos2;
    private final Location[] capturePoints;

    GangwarZones(int id, String name, Location spawn, Location pos1, Location pos2, Location[] capturePoints) {
        this.id = id;
        this.name = name;
        this.spawn = spawn;
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

    public Location getSpawn() {
        return spawn;
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
