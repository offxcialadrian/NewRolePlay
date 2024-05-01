package de.newrp.Gangwar;

import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GangwarZonesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 1 && GangwarZones.getZoneByName(args[0]) != null) {
            p.sendMessage(GangwarCommand.PREFIX + "Die Zone " + GangwarZones.getZoneByName(args[0]).getName() + " gehört derzeit " + GangwarZones.getZoneByName(args[0]).getOwner().getName());
            return true;
        }

        p.sendMessage(GangwarCommand.PREFIX + "Die Zonen sind derzeit wie folgt verteilt:");
        for(GangwarZones zone : GangwarZones.values()) {
            p.sendMessage(GangwarCommand.PREFIX + "Die Zone " + zone.getName() + " gehört derzeit " + zone.getOwner().getName());
        }

        return false;
    }
}
