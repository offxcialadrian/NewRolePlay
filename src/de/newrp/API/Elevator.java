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

    private static final HashMap<String, Double> progress = new HashMap<>();

    public enum ElevatorAPI {
        X3(0, "X3", new Location[]{
                new Location(Script.WORLD, 677, 71, 992, 269.86963f,1.9499868f),
                new Location(Script.WORLD, 677, 83, 991, 271.0594f,0.62371284f),
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
        STADTHALLE(1, "Stadthalle", new Location[]{
                new Location(Script.WORLD, 158.84728054152365, 72.0625, 157.00266976620682, 90.08899f, 1.7017052f),
                new Location(Script.WORLD, 158.85873310514444, 80.0625, 157.00331873640653, 90.25882f, 1.361892f),
                new Location(Script.WORLD, 151.9346966195919, 89.0625, 157.03359206453865, 90.0894f, 0.16041285f),
                new Location(Script.WORLD, 151.81743255966438, 98.0625, 157.0097766732797, 89.919556f, 1.198105f),
                new Location(Script.WORLD, 151.69999998807907, 107.0, 156.98890079623362, 89.74982f, 0.85838294f),
        }),
        NOTAUFNAHME(2, "Notaufnahme", new Location[]{
                new Location(Script.WORLD, 223.50338177551546, 69.0, 208.5283757516812, -0.25895947f, 0.8398185f),
                new Location(Script.WORLD, 223.50338177551546, 75.0, 208.5283757516812, 0.080697f, 2.7079785f),
        }),
        NEWS(3, "News", new Location[]{
                new Location(Script.WORLD, -103.14341092870598, 19.0, -373.0438723706381, -89.40045f, 2.5384421f),
                new Location(Script.WORLD, -104.4608556244416, 69.0, -364.459107468096, -90.25056f, 3.0476258f),
        }),
        CHERRYS(4, "Cherrys", new Location[]{
                new Location(Script.WORLD, 38.94811672523405, 80.0, 431.3165495248266, -179.67859f, 0.6699593f),
                new Location(Script.WORLD, 39.021404512493916, 94.0, 431.52721705186906, -180.86755f, 3.5568366f),
                new Location(Script.WORLD, 38.98596644589013, 100.0, 431.54116302879754, -180.01816f, 1.688792f),
                new Location(Script.WORLD, 38.99267615070082, 106.0, 431.2306982036481, -179.84831f, 0.8583616f),
                new Location(Script.WORLD, 38.98406357923558, 112.0, 431.5643886870891, -179.84831f, 2.7265282f),
                new Location(Script.WORLD, 39.026637086598136, 118.0, 431.4368865510465, -180.69748f, 3.745513f),
                new Location(Script.WORLD, 39.04205665503477, 124.0, 431.4894230966401, -180.35782f, 3.405899f),
        });

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
        if (elevator == null) return;
        if (e.getView().getTitle().equals("§c" + elevator.getName() + " Fahrstuhl")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
                p.sendMessage(Messages.ERROR + "Du bist bereits in diesem Stockwerk.");
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
                int etage = elevator.getEtageByLoc(p.getLocation());
                int way = Math.abs(etage - Ziel_etage) *3;
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
            ItemStack no = new ItemStack(Material.BARRIER);
            ItemMeta noMeta = no.getItemMeta();
            noMeta.setDisplayName(etage);
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