package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Shop.Shops;
import de.newrp.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Checkfinances implements CommandExecutor {

    private static HashMap<String, Long> check = new HashMap<>();
    private static String PREFIX = "§8[§eFinanzamt§8] §e» §7";
    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(1);
    private static final long TIMEOUT2 = TimeUnit.MINUTES.toMillis(2);
    private static long lastTime;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (Beruf.getBeruf(p) != Beruf.Berufe.GOVERNMENT) {
            p.sendMessage(Messages.ERROR + "§cDu bist kein Regierungsmitglied.");
            return true;
        }

        if (Beruf.getAbteilung(p) != Abteilung.Abteilungen.FINANZAMT && !Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "§cDu bist nicht im Finanzamt.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/checkfinances [Spieler]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }


        if (check.containsKey(tg.getName())) {
            if (check.get(tg.getName()) > System.currentTimeMillis()) {
                p.sendMessage(PREFIX + "Du hast bereits Daten bei der Bank angefordert.");
                int seconds = (int) ((check.get(tg.getName()) - System.currentTimeMillis()) / 1000);
                p.sendMessage(Messages.INFO + "Bitte führe den Befehl in " + seconds + " Sekunden erneut aus.");
                return true;
            } else {
                check.remove(p.getName());
                p.sendMessage(PREFIX + "§6=== " + tg.getName() + " ===");
                p.sendMessage("§8» " + "§6Kontostand: " + Script.getMoney(tg, PaymentType.BANK) + "€");
                for (Shops shop : Shops.values()) {
                    if (shop.getOwner() == Script.getNRPID(tg)) {
                        p.sendMessage("§8» " + "§6Shop: " + shop.getPublicName() + " §8(§6" + shop.getKasse() + "€§8)");
                    }
                }
                p.sendMessage("§8» " + "§6Gehalt: " + Beruf.getSalary(tg) + "€");
                p.sendMessage("§8» " + "§6Arbeitslosengeld: " + (Arbeitslosengeld.hasArbeitslosengeld(p) ? "§cJa" : "§aNein"));
                p.sendMessage("§8» " + "§6Letzte Zahlungen:");
                sendCashFlow(p, tg);
                p.sendMessage("§6====================");
                return true;

            }
        }


        if (lastTime + TIMEOUT2 > System.currentTimeMillis()) {
            p.sendMessage(PREFIX + "Das Finanzamt kann erst in " + ((lastTime + TIMEOUT2 - System.currentTimeMillis()) / 1000 / 60) + " Minuten wieder Daten beantragen.");
            return true;
        }

        lastTime = System.currentTimeMillis();
        check.put(tg.getName(), System.currentTimeMillis() + TIMEOUT);
        p.sendMessage(PREFIX + "Du hast die Daten von " + tg.getName() + " angefordert.");
        p.sendMessage(Messages.INFO + "Führe den selben Befehl in " + (TIMEOUT / 1000 / 60) + " Minuten erneut aus um die Daten zu erhalten.");


        return false;
    }

    private static void sendCashFlow(Player p, OfflinePlayer tg) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cashflow WHERE nrp_id='" + Script.getNRPID(tg) + "' ORDER BY id DESC LIMIT 15")) {
            if (rs.next()) {
                do {
                    int betrag = rs.getInt("money");
                    p.sendMessage("§8» " + ((betrag > 0) ? "§a" : "§c") + betrag + "€ §8» §6" + rs.getString("reason") + " §8(§6" + Script.dateFormat.format(rs.getLong("time")) + " Uhr§8) §8» §6" + rs.getString("after") + "€");
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
