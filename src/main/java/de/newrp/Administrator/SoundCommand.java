package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;

        if(!Script.hasRankExact(player, Rank.DEVELOPER, Rank.OWNER, Rank.ADMINISTRATOR)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/sound [Sound] [Volume] [Pitch]");
            return false;
        }

        float volume = 1f;
        try {
            volume = Float.parseFloat(args[1]);
        } catch (Exception e) {
            // Ignore
        }

        float pitch = 1f;
        try {
            pitch = Float.parseFloat(args[2]);
        } catch (Exception e) {
            // Ignore
        }

        for (Sound value : Sound.values()) {
            if(value.name().equalsIgnoreCase(args[0])) {
                player.playSound(player.getLocation(), value, volume, pitch);
                player.sendMessage("Sound " + value + " abgespielt mit Volume " + volume + " und Pitch " + pitch + ".");
                return false;
            }
        }

        player.sendMessage(Messages.ERROR + "Dieser Sound existiert nicht.");
        return false;
    }
}
