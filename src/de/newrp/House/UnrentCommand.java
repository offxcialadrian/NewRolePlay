package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Player.Annehmen;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnrentCommand implements CommandExecutor {
    private static String PREFIX = "§8[§6Vermieten§8] §6» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            if(House.getHouses(Script.getNRPID(p)).size() > 1) {
                p.sendMessage(Messages.ERROR + "/unrent [Hausnummer]");
                return true;
            }

            House h = House.getHouses(Script.getNRPID(p)).get(0);
            if(h == null) {
                p.sendMessage(Messages.ERROR + "Es ist ein Fehler aufgetreten. Dieser Fehler wurde automatisch gemeldet.");
                Script.sendTeamMessage("§c§lBUG: §cEs ist ein Bug aufgetreten mit Bitte um Weiterleitung: house is null in simple unrent");
                return true;
            }

            if(h.getOwner() == Script.getNRPID(p)) {
                p.sendMessage(Messages.ERROR + "Du kannst nicht aus deinem eigenen Haus ausziehen.");
                return true;
            }

            h.removeMieter(Script.getNRPID(p));
            p.sendMessage(PREFIX + "Du bist aus Haus " + h.getID() + " ausgezogen.");
            if(Script.getOfflinePlayer(h.getOwner()).isOnline()) {
                Script.getPlayer(h.getOwner()).sendMessage(PREFIX + Script.getName(p) + " ist aus Haus "+ h.getID() + " ausgezogen.");
            } else {
                Script.addOfflineMessage(h.getOwner(), PREFIX + Script.getName(p) + " ist aus Haus "+ h.getID() + " ausgezogen.");
            }
            return true;

        }

        if(args.length == 1) {

            if(!Script.isInt(args[0])) {
                p.sendMessage(Messages.ERROR + "Bitte gebe eine valide Hausnummer an.");
                return true;
            }

            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Bitte gebe eine valide Hausnummer an.");
                return true;
            }

            int i = Integer.parseInt(args[0]);
            House h = House.getHouseByID(i);

            if(h == null) {
                p.sendMessage(Messages.ERROR + "Haus nicht gefunden.");
                return true;
            }

            if(Script.getNRPID(p) == h.getOwner()) {
                p.sendMessage(Messages.ERROR + "Du kannst nicht aus deinem eigenen Haus ausziehen.");
                return true;
            }

            h.removeMieter(Script.getNRPID(p));
            p.sendMessage(PREFIX + "Du bist aus Haus " + i + " ausgezogen.");
            if(Script.getOfflinePlayer(h.getOwner()).isOnline()) {
                Script.getPlayer(h.getOwner()).sendMessage(PREFIX + Script.getName(p) + " ist aus Haus "+ i + " ausgezogen.");
            } else {
                Script.addOfflineMessage(h.getOwner(), PREFIX + Script.getName(p) + " ist aus Haus "+ i + " ausgezogen.");
            }
            return true;

        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/unrent [Spieler] [Haus]");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "/unrent [Spieler] [Haus]");
            return true;
        }

        int houseID = Integer.parseInt(args[1]);
        House house = House.getHouseByID(houseID);
        if (house == null) {
            p.sendMessage(Messages.ERROR + "Das Haus existiert nicht.");
            return true;
        }

        if (house.getOwner() != Script.getNRPID(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht der Besitzer dieses Hauses.");
            return true;
        }

        if (!house.livesInHouse(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler wohnt nicht in diesem Haus.");
            return true;
        }

        if (p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dir nicht selber deinen Mietvertrag auflösen.");
            return true;
        }

        house.removeMieter(Script.getNRPID(tg));
        p.sendMessage(PREFIX + "Du hast " + tg.getName() + "s Mietvertrag für das Haus " + houseID + " aufgelöst.");
        if(tg.isOnline()) {
            Script.getPlayer(Script.getNRPID(tg)).sendMessage(PREFIX + Script.getName(p) + " hat deinen Mietvertrag für das Haus " + houseID + " aufgelöst.");
        } else {
            Script.addOfflineMessage(Script.getNRPID(tg), PREFIX + Script.getName(p) + " hat deinen Mietvertrag für das Haus " + houseID + " aufgelöst.");
        }
        return true;
    }
}