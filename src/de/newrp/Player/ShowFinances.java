package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Chat.Me;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowFinances implements CommandExecutor {
    private static String PREFIX = "§8[§6Finanzen§8] §6» ";
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Banken.hasBank(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Konto!");
            p.sendMessage(Messages.INFO + "Du kannst an der Bank eins mit §8/§6banken §rerstellen.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/showfinances [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p == tg) {
            p.performCommand("stats");
            return true;
        }

        if(tg.getLocation().distance(p.getLocation()) > 5) {
            p.sendMessage(Messages.PLAYER_FAR);
            return true;
        }

        ATM atm = ATM.getNearATM(p);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " deine Finanzen gezeigt.");
        tg.sendMessage(PREFIX + Script.getName(p) + "s Finanzen:");
        tg.sendMessage(PREFIX + "Bargeld: §6" + Script.getMoney(p, PaymentType.CASH) + "€");
        if(atm != null) tg.sendMessage(PREFIX + "Kontostand: §6" + Script.getMoney(p, PaymentType.BANK) + "€");
        Me.sendMessage(p, "zeigt " + Script.getName(tg) + " " + (Script.getGender(p) == Gender.MALE ? "seine" : "ihre") + " Finanzen.");
        return false;
    }
}
