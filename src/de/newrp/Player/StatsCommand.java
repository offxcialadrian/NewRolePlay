package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Administrator.Punish;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Straftat;
import de.newrp.House.House;
import de.newrp.Police.Fahndung;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        p.sendMessage("§e§l=== §6" + Script.getName(p) + " §e§l===");
        p.sendMessage("§7ID §8× §e" + Script.getNRPID(p));
        p.sendMessage("§7Rang §8× §e" + Script.getRank(p).getName(p));
        p.sendMessage("§7Bargeld §8× §e" + Script.getMoney(p, PaymentType.CASH) + "€");
        p.sendMessage("§7Fahndung §8× §e" + (Fahndung.isFahnded(p) ? Fahndung.getWanteds(p) + " WantedPunkte" : "Nein"));
        p.sendMessage("§7UUID §8× §e" + p.getUniqueId());
        p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(p).getName());
        p.sendMessage("§7Team §8× §e" + (Team.getTeam(p) != null ? Team.getTeam(p).getName() : "Kein Team"));
        p.sendMessage("§7PlayTime §8× §e" + Script.getPlayTime(p, true) + ":§e" + String.format("%02d", Script.getPlayTime(p, false)) + " Stunden §8(§e" + Script.getActivePlayTime(p, true) + ":§e" + String.format("%02d", Script.getActivePlayTime(p, false)) + " Stunden§8)");
        p.sendMessage("§7PayDay §8× §e" + PayDay.getPayDayTime(p) + "/60 Minuten");
        p.sendMessage("§7Level §8× §e" + Script.getLevel(p));
        p.sendMessage("§7Exp §8× §e" + Script.getExp(p) + "/" + Script.getLevelCost(p) + " Exp (" + Script.getPercentage(Script.getExp(p), Script.getLevelCost(p)) + "%)");
        p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(p) ? Beruf.getBeruf(p).getName() : "Kein Beruf") + (Beruf.isLeader(p, true) ? " §8(§eLeader§8)" : ""));
        p.sendMessage("§7Warns §8× §e" + Punish.getWarns(p) + "/3");
        if(!Punish.getWarnsMap(p).isEmpty()) {
            for(Map.Entry<Long, String> entry : Punish.getWarnsMap(p).entrySet()) {
                p.sendMessage("  §7» §e" + Script.dateFormat2.format(entry.getKey()) + " §8× §e" + entry.getValue());
            }
        }
        StringBuilder houses = new StringBuilder();
        for(House house : House.getHouses(Script.getNRPID(p))) {
            if(House.getHouses(Script.getNRPID(p)).size() == 1) {
                houses.append("Haus " + house.getID());
                break;
            }
            houses.append(house.getID()).append(house.getID() == House.getHouses(Script.getNRPID(p)).get(House.getHouses(Script.getNRPID(p)).size() - 1).getID() ? "" : ", ");
        }
        if(houses.toString().equalsIgnoreCase("")) houses = new StringBuilder("Keine Häuser");
        p.sendMessage("§7Häuser §8× §e" + houses.toString());

        return false;
    }
}
