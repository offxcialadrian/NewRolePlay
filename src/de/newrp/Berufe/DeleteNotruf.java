package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Player.Notruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteNotruf implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST) && !Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/deletenotruf [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        for(Beruf.Berufe beruf : Notruf.call2.get(p)) {
            beruf.sendMessage(Notruf.PREFIX + "Der Notruf von " + Script.getName(tg) + " wurde gelöscht.");
        }

        Notruf.call.remove(tg);
        Notruf.call2.remove(tg);
        Notruf.call3.remove(tg);
        tg.sendMessage(Notruf.PREFIX + "Dein Notruf wurde gelöscht.");
        Beruf.getBeruf(p).sendMessage(Notruf.PREFIX + "Der Notruf von " + Script.getName(tg) + " wurde von " + Beruf.getAbteilung(p).getName() + " " + Script.getName(p) + " gelöscht.");

        return false;
    }
}
