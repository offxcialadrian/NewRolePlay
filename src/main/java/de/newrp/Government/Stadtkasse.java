package de.newrp.Government;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.Statement;

public class Stadtkasse implements CommandExecutor {

    public static final String PREFIX = "§8[§eStadtkasse§8] §e» §7";
    public static int AUSZAHLUNG = 0;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Beruf.hasBeruf(p) && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (Beruf.getBeruf(p) != Beruf.Berufe.GOVERNMENT && !SDuty.isSDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Regierungsmitglied.");
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT) && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Regierungsmitglied.");
            return true;
        }

        if (!Beruf.isLeader(p, true) && Beruf.getAbteilung(p) != Abteilung.Abteilungen.FINANZAMT && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        ATM atm = ATM.getNearestATM(p.getLocation());
        if (atm == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe eines Geldautomaten.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            p.sendMessage(PREFIX + "In der Stadtkasse befinden sich §e" + getStadtkasse() + "€.");
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("auszahlen")) {
            if (!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "Der Betrag muss eine Zahl sein.");
                return true;
            }

            int betrag = Integer.parseInt(args[1]);
            if (betrag <= 0) {
                p.sendMessage(Messages.ERROR + "Der Betrag muss größer als 0 sein.");
                return true;
            }

            if (getStadtkasse() < betrag) {
                p.sendMessage(Messages.ERROR + "In der Stadtkasse befinden sich nicht genügend Geld.");
                return true;
            }

            if (AUSZAHLUNG + betrag > 50000) {
                p.sendMessage(Messages.ERROR + "Es können maximal 50.000€ am Tag ausgezahlt werden.");
                return true;
            }

            AUSZAHLUNG += betrag;
            atm.removeCash(betrag);
            StringBuilder reason = new StringBuilder();
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    if (reason.length() > 0) {
                        reason.append(" ");
                    }
                    reason.append(args[i]);
                }
            }
            Stadtkasse.removeStadtkasse(betrag, "Auszahlung von " + Script.getName(p) + " Verwendungszweck: " + reason.toString());
            Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + "Es wurde " + betrag + "€ aus der Stadtkasse ausgezahlt. Verwendungszweck: " + reason.toString());
            Script.addMoney(p, PaymentType.CASH, betrag);
            return true;
        }


        return false;
    }

    public static int getStadtkasse() {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM city")) {
            if (rs.next()) {
                return rs.getInt("money");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void removeStadtkasse(int betrag, String reason) {
        if (betrag == 0) return;
        SteuerNotification.sendNotification(reason + " " + Messages.ARROW + " " + -betrag + "€");
        Script.executeAsyncUpdate("UPDATE city SET money = money - " + betrag);
        Script.executeAsyncUpdate("INSERT INTO stadtkasse (betrag, grund, steuerID, steuerPercentage) VALUES (" + -betrag + ", '" + reason + "', NULL, NULL)");
        new BukkitRunnable() {
            @Override
            public void run() {
                SDuty.updateScoreboard();
            }
        }.runTaskLater(Main.getInstance(), 20L);
    }

    public static void setStadtkasse(int betrag) {
        Script.executeAsyncUpdate("UPDATE city SET money = " + betrag);
    }

    public static void addStadtkasse(int betrag, String grund, Steuern.Steuer steuer) {
        if (betrag == 0) return;
        SteuerNotification.sendNotification(grund + " " + Messages.ARROW + " " + betrag + "€" + (steuer != null && !grund.contains(steuer.getName()) ? " (" + steuer.getName() + ")" : ""));
        Script.executeAsyncUpdate("UPDATE city SET money = money + " + betrag);
        Script.executeAsyncUpdate("INSERT INTO stadtkasse (betrag, grund, steuerID, steuerPercentage) VALUES (" + betrag + ", '" + grund + "', " + (steuer != null ? steuer.getID() : "NULL") + ", " + (steuer != null ? steuer.getPercentage() : "NULL") + ")");
        new BukkitRunnable() {
            @Override
            public void run() {
                SDuty.updateScoreboard();
            }
        }.runTaskLater(Main.getInstance(), 20L);
    }

    public static int getArbeitslosengeld() {
        try (Statement stmt = Main.getConnection().createStatement();
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
