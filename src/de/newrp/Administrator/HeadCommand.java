package de.newrp.Administrator;

import de.newrp.API.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im BuildMode.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/head [Spieler]");
            return true;
        }

        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) is.getItemMeta();
        sm.setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
        sm.setDisplayName(args[0]);
        is.setItemMeta(sm);
        p.getInventory().addItem(is);

        return false;
    }
}
