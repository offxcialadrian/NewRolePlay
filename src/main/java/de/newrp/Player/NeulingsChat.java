package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NeulingsChat implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Script.isNRPTeam(p) && Script.getLevel(p) > 3) {
            p.sendMessage(Messages.ERROR + "Du kannst den NeulingsChat nur bis Level 3 nutzen.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/nc [Nachricht]");
            return true;
        }

        for(Player all : Bukkit.getOnlinePlayers()) {
            if(Script.isNRPTeam(all) || Script.getLevel(all) <= 3) {
                all.sendMessage("§8[§9NeulingsChat§8] §9» " + (Script.isNRPTeam(p)?"§f":"§7") + Script.getName(p) + "§8: §7" + String.join(" ", args));
            }
        }

        return false;
    }
}
