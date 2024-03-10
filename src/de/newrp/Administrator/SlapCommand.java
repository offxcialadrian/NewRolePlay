package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (!Script.hasRank(p, Rank.OWNER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }
        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/slap [Spieler]");
        } else {
            Player p1 = Script.getPlayer(args[0]);
            if (p1 == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
            } else {
                if (p == p1) {
                    p1.setVelocity(p1.getVelocity().setY(2.0D));
                    p.sendMessage("§d§lDie Sterne sind zum greifen nah,\n komm hol' mir einen runter!");
                    p1.sendTitle(Script.rainbowChatColor("ABFLUUUUG"), "§cHoch mit dir! :)");
                } else {
                    p1.setVelocity(p1.getVelocity().setY(2.0D));
                    p.sendMessage("§cDu hast " + p1.getName() + " geslapt!");
                    p1.sendTitle(Script.rainbowChatColor("ABFLUUUUG"), "§cHoch mit dir! :)");
                    p1.sendMessage("§d§lDie Sterne sind zum greifen nah,\n komm hol' mir einen runter!");
                }
            }
        }
        return true;
    }
}
