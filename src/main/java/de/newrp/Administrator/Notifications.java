package de.newrp.Administrator;

import com.google.common.collect.Sets;
import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Notifications implements CommandExecutor, Listener {

    private static final Map<UUID, Set<NotificationType>> NOTIFICATION_CACHE = new HashMap<>();

    public enum NotificationType {
        JOIN(1, "join", "Join-Notification"),
        LEAVE(2, "leave", "Quit-Notification"),
        PAYMENT(3, "payment", "Payment-Notification"),
        COMMAND(4, "command", "Command-Spy"),
        CHAT(5, "chat", "Chat-Spy"),
        DEAD(6, "dead", "Todes-Notification"),
        SHOP(7, "shop", "Shop-Notification"),
        ADVANCED_ANTI_CHEAT(8, "aac", "Advanced-Anti-Cheat-Notification"),
        REGISTRATION(9, "registration", "Registration-Notification"),
        DEBUG(10, "debug", "Debug-Notification"),
        NRPSHOP(11, "nrpshop", "NRPSHOP-Notification");

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

    public static void loadNotificationsForPlayer(final Player player) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT notification_id FROM notifications WHERE nrp_id = '" + Script.getNRPID(player) + "'")) {
            while(rs.next()) {
                if(!NOTIFICATION_CACHE.containsKey(player.getUniqueId())) {
                    NOTIFICATION_CACHE.put(player.getUniqueId(), Sets.newHashSet(getNotificationById(rs.getInt(1))));
                } else {
                    NOTIFICATION_CACHE.get(player.getUniqueId()).add(getNotificationById(rs.getInt(1)));
                }
            }
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }

    private static boolean isNotificationEnabled(Player p, NotificationType type) {
        if(NOTIFICATION_CACHE.containsKey(p.getUniqueId())) {
            return NOTIFICATION_CACHE.get(p.getUniqueId()).contains(type);
        }
        return false;
    }

    public static NotificationType getNotificationById(final int id) {
        for (final NotificationType notificationType : NotificationType.values()) {
            if(notificationType.id == id) {
                return notificationType;
            }
        }
        return NotificationType.DEAD;
    }

    public static void sendMessage(NotificationType type, String msg) {
        final List<UUID> hasReceived = new ArrayList<>();
        // members of the nrp team shouldn't receive sql errors etc to protect internal structure
        if (type != NotificationType.DEBUG) {
            for (Player p : Script.getNRPTeam()) {
                hasReceived.add(p.getUniqueId());
                if (isNotificationEnabled(p, type)) {
                    if(msg.contains("/sql")) continue; // members of the nrp team shouldn't see internal tables, which they do if they see a /sql command being ran
                    p.sendMessage((type == NotificationType.ADVANCED_ANTI_CHEAT ? AntiCheatSystem.PREFIX : PREFIX) + msg);
                }
            }
        }

        for (UUID member : Team.Teams.ENTWICKLUNG.getMembers()) {
            if(hasReceived.contains(member)) continue;
            final Player p = Bukkit.getPlayer(member);
            if(p == null) continue;

            if (isNotificationEnabled(p, type)) {
                p.sendMessage((type == NotificationType.ADVANCED_ANTI_CHEAT ? AntiCheatSystem.PREFIX : PREFIX) + msg);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.DEVELOPER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/notifications");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, Script.calcInvSize(NotificationType.values().length), "§8[§aNotifications§8]");
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
                        if(NOTIFICATION_CACHE.containsKey(p.getUniqueId())) {
                            NOTIFICATION_CACHE.get(p.getUniqueId()).add(type);
                        } else {
                            NOTIFICATION_CACHE.put(p.getUniqueId(), Sets.newHashSet(type));
                        }

                        p.sendMessage(PREFIX + "Du hast die " + type.getName() + " aktiviert.");
                        e.getInventory().setItem(e.getSlot(), new ItemBuilder(Material.REDSTONE_BLOCK).setName("§c" + type.getName()).setLore(" §7" + Messages.ARROW + " Deaktiviere " + type.getName()).build());
                        return;
                    }
                }
            } else if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
                for (NotificationType type : NotificationType.values()) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§c" + type.getName())) {
                        Script.executeUpdate("DELETE FROM notifications WHERE nrp_id = '" + Script.getNRPID(p) + "' AND notification_id = '" + type.getID() + "'");
                        p.sendMessage(PREFIX + "Du hast die " + type.getName() + " deaktiviert.");
                        if(NOTIFICATION_CACHE.containsKey(p.getUniqueId())) {
                            NOTIFICATION_CACHE.get(p.getUniqueId()).remove(type);
                        } else {
                            NOTIFICATION_CACHE.put(p.getUniqueId(), Sets.newHashSet());
                        }
                        e.getInventory().setItem(e.getSlot(), new ItemBuilder(Material.EMERALD_BLOCK).setName("§a" + type.getName()).setLore(" §7" + Messages.ARROW + " Aktiviere " + type.getName()).build());
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
        if (e.getMessage().startsWith("/spec")) return;
        if (e.getMessage().startsWith("/tc")) return;
        // Just block sql command spy for sql to prevent leaking internal db info to sups, mods
        if (e.getMessage().startsWith("/sql")) return;
        Log.COMMAND.write(e.getPlayer(), e.getMessage());
        if (e.getMessage().startsWith("/op") || e.getMessage().startsWith("/deop") || e.getMessage().startsWith("/gamemode") || e.getMessage().startsWith("/punish") || e.getMessage().startsWith("/nrp") || e.getMessage().startsWith("/setsupporter") ||
            e.getMessage().startsWith("/rnrp") || e.getMessage().startsWith("/tp")) {
            if (!Script.isNRPTeam(e.getPlayer()))
                sendMessage(NotificationType.ADVANCED_ANTI_CHEAT, "§c" + Script.getName(e.getPlayer()) + " hat versucht einen Team-Befehl auszuführen (" + e.getMessage() + ")");
        }
        sendMessage(NotificationType.COMMAND, "§e" + Script.getName(e.getPlayer()) + " §7hat den Befehl §e" + e.getMessage() + " §7ausgeführt.");
    }


}
