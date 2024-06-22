package de.newrp.features.bomb;

import de.newrp.features.bomb.data.WireType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IBombService {

    void plantBombAtLocation(final Player planter, final Location location);

    void openDefuserInventory(final Player player, final Location bombLocation);

    void defuseBomb(final Player player, final Location bombLocation, final WireType defuserType);
}
