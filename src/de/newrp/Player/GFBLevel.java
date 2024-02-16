package de.newrp.Player;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.GFB.GFB;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class GFBLevel implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length > 1) {
            p.sendMessage(Messages.ERROR + "/gfblevel [Job]");
            return true;
        }

        if(args.length == 0) {
            Inventory inv = Bukkit.createInventory(null, 9, "§6GFB Level");
            for(GFB gfb : GFB.values()) {
                inv.addItem(new ItemBuilder(Material.NETHER_STAR).setName("§6" + gfb.getName()).setLore(" §7» Level: §6" + gfb.getLevel(p), " §7» Exp: §6" + gfb.getExp(p) + "§8/§6" + GFB.getLevelCost(gfb.getLevel(p))).build());
            }
            p.openInventory(inv);
            return true;
        }

        GFB gfb = GFB.getGFBByName(args[0]);
        if(gfb == null) {
            p.sendMessage(Messages.ERROR + "Dieser GFB-Job existiert nicht.");
            return true;
        }

        p.sendMessage(GFB.PREFIX + "§6" + gfb.getName() + " §7» Level: §6" + gfb.getLevel(p) + " §7» Exp: §6" + gfb.getExp(p) + "§8/§6" + GFB.getLevelCost(gfb.getLevel(p)));

        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getView().getTitle().equals("§6GFB Level")) {
            e.setCancelled(true);
        }
    }

}
