package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Navi;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShareLocation implements CommandExecutor {

    public static String PREFIX = "§8[§bShareLocation§8] §b" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (args.length != 1) {
            cs.sendMessage(Messages.ERROR + "/shareloc [Spieler]");
            return true;
        }

        if (!Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
            return true;
        }

        if (!Mobile.mobileIsOn(p)) {
            p.sendMessage(Messages.ERROR + "Dein Handy ist ausgeschaltet.");
            return true;
        }

        if (!Mobile.hasConnection(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keine Verbindung.");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dir nicht selbst deinen Standort senden.");
            return true;
        }

        if (!Mobile.hasPhone(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat kein Handy.");
            return true;
        }

        if (!Mobile.mobileIsOn(tg)) {
            p.sendMessage(Messages.ERROR + "Die Nachricht wurde nicht zugestellt (keine Verbindung).");
            return true;
        }

        if (!Mobile.hasConnection(tg)) {
            p.sendMessage(Messages.ERROR + "Die Nachricht wurde nicht zugestellt (keine Verbindung).");
            return true;
        }

        p.sendMessage(PREFIX + "Dein Standort wurde an " + Script.getName(tg) + " gesendet.");
        Script.sendClickableMessage(tg, PREFIX + Script.getName(p) + " befindet sich in der Nähe von " + Navi.getNextNaviLocation(p.getLocation()).getName() + " (Klicke hier für eine exakte Navigation)", "/navi " + (int) p.getLocation().getX() + "/" + (int) p.getLocation().getY() + "/" + (int) p.getLocation().getZ(), "§7» Klicke für Navigation");

        return false;
    }
}

