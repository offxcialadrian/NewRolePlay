package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.NewRoleplayMain;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class PremiumCommand implements CommandExecutor {

    public static final String PREFIX = "§8[§bPremium§8] §b" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length >= 2 && args[0].equalsIgnoreCase("feedback")) {
            if(!AddPremiumToPlayer.awaitFeedback.containsKey(p.getName())) {
                p.sendMessage(Messages.ERROR + "Du hast keine ausstehenden Feedbacks.");
                return true;
            }

            if(args.length < 5) {
                p.sendMessage(Messages.ERROR + "Bitte gebe dein Feedback ausführlicher an.");
                return true;
            }

            StringBuilder feedback = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                feedback.append(args[i]).append(" ");
            }

            p.sendMessage(PREFIX + "Vielen Dank für dein Feedback.");
            p.sendMessage(Messages.INFO + "Bitte beachte, dass wir bei \"Troll-Feedback\" dein zusätzliches Premium entfernen.");
            p.sendMessage(Messages.INFO + "Dein Feedback: " + feedback.toString());
            Script.executeUpdate("INSERT INTO premium_feedback (nrp_id, days, feedback) VALUES (" + Script.getNRPID(p) + ", " + AddPremiumToPlayer.awaitFeedback.get(p.getName()) + ", '" + feedback.toString() + "')");
            Premium.addPremium(p, TimeUnit.DAYS.toMillis(3));
            AddPremiumToPlayer.awaitFeedback.remove(p.getName());
            Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, Script.getName(p) + " hat ein Feedback abgegeben und drei weitere Tage Premium erhalten (" + feedback.toString() + ").");
            return true;
        }

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false) || !SDuty.isSDuty(p)) {

            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("activate")) {
                    int id;
                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
                        return true;
                    }

                    activatePremium(p, id);
                    return true;
                }

                p.sendMessage(Messages.ERROR + "/premium <activate> <ID>");
                return true;

            }

            if(!Premium.hasPremium(p)) {
                p.sendMessage(Messages.ERROR + "Du hast keinen Premium Rang.");
                p.sendMessage(Messages.INFO + "Du kannst dir einen Premium Rang auf https://shop.newrp.de/ kaufen.");
                if(hasPremiumStored(p)) {
                    p.sendMessage(PREFIX + "Du kannst noch Premium aktivieren.");
                    sendPremiumStorage(p);
                }
            } else {
                if(Script.hasRank(p, Rank.MODERATOR, false)) {
                    p.sendMessage(PREFIX + "Du hast automatisch so lang Premium, wie du " + Script.getRank(p).getName(p) + " bist.");
                    return true;
                }
                p.sendMessage(PREFIX + "Du hast noch bis zum " + Script.dateFormat.format(Premium.getPremiumTime(p)) + " Uhr Premium.");
                if(hasPremiumStored(p)) {
                    p.sendMessage(PREFIX);
                    p.sendMessage(PREFIX + "Du kannst noch Premium aktivieren.");
                    sendPremiumStorage(p);
                }
            }
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/premium [Spieler] [Tage]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        int days;
        try {
            days = Integer.parseInt(args[1]);
        } catch (Exception e) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
            return true;
        }


        Premium.addPremium(tg, TimeUnit.DAYS.toMillis(days));
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " für " + days + " Tage Premium gegeben.");
        tg.sendMessage(PREFIX + "Du hast von " + Script.getName(p) + " für " + days + " Tage Premium bekommen.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " für " + days + " Tage Premium gegeben.", true);
        TeamSpeak.sync(Script.getNRPID(tg));

        return false;
    }

    public static boolean hasPremiumStored(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium_storage WHERE nrp_id=" + Script.getNRPID(p))) {
            while (rs.next()) {
                if(rs.getLong("expires") == 0) {
                    return true;
                }
                if (rs.getLong("expires") > System.currentTimeMillis()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void sendPremiumStorage(Player p) {
        int i = 1;
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium_storage WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id ASC;")) {
            while (rs.next()) {
                long expires = rs.getLong("expires");
                if(expires == 0) {
                    String expirationMessage = "× Permanent";
                    Script.sendClickableMessage(p, PREFIX + "§l#" + i + " §7» §b" + rs.getInt("duration") + " Tage " + expirationMessage, "/premium activate " + i, "§7Klicke hier, um den Premium Rang zu aktivieren.");
                    i++;
                    continue;
                }

                if (expires > System.currentTimeMillis()) {
                    String expirationMessage = "× Läuft in " + Script.getRemainingTime(expires) + " ab.";
                    Script.sendClickableMessage(p, PREFIX + "§l#" + i + " §7» §b" + rs.getInt("duration") + " Tage " + expirationMessage, "/premium activate " + i, "§7Klicke hier, um den Premium Rang zu aktivieren.");
                    i++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static OfflinePlayer getOwner(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium_storage WHERE id=" + id)) {
            if (rs.next()) {
                return Script.getOfflinePlayer(rs.getInt("nrp_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void activatePremium(Player p, int id) {
        int i = 0;
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM premium_storage WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id ASC;")) {

            while (rs.next()) {
                long expires = rs.getLong("expires");

                if (expires == 0 || expires > System.currentTimeMillis()) {
                    i++;
                    if(i == id) {
                        Premium.addPremium(p, TimeUnit.DAYS.toMillis(rs.getInt("duration")));
                        Script.executeAsyncUpdate("DELETE FROM premium_storage WHERE id=" + rs.getInt("id"));
                        p.sendMessage(PREFIX + "Du hast den Premium Rang aktiviert.");
                        Log.HIGH.write(p, "hat den Premium Rang aktiviert (" + rs.getInt("duration") + " Tage).");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
