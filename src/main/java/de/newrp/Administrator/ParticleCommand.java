package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ParticleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;

        if(!Script.hasRankExact(player, Rank.DEVELOPER, Rank.OWNER, Rank.ADMINISTRATOR)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/particle [Particle] [Count]");
            return false;
        }

        int count = 1;
        try {
            count = Integer.parseInt(args[1]);
        } catch (Exception e) {
            // Ignore
        }

        for (Particle value : Particle.values()) {
            if(value.toString().equalsIgnoreCase(args[0])) {
                player.spawnParticle(value, player.getLocation(), count);
                return true;
            }
        }

        player.sendMessage(Messages.ERROR + "Dieser Sound existiert nicht.");
        return false;
    }

}
