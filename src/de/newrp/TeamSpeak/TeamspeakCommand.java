package de.newrp.TeamSpeak;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.newrp.API.Achievement;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.newrp.TeamSpeak.TeamSpeak.*;

public class TeamspeakCommand implements CommandExecutor {

    public static final List<String> WHITELIST = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/teamspeak [Eindeutige Identität]");
            return true;
        }
        String argument = args[0];
        if (argument.equalsIgnoreCase("reconnect") && Script.hasRank(p, Rank.ADMINISTRATOR, false) && SDuty.isSDuty(p)) {
            try {
                Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), TeamSpeak::disconnect);
            } catch (Exception e) {
                p.sendMessage(TeamSpeak.PREFIX + "§4Die Verbindung konnte nicht neu verbunden werden.");
            }
            Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), TeamSpeak::connect);
            Script.sendTeamMessage(TeamSpeak.PREFIX + "§c" + Script.getName(p) + " hat den Teamspeak neu verbunden.");
            return true;
        }

        if(args.length == 1 && argument.equalsIgnoreCase("sync")) {
            if(!TeamSpeak.isVerified(Script.getNRPID(p))) {
                p.sendMessage(TeamSpeak.PREFIX + "§cDu hast dich noch nicht verifiziert.");
                return true;
            }
            p.sendMessage(TeamSpeak.PREFIX + "§cDu hast deinen Teamspeak Account neu synchronisiert.");
            TeamSpeak.sync(Script.getNRPID(p));
        } else if (argument.equalsIgnoreCase("forcesync") && Script.hasRank(p, Rank.ADMINISTRATOR, false) && SDuty.isSDuty(p)) {
                if (args.length == 1) {
                    p.sendMessage(TeamSpeak.PREFIX + "/teamspeak forcesync [Spieler]");
                    return true;
                }
                OfflinePlayer target = Script.getOfflinePlayer(args[1]);
                if (target == null) {
                    p.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return true;
                }
                if(!TeamSpeak.isVerified(Script.getNRPID(target))) {
                    p.sendMessage(TeamSpeak.PREFIX + "§cDer Spieler hat sich noch nicht verifiziert.");
                    return true;
                }
                Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
                    TeamSpeak.sync(Script.getNRPID(target), getClient(Script.getNRPID(target)));
                    if(target.isOnline()) target.getPlayer().sendMessage(PREFIX + Messages.RANK_PREFIX(p) + " hat deinen Teamspeak Account neu synchronisiert.");
                    Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den TeamSpeak Account von " + target.getName() + " neu synchronisiert.", true);
                    p.sendMessage(TeamSpeak.PREFIX + "Du hast den Teamspeak Account von " + target.getName() + " neu synchronisiert.");
                });
                return true;

            } else if (argument.equalsIgnoreCase("whitelist")) {

                if (!SDuty.isSDuty(p)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                if (args.length == 1) {
                    p.sendMessage(TeamSpeak.PREFIX + "/teamspeak whitelist [Spieler]");
                } else {
                    Player p1 = Script.getPlayer(args[1]);
                    if (p1 == null) {
                        p.sendMessage(Messages.PLAYER_NOT_FOUND);
                    } else {
                        WHITELIST.add(p1.getName());
                        p.sendMessage("§cDu hast " + p1.getName() + " zur TeamSpeak Verifikation freigeschaltet.");
                    }
                }
            } else {


                Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
                    Client client = getClient(argument);
                    int unicaID = Script.getNRPID(p);
                    if (client == null) {
                        p.sendMessage(TeamSpeak.PREFIX + "§cEs wurde kein Teamspeak-User mit dieser EindeutigenID gefunden.");
                        if (Script.getRandom(1, 2) == 1)
                            p.sendMessage(Messages.INFO + "Sicher, dass du auf dem Teamspeak \"newrp.de\" verbunden bist?");
                    } else if (TeamSpeak.UidIsUsed(argument)) {
                        p.sendMessage(TeamSpeak.PREFIX + "§cDiese EindeutigeID ist bereits vergeben.");
                    } else if (getVerification(unicaID) != null) {
                        p.sendMessage(TeamSpeak.PREFIX + "§cDu hast dich bereits verifiziert.");
                    } else if (!client.getIp().equals(p.getAddress().getAddress().getHostAddress().split(":")[0])) {
                        p.sendMessage(TeamSpeak.PREFIX + "§cDu kannst dich nicht mit diesem Client verifizieren.");
                    } else {
                        TeamSpeak.verify(unicaID, client);
                        p.sendMessage(TeamSpeak.PREFIX + "Du hast deinen Minecraft Account mit deinem Teamspeak-Account verbunden!");
                        Achievement.TEAMSPEAK.grant(p);
                    }
                });
            }
        return true;
    }
}
