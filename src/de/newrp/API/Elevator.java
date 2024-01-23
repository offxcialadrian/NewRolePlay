package de.newrp.API;

import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Elevator implements Listener {

    public static final HashMap<String, Double> progress = new HashMap<>();

    public enum ElevatorAPI {
        X3(0, "X3", new Location[]{
                new Location(Script.WORLD, 677, 71, 992, 90.30029f, -0.8999349f),
                new Location(Script.WORLD, 677, 83, 992, 268.51172f, 1.3737835f),
                new Location(Script.WORLD, 677, 90, 991, 87.00944f, 4.9500804f),
                new Location(Script.WORLD, 677, 96, 991, 91.35059f, -0.29995036f),
                new Location(Script.WORLD, 677, 102, 991, 89.24286f, 0.90017503f),
                new Location(Script.WORLD, 678, 108, 991, 89.98947f, 2.100277f),
                new Location(Script.WORLD, 677, 114, 992, 90.590225f, 1.8002541f),
                new Location(Script.WORLD, 677, 120, 992, 90.59046f, 2.1000636f),
                new Location(Script.WORLD, 677, 126, 992, -269.4025f, 1.8001311f),
                new Location(Script.WORLD, 677, 132, 991, -270.00256f, 2.2501912f),
        }),
        AEKI(1, "AEKI", new Location[]{
                new Location(Script.WORLD, 683, 68, 914, 180.1789f, -1.8678606f),
                new Location(Script.WORLD, 683, 77, 914, 180.18433f, 0.2998221f),
                new Location(Script.WORLD, 683, 84, 914, 180.03833f, 0.2998364f),
                new Location(Script.WORLD, 683, 91, 914, 179.8894f, -0.45017368f),
                new Location(Script.WORLD, 683, 98, 914, 180.19122f, -0.6002175f),
                new Location(Script.WORLD, 683, 105, 914, 180.34047f, -1.0502068f),
                new Location(Script.WORLD, 683, 112, 914, -179.95894f, -0.15020083f),
                new Location(Script.WORLD, 683, 119, 914, -179.9613f, -0.4501556f),
                new Location(Script.WORLD, 683, 126, 914, -181.30792f, 0.59986407f),
                new Location(Script.WORLD, 683, 131, 914, -181.00977f, -1.4027208f),
                new Location(Script.WORLD, 683, 138, 914, -180.10474f, 0.74987704f)
        }),
        STADTHALLE(2, "Stadthalle", new Location[]{
                new Location(Script.WORLD, 545, 70, 991, -90.51386f, -3.9811409f),
                new Location(Script.WORLD, 545, 78, 992, -90.06347f, 1.4188595f)
        }),
        KRANKENHAUS(12, "Krankenhaus", new Location[]{ // Linker Eingang
                new Location(Script.WORLD, 268, 75, 1235, 270.25488f, -1.0502267f),
                new Location(Script.WORLD, 268, 83, 1235, 270.33887f, -1.650025f),
                new Location(Script.WORLD, 268, 91, 1235, 270.25684f, -0.3001627f),
        }),
        KRANKENHAUS_2(12, "Krankenhaus", new Location[]{ // Rechter Eingang
                new Location(Script.WORLD, 268, 75, 1231, 270.25684f, -1.6501942f),
                new Location(Script.WORLD, 268, 83, 1231, 270.39258f, -0.15001848f),
                new Location(Script.WORLD, 268, 91, 1231, 270.53516f, 1.1999671f),
        }),

        CASINO(13, "Casino", new Location[]{
                new Location(Script.WORLD, 780, 77, 851, 0.05621338f, 0.90009636f),
                new Location(Script.WORLD, 781, 85, 851, 0.37005615f, -0.29973233f),
                new Location(Script.WORLD, 781, 93, 851, 359.4635f, 6.528944E-5f),
                new Location(Script.WORLD, 780, 101, 851, 359.1611f, 2.3997068f),
                new Location(Script.WORLD, 781, 109, 851, -358.28638f, 0.7494683f),
        }),
        FPA(14, "FPA", new Location[]{
                new Location(Script.WORLD, 723, 73, 811, -359.73596f, -1.6499771f), // 0
                new Location(Script.WORLD, 723, 78, 812, -0.48876953f, 0.45008942f), // 1
                new Location(Script.WORLD, 723, 83, 812, -0.48876953f, -0.45000762f), // 2
                new Location(Script.WORLD, 723, 88, 812, -0.6398225f, 1.9499756f), // 3
                new Location(Script.WORLD, 723, 93, 812, 359.5089f, 0.7498441f), // 4
                new Location(Script.WORLD, 723, 98, 812, 359.057f, 0.7498243f), // 5
                new Location(Script.WORLD, 724, 103, 811, 0.26940918f, 0.44987455f), // 6
                new Location(Script.WORLD, 723, 108, 812, 359.229f, 0.44979924f), // 7
                new Location(Script.WORLD, 723, 115, 812, 358.4734f, 0.9002828f) // 8

        }),
        SelfS(14, "SelfS", new Location[]{
                new Location(Script.WORLD, 1017, 68, 1176, 0.3227539f, 0.45031723f), // 0
                new Location(Script.WORLD, 1017, 60, 1175, 0.39379883f, -0.2999653f), // -1
                new Location(Script.WORLD, 1017, 51, 1176, 0.2890625f, -7.0147216E-5f), // -2
                new Location(Script.WORLD, 1017, 42, 1176, 0.30078125f, -0.45009407f) // - 3
        }),
        KHN(17, "KHN", new Location[]{
                new Location(Script.WORLD, 347, 76, 1271, 271.12177f, 3.997005f), // 0
                new Location(Script.WORLD, 347, 83, 1271, 271.41467f, 4.4470463f)});

        int id;
        String name;
        Location[] locs;

        ElevatorAPI(int id, String name, Location[] locs) {
            this.id = id;
            this.name = name;
            this.locs = locs;
        }

        public Location getLocByEtage(int i) {
            return locs[i];
        }

        public Location[] getLocs() {
            return locs;
        }

        public Location getLocByLocID(int i) {
            return getLocs()[i];
        }

        public String getName() {
            return name;
        }

        public static ElevatorAPI getNearestElevator(int max_distance, Location loc) {
            int i = max_distance;
            ElevatorAPI elevator = null;
            for (ElevatorAPI elevatorAPI : ElevatorAPI.values()) {
                for (Location locs : elevatorAPI.getLocs()) {
                    if (loc.distance(locs) < i) {
                        i = (int) loc.distance(locs);
                        elevator = elevatorAPI;
                    }
                }
            }
            return elevator;
        }

        public void teleportToEtage(Player p, int i) {
            Location loc = getLocByEtage(i);
            p.teleport(loc);
        }

        public int getEtageByLoc(Location loc) {
            int i = 50000;
            int etage = 0;
            int getetage = 0;
            for (Location locs : getLocs()) {
                etage++;
                if (loc.distance(locs) < i) {
                    i = (int) loc.distance(locs);
                    getetage = etage;
                }
            }
            return getetage - 1;
        }

    }

    public static ElevatorAPI getElevatorByDriveLoc(Location loc) {
        for (ElevatorDoor.ElevatorDoors elevator : ElevatorDoor.ElevatorDoors.values()) {
            if (elevator.getDriveLoc().equals(loc)) {
                return elevator.getElevator();
            }
        }
        return null;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.STONE_BUTTON)) {
                if (ElevatorDoor.ElevatorDoors.getDoorByDriveLoc(e.getClickedBlock().getLocation()) == null) return;
                if(progress.containsKey(p.getName())) return;
                ElevatorAPI elevator = Elevator.getElevatorByDriveLoc(e.getClickedBlock().getLocation());
                if (elevator != null) {
                    openGUI(p);
                } else {
                    Debug.debug("elevator is null");
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(progress.containsKey(p.getName()) && !e.isCancelled()) {
                progress.remove(p.getName());
                p.sendMessage("§cDie Fahrt wurde abgebrochen, da du Schaden erlitten hast.");
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        Player p = (Player) e.getWhoClicked();
        ElevatorAPI elevator = ElevatorAPI.getNearestElevator(3, p.getLocation());
        if(!e.getView().getTitle().contains("Fahrstuhl")) return;
        if(elevator == null) {
            p.closeInventory();
            return;
        }
        int etage = elevator.getEtageByLoc(p.getLocation());
        if (e.getView().getTitle().equals("§c" + elevator.getName() + " Fahrstuhl")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getType().equals(Material.IRON_DOOR)) {
                p.sendMessage("§8[§c" + elevator.getName() + "§8] " + "§6Du hast die Türen geöffnet.");
                ElevatorDoor.openDoors(p, elevator, etage);
                p.closeInventory();
            } else if (e.getCurrentItem().getType().equals(Material.CHEST) && e.getCurrentItem().hasItemMeta()) {
                int current_etage = elevator.getEtageByLoc(p.getLocation());
                int ziel_etage = 0;
                if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§6EG")) {
                    ziel_etage = 0;
                } else {
                    ziel_etage = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName().replace("§6", "").replace(". Etage", ""));
                }
                final int Ziel_etage = ziel_etage;
                int way = Math.abs(Math.abs(etage) - Math.abs(Ziel_etage)) *3;
                progress.put(p.getName(), 0.0);
                p.sendMessage("§8[§c" + elevator.getName() + "§8] " + "§6Du fährst nun " + (Ziel_etage > 0 ? "in die " + Ziel_etage + ". Etage" : "ins EG") + "...");
                p.closeInventory();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (progress.containsKey(p.getName())) {
                            if (progress.get(p.getName()) < way) {
                                if (p.getLocation().distance(elevator.getLocByEtage(current_etage)) < 5) {
                                    progressBar(way, p);
                                } else {
                                    cancel();
                                    progress.remove(p.getName());
                                    p.closeInventory();
                                    Script.sendActionBar(p, "§cDie Fahrt wurde abgebrochen, da du dich zu weit entfernt hast.");
                                }
                            } else {
                                cancel();
                                progress.remove(p.getName());
                                elevator.teleportToEtage(p, Ziel_etage);
                                p.sendMessage("§8[§c" + elevator.getName() + "§8] " + "§6Du hast die Etage erreicht.");
                                ElevatorDoor.openDoors(p, elevator, Ziel_etage);
                            }
                        } else {
                            cancel();
                        }
                    }
                }.runTaskTimer(main.getInstance(), 20, 20);
            }
        }
    }

    public static void openGUI(Player p) {
        ElevatorAPI elevator = ElevatorAPI.getNearestElevator(5, p.getLocation());
        int etagen_amount = 0;
        Inventory inv;
        for (Location loc : elevator.getLocs()) {
            etagen_amount++;
        }


        if (etagen_amount <= 5) {
            inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§c" + elevator.getName() + " Fahrstuhl");
        } else {
            inv = Bukkit.createInventory(null, (etagen_amount<=9?9:18), "§c" + elevator.getName() + " Fahrstuhl");
        }
        int i = 0;
        String etage;

        for (Location loc : elevator.getLocs()) {
            if (i != 0) {
                etage = "§6" + i + ". Etage";
            } else {
                etage = "§6EG";
            }
            ItemStack no = new ItemStack(Material.IRON_DOOR);
            ItemMeta noMeta = no.getItemMeta();
            noMeta.setDisplayName("§6Tür öffnen");
            no.setItemMeta(noMeta);

            ItemStack is = new ItemStack(Material.CHEST);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(etage);
            is.setItemMeta(meta);

            if (i == elevator.getEtageByLoc(p.getLocation())) {
                inv.setItem(i, no);
                i++;
                continue;
            }
            inv.setItem(i, is);
            i++;
        }

        p.openInventory(inv);
    }

    public static void progressBar(double required_progress, Player p) {
        double current_progress = progress.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        progress.replace(p.getName(), progress.get(p.getName()) + 1.0);
        Script.sendActionBar(p, "§eFahren... §8» §a" + sb);
    }
}