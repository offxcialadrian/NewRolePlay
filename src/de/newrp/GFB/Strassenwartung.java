package de.newrp.GFB;

import de.newrp.API.*;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Slab;
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

import java.text.DecimalFormat;
import java.util.HashMap;

public class Strassenwartung implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§aStrassenwartung§8] §a" + Messages.ARROW + " §7";
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Construction> construction = new HashMap<>();
    public static HashMap<Construction, String> CONSTRUCTION = new HashMap<>();
    public static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/straßenwartung");
            return true;
        }

        if (GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if (p.getLocation().distance(new Location(Script.WORLD, 475, 66, 1316, -36.769684f, 17.13762f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe des Straßenwartungsdepots.");
            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        if(Construction.getRandomConstruction() == null) {
            p.sendMessage(Messages.ERROR + "Es gibt gerade keine Baustellen. Bitte versuche es gleich erneut.");
            return true;
        }

        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);
        GFB.CURRENT.put(p.getName(), GFB.STRASSENWARTUNG);
        int count = GFB.STRASSENWARTUNG.getLevel(p) + Script.getRandom(4, 5);
        SCORE.put(p.getName(), count);
        TOTAL_SCORE.put(p.getName(), count);
        construction.put(p.getName(), Construction.getRandomConstruction());
        CONSTRUCTION.put(construction.get(p.getName()), p.getName());
        for (Location loc : construction.get(p.getName()).getLocations()) {
            loc.getBlock().setType(Material.AIR);
        }
        p.sendMessage(PREFIX + "Du hast den Job der Straßenwartung angenommen.");
        p.sendMessage(PREFIX + "Du musst " + count + " Straßen reparieren.");
        p.sendMessage(PREFIX + "Nächste Baustelle: " + construction.get(p.getName()).getName());
        p.sendMessage(Messages.INFO + "Begebe dich zur Baustelle die auf deinem Navi markiert ist und repariere die Straße.");
        p.sendMessage(Messages.INFO + "Du hast insgesamt " + 10 * GFB.STRASSENWARTUNG.getLevel(p) + " Minuten Zeit.");
        Cache.saveInventory(p);
        p.getInventory().clear();
        p.getInventory().addItem(new ItemBuilder(Material.ANDESITE_SLAB).setAmount(construction.get(p.getName()).getLocations().length).setName("§7Straßenblock").build());
        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), construction.get(p.getName()).getLocation(1)).start();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (construction.containsKey(p.getName())) {
                    int repaired_total = TOTAL_SCORE.get(p.getName()) - SCORE.get(p.getName());
                    for (Location loc : construction.get(p.getName()).getLocations()) {
                        if (loc.getBlock().getType() == Material.ANDESITE) {
                            loc.getBlock().setType(Material.ANDESITE_SLAB);
                            Slab block = (Slab) loc.getBlock();
                            block.setType(Slab.Type.BOTTOM);
                            continue;
                        }
                        loc.getBlock().setType(Material.AIR);
                    }
                    p.sendMessage(PREFIX + "Du hast zu lange gebraucht.");
                    p.sendMessage(PREFIX + "Du hast " + repaired_total + " Straßen repariert.");
                    PayDay.addPayDay(p, GFB.STRASSENWARTUNG.getLevel(p) * Script.getRandom(1, 2) * repaired_total);
                    GFB.STRASSENWARTUNG.addExp(p, GFB.STRASSENWARTUNG.getLevel(p) * Script.getRandom(3, 4) * repaired_total);
                    Script.addEXP(p, GFB.STRASSENWARTUNG.getLevel(p) * Script.getRandom(1, 2) * repaired_total);
                    Cache.loadInventory(p);
                    GFB.CURRENT.remove(p.getName());
                    construction.remove(p.getName());
                    CONSTRUCTION.remove(construction.get(p.getName()));
                    SCORE.remove(p.getName());
                    TOTAL_SCORE.remove(p.getName());
                }
            }
        }.runTaskLater(main.getInstance(), (10L * GFB.STRASSENWARTUNG.getLevel(p)) * 60 * 20L);

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!construction.containsKey(p.getName())) return;
        if (e.getBlock().getType() != Material.ANDESITE_SLAB) return;
        if(e.getBlock().getLocation().getBlock().getType() == Material.ANDESITE_SLAB) {
            Slab slab = (Slab) e.getBlock().getBlockData();
            if(slab.getType() == Slab.Type.DOUBLE) {
                e.setCancelled(true);
                return;
            }
        }
        boolean part = false;
        for (Location loc : construction.get(p.getName()).getLocations()) {
            if (e.getBlock().getLocation().equals(loc)) {
                part = true;
                e.setCancelled(false);
                break;
            }
        }

        if (!part) {
            return;
        }


        for (Location loc : construction.get(p.getName()).getLocations()) {
            if (loc.getBlock().getType() != Material.ANDESITE_SLAB) {
                return;
            }
        }


        if (SCORE.get(p.getName()) == 1) {
            int repaired_total = TOTAL_SCORE.get(p.getName()) - SCORE.get(p.getName());
            p.sendMessage(PREFIX + "Du hast alle Straßen repariert.");
            PayDay.addPayDay(p, GFB.STRASSENWARTUNG.getLevel(p) + (repaired_total*3));
            Script.addEXP(p, GFB.STRASSENWARTUNG.getLevel(p) + (repaired_total*3));
            GFB.STRASSENWARTUNG.addExp(p, GFB.STRASSENWARTUNG.getLevel(p) + (repaired_total*2));
            Cache.loadInventory(p);
            GFB.CURRENT.remove(p.getName());
            construction.remove(p.getName());
            CONSTRUCTION.remove(construction.get(p.getName()));
            SCORE.remove(p.getName());
            TOTAL_SCORE.remove(p.getName());
            return;
        }

        if(Construction.getRandomConstruction() == null) {
            p.sendMessage(Messages.ERROR + "Leider gibt es derzeit keine Baustellen. Dein Job wurde beendet.");
            int repaired_total = TOTAL_SCORE.get(p.getName()) - SCORE.get(p.getName());
            p.sendMessage(PREFIX + "Du hast alle Straßen repariert.");
            PayDay.addPayDay(p, GFB.STRASSENWARTUNG.getLevel(p) + (repaired_total*3));
            Script.addEXP(p, GFB.STRASSENWARTUNG.getLevel(p) + (repaired_total*3));
            GFB.STRASSENWARTUNG.addExp(p, GFB.STRASSENWARTUNG.getLevel(p) + (repaired_total*2));
            Cache.loadInventory(p);
            GFB.CURRENT.remove(p.getName());
            construction.remove(p.getName());
            CONSTRUCTION.remove(construction.get(p.getName()));
            SCORE.remove(p.getName());
            TOTAL_SCORE.remove(p.getName());
            return;
        }


        CONSTRUCTION.remove(construction.get(p.getName()));
        construction.remove(p.getName());
        SCORE.put(p.getName(), SCORE.get(p.getName()) - 1);
        p.sendMessage(PREFIX + "Du hast noch " + SCORE.get(p.getName()) + " Straßen zu reparieren.");
        Construction construction1 = Construction.getRandomConstruction();
        construction.put(p.getName(), construction1);
        CONSTRUCTION.put(construction.get(p.getName()), p.getName());
        p.sendMessage(PREFIX + "Nächste Baustelle: " + construction.get(p.getName()).getName());
        p.sendMessage(Messages.INFO + "Begebe dich zur nächsten Baustelle.");
        for (Location locs : construction.get(p.getName()).getLocations()) {
            if (locs.getBlock().getType() == Material.ANDESITE) {
                locs.getBlock().setType(Material.ANDESITE_SLAB);
                Slab block = (Slab) locs.getBlock().getBlockData();
                block.setType(Slab.Type.BOTTOM);
                continue;
            }
            locs.getBlock().setType(Material.AIR);
        }
        p.getInventory().clear();
        p.getInventory().addItem(new ItemBuilder(Material.ANDESITE_SLAB).setAmount(construction.get(p.getName()).getLocations().length ).setName("§7Straßenblock").build());
        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), construction.get(p.getName()).getLocation(1)).start();

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!construction.containsKey(p.getName())) return;
        Cache.loadInventory(p);
        Strassenwartung.construction.remove(p.getName());
        for (Location loc : Strassenwartung.construction.get(p.getName()).getLocations()) {
            loc.getBlock().setType(Material.ANDESITE_SLAB);
        }
        Strassenwartung.CONSTRUCTION.remove(Strassenwartung.construction.get(p.getName()));
        Strassenwartung.SCORE.remove(p.getName());
        Strassenwartung.TOTAL_SCORE.remove(p.getName());
        p.sendMessage(GFB.PREFIX + "Du hast den Job §6Straßenwartung §7verlassen.");
    }

    public enum Construction {


        LOC2(2, "Nähe Polizei", new Location[]{
                new Location(Script.WORLD, 466, 67, 873),
                new Location(Script.WORLD, 467, 67, 874),
                new Location(Script.WORLD, 465, 67, 871),
                new Location(Script.WORLD, 471, 67, 869),
                new Location(Script.WORLD, 469, 67, 868),
                new Location(Script.WORLD, 468, 67, 872),
                new Location(Script.WORLD, 466, 67, 869),
                new Location(Script.WORLD, 465, 67, 868),
                new Location(Script.WORLD, 465, 67, 870),
                new Location(Script.WORLD, 469, 67, 875),
                new Location(Script.WORLD, 468, 67, 874),
                new Location(Script.WORLD, 466, 67, 875),
                new Location(Script.WORLD, 467, 67, 872),
                new Location(Script.WORLD, 473, 67, 869),
                new Location(Script.WORLD, 473, 67, 871),
                new Location(Script.WORLD, 472, 67, 873),
                new Location(Script.WORLD, 471, 67, 872),
                new Location(Script.WORLD, 471, 67, 874),
                new Location(Script.WORLD, 470, 67, 873),
                new Location(Script.WORLD, 471, 67, 875),
                new Location(Script.WORLD, 468, 67, 871),
                new Location(Script.WORLD, 470, 67, 871),
                new Location(Script.WORLD, 472, 67, 870),
                new Location(Script.WORLD, 473, 67, 872),
                new Location(Script.WORLD, 470, 67, 869),
                new Location(Script.WORLD, 468, 67, 869),
                new Location(Script.WORLD, 467, 67, 868),
                new Location(Script.WORLD, 471, 67, 868),
        }),


        LOC3(3, "Nähe Hafen", new Location[]{
                new Location(Script.WORLD, 984, 65, 1158),
                new Location(Script.WORLD, 984, 65, 1156),
                new Location(Script.WORLD, 983, 65, 1154),
                new Location(Script.WORLD, 981, 65, 1154),
                new Location(Script.WORLD, 982, 65, 1155),
                new Location(Script.WORLD, 982, 65, 1157),
                new Location(Script.WORLD, 980, 65, 1157),
                new Location(Script.WORLD, 979, 65, 1159),
                new Location(Script.WORLD, 981, 65, 1160),
                new Location(Script.WORLD, 980, 65, 1161),
                new Location(Script.WORLD, 978, 65, 1160),
                new Location(Script.WORLD, 981, 65, 1163),
                new Location(Script.WORLD, 983, 65, 1161),
                new Location(Script.WORLD, 983, 65, 1163),
                new Location(Script.WORLD, 984, 65, 1160),
                new Location(Script.WORLD, 984, 65, 1162),
                new Location(Script.WORLD, 984, 65, 1164),
                new Location(Script.WORLD, 982, 65, 1164),
                new Location(Script.WORLD, 980, 65, 1164),
                new Location(Script.WORLD, 979, 65, 1162),
                new Location(Script.WORLD, 981, 65, 1162),
                new Location(Script.WORLD, 982, 65, 1159),
                new Location(Script.WORLD, 981, 65, 1158),
                new Location(Script.WORLD, 978, 65, 1158),
                new Location(Script.WORLD, 981, 65, 1156),
                new Location(Script.WORLD, 985, 65, 1157),
                new Location(Script.WORLD, 983, 65, 1159),
                new Location(Script.WORLD, 983, 65, 1156),
                new Location(Script.WORLD, 985, 65, 1160),
                new Location(Script.WORLD, 985, 65, 1162),
                new Location(Script.WORLD, 985, 65, 1163),
                new Location(Script.WORLD, 980, 65, 1159),
                new Location(Script.WORLD, 979, 65, 1158),
                new Location(Script.WORLD, 983, 65, 1158),
                new Location(Script.WORLD, 979, 65, 1160)
        }),
        LOC4(4, "Nähe Stop&Go", new Location[] {
                new Location(Script.WORLD, 808, 66, 1353),
                new Location(Script.WORLD, 808, 66, 1354),
                new Location(Script.WORLD, 808, 66, 1355),
                new Location(Script.WORLD, 808, 66, 1356),
                new Location(Script.WORLD, 805, 66, 1356),
                new Location(Script.WORLD, 803, 66, 1355),
                new Location(Script.WORLD, 802, 66, 1356),
                new Location(Script.WORLD, 802, 66, 1354),
                new Location(Script.WORLD, 804, 66, 1354),
                new Location(Script.WORLD, 806, 66, 1353),
                new Location(Script.WORLD, 806, 66, 1355),
                new Location(Script.WORLD, 804, 66, 1356),
                new Location(Script.WORLD, 805, 66, 1354),
                new Location(Script.WORLD, 803, 66, 1357),
                new Location(Script.WORLD, 805, 66, 1357),
                new Location(Script.WORLD, 807, 66, 1355),
                new Location(Script.WORLD, 806, 66, 1356),
                new Location(Script.WORLD, 803, 66, 1353),
                new Location(Script.WORLD, 801, 66, 1353),
                new Location(Script.WORLD, 801, 66, 1355),
                new Location(Script.WORLD, 801, 66, 1357),
        }),

        LOC5(5, "Nähe Tierheim", new Location[]{
                new Location(Script.WORLD, 579, 68, 1106),
                new Location(Script.WORLD, 577, 68, 1107),
                new Location(Script.WORLD, 578, 68, 1108),
                new Location(Script.WORLD, 574, 68, 1108),
                new Location(Script.WORLD, 572, 68, 1108),
                new Location(Script.WORLD, 573, 68, 1107),
                new Location(Script.WORLD, 574, 68, 1106),
                new Location(Script.WORLD, 572, 68, 1106),
                new Location(Script.WORLD, 576, 68, 1106),
                new Location(Script.WORLD, 575, 68, 1107),
                new Location(Script.WORLD, 577, 68, 1109),
                new Location(Script.WORLD, 576, 68, 1110),
                new Location(Script.WORLD, 575, 68, 1109),
                new Location(Script.WORLD, 572, 68, 1110),
                new Location(Script.WORLD, 573, 68, 1109),
                new Location(Script.WORLD, 571, 68, 1109),
                new Location(Script.WORLD, 575, 68, 1111),
                new Location(Script.WORLD, 573, 68, 1112),
                new Location(Script.WORLD, 571, 68, 1112),
                new Location(Script.WORLD, 572, 68, 1111),
        }),

        LOC6(6, "Hankys", new Location[]{
                new Location(Script.WORLD, 572, 64, 1251),
                new Location(Script.WORLD, 572, 64, 1253),
                new Location(Script.WORLD, 572, 64, 1255),
                new Location(Script.WORLD, 574, 64, 1255),
                /*new Location(Script.WORLD, 574, 64, 1253),
                new Location(Script.WORLD, 573, 64, 1254),
                new Location(Script.WORLD, 573, 64, 1256),
                new Location(Script.WORLD, 575, 64, 1256),
                new Location(Script.WORLD, 576, 64, 1258),
                new Location(Script.WORLD, 576, 64, 1256),
                new Location(Script.WORLD, 574, 64, 1258),
                new Location(Script.WORLD, 572, 64, 1257),
                new Location(Script.WORLD, 574, 64, 1251),
                new Location(Script.WORLD, 575, 64, 1257),
                new Location(Script.WORLD, 574, 64, 1257),
                new Location(Script.WORLD, 571, 64, 1109),
                new Location(Script.WORLD, 577, 64, 1252),
                new Location(Script.WORLD, 577, 64, 1254),
                new Location(Script.WORLD, 577, 64, 1256),
                new Location(Script.WORLD, 577, 64, 1258),
                new Location(Script.WORLD, 577, 64, 1257),*/
        }),


        LOC7(7, "Nähe Barbershop", new Location[]{
                new Location(Script.WORLD, 616, 64, 1293),
                new Location(Script.WORLD, 614, 64, 1294),
                new Location(Script.WORLD, 614, 64, 1296),
                new Location(Script.WORLD, 615, 64, 1298),
                new Location(Script.WORLD, 616, 64, 1295),
                new Location(Script.WORLD, 616, 64, 1297),
                new Location(Script.WORLD, 615, 64, 1299),
                new Location(Script.WORLD, 613, 64, 1299),
                new Location(Script.WORLD, 612, 64, 1298),
                new Location(Script.WORLD, 614, 64, 1297),
                new Location(Script.WORLD, 615, 64, 1296),
                new Location(Script.WORLD, 616, 64, 1299),
                new Location(Script.WORLD, 614, 64, 1298),
                new Location(Script.WORLD, 614, 64, 1295),
                new Location(Script.WORLD, 615, 64, 1298),
                new Location(Script.WORLD, 613, 64, 1294),
                new Location(Script.WORLD, 613, 64, 1297),
                new Location(Script.WORLD, 615, 64, 1294),
                new Location(Script.WORLD, 612, 64, 1295),
                new Location(Script.WORLD, 612, 64, 1293),
                new Location(Script.WORLD, 614, 64, 1293),
                new Location(Script.WORLD, 613, 64, 1300),
                new Location(Script.WORLD, 612, 64, 1300),
                new Location(Script.WORLD, 614, 64, 1300),
        });

        private final int id;
        private final String name;
        private final Location[] locations;

        Construction(int id, String name, Location[] locations) {
            this.id = id;
            this.name = name;
            this.locations = locations;
        }

        public static Construction getRandomConstruction() {
            //get a random construction which is not in use but make it random
            Construction[] constructions = Construction.values();
            for (int i = 0; i < constructions.length; i++) {
                Construction construction = constructions[Script.getRandom(0, constructions.length - 1)];
                if (!CONSTRUCTION.containsKey(construction)) {
                    return construction;
                }
            }
            return null;
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
    }
}

