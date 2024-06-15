package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.Team;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Contract.model.Contract;
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
                leader.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, dem Beruf " + Beruf.getBeruf(leader).getName() + " beizutreten.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, dem Beruf " + Beruf.getBeruf(leader).getName() + " beizutreten.");
            Annehmen.offer.remove(p.getName());
        } else if(Annehmen.offer.containsKey(p.getName() + ".jointeam")) {
            Player leader = Script.getPlayer(Annehmen.offer.get(p.getName() + ".jointeam"));
            if (leader != null) {
                leader.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, dem Team " + Team.getTeam(leader).getName() + " beizutreten.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, dem Team " + Team.getTeam(leader).getName() + " beizutreten.");
            Annehmen.offer.remove(p.getName());
        } else if(Annehmen.offer.containsKey(p.getName() + ".rezept")) {
            Player tg = Script.getPlayer(Annehmen.offer.get(p.getName() + ".rezept"));
            if (tg != null) {
                tg.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, dein Rezept anzunehmen.");
            }

            p.sendMessage(DENIED + "Du hast es abgelehnt, das Rezept anzunehmen.");
            Annehmen.offer.remove(p.getName() + ".rezept");
            Annehmen.offer.remove(p.getName() + ".rezept.medikament");

        } else if(Annehmen.offer.containsKey(p.getName() + ".beziehung")) {
            Player tg = Script.getPlayer(Annehmen.offer.get(p.getName() + ".beziehung"));
            if (tg != null) {
                tg.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, mit dir zusammen zu sein.");
            }

            p.sendMessage(DENIED + "Du hast es abgelehnt, mit " + Script.getName(tg) + " zusammen zu sein.");
            Annehmen.offer.remove(p.getName() + ".beziehung");
        } else if(Annehmen.offer.containsKey(p.getName() + ".erstehilfeschein")) {
            p.sendMessage(DENIED + "Du hast es abgelehnt, den Erste-Hilfe-Schein zu erhalten.");
            Annehmen.offer.remove(p.getName() + ".erstehilfeschein");

        } else if(Annehmen.offer.containsKey(p.getName() + ".sellhouse")) {
            Player owner = Script.getPlayer(Annehmen.offer.get(p.getName() + ".sellhouse"));
            if (owner != null) {
                owner.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt dein Haus zu kaufen.");
            }

            p.sendMessage(DENIED + "Du hast es abgelehnt, das Haus zu kaufen.");
            Annehmen.offer.remove(p.getName() + ".sellhouse");
            Annehmen.offer.remove(p.getName() + ".sellhouse.house");
            Annehmen.offer.remove(p.getName() + ".sellhouse.price");

        } else if(Annehmen.offer.containsKey(p.getName() + ".vertrag.from")) {
            Player tg = Script.getPlayer(Annehmen.offer.get(p.getName() + ".vertrag.from"));
            if (tg != null) {
                tg.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, dein Vertrag anzunehmen.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, den Vertrag anzunehmen.");
            Annehmen.offer.remove(p.getName() + ".vertrag.from");
            Annehmen.offer.remove(p.getName() + ".vertrag.condition");
        } else if(Annehmen.offer.containsKey(p.getName() + ".tasche")) {
            Player tg = Script.getPlayer(Annehmen.offer.get(p.getName() + ".tasche"));
            if (tg != null) {
                tg.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, deine Tasche zu sehen.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, die Tasche zu sehen.");
            Annehmen.offer.remove(p.getName() + ".tasche");

        } else if(Annehmen.offer.containsKey(p.getName() + ".joinorganisation")) {

            Player leader = Script.getPlayer(Annehmen.offer.get(p.getName() + ".joinorganisation"));
            if (leader != null) {
                leader.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, der Organisation " + Beruf.getBeruf(leader).getName() + " beizutreten.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, der Organisation " + Beruf.getBeruf(leader).getName() + " beizutreten.");
            Annehmen.offer.remove(p.getName());
        } else if(Annehmen.offer.containsKey(p.getName() + ".shop.sell")) {
            Player sell = Script.getPlayer(Annehmen.offer.get(p.getName() + ".shop.sell.seller"));
            if (sell != null) {
                sell.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, deinen Shop zu kaufen.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, den Shop zu kaufen.");
            Annehmen.offer.remove(p.getName() + ".shop.sell");
            Annehmen.offer.remove(p.getName() + ".shop.sell.seller");
            Annehmen.offer.remove(p.getName() + ".shop.sell.price");
            Annehmen.offer.remove(p.getName() + ".shop.sell.shop");

        } else if(Annehmen.offer.containsKey(p.getName() + ".house.rent")) {
            Player owner = Script.getPlayer(Annehmen.offer.get(p.getName() + ".house.rent.owner"));
            if (owner != null) {
                owner.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, dein Haus zu mieten.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, das Haus zu mieten.");
            Annehmen.offer.remove(p.getName() + ".house.rent");
            Annehmen.offer.remove(p.getName() + ".house.rent.owner");
            Annehmen.offer.remove(p.getName() + ".house.rent.price");
        } else if (Annehmen.offer.containsKey(p.getName() + ".rob")) {
            Player robber = Script.getPlayer(Annehmen.offer.get(p.getName() + ".rob"));
            if (robber != null) {
                robber.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, den Ausraub einzutragen.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, den Ausraub einzutragen.");
            Annehmen.offer.remove(p.getName() + ".rob");
        } else if (Annehmen.offer.containsKey(p.getName() + ".contract")) {
            Player target = Script.getPlayer(Annehmen.offer.get(p.getName() + ".contract"));
            if (target != null) {
                target.sendMessage(DENIED + Script.getName(p) + " hat es abgelehnt, das Kopfgeld einzutragen.");
            }
            p.sendMessage(DENIED + "Du hast es abgelehnt, das Kopfgeld einzutragen.");
            Annehmen.offer.remove(p.getName() + ".contract");
            Contract.removeOffer(p);
        } else {
            p.sendMessage(Messages.ERROR + "Du hast keine Anfrage.");
        }
        return false;
    }
}
