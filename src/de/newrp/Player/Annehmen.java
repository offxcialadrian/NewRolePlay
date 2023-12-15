package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.House.House;
import de.newrp.Shop.Shops;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Annehmen implements CommandExecutor {
    public static final HashMap<String, String> offer = new HashMap<>();
    public static String PREFIX = "§8[§eAnnehmen§8] §e" + Messages.ARROW + " ";
    public static String ACCEPTED = "§8[§eAngenommen§8] §e" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (offer.containsKey(p.getName() + ".jointeam")) {
            Team.Teams team = Team.Teams.getTeam(offer.get(p.getName() + ".jointeam"));
            if (team == null) {
                p.sendMessage(Messages.ERROR + "Das Team existiert nicht.");
                return true;
            }

            if (Team.getTeam(p) != null) {
                p.sendMessage(Messages.ERROR + "Du bist bereits in einem Team.");
                return true;
            }

            if (Team.getTeam(p) == team) {
                p.sendMessage(Messages.ERROR + "Du bist bereits in diesem Team.");
                return true;
            }

            team.addMember(p);
            p.sendMessage(ACCEPTED + "Du bist dem Team " + team.getName() + " beigetreten.");
            Script.addEXP(p, 100);
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (Team.getTeam(all) == team)
                    all.sendMessage("§8[§e" + team.getName() + "§8] §e" + p.getName() + " §8» §7ist dem Team beigetreten.");
            }
            offer.remove(p.getName() + ".jointeam");

        } else if (offer.containsKey(p.getName() + ".joinberuf")) {
            Player leader = Bukkit.getPlayer(offer.get(p.getName() + ".joinberuf"));
            if (leader == null) {
                p.sendMessage(Messages.ERROR + "Der Leader ist nicht mehr online.");
                return true;
            }

            if (!Beruf.isLeader(leader)) {
                p.sendMessage(Messages.ERROR + "Der Leader ist kein Leader mehr.");
                return true;
            }

            Beruf.Berufe beruf = Beruf.getBeruf(leader);
            p.sendMessage(ACCEPTED + "Du bist dem Beruf " + beruf.getName() + " beigetreten.");
            leader.sendMessage(PREFIX + Script.getName(p) + " ist dem Beruf beigetreten.");
            leader.sendMessage(Messages.INFO + "Nutze /abteilung [Spieler] [Abteilung], um " + Script.getName(p) + " in eine Abteilung zu verschieben.");
            leader.sendMessage(Messages.INFO + "Nutze /salary [Spieler] [Gehalt], um " + Script.getName(p) + " ein Gehalt zu geben.");
            beruf.addMember(p, leader);
            Script.addEXP(p, Script.getRandom(50, 100));
            offer.remove(p.getName() + ".joinberuf");

        } else if (offer.containsKey(p.getName() + ".shop.sell")) {
            Player sell = Script.getPlayer(offer.get(p.getName() + ".shop.sell.seller"));
            int price = Integer.parseInt(offer.get(p.getName() + ".shop.sell.price"));
            int shopid = Integer.parseInt(offer.get(p.getName() + ".shop.sell.shop"));

            if (sell == null) {
                p.sendMessage(Messages.ERROR + "Der Verkäufer ist nicht mehr online.");
                return true;
            }

            if (Script.getMoney(p, PaymentType.BANK) < price) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld auf deinem Bankkonto.");
                return true;
            }

            Shops shop = Shops.getShop(shopid);
            if (shop == null) {
                p.sendMessage(Messages.ERROR + "Der Shop existiert nicht mehr.");
                return true;
            }

            if (shop.getOwner() != Script.getNRPID(sell)) {
                p.sendMessage(Messages.ERROR + "Der Verkäufer ist nicht mehr der Besitzer des Shops.");
                return true;
            }

            if (shop.getOwner() == Script.getNRPID(p)) {
                p.sendMessage(Messages.ERROR + "Du bist bereits der Besitzer des Shops.");
                return true;
            }

            Script.removeMoney(p, PaymentType.BANK, price);
            double tax = Steuern.Steuer.SHOP_VERKAUFSSTEUER.getPercentage();
            Stadtkasse.addStadtkasse((int) Script.getPercent(tax, price));
            int add = price - (int) Script.getPercent(tax, price);
            Script.addMoney(sell, PaymentType.BANK, add);
            shop.setOwner(Script.getNRPID(p));
            p.sendMessage(ACCEPTED + "Du hast den Shop erfolgreich gekauft.");
            sell.sendMessage(PREFIX + Script.getName(p) + " hat deinen Shop " + shop.getPublicName() + " gekauft.");
            Log.HIGH.write(p.getName() + " hat den Shop " + shop.getPublicName() + " gekauft.");
            offer.remove(p.getName() + ".shop.sell");
            offer.remove(p.getName() + ".shop.sell.seller");
            offer.remove(p.getName() + ".shop.sell.price");
            offer.remove(p.getName() + ".shop.sell.shop");

        } else if(offer.containsKey(p.getName() + ".house.rent")) {
            Player owner = Script.getPlayer(offer.get(p.getName() + ".house.rent.owner"));
            int price = Integer.parseInt(offer.get(p.getName() + ".house.rent.price"));
            int houseID = Integer.parseInt(offer.get(p.getName() + ".house.rent"));

            House house = House.getHouseByID(houseID);

            if(owner == null) {
                p.sendMessage(Messages.ERROR + "Der Besitzer ist nicht mehr online.");
                return true;
            }

            if(Script.getMoney(p, PaymentType.BANK) < price) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld auf deinem Bankkonto.");
                return true;
            }

            if(house == null) {
                p.sendMessage(Messages.ERROR + "Das Haus existiert nicht mehr.");
                return true;
            }

            if(house.getOwner() != Script.getNRPID(owner)) {
                p.sendMessage(Messages.ERROR + "Der Besitzer ist nicht mehr der Besitzer des Hauses.");
                return true;
            }

            if(house.getOwner() == Script.getNRPID(p)) {
                p.sendMessage(Messages.ERROR + "Du bist bereits der Besitzer des Hauses.");
                return true;
            }

            if(house.livesInHouse(p)) {
                p.sendMessage(Messages.ERROR + "Du wohnst bereits in diesem Haus.");
                return true;
            }

            if(house.getFreeSlots() == 0) {
                p.sendMessage(Messages.ERROR + "Das Haus ist voll.");
                return true;
            }

            house.addMieter(new House.Mieter(p.getName(), Script.getNRPID(p), price, 0), false);
            p.sendMessage(ACCEPTED + "Du hast das Haus erfolgreich gemietet.");
            owner.sendMessage(PREFIX + Script.getName(p) + " hat dein Haus " + house.getID() + " gemietet.");
            Log.HIGH.write(p.getName() + " hat das Haus " + house.getID() + " gemietet.");

            offer.remove(p.getName() + ".house.rent");
            offer.remove(p.getName() + ".house.rent.owner");
            offer.remove(p.getName() + ".house.rent.price");
        } else {
            p.sendMessage(Messages.ERROR + "Dir wird nichts angeboten.");
        }
        return true;
    }
}
