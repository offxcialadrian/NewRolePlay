package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class JoinTeam implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§eTeam§8] §e" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/jointeam");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§8[§eTeam§8]");
        int i = 0;
        for (Team.Teams team : Team.Teams.values()) {
            inv.setItem(i, new ItemBuilder(Material.PAPER).setName("§e" + team.getName()).setLore("§7Klicke um das Team zu betreten.").build());
            i++;
        }
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("§8[§eTeam§8]")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            Team.Teams team = Team.Teams.getTeam(e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", ""));
            if (team == null) return;
            if (Team.getTeam(p) != null) Team.removeTeam(p);
            Team.setTeam(p, team, true);
            p.sendMessage(PREFIX + "Du bist nun im " + team.getName() + ".");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "ist nun im " + team.getName() + ".", true);
            Log.HIGH.write(p.getName() + " hat das Team " + team.getName() + " betreten.");
            p.closeInventory();
            TeamSpeak.sync(Script.getNRPID(p));
        }
    }


}
