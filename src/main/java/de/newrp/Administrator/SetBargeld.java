package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBargeld implements CommandExecutor {

    private static final String PREFIX = "§8[§eGeld§8] §e" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.OWNER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/setbargeld [Spieler] [Betrag]");
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        int money = 0;
        try {
            money = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
            return true;
        }

        Script.setMoney(tg, PaymentType.CASH, money);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " Bargeld auf " + money + "€ gesetzt.");
        tg.sendMessage(PREFIX + "Dein Bargeld wurde von " + Script.getName(p) + " auf " + money + "€ gesetzt.");
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (Script.hasRank(all, Rank.OWNER, false)) {
                all.sendMessage(PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " Bargeld auf " + money + "€ gesetzt.");
            }
        }
        return false;
    }
}
