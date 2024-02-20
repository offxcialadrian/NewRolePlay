package de.newrp.News;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Zeitung implements CommandExecutor, Listener {
    public static final String prefix = "§8[§6Zeitung§8]§6 " + Messages.ARROW + " §7";
    public static ItemStack zeitung;
    public static BookMeta cache = null;

    public static int getNextID() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM zeitung ORDER BY id DESC LIMIT 1")) {
            if (rs.next()) {
                return (rs.getInt("id") + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void saveZeitung(BookMeta item, Player p) {
        p.sendMessage(prefix + "Die Zeitung wird gespeichert...");
        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        item.setDisplayName("Zeitung [" + getNextID() + ". Auflage]");
        item.setGeneration(null);
        item.setAuthor("News Redaktion");
        ItemStack i = new ItemStack(Material.WRITTEN_BOOK);
        i.setItemMeta(item);
        zeitung = i;
        if(zeitung == null) Debug.debug("zeitung is null");
        else Debug.debug(zeitung.getItemMeta().getDisplayName());
        StringBuilder text = new StringBuilder();
        for (String seite : item.getPages()) {
            if (seite != null) {
                text.append(ChatColor.stripColor(seite)).append("/{new_page}/");
            }
        }
        Debug.debug("text: " + text.toString());
        text = new StringBuilder(text.toString().replace("'", "`"));
        Script.executeUpdate("INSERT INTO zeitung (time, herrausbringer, content) VALUES ('" + System.currentTimeMillis() + "', " + Script.getNRPID(p) + ", '" + text.substring(0, text.length() - 12) + "');");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (Beruf.getBeruf(p) != Beruf.Berufe.NEWS) {
            p.sendMessage(Messages.ERROR + "Du bist kein Mitglied der News.");
            return true;
        }

        if (!Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        if (p.getInventory().getItemInMainHand().getType().equals(Material.WRITTEN_BOOK)) {
            if (cache == null) {
                cache = (BookMeta) p.getInventory().getItemInMainHand().getItemMeta();
            }
            if (cache.getPages().size() > 5) {
                Inventory inv = p.getServer().createInventory(null, 9, "§6Zeitung");
                inv.setItem(3, Script.setName(new ItemStack(Material.EMERALD_BLOCK), "§aFertig"));
                inv.setItem(5, Script.setName(new ItemStack(Material.REDSTONE_BLOCK), "§cNicht fertig"));
                p.openInventory(inv);
            } else {
                p.sendMessage(prefix + "Die Zeitung ist zu kurz.");
            }
        }
        return true;
    }

    @EventHandler
    public void onEdit(PlayerEditBookEvent e) {
        Player p = e.getPlayer();
        if (Beruf.getBeruf(p) != Beruf.Berufe.NEWS) {
            p.sendMessage(Messages.ERROR + "Du bist kein Mitglied der News.");
            return;
        }

        if (!Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return;
        }

        if (e.getNewBookMeta().hasAuthor()) {
            if (cache == null) {
                cache = e.getNewBookMeta();
            }
            if (e.getNewBookMeta().getPageCount() > 5) {
                if (e.getNewBookMeta().getPages().size() > 5) {
                    Inventory inv = p.getServer().createInventory(null, 9, "§6Zeitung");
                    inv.setItem(3, Script.setName(new ItemStack(Material.EMERALD_BLOCK), "§aFertig"));
                    inv.setItem(5, Script.setName(new ItemStack(Material.REDSTONE_BLOCK), "§cNicht fertig"));
                    p.openInventory(inv);
                } else {
                    e.setSigning(false);
                    p.sendMessage(prefix + "Die Zeitung ist zu kurz.");
                }
            } else {
                e.setSigning(false);
                p.sendMessage(prefix + "Die Zeitung ist zu kurz.");
            }
        } else {
            Debug.debug("book has no author");
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§6Zeitung")) {
            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);
            e.getView().close();
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                if (is.getItemMeta().getDisplayName().equals("§aFertig")) {
                    if (cache.getPages().size() > 5) {
                        saveZeitung(cache, p);
                        cache = null;
                    }
                } else if (is.getItemMeta().getDisplayName().equals("§cNicht fertig")) {
                    p.sendMessage(prefix + "Okay, die Zeitung ist also noch nicht fertig.");
                    cache = null;
                }
            }
        }
    }

}
