package de.newrp.features.group.impl;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.features.group.IGroupService;
import de.newrp.features.group.data.Group;
import de.newrp.features.group.data.OnlineGroupMember;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupService implements IGroupService {

    private final List<Group> groups = new ArrayList<>();

    @Override
    public void createGroup(String groupName, Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO groups (group_name, group_owner, group_bank) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, groupName);
            preparedStatement.setInt(2, Script.getNRPID(player));
            preparedStatement.setInt(3, 0);
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                int groupId = resultSet.getInt("group_id");
                final Group group = new Group(groupId, groupName, null, -1, player.getUniqueId(), new ArrayList<>(), 10);
                this.groups.add(group);
            }
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public void deleteGroup(int groupId, Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("DELETE FROM groups WHERE group_id = ?")) {
            preparedStatement.setInt(1, groupId);
            preparedStatement.executeUpdate();
            this.groups.removeIf(group -> group.groupId() == groupId);
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public void invitePlayer(Group group, Player player, Player targetPlayer, int rank) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO group_members (group_id, nrp_id, rank, since) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, group.groupId());
            preparedStatement.setInt(2, Script.getNRPID(targetPlayer));
            preparedStatement.setInt(3, rank);
            preparedStatement.setLong(4, System.currentTimeMillis());
            preparedStatement.executeUpdate();

            group.members().add(new OnlineGroupMember(targetPlayer, rank));
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public void kickPlayer(Player player, OfflinePlayer targetPlayer) {
        final Group groupOfPlayer = this.getGroup(player);
        if(groupOfPlayer == null) {
            return;
        }

        if(!groupOfPlayer.groupOwner().equals(player.getUniqueId())) {
            player.sendMessage(Messages.ERROR + "Du kannst keine Spieler kicken, da du nicht der Besitzer der Gruppierung bist");
            return;
        }

        final int targetPlayerGroup = this.getGroup(targetPlayer);
        if(targetPlayerGroup == -1 || targetPlayerGroup != groupOfPlayer.groupId()) {
            player.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht in deiner Gruppierung");
            return;
        }

        groupOfPlayer.members().removeIf(e -> e.getPlayer().getUniqueId() == targetPlayer.getUniqueId());
        groupOfPlayer.sendMessage(getPrefix() + Script.getName(player) + " hat " + Script.getName(targetPlayer) + " aus der Gruppierung geworfen");

        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("DELETE FROM group_members WHERE nrp_id = ?")) {
            preparedStatement.setInt(1, Script.getNRPID(targetPlayer));
            preparedStatement.executeUpdate();
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public void leaveGroup(Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("DELETE FROM group_members WHERE nrp_id = ?")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            preparedStatement.executeUpdate();
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public void transferOwnership(int groupId, Player player, Player targetPlayer) {

    }

    @Override
    public List<Group> getGroups() {
        return this.groups;
    }

    @Override
    public void initGroupForPlayer(Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT group_id, rank FROM group_members WHERE nrp_id = ?")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    final int groupId = resultSet.getInt("group_id");
                    final int rank = resultSet.getInt("rank");
                    if(this.groups.stream().anyMatch(e -> e.groupId() == groupId)) {
                        this.groups.stream()
                                .filter(e -> e.groupId() == groupId)
                                .findFirst()
                                .ifPresent(group -> group.members().add(new OnlineGroupMember(player, rank)));
                        return;
                    }

                    try(final PreparedStatement preparedStatement1 = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM groups WHERE group_id = ?")) {
                        preparedStatement1.setInt(1, groupId);
                        try(final ResultSet resultSet1 = preparedStatement1.executeQuery()) {
                            if(resultSet1.next()) {
                                final Group group = new Group(groupId, resultSet1.getString("group_name"), null, -1, player.getUniqueId(), new ArrayList<>(), 10);
                                this.groups.add(group);
                                group.members().add(new OnlineGroupMember(player, rank));
                            }
                        }
                    }
                }
            }
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public Group getGroup(int groupId) {
        if(!this.groups.stream().anyMatch(e -> e.groupId() == groupId)) {
            try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM groups WHERE group_id = ?")) {
                preparedStatement.setInt(1, groupId);
                try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        final Group group = new Group(groupId, resultSet.getString("group_name"), null, -1, Script.getOfflinePlayer(resultSet.getInt("group_owner")).getUniqueId(), new ArrayList<>(), 10);
                        this.groups.add(group);
                    }
                }
            } catch(final Exception exception) {
                NewRoleplayMain.handleError(exception);
            }
        }
        return this.groups.stream().filter(e -> e.groupId() == groupId).findFirst().orElse(null);
    }

    @Override
    public Group getGroup(Player player) {
        return this.groups.stream()
                .filter(e -> e.members()
                        .stream()
                        .anyMatch(m -> m.getPlayer().getUniqueId().equals(player.getUniqueId()))
                )
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getGroup(OfflinePlayer player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT group_id FROM group_members WHERE nrp_id = ?")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getInt("group_id");
                }
            }
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
        return -1;
    }

    @Override
    public boolean hasGroup(Player player) {
        return false;
    }

    @Override
    public String getPrefix() {
        return "§8[§6Gruppierung§8] §6" + Messages.ARROW + " §7";
    }
}
