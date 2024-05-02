package de.newrp.House;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Shop.PayShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InstallAddon implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        p.sendMessage(Messages.ERROR + "Das Installieren des Addons funktioniert nun mit Rechtsklick auf das Hausschild.");



        return false;
    }
}
