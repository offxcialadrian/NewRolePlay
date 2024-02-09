package de.newrp.Administrator;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§cFly§8] §c" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.ADMINISTRATOR, true) && !Script.isInTestMode()) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p) && !Script.isInTestMode()) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(Messages.ERROR + "/fly {Spieler}");
            return true;
        }

        if (args.length == 0) {
            if (isFly(p)) {
                removeFly(p);
                p.sendMessage(PREFIX + "Du hast das Fliegen deaktiviert.");
                Script.sendTeamMessage(p, ChatColor.RED, "hat den Fly-Modus deaktiviert", true);
                Log.HIGH.write(p, "hat den Fly-Modus deaktiviert.");
            } else {
                setFly(p);
                p.sendMessage(PREFIX + "Du hast das Fliegen aktiviert");
                Script.sendTeamMessage(p, ChatColor.RED, "hat den Fly-Modus aktiviert", true);
                Log.HIGH.write(p, "hat den Fly-Modus aktiviert.");
            }
            return true;
        }

        Player tg = Bukkit.getPlayer(args[0]);

        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (isFly(tg)) {
            removeFly(tg);
            p.sendMessage(PREFIX + "Du hast das " + Script.getName(tg) + " das Fliegen deaktiviert.");
            tg.sendMessage(PREFIX + Messages.RANK_PREFIX(p) + " hat dir das Fliegen deaktiviert.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat den Fly-Modus deaktiviert.", true);
            Log.HIGH.write(p, "hat " + Script.getName(tg) + " den Fly-Modus deaktiviert.");
            Log.HIGH.write(tg, "wurde von " + Script.getName(p) + " der Fly-Modus deaktiviert.");
        } else {
            setFly(tg);
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " das Fliegen aktiviert.");
            tg.sendMessage(PREFIX + Messages.RANK_PREFIX(p) + " hat dir das Fliegen aktiviert.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " den Fly-Modus aktiviert.", true);
            Log.HIGH.write(p, "hat " + Script.getName(tg) + " den Fly-Modus aktiviert.");
            Log.HIGH.write(tg, "wurde von " + Script.getName(p) + " der Fly-Modus aktiviert.");
        }

        return false;
    }

    public static Boolean isFly(Player p) {
        return p.getAllowFlight();
    }

    public static void setFly(Player p) {
        p.setAllowFlight(true);
        p.setFlying(true);
    }

    public static void removeFly(Player p) {
        p.setFlying(false);
        p.setAllowFlight(false);
    }
}
