package de.newrp.features.bomb;

import de.newrp.Gangwar.GangwarZones;
import de.newrp.Organisationen.Organisation;
import de.newrp.features.bomb.data.Bomb;
import de.newrp.features.bomb.data.WireType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class BombService implements IBombService {

    private Bomb activeBomb = null;

    @Override
    public void plantBombAtLocation(Player planter, Location location) {
        Organisation organisation = null;
        for (GangwarZones value : GangwarZones.values()) {
            final Location location1 = value.getPos1();
            final Location location2 = value.getPos2();

            if(!isPointBetweenTwoPoints(location1, location2, location)) {
                continue;
            }

            organisation = value.getOwner();
        }


        activeBomb = Bomb.builder()
                .location(location)
                .planter(planter)
                .policeIsTarget(organisation == null)
                .organisationTarget(organisation)
                .build();
    }

    @Override
    public void openDefuserInventory(Player player, Location bombLocation) {

    }

    @Override
    public void defuseBomb(Player player, Location bombLocation, WireType defuserType) {

    }

    public boolean isPointBetweenTwoPoints(final Location point1, final Location point2, final Location location) {
        double distanceAC = Math.sqrt(Math.pow(location.getX() - point1.getX(), 2) + Math.pow(location.getY() - point1.getY(), 2) + Math.pow(location.getZ() - point1.getZ(), 2));
        double distanceCB = Math.sqrt(Math.pow(point2.getX() - location.getX(),2) + Math.pow(point2.getY() - location.getY(), 2) + Math.pow(point2.getZ()- location.getZ(), 2));
        double distanceAB = Math.sqrt(Math.pow(point2.getX() - point1.getX(),2) + Math.pow(point2.getY() - point1.getY(), 2) + Math.pow(point2.getZ() - point1.getZ(), 2));
        return Math.abs(distanceAC + distanceCB - distanceAB) < 1e-9;
    }
}
