package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Administrator.Notifications;
import de.newrp.Berufe.Beruf;
import de.newrp.Forum.Forum;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import de.newrp.Medic.Medikamente;
import de.newrp.Medic.Rezept;
import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Annehmen implements CommandExecutor {
    public static final HashMap<String, String> offer = new HashMap<>();
    public static String PREFIX = "§8[§eAnnehmen§8] §e" + Messages.ARROW + " §7";
    public static String ACCEPTED = "§8[§eAngenommen§8] §e" + Messages.ARROW + " §7";

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
            Achievement.TEAMJOIN.grant(p);
            TeamSpeak.sync(Script.getNRPID(p));

        } else if (offer.containsKey(p.getName() + ".joinberuf")) {
            Player leader = Bukkit.getPlayer(offer.get(p.getName() + ".joinberuf"));
            if (leader == null) {
                p.sendMessage(Messages.ERROR + "Der Leader ist nicht mehr online.");
                return true;
            }

            if (!Beruf.isLeader(leader, true)) {
                p.sendMessage(Messages.ERROR + "Der Leader ist kein Leader mehr.");
                return true;
            }

            Beruf.Berufe beruf = Beruf.getBeruf(leader);
            p.sendMessage(ACCEPTED + "Du bist dem Beruf " + beruf.getName() + " beigetreten.");
            leader.sendMessage(PREFIX + Script.getName(p) + " ist dem Beruf beigetreten.");
            leader.sendMessage(Messages.INFO + "Nutze /abteilung [Spieler] [Abteilung], um " + Script.getName(p) + " in eine Abteilung zu verschieben.");
            leader.sendMessage(Messages.INFO + "Nutze /salary [Spieler] [Gehalt], um " + Script.getName(p) + " ein Gehalt zu geben.");
            beruf.addMember(p, leader);
            beruf.setMember(p);
            offer.remove(p.getName() + ".joinberuf");
            Achievement.BERUF_JOIN.grant(p);
            TeamSpeak.sync(Script.getNRPID(p));
            Forum.syncPermission(p);

        } else if(offer.containsKey(p.getName() + ".beziehung")) {
            Player tg = Script.getPlayer(offer.get(p.getName() + ".beziehung"));
            if (tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if (BeziehungCommand.hasRelationship(p)) {
                p.sendMessage(Messages.ERROR + "Du hast bereits eine Beziehung.");
                return true;
            }

            if (BeziehungCommand.hasRelationship(tg)) {
                p.sendMessage(Messages.ERROR + Script.getName(tg) + " ist bereits in einer Beziehung.");
                return true;
            }

            if (tg == p) {
                p.sendMessage(Messages.ERROR + "Du kannst nicht mit dir selbst in einer Beziehung sein.");
                return true;
            }

            BeziehungCommand.createRelationship(p, tg);
            p.sendMessage(ACCEPTED + "Du bist nun mit " + Script.getName(tg) + " in einer Beziehung.");
            tg.sendMessage(ACCEPTED + "Du bist nun mit " + Script.getName(p) + " in einer Beziehung.");
            new Particle(org.bukkit.Particle.HEART, p.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 2).sendAll();
            new Particle(org.bukkit.Particle.HEART, tg.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 2).sendAll();
            offer.remove(p.getName() + ".beziehung");
        } else if(offer.containsKey(p.getName() + ".loan")) {
            Player lender = Script.getPlayer(offer.get(p.getName() + ".loan"));
            if (lender == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            int days = Integer.parseInt(offer.get(p.getName() + ".loan.days"));
            double interest = Double.parseDouble(offer.get(p.getName() + ".loan.interest"));
            int amount = Integer.parseInt(offer.get(p.getName() + ".loan.amount"));

            if (amount < 1 || amount > Stadtkasse.getStadtkasse()) {
                p.sendMessage(Messages.ERROR + "Der Betrag muss zwischen 1€ und " + Stadtkasse.getStadtkasse() + "€ liegen.");
                return true;
            }

            if (Stadtkasse.getStadtkasse() < amount) {
                p.sendMessage(Messages.ERROR + "Die Stadtkasse hat nicht genügend Geld.");
                return true;
            }

            Stadtkasse.removeStadtkasse(amount, "Kreditvergabe an " + Script.getName(p));
            Script.addMoney(p, PaymentType.BANK, amount);
            Script.executeAsyncUpdate("INSERT INTO loans (userID, amount, zins, time) VALUES (" + Script.getNRPID(p) + ", " + amount + ", " + interest + ", " + (System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(days)) + ");");
            p.sendMessage(ACCEPTED + "Du hast den Kredit über " + days + " Tage und " + amount + "€ angenommen (Zinssatz: " + interest + "%).");
            lender.sendMessage(ACCEPTED + "Du hast von " + Script.getName(p) + " ein Kreditangebot über " + days + " Tage und " + amount + "€ erhalten (Zinssatz: " + interest + "%).");
            offer.remove(p.getName() + ".loan");
            offer.remove(p.getName() + ".loan.days");
            offer.remove(p.getName() + ".loan.interest");
            offer.remove(p.getName() + ".loan.amount");
        } else if(offer.containsKey(p.getName() + ".vertrag.from")) {
            Player tg = Script.getPlayer(offer.get(p.getName() + ".vertrag.from"));
            if (tg == null) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht mehr online.");
                return true;
            }

            if (!Annehmen.offer.containsKey(p.getName() + ".vertrag.condition")) {
                p.sendMessage(Messages.ERROR + "Dir wird nichts angeboten.");
                return true;
            }

            String condition = Annehmen.offer.get(p.getName() + ".vertrag.condition");
            Vertrag.saveVertrag(tg, p, condition, true);
            p.sendMessage(ACCEPTED + "Du hast den Vertrag angenommen.");
            tg.sendMessage(PREFIX + Script.getName(p) + " hat den Vertrag angenommen.");
            Log.NORMAL.write(p, "hat den Vertrag von " + Script.getName(tg) + " angenommen. (" + condition + ")");
            Log.NORMAL.write(tg, "hat den Vertrag von " + Script.getName(p) + " angenommen. (" + condition + ")");
            offer.remove(p.getName() + ".vertrag.from");
            offer.remove(p.getName() + ".vertrag.condition");

        } else if(offer.containsKey(p.getName() + ".tasche")) {
            Player tasche = Script.getPlayer(offer.get(p.getName() + ".tasche"));
            if (tasche == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if (tasche.getLocation().distance(p.getLocation()) > 5) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
                return true;
            }

            p.sendMessage(ACCEPTED + "Du siehst nun die Tasche von " + Script.getName(tasche) + ".");
            tasche.sendMessage(PREFIX + Script.getName(p) + " sieht nun deine Tasche.");

            Inventory inv = Bukkit.createInventory(null, 36, "§8[§9Tasche§8] §e» §9" + tasche.getName());
            for (ItemStack is : tasche.getInventory().getContents()) {
                if (is == null) continue;
                inv.addItem(is);
            }
            p.openInventory(inv);
            offer.remove(p.getName() + ".tasche");

        } else if(offer.containsKey(p.getName() + ".erstehilfeschein")) {
            Player medic = Script.getPlayer(offer.get(p.getName() + ".erstehilfeschein"));
            if (medic == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if (Script.getMoney(p, PaymentType.CASH) < 250) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld für einen Erste-Hilfe-Kurs.");
                return true;
            }

            Licenses.ERSTE_HILFE.grant(Script.getNRPID(medic));
            p.sendMessage(ACCEPTED + "Du hast den Erste-Hilfe-Schein erfolgreich erworben.");
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(Beruf.PREFIX + Script.getName(medic) + " hat " + Script.getName(p) + " einen Erste-Hilfe-Schein ausgestellt.");
            Script.executeAsyncUpdate("INSERT INTO erste_hilfe (nrp_id, awarded) VALUES (" + Script.getNRPID(medic) + ", " + System.currentTimeMillis() + ");");
            Script.removeMoney(p, PaymentType.CASH, 250);
            Stadtkasse.addStadtkasse(250, "Erste-Hilfe-Kurs von " + Script.getName(p), null);
            offer.remove(p.getName() + ".erstehilfeschein");

        } else if(offer.containsKey(p.getName() + ".sellhouse")) {
            Player seller = Script.getPlayer(offer.get(p.getName() + ".sellhouse"));
            int price = Integer.parseInt(offer.get(p.getName() + ".sellhouse.price"));
            int houseID = Integer.parseInt(offer.get(p.getName() + ".sellhouse.house"));

            if (seller == null) {
                p.sendMessage(Messages.ERROR + "Der Verkäufer ist nicht mehr online.");
                return true;
            }

            if (Script.getMoney(p, PaymentType.BANK) < price) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld auf deinem Bankkonto.");
                return true;
            }

            House house = House.getHouseByID(houseID);
            if (house == null) {
                p.sendMessage(Messages.ERROR + "Das Haus existiert nicht mehr.");
                return true;
            }

            if (house.getOwner() != Script.getNRPID(seller)) {
                p.sendMessage(Messages.ERROR + "Der Verkäufer ist nicht mehr der Besitzer des Hauses.");
                return true;
            }

            if (house.getOwner() == Script.getNRPID(p)) {
                p.sendMessage(Messages.ERROR + "Du bist bereits der Besitzer des Hauses.");
                return true;
            }

            if (House.getHouses(Script.getNRPID(p)).size() >= SlotLimit.HOUSE.get(Script.getNRPID(p))) {
                p.sendMessage(Messages.ERROR + "Du hast zuviele Häuser um in noch einem weiteren zu wohnen.");
                p.sendMessage(Messages.INFO + "Du kannst einen weiteren Hausslot im Shop erwerben.");
                return true;
            }

            Script.executeUpdate("DELETE FROM house_bewohner WHERE houseID = " + houseID + " AND mieterID = " + Script.getNRPID(seller));
            Script.removeMoney(p, PaymentType.BANK, price);
            Stadtkasse.addStadtkasse((int) Script.getPercent(Steuern.Steuer.HAUSVERKAUFSSTEUER.getPercentage(), price), "Hausverkauf von " + Script.getName(seller) + " an " + Script.getName(p) + " (Haus: " + house.getID() + ")", Steuern.Steuer.HAUSVERKAUFSSTEUER);
            int add = price - (int) Script.getPercent(Steuern.Steuer.HAUSVERKAUFSSTEUER.getPercentage(), price);
            Script.addMoney(seller, PaymentType.BANK, add);
            house.setOwner(Script.getNRPID(p));
            house.updateSign();
            for(HouseAddon addon : house.getAddons()) {
                house.removeAddon(addon);
            }
            house.setSlots(1);
            Script.executeAsyncUpdate("INSERT INTO house_bewohner (houseID, mieterID, vermieter, miete, nebenkosten, immobilienmarkt) VALUES (" + houseID + ", " + Script.getNRPID(p) + ", " + true + ", " + 0 + ", 0, FALSE);");
            p.sendMessage(ACCEPTED + "Du hast das Haus erfolgreich gekauft.");
            seller.sendMessage(PREFIX + Script.getName(p) + " hat dein Haus " + house.getID() + " gekauft.");
            Log.HIGH.write(p.getName() + " hat das Haus " + house.getID() + " gekauft.");
            offer.remove(p.getName() + ".sellhouse");
            offer.remove(p.getName() + ".sellhouse.seller");
            offer.remove(p.getName() + ".sellhouse.price");
            offer.remove(p.getName() + ".sellhouse.house");
            Achievement.HAUS.grant(p);

        } else if(offer.containsKey(p.getName() + ".joinorganisation")) {
            Player leader = Bukkit.getPlayer(offer.get(p.getName() + ".joinorganisation"));
            if (leader == null) {
                p.sendMessage(Messages.ERROR + "Der Leader ist nicht mehr online.");
                return true;
            }

            if (!Organisation.isLeader(leader, true)) {
                p.sendMessage(Messages.ERROR + "Der Leader ist kein Leader mehr.");
                return true;
            }

            Organisation organisation = Organisation.getOrganisation(leader);
            organisation.addExp(Script.getRandom(50, 100));
            p.sendMessage(ACCEPTED + "Du bist der Organisation " + organisation.getName() + " beigetreten.");
            leader.sendMessage(PREFIX + Script.getName(p) + " ist der Organisation beigetreten.");
            leader.sendMessage(Messages.INFO + "Nutze /salary [Spieler] [Gehalt], um " + Script.getName(p) + " ein Gehalt zu geben.");
            organisation.addMember(p, leader);
            organisation.setMember(p);
            offer.remove(p.getName() + ".joinorganisation");
            TeamSpeak.sync(Script.getNRPID(p));
            Forum.syncPermission(p);

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

            if (Shops.getShopsByPlayer(Script.getNRPID(p)).size() > SlotLimit.SHOP.get(Script.getNRPID(p))) {
                p.sendMessage(Messages.ERROR + "Du hast zuviele Shops");
                p.sendMessage(Messages.INFO + "Du kannst einen weiteren Shopslot im Shop erwerben.");
                return true;
            }

            Script.removeMoney(p, PaymentType.BANK, price);
            double tax = Steuern.Steuer.SHOP_VERKAUFSSTEUER.getPercentage();
            Stadtkasse.addStadtkasse((int) Script.getPercent(tax, price), "Shopverkauf von " + Script.getName(sell) + " an " + Script.getName(p) + " (Shop: " + shop.getPublicName() + ")", Steuern.Steuer.SHOP_VERKAUFSSTEUER);
            int add = price - (int) Script.getPercent(tax, price);
            Notifications.sendMessage(Notifications.NotificationType.SHOP, "§6" + Script.getName(sell) + "§7 hat §6" + Script.getName(p) + " §7" + (Script.getGender(p) == Gender.MALE ? "seinen" : "ihren") + " Shop §6" + shop.getPublicName() + "§7 für §6" + price + "€ §7verkauft.");
            Script.addMoney(sell, PaymentType.BANK, add);
            shop.setOwner(Script.getNRPID(p));
            p.sendMessage(ACCEPTED + "Du hast den Shop erfolgreich gekauft.");
            sell.sendMessage(PREFIX + Script.getName(p) + " hat deinen Shop " + shop.getPublicName() + " gekauft.");
            Log.HIGH.write(p.getName() + " hat den Shop " + shop.getPublicName() + " gekauft.");
            offer.remove(p.getName() + ".shop.sell");
            offer.remove(p.getName() + ".shop.sell.seller");
            offer.remove(p.getName() + ".shop.sell.price");
            offer.remove(p.getName() + ".shop.sell.shop");
            Achievement.SHOP_OWNER.grant(p);

        } else if(offer.containsKey(p.getName() + ".rezept")) {
            Player tg = Script.getPlayer(offer.get(p.getName() + ".rezept"));
            Medikamente m = Medikamente.getMedikament(offer.get(p.getName() + ".rezept.medikament"));
            if (m == null) return true;
            p.sendMessage(ACCEPTED + "Du hast ein Rezept für " + m.getName() + " erhalten.");
            tg.sendMessage(ACCEPTED + Script.getName(p) + " hat das Rezept genommen.");
            p.getInventory().addItem(m.getRezept());
            Log.NORMAL.write(p, "hat ein Rezept für " + m.getName() + " von " + Script.getName(tg) + " erhalten.");
            Log.NORMAL.write(tg, "hat ein Rezept für " + m.getName() + " an " + Script.getName(p) + " gegeben.");
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(Rezept.PREFIX + Script.getName(tg) + " hat " + Script.getName(p) + " ein Rezept für " + m.getName() + " ausgestellt.");
            offer.remove(p.getName() + ".rezept");
            offer.remove(p.getName() + ".medikament");
            Stadtkasse.removeStadtkasse(30, "Rezeptausstellung an " + Script.getName(p));
            Achievement.REZEPT.grant(p);

        } else if(offer.containsKey(p.getName() + ".house.rent")) {
            Player owner = Script.getPlayer(offer.get(p.getName() + ".house.rent.owner"));
            int price = Integer.parseInt(offer.get(p.getName() + ".house.rent.price"));
            int houseID = Integer.parseInt(offer.get(p.getName() + ".house.rent"));

            House house = House.getHouseByID(houseID);

            if(owner == null) {
                p.sendMessage(Messages.ERROR + "Der Besitzer ist nicht mehr online.");
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

            if(House.getHouses(Script.getNRPID(p)).size() >= SlotLimit.HOUSE.get(Script.getNRPID(p))) {
                p.sendMessage(Messages.ERROR + "Du hast zuviele Häuser um in noch einem weiteren zu wohnen.");
                p.sendMessage(Messages.INFO + "Du kannst einen weiteren Hausslot im Shop erwerben.");
                return true;
            }

            house.addMieter(new House.Mieter(p.getName(), Script.getNRPID(p), price, 0), false);
            p.sendMessage(ACCEPTED + "Du hast das Haus erfolgreich gemietet.");
            owner.sendMessage(PREFIX + Script.getName(p) + " hat dein Haus " + house.getID() + " gemietet.");
            Log.HIGH.write(p.getName() + " hat das Haus " + house.getID() + " gemietet.");

            offer.remove(p.getName() + ".house.rent");
            offer.remove(p.getName() + ".house.rent.owner");
            offer.remove(p.getName() + ".house.rent.price");
            Achievement.HAUS.grant(p);
            Achievement.HOUSE_RENT.grant(p);
        } else {
            p.sendMessage(Messages.ERROR + "Dir wird nichts angeboten.");
        }
        return true;
    }
}
