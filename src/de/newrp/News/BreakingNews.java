package de.newrp.News;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Player.Mobile;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BreakingNews implements CommandExecutor {

        public static String NEWS = "§8[§6Breaking News§8] §6" + Messages.ARROW + " ";
        public static String BREAKING_NEWS = null;
        public static ArrayList<String> waitingForMessage = new ArrayList<>();
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (Beruf.getBeruf(p) != Beruf.Berufe.NEWS) {
            p.sendMessage(Messages.ERROR + "Nur Mitglieder der News können Nachrichten schalten.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/news [News]");
            return true;
        }

        if (Beruf.getAbteilung(p) != Abteilung.Abteilungen.CHEFREDAKTION && Beruf.getAbteilung(p) != Abteilung.Abteilungen.JOURNALIST) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der richtigen Abteilung um News zu schalten.");
            return true;
        }

        StringBuilder msg = new StringBuilder(NEWS);
        for (String arg : args) {
            msg.append(arg).append(" ");
        }

        BREAKING_NEWS = msg.toString();
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(Mobile.hasPhone(p) && Mobile.hasConnection(p)) {
                all.sendMessage(NEWS + "NachrichtenApp: Es gibt eine Breaking News!");
                if(!Mobile.getPhone(p).getLautlos(p)) {
                    all.playSound(all.getLocation(), Sound.ENTITY_SHEEP_AMBIENT, 1, 1);
                }
            }
        }
        Log.HIGH.write(p, "hat eine Breaking News geschaltet: " + msg.toString());
        return true;
    }
}
