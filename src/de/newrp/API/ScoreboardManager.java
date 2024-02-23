package de.newrp.API;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

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
        for (ScoreboardTeam team : ScoreboardTeam.values()) {
            Team t = MAIN.registerNewTeam(team.getName());
            t.setOption(team.getOption(), team.getOptionStatus());
        }
    }

    public enum ScoreboardTeam {
        NO_PUSH("zzznopush", Option.COLLISION_RULE, OptionStatus.NEVER),
        NRPS("1nrps", null, null),
        GOVERNMENT("2government", null, null),
        POLICE("3police", null, null),
        MEDIC("4medic", null, null),
        NEWS("5news", null, null);


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
