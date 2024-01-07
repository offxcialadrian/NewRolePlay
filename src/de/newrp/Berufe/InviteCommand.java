package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Player.Annehmen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§eInvite§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/invite [Spieler]");
            return true;
        }

        if (!Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst einladen.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        if (Beruf.hasBeruf(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat bereits einen Beruf.");
            return true;
        }

        Annehmen.offer.put(tg.getName() + ".joinberuf", p.getName());
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " in die " + Beruf.getBeruf(p).getName() + " eingeladen.");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " in die " + Beruf.getBeruf(p).getName() + " eingeladen.");
        Script.sendAcceptMessage(tg);


        return false;
    }
}
