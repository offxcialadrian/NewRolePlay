package de.newrp.Player;

import de.newrp.API.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ParticleCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (!Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du benötigst Premium, um deine Partikel zu ändern.");
            p.sendMessage(Messages.INFO + "Du kannst Premium im Shop unter https://shop.newrp.de erwerben.");
            return true;
        }

        if (Route.getRoute(p) != null) {
            p.sendMessage(ParticleManager.PREFIX + "Du musst zuerst deine aktuelle Route mit \"/navistop\" löschen.");
            return true;
        }

        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, "§3Partikel anpassen");
        inv.setItem(1, Script.setNameAndLore(Material.COMPASS, "§6Navi-Route", "§3Ändere die Partikel für deine Route."));
        inv.setItem(3, Script.setNameAndLore(Material.COMPASS, "§6Spot-Partikel", "§3Ändere die Partikel für deinen Spot der Route."));
        p.openInventory(inv);


        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("§3Partikel anpassen")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                Player p = (Player) e.getWhoClicked();

                e.setCancelled(true);
                e.getView().close();

                if (is.getItemMeta().getDisplayName().equals("§6Navi-Route")) {
                    int size = ((Route.VALID_PARTICLE_ROUTE.length / 9) * 9) + 9;

                    int i = 0;

                    Inventory inv = Bukkit.getServer().createInventory(null, size, "§3Partikel auswählen...");
                    for (ParticleManager.ParticleWrapper pw : Route.VALID_PARTICLE_ROUTE) {
                        inv.setItem(i++, Script.setNameAndLore(pw.getItemStack(), pw.getColorCode() + pw.getName(), "§6Diese Partikel anzeigen."));
                    }
                    p.openInventory(inv);

                } else if (is.getItemMeta().getDisplayName().equals("§6Spot-Partikel")) {
                    int size = ((Route.VALID_PARTICLE_SPOT.length / 9) * 9) + 9;

                    int i = 0;

                    Inventory inv = Bukkit.getServer().createInventory(null, size, "§3Partikel auswählen...§r");
                    for (ParticleManager.ParticleWrapper pw : Route.VALID_PARTICLE_SPOT) {
                        inv.setItem(i++, Script.setNameAndLore(pw.getItemStack(), pw.getColorCode() + pw.getName(), "§6Diese Partikel anzeigen."));
                    }
                    p.openInventory(inv);
                }
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("§3Partikel auswählen...")) {
            //ROUTE
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                Player p = (Player) e.getWhoClicked();

                e.setCancelled(true);
                e.getView().close();

                ParticleManager.ParticleWrapper pw = null;

                for (ParticleManager.ParticleWrapper all : Route.VALID_PARTICLE_ROUTE) {
                    if (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals(all.getName())) {
                        pw = all;
                        break;
                    }
                }

                if (pw != null) {
                    ParticleManager.setParticle(Script.getNRPID(p), ParticleManager.ParticleType.ROUTE, pw);
                    p.sendMessage(ParticleManager.PREFIX + "Dir werden nun \"" + pw.getName() + "\"-Partikel angezeigt");
                }
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("§3Partikel auswählen...§r")) {
            //SPOT
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                Player p = (Player) e.getWhoClicked();

                e.setCancelled(true);
                e.getView().close();

                ParticleManager.ParticleWrapper pw = null;

                for (ParticleManager.ParticleWrapper all : Route.VALID_PARTICLE_SPOT) {
                    if (ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals(all.getName())) {
                        pw = all;
                        break;
                    }
                }

                if (pw != null) {
                    ParticleManager.setParticle(Script.getNRPID(p), ParticleManager.ParticleType.SPOT, pw);
                    p.sendMessage(ParticleManager.PREFIX + "Dir werden nun \"" + pw.getName() + "\"-Partikel angezeigt");
                }
            }
        }
    }

}
