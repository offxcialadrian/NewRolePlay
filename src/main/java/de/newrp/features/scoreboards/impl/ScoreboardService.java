package de.newrp.features.scoreboards.impl;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.scoreboards.IScoreboardService;
import de.newrp.features.scoreboards.config.ScoreboardConfig;
import de.newrp.features.scoreboards.data.ScoreboardTeamData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Set;

public class ScoreboardService implements IScoreboardService {

    private final ScoreboardConfig scoreboardConfig = DependencyContainer.getContainer().getDependency(ScoreboardConfig.class);

    @Override
    public void createScoreboardOnJoin(Player player) {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (ScoreboardTeamData scoreboardTeamDatum : this.getScoreboardTeamData()) {
            final Team team = scoreboard.registerNewTeam(scoreboardTeamDatum.teamName());
            team.prefix(Component.text(scoreboardTeamDatum.displayPrefix()));
            team.setOption(Team.Option.COLLISION_RULE, scoreboardTeamDatum.collide() ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, scoreboardTeamDatum.nameInvisible() ? Team.OptionStatus.FOR_OWN_TEAM : Team.OptionStatus.ALWAYS);
        }

        this.updateGroup(player);
        this.sync(player);
    }

    @Override
    public void updateGroup(Player player) {

    }

    @Override
    public void sync(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            final Scoreboard scoreboard = onlinePlayer.getScoreboard();
            Team team = scoreboard.getEntryTeam(onlinePlayer.getName());
            if(team == null) {
                team = scoreboard.getTeam("1000default");
            }

            if(team == null) {
                continue;
            }

            final Team teamInOwnScoreboard = player.getScoreboard().getTeam(team.getName());
            if(teamInOwnScoreboard == null) {
                continue;
            }

            teamInOwnScoreboard.addEntry(onlinePlayer.getName());
        }
    }

    @Override
    public void setScoreboard(List<String> lines, Player player) {

    }

    @Override
    public void updateLine(int score, String line) {

    }

    @Override
    public Set<ScoreboardTeamData> getScoreboardTeamData() {
        return this.scoreboardConfig.teams();
    }
}
