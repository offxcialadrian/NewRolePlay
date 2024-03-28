package de.newrp.Player;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BeziehungCommand implements CommandExecutor {

    public static String PREFIX = "§8[§c§l♥§8] §c» ";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            if(hasRelationship(p)) {
                int days = (int) ((System.currentTimeMillis() - getSince(p)) / 86400000);
                p.sendMessage(PREFIX + "Du bist mit " + Script.getNameInDB(getPartner(p)) + " seit " + days + " Tagen zusammen.");
                return true;
            } else {
                p.sendMessage(Messages.ERROR + "/beziehung [Spieler]");
            }
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/beziehung [Spieler]");
            return true;
        }

        if (hasRelationship(p)) {
            p.sendMessage(Messages.ERROR + "Du hast bereits eine Beziehung.");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (hasRelationship(tg)) {
            p.sendMessage(Messages.ERROR + Script.getName(tg) + " ist bereits in einer Beziehung :(.");
            return true;
        }

        if (tg == p) {
            p.sendMessage(Messages.ERROR + "Du kannst nicht mit dir selbst in einer Beziehung sein.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist zu weit weg von " + Script.getName(tg) + ".");
            return true;
        }

        Annehmen.offer.put(tg.getName() + ".beziehung", p.getName());
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " gefragt, ob " + (Script.getGender(tg) == Gender.MALE ? "er" : "sie") + " mit dir zusammen sein möchte.");
        tg.sendMessage(PREFIX + Script.getName(p) + " hat dich ob du mit " + (Script.getGender(p) == Gender.MALE ? "ihm" : "ihr") + " zusammen sein möchtest.");
        Script.sendAcceptMessage(tg);


        return false;
    }

    public static boolean hasRelationship(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM relationship WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'")) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasRelationship(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM relationship WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'")) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMarried(Player p) {
        if (!hasRelationship(p)) return false;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM relationship WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getBoolean("married");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getSince(Player p) {
        if (!hasRelationship(p)) return 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM relationship WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'")) {
            if (rs.next()) {
                return rs.getLong("since");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static OfflinePlayer getPartner(Player p) {
        if (!hasRelationship(p)) return null;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM relationship WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'")) {
            if (rs.next()) {
                if (rs.getInt("person1") == Script.getNRPID(p)) {
                    return Script.getOfflinePlayer(rs.getInt("person2"));
                } else {
                    return Script.getOfflinePlayer(rs.getInt("person1"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setMarried(Player p, boolean married) {
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE relationship SET married = '" + (married ? 1 : 0) + "' WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setMarried(OfflinePlayer p, boolean married) {
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE relationship SET married = '" + (married ? 1 : 0) + "' WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void breakup(Player p) {
        if (!hasRelationship(p)) return;
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM relationship WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void breakup(OfflinePlayer p) {
        if (!hasRelationship(p)) return;
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM relationship WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setPartner(Player p, OfflinePlayer partner) {
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE relationship SET person1 = '" + Script.getNRPID(p) + "', person2 = '" + Script.getNRPID(partner) + "' WHERE person1 = '" + Script.getNRPID(p) + "' OR person2 = '" + Script.getNRPID(p) + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createRelationship(Player p, OfflinePlayer partner) {
        try (Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO relationship (person1, person2, married, since) VALUES ('" + Script.getNRPID(p) + "', '" + Script.getNRPID(partner) + "', 0, " + System.currentTimeMillis() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
