package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;

public class Strafregister implements CommandExecutor {

    public static String PREFIX = "§8[§9Strafregister§8] §9" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (Beruf.getAbteilung(p, true) != Abteilung.Abteilungen.JUSTIZMINISTERIUM && !Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "§cDu bist nicht im Justizministerium.");
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/strafregister [Spieler] [Tage]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine gültige Zahl an.");
            return true;
        }

        int days = 0;
        if (Script.isInt(args[1])) {
            days = Integer.parseInt(args[1]);
        } else {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine gültige Zahl an.");
            return true;
        }

        if (days < 1) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine gültige Zahl an.");
            return true;
        }

        if (days > 365) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine gültige Zahl an.");
            return true;
        }

        long time = System.currentTimeMillis() - (days * 86400000L);
        int amount = getWantedAmount(tg, time);
        int amountperday = amount / days;

        if (amountperday < 10) {
            p.sendMessage(PREFIX + "Dem Spieler " + Script.getName(tg) + " kann bedenkenlos ein Waffenschein ausgestellt werden.");
            return true;
        }

        if (amountperday < 20) {
            p.sendMessage(PREFIX + "Dem Spieler " + Script.getName(tg) + " kann ein Waffenschein ausgestellt werden.");
            return true;
        }

        if (amountperday < 30) {
            p.sendMessage(PREFIX + "Dem Spieler " + Script.getName(tg) + " sollte man ggf. keinen Waffenschein ausstellen.");
            return true;
        }

        if (amountperday < 40) {
            p.sendMessage(PREFIX + "Dem Spieler " + Script.getName(tg) + " sollte kein Waffenschein ausgestellt werden.");
            return true;
        }

        p.sendMessage(PREFIX + "Dem Spieler " + Script.getName(tg) + " sollte auf keinen Fall ein Waffenschein ausgestellt werden.");


        return false;
    }

    public static int getWantedAmount(OfflinePlayer p, Long time) {
        int amount = 0;
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wantedlog WHERE userID='" + Script.getNRPID(p) + "' AND time > '" + time + "'")) {
            while (rs.next()) {
                amount += rs.getInt("amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amount;
    }

}
