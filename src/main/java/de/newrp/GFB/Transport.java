package de.newrp.GFB;

import de.newrp.API.*;
import de.newrp.Shop.Shops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
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

import java.util.HashMap;

public class Transport implements CommandExecutor, Listener {

    public static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Long> STARTED = new HashMap<>();
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Shops> SHOP = new HashMap<>();
    public static HashMap<String, Long> cooldown2 = new HashMap<>();
    public static HashMap<String, Integer> SAFE_SCORE = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(GFB.PREFIX + "/transport");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 935, 66, 1078, 190.34898f, 22.349966f)) > 5) {
            p.sendMessage(GFB.PREFIX + "Du bist nicht in der Nähe des Hafens.");
            return true;
        }

        if(GFB.CURRENT.containsKey(p.getName()) && GFB.CURRENT.get(p.getName()) == GFB.TRANSPORT && !SCORE.containsKey(p.getName())) {
            int invSize = 0;
            for(Shops shop : Shops.values()) {
                if(shop.getLager() >= shop.getLagerSize()) continue;
                invSize++;
            }

            if(invSize == 0) {
                p.sendMessage(GFB.PREFIX + "Es gibt keine Shops, die Ware benötigen.");
                p.sendMessage(Messages.INFO + "Der Job wurde automatisch beendet. Du hast keinen Cooldown erhalten.");
                cooldown.remove(p.getName());
                cooldown2.remove(p.getName());
                GFB.CURRENT.remove(p.getName());
                return true;
            }

            invSize = (int) Math.ceil(invSize / 9.0) * 9;
            Inventory inv = Bukkit.createInventory(null, invSize, "§8» §eTransport");
            int i = 0;
            for(Shops shop : Shops.values()) {
                if(shop.getLager() >= shop.getLagerSize()) continue;
                inv.setItem(i++, new ItemBuilder(Material.NETHER_STAR).setName("§8» §e" + shop.getName()).build());
            }

            if(i == 0) {
                p.sendMessage(GFB.PREFIX + "Es gibt keine Shops, die Ware benötigen.");
                p.sendMessage(Messages.INFO + "Der Job wurde automatisch beendet. Du hast keinen Cooldown erhalten.");
                cooldown.remove(p.getName());
                cooldown2.remove(p.getName());
                GFB.CURRENT.remove(p.getName());
                return true;
            }
            p.openInventory(inv);

            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(GFB.PREFIX + "Du hast bereits einen Job.");
            return true;
        }

        if (Premium.hasPremium(p)) {
            cooldown.put(p.getName(), System.currentTimeMillis() + 15 * 60 * 1000L);
        } else {
            cooldown.put(p.getName(), System.currentTimeMillis() + 20 * 60 * 1000L);
        }
        GFB.CURRENT.put(p.getName(), GFB.TRANSPORT);
        p.sendMessage(GFB.PREFIX + "Du hast den Job §6Transport §7angenommen.");
        p.sendMessage(GFB.PREFIX + "Wähle nun ein Ziel aus.");

        int invSize = 0;
        for(Shops shop : Shops.values()) {
            if(shop.getLager() >= shop.getLagerSize()) continue;
            invSize++;
        }
        if(invSize == 0) {
            p.sendMessage(GFB.PREFIX + "Es gibt keine Shops, die Ware benötigen.");
            return true;
        }
        invSize = (int) Math.ceil(invSize / 9.0) * 9;
        Inventory inv = Bukkit.createInventory(null, invSize, "§8» §eTransport");
        int i = 0;
        for(Shops shop : Shops.values()) {
            if(shop.getLager() >= shop.getLagerSize()) continue;
            inv.setItem(i++, new ItemBuilder(Material.NETHER_STAR).setName("§8» §e" + shop.getName()).build());
        }

        if(i == 0) {
            p.sendMessage(GFB.PREFIX + "Es gibt keine Shops, die Ware benötigen.");
            return true;
        }
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if(!GFB.CURRENT.containsKey(p.getName())) return;
        if(!e.getView().getTitle().equalsIgnoreCase("§8» §eTransport")) return;
        e.setCancelled(true);

        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getType() == Material.AIR) return;

        Shops shop = Shops.getShop(e.getCurrentItem().getItemMeta().getDisplayName().replace("§8» §e", ""));
        if(shop == null) return;

        p.sendMessage(GFB.PREFIX + "Du hast dich für den Transport zum Shop §6" + shop.getName() + " §7entschieden.");
        p.sendMessage(GFB.PREFIX + "Du hast nun " + (GFB.TRANSPORT.getLevel(p)*5) + " Minuten Zeit, um die Ware zu transportieren.");
        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), shop.getBuyLocation()).start();
        p.sendMessage(Messages.INFO + "Klicke nun auf das Schild \"Lager\".");

        int score = Math.min(GFB.TRANSPORT.getLevel(p) + Script.getRandom(4, 7), shop.getLagerSize());

        p.sendMessage(GFB.PREFIX + "Du musst " + score + " Waren transportieren.");
        SAFE_SCORE.put(p.getName(), score);
        SCORE.put(p.getName(), score);
        STARTED.put(p.getName(), System.currentTimeMillis());
        SHOP.put(p.getName(), shop);
        p.closeInventory();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        GFB.CURRENT.remove(p.getName());
        if(SCORE.containsKey(p.getName())) SCORE.remove(p.getName());
        if(STARTED.containsKey(p.getName())) STARTED.remove(p.getName());
        if(SAFE_SCORE.containsKey(p.getName())) SAFE_SCORE.remove(p.getName());
        if(SHOP.containsKey(p.getName())) SHOP.remove(p.getName());
        if(cooldown.containsKey(p.getName())) cooldown.remove(p.getName());
    }

    @EventHandler
    public void onClickSign(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        if(e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() != Material.OAK_SIGN && e.getClickedBlock().getType() != Material.OAK_WALL_SIGN) return;
        if(!(e.getClickedBlock().getState() instanceof Sign)) return;
        if(!((Sign) e.getClickedBlock().getState()).getLine(2).equalsIgnoreCase("§lLager")) return;
        if(!SCORE.containsKey(p.getName())) return;
        if(!STARTED.containsKey(p.getName())) return;
        if(SCORE.get(p.getName()) == 0) return;
        if(p.getLocation().distance(SHOP.get(p.getName()).getLocation())>20) {
            p.sendMessage(GFB.PREFIX + "Du bist nicht in der Nähe des Shops.");
            return;
        }
        if(STARTED.get(p.getName()) + (GFB.TRANSPORT.getLevel(p)* 5L) * 60 * 1000L < System.currentTimeMillis()) {
            p.sendMessage(GFB.PREFIX + "Du hast zu lange gebraucht.");
            GFB.CURRENT.remove(p.getName());
            SCORE.remove(p.getName());
            STARTED.remove(p.getName());
            SAFE_SCORE.remove(p.getName());
            SHOP.remove(p.getName());
            cooldown2.remove(p.getName());
            return;
        }

        SHOP.get(p.getName()).addLager(Script.getRandom(4,5));
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        if(cooldown2.containsKey(p.getName())) {
            if(cooldown2.get(p.getName()) > System.currentTimeMillis()) {
                Script.sendActionBar(p, Messages.ERROR + "Warte kurz...");
                return;
            }
        }
        SCORE.put(p.getName(), SCORE.get(p.getName()) - 1);
        p.sendMessage(GFB.PREFIX + "Du hast noch " + SCORE.get(p.getName()) + " Waren zu transportieren.");
        cooldown2.put(p.getName(), System.currentTimeMillis() + 1000L);
        if(SCORE.get(p.getName()) == 0) {
            p.sendMessage(GFB.PREFIX + "Du hast den Transport erfolgreich abgeschlossen.");
            int add = GFB.TRANSPORT.getLevel(p) + SAFE_SCORE.get(p.getName());
            GFB.TRANSPORT.addExp(p, add);
            PayDay.addPayDay(p, add*6);
            Script.addEXP(p, add*4);
            GFB.CURRENT.remove(p.getName());
            SCORE.remove(p.getName());
            STARTED.remove(p.getName());
            SAFE_SCORE.remove(p.getName());
            SHOP.remove(p.getName());
            cooldown2.remove(p.getName());
        }
    }

}
