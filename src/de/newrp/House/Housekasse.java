package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.API.SlotLimit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Housekasse implements CommandExecutor {

    private static String PREFIX = "§8[§6Hauskasse§8] §6» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        House h = House.getNearHouse(p.getLocation(), 5);
        if(h == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe eines Hauses.");
            return true;
        }

        if(!h.livesInHouse(p)) {
            p.sendMessage(Messages.ERROR + "Du wohnst nicht in diesem Haus.");
            return true;
        }

        if(h.getOwner() != Script.getNRPID(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht der Besitzer dieses Hauses.");
            return true;
        }

        if(!h.getAddons().contains(HouseAddon.HAUSKASSE)) {
            p.sendMessage(Messages.ERROR + "Dieses Haus hat keine Hauskasse.");
            return true;
        }

        if(House.getHouses(Script.getNRPID(p)).size() > SlotLimit.HOUSE.get(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du hast zuviele Häuser");
            p.sendMessage(Messages.INFO + "Du kannst einen weiteren Hausslot im Shop erwerben.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage("§6=== Hauskasse ===");
            p.sendMessage("§8» §6Guthaben: §7" + h.getKasse() + "€");
            p.sendMessage("§8» §6Mieter: §7" + h.getMieter().size());
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("einzahlen") || args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("payin") || args[0].equalsIgnoreCase("add")) {
                p.sendMessage(Messages.ERROR + "/hauskasse einzahlen [Betrag]");
                return true;
            }

            if(args[0].equalsIgnoreCase("auszahlen") || args[0].equalsIgnoreCase("abbuchen") || args[0].equalsIgnoreCase("payout") || args[0].equalsIgnoreCase("remove")) {
                p.sendMessage(Messages.ERROR + "/hauskasse auszahlen [Betrag]");
                return true;
            }


            p.sendMessage(Messages.ERROR + "/hauskasse [einzahlen/auszahlen]");
            return true;
        }

        if(args.length == 2) {
            if(!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "/hauskasse [einzahlen/auszahlen] [Betrag]");
                return true;
            }

            int i = Integer.parseInt(args[1]);
            if(i < 0) {
                p.sendMessage(Messages.ERROR + "Du kannst keine negativen Beträge einzahlen.");
                return true;
            }

            if(args[0].equalsIgnoreCase("einzahlen") || args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("payout") || args[0].equalsIgnoreCase("add")) {
                if(i > Script.getMoney(p, PaymentType.CASH)) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    return true;
                }

                Script.removeMoney(p, PaymentType.CASH, i);
                h.addKasse(i);
                p.sendMessage(PREFIX + "Du hast " + i + "€ in die Hauskasse eingezahlt.");
                return true;
            }

            if(args[0].equalsIgnoreCase("auszahlen") || args[0].equalsIgnoreCase("abbuchen") || args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("remove")) {
                if(i > h.getKasse()) {
                    p.sendMessage(Messages.ERROR + "Die Hauskasse hat nicht genug Geld.");
                    return true;
                }

                Script.addMoney(p, PaymentType.CASH, i);
                h.setKasse(h.getKasse() - i);
                p.sendMessage(PREFIX + "Du hast " + i + "€ aus der Hauskasse ausgezahlt.");
                return true;
            }

            p.sendMessage(Messages.ERROR + "/hauskasse [einzahlen/auszahlen] [Betrag]");
            return true;
        }

        return false;
    }
}
