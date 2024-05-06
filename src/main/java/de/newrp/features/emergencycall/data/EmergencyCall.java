package de.newrp.features.emergencycall.data;

import de.newrp.Berufe.Beruf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Accessors(fluent = true)
@Getter()
@Setter
@AllArgsConstructor
public class EmergencyCall {

    private final Player sender;
    private final Location location;
    private final Beruf.Berufe faction;
    private final String reason;
    private AcceptEmergencyCallMetadata acceptEmergencyCallMetadata;
}
