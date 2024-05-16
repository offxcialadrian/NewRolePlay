package de.newrp.Vehicle;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
public class StrafzettelCommand implements CommandExecutor {

    public static final String PREFIX = "§8[§eStrafzettel§8]§e " + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            final Beruf.Berufe faction = Beruf.getBeruf(player);
            if(faction != Beruf.Berufe.POLICE) {
                player.sendMessage(Messages.ERROR + "Du bist kein Polizist!");
                return true;
            }

            if(args.length == 0) {
                player.sendMessage(PREFIX + "/strafzettel [Preis/remove] ([Grund])!");
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                Strafzettel.removes.add(player);
                player.sendMessage(Messages.INFO + "Rechtsklicke jetzt das Auto, um den Strafzettel zu entfernen");
                return true;
            }

            final String reason = String.join(" ",  Arrays.copyOfRange(args, 1, args.length));

            if(!Script.isInt(args[0])) {
                player.sendMessage(PREFIX + "§cDer Preis muss eine Zahl sein!");
                return true;
            }

            int price = Integer.parseInt(args[0]);

            if(price <= 0) {
                player.sendMessage(PREFIX + "§cDu kannst keinen Strafzettel mit einem so geringen Preis erstellen!");
                return true;
            }

            Strafzettel.reasons.put(player, reason);
            Strafzettel.prices.put(player, price);
            player.sendMessage(PREFIX + "Du hast ein Strafzettel von §e" + price + "€ §7mit dem Grund §e" + reason + " §7erstellt.");
            player.sendMessage(Messages.INFO + "Rechtsklicke jetzt das Auto, um den Strafzettel anzulegen");
        }
        return true;
    }
}
