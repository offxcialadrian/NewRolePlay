package de.newrp.Government;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TakeDriversLicense implements CommandExecutor {

    public static String PREFIX = "§8[§9Führerschein§8] §9» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;
        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getAbteilung(p, true).equals(Abteilung.Abteilungen.JUSTIZMINISTERIUM)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.isLeader(p, true) && !Beruf.getAbteilung(p, true).equals(Abteilung.Abteilungen.JUSTIZMINISTERIUM)) {
            p.sendMessage(Messages.ERROR + "Nur der Polizeipräsident oder das Justizministerium können Spieler den Führerschein abnehmen.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/takeführerschein [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Licenses.FUEHRERSCHEIN.hasLicense(Script.getNRPID(tg))) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keinen Führerschein.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        Licenses.FUEHRERSCHEIN.remove(Script.getNRPID(tg));
        p.sendMessage(PREFIX + "Du hast §6" + Script.getName(tg) + " §7den Führerschein abgenommen.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7den Führerschein abgenommen.");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7den Führerschein abgenommen.");
        tg.sendMessage(PREFIX + Script.getName(p) + " hat dir den Führerschein abgenommen.");
        Me.sendMessage(p, "nimmt " + Script.getName(tg) + " den Führerschein ab.");

        return false;
    }
}
