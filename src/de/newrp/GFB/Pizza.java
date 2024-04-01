package de.newrp.GFB;

import de.newrp.API.*;
import de.newrp.House.House;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Pizza implements CommandExecutor, Listener {

    public static final String PREFIX = "§8[§cPizza§8] §c" + Messages.ARROW + " §7";
    public static final HashMap<String, Long> cooldown = new HashMap<>();
    public static final HashMap<String, Integer> pizza = new HashMap<>();
    public static final HashMap<String, House> house = new HashMap<>();
    public static final HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();
    public static final HashMap<String, Long> timer = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/pizzalieferant");
            return true;
        }

        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if(cooldown.containsKey(p.getName())) {
            if(cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 637, 69, 884, 66.48648f, 9.386628f))> 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Pizzareia.");
            return true;
        }

        GFB.CURRENT.put(p.getName(), GFB.PIZZALIEFERANT);
        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);
        p.sendMessage(PREFIX + "Du hast den Job als §6Pizzalieferant §7angenommen.");
        p.sendMessage(Messages.INFO + "Gehe nun in die Küche und nehme die Pizza aus dem Ofen (Rechtsklick).");
        int total = GFB.PIZZALIEFERANT.getLevel(p) + Script.getRandom(2,3);
        pizza.put(p.getName(), total);
        TOTAL_SCORE.put(p.getName(), total);

        return false;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        if(!TOTAL_SCORE.containsKey(p.getName())) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() != Material.FURNACE) return;
        if(!e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 636, 69, 886)) && !e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 637, 69, 886))) return;
        e.setCancelled(false);
        Furnace f = (Furnace) e.getClickedBlock().getState();
        f.getInventory().setResult(Script.getHead(27490));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        if(e.getInventory().getType() != InventoryType.FURNACE) return;
        if(e.getInventory().getLocation() == null) return;
        if(!e.getInventory().getLocation().equals(new Location(Script.WORLD, 636, 69, 886)) && !e.getInventory().getLocation().equals(new Location(Script.WORLD, 637, 69, 886))) return;
        e.setCancelled(true);
        e.getView().close();

        if(house.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Liefere erstmal die aktuelle Pizza aus.");
            return;
        }

        house.remove(p.getName());
        timer.remove(p.getName());

        Route.invalidate(p);

        p.sendMessage(PREFIX + "Du hast eine Pizza aus dem Ofen genommen.");
        house.put(p.getName(), House.getRandomHouse());
        timer.put(p.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((int) p.getLocation().distance(house.get(p.getName()).getSignLocation())));
        p.sendMessage(PREFIX + "§8=== §6Pizza §8===\n" +
                PREFIX + "§8» §7Adresse: §6Haus " + house.get(p.getName()).getID() + "\n" +
                PREFIX + "§8» §7Kunde: §6" + getRandomPlayer(house.get(p.getName())).getName() + "\n" +
                PREFIX + "§8» §7Zeit: §6" + Script.getRemainingTime(timer.get(p.getName())) + "\n" +
                PREFIX + "§8» §7Preis: §6" + Script.getRandom(10, 20) + "€");
        p.sendMessage(Messages.INFO + "Klicke Rechtsklick auf das Hausschild, sobald du vor dem Haus bist.");
        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), house.get(p.getName()).getSignLocation()).start();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!house.containsKey(p.getName())) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() != Material.OAK_SIGN && e.getClickedBlock().getType() != Material.OAK_WALL_SIGN) return;
        if(!e.getClickedBlock().getLocation().equals(house.get(p.getName()).getSignLocation())) return;

        if(timer.get(p.getName()) < System.currentTimeMillis()) {
            pizza.replace(p.getName(), pizza.get(p.getName()) - 1);
            p.sendMessage(PREFIX + "Du hast die Pizza nicht rechtzeitig ausgeliefert und erhältst kein Gehalt für diese Auslieferung.");
            p.sendMessage(Messages.INFO + "Gehe nun in die Küche und nehme die nächste Pizza aus dem Ofen (Rechtsklick).");
            if(pizza.get(p.getName()) <= 0) {
                p.sendMessage(PREFIX + "Du hast alle Pizzen erfolgreich ausgeliefert.");
                GFB.PIZZALIEFERANT.addExp(p, GFB.PIZZALIEFERANT.getLevel(p) + (TOTAL_SCORE.get(p.getName()))*2);
                PayDay.addPayDay(p, (GFB.PIZZALIEFERANT.getLevel(p) + (TOTAL_SCORE.get(p.getName()))*8));
                Script.addEXP(p, GFB.PIZZALIEFERANT.getLevel(p) + (TOTAL_SCORE.get(p.getName()))*4);
                pizza.remove(p.getName());
                timer.remove(p.getName());
                GFB.CURRENT.remove(p.getName());
                TOTAL_SCORE.remove(p.getName());
                return;
            } else {
                p.sendMessage(PREFIX + "Du hast noch " + pizza.get(p.getName()) + " Pizzen zu liefern.");
                p.sendMessage(Messages.INFO + "Gehe nun in die Küche und nehme die nächste Pizza aus dem Ofen (Rechtsklick).");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                house.remove(p.getName());
                timer.remove(p.getName());
                TOTAL_SCORE.replace(p.getName(), TOTAL_SCORE.get(p.getName()) - 1);
                new Route(p.getName(), Script.getNRPID(p), p.getLocation(),new Location(Script.WORLD, 637, 69, 884)).start();
            }
            return;
        }

        p.sendMessage(PREFIX + "Du hast die Pizza erfolgreich ausgeliefert.");
        house.remove(p.getName());
        timer.remove(p.getName());
        pizza.replace(p.getName(), pizza.get(p.getName()) - 1);

        if(Script.getRandom(1, 100) <= 20) {
            p.sendMessage(PREFIX + "Der Kunde war sehr zufrieden und hat dir ein Trinkgeld gegeben.");
            Script.addMoney(p, PaymentType.CASH, Script.getRandom(1, 3));
        }

        if(pizza.get(p.getName()) <= 0) {
            p.sendMessage(PREFIX + "Du hast alle Pizzen erfolgreich ausgeliefert.");
            GFB.PIZZALIEFERANT.addExp(p, GFB.PIZZALIEFERANT.getLevel(p) + (TOTAL_SCORE.get(p.getName()))*2);
            PayDay.addPayDay(p, (GFB.PIZZALIEFERANT.getLevel(p) + (TOTAL_SCORE.get(p.getName()))*8));
            Script.addEXP(p, GFB.PIZZALIEFERANT.getLevel(p) + (TOTAL_SCORE.get(p.getName()))*8);
            pizza.remove(p.getName());
            timer.remove(p.getName());
            GFB.CURRENT.remove(p.getName());
            TOTAL_SCORE.remove(p.getName());
            return;
        }

        p.sendMessage(PREFIX + "Du hast noch " + pizza.get(p.getName()) + " Pizzen zu liefern.");
        p.sendMessage(Messages.INFO + "Gehe nun in die Küche und nehme die nächste Pizza aus dem Ofen (Rechtsklick).");
        new Route(p.getName(), Script.getNRPID(p), p.getLocation(),new Location(Script.WORLD, 637, 69, 884)).start();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(GFB.CURRENT.containsKey(p.getName())) {
            GFB.CURRENT.remove(p.getName());
        }
        if(cooldown.containsKey(p.getName())) {
            cooldown.remove(p.getName());
        }
        if(pizza.containsKey(p.getName())) {
            pizza.remove(p.getName());
        }
        if(house.containsKey(p.getName())) {
            house.remove(p.getName());
        }
        if(TOTAL_SCORE.containsKey(p.getName())) {
            TOTAL_SCORE.remove(p.getName());
        }
        if(timer.containsKey(p.getName())) {
            timer.remove(p.getName());
        }
    }

    public static OfflinePlayer getRandomPlayer(House h) {
        return Script.getOfflinePlayer(h.getOwner());
    }

}
