package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Player.Annehmen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RentCommand implements CommandExecutor {

    private static String PREFIX = "§8[§6Vermieten§8] §6» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 3) {
            p.sendMessage(Messages.ERROR + "/rent [Spieler] [Haus] [Preis]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "/rent [Spieler] [Haus] [Preis]");
            return true;
        }

        if(!Script.isInt(args[2])) {
            p.sendMessage(Messages.ERROR + "/rent [Spieler] [Haus] [Preis]");
            return true;
        }

        int houseID = Integer.parseInt(args[1]);
        int price = Integer.parseInt(args[2]);

        if(price < 0) {
            p.sendMessage(Messages.ERROR + "Du kannst niemanden Bezahlen, damit er bei dir wohnt.");
            return true;
        }

        House house = House.getHouseByID(houseID);
        if(house == null) {
            p.sendMessage(Messages.ERROR + "Das Haus existiert nicht.");
            return true;
        }

        if(house.getOwner() != Script.getNRPID(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht der Besitzer dieses Hauses.");
            return true;
        }

        if(house.livesInHouse(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler wohnt bereits in diesem Haus.");
            return true;
        }

        if(house.getFreeSlots() == 0) {
            p.sendMessage(Messages.ERROR + "Das Haus ist voll.");
            return true;
        }

        if(p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dir nicht selber ein Mietvertrag anbieten.");
            return true;
        }

        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " einen Mietvertrag für das Haus " + houseID + " in Höhe von " + price + "€ angeboten.");
        tg.sendMessage(PREFIX + Script.getName(p) + " hat dir einen Mietvertrag für das Haus " + houseID + " in Höhe von " + price + "€ angeboten.");
        Script.sendAcceptMessage(tg);
        Annehmen.offer.put(tg.getName() + ".house.rent", "" + houseID);
        Annehmen.offer.put(tg.getName() + ".house.rent.price", "" + price);
        Annehmen.offer.put(tg.getName() + ".house.rent.owner", p.getName());

        return false;
    }
}
