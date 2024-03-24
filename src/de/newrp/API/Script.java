package de.newrp.API;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.newrp.Administrator.AntiCheatSystem;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Berufe.Equip;
import de.newrp.Forum.Forum;
import de.newrp.House.House;
import de.newrp.Organisationen.Drogen;
import de.newrp.Player.AFK;
import de.newrp.Player.Mobile;
import de.newrp.Player.Passwort;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.Ticket.TicketCommand;
import de.newrp.Votifier.VoteListener;
import de.newrp.Waffen.Weapon;
import de.newrp.main;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static de.newrp.API.Rank.*;

public class Script {

    public static World WORLD = Bukkit.getServer().getWorld("World");
    public static String PREFIX = "§8[§cNew RolePlay§8] §8" + Messages.ARROW + " §c";
    public static ArrayList<Player> team = new ArrayList<>();
    public static final ArrayList<String> FREEZE = new ArrayList<>();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.y HH:mm:ss", Locale.GERMANY);
    public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("d.M.y", Locale.GERMANY);
    public static DecimalFormat df = new DecimalFormat("#,###");
    public static final HashMap<String, Long> level_cooldown = new HashMap<>();

    public static List<Player> getNRPTeam() {
        //select all nrp_ids from ranks
        List<Player> players = new ArrayList<>();
        for(Player p : team) {
            players.add(p);
        }
        //sort list by rank
        players.sort((o1, o2) -> {
            if (getRank(o1).getWeight() > getRank(o2).getWeight()) {
                return -1;
            } else if (getRank(o1).getWeight() < getRank(o2).getWeight()) {
                return 1;
            }
            return 0;
        });
        return players;
    }

    public static ItemStack tazer() {
        ItemStack i = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = i.getItemMeta();
        meta.setUnbreakable(true);
        meta.setDisplayName("§7Tazer");
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack createHead(int headid, String name) {
        ItemStack item = Script.getHead(headid);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }


    public static void updateListname(Player p) {
        String color = "";
        if(Beruf.hasBeruf(p)) {
            if(Beruf.getBeruf(p) == Beruf.Berufe.POLICE) color = "§9";
            if(Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST) color = "§4";
            if(Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) color = "§3";
            if(Beruf.getBeruf(p) == Beruf.Berufe.NEWS) color = "§6";
        }
        if (SDuty.isSDuty(p)) p.setPlayerListName("§c§lNRP §8× §9" + p.getName());
        if (!SDuty.isSDuty(p)) p.setPlayerListName("§r" + p.getName());
        if (Duty.isInDuty(p) && Beruf.getAbteilung(p) != Abteilung.Abteilungen.ZIVILPOLICE)
            p.setPlayerListName(color + p.getPlayerListName());
        if (BuildMode.isInBuildMode(p)) p.setPlayerListName("§e§lB §8× §r" + p.getPlayerListName());
        if (TicketCommand.isInTicket(p)) p.setPlayerListName("§b§lT §8× §r" + p.getPlayerListName());
    }

    public static Inventory fillInv(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR)
                inv.setItem(i, setName(Material.WHITE_STAINED_GLASS_PANE, " "));
        }
        return inv;
    }

    public static int calcInvSize(int entries) {
        return (int) Math.ceil(entries / 9.0) * 9;
    }

    public static ItemStack setName(ItemStack is, String s) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(s);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack kevlar(int lvl) {
        ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE, 1, (short) (lvl == 1 ? 50 : 30));
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName("§7" + (lvl==2?"Schwere ":"") + "Schutzweste");
        LeatherArmorMeta armormeta = (LeatherArmorMeta) meta;
        if (lvl == 1) {
            armormeta.setColor(Color.fromRGB(2105376));
        } else {
            armormeta.setColor(Color.fromRGB(5000268));
        }
        is.setItemMeta(meta);
        return removeAttributes(is);
    }

    public static boolean isFalling(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        boolean upY = from.getY() - to.getY() <= .45;
        return !upY;
    }

    public static ItemStack einsatzschild(int level) {
        ItemStack schild = new ItemStack(Material.SHIELD, 1, (short) (level == 1 ? 240 : 160));
        ItemMeta meta = schild.getItemMeta();
        meta.setDisplayName("§7" + (level==2?"Schweres ":"") + "Einsatzschild");
        schild.setItemMeta(meta);
        return schild;
    }

    public static ItemStack fallschirm() {
        return setName(Material.ELYTRA, "§7Fallschirm");
    }

    public static ArrayList<Location> getBlocksAroundCenter(Location loc, int radius) {
        ArrayList<Location> blocks = new ArrayList<>();
        for (int x = (loc.getBlockX() - radius); x <= (loc.getBlockX() + radius); x++) {
            for (int y = (loc.getBlockY() - radius); y <= (loc.getBlockY() + radius); y++) {
                for (int z = (loc.getBlockZ() - radius); z <= (loc.getBlockZ() + radius); z++) {
                    Location l = new Location(loc.getWorld(), x, y, z);
                    if (isInRange(l, loc, radius)) {
                        blocks.add(l);
                    }
                }
            }
        }
        return blocks;
    }

    public static ItemStack flashbang() {
        ItemStack i = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName("§7Flashbang");
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack rauchgranate() {
        return setName(Material.FIREWORK_STAR, "§7Rauchgranate");
    }

    public static ItemStack setNameAndLore(Material mat, String s1, String... s2) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(s1);
        meta.setLore(Arrays.asList(s2));
        is.setItemMeta(meta);
        return is;
    }

    public static boolean isWhitelistedIP(String ip) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM whitelisted_ips WHERE ip=" + ip)) {
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addWhitelistedIP(String ip) {
        executeUpdate("INSERT INTO whitelisted_ips (ip) VALUES ('" + ip + "')");
    }

    public static void addWhiteListName(String name) {
        executeUpdate("INSERT INTO whitelist (name) VALUES ('" + name + "')");
    }

    public static boolean isWhitelistedName(String name) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM whitelist WHERE name='" + name + "'")) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void freeze(Player p) {
        FREEZE.add(p.getName());
        /*p.setWalkSpeed(0f);
        p.removePotionEffect(PotionEffectType.JUMP);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, false, false));*/
    }

    public static void unfreeze(Player p) {
        FREEZE.remove(p.getName());
        /*p.setWalkSpeed(0.2f);
        p.removePotionEffect(PotionEffectType.JUMP);*/
    }

    public static Weapon getWeapon(String name) {
        for (Weapon weapon : Weapon.values()) {
            if (name.equalsIgnoreCase(weapon.getName())) {
                return weapon;
            }
        }
        return null;
    }

    public static int getRandomAlt(int min, int max) {
        boolean min_neg = min <= 0, max_neg = max <= 0;
        min = Math.abs(min);
        max = Math.abs(max);
        int r;
        if (max > min) {
            r = io.netty.util.internal.ThreadLocalRandom.current().nextInt(max - min + 1) + min;
        } else {
            r = io.netty.util.internal.ThreadLocalRandom.current().nextInt(min - max + 1) + max;
        }
        return (min_neg && max_neg ? -r : r);
    }

    public static ItemStack setLore(Material mat, String... s) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setLore(Arrays.asList(s));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack removeLore(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        meta.setLore(null);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack setLore(ItemStack is, String... s) {
        ItemMeta meta = is.getItemMeta();
        meta.setLore(Arrays.asList(s));
        is.setItemMeta(meta);
        return is;
    }

    public static boolean haveGunInInventory(Player p, Weapon weapon) {
        if (p.getInventory().contains(weapon.getWeapon().getType())) {
            return true;
        } else {
            return p.getInventory().getItemInOffHand().getType().equals(weapon.getWeapon().getType());
        }
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

    public static Rank getRank(int p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT rank_id FROM ranks WHERE nrp_id=" + p)) {
            if (rs.next()) {
                return Rank.getRankByID(rs.getInt("rank_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PLAYER;
    }

    public static Rank getRank(OfflinePlayer p) {
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
             ResultSet rs = stmt.executeQuery("SELECT level FROM level WHERE nrp_id=" + getNRPID(name))) {
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
             ResultSet rs = stmt.executeQuery("SELECT level FROM level WHERE nrp_id=" + getNRPID(name))) {
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

    public static String getName(OfflinePlayer p) {
        if(p.isOnline() && p.getPlayer() != null) {
            if (isNRPTeam(p) && SDuty.isSDuty(p.getPlayer())) return "NRP × " + p.getName();
        }
        return p.getName();
    }


    public static Boolean isNRPTeam(Player p) {
        return getRank(p).getWeight() >= SUPPORTER.getWeight();
    }

    public static Boolean isNRPTeam(OfflinePlayer p) {
        return getRank(p).getWeight() >= SUPPORTER.getWeight();
    }

    public static Rank getNextHigherRank(Rank rank) {
        Rank[] ranks = Rank.values();
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i] == rank) {
                if (i == ranks.length - 1) return OWNER;
                return ranks[i - 1];
            }
        }
        return rank;
    }

    public static Boolean hasRank(Player p, Rank rank, Boolean allowLower) {
        if (allowLower) {
            if (getActiveAmountByRank(getNextHigherRank(rank)) == 0) {
                return getRank(p).getWeight() >= rank.getWeight() - 50;
            } else {
                return getRank(p).getWeight() >= rank.getWeight();
            }
        } else {
            return getRank(p).getWeight() >= rank.getWeight();
        }
    }

    public static Boolean hasRank(Integer p, Rank rank, Boolean allowLower) {
        if (allowLower) {
            if (getActiveAmountByRank(getNextHigherRank(rank)) == 0) {
                return getRank(p).getWeight() >= rank.getWeight() - 50;
            } else {
                return getRank(p).getWeight() >= rank.getWeight();
            }
        } else {
            return getRank(p).getWeight() >= rank.getWeight();
        }
    }

    public static boolean isIP(String s) {
        return s.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");
    }

    public static String rainbowChatColor(String string) {
        int currColor;
        StringBuilder newMessage = new StringBuilder();
        String colors = "1234569abcde";
        for (int i = 0; i < string.length(); i++) {
            currColor = new Random().nextInt(colors.length() - 1) + 1;
            newMessage.append(ChatColor.RESET).append(ChatColor.getByChar(colors.charAt(currColor))).append(string.charAt(i));
        }
        return newMessage.toString();
    }

    public static Boolean hasRank(OfflinePlayer p, Rank rank, Boolean allowLower) {
        if (allowLower) {
            if (getActiveAmountByRank(getNextHigherRank(rank)) == 0) {
                return getRank(p).getWeight() >= rank.getWeight() - 50;
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

    public static int getActiveAmountByRank(Rank rank) {
        int i = 0;
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (hasRank(all, rank, false) && !AFK.isAFK(all)) {
                i++;
            }
        }
        Debug.debug(i + " team members with a higher rank are active.");
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
            int id = getNRPID(p);
            if (Forum.getForumID(id) != 0) {
                Forum.setForumName(Forum.getForumID(id), (isNRPTeam(p) ? "NRP × " : "") + p.getName());
            }
            for (House h : House.getHouses(id)) {
                h.updateSign();
            }
            Client cl = TeamSpeak.getClient(id);
            if (cl != null) TeamSpeak.setDescription(cl.getId(), (isNRPTeam(p) ? "NRP × " : "") + p.getName());
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
            for (Player all : Script.getNRPTeam()) {
                if (isNRPTeam(all)) {
                    if (all != p)
                        all.sendMessage("§8[" + cc + "§lT§8] " + cc + "» " + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                }
            }
        } else {
            for (Player all : Script.getNRPTeam()) {
                if (isNRPTeam(all)) {
                    all.sendMessage("§8[" + cc + "§lT§8] " + cc + "» " + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                }
            }
        }
    }

    public static void sendTeamMessage(String msg) {
        for (Player all : Script.getNRPTeam()) {
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
                            all.sendMessage("§8[" + cc + "§LT§8] " + cc + "» " + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                    }
                }
            }
        } else {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isNRPTeam(all)) {
                    if (getRank(p).getWeight() >= rank.getWeight()) {
                        all.sendMessage("§8[" + cc + "§lT§8] " + cc + "» " + cc + getRank(p).getName(p) + " " + getName(p) + " " + msg);
                    }
                }
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

    public static String getBirthday(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT day, month, year FROM birthday WHERE id=" + id)) {
            if (rs.next()) {
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                int year = rs.getInt("year");

                return day + "." + month + "." + year;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "01.01." + Calendar.YEAR;
    }

    public static void setGender(Player p, Gender gender) {
        Script.executeUpdate("DELETE FROM gender WHERE nrp_id=" + Script.getNRPID(p));
        Script.executeAsyncUpdate("INSERT INTO gender (nrp_id, gender) VALUES (" + Script.getNRPID(p) + ", '" + (gender==Gender.MALE?"m":"f") + "')");
    }

    public static String getMonth(int i, boolean UFT8) {
        String[] s = {"Januar", "Februar", UFT8 ? "März" : "Maerz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
        return s[i - 1];
    }

    public static int getMonth(String m) {
        String[] s = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
        for (int i = 0; i < s.length; i++) {
            if (m.equalsIgnoreCase(s[i])) {
                return (i + 1);
            }
        }
        return 1;
    }

    public static void setBirthDay(Player p, int tag, int monat, int jahr, int control) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM birthday WHERE id=" + getNRPID(p))) {
            if (rs.next()) {
                executeUpdate("UPDATE birthday SET year=" + jahr + ", month=" + monat + ", day=" + tag + ", control=" + control + ", geschenk=FALSE WHERE id=" + getNRPID(p));
            } else {
                executeUpdate("INSERT INTO birthday(id, year, month, day, control, geschenk) VALUES(" + getNRPID(p) + ", " + jahr + ", " + monat + ", " + tag + ", " + control + ", FALSE)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer getAge(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT day, month, year FROM birthday WHERE id=" + id)) {
            if (rs.next()) {
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                if (month == 2) {
                    if (day == 29) {
                        day = 1;
                        month = 3;
                    }
                }
                int year = rs.getInt("year");
                LocalDate birthDate = LocalDate.of(year, month, day);
                return Period.between(birthDate, LocalDate.now()).getYears();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float getRandomFloat(float lower, float upper) {
        Random random = new Random();
        return lower + random.nextFloat() * (upper - lower);
    }

    public static double getRandomFloat(double lower, double upper) {
        Random random = new Random();
        return lower + random.nextFloat() * (upper - lower);
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

    public static void executeForumUpdate(String sql) {
        try (Statement stmt = main.getForumConnection().createStatement()) {
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
        p.sendMessage(Script.PREFIX + "Oh nein, ein Bug ist aufgetreten. Wir haben diesen Bug automatisch gemeldet. Bitte entschuldige die Unannehmlichkeiten.");
        sendTeamMessage("§c§lARC: §c" + getName(p) + ": " + msg);
    }

    public static boolean isInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void sendPaymentTypeGUI(Player p, int price) {
        Debug.debug("step 1");
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§8[§aZahlungsmethode§8]");
        ItemStack cash = new ItemBuilder(Material.CHEST).setName("§aBar").setLore("§8» §c" + price + "€").build();
        ItemStack bank = new ItemBuilder(Material.CHEST).setName("§aKarte").setLore("§8» §c" + price + "€").build();
        Debug.debug("step 2");
        inv.setItem(1, cash);
        inv.setItem(3, bank);
        p.openInventory(inv);
        Script.fillInv(inv);
        Debug.debug("step 3");
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
        executeUpdate(sql);
        //Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> ));
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

    public static String setString(Player p, String dbName, String s, String value) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                executeUpdate("UPDATE " + dbName + " SET " + s + "='" + value + "' WHERE nrp_id='" + getNRPID(p) + "'");
            } else {
                executeUpdate("INSERT INTO " + dbName + " (nrp_id, " + s + ") VALUES (" + getNRPID(p) + ", '" + value + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String setString(OfflinePlayer p, String dbName, String s, String value) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbName + " WHERE nrp_id='" + getNRPID(p) + "'")) {
            if (rs.next()) {
                executeUpdate("UPDATE " + dbName + " SET " + s + "='" + value + "' WHERE nrp_id='" + getNRPID(p) + "'");
            } else {
                executeUpdate("INSERT INTO " + dbName + " (nrp_id, " + s + ") VALUES (" + getNRPID(p) + ", '" + value + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(OfflinePlayer p, String dbName, String s) {
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

    public static long getLong(OfflinePlayer p, String dbName, String s) {
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
                    if(rs.getString("msg").equalsIgnoreCase("§8[§eBeruf§8] §e" + Messages.ARROW + " Du wurdest aus deinem Beruf geworfen."))
                        Equip.removeEquip(p);
                    p.sendMessage(rs.getString("msg"));
                } while (rs.next());
                executeAsyncUpdate("DELETE FROM offline_msg WHERE nrp_id='" + getNRPID(p) + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ItemStack brechstange() {
        ItemStack brechstange = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta1 = brechstange.getItemMeta();
        meta1.setDisplayName("§7Brechstange");
        brechstange.setItemMeta(meta1);
        return brechstange;
    }

    public static boolean hasDrugs(Player p) {
        return (p.getInventory().contains(Material.SUGAR) || p.getItemOnCursor().getType() == Material.SUGAR || p.getInventory().getItemInOffHand().getType() == Material.SUGAR || p.getInventory().contains(Material.GREEN_DYE) || p.getItemOnCursor().getType() == Material.GREEN_DYE || p.getInventory().getItemInOffHand().getType() == Material.GREEN_DYE);
    }

    public static int getDrugAmount(Player p) {
        int i = 0;
        for(ItemStack is : p.getInventory().getContents()) {
            if(is != null) {
                if(is.getType() == Material.SUGAR || is.getType() == Material.GREEN_DYE) {
                    i += is.getAmount();
                }
            }
        }
        return i;
    }

    public static void removeDrugs(Player p) {
        p.getInventory().remove(Material.SUGAR);
        p.getInventory().remove(Material.GREEN_DYE);
        p.getItemOnCursor();
        if(p.getItemOnCursor().getType() == Material.SUGAR) p.setItemOnCursor(new ItemStack(Material.AIR));
        if(p.getItemOnCursor().getType() == Material.GREEN_DYE) p.setItemOnCursor(new ItemStack(Material.AIR));
    }

    public static long getLastDisconnect(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM last_disconnect WHERE nrp_id='" + getNRPID(p) + "' ORDER BY id DESC LIMIT 1")) {
            if (rs.next()) {
                return rs.getLong("time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLastDeadOfficer() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM last_officer_dead")) {
            if (rs.next()) {
                return rs.getLong("time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean haveBirthDay(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT day, month FROM birthday WHERE id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                int month = rs.getInt("month");
                int day = rs.getInt("day");

                int c_m = Calendar.getInstance().get(Calendar.MONTH) + 1;
                int c_d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                if (month == c_m) {
                    return day == c_d;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setLastDeadOfficer(long time) {
        executeUpdate("UPDATE last_officer_dead SET time=" + time);
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

    public static void setMoney(OfflinePlayer p, PaymentType paymentType, int amount) {
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + amount + " WHERE nrp_id=" + getNRPID(p));
    }

    public static void addMoney(Player p, PaymentType paymentType, int amount) {
        if(paymentType == PaymentType.CASH) p.sendMessage(Messages.INFO + "Du hast " + amount + "€ Bargeld erhalten.");
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(p, paymentType) + amount) + " WHERE nrp_id=" + getNRPID(p));
        if(Script.getLevel(p) == 1 && getMoney(p, paymentType) >= 50000) Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdächtige Geldmenge: " + Script.getName(p) + " (" + getMoney(p, paymentType) + "€)");
    }

    public static void addMoney(int id, PaymentType paymentType, int amount) {
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(id, paymentType) + amount) + " WHERE nrp_id=" + id);

        if(Script.getLevel(Script.getOfflinePlayer(id)) == 1 && getMoney(id, paymentType) >= 50000) Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdächtige Geldmenge: " + Script.getOfflinePlayer(id).getName() + " (" + getMoney(id, paymentType) + "€)");
    }

    public static void addMoney(OfflinePlayer p, PaymentType paymentType, int amount) {
        amount = Math.abs(amount);
        executeUpdate("UPDATE money SET " + paymentType.getName() + "=" + (getMoney(p, paymentType) + amount) + " WHERE nrp_id=" + getNRPID(p));
        if(Script.getLevel(p) == 1 && getMoney(p, paymentType) >= 50000) Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdächtige Geldmenge: " + p.getName() + " (" + getMoney(p, paymentType) + "€)");
    }

    public static void removeMoney(Player p, PaymentType paymentType, int amount) {
        if(paymentType == PaymentType.CASH) p.sendMessage(Messages.INFO + "Du hast " + amount + "€ Bargeld bezahlt.");
        if(paymentType == PaymentType.BANK) {
            if(Premium.hasPremium(p) && Mobile.hasPhone(p)) {
                if(Mobile.mobileIsOn(p) && Mobile.hasConnection(p)) {
                    p.sendMessage(Messages.INFO + "Du hast " + amount + "€ von deinem Konto bezahlt.");
                }
            }
        }
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
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
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

    public static void playLocalSound(Location loc, Sound sound, int radius) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (isInRange(loc, online.getLocation(), radius)) {
                    online.playSound(loc, sound, 1.0F, 1.0F);
                }
            }
        });
    }

    public static void playLocalSound(Location loc, Sound sound, int radius, float f, float f2) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (isInRange(loc, online.getLocation(), radius)) {
                    online.playSound(loc, sound, f, f2);
                }
            }
        });
    }

    public static ItemStack playerHead(Player p) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwningPlayer(p);
        is.setItemMeta(meta);
        return is;
    }

    public static boolean canOpenGeschenk(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT geschenk FROM birthday WHERE id=" + getNRPID(p))  ) {
            if (rs.next()) {
                return rs.getBoolean("geschenk");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void toggleCanOpenGeschenk(Player p, boolean canopen) {
        executeUpdate("UPDATE birthday SET geschenk=" + canopen + " WHERE id=" + getNRPID(p));
    }

    public static org.bukkit.inventory.ItemStack addGlow(org.bukkit.inventory.ItemStack item) {
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) {
            tag = nmsStack.getTag();
        }
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    public static void startEvent(Event e, boolean message) {
        if (e == null) {
            executeUpdate("UPDATE serversettings SET event='none'");
            main.event = null;
        } else {
            main.event = e;
            if (e.equals(Event.LASERTAG)) {
                if (message) {
                    Bukkit.broadcastMessage("§8[§6Event§8]§6 Es hat ein §lLasertag §r§6begonnen!");
                    Bukkit.broadcastMessage("§8[§6Event§8]§6 Teleportiere dich kostenlos per /event dort hin.");
                }
                executeAsyncUpdate("UPDATE serversettings SET event='" + e.getName() + "'");
            } else if (e.equals(Event.DOUBLE_XP)) {
                if (message) Bukkit.broadcastMessage("§8[§6Event§8]§6 Es hat ein §lDouble XP-Event §r§6begonnen!");
                executeAsyncUpdate("UPDATE serversettings SET event='" + e.getName() + "'");
            } else if (e.equals(Event.VOTE)) {
                if (message)
                    Bukkit.broadcastMessage("§8[§6Event§8]§6 Es hat das Vote-Event §7(§lDouble XP§7)§6 §r§6begonnen!");
                executeAsyncUpdate("UPDATE serversettings SET event='" + e.getName() + "'");
                Vote.startVoteRamble();
            } else if (e.equals(Event.DOUBLE_XP_WEEKEND)) {
                if (message) Bukkit.broadcastMessage("§8[§6Event§8]§6 Es hat ein §lDouble XP-Event §r§6begonnen!");
                executeAsyncUpdate("UPDATE serversettings SET event='" + e.getName() + "'");
            } else if (e.equals(Event.FRIEND_WEEK)) {
                if (message)
                    Bukkit.broadcastMessage("§8[§6Event§8]§6 Es hat eine §lFriend-Week §r§6begonnen!");
                executeAsyncUpdate("UPDATE serversettings SET event='" + e.getName() + "'");
            } else if(e.equals(Event.TRIPPLE_XP)) {
                if (message)
                    Bukkit.broadcastMessage("§8[§6Event§8]§6 Es hat ein §lTripple XP-Event §r§6begonnen!");
                executeAsyncUpdate("UPDATE serversettings SET event='" + e.getName() + "'");
            }
        }
    }

    public static void registerPlayer(Player p) {
        executeUpdate("INSERT INTO nrp_id (id, uuid, name, first_join) VALUES (NULL, '" + p.getUniqueId() + "', '" + p.getName() + "', " + System.currentTimeMillis() + ")");
        executeUpdate("INSERT INTO money (nrp_id, cash, bank) VALUES (" + getNRPID(p) + ", 0, NULL)");
        executeUpdate("INSERT INTO level (nrp_id, level, exp) VALUES (" + getNRPID(p) + ", 1, 0)");
        executeUpdate("INSERT INTO playtime (id, nrp_id, hours, minutes, a_minutes, a_hours) VALUES (NULL, " + getNRPID(p) + ", 0, 1, 0, 0)");
        executeUpdate("INSERT INTO payday (id, nrp_id, time, money) VALUES (NULL, " + getNRPID(p) + ", 1, 0)");
        executeUpdate("INSERT INTO licenses (id, personalausweis, fuehrerschein, waffenschein, angelschein, erste_hilfe) VALUES (" + getNRPID(p) + ", FALSE, FALSE, FALSE, FALSE, FALSE)");

        Premium.addPremium(p, TimeUnit.DAYS.toMillis(7));
        p.setLevel(1);
        setMoney(p, PaymentType.CASH, 1500);
        p.sendMessage(Messages.INFO + "Du hast dich erfolgreich registriert.");
        p.sendMessage(Messages.INFO + "Du hast automatisch einen Premium Account für 7 Tage erhalten.");

        if(Script.getPreReleaseVotes(p) > 0) {
            p.sendMessage(Messages.INFO + "Wir haben bereits " + Script.getPreReleaseVotes(p) + " Stimmen von dir erhalten. Diese werden dir nun gutgeschrieben.");
            Script.executeUpdate("DELETE FROM pre_release_votes WHERE username='" + p.getName() + "'");
            int id = Script.getNRPID(p);
            int points = VoteListener.getVotepoints(id);
            if (points == -1) {
                Script.executeUpdate("INSERT INTO vote(id, votepoints, totalvotes) VALUES(" + id + ", " + getPreReleaseVotes(p) + ", " + getPreReleaseVotes(p) + ")");
            } else {
                Script.executeUpdate("UPDATE vote SET votepoints=votepoints+" + getPreReleaseVotes(p) + ", totalvotes=totalvotes+" + getPreReleaseVotes(p) + " WHERE id=" + id);
            }
        }

    }

    public static void registerPlayer(OfflinePlayer p) {
        executeUpdate("INSERT INTO nrp_id (id, uuid, name, first_join) VALUES (NULL, '" + p.getUniqueId() + "', '" + p.getName() + "', " + System.currentTimeMillis() + ")");
        executeUpdate("INSERT INTO money (nrp_id, cash, bank) VALUES (" + getNRPID(p) + ", 0, NULL)");
        executeUpdate("INSERT INTO level (nrp_id, level, exp) VALUES (" + getNRPID(p) + ", 1, 0)");
        executeUpdate("INSERT INTO playtime (id, nrp_id, hours, minutes, a_minutes, a_hours) VALUES (NULL, " + getNRPID(p) + ", 0, 1, 0, 0)");
        executeUpdate("INSERT INTO payday (id, nrp_id, time, money) VALUES (NULL, " + getNRPID(p) + ", 1, 0)");
        executeUpdate("INSERT INTO licenses (id, personalausweis, fuehrerschein, waffenschein, angelschein) VALUES (" + getNRPID(p) + ", FALSE, FALSE, FALSE, FALSE)");


        Premium.addPremium(p, TimeUnit.DAYS.toMillis(7));
        setMoney(p, PaymentType.CASH, 1500);
        if (p.isOnline()) {
            Player pl = p.getPlayer();
            pl.setLevel(1);
            pl.sendMessage(Messages.INFO + "Du hast dich erfolgreich registriert.");
        }
    }

    public static void registerPlayer(String name, UUID uuid) {
        executeUpdate("INSERT INTO nrp_id (id, uuid, name, first_join) VALUES (NULL, '" + uuid.toString() + "', '" + name + "', " + System.currentTimeMillis() + ")");
        executeUpdate("INSERT INTO money (nrp_id, cash, bank) VALUES (" + getNRPID(name) + ", 0, NULL)");
        executeUpdate("INSERT INTO level (nrp_id, level, exp) VALUES (" + getNRPID(name) + ", 1, 0)");
        executeUpdate("INSERT INTO playtime (id, nrp_id, hours, minutes, a_minutes, a_hours) VALUES (NULL, " + getNRPID(name) + ", 0, 1, 0, 0)");
        executeUpdate("INSERT INTO payday (id, nrp_id, time, money) VALUES (NULL, " + getNRPID(name) + ", 1, 0)");
        executeUpdate("INSERT INTO licenses (id, personalausweis, fuehrerschein, waffenschein, angelschein) VALUES (" + getNRPID(name) + ", FALSE, FALSE, FALSE, FALSE)");
    }

    public static void registerPlayer(UUID uuid) {
        executeUpdate("INSERT INTO nrp_id (id, uuid, name, first_join) VALUES (NULL, '" + uuid + "', '" + Bukkit.getOfflinePlayer(uuid).getName() + "', " + System.currentTimeMillis() + ")");
        executeUpdate("INSERT INTO money (nrp_id, cash, bank) VALUES (" + getNRPID(Bukkit.getOfflinePlayer(uuid)) + ", 0, NULL)");
        executeUpdate("INSERT INTO level (nrp_id, level, exp) VALUES (" + getNRPID(Bukkit.getOfflinePlayer(uuid)) + ", 1, 0)");
        executeUpdate("INSERT INTO playtime (id, nrp_id, hours, minutes, a_minutes, a_hours) VALUES (NULL, " + getNRPID(Bukkit.getOfflinePlayer(uuid)) + ", 0, 1, 0, 0)");
        executeUpdate("INSERT INTO payday (id, nrp_id, time, money) VALUES (NULL, " + getNRPID(Bukkit.getOfflinePlayer(uuid)) + ", 1, 0)");
        executeUpdate("INSERT INTO licenses (id, personalausweis, fuehrerschein, waffenschein, angelschein) VALUES (" + getNRPID(Bukkit.getOfflinePlayer(uuid)) + ", FALSE, FALSE, FALSE, FALSE)");

        Premium.addPremium(Bukkit.getOfflinePlayer(uuid), TimeUnit.DAYS.toMillis(7));
        setMoney(Bukkit.getOfflinePlayer(uuid), PaymentType.CASH, 1500);
    }

    public static boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String getBackUpCode(Player p) {
        return getString(p, "backupcodes" , "code");
    }

    public static String getBackUpCode(OfflinePlayer p) {
        return getString(p, "backupcodes" , "code");
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

    public static void removeWeapons(Player p) {
        for (Weapon w : Weapon.values()) {
            Material mat = w.getWeapon().getType();
            if (p.getInventory().contains(mat)) p.getInventory().remove(mat);
            if (p.getItemOnCursor().getType().equals(mat)) p.setItemOnCursor(null);
        }
        if (p.getInventory().getItemInOffHand() != null) {
            for (Weapon w : Weapon.values()) {
                Material mat = w.getWeapon().getType();
                if (p.getInventory().getItemInOffHand().getType().equals(mat)) {
                    p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                }
            }
        }

        Material[] blocked = new Material[]{Material.TNT, Material.LEATHER_CHESTPLATE, Material.FEATHER, Material.BONE, Material.FLINT};
        for (Material mat : blocked) {
            p.getInventory().remove(mat);
            if (p.getInventory().getItemInOffHand().getType().equals(mat)) {
                p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
            if (p.getItemOnCursor().getType().equals(mat)) p.setItemOnCursor(null);
        }
    }
    public static void increaseActivePlayTime(Player p) {
        executeAsyncUpdate("UPDATE playtime SET a_minutes=a_minutes+1 WHERE nrp_id=" + getNRPID(p));
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM playtime WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                if (rs.getInt("a_minutes") == 59) {
                    executeAsyncUpdate("UPDATE playtime SET a_hours=a_hours+1, a_minutes=0 WHERE nrp_id=" + getNRPID(p));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(getActivePlayTime(p, true) % 50 == 0 && getActivePlayTime(p, false) == 0) {
            p.sendMessage(PREFIX + "Du spielst nun bereits seit " + getActivePlayTime(p, true) + " Stunden aktiv auf NRP × New RolePlay. Vielen Dank dafür!");
            p.sendMessage(PREFIX + "Du erhältst als Dankeschön für deine Treue " + getActivePlayTime(p, true) + " Exp");
            addEXP(p, getActivePlayTime(p, true));
        }

        if(getActivePlayTime(p, true) % 150 == 0 && getActivePlayTime(p, false) == 0) {
            p.sendMessage(PREFIX + "Du erhältst als Dankeschön für deine Treue 3 Tage Premium");
            Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(3), true);
        }

    }

    public static String getLastChar(String s) {
        return s.substring(s.length() - 1);
    }

    public static void setLevel(Player p, int level) {
        executeUpdate("UPDATE level SET level=" + level + " WHERE nrp_id=" + getNRPID(p));
        executeUpdate("UPDATE level SET exp=" + 0 + " WHERE nrp_id=" + getNRPID(p));
        p.setLevel(level);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        Log.NORMAL.write(p, "hat Level " + level + " erreicht.");
        updateExpBar(p);
    }

    public static ItemStack getGermanyFlag() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta banner1 = (BannerMeta) banner.getItemMeta();
        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(DyeColor.RED, PatternType.BASE));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
        patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_RIGHT));
        banner1.setPatterns(patterns);
        banner.setItemMeta(banner1);
        return banner;
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
        p.sendMessage(Script.PREFIX + "§aDu bist nun Level §6" + lvl + "§a!");
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
        if(level_cooldown.containsKey(p.getName())) return;
        level_cooldown.put(p.getName(), System.currentTimeMillis());
        new BukkitRunnable() {
            @Override
            public void run() {
                level_cooldown.remove(p.getName());
            }
        }.runTaskLater(main.getInstance(), 20 * 5);
        if(exp > 200) {
            Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdacht auf Exp-Cheat bei " + Script.getName(p) + " (+" + exp + " Exp)");
        }
        int id = getNRPID(p);
        if (main.event == Event.TRIPPLE_XP) {
            exp *= 3;
            p.sendMessage(" §a+" + exp + " Exp! §7(§6§lTRIPPLE EXP§7)");
        } else if(main.event == Event.DOUBLE_XP || main.event == Event.DOUBLE_XP_WEEKEND) {
            exp *= 2;
            p.sendMessage(" §a+" + exp + " Exp! §7(§6§lDOUBLE EXP§7)");
        } else {
            p.sendMessage(" §a+" + exp + " Exp!");
        }
        sendActionBar(p, "§a+ " + exp + " Exp!");
        executeUpdate("UPDATE level SET exp=" + (getExp(p) + exp) + " WHERE nrp_id=" + id);
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

    public static ArrayList<Integer> getRandomNumbers(int min, int max, int amount) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for(int i = 0; i < amount; i++) {
            int number = Script.getRandom(min, max);
            while(numbers.contains(number)) {
                number = Script.getRandom(min, max);
            }
            numbers.add(number);
        }
        return numbers;
    }

    public static ItemStack getHead(int id) {
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        return api.getItemHead(id + "");
    }

    public static List<Block> getBlocksBetween(Location l1, Location l2) {
        List<Block> blocks = new ArrayList<>();
        int topBlockX = (Math.max(l1.getBlockX(), l2.getBlockX()));
        int bottomBlockX = (Math.min(l1.getBlockX(), l2.getBlockX()));
        int topBlockY = (Math.max(l1.getBlockY(), l2.getBlockY()));
        int bottomBlockY = (Math.min(l1.getBlockY(), l2.getBlockY()));
        int topBlockZ = (Math.max(l1.getBlockZ(), l2.getBlockZ()));
        int bottomBlockZ = (Math.min(l1.getBlockZ(), l2.getBlockZ()));
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int y = bottomBlockY; y <= topBlockY; y++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    Block block = l1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static boolean isInArea(Player p, Location loc1, Location loc2) {
        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));
        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));
        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));
        return (p.getLocation().getBlockX() <= topBlockX && p.getLocation().getBlockX() >= bottomBlockX) && (p.getLocation().getBlockY() <= topBlockY && p.getLocation().getBlockY() >= bottomBlockY) && (p.getLocation().getBlockZ() <= topBlockZ && p.getLocation().getBlockZ() >= bottomBlockZ);
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

    public static void removeEXP(OfflinePlayer p, int exp) {
        int i = (getExp(getNRPID(p)) - exp);
        if (i < 0) i = 0;
        executeUpdate("UPDATE level SET exp=" + i + " WHERE nrp_id=" + getNRPID(p));
        if (p.isOnline()) {
            Script.getPlayer(getNRPID(p)).sendMessage("  §c-" + exp + " Exp!");
            updateExpBar(Script.getPlayer(getNRPID(p)));
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

    public static ItemStack Pfandflasche() {
        ItemStack is = new ItemStack(Material.GLASS_BOTTLE, 1);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName("§7Pfandflasche");
        is.setItemMeta(meta);
        return is;
    }

    public static String getRemainingTime(long time) {
        long difference = time - System.currentTimeMillis();
        if (difference <= 0) return "§cAbgelaufen";

        long seconds = difference / 1000 % 60;
        long minutes = difference / (1000 * 60) % 60;
        long hours = difference / (1000 * 60 * 60) % 24;
        long days = difference / (1000 * 60 * 60 * 24);

        StringBuilder s = new StringBuilder();
        if (days > 0) s.append(days).append(" Tage ");
        if (hours > 0) s.append(hours).append(" Stunden ");
        if (minutes > 0) s.append(minutes).append(" Minuten ");
        if (seconds > 0) s.append(seconds).append(" Sekunden");

        return s.toString();
    }


    public static void sendAcceptMessage(Player p) {
        TextComponent msg = new TextComponent("  §bInfo:§r Nimm es mit ");

        TextComponent annehmen = new TextComponent("§7/§6annehmen");
        annehmen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/annehmen"));
        annehmen.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Annehmen").create()));
        TextComponent msg1 = new TextComponent(" an oder lehne mit ");

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

    public static void sendLinkMessage(Player p, String msg, String link, String hover) {
        TextComponent msg1 = new TextComponent(msg);
        msg1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        p.spigot().sendMessage(msg1);
    }

    public static void sendCopyMessage(Player p, String msg, String copy, String hover) {
        TextComponent msg1 = new TextComponent(msg);
        msg1.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy));
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
        if (i == 0) return 0;
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

    public static void addToBauLog(Player p, Material m, Location loc, boolean removed) {
        String location = loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ();
        executeAsyncUpdate("INSERT INTO baulog (id, nrp_id, material, location, time, removed) VALUES (NULL, " + getNRPID(p) + ", '" + m.name() + "', '" + location + "', " + System.currentTimeMillis() + ", " + removed + ")");
    }

    public static int getBuiltBlocks(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(id) AS total FROM baulog WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPreReleaseVotes(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(id) AS total FROM preReleaseVote WHERE username=" + p.getName())) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getBuiltOnlyPlacedBlocks(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(id) AS total FROM baulog WHERE nrp_id=" + getNRPID(p) + " AND removed=1")) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getActivePlayTime(Player p, boolean hours) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM playtime WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt(hours ? "a_hours" : "a_minutes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getActivePlayTime(OfflinePlayer p, boolean hours) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM playtime WHERE nrp_id=" + getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt(hours ? "a_hours" : "a_minutes");
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

    public static boolean hasWeapons(Player p) {
        Inventory inv = p.getInventory();
        boolean b = false;
        for (Weapon w : Weapon.values()) {
            if (!b && inv.contains(w.getWeapon().getType())) b = true;
        }
        return b;
    }

    public static int getPercentage(int amount, int total) {
        return (int) ((double) amount / total * 100);
    }

    public static int getPercentage(double amount, double total) {
        return (int) ((double) amount / total * 100);
    }

    public static int getPercentage(double amount, int total) {
        return (int) ((double) amount / total * 100);
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
        new BukkitRunnable() {
            @Override
            public void run() {
                String header = "\n§5§lNEW ROLEPLAY\n";
                String footer;
                int online = Bukkit.getOnlinePlayers().size();
                footer = "\n§6Version §8» §6" + main.getInstance().getDescription().getVersion() + "\n§cOnline §8» §c" + online + " Spieler\n";
                p.setPlayerListHeader(header);
                p.setPlayerListFooter(footer);
            }
        }.runTaskLater(main.getInstance(), 20);


    }

}
