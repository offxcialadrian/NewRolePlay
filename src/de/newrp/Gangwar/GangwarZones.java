package de.newrp.Gangwar;

import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import de.newrp.main;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.Statement;

public enum GangwarZones {

    PARK_KRANKENHAUS(1, "Wald", new Location[] { new Location(Script.WORLD, 371, 77, 1141, 317.19263f, 4.649651f), new Location(Script.WORLD, 458, 76, 1094, 316.1435f, 7.0496526f), new Location(Script.WORLD, 585, 69, 1103, 73.91614f, 5.0996685f), new Location(Script.WORLD, 564, 65, 1188, 135.55707f, -3.3003342f)},
                                                    new Location(Script.WORLD, 467, 93, 1198, 221.80402f, 6.791289f), new Location(Script.WORLD, 564, 69, 1111, 75.2457f, 9.449617f),
                                                    new Location[] {new Location(Script.WORLD, 542, 69, 1128, 61.892517f, 9.449564f), new Location(Script.WORLD, 471, 69, 1193, 161.78973f, 6.4495254f), new Location(Script.WORLD, 470, 73, 1134, 322.32104f, 3.5996263f)}),
    HAFEN(2, "Hafen", new Location[] { new Location(Script.WORLD, 888, 66, 1008, -0.57152957f, 2.0078812f), new Location(Script.WORLD, 917, 66, 1201, 226.83038f, 0.9578541f), new Location(Script.WORLD, 997, 67, 1208, 165.4784f, 0.50786877f), new Location(Script.WORLD, 938, 66, 1290, 143.87863f, 3.057858f)},
                                                new Location(Script.WORLD, 1088, 103, 1148, 183.333f, 46.949924f), new Location(Script.WORLD, 847, 61, 1055, 288.63196f, -3.15017f),
                                                new Location[] { new Location(Script.WORLD, 940, 66, 1056, -174.11232f, 3.4497628f), new Location(Script.WORLD, 851, 66, 1111, -46.3024f, 4.949781f), new Location(Script.WORLD, 1022, 66, 1059, -0.93795776f, -3.8696663f),
                                                        new Location(Script.WORLD, 1031, 69, 1098, -86.43622f, 3.6302805f), new Location(Script.WORLD, 1031, 66, 1135, -198.0346f, -17.250383f)}),
    FUNPARK(3, "Freizeitpark", new Location[] { new Location(Script.WORLD, 786, 63, 801, 213.37256f, 2.2247376f), new Location(Script.WORLD, 754, 64, 779, 232.57605f, -2.4252532f), new Location(Script.WORLD, 744, 65, 738, 247.27612f, -2.4252245f), new Location(Script.WORLD, 738, 65, 700, 303.53223f, -0.6252243f), new Location(Script.WORLD, 747, 63, 672, 313.60486f, 6.7247787f)}, new Location(Script.WORLD, 797, 61, 657, 341.0631f, 4.7747774f), new Location(Script.WORLD, 878, 101, 770, 131.0664f, 24.124739f),
            new Location[] {new Location(Script.WORLD, 814, 67, 667, 1.4716797f, 12.274769f), new Location(Script.WORLD, 868, 66, 743, 284.67517f, 18.874771f), new Location(Script.WORLD, 815, 66, 752, 128.69434f, 3.424541f)});


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

    public void setOwner(Organisation org) {
        try(Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE gangwarzone SET ownerID='" + org.getID() + "' WHERE zoneID='" + this.id + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPointsToWin() {
        return capturePoints.length * (Script.isInTestMode()?5:25);
    }





}
