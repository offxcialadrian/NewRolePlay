package de.newrp.Player;

import de.newrp.API.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class UBahn {
    enum Stops {
        X3(1,1, "X3", new Location(Script.WORLD, 623, 57, 958, 122.71573f, 89.12498f), new Location(Script.WORLD, 621, 54, 950, 0.6724518f, 85.112625f), new Location(Script.WORLD, 625, 63, 988, 178.47375f, 87.25468f)),
        GANGGEBIET(2, 1, "Ganggebiet", new Location(Script.WORLD, 668, 40, 1280, -181.50128f, 89.12495f), new Location(Script.WORLD, 670, 37, 1299, -179.93205f, 87.6163f), new Location(Script.WORLD, 666, 46, 1260, 1.2470001f, 87.94817f)),
        MALL(3, 1, "Mall", new Location(Script.WORLD, 753, 57, 917, 180.9493f, 89.15512f), new Location(Script.WORLD, 755, 54, 936, -180.38312f, 88.25057f), new Location(Script.WORLD, 751, 62, 897, -0.7726388f, 87.586815f)),
        BERUFSSHULE(4, 1,"Berufsschule", new Location(Script.WORLD, 675, 57, 763, 271.1633f, 88.67237f), new Location(Script.WORLD, 644, 54, 765, -90.59423f, 85.95698f), new Location(Script.WORLD, 683, 62, 761, 82.348915f, 86.499985f));

        private final int id;
        private final int line;
        private final String name;
        private final Location location;
        private final Location locationmin;
        private final Location locationmax;

        Stops(int id, int line, String name, Location location, Location locationmin, Location locationmax) {
            this.id = id;
            this.line = line;
            this.name = name;
            this.location = location;
            this.locationmin = locationmin;
            this.locationmax = locationmax;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
        }

        public Location getLocationmin() {
            return locationmin;
        }

        public Location getLocationmax() {
            return locationmax;
        }

        public int getLine() {
            return line;
        }

        public static Stops getStopById(int id) {
            for (Stops stop : values()) {
                if (stop.getId() == id) {
                    return stop;
                }
            }
            return null;
        }

        public static Stops getStopByName(String name) {
            for (Stops stop : values()) {
                if (stop.getName().equalsIgnoreCase(name)) {
                    return stop;
                }
            }
            return null;
        }

        public static Stops getStopByLocation(Player p) {
            for (Stops stop : values()) {
                if (Script.isInArea(p, stop.getLocationmin(), stop.getLocationmax())) {
                    return stop;
                }
            }
            return null;
        }

        public Stops getNextStop() {
            //get next stop from same line
            for (Stops stop : values()) {
                if (stop.getLine() == this.getLine() && stop.getId() == this.getId() + 1) {
                    return stop;
                }
            }
            //get first stop from same line
            for (Stops stop : values()) {
                if (stop.getLine() == this.getLine() && stop.getId() == 1) {
                    return stop;
                }
            }
            return null;
        }

    }

    public static boolean isInSubway(Player p) {
        for(Stops stop : Stops.values()) {
            if(Script.isInArea(p, stop.getLocationmin(), stop.getLocationmax()))
                return true;
        }
        return false;
    }

    public static void driveToNextStop(Player p) {
        if (!isInSubway(p)) {
            return;
        }

        if(hasTicket(p)) {
            removeTicket(p);
        } else {
            if(Script.getRandom(1, 100) <= (Premium.hasPremium(p) ? 20 : 30)) {
                p.sendMessage("§8[§eUBahn§8] §e" + Messages.ARROW + " §7Du wurdest beim Schwarzfahren erwischt und zahlst 60€ Vertragsstrafe.");
                Script.removeMoney(p, PaymentType.BANK, 60);
            }
        }

        Debug.debug(p.getName() + " is in subway");

        Stops currentStop = Stops.getStopByLocation(p);
        if(currentStop == null) return;
        Stops nextStop = currentStop.getNextStop();
        Debug.debug("Current stop: " + currentStop.getName());
        Debug.debug("Next stop: " + nextStop.getName());
        p.teleport(nextStop.getLocation());
        p.sendMessage("§8[§eUBahn§8] §e" + Messages.ARROW + " §7Du bist nun an der Haltestelle §6" + nextStop.getName() + "§7.");
        p.sendMessage("§8[§eUBahn§8] §e" + Messages.ARROW + " §7Die nächste Haltestelle ist §6" + nextStop.getNextStop().getName() + "§7.");
    }

    public static boolean hasTicket(Player p) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null) continue;
            if(is.getType() != Material.PAPER) continue;
            if(!is.hasItemMeta()) continue;
            if(!is.getItemMeta().hasDisplayName()) continue;
            if(is.getItemMeta().getDisplayName().startsWith("§6UBahn-Ticket")) {
                return true;
            }
        }
        return false;
    }

    public static void removeTicket(Player p) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null) continue;
            if(is.getType() != Material.PAPER) continue;
            if(!is.hasItemMeta()) continue;
            if(!is.getItemMeta().hasDisplayName()) continue;
            if(is.getItemMeta().getDisplayName().startsWith("§6UBahn-Ticket")) {
                if(is.getItemMeta().hasLore()) {
                    assert is.getItemMeta().getLore() != null;
                    int remaining = Integer.parseInt(is.getItemMeta().getLore().get(0).replace("Verbleibende Fahrten: ", ""));
                    if(remaining == 1) {
                        p.getInventory().remove(is);
                        return;
                    } else {
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add("Verbleibende Fahrten: " + (remaining - 1));
                        is.getItemMeta().setLore(lore);
                    }
                }
                return;
            }
        }
    }

}
