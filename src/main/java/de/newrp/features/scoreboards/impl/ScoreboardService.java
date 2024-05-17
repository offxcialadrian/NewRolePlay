package de.newrp.features.scoreboards.impl;

import de.newrp.API.Debug;
import de.newrp.API.Friedhof;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Organisationen.MaskHandler;
import de.newrp.Ticket.TicketCommand;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.scoreboards.BoardConfiguration;
import de.newrp.features.scoreboards.IScoreboardService;
import de.newrp.features.scoreboards.config.ScoreboardConfig;
import de.newrp.features.scoreboards.data.ScoreboardTeamData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScoreboardService implements IScoreboardService {

    private final ScoreboardConfig scoreboardConfig = DependencyContainer.getContainer().getDependency(ScoreboardConfig.class);

    @Override
    public void createScoreboardOnJoin(Player player) {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (ScoreboardTeamData scoreboardTeamDatum : this.getScoreboardTeamData()) {
            final Team team = scoreboard.registerNewTeam(scoreboardTeamDatum.teamName());
            team.prefix(Component.text(scoreboardTeamDatum.displayPrefix()));
            team.color(NamedTextColor.NAMES.value(scoreboardTeamDatum.displayColor()));
            team.suffix(Component.text(scoreboardTeamDatum.displaySuffix()));
            team.setOption(Team.Option.COLLISION_RULE, scoreboardTeamDatum.collide() ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, scoreboardTeamDatum.nameInvisible() ? Team.OptionStatus.FOR_OWN_TEAM : Team.OptionStatus.ALWAYS);
            Debug.debug("Registered team: " + team.getName());
        }
        player.setScoreboard(scoreboard);

        this.updateGroup(player);
        this.sync(player);
    }

    @Override
    public void updateGroup(Player player) {
        final Team teamForCurrentState = getTeamNameForCurrentState(player);
        teamForCurrentState.addEntry(player.getName());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            final Scoreboard scoreboard = onlinePlayer.getScoreboard();
            scoreboard.getTeam(teamForCurrentState.getName()).addEntry(player.getName());
        }
    }

    @Override
    public void sync(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            final Scoreboard scoreboard = onlinePlayer.getScoreboard();
            Team team = scoreboard.getEntryTeam(onlinePlayer.getName());
            if(team == null) {
                team = scoreboard.getTeam(getDefaultTeamName());
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
    public void setScoreboard(Player player, String displayName, BiConsumer<Scoreboard, Objective> scoreboardConsumer) {
        final Scoreboard scoreboard = player.getScoreboard();
        this.hideScoreboard(player);
        final Objective objective = scoreboard.registerNewObjective("sidebar", "dummy", Component.text(displayName));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboardConsumer.accept(scoreboard, objective);
    }

    @Override
    public void setScoreboard(BoardConfiguration boardConfiguration, Player player) {
        final Scoreboard scoreboard = player.getScoreboard();
        this.hideScoreboard(player);

        final Objective objective = scoreboard.registerNewObjective("sidebar", "dummy", Component.text(boardConfiguration.getScoreboardTitle(player)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final List<String> lines = boardConfiguration.getLines(player);
        int currentScore = lines.size() - 1;
        for (final String line : lines) {
            final boolean isPlaceholderLine = line.startsWith("[") && line.endsWith("]");
            final int score = currentScore--;
            if(!isPlaceholderLine) {
                objective.getScore(line).setScore(score);
                continue;
            }

            final Team team = scoreboard.registerNewTeam("sb_" + line.substring(1, line.length() - 1));
            team.addEntry("§1§" + ChatColor.values()[score].getChar());
            team.prefix(Component.text(line));
            objective.getScore("§1§" + ChatColor.values()[score].getChar()).setScore(score);
        }
    }

    @Override
    public void hideScoreboard(Player player) {
        final Scoreboard scoreboard = player.getScoreboard();
        final Objective objective = scoreboard.getObjective("sidebar");
        if(objective != null)
            objective.unregister();

        for (Team team : scoreboard.getTeams()) {
            if(team.getName().startsWith("sb_")) {
                team.unregister();
            }
        }
    }

    @Override
    public void updateLine(Player player, String placeholder, String line) {
        final Scoreboard scoreboard = player.getScoreboard();
        final Team team = scoreboard.getTeam("sb_" + placeholder);
        if(team == null) {
            return;
        }
        team.prefix(Component.text(line));
    }

    @Override
    public void updateBoard(BoardConfiguration boardConfiguration, Player player, final Map<String, String> argumentMap) {
        final Scoreboard scoreboard = player.getScoreboard();
        boardConfiguration.update(player, argumentMap);
    }

    @Override
    public Team createScoreboardTeam(Player player, String placeholderName) {
        final Scoreboard scoreboard = player.getScoreboard();
        if(scoreboard.getTeam("sb_" + placeholderName) != null) {
            scoreboard.getTeam("sb_" + placeholderName).unregister();
        }
        final Team team = scoreboard.registerNewTeam("sb_" + placeholderName);
        return team;
    }


    @Override
    public Set<ScoreboardTeamData> getScoreboardTeamData() {
        return this.scoreboardConfig.teams();
    }

    @Override
    public String getDefaultTeamName() {
        return "1000default";
    }

    @Override
    public String getMaskedTeamName() {
        return "1001masked";
    }

    @Override
    public String getAFKTeamName() {
        return "1002afk";
    }

    @Override
    public String getDeadTeamName() {
        return "1003dead";
    }

    @Override
    public Team getTeamNameForCurrentState(Player player) {
        final Beruf.Berufe faction = Beruf.getBeruf(player);
        final Abteilung.Abteilungen abteilungen = Beruf.getAbteilung(player);
        String factionTeamName = getDefaultTeamName();

        if (faction == Beruf.Berufe.POLICE) factionTeamName = "0002cop";
        if (faction == Beruf.Berufe.RETTUNGSDIENST) factionTeamName = "0003medics";
        if (faction == Beruf.Berufe.GOVERNMENT) factionTeamName = "0004government";
        if (faction == Beruf.Berufe.NEWS) factionTeamName = "0005news";
        if (abteilungen == Abteilung.Abteilungen.ZIVILPOLIZEI) factionTeamName = "1000default";


        String finalTeamName = getDefaultTeamName();
        if (SDuty.isSDuty(player)) {
            finalTeamName = "0001nrp";
        } else {
            if(Friedhof.isDead(player)) {
                finalTeamName = getDeadTeamName();
            } else {
                if(Duty.isInDuty(player)) {
                    finalTeamName = factionTeamName;
                }

                if(MaskHandler.masks.containsKey(player.getUniqueId())) {
                    finalTeamName = getMaskedTeamName();
                }
            }
        }

        return player.getScoreboard().getTeam(finalTeamName);
    }
}
