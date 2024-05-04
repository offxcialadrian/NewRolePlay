package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.Hotel;
import de.newrp.Player.Mobile;
import de.newrp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AkkuCommand implements CommandExecutor, Listener {

    private static final String AKKU = "§8[§6Akku§8] §6" + Messages.ARROW + " ";

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            Player p = e.getPlayer();
            ItemStack is = p.getInventory().getItemInMainHand();
            if (is.getType().equals(Material.NAME_TAG) && is.hasItemMeta() && is.getItemMeta().getDisplayName().equals("§3Powerbank")) {
                if (is.getItemMeta().getLore() != null) {
                    String raw = ChatColor.stripColor(is.getItemMeta().getLore().get(0));
                    String left = raw.split("/")[0];
                    int i = (Script.isInt(left) ? Integer.parseInt(left) : 0);
                    Inventory inv = p.getServer().createInventory(null, InventoryType.HOPPER, "§3Powerbank");
                    inv.setItem(1, Script.setName(Material.STONE_BUTTON, "§6Noch " + i + " " + (i == 1 ? "Ladung" : "Ladungen")));
                    inv.setItem(3, Script.setName(Material.STONE_BUTTON, "§6Handy aufladen"));
                    Script.fillInv(inv);
                    p.openInventory(inv);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§3Powerbank")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                e.getView().close();
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§6Handy aufladen")) {
                    String raw = e.getInventory().getItem(1).getItemMeta().getDisplayName();
                    String left = raw.split(" ")[1];
                    int i = (Script.isInt(left) ? Integer.parseInt(left) : 0);
                    if (i > 0) {
                        startRecharge(p, null);
                    } else {
                        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        p.sendMessage(AKKU + "§7Deine Powerbank ist leer.");
                    }
                } else {
                    String raw = e.getInventory().getItem(1).getItemMeta().getDisplayName();
                    String left = raw.split(" ")[1];
                    int i = (Script.isInt(left) ? Integer.parseInt(left) : 0);
                    p.sendMessage(AKKU + "§7Du kannst dein Handy noch " + i + " Mal mit der Powerbank aufladen.");
                }
            }
        }
    }


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/akku");
            return true;
        }

        if(!Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
            return true;
        }

        if(Beruf.hasBeruf(p)) {
            if(p.getLocation().distance(Beruf.getBeruf(p).getLoc())<10) {
                startRecharge(p, Beruf.getBeruf(p).getLoc());
                return true;
            }
        }

        if(Organisation.hasOrganisation(p)) {
            if(p.getLocation().distance(Organisation.getOrganisation(p).getDbank())<10) {
                startRecharge(p, Organisation.getOrganisation(p).getDbank());
                return true;
            }
        }

        if(!House.isInHouse(p) && !Hotel.isInHotelRoom(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Haus.");
            return true;
        }

        House h = House.getInsideHouse(p);
        if(h == null && !Hotel.isInHotelRoom(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Haus.");
            return true;
        }

        if(h != null) {
            if(!h.isInside(p)) {
                p.sendMessage(Messages.ERROR + "Du bist nicht in diesem Haus.");
                return true;
            }
        } else {
            if(!Hotel.isInHotelRoom(p)) {
                p.sendMessage(Messages.ERROR + "Du bist nicht in diesem Haus.");
                return true;
            }
        }

        startRecharge(p, p.getLocation());


        return false;
    }

    public static void startRecharge(Player p, Location loc) {
        p.sendMessage(AKKU + "Du hast begonnen, dein Handy aufzuladen.");
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!Mobile.hasPhone(p)) {
                    p.sendMessage(AKKU + "Du hast aufgehört, dein Handy aufzuladen.");
                    cancel();
                    return;
                }

                if(loc != null) {
                    if (p.getLocation().distance(loc) > 10) {
                        p.sendMessage(AKKU + "Du hast aufgehört, dein Handy aufzuladen.");
                        cancel();
                        return;
                    }
                }

                if(Mobile.getPhone(p).getAkku(p) >= Mobile.getPhone(p).getMaxAkku()) {
                    p.sendMessage(AKKU + "Dein Handy ist vollständig aufgeladen.");
                    cancel();
                    return;
                }

                Mobile.getPhone(p).addAkku(p, 1);
                double current_progress = Mobile.getPhone(p).getAkku(p);
                double progress_percentage = current_progress / Mobile.getPhone(p).getMaxAkku();
                StringBuilder sb = new StringBuilder();
                int bar_length = 10;
                for (int i = 0; i < bar_length; i++) {
                    if (i < bar_length * progress_percentage) {
                        sb.append("§e▉");
                    } else {
                        sb.append("§8▉");
                    }
                }
                Script.sendActionBar(p, "§eAufladen... §8» §a" + sb);

            }
        }. runTaskTimerAsynchronously(Main.getInstance(), Premium.hasPremium(p) ? 3L : 5L, Premium.hasPremium(p) ? 3L : 5L);
    }
}
