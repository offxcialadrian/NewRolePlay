package de.newrp.API;

import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Arbeitslosengeld;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.Player.AFK;
import de.newrp.Player.Banken;
import de.newrp.Shop.Shops;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PayDay extends BukkitRunnable {

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (AFK.isAFK(p)) continue;
            if (SDuty.isSDuty(p)) continue;
            if (getPayDayTime(p) < 59) {
                addPayDayTime(p);
                return;
            }

            if (!Banken.hasBank(p)) {
                p.sendMessage(Messages.INFO + "Du hast kein Geld am PayDay erhalten, da du kein Konto hast.");
                return;
            }

            int payday = 0;
            int interest = (Script.getMoney(p, PaymentType.BANK) > 0 ? (int) (Banken.getBankByPlayer(p).getInterest() * Script.getMoney(p, PaymentType.BANK)) : (int) (0.2 * Script.getMoney(p, PaymentType.BANK)));
            double einkommenssteuer = Steuern.Steuer.EINKOMMENSSTEUER.getPercentage();
            double arbeitslosenversicherung = Steuern.Steuer.ARBEITSLOSENVERSICHERUNG.getPercentage();
            double lohnsteuer = Steuern.Steuer.LOHNSTEUER.getPercentage();
            double gfb_lohnsteuer = Steuern.Steuer.GFB_LOHNSTEUER.getPercentage();
            p.sendMessage("§9=== §l§ePayDay §9===");
            p.sendMessage("§8" + Messages.ARROW + " §7Kontostand: " + (Script.getMoney(p, PaymentType.BANK) >= 0 ? "§a" : "§c") + Script.getMoney(p, PaymentType.BANK) + "€");
            p.sendMessage("§8" + Messages.ARROW + " §7Kontoführungsgebühr: §c-" + Banken.getBankByPlayer(p).getKontoKosten() + "€");
            payday -= Banken.getBankByPlayer(p).getKontoKosten();
            p.sendMessage("§8" + Messages.ARROW + " §7Zinsen: " + (interest >= 0 ? "§a+" : "§c") + interest + "€");
            payday += interest;
            int other_salary = getPayDayPay(p);
            if (other_salary > 0) {
                p.sendMessage("§8" + Messages.ARROW + " §7Lohn/Gehalt (GFB) §a+" + other_salary + "€");
                payday += other_salary;
                p.sendMessage("§8" + Messages.ARROW + " §7Lohnsteuer (GFB) (" + gfb_lohnsteuer + "%): §c-" + (int) Script.getPercent(gfb_lohnsteuer, other_salary) + "€");
                payday -= (int) Script.getPercent(gfb_lohnsteuer, other_salary);
                Stadtkasse.addStadtkasse((int) Script.getPercent(gfb_lohnsteuer, other_salary));

                p.sendMessage("§8" + Messages.ARROW + " §7Arbeitslosenversicherung (GFB) (" + arbeitslosenversicherung + "%): §c-" + (int) Script.getPercent(arbeitslosenversicherung, other_salary) + "€");
                Stadtkasse.addStadtkasse((int) Script.getPercent(arbeitslosenversicherung, other_salary));
                payday -= (int) Script.getPercent(arbeitslosenversicherung, other_salary);
            }

            if (Beruf.hasBeruf(p)) {

                int salary = Beruf.getSalary(p);
                p.sendMessage("§8" + Messages.ARROW + " §7Lohn/Gehalt: §a+" + salary + "€");
                payday += salary;
                if (Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) Stadtkasse.removeStadtkasse(salary);

                p.sendMessage("§8" + Messages.ARROW + " §7Lohnsteuer (" + lohnsteuer + "%): §c-" + (int) Script.getPercent(lohnsteuer, salary) + "€");
                Stadtkasse.addStadtkasse((int) Script.getPercent(lohnsteuer, salary));
                payday -= (int) Script.getPercent(lohnsteuer, salary);


                p.sendMessage("§8" + Messages.ARROW + " §7Arbeitslosenversicherung (" + arbeitslosenversicherung + "%): §c-" + (int) Script.getPercent(arbeitslosenversicherung, salary) + "€");
                Stadtkasse.addStadtkasse((int) Script.getPercent(arbeitslosenversicherung, salary));
                payday -= (int) Script.getPercent(arbeitslosenversicherung, salary);

            } else if (Arbeitslosengeld.hasArbeitslosengeld(p)) {
                p.sendMessage("§8" + Messages.ARROW + " §7Arbeitslosengeld: §a+" + Stadtkasse.getArbeitslosengeld() + "€");
                Stadtkasse.removeStadtkasse(Stadtkasse.getArbeitslosengeld());
                payday += Stadtkasse.getArbeitslosengeld();
            }

            int shops = 0;
            for (Shops shop : Shops.values()) {
                if (shop.getOwner() == Script.getNRPID(p)) {
                    shops++;
                }
            }

            if (shops > 0) {
                int tax = (int) (Steuern.Steuer.GEWERBESTEUER.getPercentage())*shops;
                p.sendMessage("§8" + Messages.ARROW + " §7Gewerbesteuer: §c-" + tax + "€");
                Stadtkasse.addStadtkasse(tax);
                payday -= tax;
            }

            if (payday > 0) {
                p.sendMessage("§8" + Messages.ARROW + " §7Einkommenssteuer (" + einkommenssteuer + "%): §c-" + (int) Script.getPercent(einkommenssteuer, payday) + "€");
                Stadtkasse.addStadtkasse((int) Script.getPercent(einkommenssteuer, payday));
                payday -= (int) Script.getPercent(einkommenssteuer, payday);
            } else {
                p.sendMessage("§8" + Messages.ARROW + " §7Einkommenssteuer (" + einkommenssteuer + "%): §c-" + 0 + "€");
            }
            p.sendMessage("§8" + Messages.ARROW + " §7Bilanz: " + (payday >= 0 ? "§a+" : "§c") + payday + "€");
            p.sendMessage("§8" + Messages.ARROW + " §7Neuer Kontostand: " + (Script.getMoney(p, PaymentType.BANK) + payday >= 0 ? "§a" : "§c") + (Script.getMoney(p, PaymentType.BANK) + payday) + "€");
            p.sendMessage("§9================");
            if (payday >= 0) Script.addMoney(p, PaymentType.BANK, payday);
            else Script.removeMoney(p, PaymentType.BANK, payday);
            setPayDayTime(p, 0);
            Script.executeAsyncUpdate("UPDATE payday SET money = 0 WHERE nrp_id = '" + Script.getNRPID(p) + "'");
        }
    }

    public static int getPayDayTime(Player p) {
        return Script.getInt(p, "payday", "time");
    }

    public static void setPayDayTime(Player p, int time) {
        Script.setInt(p, "payday", "time", time);
    }

    public static int getPayDayPay(Player p) {
        return Script.getInt(p, "payday", "money");
    }

    public static void addPayDayTime(Player p) {
        setPayDayTime(p, getPayDayTime(p) + 1);
    }


}
