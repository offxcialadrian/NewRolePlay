package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.Statement;

public class Stadtkasse implements CommandExecutor {

    private static final String PREFIX = "§8[§eStadtkasse§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (Beruf.getBeruf(p) != Beruf.Berufe.GOVERNMENT) {
            p.sendMessage(Messages.ERROR + "Du bist kein Regierungsmitglied.");
            return true;
        }

        p.sendMessage(PREFIX + "In der Stadtkasse befinden sich §e" + Script.df.format(getStadtkasse()) + "€.");


        return false;
    }

    public static int getStadtkasse() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM city")) {
            if (rs.next()) {
                return rs.getInt("money");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void removeStadtkasse(int betrag) {
        Script.executeAsyncUpdate("UPDATE city SET money = money - " + betrag);
    }

    public static void setStadtkasse(int betrag) {
        Script.executeAsyncUpdate("UPDATE city SET money = " + betrag);
    }

    public static void addStadtkasse(int betrag) {
        Script.executeAsyncUpdate("UPDATE city SET money = money + " + betrag);
        new BukkitRunnable() {
            @Override
            public void run() {
                SDuty.updateScoreboard();
            }
        }.runTaskLater(main.getInstance(), 20L);
    }

    public static int getArbeitslosengeld() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM city")) {
            if (rs.next()) {
                return rs.getInt("arbeitslosengeld");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
