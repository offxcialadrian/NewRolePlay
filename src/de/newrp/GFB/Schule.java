package de.newrp.GFB;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Schule implements CommandExecutor, Listener {

    public static HashMap<Player, GFB> STUDIYING = new HashMap<>();
    public static HashMap<Player, Long> STARTED = new HashMap<>();
    public static String PREFIX = "§8[§6Schule§8] §6» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(p.getLocation().distance(new Location(Script.WORLD, 731, 67, 750, 108.154785f, 36.79814f))>5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe der Schule.");
            return true;
        }

        if(STARTED.containsKey(p)) {
            long time = System.currentTimeMillis();
            long started = STARTED.get(p);
            long diff = time - started;
            long mins = (15 * 60 * 1000 - diff) / 1000 / 60;
            long secs = (15 * 60 * 1000 - diff) / 1000 % 60;
            p.sendMessage(Messages.ERROR + "Du lernst bereits für den GFB-Job " + STUDIYING.get(p).getName() + ".");
            p.sendMessage(Messages.INFO + "Du musst noch " + mins + " Minuten und " + secs + " Sekunden lernen.");
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/schule");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§8» §eSchule");
        int i = 0;
        for(GFB gfb : GFB.values()) {
            inv.setItem(i++, new ItemBuilder(Material.PAPER).setName("§8» §e" + gfb.getName()).setLore("§8» §7Preis: " + gfb.getLevel(p)*500 + "€").build());
        }
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(e.getView().getTitle().equals("§8» §eSchule")) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            GFB gfb = GFB.getGFBByName(e.getCurrentItem().getItemMeta().getDisplayName().replace("§8» §e", ""));
            if(gfb == null) return;
            if(Script.getMoney(p, PaymentType.BANK) < gfb.getLevel(p)*500) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld auf der Bank.");
                return;
            }

            Script.removeMoney(p, PaymentType.BANK, gfb.getLevel(p)*500);
            STUDIYING.put(p, gfb);
            STARTED.put(p, System.currentTimeMillis());
            p.sendMessage(PREFIX + "Du lernst nun für den GFB-Job " + gfb.getName() + ".");
            p.sendMessage(Messages.INFO + "Gehe innerhalb der nächsten 15 Minuten nicht in den AFK-Modus um den Kurs zu bestehen.");
            p.closeInventory();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(STUDIYING.containsKey(p)) {
                        p.sendMessage(PREFIX + "§aDu hast den Kurs bestanden.");
                        gfb.addExp(p, Script.getRandom(100, 200));
                        STUDIYING.remove(p);
                        STARTED.remove(p);
                    }
                }
            }.runTaskLater(main.getInstance(), 20 * 60 * 15);

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(STUDIYING.containsKey(p)) {
            p.sendMessage(PREFIX + "§cDu hast den Kurs nicht bestanden.");
            STUDIYING.remove(p);
            STARTED.remove(p);
        }
    }

}

