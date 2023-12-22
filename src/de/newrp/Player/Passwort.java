package de.newrp.Player;

import de.newrp.API.AES;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Passwort implements CommandExecutor, Listener {

    private static String PREFIX = "§8[§6Passwort§8] §8» §7";
    static final List<String> locked = new ArrayList<>();

    public static boolean isLocked(Player p) {
        return locked.contains(p.getName());
    }

    public static void lock(Player p) {
        if (!isLocked(p)) locked.add(p.getName());
    }

    public static void unlock(Player p) {
        if (isLocked(p)) locked.remove(p.getName());
    }

    public static boolean hasPasswort(Player p) {
        boolean b = false;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM password WHERE nrp_id= '" + Script.getNRPID(p) + "'")) {
            b = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static void setPasswort(Player p, String s) {
        Script.executeAsyncUpdate("INSERT INTO password(nrp_id, password) VALUES(" + Script.getNRPID(p) + ", '" + s + "');");
    }

    public static void removePasswort(Player p) {
        Script.executeAsyncUpdate("DELETE FROM password WHERE nrp_id=" + Script.getNRPID(p));
    }

    public static void removePasswort(OfflinePlayer p) {
        Script.executeAsyncUpdate("DELETE FROM password WHERE nrp_id=" + Script.getNRPID(p));
    }


    public static boolean usePasswort(Player p, String passwort) {
        String pw = null;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM password WHERE nrp_id= '" + Script.getNRPID(p) + "'")) {
            if (rs.next()) {
                pw = rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (pw != null) {
            try {
                return pw.equals(AES.encode(passwort));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if(args.length == 1 && isLocked(p)) {
            if(usePasswort(p, args[0])) {
                unlock(p);
                p.sendMessage(Messages.INFO + "Du hast dich erfolgreich eingeloggt.");
                Script.sendActionBar(p, "§e§lPasswort §8» §2Richtig!");
            } else {
                p.sendMessage(Messages.ERROR + "Das Passwort ist falsch.");
            }
            return true;
        }

        if (isLocked(p)) {
            p.sendMessage(Messages.ERROR + "Du musst dein Passwort eingeben.");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            if(!hasPasswort(p)) {
                p.sendMessage(Messages.ERROR + "Du hast kein Passwort.");
                return true;
            }
            removePasswort(p);
            p.sendMessage(PREFIX + "Dein Passwort wurde entfernt.");
            return true;
        }

        if(args.length > 1 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("new"))) {

            if(hasPasswort(p)) {
                p.sendMessage(Messages.ERROR + "Du hast bereits ein Passwort.");
                return true;
            }

            if(args[1].length() < 5 || args[1].length() > 20) {
                p.sendMessage(Messages.ERROR + "Dein Passwort muss zwischen 5 & 20 Zeichen haben.");
                return true;
            }
            if(!Script.isValidPassword(args[1])) {
                p.sendMessage(Messages.ERROR + "Dein Passwort muss mindestens 1 Großbuchstaben, 1 Kleinbuchstaben, 1 Sonderzeichen und 1 Zahl enthalten.");
                return true;
            }
            try {
                String s1 = AES.encode(args[1]);
                setPasswort(p, s1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            p.sendMessage("§eDein Account wird nun mit einem Passwort geschützt.");
            Script.sendActionBar(p, "§e§lPasswort §8» §2Aktiviert!");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
            p.sendMessage("§8» §6§lPasswort §8| §7Hilfe");
            p.sendMessage("§8» §7/passwort set [Passwort] §8| §7Setzt dein Passwort.");
            p.sendMessage("§8» §7/passwort remove §8| §7Entfernt dein Passwort.");
            p.sendMessage("§8» §7/passwort info §8| §7Zeigt Informationen über dein Passwort an.");
            p.sendMessage("§8» §7/passwort help §8| §7Zeigt diese Hilfe an.");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
            p.sendMessage(Messages.INFO + "Dein Passwort ist in unserer Datenbank sicher verschlüsselt.");
            p.sendMessage(Messages.INFO + "Unsere Datenbank wird regelmäßig von externen Sicherheitsfirmen überprüft.");
            return true;
        }

        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (Passwort.hasPasswort(p)) {
            Passwort.lock(p);
            p.sendMessage(Messages.INFO + "Gebe bitte dein Passwort ein. /passwort [Passwort]");
            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                if (Passwort.isLocked(p)) {
                    p.sendMessage(PREFIX + "Bitte gebe dein Passwort innerhalb der nächsten 10 Sekunden ein");
                }
            }, 880L);
            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                if (Passwort.isLocked(p)) {
                    p.sendMessage(PREFIX + "Bitte gebe dein Passwort innerhalb der nächsten 3 Sekunden ein");
                }
            }, 1144L);
            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                if (Passwort.isLocked(p)) {
                    p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKICK §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §eDu hast dein Passwort nicht eingegeben\n\n§7Solltest du dein Passwort vergessen haben, melde dich bitte auf unserem Discord beim Support!\n\n§8§m------------------------------");
                }
            }, 1408L);
        }
        if (!Passwort.hasPasswort(p)) {
            p.sendMessage("");
            p.sendMessage("§8» §cDein Account wird §4nicht §cmit einem Passwort geschützt!");
            p.sendMessage("§8» §cSchütze deinen Account mit §6/passwort set [Passwort]§c.");
            p.sendMessage("");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Passwort.unlock(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (Passwort.isLocked(p)) {
            Location from = e.getFrom();
            Location to = e.getTo();
            double x = Math.floor(from.getX());
            double z = Math.floor(from.getZ());
            if (Math.floor(to.getX()) != x || Math.floor(to.getZ()) != z) {
                x += .1;
                z += .1;
                e.getPlayer().teleport(new Location(from.getWorld(), x, from.getY(), z, from.getYaw(), from.getPitch()));
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (Passwort.isLocked(e.getPlayer())) {
            e.setCancelled((!e.getMessage().startsWith("/passwort")) && (!e.getMessage().startsWith("/password")));
        }
    }
}
