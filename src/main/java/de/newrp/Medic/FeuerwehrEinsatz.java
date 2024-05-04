package de.newrp.Medic;

import de.newrp.API.*;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.House.House;
import de.newrp.Shop.Shops;
import de.newrp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class FeuerwehrEinsatz {
    public static final ArrayList<Block> onFire = new ArrayList<>();
    public static final HashMap<Shops, Long> biz_cooldown = new HashMap<>();
    public static final String PREFIX = "§8[§4Einsatz§8]§4 " + Messages.ARROW + " §7";
    public static Shops business = null;
    public static House house;

    public FeuerwehrEinsatz(House haus) {
        house = haus;
    }

    public static House getHaus() {
        return house;
    }

    public static void removeFire(Block b) {
        if (onFire.contains(b)) {
            b.setType(Material.AIR);
            new Particle(org.bukkit.Particle.SMOKE_LARGE, b.getLocation(), false, 0.5F, 0.5F, 0.5F, 0, 5);
            onFire.remove(b);
        }
        if (onFire.isEmpty())
            Abteilung.Abteilungen.FEUERWEHR.sendMessage(PREFIX + "Das Feuer wurde erfolgreich gelöscht, over.");
    }

    public static void removeTotalFire() {
        removeTotalFire(true);
    }

    public static void removeTotalFire(boolean scheduled) {
        Runnable runnable = () -> {
            for (Block b : onFire) {
                b.setType(Material.AIR);
            }
            onFire.clear();
        };

        if (scheduled) {
            Bukkit.getScheduler().runTask(Main.getInstance(), runnable);
        } else {
            runnable.run();
        }
    }

    public void start() {
        if (Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers().size() >= 2) {
            if (getHaus() == null) return;
            removeTotalFire();
            Beruf.Berufe.NEWS.sendMessage(Beruf.PREFIX + "Es wurde ein Feuer bei Haus " + getHaus().getID() + " gemeldet!");
            Abteilung.Abteilungen.FEUERWEHR.sendMessage(FeuerwehrEinsatz.PREFIX + "§4Brand 3, Haus:" + getHaus().getID() + ", Alle verfügbaren Einheiten!");
            Location loc = house.getSignLocation();
            ArrayList<Block> fire = new ArrayList<>();
            int radius = Script.getRandom(8, 10);
            Block block = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()).getBlock();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            int minX = x - radius;
            int minY = y - 5;
            int minZ = z - radius;
            int maxX = x + radius;
            int maxY = y + 5;
            int maxZ = z + radius;
            for (int counterX = minX; counterX <= maxX; counterX++) {
                for (int counterY = minY; counterY <= maxY; counterY++) {
                    for (int counterZ = minZ; counterZ <= maxZ; counterZ++) {
                        Block blockName = loc.getWorld().getBlockAt(counterX, counterY, counterZ);
                        fire.add(blockName);
                    }
                }
            }
            int count = 0;
            for (Block b : fire) {
                if (count == 20) break;
                Location l = b.getLocation();
                count = 0;
                if (Script.getRandom(1, 5) == 1) {
                    while (!b.getType().equals(Material.AIR)) {
                        if (count > 6) break;
                        l.add(0, 1, 0);
                        count++;
                    }
                    if (l.getBlock().getType().equals(Material.AIR) && (l.getBlockY() - loc.getBlockY() < 5)) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            Block setFire = b.getWorld().getBlockAt(l);
                            setFire.setType(Material.FIRE);
                            if (setFire.getType().equals(Material.FIRE)) {
                                onFire.add(setFire);
                            }
                        });
                    }
                }
            }

            for (Player ff : Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers()) {
                new Route(ff.getName(), Script.getNRPID(ff), ff.getLocation(), loc).start();
            }
            Abteilung.Abteilungen.FEUERWEHR.sendMessage("§8» §cIn der Nähe ist§8:§6 " + Navi.getNextNaviLocation(loc));
            House haus = house;
            House.Mieter m = haus.getMieterByID(haus.getOwner());
            m.setNebenkosten(haus, m.getNebenkosten() + Script.getRandom(750, 2000));
        }
    }

    public void start(Shops biz) {
        business = biz;
        Beruf.Berufe.NEWS.sendMessage(Beruf.PREFIX + "Es wurde ein Feuer bei " + biz.getName() + " gemeldet!");
        Abteilung.Abteilungen.FEUERWEHR.sendMessage(FeuerwehrEinsatz.PREFIX + "§4Brand 4, " + biz.getName() + ", Alle verfügbaren Einheiten!");
        Location loc = biz.getBuyLocation();
        //    removeTotalFire();
        biz_cooldown.put(biz, System.currentTimeMillis());
        ArrayList<Block> fire = new ArrayList<>();
        int radius = Script.getRandom(8, 10);
        Block block = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()).getBlock();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        int minX = x - radius;
        int minY = y - 5;
        int minZ = z - radius;
        int maxX = x + radius;
        int maxY = y + 5;
        int maxZ = z + radius;
        for (int counterX = minX; counterX <= maxX; counterX++) {
            for (int counterY = minY; counterY <= maxY; counterY++) {
                for (int counterZ = minZ; counterZ <= maxZ; counterZ++) {
                    Block blockName = loc.getWorld().getBlockAt(counterX, counterY, counterZ);
                    fire.add(blockName);
                }
            }
        }
        int count = 0;
        for (Block b : fire) {
            if (count == 20) break;
            Location l = b.getLocation();
            count = 0;
            if (Script.getRandom(1, 5) == 1) {
                while (!b.getType().equals(Material.AIR)) {
                    if (count > 6) break;
                    l.add(0, 1, 0);
                    count++;
                }
                if (l.getBlock().getType().equals(Material.AIR) && (l.getBlockY() - loc.getBlockY() < 5)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        Block setFire = b.getWorld().getBlockAt(l);
                        setFire.setType(Material.FIRE);
                        if (setFire.getType().equals(Material.FIRE)) {
                            onFire.add(setFire);
                        }
                    });
                }
            }
        }
        for (Player ff : Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers()) {
            new Route(ff.getName(), Script.getNRPID(ff), ff.getLocation(), loc).start();
        }
        Abteilung.Abteilungen.FEUERWEHR.sendMessage("§8» §cIn der Nähe ist§8:§6 " + Navi.getNextNaviLocation(loc));
        biz.removeKasse(biz.getKasse());
        biz.removeLager(biz.getLager());
    }
}