package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Main;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;

public class Anrufbeantworter implements CommandExecutor {

    public static String PREFIX = "§8[§bAnrufbeantworter§8] §b» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;



        if(args.length == 1 && SDuty.isSDuty(p)) {
            OfflinePlayer tg = Script.getOfflinePlayer(args[0]);

            if(Script.getNRPID(tg) == 0) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(getAnrufbeantworter(tg) == null) {
                p.sendMessage(PREFIX + "Der Spieler hat keinen Anrufbeantworter.");
            } else {
                p.sendMessage(PREFIX + "Der Anrufbeantworter von §e" + tg.getName() + "§7 ist §e" + getAnrufbeantworter(tg) + "§7.");
            }
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("reset") && SDuty.isSDuty(p) && Script.hasRank(p, Rank.MODERATOR, false)) {
            OfflinePlayer tg = Script.getOfflinePlayer(args[1]);

            if(Script.getNRPID(tg) == 0) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(getAnrufbeantworter(tg) == null) {
                p.sendMessage(PREFIX + "Der Spieler hat keinen Anrufbeantworter.");
            } else {
                try (Statement stmt = Main.getConnection().createStatement()) {
                    stmt.executeUpdate("UPDATE handy_settings SET anrufbeantworter=NULL WHERE nrp_id=" + Script.getNRPID(tg));
                    p.sendMessage(PREFIX + "Der Anrufbeantworter von §e" + tg.getName() + "§7 wurde entfernt.");
                    Script.sendTeamMessage(p, ChatColor.GOLD, "hat den Anruf beantworter von " + tg.getName() + " entfernt.", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if(!Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du benötigst Premium, um den Anrufbeantworter zu benutzen.");
            p.sendMessage(Messages.INFO + "Du kannst Premium im Shop unter https://shop.newrp.de erwerben.");
            return true;
        }

        if(!Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
            return true;
        }

        if(args.length == 0) {
            if(getAnrufbeantworter(p) == null) {
                p.sendMessage(PREFIX + "Du hast keinen Anrufbeantworter.");
            } else {
                p.sendMessage(PREFIX + "Dein Anrufbeantworter ist §e" + getAnrufbeantworter(p) + "§7.");
            }
            return true;
        }

        StringBuilder ab = new StringBuilder();
        for (String arg : args) {
            ab.append(arg).append(" ");
        }

        Script.executeUpdate("UPDATE handy_settings SET anrufbeantworter='" + ab.toString().trim() + "' WHERE nrp_id=" + Script.getNRPID(p));
        p.sendMessage(PREFIX + "Dein Anrufbeantworter wurde auf §e" + ab.toString().trim() + "§7 gesetzt.");

        return false;
    }

    public static String getAnrufbeantworter(OfflinePlayer p) {
        if(!Premium.hasPremium(p)) return null;
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM handy_settings WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getString("anrufbeantworter");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
