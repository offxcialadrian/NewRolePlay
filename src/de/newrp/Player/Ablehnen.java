package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.Team;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ablehnen implements CommandExecutor {


    public static String DENIED = "§8[§cABGELEHNT§8] §e" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (Annehmen.offer.containsKey(p.getName() + ".joinberuf")) {
            Player leader = Script.getPlayer(Annehmen.offer.get(p.getName() + ".joinberuf"));
            if (leader != null) {
                leader.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt dem Beruf " + Beruf.getBeruf(leader).getName() + " beizutreten.");
            }
            p.sendMessage(DENIED + "Du hast es Abgelehnt dem Beruf " + Beruf.getBeruf(leader).getName() + " beizutreten.");
            Annehmen.offer.remove(p.getName());
        } else if(Annehmen.offer.containsKey(p.getName() + ".jointeam")) {
            Player leader = Script.getPlayer(Annehmen.offer.get(p.getName() + ".jointeam"));
            if (leader != null) {
                leader.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt dem Team " + Team.getTeam(leader).getName() + " beizutreten.");
            }
            p.sendMessage(DENIED + "Du hast es Abgelehnt dem Team " + Team.getTeam(leader).getName() + " beizutreten.");
            Annehmen.offer.remove(p.getName());
        } else if(Annehmen.offer.containsKey(p.getName() + ".shop.sell")) {
            Player sell = Script.getPlayer(Annehmen.offer.get(p.getName() + ".shop.sell.seller"));
            if (sell != null) {
                sell.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt deinen Shop zu kaufen.");
            }
            p.sendMessage(DENIED + "Du hast es Abgelehnt den Shop zu kaufen.");
            Annehmen.offer.remove(p.getName() + ".shop.sell");
            Annehmen.offer.remove(p.getName() + ".shop.sell.seller");
            Annehmen.offer.remove(p.getName() + ".shop.sell.price");
            Annehmen.offer.remove(p.getName() + ".shop.sell.shop");

        } else if(Annehmen.offer.containsKey(p.getName() + ".house.rent")) {
            Player owner = Script.getPlayer(Annehmen.offer.get(p.getName() + ".house.rent.owner"));
            if (owner != null) {
                owner.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt dein Haus zu mieten.");
            }
            p.sendMessage(DENIED + "Du hast es Abgelehnt das Haus zu mieten.");
            Annehmen.offer.remove(p.getName() + ".house.rent");
            Annehmen.offer.remove(p.getName() + ".house.rent.owner");
            Annehmen.offer.remove(p.getName() + ".house.rent.price");
        } else {
            p.sendMessage(Messages.ERROR + "Du hast keine Anfrage.");
        }
        return false;
    }
}
