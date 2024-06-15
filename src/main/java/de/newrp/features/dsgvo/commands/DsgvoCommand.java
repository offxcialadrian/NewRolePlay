package de.newrp.features.dsgvo.commands;

import de.newrp.API.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DsgvoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("§8[§5NewRP§8] §5" + Messages.ARROW + " §7Der Link zu unserer Datenschutzerklärung: https://forum.newrp.de/index.php?datenschutzerklaerung/");
        return false;
    }
}
