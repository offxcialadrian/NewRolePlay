package de.newrp.features.group;

import de.newrp.features.group.data.Group;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface IGroupService {

    void createGroup(String groupName, Player player);

    void deleteGroup(int groupId, Player player);

    void invitePlayer(int groupId, Player player, Player targetPlayer);

    void kickPlayer(int groupId, Player player, Player targetPlayer);

    void leaveGroup(int groupId, Player player);

    void transferOwnership(int groupId, Player player, Player targetPlayer);

    void setGroupSpawnLocation(int groupId, Player player, Location location);

    void setGroupHouseNumber(int groupId, Player player, int houseNumber);

    void setGroupName(int groupId, Player player, String groupName);

    List<Group> getGroups();

    Group getGroup(int groupId);

    Group getGroup(Player player);

    Group getGroup(String groupName);

    boolean hasGroup(Player player);

}
