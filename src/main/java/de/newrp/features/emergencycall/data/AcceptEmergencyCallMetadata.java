package de.newrp.features.emergencycall.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class AcceptEmergencyCallMetadata {

    private final Player player;
    private final Location startLocation;
    private final long timestamp;

}
