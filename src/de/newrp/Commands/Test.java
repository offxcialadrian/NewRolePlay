package de.newrp.Commands;

import de.newrp.API.Debug;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Test implements CommandExecutor, Listener {

    private static final boolean block = false;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        p.setPassenger(Bukkit.getPlayer(args[0]));

        return false;
    }


}
