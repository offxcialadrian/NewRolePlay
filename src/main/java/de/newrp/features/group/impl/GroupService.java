package de.newrp.features.group.impl;

import de.newrp.NewRoleplayMain;
import de.newrp.features.group.IGroupService;
import de.newrp.features.group.data.Group;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.util.List;

public class GroupService implements IGroupService {

    @Override
    public void createGroup(String groupName, Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO groups (group_name, group_house, group_owner, group_spawn_x, group_spawn_y, group_spawn_z) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, groupName);
            preparedStatement.setObject(2, null);
            preparedStatement.setString(3, player.getUniqueId().toString());
            preparedStatement.setObject(4, null);
            preparedStatement.setObject(5, null);
            preparedStatement.setObject(6, null);
            preparedStatement.executeUpdate();
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public void deleteGroup(int groupId, Player player) {

    }

    @Override
    public void invitePlayer(int groupId, Player player, Player targetPlayer) {

    }

    @Override
    public void kickPlayer(int groupId, Player player, Player targetPlayer) {

    }

    @Override
    public void leaveGroup(int groupId, Player player) {

    }

    @Override
    public void transferOwnership(int groupId, Player player, Player targetPlayer) {

    }

    @Override
    public void setGroupSpawnLocation(int groupId, Player player, Location location) {

    }

    @Override
    public void setGroupHouseNumber(int groupId, Player player, int houseNumber) {

    }

    @Override
    public void setGroupName(int groupId, Player player, String groupName) {

    }

    @Override
    public List<Group> getGroups() {
        return null;
    }

    @Override
    public Group getGroup(int groupId) {
        return null;
    }

    @Override
    public Group getGroup(Player player) {
        return null;
    }

    @Override
    public Group getGroup(String groupName) {
        return null;
    }

    @Override
    public boolean hasGroup(Player player) {
        return false;
    }
}
