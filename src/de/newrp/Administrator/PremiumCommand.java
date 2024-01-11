package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PremiumCommand implements CommandExecutor {

    public static final String PREFIX = "§8[§bPremium§8] §b" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.OWNER, false)) {
            if(!Premium.hasPremium(p)) {
                p.sendMessage(Messages.ERROR + "Du hast keinen Premium Rang.");
                p.sendMessage(Messages.INFO + "Du kannst dir einen Premium Rang auf https://shop.newrp.de/ kaufen.");
                return true;
            } else {
                if(Script.hasRank(p, Rank.MODERATOR, false)) {
                    p.sendMessage(PREFIX + "Du hast automatisch so lang Premium, wie du " + Script.getRank(p).getName(p) + " bist.");
                    return true;
                }
                p.sendMessage(PREFIX + "Du hast noch bis zum " + Script.dateFormat.format(Premium.getPremiumTime(p)) + " Uhr.");
                return true;
            }
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/premium [Spieler] [Tage]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        int days;
        try {
            days = Integer.parseInt(args[1]);
        } catch (Exception e) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
            return true;
        }


        Premium.addPremium(tg, TimeUnit.DAYS.toMillis(days));
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " für " + days + " Tage Premium gegeben.");
        tg.sendMessage(PREFIX + "Du hast von " + Script.getName(p) + " für " + days + " Tage Premium bekommen.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " für " + days + " Tage Premium gegeben.", true);
        TeamSpeak.sync(Script.getNRPID(tg));

        return false;
    }
}
