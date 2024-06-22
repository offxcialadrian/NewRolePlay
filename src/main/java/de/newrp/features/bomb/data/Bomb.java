package de.newrp.features.bomb.data;

import de.newrp.Organisationen.Organisation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
@Builder
public class Bomb {

    private Location location;
    private WireType wireType;
    private boolean isDefused;
    private boolean policeIsTarget;
    private Organisation organisationTarget;
    private Player planter;
    private long plantTime;

}
