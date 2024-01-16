package de.newrp.Administrator;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Forum.Forum;
import de.newrp.Government.Arbeitslosengeld;
import de.newrp.Organisationen.Organisation;
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

public class JoinOrganisation implements CommandExecutor, Listener {
    private static final String PREFIX = "§8[§eOrganisation§8] §e" + Messages.ARROW + " ";

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
            p.sendMessage(Messages.ERROR + "/joinorganisation");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§8[§eOrganisationen§8]");
        int i = 0;
        for (Organisation beruf : Organisation.values()) {
            inv.setItem(i, new ItemBuilder(Material.PAPER).setName("§e" + beruf.getName()).setLore("§7Klicke um " + beruf.getName() + " zu betreten.").build());
            i++;
        }
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("§8[§eOrganisationen§8]")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            Organisation beruf = Organisation.getOrganisation(e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", ""));
            if (beruf == null) return;
            if (Beruf.getBeruf(p) != null) Beruf.getBeruf(p).removeMember(p, p);
            if (Organisation.getOrganisation(p) != null) Organisation.getOrganisation(p).removeMember(p, p);
            beruf.addMember(p, p);
            p.sendMessage(PREFIX + "Du bist nun Teil der " + beruf.getName() + ".");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "ist nun Teil der " + beruf.getName() + ".", true);
            p.sendMessage(Messages.INFO + "Du hast automatisch die Leaderrechte erhalten.");
            TeamSpeak.sync(Script.getNRPID(p));
            Forum.syncPermission(p);
            p.closeInventory();
        }
    }
}
