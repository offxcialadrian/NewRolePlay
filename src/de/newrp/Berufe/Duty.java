package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Police.Fahndung;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Duty implements CommandExecutor {

    public static String PREFIX = "§8[§eDuty§8] §e» §7";
    private static ArrayList<Player> duty = new ArrayList<>();

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
            if (p.getLocation().distance(new Location(Script.WORLD, 347, 76, 1265)) > 5 && p.getLocation().distance(new Location(Script.WORLD, 281, 75, 1239)) > 5) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                p.sendMessage(PREFIX + "Du hast den Dienst verlassen.");
                Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p);
                Script.updateListname(p);
                return true;
            }

            p.sendMessage(PREFIX + "Du hast den Dienst betreten.");
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst betreten.");
            duty.add(p);
            Script.updateListname(p);
            return true;

        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
            if (p.getLocation().distance(new Location(Script.WORLD, 408, 71, 824)) > 10) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe des Duty-Punkt");
                return true;
            }

            if (isInDuty(p)) {
                Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " hat den Dienst verlassen.");
                duty.remove(p);
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
