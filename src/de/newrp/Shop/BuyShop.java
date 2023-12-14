package de.newrp.Shop;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Government.Stadtkasse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyShop implements CommandExecutor {

    private static final String PREFIX = "§8[§6Shop§8] §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        Shops shop = null;
        for (Shops shops : Shops.values()) {
            if (shops.getBuyLocation() != null) {
                if (p.getLocation().distance(shops.getBuyLocation()) < 5) {
                    shop = shops;
                }
            }
        }

        if (shop == null) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der nähe von einem Shop.");
            return true;
        }

        if(args.length == 0) {
            if(shop.getOwner() == 0) {
                p.sendMessage(PREFIX + "Dieser Shop wird derzeit von der Stadt verkauft.");
                p.sendMessage(Messages.INFO + "Kaufe ihn mit /buyshop buy oder nutze ihn mit /buyshop info");
                return true;
            }

            if(shop.getOwner() == -1) {
                p.sendMessage(PREFIX + "Dieser Shop wird derzeit nicht verkauft.");
                return true;
            }

            if(shop.getOwner() > 0) {
                p.sendMessage(PREFIX + "Dieser Shop gehört derzeit " + Script.getOfflinePlayer(shop.getOwner()).getName() + ".");
                return true;
            }
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("buy")) {
                if(shop.getOwner() > 0) {
                    p.sendMessage(Messages.ERROR + "Dieser Shop gehört bereits " + Script.getOfflinePlayer(shop.getOwner()).getName() + ".");
                    return true;
                }

                if(shop.getOwner() != 0) {
                    p.sendMessage(Messages.ERROR + "Dieser Shop wird derzeit nicht verkauft.");
                    return true;
                }

                if(Script.getMoney(p, PaymentType.BANK) < shop.getPrice()) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    return true;
                }

                Script.removeMoney(p, PaymentType.BANK, shop.getPrice());
                Stadtkasse.addStadtkasse(shop.getPrice());
                shop.setOwner(Script.getNRPID(p));
                p.sendMessage(PREFIX + "Du hast den Shop erfolgreich gekauft.");
                Log.HIGH.write(p.getName() + " hat den Shop " + shop.getPublicName() + " gekauft.");
                Script.sendTeamMessage(PREFIX + Script.getName(p) + " hat den Shop " + shop.getPublicName() + " gekauft.");
                Script.executeAsyncUpdate("DELETE FROM shopprice WHERE shopID = " + shop.getID() + ";");
                return true;

            }

            if(args[0].equalsIgnoreCase("info")) {
                if(shop.getOwner() == 0) {
                    p.sendMessage(PREFIX + "Dieser Shop wird derzeit von der Stadt verkauft.");
                    p.sendMessage(Messages.INFO + "Preis: " + shop.getPrice() + "€");
                    p.sendMessage(Messages.INFO + "Miete (Gebäude): " + shop.getRent() + "€");
                    p.sendMessage(Messages.INFO + "Lager: " + shop.getLagerSize());
                    p.sendMessage(Messages.INFO + "Betriebskosten: " + shop.getRunningCost() + "€");
                    return true;
                }
            }
        }


        return false;
    }
}
