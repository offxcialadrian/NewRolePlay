package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.Call.Call;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class SMSCommand implements CommandExecutor {

    public static String PREFIX = "§8[§6Messenger§8] §6"  + Messages.ARROW + " §7";
    public static ArrayList<String> waitingForMessage = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length < 2) {
            p.sendMessage(Messages.ERROR + "/sms [Spieler] [Nachricht]");
            return true;
        }

        if(!Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
            return true;
        }

        if(!Mobile.isPhone(p.getInventory().getItemInMainHand())) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy in der Hand.");
            return true;
        }

        if(!Mobile.mobileIsOn(p)) {
            p.sendMessage(Messages.ERROR + "Dein Handy ist ausgeschaltet.");
            return true;
        }

        if(!Mobile.hasConnection(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keine Verbindung.");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        StringBuilder message = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        if(p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dir nicht selbst eine Nachricht senden.");
            return true;
        }

        if(!Mobile.hasPhone(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat kein Handy.");
            return true;
        }

        if(!Mobile.hasConnection(tg) || !Mobile.mobileIsOn(tg)) {
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " eine Nachricht gesendet.");
            p.sendMessage(PREFIX + "§7" + message.toString());
            waitingForMessage.add(tg.getName());
            Script.executeAsyncUpdate("INSERT INTO messages (nrp_id, sender, message, time, seen) VALUES ('" + Script.getNRPID(tg) + "', '" + Script.getNRPID(p) + "', '" + message.toString() + "', '" + System.currentTimeMillis() + "', false)");
            return true;
        }

        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " eine Nachricht gesendet.");
        p.sendMessage(PREFIX + "§7" + message.toString());
        tg.sendMessage(PREFIX + "Du hast eine neue Nachricht von " + Script.getName(p) + " erhalten.");
        tg.sendMessage(PREFIX + "§7" + message.toString());
        Script.executeAsyncUpdate("INSERT INTO messages (nrp_id, sender, message, time, seen) VALUES ('" + Script.getNRPID(tg) + "', '" + Script.getNRPID(p) + "', '" + message.toString() + "', '" + System.currentTimeMillis() + "', true)");
        if(!Mobile.getPhone(tg).getLautlos(tg)) tg.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);


        return false;
    }
}
