package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Organisationen.Bankautomaten;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TakeMoney implements CommandExecutor {

    public static final String PREFIX = "§8[§6Bankautomat§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.POLICE) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst!");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/takemoney [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Bankautomaten.win.containsKey(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat kein Geld gewonnen.");
            return true;
        }

        int money = Bankautomaten.win.get(tg);

        if(money > Script.getMoney(tg, PaymentType.CASH)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat nicht genug Geld.");
            return true;
        }

        Bankautomaten.win.remove(tg);
        Script.removeMoney(tg, PaymentType.CASH, money);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " §6" + money + "§7 abgenommen.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §6" + money + "€§7 abgenommen.");
        tg.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat dir §6" + money + "€§7 abgenommen.");
        Organisation.getOrganisation(tg).sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat " + Script.getName(tg) + " §6" + money + "€§7 abgenommen.");


        return false;
    }
}
