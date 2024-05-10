package de.newrp.Vehicle;

import de.newrp.API.Messages;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CheckKFZ implements CommandExecutor {

    public static final String PREFIX = "§8[§bCheckKFZ§8]§b " + Messages.ARROW + " §7";

    public static ArrayList<Player> kfz_check = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!SDuty.isSDuty(p)) {
            if (!kfz_check.contains(p)) {
                kfz_check.add(p);
                p.sendMessage(PREFIX + "Klicke nun auf ein Fahrzeug, um es zu überprüfen.");
            } else {
                kfz_check.remove(p);
                p.sendMessage(PREFIX + "Du hast das Überprüfen beendet.");
            }
        } else {
            Car car = Car.getNearbyCar(p, 5);
            check(p, car);
        }
        return true;
    }

    public static void check(Player p, Car car) {
        if (car != null) {
            if (car.isCarOwner(p) || Beruf.hasBeruf(p, Beruf.Berufe.POLICE)) {
                if (car.isCarOwner(p) || Duty.isInDuty(p)) {
                    /*if (car.hasBomb()) {
                        int i = Script.getRandom(1, 100);
                        if (i > 50) {
                            p.sendMessage(Car.PREFIX + "Du hast §cetwas merkwürdiges §7am Auto entdeckt...");
                        } else
                            p.sendMessage(Car.PREFIX + "Du konntest §anichts merkwürdiges §7am Auto entdecken.");
                    } else
                        p.sendMessage(Car.PREFIX + "Du konntest §anichts merkwürdiges §7am Auto entdecken.");*/
                }
            }
        }
        if (SDuty.isSDuty(p)) {
            if (car == null) {
                p.sendMessage(Messages.ERROR + "§cKein Fahrzeug gefunden.");
            } else {
                p.sendMessage(PREFIX + "Kennzeichen§8: " + car.getLicenseplate() + " | Modell§8: " + car.getCarType().getName() + " | Besitzer§8: " + car.getOwner().getName());
            }
        } else if (Beruf.hasBeruf(p, Beruf.Berufe.POLICE)) {
            if (Duty.isInDuty(p)) {
                if (car == null) {
                    p.sendMessage(PREFIX + "Es wurde kein registriertes Fahrzeug in ihrer Nähe gefunden.");
                } else {
                    p.sendMessage(PREFIX + "Das Fahrzeug mit dem Kennzeichen " + car.getLicenseplate() + " ist auf den Spieler " + car.getOwner().getName() + " registriert.");
                    if (car.getStrafzettel() != null)
                        p.sendMessage(PREFIX + "Das Fahrzeug hat einen Strafzettel aufgrund von " + car.getStrafzettel().getReason() + " in Höhe von " + car.getStrafzettel().getPrice() + ".");
                }
            } else {
                p.sendMessage(PREFIX + "Du bist nicht im Dienst.");
            }
        }
    }

    public static boolean isChecking(Player p) {
        return kfz_check.contains(p);
    }
}