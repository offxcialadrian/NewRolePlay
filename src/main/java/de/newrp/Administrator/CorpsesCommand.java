package de.newrp.Administrator;

import de.newrp.API.*;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class CorpsesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Team.Teams team = Team.getTeam(player);

        if(!Script.hasRank(player, Rank.ADMINISTRATOR, false) && team != Team.Teams.ENTWICKLUNG) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        /*if(args.length == 0) {
            for (Map.Entry<UUID, EntityPlayer> uuidEntityPlayerEntry : Corpse.npcMap.entrySet()) {
                uuidEntityPlayerEntry.getValue().remo
            }
            return false;
        }*/

        return false;
    }
}
