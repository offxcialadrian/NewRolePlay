package de.newrp.Vehicle;

import de.newrp.API.Messages;
import de.newrp.Berufe.Beruf;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class StrafzettelCommand implements CommandExecutor {

    public static final String PREFIX = "§8[§eStrafzettel§8]§e " + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (Beruf.hasBeruf(player)) {
                if (Beruf.getBeruf(player) == Beruf.Berufe.POLICE) {
                    if (args.length >= 1) {
                        if (args.length >= 2) {
                            StringBuilder r = new StringBuilder();
                            int price = Integer.parseInt(args[0]);
                            args[0] = "";
                            for (String arg : args) {
                                r.append(" ").append(arg);
                            }
                            String reason = String.valueOf(r).replaceFirst("  ", "");
                            Strafzettel.reasons.put(player, reason);
                            Strafzettel.prices.put(player, price);
                        } else {
                            player.sendMessage(PREFIX + "Du musst einen Grund angeben!");
                        }
                        return true;
                    } else {
                        player.sendMessage(PREFIX + "Du musst einen Preis angeben!");
                        return true;
                    }
                }
            }
            player.sendMessage(Messages.ERROR + "Du bist kein Polizist!");
        }
        return true;
    }
}
