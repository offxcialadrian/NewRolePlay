package de.newrp.features.group.data;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class OnlineGroupMember {

    private final Player player;
    private int rank;

}
