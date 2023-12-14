package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Navi;
import de.newrp.API.Route;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopRoute implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (Route.invalidate(p)) {
            p.sendMessage(Navi.PREFIX + "Du hast deine Route gelöscht.");
        } else {
            p.sendMessage(Messages.ERROR + "Du hast keine Route.");
        }
        return true;
    }
}