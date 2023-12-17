package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewsCommand implements CommandExecutor {

    public static String NEWS = "§8[§6News§8] §6" + Messages.ARROW + " ";

    public static boolean wahlenNews = false;
    public static boolean wahlenNewsActive = false;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.NEWS) {
            p.sendMessage(Messages.ERROR + "Du Mitglieder der News können Nachrichten schalten.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/news [News]");
            return true;
        }

        if(Beruf.getAbteilung(p) != Abteilung.Abteilungen.NEWS_NACHRICHTEN) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der richtigen Abteilung um News zu schalten.");
            return true;
        }

        StringBuilder msg = new StringBuilder(NEWS);
        for(String arg : args) {
            msg.append(arg).append(" ");
        }

        if(wahlenNewsActive) wahlenNews = true;

        Bukkit.broadcastMessage(msg.toString());
        Script.sendTeamMessage(NEWS + "Diese News wurde geschaltet von " + Script.getName(p));
        Beruf.Berufe.NEWS.sendMessage(NEWS + "Diese News wurde geschaltet von " + Script.getName(p));

        return false;
    }
}
