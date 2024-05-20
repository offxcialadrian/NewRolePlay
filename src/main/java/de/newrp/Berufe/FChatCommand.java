package de.newrp.Berufe;

import com.comphenix.protocol.PacketType;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            StringBuilder arg = new StringBuilder();
            for (String str : args) arg.append(" ").append(str);
            Player player = (Player) sender;
            if (Beruf.hasBeruf(player)) {
                player.performCommand("b" + arg);
                return true;
            }
            if (Organisation.hasOrganisation(player)) {
                player.performCommand("o" + arg);
                return true;
            }
        }
        return true;
    }
}
