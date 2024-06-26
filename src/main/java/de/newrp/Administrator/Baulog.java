package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.API.Team;
import de.newrp.NewRoleplayMain;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Baulog implements CommandExecutor {

    private static final String PREFIX = "§8[§6Baulog§8] §6" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        
        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false) && Team.getTeam(p) != Team.Teams.BAU) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p) && !Team.isTeamLeader(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length > 1) {
            p.sendMessage(Messages.ERROR + "/baulog <Name>");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(PREFIX + "Übersicht:");
            new BukkitRunnable() {
                @Override
                public void run() {
                    for(OfflinePlayer builder : Team.Teams.BAU.getAllMembers()) {
                        p.sendMessage(PREFIX + builder.getName() + " §8× §6" + Script.getBuiltBlocks(builder) + " §8(§6" + Script.getPercentage(Script.getBuiltOnlyPlacedBlocks(builder), Script.getBuiltBlocks(builder)) + "%§8)");
                    }
                }
            }.runTaskAsynchronously(NewRoleplayMain.getInstance());
            return true;
        }

        OfflinePlayer builder = Script.getOfflinePlayer(args[0]);

        if(Script.getNRPID(builder) == 0) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler existiert nicht.");
            return true;
        }

        if(Team.getTeam(builder) != Team.Teams.BAU) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler ist kein Mitglied des Bau-Teams.");
            return true;
        }

        p.sendMessage(PREFIX + builder.getName() + " §8× §6" + Script.getBuiltBlocks(builder));
        return true;

    }
}
