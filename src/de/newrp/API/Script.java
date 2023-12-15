package de.newrp.API;

import de.newrp.Administrator.SDuty;
import de.newrp.Player.Passwort;
import de.newrp.Ticket.TicketCommand;
import de.newrp.main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static de.newrp.API.Rank.PLAYER;
import static de.newrp.API.Rank.SUPPORTER;

public class Script {

    public static World WORLD = Bukkit.getServer().getWorld("World");
    public static String PREFIX = "§8[§cNRP§8] §8" + Messages.ARROW + " §c";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.y HH:mm:ss", Locale.GERMANY);
    public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("d.M.y", Locale.GERMANY);
    public static DecimalFormat df = new DecimalFormat("#,###");

    public static List<Player> getNRPTeam() {
        List<Player> list = new ArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (isNRPTeam(all)) {
                list.add(all);
            }
        }
        return list;
    }

    public static void updateListname(Player p) {
        if (SDuty.isSDuty(p) && TicketCommand.isInTicket(p)) {
            p.setPlayerListName("§8[§b§lT§8] §cNRP §8× §9" + p.getName());
        } else if (SDuty.isSDuty(p)) {
            p.setPlayerListName("§cNRP §8× §9" + p.getName());
        } else {
            p.setPlayerListName(Script.getName(p));
        }
    }

    public static ItemStack setNameAndLore(Material mat, String s1, String... s2) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(s1);
        meta.setLore(Arrays.asList(s2));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack setNameAndLore(ItemStack is, String s1, String... s2) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(s1);
        meta.setLore(Arrays.asList(s2));
        is.setItemMeta(meta);
        return is;
    }

    public static Rank getRank(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT rank_id FROM ranks WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                return Rank.getRankByID(rs.getInt("rank_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PLAYER;
    }

    public static int getLevel(Player name) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT level FROM level WHERE id=" + getNRPID(name))) {
            if (rs.next()) {
                return rs.getInt("level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getLevel(OfflinePlayer name) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT level FROM level WHERE id=" + getNRPID(name))) {
            if (rs.next()) {
                return rs.getInt("level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Location> getBlocksAroundLocation(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        if (!circleblocks.contains(l)) circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<Location> getSquareBlocksAroundLocation(Location loc, int a) {
        ArrayList<Location> blocks = new ArrayList<>();

        for (int x = loc.getBlockX() - a; x <= loc.getBlockX() + a; x++) {
            for (int y = loc.getBlockY() - a; y <= loc.getBlockY() + a; y++) {
                for (int z = loc.getBlockZ() - a; z <= loc.getBlockZ() + a; z++) {
                    Location location = new Location(Script.WORLD, x, y, z);
                    blocks.add(location);
                }
            }
        }

        return blocks;
    }

    public static void resetHealth(Player p) {
        double health = 32D + (((double) getLevel(p) / 5) * 2D);
        p.setMaxHealth(health);
    }

    public static double getMaxHealth(Player p) {
        return 32D + (((double) getLevel(p) / 5) * 2D);
    }

    public static String getName(Player p) {
        if (isNRPTeam(p) && SDuty.isSDuty(p)) return "NRP × " + p.getName();
        return p.getName();
    }


    public static Boolean isNRPTeam(Player p) {
        return getRank(p).getWeight() >= SUPPORTER.getWeight();
    }

    public static Boolean hasRank(Player p, Rank rank, Boolean allowDesc) {
        if(!Passwort.hasPasswort(p)) {
            p.sendMessage(PREFIX + "Du kannst Team-Befehle nur nutzen, wenn du ein Passwort hast.");
            return false;
        }
        if (allowDesc) {
            if (getOnlineAmountByRank(rank) == 0) {
                return getRank(p).getWeight() - 50 == rank.getWeight();
            } else {
                return getRank(p).getWeight() >= rank.getWeight();
            }
        } else {
            return getRank(p).getWeight() >= rank.getWeight();
        }
    }

    public static int getOnlineAmountByRank(Rank rank) {
        int i = 0;
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (hasRank(all, rank, false)) {
                i++;
            }
        }
        return i;
    }

    public static int getNRPID(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM nrp_id WHERE uuid='" + p.getUniqueId() + "'")) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int getNRPID(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM nrp_id WHERE uuid='" + p.getUniqueId() + "'")) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNRPID(String p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM nrp_id WHERE name='" + p + "'")) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void checkPlayerName(Player p) {
        String uuid = p.getUniqueId().toString();
        if (getNameInDB(p) == null || !getNameInDB(p).equals(p.getName())) {
            executeUpdate("UPDATE nrp_id SET name='" + p.getName() + "' WHERE uuid='" + uuid + "'");
            p.sendMessage(Messages.INFO + "Dein Name wurde aktualisiert.");
        }
    }

    public static String getNameInDB(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM nrp_id WHERE uuid='" + p.getUniqueId() + "'")) {
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendTeamMessage(Player p, ChatColor cc, String msg, Boolean skipPlayer) {
        if (skipPlayer) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isNRPTeam(all)) {
                    if (all != p)
                        all.sendMessage(PREFIX + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                }
            }
        } else {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isNRPTeam(all)) {
                    all.sendMessage(PREFIX + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                }
            }
        }
    }

    public static void sendTeamMessage(String msg) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (isNRPTeam(all)) {
                all.sendMessage(msg);
            }
        }
    }

    public static void sendTeamMessage(Player p, ChatColor cc, Rank rank, String msg, Boolean skipPlayer) {
        if (skipPlayer) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isNRPTeam(all)) {
                    if (getRank(p).getWeight() >= rank.getWeight()) {
                        if (all != p)
                            all.sendMessage(PREFIX + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                    }
                }
            }
        } else {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isNRPTeam(all)) {
                    if (getRank(p).getWeight() >= rank.getWeight()) {
                        all.sendMessage(PREFIX + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                    }
                }
            }
        }
    }

    public static void sendLocalChatMessage(Player p, int radius, String msg) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getLocation().distance(p.getLocation()) <= radius) {
                all.sendMessage("§8[§c" + p.getLevel() + "§8] §r" + Script.getName(p) + " sagt: " + msg);
            }
        }
    }

    public static void sendLocalMessage(int radius, Player p, String msg) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getLocation().distance(p.getLocation()) <= radius) {
                all.sendMessage(msg);
            }
        }
    }

    public static void sendActionBar(Player p, String msg) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    }

    public static void prepareScoreboard() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        if (board.getTeam("nopush") == null) {
            Team t = board.registerNewTeam("nopush");
            t.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    public static int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }

    public static void executeUpdate(String sql) {
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + sql);
            System.out.println("SQLException -> " + sql);
        }
    }

    public static Location setDirection(Location loc, Direction direction) {
        loc.setYaw(direction.getYaw());
        return loc;
    }

    public static void resetPotionEffects(Player p) {
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
    }

    public static boolean isInTestMode() {
        return main.isTest();
    }

    public static boolean isAdmin(Player p) {
        return true;
    }

    public static Gender getGender(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM gender WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                if (rs.getString("gender").equals("m")) return Gender.MALE;
                if (rs.getString("gender").equals("f")) return Gender.FEMALE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Gender.MALE;
    }

    public static Gender getGender(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM gender WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                if (rs.getString("gender").equals("m")) return Gender.MALE;
                if (rs.getString("gender").equals("f")) return Gender.FEMALE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Gender.MALE;
    }

    public static void sendBugReport(Player p, String msg) {
        sendTeamMessage(p, ChatColor.RED, "§lARC: " + getName(p) + ": " + msg, false);
    }

    public static boolean isInt(String i) {
        try {
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static void sendPaymentTypeGUI(Player p, int price) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§8[§aZahlungsmethode§8]");
        ItemStack cash = new ItemBuilder(Material.CHEST).setName("§aBar").setLore("§8» §c" + price + "€").build();
        ItemStack bank = new ItemBuilder(Material.CHEST).setName("§aKarte").setLore("§8» §c" + price + "€").build();

        inv.setItem(1, cash);
        inv.setItem(3, bank);
        p.openInventory(inv);
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void executeAsyncUpdate(String sql) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> executeUpdate(sql));
    }

    public static int getMoney(Player p, PaymentType paymentType) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM money WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getInt(paymentType.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getMoney(OfflinePlayer p, PaymentType paymentType) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM money WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getInt(paymentType.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getMoney(int id, PaymentType paymentType) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM money WHERE nrp_id='" + id + "'")) {
            if (rs.next()) {
                return rs.getInt(paymentType.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean getBoolean(Player p, String dbName, String bool) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getBoolean(bool);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getString(Player p, String dbName, String s) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getString(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getInt(Player p, String dbName, String s) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getInt(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getInt(OfflinePlayer p, String dbName, String s) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getInt(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLong(Player p, String dbName, String s) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getLong(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getInt(Player p, String dbName, int s) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getInt(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void sendOfflineMessages(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM offline_msg WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                p.sendMessage(PREFIX + "Du hast Nachrichten erhalten während du Offline warst:");
                do {
                    p.sendMessage(rs.getString("msg"));
                } while (rs.next());
                executeAsyncUpdate("DELETE FROM offline_msg WHERE nrp_id='" + getNRPID(p) + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setBoolean(Player p, String dbName, String bool, boolean value) {
        executeAsyncUpdate("UPDATE " + dbName + " SET " + bool + "=" + value + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void setInt(Player p, String dbName, String s, int value) {
        executeAsyncUpdate("UPDATE " + dbName + " SET " + s + "=" + value + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void setInt(OfflinePlayer p, String dbName, String s, int value) {
        executeAsyncUpdate("UPDATE " + dbName + " SET " + s + "=" + value + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void setMoney(Player p, PaymentType paymentType, int amount) {
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + amount + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void addMoney(Player p, PaymentType paymentType, int amount) {
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(p, paymentType) + amount) + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void addMoney(int id, PaymentType paymentType, int amount) {
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(id, paymentType) + amount) + " WHERE nrp_id=" + id);
    }

    public static void addMoney(OfflinePlayer p, PaymentType paymentType, int amount) {
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(p, paymentType) + amount) + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void removeMoney(Player p, PaymentType paymentType, int amount) {
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(p, paymentType) - amount) + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void removeMoney(OfflinePlayer p, PaymentType paymentType, int amount) {
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(p, paymentType) - amount) + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void addOfflineMessage(OfflinePlayer p, String msg) {
        executeAsyncUpdate("INSERT INTO offline_msg (id, nrp_id, msg) VALUES (NULL, " + getNRPID(p) + ", '" + msg + "')");
    }

    public static void addOfflineMessage(int id, String msg) {
        executeAsyncUpdate("INSERT INTO offline_msg (id, nrp_id, msg) VALUES (NULL, " + id + ", '" + msg + "')");
    }

    public static ItemStack setName(Material mat, String s) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(s);
        is.setItemMeta(meta);
        return is;
    }

    public static boolean isValidPassword(String password) {
        String ePattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(password);
        return m.matches();
    }

    public static Player getPlayer(String name) {
        if (name == null) return null;
        return Bukkit.getPlayer(name);
    }

    public static Date getDate(long time) {
        return new Date(time);
    }

    public static Player getPlayer(int id) {
        if (id == 0) return null;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM nrp_id WHERE id='" + id + "'")) {
            if (rs.next()) {
                return Bukkit.getPlayer(UUID.fromString(rs.getString("uuid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static OfflinePlayer getOfflinePlayer(int id) {
        if (id == 0) return null;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM nrp_id WHERE id='" + id + "'")) {
            if (rs.next()) {
                return Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCountry(Player p) {
        String apiKey = "62e2779b3f40538a4cce5e2a197bcb8b";

        String ipAddress = p.getAddress().getAddress().getHostAddress();

        try {
            String apiUrl = "http://api.ipstack.com/" + ipAddress + "?access_key=" + apiKey;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        if (name == null) return null;
        return Bukkit.getOfflinePlayer(name);
    }


    public static void registerPlayer(Player p) {
        executeUpdate("INSERT INTO nrp_id (id, uuid, name, first_join) VALUES (NULL, '" + p.getUniqueId() + "', '" + p.getName() + "', NOW())");
        executeUpdate("INSERT INTO money (nrp_id, cash, bank) VALUES (" + getNRPID(p) + ", 0, NULL)");
        executeUpdate("INSERT INTO level (nrp_id, level, exp) VALUES (" + getNRPID(p) + ", 1, 0)");
        executeUpdate("INSERT INTO playtime (id, nrp_id, hours, minutes) VALUES (NULL, " + getNRPID(p) + ", 0, 1)");
        executeUpdate("INSERT INTO payday (id, nrp_id, time, money) VALUES (NULL, " + getNRPID(p) + ", 1, 0)");

        p.setLevel(1);
        setMoney(p, PaymentType.BANK, 500);
        setMoney(p, PaymentType.CASH, 50);
        p.sendMessage(Messages.INFO + "Du hast dich erfolgreich registriert.");
    }

    public static void increasePlayTime(Player p) {
        executeAsyncUpdate("UPDATE playtime SET minutes=minutes+1 WHERE nrp_id=" + getNRPID(p));
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM playtime WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                if (rs.getInt("minutes") == 59) {
                    executeAsyncUpdate("UPDATE playtime SET hours=hours+1, minutes=0 WHERE nrp_id=" + getNRPID(p));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLevel(Player p, int level) {
        executeUpdate("UPDATE level SET level=" + level + " WHERE nrp_id=" + getNRPID(p));
        p.setLevel(level);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        Log.NORMAL.write(p, "hat Level " + level + " erreicht.");
        updateExpBar(p);
    }

    public static int getExp(String name) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT exp FROM level WHERE id=" + getNRPID(name))) {
            if (rs.next()) {
                return rs.getInt("exp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int getLevelCost(Player p) {
        int level_cost;
        level_cost = 692 + ((getLevel(p) * 2) * 497);
        if (getLevel(p) % 2 == 0) {
            level_cost += 173;
        }
        return level_cost;
    }

    public static void increaseLevel(Player p) {
        int level_cost = getLevelCost(p);
        int lvl = getLevel(p) + 1;
        int id = getNRPID(p);
        setLevel(p, lvl);
        p.sendMessage("§aDu bist nun Level §6" + lvl + "§a!");
        setExpbarPercentage(p, 0);
        resetHealth(p);
        updateExpBar(p);
    }

    public static void updateExpBar(Player p) {
        float exp = getExp(p);
        float level_cost = getLevelCost(p);
        float temp = (exp / level_cost);
        float i = ((temp) * 100F);
        if (i > 99) {
            i = 0;
        }
        setExpbarPercentage(p, i);
    }

    public static void addEXP(Player p, int exp) {
        int id = getNRPID(p);
        p.sendMessage("  §a+" + exp + " Exp!");
        sendActionBar(p, "§a+ " + exp + " Exp!");
        executeUpdate("UPDATE level SET exp=" + (getExp(p) + exp) + " WHERE id=" + id);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        int level_cost = getLevelCost(p);
        int current = getExp(p);
        if (current >= level_cost) increaseLevel(p);
        updateExpBar(p);
    }

    public static void setExpbarPercentage(Player p, double percent) {
        int toLevel = p.getExpToLevel();
        int add = (int) (toLevel * (percent / 100));
        if (add < 0) add = 0;
        int totalLevel = getExpForLevel(p.getLevel());
        ExperienceManager manager = new ExperienceManager(p);
        if ((getExpForLevel(p.getLevel()) - 1) >= (totalLevel + add)) {
            manager.setTotalExperience((Math.max(totalLevel, 0)));
        } else {
            manager.setTotalExperience((totalLevel + add));
        }
        if (p.getLevel() != getLevel(p)) {
            p.setLevel(getLevel(p));
        }
    }

    public static int getExpForLevel(int level) {
        if (level < 0) level = 1;

        if (level >= 1 && level <= 16) {
            return (int) (Math.pow(level, 2) + (6 * level));
        } else if (level >= 17 && level <= 31) {
            return (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
        }
    }

    public static void addEXP(int id, int exp) {
        executeUpdate("UPDATE level SET exp=" + (getExp(id) + exp) + " WHERE nrp_id=" + id);
    }

    public static void removeEXP(String p, int exp) {
        if (getPlayer(p) != null) {
            getPlayer(p).sendMessage("  §c-" + exp + " Exp!");
        }
        int i = (getExp(p) - exp);
        if (i < 0) i = 0;
        executeUpdate("UPDATE level SET exp=" + i + " WHERE nrp_id=" + getNRPID(p));
        if (getPlayer(p) != null) {
            updateExpBar(getPlayer(p));
        }
    }

    public static int getExp(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT exp FROM level WHERE nrp_id=" + id)) {
            if (rs.next()) {
                return rs.getInt("exp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getExp(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT exp FROM level WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("exp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void sendAcceptMessage(Player p) {
        TextComponent msg = new TextComponent("  §bInfo:§r Nimm es mit ");

        TextComponent annehmen = new TextComponent("§7/§6annehmen");
        annehmen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/annehmen"));
        annehmen.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Annehmen").create()));
        TextComponent msg1 = new TextComponent(" an oder mit ");

        TextComponent ablehnen = new TextComponent("§7/§6ablehnen");
        ablehnen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ablehnen"));
        ablehnen.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Ablehnen").create()));

        TextComponent msg2 = new TextComponent("§r ab.");

        msg.addExtra(annehmen);
        msg.addExtra(msg1);
        msg.addExtra(ablehnen);
        msg.addExtra(msg2);
        p.spigot().sendMessage(msg);
    }

    public static void sendClickableMessage(Player p, String msg, String cmd, String hover) {
        TextComponent msg1 = new TextComponent(msg);
        msg1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
        msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        p.spigot().sendMessage(msg1);
    }

    public static boolean isInAir(Player p) {
        Block b = p.getWorld().getBlockAt(p.getLocation());
        return (b.getType().equals(Material.AIR) && b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR));
    }

    public static boolean isInRange(Location loc1, Location loc2, int maxDistance) {
        if (loc1 == null || loc2 == null) return false;
        maxDistance = maxDistance * maxDistance;
        return loc1.distanceSquared(loc2) <= maxDistance;
    }

    public static boolean isInRegion(Player p, Location loc1, Location loc2) {
        return isInRegion(p.getLocation(), loc1, loc2);
    }

    public static boolean isInRegion(Location loc, Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int px = loc.getBlockX();
        int py = loc.getBlockY();
        int pz = loc.getBlockZ();
        if (loc1.getWorld() == loc.getWorld()) {
            if ((px >= x1) && (px <= x2)) {
                if ((py >= y1) && (py <= y2)) {
                    return (pz >= z1) && (pz <= z2);
                }
            }
        }
        return false;
    }

    public static int manipulateInt(int i) {
        if(i == 0) return 0;
        int climbingorfalling = Script.getRandom(1, 100);
        if (climbingorfalling <= 50) {
            i += (int) Script.getPercent(Script.getRandom(1, 20), i);
        } else {
            i -= (int) Script.getPercent(Script.getRandom(1, 20), i);
        }
        return i;
    }

    public static boolean isInRange(Location loc1, Location loc2, double maxDistance) {
        if (loc1 == null || loc2 == null) return false;
        maxDistance = maxDistance * maxDistance;
        return loc1.distanceSquared(loc2) <= maxDistance;
    }

    public static List<Location> circle(Location mid, int r) {
        List<Location> circleblocks = new ArrayList<>();
        double xcenter = mid.getX();
        double zcenter = mid.getZ();
        double y = mid.getY();
        for (int i = 0; i <= 360; i++) {
            double tempx = (r * Math.cos(i)) + xcenter;
            double tempz = (r * Math.sin(i)) + zcenter;
            Location loc = new Location(mid.getWorld(), tempx, y, tempz);
            circleblocks.add(loc);
        }
        return circleblocks;
    }

    public static ItemStack removeAttributes(ItemStack item) {
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        } else {
            tag = nmsStack.getTag();
        }
        NBTTagList am = new NBTTagList();
        tag.set("AttributeModifiers", am);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    public static List<Location> circle(Location mid, double r, int amount) {
        List<Location> circleblocks = new ArrayList<>();
        double xcenter = mid.getX();
        double zcenter = mid.getZ();
        double y = mid.getY();
        for (int i = 0; i <= 360; i += amount) {
            double tempx = (r * Math.cos(i)) + xcenter;
            double tempz = (r * Math.sin(i)) + zcenter;
            Location loc = new Location(mid.getWorld(), tempx, y, tempz);
            circleblocks.add(loc);
        }
        return circleblocks;
    }

    public static int getPlayTime(Player p, boolean hours) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM playtime WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt(hours ? "hours" : "minutes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPlayTime(OfflinePlayer p, boolean hours) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM playtime WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt(hours ? "hours" : "minutes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static double getPercent(double percent, int total) {
        if (total == 0) return 0;
        return ((double) total / 100) * percent;
    }

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static void sendTabTitle(Player p) {
        String header = "\n§5§lNEW ROLEPLAY\n";
        String footer;
        int online = Bukkit.getOnlinePlayers().size();
        footer = "\n§6Version §8» §6" + main.getInstance().getDescription().getVersion() + "\n§cOnline §8» §c" + online + " Spieler\n";
        p.setPlayerListHeader(header);
        p.setPlayerListFooter(footer);

    }

}
