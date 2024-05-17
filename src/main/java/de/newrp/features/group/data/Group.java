package de.newrp.features.group.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class Group {

    private final int groupId;
    private String groupName;
    private Location groupSpawnLocation;
    private int groupHouseNumber;
    private UUID groupOwner;
    private List<OnlineGroupMember> members;

}
