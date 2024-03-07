package de.newrp.News;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
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
import java.util.ArrayList;

public class Zeitung implements CommandExecutor, Listener {
    public static final String prefix = "§8[§6Zeitung§8]§6 " + Messages.ARROW + " §7";
    public static ItemStack zeitung;
    public static ArrayList<Integer> zeitungIDs = new ArrayList<>();
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
        if (!Beruf.hasBeruf(p) && !SDuty.isSDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (Beruf.getBeruf(p) != Beruf.Berufe.NEWS && !SDuty.isSDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Mitglied der News.");
            return true;
        }

        if (!Beruf.isLeader(p, true) && !SDuty.isSDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("setprice")) {
            if(!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "Der Preis muss eine Zahl sein.");
                return true;
            }
            int price = Integer.parseInt(args[1]);
            if(price < 0) {
                p.sendMessage(Messages.ERROR + "Der Preis muss größer oder gleich 0 sein.");
                return true;
            }
            Script.executeUpdate("UPDATE zeitung_price SET price=" + price);
            p.sendMessage(prefix + "Der Preis wurde gesetzt.");
            return true;
        }

        if(args.length == 1) {
            if(!Script.isInt(args[0])) {
                p.sendMessage(Messages.ERROR + "Die ID muss eine Zahl sein.");
                return true;
            }
            int id = Integer.parseInt(args[0]);
            if(id <= 0) {
                p.sendMessage(Messages.ERROR + "Die ID muss größer als 0 sein.");
                return true;
            }

            if(zeitungIDs.contains(id)) {
                p.sendMessage(Messages.ERROR + "Du kannst eine Zeitung nur einmal am Tag abrufen.");
                return true;
            }

            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM zeitung WHERE id=" + id)) {
                if(rs.next()) {
                    String[] pages = rs.getString("content").split("/\\{new_page}/");
                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta bm = (BookMeta) book.getItemMeta();
                    bm.setGeneration(null);
                    bm.setAuthor("News Redaktion");
                    bm.setDisplayName("Zeitung [" + id + ". Auflage]");
                    bm.setTitle("Zeitung [" + id + ". Auflage]");
                    bm.setPages(pages);
                    book.setItemMeta(bm);
                    p.getInventory().addItem(book);
                    p.sendMessage(prefix + "Die Zeitung wurde deinem Inventar hinzugefügt.");
                    zeitungIDs.add(id);
                    return true;
                } else {
                    p.sendMessage(Messages.ERROR + "Die Zeitung wurde nicht gefunden.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

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
        if(!e.getNewBookMeta().getTitle().startsWith("Zeitung")) return;
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
                    Inventory inv = p.getServer().createInventory(null, 9, "§6Zeitung");
                    inv.setItem(3, Script.setName(new ItemStack(Material.EMERALD_BLOCK), "§aFertig"));
                    inv.setItem(5, Script.setName(new ItemStack(Material.REDSTONE_BLOCK), "§cNicht fertig"));
                    p.openInventory(inv);
            } else {
                e.setSigning(false);
                p.sendMessage(prefix + "Die Zeitung ist zu kurz.");
            }
        } else {
            Debug.debug("book has no author");
        }
    }

    public static int getLatestZeitungID() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM zeitung ORDER BY id DESC LIMIT 1")) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void restoreZeitung() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM zeitung WHERE id=" + getLatestZeitungID())) {
            if(rs.next()) {
                String[] pages = rs.getString("content").split("/\\{new_page}/");
                ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta bm = (BookMeta) book.getItemMeta();
                bm.setDisplayName("Zeitung [" + getLatestZeitungID() + ". Auflage]");
                bm.setGeneration(null);
                bm.setTitle("Zeitung [" + getLatestZeitungID() + ". Auflage]");
                bm.setAuthor("News Redaktion");
                bm.setPages(pages);
                book.setItemMeta(bm);
                zeitung = book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                    if (cache.getPageCount() > 5) {
                        saveZeitung(cache, p);
                        cache = null;
                    } else {
                        p.sendMessage(prefix + "Die Zeitung ist zu kurz.");
                    }
                } else if (is.getItemMeta().getDisplayName().equals("§cNicht fertig")) {
                    p.sendMessage(prefix + "Okay, die Zeitung ist also noch nicht fertig.");
                    cache = null;
                }
            }
        }
    }

    public static int getBuyPrice() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT price FROM zeitung_price")) {
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
