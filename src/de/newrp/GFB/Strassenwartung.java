package de.newrp.GFB;

import de.newrp.API.*;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Strassenwartung implements CommandExecutor, Listener {

    public enum Construction {
        LOC1(1, "Nähe Stadthalle", new Location[] {}),
        LOC2(2, "Nähe Stadthalle", new Location[] {});

        private final int id;
        private final String name;
        private final Location[] locations;

        Construction(int id, String name, Location[] locations) {
            this.id = id;
            this.name = name;
            this.locations = locations;
        }

        public int getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Location[] getLocations() {
            return locations;
        }

        public Location getLocation(int id) {
            return locations[id];
        }

        public Construction getLocation(String name) {
            for (Construction location : Construction.values()) {
                if (location.getName().equalsIgnoreCase(name)) {
                    return location;
                }
            }
            return null;
        }

        public static Construction getRandomConstruction() {
            return Construction.values()[(int) (Math.random() * Construction.values().length)];
        }
    }

    public static String PREFIX = "§8[§aStrassenwartung§8] §a" + Messages.ARROW + " §7";
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Construction> construction = new HashMap<>();
    public static HashMap<Construction, String> CONSTRUCTION = new HashMap<>();
    public static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();


    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/straßenwartung");
            return true;
        }

        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 475, 66, 1316, -36.769684f, 17.13762f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe des Straßenwartungsdepots.");
            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);
        GFB.CURRENT.put(p.getName(), GFB.BURGERFRYER);
        int count = GFB.BURGERFRYER.getLevel(p) * Script.getRandom(2, 3);
        SCORE.put(p.getName(), count);
        TOTAL_SCORE.put(p.getName(), count);
        construction.put(p.getName(), Construction.getRandomConstruction());
        CONSTRUCTION.put(construction.get(p.getName()), p.getName());
        for(Location loc : construction.get(p.getName()).getLocations()) {
            loc.getBlock().setType(Material.AIR);
        }
        p.sendMessage(PREFIX + "Du hast den Job der Straßenwartung angenommen.");
        p.sendMessage(PREFIX + "Du musst " + count + " Straßen reparieren.");
        p.sendMessage(Messages.INFO + "Begebe dich zur Baustelle die auf deinem Navi markiert ist und repariere die Straße.");
        p.sendMessage(Messages.INFO + "Du hast insgesamt " + 10*GFB.STRASSENWARTUNG.getLevel(p) + " Minuten Zeit.");
        Cache.saveInventory(p);
        p.getInventory().clear();
        p.getInventory().addItem(new ItemBuilder(Material.ANDESITE_SLAB).setAmount(count).setName("§7Straßenblock").build());
        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), construction.get(p.getName()).getLocation(1)).start();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(construction.containsKey(p.getName())) {
                    int repaired_total = TOTAL_SCORE.get(p.getName())-SCORE.get(p.getName());
                    for(Location loc : construction.get(p.getName()).getLocations()) {
                        loc.getBlock().setType(Material.ANDESITE_SLAB);
                    }
                    p.sendMessage(PREFIX + "Du hast zu lange gebraucht.");
                    p.sendMessage(PREFIX + "Du hast " + repaired_total + " Straßen repariert.");
                    Script.addMoney(p, PaymentType.BANK, GFB.STRASSENWARTUNG.getLevel(p)*Script.getRandom(1,2)*repaired_total);
                    Cache.loadInventory(p);
                    GFB.CURRENT.remove(p.getName());
                    construction.remove(p.getName());
                    CONSTRUCTION.remove(construction.get(p.getName()));
                    SCORE.remove(p.getName());
                    TOTAL_SCORE.remove(p.getName());
                }
            }
        }.runTaskLater(main.getInstance(), (10L *GFB.STRASSENWARTUNG.getLevel(p)) * 60 * 20L);

        return false;
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if(!construction.containsKey(p.getName())) return;
        if(e.getBlock().getType() != Material.ANDESITE_SLAB) return;
        for(Location loc : construction.get(p.getName()).getLocations()) {
            if(e.getBlock().getLocation().equals(loc)) {
                e.setCancelled(false);
                SCORE.put(p.getName(), SCORE.get(p.getName())-1);
                if(SCORE.get(p.getName()) == 0) {
                    int repaired_total = TOTAL_SCORE.get(p.getName())-SCORE.get(p.getName());
                    p.sendMessage(PREFIX + "Du hast alle Straßen repariert.");
                    Script.addMoney(p, PaymentType.BANK, GFB.STRASSENWARTUNG.getLevel(p)*Script.getRandom(1,2)*repaired_total);
                    Cache.loadInventory(p);
                    GFB.CURRENT.remove(p.getName());
                    construction.remove(p.getName());
                    CONSTRUCTION.remove(construction.get(p.getName()));
                    SCORE.remove(p.getName());
                    TOTAL_SCORE.remove(p.getName());
                    return;
                }

                p.sendMessage(PREFIX + "Du hast noch " + SCORE.get(p.getName()) + " Straßen zu reparieren.");
                p.sendMessage(Messages.INFO + "Begebe dich zur nächsten Baustelle. Du hast wieder 10 Minuten Zeit.");
                construction.put(p.getName(), Construction.getRandomConstruction());
                CONSTRUCTION.put(construction.get(p.getName()), p.getName());
                for(Location locs : construction.get(p.getName()).getLocations()) {
                    locs.getBlock().setType(Material.AIR);
                }
                new Route(p.getName(), Script.getNRPID(p), p.getLocation(), construction.get(p.getName()).getLocation(1)).start();

                return;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!construction.containsKey(p.getName())) return;
        Strassenwartung.construction.remove(p.getName());
        for (Location loc : Strassenwartung.construction.get(p.getName()).getLocations()) {
            loc.getBlock().setType(Material.ANDESITE_SLAB);
        }
        Strassenwartung.CONSTRUCTION.remove(Strassenwartung.construction.get(p.getName()));
        Strassenwartung.SCORE.remove(p.getName());
        Strassenwartung.TOTAL_SCORE.remove(p.getName());
        Cache.loadInventory(p);
        p.sendMessage(GFB.PREFIX + "Du hast den Job §6Straßenwartung §7verlassen.");
    }

}
