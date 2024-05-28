package de.newrp.Vehicle;


import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayTicket implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        Car car = Car.getNearbyCar(p, 3);
        if (car == null) {
            p.sendMessage(Messages.ERROR + "Du bist nicht bei deinem Auto");
            return true;
        }

        if(!car.isCarOwner(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht bei deinem Auto");
            return true;
        }

        Strafzettel strafzettel = car.getStrafzettel();
        if (strafzettel == null) {
            p.sendMessage(StrafzettelCommand.PREFIX + "Dein Fahrzeug hat keinen Strafzettel.");
            return true;
        }

        int price = strafzettel.getPrice();
        if (Script.getMoney(p, PaymentType.BANK) < price) {
            p.sendMessage(Messages.ERROR + "Du hast zu wenig Geld.");
            return true;
        }

        p.sendMessage(StrafzettelCommand.PREFIX + "Du hast den Strafzettel bezahlt.");
        Beruf.Berufe.POLICE.sendMessage(StrafzettelCommand.PREFIX + Script.getName(p) + " hat seinen Strafzettel am Auto mit dem Kennzeichen §e" + car.getLicenseplate() + " §7bezahlt!");

        car.removeStrafzettel();
        Script.removeMoney(p, PaymentType.BANK, price);
        Stadtkasse.addStadtkasse(strafzettel.getPrice(), "Strafzettel von " + Script.getName(p), null);
        Debug.debug("Paid Strafzettel " + car.getLicenseplate() + " for " + car.getStrafzettel().getPrice()  + "€ with type " + car.getCarType().getName());
        car.setStrafzettel(null);
        return true;
    }
}
