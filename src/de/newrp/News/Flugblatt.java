package de.newrp.News;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
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
import org.jetbrains.annotations.NotNull;

public class Flugblatt implements CommandExecutor, Listener {

    public static ItemStack flugblatt;
    public static BookMeta cache = null;
    public static final String PREFIX = "§8[§6Flugblatt§8]§6 " + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.NEWS)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(flugblatt == null) {
            p.sendMessage(Messages.ERROR + "Das Flugblatt ist noch nicht gespeichert.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/flugblatt [Anzahl]");
            return true;
        }

        if(!Script.isInt(args[0])) {
            p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl an.");
            return true;
        }

        int amount = Integer.parseInt(args[0]);
        if(amount < 1) {
            p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl größer als 0 an.");
            return true;
        }

        BookMeta meta = (BookMeta) flugblatt.getItemMeta();
        assert meta != null;
        int pages = meta.getPageCount();

        if(Beruf.Berufe.NEWS.getKasse() < (amount*(pages*2))) {
            p.sendMessage(Messages.ERROR + "Die News hat nicht genug Geld.");
            return true;
        }

        for(int i = 0; i < amount; i++) {
            p.getInventory().addItem(flugblatt);
        }

        p.sendMessage(PREFIX + "Du hast " + amount + " Flugblätter drucken lassen.");
        Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(p) + " hat " + amount + " Flugblätter drucken lassen.");
        Beruf.Berufe.NEWS.removeKasse(amount*(pages*2));
        Beruf.Berufe.NEWS.sendLeaderMessage(PREFIX + "Das hat die News " + (amount*(pages*2) + "€ gekostet"));

        return false;
    }

    public static void saveFlugblatt(BookMeta item, Player p) {
        p.sendMessage(PREFIX + "Das Flugblatt wird gespeichert...");
        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        item.setDisplayName("Flugblatt");
        item.setGeneration(null);
        item.setAuthor("News Redaktion");
        ItemStack i = new ItemStack(Material.WRITTEN_BOOK);
        i.setItemMeta(item);
        flugblatt = i;
        if(flugblatt == null) Debug.debug("flugblatt is null");
        else Debug.debug(flugblatt.getItemMeta().getDisplayName());
        StringBuilder text = new StringBuilder();
        for (String seite : item.getPages()) {
            if (seite != null) {
                text.append(ChatColor.stripColor(seite)).append("/{new_page}/");
            }
        }
        Debug.debug("text: " + text.toString());
        text = new StringBuilder(text.toString().replace("'", "`"));
        Script.executeUpdate("INSERT INTO flugblatt (time, herrausbringer, content) VALUES ('" + System.currentTimeMillis() + "', " + Script.getNRPID(p) + ", '" + text.substring(0, text.length() - 12) + "');");
    }

    @EventHandler
    public void onEditBook(PlayerEditBookEvent e) {
        Player p = e.getPlayer();
        if(!e.getNewBookMeta().getTitle().startsWith("Flugbl")) return;
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
            Inventory inv = p.getServer().createInventory(null, 9, "§6Flugblatt");
            inv.setItem(3, Script.setName(new ItemStack(Material.EMERALD_BLOCK), "§aFertig"));
            inv.setItem(5, Script.setName(new ItemStack(Material.REDSTONE_BLOCK), "§cNicht fertig"));
            p.openInventory(inv);
        } else {
            Debug.debug("book has no author");
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§6Flugblatt")) {
            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);
            e.getView().close();
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                if (is.getItemMeta().getDisplayName().equals("§aFertig")) {
                        saveFlugblatt(cache, p);
                        cache = null;
                } else if (is.getItemMeta().getDisplayName().equals("§cNicht fertig")) {
                    p.sendMessage(PREFIX + "Okay, das Flugblatt ist also noch nicht fertig.");
                    cache = null;
                }
            }
        }
    }

}
