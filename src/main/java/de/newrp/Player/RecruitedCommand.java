package de.newrp.Player;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecruitedCommand implements @Nullable CommandExecutor {

    public static final String PREFIX = "§8[§dUwU§8] §d" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage(PREFIX + "Angeworbene Spieler:");
            if (args.length == 0) {
                for (Integer id : Objects.requireNonNull(getRecruited(Script.getNRPID(player)))) {
                    String name = Objects.requireNonNull(Script.getOfflinePlayer(id)).getName();
                    player.sendMessage("§8      - §7" + name);
                }
            } else {
                if (isRecruited(Script.getNRPID(player))) {
                    player.sendMessage(Messages.ERROR + "Du wurdest bereits angeworben!");
                    return true;
                }

                if (Script.getLevel(player) < 3) {
                    player.sendMessage(Messages.ERROR + "Du musst mindestens Level-3 sein um als angeworben zu gelten!");
                    return true;
                }

                if (Script.getLevel(player) > 5) {
                    player.sendMessage(Messages.ERROR + "Das Anwerben ist nur bis Level-5 gültig!");
                    return true;
                }

                Player recruiter = Script.getPlayer(args[0]);
                if (recruiter != null) {
                    addRecruited(Script.getNRPID(player), Script.getNRPID(recruiter));
                    player.sendMessage(PREFIX + "Du hast " + recruiter.getName() + " als Anwerber angegeben und 1000€ erhalten.");
                    Script.addMoney(player, PaymentType.CASH, 1000);
                    player.sendMessage(PREFIX + player.getName() + " hat dich als Anwerber angegeben.");
                } else {
                    player.sendMessage(Messages.ERROR + "Der Spieler " + args[0] + " wurde nicht gefunden!");
                }
            }
        }

        return true;
    }

    public static List<Integer> getRecruited(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recruited WHERE recruiter = " + id)) {
            List<Integer> list = new ArrayList<>();
            while (rs.next()) list.add(rs.getInt("nrp_id"));
            return list;
        } catch (SQLException ex) {
            Debug.debug("SQLException -> " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static int getRecruiter(int rid) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recruited WHERE nrp_id = " + rid)) {
            if (rs.next()) return rs.getInt("recruiter");
        } catch (SQLException ex) {
            Debug.debug("SQLException -> " + ex.getMessage());
            ex.printStackTrace();
        }
        return 0;
    }

    public static boolean isRecruited(int rid, int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recruited WHERE nrp_id = " + rid)) {
            if (rs.next()) return rs.getInt("recruiter") == id;
        } catch (SQLException ex) {
            Debug.debug("SQLException -> " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isRecruited(int rid) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM recruited WHERE nrp_id = " + rid)) {
            if (rs.next()) return true;
        } catch (SQLException ex) {
            Debug.debug("SQLException -> " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public static void addRecruited(int rid, int id) {
        Script.executeAsyncUpdate("INSERT INTO recruited (nrp_id, recruiter) VALUES (" + rid + ", " + id + ");");
    }
}
