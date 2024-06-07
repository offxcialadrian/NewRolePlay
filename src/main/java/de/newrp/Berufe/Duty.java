package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Police.Fahndung;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Duty implements CommandExecutor {

    public static String PREFIX = "§8[§eDuty§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/duty");
            return true;
        }

        if(Fahndung.isFahnded(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst nicht in den Dienst gehen, während du gefahndet wirst.");
            return true;
        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST) {
            if (p.getLocation().distance(new Location(Script.WORLD, 347, 76, 1265)) > 5 && p.getLocation().distance(new Location(Script.WORLD, 285, 75, 1239)) > 5) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dmedic").removeEntry(p.getName());
                Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                Beruf.getBeruf(p.getPlayer()).changeDuty(p, false);
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dmedic").addEntry(p.getName());
            Beruf.getBeruf(p.getPlayer()).changeDuty(p, true);
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            Script.updateListname(p);
            return true;

        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
            if (p.getLocation().distance(new Location(Script.WORLD, 408, 71, 824)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("cpolice").removeEntry(p.getName());
                Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                Beruf.getBeruf(p.getPlayer()).changeDuty(p, false);
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("cpolice").addEntry(p.getName());
            Beruf.getBeruf(p.getPlayer()).changeDuty(p, true);
            Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            Script.updateListname(p);
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.NEWS) {

            if (p.getLocation().distance(new Location(Script.WORLD, 301, 67, 762, 358.99658f, -1.2081925f)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("enews").removeEntry(p.getName());
                Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                Beruf.getBeruf(p.getPlayer()).changeDuty(p, false);
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("enews").addEntry(p.getName());
            Beruf.getBeruf(p.getPlayer()).changeDuty(p, true);
            Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            Script.updateListname(p);
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.BUNDESNACHRICHTENDIENST) {
            if (p.getLocation().distance(new Location(Script.WORLD, 200, 71, 824)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("fbnd").removeEntry(p.getName());
                Beruf.Berufe.BUNDESNACHRICHTENDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                Beruf.getBeruf(p.getPlayer()).changeDuty(p, false);
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("fbnd").addEntry(p.getName());
            Beruf.getBeruf(p.getPlayer()).changeDuty(p, true);
            Beruf.Berufe.BUNDESNACHRICHTENDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            Script.updateListname(p);
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) {
            if (p.getLocation().distance(new Location(Script.WORLD, 533, 88, 1010, -135.82129f, 4.5042744f)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("bgovernment").removeEntry(p.getName());
                Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                Beruf.getBeruf(p.getPlayer()).changeDuty(p, false);
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("bgovernment").addEntry(p.getName());
            Beruf.getBeruf(p.getPlayer()).changeDuty(p, true);
            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            Script.updateListname(p);
            return true;
        }

        p.sendMessage(Messages.ERROR + "Dein Beruf hat keinen Duty.");

        return false;
    }

    public static boolean isInDuty(Player p) {
        final Beruf.Berufe beruf = Beruf.getBeruf(p.getPlayer());
        if(beruf == null) {
            return false;
        }

        return beruf.isDuty(p);
    }

    public static boolean isInDuty(UUID p) {
        return isInDuty(Objects.requireNonNull(Bukkit.getPlayer(p)));
    }

    public static void setDuty(Player p) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").removeEntry(p.getName());
        Beruf.getBeruf(p.getPlayer()).changeDuty(p, true);
    }

    public static void removeDuty(Player p) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").addEntry(p.getName());
        final Beruf.Berufe berufe = Beruf.getBeruf(p.getPlayer());
        if(berufe != null) {
            berufe.changeDuty(p, false);
        }
    }
}
