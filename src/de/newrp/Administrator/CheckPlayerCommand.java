package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;

public class CheckPlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/checkplayer [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null && Script.getNRPID(args[0]) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(tg == null && Script.getNRPID(args[0]) != 0) {
            OfflinePlayer offtg = Script.getOfflinePlayer(Script.getNRPID(args[0]));
            p.sendMessage("§e§l=== §6" + offtg.getName() + " §8(§cOffline§8) §e§l===");
            p.sendMessage("§7ID §8× §e" + Script.getNRPID(offtg));
            if (Punish.getBanUntil(offtg) > System.currentTimeMillis() || Punish.getBanUntil(offtg) == 0)
                p.sendMessage("§cGebannt §8× §c" + (Punish.getBanUntil(offtg) != 0 ? "bis " + Script.dateFormat.format(Punish.getBanUntil(offtg)) + " Uhr" : "Lebenslang")  + " §8(§c" + Punish.getBanReason(offtg) + "§8)");
            p.sendMessage("§7Warns §8× §e" + Punish.getWarns(offtg) + "/3");
            if(!Punish.getWarnsMap(offtg).isEmpty()) {
                for(Map.Entry<Long, String> entry : Punish.getWarnsMap(offtg).entrySet()) {
                    p.sendMessage("  §7» §e" + Script.dateFormat2.format(entry.getKey()) + " §8× §e" + entry.getValue());
                }
            }
            p.sendMessage("§7Geld §8× §e" + Script.getMoney(offtg, PaymentType.CASH) + "€ | " + Script.getMoney(offtg, PaymentType.BANK) + "€");
            p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(offtg).getName());
            p.sendMessage("§7Team §8× §e" + (Team.getTeam(offtg) != null ? Team.getTeam(offtg).getName() : "Kein Team"));
            p.sendMessage("§7PlayTime §8× §e" + Script.getPlayTime(offtg, true) + ":§e" + String.format("%02d", Script.getPlayTime(offtg, false)) + " Stunden");
            p.sendMessage("§7Level §8× §e" + Script.getLevel(offtg));
            p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(offtg) ? Beruf.getBeruf(offtg).getName() : "Kein Beruf"));
            return true;
        }

        p.sendMessage("§e§l=== §6" + Script.getName(tg) + " §e§l===");
        p.sendMessage("§7ID §8× §e" + Script.getNRPID(tg));
        if (Script.hasRank(p, Rank.ADMINISTRATOR, false))
            p.sendMessage("§7IP §8× §e" + tg.getAddress().getAddress().getHostAddress());
        p.sendMessage("§7Warns §8× §e" + Punish.getWarns(tg) + "/3");
        if(!Punish.getWarnsMap(tg).isEmpty()) {
            for(Map.Entry<Long, String> entry : Punish.getWarnsMap(tg).entrySet()) {
                p.sendMessage("  §7» §e" + Script.dateFormat2.format(entry.getKey()) + " §8× §e" + entry.getValue());
            }
        }
        p.sendMessage("§7Rang §8× §e" + Script.getRank(tg).getName(tg));
        p.sendMessage("§7Geld §8× §e" + Script.getMoney(tg, PaymentType.CASH) + "€ | " + Script.getMoney(tg, PaymentType.BANK) + "€");
        p.sendMessage("§7UUID §8× §e" + tg.getUniqueId());
        p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(tg).getName());
        p.sendMessage("§7Team §8× §e" + (Team.getTeam(tg) != null ? Team.getTeam(tg).getName() : "Kein Team"));
        p.sendMessage("§7PlayTime §8× §e" + Script.getPlayTime(tg, true) + ":§e" + String.format("%02d", Script.getPlayTime(tg, false)) + " Stunden");
        p.sendMessage("§7PayDay §8× §e" + PayDay.getPayDayTime(tg) + "/60 Minuten");
        p.sendMessage("§7Exp §8× §e" + Script.getExp(tg) + "/" + Script.getLevelCost(tg) + " Exp");
        p.sendMessage("§7Health §8× §e" + (tg.getHealth()/2) + "/" + (tg.getMaxHealth()/2) + " HP");
        p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(tg) ? Beruf.getBeruf(tg).getName() : "Kein Beruf"));


        return false;
    }
}
