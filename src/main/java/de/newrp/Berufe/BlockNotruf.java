package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlockNotruf implements CommandExecutor {

    public static String PREFIX = "§8[§cNotrufe§8] §c» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST) && !Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        Beruf.Berufe b = Beruf.getBeruf(p);

        if (args.length == 0) {
            p.sendMessage(PREFIX + "Die Notrufe von folgenden Personen sind blockiert:");
            StringBuilder sb = new StringBuilder();
            for (int unicacid : getBlockedNRPIDs(b)) {
                OfflinePlayer banned = Script.getOfflinePlayer(unicacid);
                sb.append("\n  §7»§6 ").append(Script.getNameInDB(banned));
            }
            p.sendMessage(sb.toString());
            return true;
        }

        if (args.length == 1) {
            OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
            if (!isBlocked(b, tg)) {
                p.sendMessage(PREFIX + "Der Notruf von " + Script.getNameInDB(tg) + " ist nun blockiert.");
                b.sendMessage(PREFIX + "Der Notruf von " + Script.getNameInDB(tg) + " wurden von " + Beruf.getAbteilung(p).getName() + " " + Script.getName(p) + " blockiert.");
                block(b, tg);
            } else {
                p.sendMessage(PREFIX + "Der Notruf von " + Script.getNameInDB(tg) + " ist nun nicht mehr blockiert.");
                b.sendMessage(PREFIX + "Der Notruf von " + Script.getNameInDB(tg) + " wurden von " + Beruf.getAbteilung(p).getName() + " " + Script.getName(p) + " wieder freigegeben.");
                unblock(b, tg);
            }
            return true;
        }


        return false;
    }

    private static List<Integer> getBlockedNRPIDs(Beruf.Berufe b) {
        List<Integer> list = new ArrayList<>();
        try (PreparedStatement statement = Main.getConnection().prepareStatement(
                "SELECT nrp_id FROM blocked_notruf WHERE berufID = ?")) {
            statement.setInt(1, b.getID());
            statement.setLong(2, System.currentTimeMillis());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("nrp_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isBlocked(Beruf.Berufe b, OfflinePlayer tg) {
        try (PreparedStatement statement = Main.getConnection().prepareStatement(
                "SELECT * FROM blocked_notruf WHERE berufID = ? AND nrp_id = ?")) {
            statement.setInt(1, b.getID());
            statement.setInt(2, Script.getNRPID(tg));
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void block(Beruf.Berufe b, OfflinePlayer tg) {
        try (PreparedStatement statement = Main.getConnection().prepareStatement(
                "INSERT INTO blocked_notruf (nrp_id, berufID) VALUES (?, ?)")) {
            statement.setInt(1, Script.getNRPID(tg));
            statement.setInt(2, b.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unblock(Beruf.Berufe b, OfflinePlayer tg) {
        try (PreparedStatement statement = Main.getConnection().prepareStatement(
                "DELETE FROM blocked_notruf WHERE nrp_id = ? AND berufID = ?")) {
            statement.setInt(1, Script.getNRPID(tg));
            statement.setInt(2, b.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
