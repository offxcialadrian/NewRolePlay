package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RestoreZeitung implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.NEWS)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/restorezeitung");
            return true;
        }

        Zeitung.restoreZeitung();
        p.sendMessage(Messages.INFO + "Die Zeitung wurde wiederhergestellt.");

        return false;
    }
}
