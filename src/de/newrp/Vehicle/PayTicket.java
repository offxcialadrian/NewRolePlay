package de.newrp.Vehicle;


import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Government.Stadtkasse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayTicket implements CommandExecutor {

    public static int amount;
    public static int money;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        Car car = Car.getNearbyCar(p, 3);
        if (car == null) return true;
        Strafzettel strafzettel = car.getStrafzettel();
        if (strafzettel == null) {
            p.sendMessage(Car.PREFIX + "Dieses Fahrzeug hat kein Strafzettel.");
            return true;
        }
        int price = strafzettel.getPrice();
        if (Script.getMoney(p, PaymentType.BANK) < price) {
            p.sendMessage(Messages.ERROR + "Du hast zu wenig Geld.");
            return true;
        }
        p.sendMessage(Car.PREFIX + "Du hast den Strafzettel bezahlt.");
        car.setStrafzettel(null);

        Script.executeAsyncUpdate("DELETE FROM strafzettel WHERE id=" + car.getCarID());
        Script.removeMoney(p, PaymentType.BANK, price);
        amount++;
        money = money+(strafzettel.getPrice()/2);
        Stadtkasse.addStadtkasse(strafzettel.getPrice(), "Strafzettel von " + Script.getName(p), null);
        return true;
    }
}
