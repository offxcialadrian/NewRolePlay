package de.newrp.House;

import com.mysql.jdbc.Statement;
import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HouseRegister implements CommandExecutor, Listener {
    public static final HashMap<String, HouseRegistration> CACHE = new HashMap<>();

    public static HouseRegistration getRegistration(Player p) {
        if (CACHE.containsKey(p.getName())) {
            return CACHE.get(p.getName());
        }
        HouseRegistration reg = new HouseRegistration(0, 0, null, null, null, null, 0, new ArrayList<>(), p.getName());
        CACHE.put(p.getName(), reg);
        return reg;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

            if (args.length == 0) {
                p.sendMessage(Messages.ERROR + "/registerhouse [Hausnummer/AUTO] [Preis] <Besitzer>");
                return false;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
                if (reset(p)) {
                    p.sendMessage("§cDu hast deinen Haus-Cache gelöscht.");
                } else {
                    p.sendMessage("§cDu hast keine Positionen gesammelt.");
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
                if (CACHE.containsKey(p.getName())) {
                    HouseRegistration reg = CACHE.get(p.getName());
                    StringBuilder sb = new StringBuilder("§7=== §6Haus-Registration §7===\n");
                    if (reg.house_region_min != null) {
                        sb.append("  §6» Position #1§8:§6 ").append(reg.house_region_min.getBlockX()).append("/").append(reg.house_region_min.getBlockY()).append("/").append(reg.house_region_min.getBlockZ()).append("\n");
                    } else {
                        sb.append("  §6» Position #1§8:§6 Keine Position gefunden.\n");
                    }
                    if (reg.house_region_max != null) {
                        sb.append("  §6» Position #2§8:§6 ").append(reg.house_region_max.getBlockX()).append("/").append(reg.house_region_max.getBlockY()).append("/").append(reg.house_region_max.getBlockZ()).append("\n");
                    } else {
                        sb.append("  §6» Position #2§8:§6 Keine Position gefunden.\n");
                    }
                    if (reg.getSignLocation() != null) {
                        sb.append("  §6» Schild§8:§6 ").append(reg.getSignLocation().getBlockX()).append("/").append(reg.getSignLocation().getBlockY()).append("/").append(reg.getSignLocation().getBlockZ()).append("\n");
                    } else {
                        sb.append("  §6» Schild§8:§6 Keine Position gefunden.\n");
                    }
                    if (reg.getDoorLocations() != null && !reg.getDoorLocations().isEmpty()) {
                        sb.append("  §6» Türen§8:§6 \n");
                        int i = 1;
                        for (Location door : reg.getDoorLocations()) {
                            sb.append("    §7- #").append(i++).append(" ").append(door.getBlockX()).append("/").append(door.getBlockY()).append("/").append(door.getBlockZ()).append("\n");
                        }
                    } else {
                        sb.append("  §6» Türen§8:§6 Keine Türen gefunden.");
                    }
                    p.sendMessage(sb.toString());
                } else {
                    p.sendMessage(Messages.ERROR + "Du hast noch keine Positionen gesammelt.");
                }
                return true;
            }
            if (args.length > 3) {
                p.sendMessage(Messages.ERROR + "/registerhouse [Hausnummer/AUTO] [Preis] <Besitzer>");
            } else {
                if (CACHE.containsKey(p.getName())) {
                    HouseRegistration reg = CACHE.get(p.getName());
                    boolean autoID = false;
                    int hausnr = 0;

                    if (args[0].equalsIgnoreCase("auto")) {
                        autoID = true;
                    } else if (Script.isInt(args[0])) {
                        hausnr = Integer.parseInt(args[0]);
                    } else {
                        p.sendMessage(Messages.ERROR + "/registerhouse [Hausnummer/AUTO] [Preis] <Besitzer>");
                        return true;
                    }

                    if (Script.isInt(args[1])) {
                        int price = Integer.parseInt(args[1]);
                        Player p1 = null;
                        int p1ID = 0;

                        if (args.length == 3) {
                            Player p1_ = Script.getPlayer(args[2]);
                            if (p1_ != null) {
                                p1 = p1_;
                                p1ID = Script.getNRPID(p1_);
                            } else {
                                int id1 = Script.getNRPID(args[2]);
                                if (id1 == 0) {
                                    p.sendMessage(Messages.PLAYER_NOT_FOUND);
                                    return true;
                                }
                                p1ID = id1;
                            }
                        }

                        if (autoID) {
                            try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("SELECT houseID FROM house ORDER BY houseID DESC LIMIT 1;")) {
                                try (ResultSet rs = statement.executeQuery()) {
                                    if (rs.next()) {
                                        reg.setHouseID(rs.getInt("houseID") + 1);
                                    } else {
                                        hausnr = -1;
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        if (!autoID) reg.setHouseID(hausnr);
                        if (p1 != null) {
                            reg.setOwnerName(p.getName());
                            reg.setOwnerID(p1ID);
                        } else if (p1ID != 0) {
                            reg.setOwnerName(Script.getOfflinePlayer(p1ID).getName());
                            reg.setOwnerID(p1ID);
                        }
                        reg.setPrice(price);

                        reg.register();
                        p.sendMessage("§8[§eHaus-Registration§8] §7Du hast das Haus §6" + reg.getHouseID() + " §7registriert.");
                        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat das Haus " + reg.getHouseID() + " registriert.", true);
                        reset(p);
                    } else {
                        p.sendMessage(Messages.ERROR + "/registerhouse [Hausnummer/AUTO] [Preis] <Besitzer>");
                        return true;
                    }
                } else {
                    p.sendMessage(Messages.ERROR + "Du hast noch keine Positionen gesammelt.");
                }
            }
        return true;
    }

    public boolean reset(Player p) {
        if (CACHE.containsKey(p.getName())) {
            CACHE.remove(p.getName());
            return true;
        }
        return false;
    }

    public static class HouseRegistration {

        private final List<Location> doors;
        private final String adminName;
        private int houseID;
        private int ownerID;
        private String ownerName;
        private Location house_region_max;
        private Location house_region_min;
        private Location sign;
        private int price;

        public HouseRegistration(int houseID, int ownerID, String ownerName, Location house_region_max, Location house_region_min, Location sign, int price, List<Location> doors, String adminName) {
            this.houseID = houseID;
            this.ownerID = ownerID;
            this.ownerName = ownerName;
            this.house_region_max = house_region_max;
            this.house_region_min = house_region_min;
            this.sign = sign;
            this.price = price;
            this.doors = doors;
            this.adminName = adminName;
        }

        public String toString() {
            return "{houseID: " + houseID + ", ownerID: " + ownerID + ", ownerName: " + ownerName + ", house_region_max: " + house_region_max + ", house_region_min: " + house_region_max + ", sign: " + sign + ", price: " + price + ", doors: " + doors.toString() + "}";
        }

        public int getHouseID() {
            return this.houseID;
        }

        public void setHouseID(int houseID) {
            this.houseID = houseID;
        }

        public int getOwnerID() {
            return this.ownerID;
        }

        public void setOwnerID(int ownerID) {
            this.ownerID = ownerID;
        }

        public String getOwnerName() {
            return this.ownerName;
        }

        public void setOwnerName(String name) {
            this.ownerName = name;
        }

        public Location getLocationMax() {
            return this.house_region_max;
        }

        public void setLocationMax(Location max) {
            this.house_region_max = max;
        }

        public Location getLocationMin() {
            return this.house_region_min;
        }

        public void setLocationMin(Location min) {
            this.house_region_min = min;
        }

        public Location getSignLocation() {
            return this.sign;
        }

        public void setSignLocation(Location sign) {
            this.sign = sign;
        }

        public int getPrice() {
            return this.price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public List<Location> getDoorLocations() {
            return this.doors;
        }

        public String getAdminName() {
            return adminName;
        }

        public void addDoor(Location door) {
            if (!this.doors.contains(door)) this.doors.add(door);
        }

        public void register() {
            if (this.houseID < 0 || this.house_region_min == null || this.house_region_max == null || this.sign == null || this.doors == null || this.doors.isEmpty()) {
                Debug.debug("NO REGISTRATION");
                return;
            }
            List<House.Mieter> mieter = new ArrayList<>();
            if (this.ownerID != 0 && this.ownerName != null) {
                mieter.add(new House.Mieter(this.ownerName, this.ownerID, 0, 0));
            }
            if (this.houseID == 0) {
                final HouseRegistration reg = this;
                Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                    try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                            "INSERT INTO house (ownerID, min_x, min_y, min_z, max_x, max_y, max_z, sign, kasse, slots, price, snacks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
                    )) {
                        statement.setInt(1, reg.getOwnerID());

                        statement.setInt(2, reg.getLocationMin().getBlockX());
                        statement.setInt(3, reg.getLocationMin().getBlockY());
                        statement.setInt(4, reg.getLocationMin().getBlockZ());

                        statement.setInt(5, reg.getLocationMax().getBlockX());
                        statement.setInt(6, reg.getLocationMax().getBlockY());
                        statement.setInt(7, reg.getLocationMax().getBlockZ());

                        Location sign_loc = reg.getSignLocation();

                        statement.setString(8, sign_loc.getBlockX() + "/" + sign_loc.getBlockY() + "/" + sign_loc.getBlockZ());
                        statement.setInt(9, 0);
                        statement.setInt(10, 2);
                        statement.setInt(11, reg.getPrice());
                        statement.setInt(12, 0);

                        statement.execute();
                        try (ResultSet keys = statement.getGeneratedKeys()) {
                            if (keys.next()) {
                                int houseID = reg.getHouseID();
                                House house = new House(houseID, reg.ownerID, new Location[]{reg.house_region_min, reg.house_region_max}, reg.sign, 0, 2, reg.price, 0, new ArrayList<>(), mieter);
                                House.HOUSES.add(house);
                                reg.setHouseID(houseID);
                                if (reg.ownerID != 0) {
                                    if (House.PLAYER_HOUSES.containsKey(reg.ownerID)) {
                                        House.PLAYER_HOUSES.get(reg.ownerID).add(house);
                                    } else {
                                        List<House> houses = new ArrayList<>();
                                        houses.add(house);
                                        House.PLAYER_HOUSES.put(reg.ownerID, houses);
                                    }
                                }
                                for (Location door : reg.getDoorLocations()) {
                                    int x = door.getBlockX();
                                    int y = door.getBlockY();
                                    int z = door.getBlockZ();
                                    Script.executeAsyncUpdate("INSERT INTO house_door (houseID, x, y, z) VALUES (" + houseID + ", " + x + ", " + y + ", " + z + ");");
                                }

                                /*Cache.C_HOUSE_DOOR.put(houseID, new ArrayList<>(reg.getDoorLocations()));
                                Cache.C_HOUSE_SIGN.put(reg.houseID, reg.getSignLocation());*/

                                if (reg.getOwnerID() != 0) {
                                    Script.executeAsyncUpdate("INSERT INTO house_bewohner (houseID, mieterID, vermieter, miete, nebenkosten, immobilienmarkt) VALUES (" + houseID + ", " + reg.getOwnerID() + ", FALSE, 0, 0, FALSE); ");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        throw new IllegalStateException(e);
                    }
                });
                Sign s = (Sign) reg.getSignLocation().getBlock().getState();
                s.setLine(0, "");
                s.setLine(1, "");
                s.setLine(2, "");
                s.setLine(3, "");
                if (reg.getOwnerID() != 0) {
                    s.setLine(1, "== " + reg.getHouseID() + " ==");
                    if (reg.getOwnerName() != null) {
                        s.setLine(2, Script.getOfflinePlayer(reg.getOwnerID()).getName());
                    } else {
                        s.setLine(2, "Frei");
                        s.setLine(3, reg.getPrice() + "€");
                    }
                } else {
                    s.setLine(1, "== " + reg.getHouseID() + " ==");
                    s.setLine(2, "Frei");
                    s.setLine(3, reg.getPrice() + "€");
                }
                s.update(true);
            } else {
                House house = new House(this.houseID, this.ownerID, new Location[]{this.house_region_min, this.house_region_max}, this.sign, 0, 2, this.price, 0, new ArrayList<>(), mieter);
                House.HOUSES.add(house);
                if (this.ownerID != 0) {
                    if (House.PLAYER_HOUSES.containsKey(this.ownerID)) {
                        House.PLAYER_HOUSES.get(this.ownerID).add(house);
                    } else {
                        List<House> houses = new ArrayList<>();
                        houses.add(house);
                        House.PLAYER_HOUSES.put(this.ownerID, houses);
                    }
                }

                final HouseRegistration reg = this;

                Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                    try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO house (houseID, ownerID, min_x, min_y, min_z, max_x, max_y, max_z, sign, kasse, slots, price, snacks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ")) {
                        statement.setInt(1, reg.getHouseID());
                        statement.setInt(2, reg.getOwnerID());

                        statement.setInt(3, reg.getLocationMin().getBlockX());
                        statement.setInt(4, reg.getLocationMin().getBlockY());
                        statement.setInt(5, reg.getLocationMin().getBlockZ());

                        statement.setInt(6, reg.getLocationMax().getBlockX());
                        statement.setInt(7, reg.getLocationMax().getBlockY());
                        statement.setInt(8, reg.getLocationMax().getBlockZ());

                        Location sign_loc = reg.getSignLocation();

                        statement.setString(9, sign_loc.getBlockX() + "/" + sign_loc.getBlockY() + "/" + sign_loc.getBlockZ());
                        statement.setInt(10, 0);
                        statement.setInt(11, 2);
                        statement.setInt(12, reg.getPrice());
                        statement.setInt(13, 0);

                        statement.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    for (Location door : reg.getDoorLocations()) {
                        int x = door.getBlockX();
                        int y = door.getBlockY();
                        int z = door.getBlockZ();

                        Script.executeAsyncUpdate("INSERT INTO house_door (houseID, x, y, z) VALUES (" + reg.getHouseID() + ", " + x + ", " + y + ", " + z + ");");
                    }

                    /*Cache.C_HOUSE_DOOR.put(reg.houseID, new ArrayList<>(reg.getDoorLocations()));
                    Cache.C_HOUSE_SIGN.put(reg.houseID, reg.getSignLocation());*/

                    if (reg.getOwnerID() != 0) {
                        Script.executeAsyncUpdate("INSERT INTO house_bewohner (houseID, mieterID, vermieter, miete, nebenkosten, immobilienmarkt) VALUES (" + reg.getHouseID() + ", " + reg.getOwnerID() + ", FALSE, 0, 0, FALSE); ");
                    }
                });
                Sign s = (Sign) reg.getSignLocation().getBlock().getState();
                s.setLine(0, "");
                s.setLine(1, "");
                s.setLine(2, "");
                s.setLine(3, "");
                if (reg.getOwnerID() != 0) {
                    s.setLine(1, "== " + reg.getHouseID() + " ==");
                    if (reg.getOwnerName() != null) {
                        s.setLine(2, reg.getOwnerName());
                    } else {
                        s.setLine(2, "Frei");
                        s.setLine(3, reg.getPrice() + "€");
                    }
                } else {
                    s.setLine(1, "== " + reg.getHouseID() + " ==");
                    s.setLine(2, "Frei");
                    s.setLine(3, reg.getPrice() + "€");
                }
                s.update(true);
            }
            CACHE.remove(adminName);
        }
    }
        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (e.getHand() == EquipmentSlot.OFF_HAND) return;
            Action action = e.getAction();
            Player p = e.getPlayer();
            p.getInventory().getItemInMainHand();
            if (p.getInventory().getItemInMainHand().getType().equals(Material.IRON_HOE)) {
                if (p.getInventory().getItemInMainHand().hasItemMeta()) return;
                if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) return;
                if(!SDuty.isSDuty(p)) return;
                e.setCancelled(true);
                if (action == Action.LEFT_CLICK_BLOCK) {
                    Block b = e.getClickedBlock();
                    Location loc = b.getLocation();
                    if (b.getType().equals(Material.OAK_WALL_SIGN)) {
                        HouseRegister.getRegistration(p).setSignLocation(loc);
                        p.sendMessage(House.PREFIX + "Du hast die Position des Haus-Schildes auf " + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ() + " gesetzt.");
                    } else if (b.getType().equals(Material.OAK_DOOR)) {
                        boolean top;
                        top = b.getRelative(BlockFace.DOWN).getType().equals(b.getType());
                        HouseRegister.getRegistration(p).addDoor(loc);
                        HouseRegister.getRegistration(p).addDoor(loc.clone().add(0, (top ? -1 : +1), 0));

                        p.sendMessage(House.PREFIX + "Du hast eine Tür mit der Position " + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ() + " hinzugefügt.");
                    } else {
                        HouseRegister.getRegistration(p).setLocationMin(loc);
                        p.sendMessage(House.PREFIX + "Du hast die Position #1 auf " + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ() + " gesetzt.");
                    }
                } else if (action == Action.RIGHT_CLICK_BLOCK) {
                    Block b = e.getClickedBlock();
                    Location loc = b.getLocation();
                    HouseRegister.getRegistration(p).setLocationMax(loc);
                    p.sendMessage(House.PREFIX + "Du hast die Position #2 auf " + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ() + " gesetzt.");
                }
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            HouseRegister.CACHE.remove(e.getPlayer().getName());
        }
}

