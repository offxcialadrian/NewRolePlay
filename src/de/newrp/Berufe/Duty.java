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

import java.util.ArrayList;

public class Duty implements CommandExecutor {

    public static String PREFIX = "§8[§eDuty§8] §e» §7";
    private static ArrayList<String> duty = new ArrayList<>();

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
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("4medic").removeEntry(p.getName());
                Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p.getName());
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("4medic").addEntry(p.getName());
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            duty.add(p.getName());
            Script.updateListname(p);
            return true;

        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
            if (p.getLocation().distance(new Location(Script.WORLD, 408, 71, 824)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("3police").removeEntry(p.getName());
                Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p.getName());
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("3police").addEntry(p.getName());
            Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            duty.add(p.getName());
            Script.updateListname(p);
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.NEWS) {

            if (p.getLocation().distance(new Location(Script.WORLD, 301, 67, 762, 358.99658f, -1.2081925f)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("5news").removeEntry(p.getName());
                Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p.getName());
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("5news").addEntry(p.getName());
            Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            duty.add(p.getName());
            Script.updateListname(p);
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) {
            if (p.getLocation().distance(new Location(Script.WORLD, 555, 78, 972, 56.03585f, 13.083509f)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("2government").removeEntry(p.getName());
                Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p.getName());
                Script.updateListname(p);
                return true;
            }

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("2government").addEntry(p.getName());
            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            duty.add(p.getName());
            Script.updateListname(p);
            return true;
        }

        p.sendMessage(Messages.ERROR + "Dein Beruf hat keinen Duty.");

        return false;
    }

    public static boolean isInDuty(Player p) {
        return duty.contains(p.getName());
    }

    public static void setDuty(Player p) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").removeEntry(p.getName());
        duty.add(p.getName());
    }

    public static void removeDuty(Player p) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").addEntry(p.getName());
        duty.remove(p.getName());
    }

}
