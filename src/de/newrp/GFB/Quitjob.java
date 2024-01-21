package de.newrp.GFB;

import de.newrp.API.Cache;
import de.newrp.API.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Quitjob implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/quitjob");
            return true;
        }

        if(!GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Job.");
            return true;
        }

        GFB gfb = GFB.CURRENT.get(p.getName());
        GFB.CURRENT.remove(p.getName());
        switch (gfb) {
            case LAGERARBEITER:
                Lagerarbeiter.SCORE.remove(p.getName());
                Lagerarbeiter.ON_JOB.remove(p.getName());
                Cache.loadInventory(p);
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Lagerarbeiter §7verlassen.");
                break;
        }

        return false;
    }
}
