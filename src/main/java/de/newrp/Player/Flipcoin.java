package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Flipcoin implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;
        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/flipcoin");
            return true;
        }

        Script.sendLocalMessage(7, p, "§8[§cFlipcoin§8] §c" + Messages.ARROW + " §7" + Script.getName(p) + " hat " + (Script.getRandom(0, 1) == 0 ? "Kopf" : "Zahl") + " geworfen.");

        return false;
    }
}
