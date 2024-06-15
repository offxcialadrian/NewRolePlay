package de.newrp.Administrator;

import de.newrp.API.Friedhof;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;

public class GetHere implements CommandExecutor {

    public static String PREFIX = "§8[§eTeleport§8] §e" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.DEVELOPER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/tphere [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {

            OfflinePlayer offtg = Script.getOfflinePlayer(args[0]);
            if(Script.getNRPID(offtg) == 0) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(hasOfflineTP(offtg)) {
                Script.executeUpdate("DELETE FROM offline_tp WHERE nrp_id=" + Script.getNRPID(offtg) + " ORDER BY id DESC LIMIT 1;");
            }

            Script.executeUpdate("INSERT INTO offline_tp (nrp_id, x, y, z) VALUES (" + Script.getNRPID(offtg) + ", " + (int) p.getLocation().getY() + ", " + (int) p.getLocation().getX() + ", " + (int) p.getLocation().getY() + ");");
            p.sendMessage(PREFIX + "Du hast " + Script.getName(offtg) + " zu dir teleportiert.");

            return true;
        }

        if(Friedhof.isDead(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist tot.");
            return true;
        }

        if (tg == p) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst teleportieren.");
            return true;
        }

        Teleport.back.put(tg, tg.getLocation());
        tg.teleport(p.getLocation());
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " zu dir teleportiert.");
        tg.sendMessage(PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat dich zu sich teleportiert.");
        Script.sendTeamMessage(p, ChatColor.YELLOW, "hat " + Script.getName(tg) + " zu sich teleportiert.", true);

        return false;
    }

    public static Location getOfflineTP(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM offline_tp WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                return new Location(Script.WORLD, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
       }
        return null;
    }

    public static boolean hasOfflineTP(OfflinePlayer p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM offline_tp WHERE nrp_id=" + Script.getNRPID(p))) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasOfflineTP(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM offline_tp WHERE nrp_id=" + Script.getNRPID(p))) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
