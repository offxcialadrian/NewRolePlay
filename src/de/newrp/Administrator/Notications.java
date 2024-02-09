package de.newrp.Administrator;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.sql.ResultSet;
import java.sql.Statement;

public class Notications implements CommandExecutor, Listener {

    public enum NotificationType {
        JOIN(1, "join", "Join-Notification"),
        LEAVE(2, "leave", "Leave-Notification"),
        PAYMENT(3, "payment", "Payment-Notification"),
        COMMAND(4, "command", "Command-Spy"),
        CHAT(5, "chat", "Chat-Spy"),
        DEAD(6, "dead", "Todes-Notification"),
        SHOP(7, "shop", "Shop-Notification"),
        ADVANCED_ANTI_CHEAT(8, "aac", "Advanced-Anti-Cheat-Notification"),
        REGISTRATION(9, "registration", "Registration-Notification");

        private final int id;
        private final String dbname;
        private final String name;

        NotificationType(int id, String dbname, String name) {
            this.id = id;
            this.dbname = dbname;
            this.name = name;
        }

        public int getID() {
            return id;
        }

        public String getDBName() {
            return dbname;
        }

        public String getName() {
            return name;
        }
    }

    private static final String PREFIX = "§8[§aNotifications§8] §a" + Messages.ARROW + " ";

    private static boolean isNotificationEnabled(Player p, NotificationType type) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM notifications WHERE nrp_id = '" + Script.getNRPID(p) + "' AND notification_id = '" + type.getID() + "'")) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void sendMessage(NotificationType type, String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isNotificationEnabled(p, type)) {
                p.sendMessage((type == NotificationType.ADVANCED_ANTI_CHEAT ? AntiCheatSystem.PREFIX : PREFIX) + msg);
            }
        }
    }

    public static void sendMessage(NotificationType type, String msg, Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isNotificationEnabled(p, type)) {
                if (p != player)
                    p.sendMessage((type == NotificationType.ADVANCED_ANTI_CHEAT ? AntiCheatSystem.PREFIX : PREFIX) + msg);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/notifications");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§8[§aNotifications§8]");
        for (NotificationType type : NotificationType.values()) {
            inv.setItem(type.getID() - 1, (isNotificationEnabled(p, type) ? new ItemBuilder(Material.REDSTONE_BLOCK).setName("§c" + type.getName()).setLore(" §7" + Messages.ARROW + " Deaktiviere " + type.getName()).build() : new ItemBuilder(Material.EMERALD_BLOCK).setName("§a" + type.getName()).setLore(" §7" + Messages.ARROW + " Aktiviere " + type.getName()).build()));
        }
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§8[§aNotifications§8]")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
                for (NotificationType type : NotificationType.values()) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§a" + type.getName())) {
                        Script.executeUpdate("INSERT INTO notifications (nrp_id, notification_id) VALUES ('" + Script.getNRPID(p) + "', '" + type.getID() + "')");
                        p.sendMessage(PREFIX + "Du hast die " + type.getName() + " aktiviert.");
                        p.closeInventory();
                        return;
                    }
                }
            } else if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
                for (NotificationType type : NotificationType.values()) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§c" + type.getName())) {
                        Script.executeUpdate("DELETE FROM notifications WHERE nrp_id = '" + Script.getNRPID(p) + "' AND notification_id = '" + type.getID() + "'");
                        p.sendMessage(PREFIX + "Du hast die " + type.getName() + " deaktiviert.");
                        p.closeInventory();
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        sendMessage(NotificationType.LEAVE, "§e" + Script.getName(e.getPlayer()) + " §7hat den Server verlassen.");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/sudo")) return;
        if (e.getMessage().startsWith("/passwort")) return;
        if (e.getMessage().startsWith("/password")) return;
        if (e.getMessage().startsWith("/schreien")) return;
        if (e.getMessage().startsWith("/whisper")) return;
        if (e.getMessage().startsWith("/rnrp")) return;
        if (e.getMessage().startsWith("/nrp")) return;
        if (e.getMessage().startsWith("/tc")) return;
        if (e.getMessage().startsWith("/op") || e.getMessage().startsWith("/deop") || e.getMessage().startsWith("/gamemode") || e.getMessage().startsWith("/punish") || e.getMessage().startsWith("/nrp") || e.getMessage().startsWith("/setsupporter") ||
            e.getMessage().startsWith("/rnrp") || e.getMessage().startsWith("/tp")) {
            if (!Script.isNRPTeam(e.getPlayer()))
                sendMessage(NotificationType.ADVANCED_ANTI_CHEAT, "§c" + Script.getName(e.getPlayer()) +  " hat versucht einen Team-Befehl auszuführen (" + e.getMessage() + ")");
    }
        sendMessage(NotificationType.COMMAND, "§e" + Script.getName(e.getPlayer()) + " §7hat den Befehl §e" + e.getMessage() + " §7ausgeführt.", e.getPlayer());
    }


}
