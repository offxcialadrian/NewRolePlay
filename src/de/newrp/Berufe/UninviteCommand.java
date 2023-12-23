package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UninviteCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§eUninvite§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/uninvite [Spieler]");
            return true;
        }

        if (!Beruf.isLeader(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (Beruf.getBeruf(p) != Beruf.getBeruf(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deinem Beruf.");
            return true;
        }

        Beruf.Berufe beruf = Beruf.getBeruf(p);

        p.sendMessage(PREFIX + "Du hast " + tg.getName() + " aus der " + Beruf.getBeruf(p).getName() + " entlassen.");

        if(tg.isOnline() && tg.getPlayer() != null) {
            tg.getPlayer().sendMessage(PREFIX + "Du wurdest aus der " + Beruf.getBeruf(p).getName() + " entlassen.");
        } else {
            Script.addOfflineMessage(tg, PREFIX + "Du wurdest aus der " + Beruf.getBeruf(p).getName() + " entlassen.");
        }

        beruf.removeMember(tg, p);
        Script.removeEXP(tg.getName(), Script.getRandom(50, 100));
        TeamSpeak.sync(Script.getNRPID(tg));


        return false;
    }
}


