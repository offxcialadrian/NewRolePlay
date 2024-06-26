package de.newrp.Government;

import de.newrp.API.Achievement;
import de.newrp.API.Messages;
import de.newrp.Berufe.Beruf;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegierungCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§6Regierung§8] §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        p.sendMessage(PREFIX + "Die nächsten Wahlen finden am §6" + Wahlen.getNextElection() + " §7statt.");
        p.sendMessage(PREFIX + "Die Regierung besteht derzeit aus folgenden Mitgliedern:");
        for(OfflinePlayer player : Beruf.Berufe.GOVERNMENT.getAllMembers()) {
            p.sendMessage("§8" + Messages.ARROW + " §6" + player.getName() + " §8(§6" + Beruf.getAbteilung(player).getName() + "§8)");
        }
        Achievement.WAHLEN.grant(p);
        return false;
    }
}
