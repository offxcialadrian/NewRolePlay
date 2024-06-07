package de.newrp.features.group.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class OnlineGroupMember {

    private final Player player;
    private int rank;

}
