package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MieterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            House h = House.getNearHouse(p.getLocation(), 5);
            if(h == null) {
                p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe eines Hauses (Alternativ kannst du auch /mieter [Hausnummer] verwenden)");
                return true;
            }

            if(!h.hasAccess(Script.getNRPID(p))) {
                p.sendMessage(Messages.ERROR + "Du wohnst nicht in diesem Haus.");
                return true;
            }

            p.sendMessage("§6=== Haus " + h.getID() + " ===");
            for(House.Mieter mieter : h.getMieter()) {
                OfflinePlayer tg = Script.getOfflinePlayer(mieter.getID());
                p.sendMessage("§8» §6" + mieter.getName() + " §8× §6" + mieter.getMiete() + " §8× §6" + (!tg.isOnline()?"Offline seit " + Script.dateFormat2.format(Script.getLastDisconnect(tg)):"Online"));
            }
            return true;
        }

        if(args.length == 1) {
            if(!Script.isInt(args[0])) {
                p.sendMessage(Messages.ERROR + "/mieter <Hausnummer>");
                return true;
            }

            int i = Integer.parseInt(args[0]);
            House h = House.getHouseByID(i);

            if(h == null) {
                p.sendMessage(Messages.ERROR + "Haus nicht gefunden.");
                return true;
            }

            if(!h.hasAccess(Script.getNRPID(p))) {
                p.sendMessage(Messages.ERROR + "Du wohnst nicht in diesem Haus.");
                return true;
            }

            p.sendMessage("§6=== Haus " + h.getID() + " ===");
            for(House.Mieter mieter : h.getMieter()) {
                OfflinePlayer tg = Script.getOfflinePlayer(mieter.getID());
                p.sendMessage("§8» §6" + mieter.getName() + " §8× §6" + mieter.getMiete() + "€ §8× §6" + (!tg.isOnline()?"Offline seit " + Script.dateFormat2.format(Script.getLastDisconnect(tg)):"Online"));
            }

        }

        return false;
    }
}
