package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMoney implements CommandExecutor {

    private static final String PREFIX = "§8[§eGeld§8] §e" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/setmoney [Spieler] [Betrag]");
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
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

        Script.setMoney(tg, PaymentType.BANK, money);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " Kontostand auf " + money + "€ gesetzt.");
        if(tg.getPlayer() != null) tg.getPlayer().sendMessage(PREFIX + "Dein Kontostand wurde von " + Messages.RANK_PREFIX(p) + " auf " + money + "€ gesetzt.");
        else Script.addOfflineMessage(tg, PREFIX + "Dein Kontostand wurde von " + Messages.RANK_PREFIX(p) + " auf " + money + "€ gesetzt.");
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (Script.hasRank(all, Rank.OWNER, false)) {
                all.sendMessage(PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " Kontostand auf " + money + "€ gesetzt.");
            }
        }

        return false;
    }
}
