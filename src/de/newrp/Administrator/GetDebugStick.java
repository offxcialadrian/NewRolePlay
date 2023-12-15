package de.newrp.Administrator;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetDebugStick implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im BuildMode.");
            return true;
        }
        p.getInventory().addItem(new ItemBuilder(Material.DEBUG_STICK).setName("§eDebug-Stick").setLore(Script.getName(p)+"s Debug-Stick").build());
        return false;
    }
}
