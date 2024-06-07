package de.newrp.features.group;

import de.newrp.features.group.data.Group;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface IGroupService {

    void createGroup(String groupName, Player player);

    void deleteGroup(int groupId, Player player);

    void invitePlayer(Group group, Player player, Player targetPlayer, final int rank);

    void kickPlayer(Player player, OfflinePlayer targetPlayer);

    void leaveGroup(Player player);

    void transferOwnership(int groupId, Player player, Player targetPlayer);

    List<Group> getGroups();

    void initGroupForPlayer(final Player player);

    Group getGroup(int groupId);

    Group getGroup(Player player);

    int getGroup(final OfflinePlayer player);

    boolean hasGroup(Player player);

    String getPrefix();

}
