package de.newrp.Government;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.Police.Jail;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TakeWaffenschein implements CommandExecutor {

    public static String PREFIX = "§8[§9Waffenschein§8] §9» §7";

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
            p.sendMessage(Messages.ERROR + "Nur der Polizeipräsident oder das Justizministerium können Spieler den Waffenschein abnehmen.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/removewaffenschein [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Licenses.WAFFENSCHEIN.hasLicense(Script.getNRPID(tg))) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keinen Waffenschein.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        Licenses.WAFFENSCHEIN.remove(Script.getNRPID(tg));
        p.sendMessage(PREFIX + "Du hast §6" + Script.getName(tg) + " §7den Waffenschein abgenommen.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7den Waffenschein abgenommen.");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + Script.getName(tg) + " §7den Waffenschein abgenommen.");
        tg.sendMessage(PREFIX + Script.getName(p) + " hat dir den Waffenschein abgenommen.");
        Me.sendMessage(p, "nimmt " + Script.getName(tg) + " den Waffenschein ab.");

        return false;
    }
}
