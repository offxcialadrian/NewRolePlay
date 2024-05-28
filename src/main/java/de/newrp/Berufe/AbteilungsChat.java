package de.newrp.Berufe;

import de.newrp.API.FrakChatColor;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class AbteilungsChat implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player)) {
                player.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
                return true;
            }

            Beruf.Berufe beruf = Beruf.getBeruf(player);
            Abteilung.Abteilungen abteilung = Beruf.getAbteilung(player);
            if (abteilung.getID() == 0) {
                player.sendMessage(Messages.ERROR + "Du bist in keiner Abteilung.");
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(Messages.ERROR + "/abteilungschat [Nachricht]");
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (String arg : args) message.append(arg).append(" ");

            for (UUID p : Beruf.getBeruf(player).getMember()) {
                if (Bukkit.getPlayer(p) != null) {
                    Player target = Bukkit.getPlayer(p);
                    if (Beruf.getAbteilung(target) == abteilung) {
                        Objects.requireNonNull(target).sendMessage("§" + FrakChatColor.getNameColor(Beruf.getBeruf(player)) + "§o" + abteilung.getName() + " " + Script.getName(player) + "§8: §" + FrakChatColor.getTextColor(beruf) + message);
                    }
                }
            }
        }

        return true;
    }
}
