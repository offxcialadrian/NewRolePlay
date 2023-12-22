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

    private static String PREFIX = "§8[§aSperrinfo§8] §a" + Messages.ARROW + " ";

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

        if (nothing) {
            p.sendMessage(PREFIX + "Du hast keine Sperre.");
        }
        return true;
    }
}