package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Chat.Chat;
import de.newrp.Government.Straftat;
import de.newrp.Police.Fahndung;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class UBahn {
    public enum Stops {
        X3(1,1, "X3", new Location(Script.WORLD, 623, 57, 958, 122.71573f, 89.12498f), new Location(Script.WORLD, 621, 54, 950, 0.6724518f, 85.112625f), new Location(Script.WORLD, 625, 63, 988, 178.47375f, 87.25468f)),
        GANGGEBIET(2, 1, "Ganggebiet", new Location(Script.WORLD, 668, 40, 1280, -181.50128f, 89.12495f), new Location(Script.WORLD, 670, 37, 1299, -179.93205f, 87.6163f), new Location(Script.WORLD, 666, 46, 1260, 1.2470001f, 87.94817f)),
        MALL(3, 1, "Mall", new Location(Script.WORLD, 753, 57, 917, 180.9493f, 89.15512f), new Location(Script.WORLD, 755, 54, 936, -180.38312f, 88.25057f), new Location(Script.WORLD, 751, 62, 897, -0.7726388f, 87.586815f)),
        BERUFSSHULE(4, 1,"Berufsschule", new Location(Script.WORLD, 675, 57, 763, 271.1633f, 88.67237f), new Location(Script.WORLD, 644, 54, 765, -90.59423f, 85.95698f), new Location(Script.WORLD, 683, 62, 761, 82.348915f, 86.499985f)),
        MALL_UNTEN(1, 2, "Mall (tief)", new Location(Script.WORLD, 696, 40, 943, 188.02191f, 85.35441f), new Location(Script.WORLD, 689, 37, 945, 271.4676f, 86.982635f), new Location(Script.WORLD, 727, 45, 941, 90.41748f, 68.36674f)),
        X3_UNTEN(2, 2, "X3 (tief)", new Location(Script.WORLD, 623, 40, 925, 358.3131f, 85.83632f), new Location(Script.WORLD, 642, 37, 923, 37.83258f, 89.93965f), new Location(Script.WORLD, 604, 45, 928, 270.479f, 72.0783f)),
        COPS(3, 2, "Polizeistation", new Location(Script.WORLD, 406, 40, 817, 274.03052f, 86.07821f), new Location(Script.WORLD, 404, 37, 810, 317.53418f, 90.0f),new Location(Script.WORLD, 409, 45, 887, 159.1388f, 84.32813f)),
        NEWS(4, 2, "News", new Location(Script.WORLD, 290, 52, 885, 88.72345f, 84.629524f), new Location(Script.WORLD, 293, 49, 916, 169.90729f, 7.420643f), new Location(Script.WORLD, 287, 57, 878, 354.68274f, 30.501802f)),
        KH(5, 2, "Krankenhaus", new Location(Script.WORLD, 337, 52, 1156, 183.48938f, 89.00425f), new Location(Script.WORLD, 330, 49, 1158, 250.89606f, 89.57756f), new Location(Script.WORLD, 368, 57, 1153, 90.81262f, 71.47494f));

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

    public static Stops getNearestStop(Player p) {
        double distance = Double.MAX_VALUE;
        Stops nearest = null;
        for(Stops stop : Stops.values()) {
            double d = p.getLocation().distance(stop.getLocation());
            if(d < distance) {
                distance = d;
                nearest = stop;
            }
        }
        return nearest;
    }

    public static void driveToNextStop(Player p) {
        if (!isInSubway(p)) {
            return;
        }

        if(hasTicket(p)) {
            removeTicket(p);
        } else {
            if(Script.getRandom(1, 100) <= (Premium.hasPremium(p) ? 20 : 30)) {
                if (Script.getMoney(p, PaymentType.BANK) >= 60) {
                    p.sendMessage("§8[§eUBahn§8] §e" + Messages.ARROW + " §7Du wurdest beim Schwarzfahren erwischt und zahlst 60€ Vertragsstrafe.");
                    Script.removeMoney(p, PaymentType.BANK, 60);
                } else {
                    p.sendMessage("§8[§eUBahn§8] §e" + Messages.ARROW + " §7Du wurdest beim Schwarzfahren erwischt und zahlst 60€ Vertragsstrafe.");
                    p.sendMessage(Messages.INFO + "Du hast nicht genug Geld auf deinem Konto um die Vertragsstrafe zu bezahlen. Du wirst gesucht.");
                    Script.executeAsyncUpdate("INSERT INTO wanted (nrp_id, copID, wantedreason, time) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getNRPID(p) + "', '" + Straftat.getReasonID("Schwarzfahren") + "', '" + System.currentTimeMillis() + "')");
                }
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
        for (ItemStack is : p.getInventory().getContents()) {
            if (is == null) continue;
            if (is.getType() != Material.PAPER) continue;
            if (!is.hasItemMeta()) continue;
            if (!is.getItemMeta().hasDisplayName()) continue;
            if (is.getItemMeta().getDisplayName().startsWith("§6UBahn-Ticket")) {
                if (is.getItemMeta().hasLore()) {
                    assert is.getItemMeta().getLore() != null;
                    int remaining = Integer.parseInt(ChatColor.stripColor(is.getItemMeta().getLore().get(0).replace("Verbleibende Fahrten: ", "")));

                    if (remaining <= 1) {
                        // Decrease amount if remaining is 1 or less
                        if (is.getAmount() > 1) {
                            is.setAmount(is.getAmount() - 1);
                        } else {
                            // Remove the ticket if only one ride is left
                            p.getInventory().removeItem(is);
                        }
                    } else {
                        // Decrease the remaining rides
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add("Verbleibende Fahrten: " + (remaining - 1));
                        ItemMeta meta = is.getItemMeta();
                        meta.setLore(lore);
                        is.setItemMeta(meta);
                    }
                    return;
                }
            }
        }
    }


}
