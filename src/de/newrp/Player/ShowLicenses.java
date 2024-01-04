package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Chat.Me;
import de.newrp.House.House;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ShowLicenses implements CommandExecutor {

    private static String PREFIX = "§8[§6Lizenzen§8] §6» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        int id = Script.getNRPID(p);
        if(args.length == 0) {
            StringBuilder sb = new StringBuilder(PREFIX + "§6Deine Lizenzen§7:\n");
            Map<Licenses, Boolean> licenses = Licenses.getLicenses(id);

            if (licenses.get(Licenses.PERSONALAUSWEIS)) {
                sb.append(PREFIX + "  §7- §9Personalausweis§8: §bVorhanden\n");
            } else {
                sb.append(PREFIX + "  §7- §9Personalausweis§8: §bNicht vorhanden\n");
            }

            if (licenses.get(Licenses.FUEHRERSCHEIN)) {
                sb.append(PREFIX + "  §7- §9Führerschein§8: §bVorhanden\n");
            } else {
                sb.append(PREFIX + "  §7- §9Führerschein§8: §bNicht vorhanden\n");
            }

            if (licenses.get(Licenses.WAFFENSCHEIN)) {
                sb.append(PREFIX + "  §7- §9Waffenschein§8: §bVorhanden\n");
            } else {
                sb.append(PREFIX + "  §7- §9Waffenschein§8: §bNicht vorhanden\n");
            }

            if (licenses.get(Licenses.ANGELSCHEIN)) {
                sb.append(PREFIX + "  §7- §9Angelschein§8: §bVorhanden\n");
            } else {
                sb.append(PREFIX + "  §7- §9Angelschein§8: §bNicht vorhanden\n");
            }

            if (licenses.get(Licenses.JADDLIZENZ)) {
                sb.append(PREFIX + "  §7- §9Jagdschein§8: §bVorhanden\n");
            } else {
                sb.append(PREFIX + "  §7- §9Jagdschein§8: §bNicht vorhanden\n");
            }

            p.sendMessage(sb.toString());
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/showlicenses [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        StringBuilder sb = new StringBuilder("§6" + Script.getName(p) + "s Lizenzen§7:\n");
        Map<Licenses, Boolean> licenses = Licenses.getLicenses(id);

        if (licenses.get(Licenses.PERSONALAUSWEIS)) {
            sb.append("  §7- §9Personalausweis§8: §bVorhanden\n");
        } else {
            sb.append("  §7- §9Personalausweis§8: §bNicht vorhanden\n");
        }

        if (licenses.get(Licenses.FUEHRERSCHEIN)) {
            sb.append("  §7- §9Führerschein§8: §bVorhanden\n");
        } else {
            sb.append("  §7- §9Führerschein§8: §bNicht vorhanden\n");
        }

        if (licenses.get(Licenses.WAFFENSCHEIN)) {
            sb.append("  §7- §9Waffenschein§8: §bVorhanden\n");
        } else {
            sb.append("  §7- §9Waffenschein§8: §bNicht vorhanden\n");
        }

        if (licenses.get(Licenses.ANGELSCHEIN)) {
            sb.append("  §7- §9Angelschein§8: §bVorhanden\n");
        } else {
            sb.append("  §7- §9Angelschein§8: §bNicht vorhanden\n");
        }

        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " deine Lizenzen gezeigt.");
        tg.sendMessage(sb.toString());
        return true;
    }
}
