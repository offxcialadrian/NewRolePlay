package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MemberCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§6Member§8] §6» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {

            if(Organisation.hasOrganisation(p)) {
                Organisation org = Organisation.getOrganisation(p);
                p.sendMessage(PREFIX + "Mitglieder von " + org.getName() + ":");
                for(OfflinePlayer player : org.getAllMembers()) {
                    p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Organisation.getRankName(player) + "§8)" + (player.isOnline()? " §8[§aOnline§8]" : ""));
                }
                return true;
            }

            if(!Beruf.hasBeruf(p)) {
                p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
                return true;
            }

            Beruf.Berufe beruf = Beruf.getBeruf(p);
            p.sendMessage(PREFIX + "Mitglieder von " + beruf.getName() + ":");
            for(OfflinePlayer player : beruf.getAllMembers()) {
                p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Beruf.getAbteilung(player).getName() + "§8)" + (player.isOnline()? " §8[§aOnline§8]" : ""));
            }

            return true;
        }

        if(args.length == 1) {
            Beruf.Berufe beruf = Beruf.Berufe.getBeruf(args[0]);
            if(beruf != null) {
                p.sendMessage(PREFIX + "Mitglieder der " + beruf.getName() + ":");
                for(OfflinePlayer player : beruf.getAllMembers()) {
                    p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName());
                }
                return true;
            }

            OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
            if(Script.getNRPID(tg) == 0) {
                p.sendMessage(Messages.ERROR + "Spieler oder Beruf nicht gefunden.");
                return true;
            }

            if(!Beruf.hasBeruf(tg)) {
                p.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen Beruf.");
                return true;
            }

            p.sendMessage(PREFIX + tg.getName() + " ist Mitglied von " + Beruf.getBeruf(tg).getName());
            return true;
        }

        p.sendMessage(Messages.ERROR + "/member [Beruf/Spieler]");

        return false;
    }
}
