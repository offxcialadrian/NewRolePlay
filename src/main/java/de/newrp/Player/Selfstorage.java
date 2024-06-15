package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Berufe.Equip;
import de.newrp.GFB.GFB;
import de.newrp.Waffen.Weapon;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

        OOMA1(1, "A1", new Location(Script.WORLD, 1009, 68, 1234, 270.72272f, 1.6500553f)),
        ROOMA2(2, "A2", new Location(Script.WORLD, 1008, 68, 1224, 266.97308f, -2.699936f)),
        ROOMA3(3, "A3", new Location(Script.WORLD, 1009, 68, 1214, 269.3755f, 0.45005956f)),
        ROOMA4(4, "A4", new Location(Script.WORLD, 1009, 68, 1193, 270.12878f, -1.0499196f)),
        ROOMA5(5, "A5", new Location(Script.WORLD, 1011, 68, 1182, 268.031f, -1.499913f)),
        ROOMA6(6, "A6", new Location(Script.WORLD, 1025, 68, 1183, 90.28113f, 2.5501285f)),
        ROOMA7(7, "A7", new Location(Script.WORLD, 1025, 68, 1192, 88.47705f, 2.4001322f)),
        ROOMA8(8, "A8", new Location(Script.WORLD, 1025, 68, 1214, 90.57263f, 1.8001455f)),
        ROOMA9(9, "A9", new Location(Script.WORLD, 1026, 68, 1224, 89.37109f, -1.7998089f)),
        ROOMA10(10, "A10", new Location(Script.WORLD, 1024, 68, 1234, 88.02136f, -1.6498097f)),
        ROOMB1(11, "B1", new Location(Script.WORLD, 1009, 60, 1234, 269.6538f, -0.44971186f)),
        ROOMB2(12, "B2", new Location(Script.WORLD, 1009, 60, 1224, 269.8015f, 0.3003056f)),
        ROOMB3(13, "B3", new Location(Script.WORLD, 1007, 60, 1214, 268.4707f, -1.6494827f)),
        ROOMB4(14, "B4", new Location(Script.WORLD, 1010, 60, 1193, 269.84497f, 5.678311E-4f)),
        ROOMB5(15, "B5", new Location(Script.WORLD, 1009, 60, 1183, 269.4121f, -1.2001898f)),
        ROOMB6(16, "B6", new Location(Script.WORLD, 1026, 60, 1182, 89.0957f, -1.8001564f)),
        ROOMB7(17, "B7", new Location(Script.WORLD, 1026, 60, 1193, 89.71118f, 5.549921f)),
        ROOMB8(18, "B8", new Location(Script.WORLD, 1026, 60, 1214, 88.21533f, 0.4498805f)),
        ROOMB9(19, "B9", new Location(Script.WORLD, 1025, 60, 1224, 88.19946f, 0.44995156f)),
        ROOMB10(20, "B10", new Location(Script.WORLD, 1025, 60, 1233, 87.778564f, 0.59995085f)),
        ROOMC1(21, "C1", new Location(Script.WORLD, 1009, 51, 1234, 268.7954f, 0.14995451f)),
        ROOMC2(22, "C2", new Location(Script.WORLD, 1009, 51, 1224, 269.26245f, 0.74996996f)),
        ROOMC3(23, "C3", new Location(Script.WORLD, 1008, 51, 1214, 269.40967f, 0.7499815f)),
        ROOMC4(24, "C4", new Location(Script.WORLD, 1009, 51, 1193, 270.3015f, 0.90000147f)),
        ROOMC5(25, "C5", new Location(Script.WORLD, 1010, 51, 1183, 271.36377f, 1.0500015f)),
        ROOMC6(26, "C6", new Location(Script.WORLD, 1025, 51, 1183, 90.90161f, 1.5000602f)),
        ROOMC7(27, "C7", new Location(Script.WORLD, 1026, 51, 1193, 87.90527f, 0.30003476f)),
        ROOMC8(28, "C8", new Location(Script.WORLD, 1025, 51, 1214, 88.96057f, 4.3191016E-5f)),
        ROOMC9(29, "C9", new Location(Script.WORLD, 1027, 51, 1225, 88.21023f, 3.3000488f)),
        ROOMC10(30, "C10", new Location(Script.WORLD, 1024, 51, 1234, 88.21046f, 1.6500586f)),
        ROOMD1(31, "D1", new Location(Script.WORLD, 1010, 42, 1234, 269.4132f, 1.4999636f)),
        ROOMD2(32, "D2", new Location(Script.WORLD, 1009, 42, 1224, 270.7688f, 1.3501364f)),
        ROOMD3(33, "D3", new Location(Script.WORLD, 1009, 42, 1214, 271.0515f, 4.0501876f)),
        ROOMD4(34, "D4", new Location(Script.WORLD, 1009, 42, 1193, 271.20923f, -0.44978526f)),
        ROOMD5(35, "D5", new Location(Script.WORLD, 1008, 42, 1183, 271.0652f, 5.400547f)),
        ROOMD6(36, "D6", new Location(Script.WORLD, 1026, 42, 1183, 90.01367f, 2.2505493f)),
        ROOMD7(37, "D7", new Location(Script.WORLD, 1026, 42, 1193, 88.96631f, 6.000555f)),
        ROOMD8(38, "D8", new Location(Script.WORLD, 1026, 42, 1212, 91.22461f, -0.44940072f)),
        ROOMD9(39, "D9", new Location(Script.WORLD, 1025, 42, 1224, 89.11792f, 0.4506046f)),
        ROOMD10(40, "D10", new Location(Script.WORLD, 1025, 42, 1234, 89.26221f, 0.45061412f));

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

        if(GFB.CURRENT.containsKey(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst diesen Befehl nicht während eines GFBs nutzen.");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 1012, 68, 1201)) < 3) {
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
                p.sendMessage(PREFIX + "Dein Selfstorage-Room ist §6" + getSelfstorage(p).getName() + "§7.");
                p.sendMessage(Messages.INFO + "Nutze §8/§6selfstorage remove §fum deinen Selfstorage-Room zu kündigen und alle Inhalte zu löschen.");
                return true;
            }

            if(getFreeRoom() == null) {
                p.sendMessage(PREFIX + "Es sind leider keine Selfstorage-Rooms mehr frei.");
                return true;
            }

            if(Script.getLevel(p) < 3 || Script.getPlayTime(p, true) < 25) {
                p.sendMessage(Messages.ERROR + "Du musst mindestens Level 3 sein und 25 Stunden gespielt haben, um einen Selfstorage-Room zu mieten.");
                return true;
            }

            Rooms free = getFreeRoom();
            setSelfstorage(p, free.getID());
            p.sendMessage(PREFIX + "Du hast einen Selfstorage-Room gemietet. Dein Raum ist §6" + free.getName() + "§7.");
            p.sendMessage(Messages.INFO + "Nutze §8/§6selfstorage remove §fum deinen Selfstorage-Room zu kündigen.");
            return true;
        } else if(args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            p.sendMessage(Messages.ERROR + "Du kannst deinen Selfstorage Raum nur am Empfang kündigen.");
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
                if(i == null) continue;
                for(Weapon w : Weapon.values()) {
                    if(i.getType().equals(w.getWeapon().getType())) {
                        inv.remove(i);
                        p.getInventory().addItem(i);
                        p.sendMessage(Messages.ERROR + "Du kannst keine Waffen in deinem Selfstorage-Room lagern.");
                        continue;
                    }
                }

                if(i.getType().equals(Material.SUGAR) || i.getType().equals(Material.GREEN_DYE) || i.getType().equals(Material.WARPED_BUTTON)) {
                    inv.remove(i);
                    p.getInventory().addItem(i);
                    p.sendMessage(Messages.ERROR + "Du kannst keine Drogen in deinem Selfstorage-Room lagern.");
                    continue;
                }

                if(Equip.Stuff.isEquip(i)) {
                    inv.remove(i);
                    p.getInventory().addItem(i);
                    p.sendMessage(Messages.ERROR + "Du kannst kein Equip in deinem Selfstorage-Room lagern.");
                    continue;
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
