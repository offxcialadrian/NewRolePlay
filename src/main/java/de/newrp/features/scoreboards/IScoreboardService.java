package de.newrp.features.scoreboards;

import de.newrp.features.scoreboards.data.ScoreboardTeamData;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IScoreboardService {

    void createScoreboardOnJoin(final Player player);

    void updateGroup(final Player player);

    void sync(final Player player);

    void setScoreboard(final Player player, final String displayName, final BiConsumer<Scoreboard, Objective> scoreboardConsumer);

    void setScoreboard(final BoardConfiguration boardConfiguration, final Player player);

    void hideScoreboard(final Player player);

    void updateLine(final Player player, final String placeholder, final String line);

    void updateBoard(final BoardConfiguration boardConfiguration, final Player player, final Map<String, String> argumentMap);

    Team createScoreboardTeam(final Player player, final String placeholderName);

    Set<ScoreboardTeamData> getScoreboardTeamData();

    String getDefaultTeamName();

    String getMaskedTeamName();

    String getAFKTeamName();

    String getDeadTeamName();

    Team getTeamNameForCurrentState(final Player player);

}
