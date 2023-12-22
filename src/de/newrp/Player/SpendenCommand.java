package de.newrp.Player;

import de.newrp.API.ATM;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpendenCommand implements CommandExecutor {

    public static final String PREFIX = "§8[§eSpenden§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        ATM atm = ATM.getNearATM(p);

        if(atm == null) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe eines ATMs.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/spenden [Betrag]");
            return true;
        }

        if(!Script.isInt(args[0])) {
            p.sendMessage(Messages.ERROR + "/spenden [Betrag]");
            return true;
        }

        int amount = Integer.parseInt(args[0]);
        if(amount < 0) {
            p.sendMessage(Messages.ERROR + "Gib einen Betrag an.");
            return true;
        }

        if(Script.getMoney(p, PaymentType.BANK) < amount) {
            p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
            return true;
        }

        Script.removeMoney(p, PaymentType.BANK, amount);
        Stadtkasse.addStadtkasse(amount);
        p.sendMessage(PREFIX + "Du hast " + amount + "€ gespendet.");
        if(amount >= 1000) Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + Script.getName(p) + " hat der Stadtkasse " + amount + "€ gespendet.");

        return false;
    }
}
