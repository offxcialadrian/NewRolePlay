package de.newrp.config.data;

import de.newrp.API.Script;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public class LocationConfig {
    private double x, y, z;
    private float yaw, pitch;

    public Location toLocation() {
        return new Location(Script.WORLD, x, y, z, yaw, pitch);
    }

    public static LocationConfig fromLocation(final Location location) {
        return new LocationConfig(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
