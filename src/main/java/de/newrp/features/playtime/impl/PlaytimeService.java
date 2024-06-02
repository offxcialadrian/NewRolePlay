package de.newrp.features.playtime.impl;

import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.AFK;
import de.newrp.features.playtime.IPlaytimeService;
import de.newrp.features.playtime.data.PlaytimeData;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlaytimeService implements IPlaytimeService {

    private final Map<UUID, PlaytimeData> playtimeData = new ConcurrentHashMap<>();

    @Override
    public void increasePlaytime(Player player) {
        PlaytimeData playtimeData = this.playtimeData.get(player.getUniqueId());
        if (playtimeData == null) {
            playtimeData = new PlaytimeData();
            this.playtimeData.put(player.getUniqueId(), playtimeData);
        }

        int minutes = playtimeData.getMinutes();
        int hours = playtimeData.getHours();

        if(minutes == 59) {
            playtimeData.setMinutes(0);
            playtimeData.setHours(hours + 1);
        } else {
            playtimeData.setMinutes(minutes + 1);
        }

        if(!AFK.isAFK(player)) {
            int a_minutes = playtimeData.getA_minutes();
            int a_hours = playtimeData.getA_hours();

            if(a_minutes == 59) {
                playtimeData.setA_minutes(0);
                playtimeData.setA_hours(a_hours + 1);
            } else {
                playtimeData.setA_minutes(a_minutes + 1);
            }

            if (playtimeData.getA_hours() % 50 == 0 && playtimeData.getA_minutes() == 0) {
                player.sendMessage(Script.PREFIX + "Du spielst nun bereits seit " + playtimeData.getA_hours() + " Stunden aktiv auf NRP × New RolePlay. Vielen Dank dafür!");
                player.sendMessage(Script.PREFIX + "Du erhältst als Dankeschön für deine Treue " + playtimeData.getA_hours() + " Exp");
                Script.addEXP(player, playtimeData.getA_hours(), true);
            }

            if (playtimeData.getA_hours() % 150 == 0 && playtimeData.getA_minutes() == 0) {
                player.sendMessage(Script.PREFIX + "Du erhältst als Dankeschön für deine Treue 3 Tage Premium");
                Premium.addPremiumStorage(player, TimeUnit.DAYS.toMillis(3), true);
            }
        }
    }

    @Override
    public void handleQuit(Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("UPDATE playtime SET hours = ?, minutes = ?, a_minutes = ?, a_hours = ? WHERE nrp_id = ?")) {
            final PlaytimeData playtimeData = this.playtimeData.get(player.getUniqueId());
            if (playtimeData != null) {
                preparedStatement.setInt(1, playtimeData.getHours());
                preparedStatement.setInt(2, playtimeData.getMinutes());
                preparedStatement.setInt(3, playtimeData.getA_minutes());
                preparedStatement.setInt(4, playtimeData.getA_hours());
                preparedStatement.setInt(5, Script.getNRPID(player));
                preparedStatement.executeUpdate();
            }
        } catch (Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
    }

    @Override
    public void loadPlaytime(Player player) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM playtime WHERE nrp_id = ?")) {
            preparedStatement.setInt(1, Script.getNRPID(player));
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final PlaytimeData playtimeData = new PlaytimeData();
                playtimeData.setHours(resultSet.getInt("hours"));
                playtimeData.setMinutes(resultSet.getInt("minutes"));
                playtimeData.setA_hours(resultSet.getInt("a_hours"));
                playtimeData.setA_minutes(resultSet.getInt("a_minutes"));
                this.playtimeData.put(player.getUniqueId(), playtimeData);
                System.out.println("loaded playtime for " + player.getName() + " with " + playtimeData.getHours() + " hours and " + playtimeData.getMinutes() + " minutes");
            }
        } catch (Exception exception) {
            NewRoleplayMain.handleError(exception);
        }

        if(!this.playtimeData.containsKey(player.getUniqueId())) {
            this.playtimeData.put(player.getUniqueId(), new PlaytimeData());
        }
    }

    @Override
    public PlaytimeData getPlaytime(Player player) {
        return this.playtimeData.getOrDefault(player.getUniqueId(), new PlaytimeData());
    }
}
