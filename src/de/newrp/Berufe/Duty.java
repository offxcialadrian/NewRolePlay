package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Duty implements CommandExecutor {

    public static String color;
    public static String PREFIX = "§8[§eDuty§8] §e» §7";
    private static ArrayList<Player> duty = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/duty");
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST) {
            if(p.getLocation().distance(new Location(Script.WORLD, 338, 78, 1155)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if(isInDuty(p)) {
                p.sendMessage(PREFIX + "Du hast den Dienst verlassen.");
                Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p);
                Script.updateListname(p);
                return true;
            }

            p.sendMessage(PREFIX + "Du hast den Dienst betreten.");
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            duty.add(p);
            color = "§4";
            Script.updateListname(p);
            return true;

        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
            if(p.getLocation().distance(new Location(Script.WORLD, 408, 71, 824)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if(isInDuty(p)) {
                Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p);
                color = "§9";
                Script.updateListname(p);
                return true;
            }

            Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            duty.add(p);
            Script.updateListname(p);
            return true;
        }

        p.sendMessage(Messages.ERROR + "Dein Beruf hat keinen Duty.");

        return false;
    }

    public static boolean isInDuty(Player p) {
        return duty.contains(p);
    }

    public static void setDuty(Player p) {
        duty.add(p);
    }

    public static void removeDuty(Player p) {
        duty.remove(p);
    }

}
