package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.Sperre;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sperrinfo implements CommandExecutor {

    private static final String PREFIX = "§8[§cSperrinfo§8] §c" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        boolean nothing = true;
        int id = Script.getNRPID(p);
        if (Sperre.TRAGENSPERRE.isActive(id)) {
            nothing = false;
            long time = System.currentTimeMillis();
            long sperre = Sperre.TRAGENSPERRE.getTime(id);
            long left = sperre - time;
            int min = (int) (left / 1000) / 60;
            int hour = min / 60;
            min -= hour * 60;
            if (hour > 0) {
                p.sendMessage(PREFIX + "Deine Tragensperre geht noch " + hour + " Stunden und " + min + " Minuten.");
            } else if (min > 0) {
                p.sendMessage(PREFIX + "Deine Tragensperre geht noch " + min + " Minuten.");
            } else {
                p.sendMessage(PREFIX + "Deine Tragensperre ist nicht mehr aktiv.");
                Sperre.TRAGENSPERRE.remove(id);
            }
        }

        if(Sperre.PERSONALAUSWEIS.isActive(id)) {
            nothing = false;
            long time = System.currentTimeMillis();
            long sperre = Sperre.PERSONALAUSWEIS.getTime(id);
            long left = sperre - time;
            int min = (int) (left / 1000) / 60;
            int hour = min / 60;
            min -= hour * 60;
            if (hour > 0) {
                p.sendMessage(PREFIX + "Dein Personalausweis braucht noch " + hour + " Stunden und " + min + " Minuten.");
            } else if (min > 0) {
                p.sendMessage(PREFIX + "Dein Personalausweis braucht noch " + min + " Minuten.");
            } else {
                p.sendMessage(PREFIX + "Dein Personalausweis ist fertig.");
                Sperre.PERSONALAUSWEIS.remove(id);
            }
        }

        if(Sperre.MUTE.isActive(id)) {
            nothing = false;
            long time = System.currentTimeMillis();
            long sperre = Sperre.MUTE.getTime(id);
            long left = sperre - time;
            int min = (int) (left / 1000) / 60;
            int hour = min / 60;
            min -= hour * 60;
            if (hour > 0) {
                p.sendMessage(PREFIX + "Dein Mute geht noch " + hour + " Stunden und " + min + " Minuten.");
            } else if (min > 0) {
                p.sendMessage(PREFIX + "Dein Mute geht noch " + min + " Minuten.");
            } else {
                p.sendMessage(PREFIX + "Dein Mute ist nicht mehr aktiv.");
                Sperre.MUTE.remove(id);
            }
        }

        if (nothing) {
            p.sendMessage(PREFIX + "Du hast keine Sperre.");
        }
        return true;
    }
}