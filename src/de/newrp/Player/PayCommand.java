package de.newrp.Player;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Administrator.Notications;
import de.newrp.Chat.Me;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§aGeld§8] §a" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/pay [Spieler] [Betrag]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (tg == p) {
            p.sendMessage(Messages.ERROR + "Du kannst dir nicht selber Geld geben.");
            return true;
        }

        if (p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist zu weit weg.");
            return true;
        }

        int money = 0;
        try {
            money = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
            return true;
        }

        money = Math.abs(money);

        if (Script.getMoney(p, PaymentType.CASH) < money) {
            p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
            return true;
        }

        Script.removeMoney(p, PaymentType.CASH, money);
        Script.addMoney(tg, PaymentType.CASH, money);

        Me.sendMessage(p, "überreicht " + Script.getName(tg) + " etwas Geld.");
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " " + money + "€ überreicht.");
        tg.sendMessage(PREFIX + Script.getName(p) + " überreicht dir " + money + "€.");
        Log.LOW.write(p, "überreicht " + Script.getName(tg) + " " + money + "€.");
        Log.LOW.write(tg, "erhält von " + Script.getName(p) + " " + money + "€.");
        Notications.sendMessage(Notications.NotificationType.PAYMENT, Script.getName(p) + " hat " + Script.getName(tg) + " " + money + "€ überreicht. (CASH)");


        return false;
    }
}
