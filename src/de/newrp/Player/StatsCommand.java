package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Administrator.Punish;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Straftat;
import de.newrp.House.House;
import de.newrp.Organisationen.Organisation;
import de.newrp.Police.Fahndung;
import de.newrp.Votifier.VoteListener;
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
        p.sendMessage("§7Rang §8× §e" + (Script.isNRPTeam(p) ? Script.getRank(p).getName(p) : (Premium.hasPremium(p) ? "§aPremium" : "Spieler")));
        p.sendMessage("§7Bargeld §8× §e" + Script.getMoney(p, PaymentType.CASH) + "€");
        p.sendMessage("§7Fahndung §8× §e" + (Fahndung.isFahnded(p) ? Fahndung.getWanteds(p) + " WantedPunkte" : "Nein"));
        p.sendMessage("§7UUID §8× §e" + p.getUniqueId());
        p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(p).getName());
        p.sendMessage("§7Team §8× §e" + (Team.getTeam(p) != null ? Team.getTeam(p).getName() : "Kein Team"));
        p.sendMessage("§7PlayTime §8× §e" + (Premium.hasPremium(p)?Script.getPlayTime(p, true) + ":§e" + String.format("%02d", Script.getPlayTime(p, false)) + " Stunden §8(§e" + Script.getActivePlayTime(p, true) + ":§e" + String.format("%02d", Script.getActivePlayTime(p, false)) + " Stunden§8)":"Premium benötigt"));
        p.sendMessage("§7PayDay §8× §e" + PayDay.getPayDayTime(p) + "/60 Minuten" + (Premium.hasPremium(p) ? " §8(§e" +  PayDay.getPayDayPay(p) + "€ GFB Gehalt§8)" : " §8(§ePremium für Gehaltsvorschau benötigt§8)"));
        p.sendMessage("§7Level §8× §e" + Script.getLevel(p));
        p.sendMessage("§7VotePoints §8× §e" + VoteListener.getVotepoints(Script.getNRPID(p)));
        p.sendMessage("§7Exp §8× §e" + Script.getExp(p) + "/" + Script.getLevelCost(p) + " Exp §8(§e" + Script.getPercentage(Script.getExp(p), Script.getLevelCost(p)) + "%§8)");
        p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(p) ? Beruf.getBeruf(p).getName() : "Kein Beruf") + (Beruf.isLeader(p, true) ? " §8(§eLeader§8)" : ""));
        p.sendMessage("§7Organisation §8× §e" + (Organisation.hasOrganisation(p) ? Organisation.getOrganisation(p).getName() : "Keine Organisation") + (Organisation.isLeader(p, true) ? " §8(§eLeader§8)" : ""));
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
        StringBuilder licenses = new StringBuilder();
        for(Licenses license : Licenses.values()) {
            if(license.hasLicense(Script.getNRPID(p))) {
                if(licenses.length() > 0) {
                    licenses.append(", ");
                }
                licenses.append(license.getName());
            }
        }
        if(houses.toString().equalsIgnoreCase("")) houses = new StringBuilder("Keine Häuser");
        p.sendMessage("§7Häuser §8× §e" + houses.toString());
        p.sendMessage("§7Lizenzen §8× §e" + licenses.toString());

        Achievement.STATS.grant(p);

        return false;
    }
}
