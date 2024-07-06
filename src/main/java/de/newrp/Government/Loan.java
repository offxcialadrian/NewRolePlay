package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.Annehmen;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Loan implements CommandExecutor {

    public static final String PREFIX = "§8[§aKredit§8] §a» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;
        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.GOVERNMENT) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!Beruf.isLeader(p, true) && !Beruf.getAbteilung(p, false).isLeader()) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(PREFIX + "Aktuell vergebene Kredite:");
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT userID, amount, zins, id FROM loans WHERE time < " + System.currentTimeMillis())) {
                while (rs.next()) {
                    OfflinePlayer op = Script.getOfflinePlayer(rs.getInt("userID"));
                    int amount = rs.getInt("amount");
                    double interest = rs.getDouble("zins");
                    int id = rs.getInt("id");

                    if (op == null) continue;
                    p.sendMessage("       §8" + Messages.ARROW + " §7" + op.getName() + " §8| §7" + amount + "€ §8| §7" + interest + "% §8| §7ID: " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (args.length != 4) {
            p.sendMessage(Messages.ERROR + "/kredit [Name] [Dauer in Tagen] [Zinssatz] [Summe]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        int days;
        double interest;
        int amount;

        try {
            days = Integer.parseInt(args[1]);
            interest = Double.parseDouble(args[2]);
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine gültige Zahl ein.");
            return true;
        }

        if (days < 1 || days > 365) {
            p.sendMessage(Messages.ERROR + "Die Dauer muss zwischen 1 und 365 Tagen liegen.");
            return true;
        }

        if (interest < 0 || interest > 100) {
            p.sendMessage(Messages.ERROR + "Der Zinssatz muss zwischen 0 und 100 liegen.");
            return true;
        }

        if (amount < 1 || amount > Stadtkasse.getStadtkasse()) {
            p.sendMessage(Messages.ERROR + "Der Betrag muss zwischen 1€ und " + Stadtkasse.getStadtkasse() + "€ liegen.");
            return true;
        }

        if (Stadtkasse.getStadtkasse() < amount) {
            p.sendMessage(Messages.ERROR + "Die Stadtkasse hat nicht genügend Geld.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist zu weit entfernt.");
            return true;
        }

        Annehmen.offer.put(tg.getName() + ".loan", p.getName());
        Annehmen.offer.put(tg.getName() + ".loan.days", String.valueOf(days));
        Annehmen.offer.put(tg.getName() + ".loan.interest", String.valueOf(interest));
        Annehmen.offer.put(tg.getName() + ".loan.amount", String.valueOf(amount));

        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " einen Kredit über " + days + " Tage und " + amount + "€ angeboten (Zinssatz: " + interest + "%).");
        tg.sendMessage(PREFIX + "Du hast von " + Script.getName(p) + " ein Kreditangebot über " + days + " Tage und " + amount + "€ erhalten (Zinssatz: " + interest + "%).");
        Script.sendAcceptMessage(tg);


        return false;
    }
}
