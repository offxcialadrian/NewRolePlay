package de.newrp.API;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class WantedInformation {

    private final Player player;
    private final int wantedPoints;
}
