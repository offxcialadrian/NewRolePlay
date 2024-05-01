package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.House.House;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SellHouse implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 3) {
            p.sendMessage(Messages.ERROR + "/sellhouse [Spieler] [Haus] [Preis]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "/sellhouse [Spieler] [Haus] [Preis]");
            return true;
        }

        if(!Script.isInt(args[2])) {
            p.sendMessage(Messages.ERROR + "/sellhouse [Spieler] [Haus] [Preis]");
            return true;
        }

        int houseID = Integer.parseInt(args[1]);
        int price = Integer.parseInt(args[2]);

        if(price < 0) {
            p.sendMessage(Messages.ERROR + "Du kannst niemanden Bezahlen, damit er dein Haus kauft.");
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

        if(p.getLocation().distance(tg.getLocation())>5) {
            p.sendMessage(Messages.ERROR + "Du bist zu weit weg von " + Script.getName(tg) + ".");
            return true;
        }

        Annehmen.offer.put(tg.getName() + ".sellhouse", p.getName());
        Annehmen.offer.put(tg.getName() + ".sellhouse.house", houseID + "");
        Annehmen.offer.put(tg.getName() + ".sellhouse.price", price + "");
        p.sendMessage(Messages.INFO + "Du hast " + Script.getName(tg) + " das Haus " + house.getID() + " für " + price + "€ angeboten.");
        tg.sendMessage(Messages.INFO + Script.getName(p) + " bietet dir das Haus " + house.getID() + " für " + price + "€ an.");
        Script.sendAcceptMessage(tg);

        return false;
    }
}
