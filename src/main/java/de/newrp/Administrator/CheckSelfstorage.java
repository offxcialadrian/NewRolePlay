package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Player.Selfstorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CheckSelfstorage implements CommandExecutor {

    public static final String PREFIX = "§8[§cSelfstorage§8] §c" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.MODERATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/checkselfstorage [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);

        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        p.openInventory(tg.getEnderChest());
        p.sendMessage(PREFIX + "Du checkst den Selfstorage-Room von §6" + Script.getName(tg) + "§7.");
        Script.sendTeamMessage(p, ChatColor.RED, "checkt den Selfstorage-Room von " + Script.getName(tg) + ".", true);


        return false;
    }
}
