package de.newrp.Administrator;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveLeaderrechte implements CommandExecutor {

    private static final String PREFIX = "§8[§cLeaderrechte§8] §c" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/giveleaderrechte [Spieler] [Main/Co]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if(Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(Beruf.hasBeruf(tg)) {
            if(Beruf.isLeader(tg, true)) {
                p.sendMessage(Messages.ERROR + " Dieser Spieler ist bereits Leader.");
                return true;
            }

            boolean main = args[1].equalsIgnoreCase("main");

            Beruf.setLeader(tg, main);
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " " + (main?"Main-":"Co-") + "Leaderrechte gegeben.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " " + (main?"Main-":"Co-") + "Leaderrechte gegeben.", true);
            if(tg.isOnline()) {
                tg.getPlayer().sendMessage(PREFIX + "Du hast " + (main?"Main-":"Co-") + "Leaderrechte bekommen.");
            }
            Script.addEXP(Script.getNRPID(tg), Script.getRandom(10, 20));
            Log.HIGH.write(p, "hat " + tg.getName() + " " + (main?"Main-":"Co-") + "Leaderrechte gegeben.");
            Log.HIGH.write(tg, "hat " + (main?"Main-":"Co-") + "Leaderrechte bekommen.");
            TeamSpeak.sync(Script.getNRPID(tg));
            return true;
        }

        if(Organisation.hasOrganisation(tg)) {
            if(Organisation.isLeader(tg, true)) {
                p.sendMessage(Messages.ERROR + " Dieser Spieler ist bereits Leader.");
                return true;
            }

            boolean main = args[1].equalsIgnoreCase("main");

            Organisation.setLeader(tg, main);
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " " + (main?"Main-":"Co-") + "Leaderrechte gegeben.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " " + (main?"Main-":"Co-") + "Leaderrechte gegeben.", true);
            if(tg.isOnline()) {
                tg.getPlayer().sendMessage(PREFIX + "Du hast " + (main?"Main-":"Co-") + "Leaderrechte bekommen.");
            }
            Script.addEXP(Script.getNRPID(tg), Script.getRandom(10, 20));
            Log.HIGH.write(p, "hat " + tg.getName() + " " + (main?"Main-":"Co-") + "Leaderrechte gegeben.");
            Log.HIGH.write(tg, "hat " + (main?"Main-":"Co-") + "Leaderrechte bekommen.");
            TeamSpeak.sync(Script.getNRPID(tg));
            return true;
        }

        p.sendMessage(Messages.ERROR + "Dieser Spieler ist in keiner Organisation oder Beruf.");



        return false;
    }
}
