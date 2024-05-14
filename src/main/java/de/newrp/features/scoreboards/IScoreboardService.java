package de.newrp.features.scoreboards;

import de.newrp.features.scoreboards.data.ScoreboardTeamData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface IScoreboardService {

    void createScoreboardOnJoin(final Player player);

    void updateGroup(final Player player);

    void sync(final Player player);

    void setScoreboard(final List<String> lines, final Player player);

    void updateLine(final int score, final String line);

    Set<ScoreboardTeamData> getScoreboardTeamData();

}
