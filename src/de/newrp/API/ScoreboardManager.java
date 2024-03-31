package de.newrp.API;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreboardManager {

    public static final Scoreboard MAIN = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

    public static void clearMainScoreboard() {
        for (org.bukkit.scoreboard.Team t : MAIN.getTeams()) {
            if (MAIN.getTeam(t.getName()) != null) MAIN.getTeam(t.getName()).unregister();
        }
        for (Objective obj : MAIN.getObjectives()) {
            if (MAIN.getObjective(obj.getName()) != null) MAIN.getObjective(obj.getName()).unregister();
        }
    }

    public static void initMainScoreboard() {
        clearMainScoreboard();
        List<ScoreboardTeam> sortedTeams = Arrays.asList(ScoreboardTeam.values());
        sortedTeams.sort(Comparator.comparingInt(Enum::ordinal));

        for (ScoreboardTeam team : sortedTeams) {
            Team t = MAIN.registerNewTeam(team.getName());
            if (team.getOption() != null) t.setOption(team.getOption(), team.getOptionStatus());
        }

        for(Team team : MAIN.getTeams()) {
            System.out.println("Team: " + team.getName() + " Option: " + team.getOption(Option.COLLISION_RULE));
            Debug.debug("Team: " + team.getName() + " Option: " + team.getOption(Option.COLLISION_RULE));
        }

    }


    public enum ScoreboardTeam {
        NO_PUSH("zzznopush", Option.COLLISION_RULE, OptionStatus.NEVER),
        NRPS("anrps", Option.COLLISION_RULE, OptionStatus.NEVER),
        GOVERNMENT("bgovernment", null, null),
        POLICE("cpolice", null, null),
        MEDIC("dmedic", null, null),
        NEWS("enews", null, null),
        PLAYER("player", null, null);


        private final String name;
        private final Option option;
        private final OptionStatus status;

        ScoreboardTeam(String name, Option option, OptionStatus status) {
            this.name = name;
            this.option = option;
            this.status = status;
        }

        public String getName() {
            return this.name;
        }

        public Option getOption() {
            return this.option;
        }

        public OptionStatus getOptionStatus() {
            return this.status;
        }

    }
}
