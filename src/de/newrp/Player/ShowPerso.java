package de.newrp.Player;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.House.House;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowPerso implements CommandExecutor {
    public static String PREFIX = "§8[§6Personalausweis§8] §6» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        int id = Script.getNRPID(p);
        if(args.length == 0) {
            p.sendMessage(PREFIX + "Deine Personalien:");
            p.sendMessage(PREFIX + "Name: " + Script.getName(p));
            p.sendMessage(" §8- §6Geburtsdatum: §c" + Script.getBirthday(id) + " (" + Script.getAge(Script.getNRPID(p)) + ")");
            if (Script.getGender(p).equals(Gender.MALE)) {
                p.sendMessage(" §8- §6Geschlecht: §cMännlich");
            } else if (Script.getGender(p).equals(Gender.FEMALE)) {
                p.sendMessage(" §8- §6Geschlecht: §cWeiblich");
            }
            if (House.hasHouse(id)) {
                StringBuilder houses = new StringBuilder();
                for (House h : House.getHouses(id)) {
                    houses.append(", ").append(h.getID());
                }
                p.sendMessage(" §8- §6Wohnhaft:§6" + houses.substring(1));
            } else {
                p.sendMessage(" §8- §6Wohnhaft: §6Obdachlos");
            }
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/showperso [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

            tg.sendMessage(PREFIX + Script.getName(p) + "s Personalien:");
            tg.sendMessage(PREFIX + "Name: " + Script.getName(p));
            tg.sendMessage(" §8- §6Geburtsdatum: §c" + Script.getBirthday(id) + " (" + Script.getAge(Script.getNRPID(p)) + ")");
            if (Script.getGender(p).equals(Gender.MALE)) {
                tg.sendMessage(" §8- §6Geschlecht: §cMännlich");
            } else if (Script.getGender(p).equals(Gender.FEMALE)) {
                tg.sendMessage(" §8- §6Geschlecht: §cWeiblich");
            }
            if (House.hasHouse(id)) {
                StringBuilder houses = new StringBuilder();
                for (House h : House.getHouses(id)) {
                    houses.append(", ").append(h.getID());
                }
                tg.sendMessage(" §8- §6Wohnhaft:§6" + houses.substring(1));
            } else {
                tg.sendMessage(" §8- §6Wohnhaft: §6Obdachlos");
            }
            Me.sendMessage(p, "zeigt " + Script.getName(tg) + " " + (Script.getGender(p)==Gender.MALE?"seinen":"ihren") + " Personalausweis.");

        return false;
    }
}
