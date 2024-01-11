package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Chat.Me;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Mobile implements Listener {

    private static final Map<String, Long> TOOGLE_COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();

    public static String PREFIX = "§8[§6Handy§8] §6" + Messages.ARROW + " §7";

    public enum Phones {

        APPLE(1, "iPhone 15", 100, new ItemBuilder(Material.IRON_INGOT).setName("iPhone 15").build(), 20),
        SAMSUNG(2, "Galaxy S21", 100, new ItemBuilder(Material.IRON_INGOT).setName("Galaxy S21").build(), 10),
        HUAWEI(3, "P60", 100, new ItemBuilder(Material.IRON_INGOT).setName("P60").build(), 8),
        GOOGLE(4, "Pixel 10", 100, new ItemBuilder(Material.IRON_INGOT).setName("Pixel 10").build(), 5);

        int id;
        String name;
        int akku;
        ItemStack item;
        int connection;


        Phones(int id, String name, int akku, ItemStack item, int connection) {
            this.id = id;
            this.name = name;
            this.akku = akku;
            this.item = item;
            this.connection = connection;
        }

        public int getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getMaxAkku() {
            return akku;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getConnection() {
            return connection;
        }

        public static Phones getPhone(String s) {
            for (Phones phones : Phones.values()) {
                if (phones.getName().equalsIgnoreCase(s)) {
                    return phones;
                }
            }
            return null;
        }

        public static Phones getPhone(int id) {
            for (Phones phones : Phones.values()) {
                if (phones.getID() == id) {
                    return phones;
                }
            }
            return null;
        }

        public static Phones getPhone(ItemStack item) {
            for (Phones phones : Phones.values()) {
                if (phones.getItem().equals(item)) {
                    return phones;
                }
            }
            return null;
        }

        public int getAkku(Player p) {
            try(PreparedStatement stmt = main.getConnection().prepareStatement("SELECT akku FROM phone WHERE nrp_id = ?")) {
                stmt.setInt(1, Script.getNRPID(p));
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return rs.getInt("akku");
                } else {
                    Script.executeAsyncUpdate("INSERT INTO phone (nrp_id, phone_id, akku) VALUES (" + Script.getNRPID(p) + ", " + getID() + ", " + getMaxAkku() + ")");
                    return 100;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public void setAkku(Player p, int akku) {
            Script.executeAsyncUpdate("UPDATE phone SET akku = " + akku + " WHERE nrp_id = " + Script.getNRPID(p) + " AND phone_id = " + getID());
        }

        public void addAkku(Player p, int akku) {
            akku = getAkku(p) + akku;
            Script.executeAsyncUpdate("UPDATE phone SET akku = " + akku + " WHERE nrp_id = " + Script.getNRPID(p) + " AND phone_id = " + getID());
        }

        public void removeAkku(Player p, int akku) {
            akku = getAkku(p) - akku;
            Script.executeAsyncUpdate("UPDATE phone SET akku = " + akku + " WHERE nrp_id = " + Script.getNRPID(p) + " AND phone_id = " + getID());
        }

        public boolean hasCloud(Player p) {
            try(PreparedStatement stmt = main.getConnection().prepareStatement("SELECT cloud FROM handy_settings WHERE nrp_id = ?")) {
                stmt.setInt(1, Script.getNRPID(p));
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return rs.getBoolean("cloud");
                } else {
                    Script.executeAsyncUpdate("INSERT INTO handy_settings (nrp_id, cloud, lautlos) VALUES (" + Script.getNRPID(p) + ", " + "false" + ", false)");
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void setCloud(Player p, boolean cloud) {
            Script.executeAsyncUpdate("UPDATE handy_settings SET cloud = " + cloud + " WHERE nrp_id = " + Script.getNRPID(p));
        }

        public boolean getLautlos(Player p) {
            try(PreparedStatement stmt = main.getConnection().prepareStatement("SELECT lautlos FROM handy_settings WHERE nrp_id = ?")) {
                stmt.setInt(1, Script.getNRPID(p));
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return rs.getBoolean("lautlos");
                } else {
                    Script.executeAsyncUpdate("INSERT INTO handy_settings (nrp_id, cloud, lautlos) VALUES (" + Script.getNRPID(p) + ", " + "false" + ", false)");
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void setLautlos(Player p, boolean lautlos) {
            Script.executeAsyncUpdate("UPDATE handy_settings SET lautlos = " + lautlos + " WHERE nrp_id = " + Script.getNRPID(p));
        }

    }

    public static boolean hasCloud(Player p) {
        try(PreparedStatement stmt = main.getConnection().prepareStatement("SELECT cloud FROM handy_settings WHERE nrp_id = ?")) {
            stmt.setInt(1, Script.getNRPID(p));
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getBoolean("cloud");
            } else {
                Script.executeAsyncUpdate("INSERT INTO handy_settings (nrp_id, cloud, lautlos) VALUES (" + Script.getNRPID(p) + ", " + "false" + ", false)");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasPhone(Player p) {
        return getPhone(p) != null;
    }

    public static Phones getPhone(Player p) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null) continue;
            for(Phones phones : Phones.values()) {
                if(Objects.requireNonNull(is.getItemMeta()).getDisplayName().replace("§7", "").replace("§c","").startsWith(phones.getName())) {
                    return phones;
                }
            }
        }
        return null;
    }

    public static boolean isPhone(ItemStack is) {
        if(is == null) return false;
        for(Phones phones : Phones.values()) {
            if(Objects.requireNonNull(is.getItemMeta()).getDisplayName().replace("§7", "").replace("§c","").startsWith(phones.getName())) {
                return true;
            }
        }
        return false;
    }

    public static void removePhone(Player p) {
        p.getInventory().remove(getPhone(p).getItem());
    }

    public static void addPhone(Player p, Phones phone) {
        p.getInventory().addItem(phone.getItem());
    }

    public static void setPhone(Player p, Phones phone) {
        removePhone(p);
        addPhone(p, phone);
    }

    public static boolean hasConnection(Player p) {
        Phones phone = getPhone(p);
        if(phone == null) return false;
        Location loc = p.getLocation();
        int blocks = 0;
        for(int i = loc.getBlockY(); i < 256; i++) {
            if (loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).getType() != Material.AIR) {
                blocks++;
            }
        }
        return blocks <= phone.getConnection();
    }

    public static boolean mobileIsOn(Player p) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null) continue;
            if(is.getType() == Material.IRON_INGOT) {
                return is.getItemMeta().getDisplayName().startsWith("§7");
            }
        }
        return false;
    }

    public static void sendCallHistory(Player p) {
        try(PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM call_history WHERE nrp_id = ?")) {
            stmt.setInt(1, Script.getNRPID(p));
            ResultSet rs = stmt.executeQuery();
            p.sendMessage("§8» §7Anrufverlauf:");
            while (rs.next()) {
                p.sendMessage("§8» §7" + rs.getString("participants") + " §8× §7" + Script.dateFormat.format(rs.getLong("time")) + " Uhr");
            }
            if(!rs.next()) {
                p.sendMessage("§8» §7Du hast noch keine Anrufe getätigt.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        };
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!isPhone(p.getInventory().getItemInMainHand())) return;
        if(!mobileIsOn(p)) {
            long time = System.currentTimeMillis();

            Long lastUsage = TOOGLE_COOLDOWN.get(p.getName());
            if (lastUsage != null && lastUsage + TimeUnit.MINUTES.toMillis(4) > time) {
                return;
            }

            Long lastClick = LAST_CLICK.get(p.getName());
            if (lastClick == null) {
                LAST_CLICK.put(p.getName(), time);
                return;
            }

            long difference = time - lastClick;
            if (difference >= 800) LEVEL.remove(p.getName());

            int level = LEVEL.computeIfAbsent(p.getName(), k -> 0);

            LAST_CLICK.put(p.getName(), time);
            LEVEL.replace(p.getName(), level + 1);
            progressBar(11,  p);

            if (level >= 10) {
                PlayerInventory inv = p.getInventory();
                ItemStack is = inv.getItemInMainHand();
                if (is.getAmount() > 1) {
                    is.setAmount(is.getAmount() - 1);
                } else {
                    inv.setItemInMainHand(new ItemStack(Material.AIR));
                }

                if (Health.BLEEDING.containsKey(p.getName())) {
                    float amount = Health.BLEEDING.get(p.getName());
                    if (amount < 1F) {
                        Health.BLEEDING.remove(p.getName());
                    }
                }

                Me.sendMessage(p, "schaltet " + (Script.getGender(p)==Gender.MALE?"sein":"ihr") + " Handy ein.");
                p.getInventory().setItemInHand(new ItemBuilder(Material.IRON_INGOT).setName("§7" + getPhone(p).getName()).build());

                TOOGLE_COOLDOWN.put(p.getName(), time);
                LAST_CLICK.remove(p.getName());
                LEVEL.remove(p.getName());
            }
            return;
        }

        openGUI(p);

    }

    public static void openGUI(Player p) {
        Mobile.Phones phone = Mobile.getPhone(p);
        assert phone != null;
        Inventory inv = Bukkit.createInventory(null, 9, "§8» §aHandy");
        inv.setItem(0, new ItemBuilder(Material.CHEST).setName("§8» §cAusschalten").build());
        inv.setItem(1, new ItemBuilder(Material.CHEST).setName("§8» §cNachrichten").build());
        inv.setItem(2, new ItemBuilder(Material.CHEST).setName("§8» §cTelefon").build());
        inv.setItem(3, new ItemBuilder(Material.CHEST).setName("§8» §cEinstellungen").build());
        inv.setItem(4, new ItemBuilder(Material.CHEST).setName("§8» §cNotruf").build());
        inv.setItem(5, new ItemBuilder(Material.CHEST).setName("§8» §cNavigation").build());
        inv.setItem(6, new ItemBuilder(Material.CHEST).setName("§8» §cAkku").setLore("§8 × §6" + Script.getPercentage(phone.getAkku(p), phone.getMaxAkku()) + "%").build());
        inv.setItem(7, new ItemBuilder(Material.CHEST).setName("§8» §cSMS").build());
        p.openInventory(inv);
    }

    public static void openSettingGUI(Player p) {
        Mobile.Phones phone = Mobile.getPhone(p);
        assert phone != null;
        Inventory inv = Bukkit.createInventory(null, 9, "§8» §aHandy");
        inv.setItem(0, new ItemBuilder(Material.CHEST).setName("§8» §cWerkseinstellungen").build());
        inv.setItem(1, new ItemBuilder(Material.CHEST).setName("§8» §cCloud").setLore("§8 × §6Aktiviere die Cloud um Daten auf ein neues Handy automatisch zu übertragen.", "§8 × §6Dies kostet " + (Premium.hasPremium(p)?"5€":"10€"), "§8 × §6Aktiviert: " + (phone.hasCloud(p)?"§aJa":"§cNein")).build());
        inv.setItem(2, new ItemBuilder(Material.CHEST).setName("§8» §cTöne").setLore("§8 × §6Aktiviere Töne um Benachrichtigungen zu erhalten.", "§8 × §6Aktiviert: " + (phone.getLautlos(p)?"§cNein":"§aJa")).build());
        p.openInventory(inv);
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equals("§8» §aHandy")) return;
        e.setCancelled(true);
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cAusschalten")) {
            p.getInventory().setItemInHand(new ItemBuilder(Material.IRON_INGOT).setName("§c" + getPhone(p).getName()).build());
            p.closeInventory();
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cNachrichten")) {
            p.closeInventory();
            p.sendMessage(Messages.ERROR + "Dieser Bereich ist noch nicht verfügbar.");
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cTelefon")) {
            p.closeInventory();
            sendCallHistory(p);
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cEinstellungen")) {
            p.closeInventory();
            openSettingGUI(p);
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cNotruf")) {
            p.closeInventory();
            Notruf.openGUI(p, Notruf.Questions.FRAGE1);
            Me.sendMessage(p, "wählt den Notruf auf seinem Handy.");
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cNavigation")) {
            p.closeInventory();
            p.sendMessage(PREFIX + "Du befindest dich in der Nähe von " + Navi.getNextNaviLocation(p.getLocation()).getName());
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cAkku")) {
            p.closeInventory();
            p.sendMessage(PREFIX + "Dein Akku ist zu " + Script.getPercentage(getPhone(p).getAkku(p), getPhone(p).getMaxAkku()) + "% geladen.");
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cSMS")) {
            p.closeInventory();
            p.sendMessage(Messages.ERROR + "Dieser Bereich ist noch nicht verfügbar.");
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cWerkseinstellungen")) {
            p.closeInventory();
            Script.executeAsyncUpdate("DELETE FROM handy_settings WHERE nrp_id = " + Script.getNRPID(p));
            p.sendMessage(PREFIX + "Du hast die Werkseinstellungen wiederhergestellt.");
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cCloud")) {
            p.closeInventory();
            if(getPhone(p).hasCloud(p)) {
                getPhone(p).setCloud(p, false);
                p.sendMessage(PREFIX + "Du hast die Cloud deaktiviert.");
                return;
            } else {
                getPhone(p).setCloud(p, true);
                p.sendMessage(PREFIX + "Du hast die Cloud aktiviert.");
                return;
            }
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§8» §cTöne")) {
            p.closeInventory();
            if(getPhone(p).getLautlos(p)) {
                getPhone(p).setLautlos(p, false);
                p.sendMessage(PREFIX + "Du hast die Töne aktiviert.");
                return;
            } else {
                getPhone(p).setLautlos(p, true);
                p.sendMessage(PREFIX + "Du hast die Töne deaktiviert.");
                return;
            }
        }

    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
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
        Script.sendActionBar(p, "§cVerband anlegen.. §8» §a" + sb.toString());
    }


}
