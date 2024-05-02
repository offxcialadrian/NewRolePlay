package de.newrp.Chat;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Punish;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaatsChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST) && !Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/staatschat [Nachricht]");
            return true;
        }

        if (Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemutet!");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        String msg = sb.toString().trim();

        for(Player all : Beruf.getPlayersFromBeruf(Beruf.Berufe.POLICE)) {
            all.sendMessage("§c" + Beruf.getBeruf(p).getName() + " " + Script.getName(p) + "§8: §c" + msg);
        }

        for(Player all : Beruf.getPlayersFromBeruf(Beruf.Berufe.RETTUNGSDIENST)) {
            all.sendMessage("§c" + Beruf.getBeruf(p).getName() + " " + Script.getName(p) + "§8: §c" + msg);
        }

        for(Player all : Beruf.getPlayersFromBeruf(Beruf.Berufe.GOVERNMENT)) {
            all.sendMessage("§c" + Beruf.getBeruf(p).getName() + " " + Script.getName(p) + "§8: §c" + msg);
        }

        return false;
    }
}
