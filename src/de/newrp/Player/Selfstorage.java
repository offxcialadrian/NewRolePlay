package de.newrp.Player;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Waffen.Weapon;
import de.newrp.Waffen.WeaponData;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.Statement;

public class Selfstorage implements CommandExecutor, Listener {

    public enum Rooms {

        ROOMA1(1, "A1", new Location(Script.WORLD, 1009, 69, 1224));

        private final int id;
        private final String name;
        private final Location loc;

        Rooms(int id, String name, Location loc) {
            this.id = id;
            this.name = name;
            this.loc = loc;
        }

        public int getID() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public Location getLocation() {
            return this.loc;
        }

        public static Rooms getRoomByID(int id) {
            for (Rooms r : Rooms.values()) {
                if (r.getID() == id) {
                    return r;
                }
            }
            return null;
        }

        public static Rooms getRoomByLocation(Location loc) {
            for (Rooms r : Rooms.values()) {
                if (r.getLocation().distance(loc) < 5) {
                    return r;
                }
            }
            return null;
        }

    }

    public static String PREFIX = "§8[§eSelfstorage§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst diesen Befehl nicht im BuildMode nutzen.");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 1012, 68, 1201)) < 5) {
            if(args.length == 1 && args[0].equalsIgnoreCase("remove")) {
                if(!hasSelfstorage(p)) {
                    p.sendMessage(Messages.ERROR + "Du hast keinen Selfstorage-Room.");
                    return true;
                }
                removeSelfstorage(p, true);
                p.sendMessage(PREFIX + "Du hast deinen Selfstorage-Room gekündigt.");
                return true;
            }

            if(hasSelfstorage(p)) {
                p.sendMessage(PREFIX + "Du hast bereits einen Selfstorage-Room.");
                p.sendMessage(Messages.INFO + "Nutze §8/§6selfstorage remove §fum deinen Selfstorage-Room zu kündigen und alle Inhalte zu löschen.");
                return true;
            }

            if(getFreeRoom() == null) {
                p.sendMessage(PREFIX + "Es sind leider keine Selfstorage-Rooms mehr frei.");
                return true;
            }

            Rooms free = getFreeRoom();
            setSelfstorage(p, free.getID());
            p.sendMessage(PREFIX + "Du hast einen Selfstorage-Room gemietet. Dein Raum ist §6" + free.getName() + "§7.");
            p.sendMessage(Messages.INFO + "Nutze §8/§6selfstorage remove §fum deinen Selfstorage-Room zu kündigen.");
            return true;
        }

        if(!hasSelfstorage(p)) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht am Selfstorage-Gebäude.");
            return true;
        }

        Rooms r = getSelfstorage(p);
        if(r == null) {
            p.sendMessage(Messages.ERROR + "Dein Selfstorage-Room wurde nicht gefunden.");
            return true;
        }

        if(p.getLocation().distance(r.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in deinem Selfstorage-Room.");
            return true;
        }

        Inventory inv = p.getServer().createInventory(null, 27, "§eSelfstorage §8» §7" + r.getName());
        Inventory ender = p.getEnderChest();
        ItemStack[] contents = ender.getContents();
        inv.setContents(contents);
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(e.getView().getTitle().startsWith("§eSelfstorage §8» §7")) {
            Inventory inv = e.getInventory();
            ItemStack[] contents = inv.getContents();
            for(ItemStack i : contents) {
                for(Weapon w : Weapon.values()) {
                    if(i != null && i.getType().equals(w.getWeapon().getType())) {
                        inv.remove(i);
                        p.getInventory().addItem(i);
                        p.sendMessage(Messages.ERROR + "Du kannst keine Waffen in deinem Selfstorage-Room lagern.");
                        continue;
                    }
                }
            }
            p.sendMessage(PREFIX + "Speichere Selfstorage-Room...");
            contents = inv.getContents();
            p.getEnderChest().setContents(contents);
            p.sendMessage(PREFIX + "Dein Selfstorage-Room wurde gespeichert.");
        }
    }

    public static boolean hasSelfstorage(Player p) {
        return getSelfstorage(p) != null;
    }

    public static boolean hasSelfstorage(OfflinePlayer p) {
        return getSelfstorage(p) != null;
    }

    public static Rooms getSelfstorage(Player p) {
        return Rooms.getRoomByID(Script.getInt(p, "selfstorage", "room_id"));
    }

    public static Rooms getSelfstorage(OfflinePlayer p) {
        return Rooms.getRoomByID(Script.getInt(p, "selfstorage", "room_id"));
    }

    public static void removeSelfstorage(Player p, boolean clear) {
        if(clear) p.getEnderChest().clear();
        Script.executeUpdate("DELETE FROM selfstorage WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void removeSelfstorageAdmin(OfflinePlayer p) {
        Script.executeUpdate("DELETE FROM selfstorage WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void setSelfstorage(Player p, int room) {
        Script.executeUpdate("INSERT INTO selfstorage (nrp_id, room_id) VALUES (" + Script.getNRPID(p) + ", " + room + ")");
    }

    public static OfflinePlayer getOwner(Rooms room) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM selfstorage WHERE room_id=" + room.getID())) {
            if (rs.next()) {
                return Script.getOfflinePlayer(rs.getInt("nrp_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Rooms getFreeRoom() {
        for (Rooms r : Rooms.values()) {
            if (getOwner(r) == null) {
                return r;
            }
        }
        return null;
    }

}
